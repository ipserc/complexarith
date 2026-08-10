package TestComplex;

import com.ipserc.arith.complex.Complex;

/**
 * Auditoria matematica dedicada -- Bloque D ("nuevos instrumentos matematicos", ver
 * Claude/ComplexArithRev.md): verificacion numerica del polilogaritmo Li_s(z), recien anadido a
 * ComplexFunctions.java.
 */
public class ScratchPolylogAudit01 {

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

		System.out.println("=== (1) Li_1(z) == -log(1-z) ===");
		double[][] pts = {{0.5, 0.0}, {-0.3, 0.0}, {0.4, 0.3}, {-0.2, -0.5}, {0.0, 0.6}};
		for (double[] pt : pts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex lhs = Complex.polylog(new Complex(1, 0), z);
			Complex rhs = Complex.log(Complex.ONE.minus(z)).opposite();
			check("Li_1(z)==-log(1-z), z=" + z, lhs.minus(rhs).mod(), 1e-6);
		}

		System.out.println("\n=== (2) Li_0(z) == z/(1-z), Li_-1(z) == z/(1-z)^2 (formas cerradas) ===");
		for (double[] pt : pts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex li0 = Complex.polylog(new Complex(0, 0), z);
			Complex li0Expected = z.divides(Complex.ONE.minus(z));
			check("Li_0(z)==z/(1-z), z=" + z, li0.minus(li0Expected).mod(), 1e-6);

			Complex liM1 = Complex.polylog(new Complex(-1, 0), z);
			Complex liM1Expected = z.divides(Complex.ONE.minus(z).power(2));
			check("Li_-1(z)==z/(1-z)^2, z=" + z, liM1.minus(liM1Expected).mod(), 1e-6);
		}

		System.out.println("\n=== (3) Li_2(1) == zeta(2) == pi^2/6 (caso especial z=1) ===");
		Complex li2at1 = Complex.polylog(new Complex(2, 0), Complex.ONE);
		check("Li_2(1)==zeta(2)", li2at1.minus(Complex.zeta(new Complex(2, 0))).mod(), 1e-6);
		check("Li_2(1)==pi^2/6", li2at1.minus(new Complex(Math.PI * Math.PI / 6, 0)).mod(), 1e-6);
		Complex li3at1 = Complex.polylog(new Complex(3, 0), Complex.ONE);
		check("Li_3(1)==zeta(3)", li3at1.minus(Complex.zeta(new Complex(3, 0))).mod(), 1e-6);

		System.out.println("\n=== (4) Li_s(z) ~= z para |z| pequeno (termino dominante) ===");
		double[][] smallPts = {{0.001, 0.0}, {0.0005, 0.0007}};
		for (double[] pt : smallPts) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex li = Complex.polylog(new Complex(2.5, -0.7), z);
			check("Li_s(z)~=z, z=" + z, li.minus(z).mod(), 1e-5);
		}

		System.out.println("\n=== (5) Excepcion para |z|>=1 (excepto z=1 con Re(s)>1) ===");
		Complex s2 = new Complex(2, 0);
		try {
			Complex.polylog(s2, new Complex(1.5, 0));
			check("polylog lanza para |z|=1.5", 1.0, 0.0); // force fail: no exception thrown
		} catch (IllegalArgumentException e) {
			check("polylog lanza para |z|=1.5", 0.0, 1.0);
		}
		try {
			Complex.polylog(s2, new Complex(0, 1)); // |z|==1, z!=1
			check("polylog lanza para z=i (|z|=1, z!=1)", 1.0, 0.0);
		} catch (IllegalArgumentException e) {
			check("polylog lanza para z=i (|z|=1, z!=1)", 0.0, 1.0);
		}
		Complex s05 = new Complex(0.5, 0);
		try {
			Complex.polylog(s05, Complex.ONE); // z=1 pero Re(s)<=1: no aplica el caso especial
			check("polylog lanza para z=1, Re(s)=0.5<=1", 1.0, 0.0);
		} catch (IllegalArgumentException e) {
			check("polylog lanza para z=1, Re(s)=0.5<=1", 0.0, 1.0);
		}

		System.out.println("\n" + ok + " OK, " + fail + " FAIL");
	}
}
