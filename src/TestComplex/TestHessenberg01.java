package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Hessenbergfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for {@code Hessenbergfactor} (Etapa 1 del plan QR-con-desplazamientos, ver
 * {@code Claude/ComplexArithRev.md}, Novena sesion / plan mutable-rolling-stardust.md): reduccion
 * de una matriz compleja cuadrada a forma de Hessenberg superior por semejanza unitaria,
 * {@code A = Q*H*Q^H}, sin pasar nunca por el polinomio caracteristico.
 * <p>
 * Tres comprobaciones independientes por caso:
 * <ol>
 * <li>Reconstruccion {@code Q*H*Q^H = A} a precision de maquina.</li>
 * <li>{@code Q} unitaria: {@code Q*Q^H = I}.</li>
 * <li>{@code H} genuinamente Hessenberg superior: entradas con fila &gt; columna+1 nulas.</li>
 * </ol>
 * Casos: matrices densas reales/complejas 2x2..4x4, los bloques de Jordan defectuosos conjugados
 * por P no ortogonal ya usados para verificar {@code logm()} en la Novena sesion (mismo tipo de
 * caso adversario), una matriz ya en forma de Hessenberg (el reflector debe omitirse sin romper
 * nada), el caso trivial 1x1, y el rechazo explicito de una matriz no cuadrada.
 */
public class TestHessenberg01 {

	private static int pass = 0;
	private static int fail = 0;

	private static double maxAbsDiff(MatrixComplex a, MatrixComplex b) {
		double max = 0.0;
		for (int r = 0; r < a.rows(); ++r) {
			for (int c = 0; c < a.cols(); ++c) {
				Complex d = a.getItem(r, c).minus(b.getItem(r, c));
				max = Math.max(max, Math.max(Math.abs(d.rep()), Math.abs(d.imp())));
			}
		}
		return max;
	}

	private static double maxSubHessenbergEntry(MatrixComplex h) {
		double max = 0.0;
		for (int r = 0; r < h.rows(); ++r)
			for (int c = 0; c < h.cols(); ++c)
				if (r > c + 1)
					max = Math.max(max, h.getItem(r, c).mod());
		return max;
	}

	private static void report(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + " -- " + detail);
		if (ok) ++pass; else ++fail;
	}

	private static void checkFactorization(String label, MatrixComplex a, double tolerance) {
		Hessenbergfactor hf = new Hessenbergfactor(a);

		if (!hf.factorized()) {
			report(label, false, "factorize() did not set factorized=true");
			return;
		}

		MatrixComplex q = hf.getQ();
		MatrixComplex h = hf.getH();
		int n = a.rows();

		double reconDiff = maxAbsDiff(q.times(h).times(q.adjoint()), a);
		double unitaryDiff = maxAbsDiff(q.times(q.adjoint()), MatrixComplex.eye(n));
		double subHessenberg = maxSubHessenbergEntry(h);

		boolean ok = reconDiff < tolerance && unitaryDiff < tolerance && subHessenberg < tolerance;
		report(label, ok, "recon=" + reconDiff + " unitary=" + unitaryDiff + " subHessenberg=" + subHessenberg);
	}

	public static void main(String[] args) {
		Complex.setFixedOFF();

		checkFactorization("2x2 real dense", new MatrixComplex("4,1;2,3"), 1e-9);
		checkFactorization("3x3 real dense", new MatrixComplex("1,2,3;4,5,6;7,8,10"), 1e-9);
		checkFactorization("4x4 real dense", new MatrixComplex("4,1,2,0;0,3,1,2;1,0,5,1;2,1,0,3"), 1e-9);
		checkFactorization("3x3 complex dense", new MatrixComplex("1+2i,3,0;4,5-1i,2;1,0,3+1i"), 1e-9);

		// A = P*J*P^-1, P no ortogonal -- mismos casos adversarios usados para logm() en la Novena sesion.
		MatrixComplex jSingle = new MatrixComplex("-50,1;0,-50");
		MatrixComplex p2 = new MatrixComplex("2,1;1,1");
		checkFactorization("2x2 bloque de Jordan lambda=-50, P no ortogonal",
			p2.times(jSingle).times(p2.inverse()), 1e-6);

		MatrixComplex jMulti = new MatrixComplex("5,1,0;0,5,0;0,0,-2");
		MatrixComplex p = new MatrixComplex("1,2,0;0,1,1;1,0,1");
		checkFactorization("3x3 multi-bloque (2x2 lambda=5 + 1x1 lambda=-2), P no ortogonal",
			p.times(jMulti).times(p.inverse()), 1e-6);

		// Ya en forma de Hessenberg: el reflector de cada paso debe omitirse (subcolumna ya nula).
		checkFactorization("3x3 ya Hessenberg", new MatrixComplex("1,2,3;4,5,6;0,7,8"), 1e-9);

		// Caso trivial: 1x1, ningun paso se ejecuta (n-3 < 0).
		checkFactorization("1x1 trivial", new MatrixComplex("5"), 1e-9);

		try {
			new Hessenbergfactor(new MatrixComplex("1,2,3;4,5,6"));
			System.out.println("FAIL non-square matrix -- factorize() did not throw");
			++fail;
		} catch (IllegalArgumentException e) {
			System.out.println("OK   non-square matrix -- factorize() threw as expected: " + e.getMessage());
			++pass;
		}

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}

}
