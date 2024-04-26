package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestTriang01 {
	
	private static Complex prodDiag(MatrixComplex aMatrix) {
		int rowLen = aMatrix.complexMatrix.length;
		Complex cNum = new Complex("1");

		for (int row = 0; row < rowLen; ++row)
			cNum = cNum.times(aMatrix.complexMatrix[row][row]);
		return cNum;
	}

	private static void showResults(MatrixComplex aMatrix) {
		int boxSize = 65;
		MatrixComplex triLo, triUp, diaUp, diaLo;

		System.out.println(Complex.boxTitleRandom(boxSize, "TRIANGULARIZATION TEST"));
 		aMatrix.println("aMatrix");
		System.out.println(Complex.boxTextRandom(boxSize, "Matrix Triang Lo"));
		triLo = aMatrix.triangleLo();
		triLo.println("aMatrix Triang Lo");
		System.out.println(Complex.boxTextRandom(boxSize, "Matrix Triang Up"));
		triUp = aMatrix.triangleUp();
		triUp.println("aMatrix Triang Up");
		System.out.println(Complex.boxTextRandom(boxSize, "Matrix Diagonal Lo"));
		diaLo = aMatrix.diagonalLo();
		diaLo.println("aMatrix Diagonal Lo");
		System.out.println(Complex.boxTextRandom(boxSize, "Matrix Diagonal Up"));
		diaUp = aMatrix.diagonalUp();
		diaUp.println("aMatrix Diagonal Up");
		if (aMatrix.isSquare()) {
			System.out.println(Complex.boxTextRandom(boxSize, "Some othe Matrix Operations"));
	  		System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
			System.out.println("aMatrix DetChi:" + aMatrix.determinant());
			System.out.println("triLo.determinant():" + triLo.determinant());
			System.out.println("triUp.determinant():" + triUp.determinant());
			System.out.println("diaLo.determinant():" + diaLo.determinant());
			System.out.println("diaUp.determinant():" + diaUp.determinant());
			System.out.println("aMatrix.trace():" + aMatrix.trace());
			System.out.println("triLo.trace()  :" + triLo.trace());
			System.out.println("triUp.trace()  :" + triUp.trace());
			System.out.println("diaLo.trace()  :" + diaLo.trace());
			System.out.println("diaUp.trace()  :" + diaUp.trace());
		}
	}

	public static void main(String[] args) {
		MatrixComplex aMatrix;

       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	aMatrix = new MatrixComplex("1,2,1;2,1,1;1,1,2"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("2,2,1;0,0,1;1,0,1"); showResults(aMatrix);
		aMatrix = new MatrixComplex("3,-1,1,2;1,2,1,-4;1,0,1,0;2,2,1,-3"); showResults(aMatrix);

    	/* */
    	aMatrix = new MatrixComplex("2,2,1;0,0,1;1,0,1"); showResults(aMatrix);
		aMatrix = new MatrixComplex("1,0,1;0,0,1;2,2,1"); showResults(aMatrix);
		aMatrix = new MatrixComplex("0,0,1;2,2,1;1,0,1"); showResults(aMatrix);
		aMatrix = new MatrixComplex("0,0,1;1,0,1;2,2,1"); showResults(aMatrix);
		aMatrix = new MatrixComplex("3,-1,1,2;1,2,1,-4;1,0,1,0;2,2,1,-3"); showResults(aMatrix);
		aMatrix = new MatrixComplex("-8,1,7,-14;-2,-3,5,7;1,-5,4,-3;4,7,3,1"); showResults(aMatrix);
		aMatrix = new MatrixComplex(10,10);aMatrix.initMatrixRandomInteger(10); showResults(aMatrix);
		aMatrix = new MatrixComplex(10,10);aMatrix.initMatrixRandomRec(10); showResults(aMatrix);
		aMatrix = new MatrixComplex("2,3,-2,1;1,3,2,-1;2,-2,0,1;-1,1,1,0"); showResults(aMatrix);
		/* * /
    	aMatrix = new MatrixComplex("0,0,6;0,4,5;1,2,3"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("0,4,5;0,0,6;1,2,3"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("0,0,6;1,2,3;0,4,5"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("0,4,5;1,2,3;0,0,6"); showResults(aMatrix);
        	
    	aMatrix = new MatrixComplex("1,0,0;2,3,0;4,5,6"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("2,3,0;4,5,6;1,0,0"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("4,5,6;1,0,0;2,3,0"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("1,0,0;4,5,6;2,3,0"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("2,3,0;1,0,0;4,5,6"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("4,5,6;2,3,0;1,0,0"); showResults(aMatrix);
    	/ * */
    	/* * /
		aMatrix = new MatrixComplex("1.0,-1.0,-1.0,-1.0,-1.0;1.0,-1.0,1.0,-1.0,1.0;-1.0,-1.0,-1.0,-1.0,-1.0;-1.0,-1.0,1.0,-1.0,1.0"); showResults(aMatrix);

		aMatrix = new MatrixComplex(" 1.0,-1.0,-1.0;"
				+ 					"-1.0,-1.0, 1.0;"
				+ 					"-1.0, 1.0,-1.0;"
				+ 					" 1.0,-1.0,-1.0;"
				+ 					"-1.0,-1.0,-1.0;"
				+ 					"-1.0,-1.0, 1.0;"
				+ 					" 1.0,-1.0, 1.0"); 
		aMatrix = aMatrix.transpose();
		showResults(aMatrix);
		aMatrix = aMatrix.triangle();
		aMatrix.println();
		aMatrix = aMatrix.transpose();
		aMatrix.println();
		/ * */

    	aMatrix = new MatrixComplex( "" 
				+ "-1,-1, 0, 0;"
				+ " 0, 2, 2, 0;"
				+ " 2, 0,-1, 0"); 
    	showResults(aMatrix);

    	aMatrix = new MatrixComplex( "" 
				+ " 1, 2, 3, 0;"
				+ " 2, 1, 2, 0;"
				+ " 3, 3, 5, 0");
    	showResults(aMatrix);
	}
}
