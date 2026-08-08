/**
 * 
 */
package com.ipserc.arith.matrixcomplex;



/**
 * @author ipserc
 *
 */

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.factorization.QRSchurfactor;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.arith.syseq.Syseq;
import com.ipserc.arith.vectorcomplex.*;

public class Eigenspace extends MatrixComplex {

	private final static String HEADINFO = "Eigenspace --- INFO: ";
	private final static String VERSION = "1.13 (2026_0808_1019)";
	/* VERSION Release Note
	 *
	 * 1.13 (2026_0808_1019)
	 * eigenval(): el agrupamiento de raices crudas paso de CADENA por orden de modulo (compara solo
	 * contra el elemento inmediatamente anterior tras ordenar, VERSION 1.10) a COMPONENTES CONEXAS
	 * (Union-Find sobre TODOS los pares dentro de tolerancia). Investigado a peticion del usuario
	 * tras encontrar y arreglar la misma debilidad en Polynom.solveStatistic() (8 agosto 2026, ver
	 * Claude/ComplexArithRev.md): 2 estimaciones crudas en lados opuestos del "anillo" de dispersion
	 * de una raiz repetida pueden compartir modulo casi identico (y por tanto caer adyacentes tras
	 * ordenar) pese a estar lejos entre si -- rompe la cadena justo donde no deberia, sin que ningun
	 * umbral lo arregle.
	 * Confirmado con el mismo metodo de calibracion que en Polynom (matrices de autovalor conocido
	 * via A=P*D*P^-1, multiplicidad 2-9, magnitud 1-30, mas un caso multi-cluster de 3 multiplicidades
	 * simultaneas): el motor POR DEFECTO (QRSchurfactor) nunca disparo el fallo en 28 casos (sus
	 * estimaciones ya son mucho mas ajustadas que las de un buscador de raices sobre el polinomio
	 * caracteristico -- consecuencia feliz de la propia arquitectura QR-con-desplazamientos, VERSION
	 * 1.10). El motor de RESERVA (charactPoly().solveRobust(), el mismo Durand-Kerner/Aberth-Ehrlich
	 * que usa Polynom) SI lo disparaba: con el mismo criterio de agrupamiento de siempre, la mayoria
	 * de casos con multiplicidad >=3 no se agrupaban en absoluto. Con componentes conexas, el motor
	 * de reserva recupera correctamente multiplicidad 2-4 (antes fallaba incluso en multiplicidad 2);
	 * multiplicidad >=5 sigue sin recuperarse ahi, pero es el mismo techo de precision ya aceptado y
	 * documentado en BEST_NUM_DECS_CAP ("multiplicidad 3+ sigue fallando aproximadamente igual"), no
	 * un hueco nuevo que abra o deje este cambio.
	 * Riesgo real pero estrecho: eigenval() solo cae al motor de reserva cuando QRSchurfactor lanza
	 * excepcion (documentado como raro, 5 matrices encontradas en una busqueda acotada, Decima
	 * sesion) -- si ademas esa matriz tiene un autovalor repetido, heredaba en silencio el mismo bug
	 * de fragmentacion ya arreglado en Polynom.
	 * Sin cambio en el motor por defecto (verificado: 28/28 casos identicos antes y despues). Añadido
	 * groupingFind() (Union-Find con compresion de camino), mismo helper que Polynom.groupingFind().
	 * arithmeticMultiplicity(Complex,int) sin cambios -- ya operaba sobre el array `eigenvalues` ya
	 * agrupado, no sobre las raices crudas, asi que no formaba parte del bug.
	 * Verificado: bateria de 19 ficheros (TestJordanAudit01, TestJordan01/02, TestJordanGeomMult01,
	 * TestQRSchur01, TestDiag01, TestDiagonal01, TestEigenspaceQRCompare01, TestEigenspaceFallback01,
	 * TestEigenV01-10) -- 18/19 exit=0, TestJordan01 reproduce exit=1 identico byte a byte contra el
	 * Eigenspace.java de HEAD sin este cambio (fallo preexistente de checkReconstruction(), no
	 * relacionado). ScratchEigenspaceGroupC01.java (conservado en src/TestComplex/) documenta el
	 * hallazgo completo (motor por defecto vs reserva, antes/despues del arreglo).
	 *
	 * 1.12 (2026_0806_0800)
	 * eigenvector(int): new fail-loud guard for a DIFFERENT "too few" failure mode than VERSION
	 * 1.11's "too many" one -- found while investigating why Schurfactor.factorize() opaquely
	 * failed on a well-conditioned 3x3 matrix (Decimoctava sesion, 6 agosto 2026, ver
	 * ComplexArithRev.md). Root cause: eigenval()'s DISTANCE-based grouping (VERSION 1.10) merges
	 * genuinely DISTINCT eigenvalues into one spurious "repeated" eigenvalue whenever their
	 * spacing is smaller than groupingTolerance(bestNumDecs()) -- confirmed for THIS matrix by
	 * comparing raw QRSchurfactor roots (three real, distinct eigenvalues ~0.05-0.13 apart) against
	 * the post-grouping result (one eigenvalue, spurious multiplicity 3): bestNumDecs()==0 for a
	 * well-conditioned matrix (cond()<2) makes the tolerance a full 0.5, coarser than the actual
	 * spacing. That averaged eigenvalue isn't a real eigenvalue of the matrix, so its homogeneous
	 * system is correctly INCONSISTENT for every "copy" of it, leaving `solutions` entirely null
	 * and `eigenvectors` empty (0 rows) -- Schurfactor.Schur() then called eigenvector(0)
	 * unconditionally (needs it for its recursive algorithm) and hit a raw, opaque
	 * ArrayIndexOutOfBoundsException from MatrixComplex.getRow() with zero diagnostic context.
	 * FIRST ATTEMPT (reverted before commit): guarded this inside setEigenvectors() itself,
	 * mirroring VERSION 1.11's placement exactly -- wrong call, caught by testing before
	 * committing. An empty `eigenvectors` is legitimate AT THAT LEVEL: isDiagonaizable() (used by
	 * Diagfactor.diagonalize(), in turn used by MatrixComplex.power()) reads an all-null
	 * `solutions` as `solutions.determinant()==0` and correctly returns false ("not
	 * diagonalizable") WITHOUT ever calling eigenvector(idx) -- confirmed with a live repro:
	 * nearIdentity.power(2) succeeded via a graceful non-exceptional fallback BEFORE this
	 * investigation touched anything, and the setEigenvectors()-level guard broke that by throwing
	 * unconditionally at Eigenspace CONSTRUCTION time, not at actual misuse. Moved the guard to
	 * eigenvector(int idx) instead -- the actual point where a missing eigenvector is a real
	 * problem for the caller, not before. Verified both directions after the correction: the
	 * original crash (Schurfactor via eigenvector(0)) now throws a clear, diagnosable message
	 * instead of an opaque one; MatrixComplex.power(2) on the same matrix still succeeds exactly as
	 * before (byte-for-byte identical result), confirming the corrected fix touches only the
	 * actually-broken call path. Same "fail loud with a clear message, don't fix the precision
	 * ceiling here" pattern as VERSION 1.11 and the rest of the project's precedent for this kind
	 * of defective/imprecise-eigenvalue edge case.
	 *
	 * 1.11 (2026_0805_2000)
	 * setEigenvectors(): hallazgo colateral de la bateria de regresion de VERSION 1.10, investigado
	 * a peticion del usuario. Reproducido con un bloque de Jordan 4x4 puro (autovalor defectuoso,
	 * multiplicidad algebraica 4, geometrica real 1): ArrayIndexOutOfBoundsException opaco,
	 * confirmado PREEXISTENTE (identico contra HEAD antes de VERSION 1.10 -- el cambio anterior solo
	 * cambio que matrices concretas exponen el hueco, no lo creo). Causa raiz: Syseq.solution() (via
	 * MatrixComplexEquationSystems.solveGauss()'s rama INDETERMINATE, nbrOfSolutions()) calcula los
	 * grados de libertad contando ceros en la diagonal de triangleUpPerfect() -- para un autovalor
	 * defectuoso IMPRECISO, un sistema casi-singular puede hacer que esa cuenta SOBREESTIME la
	 * multiplicidad geometrica real, devolviendo mas filas "solucion" no nulas de las que
	 * solutions.rank() (un calculo de rango independiente) puede dar cabida en setEigenvectors().
	 * Fix acotado (a peticion explicita del usuario, valorado tambien un fix de fondo en
	 * nbrOfSolutions()/triangleUpPerfect() y descartado por tocar el nucleo de EQUATION SYSTEMS del
	 * que depende Syseq, mismo tipo de riesgo de tolerancia ad-hoc ya evitado antes en el proyecto):
	 * mismo patron "fallar alto con mensaje claro" ya usado en Jordan.checkReconstruction()/
	 * logTaylor()/logMercator() -- IllegalArgumentException diagnostica en vez del
	 * ArrayIndexOutOfBoundsException opaco, sin tocar el techo de precision de fondo.
	 *
	 * 1.10 (2026_0805_1800)
	 * eigenval(): dos cambios juntos, medidos antes de aplicar (ver Claude/ComplexArithRev.md,
	 * secciones "QR-con-desplazamientos: precision bruta vs. agrupamiento" y "Candidato real:
	 * agrupamiento por DISTANCIA"):
	 * (1) QRSchurfactor pasa a ser la fuente de autovalores POR DEFECTO (antes: solo fallback si
	 * solveRobust() lanzaba excepcion) -- invertido el orden try/catch. Medido contra los mismos
	 * ~600 casos de autovalor repetido real: precision bruta ~35-47x mejor, agrupamiento mult=3
	 * 49%->55%, mult=4 41%->53%, falsos positivos 6.8%->6.1%, CERO excepciones de factorizacion
	 * en el barrido -- gana en las tres metricas a la vez, sin contrapartida. solveRobust() se
	 * mantiene como fallback (mismo patron "prueba lo conocido, cae a lo robusto solo ante
	 * excepcion explicita") por si QRSchurfactor.factorize() lanza en algun caso no cubierto por
	 * el barrido.
	 * (2) El agrupamiento de raices consecutivas ("la misma raiz repetida") pasa de redondeo por
	 * COMPONENTE (Complex.round(a,digits).equals(Complex.round(b,digits))) a DISTANCIA
	 * (|a-b|<=tolFactor*10^-digits, tolFactor=0.5) -- diagnosticado que el redondeo por componente
	 * penaliza el autovalor repetido COMPLEJO el doble que el real (parte real Y parte imaginaria
	 * deben caer del mismo lado del cubo de redondeo para que el redondeo coincida, dos
	 * oportunidades independientes de fallar en vez de una) incluso cuando la precision bruta ya
	 * es buena. tolFactor=0.5 elegido por ser el punto medido que mejora o empata TODAS las
	 * metricas a la vez (real, complejo Y falsos positivos) -- tolFactor mas suelto (5-10x) llega
	 * a mult=3 casi perfecto pero empeora los falsos positivos, no elegido por eso. El agrupamiento
	 * pasa de "ancla" (comparaba contra el primer miembro fijo del grupo en curso) a "cadena"
	 * (compara siempre contra el elemento inmediatamente anterior, actualizado en cada paso) --
	 * necesario porque, a diferencia de la igualdad de redondeo (transitiva por construccion), la
	 * distancia NO es transitiva (|a-b|<=tol y |b-c|<=tol no implican |a-c|<=tol) y el barrido que
	 * midio estos numeros usaba cadena, no ancla.
	 * arithmeticMultiplicity(Complex,int) actualizado con el mismo criterio de distancia para
	 * mantenerse consistente con lo que eigenval() ya promete (mismo motivo que la Novena sesion
	 * para el redondeo). Diagfactor.java linea ~203 usa una comparacion equivalente (Complex.equals
	 * (Complex,int), tambien redondeo por componente) -- NO tocada aqui, fuera de alcance de este
	 * cambio, candidato aparte del mismo tipo si hiciera falta.
	 *
	 * 1.9 (2026_0802_1200)
	 * eigenval(): charactPoly.solveRobust() ahora tiene un fallback a QRSchurfactor.getEigenvalues()
	 * si lanza excepcion (Durand-Kerner Y Aberth-Ehrlich exhaustos, caso confirmado real para
	 * polinomios caracteristicos de grado>=10 con coeficientes muy dispares). No sustituye
	 * solveRobust() en el caso comun (mismo patron "prueba lo conocido, cae a lo robusto solo ante
	 * excepcion explicita" ya usado en solveRobust() mismo), solo rescata el caso donde ambos
	 * root-finders ya fallaban.
	 *
	 * 1.8 (2026_0802_1100)
	 * public MatrixComplex eigenvaluesQR() -- Etapa 4 del plan QR-con-desplazamientos: via
	 * ALTERNATIVA de autovalores (Hessenberg + QR con desplazamiento de Wilkinson, QRSchurfactor)
	 * puramente aditiva, para comparar contra el motor por defecto (polinomio caracteristico +
	 * Durand-Kerner/Aberth-Ehrlich, eigenval()/eigenvalues()) sin sustituirlo. No toca eigenval(),
	 * eigenvalues(), calculate() ni ningun otro metodo existente -- cero riesgo de regresion para
	 * los ~30 ficheros que ya dependen de esta clase.
	 *
	 * 1.7 (2026_0802_0131)
	 * eigenval() now solves the characteristic polynomial via Polynom.solveRobust() instead of
	 * solve() (Durand-Kerner only) -- same fix as MatrixComplex.rank2() (previous commit), same
	 * root cause (Durand-Kerner's arithmetic-overflow exception on higher-degree/widely-spread-
	 * coefficient polynomials). Confirmed with a live repro: TestDiag.java/TestDiag01.java were
	 * crashing outright (uncaught IllegalArgumentException from solveWeierstrass(), not a check
	 * failure) via Diagfactor -> Eigenspace's own constructor -- both now complete cleanly.
	 * Verified with the full Schurfactor/Jordan-block sweep already used this session to
	 * characterize Eigenspace's fragility: byte-for-byte identical results to plain Durand-Kerner
	 * in every case that already worked -- solveRobust() only ever falls back to Aberth-Ehrlich on
	 * cases that were already throwing, never changes an already-successful result. Does NOT fix
	 * the separate, already-documented precision ceiling for repeated eigenvalues in larger Jordan
	 * blocks (see Jordan.java/TestJordanAudit01, MatrixComplex.logm()) -- that is inherent to
	 * Newton-type root-finding near a multiple root, not an overflow.
	 *
	 * 1.6 (2026_0801_2252)
	 * Bug fix: eigenval() grouped consecutive roots into "the same eigenvalue" using
	 * Complex.equals()'s fixed ~1e-11 tolerance, tighter than Durand-Kerner's actual error on a
	 * genuinely repeated root (measured ~5.3e-9 for a double eigenvalue) -- silently splitting one
	 * defective eigenvalue of multiplicity 2 into two "eigenvalues" of multiplicity 1 each,
	 * corrupting Jordan.factorize() downstream (see Jordan.VERSION 1.2). Fixed to group with the
	 * same bestNumDecs()-based rounded comparison arithmeticMultiplicity() already promises, and to
	 * store the GROUP AVERAGE of the grouped roots as the representative eigenvalue instead of an
	 * arbitrary raw member (measurably closer to the true value).
	 *
	 * 1.5 (2025_0319_2345)
	 * 	public MatrixComplex eigenvector(int i) {
	 *  public void check() {
	 *  public void checkEigenvectors() {
	 * 
	 * 1.4 (2022_0123_0100)
	 * toMaxima_eigenvalues(boolean expand)
	 * toMaxima_eigenvectors(boolean expand)
	 * toMaxima_charpoly(boolean expand)
	 * toOctave_eigenvectors()
	 * toWolfram_eigenvectors() --> Eigenvectors[...]
	 * eigenvectors3(): Checks if (dMatrix.typeEqSys() == INCONSISTENT) and no eigenvector is calculated
	 * toWolfram_eigenvalues() --> Eigenvalues[...]
	 * toWolfram_eigensystem() --> Eigensystem[...]
	 * eigensolve() returns the eigenvectors normalized, if it is possible
	 * eigenvectors2() DEPRECATED - REMOVED
	 * geometricMultiplicity(Complex eigenVal) Based on rank
	 * geometricMultiplicityFromEVectors(Complex eigenVal) geometric multiplicity based on linearity independence of Eigenvectors
	 * root(int rootId) Returns the root rootId index
	 * 
	 *
	 * 1.3 (2021_0929_2100)
	 * eigensolve uses eigenvect3 which has changed MatrixComplex cMatrix, dMatrix; to Syseq dMatrix; for solving the system equations based on Syseq class.
	 * eigenvect2 is deprecated.
	 * 
	 */

