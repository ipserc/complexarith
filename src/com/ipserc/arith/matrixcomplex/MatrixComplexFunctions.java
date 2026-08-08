package com.ipserc.arith.matrixcomplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.factorization.Schurfactor;

/**
 * Package-private matrix-function logic for {@link MatrixComplex} -- exponential, circular
 * trigonometric (sin/cos/tan and their Euler-formula/item-to-item variants), hyperbolic
 * trigonometric, logarithm (Taylor/Mercator/hyperbolic-arctangent/{@code logm()} families) and
 * fixed-base power.
 * <p>
 * Extracted from {@code MatrixComplex.java} (Undecima sesion, Etapa 2 de la reestructuracion, ver
 * {@code Claude/ComplexArithRev.md}) -- same pattern as {@code MatrixComplexFormat} (Etapa 1):
 * every method here is {@code static}, takes the {@link MatrixComplex} instance as an explicit
 * parameter, and reads it only through already-public members plus {@code MatrixComplex}'s debug
 * helper {@code trace(...)} (widened from {@code private} to package-private for this exact
 * purpose) and {@code __log10__} (widened the same way). The convergence-deviation plot used by
 * the Taylor/Mercator loops now delegates to {@link MatrixComplexPlot#doPlot(String, double[][],
 * int)} (8 agosto 2026, moved there to deduplicate against {@code Fourier}/{@code Laplace}/{@code
 * Z}'s own plotting code -- see that class's Javadoc). {@code MatrixComplex.java}'s own public
 * methods keep their exact signatures, delegating to these in one line each -- the public API is
 * unchanged.
 */
class MatrixComplexFunctions {

	/**
	 * Calculates the exponential of the matrix (e^m)
	 * This calculation is achieved using the Taylor's series of the exponential extended for complex matrices
	 * @param m The matrix.
	 * @return The value of e^m
	 */
	static MatrixComplex exp(MatrixComplex m) {
		MatrixComplex.trace("------------ exp() ------------ ");
		if (m.isNaN() || m.isNull() || m.isInfinite() ) return m;
		if (m.rows() != m.cols()) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}
		// Take advantage from diagonal matrices
		if (m.isDiagonal()) {
    		MatrixComplex.trace("Exp() of diagonal matrix");
    		MatrixComplex powerMat = m.copy();
			for (int i = 0; i < m.rows(); ++i)
				powerMat.setItem(i, i, Complex.exp(m.getItem(i, i)));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(m);
    	if (dmat.isDiagonalizable()) {
    		MatrixComplex.trace("Exp() using diagonalization P·D·P⁻¹");
	        MatrixComplex.trace(dmat.P(), "Matrix P");
	        MatrixComplex.trace(dmat.D(), "Matrix D");

        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i)
        		Dmat.setItem(i, i, Complex.exp(Dmat.getItem(i, i)));
    		MatrixComplex.trace(Dmat, "Dmat");
            MatrixComplex.trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Exp() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}

