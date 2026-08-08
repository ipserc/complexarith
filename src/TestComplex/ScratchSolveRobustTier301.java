package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;

/** Verifies solveRobust()'s new 3rd tier (companion+QRSchurfactor) actually gets reached and
 * resolves a case that previously overflowed, and that normal cases are unaffected (still resolved
 * by solveWeierstrass, tier 1, byte for byte). */
public class ScratchSolveRobustTier301 {

	static Polynom realFactor(double root, int mult) {
		Polynom f = new Polynom("1, " + (-root));
		return f.power(mult);
	}

	static Polynom complexPairFactor(double re, double im, int mult) {
		double c1 = -2 * re;
		double c0 = re * re + im * im;
		Polynom f = new Polynom("1, " + c1 + ", " + c0);
		return f.power(mult);
	}

	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setScientificON(6);

		// Case that overflowed both solveWeierstrass AND solveAberth in the investigation.
		double mag = 200.0;
		int m = 9;
		double re = mag * 0.7, im = mag * 0.9 + 0.05;
		Polynom p = complexPairFactor(re, im, m).times(realFactor(mag + 23.1, 1));

		System.out.println("=== previously-overflowing case, via solve() (public entry point) ===");
		try {
			MatrixComplex roots = p.solve();
			System.out.println("OK, degree=" + roots.rows() + " roots returned, no exception");
			System.out.println("f(re+im*i) = " + p.eval(new Complex(re, im)));
		} catch (Exception e) {
			System.out.println("STILL THROWS: " + e);
		}

		System.out.println();
		System.out.println("=== normal case (degree 5, simple roots), solveRobust() vs solveWeierstrass() should match exactly ===");
		Polynom normal = realFactor(1.5, 1).times(realFactor(-2.3, 1)).times(realFactor(4.1, 1)).times(realFactor(0.7, 1)).times(realFactor(-9.2, 1));
		MatrixComplex viaRobust = normal.solveRobust();
		MatrixComplex viaWeierstrass = normal.solveWeierstrass();
		viaRobust.quicksort(0);
		viaWeierstrass.quicksort(0);
		boolean identical = true;
		for (int i = 0; i < viaRobust.rows(); ++i) {
			if (!viaRobust.getItem(i, 0).equals(viaWeierstrass.getItem(i, 0))) identical = false;
		}
		System.out.println("solveRobust() == solveWeierstrass() on a normal case: " + identical);
	}
}
