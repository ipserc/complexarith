package TestComplex;

import com.ipserc.arith.signal.Fourier;

/**
 * Regression driver for the Fourier.bandPassFilter(gain,fIni,bandwidth,slope,samplefreq) mirror
 * off-by-one fix (Decimoctava sesion, continuacion, ver Claude/ComplexArithRev.md, Fourier.VERSION
 * 1.4). Same bug family as slopeFilter() (VERSION 1.3, fixed previously): the mirror
 * "transform.setItem(0, N-i-1, fVal)" shifted every pairing by one bin (except the boundary case
 * i=N2-1, which happened to land on the Nyquist bin). A genuinely conjugate-symmetric real filter
 * reconstructs to a purely real time-domain signal via IDFT() -- this checks that.
 */
public class ScratchFourierFilterAudit03 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + (detail.isEmpty() ? "" : " (" + detail + ")"));
		if (ok) ++pass; else ++fail;
	}

	public static void main(String[] args) {
		int N = 32;
		Fourier f = new Fourier(N, 0.0, 1.0);
		f.bandPassFilter(1.0, 5, 8, 0.5, N); // gain=1, fIni=5, bandwidth=8, slope=0.5

		double maxImag = 0;
		double maxReal = 0;
		for (int n = 0; n < N; ++n) {
			com.ipserc.arith.complex.Complex s = f.getSampleItem(n);
			maxImag = Math.max(maxImag, Math.abs(s.imp()));
			maxReal = Math.max(maxReal, Math.abs(s.rep()));
		}
		check("bandPassFilter(slope) reconstructed signal is purely real: max|Im|=" + maxImag + ", max|Re|=" + maxReal,
				maxImag < 1e-9 * maxReal, "was ~15% of max|Re| before the fix");

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
