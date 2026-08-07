package TestComplex;

import com.ipserc.arith.complex.Complex;

public class ScratchSVDDiag03 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex root = new Complex(7.507313866853797E-16, 0);
		Complex sv = root.power(0.5);
		System.out.println("zero_threshold_approx() = " + Complex.zero_threshold_approx());
		System.out.println("root = " + root + " isZero=" + root.isZero());
		System.out.println("sqrt(root) = " + sv + " isZero=" + sv.isZero());
	}
}
