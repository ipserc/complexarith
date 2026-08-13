# Guía de uso — capa de plotting (SimpleGnuplot / MatrixComplexPlot / PolynomPlot / Polynom)

> Escrita tras la sesión del 13 de agosto de 2026 ("hacer la clase plot más potente", commits `236cd7b` y `d1199e6`). Cubre las novedades: `PlotStyle`, `CanvasOptions`, exportar a fichero, estilo por serie, `Polynom.plotSeriesSync/Async(List<NamedSeries>,...)` — y, porque salió en la propia sesión, una aclaración a fondo de `Sync` vs `Async` (`SimpleGnuplot.e_syncMode`), que ya existía pero no estaba explicado en ningún sitio. Todo lo que ya sabías usar (`plotSync`/`plotAsync`, `NamedSeries`, `e_lineStyle`...) sigue funcionando exactamente igual — esto es todo capacidad **añadida**, nada roto.

## 1. Qué cambió y por qué

Antes, cada método de `MatrixComplexPlot`/`PolynomPlot` repetía a mano este bloque:

```java
p.setPersist(true);
p.getPostInit().add("set terminal windows");
```

Eso era también el único sitio donde se podía tocar cosas como el rango del eje Y, el color de una serie o exportar a fichero — y no había manera de llegar hasta ahí desde fuera. Ahora:

- Ese bloque **ya no hace falta escribirlo** — es el comportamiento por defecto de `SimpleGnuplot` cuando no le dices lo contrario.
- Hay **2 objetos nuevos** para pedir lo que antes no se podía pedir:
  - **`PlotStyle`** — el aspecto de UNA serie/curva (color, grosor, tipo de línea, tipo de punto).
  - **`CanvasOptions`** — configuración del lienzo entero (rango de ejes, posición de la leyenda, `pm3d`, anotaciones, exportar a fichero...).

## 2. `Sync` vs `Async` — cuándo bloquea y cuándo no

Todo método de plot tiene 2 versiones — `plotSync`/`plotAsync`, o un parámetro `SimpleGnuplot.e_syncMode` que vale `SYNC`/`ASYNC` — desde antes de esta sesión. No es una duplicación superficial: cambian una única línea, pero esa línea es real:

```java
// SimpleGnuplot.launch(boolean wait)
Process proc = pb.start();
...
if (wait) proc.waitFor();   // <- la única diferencia entre Sync y Async
```

- **`Sync`** (`wait=true`) — el hilo Java se **bloquea** hasta que el proceso `gnuplot` termina. Con ventana persistente (el caso normal), eso significa: bloqueado hasta que **tú cierras la ventana a mano**.
- **`Async`** (`wait=false`) — el método vuelve **inmediatamente** después de mandarle el script a gnuplot por stdin; la ventana queda abierta por su cuenta, sin congelar el resto del programa.

### Por qué existe esto

La librería que usaba antes el proyecto (`com.panayotis.gnuplot.JavaPlot`) llamaba a `Process.waitFor()` **incondicionalmente** — con ventana persistente en Windows, el hilo se quedaba bloqueado sí o sí hasta que alguien cerraba la ventana. Eso hacía inviable, por ejemplo, correr una batería de tests que abre varias gráficas seguidas sin tener que cerrar cada ventana a mano para que el siguiente test arrancara. `SimpleGnuplot` se escribió específicamente para poder elegir.

### Ejemplos reales del propio proyecto

**Varias gráficas relacionadas, una detrás de otra, sin esperar** — `TestFilter01.java`:
```java
filter.plotDFTAsync("Filter " + filterparams, ...);
signalFiltered.plotSamplesAsync("Filtered signal " + filterparams, ...);
signalFiltered.plotDFTAsync("Filtered signal " + filterparams, ...);
```
Si fueran `Sync`, el programa se quedaría bloqueado en la primera línea hasta cerrar esa ventana a mano — y solo entonces se abriría la segunda, y así sucesivamente. Con `Async` las 3 ventanas aparecen seguidas, sin esperar a que cierres ninguna.

