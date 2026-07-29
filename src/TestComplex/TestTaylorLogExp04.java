/******************************************************************************
 *  Compilation:  javac TestComplex.java
 *  Execution:    java TestComplex
 *   clear;runJava.sh eclipse-workspace/complexarith/bin/TestComplex/TestTaylorLogExp04.class
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


public class TestTaylorLogExp04 {

	static int boxSize = 65;

	public static void analisys(MatrixComplex matrix) {
	   	MatrixComplex prodMat = matrix.copy();
	   	MatrixComplex yMatrix = matrix.copy();
	   	MatrixComplex sumMat = matrix.copy().opposite();
	   	int m = 0;

	   	Complex.printBoxTextRandom(boxSize, "Analisys for matrix");
		matrix.println("Matrix");
		System.out.println("trace       = " + matrix.trace());
		System.out.println("cotrace     = " + matrix.cotrace());
		System.out.println("determinant = " + matrix.determinant());
		System.out.println("rank        = " + matrix.rank());
		System.out.println();
		/*
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
		*/
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
     	 

    	aMatrix = new MatrixComplex(""
    			+ " .6,  .0;"
    			+ "-.1,  .7");
   	 	analisys(aMatrix);
   	 	
    	aMatrix = new MatrixComplex(""
    			+ "-1.6,  -.3;"
    			+ " 1.2,  .7");
   	 	analisys(aMatrix);
   	 	
   	 	/*
   	 	 * [ 7.0 , -7.0 ]
   	 	 * [ -2.0 , 1.0 ]
   	 	 */
   	 	System.out.println("NUMERIC OVERFLOOD");
   		aMatrix = new MatrixComplex("7.0,-7.0;-2.0,1.0");
   	 	analisys(aMatrix);
   	 	
   	 	/*
   	 	 * [ 7.0 , -7.0 ]
   	 	 * [ 2.0 , 1.0 ]
   	 	 */
   	 	System.out.println("LOGARYTHM OK");
   		aMatrix = new MatrixComplex("7.0,-7.0;2.0,1.0");
   	 	analisys(aMatrix);
   	 	
   	 	/*
   	 	 * [ 7.0 , 7.0 ]
   	 	 * [ -2.0 , 1.0 ]
   	 	 */
   	 	System.out.println("LOGARYTHM OK");
   		aMatrix = new MatrixComplex("7.0,7.0;-2.0,1.0");
   	 	analisys(aMatrix);
   	 	
   	 	/*
   	 	 * [ 1.0 , -8.0 ]
   	 	 * [ 4.0 , -7.0 ]
   	 	 */
   	 	System.out.println("NUMERIC OVERFLOOD");
   		aMatrix = new MatrixComplex("1.0,-8.0;4.0,-7.0");
   	 	analisys(aMatrix);

   	 	/*
   	 	 * [ -1.0 , 8.0 ]
   	 	 * [ -4.0 , 7.0 ]
   	 	 */
   	 	System.out.println("LOGARYTHM OK");
   		aMatrix = new MatrixComplex(""
   				+ " -1.0, 8.0;"
   				+ " -4.0, 7.0");

    	
    }
}
