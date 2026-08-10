package TestComplex;

import com.ipserc.arith.complex.Complex;

/**
 * Auditoria matematica dedicada -- Bloque C ("nuevos instrumentos matematicos", ver
 * Claude/ComplexArithRev.md): verificacion numerica de la funcion eta de Dirichlet eta(s), recien
 * anadida a ComplexFunctions.java, y regresion de zeta_havil(s) tras su refactorizacion para
 * reutilizar el motor de eta() en vez de duplicar el bucle.
 */
public class ScratchEtaAudit01 {

	static int ok = 0;
	static int fail = 0;

	static void check(String label, double err, double tol) {
		boolean pass = err < tol;
		if (pass) ok++; else fail++;
		System.out.printf("%-60s err=%.3e  %s%n", label, err, pass ? "OK" : "***FAIL***");
	}

	// Suma alternada directa Sum_{k=1}^N (-1)^(k-1)/k^s, referencia independiente del motor de
	// eta() -- no llama a ningun metodo de ComplexFunctions.
	static Complex etaDirectSum(Complex s, int N) {
		Complex sum = new Complex(0);
		for (int k = 1; k <= N; ++k) {
			Complex term = Complex.ONE.divides(new Complex(k, 0).power(s));
			sum = (k % 2 == 1) ? sum.plus(term) : sum.minus(term);
		}
		return sum;
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(8);

		System.out.println("=== (1) Valores conocidos ===");
		check("eta(1)==ln(2)", Complex.eta(new Complex(1, 0)).minus(new Complex(Math.log(2), 0)).mod(), 1e-9);
		check("eta(2)==pi^2/12", Complex.eta(new Complex(2, 0)).minus(new Complex(Math.PI * Math.PI / 12, 0)).mod(), 1e-9);

		System.out.println("\n=== (2) Identidad eta(s)==(1-2^(1-s))*zeta(s) ===");
		double[][] idPts = {{3.0, 0.0}, {2.0, 1.0}, {1.5, -0.7}, {-2.0, 0.0}, {0.3, 0.5}};
		for (double[] pt : idPts) {
			Complex s = new Complex(pt[0], pt[1]);
			Complex lhs = Complex.eta(s);
			Complex rhs = Complex.ONE.minus(new Complex(2, 0).power(Complex.ONE.minus(s))).times(Complex.zeta(s));
			check("eta(s)==(1-2^(1-s))*zeta(s), s=" + s, lhs.minus(rhs).mod(), 1e-6);
		}

		System.out.println("\n=== (3) Cruce contra suma alternada directa (referencia independiente) ===");
		double[][] directPts = {{2.0, 0.0}, {3.0, 0.0}, {2.0, 1.5}};
		for (double[] pt : directPts) {
			Complex s = new Complex(pt[0], pt[1]);
			Complex viaEngine = Complex.eta(s);
			Complex viaDirect = etaDirectSum(s, 200000);
			check("eta(s) vs suma alternada directa (N=2e5), s=" + s, viaEngine.minus(viaDirect).mod(), 1e-3);
		}

		System.out.println("\n=== (4) Regresion de zeta_havil tras la refactorizacion (delega en eta()) ===");
		// zeta_havil cubre -1<Re(s)<2 dentro de zeta(); cruzado aqui contra zeta_re/zeta_ext justo
		// fuera de ese rango, donde ambos motores deberian coincidir (mismo valor, dos caminos).
		check("zeta_havil(1.5) vs zeta_re(1.5) via zeta_re directo", Complex.zeta_havil(new Complex(1.5, 0)).minus(Complex.zeta_re(new Complex(1.5, 0))).mod(), 1e-6);
		check("zeta_havil(2.5) vs zeta_re(2.5) (frontera del dominio)", Complex.zeta_havil(new Complex(2.5, 0)).minus(Complex.zeta_re(new Complex(2.5, 0))).mod(), 1e-6);
		check("zeta_havil(-1.5) vs zeta_ext(-1.5) (frontera del dominio)", Complex.zeta_havil(new Complex(-1.5, 0)).minus(Complex.zeta_ext(new Complex(-1.5, 0))).mod(), 1e-6);
		check("zeta(2) via zeta_re == zeta_havil(2) directo", Complex.zeta(new Complex(2, 0)).minus(Complex.zeta_havil(new Complex(2, 0))).mod(), 1e-6);

		System.out.println("\n" + ok + " OK, " + fail + " FAIL");
	}
}