**Comparar 2 superficies lado a lado** — `TestSurfaceSinc02.java`:
```java
MatrixComplexPlot.plotGrid3DAsync("Re(sinc(z))", surfaceRe);
MatrixComplexPlot.plotGrid3DAsync("Im(sinc(z))", surfaceIm);
```
Con `Sync` verías primero Re, tendrías que cerrarla, y solo entonces aparecería Im — perdiendo la comparación visual lado a lado que es justo el objetivo del test.

### Cuál usar

| Situación | Usa |
|---|---|
| El siguiente paso del programa depende de que ya hayas terminado de mirar esa gráfica | `Sync` |
| Quieres varias gráficas abiertas a la vez para comparar | `Async` |
| Exportas a fichero (`CanvasOptions.withOutputFile`) y quieres estar seguro de que ya se ha escrito antes de seguir | `Sync` (no hay ventana que cerrar — bloquea solo hasta que gnuplot termina de escribir) |
| Un test/script que abre varias gráficas y no debe congelarse | `Async` |

## 3. `PlotStyle` — el aspecto de una serie

```java
import com.ipserc.arith.plot.PlotStyle;

PlotStyle rojoGrueso = new PlotStyle(
    "red",          // color (spec de gnuplot: "red", "#1f77b4", ...) o null = automático
    3,               // linewidth, o null = por defecto
    2,               // dashtype, o null = por defecto
    7,               // pointtype, o null = por defecto
    "linespoints"    // el "with <estilo>" de gnuplot, o null -> usa "lines"
);
```

Todos los campos son opcionales (`null` = "no tocar ese aspecto"). Si solo quieres el color:

```java
PlotStyle soloColor = new PlotStyle("blue", null, null, null, null);
```

### Dónde se usa

- **Directamente en `SimpleGnuplot`** (el motor):
  ```java
  SimpleGnuplot p = new SimpleGnuplot();
  p.addPlot(datos, "Mi serie", rojoGrueso);       // 2D
  p.addPlotGrid(grid, "Superficie", rojoGrueso);   // 3D en malla
  p.addPlot("sin(x)", rojoGrueso);                 // expresión nativa gnuplot
  ```

- **En `MatrixComplexPlot`, vía `NamedSeries`/`NamedGrid`** (el tercer argumento es nuevo):
  ```java
  import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
  import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.NamedSeries;

  NamedSeries curva = new NamedSeries("Amplitud", datos, rojoGrueso);
  MatrixComplexPlot.plotSeriesSync("Mi gráfica", MatrixComplexPlot.e_lineStyle.LINES, curva);
  ```

- **En `Polynom`, vía el mismo `NamedSeries`** — ver sección 4, es nuevo.

## 4. Listas de series con estilo en `Polynom` — `plotSeriesSync`/`plotSeriesAsync(List<NamedSeries>,...)`

Hasta aquí, `Polynom` solo tenía `plotSync(List<double[][]> pointsList, List<String> labels, String title)`: cada curva podía llevar su propio nombre, pero **no** su propio estilo — las 2 listas iban en paralelo (posición `i` de `pointsList` con posición `i` de `labels`) y no había un tercer hueco para el color.

`plotSeriesSync`/`plotSeriesAsync` cierran ese hueco reutilizando el **mismo** `NamedSeries` que ya usa `MatrixComplexPlot` — no se ha inventado un tipo nuevo para `Polynom`. Cada elemento de la lista lleva sus datos, su etiqueta y su `PlotStyle` (opcional) juntos en un solo objeto:

