/*
 * Factorización LU

    Aplicable a: una matriz cuadrada A

    Factorización: A = L U, donde L es una matriz triangular inferior y U es una matriz triangular superior

    Notas: La factorización LU expresa el método de Gauss en forma matricial. En efecto, PA = LU donde P es una matriz 
    de permutación. Los elementos de la diagonal principal de L son todos iguales a 1. Una condición suficiente de que 
    exista la factorización es que la matriz A sea invertible.

    Resolución del sistema de ecuaciones lineales Ax = b: primero se resuelve el sistema de ecuaciones Ly = b y después 
    Ux = y.

    Existencia: Una condición necesaria y suficiente es que todos los menores principales de A sean distintos de cero.1

    Métodos de cálculo: método de Crout que obtiene una matriz U cuyos elementos de la diagonal son todos 1. 
    El método de Doolittle es una modificación del mismo.

 */


package com.ipserc.arith.factorization;

import com.ipserc.arith.matrixcomplex.*;

/**
 * 
 * @author ipserc
 *
 */
public class LUfactor extends MatrixComplex {

	private MatrixComplex cL;
	private MatrixComplex cU;
	private MatrixComplex cP;
	private boolean factorized = false;

	private final static String HEADINFO = "LUfactor --- INFO: ";
	private final static String VERSION = "1.1 (2021_0214_2300)";

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
	 * Instantiates a complex square array of length len.
	 * @param len The length of the square array.
	 */
	public LUfactor(int len) {
		super(len);
	}

	/**
	 * Instantiates a complex array of dimensions rowLen x colLen.
	 * @param rowLen Number of rows.
	 * @param colLen Number of columns.
	 */
	public LUfactor(int rowLen, int colLen) {
		super(rowLen, colLen);
	}

	/**
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with "," and factorizes it.
	 * @param strMatrix the string with the rows and columns.
	 */
	public LUfactor(String strMatrix) {
		super(strMatrix);
		LUfactorizePP();
	}

