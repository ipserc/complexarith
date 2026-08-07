package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.syseq.Syseq;

public class ScratchSplineDebug03 {
	public static void main(String[] args) {
		Complex.setFormatOFF();

		// trivial 1x1 systems: a*x = b
		test(6, -15);   // expect x = -2.5
		test(2, 10);    // expect x = 5
		test(1, 7);     // expect x = 7
		test(-3, 9);    // expect x = -3

		// a small 2x2 for comparison
		Syseq M2 = new Syseq(2);
		M2.setItem(0, 0, new Complex(2)); M2.setItem(0, 1, new Complex(1)); M2.setItem(0, 2, new Complex(5));
		M2.setItem(1, 0, new Complex(1)); M2.setItem(1, 1, new Complex(3)); M2.setItem(1, 2, new Complex(10));
		// 2x+y=5 ; x+3y=10  ->  x=1, y=3
		M2.solveq();
		M2.printSol("2x2 system");
	}

	static void test(double a, double b) {
		Syseq M = new Syseq(1);
		M.setItem(0, 0, new Complex(a));
		M.setItem(0, 1, new Complex(b));
		M.solveq();
		System.out.println(a + "*x = " + b + "  -->  solved x = " + M.partSol(0) + "   (expected " + (b / a) + ")");
	}
}