```java
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.NamedSeries;
import com.ipserc.arith.plot.PlotStyle;

List<NamedSeries> curvas = new ArrayList<>();
curvas.add(new NamedSeries("Curva Roja", puntosA, new PlotStyle("red", 2, null, null, null)));
curvas.add(new NamedSeries("Curva Simple", puntosB));   // sin estilo -> gnuplot decide

Polynom p = new Polynom(1);
p.plotSeriesSync(curvas, "mi título");

// con CanvasOptions, igual que el resto de la familia:
p.plotSeriesSync(curvas, "mi título", new CanvasOptions().withSetting("yrange", "[-3:3]"));

// y su gemelo asíncrono, como siempre:
p.plotSeriesAsync(curvas, "mi título");
```

### ¿Por qué un nombre distinto (`plotSeriesSync`) y no un overload más de `plotSync`?

Porque **no se puede**. En Java, dos métodos de la misma clase que solo se diferencian en el tipo genérico del parámetro (`List<double[][]>` frente a `List<NamedSeries>`) colisionan: tras el *type erasure*, ambos quedan como `plotSync(List, String)` — el mismo método, dos veces. El compilador lo rechaza con `name clash: ... have the same erasure`. Por eso `MatrixComplexPlot` ya tenía este mismo patrón (`plot` para el caso simple, `plotSeries` para el caso con `NamedSeries`) y `Polynom` lo hereda igual: `plotSync`/`plotAsync` (con `List<double[][]>`+`labels`) siguen para el caso de siempre, `plotSeriesSync`/`plotSeriesAsync` (con `List<NamedSeries>`) son la vía nueva con estilo.

### Alcance de este cambio

Solo llega a los métodos que reciben la lista de puntos **directamente** (`plot`/`plotSeries` en `PolynomPlot`, que es lo que hay detrás de `Polynom.plotSync`/`plotSeriesSync`). Las familias `plotRe`/`plotIm`/`plotMod`/`plotPha(List<MatrixComplex>, ...)` — que primero *extraen* la parte real/imaginaria/módulo/fase de cada `MatrixComplex` y luego pintan — **no** ganaron esta vía todavía: ahí seguirías con la lista de `labels` en paralelo, sin estilo por curva. Si algún día hace falta, es el mismo patrón, aplicado un nivel más adentro.

## 5. `CanvasOptions` — configuración del lienzo

Es una bolsa de "cualquier cosa que gnuplot entienda", con 4 operaciones encadenables (`with...`, devuelven `this`):

```java
import com.ipserc.arith.plot.CanvasOptions;

CanvasOptions opciones = new CanvasOptions()
    .withSetting("yrange", "[-2:2]")              // -> "set yrange [-2:2]"
    .withSetting("key", "top left")                // -> "set key top left"
    .withPostInit("set label 'origen' at 0,0")      // comando gnuplot crudo, tal cual
    .withOutputFile("C:\\tmp\\mi_grafica.png")      // exportar a PNG (ver sección 6)
    .withTerminal("svg size 800,600");              // o un terminal distinto (ver sección 6)
```

`withSetting(clave, valor)` es literalmente `set <clave> <valor>` — cualquier directiva de gnuplot que aceptes con `set` funciona aquí sin que haga falta un método nuevo por cada una: `yrange`, `zrange`, `key`, `pm3d`, `palette`, `size ratio`, lo que sea.

`withPostInit(comando)` es para comandos que no son `set` (`set label ...`, `set arrow ...`) o cuando quieres controlar el texto exacto.

### Dónde se usa

Cada método "genérico" de `MatrixComplexPlot`/`PolynomPlot`/`Polynom` (los que ya recibían `SimpleGnuplot.e_syncMode mode`) ganó un overload con `CanvasOptions` como parámetro extra — la firma antigua sigue existiendo, sin cambios. `opciones` es un único parámetro para todo el lienzo; el estilo de cada curva **no** aparece aquí como parámetro — va pegado a su propio `NamedSeries` (secciones 3 y 4), porque cada serie del mismo plot puede querer un estilo distinto:

