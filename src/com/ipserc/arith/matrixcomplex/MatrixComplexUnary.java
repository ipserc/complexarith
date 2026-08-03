package com.ipserc.arith.matrixcomplex;

import com.ipserc.arith.complex.Complex;

/**
 * Package-private UNARY OPERATORS core logic for {@link MatrixComplex} -- boolean predicates
 * (empty/singular/normal/unitary/diagonal/orthogonal/Hessenberg/infinite/NaN/null), dimension and
 * condition numbers, the basic unary transformations (trace/cotrace/opposite/transpose/conjugate/
 * adjoint), minors/cofactors/adjugate-adjunct, matrix inversion and triangularization shortcuts,
 * the three determinant implementations (Gauss/Sarrus-for-3x3/cofactor-expansion), the symmetry/
 * hermiticity family, and identity/(semi)definiteness predicates.
 * <p>
 * Extracted from {@code MatrixComplex.java} (Decimocuarta sesion, Etapa 4 de la reestructuracion,
 * ver {@code Claude/ComplexArithRev.md}) -- same pattern as every previous extraction
 * ({@code MatrixComplexFormat}, {@code MatrixComplexFunctions}, {@code MatrixComplexEquationSystems},
 * {@code MatrixComplexRank}, {@code MatrixComplexOrtho}, {@code MatrixComplexKernel}): every method
 * here is {@code static}, takes the {@link MatrixComplex} instance as an explicit parameter {@code m},
 * and reads it only through already-public members. Sibling methods that also moved here (e.g.
 * {@code adjoint()}, {@code determinant()}, {@code inverse()}, {@code isSquare()} is the one
 * exception -- it stayed in {@code MatrixComplexRank}) are called directly as static methods of this
 * class; everything that stays in the core or lives in another already-extracted class is called
 * qualified as {@code m.xxx(...)}. {@code MatrixComplex.java}'s own public methods keep their exact
 * signatures, delegating to these in one line each -- the public API is unchanged.
 * <p>
 * One field widened for this extraction: {@code mSign} (private -> package-private), read by
 * {@code determinantGauss()} off the {@code MatrixComplex} instance returned by {@code triangle()}
 * (set by {@code triangleUp()} in {@code MatrixComplexRank}, Chio's rule sign tracking). Confirmed by
 * grep that {@code mSign} has no reader/writer anywhere outside {@code MatrixComplex.java} itself, so
 * widening it to package-private (rather than the more permissive {@code protected}) is safe -- no
 * subclass in another package touches it directly.
 * <p>
 * <b>Bug fixed as part of this extraction, at the user's explicit request</b>: {@code adjunct(int[])}
 * called itself instead of delegating to {@code adjugate(int[])} (a plain copy-paste typo -- every
 * other {@code adjunct(...)} overload correctly delegates to its {@code adjugate(...)} sibling),
 * guaranteeing a {@code StackOverflowError} on every call. Confirmed zero callers anywhere in the
 * project (grepped before fixing) -- the bug was never exercised by any test or real code path before
 * this session's verification driver hit it by exhaustively exercising every method in the section.
 * @since VERSION 1.46
 */
class MatrixComplexUnary {

	private MatrixComplexUnary() {}

	/**
	 * Checks if a matrix is empty, A matrix is empty if rows = cols = 0
	 * @param m The matrix.
	 * @return True is matrix is empty
	 */
	static boolean isEmpty(MatrixComplex m) {
		if (m.rows() == 0 && m.cols() == 0) return true;
		return false;
	}

	/**
	 * Makes the matrix to become positive semidefinite
	 * @param m The matrix.
	 */
	static void abs(MatrixComplex m) {
		for (int row = 0; row < m.rows(); ++row)
			for (int col = 0; col < m.cols(); ++col)
				m.setItem(row, col, m.getItem(row, col).abs());
	}

