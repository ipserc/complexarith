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

public class TestTaylorSeries04 {
	
	static int boxSize = 65;
	
	private static void trigonize(MatrixComplex aMatrix) {
		MatrixComplex Sin = new MatrixComplex(aMatrix.rows(), aMatrix.cols());
		MatrixComplex Cos = new MatrixComplex(aMatrix.rows(), aMatrix.cols());
		MatrixComplex Tan = new MatrixComplex(aMatrix.rows(), aMatrix.cols());
		MatrixComplex Euler = new MatrixComplex(aMatrix.rows(), aMatrix.cols());
		
		for(int row = 0; row < aMatrix.rows(); ++row)
			for(int col = 0; col < aMatrix.cols(); ++col)
				Sin.setItem(row, col, Complex.sin(aMatrix.getItem(row, col)));
			
		for(int row = 0; row < aMatrix.rows(); ++row)
			for(int col = 0; col < aMatrix.cols(); ++col)
				Cos.setItem(row, col, Complex.cos(aMatrix.getItem(row, col)));
		
		for(int row = 0; row < aMatrix.rows(); ++row)
			for(int col = 0; col < aMatrix.cols(); ++col)
				Tan.setItem(row, col, Complex.tan(aMatrix.getItem(row, col)));

		for(int row = 0; row < aMatrix.rows(); ++row)
			for(int col = 0; col < aMatrix.cols(); ++col)
				Euler.setItem(row, col, Complex.exp(aMatrix.getItem(row, col).times(Complex.i)));
				//Euler.setItem(row, col, Cos.getItem(row, col).plus(Sin.getItem(row, col).times(Complex.i)));
		
    	Complex.printBoxTitleRandom(boxSize, "INCORRECT Trigonometrics Matrix functions");
    	aMatrix.println("aMatrix");
    	Sin.println("Sin");
    	Cos.println("Cos");
    	Tan.println("Tan");
    	Sin.power(2).plus(Cos.power(2)).println("Sin²+Cos²");
    	Euler.println("Euler e**(i·aMatrix) = cos(a)+i·(sin(a))");

	}

	private static void trigonometrix(MatrixComplex aMatrix) {
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;

    	Complex.printBoxTitleRandom(boxSize, "CORRECT Trigonometrics Matrix functions");
    	aMatrix.println("aMatrix");
    	bMatrix = aMatrix.sinTaylor(); bMatrix.println("Sin as Taylor expansion");
    	//aMatrix.sinEuler().println("Sin Euler");
    	cMatrix = aMatrix.cosTaylor(); cMatrix.println("Cos as Taylor expansion");
    	//aMatrix.cosEuler().println("Cos Euler");
    	aMatrix.tan().println("Tan");
    	dMatrix = bMatrix.power(2).plus(cMatrix.power(2));
    	dMatrix.println("Sin²+Cos²");
    	//eMatrix = aMatrix.times(new Complex(0,1)).exp();
    	eMatrix = aMatrix.euler();
    	eMatrix.println("Euler e**(i·aMatrix) = cos(a)+i·(sin(a))");
    	dMatrix.determinant().println("Det=");
    	
    	trigonize(aMatrix);
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

    	Complex.printBoxTitleRandom(boxSize, "COMPLEX MATRIX TRIGONOMETRICS CHECK");
    	System.out.println();
    	
    	System.out.println(""
    			+ "This program demonstrates that the most of the implementations of trigonometrics functions \n"
    			+ "associated to Matrices are incorrect.\n"
    			+ "In fact, the sinus of [a11, a21; a21, a22] is NOT [sin(a11), sin(a21); sin(a21), sin(a22)]. \n"
    			+ "    	To check that, only is required to verify:\n"
    			+ "    		1. SIN²+COS²=I, where I is the identity matrix\n"
    			+ "    		2. The identity matrix is, for 2x2, [1, 0; 0, 1] NOT [1, 1; 1, 1]");

    	aMatrix = new MatrixComplex("32.0");
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10)); 
    	
    	aMatrix = new MatrixComplex("1.0, 0.0; 0.0, 1.0");
       	trigonometrix(aMatrix); 
 
    	aMatrix = new MatrixComplex("1.0, 0.0; 0.0, 1.0").times(Complex.DOSPI);
       	trigonometrix(aMatrix); 

    	aMatrix = new MatrixComplex("1.0, 1.0; 1.0, 1.0").times(Complex.DOSPI);
       	trigonometrix(aMatrix); 
 
       	aMatrix = new MatrixComplex("32.0,25.0,30.0;13.0,20.0,-21.0;-2.0,-1.0,31.0");
       	trigonometrix(aMatrix); 

    }
}
