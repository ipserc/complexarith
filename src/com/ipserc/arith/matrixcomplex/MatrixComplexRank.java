package com.ipserc.arith.matrixcomplex;

import com.ipserc.arith.combinatoric.CombinationNoReps;
import com.ipserc.arith.complex.Complex;

/**
 * Package-private rank/nullity/triangularization core logic for {@link MatrixComplex} --
 * {@code rank()}'s three independent implementations ({@code rank0()} brute force via minors,
 * {@code rank1()} via double triangularization, {@code rank2()} via singular values of
 * {@code A}{@code .adjoint()}{@code .times(A)}), {@code majorIL()}, {@code nullity()}, and upper/lower
 * triangularization (regular and "perfect", i.e. row-permuted to keep the pivot on the diagonal).
 * <p>
 * Extracted from {@code MatrixComplex.java} (Duodecima/Decimotercera sesion, Etapa 3 sub-fase C de
 * la reestructuracion, ver {@code Claude/ComplexArithRev.md}) -- same pattern as
 * {@code MatrixComplexEquationSystems} (sub-fase A+B), {@code MatrixComplexFormat} (Etapa 1) and
 * {@code MatrixComplexFunctions} (Etapa 2): every method here is {@code static}, takes the
 * {@link MatrixComplex} instance as an explicit parameter, and reads it only through already-public
 * members plus {@code MatrixComplex}'s debug helper ({@code trace(...)}, package-private since
 * Etapa 2, referenced here qualified as {@code MatrixComplex.trace(...)}). {@code partialPivotUp(int)}
 * (used by {@code triangleLo()}) was widened from {@code private} to package-private for this exact
 * purpose -- every other helper this section calls ({@code partialPivot}, {@code swapRows},
 * {@code Ftransf}, {@code locateSwapRowDown}, {@code getRow}, {@code subMatrix}, {@code getItem},
 * {@code setItem}, {@code isNull}, {@code isNullRow}) was already public. {@code MatrixComplex.java}'s
 * own public methods keep their exact signatures, delegating to these in one line each -- the public
 * API is unchanged.
 * <p>
 * {@code rank11()}/{@code rank12()} (helpers of {@code rank1()}) and {@code rowsAreLC(int,int)}
 * (helper of {@code triangleUpPerfect()}/{@code triangleUpPerfect_DEPRECATED()}) stay {@code private}
 * here, called directly as sibling static methods (no public delegator ever existed for them).
 * {@code locateSwapRow(int,int)} is carried over verbatim as dead code (zero callers anywhere in the
 * project, confirmed by grep before this extraction) -- out of scope to remove, same criterion as the
 * {@code ALGEBRAIC BASIS (REMOVED)} block noted in the restructuring plan.
 */
class MatrixComplexRank {

