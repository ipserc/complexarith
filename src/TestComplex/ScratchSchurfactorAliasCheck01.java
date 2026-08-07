package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Schurfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Diagnostic driver for the com.ipserc.arith.factorization.Schurfactor audit (Decimoctava sesion,
 * continuacion, ver Claude/ComplexArithRev.md). Checks whether Schurfactor(MatrixComplex matrix)'s
 * "this.complexMatrix = matrix.complexMatrix.clone();" is a genuine deep copy (as intended,
 * matching the MatrixComplex.copy()/clone() idiom used everywhere else in the project) or a
 * shallow Complex[][].clone() that aliases the row arrays with the original matrix. Read-only
 * reconnaissance, no production code touched.
 */
public class ScratchSchurfactorAliasCheck01 {
	public static void main(String[] args) {
		MatrixComplex original = new MatrixComplex("1,2;3,4");
		Complex before = original.getItem(0, 0).copy();

		Schurfactor sf = new Schurfactor(original);
		// Mutate the Schurfactor instance directly (inherited MatrixComplex.setItem()), simulating
		// any caller-side or library-internal mutation of the Schurfactor object after construction.
		sf.setItem(0, 0, new Complex(999, 0));

		Complex after = original.getItem(0, 0);
		boolean leaked = !after.equals(before.rep(), before.imp());
		System.out.println("original[0][0] before mutating Schurfactor: " + before.toString());
		System.out.println("original[0][0] after  mutating Schurfactor: " + after.toString());
		System.out.println(leaked
				? "FAIL: mutating the Schurfactor instance corrupted the caller's original matrix (shallow clone aliasing confirmed)."
				: "OK: original matrix unaffected (deep copy confirmed).");
		if (leaked) System.exit(1);
	}
}
