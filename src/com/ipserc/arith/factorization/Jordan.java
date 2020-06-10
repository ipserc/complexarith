package com.ipserc.arith.factorization;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.complex.Complex;;

public class Jordan extends MatrixComplex {
	private MatrixComplex cJ;
	private MatrixComplex cP;
	private boolean factorized = false;
	private final static String HEADINFO = "Jordan --- INFO:";

	/*
	 * 	CONSTRUCTORS 
	 */
	/**
	 * Instantiates a complex square array of length len.
	 * @param len The length of the square array.
	 */
	public Jordan(int len) {
		super(len);
	}

	/**
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix the string with the rows and columns.
	 */
	public Jordan(String strMatrix) {
		super(strMatrix);
	}

	/**
	 * Instantiates a Diagfactor array from a MatrixComplex.
	 * @param matrix the MatrixComplex already instantiated.
	 */
	public Jordan(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
	}

	/*
	 * 	GETTERS 
	 */

	/**
	 * Gets the diagonal matrix
	 * @return J
	 */
	public MatrixComplex J() {
		return cJ;
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
	 * 
	 * @param order
	 * @param arithMult
	 * @param eigenValue
	 * @return
	 */
	private MatrixComplex block(int order, int arithMult, Complex eigenValue) {
		MatrixComplex block = new MatrixComplex(this.rows(), this.cols());
		block.initMatrix(0, 0);
		for (int i = 0, row = order; i < arithMult; ++i, ++row) {
			block.setItem(row, row, eigenValue);
			//if ( i+row > 0 && row+1 < this.cols()) block.setItem(row, row+1, Complex.ONE);
			if ( row+1 < this.cols()) block.setItem(row, row+1, Complex.ONE);
		}
		return block;
	}

	/**
	 * Calculates the eigenvector or characteristic vector using as generating space (M-lI)^a 
	 * where M is the coef matrix, l is the eigenvalue, I is the identity matrix and a i the power to raise the  
	 * @param eigenVal The eigenvalue to calculate the eigenvector
	 * @param arithMult
	 * @return
	 */
	public MatrixComplex eigenvectors(Complex eigenval, int arithMult) {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex I = new MatrixComplex(rowLen, colLen); I.initMatrixDiag(1,0);
		MatrixComplex eigenVectors = new MatrixComplex(rowLen, colLen);
		MatrixComplex eigenVect;

		MatrixComplex cMatrix = (this.minus(I.times(eigenval))).power(arithMult);
		MatrixComplex dMatrix = cMatrix.augment().heap();
		eigenVect = dMatrix.solve();
		return eigenVect;
		/*
		for (int sol = 0; sol < eigenVect.rows(); ++sol)
		//for (int sol = 0; sol < arithMult; ++sol)
			eigenVectors.complexMatrix[sol] = eigenVect.complexMatrix[sol].clone();
		return eigenVectors;
		*/
	}

	/**
	 * Factorizes the matrix using a diagonal matrix of eigenvectors (D) and a eigenvalue matrix (P)
	 * The factorization gives A=P·J·P⁻¹
	 */
	public void factorize() {
		int rowLen = this.rows(); 
		int colLen= this.cols();
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Jordan Matrix");
			System.exit(-1);
		}
		MatrixComplex eigenValArray = this.eigenvalues();
		eigenValArray.quicksort(0);
		
		cJ = new MatrixComplex(rowLen, colLen);
		cP = new MatrixComplex(rowLen, colLen);

		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = eigenValArray.arithmeticMultiplicity(eigenval);
			MatrixComplex jordanBlock = this.block(i, arithMult, eigenval);
			cJ = cJ.plus(jordanBlock);
			i += arithMult;
		}
		cJ.println("----------JORDAN");
		
		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = eigenValArray.arithmeticMultiplicity(eigenval);
			MatrixComplex eigenVect = this.eigenvectors(eigenval, arithMult);
			eigenVect.println("----------EIGENVECTORS for:" + eigenval.toString() + " multiplicity:" + arithMult);
			for (int sol = 0; sol < eigenVect.rows(); ++sol) {
				cP.complexMatrix[sol+i] = eigenVect.complexMatrix[sol].clone();
				
			}
			i += arithMult;
		}
		cP.println("----------PASS MATRIX");
	}
}
