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

public class TestTaylorSeries01 {
	
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
    	eMatrix = aMatrix.times(Complex.i).exp();
    	eMatrix.println("Moivre e**(iaMatrix) = cos(a)+i(sin(a))");
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

    	Complex.setScientificON(6);
    	Complex.setFormatON();
    	Complex.showPrecision();
    	Complex.printFormatStatus();

    	Complex.printBoxTitleRandom(boxSize, "MATRIX COMPLEX TAYLOR & McLAURIN SERIES");
    	System.out.println();

		Complex.printBoxTitleRandom(boxSize, "Exponential function");
    	aMatrix = new MatrixComplex(""
    			+ " 1.2,  3.7,  5.6;"
    			+ " 0.0, -2.2,  4.0;"
    			+ "-5.3,  0.0,  1.4");
    	aMatrix.println("aMatrix");
    	aMatrix.exp().println("Exp");
    	
    	aMatrix = new MatrixComplex(""
    			+ "1, 2;"
    			+ "0, 1");
    	aMatrix.println("aMatrix");
    	aMatrix.exp().println("Exp");

    	System.out.println();
    	Complex.printBoxTitleRandom(boxSize, "Exponential & Trigonometrics Matrix functions");
    	aMatrix = new MatrixComplex(""+
    			" 1, 1,-i, 1;"+
    			" 2i,2, 1, 2;"+
    			" 1, i, 0, 2;"+
				" 1, 0, 1,-2i");
    	trigonometrix(aMatrix); 
    	
    	aMatrix = new MatrixComplex(""
    			+ "1, 2;"
    			+ "0, 1");
    	trigonometrix(aMatrix); 

    	aMatrix = new MatrixComplex(""
    			+ " 1.2,  3.7,  5.6;"
    			+ " 0.0, -2.2,  4.0;"
    			+ "-5.3,  0.0,  1.4");
    	trigonometrix(aMatrix); 

    	System.out.println();
    	Complex.printBoxTitleRandom(boxSize, "Hyperbolic Trigonometrics Matrix functions");
    	aMatrix = new MatrixComplex(""
    			+ "1, 2;"
    			+ "0, 1");
    	trigonometrix(aMatrix); 
    	System.out.println();
		
    	Complex.printBoxTitleRandom(boxSize, "Hyperbolic Trigonometrics Matrix chronos");
    	aMatrix = new MatrixComplex(3);
       	aMatrix.initMatrixRandomPol(3);
       	// aMatrix = aMatrix.divides(1E2);
       	
    	aMatrix.println("aMatrix");
    	chrono.start();
    	bMatrix = aMatrix.sinhTaylor(); bMatrix.println("b=Hyperbolic Sin Taylor");
    	chrono.stop();
    	chrono.println("Hyperbolic Sin Taylor: ");
    	
    	chrono.start();
    	aMatrix.sinhEuler().println("Hyperbolic Sin Euler");
    	chrono.stop();
    	chrono.println("Hyperbolic Sin Euler: ");
    	
    	chrono.start();
    	bMatrix = aMatrix.coshTaylor(); bMatrix.println("b=Hyperbolic Cos Taylor");
    	chrono.stop();
    	chrono.println("Hyperbolic Cos Taylor: ");
    	
    	chrono.start();
    	aMatrix.coshEuler().println("Hyperbolic Cos Euler");
    	chrono.stop();
    	chrono.println("Hyperbolic Cos Euler: ");

    	System.out.println();
    	Complex.printBoxTitleRandom(boxSize, "Natural logarithm Matrix functions");
    	aMatrix = new MatrixComplex(""
    			+ "0.1, 0.2;"
    			+ "0.0, 0.1");
    	aMatrix.println("aMatrix");
    	aMatrix.determinant().println("Det=");
       	//bMatrix = aMatrix.log(); bMatrix.println("b=log");
    	Complex.printBoxTextRandom(boxSize, "log(1-x)");
       	bMatrix = aMatrix.logTaylor(); bMatrix.println("b=logTaylor");
     	bMatrix.exp().println("exp(b)");
    	Complex.printBoxTextRandom(boxSize, "log(1+x)");
       	bMatrix = aMatrix.logMercator(); bMatrix.println("b=logMercator");
    	bMatrix.exp().println("exp(b)");
    	
