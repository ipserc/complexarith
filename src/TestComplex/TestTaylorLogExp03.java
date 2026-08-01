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

public class TestTaylorLogExp03 {
	
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

		Complex.resetFormatStatus();
		Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotON();
    	Complex.digits(10000000);
    	Complex.setScientificON(4);
    	
    	Complex.showPrecision();
    	Complex.printFormatStatus();


    	Complex.printBoxTitleRandom(boxSize, "MATRIX COMPLEX NATURAL LOG EXP");
    	System.out.println();
   		
    	/**/
		Complex.printBoxTextRandom(boxSize, "Natural logarithm Matrix functions - 5 Random");
   	aMatrix = new MatrixComplex(4);
   		aMatrix.initMatrixRandomInt(9);
   		//aMatrix = aMatrix.divides(10);
    	//aMatrix = aMatrix.hermitian();
    	//aMatrix.abs();
   		//aMatrix = new MatrixComplex("2.0000E+00,5.0000E+00,-6.0000E+00,9.0000E+00;-9.0000E+00,4.0000E+00,5.0000E+00,-9.0000E+00;6.0000E+00,5.0000E+00,6.0000E+00,-3.0000E+00;-8.0000E+00,6.0000E+00,-8.0000E+00,7.0000E+00");
   		aMatrix = new MatrixComplex("3.0000E+00,5.0000E+00,6.0000E+00,2.0000E+00;-8.0000E+00,9.0000E+00,-3.0000E+00,-2.0000E+00;-1.0000E+00,3.0000E+00,6.0000E+00,-1.0000E+00;-1.0000E+00,7.0000E+00,2.0000E+00,5.0000E+00");

   		//aMatrix = new MatrixComplex("6.0000E+00,9.0000E+00,-3.0000E+00,-4.0000E+00;-8.0000E+00,8.0000E+00,-9.0000E+00,1.0000E+00;5.0000E+00,6.0000E+00,4.0000E+00,-6.0000E+00;-6.0000E+00,-5.0000E+00,6.0000E+00,5.0000E+00");
   		aMatrix = aMatrix.times(1);
		Complex.printBoxTextRandom(boxSize, "log(p·n) = log(p)+log(n ). Si p es una matriz y n un número log(p·n) = log(p)+log(n ), pero +log(n ) es en realidad +I·log(n ) donde I es la matirz identidad, entonces es sumar log(n ) a la diagonal principal. ");
		aMatrix.println("aMatrix");

   		double factor = 1.0; //aMatrix.norm();
 
   		aMatrix = aMatrix.divides(factor);   			

   		aMatrix.println("aMatrix normalized");
       	System.out.println("aMatrix = " + aMatrix.toMatrixComplex());
    	aMatrix.determinant().println("A Det=");   	
        bMatrix = aMatrix.log(); 

        bMatrix = bMatrix.plusMat(Math.log(factor));

        bMatrix.println("b=log");
        MatrixComplex.debugOFF();
    	bMatrix.exp().println("exp(b)");
    	/**/
    }
}
