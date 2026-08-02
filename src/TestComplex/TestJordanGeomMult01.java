package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Jordan;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for {@code Jordan.java}'s geometric-multiplicity-greater-than-1 support (ver
 * {@code Jordan.VERSION} 1.3 / {@code Claude/ComplexArithRev.md}): {@code
 * blockSizePartition(Complex,int)} + {@code eigenvectors(Complex,int)}'s chain-based
 * construction, resolving the previously-documented KNOWN LIMITATION that {@code block()} always
 * built a single Jordan chain per eigenvalue.
 * <p>
 * <b>Every case here uses an EXACT eigenvalue</b>, calling {@code blockSizePartition}/{@code
 * eigenvectors} directly rather than through {@code factorize()}'s real root-finding -- this is
 * what actually verifies the ALGORITHM: the block-size partition matches a known structure, and
 * {@code P*J*P^-1} (or the direct chain relation {@code N*v_1=0, N*v_k=v_{k-1}}, for matrices with
 * more than one eigenvalue) holds to machine precision, for several genuinely multi-block cases
 * (including {@code TestJordan02}'s own matrix and eigenvalue, in isolation from its root-finding
 * problem).
 * <p>
 * <b>Deliberately NOT tested here: the full pipeline (real root-finding) on ANY matrix.</b> A
 * bounded exploration during this fix found that end-to-end behavior for a defective eigenvalue of
 * algebraic multiplicity 3 or higher is fundamentally unpredictable once its eigenvalue comes from
 * Durand-Kerner/Aberth-Ehrlich instead of being exact -- REGARDLESS of how well-separated it is
 * from the matrix's other eigenvalues (confirmed with {@code lambda=50}, no other eigenvalue
 * anywhere near it): sometimes {@code factorize()} throws cleanly (either {@link
 * Jordan#blockSizePartition(Complex, int)}'s consistency check, or the NaN safety net added to
 * {@link Jordan#eigenvectors(Complex, int)}); sometimes it succeeds numerically but returns a
 * near-singular {@code P} (two generalized eigenvectors differing only in their 6th decimal, no
 * literal NaN anywhere) purely because {@code extendBasis()}'s {@code rank()}-based independence
 * check has its own blind spot at that error scale -- confirmed real case: {@code lambda=3}, a
 * 4x4 matrix with two {@code 2x2} chains. {@code TestJordan02} itself ({@code lambda=-2},
 * algebraic multiplicity 5) additionally never even gets its true multiplicity reported by {@link
 * Jordan#arithmeticMultiplicity(Complex)} (Durand-Kerner/Aberth-Ehrlich split it into 5
 * DIFFERENT-looking roots ~1e-3 apart), so the new machinery barely gets exercised for it at all,
 * and hits instead an ALREADY-PRE-EXISTING, separate fragility of {@code MatrixComplex.solve()}/
 * {@code inverse()} for a near-singular system, which prints an error and returns garbage ({@code
 * NaN}) instead of throwing. Asserting ANY specific full-pipeline outcome would test that much
 * deeper, pre-existing root-finding-precision problem, not this fix -- so this class deliberately
 * limits itself to what the fix actually controls: the exact-eigenvalue algorithm above.
 */
public class TestJordanGeomMult01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void report(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + " -- " + detail);
		if (ok) ++pass; else ++fail;
	}

	private static double maxAbsDiff(MatrixComplex a, MatrixComplex b) {
		double max = 0.0;
		for (int r = 0; r < a.rows(); ++r)
			for (int c = 0; c < a.cols(); ++c) {
				Complex d = a.getItem(r, c).minus(b.getItem(r, c));
				max = Math.max(max, Math.max(Math.abs(d.rep()), Math.abs(d.imp())));
			}
		return max;
	}

	private static MatrixComplex jordanBlock(int size, Complex lambda) {
		MatrixComplex j = new MatrixComplex(size, size);
		for (int i = 0; i < size; ++i) {
			j.setItem(i, i, lambda);
			if (i + 1 < size) j.setItem(i, i + 1, Complex.ONE);
		}
		return j;
	}

	private static MatrixComplex blockDiag(MatrixComplex... blocks) {
		int n = 0;
		for (MatrixComplex b : blocks) n += b.rows();
		MatrixComplex result = new MatrixComplex(n, n);
		int offset = 0;
		for (MatrixComplex b : blocks) {
			for (int i = 0; i < b.rows(); ++i)
				for (int j = 0; j < b.cols(); ++j)
					result.setItem(offset + i, offset + j, b.getItem(i, j));
			offset += b.rows();
		}
		return result;
	}

	// For a matrix with MORE than one eigenvalue, checking P*J*P^-1 with only ONE eigenvalue's
	// chains isn't meaningful (P wouldn't be square) -- verify the chain relation directly instead:
	// N*v_1=0, N*v_k=v_{k-1}. Valid regardless of what other eigenvalues the matrix has.
	private static void checkChainRelation(String label, MatrixComplex a, Complex eigenval, int arithMult, double tolerance) {
		Jordan jordan = new Jordan(a);
		java.util.List<Integer> blockSizes = jordan.blockSizePartition(eigenval, arithMult);
		MatrixComplex eigenVect = jordan.eigenvectors(eigenval, arithMult);
		int n = a.rows();
		MatrixComplex nMat = a.minus(MatrixComplex.eye(n).times(eigenval));

		double maxResidual = 0.0;
		int row = 0;
		for (int size : blockSizes) {
			MatrixComplex prev = null;
			for (int i = 0; i < size; ++i) {
				MatrixComplex v = eigenVect.getRow(row + i);
				MatrixComplex nv = nMat.times(v.transpose()).transpose();
				MatrixComplex expected = (i == 0) ? new MatrixComplex(1, n) : prev;
				for (int c = 0; c < n; ++c) maxResidual = Math.max(maxResidual, nv.getItem(0, c).minus(expected.getItem(0, c)).mod());
				prev = v;
			}
			row += size;
		}
		boolean ok = eigenVect.rows() == arithMult && maxResidual < tolerance;
		report(label, ok, "blockSizes=" + blockSizes + " rows=" + eigenVect.rows() + " maxResidual=" + maxResidual);
	}

	// Exact-eigenvalue check: partition + chain construction directly, P*J*P^-1 = A to machine precision.
	private static void checkExact(String label, MatrixComplex a, Complex eigenval, int arithMult, double tolerance) {
		Jordan jordan = new Jordan(a);
		java.util.List<Integer> blockSizes = jordan.blockSizePartition(eigenval, arithMult);
		MatrixComplex eigenVect = jordan.eigenvectors(eigenval, arithMult);

		MatrixComplex[] blocks = new MatrixComplex[blockSizes.size()];
		for (int i = 0; i < blockSizes.size(); ++i) blocks[i] = jordanBlock(blockSizes.get(i), eigenval);
		MatrixComplex j = blockDiag(blocks);
		MatrixComplex p = eigenVect.transpose();

		MatrixComplex reconstructed = p.times(j).times(p.inverse());
		double diff = maxAbsDiff(a, reconstructed);
		report(label, diff < tolerance, "blockSizes=" + blockSizes + " diff=" + diff);
	}

	public static void main(String[] args) {
		Complex.setFixedOFF();

		// TestJordan02's own matrix and eigenvalue, EXACT (not via root-finding) -- isolates the
		// new machinery from the separate root-finding-precision problem documented below.
		MatrixComplex testJordan02Matrix = new MatrixComplex(
			"-2, 0, 3, 4, 5;"
		  + " 0,-2, 0, 6, 7;"
		  + " 0, 0,-2, 0, 8;"
		  + " 0, 0, 0,-2, 0;"
		  + " 0, 0, 0, 0,-2");
		checkExact("TestJordan02 matrix, exact eigenvalue -2 (algebraic mult 5, geometric mult 2, blocks [3,2])",
			testJordan02Matrix, new Complex(-2, 0), 5, 1e-9);

		// Synthetic multi-block cases, P not orthogonal, exact eigenvalues.
		MatrixComplex p4 = new MatrixComplex("1,2,0,1;0,1,3,0;1,0,1,2;0,1,0,1");
		MatrixComplex j22 = blockDiag(jordanBlock(2, new Complex(3, 0)), jordanBlock(2, new Complex(3, 0)));
		checkExact("two blocks {2,2}, P not orthogonal", p4.times(j22).times(p4.inverse()), new Complex(3, 0), 4, 1e-6);

		MatrixComplex p5 = new MatrixComplex("1,2,0,1,0;0,1,3,0,1;1,0,1,2,0;0,1,0,1,1;1,0,1,0,1");
		MatrixComplex j221 = blockDiag(jordanBlock(2, new Complex(-4, 0)), jordanBlock(2, new Complex(-4, 0)), jordanBlock(1, new Complex(-4, 0)));
		checkExact("three blocks {2,2,1}, P not orthogonal", p5.times(j221).times(p5.inverse()), new Complex(-4, 0), 5, 1e-6);

		MatrixComplex diagPart = blockDiag(jordanBlock(1, new Complex(5, 0)), jordanBlock(1, new Complex(5, 0)), jordanBlock(1, new Complex(5, 0)));
		MatrixComplex full = blockDiag(diagPart, jordanBlock(2, new Complex(-4, 0)));
		MatrixComplex aFull = p5.times(full).times(p5.inverse());
		checkChainRelation("fully diagonalizable eigval (3 x 1x1 blocks) sharing a matrix with a 2x2 block",
			aFull, new Complex(5, 0), 3, 1e-6);
		checkChainRelation("the OTHER eigenvalue's 2x2 block in that same matrix",
			aFull, new Complex(-4, 0), 2, 1e-6);

		// Deliberately NOT tested here: the full pipeline (real root-finding) on any of these
		// matrices. A bounded exploration during this fix found that end-to-end behavior for a
		// defective eigenvalue is fundamentally unpredictable once its algebraic multiplicity is
		// 3+, REGARDLESS of how well-separated it is from other eigenvalues -- sometimes
		// factorize() throws cleanly (either from blockSizePartition's consistency check, or from
        // the NaN safety net in eigenvectors()); sometimes it succeeds numerically but returns a
		// near-singular P (two generalized eigenvectors differing only in their 6th decimal, no
		// literal NaN anywhere, so nothing currently detects it) purely because extendBasis()'s
		// rank()-based independence check has its own blind spot at that error scale. Asserting
		// ANY specific outcome for the full pipeline would be testing that pre-existing, much
		// deeper root-finding-precision problem, not this fix -- so this class deliberately limits
		// itself to what the fix actually controls: the exact-eigenvalue algorithm above.

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}

}
