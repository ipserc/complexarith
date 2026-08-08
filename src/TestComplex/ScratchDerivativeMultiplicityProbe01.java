package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;

/**
 * Investigates the user's idea: a root of multiplicity m survives in f, f', f'', ..., f^(m-1) and
 * vanishes only at f^(m) -- could that be used to (A) detect multiplicity analytically instead of
 * clustering by distance, and/or (B) recover more precision than the current solvers?
 * <p>
 * Uses repeated synthetic division (the numerically standard "Horner scheme for Taylor
 * coefficients") to get taylor[k] = f^(k)(z)/k! at a point z, WITHOUT the naive
 * coefficient-times-i-factorial differentiation that would amplify the same coefficient-corruption
 * problem measured for evalFromRoots() this session.
 * <p>
 * Two experiments, same (magnitude x multiplicity) grid already calibrated in the Decimonovena
 * sesion (Fase 2/Parte A), for direct comparability:
 * <ul>
 * <li>A: does the vanishing-derivative pattern reliably reveal the TRUE multiplicity when probed
 * at a REALISTIC raw solver estimate (not the exact root)?</li>
 * <li>B: does one step of multiplicity-aware Newton (z - m*f(z)/f'(z), quadratic convergence at a
 * root of KNOWN multiplicity m, vs plain Newton's linear convergence there) land closer to the
 * true root than the raw solver estimate, or than solveStatistic()'s cluster-average centroid?</li>
 * </ul>
 */
public class ScratchDerivativeMultiplicityProbe01 {

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

	/** taylor[k] = f^(k)(x0)/k!, via repeated synthetic division (ascending coefficients in, as
	 * Polynom stores them). Numerically the same operation count/stability as evalHorner, run
	 * "degree" times -- no i!-style amplification. */
	static Complex[] taylorCoeffs(Polynom p, Complex x0, int maxK) {
		int n = p.degree();
		// descending working array: coeffs[0]=leading ... coeffs[n]=constant
		Complex[] coeffs = new Complex[n + 1];
		for (int i = 0; i <= n; ++i) coeffs[i] = p.getItem(0, n - i).copy();

		int outLen = Math.min(maxK, n) + 1;
		Complex[] taylor = new Complex[outLen];
		int len = coeffs.length;
		for (int k = 0; k < outLen && len > 0; ++k) {
			Complex[] newc = new Complex[len];
			newc[0] = coeffs[0].copy();
			for (int i = 1; i < len; ++i) newc[i] = coeffs[i].plus(newc[i - 1].times(x0));
			taylor[k] = newc[len - 1];
			len = len - 1;
			coeffs = new Complex[len];
			for (int i = 0; i < len; ++i) coeffs[i] = newc[i];
		}
		return taylor;
	}

	static int totalCases = 0, correctDetections = 0, newtonBeatsRaw = 0, newtonBeatsStatistic = 0;

	static void probeCase(double mag, int m) {
		double re = mag * 0.7, im = mag * 0.9 + 0.05;
		double thirdRoot = mag + 23.1;
		Polynom p = complexPairFactor(re, im, m).times(realFactor(thirdRoot, 1));
		Complex target = new Complex(re, im);

		// realistic raw estimate: closest of solveRobust()'s raw output to the true target
		MatrixComplex raw = p.solveRobust();
		Complex rawEst = raw.getItem(0, 0);
		double bestDist = rawEst.minus(target).mod();
		for (int i = 1; i < raw.rows(); ++i) {
			double d = raw.getItem(i, 0).minus(target).mod();
			if (d < bestDist) { bestDist = d; rawEst = raw.getItem(i, 0); }
		}

		// STATISTIC centroid closest to target, for comparison
		MatrixComplex stat = p.solve(Polynom.e_rootCalcMode.STATISTIC);
		Complex statEst = stat.getItem(0, 0);
		double statDist = statEst.minus(target).mod();
		for (int i = 1; i < stat.rows(); ++i) {
			double d = stat.getItem(i, 0).minus(target).mod();
			if (d < statDist) { statDist = d; statEst = stat.getItem(i, 0); }
		}

		// --- Experiment A: multiplicity detection at the raw estimate ---
		// Near a root of multiplicity m probed at distance delta, taylor[k] ~ delta^(m-k) for
		// k=0..m (vanishing prefix), then O(1) (generic Taylor coefficients) for k>m -- so
		// consecutive ratios |taylor[k]/taylor[k-1]| stay roughly CONSTANT (~1/delta) through k=m,
		// then drop sharply at k=m+1. Detect m as the k with the biggest proportional drop in the
		// ratio sequence (ratio[k]/ratio[k+1] maximal).
		Complex[] taylor = taylorCoeffs(p, rawEst, m + 2);
		StringBuilder mags = new StringBuilder();
		for (Complex t : taylor) mags.append(String.format("%.2e ", t.mod()));
		double[] ratios = new double[taylor.length - 1];
		for (int k = 1; k < taylor.length; ++k) {
			double prev = taylor[k - 1].mod();
			ratios[k - 1] = (prev == 0) ? Double.POSITIVE_INFINITY : taylor[k].mod() / prev;
		}
		int detectedMult = ratios.length; // default: no drop found, assume it never levels off
		double biggestDrop = -1;
		for (int k = 0; k < ratios.length - 1; ++k) {
			double drop = ratios[k] / Math.max(ratios[k + 1], 1e-300);
			if (drop > biggestDrop) { biggestDrop = drop; detectedMult = k + 1; }
		}

		// --- Experiment B: one step of multiplicity-aware Newton from rawEst, using TRUE m ---
		Complex[] t1 = taylorCoeffs(p, rawEst, 1); // [f(rawEst), f'(rawEst)]
		Complex newtonEst = rawEst;
		double newtonDist = bestDist;
		if (t1[1].mod() != 0) {
			newtonEst = rawEst.minus(t1[0].divides(t1[1]).times(new Complex(m, 0)));
			newtonDist = newtonEst.minus(target).mod();
		}

		totalCases++;
		if (detectedMult == m) correctDetections++;
		if (newtonDist < bestDist) newtonBeatsRaw++;
		if (newtonDist < statDist) newtonBeatsStatistic++;

		System.out.printf("mag=%-6.1f mult=%d  raw_dist=%.3e  statistic_dist=%.3e  newton_dist=%.3e  detected_mult=%d (true=%d) %s%n",
				mag, m, bestDist, statDist, newtonDist, detectedMult, m, (detectedMult == m ? "OK" : "MISS"));
		System.out.println("  taylor magnitudes at raw estimate: " + mags);
	}

	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setScientificON(6);

		double[] mags = { 1.0, 8.0, 30.0, 200.0 };
		int[] mults = { 2, 3, 5, 7, 9 };
		for (double mag : mags) {
			for (int m : mults) probeCase(mag, m);
			System.out.println();
		}

		System.out.println("=== SUMMARY over " + totalCases + " cases ===");
		System.out.println("multiplicity correctly detected: " + correctDetections + "/" + totalCases);
		System.out.println("newton polish beats raw estimate: " + newtonBeatsRaw + "/" + totalCases);
		System.out.println("newton polish beats STATISTIC centroid: " + newtonBeatsStatistic + "/" + totalCases);
	}
}
