package TestComplex;

import com.ipserc.arith.complex.Complex;

/**
 * Auditoria matematica dedicada -- Bloque E ("nuevos instrumentos matematicos", ver
 * Claude/ComplexArithRev.md): verificacion numerica de las funciones de Bessel besselJ(nu,z)/
 * besselY(nu,z), recien anadidas a ComplexFunctions.java. Ultimo bloque de la hoja de ruta.
 */
public class ScratchBesselAudit01 {

	static int ok = 0;
	static int fail = 0;

	static void check(String label, double err, double tol) {
		boolean pass = err < tol;
		if (pass) ok++; else fail++;
		System.out.printf("%-65s err=%.3e  %s%n", label, err, pass ? "OK" : "***FAIL***");
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(8);

		System.out.println("=== (1) J_0(0)==1, J_n(0)==0 (n>0) ===");
		check("J_0(0)==1", Complex.besselJ(new Complex(0, 0), Complex.ZERO).minus(Complex.ONE).mod(), 1e-9);
		for (int n = 1; n <= 3; n++) {
			check("J_" + n + "(0)==0", Complex.besselJ(new Complex(n, 0), Complex.ZERO).mod(), 1e-9);
		}

		System.out.println("\n=== (2) Valores tabulados conocidos ===");
		check("J_0(1)==0.7651976866", Complex.besselJ(new Complex(0, 0), new Complex(1, 0)).minus(new Complex(0.7651976866, 0)).mod(), 1e-8);
		check("J_1(1)==0.4400505857", Complex.besselJ(new Complex(1, 0), new Complex(1, 0)).minus(new Complex(0.4400505857, 0)).mod(), 1e-8);
		check("J_0(2)==0.2238907791", Complex.besselJ(new Complex(0, 0), new Complex(2, 0)).minus(new Complex(0.2238907791, 0)).mod(), 1e-8);

		System.out.println("\n=== (3) Recurrencia J_(n-1)(z)+J_(n+1)(z)==(2n/z)*J_n(z) ===");
		double[][] recPts = {{2.5, 0.0}, {1.3, 0.8}, {-0.7, 1.5}};
		for (int n = 1; n <= 3; n++) {
			for (double[] pt : recPts) {
				Complex z = new Complex(pt[0], pt[1]);
				Complex lhs = Complex.besselJ(new Complex(n - 1, 0), z).plus(Complex.besselJ(new Complex(n + 1, 0), z));
				Complex rhs = Complex.besselJ(new Complex(n, 0), z).times(new Complex(2.0 * n, 0)).divides(z);
				check("J_(n-1)+J_(n+1)==(2n/z)J_n, n=" + n + ", z=" + z, lhs.minus(rhs).mod(), 1e-6);
			}
		}

		System.out.println("\n=== (4) Reflexion orden entero negativo: J_(-n)(z)==(-1)^n*J_n(z) ===");
		Complex zRef = new Complex(1.7, 0.3);
		for (int n = 1; n <= 3; n++) {
			Complex lhs = Complex.besselJ(new Complex(-n, 0), zRef);
			Complex rhs = Complex.besselJ(new Complex(n, 0), zRef).times(n % 2 == 0 ? 1 : -1);
			check("J_-" + n + "(z)==(-1)^" + n + "*J_" + n + "(z)", lhs.minus(rhs).mod(), 1e-9);
		}

		System.out.println("\n=== (5) Orden semientero, forma cerrada ===");
		double[][] halfPts = {{1.0, 0.0}, {2.3, 0.0}, {1.5, 0.6}};
		for (double[] pt : halfPts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex scale = Complex.sqrt(new Complex(2, 0).divides(Complex.PI.times(z)));
			Complex jHalf = Complex.besselJ(new Complex(0.5, 0), z);
			Complex jHalfExpected = scale.times(Complex.sin(z));
			check("J_1/2(z)==sqrt(2/(pi*z))*sin(z), z=" + z, jHalf.minus(jHalfExpected).mod(), 1e-6);

			Complex jMinusHalf = Complex.besselJ(new Complex(-0.5, 0), z);
			Complex jMinusHalfExpected = scale.times(Complex.cos(z));
			check("J_-1/2(z)==sqrt(2/(pi*z))*cos(z), z=" + z, jMinusHalf.minus(jMinusHalfExpected).mod(), 1e-6);

			Complex yHalf = Complex.besselY(new Complex(0.5, 0), z);
			Complex yHalfExpected = jMinusHalfExpected.opposite();
			check("Y_1/2(z)==-sqrt(2/(pi*z))*cos(z), z=" + z, yHalf.minus(yHalfExpected).mod(), 1e-6);

			Complex yMinusHalf = Complex.besselY(new Complex(-0.5, 0), z);
			Complex yMinusHalfExpected = jHalfExpected;
			check("Y_-1/2(z)==sqrt(2/(pi*z))*sin(z), z=" + z, yMinusHalf.minus(yMinusHalfExpected).mod(), 1e-6);
		}

		System.out.println("\n=== (6) Y_n(z) orden entero: valores tabulados conocidos ===");
		check("Y_0(1)==0.0882569642", Complex.besselY(new Complex(0, 0), new Complex(1, 0)).minus(new Complex(0.0882569642, 0)).mod(), 1e-6);
		check("Y_1(1)==-0.7812128213", Complex.besselY(new Complex(1, 0), new Complex(1, 0)).minus(new Complex(-0.7812128213, 0)).mod(), 1e-6);

		System.out.println("\n=== (7) Wronskiano J_n(z)*Y_n'(z)-J_n'(z)*Y_n(z)==2/(pi*z) ===");
		double[][] wronskPts = {{1.5, 0.0}, {2.2, 0.4}};
		for (int n = 0; n <= 2; n++) {
			Complex nC = new Complex(n, 0);
			for (double[] pt : wronskPts) {
				Complex z = new Complex(pt[0], pt[1]);
				Complex jPrime = Complex.derivative(z, w -> Complex.besselJ(nC, w), 6);
				Complex yPrime = Complex.derivative(z, w -> Complex.besselY(nC, w), 6);
				Complex lhs = Complex.besselJ(nC, z).times(yPrime).minus(jPrime.times(Complex.besselY(nC, z)));
				Complex rhs = new Complex(2, 0).divides(Complex.PI.times(z));
				check("Wronskiano, n=" + n + ", z=" + z, lhs.minus(rhs).mod(), 1e-3);
			}
		}

		System.out.println("\n=== (8) Ecuacion diferencial de Bessel z^2*y''+z*y'+(z^2-n^2)*y==0 ===");
		double[][] odePts = {{2.0, 0.0}, {1.8, 0.5}};
		for (int n = 0; n <= 2; n++) {
			Complex nC = new Complex(n, 0);
			for (double[] pt : odePts) {
				Complex z = new Complex(pt[0], pt[1]);
				Complex yPrime = Complex.derivative(z, w -> Complex.besselJ(nC, w), 4);
				Complex yDoublePrime = Complex.derivative(z, w -> Complex.derivative(w, v -> Complex.besselJ(nC, v), 4), 4);
				Complex lhs = z.power(2).times(yDoublePrime).plus(z.times(yPrime)).plus(z.power(2).minus(nC.power(2)).times(Complex.besselJ(nC, z)));
				check("Bessel ODE (J_n), n=" + n + ", z=" + z, lhs.mod(), 1e-2);
			}
		}

		System.out.println("\n" + ok + " OK, " + fail + " FAIL");
	}
}
