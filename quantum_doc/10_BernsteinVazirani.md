# `BernsteinVazirani.java` -- recuperar un secreto oculto con 1 sola consulta

## Qué es

El algoritmo de Bernstein-Vazirani: dada una función de caja negra
`f(x) = (a·x) mod 2` (el producto escalar bit a bit de la entrada `x` con
un secreto OCULTO de `n` bits `a`), recupera el secreto `a` COMPLETO con
UNA SOLA consulta al oráculo -- frente a las `n` consultas clásicas que
harían falta en el peor caso (probar cada bit de `a` por separado con
`f(2^i)`).

## Para qué sirve / contexto físico

Es, estructuralmente, el MISMO circuito que `DeutschJozsa` -- misma
preparación, mismo `H⊗(n+1)`-oráculo-`H⊗n` -- pero con una `f` distinta y
una forma distinta de leer el resultado final: Deutsch-Jozsa solo lee 1 bit
de información (¿la amplitud de "todo ceros" es `~1` o `~0`?),
Bernstein-Vazirani lee el registro de entrada COMPLETO -- el estado final
colapsa determinísticamente a `|a>`, no solo a "distinto de `|0>`".

Es un buen ejemplo para entender que la MISMA maquinaria de circuito puede
extraer distinta información según cómo se interprete la salida.

## Métodos

### `oracleFunction(int secret, int n)`
Construye la función `f(x) = (secret·x) mod 2` para un secreto de `n` bits
dado -- la `f` concreta que este algoritmo está diseñado para resolver.

```java
IntPredicate f = BernsteinVazirani.oracleFunction(5, 3); // secreto = 5 = 101 en binario
```

### `findSecret(IntPredicate f, int n)`
Recupera el secreto oculto con una sola consulta: ejecuta
`DeutschJozsa.runCircuit(f, n)` (el mismo circuito, reusado sin duplicar),
y lee cuál de los `2^n` estados base del registro de entrada tiene
probabilidad `~1` (marginando la ancilla) -- ese estado ES el secreto.

```java
int secretoRecuperado = BernsteinVazirani.findSecret(f, 3);
// secretoRecuperado == 5, con UNA sola consulta al oraculo
```

Lanza excepción si el estado final no colapsa a un único resultado
determinista -- señal de que `f` no era realmente de la forma lineal
`(a·x) mod 2` que este algoritmo asume.

## Un hallazgo real de la verificación (no un bug)

Una función CONSTANTE (`f(x)=0` para toda `x`, o `f(x)=1` para toda `x`)
colapsa exactamente al MISMO resultado que "secreto=0": el circuito NO
puede distinguir una `f` constante-0 de una `f` lineal con secreto `0` --
una `f` constante solo aporta una fase global, que no perturba en absoluto
el patrón de interferencia. No es un fallo del algoritmo: simplemente esas
2 situaciones son indistinguibles con este circuito. Puedes comprobarlo tú
mismo:

```java
IntPredicate constante1 = x -> true;
int resultado = BernsteinVazirani.findSecret(constante1, 3);
// resultado == 0 -- el circuito "cree" que el secreto es 0
```

## Experimento guiado: recuperar todos los secretos posibles

```java
import com.ipserc.arith.quantum.BernsteinVazirani;
import java.util.function.IntPredicate;

public class ProbandoBernsteinVazirani {
    public static void main(String[] args) {
        int n = 4;
        for (int secreto = 0; secreto < (1 << n); ++secreto) {
            IntPredicate f = BernsteinVazirani.oracleFunction(secreto, n);
            int recuperado = BernsteinVazirani.findSecret(f, n);
            System.out.println("secreto=" + secreto + "  recuperado=" + recuperado
                + (secreto == recuperado ? "  OK" : "  MISMATCH"));
        }
        // Los 16 secretos (n=4) se recuperan EXACTOS, cada uno con 1 sola consulta
    }
}
```

## Relación con el resto del paquete

Reusa `DeutschJozsa.runCircuit()` verbatim -- no reconstruye el circuito.
`NoisyBernsteinVazirani` (`14_NoisyBernsteinVazirani.md`) combina esta
clase con `Decoherence`, reusando a su vez la maquinaria de
`NoisyDeutschJozsa` de la misma forma (mismo circuito ruidoso, distinta
`f`, distinta lectura del resultado).
