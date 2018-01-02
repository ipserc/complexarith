package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestTriang {

	private static void showResults(MatrixComplex aMatrix) {
       	Complex.setFormatON();
    	Complex.setFixedON(3);
		aMatrix.println("aMatrix");
		aMatrix.triangleLo().println("aMatrix Triang Lo");
		aMatrix.triangleUp().println("aMatrix Triang Up");
		aMatrix.triangleLo().println("aMatrix Triang lo");
       	Complex.setFormatOFF();
		System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		System.out.println("---------------------------------------");
	}

	public static void main(String[] args) {
		MatrixComplex aMatrix;

		aMatrix = new MatrixComplex("2,2,1;0,0,1;1,0,1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("1,0,1;0,0,1;2,2,1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("0,0,1;2,2,1;1,0,1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("0,0,1;1,0,1;2,2,1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("3,-1,1,2;1,2,1,-4;1,0,1,0;2,2,1,-3");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("-8,1,7,-14;-2,-3,5,7;1,-5,4,-3;4,7,3,1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex(10,10);aMatrix.initMatrixRandomInteger(10);
		showResults(aMatrix);

		aMatrix = new MatrixComplex(10,10);aMatrix.initMatrixRandomRec(10);
		showResults(aMatrix);
	}
}
