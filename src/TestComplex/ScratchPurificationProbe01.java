package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.arith.polynom.Polynom.e_rootCalcMode;

/** Reproduces the user's exact TestRoots02.java case (roots 1.12131415^3, 3^1, -1^3) with full
 * precision display (no 6-decimal rounding) to see the RAW values before deciding on purification/
 * multiplicity-assignment changes. Also compares DETERMINISTIC vs STATISTIC on this exact case. */
public class ScratchPurificationProbe01 {
	public static void main(String[] args) {
		Polynom aPolynom = new Polynom("1");
		Polynom rootPoly;

		Complex.setFormatON();
		Complex.setScientificON(16);

		rootPoly = new Polynom("1, -1.12131415");
		rootPoly = rootPoly.power(3);
		aPolynom = aPolynom.times(rootPoly);

		rootPoly = new Polynom("1, -3");
		rootPoly = rootPoly.power(1);
		aPolynom = aPolynom.times(rootPoly);

		rootPoly = new Polynom("1, 1");
		rootPoly = rootPoly.power(3);
		aPolynom = aPolynom.times(rootPoly);

		System.out.println("precision()=" + Complex.precision());
		System.out.println("maxPrec=sqrt(precision*10)=" + Math.sqrt(Complex.precision() * 10));
		System.out.println("exact()=" + Complex.exact());

		System.out.println();
		System.out.println("=== DETERMINISTIC (solve(), full precision display) ===");
		MatrixComplex det = aPolynom.solve();
		det.quicksort(0);
		for (int i = 0; i < det.rows(); ++i) {
			Complex r = det.getItem(i, 0);
			System.out.println("  rep=" + r.rep() + "  imp=" + r.imp() + "  |imp|=" + Math.abs(r.imp()));
		}

		System.out.println();
		System.out.println("=== STATISTIC (solve(e_rootCalcMode.STATISTIC)) ===");
		MatrixComplex stat = aPolynom.solve(e_rootCalcMode.STATISTIC);
		stat.quicksort(0);
		for (int i = 0; i < stat.rows(); ++i) {
			Complex r = stat.getItem(i, 0);
			System.out.println("  rep=" + r.rep() + "  imp=" + r.imp());
		}
	}
}