```java
// el estilo va PEGADO a cada serie (NamedSeries), no como parámetro aparte
NamedSeries curva1 = new NamedSeries("Alpha", datosA, new PlotStyle("red", null, null, null, null));
NamedSeries curva2 = new NamedSeries("Beta", datosB); // sin estilo -> gnuplot decide

// CanvasOptions, en cambio, es UN único parámetro para todo el lienzo
MatrixComplexPlot.plotSeries("título", null, null, false, MatrixComplexPlot.e_lineStyle.LINES,
    SimpleGnuplot.e_syncMode.SYNC, opciones, curva1, curva2);

MatrixComplexPlot.plotSeries3D("título", false, MatrixComplexPlot.e_lineStyle3D.LINES,
    SimpleGnuplot.e_syncMode.SYNC, opciones, curva3D);

MatrixComplexPlot.plotGrid3D("título", false, SimpleGnuplot.e_syncMode.SYNC, opciones, grid3D);

// Polynom — mismo patrón en TODOS los pares plotXxxSync/plotXxxAsync, incluido plotSeriesSync/Async
polinomio.plotSync(puntos, "título", opciones);
polinomio.plotSeriesSync(curvas, "título", opciones);
polinomio.plotExpressionSync(loLimit, upLimit, opciones);
polinomio.plotReSync(matrizCompleja, "título", opciones);
// ... (plotIm/plotMod/plotPha, las versiones con List<...>, las plotExpressionXxx, etc. — todas tienen su gemela con CanvasOptions)
```

## 6. Exportar a fichero (en vez de ventana interactiva)

```java
CanvasOptions exportar = new CanvasOptions().withOutputFile("C:\\tmp\\salida.png");
MatrixComplexPlot.plotSeriesSync("título", MatrixComplexPlot.e_lineStyle.LINES, exportar, curva1);
```

Esto hace 3 cosas automáticamente:
- `set terminal pngcairo size 1024,768`
- `set output 'C:\tmp\salida.png'`
- **No** abre ventana ni usa `-persist` (no tiene sentido con salida a fichero).

Si quieres otro formato (SVG, PDF, tamaño distinto), usa el escape hatch genérico en vez de `withOutputFile`:

```java
CanvasOptions svg = new CanvasOptions()
    .withTerminal("svg size 800,600")
    .withSetting("output", "'C:\\tmp\\salida.svg'");   // el output hay que ponerlo tú con set()
```

(`withOutputFile` es solo el atajo para el caso común PNG; para todo lo demás, `withTerminal` + `withSetting("output", ...)`.)

## 7. Recetas rápidas

**Cambiar el rango del eje Y:**
```java
new CanvasOptions().withSetting("yrange", "[-10:10]")
```

**Mover la leyenda:**
```java
new CanvasOptions().withSetting("key", "top left box")
```

**Superficie 3D con mapa de color (`pm3d`):**
```java
new CanvasOptions()
    .withSetting("pm3d", "")
    .withSetting("palette", "rgbformulae 33,13,10")
```
(usar con `plotGrid3D`/`plotSeries3D`, que ya hacen `splot`)

**Poner una anotación de texto:**
```java
new CanvasOptions().withPostInit("set label 'pico' at 3.14,1.0 point")
```

**Curva roja discontinua, gruesa:**
```java
new PlotStyle("red", 3, 2, null, null)
```

**Varias curvas de un polinomio, cada una con su color:**
```java
List<NamedSeries> curvas = List.of(
    new NamedSeries("Original", puntosA, new PlotStyle("red", null, null, null, null)),
    new NamedSeries("Aproximación", puntosB, new PlotStyle("blue", null, 2, null, null))
);
polinomio.plotSeriesSync(curvas, "comparación");
```

## 8. Lo que NO cambió

