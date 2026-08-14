# `NoisyGrover.java` -- Grover + ruido

## Qué es

`Grover` (`11_Grover.md`) con un canal de ruido aplicado a UN qubit justo
tras preparar la superposición uniforme inicial, ANTES de que corra
ninguna ronda de oráculo+difusión -- decisión de diseño deliberada
(confirmada explícitamente antes de implementarla): a diferencia de
Deutsch-Jozsa/Bernstein-Vazirani (circuito de un solo disparo), Grover
repite oráculo+difusión `~sqrt(N)` veces, así que había que elegir entre
"una sola dosis de ruido al principio" (lo implementado aquí) o "ruido en
cada iteración" (más realista para hardware real, pero un patrón distinto,
no implementado en este paquete). La pregunta que responde esta clase: "una
única dosis de decoherencia, inyectada antes de que empiece la búsqueda,
¿se diluye o se amplifica con las `sqrt(N)` rondas de amplificación de
amplitud que siguen?".

## Métodos

### `circuitDensityMatrix(target, n, iterations, kraus, noisyQubit)`
La matriz densidad `2^n x 2^n` tras `iterations` rondas ruidosas: prepara
`Grover.initialState(n)`, aplica `kraus` al qubit `noisyQubit`, luego
`iterations` rondas de `diffusion(n)·oracle(target,n)` por conjugación.

### `probabilityOfTarget(rho, target)`
La probabilidad de medir `target` -- lectura diagonal directa (reusa
`NoisyDeutschJozsa.diagonalProbability()`).

### `search(target, n, kraus, noisyQubit)`
El análogo ruidoso de `Grover.search()`: `optimalIterations(n)` rondas
ruidosas, seguido de la probabilidad del objetivo.

```java
double p = NoisyGrover.search(5, 4, Decoherence.depolarizing(0.3), 0);
```

## Los 2 hallazgos centrales (genuinamente sorprendentes, verificados numéricamente)

Ninguno de los 2 se podía anticipar por analogía con `NoisyDeutschJozsa`/
`NoisyBernsteinVazirani` -- hubo que probarlos numéricamente antes de
escribirlos como hechos:

**1. `bitFlip(p)` tiene efecto EXACTAMENTE CERO sobre `search()`**, para
cualquier `target`/qubit/probabilidad. La razón: el estado inicial de
Grover es la superposición uniforme `|+>^n`, y `|+>` es el autoestado
PUNTO FIJO exacto de `X` (`X|+>=|+>`) -- así que el canal de bit-flip deja
el estado de ese qubit completamente intacto, sea cual sea la intensidad
del ruido.

**2. `amplitudeDamping` es el ÚNICO de los 4 canales cuyo efecto depende de
CUÁL estado base es el objetivo** -- concretamente del bit del `target` en
el qubit ruidoso (2 valores exactos posibles, ni más). La razón: es el
único de los 4 canales que NO es simétrico bajo `|0><->|1>`.
`depolarizing`/`phaseFlip`, en cambio, SÍ son simétricos y degradan de
forma UNIFORME (exactamente igual sin importar el `target` ni el qubit
elegido), de forma monótona con `p`.

## Experimento guiado: reproducir los 2 hallazgos

```java
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.Grover;
import com.ipserc.arith.quantum.NoisyGrover;

public class ProbandoNoisyGrover {
    public static void main(String[] args) {
        int n = 4; // N=16
        double base = Grover.search(5, n);
        System.out.println("Grover.search() sin ruido = " + base);

        System.out.println("--- bitFlip: SIN efecto, para cualquier p ---");
        for (double p : new double[] { 0.2, 0.5, 0.9, 1.0 }) {
            double conRuido = NoisyGrover.search(5, n, Decoherence.bitFlip(p), 0);
            System.out.println("p=" + p + "  search()=" + conRuido + " (deberia == " + base + ")");
        }

        System.out.println("--- amplitudeDamping: depende del bit del target en el qubit ruidoso ---");
        // target=0 (bits 0000): bit en qubit0 es 0
        // target=8 (bits 1000): bit en qubit0 es 1
        double bit0 = NoisyGrover.search(0, n, Decoherence.amplitudeDamping(1.0), 0);
        double bit1 = NoisyGrover.search(8, n, Decoherence.amplitudeDamping(1.0), 0);
        System.out.println("target con bit=0 en qubit0: " + bit0);
        System.out.println("target con bit=1 en qubit0: " + bit1);
        // Los 2 valores son distintos entre si, pero CUALQUIER otro target
        // con el mismo bit en esa posicion da EXACTAMENTE el mismo resultado
    }
}
```

## Relación con el resto del paquete

Combina `Grover`, `Decoherence` y `DensityMatrix`, reusando
`NoisyDeutschJozsa.diagonalProbability()` para la lectura final. Es la
cuarta y última de las combinaciones cruzadas "algoritmo + ruido"
construidas en este paquete.
