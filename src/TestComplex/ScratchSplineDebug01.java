package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.syseq.Syseq;

public class ScratchSplineDebug01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		MatrixComplex pTable = new MatrixComplex("0,1; 1,3; 3,2");
		int intervals = pTable.rows() - 1; // = 2
		System.out.println("intervals=" + intervals);

		double[] t = {0, 1, 3};
		double[] y = {1, 3, 2};
		double h0 = t[1] - t[0];
		double h1 = t[2] - t[1];
		double u1 = 2 * (h1 + h0);
		double alpha1 = 6 * ((y[2] - y[1]) / h1 - (y[1] - y[0]) / h0);
		System.out.println("hand: h0=" + h0 + " h1=" + h1 + " u1=" + u1 + " alpha1=" + alpha1 + " M1=" + (alpha1 / u1));

		Syseq M = new Syseq(intervals - 1);
		System.out.println("M.rows()=" + M.rows() + " M.cols()=" + M.cols());

		for (int i = 1, j = 0; i < intervals; ++i, ++j) {
			System.out.println("loop i=" + i + " j=" + j);
		}
		// replicate the exact construction from interpolate3Natural
		for (int i = 1, j = 0; i < intervals; ++i, ++j) {
			if (j - 1 >= 0) {
				System.out.println("  sub-diag M[" + j + "][" + (j-1) + "] = h(" + (i-1) + ")");
			}
			if (j >= 0) {
				System.out.println("  diag M[" + j + "][" + j + "] = u(" + i + ")");
			}
			if (j + 1 <= intervals) {
				System.out.println("  super-diag GUARD PASSES: M[" + j + "][" + (j+1) + "] = h(" + i + ")  (M.cols()-1=" + (M.cols()-1) + ")");
			} else {
				System.out.println("  super-diag guard BLOCKS");
			}
			System.out.println("  rhs M[" + j + "][" + (M.cols()-1) + "] = alpha(" + i + ")");
		}
	}
}
