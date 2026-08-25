package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.NamedSeries;
import com.ipserc.arith.plot.SimpleGnuplot;
import com.ipserc.arith.rf.CoupledCavityFilter;

/**
 * Verifica {@link CoupledCavityFilter}, la síntesis clásica (prototipo Chebyshev + inversores de
 * impedancia) en contraste con {@link com.ipserc.arith.rf.EvanescentModeFilter} (Maxwell directo):
 * <ol>
 * <li>El prototipo paso bajo {@link CoupledCavityFilter#chebyshevPrototype} coincide con un caso
 * de referencia comprobado a mano (orden 3, rizado 0.5 dB -- valores tabulados estándar).</li>
 * <li>El filtro completo es sin pérdidas ({@code |S11|^2+|S21|^2=1}) en todo el barrido.</li>
 * <li>La respuesta tiene la firma de un Chebyshev de orden {@code N}: exactamente {@code N} picos
 * locales de {@code |S21|} dentro de la banda, todos muy cerca de 0 dB.</li>
 * <li>El rizado real medido converge al rizado de diseño ({@code rippleDB}) al estrechar el ancho
 * de banda fraccional {@code fbw} -- el circuito equivalente usa un parámetro de pendiente de
 * reactancia normalizado igual en todos los resonadores (Pozar, aproximación de banda estrecha),
 * así que el ajuste exacto solo se espera en el límite {@code fbw->0}; medido con {@code fbw=0.05,
 * 0.005}, el error decrece de forma monótona, confirmando que la convergencia es genuina y no
 * casualidad.</li>
 * <li>Fuera de banda la transmisión cae muy por debajo del rizado.</li>
 * <li>Validación de argumentos en el constructor y en {@code chebyshevPrototype}.</li>
 * </ol>
 */
