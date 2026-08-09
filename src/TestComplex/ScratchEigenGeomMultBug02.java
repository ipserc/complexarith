package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.factorization.Diagfactor;

/**
 * Follow-up to ScratchEigenGeomMultBug01.java: end-to-end check that the eigenvector for the
 * previously-broken eigenvalue (-2.676946) is now a real, finite vector (not NaN) and that
 * checkEigenvectors()/Diagfactor behave sanely, after the relative-singularity fix in
 * MatrixComplexUnary.inverse()/MatrixComplexRank.rank1() (8 agosto 2026).
 */
public class ScratchEigenGeomMultBug02 {
	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedON(4);

		MatrixComplex aMatrix = new MatrixComplex(
				"+5,+5,+3,+4,+4,+2,+1;" +
				"+3,-3,+4,-5,+2,+3,+2;" +
				"+3,+4,+2,+3,-2,+1,-4;" +
				"-4,+2,-5,-2,+2,-5,-2;" +
				"+1,+5,-5,-1,+5,-3,-3;" +
				"+1,+4,-4,+3,+4,-2,-5;" +
				"-4,+1,+1,+1,+2,+2,+2");

		Eigenspace eigenSpace = new Eigenspace(new Complex(1, 0), aMatrix);

		for (int row = 0; row < eigenSpace.solutions().rows(); ++row) {
			System.out.println("root: " + eigenSpace.root(row).getItem(0,0) + " - solution: " + eigenSpace.solution(row).getRow(0) + " - Is eigenvector: " + (eigenSpace.solution(row).isNull() ? "No" : "Yes"));
		}

		eigenSpace.eigenvectors().println("EigenVectors:");
		System.out.println("EigenVectors - Determinant:" + eigenSpace.eigenvectors().determinant());

		eigenSpace.checkEigenvectors();

		Diagfactor diagonal = new Diagfactor(aMatrix);
		System.out.println("Diagonalizable: " + diagonal.factorized());
	}
}
