package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Sigfunc;

/**
 * Audit driver for com.ipserc.arith.signal.Sigfunc (Decimoctava sesion, continuacion, ver
 * Claude/ComplexArithRev.md). Checks whether step(Complex,double,double) (the non-periodic
 * "rectangular function", 3-arg) is symmetric around 0 -- its own Javadoc never says so, unlike
 * its periodic sibling step(Complex,int,double,double), which explicitly documents "simmetric
 * respect 0". Read-only reconnaissance, no production code touched.
 */
public class ScratchSigfuncAudit01 {
	public static void main(String[] args) {
		// step(z, 3, 6): per its own Javadoc, "not null from 3, becomes null from 6" -- a one-sided
		// pulse. z=-4 has |z|=4, which falls in [3,6], so if step() is (unintentionally) symmetric,
		// step(-4,3,6) will read as 1 even though -4 is nowhere near the documented [3,6] interval.
		Complex z = new Complex(-4, 0);
		Complex result = Sigfunc.step(z, 3, 6);
		System.out.println("Sigfunc.step(-4, 3, 6) = " + result + "  (expected 0 if one-sided as documented; 1 confirms the unintended Math.abs() symmetry)");
	}
}
