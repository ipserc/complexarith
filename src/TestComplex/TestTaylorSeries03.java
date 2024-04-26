/******************************************************************************
 *  Compilation:  javac TestComplex.java
 *  Execution:    java TestComplex
 *
 *  Tests for arith.Complex.
 *	
 *  Este programa chequea la validez de las diferentes implementaciones de las 
 *  funciones trigonométricas y trigonométricas hiperbólicas entre  matrices 
 *  para valores enteros, reales y complejos.
 *  
 *  El método de chequeo consiste comprobar las siguientes propiedades:
 *  1. Fórmula de euler exp(i*A) = cos(A)+i*sin(A)
 *  2. cos^2(A)+sin^2(A) = I
 *  3. cosh^2(A)-sinh^2(A) = I
 *  Dónde I es la matriz Identidad o matriz Unitaria
 *  
 *  This program checks the validity of the different implementations of the 
 *  trigonometric and hyperbolic trigonometric functions between matrices 
 *  for integer, real and complex values.
 *  
 *  The checking method consists of checking the following properties:
 *  1. Euler formula exp(i*A) = cos(A)+i*sin(A)
 *  2. cos^2(A)+sin^2(A) = I
 *  3. cosh^2(A)-sinh^2(A) = I
 *  Where I is the Identity matrix or Unitary matrix
 *  
 ******************************************************************************/
package TestComplex;

import com.ipserc.arith.complex.Complex;
//import arith.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;

import java.util.List;
import java.util.ArrayList;

public class TestTaylorSeries03 {
	
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
    	eMatrix.println("Euler e**(iÂ·aMatrix) = cos(a)+iÂ·(sin(a))");
    	dMatrix = bMatrix.power(2).plus(cMatrix.power(2));
    	dMatrix.println("bÂ²+cÂ²");
    	dMatrix.determinant().println("Det=");
    	aMatrix.tan().println("Tan");
    	
    	Complex.printBoxTitleRandom(boxSize, "Hyperbolic Trigonometrics Matrix functions");
    	aMatrix.println("aMatrix");
    	bMatrix = aMatrix.sinhTaylor(); bMatrix.println("b=Sin Hyp Taylor");
    	bMatrix = aMatrix.sinhEuler(); bMatrix.println("b=Sin Hyp Euler");
    	cMatrix = aMatrix.coshTaylor(); cMatrix.println("c=Cos Hyp Taylor");
    	cMatrix = aMatrix.coshEuler(); cMatrix.println("c=Cos Hyp Euler");
    	dMatrix = aMatrix.tanhTaylor(); dMatrix.println("d=Tan Hyp Taylor");
    	dMatrix = aMatrix.tanhEuler(); dMatrix.println("d=Tan Hyp Euler");
    	eMatrix = cMatrix.power(2).minus(bMatrix.power(2));
    	eMatrix.println("c²-b²:");


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
    	Complex.setFixedON(4);
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	Complex.digits(10000000);
    	
    	Complex.showPrecision();
    	Complex.printFormatStatus();

    	Complex.printBoxTitleRandom(boxSize, "COMPLEX MATRIX TAYLOR'S TRIGONOMETRICS TEST");
    	MatrixComplex.version();
    	System.out.println();

    	aMatrix = new MatrixComplex("32.0");
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10)); 

    	aMatrix = new MatrixComplex("1.0, 0.0; 0.0, 1.0");
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10)); 

    	aMatrix = new MatrixComplex("1.0, 0.0; 0.0, 1.0").times(Complex.DOSPI);
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10)); 

    	aMatrix = new MatrixComplex("1.0, 1.0; 1.0, 1.0").times(Complex.DOSPI);
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10)); 

       	aMatrix = new MatrixComplex("32.0, 0.0; 0.0, 32.0");
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10)); 

       	aMatrix = new MatrixComplex("32.0,25.0,30.0;13.0,20.0,-21.0;-2.0,-1.0,31.0");
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(150)); 

    	aMatrix = new MatrixComplex("132.0,25.0,30.0;13.0,120.0,-21.0;-2.0,-1.0,131.0");
       	trigonometrix(aMatrix.divides(10)); 
       	trigonometrix(aMatrix.divides(150));
    }
}
