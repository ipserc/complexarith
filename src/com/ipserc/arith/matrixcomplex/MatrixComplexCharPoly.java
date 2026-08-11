package com.ipserc.arith.matrixcomplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.polynom.Polynom;

/**
 * Package-private CHARACTERISTIC POLYNOMIAL core logic for {@link MatrixComplex} -- the
 * characteristic polynomial itself, the augmented-matrix/unknowns-matrix helpers used to build
 * equation systems, minors/cofactors-by-row-list and the per-order coefficient of the
 * characteristic polynomial (Leverrier-style minor expansion), the row quicksort family, and the
 * Hermitian/skew-Hermitian/commutator/anticommutator operations.
 * <p>
 * Extracted from {@code MatrixComplex.java} (sesion en curso, Etapa 5 de la reestructuracion, ver
 * {@code Claude/ComplexArithRev.md}) -- same pattern as every previous extraction
 * ({@code MatrixComplexFormat}, {@code MatrixComplexFunctions}, {@code MatrixComplexEquationSystems},
 * {@code MatrixComplexRank}, {@code MatrixComplexOrtho}, {@code MatrixComplexKernel},
 * {@code MatrixComplexUnary}): every method here is {@code static}, takes the {@link MatrixComplex}
 * instance as an explicit parameter {@code m}, and reads it only through already-public members.
 * Sibling methods that also moved here are called directly as static methods of this class;
 * everything that stays in the core or lives in another already-extracted class is called qualified
 * as {@code m.xxx(...)} -- in particular {@code cofactor()} calls {@code m.cofactors(row,col)}
 * (the two-arg overload, moved to {@code MatrixComplexUnary} in Etapa 4) and {@code hermitian()}/
 * {@code skewHermitian()} call {@code m.adjoint()} (also {@code MatrixComplexUnary}). No field or
 * method visibility was widened for this extraction. {@code MatrixComplex.java}'s own public methods
 * keep their exact signatures, delegating to these in one line each -- the public API is unchanged.
 * @since VERSION 1.47
 */
class MatrixComplexCharPoly {

	private MatrixComplexCharPoly() {}

	/**
	 * Returns the characteristic polynomial of the matrix.
	 * The characteristic polynomial of a square matrix is a polynomial which is invariant under matrix similarity and has the eigenvalues as roots. It has the determinant and the trace of the matrix as coefficients. The characteristic polynomial of an endomorphism of vector spaces of finite dimension is the characteristic polynomial of the matrix of the endomorphism over any base; it does not depend on the choice of a basis
	 * @param m The matrix.
	 * @return The characteristic polynomial
	 */
	static Polynom charactPoly(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		Polynom charactPoly = new Polynom(colLen);

		if (rowLen != colLen) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		for (int order = 0; order <= colLen; ++order) {
			switch (order) {
				case 0:
					charactPoly.complexMatrix[0][colLen-order].setComplexPol(1, 0);
					break;
				case 1:
					charactPoly.complexMatrix[0][colLen-order] = m.trace().opposite();
					break;
				default:
					charactPoly.complexMatrix[0][colLen-order] = coefCP(m, order).times(Math.pow(-1, order));
					break;
			}
		}
		//m.print(charactPolyMatrix.complexMatrix);
		return (colLen % 2) == 0 ? charactPoly : charactPoly.opposite();
	}