	/**
	 * Checks whether the matrix is singular or not (determinant = 0)
	 * @param m The matrix.
	 * @return True if the matrix is singular, false otherwise
	 */
	static boolean isSingular(MatrixComplex m) {
		return determinant(m).equals(Complex.ZERO);
	}

	/**
	 * Normal matrices: A*A.adjoint() = A.adjoint()*A
	 * @param m The matrix.
	 * @return True if the matrix is normal, false otherwise
	 */
	static boolean isNormal(MatrixComplex m) {
		if (!m.isSquare()) return false;
		return m.times(adjoint(m)).equals(adjoint(m).times(m));
	}

	/**
	 * Normal matrices: A square and A*A.adjoint() = A.adjoint()*A = I
	 * @param m The matrix.
	 * @return True if the matrix is normal, false otherwise
	 */
	static boolean isUnitary(MatrixComplex m) {
		if (!m.isSquare()) return false;
		return adjoint(m).times(m).equals(eye(m.rows()));
	}

	/**
	 * Checks whether a Matrix is diagonal or not
	 * @param m The matrix.
	 * @return True if the matrix is diagonal, false otherwise
	 */
	static boolean isDiagonal(MatrixComplex m) {
		if (!m.isSquare()) return false;
		for (int row = 0; row < m.rows(); ++row)
			for (int col = 0; col < m.cols(); ++col) {
				if (row != col) {
					if (!m.getItem(row, col).equals(Complex.ZERO)) return false;
				}
			}
		return true;
	}

	/**
	 * Checks whether a Matrix is orthogonal or not
	 * @param m The matrix.
	 * @return True if the matrix is orthogonal, false otherwise
	 */
	static boolean isOrthogonal(MatrixComplex m) {
		if (!m.isSquare()) return false;
		if (determinant(m).isZero()) return false;
		//return isDiagonal(m) && adjoint(m).times(m).equals(eye(m.rows()));
		//return m.times(adjoint(m)).determinant().abs() - 1 <= Complex.zero_threshold_approx();
		/*
		 * I've observed that this happens if the basis is orthogonal.
		 * m.times(adjoint(m)).isDiagonal();
		 * This will resolve the duality between orthogonal matrices and orthogonal bases of a vector space, allowing a single orthogonality method to be defined for matrices.
		 * adjoint(m).times(m), on the other hand, does not satisfy this property.
		 */
		//return m.times(adjoint(m)).isDiagonal();
		//Una matriz ortogonal es una matriz cuadrada cuya matriz inversa coincide con su matriz traspuesta conjugada.
		return adjoint(m).equals(inverse(m));
		// return m.isUnitary();
	}

	/**
	 * Checks whether a Matrix is orthonormal or not. Othonormal and Orthogonal are the same concept in Matrices. BAD!!!!
	 * @param m The matrix.
	 * @return True if the matrix is orthonormal, false otherwise
	 */
	static boolean isOrthonormal(MatrixComplex m) {
		return isOrthogonal(m);
	}

	/**
	 * Upper Hessenberg matrices: a(i,j) = 0 for any pair i, j such that i > j + 1.
	 * @param m The matrix.
	 * @return True if the matrix is upper Hessenberg, false otherwise
	 */
	static boolean isHessenbergUpper(MatrixComplex m) {
		for (int row = 0; row < m.rows(); ++row)
			for (int col = 0; col <= row; ++col)
				if (!m.getItem(row,col).equals(Complex.ZERO)) return false;
		return true;
	}

	/**
	 * Upper Hessenberg matrices: a(i,j) = 0 for any pair i, j such that i <= j.
	 * @param m The matrix.
	 * @return True if the matrix is lower Hessenberg, false otherwise
	 */
	static boolean isHessenbergLower(MatrixComplex m) {
		for (int row = 0; row < m.rows(); ++row)
			for (int col = row; col < m.cols(); ++col)
				if (!m.getItem(row,col).equals(Complex.ZERO)) return false;
		return true;
	}

