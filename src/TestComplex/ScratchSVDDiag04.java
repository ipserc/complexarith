package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.factorization.SVDfactor;
import com.ipserc.arith.factorization.SVDfactor.SVDmethod;

public class ScratchSVDDiag04 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		MatrixComplex A = new MatrixComplex("1,2,3;2,4,6;1,1,1");
		for (int i = 0; i < 10; ++i) {
			SVDfactor svd = new SVDfactor(A, SVDmethod.SVD);
			MatrixComplex U = svd.getU();
			MatrixComplex UhU = U.adjoint().times(U);
			MatrixComplex eye = new MatrixComplex(UhU.rows()).eye();
			double unitaryErr = UhU.minus(eye).inf_norm();
			MatrixComplex recon = U.times(svd.getS()).times(svd.getV().adjoint());
			double reconErr = recon.minus(A).inf_norm();
			System.out.println("run " + i + ": factorized=" + svd.factorized() + " unitaryErr=" + unitaryErr + " reconErr=" + reconErr);
		}
	}
}
