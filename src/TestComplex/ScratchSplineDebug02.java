package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Spline;
import com.ipserc.arith.syseq.Syseq;

public class ScratchSplineDebug02 {
	public static void main(String[] args) {
		Complex.setFormatOFF();

		// replicate interpolate3Natural's system building/solving standalone
		int intervals = 2;
		double h0 = 1, h1 = 2;
		double u1 = 6, alpha1 = -15;
		Syseq M = new Syseq(intervals - 1);
		M.setItem(0, 0, new Complex(u1));
		M.setItem(0, M.cols() - 1, new Complex(alpha1));
		M.print("M before solve");
		M.solveq();
		M.printSol("M after solve");
		System.out.println("partSol(0) = " + M.partSol(0));

		// now the real thing
		MatrixComplex pTable = new MatrixComplex("0,1; 1,3; 3,2");
		Spline spline = new Spline(3, pTable);
		spline.interpolate(true); // showinternals=true to dump the system it actually builds
		spline.print();
		System.out.println("eval(0.5) = " + spline.eval(new Complex(0.5)));
		System.out.println("expected (hand calc) = 2.15625");
	}
}
