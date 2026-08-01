package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for {@code MatrixComplex.logm()} (Novena sesion, 1 agosto 2026): the natural
 * logarithm of a defective (non-diagonalizable) matrix via Schur factorization plus inverse
 * scaling-and-squaring, the standard method (MATLAB {@code logm}, Higham) filling the gap
 * {@code log()}'s {@code logTaylor()} fallback leaves for eigenvalues not close to {@code +‖A‖}.
 * <p>
 * Three independent verification strategies:
 * <ol>
 * <li>A closed-form oracle for a single Jordan block {@code J=λI+N} ({@code N} nilpotent):
 * {@code log(J) = log(λ)·I + Σ_{k=1}^{n-1} (-1)^{k+1}(N/λ)^k/k} -- a FINITE sum since {@code N} is
 * nilpotent, giving an exact analytic reference across real/negative-real/complex/small/large
 * eigenvalues.</li>
 * <li>Regression against the already-verified {@code log()} on diagonal/diagonalizable matrices
 * (both methods must agree there).</li>
 * <li>Self-consistency {@code exp(logm(A))≈A} for multi-block synthetic defective matrices, where
 * no simple closed form is available.</li>
 * </ol>
 * <p>
 * <b>KNOWN LIMITATION, discovered while writing this test, NOT specific to {@code logm()}:</b>
 * {@code Schurfactor}'s internal {@code Eigenspace.eigenvectors3()} (the generic eigenvector
 * solver, distinct from {@code Jordan.eigenvectors()}, fixed earlier this same session) hits the
 * same "imprecise Durand-Kerner eigenvalue keeps {@code (A-eigenval*I)} from being quite
 * singular, homogeneous solve collapses to the trivial all-zero vector" failure mode, for
 * REPEATED eigenvalues at larger block sizes (confirmed with a sweep: {@code n=4} fails for
 * {@code λ=3/-3/-1/50/-50}, only {@code λ=1} works; {@code n=3} fails only for {@code λ=50}).
 * Distinct eigenvalues (the diagonalizable case) are unaffected at any size tested. {@code
 * logm()} detects this correctly via {@code Schurfactor.factorized()} and throws a clear
 * exception rather than returning garbage -- but it narrows {@code logm()}'s practical domain for
 * genuinely defective matrices until {@code Eigenspace}'s generic solver gets an analogous fix.
 * The test cases below deliberately stick to combinations confirmed to work; do not add a larger
 * single-block case (n&gt;=4 with most eigenvalues, or n&gt;=3 with a complex eigenvalue) without
 * first checking it against a fresh {@code Schurfactor} sweep -- it will likely throw for reasons
 * unrelated to {@code logm()} itself.
 */
