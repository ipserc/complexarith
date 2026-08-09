package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.syseq.Syseq;

public class ScratchEigenInconsistentBug03 {
	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedOFF();

		MatrixComplex aMatrix = new MatrixComplex(
				"-1,3,6,-2,3;" +
				"2,-1,-1,2,-1;" +
				"-5,-1,-2,-5,-1;" +
				"3,0,-1,4,-2;" +
				"-1,-1,-2,-5,-2");

		Eigenspace eigenSpace = new Eigenspace(new Complex(1, 0), aMatrix);
		Complex eVal = eigenSpace.eigenvalues().getItem(0, 0);
		System.out.println("Investigating eigenval[0] = " + eVal);

		MatrixComplex I = MatrixComplex.eye(aMatrix.rows());
		MatrixComplex cMatrix = aMatrix.minus(I.times(eVal));
		Syseq dMatrix = new Syseq(cMatrix.augment().heap());

		MatrixComplex.debugON();
		MatrixComplex sol = dMatrix.solution(1);
		MatrixComplex.debugOFF();

		System.out.println("\nFinal solution row (post-check): " + sol.getRow(0));

		// Manually replicate checkSingleSol()'s residual using the CONJUGATE of the row 3
		// (eigenval -3.8358i) solution as the candidate -- since the matrix is real, the true
		// eigenvector for the conjugate eigenvalue must be the conjugate of that one.
		Complex[] candidate = new Complex[] {
			new Complex(2.2686, 3.4538),
			new Complex(0.5966, -1.4801),
			new Complex(-2.6040, 2.1010),
			new Complex(0.0677, -2.0023),
			new Complex(1.0, 0)
		};
		MatrixComplex candRow = new MatrixComplex(1, 5);
		for (int c = 0; c < 5; ++c) candRow.setItem(0, c, candidate[c]);

		MatrixComplex residual = cMatrix.times(candRow.transpose());
		System.out.println("\nResidual of conjugate-guess candidate against cMatrix (A-lambda*I)*x:");
		residual.println("residual:");
		System.out.println("residual.isNull() = " + residual.isNull());
		double maxMod = 0;
		for (int r = 0; r < residual.rows(); ++r) maxMod = Math.max(maxMod, residual.getItem(r,0).mod());
		System.out.println("max |residual component| = " + maxMod);
		System.out.println("Complex.zero_treshold_exact() = " + Complex.zero_treshold_exact());
	}
}
