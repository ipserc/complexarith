package com.ipserc.arith.matrixcomplex;

import java.lang.Math;

import com.ipserc.arith.combinatoric.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.factorization.Schurfactor;
import com.ipserc.arith.polynom.*;
import com.ipserc.arith.syseq.Syseq;

/**
 * 
 * @author ipserc
 *
 */
public class MatrixComplex {
	public Complex[][] complexMatrix;
	
	final static String HEADINFO = "MatrixComplex --- INFO: ";
	private final static String VERSION = "1.71 (2026_0810_2340)";
	/* VERSION Release Note
	 *
	 * 1.71 (2026_0810_2340)
	 * Bug real reportado por el usuario: Eigenspace imprimia "arith mult:1 - geom mult:2" para un
	 * autovalor (violacion del teorema fundamental geom_mult<=arith_mult). Causa raiz en
	 * MatrixComplexUnary.rankByRelativePivot() (usado por rankNearSingular(), y este por
	 * Eigenspace.geometricMultiplicity()): leia solo la entrada DIAGONAL de cada fila tras
	 * triangleUp(), que solo pivota por FILAS -- si una columna se anula del todo antes de su turno
	 * (columna dependiente de una anterior), el pivote real de esa fila queda desplazado a una
	 * columna posterior y la diagonal se queda con un cero ESTRUCTURAL, infravalorando el rango.
	 * Confirmado con [i,-i,i; i,-i,-i; i,-i,i] (columna 1 = -columna 0): triangleUp() da
	 * [i,-i,i; 0,0,-2i; 0,0,0], diagonal [i,0,0] -> rango 1 en vez del rango real 2 (rank() de
	 * referencia y el nucleo (1,1,0) lo confirman).
	 * Primer intento (leer el maximo modulo de TODA la fila en vez de solo la diagonal) resulto
	 * INSUFICIENTE -- detectado en la propia bateria de regresion de esta sesion antes de commitear:
	 * rompia el caso general (autovalor simple con geom mult correctamente 1 pasaba a reportar 0,
	 * imposible matematicamente). Causa: cuando DOS filas quedan con su pivote desplazado a la MISMA
	 * columna posterior (A-1*I de TestEigenV05, [2,2,-1;2,2,1;0,0,4] -> triangleUp() da
	 * [2,2,-1;0,0,2;0,0,4], filas 1 y 2 son linealmente DEPENDIENTES entre si -- fila2=2*fila1 -- pero
	 * cada una por separado parecia "no despreciable"), una sola pasada de triangleUp() no basta ni
	 * repetida (el hueco estructural persiste, la eliminacion nunca mira mas alla de la columna k en
	 * el paso k). Arreglo definitivo: rankByRelativePivot() ya NO depende de triangleUp() -- eliminacion
	 * gaussiana propia con AVANCE DE COLUMNA (si la columna actual no tiene pivote no-despreciable en
	 * las filas restantes, se prueba la siguiente columna sin avanzar de fila), mismo umbral relativo
	 * SINGULARITY_REL_TOL aplicado en cada paso de eliminacion en vez de solo al leer el resultado
	 * final. Verificado con ScratchGeomMultBug01.java (caso reportado) y ScratchGeomMultBug02.java
	 * (caso de regresion de la Ronda 1), ambos conservados en src/TestComplex/: coinciden con rank().
	 *
	 * 1.70 (2026_0810_2330)
	 * Auditoria matematica dedicada (Vigesimosexta sesion, bloque 4 de la hoja de ruta
	 * "Matematicas Aplicadas", ver Claude/ComplexArithRev.md) -- 2 bugs reales encontrados y
	 * arreglados en MatrixComplexOrtho.gramSchmidt()/gramSchmidtFull()/gramSchmidtM()/
	 * gramSchmidtMFull() (helper cubierto por este VERSION, sin VERSION propio), verificados con
	 * ScratchFactorizationAudit01.java (conservado en src/TestComplex/):
	 * - Los 4 metodos compartian una linea "colLen = colLen>rowLen?rowLen:colLen" que, para una
	 *   matriz genuinamente rectangular, encogia la DIMENSION AMBIENTE de cada vector en vez de
	 *   solo el numero de vectores de salida -- confirmado que QRfactor.qrGramSchmidt() sobre una
	 *   matriz 3x2 devolvia una Q de 2x2 (no 3x2), haciendo Q*R literalmente indefinido
	 *   (dimensiones incompatibles, se manifestaba como valores Infinity). Arreglado separando el
	 *   numero de vectores de salida (min(dimension_ambiente, num_vectores), como ya prometia el
	 *   Javadoc de cada metodo) de la dimension ambiente en si, que ya no se recorta.
	 * - gramSchmidtFull()/gramSchmidtMFull(): de propina, la rama de relleno aleatorio
	 *   ("else x.initMatrixRandomInt(9)") era codigo INALCANZABLE -- el bucle nunca llegaba a
	 *   superar el limite (ya recortado) que la propia guarda comprobaba, asi que ninguno de los 2
	 *   metodos podia hacer nunca lo que su nombre/Javadoc prometen ("extendido a la dimension
	 *   completa... los vectores no incluidos se generan aleatoriamente"). Arreglado junto con el
	 *   bug de dimension: el bucle ahora llega de verdad a la dimension ambiente completa.
	 * La convencion "un vector por FILA" (coherente con Eigenspace.solutions()/VectorComplex.base()
	 * en el resto del proyecto) se conserva sin tocar -- un primer intento de reescribir estos 4
	 * metodos operando directamente sobre las COLUMNAS de la matriz de entrada rompia Schurfactor
	 * (que depende de VectorComplex.base().orthonormalize() con la convencion de filas), revertido
	 * antes de commitear. El desajuste real para QR (que necesita las COLUMNAS de la matriz
	 * ortogonalizadas) se arreglo en QRfactor.java en su lugar (ver ese VERSION) transponiendo
	 * antes/despues de llamar a estos 4 metodos, sin tocar su contrato de "filas".
	 * Verificado con ScratchFactorizationAudit01.java (42/42 OK: reconstruccion + unitariedad de
	 * LUfactor x4 metodos, QRfactor x5 metodos incluida una matriz genuinamente rectangular,
	 * Schurfactor, SVDfactor x2 metodos, Diagfactor con autovalor repetido, Jordan defectuoso) y
	 * una bateria de 65 ficheros consumidores: 63/65 exit=0, TestJordan01/TestVector03 exit=1
	 * (ambos fallos preexistentes ya documentados, confirmados no relacionados).
	 *
	 * 1.69 (2026_0809_1940)
	 * Superficie 3D REAL, a peticion del usuario ("puedes hacer que TestZeta05 haga un plot de
	 * superficie real?"). Diagnostico: 2 problemas distintos, no uno. (1) SimpleGnuplot.addPlot()
	 * volcaba TODOS los puntos como un unico bloque plano sin separadores -- splot no tiene forma
	 * de saber donde termina una "linea de barrido" y empieza la siguiente, asi que conectaba (o
	 * no) los puntos en un orden arbitrario en vez de una malla real, aunque los datos de
	 * TestZeta05 ya se calculaban en orden de grid (x fuera, y dentro). (2) el estilo usado,
	 * "data surface", no es (hasta donde se pudo razonar sin gnuplot fiable en este entorno para
	 * confirmarlo en vivo) una palabra clave valida de "set style data" en gnuplot -- sospecha
	 * razonada, no verificada, señalada al usuario antes de arreglar.
	 * SimpleGnuplot.java: nuevo addPlotGrid(double[][][]) + buildScript() ampliado para insertar
	 * una linea en blanco tras cada fila de un grid (la convencion que splot necesita para
	 * conectar puntos DENTRO de una fila sin conectar a traves de filas -- confirmado el formato
	 * exacto por inspeccion del script generado via reflexion, sin lanzar gnuplot,
	 * ScratchSimpleGnuplotGridVerify01.java conservado). Sigue sin VERSION propio.
	 * MatrixComplexPlot.java: nuevo plotGrid3D()/plotGrid3DSync/Async -- deliberadamente SIN
	 * parametro de estilo (a diferencia de plotSeries3D()): LINES es la unica de las 3 opciones
	 * de e_lineStyle3D que tiene sentido para datos en grid (BOXPLOT dibujaria una caja
	 * desconectada por punto, ignorando el grid; "SURFACE" era precisamente la suposicion sin
	 * verificar que este cambio sustituye). "set hidden3d" activado siempre, para que la malla se
	 * vea como una superficie solida real, no un alambre transparente.
	 * TestZeta05.java: reconstruido para pasar un grid double[sampleBase][sampleBase][3] en vez
	 * de una lista plana -- los datos YA se calculaban en ese orden (fila=indice de x,
	 * columna=indice de y), solo hacia falta reindexar la salida de coordPlot[k] a
	 * pointsRe/Im[row][col] en vez de aplanarla. plot() pierde el parametro "samples" (ya estaba
	 * muerto en la version original, solo referenciado en una linea comentada).
	 *
	 * 1.68 (2026_0809_1928)
	 * MatrixComplexPlot.java: nuevos metodos 3D (e_lineStyle3D {LINES,BOXPLOT,SURFACE},
	 * setLineStyle3D(), plotSeries3DSync/Async, plotSeries3D generico) -- candidato pendiente
	 * anotado en la Vigesimotercera sesion (ver Claude/ComplexArithRev.md), mismo diseno que la
	 * superficie 2D ya consolidada (plotSeriesSync/Async + entrada generica parametrizada por
	 * SimpleGnuplot.e_syncMode). Cubre el mismo patron ya usado a mano en los 8 scripts 3D de
	 * TestComplex/ (SimpleGnuplot.newGraph3D()+addPlot(double[][3])+"set style data X") --
	 * inventariado con un agente Explore en la misma sesion, sin migrar todavia ningun llamador
	 * (decision de alcance aparte). Solo 3 estilos expuestos (LINES/BOXPLOT/SURFACE): son los
	 * unicos realmente usados, el resto (polygons/rgbalpha/isosurface/pm3d) aparecen solo
	 * comentados en los 8 scripts, nunca en vivo. Verificado sin lanzar gnuplot (regla ya
	 * establecida): ScratchMatrixComplexPlot3DVerify01.java (conservado) compara via reflexion el
	 * texto exacto del script generado (SimpleGnuplot.buildScript(), privado) entre una replica a
	 * mano de los 3 patrones reales (TestSurfaceCosc01 BOXPLOT, TestSurfaceLog01 BOXPLOT+logscale
	 * Z, TestZeta05 SURFACE) y la nueva API -- script identico en los 3, mas el caso de varias
	 * series. MatrixComplexPlot.java sigue sin VERSION propio (helper, cubierto por esta).
	 *
	 * 1.67 (2026_0809_1922)
	 * timesEqRaw() cableado en los 10 sitios de acumulador powMatrix/powMat de los 7 metodos
	 * Taylor/Mercator de MatrixComplexFunctions.java (exp_, trigonTaylor -x3-, trigonHyperbolyc-
	 * Taylor -x2-, logTaylor, logMercator, logHat -x2-, xMat en el ultimo), sustituyendo timesEq()
	 * por timesEqRaw() (VERSION 1.66) en cada sitio. A diferencia del intento anterior de esta
	 * misma familia de candidatos ("*Eq a nivel MatrixComplex", Decimoctava sesion, sin ganancia
	 * de tiempo de pared medible), este SI da una ganancia real y grande, medida con
	 * ScratchTimesEqRawBench01.java (conservado): timesEqRaw() vs timesEq() en un producto
	 * aislado, 3.3x (N=8) a 6.9x (N=200) mas rapido; en una cadena de 20 productos encadenados
	 * (el patron real del do-while de Taylor), 5-6x mas rapido en N=8/50/100. Verificado sin
	 * regresion: ScratchTimesEqRawWiringVerify01.java (conservado) da salida byte a byte
	 * identica a un build de referencia desde HEAD en los 9 delegadores publicos (exp_/sinTaylor/
	 * cosTaylor/sinhTaylor/coshTaylor/logTaylor/logMercator/logHat/logm); bateria de 11 ficheros
	 * (misma de la Decimoctava sesion) con exit codes identicos en ambos builds (5 fallos
	 * preexistentes de TestTaylorSeries01-04/07, mismo stack trace exacto salvo numero de linea);
	 * bateria adicional de 100 ficheros (Eigen/Jordan/Diag/SVD/Schur/Rank/Solve/Syseq/Line/
	 * MatrixOperators/LU/Determinant) con exit codes identicos (solo TestJordan01 exit=1,
	 * preexistente y ya documentado). Scripts conservados: ScratchTimesEqRawVerify01.java,
	 * ScratchTimesDirectCheck01.java, ScratchTimesEqRawBench01.java,
	 * ScratchTimesEqRawWiringVerify01.java (src/TestComplex/).
	 *
	 * 1.66 (2026_0809_1906)
	 * timesEqRaw(MatrixComplex): nuevo producto in-place para el candidato "Camino A" de
	 * rendimiento (Vector API, ver Claude/ComplexArithRev.md). Ataca el cuello de botella real
	 * de los bucles Taylor/Mercator de MatrixComplexFunctions.java, medido en la Decimoctava
	 * sesion (Fase 5): plusEq(Complex) recalcula mod/pha/cre trigonometricamente en CADA termino
	 * sumado del triple bucle O(n^3) de times(MatrixComplex), no solo en el ultimo, que es el
	 * unico que se lee. timesEqRaw() usa Complex.plusEqRaw() (nuevo, Complex.VERSION 1.33) para
	 * los terminos intermedios y Complex.syncPolar() una sola vez por celda de salida -- de
	 * O(rows*cols*inner) a O(rows*cols) llamadas trigonometricas, resultado bit a bit identico.
	 * times()/timesEq() SIN TOCAR (metodo nuevo aparte, cero riesgo para el resto del proyecto,
	 * que sigue usando el camino de siempre). Sin cablear todavia en ningun llamador -- eso es
	 * la fase siguiente (MatrixComplexFunctions.java, 2 acumuladores powMatrix/powMat en los 7
	 * metodos Taylor/Mercator).
	 *
	 * 1.65 (2026_0809_1018)
	 * MatrixComplexUnary.java (clasificacion de signo/tipo de autovalores, 8 sitios): Complex.zero()
	 * -- otro sitio dependiente del modo EXACT/APPROXIMATED global que la investigacion previa (ver
	 * VERSION 1.64) no habia detectado -- sustituido por Complex.zero_treshold_exact() (fijo).
	 * Eliminacion completa (a peticion del usuario) del mecanismo EXACT/APPROXIMATED: Complex.exact()
	 * /Complex.exact(boolean)/EXACT/ZERO_THRESHOLD (agregado conmutado) retirados de
	 * com.ipserc.arith.complex; ZERO_THRESHOLD_APPROX sobrevive como constante fija e independiente
	 * (Syseqnum/sqrtTriangular/setCre() la seguian usando directamente, sin pasar por el modo). Ver
	 * Claude/ComplexArithRev.md, Vigesimosegunda sesion, para el detalle completo.
	 * 1.64 (2026_0809_0938)
	 * MatrixComplexOrtho.normalizeByCols()/normalizeByRows(): el umbral Complex.zero_treshold()
	 * (dependiente del modo EXACT/APPROXIMATED global) sustituido por Complex.zero_treshold_exact()
	 * (fijo) al decidir si un vector es despreciable antes de dividir por su norma en Gram-Schmidt.
	 * Parte del desmontaje del acoplamiento EXACT/APPROXIMATED-a-precision-numerica en toda la
	 * libreria (ver Claude/ComplexArithRev.md, Vigesimosegunda sesion): investigando por que
	 * TestEigenV21 (Complex.exact(false)) daba geometricMultiplicity()=0 para autovalores genuinos
	 * mientras exact(true) funcionaba bien, se confirmo que QRSchurfactor.factorize() deflaciona
	 * via h.getItem(hi,hi-1).isZero() -- en modo APPROXIMATED ese umbral es ~300x mas laxo, la
	 * iteracion QR para antes y cada autovalor calculado queda con un residuo de ~1e-9 a ~1e-8 en
	 * vez de ~1e-14/1e-15. rankNearSingular() (umbral FIJO de ayer, SINGULARITY_REL_TOL=1e-9) ya no
	 * consideraba eso "casi singular" para 3 de los 5 autovalores del caso de TestEigenV21,
	 * dejando geometricMultiplicity()=0. Como isZero() (Complex.java, VERSION 1.31) ahora usa
	 * SIEMPRE ZERO_THRESHOLD_EXACT, QRSchurfactor queda arreglado sin tocar su fichero -- hereda el
	 * fix de isZero(). Verificado con TestEigenV21: ambos modos dan ahora los mismos autovalores,
	 * multiplicidades geometricas y DIAGONALIZABLE.
	 * 1.63 (2026_0809_1200)
	 * Bug real reportado por el usuario: para un autovalor imprecio (residuo de QRSchurfactor), un
	 * autovector podia salir todo NaN, "geom mult:0" y "IS NOT DIAGONALIZABLE" para una matriz que
	 * si lo era -- ver Eigenspace.VERSION 1.14 para el diagnostico completo (7x7 real, confirmado
	 * reproducible). Causa raiz de fondo: inverse()/rank() deciden "es esto cero" comparando contra
	 * un epsilon ABSOLUTO fijo (Complex.equals(0,0)'s ~1e-11) -- para un pivote residual de una
	 * matriz A-lambda*I, el DETERMINANTE completo (producto de TODOS los pivotes) amplifica ese
	 * residuo diminuto muy por encima del epsilon aunque la matriz sea, en la practica, singular;
	 * inverse() entonces intentaba invertir de verdad, desbordando a Infinity/NaN.
	 * inverse(): el chequeo previo "determinant(m).equals(0,0)" sustituido por
	 * MatrixComplexUnary.isNumericallySingular(m) -- razon pivote-menor/pivote-mayor de una sola
	 * pasada triangleUp() contra un umbral RELATIVO (SINGULARITY_REL_TOL=1e-9), calibrado (8 agosto
	 * 2026, ver Claude/ComplexArithRev.md) contra 3 familias de matrices: bien condicionadas (razon
	 * ~0.3-0.6), singulares por construccion (razon 0.0), y casi-singulares por autovalor impreciso
	 * real/sintetico (razon hasta ~2.2e-11 en todos los casos medidos) -- ~10 ordenes de magnitud de
	 * margen entre "genuinamente singular" y "genuinamente bien condicionada" para las matrices que
	 * este proyecto produce en la practica. Verificado sin regresion en una bateria de 67+ ficheros
	 * (Rank/Determinant/Jordan/QRSchur/Diag/Eigenspace/SVD/Schur/Spline/VectorAudit/
	 * MatrixOperators) -- identico byte a byte donde ya funcionaba, incluidas las 2 fallas
	 * preexistentes ya documentadas (TestJordan01 exit=1, TestDeterminant03/05 timeout O(n!)).
	 * Aplicar la MISMA tecnica a rank()/rank1() GLOBALMENTE (probado primero, revertido): rompe
	 * Eigenspace.setEigenvectors() -- su matriz `solutions` (autovectores candidatos) puede mezclar
	 * legitimamente una componente normalizada a 1 con otras genuinamente pequenas, sin una escala
	 * unica coherente; un unico umbral relativo por matriz zonificaba filas reales como "nulas".
	 * rank()/rank1() se dejan SIN TOCAR (siguen sirviendo a sus ~30 llamadores como siempre).
	 * En su lugar, 2 metodos nuevos, deliberadamente acotados a matrices con escala coherente
	 * (A-lambda*I, no una mezcla heterogenea): rankNearSingular() (MatrixComplexUnary.
	 * rankByRelativePivot()) y nullspaceBasisNearSingular() (MatrixComplexKernel.
	 * nullspaceBasisNearSingular(), mismo algoritmo que nullspaceBasis() pero con el mismo test
	 * relativo en vez del isZero() absoluto) -- usados solo por Eigenspace.geometricMultiplicity()/
	 * eigenvectors3() (ver Eigenspace.VERSION 1.14).
	 *
	 * 1.62 (2026_0808_1500)
	 * MatrixComplexPlot: plot()/plotSeries()/doPlot() sustituidos por pares xxxSync/xxxAsync (a
	 * peticion del usuario) -- cada uno llama a un metodo generico, ahora PUBLIC (antes private,
	 * para que Fourier/Laplace/Z puedan llamarlo directamente desde otro paquete) con un parametro
	 * SimpleGnuplot.e_syncMode explicito. Sin alias sin sufijo: los nombres viejos ya no existen,
	 * todos los llamadores del proyecto actualizados (MatrixComplexFunctions.doPlot() -> doPlotSync,
	 * Fourier/Laplace/Z y ~20 ficheros de TestComplex).
	 *
	 * 1.61 (2026_0808_1400)
	 * MatrixComplexPlot: com.panayotis.gnuplot.JavaPlot sustituido por com.ipserc.arith.plot.
	 * SimpleGnuplot (nuevo, sin dependencias externas) en plot()/plotSeries()/doPlot() -- ver la
	 * clase nueva para el detalle completo. Migracion "drop-in": mismas firmas de metodo, mismo
	 * motor gnuplot por debajo, verificado en vivo antes de aplicar (confirmado por el usuario:
	 * ambas ventanas -- estilo MatrixComplexPlot sync y estilo PolynomPlot async -- renderizan
	 * correctamente, sin procesos java/gnuplot residuales tras cerrar).
	 * 1.60 (2026_0808_0845)
	 * MatrixComplexPlot: nuevo plotSeries(String,e_lineStyle,double[][]...) y su sobrecarga con
	 * etiquetas de eje/escala logaritmica (String,String,String,boolean,e_lineStyle,double[][]...),
	 * a peticion del usuario, continuando la extraccion de VERSION 1.59. Consolida la cola de
	 * construccion de JavaPlot (setTitle/addPlot por serie/zeroaxis/style/grid/persist/terminal
	 * windows/plot()) que Fourier.plotSamples/plotSeries/plotCompare/plotDFTsamp/plotDFTfrec
	 * llevaban duplicada a mano -- Laplace/Z ya delegaban correctamente, solo Fourier se habia
	 * quedado atras. El calculo especifico de cada serie (necesita `transform`/`eval()`, estado
	 * privado de Fourier) sigue en Fourier.java sin tocar. De paso, en Laplace.java/Z.java: borrado
	 * codigo ya aparcado (comentado) que duplicaba esta misma logica, superado por la delegacion
	 * existente -- confirmado con el usuario antes de borrar.
	 *
	 * 1.59 (2026_0808_0300)
	 * A peticion del usuario, 2 cambios:
	 * (1) Nueva clase MatrixComplexPlot (publica, no package-private como el resto de las
	 * extracciones de esta reestructuracion, porque Fourier/Laplace/Z -- paquete distinto -- la
	 * llaman): recoge plot(String,int,MatrixComplex,boolean,e_lineStyle) y setLineStyle(), que
	 * Fourier/Laplace/Z llevaban cada una por triplicado (copia byte a byte); y doPlot(), movido
	 * desde MatrixComplexFunctions (el plot de desviacion usado en los bucles de convergencia
	 * Taylor/Mercator). Fourier/Laplace/Z mantienen su propio enum e_lineStyle y su firma publica
	 * intacta -- solo la implementacion delega, convirtiendo el enum en la frontera. Alcance
	 * deliberadamente acotado a lo genuinamente duplicado; los metodos de plot especificos de cada
	 * clase (plotFunction/plotSamples/plotDFTxxx/plotDLTxxx/plotZTxxx, que leen estado privado
	 * propio) se quedan donde estaban, ampliacion aplazada a peticion del usuario.
	 * (2) MatrixComplexFormat.toString(): cada celda se rellena ahora al ancho del item mas largo
	 * de TODA la matriz (no por columna), para que la presentacion salga "cuadrada" -- todas las
	 * columnas quedan apiladas en la misma posicion en cada fila.
	 * Verificado sin regresion: TestQRSchur01 (9/9), TestSVD*, TestVectorAudit01 (28/28),
	 * TestJordanAudit01 (3/3), TestLogmAudit01 (15/15), TestTaylorSeries05/08 (sin fallo).
	 *
	 * 1.58 (2026_0808_0000)
	 * MatrixComplexFunctions.sqrtTriangular(): el caso limite documentado (bloque nilpotente de
	 * autovalor cero repetido, sin raiz cuadrada triangular por ningun metodo -- hecho matematico,
	 * no limitacion del algoritmo) ahora falla explicito con un mensaje especifico en cuanto la
	 * division degenerada esta a punto de ocurrir, en vez de propagar NaN/Infinity hasta el tope
	 * generico de 100 iteraciones de logm(). Verificado con ScratchLogmNilpotentProbe01.java (nuevo)
	 * y TestLogmAudit01.java (15/15, sin regresion, incluye el caso nilpotente ya esperado).
	 *
	 * 1.57 (2026_0807_1600)
	 * MatrixComplexFunctions.doPlot(): a peticion del usuario, antes de "p.plot()" se anade
	 * "p.setPersist(true)" + "p.getPostInit().add(\"set terminal windows\")" -- evita que la ventana
	 * de gnuplot se quede congelada al hacer zoom. Ver Polynom.VERSION 1.14 para el detalle completo
	 * y el resto de ficheros con el mismo parche.
	 *
	 * 1.56 (2026_0807_1500)
	 * dividesleft(MatrixComplex): fixed a copy-paste bug from dividesright(), found while auditing
	 * Spline.java's natural cubic spline. dividesleft() documents "this^-1*cMatrix"; its 1x1 fast
	 * path ("this.rows()==1 && this.cols()==1") returned "cMatrix.inverse().times(this.getItem(0,0))"
	 * -- the exact formula that IS correct for dividesright()'s analogous branch ("this*cMatrix^-1"),
	 * but wrong here: it inverts the wrong operand and swaps the roles, giving a/b instead of b/a for
	 * a trivial 1-equation system "a*x=b". Confirmed with numberOf(): 6*x=-15 (expected x=-2.5) gave
	 * x=-0.4 (=6/-15). This is exactly the path MatrixComplexEquationSystems.solveGauss()'s
	 * DETERMINATE branch uses ("coefMatrix.dividesleft(indMatrix)") -- any 1-unknown linear system
	 * solved via Syseq/solve()/solveGauss() anywhere in the project got the reciprocal-swapped
	 * answer, silently (no exception, no NaN). Also reachable via VectorComplex.baseChg() for a
	 * 1-D basis change. Fixed to "cMatrix.times(this.getItem(0,0).reciprocal())" (this^-1, a scalar,
	 * broadcast-scales cMatrix -- matches dividesright()'s correct branch by symmetry). The second
	 * fast path (cMatrix is 1x1) had the mirror problem -- "this.divides(cMatrix.getItem(0,0))" never
	 * inverts 'this' at all, wrong for a left division -- fixed to
	 * "this.inverse().times(cMatrix.getItem(0,0))"; currently unreachable from solveGauss() (that
	 * branch and the first one coincide exactly when both operands are 1x1, and the first one wins),
	 * fixed anyway since it's the same class of bug in the same method.
	 * Verified against an independent reference (Thomas-algorithm natural cubic spline, no shared
	 * code) via ScratchSplineAudit01.java: the 3-point dataset (1 interior unknown, the case that
	 * exercises this exact path) now matches to ~1e-14 (was off by 0.54). See Spline.VERSION 1.1 for
	 * the 2 fixes in the class that surfaced this.
	 *
	 * 1.55 (2026_0807_1400)
	 * MatrixComplexRank.java (rank0()/majorIL(), extracted helper): fixed a copy-paste bug found
	 * while auditing CombinationNoReps.java -- the loop that converts each cols[] combination
	 * from long[] to int[] used "idx < rowsi.length" instead of "idx < colsi.length". Harmless in
	 * practice (rowsi.length always equals colsi.length, since both come from the same "order"),
	 * but wrong by construction. Extracted the duplicated long[]->int[] conversion (4 identical
	 * copies across rank0()/majorIL()) into a single private toIntArray(long[]) helper, at the
	 * user's explicit request to remove the duplication rather than just patch each copy.
	 * See CombinationNoReps.VERSION 1.1 for the 2 real findings fixed in the class this depends on
	 * (order>grade StackOverflowError, factorial() long overflow for grade>=21) -- neither was
	 * reachable from rank0()/majorIL() today (order<=grade always holds here, grade rarely >=21),
	 * so this entry is the dependent no-op-verification side, not a behavior change for MatrixComplex.
	 *
	 * 1.54 (2026_0806_0400)
	 * Fase 4 of the "*Eq a nivel MatrixComplex" candidate: wired the VERSION 1.53 timesEq(MatrixComplex)
	 * into all 10 times-accumulator call sites of MatrixComplexFunctions.java's 7 Taylor/Mercator methods
	 * (exp_: 1 site; trigonTaylor, shared by sinTaylor/cosTaylor: 3 sites; trigonHyperbolycTaylor, shared
	 * by sinhTaylor/coshTaylor: 2 sites; logTaylor: 1; logMercator: 1; logm: 1; logHat: 1, the only
	 * chained double-product "powMat.timesEq(terMat).timesEq(terMat)"), replacing
	 * "powMatrix = powMatrix.times(x)" with "powMatrix.timesEq(x)" throughout. Same finding as Fase 2:
	 * every powMatrix/powMat accumulator was already a private per-call instance, never the method's own
	 * input parameter, so no aliasing precaution was needed. As documented on VERSION 1.53, this fase
	 * does NOT remove the internal per-iteration complexMatrix allocation (timesEq() is still syntactic
	 * sugar) -- it only removes the outer MatrixComplex variable reassignment/wrapper churn at each of
	 * the 10 call sites.
	 * Verified the same two ways as Fase 2: (1) the fixed-matrix driver (all 9 public delegators on 2
	 * fixed 3x3 matrices, deleted after measuring) -- byte-for-byte identical output against a build from
	 * HEAD; (2) the same 11-file TestComplex/*.java battery -- identical exit codes against a build from
	 * HEAD in all 11 (0 regressions). TestTaylorSeries01 (the one file whose failure path is a fixed,
	 * deterministic matrix, not a random one) produced the exact same stack trace in both builds --
	 * confirms the same pre-existing, unrelated Eigenspace.setEigenvectors() failure as Fase 2, not a new
	 * one from this change.
	 * The "*Eq a nivel MatrixComplex" candidate's 4 fases (design+plusEq/minusEq, wire plusEq, design+
	 * timesEq, wire timesEq) are now all closed. Fase 5 (aggregate verification + Chronometer benchmark
	 * before/after) remains, not started.
	 *
	 * 1.53 (2026_0806_0300)
	 * New timesEq(MatrixComplex): Fase 3 of the "*Eq a nivel MatrixComplex" candidate. Deliberately the
	 * "syntactic sugar" design option (chosen by the user over a buffer-reusing true in-place product):
	 * "this.complexMatrix = this.times(cMatrix).complexMatrix; return this;" -- saves the caller's own
	 * MatrixComplex variable reassignment and wrapper-object churn (e.g. "powMatrix.timesEq(x)" instead
	 * of "powMatrix = powMatrix.times(x)"), but still allocates a full replacement complexMatrix array
	 * internally via times(), since a true in-place product can't overwrite a cell of 'this' while other
	 * cells of the same row/column are still needed for the rest of the product -- that would need a
	 * temporary row/matrix buffer, a possible future step, not this one. Safe even when the argument is
	 * 'this' itself (in-place squaring): times() fully computes the product from the original
	 * complexMatrix before this method reassigns it, so there is no read-after-write hazard.
	 * Verified with a driver (ScratchTimesEqVerify01.java, deleted after measuring): result matches
	 * times() exactly, returns 'this' (same object identity), the argument matrix is left unmutated,
	 * timesEq(this) (in-place squaring) matches times(this), and a chained
	 * timesEq(b).timesEq(b) matches times(b).times(b). Not yet wired into any caller -- the 2
	 * times-accumulators (powMatrix) in MatrixComplexFunctions.java's 7 Taylor/Mercator methods are
	 * Fase 4, still to decide/do.
	 *
	 * 1.52 (2026_0806_0200)
	 * Fase 2 of the "*Eq a nivel MatrixComplex" candidate: wired the VERSION 1.51 plusEq(MatrixComplex)
	 * into all 5 plus-accumulator loops of MatrixComplexFunctions.java's Taylor/Mercator series (exp_,
	 * trigonTaylor -- shared by sinTaylor/cosTaylor, trigonHyperbolycTaylor -- shared by sinhTaylor/
	 * coshTaylor, logTaylor, logMercator, logm, logHat), replacing "acc = acc.plus(term)" with
	 * "acc.plusEq(term)" in each. Each accumulator was already a private per-call instance (freshly
	 * constructed or .copy()'d, never a shared/cached matrix and never the method's own input
	 * parameter 'm'/'normalThis'), so no aliasing precaution was needed this time, unlike VERSION 1.50's
	 * Complex.ZERO case. Eliminates one MatrixComplex (and its backing Complex[][], fully re-allocated
	 * cell by cell) per loop iteration in each of the 7 methods.
	 * Verified two ways: (1) a fixed-matrix driver (ScratchTaylorMercatorVerify01.java, deleted after
	 * measuring) calling all 9 public delegators (exp_, sinTaylor, cosTaylor, sinhTaylor, coshTaylor,
	 * logTaylor, logMercator, logHat, logm) on two fixed 3x3 matrices -- byte-for-byte identical output
	 * against a build from HEAD; (2) the 11-file TestComplex/*.java battery that exercises these methods
	 * -- identical exit codes against a build from HEAD in all 11 (0 regressions). Several of those files
	 * showed content-level diffs on inspection, traced to unseeded random matrix generation
	 * (initMatrixRandomInt(), confirmed in TestTaylorLogExp03a.java) and elapsed-time logging, not to
	 * this change -- confirmed by one deterministic case (TestTaylorSeries01, fixed matrix) producing the
	 * exact same stack trace in both builds for a pre-existing, unrelated failure upstream of these
	 * methods (Eigenspace.setEigenvectors(), reached via exp()'s diagonalization fallback path before
	 * ever calling exp_()).
	 * timesEq(MatrixComplex) for the 2 times-accumulators (powMatrix in all 7 methods) remains deferred
	 * to its own fase, per the design note already on VERSION 1.51.
	 *
	 * 1.51 (2026_0806_0100)
	 * New in-place matrix arithmetic: plusEq(MatrixComplex)/minusEq(MatrixComplex), mirroring the
	 * Complex.plusEq()/minusEq() accumulator idiom one level up. Mutates 'this' cell by cell via the
	 * existing Complex.plusEq()/minusEq() (never reassigns this.complexMatrix, never allocates a new
	 * MatrixComplex), reads but never mutates the argument. Fase 1 of the "*Eq a nivel MatrixComplex"
	 * candidate (ver ComplexArithRev.md, Decimoctava sesion): wires into the 5 plus-accumulator loops
	 * of MatrixComplexFunctions.java's Taylor/Mercator methods (exp_, trigonTaylor,
	 * trigonHyperbolycTaylor, logTaylor, logMercator, logm, logHat) in a later fase. timesEq(MatrixComplex)
	 * deliberately deferred to its own fase: a true in-place matrix product needs an internal buffer
	 * (can't overwrite this.complexMatrix[i][j] while other cells of the same row/col are still needed),
	 * unlike plusEq/minusEq which are safely elementwise.
	 *
	 * 1.50 (2026_0806_0000)
	 * MatrixComplexFunctions.sqrtTriangular() (private helper of logm()'s inverse
	 * scaling-and-squaring): its off-diagonal Parlett-recurrence sum loop did
	 * "Complex sum = Complex.ZERO; ... sum = sum.plus(...)" -- same allocation-per-iteration
	 * pattern as times(MatrixComplex) before VERSION 1.49, now fixed the same way (sum.plusEq(...)
	 * into a private zero-valued accumulator). Deliberately NOT a naive "swap plus() for plusEq()"
	 * on the existing sum variable: plusEq() mutates its receiver in place, and the old code started
	 * the accumulator from Complex.ZERO, the library's shared zero constant -- calling plusEq()
	 * directly on that would have corrupted it for every other caller in the JVM (the exact danger
	 * plusEq()'s own Javadoc warns about). Changed the initial value to a private "new Complex()"
	 * first, so the accumulator is safe to mutate. Verified byte-for-byte identical logm() output
	 * against a build from HEAD on a fixed 4x4 defective matrix, plus a 55-file battery (every
	 * TestComplex file calling exp/sin/cos/tan/log/logm/sqrt family functions) against a build from
	 * HEAD: 0 regressions from this change. 2 apparent mismatches investigated and confirmed
	 * unrelated: TestLaplace01 (same pre-existing determinantAdj()-adjacent timeout flakiness as
	 * VERSION 1.49's battery) and TestTaylorSeries08 (a genuinely pre-existing bug in the TEST file
	 * itself, not this class -- see that file's own history for the fix, confirmed present at a
	 * similar failure rate in BOTH builds over 20 runs each before being fixed there, unrelated to
	 * this change).
	 *
	 * 1.49 (2026_0805_2300)
	 * times(MatrixComplex) (the O(n^3) matrix-product hot path): the inner loop used to do
	 * resultMatrix.complexMatrix[rowf][colf] = resultMatrix.complexMatrix[rowf][colf].plus(
	 * this.complexMatrix[rowf][iter].times(cMatrix.complexMatrix[iter][colf])) -- 2 new Complex
	 * allocations per iteration (times() and plus()) plus a redundant array read+store every
	 * iteration. Now accumulates in place via plusEq() into the zero-initialized cell the
	 * constructor already allocated -- same accumulator idiom already used throughout
	 * ComplexFunctions.java (acc.plusEq(a.times(b))), just applied to this class' own hot path.
	 * Halves the allocations in the inner loop (only the times() product remains) and drops the
	 * per-iteration array store. Candidate from the "double[]/Vector API" performance roadmap
	 * (Rol 1-2, ver ComplexArithRev.md) -- deliberately the smallest possible first step: no
	 * layout change, no JDK/module changes (jdk.incubator.vector needs the project's compiler
	 * compliance raised from 1.8 first, a separate decision). Measured with a Chronometer-based
	 * driver (matrices 50-300 square, 10 reps, warmup): ~5-15% wall-time reduction depending on
	 * size, plus visibly less run-to-run variance (fewer allocations -> less GC pressure) -- the
	 * expected, modest gain for removing allocations while the per-add trigonometric recompute
	 * (setPolCoord() inside plusEq()) still dominates the cost, unaffected by this change. Verified
	 * byte-for-byte identical output against a build from HEAD on a fixed 4x4*4x4 product, plus a
	 * 143-file battery (every TestComplex file that calls .times()) comparing exit codes against a
	 * build from HEAD: 0 real regressions (2 apparent mismatches, TestDeterminant01/TestLaplace01,
	 * traced to pre-existing timeout flakiness in determinantAdj()'s O(n!) cofactor expansion on an
	 * unseeded random 11x11 matrix -- confirmed unrelated: the mismatches occurred in BOTH
	 * directions across the two files, and a standalone timing of determinantAdj() alone, which
	 * never calls this method, already sits at ~8.6s, right at the battery's 10s per-file timeout).
	 *
	 * 1.48 (2026_0805_1400)
	 * bestNumDecs()'s ceiling lowered from Complex.getSignificative() (general library precision,
	 * usually 8) to a new BEST_NUM_DECS_CAP=5 -- reintroduces, measured this time, a restriction
	 * the user had applied by hand in the past specifically to stop a genuinely repeated eigenvalue
	 * from being grouped as several distinct eigenvalues of multiplicity 1 each (root cause: a
	 * repeated root only converges to about machine_epsilon^(1/m) via Durand-Kerner/Aberth-Ehrlich,
	 * which cond()/2 alone doesn't capture, so the old 8-decimal ceiling was almost always too
	 * optimistic once cond() was even moderately large). Confirmed real, not just theoretical: the
	 * same root cause behind both Jordan.checkReconstruction() failures documented in Jordan.VERSION
	 * 1.4. Measured with a 75-case synthetic sweep (known multiplicities 2-4, well-conditioned
	 * random P, real root-finding pipeline) before committing to a value: the old cap misgroups
	 * 43/75; BEST_NUM_DECS_CAP=5 fixes every multiplicity-2 case (0 remaining failures there) with
	 * zero new false merges in a companion 63-case distinct-eigenvalue sweep (a looser cap of 6
	 * gave identical results, so 5 -- the user's original value -- was kept rather than loosened
	 * further). Multiplicity 3+ still fails about as often as before -- that residual is NOT this
	 * bug: it's eigenvector CONSTRUCTION collapsing to a near-zero generalized eigenvector once the
	 * grouped eigenvalue is used to build (A-eigenval*I)^k (confirmed with a dedicated geometric-
	 * multiplicity-1 case that groups correctly under this fix but still fails at that later step,
	 * residual ~1.6e-8) -- the same already-documented precision ceiling that needs
	 * QR-con-desplazamientos, a DIFFERENT stage of the pipeline than this fix touches. Verified
	 * against a 57-file battery (every TestComplex file referencing Diagfactor/Jordan/Eigenspace/
	 * Schurfactor/logm/QRSchur) against a build from HEAD: 0 exit-code mismatches.
	 *
	 * 1.47 (2026_0805_1100)
	 * CHARACTERISTIC POLYNOMIAL section (422 lines: charactPoly, the augment/augment1/augment2/
	 * unkMatrix family, cofactor/cofactors/minor, coefCP, the quicksort/quicksortdown/quicksortup
	 * family, hermitian/skewHermitian/commutator/anticommutator) extracted to new package-private
	 * MatrixComplexCharPoly, same pattern as MatrixComplexUnary (Etapa 4) -- a single class, no
	 * sub-phases, since the section (422 lines, well under Etapa 4's 867) didn't justify splitting
	 * despite its subgroups having low cross-cohesion. Etapa 5 of the multi-session MatrixComplex.java
	 * restructuring roadmap (ver ComplexArithRev.md). No field or method visibility widened -- the one
	 * cross-class dependency anticipated when Etapa 4 closed (cofactor() called from
	 * MatrixComplexUnary.adjugate()) resolves through the already-public m.cofactor()/m.cofactors(...)
	 * delegators, same direction as every previous cross-section call. Public API unchanged; verified
	 * byte-for-byte against HEAD (19 methods/overloads x 7 representative matrices: real/complex/
	 * singular/Hermitian/2x2/4x4/rectangular 3x2, including exception-path cases -- non-square
	 * cofactor(), dimension-mismatched augment(MatrixComplex)/commutator(), out-of-range coefCP(order))
	 * plus a 56-file regression battery (every TestComplex file referencing any moved method), 0
	 * exit-code mismatches (4 pre-existing non-deterministic/known failures -- TestEigenV16,
	 * TestSyseqnum01/02, TestTaylorSeries01 -- reproduced identically, exit code 1, in both builds).
	 *
	 * 1.40 (2026_0803_1900)
	 * EQUATION SYSTEMS section, sub-fase A+B (classification/resolution + Gauss/Cramer/submatrices,
	 * ~808 lines) extracted to new package-private MatrixComplexEquationSystems, same pattern as
	 * MatrixComplexFormat (Etapa 1)/MatrixComplexFunctions (Etapa 2). HEADINFO widened private ->
	 * package-private (used throughout the moved code, same as trace()/__log10__ in Etapa 2);
	 * intSplit(String,String) and copyCol(MatrixComplex,int) (both COLS & ROWS OPERATIONS, staying
	 * in the core) widened the same way for the same reason. The INCONSISTENT/INDETERMINATE/
	 * DETERMINATE constants stay declared here (public API), referenced fully qualified from the
	 * new class. isNullSolution(int,int) (private, confirmed zero callers anywhere in the project,
	 * pre-existing dead code per the saved restructuring plan) moved verbatim, unchanged. Etapa 3
	 * sub-fase A+B of the multi-session MatrixComplex.java restructuring roadmap (Duodecima sesion,
	 * ver ComplexArithRev.md) -- Syseq.java confirmed to only call public methods of this core
	 * (typeEqSys, solveGauss, isHomogeneous, completeEqSys, solve, coefMatrix, indMatrix,
	 * constMatrix), extraction transparent to it. Public API unchanged; verified byte-for-byte
	 * against HEAD (~35 method calls over 6 representative matrices: determinate/indeterminate/
	 * rectangular/inconsistent/homogeneous/complex, including exception-path cases) plus the full
	 * 73-file regression battery (equation-system-specific + standard), 0 exit-code mismatches
	 * (residual stdout differences confirmed pre-existing Math.random()-decoration/timing/unseeded-
	 * random-matrix noise, same as documented in Decima/Undecima sesion -- verified by diffing the
	 * unmodified reference binary against itself).
	 *
	 * 1.39 (2026_0803_0130)
	 * Fixed both bugs found incidentally in VERSION 1.38 (both explicitly requested by the user,
	 * not deferred): (1) static ccos(MatrixComplex) used Complex.sin() instead of Complex.cos()
	 * for every entry -- now matches ccos()'s (correct) instance body; verified diff==0 against
	 * the instance method. (2) MatrixComplexFunctions.logMercator() had no iteration cap (still
	 * used raw Complex.digits(), ~10^13) -- added LOG_MERCATOR_MAX_ITER=10000 (same value/rationale
	 * as logTaylor()'s LOG_TAYLOR_MAX_ITER, VERSION 1.36) plus an explicit "did not converge"
	 * exception, same pattern. Confirmed real hang before the fix (the nilpotent "0,1;0,0" case,
	 * same boundary as logTaylor()'s); now throws in ~40ms. Convergent-case regression (matches
	 * log()) and full test battery unaffected -- see ComplexArithRev.md for detail.
	 *
	 * 1.38 (2026_0803_0100)
	 * TAYLOR'S SERIES section (exp/exp_, sin/cos/tan families incl. Taylor/Euler/item-to-item
	 * variants, euler, sinh/cosh/tanh families, logTaylor/logMercator/logHat/logm/log,
	 * llog/log10/llog10/logbase family) extracted to new package-private MatrixComplexFunctions,
	 * same pattern as MatrixComplexFormat (Etapa 1). trace(String/MatrixComplex/Complex) widened
	 * private->package-private (used throughout the moved code); __log10__ widened the same way;
	 * doPlot(String,double[][],int) moved in (all 8 call sites were in this section) instead of
	 * just widened, using the already-public doPlot()/debug() getters for its flag checks. Item-
	 * to-item static twins (ssin(MatrixComplex) etc.) and the 3 power(...) statics were left
	 * UNTOUCHED in this class -- they don't depend on anything moved, and moving them too would
	 * have collided in signature with their own instance-derived counterpart in the new class.
	 * Etapa 2 of the multi-session MatrixComplex.java restructuring roadmap (Undecima sesion, ver
	 * ComplexArithRev.md). Public API unchanged; verified byte-for-byte against HEAD (65 method
	 * calls x 7 matrices) plus the full regression battery, 0 behavior changes (only expected
	 * extra stack frames on the tests that exercise thrown exceptions).
	 * Two PRE-EXISTING bugs found incidentally while moving code, NOT fixed here (analysis only,
	 * per usual practice): (1) logMercator() has no iteration cap -- same LOG_TAYLOR_MAX_ITER-style
	 * hang logTaylor() used to have (fixed VERSION 1.36) for a boundary-case matrix (e.g. the
	 * nilpotent "0,1;0,0"), confirmed still present, unrelated to this move; (2) the static
	 * ccos(MatrixComplex) item-to-item method computes Complex.sin() instead of Complex.cos() for
	 * every entry -- confirmed present verbatim in this class, unrelated to this move.
	 *
	 * 1.37 (2026_0802_2230)
	 * PRINTING section (print/println/toString/toMaxima/toWolfram/toMatlab/toOctave/
	 * preMatrixComplex/toMatrixComplex) extracted to new package-private MatrixComplexFormat,
	 * same pattern as PolynomFormat (Decima sesion) -- MatrixComplex.java's own methods keep their
	 * exact signatures, delegating in one line each. Etapa 1 of the multi-session MatrixComplex.java
	 * restructuring roadmap (Undecima sesion, ver ComplexArithRev.md). No visibility widening needed
	 * (isEmpty() was already public). Public API unchanged.
	 *
	 * 1.36 (2026_0802_1800)
	 * logTaylor(): new LOG_TAYLOR_MAX_ITER=10000 cap (was effectively Complex.digits()'s
	 * 10^precision, never meant to be reached) plus an explicit "did not converge" exception when
	 * the iteration cap is exhausted -- fixes the hang confirmed for a nilpotent matrix (see the
	 * constant's own Javadoc). VERSION bump missed in the commit that made this change; added here
	 * as a tiny follow-up, no further code change.
	 *
	 * 1.35 (2026_0802_1700)
	 * log(): the defective (non-diagonalizable) branch now tries logm() (Schur + inverse
	 * scaling-and-squaring, any eigenvalue orientation) first, falling back to logTaylor() (narrow
	 * convergence range, close to +||A||) only on explicit failure. Connects logm() into the
	 * dispatcher for the first time -- was deliberately left unconnected since the Novena sesion.
	 * Confirmed real case fixed: a defective 2x2 block (lambda=-50, P not orthogonal) that made
	 * log() throw outright now resolves cleanly via logm() (exp(log(A)) matches A to ~1.5e-9).
	 *
	 * 1.34 (2026_0802_1400)
	 * New public nullspaceBasis(): Gauss-Jordan elimination to reduced row echelon form, tracking
	 * pivot vs. free columns explicitly, returning one independent basis vector per free column
	 * (nullity() rows) -- unlike kernel()/kernel(Complex), which return a SINGLE vector (all free
	 * variables set to the same scalar), the right tool only when the nullspace is 1-dimensional.
	 * Fase A of generalizing Jordan.java beyond geometric multiplicity 1 (ver ComplexArithRev.md).
	 *
	 * 1.33 (2026_0802_0131)
	 * rank2() now solves A'*A's characteristic polynomial via Polynom.solveRobust() instead of
	 * solve() (Durand-Kerner only) -- fixes the "Fail prone due to lack precision" fragility this
	 * method's own Javadoc had warned about (comment now removed, no longer true): confirmed with
	 * a 1200-random-matrix battery that Durand-Kerner alone threw an arithmetic-overflow exception
	 * 74-100% of the time for 5x5+ matrices; solveRobust() (try Durand-Kerner, fall back to
	 * Aberth-Ehrlich only on overflow) gives 0 exceptions and identical rank to rank1()/brute-force
	 * ground truth in every case, with no behavior change on the cases that already worked.
	 *
	 * 1.32 (2026_0801_2318)
	 * New public logm(): natural logarithm of a defective (non-diagonalizable) matrix via Schur
	 * factorization + inverse scaling-and-squaring (MATLAB logm/Higham) -- fills the gap log()
	 * falls back to logTaylor() for, which only converges for a narrow dominant-eigenvalue
	 * orientation. Uses sqrtTriangular() (VERSION 1.31) plus logMercator()'s convergence-loop body
	 * reused without its own norm-reduction preprocessing. Verified against 14 cases: a closed-form
	 * oracle for single Jordan blocks (log(λI+N)=log(λ)I+Σ(-1)^(k+1)(N/λ)^k/k, finite since N is
	 * nilpotent) across real/negative-real/complex/small/large eigenvalues; regression against
	 * log() on diagonal/diagonalizable matrices; exp(logm(A))~=A self-consistency for multi-block
	 * synthetic defective matrices; a genuinely singular (nilpotent) matrix failing cleanly instead
	 * of hanging. NOT yet wired into log()'s dispatcher -- deliberate scope decision, see the
	 * Novena sesion plan.
	 * Newly characterized while verifying (not caused by this change, pre-existing): Schurfactor's
	 * internal Eigenspace.eigenvectors3() hits the same "imprecise Durand-Kerner eigenvalue keeps
	 * (A-eigenval*I) from being quite singular, homogeneous solve collapses to the trivial all-zero
	 * vector" failure mode already diagnosed in Jordan.java this session, for REPEATED eigenvalues
	 * at larger block sizes (confirmed with a sweep: e.g. n=4 fails for lambda=3/-3/-1/50/-50, only
	 * lambda=1 works; n=3 fails only for lambda=50). Distinct eigenvalues (diagonalizable case) are
	 * unaffected at any size tested. logm() detects this correctly via Schurfactor.factorized()
	 * and throws, doesn't produce garbage -- but it does mean logm()'s practical domain for
	 * genuinely defective matrices is narrower than hoped until Eigenspace's generic eigenvector
	 * solver gets the same kind of fix Jordan.java got this session. Documented, not fixed here --
	 * separate, larger-scope decision.
	 * New private sqrtTriangular(MatrixComplex): principal square root of an upper triangular
	 * matrix via the Parlett recurrence (Björck-Hammarling), the standard building block for
	 * scaling-and-squaring logm() -- see the pending Novena sesion plan. Diagonal via
	 * Complex.sqrt()'s principal branch; off-diagonal solved diagonal-by-diagonal from S*S=T.
	 * Verified against 7 hand-built/self-consistency cases (real/complex/repeated eigenvalues up
	 * to 5x5, including a genuinely negative real eigenvalue). KNOWN LIMITATION, documented not
	 * fixed: divides by S_ii+S_jj, which under THIS project's principal branch (Re>=0 always) can
	 * only vanish for a repeated ZERO eigenvalue within the same Jordan chain (a nontrivial
	 * nilpotent block genuinely has no triangular square root) -- verified the naive "opposite
	 * eigenvalues" case from the generic literature does NOT actually degenerate here
	 * (T=diag(4,-4) gives S_00+S_11=2+2i, nowhere near zero).
	 *
	 * 1.30 (2026_0801_1600)
	 * orthonormalize() normalized the wrong axis: this.orthogonalize().normalizeByCols() should
	 * have been normalizeByRows(). orthogonalize() (Gram-Schmidt via gramSchmidt()) leaves the
	 * orthogonal basis vectors in the ROWS of the result (confirmed: gramSchmidt() builds each
	 * vector as a column of an internal matrix, then returns it transposed), so normalizing by
	 * columns left the actual basis vectors untouched -- confirmed with A=[[4,1],[2,3]]: before
	 * the fix, U*.U-I had error ~0.6 (not unitary at all); after, ~2.2e-16 (machine precision).
	 * Only 2 callers in the whole project: Schurfactor.java (the Schur decomposition used to
	 * build a matrix U1 to complete an eigenvector to an orthonormal basis -- was silently
	 * producing a non-unitary U1, so every downstream Schur factorization was wrong) and
	 * TestGram06.java (never checked unitarity, which is why this went undetected). Verified
	 * with a 9-matrix battery covering diagonal, well-conditioned non-diagonal (real/complex),
	 * and the defective Jordan case that matters for the pending logm work (A=P*J*P^-1,
	 * J=[[50,1],[0,50]], P not orthogonal, not already triangular): Schurfactor now factorizes
	 * it correctly (U unitary to 3e-16, reconstruction exact to 2e-14).
	 *
	 * 1.29 (2026_0801_0932)
	 * solveGauss()'s INCONSISTENT branch now throws IllegalArgumentException instead of
	 * returning a NaN/Infinity marker (follow-up to the Hallazgo 3 SCOPE DECISION of 1.26 --
	 * Syseq.java was fixed first, see Syseq.VERSION 1.6, to stop depending on that marker).
	 * Verified no other real caller (MatrixComplex.kernel(), Jordan.java, geom/Plane.java,
	 * Polynom.java's Vandermonde interpolation) depends on it either -- all solve either a
	 * homogeneous system (always consistent) or one geometrically/algebraically guaranteed
	 * consistent whenever actually reached.
	 *
	 * 1.28 (2026_0801_0915)
	 * removeDuplicateRows() had an ungated System.out.println() logging every duplicate row it
	 * dropped, unconditionally, in normal (non-debug) use. Switched to trace() (gated behind
	 * the class's __DEBUG__ flag, false by default), same convention already used everywhere
	 * else in this file. With this commit, the 5 EQUATION SYSTEMS findings of the Octava
	 * sesion are closed (Hallazgo 3 documented, not code-changed; the other 4 fixed).
	 *
	 * 1.27 (2026_0801_0912)
	 * typeEqSys() misclassified a homogeneous system of full column rank as INDETERMINATE
	 * instead of DETERMINATE. Full column rank always means exactly one solution (the trivial
	 * x=0 for a homogeneous system), not a free parameter.
	 *
	 * 1.26 (2026_0801_0906)
	 * Javadoc only, no behavior change: documents why solveGauss()'s INCONSISTENT branch keeps
	 * returning a NaN/Infinity marker instead of throwing -- Syseq.solveq() genuinely depends
	 * on it (calls solve() before checking typeEqSys()). SCOPE DECISION, deferred at the
	 * user's explicit request; a real fix would also need to touch Syseq.java.
	 *
	 * 1.25 (2026_0801_0858)
	 * solveGauss()/solveCramer() throw IllegalArgumentException on a shape they can't handle
	 * instead of continuing in silence: solveGauss() for an over-determined system (more
	 * equations than unknowns -- the trace()-only guard was a no-op by default, __DEBUG__=false);
	 * solveCramer() for any non-square coefficient shape (println without return/throw before).
	 *
	 * 1.24 (2026_0801_0854)
	 * solveGauss()/nbrOfSolutions() completed a genuinely rectangular system (fewer equations
	 * than unknowns) internally via completeEqSys() instead of silently returning an empty
	 * solution matrix / 0. Only for the under-determined case; over-determined systems are
	 * left untouched (completeEqSys() would truncate, not pad, extra equations).
	 *
	 * 1.23 (2026_0801_0100)
	 * trigonTaylor(): trampa explicita de NaN/Infinity (hallazgo colateral del
	 * hallazgo 6, descubierto al investigar el cuelgue de sinTaylor() con Jordan
	 * [[80,1],[0,80]]): para autovalores grandes, powMatrix desborda el rango de
	 * double antes de que la serie converja o dispare cualquiera de los 2 detectores
	 * ya existentes (divergencia del hallazgo 5, cancelacion del hallazgo 6) -- y una
	 * vez aparece NaN, AMBOS quedan ciegos, porque toda comparacion numerica contra
	 * NaN es falsa en IEEE754 (deviation>1 falso, finalNorm<noiseFloor falso). Antes:
	 * sinTaylor() en ese Jordan tardaba 83s en agotar ~10^7 iteraciones y devolvia
	 * NaN+NaNi sin avisar. Ahora: excepcion inmediata en 13ms, en el momento exacto
	 * en que el NaN/Infinity aparece por primera vez (confirmado: powMatrix.norm()
	 * ya es NaN en k=163, antes de que fact desborde en k=171). Confirmado con
	 * barrido de autovalores 1..100: sin excepcion hasta ~20, cancelacion catastrofica
	 * 35-60, desbordamiento a partir de ~80, sin cuelgues en todo el rango. sin()/
	 * cos() publicos (via Euler/exp(), que si escala) dan el resultado exacto en el
	 * mismo Jordan sin tocar este codigo -- no estan expuestos, igual que el hallazgo 6.
	 *
	 * 1.22 (2026_0731_2400)
	 * Cancelacion numerica catastrofica detectada en trigonTaylor() (auditoria
	 * matematica, hallazgo 6): sinTaylor()/cosTaylor() lanzan IllegalArgumentException
	 * si las sumas parciales de la serie alternante superaron el resultado final por
	 * encima del suelo de ruido de double precision (maxPartialNorm*k*eps*10, con
	 * cushion de 10x), en vez de devolver basura numerica en silencio -- mismo
	 * espiritu que los hallazgos 3 y 5. Confirmado con Jordan [[50,1],[0,50]]:
	 * sinTaylor() pasaba de -31194.8 (exacto: -0.2624) a lanzar excepcion; el propio
	 * test preexistente TestTaylorSeries02/03/04.java confirma el bug en produccion,
	 * sin relacion con mi caso sintetico (aMatrix=[32.0] escalar: Sin Taylor=0.552733
	 * frente a Sin Euler=0.551427, exacto, con Sin^2+Cos^2=1.0036 en vez de 1.0). La
	 * API publica sin()/cos() no esta expuesta (usan sinEuler()/cosEuler() desde el
	 * hallazgo 4); sinh()/cosh() tampoco (sin cancelacion, terminos de un solo signo).
	 *
	 * 1.21 (2026_0731_2330)
	 * Detectores de divergencia en trigonTaylor()/trigonHyperbolycTaylor()/exp_()
	 * (auditoria matematica, hallazgo 5): mismo patron ya usado en logTaylor/
	 * logMercator/logHat (accumulator>500 tras k>100 -> IllegalArgumentException)
	 * en vez de iterar en silencio hasta maxIter (~10^8). exp_() ademas convierte
	 * el "if (errMatrix.isNaN()) break;" ya existente (silencioso) en excepcion.
	 * Verificado sin cambio de comportamiento en casos bien condicionados.
	 *
	 * 1.20 (2026_0731_2230)
	 * normalize2PI() eliminado (auditoria matematica, hallazgo 4): reducia la
	 * matriz por su norma euclidea global antes de sin()/cos()/tan()/euler(),
	 * invalido matematicamente -- la periodicidad de sin/cos es por autovalor,
	 * no por la norma conjunta de la matriz. Corrompia silenciosamente sin()/
	 * cos()/tan() para matrices no diagonalizables con norma grande. Quitada la
	 * llamada de trigonTaylor()/sinEuler()/cosEuler()/euler()/euler(MatrixComplex);
	 * sin()/cos() pasan a usar sinEuler()/cosEuler() (formula de Euler via exp(),
	 * ya correcto) como fallback en vez de sinTaylor()/cosTaylor(), igual que ya
	 * hacia tan()/tanEuler(). normalize2PI() eliminado del todo (sin llamadores).
	 *
	 * 1.19 (2026_0731_2130)
	 * logTaylor()/logMercator()/logHat() (auditoria matematica, hallazgo 3): el
	 * detector de divergencia ya existente (accumulator>500) devolvia
	 * this.divides(Complex.ZERO) -- Infinity/NaN en silencio -- al dispararse.
	 * Ahora lanza IllegalArgumentException. No arregla la convergencia en si
	 * (decision de alcance explicita: el fix real, scaling-and-squaring inverso,
	 * queda aplazado para otra sesion), solo deja de devolver basura sin avisar.
	 *
	 * 1.18 (2026_0731_2030)
	 * Pivoteo parcial proactivo en inverse()/triangleUp()/triangleLo() (auditoria
	 * matematica, paso 3, hallazgo 2): antes solo se pivotaba si el pivote actual
	 * era EXACTAMENTE cero; ahora siempre se pivota a la fila de mayor modulo de
	 * la columna, evitando amplificacion de error de redondeo con pivotes pequeños
	 * pero no nulos. Nuevo helper privado partialPivotUp(int) (espejo de
	 * partialPivot(int), para triangleLo()).
	 *
	 * 1.17 (2026_0731_1900)
	 * Los 25 System.exit(1) del fichero sustituidos por IllegalArgumentException
	 * (auditoria matematica, paso 3): plus/minus (dimensiones), power(MatrixComplex),
	 * exp/trigonTaylor/trigonHyperbolycTaylor/logTaylor/logMercator/logHat (no cuadrada),
	 * p_norm (orden<=0), trace/cotrace (no cuadrada), minor/cofactors (indice de pivote),
	 * determinantGauss/determinant3/determinantAdj (no cuadrada), subMatrix/subMatrixAug
	 * (rango fuera de la matriz), charactPoly/cofactor (no cuadrada).
	 *
	 * 1.16 (2025_0131_2358)
	 * 	private static double __log10__ = 2.30258509299405;
	 * 	public static MatrixComplex log(MatrixComplex matrix)
	 * 	public static MatrixComplex log10(MatrixComplex matrix)
	 * 	public String toString()
	 * 	public MatrixComplex sin()
	 * 	public MatrixComplex cos()
	 * 	public MatrixComplex tan()
	 * 	public MatrixComplex sinh()
	 * 	public MatrixComplex cosh()
	 * 	public MatrixComplex tanh()
	 * 	public MatrixComplex log()
	 * 	public MatrixComplex logTaylor()
	 * 	public MatrixComplex logMercator()
	 * 	public MatrixComplex exp()
	 * 	public MatrixComplex exp_()
	 * 	public MatrixComplex minusMat(Complex cNum)
	 * 	public MatrixComplex minusMat(double dNum)
	 * 	public MatrixComplex minusMat(int iNum)
	 * 	public MatrixComplex plusMat(Complex cNum)
	 * 	public MatrixComplex plusMat(int iNum)
	 * 	public MatrixComplex log()
	 * 	public MatrixComplex ppower(int iExp)
	 * 	public MatrixComplex ppower(double dExp)
	 * 	public MatrixComplex ppower(Complex cExp)
	 * 	public MatrixComplex ssin()
	 * 	public static MatrixComplex ssin(MatrixComplex matrix)
	 * 	public MatrixComplex ccos()
	 * 	public static MatrixComplex ccos(MatrixComplex matrix)
	 * 	public MatrixComplex ttan()
	 * 	public static MatrixComplex ttan(MatrixComplex matrix)
	 * 	public MatrixComplex ssinh()
	 * 	public static MatrixComplex ssinh(MatrixComplex matrix)
	 * 	public MatrixComplex ccosh()
	 * 	public static MatrixComplex ccosh(MatrixComplex matrix)
	 * 	public MatrixComplex ttanh()
	 * 	public static MatrixComplex ttanh(MatrixComplex matrix)
	 * 	public MatrixComplex llog()
	 * 	public static MatrixComplex llog(MatrixComplex matrix)
	 * 	public MatrixComplex llog10()
	 * 	public static MatrixComplex llog10(MatrixComplex matrix)
	 * 	public MatrixComplex llogbase(Complex base)
	 * 	public MatrixComplex llogbase(double base)
	 * 	public static MatrixComplex llogbase(MatrixComplex matrix, double base)
	 *	public MatrixComplex minor(int rowPivot, int colPivot) {
	 *	public MatrixComplex cofactors(int rowPivot, int colPivot) {
	 *	public MatrixComplex orthogonalize()
	 *	public MatrixComplex orthonormalize()
	 *  public MatrixComplex kroneckerprod(MatrixComplex matrix) {
	 * 
	 *
	 * 1.15 (2024_0408_1400)
	 *  private MatrixComplex trigonTaylor(int sign)
	 * 	private enum hyptrigon {SINH, COSH};
	 *  private MatrixComplex trigonHyperbolycTaylor(hyptrigon hypFunc) {
	 *  public MatrixComplex sinhTaylor() {
	 *  public MatrixComplex coshTaylor() {
	 *  public MatrixComplex completeRows() {
	 * 
	 * 1.14 (2024_0320_1945)
	 * 	public MatrixComplex log10() {
	 * 	public static MatrixComplex log10(MatrixComplex matrix) {
	 * 	public MatrixComplex logbase(Complex base) {
	 * 	public static MatrixComplex logbase(MatrixComplex matrix, Complex base) {
	 * 	public MatrixComplex logbase(Complex base) {
	 * 	public static MatrixComplex logbase(MatrixComplex matrix, Complex base) {
	 * 	public static MatrixComplex power(Complex cBase, MatrixComplex exponent) {
	 * 	public static MatrixComplex power(double base, MatrixComplex exponent) {
	 * 	private MatrixComplex trigonTaylor(int sign) {
	 *  public MatrixComplex sinTaylor() {
	 *  public MatrixComplex sinEuler() {
	 *  public MatrixComplex cosTaylor() {
	 *  public MatrixComplex cosEuler() {
	 * 	public MatrixComplex euler() {
	 * 	public static MatrixComplex euler(MatrixComplex matrix) {
	 * 	public MatrixComplex normalize2PI() {
	 *  REMOVED private MatrixComplex log1mx() {
	 *  REMOVED private MatrixComplex log1px() {
	 *  public MatrixComplex logTaylor() {
	 *  public MatrixComplex logMercator() {
	 *  public MatrixComplex power(MatrixComplex mcExpo) {
	 *  public static MatrixComplex power(MatrixComplex mcBase, MatrixComplex mcExpo) {
	 * 
	 *  
	 * 1.13 (2024_0120_2330)
	 *  public MatrixComplex exp() {
	 *  public MatrixComplex sinTaylor() {
	 *  public MatrixComplex sinEuler() {
	 *  public MatrixComplex sin() {
	 *  public MatrixComplex cosTaylor() {
	 *  public MatrixComplex cosEuler() {
	 *  public MatrixComplex cos() {
	 *  public MatrixComplex tanTaylor() {
	 *  public MatrixComplex tanEuler() {
	 *  public MatrixComplex tan() {
	 *  public MatrixComplex sinhTaylor() {
	 *  public MatrixComplex sinhEuler()
	 *  public MatrixComplex sinh() {
	 *  public MatrixComplex coshTaylor() {
	 *  public MatrixComplex coshEuler() {
	 *  public MatrixComplex cosh() {
	 *  public MatrixComplex tanhTaylor() {
	 *  public MatrixComplex tanhEuler() {
	 *  public MatrixComplex tanh() {
	 *  private MatrixComplex log1mx() {
	 *  private MatrixComplex log1px() {
	 *  public MatrixComplex logTaylor() {
	 *  public MatrixComplex logMercator() {
	 *  public MatrixComplex logHat() {
	 *  public MatrixComplex log() {
	 *  public boolean sameDimension(MatrixComplex matrix) {
	 *  public Complex totalize() {
	 *  public boolean isGT(MatrixComplex matrix) {
	 *  public boolean isGTE(MatrixComplex matrix) {
	 *  public boolean isLT(MatrixComplex matrix) {
	 *  public boolean isLTE(MatrixComplex matrix) {
	 *  public boolean isPostiveSemiDefinite() {
	 *  public boolean isNegtiveDefinite() {
	 *  public boolean isNegtiveSemiDefinite() {
	 *  public boolean hasZeroMainDiag() {
	 *  public boolean repPositiveMainDiag() {
	 *  public MatrixComplex plusMat(Complex cNum) {
	 *  public MatrixComplex plusMat(String strcNum) {
	 *  public MatrixComplex plusMat(double rep, double imp) {
	 *  public MatrixComplex minusMat(Complex cNum) {
	 *  public MatrixComplex minusMat(String strcNum) {
	 *  public MatrixComplex minusMat(double rep, double imp) {
	 *  private void doPlot(String Title, double[][] dataTable) {
	 * 
	 * 1.12 (2023_0507_1800)
	 * 	public MatrixComplex times(MatrixComplex cMatrix)
	 *  	Removed because makes to enter in an infinite loop call
	 *  		if (this.rows() == 1 && this.cols() == 1) return cMatrix.times(this.getItem(0, 0));
	 *  		if (cMatrix.rows() == 1 && cMatrix.cols() == 1) return this.times(cMatrix.getItem(0, 0));
	 * 
	 * 1.11 (2022_0319_2359)
	 * public MatrixComplex base()
	 * 
	 * 1.10 (2022_0123_0100)
	 *  kernel of a basis' generator of vectors
	 * 		kernel(Complex lambda)
	 * 		kernel()
	 * 		ker(Complex lambda)
	 * 		ker()
	 * 	public MatrixComplex normalize(): shortcut to normalizeByRows()
	 * 	public MatrixComplex normalizeByCols()
	 * 	public MatrixComplex normalizeByRows()
	 *  public void setCol(int colIdx, Complex cValue)
	 *  public void setRow(int rowIdx, Complex cValue)
	 *  public MatrixComplex rowReduce()
	 *  public MatrixComplex getCol(int colIdx)
	 *  public boolean isTriangleLo() Missing curly braces and remade the method
	 *  public MatrixComplex gramSchmidt() Reprogrammed
	 *  public MatrixComplex gramSchmidtFull() Reprogrammed
	 *  public MatrixComplex gramSchmidtMFull() Reprogrammed
	 *  public MatrixComplex gramSchmidtM() Reprogrammed
	 *  public int rank3() is now the rank method
	 *  public boolean isEmpty()
	 * 
	 * 1.9 (2022_0106_1400)
	 * solveGauss(Complex lambda, boolean Reduced) is now the method used to solve Equation Systems
	 * 		It uses the Gaussian Elimination and Back Substitution from a diagonalized matrix. 
	 * 		The matrix is "perfect" diagonalized to accommodate the rows in the position into the system where the not null main diagonal terms occupy the row corresponding with their column number
	 * 		It takes on account the use of the value lambda for the unknown, and so on, to find the base solution "solBase" for each equation
	 * 		The solBase keeps the fixed values for the unknowns with the null equations of the system
	 * 		With the solBase calculated, the rest of the solutions are calculated by the Gaussian Elimination and Back Substitution method
	 * 		solBase is constructed with the complex number parameter lambda
	 * MatrixComplex power(int power). Now power can be negative.
	 * 
	 * 1.8 (2021_1106_1400)
	 * augment(matrixComplex interms) is now using augment2(matrixComplex interms) which returns an augmented matrix with full columns. 
	 * augment1(matrixComplex interms) is DEPRECATED and is kept only for recovery.
	 * gramSchmidtGauss() added. It should be used only with square matrices.
	 * 
	 * 1.7 (2021_0929_2000)
	 * solveGauss is now using the solveGauss2 Method. solveReduction and solveSubstition dosen't work right and are deprecated.
	 */

