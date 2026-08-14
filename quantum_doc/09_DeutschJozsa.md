# `DeutschJozsa.java` -- el primer algoritmo cuántico con ventaja demostrable

## Qué es

El algoritmo de Deutsch-Jozsa: dada una función booleana de caja negra
`f:{0,1}^n -> {0,1}`, PROMETIDA de antemano a ser CONSTANTE (misma salida
para toda entrada) o EQUILIBRADA (salida `0` para exactamente la mitad de
las entradas, `1` para la otra mitad), decide cuál de las 2 con UNA SOLA
consulta al oráculo -- exponencialmente menos que las `2^(n-1)+1` consultas
que necesitaría, en el peor caso, cualquier algoritmo clásico determinista.

## Para qué sirve / contexto físico

Es un problema artificial (nadie tiene, en la práctica, una función con esa
promesa exacta) pero histórico: fue el primer ejemplo, en 1992, de un
algoritmo cuántico con una ventaja PROBADA (no solo conjeturada) sobre
cualquier algoritmo clásico. El truco central -- "el kickback de fase" -- es
la base de casi todos los algoritmos cuánticos posteriores más útiles,
incluidos `BernsteinVazirani` y (de otra forma) `Grover`.

## Cómo funciona el circuito (intuición)

1. Prepara `n+1` qubits: los primeros `n` (el "registro de entrada") en
   `|0>`, el último (la "ancilla") en `|1>`.
2. Aplica `Hadamard` a los `n+1` qubits -- crea superposición uniforme sobre
   las `2^n` entradas posibles, con la ancilla en `|->`.
3. Aplica el oráculo `U_f: |x>|y> -> |x>|y XOR f(x)>`. Con la ancilla en
   `|->`, esto tiene el efecto de multiplicar `|x>` por la fase `(-1)^f(x)`
   SIN cambiar la ancilla -- el "kickback de fase".
4. Aplica `Hadamard` de nuevo, SOLO al registro de entrada (no a la
   ancilla).
5. Mide el registro de entrada: si sale TODO CEROS con probabilidad `1`,
   `f` era constante; si sale con probabilidad `0`, `f` era equilibrada --
   nunca hay una tercera posibilidad.

## Métodos

### `oracle(IntPredicate f, int n)`
Construye la matriz unitaria `U_f` de `2^(n+1) x 2^(n+1)` directamente de
la tabla de verdad de `f` (sin simular ningún circuito para `f` en sí --
eso es lo que significa "caja negra").

```java
IntPredicate paridad = x -> (Integer.bitCount(x) % 2) == 1; // f(x) = paridad de x, EQUILIBRADA
MatrixComplex uf = DeutschJozsa.oracle(paridad, 3);
```

### `hadamardChain(int count)` -- paquete-visible
`H⊗H⊗...⊗H`, `count` copias -- el "aplica Hadamard a todo este bloque" que
el circuito necesita 2 veces (registro completo, luego solo la entrada).
No se usa directamente desde fuera del paquete, pero conviene saber que
existe: la reusan `BernsteinVazirani` y `Grover`.

### `runCircuit(IntPredicate f, int n)` -- paquete-visible
El circuito completo (los pasos 1-4 de arriba), devuelve el estado final
antes de medir. Tampoco pública, pero es la pieza que `BernsteinVazirani`
reutiliza literalmente (mismo circuito, distinta `f`, distinta forma de
leer el resultado).

### `probabilityAllZero(IntPredicate f, int n)`
La probabilidad de medir "todo ceros" en el registro de entrada -- el
resultado del paso 5. Exactamente `1.0` si `f` es constante, exactamente
`0.0` si es equilibrada (a precisión `double`, no solo "aproximadamente").

```java
double p = DeutschJozsa.probabilityAllZero(paridad, 3);
// p == 0.0  -- paridad(x) es equilibrada
```

### `isConstant(IntPredicate f, int n)`
El veredicto final, con una sola llamada al oráculo por debajo. Lanza
excepción si `probabilityAllZero()` no sale ni `~0` ni `~1` -- señal de que
`f` no cumplía la promesa constante-o-equilibrada.

```java
boolean esConstante = DeutschJozsa.isConstant(paridad, 3);
// esConstante == false
```

## Experimento guiado: comprobar el "no hay término medio"

```java
import com.ipserc.arith.quantum.DeutschJozsa;
import java.util.function.IntPredicate;

public class ProbandoDeutschJozsa {
    public static void main(String[] args) {
        int n = 4;
        IntPredicate constante0 = x -> false;
        IntPredicate constante1 = x -> true;
        IntPredicate masSignificativo = x -> ((x >> (n - 1)) & 1) == 1; // equilibrada

        for (IntPredicate f : new IntPredicate[] { constante0, constante1, masSignificativo }) {
            double p = DeutschJozsa.probabilityAllZero(f, n);
            boolean esConstante = DeutschJozsa.isConstant(f, n);
            System.out.println("P(todo-ceros)=" + p + "  esConstante=" + esConstante);
        }
        // Las 2 primeras: p=1.0, esConstante=true
        // La tercera: p=0.0, esConstante=false
        // NUNCA un valor intermedio -- es la garantia matematica del algoritmo
    }
}
```

Prueba a construir tu propia `f` que NO cumpla la promesa (p. ej.
`x -> x == 0`, verdadera solo para 1 entrada de las `2^n`) y observa que
`isConstant()` lanza `IllegalStateException` -- el algoritmo detecta que
se ha violado su premisa en vez de dar un resultado silenciosamente
incorrecto.

## Relación con el resto del paquete

Usa `Qubits` (kets, Hadamard) directamente. `BernsteinVazirani`
(`10_BernsteinVazirani.md`) reusa `hadamardChain()`/`runCircuit()` sin
duplicarlos -- mismo circuito exacto, distinta forma de leer el resultado
final. `NoisyDeutschJozsa` (`13_NoisyDeutschJozsa.md`) combina esta clase
con `Decoherence`.
