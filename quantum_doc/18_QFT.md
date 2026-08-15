# `QFT.java` -- la Transformada Cuántica de Fourier

## Qué es

La Transformada Cuántica de Fourier (QFT): la unitaria de `n` qubits

```
QFT|j> = (1/sqrt(2^n)) * sum_{k=0}^{2^n-1} e^(2*pi*i*j*k / 2^n) |k>
```

el análogo cuántico exacto de la DFT clásica (`com.ipserc.arith.signal.Fourier`)
sobre la base computacional -- la misma fórmula, solo que actuando sobre
amplitudes de probabilidad de un registro de qubits en vez de sobre muestras
de una señal real.

## Para qué sirve / contexto físico

Es la pieza central de casi todos los algoritmos cuánticos con ventaja
exponencial conocidos: la factorización de Shor, la estimación de fase
cuántica (usada en química cuántica para calcular energías moleculares), y
varios algoritmos de resolución de sistemas lineales. Su interés no es solo
"calcular una DFT" (para eso un ordenador clásico ya es rapidísimo) --
sino que, al vivir dentro de una superposición cuántica, permite extraer
información de PERIODICIDAD de una función codificada en el estado sin
tener que evaluarla clásicamente en todos sus puntos, el ingrediente que
hace posible la ventaja exponencial de Shor.

Como en `DeutschJozsa`/`Grover`, en este paquete se implementa por
simulación numérica clásica de las puertas -- no hay ninguna ventaja de
rendimiento real aquí (al contrario, `matrix(n)` clásica es más barata que
simular el circuito cuántico), el valor es puramente de aprendizaje: ver
CÓMO se construye la unitaria a partir de puertas elementales.

## Cómo funciona el circuito (intuición)

Para cada qubit `j` (de `0` a `n-1`, en ese orden):

1. `Hadamard` sobre el qubit `j`.
2. Para cada qubit `k>j` que queda por delante: una fase controlada `R_(k-j+1)`
   (`control=k`, `target=j`) -- introduce el acoplamiento entre qubits que
   distingue la QFT de una superposición ingenua (`H` en cada qubit por
   separado, sin más, daría una unitaria válida pero que NO es la QFT).

Al terminar el bucle, un bloque de `SWAP` invierte el orden de los qubits
(intercambia el qubit `i` con el `n-1-i`, para `i<n/2`; el qubit central de
un `n` impar queda fijo). Es necesario porque el circuito, tal y como está
construido, calcula los bits del resultado en orden inverso al que espera
la fórmula -- un hecho estándar de la QFT (Nielsen & Chuang), no un arreglo
adicional discrecional.

### El caso trivial, `n=1`

Con un solo qubit no hay ningún otro qubit `k>j` sobre el que iterar, así
que el bucle de fases controladas queda vacío y el `SWAP` no tiene qué
invertir (`i<n/2` con `n=1` nunca se cumple). Lo único que queda es
`Hadamard` -- es decir, `QFT` de 1 qubit `== Qubits.hadamard()` exactamente.
Es el caso base con el que conviene empezar a razonar el algoritmo.

### El caso `n=2` a mano

- `H` sobre `q0`.
- fase controlada `R_2` (control=`q1`, target=`q0`).
- `H` sobre `q1`.
- `SWAP(q0,q1)`.

### El caso `n=3` a mano

- `j=0`: `H(q0)`; `R_2` (control=`q1`, target=`q0`); `R_3` (control=`q2`, target=`q0`).
- `j=1`: `H(q1)`; `R_2` (control=`q2`, target=`q1`).
- `j=2`: `H(q2)` -- sin fase controlada detrás, último qubit.
- `SWAP(q0,q2)` -- solo ese par, `q1` (el del medio) queda fijo.

Nótese que el índice de la fase controlada NO es "el número total de
qubits" -- es `R_(k-j+1)`, que depende de la distancia entre el qubit
target y el control. Para `n=2` coincide con `N` por pura casualidad (el
único término, `j=0,k=1`, da `R_2==R_N`); con `n=3` ya no coincide en
absoluto (`j=1,k=2` da `R_2`, no `R_3`).

## Métodos

### `circuit(int n)`
La QFT construida con puertas elementales (`Qubits.hadamard()`, fase
controlada `Qubits.phaseGate(k-j+1)`, `SWAP` montado con 3
`controlledGate(pauliX(),...)` encadenados) -- exactamente como se
implementaría en un ordenador cuántico real, con `O(n^2)` puertas.

```java
MatrixComplex u = QFT.circuit(3); // 8x8, unitaria
```

### `matrix(int n)`
La fórmula literal, construida entrada a entrada (`O(2^(2n))`, sin ninguna
puerta involucrada). Se usa únicamente como oráculo de verificación
independiente contra el que comprobar `circuit(n)` -- NO es la forma
práctica de calcular la QFT, es "el solucionario".

```java
MatrixComplex m = QFT.matrix(3);
boolean coincide = u.equals(m, 10); // true
```

Ambos métodos rechazan `n<1` con `IllegalArgumentException`.

## Experimento guiado: comprobar el caso base y la superposición uniforme

```java
import com.ipserc.arith.quantum.QFT;
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ProbandoQFT {
    public static void main(String[] args) {
        // Caso base: QFT de 1 qubit == Hadamard
        System.out.println(QFT.circuit(1).equals(Qubits.hadamard(), 10));
        // true

        // QFT aplicada a |0...0> da la superposición equitativa, fase 0 en cada término
        int n = 3;
        MatrixComplex ceroACero = Qubits.ket(new int[n]);
        MatrixComplex salida = QFT.circuit(n).times(ceroACero);
        salida.println("QFT|000>"); // 8 amplitudes, todas 1/sqrt(8), parte imaginaria 0
    }
}
```

Prueba a comparar `circuit(n)` contra `matrix(n)` para varios `n`, y a
aplicar `circuit(n)` sobre distintos kets de base (`Qubits.ket(...)`) para
ver cómo cada uno se transforma en una superposición con fases distintas --
la "huella" de periodicidad que la QFT extrae.

## Relación con el resto del paquete

Usa `Qubits` (`hadamard()`, `phaseGate(k)`, `controlledGate()`,
`operatorOnQubit()`, `ket()`) directamente, sin depender de ninguna otra
clase del paquete. Conceptualmente es el puente más directo con la
formación de tratamiento de señal (`com.ipserc.arith.signal.Fourier`) de
todo el paquete `quantum` -- ver `17_limites_y_extensiones.md` para la
discusión de qué otros grados de libertad podría representar este paquete
en el futuro. Candidatos naturales de continuación, sin desarrollar
todavía: comparar `circuit(n)` con `Fourier.DFT()` sobre una señal
codificada como estado, y la Estimación de Fase Cuántica (QPE), que usa la
QFT inversa (`circuit(n).adjoint()`, unitaria por construcción, aunque
todavía no verificada explícitamente como QFT inversa en este paquete)
junto con un control-`U` elevado a potencias.
