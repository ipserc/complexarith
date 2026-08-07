package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.arith.polynom.Spline;

public class ScratchSplineAudit01 {

	static int pass = 0, fail = 0;

	static void check(String label, boolean ok, String detail) {
		if (ok) { pass++; System.out.println("OK   " + label); }
		else { fail++; System.out.println("FAIL " + label + " -- " + detail); }
	}

	// Independent reference implementation of the natural cubic spline (Thomas algorithm),
	// plain doubles, textbook formula -- deliberately NOT sharing any code with Spline.java.
	static class RefNaturalSpline {
		double[] x, y, M;
		int n; // number of intervals = points.length - 1

		RefNaturalSpline(double[] x, double[] y) {
			this.x = x; this.y = y;
			this.n = x.length - 1;
			this.M = new double[n + 1]; // M[0]=M[n]=0, interior solved
			if (n >= 2) solve();
		}

		double h(int i) { return x[i + 1] - x[i]; }

		void solve() {
			int m = n - 1; // number of unknowns M[1..n-1]
			double[] sub = new double[m], diag = new double[m], sup = new double[m], rhs = new double[m];
			for (int k = 0; k < m; ++k) {
				int i = k + 1; // interior index 1..n-1
				diag[k] = 2 * (h(i - 1) + h(i));
				rhs[k] = 6 * ((y[i + 1] - y[i]) / h(i) - (y[i] - y[i - 1]) / h(i - 1));
				if (k - 1 >= 0) sub[k] = h(i - 1);
				if (k + 1 < m) sup[k] = h(i);
			}
			// Thomas algorithm
			for (int k = 1; k < m; ++k) {
				double w = sub[k] / diag[k - 1];
				diag[k] -= w * sup[k - 1];
				rhs[k] -= w * rhs[k - 1];
			}
			double[] sol = new double[m];
			sol[m - 1] = rhs[m - 1] / diag[m - 1];
			for (int k = m - 2; k >= 0; --k) sol[k] = (rhs[k] - sup[k] * sol[k + 1]) / diag[k];
			for (int k = 0; k < m; ++k) M[k + 1] = sol[k];
		}

		int locate(double xv) {
			for (int i = 0; i < n; ++i) if (xv >= x[i] && xv <= x[i + 1]) return i;
			return -1;
		}

		double eval(double xv) {
			int i = locate(xv);
			double hi = h(i);
			double t1 = M[i] * Math.pow(x[i + 1] - xv, 3) / (6 * hi);
			double t2 = M[i + 1] * Math.pow(xv - x[i], 3) / (6 * hi);
			double t3 = (xv - x[i]) * (y[i + 1] / hi - M[i + 1] * hi / 6);
			double t4 = (x[i + 1] - xv) * (y[i] / hi - M[i] * hi / 6);
			return t1 + t2 + t3 + t4;
		}
	}

	static void checkDataset(String label, double[] xs, double[] ys, int nSamplesPerInterval) {
		StringBuilder pares = new StringBuilder();
		for (int i = 0; i < xs.length; ++i) {
			if (i > 0) pares.append("; ");
			pares.append(xs[i]).append(",").append(ys[i]);
		}
		MatrixComplex pTable = new MatrixComplex(pares.toString());
		Spline spline = new Spline(3, pTable);
		spline.interpolate(false);

		RefNaturalSpline ref = new RefNaturalSpline(xs, ys);

		double maxErr = 0;
		int n = xs.length - 1;
		for (int i = 0; i < n; ++i) {
			for (int s = 0; s <= nSamplesPerInterval; ++s) {
				double xv = xs[i] + (xs[i + 1] - xs[i]) * s / nSamplesPerInterval;
				double got = spline.eval(new Complex(xv)).rep();
				double expected = ref.eval(xv);
				double err = Math.abs(got - expected);
				if (err > maxErr) maxErr = err;
			}
		}
		check(label + " matches reference natural cubic spline (maxErr=" + maxErr + ")", maxErr < 1e-9,
			"maxErr=" + maxErr);

		// interpolation property: S(x_i) == y_i exactly (within numeric tolerance) at every knot
		for (int i = 0; i < xs.length; ++i) {
			double got = spline.eval(new Complex(xs[i])).rep();
			check(label + " interpolates knot " + i + " (x=" + xs[i] + ")", Math.abs(got - ys[i]) < 1e-9,
				"got=" + got + " expected=" + ys[i]);
		}
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();

		// 1) TestSpline02's own dataset: 6 points, real-valued
		checkDataset("6-point dataset",
			new double[]{1, 2, 3, 4, 5, 6},
			new double[]{2.9, 1.3, 2.4, 1.9, 1.0, 2.1}, 9);

		// 2) 4 points (odd interval count = 3, exercises the last-row guard in the tridiagonal build)
		checkDataset("4-point dataset",
			new double[]{0, 1, 2, 3},
			new double[]{0, 1, 0, 1}, 7);

		// 3) 5 points (interval count = 4, even)
		checkDataset("5-point dataset",
			new double[]{-2, -1, 0, 1, 2},
			new double[]{4, 1, 0, 1, 4}, 7);

		// 4) irregular spacing
		checkDataset("irregular-spacing dataset",
			new double[]{0, 0.5, 2.0, 2.5, 5.0},
			new double[]{0, 1.2, -0.5, 0.3, 2.0}, 11);

		// 5) minimal case: 3 points (2 intervals, 1 unknown M) -- smallest non-degenerate system
		checkDataset("3-point dataset",
			new double[]{0, 1, 3},
			new double[]{1, 3, 2}, 9);

		// 6) degenerate: exactly 2 points (1 interval, 0 unknowns -- Syseq(0))
		boolean twoPointOk = true;
		String twoPointDetail = "";
		try {
			MatrixComplex pTable = new MatrixComplex("0,1; 2,5");
			Spline spline = new Spline(3, pTable);
			spline.interpolate(false);
			double mid = spline.eval(new Complex(1.0)).rep();
			double expectedLinear = 1 + (5 - 1) * (1.0 - 0) / (2 - 0); // straight line between the 2 points
			twoPointOk = Math.abs(mid - expectedLinear) < 1e-9;
			twoPointDetail = "got=" + mid + " expected(linear)=" + expectedLinear;
		} catch (Exception | Error e) {
			twoPointOk = false;
			twoPointDetail = "threw " + e;
		}
		check("2-point degenerate case reduces to a straight line", twoPointOk, twoPointDetail);

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
	}
}
