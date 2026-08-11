package com.ipserc.arith.factorization;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.vectorcomplex.VectorComplex;

public class Schurfactor extends MatrixComplex {
	
	private final static String HEADINFO = "Schurfactor --- INFO: ";
	private final static String VERSION = "1.3 (2026_0811_2130)";
	private final static int boxSize = 65;

	private boolean factorized = false;

	private MatrixComplex cU;
	private MatrixComplex cSchur;

	/* VERSION Release Note
	 *
	 * 1.3 (2026_0811_2130)
	 * Corregido error de generacion de Javadoc preexistente: el @param del constructor
	 * Schurfactor(MatrixComplex) nombraba "strMatrix" (copiado del otro constructor) en vez de
	 * "matrix", el nombre real del parametro. Sin cambios funcionales.
	 *
	 * 1.2 (2026_0811_2000)
	 * Comentarios Javadoc traducidos al inglés y corregidos (sin cambios funcionales), como parte
	 * de la generación de la documentación de la API.
	 *
	 * 1.1 (2026_0807_1600)
	 * Schurfactor(MatrixComplex matrix): "this.complexMatrix = matrix.complexMatrix.clone();" was
	 * a shallow Complex[][].clone() (Java array clone -- copies the outer array, but the row
	 * arrays stay the SAME objects as matrix's), not the deep-copy idiom used everywhere else in
	 * the project (MatrixComplex.copy()/clone()). Confirmed with a live repro
	 * (ScratchSchurfactorAliasCheck01.java, ver Claude/ComplexArithRev.md): mutating the
	 * constructed Schurfactor instance (any inherited MatrixComplex mutator, e.g. setItem())
	 * silently corrupted the caller's original matrix. Latent in the only production caller
	 * (MatrixComplexFunctions.logm(), which only reads getU()/getSchur() and never mutates the
	 * Schurfactor instance) but a real landmine for any future caller. Fixed to matrix.copy().
	 *
	 * 1.0 (2025_0324_1930)
	 * public Schurfactor(String strMatrix) {
	 * public Schurfactor(MatrixComplex matrix) {
	 * private static boolean __DEBUG__ = false;
	 * public static void debugON() {
	 * public static void debugOFF() {
	 * public static boolean debug() {
	 * public void factorize() {
	 * private MatrixComplex Schur(MatrixComplex aMat, int iteracion, int rows) {
	 * public MatrixComplex getU() {
	 * public MatrixComplex getU() {
	 * public MatrixComplex getSchur() {
	 * public boolean factorized() {
	 *
	 *
	 */

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
	 * 	CONSTRUCTORS 
	 * ***********************************************
	 */

	/**
	 * Constructs a Schurfactor instance from a complex matrix expressed as a string.
	 * @param strMatrix The complex matrix in string format
	 */
	public Schurfactor(String strMatrix) {
		super(strMatrix);
		factorize();
	}