	/**
	 * Returns a new augmented matrix.
	 * Copy the original matrix and add a column to zeros.
	 * @param m The matrix.
	 * @return The augmented matrix.
	 */
	static MatrixComplex augment(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int row, col;

		MatrixComplex extendedMatrix = new MatrixComplex(rowLen, colLen+1);
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				extendedMatrix.complexMatrix[row][col] = m.complexMatrix[row][col];
			}
			extendedMatrix.complexMatrix[row][col].setComplexPol(0,0);
		}
		return extendedMatrix;
	}

	static MatrixComplex augment(MatrixComplex m, int numCols) {
		MatrixComplex extendedMatrix = m;
		for (int iter = 0; iter < numCols; ++iter) {
			extendedMatrix = augment(extendedMatrix);
		}
		return extendedMatrix;
	}

	/**
	 * Shortcut to the default augment method
	 * @param m The matrix.
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	static MatrixComplex augment(MatrixComplex m, MatrixComplex interms) {
		return augment2(m, interms);
	}

	/**
	 * DEPRECATED Returns a new augmented array with the FIRST column of terms.
	 * DEPRECATED Copies the original matrix and add the FIRST column "interms".
	 * DEPRECATED Left only for fail recovery of augment1()
	 * @param m The matrix.
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	static MatrixComplex augment1(MatrixComplex m, MatrixComplex interms) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int row, col;

		MatrixComplex extendedMatrix = new MatrixComplex(rowLen, colLen+1);
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				extendedMatrix.complexMatrix[row][col] = m.complexMatrix[row][col];
			}
			extendedMatrix.complexMatrix[row][col] = interms.complexMatrix[row][0];
		}
		return extendedMatrix;
	}

	/**
	 * Returns a new augmented array with ALL the columns of terms.
	 * Copies the original matrix and add the ALL the columns of "interms".
	 * @param m The matrix.
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	static MatrixComplex augment2(MatrixComplex m, MatrixComplex interms) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int row, col;

		MatrixComplex extendedMatrix = new MatrixComplex(rowLen, colLen+interms.cols());
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				extendedMatrix.complexMatrix[row][col] = m.complexMatrix[row][col];
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
	 * @param m The matrix.
	 * @return The augmented matrix.
	 */
	static MatrixComplex unkMatrix(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int row, col;

		MatrixComplex unkMatrix = new MatrixComplex(rowLen, colLen-1);
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen-1; ++col) {
				unkMatrix.complexMatrix[row][col] = m.complexMatrix[row][col];
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
	private static boolean foundItem(int[] listRows, int item) {
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
	private static boolean notFoundItem(int[] listRows, int item) {
		for (int i = 0; i < listRows.length; ++i) {
			if (listRows[i] == item)
				return false;
		}
		return true;
	}

	/**
	 * Calculates the new cofactor matrix.
	 * @param m The matrix.
	 * @return The new cofactor matrix.
	 */
	static MatrixComplex cofactor(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int row, col;
		MatrixComplex cofactor = new MatrixComplex(rowLen);

		if (rowLen != colLen) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		for (row = 0; row < rowLen; ++row)
			for (col = 0; col < colLen; ++col) {
				cofactor.complexMatrix[row][col] = m.cofactors(row, col).determinant().times(((col+row)&1)==0?1:-1);
			}
		return cofactor;
	}

	/**
	 * Returns a new matrix of cofactors.
	 * @param m The matrix.
	 * @param includedRows The list with the indexes of the rows included in the new matrix.
	 * @return The new matrix of cofactors.
	 */
	static MatrixComplex cofactors(MatrixComplex m, int[] includedRows) {
		int rowLen = m.rows();
		int order = includedRows.length;

		MatrixComplex cofactor = new MatrixComplex(order, order);
		for (int row = 0, rrow = 0; row < rowLen; ++row) {
			if (notFoundItem(includedRows, row))
				continue;
			for (int col = 0, ccol = 0; col < rowLen; ++col) {
				if (notFoundItem(includedRows, col))
					continue;
				cofactor.complexMatrix[rrow][ccol++] = m.complexMatrix[row][col];
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
	 * @param m The matrix.
	 * @param includedRowsList The indexes' list of the rows included in the new matrix as a comma separated string.
	 * @return The new matrix of cofactors.
	 */
	static MatrixComplex cofactors(MatrixComplex m, String includedRowsList) {
		String[] includedRowsTextItems = includedRowsList.split(",");
		int incRows[] = new int[includedRowsTextItems.length];
		for (int i = 0; i < incRows.length; ++i) incRows[i] = Integer.parseInt(includedRowsTextItems[i]);
		incRows = bubbleSort(incRows);
		return cofactors(m, incRows);
	}

	/**
	 * Calculates the determinant of the cofactors' array generated with the included rows (minor)
	 * The determinant of some smaller square matrix, cut down from this by removing one or more of its rows.
	 * @param m The matrix.
	 * @param includedRows Array with the indexes of the included rows to generate the cofactors' matrix.
	 * @return The result of the determinant.
	 */
	static Complex minor(MatrixComplex m, int[] includedRows) {
		return cofactors(m, includedRows).determinant();
	}

	/**
	 * Private method. Returns the exact list of rows included to generate the cofactors' matrix.
	 * Used in coefCP(int order)
	 * @param order The order of the cofactors' matrix.
	 * @param v The array of the rows included.
	 * @return An array with the indexes of the included rows
	 */
	private static int[] includedRows(int order, int[] v) {
		int includedRows[] = new int[order];
		int i;

		for (i = 0; i < order; ++i) includedRows[i] = v[i];
		return includedRows;
	}

	/**
	 * Returns the coefficient of order "order" of the characteristic polynomial from an array
	 * @param m The matrix.
	 * @param order The order of the coefficient
	 * @return The coefficient of the polynomial
	 */
	static Complex coefCP(MatrixComplex m, int order) {
		int grade = m.rows();
		Complex coefCP = new Complex();
		int[] includedRows;
		int i, j;
		int v[] = new int[grade];

		for (i = 0; i < grade; ++i) v[i]=i;
		includedRows = includedRows(order, v);
		coefCP = coefCP.plus(minor(m, includedRows));
		while (true) {
			i = order-1;
			while (v[i] == grade-order+i && --i >= 0);
			if (i < 0) break;
			v[i] += 1;
			for (j = i+1; j < order; ++j) v[j] = v[i]+j-i;
			includedRows = includedRows(order, v);
			coefCP = coefCP.plus(minor(m, includedRows));
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
	 * @param m The matrix, sorted in place.
	 * @param col Index of the column to order.
	 * @param izq Pivot index on the left.
	 * @param der Pivot index on the right.
	 * @param order the type of order to sort. From Max to min, decreasing order = DECREASING = 0. From min to Max, increasing order = INCREASING = 1
	 */
	private static void quicksort(MatrixComplex m, int col, int izq, int der, int order) {
		int colLen = m.cols();
		int i = izq; 									// i realiza la búsqueda de izquierda a derecha
		int j = der; 									// j realiza la búsqueda de derecha a izquierda

		MatrixComplex aux = new MatrixComplex(1, colLen);
		MatrixComplex pivote = new MatrixComplex(1, colLen);
		pivote.complexMatrix[0] = m.complexMatrix[izq].clone();	// tomamos primer elemento como pivote

		while (i < j) {            																		// mientras no se crucen las búsquedas
			while (i < j &&  compare(m.complexMatrix[i][col], pivote.complexMatrix[0][col], order)) ++i;	// busca elemento mayor que pivote
			while (!compare(m.complexMatrix[j][col], pivote.complexMatrix[0][col], order)) --j;			// busca elemento menor que pivote
			if (i < j) {                      									// si no se han cruzado
				aux.complexMatrix[0] = m.complexMatrix[i].clone();			// los intercambia
				m.complexMatrix[i] = m.complexMatrix[j].clone();
				m.complexMatrix[j] = aux.complexMatrix[0].clone();
			}
		}
		m.complexMatrix[izq] = m.complexMatrix[j].clone(); 	// se coloca el pivote en su lugar de forma que tendremos
		m.complexMatrix[j] = pivote.complexMatrix[0].clone(); 	// los menores a su izquierda y los mayores a su derecha
		if (izq < j-1)
			quicksort(m, col, izq, j-1, order); // ordenamos submatrix izquierdo
		if (j+1 < der)
			quicksort(m, col, j+1, der, order); // ordenamos submatrix derecho
	}

	/**
	 * Sorts from maximum to minimum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param m The matrix, sorted in place.
	 * @param col Index of the column to order.
	 */
	static void quicksort(MatrixComplex m, int col) {
		quicksort(m, col, 0, m.rows()-1, DECREASING);
	}

	/**
	 * Sorts from maximum to minimum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param m The matrix, sorted in place.
	 * @param col Index of the column to order.
	 */
	static void quicksortdown(MatrixComplex m, int col) {
		quicksort(m, col, 0, m.rows()-1, DECREASING);
	}

	/**
	 * Sorts from minimum to maximum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param m The matrix, sorted in place.
	 * @param col Index of the column to order.
	 */
	static void quicksortup(MatrixComplex m, int col) {
		quicksort(m, col, 0, m.rows()-1, INCREASING);
	}

	/**
	 * Calculates the Hermitian matrix (or self-adjoint matrix).
	 * The Hermitian matrix (or self-adjoint matrix) is a complex square matrix that is equal to its own conjugate transpose -- that is, the element in the i-th row and j-th column is equal to the complex conjugate of the element in the j-th row and i-th column, for all indices i and j.
	 * Hermitian matrices can be understood as the complex extension of real symmetric matrices.
	 * @param m The matrix.
	 * @return The Hermitian matrix.
	 */
	static MatrixComplex hermitian(MatrixComplex m) {
		return (m.plus(m.adjoint()));
	}

	/**
	 * Calculates the skew-Hermitian or antihermitian.
	 * The skew-Hermitian or antihermitian if its conjugate transpose is equal to the original matrix, with all the entries being of opposite sign.[1] That is, the matrix A is skew-Hermitian if it satisfies the relation.
	 * @param m The matrix.
	 * @return The skew-Hermitian matrix.
	 */
	static MatrixComplex skewHermitian(MatrixComplex m) {
		return (m.minus(m.adjoint()));
	}

	/**
	 * Calculates the commutator between two arrays.
	 * The commutator gives an indication of the extent to which a certain binary operation fails to be commutative.
	 * @param m The matrix.
	 * @param B the second array.
	 * @return The commutator array.
	 */
	static MatrixComplex commutator(MatrixComplex m, MatrixComplex B) {
		return (m.times(B)).minus(B.times(m));
	}

	/**
	 * Calculates the anticommutator between two arrays.
	 * @param m The matrix.
	 * @param B the second array.
	 * @return The anticommutator array.
	 */
	static MatrixComplex anticommutator(MatrixComplex m, MatrixComplex B) {
		return (m.times(B)).plus(B.times(m));
	}

}
