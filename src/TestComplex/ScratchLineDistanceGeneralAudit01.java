package TestComplex;

import com.ipserc.arith.geom.Line;

/**
 * Verification driver for the Line.distance(Line) generalization to any dimension (Decimoctava
 * sesion, continuacion, ver Claude/ComplexArithRev.md, Line.VERSION 1.4). Resolves the KNOWN
 * LIMITATION documented since VERSION 1.1: the "not parallel" branch used to rely on
 * mixedprod()/crossprod(), only valid in 3D.
 *
 * This file is compiled and run against TWO builds (current HEAD, before this fix, and the new
 * fix) with the SAME 3D test cases -- their outputs must match to confirm the general formula
 * reduces correctly to the known-good 3D behavior. It also exercises 2D/4D/5D/7D cases that only
 * the new build can run at all (the old build's crossprod()/mixedprod() throw or misbehave beyond
 * 3D per the documented limitation).
 */
public class ScratchLineDistanceGeneralAudit01 {
	public static void main(String[] args) {
		System.out.println("=== 3D cases (must match old build exactly) ===");
		printDistance("3D skew #1", new Line("1,0,0", "0,0,0"), new Line("0,1,0", "0,0,1"));
		printDistance("3D skew #2", new Line("2,1,-2", "2,1,-1"), new Line("-4,-2,4", "-2,0,5"));
		printDistance("3D skew #3", new Line("1,1,1", "0,0,0"), new Line("1,-1,0", "1,0,0"));
		printDistance("3D parallel, distinct", new Line("1,0,0", "0,0,0"), new Line("1,0,0", "0,1,0"));
		printDistance("3D complex direction", new Line("1+1i,0,0", "0,0,0"), new Line("0,1,0", "0,0,1+2i"));

		System.out.println();
		System.out.println("=== 2D case (must be exactly 0.0, non-parallel lines always intersect) ===");
		printDistance("2D non-parallel", new Line("1,0", "0,0"), new Line("0,1", "3,3"));

		System.out.println();
		System.out.println("=== dim>3 cases (KNOWN LIMITATION before this fix) ===");
		printDistance("4D skew", new Line("1,0,0,0", "0,0,0,0"), new Line("0,1,0,0", "0,0,1,1"));
		printDistance("5D skew", new Line("1,0,0,0,0", "0,0,0,0,0"), new Line("0,1,1,0,0", "0,0,0,1,1"));
		printDistance("7D skew", new Line("1,0,0,0,0,0,0", "0,0,0,0,0,0,0"), new Line("0,1,0,1,0,1,0", "0,0,1,0,1,0,1"));

		System.out.println();
		System.out.println("=== dim>7 cases (beyond crossprod()'s own ceiling, Hurwitz's theorem) ===");
		printDistance("10D skew", new Line("1,0,0,0,0,0,0,0,0,0", "0,0,0,0,0,0,0,0,0,0"), new Line("0,1,0,0,0,0,0,0,0,0", "0,0,1,0,0,0,0,0,0,0"));
		printDistance("10D parallel, distinct", new Line("1,0,0,0,0,0,0,0,0,0", "0,0,0,0,0,0,0,0,0,0"), new Line("2,0,0,0,0,0,0,0,0,0", "0,1,0,0,0,0,0,0,0,0"));
	}

	private static void printDistance(String label, Line a, Line b) {
		try {
			double d = a.distance(b);
			System.out.println(label + ": distance = " + d);
		} catch (Exception e) {
			System.out.println(label + ": threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
}
