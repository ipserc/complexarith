package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Sigfunc;

/**
 * Regression driver for the Sigfunc.step(Complex,double,double) symmetry fix (Decimoctava sesion,
 * continuacion, ver Claude/ComplexArithRev.md, Sigfunc.VERSION 1.2). This 3-arg, non-periodic
 * "rectangular function" used to be unintentionally symmetric around 0 (Math.abs(z.rep())),
 * unlike its periodic sibling step(Complex,int,double,double), which documents symmetry
 * explicitly. Fixed to a plain one-sided pulse matching its own Javadoc.
 */
public class ScratchSigfuncAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, boolean ok) {
		System.out.println((ok ? "OK   " : "FAIL ") + label);
		if (ok) ++pass; else ++fail;
	}

	public static void main(String[] args) {
		// step(z, 3, 6): "not null from 3, becomes null from 6" -- a one-sided pulse.
		check("step(-4,3,6) = 0 (outside [3,6], not the old Math.abs()-mirrored 1)",
				Sigfunc.step(new Complex(-4, 0), 3, 6).equals(Complex.ZERO));
		check("step(4,3,6) = 1 (inside [3,6])",
				Sigfunc.step(new Complex(4, 0), 3, 6).equals(Complex.ONE));
		check("step(2,3,6) = 0 (below ton)",
				Sigfunc.step(new Complex(2, 0), 3, 6).equals(Complex.ZERO));
		check("step(7,3,6) = 0 (above toff)",
				Sigfunc.step(new Complex(7, 0), 3, 6).equals(Complex.ZERO));

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