- Todos los métodos `xxxSync`/`xxxAsync` de siempre siguen ahí, con la misma firma, mismo comportamiento por defecto (ventana interactiva persistente).
- No hace falta tocar nada de código existente para que siga funcionando — `PlotStyle`/`CanvasOptions`/`plotSeriesSync` son 100% opt-in, nada se ha quitado ni renombrado.
- `Fourier`/`Laplace`/`Z` no se tocaron: si algún día quieres `CanvasOptions` ahí, se puede llamar directamente a `MatrixComplexPlot.plotSeries(...)` (ya es público), como hacen ya algunos tests.

## 9. Fuera de alcance (candidato para otro día)

- **Multiplot/subplots** (varias gráficas en un mismo canvas) — decisión consciente de dejarlo fuera de esta pasada porque obligaría a separar "construir el script" de "lanzar el proceso" dentro de `SimpleGnuplot`, un cambio de arquitectura mayor e independiente de todo lo de arriba.
- **`PlotStyle` en `plotRe`/`plotIm`/`plotMod`/`plotPha(List<MatrixComplex>, ...)`** — ver nota de alcance en la sección 4.

## 10. Ejemplo completo: `plotFunc01FullOptions02.java`

Todo lo de arriba junto, en un fichero real del repo (`src/TestComplex/plotFunc01FullOptions02.java`) — vale la pena leerlo entero porque encadena las 6 piezas de esta guía sobre una única señal.

### El punto de partida: generalizar `plotFunc01.signalSin`

`plotFunc01.signalSin(Complex,double,double)` calcula `amplitud * exp(i*2*pi*freq*t)` — la función `exp` viene fija. La idea de este fichero es sacar esa función como parámetro:

```java
private static CalcResultFunc calcFunc(String nombreFuncion, Function<Complex, Complex> complexFunc) {
    double freq = 60;
    long samples = (long) (freq * 4);

    MatrixComplex data = new MatrixComplex((int) samples + 1, 1);
    Complex amplitud = new Complex("2");
    double twoPI = 2 * Math.PI;
    for (int t = 0; t <= samples; ++t) {
        Complex angulo = new Complex(twoPI / freq * t);   // angulo REAL, sin el factor "i"
        data.complexMatrix[t][0] = amplitud.times(complexFunc.apply(angulo));
    }
    // ... separa data en dataRe/dataIm (double[][]) ...
    return new CalcResultFunc(nombreFuncion, amplitud, dataRe, dataIm);
}
```

**El truco**: las funciones reales de `sin`/`cos`/`tan`/`exp`/... viven en `com.ipserc.arith.complex.ComplexFunctions`, una clase *package-private* — no se puede referenciar desde `TestComplex`. Pero cada una tiene un delegador público en `Complex.java` con la misma firma (`Complex.sin(Complex)`, `Complex.cos(Complex)`, `Complex.exp(Complex)`...), que encaja exactamente en `Function<Complex,Complex>` vía referencia a método: `Complex::sin`, `Complex::cos`, `Complex::exp`. Eso es lo que se pasa como `complexFunc`.

**Un matiz importante, no obvio a la primera**: como el ángulo aquí es REAL (no `i*ángulo` como en `signalSin`), `calcFunc("exp", Complex::exp)` **no** reproduce la señal original — con ángulo real, `exp` crece sin oscilar. En cambio `sin`/`cos`/`tan` de un ángulo real sí oscilan igual que sus equivalentes reales de toda la vida (parte imaginaria nula), que es justo lo que se compara más abajo.

### La demo completa, para una sola función

