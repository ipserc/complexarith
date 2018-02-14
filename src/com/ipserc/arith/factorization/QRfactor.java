/*
 * Factorización QR o triangularización ortogonal

    Aplicable a: una matriz A m por n.

    Factorización: A = Q R donde Q es una matriz ortogonal m por m, y R es una matriz triangular superior m por n.

    Métodos de cálculo: La factorización QR puede calcularse mediante el proceso de ortogonalización de Gram-Schmidt 
    aplicado a las columnas de A, mediante el uso de transformaciones de Householder y mediante transformaciones de Givens.

    Notas: La factorización QR puede utilizarse para "resolver" el sistema de ecuaciones lineales Ax = b cuando el 
    número de ecuaciones es distinto al de incógnitas.

 */

package com.ipserc.arith.factorization;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;

public class QRfactor extends MatrixComplex {
	private MatrixComplex cR;
	private MatrixComplex cQ;
	private boolean factorized = false;

	/*
	 * 	CONSTRUCTORS 
	 */
	/**
	 * Instantiates a complex square array of length len.
	 * @param len the length of the square array.
	 */
	public QRfactor(int len) {
		super(len);
	}

	/**
	 * Instantiates a complex array of dimensions rowLen x colLen
	 * @param rowLen The number of rows.
	 * @param colLen The number of columns.
	 */
	public QRfactor(int rowLen, int colLen) {
		super(rowLen, colLen);
	}

	/**
	 * Instantiates a complex array from a string, rows are separated with ";", columns are separated with ","
	 * @param strMatrix The string with the rows and cols
	 */
	public QRfactor(String strMatrix) {
		super(strMatrix);
	}

