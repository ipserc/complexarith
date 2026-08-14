# `Qubits.java` -- el vocabulario básico

## Qué es

Una clase de solo métodos estáticos (no se instancia, `Qubits()` es privado)
que fabrica los objetos elementales que todo lo demás del paquete usa:
kets de la base computacional, las puertas/operadores de 1 qubit más
comunes (Pauli, Hadamard), estados entrelazados canónicos (Bell, GHZ), y 2
herramientas de "álgebra de registros" (`operatorOnQubit`, `controlledGate`)
que permiten aplicar un gate de 1 o 2 qubits a una posición concreta de un
registro de `n` qubits sin tener que construir la matriz `2^n x 2^n`
entera a mano cada vez.

Todo son simplemente objetos `MatrixComplex` -- no hay ningún tipo nuevo
"Qubit" en el proyecto. Un ket es un `MatrixComplex` columna, un operador es
un `MatrixComplex` cuadrado.

## Para qué sirve / cuándo usarla

Es la clase que se importa casi siempre que se trabaja con este paquete --
todas las demás clases construyen sobre su vocabulario. Empieza aquí
cualquier experimento nuevo.

## Métodos

### `ket0()` / `ket1()`
Los estados base `|0>` y `|1>`, como `MatrixComplex` `2x1`.
```java
MatrixComplex zero = Qubits.ket0(); // [1; 0]
MatrixComplex one = Qubits.ket1();  // [0; 1]
```

