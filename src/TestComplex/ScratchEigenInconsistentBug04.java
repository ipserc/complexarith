package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ScratchEigenInconsistentBug04 {
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
		MatrixComplex I = MatrixComplex.eye(aMatrix.rows());

		for (int i = 0; i < eigenSpace.eigenvalues().rows(); ++i) {
			Complex eVal = eigenSpace.eigenvalues().getItem(i, 0);
			MatrixComplex cMatrix = aMatrix.minus(I.times(eVal));
			System.out.println("eigenval[" + i + "] = " + eVal
					+ "  rankNearSingular=" + cMatrix.rankNearSingular() + "/" + cMatrix.rows());
			MatrixComplex ns = cMatrix.nullspaceBasisNearSingular();
			ns.println("  nullspaceBasisNearSingular():");
			// Check A*v == lambda*v for the first nullspace vector, normalized so last comp = 1
			if (ns.rows() > 0) {
				MatrixComplex v = ns.getRow(0).transpose();
				Complex last = v.getItem(v.rows()-1, 0);
				MatrixComplex vNorm = v.divides(last);
				MatrixComplex Av = aMatrix.times(vNorm);
				MatrixComplex lv = vNorm.times(eVal);
				System.out.println("  vNorm  = " + vNorm.transpose().getRow(0));
				System.out.println("  A*v    = " + Av.transpose().getRow(0));
				System.out.println("  lambda*v = " + lv.transpose().getRow(0));
			}
		}
	}
}
