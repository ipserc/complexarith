package com.ipserc.arith.matrixcomplex;

/**
 * Package-private presentation/export logic for {@link MatrixComplex} -- string representations
 * (default {@code toString()}, console printing, constructor-statement round-trip) and export to
 * the syntax of external CAS tools (Maxima, Wolfram, Matlab/Octave).
 * <p>
 * Extracted from {@code MatrixComplex.java} (Undecima sesion, ver {@code Claude/ComplexArithRev.md})
 * to separate presentation from the algebraic core, same pattern already used for
 * {@code Complex.java}'s {@code ComplexFormat} (Sexta sesion, Paso 2) and {@code Polynom.java}'s
 * {@code PolynomFormat} (Decima sesion): every method here is {@code static}, takes the
 * {@link MatrixComplex} instance as an explicit parameter, and reads it only through already-public
 * members ({@code complexMatrix}, {@code rows()}, {@code cols()}, {@code isEmpty()},
 * {@code toMatlab()}) plus the {@code MatrixComplex(int,int)} constructor. {@code MatrixComplex.java}'s
 * own public methods keep their exact signatures, delegating to these in one line each -- the public
 * API is unchanged.
 */
class MatrixComplexFormat {

	/**
	 * Prints the array of values without carriage return.
	 * @param m The matrix.
	 */
	static void print(MatrixComplex m) {
		printCM(m);
	}

	/**
	 * Prints the array of values with a carriage return.
	 * @param m The matrix.
	 */
	static void println(MatrixComplex m) {
		printCM(m);
		System.out.print('\n');
	}

	/**
	 * Prints a title and then in a new line the array of values without carriage return.
	 * @param m The matrix.
	 * @param caption The title above the matrix.
	 */
	static void print(MatrixComplex m, String caption) {
		int rowLen = m.rows();

		if (rowLen > 1) System.out.println(caption);
		else System.out.print(caption+" ");
		print(m);
	}

	/**
	 * Prints a title and then in a new line the array of values with a carriage return.
	 * @param m The matrix.
	 * @param caption The title above the matrix.
	 */
	static void println(MatrixComplex m, String caption) {
		int rowLen = m.rows();

		if (rowLen > 1) System.out.println(caption);
		else System.out.print(caption+" ");
		println(m);
	}

	/**
	 * Private method that presents the matrix enclosed in brackets.
	 * Each line corresponds to a row of the array.
	 * The columns are separated by commas.
	 * @param m The matrix.
	 * @return The string representation of the matrix.
	 */
	static String toString(MatrixComplex m) {
		if (m.isEmpty()) return "[]";
		int rowLen = m.rows();
		int colLen = m.cols();
		String toString = new String();
		String itemStr;
		for (int row = 0; row < rowLen; ++row) {
			toString += "[ ";
			for (int col = 0; col < colLen; ++col) {
				itemStr = m.complexMatrix[row][col].toString();
				toString += itemStr;
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
	 * @param m The matrix.
	 */
	private static void printCM(MatrixComplex m) {
		System.out.print(toString(m));
	}

	/**
	 * Displays a title and selected column
	 * @param m The matrix.
	 * @param col The selected column
	 * @param caption The title
	 */
	static void println(MatrixComplex m, int col, String caption) {
		int rowLen = m.rows();

		MatrixComplex cMatrix = new MatrixComplex(rowLen, 1);
		for (int row = 0; row < rowLen; ++row) cMatrix.complexMatrix[row][0] = m.complexMatrix[row][col];

		println(cMatrix, caption);
	}

	/**
	 * Returns a string with the array expression in the format used by Maxima (Computer Algebra System)
	 * @param m The matrix.
	 * @return The string with the array in Maxima format.
	 */
	static String toMaxima(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		String matrixMaxima = new String();

		for (int row = 0; row < rowLen; ++row) {
			matrixMaxima += "[";
			for (int col = 0; col < colLen; ++col) {
				matrixMaxima += m.complexMatrix[row][col].toString();
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
	 * @param m The matrix.
	 * @return The string with the array in Wolfram Mathematica format.
	 */
	static String toWolfram(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		String matrixWolfram = new String();
		matrixWolfram += "{";

		for (int row = 0; row < rowLen; ++row) {
			matrixWolfram += "{";
			for (int col = 0; col < colLen; ++col) {
				matrixWolfram += m.complexMatrix[row][col].toStringRecWolfram();
				matrixWolfram += (col == colLen-1 ? "}" : ",");
			}
			matrixWolfram += (row == rowLen-1 ? "}" : ",");
		}
		return matrixWolfram.replace("E", "*10^");
	}

	/**
	 * Returns a string with the array expression in the format used by Matlab.
	 * @param m The matrix.
	 * @return The string with the array in Matlab format.
	 */
	static String toMatlab(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		String matrixWolfram = new String();
		matrixWolfram += "[";

		for (int row = 0; row < rowLen; ++row) {
			for (int col = 0; col < colLen; ++col) {
				matrixWolfram += m.complexMatrix[row][col].toString();
				matrixWolfram += (col == colLen-1 ? "" : ",");
			}
			matrixWolfram += (row == rowLen-1 ? "]" : ";");
		}
		return matrixWolfram;
	}

	/**
	 * Returns a string with the array expression in the format used by GNU Octave.
	 * @param m The matrix.
	 * @return The string with the array in GNU Octave format.
	 */
	static String toOctave(MatrixComplex m) {
		return toMatlab(m);
	}

	/**
	 * Prepares the matrix string for toMatrixComplex
	 * @param m The matrix.
	 * @return the matrix string
	 */
	static String preMatrixComplex(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		String preMatrixComplex = new String();
		int row, col;

		preMatrixComplex = "\"";
		for (row = 0; row < rowLen-1; ++row) {
			for (col = 0; col < colLen; ++col) {
				preMatrixComplex += m.complexMatrix[row][col].toString();
				preMatrixComplex += (col == colLen-1 ? ";" : ",");
			}
		}
		for (col = 0; col < colLen; ++col) {
			preMatrixComplex += m.complexMatrix[row][col].toString();
			preMatrixComplex += (col == colLen-1 ? "\"" : ",");
		}
		return preMatrixComplex;
	}

	/**
	 * Returns a string with the array expression in the format used by Matrix Complex.
	 * @param m The matrix.
	 * @return The string with the array in MatrixComplex format.
	 */
	static String toMatrixComplex(MatrixComplex m) {
		String toMatrixComplex;
		toMatrixComplex = "new MatrixComplex(" + preMatrixComplex(m) +");";
		return toMatrixComplex;
	}

}