	/**
	 * Checks if at least one of the values of the array is infinite
	 * @param m The matrix.
	 * @return True if one infinite value is found
	 */
	static boolean isInfinite(MatrixComplex m) {
		for (int row = 0; row < m.rows(); ++row)
			for (int col = 0; col < m.cols(); ++col)
				if (m.getItem(row, col).isInfinite()) return true;
		return false;
	}

	/**
	 * Checks if at least one of the values of the array is NaN
	 * @param m The matrix.
	 * @return True if one NaN value is found
	 */
	static boolean isNaN(MatrixComplex m) {
		for (int row = 0; row < m.rows(); ++row)
			for (int col = 0; col < m.cols(); ++col)
				if (m.getItem(row, col).isNaN()) return true;
		return false;
	}

	/**
	 * Checks if the matrix is null compared with Complex.ZERO.
	 * @param m The matrix.
	 * @return true if the matrix is null, otherwise false.
	 */
	static boolean isNullC(MatrixComplex m) {
		for (int row = 0; row < m.rows(); ++row)
			for (int col = 0; col < m.cols(); ++col)
				if (!m.getItem(row, col).equals(Complex.ZERO)) return false;
		return true;
	}

	/**
	 * Checks if the matrix is null compared with Complex.ZERO.
	 * @param m The matrix.
	 * @return true if the matrix is null, otherwise false.
	 */
	static boolean isNull(MatrixComplex m) {
		for (int row = 0; row < m.rows(); ++row)
			for (int col = 0; col < m.cols(); ++col)
				if (!m.getItem(row, col).equals(Complex.ZERO)) return false;
		return true;
	}

	/**
	 * The dimension of the matrix as a product of the number of rows by the number of columns.
	 * @param m The matrix.
	 * @return The matrix dimension.
	 */
	static int dim(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		return rowLen * colLen;
	}

	/**
	 * Returns the condition number of the array using the p norm, where p is the order of the norm.
	 * @param m The matrix.
	 * @param p The order of the norm.
	 * @return The condition number
	 */
	static double cond_p(MatrixComplex m, int p) {
		return m.p_norm(p) * inverse(m).p_norm(p);
	}

	/**
	 * Returns the condition number of the array using the euclidean norm
	 * @param m The matrix.
	 * @return The condition number
	 */
	static double cond_f(MatrixComplex m) {
		return m.f_norm() * inverse(m).f_norm();
	}

	/**
	 * Returns the condition number of the array using the infinite norm
	 * @param m The matrix.
	 * @return The condition number
	 */
	static double cond_inf(MatrixComplex m) {
		return m.inf_norm() * inverse(m).inf_norm();
	}

	/**
	 * Returns the condition number of the array using the infinite norm.
	 * Short cut to cond_imf()
	 * @param m The matrix.
	 * @return The condition number
	 */
	static double cond(MatrixComplex m) {
		return cond_inf(m);
	}

	/**
	 * Trace of an n-by-n square matrix A - the sum of the elements on the main diagonal.
	 * @param m The matrix.
	 * @return The value of the trace.
	 */
	static Complex trace(MatrixComplex m) {
		if (!m.isSquare()) {
			throw new IllegalArgumentException("Not valid trace: The matrix has to be square.");
		}
		int rowLen = m.rows();
		Complex trace = new Complex();

		for (int i = 0; i < rowLen; ++i)
			trace = trace.plus(m.complexMatrix[i][i]);
		return trace;
	}

	/**
	 * Cotrace of an n-by-n square matrix A - the sum of the elements on the secondary diagonal.
	 * @param m The matrix.
	 * @return The value of the trace.
	 */
	static Complex cotrace(MatrixComplex m) {
		if (!m.isSquare()) {
			throw new IllegalArgumentException("Not valid cotrace: The matrix has to be square.");
		}
		int rowLen = m.rows();
		Complex cotrace = new Complex();

		int col = rowLen - 1;
		for (int i = 0; i < rowLen; ++i)
			cotrace = cotrace.plus(m.complexMatrix[i][col--]);
		return cotrace;
	}

