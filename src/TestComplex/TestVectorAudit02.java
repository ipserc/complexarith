package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.vectorcomplex.VectorComplex;

import java.util.Random;

/**
 * Regression test for {@code VectorComplex.vectorprodN(VectorComplex...)} (ver {@code
 * VectorComplex.VERSION} 1.9 / {@code Claude/ComplexArithRev.md}): the n-dimensional
 * generalization of the (n-1)-ary Levi-Civita vector product, deferred since VERSION 1.8.
 * <p>
 * A genuine BINARY vector product (2 operands, orthogonal to both) only exists with the classical
 * defining properties in 3 and 7 dimensions (Hurwitz's theorem) -- {@code vectorprod(VectorComplex)}
 * itself is deliberately left untouched (still valid only in 3D, per the user's explicit request
 * not to touch its ~20 existing 3D call sites). {@code vectorprodN} instead generalizes the
 * (n-1)-ARY construction (n-1 vectors in, one out), which is well-defined in every dimension.
 * <p>
 * Four checks:
 * <ol>
 * <li>3D consistency: {@code vectorprodN} with 1 extra vector must match {@code vectorprod}
 * bit-for-bit.</li>
 * <li>Standard basis vectors in 4D-6D: {@code e_1^e_2^...^e_(n-1)} must give exactly
 * {@code +-e_n}.</li>
 * <li>Orthogonality: the result must be BILINEAR-orthogonal (no conjugate -- {@code dotprod()}'s
 * Hermitian inner product is the wrong relation here) to every one of the {@code n-1} input
 * vectors, for random complex vectors in 4D-6D.</li>
 * <li>Explicit validation: wrong argument count or mismatched vector dimension must throw.</li>
 * </ol>
 */
public class TestVectorAudit02 {

	private static int pass = 0;
	private static int fail = 0;

	private static void report(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + " -- " + detail);
		if (ok) ++pass; else ++fail;
	}

	private static VectorComplex randVec(int n, Random rnd) {
		VectorComplex v = new VectorComplex(n);
		for (int i = 0; i < n; ++i) v.setCoord(i, new Complex(rnd.nextInt(21) - 10, rnd.nextInt(21) - 10));
		return v;
	}

	private static double maxDiff(VectorComplex a, VectorComplex b) {
		double max = 0.0;
		for (int i = 0; i < a.dim(); ++i) max = Math.max(max, a.getCoord(i).minus(b.getCoord(i)).mod());
		return max;
	}

	// Bilinear (no conjugate) dot product -- the correct orthogonality relation for the
	// Levi-Civita/wedge-product construction; dotprod()/innerprod() use adjoint() (Hermitian),
	// the wrong relation for a complex-valued antisymmetric product like this one.
	private static Complex bilinearDot(VectorComplex a, VectorComplex b) {
		Complex sum = new Complex(0, 0);
		for (int i = 0; i < a.dim(); ++i) sum = sum.plus(a.getCoord(i).times(b.getCoord(i)));
		return sum;
	}

	public static void main(String[] args) {
		Complex.setFixedOFF();
		Random rnd = new Random(3);

		int mismatches3D = 0;
		double maxDiff3D = 0.0;
		for (int t = 0; t < 20; ++t) {
			VectorComplex u = randVec(3, rnd), v = randVec(3, rnd);
			double diff = maxDiff(u.vectorprod(v), u.vectorprodN(v));
			maxDiff3D = Math.max(maxDiff3D, diff);
			if (diff > 1e-9) ++mismatches3D;
		}
		report("3D consistency with vectorprod() (20 random pairs)", mismatches3D == 0, "maxDiff=" + maxDiff3D);

		for (int n = 4; n <= 6; ++n) {
			VectorComplex[] basis = new VectorComplex[n];
			for (int i = 0; i < n; ++i) {
				basis[i] = new VectorComplex(n);
				basis[i].setCoord(i, Complex.ONE);
			}
			VectorComplex[] extra = new VectorComplex[n - 2];
			for (int i = 0; i < n - 2; ++i) extra[i] = basis[i + 1];
			VectorComplex result = basis[0].vectorprodN(extra);

			VectorComplex expectedPos = new VectorComplex(n);
			expectedPos.setCoord(n - 1, Complex.ONE);
			VectorComplex expectedNeg = new VectorComplex(n);
			expectedNeg.setCoord(n - 1, new Complex(-1, 0));
			boolean ok = maxDiff(result, expectedPos) < 1e-9 || maxDiff(result, expectedNeg) < 1e-9;
			report(n + "D: e1^e2^...^e" + (n - 1) + " = +-e" + n, ok, "result=" + result.toMatrixComplex());
		}

		for (int n = 4; n <= 6; ++n) {
			VectorComplex first = randVec(n, rnd);
			VectorComplex[] extra = new VectorComplex[n - 2];
			for (int i = 0; i < n - 2; ++i) extra[i] = randVec(n, rnd);
			VectorComplex result = first.vectorprodN(extra);

			double maxDot = bilinearDot(first, result).mod();
			for (VectorComplex e : extra) maxDot = Math.max(maxDot, bilinearDot(e, result).mod());
			report(n + "D orthogonality (bilinear, random complex vectors)", maxDot < 1e-9, "maxDot=" + maxDot);
		}

		try {
			VectorComplex u = randVec(4, rnd);
			u.vectorprodN(randVec(4, rnd));
			report("wrong argument count throws", false, "did not throw");
		} catch (IllegalArgumentException e) {
			report("wrong argument count throws", true, e.getMessage());
		}

		try {
			VectorComplex u = randVec(4, rnd);
			u.vectorprodN(randVec(4, rnd), randVec(3, rnd));
			report("mismatched vector dimension throws", false, "did not throw");
		} catch (IllegalArgumentException e) {
			report("mismatched vector dimension throws", true, e.getMessage());
		}

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}

}
