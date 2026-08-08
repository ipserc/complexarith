package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.QRSchurfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;

/**
 * Investigates whether the companion-matrix + QRSchurfactor approach (standard technique, same one
 * MATLAB's roots() uses internally) gives better raw root precision than Polynom's own
 * solveWeierstrass()/solveAberth()/solveRobust() (Durand-Kerner/Aberth-Ehrlich applied directly to
 * the polynomial coefficients) -- motivated by the just-confirmed finding that QRSchurfactor's raw
 * eigenvalue estimates are far tighter than a characteristic-polynomial root-finder's, for the exact
 * same underlying numerical problem (a repeated/clustered value in floating point).
 */
public class ScratchPolynomQRCompanion01 {

	static Polynom realFactor(double root, int mult) {
		Polynom f = new Polynom("1, " + (-root));
		return f.power(mult);
	}

	static MatrixComplex companionMatrix(Polynom p) {
		int degree = p.degree();
		Polynom norm = p.normalize(); // monic: complexMatrix[0][i] holds the coefficient of x^i
		MatrixComplex C = new MatrixComplex(degree, degree);
		for (int i = 1; i < degree; ++i) {
			C.setItem(i, i - 1, Complex.ONE);
		}
		for (int i = 0; i < degree; ++i) {
			C.setItem(i, degree - 1, norm.getItem(0, i).opposite());
		}
		return C;
	}

	/** Sorted-by-modulus pairwise nearest-neighbor spread within the group closest to the target
	 * (crude but sufficient proxy for "how tight is the ring of estimates around the true root"):
	 * returns max distance between the target and its `mult` nearest raw roots. */
	static double clusterSpread(MatrixComplex raw, Complex target, int mult) {
		int n = raw.rows();
		double[] dist = new double[n];
		for (int i = 0; i < n; ++i) dist[i] = raw.getItem(i, 0).minus(target).mod();
		java.util.Arrays.sort(dist);
		return dist[mult - 1]; // distance of the mult-th closest raw root to the target
	}

	static void compareCase(String label, double mag, int mult) {
		double rep = mag * 1.234567;
		Polynom p = realFactor(rep, mult).times(realFactor(mag + 17.3, 1)).times(realFactor(-mag - 9.1, 1));

		System.out.println("=== " + label + " mag=" + mag + " mult=" + mult + " target=" + rep + " ===");

		String detResult;
		double detSpread = Double.NaN;
		try {
			MatrixComplex raw = p.solveRobust();
			detSpread = clusterSpread(raw, new Complex(rep, 0), mult);
			detResult = "spread=" + detSpread;
		} catch (IllegalArgumentException e) {
			detResult = "EXCEPTION: " + e.getMessage().substring(0, Math.min(60, e.getMessage().length()));
		}

		String qrResult;
		double qrSpread = Double.NaN;
		try {
			MatrixComplex C = companionMatrix(p);
			MatrixComplex rawQR = new QRSchurfactor(C).getEigenvalues();
			qrSpread = clusterSpread(rawQR, new Complex(rep, 0), mult);
			qrResult = "spread=" + qrSpread;
		} catch (Exception e) {
			qrResult = "EXCEPTION: " + e;
		}

		System.out.println("  solveRobust (Durand-Kerner/Aberth): " + detResult);
		System.out.println("  companion+QRSchurfactor:            " + qrResult);
		if (!Double.isNaN(detSpread) && !Double.isNaN(qrSpread) && qrSpread > 0) {
			System.out.printf("  ratio (DK-Aberth/QR): %.2fx%n", detSpread / qrSpread);
		}
	}

	static Polynom complexPairFactor(double re, double im, int mult) {
		double c1 = -2 * re;
		double c0 = re * re + im * im;
		Polynom f = new Polynom("1, " + c1 + ", " + c0);
		return f.power(mult);
	}

	/** Reproduces the exact OVERFLOW cases found in Fase 2 (complex conjugate pair, high magnitude,
	 * high multiplicity) -- checks whether companion+QRSchurfactor rescues what solveRobust() could
	 * not, the same way solveAberth() already rescues solveWeierstrass()'s own overflow cases. */
	static void compareOverflowCase(double mag, int m) {
		double re = mag * 0.7, im = mag * 0.9 + 0.05;
		Polynom p = complexPairFactor(re, im, m).times(realFactor(mag + 23.1, 1));

		System.out.println("=== OVERFLOW-CHECK mag=" + mag + " mult=" + m + " (complex pair, degree=" + p.degree() + ") ===");
		try {
			p.solveRobust();
			System.out.println("  solveRobust: OK (no exception)");
		} catch (IllegalArgumentException e) {
			System.out.println("  solveRobust: EXCEPTION (" + e.getMessage().substring(0, Math.min(50, e.getMessage().length())) + ")");
		}
		try {
			MatrixComplex C = companionMatrix(p);
			MatrixComplex rawQR = new QRSchurfactor(C).getEigenvalues();
			double spread1 = clusterSpread(rawQR, new Complex(re, im), m);
			double spread2 = clusterSpread(rawQR, new Complex(re, -im), m);
			System.out.println("  companion+QR: OK, spread(+im)=" + spread1 + " spread(-im)=" + spread2);
		} catch (Exception e) {
			System.out.println("  companion+QR: EXCEPTION (" + e + ")");
		}
	}

	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setScientificON(6);

		double[] mags = {1.0, 8.0, 30.0, 200.0};
		int[] mults = {2, 3, 4, 5, 6, 7, 8, 9};

		for (double mag : mags) {
			for (int m : mults) {
				compareCase("isolated", mag, m);
			}
		}

		System.out.println();
		System.out.println("################ OVERFLOW CASES FROM FASE 2 (complex pair) ################");
		compareOverflowCase(8.0, 9);
		compareOverflowCase(30.0, 7);
		compareOverflowCase(30.0, 8);
		compareOverflowCase(30.0, 9);
		compareOverflowCase(200.0, 6);
		compareOverflowCase(200.0, 7);
		compareOverflowCase(200.0, 8);
		compareOverflowCase(200.0, 9);
	}
}
