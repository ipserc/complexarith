package TestComplex;

import com.ipserc.arith.geom.Plane;
import com.ipserc.arith.geom.Point;

/**
 * Regression test for the Plane.distance() cleanup (Octava sesion, 1 agosto 2026). Its
 * dimension-mismatch guard used to reset internal state and call System.exit(0); that had
 * already become unreachable dead code as an incidental side effect of the VectorComplex(int)
 * audit fix earlier this session (new VectorComplex(0) now throws IllegalArgumentException
 * before System.exit(0) is ever reached) -- but the resulting exception message was misleading
 * ("Not valid dimension: 0" from VectorComplex, nothing about the plane/point mismatch). Cleaned
 * up to throw directly with a clear message.
 */
public class TestPlaneAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	public static void main(String[] args) {
		Plane plane3d = new Plane("0,0,1", "0,0,0");
		Point point2d = new Point("1,1");

		try {
			plane3d.distance(point2d);
			System.out.println("FAIL (no exception): distance() with mismatched dimensions");
			++fail;
		} catch (IllegalArgumentException e) {
			boolean mentionsDimension = e.getMessage().contains("dimension");
			if (mentionsDimension) {
				System.out.println("OK   (" + e.getMessage() + "): distance() with mismatched dimensions");
				++pass;
			} else {
				System.out.println("FAIL (wrong/misleading message): " + e.getMessage());
				++fail;
			}
		} catch (Exception e) {
			System.out.println("FAIL (wrong exception type " + e.getClass().getSimpleName() + "): distance() with mismatched dimensions");
			++fail;
		}

		// regression guard: matching dimensions must still work
		Point point3d = new Point("1,1,1");
		try {
			double d = plane3d.distance(point3d);
			System.out.println("OK   (no exception, distance=" + d + "): distance() with matching dimensions");
			++pass;
		} catch (Exception e) {
			System.out.println("FAIL (unexpected " + e + "): distance() with matching dimensions");
			++fail;
		}

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
