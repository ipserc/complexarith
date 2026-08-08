package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Investigates whether Eigenspace.eigenval()'s chain-by-modulus-sort grouping has the same
 * fragmentation weakness found and fixed (connected components) in Polynom.solveStatistic()
 * (8 agosto 2026, Fase 2). Builds a matrix with KNOWN eigenvalues via similarity transform of a
 * diagonal matrix (A = P*D*P^-1) -- same known-multiplicity idea as the Polynom Group C case
 * (multiplicities 3, 4, 2 simultaneously, well separated in magnitude), so eigenval()'s own
 * arithmeticMultiplicity() result can be checked against ground truth.
 */
public class ScratchEigenspaceGroupC01 {

	static MatrixComplex diagValues(double[] values, int[] mult) {
		int n = 0;
		for (int m : mult) n += m;
		MatrixComplex row = new MatrixComplex(1, n);
		int idx = 0;
		for (int i = 0; i < values.length; ++i) {
			for (int k = 0; k < mult[i]; ++k) {
				row.setItem(0, idx++, new Complex(values[i], 0));
			}
		}
		return row;
	}

	static MatrixComplex similarityP(int n) {
		MatrixComplex P = MatrixComplex.eye(n);
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				if (i != j) {
					double v = 0.15 * Math.sin(0.7 * i + 1.3 * j + 0.4);
					P.setItem(i, j, new Complex(v, 0));
				}
			}
		}
		return P;
	}

	static void reportCase(String label, double[] values, int[] mult) {
		int n = 0;
		for (int m : mult) n += m;
		MatrixComplex D = MatrixComplex.diagonal(diagValues(values, mult));
		MatrixComplex P = similarityP(n);
		MatrixComplex A = P.times(D).times(P.inverse());

		System.out.println("=== " + label + " (n=" + n + ") ===");
		try {
			Eigenspace eig = new Eigenspace(A);
			MatrixComplex grouped = eig.eigenvalues();
			grouped.quicksort(0);
			int totalMult = 0;
			for (int i = 0; i < grouped.rows(); ++i) totalMult += (int) grouped.getItem(i, 1).cre();
			System.out.println("groups found=" + grouped.rows() + " (expected " + mult.length + "), total mult=" + totalMult + " (expected " + n + ")");
			for (int i = 0; i < grouped.rows(); ++i) {
				System.out.println("  eigenvalue=" + grouped.getItem(i, 0) + "  mult=" + (int) grouped.getItem(i, 1).cre());
			}
		} catch (Exception e) {
			System.out.println("EXCEPTION: " + e);
		}
	}

	static int find(int[] parent, int i) {
		while (parent[i] != i) {
			parent[i] = parent[parent[i]];
			i = parent[i];
		}
		return i;
	}

	/** Mirrors Eigenspace.eigenval()'s NEW (post-fix) connected-components clustering (same formula:
	 * GROUPING_TOL_FACTOR=0.5, digits=A.bestNumDecs()), applied to the FALLBACK engine's raw output
	 * (charactPoly().solveRobust(), same Durand-Kerner/Aberth engine Polynom itself uses) instead of
	 * the default QRSchurfactor -- confirms the same fix that closed this gap in Polynom also closes
	 * it here, on the exact raw data that showed the chain-by-modulus fragmentation before the fix. */
	static void reportFallbackCase(String label, double[] values, int[] mult) {
		int n = 0;
		for (int m : mult) n += m;
		MatrixComplex D = MatrixComplex.diagonal(diagValues(values, mult));
		MatrixComplex P = similarityP(n);
		MatrixComplex A = P.times(D).times(P.inverse());

		System.out.println("=== FALLBACK (connected components) " + label + " (n=" + n + ") ===");
		MatrixComplex raw = A.charactPoly().solveRobust();
		int digits = A.bestNumDecs();
		double tol = 0.5 * Math.pow(10, -digits);
		System.out.println("bestNumDecs=" + digits + " tol=" + tol);

		int rn = raw.rows();
		int[] parent = new int[rn];
		for (int i = 0; i < rn; ++i) parent[i] = i;
		for (int i = 0; i < rn; ++i) {
			for (int j = i + 1; j < rn; ++j) {
				if (raw.getItem(i, 0).minus(raw.getItem(j, 0)).mod() <= tol) {
					int ri = find(parent, i), rj = find(parent, j);
					if (ri != rj) parent[ri] = rj;
				}
			}
		}
		java.util.Map<Integer, Integer> sizeByRoot = new java.util.HashMap<>();
		for (int i = 0; i < rn; ++i) sizeByRoot.merge(find(parent, i), 1, Integer::sum);
		java.util.List<Integer> sizes = new java.util.ArrayList<>(sizeByRoot.values());
		System.out.println("groups=" + sizes + " (expected sizes " + java.util.Arrays.toString(mult) + ")");
	}

	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedON(6);

		reportCase("Group C analog (3,4,2,1,1)", new double[]{2.0, 9.5, -4.3, 20.0, -15.0}, new int[]{3, 4, 2, 1, 1});

		// Sweep isolated multiplicity, default engine (QRSchurfactor)
		double[] mags = {1.0, 8.0, 30.0};
		int[] mults = {2, 3, 4, 5, 6, 7, 8, 9};
		for (double mag : mags) {
			for (int m : mults) {
				double rep = mag * 1.234567;
				reportCase("isolated mag=" + mag + " mult=" + m, new double[]{rep, mag + 17.3, -mag - 9.1}, new int[]{m, 1, 1});
			}
		}

		System.out.println();
		System.out.println("################ FALLBACK ENGINE (charactPoly().solveRobust(), same as Polynom) ################");
		reportFallbackCase("Group C analog (3,4,2,1,1)", new double[]{2.0, 9.5, -4.3, 20.0, -15.0}, new int[]{3, 4, 2, 1, 1});
		for (double mag : mags) {
			for (int m : mults) {
				double rep = mag * 1.234567;
				reportFallbackCase("isolated mag=" + mag + " mult=" + m, new double[]{rep, mag + 17.3, -mag - 9.1}, new int[]{m, 1, 1});
			}
		}
	}
}
