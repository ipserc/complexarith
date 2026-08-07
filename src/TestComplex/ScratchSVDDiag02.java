package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.factorization.SVDfactor;

public class ScratchSVDDiag02 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		try {
			SVDfactor.debugON();
			MatrixComplex A = new MatrixComplex("1,2,3;2,4,6;1,1,1");
			SVDfactor svd = new SVDfactor(A);
			System.out.println("factorized=" + svd.factorized());
			MatrixComplex U = svd.getU();
			U.println("U");
			MatrixComplex UhU = U.adjoint().times(U);
			UhU.println("U^H*U (should be I)");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
