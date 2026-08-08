package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ScratchEigenGeomMultBug01 {
	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedON(6);
		Complex.exact(true);

		MatrixComplex aMatrix = new MatrixComplex(
				"+5,+5,+3,+4,+4,+2,+1;" +
				"+3,-3,+4,-5,+2,+3,+2;" +
				"+3,+4,+2,+3,-2,+1,-4;" +
				"-4,+2,-5,-2,+2,-5,-2;" +
				"+1,+5,-5,-1,+5,-3,-3;" +
				"+1,+4,-4,+3,+4,-2,-5;" +
				"-4,+1,+1,+1,+2,+2,+2");

		Eigenspace eigenSpace = new Eigenspace(new Complex(1, 0), aMatrix);

		Complex badEigVal = null;
		for (int i = 0; i < eigenSpace.eigenvalues().rows(); ++i) {
			Complex eVal = eigenSpace.eigenvalues().getItem(i, 0);
			int geom = eigenSpace.geometricMultiplicity(eVal);
			System.out.println("EigenValue: " + eVal.toString() + " - geom mult:" + geom);
			if (geom == 0) badEigVal = eVal;
		}

		if (badEigVal == null) {
			System.out.println("No geom-mult-0 eigenvalue found in this run -- can't reproduce.");
			return;
		}

		System.out.println("\n================ INVESTIGATING badEigVal = " + badEigVal.toString() + " ================");

		MatrixComplex I = MatrixComplex.eye(aMatrix.rows());
		MatrixComplex cMatrix = aMatrix.minus(I.times(badEigVal));

		Complex.setFixedOFF();
		System.out.println("cMatrix (A - lambda*I), FULL PRECISION:");
		cMatrix.println("cMatrix:");

		System.out.println("\ncMatrix.determinant() = " + cMatrix.determinant());
		System.out.println("cMatrix.determinant().equals(0,0) = " + cMatrix.determinant().equals(0, 0));

		System.out.println("\ncMatrix.rank()  (used by typeEqSys/geometricMultiplicity) = " + cMatrix.rank());
		System.out.println("cMatrix.rank0() (brute force, ground truth)                = " + cMatrix.rank0());
		System.out.println("cMatrix.rank1() (same as rank())                           = " + cMatrix.rank1());

		System.out.println("\ncMatrix.triangleUp() diag (single-pass elimination, feeds determinant()):");
		MatrixComplex tri = cMatrix.triangleUp();
		for (int i = 0; i < tri.rows(); ++i) {
			System.out.println("  diag[" + i + "][" + i + "] = " + tri.getItem(i, i));
		}

		System.out.println("\ncMatrix.inverse() (what dividesleft()/solveGauss() DETERMINATE branch calls):");
		MatrixComplex inv = cMatrix.inverse();
		inv.println("inverse:");
	}
}
