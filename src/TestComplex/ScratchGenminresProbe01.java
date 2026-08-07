package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.syseq.Syseqnum;

public class ScratchGenminresProbe01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		try {
			// Case 1: same 3x3 diagonally dominant SYMMETRIC system already verified with gaussSeidel().
			// True solution: 0.9642857142857143, 0.8571428571428572, 0.4642857142857143
			MatrixComplex sys1 = new MatrixComplex("4,-1,0,3; -1,4,-1,2; 0,-1,4,1");
			Syseqnum s1 = new Syseqnum(sys1);
			MatrixComplex sol1 = s1.genminres(new Complex(0, 0), 3, 5);
			System.out.println("Case 1 (symmetric, real) -- expected 0.9642857142857143, 0.8571428571428572, 0.4642857142857143:");
			sol1.println("result");
			System.out.println("solved=" + s1.isSolved() + " numIters=" + s1.getIterations());

			// Case 2: NON-symmetric 3x3 system, real coefficients.
			// 2x0 +  x1        = 5
			//  x0 + 3x1 -  x2  = 10
			//       2x1 + 4x2  = 15
			// Solve by hand-checkable elimination -> verify residual, not a literal expected vector.
			MatrixComplex sys2 = new MatrixComplex("2,1,0,5; 1,3,-1,10; 0,2,4,15");
			Syseqnum s2 = new Syseqnum(sys2);
			MatrixComplex sol2 = s2.genminres(new Complex(0, 0), 3, 10);
			System.out.println("Case 2 (non-symmetric, real):");
			sol2.println("result");
			MatrixComplex A2 = new MatrixComplex("2,1,0; 1,3,-1; 0,2,4");
			MatrixComplex b2 = new MatrixComplex("5;10;15");
			MatrixComplex residual2 = b2.minus(A2.times(sol2.transpose()));
			System.out.println("residual norm case 2 = " + residual2.euc_norm());

			// Case 3: complex-valued non-symmetric 2x2 system, to exercise the Hermitian inner product path.
			// (1+i)x0 +   2x1     = (3+i)
			//     x0  + (2-i)x1  = 4
			MatrixComplex sys3 = new MatrixComplex("1+1i,2,3+1i; 1,2-1i,4");
			Syseqnum s3 = new Syseqnum(sys3);
			MatrixComplex sol3 = s3.genminres(new Complex(0, 0), 2, 10);
			System.out.println("Case 3 (non-symmetric, complex):");
			sol3.println("result");
			MatrixComplex A3 = new MatrixComplex("1+1i,2; 1,2-1i");
			MatrixComplex b3 = new MatrixComplex("3+1i;4");
			MatrixComplex residual3 = b3.minus(A3.times(sol3.transpose()));
			System.out.println("residual norm case 3 = " + residual3.euc_norm());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
