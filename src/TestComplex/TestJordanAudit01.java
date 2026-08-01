package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Jordan;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for the Jordan.java fixes (Octava sesion, 1 agosto 2026): values()/vectors()
 * replaced with roots()/solutions() (compile fix); the appendRows() return-value-discarded bug in
 * eigenvectors()/factorize(); and block()'s superdiagonal condition (checked global matrix bounds
 * instead of "still inside this eigenvalue's own chain", silently corrupting the Jordan matrix
 * whenever a simple eigenvalue -- arithMult=1 -- wasn't processed last). Covers the case this
 * class is verified correct for: every eigenvalue with geometric multiplicity exactly 1.
 * <p>
 * Does NOT cover eigenvalues with geometric multiplicity greater than 1 (including the
 * diagonalizable-for-that-eigenvalue case, geomMult==arithMult) -- documented KNOWN LIMITATION in
 * Jordan.factorize()'s own Javadoc, not fixed in this pass.
 * <p>
 * <b>Novena sesion (1 agosto 2026) -- root-caused, both root causes fixed, residual tolerance
 * relaxed for the defective case.</b> The "simple eigenvalue processed first" case below started
 * failing once Durand-Kerner's seeding became deterministic (Cauchy's bound, commit 0ad61fb): the
 * two roots Durand-Kerner finds for the genuinely double eigenvalue -2 are close but never
 * bit-identical (measured ~5.3e-9 apart). Two real, general bugs were found and fixed as a result
 * (NOT specific to this test, both in {@code Eigenspace}/{@code Jordan}, not "a rounding step in
 * P.inverse() or eigenvector normalization" as first suspected when this was documented and
 * deferred):
 * <ol>
 * <li>{@code Eigenspace.eigenval()} grouped consecutive roots into "the same eigenvalue" using
 * {@code Complex.equals()}'s fixed ~1e-11 tolerance, tighter than Durand-Kerner's actual error on
 * this root pair -- silently splitting one defective eigenvalue of multiplicity 2 into two
 * "eigenvalues" of multiplicity 1 each. Fixed to use the same {@code bestNumDecs()}-based rounded
 * comparison {@code arithmeticMultiplicity()} already used, and to store the GROUP AVERAGE of the
 * grouped roots as the representative eigenvalue (closer to the true value than an arbitrary raw
 * member -- ~5e-12 error instead of ~2.6e-9 for this pair).</li>
 * <li>{@code Jordan.factorize()}/{@code factorize2()} still fed the raw, individually-perturbed
 * root from {@code roots()} into {@code eigenvectors(Complex,int)} to build the generalized
 * eigenvector chain, even after {@code arithmeticMultiplicity()} correctly detected multiplicity 2
 * via the fix above -- turning {@code (A-eigenval*I)^2} into a matrix that isn't close enough to
 * singular, collapsing the generalized eigenvector to the trivial all-zero solution (P singular,
 * P.inverse() -&gt; NaN). Fixed with a new helper, {@code Jordan.expandedEigenValues()}, that
 * expands the corrected/averaged {@code eigenvalues()} array back to one row per repetition
 * (preserving the existing {@code i += arithMult} loop convention) instead of using
 * {@code roots()} directly.</li>
 * </ol>
 * With both fixes, the reconstruction residual for this case dropped from {@code NaN} (P singular)
 * to a measured max entry of {@code ~3.4e-7} -- real, but still five orders of magnitude above
 * {@code isNull()}'s ~1e-11 tolerance. This remaining residual is NOT a further bug: building a
 * generalized eigenvector via {@code (A-eigenval*I)^2} then solving is inherently ill-conditioned
 * once {@code eigenval} is only approximately known (as it always will be, coming from
 * Durand-Kerner) -- the ~5e-12 residual eigenvalue error gets amplified roughly 5 orders of
 * magnitude through a near-singular pivot. This is the same family of limitation already
 * documented for Durand-Kerner's robustness on repeated roots, not a new one, and fixing it
 * properly (e.g. building the generalized eigenvector without ever squaring a near-singular
 * matrix) is real numerical linear algebra work, deliberately out of scope here -- decided with
 * the user to relax {@link #checkReconstruction(String, String, double)}'s tolerance for this one
 * case instead of leaving it failing or chasing the amplification further.
 */
public class TestJordanAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void checkReconstruction(String label, String matrixStr) {
		checkReconstruction(label, matrixStr, 0.0);
	}

	/**
	 * @param tolerance if {@code > 0.0}, accept a reconstruction residual with max absolute entry
	 * below this value instead of requiring exact {@code isNull()} -- see the class Javadoc for why
	 * the "defective 3x3, simple eigenvalue processed first" case needs this.
	 */
	private static void checkReconstruction(String label, String matrixStr, double tolerance) {
		Jordan jordan = new Jordan(matrixStr);
		jordan.factorize();
		MatrixComplex P = jordan.P();
		MatrixComplex J = jordan.J();
		MatrixComplex reconstructed = P.times(J).times(P.inverse());
		MatrixComplex original = new MatrixComplex(matrixStr);
		MatrixComplex diff = reconstructed.minus(original);
		boolean ok = tolerance > 0.0 ? maxAbs(diff) < tolerance : diff.isNull();
		if (ok) {
			System.out.println("OK   " + label + " -- P*J*P^-1 reconstructs the original matrix");
			++pass;
		} else {
			System.out.println("FAIL " + label + " -- reconstruction differs:");
			diff.println("     diff");
			++fail;
		}
	}

	private static double maxAbs(MatrixComplex m) {
		double max = 0.0;
		for (int row = 0; row < m.rows(); ++row) {
			for (int col = 0; col < m.cols(); ++col) {
				Complex v = m.getItem(row, col);
				max = Math.max(max, Math.max(Math.abs(v.rep()), Math.abs(v.imp())));
			}
		}
		return max;
	}

	public static void main(String[] args) {
		Complex.setFixedON(6);

		// Defective 3x3, single Jordan block for the repeated eigenvalue (geometric mult 1)
		checkReconstruction("defective 3x3, one double eigenvalue (geomMult=1)", "0,2,2;2,0,-1;-1,-1,0");

		// Diagonalizable, distinct eigenvalues -- no Jordan blocks needed at all
		checkReconstruction("diagonalizable, distinct eigenvalues", "4,1;2,3");

		// Defective 3x3 where the SIMPLE eigenvalue (arithMult=1) is processed FIRST, not last --
		// the exact case that exposed the block() superdiagonal bug (previously gave a silently
		// wrong, non-NaN reconstruction). Needs a relaxed tolerance -- see the class Javadoc for the
		// full diagnosis (measured max residual ~3.4e-7, inherent to squaring a near-singular
		// matrix with an approximately-known eigenvalue, not a bug).
		checkReconstruction("defective 3x3, simple eigenvalue processed first", "0,3,1;2,-1,-1;-2,-1,-1", 1e-6);

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
