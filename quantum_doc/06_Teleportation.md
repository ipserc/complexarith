# `Teleportation.java` -- teletransportación cuántica

## Qué es

La simulación del protocolo de teletransportación cuántica: Alicia tiene un
qubit desconocido `psi` que quiere que Bob tenga, y comparten un par de Bell
(1 mitad cada uno). Tras un circuito local en el lado de Alicia y una
medida, Alicia envía 2 bits CLÁSICOS a Bob; con esos 2 bits, Bob aplica una
corrección sencilla a su propio qubit y el resultado es EXACTAMENTE `psi`
-- sin que ningún qubit haya viajado físicamente de Alicia a Bob.

## Para qué sirve / contexto físico

No es "teletransporte" en el sentido de ciencia ficción -- no se mueve
materia ni energía más rápido que la luz (los 2 bits clásicos tienen que
viajar por un canal normal, limitado por la velocidad de la luz). Lo que sí
demuestra es que un estado cuántico DESCONOCIDO se puede transferir
usando solo: 1 recurso entrelazado compartido de antemano + 2 bits
clásicos -- ni una copia física del qubit original, que además queda
destruido en el proceso (el "teorema de no-clonación" prohíbe copiar un
estado cuántico desconocido; teletransportar no lo viola porque el
original se pierde al medir).

## Convención de qubits

Registro de 3 qubits, MSB-first: qubit 0 es `psi` (el qubit de Alicia a
teletransportar), qubit 1 es la mitad de Alicia del par de Bell, qubit 2 es
la mitad de Bob.

## Métodos

### `circuitState(MatrixComplex psi)` -- paquete-visible, no pública
El estado de los 3 qubits justo antes de que Alicia mida: `psi` entrelazado
con un par de Bell, luego el circuito local de Alicia (`CNOT`
qubit0→qubit1, después `Hadamard` en qubit0). No se llama directamente
desde fuera del paquete -- es el paso interno que usan los métodos
públicos de abajo.

### `probabilityOfOutcome(MatrixComplex psi, int m1, int m2)`
La probabilidad (regla de Born) de que Alicia mida el resultado clásico
`(m1,m2)` sobre sus 2 qubits. Un resultado notable, verificado en la
batería de tests: sale EXACTAMENTE `0.25` para las 4 combinaciones posibles
de `(m1,m2)`, sin importar cuál sea `psi` -- la medida de Alicia no revela
NADA sobre `psi`, solo produce 2 bits aleatorios uniformes que Bob
necesitará para su corrección.

```java
double p00 = Teleportation.probabilityOfOutcome(psi, 0, 0); // == 0.25 siempre
```

### `correctedStateForOutcome(MatrixComplex psi, int m1, int m2)`
El qubit de Bob, YA corregido, para el resultado clásico `(m1,m2)` que
Alicia midió -- el valor exacto (sin aleatoriedad) que predice la teoría
para esa rama concreta. La corrección se deriva analíticamente: el estado
crudo (sin corregir) de Bob para la rama `(m1,m2)` es `X^m2·Z^m1·psi`
(hasta un factor real positivo), así que la corrección que lo deshace es
`Z^m1` aplicado DESPUÉS de `X^m2`:

| `(m1,m2)` | Corrección necesaria |
|---|---|
| `(0,0)` | ninguna |
| `(0,1)` | `X` |
| `(1,0)` | `Z` |
| `(1,1)` | `X` luego `Z` |

El resultado es EXACTAMENTE `psi` -- no solo "hasta una fase global", sino
la fase exacta también, verificado con estados de amplitud no trivial.

```java
MatrixComplex bobsQubit = Teleportation.correctedStateForOutcome(psi, 1, 0);
// bobsQubit == psi, exactamente (hasta redondeo de punto flotante)
```

### `simulate(MatrixComplex psi, Random rng)`
Una ejecución completa simulada del protocolo: muestrea el resultado de
Alicia (con la regla de Born, igual que `BellTest.simulateCorrelation()`),
y devuelve el qubit corregido de Bob para esa rama muestreada -- igual
que lo haría un laboratorio real (que no elige qué rama sale, solo la
observa).

```java
MatrixComplex bobsQubit = Teleportation.simulate(psi, new java.util.Random(42));
// == psi, sea cual sea la rama muestreada
```

## Experimento guiado: teletransportar un qubit y verificarlo

```java
import com.ipserc.arith.quantum.Teleportation;
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ProbandoTeleportation {
    public static void main(String[] args) {
        // Un qubit "desconocido" con amplitudes no triviales
        MatrixComplex psi = new MatrixComplex(2, 1);
        psi.setItem(0, 0, new Complex(0.6, 0.0));
        psi.setItem(1, 0, new Complex(0.0, 0.8));

        // Recorre las 4 ramas posibles y comprueba que TODAS dan psi exacto
        for (int m1 = 0; m1 <= 1; ++m1) {
            for (int m2 = 0; m2 <= 1; ++m2) {
                double prob = Teleportation.probabilityOfOutcome(psi, m1, m2);
                MatrixComplex bob = Teleportation.correctedStateForOutcome(psi, m1, m2);
                System.out.println("(m1=" + m1 + ",m2=" + m2 + ") prob=" + prob + " bob=" + bob);
            }
        }

        // Una ejecucion simulada individual
        MatrixComplex bobSim = Teleportation.simulate(psi, new java.util.Random(7));
        System.out.println("Simulado: " + bobSim);
    }
}
```

**Qué esperar**: las 4 probabilidades deben salir `0.25` cada una, y los 4
`bob` deben coincidir con `psi` (fila 0 = `0.6+0i`, fila 1 = `0+0.8i`).

## Relación con el resto del paquete

Usa `Qubits.bellPhiPlus()`, `Qubits.controlledGate()`, `Qubits.hadamard()`.
`NoisyTeleportation` (`12_NoisyTeleportation.md`) es la combinación de esta
clase con `Decoherence` -- qué pasa si el par de Bell compartido (u otro
qubit del protocolo) ya ha sufrido ruido antes de que corra el protocolo.
