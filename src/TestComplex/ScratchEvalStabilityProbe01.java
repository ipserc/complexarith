package TestComplex;

import java.math.BigDecimal;
import java.math.MathContext;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.polynom.Polynom;

/**
 * Reproduces and MEASURES the "eval() near a root with extreme coefficient-magnitude dispersion"
 * candidate noted at the close of the Decimonovena sesion (Parte B/E of ComplexArithRev.md):
 * degree-19 polynomial, magnitude 200, multiplicity 9 -- the exact case where solveAberth()
 * overflowed and f(root) via evalHorner() came back around 9.5e31 despite the root being exact.
 * <p>
 * Compares 3 ways of evaluating f at a KNOWN exact root (constructed, not solved-for, so the true
 * value is exactly 0):
 * <ol>
 * <li>evalHorner(root) -- the current default, double-precision Horner over the EXPANDED
 * coefficients (huge dynamic range for a degree-19/magnitude-200 polynomial).</li>
 * <li>BigDecimal Horner, 60 significant digits -- ground truth / upper bound of what "just use
 * more precision" can buy, still over the same expanded coefficients.</li>
 * <li>Factored-form evaluation, double precision -- Complex.ONE times each (z - root_i) for the
 * exact roots used to CONSTRUCT the polynomial (this is the best case for the factored approach:
 * exact roots, not roots recovered by a solver).</li>
 * </ol>
 */
public class ScratchEvalStabilityProbe01 {

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

	/** Horner's method in BigDecimal, coefficients read from the (expanded) Polynom, ascending order. */
	static BigDecimal[] evalHornerBigDecimal(Polynom p, BigDecimal xRe, BigDecimal xIm, MathContext mc) {
		int colLen = p.cols();
		BigDecimal accRe = new BigDecimal(p.getItem(0, colLen - 1).rep(), mc);
		BigDecimal accIm = new BigDecimal(p.getItem(0, colLen - 1).imp(), mc);
		for (int i = colLen - 2; i >= 0; --i) {
			// acc = coef[i] + acc * x  (complex multiply/add, done component-wise in BigDecimal)
			BigDecimal newRe = accRe.multiply(xRe, mc).subtract(accIm.multiply(xIm, mc), mc)
					.add(new BigDecimal(p.getItem(0, i).rep(), mc), mc);
			BigDecimal newIm = accRe.multiply(xIm, mc).add(accIm.multiply(xRe, mc), mc)
					.add(new BigDecimal(p.getItem(0, i).imp(), mc), mc);
			accRe = newRe;
			accIm = newIm;
		}
		return new BigDecimal[] { accRe, accIm };
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

		System.out.println("degree=" + p.degree() + "  target root=" + target + "  |target|=" + target.mod());

		// --- 1. Current default: evalHorner over expanded double coefficients ---
		Complex viaHorner = p.evalHorner(target);
		System.out.println("evalHorner(target)          = " + viaHorner + "   |.|=" + viaHorner.mod());

		// --- 2. BigDecimal Horner, 60 significant digits, same expanded coefficients ---
		MathContext mc = new MathContext(60);
		BigDecimal xRe = new BigDecimal(re, mc);
		BigDecimal xIm = new BigDecimal(im, mc);
		BigDecimal[] viaBig = evalHornerBigDecimal(p, xRe, xIm, mc);
		System.out.println("BigDecimal Horner(60 digits) = (" + viaBig[0] + ", " + viaBig[1] + "i)");
		double bigModApprox = Math.hypot(viaBig[0].doubleValue(), viaBig[1].doubleValue());
		System.out.println("  |.| (cast back to double)  = " + bigModApprox);

		// --- 3. Factored form, double precision, using the EXACT construction roots ---
		// Evaluating exactly at "target" would trivially give 0 for both methods (target IS a
		// root) -- evaluate instead at a nearby probe point to get a meaningful comparison.
		Complex conj = new Complex(re, -im);
		Complex third = new Complex(thirdRoot, 0);
		Complex probe = target.plus(new Complex(1e-6, -1e-6));
		Complex fh = p.evalHorner(probe);
		BigDecimal pRe = new BigDecimal(probe.rep(), mc);
		BigDecimal pIm = new BigDecimal(probe.imp(), mc);
		BigDecimal[] fbig = evalHornerBigDecimal(p, pRe, pIm, mc);
		double fbigMod = Math.hypot(fbig[0].doubleValue(), fbig[1].doubleValue());

		Complex ffact = Complex.ONE;
		for (int i = 0; i < m; ++i) ffact = ffact.times(probe.minus(target));
		for (int i = 0; i < m; ++i) ffact = ffact.times(probe.minus(conj));
		ffact = ffact.times(probe.minus(third));

		System.out.println();
		System.out.println("=== probe point near the root (root perturbed by 1e-6-1e-6i), true value NOT zero ===");
		System.out.println("probe = " + probe);
		System.out.println("evalHorner(probe)            = " + fh + "   |.|=" + fh.mod());
		System.out.println("BigDecimal Horner(60 digits) = |.|=" + fbigMod + "  (ground truth)");
		System.out.println("factored double (exact roots) = " + ffact + "   |.|=" + ffact.mod());
		System.out.println();
		System.out.println("relative error evalHorner  vs ground truth = " + (Math.abs(fh.mod() - fbigMod) / fbigMod));
		System.out.println("relative error factored    vs ground truth = " + (Math.abs(ffact.mod() - fbigMod) / fbigMod));
	}
}
