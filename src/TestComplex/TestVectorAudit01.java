package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.vectorcomplex.VectorComplex;

/**
 * Regression test for the VectorComplex.java audit fixes (Octava sesion, 1 agosto 2026).
 * Covers, in order: Fase A (aliasing fix), Fase B (System.exit/silent-null -> IllegalArgumentException),
 * Fase C (division by zero -> IllegalArgumentException). Fase D (vectorprod doc only, no test needed).
 */
public class TestVectorAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void expectException(String label, Runnable action) {
		try {
			action.run();
			System.out.println("FAIL (no exception): " + label);
			++fail;
		} catch (IllegalArgumentException e) {
			System.out.println("OK   (" + e.getMessage() + "): " + label);
			++pass;
		} catch (Exception e) {
			System.out.println("FAIL (wrong exception type " + e.getClass().getSimpleName() + "): " + label);
			++fail;
		}
	}

	private static void expectNoException(String label, Runnable action) {
		try {
			action.run();
			System.out.println("OK   (no exception): " + label);
			++pass;
		} catch (Exception e) {
			System.out.println("FAIL (unexpected " + e.getClass().getSimpleName() + " " + e.getMessage() + "): " + label);
			++fail;
		}
	}

	public static void main(String[] args) {

		// ---- Fase A: aliasing fix in VectorComplex(MatrixComplex row) ----
		MatrixComplex row = new MatrixComplex("1,2,3");
		VectorComplex v = new VectorComplex(row);
		Complex before = v.getCoord(0);
		row.setItem(0, 0, new Complex(99.0, 0.0));
		Complex after = v.getCoord(0);
		if (before.equals(after)) {
			System.out.println("OK   (vector isolated from row mutation): Fase A aliasing fix");
			++pass;
		} else {
			System.out.println("FAIL (vector.getCoord(0) changed from " + before + " to " + after + "): Fase A aliasing fix");
			++fail;
		}

		// ---- Fase B: constructors that used to leave complexMatrix=null in silence ----
		// Note: dim==0 is the actual "silently null" case this fase fixes (new Complex[1][0]
		// doesn't throw on its own). dim<0 already threw NegativeArraySizeException before this
		// fase too (new Complex[1][dim] fails inside super(1,dim), never reaching our own guard) --
		// a preexisting, less-informative RuntimeException, out of scope here, not a regression.
		expectException("new VectorComplex(0)", () -> new VectorComplex(0));
		expectException("new VectorComplex(\"1,2;3,4\") (2 rows)", () -> new VectorComplex("1,2;3,4"));
		expectException("new VectorComplex(MatrixComplex 2x2)", () -> new VectorComplex(new MatrixComplex("1,2;3,4")));

		// valid constructors must keep working
		expectNoException("new VectorComplex(3)", () -> new VectorComplex(3));
		expectNoException("new VectorComplex(\"1,2,3\")", () -> new VectorComplex("1,2,3"));
		expectNoException("new VectorComplex(MatrixComplex 1x3)", () -> new VectorComplex(new MatrixComplex("1,2,3")));

		// ---- Fase B: plus/minus used to System.exit(1) ----
		VectorComplex v3 = new VectorComplex("1,2,3");
		VectorComplex v4 = new VectorComplex("1,2,3,4");
		expectException("v3.plus(v4) (size 3 vs 4)", () -> v3.plus(v4));
		expectException("v3.minus(v4) (size 3 vs 4)", () -> v3.minus(v4));
		expectNoException("v3.plus(v3)", () -> v3.plus(v3));
		expectNoException("v3.minus(v3)", () -> v3.minus(v3));

		// ---- Fase B: innerprod/dotprod/crossprod used to warn on stderr and keep going ----
		expectException("v3.innerprod(v4) (size mismatch)", () -> v3.innerprod(v4));
		expectException("v3.dotprod(v4) (size mismatch)", () -> v3.dotprod(v4));
		expectException("v3.crossprod(v4) (size mismatch)", () -> v3.crossprod(v4));
		expectNoException("v3.innerprod(v3)", () -> v3.innerprod(v3));
		expectNoException("v3.crossprod(v3)", () -> v3.crossprod(v3));

		// ---- Fase C: division by zero used to give a silent NaN/Infinity ----
		VectorComplex nullVector = new VectorComplex(3);   // initialized to zero, per the class Javadoc
		VectorComplex nonNull = new VectorComplex("1,0,0");

		expectException("nullVector.normalize()", () -> nullVector.normalize());
		expectNoException("nonNull.normalize()", () -> nonNull.normalize());

		expectException("nonNull.projectionScalar(nullVector)", () -> nonNull.projectionScalar(nullVector));
		expectException("nonNull.projection(nullVector)", () -> nonNull.projection(nullVector));
		expectNoException("nonNull.projectionScalar(nonNull)", () -> nonNull.projectionScalar(nonNull));
		expectNoException("nonNull.projection(nonNull)", () -> nonNull.projection(nonNull));

		expectException("nullVector.angleps(nonNull)", () -> nullVector.angleps(nonNull));
		expectException("nonNull.angleps(nullVector)", () -> nonNull.angleps(nullVector));
		expectNoException("nonNull.angleps(nonNull)", () -> nonNull.angleps(nonNull));

		expectException("nullVector.angle(nonNull)", () -> nullVector.angle(nonNull));
		expectException("nonNull.angle(nullVector)", () -> nonNull.angle(nullVector));
		expectNoException("nonNull.angle(nonNull)", () -> nonNull.angle(nonNull));

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
