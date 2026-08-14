# `NoisyDeutschJozsa.java` -- Deutsch-Jozsa + ruido

## Qué es

`DeutschJozsa` (`09_DeutschJozsa.md`) con un canal de ruido aplicado a UNO
de los `n+1` qubits del registro justo tras preparar el estado, ANTES de
que corra ninguna puerta del circuito -- la pregunta "¿sobrevive la
garantía determinista todo-o-nada (`probabilityAllZero()` exactamente `0`
o `1`) si el registro ya ha decoherido para cuando se ejecuta el
circuito?". Trabaja en matriz densidad, igual que `NoisyTeleportation`.

## Métodos

### `circuitDensityMatrix(f, n, kraus, noisyQubit)` -- paquete-visible
La matriz densidad `2^(n+1) x 2^(n+1)` justo antes de medir, CON ruido:
prepara `|0>^n|1>`, aplica `kraus` al qubit `noisyQubit`, luego el circuito
completo de Deutsch-Jozsa por conjugación. Esta pieza se reusa TAL CUAL en
`NoisyBernsteinVazirani` (mismo circuito exacto, distinta `f`).

### `diagonalProbability(rho, index)` -- paquete-visible
Una única entrada diagonal real de una matriz densidad -- es directamente
una probabilidad de medida en la base computacional. También reusado por
`NoisyBernsteinVazirani`/`NoisyGrover`.

### `probabilityAllZero(f, n, kraus, noisyQubit)`
La probabilidad de medir "todo ceros" en el registro de entrada, con
ruido -- la suma de las 2 entradas diagonales relevantes (`x=0`, ambos
valores de la ancilla) de `circuitDensityMatrix()`.

```java
double p = NoisyDeutschJozsa.probabilityAllZero(f, 4, Decoherence.depolarizing(0.3), 4);
```

## Los 2 hallazgos centrales (verificados numéricamente, no asumidos)

Estos son el resultado más interesante de esta clase -- vale la pena
entenderlos, porque son la base de lo que luego se encuentra en
`NoisyBernsteinVazirani` también:

**1. El ruido en la ANCILLA nunca afecta a una `f` CONSTANTE.**
`probabilityAllZero()` se queda EXACTAMENTE en `1.0`, sea cual sea el
canal, sea cual sea su intensidad -- incluso `amplitudeDamping(1.0)`, que
fuerza a la ancilla a un estado completamente distinto. La razón física:
una `f` constante NUNCA entrelaza la ancilla con el registro de entrada
(el oráculo actúa igual para toda `x`), así que el `H⊗n` final siempre
deshace EXACTAMENTE el `H⊗n` inicial, sin importar en qué estado esté la
ancilla (que además se marginaliza al medir).

**2. Pero el ruido en la ancilla SÍ degrada una `f` EQUILIBRADA**, con
fórmula cerrada exacta para algunos canales: `depolarizing(p)` da
exactamente `p/2`, `amplitudeDamping(gamma)` da exactamente `gamma`.

**3. El ruido en un qubit de ENTRADA es justo al revés**: degrada una `f`
CONSTANTE (`bitFlip(p)` da la fórmula cerrada exacta `1-p`), pero
`amplitudeDamping`/`phaseFlip` en un qubit de entrada NO tienen NINGÚN
efecto para ninguna `f` -- porque los qubits de entrada arrancan
EXACTAMENTE en `|0>`, el punto fijo de esos 2 canales.

## Experimento guiado: reproducir los 2 hallazgos

```java
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.DeutschJozsa;
import com.ipserc.arith.quantum.NoisyDeutschJozsa;
import java.util.function.IntPredicate;

public class ProbandoNoisyDeutschJozsa {
    public static void main(String[] args) {
        int n = 3;
        IntPredicate constante = x -> false;
        IntPredicate paridad = x -> (Integer.bitCount(x) % 2) == 1; // equilibrada

        System.out.println("--- Ruido en la ANCILLA (qubit n=" + n + ") ---");
        for (double p : new double[] { 0.0, 0.25, 0.5, 0.75, 1.0 }) {
            double pConstante = NoisyDeutschJozsa.probabilityAllZero(constante, n, Decoherence.depolarizing(p), n);
            double pEquilibrada = NoisyDeutschJozsa.probabilityAllZero(paridad, n, Decoherence.depolarizing(p), n);
            System.out.printf("p=%.2f  constante:%.4f (deberia quedarse en 1.0)  equilibrada:%.4f (deberia ser p/2=%.4f)%n",
                p, pConstante, pEquilibrada, p / 2.0);
        }

        System.out.println("--- Ruido en un qubit de ENTRADA (qubit 0) ---");
        for (double p : new double[] { 0.0, 0.25, 0.5, 0.75, 1.0 }) {
            double pConstanteBitFlip = NoisyDeutschJozsa.probabilityAllZero(x -> true, n, Decoherence.bitFlip(p), 0);
            System.out.printf("bitFlip p=%.2f  constante(f=1):%.4f (deberia ser 1-p=%.4f)%n",
                p, pConstanteBitFlip, 1.0 - p);
        }
    }
}
```

## Relación con el resto del paquete

Combina `DeutschJozsa`, `Decoherence` y `DensityMatrix`.
`NoisyBernsteinVazirani` (`14_NoisyBernsteinVazirani.md`) reusa
`circuitDensityMatrix()`/`diagonalProbability()` de esta clase VERBATIM --
mismo circuito ruidoso exacto, solo cambia qué se lee del resultado final.
