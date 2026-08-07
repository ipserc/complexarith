package TestComplex;

import com.ipserc.arith.geom.Line;
import com.ipserc.arith.geom.Plane;
import com.ipserc.arith.vectorcomplex.VectorComplex;

/**
 * Second-pass audit driver for com.ipserc.arith.geom (Decimoctava sesion, continuacion, ver
 * Claude/ComplexArithRev.md), covering methods NOT touched by the first pass's 6 findings.
 *
 * Finding A (FIXED, VectorComplex.VERSION 1.10): angle(VectorComplex) had the same acos-domain
 * fragility already removed from Line.java's own distance(Line)/intersection(Line) last session,
 * but was never fixed in the shared method -- reached from Plane.angle()/distance(Line)/
 * distance(Plane)/intersection(Plane). This section is now a regression check (OK/FAIL), verifying
 * the fix.
 *
 * Finding B (confirmed, NOT fixed yet -- awaiting a decision): Line.normalPoint() uses the
 * bilinear inner product (direction[i].power(2)) instead of the Hermitian one (|direction[i]|^2,
 * via dotprod/adjoint) used consistently elsewhere in the project. Kept here as a live
 * demonstration for whenever that gets addressed.
 */
public class ScratchGeomAudit02 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + (detail.isEmpty() ? "" : " (" + detail + ")"));
		if (ok) ++pass; else ++fail;
	}

	public static void main(String[] args) {

		System.out.println("--- Finding A (FIXED): VectorComplex.angle() on parallel vectors, must not be NaN ---");
		String[][] pairs = {
			{"2,3", "4,6"},
			{"1,2,3", "2,4,6"},
			{"3,-2,5", "6,-4,10"},
			{"1,1,1,1", "3,3,3,3"},
			{"2,1,-2", "-4,-2,4"},
		};
		for (String[] p : pairs) {
			VectorComplex a = new VectorComplex(p[0]);
			VectorComplex b = new VectorComplex(p[1]);
			double angle = a.angle(b);
			check("angle(" + p[0] + " , " + p[1] + ") = " + angle, !Double.isNaN(angle) && angle < 1e-9, "");
		}

		System.out.println();
		System.out.println("--- Finding A (FIXED): Plane.distance(Plane) on genuinely parallel-and-distinct planes ---");
		Plane planeA = new Plane("3,-2,5", "1,1,1");
		Plane planeB = new Plane("6,-4,10", "0,0,0"); // normal B = 2 * normal A -> parallel
		double dist = planeA.distance(planeB);
		check("distance(parallel, distinct planes) = " + dist, dist > 1e-9, "must be > 0, was silently 0.0 before the fix");

		System.out.println();
		System.out.println("--- Finding B (confirmed, NOT fixed): Line.normalPoint() bilinear vs Hermitian ---");
		Line line2 = new Line("1+1i,2", "0,0");
		com.ipserc.arith.geom.Point q2 = new com.ipserc.arith.geom.Point("1,3");
		System.out.println("distance : " + line2.distance(q2) + "  (crossprod-based, Hermitian-consistent)");
		System.out.println("distance2: " + line2.distance2(q2) + "  (via normalPoint(), bilinear -- diverges, ~33% off)");

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
