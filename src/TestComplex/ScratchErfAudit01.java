package TestComplex;

import com.ipserc.arith.complex.Complex;

/**
 * Auditoria matematica dedicada -- Bloque B ("nuevos instrumentos matematicos", ver
 * Claude/ComplexArithRev.md): verificacion numerica de erf(z)/erfc(z), recien anadidos a
 * ComplexFunctions.java.
 */
public class ScratchErfAudit01 {

	static int ok = 0;
	static int fail = 0;

	static void check(String label, double err, double tol) {
		boolean pass = err < tol;
		if (pass) ok++; else fail++;
		System.out.printf("%-60s err=%.3e  %s%n", label, err, pass ? "OK" : "***FAIL***");
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(8);

		System.out.println("=== (1) erf(0)==0, erfc(0)==1 ===");
		check("erf(0)==0", Complex.erf(Complex.ZERO).mod(), 1e-9);
		check("erfc(0)==1", Complex.erfc(Complex.ZERO).minus(Complex.ONE).mod(), 1e-9);

		System.out.println("\n=== (2) erf(-z)==-erf(z) (funcion impar) ===");
		double[][] oddPts = {{0.7, 0.0}, {1.3, 0.9}, {-0.5, 2.1}, {2.0, -1.4}, {Math.log(2), Math.sqrt(3)}};
		for (double[] pt : oddPts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex lhs = Complex.erf(z.opposite());
			Complex rhs = Complex.erf(z).opposite();
			check("erf(-z)==-erf(z), z=" + z, lhs.minus(rhs).mod(), 1e-9);
		}

		System.out.println("\n=== (3) Valores tabulados conocidos (eje real) ===");
		check("erf(0.5)==0.5204998778130465", Complex.erf(new Complex(0.5, 0)).minus(new Complex(0.5204998778130465, 0)).mod(), 1e-9);
		check("erf(1)==0.8427007929497149", Complex.erf(new Complex(1, 0)).minus(new Complex(0.8427007929497149, 0)).mod(), 1e-9);
		check("erf(2)==0.9953222650189527", Complex.erf(new Complex(2, 0)).minus(new Complex(0.9953222650189527, 0)).mod(), 1e-9);
		check("erf(4)->~1 (saturacion)", Complex.erf(new Complex(4, 0)).minus(Complex.ONE).mod(), 1e-6);

		System.out.println("\n=== (4) erfc(z) == 1-erf(z) ===");
		for (double[] pt : oddPts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex lhs = Complex.erfc(z);
			Complex rhs = Complex.ONE.minus(Complex.erf(z));
			check("erfc(z)==1-erf(z), z=" + z, lhs.minus(rhs).mod(), 1e-9);
		}

		System.out.println("\n=== (5) erf'(z) == (2/sqrt(pi))*exp(-z^2), via derivada numerica ===");
		double twoOverSqrtPi = 2.0 / Math.sqrt(Math.PI);
		double[][] derivPts = {{0.6, 0.0}, {0.3, 0.4}, {-0.2, 0.5}, {Math.log(2), Math.sqrt(3)}};
		for (double[] pt : derivPts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex viaDerivative = Complex.derivative(z, Complex::erf, 6);
			Complex viaFormula = Complex.exp(z.power(2).opposite()).times(twoOverSqrtPi);
			check("erf'(z) vs closed form, z=" + z, viaDerivative.minus(viaFormula).mod(), 1e-4);
		}

		System.out.println("\n=== (6) Cruce contra integracion numerica directa de exp(-t^2) ===");
		double[][] intPts = {{0.8, 0.0}, {0.5, 0.6}, {-0.4, 0.3}, {Math.log(2), Math.sqrt(3)}};
		for (double[] pt : intPts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex viaSeries = Complex.erf(z);
			Complex viaIntegral = Complex.integrate(Complex.ZERO, z, t -> Complex.exp(t.power(2).opposite()), 6).times(twoOverSqrtPi);
			check("erf(z) vs integral(exp(-t^2)), z=" + z, viaSeries.minus(viaIntegral).mod(), 1e-4);
		}

		System.out.println("\n" + ok + " OK, " + fail + " FAIL");
	}
}
