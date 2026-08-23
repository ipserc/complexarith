package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.NamedSeries;
import com.ipserc.arith.plot.SimpleGnuplot;
import com.ipserc.arith.rf.EvanescentModeFilter;
import com.ipserc.arith.rf.RectangularWaveguide;

/**
 * Verifica {@link EvanescentModeFilter}: que la cascada barrera-cavidad-barrera es sin pérdidas
 * ({@code |S11|^2+|S21|^2=1} en todo el barrido) y que exhibe una respuesta de paso banda genuina
 * -- transmisión casi total en una frecuencia de resonancia y fuerte atenuación en los extremos
 * de la banda -- consecuencia directa de la física de modos evanescentes de {@link
 * RectangularWaveguide} (Maxwell), no de una tabla de síntesis de filtros. Parámetros de la
 * cavidad (localizados por barrido en {@code ScratchRFFilterSweep01}, no conservado): la longitud
 * de cavidad {@code 0.01 m} da transmisión total dentro de la banda pasante de la guía ancha.
 */
public class TestRF_EvanescentFilter01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;
		Complex.printBoxText(boxShape, boxMargin, "Filtro de modo evanescente -- Fabry-Perot en guia de onda");

		double a = 0.02286, b = 0.01016; // guia WR-90 (banda X)
		RectangularWaveguide passGuide = new RectangularWaveguide(a, b);
		double fc = passGuide.cutoffFrequencyTE10();

		double a2 = 0.17*a; // guia mas estrecha para las barreras -- corte al doble de frecuencia
		RectangularWaveguide barrierGuide = new RectangularWaveguide(a2, b);
		double fc2 = barrierGuide.cutoffFrequencyTE10();
		System.out.printf("fc(passGuide)=%.4e Hz , fc(barrierGuide)=%.4e Hz%n", fc, fc2);

		double barrierLength = 0.5*a, cavityLength = 0.01;
		EvanescentModeFilter filter = new EvanescentModeFilter(passGuide, barrierGuide, barrierLength, cavityLength);

		double fLo = 1.05*fc, fHi = 0.85*fc2;
		int nPoints = 2000;
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

		// El pico de resonancia puede ser muchisimo mas estrecho que el paso del barrido grueso
		// (cuanto mas evanescente es la barrera, mayor la Q del resonador Fabry-Perot y mas fino
		// el pico) -- un barrido de resolucion fija puede saltarselo por completo sin que eso
		// signifique que la resonancia no exista. Se localiza aproximadamente en el barrido grueso
		// y se refina por busqueda adaptativa (ventana que se estrecha) hasta converger,
		// independientemente de lo estrecho que sea el pico real.
		double[] peak = refinePeak(filter, freqAtMaxS21, (fHi-fLo)/(nPoints-1));
		double refinedFreq = peak[0], refinedS21 = peak[1];
		System.out.printf("pico refinado: |S21| = %.10f en f=%.6e Hz (barrido grueso daba %.6f en f=%.4e Hz)%n",
				refinedS21, refinedFreq, maxS21, freqAtMaxS21);
		check("Hay una frecuencia de resonancia con transmision casi total (|S21|>0.999 tras refinar)", refinedS21 > 0.999);

		System.out.printf("|S21| en el borde inferior de banda = %.4f , en el borde superior = %.4f%n", s21AtLowEdge, s21AtHighEdge);
		// El perfil es asimetrico (fisica real, no un bug): el flanco de baja frecuencia cae con
		// fuerza porque la barrera esta bien dentro de su zona evanescente, pero el de alta
		// frecuencia se aplana porque la barrera deja de ser tan evanescente cerca de su propio
		// corte (fc2) -- ver ScratchRFFilterSweep02/03/04.java (no conservados) para el barrido
		// que caracterizo esta asimetria antes de fijar el umbral.
		check("La transmision en ambos bordes de banda es claramente menor que en resonancia (forma de paso banda)",
				s21AtLowEdge < 0.3*refinedS21 && s21AtHighEdge < 0.3*refinedS21);

		boolean rejectsNonPositiveLengths = true;
		try { new EvanescentModeFilter(passGuide, barrierGuide, -1, cavityLength); rejectsNonPositiveLengths = false; } catch (IllegalArgumentException e) { }
		check("constructor rechaza longitudes no positivas", rejectsNonPositiveLengths);

		// Caso degenerado: sin barreras (longitud de barrera -> 0), la cascada se reduce a un
		// tramo simple de passGuide -- debe seguir siendo sin perdidas y con transmision uniforme
		// (sin resonancia marcada, ya que ya no hay reflexion que la produzca).
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

		MatrixComplexPlot.plotSeries("Filtro evanescente -- |S21| vs frecuencia", "\"Frecuencia (Hz)\"", "\"Hz\"", false,
				MatrixComplexPlot.e_lineStyle.LINES, SimpleGnuplot.e_syncMode.ASYNC, new NamedSeries("|S21|", s21Magnitude));

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}

	/**
	 * Localiza el máximo de {@code |S21|} cerca de {@code approxFreq} (el máximo aproximado
	 * hallado en un barrido grueso) por ventana decreciente: cada iteración muestrea densamente un
	 * entorno del mejor punto encontrado hasta ahora y luego estrecha la ventana alrededor de él,
	 * convergiendo al pico real sin importar lo estrecho que sea -- necesario porque la anchura de
	 * la resonancia depende de lo evanescente que sea la barrera (a mayor evanescencia, mayor Q,
	 * pico más fino) y un barrido de resolución fija puede no verlo nunca.
	 * @return {@code {frecuencia, |S21|}} en el máximo hallado.
	 */
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
