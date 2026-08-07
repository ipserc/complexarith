package TestComplex;

import com.ipserc.arith.signal.Fourier;

/**
 * Audit driver for Fourier.bandPassFilter(gain,fIni,bandwidth,slope,samplefreq) (5-arg, "Band
 * Pass with slopes"), Decimoctava sesion, continuacion, ver Claude/ComplexArithRev.md.
 *
 * Same suspicion as slopeFilter() (Fourier.VERSION 1.3, already fixed): the mirror
 * "transform.setItem(0, N-i-1, fVal)" for i in [0,N2) maps i=N2-1 to bin N2 (Nyquist, correct
 * boundary handling) but as a side effect shifts every OTHER pairing by one bin too (e.g. bin 0's
 * DC value ends up mirrored onto bin N-1, which represents frequency -1, not "negative DC").
 * Checking the same way: a genuinely conjugate-symmetric real filter reconstructs to a purely
 * real signal via IDFT(); a broken mirror shows up as a non-negligible imaginary part.
 */
public class ScratchFourierFilterAudit03 {
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
		System.out.println("bandPassFilter(slope) reconstructed signal: max|Im| = " + maxImag + ", max|Re| = " + maxReal);
		System.out.println((maxImag > 1e-9 * maxReal)
				? "MISMATCH: non-negligible imaginary part -- confirms the same off-by-one mirror pattern."
				: "OK: imaginary part negligible -- no bug here.");
	}
}
