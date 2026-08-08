package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.arith.polynom.Polynom.e_rootCalcMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fase 2, ronda 2 (8 agosto 2026): amplia la calibracion de ScratchRootStatisticCalibration01.java
 * (conservado) con mas magnitudes (incluye 0.1 y 200), raices COMPLEJAS (pares conjugados de
 * multiplicidad m, construidos como factor cuadratico real (x^2-2*re*x+(re^2+im^2))^m -- las raices
 * de un polinomio de coeficientes reales SIEMPRE vienen en pares conjugados, asi que este factor
 * cuadratico tiene coeficientes reales aunque sus raices sean complejas) y un caso de grado alto
 * con VARIOS clusters simultaneos (para detectar interaccion entre grupos, no solo casos aislados).
 * Reusa la misma logica de clustering relativo ya usada en la ronda 1.
 */
public class ScratchRootStatisticCalibration02 {

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

	/**
	 * Alternative to clusterGroupSizesRelative(): connected-components over a graph where an edge
	 * joins ANY pair of raw roots within relTol (not just adjacent-after-sorting-by-modulus pairs).
	 * Hypothesis being tested: chain-by-modulus-sort only needs to bridge the largest gap along ONE
	 * specific traversal order (modulus-ascending); for a ring of m estimates around a repeated
	 * root (radius r, center c far from 0), points at angle +/-theta share near-identical modulus
	 * (sqrt(c^2+2cr*cos(theta)+r^2) is symmetric in theta) and so land ADJACENT after sorting even
	 * though their true distance is ~2r*sin(theta) -- while their true geometric neighbors around
	 * the ring (chord ~2r*sin(pi/m), much smaller) may land far apart in modulus order. Connected
	 * components only needs tol to bridge the smallest spanning structure of the ACTUAL 2D point
	 * cloud (its minimum spanning tree, largest edge ~2r*sin(pi/m) for an even ring) -- never worse,
	 * and possibly much better, than the modulus-order traversal's worst gap.
	 */
	static int[] clusterGroupSizesConnected(MatrixComplex rawRoots, double relTol) {
		int n = rawRoots.rows();
		Complex[] v = new Complex[n];
		for (int i = 0; i < n; ++i) v[i] = rawRoots.getItem(i, 0);
		int[] parent = new int[n];
		for (int i = 0; i < n; ++i) parent[i] = i;
		for (int i = 0; i < n; ++i) {
			for (int j = i + 1; j < n; ++j) {
				double scale = Math.max(1.0, Math.max(v[i].mod(), v[j].mod()));
				if (v[i].minus(v[j]).mod() / scale <= relTol) {
					int ri = find(parent, i), rj = find(parent, j);
					if (ri != rj) parent[ri] = rj;
				}
			}
		}
		java.util.Map<Integer, Integer> sizeByRoot = new java.util.HashMap<>();
		for (int i = 0; i < n; ++i) sizeByRoot.merge(find(parent, i), 1, Integer::sum);
		int[] sizes = new int[sizeByRoot.size()];
		int k = 0;
		for (int s : sizeByRoot.values()) sizes[k++] = s;
		return sizes;
	}

	static int find(int[] parent, int i) {
		while (parent[i] != i) {
			parent[i] = parent[parent[i]];
			i = parent[i];
		}
		return i;
	}

	static boolean matchesExpected(int[] actual, int[] expected) {
		int[] a = actual.clone();
		Arrays.sort(a);
		int[] e = expected.clone();
		Arrays.sort(e);
		return Arrays.equals(a, e);
	}

	static final double[] REL_TOL_CANDIDATES = {1e-6, 1e-5, 1e-4, 3e-4, 1e-3, 3e-3, 1e-2, 3e-2, 1e-1, 3e-1};

	static String minTolFor(MatrixComplex raw, int[] expected, int[] passCounter) {
		String result = "NONE";
		for (int t = 0; t < REL_TOL_CANDIDATES.length; ++t) {
			if (matchesExpected(clusterGroupSizesRelative(raw, REL_TOL_CANDIDATES[t]), expected)) {
				if (passCounter != null) passCounter[t]++;
				if (result.equals("NONE")) result = String.valueOf(REL_TOL_CANDIDATES[t]);
			}
		}
		return result;
	}

	static String maxTolFor(MatrixComplex raw, int[] expected, int[] passCounter) {
		String result = "NONE";
		for (int t = 0; t < REL_TOL_CANDIDATES.length; ++t) {
			if (matchesExpected(clusterGroupSizesRelative(raw, REL_TOL_CANDIDATES[t]), expected)) {
				if (passCounter != null) passCounter[t]++;
				result = String.valueOf(REL_TOL_CANDIDATES[t]);
			}
		}
		return result;
	}

