package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.arith.polynom.Polynom.e_rootCalcMode;

/**
 * Quick smoke test for Polynom.e_rootCalcMode.STATISTIC (Fase 1, 8 agosto 2026): builds a
 * polynomial from known roots with explicit multiplicity (same construction as TestRoots02.java),
 * compares DETERMINISTIC vs STATISTIC output.
 */
public class ScratchRootStatisticProbe01 {

	static Polynom fromRoots(double[] roots, int[] mult) {
		Polynom p = new Polynom("1");
		for (int i = 0; i < roots.length; ++i) {
			Polynom factor = new Polynom("1, " + (-roots[i]));
			factor = factor.power(mult[i]);
			p = p.times(factor);
		}
		return p;
	}

	static void report(String label, double[] roots, int[] mult) {
		Polynom p = fromRoots(roots, mult);
		System.out.println("=== " + label + " ===");

		MatrixComplex det = p.solve(e_rootCalcMode.DETERMINISTIC);
		MatrixComplex stat = p.solve(e_rootCalcMode.STATISTIC);
		det.quicksort(0);
		stat.quicksort(0);

		System.out.println("-- DETERMINISTIC --");
		for (int i = 0; i < det.rows(); ++i) {
			Complex r = det.getItem(i, 0);
			System.out.println("  root[" + i + "]=" + r + "  f(root)=" + p.eval(r));
		}
		System.out.println("-- STATISTIC --");
		for (int i = 0; i < stat.rows(); ++i) {
			Complex r = stat.getItem(i, 0);
			System.out.println("  root[" + i + "]=" + r + "  f(root)=" + p.eval(r));
		}

		// count distinct groups + multiplicity in STATISTIC output (exact equality expected within a group)
		System.out.println("-- STATISTIC groups --");
		int i = 0;
		while (i < stat.rows()) {
			int j = i + 1;
			while (j < stat.rows() && stat.getItem(j, 0).equals(stat.getItem(i, 0))) ++j;
			System.out.println("  value=" + stat.getItem(i, 0) + "  count=" + (j - i));
			i = j;
		}
		System.out.println();
	}

	static void reportGroupsOnly(String label, double[] roots, int[] mult) {
		Polynom p = fromRoots(roots, mult);
		MatrixComplex stat = p.solve(e_rootCalcMode.STATISTIC);
		stat.quicksort(0);
		System.out.println("=== " + label + " ===");
		int i = 0;
		while (i < stat.rows()) {
			int j = i + 1;
			while (j < stat.rows() && stat.getItem(j, 0).equals(stat.getItem(i, 0))) ++j;
			System.out.println("  value=" + stat.getItem(i, 0) + "  count=" + (j - i));
			i = j;
		}
	}

	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedON(8);

		report("simple roots, no multiplicity", new double[]{1.5, -2.3, 4.1}, new int[]{1, 1, 1});
		report("one double root", new double[]{1.5, -2.3, 4.1}, new int[]{2, 1, 1});
		report("one triple root", new double[]{1.12131415, -3, 1}, new int[]{9, 1, 3});
		report("two close-but-distinct simple roots (stress case)", new double[]{2.0, 2.05, -5.0}, new int[]{1, 1, 1});

		System.out.println();
		System.out.println("### Mapping the practical multiplicity ceiling at tol=ROOT_GROUPING_TOL_FACTOR*10^-ROOT_GROUPING_DIGITS ###");
		for (int m = 2; m <= 9; ++m) {
			reportGroupsOnly("isolated root, multiplicity " + m, new double[]{1.5, -7.25}, new int[]{m, 1});
		}
	}
}
