package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;

/**
 * Plots sinc(z) for z ranging over the complex square from -6-6i to 6+6i,
 * as two connected 3D surfaces (real part, imaginary part).
 */
public class TestSurfaceSinc02 {

	private static final double LOW = -6, HIGH = 6;
	private static final int SAMPLES = 161;

	public static void main(String[] args) {
		double step = (HIGH - LOW) / (SAMPLES - 1);
		double[][][] surfaceRe = new double[SAMPLES][SAMPLES][3];
		double[][][] surfaceIm = new double[SAMPLES][SAMPLES][3];

		for (int row = 0; row < SAMPLES; ++row) {
			double re = LOW + row * step;
			for (int col = 0; col < SAMPLES; ++col) {
				double im = LOW + col * step;
				Complex sincZ = Complex.sinc(new Complex(re, im));
				surfaceRe[row][col] = new double[] { re, im, sincZ.rep() };
				surfaceIm[row][col] = new double[] { re, im, sincZ.imp() };
			}
		}

		MatrixComplexPlot.plotGrid3DAsync("Re(sinc(z))", surfaceRe);
		MatrixComplexPlot.plotGrid3DAsync("Im(sinc(z))", surfaceIm);
	}
}
