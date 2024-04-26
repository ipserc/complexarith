/**
 * Una matriz cuadrada "A" se dice que es diagonalizable si es semejante a una matriz diagonal. 
 * Es decir, si mediante un cambio de base puede reducirse a una forma diagonal. 
 * En este caso, la matriz podrá descomponerse de la forma A=P·D·P⁻¹. En donde "P" es una matriz invertible cuyos 
 * vectores columna son vectores propios de A, y D es una matriz diagonal formada por los valores propios de A.
 * 
 * Si la matriz A es semejante ortogonalmente a una matriz diagonal, es decir, si la matriz P es ortogonal 
 * se dice entonces que la matriz A es diagonalizable ortogonalmente, pudiendo escribirse como A=PDP^t. 
 * El teorema espectral garantiza que cualquier matriz cuadrada simétrica con coeficientes reales es ortogonalmente diagonalizable. 
 * En este caso P está formada por una base ortonormal de vectores propios de la matriz siendo los valores propios reales. 
 * La matriz P es por tanto ortogonal y los vectores filas de P⁻¹ son los vectores columnas de P.
 * 
 * Sea A ∈ M(n×n) de (K) una matriz cuadrada con valores en un cuerpo K, se dice que la matriz A es diagonalizable si, y sólo si, 
 * A se puede descomponer de la forma:
 * 	A=P·D·P⁻¹
 * Donde:
 *	D es una matriz diagonal cuya diagonal principal está formada por los elementos de σ(A), 
 *  apareciendo cada uno tantas veces como indique su multiplicidad algebraica, siendo σ(A) 
 *  el espectro de A, es decir, el conjunto de autovalores de la matriz A:
 *  	σ(A) = {λi ∈ K | A·v = λi·v ∀ i = 1, 2,..., n} 
 *  P es la matriz cuyas columnas son los vectores que constituyen una base del subespacio 
 *  propio asociado a cada λi, siguiendo el orden establecido en D,
 *  esto es, los vectores que forman el núcleo de la matriz (A − λ i I):
 *  	P = (v1|v2|...|vn ), vj ∈ ker(A − λi·I) ∀ i, j = 1,..., n
 */
package com.ipserc.arith.factorization;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;

/**
 * @author ipserc
 *
 */
public class Diagfactor extends MatrixComplex {
	private MatrixComplex cD;
	private MatrixComplex cP;
	private boolean factorized = false;

	private final static String HEADINFO = "Diagfactor --- INFO: ";
	private final static String VERSION = "1.2 (2024_0418_2000)";
	/* VERSION Release Note
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
	 * @param dimension The dimension of the saquare matrix.
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
		this.complexMatrix = matrix.complexMatrix.clone();
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
	 * @return True if the has been diagonalized successfully
	 */
	public boolean isSimilar() {
		return factorized;
	}
	
	/**
	 * Returns true if the matrix has been diagonalized successfully
	 * @return True if the has been diagonalized successfully
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
	 * Indicates if the Matrix would be or not diagonalizable 
	 * If for every eigenvalue of A, the geometric multiplicity equals the algebraic multiplicity, then A is said to be diagonalizable.
	 * @param eigenspace The Eigenspace object
	 * @return true if the matrix is diagonalizable, otherwise false
	 */
	public boolean isDiagonalizable(Eigenspace eigenspace) {
		return eigenspace.isDiagonaizable();
	}
	
	/**
	 * DEPRECATED. USELESS it needs an Eigenspace and then can use Eigenspace isDiagonaizable() method
	 * @param eigenspace
	 * @return
	 */
	public boolean isDiagonalizable__(Eigenspace eigenspace) {
		final boolean DEBUG_ON = false;
		final String METH_NAME = "isDiagonalizable";
		int arithMult, geomMult;
		int sumArithMult = 0;

		Complex eigenValue = eigenspace.eigenvalues().getItem(0, 0);
		arithMult = eigenspace.arithmeticMultiplicity(eigenValue);
		geomMult = eigenspace.geometricMultiplicity(eigenValue);
		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			System.out.printf(HEADINFO + METH_NAME + ": eigenValue = %s\n", eigenValue.toString());			
			System.out.printf(HEADINFO + METH_NAME + ": arithMult = %d\n", arithMult);
			System.out.printf(HEADINFO + METH_NAME + ": geomMult  = %d\n", geomMult);
		}
		/* ------------- END DEBUGGING BLOCK ------------- */
		
		if (arithMult != geomMult) return false;

		sumArithMult += arithMult;		
		for (int row = 1; row < eigenspace.eigenvalues().rows(); ++row) {
			// ***** if (eigenValue.equalsred(eigenspace.values().getItem(row, 0), this.bestNumDecs())) continue;
			if (eigenValue.equals(eigenspace.eigenvalues().getItem(row, 0), this.bestNumDecs())) continue;
			eigenValue = eigenspace.eigenvalues().getItem(row, 0);
			arithMult = eigenspace.arithmeticMultiplicity(eigenValue);
			geomMult = eigenspace.geometricMultiplicity(eigenValue);
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				System.out.printf(HEADINFO + METH_NAME + ": eigenValue = %s\n", eigenValue.toString());			
				System.out.printf(HEADINFO + METH_NAME + ": arithMult = %d\n", arithMult);
				System.out.printf(HEADINFO + METH_NAME + ": geomMult  = %d\n", geomMult);
			}
			/* ------------- END DEBUGGING BLOCK ------------- */
			if (arithMult != geomMult) return false;
			sumArithMult += arithMult;
		}
		if (sumArithMult == this.rows()) return true;
		else return false;
	}

	/*
	 * ***********************************************
	 * 	OPERATION 
	 * ***********************************************
	 */

	/**
	 * Factorizes the matrix using a diagonal matrix of eigenvectors (D) and a eigenvalue matrix (P)
	 * The factorization gives A=P·D·P⁻¹
	 */
	public void diagonalize() {
		final boolean DEBUG_ON = false; 
		final String METH_NAME = "diagonalize";
		int rowLen = this.complexMatrix.length; 
		int colLen= this.complexMatrix[0].length;
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Diagonal Matrix");
			System.exit(-1);
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
