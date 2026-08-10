package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.factorization.Jordan;
import com.ipserc.arith.factorization.LUfactor;
import com.ipserc.arith.factorization.LUfactor.LUmethod;
import com.ipserc.arith.factorization.QRfactor;
import com.ipserc.arith.factorization.SVDfactor;
import com.ipserc.arith.factorization.SVDfactor.SVDmethod;
import com.ipserc.arith.factorization.Schurfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Auditoria matematica dedicada del resto de factorization/* (Vigesimosexta sesion, bloque 4 de
 * la hoja de ruta "Matematicas Aplicadas"): LUfactor, QRfactor, SVDfactor, Schurfactor,
 * Diagfactor, Jordan. Verifica las identidades de reconstruccion/definicion de cada
 * factorizacion -- no solo lectura de codigo.
 */
public class ScratchFactorizationAudit01 {

	static int pass = 0, fail = 0;

	static double maxAbsDiff(MatrixComplex a, MatrixComplex b) {
		if (a.rows() != b.rows() || a.cols() != b.cols()) return Double.POSITIVE_INFINITY;
		double max = 0;
		for (int r = 0; r < a.rows(); ++r)
			for (int c = 0; c < a.cols(); ++c) {
				Complex d = a.getItem(r, c).minus(b.getItem(r, c));
				max = Math.max(max, Math.max(Math.abs(d.rep()), Math.abs(d.imp())));
			}
		return max;
	}

	static void checkClose(String label, MatrixComplex actual, MatrixComplex expected, double tol) {
		double err = maxAbsDiff(actual, expected);
		boolean ok = err < tol;
		if (ok) ++pass; else ++fail;
		System.out.printf("%-65s %-4s maxAbsDiff=%.3e%n", label, ok ? "OK" : "***FAIL***", err);
	}

