package TestComplex;

import java.math.BigDecimal;
import java.math.MathContext;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.polynom.Polynom;

/**
 * Follow-up to ScratchEvalStabilityProbe01: isolates WHERE the ~1e33 residual comes from --
 * evaluation (Horner's arithmetic while evaluating) or construction (the coefficients themselves,
 * already corrupted by the double-precision .power()/.times() calls used to expand (z-r)^m into
 * monomial form). Evaluates the SAME stored coefficients with BigDecimal(60 digits) Horner: if
 * that also comes back far from 0, the corruption predates evaluation entirely -- no evaluation
 * algorithm, however precise, can fix it.
 */
public class ScratchEvalStabilityProbe02 {

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

	static BigDecimal[] evalHornerBigDecimal(Polynom p, BigDecimal xRe, BigDecimal xIm, MathContext mc) {
		int colLen = p.cols();
		BigDecimal accRe = new BigDecimal(p.getItem(0, colLen - 1).rep(), mc);
		BigDecimal accIm = new BigDecimal(p.getItem(0, colLen - 1).imp(), mc);
		for (int i = colLen - 2; i >= 0; --i) {
			BigDecimal newRe = accRe.multiply(xRe, mc).subtract(accIm.multiply(xIm, mc), mc)
					.add(new BigDecimal(p.getItem(0, i).rep(), mc), mc);
			BigDecimal newIm = accRe.multiply(xIm, mc).add(accIm.multiply(xRe, mc), mc)
					.add(new BigDecimal(p.getItem(0, i).imp(), mc), mc);
			accRe = newRe;
			accIm = newIm;
		}
		return new BigDecimal[] { accRe, accIm };
	}

	static void probeCase(double mag, int m) {
		double re = mag * 0.7, im = mag * 0.9 + 0.05;
		double thirdRoot = mag + 23.1;
		Polynom p = complexPairFactor(re, im, m).times(realFactor(thirdRoot, 1));
		Complex target = new Complex(re, im);

		// coefficient magnitude spread of the STORED (expanded) polynomial
		int colLen = p.cols();
		double minMod = Double.MAX_VALUE, maxMod = 0;
		for (int i = 0; i < colLen; ++i) {
			double mod = p.getItem(0, i).mod();
			if (mod == 0) continue;
			minMod = Math.min(minMod, mod);
			maxMod = Math.max(maxMod, mod);
		}

		Complex viaHorner = p.evalHorner(target);
		MathContext mc = new MathContext(60);
		BigDecimal[] viaBig = evalHornerBigDecimal(p, new BigDecimal(re, mc), new BigDecimal(im, mc), mc);
		double bigMod = Math.hypot(viaBig[0].doubleValue(), viaBig[1].doubleValue());

		System.out.printf(
				"mag=%-6.1f mult=%d  coefSpread(max/min)=%.3e  |evalHorner(target)|=%.3e  |BigDecimal60(target)|=%.3e  agree(Horner~Big)=%s%n",
				mag, m, maxMod / minMod, viaHorner.mod(), bigMod,
				(bigMod == 0 ? viaHorner.mod() < 1e-6 : Math.abs(viaHorner.mod() - bigMod) / bigMod < 0.05));
	}

	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setScientificON(6);

		double[] mags = { 1.0, 8.0, 30.0, 200.0 };
		int[] mults = { 2, 3, 5, 7, 9 };
		for (double mag : mags) {
			for (int m : mults) {
				probeCase(mag, m);
			}
			System.out.println();
		}
	}
}
