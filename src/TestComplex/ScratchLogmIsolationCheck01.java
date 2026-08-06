package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ScratchLogmIsolationCheck01 {
	public static void main(String[] args) {
		Complex.digits(30L);
		MatrixComplex.debugOFF();

		// Known-good matrix from TestLogmAudit01.java ("diagonalizable 3x3"): logm() handles
		// arbitrary-norm input itself via inverse scaling-and-squaring (sqrtTriangular() loop)
		// before applying the Mercator series, so it doesn't need to start near-identity.
		MatrixComplex nearIdentitySmall = new MatrixComplex("2,0,0;0,3,4;0,4,9");

		MatrixComplex result = nearIdentitySmall.logm();
		result.println("logm result (isolated, no prior benchmarks in this process)");
	}
}
