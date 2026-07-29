/******************************************************************************
 *  Compilation:  javac TestComplex.java
 *  Execution:    java TestComplex
 *
 *  Tests for arith.Complex.
 *	
 *  
 *  
 *  
 *  
 *  
 *  
 *
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *
 ******************************************************************************/
package TestComplex;

import com.ipserc.arith.complex.Complex;
//import arith.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;


public class TestTaylorLogExp02 {

	static int boxSize = 65;

	public static void analisys(MatrixComplex matrix) {
	   	MatrixComplex prodMat = matrix.copy();
	   	MatrixComplex yMatrix = matrix.copy();
	   	MatrixComplex sumMat = matrix.copy().opposite();
	   	int m = 0;

	   	Complex.printBoxTextRandom(boxSize, "Analisys for matrix");
		matrix.println("Matrix");
		System.out.println("trace = " + matrix.trace());
		System.out.println("deter = " + matrix.determinant());
		System.out.println("rank  = " + matrix.rank());
		System.out.println();
		while (yMatrix.determinant().mod() > .01) {
			++m;
			yMatrix = yMatrix.divides(Math.E);
		}
    	yMatrix.println("yMatrix");
    	
    	//yMatrix = yMatrix.minusMat(1, 0);

		for (int i = 2; i < 10; ++i) {
			prodMat = prodMat.times(yMatrix);
			sumMat = sumMat.plus(prodMat.divides(i*(i%2 == 0 ? -1 : 1)));
			sumMat.println("sumMat "+i);
		}
	}
	
