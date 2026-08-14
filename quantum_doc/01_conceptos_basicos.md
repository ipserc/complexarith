# Conceptos básicos de mecánica cuántica, para el código de este paquete

Este documento no es un curso de mecánica cuántica -- es el mínimo
imprescindible para leer el código de `com.ipserc.arith.quantum` y entender
qué representa cada línea. Cada concepto se explica ligado directamente a su
representación en Java (con `MatrixComplex`, la clase de matrices complejas
que ya existía en el proyecto).

## 1. Un qubit es un vector columna complejo de 2 componentes

En un ordenador clásico, un bit vale `0` o `1`. En un ordenador cuántico, un
**qubit** puede estar en una **superposición** de ambos: un vector

```
|psi> = alpha·|0> + beta·|1>,   con |alpha|^2 + |beta|^2 = 1
```

donde `alpha`, `beta` son números complejos, y `|0>`, `|1>` son los vectores
de la base "computacional" -- el análogo cuántico de "el bit vale 0" / "el
bit vale 1". En este proyecto:

- `|0>` es `Qubits.ket0()`: un `MatrixComplex` de `2x1` con `1` en la fila 0.
- `|1>` es `Qubits.ket1()`: un `MatrixComplex` de `2x1` con `1` en la fila 1.
- Un `|psi>` genérico es simplemente cualquier `MatrixComplex` `2x1` cuya
  norma (euclídea, sobre los módulos de sus 2 componentes complejas) valga 1.

La notación `|...>` se llama **notación de Dirac**, y `|...>` en sí se llama
un **ket**. No es más que la notación estándar de la física para "vector
columna". Si has trabajado con vectores de estado o con la representación
fasorial de señales, la idea de "vector complejo normalizado" ya te resulta
familiar -- lo nuevo es la interpretación física de sus componentes.

## 2. El cuadrado del módulo de una amplitud es una probabilidad (regla de Born)

Si mides un qubit `|psi> = alpha·|0> + beta·|1>` en la base computacional,
obtienes `0` con probabilidad `|alpha|^2` y `1` con probabilidad `|beta|^2`.
Esta es la **regla de Born**, y es la única forma en que la mecánica
cuántica te deja "leer" información de un estado -- no puedes leer `alpha` y
`beta` directamente, solo obtener una muestra aleatoria gobernada por sus
módulos al cuadrado.

En el código, esto aparece una y otra vez como `amplitud.mod()` seguido de
elevar al cuadrado -- por ejemplo en `Teleportation.probabilityOfOutcome()`
o `Grover.probabilityOfTarget()`.

## 3. Un operador cuántico es una matriz; una medida física es una matriz Hermítica

Cualquier transformación reversible de un estado cuántico (una "puerta") se
representa como una matriz **unitaria** `U` (aquella que cumple `U·U† = I`,
donde `U†` es la traspuesta conjugada -- `adjoint()` en este proyecto):
`|psi'> = U·|psi>`. Al ser unitaria, preserva la norma -- un estado
normalizado sigue normalizado tras aplicar `U`.

Una **magnitud física medible** (un "observable") se representa como una
matriz **Hermítica** `A` (aquella que cumple `A = A†`). Su valor esperado
sobre un estado es `<psi|A|psi>` (siempre un número real, nunca complejo --
si te sale con parte imaginaria significativa, es una señal de que `A` no
era realmente Hermítica; varias clases de este paquete comprueban esto
explícitamente y lanzan una excepción si falla). `<psi|` es el **bra**, el
vector fila conjugado de `|psi>` -- `Qubits.bra(psi)` en código (un
envoltorio de 1 línea sobre `psi.adjoint()`, que sigue siendo válido y es
lo que hay que usar sobre un OPERADOR, no un ket -- `Qubits.bra()` solo
tiene sentido semántico sobre kets, ver `02_Qubits.md`).

Ejemplos en el código: `Qubits.hadamard()` es unitaria (una puerta),
`Qubits.pauliZ()` es Hermítica (una medida), `Qubits.spinOperator(theta)` es
Hermítica (una familia de medidas parametrizada por un ángulo).

## 4. El producto tensorial combina varios qubits en un registro

Un sistema de `n` qubits vive en un espacio de `2^n` dimensiones -- el
producto tensorial (Kronecker) de los `n` espacios individuales de 2
dimensiones cada uno. En este proyecto, `MatrixComplex.kroneckerprod()` ya
existía antes de este paquete (de otro contexto del proyecto) y es
exactamente la herramienta que hace falta aquí. `Qubits.ket(b1,...,bn)`
encadena `n` kets de 1 qubit con `kroneckerprod()` para construir el estado
de un registro completo de `n` qubits, de dimensión `2^n`.

Este crecimiento exponencial (`2^n`) es la razón física de por qué un
ordenador cuántico puede ser más potente que uno clásico para ciertos
problemas -- y también la razón puramente práctica de por qué las
simulaciones de este paquete se vuelven lentas para `n` grande (`Grover`
con `n=8`, por ejemplo, ya maneja matrices `256x256`; ver la nota de
rendimiento en `11_Grover.md`).

## 5. Entrelazamiento (entanglement): un estado que no se separa en piezas