	 /**
	  * Controls whether the eigenvectors are normalized or not
	  * Only the eigenvectors, the solutions rest as they were calculated
	  */
	 private static boolean normalizeVectors = false;

	 /**
	  * Sets the order in which the roots and solutions of the Characteristic polynomila are stored
	  */
	 private static Order order = Order.DOWN;

	/**
	 * Enumeration that gives the value of the order in which the eigenvalues, and therefore, the eigenvectors are returned
	 * DOWN: The eigenvalues are sorted from higher to lower
	 * UP: The eigenvalues are sorted from lower to higher
	 */
	public enum Order {DOWN, UP};
	
	private MatrixComplex roots; // The roots of the characteristics polynomial
	private MatrixComplex eigenvalues; // First Column:The eigenvalues, the roots with no repetitions, 2nd Column: The artihmetic multiplicity
	private MatrixComplex solutions; // The solution vectors of the eigen equations
	private MatrixComplex eigenvectors; // The eigenvectors taken from vectors by removing the null and the linear combination ones
	private Polynom charactPoly;
	private Complex seed;

	/**
	 * Factor multiplicativo sobre la tolerancia derivada de {@link MatrixComplex#bestNumDecs()}
	 * para el agrupamiento de raices por DISTANCIA (ver {@link #groupingTolerance(int)}). Valor
	 * medido, no arbitrario: barrido de tolFactor en {0.5,1,2,5,10} sobre ~600 casos de
	 * multiplicidad conocida (real y compleja) mas un barrido companion de autovalores distintos
	 * -- 0.5 es el unico valor que mejora o empata TODAS las metricas a la vez (agrupamiento real,
	 * agrupamiento complejo Y falsos positivos); valores mas sueltos ganan mas en agrupamiento pero
	 * pierden mas en falsos positivos. Ver Claude/ComplexArithRev.md, VERSION 1.10 de esta clase.
	 */
	private static final double GROUPING_TOL_FACTOR = 0.5;
	
	

