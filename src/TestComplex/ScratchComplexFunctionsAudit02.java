package TestComplex;

import com.ipserc.arith.complex.Complex;

/**
 * Auditoria matematica dedicada de ComplexFunctions.java (Vigesimosexta sesion) -- seguimiento
 * dirigido a caracterizar 3 candidatos de discrepancia detectados en ScratchComplexFunctionsAudit01:
 * (1) asin(z)+acos(z)==pi/2 rompe para z complejo (posible corte de rama inconsistente entre
 * arcsin/arccos), (2) gamma_integral(z) para 0<Re(z)<1 (posible singularidad no manejada), (3) el
 * criterio de parada de gamma_weiertrass/gamma_euler no escala con los decimales pedidos (mismo
 * patron que el bug ya arreglado en zeta_re, sesion 6).
 */
public class ScratchComplexFunctionsAudit02 {

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(6);
		Complex halfPi = new Complex(Complex.HALF_PI, 0);

		System.out.println("=== (1) asin(z)+acos(z)==pi/2 -- barrido de puntos ===");
		double[][] points = {
			{0.4, 0.0}, {-0.4, 0.0}, {0.9, 0.0}, {-0.9, 0.0},          // eje real dentro de [-1,1]
			{1.5, 0.0}, {-1.5, 0.0}, {3.0, 0.0}, {-3.0, 0.0},          // eje real fuera de [-1,1]
			{0.0, 0.5}, {0.0, -0.5}, {0.0, 2.0},                       // eje imaginario puro
			{0.4, 0.6}, {0.4, -0.6}, {-0.4, 0.6}, {-0.4, -0.6},        // 4 cuadrantes, |z|<1
			{1.5, 0.5}, {1.5, -0.5}, {-1.5, 0.5}, {-1.5, -0.5}         // 4 cuadrantes, |z|>1
		};
		for (double[] pt : points) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex sum = Complex.arcsin(z).plus(Complex.arccos(z));
			double err = sum.minus(halfPi).mod();
			System.out.printf("z=%-18s asin+acos=%-24s err=%.3e  %s%n", z, sum, err, err < 1e-6 ? "OK" : "***FAIL***");
		}

		System.out.println("\n=== (1b) formulas de raiz usadas por arcsin/arccos, mismo z ===");
		// arcsin usa root(1-z^2, 2); arccos usa root(z^2-1, 2). Si root() escoge la rama principal
		// de forma "ingenua" (sin coordinar signo con la otra), root(z^2-1,2) puede NO ser igual a
		// i*root(1-z^2,2) (lo que la identidad de verdad necesita), sino a -i*root(1-z^2,2) en according
		// zonas del plano.
		for (double[] pt : new double[][]{ {0.4, 0.6}, {1.5, 0.5} }) {
			Complex z = new Complex(pt[0], pt[1]);
			Complex one = new Complex(1, 0);
			Complex sqrt1mz2 = Complex.sqrt(one.minus(z.power(2)));
			Complex sqrtz2m1 = Complex.sqrt(z.power(2).minus(one));
			Complex i = new Complex(0, 1);
			Complex expectedIfConsistent = sqrt1mz2.times(i);
			System.out.printf("z=%-18s sqrt(z^2-1)=%-22s i*sqrt(1-z^2)=%-22s iguales=%s%n",
					z, sqrtz2m1, expectedIfConsistent, sqrtz2m1.minus(expectedIfConsistent).mod() < 1e-9);
		}

		System.out.println("\n=== (2) gamma_integral(z) para 0<Re(z)<1 (singularidad integrable t^(z-1) en t=0) ===");
		double[] res = {0.9, 0.7, 0.5, 0.3, 0.1};
		for (double re : res) {
			Complex z = new Complex(re, 0);
			Complex viaIntegral = Complex.gamma_integral(z);
			Complex viaFast = Complex.gamma_fast(z);
			double err = viaIntegral.minus(viaFast).mod();
			System.out.printf("Re(z)=%.1f  gamma_integral=%-14s gamma_fast(referencia)=%-14s err=%.3e  %s%n",
					re, viaIntegral, viaFast, err, err < 1e-4 ? "OK" : "***FAIL***");
		}
		System.out.println("(referencia adicional, z=1..5, donde no hay singularidad en t=0):");
		for (double re = 1.0; re <= 5.0; re += 1.0) {
			Complex z = new Complex(re, 0);
			Complex viaIntegral = Complex.gamma_integral(z);
			Complex viaFast = Complex.gamma_fast(z);
			double err = viaIntegral.minus(viaFast).mod();
			System.out.printf("Re(z)=%.1f  gamma_integral=%-14s gamma_fast(referencia)=%-14s err=%.3e  %s%n",
					re, viaIntegral, viaFast, err, err < 1e-4 ? "OK" : "***FAIL***");
		}

		System.out.println("\n=== (3) gamma_weiertrass/gamma_euler: error real vs decimales pedidos ===");
		Complex z5 = new Complex(5, 0);
		Complex expected24 = new Complex(24, 0);
		for (int decimals : new int[]{3, 5, 7, 9}) {
			Complex.setFixedON(decimals);
			double errW = Complex.gamma_weiertrass(z5).minus(expected24).mod();
			double errE = Complex.gamma_euler(z5).minus(expected24).mod();
			double requestedTol = Math.pow(10, -decimals);
			System.out.printf("decimales pedidos=%d (tol implicita ~%.1e)  err_weiertrass=%.3e (%s)  err_euler=%.3e (%s)%n",
					decimals, requestedTol, errW, errW < requestedTol ? "cumple" : "NO CUMPLE",
					errE, errE < requestedTol ? "cumple" : "NO CUMPLE");
		}
	}
}
