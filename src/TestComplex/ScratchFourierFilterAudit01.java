package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Fourier;

/**
 * Audit driver for Fourier's filter-design methods (bandPassFilter/deltaFilter/slopeFilter),
 * Decimoctava sesion, continuacion, ver Claude/ComplexArithRev.md.
 *
 * Hypothesis: these methods build 'transform[i]' assuming index i maps LINEARLY from -sampleFreq/2
 * (i=0) to +sampleFreq/2 (i=N-1) -- the "fftshift"-style, DC-centered layout that plotDFTfrec()
 * explicitly constructs FOR PLOTTING ONLY (by swapping the two halves of the real transform[]).
 * But DFT()/IDFT() themselves use the STANDARD (unshifted) DFT bin order (bin 0 = DC, bin k =
 * +k*fs/N for k=1..N/2, bin N-k = -k*fs/N) -- confirmed by reading DFT()'s own loop. If the
 * filter methods feed a shifted-layout array into IDFT() (which expects standard order), the
 * energy ends up in the wrong place.
 *
 * Test: build a delta filter at a known target frequency, IDFT() it (as the method itself does),
 * then re-DFT() the resulting time samples with the class's own (guaranteed-correct) DFT(), and
 * check exactly where the energy peak actually lands, translated into a real Hz frequency using
 * the SAME shift plotDFTfrec() uses. If it lands at the target frequency, the layout is fine; if
 * not, the shift-mismatch hypothesis is confirmed.
 */
public class ScratchFourierFilterAudit01 {
	public static void main(String[] args) {
		int N = 32;
		double target = 5; // Hz, must land within [-N/2, N/2)

		Fourier f = new Fourier(N, 0.0, 1.0);
		f.deltaFilter(1.0, target, N); // builds transform[], then internally calls IDFT() -> f.samples

		// Re-DFT the resulting time-domain samples with the class's own DFT() (guaranteed standard
		// order) -- f.samples/isSampled are already set by deltaFilter()'s internal IDFT() call, so
		// DFT() will transform them directly instead of re-sampling from a (nonexistent) function.
		f.DFT(N);

		// Find the bin (standard order) with maximum magnitude.
		int bestK = -1;
		double bestMag = -1;
		for (int k = 0; k < N; ++k) {
			double mag = f.getTransformItem(k).mod();
			if (mag > bestMag) {
				bestMag = mag;
				bestK = k;
			}
		}
		// Standard DFT bin-to-frequency mapping (same one plotDFTfrec()/fftfreq use): bins
		// 0..N/2-1 are frequencies 0..+(N/2-1)*fs/N; bins N/2..N-1 are -N/2..-1 * fs/N.
		int N2 = N / 2;
		double freqOfBestK = (bestK < N2) ? bestK : (bestK - N);

		System.out.println("deltaFilter target frequency: " + target + " Hz");
		System.out.println("Peak energy actually lands at standard DFT bin " + bestK
				+ " = " + freqOfBestK + " Hz (mag=" + bestMag + ")");
		System.out.println(Math.abs(freqOfBestK - target) < 1e-6
				? "OK: peak lands exactly at the target frequency -- no shift mismatch."
				: "MISMATCH: peak does NOT land at the target frequency -- confirms the shift-layout bug.");
	}
}
