package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for {@code MatrixComplex.log()}'s dispatcher (ver {@code MatrixComplex.VERSION}
 * 1.35 / {@code Claude/ComplexArithRev.md}): the defective (non-diagonalizable) branch now tries
 * {@code logm()} (Schur factorization + inverse scaling-and-squaring, any eigenvalue orientation)
 * before falling back to {@code logTaylor()} (narrow convergence range, close to {@code +||A||}).
 * <p>
 * Three kinds of checks:
 * <ol>
 * <li>Diagonal and diagonalizable matrices: {@code log()} must be UNCHANGED (those branches were
 * not touched) -- compared against a fresh reference build from before this fix.</li>
 * <li>Defective matrices whose dominant eigenvalue is NOT close to {@code +||A||} (logTaylor()'s
 * blind spot): {@code log()} must now succeed (via {@code logm()}) instead of throwing --
 * confirmed real case: a defective 2x2 block ({@code lambda=-50}, P not orthogonal) that used to
 * make {@code log()} throw outright.</li>
 * </ol>
 * <b>Deliberately NOT tested here:</b> the genuinely-unrecoverable (nilpotent) case. {@code
 * logm()} itself already handles it correctly (throws cleanly, verified in {@code
 * TestLogmAudit01}) -- but {@code log()}'s dispatch falls back to {@code logTaylor()} whenever
 * {@code logm()} throws, and {@code logTaylor()} was found, DURING this fix, to hang (not throw,
 * not terminate within 15s) for a nilpotent matrix. Confirmed PRE-EXISTING and unrelated to this
 * fix: calling {@code logTaylor()} directly on a fresh build from before this change hangs
 * identically -- {@code log()}'s dispatch reached {@code logTaylor()} for this exact input before
 * this fix too (neither diagonal nor diagonalizable), so the exposure is unchanged, just newly
 * discovered while writing this test. Documented as a separate, deferred finding (see {@code
 * Claude/ComplexArithRev.md}), not fixed here -- exercising it in an automated test would make
 * this suite hang for whoever runs it.
 */
public class TestLogDispatch01 {

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

	private static void checkSelfConsistentExp(String label, MatrixComplex a, double tolerance) {
		try {
			MatrixComplex logA = a.log();
			MatrixComplex reconstructed = logA.exp();
			double diff = maxAbsDiff(reconstructed, a);
			report(label, diff < tolerance, "diff=" + diff);
		} catch (Exception e) {
			report(label, false, "threw: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		Complex.setFixedOFF();

		// Diagonal (unchanged branch).
		checkSelfConsistentExp("diagonal real", new MatrixComplex("2,0;0,3"), 1e-9);
		checkSelfConsistentExp("diagonal complex", new MatrixComplex("2+1i,0;0,3-2i"), 1e-9);

		// Diagonalizable, distinct eigenvalues (unchanged branch).
		checkSelfConsistentExp("diagonalizable 2x2", new MatrixComplex("4,1;2,3"), 1e-9);
		checkSelfConsistentExp("diagonalizable 3x3", new MatrixComplex("2,0,0;0,3,4;0,4,9"), 1e-9);

		// Defective, dominant eigenvalue NOT close to +||A|| -- logTaylor()'s blind spot,
		// confirmed to throw before this fix; must now succeed via logm().
		MatrixComplex jSingle = new MatrixComplex("-50,1;0,-50");
		MatrixComplex p2 = new MatrixComplex("2,1;1,1");
		checkSelfConsistentExp("defective 2x2, lambda=-50, P not orthogonal (used to throw)",
			p2.times(jSingle).times(p2.inverse()), 1e-2);

		MatrixComplex jMulti = new MatrixComplex("5,1,0;0,5,0;0,0,-2");
		MatrixComplex pMulti = new MatrixComplex("1,2,0;0,1,1;1,0,1");
		checkSelfConsistentExp("defective 3x3 multi-block (2x2 lambda=5 + 1x1 lambda=-2), P not orthogonal",
			pMulti.times(jMulti).times(pMulti.inverse()), 1e-4);

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}

}