	/**
	 * Calculates the opposite of the matrix.
	 * @param m The matrix.
	 * @return The matrix opposite.
	 */
	static MatrixComplex opposite(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();

		MatrixComplex cMatrix = new MatrixComplex(colLen, rowLen);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[row][col] = m.complexMatrix[row][col].opposite();
		return cMatrix;
	}

	/**
	 * Transpose of the matrix by reflecting it over its main diagonal.
	 * @param m The matrix.
	 * @return The matrix transposed.
	 */
	static MatrixComplex transpose(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();

		MatrixComplex cMatrix = new MatrixComplex(colLen, rowLen);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[col][row] = m.complexMatrix[row][col];
		return cMatrix;
	}

	/**
	 * Calculates the conjugate of the matrix.
	 * Matrix complex conjugate is a new matrix with equal real part and imaginary part equal in magnitude but opposite in sign.
	 * @param m The matrix.
	 * @return The matrix conjugated.
	 */
	static MatrixComplex conjugate(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();

		MatrixComplex cMatrix = new MatrixComplex(rowLen, colLen);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[row][col] = m.complexMatrix[row][col].conjugate();
		return cMatrix;
	}

	/**
	 * Calculates the adjoint of the matrix.
	 * The adjoint is the transposed conjugated matrix.
	 * @param m The matrix.
	 * @return The new matrix adjoint.
	 */
	static MatrixComplex adjoint(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();

		MatrixComplex cMatrix = new MatrixComplex(colLen, rowLen);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[col][row] = m.complexMatrix[row][col].conjugate();
		return cMatrix;
	}

	/**
	 * Minor for row "rowPivot" and column "colPivot".
	 * The minor the Matrix resultant of removing the row "rowPivot" and column "colPivot".
	 * @param m The matrix.
	 * @param rowPivot The index of the row to eliminate.
	 * @param colPivot The index of the column to eliminate.
	 * @return The minors' matrix.
	 */
	static MatrixComplex minor(MatrixComplex m, int rowPivot, int colPivot) {
		int rowLen = m.rows();
		int colLen = m.cols();

		if (rowPivot < 0 || rowPivot > rowLen) {
			throw new IllegalArgumentException("Not valid minor: The row to pivot is incorrect.");
		}

		if (colPivot < 0 || colPivot > colLen) {
			throw new IllegalArgumentException("Not valid minor: The col to pivot is incorrect.");
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLen-1, colLen-1);

		for (int row = 0, rowf = 0; row < rowLen; ++row) {
			if (row == rowPivot)
				continue;
			for (int col = 0, colf = 0; col < colLen; ++col) {
				if (col == colPivot)
					continue;
				resultMatrix.complexMatrix[rowf][colf++] = m.complexMatrix[row][col];
			}
			++rowf;
		}
		return resultMatrix;
	}

	/**
	 * Matrix of Cofactors order 1 for row "rowPivot" and column "colPivot".
	 * The co-factor of an element of the matrix is equal to the product of the minor of the element and -1 to the power of the positional value of the element.
	 * @param m The matrix.
	 * @param rowPivot The index of the row minor.
	 * @param colPivot The index of the column minor.
	 * @return The cofactors' matrix.
	 */
	static MatrixComplex cofactors(MatrixComplex m, int rowPivot, int colPivot) {
		int rowLen = m.rows();
		int colLen = m.cols();

		if (rowPivot < 0 || rowPivot > rowLen) {
			throw new IllegalArgumentException("Not valid cofactor: The row to pivot is incorrect.");
		}

		if (colPivot < 0 || colPivot > colLen) {
			throw new IllegalArgumentException("Not valid cofactor: The col to pivot is incorrect.");
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLen-1, colLen-1);

		for (int row = 0, rowf = 0; row < rowLen; ++row) {
			if (row == rowPivot)
				continue;
			for (int col = 0, colf = 0; col < colLen; ++col) {
				if (col == colPivot)
					continue;
				resultMatrix.complexMatrix[rowf][colf++] = m.complexMatrix[row][col].times(Math.pow(-1, row+col));
			}
			++rowf;
		}
		return resultMatrix;
	}