    public static void main(String[] args) {
    	MatrixComplex aMatrix;
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;
    	MatrixComplex fMatrix;
    	MatrixComplex gMatrix;
    	MatrixComplex hMatrix;
    	MatrixComplex iMatrix;
    	MatrixComplex sMatrix;
		Chronometer chrono = new Chronometer();

    	Complex.setFixedON(3);
    	Complex.setFormatON();
    	Complex.showPrecision();
    	Complex.printFormatStatus();

    	Complex.printBoxTitleRandom(boxSize, "ANALISYS OF MATRIX COMPLEX FROM NATURAL LOG EXP");
    	System.out.println();
    	
    	Complex.printBoxTextRandom(boxSize, "Natural logarithm Matrix functions");
    	/*
    	 * [ 1.600000E+01 , 2.000000E+00 , 3.000000E+00 ]
    	 * [ 2.000000E+00 , 1.600000E+01 , 5.000000E+00 ]
    	 * [ 3.000000E+00 , 5.000000E+00 , 6.000000E+00 ]
    	 */
    	 aMatrix = new MatrixComplex(""
    			 + "1.600000E+01 , 2.000000E+00 , 3.000000E+00;"
    			 + "2.000000E+00 , 1.600000E+01 , 5.000000E+00;"
    			 + "3.000000E+00 , 5.000000E+00 , 6.000000E+00");
    	 analisys(aMatrix);
    	 /* 
    	 * [ 1.600000E+01 , -3.000000E+00 , 3.000000E+00 ]
    	 * [ 3.000000E+00 , 1.600000E+01 , 1.500000E+01 ]
    	 * [ -3.000000E+00 , 5.000000E+00 , 1.700000E+01 ]
    	 */
    	 aMatrix = new MatrixComplex(""
    			 + "1.600000E+01,-3.000000E+00,3.000000E+00;"
    			 + "3.000000E+00,1.600000E+01,1.500000E+01;"
    			 + "-3.000000E+00,5.000000E+00,1.700000E+01");
    	 analisys(aMatrix);
    	 
    	 /* 
    	 * [ 1.600000E+01 , 5.000000E+00 , 4.000000E+00 ]
    	 * [ 5.000000E+00 , 1.800000E+01 , 6.000000E+00 ]
    	 * [ 4.000000E+00 , 6.000000E+00 , 1.200000E+01 ]
    	 */
    	 aMatrix = new MatrixComplex(""
    			 + "1.600000E+01 , 5.000000E+00 , 4.000000E+00;"
    			 + "5.000000E+00 , 1.800000E+01 , 6.000000E+00;"
    			 + "4.000000E+00 , 6.000000E+00 , 1.200000E+01");
    	 analisys(aMatrix);

    	 /* 
    	 * [ 1.800000E+01 , 1.000000E+00 , 2.000000E+00 ]
    	 * [ 1.000000E+00 , 1.800000E+01 , 0.000000E+00 ]
    	 * [ 2.000000E+00 , 0.000000E+00 , 6.000000E+00 ]
    	 */ 
    	 aMatrix = new MatrixComplex(""
    			 + "1.800000E+01 , 1.000000E+00 , 2.000000E+00;"
    			 + "1.000000E+00 , 1.800000E+01 , 0.000000E+00;"
    			 + "2.000000E+00 , 0.000000E+00 , 6.000000E+00");
    	 analisys(aMatrix);

    	 /* 
    	 * [ 1.400000E+01 , 2.000000E+00 , 1.000000E+00 ]
    	 * [ 2.000000E+00 , 1.800000E+01 , 2.000000E+00 ]
    	 * [ 1.000000E+00 , 2.000000E+00 , 1.200000E+01 ]
    	 */
    	 aMatrix = new MatrixComplex(""
    			 + "1.400000E+01,2.000000E+00,1.000000E+00;"
    			 + "2.000000E+00,1.800000E+01,2.000000E+00;"
    			 + "1.000000E+00,2.000000E+00,1.200000E+01");
    	 analisys(aMatrix);

    	 /* 
    	 * [ 1.600000E+01 , -3.000000E+00 , 3.000000E+00 ]
    	 * [ 3.000000E+00 , 1.600000E+01 , 1.500000E+01 ]
    	 * [ -3.000000E+00 , 5.000000E+00 , 7.000000E+00 ]
    	 */
    	 aMatrix = new MatrixComplex(""
    			 + "1.600000E+01,-3.000000E+00,3.000000E+00;"
    			 + "3.000000E+00,1.600000E+01,1.500000E+01;"
    			 + "-3.000000E+00,5.000000E+00,7.000000E+00");
    	 analisys(aMatrix);

    	 /* 
    	 * [ 1.600000E+01 , 1.000000E+00 , 0.000000E+00 ]
    	 * [ 1.000000E+00 , 1.400000E+01 , 0.000000E+00 ]
    	 * [ 0.000000E+00 , 0.000000E+00 , 1.600000E+01 ]
    	 */
    	 aMatrix = new MatrixComplex(""
    			 + "1.600000E+01,1.000000E+00,0.000000E+00;"
    			 + "1.000000E+00,1.400000E+01,0.000000E+00;"
    			 + "0.000000E+00,0.000000E+00,1.600000E+01");
    	 analisys(aMatrix);

    	 /* 
    	 * [ 1.600000E+01 , 8.000000E+00 , 7.000000E+00 ]
    	 * [ 8.000000E+00 , 1.000000E+01 , 4.000000E+00 ]
    	 * [ 7.000000E+00 , 4.000000E+00 , 1.600000E+01 ]
    	 */
    	 aMatrix = new MatrixComplex(""
    			 + "1.600000E+01,8.000000E+00,7.000000E+00;"
    			 + "8.000000E+00,1.000000E+01,4.000000E+00;"
    			 + "7.000000E+00,4.000000E+00,1.600000E+01");
    	 analisys(aMatrix);

    	 /* 
    	 * [ 1.200000E+01 , 2.000000E+00 , 3.000000E+00 ]
    	 * [ 2.000000E+00 , 1.000000E+01 , 2.000000E+00 ]
    	 * [ 3.000000E+00 , 2.000000E+00 , 1.600000E+01 ]
    	 */
    	 aMatrix = new MatrixComplex(""
    			 + "1.200000E+01,2.000000E+00,3.000000E+00;"
    			 + "2.000000E+00,1.000000E+01,2.000000E+00;"
    			 + "3.000000E+00,2.000000E+00,1.600000E+01");
    	 analisys(aMatrix);

    	 /* 
    	 * [ 1.800000E+00 , -3.000000E-01i , 3.000000E-01 ]
    	 * [ 3.000000E-01i , 1.600000E+00 , 1.500000E+00 ]
    	 * [ -3.000000E-01 , 5.000000E-01 , 1.700000E+00 ]
    	 */
    	 aMatrix = new MatrixComplex(""
    			 + "1.800000E+00,-3.000000E-01i,3.000000E-01;"
    			 + "3.000000E-01i,1.600000E+00,1.500000E+00;"
    			 + "-3.000000E-01,5.000000E-01,1.700000E+00");
    	 analisys(aMatrix);

    	 /* 
    	 * [ 2.000000E+00 , 1.000000E+00 , 5.000000E+00 ]
    	 * [ 1.000000E+00 , 9.000000E+00 , 7.000000E+00 ]
    	 * [ 1.000000E+00 , 1.000000E+00 , 8.000000E+00 ]
    	 */
    	 aMatrix = new MatrixComplex(""
    			 + "2.000000E+00,1.000000E+00,5.000000E+00;"
    			 + "1.000000E+00,9.000000E+00,7.000000E+00;"
    			 + "1.000000E+00,1.000000E+00,8.000000E+00");
    	 analisys(aMatrix);

    	 /*
    	  * [ 6.000000E+00 , 6.000000E+00 , 3.000000E+00 ]
    	  * [ 2.000000E+00 , 8.000000E+00 , 9.000000E+00 ]
    	  * [ 2.000000E+00 , 2.000000E+00 , 7.000000E+00 ]
    	  */
    	 aMatrix = new MatrixComplex(""
    			 + "6.000000E+00,6.000000E+00,3.000000E+00;"
    			 + "2.000000E+00,8.000000E+00,9.000000E+00;"
    			 + "2.000000E+00,2.000000E+00,7.000000E+00");
    	 analisys(aMatrix);

    	 

    	 aMatrix = new MatrixComplex(""
    			 + "7.000000E+00,2.000000E+00,9.000000E+00;"
    			 + "9.000000E+00,2.000000E+00,5.000000E+00;"
    			 + "6.000000E+00,4.000000E+00,9.000000E+00");
    	 analisys(aMatrix);
    	 /* 
    	 * 
    	 * 
    	 * 
    	 */
    	
    }
}
