/*
 * Descomposición en valores singulares

    Aplicable a: una matriz A m-por-n.

    Factorización: A = U Σ V T, donde Σ es una matriz diagonal mxn, y U y V son matrices ortogonales mxm y nxn 
    respectivamente, siendo V T la traspuesta de V. Los elementos de la diagonal de Σ son los valores singulares de A 
    y son mayores o iguales a cero.

    Notas: a la matriz V Σ + U T , donde Σ + es igual a la matriz Σ reemplazando los valores singulares por sus recíprocos, 
    se le llama pseudoinversa de A.


 */

package com.ipserc.arith.factorization;

import com.ipserc.arith.matrixcomplex.*;

/**
 * 
 * @author ipserc
 *
 */
public class SVDfactor extends MatrixComplex {
	private MatrixComplex cS;
	private MatrixComplex cV;
	private MatrixComplex cU;

	private final static String HEADINFO = "SVDfactor --- INFO: ";
	private final static String VERSION = "1.1 (2022_0123_0100)";
	/* VERSION Release Note
	 * 
	 * 1.1 (2022_0123_0100)
	 * toWolfram_svd() --> svd[...]
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
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix the string with the rows and columns.
	 */
	public SVDfactor(String strMatrix) {
		super(strMatrix);
		factorize();
	}

