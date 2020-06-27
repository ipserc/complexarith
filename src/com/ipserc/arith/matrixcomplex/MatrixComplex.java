package com.ipserc.arith.matrixcomplex;

import java.lang.Double;
import java.lang.Math;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.polynom.*;
import com.ipserc.combinatoric.*;

public class MatrixComplex {
	public Complex[][] complexMatrix;

	private final static String HEADINFO = "MatrixComplex --- INFO:";
	private final static String VERSION = "1.0 (2020_0627_1130)";

	private int mSign = 1; //Tracks the correct sign in the determinants calculated through triangulation (Chio's rule)

	/*
	 * 
	 */
	public void version() {
		System.out.println("VERSION:" + VERSION); 
	}
	
	/*
	 * 	CONSTRUCTORS 
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
	 * Returns the complex matrix object of dimensions rowLen x colLen.
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
	 * Returns the complex matrix object from a string representation of the matrix where rows are separated with ";", columns are separated with ",".
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
	 * INITIALIZERS
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

	/**
	 * Métodos para crear Bases Algebraícas
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
	 * INTERNAL FUNCTIONS
	 */

	/**
	 * Returns the number of rows of the array
	 * @return The number of rows
	 */
	public int rows() {
		return this.complexMatrix.length;
	}
	
	/**
	 * Returns the number of columns of the array
	 * @return The number of columns
	 */
	public int cols() {
		return this.complexMatrix[0].length;
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
	 * Sets the item(row, col) of the array to a specific complex number
	 * @param row
	 * @param col
	 * @param numC
	 */
	public void setItem(int row, int col, Complex numC) {
		this.complexMatrix[row][col].setComplexRec(numC.rep(), numC.imp());
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

	/*
	 * COPY & REPLICATION
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
	 * PRINTING
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
	private void printCM() {
		int rowLen = this.rows();
		int colLen = this.cols();
		for (int row = 0; row < rowLen; ++row) {
			System.out.print("[ ");
			for (int col = 0; col < colLen; ++col) {
				System.out.print(this.complexMatrix[row][col]);
				System.out.print(col == colLen-1 ? " ]" : " , ");
			}
			System.out.print(row == rowLen-1 ? "" : "\n");
		}
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
	//		 [5.0 + 7.0*%i,2.0 + 4.0*%i,-3.0 + 4.0*%i,6.0 + 6.0*%i],
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
			matrixMaxima += (row == rowLen-1 ? ");" : ",");
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
		return matrixWolfram;
	}

	/**
	 * Returns a string with the array expression in the format used by Mathlab.
	 * @return The string with the array in Mathlab format.
	 */
	public String toMathlab() {
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
	 * Returns a string with the array expression in the format used by Matrix Complex. 
	 * @return The string with the array in MatrixComplex format.
	 */
	public String toMatrixComplex() {
		int rowLen = this.rows();
		int colLen = this.cols();
		String matrixMatrixComplex = new String();
		int row, col;

		matrixMatrixComplex = "new MatrixComplex(\"";
		for (row = 0; row < rowLen-1; ++row) {
			for (col = 0; col < colLen; ++col) {
				matrixMatrixComplex += this.complexMatrix[row][col].toString();
				matrixMatrixComplex += (col == colLen-1 ? ";" : ",");
			}
		}
		for (col = 0; col < colLen; ++col) {
			matrixMatrixComplex += this.complexMatrix[row][col].toString();
			matrixMatrixComplex += (col == colLen-1 ? "\")" : ",");
		}
		return matrixMatrixComplex;
	}

	/*
	 * COLS & ROWS OPERATIONS
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

		pivot.complexMatrix[0] = this.complexMatrix[row1];
		this.complexMatrix[row1] = this.complexMatrix[row2];
		this.complexMatrix[row2] = pivot.complexMatrix[0];
		this.mSign = -this.mSign;
		//	System.out.println("swapRows this.mSign="+this.mSign);
	}

	/**
	 * Swaps the rows "row1" and "row2" in the array and updates the sign variable to correctly evaluate the determinant.
	 * Performs the swap by copying the values of the columns
	 * @param row1 Row to swap.
	 * @param row2 Row to swap.
	 */
	public void swapRowsL(int row1, int row2) {
		Complex pivot ;

		if (row1 == row2) 
			return;
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
	 * Copies into "origMatrix" the value of the "copyCol" column in the "destCol" column.
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
	 * Copies into "origMatrix" the value of the "copyRow" row in the "destRow" row.
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
	 * Compares two matrices and return the result of the comparing
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
	 * Compares two matrices in reduced mode and return the result of the comparing
	 * @param cMatrix The matrix to compare with
	 * @return The result of the comparing
	 */
	public boolean equalsred(MatrixComplex cMatrix) {
		int rowLen = this.rows();
		int colLen = this.cols();
		
		if (rowLen != cMatrix.rows()) return false;
		if (colLen != cMatrix.cols()) return false;
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (!this.complexMatrix[row][col].equalsred(cMatrix.complexMatrix[row][col])) return false;
		return true;
	}

	/*
	 * ARITHMETIC OPERATIONS
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
	 * @return The matrix resulting from the matrix product.
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
		return this.inverse().times(cMatrix);
	}

	/**
	 * Calculates the left division of two arrays as this*cMatrix⁻¹
	 * @param cMatrix The matrix used as divisor
	 * @return The right division
	 */
	public MatrixComplex dividesright(MatrixComplex cMatrix) {
		return this.times(cMatrix.inverse());
	}

	/**
	 * Calculates the power of a Matrix
	 * @param power The power at which the matrix is ​​raised. Only positive integers are allowed
	 * @return The matrix raised to power
	 */
	public MatrixComplex power(int power) {
		MatrixComplex powerMatrix = new MatrixComplex(this.rows(), this.cols());
		powerMatrix.initMatrixDiag(1, 0);
		if (power == 0) return powerMatrix;
		for (int i = 1; i <= power; ++i)
			powerMatrix = powerMatrix.times(this);
		return powerMatrix;
	}
	
	/*
	 * NORMS
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

		double[] norm = new double[colLen];
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

		double[] norm = new double[colLen];
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
	 * UNARY OPERATORS
	 */

	/**
	 * Checks if the matrix is null.
	 * @return true if the matrix is null, otherwise false.
	 */
	public boolean isNull() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				if (!this.getItem(row, col).equalsred(Complex.ZERO)) return false;
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
	 * Trace of an n-by-n square matrix A - the sum of the elements on the main diagonal. 
	 * @return The value of the trace.
	 */
	public Complex trace() {
		int rowLen = this.rows();
		Complex trace = new Complex();

		for (int i = 0; i < rowLen; ++i)
			trace = trace.plus(this.complexMatrix[i][i]);
		return trace;
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
			System.err.println(HEADINFO + "inverse: Not valid matrix: The matrix determinat is ZERO.");
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
	 * Returns the diagonal matrix using triangularization Lo-Up
	 * @return The diagonal matrix 
	 */  
	public MatrixComplex diagonal() {
		return (this.triangleLo()).triangleUp();
	}

	/**
	 * Returns the matrix codiagonalized using triangularization Up-Lo
	 * @return The matrix codiagonalized
	 */  
	public MatrixComplex codiagonal() {
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
		// System.out.println("auxMatrix.mSign="+auxMatrix.mSign);
		for (int iter = 0; iter < rowLen; ++iter) {
			cResult = cResult.times(auxMatrix.complexMatrix[iter][iter]);
		}
		return cResult.times(auxMatrix.mSign);
	}

	/**
	 * Private method that calculates the matrix 3x3 determinant by the Sarrus's rule.
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
					if (b < i) { smaller.complexMatrix[a-1][b] = this.complexMatrix[a][b];
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
				coefMatrix.complexMatrix[row][col] = this.complexMatrix[row][col];
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
			indMatrix.complexMatrix[row][0] = this.complexMatrix[row][colLen-1];
		return indMatrix;
	}
	
	/**
	 * Defines the constants that identify the type of equation system being solved.
	 */
	public static final int INCOMPATIBLE = -1;
	public static final int COMPATIBLE_INDET = 0;
	public static final int COMPATIBLE_DET = 1;

	/**
	 * Identifies whether the system of equations is homogeneous.
	 * @return Returns true if the system is homogeneous, false otherwise.
	 */
	public boolean homogeneous() {
		int rowLen = this.rows();
		int lastCol = this.cols()-1;
		boolean homogeneous = true;

		for (int row = 0; row < rowLen; ++row)
			homogeneous &= this.complexMatrix[row][lastCol].equalsred(0, 0);
		return homogeneous;
	}

	/**
	 * Identifies the type of systems of equations returning the constant according to the definition.
	 * @return INCOMPATIBLE = -1, COMPATIBLE_INDET = 0 or COMPATIBLE_DET = 1.
	 */
	public int typeEqSys() {
		int numUnk = this.cols()-1;
		int coefRank, augmRank;

		MatrixComplex coefMatrix = this.coefMatrix();

		augmRank = this.rank();
		coefRank = coefMatrix.rank();
		
		//	coefMatrix.println("------------------ typeEqSys() coefMatrix");
		//	System.out.println("------------------ typeEqSys() augmRank:" + augmRank);
		//	System.out.println("------------------ typeEqSys() coefRank:" + coefRank);
				
		if (augmRank != coefRank) return INCOMPATIBLE;
		//if (augmRank != coefRank || coefRank == 0) return INCOMPATIBLE;
			
		if (coefRank == numUnk) return this.homogeneous() ? COMPATIBLE_INDET : COMPATIBLE_DET;
		else return COMPATIBLE_INDET;	
	}
	
	/**
	 * Prints a message indicating the system type of equations according to type "type".
	 * @param type INCOMPATIBLE = -1, COMPATIBLE_INDET = 0 or COMPATIBLE_DET = 1.
	 * @param lambda Parameter value to calculate solutions for indeterminate systems.
	 */
	public void printTypeEqSys(int type, Complex lambda) {
		String text = "";
		switch (type) {
			case INCOMPATIBLE: text = "The system is INCOMPATIBLE"; 
				break;
			case COMPATIBLE_INDET: text = "The system is COMPATIBLE INDETERMINATED. Solutions calculated for λ = " + lambda.toString(); 
				break;
			case COMPATIBLE_DET: text = "The system is COMPATIBLE DETERMINANTED"; 
				break;
		}    	
		System.out.println(text);
	}

	/**
	 * Prints a message indicating the system type of equations according to type "type". lambda is 1 by default
	 * @param type INCOMPATIBLE = -1, COMPATIBLE_INDET = 0 or COMPATIBLE_DET = 1.
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
			return this.solveGauss(Complex.ONE);
	}

	/**
	 * Shortcut to solveGauss
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist.
	 */
	public MatrixComplex solve(Complex lambda) {
		return this.solveGauss(lambda);
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
	 * Checks whether the all the items (except colIdx) of rowIdx of the Matrix are null or not
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

	private Complex getCoef(int row1, int row2) {
		Complex coef = new Complex(0);
		
		for (int col = 0; col < this.cols(); ++col) {
			if(!this.getItem(row2, col).equalsred(Complex.ZERO)) {
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
			case MatrixComplex.COMPATIBLE_DET:
				System.out.println("1 single solution is returned."); 
				numSols = 1;
				break;
			case MatrixComplex.COMPATIBLE_INDET:
					numSols = this.nbrOfSolutions();
					System.out.println(numSols + " linear independent solutions are returned."); 
					break;
			default:
				numSols = 0;
				System.out.println("System without solution.");
		}
		return numSols;
	}
	
	public MatrixComplex solveSubstitution(MatrixComplex solMatrix, Complex lambda) {
		MatrixComplex auxMatrix;
		int eqsIdx = this.rows()-1;
		int nbrSols = this.nbrOfSolutions();
		auxMatrix = this.triangle().heap();
		int rowIdx, colIdx, solIdx, reduxSol = 0;
		Complex value;

		System.out.println(HEADINFO + "SOLVED by SUBSTITUTION method ");
		for (rowIdx = eqsIdx; rowIdx >= 0; --rowIdx) {
			colIdx = rowIdx;
			if (auxMatrix.isNullRow(rowIdx) || auxMatrix.isNullCol(colIdx)) {
				++reduxSol;
			}
		}
		// Prepare the parametric values for solutions.
		// We need to try with	0,0,0,...,0,0,1 
		//						0,0,0,...,0,1,0
		//						0,0,0,...,1,0,0
		//						...
		//						0,1,0,...,0,0,0
		//						1,0,0,...,0,0,0
		for (solIdx = 0; solIdx < nbrSols; ++solIdx) {
			solMatrix.setItem(solIdx, this.cols()-2-solIdx, lambda);
		}
		
		// With the partial solution calculated get the rest of the values
		for (solIdx = 0; solIdx < nbrSols; ++solIdx) {
			for (rowIdx = eqsIdx-reduxSol; rowIdx >= 0; --rowIdx) {
				// Independent term
				value = auxMatrix.getItem(rowIdx, auxMatrix.cols()-1);
				for (colIdx = auxMatrix.cols()-2; colIdx > rowIdx; --colIdx) {
					value = value.minus(auxMatrix.getItem(rowIdx, colIdx).times(solMatrix.getItem(solIdx, colIdx)));
				}
				value = value.divides(auxMatrix.getItem(rowIdx, colIdx));
				solMatrix.setItem(solIdx, rowIdx, value);
			}
		}
		return solMatrix;
	}

	/**
	 * Finds the solutions to a equation systems
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist, otherwise null.
	 */	
	public MatrixComplex solveGauss3(Complex lambda) {
		int rowLen = this.rows();
		int colLen = this.cols();

		if (rowLen+1 > colLen) {
			System.out.println(HEADINFO + "solveGauss: " + "Not valid matrix: The matrix doesn't represent an equation system.");
		}
		
		int nbrOfSols = this.nbrOfSolutions();
		MatrixComplex coefMatrix, indMatrix;
		MatrixComplex solMatrix = new MatrixComplex(nbrOfSols, colLen-1);

		int typeEqSys = this.typeEqSys();
		//this.printTypeEqSys(typeEqSys, lambda);
		if (typeEqSys == INCOMPATIBLE) return solMatrix.divides(0).transpose();

		coefMatrix = this.coefMatrix();
		indMatrix = this.indMatrix();
		if (typeEqSys == COMPATIBLE_DET) return coefMatrix.dividesleft(indMatrix).transpose();
		solMatrix = solveReduction(solMatrix, lambda);
		for (int rowIdx = 0 ; rowIdx < solMatrix.rows(); ++rowIdx) {
			if (solMatrix.isNullRow(rowIdx)) {
				solMatrix = solveSubstitution(solMatrix, lambda);
				break;
			}
		}
		return solMatrix;
	}
	
	/**
	 * Private function that generates the new reduced equations system to find out its solutions
	 * @param colLen
	 * @param nbrSols
	 * @param solMatrix
	 * @param sol
	 * @return
	 */
	private MatrixComplex setNewMatrixRed(int colLen, int nbrSols, MatrixComplex solMatrix, int sol) {
		Complex cVal;
		MatrixComplex newMatrix = this.subMatrixAug(0, 0, colLen-nbrSols);
		for (int row = 0; row < newMatrix.rows(); ++row) {
			cVal = this.getItem(row, colLen);
			for (int col = colLen-nbrSols; col < colLen; ++col) {
				cVal = cVal.minus(this.getItem(row, col).times(solMatrix.getItem(sol, col)));
			}
			newMatrix.setItem(row, newMatrix.cols()-1, cVal) ;
		}
		return newMatrix;
	}

	
	public MatrixComplex solveReduction(MatrixComplex solMatrix, Complex lambda) {
		/*
		 * System reduction is used to find a particular solution for indeterminate compatible systems
		 * Based on the number of solutions, unknowns are fixed in a orthogonal base to determine the 
		 * linear independent solutions 
		 * A value for lambda is given to calculate the indeterminate solutions
		 */
		int rowLen = this.rows();
		int colLen = this.cols();
		int colSol = rowLen-1;
		MatrixComplex auxMatrix;
		MatrixComplex newMatrix;
		Complex cVal;

		//System.out.println(HEADINFO + "SOLVED by REDUCTION method ");
			//this.println("---------------- Matrix solve");
		auxMatrix = this.triangleUp();	// .heap();
		int nbrOfSols = auxMatrix.nbrOfSolutions();
			//System.out.println("---------------- nbrOfSols:" + nbrOfSols);
		solMatrix = new MatrixComplex(nbrOfSols, colLen-1);
			//auxMatrix.coefMatrix().println("---------------- coefMatrix");
			//System.out.println("----------------");
		if ( auxMatrix.coefMatrix().isNull() ) return solMatrix;

		for (int sol = 0; sol < nbrOfSols; ++sol) {
			for (int row = rowLen-1; row >= 0; --row) {
				if (colSol < 0) break;
				if (auxMatrix.isNullRow(row)) {
					cVal = lambda;
				}
				else {
					cVal = auxMatrix.getItem(row, colLen-1);
					cVal = cVal.divides(auxMatrix.getItem(row, colLen-2));
				}
				solMatrix.setItem(sol, colSol--, cVal);
				newMatrix = auxMatrix.setNewMatrixRed(colLen-1, nbrOfSols, solMatrix, sol);
				if (newMatrix.isNull()) break;
				MatrixComplex newSolMatrix = newMatrix.solve(lambda);
				for (int col = 0; col < newSolMatrix.cols(); ++col)
					solMatrix.setItem(sol, col, newSolMatrix.getItem(0, col));
			}
		}
		return solMatrix;
	}
	
	/**
	 * Private function that generates the new reduced equations system to find out its solutions
	 * @param auxMatrix
	 * @param nbrOfSols
	 * @param rowLen
	 * @param colLen
	 * @param sol
	 * @param solMatrix
	 * @return
	 */
	private MatrixComplex setNewMatrix_(MatrixComplex auxMatrix, int nbrOfSols, int rowLen, int colLen, int sol, MatrixComplex solMatrix) {
		Complex cVal;
		MatrixComplex newMatrix = auxMatrix.subMatrixAug(0, nbrOfSols, colLen-nbrOfSols-1);
		for (int row = 0; row < colLen-nbrOfSols-1; ++row) {
			cVal = newMatrix.getItem(row, newMatrix.cols()-1);
			for (int col = 0; col < nbrOfSols; ++col) {
				cVal = cVal.minus(auxMatrix.getItem(row, col).times(solMatrix.getItem(sol,col)));
			}
			newMatrix.setItem(row, rowLen-nbrOfSols, cVal);
		}
		return newMatrix;
	}

	public MatrixComplex solveReduction_(MatrixComplex solMatrix, Complex lambda) {
		/*
		 * System reduction is used to find a particular solution for indeterminate compatible systems
		 * Based on the number of solutions, unknowns are fixed in a orthogonal base to determine the 
		 * linear independent solutions 
		 * A value for lambda is given to calculate the indeterminate solutions
		 */
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex auxMatrix;
		MatrixComplex newMatrix;

		System.out.println(HEADINFO + "SOLVED by REDUCTION method ");
			//this.println("---------------- Matriz solve");
		auxMatrix = this.triangleUp().heap();
		int nbrOfSols = auxMatrix.nbrOfSolutions();
			//System.out.println("---------------- nbrOfSols:" + nbrOfSols);
		solMatrix = new MatrixComplex(nbrOfSols, colLen-1);
			//auxMatrix.coefMatrix().println("---------------- coefMatrix");
			//System.out.println("----------------");
		if ( auxMatrix.coefMatrix().isNull() ) return solMatrix;

		for (int sol = 0; sol < nbrOfSols; ++sol) {
			for (int col = 0; col < nbrOfSols; ++col) {
				// TODO I don't know WHY this "if" works, I have to investigate it sooner or later
				if (this.homogeneous()) solMatrix.setItem(sol, col, sol != col ? Complex.ZERO : lambda);
				else solMatrix.setItem(sol, col, sol == col ? Complex.ZERO : lambda);
			}
			MatrixComplex rowSolMatrix = new MatrixComplex(1, rowLen-nbrOfSols);	
			newMatrix = setNewMatrix_(auxMatrix, nbrOfSols, rowLen, colLen, sol, solMatrix);
			rowSolMatrix = newMatrix.solve(lambda);	
			/*
			 * Check if the solutions calculated are valid
			 * If not, uses the inverse rule to assign the fixed value to the solution
			 * TODO This is a brute force solution. I need to find out the condition in the equations system that forces to change the rule
			 */
			if (!Double.isFinite(rowSolMatrix.times(rowSolMatrix.transpose()).getItem(0,0).rep())) {
				for (int col = 0; col < nbrOfSols; ++col) {
					// TODO I don't know WHY this "if" works, I have to investigate it sooner or later
					if (this.homogeneous()) solMatrix.setItem(sol, col, sol == col ? Complex.ZERO : lambda);
					else solMatrix.setItem(sol, col, sol != col ? Complex.ZERO : lambda);
				}
				newMatrix = setNewMatrix_(auxMatrix, nbrOfSols, rowLen, colLen, sol, solMatrix);
				rowSolMatrix = newMatrix.solve(lambda);
			}
			if (newMatrix.typeEqSys() == INCOMPATIBLE) {
				this.printTypeEqSys(INCOMPATIBLE, lambda);
				return solMatrix.divides(0);
			}
			else {
				for (int col = nbrOfSols; col < colLen-1; ++col) {
					solMatrix.setItem(sol, col, rowSolMatrix.getItem(0, col-nbrOfSols));
				}
			}
		}
		return solMatrix;
	}
	

	/**
	 * DEPRECATED KEPT ONLY FOR VALIDATION PORPOUSES
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist, otherwise null.
	 */	
	public MatrixComplex solveGauss2(Complex lambda) {
		int rowLen = this.rows();
		int colLen = this.cols();

		if (rowLen+1 > colLen) {
			System.out.println(HEADINFO + "solveGauss: " + "Not valid matrix: The matrix doesn't represent an equation system.");
		}

		MatrixComplex coefMatrix, indMatrix;
		MatrixComplex solMatrix = new MatrixComplex(1, colLen-1);

		int typeEqSys = this.typeEqSys();
		//this.printTypeEqSys(typeEqSys, lambda);
		if (typeEqSys == INCOMPATIBLE) return solMatrix.divides(0).transpose();

		coefMatrix = this.coefMatrix();
		indMatrix = this.indMatrix();
		if (typeEqSys == COMPATIBLE_DET) return coefMatrix.dividesleft(indMatrix).transpose();
	
		if (this.homogeneous() && !coefMatrix.determinant().equalsred(Complex.ZERO)) {
			System.out.println(HEADINFO + "solveGauss: " + "This system only has got the trivial soution!!!!!!!!!!");
			return solMatrix;
		}
		
		if (rowLen == 1 && colLen == 2) {
			solMatrix.setItem(0, 0, lambda);
			return solMatrix;
		}
		/*
		 * The complete system of equations is solved by triangularization
		 * The solutions are calculated by system reduction
		 */
		return solveReduction(solMatrix, lambda);
	}

	public MatrixComplex solveGauss21(Complex lambda) {
		int rowLen = this.rows();
		int colLen = this.cols();

		if (rowLen+1 > colLen) {
			System.out.println(HEADINFO + "solveGauss: " + "Not valid matrix: The matrix doesn't represent an equation system.");
		}

		MatrixComplex auxMatrix, coefMatrix, indMatrix;
		MatrixComplex solMatrix = new MatrixComplex(1, colLen-1);

		int typeEqSys = this.typeEqSys();
		//this.printTypeEqSys(typeEqSys, lambda);
		if (typeEqSys == INCOMPATIBLE) return solMatrix.divides(0).transpose();

		coefMatrix = this.coefMatrix();
		indMatrix = this.indMatrix();
		if (typeEqSys == COMPATIBLE_DET) return coefMatrix.dividesleft(indMatrix).transpose();
	
		if (this.homogeneous() && !coefMatrix.determinant().equalsred(Complex.ZERO)) {
			System.out.println(HEADINFO + "solveGauss: " + "This system only has got the trivial soution!!!!!!!!!!");
			return solMatrix;
		}
		
		if (rowLen == 1 && colLen == 2) {
			solMatrix.setItem(0, 0, lambda);
			return solMatrix;
		}
		/*
		 * The complete system of equations is solved by triangularization
		 * The solutions are calculated by system reduction
		 */
		/*
		 * System reduction is used to find a particular solution for indeterminate compatible systems
		 * Based on the number of solutions unknowns are fixed in a orthogonal base to determine the 
		 * linear independent solutions 
		 * A value for lambda is given to calculate the indeterminate solutions
		 */
		System.out.println(HEADINFO + "SOLVED by REDUCTION method ");

			//this.println("---------------- Matriz solve");
		auxMatrix = this.triangleUp().heap();
		indMatrix = auxMatrix.indMatrix();
		MatrixComplex newMatrix;
		int nbrOfSols = auxMatrix.nbrOfSolutions();
			//System.out.println("---------------- nbrOfSols:" + nbrOfSols);
		solMatrix = new MatrixComplex(nbrOfSols, colLen-1);
			//auxMatrix.coefMatrix().println("---------------- coefMatrix");
			//System.out.println("----------------");
		if ( auxMatrix.coefMatrix().isNull() ) return solMatrix;

		for (int sol = 0; sol < nbrOfSols; ++sol) {
			for (int col = 0; col < nbrOfSols; ++col) {
				// TODO I don't know WHY this "if" works, I have to investigate it sooner or later
				if (this.homogeneous()) solMatrix.setItem(sol, col, sol != col ? Complex.ZERO : lambda);
				else solMatrix.setItem(sol, col, sol == col ? Complex.ZERO : lambda);
			}
			MatrixComplex rowSolMatrix = new MatrixComplex(1, rowLen-nbrOfSols);	
			newMatrix = setNewMatrix_(auxMatrix, nbrOfSols, rowLen, colLen, sol, solMatrix);
			rowSolMatrix = newMatrix.solve();	
			/*
			 * Check if the solutions calculated are valid
			 * If not, uses the inverse rule to assign the fixed value to the solution
			 * TODO This is a brute force solution. I need to find out the condition in the equations system that forces to change the rule
			 */
			if (!Double.isFinite(rowSolMatrix.times(rowSolMatrix.transpose()).getItem(0,0).rep())) {
				for (int col = 0; col < nbrOfSols; ++col) {
					// TODO I don't know WHY this "if" works, I have to investigate it sooner or later
					if (this.homogeneous()) solMatrix.setItem(sol, col, sol == col ? Complex.ZERO : lambda);
					else solMatrix.setItem(sol, col, sol != col ? Complex.ZERO : lambda);
				}
				newMatrix = setNewMatrix_(auxMatrix, nbrOfSols, rowLen, colLen, sol, solMatrix);
				rowSolMatrix = newMatrix.solve();
			}
			if (newMatrix.typeEqSys() == INCOMPATIBLE) {
				this.printTypeEqSys(INCOMPATIBLE, lambda);
				return solMatrix.divides(0);
			}
			else {
				for (int col = nbrOfSols; col < colLen-1; ++col) {
					solMatrix.setItem(sol, col, rowSolMatrix.getItem(0, col-nbrOfSols));
				}
			}
		}
		//solMatrix.println(HEADINFO + "solveGauss: " + "solMatrix");
		return solMatrix;
	}


	/**
	 * DEPRECATED KEPT ONLY FOR VALIDATION PORPOUSES
	 * @param lambda
	 * @return
	 */
	public MatrixComplex solveGauss1(Complex lambda) {
		int rowLen = this.rows();
		int colLen = this.cols();

		if (rowLen+1 > colLen) {
			System.out.println(HEADINFO + "solveGauss: " + "Not valid matrix: The matrix doesn't represent an equation system.");
		}

		MatrixComplex auxMatrix, coefMatrix, indMatrix;
		MatrixComplex solMatrix = new MatrixComplex(1, colLen-1);

		int typeEqSys = this.typeEqSys();
		//this.printTypeEqSys(typeEqSys, lambda);
		if (typeEqSys == INCOMPATIBLE) return solMatrix.divides(0).transpose();

		coefMatrix = this.coefMatrix();
		indMatrix = this.indMatrix();
		if (typeEqSys == COMPATIBLE_DET) return coefMatrix.dividesleft(indMatrix).transpose();
	
		if (this.homogeneous() && !coefMatrix.determinant().equalsred(Complex.ZERO)) {
			System.out.println(HEADINFO + "solveGauss: " + "This system only has got the trivial soution!!!!!!!!!!");
			return solMatrix;
		}
		
		if (rowLen == 1 && colLen == 2) {
			solMatrix.setItem(0, 0, lambda);
			return solMatrix;
		}
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
			this.println("---------------- Matriz solve");
		auxMatrix = this.triangleUp().heap();
		indMatrix = auxMatrix.indMatrix();
		MatrixComplex newMatrix;
		int nbrOfSols = auxMatrix.nbrOfSolutions();
			System.out.println("---------------- nbrOfSols:" + nbrOfSols);
		solMatrix = new MatrixComplex(nbrOfSols, colLen-1);
		
			auxMatrix.coefMatrix().println("---------------- coefMatrix");
			System.out.println("----------------");
/****/
		if (coefMatrix.isNull()) {
			solMatrix.initMatrixDiag(1,0);
			return solMatrix;
		}
		
		if (this.homogeneous()) {
			System.out.println("---------------- this.homogeneous()");
			for (int sol = 0; sol < nbrOfSols; ++sol) {
				if (auxMatrix.isNullSolution(sol, sol)) {
					solMatrix.setItem(sol, sol+nbrOfSols, Complex.ONE);
				}
			}
			return solMatrix;
			// if ( auxMatrix.coefMatrix().isNull() ) return solMatrix;
		}
/****/
//		if ( auxMatrix.coefMatrix().isNull() ) return solMatrix;

		for (int sol = 0; sol < nbrOfSols; ++sol) {
			for (int col = 0; col < nbrOfSols; ++col) {
				// TODO I don't know WHY this "if" works, I have to investigate it sooner or later
				if (this.homogeneous()) solMatrix.setItem(sol, col, sol != col ? Complex.ZERO : lambda);
				else solMatrix.setItem(sol, col, sol == col ? Complex.ZERO : lambda);
			}
			MatrixComplex rowSolMatrix = new MatrixComplex(1, rowLen-nbrOfSols);	
			newMatrix = setNewMatrix_(auxMatrix, nbrOfSols, rowLen, colLen, sol, solMatrix);
			rowSolMatrix = newMatrix.solve();	
			/*
			 * Check if the solutions calculated are valid
			 * If not, uses the inverse rule to assign the fixed value to the solution
			 * TODO This is a brute force solution. I need to find out the condition in the equations system that forces to change the rule
			 */
			if (!Double.isFinite(rowSolMatrix.times(rowSolMatrix.transpose()).getItem(0,0).rep())) {
				for (int col = 0; col < nbrOfSols; ++col) {
					// TODO I don't know WHY this "if" works, I have to investigate it sooner or later
					if (this.homogeneous()) solMatrix.setItem(sol, col, sol == col ? Complex.ZERO : lambda);
					else solMatrix.setItem(sol, col, sol != col ? Complex.ZERO : lambda);
				}
				newMatrix = setNewMatrix_(auxMatrix, nbrOfSols, rowLen, colLen, sol, solMatrix);
				rowSolMatrix = newMatrix.solve();
			}
			if (newMatrix.typeEqSys() == INCOMPATIBLE) {
				//this.printTypeEqSys(INCOMPATIBLE, lambda);
				return solMatrix.divides(0);
			}
			else {
				for (int col = nbrOfSols; col < colLen-1; ++col) {
					solMatrix.setItem(sol, col, rowSolMatrix.getItem(0, col-nbrOfSols));
				}
			}
		}
		//solMatrix.println(HEADINFO + "solveGauss: " + "solMatrix");
		return solMatrix;
	}

	/**
	 * DEPRECATED KEPT ONLY FOR VALIDATION PORPOUSES
	 * @param lambda
	 * @return
	 */
	public MatrixComplex solveGauss0(Complex lambda) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cCoef = new Complex();
		Complex cVal = new Complex();
		int col;
		int row;

		if (rowLen+1 > colLen) {
			System.out.println(HEADINFO + "solveGauss: " + "Not valid matrix: The matrix doesn't represent an equation system.");
		}

		MatrixComplex auxMatrix, coefMatrix, indMatrix;
		MatrixComplex solMatrix = new MatrixComplex(colLen-1, 1);

		/*
		 * The complete system of equations is solved by triangularization
		 * The solutions are calculated by process known as back-substitution
		 */
		int typeEqSys = this.typeEqSys();
		this.printTypeEqSys(typeEqSys, lambda);
		if (typeEqSys == INCOMPATIBLE) return solMatrix.divides(0);

		coefMatrix = this.coefMatrix();
		indMatrix = this.indMatrix();
		if (typeEqSys == COMPATIBLE_DET) return coefMatrix.dividesleft(indMatrix).transpose();

		/*
		 * The complete system of equations is solved by triangularization
		 * The solutions are calculated by process known as back-substitution
		 */
		/*
		 * Back-substitution is used also to find a particular solution for indeterminate compatible systems
		 * A value for lambda is set to calculate the indeterminate solutions
		 */
		System.out.println(HEADINFO + "SOLVED by GAUSS method ");
		auxMatrix = this.triangleUp().heap();
		coefMatrix = auxMatrix.coefMatrix();
		indMatrix = auxMatrix.indMatrix();
		int nbrOfSols = auxMatrix.nbrOfSolutions();
		solMatrix = new MatrixComplex(colLen-1, nbrOfSols);
		for (int sol = 0; sol < nbrOfSols; ++sol) {
			for (row = rowLen-1; row >= 0; --row) {
				cCoef = coefMatrix.getItem(row, row);
				if (cCoef.equalsred(0,0)) {
					if (this.homogeneous() && nbrOfSols == 1) solMatrix.setItem(row,sol,lambda);
					else solMatrix.setItem(row,sol, row == sol ? lambda : Complex.ZERO);
					continue;
				}
				cVal = indMatrix.getItem(row,0);
				for (col = row+1; col < colLen-1; ++col) {
					cVal = cVal.minus(coefMatrix.getItem(row,col).times(solMatrix.getItem(col,sol)));
				}         
				solMatrix.setItem(row,sol,cVal.divides(cCoef));
			}
		}
		return solMatrix.transpose();
	}

	/**
	 * Shortcut to ACTIVE solveGauss
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist.
	 */
	public MatrixComplex solveGauss(Complex lambda) {
		return solveGauss3(lambda);
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

		if (this.typeEqSys() != COMPATIBLE_DET) {
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
	 * DEPRECATED USE isNullRow INSTEAD. Checks if the row of an array is zero.
	 * @param row Index of the Row to check.
	 * @return true if null, false otherwise.
	 */
	public boolean isRowNull_(int row) {
		int colLen = this.cols();
		for (int col = 0; col < colLen; ++col)
			if (!this.complexMatrix[row][col].equals(0,0)) 
				return false;
		return true;
	}
	
	/**
	 * Returns TRUE if rowIdx has only zeros
	 * @param rowIdx The row to check
	 * @return True if is null
	 */
	public boolean isNullRow(int rowIdx) {
		for (int col = 0; col < this.cols(); ++col)
			if (!this.getItem(rowIdx, col).equalsred(Complex.ZERO)) return false;
		return true;
	}

	/**
	 * Returns TRUE if colIdx has only zeros
	 * @param colIdx The col to check
	 * @return True if is null
	 */
	public boolean isNullCol(int colIdx) {
		for (int row = 0; row < this.rows(); ++row)
			if (!this.getItem(row, colIdx).equalsred(Complex.ZERO)) return false;
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
		return rankDown();
	}
	
	public int rankDown() {
		int maxRank = this.rows(), rank = maxRank ;
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
		for (int order = maxRank; order > 0 ; --order) {
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
					if (!incrMatrix.determinant().equalsred(Complex.ZERO)) {
						rankfound = true;
						break;
					}
				}
				if (rankfound) return rank;
			}
			--rank;
		}
		return rank;
	}

	public int rankUp() {
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
					if (!incrMatrix.determinant().equalsred(Complex.ZERO)) {
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

/*
	 * No he conseguido encontrar un método fiable para hayar el rango en matrices no cuadradas
	 * Dejo comentado el código para retomarlo algún día
	 */
	/**
	 * Alternative method to calculate the rank based on triangularization.
	 * Triangularizing arrays will show the rank of the matrix by counting zeros in rows.
	 * Null columns must be identified.
	 * @return The rank of the matrix. 
	 * /
	public int rank_() {
		int result, rank = 0;
		MatrixComplex tempMatrix;

		if (this.isNull()) return 0;
		
		tempMatrix = this.copy().triangle().heap();
		tempMatrix = tempMatrix.removeNullRows();
		//tempMatrix.println("tempMatrix");
		tempMatrix = tempMatrix.removeDuplicateRows();
		tempMatrix.println("tempMatrix");
		for (int row = 0; row < tempMatrix.rows(); ++row) {
			result = 0;
			for (int col = row; col < tempMatrix.cols(); ++col) {
				if (this.isNullCol(row)) break;
				result += tempMatrix.getItem(row,col).equalsred(0,0) ? 0 : 1;				
			}
			rank += result == 0 ? 0 : 1;
		}

		return rank > tempMatrix.rows() ? tempMatrix.rows() : rank;
	}

	public int rank__() {
		int result, rankHi = 0, rankLo = 0;
		MatrixComplex tempMatrix;

		if (this.isNull()) return 0;
		
		tempMatrix = this.copy().triangle().heap();
		for (int row = 0; row < tempMatrix.rows(); ++row) {
			result = 0;
			for (int col = row; col < tempMatrix.cols(); ++col) {
				if (this.isNullCol(row)) break;
				result += tempMatrix.getItem(row,col).equalsred(0,0) ? 0 : 1;				
			}
			rankHi += result == 0 ? 0 : 1;
		}
		
		tempMatrix = this.copy().triangleLo().heap();
		for (int row = 0; row < tempMatrix.rows(); ++row) {
			result = 0;
			for (int col = row; col < tempMatrix.cols(); ++col) {
				if (this.isNullCol(row)) break;
				result += tempMatrix.getItem(row,col).equalsred(0,0) ? 0 : 1;				
			}
			rankLo += result == 0 ? 0 : 1;
		}

		return rankHi > rankLo ? rankLo : rankHi;
	}

	public int rank1() {
		int result, rank = 0;
		MatrixComplex tempMatrix = this.copy();
		MatrixComplex redTempMatrix;
		
		//Check if is the NULL Matrix, the rank = 0
		if (this.isNull()) return 0;
		
		//first reduce tempMatrix
		do {
			tempMatrix = tempMatrix.triangle().heap();
			redTempMatrix = tempMatrix.reduce();
			if (tempMatrix.dim() == redTempMatrix.dim()) break;
			tempMatrix = redTempMatrix;
		} while (true);
		
		//		tempMatrix.println("---------------- rank tempMatrix");
		int rowLen = tempMatrix.rows(); //complexMatrix.length;
		int colLen = tempMatrix.cols(); //complexMatrix[0].length;
		//		System.out.println("---------------- rank colLen:" +  colLen);

		for (int row = 0; row < rowLen; ++row) {
			result = 0;
			if (colLen <= row) break;
			for (int col = 0; col <= row; ++col) {
				result += tempMatrix.complexMatrix[row][col].equalsred(0,0) ? 0 : 1;				
			//		System.out.println("---------------- rank result:" +  result);
			}
			rank += result == 0 ? 0 : 1;
			//	System.out.println("---------------- rank.:" +  rank);
		}
		//	System.out.println("---------------- rank:" +  rank);
		return rank;
	}
	*/
	
	/**
	 * Calculates the nullity of an array.
	 * @return The nullity of the matrix.
	 */
	public int nullity() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int maxLen = rowLen >= colLen ? rowLen : colLen;

		return maxLen - this.rank();
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
	 * Checks whether the matrix is upper triangular.
	 * @return true if the matrix is upper triangular, false otherwise.
	 */
	public boolean isTriangleUp() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int maxIter = rowLen < colLen ? rowLen : colLen;

		for (int row = 0; row < maxIter; ++row)
			for (int col = 0; col < row; ++col) {
				if (!this.complexMatrix[row][col].equals(0,0)) 
					return false;
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
			for(int col = colLen-1; col < row && col > -1; --col) {
				if (!this.complexMatrix[row][col].equalsred(Complex.ZERO)) isZero = false;
			}
			if (isZero) nonZeroArray.complexMatrix[countNonZeroArray++] = this.complexMatrix[row].clone(); 
			else zeroArray.complexMatrix[countZeroArray++] = this.complexMatrix[row].clone();
		}
		for(int row = 0; row < countNonZeroArray; ++row) hollow.complexMatrix[row] = nonZeroArray.complexMatrix[row].clone();
		for(int row = 0; row < countZeroArray; ++row) hollow.complexMatrix[row+countNonZeroArray] = nonZeroArray.complexMatrix[row+countNonZeroArray].clone();
		
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
				if (!this.complexMatrix[row][col].equalsred(Complex.ZERO)) isZero = false;
			}
			if (isZero) zeroArray.complexMatrix[countZeroArray++] = this.complexMatrix[row].clone();
			else nonZeroArray.complexMatrix[countNonZeroArray++] = this.complexMatrix[row].clone();
		}
		for(int row = 0; row < countNonZeroArray; ++row) heap.complexMatrix[row] = nonZeroArray.complexMatrix[row].clone();
		for(int row = 0; row < countZeroArray; ++row) heap.complexMatrix[row+countNonZeroArray] = nonZeroArray.complexMatrix[row+countNonZeroArray].clone();
		
		return heap;
	}
	
	/**
	 * Calculates the upper triangularization of the matrix.
	 * @return The upper triangularized matrix.
	 */
	public MatrixComplex triangleUp(){
		int rowLen = this.rows();
		Complex cCoef = new Complex();
		MatrixComplex auxMatrix = this.clone();

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
				if (auxMatrix.getItem(k,k).equals(Complex.ZERO)) continue;
				cCoef = auxMatrix.getItem(row, k).divides(auxMatrix.getItem(k,k).opposite());
				auxMatrix.Ftransf(row, k, cCoef);
			}
		}
		return auxMatrix;
	}

	/**
	 * Checks if the matrix is lower triangular.
	 * @return true if the matrix is lower triangular, false otherwise.
	 */
	public boolean isTriangleLo() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int maxIter = rowLen < colLen ? rowLen : colLen;

		for (int col = 0; col < maxIter; ++col)
			for (int row = 0; row < col; ++row)
				if (!this.complexMatrix[row][col].equals(0,0)) 
					return false;
		return true;
	}

	/**
	 * Calculates the lower triangularization of the matrix.
	 * @return The lower triangularized matrix.
	 */
	public MatrixComplex triangleLo(){
		int rowLen = this.rows();
		Complex cCoef = new Complex();
		MatrixComplex auxMatrix = this.clone();

		if (this.isTriangleLo()) return auxMatrix;

		for (int k = rowLen-1; k >= 0 ; --k) {
			if (auxMatrix.getItem(k,k).equals(0,0)) {
				int rowSwap = auxMatrix.locateSwapRowDown(k);
				if (rowSwap == -1) {
					continue;
				}
				if (rowSwap != k) auxMatrix.swapRows(k, rowSwap);
			}
			for (int row = k-1; row >= 0; --row) {
				if (auxMatrix.getItem(k,k).equals(Complex.ZERO)) continue;
				cCoef = auxMatrix.getItem(row,k).divides(auxMatrix.getItem(k,k)).opposite();
				auxMatrix.Ftransf(row, k, cCoef);
			}
		}
		return auxMatrix;
	}
	
	/**
	 * Indicates whether the array is square or nor
	 * @return true for square matrix, false otherwise
	 */
	public boolean isSquare() {
		int rowLen = this.rows();
		int colLen = this.cols();

		return (rowLen == colLen);
	}
	
	/**
	 * Gram-Schmidt orthogonalization process.
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidt() {
		int rowLen = this.rows();
		int colLen = this.cols();

		colLen = colLen > rowLen ? rowLen : colLen;

		MatrixComplex gramschmidt = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < colLen; ++i) {
			x.copyCol(0, this, i);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidt, j);
				g = g.minus(v.times((x.dotprod(v)).divides(v.dotprod(v)))) ;				
			}
			gramschmidt.copyCol(i, g, 0);
		}
		return gramschmidt;
	}

	/**
	 * Gram-Schmidt Full orthogonalization process.
	 * The calculated orthogonal matrix is extended to the full dimension of the matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtFull() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex gramschmidtF = new MatrixComplex(rowLen, rowLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < rowLen; ++i) {
			if (i < colLen) x.copyCol(0, this, i);
			else x.initMatrixRandomInteger(9);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidtF, j);
				g = g.minus(v.times((x.dotprod(v)).divides(v.dotprod(v)))) ;				
			}
			gramschmidtF.copyCol(i, g, 0);
		}
		return gramschmidtF;
	}

	/**
	 * Gram-Schmidt Full Modified orthogonalization process. Modified Algorithm (http://www.ehu.eus/izaballa/Ana_Matr/Apuntes/lec6.pdf)
	 * The calculated orthogonal matrix is extended to the full dimension of the matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtMFull() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex gramschmidtF = new MatrixComplex(rowLen, rowLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < rowLen; ++i) {
			if (i < colLen) x.copyCol(0, this, i);
			else x.initMatrixRandomInteger(9);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidtF, j);
				g = g.minus(v.times((g.dotprod(v)).divides(v.dotprod(v)))) ;				
			}
			gramschmidtF.copyCol(i, g, 0);
		}
		return gramschmidtF;
	}

	/**
	 * Gram-Schmidt Modified orthogonalization process. Modified Algorithm (http://www.ehu.eus/izaballa/Ana_Matr/Apuntes/lec6.pdf)
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * @return The matrix with the orthogonal basis that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtM() {
		int rowLen = this.rows();
		int colLen = this.cols();

		colLen = colLen > rowLen ? rowLen : colLen;

		MatrixComplex gramschmidt = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < colLen; ++i) {
			x.copyCol(0, this, i);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidt, j);
				g = g.minus(v.times((g.dotprod(v)).divides(v.dotprod(v)))) ;				
			}
			gramschmidt.copyCol(i, g, 0);
		}
		return gramschmidt;
	}

	/**
	 * Normalizes the matrix using the Euclidean norm.
	 * @return The normalized matrix.
	 */
	public MatrixComplex normalize() {
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

	/*
	 * Characteristic Polynomial Eigenvalues Eigenvectors
	 */

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

	/**
	 * Returns a new augmented array with a column of terms.
	 * Copy the original matrix and add the column "interms".
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment(MatrixComplex interms) {
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
	 * Private method. Identifies whether a row is or not used to construct the cofactor's matrix (included for convenience)
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
	 * Private method. Identifies whether a row is or not used to construct the cofactor's matrix.
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
			case 0: charactPoly.complexMatrix[0][colLen-order].setComplexPol(1, 0); 
			break;
			case 1: charactPoly.complexMatrix[0][colLen-order] = this.trace().opposite(); 
			break;
			default: charactPoly.complexMatrix[0][colLen-order] = this.coefCP(order).times(Math.pow(-1, order)); 
			break;
			}
		}
		//this.print(charactPolyMatrix.complexMatrix);
		return (colLen % 2) == 0 ? charactPoly : charactPoly.opposite();
	}

	/**
	 * Calculates the eigenvalue, characteristic value, or characteristic root associated with eigenvector v by solving the Characteristic Polynomial.
	 * An eigenvalue is a scalar associated with a given linear transformation of a vector space and having the property that there is some nonzero vector which when multiplied by the scalar is equal to the vector obtained by letting the transformation operate on the vector; especially :  a root of the characteristic equation of a matrix
	 * @return The eigenvalues as a column array
	 */
	public MatrixComplex eigenvalues() {
		Polynom charactPoly = this.charactPoly().copy();
		MatrixComplex eigenvalues = charactPoly.solve();
		return eigenvalues;
	}
	
	/**
	 * Returns the arithmetic multiplicity of an specific eigenvalue
	 * @param eigenvalues The eingenvalues matrix
	 * @param eigenvalue The value to evaluate the arithmetic multiplicity
	 * @return The arithmetic multiplicity
	 */
	public int arithmeticMultiplicity(Complex eigenvalue) {
		int arithMult = 0;
		for (int i = 0; i < this.rows(); ++i) {
			if (this.getItem(i,0).equalsred(eigenvalue)) ++arithMult;
		}
		return arithMult;
	}
	
	/**
	 * Returns the geometric multiplicity of an specific eigenvalue using the eigenvectors matrix
	 * @param eigenVal The value to evaluate the geometric multiplicity
	 * @return The geometric multiplicity
	 */
	public int geometricMultiplicity(Complex eigenVal, MatrixComplex eigenVects) {
		MatrixComplex eigenV = new MatrixComplex(eigenVects.rows(), eigenVects.cols());

		for (int i = 0; i < this.rows(); ++i) {
			if (this.getItem(i,0).equalsred(eigenVal)) eigenV.complexMatrix[i] = eigenVects.complexMatrix[i];
		}
		return eigenV.rank();
	}
	
	/**
	 * Returns the geometric multiplicity of an specific eigenvalue calcultaing the eigenvectors
	 * @param eigenVal The value to evaluate the geometric multiplicity
	 * @return The geometric multiplicity
	 */
	public int geometricMultiplicity(Complex eigenVal) {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex I = new MatrixComplex(rowLen, colLen); I.initMatrixDiag(1,0);
		MatrixComplex cMatrix = (I.times(eigenVal)).minus(this);
		MatrixComplex dMatrix = cMatrix.augment().heap();
		return dMatrix.solve().rank();
	}

	/**
	 * Calculates the eigenvector or characteristic vector of a linear transformation associated to an specific eigenvalues.
	 * The eigenvector is a non-zero vector whose direction does not change when that linear transformation is applied to it
	 * @param eigenVal The eigenvalue to calculate the eigenvector
	 * @return The eigenvector as a column array
	 */
	public MatrixComplex eigenvectors(MatrixComplex eigenValArray) {
		return eigenvectors2(eigenValArray);
	}
	
	public MatrixComplex eigenvectors1(MatrixComplex eigenValArray) {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex I = new MatrixComplex(rowLen, colLen); I.initMatrixDiag(1,0);
		MatrixComplex eigenVectors = new MatrixComplex(rowLen, colLen);
		MatrixComplex eigenVect;

		Complex oldEigenVal = new Complex();
		MatrixComplex cMatrix = ((I.times(eigenValArray.getItem(0,0))).minus(this));
		MatrixComplex dMatrix = cMatrix.augment().heap();
		eigenVect = dMatrix.solve();
		for (int sol = 0; sol < eigenVect.rows(); ++sol)
			eigenVectors.complexMatrix[sol] = eigenVect.complexMatrix[sol].clone();
		oldEigenVal = eigenValArray.getItem(0,0);
		for (int rowEig = 1; rowEig < rowLen; ++rowEig) {
			if (oldEigenVal.equalsred(eigenValArray.getItem(rowEig,0))) continue;
			//cMatrix = this.minus(I.times(eigenValArray.getItem(rowEig,0)));
			cMatrix = (I.times(eigenValArray.getItem(rowEig,0))).minus(this);
			dMatrix = cMatrix.augment().heap();
			eigenVect = dMatrix.solve();
			for (int sol = 0; sol < eigenVect.rows(); ++sol) {
				eigenVectors.complexMatrix[rowEig+sol] = eigenVect.complexMatrix[sol].clone();
			}
			oldEigenVal = eigenValArray.getItem(rowEig,0);
		}
		return eigenVectors;
	}

	public MatrixComplex eigenvectors2(MatrixComplex eigenValArray) {
		int rowLen = this.rows();
		int colLen = this.cols();

		eigenValArray.quicksort(0);

		MatrixComplex I = new MatrixComplex(rowLen, colLen); I.initMatrixDiag(1,0);
		MatrixComplex eigenVectors = new MatrixComplex(rowLen, colLen);
		MatrixComplex eigenVect;
		MatrixComplex cMatrix, dMatrix;
		Complex eigenval;
		
		for (int rowEig = 0; rowEig < rowLen;) {
			eigenval = eigenValArray.getItem(rowEig, 0);
			int arithMult = eigenValArray.arithmeticMultiplicity(eigenval);
			cMatrix = (I.times(eigenval)).minus(this);
			dMatrix = cMatrix.augment().heap();
			eigenVect = dMatrix.solve();
			for (int sol = 0; sol < eigenVect.rows(); ++sol) {
			//for (int sol = 0; sol < arithMult; ++sol) {
				eigenVectors.complexMatrix[rowEig+sol] = eigenVect.complexMatrix[sol].clone();
			}
			rowEig += arithMult;
		}
		return eigenVectors;
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
		switch (sort) {
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
	public void quicksortdown(int col) {
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
	 * LINE EQUATION
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
	 * @param point expressed as a string separated by commas
	 * @return the distance
	 */
	public double distance(String spoint) {
		MatrixComplex point = new MatrixComplex(spoint);
		return this.distance(point);
	}

	/*
	 * Transformaciones elementales fila
	 */

	/**
	 * Transformation F(i,j) it swaps rows i and j of this matrix A ∈ C m × n and returns the result into a new Matrix.
	 * @param rowi Index of row i.
	 * @param rowj Index of row j.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int rowi, int rowj) {
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
	 * Transformation F(i,j) it swaps rows i and j of this matrix A ∈ C m × n.
	 * @param rowi Index of row i.
	 * @param rowj Index of row j.
	 */
	public void Ftransf(int rowi, int rowj) {
		int rowLen = this.rows();
		MatrixComplex pivot = new MatrixComplex(1, rowLen);

		pivot.complexMatrix[0] = this.complexMatrix[rowi];
		this.complexMatrix[rowi] = this.complexMatrix[rowj];
		this.complexMatrix[rowj] = pivot.complexMatrix[0];
		this.mSign *= -1;
	}

	/**
	 * Transformation F(i,α) multiplies row i of this matrix A ∈ C m × n by a number α != 0 and returns the result into a new Matrix.
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
	 * Transformation F(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α != 0 in text format and returns the result into a new Matrix.
	 * @param row The index of row i.
	 * @param sNum The complex number α in text format.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int row, String sNum) {
		Complex cNum = new Complex(sNum);
		return this.Ftransff(row, cNum);
	}

	/**
	 * Transformation F(i,α) multiplies row i of this matrix A ∈ C m × n by a number α != 0.
	 * @param row Index of row i.
	 * @param cNum The complex number α.
	 */
	public void Ftransf(int row, Complex cNum) {
		int colLen = this.cols();
		
		for(int col = 0; col < colLen; ++col)
			this.setItem(row, col, this.getItem(row, col).times(cNum));
	}

	/**
	 * Transformation F(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α != 0 in text format.
	 * @param row The index of row i.
	 * @param sNum The complex number α in text format.
	 */
	public void Ftransf(int row, String sNum) {
		Complex cNum = new Complex(sNum);
		this.Ftransf(row, cNum);
	}

	/**
	 * Transformation F(i,j,α) Adds to row i of a matrix A ∈ C m × n its row j multiplied by the complex α != 0.
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
	 * Transformation F(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α != 0 in string format.
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
	 * Transformation F(i,j,α) Adds to row i of a matrix A ∈ C m × n its row j multiplied by the complex α != 0.
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
	 * Transformation F(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α != 0 in string format.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param sNum The complex number α in text format.
	 */
	public void Ftransf(int rowi, int rowj, String sNum) {
		Complex cNum = new Complex(sNum);    	
		this.Ftransf(rowi, rowj, cNum);
	}    
}
