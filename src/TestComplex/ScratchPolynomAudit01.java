package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;

/**
 * Auditoria matematica dedicada de Polynom.java (Vigesimosexta sesion, bloque 2 de la hoja de
 * ruta "Matematicas Aplicadas"). Verifica valores conocidos, identidades y round-trips -- no
 * solo lectura de codigo.
 */
public class ScratchPolynomAudit01 {

	static int pass = 0, fail = 0;

	static void check(String label, Complex actual, Complex expected, double tol) {
		double err = actual.minus(expected).mod();
		boolean ok = err < tol;
		if (ok) ++pass; else ++fail;
		System.out.printf("%-60s %-4s actual=%-20s expected=%-20s err=%.3e%n",
				label, ok ? "OK" : "***FAIL***", actual.toString(), expected.toString(), err);
	}

	static void checkTrue(String label, boolean cond) {
		if (cond) ++pass; else ++fail;
		System.out.printf("%-60s %s%n", label, cond ? "OK" : "***FAIL***");
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(6);
		double tol = 1e-6;

		System.out.println("=== power(int pot): P^0 debe ser el polinomio constante 1 ===");
		Polynom p = new Polynom("1,2,3"); // 1x^2+2x+1 en orden natural -> ver constructor
		try {
			Polynom p0 = p.power(0);
			System.out.println("power(0).degree()=" + p0.degree() + " cols=" + (p0.degree()+1));
			Complex val = p0.eval(new Complex(2.5, -1.3));
			check("P^0 evaluado en un punto arbitrario == 1", val, Complex.ONE, tol);
		} catch (Exception e) {
			System.out.println("power(0) -> EXCEPCION: " + e);
			++fail;
		}

		System.out.println("\n=== power(int pot): P^1, P^2, P^3 consistentes con evaluacion directa ===");
		Complex x0 = new Complex(1.7, 0.3);
		Complex px0 = p.eval(x0);
		check("P^1(x0) == P(x0)", p.power(1).eval(x0), px0, tol);
		check("P^2(x0) == P(x0)^2", p.power(2).eval(x0), px0.power(2), tol);
		check("P^3(x0) == P(x0)^3", p.power(3).eval(x0), px0.power(3), tol);

		System.out.println("\n=== HERMITE (fisicos): valores conocidos H_2(x)=4x^2-2, H_3(x)=8x^3-12x ===");
		double[] xs = {0.5, -1.3, 2.0};
		for (double xv : xs) {
			Complex xc = new Complex(xv, 0);
			check("H_0(" + xv + ")==1", p.hermite(0).eval(xc), Complex.ONE, tol);
			check("H_1(" + xv + ")==2x", p.hermite(1).eval(xc), new Complex(2 * xv, 0), tol);
			check("H_2(" + xv + ")==4x^2-2", p.hermite(2).eval(xc), new Complex(4 * xv * xv - 2, 0), tol);
			check("H_3(" + xv + ")==8x^3-12x", p.hermite(3).eval(xc), new Complex(8 * Math.pow(xv, 3) - 12 * xv, 0), tol);
			check("H_4(" + xv + ")==16x^4-48x^2+12", p.hermite(4).eval(xc), new Complex(16 * Math.pow(xv, 4) - 48 * xv * xv + 12, 0), tol);
		}

		System.out.println("\n=== HERMITE I: se espera que satisfaga SU PROPIA recurrencia autorreferente ===");
		System.out.println("(hermiteI(n) = (2i x)*hermiteI(n-1) - 2(n-1)*hermiteI(n-2), igual patron que hermite/legendre/chebyshev)");
		for (double xv : xs) {
			Complex xc = new Complex(xv, 0);
			// Referencia independiente: recurrencia auto-referente calculada a mano en Java puro (double),
			// sin pasar por Polynom en absoluto.
			Complex hi0 = Complex.ONE;
			Complex hi1 = new Complex(0, 2 * xv);
			Complex hi2 = hi1.times(new Complex(0, 2 * xv)).minus(hi0.times(2.0));
			Complex hi3 = hi2.times(new Complex(0, 2 * xv)).minus(hi1.times(4.0));
			check("hermiteI(2)(" + xv + ") vs recurrencia autorreferente independiente", p.hermiteI(2).eval(xc), hi2, tol);
			check("hermiteI(3)(" + xv + ") vs recurrencia autorreferente independiente", p.hermiteI(3).eval(xc), hi3, tol);
		}

		System.out.println("\n=== LEGENDRE: valores conocidos P_2(x)=(3x^2-1)/2, P_3(x)=(5x^3-3x)/2 ===");
		for (double xv : new double[]{0.5, -0.7, 0.9}) {
			Complex xc = new Complex(xv, 0);
			check("P_0(" + xv + ")==1", p.legendre(0).eval(xc), Complex.ONE, tol);
			check("P_1(" + xv + ")==x", p.legendre(1).eval(xc), xc, tol);
			check("P_2(" + xv + ")==(3x^2-1)/2", p.legendre(2).eval(xc), new Complex((3 * xv * xv - 1) / 2.0, 0), tol);
			check("P_3(" + xv + ")==(5x^3-3x)/2", p.legendre(3).eval(xc), new Complex((5 * Math.pow(xv, 3) - 3 * xv) / 2.0, 0), tol);
		}

		System.out.println("\n=== LEGENDRE I: se espera recurrencia autorreferente ===");
		for (double xv : new double[]{0.5, -0.7}) {
			Complex xc = new Complex(xv, 0);
			Complex li0 = Complex.ONE;
			Complex li1 = new Complex(0, xv);
			// (n+1)L_{n+1} = (2n+1)*i*x*L_n - n*L_{n-1}  =>  L_2 = (3*i*x*L_1 - 1*L_0)/2
			Complex li2 = (new Complex(0, 3 * xv)).times(li1).minus(li0).divides(2.0);
			check("legendreI(2)(" + xv + ") vs recurrencia autorreferente independiente", p.legendreI(2).eval(xc), li2, tol);
		}

		System.out.println("\n=== CHEBYSHEV (kind 1): T_2(x)=2x^2-1, T_3(x)=4x^3-3x ===");
		for (double xv : new double[]{0.3, -0.6, 0.9}) {
			Complex xc = new Complex(xv, 0);
			check("T_2(" + xv + ")==2x^2-1", p.chebyshev1(2).eval(xc), new Complex(2 * xv * xv - 1, 0), tol);
			check("T_3(" + xv + ")==4x^3-3x", p.chebyshev1(3).eval(xc), new Complex(4 * Math.pow(xv, 3) - 3 * xv, 0), tol);
			// Identidad cerrada T_n(x) = cos(n*acos(x)) para x en [-1,1]
			if (Math.abs(xv) <= 1) {
				Complex viaTrig = Complex.cos(Complex.arccos(xc).times(3));
				check("T_3(" + xv + ") == cos(3*acos(x))", p.chebyshev1(3).eval(xc), viaTrig, tol);
			}
		}

		System.out.println("\n=== LAGUERRE: L_1(x)=-x+1, L_2(x)=(x^2-4x+2)/2 ===");
		for (double xv : new double[]{0.5, 2.0}) {
			Complex xc = new Complex(xv, 0);
			check("Laguerre_1(" + xv + ")==-x+1", p.laguerre(1).eval(xc), new Complex(-xv + 1, 0), tol);
			check("Laguerre_2(" + xv + ")==(x^2-4x+2)/2", p.laguerre(2).eval(xc), new Complex((xv * xv - 4 * xv + 2) / 2.0, 0), tol);
		}

		System.out.println("\n=== DIVISION: quotient*divisor+remainder == dividend ===");
		Polynom dividend = new Polynom("1,-3,3,-1"); // (x-1)^3 en orden natural
		Polynom divisor = new Polynom("1,-1"); // x-1
		Polynom quotient = dividend.divides(divisor);
		Polynom remainder = quotient.getRemainder();
		Polynom reconstructed = quotient.times(divisor).plus(remainder);
		Complex xd = new Complex(3.3, -0.7);
		check("quotient*divisor+remainder == dividend, evaluado en un punto", reconstructed.eval(xd), dividend.eval(xd), tol);

		System.out.println("\n=== fromRoots: el polinomio construido se anula en cada raiz dada ===");
		MatrixComplex roots = new MatrixComplex(3, 1);
		roots.setItem(0, 0, new Complex(2, 0));
		roots.setItem(1, 0, new Complex(-1, 1));
		roots.setItem(2, 0, new Complex(-1, -1));
		Polynom fromR = new Polynom().fromRoots(roots);
		for (int i = 0; i < 3; ++i) {
			Complex r = roots.getItem(i, 0);
			check("fromRoots(...) se anula en raiz " + r, fromR.eval(r), Complex.ZERO, 1e-9);
		}

		System.out.println("\n=== INTERPOLACION: el polinomio reproduce los puntos dados ===");
		MatrixComplex points = new MatrixComplex(4, 2);
		double[] xsi = {-2, -1, 1, 3};
		double[] ysi = {5, -1, 3, 25};
		for (int i = 0; i < 4; ++i) {
			points.setItem(i, 0, new Complex(xsi[i], 0));
			points.setItem(i, 1, new Complex(ysi[i], 0));
		}
		Polynom newtonP = new Polynom().interpolationNewton(points);
		Polynom lagrangeP = new Polynom().interpolationLagrange(points);
		for (int i = 0; i < 4; ++i) {
			Complex xi = new Complex(xsi[i], 0);
			Complex yi = new Complex(ysi[i], 0);
			check("Newton interp. en x=" + xsi[i], newtonP.eval(xi), yi, tol);
			check("Lagrange interp. en x=" + xsi[i], lagrangeP.eval(xi), yi, tol);
		}
		// Ambos metodos deben dar el MISMO polinomio (interpolador unico de grado <= n-1)
		Complex probe = new Complex(0.37, 0);
		check("Newton(x)==Lagrange(x) en un punto no dado", newtonP.eval(probe), lagrangeP.eval(probe), tol);

		System.out.println("\n=== solve2d: formula cuadratica contra raices conocidas ===");
		// (x-2)(x-3) = x^2-5x+6, raices 2 y 3
		Polynom quad = new Polynom("1,-5,6");
		MatrixComplex sol = quad.solve2d();
		Complex r1 = sol.getItem(0, 0), r2 = sol.getItem(1, 0);
		boolean matchesKnownRoots = (r1.minus(new Complex(2, 0)).mod() < tol && r2.minus(new Complex(3, 0)).mod() < tol)
				|| (r1.minus(new Complex(3, 0)).mod() < tol && r2.minus(new Complex(2, 0)).mod() < tol);
		checkTrue("solve2d() de x^2-5x+6 da raices {2,3}", matchesKnownRoots);

		System.out.println("\n===================================");
		System.out.println("TOTAL: " + pass + " OK, " + fail + " FAIL");
	}
}
