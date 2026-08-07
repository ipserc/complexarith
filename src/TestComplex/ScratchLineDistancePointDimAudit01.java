package TestComplex;

import com.ipserc.arith.geom.Line;
import com.ipserc.arith.geom.Point;

/**
 * Audit driver for a NEW finding discovered while verifying the Line.distance(Line)
 * generalization (Decimoctava sesion, continuacion, ver Claude/ComplexArithRev.md): a related but
 * DISTINCT, deeper limitation in Line.distance(Point) (and therefore in distance(Line)'s "parallel"
 * branch, which delegates to it). Read-only reconnaissance, no production code touched.
 *
 * distance(Point) is implemented as PaPp.crossprod(this.direction).norm()/this.direction.norm().
 * crossprod() is only mathematically well-founded in 3D and 7D (Hurwitz's theorem) -- for any other
 * dimension (including "in between" ones like 4,5,6, and anything beyond 7), VectorComplex.crossprod()
 * falls through its own switch statement to a default empty VectorComplex(), whose norm() is 0. That
 * makes distance(Point) SILENTLY return 0.0 for ANY point/line pair outside {3,7} dimensions,
 * regardless of the true distance -- confirmed below with a 10D case where the true answer is 5.0.
 *
 * distance2(Point) (via the already-Hermitian-fixed normalPoint(), Line.VERSION 1.3) does NOT
 * depend on crossprod() at all, and is checked here as a working alternative for dimensions where
 * distance(Point) is broken.
 */
public class ScratchLineDistancePointDimAudit01 {
	public static void main(String[] args) {
		Line line = new Line("1,0,0,0,0,0,0,0,0,0", "0,0,0,0,0,0,0,0,0,0");
		Point p = new Point("0,5,0,0,0,0,0,0,0,0");
		System.out.println("10D case, true answer = 5.0:");
		System.out.println("  distance(Point)  = " + line.distance(p) + "  <-- silently wrong (crossprod() degenerates beyond 3D/7D)");
		System.out.println("  distance2(Point) = " + line.distance2(p) + "  <-- via normalPoint(), does not use crossprod()");
	}
}
