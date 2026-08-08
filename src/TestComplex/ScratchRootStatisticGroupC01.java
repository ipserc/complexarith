package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.arith.polynom.Polynom.e_rootCalcMode;

/** Verifies the PRODUCTION Polynom.solveStatistic() (connected components) against the degree-11
 * multi-cluster case that defeated the Fase 1 chain-by-modulus implementation. */
public class ScratchRootStatisticGroupC01 {
	static Polynom realFactor(double root, int mult) {
		Polynom f = new Polynom("1, " + (-root));
		return f.power(mult);
	}

	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedON(6);
		Polynom p = realFactor(2.0, 3).times(realFactor(9.5, 4)).times(realFactor(-4.3, 2))
			.times(realFactor(20.0, 1)).times(realFactor(-15.0, 1));

		MatrixComplex stat = p.solve(e_rootCalcMode.STATISTIC);
		stat.quicksort(0);
		System.out.println("Expected groups: {3,4,2,1,1} (degree 11)");
		int i = 0;
		while (i < stat.rows()) {
			int j = i + 1;
			while (j < stat.rows() && stat.getItem(j, 0).equals(stat.getItem(i, 0))) ++j;
			Complex v = stat.getItem(i, 0);
			System.out.println("  value=" + v + "  count=" + (j - i) + "  f(v)=" + p.eval(v));
			i = j;
		}
	}
}
