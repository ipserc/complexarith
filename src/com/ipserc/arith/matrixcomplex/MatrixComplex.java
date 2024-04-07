package com.ipserc.arith.matrixcomplex;

import java.lang.Math;

import javax.management.NotCompliantMBeanException;

import com.ipserc.arith.combinatoric.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.polynom.*;
import com.ipserc.arith.syseq.Syseq;
import com.ipserc.arith.vector.Vector;
import com.panayotis.gnuplot.JavaPlot;

/**
 * 
 * @author ipserc
 *
 */
public class MatrixComplex {
	public Complex[][] complexMatrix;
	
	private final static String HEADINFO = "MatrixComplex --- INFO: ";
	private final static String VERSION = "1.14 (2024_0320_1945)";
	/* VERSION Release Note
	 * 
	 * 1.14 (2024_0320_1945)
	 * 	public MatrixComplex log10() {
	 * 	public static MatrixComplex log10(MatrixComplex matrix) {
	 * 	public MatrixComplex logbase(Complex base) {
	 * 	public static MatrixComplex logbase(MatrixComplex matrix, Complex base) {
	 * 	public MatrixComplex logbase(Complex base) {
	 * 	public static MatrixComplex logbase(MatrixComplex matrix, Complex base) {
	 * 	public static MatrixComplex power(Complex cBase, MatrixComplex exponent) {
	 * 	public static MatrixComplex power(double base, MatrixComplex exponent) {
	 * 	private MatrixComplex trigonTaylor(int sign) {
	 *  public MatrixComplex sinTaylor() {
	 *  public MatrixComplex sinEuler() {
	 *  public MatrixComplex cosTaylor() {
	 *  public MatrixComplex cosEuler() {
	 * 	public MatrixComplex euler() {
	 * 	public static MatrixComplex euler(MatrixComplex matrix) {
	 * 	public MatrixComplex normalize2PI() {
	 *  REMOVED private MatrixComplex log1mx() {
	 *  REMOVED private MatrixComplex log1px() {
	 *  public MatrixComplex logTaylor() {
	 *  public MatrixComplex logMercator() {
	 *  public MatrixComplex power(MatrixComplex mcExpo) {
	 *  public static MatrixComplex power(MatrixComplex mcBase, MatrixComplex mcExpo) {
	 * 
	 *  
	 * 1.13 (2024_0120_2330)
	 *  public MatrixComplex exp() {
	 *  public MatrixComplex sinTaylor() {
	 *  public MatrixComplex sinEuler() {
	 *  public MatrixComplex sin() {
	 *  public MatrixComplex cosTaylor() {
	 *  public MatrixComplex cosEuler() {
	 *  public MatrixComplex cos() {
	 *  public MatrixComplex tanTaylor() {
	 *  public MatrixComplex tanEuler() {
	 *  public MatrixComplex tan() {
	 *  public MatrixComplex sinhTaylor() {
	 *  public MatrixComplex sinhEuler()
	 *  public MatrixComplex sinh() {
	 *  public MatrixComplex coshTaylor() {
	 *  public MatrixComplex coshEuler() {
	 *  public MatrixComplex cosh() {
	 *  public MatrixComplex tanhTaylor() {
	 *  public MatrixComplex tanhEuler() {
	 *  public MatrixComplex tanh() {
	 *  private MatrixComplex log1mx() {
	 *  private MatrixComplex log1px() {
	 *  public MatrixComplex logTaylor() {
	 *  public MatrixComplex logMercator() {
	 *  public MatrixComplex logHat() {
	 *  public MatrixComplex log() {
	 *  public boolean sameDimension(MatrixComplex matrix) {
	 *  public Complex totalize() {
	 *  public boolean isGT(MatrixComplex matrix) {
	 *  public boolean isGTE(MatrixComplex matrix) {
	 *  public boolean isLT(MatrixComplex matrix) {
	 *  public boolean isLTE(MatrixComplex matrix) {
	 *  public boolean isPostiveSemiDefinite() {
	 *  public boolean isNegtiveDefinite() {
	 *  public boolean isNegtiveSemiDefinite() {
	 *  public boolean hasZeroMainDiag() {
	 *  public boolean repPositiveMainDiag() {
	 *  public MatrixComplex plusMat(Complex cNum) {
	 *  public MatrixComplex plusMat(String strcNum) {
	 *  public MatrixComplex plusMat(double rep, double imp) {
	 *  public MatrixComplex minusMat(Complex cNum) {
	 *  public MatrixComplex minusMat(String strcNum) {
	 *  public MatrixComplex minusMat(double rep, double imp) {
	 *  private void doPlot(String Title, double[][] dataTable) {
	 * 
	 * 1.12 (2023_0507_1800)
	 * 	public MatrixComplex times(MatrixComplex cMatrix)
	 *  	Removed because makes to enter in an infinite loop call
	 *  		if (this.rows() == 1 && this.cols() == 1) return cMatrix.times(this.getItem(0, 0));
	 *  		if (cMatrix.rows() == 1 && cMatrix.cols() == 1) return this.times(cMatrix.getItem(0, 0));
	 * 
	 * 1.11 (2022_0319_2359)
	 * public MatrixComplex base()
	 * 
	 * 1.10 (2022_0123_0100)
	 *  kernel of a basis' generator of vectors
	 * 		kernel(Complex lambda)
	 * 		kernel()
	 * 		ker(Complex lambda)
	 * 		ker()
	 * 	public MatrixComplex normalize(): shortcut to normalizeByRows()
	 * 	public MatrixComplex normalizeByCols()
	 * 	public MatrixComplex normalizeByRows()
	 *  public void setCol(int colIdx, Complex cValue)
	 *  public void setRow(int rowIdx, Complex cValue)
	 *  public MatrixComplex rowReduce()
	 *  public MatrixComplex getCol(int colIdx)
	 *  public boolean isTriangleLo() Missing curly braces and remade the method
	 *  public MatrixComplex gramSchmidt() Reprogrammed
	 *  public MatrixComplex gramSchmidtFull() Reprogrammed
	 *  public MatrixComplex gramSchmidtMFull() Reprogrammed
	 *  public MatrixComplex gramSchmidtM() Reprogrammed
	 *  public int rank3() is now the rank method
	 *  public boolean isEmpty()
	 * 
	 * 1.9 (2022_0106_1400)
	 * solveGauss(Complex lambda, boolean Reduced) is now the method used to solve Equation Systems
	 * 		It uses the Gaussian Elimination and Back Substitution from a diagonalized matrix. 
	 * 		The matrix is "perfect" diagonalized to accommodate the rows in the position into the system where the not null main diagonal terms occupy the row corresponding with their column number
	 * 		It takes on account the use of the value lambda for the unknown, and so on, to find the base solution "solBase" for each equation
	 * 		The solBase keeps the fixed values for the unknowns with the null equations of the system
	 * 		With the solBase calculated, the rest of the solutions are calculated by the Gaussian Elimination and Back Substitution method
	 * 		solBase is constructed with the complex number parameter lambda
	 * MatrixComplex power(int power). Now power can be negative.
	 * 
	 * 1.8 (2021_1106_1400)
	 * augment(matrixComplex interms) is now using augment2(matrixComplex interms) which returns an augmented matrix with full columns. 
	 * augment1(matrixComplex interms) is DEPRECATED and is kept only for recovery.
	 * gramSchmidtGauss() added. It should be used only with square matrices.
	 * 
	 * 1.7 (2021_0929_2000)
	 * solveGauss is now using the solveGauss2 Method. solveReduction and solveSubstition dosen't work right and are deprecated.
	 */

	private int mSign = 1; //Tracks the correct sign in the determinants calculated through triangularization (Chio's rule)

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
	
	/**
	 * Enumeration that selects the possible output format for some expressions 
	 * MATRIXCOMPLEX, 
	 * MAXIMA, 
	 * OCTAVE, 
	 * MATLAB, 
	 * WOLFRAM
	 */
	public enum outputFormat {MATRIXCOMPLEX, MAXIMA, OCTAVE, MATLAB, WOLFRAM};

	/*
	 * __DEBUG__
	 */
	
	private static boolean __DEBUG__ = false;

	public static void debugON() {
		__DEBUG__ = true;
	}

	public static void debugOFF() {
		__DEBUG__ = false;
	}

	public static boolean debug() {
		return __DEBUG__;
	}

	private static boolean __DOPLOT__ = true;

	public static void doPlotON() {
		__DOPLOT__ = true;
	}

	public static void doPlotOFF() {
		__DOPLOT__ = false;
	}

	public static boolean doPlot() {
		return __DOPLOT__;
	}


	/*
	 * ***********************************************
	 * 	CONSTRUCTORS 
	 * ***********************************************
	 */

	/**
	 * Returns the empty complex square matrix object of length 0.
	 */
 	public MatrixComplex() {
		this.complexMatrix = new Complex[0][0];
	}

	/**
	 * Returns the complex square matrix object of length len.
	 * @param len length of the square matrix.
	 */
	public MatrixComplex(int len) {
		this.complexMatrix = new Complex[len][len];
		for (int row = 0; row < len; ++row)
			for (int col = 0; col < len; ++col)
				this.complexMatrix[row][col] = new Complex();
	}

	/**
	 * Instantiates the complex matrix object of dimensions rowLen x colLen.
	 * @param rowLen Number of rows.
	 * @param colLen Number of columns.
	 */
	public MatrixComplex(int rowLen, int colLen) {
		this.complexMatrix = new Complex[rowLen][colLen];
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				this.complexMatrix[row][col] = new Complex();
	}

	/**
	 * Instantiates the complex matrix object from a string representation of the matrix where rows are separated with ";", columns are separated with ",".
	 * @param cadena the string with the rows and columns.
	 */
	public MatrixComplex(String cadena) {
		this.setMatrix(cadena);
	}

	/**
	 * Method for creating a complex matrix object from a string representation of the matrix where rows are separated with ";", columns are separated with ",".
	 * @param cadena the string representation of the matrix.
	 */
	public void setMatrix(String cadena) {
		String[] sRow = cadena.split(";");
		int rowLen = sRow.length;
		String[] sCol = sRow[0].split(",");
		int colLen = sCol.length;
		complexMatrix = new Complex[rowLen][colLen];
		for (int row = 0; row < rowLen; ++row) {
			sCol = sRow[row].split(",");
			colLen = sCol.length;
			for (int col = 0; col < colLen; ++col)
				complexMatrix[row][col] = new Complex(sCol[col].trim());
		}
	}

	/*
	 * ***********************************************
	 * INITIALIZERS
	 * ***********************************************
	 */
	
