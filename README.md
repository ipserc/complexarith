# complexarith

Librería Java para aritmética y álgebra lineal en el cuerpo complejo (ℂ), sin dependencias
externas. Empezó en 2017 como un proyecto personal para modelar los números complejos en Java y
ha ido creciendo hasta cubrir matrices complejas, factorizaciones matriciales clásicas, polinomios,
sistemas de ecuaciones, geometría y las transformadas de señal habituales (Fourier, Laplace, Z) —
todo sobre el mismo tipo `Complex` de base.

## Highlights

### `com.ipserc.arith.complex` — `Complex`
Número complejo con parser propio: acepta literales en texto natural (`"3-2i"`) además de
`Complex(re, im)`. Aritmética completa (suma, resta, producto, división, potencia) y funciones
(exponencial, logaritmo, trigonométricas e hiperbólicas y sus inversas, raíces n-ésimas).

### `com.ipserc.arith.matrixcomplex` — `MatrixComplex`
Matrices sobre ℂ, con la operativa dividida en clases dedicadas (`MatrixComplexUnary`,
`MatrixComplexRank`, `MatrixComplexKernel`, `MatrixComplexOrtho`, `MatrixComplexFunctions`,
`MatrixComplexCharPoly`, `MatrixComplexEquationSystems`...):
- Determinante, inversa, rango, núcleo, polinomio característico.
- Detección de singularidad numérica por tolerancia relativa (no epsilon absoluto), para que
  matrices grandes con residuos de redondeo pequeños en términos absolutos pero significativos en
  el determinante completo no se traten como no singulares por error.
- Funciones matriciales (`exp`, `log`, `sqrt`, seno/coseno hiperbólico...) vía diagonalización o
  desarrollo en serie (Taylor/Mercator) cuando la matriz no es diagonalizable.
- `Eigenspace`: autovalores y autovectores, multiplicidad algebraica y geométrica, con
  agrupamiento de raíces repetidas por componentes conexas (no por redondeo por componente, que
  fragmenta clusters legítimos).
- `MatrixComplexPlot`: representación gráfica de matrices (incluida una presentación "cuadrada").

### `com.ipserc.arith.factorization`
Las factorizaciones matriciales estándar, todas sobre matrices complejas:
- `LUfactor` — `A = L·U`.
- `QRfactor` — `A = Q·R` (Gram-Schmidt, Householder, Givens).
- `SVDfactor` — `A = U·Σ·Vᵀ`.
- `Diagfactor` — `A = P·D·P⁻¹`.
- `Hessenbergfactor` — reducción a Hessenberg superior por semejanza unitaria (`A = Q·H·Qᴴ`).
- `QRSchurfactor` / `Schurfactor` — factorización de Schur (`A = Q·T·Qᴴ`) vía iteración QR con
  desplazamiento de Wilkinson y deflación — el método tipo LAPACK/MATLAB para autovalores sin
  formar nunca el polinomio característico explícito.
- `Jordan` — forma canónica de Jordan, incluida multiplicidad geométrica > 1.

### `com.ipserc.arith.polynom` — `Polynom`
Polinomios sobre ℂ y cálculo de sus raíces:
- Generación de familias clásicas: Hermite, Legendre, Laguerre, Chebyshev, de cualquier grado.
- Cálculo de raíces vía matriz compañía + `QRSchurfactor` (`solveQRCompanion`), con modo opcional
  de agrupamiento estadístico de raíces por multiplicidad (`e_rootCalcMode`).
- `evalFromRoots()` — evaluación numéricamente estable a través de la forma factorizada, evita la
  pérdida de precisión de reconstruir coeficientes vía `power()`/`times()` para grado alto.
- `Spline` — interpolación por splines, apoyada en `Syseq` para resolver el sistema.

### `com.ipserc.arith.vectorcomplex` — `VectorComplex`
Vectores sobre ℂ: producto escalar, norma, y producto vectorial generalizado a n dimensiones
(`vectorprodN`) además del binario 3D clásico.

### `com.ipserc.arith.geom`
Geometría afín sobre ℂ: `Point`, `Line`, `Plane` — incidencia, paralelismo, distancias (incluida
distancia entre rectas en dimensión > 3).

### `com.ipserc.arith.syseq` — `Syseq` / `Syseqnum`
Sistemas de ecuaciones lineales: resolución directa (`Syseq`, homogéneos y no homogéneos) y
métodos numéricos iterativos (`Syseqnum`) — gradiente conjugado (`congrad`) y GMRES (`genminres`).

### `com.ipserc.arith.signal` — `Fourier` / `Laplace` / `Z` / `Sigfunc`
Transformadas de señal clásicas y sus filtros, con representación gráfica de los resultados.

### `com.ipserc.arith.combinatoric` — `CombinationNoReps`
Combinaciones sin repetición y numeración asociada.

### `com.ipserc.arith.plot` — `SimpleGnuplot`
Lanzador de gnuplot propio, sin dependencias externas (sustituye a la antigua librería
`com.panayotis.gnuplot`, que bloqueaba con `Process.waitFor()`). Cada operación de graficado del
proyecto expone un par `xxxSync()`/`xxxAsync()` sobre un único método genérico parametrizado por
`SimpleGnuplot.e_syncMode`.

### `TestComplex`
Numerosos ejemplos de uso de todas las clases anteriores, además de los drivers `ScratchXxx`/
`TestXxx` usados para investigar y medir bugs a lo largo del desarrollo — se conservan todos,
también después de resolverse, como referencia.

## Configuración

Importar como proyecto Java. Para Eclipse: `Properties → Java Build Path → Libraries → Add Class
Folder → classes`.

No hay dependencias de terceros: todo el código, incluido el graficado, es autocontenido.
