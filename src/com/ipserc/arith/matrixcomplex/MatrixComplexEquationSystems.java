package com.ipserc.arith.matrixcomplex;

import com.ipserc.arith.complex.Complex;

/**
 * Package-private equation-system core logic for {@link MatrixComplex} -- classification
 * (homogeneous/type), resolution (Gauss reduction, Cramer's rule), row/col accessors and
 * submatrix extraction.
 * <p>
 * Extracted from {@code MatrixComplex.java} (Duodecima sesion, Etapa 3 sub-fase A+B de la
 * reestructuracion, ver {@code Claude/ComplexArithRev.md}) -- same pattern as
 * {@code MatrixComplexFormat} (Etapa 1) and {@code MatrixComplexFunctions} (Etapa 2): every method
 * here is {@code static}, takes the {@link MatrixComplex} instance as an explicit parameter, and
 * reads it only through already-public members plus {@code MatrixComplex}'s debug helper
 * ({@code trace(...)}, already package-private since Etapa 2) and {@code HEADINFO} plus
 * {@code intSplit(String,String)} and {@code copyCol(MatrixComplex,int)} (both widened from
 * {@code private} to package-private for this exact purpose). {@code MatrixComplex.java}'s own
 * public methods keep their exact signatures, delegating to these in one line each -- the public
 * API is unchanged, including the {@code INCONSISTENT}/{@code INDETERMINATE}/{@code DETERMINATE}
 * constants, which stay declared on {@code MatrixComplex} itself (referenced here fully qualified)
 * since they are part of its public API surface. {@code Syseq.java} depends on this core
 * (confirmed to only call public methods -- {@code typeEqSys}, {@code solveGauss},
 * {@code isHomogeneous}, {@code completeEqSys}, {@code solve}, {@code coefMatrix},
 * {@code indMatrix}, {@code constMatrix} -- extraction is transparent to it).
 */
class MatrixComplexEquationSystems {

