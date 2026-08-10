package com.ipserc.arith.matrixcomplex;

import com.ipserc.arith.complex.Complex;

/**
 * Package-private Gram-Schmidt orthogonalization/normalization core logic for
 * {@link MatrixComplex} -- the four Gram-Schmidt variants ({@code gramSchmidt()} column-oriented
 * reduced, {@code gramSchmidtGauss()} via Gaussian elimination on the augmented Gram matrix,
 * {@code gramSchmidtFull()}/{@code gramSchmidtMFull()} extended to full dimension with random
 * fill-in vectors, {@code gramSchmidtM()} the "Modified" numerically-steadier variant),
 * {@code orthogonalize()}/{@code orthonormalize()} (shortcuts to the preferred variant),
 * {@code normalize()}/{@code normalizeByRows()}/{@code normalizeByCols()}, and the private
 * {@code dotprod(MatrixComplex,MatrixComplex)} helper they all share.
 * <p>
 * Extracted from {@code MatrixComplex.java} (Decimotercera sesion, Etapa 3 sub-fase D de la
 * reestructuracion, ver {@code Claude/ComplexArithRev.md}) -- same pattern as
 * {@code MatrixComplexEquationSystems} (sub-fase A+B), {@code MatrixComplexRank} (sub-fase C),
 * {@code MatrixComplexFormat} (Etapa 1) and {@code MatrixComplexFunctions} (Etapa 2): every method
 * here is {@code static}, takes the {@link MatrixComplex} instance as an explicit parameter, and
 * reads it only through already-public members plus {@code MatrixComplex}'s debug helper
 * ({@code trace(...)}, package-private since Etapa 2, referenced here qualified as
 * {@code MatrixComplex.trace(...)}) and {@code HEADINFO} (package-private since sub-fase A+B,
 * referenced here qualified as {@code MatrixComplex.HEADINFO}). No visibility needed widening for
 * this sub-fase -- every other helper this block calls ({@code times}, {@code adjoint},
 * {@code augment}, {@code Ftransf}, {@code copyCol}, {@code copyRow}, {@code initMatrixRandomInt},
 * {@code euc_norm}, {@code divides}) was already public. {@code MatrixComplex.java}'s own public
 * methods keep their exact signatures, delegating to these in one line each -- the public API is
 * unchanged.
 * <p>
 * {@code dotprod(MatrixComplex,MatrixComplex)} (originally {@code dotprod(MatrixComplex)}, an
 * instance method reading {@code this} as the first operand) stays {@code private} here, called
 * directly as a sibling static method by the four Gram-Schmidt variants that use it (no public
 * delegator ever existed for it -- it lives physically at the start of the file's next section
 * (Kronecker/kernel/nullspace, sub-fase E) but its only four call sites are all inside this Gram-
 * Schmidt family, so it moves with D per the restructuring plan, avoiding a needless
 * package-private widening between D and E).
 * <p>
 * {@code gramSchmidtGauss()} was also fixed here (not just moved), at the user's explicit request,
 * after the mechanical extraction exposed two layered bugs: (1) a shape/offset bug crashing with
 * {@code ArrayIndexOutOfBoundsException} on non-square input and silently returning a transposed
 * result on square input (both fixed by reading the augmented matrix's right block at column offset
 * {@code m.rows()} instead of {@code m.cols()}, and dropping a spurious final {@code .transpose()});
 * (2) a deeper, preexisting algorithmic bug found while verifying fix (1): the method reused
 * {@code augmentedMatrix.triangle()} (i.e. {@code triangleUp()}) for its Gaussian elimination step,
 * but that method's proactive partial pivoting (Octava sesion, commit {@code db4c912}) swaps to the
 * row of largest modulus in each column -- this breaks the row-to-row correspondence between the
 * Gram matrix {@code G=m*m}{@code ^H} and {@code m} that the whole "eliminate {@code [G|m]}, read
 * back the {@code m}-block" trick depends on (it is, in effect, computing {@code L}{@code ^-1*m} via
 * forward elimination, valid only if elimination follows the ORIGINAL row order). Confirmed
 * empirically with a 200-trial random-matrix sweep: ~43% of non-singular square inputs produced a
 * non-orthogonal result with pivoting; the method now does its own elimination loop inline
 * (via {@code Ftransf}), deliberately without any row swap, verified 0/200 non-orthogonal on the
 * same sweep, plus row-orthogonality (not just shape) confirmed for tall/wide/rank-deficient cases.
 * <p>
 * The one debug {@code trace(...)} call in {@code gramSchmidtGauss()} is genuinely live code (its
 * surrounding {@code /* ... *}{@code /} markers both closed normally on their own line in the
 * original), unlike the parked-comment convention seen elsewhere in this codebase (a deliberately
 * unclosed {@code /* ... * /} marker, space before the final slash, used to "park" code without
 * deleting it -- see {@code [[complexarith_codigo_aparcado_convencion]]} in memory) -- verified
 * character by character before moving it, to avoid silently turning dead comment-text into live
 * code or vice versa; its surrounding markers were dropped when this method's body was rewritten for
 * the algorithmic fix above, but the {@code trace(...)} call itself is kept, now unconditionally
 * adjacent (no comment wrapper) since there was nothing left to preserve verbatim.
 */
