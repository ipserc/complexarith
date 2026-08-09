package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.syseq.Syseq;

public class ScratchEigenInconsistentBug01 {
	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedON(6);

		MatrixComplex aMatrix = new MatrixComplex(
				"-1,3,6,-2,3;" +
				"2,-1,-1,2,-1;" +
				"-5,-1,-2,-5,-1;" +
				"3,0,-1,4,-2;" +
				"-1,-1,-2,-5,-2");

		Eigenspace eigenSpace = new Eigenspace(new Complex(1, 0), aMatrix);

		System.out.println("EigenValues:");
		for (int i = 0; i < eigenSpace.eigenvalues().rows(); ++i) {
			Complex eVal = eigenSpace.eigenvalues().getItem(i, 0);
			System.out.println("  [" + i + "] " + eVal.toString());
		}

		MatrixComplex I = MatrixComplex.eye(aMatrix.rows());

		for (int i = 0; i < eigenSpace.eigenvalues().rows(); ++i) {
			Complex eVal = eigenSpace.eigenvalues().getItem(i, 0);
			MatrixComplex cMatrix = aMatrix.minus(I.times(eVal));
			Syseq dMatrix = new Syseq(cMatrix.augment().heap());

			System.out.println("\n================ eigenval[" + i + "] = " + eVal.toString() + " ================");
			System.out.println("cMatrix.rank()             = " + cMatrix.rank());
			System.out.println("cMatrix.rankNearSingular()  = " + cMatrix.rankNearSingular());
			System.out.println("dMatrix (augmented).rank()  = " + dMatrix.rank());
			System.out.println("dMatrix.typeEqSys()         = " + dMatrix.typeEqSys()
					+ " (INCONSISTENT=" + MatrixComplex.INCONSISTENT
					+ " INDETERMINATE=" + MatrixComplex.INDETERMINATE
					+ " DETERMINATE=" + MatrixComplex.DETERMINATE + ")");

			Complex.setFixedOFF();
			System.out.println("cMatrix triangleUp() diag pivots (coef, full precision):");
			MatrixComplex triCoef = cMatrix.triangleUp();
			for (int r = 0; r < triCoef.rows(); ++r) {
				System.out.println("  coef diag[" + r + "] = " + triCoef.getItem(r, r) + "  |mod|=" + triCoef.getItem(r, r).mod());
			}
			System.out.println("dMatrix (augmented) triangleUp() diag pivots, full precision:");
			MatrixComplex triAug = dMatrix.triangleUp();
			for (int r = 0; r < triAug.rows(); ++r) {
				System.out.println("  aug diag[" + r + "] = " + triAug.getItem(r, r) + "  |mod|=" + triAug.getItem(r, r).mod());
			}
			Complex.setFixedON(6);
		}
	}
}
