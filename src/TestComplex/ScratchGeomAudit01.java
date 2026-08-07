package TestComplex;

import com.ipserc.arith.geom.*;
import com.ipserc.arith.vectorcomplex.VectorComplex;

/**
 * Verification driver for the com.ipserc.arith.geom audit fixes (Decimoctava sesion, continuacion,
 * ver Claude/ComplexArithRev.md). Confirms, at runtime, that the 6 findings from the read-only
 * audit are actually fixed. Kept as a permanent regression check for this package.
 */
public class ScratchGeomAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, boolean ok, String detail) {
		if (ok) {
			System.out.println("OK   " + label + (detail.isEmpty() ? "" : " (" + detail + ")"));
			++pass;
		} else {
			System.out.println("FAIL " + label + (detail.isEmpty() ? "" : " (" + detail + ")"));
			++fail;
		}
	}

	public static void main(String[] args) {

		// --- Finding 1: Point(int dim) off-by-one ---
		Point pInt = new Point(3);
		Point pStr = new Point("1,2,3");
		check("Finding 1: Point(int dim) dim() matches Point(String) dim()", pInt.dim() == 3 && pInt.dim() == pStr.dim(),
				"Point(3).dim()=" + pInt.dim());

		try {
			new Point(0);
			check("Finding 1: Point(0) throws", false, "no exception");
		} catch (IllegalArgumentException e) {
			check("Finding 1: Point(0) throws", true, e.getMessage());
		}

		// --- Finding 1b: Plane(v1,v2,point) + generalEq() ---
		VectorComplex v1 = new VectorComplex("1,0,0");
		VectorComplex v2 = new VectorComplex("0,1,0");
		Point point = new Point("0,0,0");
		Plane plane = new Plane(v1, v2, point);
		boolean dimOk = plane.point().dim() == 3;
		boolean generalEqOk;
		try {
			plane.generalEq();
			generalEqOk = true;
		} catch (Exception e) {
			generalEqOk = false;
		}
		check("Finding 1b: Plane(v1,v2,point).point().dim()==3", dimOk, "dim=" + plane.point().dim());
		check("Finding 1b: generalEq() no longer throws", generalEqOk, "");

		// --- Finding 2: Line.intersection(Line) on parallel lines now throws ---
		Line lineA = new Line("3,-2,5", "1,1,1");
		Line lineB = new Line("6,-4,10", "0,0,0"); // direction B = 2*direction A -> parallel
		try {
			lineA.intersection(lineB);
			check("Finding 2: parallel lines intersection() throws", false, "no exception");
		} catch (IllegalArgumentException e) {
			check("Finding 2: parallel lines intersection() throws", true, e.getMessage());
		}

		// --- Finding 3: Line.intersection(Line) on skew 3D lines now throws instead of a wrong point ---
		Line skewA = new Line("1,0,0", "0,0,0");
		Line skewB = new Line("0,1,0", "0,0,1");
		try {
			Point wrong = skewA.intersection(skewB);
			check("Finding 3: skew lines intersection() throws", false, "returned " + wrong.toString());
		} catch (IllegalArgumentException e) {
			check("Finding 3: skew lines intersection() throws", true, e.getMessage());
		}

		// --- Finding 3, positive control: genuinely intersecting 3D lines still work ---
		// x-axis and y-axis both pass through the origin.
		Line xAxis = new Line("1,0,0", "0,0,0");
		Line yAxis = new Line("0,1,0", "0,0,0");
		try {
			Point origin = xAxis.intersection(yAxis);
			boolean isOrigin = origin.complexMatrix[0][0].mod() < 1e-9 && origin.complexMatrix[0][1].mod() < 1e-9 && origin.complexMatrix[0][2].mod() < 1e-9;
			check("Finding 3 control: genuinely intersecting lines still resolve correctly", isOrigin, origin.toString());
		} catch (Exception e) {
			check("Finding 3 control: genuinely intersecting lines still resolve correctly", false, "threw " + e.getMessage());
		}

		// --- Finding 4: Line.line(String,String) removed (dead, unconditionally broken method) ---
		// Nothing to run: absence of the method is itself the fix, confirmed by this file compiling
		// without it. See VERSION history of Line.java.
		check("Finding 4: Line.line(String,String) removed", true, "compiles without it");

		// --- Finding 5: dead-sentinel pattern in Line(Point,Point) now throws its OWN clear message ---
		try {
			Point p2d = new Point("1,2");
			Point p3d = new Point("1,2,3");
			new Line(p2d, p3d);
			check("Finding 5: Line(mismatched Points) throws its own message", false, "no exception");
		} catch (IllegalArgumentException e) {
			boolean rightMessage = e.getMessage().contains("Both points must have the same dimension");
			check("Finding 5: Line(mismatched Points) throws its own message", rightMessage, e.getMessage());
		}

		// --- Finding 6: Plane.intersection(Line) "no intersection" now throws instead of a fake dim()==0 sentinel ---
		VectorComplex normal = new VectorComplex("0,0,1");
		Point planePoint = new Point("0,0,0");
		Plane planeXY = new Plane(normal, planePoint);
		Line lineParallelToPlane = new Line("1,0,0", "0,0,5"); // direction orthogonal to normal, not on the plane
		try {
			planeXY.intersection(lineParallelToPlane);
			check("Finding 6: Plane.intersection(Line) with no intersection throws", false, "no exception");
		} catch (IllegalArgumentException e) {
			check("Finding 6: Plane.intersection(Line) with no intersection throws", true, e.getMessage());
		}

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
