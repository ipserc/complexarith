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
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.arith.syseq.Syseq;

/**
 * 
 * @author ipserc
 *
 */
public class Eigenspace extends MatrixComplex {
	
	private final static String HEADINFO = "Eigenspace --- INFO: ";
	private final static String VERSION = "1.1 (2021_0207_2100)";

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
	 * @param cmatrix The MatrixComplex that generates the Eigen Space
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
	 * @param cmatrix The MatrixComplex that generates the Eigen Space
	 */
	public Eigenspace(Complex seed, MatrixComplex cmatrix) {
		super();
		this.complexMatrix = cmatrix.complexMatrix.clone();
		this.seed = seed;
		calculate();
	}
	
	/**
	 * Instantiates an eigenspace class from matrix represented as a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix The matrix that generates the Eigen Space
	 */
	public Eigenspace(String strMatrix) {
		super(strMatrix);
		seed = Complex.ONE;
		calculate();		
	}
	
	/**
	 * Instantiates an eigenspace class from matrix represented as a string, rows are separated with ";", cols are separated with ",".
	 * @param seed Is the value of the constant used to find the eigenvectors 
	 * @param strMatrix The matrix that generates the Eigen Space
	 */
	public Eigenspace(Complex seed, String strMatrix) {
		super(strMatrix);
		this.seed = seed;
		calculate();
	}

	/*
	 * ***********************************************
	 * GETTERS
	 * ***********************************************
	 */
	
	/**
	 * Private Method. Calculates the Eigen Space components. Characteristic Polynomial, Eigenvalues and Eigenvetors
	 */
	private void calculate() {
		charactPoly = this.charactPoly();
		eigenval();
		eigenvect();
	}
	
	/*
	 * ***********************************************
	 * GETTERS
	 * ***********************************************
	 */
	
	/**
	 * Returns the eigenvectors as a MatrixComplex with the vectors in the array rows
	 * @return The eigenvectors
	 */
	public MatrixComplex vectors() {
		return vectors;
	}
	
	/**
	 * Returns the eigenvectors as a MatrixComplex with the vectors in the array rows
	 * @return The eigenvectors
	 */
	public MatrixComplex base() {
		return vectors;
	}
	
	/**
	 * Returns the eigenvalues as a MatrixComplex with the values in the rows of one column array
	 * @return The eigenvalues
	 */
	public MatrixComplex values() {
		return values;
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
	public Order order() {
		return order;
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
		order = Order.DOWN;
		values.quicksort(0);
	}

	/**
	 * Swaps the order of the eigenvalues and the eigenvectors
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
	 * Private method. Implementation of eigenvect
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
			// int arithMult = this.arithmeticMultiplicity(eigenval);
			cMatrix = (I.times(eigenval)).minus(this);
			dMatrix = cMatrix.augment().heap();
			// System.out.println("Ec.Caract.["+rowEig+"]" + dMatrix.toMatrixComplex());
			eigenVect = dMatrix.solve(seed);
			for (int sol = 0; sol < eigenVect.rows(); ++sol) {
				vectors.complexMatrix[rowEig+sol] = eigenVect.complexMatrix[sol].clone();
			}
			rowEig += eigenVect.rows();
			// rowEig += arithMult;
		}
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
	public String Maxima_eigenvalues(boolean expand) {
		String toMaxima = "eigenvalues("+this.toMaxima()+")";
		return expand ? "expand("+toMaxima+")" : toMaxima; 		
	}
	
	/**
	 * Returns the eigenvectors command as a String for the matrix to use in Maxima
	 * @param expand If true adds epxpand() to the expression
	 * @return The eigenvectors command for Maxima 
	 */
	public String Maxima_eigenvectors(boolean expand) {
		String toMaxima = "eigenvectors("+this.toMaxima()+")";
		return expand ? "expand("+toMaxima+")" : toMaxima; 		
	}
	
	/**
	 * Returns the charpoly command as a String for the matrix to use in Maxima
	 * @param expand If true adds epxpand() to the expression
	 * @return The charpoly command for Maxima 
	 */
	public String Maxima_charpoly(boolean expand) {
		String toMaxima = "charpoly("+this.toMaxima()+",t)";
		return expand ? "expand("+toMaxima+")" : toMaxima;
	}
	
	/**
	 * Returns the eig command as a String for the matrix to use in Octave
	 * @return The eig command for Octave
	 */
	public String Octave_eigenvectors() {
		return "[V,lambda] = eig("+this.toMatlab()+")";
	}

	/**
	 * Returns the eigenvectors command as a String for the matrix to use in Wolfram
	 * @return The eigenvectors command for Wolfram
	 */
	public String Wolfram_eigenvectors() {
		return "eigenvectors("+this.toWolfram()+")";		
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
		
		for (int rowEig = 0; rowEig < rowLen;) {
			eigenval = values.getItem(rowEig, 0);
			cMatrix = (I.times(eigenval)).minus(this);
			dMatrix = cMatrix.augment().heap();
			system.complexMatrix = dMatrix.complexMatrix;
			System.out.print("Ec.Caract.["+rowEig+"]: " ); system.printSystemEqSolve(format, display);
			rowEig += this.arithmeticMultiplicity(eigenval);
		}
	}

	/**
	 * Prints the characteristics equations for each eigenvalue in MATRIXCOMPLEX dialect
	 */
	public void printCharactEq() {
		printCharactEq(outputFormat.MATRIXCOMPLEX, false);
	}

}
