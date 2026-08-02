package com.ipserc.arith.matrixcomplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.syseq.Syseq;

/**
 * Package-private Kronecker product / kernel (nullspace) core logic for {@link MatrixComplex} --
 * {@code kroneckerprod(MatrixComplex)}, the {@code kernel(Complex)}/{@code kernel()} pair (and
 * their {@code ker(...)} shortcuts, a SINGLE nullspace vector parameterized by a scalar
 * {@code lambda}, via {@code Syseq}), and {@code nullspaceBasis()} (a genuine basis of the
 * nullspace, one vector per free column, via its own Gauss-Jordan reduction).
 * <p>
 * Extracted from {@code MatrixComplex.java} (Decimotercera sesion, Etapa 3 sub-fase E de la
 * reestructuracion, ver {@code Claude/ComplexArithRev.md}) -- last sub-fase of Etapa 3, same
 * pattern as {@code MatrixComplexEquationSystems} (sub-fase A+B), {@code MatrixComplexRank}
 * (sub-fase C) and {@code MatrixComplexOrtho} (sub-fase D): every method here is {@code static},
 * takes the {@link MatrixComplex} instance as an explicit parameter, and reads it only through
 * already-public members ({@code getItem}, {@code setItem}, {@code times}, {@code rows},
 * {@code cols}, {@code augment}, {@code copy}, {@code swapRows}, {@code Ftransf}, {@code inverse})
 * -- no visibility needed widening for this sub-fase, unlike A+B/C/D. {@code MatrixComplex.java}'s
 * own public methods keep their exact signatures, delegating to these in one line each -- the
 * public API is unchanged.
 * <p>
 * {@code dotprod(MatrixComplex,MatrixComplex)} originally lived physically at the start of this
 * section in the pre-split file, but moved to {@code MatrixComplexOrtho} in sub-fase D instead
 * (its only four call sites are all inside the Gram-Schmidt family), per the restructuring plan --
 * nothing from that helper carries over here.
 */
class MatrixComplexKernel {

	/**
	 * We define here A ⊗ B, the Kronecker product of two square matrices A = (ai,j) and B of dimension nA and nB,
	 * respectively: A ⊗ B is the square matrix of dimension nA nB obtained from A by replacing every entry ai,j by ai,j B.
	 * https://www.sciencedirect.com/topics/mathematics/kronecker-product
	 * @param m The matrix.
	 * @param matrix
	 * @return
	 */
	static MatrixComplex kroneckerprod(MatrixComplex m, MatrixComplex matrix) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int rowLenC = matrix.rows();
		int colLenC = matrix.cols();

		MatrixComplex kronmat = new MatrixComplex(rowLen*rowLenC, colLen*colLenC);
		int row = 0, col = 0;
		for (int f1 = 0; f1 < rowLen; ++f1) {
			for (int f2 = 0; f2 < rowLenC; ++f2) {
				for (int c1 = 0; c1 < colLen; ++c1) {
					for (int c2 = 0; c2 < colLenC; ++c2) {
						// System.out.printf("(f1:%d,c1:%d)*(f2:%d,c2:%d) - ", f1,c1, f2,c2);
						kronmat.setItem(row, col, m.getItem(f1, c1).times(matrix.getItem(f2, c2)));
						++col;
					}
				}
				// System.out.println();
				++row;
				col = 0;
			}
		}
		return kronmat;
	}

	/**
	 * Calulates the Kernel of a base
	 * @param m The matrix.
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The kernel vector components
	 */
	static MatrixComplex kernel(MatrixComplex m, Complex lambda) {
		MatrixComplex kernel = new MatrixComplex();
		if (m.rows() != m.cols()) {
			System.err.println("The matrix has to be square.");
			return kernel;
		}
		Syseq newSysEq = new Syseq(m.augment());
		return newSysEq.solve(lambda);
	}

	/**
	 * Calulates the Kernel of a base using Complex.ONE as seed
	 * @param m The matrix.
	 * @return The kernel vector components
	 */
	static MatrixComplex kernel(MatrixComplex m) {
		return m.kernel(Complex.ONE);
	}

	/**
	 * Shortcut to kernel() method
	 * @param m The matrix.
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The kernel vector components
	 */
	static MatrixComplex ker(MatrixComplex m, Complex lambda) {
		return m.kernel(lambda);
	}

	/**
	 * Shortcut to kernel() method
	 * @param m The matrix.
	 * @return The kernel vector components
	 */
	static MatrixComplex ker(MatrixComplex m) {
		return m.kernel();
	}

	/**
	 * Computes a genuine BASIS of the nullspace (kernel) of this matrix via Gauss-Jordan
	 * elimination to reduced row echelon form, tracking pivot vs. free columns explicitly.
	 * <p>
	 * Unlike {@link MatrixComplex#kernel()}/{@link MatrixComplex#kernel(Complex)} (which return a
	 * SINGLE vector, all free variables set to the same scalar {@code lambda} -- the right tool
	 * when the nullspace is known to be 1-dimensional, the common case throughout this project),
	 * this returns one independent vector per free column: {@code nullity()} rows, each with
	 * exactly one free variable set to {@code 1} and the rest to {@code 0}, back-substituted
	 * through the reduced pivot rows. Needed to generalize {@code Jordan.java} beyond geometric
	 * multiplicity 1, where a single scalar-parameterized vector can no longer span the eigenspace.
	 * @param m The matrix.
	 * @return A matrix with one basis vector per row ({@code cols()-rank()} rows, {@code cols()}
	 * columns each); empty (0 rows) if the nullspace is trivial.
	 */
	static MatrixComplex nullspaceBasis(MatrixComplex m) {
		int n = m.cols();
		int rowLen = m.rows();
		MatrixComplex r = m.copy();

		int[] pivotColOfRow = new int[rowLen];
		boolean[] isPivotCol = new boolean[n];
		int pivotRow = 0;

		for (int col = 0; col < n && pivotRow < rowLen; ++col) {
			int best = pivotRow;
			double bestMod = r.getItem(pivotRow, col).mod();
			for (int row = pivotRow + 1; row < rowLen; ++row) {
				double mod = r.getItem(row, col).mod();
				if (mod > bestMod) { bestMod = mod; best = row; }
			}
			if (r.getItem(best, col).isZero()) continue; // no usable pivot in this column: free column

			if (best != pivotRow) r.swapRows(pivotRow, best);
			r.Ftransf(pivotRow, r.getItem(pivotRow, col).inverse()); // normalize pivot to 1
			for (int row = 0; row < rowLen; ++row) {
				if (row == pivotRow) continue;
				Complex factor = r.getItem(row, col);
				if (!factor.isZero()) r.Ftransf(row, pivotRow, factor.opposite());
			}
			pivotColOfRow[pivotRow] = col;
			isPivotCol[col] = true;
			++pivotRow;
		}

		int nullity = 0;
		for (int col = 0; col < n; ++col) if (!isPivotCol[col]) ++nullity;

		MatrixComplex basis = new MatrixComplex(nullity, n);
		int basisRow = 0;
		for (int freeCol = 0; freeCol < n; ++freeCol) {
			if (isPivotCol[freeCol]) continue;
			basis.setItem(basisRow, freeCol, Complex.ONE);
			for (int prow = 0; prow < pivotRow; ++prow)
				basis.setItem(basisRow, pivotColOfRow[prow], r.getItem(prow, freeCol).opposite());
			++basisRow;
		}
		return basis;
	}
}