	/* 
	 * ***********************************************
	 * MATH & PROGRAM CONSTANTS 
	 * ***********************************************
	 */
	static double __log10__ = 2.30258509299405;

	/* 
	 * ***********************************************
	 * INTERNAL FLAGS 
	 * ***********************************************
	 */
	int mSign = 1; //Tracks the correct sign in the determinants calculated through triangularization (Chio's rule); package-private since Etapa 4 (MatrixComplexUnary.determinantGauss() reads it off another instance)

	/*
	 * ***********************************************
	 * 	VERSION 
	 * ***********************************************
	 */
	
	/**
	 * Prints Class Version
	 */
	public static void version() {
		trace("VERSION:" + VERSION); 
	}
	
	/**
	 * Enumeration that selects the possible output format for some expressions 
	 * MATRIXCOMPLEX, 
	 * MAXIMA, 
	 * OCTAVE, 
	 * MATLAB, 
	 * WOLFRAM
	 */
	public enum outputFormat {MATRIXCOMPLEX, MAXIMA, OCTAVE, MATLAB, WOLFRAM};

	/*
	 * __DEBUG__
	 */
	
	private static boolean __DEBUG__ = false;
	
	public static void debugON() {
		__DEBUG__ = true;
	}

	public static void debugOFF() {
		__DEBUG__ = false;
	}

