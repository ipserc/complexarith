package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Fourier;
import com.ipserc.arith.signal.Laplace;
import com.ipserc.arith.signal.Z;

/**
 * Self-contained scratch check for the MatrixComplexPlot extension (8 agosto 2026): exercises every
 * plotting method touched in Fourier/Laplace/Z, confirming each one runs its full data-prep +
 * MatrixComplexPlot delegation without exception, failing only at JavaPlot's actual gnuplot process
 * launch (confirmed absent from PATH in this environment -- documented limitation, not a regression).
 * A failure ANYWHERE ELSE (NPE, ArrayIndexOutOfBounds, wrong arg count/order) would surface before
 * that point and is what this script is meant to catch.
 */
public class ScratchMatrixComplexPlotAudit01 {

	static int ok = 0, fail = 0;

	static void attempt(String label, Runnable r) {
		try {
			r.run();
			System.out.println("UNEXPECTED NO-THROW: " + label);
			fail++;
		} catch (Throwable t) {
			String top = t.getClass().getName();
			StackTraceElement[] st = t.getStackTrace();
			String firstFrame = st.length > 0 ? st[0].getClassName() + "." + st[0].getMethodName() : "?";
			boolean reachedGnuplot = firstFrame.startsWith("com.panayotis.gnuplot") || firstFrame.startsWith("java.lang.ProcessBuilder") || firstFrame.startsWith("java.lang.ProcessImpl");
			System.out.println((reachedGnuplot ? "OK  " : "FAIL") + " " + label + " -> " + top + " at " + firstFrame + " : " + t.getMessage());
			if (reachedGnuplot) ok++; else fail++;
		}
	}

	public static void main(String[] args) {
		Complex.setFormatON();

		// ---- Fourier ----
		Fourier f = new Fourier(z -> Complex.sin(z.times(Complex.DOS_PI)), 0.0, 1.0);
		f.serialize(5, 3);

		attempt("Fourier.plotSamples(int)", () -> f.plotSamples("samples", 16, true, Fourier.e_lineStyle.LINES));
		attempt("Fourier.plotSeries", () -> f.plotSeries("series", 16, true, Fourier.e_lineStyle.LINES));
		attempt("Fourier.plotSeries showIm=false", () -> f.plotSeries("series-noim", 16, false, Fourier.e_lineStyle.LINES));
		attempt("Fourier.plotCompare", () -> f.plotCompare(16, Fourier.e_lineStyle.LINES));

		Fourier f2 = new Fourier(z -> Complex.sin(z.times(Complex.DOS_PI)), 0.0, 1.0);
		f2.DFT(16);
		attempt("Fourier.plotDFT SAMP", () -> f2.plotDFT("dft-samp", Fourier.e_domain.SAMP, Fourier.e_operator.COMPLEX, false, Fourier.e_lineStyle.LINES));
		attempt("Fourier.plotDFT FREC", () -> f2.plotDFT("dft-frec", Fourier.e_domain.FREC, Fourier.e_operator.MAGNITUDE, false, Fourier.e_lineStyle.LINES));

		// ---- Laplace ----
		Laplace l = new Laplace(z -> Complex.sin(z.times(Complex.DOS_PI)), new Complex(0, 0), new Complex(1, 0));
		l.DLT(16);
		attempt("Laplace.plotFunction", () -> l.plotFunction("lfunc", 16, true, Laplace.e_lineStyle.LINES));
		attempt("Laplace.plotSamples", () -> l.plotSamples("lsamples", true, Laplace.e_lineStyle.LINES));
		attempt("Laplace.plotDLT", () -> l.plotDLT("ldlt", true, Laplace.e_lineStyle.LINES));

		// ---- Z ----
		Z zt = new Z(z -> Complex.sin(z.times(Complex.DOS_PI)), new Complex(0, 0), new Complex(1, 0));
		zt.DZT(16);
		attempt("Z.plotFunction", () -> zt.plotFunction("zfunc", 16, true, Z.e_lineStyle.LINES));
		attempt("Z.plotSamples", () -> zt.plotSamples("zsamples", true, Z.e_lineStyle.LINES));
		attempt("Z.plotDZT", () -> zt.plotDZT("zdzt", true, Z.e_lineStyle.LINES));

		System.out.println("---");
		System.out.println("OK(reached gnuplot launch):" + ok + " FAIL(broke earlier):" + fail);
	}
}
