package TestComplex;

import com.ipserc.arith.complex.Complex;

/**
 * Auditoria matematica dedicada de ComplexFunctions.java (Vigesimosexta sesion, bloque 1 de la
 * hoja de ruta "Matematicas Aplicadas"). Verifica valores conocidos, identidades y consistencia
 * cruzada entre las distintas implementaciones de gamma/zeta, y el round-trip de las inversas
 * trigonometricas/hiperbolicas -- no solo lectura de codigo.
 */
public class ScratchComplexFunctionsAudit01 {

	static int pass = 0, fail = 0;

	static void check(String label, Complex actual, Complex expected, double tol) {
		double err = actual.minus(expected).mod();
		boolean ok = err < tol;
		if (ok) ++pass; else ++fail;
		System.out.printf("%-55s %-4s actual=%s expected=%s err=%.3e%n",
				label, ok ? "OK" : "***FAIL***", actual.toString(), expected.toString(), err);
	}

	static void checkReal(String label, double actual, double expected, double tol) {
		double err = Math.abs(actual - expected);
		boolean ok = err < tol;
		if (ok) ++pass; else ++fail;
		System.out.printf("%-55s %-4s actual=%.10f expected=%.10f err=%.3e%n",
				label, ok ? "OK" : "***FAIL***", actual, expected, err);
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(5);
		double tol = 1e-6;

		System.out.println("=== GAMMA: valores conocidos (enteros/semienteros) ===");
		Complex g5 = new Complex(5, 0);
		Complex expected24 = new Complex(24, 0);
		check("gamma_fast(5) == 24", Complex.gamma_fast(g5), expected24, tol);
		check("gamma_integral(5) == 24", Complex.gamma_integral(g5), expected24, tol);
		check("gamma_euler(5) == 24", Complex.gamma_euler(g5), expected24, tol);
		check("gamma_weiertrass(5) == 24", Complex.gamma_weiertrass(g5), expected24, tol);
		check("gamma_nemes(5) == 24", Complex.gamma_nemes(g5), expected24, tol);
		check("gamma_zones(5) == 24", Complex.gamma_zones(g5), expected24, tol);

		Complex g05 = new Complex(0.5, 0);
		Complex sqrtPi = new Complex(Math.sqrt(Math.PI), 0);
		check("gamma_fast(0.5) == sqrt(pi)", Complex.gamma_fast(g05), sqrtPi, tol);
		check("gamma_integral(0.5) == sqrt(pi)", Complex.gamma_integral(g05), sqrtPi, tol);
		check("gamma_euler(0.5) == sqrt(pi)", Complex.gamma_euler(g05), sqrtPi, tol);
		check("gamma_weiertrass(0.5) == sqrt(pi)", Complex.gamma_weiertrass(g05), sqrtPi, tol);
		check("gamma_nemes(0.5) == sqrt(pi)", Complex.gamma_nemes(g05), sqrtPi, 1e-3);

		System.out.println("\n=== GAMMA: formula de reflexion Gamma(z)Gamma(1-z) = pi/sin(pi z), z complejo ===");
		Complex z = new Complex(0.3, 0.4);
		Complex one = new Complex(1, 0);
		Complex lhs = Complex.gamma_fast(z).times(Complex.gamma_fast(one.minus(z)));
		Complex rhs = Complex.PI.divides(Complex.sin(Complex.PI.times(z)));
		check("gamma_fast reflection z=0.3+0.4i", lhs, rhs, tol);

		System.out.println("\n=== GAMMA: factorial(int n) == n! (referencia real) ===");
		checkReal("factorial(5) == 120", Complex.factorial(5), 120.0, 1e-9);

		System.out.println("\n=== GAMMA: factorial(Complex n) -- se espera n! = Gamma(n+1) ===");
		Complex fact5 = Complex.factorial(new Complex(5, 0));
		Complex expected120 = new Complex(120, 0);
		check("factorial(Complex 5) == 120 (n! = Gamma(n+1))", fact5, expected120, tol);
		System.out.println("  (si FALLA: factorial(Complex) devuelve gamma(n) = (n-1)!, no n! = gamma(n+1))");

		System.out.println("\n=== BETA: B(p,q) = Gamma(p)Gamma(q)/Gamma(p+q), y == integral cruda ===");
		Complex p = new Complex(2, 0), q = new Complex(3, 0);
		// B(2,3) = 1!*2!/4! = 1*2/24 = 1/12
		check("beta(2,3) == 1/12", Complex.beta(p, q), new Complex(1.0 / 12, 0), tol);

		System.out.println("\n=== BINOMIAL: valores conocidos de Pascal ===");
		check("binomialCoef(6,2) == 15", Complex.binomialCoef(6, 2), new Complex(15, 0), 1e-9);
		check("binomialCoef(Complex 6,2) == 15", Complex.binomialCoef(new Complex(6, 0), new Complex(2, 0)), new Complex(15, 0), tol);
		check("binomialCoef(Complex 10,0) == 1", Complex.binomialCoef(new Complex(10, 0), new Complex(0, 0)), new Complex(1, 0), tol);

		System.out.println("\n=== ZETA: valores conocidos ===");
		// zeta(2) = pi^2/6, zeta(4) = pi^4/90
		check("zeta_re(2.5) region sanity (zeta(2)=pi^2/6 via zeta_re path? Re>2 needed)", Complex.zeta(new Complex(4, 0)), new Complex(Math.pow(Math.PI, 4) / 90.0, 0), tol);
		check("zeta_havil(2) == pi^2/6", Complex.zeta_havil(new Complex(2, 0)), new Complex(Math.PI * Math.PI / 6.0, 0), 1e-4);
		check("zeta(0) == -1/2", Complex.zeta(new Complex(0, 0)), new Complex(-0.5, 0), tol);
		check("zeta(-1) == -1/12 (via functional equation zeta_ext)", Complex.zeta(new Complex(-1, 0)), new Complex(-1.0 / 12, 0), 1e-3);

		System.out.println("\n=== TRIG: identidades fundamentales, z complejo ===");
		Complex zt = new Complex(0.7, 0.9);
		Complex sin2 = Complex.sin(zt).power(2);
		Complex cos2 = Complex.cos(zt).power(2);
		check("sin^2+cos^2 == 1", sin2.plus(cos2), one, tol);
		check("tan(z) == sin(z)/cos(z)", Complex.tan(zt), Complex.sin(zt).divides(Complex.cos(zt)), tol);
		check("cot(z) == cos(z)/sin(z)", Complex.cot(zt), Complex.cos(zt).divides(Complex.sin(zt)), tol);
		Complex cosh2 = Complex.cosh(zt).power(2);
		Complex sinh2 = Complex.sinh(zt).power(2);
		check("cosh^2-sinh^2 == 1", cosh2.minus(sinh2), one, tol);
		check("tanh(z) == sinh(z)/cosh(z)", Complex.tanh(zt), Complex.sinh(zt).divides(Complex.cosh(zt)), tol);

		System.out.println("\n=== INVERSAS: round-trip f(f^-1(z)) == z ===");
		Complex zi = new Complex(0.4, 0.6);
		check("sin(arcsin(z)) == z", Complex.sin(Complex.arcsin(zi)), zi, tol);
		check("cos(arccos(z)) == z", Complex.cos(Complex.arccos(zi)), zi, tol);
		check("tan(arctan(z)) == z", Complex.tan(Complex.arctan(zi)), zi, tol);
		check("sinh(arcsinh(z)) == z", Complex.sinh(Complex.arcsinh(zi)), zi, tol);
		check("cosh(arccosh(z)) == z", Complex.cosh(Complex.arccosh(zi)), zi, tol);
		check("tanh(arctanh(z)) == z", Complex.tanh(Complex.arctanh(zi)), zi, tol);
		check("asin(z)+acos(z) == pi/2", Complex.arcsin(zi).plus(Complex.arccos(zi)), new Complex(Complex.HALF_PI, 0), tol);

		System.out.println("\n=== SINC/COSC/TANC: limites y valores ===");
		check("sinc(0) == 1", Complex.sinc(Complex.ZERO), one, tol);
		check("tanc(0) == 1", Complex.tanc(Complex.ZERO), one, tol);
		Complex smallZ = new Complex(1e-6, 0);
		check("sinc(1e-6) ~= 1 (limit sanity)", Complex.sinc(smallZ), one, 1e-9);

		System.out.println("\n=== CHEBYSHEV: cos(n*acos(x)) vs recurrencia T_n ===");
		double[] xs = {0.3, -0.7, 1.5, -2.0};
		for (double x : xs) {
			Complex cx = new Complex(x, 0);
			for (int n = 0; n <= 5; ++n) {
				Complex viaTrig = Complex.chebyshev(n, cx);
				Complex viaRecurrence = chebyshevRecurrence(n, x);
				check("chebyshev(" + n + "," + x + ")", viaTrig, viaRecurrence, 1e-6);
			}
		}

		System.out.println("\n=== POWER: 0^z casos frontera ===");
		check("0^0 == 1", Complex.ZERO.power(Complex.ZERO), one, tol);
		check("0^2 == 0", Complex.ZERO.power(new Complex(2, 0)), Complex.ZERO, tol);

		System.out.println("\n=== LOG: logbase consistente con log natural ===");
		Complex zl = new Complex(3, 2);
		check("logbase(z,e) == log(z)", Complex.logbase(zl, Math.E), Complex.log(zl), tol);
		check("logbase(z,10) == log10(z)", Complex.logbase(zl, 10.0), Complex.log10(zl), tol);

		System.out.println("\n===================================");
		System.out.println("TOTAL: " + pass + " OK, " + fail + " FAIL");
	}

	// Reference implementation via the standard 3-term Chebyshev recurrence (T0=1, T1=x, Tn=2x Tn-1 - Tn-2),
	// independent of ComplexFunctions.chebyshev's trig-based analytic continuation.
	static Complex chebyshevRecurrence(int n, double x) {
		if (n == 0) return new Complex(1, 0);
		if (n == 1) return new Complex(x, 0);
		double tPrev2 = 1, tPrev1 = x, t = 0;
		for (int k = 2; k <= n; ++k) {
			t = 2 * x * tPrev1 - tPrev2;
			tPrev2 = tPrev1;
			tPrev1 = t;
		}
		return new Complex(t, 0);
	}
}