	/**
	 * Calculates the adjugate of an square matrix.
	 * Adjugate or classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param m The matrix.
	 * @return The adjugate matrix.
	 */
	static MatrixComplex adjugate(MatrixComplex m) {
		return m.cofactor().transpose();
	}

	/**
	 * Calculates the adjugate of an square matrix.
	 * Adjunct or adjugate, or classical adjoint, of a square matrix is the transpose of its cofactor matrix.
	 * @param m The matrix.
	 * @return The adjunct matrix.
	 */
	static MatrixComplex adjunct(MatrixComplex m) {
		return adjugate(m);
	}

	/**
	 * Calculates the adjugate for a row and a column.
	 * Adjugate or classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param m The matrix.
	 * @param rowPivot The index of the row to remove.
	 * @param colPivot The index of the column to remove.
	 * @return The adjugate matrix.
	 */
	static MatrixComplex adjugate(MatrixComplex m, int rowPivot, int colPivot) {
		return cofactors(m, rowPivot, colPivot).transpose();
	}

	/**
	 * Calculates the adjunct for a row and a column.
	 * Adjunct or adjugate, or classical adjoint, of a square matrix is the transpose of its cofactor matrix.
	 * @param m The matrix.
	 * @param rowPivot The index of the row to remove.
	 * @param colPivot The index of the column to remove.
	 * @return The adjunct matrix.
	 */
	static MatrixComplex adjunct(MatrixComplex m, int rowPivot, int colPivot) {
		return adjugate(m, rowPivot, colPivot);
	}

	/**
	 * Calculates the adjugate matrix of the rows passed in the parameter "includedRows".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param m The matrix.
	 * @param includedRows A list with the indexes of the the rows included in the cofactors array.
	 * @return The adjugate matrix.
	 */
	static MatrixComplex adjugate(MatrixComplex m, int[] includedRows) {
		return m.cofactors(includedRows).transpose();
	}

	/**
	 * Calculates the adjunct matrix of the rows passed in the parameter "includedRows".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * <p>
	 * FIX (Etapa 4, Decimocuarta sesion): delegated to {@code adjugate(m, includedRows)} -- the
	 * original body called itself ({@code m.adjunct(includedRows)}), an infinite recursion that
	 * guaranteed a {@code StackOverflowError} on every call. Zero callers anywhere in the project.
	 * @param m The matrix.
	 * @param includedRows A list with the indexes of the the rows included in the cofactors array.
	 * @return The adjunct matrix.
	 */
	static MatrixComplex adjunct(MatrixComplex m, int[] includedRows) {
		return adjugate(m, includedRows);
	}

	/**
	 * Calculates the adjugate matrix of the rows passed in the parameter "includedRowsList".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param m The matrix.
	 * @param includedRowsList A list with the rows included in the cofactors array as a comma separated string.
	 * @return The adjugate matrix.
	 */
	static MatrixComplex adjugate(MatrixComplex m, String includedRowsList) {
		return m.cofactors(includedRowsList).transpose();
	}

	/**
	 * Calculates the adjunct matrix of the rows passed in the parameter "includedRowsList".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param m The matrix.
	 * @param includedRowsList A list with the rows included in the cofactors array as a comma separated string.
	 * @return The adjunct matrix.
	 */
	static MatrixComplex adjunct(MatrixComplex m, String includedRowsList) {
		return adjugate(m, includedRowsList);
	}

