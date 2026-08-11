/**
 * A square matrix "A" is said to be diagonalizable if it is similar to a diagonal matrix.
 * That is, if it can be reduced to a diagonal form through a change of basis.
 * In that case, the matrix can be decomposed as A=P·D·P⁻¹, where "P" is an invertible matrix whose
 * column vectors are eigenvectors of A, and D is a diagonal matrix formed by the eigenvalues of A.
 * <p>
 * If A is orthogonally similar to a diagonal matrix, i.e. if P is orthogonal, then A is said to be
 * orthogonally diagonalizable, and can be written as A=PDP^t. The spectral theorem guarantees that
 * any real symmetric square matrix is orthogonally diagonalizable. In that case P is formed by an
 * orthonormal basis of eigenvectors of the matrix, and the eigenvalues are real. P is therefore
 * orthogonal, and the row vectors of P⁻¹ are the column vectors of P.
 * <p>
 * Let A ∈ M(n×n) over a field K be a square matrix. A is said to be diagonalizable if, and only if,
 * A can be decomposed as:
 * 	A=P·D·P⁻¹
 * where:
 *	D is a diagonal matrix whose main diagonal is formed by the elements of σ(A), each one
 *  appearing as many times as its algebraic multiplicity indicates, σ(A) being the spectrum of A,
 *  i.e. the set of eigenvalues of A:
 *  	σ(A) = {λi ∈ K | A·v = λi·v ∀ i = 1, 2,..., n}
 *  P is the matrix whose columns are the vectors that form a basis of the eigenspace associated
 *  with each λi, following the order established in D, i.e. the vectors that form the kernel of
 *  the matrix (A − λi·I):
 *  	P = (v1|v2|...|vn), vj ∈ ker(A − λi·I) ∀ i, j = 1,..., n
 */
package com.ipserc.arith.factorization;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * @author ipserc
 *
 */
public class Diagfactor extends MatrixComplex {
	private MatrixComplex cD;
	private MatrixComplex cP;
	private boolean factorized = false;

	private final static String HEADINFO = "Diagfactor --- INFO: ";
	private final static String VERSION = "1.6 (2026_0811_1800)";
	/* VERSION Release Note
	 * 1.6 (2026_0811_1800)
	 * Comentarios Javadoc traducidos al inglés y corregidos (sin cambios funcionales), como parte de
	 * la generación de la documentación de la API. De paso, corregida una descripción incorrecta en
	 * el Javadoc de diagonalize(): decía que D contenía los autovectores y P los autovalores, al
	 * revés de lo que hace realmente el código (D=autovalores, P=autovectores).
	 *
	 * 1.5 (2026_0807_2359)
	 * Diagfactor(MatrixComplex): mismo bug de aliasing por clone() superficial encontrado en
	 * Schurfactor/LUfactor/QRfactor esta sesion -- "matrix.complexMatrix.clone()" comparte las
	 * filas del array con la matriz original del llamador. Arreglado con
	 * matrix.copy().complexMatrix, a peticion del usuario tras revisar el resto de la
	 * factorization/ y encontrar el mismo patron sin arreglar aqui.
	 *
	 * 1.4 (2026_0805_1900)
	 * isDiagonalizable__(Eigenspace) borrado -- codigo muerto confirmado (cero llamadores en todo
	 * el proyecto, ya marcado en su propio Javadoc como "DEPRECATED. USELESS"). Contenia la unica
	 * comparacion de agrupamiento por redondeo por componente que quedaba fuera de Eigenspace.java
	 * tras el fix de VERSION 1.10 de esa clase (agrupamiento por distancia) -- el camino REAL que
	 * se ejecuta de verdad, diagonalize()->eigenspace.isDiagonaizable(), ya usaba (y sigue usando)
	 * arithmeticMultiplicity(Complex), ya corregido alli. No quedaba nada que mejorar en el metodo
	 * muerto, solo borrarlo.
	 *
	 * 1.3 (2026_0731_1930)
	 * diagonalize(): System.exit(-1) sobre matriz no cuadrada sustituido por
	 * IllegalArgumentException (auditoria matematica de MatrixComplex, paso 3) --
	 * era el System.exit que impedia que sin()/cos()/tan()/sinh()/cosh()/tanh()
	 * de MatrixComplex se recuperaran de una entrada no cuadrada.
	 *
	 * 1.2 (2024_0418_2000)
	 * public boolean isSimilar() {
	 * public boolean isSimilar()
	 * 
	 * 1.1 (2021_0123_0100)
	 * toWolfram_diagonalize() --> Diagonalize[....] 
	 * 
	 * 1.0 (2020_0824_1800)
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
	 * Instantiates a complex array of dimension.
	 * @param dimension The dimension of the square matrix.
	 */
	public Diagfactor(int dimension) {
		super(dimension);
	}

	/**
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix the string with the rows and columns.
	 */
	public Diagfactor(String strMatrix) {
		super(strMatrix);
		diagonalize();
	}