    	// Finally use the taylor Expansion
		MatrixComplex.trace("Exp() using the Taylor expansion");
    	return exp_(m);
	}

	/**
	 * Calculates the exponential of the matrix (e^m)
	 * This calculation is achieved using the Taylor's series of the exponential extended for complex matrices
	 * @param m The matrix.
	 * @return The value of e^m
	 */
	static MatrixComplex exp_(MatrixComplex m) {
		MatrixComplex expMatrix = new MatrixComplex(m.rows(), m.cols());
		MatrixComplex expMatant;
		MatrixComplex powMatrix = new MatrixComplex(m.rows(), m.cols());
		MatrixComplex errMatrix;
		MatrixComplex errAntMat = new MatrixComplex(m.rows(), m.cols());

		int cNorma = (int)Math.ceil(m.euc_norm())*10;
		MatrixComplex thisNorma = m.divides(cNorma);

		expMatrix.initMatrixDiag(1, 0);
		powMatrix.initMatrixDiag(1, 0);

		// precision can be changed with Complex.digits(long_value)
		long maxIter = Complex.digits();
		long k = 1;
		double fact = 1;
		double accumulator = 0.0;
		do {
			expMatant = expMatrix.copy();
			powMatrix.timesEq(thisNorma);
			fact *= k++;
			expMatrix.plusEq(powMatrix.divides(fact));
			errMatrix = expMatant.minus(expMatrix);
			errMatrix.abs();
			MatrixComplex.trace(errMatrix, "public MatrixComplex exp() - errmatrix:");
			// thisNorma is already scaled to a small norm (~1/10), so this series converges very
			// fast in practice; still, fail loudly instead of silently returning a NaN-poisoned
			// result if something pathological happens (e.g. the scaling itself overflowed).
			if (errMatrix.isNaN()) {
				throw new IllegalArgumentException("exp_: the Taylor series produced a non-finite result for this matrix.");
			}
			if (errMatrix.isNullC()) break;
			if (k > 100) {
				double deviation = errMatrix.norm()/errAntMat.norm();
				accumulator += deviation > 1 ? deviation : 0.0;
				if (accumulator > 500) {
					throw new IllegalArgumentException("exp_: the Taylor series is not converging cleanly for this matrix.");
				}
			}
			errAntMat = errMatrix.copy();
		} while(k < maxIter);

		MatrixComplex.trace("Iterations to converge:" + k);

		return expMatrix.power(cNorma);
	}

	/**
	 * The "SIN" or "COS" depending on the sign. One method to rule them all
	 * @param m The matrix.
	 * @param sign 1 for "SIN"; -1 for "COS"
	 * @return The "SIN" or "COS" depending on the sign
	 */
	private static MatrixComplex trigonTaylor(MatrixComplex m, int sign) {
		if (m.rows() != m.cols()) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		// normalize2PI() used to be applied here, but reducing the WHOLE matrix's norm modulo 2pi
		// is only valid periodicity-wise for a scalar -- sin/cos periodicity (z+2pi*k) applies per
		// eigenvalue, not to a matrix rescaled by a single norm-based factor. Removed; the raw
		// Taylor series converges for any matrix (sin/cos are entire functions), just possibly
		// slower for a matrix with a large norm.
		MatrixComplex normalThis = m;
		MatrixComplex trigonMatrix = new MatrixComplex(m.rows(), m.cols());
		MatrixComplex trigonMatant;
		MatrixComplex powMatrix = new MatrixComplex(m.rows(), m.cols());
		MatrixComplex errMatrix;
		MatrixComplex errAntMat = new MatrixComplex(m.rows(), m.cols());

		boolean SIN = sign == 1 ? true : false;
		boolean COS = !SIN;

		trigonMatrix.initMatrixDiag(SIN ? 0 : 1, 0);
		powMatrix.initMatrixDiag(1, 0);

		// precision can be changed with Complex.digits(long_value)
		long maxIter = Complex.digits();
		int k = 1;
		double fact = 1;
		double accumulator = 0.0;
		// Catastrophic cancellation watchdog: the alternating sin/cos series can swing through
		// partial sums many orders of magnitude larger than the converged result (verified: Jordan
		// [[50,1],[0,50]] makes sinTaylor() peak around 1e21 before "converging" to -31194.8, when
		// the true value is -0.2624 -- the divergence safety net above does NOT catch this, since
		// the iteration-to-iteration error keeps shrinking normally throughout, it just shrinks
		// towards the wrong number). Track the largest partial-sum norm seen; if it dwarfs the
		// final norm by more than a double can represent (~2^52, about 4.5e15), every bit of the
		// final result is rounding noise from the cancellation, not signal.
		double maxPartialNorm = trigonMatrix.norm();
		do {
			fact *= k;
			if (SIN && k%2 == 0) {
					powMatrix.timesEq(normalThis);
					continue;
			}
			if (COS && k%2 != 0) {
					powMatrix.timesEq(normalThis);
					continue;
			}
			trigonMatant = trigonMatrix.copy();
			powMatrix.timesEq(normalThis);
			trigonMatrix.plusEq(powMatrix.divides(fact).times(sign));
			sign *= -1;
			// NaN/Infinity trap: for large eigenvalues (e.g. Jordan [[80,1],[0,80]]) the unscaled
			// power powMatrix can overflow double's range well before the series would otherwise
			// converge or trip the divergence/cancellation safety nets below -- and once NaN
			// appears, BOTH of those nets go blind, since every numeric comparison against NaN is
			// false in IEEE754 (deviation>1 is false, finalNorm<noiseFloor is false), letting NaN
			// propagate silently all the way to the return value (verified: sinTaylor() on that
			// Jordan returns NaN+NaNi with no exception, taking 80s to grind through ~10^7
			// iterations first). Catch it here, immediately, instead of relying on those checks.
			double curNorm = trigonMatrix.norm();
			if (Double.isNaN(curNorm) || Double.isInfinite(curNorm)) {
				throw new IllegalArgumentException((SIN ? "sinTaylor" : "cosTaylor") + ": the unscaled Taylor series overflowed double's range at iteration " + k
						+ " for this matrix (norm too large?); use sin()/cos() instead, which fall back to Euler's formula and scale correctly.");
			}
			if (curNorm > maxPartialNorm) maxPartialNorm = curNorm;
			errMatrix = trigonMatant.minus(trigonMatrix);
			errMatrix.abs();
			if (errMatrix.isNullC()) break;

			// Divergence safety net: sin/cos are entire functions (the series converges for any
			// matrix), but without any argument reduction a large-norm matrix can need many terms
			// and suffer growing intermediate cancellation before the terms start shrinking. Fail
			// loudly instead of silently iterating up to maxIter (~10^8 by default).
			if (k > 100) {
				double deviation = errMatrix.norm()/errAntMat.norm();
				accumulator += deviation > 1 ? deviation : 0.0;
				if (accumulator > 500) {
					throw new IllegalArgumentException((SIN ? "sinTaylor" : "cosTaylor") + ": the Taylor series is not converging cleanly for this matrix (norm too large?).");
				}
			}
			errAntMat = errMatrix.copy();
		} while(++k < maxIter);

		MatrixComplex.trace("Iterations to converge:" + k);

		// Noise-floor test: each of the k additions carries a relative rounding error of about one
		// ULP, so a running sum that peaked at maxPartialNorm accumulates an absolute error floor
		// of roughly maxPartialNorm * k * eps (a 10x cushion is applied since this is an estimate,
		// not an exact bound). A single fixed peak/final ratio threshold is not enough on its own:
		// verified with the same catastrophic Jordan that cosTaylor() converges in fewer terms with
		// a smaller peak/final ratio (2.45e15) than sinTaylor() (5.66e15) despite being equally
		// wrong (1929.6 vs the exact 0.965) -- accounting for k separates both from the
		// well-conditioned case (ratio 1.2, k=15) correctly.
		double finalNorm = trigonMatrix.norm();
		double noiseFloor = maxPartialNorm * k * Math.ulp(1.0) * 10;
		if (maxPartialNorm > 0 && finalNorm < noiseFloor) {
			throw new IllegalArgumentException((SIN ? "sinTaylor" : "cosTaylor") + ": catastrophic cancellation destroyed all precision for this matrix (partial sums reached "
					+ maxPartialNorm + " over " + k + " terms before cancelling down to " + finalNorm + ", below the double-precision noise floor of " + noiseFloor
					+ "; norm too large for a direct Taylor series -- use sin()/cos() instead, which fall back to Euler's formula).");
		}

		return trigonMatrix;
	}

	/**
	 * Calculates the sin of the matrix
	 * This calculation is achieved using the Taylor's series of the sin extended for complex matrices
	 * @param m The matrix.
	 * @return The value of sinTaylor()
	 */
	static MatrixComplex sinTaylor(MatrixComplex m) {
		return trigonTaylor(m, 1);
	}

	/**
	 * Calculates the sin of the matrix sinEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @param m The matrix.
	 * @return The value of sinEuler()
	 */
	static MatrixComplex sinEuler(MatrixComplex m) {
		Complex plusj = new Complex(0,1);
		Complex minusj = new Complex(0,-1);

		// normalize2PI() removed: exp() already handles any matrix correctly (including scaling
		// for large norms), so no periodicity reduction is needed -- and the previous reduction
		// was mathematically invalid for non-scalar matrices (see trigonTaylor()).
		return (m.times(plusj).exp().minus(m.times(minusj).exp())).divides(Complex.i).divides(2);
	}

	/**
	 * Calculates the sin of the matrix sin()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @param m The matrix.
	 * @return The value of sin()
	 */
	static MatrixComplex sin(MatrixComplex m) {
		// Take advantage from diagonal matrices
		if (m.isDiagonal()) {
    		MatrixComplex.trace("Sin() of diagonal matrix");
    		MatrixComplex powerMat = m.copy();
			for (int i = 0; i < m.rows(); ++i)
				powerMat.setItem(i, i, Complex.sin(m.getItem(i, i)));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(m);
    	if (dmat.isDiagonalizable()) {
			MatrixComplex.trace("Sin() using diagonalization P·D·P⁻¹");
        	MatrixComplex.trace(dmat.P(), "Matrix P");
        	MatrixComplex.trace(dmat.D(), "Matrix D");

        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i)
        		Dmat.setItem(i, i, Complex.sin(Dmat.getItem(i, i)));
			MatrixComplex.trace(Dmat, "Dmat");
        	MatrixComplex.trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Sin() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}

    	// Finally use Euler's formula (relies on exp(), already correct for any matrix including
    	// scaling for large norms) -- same choice already made by tan()/tanEuler().
		MatrixComplex.trace("Sin() using Euler's formula");
		return sinEuler(m);
	}

	/**
	 * Calculates the sin item to item of this matrix
	 * @param m The matrix.
	 * @return The sin item to item of this matrix
	 */
	static MatrixComplex ssin(MatrixComplex m) {
		MatrixComplex sinMat = new MatrixComplex(m.rows(), m.cols());

		for (int row = 0; row < sinMat.rows(); ++row)
			for (int col = 0; col < sinMat.cols(); ++col)
				sinMat.setItem(row, col, Complex.sin(m.getItem(row, col)));
		return sinMat;
	}

	/**
	 * Calculates the cos of the matrix cosTaylor()
	 * This calculation is achieved using the Taylor's series of the cos extended for complex matrices
	 * @param m The matrix.
	 * @return The value of cosTaylor()
	 */
	static MatrixComplex cosTaylor(MatrixComplex m) {
		return trigonTaylor(m, -1);
	}

	/**
	 * Calculates the sin of the matrix cosEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @param m The matrix.
	 * @return The value of cosEuler()
	 */
	static MatrixComplex cosEuler(MatrixComplex m) {
		Complex plusj = new Complex(0,1);
		Complex minusj = new Complex(0,-1);

		// normalize2PI() removed, see sinEuler().
		return (m.times(plusj).exp().plus(m.times(minusj).exp())).divides(2);
	}

	/**
	 * Calculates the sin of the matrix cos()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @param m The matrix.
	 * @return The value of cos()
	 */
	static MatrixComplex cos(MatrixComplex m) {
		// Take advantage from diagonal matrices
		if (m.isDiagonal()) {
			MatrixComplex.trace("Cos() of diagonal matrix");
    		MatrixComplex powerMat = m.copy();
			for (int i = 0; i < m.rows(); ++i)
				powerMat.setItem(i, i, Complex.cos(m.getItem(i, i)));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(m);
    	if (dmat.isDiagonalizable()) {
			MatrixComplex.trace("Cos() using diagonalization P·D·P⁻¹");
        	MatrixComplex.trace(dmat.P(), "Matrix P");
        	MatrixComplex.trace(dmat.D(), "Matrix D");

        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i)
        		Dmat.setItem(i, i, Complex.cos(Dmat.getItem(i, i)));
			MatrixComplex.trace(Dmat, "Dmat");
        	MatrixComplex.trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "cos() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}

    	// Finally use Euler's formula (relies on exp(), already correct for any matrix including
    	// scaling for large norms) -- same choice already made by tan()/tanEuler().
		MatrixComplex.trace("Cos() using Euler's formula");
		return cosEuler(m);
	}

	/**
	 * Calculates the cos item to item of this matrix
	 * @param m The matrix.
	 * @return The cos item to item of this matrix
	 */
	static MatrixComplex ccos(MatrixComplex m) {
		MatrixComplex cosMat = new MatrixComplex(m.rows(), m.cols());

		for (int row = 0; row < cosMat.rows(); ++row)
			for (int col = 0; col < cosMat.cols(); ++col)
				cosMat.setItem(row, col, Complex.cos(m.getItem(row, col)));
		return cosMat;
	}

	/**
	 * Calculates the tan of the matrix tanTaylor()
	 * The tangent is calculated as sinTaylor()/cosTaylor()
	 * @param m The matrix.
	 * @return The value of tanTaylor()
	 */
	static MatrixComplex tanTaylor(MatrixComplex m) {
		return sinTaylor(m).divides(cosTaylor(m));
	}

	/**
	 * Calculates the tan of the matrix tanEuler()
	 * The tangent is calculated as sinEuler()/cosEuler()
	 * @param m The matrix.
	 * @return The value of tanEuler()
	 */
	static MatrixComplex tanEuler(MatrixComplex m) {
		return sinEuler(m).divides(cosEuler(m));
	}

	/**
	 * Calculates the sin of the matrix tan()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @param m The matrix.
	 * @return The value of tan()
	 */
	static MatrixComplex tan(MatrixComplex m) {
		return tanEuler(m);
	}

	/**
	 * Calculates the tan item to item of this matrix
	 * @param m The matrix.
	 * @return The tan item to item of this matrix
	 */
	static MatrixComplex ttan(MatrixComplex m) {
		MatrixComplex tanMat = new MatrixComplex(m.rows(), m.cols());

		for (int row = 0; row < tanMat.rows(); ++row)
			for (int col = 0; col < tanMat.cols(); ++col)
				tanMat.setItem(row, col, Complex.tan(m.getItem(row, col)));
		return tanMat;
	}

	/**
	 * Euler's formula e^[+/-]x=cos(x)[+/-]i·sin(x)
	 * @param m The matrix.
	 * @return Euler's formula e^x
	 */
	static MatrixComplex euler(MatrixComplex m) {
		// normalize2PI() removed, see sinEuler().
		return exp(m.times(Complex.i));
	}

	/**
	 * The "SINH" or "COSH" depending hypFunc. One method to rule them all
	 * @param m The matrix.
	 * @param hypFunc hyptrigon.SINH for SINH, hyptrigon.COSH for COSH
	 * @return
	 */
	private enum hyptrigon {SINH, COSH};
	private static MatrixComplex trigonHyperbolycTaylor(MatrixComplex m, hyptrigon hypFunc) {
		if (!m.isSquare()) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		MatrixComplex trigHypMatrix = new MatrixComplex(m.rows(), m.cols());
		MatrixComplex trigHypMatant;
		MatrixComplex powMatrix = new MatrixComplex(m.rows(), m.cols());
		MatrixComplex errMatrix;
		MatrixComplex errAntMat = new MatrixComplex(m.rows(), m.cols());

		trigHypMatrix.initMatrixDiag(hypFunc == hyptrigon.SINH ? 0 : 1, 0);
		powMatrix.initMatrixDiag(1, 0);

		// precision can be changed with Complex.digits(long_value)
		long maxIter = Complex.digits();
		int k = 1;
		double fact = 1;
		double accumulator = 0.0;
		do {
			fact *= k;
			if ((hypFunc == hyptrigon.SINH && k%2 == 0) || (hypFunc == hyptrigon.COSH && k%2 != 0)) {
				powMatrix.timesEq(m);
				continue;
			}
			trigHypMatant = trigHypMatrix.copy();
			powMatrix.timesEq(m);
			trigHypMatrix.plusEq(powMatrix.divides(fact));
			errMatrix = trigHypMatant.minus(trigHypMatrix);
			errMatrix.abs();
			if (errMatrix.isNullC()) break;

			// Divergence safety net, same rationale as trigonTaylor().
			if (k > 100) {
				double deviation = errMatrix.norm()/errAntMat.norm();
				accumulator += deviation > 1 ? deviation : 0.0;
				if (accumulator > 500) {
					throw new IllegalArgumentException((hypFunc == hyptrigon.SINH ? "sinhTaylor" : "coshTaylor") + ": the Taylor series is not converging cleanly for this matrix (norm too large?).");
				}
			}
			errAntMat = errMatrix.copy();
		} while(++k < maxIter);

		MatrixComplex.trace("Iterations to converge:" + k);
		return trigHypMatrix;
	}

	/**
	 * Calculates the hyperbolic sin of the matrix sinhTaylor(this)
	 * This calculation is achieved using the Taylor's series of the hyperbolic sin extended for complex matrices
	 * @param m The matrix.
	 * @return The value of sinh()
	 */
	static MatrixComplex sinhTaylor(MatrixComplex m) {
		return trigonHyperbolycTaylor(m, hyptrigon.SINH);
	}

	/**
	 * Calculates the hyperbolic sin of the matrix sinhEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @param m The matrix.
	 * @return The value of sinhEuler()
	 */
	static MatrixComplex sinhEuler(MatrixComplex m) {
		return m.exp().minus(m.opposite().exp()).divides(2);
	}

	/**
	 * Calculates the sin of the matrix sinh()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @param m The matrix.
	 * @return The value of sinh()
	 */
	static MatrixComplex sinh(MatrixComplex m) {
		// Take advantage from diagonal matrices
		if (m.isDiagonal()) {
			MatrixComplex.trace("Sinh() of diagonal matrix");
    		MatrixComplex powerMat = m.copy();
			for (int i = 0; i < m.rows(); ++i)
				powerMat.setItem(i, i, Complex.sinh(m.getItem(i, i)));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(m);
    	if (dmat.isDiagonalizable()) {
			MatrixComplex.trace("Sinh() using diagonalization P·D·P⁻¹");
        	MatrixComplex.trace(dmat.P(), "Matrix P");
        	MatrixComplex.trace(dmat.D(), "Matrix D");

        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i)
        		Dmat.setItem(i, i, Complex.sinh(Dmat.getItem(i, i)));
			MatrixComplex.trace(Dmat, "Dmat");
        	MatrixComplex.trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Sinh() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}

    	// Finally use the taylor Expansion
		MatrixComplex.trace("Sinh() using the Taylor expansion");
		return sinhTaylor(m);
	}

	/**
	 * Calculates the sinh item to item of this matrix
	 * @param m The matrix.
	 * @return The sinh item to item of this matrix
	 */
	static MatrixComplex ssinh(MatrixComplex m) {
		MatrixComplex sinhMat = new MatrixComplex(m.rows(), m.cols());

		for (int row = 0; row < sinhMat.rows(); ++row)
			for (int col = 0; col < sinhMat.cols(); ++col)
				sinhMat.setItem(row, col, Complex.sinh(m.getItem(row, col)));
		return sinhMat;
	}

	/**
	 * Calculates the hyperbolic cos of the matrix coshTaylor()
	 * This calculation is achieved using the Taylor's series of the hyp cos extended for complex matrices
	 * @param m The matrix.
	 * @return The value of coshTaylor()
	 */
	static MatrixComplex coshTaylor(MatrixComplex m) {
		return trigonHyperbolycTaylor(m, hyptrigon.COSH);
	}

	/**
	 * Calculates the hyperbolic sin of the matrix coshEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @param m The matrix.
	 * @return The value of coshEuler()
	 */
	static MatrixComplex coshEuler(MatrixComplex m) {
		return m.exp().plus(m.opposite().exp()).divides(2);
	}

	/**
	 * Calculates the sin of the matrix cosh()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @param m The matrix.
	 * @return The value of cosh()
	 */
	static MatrixComplex cosh(MatrixComplex m) {
		// Take advantage from diagonal matrices
		if (m.isDiagonal()) {
			MatrixComplex.trace("Cosh() of diagonal matrix");
    		MatrixComplex powerMat = m.copy();
			for (int i = 0; i < m.rows(); ++i)
				powerMat.setItem(i, i, Complex.cosh(m.getItem(i, i)));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(m);
    	if (dmat.isDiagonalizable()) {
			MatrixComplex.trace("Cosh() using diagonalization P·D·P⁻¹");
        	MatrixComplex.trace(dmat.P(), "Matrix P");
        	MatrixComplex.trace(dmat.D(), "Matrix D");

        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i)
        		Dmat.setItem(i, i, Complex.cosh(Dmat.getItem(i, i)));
			MatrixComplex.trace(Dmat, "Dmat");
        	MatrixComplex.trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Cosh() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}

    	// Finally use the taylor Expansion
		MatrixComplex.trace("Cosh() using the Taylor expansion");
		return coshTaylor(m);
	}

	/**
	 * Calculates the cosh item to item of this matrix
	 * @param m The matrix.
	 * @return The cosh item to item of this matrix
	 */
	static MatrixComplex ccosh(MatrixComplex m) {
		MatrixComplex coshMat = new MatrixComplex(m.rows(), m.cols());

		for (int row = 0; row < coshMat.rows(); ++row)
			for (int col = 0; col < coshMat.cols(); ++col)
				coshMat.setItem(row, col, Complex.cosh(m.getItem(row, col)));
		return coshMat;
	}

	/**
	 * Calculates the hyperbolic tan of the matrix tanhTaylor()
	 * This calculation uses the Taylor's series of the sin and cos extended for complex matrices
	 * The hyperbolic tangent is calculated as sinhTaylor()/coshTaylor()
	 * @param m The matrix.
	 * @return The value of tanhTaylor()
	 */
	static MatrixComplex tanhTaylor(MatrixComplex m) {
		return sinhTaylor(m).divides(coshTaylor(m));
	}

	/**
	 * Calculates the hyperbolic tan of the matrix tanhEuler()
	 * This calculation uses the Euler's formulas of the sin and cos extended for complex matrices
	 * The hyperbolic tangent is calculated as sinhEuler()/coshEuler()
	 * @param m The matrix.
	 * @return The value of tanhEuler()
	 */
	static MatrixComplex tanhEuler(MatrixComplex m) {
		return sinhEuler(m).divides(coshEuler(m));
	}

	/**
	 * Calculates the tan of the matrix tanh()
	 * The tangent is calculated as tanh preferred method
	 * @param m The matrix.
	 * @return The value of tanh()
	 */
	static MatrixComplex tanh(MatrixComplex m) {
		// Take advantage from diagonal matrices
		if (m.isDiagonal()) {
			MatrixComplex.trace("Tanh() of diagonal matrix");
    		MatrixComplex powerMat = m.copy();
			for (int i = 0; i < m.rows(); ++i)
				powerMat.setItem(i, i, Complex.tanh(m.getItem(i, i)));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(m);
    	if (dmat.isDiagonalizable()) {
			MatrixComplex.trace("Tanh() using diagonalization P·D·P⁻¹");
        	MatrixComplex.trace(dmat.P(), "Matrix P");
        	MatrixComplex.trace(dmat.D(), "Matrix D");

        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i)
        		Dmat.setItem(i, i, Complex.tanh(Dmat.getItem(i, i)));
			MatrixComplex.trace(Dmat, "Dmat");
        	MatrixComplex.trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Tanh() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}

    	// Finally use the taylor Expansion
		MatrixComplex.trace("Tanh() using the Taylor expansion");
		return tanhTaylor(m);
	}

	/**
	 * Calculates the tanh item to item of this matrix
	 * @param m The matrix.
	 * @return The tanh item to item of this matrix
	 */
	static MatrixComplex ttanh(MatrixComplex m) {
		MatrixComplex tanhMat = new MatrixComplex(m.rows(), m.cols());

		for (int row = 0; row < tanhMat.rows(); ++row)
			for (int col = 0; col < tanhMat.cols(); ++col)
				tanhMat.setItem(row, col, Complex.tanh(m.getItem(row, col)));
		return tanhMat;
	}

	/**
	 * Hard cap on {@link #logTaylor}'s iteration count, overriding {@code Complex.digits()}
	 * (which is {@code 10^precision}, astronomically large, never meant to be reached in
	 * practice -- the real exit was supposed to be either convergence or the deviation-based
	 * divergence check). Confirmed real case where BOTH those exits fail to fire in reasonable
	 * time: a nilpotent matrix ({@code [[0,1],[0,0]]}), whose {@code thisMatrix=I-A} is a
	 * defective Jordan block with eigenvalue exactly {@code 1} (the boundary of the series'
	 * convergence radius) -- its powers grow LINEARLY (not exponentially), so the error norm
	 * never shrinks (never converges) AND the deviation ratio hovers at {@code ~1.0} (never
	 * clearly {@code >1} by enough to make the {@code accumulator>500} check fire before
	 * {@code Complex.digits()}'s ~{@code 10^13} iterations -- confirmed hanging for minutes on a
	 * 2x2 matrix before this fix). See {@link #logTaylor}'s own Javadoc.
	 */
	private final static int LOG_TAYLOR_MAX_ITER = 10000;

	/**
	 * Calculates the logarithm of a Matrix using Taylor's Extension summation log(1 - x)
	 * <p>
	 * <b>KNOWN LIMITATION, root cause fixed, detection improved:</b> the series converges only
	 * when {@code I-this/||this||}'s spectral radius is {@code <1} -- for a matrix whose dominant
	 * eigenvalue orientation isn't close to {@code +||A||}, or (the case that used to hang, not
	 * just diverge) a defective/nilpotent structure putting that spectral radius exactly {@code
	 * =1}, the series does not converge. The existing deviation-based divergence check (comparing
	 * successive error norms) only catches EXPONENTIAL divergence quickly; a boundary case where
	 * the error norm simply fails to shrink (LINEAR, not exponential growth) can evade it for as
	 * long as {@link #LOG_TAYLOR_MAX_ITER} allows before this method now throws explicitly instead
	 * of running that many iterations.
	 * @param m The matrix.
	 * @return The logarithm of a Matrix using Taylor's Extension
	 * https://es.wikipedia.org/wiki/Logaritmo_de_una_matriz
	 * @throws IllegalArgumentException if the series diverges (existing check), or if it fails to
	 * converge within {@link #LOG_TAYLOR_MAX_ITER} iterations (new safety net for the boundary
	 * case the divergence check can miss).
	 */
	static MatrixComplex logTaylor(MatrixComplex m) {
		MatrixComplex.trace("------------ logtaylor() ------------ ");
		if (m.isNaN() || m.isNull() || m.isInfinite() ) return m;
		if (!m.isSquare()) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		MatrixComplex yMatrix = m.copy();

		/* ***************************************
		 * 1st Transformation
		 * Applying a reduction to reduce the
		 * overflow error risk
		* ***************************************/
		double factor = yMatrix.euc_norm();
		yMatrix = yMatrix.divides(factor);

		MatrixComplex.trace(yMatrix, "1st Transformation : Taylor's Extension log(1 - x) - yMatrix reduced by factor:" + factor);
		MatrixComplex.trace("yMatrix.euc_norm(): " + factor);

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

		MatrixComplex.trace(thisMatrix, "2nd Transformation : thisMatrix:");
		MatrixComplex.trace("thisMatrix.euc_norm(): " + thisMatrix.euc_norm());

		// These are the values for the 1st item of the summation. The first item of the expansion
		// In this case we use the summation opposite. At the end we need to return the opposite
		MatrixComplex logMatrix = thisMatrix.copy();
		MatrixComplex powMatrix = thisMatrix.copy();
		MatrixComplex errMatrix;
		MatrixComplex errAntMat = new MatrixComplex(m.rows(), m.cols());
		MatrixComplex logMatant;

		// precision can be changed with Complex.digits(long_value) -- capped at LOG_TAYLOR_MAX_ITER
		// regardless (see that constant's Javadoc for why Complex.digits() alone isn't safe here).
		long maxIter = Math.min(Complex.digits(), LOG_TAYLOR_MAX_ITER);
		long k = 2;
		boolean converged = false;

		// Variables to use at check convergence section
		int maxPoints = 99999;
		double[][] dataTable = new double[maxPoints][2];
		int c = 0;
		double accumulator = 0.0;
		Double deviation = 0.0;

		do {
			logMatant = logMatrix.copy();
			powMatrix.timesEq(thisMatrix);
			logMatrix.plusEq(powMatrix.divides(k));
			errMatrix = logMatant.minus(logMatrix);
			errMatrix.abs();
			if (errMatrix.isNullC()) { converged = true; break; }

			/*
			 * Check convergence section
			 */
			if ( k > 100 ) {

				/* * /
				logMatrix.println("- - - DEBUG · logMatrix:");
				if ( errMatrix.norm() > 2 ) {
					logMatrix.println("- - - DEBUG · logMatrix:");
					System.out.println("- - - DEBUG · errMatrix.norm():" + errMatrix.norm());
					System.out.println("- - - DEBUG · errAntMat.norm():" + errAntMat.norm());
				}
				/* */

				deviation = errMatrix.norm()/errAntMat.norm();
				accumulator += deviation > 1 ? deviation : 0.0;
				if (c < maxPoints) {
					dataTable[c][0] = k;
					dataTable[c++][1] = deviation;
				}
				/* */
				if (accumulator > 500) {
					/* * /
					if (MatrixComplex.debug()) {
						System.out.println("- - - DEBUG · Iteration:"+ k +" - The logarithm is divergent");
						System.out.println("- - - DEBUG · accumulator:" + accumulator);
						System.out.println("- - - DEBUG · last deviation:" + deviation);
						MatrixComplexPlot.doPlotSync("-- DEVIATION --", dataTable, --c);
					}
					/* */
					throw new IllegalArgumentException("logTaylor: The Taylor series log(1-x) is divergent for this matrix (its dominant eigenvalue is not close enough to +||A|| after the norm reduction).");
				}

				/* * /
				if (MatrixComplex.debug()) {
					if ( deviation > 0) System.out.println("- - - DEBUG · Iteration:"+ k);
					System.out.println("- - - DEBUG · deviation:" + deviation);
				}
				/* */

				/* */
			}
			errAntMat = errMatrix.copy();
			if ( deviation.isInfinite() ) { converged = true; break; }
			/*
			 * End of  Check convergence section
			 */
		} while(k++ < maxIter);

		if (!converged)
			throw new IllegalArgumentException("logTaylor: the Taylor series log(1-x) did not converge "
				+ "within " + maxIter + " iterations -- the matrix's dominant eigenvalue is likely "
				+ "exactly on (or very close to) the series' radius of convergence (e.g. a defective/"
				+ "nilpotent structure), a boundary case the deviation-based divergence check above can "
				+ "miss (grows too slowly -- linearly, not exponentially -- to trigger it in reasonable time).");

		/* * /
		if (MatrixComplex.debug()) {
			System.out.println("- - - DEBUG · Iterations to converge:" + k);
			System.out.println("- - - DEBUG · accumulator:" + accumulator);
			System.out.println("- - - DEBUG · last deviation:" + deviation);
			MatrixComplexPlot.doPlotSync("-- DEVIATION --", dataTable, --c);
		}
		/* */

		/* * /
		if (MatrixComplex.debug()) {
			logMatrix.println("--- CHECK logMatrix:");
			logMatrix.opposite().println("--- CHECK logMatrix.opposite():");
			logMatrix.opposite().plusMat(m,0).println("--- CHECK logMatrix.opposite().plusMat(m,0)");
		/* */

		return logMatrix.opposite().plusMat(Math.log(factor));
	}

	/**
	 * Hard cap on {@link #logMercator}'s iteration count, overriding {@code Complex.digits()} --
	 * same rationale and same value as {@link #LOG_TAYLOR_MAX_ITER}, applied here after this cap
	 * was found missing (confirmed hanging on the same boundary-case nilpotent matrix,
	 * {@code [[0,1],[0,0]]}, that {@link #logTaylor}'s own cap was added for) while extracting
	 * this class (Undecima sesion).
	 */
	private final static int LOG_MERCATOR_MAX_ITER = 10000;

	/**
	 * Calculates the logarithm of a Matrix using Mercator's Extension summation log(1 + x)
	 * @param m The matrix.
	 * @return The logarithm of a Matrix using Mercator's Extension
	 * @throws IllegalArgumentException if the series diverges (existing check), or if it fails to
	 * converge within {@link #LOG_MERCATOR_MAX_ITER} iterations (same boundary case as
	 * {@link #logTaylor}).
	 */
	static MatrixComplex logMercator(MatrixComplex m) {
		MatrixComplex.trace("------------ logMercator() ------------ ");
		if (m.isNaN() || m.isNull() || m.isInfinite() ) return m;
		if (!m.isSquare()) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		MatrixComplex yMatrix = m.copy();
		MatrixComplex.trace(yMatrix.determinant(), "[log()] - Determinant:");

		/* ***************************************
		 * 1st Transformation
		 * Applying a reduction to reduce the
		 * overflow error risk
		 * ***************************************/
		double factor = yMatrix.euc_norm();
		yMatrix = yMatrix.divides(factor);

		MatrixComplex.trace(yMatrix, "1st Transformation : Taylor's Extension log(1 - x) - yMatrix reduced by factor:" + factor);
		MatrixComplex.trace("yMatrix.euc_norm(): " + factor);

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

		MatrixComplex.trace(thisMatrix, "2nd Transformation : thisMatrix:");
		MatrixComplex.trace("thisMatrix.euc_norm(): " +thisMatrix.euc_norm());

		// These are the values for the 1st item of the summation
		MatrixComplex logMatrix = thisMatrix.copy();
		MatrixComplex powMatrix = thisMatrix.copy();
		MatrixComplex errMatrix;
		MatrixComplex errAntMat = new MatrixComplex(m.rows(), m.cols());
		MatrixComplex logMatant;

		// precision can be changed with Complex.digits(long_value) -- capped at
		// LOG_MERCATOR_MAX_ITER regardless (see that constant's Javadoc for why
		// Complex.digits() alone isn't safe here, same boundary case as logTaylor()).
		long maxIter = Math.min(Complex.digits(), LOG_MERCATOR_MAX_ITER);
		long k = 2;
		boolean converged = false;

		// Variables to use at check convergence section
		int maxPoints = 99999;
		double[][] dataTable = new double[maxPoints][2];
		int c = 0;
		double accumulator = 0.0;
		double deviation;

		do {
			logMatant = logMatrix.copy();
			powMatrix.timesEq(thisMatrix);
			logMatrix.plusEq(powMatrix.divides(k*(k%2 == 0 ? -1 : 1)));
			errMatrix = logMatant.minus(logMatrix);
			errMatrix.abs();
			if (errMatrix.isNullC()) { converged = true; break; }

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
					if (MatrixComplex.debug()) {
						MatrixComplex.trace("Iteration:"+ k +" - The logarithm is divergent");
						MatrixComplex.trace("accumulator:" + accumulator);
						MatrixComplexPlot.doPlotSync("-- DEVIATION --", dataTable, --c);
					}
					throw new IllegalArgumentException("logMercator: The Mercator series log(1+x) is divergent for this matrix (its dominant eigenvalue is not close enough to +||A|| after the norm reduction).");
				}
			}
			errAntMat = errMatrix.copy();
			/*
			 * End of Check convergence section
			 */
		} while(k++ < maxIter);

		if (!converged)
			throw new IllegalArgumentException("logMercator: the Mercator series log(1+x) did not converge "
				+ "within " + maxIter + " iterations -- the matrix's dominant eigenvalue is likely "
				+ "exactly on (or very close to) the series' radius of convergence (e.g. a defective/"
				+ "nilpotent structure), a boundary case the deviation-based divergence check above can "
				+ "miss (grows too slowly -- linearly, not exponentially -- to trigger it in reasonable time).");

		if (MatrixComplex.debug()) {
			MatrixComplex.trace("Iterations to converge:" + k);
			MatrixComplex.trace("accumulator:" + accumulator);
			MatrixComplexPlot.doPlotSync("-- DEVIATION --", dataTable, --c);
		}
		return logMatrix.plusMat(Math.log(factor));
	}

	/**
	 * Calculates the logarithm of a Matrix using Hyperbolic Arc Tangent's Extension summation.
	 * Not recommended to use
	 * @param m The matrix.
	 * @return The logarithm of a Matrix using Hyperbolic Arc Tangent's Extension
	 */
	static MatrixComplex logHat(MatrixComplex m) {
		MatrixComplex.trace("------------ loghat() ------------ ");
		if (m.isNaN() || m.isNull() || m.isInfinite() ) return m;
		if (m.rows() != m.cols()) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		MatrixComplex terMat = (m.power(2).minusMat(1,0)).divides(m.power(2).plusMat(1,0));
		MatrixComplex powMat = terMat.copy();
		MatrixComplex sumMat = powMat.copy();
		MatrixComplex sumAnt;
		MatrixComplex errMat;
		MatrixComplex errAnt = new MatrixComplex(m.rows(), m.cols());

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
			powMat.timesEq(terMat).timesEq(terMat);
			sumMat.plusEq(powMat.divides(2*k+1));
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
					if (MatrixComplex.debug()) {
						MatrixComplex.trace("Iteration:"+ k +" - The logarithm is divergent");
						MatrixComplex.trace("accumulator:" + accumulator);
						MatrixComplexPlot.doPlotSync("-- DEVIATION --", dataTable, --c);
					}
					throw new IllegalArgumentException("logHat: The Hyperbolic Arc Tangent series is divergent for this matrix.");
				}
			}

			errAnt = errMat.copy();
		} while(k++ < maxIter);
		if (MatrixComplex.debug()) {
			MatrixComplex.trace("Iterations to converge:" + k);
			MatrixComplex.trace("accumulator:" + accumulator);
			MatrixComplexPlot.doPlotSync("-- DEVIATION --", dataTable, --c);
		}
		return sumMat;
	}

	/**
	 * Calculates the principal square root {@code S} of an upper triangular matrix {@code T}
	 * ({@code S*S=T}, {@code S} also upper triangular) using the Parlett recurrence -- the
	 * standard, numerically stable method (Björck-Hammarling): unlike a generic matrix square
	 * root, a triangular one never needs an eigendecomposition of its own, since a triangular
	 * matrix's eigenvalues are already its diagonal entries.
	 * <p>
	 * Diagonal: {@code S_ii = Complex.sqrt(T_ii)} (principal branch, {@code arg(T_ii) ∈ (-π,π]}
	 * gives {@code arg(S_ii) ∈ (-π/2,π/2]} -- the standard convention this recurrence requires, no
	 * further branch choice needed once this is fixed). Off-diagonal, solved diagonal-by-diagonal
	 * (increasing {@code d=j-i}) from {@code S*S=T} entry by entry: {@code S_ij = (T_ij -
	 * Σ_{k=i+1}^{j-1} S_ik·S_kj) / (S_ii+S_jj)} -- by the time {@code S_ij} is computed, every
	 * {@code S_ik}/{@code S_kj} needed by the sum (strictly between {@code i} and {@code j}) has
	 * already been filled in, since those have a smaller {@code d}.
	 * <p>
	 * <b>KNOWN LIMITATION, irresoluble, now fails explicitly instead of NaN/timeout:</b> the
	 * recurrence divides by {@code S_ii+S_jj}, which is (numerically) zero only in one genuine
	 * case under this project's principal-branch convention: two DIAGONAL entries of {@code T}
	 * that are BOTH exactly zero and adjacent within the same Jordan chain (a repeated zero
	 * eigenvalue with a nontrivial nilpotent block). Verified empirically that "two eigenvalues
	 * that are each other's negative" -- the case one would expect from the generic
	 * Parlett-recurrence literature -- does NOT actually trigger this here: {@code Complex.sqrt()}'s
	 * principal branch always has {@code Re>=0} (confirmed with {@code T=diag(4,-4)}: {@code S_00=2},
	 * {@code S_11=2i}, sum {@code =2+2i}, nowhere near zero), so {@code S_ii+S_jj} can only vanish
	 * when both terms are individually zero. A nontrivial nilpotent Jordan block genuinely has NO
	 * square root at all -- triangular or otherwise, by ANY method, not just this recurrence (a
	 * mathematical fact: {@code sqrt} isn't differentiable at {@code 0}, so the usual Jordan-chain
	 * function calculus that defines {@code f(J)} for an analytic {@code f} simply doesn't apply
	 * here). This is not a gap to fill with a smarter algorithm (e.g. block Schur-Parlett, which
	 * only helps for REPEATED NONZERO eigenvalues, where {@code sqrt} is analytic) -- a
	 * mathematically honest {@code sqrtTriangular} cannot return a value for this input. Confirmed
	 * this case is unreachable except when the ORIGINAL matrix already has an exact zero eigenvalue
	 * with geometric deficiency (singular AND defective at {@code 0}) -- repeated square-rooting
	 * never creates a zero eigenvalue that wasn't already there. Since {@code e^L} can never have
	 * eigenvalue {@code 0}, {@code log} of such a matrix does not exist either, so failing here is
	 * the mathematically correct outcome, not a missing feature. Now throws explicitly with a
	 * specific message the moment the degenerate division is about to happen, instead of silently
	 * producing {@code NaN}/{@code Infinity} that used to propagate through {@link #logm}'s scaling
	 * loop until its generic 100-iteration safety cap gave a much less specific error.
	 * @param tMat An upper triangular matrix (trusted as such by the caller -- not re-verified
	 * here, see {@link #logm}, the only caller, which always passes a genuine Schur factor).
	 * @return The principal square root of tMat, upper triangular.
	 * @throws IllegalArgumentException if tMat has a repeated zero eigenvalue inside a nontrivial
	 * Jordan chain -- see above, no triangular square root exists for that input.
	 */
	private static MatrixComplex sqrtTriangular(MatrixComplex tMat) {
		int n = tMat.rows();
		MatrixComplex sMat = new MatrixComplex(n, n);
		for (int i = 0; i < n; ++i) {
			sMat.setItem(i, i, Complex.sqrt(tMat.getItem(i, i)));
		}
		for (int d = 1; d < n; ++d) {
			for (int i = 0; i < n - d; ++i) {
				int j = i + d;
				// Private zero-valued accumulator, NOT Complex.ZERO -- plusEq() mutates its
				// receiver in place, and calling it directly on the shared ZERO constant (as a
				// naive sum=Complex.ZERO; sum.plusEq(...) rewrite would) would corrupt it for
				// every other caller in the JVM. Same accumulator idiom as MatrixComplex.times().
				Complex sum = new Complex();
				for (int k = i + 1; k < j; ++k) {
					sum.plusEq(sMat.getItem(i, k).times(sMat.getItem(k, j)));
				}
				Complex numerator = tMat.getItem(i, j).minus(sum);
				Complex denominator = sMat.getItem(i, i).plus(sMat.getItem(j, j));
				if (denominator.mod() < Complex.zero_threshold_approx()) {
					throw new IllegalArgumentException("sqrtTriangular: S_" + i + i + "+S_" + j + j
						+ " is (numerically) zero -- this means T has a repeated ZERO eigenvalue "
						+ "inside a nontrivial Jordan chain (a genuine nilpotent block of size>=2), "
						+ "which has NO triangular square root at all, for any method (a mathematical "
						+ "fact, not a numerical shortcoming of this recurrence) -- and since e^L can "
						+ "never have eigenvalue 0, the matrix logarithm of the original matrix does "
						+ "not exist either.");
				}
				sMat.setItem(i, j, numerator.divides(denominator));
			}
		}
		return sMat;
	}

	/**
	 * Returns true if every diagonal entry of tMat is within threshold of 1 (in modulus).
	 * Private helper for {@link #logm}'s scaling loop -- since tMat is triangular by
	 * construction there, its diagonal entries ARE its eigenvalues, no recomputation needed.
	 */
	private static boolean isNearIdentityDiagonal(MatrixComplex tMat, double threshold) {
		for (int i = 0; i < tMat.rows(); ++i) {
			if (tMat.getItem(i, i).minus(Complex.ONE).mod() >= threshold) return false;
		}
		return true;
	}

	/**
	 * Calculates the principal natural logarithm of a (possibly defective, non-diagonalizable)
	 * matrix via Schur factorization plus inverse scaling-and-squaring -- the standard method
	 * (MATLAB {@code logm}, Higham): {@code A=U·T·U*} (Schur, {@code T} upper triangular);
	 * repeatedly take the principal square root of {@code T} ({@link
	 * #sqrtTriangular(MatrixComplex)}) until its diagonal is close enough to {@code 1} that
	 * Mercator's series {@code log(I+x)} converges quickly and safely (the same series already
	 * used by {@link #logMercator}, reused here WITHOUT its own {@code euc_norm()}-based
	 * reduction/compensation -- {@code x} is already small by construction from the scaling);
	 * then undo the scaling ({@code log(T)=2^s·log(S)}) and the Schur similarity
	 * ({@code log(A)=U·log(T)·U*}, using {@code adjoint()} rather than {@code inverse()} since
	 * {@code U} is unitary -- the same pattern {@code Schurfactor#factorize()} itself already
	 * uses, exact and free of Gaussian-elimination noise).
	 * <p>
	 * Unlike {@link #log} -- which already handles the diagonal and diagonalizable cases
	 * correctly, falling back to {@link #logTaylor} only for the remaining defective case, where
	 * it only converges for a narrow range of dominant-eigenvalue orientations (close to
	 * {@code +‖A‖}) -- this method targets exactly the gap: genuinely defective matrices (at least
	 * one nontrivial Jordan block), for any eigenvalue orientation.
	 * <p>
	 * Inherits the same numerical-precision caveat already characterized while fixing
	 * {@code Jordan.java}/{@code TestJordanAudit01}: {@link Schurfactor} recomputes
	 * {@code Eigenspace}/Durand-Kerner at every step of its own recursion, so {@code T}'s diagonal
	 * entries carry whatever residual imprecision that root-finder leaves on them -- for a
	 * defective eigenvalue this CAN be amplified through this method's own near-singular Parlett
	 * divisions, same mechanism as diagnosed there.
	 * @param m The matrix.
	 * @return The principal natural logarithm of this matrix.
	 * @throws IllegalArgumentException if this matrix is not square, if a Schur factorization
	 * could not be found, if the scaling loop doesn't reach a near-identity diagonal within a
	 * generous safety cap, or if the Mercator series fails to converge even after scaling (would
	 * indicate a deeper problem, since scaling is specifically meant to guarantee convergence).
	 */
	static MatrixComplex logm(MatrixComplex m) {
		MatrixComplex.trace("------------ logm() ------------ ");
		if (m.isNaN() || m.isNull() || m.isInfinite()) return m;
		if (!m.isSquare()) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		Schurfactor schur = new Schurfactor(m);
		if (!schur.factorized()) {
			throw new IllegalArgumentException("logm: could not compute a Schur factorization for this matrix.");
		}
		MatrixComplex uMat = schur.getU();
		MatrixComplex tMat = schur.getSchur();

		/* ***************************************
		 * Inverse scaling: repeated principal square roots until T's diagonal is close enough to
		 * 1 for Mercator's series to converge quickly, safely inside its |x|<1 radius with margin.
		 * *************************************** */
		final double NEAR_IDENTITY_THRESHOLD = 0.5;
		final int MAX_SQRT_ITER = 100;
		int s = 0;
		while (!isNearIdentityDiagonal(tMat, NEAR_IDENTITY_THRESHOLD)) {
			if (s >= MAX_SQRT_ITER) {
				throw new IllegalArgumentException("logm: scaling did not reach a near-identity diagonal after "
					+ MAX_SQRT_ITER + " square roots -- matrix may be singular or otherwise pathological.");
			}
			tMat = sqrtTriangular(tMat);
			++s;
		}
		MatrixComplex.trace("logm(): scaling steps s=" + s);

		/* *************************************************
		 * Mercator's Extension log(1 + x), reused from logMercator()'s convergence loop verbatim
		 * -- but WITHOUT its euc_norm() reduction/compensation, since x is already small here.
		 * (I + xMat) = tMat -> xMat = tMat - I
		 * *************************************************/
		MatrixComplex xMat = tMat.minusMat(1, 0);

		MatrixComplex logMatrix = xMat.copy();
		MatrixComplex powMatrix = xMat.copy();
		MatrixComplex errMatrix;
		MatrixComplex errAntMat = new MatrixComplex(m.rows(), m.cols());
		MatrixComplex logMatant;

		long maxIter = Complex.digits();
		long k = 2;

		int maxPoints = 99999;
		double[][] dataTable = new double[maxPoints][2];
		int c = 0;
		double accumulator = 0.0;
		double deviation;

		do {
			logMatant = logMatrix.copy();
			powMatrix.timesEq(xMat);
			logMatrix.plusEq(powMatrix.divides(k * (k % 2 == 0 ? -1 : 1)));
			errMatrix = logMatant.minus(logMatrix);
			errMatrix.abs();
			if (errMatrix.isNullC()) break;

			if (k > 100) {
				deviation = errMatrix.norm() / errAntMat.norm();
				accumulator += deviation > 1 ? deviation : 0.0;
				if (c < maxPoints) {
					dataTable[c][0] = k;
					dataTable[c++][1] = deviation;
				}
				if (accumulator > 500) {
					if (MatrixComplex.debug()) {
						MatrixComplex.trace("logm(): Mercator series iteration:" + k + " - unexpectedly divergent after scaling");
						MatrixComplex.trace("accumulator:" + accumulator);
						MatrixComplexPlot.doPlotSync("-- DEVIATION --", dataTable, --c);
					}
					throw new IllegalArgumentException(
						"logm: the Mercator series failed to converge even after scaling close to identity.");
				}
			}
			errAntMat = errMatrix.copy();
		} while (k++ < maxIter);
		if (MatrixComplex.debug()) {
			MatrixComplex.trace("logm(): Mercator iterations to converge:" + k);
			MatrixComplex.trace("accumulator:" + accumulator);
			MatrixComplexPlot.doPlotSync("-- DEVIATION --", dataTable, --c);
		}

		/* undo the scaling: log(T) = 2^s * log(S) */
		MatrixComplex logT = logMatrix.times(Math.pow(2, s));

		/* undo the Schur similarity: log(A) = U * log(T) * U* (U unitary: adjoint()==inverse() exactly) */
		return uMat.times(logT).times(uMat.adjoint());
	}

	/**
	 * Shortcut to the preferred natural logarithm expansion.
	 * <p>
	 * For the defective (non-diagonalizable) case, tries {@link #logm} (Schur factorization +
	 * inverse scaling-and-squaring, converges for any eigenvalue orientation) first, falling back
	 * to {@link #logTaylor} only if {@code logm()} throws explicitly -- same "try the general
	 * method, fall back to the narrower one only on explicit failure" pattern already used by
	 * {@code Polynom.solveRobust()}/{@code Eigenspace.eigenval()}. {@code logTaylor()} alone only
	 * converges for a narrow range of dominant-eigenvalue orientations (close to {@code +‖A‖}) --
	 * confirmed real case: {@code [[-52,4],[-1,-48]]} (a defective 2x2 block, {@code lambda=-50},
	 * conjugated by a non-orthogonal {@code P}) used to make {@code log()} throw outright;
	 * {@code logm()} resolves it cleanly ({@code exp(logm(A))} matches {@code A} to {@code ~1.5e-9}).
	 * @param m The matrix.
	 * @return the natural logarithm of the matrix
	 */
	static MatrixComplex log(MatrixComplex m) {
		// Take advantage from diagonal matrices
		if (m.isDiagonal()) {
    		MatrixComplex.trace("Log() of diagonal matrix");
    		MatrixComplex powerMat = m.copy();
			for (int i = 0; i < m.rows(); ++i)
				powerMat.setItem(i, i, Complex.log(m.getItem(i, i)));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(m);
    	if (dmat.isDiagonalizable()) {
			MatrixComplex.trace("Log() using diagonalization P·D·P⁻¹");
        	MatrixComplex.trace(dmat.P(), "Matrix P");
        	MatrixComplex.trace(dmat.D(), "Matrix D");

        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i)
        		Dmat.setItem(i, i, Complex.log(Dmat.getItem(i, i)));
			MatrixComplex.trace(Dmat, "Dmat");
        	MatrixComplex.trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Log() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}

    	// Defective (non-diagonalizable): try logm() (Schur + scaling-and-squaring, handles any
		// eigenvalue orientation) first, fall back to the narrower Taylor expansion only if it
		// throws explicitly.
		try {
			MatrixComplex.trace("Log() using logm() (Schur factorization + inverse scaling-and-squaring)");
			return logm(m);
		} catch (IllegalArgumentException e) {
			MatrixComplex.trace("Log() logm() failed (" + e.getMessage() + "), falling back to the Taylor expansion");
			return logTaylor(m);
		}
	}

	/**
	 * Calculates the log item to item of this matrix
	 * @param m The matrix.
	 * @return The log item to item of this matrix
	 */
	static MatrixComplex llog(MatrixComplex m) {
		MatrixComplex logMat = new MatrixComplex(m.rows(), m.cols());

		for (int row = 0; row < logMat.rows(); ++row)
			for (int col = 0; col < logMat.cols(); ++col)
				logMat.setItem(row, col, Complex.log(m.getItem(row, col)));
		return logMat;
	}

	/**
	 * Calculates the natural log in base 10 of the matrix log10(matrix)
	 * @param m The matrix.
	 * @return
	 */
	static MatrixComplex log10(MatrixComplex m) {
		return log(m).divides(MatrixComplex.__log10__);
	}

	/**
	 * Calculates the log10 item to item of this matrix
	 * @param m The matrix.
	 * @return The log  item to item of this matrix
	 */
	static MatrixComplex llog10(MatrixComplex m) {
		MatrixComplex logMat = new MatrixComplex(m.rows(), m.cols());

		for (int row = 0; row < logMat.rows(); ++row)
			for (int col = 0; col < logMat.cols(); ++col)
				logMat.setItem(row, col, Complex.log(m.getItem(row, col)).divides(MatrixComplex.__log10__));
		return logMat;
	}

	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param m The matrix.
	 * @param base
	 * @return
	 */
	static MatrixComplex logbase(MatrixComplex m, Complex base) {
		return log(m).divides(Complex.log(base));
	}

	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param m The matrix.
	 * @param base
	 * @return
	 */
	static MatrixComplex llogbase(MatrixComplex m, Complex base) {
		MatrixComplex logMat = new MatrixComplex(m.rows(), m.cols());

		for (int row = 0; row < logMat.rows(); ++row)
			for (int col = 0; col < logMat.cols(); ++col)
				logMat.setItem(row, col, Complex.log(m.getItem(row, col)).divides(Complex.log(base)));
		return logMat;
	}

	/**
	 * Calculates the natural log in Real base "base" of the matrix logbase(matrix)
	 * @param m The matrix.
	 * @param base
	 * @return
	 */
	static MatrixComplex logbase(MatrixComplex m, double base) {
		return log(m).divides(Math.log(base));
	}

	/**
	 * Calculates the natural log in Real base "base" of the matrix logbase(matrix)
	 * @param m The matrix.
	 * @param base
	 * @return
	 */
	static MatrixComplex llogbase(MatrixComplex m, double base) {
		Complex cBase = new Complex(base);
		return llogbase(m, cBase);
	}

	/**
	 * Calculates the log in complex matrix base "baseMat" of the matrix mat
	 * @param m The matrix.
	 * @param baseMat
	 * @return
	 */
	static MatrixComplex logbase(MatrixComplex m, MatrixComplex baseMat) {
		return log(m).divides(log(baseMat));
	}

}
