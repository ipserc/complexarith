# `BlochSphere.java` -- visualización 3D de un qubit

## Qué es

Una forma geométrica de VER el estado de un único qubit: cualquier matriz
densidad `2x2` corresponde exactamente a un punto `(x,y,z)` dentro de (o
sobre) una bola de radio 1 -- la **esfera de Bloch**. Esta clase convierte
en ambas direcciones (matriz densidad ↔ punto 3D) y dibuja trayectorias de
puntos junto con la esfera de referencia.

## Para qué sirve / contexto físico

- Los **estados puros** (norma exactamente 1, `x²+y²+z²=1`) están SIEMPRE
  sobre la SUPERFICIE de la esfera.
- Los **estados mixtos** (los que produce `Decoherence`) están DENTRO --
  cuanto más mixto, más cerca del centro.
- El **centro exacto** `(0,0,0)` es el estado máximamente mixto `I/2`.
- Los polos y el ecuador tienen significado concreto: `|0>` está en el
  polo `+Z`, `|1>` en el polo `-Z`, `|+>=(|0>+|1>)/sqrt2` en `+X`, etc.

Es la herramienta más directa para "ver" lo que le hace la decoherencia a
un qubit: una trayectoria bajo `Decoherence` es una espiral que va desde
la superficie hacia el centro; una trayectoria bajo `TimeEvolution` (sin
ruido) se queda siempre exactamente sobre la superficie (la evolución
unitaria nunca mezcla un estado puro).

## Métodos

### `vector(MatrixComplex rho)`
El vector de Bloch `(x,y,z) = (Tr(rho·X), Tr(rho·Y), Tr(rho·Z))` de una
matriz densidad `2x2`.

```java
double[] v = BlochSphere.vector(DensityMatrix.of(Qubits.ket0()));
// v == {0.0, 0.0, 1.0}  -- |0> esta en el polo +Z
```

### `vectorOfState(MatrixComplex psi)`
Atajo para un ket puro en vez de una matriz densidad ya construida --
`vector(DensityMatrix.of(psi))`.

### `fromVector(double x, double y, double z)`
La inversa: reconstruye `rho = (I + x·X + y·Y + z·Z)/2` a partir de un
punto. Lanza excepción si el punto está fuera de la bola unidad (no
correspondería a ningún estado físico -- `rho` tendría un autovalor
negativo).

### `plotTrajectory(String title, double[][] blochVectors, SimpleGnuplot.e_syncMode mode)`
Dibuja una esfera de referencia (malla) más una trayectoria de puntos
superpuesta, con los 3 ejes a la misma escala para que la esfera se vea
realmente redonda. `blochVectors` es un array de filas `{x,y,z}`, en orden.
`mode` es `SYNC` (bloquea hasta cerrar la ventana) o `ASYNC` (no bloquea).

## Experimento guiado: ver la decoherencia encoger el vector de Bloch

```java
import com.ipserc.arith.quantum.BlochSphere;
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.DensityMatrix;
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.plot.SimpleGnuplot;

public class ProbandoBlochSphere {
    public static void main(String[] args) {
        // Un qubit inicial en |+> (ecuador de la esfera, x=1,y=0,z=0)
        MatrixComplex rho = DensityMatrix.of(Qubits.hadamard().times(Qubits.ket0()));

        // Aplica depolarizing() repetidamente, en pasos crecientes de "dosis"
        double[][] trayectoria = new double[11][];
        for (int i = 0; i <= 10; ++i) {
            double p = i / 10.0;
            MatrixComplex rhoRuidoso = Decoherence.apply(rho, Decoherence.depolarizing(p), 0, 1);
            trayectoria[i] = BlochSphere.vector(rhoRuidoso);
            System.out.printf("p=%.1f -> (%.3f, %.3f, %.3f)%n", p,
                trayectoria[i][0], trayectoria[i][1], trayectoria[i][2]);
        }
        // El vector va de (1,0,0) en p=0 hasta (0,0,0) en p=1, en linea recta --
        // depolarizing(p) encoge el vector de Bloch por exactamente el factor (1-p)

        BlochSphere.plotTrajectory("Decoherencia de |+>", trayectoria, SimpleGnuplot.e_syncMode.SYNC);
        // abre una ventana grafica con la esfera y la trayectoria dibujada
    }
}
```

**Nota**: `plotTrajectory` abre una ventana de Gnuplot de verdad (necesita
tener `gnuplot` instalado y localizable, igual que el resto de la capa de
plotting del proyecto). Si solo quieres los NÚMEROS sin abrir ninguna
ventana, usa `SimpleGnuplot.e_syncMode.ASYNC` o simplemente no llames a
`plotTrajectory` y trabaja directamente con el array `trayectoria`.

## Relación con el resto del paquete

Depende de `Qubits` (Pauli), `DensityMatrix` (matriz densidad de un
estado puro) y `Decoherence` (fuente natural de trayectorias
interesantes). Usa la capa de plotting `com.ipserc.arith.plot`
(`SimpleGnuplot`/`PlotStyle`), la misma que usa el resto del proyecto para
gráficas 2D/3D fuera de este paquete.