	public static boolean debug() {
		return __DEBUG__;
	}

	private static boolean __DOPLOT__ = true;

	public static void doPlotON() {
		__DOPLOT__ = true;
	}

	public static void doPlotOFF() {
		__DOPLOT__ = false;
	}

	public static boolean doPlot() {
		return __DOPLOT__;
	}

	static void trace(String cadena) {
		if (__DEBUG__) System.out.println("--- TRACE --- " + HEADINFO + cadena);
	}

	static void trace(MatrixComplex mat, String cadena) {
		if (__DEBUG__) mat.println("--- TRACE --- " + HEADINFO + cadena);
	}

	static void trace(Complex complex, String cadena) {
		if (__DEBUG__) complex.println("--- TRACE --- " + HEADINFO + cadena);
	}
	
	/*
	 * ***********************************************
	 * 	CONSTRUCTORS 
	 * ***********************************************
	 */

	/**
	 * Returns the empty complex square matrix object of length 0.
	 */
 	public MatrixComplex() {
		this.complexMatrix = new Complex[0][0];
	}

	/**
	 * Returns the complex square matrix object of length len.
	 * @param len length of the square matrix.
	 */
	public MatrixComplex(int len) {
		this.complexMatrix = new Complex[len][len];
		for (int row = 0; row < len; ++row)
			for (int col = 0; col < len; ++col)
				this.complexMatrix[row][col] = new Complex();
	}

