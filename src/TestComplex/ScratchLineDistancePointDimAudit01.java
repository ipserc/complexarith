package TestComplex;

import com.ipserc.arith.geom.Line;
import com.ipserc.arith.geom.Point;

/**
 * Regression driver for the Line.distance(Point) fix (Decimoctava sesion, continuacion, ver
 * Claude/ComplexArithRev.md, Line.VERSION 1.5). distance(Point) used to be
 * PaPp.crossprod(this.direction).norm()/this.direction.norm() -- crossprod() is only
 * well-founded up to 7D (Hurwitz's theorem); for dim>7 it falls back to a degenerate empty
 * vector whose norm() is 0, so distance(Point) silently returned 0.0 regardless of the true
 * distance. Now uses the same Hermitian projection as normalPoint() (fixed in VERSION 1.3),
 * valid in any dimension. distance2(Point) is now a thin delegate to distance(Point).
 */
public class ScratchLineDistancePointDimAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, double actual, double expected) {
		boolean ok = Math.abs(actual - expected) < 1e-9;
		System.out.println((ok ? "OK   " : "FAIL ") + label + " actual=" + actual + " expected=" + expected);
		if (ok) ++pass; else ++fail;
	}

	public static void main(String[] args) {
		// 3D case: must still match the classical crossprod-based answer.
		Line line3d = new Line("1,0,0", "0,0,0");
		Point p3d = new Point("0,5,0");
		check("3D distance(Point)", line3d.distance(p3d), 5.0);
		check("3D distance2(Point)", line3d.distance2(p3d), 5.0);

		// 10D case: true answer 5.0, used to silently return 0.0.
		Line line10d = new Line("1,0,0,0,0,0,0,0,0,0", "0,0,0,0,0,0,0,0,0,0");
		Point p10d = new Point("0,5,0,0,0,0,0,0,0,0");
		check("10D distance(Point) (was silently 0.0)", line10d.distance(p10d), 5.0);
		check("10D distance2(Point)", line10d.distance2(p10d), 5.0);

		// distance(Line)'s "parallel" branch delegates to distance(Point) -- must also be fixed for dim>7.
		Line line10dParallel = new Line("2,0,0,0,0,0,0,0,0,0", "0,5,0,0,0,0,0,0,0,0");
		check("10D distance(Line), parallel case (delegates to distance(Point))", line10d.distance(line10dParallel), 5.0);

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