class MatrixComplexOrtho {

	/*
	 * https://es.wikipedia.org/wiki/Proceso_de_ortogonalizaci%C3%B3n_de_Gram-Schmidt
	 * Proceso de ortogonalizaciÃ³n de Gram-Schmidt con el mÃ©todo de Gauss
	 */
	/**
	 * Gram-Schmidt orthogonalization process via Gaussian elimination.
	 * @param m The matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	static MatrixComplex gramSchmidtGauss(MatrixComplex m) {
		MatrixComplex auxMatrix = m.times(m.adjoint());

		MatrixComplex augmentedMatrix = auxMatrix.copy();
		augmentedMatrix = augmentedMatrix.augment(m);

		MatrixComplex.trace(augmentedMatrix, "augmentedMatrix");

		// Gaussian elimination on [G|m] (G=m*m^H) WITHOUT row swaps -- deliberately NOT
		// augmentedMatrix.triangle()/triangleUp(), whose proactive partial pivoting (since the
		// Octava sesion, commit db4c912) swaps to the row of largest modulus in each column. This
		// method computes L^-1*m via forward elimination, which is only mathematically valid when
		// elimination follows the ORIGINAL row order: swapping rows breaks the row-to-row
		// correspondence between G's structure and m's rows, producing a non-orthogonal result.
		// Confirmed empirically (200-trial random sweep): with triangle()/pivoting, ~43% of
		// non-singular square matrices produced a non-orthogonal result; with this manual,
		// swap-free elimination, 0/200.
		int n = m.rows();
		for (int k = 0; k < n; ++k) {
			Complex pivot = augmentedMatrix.getItem(k, k);
			if (pivot.equals(Complex.ZERO)) continue; // row k already a linear combination of earlier rows
			for (int row = k+1; row < n; ++row) {
				Complex cCoef = augmentedMatrix.getItem(row, k).divides(pivot.opposite());
				augmentedMatrix.Ftransf(row, k, cCoef);
			}
		}

		MatrixComplex gramSchmidtMatrix = new MatrixComplex(m.rows(), m.cols());
		for (int row = 0; row < gramSchmidtMatrix.rows(); ++row) {
			for (int col = 0; col < gramSchmidtMatrix.cols(); ++col) {
				gramSchmidtMatrix.setItem(row, col, augmentedMatrix.getItem(row, col+m.rows()));
			}
		}

		return gramSchmidtMatrix;
	}

	/**
	 * Gram-Schmidt orthogonalization process.
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * It uses the MatrixComplex dotprod column oriented
	 * Use the Column calc for the dotprod. Otherwise dotprod needs work with transposed
	 * @param m The matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 * @apiNote BUG FIXED (Vigesimosexta sesion, auditoria matematica): the vectors being
	 * orthogonalized are {@code m}'s ROWS (extracted via {@code m.transpose()} + {@code copyCol},
	 * matching the row-per-vector convention used throughout this codebase for a basis/list of
	 * vectors -- e.g. {@code Eigenspace.solutions()}, {@code VectorComplex.base()} -- despite the
	 * misleading "we operate with cols" comment below, which refers to the INTERMEDIATE transposed
	 * matrix's columns, not the conceptual vectors; confirmed empirically: extracting {@code m}'s
	 * actual columns instead breaks {@code Schurfactor} (its Schur matrix stopped being upper
	 * triangular), which relies on {@code VectorComplex.base().orthonormalize()}'s row convention.
	 * The REAL bug was the clamp {@code colLen = colLen>rowLen?rowLen:colLen}, which shrank
	 * {@code colLen} (the raw vector COUNT, {@code m.rows()}) down to {@code rowLen} (the ambient
	 * DIMENSION, {@code m.cols()}) instead of only capping the LOOP bound / output vector count --
	 * for a rectangular {@code m} this produced a wrong-shaped result (confirmed:
	 * {@code QRfactor.qrGramSchmidt()} on a 3x2 matrix returned a 2x2 {@code Q}, making
	 * {@code Q*R} dimensionally undefined, surfacing as {@code Infinity}). Fixed by keeping
	 * {@code colLen} unclamped and introducing a separate {@code vecCount=min(rowLen,colLen)}
	 * ("reduced to the smaller dimension", this method's own contract) for the loop/output size
	 * only -- extraction, dotprod plumbing and the final transpose are otherwise UNCHANGED. See
	 * {@code Claude/ComplexArithRev.md} for the measurements (both the rectangular-QR fix and the
	 * Schurfactor regression this avoids).
	 */
	static MatrixComplex gramSchmidt(MatrixComplex m) {
		// Because we operate with cols
		MatrixComplex thsiTansposed = m.transpose();
		int rowLen = thsiTansposed.rows();
		int colLen = thsiTansposed.cols();
		int vecCount = Math.min(rowLen, colLen);

		MatrixComplex gramschmidt = new MatrixComplex(rowLen, vecCount);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < vecCount; ++i) {
			x.copyCol(0, thsiTansposed, i);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidt, j);
				g = g.minus(v.times((dotprod(g, v)).divides(dotprod(v, v)))) ;
			}
			gramschmidt.copyCol(i, g, 0);
		}
		return gramschmidt.transpose();
	}

	/**
	 * Gram-Schmidt Full orthogonalization process. Full means that the not included vectors of the base are randomly generated with integers between 0 and 9.
	 * The calculated orthogonal matrix is extended to the full dimension of the matrix.
	 * @param m The matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 * @apiNote BUG FIXED (Vigesimosexta sesion, auditoria matematica): same shape/dimension defect
	 * as {@link #gramSchmidt(MatrixComplex)} (see its apiNote), plus a SECOND bug specific to this
	 * method -- the random-fill branch ({@code else x.initMatrixRandomInt(9)}) was unreachable
	 * dead code, since the loop bound was the same (clamped) {@code colLen} the {@code i<colLen}
	 * guard tested, so {@code i} could never reach the {@code else}. This method could never
	 * actually do what its own name/Javadoc promise ("extended to the full dimension... not
	 * included vectors... randomly generated"). The vectors are {@code m}'s ROWS (see {@link
	 * #gramSchmidt(MatrixComplex)}'s apiNote for why that convention is kept, not {@code m}'s
	 * columns), extracted via {@code m.transpose()}+{@code copyCol} exactly as before -- only the
	 * loop now genuinely runs to the full ambient dimension ({@code rowLen}, unclamped), reaching
	 * the random fill-in branch for slots beyond {@code m}'s own {@code colLen} row vectors.
	 */
	static MatrixComplex gramSchmidtFull(MatrixComplex m) {
		// Because we operate with cols
		MatrixComplex thsiTansposed = m.transpose();
		int rowLen = thsiTansposed.rows();		// ambient dimension -- also the FULL output vector count
		int colLen = thsiTansposed.cols();		// number of m's own row vectors available

		MatrixComplex gramschmidtF = new MatrixComplex(rowLen, rowLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < rowLen; ++i) {
			if (i < colLen) x.copyCol(0, thsiTansposed, i);
			else x.initMatrixRandomInt(9);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidtF, j);
				g = g.minus(v.times((dotprod(x, v)).divides(dotprod(v, v)))) ;
			}
			gramschmidtF.copyCol(i, g, 0);
		}
		return gramschmidtF.transpose();
	}

	/**
	 * Gram-Schmidt Full Modified orthogonalization process. Modified Algorithm (http://www.ehu.eus/izaballa/Ana_Matr/Apuntes/lec6.pdf)
	 * The calculated orthogonal matrix is extended to the full dimension of the matrix.
	 * @param m The matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 * @apiNote BUG FIXED (Vigesimosexta sesion, auditoria matematica): same family of defects as
	 * {@link #gramSchmidtFull(MatrixComplex)} (see its apiNote) -- a clamp that shrank the ambient
	 * dimension instead of just the loop count, and on top of that the pre-fill {@code
	 * gramschmidtF = thsiTansposed.copy()} recycled leftover transposed data for the "extra" slots
	 * instead of ever reaching the random fill-in branch (same unreachable-{@code else} bug as
	 * {@link #gramSchmidtFull(MatrixComplex)}). Row-vector extraction convention kept unchanged
	 * (see {@link #gramSchmidt(MatrixComplex)}'s apiNote for why); loop now genuinely reaches the
	 * random fill-in for slots beyond {@code m}'s own row vectors; keeps this method's own
	 * {@code dotprod(g,v)} (evolving vector) distinction from
	 * {@link #gramSchmidtFull(MatrixComplex)}'s {@code dotprod(x,v)} (fixed original) intact.
	 */
	static MatrixComplex gramSchmidtMFull(MatrixComplex m) {
		// Because we operate with cols
		MatrixComplex thsiTansposed = m.transpose();
		int rowLen = thsiTansposed.rows();		// ambient dimension -- also the FULL output vector count
		int colLen = thsiTansposed.cols();		// number of m's own row vectors available

		MatrixComplex gramschmidtF = new MatrixComplex(rowLen, rowLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < rowLen; ++i) {
			if (i < colLen) x.copyCol(0, thsiTansposed, i);
			else x.initMatrixRandomInt(9);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidtF, j);
				g = g.minus(v.times((dotprod(g, v)).divides(dotprod(v, v)))) ;
			}
			gramschmidtF.copyCol(i, g, 0);
		}
		return gramschmidtF.transpose();
	}

	/**
	 * Gram-Schmidt Modified orthogonalization process. Modified Algorithm (http://www.ehu.eus/izaballa/Ana_Matr/Apuntes/lec6.pdf)
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * @param m The matrix.
	 * @return The matrix with the orthogonal basis that generates the same vector subspace.
	 * @apiNote BUG FIXED (Vigesimosexta sesion, auditoria matematica): same shape/dimension defect
	 * as {@link #gramSchmidt(MatrixComplex)} (see its apiNote) -- same fix, same row-vector
	 * extraction convention kept unchanged, only the clamp replaced by a separate {@code vecCount}
	 * for the loop/output size.
	 */
	static MatrixComplex gramSchmidtM(MatrixComplex m) {
		// Because we operate with cols
		MatrixComplex thsiTansposed = m.transpose();
		int rowLen = thsiTansposed.rows();
		int colLen = thsiTansposed.cols();
		int vecCount = Math.min(rowLen, colLen);

		MatrixComplex gramschmidt = new MatrixComplex(rowLen, vecCount);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < vecCount; ++i) {
			x.copyCol(0, thsiTansposed, i);
			g = x;
			for (int j = i-1; j >= 0; --j) {
				v.copyCol(0, gramschmidt, j);
				g = g.minus(v.times((dotprod(g, v)).divides(dotprod(v, v)))) ;
			}
			gramschmidt.copyCol(i, g, 0);
		}
		return gramschmidt.transpose();
	}

	/**
	 * Shortcut to the preferred orthogonalization method
	 * @param m The matrix.
	 * @return The orthogonal Matrix
	 */
	static MatrixComplex orthogonalize(MatrixComplex m) {
		return m.gramSchmidt();
	}

	/**
	 * Shortcut to normalize method.
	 * @param m The matrix.
	 * @return The normalized matrix.
	 */
	static MatrixComplex normalize(MatrixComplex m) {
		return m.normalizeByRows();
	}

	/**
	 * Shortcut to the preferred orthonormalization method
	 * @param m The matrix.
	 * @return The orthonormal Matrix
	 */
	static MatrixComplex orthonormalize(MatrixComplex m) {
		return m.orthogonalize().normalizeByRows();
	}

	/**
	 * Normalizes the matrix by columns using the Euclidean norm.
	 * @param m The matrix.
	 * @return The normalized matrix.
	 */
	static MatrixComplex normalizeByCols(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		double v_euc_norm;
		MatrixComplex norm = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);

		for (int col = 0; col < colLen; ++col) {
			v.copyCol(0, m, col);
			v_euc_norm = v.euc_norm();
			if (v_euc_norm > Complex.zero_treshold_exact()) v = v.divides(v.euc_norm());
			norm.copyCol(col, v, 0);
		}
		return norm;
	}

	/**
	 * Normalizes the matrix by rows using the Euclidean norm.
	 * @param m The matrix.
	 * @return The normalized matrix.
	 */
	static MatrixComplex normalizeByRows(MatrixComplex m) {
		int rowLen = m.rows();
		int colLen = m.cols();
		double v_euc_norm;
		MatrixComplex norm = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(1, colLen);

		for (int row = 0; row < rowLen; ++row) {
			v.copyRow(0, m, row);
			v_euc_norm = v.euc_norm();
			if (v_euc_norm > Complex.zero_treshold_exact()) v = v.divides(v.euc_norm());
			norm.copyRow(row, v, 0);
		}
		return norm;
	}

	/**
	 * Private method. The dot product used in some other methods (gramSchmidt)
	 * This is the dotprod for MatrixComplex class. "Vector" class has the public method dotprod used in vector arithmetic.
	 * @param m The matrix (first operand, was {@code this}).
	 * @param cMatrix A index to the row to make the dot product.
	 * @return The Complex result of the dot product.
	 */
	private static Complex dotprod(MatrixComplex m, MatrixComplex cMatrix) {
		int rowLen = m.rows();
		int colLen = m.cols();
		int rowLenC = cMatrix.rows();
		int colLenC = cMatrix.cols();

		if (colLen != 1 || colLenC != 1) {
			System.err.println(MatrixComplex.HEADINFO + "dotprod: " + "One of the componentes isn't a row/col");
		}

		if (rowLen != rowLenC) {
			System.err.println(MatrixComplex.HEADINFO + "dotprod: " + "row col of different size");
		}

		return cMatrix.adjoint().times(m).complexMatrix[0][0];
	}
}