### `bra(MatrixComplex ket)`
El bra `<psi|` correspondiente a un ket `|psi>` -- un envoltorio de 1 línea
sobre `ket.adjoint()` (traspuesta conjugada), que ya existía en
`MatrixComplex` de forma genérica. No es un tipo de objeto nuevo: en este
proyecto un bra sigue siendo un `MatrixComplex`, ahora fila en vez de
columna. Existe solo para que un punto del código que significa "el bra de
este ket" lo diga explícitamente, en vez del `.adjoint()` genérico -- que
se lee igual tanto si el operando es un ket (donde "bra" es la lectura
físicamente correcta) como si es un operador (donde `.adjoint()` es su
Hermítico conjugado/dagger, un concepto DISTINTO: `U.adjoint()` no es "el
bra de U"). Úsalo solo sobre kets; para operadores sigue usando
`operador.adjoint()` directamente.

```java
MatrixComplex psi = Qubits.ket0();
MatrixComplex proyector = psi.times(Qubits.bra(psi)); // |0><0|
```

### `identity2()`
La matriz identidad `2x2` -- "no hacer nada" a un qubit.

### `pauliX()`, `pauliY()`, `pauliZ()`
Las 3 matrices de Pauli, los operadores/puertas de 1 qubit más importantes
de toda la mecánica cuántica:
- `pauliX()` = `[[0,1],[1,0]]` -- el "NOT cuántico": intercambia `|0>` y
  `|1>`.
- `pauliY()` = `[[0,-i],[i,0]]`.
- `pauliZ()` = `[[1,0],[0,-1]]` -- deja `|0>` igual, invierte el signo de
  `|1>` (un "flip de fase").

Las 3 son a la vez unitarias Y Hermíticas -- sirven tanto como puerta
(`pauliX().times(psi)`) como como medida (`<psi|pauliZ()|psi>`).

### `hadamard()`
`(1/sqrt2)·[[1,1],[1,-1]]` -- la puerta que crea superposición:
`H|0> = |+> = (|0>+|1>)/sqrt2`, `H|1> = |-> = (|0>-|1>)/sqrt2`. Es la puerta
más usada de todo el paquete (aparece en `DeutschJozsa`, `BernsteinVazirani`,
`Grover`, `Teleportation`...).

```java
MatrixComplex plus = Qubits.hadamard().times(Qubits.ket0());
// plus = [0.7071; 0.7071]  -- superposición equitativa
```

### `spinOperator(double theta)`
`A(theta) = cos(theta)·Z + sin(theta)·X` -- una familia de medidas de
espín/polarización, parametrizada por un ángulo. Es el operador de medida
que usa `BellTest` para el experimento CHSH (medir en distintas bases
girando `theta`).

### `bellPhiPlus()`
El estado de Bell `|Phi+> = (|00>+|11>)/sqrt(2)`, el par entrelazado
canónico de 2 qubits.
```java
MatrixComplex bell = Qubits.bellPhiPlus(); // 4x1: [0.7071; 0; 0; 0.7071]
```

### `ket(int... bits)`
El ket de un registro de `n` qubits en la base computacional, a partir de
una cadena de bits clásicos -- p. ej. `Qubits.ket(1,0,1)` es `|101>`, un
vector `8x1` con un único `1` en la fila correspondiente. El primer bit del
array es el qubit 0 (MSB), ver `00_introduccion.md`.
```java
MatrixComplex state = Qubits.ket(1, 0, 1); // |101>, 8x1
```

### `ghz(int n)`
El estado GHZ de `n` qubits, `(|00...0>+|11...1>)/sqrt2` -- la
generalización de `bellPhiPlus()` a más de 2 qubits, igual de entrelazado
(los `n` qubits colapsan todos al mismo valor al medir, sea `0` o `1`).
Requiere `n>=2`.

### `operatorOnQubit(MatrixComplex op, int qubitIndex, int nQubits)`
La herramienta de "elevación" más usada del paquete: toma un operador de 1
qubit (`op`, `2x2`) y lo convierte en un operador `2^nQubits x 2^nQubits`
que actúa SOLO sobre `qubitIndex`, dejando el resto del registro
inalterado (identidad implícita) -- `I (x) ... (x) op (x) ... (x) I`.

```java
// Aplica pauliZ() al qubit 1 de un registro de 3 qubits, sin tocar los otros 2
MatrixComplex zOnQubit1 = Qubits.operatorOnQubit(Qubits.pauliZ(), 1, 3);
MatrixComplex result = zOnQubit1.times(Qubits.ket(0, 1, 0));
```

### `controlledGate(MatrixComplex op, int controlIndex, int targetIndex, int nQubits)`
Construye una puerta controlada de 2 qubits (p. ej. el `CNOT`, la puerta
entrelazadora por excelencia): aplica `op` al qubit `targetIndex` SOLO si el
qubit `controlIndex` está en `|1>`. `controlIndex`/`targetIndex` no
necesitan ser adyacentes.
```java
// CNOT con control=qubit0, target=qubit1, en un registro de 3 qubits
MatrixComplex cnot = Qubits.controlledGate(Qubits.pauliX(), 0, 1, 3);
```

## Experimento mínimo para probar esta clase

```java
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ProbandoQubits {
    public static void main(String[] args) {
        // 1. Crea una superposición y comprueba las probabilidades (regla de Born)
        MatrixComplex plus = Qubits.hadamard().times(Qubits.ket0());
        double p0 = Math.pow(plus.getItem(0, 0).mod(), 2);
        double p1 = Math.pow(plus.getItem(1, 0).mod(), 2);
        System.out.println("P(0)=" + p0 + "  P(1)=" + p1); // ambas 0.5

        // 2. Construye un CNOT y comprueba que entrelaza |+>|0> -> Bell
        MatrixComplex input = plus.kroneckerprod(Qubits.ket0()); // |+>|0>
        MatrixComplex cnot = Qubits.controlledGate(Qubits.pauliX(), 0, 1, 2);
        MatrixComplex output = cnot.times(input);
        System.out.println(output); // debería coincidir con Qubits.bellPhiPlus()
    }
}
```

Este es, de hecho, el circuito estándar de preparación de un par de Bell:
`H` en el primer qubit, luego `CNOT` control=primero, target=segundo.

## Relación con el resto del paquete

Prácticamente toda otra clase de `com.ipserc.arith.quantum` importa
`Qubits`. Sigue con `03_BellTest.md` (el primer experimento completo que
usa este vocabulario) o `04_DensityMatrix.md`.
