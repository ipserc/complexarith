/**
 * 
 */
package com.ipserc.arith.matrixcomplex;

/**
 * @author ipserc
 *
 */

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;

public class Eigenspace extends MatrixComplex {
	/**
	 * Enumeration that gives the value of the order in wihch the eigenvalues, and therefore, the eigenvectors are returned
	 * DOWN: The eigenvalues are sorted from higher to lower
	 * UP: The eigenvalues are sorted from lower to higher
	 */
	public enum Order {DOWN, UP};
	
	private MatrixComplex vectors;
	private MatrixComplex values;
	private Polynom charactPoly;
	private Complex seed;
	private Order order;
	
	/*
	 * CONSTRUCTORS
	 */
	/**
	 * 
	 * @param cmatrix
	 */
	public Eigenspace(MatrixComplex cmatrix) {
		super();
		this.complexMatrix = cmatrix.complexMatrix.clone();
		this.seed = Complex.ONE;
		calculate();
	}

	/**
	 * 
	 * @param seed
	 * @param cmatrix
	 */
	public Eigenspace(Complex seed, MatrixComplex cmatrix) {
		super();
		this.complexMatrix = cmatrix.complexMatrix.clone();
		this.seed = seed;
		calculate();
	}
	
	/**
	 * 
	 * @param strMatrix
	 */
	public Eigenspace(String strMatrix) {
		super(strMatrix);
		seed = Complex.ONE;
		calculate();		
	}
	
	/**
	 * 
	 * @param seed
	 * @param strMatrix
	 */
	public Eigenspace(Complex seed, String strMatrix) {
		super(strMatrix);
		this.seed = seed;
		calculate();
	}

	/*
	 * GETTERS
	 */
	/**
	 * 
	 */
	private void calculate() {
		charactPoly = this.charactPoly();
		eigenval();
		eigenvect();
	}
	
	/*
	 * GETTERS
	 */
	/**
	 * 
	 * @return
	 */
	public MatrixComplex vectors() {
		return vectors;
	}
	
	/**
	 * 
	 * @return
	 */
	public MatrixComplex base() {
		return vectors;
	}
	
	/**
	 * 
	 * @return
	 */
	public MatrixComplex values() {
		return values;
	}
	
	/**
	 * 
	 * @return
	 */
	public Polynom getCharactPoly() {
		return charactPoly;
	}
	
	/**
	 * Gets the order in which the eigenvalues are sorted. The eigenvectors follow this order
	 * @return the order in which the eigenvalues are stored
	 */
	public Order order() {
		return order;
	}
	
	/*
	 * OPERATORS
	 */

	/**
	 * Returns the arithmetic multiplicity of an specific eigenvalue
	 * @param eigenVal The value to evaluate the arithmetic multiplicity
	 * @return The arithmetic multiplicity
	 */
	public int arithmeticMultiplicity(Complex eigenVal) {
		int arithMult = 0;
		for (int i = 0; i < this.rows(); ++i) {
			if (values.getItem(i,0).equalsred(eigenVal)) ++arithMult;
		}
		return arithMult;
	}
	
	/**
	 * Returns the geometric multiplicity of an specific eigenvalue using the eigenvectors matrix
	 * @param eigenVal The value to evaluate the geometric multiplicity
	 * @return The geometric multiplicity
	 */
	public int geometricMultiplicity(Complex eigenVal) {
		MatrixComplex eigenV = new MatrixComplex(vectors.rows(), vectors.cols());

		for (int i = 0; i < this.rows(); ++i) {
			if (values.getItem(i,0).equalsred(eigenVal)) eigenV.complexMatrix[i] = vectors.complexMatrix[i];
		}
		return eigenV.rank();
	}
		
	/**
	 * Calculates the eigenvalue, characteristic value, or characteristic root associated with eigenvector v by solving the Characteristic Polynomial.
	 * An eigenvalue is a scalar associated with a given linear transformation of a vector space and having the property that there is some nonzero vector which when multiplied by the scalar is equal to the vector obtained by letting the transformation operate on the vector; especially :  a root of the characteristic equation of a matrix
	 * The eigenvalues as a column array
	 */
	public void eigenval() {
		values = charactPoly.solve();
		// values.quicksortup(0); // DO NOT USE - SVD factorization doesn't allow this
		// order = Order.UP;
		// By dafault the order in which the eigenvalues are sorted is from Higher to Lower
		values.quicksort(0);
		order = Order.DOWN;
	}

	/**
	 * 
	 */
	public void orderSwap() {
		MatrixComplex valuesTmp = values.clone();
		MatrixComplex vectorsTmp = vectors.clone();
		// swap values
		for (int row = 0; row < values.rows(); ++row)
			values.complexMatrix[values.rows() - row - 1] = valuesTmp.complexMatrix[row];
		// swap vectors
		for (int row = 0; row < values.rows(); ++row)
			vectors.complexMatrix[values.rows() - row - 1] = vectorsTmp.complexMatrix[row];
		order = order == Order.DOWN? Order.UP : Order.DOWN;
	}
	
	/**
	 * Calculates the eigenvector or characteristic vector of a linear transformation associated to an specific eigenvalues.
	 * The eigenvector is a non-zero vector whose direction does not change when that linear transformation is applied to it
	 * The eigenvector as a column array
	 */
	public void eigenvect() {
		eigenvectors2();
	}
	
	/**
	 * 
	 */
	private void eigenvectors2() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex I = new MatrixComplex(rowLen, colLen); I.initMatrixDiag(1,0);
		MatrixComplex eigenVect;
		MatrixComplex cMatrix, dMatrix;
		Complex eigenval;
		vectors = new MatrixComplex(rowLen, colLen);
		
		for (int rowEig = 0; rowEig < rowLen;) {
			eigenval = values.getItem(rowEig, 0);
			int arithMult = this.arithmeticMultiplicity(eigenval);
			cMatrix = (I.times(eigenval)).minus(this);
			dMatrix = cMatrix.augment().heap();
			eigenVect = dMatrix.solve(seed);
			for (int sol = 0; sol < eigenVect.rows(); ++sol) {
				vectors.complexMatrix[rowEig+sol] = eigenVect.complexMatrix[sol].clone();
			}
			rowEig += arithMult;
		}
	}
}