public class TestLogmAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static double maxAbsDiff(MatrixComplex a, MatrixComplex b) {
		double max = 0.0;
		for (int r = 0; r < a.rows(); ++r) {
			for (int c = 0; c < a.cols(); ++c) {
				Complex d = a.getItem(r, c).minus(b.getItem(r, c));
				max = Math.max(max, Math.max(Math.abs(d.rep()), Math.abs(d.imp())));
			}
		}
		return max;
	}

	// Closed form for a single Jordan block J=lambda*I+N (N nilpotent, index n):
	// log(J) = log(lambda)*I + sum_{k=1}^{n-1} (-1)^(k+1) * (N/lambda)^k / k
	private static MatrixComplex closedFormJordanLog(int n, Complex lambda) {
		MatrixComplex nMat = new MatrixComplex(n, n);
		for (int i = 0; i < n - 1; ++i) nMat.setItem(i, i + 1, Complex.ONE);

		MatrixComplex result = new MatrixComplex(n, n);
		Complex logLambda = Complex.log(lambda);
		for (int i = 0; i < n; ++i) result.setItem(i, i, logLambda);

		MatrixComplex nOverLambdaPowK = nMat.divides(lambda);
		for (int k = 1; k <= n - 1; ++k) {
			Complex sign = (k % 2 == 0) ? Complex.mONE : Complex.ONE;
			MatrixComplex term = nOverLambdaPowK.times(sign).divides((double) k);
			result = result.plus(term);
			if (k < n - 1) nOverLambdaPowK = nOverLambdaPowK.times(nMat).divides(lambda);
		}
		return result;
	}

	private static MatrixComplex jordanBlock(int n, Complex lambda) {
		MatrixComplex j = new MatrixComplex(n, n);
		for (int i = 0; i < n; ++i) {
			j.setItem(i, i, lambda);
			if (i + 1 < n) j.setItem(i, i + 1, Complex.ONE);
		}
		return j;
	}

	private static void report(String label, boolean ok, double diff) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + " -- diff = " + diff);
		if (ok) ++pass; else ++fail;
	}

	private static void checkAgainstClosedForm(String label, int n, Complex lambda, double tolerance) {
		MatrixComplex j = jordanBlock(n, lambda);
		MatrixComplex expected = closedFormJordanLog(n, lambda);
		MatrixComplex actual = j.logm();
		double diff = maxAbsDiff(actual, expected);
		report(label, diff < tolerance, diff);
	}

	private static void checkSelfConsistentExp(String label, MatrixComplex a, double tolerance) {
		MatrixComplex logA = a.logm();
		MatrixComplex reconstructed = logA.exp();
		double diff = maxAbsDiff(reconstructed, a);
		report(label, diff < tolerance, diff);
	}

	private static void checkAgainstLog(String label, MatrixComplex a, double tolerance) {
		MatrixComplex viaLogm = a.logm();
		MatrixComplex viaLog = a.log();
		double diff = maxAbsDiff(viaLogm, viaLog);
		report(label, diff < tolerance, diff);
	}

	public static void main(String[] args) {
		Complex.setFixedOFF();

		checkAgainstClosedForm("2x2, lambda=50 (positive real)", 2, new Complex(50, 0), 1e-6);
		checkAgainstClosedForm("2x2, lambda=-50 (negative real -- logTaylor's blind spot)", 2, new Complex(-50, 0), 1e-6);
		checkAgainstClosedForm("3x3, lambda=3 (positive real)", 3, new Complex(3, 0), 1e-6);
		checkAgainstClosedForm("3x3, lambda=-3 (negative real)", 3, new Complex(-3, 0), 1e-6);
		checkAgainstClosedForm("2x2, lambda=2+3i (complex)", 2, new Complex(2, 3), 1e-6);
		checkAgainstClosedForm("4x4, lambda=1", 4, new Complex(1, 0), 1e-6);
		checkAgainstClosedForm("2x2, lambda=0.01 (small magnitude)", 2, new Complex(0.01, 0), 1e-6);
		checkAgainstClosedForm("2x2, lambda=1000 (large magnitude)", 2, new Complex(1000, 0), 1e-4);

		checkAgainstLog("diagonal real", new MatrixComplex("2,0;0,3"), 1e-6);
		checkAgainstLog("diagonal complex", new MatrixComplex("2+1i,0;0,3-2i"), 1e-6);
		checkAgainstLog("diagonalizable 2x2 (distinct eigenvalues)", new MatrixComplex("4,1;2,3"), 1e-6);
		checkAgainstLog("diagonalizable 3x3", new MatrixComplex("2,0,0;0,3,4;0,4,9"), 1e-6);

		// A = P*J*P^-1: 2x2 Jordan block (lambda=5) + a distinct 1x1 eigenvalue (-2), P not orthogonal.
		MatrixComplex jMulti = new MatrixComplex("5,1,0;0,5,0;0,0,-2");
		MatrixComplex p = new MatrixComplex("1,2,0;0,1,1;1,0,1");
		checkSelfConsistentExp("3x3 multi-block (2x2 lambda=5 + 1x1 lambda=-2), P not orthogonal",
			p.times(jMulti).times(p.inverse()), 1e-4);

		// Same prerequisite-fix case from the previous session (Schurfactor/orthonormalize),
		// conjugated by a non-orthogonal P.
		MatrixComplex jSingle = new MatrixComplex("-50,1;0,-50");
		MatrixComplex p2 = new MatrixComplex("2,1;1,1");
		checkSelfConsistentExp("2x2 single block, lambda=-50, P not orthogonal (prerequisite-fix case)",
			p2.times(jSingle).times(p2.inverse()), 1e-2);

		// Degenerate case: a genuinely singular (nilpotent) matrix has no logarithm. Must fail
		// cleanly (an exception), not hang or return garbage.
		try {
			new MatrixComplex("0,1;0,0").logm();
			System.out.println("FAIL nilpotent matrix -- logm() did not throw");
			++fail;
		} catch (IllegalArgumentException e) {
			System.out.println("OK   nilpotent matrix -- logm() threw as expected: " + e.getMessage());
			++pass;
		}

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