	/**
	 * Method for initializing a complex matrix with all its items set to the value csNum.
	 * @param csNum String expression of the number in rectangular "A+Bi" or polar "A|B" coordinates.
	 */
	public void initMatrix(String csNum) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cNum = new Complex(csNum);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				this.complexMatrix[row][col].setComplexRec(cNum.rep(), cNum.imp());
	}

	/**
	 * Method for initializing a complex matrix with all its items set to the value cNum.
	 * @param cNum Complex number.
	 */
	public void initMatrix(Complex cNum) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				this.complexMatrix[row][col].setComplexRec(cNum.rep(), cNum.imp());
	}

	/**
	 * Method for initializing a complex matrix with all its items set to the value from the two parts of the complex both Cartesian or Polar representation.
	 * @param coordType Type of coordinate "C" Cartesian, "P" Polar.
	 * @param n1 Value of the first coordinate.
	 * @param n2 Value of the second coordinate.
	 */
	private void initMatrix(char coordType, double n1, double n2) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				switch (coordType) {
					case 'C':
					case 'c':this.complexMatrix[row][col].setComplexRec(n1, n2); 
					break;
					case 'P':
					case 'p':this.complexMatrix[row][col].setComplexPol(n1, n2); 
					break;
				}
	}

	/**
	 * Shortcut of initMatrixRec.
	 * Initializes an array with a complex value in Cartesian coordinates specified as real part and imaginary part.
	 * @param n1 Real Part.
	 * @param n2 Imaginary Part.
	 */    
	public void initMatrix(double n1, double n2) {
		this.initMatrixRec(n1, n2);
	}

	/**
	 * Initializes an array with a complex value in Cartesian coordinates specified as real part and imaginary part.
	 * @param n1 Real Part.
	 * @param n2 Imaginary Part.
	 */
	public void initMatrixRec(double n1, double n2) {
		this.initMatrix('C', n1, n2);
	}

	/**
	 * Initializes an array with a complex value in polar coordinates specified as module and phase.
	 * @param n1 Module.
	 * @param n2 Phase.
	 */
	public void initMatrixPol(double n1, double n2) {
		this.initMatrix('P', n1, n2);
	}

	/**
	 * Initializes the main diagonal of an array with the complex number indicated in text.
	 * @param csNum Complex number in text format.
	 */
	public void initMatrixDiag(String csNum) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cNum = new Complex(csNum);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (row == col) this.complexMatrix[row][col].setComplexRec(cNum.rep(), cNum.imp());
				else this.complexMatrix[row][col].setComplexRec(0, 0);
	}

	/**
	 * Private method to initialize the main diagonal of a matrix with a complex number expressed in Cartesian or polar coordinates.
	 * @param coordType Coordinate type 'C' or 'P'.
	 * @param n1 coordinate 1.
	 * @param n2 coordinate 2.
	 */
	private void initMatrixDiag(char coordType, double n1, double n2) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (row == col) {
					switch (coordType) {
						case 'C':
						case 'c':this.complexMatrix[row][col].setComplexRec(n1, n2); 
						break;
						case 'P':
						case 'p':this.complexMatrix[row][col].setComplexPol(n1, n2); 
						break;
					}
				}
				else this.complexMatrix[row][col].setComplexRec(0, 0);
	}

	/**
	 * Shortcut to initMatrixDiagRec
	 * Initializes the main diagonal of a matrix with the complex number in Cartesian coordinates indicated as real part and imaginary part.
	 * @param n1 Real part.
	 * @param n2 Imaginary Part.
	 */
	public void initMatrixDiag(double n1, double n2) {
		this.initMatrixDiagRec(n1, n2);
	}

	/**
	 * Shortcut to initMatrixDiagRec
	 * Initializes the main diagonal of a matrix with the complex number cNum.
	 * @param cNum the complex number
	 */
	public void initMatrixDiag(Complex cNum) {
		this.initMatrixDiagRec(cNum.rep(), cNum.imp());
	}

	/**
	 * Initializes the main diagonal of a matrix with the complex number in Cartesian coordinates indicated as real part and imaginary part.
	 * @param n1 Real part.
	 * @param n2 Imaginary Part.
	 */
	public void initMatrixDiagRec(double n1, double n2) {
		this.initMatrixDiag('C', n1, n2);
	}

	/**
	 * Initializes the main diagonal of an array with the complex number in polar coordinates indicated as module and phase.
	 * @param n1 Module.
	 * @param n2 Phase.
	 */
	public void initMatrixDiagPol(double n1, double n2) {
		this.initMatrixDiag('P', n1, n2);
	}

	/**
	 * Private method to initialize the main diagonal of an array with a base random number and a type of coordinate.
	 * @param coordType Type of coordinate 'A' Real, 'B' Integer, 'C' Cartesian Complex, 'D', Pure imaginary, 'E' Imaginary pure integer, 'P' Polar Complex, 'I' Complex Cartesian Integers, 'J' Polar Integers.
	 * @param base Base to generate the random number.
	 */
	private void initMatrixRandom(char coordType, int base) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				switch (coordType) {
					case 'A':
					case 'a':this.complexMatrix[row][col].setComplexRandomReal(base); 
					break;
					case 'B':
					case 'b':this.complexMatrix[row][col].setComplexRandomInt(base); 
					break;
					case 'C':
					case 'c':this.complexMatrix[row][col].setComplexRandomRec(base); 
					break;
					case 'D':
					case 'd':this.complexMatrix[row][col].setComplexRandomImag(base); 
					break;
					case 'E':
					case 'e':this.complexMatrix[row][col].setComplexRandomImagInt(base); 
					break;
					case 'P':
					case 'p':this.complexMatrix[row][col].setComplexRandomPol(base); 
					break;
					case 'I':
					case 'i':this.complexMatrix[row][col].setComplexRandomRecInt(base); 
					break;
					case 'J':
					case 'j':this.complexMatrix[row][col].setComplexRandomPolInt(base); 
					break;
				}
	}

	/**
	 * Initializes an array with a complex number of module 1 in Cartesian coordinates.
	 */
	public void initMatrixRandomRec() {
		this.initMatrixRandom('C', 1);
	}

	/**
	 * Initializes an array with a complex number of module 1 in polar coordinates.
	 */
	public void initMatrixRandomPol() {
		this.initMatrixRandom('P', 1);
	}

	/**
	 * Initializes an array with a real number between -1 and 1 in Cartesian coordinates.
	 */
	public void initMatrixRandomReal() {
		this.initMatrixRandom('A', 1);
	}

	/**
	 * Initializes an array with an integer between -1 and 1 in Cartesian coordinates.
	 */
	public void initMatrixRandomInt() {
		this.initMatrixRandom('B', 1);
	}

	/**
	 * Initializes an array with a pure imaginary number between -i and i in Cartesian coordinates.
	 */
	public void initMatrixRandomImag() {
		this.initMatrixRandom('D', 1);
	}

	/**
	 * Initializes an array with an integer pure imaginary number between -i and i in Cartesian coordinates.
	 */
	public void initMatrixRandomImagInt() {
		this.initMatrixRandom('E', 1);
	}

	/**
	 * Initializes an array with a complex module number between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomRec(int base) {
		this.initMatrixRandom('C', base);
	}

	/**
	 * Initializes an array with a complex module number between 0 and base in polar coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomPol(int base) {
		this.initMatrixRandom('P', base);
	}

	/**
	 * Initializes an array with a module integer between 0 and base in Cartesian coordinates
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomRecInt(int base) {
		this.initMatrixRandom('I', base);
	}

	/**
	 * Initializes an array with a complex integer number between 0 and base in polar coordinates
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomPolInt(int base) {
		this.initMatrixRandom('J', base);
	}

	/**
	 * Initializes an array with a real module number between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomReal(int base) {
		this.initMatrixRandom('A', base);
	}

	/**
	 * Initializes an array with a complex number of real module between 0 and base in polar coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomInteger(int base) {
		this.initMatrixRandom('B', base);
	}

	/**
	 * Initializes an array with an imaginary module number between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomImag(int base) {
		this.initMatrixRandom('D', base);
	}

	/**
	 * Initializes an array with an imaginary module integer between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomImagInt(int base) {
		this.initMatrixRandom('E', base);
	}

	/**
	 * Initializes an array sequentially with an integer from 0 with steps of 1 in 1 in polar coordinates.
	 */
	public void initMatrixSeqInt() {
		this.initMatrixSeqInt(0, 1);
	}

	/**
	 * Initializes an array sequentially with a real number from first with steps of 1 in 1 in polar coordinates.
	 * @param first Starting number.
	 */
	public void initMatrixSeqInt(double first) {
		this.initMatrixSeqInt(first, 1);
	}

	/**
	 * Initializes an array sequentially with a real number from first with increment "step" in polar coordinates.
	 * @param first Starting number.
	 * @param step Increment.
	 */
	public void initMatrixSeqInt(double first, double step) {
		int rowLen = this.rows();
		int colLen = this.cols();
		double val = first;
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col) {
				val += step;
				this.complexMatrix[row][col].setComplexPol(val,0);
			}
	}

	/*
	 * ***********************************************
	 * ALGEBRAIC BASIS
	 * ***********************************************
	 */

	/**
	 * Creates a base of a vector space.
	 * Allows the input of a set vectors written by rows, the base. 
	 * @param str the base vectors.
	 */
	public void base(String str) {
		MatrixComplex setOfVectors = new MatrixComplex(str); 
		this.complexMatrix = setOfVectors.complexMatrix.clone(); 
	}

	/**
	 * Calculates an orthonormal base associated to the first row component of the matrix
	 * @return the orthonormal base
	 */
	public MatrixComplex base() {
		MatrixComplex vectorBase = new MatrixComplex(this.rows(), this.cols());
		vectorBase.initMatrixDiag(1, 0);
		MatrixComplex normal = this.getRow(0).normalize();
		//Check for unitary vector and gets its position
		Complex sum = new Complex();
		int pos = -1;
		for (int i = 0; i < this.cols(); ++i) {
			if (normal.getItem(0, i).equals(Complex.ZERO)) continue;
			pos = i;
			sum = sum.plus(normal.getItem(0, i));
		}
		// if unitary vector is found, put the base row in its position
		// else use position 0
		if (!sum.equals(Complex.ONE)) pos = 0;
		vectorBase.setRow(pos, normal);
		vectorBase = vectorBase.gramSchmidt();
		return vectorBase;		
	}

	/**
	 * Creates a new base of a vector space.
	 * Allows the input of a set vectors written by rows, the base. 
	 * @param str the base vectors.
	 * @return The new matrix with the base.
	 */
	public MatrixComplex newBase(String str) {
		MatrixComplex setOfVectors = new MatrixComplex(str);
		return setOfVectors;
	}

	/*
	 * ***********************************************
	 * INTERNAL FUNCTIONS
	 * ***********************************************
	 */

	/**
	 * Returns the number of rows of the array
	 * @return The number of rows
	 */
	public int rows() {
		int rows = 0;
		try {
			rows = this.complexMatrix.length;
		}
		catch (Exception e){
			rows = 0;
		}
		return rows;
	}
	
	/**
	 * Returns the number of columns of the array
	 * @return The number of columns
	 */
	public int cols() {
		int cols = 0;
		
		try {
			cols = this.complexMatrix[0].length;
		}
		catch (Exception e) {
			cols = 0;
		}
		return cols;
	}
	
	/**
	 * Gets the item(row, col) of the array
	 * @param row
	 * @param col
	 * @return
	 */
	public Complex getItem(int row, int col) {
		return this.complexMatrix[row][col].copy();
	}
	
	/**
	 * Sets the item(row, col) of the array to a specific double number
	 * @param row
	 * @param col
	 * @param numD
	 */
	public void setItem(int row, int col, double numD) {
		this.complexMatrix[row][col].setComplexRec(numD, 0);
	}
	
	/**
	 * Sets the item(row, col) of the array to a specific complex number
	 * @param row
	 * @param col
	 * @param numC
	 */
	public void setItem(int row, int col, Complex numC) {
		this.complexMatrix[row][col] = numC.copy();
		// this.complexMatrix[row][col].setComplexRec(numC.rep(), numC.imp());
	}
	
	/**
	 * Sets the item(row, col) of the array to a specific complex number in string format
	 * @param row
	 * @param col
	 * @param snumC
	 */
	public void setItem(int row, int col, String snumC) {
		this.complexMatrix[row][col].setComplex(snumC);
	}
	
	/**
	 * Private method to return the components of a string separated by a delimiter. The components have to be integers.
	 * @param str String to chop.
	 * @param delimiter Delimiter of tokens.
	 * @return Array of integers with the tokens.
	 */
	private int[] intSplit(String str, String delimiter) {   	
		String[] sItem = str.split(delimiter);
		int[] token = new int[sItem.length];
		for (int i = 0; i < sItem.length; ++i) {
			token[i] = Integer.parseInt(sItem[i]);
		}
		return token;
	}

	/**
	 * Returns the best number of significant decimals based on the condition number
	 * @return the best number of significant decimals
	 */
	public int bestNumDecs() {
		int numDecs = (int)(this.cond()/2);
		return numDecs > Complex.getSignificative() ? Complex.getSignificative() : numDecs;
	}
	
	/*
	 * ***********************************************
	 * COPY & REPLICATION
	 * ***********************************************
	 */
	
	/**
	 * Copies the values of the array into another object.
	 * @return The new array with the copy.
	 */
	public MatrixComplex copy() {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex cMatrix = new MatrixComplex(rowLen, colLen);
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].copy();
		return cMatrix;
	}

	/**
	 * Clones the values of the array into another object.
	 * @return The new array with the clone.
	 */
	public MatrixComplex clone() {
		MatrixComplex cMatrix = this.copy();
		cMatrix.mSign = this.mSign;
		return cMatrix;
	}

	/*
	 * ***********************************************
	 * PRINTING
	 * ***********************************************
	 */

	/**
	 * Prints the array of values without carriage return.
	 */
	public void print() {
		//print(this.complexMatrix);
		this.printCM();
	}

	/**
	 * Prints the array of values with a carriage return.
	 */
	public void println() {
		//print(this.complexMatrix);
		this.printCM();
		System.out.print('\n');
	}

	/**
	 * Prints a title and then in a new line the array of values without carriage return.
	 * @param caption The title above the matrix.
	 */
	public void print(String caption) {
		int rowLen = this.rows();

		if (rowLen > 1) System.out.println(caption);
		else System.out.print(caption+" ");
		this.print();
	}

	/**
	 * Prints a title and then in a new line the array of values with a carriage return.
	 * @param caption The title above the matrix.
	 */
	public void println(String caption) {
		int rowLen = this.rows();

		if (rowLen > 1) System.out.println(caption);
		else System.out.print(caption+" ");
		this.println();
	}

	/**
	 * Private method that presents the matrix enclosed in brackets.
	 * Each line corresponds to a row of the array.
	 * The columns are separated by commas.
	 */
	public String toString() {
		if (this.isEmpty()) return "[]";
		int rowLen = this.rows();
		int colLen = this.cols();
		String toString = new String();
		for (int row = 0; row < rowLen; ++row) {
			toString += "[ ";
			for (int col = 0; col < colLen; ++col) {
				//toString += (this.complexMatrix[row][col].rep()+"").charAt(0) == '-' ? "" : " " ;
				toString += this.complexMatrix[row][col];
				toString += (col == colLen-1 ? " ]" : " , ");
			}
			toString += (row == rowLen-1 ? "" : "\n");
		}
		return toString;
	}

	/**
	 * Private method that presents the matrix enclosed in brackets.
	 * Each line corresponds to a row of the array.
	 * The columns are separated by commas.
	 */
	private void printCM() {
		System.out.print(this.toString());
	}

	/**
	 * Displays a title and selected column
	 * @param col The selected column
	 * @param caption The title
	 */
	public void println(int col, String caption) {
		int rowLen = this.rows();

		MatrixComplex cMatrix = new MatrixComplex(rowLen, 1);
		for (int row = 0; row < rowLen; ++row) cMatrix.complexMatrix[row][0] = this.complexMatrix[row][col];  

		cMatrix.println(caption);
	}

	/**
	 * Returns a string with the array expression in the format used by Maxima (Computer Algebra System)
	 * @return The string with the array in Maxima format.
	 */
	//matrix([-1.0 + 6.0*%i,2.0 - 8.0*%i,-1.0,1.0 - 4.0*%i],
	//		 [4.0 - 9.0*%i,8.0 - 4.0*%i,7.0 + 5.0*%i,-9.0 - 3.0*%i],
	//		 [5.0 + 7.0*%i,2.0 + 4.0*%i,-3.0 + 4.0*%i,6.0 + 6.0*%i],if (__DEBUG__) 
	//		 [-4.0 - 4.0*%i,6.0 - 5.0*%i,3.0 + 8.0*%i,-3.0 - 3.0*%i]);
	public String toMaxima() {
		int rowLen = this.rows();
		int colLen = this.cols();
		String matrixMaxima = new String();

		for (int row = 0; row < rowLen; ++row) {
			matrixMaxima += "[";
			for (int col = 0; col < colLen; ++col) {
				matrixMaxima += this.complexMatrix[row][col].toString();
				matrixMaxima += (col == colLen-1 ? "]" : ",");
			}
			matrixMaxima += (row == rowLen-1 ? ")" : ",");
		}
		matrixMaxima = matrixMaxima.replace("i", "*%i");
		matrixMaxima = "matrix(" + matrixMaxima;

		return matrixMaxima;
	}

	/**
	 * Returns a string with the array expression in the format used by Wolfram Mathematica.
	 * @return The string with the array in Wolfram Mathematica format.
	 */
	//{{-1.0 + 6.0i,2.0 - 8.0i,-1.0,1.0 - 4.0i},
	// {4.0 - 9.0i,8.0 - 4.0i,7.0 + 5.0i,-9.0 - 3.0i},
	// {5.0 + 7.0i,2.0 + 4.0i,-3.0 + 4.0i,6.0 + 6.0i},
	// {-4.0 - 4.0i,6.0 - 5.0i,3.0 + 8.0i,-3.0 - 3.0i}}
	public String toWolfram() {
		int rowLen = this.rows();
		int colLen = this.cols();
		String matrixWolfram = new String();
		matrixWolfram += "{";

		for (int row = 0; row < rowLen; ++row) {
			matrixWolfram += "{";
			for (int col = 0; col < colLen; ++col) {
				matrixWolfram += this.complexMatrix[row][col].toStringRecWolfram();
				matrixWolfram += (col == colLen-1 ? "}" : ",");
			}
			matrixWolfram += (row == rowLen-1 ? "}" : ",");
		}
		return matrixWolfram.replace("E", "*10^");
	}

	/**
	 * Returns a string with the array expression in the format used by Matlab.
	 * @return The string with the array in Matlab format.
	 */
	public String toMatlab() {
		int rowLen = this.rows();
		int colLen = this.cols();
		String matrixWolfram = new String();
		matrixWolfram += "[";

		for (int row = 0; row < rowLen; ++row) {
			for (int col = 0; col < colLen; ++col) {
				matrixWolfram += this.complexMatrix[row][col].toString();
				matrixWolfram += (col == colLen-1 ? "" : ",");
			}
			matrixWolfram += (row == rowLen-1 ? "]" : ";");
		}
		return matrixWolfram;
	}

	/**
	 * Returns a string with the array expression in the format used by GNU Octave.
	 * @return The string with the array in GNU Octave format.
	 */
	public String toOctave() {
		return toMatlab();
	}
	
	/**
	 * Prepares the matrix string for toMatrixComplex
	 * @return the matrix string
	 */
	public String preMatrixComplex() {
		int rowLen = this.rows();
		int colLen = this.cols();
		String preMatrixComplex = new String();
		int row, col;

		preMatrixComplex = "\"";
		for (row = 0; row < rowLen-1; ++row) {
			for (col = 0; col < colLen; ++col) {
				preMatrixComplex += this.complexMatrix[row][col].toString();
				preMatrixComplex += (col == colLen-1 ? ";" : ",");
			}
		}
		for (col = 0; col < colLen; ++col) {
			preMatrixComplex += this.complexMatrix[row][col].toString();
			preMatrixComplex += (col == colLen-1 ? "\"" : ",");
		}
		return preMatrixComplex;
	}
	
	/**
	 * Returns a string with the array expression in the format used by Matrix Complex. 
	 * @return The string with the array in MatrixComplex format.
	 */
	public String toMatrixComplex() {
		String toMatrixComplex;
		toMatrixComplex = "new MatrixComplex(" + preMatrixComplex() +");";
		return toMatrixComplex;
	}

	/*
	 * ***********************************************
	 * COLS & ROWS OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Looks for from the row "col" to the last row of the array the element that complies that the indicated column is different from zero to use it as an exchange row in the diagonalization of arrays.
	 * @param col Column to check.
	 * @return The index of the row found or -1 if it does not find it.
	 */
	public int locateSwapRowUp(int col) {
		int row;

		for (row = col; row < this.rows(); ++row)
			if (!this.complexMatrix[row][col].equals(0,0)) 
				break;
		return (row == this.rows()) ? -1 : row;
	}

	/**
	 * Looks for from the first row to the row "col-1" of the array that complies that the indicated column is different from zero to use it as an exchange row in the diagonalization of arrays.
	 * @param col Column to check.
	 * @return The index of the row found or -1 if it does not find it.
	 */
	public int locateSwapRowDown(int col) {
		int row;

		for (row = 0; row < col; ++row)
			if (!this.complexMatrix[row][col].equals(0,0)) 
				break;
		return row;
	}

	/**
	 * Returns the row from "rowIni" whose column from "rowIni" to the last one has the maximum value in module.
	 * @param rowIni Row and column from which the maximum search starts.
	 * @return The index of the row with the value in maximum nonzero module or -1 if the value was not found.
	 */
	public int partialPivot(int rowIni) {
		int rowLen = this.rows();
		int colP = rowIni;
		int rowMax = rowIni;
		Complex cMax = new Complex(this.complexMatrix[rowIni][rowIni].rep(), this.complexMatrix[rowIni][rowIni].imp());
		for (int row = rowIni; row < rowLen; ++row) {
			if (this.complexMatrix[row][colP].mod() > cMax.mod())  {
				cMax = this.complexMatrix[row][colP];
				rowMax = row;
			}
		}
		if (cMax.equals(0,0)) 
			return -1;
		return rowMax;
	}

	/**
	 * Swaps the rows "row1" and "row2" in the array and updates the sign variable to correctly evaluate the determinant.
	 * @param row1 Row to swap.
	 * @param row2 Row to swap.
	 */
	public void swapRows(int row1, int row2) {
		int colLen = this.cols();
		MatrixComplex pivot = new MatrixComplex(1, colLen);

		if (row1 == row2) return;
		pivot.complexMatrix[0] = this.complexMatrix[row1];
		this.complexMatrix[row1] = this.complexMatrix[row2];
		this.complexMatrix[row2] = pivot.complexMatrix[0];
		this.mSign = -this.mSign;
		//	System.out.println("swapRows this.mSign="+this.mSign);
	}

	/**
	 * Swaps the rows "row1" and "row2" in the array and updates the sign variable to correctly evaluate the determinant.
	 * Performs the swap by copying the values of the columns. Is the Long way to do it.
	 * @param row1 Row to swap.
	 * @param row2 Row to swap.
	 */
	public void swapRowsL(int row1, int row2) {
		Complex pivot ;

		if (row1 == row2) return;
		for (int col = 0; col < row1; ++col) {
			pivot = this.complexMatrix[row1][col];
			this.complexMatrix[row1][col] = this.complexMatrix[row2][col];
			this.complexMatrix[row2][col] = pivot;
		}
		this.mSign = -this.mSign;
	}
	
	/**
	 * Copies the value of the last column of the matrix "origMatrix" into the column indicated by "copyCol".
	 * @param origMatrix Array with values to be copied.
	 * @param copyCol Index of the column to place the copied values.
	 */
	private void copyCol(MatrixComplex origMatrix, int copyCol) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				this.complexMatrix[row][col] = col == copyCol ? origMatrix.complexMatrix[row][colLen].copy() : origMatrix.complexMatrix[row][col].copy();
			}
		}	
	}

	/**
	 * Copies the value of the "copyCol" column of "origMatrix" in the "destCol" column of this.
	 * @param destCol target copy column.
	 * @param origMatrix Matrix in which values are copied.
	 * @param copyCol index to the column to be copied.
	 */
	public void copyCol(int destCol, MatrixComplex origMatrix, int copyCol) {
		int rowLen = this.rows();
		int row;

		for (row = 0; row < rowLen; ++row) {
			this.complexMatrix[row][destCol] = origMatrix.complexMatrix[row][copyCol].copy();
		}
	}

	/**
	 * Copies the value of the "copyRow" column of "origMatrix" in the "destRow" column of this.
	 * @param destRow target copy row.
	 * @param origMatrix Matrix in which values are copied.
	 * @param copyRow index to the row to be copied.
	 */
	public void copyRow(int destRow, MatrixComplex origMatrix, int copyRow) {
		int colLen = this.cols();
		int col;

		for (col = 0; col < colLen; ++col) {
			this.complexMatrix[destRow][col] = origMatrix.complexMatrix[copyRow][col].copy();
		}
	}

	/**
	 * Compares two matrices and return the result of the comparingif (__DEBUG__) 
	 * @param cMatrix The matrix to compare with
	 * @return The result of the comparing
	 */
	public boolean equals(MatrixComplex cMatrix) {
		int rowLen = this.rows();
		int colLen = this.cols();
		
		if (rowLen != cMatrix.rows()) return false;
		if (colLen != cMatrix.cols()) return false;
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (!this.complexMatrix[row][col].equals(cMatrix.complexMatrix[row][col])) return false;
		return true;
	}
	
	/**
	 * 
	 * @param cMatrix					matrix.println("row "+row+" col "+col);
	 * @param numdecs
	 * @return
	 */
	public boolean equals(MatrixComplex cMatrix, int numdecs) {
		int rowLen = this.rows();
		int colLen = this.cols();
		
		if (rowLen != cMatrix.rows()) return false;
		if (colLen != cMatrix.cols()) return false;
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (!this.complexMatrix[row][col].equals(cMatrix.complexMatrix[row][col], numdecs)) return false;
		return true;
	}

	/**
	 * Appends new rows to the array (Only if the number of columns are the same in both arrays)
	 * @param newRows The new rows to append
	 */
	public void appendRows(MatrixComplex newRows) {
		int rows = this.rows() + newRows.rows();
		int row;
		
		if ( this.cols() != 0 && this.cols() != newRows.cols()) {
			return;
		}
		MatrixComplex newArray = new MatrixComplex(rows, this.cols());
		
		for (row = 0; row < this.rows(); ++row)
			newArray.setRow(row, this.getRow(row));
		for (int idx = 0; row < rows; ++row, ++idx)
			newArray.setRow(row, newRows.getRow(idx));
		this.complexMatrix = newArray.complexMatrix.clone();
	}
	
	/**
	 * Reorders the array by putting the last row in the first position and so on
	 */
	public void transrow() {
		MatrixComplex transrow = new MatrixComplex(this.rows(), this.cols());
		
		for (int row1 = 0, row2 = this.rows()-1; row1 < this.rows(); ++row1, --row2)
			transrow.setRow(row1, this.getRow(row2));
		
		this.complexMatrix = transrow.complexMatrix.clone();
	}
	
	/*
	 * ***********************************************
	 * ARITHMETIC OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Calculates the sum of two matrices.
	 * @param cMatrix the array to add.
	 * @return The matrix result from the sum.
	 */
	public MatrixComplex plus(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();		
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (rowLenA1 != rowLenA2 || colLenA1 != colLenA2) {
			System.err.println("Not valid sum: The rows/cols of matrix1 has to be equal to the rows/cols of matrix2.");
			System.exit(1);
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLenA1, colLenA2);

		for (int row = 0; row < rowLenA1; ++row) {
			for (int col = 0; col < colLenA1; ++col) {
				resultMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].plus(cMatrix.complexMatrix[row][col]);
			}
		}
		return resultMatrix;
	}

	/**
	 * THIS SUM MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE plusMat FOR A DIFFERENT APPROXIMATION OF THE ADITION BETWEEN A MATRIX AND A SCALAR.
	 * @param val
	 */
	public MatrixComplex plus(double val) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(val));
		return newThis;
	}

	/**
	 * THIS SUM MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE plusMat FOR A DIFFERENT APPROXIMATION OF THE ADITION BETWEEN A MATRIX AND A SCALAR.
	 * @param cVal
	 */
	public MatrixComplex plus(Complex cVal) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(cVal));
		return newThis;
	}

	/**
	 * This method returns the sum for this plus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(Complex cNum) {
		if (!this.isSquare()) {
			System.err.println("Not valid sum: This sum is only for square matrices.");
		}
		
		MatrixComplex cNumMat = new MatrixComplex(this.rows(), this.cols());
		cNumMat.initMatrixDiag(cNum);
		
		return this.plus(cNumMat);
	}
	
	/**
	 * This method returns the sum for this plus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number in String format to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(String strcNum) {
		Complex cNum = new Complex(strcNum);
		return plusMat(cNum);
	}

	/**
	 * This method returns the sum for this plus cNum*I, where I is the identity matrix.
	 * @param rep the real part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @param imp the imaginary part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(double rep, double imp) {
		Complex cNum = new Complex(rep, imp);
		return plusMat(cNum);
	}
	
	/**
	 * Calculates the difference of two matrices.
	 * @param cMatrix the subtracting matrix.
	 * @return the matrix result of the difference.
	 */
	public MatrixComplex minus(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();		
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (rowLenA1 != rowLenA2 || colLenA1 != colLenA2) {
			System.err.println("Not valid substraction: The rows/cols of matrix1 has to be equal to the rows/cols of matrix2.");
			System.exit(1);
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLenA1, colLenA2);

		for (int row = 0; row < rowLenA1; ++row) {
			for (int col = 0; col < colLenA1; ++col) {
				resultMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].minus(cMatrix.complexMatrix[row][col]);
			}
		}
		return resultMatrix;
	}

	/**
	 * THIS SUBTRACTION MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE MINUSMat FOR A DIFFERENT APPROXIMATION OF THE SUBTRACTION BETWEEN A MATRIX AND A SCALAR.
	 * @param val
	 */
	public MatrixComplex minus(double val) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(val));
		return newThis;
	}

	/**
	 * THIS SUBTRACTION MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE MINUSMat FOR A DIFFERENT APPROXIMATION OF THE SUBTRACTION BETWEEN A MATRIX AND A SCALAR.
	 * @param cVal
	 */
	public MatrixComplex minus(Complex cVal) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(cVal));
		return newThis;
	}

	/**
	 * This method returns the difference for this minus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(Complex cNum) {
		if (!this.isSquare()) {
			System.err.println("Not valid sum: This sum is only for square matrices.");
		}
		
		MatrixComplex cNumMat = new MatrixComplex(this.rows(), this.cols());
		cNumMat.initMatrixDiag(cNum);
		
		return this.minus(cNumMat);
	}
	
	/**
	 * This method returns the difference for this minus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number in String format to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(String strcNum) {
		Complex cNum = new Complex(strcNum);
		return minusMat(cNum);
	}

	/**
	 * This method returns the difference for this minus cNum*I, where I is the identity matrix.
	 * @param rep the real part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @param imp the imaginary part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(double rep, double imp) {
		Complex cNum = new Complex(rep, imp);
		return minusMat(cNum);
	}

	/**
	 * Calculates the matrix product by a real scalar.
	 * @param num Real number.
	 * @return The result matrix of the product by the scalar.
	 */
	public MatrixComplex times(double num) {
		Complex cNum = new Complex(num, 0);
		return this.times(cNum);
	}

	/**
	 * Calculates the product of the matrix by a scalar complex
	 * @param cNum Complex number
	 * @return The result matrix of the product by the scalar
	 */
	public MatrixComplex times(Complex cNum) {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex resultMatrix = new MatrixComplex(rowLen, colLen);

		for (int rowf = 0; rowf < rowLen; ++rowf)
			for (int colf = 0; colf < colLen; ++colf)
				resultMatrix.complexMatrix[rowf][colf] = this.complexMatrix[rowf][colf].times(cNum);
		return resultMatrix;    	
	}

	/**
	 * Calculates the product of two matrices.
	 * @param cMatrix The multiplier matrix.
	 * @return The matrix resulting from the matrices product.
	 */
	public MatrixComplex times(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (colLenA1 != rowLenA2) {
			System.err.println("Not valid product: The cols of matrix1 has to be equal to the rows of matrix2.");
			//System.exit(1);
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLenA1, colLenA2);

		for (int rowf = 0; rowf < rowLenA1; ++rowf)
			for (int colf = 0; colf < colLenA2; ++colf)
				for (int iter = 0; iter < colLenA1; ++iter)
					resultMatrix.complexMatrix[rowf][colf] = resultMatrix.complexMatrix[rowf][colf].plus(this.complexMatrix[rowf][iter].times(cMatrix.complexMatrix[iter][colf]));
		return resultMatrix;
	}

	/**
	 * Calculates the matrix division by a real scalar.
	 * @param num Real number.
	 * @return The matrix resulting from the division by the scalar.
	 */
	public MatrixComplex divides(double num) {
		Complex cNum = new Complex(num, 0);
		return this.divides(cNum);
	}

	/**
	 * Calculates the division of the matrix by a complex scalar.
	 * @param cNum The complex number.
	 * @return The result matrix by the scalar division.
	 */
	public MatrixComplex divides(Complex cNum) {
		return this.times(cNum.reciprocal());
	}

	/**
	 * Calculates the division between two matrices as the product of the dividend by the inverse of the divisor matrix.
	 * @param cMatrix the divisor matrix.
	 * @return The matrix resulting from the division.
	 */
	public MatrixComplex divides(MatrixComplex cMatrix) {
		//return this.times(cMatrix.inverse());
		return this.dividesright(cMatrix);
	}

	/**
	 * Calculates the left division of two arrays as this⁻¹*cMatrix
	 * @param cMatrix The matrix to divide
	 * @return The left division
	 */
	public MatrixComplex dividesleft(MatrixComplex cMatrix) {
		if (this.rows() == 1 && this.cols() == 1) return cMatrix.inverse().times(this.getItem(0, 0));
		if (cMatrix.rows() == 1 && cMatrix.cols() == 1) return this.divides(cMatrix.getItem(0, 0));
		return this.inverse().times(cMatrix);
	}

	/**
	 * Calculates the right division of two arrays as this*cMatrix⁻¹
	 * @param cMatrix The matrix used as divisor
	 * @return The right division
	 */
	public MatrixComplex dividesright(MatrixComplex cMatrix) {
		if (this.rows() == 1 && this.cols() == 1) return cMatrix.inverse().times(this.getItem(0, 0));
		if (cMatrix.rows() == 1 && cMatrix.cols() == 1) return this.divides(cMatrix.getItem(0, 0));
		return this.times(cMatrix.inverse());
	}

	/**
	 * Calculates the power of a Matrix, power can be positive or negative
	 * @param power The power at which the matrix is ​​raised. Only integers are allowed
	 * @return The matrix raised to power
	 */
	public MatrixComplex power(int power) {
		boolean inverse = false;
		
		// Take advantage from diagonal matrices
		if (this.isDiagonal()) {
			MatrixComplex powerMat = this.copy();
			for (int i = 0; i < this.rows(); ++i)
				powerMat.setItem(i, i, this.getItem(i, i).power(power));
			return powerMat;
		}
		
		MatrixComplex powerMatrix = new MatrixComplex(this.rows(), this.cols());
		powerMatrix.initMatrixDiag(1, 0);
		if (power == 0) return powerMatrix;

		if (power < 0) {
			inverse = true;
			power = -power;
		}
		
		double cNorma = this.euc_norm();
		MatrixComplex thisNorma = this.divides(cNorma);
		
		for (int i = 1; i <= power; ++i)
			powerMatrix = powerMatrix.times(thisNorma);
		powerMatrix = powerMatrix.times(Math.pow(cNorma,power));

		if (inverse)
			return powerMatrix.inverse();
		else
			return powerMatrix;
	}

	/**
	 * 
	 * @param cExpo
	 * @return
	 */
	public MatrixComplex power(Complex cExpo) {
		// Take advantage from diagonal matrices
		if (this.isDiagonal()) {
			MatrixComplex powerMat = this.copy();
			for (int i = 0; i < this.rows(); ++i)
				powerMat.setItem(i, i, this.getItem(i, i).power(cExpo));
			return powerMat;
		}
		
		if (cExpo.isPureReal()) {
			double dExpo = cExpo.rep();
			return this.power(dExpo);
		}
		
		return MatrixComplex.exp((this.log()).times(cExpo));
	}

	/**
	 * 
	 * @param dExpo
	 * @return
	 */
	public MatrixComplex power(double dExpo) {
		// Take advantage from diagonal matrices
		if (this.isDiagonal()) {
			MatrixComplex powerMat = this.copy();
			
			for (int i = 0; i < this.rows(); ++i)
				powerMat.setItem(i, i, this.getItem(i, i).power(dExpo));
			return powerMat;
		}
		
		if(Math.ceil(dExpo) == Math.floor(dExpo)) {
			int iExpo = (int)Math.floor(dExpo);
			return this.power(iExpo);
		}
		
		return MatrixComplex.exp((this.log()).times(dExpo));
	}

	/**
	 * Complex Matrix raised to a Complex Matrix 
	 * @param mcExpo
	 * @return
	 */
	public MatrixComplex power(MatrixComplex mcExpo) {
		if (this.dim() != mcExpo.dim() || !this.isSquare() || !mcExpo.isSquare()) {
			System.err.println("Not valid matrices: Both matrices has to be square and of the same dimension.");
			System.exit(1);
		}

		return MatrixComplex.exp((this.log()).times(mcExpo));
	}
	
	/*
	 * ***********************************************
	 * EXPANSIONS TAYLOR 
	 * ***********************************************
	 */

	/**
	 * Normalize the matrix to 0 < matrix <= 2pi
	 * @return the matrix normalized between 0 < matrix <= 2pi
	 */
	public MatrixComplex normalize2PI() {
		double norm = this.euc_norm();
		double redux = norm/Complex.DOS_PI; 
		redux = (redux-Math.floor(redux))*Complex.DOS_PI;
		MatrixComplex normalThis = this.divides(norm).times(redux);
		return normalThis;
	}
	
	/**
	 * Calculates the exponential of the matrix (e^this)
	 * This calculation is achieved using the Taylor's series of the exponential extended for complex matrices
	 * @return The value of e^this
	 */
	public MatrixComplex exp() {
		if (__DEBUG__) System.out.println("------------ exp() ------------ ");
		if (this.isNaN() || this.isNull() || this.isInfinite() ) return this;
		if (this.rows() != this.cols()) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}

		MatrixComplex expMatrix = new MatrixComplex(this.rows(), this.cols());
		MatrixComplex expMatant;
		MatrixComplex powMatrix = new MatrixComplex(this.rows(), this.cols());
		MatrixComplex errMatrix;
		
		int cNorma = (int)Math.ceil(this.euc_norm())*10;
		MatrixComplex thisNorma = this.divides(cNorma);

		expMatrix.initMatrixDiag(1, 0);
		powMatrix.initMatrixDiag(1, 0);
		
		// precision can be changed with Complex.digits(long_value)
		long maxIter = Complex.digits();
		long k = 1;
		double fact = 1;
		do {
			expMatant = expMatrix.copy();
			powMatrix = powMatrix.times(thisNorma);
			fact *= k++;
			expMatrix = expMatrix.plus(powMatrix.divides(fact));
			errMatrix = expMatant.minus(expMatrix);
			errMatrix.abs();
			// errMatrix.println("public MatrixComplex exp() - errmatrix:");
			if (errMatrix.isNaN()) break;
			if (errMatrix.isNullC()) break;
		} while(k < maxIter);
		
		if (__DEBUG__) {
			System.out.println("Iterations to converge:" + k);
		}

		return expMatrix.power(cNorma);
	}

	/**
	 * Calculates the exp of the matrix exp(matrix)
	 * @param matrix
	 * @return The exp of matrix
	 */
	public static MatrixComplex exp(MatrixComplex matrix) {
		return matrix.exp();
	}

	/**
	 * The "SIN" or "COS" depending on the sign. One method to rule them all
	 * @param sign 1 for "SIN"; -1 for "COS"
	 * @return The "SIN" or "COS" depending on the sign	 
	 */
	private MatrixComplex trigonTaylor(int sign) {
		if (this.rows() != this.cols()) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}

		MatrixComplex normalThis = this.normalize2PI();
		MatrixComplex trigonMatrix = new MatrixComplex(this.rows(), this.cols());
		MatrixComplex trigonMatant;
		MatrixComplex powMatrix = new MatrixComplex(this.rows(), this.cols());
		MatrixComplex errMatrix;
		
		boolean SIN = sign == 1 ? true : false;
		boolean COS = !SIN;
		
		trigonMatrix.initMatrixDiag(SIN ? 0 : 1, 0);
		powMatrix.initMatrixDiag(1, 0);
		
		int k = 1;
		double fact = 1;
		do {
			fact *= k;
			if (SIN && k%2 == 0) {
					powMatrix = powMatrix.times(normalThis);
					continue;
			}
			if (COS && k%2 != 0) {
					powMatrix = powMatrix.times(normalThis);
					continue;
			}
			trigonMatant = trigonMatrix.copy();
			powMatrix = powMatrix.times(normalThis);
			trigonMatrix = trigonMatrix.plus(powMatrix.divides(fact).times(sign));
			sign *= -1;
			errMatrix = trigonMatant.minus(trigonMatrix);
			errMatrix.abs();
			if (errMatrix.isNullC()) break;
		} while(++k < 1000);
		return trigonMatrix;
	}

	/**
	 * Calculates the sin of the matrix
	 * This calculation is achieved using the Taylor's series of the sin extended for complex matrices
	 * @return The value of sinTaylor()
	 */
	public MatrixComplex sinTaylor() {
		return trigonTaylor(1);
	}

	/**
	 * Calculates the sin of the matrix sinEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of sinEuler()
	 */
	public MatrixComplex sinEuler() {
		Complex plusj = new Complex(0,1);
		Complex minusj = new Complex(0,-1);
			
		MatrixComplex normalThis = this.normalize2PI();
		return (normalThis.times(plusj).exp().minus(normalThis.times(minusj).exp())).divides(Complex.i).divides(2);
	}

	/**
	 * Calculates the sin of the matrix sin()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of sin()
	 */	
	public MatrixComplex sin() {
		return this.sinTaylor();
	}

	/**
	 * Calculates the sin of the matrix sin(matrix)
	 * @param matrix
	 * @return The sin of matrix
	 */
	public static MatrixComplex sin(MatrixComplex matrix) {
		return matrix.sin();
	}
	
	/**
	 * Calculates the cos of the matrix cosTaylor()
	 * This calculation is achieved using the Taylor's series of the cos extended for complex matrices
	 * @return The value of cosTaylor()
	 */
	public MatrixComplex cosTaylor() {
		return trigonTaylor(-1);
	}

	/**
	 * Calculates the sin of the matrix cosEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of cosEuler()
	 */
	public MatrixComplex cosEuler() {
		Complex plusj = new Complex(0,1);
		Complex minusj = new Complex(0,-1);

		MatrixComplex normalThis = this.normalize2PI();
		return (normalThis.times(plusj).exp().plus(normalThis.times(minusj).exp())).divides(2);
	}

	/**
	 * Calculates the sin of the matrix cos()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of cos()
	 */	
	public MatrixComplex cos() {
		return this.cosTaylor();
	}

	/**
	 * Calculates the cos of the matrix cos(matrix)
	 * @param matrix
	 * @return The cos of matrix
	 */
	public static MatrixComplex cos(MatrixComplex matrix) {
		return matrix.cos();
	}
	
	/**
	 * Calculates the tan of the matrix tanTaylor()
	 * The tangent is calculated as sinTaylor()/cosTaylor()
	 * @return The value of tanTaylor()
	 */
	public MatrixComplex tanTaylor() {	
		return this.sinTaylor().divides(this.cosTaylor());
	}
	
	/**
	 * Calculates the tan of the matrix tanEuler()
	 * The tangent is calculated as sinEuler()/cosEuler()
	 * @return The value of tanEuler()
	 */
	public MatrixComplex tanEuler() {	
		return this.sinEuler().divides(this.cosEuler());
	}

	/**
	 * Calculates the sin of the matrix tan()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of tan()
	 */	
	public MatrixComplex tan() {	
		return this.tanEuler();
	}

	/**
	 * Calculates the tan of the matrix tan(matrix)
	 * @param matrix
	 * @return The tan of matrix
	 */
	public static MatrixComplex tan(MatrixComplex matrix) {
		return matrix.tan();
	}
	
	/**
	 * Euler's formula eⁱˣ=cos(x)+i⋅sin(x)
	 * @return Euler's formula eⁱˣ
	 */
	public MatrixComplex euler() {	
		MatrixComplex normalThis = this.normalize2PI();
		return exp(normalThis.times(Complex.i));
	}

	/**
	 * Euler's formula eⁱˣ 
	 * @param matrix
	 * @return Euler's formula eⁱˣ
	 */
	public static MatrixComplex euler(MatrixComplex matrix) {
		MatrixComplex normalThis = matrix.normalize2PI();
		return exp(normalThis.times(Complex.i));
	}

	/**
	 * Calculates the hyperbolic sin of the matrix sinhTaylor(this)
	 * This calculation is achieved using the Taylor's series of the hyperbolic sin extended for complex matrices
	 * @return The value of sinh()
	 */
	public MatrixComplex sinhTaylor() {
		if (this.rows() != this.cols()) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}

		MatrixComplex sinMatrix = new MatrixComplex(this.rows(), this.cols());
		MatrixComplex sinMatant;
		MatrixComplex powMatrix = new MatrixComplex(this.rows(), this.cols());
		MatrixComplex errMatrix;
		
		sinMatrix.initMatrixDiag(0, 0);
		powMatrix.initMatrixDiag(1, 0);
		int k = 1;
		double fact = 1;
		do {
			fact *= k;
			if (k%2 == 0) {
				++k;
				powMatrix = powMatrix.times(this);
				continue;
			}
			sinMatant = sinMatrix.copy();
			powMatrix = powMatrix.times(this);
			sinMatrix = sinMatrix.plus(powMatrix.divides(fact));
			errMatrix = sinMatant.minus(sinMatrix);
			errMatrix.abs();
			if (errMatrix.isNullC()) break;
			++k;
		} while(k < 1000);
		return sinMatrix;
	}

	/**
	 * Calculates the hyperbolic sin of the matrix sinhEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of sinhEuler()
	 */
	public MatrixComplex sinhEuler() {
		return this.exp().minus(this.opposite().exp()).divides(2);
	}

	/**
	 * Calculates the sin of the matrix sinh()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of sinh()
	 */	
	public MatrixComplex sinh() {
		return this.sinhTaylor();
	}

	/**
	 * Calculates the sinh of the matrix sinh(matrix)
	 * @param matrix
	 * @return The sinh of matrix
	 */
	public static MatrixComplex sinh(MatrixComplex matrix) {
		return matrix.sinh();
	}
	
	/**
	 * Calculates the hyperbolic cos of the matrix coshTaylor()
	 * This calculation is achieved using the Taylor's series of the cos extended for complex matrices
	 * @return The value of coshTaylor()
	 */
	public MatrixComplex coshTaylor() {
		if (this.rows() != this.cols()) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}

		MatrixComplex cosMatrix = new MatrixComplex(this.rows(), this.cols());
		MatrixComplex cosMatant;
		MatrixComplex powMatrix = new MatrixComplex(this.rows(), this.cols());
		MatrixComplex errMatrix;
		
		cosMatrix.initMatrixDiag(1, 0);
		powMatrix.initMatrixDiag(1, 0);
		int k = 1;
		double fact = 1;
		do {
			fact *= k;
			if (k%2 != 0) {
				++k;
				powMatrix = powMatrix.times(this);
				continue;
			}
			cosMatant = cosMatrix.copy();
			powMatrix = powMatrix.times(this);
			cosMatrix = cosMatrix.plus(powMatrix.divides(fact));
			errMatrix = cosMatant.minus(cosMatrix);
			errMatrix.abs();
			if (errMatrix.isNullC()) break;
			++k;
		} while(k < 1000);
		return cosMatrix;
	}

	/**
	 * Calculates the hyperbolic sin of the matrix coshEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of coshEuler()
	 */
	public MatrixComplex coshEuler() {
		return this.exp().plus(this.opposite().exp()).divides(2);
	}

	/**
	 * Calculates the sin of the matrix cosh()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of cosh()
	 */	
	public MatrixComplex cosh() {
		return this.coshTaylor();
	}

	/**
	 * Calculates the cosh of the matrix cosh(matrix)
	 * @param matrix
	 * @return The cosh of matrix
	 */
	public static MatrixComplex cosh(MatrixComplex matrix) {
		return matrix.cosh();
	}

	/**
	 * Calculates the hyperbolic tan of the matrix tanhTaylor()
	 * This calculation uses the Taylor's series of the sin and cos extended for complex matrices
	 * The hyperbolic tangent is calculated as sinhTaylor()/coshTaylor()
	 * @return The value of tanhTaylor()
	 */
	public MatrixComplex tanhTaylor() {
		return this.sinhTaylor().divides(this.coshTaylor());
	}

	/**
	 * Calculates the hyperbolic tan of the matrix tanhEuler()
	 * This calculation uses the Euler's formulas of the sin and cos extended for complex matrices
	 * The hyperbolic tangent is calculated as sinhEuler()/coshEuler()
	 * @return The value of tanhEuler()
	 */
	public MatrixComplex tanhEuler() {
		return this.sinhEuler().divides(this.coshEuler());
	}
	
	/**
	 * Calculates the tan of the matrix tanh()
	 * The tangent is calculated as tanh preferred method
	 * @return The value of tanh()
	 */
	public MatrixComplex tanh() {
		return this.tanhTaylor();
	}

	/**
	 * Calculates the tanh of the matrix tanh(matrix)
	 * @param matrix
	 * @return The tanh of matrix
	 */
	public static MatrixComplex tanh(MatrixComplex matrix) {
		return matrix.tanh();
	}

	/**
	 * Calculates the logarithm of a Matrix using Taylor's Extension summation log(1 - x)
	 * @return The logarithm of a Matrix using Taylor's Extension 
	 * https://es.wikipedia.org/wiki/Logaritmo_de_una_matriz
	 */
	public MatrixComplex logTaylor() {
		if (__DEBUG__) System.out.println("------------ logtaylor() ------------ ");
		if (this.isNaN() || this.isNull() || this.isInfinite() ) return this;
		if (!this.isSquare()) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}

		MatrixComplex yMatrix = this.copy();

		/* ***************************************
		 * 1st Transformation
		 * Applying a reduction to reduce the 
		 * overflow error risk 
		 * ***************************************/		
		int m = 0;
		/**/
		while (yMatrix.euc_norm() > .01) {
			++m;
			yMatrix = yMatrix.divides(Math.E);
		}

		if (__DEBUG__) {
			yMatrix.println("1st Transformation : Taylor's Extension log(1 - x) - yMatrix reduced "+ m +" times:");
			System.out.println("yMatrix.euc_norm(): " +yMatrix.euc_norm());
		}
		/**/

		/* *************************************************
		 * Taylor's Extension log(1 - x)
		 * 2nd Transformation log(1 - x)
		 * (1 - x) = y -> x = 1 - y
		 * -1 <= x < 1 -> -1 <= 1 - y < 1 -> -1 < y - 1 <= 1 -> 0 < y <= 2
		 * 
		 * (I - thisMatrix) = this -> thisMatrix = I - this
		 * -I <= this < I -> 0 <= thisMatrix < 2I 
		 * *************************************************/
		MatrixComplex thisMatrix = yMatrix.minusMat(1,0).opposite();

		if (__DEBUG__) {
			thisMatrix.println("2nd Transformation : thisMatrix:");
			System.out.println("thisMatrix.euc_norm(): " +thisMatrix.euc_norm());
		}
		
		/* ***************************************
		 * 3rd Transformation
		 * Applying a reduction to bring the matrix 
		 * to the region of convergence. NOT REALLY 
		 * ***************************************/		
		/** /
		int n = 0;
		while (thisMatrix.euc_norm() > .01) {
			++n;
			thisMatrix = thisMatrix.divides(Math.E);
		}
		if (__DEBUG__) thisMatrix.println("Taylor's Extension log(1 - x) - thisMatrix reduced "+ n +" times:");
		thisMatrix.println("3rd thisMatrix reduced "+ n +" times:");
		/**/

		// These are the values for the 1st item of the summation
		// In this case we use the summation opposite. At the end we need to return the opposite
		MatrixComplex logMatrix = thisMatrix.copy();
		MatrixComplex powMatrix = thisMatrix.copy();
		MatrixComplex errMatrix;
		MatrixComplex errAntMat = new MatrixComplex(this.rows(), this.cols());
		MatrixComplex logMatant;
		
		// precision can be changed with Complex.digits(long_value)
		long maxIter = Complex.digits();
		long k = 2;
		
		// Variables to use at check convergence section
		int maxPoints = 99999;
		double[][] dataTable = new double[maxPoints][2];
		int c = 0;
		double accumulator = 0.0;
		double deviation;
		
		do {
			logMatant = logMatrix.copy();
			powMatrix = powMatrix.times(thisMatrix);
			logMatrix = logMatrix.plus(powMatrix.divides(k));
			errMatrix = logMatant.minus(logMatrix);
			errMatrix.abs();
			if (errMatrix.isNullC()) break;

			/*
			 * Check convergence section
			 */
			if ( k > 100 ) {
				deviation = errMatrix.norm()/errAntMat.norm();
				accumulator += deviation > 1 ? deviation: 0.0;
				if (c < maxPoints) {
					dataTable[c][0] = k;
					dataTable[c++][1] = deviation;
				}
				if (accumulator > 500) {
					if (__DEBUG__) {
						System.out.println("Iteration:"+ k +" - The logarithm is divergent");
						System.out.println("accumulator:" + accumulator);
						doPlot("-- DEVIATION --", dataTable);
					}
					return this.divides(Complex.ZERO);
				}
			}
			errAntMat = errMatrix.copy();
			/*
			 * End of  Check convergence section
			 */
		} while(k++ < maxIter);
		if (__DEBUG__) {
			System.out.println("Iterations to converge:" + k);
			System.out.println("accumulator:" + accumulator);
			doPlot("-- DEVIATION --", dataTable);
		}
		return logMatrix.opposite().plusMat(m,0);
	}
	
	/** 
	 * Calculates the logarithm of a Matrix using Mercator's Extension summation log(1 + x)
	 * @return The logarithm of a Matrix using Mercator's Extension 
	 */
	public MatrixComplex logMercator() {
		if (__DEBUG__) System.out.println("------------ logMercator() ------------ ");
		if (this.isNaN() || this.isNull() || this.isInfinite() ) return this;
		if (!this.isSquare()) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}

		MatrixComplex yMatrix = this.copy();
		if (__DEBUG__) yMatrix.determinant().println("[log()] - Determinant:");

		/* ***************************************
		 * 1st Transformation
		 * Applying a reduction to reduce the 
		 * overflow error risk 
		 * ***************************************/		
		int m = 0;
		while (yMatrix.euc_norm() > .01) {
			++m;
			yMatrix = yMatrix.divides(Math.E);
		}

		if (__DEBUG__) {
			yMatrix.println("1st Transformation : Mercator's Extension log(1 + x) - yMatrix reduced "+ m +" times:");
			System.out.println("yMatrix.euc_norm(): " +yMatrix.euc_norm());
		}

		/* *************************************************
		 * Mercator's Extension log(1 + x)
		 * 2nd Transformation log(1 + x)
		 * (1 + x) = y -> x = y - 1
		 * -1 < x <= 1 -> 0 < y <= 2
		 * 
		 * (I + thisMatrix) = this -> thisMatrix = this - I
		 * -I < this <= I -> 0 < thisMatrix <= 2I
		 * *************************************************/
		MatrixComplex thisMatrix = yMatrix.minusMat(1,0);
	
		if (__DEBUG__) {
			thisMatrix.println("2nd Transformation : thisMatrix:");
			System.out.println("thisMatrix.euc_norm(): " +thisMatrix.euc_norm());
		}

		// These are the values for the 1st item of the summation
		MatrixComplex logMatrix = thisMatrix.copy();
		MatrixComplex powMatrix = thisMatrix.copy();
		MatrixComplex errMatrix;
		MatrixComplex errAntMat = new MatrixComplex(this.rows(), this.cols());
		MatrixComplex logMatant;

		// precision can be changed with Complex.digits(long_value)
		long maxIter = Complex.digits();
		long k = 2;

		// Variables to use at check convergence section
		int maxPoints = 99999;
		double[][] dataTable = new double[maxPoints][2];
		int c = 0;
		double accumulator = 0.0;
		double deviation;
		
		do {
			logMatant = logMatrix.copy();
			powMatrix = powMatrix.times(thisMatrix);
			logMatrix = logMatrix.plus(powMatrix.divides(k*(k%2 == 0 ? -1 : 1)));
			errMatrix = logMatant.minus(logMatrix);
			errMatrix.abs();
			if (errMatrix.isNullC()) break;

			/*
			 * Check convergence section
			 */
			if ( k > 100 ) {
				deviation = errMatrix.norm()/errAntMat.norm();
				accumulator += deviation > 1 ? deviation: 0.0;
				if ( c < maxPoints ) {
					dataTable[c][0] = k;
					dataTable[c++][1] = deviation;
				}
				if (accumulator > 500) {
					if (__DEBUG__) {
						System.out.println("Iteration:"+ k +" - The logarithm is divergent");
						System.out.println("accumulator:" + accumulator);
						doPlot("-- DEVIATION --", dataTable);
					}
					return this.divides(Complex.ZERO);
				}
			}
			errAntMat = errMatrix.copy();
			/*
			 * End of Check convergence section
			 */			
		} while(k++ < maxIter);		
		if (__DEBUG__) {
			System.out.println("Iterations to converge:" + k);
			System.out.println("accumulator:" + accumulator);
			doPlot("-- DEVIATION --", dataTable);
		}
		return logMatrix.plusMat(m,0);
	}

	/**
	 * Calculates the logarithm of a Matrix using Hyperbolic Arc Tangent's Extension summation.
	 * Not recommended to use
	 * @return The logarithm of a Matrix using Hyperbolic Arc Tangent's Extension 
	 */
	public MatrixComplex logHat() {
		if (__DEBUG__) System.out.println("------------ loghat() ------------ ");
		if (this.isNaN() || this.isNull() || this.isInfinite() ) return this;
		if (this.rows() != this.cols()) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}
		
		MatrixComplex terMat = (this.power(2).minusMat(1,0)).divides(this.power(2).plusMat(1,0));
		MatrixComplex powMat = terMat.copy();
		MatrixComplex sumMat = powMat.copy();
		MatrixComplex sumAnt;
		MatrixComplex errMat;
		MatrixComplex errAnt = new MatrixComplex(this.rows(), this.cols());
		
		// The term for k = 0 is already calculated at the variables definition 
		long maxIter = Complex.digits();
		long k = 1;

		// Variables to check convergence section
		int maxPoints = 99999;
		double[][] dataTable = new double[maxPoints][2];
		int c = 0;
		double accumulator = 0.0;
		double deviation;
		
		do {
			sumAnt = sumMat.copy();
			powMat = powMat.times(terMat).times(terMat);
			sumMat = sumMat.plus(powMat.divides(2*k+1));
			errMat = sumAnt.minus(sumMat);
			errMat.abs();
			if (errMat.isNullC()) break;

			// Check convergence section
			if ( k > 100 ) {
				deviation = errMat.norm()/errAnt.norm();
				accumulator += deviation > 1 ? deviation: 0.0;
				if ( c < maxPoints ) {
					dataTable[c][0] = k;
					dataTable[c++][1] = deviation;
				}
				if (accumulator > 500) {
					if (__DEBUG__) {
						System.out.println("Iteration:"+ k +" - The logarithm is divergent");
						System.out.println("accumulator:" + accumulator);
						doPlot("-- DEVIATION --", dataTable);
					}
					return this.divides(Complex.ZERO);
				}
			}
			
			errAnt = errMat.copy();
		} while(k++ < maxIter);
		if (__DEBUG__) {
			System.out.println("Iterations to converge:" + k);
			System.out.println("accumulator:" + accumulator);
			doPlot("-- DEVIATION --", dataTable);
		}
		return sumMat;
	}
	
	/**
	 * Shortcut to the preferred natural logarithm expansion
	 * @return the natural logarithm of the matrix
	 */
	public MatrixComplex log() {
		return logTaylor();
	}

	/**
	 * Calculates the natural log of the matrix log(matrix)
	 * @param matrix
	 * @return The log of matrix
	 */
	public static MatrixComplex log(MatrixComplex matrix) {
		return matrix.log();
	}
	
	/**
	 * Calculates the natural log in base 10 of the matrix log10(matrix)
	 * @param matrix
	 * @return
	 */
	public MatrixComplex log10() {
		return this.log().divides(Math.log(10));
	}

	/**
	 * Calculates the natural log in base 10 of the matrix log10(matrix)
	 * @param matrix
	 * @return
	 */
	public static MatrixComplex log10(MatrixComplex matrix) {
		return log(matrix).divides(Math.log(10));
	}

	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public MatrixComplex logbase(Complex base) {
		return this.log().divides(Complex.log(base));
	}

	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public static MatrixComplex logbase(MatrixComplex matrix, Complex base) {
		return log(matrix).divides(Complex.log(base));
	}

	/**
	 * Calculates the natural log in Real base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public MatrixComplex logbase(double base) {
		return log().divides(Math.log(base));
	}

	/**
	 * Calculates the natural log in Real base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public static MatrixComplex logbase(MatrixComplex matrix, double base) {
		return log(matrix).divides(Math.log(base));
	}

	/**
	 * Calculates the power of a complex number raised a complex matrix
	 * @param cBase
	 * @param matrix
	 * @return
	 */
	public static MatrixComplex power(Complex cBase, MatrixComplex exponent) {
		return exp(exponent.times(Complex.log(cBase)));
	}

	/**
	 * Calculates the power of a real number raised a complex matrix
	 * @param base
	 * @param matrix
	 * @return
	 */
	public static MatrixComplex power(double base, MatrixComplex exponent) {
		Complex cBase = new Complex(base);
		return power(cBase, exponent);
	}

	/**
	 * Complex Matrix raised to a Complex Matrix 
	 * @param mcBase
	 * @param mcExpo
	 * @return
	 */
	public static MatrixComplex power(MatrixComplex mcBase, MatrixComplex mcExpo) {
		return mcBase.power(mcExpo);
	}
	
	/*
	 * ***********************************************
	 * ORDER RELATIONSHIPS IN MATRICES
	 * ***********************************************
	 */
	
	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean sameDimension(MatrixComplex matrix) {
		if (this.rows() != matrix.rows() || this.cols() != matrix.cols()) return false;
		return true;
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isGT(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}
		//return this.totalize().cre() > matrix.totalize().cre();
		return this.norm() > matrix.norm();
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isGTE(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}	
		//return this.totalize().cre() >= matrix.totalize().cre();
		return this.norm() >= matrix.norm();

	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isLT(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}
		// return this.totalize().cre() < matrix.totalize().cre();
		return this.norm() < matrix.norm();
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isLTE(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}
		// return this.totalize().cre() <= matrix.totalize().cre();
		return this.norm() <= matrix.norm();
	}

	/*
	 * ***********************************************
	 * NORMS
	 * ***********************************************
	 */
	
	/**
	 * Private method that returns the maximum value of a set of real values.
	 * @param valMatrix The set of real values.
	 * @return The maximum real value.
	 */
 	private double max(double[] valMatrix) {
		double temp;
		temp = valMatrix[0];

		for (int i = 1; i < valMatrix.length;  ++i)
			temp = Math.max(temp, valMatrix[i]);
		return temp;
	}
	
 	/**
	 * P Norm or Hölder Norm. Calculates Hölder's norm of order "p".
	 * @param p The order of the norm.
	 * @return The value of the norm.
	 */
	public double p_norm(int p) {
		int rowLen = this.rows();
		int colLen = this.cols();

		if ( p <= 0 ) {
			System.err.println("Not valid order. The order of the norm must be greater than zero.");
			System.exit(1);			
		}

		double[] norm = new double[rowLen];
		for (int row = 0; row  < rowLen; ++row) {
			norm[row] = 0.0;
			for (int col = 0; col < colLen; ++col)
				norm[row] += Math.pow(this.complexMatrix[row][col].mod(), p);
		}	
		return Math.pow(max(norm), 1.0/p);
	}

	/**
	 * Shortcut to euc_norm method.
	 * Euclidean Norm. Calculates the Euclidean norm.
	 * In linear algebra, functional analysis, and related areas of mathematics, a norm is a function that assigns a strictly positive length or size to each vector in a vector space—save for the zero vector, which is assigned a length of zero.
	 * @return The value of the norm.
	 */
	public double norm() {
		return this.euc_norm();
	}

	/**
	 * Calculates the norm of infinite order.
	 * @return The value of the norm.
	 */
	public double inf_norm() {
		int rowLen = this.rows();
		int colLen = this.cols();

		double[] norm = new double[rowLen+1];
		for (int row = 0; row < rowLen; ++row) {
			norm[row] = 0.0;
			for (int col = 0; col  < colLen; ++col)
				norm[row] += this.complexMatrix[row][col].mod();
		}	
		return max(norm);
	}

	/**
	 * Euclidean Norm. Calculates the Euclidean norm or norm of order 2.
	 * @return The value of the norm.
	 */
	public double euc_norm() {
		int rowLen = this.rows();
		int colLen = this.cols();

		double norm = 0;
		for (int row = 0; row < rowLen; ++row) {
			for (int col = 0; col  < colLen; ++col)
				norm += Math.pow(this.complexMatrix[row][col].mod(), 2.0);
		}	
		return Math.pow(norm, 0.5);
	}

	/**
	 * Frobenius norm. Calculates the Euclidean norm or norm of order 2.
	 * @return The actual value of the norm.
	 */
	public double f_norm() {
		return this.euc_norm();    	
	}

	/*
	 * ***********************************************
	 * UNARY OPERATORS
	 * ***********************************************
	 */
	
	/**
	 * Checks if a matrix is empty, A matrix is empty if rows = cols = 0
	 * @return True is matrix is empty
	 */
	public boolean isEmpty() {
		if (this.rows() == 0 && this.cols() == 0) return true;
		return false;
	}
	
	/**
	 * Makes the matrix to become positive semidefinite
	 */
	public void abs() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				this.setItem(row, col, this.getItem(row, col).abs());
	}	

	/**
	 * Checks whether the matrix is singular or not (determinant = 0)
	 * @return True if the matrix is singular, false otherwise
	 */
	public boolean isSingular() {
		return determinant().equals(Complex.ZERO);
	}

	/**
	 * Normal matrices: A·A.adjount() = A*A.adjoint()
	 * @return True if the matrix is normal, false otherwise
	 */
	public boolean isNormal() {
		return this.times(this.adjoint()).equals(this.adjoint().times(this));
	}

	/**
	 * Normal matrices: A·A.adjount() = A*A.adjoint() = I
	 * @return True if the matrix is normal, false otherwise
	 */
	public boolean isUnitary() {
		if (!isSquare()) return false;
		return this.adjoint().times(this).equals(eye(this.rows()));
	}

	/**
	 * Checks whether a Matrix is diagonal or not
	 * @return True if the matrix is diagonal, false otherwise
	 */
	public boolean isDiagonal() {
		if (!isSquare()) return false;
		for (int row = 0; row < rows(); ++row)
			for (int col = 0; col < cols(); ++col) {
				if (row != col) {
					if (!getItem(row, col).equals(Complex.ZERO)) return false;
				}
			}
		return true;
	}

	/**
	 * Checks whether a Matrix is orthogonal or not
	 * @return True if the matrix is orthogonal, false otherwise
	 */
	public boolean isOrthogonal() {
		if (!isSquare()) return false;
		return isDiagonal() && this.adjoint().times(this).equals(eye(this.rows()));
	}

	/**
	 * Checks whether a Matrix is orthogonal or not
	 * @return True if the matrix is orthonormal, false otherwise
	 */
	public boolean isOrthonormal() {
		Complex det = determinant();
		return isOrthogonal() && (det.equals(Complex.ONE) || det.equals(Complex.ONE.opposite()));
	}
	
	/**
	 * Upper Hessenberg matrices: a(i,j) = 0 for any pair i, j such that i > j + 1.
	 * @return True if the matrix is upper Hessenberg, false otherwise
	 */
	public boolean isHessenbergUpper() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col <= row; ++col)
				if (!getItem(row,col).equals(Complex.ZERO)) return false;
		return true;
	}

	/**
	 * Upper Hessenberg matrices: a(i,j) = 0 for any pair i, j such that i <= j.
	 * @return True if the matrix is lower Hessenberg, false otherwise
	 */
	public boolean isHessenbergLower() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = row; col < this.cols(); ++col)
				if (!getItem(row,col).equals(Complex.ZERO)) return false;					
		return true;
	}
	
	/**
	 * Checks if at least one of the values of the array is infinite
	 * @return True if one infinite value is found
	 */
 	public boolean isInfinite() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				if (this.getItem(row, col).isInfinite()) return true;
		return false;
	}
	
	/**
	 * Checks if at least one of the values of the array is NaN
	 * @return True if one NaN value is found
	 */
	public boolean isNaN() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				if (this.getItem(row, col).isNaN()) return true;
		return false;
	}

	/**
	 * Checks if the matrix is null compared with Complex.ZERO.
	 * @return true if the matrix is null, otherwise false.
	 */
	public boolean isNullC() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				if (!this.getItem(row, col).equals(Complex.ZERO)) return false;
		return true;
	}

	/**
	 * Checks if the matrix is null compared with Complex.ZERO.
	 * @return true if the matrix is null, otherwise false.
	 */
	public boolean isNull() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				if (!this.getItem(row, col).equals(Complex.ZERO)) return false;					
		return true;
	}

	/**
	 * The dimension of the matrix as a product of the number of rows by the number of columns.
	 * @return The matrix dimension.
	 */
	public int dim() {
		int rowLen = this.rows();
		int colLen = this.cols();
		return rowLen * colLen;
	}

	/**
	 * Returns the condition number of the array using the p norm, where p is the order of the norm.  
	 * @return The condition number
	 */
	public double cond_p(int p) {
		return this.p_norm(p) * this.inverse().p_norm(p);
	}
	
	/**
	 * Returns the condition number of the array using the euclidean norm 
	 * @return The condition number
	 */
	public double cond_f() {
		return this.f_norm() * this.inverse().f_norm();
	}
	
	/**
	 * Returns the condition number of the array using the infinite norm 
	 * @return The condition number
	 */
	public double cond_inf() {
		return this.inf_norm() * this.inverse().inf_norm();
	}

	/**
	 * Returns the condition number of the array using the infinite norm.
	 * Short cut to cond_imf() 
	 * @return The condition number
	 */
	public double cond() {
		return cond_inf();
	}
	
	/**
	 * Trace of an n-by-n square matrix A - the sum of the elements on the main diagonal. 
	 * @return The value of the trace.
	 */
	public Complex trace() {
		if (!this.isSquare()) {
			System.err.println("Not valid trace: The matrix has to be square.");
			System.exit(1);
		}
		int rowLen = this.rows();
		Complex trace = new Complex();

		for (int i = 0; i < rowLen; ++i)
			trace = trace.plus(this.complexMatrix[i][i]);
		return trace;
	}

	/**
	 * Cotrace of an n-by-n square matrix A - the sum of the elements on the secondary diagonal. 
	 * @return The value of the trace.
	 */
	public Complex cotrace() {
		if (!this.isSquare()) {
			System.err.println("Not valid cotrace: The matrix has to be square.");
			System.exit(1);
		}
		int rowLen = this.rows();
		Complex cotrace = new Complex();
		
		int col = rowLen - 1;
		for (int i = 0; i < rowLen; ++i)
			cotrace = cotrace.plus(this.complexMatrix[i][col--]);
		return cotrace;
	}

	/**
	 * Calculates the opposite of the matrix.
	 * @return The matrix opposite.
	 */
	public MatrixComplex opposite() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex cMatrix = new MatrixComplex(colLen, rowLen);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].opposite();  		
		return cMatrix;
	}

	/**
	 * Transpose of the matrix by reflecting it over its main diagonal.
	 * @return The matrix transposed.
	 */
	public MatrixComplex transpose() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex cMatrix = new MatrixComplex(colLen, rowLen);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[col][row] = this.complexMatrix[row][col];
		return cMatrix;
	}

	/**
	 * Calculates the conjugate of the matrix.
	 * Matrix complex conjugate is a new matrix with equal real part and imaginary part equal in magnitude but opposite in sign.
	 * @return The matrix conjugated.
	 */
	public MatrixComplex conjugate() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex cMatrix = new MatrixComplex(rowLen, colLen);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].conjugate();		
		return cMatrix;
	}

	/**
	 * Calculates the adjoint of the matrix.
	 * The adjoint is the transposed conjugated matrix.
	 * @return The new matrix adjoint.
	 */
	public MatrixComplex adjoint() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex cMatrix = new MatrixComplex(colLen, rowLen);

		//cMatrix = this.transpose().conjugate();
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[col][row] = this.complexMatrix[row][col].conjugate();
		return cMatrix;
	}

	/**
	 * Matrix of Cofactors order 1 for row "rowPivovt" and column "colPivot".
	 * The cofactor is the determinant of the minor associated.
	 * @param rowPivot The index of the row to eliminate.
	 * @param colPivot The index of the column to eliminate.
	 * @return The cofactors' matrix.
	 */
	public MatrixComplex cofactors(int rowPivot, int colPivot) {
		int rowLen = this.rows();
		int colLen = this.cols();

		if (rowPivot < 0 || rowPivot > rowLen) {
			System.err.println("Not valid cofactor: The row to pivot is incorrect.");
			System.exit(1);
		}

		if (colPivot < 0 || colPivot > colLen) {
			System.err.println("Not valid cofactor: The col to pivot is incorrect.");
			System.exit(1);
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLen-1, colLen-1);

		for (int row = 0, rowf = 0; row < rowLen; ++row) {
			if (row == rowPivot) 
				continue;
			for (int col = 0, colf = 0; col < colLen; ++col) {
				if (col == colPivot) 
					continue;
				resultMatrix.complexMatrix[rowf][colf++] = this.complexMatrix[row][col];
			}
			++rowf;
		}
		return resultMatrix;
	}

	/**
	 * Calculates the adjugate of an square matrix.
	 * Adjugate or classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate() {
		return this.cofactor().transpose();
	}

	/**
	 * Calculates the adjugate of an square matrix.
	 * Adjunct or adjugate, or classical adjoint, of a square matrix is the transpose of its cofactor matrix.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct() {
		return this.adjugate();
	}

	/**
	 * Calculates the adjugate for a row and a column.
	 * Adjugate or classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param rowPivot The index of the row to remove.
	 * @param colPivot The index of the column to remove.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate(int rowPivot, int colPivot) {
		return this.cofactors(rowPivot, colPivot).transpose();
	}

	/**
	 * Calculates the adjunct for a row and a column.
	 * Adjunct or adjugate, or classical adjoint, of a square matrix is the transpose of its cofactor matrix.
	 * @param rowPivot The index of the row to remove.
	 * @param colPivot The index of the column to remove.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct(int rowPivot, int colPivot) {
		return this.adjugate(rowPivot, colPivot);
	}

	/**
	 * Calculates the adjugate matrix of the rows passed in the parameter "includedRows".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRows A list with the indexes of the the rows included in the cofactors array.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate(int[] includedRows) {
		return this.cofactors(includedRows).transpose();
	}

	/**
	 * Calculates the adjunct matrix of the rows passed in the parameter "includedRows".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRows A list with the indexes of the the rows included in the cofactors array.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct(int[] includedRows) {
		return this.adjunct(includedRows);
	}

	/**
	 * Calculates the adjugate matrix of the rows passed in the parameter "includedRowsList".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRowsList A list with the rows included in the cofactors array as a comma separated string.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate(String includedRowsList) {
		return this.cofactors(includedRowsList).transpose();
	}

	/**
	 * Calculates the adjunct matrix of the rows passed in the parameter "includedRowsList".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRowsList A list with the rows included in the cofactors array as a comma separated string.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct(String includedRowsList) {
		return this.adjugate(includedRowsList);
	}

	/**
	 * The inverse of the matrix calculated by Gauss–Jordan elimination method
	 * Gauss–Jordan elimination method can be used for finding the inverse of a matrix, if it exists. 
	 * If A is a n by n square matrix, then row reduction can be used to compute its inverse matrix, if it exists. 
	 * First, the n by n identity matrix is augmented to the right of A, forming a n by 2n block matrix [A | I]. 
	 * Now through application of elementary row operations, finds the reduced echelon form of this n by 2n matrix. 
	 * The matrix A is invertible if and only if the left block can be reduced to the identity matrix I; in this case 
	 * the right block of the final matrix is A^−1. If the algorithm is unable to reduce the left block to I, 
	 * then A is not invertible.
	 * @return The inverse matrix.
	 */
	public MatrixComplex inverse() {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cCoef = new Complex();
		int row, col;

		if (rowLen != colLen) {
			System.err.println(HEADINFO + "inverse: Not valid matrix: The matrix has to be square.");
			return this.divides(0);
		}

		if (this.determinant().equals(0,0) ) {
			//System.err.println(HEADINFO + "inverse: Not valid matrix: The matrix determinat is ZERO.");
			return this.divides(0);
		}

		MatrixComplex auxMatrix = this.copy();
		MatrixComplex unitMatrix = new MatrixComplex(rowLen); unitMatrix.initMatrixDiag(1,0);

		for (int k = 0; k < rowLen-1; ++k) {
			if (auxMatrix.complexMatrix[k][k].equals(0,0)) {
				int rowSwap = auxMatrix.partialPivot(k);
				//int rowSwap = auxMatrix.locateSwapRowUp(k);
				if (rowSwap == -1) 
					return auxMatrix.divides(0);
				if (rowSwap != k) {
					auxMatrix.swapRows(k, rowSwap);
					unitMatrix.swapRows(k, rowSwap);
				}
			}
			for (row = k+1; row < rowLen; ++row) {
				cCoef = auxMatrix.complexMatrix[row][k].divides(auxMatrix.complexMatrix[k][k]);
				for (col = 0; col < colLen; ++col) {
					unitMatrix.complexMatrix[row][col] = unitMatrix.complexMatrix[row][col].minus(unitMatrix.complexMatrix[k][col].times(cCoef)); 
					auxMatrix.complexMatrix[row][col] = auxMatrix.complexMatrix[row][col].minus(auxMatrix.complexMatrix[k][col].times(cCoef));
				}
			}
		}

		for (int k = rowLen-1; k >= 0 ; --k) {
			if (auxMatrix.complexMatrix[k][k].equals(0,0)) {
				int rowSwap = auxMatrix.partialPivot(k);
				//int rowSwap = auxMatrix.locateSwapRowDown(k);
				if (rowSwap == -1) 
					return auxMatrix.divides(0);
				if (rowSwap != k) {
					auxMatrix.swapRows(k, rowSwap);
					unitMatrix.swapRows(k, rowSwap);
				}
			}
			for (row = k-1; row >= 0; --row) {
				cCoef = auxMatrix.complexMatrix[row][k].divides(auxMatrix.complexMatrix[k][k]);
				for (col = 0; col < colLen; ++col) {
					unitMatrix.complexMatrix[row][col] = unitMatrix.complexMatrix[row][col].minus(unitMatrix.complexMatrix[k][col].times(cCoef)); 
					auxMatrix.complexMatrix[row][col] = auxMatrix.complexMatrix[row][col].minus(auxMatrix.complexMatrix[k][col].times(cCoef));
				}
			}
		}

		for (row = 0; row < rowLen; ++row) {
			cCoef = auxMatrix.complexMatrix[row][row].reciprocal();
			for (col = 0; col < colLen; ++col) {
				auxMatrix.complexMatrix[row][col] = auxMatrix.complexMatrix[row][col].times(cCoef);
				unitMatrix.complexMatrix[row][col] = unitMatrix.complexMatrix[row][col].times(cCoef); 
			}
		}
		return unitMatrix;
	}

	/**
	 * Returns the upper triangularization of the matrix.
	 * @return The  upper triangular matrix.
	 */
	public MatrixComplex triangle(){
		return this.triangleUp();
	}

	/**
	 * Generates a diagonal matrix using triangularization Low and then Up
	 * @return The diagonal matrix
	 */  
	public MatrixComplex diagonalLo() {
		return (this.triangleLo()).triangleUp();
	}

	/**
	 * Generates a diagonal matrix using triangularization Up and then Lo
	 * @return The diagonal matrix
	 */  
	public MatrixComplex diagonalUp() {
		return (this.triangleUp()).triangleLo();
	}

	/**
	 * Shortcut to determinantGauss.
	 * Calculate the matrix determinant by the default rule (Gauss)
	 * @return The value of the determinant.
	 */
	public Complex determinant() {
		return this.determinantGauss();
	}

	/**
	 * Calculates the matrix determinant by the Gauss' method.
	 * @return The value of the determinant.
	 */
	public Complex determinantGauss() {
		int rowLen = this.rows();

		if (rowLen != this.cols()) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}

		Complex cResult = new Complex(1, 0);
		MatrixComplex auxMatrix = this.triangle();
		for (int iter = 0; iter < rowLen; ++iter) {
			cResult = cResult.times(auxMatrix.complexMatrix[iter][iter]);
		}
		return cResult.times(auxMatrix.mSign);
	}

	/**
	 * Private method that calculates the matrix 3x3 determinant by the Sarrus' rule.
	 * @return The value of the determinant.
	 */
	private Complex determinant3() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int k = 0;
		Complex cGroup = new Complex(1,0);
		Complex determinant = new Complex();

		if (rowLen != colLen) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}

		for (int row = 0; row < rowLen; ++row) {
			k = 0;
			for (int col = 0; col < colLen; ++col) {
				cGroup = cGroup.times(this.complexMatrix[(row+k++)%rowLen][col]);
			}
			determinant = determinant.plus(cGroup);
			cGroup.setComplexPol(1, 0);

		}
		for (int row = rowLen-1; row >= 0; --row) {
			k = rowLen-1;
			for (int col = 0; col < colLen; ++col) {
				cGroup = cGroup.times(this.complexMatrix[(row+k--)%rowLen][col]);
			}
			determinant = determinant.minus(cGroup);
			cGroup.setComplexRec(1, 0);
		}
		return determinant;
	}

	/**
	 * Calculates the matrix determinant through matrix of adjoints (cofactors)
	 * DO NOT USE FOR MATRIX OVER 5x5.
	 * @return The value of the determinant.
	 */
	public Complex determinantAdj() {
		Complex cSum = new Complex(); 
		int rowLen = this.rows();
		if (rowLen == 1) 
			return this.complexMatrix[0][0];	

		int colLen = this.cols();

		if (rowLen != colLen) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}

		if (rowLen == 2) 
			return (this.complexMatrix[0][0].times(this.complexMatrix[1][1])).
					minus (this.complexMatrix[0][1].times(this.complexMatrix[1][0]));

		if (rowLen == 3) {  //bottom case of recursion. size 1 complexMatrix determinant is itself.
			return this.determinant3();
		}

		for (int i = 0; i < rowLen; ++i){ //finds determinant using row-by-row expansion
			MatrixComplex smaller = new MatrixComplex(rowLen - 1, colLen - 1); //creates smaller complexMatrix- values not in same row, column
			for (int a = 1; a < rowLen; ++a) {
				for (int b = 0; b < colLen; ++b) {
					if (b < i) { 
						smaller.complexMatrix[a-1][b] = this.complexMatrix[a][b];
					}
					else if (b > i) { smaller.complexMatrix[a-1][b-1] = this.complexMatrix[a][b];
					}
				}
			}
			if ((i&1) == 0) cSum = cSum.plus (this.complexMatrix[0][i].times(smaller.determinantAdj()));
			else cSum = cSum.minus(this.complexMatrix[0][i].times(smaller.determinantAdj()));
		}
		return cSum ; //returns determinant value. once stack is finished, returns final determinant.
	}

	/**
	 * Checks if the matrix is symmetric or not
	 * @return True if the matrix is symmetric
	 */
	public boolean isSymmetric() {
		if (this.rows() != this.cols()) return false;
		for (int row = 0; row < this.rows(); ++row)
			for (int col = row; col < this.cols(); ++col)
				if(!getItem(row, col).equals(getItem(col, row))) return false;
		return true;
	}
	
	/**
	 * Checks if the matrix is antisymmetric or not
	 * @return True if the matrix is antisymmetric
	 */
	public boolean isAntiSymmetric() {
		if (this.rows() != this.cols()) return false;
		for (int row = 0; row < this.rows(); ++row)
			for (int col = row; col < this.cols(); ++col)
				if(row == col) {
					if (!getItem(row, col).equals(Complex.ZERO)) return false;
				}
				else if(!getItem(row, col).equals(getItem(col, row).opposite().conjugate())) return false;
		return true;
	}

	/**
	 * Checks if the matrix is skew-symmetric or not
	 * @return True if the matrix is skew-symmetric
	 */
	public boolean isSkewSymmetric() {
		return isAntiSymmetric();
	}

	/**
	 * Checks if the matrix is hermitian or not
	 * @return True if the matrix is hermitian
	 */
	public boolean isHermitian() {
		if (this.rows() != this.cols()) return false;
		for (int row = 0; row < this.rows(); ++row)
			for (int col = row; col < this.cols(); ++col)
				if(row == col) {
					if (!getItem(row, col).isPureReal()) return false;
				}
				else if(!getItem(row, col).equals(getItem(col, row).conjugate())) return false;
		return true;
	}
	
	/**
	 * Checks if the matrix is antihermitian or not
	 * @return True if the matrix is antihermitian
	 */
	public boolean isAntiHermitian() {
		if (this.rows() != this.cols()) return false;
		for (int row = 0; row < this.rows(); ++row)
			for (int col = row; col < this.cols(); ++col)
				if(row == col) {
					if (!getItem(row, col).isPureImaginary()) return false;
				}
				else if(!getItem(row, col).equals(getItem(col, row).opposite().conjugate())) return false;
		return true;
	}

	/**
	 * Checks if the matrix is skew-hermitian or not
	 * @return True if the matrix is skew-hermitian
	 */
	public boolean isSkewHermitian() {
		return isAntiHermitian();
	}
	
	/**
	 * Method for creating an Square Identity array of "dim" size
	 * @param dim The size of Identity array
	 * @return The Identity array
	 */
	public static MatrixComplex eye(int dim) {
		MatrixComplex eye = new MatrixComplex(dim);
		eye.initMatrixDiag(1,0);
		return eye;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPostiveDefinite() {
		if (this.isHermitian()) {
			Eigenspace eigenSpace = new Eigenspace(this);
			MatrixComplex eigenvals = eigenSpace.eigenvalues();
			for (int row = 0; row < eigenvals.rows(); ++row) {
				if (Math.abs(eigenvals.getItem(row, 0).imp()) < Complex.zero() &&
						eigenvals.getItem(row, 0).rep() <= Complex.zero() )
					return false;
			}
			return true;
		}
		else return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPostiveSemiDefinite() {
		if (this.isHermitian()) {
			Eigenspace eigenSpace = new Eigenspace(this);
			MatrixComplex eigenvals = eigenSpace.eigenvalues();
			for (int row = 0; row < eigenvals.rows(); ++row) {
				if (Math.abs(eigenvals.getItem(row, 0).imp()) < Complex.zero() &&
						eigenvals.getItem(row, 0).rep() < Complex.zero() )
					return false;
			}
			return true;
		}
		else return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNegtiveDefinite() {
		if (this.isHermitian()) {
			Eigenspace eigenSpace = new Eigenspace(this);
			MatrixComplex eigenvals = eigenSpace.eigenvalues();
			for (int row = 0; row < eigenvals.rows(); ++row) {
				if (Math.abs(eigenvals.getItem(row, 0).imp()) > Complex.zero() && 
						eigenvals.getItem(row, 0).rep() >= -Complex.zero() )
					return false;
			}
			return true;
		}
		else return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNegtiveSemiDefinite() {
		if (this.isHermitian()) {
			Eigenspace eigenSpace = new Eigenspace(this);
			MatrixComplex eigenvals = eigenSpace.eigenvalues();
			for (int row = 0; row < eigenvals.rows(); ++row) {
				if (Math.abs(eigenvals.getItem(row, 0).imp()) > Complex.zero() && 
						eigenvals.getItem(row, 0).rep() > -Complex.zero() )
					return false;
			}
			return true;
		}
		else return false;
	}

	/**
	 * Checks if there is a zero on the main diagonal.
	 * @return True if a zero was found, false otherwise.
	 */
	public boolean hasZeroMainDiag() {
		for (int i=0; i < this.rows(); ++i)
			if (this.getItem(i, i).isZero()) return true;
		return false;
	}
	
	/**
	 * Checks if there is one item on the main diagonal for which its REAL PART is zero or negative .
	 * @return False if a non positive was found, false otherwise.
	 */
	public boolean repPositiveMainDiag() {
		for (int i=0; i < this.rows(); ++i)
			if (this.getItem(i, i).rep() < 0) return false;
		return true;
	}
	
	/**
	 * Method for creating an Square Identity array of "this" matrix size
	 * @param dim The size of Identity array
	 * @return The Identity array
	 */
	public MatrixComplex eye() {
		MatrixComplex eye = new MatrixComplex(this.rows());
		eye.initMatrixDiag(1,0);
		return eye;
	}

	/**
	 * Copies the 1xN values of a one row array and put one by one in a diagonal NxN matrix
	 * @param values The values of a one row array
	 * @return The diagonal NxN matrix
	 */
	public static MatrixComplex diagonal(MatrixComplex values) {
		MatrixComplex newValArray = values.clone();
		if (newValArray.rows() == 1) newValArray = newValArray.transpose();
		
		MatrixComplex sqDiagonal = new MatrixComplex(newValArray.rows());
		for (int row = 0; row < newValArray.rows(); ++row) {
			sqDiagonal.setItem(row, row, newValArray.getItem(row, 0));
		}
		return sqDiagonal;
	}
	
	/*
	 * ***********************************************
	 * EQUATION SYSTEMS
	 * ***********************************************
	 */
	
	/**
	 * Returns a new matrix with the coefficients of the object matrix.
	 * The new matrix is the original one with the independent terms removed.
	 * @return The new matrix with the coefficients copied without independent terms.
	 */
 	public MatrixComplex coefMatrix() {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex coefMatrix = new MatrixComplex(rowLen, colLen-1);
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen-1; ++col)
				coefMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].copy();
		return coefMatrix;
	}

 	/**
	 * Returns a new matrix with the independent terms of the object matrix.
	 * The new matrix is the independent terms column matrix.
	 * @return The new matrix with the independent terms.
	 */
	public MatrixComplex indMatrix() {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex indMatrix = new MatrixComplex(rowLen, 1);
		for (int row = 0; row < rowLen; ++row)
			indMatrix.complexMatrix[row][0] = this.complexMatrix[row][colLen-1].copy();
		return indMatrix;
	}

	/**
	 * Returns a new matrix with the independent terms of the object matrix.
	 * The new matrix is the independent terms column matrix.
	 * @return The new matrix with the independent terms.
	 */
	public MatrixComplex constMatrix() {
		return this.indMatrix();
	}
	
	/**
	 * Defines the constants that identify the type of equation system being solved.
	 */
	public static final int INCONSISTENT = -1;
	public static final int INDETERMINATE = 0;
	public static final int DETERMINATE = 1;

	/**
	 * Identifies if the system of equations is isHomogeneous.
	 * @return Returns true if the system is isHomogeneous, false otherwise.
	 */
	public boolean isHomogeneous() {
		int rowLen = this.rows();
		int lastCol = this.cols()-1;
		boolean isHomogeneous = true;

		for (int row = 0; row < rowLen; ++row)
			isHomogeneous &= this.complexMatrix[row][lastCol].equals(0, 0);
		return isHomogeneous;
	}

	/**
	 * Returns the homogeneous equation system of this
	 * @return the homogeneous equation system of this
	 */
	public MatrixComplex homogeneous() {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex homogeneous = new MatrixComplex(rowLen, colLen);

		homogeneous = this.copy();
		
		for (int row = 0; row < rowLen; ++row)
			homogeneous.setItem(row, colLen-1, Complex.ZERO);
		return homogeneous;
	}
	
	/**
	 * Identifies the type of systems of equations returning the constant according to the definition.
	 * @return INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 */
	public int typeEqSys() {
		final boolean DEBUG_ON = false;
		int numUnk = this.cols()-1;
		int coefRank, augmRank;

		MatrixComplex coefMatrix = this.coefMatrix();

		augmRank = this.rank();
		coefRank = coefMatrix.rank();
		
		if (DEBUG_ON) {
			coefMatrix.println("------------------ typeEqSys() coefMatrix");
			System.out.println("------------------ typeEqSys() augmRank:" + augmRank);
			System.out.println("------------------ typeEqSys() coefRank:" + coefRank);
		}
		
		if (augmRank != coefRank) return INCONSISTENT;
		//if (augmRank != coefRank || coefRank == 0) return INCONSISTENT;
			
		if (coefRank == numUnk) return this.isHomogeneous() ? INDETERMINATE : DETERMINATE;
		else return INDETERMINATE;
	}

	/**
	 * Returns a string indicating the system type of equations according to type "type".
	 * @param type INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 * @param lambda Parameter value to calculate solutions for indeterminate systems.
	 */
	public String strTypeEqSys(int type, Complex lambda) {
		String text = "";
		switch (type) {
			case INCONSISTENT: text = "The system is INCONSISTENT"; 
				break;
			case INDETERMINATE: text = "The system is INDETERMINATE. Solutions calculated for λ = " + lambda.toString(); 
				break;
			case DETERMINATE: text = "The system is DETERMINATE"; 
				break;
		}    	
		return text;
	}

	/**
	 * Prints a message indicating the system type of equations according to type "type".
	 * @param type INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 * @param lambda Parameter value to calculate solutions for indeterminate systems.
	 */
	public void printTypeEqSys(int type, Complex lambda) {
		System.out.println(this.strTypeEqSys(type, lambda) );
	}

	/**
	 * Prints a message indicating the system type of equations according to type "type". lambda is 1 by default
	 */
	public void printTypeEqSys() {
		int type = this.typeEqSys();
		Complex lambda = new Complex(1,0);
		printTypeEqSys(type, lambda);
	}

	/**
	 * If the number of equations is less than of unknowns, the missing equations are introduced with the coefficients and the independent term set to zero.
	 * @return the system of equations completed with equations that are missing to zero.
	 */
	public MatrixComplex completepEqSys() {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex eqsMatrix = new MatrixComplex(colLen-1, colLen);
		Complex[] zeroRowComplex = new Complex[colLen];
		for (int col = 0; col < colLen; ++col) zeroRowComplex[col] = new Complex();

		for (int row = 0; row < colLen-1; ++row) {
			if (row < rowLen)
				eqsMatrix.complexMatrix[row] = this.complexMatrix[row].clone(); 
			else
				eqsMatrix.complexMatrix[row] = zeroRowComplex.clone();
		}
		return eqsMatrix;
	}

	/**
	 * Finds the solutions of an equation systems by the default rule (Gauss reduction)
	 * @return The solutions of the equation systems
	 */
	public MatrixComplex solve() {
			MatrixComplex newThis = this.clone();
			// -------------- newThis.quicksort(0);
			return newThis.solveGauss(Complex.ONE);
	}

	/**
	 * Shortcut to solveGauss
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist.
	 */
	public MatrixComplex solve(Complex lambda) {
		MatrixComplex newThis = this.clone();
		// ----------------- newThis.quicksort(0);
		return newThis.solveGauss(lambda);
	}

	/**
	 * Equation evaluator
	 * Evaluates an equation replacing its unknowns with values
	 * @param row
	 * @param col
	 * @param point
	 * @return
	 */
	public Complex eqEval(int row, int col,MatrixComplex point) {
		int colLen = this.cols();
		Complex eqEval = new Complex();
		Complex divisor = new Complex();
		int ncol;
		
		eqEval = this.complexMatrix[row][colLen-1];
		for (ncol = 0; ncol < colLen -1; ++ncol) {
			if (ncol == col) divisor =  this.complexMatrix[row][ncol];
			else eqEval = eqEval.minus(this.complexMatrix[row][ncol].times(point.complexMatrix[0][ncol]));
		}
		return eqEval.divides(divisor);
	}
	
	/**
	 * Checks if the all the items (except colIdx) of rowIdx of the Matrix are null or not
	 * @param rowIdx The row to inspect
	 * @param colIdx The col to skip
	 * @return True if all are null
	 */
	private boolean isNullSolution(int rowIdx, int colIdx) {
		for(int col = 0; col < this.cols(); ++col) {
			if (col == colIdx) continue;
			if (!this.getItem(rowIdx,col).equals(0,0)) return false;
		}
		return true;
	}
	
	/**
	 * Removes the null rows of a Matrix
	 * @return A new Matrix without the null rows
	 */
	public MatrixComplex removeNullRows() {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex newMatrix = new MatrixComplex(rowLen, colLen);
		int ndRow = 0;
		for(int row = 0; row < rowLen; ++row) {
			if (!this.isNullRow(row)) newMatrix.complexMatrix[ndRow++] = this.complexMatrix[row].clone();
		}
		MatrixComplex rdMatrix = new MatrixComplex(ndRow, colLen);
		for(int row = 0; row < ndRow; ++row) {
			rdMatrix.complexMatrix[row] = newMatrix.complexMatrix[row].clone();
		}
		return rdMatrix;
	}

	/**
	 * Gets a valid coefficient to normalize the linear equation 
	 * @param row1 The row to normalize
	 * @param row2 The row to get the coefficient to use to do the normalization
	 * @return The coefficient found
	 */
	private Complex getCoef(int row1, int row2) {
		Complex coef = new Complex(0);
		
		for (int col = 0; col < this.cols(); ++col) {
			if(!this.getItem(row2, col).equals(Complex.ZERO)) {
				coef = this.getItem(row1, col).divides(this.getItem(row2, col));
				break;
			}
		}
		return coef;
	}
	
	/**
	 * Removes the duplicated rows of a Matrix
	 * @return A new Matrix without the duplicated rows
	 */
	public MatrixComplex removeDuplicateRows() {
		boolean isDuplicate = true;
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex newMatrix = new MatrixComplex(rowLen, colLen);
		int ndRow = 0;
		newMatrix.complexMatrix[ndRow++] = this.complexMatrix[0].clone();
		for(int row = 1; row < rowLen; ++row) {
			for(int rrow = 0; rrow < row; ++rrow) {
				//System.out.println("checking row:" + row +" - rrow:" + rrow);
				isDuplicate = true;
				Complex coef = this.getCoef(rrow, row);
				//coef.println("El coeficiente encontrado:");
				for(int col = 0; col < colLen; ++col) {
					if (coef.equals(Complex.ZERO)) {
						isDuplicate = false;
						break;
					}
					if (!this.getItem(row,col).equals(this.getItem(rrow,col).divides(coef))) {
						isDuplicate = false;
						break;
					}
				}
				if (isDuplicate) {
					System.out.println("------- duplicate row:" + row +" - rrow:" + rrow);
					break;
				}
			}
			if (!isDuplicate) newMatrix.complexMatrix[ndRow++] = this.complexMatrix[row].clone();
		}
		MatrixComplex rdMatrix = new MatrixComplex(ndRow, colLen);
		for(int row = 0; row < ndRow; ++row) {
			rdMatrix.complexMatrix[row] = newMatrix.complexMatrix[row].clone();
		}
		return rdMatrix;
	}
	
	/**
	 * Returns the minimum number of LI solutions that the equation system has
	 * @return The minimum number of LI solutions
	 */
	public int nbrOfSolutions() {
		if (this.rows()+1 != this.cols()) return 0;
		int rank = this.rank();
		int dim = this.cols();
		int nbrUkn = dim-1;
		int nbrSolutions = nbrUkn-rank;
		nbrSolutions = nbrSolutions == 0 ? 1 : nbrSolutions;
		return nbrSolutions;
	}
	
	/**
	 * Calculates the minimum number of LI solutions of an equation system and writes it on the console
	 * @return The minimum number of LI solutions
	 */
	public int nbrOfSolutionsText() {
		int numSols;
		switch (this.typeEqSys()) {
			case MatrixComplex.DETERMINATE:
				System.out.println("1 single solution is returned."); 
				numSols = 1;
				break;
			case MatrixComplex.INDETERMINATE:
					numSols = this.nbrOfSolutions();
					System.out.println(numSols + " linear independent solutions are returned."); 
					break;
			default:
				numSols = 0;
				System.out.println("System without solution.");
		}
		return numSols;
	}
	
	/**
	 * Checks a SINGLE solution putting that solution in the equation system and operating 
	 * If the check operation returns a row with all zeros, the solution is correct
	 * @param solution The solution to check as a one row array 
	 * @return True If the solution is correct
	 */
	private boolean checkSingleSol(MatrixComplex solution) {
		MatrixComplex indTerm = this.indMatrix().transpose();
		MatrixComplex uknMatix = this.unkMatrix().transpose();
		MatrixComplex unitMatrix = new MatrixComplex(solution.rows(),1);
		unitMatrix.initMatrix(1, 0);
		
		if (solution.times(uknMatix).minus(unitMatrix.times(indTerm)).isNull()) return true;
		else return false;
	}

	/**
	 * Gets the row (rowIdx) of a Matrix
	 * @param rowIdx The row index to retrieve. 1st row is 0, and so on.
	 * @return The row selected
	 */
	public MatrixComplex getRow(int rowIdx) {
		MatrixComplex rowMatrix = new MatrixComplex(1,this.cols());
		//if (rowIdx > this.rows()-1) return rowMatrix;
		
		rowMatrix.complexMatrix[0] = this.complexMatrix[rowIdx].clone();
		return rowMatrix;
	}

	/**
	 * Gets the col (colIdx) of a Matrix
	 * @param colIdx The col index to retrieve. 1st col is 0, and so on.
	 * @return The col selected
	 */
	public MatrixComplex getCol(int colIdx) {
		MatrixComplex colMatrix = new MatrixComplex(this.rows(),1);
		//if (colIdx > this.cols()-1) return colMatrix;
		for (int row = 0; row < this.rows(); ++row) {
			colMatrix.setItem(row, 0, this.getItem(row,colIdx));
		}
		return colMatrix;
	}
	/**
	 * Sets the row (rowIdx) of "this" with the values of rowMatrix
	 * @param rowIdx The row index to retrieve. 1st row is 0, and so on.
	 */
	public void setRow(int rowIdx, MatrixComplex rowMatrix) {		
		this.complexMatrix[rowIdx] = rowMatrix.complexMatrix[0].clone();
	}

	/**
	 * Sets the column colIdx of the matrix to certain complex value
	 * @param colIdx The Id of the col to set
	 * @param cValue The value to set
	 */
	public void setCol(int colIdx, Complex cValue) {
		for (int row = 0; row < this.rows(); ++row)
			this.complexMatrix[row][colIdx] = cValue;
	}

	/**
	 * Sets colum colMatrix into this at colIdx column
	 * @param colIdx
	 * @param colMatrix
	 */
	public void setCol(int colIdx, MatrixComplex colMatrix) {
		for (int row = 0; row < this.rows(); ++row)
			this.setItem(row, colIdx, colMatrix.getItem(row, 0));
	}

	/**
	 * Sets the row rowIdx of the matrix to certain complex value
	 * @param rowIdx The Id of the col to set
	 * @param cValue The value to set
	 */
	public void setRow(int rowIdx, Complex cValue) {		
		for (int col = 0; col < this.cols(); ++col)
			this.complexMatrix[rowIdx][col] = cValue;
	}

	/**
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist, otherwise null.
	 */	
	public MatrixComplex solveGauss(Complex lambda) {
		final boolean DEBUG_ON = false;
		final String METH_NAME = "solveGauss";
		int rowLen = this.rows();
		int colLen = this.cols();

		/* ----------  START DEBUGGING BLOCK   ----------- */
		if (DEBUG_ON) {
			this.println(HEADINFO + METH_NAME + ": " + "= = = = = = = = Original Matrix");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		if (rowLen+1 > colLen) {
			System.out.println(HEADINFO + METH_NAME + ": " + "Not valid matrix: The matrix doesn't represent an equation system.");
		}

		MatrixComplex auxMatrix, coefMatrix, indMatrix;
		MatrixComplex solMatrix = new MatrixComplex(1, colLen-1);

		int typeEqSys = this.typeEqSys();
		
		/* ----------  START DEBUGGING BLOCK   ----------- */
		if (DEBUG_ON) {
			this.printTypeEqSys(typeEqSys, lambda);
		}
		/* ------------- END DEBUGGING BLOCK ------------- */
		
		// *************** INCONSISTENT ***************
		if (typeEqSys == INCONSISTENT) {
			if (DEBUG_ON) {
				System.out.println(HEADINFO + METH_NAME + ": " + "This system is INCONSISTENT. It hasn't got any solution!!!!!!!!!!");
			}
			// This is required to mark the Inconsistent solution for later operations 
			return solMatrix.divides(0).transpose();
		}

		// *************** DETERMINATE ***************
		if (typeEqSys == DETERMINATE) {
			if (DEBUG_ON) {
				System.out.println(HEADINFO + METH_NAME + ": " + "This system is DETERMINATE. This is the unique solution!!!!!!!!!!");
			}
			coefMatrix = this.coefMatrix();
			indMatrix = this.indMatrix();
			return coefMatrix.dividesleft(indMatrix).transpose();
		}

		// *************** INDETERMINATE ***************
		/*
		 * The complete system of equations is solved by triangularization
		 * The solutions are calculated by process known as back-substitution
		 */
		/*
		 * System reduction is used to find a particular solution for indeterminate compatible systems
		 * Based on the number of solutions unknowns are fixed in a orthogonal base to determine the 
		 * linear independent solutions 
		 * A value for lambda is given to calculate the indeterminate solutions
		 */
		auxMatrix = this.triangleUpPerfect();
		/* ----------  START DEBUGGING BLOCK   ----------- */
		if (DEBUG_ON) {
			this.println(HEADINFO + METH_NAME + ": " + "= = = = = = = = Original Matrix");
			auxMatrix.println(HEADINFO + METH_NAME + ": " + "+ + + + + + auxMatrix Triangle Up Perfect");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		indMatrix = auxMatrix.indMatrix();
		int nbrOfSols = auxMatrix.nbrOfSolutions();
		solMatrix = new MatrixComplex(nbrOfSols, rowLen);
		// solBase: base para el cálculo de las soluciones
		// solBase: base for calculating the solutions 
		MatrixComplex solBase = new MatrixComplex(nbrOfSols, rowLen);
		Complex coefSol;
		
		// Se construye la base para las soluciones recorriendo la matriz triangular perfecta
		// y rellenando cada fila de la base con lambda en la posición en que la diagonal principal
		// de la matriz triangular perfecta es cero
		//
		// Build the basis for the solutions by traversing the perfect triangular matrix
		// and padding each base row with lambda at the position where the leading diagonal
		// of the perfect triangular matrix is zero
		for (int row = 0, solNbr = 0; row < rowLen && solNbr < nbrOfSols; ++row) {
			if (auxMatrix.getItem(row ,row).equals(Complex.ZERO)) {
				solBase.setItem(solNbr++, row, lambda);
			}
		}
		
		// Ahora con la solución base se calculan las soluciones del sistema de ecuaciones
		// mediante sustitución inversa sobre la matriz tiangular perfecta en aquellas 
		// filas cuyos elemntos de la diagonal principal no son nulos
		//
		// Now with the base solution the solutions of the system of equations are calculated
		// by inverse substitution on the perfect tiangutar matrix in those
		// rows whose main diagonal elements are not null 
		for (int solNbr = 0; solNbr < nbrOfSols; ++solNbr) {
			for (int row = rowLen-1; row >= 0; --row) {
				if (!auxMatrix.getItem(row ,row).equals(Complex.ZERO)) {
					// Se calcula el coeficiente del nuevo término independiente con los valores de las incógnitas ya calculadas
					// Se obtiene el término independiente de la ecuación original
					//
					// The coefficient of the new independent term is calculated with the values of the unknowns already calculated
					// The independent term of the original equation is obtained 
					coefSol = auxMatrix.getItem(row, colLen-1);
					// Para cada incógnita calculada...
					//
					// For each calculated unknown ... 
					for (int col = colLen-2; col > row; --col) {
						// Se despeja y actualiza el nuevo término independiente
						//
						// The new independent term is cleared and updated 
						coefSol = coefSol.minus(auxMatrix.getItem(row, col).times(solMatrix.getItem(solNbr, col)));
					}
					// Si el coeficiente de la incógnita a calcular es cero entonces...
					//
					// If the coefficient of the unknown to be calculated is zero then ... 
					if (auxMatrix.getItem(row, row).equals(Complex.ZERO))
						// ...el nuevo término independiente se fija a cero
						//
						// ... the new independent term is set to zero 
						coefSol = Complex.ZERO;
					else 
						// Sino, el nuevo término independiente se divide por el coeficiente de la incógnita
						//
						// Otherwise, the new independent term is divided by the coefficient of the unknown 
						coefSol = coefSol.divides(auxMatrix.getItem(row, row));
				}
				else {
					coefSol = solBase.getItem(solNbr, row);
					/* * /
					// This piece of code is kept to check in more examples
					if (auxMatrix.getRow(row).isNull() && this.isHomogeneous())
						coefSol = lambda;
					else 
						coefSol = solBase.getItem(solNbr, row);
					/* */
				}

				// Se actualiza la matriz de soluciones con el valor calculado para la solución número solNbr y la columan correspondiente que coincide con el número de fila o ecuación tratada
				//
				// The solution matrix is updated with the value calculated for the solNbr number solution and the corresponding column that matches the row number or treated equation 
				solMatrix.setItem(solNbr, row, coefSol);
			}
		}
		
		/* ----------  START DEBUGGING BLOCK   ----------- */
		if (DEBUG_ON) {
			solMatrix.println(HEADINFO + METH_NAME + ": " + "solMatrix");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		/* Check sols for INDETERMINATE Systems */
		MatrixComplex nullRow = new MatrixComplex(1, rowLen); // By default initialized to ZERO nullRow.initMatrix(0, 0);
		for (int solIdx = 0; solIdx < solMatrix.rows(); ++solIdx) {
			if (this.checkSingleSol(solMatrix.getRow(solIdx))) continue;
			else {
				/* ----------  START DEBUGGING BLOCK   ----------- */
				if (DEBUG_ON) {
					solMatrix.getRow(solIdx).println(HEADINFO + METH_NAME + ": " + "Solution FAILED!!! ");
				}
				/* ------------- END DEBUGGING BLOCK ------------- */
				solMatrix.complexMatrix[solIdx] = nullRow.complexMatrix[0].clone();
			}
		}

		return solMatrix;
	}

	/**
	 * finds the solutions to a equation systems using the Cramer's rule
	 * @return The column matrix with the solutions if they exist.
	 */
	public MatrixComplex solveCramer() {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cCoef = new Complex();
		int row;

		if (rowLen+1 != colLen) {
			System.err.println(HEADINFO + "solveCramer: " + "Not valid matrix: The matrix doesn't represent an equation system.");
		}

		MatrixComplex coefMatrix = new MatrixComplex(rowLen, rowLen);
		MatrixComplex auxMatrix = new MatrixComplex(rowLen, rowLen);
		MatrixComplex solMatrix = new MatrixComplex(rowLen, 1);

		if (this.typeEqSys() != DETERMINATE) {
			System.out.println(HEADINFO + "solveCramer ERROR: " + "The system is not determined, so there is no Cramer solution.");
			return solMatrix.transpose().divides(0);			
		}
		//coefMatrix.copyCol(this, colLen);
		System.out.println(HEADINFO + "SOLVED by KRAMER's method ");
		coefMatrix = this.unkMatrix();
		cCoef = coefMatrix.determinant();
		for (row = 0; row < rowLen; ++row) {
			auxMatrix.copyCol(this, row);
			solMatrix.complexMatrix[row][0] = (auxMatrix.determinant()).divides(cCoef);
		}

		return solMatrix.transpose();
	}

	/**
	 * rowReduce performs the Gauss-Jordan elimination, adding multiples of rows together so as to produce zero elements when possible. The final matrix is in reduced row echelon form. 
	 * @return The final matrix in its reduced row echelon form. 
	 */
	public MatrixComplex rowReduce() {
		MatrixComplex rowRed = this.triangleUpPerfect();
		for (int row = 0; row < rowRed.rows(); ++row) {
			Complex term = new Complex(1,0);
			boolean doDiv = false;
			for (int col = 0; col < rowRed.cols(); ++col) {
				if (doDiv) {
					rowRed.setItem(row, col, rowRed.getItem(row, col).divides(term));
				}
				else {
					term = rowRed.getItem(row, col);
					if (term.equals(Complex.ZERO)) continue;
					else {
						doDiv = true;
						rowRed.setItem(row, col, Complex.ONE);
					}
				}
			}
		}
		return rowRed.triangleLo();
	}

	/**
	 * Returns the sub-matrix from row, col of order "order" (number of rows/columns taken)
	 * @param row The index of the initial row.
	 * @param col The index of the initial column.
	 * @param order Number of rows/columns taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrixAug(int row, int col, int order) {
		MatrixComplex resultMatrix = new MatrixComplex(order, order+1);
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowEnd = row + order;
		int colEnd = col + order + 1;

		if (rowEnd > rowLen) {
			System.err.println("Not valid submatrix: The row length surpass the matrix length.");
			System.exit(1);   		
		}
		if (colEnd > colLen) {
			System.err.println("Not valid submatrix: The col length surpass the matrix length.");
			System.exit(1);   		
		}

		for (int i = row, rrow = 0; i < rowEnd; ++i, ++rrow) {
			for (int j = col, ccol = 0; j < colEnd; ++j, ++ccol) {
				resultMatrix.complexMatrix[rrow][ccol] = this.complexMatrix[i][j].copy();
			}
		}
		return resultMatrix;
	}

	/**
	 * Returns the sub-matrix from row, col of order "order" (number of rows/columns taken)
	 * @param row The index of the initial row.
	 * @param col The index of the initial column.
	 * @param order Number of rows/columns taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrix(int row, int col, int order) {
		MatrixComplex resultMatrix = new MatrixComplex(order, order);
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowEnd = row + order;
		int colEnd = col + order;

		if (rowEnd > rowLen) {
			System.err.println("Not valid submatrix: The row length surpass the matrix length.");
			System.exit(1);   		
		}
		if (colEnd > colLen) {
			System.err.println("Not valid submatrix: The col length surpass the matrix length.");
			System.exit(1);   		
		}

		for (int i = row, rrow = 0; i < rowEnd; ++i, ++rrow) {
			for (int j = col, ccol = 0; j < colEnd; ++j, ++ccol) {
				resultMatrix.complexMatrix[rrow][ccol] = this.complexMatrix[i][j].copy();
			}
		}
		return resultMatrix;
	}

	/**
	 * Returns the sub-matrix formed with rows an columns indicated in the parameters "rows" and "columns".
	 * @param rows A list of integers of the rows to be taken.
	 * @param cols A list of integers of the columns to be taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrix(int[] rows, int[] cols) {
		MatrixComplex subMatrix = new MatrixComplex(rows.length, cols.length);

		for (int row = 0; row < rows.length; ++row)
			for (int col = 0; col < cols.length; ++col)
				subMatrix.complexMatrix[row][col] = this.complexMatrix[rows[row]][cols[col]].copy();
		return subMatrix;
	}

	/**
	 * Returns the sub-matrix formed with rows an columns indicated in the parameters "rows" and "columns" as a list of comma separated values.
	 * @param Srows A string with a list of integers separated by commas of the rows to be taken.
	 * @param Scols A string with a list of integers separated by commas of the columns to be taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrix(String Srows, String Scols) {
		int[] rows, cols;

		if (Srows.length() > 0) rows = intSplit(Srows, ",");
		else {
			rows = new int[1];
			rows[0] = -1;
		}
		if (Scols.length() > 0) cols = intSplit(Scols, ",");
		else {
			cols = new int[1];
			cols[0] = -1;
		}
		return this.subMatrix(rows, cols);
	}

	/**
	 * Returns TRUE if rowIdx has only zeros
	 * @param rowIdx The row to check
	 * @return True if is null
	 */
	public boolean isNullRow(int rowIdx) {
		for (int col = 0; col < this.cols(); ++col) {
			if (!this.getItem(rowIdx, col).isZero()) return false;
			//if (!this.getItem(rowIdx, col).equals(Complex.ZERO)) return false;
		}
		return true;
	}

	/**
	 * Returns TRUE if colIdx has only zeros
	 * @param colIdx The col to check
	 * @return True if is null
	 */
	public boolean isNullCol(int colIdx) {
		for (int row = 0; row < this.rows(); ++row) {
			if (!this.getItem(row, colIdx).isZero()) return false;
			//if (!this.getItem(row, colIdx).equals(Complex.ZERO)) return false;
		}
		return true;
	}

	/**
	 * Removes every row and col which are null
	 * @return The reduced matrix without null rows and cols
	 */
	public MatrixComplex reduce() {
		String cols, rows;
		cols = "";
		rows = "";
		
		for (int col = 0; col < this.cols(); ++col) {
			if (!isNullCol(col)) {
				cols = cols + col + ",";
			}
		}
		for (int row = 0; row < this.rows(); ++row) {
			if (!isNullRow(row)) {
				rows = rows + row + ",";
			}
		}
		if (rows.length() > 0 && rows.substring(rows.length()-1).equals(",")) 
			rows = rows.substring(0, rows.length()-1);
		if (cols.length() > 0 && cols.substring(cols.length()-1).equals(",")) 
			cols = cols.substring(0, cols.length()-1);
		return subMatrix(rows, cols);
	}
	
	/**
	 * Calculates the rank of an array.
	 * @return The rank of the matrix.
	*/
	public int rank() {
		return rank1();
	}
	
	/**
	 * Calculates the rank of an array. It is not reliable for ill-conditioned matrix due to lack of precision
	 * Kept for testing proposes
	 * @return The rank of the matrix.
	 */
	public int rank0() {
		final boolean DEBUG_ON = false;
		int rank = 0, maxRank = this.rows();
		MatrixComplex tempMatrix = this.copy();
		MatrixComplex incrMatrix;
		CombinationNoReps combinat = new CombinationNoReps();
		
		if (this.isNull()) return 0;

		if (this.rows() > this.cols()) {
			tempMatrix = this.transpose();
			maxRank = this.cols();
		}
		
		long[][] rows, cols;
		boolean rankfound;
		for (int order = 1; order <= maxRank; ++order) {
			rankfound = false;
			rows = combinat.getCollection(tempMatrix.rows(), order);
			cols = combinat.getCollection(tempMatrix.cols(), order);
			for (int row = 0; row < rows.length; ++row) {
				int[] rowsi = new int[rows[row].length];
				for (int idx = 0; idx < rowsi.length; ++idx ) rowsi[idx] = (int)rows[row][idx];
				for (int col = 0; col < cols.length; ++col) {
					int[] colsi = new int[cols[col].length];
					for (int idx = 0; idx < rowsi.length; ++idx ) colsi[idx] = (int)cols[col][idx];
					incrMatrix = tempMatrix.subMatrix(rowsi, colsi);
					if (DEBUG_ON) {
						incrMatrix.println("**************** incrMatrix");
						System.out.println("**************** Determinant incrMatrix:" + incrMatrix.determinant());
					}
					if (!incrMatrix.determinant().equals(Complex.ZERO)) {
						++rank;
						rankfound = true;
						break;
					}
				}
				if (rankfound) break;
			}
		}
		return rank;
	}

	/**
	 * Calculates the rank of an array.
	 *  TEST FAILED FIXED
	 * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	 * :                          TEST #2853                           :
	 * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	 * rank1 = 4
	 * rank2 = 3
	 * CMPLXAR: new MatrixComplex("1.00,-1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,1.00,-1.00,1.00;-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,-1.00");
	 * MAXIMA : rank(matrix([1.00,-1.00,1.00,1.00,-1.00,1.00],[1.00,-1.00,1.00,1.00,-1.00,1.00],[-1.00,1.00,1.00,1.00,-1.00,1.00],[1.00,-1.00,-1.00,-1.00,1.00,-1.00],[-1.00,1.00,-1.00,1.00,-1.00,-1.00]))
	 * OCTAVE : rank([1.00,-1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,1.00,-1.00,1.00;-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,-1.00])
	 * WOLFRAM: MatrixRank[{{1.00,-1.00,1.00,1.00,-1.00,1.00},{1.00,-1.00,1.00,1.00,-1.00,1.00},{-1.00,1.00,1.00,1.00,-1.00,1.00},{1.00,-1.00,-1.00,-1.00,1.00,-1.00},{-1.00,1.00,-1.00,1.00,-1.00,-1.00}}]
	 * *****************************************************************
	 * |                          TEST #3648                           |
	 * *****************************************************************
	 * rank1 = 4
	 * rank2 = 3
	 * CMPLXAR: new MatrixComplex("1.00,1.00,-1.00,1.00,1.00,1.00;1.00,1.00,-1.00,1.00,1.00,1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;-1.00,-1.00,1.00,-1.00,-1.00,-1.00;-1.00,-1.00,1.00,-1.00,-1.00,1.00");
	 * MAXIMA : rank(matrix([1.00,1.00,-1.00,1.00,1.00,1.00],[1.00,1.00,-1.00,1.00,1.00,1.00],[-1.00,1.00,-1.00,1.00,-1.00,1.00],[-1.00,-1.00,1.00,-1.00,-1.00,-1.00],[-1.00,-1.00,1.00,-1.00,-1.00,1.00]))
	 * OCTAVE : rank([1.00,1.00,-1.00,1.00,1.00,1.00;1.00,1.00,-1.00,1.00,1.00,1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;-1.00,-1.00,1.00,-1.00,-1.00,-1.00;-1.00,-1.00,1.00,-1.00,-1.00,1.00])
	 * WOLFRAM: MatrixRank[{{1.00,1.00,-1.00,1.00,1.00,1.00},{1.00,1.00,-1.00,1.00,1.00,1.00},{-1.00,1.00,-1.00,1.00,-1.00,1.00},{-1.00,-1.00,1.00,-1.00,-1.00,-1.00},{-1.00,-1.00,1.00,-1.00,-1.00,1.00}}]
	 * *****************************************************************
	 * |                          TEST #7425                           |
	 * *****************************************************************
	 * rank1 = 4
	 * rank2 = 3
	 * CMPLXAR: new MatrixComplex("-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,1.00,-1.00,-1.00;1.00,-1.00,1.00,1.00,-1.00,-1.00");
	 * MAXIMA : rank(matrix([-1.00,1.00,1.00,1.00,-1.00,1.00],[1.00,-1.00,1.00,-1.00,1.00,-1.00],[-1.00,1.00,-1.00,1.00,-1.00,1.00],[1.00,-1.00,-1.00,1.00,-1.00,-1.00],[1.00,-1.00,1.00,1.00,-1.00,-1.00]))
	 * OCTAVE : rank([-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,1.00,-1.00,-1.00;1.00,-1.00,1.00,1.00,-1.00,-1.00])
	 * WOLFRAM: MatrixRank[{{-1.00,1.00,1.00,1.00,-1.00,1.00},{1.00,-1.00,1.00,-1.00,1.00,-1.00},{-1.00,1.00,-1.00,1.00,-1.00,1.00},{1.00,-1.00,-1.00,1.00,-1.00,-1.00},{1.00,-1.00,1.00,1.00,-1.00,-1.00}}]
	 * @return The rank of the matrix.
	 */
	public int rank1() {
		MatrixComplex matrix = this.copy();
		int rank11 = matrix.rank11();
		int rank12 = matrix.rank12();
		return rank11 < rank12 ? rank11 : rank12;
	}
	
	private int rank11() {
		int rank = 0;
		MatrixComplex rankMatrix;
		if (this.isNull()) return 0;
		
		if (this.cols() < this.rows()) rankMatrix = this.transpose();
		else rankMatrix = this.copy();
		
		rankMatrix = rankMatrix.triangleLo().hollow();
		rankMatrix = rankMatrix.triangleLo().hollow();
		rankMatrix = rankMatrix.triangleUp().heap();
		rankMatrix = rankMatrix.triangleUp().heap();
		
		for(int i = 0; i < rankMatrix.rows(); ++i)
			if (!rankMatrix.isNullRow(i)) ++rank;
		return rank;
	}

	private int rank12() {
		int rank = 0;
		MatrixComplex rankMatrix;
		if (this.isNull()) return 0;
		
		if (this.cols() > this.rows()) rankMatrix = this.transpose();
		else rankMatrix = this.copy().triangleUp();
		
		rankMatrix = rankMatrix.triangleUp().hollow();
		rankMatrix = rankMatrix.triangleUp().hollow();
		rankMatrix = rankMatrix.triangleLo().heap();
		rankMatrix = rankMatrix.triangleLo().heap();
		
		for(int i = 0; i < rankMatrix.rows(); ++i)
			if (!rankMatrix.isNullRow(i)) ++rank;
		return rank;
	}
	
	/**
	 * The rank of A is equal the number of non-zero singular values of the characteristic polynomial of A.adjoint()*A
	 * This is method used for other numerical programs
	 * Fail prone due to lack precision
	 * Kept for testing proposes
	 * @return The rank of the matrix.
	 */
	public int rank2() {
		int rank = 0;
		MatrixComplex ATA = this.adjoint().times(this);
		MatrixComplex roots = ATA.charactPoly().solve();
		for (int row = 0; row < roots.rows(); ++row) {
			if (roots.getItem(row, 0).equals(0,0)) continue;
			if (roots.getItem(row, 0).isZero()) continue;
			++rank;
		}
		return rank;
	}
	
	/**
	 * Calculates the nullity of a Vectorial Space.
	 * @return The nullity of the Vectorial Space.
	 */
	public int nullity() {
		return  this.cols() - this.rank();
	}

	/**
	 * Private method. Locates the appropriate row to perform the swap in the triangularization methods.
	 * The appropriate row is one whose column to pivot is not zero.
	 * @param row The index of the start row for the search.
	 * @param col The index of the column you want to pivot.
	 * @return The value of the row found or -1 otherwise.
	 */
	private int locateSwapRow(int row, int col) {
		int i;

		for (i = row; i < this.rows(); ++i)
			if (!this.complexMatrix[i][col].equals(0, 0))
				break;
		return (i == this.rows()) ? -1 : i;
	}

	/**
	 * Checks if the matrix is upper triangular.
	 * @return true if the matrix is upper triangular, false otherwise.
	 */
	public boolean isTriangleUp() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int maxIter = rowLen < colLen ? rowLen : colLen;

		for (int row = 1; row < maxIter; ++row)
			for (int col = 0; col < row; ++col) {
				if (!this.complexMatrix[row][col].equals(0,0)) return false;
			}
		return true;
	}
	
	/**
	 * Sorts the rows of an array so that those rows whose elements are all zeros occupy the highest positions in the array
	 * @return The array with the null rows at the top
	 */
	public MatrixComplex hollow() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int countZeroArray = 0;
		int countNonZeroArray = 0;
		boolean isZero;
		MatrixComplex zeroArray = new MatrixComplex(rowLen, colLen);
		MatrixComplex nonZeroArray = new MatrixComplex(rowLen, colLen);
		MatrixComplex hollow = new MatrixComplex(rowLen, colLen);
		for(int row = 0; row < rowLen; ++row) {
			isZero = true;
			//for(int col = colLen-1; col < row && col > -1; --col) {
			for(int col = 0; col < colLen; ++col) {
				if (!this.complexMatrix[row][col].equals(Complex.ZERO)) {
					isZero = false;
					break;
				}
			}
			if (isZero) zeroArray.complexMatrix[countZeroArray++] = this.complexMatrix[row].clone();
			else nonZeroArray.complexMatrix[countNonZeroArray++] = this.complexMatrix[row].clone(); 
		}
		for(int row = 0; row < countZeroArray; ++row) hollow.complexMatrix[row] = zeroArray.complexMatrix[row].clone();
		for(int row = 0; row < countNonZeroArray; ++row) hollow.complexMatrix[row+countZeroArray] = nonZeroArray.complexMatrix[row].clone();
		
		return hollow;
	}
	
	/**
	 * Sorts the rows of an array so that those rows whose elements are all zeros occupy the lowest positions in the array
	 * @return The array with the null rows at the end
	 */
	public MatrixComplex heap() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int countZeroArray = 0;
		int countNonZeroArray = 0;
		boolean isZero;
		MatrixComplex zeroArray = new MatrixComplex(rowLen, colLen);
		MatrixComplex nonZeroArray = new MatrixComplex(rowLen, colLen);
		MatrixComplex heap = new MatrixComplex(rowLen, colLen);
		for(int row = 0; row < rowLen; ++row) {
			isZero = true;
			for(int col = 0; col < colLen; ++col) {
				if (!this.complexMatrix[row][col].equals(Complex.ZERO)) {
					isZero = false ;
					break;
				}
			}
			if (isZero) zeroArray.complexMatrix[countZeroArray++] = this.complexMatrix[row].clone();
			else nonZeroArray.complexMatrix[countNonZeroArray++] = this.complexMatrix[row].clone();
		}
		for(int row = 0; row < countNonZeroArray; ++row) heap.complexMatrix[row] = nonZeroArray.complexMatrix[row].clone();
		for(int row = 0; row < countZeroArray; ++row) heap.complexMatrix[row+countNonZeroArray] = zeroArray.complexMatrix[row].clone();
		
		return heap;
	}
	
	/**
	 * Checks whether two rows are linear combination or not
	 * @param idrow1 The 1st row
	 * @param idrow2 The 2nd row
	 * @return True if the two rows are linear combination, otherwise false
	 */
	private boolean rowsAreLC(int idrow1, int idrow2) {
		Complex cCoef;
		MatrixComplex row1 = this.getRow(idrow1).copy();
		MatrixComplex row2 = this.getRow(idrow2).copy();		
		for (int col = 0; col < row1.cols()-1; ++col) {
			cCoef = row1.getItem(0, col);
			if (row2.getItem(0, col).equals(Complex.ZERO)) continue;
			else {
				cCoef = cCoef.divides(row2.getItem(0, col));
				if (row1.divides(cCoef).equals(row2)) return true;
			}
		}
		return false;
	}
	
	/**
	 * Calculates the upper triangularization of the matrix.
	 * @return The upper triangularized matrix.
	 */
	public MatrixComplex triangleUp() {
		final boolean DEBUG_ON = false;
		final String METH_NAME = "triangleUp";

		int rowLen = this.rows();
		Complex cCoef = new Complex();
		MatrixComplex auxMatrix = this.clone();

		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			auxMatrix.println(HEADINFO + METH_NAME + ": auxMatrix:");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		if (this.isTriangleUp()) return auxMatrix;

		for (int k = 0; k < rowLen-1; ++k) {
			if (auxMatrix.getItem(k,k).equals(0,0)) {
				int rowSwap = auxMatrix.locateSwapRowUp(k);
				if (rowSwap == -1) {
					continue;
				}
				if (rowSwap != k) auxMatrix.swapRows(k, rowSwap);
			}
			for (int row = k+1; row < rowLen; ++row) {
				/* -------------   DEBUGGING BLOCK   ------------- */
				if (DEBUG_ON) {
					System.out.printf(HEADINFO + METH_NAME + ": auxMatrix.getItem(row, k) = %s\n", auxMatrix.getItem(row, k).toString());
					System.out.printf(HEADINFO + METH_NAME + ": auxMatrix.getItem(k,k) = %s\n", auxMatrix.getItem(k,k).toString());
				}
				/* ------------- END DEBUGGING BLOCK ------------- */

				if (auxMatrix.getItem(k,k).equals(Complex.ZERO)) continue;
				cCoef = auxMatrix.getItem(row, k).divides(auxMatrix.getItem(k,k).opposite());
				auxMatrix.Ftransf(row, k, cCoef);
			}
			if (auxMatrix.isTriangleUp()) break;
		}
		return auxMatrix;
	}
	
	/**
	 * It Upper Triangularize  the matrix by rearranging its rows so that they occupy the place corresponding to their non-zero element on the diagonal  
	 * @return The perfect upper triangularized array
	 */
	public MatrixComplex triangleUpPerfect() {
		boolean DEBUG_ON = false;
		String METH_NAME = "triangleUpPerfect()";
		
		MatrixComplex triUpMatrix = this.triangleUp().heap();
		/* ----------  START DEBUGGING BLOCK   ----------- */
		if (DEBUG_ON) {
			triUpMatrix.println(HEADINFO + METH_NAME + ": triUpMatrix Start");
			System.out.println(HEADINFO + METH_NAME + ": triUpMatrix: " + triUpMatrix.toMatrixComplex());
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		// Clean up linear combinations rows
		int rowLen = triUpMatrix.rows();
		for (int row1 = 0; row1 < rowLen -1; ++row1)
			for (int row2 = row1+1; row2 < rowLen -1; ++row2) {
				if (triUpMatrix.rowsAreLC(row1, row2)) {
					for (int col2 = 0; col2 < this.cols(); ++col2)
						triUpMatrix.setItem(row2, col2, Complex.ZERO);
				}
			}

		int[][] matIndex = new int[this.rows()][2];
		for (int row = 0; row < this.rows(); ++row) {
			matIndex[row][0] = -1;
			matIndex[row][1] = -1;
		}
		
		// Ubicar las filas correctas
		for (int row = 0; row < this.rows(); ++row) {
			if (triUpMatrix.isNullRow(row)) continue;
			for (int col = 0; col < this.cols(); ++col) {
				if (!triUpMatrix.getItem(row, col).isZero()) {
					matIndex[row][0] = row;
					matIndex[row][1] = col;
					break;
				}
			}
		}
		
		//Ubicar las restantes filas
		for (int row = this.rows()-1; row > -1 ; --row) {
			if (matIndex[row][0] != -1) continue;
			{
				for(int matIdxFreeRow = matIndex.length-1; matIdxFreeRow > -1; --matIdxFreeRow) {
					if (matIndex[matIdxFreeRow][0] == -1) {
						matIndex[matIdxFreeRow][0] = row;
						matIndex[matIdxFreeRow][1] = matIdxFreeRow;						
						break;
					}
				}				
			}
		}
				
		//Comprobar que todas las filas están ubicadas
		for (int row = 0; row < this.rows(); ++row) {
			if (matIndex[row][0] == -1) {
				System.err.println("Location failure error.");
				return triUpMatrix;
			}
		}
		
		for (int row = this.rows()-1; row > -1; --row) {
			triUpMatrix.swapRows(matIndex[row][0], matIndex[row][1]);			
		}
		
		/* ----------  START DEBUGGING BLOCK   ----------- */
		if (DEBUG_ON) {
			triUpMatrix.println(HEADINFO + METH_NAME + ": triUpMatrix End");
			System.out.println(HEADINFO + METH_NAME + ": triUpMatrix: " + triUpMatrix.toMatrixComplex());
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		return triUpMatrix;
		
	}
	
	public MatrixComplex triangleUpPerfect_DEPRECATED() {
		boolean DEBUG_ON = false;
		String METH_NAME = "triangleUpPerfect()";
		
		MatrixComplex triUpMatrix = this.triangleUp().heap();
		/* ----------  START DEBUGGING BLOCK   ----------- */
		if (DEBUG_ON) {
			triUpMatrix.println(HEADINFO + METH_NAME + ": triUpMatrix");
			System.out.println(HEADINFO + METH_NAME + ": triUpMatrix: " + triUpMatrix.toMatrixComplex());
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		// Clean up linear combinations rows
		int rowLen = triUpMatrix.rows();
		for (int row1 = 0; row1 < rowLen -1; ++row1)
			for (int row2 = row1+1; row2 < rowLen -1; ++row2) {
				if (triUpMatrix.rowsAreLC(row1, row2)) {
					for (int col2 = 0; col2 < this.cols(); ++col2)
						triUpMatrix.setItem(row2, col2, Complex.ZERO);
				}
			}

		for (int row = 1, rowCount = 0; row < triUpMatrix.rows()-1;) {
			for (int col = row; col < triUpMatrix.cols()-2; ++col) {
				if (triUpMatrix.getItem(row, col).equals(Complex.ZERO) && !triUpMatrix.getItem(row, col+1).equals(Complex.ZERO)) { 
					triUpMatrix.swapRows(row, col+1);
					// Added to exit in case of misplaced rows. Avoid endless loop
					if (++rowCount >= triUpMatrix.rows()) ++row;
					break;
				}
				//if (col > row || col == triUpMatrix.cols()-3 ) {
				if (col == triUpMatrix.cols()-3 || ++rowCount >= triUpMatrix.rows()) {
					++row; 
					break;
				}
			}
		}
		return triUpMatrix;
	}

	/**
	 * Checks if the matrix is lower triangular.
	 * @return true if the matrix is lower triangular, false otherwise.
	 */
	public boolean isTriangleLo() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int maxIter = rowLen < colLen ? rowLen : colLen;

		for (int row = 0; row < rowLen-1; ++row)
			for (int col = row+1; col < maxIter; ++col) {
				if (!this.complexMatrix[row][col].equals(0,0)) return false;
			}
		return true;
	}

	/**
	 * Calculates the lower triangularization of the matrix.
	 * @return The lower triangularized matrix.
	 */
	public MatrixComplex triangleLo(){
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cCoef = new Complex();
		MatrixComplex auxMatrix = this.clone();

		if (this.isTriangleLo()) return auxMatrix;

		//Prepare Matrix
		int upLimit = rowLen < colLen ? colLen : rowLen;
		int loLimit = rowLen > colLen ? colLen : rowLen;
		for (int k = upLimit-1; k >= 0 ; --k) {
			if (k < rowLen && k < colLen && auxMatrix.getItem(k,k).equals(0,0)) {
				int rowSwap = auxMatrix.locateSwapRowDown(k);
				if (rowSwap == -1) {
					continue;
				}
				if (rowSwap != k) auxMatrix.swapRows(k, rowSwap);
			}
			
			for (int row = k-1; row >= 0; --row) {
				if (k >= loLimit || auxMatrix.getItem(k,k).equals(Complex.ZERO)) continue;
				cCoef = auxMatrix.getItem(row,k).divides(auxMatrix.getItem(k,k)).opposite();
				auxMatrix.Ftransf(row, k, cCoef);
			}
		}
		return auxMatrix;
	}
	
	public MatrixComplex triangleLo1() {

		int rowLen = this.rows();
		Complex cCoef = new Complex();
		MatrixComplex auxMatrix = this.clone();

		if (this.isTriangleLo()) return auxMatrix;

		for (int k = rowLen-1; k < 0; --k) {
			if (auxMatrix.getItem(k,k).equals(Complex.ZERO)) {
				int rowSwap = auxMatrix.locateSwapRowDown(k);
				if (rowSwap == -1) {
					continue;
				}
				if (rowSwap != k) auxMatrix.swapRows(k, rowSwap);
			}
			for (int row = k+1; row < 0; --row) {
				if (auxMatrix.getItem(k,k).equals(Complex.ZERO)) continue;
				cCoef = auxMatrix.getItem(row, k).divides(auxMatrix.getItem(k,k).opposite());
				auxMatrix.Ftransf(row, k, cCoef);
			}
			if (auxMatrix.isTriangleLo()) break;
		}
		return auxMatrix;
	}

	/**
	 * Indicates if the array is square or nor
	 * @return true for square matrix, false otherwise
	 */
	public boolean isSquare() {
		return (this.rows() == this.cols());
	}

	/*
	 * https://es.wikipedia.org/wiki/Proceso_de_ortogonalizaci%C3%B3n_de_Gram-Schmidt
	 * Proceso de ortogonalización de Gram-Schmidt con el método de Gauss
	 */
	/**
	 * Gram-Schmidt orthogonalization process via Gaussian elimination. 
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtGauss() {
		final boolean DEBUG_ON = false; 
		MatrixComplex auxMatrix = this.times(this.adjoint());
		
		MatrixComplex augmentedMatrix = auxMatrix.copy();
		augmentedMatrix = augmentedMatrix.augment(this);

		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			augmentedMatrix.println(HEADINFO + "augmentedMatrix");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */
		
		augmentedMatrix = augmentedMatrix.triangle();
		
		MatrixComplex gramSchmidtMatrix = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < gramSchmidtMatrix.rows(); ++row) {
			for (int col = 0; col < gramSchmidtMatrix.cols(); ++col) {
				gramSchmidtMatrix.setItem(row, col, augmentedMatrix.getItem(row, col+this.cols()));
			}
		}
		
		return gramSchmidtMatrix.transpose();
	}

	/**
	 * Gram-Schmidt orthogonalization process.
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * It uses the MatrixComplex dotprod column oriented
	 * Use the Column calc for the dotprod. Otherwise dotprod needs work with transposed
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidt() {
		// Because we operate with cols
		MatrixComplex thsiTansposed = this.transpose();
		int rowLen = thsiTansposed.rows();
		int colLen = thsiTansposed.cols();

		colLen = colLen > rowLen ? rowLen : colLen;

		MatrixComplex gramschmidt = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < colLen; ++i) {
			x.copyCol(0, thsiTansposed, i);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidt, j);
				g = g.minus(v.times((g.dotprod(v)).divides(v.dotprod(v)))) ;				
			}
			gramschmidt.copyCol(i, g, 0);
		}
		return gramschmidt.transpose();
	}

	/**
	 * Gram-Schmidt Full orthogonalization process. Full means that the not included vectors of the base are randomly generated with integers between 0 and 9.
	 * The calculated orthogonal matrix is extended to the full dimension of the matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtFull() {
		// Because we operate with cols
		MatrixComplex thsiTansposed = this.transpose();
		int rowLen = thsiTansposed.rows();
		int colLen = thsiTansposed.cols();

		colLen = colLen > rowLen ? rowLen : colLen;

		MatrixComplex gramschmidtF = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < colLen; ++i) {
			if (i < colLen) x.copyCol(0, thsiTansposed, i);
			else x.initMatrixRandomInteger(9);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidtF, j);
				g = g.minus(v.times((x.dotprod(v)).divides(v.dotprod(v)))) ;				
			}
			gramschmidtF.copyCol(i, g, 0);
		}
		return gramschmidtF.transpose();
	}

	/**
	 * Gram-Schmidt Full Modified orthogonalization process. Modified Algorithm (http://www.ehu.eus/izaballa/Ana_Matr/Apuntes/lec6.pdf)
	 * The calculated orthogonal matrix is extended to the full dimension of the matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtMFull() {
		// Because we operate with cols
		MatrixComplex thsiTansposed = this.transpose();
		int rowLen = thsiTansposed.rows();
		int colLen = thsiTansposed.cols();

		colLen = colLen > rowLen ? rowLen : colLen;

		MatrixComplex gramschmidtF = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		gramschmidtF = thsiTansposed.copy();
		for (int i = 0; i < colLen; ++i) {
			if (i < colLen) x.copyCol(0, gramschmidtF, i);
			else x.initMatrixRandomInteger(9);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidtF, j);
				g = g.minus(v.times((g.dotprod(v)).divides(v.dotprod(v)))) ;				
			}
			gramschmidtF.copyCol(i, g, 0);
		}
		return gramschmidtF.transpose();
	}

	/**
	 * Gram-Schmidt Modified orthogonalization process. Modified Algorithm (http://www.ehu.eus/izaballa/Ana_Matr/Apuntes/lec6.pdf)
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * @return The matrix with the orthogonal basis that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtM() {
		// Because we operate with cols
		MatrixComplex thsiTansposed = this.transpose();
		int rowLen = thsiTansposed.rows();
		int colLen = thsiTansposed.cols();

		colLen = colLen > rowLen ? rowLen : colLen;

		MatrixComplex gramschmidt = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		gramschmidt = thsiTansposed.copy();
		for (int i = 0; i < colLen; ++i) {
			x.copyCol(0, gramschmidt, i);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidt, j);
				g = g.minus(v.times((g.dotprod(v)).divides(v.dotprod(v)))) ;				
			}
			gramschmidt.copyCol(i, g, 0);
		}
		return gramschmidt.transpose();
	}

	/**
	 * Shortcut to normalize method.
	 * @return The normalized matrix.
	 */
	public MatrixComplex normalize() {
		return normalizeByRows();
	}

	/**
	 * Normalizes the matrix by columns using the Euclidean norm.
	 * @return The normalized matrix.
	 */
	public MatrixComplex normalizeByCols() {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex norm = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);

		for (int col = 0; col < colLen; ++col) {
			v.copyCol(0, this, col);
			v = v.divides(v.euc_norm());
			norm.copyCol(col, v, 0);
		}
		return norm;
	}

	/**
	 * Normalizes the matrix by columns using the Euclidean norm.
	 * @return The normalized matrix.
	 */
	public MatrixComplex normalizeByRows() {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex norm = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(1, colLen);

		for (int row = 0; row < rowLen; ++row) {
			v.copyRow(0, this, row);
			v = v.divides(v.euc_norm());
			norm.copyRow(row, v, 0);
		}
		return norm;
	}

	/**
	 * Private method. The dot product used in some other methods (gramSchmidt)
	 * This is the dotprod for MatrixComplex class. "Vector" class has the public method dotprod used in vector arithmetic.
	 * @param cMatrix A index to the row to make the dot product.
	 * @return The Complex result of the dot product.
	 */
	private Complex dotprod(MatrixComplex cMatrix) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowLenC = cMatrix.rows();
		int colLenC = cMatrix.cols();

		if (colLen != 1 || colLenC != 1) {
			System.err.println(HEADINFO + "dotprod: " + "One of the componentes isn't a row/col");
		}

		if (rowLen != rowLenC) {
			System.err.println(HEADINFO + "dotprod: " + "row col of different size");
		}

		return cMatrix.adjoint().times(this).complexMatrix[0][0];
	}

	public MatrixComplex tensorprod(MatrixComplex matrix) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowLenC = matrix.rows();
		int colLenC = matrix.cols();

		if (rowLen != rowLenC) {
			System.err.println(HEADINFO + "tensorialprod: " + "Tensors of different size");
		}

		return this.adjoint().times(matrix);
	}

	public MatrixComplex kroneckerprod(MatrixComplex matrix) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowLenC = matrix.rows();
		int colLenC = matrix.cols();

		if (colLen != colLenC) {
			System.err.println(HEADINFO + "tensorialprod: " + "Tensors of different size");
		}

		return matrix.adjoint().times(this);
	}
	
	/**
	 * Calulates the Kernel of a base
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The kernel vector components
	 */
	public MatrixComplex kernel(Complex lambda) {
		MatrixComplex kernel = new MatrixComplex();
		if (this.rows() != this.cols()) {
			System.err.println("The matrix has to be square.");
			return kernel;
		}
		Syseq newSysEq = new Syseq(this.augment());
		return newSysEq.solve(lambda);
	}
	
	/**
	 * Calulates the Kernel of a base using Complex.ONE as seed
	 * @return The kernel vector components
	 */
	public MatrixComplex kernel() {
		return this.kernel(Complex.ONE);
	}
	
	/**
	 * Shortcut to kernel() method
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The kernel vector components
	 */
	public MatrixComplex ker(Complex lambda) {
		return kernel(lambda);
	}
	
	/**
	 * Shortcut to kernel() method
	 * @return The kernel vector components
	 */
	public MatrixComplex ker() {
		return kernel();
	}

	/*
	 * ***********************************************
	 * CHARACTERISTIC POLYNOMIAL
	 * ***********************************************
	 */
	
	/**
	 * Returns the characteristic polynomial of the matrix.
	 * The characteristic polynomial of a square matrix is a polynomial which is invariant under matrix similarity and has the eigenvalues as roots. It has the determinant and the trace of the matrix as coefficients. The characteristic polynomial of an endomorphism of vector spaces of finite dimension is the characteristic polynomial of the matrix of the endomorphism over any base; it does not depend on the choice of a basis
	 * @return The characteristic polynomial
	 */
	public Polynom charactPoly() {
		int rowLen = this.rows();
		int colLen = this.cols();
		Polynom charactPoly = new Polynom(colLen);

		if (rowLen != colLen) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}

		for (int order = 0; order <= colLen; ++order) {
			switch (order) {
				case 0: 
					charactPoly.complexMatrix[0][colLen-order].setComplexPol(1, 0); 
					break;
				case 1: 
					charactPoly.complexMatrix[0][colLen-order] = this.trace().opposite(); 
					break;
				default: 
					charactPoly.complexMatrix[0][colLen-order] = this.coefCP(order).times(Math.pow(-1, order)); 
					break;
			}
		}
		//this.print(charactPolyMatrix.complexMatrix);
		return (colLen % 2) == 0 ? charactPoly : charactPoly.opposite();
	}

	/**
	 * Returns a new augmented matrix.
	 * Copy the original matrix and add a column to zeros.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		MatrixComplex extendedMatrix = new MatrixComplex(rowLen, colLen+1);
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				extendedMatrix.complexMatrix[row][col] = this.complexMatrix[row][col];
			}
			extendedMatrix.complexMatrix[row][col].setComplexPol(0,0);
		}
		return extendedMatrix;
	}

	public MatrixComplex augment(int numCols) {
		MatrixComplex extendedMatrix = this;
		for (int iter = 0; iter < numCols; ++iter) {
			extendedMatrix = extendedMatrix.augment();
		}
		return extendedMatrix;
	}

	/**
	 * Shortcut to the default augment method
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment(MatrixComplex interms) {
		return augment2(interms);
	}
	
	/**
	 * DEPRECATED Returns a new augmented array with the FIRST column of terms.
	 * DEPRECATED Copies the original matrix and add the FIRST column "interms".
	 * DEPRECATED Left only for fail recovery of augment1()
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment1(MatrixComplex interms) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		MatrixComplex extendedMatrix = new MatrixComplex(rowLen, colLen+1);
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				extendedMatrix.complexMatrix[row][col] = this.complexMatrix[row][col];
			}
			extendedMatrix.complexMatrix[row][col] = interms.complexMatrix[row][0];
		}
		return extendedMatrix;
	}

	/**
	 * Returns a new augmented array with ALL the columns of terms.
	 * Copies the original matrix and add the ALL the columns of "interms".
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment2(MatrixComplex interms) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		MatrixComplex extendedMatrix = new MatrixComplex(rowLen, colLen+interms.cols());
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				extendedMatrix.complexMatrix[row][col] = this.complexMatrix[row][col];
			}
			for (int incol = 0; incol < interms.cols(); ++incol) {
				extendedMatrix.complexMatrix[row][col+incol] = interms.complexMatrix[row][incol];
			}
		}
		return extendedMatrix;
	}

	/**
	 * Returns a new array with the terms of the unknowns.
	 * Copy the original matrix and remove the column "interms".
	 * @return The augmented matrix.
	 */
	public MatrixComplex unkMatrix() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		MatrixComplex unkMatrix = new MatrixComplex(rowLen, colLen-1);
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen-1; ++col) {
				unkMatrix.complexMatrix[row][col] = this.complexMatrix[row][col];
			}
		}
		return unkMatrix;
	}
	
	/**
	 * Private method. Identifies if a row is used to construct the cofactor's matrix (included for convenience)
	 * @param listRows List with the rows to check.
	 * @param item The item to look for.
	 * @return true if found, otherwise false.
	 */
	private boolean foundItem(int[] listRows, int item) {
		for (int i = 0; i < listRows.length; ++i) {
			if (listRows[i] == item) 
				return true;
		}
		return false;
	}

	/**
	 * Private method. Identifies if a row is used to construct the cofactor's matrix.
	 * @param listRows List with the rows to check.
	 * @param item The item to look for.
	 * @return true if NOT found, otherwise false.
	 */    
	private boolean notFoundItem(int[] listRows, int item) {
		for (int i = 0; i < listRows.length; ++i) {
			if (listRows[i] == item) 
				return false;
		}
		return true;
	}

	/**
	 * Calculates the new cofactor matrix.
	 * @return The new cofactor matrix.
	 */
	public MatrixComplex cofactor() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;
		MatrixComplex cofactor = new MatrixComplex(rowLen);
		
		if (rowLen != colLen) {
			System.err.println("Not valid matrix: The matrix has to be square.");
			System.exit(1);
		}
		
		for (row = 0; row < rowLen; ++row)
			for (col = 0; col < colLen; ++col) {
				cofactor.complexMatrix[row][col] = this.cofactors(row, col).determinant().times(((col+row)&1)==0?1:-1);
			}
		return cofactor;
	}
	
	/**
	 * Returns a new matrix of cofactors.
	 * @param includedRows The list with the indexes of the rows included in the new matrix.
	 * @return The new matrix of cofactors.
	 */
	public MatrixComplex cofactors(int[] includedRows) {
		int rowLen = this.rows();
		int order = includedRows.length;

		MatrixComplex cofactor = new MatrixComplex(order, order);
		for (int row = 0, rrow = 0; row < rowLen; ++row) {
			if (notFoundItem(includedRows, row)) 
				continue;
			for (int col = 0, ccol = 0; col < rowLen; ++col) {
				if (notFoundItem(includedRows, col)) 
					continue;
				cofactor.complexMatrix[rrow][ccol++] = this.complexMatrix[row][col];
			}
			++rrow;
		}
		return cofactor;
	}

	/**
	 * Private method. Bubblesort method for sort a list of integers. 
	 * Used in cofactors(String includedRowsList) for sorting the row list.
	 * @param in The list of integers to sort.
	 * @return The sorted list.
	 */
	private static int[] bubbleSort(int[] in) {
		int n = in.length;
		int c, d, swap;
		int matrix[] = new int[in.length];
		for (c = 0; c < n; ++c) matrix[c] = in[c];

		for (c = 0; c < n-1; ++c) {
			for (d = 0; d < n-c-1; ++d) {
				if (matrix[d] > matrix[d+1]) { /* For descending order use < */
					swap 		= matrix[d];
					matrix[d]   = matrix[d+1];
					matrix[d+1] = swap;
				}
			}
		}
		return matrix;
	}

	/**
	 * Returns the cofactors' matrix for a included row list given in string format.
	 * @param includedRowsList The indexes' list of the rows included in the new matrix as a comma separated string.
	 * @return The new matrix of cofactors.
	 */
	public MatrixComplex cofactors(String includedRowsList) {
		String[] includedRowsTextItems = includedRowsList.split(",");
		int incRows[] = new int[includedRowsTextItems.length];    	
		for (int i = 0; i < incRows.length; ++i) incRows[i] = Integer.parseInt(includedRowsTextItems[i]);
		incRows = bubbleSort(incRows);
		return this.cofactors(incRows);
	}

	/**
	 * Calculates the determinant of the cofactors' array generated with the included rows (minor)
	 * The determinant of some smaller square matrix, cut down from this by removing one or more of its rows.
	 * @param includedRows Array with the indexes of the included rows to generate the cofactors' matrix.
	 * @return The result of the determinant.
	 */
	public Complex minor(int[] includedRows) {
		return cofactors(includedRows).determinant();
	}

	/**
	 * Private method. Returns the exact list of rows included to generate the cofactors' matrix.
	 * Used in coefCP(int order)
	 * @param order The order of the cofactors' matrix.
	 * @param v The array of the rows included.
	 * @return Aa array with the indexes of the included rows
	 */
	private int[] includedRows(int order, int[] v) {
		int includedRows[] = new int[order];		
		int i;

		for (i = 0; i < order; ++i) includedRows[i] = v[i];
		return includedRows;
	}

	/**
	 * Returns the coefficient of order "order" of the characteristic polynomial from an array
	 * @param order The order of the coefficient
	 * @return The coefficient of the polynomial
	 */
	public Complex coefCP(int order) {
		int grade = this.rows();
		Complex coefCP = new Complex();
		int[] includedRows;
		int i, j;
		int v[] = new int[grade];

		for (i = 0; i < grade; ++i) v[i]=i;
		includedRows = includedRows(order, v);
		coefCP = coefCP.plus(minor(includedRows));
		while (true) {
			i = order-1;
			while (v[i] == grade-order+i && --i >= 0);
			if (i < 0) break;
			v[i] += 1;
			for (j = i+1; j < order; ++j) v[j] = v[i]+j-i;
			includedRows = includedRows(order, v);
			coefCP = coefCP.plus(minor(includedRows));
		}
		return coefCP;
	}

	/**
	 * Constant to define the decreasing sort
	 */
	private final static int DECREASING = 0;

	/**
	 * Constant to define the increasing sort
	 */
	private final static int INCREASING = 1;

	/**
	 * Compare method parameterized by sort
	 * @param cVal1 Complex Value1. The method uses cVal1.cre()
	 * @param cVal2 Complex Value2. The method uses cVal1.cre()
	 * @param sort the type of rule to sort. From Max to min, decreasing sort = DECREASING = 0. From min to Max, increasing sort = INCREASING = 1
	 * @return the comparison result
	 */
	private static boolean compare(Complex cVal1, Complex cVal2, int sort) {
		//int signif = Complex.significative()-1;
		switch (sort) {
		//case DECREASING: return Complex.round(cVal1.cre(), signif) >= Complex.round(cVal2.cre(), signif);
		//case INCREASING: return Complex.round(cVal1.cre(), signif) <= Complex.round(cVal2.cre(), signif);
		case DECREASING: return cVal1.cre() >= cVal2.cre();
		case INCREASING: return cVal1.cre() <= cVal2.cre();
		}
		return cVal1.cre() >= cVal2.cre();
	}

	/**
	 * Sorts using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param col Index of the column to order.
	 * @param izq Pivot index on the left.
	 * @param der Pivot index on the right.
	 * @param order the type of order to sort. From Max to min, decreasing order = DECREASING = 0. From min to Max, increasing order = INCREASING = 1
	 */
	private void quicksort(int col, int izq, int der, int order) {
		int colLen = this.cols();
		int i = izq; 									// i realiza la búsqueda de izquierda a derecha
		int j = der; 									// j realiza la búsqueda de derecha a izquierda

		MatrixComplex aux = new MatrixComplex(1, colLen);
		MatrixComplex pivote = new MatrixComplex(1, colLen);
		pivote.complexMatrix[0] = this.complexMatrix[izq].clone();	// tomamos primer elemento como pivote

		while (i < j) {            																		// mientras no se crucen las búsquedas
			while (i < j &&  compare(this.complexMatrix[i][col], pivote.complexMatrix[0][col], order)) ++i;	// busca elemento mayor que pivote
			while (!compare(this.complexMatrix[j][col], pivote.complexMatrix[0][col], order)) --j;			// busca elemento menor que pivote
			if (i < j) {                      									// si no se han cruzado                      
				aux.complexMatrix[0] = this.complexMatrix[i].clone();			// los intercambia
				this.complexMatrix[i] = this.complexMatrix[j].clone();
				this.complexMatrix[j] = aux.complexMatrix[0].clone();
			}
		}
		this.complexMatrix[izq] = this.complexMatrix[j].clone(); 		// se coloca el pivote en su lugar de forma que tendremos
		this.complexMatrix[j] = pivote.complexMatrix[0].clone(); 	// los menores a su izquierda y los mayores a su derecha
		if (izq < j-1)
			this.quicksort(col, izq, j-1, order); // ordenamos submatrix izquierdo
		if (j+1 < der)
			this.quicksort(col, j+1, der, order); // ordenamos submatrix derecho
	}

	/**
	 * Sorts from maximum to minimum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param col Index of the column to order.
	 */   
	public void quicksort(int col) {
		this.quicksort(col,0,this.rows()-1, DECREASING);
	}

	/**
	 * Sorts from minimum to maximum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param col Index of the column to order.
	 */   
	public void quicksortup(int col) {
		this.quicksort(col,0,this.rows()-1, INCREASING);
	}

	/**
	 * Calculates the Hermitian matrix (or self-adjoint matrix).
	 * The Hermitian matrix (or self-adjoint matrix) is a complex square matrix that is equal to its own conjugate transpose—that is, the element in the i-th row and j-th column is equal to the complex conjugate of the element in the j-th row and i-th column, for all indices i and j.
	 * Hermitian matrices can be understood as the complex extension of real symmetric matrices.
	 * @return The Hermitian matrix.
	 */
	public MatrixComplex hermitian() {
		return (this.plus(this.adjoint()));
	}

	/**
	 * Calculates the skew-Hermitian or antihermitian.
	 * The skew-Hermitian or antihermitian if its conjugate transpose is equal to the original matrix, with all the entries being of opposite sign.[1] That is, the matrix A is skew-Hermitian if it satisfies the relation.
	 * @return The skew-Hermitian matrix.
	 */
	public MatrixComplex skewHermitian() {
		return (this.minus(this.adjoint()));
	}

	/**
	 * Calculates the commutator between two arrays.
	 * The commutator gives an indication of the extent to which a certain binary operation fails to be commutative.
	 * @param B the second array.
	 * @return The commutator array.
	 */
	public MatrixComplex commutator(MatrixComplex B) {
		return (this.times(B)).minus(B.times(this));
	}

	/**
	 * Calculates the anticommutator between two arrays.
	 * @param B the second array.
	 * @return The anticommutator array.
	 */
	public MatrixComplex anticommutator(MatrixComplex B) {
		return (this.times(B)).plus(B.times(this));
	}

	/*
	 * ***********************************************
	 * LINE EQUATION
	 * ***********************************************
	 */

	/**
	 * Calculates the General Form equation A0*x0 + A1*x1 +...+ An-1*xn-1 = An from a point and a normal vector (direction vector for 2 dimensions)
	 * @param point The point of the line or the surface
	 * @param vector The direction vector of the line or the normal vector of the surface
	 * @return The general form equation coefficients A0*x0 + A1*x1 +...+ An-1*xn-1 = An
	 */
	public MatrixComplex pointVector(MatrixComplex point, MatrixComplex vector) {
		int colLen = point.cols();
		int col;
		MatrixComplex lineEq = new MatrixComplex(1, colLen+1);
		for (col = 0; col < colLen; ++col)
			lineEq.complexMatrix[0][col] = vector.complexMatrix[0][col];
		lineEq.complexMatrix[0][col] = point.times(vector.transpose()).complexMatrix[0][0];
		return lineEq;
	}

	/**
	 * Calculates the General Form equation A0*x0 + A1*x1 +...+ An-1*xn-1 = An from a point and a normal vector (direction vector for 2 dimensions)
	 * @param point The point of the line or the surface as a string separated by commas
	 * @param vector The direction vector of the line or the normal vector of the surface as a string separated by commas
	 * @return The general form equation coefficients A0*x0 + A1*x1 +...+ An-1*xn-1 = An
	 */
	public MatrixComplex pointVector(String point, String vector) {
		MatrixComplex aPoint = new MatrixComplex();
		MatrixComplex aVector = new MatrixComplex();		

		aPoint = new MatrixComplex(point);
		aVector = new MatrixComplex(vector);
		return this.pointVector(aPoint, aVector);
	}

	/**
	 * Calculates the distance between a line or a surface expressed in general form and a point
	 * @param point expressed as a matrix complex
	 * @return the distance
	 */
	public double distance(MatrixComplex point) {
		Complex d1 = new Complex();
		int col;

		for (col = 0; col < point.cols(); ++col)
			d1 = d1.plus(this.complexMatrix[0][col].times(point.complexMatrix[0][col]));
		d1 = d1.minus(this.complexMatrix[0][col]);
		return d1.mod()/this.coefMatrix().norm();
	}

	/**
	 * Calculates the distance between a line or a surface expressed in general form and a point
	 * @param spoint The point expressed as a string separated by commas
	 * @return the distance
	 */
	public double distance(String spoint) {
		MatrixComplex point = new MatrixComplex(spoint);
		return this.distance(point);
	}

	/*
	 * ***********************************************
	 * ELEMENTARY ROW TRANSFORMATIONS
	 * ***********************************************
	 */

	/**
	 * Transformation FSwapff(i,j) it swaps rows i and j of this matrix A ∈ C m × n and returns the result into a new Matrix.
	 * @param rowi Index of row i.
	 * @param rowj Index of row j.
	 * @return The transformed matrix.
	 */
	public MatrixComplex FSwapff(int rowi, int rowj) {
		int rowLen = this.rows();
		MatrixComplex Ftrans = new MatrixComplex(rowLen);
		MatrixComplex pivot = new MatrixComplex(1, rowLen);

		Ftrans.initMatrixDiag(1, 0);
		pivot.complexMatrix[0] = Ftrans.complexMatrix[rowi];
		Ftrans.complexMatrix[rowi] = Ftrans.complexMatrix[rowj];
		Ftrans.complexMatrix[rowj] = pivot.complexMatrix[0];
		Ftrans = Ftrans.times(this);
		return Ftrans;
	}

	/**
	 * Transformation FSwapf(i,j) it swaps rows i and j of this matrix A ∈ C m × n.
	 * @param rowi Index of row i.
	 * @param rowj Index of row j.
	 */
	public void FSwapf(int rowi, int rowj) {
		int rowLen = this.rows();
		MatrixComplex pivot = new MatrixComplex(1, rowLen);

		pivot.complexMatrix[0] = this.complexMatrix[rowi];
		this.complexMatrix[rowi] = this.complexMatrix[rowj];
		this.complexMatrix[rowj] = pivot.complexMatrix[0];
		this.mSign *= -1;
	}

	/**
	 * Transformation Ftransff(i,α) multiplies row i of this matrix A ∈ C m × n by a number α and returns the result into a new Matrix.
	 * @param row Index of row i.
	 * @param cNum The complex number α.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int row, Complex cNum) {
		int rowLen = this.rows();
		MatrixComplex Ftrans = new MatrixComplex(rowLen);

		Ftrans.initMatrixDiag(1, 0);
		Ftrans.setItem(row, row, cNum);
		Ftrans = Ftrans.times(this);
		return Ftrans;
	}

	/**
	 * Transformation Ftransff(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α in text format and returns the result into a new Matrix.
	 * @param row The index of row i.
	 * @param sNum The complex number α in text format.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int row, String sNum) {
		Complex cNum = new Complex(sNum);
		return this.Ftransff(row, cNum);
	}

	/**
	 * Transformation Ftransff(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α and returns the result into a new Matrix.
	 * @param row The index of row i.
	 * @param dNum The number α.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int row, double dNum) {
		return Ftransff(row, new Complex(dNum,0));
	}

	/**
	 * Transformation Ftransf(i,α) multiplies row i of this matrix A ∈ C m × n by a number α.
	 * @param row Index of row i.
	 * @param cNum The complex number α.
	 */
	public void Ftransf(int row, Complex cNum) {
		int colLen = this.cols();
		
		for(int col = 0; col < colLen; ++col)
			this.setItem(row, col, this.getItem(row, col).times(cNum));
	}

	/**
	 * Transformation Ftransf(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α in text format.
	 * @param row The index of row i.
	 * @param sNum The complex number α in text format.
	 */
	public void Ftransf(int row, String sNum) {
		Complex cNum = new Complex(sNum);
		this.Ftransf(row, cNum);
	}

	/**
	 * Transformation Ftransf(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α in text format.
	 * @param row The index of row i.
	 * @param dNum The number α.
	 */
	public void Ftransf(int row, double dNum) {
		Ftransf(row, new Complex(dNum,0));
	}

	/**
	 * Transformation Ftransff(i,j,α) Adds to row i of a matrix A ∈ C m × n its row j multiplied by the complex α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param cNum The complex number α.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int rowi, int rowj, Complex cNum) {
		int rowLen = this.rows();
		MatrixComplex Ftrans = new MatrixComplex(rowLen);

		Ftrans.initMatrixDiag(1, 0);
		Ftrans.setItem(rowi,rowj,cNum);
		Ftrans = Ftrans.times(this);
		return Ftrans;
	}

	/**
	 * Transformation Ftransff(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α != 0 in string format.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param sNum The complex number α in text format.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int rowi, int rowj, String sNum) {
		Complex cNum = new Complex(sNum);    	
		return this.Ftransff(rowi, rowj, cNum);
	}    

	/**
	 * Transformation Ftransff(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param dNum The number α. 
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int rowi, int rowj, double dNum) {
		return this.Ftransff(rowi, rowj, new Complex(dNum, 0));
	}

	/**
	 * Transformation Ftransf(i,j,α) Adds to row i of a matrix A ∈ C m × n its row j multiplied by the complex α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param cNum The complex number α.
	 */
	public void Ftransf(int rowi, int rowj, Complex cNum) {
		int colLen = this.cols();
		
		for (int col = 0; col < colLen; ++col)
			this.setItem(rowi, col, this.getItem(rowi, col).plus(this.getItem(rowj, col).times(cNum)));
	}

	/**
	 * Transformation Ftransf(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α in string format.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param sNum The complex number α in text format.
	 */
	public void Ftransf(int rowi, int rowj, String sNum) {
		Complex cNum = new Complex(sNum);    	
		this.Ftransf(rowi, rowj, cNum);
	}    

	/**
	 * Transformation F(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param dNum The number α
	 */
	public void Ftransf(int rowi, int rowj, double dNum) {
		this.Ftransf(rowi, rowj, new Complex(dNum, 0));
	}


	/*
	 * ***********************************************
	 * GENERAL PORPOUSE METHODS
	 * ***********************************************
	 */

	/**
	 * Private method to plot a table
	 * Used to bring more info at debug
	 * @param dataTable the table to plot
	 */
	private void doPlot(String Title, double[][] dataTable) {
		if (!__DOPLOT__) return;
		//Plot the data
		JavaPlot p = new JavaPlot();
		p.setTitle(Title);
		p.addPlot(dataTable);
		p.set("zeroaxis", "");
		p.set("style","data lines");
		//p.set("style", setLineStyle(lineStyle));
		p.set("grid","");
		p.plot();
	}



}
