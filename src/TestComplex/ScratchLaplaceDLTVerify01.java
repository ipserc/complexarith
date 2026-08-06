package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Fourier;
import com.ipserc.arith.signal.Laplace;

/**
 * Quick sanity verification for the Laplace.DLT()/IDLT() fix (Decimoctava sesion, continuacion,
 * ver ComplexArithRev.md): (1) DLT(sigma=0) must match Fourier.DFT() on the same samples (both
 * reduce to the same sum when sigma=0); (2) IDLT(DLT(x,sigma)) must round-trip back to x, for
 * sigma=0 and a nonzero sigma.
 */
public class ScratchLaplaceDLTVerify01 {
	public static void main(String[] args) {
		int N = 16;
		double loLimit = 0, upLimit = 1;

		java.util.function.Function<Complex, Complex> func = z ->
			Complex.sin(z.times(Complex.DOS_PI * 3)).plus(Complex.cos(z.times(Complex.DOS_PI * 5)).times(0.5));

		// --- Check 1: DLT(sigma=0) vs Fourier.DFT() ---
		Laplace lap = new Laplace(func, loLimit, upLimit);
		lap.DLT(N, 0.0);

		Fourier fou = new Fourier(func, loLimit, upLimit);
		fou.DFT(N);

		double maxDiff1 = 0;
		for (int k = 0; k < N; ++k) {
			Complex d = lap.getTransformItem(k).minus(fou.getTransformItem(k));
			maxDiff1 = Math.max(maxDiff1, d.mod());
		}
		System.out.println("Check 1: max|DLT(sigma=0) - DFT| = " + maxDiff1 + (maxDiff1 < 1e-9 ? "  OK" : "  FAIL"));

		// --- Check 2: round-trip for sigma=0 ---
		Laplace lap2 = new Laplace(func, loLimit, upLimit);
		lap2.DLT(N, 0.0);
		lap2.IDLT();
		double maxDiff2 = 0;
		for (int n = 0; n < N; ++n) {
			Complex orig = func.apply(new Complex(loLimit + n * (upLimit - loLimit) / N, 0));
			Complex rec = lap2.getSampleItem(n);
			maxDiff2 = Math.max(maxDiff2, orig.minus(rec).mod());
		}
		System.out.println("Check 2: max|round-trip - original| (sigma=0) = " + maxDiff2 + (maxDiff2 < 1e-9 ? "  OK" : "  FAIL"));

		// --- Check 3: round-trip for sigma != 0 ---
		Laplace lap3 = new Laplace(func, loLimit, upLimit);
		lap3.DLT(N, 0.7);
		lap3.IDLT();
		double maxDiff3 = 0;
		for (int n = 0; n < N; ++n) {
			Complex orig = func.apply(new Complex(loLimit + n * (upLimit - loLimit) / N, 0));
			Complex rec = lap3.getSampleItem(n);
			maxDiff3 = Math.max(maxDiff3, orig.minus(rec).mod());
		}
		System.out.println("Check 3: max|round-trip - original| (sigma=0.7) = " + maxDiff3 + (maxDiff3 < 1e-6 ? "  OK" : "  FAIL"));

		boolean allOk = maxDiff1 < 1e-9 && maxDiff2 < 1e-9 && maxDiff3 < 1e-6;
		System.out.println(allOk ? "ALL CHECKS PASSED" : "SOME CHECKS FAILED");
		if (!allOk) System.exit(1);
	}
}