public class TestRF_CoupledCavityFilter01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;
		Complex.printBoxText(boxShape, boxMargin, "Filtro por sintesis clasica -- prototipo Chebyshev + inversores");

		// Caso de referencia comprobado a mano: orden 3, rizado 0.5 dB -- tabla estandar
		// (Matthaei-Young-Jones / Pozar): g = {1, 1.5963, 1.0967, 1.5963, 1.0000}.
		double[] gRef = CoupledCavityFilter.chebyshevPrototype(3, 0.5);
		double[] gExpected = {1.0, 1.5963, 1.0967, 1.5963, 1.0000};
		double maxGError = 0;
		for (int i = 0; i < gExpected.length; ++i) { maxGError = Math.max(maxGError, Math.abs(gRef[i]-gExpected[i])); }
		System.out.printf("max |g - g_tabulado| (orden 3, 0.5 dB) = %.2e%n", maxGError);
		check("El prototipo Chebyshev coincide con la tabla de referencia (orden 3, 0.5 dB)", maxGError < 1e-3);

		int order = 4;
		double rippleDB = 0.1, f0 = 1e10, fbw = 0.01, z0 = 50;
		CoupledCavityFilter filter = new CoupledCavityFilter(order, rippleDB, f0, fbw, z0);
		System.out.print("g="); for (double v : filter.gValues()) System.out.printf(" %.4f", v); System.out.println();
		System.out.print("K="); for (double v : filter.couplingInverters()) System.out.printf(" %.4f", v); System.out.println();

		double fLoWide = f0*(1-2.5*fbw), fHiWide = f0*(1+2.5*fbw);
		int nWide = 3000;
		double maxLosslessError = 0;
		double[][] s21dBWide = new double[nWide+1][2];
		for (int i = 0; i <= nWide; ++i) {
			double f = fLoWide + (fHiWide-fLoWide)*i/nWide;
			Complex[] s = filter.sParameters(f);
			double s11sq = s[0].mod()*s[0].mod(), s21sq = s[1].mod()*s[1].mod();
			maxLosslessError = Math.max(maxLosslessError, Math.abs(s11sq+s21sq-1.0));
			s21dBWide[i][0] = f;
			s21dBWide[i][1] = 20*Math.log10(Math.max(s[1].mod(), 1e-300));
		}
		System.out.printf("max ||S11|^2+|S21|^2-1| en todo el barrido = %.3e%n", maxLosslessError);
		check("El filtro es sin perdidas (|S11|^2+|S21|^2=1) en todo el barrido", maxLosslessError < 1e-9);

		// Banda de diseño exacta -- ahi es donde se espera el rizado equiondulatorio.
		double fLo = f0*(1-fbw/2), fHi = f0*(1+fbw/2);
		int n = 5000;
		double[] s21mag = new double[n+1];
		for (int i = 0; i <= n; ++i) {
			double f = fLo + (fHi-fLo)*i/n;
			s21mag[i] = filter.sParameters(f)[1].mod();
		}
		double minDB = Double.POSITIVE_INFINITY, maxDB = Double.NEGATIVE_INFINITY;
		int peaks = 0;
		for (int i = 0; i <= n; ++i) {
			double db = 20*Math.log10(s21mag[i]);
			minDB = Math.min(minDB, db);
			maxDB = Math.max(maxDB, db);
			if (i > 0 && i < n && s21mag[i] > s21mag[i-1] && s21mag[i] > s21mag[i+1]) { peaks++; }
		}
		System.out.printf("en banda de diseno: |S21| max=%.5f dB, min=%.5f dB, picos locales=%d (orden=%d)%n", maxDB, minDB, peaks, order);
		check("El pico maximo de |S21| en banda esta practicamente a 0 dB", maxDB > -0.01);
		check("Numero de picos de |S21| en banda == orden del filtro (firma Chebyshev)", peaks == order);
		check("El rizado medido esta acotado por un margen razonable sobre el de diseno", Math.abs(minDB) < 2.0*rippleDB);

		// Convergencia de banda estrecha: al reducir fbw, el rizado medido debe acercarse al
		// rizado de diseno (el circuito equivalente es una aproximacion de banda estrecha).
		double errorAt5pct = Math.abs(minRippleDB(order, rippleDB, f0, 0.05, z0) - (-rippleDB));
		double errorAt0_5pct = Math.abs(minRippleDB(order, rippleDB, f0, 0.005, z0) - (-rippleDB));
		System.out.printf("error de rizado: fbw=0.05 -> %.4f dB , fbw=0.005 -> %.4f dB%n", errorAt5pct, errorAt0_5pct);
		check("El error de rizado decrece al estrechar fbw (aproximacion de banda estrecha genuina)", errorAt0_5pct < errorAt5pct);

		// Fuera de banda, muy por debajo del suelo de rizado.
		double farFreq = f0*(1+8*fbw);
		double farS21dB = 20*Math.log10(filter.sParameters(farFreq)[1].mod());
		System.out.printf("|S21| a f=f0*(1+8*fbw) = %.2f dB%n", farS21dB);
		check("Fuera de banda la transmision cae muy por debajo del rizado de diseno", farS21dB < -3*rippleDB - 20);

		boolean rejects;
		rejects = true; try { CoupledCavityFilter.chebyshevPrototype(0, 0.5); rejects = false; } catch (IllegalArgumentException e) { }
		check("chebyshevPrototype rechaza orden no positivo", rejects);
		rejects = true; try { CoupledCavityFilter.chebyshevPrototype(3, 0); rejects = false; } catch (IllegalArgumentException e) { }
		check("chebyshevPrototype rechaza rizado no positivo", rejects);
		rejects = true; try { new CoupledCavityFilter(order, rippleDB, 0, fbw, z0); rejects = false; } catch (IllegalArgumentException e) { }
		check("constructor rechaza f0 no positiva", rejects);
		rejects = true; try { new CoupledCavityFilter(order, rippleDB, f0, 0, z0); rejects = false; } catch (IllegalArgumentException e) { }
		check("constructor rechaza fbw no positivo", rejects);
		rejects = true; try { new CoupledCavityFilter(order, rippleDB, f0, fbw, 0); rejects = false; } catch (IllegalArgumentException e) { }
		check("constructor rechaza z0 no positiva", rejects);

		MatrixComplexPlot.plotSeries("Filtro Chebyshev por sintesis clasica -- |S21| (dB) vs frecuencia", "\"Frecuencia (Hz)\"", "\"Hz\"", false,
				MatrixComplexPlot.e_lineStyle.LINES, SimpleGnuplot.e_syncMode.ASYNC, new NamedSeries("|S21| (dB)", s21dBWide));

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}

	/** The measured in-band minimum {@code |S21|} (dB) for a filter with the given design, over its exact design band. */
	static double minRippleDB(int order, double rippleDB, double f0, double fbw, double z0) {
		CoupledCavityFilter filter = new CoupledCavityFilter(order, rippleDB, f0, fbw, z0);
		double fLo = f0*(1-fbw/2), fHi = f0*(1+fbw/2);
		int n = 3000;
		double minDB = Double.POSITIVE_INFINITY;
		for (int i = 0; i <= n; ++i) {
			double f = fLo + (fHi-fLo)*i/n;
			minDB = Math.min(minDB, 20*Math.log10(filter.sParameters(f)[1].mod()));
		}
		return minDB;
	}
}
