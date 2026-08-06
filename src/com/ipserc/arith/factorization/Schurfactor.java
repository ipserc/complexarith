package com.ipserc.arith.factorization;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.vectorcomplex.VectorComplex;

public class Schurfactor extends MatrixComplex {
	
	private final static String HEADINFO = "Schurfactor --- INFO: ";
	private final static String VERSION = "1.0 (2025_0324_1930)";
	private final static int boxSize = 65;

	private boolean factorized = false;
	
	private MatrixComplex cU;
	private MatrixComplex cSchur;

	/* VERSION Release Note
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
	 * Constructor de la clase Schufactor a partir de una matriz compleja expresada como un string 
	 * @param strMatrix La matriz compleja en formato string
	 */
	public Schurfactor(String strMatrix) {
		super(strMatrix);
		factorize();
	}
	
	/**
	 * Constructor de la clase Schufactor a partir de una matriz compleja expresada cómo MatixCompplex 
	 * @param strMatrix La matriz compleja en formato MatrixComplex
	 */
	public Schurfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
		factorize();
	}

	/*
	 * __DEBUG__
	 */
	
	private static boolean __DEBUG__ = false;
	
	/**
	 * Activa el modo DEBUG
	 */
	public static void debugON() {
		__DEBUG__ = true;
	}

	/**
	 * Desactiva el modo DEBUG
	 */
	public static void debugOFF() {
		__DEBUG__ = false;
	}

	/**
	 * devuelve el estado del modo DEBUG
	 */
	public static boolean debug() {
		return __DEBUG__;
	}

	/*
	 * Methods
	 */
	/**
	 * Realiza la factorización de la matriz. Si lo consigue pone el flag factorized a true. 
	 * En caso contrario muestra un mesaje por pantalla indicado que no ha podido realizar la factorización.
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
	 * Método privado que resuelve de forma recursiva la factorización de Schur.
	 * Las matrices definitivas se recuperan con getSchur y getU.
	 * @param aMat La matriz a factorizar en cada iteraciñon
	 * @param iteracion La iteración que va a llevar a acbo
	 * @param rows El número de fila de la matriz original
	 * @return La matriz obtenida para cada iteración
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
