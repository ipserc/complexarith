package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ScratchTaylorMercatorBench01 {
	static final int N = 8;
	static final int WARMUP = 3;
	static final int REPS = 15;

	public static void main(String[] args) {
		Complex.digits(2000L);
		MatrixComplex.debugOFF();

		MatrixComplex general = buildFixed(N, 0.22, false);
		MatrixComplex nearIdentity = buildFixed(N, 0.03, true);

		// logm() needs a matrix its Schurfactor can actually decompose. Discovered mid-session
		// (ScratchLogmIsolationCheck01/02.java) that EVERY sin/cos-generated or hand-picked
		// near-identity 3x3 tried here -- including the one used in this session's Fase 2/4
		// correctness drivers, which had looked fine only because REF and MOD failed identically
		// the same way -- hits a pre-existing Schurfactor limitation confirmed present already in
		// the pre-session baseline (commit 894ec62), unrelated to any change in this session; see
		// TestLogmAudit01.java's own "KNOWN LIMITATION" note. Reusing that test's known-good
		// "diagonalizable 3x3" matrix instead. logm() handles arbitrary-norm input itself via
		// inverse scaling-and-squaring (sqrtTriangular() loop) before the Mercator series, so it
		// doesn't need to start near-identity like logTaylor/logMercator/logHat do.
		MatrixComplex nearIdentitySmall = new MatrixComplex("2,0,0;0,3,4;0,4,9");

		bench("exp_", general, m -> m.exp_());
		bench("sinTaylor", general, m -> m.sinTaylor());
		bench("cosTaylor", general, m -> m.cosTaylor());
		bench("sinhTaylor", general, m -> m.sinhTaylor());
		bench("coshTaylor", general, m -> m.coshTaylor());
		bench("logTaylor", nearIdentity, m -> m.logTaylor());
		bench("logMercator", nearIdentity, m -> m.logMercator());
		bench("logHat", nearIdentity, m -> m.logHat());
		bench("logm", nearIdentitySmall, m -> m.logm());
	}

	static MatrixComplex buildFixed(int n, double scale, boolean nearIdentity) {
		MatrixComplex m = new MatrixComplex(n, n);
		for (int r = 0; r < n; r++) {
			for (int c = 0; c < n; c++) {
				double re = scale * Math.sin(1.3 * r + 0.7 * c + 1);
				double im = scale * Math.cos(0.9 * r - 1.1 * c + 0.5);
				if (nearIdentity && r == c) re += 1.0;
				m.setItem(r, c, new Complex(re, im));
			}
		}
		return m;
	}

	interface Fn { MatrixComplex apply(MatrixComplex m); }

	static void bench(String label, MatrixComplex m, Fn fn) {
		for (int i = 0; i < WARMUP; i++) fn.apply(m);
		long totalNanos = 0;
		for (int i = 0; i < REPS; i++) {
			long t0 = System.nanoTime();
			fn.apply(m);
			totalNanos += System.nanoTime() - t0;
		}
		double avgMs = totalNanos / (double) REPS / 1_000_000.0;
		System.out.println(label + ": avg=" + String.format("%.3f", avgMs) + "ms over " + REPS + " reps (N=" + N + ")");
	}
}
