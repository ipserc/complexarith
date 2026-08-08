package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.arith.polynom.Polynom.e_rootCalcMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fase 2 (8 agosto 2026): calibration sweep for Polynom.STATISTIC's distance-based root clustering.
 * Reimplements the same clustering logic as Polynom.solveStatistic() here (small, self-contained)
 * so a candidate absolute distance tolerance can be swept WITHOUT re-solving the polynomial for
 * each candidate (Durand-Kerner/Aberth's seeding is deterministic since VERSION 1.6 -- solving once
 * per case and re-clustering the same raw output for every tol candidate is both faster and exactly
 * equivalent to calling solve(STATISTIC) with that tol baked in).
 *
 * Group A measures multiplicity RECALL (known repeated root, varying multiplicity and magnitude).
 * Group B measures the FALSE POSITIVE risk (two genuinely distinct simple roots at a known gap).
 * Reports, per case, the minimum tol that recovers the correct grouping (Group A) or the maximum
 * tol that does NOT merge the distinct roots (Group B) -- more informative than a single aggregate
 * pass rate for deciding whether tolerance should scale with root magnitude.
 */
public class ScratchRootStatisticCalibration01 {

	static Polynom fromRoots(double[] roots, int[] mult) {
		Polynom p = new Polynom("1");
		for (int i = 0; i < roots.length; ++i) {
			Polynom factor = new Polynom("1, " + (-roots[i]));
			factor = factor.power(mult[i]);
			p = p.times(factor);
		}
		return p;
	}

	static int[] clusterGroupSizes(MatrixComplex rawRoots, double tol) {
		MatrixComplex sorted = rawRoots.copy();
		sorted.quicksort(0);
		int n = sorted.rows();
		List<Integer> sizes = new ArrayList<>();
		int groupStart = 0;
		for (int i = 1; i <= n; ++i) {
			boolean closeGroup = (i == n) || sorted.getItem(i - 1, 0).minus(sorted.getItem(i, 0)).mod() > tol;
			if (closeGroup) {
				sizes.add(i - groupStart);
				groupStart = i;
			}
		}
		int[] arr = new int[sizes.size()];
		for (int i = 0; i < arr.length; ++i) arr[i] = sizes.get(i);
		return arr;
	}

	static boolean matchesExpected(int[] actual, int[] expected) {
		int[] a = actual.clone();
		Arrays.sort(a);
		int[] e = expected.clone();
		Arrays.sort(e);
		return Arrays.equals(a, e);
	}

	/**
	 * Same chain-clustering as clusterGroupSizes(), but the gap test is RELATIVE to the larger of
	 * the two moduli being compared (floor 1.0 to avoid blowing up near the origin), instead of an
	 * absolute distance. Second experiment, run after the absolute-tolerance sweep showed the
	 * required tol scaling roughly proportionally with root magnitude.
	 */
	static int[] clusterGroupSizesRelative(MatrixComplex rawRoots, double relTol) {
		MatrixComplex sorted = rawRoots.copy();
		sorted.quicksort(0);
		int n = sorted.rows();
		List<Integer> sizes = new ArrayList<>();
		int groupStart = 0;
		for (int i = 1; i <= n; ++i) {
			boolean closeGroup;
			if (i == n) {
				closeGroup = true;
			} else {
				Complex a = sorted.getItem(i - 1, 0);
				Complex b = sorted.getItem(i, 0);
				double scale = Math.max(1.0, Math.max(a.mod(), b.mod()));
				closeGroup = a.minus(b).mod() / scale > relTol;
			}
			if (closeGroup) {
				sizes.add(i - groupStart);
				groupStart = i;
			}
		}
		int[] arr = new int[sizes.size()];
		for (int i = 0; i < arr.length; ++i) arr[i] = sizes.get(i);
		return arr;
	}

	static final double[] TOL_CANDIDATES = {1e-6, 1e-5, 1e-4, 3e-4, 1e-3, 3e-3, 1e-2, 3e-2, 1e-1, 3e-1, 1.0, 3.0};
	static final double[] REL_TOL_CANDIDATES = {1e-6, 1e-5, 1e-4, 3e-4, 1e-3, 3e-3, 1e-2, 3e-2, 1e-1, 3e-1};