	/**
	 * Instantiates the complex matrix object of dimensions rowLen x colLen.
	 * @param rowLen Number of rows.
	 * @param colLen Number of columns.
	 */
	public MatrixComplex(int rowLen, int colLen) {
		this.complexMatrix = new Complex[rowLen][colLen];
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				this.complexMatrix[row][col] = new Complex();
	}

	/**
	 * Instantiates the complex matrix object from a string representation of the matrix where rows are separated with ";", columns are separated with ",".
	 * @param cadena the string with the rows and columns.
	 */
	public MatrixComplex(String cadena) {
		this.setMatrix(cadena);
	}

	/**
	 * Method for creating a complex matrix object from a string representation of the matrix where rows are separated with ";", columns are separated with ",".
	 * @param cadena the string representation of the matrix.
	 */
	public void setMatrix(String cadena) {
		String[] sRow = cadena.split(";");
		int rowLen = sRow.length;
		String[] sCol = sRow[0].split(",");
		int colLen = sCol.length;
		complexMatrix = new Complex[rowLen][colLen];
		for (int row = 0; row < rowLen; ++row) {
			sCol = sRow[row].split(",");
			colLen = sCol.length;
			for (int col = 0; col < colLen; ++col)
				complexMatrix[row][col] = new Complex(sCol[col].trim());
		}
	}

	/*
	 * ***********************************************
	 * INITIALIZERS
	 * ***********************************************
	 */
	