	/**
	 * Returns a new matrix with the coefficients of the object matrix.
	 * The new matrix is the original one with the independent terms removed.
	 * @param m The matrix.
	 * @return The new matrix with the coefficients copied without independent terms.
	 */
	static MatrixComplex coefMatrix(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		MatrixComplex coefMatrix = new MatrixComplex(rowLen, colLen-1);
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen-1; ++col)
				coefMatrix.complexMatrix[row][col] = m.complexMatrix[row][col].copy();
		return coefMatrix;
	}

	/**
	 * Returns a new matrix with the independent terms of the object matrix.
	 * The new matrix is the independent terms column matrix.
	 * @param m The matrix.
	 * @return The new matrix with the independent terms.
	 */
	static MatrixComplex indMatrix(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		MatrixComplex indMatrix = new MatrixComplex(rowLen, 1);
		for (int row = 0; row < rowLen; ++row)
			indMatrix.complexMatrix[row][0] = m.complexMatrix[row][colLen-1].copy();
		return indMatrix;
	}

	/**
	 * Returns a new matrix with the independent terms of the object matrix.
	 * The new matrix is the independent terms column matrix.
	 * @param m The matrix.
	 * @return The new matrix with the independent terms.
	 */
	static MatrixComplex constMatrix(MatrixComplex m) {
		return indMatrix(m);
	}

	/**
	 * Identifies if the system of equations is isHomogeneous.
	 * @param m The matrix.
	 * @return Returns true if the system is isHomogeneous, false otherwise.
	 */
	static boolean isHomogeneous(MatrixComplex m) {
		int rowLen = m.rows();
		int lastCol = m.cols()-1;
		boolean isHomogeneous = true;

		for (int row = 0; row < rowLen; ++row)
			isHomogeneous &= m.complexMatrix[row][lastCol].equals(0, 0);
		return isHomogeneous;
	}

	/**
	 * Returns the homogeneous equation system of m
	 * @param m The matrix.
	 * @return the homogeneous equation system of m
	 */
	static MatrixComplex homogeneous(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		MatrixComplex homogeneous = new MatrixComplex(rowLen, colLen);

		homogeneous = m.copy();

		for (int row = 0; row < rowLen; ++row)
			homogeneous.setItem(row, colLen-1, Complex.ZERO);
		return homogeneous;
	}

	/**
	 * Identifies the type of systems of equations returning the constant according to the definition.
	 * @param m The matrix.
	 * @return INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 */
	static int typeEqSys(MatrixComplex m) {
		int numUnk = m.cols()-1;
		int coefRank, augmRank;

		MatrixComplex coefMatrix = coefMatrix(m);

		augmRank = m.rank();
		coefRank = coefMatrix.rank();

		MatrixComplex.trace(coefMatrix, "------------------ typeEqSys() coefMatrix");
		MatrixComplex.trace("------------------ typeEqSys() augmRank:" + augmRank);
		MatrixComplex.trace("------------------ typeEqSys() coefRank:" + coefRank);

		if (augmRank != coefRank) return MatrixComplex.INCONSISTENT;

		// Full column rank always means exactly one solution, regardless of homogeneity: a
		// homogeneous full-rank system has the unique trivial solution (x=0), it is not
		// underdetermined -- there is no free parameter to speak of.
		if (coefRank == numUnk) return MatrixComplex.DETERMINATE;
		else return MatrixComplex.INDETERMINATE;
	}

	/**
	 * Returns a string indicating the system type of equations according to type "type".
	 * @param type INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 * @param lambda Parameter value to calculate solutions for indeterminate systems.
	 */
	static String strTypeEqSys(int type, Complex lambda) {
		String text = "";
		switch (type) {
			case MatrixComplex.INCONSISTENT: text = "The system is INCONSISTENT";
				break;
			case MatrixComplex.INDETERMINATE: text = "The system is INDETERMINATE. Solutions calculated for param = " + lambda.toString();
				break;
			case MatrixComplex.DETERMINATE: text = "The system is DETERMINATE";
				break;
		}
		return text;
	}

	/**
	 * Prints a message indicating the system type of equations according to type "type".
	 * @param type INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 * @param lambda Parameter value to calculate solutions for indeterminate systems.
	 */
	static void printTypeEqSys(int type, Complex lambda) {
		System.out.println(strTypeEqSys(type, lambda) );
	}

	/**
	 * Prints a message indicating the system type of equations according to type "type". lambda is 1 by default
	 * @param m The matrix.
	 */
	static void printTypeEqSys(MatrixComplex m) {
		int type = typeEqSys(m);
		Complex lambda = new Complex(1,0);
		printTypeEqSys(type, lambda);
	}

	/**
	 * If the number of equations is less than of unknowns, the missing equations are introduced with the coefficients and the independent term set to zero.
	 * @param m The matrix.
	 * @return the system of equations completed with equations that are missing to zero.
	 */
	static MatrixComplex completeEqSys(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		MatrixComplex eqsMatrix = new MatrixComplex(colLen-1, colLen);
		Complex[] zeroRowComplex = new Complex[colLen];
		for (int col = 0; col < colLen; ++col) zeroRowComplex[col] = new Complex();

		for (int row = 0; row < colLen-1; ++row) {
			if (row < rowLen)
				eqsMatrix.complexMatrix[row] = m.complexMatrix[row].clone();
			else
				eqsMatrix.complexMatrix[row] = zeroRowComplex.clone();
		}
		return eqsMatrix;
	}

	/**
	 * Finds the solutions of an equation systems by the default rule (Gauss reduction)
	 * @param m The matrix.
	 * @return The solutions of the equation systems
	 */
	static MatrixComplex solve(MatrixComplex m) {
			MatrixComplex newThis = m.clone();
			return newThis.solveGauss(Complex.ONE);
	}

	/**
	 * Shortcut to solveGauss
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param m The matrix.
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist.
	 */
	static MatrixComplex solve(MatrixComplex m, Complex lambda) {
		MatrixComplex newThis = m.clone();
		return newThis.solveGauss(lambda);
	}

	/**
	 * Equation evaluator
	 * Evaluates an equation replacing its unknowns with values
	 * @param m The matrix.
	 * @param row
	 * @param col
	 * @param point
	 * @return
	 */
	static Complex eqEval(MatrixComplex m, int row, int col, MatrixComplex point) {
		int colLen = m.cols();
		Complex eqEval = new Complex();
		Complex divisor = new Complex();
		int ncol;

		eqEval = m.complexMatrix[row][colLen-1];
		for (ncol = 0; ncol < colLen -1; ++ncol) {
			if (ncol == col) divisor =  m.complexMatrix[row][ncol];
			else eqEval = eqEval.minus(m.complexMatrix[row][ncol].times(point.complexMatrix[0][ncol]));
		}
		return eqEval.divides(divisor);
	}

	/**
	 * Checks if the all the items (except colIdx) of rowIdx of the Matrix are null or not
	 * Dead code: confirmed no callers anywhere in the project (pre-existing, out of scope --
	 * see the "Fuera de alcance" note in the saved restructuring plan). Moved verbatim.
	 * @param m The matrix.
	 * @param rowIdx The row to inspect
	 * @param colIdx The col to skip
	 * @return True if all are null
	 */
	private static boolean isNullSolution(MatrixComplex m, int rowIdx, int colIdx) {
		for(int col = 0; col < m.cols(); ++col) {
			if (col == colIdx) continue;
			if (!m.getItem(rowIdx,col).equals(0,0)) return false;
		}
		return true;
	}

	/**
	 * Removes the null rows of a Matrix
	 * @param m The matrix.
	 * @return A new Matrix without the null rows
	 */
	static MatrixComplex removeNullRows(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		MatrixComplex newMatrix = new MatrixComplex(rowLen, colLen);
		int ndRow = 0;
		for(int row = 0; row < rowLen; ++row) {
			if (!m.isNullRow(row)) newMatrix.complexMatrix[ndRow++] = m.complexMatrix[row].clone();
		}
		MatrixComplex rdMatrix = new MatrixComplex(ndRow, colLen);
		for(int row = 0; row < ndRow; ++row) {
			rdMatrix.complexMatrix[row] = newMatrix.complexMatrix[row].clone();
		}
		return rdMatrix;
	}

	/**
	 * Gets a valid coefficient to normalize the linear equation
	 * @param m The matrix.
	 * @param row1 The row to normalize
	 * @param row2 The row to get the coefficient to use to do the normalization
	 * @return The coefficient found
	 */
	private static Complex getCoef(MatrixComplex m, int row1, int row2) {
		Complex coef = new Complex(0);

		for (int col = 0; col < m.cols(); ++col) {
			if(!m.getItem(row2, col).equals(Complex.ZERO)) {
				coef = m.getItem(row1, col).divides(m.getItem(row2, col));
				break;
			}
		}
		return coef;
	}

	/**
	 * Removes the duplicated rows of a Matrix
	 * @param m The matrix.
	 * @return A new Matrix without the duplicated rows
	 */
	static MatrixComplex removeDuplicateRows(MatrixComplex m) {
		boolean isDuplicate = true;
		int rowLen = m.rows();
		int colLen = m.cols();
		MatrixComplex newMatrix = new MatrixComplex(rowLen, colLen);
		int ndRow = 0;
		newMatrix.complexMatrix[ndRow++] = m.complexMatrix[0].clone();
		for(int row = 1; row < rowLen; ++row) {
			for(int rrow = 0; rrow < row; ++rrow) {
				isDuplicate = true;
				Complex coef = getCoef(m, rrow, row);
				for(int col = 0; col < colLen; ++col) {
					if (coef.equals(Complex.ZERO)) {
						isDuplicate = false;
						break;
					}
					if (!m.getItem(row,col).equals(m.getItem(rrow,col).divides(coef))) {
						isDuplicate = false;
						break;
					}
				}
				if (isDuplicate) {
					MatrixComplex.trace("removeDuplicateRows: duplicate row:" + row + " - rrow:" + rrow);
					break;
				}
			}
			if (!isDuplicate) newMatrix.complexMatrix[ndRow++] = m.complexMatrix[row].clone();
		}
		MatrixComplex rdMatrix = new MatrixComplex(ndRow, colLen);
		for(int row = 0; row < ndRow; ++row) {
			rdMatrix.complexMatrix[row] = newMatrix.complexMatrix[row].clone();
		}
		return rdMatrix;
	}

	/**
	 * Returns the minimum number of LI solutions that the equation system has
	 * @param m The matrix.
	 * @return The minimum number of LI solutions
	 */
	static int nbrOfSolutions(MatrixComplex m) {
		// Same rectangular-system fix as solveGauss(): complete rather than bail out to 0
		// when there are fewer equations than unknowns.
		if (m.rows()+1 < m.cols()) return m.completeEqSys().nbrOfSolutions();
		if (m.rows()+1 > m.cols()) return 0;
		int rank = m.rank();
		int dim = m.cols();
		int nbrUkn = dim-1;
		int nbrSolutions = nbrUkn-rank;
		nbrSolutions = nbrSolutions == 0 ? 1 : nbrSolutions;
		return nbrSolutions;
	}

	/**
	 * Calculates the minimum number of LI solutions of an equation system and writes it on the console
	 * @param m The matrix.
	 * @return The minimum number of LI solutions
	 */
	static int nbrOfSolutionsText(MatrixComplex m) {
		int numSols;
		switch (m.typeEqSys()) {
			case MatrixComplex.DETERMINATE:
				System.out.println("1 single solution is returned.");
				numSols = 1;
				break;
			case MatrixComplex.INDETERMINATE:
					numSols = m.nbrOfSolutions();
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
	 * @param m The matrix (the equation system).
	 * @param solution The solution to check as a one row array
	 * @return True If the solution is correct
	 */
	private static boolean checkSingleSol(MatrixComplex m, MatrixComplex solution) {
		MatrixComplex indTerm = m.indMatrix().transpose();
		MatrixComplex uknMatix = m.unkMatrix().transpose();
		MatrixComplex unitMatrix = new MatrixComplex(solution.rows(),1);
		unitMatrix.initMatrix(1, 0);

		if (solution.times(uknMatix).minus(unitMatrix.times(indTerm)).isNull()) return true;
		else return false;
	}

	/**
	 * Gets the row (rowIdx) of a Matrix
	 * @param m The matrix.
	 * @param rowIdx The row index to retrieve. 1st row is 0, and so on.
	 * @return The row selected
	 */
	static MatrixComplex getRow(MatrixComplex m, int rowIdx) {
		MatrixComplex rowMatrix = new MatrixComplex(1,m.cols());
		rowMatrix.complexMatrix[0] = m.complexMatrix[rowIdx].clone();
		return rowMatrix;
	}

	/**
	 * Gets the col (colIdx) of a Matrix
	 * @param m The matrix.
	 * @param colIdx The col index to retrieve. 1st col is 0, and so on.
	 * @return The col selected
	 */
	static MatrixComplex getCol(MatrixComplex m, int colIdx) {
		MatrixComplex colMatrix = new MatrixComplex(m.rows(),1);
		for (int row = 0; row < m.rows(); ++row) {
			colMatrix.setItem(row, 0, m.getItem(row,colIdx));
		}
		return colMatrix;
	}

	/**
	 * Sets the row (rowIdx) of m with the values of rowMatrix
	 * @param m The matrix.
	 * @param rowIdx The row index to retrieve. 1st row is 0, and so on.
	 * @param rowMatrix
	 */
	static void setRow(MatrixComplex m, int rowIdx, MatrixComplex rowMatrix) {
		m.complexMatrix[rowIdx] = rowMatrix.complexMatrix[0].clone();
	}

	/**
	 * Sets the column colIdx of the matrix to certain complex value
	 * @param m The matrix.
	 * @param colIdx The Id of the col to set
	 * @param cValue The value to set
	 */
	static void setCol(MatrixComplex m, int colIdx, Complex cValue) {
		for (int row = 0; row < m.rows(); ++row)
			m.complexMatrix[row][colIdx] = cValue;
	}

	/**
	 * Sets colum colMatrix into m at colIdx column
	 * @param m The matrix.
	 * @param colIdx
	 * @param colMatrix
	 */
	static void setCol(MatrixComplex m, int colIdx, MatrixComplex colMatrix) {
		for (int row = 0; row < m.rows(); ++row)
			m.setItem(row, colIdx, colMatrix.getItem(row, 0));
	}

	/**
	 * Sets the row rowIdx of the matrix to certain complex value
	 * @param m The matrix.
	 * @param rowIdx The Id of the col to set
	 * @param cValue The value to set
	 */
	static void setRow(MatrixComplex m, int rowIdx, Complex cValue) {
		for (int col = 0; col < m.cols(); ++col)
			m.complexMatrix[rowIdx][col] = cValue;
	}

	/**
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param m The matrix.
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist, otherwise null.
	 */
	static MatrixComplex solveGauss(MatrixComplex m, Complex lambda) {
		final boolean DEBUG_ON = false;
		final String METH_NAME = "solveGauss";

		// A genuinely rectangular system (fewer equations than unknowns) used to fall through
		// to nbrOfSolutions() returning 0 further down, silently producing an empty solution
		// matrix. Complete it first with zero rows, same workaround Syseq.solveq() already
		// uses (this.completeEqSys().solve(lambda)). Only for fewer-than-needed rows: an
		// over-determined system (more equations than unknowns) is NOT completed here, since
		// completeEqSys() would silently truncate/drop the extra equations instead of padding.
		if (m.rows()+1 < m.cols()) {
			return m.completeEqSys().solveGauss(lambda);
		}

		int rowLen = m.rows();
		int colLen = m.cols();

		/* ----------  START DEBUGGING BLOCK   ----------- */
		MatrixComplex.trace(METH_NAME + ": " + "= = = = = = = = Original Matrix");
		/* ------------- END DEBUGGING BLOCK ------------- */

		if (rowLen+1 > colLen) {
			// This trace()-only guard used to be a no-op in normal use (trace() is gated
			// behind __DEBUG__, false by default) -- an over-determined system silently fell
			// through into typeEqSys()/coefMatrix()/etc. with mismatched dimensions, ending up
			// with garbage ([NaN+NaNi]) further down instead of a diagnosable error.
			throw new IllegalArgumentException(MatrixComplex.HEADINFO + METH_NAME + ": Not valid matrix: The matrix doesn't represent an equation system (more equations than unknowns): " + rowLen + " rows, " + colLen + " cols.");
		}

		MatrixComplex auxMatrix, coefMatrix, indMatrix;
		MatrixComplex solMatrix = new MatrixComplex(1, colLen-1);

		int typeEqSys = typeEqSys(m);

		/* ----------  START DEBUGGING BLOCK   ----------- */
		if (DEBUG_ON) {
			printTypeEqSys(typeEqSys, lambda);
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		// *************** INCONSISTENT ***************
		if (typeEqSys == MatrixComplex.INCONSISTENT) {
			// Used to return a NaN/Infinity marker in silence (solMatrix.divides(0).transpose()).
			// That was a deliberate SCOPE DECISION for a while (Octava sesion, Hallazgo 3):
			// Syseq.solveq() used to call solve() UNCONDITIONALLY, before checking typeEqSys(),
			// so it genuinely depended on getting a MatrixComplex back even for an inconsistent
			// system. Syseq.java was fixed afterward (Syseq.VERSION 1.6) to check typeEqSys()
			// FIRST and skip calling solve() entirely for INCONSISTENT -- confirmed with grep
			// that every other real caller of solve()/solveGauss() (MatrixComplex.kernel(),
			// Jordan.java, geom/Plane.java's plane-intersection, Polynom.java's Vandermonde
			// interpolation) either solves a homogeneous system (always consistent by
			// construction) or is geometrically/algebraically guaranteed consistent whenever it's
			// actually reached -- none of them depend on this marker either. Throwing is now safe.
			throw new IllegalArgumentException(MatrixComplex.HEADINFO + METH_NAME + ": This system is INCONSISTENT, there is no solution.");
		}

		// *************** DETERMINATE ***************
		if (typeEqSys == MatrixComplex.DETERMINATE) {
			MatrixComplex.trace(METH_NAME + ": " + "This system is DETERMINATE. This is the unique solution!!!!!!!!!!");
			coefMatrix = coefMatrix(m);
			indMatrix = indMatrix(m);
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
		auxMatrix = m.triangleUpPerfect();
		/* ----------  START DEBUGGING BLOCK   ----------- */
		MatrixComplex.trace(m, METH_NAME + ": " + "= = = = = = = = Original Matrix");
		MatrixComplex.trace(auxMatrix, METH_NAME + ": " + "+ + + + + + auxMatrix Triangle Up Perfect");
		/* ------------- END DEBUGGING BLOCK ------------- */

		indMatrix = auxMatrix.indMatrix();
		int nbrOfSols = auxMatrix.nbrOfSolutions();
		solMatrix = new MatrixComplex(nbrOfSols, rowLen);
		// solBase: base para el cÃ¡lculo de las soluciones
		// solBase: base for calculating the solutions
		MatrixComplex solBase = new MatrixComplex(nbrOfSols, rowLen);
		Complex coefSol;

		// Se construye la base para las soluciones recorriendo la matriz triangular perfecta
		// y rellenando cada fila de la base con lambda en la posiciÃ³n en que la diagonal principal
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

		// Ahora con la soluciÃ³n base se calculan las soluciones del sistema de ecuaciones
		// mediante sustituciÃ³n inversa sobre la matriz tiangular perfecta en aquellas
		// filas cuyos elemntos de la diagonal principal no son nulos
		//
		// Now with the base solution the solutions of the system of equations are calculated
		// by inverse substitution on the perfect tiangutar matrix in those
		// rows whose main diagonal elements are not null
		for (int solNbr = 0; solNbr < nbrOfSols; ++solNbr) {
			for (int row = rowLen-1; row >= 0; --row) {
				if (!auxMatrix.getItem(row ,row).equals(Complex.ZERO)) {
					// Se calcula el coeficiente del nuevo tÃ©rmino independiente con los valores de las incÃ³gnitas ya calculadas
					// Se obtiene el tÃ©rmino independiente de la ecuaciÃ³n original
					//
					// The coefficient of the new independent term is calculated with the values of the unknowns already calculated
					// The independent term of the original equation is obtained
					coefSol = auxMatrix.getItem(row, colLen-1);
					// Para cada incÃ³gnita calculada...
					//
					// For each calculated unknown ...
					for (int col = colLen-2; col > row; --col) {
						// Se despeja y actualiza el nuevo tÃ©rmino independiente
						//
						// The new independent term is cleared and updated
						coefSol = coefSol.minus(auxMatrix.getItem(row, col).times(solMatrix.getItem(solNbr, col)));
					}
					// Si el coeficiente de la incÃ³gnita a calcular es cero entonces...
					//
					// If the coefficient of the unknown to be calculated is zero then ...
					if (auxMatrix.getItem(row, row).equals(Complex.ZERO))
						// ...el nuevo tÃ©rmino independiente se fija a cero
						//
						// ... the new independent term is set to zero
						coefSol = Complex.ZERO;
					else
						// Sino, el nuevo tÃ©rmino independiente se divide por el coeficiente de la incÃ³gnita
						//
						// Otherwise, the new independent term is divided by the coefficient of the unknown
						coefSol = coefSol.divides(auxMatrix.getItem(row, row));
				}
				else {
					coefSol = solBase.getItem(solNbr, row);
				}

				// Se actualiza la matriz de soluciones con el valor calculado para la soluciÃ³n nÃºmero solNbr y la columan correspondiente que coincide con el nÃºmero de fila o ecuaciÃ³n tratada
				//
				// The solution matrix is updated with the value calculated for the solNbr number solution and the corresponding column that matches the row number or treated equation
				solMatrix.setItem(solNbr, row, coefSol);
			}
		}

		/* ----------  START DEBUGGING BLOCK   ----------- */
		MatrixComplex.trace(solMatrix, METH_NAME + ": " + "solMatrix");
		/* ------------- END DEBUGGING BLOCK ------------- */

		/* Check sols for INDETERMINATE Systems */
		MatrixComplex nullRow = new MatrixComplex(1, rowLen); // By default initialized to ZERO nullRow.initMatrix(0, 0);
		for (int solIdx = 0; solIdx < solMatrix.rows(); ++solIdx) {
			if (checkSingleSol(m, solMatrix.getRow(solIdx))) continue;
			else {
				/* ----------  START DEBUGGING BLOCK   ----------- */
				MatrixComplex.trace(solMatrix.getRow(solIdx), METH_NAME + ": " + "Solution FAILED!!! ");
				/* ------------- END DEBUGGING BLOCK ------------- */
				solMatrix.complexMatrix[solIdx] = nullRow.complexMatrix[0].clone();
			}
		}

		return solMatrix;
	}

	/**
	 * finds the solutions to a equation systems using the Cramer's rule
	 * @param m The matrix.
	 * @return The column matrix with the solutions if they exist.
	 */
	static MatrixComplex solveCramer(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		Complex cCoef = new Complex();
		int row;

		if (rowLen+1 != colLen) {
			throw new IllegalArgumentException(MatrixComplex.HEADINFO + "solveCramer: Not valid matrix: The matrix doesn't represent an equation system: " + rowLen + " rows, " + colLen + " cols.");
		}

		MatrixComplex coefMatrix = new MatrixComplex(rowLen, rowLen);
		MatrixComplex auxMatrix = new MatrixComplex(rowLen, rowLen);
		MatrixComplex solMatrix = new MatrixComplex(rowLen, 1);

		if (typeEqSys(m) != MatrixComplex.DETERMINATE) {
			MatrixComplex.trace("solveCramer ERROR: " + "The system is not determined, so there is no Cramer solution.");
			return solMatrix.transpose().divides(0);
		}
		//coefMatrix.copyCol(m, colLen);
		MatrixComplex.trace("SOLVED by KRAMER's method ");
		coefMatrix = m.unkMatrix();
		cCoef = coefMatrix.determinant();
		for (row = 0; row < rowLen; ++row) {
			auxMatrix.copyCol(m, row);
			solMatrix.complexMatrix[row][0] = (auxMatrix.determinant()).divides(cCoef);
		}

		return solMatrix.transpose();
	}

	/**
	 * rowReduce performs the Gauss-Jordan elimination, adding multiples of rows together so as to produce zero elements when possible. The final matrix is in reduced row echelon form.
	 * @param m The matrix.
	 * @return The final matrix in its reduced row echelon form.
	 */
	static MatrixComplex rowReduce(MatrixComplex m) {
		MatrixComplex rowRed = m.triangleUpPerfect();
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
	 * @param m The matrix.
	 * @param row The index of the initial row.
	 * @param col The index of the initial column.
	 * @param order Number of rows/columns taken.
	 * @return The new sub-matrix.
	 */
	static MatrixComplex subMatrixAug(MatrixComplex m, int row, int col, int order) {
		MatrixComplex resultMatrix = new MatrixComplex(order, order+1);
		int rowLen = m.rows();
		int colLen = m.cols();
		int rowEnd = row + order;
		int colEnd = col + order + 1;

		if (rowEnd > rowLen) {
			throw new IllegalArgumentException("Not valid submatrix: The row length surpass the matrix length.");
		}
		if (colEnd > colLen) {
			throw new IllegalArgumentException("Not valid submatrix: The col length surpass the matrix length.");
		}

		for (int i = row, rrow = 0; i < rowEnd; ++i, ++rrow) {
			for (int j = col, ccol = 0; j < colEnd; ++j, ++ccol) {
				resultMatrix.complexMatrix[rrow][ccol] = m.complexMatrix[i][j].copy();
			}
		}
		return resultMatrix;
	}

	/**
	 * Returns the sub-matrix from row, col of order "order" (number of rows/columns taken)
	 * @param m The matrix.
	 * @param row The index of the initial row.
	 * @param col The index of the initial column.
	 * @param order Number of rows/columns taken.
	 * @return The new sub-matrix.
	 */
	static MatrixComplex subMatrix(MatrixComplex m, int row, int col, int order) {
		MatrixComplex resultMatrix = new MatrixComplex(order, order);
		int rowLen = m.rows();
		int colLen = m.cols();
		int rowEnd = row + order;
		int colEnd = col + order;

		if (rowEnd > rowLen) {
			throw new IllegalArgumentException("Not valid submatrix: The row length surpass the matrix length.");
		}
		if (colEnd > colLen) {
			throw new IllegalArgumentException("Not valid submatrix: The col length surpass the matrix length.");
		}

		for (int i = row, rrow = 0; i < rowEnd; ++i, ++rrow) {
			for (int j = col, ccol = 0; j < colEnd; ++j, ++ccol) {
				resultMatrix.complexMatrix[rrow][ccol] = m.complexMatrix[i][j].copy();
			}
		}
		return resultMatrix;
	}

	/**
	 * Returns the sub-matrix formed with rows an columns indicated in the parameters "rows" and "columns".
	 * @param m The matrix.
	 * @param rows A list of integers of the rows to be taken.
	 * @param cols A list of integers of the columns to be taken.
	 * @return The new sub-matrix.
	 */
	static MatrixComplex subMatrix(MatrixComplex m, int[] rows, int[] cols) {
		MatrixComplex subMatrix = new MatrixComplex(rows.length, cols.length);

		for (int row = 0; row < rows.length; ++row)
			for (int col = 0; col < cols.length; ++col)
				subMatrix.complexMatrix[row][col] = m.complexMatrix[rows[row]][cols[col]].copy();
		return subMatrix;
	}

	/**
	 * Returns the sub-matrix formed with rows an columns indicated in the parameters "rows" and "columns" as a list of comma separated values.
	 * @param m The matrix.
	 * @param Srows A string with a list of integers separated by commas of the rows to be taken.
	 * @param Scols A string with a list of integers separated by commas of the columns to be taken.
	 * @return The new sub-matrix.
	 */
	static MatrixComplex subMatrix(MatrixComplex m, String Srows, String Scols) {
		int[] rows, cols;

		if (Srows.length() > 0) rows = m.intSplit(Srows, ",");
		else {
			rows = new int[1];
			rows[0] = -1;
		}
		if (Scols.length() > 0) cols = m.intSplit(Scols, ",");
		else {
			cols = new int[1];
			cols[0] = -1;
		}
		return m.subMatrix(rows, cols);
	}

	/**
	 * Returns TRUE if rowIdx has only zeros
	 * @param m The matrix.
	 * @param rowIdx The row to check
	 * @return True if is null
	 */
	static boolean isNullRow(MatrixComplex m, int rowIdx) {
		for (int col = 0; col < m.cols(); ++col) {
			if (!m.getItem(rowIdx, col).isZero()) return false;
		}
		return true;
	}

	/**
	 * Returns TRUE if colIdx has only zeros
	 * @param m The matrix.
	 * @param colIdx The col to check
	 * @return True if is null
	 */
	static boolean isNullCol(MatrixComplex m, int colIdx) {
		for (int row = 0; row < m.rows(); ++row) {
			if (!m.getItem(row, colIdx).isZero()) return false;
		}
		return true;
	}

	/**
	 * Removes every row and col which are null
	 * @param m The matrix.
	 * @return The reduced matrix without null rows and cols
	 */
	static MatrixComplex reduce(MatrixComplex m) {
		String cols, rows;
		cols = "";
		rows = "";

		for (int col = 0; col < m.cols(); ++col) {
			if (!m.isNullCol(col)) {
				cols = cols + col + ",";
			}
		}
		for (int row = 0; row < m.rows(); ++row) {
			if (!m.isNullRow(row)) {
				rows = rows + row + ",";
			}
		}
		if (rows.length() > 0 && rows.substring(rows.length()-1).equals(","))
			rows = rows.substring(0, rows.length()-1);
		if (cols.length() > 0 && cols.substring(cols.length()-1).equals(","))
			cols = cols.substring(0, cols.length()-1);
		return m.subMatrix(rows, cols);
	}
}
