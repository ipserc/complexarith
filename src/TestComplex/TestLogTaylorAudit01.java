package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for {@code MatrixComplex.logTaylor()}'s hang fix (ver {@code
 * MatrixComplex.LOG_TAYLOR_MAX_ITER} / {@code Claude/ComplexArithRev.md}), found while connecting
 * {@code logm()} into {@code log()}'s dispatcher: {@code logTaylor()} could run for as long as
 * {@code Complex.digits()} allows ({@code 10^precision}, astronomically large, meant as an
 * effectively-unreachable formality) whenever the series' error norm fails to shrink WITHOUT
 * growing exponentially -- the existing deviation-based divergence check (an accumulator of
 * {@code errNorm_new/errNorm_old > 1}) only catches exponential divergence quickly; a boundary
 * case (dominant eigenvalue of {@code I-this/||this||} exactly on the radius of convergence, e.g.
 * a defective/nilpotent Jordan structure) makes the error norm plateau near a constant instead,
 * so the deviation ratio hovers at {@code ~1.0} and the accumulator creeps up too slowly to ever
 * trigger. Confirmed hanging for minutes on a plain 2x2 nilpotent matrix before this fix.
 * <p>
 * Three checks:
 * <ol>
 * <li>The nilpotent case: must now throw quickly (a few seconds at most, in practice
 * milliseconds), with the NEW "did not converge within N iterations" message.</li>
 * <li>A genuinely convergent case: unaffected, still gives the correct result (the new iteration
 * cap, 10000, is never reached by a real convergent series -- those converge within dozens to a
 * few hundred iterations).</li>
 * <li>A genuinely (exponentially) divergent case: still caught by the EXISTING deviation-based
 * check, with its own pre-existing message -- confirms this fix didn't touch that path.</li>
 * </ol>
 */
public class TestLogTaylorAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void report(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + " -- " + detail);
		if (ok) ++pass; else ++fail;
	}

	private static double maxAbsDiff(MatrixComplex a, MatrixComplex b) {
		double max = 0.0;
		for (int r = 0; r < a.rows(); ++r)
			for (int c = 0; c < a.cols(); ++c) {
				Complex d = a.getItem(r, c).minus(b.getItem(r, c));
				max = Math.max(max, Math.max(Math.abs(d.rep()), Math.abs(d.imp())));
			}
		return max;
	}

	public static void main(String[] args) {
		Complex.setFixedOFF();

		// 1) Nilpotent: dominant eigenvalue of I-this/||this|| exactly on the radius of
		// convergence -- must throw quickly with the NEW "did not converge" message, not hang.
		long t0 = System.currentTimeMillis();
		try {
			new MatrixComplex("0,1;0,0").logTaylor();
			report("nilpotent matrix must throw", false, "did not throw");
		} catch (IllegalArgumentException e) {
			long elapsed = System.currentTimeMillis() - t0;
			boolean ok = elapsed < 5000 && e.getMessage().contains("did not converge");
			report("nilpotent matrix throws quickly with the new message", ok,
				"elapsed=" + elapsed + "ms, threw: " + e.getMessage());
		}

		// 2) Genuinely convergent case: unaffected by the new cap.
		try {
			MatrixComplex a = new MatrixComplex("2,0;0,3");
			MatrixComplex logA = a.logTaylor();
			MatrixComplex reconstructed = logA.exp();
			double diff = maxAbsDiff(reconstructed, a);
			report("convergent case (diagonal 2x2) unaffected", diff < 1e-9, "diff=" + diff);
		} catch (Exception e) {
			report("convergent case (diagonal 2x2) unaffected", false, "threw: " + e.getMessage());
		}

		// 3) Genuinely (exponentially) divergent case: still caught by the pre-existing check.
		try {
			MatrixComplex a = new MatrixComplex("1,0;0,-1000");
			a.logTaylor();
			report("exponentially divergent case still throws", false, "did not throw");
		} catch (IllegalArgumentException e) {
			boolean ok = e.getMessage().contains("is divergent");
			report("exponentially divergent case still throws (pre-existing check)", ok, e.getMessage());
		}

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}

}
