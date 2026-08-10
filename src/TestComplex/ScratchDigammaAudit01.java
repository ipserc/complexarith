package TestComplex;

import com.ipserc.arith.complex.Complex;

/**
 * Auditoria matematica dedicada -- Bloque A ("nuevos instrumentos matematicos", ver
 * Claude/ComplexArithRev.md): verificacion numerica de digamma psi(z) y poligamma psi^(n)(z),
 * recien anadidos a ComplexFunctions.java.
 */
public class ScratchDigammaAudit01 {

	static int ok = 0;
	static int fail = 0;

	static void check(String label, double err, double tol) {
		boolean pass = err < tol;
		if (pass) ok++; else fail++;
		System.out.printf("%-55s err=%.3e  %s%n", label, err, pass ? "OK" : "***FAIL***");
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(8);

		double eulerMasc = Complex.EULER_MASC;

		System.out.println("=== (1) Valores conocidos ===");
		check("psi(1) == -EULER_MASC", Complex.digamma(new Complex(1, 0)).minus(new Complex(-eulerMasc, 0)).mod(), 1e-6);
		check("psi(2) == 1-EULER_MASC", Complex.digamma(new Complex(2, 0)).minus(new Complex(1 - eulerMasc, 0)).mod(), 1e-6);
		Complex expectedHalf = new Complex(-eulerMasc - 2 * Math.log(2), 0);
		check("psi(1/2) == -EULER_MASC-2ln2", Complex.digamma(new Complex(0.5, 0)).minus(expectedHalf).mod(), 1e-6);

		System.out.println("\n=== (2) Recurrencia psi(z+1)-psi(z)==1/z ===");
		double[][] pts = {{1.3, 0.0}, {5.7, 0.0}, {-2.4, 0.0}, {2.0, 1.5}, {-1.0, 3.0}, {0.3, -0.7}};
		for (double[] pt : pts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex lhs = Complex.digamma(z.plus(1)).minus(Complex.digamma(z));
			Complex rhs = Complex.ONE.divides(z);
			check("psi(z+1)-psi(z)==1/z, z=" + z, lhs.minus(rhs).mod(), 1e-6);
		}

		System.out.println("\n=== (3) Reflexion psi(1-z)-psi(z)==pi*cot(pi*z) ===");
		double[][] reflPts = {{0.3, 0.0}, {0.3, 0.4}, {-0.2, 0.6}, {1.7, 0.9}, {2.5, -1.1}};
		for (double[] pt : reflPts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex lhs = Complex.digamma(Complex.ONE.minus(z)).minus(Complex.digamma(z));
			Complex rhs = Complex.PI.times(Complex.cot(Complex.PI.times(z)));
			check("psi(1-z)-psi(z)==pi*cot(pi*z), z=" + z, lhs.minus(rhs).mod(), 1e-5);
		}

		System.out.println("\n=== (4) psi'(1) == zeta(2) == pi^2/6 ===");
		Complex trigammaAt1 = Complex.polygamma(1, new Complex(1, 0));
		Complex zeta2 = Complex.zeta(new Complex(2, 0));
		check("psi'(1)==zeta(2)", trigammaAt1.minus(zeta2).mod(), 1e-6);
		check("psi'(1)==pi^2/6", trigammaAt1.minus(new Complex(Math.PI * Math.PI / 6, 0)).mod(), 1e-6);

		System.out.println("\n=== (5) Cruce contra la derivada numerica de log(gamma(z)) ===");
		double[][] derivPts = {{3.2, 0.0}, {0.8, 0.5}, {-1.5, 2.0}};
		for (double[] pt : derivPts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex viaFormula = Complex.digamma(z);
			Complex viaDerivative = Complex.derivative(z, w -> Complex.log(Complex.gamma(w)), 6);
			check("psi(z) vs d/dz[ln(gamma(z))], z=" + z, viaFormula.minus(viaDerivative).mod(), 1e-4);
		}

		System.out.println("\n=== (6) Cruce contra la derivada numerica de psi(z) para psi'(z) ===");
		for (double[] pt : derivPts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex viaFormula = Complex.polygamma(1, z);
			Complex viaDerivative = Complex.derivative(z, Complex::digamma, 6);
			check("psi'(z) vs d/dz[psi(z)], z=" + z, viaFormula.minus(viaDerivative).mod(), 1e-3);
		}

		System.out.println("\n=== (7) Orden mayor: psi''(z) via recurrencia hacia psi''(z+1) ===");
		double[][] highPts = {{4.1, 0.0}, {1.2, 1.3}};
		for (double[] pt : highPts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex lhs = Complex.polygamma(2, z.plus(1)).minus(Complex.polygamma(2, z));
			Complex rhs = Complex.ONE.divides(z.power(3)).times(2);
			check("psi''(z+1)-psi''(z)==2/z^3, z=" + z, lhs.minus(rhs).mod(), 1e-5);
		}

		System.out.println("\n" + ok + " OK, " + fail + " FAIL");
	}
}
