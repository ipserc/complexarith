package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestTriang02 {
	
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
		MatrixComplex aMatrix;

       	Complex.setFormatON();
    	Complex.setFixedON(3);


    	aMatrix = new MatrixComplex( "" 
				+ " 1 ,-1 ,-1 , 1 ,0;"
				+ " 1 , 1 ,-1 ,-1 ,0;"
				+ "-1 ,-1 , 1 , 1 ,0;"
				+ " 1 ,-1 ,-1 ,-1 ,0");
    	
    	aMatrix = new MatrixComplex( "" 
			+ "-3.000,-1.000,-2.000,9.000,7.000,-4.000,0.000;"
			+ "204.000,68.000,-204.000,-272.000,-140.000,272.000,0.000;"
			+ "0.000,0.000,-1.000,1.000,1.000,0.000,0.000;"
			+ "-3.000,-1.000,3.000,4.000,2.000,-4.000,0.000;"
			+ "-90.000,-30.000,90.000,120.000,62.000,"
			+ "-120.000,0.000;0.000,0.000,0.000,0.000,0.000,0.000,0.000");
    	
    	/* * /
    	aMatrix = new MatrixComplex(""
    			+ "-5.000,-5.000,1.000,-3.000,-5.000,-4.000,0.000;"
    			+ "30.000,30.000,-6.000,18.000,30.000,24.000,0.000;"
    			+ "-50.000,-50.000,14.000,-42.000,-68.000,-40.000,0.000;"
    			+ "-20.000,-20.000,4.000,-12.000,-20.000,-16.000,0.000;"
    			+ "-50.000,-50.000,10.000,-30.000,-52.000,-40.000,0.000;"
    			+ "20.000,20.000,-4.000,12.000,20.000,16.000,0.000");
    	// aMatrix = new MatrixComplex("-5.000,-5.000,1.000,-3.000,-5.000,-4.000,0.000;0.000,0.000,4.000,-12.000,-18.000,0.000,0.000;0.000,0.000,0.000,0.000,-2.000,0.000,0.000;0.000,0.000,0.000,0.000,0.000,0.000,0.000;0.000,0.000,0.000,0.000,0.000,0.000,0.000;0.000,0.000,0.000,0.000,0.000,0.000,0.000");
    	/* */

    	
    	/* * /
    	aMatrix = new MatrixComplex(""
    			+ "-5.000,4.000,-3.000,-2.000,1.000,2.000,0.000;"
    			+ "0.000,0.000,0.000,0.000,0.000,0.000,0.000;"
    			+ "-40.000,32.000,-24.000,-16.000,8.000,16.000,0.000;"
    			+ "-55.000,44.000,-33.000,-22.000,11.000,22.000,0.000;"
    			+ "-40.000,32.000,-24.000,-16.000,7.000,16.000,0.000;"
    			+ "-20.000,16.000,-12.000,-8.000,4.000,8.000,0.000");
    	/* */

    	showResults(aMatrix);

	}
}
