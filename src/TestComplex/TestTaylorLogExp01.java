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

import java.util.List;
import java.util.ArrayList;

public class TestTaylorLogExp01 {
	
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
		int boxSize = 65;
		Chronometer chrono = new Chronometer();

    	Complex.setScientificON(8);;
    	Complex.setFormatON();
    	Complex.exact(true);
    	Complex.showPrecision();
    	Complex.printFormatStatus();

    	Complex.printBoxTitleRandom(boxSize, "MATRIX COMPLEX NATURAL LOG EXP");
    	System.out.println();
   		
    	Complex.printBoxTextRandom(boxSize, "Natural logarithm Matrix functions");
    	aMatrix = new MatrixComplex(""
    			+ "6, 1;"
    			+ "1, 6");
    	aMatrix.println("aMatrix");
    	aMatrix.determinant().println("Det=");   	
    	bMatrix = aMatrix.log(); 
    	bMatrix.println("b=log");
    	bMatrix.exp().println("exp(b)");

    	Complex.printBoxTextRandom(boxSize, "Natural logarithm Matrix functions");
    	aMatrix = new MatrixComplex(""
    			+ "4, 2;"
    			+ "1, 3");
    	aMatrix.println("aMatrix");
    	aMatrix.determinant().println("Det=");   	
    	bMatrix = aMatrix.log(); 
    	bMatrix.println("b=log");
    	bMatrix.exp().println("exp(b)");