	/**
	 * The inverse of the matrix calculated by Gauss-Jordan elimination method
	 * Gauss-Jordan elimination method can be used for finding the inverse of a matrix, if it exists.
	 * If A is a n by n square matrix, then row reduction can be used to compute its inverse matrix, if it exists.
	 * First, the n by n identity matrix is augmented to the right of A, forming a n by 2n block matrix [A | I].
	 * Now through application of elementary row operations, finds the reduced echelon form of this n by 2n matrix.
	 * The matrix A is invertible if and only if the left block can be reduced to the identity matrix I; in this case
	 * the right block of the final matrix is A⁻¹. If the algorithm is unable to reduce the left block to I,
	 * then A is not invertible.
	 * @param m The matrix.
	 * @return The inverse matrix.
	 */
	static MatrixComplex inverse(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		Complex cCoef = new Complex();
		int row, col;

		if (rowLen != colLen) {
			System.err.println(MatrixComplex.HEADINFO + "inverse: Not valid matrix: The matrix has to be square.");
			return m.divides(0);
		}

		if (determinant(m).equals(0,0) ) {
			//System.err.println(MatrixComplex.HEADINFO + "inverse: Not valid matrix: The matrix determinat is ZERO.");
			return m.divides(0);
		}

		MatrixComplex auxMatrix = m.copy();
		MatrixComplex unitMatrix = new MatrixComplex(rowLen); unitMatrix.initMatrixDiag(1,0);

		for (int k = 0; k < rowLen-1; ++k) {
			// Proactive partial pivoting: always swap to the row with the maximum modulus in this
			// column, not only when the current pivot is exactly zero -- a pivot that is merely
			// small (but nonzero within Complex.equals() tolerance) still amplifies rounding error.
			int rowSwap = auxMatrix.partialPivot(k);
			if (rowSwap == -1)
				return auxMatrix.divides(0);
			if (rowSwap != k) {
				auxMatrix.swapRows(k, rowSwap);
				unitMatrix.swapRows(k, rowSwap);
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
	 * @param m The matrix.
	 * @return The  upper triangular matrix.
	 */
	static MatrixComplex triangle(MatrixComplex m) {
		return m.triangleUp();
	}

	/**
	 * Generates a diagonal matrix using triangularization Low and then Up
	 * @param m The matrix.
	 * @return The diagonal matrix
	 */
	static MatrixComplex diagonalLo(MatrixComplex m) {
		return (m.triangleLo()).triangleUp();
	}

	/**
	 * Generates a diagonal matrix using triangularization Up and then Lo
	 * @param m The matrix.
	 * @return The diagonal matrix
	 */
	static MatrixComplex diagonalUp(MatrixComplex m) {
		return (m.triangleUp()).triangleLo();
	}

	/**
	 * Shortcut to determinantGauss.
	 * Calculate the matrix determinant by the default rule (Gauss)
	 * @param m The matrix.
	 * @return The value of the determinant.
	 */
	static Complex determinant(MatrixComplex m) {
		return determinantGauss(m);
	}

	/**
	 * Calculates the matrix determinant by the Gauss' method.
	 * @param m The matrix.
	 * @return The value of the determinant.
	 */
	static Complex determinantGauss(MatrixComplex m) {
		int rowLen = m.rows();

		if (rowLen != m.cols()) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		Complex cResult = new Complex(1, 0);
		MatrixComplex auxMatrix = triangle(m);
		for (int iter = 0; iter < rowLen; ++iter) {
			cResult = cResult.times(auxMatrix.complexMatrix[iter][iter]);
		}
		return cResult.times(auxMatrix.mSign);
	}

	/**
	 * Private method that calculates the matrix 3x3 determinant by the Sarrus' rule.
	 * @param m The matrix.
	 * @return The value of the determinant.
	 */
	private static Complex determinant3(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int k = 0;
		Complex cGroup = new Complex(1,0);
		Complex determinant = new Complex();

		if (rowLen != colLen) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		for (int row = 0; row < rowLen; ++row) {
			k = 0;
			for (int col = 0; col < colLen; ++col) {
				cGroup = cGroup.times(m.complexMatrix[(row+k++)%rowLen][col]);
			}
			determinant = determinant.plus(cGroup);
			cGroup.setComplexPol(1, 0);

		}
		for (int row = rowLen-1; row >= 0; --row) {
			k = rowLen-1;
			for (int col = 0; col < colLen; ++col) {
				cGroup = cGroup.times(m.complexMatrix[(row+k--)%rowLen][col]);
			}
			determinant = determinant.minus(cGroup);
			cGroup.setComplexRec(1, 0);
		}
		return determinant;
	}

	/**
	 * Calculates the matrix determinant through matrix of adjoints (cofactors)
	 * DO NOT USE FOR MATRIX OVER 5x5.
	 * @param m The matrix.
	 * @return The value of the determinant.
	 */
	static Complex determinantAdj(MatrixComplex m) {
		Complex cSum = new Complex();
		int rowLen = m.rows();
		if (rowLen == 1)
			return m.complexMatrix[0][0];

		int colLen = m.cols();

		if (rowLen != colLen) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		if (rowLen == 2)
			return (m.complexMatrix[0][0].times(m.complexMatrix[1][1])).
					minus (m.complexMatrix[0][1].times(m.complexMatrix[1][0]));

		if (rowLen == 3) {  //bottom case of recursion. size 1 complexMatrix determinant is itself.
			return determinant3(m);
		}

		for (int i = 0; i < rowLen; ++i){ //finds determinant using row-by-row expansion
			MatrixComplex smaller = new MatrixComplex(rowLen - 1, colLen - 1); //creates smaller complexMatrix- values not in same row, column
			for (int a = 1; a < rowLen; ++a) {
				for (int b = 0; b < colLen; ++b) {
					if (b < i) {
						smaller.complexMatrix[a-1][b] = m.complexMatrix[a][b];
					}
					else if (b > i) { smaller.complexMatrix[a-1][b-1] = m.complexMatrix[a][b];
					}
				}
			}
			if ((i&1) == 0) cSum = cSum.plus (m.complexMatrix[0][i].times(determinantAdj(smaller)));
			else cSum = cSum.minus(m.complexMatrix[0][i].times(determinantAdj(smaller)));
		}
		return cSum ; //returns determinant value. once stack is finished, returns final determinant.
	}

	/**
	 * Checks if the matrix is symmetric or not
	 * @param m The matrix.
	 * @return True if the matrix is symmetric
	 */
	static boolean isSymmetric(MatrixComplex m) {
		if (m.rows() != m.cols()) return false;
		for (int row = 0; row < m.rows(); ++row)
			for (int col = row; col < m.cols(); ++col)
				if(!m.getItem(row, col).equals(m.getItem(col, row))) return false;
		return true;
	}

	/**
	 * Checks if the matrix is antisymmetric or not
	 * @param m The matrix.
	 * @return True if the matrix is antisymmetric
	 */
	static boolean isAntiSymmetric(MatrixComplex m) {
		if (m.rows() != m.cols()) return false;
		for (int row = 0; row < m.rows(); ++row)
			for (int col = row; col < m.cols(); ++col)
				if(row == col) {
					if (!m.getItem(row, col).equals(Complex.ZERO)) return false;
				}
				else if(!m.getItem(row, col).equals(m.getItem(col, row).opposite().conjugate())) return false;
		return true;
	}

	/**
	 * Checks if the matrix is skew-symmetric or not
	 * @param m The matrix.
	 * @return True if the matrix is skew-symmetric
	 */
	static boolean isSkewSymmetric(MatrixComplex m) {
		return isAntiSymmetric(m);
	}

	/**
	 * Checks if the matrix is hermitian or not
	 * @param m The matrix.
	 * @return True if the matrix is hermitian
	 */
	static boolean isHermitian(MatrixComplex m) {
		if (m.rows() != m.cols()) return false;
		for (int row = 0; row < m.rows(); ++row)
			for (int col = row; col < m.cols(); ++col)
				if(row == col) {
					if (!m.getItem(row, col).isPureReal()) return false;
				}
				else if(!m.getItem(row, col).equals(m.getItem(col, row).conjugate())) return false;
		return true;
	}

	/**
	 * Checks if the matrix is antihermitian or not
	 * @param m The matrix.
	 * @return True if the matrix is antihermitian
	 */
	static boolean isAntiHermitian(MatrixComplex m) {
		if (m.rows() != m.cols()) return false;
		for (int row = 0; row < m.rows(); ++row)
			for (int col = row; col < m.cols(); ++col)
				if(row == col) {
					if (!m.getItem(row, col).isPureImaginary()) return false;
				}
				else if(!m.getItem(row, col).equals(m.getItem(col, row).opposite().conjugate())) return false;
		return true;
	}

	/**
	 * Checks if the matrix is skew-hermitian or not
	 * @param m The matrix.
	 * @return True if the matrix is skew-hermitian
	 */
	static boolean isSkewHermitian(MatrixComplex m) {
		return isAntiHermitian(m);
	}

	/**
	 * Method for creating an Square Identity array of "dim" size
	 * @param dim The size of Identity array
	 * @return The Identity array
	 */
	static MatrixComplex eye(int dim) {
		MatrixComplex eye = new MatrixComplex(dim);
		eye.initMatrixDiag(1,0);
		return eye;
	}

	/**
	 * @param m The matrix.
	 * @return whether m is positive definite
	 */
	static boolean isPostiveDefinite(MatrixComplex m) {
		if (isHermitian(m)) {
			Eigenspace eigenSpace = new Eigenspace(m);
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
	 * @param m The matrix.
	 * @return whether m is positive semidefinite
	 */
	static boolean isPostiveSemiDefinite(MatrixComplex m) {
		if (isHermitian(m)) {
			Eigenspace eigenSpace = new Eigenspace(m);
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
	 * @param m The matrix.
	 * @return whether m is negative definite
	 */
	static boolean isNegtiveDefinite(MatrixComplex m) {
		if (isHermitian(m)) {
			Eigenspace eigenSpace = new Eigenspace(m);
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
	 * @param m The matrix.
	 * @return whether m is negative semidefinite
	 */
	static boolean isNegtiveSemiDefinite(MatrixComplex m) {
		if (isHermitian(m)) {
			Eigenspace eigenSpace = new Eigenspace(m);
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
	 * @param m The matrix.
	 * @return True if a zero was found, false otherwise.
	 */
	static boolean hasZeroMainDiag(MatrixComplex m) {
		for (int i=0; i < m.rows(); ++i)
			if (m.getItem(i, i).isZero()) return true;
		return false;
	}

	/**
	 * Checks if there is one item on the main diagonal for which its REAL PART is zero or negative .
	 * @param m The matrix.
	 * @return False if a non positive was found, false otherwise.
	 */
	static boolean repPositiveMainDiag(MatrixComplex m) {
		for (int i=0; i < m.rows(); ++i)
			if (m.getItem(i, i).rep() < 0) return false;
		return true;
	}

	/**
	 * Method for creating an Square Identity array of "m" matrix size
	 * @param m The matrix.
	 * @return The Identity array
	 */
	static MatrixComplex eye(MatrixComplex m) {
		MatrixComplex eye = new MatrixComplex(m.rows());
		eye.initMatrixDiag(1,0);
		return eye;
	}

	/**
	 * Copies the 1xN values of a one row array and put one by one in a diagonal NxN matrix
	 * @param values The values of a one row array
	 * @return The diagonal NxN matrix
	 */
	static MatrixComplex diagonal(MatrixComplex values) {
		MatrixComplex newValArray = values.clone();
		if (newValArray.rows() == 1) newValArray = newValArray.transpose();

		MatrixComplex sqDiagonal = new MatrixComplex(newValArray.rows());
		for (int row = 0; row < newValArray.rows(); ++row) {
			sqDiagonal.setItem(row, row, newValArray.getItem(row, 0));
		}
		return sqDiagonal;
	}
}
