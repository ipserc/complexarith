package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Diagnostic: does m.power(2) succeed or fail on the near-identity matrix WITHOUT any prior
 * calls to logTaylor()/logMercator() first? Used to check whether those methods mutate their
 * input in place (aliasing bug) -- power(2) succeeded when called after them in
 * ScratchLogmIsolationCheck02.java, but failed when tested standalone via Eigenspace directly
 * (ScratchSchurfactorDiag01.java).
 */
public class ScratchSchurfactorDiag03 {
	public static void main(String[] args) {
		Complex.digits(30L);

		MatrixComplex nearIdentity = new MatrixComplex(3, 3);
		nearIdentity.setItem(0, 0, new Complex(1.1, 0.02));
		nearIdentity.setItem(0, 1, new Complex(0.05, -0.01));
		nearIdentity.setItem(0, 2, new Complex(-0.02, 0.01));
		nearIdentity.setItem(1, 0, new Complex(0.01, 0.0));
		nearIdentity.setItem(1, 1, new Complex(0.95, 0.03));
		nearIdentity.setItem(1, 2, new Complex(0.02, -0.02));
		nearIdentity.setItem(2, 0, new Complex(-0.01, 0.02));
		nearIdentity.setItem(2, 1, new Complex(0.03, 0.0));
		nearIdentity.setItem(2, 2, new Complex(1.05, -0.01));

		System.out.println("Before power(2), nearIdentity:");
		nearIdentity.println("nearIdentity");

		MatrixComplex result = nearIdentity.power(2);
		System.out.println("power(2) SUCCEEDED:");
		result.println("result");

		System.out.println("After power(2), nearIdentity (should be unchanged):");
		nearIdentity.println("nearIdentity");
	}
}