	/*
	 * ***********************************************
	 * 	VERSION 
	 * ***********************************************
	 */
	
	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION); 
	}

	/*
	 * ***********************************************
	 * CONSTRUCTORS
	 * ***********************************************
	 */
	
	/**
	 * Instantiates an eigenspace class form a complex array previously defined.
	 * @param cmatrix The MatrixComplex that generates the Eigenspace
	 */
	public Eigenspace(MatrixComplex cmatrix) {
		super();
		this.complexMatrix = cmatrix.complexMatrix.clone();
		this.seed = Complex.ONE;
		calculate();
	}

	/**
	 * Instantiates an eigenspace class form a complex array previously defined.
	 * @param seed Is the value of the constant used to find the eigenvectors 
	 * @param cmatrix The MatrixComplex that generates the Eigenspace
	 */
	public Eigenspace(Complex seed, MatrixComplex cmatrix) {
		super();
		this.complexMatrix = cmatrix.complexMatrix.clone();
		this.seed = seed;
		calculate();
	}
	
	/**
	 * Instantiates an eigenspace class from matrix represented as a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix The matrix that generates the Eigenspace
	 */
	public Eigenspace(String strMatrix) {
		super(strMatrix);
		seed = Complex.ONE;
		calculate();		
	}
	
	/**
	 * Instantiates an eigenspace class from matrix represented as a string, rows are separated with ";", cols are separated with ",".
	 * @param seed Is the value of the constant used to find the eigenvectors 
	 * @param strMatrix The matrix that generates the Eigenspace
	 */
	public Eigenspace(Complex seed, String strMatrix) {
		super(strMatrix);
		this.seed = seed;
		calculate();
	}

	/*
	 * ***********************************************
	 * SETTERS
	 * ***********************************************
	 */
	
	 /**
	  * Sets the order in which the roots of the characteristic polynomial are found. DOWN means in decreasing order.
	  * Eigenvalues ​​are sorted only ONCE. Subsequent changes to the order affect only the roots.
	  */
	public static void setOrderDOWN() {
		order = Order.DOWN;
	}

	/**
	  * Sets the order in which the roots of the characteristic polynomial are found. UP in increasing order.
	  * Eigenvalues ​​are sorted only ONCE. Subsequent changes to the order affect only the roots.
	 */
	public static void setOrderUP() {
		order = Order.UP;
	}

	/**
	 * Sets whether the eigenvectors are normalized or not. It only affect to the eigenvectors, the roots remain as they were calculated.
	 * @param val True normalize eigenvetors, false keep as they were calculated.
	 */
	public static void setNormalize(boolean val) {
		normalizeVectors = val;
	}

	/*
	 * ***********************************************
	 * GETTERS
	 * ***********************************************
	 */

	 /**
	  * Retrieves the value of normalizeVectors which controls whether the eigenvectors are normalized or not. 
	  * @return The value of normalizeVectors.
	  */
	 public static boolean getNormalice() {
		return normalizeVectors;
	 }

	/**
	 * Private Method. Calculates the Eigenspace components. Characteristic Polynomial, Eigenvalues and Eigenvetors
	 */
	private void calculate() {
		charactPoly = this.charactPoly();
		eigenval();
		eigensolve();
	}
	
	/**
	 * Returns the solutions of the eigen system equations as a MatrixComplex with the vectors in the array rows
	 * @return The eigen system equations solutions
	 */
	public MatrixComplex solutions() {
		return solutions;
	}

	/**
	 * Returns ith the solutions of the eigen system equations as a MatrixComplex vector of 1 row 
	 * @param idx the index of the solution
	 * @return The ith solution
	 */
	public MatrixComplex solution(int idx) {
		MatrixComplex row; 
		if (idx >= solutions.rows()) return (MatrixComplex)null;
		row = new MatrixComplex(1, solutions.cols());
		row.complexMatrix[0] = solutions.complexMatrix[idx];
		return row;
	}

	/**
	 * Returns the eigenvectors as a MatrixComplex with the vectors in the array rows
	 * @return The eigenvectors
	 */
	public MatrixComplex eigenvectors() {
		return eigenvectors;
	}
	
	/**
	 * Returns the i-th eigenvector as a MatrixComplex one row
	 * @param idx The i-th eigenvector to get
	 * @return The i-th eigenvector
	 */
	public MatrixComplex eigenvector(int idx) {
		// Guarded here, not in setEigenvectors(): an empty `eigenvectors` is a legitimate result
		// there (isDiagonaizable() reads it as "not diagonalizable" without ever calling this
		// accessor, see setEigenvectors()'s own note) -- it only becomes an actual problem for a
		// caller that tries to fetch an eigenvector that doesn't exist, like Schurfactor.Schur(),
		// which needs eigenvector(0) unconditionally for its recursive algorithm. Without this
		// check, that caller hit an opaque ArrayIndexOutOfBoundsException from
		// MatrixComplex.getRow() instead of a diagnosable message.
		if (idx >= eigenvectors.rows()) {
			throw new IllegalArgumentException(HEADINFO + "eigenvector: requested eigenvector index "
				+ idx + " but only " + eigenvectors.rows() + " eigenvector(s) were found -- almost "
				+ "certainly a spurious eigenvalue produced by eigenval()'s DISTANCE-based grouping "
				+ "merging distinct-but-close roots (see bestNumDecs()) collapsing every eigenvalue's "
				+ "homogeneous system to INCONSISTENT, not a defect in this construction itself.");
		}
		return eigenvectors.getRow(idx).clone();
	}
	
	/**
	 * Returns the eigenvectors as a MatrixComplex with the vectors in the array rows
	 * @return The eigenvectors
	 */
 	public MatrixComplex base() {
		return solutions;
	}
	
	/**
	 * Returns the root rootId index from the roots of the characteristic polynomial
	 * @return The characteristic polynomial root rootId
	 */
	public MatrixComplex root(int rootId) {
		return roots.getRow(rootId);
	}
	
	/**
	 * Returns ALL the roots as a MatrixComplex with the roots of the characteristic polynomial in the rows of one column array
	 * @return The characteristic polynomial roots
	 */
	public MatrixComplex roots() {
		return roots;
	}

	/**
	 * Returns the eigenvalues as a MatrixComplex with the eigenvalues in the rows of one column array
	 * @return The eigenvalues
	 */
	public MatrixComplex eigenvalues() {
		return eigenvalues;
	}

	/**
	 * Via ALTERNATIVA de autovalores (Etapa 4 del plan QR-con-desplazamientos, ver
	 * {@code Claude/ComplexArithRev.md} / plan mutable-rolling-stardust.md): reduccion a Hessenberg
	 * + iteracion QR con desplazamiento de Wilkinson ({@code QRSchurfactor}), en vez del motor por
	 * defecto de esta clase (polinomio caracteristico + Durand-Kerner/Aberth-Ehrlich, ver
	 * {@link #eigenval()}/{@link #eigenvalues()}). NO sustituye al motor por defecto -- decision de
	 * alcance deliberada de esta etapa, pendiente de comparacion exhaustiva antes de considerarlo.
	 * <p>
	 * A diferencia de {@link #eigenvalues()}, devuelve un valor por FILA de la matriz (una entrada
	 * de la diagonal de la forma de Schur cada una), sin agrupar por multiplicidad ni promediar
	 * repeticiones -- para un autovalor de multiplicidad {@code m}, aparecera {@code m} veces
	 * (aproximadamente iguales, no exactamente, igual que las raices crudas de
	 * {@link #roots()} antes de agrupar).
	 * @return Los autovalores, uno por fila, en el orden en que aparecen en la diagonal de T.
	 * @throws IllegalArgumentException si la matriz no es cuadrada, o si la iteracion QR no converge
	 * (ver {@code QRSchurfactor.factorize()}).
	 */
	public MatrixComplex eigenvaluesQR() {
		return new QRSchurfactor(this).getEigenvalues();
	}

	/**
	 * Gets the eigenvalue index "eigValId" from the eigenvalues array
	 * @param eigValId
	 * @return
	 */
	public Complex getEigenValue(int eigValId) {
		int evvCol = 0;
		return eigenvalues().getRow(eigValId).complexMatrix[0][evvCol];
	}
	
	/**
	 * Gets the arithmetic mutltiplicity of eigenvalue index "eigValId" from the eigenvalues array
	 * @param eigValId
	 * @return
	 */
	public int getEigenValueArithMult(int eigValId) {
		int evamCol = 1;
		return (int)eigenvalues().getRow(eigValId).complexMatrix[0][evamCol].cre();
	}
	
	
	/**
	 * Returns the characteristic polynomial as a polynomial
	 * @return The characteristic polynomial
	 */
	public Polynom getCharactPoly() {
		return charactPoly;
	}
	
	/**
	 * Gets the order in which the eigenvalues are sorted. The eigenvectors follow this order
	 * @return the order in which the eigenvalues are stored
	 */
	public static Order order() {
		return order;
	}
	
	public String getOrder() {
		return order.toString();
	}
	
	
	/*
	 * ***********************************************
	 * OPERATORS
	 * ***********************************************
	 */

	/**
	 * Returns the arithmetic multiplicity of an specific eigenvalue
	 * @param eigenVal The value to evaluate the arithmetic multiplicity
	 * @return The arithmetic multiplicity
	 */
	public int arithmeticMultiplicity(Complex eigenVal) {
		return arithmeticMultiplicity(eigenVal, this.bestNumDecs());
	}
	
	/**
	 * Returns the arithmetic multiplicity of an specific eigenvalue using an specific number of decimals of precision
	 * @param eigenVal The value to evaluate the arithmetic multiplicity
	 * @param digits The number of decimals of precision
	 * @return The arithmetic multiplicity
	 */
	public int arithmeticMultiplicity(Complex eigenVal, int digits) {
		double tol = groupingTolerance(digits);
		int row;
		for (row = 0; row < eigenvalues.rows(); ++row) {
			if (eigenVal.minus(eigenvalues.getItem(row, 0)).mod() <= tol) break;
		}
		return (int)eigenvalues.getItem(row, 1).cre();
	}

	/**
	 * Tolerancia de DISTANCIA equivalente a "digits" decimales, para el agrupamiento de raices en
	 * {@link #eigenval()} y la busqueda en {@link #arithmeticMultiplicity(Complex, int)}. Ver
	 * {@link #GROUPING_TOL_FACTOR} para el porque del factor.
	 * @param digits El numero de decimales de precision (tipicamente {@link MatrixComplex#bestNumDecs()}).
	 * @return La tolerancia de distancia.
	 */
	private static double groupingTolerance(int digits) {
		return GROUPING_TOL_FACTOR * Math.pow(10, -digits);
	}

	/**
	 * Union-Find "find" with path compression, for {@link #eigenval()}'s clustering -- same helper
	 * as {@code Polynom.groupingFind(int[], int)}.
	 * @param parent The union-find parent array.
	 * @param i The element to find the representative of.
	 * @return The representative (root) index of {@code i}'s set.
	 */
	private static int groupingFind(int[] parent, int i) {
		while (parent[i] != i) {
			parent[i] = parent[parent[i]];
			i = parent[i];
		}
		return i;
	}

	public int arithmeticMultiplicity__(Complex eigenVal, int digits) {
		final boolean DEBUG_ON = false;
		final String METH_NAME = "arithmeticMultiplicity";
		int arithMult = 0;
		for (int i = 0; i < this.rows(); ++i) {
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				Complex.storeFormatStatus();
				Complex.setFixedOFF(); ;
				eigenvalues.getItem(i,0).println(HEADINFO + METH_NAME + ":eigenvalues.getItem("+i+",0):");
				eigenVal.println(HEADINFO + METH_NAME + ":eigenVal:");
				eigenvalues.getItem(i,0).minus(eigenVal).println(HEADINFO + METH_NAME + ":value - eignval:");
				System.out.println("digits:" + digits);
				Complex.round(eigenvalues.getItem(i,0), digits).println(HEADINFO + METH_NAME + ":round(eigenvalues.getItem("+i+",0), "+ digits +"):");
				eigenVal.println(HEADINFO + METH_NAME + ":Complex.round(eigenVal, "+ digits + "):");
				Complex.restoreFormatStatus();
			}
			/* ------------- END DEBUGGING BLOCK ------------- */
				if (Complex.round(eigenvalues.getItem(i,0), digits).equals(Complex.round(eigenVal, digits))) ++arithMult;
		}

		return arithMult;
	}
	
	/**
	 * Returns the geometric multiplicity of an specific eigenvalue using the eigenvectors matrix
	 * The geometric multiplicity of an eigenvalue is the number of linearly independent eigenvectors associated with it. 
	 * That is, it is the dimension of the nullspace of A
	 * The method used here is get the eigenvectors associated to the eigenvalue and calculate the rank of the eigenvector basis
	 * @param eigenVal The value to evaluate the geometric multiplicity
	 * @return The geometric multiplicity
	 */
	public int geometricMultiplicityFromEVectors(Complex eigenVal) {
		final boolean DEBUG_ON = false;
		final String METH_NAME = "geometricMultiplicity";
		MatrixComplex eigenV = new MatrixComplex(solutions.rows(), solutions.cols());

		for (int i = 0; i < this.rows(); ++i) {
				if (eigenvalues.getItem(i,0).equals(eigenVal)) 
					eigenV.complexMatrix[i] = solutions.complexMatrix[i];
		}
		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			//Complex.storeFormatStatus();
			//Complex.setFixedOFF(); ;
			eigenVal.println(HEADINFO + METH_NAME + ":eigenVal:");
			eigenV.println(HEADINFO + METH_NAME + ":eigenV:");
			Complex.restoreFormatStatus();
		}
		/* ------------- END DEBUGGING BLOCK ------------- */
		return eigenV.rank();
	}

	/**
	 * This way of calculating the geometric multiplicity is previous toa calculate the eigenvectors
	 * It should be the preferred method, but right now the calculations are done with the eigenvectors
	 * already calculated, and this way of doing the things should be changed to make all the rest of
	 * things have sense.
	 * It relays on the accuracy of the rank method
	 * @param eigenVal
	 * @return
	 */
	public int geometricMultiplicity(Complex eigenVal) {
		return this.rows() - (this.minus(eye(this.rows()).times(eigenVal))).rank();
	}
	
	/**
	 * DEPRECATED - WILL BE REMOVED
	 * Determines whether a matrix is diagonalizable or not
	 * @return True if the matrix is diagonalizable, otherwise False
	 */
	public boolean isDiagonaizable() {
		boolean DEBUG_ON = false;
		if (solutions.determinant().equals(0,0)) {
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				System.out.println("vectors.determinant().equalsred(0,0)");
			}
			/* ------------- END DEBUGGING BLOCK ------------- */
			return false;
		}
		for (int id = 0; id < eigenvalues.rows(); ++id) {
			if (geometricMultiplicity(eigenvalues.getItem(id,0)) != arithmeticMultiplicity(eigenvalues.getItem(id,0))) return false;
		}
		return true;
	}
	
	/**
	 * Calculates the eigenvalue, characteristic value, or characteristic root associated with eigenvector v by solving the Characteristic Polynomial.
	 * An eigenvalue is a scalar associated with a given linear transformation of a vector space and having the property that there is some nonzero vector which when multiplied by the scalar is equal to the vector obtained by letting the transformation operate on the vector; especially :  a root of the characteristic equation of a matrix
	 * The eigenvalues as a column array
	 */
	public void eigenval() {
		// QRSchurfactor (Hessenberg + QR con desplazamiento de Wilkinson) es la fuente de
		// autovalores por defecto desde VERSION 1.10 -- nunca forma el polinomio caracteristico
		// (el paso que Wilkinson demostro numericamente traicionero), midiendo mejor precision Y
		// mejor agrupamiento que solveRobust() sin contrapartida (ver release note de arriba).
		// solveRobust() (Durand-Kerner con fallback a Aberth-Ehrlich) se mantiene como fallback
		// ante excepcion explicita, mismo patron "prueba lo conocido, cae a lo robusto" de siempre.
		try {
			roots = new QRSchurfactor(this).getEigenvalues();
		} catch (IllegalArgumentException e) {
			roots = charactPoly.solveRobust();
		}
		// eigenvalues.quicksortup(0); // DO NOT USE - SVD factorization doesn't allow this
		// order = Order.UP;
		// By default the order in which the eigenvalues are sorted is from Higher to Lower
		switch (order) {
			case UP: roots.quicksortup(0); break;
			case DOWN: roots.quicksortdown(0); break;
		}

		// Grouping raw roots into "the same eigenvalue" uses DISTANCE (groupingTolerance()), not
		// component-wise rounding -- see VERSION 1.10 release note above for why. Kept consistent
		// with arithmeticMultiplicity(Complex,int), which uses the same tolerance.
		//
		// CONNECTED COMPONENTS (Union-Find over every pair within tolerance), not a chain that only
		// compares each root against the immediately preceding one after sorting (VERSION 1.10-1.13
		// used the chain, ported from the same idea that motivated it here in the first place) --
		// see VERSION 1.13 release note below for why: 2 raw estimates on opposite sides of a
		// repeated root's "ring" of scatter can share near-identical modulus (and so land adjacent
		// after sorting) despite being far apart, fragmenting a genuine cluster the chain can never
		// re-join at any tolerance. Confirmed to reproduce here too (not just in Polynom, where it
		// was first found and fixed) when this method's own fallback path (charactPoly.solveRobust(),
		// same Durand-Kerner/Aberth-Ehrlich engine) supplies the raw roots -- the default QRSchurfactor
		// path was never observed to trigger it in the same sweep (its eigenvalue estimates are far
		// tighter, no meaningful "ring"), but connected components costs nothing extra there either.
		int digits = this.bestNumDecs();
		double tol = groupingTolerance(digits);

		int n = roots.rows();
		int[] parent = new int[n];
		for (int i = 0; i < n; ++i) parent[i] = i;
		for (int i = 0; i < n; ++i) {
			for (int j = i + 1; j < n; ++j) {
				if (roots.getItem(i, 0).minus(roots.getItem(j, 0)).mod() <= tol) {
					int ri = groupingFind(parent, i), rj = groupingFind(parent, j);
					if (ri != rj) parent[ri] = rj;
				}
			}
		}

		int rootCount = 0;
		for (int i = 0; i < n; ++i) if (groupingFind(parent, i) == i) ++rootCount;

		// With the number of different roots creates the eignevalues array
		// Col 0 for the eigenvalue, Col 1 for the arithmetic multiplicity
		// The representative eigenvalue stored per group is the AVERAGE of its grouped raw roots,
		// not just the first one encountered: Durand-Kerner's error on the two (or more) roots of a
		// genuinely repeated eigenvalue is not one-sided, so averaging cancels part of it (in the
		// -2 case documented above, the average lands ~5e-12 from the true value vs. ~2.6e-9 for
		// either raw root alone -- about 500x closer). Picking an arbitrary raw member of the group
		// was fine when grouping only fired for near-exact matches (the old ~1e-11 tolerance), but
		// is not precise enough once grouping uses the coarser bestNumDecs()-based tolerance above:
		// feeding an imprecise scalar into (A-eigenval*I) for a generalized-eigenvector chain
		// (Jordan.java) can make that matrix insufficiently close to singular, producing a
		// degenerate (all-zero) generalized eigenvector instead of a real one.
		eigenvalues = new MatrixComplex(rootCount, 2);
		boolean[] done = new boolean[n];
		int eigvalIdx = 0;
		for (int i = 0; i < n; ++i) {
			if (done[i]) continue;
			int group = groupingFind(parent, i);
			double sumRe = 0, sumIm = 0;
			int arithMult = 0;
			for (int k = 0; k < n; ++k) {
				if (groupingFind(parent, k) == group) {
					sumRe += roots.getItem(k, 0).rep();
					sumIm += roots.getItem(k, 0).imp();
					++arithMult;
					done[k] = true;
				}
			}
			eigenvalues.setItem(eigvalIdx, 0, new Complex(sumRe / arithMult, sumIm / arithMult));
			eigenvalues.setItem(eigvalIdx, 1, new Complex(arithMult, 0));
			++eigvalIdx;
		}

		// Union-Find grouping order isn't guaranteed sorted (unlike the old chain, which preserved
		// the sort above by construction) -- re-sort the collapsed array to keep the same UP/DOWN
		// contract eigenvalues() has always had.
		switch (order) {
			case UP: eigenvalues.quicksortup(0); break;
			case DOWN: eigenvalues.quicksortdown(0); break;
		}
	}

	/**
	 * Swaps the order of the roots and the solutions. Use 
	 */
	public void orderSwap() {
		MatrixComplex rootsTmp = roots.clone();
		MatrixComplex solutionsTmp = solutions.clone();
		// swap eigenvalues
		for (int row = 0; row < eigenvalues.rows(); ++row) {
			roots.complexMatrix[eigenvalues.rows() - row - 1] = rootsTmp.complexMatrix[row];
		}
		// swap vectors
		for (int row = 0; row < eigenvalues.rows(); ++row) {
			solutions.complexMatrix[eigenvalues.rows() - row - 1] = solutionsTmp.complexMatrix[row];
		}
		order = order == Order.DOWN? Order.UP : Order.DOWN;
	}
	
	/**
	 * Calculates the eigenvector or characteristic vector of a linear transformation associated to an specific eigenvalues.
	 * The eigenvector is a non-zero vector whose direction does not change when that linear transformation is applied to it
	 * The eigenvector as a column array
	 */
	public void eigensolve() {
		eigenvectors3();
	}

	/**
	 * Private method. Implementation of eigensolve
	 */
	private void eigenvectors3() {
		final boolean DEBUG_ON = false; 
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex I = MatrixComplex.eye(rowLen);
		MatrixComplex eigensolve;
		MatrixComplex cMatrix; 
		Syseq dMatrix;
		Complex eigenval;
		solutions = new MatrixComplex(rowLen, colLen);

		//for (int rowEig = 0, eigVallIdx = 0; rowEig < rowLen; ++eigVallIdx) {
		for (int rowEig = 0, eigVallIdx = 0; eigVallIdx < eigenvalues.rows(); ++eigVallIdx) {
			eigenval = eigenvalues.getItem(eigVallIdx, 0);
			//cMatrix = (I.times(eigenval)).minus(this); //- ORIGINAL
			cMatrix = (this.minus(I.times(eigenval)));
			dMatrix = new Syseq(cMatrix.augment().heap());
			
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				eigenval.println("\n" + HEADINFO + " * * * * * eigenval:");
				dMatrix.println(HEADINFO + "Eq. System for EigenVectors");
				dMatrix.triangle().println(HEADINFO + "Eq. System TRIANGLE for EigenVectors");
		     	System.out.println("MComplex:" + dMatrix.toMatrixComplex());
		     	System.out.println("MComplex:new Syseq(" + dMatrix.preMatrixComplex() +");");
				dMatrix.printSystemEqSolve(outputFormat.MAXIMA, true);
				dMatrix.printSystemEqSolve(outputFormat.OCTAVE, true);
				dMatrix.printSystemEqSolve(outputFormat.WOLFRAM, true);
			}
			/* ------------- END DEBUGGING BLOCK ------------- */

			if (dMatrix.typeEqSys() == INCONSISTENT) {
				rowEig += this.arithmeticMultiplicity(eigenval);
				continue;
			}
			eigensolve = dMatrix.solution(1);
			
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				System.out.println("Ec.Caract.["+rowEig+"]" + dMatrix.toMatrixComplex());
				eigensolve.println(HEADINFO + "eigensolve:");
			}
			/* ------------- END DEBUGGING BLOCK ------------- */
			
			//for (int sol = 0; sol < eigensolve.rows()-rowEig; ++sol) {
			for (int sol = 0; sol < eigensolve.rows(); ++sol) {
				/* -------------   DEBUGGING BLOCK   ------------- */
				if (DEBUG_ON) {
					System.out.println("rowEig.................................:" + rowEig);
					System.out.println("sol....................................:" + sol);
				}
				/* ------------- END DEBUGGING BLOCK ------------- */
				
				if (rowEig+sol >= solutions.rows()) break;
								
				solutions.complexMatrix[rowEig+sol] = eigensolve.complexMatrix[sol].clone();
				
				/* -------------   DEBUGGING BLOCK   ------------- */
				if (DEBUG_ON) {
					System.out.println("solutions.complexMatrix["+(rowEig+sol)+"]:" + solutions.getRow(rowEig+sol).getRow(0));
					System.out.println("eigensolve.complexMatrix["+sol+"].......:" + eigensolve.complexMatrix[sol][0]);
				}
				/* ------------- END DEBUGGING BLOCK ------------- */

			}
			//rowEig += eigensolve.rows();
			rowEig += this.arithmeticMultiplicity(eigenval);
		}
		/* ******************************************** */
		// Normalize the eigenvectors if possible
		if (normalizeVectors)
			solutions = solutions.normalizeByRows();
		/* ******************************************** */

		eigenvectors = setEigenvectors();
		
		// Call to the Garbage Collector
		Runtime.getRuntime().gc();
	}
	
	private MatrixComplex setEigenvectors() {
		int eigenvRows = solutions.rank();
		eigenvectors = new MatrixComplex(eigenvRows, this.cols());

		// Recorremos TODAS las soluciones
		for (int row = 0, i = 0; row < solutions.rows(); ++row) {
			// Pero solo son autovectores las soluciones NO NULAS
			if (solutions.isNullRow(row)) continue;
			else {
				// Asume que el numero de filas NO NULAS de "solutions" siempre coincide con
				// solutions.rank() -- normalmente cierto, pero puede romperse para un autovalor
				// defectuoso impreciso: Syseq.solution()/nbrOfSolutions() (via
				// triangleUpPerfect()'s deteccion de ceros en la diagonal) puede SOBREESTIMAR
				// los grados de libertad de un sistema casi-singular, devolviendo mas filas "no
				// nulas" en solutions que el rango real -- confirmado con un bloque de Jordan 4x4
				// puro (autovalor defectuoso, multiplicidad geometrica real 1) que revienta con un
				// ArrayIndexOutOfBoundsException opaco aqui mismo antes de este fix. Mismo patron
				// "fallar alto con mensaje claro, no arreglar el techo de precision de fondo" ya
				// usado en Jordan.checkReconstruction()/logTaylor()/logMercator() -- el problema
				// real (la imprecision del autovalor defectuoso) no es arreglable aqui, solo dejar
				// de propagar una excepcion opaca de indice.
				if (i >= eigenvRows)
					throw new IllegalArgumentException(HEADINFO + "setEigenvectors: found more "
						+ "non-null solution rows than solutions.rank()=" + eigenvRows
						+ " accounts for -- the eigenvalue(s) involved are almost certainly too "
						+ "imprecise for their multiplicity (Syseq.nbrOfSolutions() overestimating "
						+ "degrees of freedom for a near-singular system), not a defect in this "
						+ "construction itself.");
				eigenvectors.setRow(i++, solutions.getRow(row));
			}
		}

		// NOTE: deliberately NOT guarding "eigenvectors.rows()==0" here (tried, reverted -- see
		// eigenvector(idx) below for where this is actually guarded and why). An empty result IS
		// legitimate at this level: isDiagonaizable() (via Diagfactor.diagonalize(), no crash risk
		// there since it never calls eigenvector(idx)) relies on exactly this -- an all-null
		// `solutions` correctly makes `solutions.determinant()==0`, which isDiagonaizable() reads
		// as "not diagonalizable" and returns false WITHOUT ever touching `eigenvectors`. Throwing
		// here unconditionally at construction time broke that legitimate silent path (confirmed:
		// MatrixComplex.power(2) on a well-conditioned matrix whose eigenvalues are genuinely
		// DISTINCT but closer together than eigenval()'s DISTANCE-based grouping tolerance
		// collapses them into one spurious "repeated" eigenvalue -- its homogeneous system is
		// correctly INCONSISTENT, `eigenvectors` ends up empty, and BEFORE this investigation that
		// silently and correctly resolved to "not diagonalizable", falling back to plain matrix
		// multiplication -- turning that success into a thrown exception would have been a real
		// regression, not a fix).
		return eigenvectors;
	}
	
	/*
	 * ***********************************************
	 * PRINTING
	 * ***********************************************
	 */
	
	/**
	 * Returns the eigenvalues command as a String for the matrix to use in Maxima
	 * @param expand If true adds epxpand() to the expression
	 * @return The eigenvalues command for Maxima 
	 */
	public String toMaxima_eigenvalues(boolean expand) {
		String toMaxima = "eigval: eigenvalues("+this.toMaxima()+")";
		return expand ? "expand("+toMaxima+")" : toMaxima; 		
	}
	
	/**
	 * Returns the eigenvectors command as a String for the matrix to use in Maxima
	 * @param expand If true adds epxpand() to the expression
	 * @return The eigenvectors command for Maxima 
	 */
	public String toMaxima_eigenvectors(boolean expand) {
		String toMaxima = "eigvect: eigenvectors("+this.toMaxima()+")";
		return expand ? "expand("+toMaxima+")" : toMaxima; 		
	}
	
	/**
	 * Returns the charpoly command as a String for the matrix to use in Maxima
	 * @param expand If true adds epxpand() to the expression
	 * @return The charpoly command for Maxima 
	 */
	public String toMaxima_charpoly(boolean expand) {
		String toMaxima = "charpoly("+this.toMaxima()+",t)";
		return expand ? "expand("+toMaxima+")" : toMaxima;
	}
	
	/**
	 * Returns the eig command as a String for the matrix to use in Octave
	 * @return The eig command for Octave
	 */
	public String toOctave_eigenvectors() {
		return "[eVects,eVals] = eig("+this.toMatlab()+",\"vector\")";
	}

	/**
	 * Returns the eigenvectors command as a String for the matrix to use in Wolfram
	 * @return The eigenvectors command for Wolfram
	 */
	public String toWolfram_eigenvectors() {
		return "Eigenvectors["+this.toWolfram()+"]";
	}

	/**
	 * Returns the eigenvalues command as a String for the matrix to use in Wolfram
	 * @return The eigenvalues command for Wolfram
	 */
	public String toWolfram_eigenvalues() {
		return "Eigenvalues["+this.toWolfram()+"]";
	}

	/**
	 * Returns the eigensystem command as a String for the matrix to use in Wolfram
	 * @return The eigensystem command for Wolfram
	 */
	public String toWolfram_eigensystem() {
		return "Eigensystem["+this.toWolfram()+"]";
	}

	/**
	 * Prints the characteristics equations for each eigenvalue in a specific dialect
	 * @param format The dialect used as MATRIXCOMPLEX, MAXIMA, OCTAVE, MATLAB or WOLFRAM
	 * @param display Adds the expand() to Maxima or the disp() to Octave or Matlab
	 */
	public void printCharactEq(MatrixComplex.outputFormat format, boolean display) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Syseq system = new Syseq();

		MatrixComplex I = new MatrixComplex(rowLen, colLen); I.initMatrixDiag(1,0);
		MatrixComplex cMatrix, dMatrix;
		Complex eigenval;
		
		for (int rowEig = 0; rowEig < eigenvalues.rows(); ++rowEig) {
			eigenval = eigenvalues.getItem(rowEig, 0);
			cMatrix = (I.times(eigenval)).minus(this);
			dMatrix = cMatrix.augment().heap();
			system.complexMatrix = dMatrix.complexMatrix;
			System.out.print("Charact.Eq.["+rowEig+"]: " ); system.printSystemEqSolve(format, display);
		}
	}
	
	/**
	 * Prints the characteristics equations associated to an specific eigenvalue in a specific dialect
	 * @param idx The idx of the eigenvalue s array
	 * @param format
	 * @param display
	 */
	public void printCharactEq(int idx, MatrixComplex.outputFormat format, boolean display) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Syseq system = new Syseq();

		if (idx > eigenvalues.rows()-1) {
			System.out.print("No characteristics equations for the idx:" + idx);
			return;
		}
		MatrixComplex I = new MatrixComplex(rowLen, colLen); I.initMatrixDiag(1,0);
		MatrixComplex cMatrix, dMatrix;
		Complex eigenval;
		
		eigenval = eigenvalues.getItem(idx, 0);
		cMatrix = (I.times(eigenval)).minus(this);
		System.out.println("NullSpace[" + cMatrix.toWolfram()+"]");
		dMatrix = cMatrix.augment().heap();
		system.complexMatrix = dMatrix.complexMatrix;
		System.out.print("Charact.Eq.["+eigenval+"]: " ); system.printSystemEqSolve(format, display);
	}
	

	/**
	 * Prints the characteristics equations for each eigenvalue in MATRIXCOMPLEX dialect
	 */
	public void printCharactEq() {
		printCharactEq(outputFormat.MATRIXCOMPLEX, false);
	}

	/*
	 * ***********************************************
	 * CHECKERS
	 * ***********************************************
	 */
	
	/**
	 * Checks the eigenvectors calculated to prove that (AX-λI) = 0
	 */
	public void checkEigenvectors() {
		int boxSize = 65;
    	Complex.printBoxTextRandom(boxSize, "Check eigenvectors");
    	int colLen = cols(); 
    	MatrixComplex eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < roots().rows(); ++eigv) {
    		eigenVect.complexMatrix[0] = solutions().complexMatrix[eigv].clone();
    		if (eigenVect.isNull()) continue;

			/* ***************************************** */
			if (normalizeVectors)
				eigenVect = eigenVect.normalizeByRows();
			/* ***************************************** */
			/* */

			Complex eigenVal = roots().complexMatrix[eigv][0];
	    	eigenVect.println ("**************** eigenVect:");
	    	System.out.println("                 eigenVal : "+ eigenVal.toString());
	    	this.times(eigenVect.transpose()).transpose().println("aMatrix·eigenVect  "+eigv);
	    	eigenVect.times(eigenVal).println("eigval["+eigv+"]·eigenVect"+eigv);
    	}
	}
	
	public void check() {
		MatrixComplex aMatrix = new MatrixComplex();
		aMatrix.complexMatrix = this.complexMatrix.clone();

     	int boxSize = 65;
		int boxId = 5;

    	System.out.println();
       	Complex.printBoxTitle(boxId, boxSize, "EIGENVALUES & EIGENVECTORS CHECK");

		Complex.showPrecision();
		Eigenspace.version();

		Complex.printBoxText(boxId, boxSize, "Matrix definition section");
		aMatrix.println("aMatrix");
       	System.out.println("MatrixComplex :"+aMatrix.toMatrixComplex());
       	/* EigenVectors Expressions in other languages */
    	Complex.printBoxText(boxId, boxSize, "EigenVectors Expressions in other languages");
    	System.out.println("Maxima:"+this.toMaxima_eigenvalues(true));
    	System.out.println("Maxima:"+this.toMaxima_eigenvectors(true));
    	System.out.println("Maxima:"+this.toMaxima_charpoly(true));
    	System.out.println("Octave:"+this.toOctave_eigenvectors());
    	System.out.println("Wolfram:"+this.toWolfram_eigenvectors());
    	System.out.println("Wolfram:"+this.toWolfram_eigensystem());

    	Complex.printBoxText(boxId, boxSize, "Determinant, Triangle & Characteristic polynomial");
    	aMatrix.determinant().println("Determinant:");
    	aMatrix.triangle().heap().println("triangle:");
    	this.getCharactPoly().println("Characteristic polynom:");

    	/* EigenValues Calculated & Multiplicities */
    	Complex.printBoxText(boxId, boxSize, "EigenValues Calculated  & Multiplicities");
    	{
	    	for (int i = 0; i < this.eigenvalues().rows(); ++i) {
	    		Complex eVal = this.eigenvalues().getItem(i,0);
				System.out.println("EigenValue: " + eVal.toString() + 
						" - arith mult:" + this.arithmeticMultiplicity(eVal) + 
						" - geom mult:" + this.geometricMultiplicity(eVal));    		
	    	}
    	}

    	/* Characteristics Equations */
    	Complex.printBoxText(boxId, boxSize, "Characteristics Equations");
       	Complex.printBoxText(boxId, boxSize, "MATRIXCOMPLEX");
    	this.printCharactEq(outputFormat.MATRIXCOMPLEX, true);
       	Complex.printBoxText(boxId, boxSize, "MAXIMA");
    	this.printCharactEq(outputFormat.MAXIMA, true);
    	Complex.printBoxText(boxId, boxSize, "OCTAVE");
    	this.printCharactEq(outputFormat.OCTAVE, true);
    	Complex.printBoxText(boxId, boxSize, "WOLFRAM");
    	this.printCharactEq(outputFormat.WOLFRAM, true);
    	
    	/* roots & solutions */
    	Complex.printBoxText(boxId, boxSize, "Roots & Solutions ");
    	for (int row = 0; row < this.solutions().rows(); ++row){
    		System.out.println("root: " + this.root(row).getItem(0,0).toString() + " - solution: " + this.solution(row).getRow(0) + " - Is eigenvector: " + (this.solution(row).isNull() ? "No" : "Yes"));
    	}
    	
    	/* EigenVectors Calculated */
    	Complex.printBoxText(boxId, boxSize, "EigenVectors Calculated" + (Eigenspace.getNormalice() ? " Normalized" : ""));
    	this.eigenvectors().println("EigenVectors:");
    	if(!this.eigenvectors().isEmpty() && this.eigenvectors().isSquare()) System.out.println("EigenVectors - Determinant:"+this.eigenvectors().determinant());
    	else System.out.println("EigenVectors - Determinant: dosen't exist.");
   	
    	/* Check Eigenvectors */
    	this.checkEigenvectors();

		Complex.printBoxText(boxId, boxSize, "EigenVectors Base, Orthogonality & Orthonormality");
		System.out.println("Eigenvectors are a Base:" +VectorComplex.checkBase(this.solutions));
		System.out.println("Eigenvectors are a Base orthogonal:" +VectorComplex.isOrthogonal(this.solutions));
		System.out.println("Eigenvectors are a Base orthonormal:" +VectorComplex.isOrthonormal(this.solutions));
		
    	/* Diagonalize Section if possible */
     	Diagfactor diagonal = new Diagfactor(aMatrix);
    	if (diagonal.factorized()) {
        	Complex.printBoxText(boxId, boxSize, "IS DIAGONALIZABLE");
         	diagonal.D().println("Matriz Diagonal (D):");
    	    diagonal.P().transpose().println("Matriz Valores Propios Traspuesta (Pŧ):");
    	    diagonal.P().times(diagonal.D()).times(diagonal.P().inverse()).println("P·D·P⁻¹:");
    	}
    	else {
        	Complex.printBoxText(boxId, boxSize, "IS NOT!!!!!!!!!!!!!! DIAGONALIZABLE");
    	}
    	
     	/*
    	System.out.print("press any key");
    	try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
       	Complex.printBoxText(boxId, boxSize, "-- END EIGENVALUES & EIGENVECTORS TEST END --");
    	System.out.println();
	}

}
