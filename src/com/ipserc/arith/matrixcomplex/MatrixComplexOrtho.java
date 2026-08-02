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
 * {@code augment}, {@code triangle}, {@code copyCol}, {@code copyRow}, {@code initMatrixRandomInt},
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
 * The one debug {@code trace(...)} call in {@code gramSchmidtGauss()} is genuinely live code (its
 * surrounding {@code /* ... *}{@code /} markers both close normally on their own line), unlike the
 * parked-comment convention seen elsewhere in this codebase (a deliberately unclosed {@code /* ... * /}
 * marker, space before the final slash, used to "park" code without deleting it -- see
 * {@code [[complexarith_codigo_aparcado_convencion]]} in memory) -- verified character by character
 * before moving it, to avoid silently turning dead comment-text into live code or vice versa.
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
		final boolean DEBUG_ON = false;
		MatrixComplex auxMatrix = m.times(m.adjoint());

		MatrixComplex augmentedMatrix = auxMatrix.copy();
		augmentedMatrix = augmentedMatrix.augment(m);

		/* -------------   DEBUGGING BLOCK   ------------- */
		MatrixComplex.trace(augmentedMatrix, "augmentedMatrix");
		/* ------------- END DEBUGGING BLOCK ------------- */

		augmentedMatrix = augmentedMatrix.triangle();

		MatrixComplex gramSchmidtMatrix = new MatrixComplex(m.rows(), m.cols());
		for (int row = 0; row < gramSchmidtMatrix.rows(); ++row) {
			for (int col = 0; col < gramSchmidtMatrix.cols(); ++col) {
				gramSchmidtMatrix.setItem(row, col, augmentedMatrix.getItem(row, col+m.cols()));
			}
		}

		return gramSchmidtMatrix.transpose();
	}

	/**
	 * Gram-Schmidt orthogonalization process.
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * It uses the MatrixComplex dotprod column oriented
	 * Use the Column calc for the dotprod. Otherwise dotprod needs work with transposed
	 * @param m The matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	static MatrixComplex gramSchmidt(MatrixComplex m) {
		// Because we operate with cols
		MatrixComplex thsiTansposed = m.transpose();
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
	 */
	static MatrixComplex gramSchmidtFull(MatrixComplex m) {
		// Because we operate with cols
		MatrixComplex thsiTansposed = m.transpose();
		int rowLen = thsiTansposed.rows();
		int colLen = thsiTansposed.cols();

		colLen = colLen > rowLen ? rowLen : colLen;

		MatrixComplex gramschmidtF = new MatrixComplex(rowLen, colLen);
		MatrixComplex v = new MatrixComplex(rowLen, 1);
		MatrixComplex x = new MatrixComplex(rowLen, 1);
		MatrixComplex g = new MatrixComplex(rowLen, 1);

		for (int i = 0; i < colLen; ++i) {
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
	 */
	static MatrixComplex gramSchmidtMFull(MatrixComplex m) {
		// Because we operate with cols
		MatrixComplex thsiTansposed = m.transpose();
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
	 */
	static MatrixComplex gramSchmidtM(MatrixComplex m) {
		// Because we operate with cols
		MatrixComplex thsiTansposed = m.transpose();
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
			if (v_euc_norm > Complex.zero_treshold()) v = v.divides(v.euc_norm());
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
			if (v_euc_norm > Complex.zero_treshold()) v = v.divides(v.euc_norm());
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
