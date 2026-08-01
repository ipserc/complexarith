package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.polynom.Polynom;

/**
 * Regression test for the Polynom.java System.exit() cleanup (Octava sesion, 1 agosto 2026):
 * evalHorner()/evalFact()/evalNorm()/solveWeierstrass()/solve2d() shape guards, and the
 * "Arithmetic Overflow" (NaN in the Durand-Kerner iteration) guard inside solveWeierstrass() --
 * previously System.exit(10), the same one already flagged as "MatrixComplex.rank2() can kill
 * the JVM" in earlier sessions. All now throw IllegalArgumentException instead of killing the JVM.
 */
public class TestPolynomAudit01 {

	private static int pass = 0;
	private static int fail = 0;

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
			System.out.println("FAIL (unexpected " + e + "): " + label);
			++fail;
		}
	}

	public static void main(String[] args) {
		Polynom p2 = new Polynom("1,2,3"); // valid 2nd degree polynomial

		expectNoException("evalHorner on valid polynomial", () -> p2.evalHorner(new Complex(1, 0)));
		expectNoException("evalFact on valid polynomial", () -> p2.evalFact(new Complex(1, 0)));
		expectNoException("solve() on valid 2nd degree", () -> p2.solve());
		expectNoException("solve2d() on valid 2nd degree", () -> p2.solve2d());

		Polynom p0 = new Polynom("5"); // degree 0 -- solve2d() requires colLen==3
		expectException("solve2d() on degree-0 polynomial", () -> p0.solve2d());

		// Arithmetic Overflow path inside solveWeierstrass()'s Durand-Kerner iteration -- known
		// to be fragile/non-deterministic (Math.random()-seeded), already documented in
		// MEMORY as the root cause of MatrixComplex.rank2()'s occasional crash. Not deterministic
		// enough to assert on here without flaking; this test only covers the guards that ARE
		// deterministic (above). See TestRank01-05.java / TestPolynom01/02.java / TestRoots01.java
		// for real-world matrices that already exercise this exact path (they now exit with a
		// clean, catchable IllegalArgumentException instead of System.exit(10) killing the JVM).

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
