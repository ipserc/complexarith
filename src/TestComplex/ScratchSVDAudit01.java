package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.factorization.SVDfactor;
import com.ipserc.arith.factorization.SVDfactor.SVDmethod;

public class ScratchSVDAudit01 {
	static int pass = 0, fail = 0;

	static void check(String label, MatrixComplex A) {
		try {
			SVDfactor svd = new SVDfactor(A);
			MatrixComplex U = svd.getU();
			MatrixComplex S = svd.getS();
			MatrixComplex V = svd.getV();

			MatrixComplex recon = U.times(S).times(V.adjoint());
			double reconErr = recon.minus(A).inf_norm();

			MatrixComplex Iu = U.adjoint().times(U);
			MatrixComplex eyeU = new MatrixComplex(Iu.rows()).eye();
			double unitaryUerr = Iu.minus(eyeU).inf_norm();

			MatrixComplex Iv = V.adjoint().times(V);
			MatrixComplex eyeV = new MatrixComplex(Iv.rows()).eye();
			double unitaryVerr = Iv.minus(eyeV).inf_norm();

			boolean shapeOk = (U.rows() == A.rows() && U.cols() == A.rows())
				&& (V.rows() == A.cols() && V.cols() == A.cols())
				&& (S.rows() == A.rows() && S.cols() == A.cols());

			boolean ok = svd.factorized() && reconErr < 1e-8 && unitaryUerr < 1e-8
				&& unitaryVerr < 1e-8 && shapeOk;

			System.out.println((ok ? "OK  " : "FAIL") + " " + label + " -- method=" + svd.getMethodName()
				+ " factorized=" + svd.factorized() + " reconErr=" + reconErr
				+ " unitaryUerr=" + unitaryUerr + " unitaryVerr=" + unitaryVerr
				+ " shapeOk=" + shapeOk + " Ushape=" + U.rows() + "x" + U.cols()
				+ " Sshape=" + S.rows() + "x" + S.cols() + " Vshape=" + V.rows() + "x" + V.cols());
			if (ok) pass++; else fail++;
		} catch (Throwable t) {
			System.out.println("FAIL " + label + " -- threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
			fail++;
		}
	}

	static void checkReduced(String label, MatrixComplex A) {
		try {
			SVDfactor svd = new SVDfactor(A, SVDmethod.REDUCED);
			MatrixComplex U = svd.getU();
			MatrixComplex S = svd.getS();
			MatrixComplex V = svd.getV();

			MatrixComplex recon = U.times(S).times(V.adjoint());
			double reconErr = recon.minus(A).inf_norm();

			MatrixComplex Iu = U.adjoint().times(U);
			MatrixComplex eyeU = new MatrixComplex(Iu.rows()).eye();
			double unitaryUerr = Iu.minus(eyeU).inf_norm();

			MatrixComplex Iv = V.adjoint().times(V);
			MatrixComplex eyeV = new MatrixComplex(Iv.rows()).eye();
			double unitaryVerr = Iv.minus(eyeV).inf_norm();

			boolean ok = svd.factorized() && reconErr < 1e-8 && unitaryUerr < 1e-8 && unitaryVerr < 1e-8;
			System.out.println((ok ? "OK  " : "FAIL") + " " + label + " (REDUCED) -- factorized=" + svd.factorized()
				+ " reconErr=" + reconErr + " unitaryUerr=" + unitaryUerr + " unitaryVerr=" + unitaryVerr
				+ " Ushape=" + U.rows() + "x" + U.cols()
				+ " Sshape=" + S.rows() + "x" + S.cols() + " Vshape=" + V.rows() + "x" + V.cols());
			if (ok) pass++; else fail++;
		} catch (Throwable t) {
			System.out.println("FAIL " + label + " (REDUCED) -- threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
			fail++;
		}
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();

		check("3x3 square full rank real", new MatrixComplex("1,2,3;4,5,6;7,8,10"));
		check("2x2 square complex", new MatrixComplex("1+1i,2;3,4-1i"));
		check("4x2 tall real (m>n)", new MatrixComplex("1,2;3,4;5,6;7,9"));
		check("2x4 wide real (m<n)", new MatrixComplex("1,2,3,4;5,6,7,9"));
		check("3x3 rank-deficient real (row2=2*row1)", new MatrixComplex("1,2,3;2,4,6;1,1,1"));
		check("3x2 rank-deficient tall (col2=2*col1)", new MatrixComplex("1,2;3,6;5,11"));
		check("2x3 rank-deficient wide (row2=2*row1)", new MatrixComplex("1,2,3;2,4,6"));
		check("3x3 symmetric real", new MatrixComplex("4,1,2;1,3,0;2,0,5"));
		check("4x4 rank-deficient (rank 2)", new MatrixComplex("1,2,3,4;2,4,6,8;1,0,1,0;0,1,0,1"));
		check("2x2 identity", new MatrixComplex("1,0;0,1"));
		check("3x1 column vector", new MatrixComplex("1;2;3"));
		check("1x3 row vector", new MatrixComplex("1,2,3"));

		System.out.println();
		checkReduced("3x3 square full rank real", new MatrixComplex("1,2,3;4,5,6;7,8,10"));
		checkReduced("4x2 tall real (m>n)", new MatrixComplex("1,2;3,4;5,6;7,9"));
		checkReduced("2x4 wide real (m<n)", new MatrixComplex("1,2,3,4;5,6,7,9"));
		checkReduced("3x3 rank-deficient real (row2=2*row1)", new MatrixComplex("1,2,3;2,4,6;1,1,1"));
		checkReduced("3x2 rank-deficient tall (col2=2*col1)", new MatrixComplex("1,2;3,6;5,11"));
		checkReduced("2x3 rank-deficient wide (row2=2*row1)", new MatrixComplex("1,2,3;2,4,6"));

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
	}
}