	/**
	 * Instantiates a LUfactor array from a MatrixComplex and factorizes it.
	 * @param matrix the MatrixComplex already instantiated.
	 */
	public LUfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
		LUfactorizePP();
	}

	/*
	 * ***********************************************
	 * 	FACTORIZATION
	 * ***********************************************
	 */

	/**
	 * Factorizes the array using the LU decomposition.
	 * In numerical analysis and linear algebra, LU decomposition (where 'LU' stands for 'lower upper', and also called LU factorization) factors a matrix as the product of a lower triangular matrix and an upper triangular matrix. 
	 * The product sometimes includes a permutation matrix as well. The LU decomposition can be viewed as the matrix form of Gaussian elimination. 
	 * Computers usually solve square systems of linear equations using the LU decomposition, and it is also a key step when inverting a matrix, or computing the determinant of a matrix. 
	 * The LU decomposition was introduced by mathematician Tadeusz Banachiewicz in 1938.[Source Wikipedia]
	 */
	public void factorice() {
		if (factorized) return;
		this.LUfactorizePP();
	}
	
	/**
	 * Factorizes the array using the LU decomposition.
	 * In numerical analysis and linear algebra, LU decomposition (where 'LU' stands for 'lower upper', and also called LU factorization) factors a matrix as the product of a lower triangular matrix and an upper triangular matrix. 
	 * The product sometimes includes a permutation matrix as well. The LU decomposition can be viewed as the matrix form of Gaussian elimination. 
	 * Computers usually solve square systems of linear equations using the LU decomposition, and it is also a key step when inverting a matrix, or computing the determinant of a matrix. 
	 * The LU decomposition was introduced by mathematician Tadeusz Banachiewicz in 1938.[Source Wikipedia]
	 */
	private void LUfactorizePP() {
		int rowLen = this.complexMatrix.length;
		int colLen = this.complexMatrix[0].length;

		if (rowLen != colLen) {
			factorized = false;
			return;
		}

		cP = new MatrixComplex(rowLen, colLen); cP.initMatrixDiag(1,0);
		cL = new MatrixComplex(rowLen, colLen); cL.initMatrixDiag(1,0);
		//cU = new MatrixComplex(rowLen, colLen); 
		cU = this.copy();

		for (int k = 0; k < rowLen-1; ++k) {
			if (cU.complexMatrix[k][k].equals(0,0)) {
				int rowSwap = cU.partialPivot(k);
				//int rowSwap = cU.locateSwapRowUp(k);
				if (rowSwap == -1) 
					return;
				if (rowSwap != k) {
					cU.swapRows(k, rowSwap);
					cP.swapRows(k, rowSwap);				
					cL.swapRows(k, rowSwap);
					//cL.swapRowsL(k, rowSwap);
				}
			}
			for (int row = k+1; row < rowLen; ++row) {
				cL.complexMatrix[row][k] = cU.complexMatrix[row][k].divides(cU.complexMatrix[k][k]);
				cU.complexMatrix[row][k].setComplexRec(0,0);
				for (int col = k+1; col < colLen; ++col) {
					cU.complexMatrix[row][col] = cU.complexMatrix[row][col].minus(cU.complexMatrix[k][col].times(cL.complexMatrix[row][k]));
				}
			}
		}
		factorized = true;
	}

	/*
	 * ***********************************************
	 * 	GETTERS 
	 * ***********************************************
	 */

	/**
	 * Gets the class member variable with the Lower array.
	 * @return The Lower array of the LU decomposition.
	 */
	public MatrixComplex L() {
		return cL;
	}

	/**
	 * Gets the class member variable with the Upper array.
	 * @return The Upper array of the LU decomposition.
	 */
	public MatrixComplex U() {
		return cU;
	}

	/**
	 * Gets the class member variable with the Permutation array.
	 * @return The Permutation array of the LU decomposition.
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
	
	/*
	 * ***********************************************
	 * 	PRINTING
	 * ***********************************************
	 */

	/**
	 * Returns the expression for LU Factorization for Maxima. expand is available. 
	 * @param expand True if you want to expand the expressions
	 * @return The LU Factorization expression
	 * 
	 * generalring - el anillo de las expresiones de Maxima
	 * floatfield - el campo de los números decimales en coma flotante de doble precisión
	 * complexfield - el campo de los números complejos decimales en coma flotante de doble precisión
	 * crering - el anillo de las expresiones canónicas racionales (Canonical Rational Expression o CRE) de Maxima
	 * rationalfield - el campo de los números racionales
	 * runningerror - controla los errores de redondeo de las operaciones en coma flotante
	 * noncommutingring - el anillo de las expresiones de Maxima en las que el producto es el operador no conmutativo "." 
	 */
	public String toMaxima_lu_factor(boolean expand) {
		String toMaxima;
		toMaxima = "get_lu_factors(lu_factor(" +this.toMaxima()+", complexfield))";
		toMaxima = expand ? "expand("+toMaxima+");" : ";";
		return toMaxima;
	}
	
	/**
	 * Returns the expression for LU Factorization for GNU Octave. expand is available. 
	 * @return The LU Factorization expression
	 */
	public String toOctave_lu() {
		String toOctave;
		toOctave = "[l, u, p] = lu("+this.toOctave()+")";
		return toOctave;
	}

	/**
	 * Returns the expression for LU Factorization for Matlab. expand is available. 
	 * @return The LU Factorization expression
	 */
	public String toMatlab_lu() {
		String toMatlab;
		toMatlab = "[l, u, p] = lu("+this.toMatlab()+")";
		return toMatlab;
	}

	/**
	 * Returns the expression for LU Factorization for Wolfram. expand is available. 
	 * @return The LU Factorization expression
	 */
	public String toWolfram_LUDecomposition() {
		String toWolfram;
		toWolfram = "LUDecomposition["+this.toWolfram()+"]";
		return toWolfram;
	}
}
