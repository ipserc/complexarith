# `NoisyTeleportation.java` -- teletransportación + ruido

## Qué es

La primera combinación cruzada del paquete: `Teleportation`
(`06_Teleportation.md`) con `Decoherence` (`07_Decoherence.md`) aplicado a
UNO de los 3 qubits del protocolo, ANTES de que corra el circuito local de
Alicia -- la pregunta "¿qué pasa con la fidelidad de la teletransportación
si el par de Bell compartido (o el propio qubit `psi`) ya ha sufrido ruido
antes de que se ejecute el protocolo?".

Trabaja en el formalismo de matriz densidad (`DensityMatrix`), porque un
canal de ruido puede convertir un estado puro en genuinamente mixto -- algo
que el vector de estado que usa `Teleportation` ya no puede representar.

## Convención de qubits

Igual que `Teleportation`: qubit 0 = `psi` (Alicia), qubit 1 = mitad de
Alicia del par de Bell, qubit 2 = mitad de Bob.

## Métodos

### `circuitDensityMatrix(psi, kraus, noisyQubit)` -- paquete-visible
La matriz densidad `8x8` de los 3 qubits justo antes de la medida de
Alicia, CON ruido: `psi` entrelazado con un par de Bell, ruido aplicado al
qubit `noisyQubit`, luego el circuito de Alicia por conjugación
(`U·rho·U†`). No se llama directamente desde fuera del paquete.

### `probabilityOfOutcome(psi, kraus, noisyQubit, m1, m2)`
La probabilidad (Born) del resultado clásico `(m1,m2)` de Alicia, con
ruido. En el caso sin ruido esto siempre da `0.25` exacto (ver
`Teleportation`); verificado que TODOS los canales de este paquete
preservan esa uniformidad (conmutan con la simetría del par de Bell que la
produce) -- pero el método en sí no lo asume, calcula el valor real.

### `correctedDensityMatrixForOutcome(psi, kraus, noisyQubit, m1, m2)`
El qubit de Bob, corregido, como matriz densidad `2x2`, para la rama
`(m1,m2)`. Igual que en `Teleportation`, se obtiene: colapsar sobre
`(m1,m2)`, trazar fuera los qubits 0/1 (`DensityMatrix.partialTrace()`),
normalizar, aplicar la MISMA corrección (`Z^m1` tras `X^m2`, ahora por
conjugación en vez de multiplicación por la izquierda). Lanza excepción si
esa rama tiene probabilidad `~0` (normalizar por casi-cero no tiene
sentido) -- para ese caso, usa `averageFidelity()` en su lugar.

### `fidelityForOutcome(psi, kraus, noisyQubit, m1, m2)`
La fidelidad `<psi|rho|psi>` del qubit corregido de Bob respecto al `psi`
original, para una rama concreta -- `1` si la teletransportación de esa
rama fue perfecta, menos si el ruido la degradó.

### `averageFidelity(psi, kraus, noisyQubit)`
La fidelidad global, promediada sobre las 4 ramas ponderadas por su
probabilidad -- calculada SIN normalizar cada rama por separado (la
normalización y el peso de probabilidad se cancelan algebraicamente), así
que nunca hay riesgo de dividir por una probabilidad casi nula.

```java
double fidelidad = NoisyTeleportation.averageFidelity(psi, Decoherence.depolarizing(0.3), 1);
// 1.0 con kraus "sin efecto" (p=0); estrictamente menor que 1 con ruido real
```

## Experimento guiado: fidelidad frente a intensidad de ruido

```java
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.NoisyTeleportation;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ProbandoNoisyTeleportation {
    public static void main(String[] args) {
        MatrixComplex psi = new MatrixComplex(2, 1);
        psi.setItem(0, 0, new Complex(0.6, 0.0));
        psi.setItem(1, 0, new Complex(0.0, 0.8));

        // Ruido en el qubit 1 (la mitad de Alicia del par de Bell compartido)
        for (double p : new double[] { 0.0, 0.1, 0.25, 0.5, 0.75, 1.0 }) {
            double fidelidad = NoisyTeleportation.averageFidelity(psi, Decoherence.depolarizing(p), 1);
            System.out.printf("depolarizing p=%.2f -> fidelidad=%.4f%n", p, fidelidad);
        }
        // Fidelidad decrece ESTRICTAMENTE segun crece p -- mas ruido, peor teletransportacion

        // Compara ruido en el recurso compartido (qubit 1) vs en psi mismo (qubit 0)
        double fidResource = NoisyTeleportation.averageFidelity(psi, Decoherence.amplitudeDamping(0.4), 1);
        double fidPsi = NoisyTeleportation.averageFidelity(psi, Decoherence.amplitudeDamping(0.4), 0);
        System.out.println("ruido en el recurso: " + fidResource);
        System.out.println("ruido en psi mismo:  " + fidPsi);
        // Ambos degradan la fidelidad -- el ruido "importa" en cualquiera de los 2 sitios
    }
}
```

## Un puente de verificación notable

Con `kraus` "sin efecto" (p. ej. `Decoherence.bitFlip(0.0)`),
`correctedDensityMatrixForOutcome()` coincide EXACTAMENTE con
`DensityMatrix.of(Teleportation.correctedStateForOutcome(...))` -- la
maquinaria nueva de matriz densidad concuerda con la implementación de
vector puro ya verificada en `Teleportation`. Si alguna vez modificas esta
clase, esta comparación es la primera que deberías volver a comprobar.

## Relación con el resto del paquete

Combina `Teleportation`, `Decoherence` y `DensityMatrix`. Es el patrón que
siguieron después `NoisyDeutschJozsa`, `NoisyBernsteinVazirani` y
`NoisyGrover`: "coge un algoritmo/protocolo ya existente, inyecta ruido al
preparar el estado, recastea en matriz densidad, verifica que con ruido
nulo coincide exactamente con la versión original".