```java
private static void doPlot(CalcResultFunc resultado) {
    // 1) PlotStyle: cada curva con su propio color/grosor/tipo de línea
    NamedSeries curvaRe = new NamedSeries("Real", resultado.dataRe(),
        new PlotStyle("red", 2, null, null, "linespoints"));
    NamedSeries curvaIm = new NamedSeries("Imaginaria", resultado.dataIm(),
        new PlotStyle("blue", 2, 2, null, "lines"));

    // 2) CanvasOptions: rango del eje Y, leyenda arriba a la derecha, anotación de texto
    CanvasOptions opciones = new CanvasOptions()
        .withSetting("yrange", stYrange)
        .withSetting("key", "top right box")
        .withPostInit("set label 'amplitud = " + resultado.amplitud().rep() + "' at graph 0.02,0.95");

    // 3) Sync vs Async: la misma gráfica, dos veces, para ver la diferencia en los println
    MatrixComplexPlot.plotSeries(titulo + " (Sync)", null, null, false,
        e_lineStyle.LINES, SimpleGnuplot.e_syncMode.SYNC, opciones, curvaRe, curvaIm);
    MatrixComplexPlot.plotSeries(titulo + " (Async)", null, null, false,
        e_lineStyle.LINES, SimpleGnuplot.e_syncMode.ASYNC, opciones, curvaRe, curvaIm);

    // 4) Exportar a fichero PNG, sin abrir ninguna ventana
    CanvasOptions exportar = new CanvasOptions().withSetting("yrange", stYrange).withOutputFile(rutaExport);
    MatrixComplexPlot.plotSeries(titulo + " (export)", null, null, false,
        e_lineStyle.LINES, SimpleGnuplot.e_syncMode.ASYNC, exportar, curvaRe, curvaIm);
}
```

En el fichero real, cada uno de estos 4 pasos va rodeado de `System.out.println(...)` — es la forma más directa de *ver* la diferencia Sync/Async al ejecutarlo en Eclipse: con `Sync` el mensaje de "después" no sale hasta que cierras la ventana; con `Async` sale enseguida (sección 2).

Nota sobre el paso 4: usa `Async`, no `Sync` — a propósito, para que se note que el `println("Grafica exportada a ...")` puede salir *antes* de que gnuplot termine de escribir el fichero. Si esa garantía importa, la sección 2 ya lo dice: usar `Sync` para exportar.

### Comparar varias funciones a la vez

```java
private static void doPlotComparacion(CalcResultFunc... resultados) {
    NamedSeries[] curvas = new NamedSeries[resultados.length];
    String[] colores = { "red", "blue", "dark-green", "orange", "purple" };
    for (int i = 0; i < resultados.length; ++i) {
        curvas[i] = new NamedSeries("Re(" + resultados[i].nombreFuncion() + ")", resultados[i].dataRe(),
            new PlotStyle(colores[i % colores.length], 2, null, null, "lines"));
    }
    CanvasOptions opciones = new CanvasOptions().withSetting("yrange", stYrange).withSetting("key", "top right box");
    MatrixComplexPlot.plotSeries("comparación", null, null, false,
        e_lineStyle.LINES, SimpleGnuplot.e_syncMode.ASYNC, opciones, curvas);
}
```

Esta es la pieza que junta **calcular con `ComplexFunctions` vía parámetro** (sección 4 de esta guía, adaptada aquí a `MatrixComplexPlot` en vez de `Polynom`) con **una lista de curvas, cada una con su propio color**, en un solo canvas — la misma idea de "lista de `NamedSeries` con estilo" que `Polynom.plotSeriesSync` (sección 4), aquí construida a mano como array en vez de `List`.

### Todo junto en `main()`

```java
public static void main(String[] args) {
    CalcResultFunc resultadoTan = calcFunc("tan", Complex::tan);
    doPlot(resultadoTan);                                   // demo completa para "tan"

    CalcResultFunc resultadoSin = calcFunc("sin", Complex::sin);
    CalcResultFunc resultadoCos = calcFunc("cos", Complex::cos);
    CalcResultFunc resultadoLn  = calcFunc("ln", Complex::log);

    doPlotComparacion(resultadoSin, resultadoCos, resultadoTan, resultadoLn); // las 4 juntas
}
```

Ninguna de las 4 funciones (`tan`/`sin`/`cos`/`log`) necesitó su propio método `signalXxx` en `plotFunc01.java` — es justo el punto de pasar la función como parámetro.
