package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Fourier;
import com.ipserc.arith.signal.Sigfunc;

/**
 * Regression driver for the Fourier.convolution() ".times(2)" fix (Decimoctava sesion,
 * continuacion, ver Claude/ComplexArithRev.md, Fourier.VERSION 1.5). The original author left the
 * comment "times(2) don't know why" on the line that doubled every convolution output sample.
 *
 * Ground truth: convolving ANY signal x[n] with the identity impulse response h[n] = delta[n]
 * (h[0]=1, h[n]=0 otherwise) must reproduce x[n] EXACTLY -- y[t] = SUM_n x[n]*h[t-n], and the only
 * nonzero term is n=t (h[0]=1), so y[t]=x[t]. Before the fix, this exact case showed the output
 * was exactly double the input at every sample; now it must match exactly.
 */
public class ScratchFourierConvolutionAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, boolean ok) {
		System.out.println((ok ? "OK   " : "FAIL ") + label);
		if (ok) ++pass; else ++fail;
	}

	public static void main(String[] args) {
		int N = 8;
		double lo = 0, hi = 8;

		// Identity filter: h[n] = delta[n] (Kronecker delta at the FIRST sample point, exactly 0).
		// DFT(N) is used only to force N/sampleFreq to be set and samples populated via
		// doSrsSampling() -- the func-based constructor leaves N at its Java default (0).
		Fourier filter = new Fourier(z -> Sigfunc.delta(z), lo, hi);
		filter.DFT(N);

		// Signal to convolve: a simple ramp x[n] = t_n (distinguishable, nonzero at every sample).
		Fourier signal = new Fourier(z -> z, lo, hi);
		signal.DFT(N);

		Fourier result = filter.convolution(signal);

		for (int n = 0; n < N; ++n) {
			Complex x = signal.getSampleItem(n);
			Complex y = result.getSampleItem(n);
			check("n=" + n + ": convolution(identity) reproduces the signal exactly (x=" + x + ", y=" + y + ")",
					x.minus(y).mod() < 1e-9);
		}

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
