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

public class TestTaylorSeries02 {
	
	static int boxSize = 65;

	private static void trigonometrix(MatrixComplex aMatrix) {
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;

    	Complex.printBoxTitleRandom(boxSize, "Trigonometrics Matrix functions");
    	aMatrix.println("aMatrix");
    	bMatrix = aMatrix.sinTaylor(); bMatrix.println("b=Sin Taylor");
    	aMatrix.sinEuler().println("Sin Euler");
    	cMatrix = aMatrix.cosTaylor(); cMatrix.println("c=Cos Taylor");
    	aMatrix.cosEuler().println("Cos Euler");
    	//eMatrix = aMatrix.times(new Complex(0,1)).exp();
    	eMatrix = aMatrix.euler();
    	eMatrix.println("Euler e**(i·aMatrix) = cos(a)+i·(sin(a))");
    	dMatrix = bMatrix.power(2).plus(cMatrix.power(2));
    	dMatrix.println("b²+c²");
    	dMatrix.determinant().println("Det=");
    	aMatrix.tan().println("Tan");
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

		Complex.resetFormatStatus();
		Complex.restorePrecisionFactorySettings();
    	//Complex.setScientificON(8);;
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	Complex.digits(10000000);
    	
    	Complex.showPrecision();
    	Complex.printFormatStatus();

    	Complex.printBoxTitleRandom(boxSize, "MATRIX COMPLEX TAYLOR & McLAURIN SERIES");
    	System.out.println();

    	aMatrix = new MatrixComplex("32.0");
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10)); 

    	aMatrix = new MatrixComplex("32.0, 0.0; 0.0, 32.0");
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10)); 

       	aMatrix = new MatrixComplex("32.0,25.0,30.0;13.0,20.0,-21.0;-2.0,-1.0,31.0");
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10)); 

    	aMatrix = new MatrixComplex("132.0,25.0,30.0;13.0,120.0,-21.0;-2.0,-1.0,131.0");
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10));
    }
}
