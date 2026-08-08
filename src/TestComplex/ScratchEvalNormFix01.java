package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.polynom.Polynom;

/**
 * Verifies the evalNorm(double) fix (Polynom.VERSION 1.20 -> 1.21): before the fix it called
 * evalFact(cNum) (power method over the UNNORMALIZED coefficients) instead of evalNorm(cNum)
 * (Horner over polyNorm, the normalized polynomial). Compares evalNorm(double) against the
 * known-equivalent public path normalize().eval(value) (documented Decimonovena sesion, Parte E)
 * for a non-monic polynomial -- a monic one would mask the bug, since normalizing a monic
 * polynomial is a no-op.
 */
public class ScratchEvalNormFix01 {
	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setScientificON(10);

		// Non-monic: leading coefficient 6, on purpose (monic would mask the old bug).
		// Polynom(String) takes natural (highest-degree-first) order and reverses it internally,
		// so "6, -5, 4" means 6x^2 - 5x + 4.
		Polynom p = new Polynom("6, -5, 4");
		p.normalize(); // populates p.polyNorm as a side effect (evalNorm(Complex) reads it directly)
		double[] points = {0.0, 1.0, 2.5, -3.0};

		boolean allOK = true;
		for (double x : points) {
			Complex viaEvalNorm = p.evalNorm(x);
			Complex viaNormalizeEval = p.normalize().eval(x);
			double diff = viaEvalNorm.minus(viaNormalizeEval).mod();
			boolean ok = diff < 1e-9;
			allOK &= ok;
			System.out.println("x=" + x + "  evalNorm(x)=" + viaEvalNorm
					+ "  normalize().eval(x)=" + viaNormalizeEval
					+ "  diff=" + diff + "  " + (ok ? "OK" : "MISMATCH"));
		}
		System.out.println(allOK ? "ALL OK" : "FAILURE");
	}
}
