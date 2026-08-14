# `BellTest.java` -- el experimento CHSH (violación de la desigualdad de Bell)

## Qué es

La simulación del experimento más famoso de los fundamentos de la mecánica
cuántica: demostrar que las correlaciones que se miden sobre un par de
qubits entrelazados NO pueden explicarse con ninguna teoría clásica de
"variables ocultas locales" -- la idea (intuitiva, y la que Einstein
defendía) de que cada partícula "lleva consigo" de antemano el resultado
que dará ante cualquier medida posible, y que ese resultado no depende de
lo que se le haga a la otra partícula, por lejos que esté.

## Para qué sirve / contexto físico

En 1964 John Bell demostró que cualquier teoría de variables ocultas
locales predice que una combinación concreta de 4 correlaciones medidas
(el parámetro CHSH, `S`) nunca puede superar `2` en valor absoluto. La
mecánica cuántica, para un par entrelazado y los ángulos de medida
adecuados, predice `S = 2·sqrt(2) ≈ 2.828` -- por encima del límite
clásico. Este experimento se ha realizado en el laboratorio real muchas
veces (Premio Nobel de Física 2022, Aspect/Clauser/Zeilinger) y siempre
confirma la predicción cuántica, no la clásica.

Esta clase ofrece 2 caminos para llegar al mismo número:
- **Cálculo exacto** (`correlation`/`chsh`): álgebra matricial directa,
  sin aleatoriedad -- el valor "de libro" que se supone que predice la
  teoría.
- **Simulación Monte Carlo** (`simulateCorrelation`/`simulateChsh`):
  muestrea medidas individuales (con la regla de Born) igual que lo haría
  un experimento de laboratorio real, y promedia sobre muchas repeticiones
  -- converge al valor exacto a medida que crece el número de intentos
  (`trials`).

## Métodos

### `correlation(state, opA, qubitA, opB, qubitB, nQubits)`
La correlación cuántica exacta `E(A,B) = <state|(opA en qubitA) · (opB en
qubitB)|state>`, midiendo 2 qubits cualesquiera (no necesariamente
adyacentes) de un registro de `nQubits`. Internamente usa
`Qubits.operatorOnQubit()` para "elevar" cada operador y
`TimeEvolution.expectationValue()` para el valor esperado.

Hay una versión de conveniencia de 2 qubits, `correlation(state, opA, opB)`,
que asume `qubitA=0`, `qubitB=1`, `nQubits=2`.

```java
double e = BellTest.correlation(Qubits.bellPhiPlus(), Qubits.pauliZ(), Qubits.pauliZ());
// e == 1.0: en un par de Bell, medir Z en ambos qubits SIEMPRE da el mismo signo
```

### `chsh(state, qubitA, qubitB, nQubits, a, aPrime, b, bPrime)`
El parámetro CHSH exacto, `S = E(a,b) - E(a,b') + E(a',b) + E(a',b')`, usando
la familia `Qubits.spinOperator(theta)` a 4 ángulos. Para `bellPhiPlus()` la
correlación sale `E(a,b)=cos(a-b)`, maximizada con ángulos espaciados
`pi/4`: `a=0, a'=pi/2, b=pi/4, b'=3*pi/4` da el máximo teórico
`2*sqrt(2)`.

```java
double s = BellTest.chsh(Qubits.bellPhiPlus(), 0.0, Math.PI / 2, Math.PI / 4, 3 * Math.PI / 4);
// s ~= 2.8284271247461903  (2*sqrt(2), el limite de Tsirelson)
```

También existe la versión de conveniencia de 2 qubits sin `qubitA`/`qubitB`/
`nQubits`.

### `simulateCorrelation(state, thetaA, qubitA, thetaB, qubitB, nQubits, trials, rng)`
La versión Monte Carlo de `correlation`: simula `trials` medidas conjuntas
independientes (con la regla de Born, resultados `+1`/`-1`), y promedia el
producto de los 2 resultados en cada intento. `rng` es un `java.util.Random`
-- pásale una semilla fija para resultados reproducibles.

```java
double eSim = BellTest.simulateCorrelation(
    Qubits.bellPhiPlus(), 0.0, Math.PI / 4, 100000, new java.util.Random(42));
// se acerca a correlation(...)=cos(0-pi/4)=0.7071 según crece "trials"
```

### `simulateChsh(state, qubitA, qubitB, nQubits, a, aPrime, b, bPrime, trials, rng)`
La versión Monte Carlo de `chsh`: 4 llamadas a `simulateCorrelation`, una
por cada pareja de ángulos.

## Experimento guiado: reproducir la violación de Bell

Este es exactamente el experimento que corre `TestBell01.java`
(`src/TestComplex/TestBell01.java`) -- puedes leerlo entero para ver todas
las comprobaciones, o replicar el núcleo aquí:

```java
import com.ipserc.arith.quantum.BellTest;
import com.ipserc.arith.quantum.Qubits;
import java.util.Random;

public class ProbandoBellTest {
    public static void main(String[] args) {
        double a = 0.0, aPrime = Math.PI / 2;
        double b = Math.PI / 4, bPrime = 3 * Math.PI / 4;

        double sExact = BellTest.chsh(Qubits.bellPhiPlus(), a, aPrime, b, bPrime);
        System.out.println("S exacto = " + sExact); // ~2.8284, > 2 (limite clasico)

        Random rng = new Random(42);
        for (int trials : new int[] { 100, 10_000, 1_000_000 }) {
            double sSim = BellTest.simulateChsh(Qubits.bellPhiPlus(), a, aPrime, b, bPrime, trials, rng);
            System.out.println("S simulado (trials=" + trials + ") = " + sSim);
            // converge hacia sExact segun crece trials
        }
    }
}
```

**Qué esperar**: `S exacto` debe salir `2.8284271247461903` de forma
determinista. `S simulado` empieza más lejos de ese valor (para `trials`
pequeño) y se va acercando según crece `trials` -- fluctuación estadística
normal, no un error.

## Generalización a n qubits (hallazgo de la Trigesimoséptima sesión)

Las formas `correlation`/`chsh`/`simulateCorrelation`/`simulateChsh` con
`qubitA`/`qubitB`/`nQubits` explícitos permiten medir 2 qubits cualesquiera
de un registro más grande -- por ejemplo, 2 qubits de un
`Qubits.ghz(5)`. Esto se validó de forma cruzada contra
`DensityMatrix.partialTrace()` (ver `04_DensityMatrix.md`): la correlación
calculada aquí coincide exactamente con `Tr(rho_reducida·(opA⊗opB))` sobre
la matriz densidad reducida de esos 2 qubits.

## Relación con el resto del paquete

Usa `Qubits` (kets, `spinOperator`, `operatorOnQubit`) y
`TimeEvolution.expectationValue()` internamente. Es un buen punto de
partida tras `02_Qubits.md` porque produce un resultado numérico concreto y
verificable sin necesitar ningún otro concepto nuevo.
