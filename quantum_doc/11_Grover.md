# `Grover.java` -- búsqueda cuántica, ventaja cuadrática

## Qué es

El algoritmo de búsqueda de Grover: encuentra un único elemento marcado
(`target`) entre `N=2^n` elementos no ordenados con `O(sqrt(N))` consultas
al oráculo, frente a las `O(N)` que necesitaría en el peor caso una
búsqueda clásica. A diferencia de `DeutschJozsa`/`BernsteinVazirani`
(ventaja EXPONENCIAL pero sobre un problema artificial con una promesa muy
específica), Grover resuelve un problema genérico y útil (búsqueda no
estructurada) con una ventaja CUADRÁTICA -- más modesta, pero mucho más
ampliamente aplicable.

## Cómo funciona (intuición): amplificación de amplitud

1. Prepara la superposición uniforme sobre las `N` entradas,
   `|s> = H⊗n|0...0>` -- cada estado con amplitud `1/sqrt(N)` (probabilidad
   `1/N`, la línea base clásica).
2. Repite, `k` veces, 2 operadores:
   - **Oráculo**: invierte el SIGNO de la amplitud de `|target>`, deja el
     resto igual.
   - **Difusión** ("inversión respecto a la media"): refleja cada amplitud
     respecto al promedio de todas.
3. Cada repetición rota el vector de estado un ángulo fijo `2·theta`
   (`theta=asin(1/sqrt(N))`) dentro del plano 2D que forman `|target>` y la
   superposición uniforme del resto -- la probabilidad de `target` CRECE en
   cada iteración, desde `1/N` hasta cerca de `1`.
4. Pasado el punto óptimo, la probabilidad empieza a DECRECER de nuevo
   ("sobre-rotación") -- por eso hay un número ÓPTIMO de iteraciones, no
   "cuantas más, mejor".

## Métodos

### `oracle(int target, int n)`
La matriz diagonal `2^n x 2^n` que invierte el signo solo en la posición
`target` -- `|x> -> -|x>` si `x=target`, `|x> -> |x>` en cualquier otro
caso.

### `diffusion(int n)`
El operador `D = 2|s><s| - I` (inversión respecto a la media), construido
vía la identidad `D = H⊗n · (2|0..0><0..0| - I) · H⊗n` -- reusa
`DeutschJozsa.hadamardChain()`.

### `initialState(int n)`
La superposición uniforme inicial `|s> = H⊗n|0..0>`.

### `optimalIterations(int n)`
El número óptimo de iteraciones, `floor(pi/4 · sqrt(N))` -- pasado este
punto, la probabilidad del objetivo empieza a decrecer de nuevo.

```java
int k = Grover.optimalIterations(6); // N=64 -> k=6
```

### `run(int target, int n, int iterations)`
Ejecuta `iterations` rondas de difusión-oráculo desde el estado inicial, y
devuelve el estado final (antes de medir).

### `probabilityOfTarget(MatrixComplex state, int target)`
La probabilidad (regla de Born) de medir `target` en un estado dado --
`|<target|state>|^2`.

### `search(int target, int n)`
El "resultado de cabecera" del algoritmo: `run()` con
`optimalIterations(n)`, seguido de `probabilityOfTarget()`. Debería salir
cercano a `1` (mucho mejor que la línea base clásica `1/N`).

```java
double p = Grover.search(5, 6); // N=64, buscando el elemento 5
// p es bastante cercano a 1.0 -- muy por encima de la base clasica 1/64 ~= 0.0156
```

## 2 hallazgos genuinos de la verificación (ninguno un bug en el algoritmo)

1. Un margen de seguridad ingenuo como "la probabilidad debe ser al menos
   10 veces la base clásica `1/N`" es matemáticamente inválido para `N`
   pequeña -- `10/N` puede superar `1`, un techo imposible de alcanzar,
   dando falsos negativos. La lección: al comprobar resultados
   numéricos, compara contra el valor exacto o una fórmula cerrada, no
   contra un margen inventado a ojo.
2. `N=2` (`n=1`) es una degeneración matemática genuina: el ángulo de
   rotación sale exactamente `theta=45°`, y cualquier múltiplo impar de
   `45°` da `sin²=0.5` exacto -- Grover NO aporta ninguna ventaja para
   `N=2`, sea cual sea el número de iteraciones. No es un fallo de la
   implementación, es una propiedad matemática real del caso más pequeño
   posible.

## Nota de rendimiento

`run()`/`search()` con `n` grande son notablemente lentos en este entorno:
`MatrixComplex` no está optimizado para álgebra densa, y cada iteración
multiplica matrices `2^n x 2^n` completas. `n=8` (dimensión 256) ya puede
tardar más de 2 minutos para un barrido amplio. Si experimentas con `n`
grande, empieza con pocas repeticiones y ve subiendo poco a poco.

## Experimento guiado: ver crecer y decrecer la probabilidad

```java
import com.ipserc.arith.quantum.Grover;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ProbandoGrover {
    public static void main(String[] args) {
        int n = 5;      // N = 32
        int target = 7;

        for (int k = 0; k <= 12; ++k) {
            MatrixComplex state = Grover.run(target, n, k);
            double p = Grover.probabilityOfTarget(state, target);
            System.out.printf("iteraciones=%2d  P(target)=%.4f%n", k, p);
        }
        // La probabilidad crece desde 1/32=0.03125 (k=0) hasta un maximo
        // cerca de k=optimalIterations(5)=4, y luego EMPIEZA A BAJAR de nuevo
        System.out.println("optimo teorico: " + Grover.optimalIterations(n));
    }
}
```

## Relación con el resto del paquete

Usa `Qubits` y `DeutschJozsa.hadamardChain()` (reusado, no reconstruido).
`NoisyGrover` (`15_NoisyGrover.md`) combina esta clase con `Decoherence`,
inyectando ruido una sola vez al preparar el estado, antes de la primera
iteración.
