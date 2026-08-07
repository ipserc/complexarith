package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Laplace;

/**
 * Verification driver for the Laplace.CLT() fix (Decimoctava sesion, continuacion, ver
 * Claude/ComplexArithRev.md). CLT() now evaluates s_n = sigma + j*2*pi*n/T (same grid as the
 * already-fixed DLT()) via direct numerical integration, instead of misusing each time sample's
 * own abscissa as the Laplace variable s.
 *
 * Ground truth: for f(t)=1 (constant) on [0,T], the Laplace-type integral
 * X(s) = INTEGRAL_0^T e^(-s*t) dt has the closed form (1-e^(-s*T))/s (s != 0). Compares CLT()'s
 * numerically-integrated coefficients against this closed form for several s_n (n=0..N-1, sigma
 * != 0 to keep s_0 away from the removable singularity at s=0).
 */
public class ScratchLaplaceCLTVerify01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, double diff, double tol) {
		boolean ok = diff < tol;
		System.out.println((ok ? "OK   " : "FAIL ") + label + " diff=" + diff + " tol=" + tol);
		if (ok) ++pass; else ++fail;
	}

	public static void main(String[] args) {
		double T = 2.0;
		double sigma = 0.3;
		int N = 4;
		int decPrec = 3; // same precision TestLaplace01.java uses for CLT() in practice

		java.util.function.Function<Complex, Complex> constOne = z -> Complex.ONE.copy();

		Laplace lap = new Laplace(constOne, 0.0, T);
		lap.setSigma(sigma);
		lap.CLT(N, decPrec);

		Complex period = new Complex(T, 0);
		for (int n = 0; n < N; ++n) {
			Complex sn = new Complex(sigma, 0).plus(Complex.i.times(Complex.DOS_PI * n).divides(period));
			Complex expected = Complex.ONE.minus(Complex.exp(sn.opposite().times(T))).divides(sn);
			Complex actual = lap.getTransformItem(n);
			double diff = actual.minus(expected).mod();
			check("CLT n=" + n + ": actual=" + actual + " expected=" + expected, diff, 1e-3);
		}

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
