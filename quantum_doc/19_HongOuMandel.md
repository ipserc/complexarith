# `Fock.java` -- espacio de Fock y efecto Hong-Ou-Mandel (bosones y fermiones)

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
el semiespejo da `-1/2 - 1/2 = -1`: dos fermiones idénticos salen
**siempre separados**. Implementado en `Fock` 1.1, ver la sección "HOM con
fermiones" más abajo.

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
| `measure(state, random)` / `measure(state, shots, random)` | Medida con detectores que cuentan fotones, regla de Born sobre la base de Fock. Comparte el muestreo con `Qubits.measure()` (vía el helper `Qubits.bornSample()`), sin su exigencia de dimensión potencia de 2. Sirve igual para estados fermiónicos. |
| `fermionDimension(n, m)` | `C(m, n)`: estados de `n` fermiones en `m` modos (cada modo 0 o 1). Exige `n <= m` (Pauli). |
| `fermionBasis(n, m)` | Vectores de ocupación 0/1, mismo orden lexicográfico descendente (`[1,1,0], [1,0,1], [0,1,1]`). |
| `fermionIndexOf(occ...)` / `fermionKet(occ...)` | Posición en esa base / estado base. Falla alto ante una ocupación distinta de 0 o 1. `fermionKet(1,0,1)` es `a†_0 a†_2 \|0>`, con los modos ocupados en orden **ascendente**: los operadores de creación de fermiones anticonmutan, así que ese orden fija el signo de cada estado base. |
| `determinant(A)` | Determinante por eliminación gaussiana con pivoteo parcial, `O(n^3)` (a diferencia del permanente, es fácil de calcular). |
| `multiFermionUnitary(U, n)` | `Lambda_n(U)`: `<s\|Lambda_n(U)\|t> = det(U[s,t])` (filas = modos ocupados de `s`, columnas = los de `t`, ambos ascendentes; sin factoriales). Para `n=1` devuelve `U`. Mismas comprobaciones de unitariedad que `multiPhotonUnitary`. |

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

## HOM con fermiones: antiagrupamiento

Con dos **fermiones** idénticos (electrones, por ejemplo) en el mismo
semiespejo pasa lo contrario: salen **siempre separados**, uno por cada
puerto.

**Por qué.** Las dos maneras de acabar en coincidencia son las mismas
(ninguno cruza `-1/2`, los dos cruzan `+1/2`), pero "los dos cruzan" es
"ninguno cruza" con las dos partículas **intercambiadas**, y para fermiones
el intercambio lleva un signo `-`:

| Partículas | Cómo se combinan | Amplitud de coincidencia | P(coincidencia) |
|---|---|---|---|
| Bosones idénticos | permanente | `-1/2 + 1/2 = 0` | 0 (salen juntos) |
| Distinguibles | se suman probabilidades | -- | `1/4 + 1/4 = 1/2` |
| Fermiones idénticos | determinante | `-1/2 - 1/2 = -1` | 1 (salen separados) |

El **principio de exclusión de Pauli** es el caso particular "los dos en la
misma salida": `|2,0>` y `|0,2>` ni siquiera existen en el espacio de Fock
fermiónico (cada modo tiene 0 o 1 partícula), así que con 2 fermiones en 2
modos el único estado es `|1,1>`, y `Lambda_2(H)` es la matriz `1x1`
`det(H) = -1`: `|1,1> -> -|1,1>`. Pauli no es una regla añadida: sale del
mismo signo `-` del intercambio, que también explica que los fotones se
agrupen.

```java
MatrixComplex salida = Fock.multiFermionUnitary(Qubits.hadamard(), 2).times(Fock.fermionKet(1, 1));
// salida = -|1,1>: probabilidad de coincidencia 1
```

