package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ScratchSVDDiag01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		try {
			MatrixComplex A = new MatrixComplex("1,2,3;2,4,6;1,1,1");
			MatrixComplex AAT = A.adjoint().times(A);
			System.out.println("colLen (expected rows for solutions()) = " + A.cols());

			Eigenspace.setOrderDOWN();
			Eigenspace.setNormalize(true);
			Eigenspace eig = new Eigenspace(AAT);

			eig.roots().println("roots()");
			System.out.println("solutions().rows()=" + eig.solutions().rows() + " solutions().cols()=" + eig.solutions().cols());
			eig.solutions().println("solutions()");
			for (int i = 0; i < eig.solutions().rows(); ++i) {
				System.out.println("row " + i + " isNullRow=" + eig.solutions().isNullRow(i));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