	static void checkTrue(String label, boolean cond) {
		if (cond) ++pass; else ++fail;
		System.out.printf("%-65s %s%n", label, cond ? "OK" : "***FAIL***");
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(6);
		double tol = 1e-6;

		System.out.println("=== LUfactor: A == L*U (CROUT, matriz bien condicionada) ===");
		MatrixComplex aLU = new MatrixComplex("4,3;6,3");
		LUfactor lu = new LUfactor(aLU);
		checkTrue("CROUT factorized()==true", lu.factorized());
		checkClose("A == L*U (CROUT)", aLU, lu.L().times(lu.U()), tol);

		System.out.println("\n=== LUfactor: PIVOT (A[0][0]=0, fuerza fallback), P*A == L*U ===");
		MatrixComplex aPivot = new MatrixComplex("0,1;1,0");
		LUfactor luP = new LUfactor(aPivot);
		checkTrue("factorized()==true (fallback a PIVOT)", luP.factorized());
		checkTrue("metodo usado == PIVOT", luP.getMethod() == LUmethod.PIVOT);
		checkClose("P*A == L*U (PIVOT)", luP.P().times(aPivot), luP.L().times(luP.U()), tol);

		System.out.println("\n=== LUfactor: CHOLESKY, Hermitiana compleja PD, A == L*L^H ===");
		MatrixComplex aChol = new MatrixComplex("4,2+1i;2-1i,3");
		LUfactor luChol = new LUfactor(aChol, LUmethod.CHOLESKY);
		checkTrue("CHOLESKY factorized()==true", luChol.factorized());
		checkClose("A == L*L^H (CHOLESKY, Hermitiana compleja)", aChol, luChol.L().times(luChol.L().adjoint()), tol);

		System.out.println("\n=== LUfactor: DOOLITTLE, A == L*U con L unitriangular ===");
		LUfactor luDoo = new LUfactor(aLU, LUmethod.DOOLITTLE);
		checkTrue("DOOLITTLE factorized()==true", luDoo.factorized());
		checkClose("A == L*U (DOOLITTLE)", aLU, luDoo.L().times(luDoo.U()), tol);
		checkTrue("DOOLITTLE: diagonal de L es 1 (unitriangular)",
				luDoo.L().getItem(0, 0).minus(Complex.ONE).mod() < tol && luDoo.L().getItem(1, 1).minus(Complex.ONE).mod() < tol);

		System.out.println("\n=== QRfactor: Q unitaria + A == Q*R, 5 metodos, matriz 3x2 ===");
		MatrixComplex aQR = new MatrixComplex("1,2;3,4;5,7");
		String[] qrMethods = {"qrHouseholder", "qrGramSchmidt", "qrGramSchmidtFull", "qrGramSchmidtM", "qrGramSchmidtMFull"};
		for (String m : qrMethods) {
			QRfactor qr = new QRfactor(aQR);
			switch (m) {
				case "qrHouseholder": qr.qrHouseholder(); break;
				case "qrGramSchmidt": qr.qrGramSchmidt(); break;
				case "qrGramSchmidtFull": qr.qrGramSchmidtFull(); break;
				case "qrGramSchmidtM": qr.qrGramSchmidtM(); break;
				case "qrGramSchmidtMFull": qr.qrGramSchmidtMFull(); break;
			}
			checkTrue(m + ": factorized()==true", qr.factorized());
			// "reduced" (non-Full) Gram-Schmidt variants deliberately give an economy-mode Q
			// (m x min(m,n), semi-unitary Q^H*Q=I) for a tall matrix -- isUnitary() requires a
			// SQUARE matrix by definition, so check Q^H*Q=I directly instead of via isUnitary().
			checkClose(m + ": Q^H*Q == I", qr.Q().adjoint().times(qr.Q()), MatrixComplex.eye(qr.Q().cols()), tol);
			checkClose(m + ": A == Q*R", aQR, qr.Q().times(qr.R()), tol);
		}

		System.out.println("\n=== Schurfactor: U unitaria + Schur triangular superior + A == U*Schur*U^H ===");
		MatrixComplex aSchur = new MatrixComplex("4,1;2,3");
		Schurfactor schur = new Schurfactor(aSchur);
		checkTrue("Schurfactor factorized()==true", schur.factorized());
		checkTrue("U unitaria (U^H*U==I)", schur.getU().isUnitary());
		checkTrue("Schur es triangular superior", schur.getSchur().getItem(1, 0).mod() < tol);
		checkClose("A == U*Schur*U^H", aSchur, schur.getU().times(schur.getSchur()).times(schur.getU().adjoint()), tol);

		System.out.println("\n=== SVDfactor: U,V unitarias + A == U*S*V^H, cuadrada y rango deficiente ===");
		MatrixComplex aSvdFull = new MatrixComplex("3,1;1,3");
		SVDfactor svd = new SVDfactor(aSvdFull);
		checkTrue("SVD (cuadrada) factorized()==true", svd.factorized());
		checkTrue("SVD: U unitaria", svd.getU().isUnitary());
		checkTrue("SVD: V unitaria", svd.getV().isUnitary());
		checkClose("SVD: A == U*S*V^H (cuadrada)", aSvdFull, svd.getU().times(svd.getS()).times(svd.getV().adjoint()), tol);

		// El caso exacto de repro documentado en SVDfactor VERSION 1.4 (rango 2, 3x3)
		MatrixComplex aSvdRankDef = new MatrixComplex("1,2,3;2,4,6;1,1,1");
		SVDfactor svdDef = new SVDfactor(aSvdRankDef, SVDmethod.SVD);
		checkTrue("SVD (rango deficiente) factorized()==true", svdDef.factorized());
		checkTrue("SVD rango deficiente: U unitaria", svdDef.getU().isUnitary());
		checkTrue("SVD rango deficiente: V unitaria", svdDef.getV().isUnitary());
		checkClose("SVD rango deficiente: A == U*S*V^H", aSvdRankDef, svdDef.getU().times(svdDef.getS()).times(svdDef.getV().adjoint()), tol);

		SVDfactor svdRed = new SVDfactor(aSvdRankDef, SVDmethod.REDUCED);
		checkTrue("REDUCED (rango deficiente) factorized()==true", svdRed.factorized());
		checkClose("REDUCED rango deficiente: A == U*S*V^H", aSvdRankDef, svdRed.getU().times(svdRed.getS()).times(svdRed.getV().adjoint()), tol);

		System.out.println("\n=== Diagfactor: A == P*D*P^-1, con AUTOVALOR REPETIDO (2,2,8) no diagonal ===");
		// A conocida: eigenvalue 8 (autovector (1,1,1)), eigenvalue 2 (multiplicidad geometrica 2)
		MatrixComplex aDiag = new MatrixComplex("4,2,2;2,4,2;2,2,4");
		Diagfactor diag = new Diagfactor(aDiag);
		checkTrue("Diagfactor factorized()==true (autovalor repetido)", diag.factorized());
		checkClose("A == P*D*P^-1 (autovalor repetido)", aDiag, diag.P().times(diag.D()).times(diag.P().inverse()), tol);

		System.out.println("\n=== Jordan: A == P*J*P^-1, matriz defectuosa conocida ===");
		MatrixComplex aJordan = new MatrixComplex("0,3,1;2,-1,-1;-2,-1,-1");
		Jordan jordan = new Jordan(aJordan);
		jordan.factorize();
		checkClose("A == P*J*P^-1 (defectuosa)", aJordan, jordan.P().times(jordan.J()).times(jordan.P().inverse()), 1e-3);

		System.out.println("\n===================================");
		System.out.println("TOTAL: " + pass + " OK, " + fail + " FAIL");
	}
}
