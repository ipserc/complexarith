# `Fock.java` -- espacio de Fock y efecto Hong-Ou-Mandel

## Qué es

El **efecto Hong-Ou-Mandel** (HOM, 1987): dos fotones **idénticos** entran
a la vez en un semiespejo 50/50, uno por cada puerto de entrada, y salen
**siempre juntos** por el mismo puerto de salida. Nunca se detecta una
*coincidencia* (un fotón en cada detector), cuando con dos partículas
clásicas -- o con dos fotones que se puedan distinguir -- las coincidencias
son el 50 % de los casos.

Para modelarlo hace falta una pieza nueva, `Fock.java`: el **espacio de
Fock** de `n` fotones repartidos en `m` modos, y la forma de aplicarle un
componente óptico (semiespejo, desfasador...) conocido solo por su matriz
de 1 fotón.

## Por qué no basta con `Qubits`

En `TestQuantum_BeamSplitter01` (Mach-Zehnder de 1 fotón) el camino del
fotón es un qubit: `|0>` = brazo *a*, `|1>` = brazo *b*, y el semiespejo es
`Qubits.hadamard()`. La tentación natural para 2 fotones es "un qubit por
fotón" y el producto tensorial `H (x) H`. Pero eso describe partículas
**distinguibles**: el qubit 1 es "el fotón 1" y el qubit 2 es "el fotón 2",
con identidad propia. Ya estaba anotado en `17_limites_y_extensiones.md`
(sección 3) como el hueco de fondo del paquete: para partículas idénticas
hace falta simetrizar el estado o, equivalentemente, pasar a segunda
cuantización.

En el espacio de Fock no se dice *qué* fotón está en cada sitio, solo
*cuántos* hay en cada modo:

| Estado | Significado |
|---|---|
| `\|1,1>` | un fotón en el modo *a* y otro en el *b* (la entrada de HOM) |
| `\|2,0>` | los dos fotones en el modo *a* |
| `\|0,2>` | los dos fotones en el modo *b* |

Con 2 fotones en 2 modos el espacio tiene dimensión 3 (no 4 como `H (x) H`):
los estados "fotón 1 en *a*, fotón 2 en *b*" y "fotón 1 en *b*, fotón 2 en
*a*" son **el mismo estado** `|1,1>`, porque no hay "fotón 1" ni "fotón 2".

## La explicación física del efecto, en palabras

Para acabar con "un fotón en cada salida" hay **dos maneras**:

- **Ninguno cruza**: el fotón que entra por *a* sale por *a*, el que entra
  por *b* sale por *b*. Amplitud: `H[a<-a]*H[b<-b] = (1/√2)*(-1/√2) = -1/2`
  (el `-1` de `H = (1/√2)[[1,1],[1,-1]]` está en `b->b`).
- **Los dos cruzan**: el de *a* sale por *b* y el de *b* sale por *a*.
  Amplitud: `H[b<-a]*H[a<-b] = (1/√2)*(1/√2) = +1/2`.

La clave es **qué se suma**:

- **Fotones distinguibles** (distinto color, llegada con retardo...): en
  principio se podría saber qué fotón llegó a cada detector, así que las dos
  maneras son sucesos distintos y se suman sus **probabilidades**:
  `|-1/2|² + |+1/2|² = 1/4 + 1/4 = 1/2`. El 50 % clásico.
- **Fotones idénticos**: no hay forma, ni siquiera en principio, de saber
  cuál de las dos maneras ocurrió. Se suman las **amplitudes** antes de
  elevar al cuadrado: `-1/2 + 1/2 = 0`. Interferencia destructiva total:
  probabilidad de coincidencia `0`.

Es la misma idea que en el Mach-Zehnder de 1 fotón (dos caminos que
interfieren), pero ahora lo que interfiere no son los caminos de un fotón,
sino **las dos formas de intercambiar a los dos fotones**.

El estado de salida completo es

```
H sobre |1,1>  ->  (|2,0> - |0,2>)/√2
```

es decir: la mitad de las veces salen los dos por *a*, la otra mitad los dos
por *b*, y nunca uno por cada lado. El signo menos es una fase relativa entre
las dos opciones; no cambia los recuentos de los detectores.

## El formalismo: permanentes

