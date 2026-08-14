# `Decoherence.java` -- canales de ruido (operadores de Kraus)

## Qué es

El modelo de "un qubit interactúa con un entorno ruidoso que no estamos
simulando explícitamente" -- 5 familias de canales de ruido de 1 qubit
(cada una expresada por sus operadores de Kraus, ver
`01_conceptos_basicos.md` sección 7), la función que aplica cualquiera de
ellos a un qubit concreto de un registro más grande, y la función que
encadena varios canales (posiblemente distintos, posiblemente sobre
distintos qubits) en una sola ejecución.

## Para qué sirve / contexto físico

Todo lo demás del paquete, hasta este punto, trabaja con evolución
UNITARIA -- reversible, sin pérdida de información. En el mundo real, ningún
qubit está perfectamente aislado: interactúa (aunque sea débilmente) con su
entorno, y esa interacción, aunque no la observemos en detalle, tiene un
efecto neto sobre el estado del qubit: lo hace más MIXTO (ver
`04_DensityMatrix.md`). Esto es la **decoherencia**, la razón física
principal por la que construir un ordenador cuántico real es tan difícil.

## Métodos

### `apply(MatrixComplex rho, MatrixComplex[] kraus, int qubitIndex, int nQubits)`
La fórmula central: `rho' = sum_k E_k·rho·E_k†`, con cada `E_k` elevado al
registro completo vía `Qubits.operatorOnQubit()` (identidad en el resto de
qubits, igual que el resto del paquete hace con operadores de 1 qubit).

```java
MatrixComplex rho = DensityMatrix.of(Qubits.ket0());
MatrixComplex rhoRuidoso = Decoherence.apply(rho, Decoherence.bitFlip(0.3), 0, 1);
```

### `applyChain(MatrixComplex rho, int nQubits, MatrixComplex[][] channels, int[] qubitIndices)`
Encadena varios canales, uno tras otro (`channels[0]` primero), cada uno
actuando sobre el qubit indicado en la posición correspondiente de
`qubitIndices` -- repeticiones del mismo canal, canales distintos, sobre el
mismo qubit o sobre distintos qubits, todo físicamente válido ("el qubit
pasó un tiempo en un entorno ruidoso, y en ese tiempo actuaron varios
procesos de ruido distintos, o el mismo proceso 2 veces").

```java
MatrixComplex resultado = Decoherence.applyChain(rho, 2,
    new MatrixComplex[][] { Decoherence.bitFlip(0.2), Decoherence.amplitudeDamping(0.3) },
    new int[] { 0, 1 }); // bitFlip en el qubit 0, luego amplitudeDamping en el qubit 1
```

**2 cosas importantes sobre cómo se combinan los canales encadenados**
(verificadas numéricamente, no solo enunciadas):
1. Encadenar el MISMO canal 2 veces con probabilidades `p1`/`p2` NO es
   igual a una sola aplicación con probabilidad `p1+p2` -- para
   `bitFlip`, la fórmula exacta de combinación es `p1+p2-2·p1·p2` (la
   probabilidad de que ocurra exactamente uno de 2 eventos independientes).
2. El ORDEN puede importar o no, según qué 2 familias se combinen:
   `bitFlip`/`phaseFlip` conmutan entre sí (da igual el orden),
   `amplitudeDamping`/`phaseFlip` también conmutan, pero
   `amplitudeDamping`/`bitFlip` NO conmutan -- el resultado cambia según
   cuál se aplique primero. Ver `ScratchDecoherenceChainAudit01.java` para
   la demostración numérica completa.

### `bitFlip(double p)`
Con probabilidad `p` aplica `pauliX()` (voltea `|0>`/`|1>`), si no deja el
qubit igual. Kraus: `{sqrt(1-p)·I, sqrt(p)·X}`.

### `phaseFlip(double p)`
Con probabilidad `p` aplica `pauliZ()` (voltea la fase relativa entre
`|0>`/`|1>`, sin tocar las poblaciones). Kraus: `{sqrt(1-p)·I, sqrt(p)·Z}`.
La forma más "pura" de decoherencia: destruye coherencia sin ningún
intercambio de energía.

### `bitPhaseFlip(double p)`
Con probabilidad `p` aplica `pauliY()`. Kraus: `{sqrt(1-p)·I, sqrt(p)·Y}`.

### `depolarizing(double p)`
Con probabilidad `p` el qubit se reemplaza por el estado máximamente mixto
`I/2` (pierde TODA la información), si no se deja igual. A `p=1`, el qubit
se convierte exactamente en `I/2`, sea cual sea su estado de partida --
el canal "borra" el qubit por completo.

### `amplitudeDamping(double gamma)`
Modela la relajación espontánea `|1>→|0>` (p. ej. emisión espontánea de un
fotón) con probabilidad de decaimiento `gamma`. A diferencia de los otros
4 canales, NO es simétrico bajo `|0><->|1>`: `|0>` es un punto fijo exacto
(nunca cambia), solo `|1>` decae. Esta asimetría es la razón de varios
hallazgos de las clases `Noisy*` (ver `13`/`14`/`15`).

## Experimento guiado: ver la decoherencia en acción

```java
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.DensityMatrix;
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ProbandoDecoherence {
    public static void main(String[] args) {
        MatrixComplex rhoPuro = DensityMatrix.of(Qubits.ket0());

        System.out.println("Sin ruido:\n" + rhoPuro);
        for (double p : new double[] { 0.0, 0.25, 0.5, 0.75, 1.0 }) {
            MatrixComplex rhoRuidoso = Decoherence.apply(rhoPuro, Decoherence.depolarizing(p), 0, 1);
            System.out.println("depolarizing(p=" + p + "):\n" + rhoRuidoso);
        }
        // A p=1.0, rhoRuidoso == [[0.5,0],[0,0.5]] == I/2, sea cual sea el estado inicial

        // amplitudeDamping: |0> NUNCA cambia, |1> siempre decae hacia |0>
        MatrixComplex rhoUno = DensityMatrix.of(Qubits.ket1());
        MatrixComplex tras = Decoherence.apply(rhoUno, Decoherence.amplitudeDamping(1.0), 0, 1);
        System.out.println("|1> tras amplitudeDamping(gamma=1.0):\n" + tras);
        // tras == [[1,0],[0,0]] == |0><0|  -- decaimiento COMPLETO y exacto
    }
}
```

La forma más intuitiva de VER este efecto es con `08_BlochSphere.md` --
graficar la trayectoria de `vector(rho)` según crece el ruido muestra
literalmente el vector encogiéndose desde la superficie de la esfera hacia
su centro.

## Relación con el resto del paquete

Todas las clases `Noisy*` (`12`-`15`) combinan un algoritmo/protocolo de
este paquete con `Decoherence` -- inyectando ruido antes de que corra el
circuito y observando el efecto sobre el resultado final. `BlochSphere`
también depende de esta clase como fuente natural de trayectorias
interesantes que graficar.
