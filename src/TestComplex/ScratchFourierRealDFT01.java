package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Fourier;

public class ScratchFourierRealDFT01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		try {
			final int N = 7; // odd, NOT a power of two
			final double loLimit = 0.0;
			final double upLimit = 2 * Math.PI;

			// Real-valued signal -- regression check that dropping the symmetry shortcut didn't
			// break the common (real-signal) case.
			Fourier fourier = new Fourier(t -> new Complex(Math.cos(2 * t.rep()) + 0.5 * Math.sin(t.rep()), 0), loLimit, upLimit);
			fourier.DFT(N);

			Complex[] samples = new Complex[N];
			for (int n = 0; n < N; ++n) {
				double t = loLimit + n * (upLimit - loLimit) / N;
				samples[n] = new Complex(Math.cos(2 * t) + 0.5 * Math.sin(t), 0);
			}
			double maxErr = 0;
			for (int k = 0; k < N; ++k) {
				Complex acc = new Complex(0, 0);
				for (int n = 0; n < N; ++n) {
					double angle = -2 * Math.PI * k * n / N;
					Complex w = new Complex(Math.cos(angle), Math.sin(angle));
					acc = acc.plus(w.times(samples[n]));
				}
				Complex got = fourier.getTransformItem(k);
				double err = got.minus(acc).mod();
				maxErr = Math.max(maxErr, err);
				System.out.println("k=" + k + " got=" + got + " expected=" + acc + " err=" + err);
			}
			System.out.println("maxErr=" + maxErr);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
