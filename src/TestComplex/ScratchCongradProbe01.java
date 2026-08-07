package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.syseq.Syseqnum;

public class ScratchCongradProbe01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		try {
			// Symmetric positive-definite 3x3, exact solution (1,2,3) by construction.
			MatrixComplex sys = new MatrixComplex("4,1,2,12; 1,3,0,7; 2,0,5,17");
			System.out.println("isSymmetric=" + new Syseqnum(sys).isSymmetric());
			Syseqnum s = new Syseqnum(sys);
			MatrixComplex sol = s.congrad(new Complex(0, 0), 20);
			System.out.println("congrad result (expected 1,2,3):");
			sol.println("result");
			System.out.println("solved=" + s.isSolved() + " numIters=" + s.getIterations());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
