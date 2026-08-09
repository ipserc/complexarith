package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ScratchEigenInconsistentBug02 {
	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedON(4);

		MatrixComplex aMatrix = new MatrixComplex(
				"-1,3,6,-2,3;" +
				"2,-1,-1,2,-1;" +
				"-5,-1,-2,-5,-1;" +
				"3,0,-1,4,-2;" +
				"-1,-1,-2,-5,-2");

		TestEigenV21.doEigenCalculations(aMatrix);
	}
}