	/**
	 * Method for initializing a complex matrix with all its items set to the value csNum.
	 * @param csNum String expression of the number in rectangular "A+Bi" or polar "A|B" coordinates.
	 */
	public void initMatrix(String csNum) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cNum = new Complex(csNum);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				this.complexMatrix[row][col].setComplexRec(cNum.rep(), cNum.imp());
	}

	/**
	 * Method for initializing a complex matrix with all its items set to the value cNum.
	 * @param cNum Complex number.
	 */
	public void initMatrix(Complex cNum) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				this.complexMatrix[row][col].setComplexRec(cNum.rep(), cNum.imp());
	}

	/**
	 * Method for initializing a complex matrix with all its items set to the value from the two parts of the complex both Cartesian or Polar representation.
	 * @param coordType Type of coordinate "C" Cartesian, "P" Polar.
	 * @param n1 Value of the first coordinate.
	 * @param n2 Value of the second coordinate.
	 */
	private void initMatrix(char coordType, double n1, double n2) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				switch (coordType) {
					case 'C':
					case 'c':this.complexMatrix[row][col].setComplexRec(n1, n2); 
					break;
					case 'P':
					case 'p':this.complexMatrix[row][col].setComplexPol(n1, n2); 
					break;
				}
	}

	/**
	 * Shortcut of initMatrixRec.
	 * Initializes an array with a complex value in Cartesian coordinates specified as real part and imaginary part.
	 * @param n1 Real Part.
	 * @param n2 Imaginary Part.
	 */    
	public void initMatrix(double n1, double n2) {
		this.initMatrixRec(n1, n2);
	}

	/**
	 * Initializes an array with a complex value in Cartesian coordinates specified as real part and imaginary part.
	 * @param n1 Real Part.
	 * @param n2 Imaginary Part.
	 */
	public void initMatrixRec(double n1, double n2) {
		this.initMatrix('C', n1, n2);
	}

	/**
	 * Initializes an array with a complex value in polar coordinates specified as module and phase.
	 * @param n1 Module.
	 * @param n2 Phase.
	 */
	public void initMatrixPol(double n1, double n2) {
		this.initMatrix('P', n1, n2);
	}

	/**
	 * Initializes the main diagonal of an array with the complex number indicated in text.
	 * @param csNum Complex number in text format.
	 */
	public void initMatrixDiag(String csNum) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cNum = new Complex(csNum);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (row == col) this.complexMatrix[row][col].setComplexRec(cNum.rep(), cNum.imp());
				else this.complexMatrix[row][col].setComplexRec(0, 0);
	}

	/**
	 * Private method to initialize the main diagonal of a matrix with a complex number expressed in Cartesian or polar coordinates.
	 * @param coordType Coordinate type 'C' or 'P'.
	 * @param n1 coordinate 1.
	 * @param n2 coordinate 2.
	 */
	private void initMatrixDiag(char coordType, double n1, double n2) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (row == col) {
					switch (coordType) {
						case 'C':
						case 'c':this.complexMatrix[row][col].setComplexRec(n1, n2); 
						break;
						case 'P':
						case 'p':this.complexMatrix[row][col].setComplexPol(n1, n2); 
						break;
					}
				}
				else this.complexMatrix[row][col].setComplexRec(0, 0);
	}

	/**
	 * Shortcut to initMatrixDiagRec
	 * Initializes the main diagonal of a matrix with the complex number in Cartesian coordinates indicated as real part and imaginary part.
	 * @param n1 Real part.
	 * @param n2 Imaginary Part.
	 */
	public void initMatrixDiag(double n1, double n2) {
		this.initMatrixDiagRec(n1, n2);
	}

	/**
	 * Shortcut to initMatrixDiagRec
	 * Initializes the main diagonal of a matrix with the complex number cNum.
	 * @param cNum the complex number
	 */
	public void initMatrixDiag(Complex cNum) {
		this.initMatrixDiagRec(cNum.rep(), cNum.imp());
	}

	/**
	 * Initializes the main diagonal of a matrix with the complex number in Cartesian coordinates indicated as real part and imaginary part.
	 * @param n1 Real part.
	 * @param n2 Imaginary Part.
	 */
	public void initMatrixDiagRec(double n1, double n2) {
		this.initMatrixDiag('C', n1, n2);
	}

	/**
	 * Initializes the main diagonal of an array with the complex number in polar coordinates indicated as module and phase.
	 * @param n1 Module.
	 * @param n2 Phase.
	 */
	public void initMatrixDiagPol(double n1, double n2) {
		this.initMatrixDiag('P', n1, n2);
	}

	/**
	 * Private method to initialize the main diagonal of an array with a base random number and a type of coordinate.
	 * @param coordType Type of coordinate 'A' Real, 'B' Integer, 'C' Cartesian Complex, 'D', Pure imaginary, 'E' Imaginary pure integer, 'P' Polar Complex, 'I' Complex Cartesian Integers, 'J' Polar Integers.
	 * @param base Base to generate the random number.
	 */
	private void initMatrixRandom(char coordType, int base) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				switch (coordType) {
					case 'A':
					case 'a':this.complexMatrix[row][col].setComplexRandomReal(base); 
					break;
					case 'B':
					case 'b':this.complexMatrix[row][col].setComplexRandomInt(base); 
					break;
					case 'C':
					case 'c':this.complexMatrix[row][col].setComplexRandomRec(base); 
					break;
					case 'D':
					case 'd':this.complexMatrix[row][col].setComplexRandomImag(base); 
					break;
					case 'E':
					case 'e':this.complexMatrix[row][col].setComplexRandomImagInt(base); 
					break;
					case 'P':
					case 'p':this.complexMatrix[row][col].setComplexRandomPol(base); 
					break;
					case 'I':
					case 'i':this.complexMatrix[row][col].setComplexRandomRecInt(base); 
					break;
					case 'J':
					case 'j':this.complexMatrix[row][col].setComplexRandomPolInt(base); 
					break;
				}
	}

	/**
	 * Initializes an array with a complex number of module 1 in Cartesian coordinates.
	 */
	public void initMatrixRandomRec() {
		this.initMatrixRandom('C', 1);
	}

	/**
	 * Initializes an array with a complex number of module 1 in polar coordinates.
	 */
	public void initMatrixRandomPol() {
		this.initMatrixRandom('P', 1);
	}

	/**
	 * Initializes an array with a real number between -1 and 1 in Cartesian coordinates.
	 */
	public void initMatrixRandomReal() {
		this.initMatrixRandom('A', 1);
	}

	/**
	 * Initializes an array with an integer between -1 and 1 in Cartesian coordinates.
	 */
	public void initMatrixRandomInt() {
		this.initMatrixRandom('B', 1);
	}

	/**
	 * Initializes an array with a pure imaginary number between -i and i in Cartesian coordinates.
	 */
	public void initMatrixRandomImag() {
		this.initMatrixRandom('D', 1);
	}

	/**
	 * Initializes an array with an integer pure imaginary number between -i and i in Cartesian coordinates.
	 */
	public void initMatrixRandomImagInt() {
		this.initMatrixRandom('E', 1);
	}

	/**
	 * Initializes an array with a complex module number between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomRec(int base) {
		this.initMatrixRandom('C', base);
	}

	/**
	 * Initializes an array with a complex module number between 0 and base in polar coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomPol(int base) {
		this.initMatrixRandom('P', base);
	}

	/**
	 * Initializes an array with a module integer between 0 and base in Cartesian coordinates
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomRecInt(int base) {
		this.initMatrixRandom('I', base);
	}

	/**
	 * Initializes an array with a complex integer number between 0 and base in polar coordinates
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomPolInt(int base) {
		this.initMatrixRandom('J', base);
	}

	/**
	 * Initializes an array with a real module number between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomReal(int base) {
		this.initMatrixRandom('A', base);
	}

	/**
	 * Initializes an array with a complex number of real module between 0 and base in polar coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomInt(int base) {
		this.initMatrixRandom('B', base);
	}

	/**
	 * Initializes an array with an imaginary module number between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomImag(int base) {
		this.initMatrixRandom('D', base);
	}

	/**
	 * Initializes an array with an imaginary module integer between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomImagInt(int base) {
		this.initMatrixRandom('E', base);
	}

	/**
	 * Initializes an array sequentially with an integer from 0 with steps of 1 in 1 in polar coordinates.
	 */
	public void initMatrixSeqInt() {
		this.initMatrixSeqInt(0, 1);
	}

	/**
	 * Initializes an array sequentially with a real number from first with steps of 1 in 1 in polar coordinates.
	 * @param first Starting number.
	 */
	public void initMatrixSeqInt(double first) {
		this.initMatrixSeqInt(first, 1);
	}

	/**
	 * Initializes an array sequentially with a real number from first with increment "step" in polar coordinates.
	 * @param first Starting number.
	 * @param step Increment.
	 */
	public void initMatrixSeqInt(double first, double step) {
		int rowLen = this.rows();
		int colLen = this.cols();
		double val = first;
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col) {
				val += step;
				this.complexMatrix[row][col].setComplexPol(val,0);
			}
	}

	/*
	 * ***********************************************
	 * ALGEBRAIC BASIS
	 * REMOVED
	 * No tiene sentido manejar bases en MatrixComplex
	 * Se reprograman en la clase Vector
	 * REMOVED
	 * ***********************************************
	 */

	/**
	 * Creates a base of a vector space.
	 * Allows the input of a set vectors written by rows, the base. 
	 * @param str the base vectors.
	 * /
	public void base(String str) {
		MatrixComplex setOfVectors = new MatrixComplex(str); 
		this.complexMatrix = setOfVectors.complexMatrix.clone(); 
	}

	/**
	 * Calculates an orthonormal base associated to the first row component of the matrix
	 * @return the orthonormal base
	 * /	
	public MatrixComplex base() {
		/*
		MatrixComplex vectorBase = new MatrixComplex(this.rows(), this.cols());
		vectorBase.initMatrixDiag(1, 0);
		* /
		MatrixComplex vectorBase = MatrixComplex.eye(this.rows());
		MatrixComplex normal = this.getRow(0).normalize();
		//Check for unitary vector and gets its position
		Complex sum = new Complex();
		int pos = -1;
		for (int i = 0; i < this.cols(); ++i) {
			if (normal.getItem(0, i).equals(Complex.ZERO)) continue;
			pos = i;
			sum = sum.plus(normal.getItem(0, i));
		}
		// if unitary vector is found, put the base row in its position
		// else use position 0
		if (!sum.equals(Complex.ONE)) pos = 0;
		vectorBase.setRow(pos, normal);
		vectorBase = vectorBase.gramSchmidt();
		return vectorBase;		
	}

	/**
	 * Creates a new base of a vector space.
	 * Allows the input of a set vectors written by rows, the base. 
	 * @param str the base vectors.
	 * @return The new matrix with the base.
	 * /
	public MatrixComplex newBase(String str) {
		MatrixComplex setOfVectors = new MatrixComplex(str);
		return setOfVectors;
	}

	/*
	 * ***********************************************
	 * INTERNAL FUNCTIONS
	 * ***********************************************
	 */

	/**
	 * Returns the number of rows of the array
	 * @return The number of rows
	 */
	public int rows() {
		int rows = 0;
		try {
			rows = this.complexMatrix.length;
		}
		catch (Exception e){
			rows = 0;
		}
		return rows;
	}
	
	/**
	 * Returns the number of columns of the array
	 * @return The number of columns
	 */
	public int cols() {
		int cols = 0;
		
		try {
			cols = this.complexMatrix[0].length;
		}
		catch (Exception e) {
			cols = 0;
		}
		return cols;
	}
	
	/**
	 * Gets the item(row, col) of the array
	 * @param row
	 * @param col
	 * @return
	 */
	public Complex getItem(int row, int col) {
		return this.complexMatrix[row][col].copy();
	}
	
	/**
	 * Sets the item(row, col) of the array to a specific double number
	 * @param row
	 * @param col
	 * @param numD
	 */
	public void setItem(int row, int col, double numD) {
		this.complexMatrix[row][col].setComplexRec(numD, 0);
	}
	
	/**
	 * Sets the item(row, col) of the array to a specific complex number
	 * @param row
	 * @param col
	 * @param numC
	 */
	public void setItem(int row, int col, Complex numC) {
		this.complexMatrix[row][col] = numC.copy();
		// this.complexMatrix[row][col].setComplexRec(numC.rep(), numC.imp());
	}
	
	/**
	 * Sets the item(row, col) of the array to a specific complex number in string format
	 * @param row
	 * @param col
	 * @param snumC
	 */
	public void setItem(int row, int col, String snumC) {
		this.complexMatrix[row][col].setComplex(snumC);
	}
	
	/**
	 * Private method to return the components of a string separated by a delimiter. The components have to be integers.
	 * @param str String to chop.
	 * @param delimiter Delimiter of tokens.
	 * @return Array of integers with the tokens.
	 */
	int[] intSplit(String str, String delimiter) {
		String[] sItem = str.split(delimiter);
		int[] token = new int[sItem.length];
		for (int i = 0; i < sItem.length; ++i) {
			token[i] = Integer.parseInt(sItem[i]);
		}
		return token;
	}

	/**
	 * Ceiling for {@link #bestNumDecs()}, deliberately much coarser than {@link
	 * Complex#getSignificative()} (general library precision, up to 8). This one is specific to
	 * comparing/grouping EIGENVALUES coming out of a numerical root-finder (Durand-Kerner/
	 * Aberth-Ehrlich) -- what {@code cond()/2} alone cannot capture is that a genuinely repeated
	 * root only converges to about {@code machine_epsilon^(1/m)} for a root of multiplicity
	 * {@code m}, regardless of how well-conditioned the matrix otherwise is. User-supplied value,
	 * reintroduced after measuring: a 75-case synthetic sweep (known multiplicities 2-4, random
	 * well-conditioned {@code P}) found the OLD cap ({@code Complex.getSignificative()}, usually 8)
	 * misgrouped 43/75 cases (confirmed real, not synthetic-only: the same root cause behind the
	 * two {@code Jordan.checkReconstruction()} failures documented in {@code Jordan.VERSION} 1.4).
	 * This cap fixes every multiplicity-2 case in that sweep (0 remaining failures) with zero new
	 * false merges in a companion 63-case distinct-eigenvalue sweep; multiplicity 3+ still fails
	 * about as often as before (a looser cap of 6 gave identical results) -- that residual is the
	 * same already-documented precision ceiling that needs QR-con-desplazamientos to fix properly,
	 * not a tolerance tweak (see {@code Claude/ComplexArithRev.md}).
	 */
	private final static int BEST_NUM_DECS_CAP = 5;

	/**
	 * Floor for {@link #bestNumDecs()} -- the counterpart of {@link #BEST_NUM_DECS_CAP} at the
	 * other end. {@code numDecs = cond()/2} returns 0 for ANY well-conditioned matrix
	 * ({@code cond()&lt;2}, a wide, common band -- not a rare edge case), which
	 * {@code Eigenspace.eigenval()}'s DISTANCE-based grouping turns into a tolerance of a full
	 * 0.5: coarse enough to merge genuinely DISTINCT eigenvalues spaced closer than that into one
	 * spurious "repeated" eigenvalue -- confirmed real via {@code Schurfactor.factorize()} opaquely
	 * failing on a well-conditioned matrix whose 3 distinct eigenvalues (confirmed correct and
	 * distinct via raw {@code QRSchurfactor} output) were only ~0.05-0.13 apart (Decimoctava
	 * sesion, 6 agosto 2026, ver {@code Claude/ComplexArithRev.md}). User-supplied value,
	 * measured the same way as {@link #BEST_NUM_DECS_CAP}: a 100-case synthetic sweep (Group A --
	 * 3 genuinely distinct eigenvalues at gaps 0.005-1.2, well-conditioned by construction via a
	 * unitary similarity) crossed with a 96-case companion sweep of genuinely repeated eigenvalues
	 * (Group B -- multiplicities 2-4, both diagonalizable and defective/Jordan, magnitudes
	 * 0.5-20), across floor candidates {0,1,2,3}, 3 random seeds each. Floor 2 is the point that
	 * improves Group A (39-40% -> 96-97% correct) with ZERO change to Group B's recall (locked at
	 * exactly 84/96 in every seed, both at floor 0 and floor 2) -- floor 3 reaches 100% on Group A
	 * but at the cost of Group B recall dropping to 84.4% in every seed, a real regression, so NOT
	 * chosen (same "improve or tie every metric at once" bar as {@link #BEST_NUM_DECS_CAP} and
	 * {@code Eigenspace}'s {@code GROUPING_TOL_FACTOR}). Floor 2's remaining Group A failures are
	 * concentrated entirely at the tightest gap tested (0.005, right at floor 2's own tolerance of
	 * 0.005 -- an inherent boundary, not a representative failure).
	 */
	private final static int BEST_NUM_DECS_FLOOR = 2;

	/**
	 * Returns the best number of significant decimals based on the condition number, clamped
	 * between {@link #BEST_NUM_DECS_FLOOR} and {@link #BEST_NUM_DECS_CAP} -- see those constants'
	 * own Javadoc for why each bound exists and where its value comes from.
	 * @return the best number of significant decimals
	 */
	public int bestNumDecs() {
		int numDecs = (int)(this.cond()/2);
		numDecs = numDecs < BEST_NUM_DECS_FLOOR ? BEST_NUM_DECS_FLOOR : numDecs;
		return numDecs > BEST_NUM_DECS_CAP ? BEST_NUM_DECS_CAP : numDecs;
	}
	
	/**
	 * Completes the matrix filling it up with zero's rows to make it a square matrix
	 * @return The new square matrix filled up with new zero's rows
	 */
	public MatrixComplex completeRows() {
		MatrixComplex matrixCompleted = new MatrixComplex(this.cols());
		
		for (int row = 0; row < this.rows(); ++row) {
			matrixCompleted.setRow(row, this.getRow(row));
		}
		return matrixCompleted;
	}
		
	/*
	 * ***********************************************
	 * COPY & REPLICATION
	 * ***********************************************
	 */
	
	/**
	 * Copies the values of the array into another object.
	 * @return The new array with the copy.
	 */
	public MatrixComplex copy() {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex cMatrix = new MatrixComplex(rowLen, colLen);
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].copy();
		return cMatrix;
	}

	/**
	 * Clones the values of the array into another object.
	 * @return The new array with the clone.
	 */
	public MatrixComplex clone() {
		MatrixComplex cMatrix = this.copy();
		cMatrix.mSign = this.mSign;
		return cMatrix;
	}

	/*
	 * ***********************************************
	 * PRINTING
	 * ***********************************************
	 */

	/**
	 * Prints the array of values without carriage return.
	 */
	public void print() {
		MatrixComplexFormat.print(this);
	}

	/**
	 * Prints the array of values with a carriage return.
	 */
	public void println() {
		MatrixComplexFormat.println(this);
	}

	/**
	 * Prints a title and then in a new line the array of values without carriage return.
	 * @param caption The title above the matrix.
	 */
	public void print(String caption) {
		MatrixComplexFormat.print(this, caption);
	}

	/**
	 * Prints a title and then in a new line the array of values with a carriage return.
	 * @param caption The title above the matrix.
	 */
	public void println(String caption) {
		MatrixComplexFormat.println(this, caption);
	}

	/**
	 * Private method that presents the matrix enclosed in brackets.
	 * Each line corresponds to a row of the array.
	 * The columns are separated by commas.
	 */
	public String toString() {
		return MatrixComplexFormat.toString(this);
	}

	/**
	 * Displays a title and selected column
	 * @param col The selected column
	 * @param caption The title
	 */
	public void println(int col, String caption) {
		MatrixComplexFormat.println(this, col, caption);
	}

	/**
	 * Returns a string with the array expression in the format used by Maxima (Computer Algebra System)
	 * @return The string with the array in Maxima format.
	 */
	public String toMaxima() {
		return MatrixComplexFormat.toMaxima(this);
	}

	/**
	 * Returns a string with the array expression in the format used by Wolfram Mathematica.
	 * @return The string with the array in Wolfram Mathematica format.
	 */
	public String toWolfram() {
		return MatrixComplexFormat.toWolfram(this);
	}

	/**
	 * Returns a string with the array expression in the format used by Matlab.
	 * @return The string with the array in Matlab format.
	 */
	public String toMatlab() {
		return MatrixComplexFormat.toMatlab(this);
	}

	/**
	 * Returns a string with the array expression in the format used by GNU Octave.
	 * @return The string with the array in GNU Octave format.
	 */
	public String toOctave() {
		return MatrixComplexFormat.toOctave(this);
	}

	/**
	 * Prepares the matrix string for toMatrixComplex
	 * @return the matrix string
	 */
	public String preMatrixComplex() {
		return MatrixComplexFormat.preMatrixComplex(this);
	}

	/**
	 * Returns a string with the array expression in the format used by Matrix Complex.
	 * @return The string with the array in MatrixComplex format.
	 */
	public String toMatrixComplex() {
		return MatrixComplexFormat.toMatrixComplex(this);
	}

	/*
	 * ***********************************************
	 * COLS & ROWS OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Looks for from the row "col" to the last row of the array the element that complies that the indicated column is different from zero to use it as an exchange row in the diagonalization of arrays.
	 * @param col Column to check.
	 * @return The index of the row found or -1 if it does not find it.
	 */
	public int locateSwapRowUp(int col) {
		int row;

		for (row = col; row < this.rows(); ++row)
			if (!this.complexMatrix[row][col].equals(0,0)) 
				break;
		return (row == this.rows()) ? -1 : row;
	}

	/**
	 * Looks for from the first row to the row "col-1" of the array that complies that the indicated column is different from zero to use it as an exchange row in the diagonalization of arrays.
	 * @param col Column to check.
	 * @return The index of the row found or -1 if it does not find it.
	 */
	public int locateSwapRowDown(int col) {
		int row;

		for (row = 0; row < col; ++row)
			if (!this.complexMatrix[row][col].equals(0,0)) 
				break;
		return row;
	}

	/**
	 * Returns the row from "rowIni" whose column from "rowIni" to the last one has the maximum value in module.
	 * @param rowIni Row and column from which the maximum search starts.
	 * @return The index of the row with the value in maximum nonzero module or -1 if the value was not found.
	 */
	public int partialPivot(int rowIni) {
		int rowLen = this.rows();
		int colP = rowIni;
		int rowMax = rowIni;
		Complex cMax = new Complex(this.complexMatrix[rowIni][rowIni].rep(), this.complexMatrix[rowIni][rowIni].imp());
		for (int row = rowIni; row < rowLen; ++row) {
			if (this.complexMatrix[row][colP].mod() > cMax.mod())  {
				cMax = this.complexMatrix[row][colP];
				rowMax = row;
			}
		}
		if (cMax.equals(0,0))
			return -1;
		return rowMax;
	}

	/**
	 * Returns the row from 0 to "rowEnd" whose column "rowEnd" has the maximum value in module. Mirror of partialPivot(int),
	 * searching upwards from "rowEnd" to row 0 instead of downwards, for triangularizations that eliminate towards row 0 (e.g. triangleLo()).
	 * @param rowEnd Row and column from which the maximum search ends (search goes from row 0 to rowEnd, inclusive).
	 * @return The index of the row with the value in maximum nonzero module or -1 if the value was not found.
	 */
	int partialPivotUp(int rowEnd) {
		int colP = rowEnd;
		int rowMax = rowEnd;
		Complex cMax = new Complex(this.complexMatrix[rowEnd][rowEnd].rep(), this.complexMatrix[rowEnd][rowEnd].imp());
		for (int row = rowEnd; row >= 0; --row) {
			if (this.complexMatrix[row][colP].mod() > cMax.mod())  {
				cMax = this.complexMatrix[row][colP];
				rowMax = row;
			}
		}
		if (cMax.equals(0,0))
			return -1;
		return rowMax;
	}

	/**
	 * Swaps the rows "row1" and "row2" in the array and updates the sign variable to correctly evaluate the determinant.
	 * @param row1 Row to swap.
	 * @param row2 Row to swap.
	 */
	public void swapRows(int row1, int row2) {
		int colLen = this.cols();
		MatrixComplex pivot = new MatrixComplex(1, colLen);

		if (row1 == row2) return;
		pivot.complexMatrix[0] = this.complexMatrix[row1];
		this.complexMatrix[row1] = this.complexMatrix[row2];
		this.complexMatrix[row2] = pivot.complexMatrix[0];
		this.mSign = -this.mSign;
		//	System.out.println("swapRows this.mSign="+this.mSign);
	}

	/**
	 * Swaps the rows "row1" and "row2" in the array and updates the sign variable to correctly evaluate the determinant.
	 * Performs the swap by copying the values of the columns. Is the Long way to do it.
	 * @param row1 Row to swap.
	 * @param row2 Row to swap.
	 */
	public void swapRowsL(int row1, int row2) {
		Complex pivot ;

		if (row1 == row2) return;
		for (int col = 0; col < row1; ++col) {
			pivot = this.complexMatrix[row1][col];
			this.complexMatrix[row1][col] = this.complexMatrix[row2][col];
			this.complexMatrix[row2][col] = pivot;
		}
		this.mSign = -this.mSign;
	}
	
	/**
	 * Copies the value of the last column of the matrix "origMatrix" into the column indicated by "copyCol".
	 * @param origMatrix Array with values to be copied.
	 * @param copyCol Index of the column to place the copied values.
	 */
	void copyCol(MatrixComplex origMatrix, int copyCol) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				this.complexMatrix[row][col] = col == copyCol ? origMatrix.complexMatrix[row][colLen].copy() : origMatrix.complexMatrix[row][col].copy();
			}
		}	
	}

	/**
	 * Copies the value of the "copyCol" column of "origMatrix" in the "destCol" column of this.
	 * @param destCol target copy column.
	 * @param origMatrix Matrix in which values are copied.
	 * @param copyCol index to the column to be copied.
	 */
	public void copyCol(int destCol, MatrixComplex origMatrix, int copyCol) {
		int rowLen = this.rows();
		int row;

		for (row = 0; row < rowLen; ++row) {
			this.complexMatrix[row][destCol] = origMatrix.complexMatrix[row][copyCol].copy();
		}
	}

	/**
	 * Copies the value of the "copyRow" row of "origMatrix" in the "destRow" row of this.
	 * @param destRow target copy row.
	 * @param origMatrix Matrix in which values are copied.
	 * @param copyRow index to the row to be copied.
	 */
	public void copyRow(int destRow, MatrixComplex origMatrix, int copyRow) {
		int colLen = this.cols();
		int col;

		for (col = 0; col < colLen; ++col) {
			this.complexMatrix[destRow][col] = origMatrix.complexMatrix[copyRow][col].copy();
		}
	}

	/**
	 * Compares two matrices and return the result of the comparingif (__DEBUG__) 
	 * @param cMatrix The matrix to compare with
	 * @return The result of the comparing
	 */
	public boolean equals(MatrixComplex cMatrix) {
		int rowLen = this.rows();
		int colLen = this.cols();
		
		if (rowLen != cMatrix.rows()) return false;
		if (colLen != cMatrix.cols()) return false;
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (!this.complexMatrix[row][col].equals(cMatrix.complexMatrix[row][col])) return false;
		return true;
	}
	
	/**
	 * 
	 * @param cMatrix					matrix.println("row "+row+" col "+col);
	 * @param numdecs
	 * @return
	 */
	public boolean equals(MatrixComplex cMatrix, int numdecs) {
		int rowLen = this.rows();
		int colLen = this.cols();
		
		if (rowLen != cMatrix.rows()) return false;
		if (colLen != cMatrix.cols()) return false;
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (!this.complexMatrix[row][col].equals(cMatrix.complexMatrix[row][col], numdecs)) return false;
		return true;
	}

	/**
	 * Appends new NULL rows to the array (Only if the number of columns are the same in both arrays)
	 * @param newRows The new rows to append
	 * @return 
	 */
	public MatrixComplex appendRows(MatrixComplex newRows) {
		int rows = this.rows() + newRows.rows();
		int row;
		
		if ( this.cols() != 0 && this.cols() != newRows.cols()) {
			return this;
		}
		MatrixComplex newArray = new MatrixComplex(rows, this.cols());
		
		for (row = 0; row < this.rows(); ++row)
			newArray.setRow(row, this.getRow(row));
		for (int idx = 0; row < rows; ++row, ++idx)
			newArray.setRow(row, newRows.getRow(idx));
		
		return newArray;
	}
	
	/**
	 * Reorders the array by putting the last row in the first position and so on
	 */
	public void transrow() {
		MatrixComplex transrow = new MatrixComplex(this.rows(), this.cols());
		
		for (int row1 = 0, row2 = this.rows()-1; row1 < this.rows(); ++row1, --row2)
			transrow.setRow(row1, this.getRow(row2));
		
		this.complexMatrix = transrow.complexMatrix.clone();
	}
	
	/*
	 * ***********************************************
	 * ARITHMETIC OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Calculates the sum of two matrices.
	 * @param cMatrix the array to add.
	 * @return The matrix result from the sum.
	 */
	public MatrixComplex plus(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();		
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (rowLenA1 != rowLenA2 || colLenA1 != colLenA2) {
			throw new IllegalArgumentException("Not valid sum: The rows/cols of matrix1 has to be equal to the rows/cols of matrix2.");
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLenA1, colLenA2);

		for (int row = 0; row < rowLenA1; ++row) {
			for (int col = 0; col < colLenA1; ++col) {
				resultMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].plus(cMatrix.complexMatrix[row][col]);
			}
		}
		return resultMatrix;
	}

	/**
	 * THIS SUM MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE plusMat FOR A DIFFERENT APPROXIMATION OF THE ADITION BETWEEN A MATRIX AND A SCALAR.
	 * @param val
	 */
	public MatrixComplex plus(double val) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(val));
		return newThis;
	}

	/**
	 * THIS SUM MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE plusMat FOR A DIFFERENT APPROXIMATION OF THE ADITION BETWEEN A MATRIX AND A SCALAR.
	 * @param cVal
	 */
	public MatrixComplex plus(Complex cVal) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(cVal));
		return newThis;
	}

	/**
	 * This method returns the sum for this plus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(Complex cNum) {
		if (!this.isSquare()) {
			System.err.println("Not valid sum: This sum is only for square matrices.");
		}
		
		MatrixComplex cNumMat = new MatrixComplex(this.rows(), this.cols());
		cNumMat.initMatrixDiag(cNum);
		
		return this.plus(cNumMat);
	}
	
	/**
	 * This method returns the sum for this plus dNum*I, where I is the identity matrix.
	 * @param dNum the real number to construct the diagonal matrix dNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(double dNum) {
		Complex cNum = new Complex(dNum, 0);
		return plusMat(cNum);
	}
	
	/**
	 * This method returns the sum for this plus iNum*I, where I is the identity matrix.
	 * @param iNum the integer number to construct the diagonal matrix iNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(int iNum) {
		Complex cNum = new Complex(iNum, 0);
		return plusMat(cNum);
	}
	
	/**
	 * This method returns the sum for this plus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number in String format to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(String strcNum) {
		Complex cNum = new Complex(strcNum);
		return plusMat(cNum);
	}

	/**
	 * This method returns the sum for this plus cNum*I, where I is the identity matrix.
	 * @param rep the real part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @param imp the imaginary part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(double rep, double imp) {
		Complex cNum = new Complex(rep, imp);
		return plusMat(cNum);
	}
	
	/**
	 * Calculates the difference of two matrices.
	 * @param cMatrix the subtracting matrix.
	 * @return the matrix result of the difference.
	 */
	public MatrixComplex minus(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();		
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (rowLenA1 != rowLenA2 || colLenA1 != colLenA2) {
			throw new IllegalArgumentException("Not valid substraction: The rows/cols of matrix1 has to be equal to the rows/cols of matrix2.");
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLenA1, colLenA2);

		for (int row = 0; row < rowLenA1; ++row) {
			for (int col = 0; col < colLenA1; ++col) {
				resultMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].minus(cMatrix.complexMatrix[row][col]);
			}
		}
		return resultMatrix;
	}

	/**
	 * THIS SUBTRACTION MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE MINUSMat FOR A DIFFERENT APPROXIMATION OF THE SUBTRACTION BETWEEN A MATRIX AND A SCALAR.
	 * @param val
	 */
	public MatrixComplex minus(double val) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(val));
		return newThis;
	}

	/**
	 * THIS SUBTRACTION MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE MINUSMat FOR A DIFFERENT APPROXIMATION OF THE SUBTRACTION BETWEEN A MATRIX AND A SCALAR.
	 * @param cVal
	 */
	public MatrixComplex minus(Complex cVal) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(cVal));
		return newThis;
	}

	/**
	 * This method returns the difference for this minus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(Complex cNum) {
		if (!this.isSquare()) {
			System.err.println("Not valid sum: This sum is only for square matrices.");
		}
		
		MatrixComplex cNumMat = new MatrixComplex(this.rows(), this.cols());
		cNumMat.initMatrixDiag(cNum);
		
		return this.minus(cNumMat);
	}

	/**
	 * This method returns the difference for this minus dNum*I, where I is the identity matrix.
	 * @param dNum the real number to construct the diagonal matrix dNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(double dNum) {
		Complex cNum = new Complex(dNum, 0);
		return minusMat(cNum);
	}
	
	/**
	 * This method returns the difference for this minus iNum*I, where I is the identity matrix.
	 * @param iNum the integer number to construct the diagonal matrix iNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(int iNum) {
		Complex cNum = new Complex(iNum, 0);
		return minusMat(cNum);		
	}
	
	/**
	 * This method returns the difference for this minus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number in String format to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(String strcNum) {
		Complex cNum = new Complex(strcNum);
		return minusMat(cNum);
	}

	/**
	 * This method returns the difference for this minus cNum*I, where I is the identity matrix.
	 * @param rep the real part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @param imp the imaginary part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(double rep, double imp) {
		Complex cNum = new Complex(rep, imp);
		return minusMat(cNum);
	}

	/**
	 * Calculates the matrix product by a real scalar.
	 * @param num Real number.
	 * @return The result matrix of the product by the scalar.
	 */
	public MatrixComplex times(double num) {
		Complex cNum = new Complex(num, 0);
		return this.times(cNum);
	}

	/**
	 * Calculates the product of the matrix by a scalar complex
	 * @param cNum Complex number
	 * @return The result matrix of the product by the scalar
	 */
	public MatrixComplex times(Complex cNum) {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex resultMatrix = new MatrixComplex(rowLen, colLen);

		for (int rowf = 0; rowf < rowLen; ++rowf)
			for (int colf = 0; colf < colLen; ++colf)
				resultMatrix.complexMatrix[rowf][colf] = this.complexMatrix[rowf][colf].times(cNum);
		return resultMatrix;    	
	}

	/**
	 * Calculates the product of two matrices.
	 * @param cMatrix The multiplier matrix.
	 * @return The matrix resulting from the matrices product.
	 */
	public MatrixComplex times(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (colLenA1 != rowLenA2) {
			System.err.println("Not valid product: The cols of matrix1 has to be equal to the rows of matrix2.");
			//System.exit(1);
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLenA1, colLenA2);

		// Accumulates in place via plusEq() into the zero-initialized cell the constructor above
		// already allocated, instead of the old resultMatrix.complexMatrix[rowf][colf] =
		// resultMatrix.complexMatrix[rowf][colf].plus(...) -- same accumulator idiom already used
		// throughout ComplexFunctions.java (acc.plusEq(a.times(b))), here applied to this class'
		// canonical O(n^3) hot path. Halves the allocations in the inner loop (was 2 new Complex
		// per iteration -- times() and plus() -- now just the times() product) and drops the
		// redundant per-iteration array read+store.
		for (int rowf = 0; rowf < rowLenA1; ++rowf)
			for (int colf = 0; colf < colLenA2; ++colf)
				for (int iter = 0; iter < colLenA1; ++iter)
					resultMatrix.complexMatrix[rowf][colf].plusEq(this.complexMatrix[rowf][iter].times(cMatrix.complexMatrix[iter][colf]));
		return resultMatrix;
	}

	/**
	 * Calculates the matrix division by a real scalar.
	 * @param num Real number.
	 * @return The matrix resulting from the division by the scalar.
	 */
	public MatrixComplex divides(double num) {
		Complex cNum = new Complex(num, 0);
		return this.divides(cNum);
	}

	/**
	 * Calculates the division of the matrix by a complex scalar.
	 * @param cNum The complex number.
	 * @return The result matrix by the scalar division.
	 */
	public MatrixComplex divides(Complex cNum) {
		return this.times(cNum.reciprocal());
	}

	/**
	 * Calculates the division between two matrices as the product of the dividend by the inverse of the divisor matrix.
	 * @param cMatrix the divisor matrix.
	 * @return The matrix resulting from the division.
	 */
	public MatrixComplex divides(MatrixComplex cMatrix) {
		//return this.times(cMatrix.inverse());
		return this.dividesright(cMatrix);
	}

	/**
	 * Calculates the left division of two arrays as this⁻¹*cMatrix
	 * @param cMatrix The matrix to divide
	 * @return The left division
	 */
	public MatrixComplex dividesleft(MatrixComplex cMatrix) {
		if (this.rows() == 1 && this.cols() == 1) return cMatrix.times(this.getItem(0, 0).reciprocal());
		if (cMatrix.rows() == 1 && cMatrix.cols() == 1) return this.inverse().times(cMatrix.getItem(0, 0));
		return this.inverse().times(cMatrix);
	}

	/**
	 * Calculates the right division of two arrays as this*cMatrix⁻¹
	 * @param cMatrix The matrix used as divisor
	 * @return The right division
	 */
	public MatrixComplex dividesright(MatrixComplex cMatrix) {
		if (this.rows() == 1 && this.cols() == 1) return cMatrix.inverse().times(this.getItem(0, 0));
		if (cMatrix.rows() == 1 && cMatrix.cols() == 1) return this.divides(cMatrix.getItem(0, 0));
		return this.times(cMatrix.inverse());
	}

	/*
	 * ***********************************************
	 * IN-PLACE (MUTATING) ARITHMETIC OPERATIONS
	 * For accumulator-style hot loops (e.g. the Taylor/Mercator series summations in
	 * MatrixComplexFunctions.java) where reassigning to a freshly allocated MatrixComplex variable on
	 * every iteration is the dominant cost. These mutate 'this' and return 'this' for fluent chaining.
	 * plusEq()/minusEq() are true zero-allocation elementwise ops (cell by cell via
	 * Complex.plusEq()/minusEq(), never touch this.complexMatrix itself). timesEq() is syntactic sugar
	 * only: a real in-place matrix product can't overwrite a cell of 'this' while other cells of the
	 * same row/column are still needed for the rest of the product, so it still allocates a full
	 * replacement complexMatrix internally (via times()) -- it saves the caller's own MatrixComplex
	 * variable reassignment and wrapper-object churn, not the underlying array allocation; a true
	 * buffer-reusing in-place product is a possible future step, not this one. Unlike plus/minus/times,
	 * calling any of these on a shared/cached MatrixComplex would corrupt it for every other caller -
	 * only use them on a private accumulator instance. Only 'this' is mutated; the argument matrix is
	 * read but never modified.
	 * ***********************************************
	 */

	/**
	 * In-place matrix addition: mutates 'this' to 'this' + 'cMatrix', cell by cell via
	 * Complex.plusEq(). Does not allocate.
	 * @param cMatrix the matrix to add.
	 * @return 'this', for chaining.
	 */
	public MatrixComplex plusEq(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (rowLenA1 != rowLenA2 || colLenA1 != colLenA2) {
			throw new IllegalArgumentException("Not valid sum: The rows/cols of matrix1 has to be equal to the rows/cols of matrix2.");
		}

		for (int row = 0; row < rowLenA1; ++row) {
			for (int col = 0; col < colLenA1; ++col) {
				this.complexMatrix[row][col].plusEq(cMatrix.complexMatrix[row][col]);
			}
		}
		return this;
	}

	/**
	 * In-place matrix subtraction: mutates 'this' to 'this' - 'cMatrix', cell by cell via
	 * Complex.minusEq(). Does not allocate.
	 * @param cMatrix the subtracting matrix.
	 * @return 'this', for chaining.
	 */
	public MatrixComplex minusEq(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (rowLenA1 != rowLenA2 || colLenA1 != colLenA2) {
			throw new IllegalArgumentException("Not valid substraction: The rows/cols of matrix1 has to be equal to the rows/cols of matrix2.");
		}

		for (int row = 0; row < rowLenA1; ++row) {
			for (int col = 0; col < colLenA1; ++col) {
				this.complexMatrix[row][col].minusEq(cMatrix.complexMatrix[row][col]);
			}
		}
		return this;
	}

	/**
	 * In-place matrix product: mutates 'this' to 'this' * 'cMatrix'. Syntactic sugar over times() --
	 * swaps 'this.complexMatrix' for the freshly computed product's backing array, so the caller's
	 * MatrixComplex variable no longer needs reassigning and no new wrapper object is returned. Still
	 * allocates a full replacement complexMatrix internally (a true buffer-reusing in-place product
	 * would need a temporary row/matrix, since a cell of 'this' can't safely be overwritten while other
	 * cells of the same row/column are still needed for the rest of the product) -- not a zero-allocation
	 * operation like plusEq()/minusEq(). Safe even when 'cMatrix' is 'this' itself (squaring in place):
	 * times() fully computes the product from the original complexMatrix before this method reassigns it.
	 * @param cMatrix The multiplier matrix.
	 * @return 'this', for chaining.
	 */
	public MatrixComplex timesEq(MatrixComplex cMatrix) {
		this.complexMatrix = this.times(cMatrix).complexMatrix;
		return this;
	}

	/**
	 * In-place matrix product, RAW ACCUMULATION: mutates 'this' to 'this' * 'cMatrix', same
	 * contract as {@link #timesEq(MatrixComplex)} (dimension check, safe when 'cMatrix' is
	 * 'this' itself), but attacks the real cost measured for this hot path (see
	 * {@code Complex.VERSION} 1.33): {@link #times(MatrixComplex)}'s inner loop calls
	 * {@code Complex.plusEq()} once per summed term, and each of those recomputes mod/pha/cre
	 * (trigonometric) even though only the LAST term's result is ever read. Here each output
	 * cell accumulates its {@code colLenA1} terms via {@link Complex#plusEqRaw(Complex)} (zero
	 * trigonometric calls) and calls {@link Complex#syncPolar()} exactly ONCE, after the sum is
	 * complete -- turns O(rows*cols*inner) trigonometric recomputations into O(rows*cols), with
	 * a bit-identical result (see {@code Complex.plusEqRaw()}'s Javadoc for why). Still allocates
	 * a full replacement complexMatrix internally, same as {@code timesEq()} -- this method
	 * targets the trigonometric cost, not the allocation, which Fase 5 of the "*Eq a nivel
	 * MatrixComplex" candidate (Decimoctava sesion) already measured as NOT the dominant cost at
	 * the matrix sizes actually used by the Taylor/Mercator loops.
	 * @param cMatrix The multiplier matrix.
	 * @return 'this', for chaining.
	 */
	public MatrixComplex timesEqRaw(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (colLenA1 != rowLenA2) {
			System.err.println("Not valid product: The cols of matrix1 has to be equal to the rows of matrix2.");
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLenA1, colLenA2);

		for (int rowf = 0; rowf < rowLenA1; ++rowf) {
			for (int colf = 0; colf < colLenA2; ++colf) {
				Complex acc = resultMatrix.complexMatrix[rowf][colf];
				for (int iter = 0; iter < colLenA1; ++iter)
					acc.plusEqRaw(this.complexMatrix[rowf][iter].times(cMatrix.complexMatrix[iter][colf]));
				acc.syncPolar();
			}
		}

		this.complexMatrix = resultMatrix.complexMatrix;
		return this;
	}

	/**
	 * Calculates the power of a Matrix raised to an integer, power can be positive or negative
	 * @param power The power at which the matrix is raised. Only integers are allowed
	 * @return The matrix raised to power
	 */
	public MatrixComplex power(int iExp) {
		// Take advantage from diagonal matrices
		if (this.isDiagonal()) {
    		trace("Power() of diagonal matrix");
    		MatrixComplex powerMat = this.copy();
			for (int i = 0; i < this.rows(); ++i)
				powerMat.setItem(i, i, this.getItem(i, i).power(iExp));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(this);
    	if (dmat.isDiagonalizable()) {
			trace("Power() using diagonalization P·D·P⁻¹");
        	trace(dmat.P(), "Matrix P");
        	trace(dmat.D(), "Matrix D");
    		
        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i) 
        		Dmat.setItem(i, i, Dmat.getItem(i, i).power(iExp));
    			trace(Dmat, "Dmat");
            	trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Power() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}
    	
    	// Finally use the Taylor Expansion
		trace("Power() using the Taylor expansion");
    	return power_(iExp);
	}

	/**
	 * Calculates the power to iExp item to item of this matrix
	 * @param iExp The integer exponent 
	 * @return The power to iExp item to item of this matrix
	 */
	public MatrixComplex ppower(int iExp) {
		MatrixComplex powerMat = new MatrixComplex(this.rows(), this.cols());
		
		for (int row = 0; row < powerMat.rows(); ++row)
			for (int col = 0; col < powerMat.cols(); ++col)
				powerMat.setItem(row, col, this.getItem(row, col).power(iExp));
		return powerMat;
	}
	
	/**
	 * Calculates the power of a Matrix raised to an integer, power can be positive or negative
	 * @param power The power at which the matrix is â€‹â€‹raised. Only integers are allowed
	 * @return The matrix raised to power
	 */
	public MatrixComplex power_(int power) {
		boolean inverse = false;
		
		MatrixComplex powerMatrix = new MatrixComplex(this.rows(), this.cols());
		powerMatrix.initMatrixDiag(1, 0);
		if (power == 0) return powerMatrix;

		if (power < 0) {
			inverse = true;
			power = -power;
		}
		
		double cNorma = this.euc_norm();
		MatrixComplex thisNorma = this.divides(cNorma);
		
		for (int i = 1; i <= power; ++i)
			powerMatrix = powerMatrix.times(thisNorma);
		powerMatrix = powerMatrix.times(Math.pow(cNorma,power));

		if (inverse)
			return powerMatrix.inverse();
		else
			return powerMatrix;
	}
	
	/**
	 * Calculates the power of a Matrix raised to a real number
	 * @param dExpo
	 * @return
	 */
	public MatrixComplex power(double dExpo) {
		// Take advantage from diagonal matrices
		if (this.isDiagonal()) {
    		trace("Power() of diagonal matrix");
			MatrixComplex powerMat = this.copy();
			for (int i = 0; i < this.rows(); ++i)
				powerMat.setItem(i, i, this.getItem(i, i).power(dExpo));
			return powerMat;
		}
		
		/* ************************************************************************		
		if(Math.ceil(dExpo) == Math.floor(dExpo)) {
			int iExpo = (int)Math.floor(dExpo);
			return this.power(iExpo);
		}
		************************************************************************ */
		
		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(this);
    	if (dmat.isDiagonalizable()) {
			trace("Power() using diagonalization P·D·P⁻¹");
        	trace(dmat.P(), "Matrix P");
        	trace(dmat.D(), "Matrix D");
    		
        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i) 
        		Dmat.setItem(i, i, Dmat.getItem(i, i).power(dExpo));
			trace(Dmat, "Dmat");
        	trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Power() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}
    	
		trace("Power() using MatrixComplex.exp((this.log()).times(dExpo))");
		return MatrixComplex.exp((this.log()).times(dExpo));
	}

	/**
	 * Calculates the power to dExp item to item of this matrix
	 * @param dExp The real exponent 
	 * @return The power to dExp item to item of this matrix
	 */
	public MatrixComplex ppower(double dExp) {
		MatrixComplex powerMat = new MatrixComplex(this.rows(), this.cols());
		
		for (int row = 0; row < powerMat.rows(); ++row)
			for (int col = 0; col < powerMat.cols(); ++col)
				powerMat.setItem(row, col, this.getItem(row, col).power(dExp));
		return powerMat;
	}
		
	/**
	 * Calculates the power to cExp item to item of this matrix
	 * @param cExp The complex exponent 
	 * @return The power to cExp item to item of this matrix
	 */
	public MatrixComplex ppower(Complex cExp) {
		MatrixComplex powerMat = new MatrixComplex(this.rows(), this.cols());
		
		for (int row = 0; row < powerMat.rows(); ++row)
			for (int col = 0; col < powerMat.cols(); ++col)
				powerMat.setItem(row, col, this.getItem(row, col).power(cExp));
		return powerMat;
	}

	/**
	 * Calculates the power of a Matrix raised to a complex number
	 * @param cExpo
	 * @return
	 */
	public MatrixComplex power(Complex cExpo) {		
		// Take advantage from diagonal matrices
		if (this.isDiagonal()) {
    		trace("Power() of diagonal matrix");
    		MatrixComplex powerMat = this.copy();
			for (int i = 0; i < this.rows(); ++i)
				powerMat.setItem(i, i, this.getItem(i, i).power(cExpo));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(this);
    	if (dmat.isDiagonalizable()) {
			trace("Power() using diagonalization P·D·P⁻¹");
        	trace(dmat.P(), "Matrix P");
        	trace(dmat.D(), "Matrix D");
    		
        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i) 
        		Dmat.setItem(i, i, Dmat.getItem(i, i).power(cExpo));
			trace(Dmat, "Dmat");
        	trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Power() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}

    	// Finally use the taylor Expansion
		trace("Power() using the Taylor expansion");
    	return power_(cExpo);
	}

	/**
	 * Calculates the power of a Matrix raised to a complex number
	 * @param cExpo
	 * @return
	 */
	public MatrixComplex power_(Complex cExpo) {	
		if (cExpo.isPureReal()) {
			double dExpo = cExpo.rep();
			return this.power(dExpo);
		}
		
		return MatrixComplex.exp((this.log()).times(cExpo));
	}
	
	/**
	 * Complex Matrix raised to a Complex Matrix 
	 * @param mcExpo
	 * @return
	 */
	public MatrixComplex power(MatrixComplex mcExpo) {
		if (this.dim() != mcExpo.dim() || !this.isSquare() || !mcExpo.isSquare()) {
			throw new IllegalArgumentException("Not valid matrices: Both matrices has to be square and of the same dimension.");
		}

		return MatrixComplex.exp((this.log()).times(mcExpo));
	}
	
	/*
	 * ***********************************************
	 * TAYLOR'S SERIES - TAYLOR'S EXPANSIONS 
	 * ***********************************************
	 */

	/**
	 * Calculates the exponential of the matrix (e^this)
	 * This calculation is achieved using the Taylor's series of the exponential extended for complex matrices
	 * @return The value of e^this
	 */
	public MatrixComplex exp() {
		return MatrixComplexFunctions.exp(this);
	}
	
	/**
	 * Calculates the exponential of the matrix (e^this)
	 * This calculation is achieved using the Taylor's series of the exponential extended for complex matrices
	 * @return The value of e^this
	 */
	public MatrixComplex exp_() {
		return MatrixComplexFunctions.exp_(this);
	}

	/**
	 * Calculates the exp of the matrix exp(matrix)
	 * @param matrix
	 * @return The exp of matrix
	 */
	public static MatrixComplex exp(MatrixComplex matrix) {
		return matrix.exp();
	}

	/**
	 * The "SIN" or "COS" depending on the sign. One method to rule them all
	 * @param sign 1 for "SIN"; -1 for "COS"
	 * @return The "SIN" or "COS" depending on the sign	 
	 */
	/**
	 * Calculates the sin of the matrix
	 * This calculation is achieved using the Taylor's series of the sin extended for complex matrices
	 * @return The value of sinTaylor()
	 */
	public MatrixComplex sinTaylor() {
		return MatrixComplexFunctions.sinTaylor(this);
	}

	/**
	 * Calculates the sin of the matrix sinEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of sinEuler()
	 */
	public MatrixComplex sinEuler() {
		return MatrixComplexFunctions.sinEuler(this);
	}

	/**
	 * Calculates the sin of the matrix sin()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of sin()
	 */
	public MatrixComplex sin() {
		return MatrixComplexFunctions.sin(this);
	}

	/**
	 * Calculates the sin of the matrix sin(matrix)
	 * @param matrix
	 * @return The sin of matrix
	 */
	public static MatrixComplex sin(MatrixComplex matrix) {
		return matrix.sin();
	}
	
	/**
	 * Calculates the sin item to item of this matrix  
	 * @return The sin item to item of this matrix
	 */
	public MatrixComplex ssin() {
		return MatrixComplexFunctions.ssin(this);
	}
			
	/**
	 * Calculates the sin item to item of the matrix ssin(matrix)
	 * @param matrix
	 * @return The sin item to item of matrix
	 */
	public static MatrixComplex ssin(MatrixComplex matrix) {
		MatrixComplex sinMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < sinMat.rows(); ++row)
			for (int col = 0; col < sinMat.cols(); ++col)
				sinMat.setItem(row, col, Complex.sin(matrix.getItem(row, col)));
		return sinMat;
	}
	
	/**
	 * Calculates the cos of the matrix cosTaylor()
	 * This calculation is achieved using the Taylor's series of the cos extended for complex matrices
	 * @return The value of cosTaylor()
	 */
	public MatrixComplex cosTaylor() {
		return MatrixComplexFunctions.cosTaylor(this);
	}

	/**
	 * Calculates the sin of the matrix cosEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of cosEuler()
	 */
	public MatrixComplex cosEuler() {
		return MatrixComplexFunctions.cosEuler(this);
	}

	/**
	 * Calculates the sin of the matrix cos()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of cos()
	 */
	public MatrixComplex cos() {
		return MatrixComplexFunctions.cos(this);
	}

	/**
	 * Calculates the cos of the matrix cos(matrix)
	 * @param matrix
	 * @return The cos of matrix
	 */
	public static MatrixComplex cos(MatrixComplex matrix) {
		return matrix.cos();
	}

	
	/**
	 * Calculates the cos item to item of this matrix  
	 * @return The cos item to item of this matrix
	 */
	public MatrixComplex ccos() {
		return MatrixComplexFunctions.ccos(this);
	}
			
	/**
	 * Calculates the cos item to item of the matrix ccos(matrix)
	 * @param matrix
	 * @return The cos item to item of matrix
	 */
	public static MatrixComplex ccos(MatrixComplex matrix) {
		MatrixComplex cosMat = new MatrixComplex(matrix.rows(), matrix.cols());

		for (int row = 0; row < cosMat.rows(); ++row)
			for (int col = 0; col < cosMat.cols(); ++col)
				cosMat.setItem(row, col, Complex.cos(matrix.getItem(row, col)));
		return cosMat;
	}

	
	/**
	 * Calculates the tan of the matrix tanTaylor()
	 * The tangent is calculated as sinTaylor()/cosTaylor()
	 * @return The value of tanTaylor()
	 */
	public MatrixComplex tanTaylor() {
		return MatrixComplexFunctions.tanTaylor(this);
	}

	/**
	 * Calculates the tan of the matrix tanEuler()
	 * The tangent is calculated as sinEuler()/cosEuler()
	 * @return The value of tanEuler()
	 */
	public MatrixComplex tanEuler() {
		return MatrixComplexFunctions.tanEuler(this);
	}

	/**
	 * Calculates the sin of the matrix tan()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of tan()
	 */
	public MatrixComplex tan() {
		return MatrixComplexFunctions.tan(this);
	}

	/**
	 * Calculates the tan of the matrix tan(matrix)
	 * @param matrix
	 * @return The tan of matrix
	 */
	public static MatrixComplex tan(MatrixComplex matrix) {
		return matrix.tan();
	}
	
	/**
	 * Calculates the tan item to item of this matrix  
	 * @return The tan item to item of this matrix
	 */
	public MatrixComplex ttan() {
		return MatrixComplexFunctions.ttan(this);
	}
			
	/**
	 * Calculates the tan item to item of the matrix ttan(matrix)
	 * @param matrix
	 * @return The tan item to item of matrix
	 */
	public static MatrixComplex ttan(MatrixComplex matrix) {
		MatrixComplex tanMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < tanMat.rows(); ++row)
			for (int col = 0; col < tanMat.cols(); ++col)
				tanMat.setItem(row, col, Complex.tan(matrix.getItem(row, col)));
		return tanMat;
	}	
	
	/**
	 * Euler's formula e^[+/-]x=cos(x)[+/-]i·sin(x)
	 * @return Euler's formula e^x
	 */
	public MatrixComplex euler() {
		return MatrixComplexFunctions.euler(this);
	}

	/**
	 * Euler's formula e^[+/-]x=cos(x)[+/-]i·sin(x)
	 * @param matrix
	 * @return Euler's formula e^x
	 */
	public static MatrixComplex euler(MatrixComplex matrix) {
		// normalize2PI() removed, see sinEuler().
		return exp(matrix.times(Complex.i));
	}

	/**
	 * Calculates the hyperbolic sin of the matrix sinhTaylor(this)
	 * This calculation is achieved using the Taylor's series of the hyperbolic sin extended for complex matrices
	 * @return The value of sinh()
	 */
	public MatrixComplex sinhTaylor() {
		return MatrixComplexFunctions.sinhTaylor(this);
	}

	/**
	 * Calculates the hyperbolic sin of the matrix sinhEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of sinhEuler()
	 */
	public MatrixComplex sinhEuler() {
		return MatrixComplexFunctions.sinhEuler(this);
	}

	/**
	 * Calculates the sin of the matrix sinh()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of sinh()
	 */
	public MatrixComplex sinh() {
		return MatrixComplexFunctions.sinh(this);
	}

	/**
	 * Calculates the sinh of the matrix sinh(matrix)
	 * @param matrix
	 * @return The sinh of matrix
	 */
	public static MatrixComplex sinh(MatrixComplex matrix) {
		return matrix.sinh();
	}
	
	/**
	 * Calculates the sinh item to item of this matrix  
	 * @return The sinh item to item of this matrix
	 */
	public MatrixComplex ssinh() {
		return MatrixComplexFunctions.ssinh(this);
	}
			
	/**
	 * Calculates the sinh item to item of the matrix ssinh(matrix)
	 * @param matrix
	 * @return The sinh item to item of matrix
	 */
	public static MatrixComplex ssinh(MatrixComplex matrix) {
		MatrixComplex sinhMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < sinhMat.rows(); ++row)
			for (int col = 0; col < sinhMat.cols(); ++col)
				sinhMat.setItem(row, col, Complex.sinh(matrix.getItem(row, col)));
		return sinhMat;
	}
	
	/**
	 * Calculates the hyperbolic cos of the matrix coshTaylor()
	 * This calculation is achieved using the Taylor's series of the hyp cos extended for complex matrices
	 * @return The value of coshTaylor()
	 */
	public MatrixComplex coshTaylor() {
		return MatrixComplexFunctions.coshTaylor(this);
	}

	/**
	 * Calculates the hyperbolic sin of the matrix coshEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of coshEuler()
	 */
	public MatrixComplex coshEuler() {
		return MatrixComplexFunctions.coshEuler(this);
	}

	/**
	 * Calculates the sin of the matrix cosh()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of cosh()
	 */
	public MatrixComplex cosh() {
		return MatrixComplexFunctions.cosh(this);
	}

	/**
	 * Calculates the cosh of the matrix cosh(matrix)
	 * @param matrix
	 * @return The cosh of matrix
	 */
	public static MatrixComplex cosh(MatrixComplex matrix) {
		return matrix.cosh();
	}

	/**
	 * Calculates the cosh item to item of this matrix  
	 * @return The cosh item to item of this matrix
	 */
	public MatrixComplex ccosh() {
		return MatrixComplexFunctions.ccosh(this);
	}
			
	/**
	 * Calculates the cosh item to item of the matrix ccosh(matrix)
	 * @param matrix
	 * @return The cosh item to item of matrix
	 */
	public static MatrixComplex ccosh(MatrixComplex matrix) {
		MatrixComplex coshMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < coshMat.rows(); ++row)
			for (int col = 0; col < coshMat.cols(); ++col)
				coshMat.setItem(row, col, Complex.cosh(matrix.getItem(row, col)));
		return coshMat;
	}
	
	/**
	 * Calculates the hyperbolic tan of the matrix tanhTaylor()
	 * This calculation uses the Taylor's series of the sin and cos extended for complex matrices
	 * The hyperbolic tangent is calculated as sinhTaylor()/coshTaylor()
	 * @return The value of tanhTaylor()
	 */
	public MatrixComplex tanhTaylor() {
		return MatrixComplexFunctions.tanhTaylor(this);
	}

	/**
	 * Calculates the hyperbolic tan of the matrix tanhEuler()
	 * This calculation uses the Euler's formulas of the sin and cos extended for complex matrices
	 * The hyperbolic tangent is calculated as sinhEuler()/coshEuler()
	 * @return The value of tanhEuler()
	 */
	public MatrixComplex tanhEuler() {
		return MatrixComplexFunctions.tanhEuler(this);
	}

	/**
	 * Calculates the tan of the matrix tanh()
	 * The tangent is calculated as tanh preferred method
	 * @return The value of tanh()
	 */
	public MatrixComplex tanh() {
		return MatrixComplexFunctions.tanh(this);
	}

	/**
	 * Calculates the tanh of the matrix tanh(matrix)
	 * @param matrix
	 * @return The tanh of matrix
	 */
	public static MatrixComplex tanh(MatrixComplex matrix) {
		return matrix.tanh();
	}

	/**
	 * Calculates the tanh item to item of this matrix  
	 * @return The tanh item to item of this matrix
	 */
	public MatrixComplex ttanh() {
		return MatrixComplexFunctions.ttanh(this);
	}
			
	/**
	 * Calculates the tanh item to item of the matrix ttanh(matrix)
	 * @param matrix
	 * @return The tanh item to item of matrix
	 */
	public static MatrixComplex ttanh(MatrixComplex matrix) {
		MatrixComplex tanhMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < tanhMat.rows(); ++row)
			for (int col = 0; col < tanhMat.cols(); ++col)
				tanhMat.setItem(row, col, Complex.tanh(matrix.getItem(row, col)));
		return tanhMat;
	}
	
	/**
	 * Calculates the logarithm of a Matrix using Taylor's Extension summation log(1 - x)
	 * @return The logarithm of a Matrix using Taylor's Extension
	 */
	public MatrixComplex logTaylor() {
		return MatrixComplexFunctions.logTaylor(this);
	}

	/**
	 * Calculates the logarithm of a Matrix using Mercator's Extension summation log(1 + x)
	 * @return The logarithm of a Matrix using Mercator's Extension
	 */
	public MatrixComplex logMercator() {
		return MatrixComplexFunctions.logMercator(this);
	}

	/**
	 * Calculates the logarithm of a Matrix using Hyperbolic Arc Tangent's Extension summation.
	 * Not recommended to use
	 * @return The logarithm of a Matrix using Hyperbolic Arc Tangent's Extension
	 */
	public MatrixComplex logHat() {
		return MatrixComplexFunctions.logHat(this);
	}
	
	/**
	 * Calculates the principal natural logarithm of a (possibly defective, non-diagonalizable)
	 * matrix via Schur factorization plus inverse scaling-and-squaring (MATLAB {@code logm},
	 * Higham).
	 * @return The principal natural logarithm of this matrix.
	 */
	public MatrixComplex logm() {
		return MatrixComplexFunctions.logm(this);
	}

	/**
	 * Shortcut to the preferred natural logarithm expansion.
	 * @return the natural logarithm of the matrix
	 */
	public MatrixComplex log() {
		return MatrixComplexFunctions.log(this);
	}

	/**
	 * Static method to get the natural log of the matrix log(matrix)
	 * @param matrix
	 * @return The log of matrix
	 */
	public static MatrixComplex log(MatrixComplex matrix) {
		return matrix.log();
	}
	
	/**
	 * Calculates the log item to item of this matrix  
	 * @return The log item to item of this matrix
	 */
	public MatrixComplex llog() {
		return MatrixComplexFunctions.llog(this);
	}
			
	/**
	 * Calculates the log item to item of the matrix llog(matrix)
	 * @param matrix
	 * @return The log item to item of matrix
	 */
	public static MatrixComplex llog(MatrixComplex matrix) {
		MatrixComplex logMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < logMat.rows(); ++row)
			for (int col = 0; col < logMat.cols(); ++col)
				logMat.setItem(row, col, Complex.log(matrix.getItem(row, col)));
		return logMat;
	}
	
	/**
	 * Calculates the natural log in base 10 of the matrix log10(matrix)
	 * @param matrix
	 * @return
	 */
	public MatrixComplex log10() {
		return MatrixComplexFunctions.log10(this);
	}

	/**
	 * Static method to get the natural log in base 10 of the matrix log10(matrix)
	 * @param matrix
	 * @return
	 */
	public static MatrixComplex log10(MatrixComplex matrix) {
		return log(matrix).divides(__log10__);
	}

	/**
	 * Calculates the log10 item to item of this matrix  
	 * @return The log  item to item of this matrix
	 */
	public MatrixComplex llog10() {
		return MatrixComplexFunctions.llog10(this);
	}
			
	/**
	 * Calculates the log10 item to item of the matrix llog10(matrix)
	 * @param matrix
	 * @return The log10 item to item of matrix
	 */
	public static MatrixComplex llog10(MatrixComplex matrix) {
		MatrixComplex logMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < logMat.rows(); ++row)
			for (int col = 0; col < logMat.cols(); ++col)
				logMat.setItem(row, col, Complex.log(matrix.getItem(row, col)).divides(__log10__));
		return logMat;
	}
	
	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public MatrixComplex logbase(Complex base) {
		return MatrixComplexFunctions.logbase(this, base);
	}

	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public MatrixComplex llogbase(Complex base) {
		return MatrixComplexFunctions.llogbase(this, base);
	}
	
	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public static MatrixComplex logbase(MatrixComplex matrix, Complex base) {
		return log(matrix).divides(Complex.log(base));
	}

	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public static MatrixComplex llogbase(MatrixComplex matrix, Complex base) {
		return matrix.llogbase(base);
	}
	
	/**
	 * Calculates the natural log in Real base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public MatrixComplex logbase(double base) {
		return MatrixComplexFunctions.logbase(this, base);
	}

	/**
	 * Calculates the natural log in Real base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public MatrixComplex llogbase(double base) {
		return MatrixComplexFunctions.llogbase(this, base);
	}
	
	/**
	 * Calculates the natural log in Real base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public static MatrixComplex logbase(MatrixComplex matrix, double base) {
		return log(matrix).divides(Math.log(base));
	}

	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public static MatrixComplex llogbase(MatrixComplex matrix, double base) {
		return matrix.llogbase(base);
	}

	/**
	 * Calculates the log in complex matrix base "baseMat" of the matrix mat
	 * @param mat
	 * @param baseMat
	 * @return
	 */
	public MatrixComplex logbase(MatrixComplex baseMat) {
		return MatrixComplexFunctions.logbase(this, baseMat);
	}

	/**
	 * Calculates the log in complex matrix base "baseMat" of the matrix mat
	 * @param mat
	 * @param baseMat
	 * @return
	 */
	public static MatrixComplex logbase(MatrixComplex mat, MatrixComplex baseMat) {
		return mat.log().divides(baseMat.log());
	}

	
	
	
	/**
	 * Calculates the power of a complex number raised a complex matrix
	 * @param cBase
	 * @param matrix
	 * @return
	 */
	public static MatrixComplex power(Complex cBase, MatrixComplex exponent) {
		return exp(exponent.times(Complex.log(cBase)));
	}
		
	/**
	 * Calculates the power of a real number raised a complex matrix
	 * @param base
	 * @param matrix
	 * @return
	 */
	public static MatrixComplex power(double base, MatrixComplex exponent) {
		Complex cBase = new Complex(base);
		return power(cBase, exponent);
	}

	/**
	 * Complex Matrix raised to a Complex Matrix 
	 * @param mcBase
	 * @param mcExpo
	 * @return
	 */
	public static MatrixComplex power(MatrixComplex mcBase, MatrixComplex mcExpo) {
		return mcBase.power(mcExpo);
	}
	
	/*
	 * ***********************************************
	 * ORDER RELATIONSHIPS IN MATRICES
	 * ***********************************************
	 */
	
	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean sameDimension(MatrixComplex matrix) {
		if (this.rows() != matrix.rows() || this.cols() != matrix.cols()) return false;
		return true;
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isGT(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}
		//return this.totalize().cre() > matrix.totalize().cre();
		return this.norm() > matrix.norm();
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isGTE(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}	
		//return this.totalize().cre() >= matrix.totalize().cre();
		return this.norm() >= matrix.norm();

	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isLT(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}
		// return this.totalize().cre() < matrix.totalize().cre();
		return this.norm() < matrix.norm();
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isLTE(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}
		// return this.totalize().cre() <= matrix.totalize().cre();
		return this.norm() <= matrix.norm();
	}

	/*
	 * ***********************************************
	 * NORMS
	 * ***********************************************
	 */
	
	/**
	 * Private method that returns the maximum value of a set of real values.
	 * @param valMatrix The set of real values.
	 * @return The maximum real value.
	 */
 	private double max(double[] valMatrix) {
		double temp;
		temp = valMatrix[0];

		for (int i = 1; i < valMatrix.length;  ++i)
			temp = Math.max(temp, valMatrix[i]);
		return temp;
	}
	
 	/**
	 * P Norm or Hölder Norm. Calculates Hölder's norm of order "p".
	 * @param p The order of the norm.
	 * @return The value of the norm.
	 */
	public double p_norm(int p) {
		int rowLen = this.rows();
		int colLen = this.cols();

		if ( p <= 0 ) {
			throw new IllegalArgumentException("Not valid order. The order of the norm must be greater than zero.");
		}

		double[] norm = new double[rowLen];
		for (int row = 0; row  < rowLen; ++row) {
			norm[row] = 0.0;
			for (int col = 0; col < colLen; ++col)
				norm[row] += Math.pow(this.complexMatrix[row][col].mod(), p);
		}	
		return Math.pow(max(norm), 1.0/p);
	}

	/**
	 * Shortcut to euc_norm method.
	 * Euclidean Norm. Calculates the Euclidean norm.
	 * In linear algebra, functional analysis, and related areas of mathematics, a norm is a function that assigns a strictly positive length or size to each vector in a vector spaceâ€”save for the zero vector, which is assigned a length of zero.
	 * @return The value of the norm.
	 */
	public double norm() {
		return this.euc_norm();
	}

	/**
	 * Calculates the norm of infinite order.
	 * @return The value of the norm.
	 */
	public double inf_norm() {
		int rowLen = this.rows();
		int colLen = this.cols();

		double[] norm = new double[rowLen+1];
		for (int row = 0; row < rowLen; ++row) {
			norm[row] = 0.0;
			for (int col = 0; col  < colLen; ++col)
				norm[row] += this.complexMatrix[row][col].mod();
		}	
		return max(norm);
	}

	/**
	 * Euclidean Norm. Calculates the Euclidean norm or norm of order 2.
	 * @return The value of the norm.
	 */
	public double euc_norm() {
		int rowLen = this.rows();
		int colLen = this.cols();

		double norm = 0;
		for (int row = 0; row < rowLen; ++row) {
			for (int col = 0; col  < colLen; ++col)
				norm += Math.pow(this.complexMatrix[row][col].mod(), 2.0);
		}	
		return Math.pow(norm, 0.5);
	}

	/**
	 * Frobenius norm. Calculates the Euclidean norm or norm of order 2.
	 * @return The actual value of the norm.
	 */
	public double f_norm() {
		return this.euc_norm();    	
	}

	/*
	 * ***********************************************
	 * UNARY OPERATORS
	 * ***********************************************
	 */
	
	/**
	 * Checks if a matrix is empty, A matrix is empty if rows = cols = 0
	 * @return True is matrix is empty
	 */
	public boolean isEmpty() {
		return MatrixComplexUnary.isEmpty(this);
	}

	/**
	 * Makes the matrix to become positive semidefinite
	 */
	public void abs() {
		MatrixComplexUnary.abs(this);
	}

	/**
	 * Checks whether the matrix is singular or not (determinant = 0)
	 * @return True if the matrix is singular, false otherwise
	 */
	public boolean isSingular() {
		return MatrixComplexUnary.isSingular(this);
	}

	/**
	 * Normal matrices: A*A.adjoint() = A.adjoint()*A
	 * @return True if the matrix is normal, false otherwise
	 */
	public boolean isNormal() {
		return MatrixComplexUnary.isNormal(this);
	}

	/**
	 * Normal matrices: A square and A*A.adjoint() = A.adjoint()*A = I
	 * @return True if the matrix is normal, false otherwise
	 */
	public boolean isUnitary() {
		return MatrixComplexUnary.isUnitary(this);
	}

	/**
	 * Checks whether a Matrix is diagonal or not
	 * @return True if the matrix is diagonal, false otherwise
	 */
	public boolean isDiagonal() {
		return MatrixComplexUnary.isDiagonal(this);
	}

	/**
	 * Checks whether a Matrix is orthogonal or not
	 * @return True if the matrix is orthogonal, false otherwise
	 */
	public boolean isOrthogonal() {
		return MatrixComplexUnary.isOrthogonal(this);
	}

	/**
	 * Checks whether a Matrix is orthonormal or not. Othonormal and Orthogonal are the same concept in Matrices. BAD!!!!
	 * @return True if the matrix is orthonormal, false otherwise
	 */
	public boolean isOrthonormal() {
		return MatrixComplexUnary.isOrthonormal(this);
	}

	/**
	 * Upper Hessenberg matrices: a(i,j) = 0 for any pair i, j such that i > j + 1.
	 * @return True if the matrix is upper Hessenberg, false otherwise
	 */
	public boolean isHessenbergUpper() {
		return MatrixComplexUnary.isHessenbergUpper(this);
	}

	/**
	 * Upper Hessenberg matrices: a(i,j) = 0 for any pair i, j such that i <= j.
	 * @return True if the matrix is lower Hessenberg, false otherwise
	 */
	public boolean isHessenbergLower() {
		return MatrixComplexUnary.isHessenbergLower(this);
	}

	/**
	 * Checks if at least one of the values of the array is infinite
	 * @return True if one infinite value is found
	 */
 	public boolean isInfinite() {
		return MatrixComplexUnary.isInfinite(this);
	}

	/**
	 * Checks if at least one of the values of the array is NaN
	 * @return True if one NaN value is found
	 */
	public boolean isNaN() {
		return MatrixComplexUnary.isNaN(this);
	}

	/**
	 * Checks if the matrix is null compared with Complex.ZERO.
	 * @return true if the matrix is null, otherwise false.
	 */
	public boolean isNullC() {
		return MatrixComplexUnary.isNullC(this);
	}

	/**
	 * Checks if the matrix is null compared with Complex.ZERO.
	 * @return true if the matrix is null, otherwise false.
	 */
	public boolean isNull() {
		return MatrixComplexUnary.isNull(this);
	}

	/**
	 * The dimension of the matrix as a product of the number of rows by the number of columns.
	 * @return The matrix dimension.
	 */
	public int dim() {
		return MatrixComplexUnary.dim(this);
	}

	/**
	 * Returns the condition number of the array using the p norm, where p is the order of the norm.
	 * @return The condition number
	 */
	public double cond_p(int p) {
		return MatrixComplexUnary.cond_p(this, p);
	}

	/**
	 * Returns the condition number of the array using the euclidean norm
	 * @return The condition number
	 */
	public double cond_f() {
		return MatrixComplexUnary.cond_f(this);
	}

	/**
	 * Returns the condition number of the array using the infinite norm
	 * @return The condition number
	 */
	public double cond_inf() {
		return MatrixComplexUnary.cond_inf(this);
	}

	/**
	 * Returns the condition number of the array using the infinite norm.
	 * Short cut to cond_imf()
	 * @return The condition number
	 */
	public double cond() {
		return MatrixComplexUnary.cond(this);
	}

	/**
	 * Trace of an n-by-n square matrix A - the sum of the elements on the main diagonal.
	 * @return The value of the trace.
	 */
	public Complex trace() {
		return MatrixComplexUnary.trace(this);
	}

	/**
	 * Cotrace of an n-by-n square matrix A - the sum of the elements on the secondary diagonal.
	 * @return The value of the trace.
	 */
	public Complex cotrace() {
		return MatrixComplexUnary.cotrace(this);
	}

	/**
	 * Calculates the opposite of the matrix.
	 * @return The matrix opposite.
	 */
	public MatrixComplex opposite() {
		return MatrixComplexUnary.opposite(this);
	}

	/**
	 * Transpose of the matrix by reflecting it over its main diagonal.
	 * @return The matrix transposed.
	 */
	public MatrixComplex transpose() {
		return MatrixComplexUnary.transpose(this);
	}

	/**
	 * Calculates the conjugate of the matrix.
	 * Matrix complex conjugate is a new matrix with equal real part and imaginary part equal in magnitude but opposite in sign.
	 * @return The matrix conjugated.
	 */
	public MatrixComplex conjugate() {
		return MatrixComplexUnary.conjugate(this);
	}

	/**
	 * Calculates the adjoint of the matrix.
	 * The adjoint is the transposed conjugated matrix.
	 * @return The new matrix adjoint.
	 */
	public MatrixComplex adjoint() {
		return MatrixComplexUnary.adjoint(this);
	}

	/**
	 * Minor for row "rowPivot" and column "colPivot".
	 * The minor the Matrix resultant of removing the row "rowPivot" and column "colPivot".
	 * @param rowPivot The index of the row to eliminate.
	 * @param colPivot The index of the column to eliminate.
	 * @return The minors' matrix.
	 */
	public MatrixComplex minor(int rowPivot, int colPivot) {
		return MatrixComplexUnary.minor(this, rowPivot, colPivot);
	}

	/**
	 * Matrix of Cofactors order 1 for row "rowPivot" and column "colPivot".
	 * The co-factor of an element of the matrix is equal to the product of the minor of the element and -1 to the power of the positional value of the element.
	 * @param rowPivot The index of the row minor.
	 * @param colPivot The index of the column minor.
	 * @return The cofactors' matrix.
	 */
	public MatrixComplex cofactors(int rowPivot, int colPivot) {
		return MatrixComplexUnary.cofactors(this, rowPivot, colPivot);
	}

	/**
	 * Calculates the adjugate of an square matrix.
	 * Adjugate or classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate() {
		return MatrixComplexUnary.adjugate(this);
	}

	/**
	 * Calculates the adjugate of an square matrix.
	 * Adjunct or adjugate, or classical adjoint, of a square matrix is the transpose of its cofactor matrix.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct() {
		return MatrixComplexUnary.adjunct(this);
	}

	/**
	 * Calculates the adjugate for a row and a column.
	 * Adjugate or classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param rowPivot The index of the row to remove.
	 * @param colPivot The index of the column to remove.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate(int rowPivot, int colPivot) {
		return MatrixComplexUnary.adjugate(this, rowPivot, colPivot);
	}

	/**
	 * Calculates the adjunct for a row and a column.
	 * Adjunct or adjugate, or classical adjoint, of a square matrix is the transpose of its cofactor matrix.
	 * @param rowPivot The index of the row to remove.
	 * @param colPivot The index of the column to remove.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct(int rowPivot, int colPivot) {
		return MatrixComplexUnary.adjunct(this, rowPivot, colPivot);
	}

	/**
	 * Calculates the adjugate matrix of the rows passed in the parameter "includedRows".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRows A list with the indexes of the the rows included in the cofactors array.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate(int[] includedRows) {
		return MatrixComplexUnary.adjugate(this, includedRows);
	}

	/**
	 * Calculates the adjunct matrix of the rows passed in the parameter "includedRows".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * <p>
	 * FIX (Etapa 4, Decimocuarta sesion): delegated to {@code MatrixComplexUnary.adjunct(m, includedRows)},
	 * which in turn delegates to {@code adjugate(m, includedRows)} -- the original body called itself
	 * ({@code this.adjunct(includedRows)}), an infinite recursion that guaranteed a
	 * {@code StackOverflowError} on every call. Zero callers anywhere in the project.
	 * @param includedRows A list with the indexes of the the rows included in the cofactors array.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct(int[] includedRows) {
		return MatrixComplexUnary.adjunct(this, includedRows);
	}

	/**
	 * Calculates the adjugate matrix of the rows passed in the parameter "includedRowsList".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRowsList A list with the rows included in the cofactors array as a comma separated string.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate(String includedRowsList) {
		return MatrixComplexUnary.adjugate(this, includedRowsList);
	}

	/**
	 * Calculates the adjunct matrix of the rows passed in the parameter "includedRowsList".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRowsList A list with the rows included in the cofactors array as a comma separated string.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct(String includedRowsList) {
		return MatrixComplexUnary.adjunct(this, includedRowsList);
	}

	/**
	 * The inverse of the matrix calculated by Gauss-Jordan elimination method
	 * Gauss-Jordan elimination method can be used for finding the inverse of a matrix, if it exists.
	 * If A is a n by n square matrix, then row reduction can be used to compute its inverse matrix, if it exists.
	 * First, the n by n identity matrix is augmented to the right of A, forming a n by 2n block matrix [A | I].
	 * Now through application of elementary row operations, finds the reduced echelon form of this n by 2n matrix.
	 * The matrix A is invertible if and only if the left block can be reduced to the identity matrix I; in this case
	 * the right block of the final matrix is A⁻¹. If the algorithm is unable to reduce the left block to I,
	 * then A is not invertible.
	 * @return The inverse matrix.
	 */
	public MatrixComplex inverse() {
		return MatrixComplexUnary.inverse(this);
	}

	/**
	 * Returns the upper triangularization of the matrix.
	 * @return The  upper triangular matrix.
	 */
	public MatrixComplex triangle(){
		return MatrixComplexUnary.triangle(this);
	}

	/**
	 * Generates a diagonal matrix using triangularization Low and then Up
	 * @return The diagonal matrix
	 */
	public MatrixComplex diagonalLo() {
		return MatrixComplexUnary.diagonalLo(this);
	}

	/**
	 * Generates a diagonal matrix using triangularization Up and then Lo
	 * @return The diagonal matrix
	 */
	public MatrixComplex diagonalUp() {
		return MatrixComplexUnary.diagonalUp(this);
	}

	/**
	 * Shortcut to determinantGauss.
	 * Calculate the matrix determinant by the default rule (Gauss)
	 * @return The value of the determinant.
	 */
	public Complex determinant() {
		return MatrixComplexUnary.determinant(this);
	}

	/**
	 * Calculates the matrix determinant by the Gauss' method.
	 * @return The value of the determinant.
	 */
	public Complex determinantGauss() {
		return MatrixComplexUnary.determinantGauss(this);
	}

	/**
	 * Calculates the matrix determinant through matrix of adjoints (cofactors)
	 * DO NOT USE FOR MATRIX OVER 5x5.
	 * @return The value of the determinant.
	 */
	public Complex determinantAdj() {
		return MatrixComplexUnary.determinantAdj(this);
	}

	/**
	 * Checks if the matrix is symmetric or not
	 * @return True if the matrix is symmetric
	 */
	public boolean isSymmetric() {
		return MatrixComplexUnary.isSymmetric(this);
	}

	/**
	 * Checks if the matrix is antisymmetric or not
	 * @return True if the matrix is antisymmetric
	 */
	public boolean isAntiSymmetric() {
		return MatrixComplexUnary.isAntiSymmetric(this);
	}

	/**
	 * Checks if the matrix is skew-symmetric or not
	 * @return True if the matrix is skew-symmetric
	 */
	public boolean isSkewSymmetric() {
		return MatrixComplexUnary.isSkewSymmetric(this);
	}

	/**
	 * Checks if the matrix is hermitian or not
	 * @return True if the matrix is hermitian
	 */
	public boolean isHermitian() {
		return MatrixComplexUnary.isHermitian(this);
	}

	/**
	 * Checks if the matrix is antihermitian or not
	 * @return True if the matrix is antihermitian
	 */
	public boolean isAntiHermitian() {
		return MatrixComplexUnary.isAntiHermitian(this);
	}

	/**
	 * Checks if the matrix is skew-hermitian or not
	 * @return True if the matrix is skew-hermitian
	 */
	public boolean isSkewHermitian() {
		return MatrixComplexUnary.isSkewHermitian(this);
	}

	/**
	 * Method for creating an Square Identity array of "dim" size
	 * @param dim The size of Identity array
	 * @return The Identity array
	 */
	public static MatrixComplex eye(int dim) {
		return MatrixComplexUnary.eye(dim);
	}

	/**
	 *
	 * @return
	 */
	public boolean isPostiveDefinite() {
		return MatrixComplexUnary.isPostiveDefinite(this);
	}

	/**
	 *
	 * @return
	 */
	public boolean isPostiveSemiDefinite() {
		return MatrixComplexUnary.isPostiveSemiDefinite(this);
	}

	/**
	 *
	 * @return
	 */
	public boolean isNegtiveDefinite() {
		return MatrixComplexUnary.isNegtiveDefinite(this);
	}

	/**
	 *
	 * @return
	 */
	public boolean isNegtiveSemiDefinite() {
		return MatrixComplexUnary.isNegtiveSemiDefinite(this);
	}

	/**
	 * Checks if there is a zero on the main diagonal.
	 * @return True if a zero was found, false otherwise.
	 */
	public boolean hasZeroMainDiag() {
		return MatrixComplexUnary.hasZeroMainDiag(this);
	}

	/**
	 * Checks if there is one item on the main diagonal for which its REAL PART is zero or negative .
	 * @return False if a non positive was found, false otherwise.
	 */
	public boolean repPositiveMainDiag() {
		return MatrixComplexUnary.repPositiveMainDiag(this);
	}

	/**
	 * Method for creating an Square Identity array of "this" matrix size
	 * @param dim The size of Identity array
	 * @return The Identity array
	 */
	public MatrixComplex eye() {
		return MatrixComplexUnary.eye(this);
	}

	/**
	 * Copies the 1xN values of a one row array and put one by one in a diagonal NxN matrix
	 * @param values The values of a one row array
	 * @return The diagonal NxN matrix
	 */
	public static MatrixComplex diagonal(MatrixComplex values) {
		return MatrixComplexUnary.diagonal(values);
	}
	
	/*
	 * ***********************************************
	 * EQUATION SYSTEMS
	 * ***********************************************
	 */
	
	/**
	 * Returns a new matrix with the coefficients of the object matrix.
	 * The new matrix is the original one with the independent terms removed.
	 * @return The new matrix with the coefficients copied without independent terms.
	 */
 	public MatrixComplex coefMatrix() {
		return MatrixComplexEquationSystems.coefMatrix(this);
	}

 	/**
	 * Returns a new matrix with the independent terms of the object matrix.
	 * The new matrix is the independent terms column matrix.
	 * @return The new matrix with the independent terms.
	 */
	public MatrixComplex indMatrix() {
		return MatrixComplexEquationSystems.indMatrix(this);
	}

	/**
	 * Returns a new matrix with the independent terms of the object matrix.
	 * The new matrix is the independent terms column matrix.
	 * @return The new matrix with the independent terms.
	 */
	public MatrixComplex constMatrix() {
		return MatrixComplexEquationSystems.constMatrix(this);
	}
	
	/**
	 * Defines the constants that identify the type of equation system being solved.
	 */
	public static final int INCONSISTENT = -1;
	public static final int INDETERMINATE = 0;
	public static final int DETERMINATE = 1;

	/**
	 * Identifies if the system of equations is isHomogeneous.
	 * @return Returns true if the system is isHomogeneous, false otherwise.
	 */
	public boolean isHomogeneous() {
		return MatrixComplexEquationSystems.isHomogeneous(this);
	}

	/**
	 * Returns the homogeneous equation system of this
	 * @return the homogeneous equation system of this
	 */
	public MatrixComplex homogeneous() {
		return MatrixComplexEquationSystems.homogeneous(this);
	}
	
	/**
	 * Identifies the type of systems of equations returning the constant according to the definition.
	 * @return INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 */
	public int typeEqSys() {
		return MatrixComplexEquationSystems.typeEqSys(this);
	}

	/**
	 * Returns a string indicating the system type of equations according to type "type".
	 * @param type INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 * @param lambda Parameter value to calculate solutions for indeterminate systems.
	 */
	public String strTypeEqSys(int type, Complex lambda) {
		return MatrixComplexEquationSystems.strTypeEqSys(type, lambda);
	}

	/**
	 * Prints a message indicating the system type of equations according to type "type".
	 * @param type INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 * @param lambda Parameter value to calculate solutions for indeterminate systems.
	 */
	public void printTypeEqSys(int type, Complex lambda) {
		MatrixComplexEquationSystems.printTypeEqSys(type, lambda);
	}

	/**
	 * Prints a message indicating the system type of equations according to type "type". lambda is 1 by default
	 */
	public void printTypeEqSys() {
		MatrixComplexEquationSystems.printTypeEqSys(this);
	}

	/**
	 * If the number of equations is less than of unknowns, the missing equations are introduced with the coefficients and the independent term set to zero.
	 * @return the system of equations completed with equations that are missing to zero.
	 */
	public MatrixComplex completeEqSys() {
		return MatrixComplexEquationSystems.completeEqSys(this);
	}

	/**
	 * Finds the solutions of an equation systems by the default rule (Gauss reduction)
	 * @return The solutions of the equation systems
	 */
	public MatrixComplex solve() {
		return MatrixComplexEquationSystems.solve(this);
	}

	/**
	 * Shortcut to solveGauss
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist.
	 */
	public MatrixComplex solve(Complex lambda) {
		return MatrixComplexEquationSystems.solve(this, lambda);
	}

	/**
	 * Equation evaluator
	 * Evaluates an equation replacing its unknowns with values
	 * @param row
	 * @param col
	 * @param point
	 * @return
	 */
	public Complex eqEval(int row, int col,MatrixComplex point) {
		return MatrixComplexEquationSystems.eqEval(this, row, col, point);
	}
	
	/**
	 * Removes the null rows of a Matrix
	 * @return A new Matrix without the null rows
	 */
	public MatrixComplex removeNullRows() {
		return MatrixComplexEquationSystems.removeNullRows(this);
	}

	/**
	 * Removes the duplicated rows of a Matrix
	 * @return A new Matrix without the duplicated rows
	 */
	public MatrixComplex removeDuplicateRows() {
		return MatrixComplexEquationSystems.removeDuplicateRows(this);
	}
	
	/**
	 * Returns the minimum number of LI solutions that the equation system has
	 * @return The minimum number of LI solutions
	 */
	public int nbrOfSolutions() {
		return MatrixComplexEquationSystems.nbrOfSolutions(this);
	}

	/**
	 * Calculates the minimum number of LI solutions of an equation system and writes it on the console
	 * @return The minimum number of LI solutions
	 */
	public int nbrOfSolutionsText() {
		return MatrixComplexEquationSystems.nbrOfSolutionsText(this);
	}

	/**
	 * Gets the row (rowIdx) of a Matrix
	 * @param rowIdx The row index to retrieve. 1st row is 0, and so on.
	 * @return The row selected
	 */
	public MatrixComplex getRow(int rowIdx) {
		return MatrixComplexEquationSystems.getRow(this, rowIdx);
	}

	/**
	 * Gets the col (colIdx) of a Matrix
	 * @param colIdx The col index to retrieve. 1st col is 0, and so on.
	 * @return The col selected
	 */
	public MatrixComplex getCol(int colIdx) {
		return MatrixComplexEquationSystems.getCol(this, colIdx);
	}
	/**
	 * Sets the row (rowIdx) of "this" with the values of rowMatrix
	 * @param rowIdx The row index to retrieve. 1st row is 0, and so on.
	 */
	public void setRow(int rowIdx, MatrixComplex rowMatrix) {
		MatrixComplexEquationSystems.setRow(this, rowIdx, rowMatrix);
	}

	/**
	 * Sets the column colIdx of the matrix to certain complex value
	 * @param colIdx The Id of the col to set
	 * @param cValue The value to set
	 */
	public void setCol(int colIdx, Complex cValue) {
		MatrixComplexEquationSystems.setCol(this, colIdx, cValue);
	}

	/**
	 * Sets colum colMatrix into this at colIdx column
	 * @param colIdx
	 * @param colMatrix
	 */
	public void setCol(int colIdx, MatrixComplex colMatrix) {
		MatrixComplexEquationSystems.setCol(this, colIdx, colMatrix);
	}

	/**
	 * Sets the row rowIdx of the matrix to certain complex value
	 * @param rowIdx The Id of the col to set
	 * @param cValue The value to set
	 */
	public void setRow(int rowIdx, Complex cValue) {
		MatrixComplexEquationSystems.setRow(this, rowIdx, cValue);
	}

	/**
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist, otherwise null.
	 */	
	public MatrixComplex solveGauss(Complex lambda) {
		return MatrixComplexEquationSystems.solveGauss(this, lambda);
	}

	/**
	 * finds the solutions to a equation systems using the Cramer's rule
	 * @return The column matrix with the solutions if they exist.
	 */
	public MatrixComplex solveCramer() {
		return MatrixComplexEquationSystems.solveCramer(this);
	}

	/**
	 * rowReduce performs the Gauss-Jordan elimination, adding multiples of rows together so as to produce zero elements when possible. The final matrix is in reduced row echelon form.
	 * @return The final matrix in its reduced row echelon form.
	 */
	public MatrixComplex rowReduce() {
		return MatrixComplexEquationSystems.rowReduce(this);
	}

	/**
	 * Returns the sub-matrix from row, col of order "order" (number of rows/columns taken)
	 * @param row The index of the initial row.
	 * @param col The index of the initial column.
	 * @param order Number of rows/columns taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrixAug(int row, int col, int order) {
		return MatrixComplexEquationSystems.subMatrixAug(this, row, col, order);
	}

	/**
	 * Returns the sub-matrix from row, col of order "order" (number of rows/columns taken)
	 * @param row The index of the initial row.
	 * @param col The index of the initial column.
	 * @param order Number of rows/columns taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrix(int row, int col, int order) {
		return MatrixComplexEquationSystems.subMatrix(this, row, col, order);
	}

	/**
	 * Returns the sub-matrix formed with rows an columns indicated in the parameters "rows" and "columns".
	 * @param rows A list of integers of the rows to be taken.
	 * @param cols A list of integers of the columns to be taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrix(int[] rows, int[] cols) {
		return MatrixComplexEquationSystems.subMatrix(this, rows, cols);
	}

	/**
	 * Returns the sub-matrix formed with rows an columns indicated in the parameters "rows" and "columns" as a list of comma separated values.
	 * @param Srows A string with a list of integers separated by commas of the rows to be taken.
	 * @param Scols A string with a list of integers separated by commas of the columns to be taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrix(String Srows, String Scols) {
		return MatrixComplexEquationSystems.subMatrix(this, Srows, Scols);
	}

	/**
	 * Returns TRUE if rowIdx has only zeros
	 * @param rowIdx The row to check
	 * @return True if is null
	 */
	public boolean isNullRow(int rowIdx) {
		return MatrixComplexEquationSystems.isNullRow(this, rowIdx);
	}

	/**
	 * Returns TRUE if colIdx has only zeros
	 * @param colIdx The col to check
	 * @return True if is null
	 */
	public boolean isNullCol(int colIdx) {
		return MatrixComplexEquationSystems.isNullCol(this, colIdx);
	}

	/**
	 * Removes every row and col which are null
	 * @return The reduced matrix without null rows and cols
	 */
	public MatrixComplex reduce() {
		return MatrixComplexEquationSystems.reduce(this);
	}
	
	/**
	 * Calculates the rank of an array.
	 * @return The rank of the matrix.
	*/
	public int rank() {
		return MatrixComplexRank.rank1(this);
	}

	/**
	 * Scale-aware rank via a relative-pivot criterion (a single {@code triangleUp()} pass; a pivot
	 * counts as non-negligible only if it is not vanishingly small RELATIVE to the largest pivot
	 * found). NOT a general-purpose replacement for {@link #rank()}: safe only for a square matrix
	 * whose entries genuinely share one physical scale throughout, such as {@code A-lambda*I} for
	 * an eigenvalue -- {@code rank()}'s own fixed-absolute-epsilon zero test can otherwise miss a
	 * residual pivot left behind by an imprecise eigenvalue (confirmed with a real 7x7 case,
	 * 8 agosto 2026, ver Claude/ComplexArithRev.md: {@code geometricMultiplicity()} computed 0
	 * instead of 1 for a genuine eigenvalue). Introduced specifically for
	 * {@code Eigenspace.geometricMultiplicity()}; a matrix without a coherent scale (e.g. a set of
	 * eigenvector candidates mixing normalized and genuinely-tiny components) should keep using
	 * {@link #rank()}.
	 * @return The relative-pivot rank.
	 */
	public int rankNearSingular() {
		return MatrixComplexUnary.rankByRelativePivot(this);
	}

	/**
	 * Calculates the rank of an array. It is not reliable for ill-conditioned matrix due to lack of precision
	 * Kept for testing proposes
	 * @return The rank of the matrix.
	 */
	public int rank0() {
		return MatrixComplexRank.rank0(this);
	}

	/**
	 * Major Independent Lineal submatrix. Traverse the different minors of the matrix untils the first not dependent linear minor
	 * @return The major independet lineal minor
	 */
	public MatrixComplex majorIL() {
		return MatrixComplexRank.majorIL(this);
	}

	/**
	 * Calculates the rank of an array.
	 *  TEST FAILED FIXED
	 * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	 * :                          TEST #2853                           :
	 * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	 * rank1 = 4
	 * rank2 = 3
	 * CMPLXAR: new MatrixComplex("1.00,-1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,1.00,-1.00,1.00;-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,-1.00");
	 * MAXIMA : rank(matrix([1.00,-1.00,1.00,1.00,-1.00,1.00],[1.00,-1.00,1.00,1.00,-1.00,1.00],[-1.00,1.00,1.00,1.00,-1.00,1.00],[1.00,-1.00,-1.00,-1.00,1.00,-1.00],[-1.00,1.00,-1.00,1.00,-1.00,-1.00]))
	 * OCTAVE : rank([1.00,-1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,1.00,-1.00,1.00;-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,-1.00])
	 * WOLFRAM: MatrixRank[{{1.00,-1.00,1.00,1.00,-1.00,1.00},{1.00,-1.00,1.00,1.00,-1.00,1.00},{-1.00,1.00,1.00,1.00,-1.00,1.00},{1.00,-1.00,-1.00,-1.00,1.00,-1.00},{-1.00,1.00,-1.00,1.00,-1.00,-1.00}}]
	 * *****************************************************************
	 * |                          TEST #3648                           |
	 * *****************************************************************
	 * rank1 = 4
	 * rank2 = 3
	 * CMPLXAR: new MatrixComplex("1.00,1.00,-1.00,1.00,1.00,1.00;1.00,1.00,-1.00,1.00,1.00,1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;-1.00,-1.00,1.00,-1.00,-1.00,-1.00;-1.00,-1.00,1.00,-1.00,-1.00,1.00");
	 * MAXIMA : rank(matrix([1.00,1.00,-1.00,1.00,1.00,1.00],[1.00,1.00,-1.00,1.00,1.00,1.00],[-1.00,1.00,-1.00,1.00,-1.00,1.00],[-1.00,-1.00,1.00,-1.00,-1.00,-1.00],[-1.00,-1.00,1.00,-1.00,-1.00,1.00]))
	 * OCTAVE : rank([1.00,1.00,-1.00,1.00,1.00,1.00;1.00,1.00,-1.00,1.00,1.00,1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;-1.00,-1.00,1.00,-1.00,-1.00,-1.00;-1.00,-1.00,1.00,-1.00,-1.00,1.00])
	 * WOLFRAM: MatrixRank[{{1.00,1.00,-1.00,1.00,1.00,1.00},{1.00,1.00,-1.00,1.00,1.00,1.00},{-1.00,1.00,-1.00,1.00,-1.00,1.00},{-1.00,-1.00,1.00,-1.00,-1.00,-1.00},{-1.00,-1.00,1.00,-1.00,-1.00,1.00}}]
	 * *****************************************************************
	 * |                          TEST #7425                           |
	 * *****************************************************************
	 * rank1 = 4
	 * rank2 = 3
	 * CMPLXAR: new MatrixComplex("-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,1.00,-1.00,-1.00;1.00,-1.00,1.00,1.00,-1.00,-1.00");
	 * MAXIMA : rank(matrix([-1.00,1.00,1.00,1.00,-1.00,1.00],[1.00,-1.00,1.00,-1.00,1.00,-1.00],[-1.00,1.00,-1.00,1.00,-1.00,1.00],[1.00,-1.00,-1.00,1.00,-1.00,-1.00],[1.00,-1.00,1.00,1.00,-1.00,-1.00]))
	 * OCTAVE : rank([-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,1.00,-1.00,-1.00;1.00,-1.00,1.00,1.00,-1.00,-1.00])
	 * WOLFRAM: MatrixRank[{{-1.00,1.00,1.00,1.00,-1.00,1.00},{1.00,-1.00,1.00,-1.00,1.00,-1.00},{-1.00,1.00,-1.00,1.00,-1.00,1.00},{1.00,-1.00,-1.00,1.00,-1.00,-1.00},{1.00,-1.00,1.00,1.00,-1.00,-1.00}}]
	 * @return The rank of the matrix.
	 */
	public int rank1() {
		return MatrixComplexRank.rank1(this);
	}

	/**
	 * The rank of A is equal the number of non-zero singular values of the characteristic polynomial of A.adjoint()*A
	 * This is method used for other numerical programs
	 * Kept for testing proposes
	 * @return The rank of the matrix.
	 */
	public int rank2() {
		return MatrixComplexRank.rank2(this);
	}

	/**
	 * Calculates the nullity of a Vectorial Space.
	 * @return The nullity of the Vectorial Space.
	 */
	public int nullity() {
		return MatrixComplexRank.nullity(this);
	}

	/**
	 * Checks if the matrix is upper triangular.
	 * @return true if the matrix is upper triangular, false otherwise.
	 */
	public boolean isTriangleUp() {
		return MatrixComplexRank.isTriangleUp(this);
	}

	/**
	 * Sorts the rows of an array so that those rows whose elements are all zeros occupy the highest positions in the array
	 * @return The array with the null rows at the top
	 */
	public MatrixComplex hollow() {
		return MatrixComplexRank.hollow(this);
	}

	/**
	 * Sorts the rows of an array so that those rows whose elements are all zeros occupy the lowest positions in the array
	 * @return The array with the null rows at the end
	 */
	public MatrixComplex heap() {
		return MatrixComplexRank.heap(this);
	}

	/**
	 * Calculates the upper triangularization of the matrix.
	 * @return The upper triangularized matrix.
	 */
	public MatrixComplex triangleUp() {
		return MatrixComplexRank.triangleUp(this);
	}

	/**
	 * It Upper Triangularize  the matrix by rearranging its rows so that they occupy the place corresponding to their non-zero element on the diagonal
	 * @return The perfect upper triangularized array
	 */
	public MatrixComplex triangleUpPerfect() {
		return MatrixComplexRank.triangleUpPerfect(this);
	}

	/**
	 * Checks if the matrix is lower triangular.
	 * @return true if the matrix is lower triangular, false otherwise.
	 */
	public boolean isTriangleLo() {
		return MatrixComplexRank.isTriangleLo(this);
	}

	/**
	 * Calculates the lower triangularization of the matrix.
	 * @return The lower triangularized matrix.
	 */
	public MatrixComplex triangleLo(){
		return MatrixComplexRank.triangleLo(this);
	}

	/**
	 * Indicates if the array is square or nor
	 * @return true for square matrix, false otherwise
	 */
	public boolean isSquare() {
		return MatrixComplexRank.isSquare(this);
	}

	/**
	 * Gram-Schmidt orthogonalization process via Gaussian elimination.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtGauss() {
		return MatrixComplexOrtho.gramSchmidtGauss(this);
	}

	/**
	 * Gram-Schmidt orthogonalization process.
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * It uses the MatrixComplex dotprod column oriented
	 * Use the Column calc for the dotprod. Otherwise dotprod needs work with transposed
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidt() {
		return MatrixComplexOrtho.gramSchmidt(this);
	}

	/**
	 * Gram-Schmidt Full orthogonalization process. Full means that the not included vectors of the base are randomly generated with integers between 0 and 9.
	 * The calculated orthogonal matrix is extended to the full dimension of the matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtFull() {
		return MatrixComplexOrtho.gramSchmidtFull(this);
	}

	/**
	 * Gram-Schmidt Full Modified orthogonalization process. Modified Algorithm (http://www.ehu.eus/izaballa/Ana_Matr/Apuntes/lec6.pdf)
	 * The calculated orthogonal matrix is extended to the full dimension of the matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtMFull() {
		return MatrixComplexOrtho.gramSchmidtMFull(this);
	}

	/**
	 * Gram-Schmidt Modified orthogonalization process. Modified Algorithm (http://www.ehu.eus/izaballa/Ana_Matr/Apuntes/lec6.pdf)
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * @return The matrix with the orthogonal basis that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtM() {
		return MatrixComplexOrtho.gramSchmidtM(this);
	}

	/**
	 * Shortcut to the preferred orthogonalization method
	 * @return The orthogonal Matrix
	 */
	public MatrixComplex orthogonalize() {
		return MatrixComplexOrtho.orthogonalize(this);
	}

	/**
	 * Shortcut to normalize method.
	 * @return The normalized matrix.
	 */
	public MatrixComplex normalize() {
		return MatrixComplexOrtho.normalize(this);
	}

	/**
	 * Shortcut to the preferred orthonormalization method
	 * @return The orthonormal Matrix
	 */
	public MatrixComplex orthonormalize() {
		return MatrixComplexOrtho.orthonormalize(this);
	}

	/**
	 * Normalizes the matrix by columns using the Euclidean norm.
	 * @return The normalized matrix.
	 */
	public MatrixComplex normalizeByCols() {
		return MatrixComplexOrtho.normalizeByCols(this);
	}

	/**
	 * Normalizes the matrix by rows using the Euclidean norm.
	 * @return The normalized matrix.
	 */
	public MatrixComplex normalizeByRows() {
		return MatrixComplexOrtho.normalizeByRows(this);
	}

	/**
	 * We define here A ⊗ B, the Kronecker product of two square matrices A = (ai,j) and B of dimension nA and nB,
	 * respectively: A ⊗ B is the square matrix of dimension nA nB obtained from A by replacing every entry ai,j by ai,j B.
	 * https://www.sciencedirect.com/topics/mathematics/kronecker-product
	 * @param matrix
	 * @return
	 */
	public MatrixComplex kroneckerprod(MatrixComplex matrix) {
		return MatrixComplexKernel.kroneckerprod(this, matrix);
	}

	/**
	 * Calulates the Kernel of a base
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The kernel vector components
	 */
	public MatrixComplex kernel(Complex lambda) {
		return MatrixComplexKernel.kernel(this, lambda);
	}

	/**
	 * Calulates the Kernel of a base using Complex.ONE as seed
	 * @return The kernel vector components
	 */
	public MatrixComplex kernel() {
		return MatrixComplexKernel.kernel(this);
	}

	/**
	 * Shortcut to kernel() method
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The kernel vector components
	 */
	public MatrixComplex ker(Complex lambda) {
		return MatrixComplexKernel.ker(this, lambda);
	}

	/**
	 * Shortcut to kernel() method
	 * @return The kernel vector components
	 */
	public MatrixComplex ker() {
		return MatrixComplexKernel.ker(this);
	}

	/**
	 * Computes a genuine BASIS of the nullspace (kernel) of this matrix via Gauss-Jordan
	 * elimination to reduced row echelon form, tracking pivot vs. free columns explicitly.
	 * <p>
	 * Unlike {@link #kernel()}/{@link #kernel(Complex)} (which return a SINGLE vector, all free
	 * variables set to the same scalar {@code lambda} -- the right tool when the nullspace is
	 * known to be 1-dimensional, the common case throughout this project), this returns one
	 * independent vector per free column: {@code nullity()} rows, each with exactly one free
	 * variable set to {@code 1} and the rest to {@code 0}, back-substituted through the reduced
	 * pivot rows. Needed to generalize {@code Jordan.java} beyond geometric multiplicity 1, where
	 * a single scalar-parameterized vector can no longer span the eigenspace.
	 * @return A matrix with one basis vector per row ({@code cols()-rank()} rows, {@code cols()}
	 * columns each); empty (0 rows) if the nullspace is trivial.
	 */
	public MatrixComplex nullspaceBasis() {
		return MatrixComplexKernel.nullspaceBasis(this);
	}

	/**
	 * Same as {@link #nullspaceBasis()}, but the free-column test is relative to the matrix's own
	 * scale instead of a fixed absolute epsilon. NOT a general-purpose replacement for
	 * {@code nullspaceBasis()}: safe only for a matrix whose entries genuinely share one physical
	 * scale, such as {@code A-lambda*I} for an eigenvalue -- see
	 * {@code MatrixComplexKernel.nullspaceBasisNearSingular()} and
	 * {@link #rankNearSingular()} for the full rationale (8-9 agosto 2026, ver
	 * Claude/ComplexArithRev.md).
	 * @return A matrix with one basis vector per row, using the relative-pivot criterion.
	 */
	public MatrixComplex nullspaceBasisNearSingular() {
		return MatrixComplexKernel.nullspaceBasisNearSingular(this);
	}

	/*
	 * ***********************************************
	 * CHARACTERISTIC POLYNOMIAL
	 * ***********************************************
	 */
	
	/**
	 * Returns the characteristic polynomial of the matrix.
	 * The characteristic polynomial of a square matrix is a polynomial which is invariant under matrix similarity and has the eigenvalues as roots. It has the determinant and the trace of the matrix as coefficients. The characteristic polynomial of an endomorphism of vector spaces of finite dimension is the characteristic polynomial of the matrix of the endomorphism over any base; it does not depend on the choice of a basis
	 * @return The characteristic polynomial
	 */
	public Polynom charactPoly() {
		return MatrixComplexCharPoly.charactPoly(this);
	}

	/**
	 * Returns a new augmented matrix.
	 * Copy the original matrix and add a column to zeros.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment() {
		return MatrixComplexCharPoly.augment(this);
	}

	public MatrixComplex augment(int numCols) {
		return MatrixComplexCharPoly.augment(this, numCols);
	}

	/**
	 * Shortcut to the default augment method
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment(MatrixComplex interms) {
		return MatrixComplexCharPoly.augment(this, interms);
	}

	/**
	 * DEPRECATED Returns a new augmented array with the FIRST column of terms.
	 * DEPRECATED Copies the original matrix and add the FIRST column "interms".
	 * DEPRECATED Left only for fail recovery of augment1()
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment1(MatrixComplex interms) {
		return MatrixComplexCharPoly.augment1(this, interms);
	}

	/**
	 * Returns a new augmented array with ALL the columns of terms.
	 * Copies the original matrix and add the ALL the columns of "interms".
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment2(MatrixComplex interms) {
		return MatrixComplexCharPoly.augment2(this, interms);
	}

	/**
	 * Returns a new array with the terms of the unknowns.
	 * Copy the original matrix and remove the column "interms".
	 * @return The augmented matrix.
	 */
	public MatrixComplex unkMatrix() {
		return MatrixComplexCharPoly.unkMatrix(this);
	}

	/**
	 * Calculates the new cofactor matrix.
	 * @return The new cofactor matrix.
	 */
	public MatrixComplex cofactor() {
		return MatrixComplexCharPoly.cofactor(this);
	}

	/**
	 * Returns a new matrix of cofactors.
	 * @param includedRows The list with the indexes of the rows included in the new matrix.
	 * @return The new matrix of cofactors.
	 */
	public MatrixComplex cofactors(int[] includedRows) {
		return MatrixComplexCharPoly.cofactors(this, includedRows);
	}

	/**
	 * Returns the cofactors' matrix for a included row list given in string format.
	 * @param includedRowsList The indexes' list of the rows included in the new matrix as a comma separated string.
	 * @return The new matrix of cofactors.
	 */
	public MatrixComplex cofactors(String includedRowsList) {
		return MatrixComplexCharPoly.cofactors(this, includedRowsList);
	}

	/**
	 * Calculates the determinant of the cofactors' array generated with the included rows (minor)
	 * The determinant of some smaller square matrix, cut down from this by removing one or more of its rows.
	 * @param includedRows Array with the indexes of the included rows to generate the cofactors' matrix.
	 * @return The result of the determinant.
	 */
	public Complex minor(int[] includedRows) {
		return MatrixComplexCharPoly.minor(this, includedRows);
	}

	/**
	 * Returns the coefficient of order "order" of the characteristic polynomial from an array
	 * @param order The order of the coefficient
	 * @return The coefficient of the polynomial
	 */
	public Complex coefCP(int order) {
		return MatrixComplexCharPoly.coefCP(this, order);
	}

	/**
	 * Sorts from maximum to minimum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param col Index of the column to order.
	 */
	public void quicksort(int col) {
		MatrixComplexCharPoly.quicksort(this, col);
	}

	/**
	 * Sorts from maximum to minimum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param col Index of the column to order.
	 */
	public void quicksortdown(int col) {
		MatrixComplexCharPoly.quicksortdown(this, col);
	}

	/**
	 * Sorts from minimum to maximum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param col Index of the column to order.
	 */
	public void quicksortup(int col) {
		MatrixComplexCharPoly.quicksortup(this, col);
	}

	/**
	 * Calculates the Hermitian matrix (or self-adjoint matrix).
	 * The Hermitian matrix (or self-adjoint matrix) is a complex square matrix that is equal to its own conjugate transposeâ€”that is, the element in the i-th row and j-th column is equal to the complex conjugate of the element in the j-th row and i-th column, for all indices i and j.
	 * Hermitian matrices can be understood as the complex extension of real symmetric matrices.
	 * @return The Hermitian matrix.
	 */
	public MatrixComplex hermitian() {
		return MatrixComplexCharPoly.hermitian(this);
	}

	/**
	 * Calculates the skew-Hermitian or antihermitian.
	 * The skew-Hermitian or antihermitian if its conjugate transpose is equal to the original matrix, with all the entries being of opposite sign.[1] That is, the matrix A is skew-Hermitian if it satisfies the relation.
	 * @return The skew-Hermitian matrix.
	 */
	public MatrixComplex skewHermitian() {
		return MatrixComplexCharPoly.skewHermitian(this);
	}

	/**
	 * Calculates the commutator between two arrays.
	 * The commutator gives an indication of the extent to which a certain binary operation fails to be commutative.
	 * @param B the second array.
	 * @return The commutator array.
	 */
	public MatrixComplex commutator(MatrixComplex B) {
		return MatrixComplexCharPoly.commutator(this, B);
	}

	/**
	 * Calculates the anticommutator between two arrays.
	 * @param B the second array.
	 * @return The anticommutator array.
	 */
	public MatrixComplex anticommutator(MatrixComplex B) {
		return MatrixComplexCharPoly.anticommutator(this, B);
	}

	/*
	 * ***********************************************
	 * LINE EQUATION
	 * ***********************************************
	 */

	/**
	 * Calculates the General Form equation A0*x0 + A1*x1 +...+ An-1*xn-1 = An from a point and a normal vector (direction vector for 2 dimensions)
	 * @param point The point of the line or the surface
	 * @param vector The direction vector of the line or the normal vector of the surface
	 * @return The general form equation coefficients A0*x0 + A1*x1 +...+ An-1*xn-1 = An
	 */
	public MatrixComplex pointVector(MatrixComplex point, MatrixComplex vector) {
		int colLen = point.cols();
		int col;
		MatrixComplex lineEq = new MatrixComplex(1, colLen+1);
		for (col = 0; col < colLen; ++col)
			lineEq.complexMatrix[0][col] = vector.complexMatrix[0][col];
		lineEq.complexMatrix[0][col] = point.times(vector.transpose()).complexMatrix[0][0];
		return lineEq;
	}

	/**
	 * Calculates the General Form equation A0*x0 + A1*x1 +...+ An-1*xn-1 = An from a point and a normal vector (direction vector for 2 dimensions)
	 * @param point The point of the line or the surface as a string separated by commas
	 * @param vector The direction vector of the line or the normal vector of the surface as a string separated by commas
	 * @return The general form equation coefficients A0*x0 + A1*x1 +...+ An-1*xn-1 = An
	 */
	public MatrixComplex pointVector(String point, String vector) {
		MatrixComplex aPoint = new MatrixComplex();
		MatrixComplex aVector = new MatrixComplex();		

		aPoint = new MatrixComplex(point);
		aVector = new MatrixComplex(vector);
		return this.pointVector(aPoint, aVector);
	}

	/**
	 * Calculates the distance between a line or a surface expressed in general form and a point
	 * @param point expressed as a matrix complex
	 * @return the distance
	 */
	public double distance(MatrixComplex point) {
		Complex d1 = new Complex();
		int col;

		for (col = 0; col < point.cols(); ++col)
			d1 = d1.plus(this.complexMatrix[0][col].times(point.complexMatrix[0][col]));
		d1 = d1.minus(this.complexMatrix[0][col]);
		return d1.mod()/this.coefMatrix().norm();
	}

	/**
	 * Calculates the distance between a line or a surface expressed in general form and a point
	 * @param spoint The point expressed as a string separated by commas
	 * @return the distance
	 */
	public double distance(String spoint) {
		MatrixComplex point = new MatrixComplex(spoint);
		return this.distance(point);
	}

	/*
	 * ***********************************************
	 * ELEMENTARY ROW TRANSFORMATIONS
	 * ***********************************************
	 */

	/**
	 * Transformation FSwapff(i,j) it swaps rows i and j of this matrix A ∈ C m × n and returns the result into a new Matrix.
	 * @param rowi Index of row i.
	 * @param rowj Index of row j.
	 * @return The transformed matrix.
	 */
	public MatrixComplex FSwapff(int rowi, int rowj) {
		int rowLen = this.rows();
		MatrixComplex Ftrans = new MatrixComplex(rowLen);
		MatrixComplex pivot = new MatrixComplex(1, rowLen);

		Ftrans.initMatrixDiag(1, 0);
		pivot.complexMatrix[0] = Ftrans.complexMatrix[rowi];
		Ftrans.complexMatrix[rowi] = Ftrans.complexMatrix[rowj];
		Ftrans.complexMatrix[rowj] = pivot.complexMatrix[0];
		Ftrans = Ftrans.times(this);
		return Ftrans;
	}

	/**
	 * Transformation FSwapf(i,j) it swaps rows i and j of this matrix A ∈ C m × n.
	 * @param rowi Index of row i.
	 * @param rowj Index of row j.
	 */
	public void FSwapf(int rowi, int rowj) {
		int rowLen = this.rows();
		MatrixComplex pivot = new MatrixComplex(1, rowLen);

		pivot.complexMatrix[0] = this.complexMatrix[rowi];
		this.complexMatrix[rowi] = this.complexMatrix[rowj];
		this.complexMatrix[rowj] = pivot.complexMatrix[0];
		this.mSign *= -1;
	}

	/**
	 * Transformation Ftransff(i,α) multiplies row i of this matrix A ∈ C m × n by a number α and returns the result into a new Matrix.
	 * @param row Index of row i.
	 * @param cNum The complex number α.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int row, Complex cNum) {
		int rowLen = this.rows();
		MatrixComplex Ftrans = new MatrixComplex(rowLen);

		Ftrans.initMatrixDiag(1, 0);
		Ftrans.setItem(row, row, cNum);
		Ftrans = Ftrans.times(this);
		return Ftrans;
	}

	/**
	 * Transformation Ftransff(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α in text format and returns the result into a new Matrix.
	 * @param row The index of row i.
	 * @param sNum The complex number α in text format.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int row, String sNum) {
		Complex cNum = new Complex(sNum);
		return this.Ftransff(row, cNum);
	}

	/**
	 * Transformation Ftransff(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α and returns the result into a new Matrix.
	 * @param row The index of row i.
	 * @param dNum The number α.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int row, double dNum) {
		return Ftransff(row, new Complex(dNum,0));
	}

	/**
	 * Transformation Ftransf(i,α) multiplies row i of this matrix A ∈ C m × n by a number α.
	 * @param row Index of row i.
	 * @param cNum The complex number α.
	 */
	public void Ftransf(int row, Complex cNum) {
		int colLen = this.cols();
		
		for(int col = 0; col < colLen; ++col)
			this.setItem(row, col, this.getItem(row, col).times(cNum));
	}

	/**
	 * Transformation Ftransf(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α in text format.
	 * @param row The index of row i.
	 * @param sNum The complex number α in text format.
	 */
	public void Ftransf(int row, String sNum) {
		Complex cNum = new Complex(sNum);
		this.Ftransf(row, cNum);
	}

	/**
	 * Transformation Ftransf(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α in text format.
	 * @param row The index of row i.
	 * @param dNum The number α.
	 */
	public void Ftransf(int row, double dNum) {
		Ftransf(row, new Complex(dNum,0));
	}

	/**
	 * Transformation Ftransff(i,j,α) Adds to row i of a matrix A ∈ C m × n its row j multiplied by the complex α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param cNum The complex number α.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int rowi, int rowj, Complex cNum) {
		int rowLen = this.rows();
		MatrixComplex Ftrans = new MatrixComplex(rowLen);

		Ftrans.initMatrixDiag(1, 0);
		Ftrans.setItem(rowi,rowj,cNum);
		Ftrans = Ftrans.times(this);
		return Ftrans;
	}

	/**
	 * Transformation Ftransff(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α != 0 in string format.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param sNum The complex number α in text format.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int rowi, int rowj, String sNum) {
		Complex cNum = new Complex(sNum);    	
		return this.Ftransff(rowi, rowj, cNum);
	}    

	/**
	 * Transformation Ftransff(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param dNum The number α. 
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int rowi, int rowj, double dNum) {
		return this.Ftransff(rowi, rowj, new Complex(dNum, 0));
	}

	/**
	 * Transformation Ftransf(i,j,α) Adds to row i of a matrix A ∈ C m × n its row j multiplied by the complex α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param cNum The complex number α.
	 */
	public void Ftransf(int rowi, int rowj, Complex cNum) {
		int colLen = this.cols();
		
		for (int col = 0; col < colLen; ++col)
			this.setItem(rowi, col, this.getItem(rowi, col).plus(this.getItem(rowj, col).times(cNum)));
	}

	/**
	 * Transformation Ftransf(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α in string format.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param sNum The complex number α in text format.
	 */
	public void Ftransf(int rowi, int rowj, String sNum) {
		Complex cNum = new Complex(sNum);    	
		this.Ftransf(rowi, rowj, cNum);
	}    

	/**
	 * Transformation F(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param dNum The number α
	 */
	public void Ftransf(int rowi, int rowj, double dNum) {
		this.Ftransf(rowi, rowj, new Complex(dNum, 0));
	}
	
	/*
	 * ***********************************************
	 * GENERAL PORPOUSE METHODS
	 * ***********************************************
	 */



}
