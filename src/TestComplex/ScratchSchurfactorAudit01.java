package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Schurfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Deep audit driver for com.ipserc.arith.factorization.Schurfactor (Decimoctava sesion,
 * continuacion, ver Claude/ComplexArithRev.md). Checks the CORE algorithm's correctness
 * (reconstruction U*T*U* == original, T upper triangular) across several real/complex matrices,
 * separate from the aliasing bug already confirmed in ScratchSchurfactorAliasCheck01.java.
 * Read-only reconnaissance, no production code touched.
 */
public class ScratchSchurfactorAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static boolean isUpperTriangular(MatrixComplex t, double tol) {
		for (int i = 1; i < t.rows(); ++i) {
			for (int j = 0; j < i; ++j) {
				if (t.getItem(i, j).mod() > tol) return false;
			}
		}
		return true;
	}

	private static void checkOne(String label, MatrixComplex m) {
		Schurfactor sf = new Schurfactor(m);
		if (!sf.factorized()) {
			System.out.println("FAIL " + label + ": did not factorize");
			++fail;
			return;
		}
		MatrixComplex u = sf.getU();
		MatrixComplex t = sf.getSchur();
		MatrixComplex reconstructed = u.times(t).times(u.adjoint());

		double maxDiff = 0;
		for (int i = 0; i < m.rows(); ++i) {
			for (int j = 0; j < m.cols(); ++j) {
				maxDiff = Math.max(maxDiff, reconstructed.getItem(i, j).minus(m.getItem(i, j)).mod());
			}
		}
		boolean triOk = isUpperTriangular(t, 1e-9);
		boolean reconOk = maxDiff < 1e-9;
		System.out.println((reconOk && triOk ? "OK   " : "FAIL ") + label
				+ " reconstruction maxDiff=" + maxDiff + " upperTriangular=" + triOk);
		if (reconOk && triOk) ++pass; else ++fail;
	}

	public static void main(String[] args) {
		checkOne("2x2 real, distinct eigenvalues", new MatrixComplex("2,1;0,3"));
		checkOne("2x2 real, non-triangular input", new MatrixComplex("4,1;2,3"));
		checkOne("3x3 real, general", new MatrixComplex("2,0,0;0,3,4;0,4,9"));
		checkOne("3x3 real, non-symmetric", new MatrixComplex("1,2,3;0,1,4;5,6,0"));
		checkOne("2x2 complex", new MatrixComplex("2+1i,1;0,3-2i"));
		checkOne("4x4 real", new MatrixComplex("4,1,0,0;0,3,1,0;0,0,2,1;0,0,0,5"));
		checkOne("1x1 trivial", new MatrixComplex("7"));

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
