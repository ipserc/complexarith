package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.QRSchurfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for {@code QRSchurfactor} (Etapas 1-3 del plan QR-con-desplazamientos, ver
 * {@code Claude/ComplexArithRev.md} / plan mutable-rolling-stardust.md): iteracion QR CON
 * desplazamiento de Wilkinson y deflacion sobre la forma de Hessenberg (Etapa 1), obteniendo la
 * factorizacion de Schur {@code A = Q*T*Q^H} y los autovalores directamente de la diagonal de
 * {@code T}, sin formar nunca el polinomio caracteristico.
 * <p>
 * Cuatro comprobaciones por caso:
 * <ol>
 * <li>Reconstruccion {@code Q*T*Q^H = A} a precision de maquina.</li>
 * <li>{@code Q} unitaria: {@code Q*Q^H = I}.</li>
 * <li>{@code T} genuinamente triangular superior (no solo Hessenberg): entradas con fila &gt;
 * columna nulas.</li>
 * <li>El conjunto de autovalores de la diagonal de {@code T} coincide (sin importar el orden,
 * dentro de tolerancia) con el oracle conocido.</li>
 * </ol>
 * El caso de autovalores de modulo EXACTAMENTE igual (par {@code +-i} de una matriz de rotacion)
 * que la Etapa 2 (sin desplazar) dejaba como limitacion conocida (lanzaba excepcion tras la cota
 * de iteraciones) ahora CONVERGE limpio -- el desplazamiento de Wilkinson se calcula localmente
 * del bloque 2x2 final, no depende de la separacion global de modulos del espectro. Incluye
 * ademas los mismos casos de bloques de Jordan defectuosos (autovalor REPETIDO, no solo modulo
 * igual) conjugados por P no ortogonal ya usados para verificar {@code logm()} en la Novena
 * sesion, con tolerancia relajada acorde al "techo de precision" ya documentado para autovalores
 * repetidos (inherente a cualquier metodo, no un bug de esta clase).
 */
public class TestQRSchur01 {

	private static int pass = 0;
	private static int fail = 0;

	private static double maxAbsDiff(MatrixComplex a, MatrixComplex b) {
		double max = 0.0;
		for (int r = 0; r < a.rows(); ++r)
			for (int c = 0; c < a.cols(); ++c) {
				Complex d = a.getItem(r, c).minus(b.getItem(r, c));
				max = Math.max(max, Math.max(Math.abs(d.rep()), Math.abs(d.imp())));
			}
		return max;
	}

	private static double maxLowerTriangularEntry(MatrixComplex t) {
		double max = 0.0;
		for (int r = 0; r < t.rows(); ++r)
			for (int c = 0; c < t.cols(); ++c)
				if (r > c) max = Math.max(max, t.getItem(r, c).mod());
		return max;
	}

	private static boolean matchesEigenvalueSet(MatrixComplex actual, Complex[] expected, double tolerance) {
		int n = expected.length;
		if (actual.rows() != n) return false;
		boolean[] used = new boolean[n];
		for (int i = 0; i < n; ++i) {
			Complex a = actual.getItem(i, 0);
			int bestIdx = -1;
			double bestDist = Double.MAX_VALUE;
			for (int j = 0; j < n; ++j) {
				if (used[j]) continue;
				double dist = a.minus(expected[j]).mod();
				if (dist < bestDist) { bestDist = dist; bestIdx = j; }
			}
			if (bestIdx == -1 || bestDist > tolerance) return false;
			used[bestIdx] = true;
		}
		return true;
	}

