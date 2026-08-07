package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.syseq.Syseqnum;

public class ScratchSyseqnumGS01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		try {
			// 4 -1  0 | 3
			// -1 4 -1 | 2
			// 0 -1  4 | 1
			MatrixComplex sys = new MatrixComplex("4,-1,0,3; -1,4,-1,2; 0,-1,4,1");
			sys.copy().triangleUp().println("triangleUp() of the raw system (to confirm row order used internally)");
			Syseqnum syseqnum = new Syseqnum(sys);

			MatrixComplex r1 = syseqnum.gaussSeidel(new Complex(0, 0), 1);
			System.out.println("gaussSeidel after 1 iteration (expected correct GS: 0.75, 0.6875, 0.421875 ; expected buggy: 0.75, 0.5, 0.25):");
			r1.println("result");

			MatrixComplex r10 = syseqnum.gaussSeidel(new Complex(0, 0), 10);
			System.out.println("gaussSeidel after 10 iterations (true solution ~ 0.9107, 0.6429, 0.4107):");
			r10.println("result");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
