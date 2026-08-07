package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Fourier;

/**
 * Verification driver for the radix-2 FFT added to Fourier.DFT()/IDFT() (Decimoctava sesion,
 * continuacion, ver Claude/ComplexArithRev.md). DFTL() (kept in the class specifically as an
 * honest, unoptimized O(n^2) reference) is the ground truth: for N a power of two, DFT(N) must
 * now go through the new FFT path and match DFTL(N) to machine precision, for both real and
 * complex-valued signals. Also checks the IDFT() round-trip and that non-power-of-two N is
 * unaffected (still the old O(n^2) symmetry path).
 */
public class ScratchFourierFFTVerify01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, double maxDiff, double tol) {
		boolean ok = maxDiff < tol;
		System.out.println((ok ? "OK   " : "FAIL ") + label + " maxDiff=" + maxDiff + " tol=" + tol);
		if (ok) ++pass; else ++fail;
	}

	private static double maxDiffFftVsRef(Fourier fft, Fourier ref, int n) {
		double maxDiff = 0;
		for (int k = 0; k < n; ++k) {
			Complex d = fft.getTransformItem(k).minus(ref.getTransformItem(k));
			maxDiff = Math.max(maxDiff, d.mod());
		}
		return maxDiff;
	}

	public static void main(String[] args) {
		double loLimit = 0, upLimit = 1;

		// Real-valued signal.
		java.util.function.Function<Complex, Complex> realFunc = z ->
			Complex.sin(z.times(Complex.DOS_PI * 3)).plus(Complex.cos(z.times(Complex.DOS_PI * 5)).times(0.5));

		// Genuinely complex-valued signal (this is exactly the case the old DFT() symmetry path
		// gets wrong for N not a power of two -- here every N tested IS a power of two, so both
		// DFT() and DFTL() should agree, since DFT() now uses the general FFT for those N).
		java.util.function.Function<Complex, Complex> complexFunc = z ->
			Complex.exp(Complex.i.times(z.times(Complex.DOS_PI * 4))).plus(new Complex(0, 1).times(z));

		int[] powersOfTwo = {4, 8, 16, 32, 64, 128, 256, 1024};
		for (int n : powersOfTwo) {
			for (String kind : new String[] {"real", "complex"}) {
				java.util.function.Function<Complex, Complex> func = kind.equals("real") ? realFunc : complexFunc;

				Fourier viaFft = new Fourier(func, loLimit, upLimit);
				viaFft.DFT(n);
				Fourier viaRef = new Fourier(func, loLimit, upLimit);
				viaRef.DFTL(n);

				double maxDiff = maxDiffFftVsRef(viaFft, viaRef, n);
				check("DFT(N=" + n + ", " + kind + ") vs DFTL", maxDiff, 1e-9);
			}
		}

		// Round-trip: IDFT(DFT(x)) reconstructs x, for a power-of-two N (exercises the new IFFT path).
		int n = 64;
		Fourier roundTrip = new Fourier(realFunc, loLimit, upLimit);
		roundTrip.DFT(n);
		roundTrip.IDFT();
		double maxDiffRt = 0;
		for (int k = 0; k < n; ++k) {
			Complex orig = realFunc.apply(new Complex(loLimit + k * (upLimit - loLimit) / n, 0));
			Complex rec = roundTrip.getSampleItem(k);
			maxDiffRt = Math.max(maxDiffRt, orig.minus(rec).mod());
		}
		check("IDFT(DFT(x)) round-trip, N=" + n, maxDiffRt, 1e-9);

		// Non-power-of-two N must be completely unaffected (still the old O(n^2) symmetry path):
		// DFT(N) must still equal itself run twice (determinism) and match DFTL(N) for a REAL signal
		// (the only case the old symmetry path is valid for).
		int nOdd = 100; // not a power of two
		Fourier viaOldPath1 = new Fourier(realFunc, loLimit, upLimit);
		viaOldPath1.DFT(nOdd);
		Fourier viaOldPath2 = new Fourier(realFunc, loLimit, upLimit);
		viaOldPath2.DFTL(nOdd);
		double maxDiffOdd = maxDiffFftVsRef(viaOldPath1, viaOldPath2, nOdd);
		check("DFT(N=" + nOdd + " -- not power of two, real signal) vs DFTL (old path unaffected)", maxDiffOdd, 1e-9);

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
