# `TimeEvolution.java` -- evolución temporal unitaria

## Qué es

La implementación directa de la ecuación de Schrödinger para un
Hamiltoniano `H` que no cambia con el tiempo: `|psi(t)> = U(t)|psi(0)>`,
con `U(t) = exp(-i·H·t)` (convención `hbar=1`, estándar en computación
cuántica). Es la clase más corta del paquete (3 métodos), pero conceptualmente
central: es "cómo evoluciona un sistema cuántico aislado en el tiempo".

## Para qué sirve / contexto físico

`H` representa la energía del sistema. La fórmula `U(t)=exp(-i·H·t)` sale
de resolver la ecuación diferencial `i·d|psi>/dt = H|psi>` para `H`
constante -- exactamente igual que resolver `dx/dt = a·x` da
`x(t)=exp(a·t)·x(0)`, pero aquí con matrices y con un factor `-i` que
garantiza que `U(t)` sea unitaria en vez de crecer o decaer sin control (la
exponencial de una matriz anti-Hermítica -- que es lo que es `-i·H·t` para
`H` Hermítica -- es siempre unitaria).

Esta clase se apoya en `MatrixComplex.exp()`, la exponenciación de
matrices, que ya existía en el proyecto antes de este paquete (de otro
contexto, mucho antes del "Rol Física").

## Métodos

### `unitary(MatrixComplex hamiltonian, double t)`
El operador de evolución `U(t) = exp(-i·H·t)`. Comprueba que `hamiltonian`
sea Hermítica (`hamiltonian.isHermitian()`) y lanza excepción si no --
un Hamiltoniano no Hermítico no generaría una evolución unitaria
(perdería probabilidad, algo físicamente imposible para un sistema
aislado).

```java
MatrixComplex u = TimeEvolution.unitary(Qubits.pauliX(), Math.PI / 2);
```

### `evolve(MatrixComplex state, MatrixComplex hamiltonian, double t)`
Atajo: `unitary(hamiltonian, t).times(state)` -- el estado evolucionado
directamente.

```java
MatrixComplex evolved = TimeEvolution.evolve(Qubits.ket0(), Qubits.pauliX(), Math.PI / 2);
```

### `expectationValue(MatrixComplex state, MatrixComplex op)`
El valor esperado `<state|op|state>` de un observable Hermítico -- la
fórmula genérica que `BellTest.correlation()` reusa internamente para el
caso concreto de un observable producto tensorial de 2 medidas locales.
Lanza excepción si el resultado sale con parte imaginaria no despreciable
(señal de que `op` no era Hermítica).

```java
double expZ = TimeEvolution.expectationValue(Qubits.ket0(), Qubits.pauliZ());
// expZ == 1.0  -- |0> es autoestado de Z con autovalor +1
```

## Experimento guiado: precesión de espín (rotación de Rabi)

Un ejemplo físico clásico de manual: un qubit inicialmente en `|0>`, bajo
el Hamiltoniano `H = pauliX()`, "rota" entre `|0>` y `|1>` con el tiempo --
la probabilidad de medir `1` oscila como `sin^2(t)`.

```java
import com.ipserc.arith.quantum.TimeEvolution;
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ProbandoTimeEvolution {
    public static void main(String[] args) {
        MatrixComplex h = Qubits.pauliX();
        MatrixComplex psi0 = Qubits.ket0();

        for (double t = 0.0; t <= Math.PI; t += Math.PI / 8) {
            MatrixComplex psiT = TimeEvolution.evolve(psi0, h, t);
            double p1 = Math.pow(psiT.getItem(1, 0).mod(), 2);
            double p1Teorico = Math.pow(Math.sin(t), 2);
            System.out.printf("t=%.3f  P(1)=%.6f  sin^2(t)=%.6f%n", t, p1, p1Teorico);
        }
        // En t=pi/2, P(1) llega a 1.0 -- el qubit ha "girado" completamente a |1>
        // En t=pi, P(1) vuelve a 0.0 -- ha completado un ciclo entero
    }
}
```

## Un bug real que se encontró usando esta clase

Al construir `TimeEvolution` (sesión Trigesimoséptima), se usó
`Diagfactor.diagonalize()` (una clase de diagonalización que ya existía en
el proyecto, de fuera de este paquete) como una forma alternativa de
calcular `exp(-i·H·t)` para verificar `MatrixComplex.exp()` de forma
cruzada. Esa verificación DESTAPÓ un bug real y preexistente en
`Diagfactor`: para autovalores con la misma parte real (pares
complejo-conjugados, o `+i`/`-i` como en `pauliX()`/`pauliY()`), `D` y `P`
podían quedar en orden distinto, de forma que `P·D·P⁻¹` no reconstruía la
matriz original en absoluto. Justo el tipo de matriz típico en mecánica
cuántica -- por eso no se había detectado en auditorías anteriores del
proyecto, ninguna de las cuales tocaba mecánica cuántica.

Moraleja para tus propios experimentos: si alguna vez comparas 2 caminos de
cálculo distintos para verificar algo (una buena práctica, muy recomendable),
y no coinciden, no asumas automáticamente que el camino nuevo está mal --
puede que el camino "de confianza" tenga un bug que nadie había visto
porque nadie lo había puesto a prueba con este tipo de matrices.

## Relación con el resto del paquete

`BellTest.correlation()` reusa `expectationValue()` directamente (desde la
Trigesimoséptima sesión, antes tenía su propia copia del cálculo).
`BlochSphere` menciona `evolve()` como ejemplo de trayectoria que se queda
siempre en la superficie de la esfera (a diferencia de `Decoherence`, que
la encoge hacia el centro).