	/**
	 * Calculates the rank of an array. It is not reliable for ill-conditioned matrix due to lack of precision
	 * Kept for testing proposes
	 * @param m The matrix.
	 * @return The rank of the matrix.
	 */
	static int rank0(MatrixComplex m) {
		final boolean DEBUG_ON = false;
		int rank = 0, maxRank = m.rows();
		MatrixComplex tempMatrix = m.copy();
		MatrixComplex incrMatrix;
		CombinationNoReps combinat = new CombinationNoReps();

		if (m.isNull()) return 0;

		if (m.rows() > m.cols()) {
			tempMatrix = m.transpose();
			maxRank = m.cols();
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
					MatrixComplex.trace(incrMatrix, "**************** incrMatrix");
					MatrixComplex.trace("**************** Determinant incrMatrix:" + incrMatrix.determinant());
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
	 * Major Independent Lineal submatrix. Traverse the different minors of the matrix untils the first not dependent linear minor
	 * @param m The matrix.
	 * @return The major independet lineal minor
	 */
	static MatrixComplex majorIL(MatrixComplex m) {
		final boolean DEBUG_ON = false;
		int maxRank = m.rows();
		boolean transposed = false;
		MatrixComplex tempMatrix = m.copy();
		MatrixComplex incrMatrix = new MatrixComplex();
		MatrixComplex majorIL= new MatrixComplex();
		CombinationNoReps combinat = new CombinationNoReps();

		if (m.isNull()) return m;

		if (m.rows() > m.cols()) {
			tempMatrix = m.transpose();
			maxRank = m.cols();
			transposed = true;
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
					MatrixComplex.trace(incrMatrix, "**************** incrMatrix");
					MatrixComplex.trace("**************** Determinant incrMatrix:" + incrMatrix.determinant());
					if (!incrMatrix.determinant().equals(Complex.ZERO, Complex.significative())) {
						rankfound = true;
						majorIL = incrMatrix.copy();
						break;
					}
				}
				if (rankfound) break;
			}
		}
		if (transposed) majorIL = majorIL.transpose();
		return majorIL;
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
	 * @param m The matrix.
	 * @return The rank of the matrix.
	 */
	static int rank1(MatrixComplex m) {
		MatrixComplex matrix = m.copy();
		int rank11 = rank11(matrix);
		int rank12 = rank12(matrix);
		return rank11 < rank12 ? rank11 : rank12;
	}

	private static int rank11(MatrixComplex m) {
		int rank = 0;
		MatrixComplex rankMatrix;
		if (m.isNull()) return 0;

		if (m.cols() < m.rows()) rankMatrix = m.transpose();
		else rankMatrix = m.copy();

		rankMatrix = rankMatrix.triangleLo().hollow();
		rankMatrix = rankMatrix.triangleLo().hollow();
		rankMatrix = rankMatrix.triangleUp().heap();
		rankMatrix = rankMatrix.triangleUp().heap();

		for(int i = 0; i < rankMatrix.rows(); ++i)
			if (!rankMatrix.isNullRow(i)) ++rank;
		return rank;
	}

	private static int rank12(MatrixComplex m) {
		int rank = 0;
		MatrixComplex rankMatrix;
		if (m.isNull()) return 0;

		if (m.cols() > m.rows()) rankMatrix = m.transpose();
		else rankMatrix = m.copy().triangleUp();

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
	 * Kept for testing proposes
	 * @param m The matrix.
	 * @return The rank of the matrix.
	 */
	static int rank2(MatrixComplex m) {
		int rank = 0;
		MatrixComplex ATA = m.adjoint().times(m);
		// Was ATA.charactPoly().solve() (Durand-Kerner only) -- confirmed with a 1200-random-matrix
		// battery that this threw "Arithmetic Overflow (NaN)" 74-100% of the time for 5x5+
		// matrices, matching this method's own long-standing "Fail prone due to lack precision"
		// warning (now removed, no longer true). solveRobust() tries Durand-Kerner first and only
		// falls back to Aberth-Ehrlich if that throws -- verified on the same battery: 0 exceptions,
		// identical rank to MatrixComplex.rank()/rank1() (ground truth via rank0()/brute force) in
		// all 1200 cases, and byte-for-byte identical to plain Durand-Kerner wherever that already
		// succeeded (no silent precision change on the already-working path).
		MatrixComplex roots = ATA.charactPoly().solveRobust();
		for (int row = 0; row < roots.rows(); ++row) {
			if (roots.getItem(row, 0).equals(0,0)) continue;
			if (roots.getItem(row, 0).isZero()) continue;
			++rank;
		}
		return rank;
	}

	/**
	 * Calculates the nullity of a Vectorial Space.
	 * @param m The matrix.
	 * @return The nullity of the Vectorial Space.
	 */
	static int nullity(MatrixComplex m) {
		return  m.cols() - m.rank();
	}

	/**
	 * Private method. Locates the appropriate row to perform the swap in the triangularization methods.
	 * The appropriate row is one whose column to pivot is not zero.
	 * @param m The matrix.
	 * @param row The index of the start row for the search.
	 * @param col The index of the column you want to pivot.
	 * @return The value of the row found or -1 otherwise.
	 */
	private static int locateSwapRow(MatrixComplex m, int row, int col) {
		int i;

		for (i = row; i < m.rows(); ++i)
			if (!m.complexMatrix[i][col].equals(0, 0))
				break;
		return (i == m.rows()) ? -1 : i;
	}

	/**
	 * Checks if the matrix is upper triangular.
	 * @param m The matrix.
	 * @return true if the matrix is upper triangular, false otherwise.
	 */
	static boolean isTriangleUp(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int maxIter = rowLen < colLen ? rowLen : colLen;

		for (int row = 1; row < maxIter; ++row)
			for (int col = 0; col < row; ++col) {
				if (!m.complexMatrix[row][col].equals(0,0)) return false;
			}
		return true;
	}

	/**
	 * Sorts the rows of an array so that those rows whose elements are all zeros occupy the highest positions in the array
	 * @param m The matrix.
	 * @return The array with the null rows at the top
	 */
	static MatrixComplex hollow(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
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
				if (!m.complexMatrix[row][col].equals(Complex.ZERO)) {
					isZero = false;
					break;
				}
			}
			if (isZero) zeroArray.complexMatrix[countZeroArray++] = m.complexMatrix[row].clone();
			else nonZeroArray.complexMatrix[countNonZeroArray++] = m.complexMatrix[row].clone();
		}
		for(int row = 0; row < countZeroArray; ++row) hollow.complexMatrix[row] = zeroArray.complexMatrix[row].clone();
		for(int row = 0; row < countNonZeroArray; ++row) hollow.complexMatrix[row+countZeroArray] = nonZeroArray.complexMatrix[row].clone();

		return hollow;
	}

