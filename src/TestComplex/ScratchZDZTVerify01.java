package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Fourier;
import com.ipserc.arith.signal.Z;

/**
 * Quick sanity verification for the Z.java rewrite (Decimoctava sesion, continuacion, ver
 * ComplexArithRev.md): (1) DZT(radius=1) must match Fourier.DFT() on the same samples (both
 * reduce to the same sum on the unit circle); (2) IDZT(DZT(x,radius)) must round-trip back to x,
 * for radius=1 and a non-unit radius; (3) calc(z) evaluated on the DZT sampling grid must match
 * the DZT coefficients at those same points (both compute X(z)=SUM x[n]*z^-n).
 */
public class ScratchZDZTVerify01 {
	public static void main(String[] args) {
		int N = 16;
		double loLimit = 0, upLimit = 1;

		java.util.function.Function<Complex, Complex> func = z ->
			Complex.sin(z.times(Complex.DOS_PI * 3)).plus(Complex.cos(z.times(Complex.DOS_PI * 5)).times(0.5));

		// --- Check 1: DZT(radius=1) vs Fourier.DFT() ---
		Z zt = new Z(func, loLimit, upLimit);
		zt.DZT(N, 1.0);

		Fourier fou = new Fourier(func, loLimit, upLimit);
		fou.DFT(N);

		double maxDiff1 = 0;
		for (int k = 0; k < N; ++k) {
			Complex d = zt.getTransformItem(k).minus(fou.getTransformItem(k));
			maxDiff1 = Math.max(maxDiff1, d.mod());
		}
		System.out.println("Check 1: max|DZT(radius=1) - DFT| = " + maxDiff1 + (maxDiff1 < 1e-9 ? "  OK" : "  FAIL"));

		// --- Check 2: round-trip for radius=1 ---
		Z zt2 = new Z(func, loLimit, upLimit);
		zt2.DZT(N, 1.0);
		zt2.IDZT();
		double maxDiff2 = 0;
		for (int n = 0; n < N; ++n) {
			Complex orig = func.apply(new Complex(loLimit + n * (upLimit - loLimit) / N, 0));
			Complex rec = zt2.getSampleItem(n);
			maxDiff2 = Math.max(maxDiff2, orig.minus(rec).mod());
		}
		System.out.println("Check 2: max|round-trip - original| (radius=1) = " + maxDiff2 + (maxDiff2 < 1e-9 ? "  OK" : "  FAIL"));

		// --- Check 3: round-trip for radius != 1 ---
		Z zt3 = new Z(func, loLimit, upLimit);
		zt3.DZT(N, 1.3);
		zt3.IDZT();
		double maxDiff3 = 0;
		for (int n = 0; n < N; ++n) {
			Complex orig = func.apply(new Complex(loLimit + n * (upLimit - loLimit) / N, 0));
			Complex rec = zt3.getSampleItem(n);
			maxDiff3 = Math.max(maxDiff3, orig.minus(rec).mod());
		}
		System.out.println("Check 3: max|round-trip - original| (radius=1.3) = " + maxDiff3 + (maxDiff3 < 1e-6 ? "  OK" : "  FAIL"));

		// --- Check 4: calc(z) on the DZT grid must match the DZT coefficients (radius=1.3) ---
		// Fresh Z instance: IDZT() overwrites 'samples' with the reconstructed signal, and calc()
		// reads from 'samples' -- must run before any IDZT() call on the same object.
		Z zt4 = new Z(func, loLimit, upLimit);
		zt4.DZT(N, 1.3);
		double maxDiff4 = 0;
		for (int k = 0; k < N; ++k) {
			double angle = Complex.DOS_PI * k / N;
			Complex zk = new Complex(1.3 * Math.cos(angle), 1.3 * Math.sin(angle));
			Complex viaCalc = zt4.calc(zk);
			Complex viaDzt = zt4.getTransformItem(k);
			maxDiff4 = Math.max(maxDiff4, viaCalc.minus(viaDzt).mod());
		}
		System.out.println("Check 4: max|calc(z_k) - DZT[k]| (radius=1.3) = " + maxDiff4 + (maxDiff4 < 1e-9 ? "  OK" : "  FAIL"));

		// --- Check 5: DZT()/setRadius() reject a non-positive radius ---
		boolean threw = false;
		try {
			new Z(func, loLimit, upLimit).DZT(N, -1.0);
		} catch (IllegalArgumentException e) {
			threw = true;
		}
		System.out.println("Check 5: DZT(radius<=0) throws IllegalArgumentException = " + threw + (threw ? "  OK" : "  FAIL"));

		boolean allOk = maxDiff1 < 1e-9 && maxDiff2 < 1e-9 && maxDiff3 < 1e-6 && maxDiff4 < 1e-9 && threw;
		System.out.println(allOk ? "ALL CHECKS PASSED" : "SOME CHECKS FAILED");
		if (!allOk) System.exit(1);
	}
}
