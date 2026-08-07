package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.signal.Fourier;

public class ScratchFourierComplexDFT01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		try {
			final int N = 6; // NOT a power of two -> exercises the O(n^2) fallback path
			final double loLimit = 0.0;
			final double upLimit = 2 * Math.PI;

			// Genuinely complex-valued signal: f(t) = e^{it}. Real and imaginary parts are NOT
			// symmetric under conjugation across samples, so a real-signal-only shortcut would
			// give the wrong answer here.
			Fourier fourier = new Fourier(t -> Complex.exp(Complex.i.times(t)), loLimit, upLimit);
			fourier.DFT(N);

			// Independent brute-force reference DFT, same sample points, no symmetry shortcut.
			Complex[] samples = new Complex[N];
			for (int n = 0; n < N; ++n) {
				double t = loLimit + n * (upLimit - loLimit) / N;
				samples[n] = Complex.exp(Complex.i.times(t));
			}
			Complex[] expected = new Complex[N];
			for (int k = 0; k < N; ++k) {
				Complex acc = new Complex(0, 0);
				for (int n = 0; n < N; ++n) {
					double angle = -2 * Math.PI * k * n / N;
					Complex w = new Complex(Math.cos(angle), Math.sin(angle));
					acc = acc.plus(w.times(samples[n]));
				}
				expected[k] = acc;
			}

			double maxErr = 0;
			for (int k = 0; k < N; ++k) {
				Complex got = fourier.getTransformItem(k);
				Complex diff = got.minus(expected[k]);
				double err = diff.mod();
				maxErr = Math.max(maxErr, err);
				System.out.println("k=" + k + " got=" + got + " expected=" + expected[k] + " err=" + err);
			}
			System.out.println("maxErr=" + maxErr);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
