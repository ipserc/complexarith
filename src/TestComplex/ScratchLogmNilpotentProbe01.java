package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ScratchLogmNilpotentProbe01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		try {
			// Singular, defective matrix: single Jordan block of eigenvalue 0, size 2.
			// A nontrivial nilpotent Jordan block has NO square root at all (any method) --
			// log() of a matrix with an exact zero eigenvalue is mathematically undefined
			// regardless (e^L can never have eigenvalue 0). Checking what logm() actually
			// does today: throws cleanly, or silently returns NaN/garbage as "the answer"?
			MatrixComplex A = new MatrixComplex("0,1;0,0");
			System.out.println("det(A)=" + A.determinant());
			MatrixComplex result = A.logm();
			System.out.println("logm() did NOT throw. Result:");
			result.println("logm(A)");
		} catch (Throwable t) {
			System.out.println("logm() threw: " + t.getClass().getSimpleName() + ": " + t.getMessage());
		}
	}
}