    	Complex.printBoxTextRandom(boxSize, "Natural logarithm Matrix functions");
    	/*
    	 * [ 1.600000E+01 , 2.000000E+00 , 3.000000E+00 ]
    	 * [ 2.000000E+00 , 1.600000E+01 , 5.000000E+00 ]
    	 * [ 3.000000E+00 , 5.000000E+00 , 6.000000E+00 ]
    	 * aMatrix = new MatrixComplex("1.600000E+01 , 2.000000E+00 , 3.000000E+00;2.000000E+00 , 1.600000E+01 , 5.000000E+00;3.000000E+00 , 5.000000E+00 , 6.000000E+00");
    	 * 
    	 * [ 1.600000E+01 , -3.000000E+00 , 3.000000E+00 ]
    	 * [ 3.000000E+00 , 1.600000E+01 , 1.500000E+01 ]
    	 * [ -3.000000E+00 , 5.000000E+00 , 1.700000E+01 ]
    	 * aMatrix = new MatrixComplex("1.600000E+01,-3.000000E+00,3.000000E+00;3.000000E+00,1.600000E+01,1.500000E+01;-3.000000E+00,5.000000E+00,1.700000E+01");
    	 * 
    	 * [ 1.600000E+01 , 5.000000E+00 , 4.000000E+00 ]
    	 * [ 5.000000E+00 , 1.800000E+01 , 6.000000E+00 ]
    	 * [ 4.000000E+00 , 6.000000E+00 , 1.200000E+01 ]
    	 * aMatrix = new MatrixComplex("1.600000E+01 , 5.000000E+00 , 4.000000E+00;5.000000E+00 , 1.800000E+01 , 6.000000E+00;4.000000E+00 , 6.000000E+00 , 1.200000E+01");
    	 * 
    	 * [ 1.800000E+01 , 1.000000E+00 , 2.000000E+00 ]
    	 * [ 1.000000E+00 , 1.800000E+01 , 0.000000E+00 ]
    	 * [ 2.000000E+00 , 0.000000E+00 , 6.000000E+00 ]
    	 * aMatrix = new MatrixComplex("1.800000E+01 , 1.000000E+00 , 2.000000E+00;1.000000E+00 , 1.800000E+01 , 0.000000E+00;2.000000E+00 , 0.000000E+00 , 6.000000E+00");
    	 * 
    	 * [ 1.400000E+01 , 2.000000E+00 , 1.000000E+00 ]
    	 * [ 2.000000E+00 , 1.800000E+01 , 2.000000E+00 ]
    	 * [ 1.000000E+00 , 2.000000E+00 , 1.200000E+01 ]
    	 * aMatrix = new MatrixComplex("1.400000E+01,2.000000E+00,1.000000E+00;2.000000E+00,1.800000E+01,2.000000E+00;1.000000E+00,2.000000E+00,1.200000E+01");
    	 * 
    	 * [ 1.600000E+01 , -3.000000E+00 , 3.000000E+00 ]
    	 * [ 3.000000E+00 , 1.600000E+01 , 1.500000E+01 ]
    	 * [ -3.000000E+00 , 5.000000E+00 , 7.000000E+00 ]
    	 * aMatrix = new MatrixComplex("1.600000E+01,-3.000000E+00,3.000000E+00;3.000000E+00,1.600000E+01,1.500000E+01;-3.000000E+00,5.000000E+00,7.000000E+00");
    	 * 
    	 * [ 1.600000E+01 , 1.000000E+00 , 0.000000E+00 ]
    	 * [ 1.000000E+00 , 1.400000E+01 , 0.000000E+00 ]
    	 * [ 0.000000E+00 , 0.000000E+00 , 1.600000E+01 ]
    	 * aMatrix = new MatrixComplex("1.600000E+01,1.000000E+00,0.000000E+00;1.000000E+00,1.400000E+01,0.000000E+00;0.000000E+00,0.000000E+00,1.600000E+01");
    	 * 
    	 * [ 1.600000E+01 , 8.000000E+00 , 7.000000E+00 ]
    	 * [ 8.000000E+00 , 1.000000E+01 , 4.000000E+00 ]
    	 * [ 7.000000E+00 , 4.000000E+00 , 1.600000E+01 ]
    	 * aMatrix = new MatrixComplex("1.600000E+01,8.000000E+00,7.000000E+00;8.000000E+00,1.000000E+01,4.000000E+00;7.000000E+00,4.000000E+00,1.600000E+01");
    	 * 
    	 * [ 1.200000E+01 , 2.000000E+00 , 3.000000E+00 ]
    	 * [ 2.000000E+00 , 1.000000E+01 , 2.000000E+00 ]
    	 * [ 3.000000E+00 , 2.000000E+00 , 1.600000E+01 ]
    	 * aMatrix = new MatrixComplex("1.200000E+01,2.000000E+00,3.000000E+00;2.000000E+00,1.000000E+01,2.000000E+00;3.000000E+00,2.000000E+00,1.600000E+01");
    	 * 
    	 * [ 1.800000E+00 , -3.000000E-01i , 3.000000E-01 ]
    	 * [ 3.000000E-01i , 1.600000E+00 , 1.500000E+00 ]
    	 * [ -3.000000E-01 , 5.000000E-01 , 1.700000E+00 ]
    	 * aMatrix = new MatrixComplex("1.800000E+00,-3.000000E-01i,3.000000E-01;3.000000E-01i,1.600000E+00,1.500000E+00;-3.000000E-01,5.000000E-01,1.700000E+00");
    	 * 
    	 * [ 2.000000E+00 , 1.000000E+00 , 5.000000E+00 ]
    	 * [ 1.000000E+00 , 9.000000E+00 , 7.000000E+00 ]
    	 * [ 1.000000E+00 , 1.000000E+00 , 8.000000E+00 ]
    	 * aMatrix = new MatrixComplex("2.000000E+00,1.000000E+00,5.000000E+00;1.000000E+00,9.000000E+00,7.000000E+00;1.000000E+00,1.000000E+00,8.000000E+00");
    	 * 
    	 * [ 6+2i , -1-9i ]
    	 * [ -2-4i , 8-6i ]
    	 * aMatrix = new MatrixComplex("6+2i,-1-9i;-2-4i,8-6");
    	 * 
    	 * [ 4.0-6.0i , -1.0-7.0i ]
    	 * [ 1.0+7.0i , 5.0+7.0i ]
    	 * aMatrix = new MatrixComplex("4.0-6.0i,-1.0-7.0i;1.0+7.0i,5.0+7.0i");
    	 * 
    	 * [ 7.0-3.0i , 8.0+2.0i ]
    	 * [ 2.0-9.0i , 7.0+3.0i ]
    	 * aMatrix = new MatrixComplex("7.0-3.0i,8.0+2.0i;2.0-9.0i,7.0+3.0i");
    	 * 
    	 * [ 8.0+5.0i , -7.0+9.0i ]
    	 * [ 1.0-1.0i , 4.0-2.0i ]
    	 * aMatrix = new MatrixComplex("8.0+5.0i,-7.0+9.0i;1.0-1.0i,4.0-2.0i");
    	 * 
    	 * [ 6.88573434E+00-7.02511181E+00i , 7.67002407E+00+4.11686862E+00i ]
    	 * [ -3.20626541E+00+3.12445753E+00i , 6.72740391E+00-6.39874010E+00i ]
    	 * aMatrix = new MatrixComplex("6.88573434E+00-7.02511181E+00i,7.67002407E+00+4.11686862E+00i;-3.20626541E+00+3.12445753E+00i,6.72740391E+00-6.39874010E+00i");
    	 * 
    	 * [ 5.34375039E+00-4.47944030E-01i , 1.89518912E+00-1.06609312E+00i ]
    	 * [ 1.72210060E-01+9.58859700E-02i , 5.79278846E+00-1.16982627E+00i ]
    	 * aMatrix = new MatrixComplex("5.34375039E+00-4.47944030E-01i,1.89518912E+00-1.06609312E+00i;1.72210060E-01+9.58859700E-02i,5.79278846E+00-1.16982627E+00i");
    	 * 
    	 * 
    	 * 
    	 * 
    	 * 
    	 * 
    	 * 
    	 */
    	
