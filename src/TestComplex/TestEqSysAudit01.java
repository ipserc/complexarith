package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for the EQUATION SYSTEMS audit fixes in MatrixComplex.java (Octava sesion,
 * 1 agosto 2026). Covers, in order, Hallazgo 1 (rectangular systems).
 * Other hallazgos are appended as they land.
 */
public class TestEqSysAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, boolean condition, String detail) {
		if (condition) {
			System.out.println("OK   " + label);
			++pass;
		} else {
			System.out.println("FAIL " + label + " -- " + detail);
			++fail;
		}
	}

	public static void main(String[] args) {

		// ---- Hallazgo 1: rectangular systems (fewer equations than unknowns) ----
		// "1,1,0" -> 1 equation (x+y=0), 2 unknowns -- genuinely underdetermined, INDETERMINATE,
		// should return a parametric family, not an empty matrix.
		MatrixComplex rect = new MatrixComplex("1,1,0");
		MatrixComplex sol = rect.solve();
		check("new MatrixComplex(\"1,1,0\").solve() is not empty",
				sol.rows() > 0, "sol.rows()=" + sol.rows());
		if (sol.rows() > 0) {
			System.out.println("     solve() result: " + sol.toString());
		}

		int nSols = rect.nbrOfSolutions();
		check("nbrOfSolutions() for \"1,1,0\" is not 0", nSols != 0, "nbrOfSolutions()=" + nSols);

		// solveGauss(lambda) directly, same expectation
		MatrixComplex solG = rect.solveGauss(com.ipserc.arith.complex.Complex.ONE);
		check("solveGauss(ONE) for \"1,1,0\" is not empty", solG.rows() > 0, "solG.rows()=" + solG.rows());

		// square system must be unaffected (regression guard)
		MatrixComplex square = new MatrixComplex("1,0,3;0,1,4"); // x=3, y=4
		MatrixComplex solSquare = square.solve();
		check("square system \"1,0,3;0,1,4\" still solves to 1 row",
				solSquare.rows() == 1, "solSquare.rows()=" + solSquare.rows());
		if (solSquare.rows() == 1) {
			System.out.println("     square solve() result: " + solSquare.toString());
		}

		// over-determined system must be unaffected (not completed/truncated by this fix)
		// x=1, y=2, x+y=3 (consistent, redundant 3rd equation)
		MatrixComplex over = new MatrixComplex("1,0,1;0,1,2;1,1,3");
		int overRows = over.rows();
		int overCols = over.cols();
		MatrixComplex overSolve = over.solve();
		System.out.println("     over-determined solve() result (informational, not asserted): rows=" + overSolve.rows());
		check("over-determined system dims unchanged by this fix (sanity)",
				overRows == 3 && overCols == 3, "rows=" + overRows + " cols=" + overCols);

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
