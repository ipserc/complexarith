package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for the EQUATION SYSTEMS audit fixes in MatrixComplex.java (Octava sesion,
 * 1 agosto 2026). Covers, in order, Hallazgo 1 (rectangular systems), Hallazgo 2
 * (solveCramer()/solveGauss() throwing on invalid shapes), Hallazgo 4 (typeEqSys()
 * misclassifying a homogeneous full-rank system). Hallazgo 3 was closed without a code
 * change (documented only). Other hallazgos are appended as they land.
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

	private static void expectException(String label, Runnable action) {
		try {
			action.run();
			System.out.println("FAIL (no exception): " + label);
			++fail;
		} catch (IllegalArgumentException e) {
			System.out.println("OK   (" + e.getMessage() + "): " + label);
			++pass;
		} catch (Exception e) {
			System.out.println("FAIL (wrong exception type " + e.getClass().getSimpleName() + "): " + label);
			++fail;
		}
	}

	private static void expectNoException(String label, Runnable action) {
		try {
			action.run();
			System.out.println("OK   (no exception): " + label);
			++pass;
		} catch (Exception e) {
			System.out.println("FAIL (unexpected " + e.getClass().getSimpleName() + " " + e.getMessage() + "): " + label);
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

		// ---- Hallazgo 2: solveGauss()/solveCramer() now throw instead of continuing with a
		// mismatched shape. x=1, y=2, x+y=3 (consistent, redundant 3rd equation) -- Gaussian
		// elimination in this class doesn't drop/detect the redundant row, so it's treated as
		// an unsupported shape, same as before this fix but now with a clear diagnosis instead
		// of falling through into non-square inverse()/times() and returning garbage.
		MatrixComplex over = new MatrixComplex("1,0,1;0,1,2;1,1,3");
		expectException("over-determined solveGauss(ONE)", () -> over.solveGauss(com.ipserc.arith.complex.Complex.ONE));
		expectException("over-determined solve()", () -> over.solve());
		expectException("over-determined solveCramer()", () -> over.solveCramer());

		// square, DETERMINATE system: solveCramer() must still work
		expectNoException("square solveCramer() \"1,0,3;0,1,4\"", () -> square.solveCramer());

		// under-determined system: solveCramer() has no valid generalization (Cramer's rule
		// requires a square coefficient matrix), so it must still throw -- unlike solveGauss(),
		// which Hallazgo 1 already taught to complete and solve it via free parameters.
		expectException("under-determined solveCramer() \"1,1,0\"", () -> rect.solveCramer());

		// ---- Hallazgo 4: typeEqSys() misclassified a homogeneous full-rank system as
		// INDETERMINATE instead of DETERMINATE (full column rank always means exactly one
		// solution -- the trivial x=0 for a homogeneous system -- not a free parameter). ----
		check("homogeneous full-rank 2x2 is DETERMINATE",
				new MatrixComplex("1,0,0;0,1,0").typeEqSys() == MatrixComplex.DETERMINATE,
				"typeEqSys()=" + new MatrixComplex("1,0,0;0,1,0").typeEqSys());
		check("homogeneous full-rank 3x3 is DETERMINATE",
				new MatrixComplex("1,0,0,0;0,1,0,0;0,0,1,0").typeEqSys() == MatrixComplex.DETERMINATE,
				"typeEqSys()=" + new MatrixComplex("1,0,0,0;0,1,0,0;0,0,1,0").typeEqSys());
		// regression guards: unaffected classifications
		check("homogeneous underdetermined \"1,1,0\" stays INDETERMINATE",
				rect.typeEqSys() == MatrixComplex.INDETERMINATE, "typeEqSys()=" + rect.typeEqSys());
		check("non-homogeneous full-rank \"1,0,3;0,1,4\" stays DETERMINATE",
				square.typeEqSys() == MatrixComplex.DETERMINATE, "typeEqSys()=" + square.typeEqSys());
		check("inconsistent \"0,0,1\" stays INCONSISTENT",
				new MatrixComplex("0,0,1").typeEqSys() == MatrixComplex.INCONSISTENT,
				"typeEqSys()=" + new MatrixComplex("0,0,1").typeEqSys());

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
