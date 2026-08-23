package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.rf.RectangularWaveguide;
import com.ipserc.arith.vectorcalculus.VectorCalculus;
import com.ipserc.arith.vectorcalculus.VectorCalculus.VectorField;

/**
 * Verifica que los campos TE10 de {@link RectangularWaveguide} cumplen de verdad las ecuaciones
 * de Maxwell -- no se asume, se comprueba por ejecución: {@code curl(E)=-mu*dH/dt},
 * {@code curl(H)=eps*dE/dt} (con {@code dH/dt}/{@code dE/dt} por diferencia central en el tiempo,
 * ya que las fórmulas del campo son cerradas en {@code t}) y {@code div(E)=div(H)=0} (región sin
 * fuentes), todo con {@link VectorCalculus}, a una frecuencia por encima del corte (modo
 * propagante).
 */
public class TestRF_MaxwellTE10_01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	static final double DT = 1e-14; // paso de diferencia central en el tiempo

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;
		Complex.printBoxText(boxShape, boxMargin, "Maxwell -- modo TE10 de guia rectangular");

		double a = 0.02286, b = 0.01016; // guia WR-90 estandar, banda X
		RectangularWaveguide guide = new RectangularWaveguide(a, b);
		double fc = guide.cutoffFrequencyTE10();
		double frequency = 1.5*fc; // por encima del corte -- modo propagante
		System.out.printf("fc(TE10) = %.4e Hz , frecuencia de trabajo = %.4e Hz%n", fc, frequency);

		double t0 = 3.7e-12; // instante de tiempo arbitrario (no un cero trivial de sin/cos)

		VectorField E = p -> new double[] {0, guide.instantEy(p[0], p[2], t0, frequency), 0};
		VectorField H = p -> new double[] {guide.instantHx(p[0], p[2], t0, frequency), 0, guide.instantHz(p[0], p[2], t0, frequency)};

		double maxCurlEError = 0, maxCurlHError = 0, maxDivEError = 0, maxDivHError = 0;
		double fieldScale = guide.instantEy(a/2, 0, t0, frequency); // orden de magnitud tipico del campo, para tolerancia relativa

		for (double x = 0.002; x < a; x += 0.003) {
			for (double z = 0.0; z <= 0.02; z += 0.005) {
				double[] point = {x, 0.0, z};

				double[] curlE = VectorCalculus.curl(E, point);
				double[] dHdt = new double[] {
						(guide.instantHx(x, z, t0+DT, frequency) - guide.instantHx(x, z, t0-DT, frequency)) / (2*DT),
						0,
						(guide.instantHz(x, z, t0+DT, frequency) - guide.instantHz(x, z, t0-DT, frequency)) / (2*DT)
				};
				for (int i = 0; i < 3; ++i) {
					double error = Math.abs(curlE[i] - (-guide.mu*dHdt[i]));
					maxCurlEError = Math.max(maxCurlEError, error);
				}

				double[] curlH = VectorCalculus.curl(H, point);
				double dEydt = (guide.instantEy(x, z, t0+DT, frequency) - guide.instantEy(x, z, t0-DT, frequency)) / (2*DT);
				double[] epsDEdt = {0, guide.epsilon*dEydt, 0};
				for (int i = 0; i < 3; ++i) {
					double error = Math.abs(curlH[i] - epsDEdt[i]);
					maxCurlHError = Math.max(maxCurlHError, error);
				}

				maxDivEError = Math.max(maxDivEError, Math.abs(VectorCalculus.divergence(E, point)));
				maxDivHError = Math.max(maxDivHError, Math.abs(VectorCalculus.divergence(H, point)));
			}
		}

		double tolCurl = 1e-3*Math.abs(fieldScale)*(2*Math.PI*frequency); // escala tipica de w*|E| o similar
		System.out.printf("max |curl(E) - (-mu*dH/dt)| = %.3e (escala de referencia ~%.3e)%n", maxCurlEError, tolCurl);
		check("curl(E) = -mu*dH/dt (ley de Faraday)", maxCurlEError < tolCurl);

		System.out.printf("max |curl(H) - eps*dE/dt| = %.3e%n", maxCurlHError);
		check("curl(H) = eps*dE/dt (ley de Ampere-Maxwell, sin fuentes)", maxCurlHError < tolCurl*guide.epsilon/guide.mu*1e6);

		System.out.printf("max |div(E)| = %.3e , max |div(H)| = %.3e%n", maxDivEError, maxDivHError);
		check("div(E) = 0 (region sin cargas)", maxDivEError < 1e-3*Math.abs(fieldScale)/a);
		check("div(H) = 0 (sin monopolos magneticos)", maxDivHError < 1e-3*Math.abs(fieldScale)/a);

		// El modo NO se propaga por debajo del corte -- las formulas de campo instantaneo deben
		// rechazar una frecuencia por debajo de fc.
		boolean rejectsBelowCutoff = true;
		try { guide.instantEy(a/2, 0, 0, 0.5*fc); rejectsBelowCutoff = false; } catch (IllegalArgumentException e) { }
		check("instantEy() rechaza una frecuencia por debajo del corte", rejectsBelowCutoff);

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}
}
