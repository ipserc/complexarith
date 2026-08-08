package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.e_lineStyle;
import com.ipserc.arith.polynom.Polynom;

/**
 * Live verification (user-authorized) through the REAL migrated call paths -- not a standalone
 * reimplementation like ScratchSimpleGnuplotLive01.java, but the actual public methods of
 * Polynom/PolynomPlot and MatrixComplexPlot after swapping in SimpleGnuplot.
 */
public class ScratchSimpleGnuplotRealPathLive01 {
	public static void main(String[] args) {
		System.out.println("=== PolynomPlot real path: Polynom.plotExpressionAbs() ===");
		Polynom p = new Polynom("1, -3, 2"); // (x-1)(x-2), natural order per Polynom(String) javadoc
		p.plotExpressionAbsSync(-1, 4);

		System.out.println("=== MatrixComplexPlot real path: plotSeriesSync() ===");
		MatrixComplexPlot.plotSeriesSync("MatrixComplexPlot real path", e_lineStyle.LINES,
				new double[][] { { 0, 0 }, { 1, 2 }, { 2, 1 }, { 3, 3 } });
		System.out.println("ambas llamadas devueltas (sync) -- si ves esto, las 2 ventanas ya se cerraron.");
	}
}
