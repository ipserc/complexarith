package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;

public class ScratchSolveRobustTier302 {
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

		double mag = 200.0;
		int m = 9;
		double re = mag * 0.7, im = mag * 0.9 + 0.05;
		Polynom p = complexPairFactor(re, im, m).times(realFactor(mag + 23.1, 1));

		MatrixComplex roots = p.solve();
		roots.quicksort(0);
		Complex target = new Complex(re, im);
		System.out.println("target root = " + target + " (magnitude " + target.mod() + ")");
		System.out.println("closest raw roots (distance to target):");
		double[] dist = new double[roots.rows()];
		for (int i = 0; i < roots.rows(); ++i) dist[i] = roots.getItem(i, 0).minus(target).mod();
		java.util.Arrays.sort(dist);
		for (int i = 0; i < Math.min(9, dist.length); ++i) System.out.println("  dist[" + i + "]=" + dist[i]);
		System.out.println("relative error (dist[8]/|target|) = " + (dist[8] / target.mod()));
	}
}