Esa "suma de las maneras de asignar fotones de entrada a salidas, cada una
con el producto de sus amplitudes de 1 fotón, todas con signo `+`" tiene
nombre: es el **permanente** de la matriz -- un determinante sin los signos
alternos:

```
det [[a,b],[c,d]] = ad - bc        Per [[a,b],[c,d]] = ad + bc
```

Para `H`: `Per(H) = (1/√2)(-1/√2) + (1/√2)(1/√2) = -1/2 + 1/2 = 0`.

En general, un componente óptico lineal sobre `m` modos queda descrito por
su unitaria de 1 fotón `U` (`m x m`), y su acción sobre `n` fotones
idénticos es, elemento a elemento:

```
<s|Phi_n(U)|t> = Per(U[s,t]) / sqrt(prod_i s_i! * prod_j t_j!)
```

donde `s`, `t` son vectores de ocupación (salida y entrada) y `U[s,t]` es la
submatriz `n x n` que se obtiene repitiendo la fila `i` de `U` `s_i` veces y
la columna `j` `t_j` veces. Los factoriales normalizan los estados con
varios fotones en el mismo modo (como `|2,0>`). Es la fórmula estándar del
*boson sampling*.

**Fermiones**: el mismo esquema con el **determinante** en vez del
permanente (el intercambio de dos fermiones idénticos lleva signo `-`). En
el semiespejo daría `-1/2 - 1/2 = -1`: dos fermiones idénticos saldrían
**siempre separados** -- el principio de exclusión de Pauli visto en un
semiespejo. No implementado (aplazado).

## API de `Fock.java`

Clase de utilidades estática, mismo patrón que `Qubits`. Los estados son
`MatrixComplex` columna sobre la base de Fock, así que `times()`,
`adjoint()`, `println()` funcionan igual que con qubits.

| Método | Qué hace |
|---|---|
| `dimension(n, m)` | `C(n+m-1, n)`: número de estados de `n` fotones en `m` modos. |
| `basis(n, m)` | Todos los vectores de ocupación, en orden lexicográfico descendente (`[2,0], [1,1], [0,2]`). Es el orden de las filas de todos los estados y operadores. |
| `indexOf(occ...)` | Posición de un vector de ocupación en esa base. |
| `ket(occ...)` | El estado base `\|n_1,...,n_m>`, p.ej. `Fock.ket(1,1)`. |
| `basisLabel(occ...)` | Etiqueta de Dirac, `"\|1,1>"`. |
| `permanent(A)` | Permanente por la fórmula de Ryser, `O(2^n n^2)`. Calcular permanentes es #P-difícil (la raíz de la dificultad clásica del boson sampling): solo práctico para pocos fotones. |
| `multiPhotonUnitary(U, n)` | `Phi_n(U)`. Para `n=1` devuelve `U`. Falla alto si `U` no es unitaria, y vuelve a comprobar la unitariedad del resultado. |
| `measure(state, random)` / `measure(state, shots, random)` | Medida con detectores que cuentan fotones, regla de Born sobre la base de Fock. Comparte el muestreo con `Qubits.measure()` (vía el helper `Qubits.bornSample()`), sin su exigencia de dimensión potencia de 2. |

Ejemplo mínimo (el núcleo de HOM en 3 líneas):

```java
MatrixComplex semiespejo2 = Fock.multiPhotonUnitary(Qubits.hadamard(), 2);
MatrixComplex salida = semiespejo2.times(Fock.ket(1, 1));
// salida = (|2,0> - |0,2>)/√2  ->  salida.getItem(Fock.indexOf(1,1), 0) == 0
```

## El "dip" de HOM: distinguibilidad parcial

¿Qué pasa entre "idénticos" (0 % de coincidencias) y "distinguibles"
(50 %)? En el laboratorio se barre un retardo entre los dos fotones: cuanto
menos se solapan sus paquetes de onda, más distinguibles son. La curva de
coincidencias frente al retardo tiene un pozo en retardo cero -- el famoso
*dip* de HOM.

