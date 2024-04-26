package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestTriang3 {
	
	private static Complex prodDiag(MatrixComplex aMatrix) {
		int rowLen = aMatrix.complexMatrix.length;
		Complex cNum = new Complex("1");

		for (int row = 0; row < rowLen; ++row)
			cNum = cNum.times(aMatrix.complexMatrix[row][row]);
		return cNum;
	}

	private static void showResults(MatrixComplex aMatrix) {
		int boxSize = 65;
		MatrixComplex triLo, triUp, triPerf, diaUp, diaLo;

		System.out.println(Complex.boxTitleRandom(boxSize, "TRIANGULARIZATION TEST"));
 		aMatrix.println("aMatrix");
		System.out.println(Complex.boxTextRandom(boxSize, "Matrix Triang Lo"));
		triLo = aMatrix.triangleLo();
		triLo.println("aMatrix Triang Lo");
		System.out.println(Complex.boxTextRandom(boxSize, "Matrix Triang Up"));
		triUp = aMatrix.triangleUp();
		triUp.println("aMatrix Triang Up");
		System.out.println(Complex.boxTextRandom(boxSize, "Matrix Triang Up perfect"));
		triPerf = aMatrix.triangleUpPerfect();
		triPerf.println("aMatrix Triang Up Perfect");
		/*
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
		*/
	}

	public static void main(String[] args) {
		MatrixComplex aMatrix, inTerms, sol1, sol2, rref;
		int rows, cols;

       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	rows = 3;
    	cols = rows+1;
     	aMatrix = new MatrixComplex(rows,cols);
     	aMatrix.initMatrixRandomInteger(2);
     	
     	inTerms  = new MatrixComplex(rows, 1);
     	inTerms.initMatrixRandomInteger(3);
    	
     	aMatrix.augment(inTerms);
     	
     	/* * /
     	aMatrix = new MatrixComplex(""
     			+ "6.000,-3.000,-6.000,-6.000,5.000,-3.000,6.000,-6.000;"
     			+ "5.000,4.000,4.000,-6.000,-4.000,-6.000,3.000,-4.000;"
     			+ "3.000,-3.000,-1.000,6.000,6.000,-3.000,-1.000,-5.000;"
     			+ "5.000,-2.000,4.000,-3.000,6.000,1.000,3.000,4.000;"
     			+ "-1.000,6.000,-3.000,3.000,4.000,5.000,6.000,5.000;"
     			+ "-4.000,4.000,1.000,6.000,-3.000,-4.000,5.000,-2.000;"
     			+ "2.000,-1.000,1.000,3.000,4.000,-1.000,-6.000,-4.000"
     			+ "");
     	/* */
     	
     	aMatrix = new MatrixComplex(""
     			+ "-2.000,-2.000,1.000,1.000;"
     			+ "-2.000,-1.000,1.000,-1.000;"
     			+ "-2.000,2.000,-1.000,-1.000");
     	
     	System.out.println(aMatrix.toMatrixComplex());
     	System.out.println(aMatrix.toOctave());
     	aMatrix.println("aMatrix");
     	sol1 = aMatrix.solve();
     	rref = aMatrix.rowReduce();
     	rref.println(" row echelon form");
     	sol2 = rref.getCol(cols-1);

     	sol1.            println("solve    :");
     	sol2.transpose().println("rowReduce:");

     	aMatrix.unkMatrix().times(sol1.transpose()).println("check 1");     	
     	aMatrix.unkMatrix().times(sol2).            println("check 2");
    	//showResults(aMatrix);

	}
}