	public static void main(String[] args) {
		Complex.setFormatON();

		double[] magnitudes = {1.0, 8.0, 30.0};
		int[] mults = {2, 3, 4, 5, 6, 7, 8, 9};

		System.out.println("=== GROUP A: minimum tol that recovers the correct grouping (cluster size m + 2 singles) ===");
		System.out.printf("%-10s %-4s %-14s%n", "magnitude", "m", "min_tol_ok");
		int groupACases = 0;
		int[] aggPassA = new int[TOL_CANDIDATES.length];
		for (double mag : magnitudes) {
			for (int m : mults) {
				double repRoot = mag * 1.234567;
				double d1 = mag + 17.3;
				double d2 = -mag - 9.1;
				Polynom p = fromRoots(new double[]{repRoot, d1, d2}, new int[]{m, 1, 1});
				MatrixComplex raw = p.solve(e_rootCalcMode.DETERMINISTIC);
				int[] expected = {m, 1, 1};
				groupACases++;
				String minTolStr = "NONE";
				for (int t = 0; t < TOL_CANDIDATES.length; ++t) {
					boolean ok = matchesExpected(clusterGroupSizes(raw, TOL_CANDIDATES[t]), expected);
					if (ok) {
						aggPassA[t]++;
						if (minTolStr.equals("NONE")) minTolStr = String.valueOf(TOL_CANDIDATES[t]);
					}
				}
				System.out.printf("%-10.2f %-4d %-14s%n", mag, m, minTolStr);
			}
		}

		System.out.println();
		System.out.println("=== GROUP B: false positives at tol, 2 distinct simple roots at gap g (expect 2 singles) ===");
		System.out.printf("%-10s %-10s %-14s%n", "magnitude", "gap", "max_tol_ok");
		double[] gaps = {1e-3, 3e-3, 1e-2, 3e-2, 1e-1, 3e-1, 1.0};
		int groupBCases = 0;
		int[] aggPassB = new int[TOL_CANDIDATES.length];
		for (double mag : magnitudes) {
			for (double gap : gaps) {
				double r1 = mag;
				double r2 = mag + gap;
				double d1 = -mag - 13.7;
				Polynom p = fromRoots(new double[]{r1, r2, d1}, new int[]{1, 1, 1});
				MatrixComplex raw = p.solve(e_rootCalcMode.DETERMINISTIC);
				int[] expected = {1, 1, 1};
				groupBCases++;
				String maxTolStr = "NONE";
				for (int t = 0; t < TOL_CANDIDATES.length; ++t) {
					boolean ok = matchesExpected(clusterGroupSizes(raw, TOL_CANDIDATES[t]), expected);
					if (ok) {
						aggPassB[t]++;
						maxTolStr = String.valueOf(TOL_CANDIDATES[t]);
					}
				}
				System.out.printf("%-10.2f %-10.4g %-14s%n", mag, gap, maxTolStr);
			}
		}

		System.out.println();
		System.out.println("=== AGGREGATE PASS RATE PER TOL (ABSOLUTE) ===");
		System.out.printf("%-10s %-20s %-20s %-14s%n", "tol", "groupA_correct", "groupB_correct(no-merge)", "combined");
		for (int t = 0; t < TOL_CANDIDATES.length; ++t) {
			int combined = aggPassA[t] + aggPassB[t];
			System.out.printf("%-10.4g %-20s %-20s %-14s%n", TOL_CANDIDATES[t],
				aggPassA[t] + "/" + groupACases, aggPassB[t] + "/" + groupBCases, combined + "/" + (groupACases + groupBCases));
		}

		// ---- Second experiment: RELATIVE tolerance ----
		System.out.println();
		System.out.println("=== RELATIVE TOLERANCE EXPERIMENT (per-case) ===");
		System.out.println("-- Group A: minimum relTol that recovers the correct grouping --");
		System.out.printf("%-10s %-4s %-14s%n", "magnitude", "m", "min_relTol_ok");
		int[] aggPassARel = new int[REL_TOL_CANDIDATES.length];
		int[] aggPassBRel = new int[REL_TOL_CANDIDATES.length];

		for (double mag : magnitudes) {
			for (int m : mults) {
				double repRoot = mag * 1.234567;
				double d1 = mag + 17.3;
				double d2 = -mag - 9.1;
				Polynom p = fromRoots(new double[]{repRoot, d1, d2}, new int[]{m, 1, 1});
				MatrixComplex raw = p.solve(e_rootCalcMode.DETERMINISTIC);
				int[] expected = {m, 1, 1};
				String minRelTolStr = "NONE";
				for (int t = 0; t < REL_TOL_CANDIDATES.length; ++t) {
					boolean ok = matchesExpected(clusterGroupSizesRelative(raw, REL_TOL_CANDIDATES[t]), expected);
					if (ok) {
						aggPassARel[t]++;
						if (minRelTolStr.equals("NONE")) minRelTolStr = String.valueOf(REL_TOL_CANDIDATES[t]);
					}
				}
				System.out.printf("%-10.2f %-4d %-14s%n", mag, m, minRelTolStr);
			}
		}
		System.out.println("-- Group B: maximum relTol that does not merge the distinct roots --");
		System.out.printf("%-10s %-10s %-14s%n", "magnitude", "gap", "max_relTol_ok");
		for (double mag : magnitudes) {
			for (double gap : gaps) {
				double r1 = mag;
				double r2 = mag + gap;
				double d1 = -mag - 13.7;
				Polynom p = fromRoots(new double[]{r1, r2, d1}, new int[]{1, 1, 1});
				MatrixComplex raw = p.solve(e_rootCalcMode.DETERMINISTIC);
				int[] expected = {1, 1, 1};
				String maxRelTolStr = "NONE";
				for (int t = 0; t < REL_TOL_CANDIDATES.length; ++t) {
					boolean ok = matchesExpected(clusterGroupSizesRelative(raw, REL_TOL_CANDIDATES[t]), expected);
					if (ok) {
						aggPassBRel[t]++;
						maxRelTolStr = String.valueOf(REL_TOL_CANDIDATES[t]);
					}
				}
				System.out.printf("%-10.2f %-10.4g %-14s%n", mag, gap, maxRelTolStr);
			}
		}

		System.out.printf("%-10s %-20s %-20s %-14s%n", "relTol", "groupA_correct", "groupB_correct(no-merge)", "combined");
		for (int t = 0; t < REL_TOL_CANDIDATES.length; ++t) {
			int combined = aggPassARel[t] + aggPassBRel[t];
			System.out.printf("%-10.4g %-20s %-20s %-14s%n", REL_TOL_CANDIDATES[t],
				aggPassARel[t] + "/" + groupACases, aggPassBRel[t] + "/" + groupBCases, combined + "/" + (groupACases + groupBCases));
		}
	}
}
