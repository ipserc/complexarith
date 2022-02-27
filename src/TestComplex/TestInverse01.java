package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestInverse01 {
	public static void showResults(MatrixComplex aMatrix) {
		MatrixComplex inv;
		int boxSize = 65;

		System.out.println(Complex.boxTextRandom(boxSize, "Matrix Inverse"));

		aMatrix.println("aMatrix");
		inv = aMatrix.inverse();
		inv.println("aMatrix inverse");
		if (!inv.isInfinite() && !inv.isNaN()) {
			System.out.println(Complex.boxTextRandom(boxSize, "Check --- aMatrix · aMatrix.inverse"));
			aMatrix.times(inv).println();
		}
		System.out.println(Complex.boxTextRandom(boxSize, "aMatrix Determinat comparision"));
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		if (aMatrix.complexMatrix.length < 10)
			System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "MATRIX INVERSE TEST"));

    	Complex.setScientificON(3);
    	Complex.setFormatON();

    	aMatrix = new MatrixComplex("2,2,1;0,0,1;1,0,1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("2,2,1;1,-2,1;1,1,-1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("2,2,1;-4,-4,-2;1,1,-1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex(40); aMatrix.initMatrixRandomInteger(9);
		showResults(aMatrix);

		aMatrix = new MatrixComplex(6); aMatrix.initMatrixRandomRec(17);
		showResults(aMatrix);
	}

}