	private static void report(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + " -- " + detail);
		if (ok) ++pass; else ++fail;
	}

	private static void checkFactorization(String label, MatrixComplex a, Complex[] expectedEigenvalues, double tolerance) {
		QRSchurfactor qs = new QRSchurfactor(a);

		if (!qs.factorized()) {
			report(label, false, "factorize() did not set factorized=true");
			return;
		}

		MatrixComplex q = qs.getQ();
		MatrixComplex t = qs.getSchur();
		int n = a.rows();

		double reconDiff = maxAbsDiff(q.times(t).times(q.adjoint()), a);
		double unitaryDiff = maxAbsDiff(q.times(q.adjoint()), MatrixComplex.eye(n));
		double lowerTriangular = maxLowerTriangularEntry(t);
		boolean eigenMatch = matchesEigenvalueSet(qs.getEigenvalues(), expectedEigenvalues, tolerance);

		boolean ok = reconDiff < tolerance && unitaryDiff < tolerance && lowerTriangular < tolerance && eigenMatch;
		report(label, ok, "recon=" + reconDiff + " unitary=" + unitaryDiff + " lowerTri=" + lowerTriangular + " eigenMatch=" + eigenMatch);
	}

	public static void main(String[] args) {
		Complex.setFixedOFF();

		checkFactorization("3x3 diagonal (0 pasos QR)", new MatrixComplex("2,0,0;0,3,0;0,0,5"),
			new Complex[]{new Complex(2, 0), new Complex(3, 0), new Complex(5, 0)}, 1e-9);

		checkFactorization("2x2 real distinct eigenvalues", new MatrixComplex("4,1;2,3"),
			new Complex[]{new Complex(5, 0), new Complex(2, 0)}, 1e-9);

		// A = P*diag(1,2,6)*P^-1, P no ortogonal -- oracle limpio de autovalores conocidos.
		MatrixComplex p3 = new MatrixComplex("1,2,0;0,1,1;1,0,1");
		MatrixComplex diag3 = new MatrixComplex("1,0,0;0,2,0;0,0,6");
		checkFactorization("3x3 real distinct eigenvalues (P no ortogonal)",
			p3.times(diag3).times(p3.inverse()),
			new Complex[]{new Complex(1, 0), new Complex(2, 0), new Complex(6, 0)}, 1e-9);

		// A = P*diag(1+1i,3-2i)*P^-1, autovalores complejos de modulo distinto.
		MatrixComplex p2 = new MatrixComplex("2,1;1,1");
		MatrixComplex diag2 = new MatrixComplex("1+1i,0;0,3-2i");
		checkFactorization("2x2 complex distinct-modulus eigenvalues (P no ortogonal)",
			p2.times(diag2).times(p2.inverse()),
			new Complex[]{new Complex(1, 1), new Complex(3, -2)}, 1e-9);

		// A = P*diag(1,2,3,4)*P^-1, 4x4, autovalores reales bien separados.
		MatrixComplex p4 = new MatrixComplex("1,2,0,1;0,1,3,0;1,0,1,2;0,1,0,1");
		MatrixComplex diag4 = new MatrixComplex("1,0,0,0;0,2,0,0;0,0,3,0;0,0,0,4");
		checkFactorization("4x4 real distinct eigenvalues (P no ortogonal)",
			p4.times(diag4).times(p4.inverse()),
			new Complex[]{new Complex(1, 0), new Complex(2, 0), new Complex(3, 0), new Complex(4, 0)}, 1e-9);

		// Autovalores +-i, modulo exactamente igual -- la Etapa 2 (sin desplazar) no convergia aqui;
		// el desplazamiento de Wilkinson (local al bloque 2x2) lo resuelve.
		checkFactorization("2x2 equal-modulus eigenvalues (+-i, rotation matrix)", new MatrixComplex("0,-1;1,0"),
			new Complex[]{new Complex(0, 1), new Complex(0, -1)}, 1e-9);

		// Bloques de Jordan defectuosos (autovalor REPETIDO, no solo modulo igual), P no ortogonal --
		// mismos casos adversarios usados para verificar logm() en la Novena sesion. Tolerancia
		// relajada acorde al "techo de precision" ya documentado para autovalores repetidos.
		MatrixComplex jSingle = new MatrixComplex("-50,1;0,-50");
		MatrixComplex pSingle = new MatrixComplex("2,1;1,1");
		// Tolerancia 1e-5, no 1e-9 como los casos de autovalores distintos: techo de precision ya
		// documentado (Novena sesion) para autovalores repetidos, inherente a cualquier metodo --
		// confirmado aqui tambien para QRSchurfactor (residuo medido ~1.2e-6 a 2.4e-6 en el
		// multi-bloque, por debajo del limite pero por encima de la precision de maquina).
		checkFactorization("2x2 bloque de Jordan lambda=-50 (repetido), P no ortogonal",
			pSingle.times(jSingle).times(pSingle.inverse()),
			new Complex[]{new Complex(-50, 0), new Complex(-50, 0)}, 1e-5);

		MatrixComplex jMulti = new MatrixComplex("5,1,0;0,5,0;0,0,-2");
		MatrixComplex pMulti = new MatrixComplex("1,2,0;0,1,1;1,0,1");
		checkFactorization("3x3 multi-bloque (2x2 lambda=5 repetido + 1x1 lambda=-2), P no ortogonal",
			pMulti.times(jMulti).times(pMulti.inverse()),
			new Complex[]{new Complex(5, 0), new Complex(5, 0), new Complex(-2, 0)}, 1e-5);

		try {
			new QRSchurfactor(new MatrixComplex("1,2,3;4,5,6"));
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
