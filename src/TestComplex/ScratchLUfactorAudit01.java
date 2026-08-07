package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.LUfactor;
import com.ipserc.arith.factorization.LUfactor.LUmethod;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression driver for the LUfactor.java audit fixes (Decimoctava sesion, continuacion, ver
 * Claude/ComplexArithRev.md, LUfactor.VERSION 1.5).
 */
public class ScratchLUfactorAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + (detail.isEmpty() ? "" : " (" + detail + ")"));
		if (ok) ++pass; else ++fail;
	}

	private static double maxAbsDiff(MatrixComplex a, MatrixComplex b) {
		double max = 0;
		for (int i = 0; i < a.rows(); ++i) {
			for (int j = 0; j < a.cols(); ++j) {
				max = Math.max(max, a.getItem(i, j).minus(b.getItem(i, j)).mod());
			}
		}
		return max;
	}

	public static void main(String[] args) {

		// --- Finding 1: constructor aliasing, fixed ---
		MatrixComplex original = new MatrixComplex("1,2;3,4");
		Complex before = original.getItem(0, 0).copy();
		LUfactor lu = new LUfactor(original);
		lu.setItem(0, 0, new Complex(999, 0));
		Complex after = original.getItem(0, 0);
		check("Finding 1: constructor no longer aliases the caller's matrix", after.equals(before.rep(), before.imp()), "");

		// --- Finding 2: CROUTfactorize() zero-pivot guard, must fall back to PIVOT cleanly ---
		LUfactor lu2 = new LUfactor("0,1;1,0"); // det=-1, well-conditioned, A[0][0]=0
		boolean noNaN = lu2.factorized() && !lu2.L().isNaN() && !lu2.L().isInfinite() && !lu2.U().isNaN() && !lu2.U().isInfinite();
		double recon2 = noNaN ? maxAbsDiff(lu2.L().times(lu2.U()), lu2.P().times(new MatrixComplex("0,1;1,0"))) : Double.NaN;
		check("Finding 2: [[0,1],[1,0]] factorizes cleanly via " + lu2.getMethodName() + ", L*U reconstructs P*A",
				noNaN && recon2 < 1e-9, "recon diff=" + recon2);

		// --- Finding 3: CHOLESKY, complex Hermitian PD 2x2 ---
		MatrixComplex hermitianPD = new MatrixComplex("2,1+1i;1-1i,3");
		LUfactor lu3 = new LUfactor(hermitianPD, LUmethod.CHOLESKY);
		double diffLLH = lu3.factorized() ? maxAbsDiff(lu3.L().times(lu3.L().adjoint()), hermitianPD) : Double.NaN;
		double diffLU = lu3.factorized() ? maxAbsDiff(lu3.L().times(lu3.U()), hermitianPD) : Double.NaN;
		check("Finding 3: complex Hermitian 2x2 CHOLESKY, L*L^H reconstructs A", lu3.factorized() && diffLLH < 1e-9, "diff=" + diffLLH);
		check("Finding 3: complex Hermitian 2x2 CHOLESKY, L*U (as returned) reconstructs A", lu3.factorized() && diffLU < 1e-9, "diff=" + diffLU);

		// --- Finding 3b (off-by-one in Step 6): 3x3 REAL symmetric PD matrix, isolates the loop
		// bound bug from the Hermitian-conjugation bug (invisible in the 2x2 case above, since
		// Step 3's loop never runs for a 2x2 matrix).
		MatrixComplex realPD3 = new MatrixComplex("4,2,-2;2,5,4;-2,4,14"); // real symmetric PD (leading minors 4,16,44 > 0)
		LUfactor lu4 = new LUfactor(realPD3, LUmethod.CHOLESKY);
		double diff3 = lu4.factorized() ? maxAbsDiff(lu4.L().times(lu4.L().adjoint()), realPD3) : Double.NaN;
		check("Finding 3b: real symmetric PD 3x3 CHOLESKY, L*L^H reconstructs A (Step 6 off-by-one)", lu4.factorized() && diff3 < 1e-9, "diff=" + diff3);

		// --- Finding 3c: complex Hermitian PD 3x3, exercises both the conjugation fix AND the
		// Step 6 loop-bound fix together.
		MatrixComplex hermitianPD3 = new MatrixComplex("4,1+1i,0-2i;1-1i,5,1+1i;0+2i,1-1i,6");
		LUfactor lu5 = new LUfactor(hermitianPD3, LUmethod.CHOLESKY);
		double diff5 = lu5.factorized() ? maxAbsDiff(lu5.L().times(lu5.L().adjoint()), hermitianPD3) : Double.NaN;
		check("Finding 3c: complex Hermitian PD 3x3 CHOLESKY, L*L^H reconstructs A", lu5.factorized() && diff5 < 1e-9, "diff=" + diff5);

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
