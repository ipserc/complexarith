package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestTriangLo {

	public static void showResults(MatrixComplex aMatrix) {
		aMatrix.println("aMatrix Original");
		aMatrix.triangleLo().println("aMatrix Triang Lo NEW");
		aMatrix.println("aMatrix Verificar");
		System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		System.out.println("---------------------------------------");

	}
	
	public static void main(String[] args) {
		MatrixComplex aMatrix;
		Complex.setFormatON();
		/*
		aMatrix = new MatrixComplex("2,2,1;0,0,1;1,0,1"); showResults(aMatrix);
		aMatrix = new MatrixComplex("2,2,1;1,-2,1;1,1,-1"); showResults(aMatrix);
		aMatrix = new MatrixComplex(4); aMatrix.initMatrixRandomInteger(4); showResults(aMatrix);
		 */
    	aMatrix = new MatrixComplex("1,0,0;2,3,0;4,5,6"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("2,3,0;4,5,6;1,0,0"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("4,5,6;1,0,0;2,3,0"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("2,3,0;1,0,0;4,5,6"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("1,0,0;4,5,6;2,3,0"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("4,5,6;2,3,0;1,0,0"); showResults(aMatrix);
    	//aMatrix = new MatrixComplex("1,-1,-1,-1,-1;1,-1,1,-1,1;-1,-1,-1,-1,-1;-1,-1,1,-1,1"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("1,-1,-1,-1;1,-1,1,-1;-1,-1,-1,-1;-1,-1,1,-1"); showResults(aMatrix);
	}
}
