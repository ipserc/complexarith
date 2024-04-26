package com.ipserc.arith.factorization;

import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.complex.Complex;

public class Jordan extends Eigenspace {

	private final static String HEADINFO = "Jordan --- INFO:";
	private final static String VERSION = "1.0 (2020_0627_1130)";

	private MatrixComplex cJ;
	private MatrixComplex cP;
	private boolean factorized = false;

	/*
	 * 	CONSTRUCTORS 
	 */
	/**
	 * Instantiates a complex square array of length len.
	 * @param len The length of the square array.
	 */
	/*
	public Jordan(int len) {
		super(len);
	}
	*/
	
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
		super(matrix);
		//this.complexMatrix = matrix.complexMatrix.clone();
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
			if (row+1 < this.cols()-1) block.setItem(row, row+1, Complex.ONE);
		}
		return block;
	}

	/**
	 * Calculates the eigenvector or characteristic vector using as generating space (M-lI)^a 
	 * where M is the coef matrix, l is the eigenvalue, I is the identity matrix and a i the power to raise the  
	 * @param eigenval The eigenvalue to calculate the eigenvector
	 * @param arithMult The arithmetic multiplicity
	 * @return A MatrixComplex with the eigenvectors
	 */
	public MatrixComplex eigenvectors(Complex eigenval, int arithMult) {
		int order = arithMult;
		MatrixComplex I = this.eye();
		MatrixComplex eigenVect = new MatrixComplex(0,0);
		MatrixComplex cMatrix;
		MatrixComplex sols;
		
		for (order = arithMult; order > 1; --order) {
			cMatrix = ((this.minus(I.times(eigenval))).power(order)).augment(); //.heap();
			cMatrix.println("------------------[f-I]^" + order);
			sols = cMatrix.solve(true);
			eigenVect.appendRows(sols.getRow(0));
		}
		cMatrix = ((this.minus(I.times(eigenval))).power(order)).augment(); //.heap();
		cMatrix.println("------------------[f-I]^" + order);
		if (arithMult > 1) {
			sols = cMatrix.unkMatrix().times(eigenVect.getRow(arithMult-order-1).transpose()).transpose();
			eigenVect.appendRows(sols);
			eigenVect.transrow();
		}
		else {
			cMatrix = (this.minus(I.times(eigenval))).augment(); //.heap();
			cMatrix.println("------------------[f-I]^" + order);
			sols = cMatrix.solve(true);
			eigenVect.appendRows(sols);
		}
		return eigenVect;
	}

	/**
	 * 
	 * @param eigenValArray
	 * @return
	 */
	public MatrixComplex jordanForm(MatrixComplex eigenValArray) {
		int rowLen = this.rows(); 
		int colLen= this.cols();
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Jordan Matrix");
			System.exit(-1);
		}
		MatrixComplex jordanForm = new MatrixComplex(rowLen, colLen);
		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = this.arithmeticMultiplicity(eigenval);
			MatrixComplex jordanBlock = this.block(i, arithMult, eigenval);
			jordanForm = jordanForm.plus(jordanBlock);
			i += arithMult;
		}
		return jordanForm;
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
		MatrixComplex eigenValArray = this.values();
		eigenValArray.quicksort(0);
		MatrixComplex eigenVectArray = this.vectors();
		eigenVectArray.println(HEADINFO + "eigenVectArray");
		
		cJ = new MatrixComplex(rowLen, colLen);
		cP = new MatrixComplex(0, 0);
		
		cJ = jordanForm(eigenValArray);
		cJ.println("----------JORDAN");
		
		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = this.arithmeticMultiplicity(eigenval);
			//int geomMult = this.geometricMultiplicity(eigenval);
			
			System.out.println("\n" + HEADINFO + "Testing eigenval:" + eigenval.toString() + "\n");
			MatrixComplex eigenVect = this.eigenvectors(eigenval, arithMult);
			eigenVect.println("----------EIGENVECTORS for eigenvalue:" + eigenval.toString() + ", multiplicity:" + arithMult);
			cP.appendRows(eigenVect);
			cP.println(HEADINFO + "+ + + + + 3. computing cP");
			i += arithMult;
		}
		cP = cP.transpose();
		cP.println("----------PASS MATRIX");
	}

	public void factorize2() {
		int rowLen = this.rows(); 
		int colLen= this.cols();
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Jordan Matrix");
			System.exit(-1);
		}
		MatrixComplex eigenValArray = this.values();
		eigenValArray.quicksort(0);
		MatrixComplex eigenVectArray = this.vectors();
		eigenVectArray.println(HEADINFO + "eigenVectArray");
		
		cJ = new MatrixComplex(rowLen, colLen);
		cP = new MatrixComplex(rowLen, colLen);
		
		cJ = jordanForm(eigenValArray);
		cJ.println("----------JORDAN");
		
		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = this.arithmeticMultiplicity(eigenval);
			int geomMult = this.geometricMultiplicity(eigenval);
			
			System.out.println("\n" + HEADINFO + "Testing eigenval:" + eigenval.toString() + "\n");
			
			if (arithMult == geomMult) {
				for (int sol = 0; sol < arithMult; ++sol) {
					cP.complexMatrix[i+sol] = eigenVectArray.complexMatrix[i+sol].clone();
					cP.println(HEADINFO + "+ + + + + 1. computing cP");
				}
				
			}
			else {
				int offset = 0;
				for (int sol = 0; sol < geomMult; ++sol) {
					cP.complexMatrix[i+sol] = eigenVectArray.complexMatrix[i+sol].clone();
					cP.println(HEADINFO + "+ + + + + 2. computing cP");
					++offset;
				}
				
				MatrixComplex eigenVect = this.eigenvectors(eigenval, arithMult);
				eigenVect.println("----------EIGENVECTORS for eigenvalue:" + eigenval.toString() + ", multiplicity:" + arithMult);
				for (int sol = 0; sol < eigenVect.rows() - offset; ++sol) {
					cP.complexMatrix[sol+offset] = eigenVect.complexMatrix[sol].clone();
					cP.println(HEADINFO + "+ + + + + 3. computing cP");
				}
			}
			i += arithMult;
		}
		cP = cP.transpose();
		cP.println("----------PASS MATRIX");
	}

}