	/**
	 * Instantiates a complex array from a MatrixComplex.
	 * @param matrix the MatrixComplex.
	 */
	public SVDfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
		factorize();
	}

	/*
	 * ***********************************************
	 * 	PRIVATE METHODS 
	 * ***********************************************
	 */

	/**
	 * Private method. If the matrix to decompose has less rows the columns this method returns the adjoint of the matrix to do the factorization over it.
	 * @return The adjoint matrix to decompose with the SVD factorization.
	 */
	private MatrixComplex orientMatrix() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex oMatrix = this.copy();
		if (rowLen <= colLen) oMatrix = oMatrix.adjoint();
		return oMatrix;
	}

	/*
	 * ***********************************************
	 * 	METHODS 
	 * ***********************************************
	 */
	
	/**
	 * Factorizes matrices mxn where m is greater or equal than n.
	 * If in the array m is lesser n, it uses the orientMatrix method to return the adjoint matrix and operate on it.
	 * At the end undoes the change to get the results for the original matrix.
	 * In linear algebra, the singular value decomposition (SVD) is a factorization of a real or complex matrix. 
	 * It is the generalization of the eigendecomposition of a positive semidefinite normal matrix (for example, a symmetric matrix with positive eigenvalues) to any m × n {\displaystyle m\times n} m\times n matrix via an extension of the polar decomposition. 
	 * It has many useful applications in signal processing and statistics.
	 * The result of the factorization are the U, S and V arrays, where M = U·S·V*
	 * U is an m × m unitary matrix (if K = R {\displaystyle \mathbb {R} } \mathbb {R} , unitary matrices are orthogonal matrices),
	 * Σ is a diagonal m × n matrix with non-negative real numbers on the diagonal,
	 * V is an n × n unitary matrix over K, and
	 * V* is the conjugate transpose of V.
	 * [Source Wikipedia]
	 */
	private void factorize() {
		MatrixComplex cSS;
		MatrixComplex cVV;
		MatrixComplex cUU;

		// Solution for rowLen > colLen
		// Otherwise works with the adjoint matrix
		MatrixComplex aMatrix = this.orientMatrix();
		// aMatrix.println("--- aMatrix 'oriented'");

		int rowLen = aMatrix.rows();
		int colLen = aMatrix.cols();

		MatrixComplex bMatrix = aMatrix.adjoint().times(aMatrix);
		// System.out.println(bMatrix);
		Eigenspace eigenSpace = new Eigenspace(bMatrix);
		// We need the eigenvalues ordered (DOWN) from higher to lower
		if (eigenSpace.order() == Eigenspace.Order.UP) eigenSpace.orderSwap();
		// bMatrix.println("--- bMatrix");
		// eigenSpace.values().println("--- eigenValues");
		// eigenSpace.vectors().println("--- eigenVectors");

		// Matrix Σ calculation section
		//
		// Σ: diagonal eigenvalues square root matrix, augmented with zeroes
		//
		cSS = new MatrixComplex(rowLen, colLen);
		for (int i = 0; i < colLen; ++i) {
			cSS.complexMatrix[i][i] = eigenSpace.values().getItem(i,0).power(0.5);
			//cSS.complexMatrix[i][i] = eigenValues.complexMatrix[i][0].power(0.5);
		}
		// cSS.println("--- cSS");

		//Matrix V calculation section
		//
		// V: eigenvectors matrix, augmented with some other linealy independent vectors to form a basis of F mxm
		//
		cVV = new MatrixComplex(colLen); cVV.initMatrixDiag(1,0);
		//MatrixComplex eigenVectorsNorm = eigenVectors.normalize(); 
		for (int row = 0; row < colLen; ++row) {
			for (int col = 0; col < colLen; ++col) {
				cVV.complexMatrix[row][col] = eigenSpace.vectors().getItem(col,row).copy();
				//cVV.complexMatrix[row][col] = eigenVectors.complexMatrix[col][row].copy();
			}
		}
		// cVV.println("--- cVV");

		// Matrix U calculation section
		//
		// U: solutions to the equations systems A·V = U·Σ, for each A·V vector (interms)
		// Ui = A·Vi / Σi
		//
		cUU = new MatrixComplex(rowLen); cUU.initMatrixDiag(1,0);
		MatrixComplex cVVt = cVV.transpose();
		MatrixComplex vRow = new MatrixComplex(rowLen, 1);
		MatrixComplex vCol = new MatrixComplex(1, colLen);
		for (int col = 0; col < colLen; ++col) {
			vCol.complexMatrix[0] = cVVt.complexMatrix[col].clone();
			// vCol.println("--- vCol");
			vRow = aMatrix.times(vCol.transpose()).divides(cSS.getItem(col, col));
			// vRow = aMatrix.times(vCol.transpose()).divides(cSS.complexMatrix[col][col]);
			// vRow.println("--- vRow");
			cUU.copyCol(col, vRow, 0);
		}
		// cUU.println("--- cUU");

		cVV = cVV.gramSchmidtM().normalize();
		cUU = cUU.gramSchmidtM().normalize();
		// cVV.println("--- cVV Orthonormal");
		// cUU.println("--- cUU Orthonormal");

		// Solution for rowLen > colLen
		// Otherwise undo the re-orientation 
		if (this.rows() > this.cols()) {
			this.cU = cUU;
			this.cV = cVV;
			this.cS = cSS;
		}
		else {
			this.cU = cVV;
			this.cV = cUU;
			this.cS = cSS.transpose();
		}
	}

	/*
	 * ***********************************************
	 * 	GETTERS
	 * ***********************************************
	 */

	/**
	 * Gets the class member variable with the V array.
	 * @return The V array of the SVD decomposition.
	 */
	public MatrixComplex getV() {
		return cV;
	}

	/**
	 * Gets the class member variable with the S (Sigma Σ) array.
	 * @return The S array of the SVD decomposition.
	 */
	public MatrixComplex getS() {
		return cS;
	}

	/**
	 * Gets the class member variable with the U array.
	 * @return The U array of the SVD decomposition.
	 */
	public MatrixComplex getU() {
		return cU;
	}

	/*
	 * ***********************************************
	 * 	PRINTING
	 * ***********************************************
	 */

	/**
	 * Returns the expression for SVD Factorization for Maxima. expand is available. 
	 * @param expand True if you want to expand the expressions
	 * @return The SVD Factorization expression
	 */
	public String toMaxima_dgesvd(boolean expand) {
		String toMaxima;
		toMaxima = "[sigma, U, VT] : dgesvd (" +this.toMaxima()+", true, true)";
		toMaxima = expand ? "expand("+toMaxima+");" : ";";
		return toMaxima;
	}
	
	/**
	 * Returns the expression for generating Sigma from sigma returned from toMaxima_dgesvd.
	 * @return Sigma:genmatrix expression
	 */
	public String toMaxima_Sigma() {
		return "Sigma:genmatrix(lambda ([i, j], if i=j then sigma[i] else 0),length(U), length(VT));";
	}
	
	/**
	 * Returns the expression for SVD Factorization for GNU Octave. 
	 * @return The SVD Factorization expression
	 */
	public String toOctave_svd() {
		String toOctave;
		toOctave = "[U, S, V] = svd("+this.toOctave()+")";
		return toOctave;
	}

	/**
	 * Returns the expression for SVD Factorization for Matlab.
	 * @return The SVD Factorization expression
	 */
	public String toMatlab_svd() {
		String toMatlab;
		toMatlab = "[U, S, V] = svd("+this.toMatlab()+")";
		return toMatlab;
	}

	/**
	 * Returns the expression for SVD Factorization for Wolfram.
	 * @return The SVD Factorization expression
	 */
	public String toWolfram_svd() {
		String toWolfram;
		toWolfram = "svd["+this.toWolfram()+"]";
		return toWolfram;
	}
}


