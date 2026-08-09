package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.syseq.Syseq;

/**
 * Calibration for the eigenvectors3() bug found this session: for an INDETERMINATE
 * characteristic system, MatrixComplexEquationSystems.solveGauss() decides which row is the
 * "free variable" via a FIXED absolute epsilon on the triangularized pivot -- if the
 * eigenvalue's own residual imprecision leaves that pivot just above the threshold, no row gets
 * seeded and the whole back-substitution silently returns the trivial all-zero vector (which
 * passes checkSingleSol() because 0 trivially solves any homogeneous system).
 *
 * Measures, across many synthetic cases with a KNOWN true eigenvector, whether replacing the
 * CURRENT narrow bypass in Eigenspace.eigenvectors3() --
 *   (typeEqSys()==DETERMINATE && rankNearSingular()<rows) -> nullspaceBasisNearSingular()
 * -- with a BROADENED one --
 *   (rankNearSingular()<rows) -> nullspaceBasisNearSingular(), for ANY typeEqSys() classification
 * -- fixes the bug without introducing any NEW wrong case (specifically: defective/Jordan
 * eigenvalues, where geometric multiplicity < arithmetic multiplicity, are the most likely place
 * a naive "always trust the nullspace" change could misbehave).
 */
public class ScratchEigenvectorsIndeterminateCalib01 {

	static final int DETERMINATE = MatrixComplex.DETERMINATE;

	/** Returns the eigenvector candidate via the CURRENT (narrow) bypass condition. */
	static MatrixComplex oldWay(MatrixComplex cMatrix) {
		try {
			Syseq dMatrix = new Syseq(cMatrix.augment().heap());
			if (dMatrix.typeEqSys() == DETERMINATE && cMatrix.rankNearSingular() < cMatrix.rows())
				return cMatrix.nullspaceBasisNearSingular();
			else
				return dMatrix.solution(1);
		} catch (IllegalArgumentException e) {
			return null; // signals EXCEPTION to the caller
		}
	}

	/** Returns the eigenvector candidate via the BROADENED bypass condition. */
	static MatrixComplex newWay(MatrixComplex cMatrix) {
		try {
			if (cMatrix.rankNearSingular() < cMatrix.rows())
				return cMatrix.nullspaceBasisNearSingular();
			else
				return new Syseq(cMatrix.augment().heap()).solution(1);
		} catch (IllegalArgumentException e) {
			return null; // signals EXCEPTION to the caller
		}
	}

	/** Normalizes a row vector so its largest-magnitude component is 1 (scale/phase-robust compare). */
	static Complex[] normalize(Complex[] row) {
		int best = 0;
		double bestMod = 0;
		for (int i = 0; i < row.length; ++i) {
			double m = row[i].mod();
			if (m > bestMod) { bestMod = m; best = i; }
		}
		Complex[] out = new Complex[row.length];
		for (int i = 0; i < row.length; ++i) out[i] = row[i].divides(row[best]);
		return out;
	}

	static double maxDiff(Complex[] a, Complex[] b) {
		double max = 0;
		for (int i = 0; i < a.length; ++i) max = Math.max(max, a[i].minus(b[i]).mod());
		return max;
	}

	static boolean isAllZero(MatrixComplex m) {
		return m.rows() == 0 || m.isNull();
	}