**El matiz del espín.** El antiagrupamiento total exige que los dos
fermiones sean idénticos en **todo**, también en el espín. El signo `-`
afecta a la función de onda completa (camino x espín). Se modela con 4
modos (camino *a*/*b* x espín arriba/abajo, modo = 2·camino + espín) y el
semiespejo `U = H (x) I_2`, que actúa solo sobre el camino:

| Estado de espín de entrada | P(coincidencia) |
|---|---|
| mismo espín, `\|a↑, b↑>` | 1 (separados) |
| triplete `m=0`, `(\|a↑,b↓> + \|a↓,b↑>)/√2` | 1 (separados) |
| singlete, `(\|a↑,b↓> - \|a↓,b↑>)/√2` | **0: se agrupan como bosones** |

En el singlete la parte de espín es antisimétrica, así que la parte de
camino queda **simétrica**, y los dos electrones salen juntos (`|1,1,0,0>`
o `|0,0,1,1>`: los dos por *a* con espines opuestos, o los dos por *b*).
Pauli no prohíbe "dos electrones por la misma salida": prohíbe "dos
electrones en el mismo estado **completo**".

**El "pico" fermiónico**, espejo del dip: el electrón 1 entra por *a* con
espín arriba, el 2 por *b* con espín `cos(α)|↑> + sin(α)|↓>`. El cálculo
en Fock da

```
P(coincidencia) = (1 + cos²(α)) / 2
```

que baja de `1` (espines idénticos) a `1/2` (espines ortogonales,
distinguibles). Con bosones el mismo barrido da `(1 - cos²(α))/2`, de `0`
a `1/2`: dip para bosones, pico para fermiones.

## Verificación

**`ScratchQuantum_FockAudit01`** (15/15 OK), cada comprobación contra un
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
10. `fermionBasis`/`fermionDimension`/`fermionIndexOf` coherentes para 8 combinaciones `(n, m)` y orden `|1,1,0>, |1,0,1>, |0,1,1>`.
11. Pauli: `fermionBasis(3,2)` y `fermionKet(2,0)` se rechazan.
12. `determinant()` == suma literal con signo sobre permutaciones, `n=1..6`; matriz con dos filas iguales -> exactamente 0.
13. `Lambda_1(U) == U` y homomorfismo `Lambda_n(UV) == Lambda_n(U)*Lambda_n(V)` (`n=2..3`, 4 modos).
14. HOM fermiónico: `Lambda_2(H)|1,1> == -|1,1>`.
15. Signo del intercambio: con `U = SWAP`, fermiones `|1,1> -> -|1,1>`, bosones `|1,1> -> +|1,1>`.

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

**`TestQuantum_HongOuMandelFermiones01`** (demostración didáctica):

1. Base fermiónica de 2 en 2 (solo `|1,1>`), `Lambda_2(H) = det(H) = -1`, comparado con `Per(H) = 0`.
2. 3477 disparos: **100 % de coincidencias** (lanza excepción si no).
3. Con espín: mismo espín y triplete -> 100 %, singlete -> 0 %, mostrando el estado de salida de cada uno.
4. Pico: 9 valores de `α`; la probabilidad calculada en Fock coincide con `(1+cos²(α))/2` hasta `1e-12` (lanza excepción si no).

## Para ejecutarlo

Desde la raíz del proyecto (Linux; `classes/` aporta `Chronometer` y
`ComplexParser`, que solo existen como bytecode):

```bash
O=$(mktemp -d); cp -r classes/com $O/
javac -nowarn -encoding UTF-8 -cp "$O:classes" -d $O \
  $(find src/com -name '*.java') \
  src/TestComplex/ScratchQuantum_FockAudit01.java src/TestComplex/TestQuantum_HongOuMandel01.java \
  src/TestComplex/TestQuantum_HongOuMandelFermiones01.java
java -cp "$O:classes" TestComplex.ScratchQuantum_FockAudit01
java -cp "$O:classes" TestComplex.TestQuantum_HongOuMandel01
java -cp "$O:classes" TestComplex.TestQuantum_HongOuMandelFermiones01
```

## Límites y extensiones posibles

- **Fermiones**: hechos (`Fock` 1.1). Sin interacción entre partículas: no
  hay repulsión de Coulomb entre electrones, solo la estadística.
- **Más fotones/modos**: `Fock` ya es general (`n` fotones, `m` modos), p.ej.
  HOM generalizado en un *multiport* (la DFT `3x3` como divisor de 3 vías ya
  se usa en el scratch). El coste crece con la dimensión `C(n+m-1, n)` y con
  el permanente (`2^n`): práctico solo para pocos fotones -- precisamente la
  razón por la que el boson sampling se propuso como prueba de ventaja
  cuántica.
- **Pérdidas y detectores imperfectos**: no modelados (estados puros,
  componentes sin pérdidas, detectores ideales que cuentan fotones).
