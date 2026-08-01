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
 * KNOWN LIMITATION discovered 2026-08-01 (Novena sesion, while verifying the Durand-Kerner
 * Cauchy-bound seeding fix in Polynom.solveWeierstrass(), commit after 759a503/f360be2): this
 * class was PASSING 3/3 only by luck, not by correctness. Before that seeding fix,
 * solveWeierstrass() used Math.random() for its initial guesses, so which case failed (if any)
 * varied between runs -- this class had been observed passing 3/3 on some runs and failing on
 * others, for the SAME unmodified code, well before today. Making the seeding deterministic
 * (Cauchy's bound, no more Math.random()) exposed this: the "defective 3x3, simple eigenvalue
 * processed first" case now consistently FAILS, with a measured reconstruction residual of
 * exactly 2^-24 (~5.96e-8) in the affected cells -- above isNull()'s ~1e-11 tolerance, but a
 * suspiciously "round" binary value that smells like a specific rounding step somewhere in the
 * Jordan.java pipeline (P.inverse(), eigenvector normalization, or similar), not generic
 * floating-point accumulation noise. NOT investigated further yet -- deliberately deferred at the
 * user's explicit request (documented in Claude/ComplexArithRev.md, "Novena sesion", pausada
 * 2026-08-01) for a future pass dedicated to Jordan.java. Do not assume a 3/3 pass (or an
 * unexplained 2/3) on some future run means anything has changed until this is root-caused.
 */
public class TestJordanAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void checkReconstruction(String label, String matrixStr) {
		Jordan jordan = new Jordan(matrixStr);
		jordan.factorize();
		MatrixComplex P = jordan.P();
		MatrixComplex J = jordan.J();
		MatrixComplex reconstructed = P.times(J).times(P.inverse());
		MatrixComplex original = new MatrixComplex(matrixStr);
		MatrixComplex diff = reconstructed.minus(original);
		boolean ok = diff.isNull();
		if (ok) {
			System.out.println("OK   " + label + " -- P*J*P^-1 reconstructs the original matrix");
			++pass;
		} else {
			System.out.println("FAIL " + label + " -- reconstruction differs:");
			diff.println("     diff");
			++fail;
		}
	}

	public static void main(String[] args) {
		Complex.setFixedON(6);

		// Defective 3x3, single Jordan block for the repeated eigenvalue (geometric mult 1)
		checkReconstruction("defective 3x3, one double eigenvalue (geomMult=1)", "0,2,2;2,0,-1;-1,-1,0");

		// Diagonalizable, distinct eigenvalues -- no Jordan blocks needed at all
		checkReconstruction("diagonalizable, distinct eigenvalues", "4,1;2,3");

		// Defective 3x3 where the SIMPLE eigenvalue (arithMult=1) is processed FIRST, not last --
		// the exact case that exposed the block() superdiagonal bug (previously gave a silently
		// wrong, non-NaN reconstruction).
		checkReconstruction("defective 3x3, simple eigenvalue processed first", "0,3,1;2,-1,-1;-2,-1,-1");

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
