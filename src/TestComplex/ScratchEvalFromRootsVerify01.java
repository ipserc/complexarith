package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;

/**
 * Verifies Polynom.evalFromRoots() (VERSION 1.22) end to end, exercising the real method (not the
 * hand-rolled arithmetic ScratchEvalStabilityProbe01/02 used) on the exact degree-19/magnitude-200/
 * multiplicity-9 case that motivated it (Decimonovena sesion, Parte B/E).
 * <p>
 * TWO separate, non-comparable scenarios, kept apart on purpose:
 * <ol>
 * <li><b>Known-exact roots</b> (the realistic use case: roots trusted independently of the
 * corrupted coefficients, e.g. because the polynomial was itself built from them, like most
 * scripts in this project that construct a polynomial via {@code fromRoots}/{@code power}/
 * {@code times}). Evaluating near the true root with {@code evalFromRoots()} should
 * land close to the mathematically sensible small residual Probe01 already measured (~1e-28),
 * unlike {@code evalHorner()} (~1e33, nonsense).</li>
 * <li><b>Solved (imperfect) roots</b> from {@code solveQRCompanion()} on the SAME multiplicity-9
 * case: root-finding at multiplicity 9 is inherently limited to ~O(eps^(1/9)) accuracy (documented,
 * accepted-as-irresoluble conditioning limit, nothing to do with evaluation) -- so
 * {@code evalFromRoots()}'s residual here is expected to be smaller than {@code evalHorner()}'s
 * (no coefficient-corruption noise added on top) but still large in absolute terms, because it's
 * genuinely reporting how far a ~2% root estimate raised to the 9th power lands from zero. This is
 * NOT a case where evalFromRoots is expected to be "small" -- it is expected to be an honest
 * reflection of solve() quality instead of astronomical evalHorner() noise.</li>
 * </ol>
 */
public class ScratchEvalFromRootsVerify01 {

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
		double thirdRoot = mag + 23.1;
		Polynom p = complexPairFactor(re, im, m).times(realFactor(thirdRoot, 1));
		Complex target = new Complex(re, im);
		Complex conj = new Complex(re, -im);

		System.out.println("degree=" + p.degree() + "  target root=" + target);

		System.out.println();
		System.out.println("=== Scenario 1: known-exact roots, evaluated near (not at) the root ===");
		MatrixComplex exactRoots = new MatrixComplex(p.degree(), 1);
		int row = 0;
		for (int i = 0; i < m; ++i) exactRoots.setItem(row++, 0, target);
		for (int i = 0; i < m; ++i) exactRoots.setItem(row++, 0, conj);
		exactRoots.setItem(row++, 0, new Complex(thirdRoot, 0));

		Complex probe = target.plus(new Complex(1e-6, -1e-6));
		Complex viaHorner1 = p.evalHorner(probe);
		Complex viaFromRoots1 = p.evalFromRoots(exactRoots, probe);
		System.out.println("probe = target + (1e-6-1e-6i) = " + probe);
		System.out.println("evalHorner(probe)                    = " + viaHorner1 + "   |.|=" + viaHorner1.mod());
		System.out.println("evalFromRoots(exactRoots, probe)      = " + viaFromRoots1 + "   |.|=" + viaFromRoots1.mod());
		System.out.println("(ScratchEvalStabilityProbe01's BigDecimal ground truth for evalHorner at this exact");
		System.out.println(" probe was |.|~1.01e33 -- evalHorner tracks the CORRUPTED coefficients faithfully,");
		System.out.println(" both are 'right' about a polynomial that isn't the intended one anymore.)");

		System.out.println();
		System.out.println("=== Scenario 2: solved (imperfect) roots from solveQRCompanion(), same polynomial ===");
		MatrixComplex solvedRoots = p.solveQRCompanion();
		Complex closest = solvedRoots.getItem(0, 0);
		double bestDist = closest.minus(target).mod();
		for (int i = 1; i < solvedRoots.rows(); ++i) {
			double d = solvedRoots.getItem(i, 0).minus(target).mod();
			if (d < bestDist) { bestDist = d; closest = solvedRoots.getItem(i, 0); }
		}
		System.out.println("closest solved root = " + closest + "  relative error vs true target = " + (bestDist / target.mod()));
		Complex viaHorner2 = p.evalHorner(target);
		Complex viaFromRoots2 = p.evalFromRoots(solvedRoots, target);
		System.out.println("evalHorner(true target)              = " + viaHorner2 + "   |.|=" + viaHorner2.mod());
		System.out.println("evalFromRoots(solvedRoots, true target) = " + viaFromRoots2 + "   |.|=" + viaFromRoots2.mod());
		System.out.println("evalFromRoots is ~" + (viaHorner2.mod() / viaFromRoots2.mod())
				+ "x smaller than evalHorner here -- still large because a ~"
				+ (bestDist / target.mod()) + " relative root error raised to multiplicity " + m
				+ " is genuinely large (eps^(1/m) conditioning limit, not an evaluation-method flaw).");

		System.out.println();
		System.out.println("=== shape guard: mismatched roots array must throw, not silently misbehave ===");
		try {
			MatrixComplex badShape = new MatrixComplex(exactRoots.rows() - 1, 1);
			for (int i = 0; i < badShape.rows(); ++i) badShape.setItem(i, 0, exactRoots.getItem(i, 0));
			p.evalFromRoots(badShape, probe);
			System.out.println("SHAPE GUARD FAILED: expected IllegalArgumentException, none thrown");
		} catch (IllegalArgumentException e) {
			System.out.println("shape guard OK: " + e.getMessage());
		}
	}
}