	/**
	 * Instantiates a Diagfactor array from a MatrixComplex.
	 * @param matrix the MatrixComplex already instantiated.
	 */
	public Diagfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.copy().complexMatrix;
		diagonalize();
	}

	/*
	 * ***********************************************
	 * GETTERS
	 * ***********************************************
	 */
	
	/**
	 * Gets the diagonal matrix
	 * @return D
	 */
	public MatrixComplex D() {
		return cD;
	}

	/**
	 * Gets the eigenvector matrix
	 * @return P
	 */
	public MatrixComplex P() {
		return cP;
	}
	
	/**
	 * Gets the class member variable with the status of the factorization.
	 * @return The factorization status.
	 */
	public boolean factorized() {
		return factorized;
	}

	/**
	 * Gets the class member variable with the status of the factorization.
	 * @return Whether the matrix is diagonalized or not.
	 */
	public boolean isDiagonalizable() {
		return factorized;
	}
	
	/**
	 * Returns true if the matrix has been diagonalized successfully
	 * @return True if it has been diagonalized successfully
	 */
	public boolean isSimilar() {
		return factorized;
	}

	/**
	 * Returns true if the matrix has been diagonalized successfully
	 * @return True if it has been diagonalized successfully
	 */
	public boolean isEquivalent( ) {
		return factorized;		
	}
	
	/*
	 * ***********************************************
	 * 	CHECKERS 
	 * ***********************************************
	 */

	/**
	 * Indicates whether the matrix is diagonalizable.
	 * If for every eigenvalue of A, the geometric multiplicity equals the algebraic multiplicity, then A is said to be diagonalizable.
	 * @param eigenspace The Eigenspace object
	 * @return true if the matrix is diagonalizable, otherwise false
	 */
	public boolean isDiagonalizable(Eigenspace eigenspace) {
		return eigenspace.isDiagonaizable();
	}
	
	/*
	 * ***********************************************
	 * 	OPERATION 
	 * ***********************************************
	 */

	/**
	 * Factorizes the matrix into a diagonal matrix of eigenvalues (D) and an eigenvector matrix (P).
	 * The factorization gives A=P·D·P⁻¹
	 */
	public void diagonalize() {
		final boolean DEBUG_ON = false; 
		final String METH_NAME = "diagonalize";
		int rowLen = this.complexMatrix.length; 
		int colLen= this.complexMatrix[0].length;
		if (colLen != rowLen) {
			throw new IllegalArgumentException(HEADINFO + "The Matrix MUST be square to be factorized as a Diagonal Matrix");
		}
		Eigenspace eigenspace = new Eigenspace(this);
	
		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			eigenspace.println(HEADINFO + METH_NAME + ":eigenspace");
			eigenspace.solutions().println(HEADINFO + METH_NAME + ":eigenspace vectors");
			eigenspace.eigenvalues().println(HEADINFO + METH_NAME + ":eigenspace values");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */
		//MatrixComplex eigenVal = this.eigenvalues();
		//eigenVal.quicksort(0);
		
		//if (!this.isDiagonalizable(eigenspace)) {
		if (!eigenspace.isDiagonaizable()) {
			factorized = false;

			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				System.out.println(HEADINFO + "The Matrix cannot be diagonalized");
			}
			/* ------------- END DEBUGGING BLOCK ------------- */

			return;
		}
		else 
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				System.out.println(HEADINFO + "The Matrix CAN BE diagonalized");
			}
			/* ------------- END DEBUGGING BLOCK ------------- */

		// P: Transformation Matrix, the eigenvalues in columns
		cP = eigenspace.solutions().transpose();

		// D: diagonal eigenvalues square root matrix
		cD = new MatrixComplex(rowLen, colLen);
		for (int i = 0; i < colLen; ++i)
			cD.complexMatrix[i][i] = eigenspace.roots().complexMatrix[i][0];
		//cD.println("--- Diagonal");
		//(cP.times(cD).times(cP.inverse())).println("A=P·D·P⁻¹");
		factorized = true;
	}

	/*
	 * ***********************************************
	 * 	PRINTING
	 * ***********************************************
	 */

	/**
	 * Returns the expression for diagonalize for Maxima.
	 * @return The diagonalize expression
	 */
	// [L, P, Pt] : dgeev (M, true, ture); D : apply (diag_matrix, L); P;P.D.invert(P);
	public String toMaxima_diagonalize() {
		String tomaxima;
		System.out.println("MAXIMA :load (lapack)$");
		tomaxima = "M:"+this.toMaxima()+"; [L, P, Pt]:dgeev(M, true, true); D:apply(diag_matrix, L); expand(P.D.invert(P));";
		return tomaxima;
	}
	
	/**
	 * Returns the expression for diagonalize for Octave.
	 * @return The diagonalize expression
	 */
	public String toOctave_diagonalize() {
		String toOctave;
		toOctave = "[P,D]=eig("+this.toOctave()+")";
		return toOctave;
	}

	/**
	 * Returns the expression for diagonalize for Octave.
	 * @return The diagonalize expression
	 */
	public String toMatlab_diagonalize() {
		String toMatlab;
		toMatlab = "[P,D]=eig("+this.toMatlab()+")";
		return toMatlab;
	}

	/**
	 * Returns the expression for diagonalize for Wolfram.
	 * @return The diagonalize expression
	 */
	public String toWolfram_diagonalize() {
		String toWolfram;
		toWolfram = "Diagonalize["+this.toWolfram()+"]";
		return toWolfram;
	}

}
