package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.rf.CircularWaveguide;
import com.ipserc.arith.vectorcalculus.VectorCalculus;
import com.ipserc.arith.vectorcalculus.VectorCalculus.VectorField;

/**
 * Verifica que los campos TE11 de {@link CircularWaveguide} cumplen de verdad las ecuaciones de
 * Maxwell -- mismo patrón que {@code TestRF_MaxwellTE10_01} para la guía rectangular:
 * {@code curl(E)=-mu*dH/dt}, {@code curl(H)=eps*dE/dt} ({@code dH/dt}/{@code dE/dt} por diferencia
 * central en el tiempo) y {@code div(E)=div(H)=0} (región sin fuentes), con {@link
 * VectorCalculus}, muestreando varios radios y ángulos (no solo un eje, a diferencia del TE10
 * rectangular -- el TE11 circular depende genuinamente de {@code phi}). También verifica que el
 * corte {@code kc*a} calculado por bisección coincide con el valor de referencia tabulado de
 * {@code p'_11}.
 */
public class TestRF_MaxwellTE11_01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	static final double DT = 1e-14; // paso de diferencia central en el tiempo

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;
		Complex.printBoxText(boxShape, boxMargin, "Maxwell -- modo TE11 de guia circular");

		double a = 0.01143; // radio (m), del orden de la WR-90 usada en el TE10 rectangular
		CircularWaveguide guide = new CircularWaveguide(a);
		double fc = guide.cutoffFrequencyTE11();
		double frequency = 1.5*fc; // por encima del corte -- modo propagante
		System.out.printf("fc(TE11) = %.4e Hz , frecuencia de trabajo = %.4e Hz%n", fc, frequency);

		double refFirstRootJ1Prime = 1.8411837813406593; // p'_11, referencia tabulada (Abramowitz & Stegun)
		double kcA = guide.cutoffWavenumberTE11()*a;
		System.out.printf("kc*a (bisección) = %.13f , referencia tabulada p'_11 = %.13f%n", kcA, refFirstRootJ1Prime);
		check("p'_11 hallado por bisección coincide con el valor de referencia tabulado", Math.abs(kcA - refFirstRootJ1Prime) < 1e-9);

		double t0 = 3.7e-12; // instante de tiempo arbitrario (no un cero trivial de sin/cos)

		VectorField E = p -> new double[] {guide.instantEx(p[0], p[1], p[2], t0, frequency), guide.instantEy(p[0], p[1], p[2], t0, frequency), 0};
		VectorField H = p -> new double[] {guide.instantHx(p[0], p[1], p[2], t0, frequency), guide.instantHy(p[0], p[1], p[2], t0, frequency), guide.instantHz(p[0], p[1], p[2], t0, frequency)};

		double maxCurlEError = 0, maxCurlHError = 0, maxDivEError = 0, maxDivHError = 0;
		double fieldScale = guide.instantEx(0.5*a, 0.1*a, 0, t0, frequency); // orden de magnitud tipico del campo

		for (double rhoFrac : new double[] {0.3, 0.5, 0.7}) {
			for (double phi : new double[] {0.0, Math.PI/6, Math.PI/3, Math.PI/2, 2*Math.PI/3}) {
				double x = rhoFrac*a*Math.cos(phi), y = rhoFrac*a*Math.sin(phi);
				for (double z = 0.0; z <= 0.01; z += 0.005) {
					double[] point = {x, y, z};

					double[] curlE = VectorCalculus.curl(E, point);
					double[] dHdt = new double[] {
							(guide.instantHx(x, y, z, t0+DT, frequency) - guide.instantHx(x, y, z, t0-DT, frequency)) / (2*DT),
							(guide.instantHy(x, y, z, t0+DT, frequency) - guide.instantHy(x, y, z, t0-DT, frequency)) / (2*DT),
							(guide.instantHz(x, y, z, t0+DT, frequency) - guide.instantHz(x, y, z, t0-DT, frequency)) / (2*DT)
					};
					for (int i = 0; i < 3; ++i) {
						maxCurlEError = Math.max(maxCurlEError, Math.abs(curlE[i] - (-guide.mu*dHdt[i])));
					}

					double[] curlH = VectorCalculus.curl(H, point);
					double[] dEdt = new double[] {
							(guide.instantEx(x, y, z, t0+DT, frequency) - guide.instantEx(x, y, z, t0-DT, frequency)) / (2*DT),
							(guide.instantEy(x, y, z, t0+DT, frequency) - guide.instantEy(x, y, z, t0-DT, frequency)) / (2*DT),
							0
					};
					for (int i = 0; i < 3; ++i) {
						maxCurlHError = Math.max(maxCurlHError, Math.abs(curlH[i] - guide.epsilon*dEdt[i]));
					}

					maxDivEError = Math.max(maxDivEError, Math.abs(VectorCalculus.divergence(E, point)));
					maxDivHError = Math.max(maxDivHError, Math.abs(VectorCalculus.divergence(H, point)));
				}
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
		try { guide.instantEx(0.5*a, 0.1*a, 0, 0, 0.5*fc); rejectsBelowCutoff = false; } catch (IllegalArgumentException e) { }
		check("instantEx() rechaza una frecuencia por debajo del corte", rejectsBelowCutoff);

		// Fuera del eje pero con x==y==0 no puede darse (mismo punto) -- la singularidad real esta
		// en rho=0, es decir x=0 e y=0 simultaneamente.
		boolean rejectsOnAxis = true;
		try { guide.instantEx(0, 0, 0, t0, frequency); rejectsOnAxis = false; } catch (IllegalArgumentException e) { }
		check("instantEx() rechaza el punto sobre el eje (rho=0, singular)", rejectsOnAxis);

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}
}