Se modela sin ninguna fórmula ad hoc, solo con más modos: cada fotón tiene
además un **modo temporal** (temprano / tardío), lo que da 4 modos (camino
*a*/*b* x instante temprano/tardío):

- fotón 1: entra por *a*, temprano;
- fotón 2: entra por *b*, en `cos(α)|temprano> + sin(α)|tardío>`.

`cos(α)` es el **solapamiento** entre los dos fotones: `α=0`, idénticos;
`α=π/2`, distinguibles del todo por el instante de llegada. El semiespejo
actúa solo sobre el camino, no sobre el tiempo: `U = H (x) I_2`, y se aplica
con `Fock.multiPhotonUnitary(U, 2)` sobre el espacio de 2 fotones en 4 modos
(dimensión 10). La probabilidad de coincidencia que sale del cálculo es

```
P(coincidencia) = (1 - cos²(α)) / 2
```

que pasa suavemente de `0` a `1/2`. Lectura física: solo la parte "idéntica"
de los dos fotones (la componente temprana del fotón 2, de peso `cos²(α)`)
interfiere y se cancela; la parte distinguible (la tardía) se comporta de
forma clásica, con su 50 %.

## Verificación

**`ScratchQuantum_FockAudit01`** (9/9 OK), cada comprobación contra un
oráculo independiente del código:

1. `basis`/`dimension`/`indexOf` coherentes para 7 combinaciones `(n, m)`.
2. Orden de la base `|2,0>, |1,1>, |0,2>`.
3. `permanent(unos n x n) == n!`, `n=1..7`.
4. Ryser == suma literal sobre permutaciones, matrices complejas aleatorias, `n=1..6`.
5. `Phi_1(U) == U`.
6. Homomorfismo `Phi_n(UV) == Phi_n(U)*Phi_n(V)` (`n=2..4` fotones, 3 modos, `U`,`V` que no conmutan) -- propiedad de grupo que no usa la fórmula de permanentes.
7. `Phi_2(H)|1,1> == (|2,0> - |0,2>)/√2`.
8. Rechazo de una `U` no unitaria.
9. `measure()` sobre dimensión 3: nunca `|1,1>`, ~50/50 entre `|2,0>` y `|0,2>`.

**`TestQuantum_HongOuMandel01`** (demostración didáctica):

1. Entrada `|1,1>`, matriz `Phi_2(H)` y estado de salida; amplitud de coincidencia `Per(H) = 0`.
2. 3477 disparos: **0 coincidencias** (lanza excepción si apareciera alguna).
3. Comparación con 2 fotones distinguibles en el modelo de qubits (`H (x) H`): ~50 % de coincidencias.
4. Dip: 9 valores de `α` en `[0, π/2]`; la probabilidad calculada en Fock coincide con `(1-cos²(α))/2` hasta `1e-12` (lanza excepción si no), y las frecuencias medidas la siguen.

Salida típica del punto 4:

```
alfa/(pi/2)  solapamiento  P(coinc) Fock  formula (1-cos^2)/2  medido (3477 disparos)
   0,000        1,0000         0,0000            0,0000               0 (  0,00%)
   0,500        0,7071         0,2500            0,2500             856 ( 24,62%)
   1,000        0,0000         0,5000            0,5000            1742 ( 50,10%)
```

## Para ejecutarlo

Desde la raíz del proyecto (Linux; `classes/` aporta `Chronometer` y
`ComplexParser`, que solo existen como bytecode):

```bash
O=$(mktemp -d); cp -r classes/com $O/
javac -nowarn -encoding UTF-8 -cp "$O:classes" -d $O \
  $(find src/com -name '*.java') \
  src/TestComplex/ScratchQuantum_FockAudit01.java src/TestComplex/TestQuantum_HongOuMandel01.java
java -cp "$O:classes" TestComplex.ScratchQuantum_FockAudit01
java -cp "$O:classes" TestComplex.TestQuantum_HongOuMandel01
```

## Límites y extensiones posibles

- **Fermiones** (determinante en vez de permanente): antiagrupamiento, 100 %
  de coincidencias. Extensión pequeña sobre la misma estructura.
- **Más fotones/modos**: `Fock` ya es general (`n` fotones, `m` modos), p.ej.
  HOM generalizado en un *multiport* (la DFT `3x3` como divisor de 3 vías ya
  se usa en el scratch). El coste crece con la dimensión `C(n+m-1, n)` y con
  el permanente (`2^n`): práctico solo para pocos fotones -- precisamente la
  razón por la que el boson sampling se propuso como prueba de ventaja
  cuántica.
- **Pérdidas y detectores imperfectos**: no modelados (estados puros,
  componentes sin pérdidas, detectores ideales que cuentan fotones).
