# Límites del modelo: ¿qué partículas se pueden representar?

Este documento no describe una clase Java concreta -- es una nota conceptual,
a partir de una pregunta directa: **¿se puede representar cualquier partícula
fundamental, fermión o bosón, con las clases de este paquete?** La respuesta
corta es "depende de qué parte de la partícula", y merece quedar anotada
porque marca la frontera exacta de lo que `com.ipserc.arith.quantum`
modela hoy -- y por qué, no solo que sí o que no.

## 1. Lo que el formalismo YA cubre, y de sobra

`MatrixComplex` no está limitado a 2 dimensiones -- `Qubits.java` solo trae
fábricas de conveniencia para el caso 2D (`ket0()`/`ket1()`), pero un vector
columna `n x 1` con operadores hermíticos `n x n` es exactamente el espacio
de Hilbert de **cualquier sistema con un número finito de estados internos
discretos**. Eso cubre el grado de libertad de espín de cualquier partícula,
sea cual sea su dimensión:

- espín 1/2 (electrón, quark, neutrino...) -> 2D, exactamente lo que ya hay.
- fotón: sin masa, no tiene 3 estados de espín-1 como un bosón masivo --
  solo 2 helicidades físicamente realizables (`+1`/`-1`), así que también
  cae en 2D (polarización horizontal/vertical, o cualquier otra base 2D
  equivalente -- ver `02_Qubits.md`).
- una partícula de espín 1 masiva (p.ej. un bosón `W`/`Z`) necesitaría 3D
  genuinas; espín 3/2 -> 4D; en general, espín `s` -> dimensión `2s+1`.

Así que "cualquier partícula, restringida a su grado de libertad interno
discreto, en un instante dado, ignorando dónde está o hacia dónde va" -- sí,
con un `MatrixComplex` de la dimensión adecuada.

## 2. Lo que NO cubre: grados de libertad continuos

El estado completo de una partícula real es (posición o momento) ⊗ (espín).
La parte espacial vive en un espacio de Hilbert de dimensión **infinita**
(funciones de onda continuas), no en un vector finito. Este paquete no tiene
wavefunctions continuas -- solo álgebra lineal de dimensión finita. Se podría
*aproximar* discretizando una malla de posiciones (un vector muy largo, cada
componente la amplitud en un punto de la malla), pero eso ya no es
"representar la partícula" exactamente, es truncar un modelo distinto y
asumir el error de truncamiento.

## 3. Lo que NO cubre, y es la diferencia real entre fermión y bosón

Aquí está el hueco de fondo -- y es donde "¿fermión o bosón?" deja de ser
solo una etiqueta y se convierte en una propiedad matemática que el paquete
no impone hoy.

Fermión y bosón no se distinguen por la dimensión del vector de estado, sino
por cómo se comporta el estado conjunto de **varias partículas idénticas**
al intercambiar dos de ellas:

- bosones: el estado debe ser **simétrico** bajo el intercambio.
- fermiones: debe ser **antisimétrico** (de ahí el principio de exclusión de
  Pauli -- dos fermiones idénticos no pueden ocupar el mismo estado, porque
  eso forzaría al vector antisimétrico a ser cero).

`MatrixComplex.kroneckerprod()` da el producto tensorial "plano", sin
imponer ninguna simetría -- correcto para componer qubits **distinguibles**
(como ya se usa en todo el paquete: cada qubit de un registro de `n` qubits
es una posición física distinta, por tanto ya distinguible sin necesidad de
(anti)simetrizar nada). Pero no basta para modelar partículas genuinamente
idénticas: para eso hace falta (anti)simetrizar explícitamente el vector
conjunto, o pasar a segunda cuantización (espacio de Fock, operadores de
creación/aniquilación). Nada de eso existe hoy en
`com.ipserc.arith.quantum`.

## 4. ¿Sería extensible en el futuro?

Sí, en piezas de dificultad muy distinta -- de menor a mayor:

**(a) Espín general `2s+1`, sin límite práctico.** Extensión natural del
patrón ya usado en `Qubits.java`: fábricas de kets de base para dimensión
`n` arbitraria, y operadores de espín generalizados (`Sx`/`Sy`/`Sz` para
espín `s`, en vez de solo las matrices de Pauli 2x2). `MatrixComplex` ya
soporta cualquier dimensión -- no hay bloqueador de infraestructura, es
trabajo de fórmulas concretas. Esfuerzo moderado, encaja directamente en el
paquete tal y como está.

**(b) Simetrización/antisimetrización para partículas idénticas.** Viable
con las piezas ya existentes (producto tensorial, matrices de permutación,
proyectores -- ya hay proyectores implícitos en el código de medida). Se
podría añadir una clase nueva (p.ej. `IdenticalParticles`) que construya el
subespacio simétrico o antisimétrico de un producto tensorial de `n` copias
del mismo espacio de 1 partícula (determinante de Slater para fermiones,
permanente para bosones). Es un proyecto real, no una fábrica más -- pero
estructuralmente compatible con lo que ya hay, sin necesitar reescribir
nada existente. Coste: crece muy rápido con el número de partículas
(factorial de permutaciones), así que solo sería práctico para `n` pequeño,
igual que `Grover`/`n` grande ya es lento hoy por el crecimiento `2^n`.

**(c) Grados de libertad continuos (posición/momento).** Aquí el límite es
más de fondo: representarlos EXACTAMENTE requeriría un tipo de dato distinto
(funciones de onda, no vectores de dimensión finita), fuera del diseño
actual de `MatrixComplex`. Lo único abordable sin salir del formalismo
finito es la aproximación por truncamiento (una malla de `N` puntos de
posición, `N` grande) -- y aquí hay una conexión real con otra parte del
proyecto: una función de onda discretizada en la base de posiciones es,
estructuralmente, una señal muestreada, exactamente el tipo de objeto que ya
trata `com.ipserc.arith.signal` (`Fourier`/`Laplace`/`Z`). No sería
"resolver mecánica cuántica continua", sino una aproximación numérica al
estilo de las que ya existen ahí -- interesante como paralelismo conceptual,
pero un candidato grande y aparte, no una extensión pequeña de
`com.ipserc.arith.quantum`.

## Resumen

| Grado de libertad | ¿Representable hoy? | Extensión futura |
|---|---|---|
| Espín/polarización (dimensión finita fija, p.ej. 2D) | Sí, ya hecho | -- |
| Espín general `2s+1` | No hay fábricas, pero el álgebra ya lo soporta | (a) esfuerzo moderado |
| Partículas idénticas (fermión/bosón, `n` partículas) | No | (b) proyecto nuevo, coste factorial en `n` |
| Posición/momento continuos | No (solo aproximable) | (c) aproximación por truncamiento, conexión con `com.ipserc.arith.signal` |

Ninguno de los tres es un candidato en marcha -- quedan anotados aquí como
posibles líneas futuras si el "Rol Física/Mecánica Cuántica" se retoma más
adelante.
