package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.QRSchurfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Fase B of the Schurfactor/Eigenspace investigation (Decimoctava sesion, 6 agosto 2026, ver
 * ComplexArithRev.md): measured sweep to decide whether raising the FLOOR of
 * Eigenspace.bestNumDecs() (not its existing CAP=5, left untouched) fixes the false-merge bug
 * found there -- for a well-conditioned matrix (cond()&lt;2), bestNumDecs()==0 makes the
 * DISTANCE-based eigenvalue grouping tolerance a full 0.5, coarse enough to merge genuinely
 * DISTINCT eigenvalues spaced closer than that into one spurious "repeated" eigenvalue.
 * <p>
 * Reimplements the grouping check independently (raw QRSchurfactor roots + the same
 * chain-distance grouping algorithm as Eigenspace.eigenval()) so multiple floor candidates can be
 * measured in one pass per case, without editing Eigenspace.java repeatedly.
 * <p>
 * Two known-ground-truth groups, same spirit as the 75+63-case sweep that set BEST_NUM_DECS_CAP
 * (see MatrixComplex.java's own Javadoc for that constant):
 * <ul>
 * <li>Group A ("should NOT group"): matrices built as P*D*P* with P unitary (so A is normal and
 * cond(A) == max|eigenvalue|/min|eigenvalue| exactly) and D holding 3 genuinely DISTINCT
 * eigenvalues at a controlled gap around magnitude ~1 -- by construction well-conditioned
 * whenever the gap is small, exactly the regime that broke.</li>
 * <li>Group B ("SHOULD group"): matrices with a genuinely repeated eigenvalue, both
 * diagonalizable (P*D*P*, D has an exactly-repeated diagonal value, P unitary) and defective
 * (P*J*P^-1, J a pure Jordan block of the given multiplicity, P unitary but A not normal since J
 * isn't) -- multiplicities 2-4, several magnitudes.</li>
 * </ul>
 * For each case, several floor candidates are measured: does the grouping produce the TRUE
 * number of distinct eigenvalues? Aggregated per floor as a false-positive rate (Group A, lower
 * is better) and a miss/recall rate (Group B, higher recall is better) to find a floor that fixes
 * Group A without regressing Group B relative to the current floor (0).
 */
public class ScratchBestNumDecsFloorSweep01 {

	private static final double GROUPING_TOL_FACTOR = 0.5;
	private static final int CAP = 5;
	private static final int[] FLOOR_CANDIDATES = {0, 1, 2, 3};

	private static Random rnd = new Random(20260806L);

	public static void main(String[] args) {
		long seed = args.length > 0 ? Long.parseLong(args[0]) : 20260806L;
		rnd = new Random(seed);
		System.out.println("seed=" + seed);
		Complex.digits(50L);
		MatrixComplex.debugOFF();

		// floorIdx -> [attempted, correct] per group
		int[] groupAAttempted = new int[FLOOR_CANDIDATES.length];
		int[] groupACorrect = new int[FLOOR_CANDIDATES.length];
		int[] groupBAttempted = new int[FLOOR_CANDIDATES.length];
		int[] groupBCorrect = new int[FLOOR_CANDIDATES.length];
		int groupASkipped = 0, groupBSkipped = 0;

		System.out.println("=== GROUP A: genuinely DISTINCT eigenvalues, should NOT group ===");
		double[] gaps = {0.005, 0.01, 0.02, 0.05, 0.1, 0.2, 0.3, 0.5, 0.8, 1.2};
		int floor2Idx = -1;
		for (int fi = 0; fi < FLOOR_CANDIDATES.length; ++fi) if (FLOOR_CANDIDATES[fi] == 2) floor2Idx = fi;
		java.util.Map<Double, int[]> perGapFloor2 = new java.util.LinkedHashMap<>(); // gap -> [correct, attempted]
		for (double gap : gaps) {
			int[] tally = {0, 0};
			for (int trial = 0; trial < 5; ++trial) {
				for (boolean complexFlavor : new boolean[]{false, true}) {
					Complex[] trueEigen = threeDistinct(gap, complexFlavor);
					MatrixComplex P = randomUnitary(3);
					MatrixComplex A = buildNormal(trueEigen, P);
					Result res = measure(A, trueEigen.length);
					if (res == null) { groupASkipped++; continue; }
					for (int fi = 0; fi < FLOOR_CANDIDATES.length; ++fi) {
						groupAAttempted[fi]++;
						if (res.groupCounts[fi] == 3) groupACorrect[fi]++;
					}
					tally[1]++;
					if (res.groupCounts[floor2Idx] == 3) tally[0]++;
				}
			}
			perGapFloor2.put(gap, tally);
		}
		System.out.println("Group A per-gap breakdown at floor=2:");
		for (java.util.Map.Entry<Double, int[]> e : perGapFloor2.entrySet()) {
			System.out.println("  gap=" + e.getKey() + ": " + e.getValue()[0] + "/" + e.getValue()[1] + " correct");
		}

		System.out.println("=== GROUP B: genuinely REPEATED eigenvalues, SHOULD group ===");
		int[] mults = {2, 3, 4};
		double[] magnitudes = {0.5, 1.0, 3.0, 20.0};
		for (int mult : mults) {
			for (double mag : magnitudes) {
				for (int trial = 0; trial < 4; ++trial) {
					// Diagonalizable repeated case: D has 'mult' copies of the same eigenvalue,
					// padded with distinct filler eigenvalues to reach size 4 when mult<4.
					{
						Complex lambda = new Complex(mag, mag * 0.3);
						Complex[] trueEigen = repeatedDiagonalizable(lambda, mult, 4);
						MatrixComplex P = randomUnitary(4);
						MatrixComplex A = buildNormal(trueEigen, P);
						Result res = measure(A, 4 - mult + 1);
						if (res == null) { groupBSkipped++; }
						else for (int fi = 0; fi < FLOOR_CANDIDATES.length; ++fi) {
							groupBAttempted[fi]++;
							if (res.groupCounts[fi] == (4 - mult + 1)) groupBCorrect[fi]++;
						}
					}
					// Defective (Jordan block) case: pure single block of size 'mult'.
					{
						Complex lambda = new Complex(mag, -mag * 0.2);
						MatrixComplex J = jordanBlock(lambda, mult);
						MatrixComplex P = randomUnitary(mult);
						MatrixComplex A = P.times(J).times(P.inverse());
						Result res = measure(A, 1);
						if (res == null) { groupBSkipped++; }
						else for (int fi = 0; fi < FLOOR_CANDIDATES.length; ++fi) {
							groupBAttempted[fi]++;
							if (res.groupCounts[fi] == 1) groupBCorrect[fi]++;
						}
					}
				}
			}
		}

		System.out.println();
		System.out.println("=== RESULTS ===");
		System.out.println("Group A skipped (QRSchurfactor threw): " + groupASkipped);
		System.out.println("Group B skipped (QRSchurfactor threw): " + groupBSkipped);
		System.out.println();
		System.out.printf("%-8s %-28s %-28s%n", "floor", "GroupA correct (no false merge)", "GroupB correct (recall)");
		for (int fi = 0; fi < FLOOR_CANDIDATES.length; ++fi) {
			double aPct = 100.0 * groupACorrect[fi] / groupAAttempted[fi];
			double bPct = 100.0 * groupBCorrect[fi] / groupBAttempted[fi];
			System.out.printf("%-8d %d/%d (%.1f%%)%-14s %d/%d (%.1f%%)%n",
				FLOOR_CANDIDATES[fi], groupACorrect[fi], groupAAttempted[fi], aPct, "",
				groupBCorrect[fi], groupBAttempted[fi], bPct);
		}
	}

	private static class Result {
		int[] groupCounts; // one per FLOOR_CANDIDATES entry
	}

	private static Result measure(MatrixComplex A, int trueDistinctCount) {
		MatrixComplex rawRoots;
		try {
			rawRoots = new QRSchurfactor(A).getEigenvalues();
		} catch (Exception e) {
			return null;
		}
		rawRoots.quicksortdown(0);
		double condA = A.cond();
		int rawDigits = (int) (condA / 2);
		Result r = new Result();
		r.groupCounts = new int[FLOOR_CANDIDATES.length];
		for (int fi = 0; fi < FLOOR_CANDIDATES.length; ++fi) {
			int digits = Math.max(rawDigits, FLOOR_CANDIDATES[fi]);
			digits = Math.min(digits, CAP);
			double tol = GROUPING_TOL_FACTOR * Math.pow(10, -digits);
			r.groupCounts[fi] = groupCount(rawRoots, tol);
		}
		return r;
	}

	private static int groupCount(MatrixComplex sortedRoots, double tol) {
		int count = 1;
		Complex prev = sortedRoots.getItem(0, 0);
		for (int i = 1; i < sortedRoots.rows(); ++i) {
			Complex cur = sortedRoots.getItem(i, 0);
			if (prev.minus(cur).mod() > tol) ++count;
			prev = cur;
		}
		return count;
	}

	private static Complex[] threeDistinct(double gap, boolean complexFlavor) {
		double base = 1.0;
		double im = complexFlavor ? 0.15 : 0.0;
		return new Complex[]{
			new Complex(base - gap, im * 0.6),
			new Complex(base, -im),
			new Complex(base + gap, im * 0.3)
		};
	}

	private static Complex[] repeatedDiagonalizable(Complex lambda, int mult, int size) {
		Complex[] result = new Complex[size];
		for (int i = 0; i < mult; ++i) result[i] = lambda;
		for (int i = mult; i < size; ++i) result[i] = lambda.plus(new Complex(2.0 + i, 1.0 + i));
		return result;
	}

	private static MatrixComplex buildNormal(Complex[] eigen, MatrixComplex P) {
		int n = eigen.length;
		MatrixComplex D = new MatrixComplex(n, n);
		for (int i = 0; i < n; ++i) D.setItem(i, i, eigen[i]);
		return P.times(D).times(P.adjoint());
	}

	private static MatrixComplex jordanBlock(Complex lambda, int size) {
		MatrixComplex J = new MatrixComplex(size, size);
		for (int i = 0; i < size; ++i) {
			J.setItem(i, i, lambda);
			if (i + 1 < size) J.setItem(i, i + 1, Complex.ONE);
		}
		return J;
	}

	private static MatrixComplex randomUnitary(int n) {
		MatrixComplex M = new MatrixComplex(n, n);
		for (int r = 0; r < n; ++r)
			for (int c = 0; c < n; ++c)
				M.setItem(r, c, new Complex(rnd.nextDouble() * 2 - 1, rnd.nextDouble() * 2 - 1));
		return M.orthonormalize();
	}
}
