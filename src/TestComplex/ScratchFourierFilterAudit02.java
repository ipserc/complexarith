package TestComplex;

import com.ipserc.arith.signal.Fourier;

/**
 * Regression driver for the Fourier.slopeFilter() mirror off-by-one fix (Decimoctava sesion,
 * continuacion, ver Claude/ComplexArithRev.md, Fourier.VERSION 1.3).
 *
 * slopeFilter() fills transform[0..N2) directly (bin i = +i Hz, standard DFT order -- confirmed
 * correct, same as ScratchFourierFilterAudit01.java's finding for deltaFilter()/bandPassFilter()),
 * then mirrors the negative-frequency half. Before the fix, the mirror was off by one bin
 * (transform[N2+k] = transform[N2-1-k] instead of transform[N2-k]), breaking conjugate symmetry
 * and contaminating the reconstructed "real" filtered signal with a non-negligible imaginary part
 * (~5% of the real part, confirmed before the fix). A genuinely conjugate-symmetric real spectrum
 * reconstructs to a purely real time-domain signal via IDFT (imaginary part ~0, floating-point
 * noise only) -- this is now a regression check for that.
 */
public class ScratchFourierFilterAudit02 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + (detail.isEmpty() ? "" : " (" + detail + ")"));
		if (ok) ++pass; else ++fail;
	}

	public static void main(String[] args) {
		int N = 32;
		Fourier f = new Fourier(N, 0.0, 1.0);
		f.slopeFilter(1.0, 5, 0.5, N); // gain=1, fIni=5, slope=0.5 (a ramping band)

		double maxImag = 0;
		double maxReal = 0;
		for (int n = 0; n < N; ++n) {
			com.ipserc.arith.complex.Complex s = f.getSampleItem(n);
			maxImag = Math.max(maxImag, Math.abs(s.imp()));
			maxReal = Math.max(maxReal, Math.abs(s.rep()));
		}
		check("slopeFilter() reconstructed signal is purely real: max|Im|=" + maxImag + ", max|Re|=" + maxReal,
				maxImag < 1e-9 * maxReal, "was ~5% of max|Re| before the fix");

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