	public static void main(String[] args) {
		Complex.setFormatON();

		double[] magnitudes = {0.1, 1.0, 8.0, 30.0, 200.0};
		int[] mults = {2, 3, 4, 5, 6, 7, 8, 9};

		int[] passA_real = new int[REL_TOL_CANDIDATES.length];
		int[] passA_complex = new int[REL_TOL_CANDIDATES.length];
		int[] passB_real = new int[REL_TOL_CANDIDATES.length];
		int[] passB_complex = new int[REL_TOL_CANDIDATES.length];

		System.out.println("=== GROUP A-REAL: min relTol per (magnitude,m), expected groups {m,1,1} ===");
		System.out.printf("%-10s %-4s %-14s%n", "magnitude", "m", "min_relTol_ok");
		for (double mag : magnitudes) {
			for (int m : mults) {
				double repRoot = mag * 1.234567;
				Polynom p = realFactor(repRoot, m).times(realFactor(mag + 17.3, 1)).times(realFactor(-mag - 9.1, 1));
				String r;
				try {
					MatrixComplex raw = p.solve(e_rootCalcMode.DETERMINISTIC);
					r = minTolFor(raw, new int[]{m, 1, 1}, passA_real);
				} catch (IllegalArgumentException e) {
					r = "OVERFLOW";
				}
				System.out.printf("%-10.2f %-4d %-14s%n", mag, m, r);
			}
		}

		System.out.println();
		System.out.println("=== GROUP A-COMPLEX: min relTol per (magnitude,m), expected groups {m,m,1} (conjugate pair) ===");
		System.out.printf("%-10s %-4s %-14s%n", "magnitude", "m", "min_relTol_ok");
		for (double mag : magnitudes) {
			for (int m : mults) {
				double re = mag * 0.7, im = mag * 0.9 + 0.05;
				Polynom p = complexPairFactor(re, im, m).times(realFactor(mag + 23.1, 1));
				String r;
				try {
					MatrixComplex raw = p.solve(e_rootCalcMode.DETERMINISTIC);
					r = minTolFor(raw, new int[]{m, m, 1}, passA_complex);
				} catch (IllegalArgumentException e) {
					r = "OVERFLOW";
				}
				System.out.printf("%-10.2f %-4d %-14s%n", mag, m, r);
			}
		}

		double[] relGaps = {1e-4, 3e-4, 1e-3, 3e-3, 1e-2, 3e-2, 1e-1, 3e-1, 1.0};

		System.out.println();
		System.out.println("=== GROUP B-REAL: max relTol per (magnitude,relGap), 2 distinct simple roots, expected {1,1,1} ===");
		System.out.printf("%-10s %-10s %-14s%n", "magnitude", "relGap", "max_relTol_ok");
		for (double mag : magnitudes) {
			for (double relGap : relGaps) {
				double gap = mag * relGap;
				if (gap == 0) gap = relGap;
				Polynom p = realFactor(mag, 1).times(realFactor(mag + gap, 1)).times(realFactor(-mag - 13.7, 1));
				String r;
				try {
					MatrixComplex raw = p.solve(e_rootCalcMode.DETERMINISTIC);
					r = maxTolFor(raw, new int[]{1, 1, 1}, passB_real);
				} catch (IllegalArgumentException e) {
					r = "OVERFLOW";
				}
				System.out.printf("%-10.2f %-10.4g %-14s%n", mag, relGap, r);
			}
		}

		System.out.println();
		System.out.println("=== GROUP B-COMPLEX: max relTol, 2 distinct SIMPLE complex-conjugate pairs close in modulus, expected {1,1,1,1} ===");
		System.out.printf("%-10s %-10s %-14s%n", "magnitude", "relGap", "max_relTol_ok");
		for (double mag : magnitudes) {
			for (double relGap : relGaps) {
				double re1 = mag * 0.7, im1 = mag * 0.9 + 0.05;
				double re2 = re1 + mag * relGap, im2 = im1; // same modulus-ish neighborhood, distinct value
				Polynom p = complexPairFactor(re1, im1, 1).times(complexPairFactor(re2, im2, 1));
				String r;
				try {
					MatrixComplex raw = p.solve(e_rootCalcMode.DETERMINISTIC);
					r = maxTolFor(raw, new int[]{1, 1, 1, 1}, passB_complex);
				} catch (IllegalArgumentException e) {
					r = "OVERFLOW";
				}
				System.out.printf("%-10.2f %-10.4g %-14s%n", mag, relGap, r);
			}
		}

		System.out.println();
		System.out.println("=== AGGREGATE (real vs complex) ===");
		System.out.printf("%-10s %-16s %-16s %-16s %-16s%n", "relTol", "A-real", "A-complex", "B-real(ok)", "B-complex(ok)");
		int totalA = magnitudes.length * mults.length;
		int totalB = magnitudes.length * relGaps.length;
		for (int t = 0; t < REL_TOL_CANDIDATES.length; ++t) {
			System.out.printf("%-10.4g %-16s %-16s %-16s %-16s%n", REL_TOL_CANDIDATES[t],
				passA_real[t] + "/" + totalA, passA_complex[t] + "/" + totalA,
				passB_real[t] + "/" + totalB, passB_complex[t] + "/" + totalB);
		}

		System.out.println();
		System.out.println("=== GROUP C: high-degree polynomial, several SIMULTANEOUS clusters (interaction check) ===");
		// degree 3 + 4 + 2 + 1 + 1 = 11, magnitudes/roots spread out, well separated by construction
		Polynom pc = realFactor(2.0, 3).times(realFactor(9.5, 4)).times(realFactor(-4.3, 2))
			.times(realFactor(20.0, 1)).times(realFactor(-15.0, 1));
		MatrixComplex rawC = pc.solve(e_rootCalcMode.DETERMINISTIC);
		int[] expectedC = {3, 4, 2, 1, 1};
		System.out.println("-- CHAIN (modulus-sort, adjacent-only) --");
		for (double relTol : REL_TOL_CANDIDATES) {
			int[] actual = clusterGroupSizesRelative(rawC, relTol);
			boolean ok = matchesExpected(actual, expectedC);
			System.out.println("relTol=" + relTol + "  groups=" + Arrays.toString(actual) + (ok ? "  OK" : "  --"));
		}
		System.out.println("-- CONNECTED COMPONENTS (any pair within tol) --");
		for (double relTol : REL_TOL_CANDIDATES) {
			int[] actual = clusterGroupSizesConnected(rawC, relTol);
			boolean ok = matchesExpected(actual, expectedC);
			System.out.println("relTol=" + relTol + "  groups=" + Arrays.toString(actual) + (ok ? "  OK" : "  --"));
		}

		// ---- Re-run Group A/B with CONNECTED COMPONENTS to compare against the CHAIN numbers above ----
		System.out.println();
		System.out.println("=== GROUP A-REAL, CONNECTED COMPONENTS vs CHAIN (min relTol) ===");
		System.out.printf("%-10s %-4s %-14s %-14s%n", "magnitude", "m", "min_relTol_CHAIN", "min_relTol_CONN");
		int[] passA_real_conn = new int[REL_TOL_CANDIDATES.length];
		int[] passB_real_conn = new int[REL_TOL_CANDIDATES.length];
		for (double mag : magnitudes) {
			for (int m : mults) {
				double repRoot = mag * 1.234567;
				Polynom p = realFactor(repRoot, m).times(realFactor(mag + 17.3, 1)).times(realFactor(-mag - 9.1, 1));
				String rChain, rConn;
				try {
					MatrixComplex raw = p.solve(e_rootCalcMode.DETERMINISTIC);
					rChain = minTolFor(raw, new int[]{m, 1, 1}, null);
					rConn = "NONE";
					for (int t = 0; t < REL_TOL_CANDIDATES.length; ++t) {
						if (matchesExpected(clusterGroupSizesConnected(raw, REL_TOL_CANDIDATES[t]), new int[]{m, 1, 1})) {
							passA_real_conn[t]++;
							if (rConn.equals("NONE")) rConn = String.valueOf(REL_TOL_CANDIDATES[t]);
						}
					}
				} catch (IllegalArgumentException e) {
					rChain = "OVERFLOW";
					rConn = "OVERFLOW";
				}
				System.out.printf("%-10.2f %-4d %-14s %-14s%n", mag, m, rChain, rConn);
			}
		}

		System.out.println();
		System.out.println("=== GROUP B-REAL, CONNECTED COMPONENTS (max relTol, false-positive check) ===");
		System.out.printf("%-10s %-10s %-14s%n", "magnitude", "relGap", "max_relTol_CONN");
		for (double mag : magnitudes) {
			for (double relGap : relGaps) {
				double gap = mag * relGap;
				Polynom p = realFactor(mag, 1).times(realFactor(mag + gap, 1)).times(realFactor(-mag - 13.7, 1));
				String r;
				try {
					MatrixComplex raw = p.solve(e_rootCalcMode.DETERMINISTIC);
					r = "NONE";
					for (int t = 0; t < REL_TOL_CANDIDATES.length; ++t) {
						if (matchesExpected(clusterGroupSizesConnected(raw, REL_TOL_CANDIDATES[t]), new int[]{1, 1, 1})) {
							passB_real_conn[t]++;
							r = String.valueOf(REL_TOL_CANDIDATES[t]);
						}
					}
				} catch (IllegalArgumentException e) {
					r = "OVERFLOW";
				}
				System.out.printf("%-10.2f %-10.4g %-14s%n", mag, relGap, r);
			}
		}

		System.out.println();
		System.out.println("=== AGGREGATE CONNECTED COMPONENTS (A-real, B-real) ===");
		int totalA2 = magnitudes.length * mults.length;
		int totalB2 = magnitudes.length * relGaps.length;
		for (int t = 0; t < REL_TOL_CANDIDATES.length; ++t) {
			System.out.printf("relTol=%-10.4g A-real: %d/%d   B-real(no false merge): %d/%d%n", REL_TOL_CANDIDATES[t],
				passA_real_conn[t], totalA2, passB_real_conn[t], totalB2);
		}
	}
}