	/**
	 * Instantiates a QRfactor array from a MatrixComplex
	 * @param matrix the MatrixComplex already instantiated.
	 */
	public QRfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
	}


	/**
	 * Private Method. Aproximation of the sign function for complex numbers to use in the Housholder decomposition.
	 * @param complexMatrix Complex number to evalute the sign.
	 * @return The sign evaluated.
	 */
	private Complex signHH(Complex complexMatrix) {
		Complex signHH = new Complex();
		signHH = Complex.signP(complexMatrix);
		signHH.setComplexPol(1, signHH.pha());
		return signHH;
	}

	/**
	 * Private Method. Changes the sign of the last row if the number of iteration is odd.
	 * This method is used to force the sign of the determinant of Q to be positive.
	 * @param k The number of iterations.
	 */
	private void QcheckSign(int k) {
		int rowLen = cQ.complexMatrix.length;
		int colLen = cQ.complexMatrix[0].length;
		//if ( k%2 != 0 ) {
		if ((k & 1) != 0) {	
			for (int i = 0; i < colLen; ++i)
				cQ.complexMatrix[rowLen-1][i] = cQ.complexMatrix[rowLen-1][i].opposite(); 
		}
	}

	/**
	 * QR decomposition using the Housholder transformation. Factorices the array using the QR decomposition.
	 * QR decomposition (also called a QR factorization) of a matrix is a decomposition of a matrix A into a product A = QR of an orthogonal matrix Q and an upper triangular matrix R. 
	 * QR decomposition is often used to solve the linear least squares problem, and is the basis for a particular eigenvalue algorithm, the QR algorithm.
	 * QR algorithm is an eigenvalue algorithm: that is, a procedure to calculate the eigenvalues and eigenvectors of a matrix. 
	 * The QR transformation was developed in the late 1950s by John G. F. Francis and by Vera N. Kublanovskaya, working independently.
	 * The basic idea is to perform a QR decomposition, writing the matrix as a product of an orthogonal matrix and an upper triangular matrix, multiply the factors in the reverse order, and iterate.
	 * [Source Wikipedia]
	 */
	public void qrHouseholder() {
		int rowLen = this.complexMatrix.length;
		int colLen = this.complexMatrix[0].length;
		MatrixComplex[] q = new MatrixComplex[rowLen];
		MatrixComplex z = this.copy();
		int k;

		factorized = false;

		MatrixComplex I = new MatrixComplex(rowLen, rowLen);
		I.initMatrixDiag(1, 0);

		for (k = 0; k < colLen && k < rowLen - 1; ++k) {
			MatrixComplex e = new MatrixComplex(rowLen,1);
			MatrixComplex x = new MatrixComplex(rowLen,1);
			MatrixComplex z1 = new MatrixComplex(rowLen, colLen);
			for (int i = 0; i < k; ++i) z1.complexMatrix[i][i].setComplexPol(1, 0);
			for (int i = k; i < rowLen; ++i)
				for (int j = k; j < colLen; ++j)
					z1.complexMatrix[i][j] = z.complexMatrix[i][j];
			z = z1;
			for (int i = 0; i < z.complexMatrix.length; ++i) x.complexMatrix[i][0] = z.complexMatrix[i][k];
			//sign(x1)||x||exp(iθ)
			Complex xNorm = (signHH(x.complexMatrix[k][0])).times(x.norm());
			for (int i = 0; i < rowLen; ++i) e.complexMatrix[i][0].setComplexPol((i == k)?1:0, 0);
			//v = sign(x[k])||x||exp(iθ)·e[k] - x
			e = (e.times(xNorm)).minus(x);
			double eNorm = e.norm();
			//u = v/||v||
			e = e.divides(eNorm);
			q[k] = new MatrixComplex(rowLen, rowLen);
			// q[k] = I-2·u·u* (conjugate transpose)
			q[k] = I.minus(e.times(e.adjoint()).times(2));
			//q[k] = I.minus(e.dotprod(e).times(2));
			z = q[k].times(z);
		}
		cQ = q[0];	
		for (int i = 1; i < k; ++i) cQ = q[i].times(cQ) ;
		this.QcheckSign(k); //Makes of Q an unitary "special" matrix --> det(Q) = 1
		cR = cQ.times(this);
		cQ = cQ.adjoint();

		factorized = true;
	}

	/**
	 * QR decomposition using the Gram - Schmidt factorization. Factorices the array using the QR decomposition.
	 */
	public void qrGramSchmidt() {
		factorized = false;
		cQ = this.gramSchmidt().normalize();
		cR = cQ.adjoint().times(this);
		factorized = true;
	}

	/**
	 * QR decomposition using the Gram - Schmidt Full factorization. Factorices the array using the QR decomposition.
	 */
	public void qrGramSchmidtFull() {
		factorized = false;
		cQ = this.gramSchmidtFull().normalize();
		cR = cQ.adjoint().times(this);
		//this.cleanCR();
		factorized = true;
	}

	/**
	 * QR decomposition using the Gram - Schmidt Modified factorization. Factorices the array using the QR decomposition.
	 */
	public void qrGramSchmidtM() {
		factorized = false;
		cQ = this.gramSchmidtM().normalize();
		cR = cQ.adjoint().times(this);
		factorized = true;
	}

	/**
	 * QR decomposition using the Gram - Schmidt Modified Full factorization. Factorices the array using the QR decomposition.
	 */
	public void qrGramSchmidtMFull() {
		factorized = false;
		cQ = this.gramSchmidtMFull().normalize();
		cR = cQ.adjoint().times(this);
		//this.cleanCR();
		factorized = true;
	}

	/**
	 * Private Method to clean the class member variable cR which has the R matrix form the QR decomposition.
	 */
	private void cleanCR() {
		int rowLen = this.cR.complexMatrix.length;
		int colLen = this.cR.complexMatrix[0].length;
		//Complex zero = new Complex(0,0);

		if (rowLen <= colLen) return;
		//for (int row = colLen, col = colLen-1; row < rowLen; ++row) this.cR.complexMatrix[row][col] = zero;
		for (int row = colLen, col = colLen-1; row < rowLen; ++row) this.cR.complexMatrix[row][col].setComplexRec(0,0);
	}

	/**
	 * Gets the class member variable with the Q array.
	 * @return The Q array result of the QR decomposition.
	 */
	public MatrixComplex Q() {
		return cQ;
	}

	/**
	 * Gets the class member variable with the R array.
	 * @return The R array result of the QR decomposition.
	 */
	public MatrixComplex R() {
		return cR;
	}

	/**
	 * Gets the class member variable with the status of the factorization.
	 * @return The factorization status.
	 */
	public boolean factorized() {
		return factorized;
	}

}