	/**
	 * Sorts the rows of an array so that those rows whose elements are all zeros occupy the lowest positions in the array
	 * @param m The matrix.
	 * @return The array with the null rows at the end
	 */
	static MatrixComplex heap(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int countZeroArray = 0;
		int countNonZeroArray = 0;
		boolean isZero;
		MatrixComplex zeroArray = new MatrixComplex(rowLen, colLen);
		MatrixComplex nonZeroArray = new MatrixComplex(rowLen, colLen);
		MatrixComplex heap = new MatrixComplex(rowLen, colLen);
		for(int row = 0; row < rowLen; ++row) {
			isZero = true;
			for(int col = 0; col < colLen; ++col) {
				if (!m.complexMatrix[row][col].equals(Complex.ZERO)) {
					isZero = false ;
					break;
				}
			}
			if (isZero) zeroArray.complexMatrix[countZeroArray++] = m.complexMatrix[row].clone();
			else nonZeroArray.complexMatrix[countNonZeroArray++] = m.complexMatrix[row].clone();
		}
		for(int row = 0; row < countNonZeroArray; ++row) heap.complexMatrix[row] = nonZeroArray.complexMatrix[row].clone();
		for(int row = 0; row < countZeroArray; ++row) heap.complexMatrix[row+countNonZeroArray] = zeroArray.complexMatrix[row].clone();

		return heap;
	}