Un estado de 2 qubits es **separable** si se puede escribir como
`|psi_A> (x) |psi_B>` (el estado de A "por su cuenta" tensor el de B "por su
cuenta"). El **estado de Bell** `|Phi+> = (|00>+|11>)/sqrt(2)`
(`Qubits.bellPhiPlus()`) NO se puede escribir así -- está **entrelazado**:
medir un qubit determina instantáneamente el resultado del otro, sin
importar la distancia entre ambos, aunque ninguno de los 2 qubits "tiene"
por separado un estado bien definido antes de medir.

Este es el fenómeno que `03_BellTest.md` demuestra experimentalmente que NO
se puede explicar con ninguna teoría clásica de "variables ocultas
locales" (la desigualdad de Bell/CHSH), y el recurso que
`06_Teleportation.md` consume para transmitir un qubit sin moverlo
físicamente.

## 6. Estados mixtos y la matriz densidad

Todo lo anterior describe un **estado puro** -- un `|psi>` conocido con
certeza. Pero a veces solo sabes que el sistema está en `|psi_1>` con
probabilidad `p_1`, en `|psi_2>` con probabilidad `p_2`, etc. (por ejemplo,
tras la interacción con un entorno ruidoso no observado). Esto es un
**estado mixto**, y ya no se puede representar como un único vector -- hace
falta la **matriz densidad** `rho = sum_i p_i |psi_i><psi_i|`.

- Un estado puro tiene `rho = |psi><psi|` (`DensityMatrix.of(psi)`), una
  matriz de **rango 1**.
- La **traza parcial** (`DensityMatrix.partialTrace()`) obtiene el estado
  "efectivo" de un subsistema, ignorando el resto -- y es la herramienta
  clave para ver que un subsistema de un estado entrelazado (aunque el
  estado GLOBAL sea puro) resulta MIXTO cuando lo miras aislado. Esto es,
  de hecho, la definición operacional de entrelazamiento que usa este
  paquete (`DensityMatrix.vonNeumannEntropy()` sobre la traza parcial).
- Un canal de **ruido/decoherencia** (`Decoherence`) es precisamente lo que
  convierte un estado puro en uno genuinamente mixto -- por eso varias
  clases "Noisy*" de este paquete trabajan con matrices densidad en vez de
  con vectores de estado.

Ver `04_DensityMatrix.md` y `07_Decoherence.md` para el desarrollo completo.

## 7. Operadores de Kraus: cómo se modela el ruido sin simular el entorno

Un canal de ruido transforma `rho -> rho' = sum_k E_k·rho·E_k†`, donde los
`E_k` (**operadores de Kraus**) satisfacen `sum_k E_k†·E_k = I` (la
condición que garantiza que `rho'` sigue siendo una matriz densidad válida,
de traza 1). Físicamente, esto modela "el qubit interactuó con un entorno
que no estamos simulando explícitamente, y esa interacción, promediada
sobre lo que no sabemos del entorno, tiene este efecto neto sobre `rho`".

`Decoherence.java` (`07_Decoherence.md`) implementa 5 familias de canales de
1 qubit como sus operadores de Kraus, y `Decoherence.apply()` es literalmente
la fórmula de arriba.

## 8. Evolución temporal: la ecuación de Schrödinger

Si un sistema tiene una energía descrita por un operador Hermítico `H` (el
**Hamiltoniano**) y no interactúa con nada más, su estado evoluciona según
`|psi(t)> = U(t)|psi(0)>`, con `U(t) = exp(-i·H·t)` (convención `hbar=1`).
`TimeEvolution.unitary()` es exactamente esta fórmula, apoyada en
`MatrixComplex.exp()` (la exponencial de una matriz, ya implementada en
otro punto del proyecto antes de este paquete).

## 9. Oráculos y algoritmos cuánticos "de juguete"

Varios algoritmos de este paquete (`DeutschJozsa`, `BernsteinVazirani`,
`Grover`) usan un **oráculo**: una caja negra unitaria `U_f` que, dado un
estado de entrada codificando `x`, produce una salida que depende de una
función clásica `f(x)` -- sin que el algoritmo "vea" cómo está implementada
`f` por dentro. El objetivo de estos algoritmos es extraer información
GLOBAL sobre `f` (¿es constante?, ¿cuál es su secreto?, ¿qué entrada la hace
verdadera?) con MENOS consultas al oráculo de las que necesitaría cualquier
algoritmo clásico -- la "ventaja cuántica" que demuestran.

## Resumen -- vocabulario mínimo para seguir el resto de los documentos

| Término | En este código |
|---|---|
| Qubit / ket `\|psi>` | `MatrixComplex` columna `2^n x 1`, norma 1 |
| Bra `<psi\|` | `Qubits.bra(psi)` (envoltorio de `psi.adjoint()`) |
| Superposición | Combinación lineal de kets de la base computacional |
| Medida (regla de Born) | `amplitud.mod()` al cuadrado = probabilidad |
| Puerta / evolución unitaria | Matriz `U` con `U·U†=I`; `U.times(psi)` |
| Observable / medida física | Matriz Hermítica `A` con `A=A†`; valor esperado `<psi\|A\|psi>` |
| Producto tensorial (varios qubits) | `MatrixComplex.kroneckerprod()` |
| Entrelazamiento | Estado no separable, p. ej. `Qubits.bellPhiPlus()` |
| Estado mixto / matriz densidad | `rho`, `DensityMatrix.of(psi)` |
| Traza parcial | `DensityMatrix.partialTrace()` |
| Canal de ruido / operadores de Kraus | `Decoherence.apply()` |
| Hamiltoniano / evolución temporal | `TimeEvolution.unitary(H,t)` |
| Oráculo | Matriz de permutación construida desde una `f` clásica |

Con esto ya puedes pasar a `02_Qubits.md`.