    	/** /
    	aMatrix = new MatrixComplex(""
    			+ " 18 , -3i , 3;"
    			+ " 3i ,  16 , 15;"
    			+ "-3 ,  5 ,  17");
    	//aMatrix = aMatrix.opposite();
    	aMatrix.println("aMatrix");
    	System.out.println("aMatrix = " + aMatrix.toMatrixComplex());
    	aMatrix.determinant().println("Det=");   	
    	bMatrix = aMatrix.log(); 
    	bMatrix.println("b=log");
    	bMatrix.exp().println("exp(b)");
    	/**/

       	Complex.printBoxTextRandom(boxSize, "Natural logarithm Matrix functions - 1");
        /*
    	 * [ 6.000000E+00 , 6.000000E+00 , 7.000000E+00 ]
    	 * [ 3.000000E+00 , 3.000000E+00 , 8.000000E+00 ]
    	 * [ 3.000000E+00 , 2.000000E+00 , 8.000000E+00 ]
    	 */ 
    	aMatrix = new MatrixComplex("6.000000E+00,6.000000E+00,7.000000E+00;3.000000E+00,3.000000E+00,8.000000E+00;3.000000E+00,2.000000E+00,8.000000E+00");
    	aMatrix.println("aMatrix");
    	System.out.println("aMatrix = " + aMatrix.toMatrixComplex());
    	aMatrix.determinant().println("Det=");   	
    	bMatrix = aMatrix.log(); 
    	bMatrix.println("b=log");
    	bMatrix.exp().println("exp(b)");
    	
       	Complex.printBoxTextRandom(boxSize, "Natural logarithm Matrix functions - 2");
    	/*
    	 * 
    	 * [ 1.600000E+01 , 1.000000E+00 , 0.000000E+00 ]
    	 * [ 1.000000E+00 , 1.400000E+01 , 0.000000E+00 ]
    	 * [ 0.000000E+00 , 0.000000E+00 , 1.600000E+01 ]
    	 */ 
   	 	aMatrix = new MatrixComplex("1.600000E+01,1.000000E+00,0.000000E+00;1.000000E+00,1.400000E+01,0.000000E+00;0.000000E+00,0.000000E+00,1.600000E+01");
    	aMatrix.println("aMatrix");
    	System.out.println("aMatrix = " + aMatrix.toMatrixComplex());
    	aMatrix.determinant().println("Det=");   	
    	bMatrix = aMatrix.log(); 
    	bMatrix.println("b=log");
    	bMatrix.exp().println("exp(b)");
    	
     	Complex.printBoxTextRandom(boxSize, "Natural logarithm Matrix functions - 3");
     	/* 
		 * [ 6+2i , -1-9i ]
		 * [ -2-4i , 8-6i ]
		 */
		aMatrix = new MatrixComplex("6+2i,-1-9i;-2-4i,8-6i");
	   	aMatrix.println("aMatrix");
	   	System.out.println("aMatrix = " + aMatrix.toMatrixComplex());
    	aMatrix.determinant().println("Det=");   	
    	bMatrix = aMatrix.log(); 
    	bMatrix.println("b=log");
    	bMatrix.exp().println("exp(b)");


       	Complex.printBoxTextRandom(boxSize, "Natural logarithm Matrix functions - 4");
  	 	aMatrix = new MatrixComplex(""
			 + "7.000000E+00,2.000000E+00,9.000000E+00;"
			 + "9.000000E+00,2.000000E+00,5.000000E+00;"
			 + "6.000000E+00,4.000000E+00,9.000000E+00");
    	aMatrix.println("aMatrix");
    	System.out.println("aMatrix = " + aMatrix.toMatrixComplex());
    	aMatrix.determinant().println("Det=");   	
    	bMatrix = aMatrix.log(); 
    	bMatrix.println("b=log");
    	bMatrix.exp().println("exp(b)");


    	/** /
		Complex.printBoxTextRandom(boxSize, "Natural logarithm Matrix functions - 5 Random");
    	aMatrix = new MatrixComplex(3,3);
   		aMatrix.initMatrixRandomInteger(9);
    	//aMatrix = aMatrix.hermitian();
    	aMatrix.abs();
    	aMatrix.println("aMatrix");
       	System.out.println("aMatrix = " + aMatrix.toMatrixComplex());
    	aMatrix.determinant().println("Det=");   	
        bMatrix = aMatrix.log(); 
    	bMatrix.println("b=log");
    	bMatrix.exp().println("exp(b)");
    	/**/
    }
}
