# `NoisyBernsteinVazirani.java` -- Bernstein-Vazirani + ruido

## Qué es

`BernsteinVazirani` (`10_BernsteinVazirani.md`) con ruido inyectado en un
qubit del registro tras la preparación, antes del circuito -- reusando
`NoisyDeutschJozsa.circuitDensityMatrix()` (`13_NoisyDeutschJozsa.md`) TAL
CUAL, porque el circuito ruidoso es byte a byte el mismo -- Bernstein-
Vazirani solo es una `f` particular. La pregunta que responde: "¿con qué
probabilidad se sigue recuperando el secreto correcto, con ruido?".

## Métodos

### `outcomeProbability(f, n, kraus, noisyQubit, x)`
La probabilidad de medir un resultado concreto `x` en el registro de
entrada (marginando la ancilla), con ruido -- construye la matriz densidad
vía `NoisyDeutschJozsa.circuitDensityMatrix()` y suma las 2 entradas
diagonales relevantes.

### `successProbability(secret, n, kraus, noisyQubit)`
La probabilidad de recuperar CORRECTAMENTE un secreto conocido, con ruido
-- `outcomeProbability(oracleFunction(secret,n), n, kraus, noisyQubit,
secret)`. La métrica principal de esta clase.

```java
double exito = NoisyBernsteinVazirani.successProbability(5, 3, Decoherence.depolarizing(0.3), 3);
```

### `findMostLikelySecret(f, n, kraus, noisyQubit)`
El análogo ruidoso de `BernsteinVazirani.findSecret()`: la mejor conjetura
(el `x` con mayor probabilidad) cuando el circuito ya no colapsa
determinísticamente. A diferencia de la versión sin ruido, NUNCA lanza
excepción -- el ruido puede legítimamente bajar la confianza por debajo de
`1`, eso es justo lo que esta clase estudia.

## Los hallazgos centrales (verificados numéricamente)

**1. `secret=0` es exactamente la `f` constante de Deutsch-Jozsa** --
hereda su invariante: el ruido en la ancilla nunca mueve
`successProbability()`, se queda EXACTAMENTE en `1.0` bajo cualquier canal,
cualquier intensidad.

**2. Para un secreto NO nulo, el ruido en la ancilla SÍ degrada**, con
fórmula cerrada exacta: `depolarizing(p)` da `1-p/2`, `amplitudeDamping
(gamma)` da `1-gamma` -- y la probabilidad PERDIDA aterriza ENTERA en el
resultado espurio `x=0` (no se reparte entre otros `x`).

**3. El ruido en un qubit de ENTRADA degrada CUALQUIER secreto por
igual**: `bitFlip(p)` en cualquier posición de entrada da
`successProbability()=1-p` exacto, sea cual sea el valor del bit del
secreto en esa posición -- el canal no depende del valor actual del qubit.

**4. `amplitudeDamping`/`phaseFlip` en un qubit de entrada no tienen NINGÚN
efecto**, para cualquier secreto -- los qubits de entrada arrancan
exactamente en `|0>`, el punto fijo de esos 2 canales (un hecho puramente
sobre el estado ANTES del oráculo, independiente de `f`).

## Experimento guiado: dónde aterriza la probabilidad perdida

```java
import com.ipserc.arith.quantum.BernsteinVazirani;
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.NoisyBernsteinVazirani;
import java.util.function.IntPredicate;

public class ProbandoNoisyBernsteinVazirani {
    public static void main(String[] args) {
        int n = 3;
        int secreto = 5;
        IntPredicate f = BernsteinVazirani.oracleFunction(secreto, n);

        System.out.println("--- Ruido en la ANCILLA (qubit " + n + ") ---");
        for (double p : new double[] { 0.0, 0.25, 0.5, 0.75, 1.0 }) {
            double exito = NoisyBernsteinVazirani.outcomeProbability(f, n, Decoherence.depolarizing(p), n, secreto);
            double enCero = NoisyBernsteinVazirani.outcomeProbability(f, n, Decoherence.depolarizing(p), n, 0);
            System.out.printf("p=%.2f  P(secreto correcto)=%.4f (esperado 1-p/2=%.4f)  P(x=0)=%.4f (esperado p/2=%.4f)%n",
                p, exito, 1.0 - p / 2.0, enCero, p / 2.0);
        }

        System.out.println("--- Recuperacion de la mejor conjetura bajo ruido sub-critico ---");
        int conjetura = NoisyBernsteinVazirani.findMostLikelySecret(f, n, Decoherence.depolarizing(0.8), n);
        System.out.println("con depolarizing(p=0.8) en la ancilla, la mejor conjetura sigue siendo: " + conjetura);
        // sigue siendo 5 -- el 0.8 no llega al punto de empate exacto (p=1.0)
    }
}
```

## Relación con el resto del paquete

Depende enteramente de `NoisyDeutschJozsa` (reusa su circuito y su lectura
diagonal) y de `BernsteinVazirani` (la `f` concreta a estudiar). Comparte
el mismo patrón de diseño que `NoisyGrover`: reusar la maquinaria de matriz
densidad ya construida por otra clase `Noisy*` en vez de duplicar la
construcción del circuito.