       	System.out.println();
       	aMatrix = new MatrixComplex(""
    			+ "1, 2;"
    			+ "0, 1");
    	aMatrix.println("aMatrix");
    	aMatrix.determinant().println("Det=");
    	Complex.printBoxTextRandom(boxSize, "log(1-x)");
    	//bMatrix = aMatrix.log(); bMatrix.println("b=log");
       	bMatrix = aMatrix.logTaylor(); bMatrix.println("b=logTaylor");
    	bMatrix.exp().println("exp(b)");
    	Complex.printBoxTextRandom(boxSize, "log(1+x)");
       	bMatrix = aMatrix.logMercator(); bMatrix.println("b=logMercator");
    	bMatrix.exp().println("exp(b)");

       	System.out.println();
       	aMatrix = new MatrixComplex(""
    			+ "1.1, 2;"
    			+ "-.5, 1");
    	aMatrix.println("aMatrix");
    	aMatrix.determinant().println("Det=");
    	Complex.printBoxTextRandom(boxSize, "log(1-x)");
    	//bMatrix = aMatrix.log(); bMatricomplexx.println("b=log");
       	bMatrix = aMatrix.logTaylor(); bMatrix.println("b=logTaylor");
    	bMatrix.exp().println("exp(b)");
    	Complex.printBoxTextRandom(boxSize, "log(1+x)");
       	bMatrix = aMatrix.logMercator(); bMatrix.println("b=logMercator");
    	bMatrix.exp().println("exp(b)");

    	System.out.println();
    	Complex.printBoxTitleRandom(boxSize, "log Matrix function");
    	aMatrix = new MatrixComplex(4);
       	aMatrix.initMatrixRandomInteger(3);
       	// aMatrix = aMatrix.triangle();
       	// aMatrix = aMatrix.divides(10000);
   		aMatrix = new MatrixComplex(""
    			+ " 1.2,  3.7,  5.6;"
    			+ " 0.0, -2.2,  4.0;"
    			+ "-5.3,  0.0,  1.4");
      	// aMatrix = aMatrix.divides(10000);

   		/*
    	aMatrix.println("aMatrix");
    	aMatrix.determinant().println("Det=");   	
    	bMatrix = aMatrix.log(); 
    	bMatrix.println("b=log");
    	bMatrix.exp().println("exp(b)");
    	*/
   		
   		aMatrix = new MatrixComplex(2,2);
   		aMatrix.initMatrixRandomInteger(3);
    	aMatrix = aMatrix.hermitian();
    	aMatrix.abs();
    	aMatrix.println("aMatrix Hermitian");
    	aMatrix.determinant().println("Det=");   	
    	bMatrix = aMatrix.log(); 
    	bMatrix.println("b=log");
    	if (!(bMatrix.isInfinite() || bMatrix.isNaN()))
    		bMatrix.exp().println("exp(b)");
    	
    	aMatrix = new MatrixComplex("4.000,-1.000,1.000; 0.000,1.000,3.000;0.000,2.000,2.000");
    	aMatrix.println("aMatrix");
    	aMatrix.exp().println("exp(a)");
    	aMatrix.sin().println("sin(a)");
    	MatrixComplex.sin(aMatrix).println("sin(a)");
    	aMatrix.cos().println("cos(a)");
    	(aMatrix.times(Complex.i)).exp().println("exp(ia)");
    	aMatrix.cos().plus(aMatrix.sin().times(Complex.i)).println("cos(a)+i*sin(a)");

    	Complex.setFixedON(3);

    	aMatrix = new MatrixComplex(3);
    	aMatrix.initMatrixRandomRecInt(10); // Complex Numbers
    	aMatrix = aMatrix.hermitian();
    	trigonometrix(aMatrix); 

    	aMatrix = new MatrixComplex(3);
    	aMatrix.initMatrixRandomInteger(10); // Integer Numbers
    	//aMatrix = aMatrix.hermitian();
    	//aMatrix.abs();		
    	trigonometrix(aMatrix); 
    	
    	aMatrix = new MatrixComplex("32.0,25.0,30.0;13.0,20.0,-21.0;-2.0,-1.0,31.0");
       	trigonometrix(aMatrix); 
       	trigonometrix(aMatrix.divides(10)); 

    }
}