	/**
	 * Constructs a Schurfactor instance from a complex matrix expressed as a MatrixComplex.
	 * @param matrix The complex matrix as a MatrixComplex
	 */
	public Schurfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.copy().complexMatrix;
		factorize();
	}

	/*
	 * __DEBUG__
	 */
	
	private static boolean __DEBUG__ = false;
	
	/**
	 * Turns DEBUG mode on.
	 */
	public static void debugON() {
		__DEBUG__ = true;
	}

	/**
	 * Turns DEBUG mode off.
	 */
	public static void debugOFF() {
		__DEBUG__ = false;
	}

	/**
	 * Returns the current state of DEBUG mode.
	 */
	public static boolean debug() {
		return __DEBUG__;
	}

	/*
	 * Methods
	 */
	/**
	 * Factorizes the matrix. On success, sets the factorized flag to true; otherwise, prints a
	 * message indicating the factorization could not be performed.
	 */
	public void factorize() {
		factorized = false;
		try {
			cU = this.Schur(this, 0, this.rows());
			cSchur = cU.adjoint().times(this).times(cU);
			factorized = true;
		}
		catch (Exception excp) {
			// Surface the real cause instead of discarding it: factorize() is meant to turn ANY
			// failure into "factorized=false, check factorized() before using getU()/getSchur()"
			// for callers (e.g. logm()) that already handle that gracefully -- but printing only
			// the generic line below with no diagnostic made every failure indistinguishable,
			// whether a genuine known limitation or an unrelated bug. Confirmed real: a spurious
			// "repeated" eigenvalue from Eigenspace.eigenval()'s DISTANCE-based grouping used to
			// surface here as an opaque ArrayIndexOutOfBoundsException with zero context.
			System.out.println(HEADINFO + " The matrix hasn't got any Schur factorization. Cause: "
				+ excp.getClass().getSimpleName() + ": " + excp.getMessage());
		}
	}
	
	/**
	 * Private method that solves the Schur factorization recursively.
	 * The final matrices are retrieved via getSchur and getU.
	 * @param aMat The matrix to be factorized at each iteration
	 * @param iteracion The iteration being carried out
	 * @param rows The number of rows of the original matrix
	 * @return The matrix obtained at each iteration
	 */
	private MatrixComplex Schur(MatrixComplex aMat, int iteracion, int rows) {
		if (__DEBUG__) {
			Complex.printBoxTextRandom(boxSize, "ITERACION " + (iteracion));
			aMat.println("aMat");
		}
		
		// Calulamos los avtovalores y autovectores de la matriz A que usaremos para la factorización
		Eigenspace aMatEigen = new Eigenspace(aMat);

		if (__DEBUG__) {
			aMatEigen.eigenvalues().println("aMatEigen.eigenvalues()");
			aMatEigen.eigenvectors().println("aMatEigen.eigenvectors()");
			aMatEigen.eigenvector(0).println("aMatEigen.eigenvector(0)");
			System.out.println("aMatEigen.eigenvector(0).norm():"+aMatEigen.eigenvector(0).norm());
		}
		
		// Tomamos λ1 autovalor de A y w1 autovector de A con autovalor λ1 -innecesario: y ||w1|| = 1-.
		// Completando {w1} a una base de Kn y aplicando ortonormalización de Gram-Schmidt -Gram-Schmidt + normalización-, 
		// podemos obtener una base ortonormal de Cn -eigenVector.base().orthonormalize()- {w(1) , z(2) , . . . , z(n)}.
		// Construimos la matriz U1 (baseW1ortn) tomando estos vectores como columnas de la matriz -transpose()-.
		VectorComplex eigenVector = new VectorComplex(aMatEigen.eigenvector(0));
		MatrixComplex baseW1ortn = eigenVector.base().orthonormalize().transpose();
		
		if (__DEBUG__) {
			eigenVector.base().println("eigenVector.base()");
			baseW1ortn.println("baseW1ortn");
			for (int i = 0; i < aMat.rows(); ++i) {		
				System.out.println("= = = = baseW1ortn.getRow("+i+").norm():"+baseW1ortn.getRow(0).norm());
			}
		}
		
		// Como la primera columna de A*U1 es λ1*w(1), obtenemos que U1.inv*A*U1 es una matriz que se compone de
		// [λ1|*] cómo primera fila y [0|A1] cómo restantes filas, esta matriz es la Schur para esa iteración
		MatrixComplex schur = baseW1ortn.inverse().times(aMat).times(baseW1ortn);
		
		if (__DEBUG__) {
			schur.println("schur");
			baseW1ortn.times(schur).times(baseW1ortn.inverse()).println("ChecK");
		}

		// Como U1inv*A*U y A son semejantes, tienen el mismo polinomio característico. 
		// Luego A1 ∈ C(n−1)×(n−1) tiene autovalores λ2 , . . . , λn .
		// Tomamos ahora w(2) ∈ Cn−1 autovector normalizado de A1 correspondiente al autovalor λ2 
		// y repetimos el procedimiento. Construimos U2 ∈ C(n−1)×(n−1) unitaria tal que U2.inversa * A1 * U2
		// sea una matriz con [λ2|*] cómo primera fila y [0|A2] cómo siguientes líneas de la matriz
		// Y definimos V2 cómo [1|*] cómo primera fila y [0|U2] cómo siguientes líneas de la matriz
		MatrixComplex V = MatrixComplex.eye(rows);
		for (int i = iteracion; i <  rows; ++i)
			for (int j = iteracion; j <  rows; ++j)
				V.setItem(i, j, baseW1ortn.getItem(i-iteracion, j-iteracion));

		if (aMat.dim() > 1) {
			// Las matrices V2 y U1*V2 son unitarias, y V2.inv*U1.inv*A*U1*V2 tiene la forma
			// [λ1, * | * ]
			// [ 1, λ2| * ]
			// [   0  | A2]
			// Repitiendo este procedimiento, obtenemos matrices unitarias Ui ∈ C(n−i+1)×(n−i+1) , i = 1, . . . , n − 1, 
			// y matrices unitarias Vi ∈ Cn×n, i = 2, . . . , n − 1. 
			// La matriz de paso U se obtiene de U = U1*V2*V3*. . .*Vn-1 y es unitaria
			// y la matriz de Schur se obtiene de U.inv*A*U
			// Esto se hace en factorize()
			// Así qué reiteramos el proceso hasta que la dimensión de schur sea 1, momento a partir del cual devolvemos las V acumulando el producto
			MatrixComplex aMat2 = schur.minor(0,0);
			V = V.times(Schur(aMat2, ++iteracion, rows));
			return V;
		}
		else {
			return V;
		}
	}
	
	/**
	 * Returns the matrix U that satisfies the Schur factorization so that U*Shur*U.inv is equal to the original matrix.
	 * U is an unitary matrix so I.inverse = U.adjoint
	 * @return The U matrix corresponding to the Shur factorization
	 */
	public MatrixComplex getU() {
		if (!factorized) System.out.println(HEADINFO + " The matrix hasn't got any Schur factorization.");
		return cU;
	}
	
	/**
	 * Returns the matrix Shur that satisfies the Schur factorization so that U*Shur*U.inv is equal to the original matrix.
	 * The Shur matrix is a upper triangular matrix
	 * @return The Schur matrix corresponding to the Shur factorization
	 */
	public MatrixComplex getSchur() {
		if (!factorized) System.out.println(HEADINFO + " The matrix hasn't got any Schur factorization.");
		return cSchur;
	}
	

	/**
	 * Returns the value of factorized. factoriced is true if the Schur and U matrices have been found
	 * @return Trued if factorized, false otherwise
	 */
	public boolean factorized() {
		return factorized;
	}

}