	/**
	 * Runs one case: A (n x n), a candidate eigenvalue (possibly perturbed), and the TRUE
	 * eigenvector (as constructed). Reports OLD vs NEW correctness.
	 */
	static void runCase(String label, MatrixComplex A, Complex lambda, Complex[] trueVec) {
		MatrixComplex I = MatrixComplex.eye(A.rows());
		MatrixComplex cMatrix = A.minus(I.times(lambda));

		MatrixComplex oldRes = oldWay(cMatrix);
		MatrixComplex newRes = newWay(cMatrix);

		Complex[] trueNorm = normalize(trueVec);

		String oldVerdict, newVerdict;
		if (oldRes == null) oldVerdict = "EXCEPTION(WRONG)";
		else if (isAllZero(oldRes)) oldVerdict = "ZERO(WRONG)";
		else {
			double d = maxDiff(normalize(oldRes.getRow(0).complexMatrix[0]), trueNorm);
			oldVerdict = (d < 1e-4 ? "OK" : "OFF(diff=" + d + ")");
		}
		if (newRes == null) newVerdict = "EXCEPTION(WRONG)";
		else if (isAllZero(newRes)) newVerdict = "ZERO(WRONG)";
		else {
			double d = maxDiff(normalize(newRes.getRow(0).complexMatrix[0]), trueNorm);
			newVerdict = (d < 1e-4 ? "OK" : "OFF(diff=" + d + ")");
		}

		String flag = oldVerdict.equals(newVerdict) ? "" : "  <<<< DIFFERS";
		System.out.println(label + "  OLD=" + oldVerdict + "  NEW=" + newVerdict + flag);
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();

		System.out.println("=== (1) Simple eigenvalues, real, several scales, EXACT lambda ===");
		double[] scales = {1, 8, 30, 200};
		for (double scale : scales) {
			MatrixComplex P = new MatrixComplex(5);
			P.initMatrixRandomRec((int) Math.max(2, scale));
			MatrixComplex D = MatrixComplex.eye(5);
			double[] vals = {3.0, 1.5, -0.7, 2.2, -0.9};
			for (int i = 0; i < 5; ++i) D.setItem(i, i, new Complex(scale * vals[i], 0));
			MatrixComplex A = P.times(D).times(P.inverse());
			for (int i = 0; i < 5; ++i) {
				Complex lambda = new Complex(scale * vals[i], 0);
				Complex[] trueVec = P.getCol(i).transpose().complexMatrix[0];
				runCase("scale=" + scale + " idx=" + i + " lambda=" + lambda, A, lambda, trueVec);
			}
		}

		System.out.println("\n=== (2) Simple eigenvalues, PERTURBED lambda straddling the ~1e-11 threshold ===");
		double[] relErrors = {1e-13, 1e-12, 1e-11, 3e-11, 1e-10, 1e-9};
		for (double scale : scales) {
			MatrixComplex P = new MatrixComplex(5);
			P.initMatrixRandomRec((int) Math.max(2, scale));
			MatrixComplex D = MatrixComplex.eye(5);
			double[] vals = {3.0, 1.5, -0.7, 2.2, -0.9};
			for (int i = 0; i < 5; ++i) D.setItem(i, i, new Complex(scale * vals[i], 0));
			MatrixComplex A = P.times(D).times(P.inverse());
			for (double relErr : relErrors) {
				int i = 0; // just perturb the first eigenvalue
				double trueLambda = scale * vals[i];
				double perturbed = trueLambda * (1 + relErr);
				Complex[] trueVec = P.getCol(i).transpose().complexMatrix[0];
				runCase("scale=" + scale + " relErr=" + relErr, A, new Complex(perturbed, 0), trueVec);
			}
		}

		System.out.println("\n=== (3) COMPLEX CONJUGATE PAIR, several scales, PERTURBED asymmetrically (mimics QRSchurfactor) ===");
		for (double scale : scales) {
			MatrixComplex P = new MatrixComplex(5);
			P.initMatrixRandomRec((int) Math.max(2, scale));
			MatrixComplex D = MatrixComplex.eye(5);
			Complex lamPlus = new Complex(0.01 * scale, 1.3 * scale);
			Complex lamMinus = lamPlus.conjugate();
			D.setItem(0, 0, lamPlus);
			D.setItem(1, 1, lamMinus);
			D.setItem(2, 2, new Complex(0.5 * scale, 0));
			D.setItem(3, 3, new Complex(-1.1 * scale, 0));
			D.setItem(4, 4, new Complex(2.0 * scale, 0));
			// Need P to be complex-safe for a real A with a complex-conjugate eigenpair:
			// build A directly via P*D*P^-1 (works over C regardless of P's realness for this probe).
			MatrixComplex A = P.times(D).times(P.inverse());
			for (double relErr : new double[] {2e-11, -2e-11, 5e-12, -5e-12}) {
				Complex trueVecPlus[] = P.getCol(0).transpose().complexMatrix[0];
				Complex perturbedPlus = new Complex(lamPlus.rep() * (1 + relErr), lamPlus.imp() * (1 + relErr));
				runCase("scale=" + scale + " lam+ relErr=" + relErr, A, perturbedPlus, trueVecPlus);
			}
		}

		System.out.println("\n=== (4) DEFECTIVE (Jordan block, geom mult 1 < arith mult), single block sizes 2 and 3 ===");
		for (int blockSize : new int[] {2, 3}) {
			for (double scale : scales) {
				int n = blockSize + 2; // pad with 2 extra simple eigenvalues to keep P invertible-ish and n>=4
				MatrixComplex P = new MatrixComplex(n);
				P.initMatrixRandomRec((int) Math.max(2, scale));
				MatrixComplex J = MatrixComplex.eye(n);
				double lamVal = 1.7 * scale;
				for (int i = 0; i < blockSize; ++i) J.setItem(i, i, new Complex(lamVal, 0));
				for (int i = 0; i < blockSize - 1; ++i) J.setItem(i, i + 1, Complex.ONE);
				for (int i = blockSize; i < n; ++i) J.setItem(i, i, new Complex(scale * (0.3 + i), 0));
				MatrixComplex A = P.times(J).times(P.inverse());
				// True eigenvector for the defective eigenvalue is the FIRST column of P
				// restricted to the Jordan block's leading vector -- P's first column overall.
				Complex[] trueVec = P.getCol(0).transpose().complexMatrix[0];
				runCase("blockSize=" + blockSize + " scale=" + scale, A, new Complex(lamVal, 0), trueVec);
			}
		}

		System.out.println("\n=== (5) REAL FAILING CASE from this session (exact reproduction) ===");
		{
			MatrixComplex A = new MatrixComplex(
					"-1,3,6,-2,3;" +
					"2,-1,-1,2,-1;" +
					"-5,-1,-2,-5,-1;" +
					"3,0,-1,4,-2;" +
					"-1,-1,-2,-5,-2");
			com.ipserc.arith.matrixcomplex.Eigenspace es = new com.ipserc.arith.matrixcomplex.Eigenspace(Complex.ONE, A);
			for (int i = 0; i < es.eigenvalues().rows(); ++i) {
				Complex eVal = es.eigenvalues().getItem(i, 0);
				MatrixComplex cMatrix = A.minus(MatrixComplex.eye(5).times(eVal));
				MatrixComplex oldRes = oldWay(cMatrix);
				MatrixComplex newRes = newWay(cMatrix);
				System.out.println("eigval=" + eVal
						+ "  OLD=" + (oldRes == null ? "EXCEPTION" : isAllZero(oldRes) ? "ZERO" : "nonzero")
						+ "  NEW=" + (newRes == null ? "EXCEPTION" : isAllZero(newRes) ? "ZERO" : "nonzero"));
			}
		}
	}
}
