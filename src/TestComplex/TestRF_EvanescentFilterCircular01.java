package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.NamedSeries;
import com.ipserc.arith.plot.SimpleGnuplot;
import com.ipserc.arith.rf.CircularWaveguide;
import com.ipserc.arith.rf.EvanescentModeFilter;

/**
 * Mismo filtro que {@code TestRF_EvanescentFilter01} (barrera-cavidad-barrera evanescente,
 * Fabry-Perot), pero sobre {@link CircularWaveguide}/TE11 en vez de {@link
 * com.ipserc.arith.rf.RectangularWaveguide}/TE10 -- {@link EvanescentModeFilter} no tuvo que
 * cambiar de comportamiento para esto, solo generalizarse sobre {@link
 * com.ipserc.arith.rf.WaveguideMode} (VERSION 1.1): confirma que la cascada ABCD es genuinamente
 * independiente de la geometría de la guía, solo depende de {@code propagationConstant}/{@code
 * waveImpedanceTE}.
 */
public class TestRF_EvanescentFilterCircular01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;
		Complex.printBoxText(boxShape, boxMargin, "Filtro de modo evanescente -- Fabry-Perot en guia circular");

		// a: radio (m) de la guia circular de paso. a2: radio mas estrecho para las barreras
		// (corte TE11 mas alto -- ver CircularWaveguide.cutoffFrequencyTE11()).
		double a = 0.01143;
		CircularWaveguide passGuide = new CircularWaveguide(a);
		double fc = passGuide.cutoffFrequencyTE11();

		double a2 = 0.45*a;
		CircularWaveguide barrierGuide = new CircularWaveguide(a2);
		double fc2 = barrierGuide.cutoffFrequencyTE11();
		System.out.printf("fc(passGuide)=%.4e Hz , fc(barrierGuide)=%.4e Hz%n", fc, fc2);

		double barrierLength = 0.5*a, cavityLength = 0.01;
		EvanescentModeFilter filter = new EvanescentModeFilter(passGuide, barrierGuide, barrierLength, cavityLength);

		double fLo = 1.05*fc, fHi = 0.85*fc2;
		int nPoints = 2500;
		double[][] s21Magnitude = new double[nPoints][2];
		double maxLosslessError = 0;
		double maxS21 = 0, freqAtMaxS21 = 0;
		double s21AtLowEdge = 0, s21AtHighEdge = 0;

		for (int i = 0; i < nPoints; ++i) {
			double f = fLo + (fHi-fLo)*i/(nPoints-1);
			Complex[] s = filter.sParameters(f);
			double s11sq = s[0].mod()*s[0].mod();
			double s21sq = s[1].mod()*s[1].mod();
			maxLosslessError = Math.max(maxLosslessError, Math.abs(s11sq + s21sq - 1.0));

			double s21mag = s[1].mod();
			s21Magnitude[i][0] = f;
			s21Magnitude[i][1] = s21mag;
			if (s21mag > maxS21) { maxS21 = s21mag; freqAtMaxS21 = f; }
			if (i == 0) s21AtLowEdge = s21mag;
			if (i == nPoints-1) s21AtHighEdge = s21mag;
		}

		System.out.printf("max ||S11|^2+|S21|^2 - 1| en todo el barrido = %.3e%n", maxLosslessError);
		check("El filtro es sin perdidas (|S11|^2+|S21|^2=1) en todo el barrido de frecuencia", maxLosslessError < 1e-9);

		double[] peak = refinePeak(filter, freqAtMaxS21, (fHi-fLo)/(nPoints-1));
		double refinedFreq = peak[0], refinedS21 = peak[1];
		System.out.printf("pico refinado: |S21| = %.10f en f=%.6e Hz (barrido grueso daba %.6f en f=%.4e Hz)%n",
				refinedS21, refinedFreq, maxS21, freqAtMaxS21);
		check("Hay una frecuencia de resonancia con transmision casi total (|S21|>0.999 tras refinar)", refinedS21 > 0.999);

		System.out.printf("|S21| en el borde inferior de banda = %.4f , en el borde superior = %.4f%n", s21AtLowEdge, s21AtHighEdge);
		check("La transmision en ambos bordes de banda es claramente menor que en resonancia (forma de paso banda)",
				s21AtLowEdge < 0.3*refinedS21 && s21AtHighEdge < 0.3*refinedS21);

		boolean rejectsNonPositiveLengths = true;
		try { new EvanescentModeFilter(passGuide, barrierGuide, -1, cavityLength); rejectsNonPositiveLengths = false; } catch (IllegalArgumentException e) { }
		check("constructor rechaza longitudes no positivas", rejectsNonPositiveLengths);

		EvanescentModeFilter noBarrier = new EvanescentModeFilter(passGuide, barrierGuide, 1e-9, cavityLength);
		double minS21NoBarrier = 1, maxS21NoBarrier = 0;
		for (int i = 0; i < nPoints; ++i) {
			double f = fLo + (fHi-fLo)*i/(nPoints-1);
			double s21mag = noBarrier.sParameters(f)[1].mod();
			minS21NoBarrier = Math.min(minS21NoBarrier, s21mag);
			maxS21NoBarrier = Math.max(maxS21NoBarrier, s21mag);
		}
		System.out.printf("Sin barreras: |S21| entre %.6f y %.6f (deberia ser ~1 en todo el barrido)%n", minS21NoBarrier, maxS21NoBarrier);
		check("Sin barreras la transmision es practicamente total en toda la banda (no hay resonador)", minS21NoBarrier > 0.999);

		MatrixComplexPlot.plotSeries("Filtro evanescente circular -- |S21| vs frecuencia", "\"Frecuencia (Hz)\"", "\"Hz\"", false,
				MatrixComplexPlot.e_lineStyle.LINES, SimpleGnuplot.e_syncMode.ASYNC, new NamedSeries("|S21|", s21Magnitude));

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}

	/** Misma búsqueda adaptativa del pico que {@code TestRF_EvanescentFilter01.refinePeak}. */
	static double[] refinePeak(EvanescentModeFilter filter, double approxFreq, double initialWindow) {
		double lo = approxFreq - initialWindow, hi = approxFreq + initialWindow;
		double bestFreq = approxFreq, bestS21 = filter.sParameters(approxFreq)[1].mod();
		int samplesPerIteration = 200;
		for (int iter = 0; iter < 12; ++iter) {
			for (int i = 0; i <= samplesPerIteration; ++i) {
				double f = lo + (hi-lo)*i/samplesPerIteration;
				double s21 = filter.sParameters(f)[1].mod();
				if (s21 > bestS21) { bestS21 = s21; bestFreq = f; }
			}
			double width = (hi-lo)/samplesPerIteration;
			lo = bestFreq - 5*width;
			hi = bestFreq + 5*width;
		}
		return new double[] {bestFreq, bestS21};
	}
}