	/**
	 * Checks whether two rows are linear combination or not
	 * @param m The matrix.
	 * @param idrow1 The 1st row
	 * @param idrow2 The 2nd row
	 * @return True if the two rows are linear combination, otherwise false
	 */
	private static boolean rowsAreLC(MatrixComplex m, int idrow1, int idrow2) {
		Complex cCoef;
		MatrixComplex row1 = m.getRow(idrow1).copy();
		MatrixComplex row2 = m.getRow(idrow2).copy();
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
	 * @param m The matrix.
	 * @return The upper triangularized matrix.
	 */
	static MatrixComplex triangleUp(MatrixComplex m) {
		final boolean DEBUG_ON = false;
		final String METH_NAME = "triangleUp";

		int rowLen = m.rows();
		Complex cCoef = new Complex();
		MatrixComplex auxMatrix = m.clone();

		/* -------------   DEBUGGING BLOCK   ------------- * /
		trace(auxMatrix, METH_NAME + ": auxMatrix:");
		/* ------------- END DEBUGGING BLOCK ------------- */

		if (m.isTriangleUp()) return auxMatrix;

		for (int k = 0; k < rowLen-1; ++k) {
			// Proactive partial pivoting: always swap to the row with the maximum modulus in this
			// column (not only when the current pivot is exactly zero), and pick that maximum
			// (partialPivot) rather than just the first nonzero row (locateSwapRowUp) -- a pivot
			// that is merely small still amplifies rounding error.
			int rowSwap = auxMatrix.partialPivot(k);
			if (rowSwap != -1 && rowSwap != k) auxMatrix.swapRows(k, rowSwap);
			for (int row = k+1; row < rowLen; ++row) {
				/* -------------   DEBUGGING BLOCK   ------------- * /
				trace(METH_NAME + ": auxMatrix.getItem(row, k) =" + auxMatrix.getItem(row, k).toString());
				trace(METH_NAME + ": auxMatrix.getItem(k,k) = " + auxMatrix.getItem(k,k).toString());
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
	 * @param m The matrix.
	 * @return The perfect upper triangularized array
	 */
	static MatrixComplex triangleUpPerfect(MatrixComplex m) {
		boolean DEBUG_ON = false;
		String METH_NAME = "triangleUpPerfect()";

		MatrixComplex triUpMatrix = m.triangleUp().heap();
		/* ----------  START DEBUGGING BLOCK   ----------- * /
		trace(triUpMatrix, METH_NAME + ": triUpMatrix Start");
		trace(METH_NAME + ": triUpMatrix: " + triUpMatrix.toMatrixComplex());
		/* ------------- END DEBUGGING BLOCK ------------- */

		// Clean up linear combinations rows
		int rowLen = triUpMatrix.rows();
		for (int row1 = 0; row1 < rowLen -1; ++row1)
			for (int row2 = row1+1; row2 < rowLen -1; ++row2) {
				if (rowsAreLC(triUpMatrix, row1, row2)) {
					for (int col2 = 0; col2 < m.cols(); ++col2)
						triUpMatrix.setItem(row2, col2, Complex.ZERO);
				}
			}

		int[][] matIndex = new int[m.rows()][2];
		for (int row = 0; row < m.rows(); ++row) {
			matIndex[row][0] = -1;
			matIndex[row][1] = -1;
		}

		// Ubicar las filas correctas
		for (int row = 0; row < m.rows(); ++row) {
			if (triUpMatrix.isNullRow(row)) continue;
			for (int col = 0; col < m.cols(); ++col) {
				if (!triUpMatrix.getItem(row, col).isZero()) {
					matIndex[row][0] = row;
					matIndex[row][1] = col;
					break;
				}
			}
		}

		//Ubicar las restantes filas
		for (int row = m.rows()-1; row > -1 ; --row) {
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

		//Comprobar que todas las filas estÃ¡n ubicadas
		for (int row = 0; row < m.rows(); ++row) {
			if (matIndex[row][0] == -1) {
				System.err.println("Location failure error.");
				return triUpMatrix;
			}
		}

		for (int row = m.rows()-1; row > -1; --row) {
			triUpMatrix.swapRows(matIndex[row][0], matIndex[row][1]);
		}

		/* ----------  START DEBUGGING BLOCK   ----------- * /
		trace(triUpMatrix, METH_NAME + ": triUpMatrix End");
		trace(METH_NAME + ": triUpMatrix: " + triUpMatrix.toMatrixComplex());
		/* ------------- END DEBUGGING BLOCK ------------- */

		return triUpMatrix;

	}

	static MatrixComplex triangleUpPerfect_DEPRECATED(MatrixComplex m) {
		boolean DEBUG_ON = false;
		String METH_NAME = "triangleUpPerfect()";

		MatrixComplex triUpMatrix = m.triangleUp().heap();
		/* ----------  START DEBUGGING BLOCK   ----------- * /
		trace(triUpMatrix, METH_NAME + ": triUpMatrix");
		trace(METH_NAME + ": triUpMatrix: " + triUpMatrix.toMatrixComplex());
		/* ------------- END DEBUGGING BLOCK ------------- */

		// Clean up linear combinations rows
		int rowLen = triUpMatrix.rows();
		for (int row1 = 0; row1 < rowLen -1; ++row1)
			for (int row2 = row1+1; row2 < rowLen -1; ++row2) {
				if (rowsAreLC(triUpMatrix, row1, row2)) {
					for (int col2 = 0; col2 < m.cols(); ++col2)
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
	 * @param m The matrix.
	 * @return true if the matrix is lower triangular, false otherwise.
	 */
	static boolean isTriangleLo(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int maxIter = rowLen < colLen ? rowLen : colLen;

		for (int row = 0; row < rowLen-1; ++row)
			for (int col = row+1; col < maxIter; ++col) {
				if (!m.complexMatrix[row][col].equals(0,0)) return false;
			}
		return true;
	}

	/**
	 * Calculates the lower triangularization of the matrix.
	 * @param m The matrix.
	 * @return The lower triangularized matrix.
	 */
	static MatrixComplex triangleLo(MatrixComplex m){
		int rowLen = m.rows();
		int colLen = m.cols();
		Complex cCoef = new Complex();
		MatrixComplex auxMatrix = m.clone();

		if (m.isTriangleLo()) return auxMatrix;

		//Prepare Matrix
		int upLimit = rowLen < colLen ? colLen : rowLen;
		int loLimit = rowLen > colLen ? colLen : rowLen;
		for (int k = upLimit-1; k >= 0 ; --k) {
			// Proactive partial pivoting: always swap to the row with the maximum modulus in this
			// column (not only when the current pivot is exactly zero), and pick that maximum
			// (partialPivotUp) rather than just the first nonzero row (locateSwapRowDown) -- a pivot
			// that is merely small still amplifies rounding error.
			if (k < rowLen && k < colLen) {
				int rowSwap = auxMatrix.partialPivotUp(k);
				if (rowSwap != -1 && rowSwap != k) auxMatrix.swapRows(k, rowSwap);
			}

			for (int row = k-1; row >= 0; --row) {
				if (k >= loLimit || auxMatrix.getItem(k,k).equals(Complex.ZERO)) continue;
				cCoef = auxMatrix.getItem(row,k).divides(auxMatrix.getItem(k,k)).opposite();
				auxMatrix.Ftransf(row, k, cCoef);
			}
		}
		return auxMatrix;
	}

	static MatrixComplex triangleLo1(MatrixComplex m) {

		int rowLen = m.rows();
		Complex cCoef = new Complex();
		MatrixComplex auxMatrix = m.clone();

		if (m.isTriangleLo()) return auxMatrix;

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
	 * @param m The matrix.
	 * @return true for square matrix, false otherwise
	 */
	static boolean isSquare(MatrixComplex m) {
		return (m.rows() == m.cols());
	}
}
