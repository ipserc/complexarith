# `DensityMatrix.java` -- formalismo de matriz densidad

## Qué es

El formalismo alternativo a "vector de estado" que hace falta para describir
**estados mixtos** (ver `01_conceptos_basicos.md`, sección 6) -- y, en
particular, la herramienta (`partialTrace`) que permite mirar SOLO una parte
de un sistema entrelazado, y la métrica (`vonNeumannEntropy`) que cuantifica
cuánto entrelazamiento hay.

## Para qué sirve / cuándo usarla

Siempre que necesites:
1. Convertir un estado puro (`MatrixComplex` columna) en su matriz densidad
   equivalente, para pasarlo a una clase que ya trabaja en ese formalismo
   (`Decoherence`, `BlochSphere`, las clases `Noisy*`).
2. "Aislar" un subconjunto de qubits de un registro más grande, ignorando
   el resto (`partialTrace`) -- imprescindible para razonar sobre
   entrelazamiento o para leer el estado de un qubit concreto tras un
   protocolo que involucra varios (p. ej. el qubit de Bob en
   `Teleportation`).
3. Medir cuánto está entrelazado un subsistema con el resto
   (`vonNeumannEntropy`).

## Métodos

### `of(MatrixComplex state)`
La matriz densidad de un estado puro, `rho = |state><state|` --
`state.times(state.adjoint())`. Siempre una matriz de rango 1 (un único
autovalor no nulo, igual a 1).

```java
MatrixComplex rho = DensityMatrix.of(Qubits.ket0());
// rho = [[1,0],[0,0]]  -- |0><0|
```

### `partialTrace(MatrixComplex rho, int nQubits, int... traceOutQubits)`
La operación central de esta clase: dada la matriz densidad de un registro
de `nQubits` qubits, "traza fuera" (descarta, promedia) los qubits listados
en `traceOutQubits` y devuelve la matriz densidad reducida de los qubits
restantes. Si el estado global es puro pero entrelazado, el resultado
reducido sale genuinamente MIXTO -- esa es la señal de entrelazamiento.

```java
MatrixComplex rhoBell = DensityMatrix.of(Qubits.bellPhiPlus());  // 4x4, puro
MatrixComplex rhoQubit0 = DensityMatrix.partialTrace(rhoBell, 2, 1); // traza fuera el qubit 1
// rhoQubit0 = [[0.5,0],[0,0.5]]  -- I/2, MAXIMAMENTE MIXTO,
// aunque el estado global bellPhiPlus() es PURO
```

Este resultado (`I/2` para un qubit de un par de Bell, aunque el par
completo sea puro) es el ejemplo canónico de por qué el entrelazamiento es
extraño: cada mitad, mirada por separado, no tiene ningún estado definido
propio -- toda la información está en la CORRELACIÓN entre las 2 mitades,
no en ninguna de ellas.

### `vonNeumannEntropy(MatrixComplex rho)`
La entropía de von Neumann `S(rho) = -sum(lambda_i·log2(lambda_i))` sobre
los autovalores `lambda_i` de `rho` -- `0` para un estado puro (rango 1,
un autovalor `=1`), hasta `log2(dim)` para el estado máximamente mixto.
Aplicada a una traza parcial, un valor `>0` demuestra entrelazamiento.

```java
double sBell = DensityMatrix.vonNeumannEntropy(rhoQubit0);
// sBell == 1.0 bit -- entropia maxima para un qubit (dim=2, log2(2)=1),
// confirma que el par de Bell esta MAXIMAMENTE entrelazado
```

## Experimento guiado: medir el entrelazamiento de un GHZ de 3 qubits

```java
import com.ipserc.arith.quantum.DensityMatrix;
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ProbandoDensityMatrix {
    public static void main(String[] args) {
        MatrixComplex ghz3 = Qubits.ghz(3);           // (|000>+|111>)/sqrt2
        MatrixComplex rho = DensityMatrix.of(ghz3);    // 8x8, puro

        // Traza fuera 2 qubits, deja solo 1 -- deberia salir I/2 (maximamente mixto)
        MatrixComplex rhoUnQubit = DensityMatrix.partialTrace(rho, 3, 1, 2);
        System.out.println(rhoUnQubit);
        System.out.println("Entropia = " + DensityMatrix.vonNeumannEntropy(rhoUnQubit));
        // Entropia == 1.0 bit, igual que en el par de Bell -- cualquier UN qubit
        // de un GHZ esta tan entrelazado con "el resto" como en un par de Bell simple

        // Traza fuera 1 qubit, deja 2 -- el resultado NO es un Bell puro
        // (es una mezcla clasica 50/50 de |00> y |11>, no una superposicion)
        MatrixComplex rhoDosQubits = DensityMatrix.partialTrace(rho, 3, 2);
        System.out.println(rhoDosQubits);
        System.out.println("Entropia = " + DensityMatrix.vonNeumannEntropy(rhoDosQubits));
        // Entropia == 1.0 bit tambien -- sigue mixto, no es un Bell puro (que daria 0)
    }
}
```

Este último resultado es un hallazgo genuinamente interesante y no del todo
intuitivo: los 2 qubits restantes de un GHZ de 3, mirados juntos (sin el
tercero), NO forman un par de Bell puro -- son una mezcla estadística
clásica 50%/50% de `|00>` y `|11>` (correlacionados, pero sin la coherencia
cuántica de una superposición). Es un ejemplo de por qué "estar
entrelazado con el resto" y "ser un estado puro entrelazado en sí mismo"
son cosas distintas.

## Relación con el resto del paquete

Usada por `BellTest` (verificación cruzada de `correlation()`),
`Decoherence`/`BlochSphere` (el formalismo de partida), y por TODAS las
clases `Noisy*` (`NoisyTeleportation`, `NoisyDeutschJozsa`,
`NoisyBernsteinVazirani`, `NoisyGrover`), que necesitan trabajar con
estados genuinamente mixtos.
