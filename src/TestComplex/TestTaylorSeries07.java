/******************************************************************************
 *  Compilation:  javac TestComplex.java
 *  Execution:    java TestComplex
 *
 *  Tests for arith.Complex.
 *	
 *  Este programa chequea la validez de las diferentes implementaciones de las 
 *  funciones potencia, exponencial y logaritmo natural entre números y matrices 
 *  para valores enteros, reales y complejos.
 *  
 *  El método de chequeo consiste en realizar la operación deseada y posteriormente 
 *  aplicar el operador inverso para comprobar que el resultado obtenido fue el de partida.
 *  
 *  La operación Log de matrices no siempre es viable, y para los casos aquí 
 *  estudiados se han utilizado matrices que sí poseen logaritmo.
 *  
 *  También se han probado operaciones que se pueden llevar a cabo mediante dos 
 *  aproximaciones diferentes, y para estos casos la prueba es que el resultado 
 *  de la operación es el mismo en ambos casos.
 *  
 *  This program checks the validity of the different implementations of the power, 
 *  exponential and natural logarithm functions between numbers and matrices 
 *  for integer, real and complex values.
 *  
 *  The checking method consists of performing the desired operation and 
 *  subsequently applying the inverse operator to verify that the result 
 *  obtained was the starting one.
 *  
 *  The Log operation of matrices is not always viable, and for the cases 
 *  studied here, matrices that do have a logarithm have been used.
 *  
 *  Operations that can be carried out using two different approaches have 
 *  also been tested, and for these cases the proof is that the result of 
 *  the operation is the same in both cases.
 *
 ******************************************************************************/
package TestComplex;

import com.ipserc.arith.complex.Complex;
//import arith.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;
import com.ipserc.arith.factorization.*;

import java.util.List;
import java.util.ArrayList;

public class TestTaylorSeries07 {
	
	static int boxSize = 65;

    public static void main(String[] args) {
    	int iExp;
    	double d1Exp, d2Exp;
    	Complex c1Exp, c2Exp;
     	MatrixComplex aMatrix;
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;
    	MatrixComplex fMatrix;
    	MatrixComplex gMatrix;
    	MatrixComplex hMatrix;
    	MatrixComplex iMatrix;
    	MatrixComplex zMatrix;
		Chronometer chrono = new Chronometer();

		Complex.resetFormatStatus();
		Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotOFF();
    	Complex.digits(10000000);
    	Complex.setScientificON(4);
    	
    	Complex.showPrecision();
    	Complex.printFormatStatus();

    	Complex.printBoxTitle(3, boxSize, "COMPLEX MATRIX POWER, EXPONENT, LOGARITHM DEMO");
    	System.out.println();

    	/*
    	 * MATRICES DECLARATION SECTION
    	 */
    	aMatrix = new MatrixComplex("32");
    	bMatrix = new MatrixComplex("-2+3i");
    	cMatrix = new MatrixComplex("3 , -2; -4, 7");
      	//cMatrix = new MatrixComplex("3 , 2; -4, 7");
      	//cMatrix = new MatrixComplex("8.0,-5.0;-2.0,7.0");
     	//cMatrix = new MatrixComplex("0,-2,-2;1,3,1;0,0,2");
      	//cMatrix = new MatrixComplex("1 , 1; 0, 1");
     	//eMatrix = new MatrixComplex("66.0,-37.0;35.0,-12.0").divides(10);
     	//eMatrix = new MatrixComplex("6.0,-1.0;9.0,-1.0").divides(10); // Real eigenvalues
     	//eMatrix = new MatrixComplex("4.0,2.0;-1.0,4.0").divides(10); // Complex eigenvalues
     	eMatrix = new MatrixComplex("0.6,0.3;0.3,0.8"); // Hermitian Postive Definite
      	//dMatrix = new MatrixComplex("-3+2i, 4-i; 1+i, 3-4i");
      	dMatrix = new MatrixComplex("99.0+49.0i,-82.0-36.0i;91.0-31.0i,27.0-93.0i").divides(1000);
      	fMatrix = new MatrixComplex("3.0+6.0i,-3.0-9.0i,-3.0+7.0i;-1.0-3.0i,8.0+5.0i,2.0-8.0i;-6.0+2.0i,-5.0-8.0i,1.0-1.0i").divides(10);
      	gMatrix = new MatrixComplex("4.0+6.0i,6.0-4.0i,-3.0-7.0i;-5.0-6.0i,5.0-2.0i,1.0+8.0i;-1.0-1.0i,-3.0-4.0i,1.0+3.0i").divides(10);
      	hMatrix = new MatrixComplex("2.0-2.0i,-1.0-3.0i;-1.0-3.0i,2.0-2.0i").divides(10);
      	iMatrix = new MatrixComplex("2.0+2.0i,-3.0+3.0i;3.0+2.0i,1.0+1.0i").divides(10);

    	/*
    	 * NUMBERS DECLARATION SECTION
    	 */
      	c1Exp = new Complex("2");
      	c2Exp = new Complex("2+.0000000001i");
    	d1Exp = 2;
    	d2Exp = 2.0000000001;    	
    	iExp = 2;

       	Complex.printBoxText(3, boxSize, "MATRICES DECLARATION SECTION");
    	Complex.setFormatOFF();
    	aMatrix.println("aMatrix:");
    	bMatrix.println("bMatrix:");
    	cMatrix.println("cMatrix:");
    	dMatrix.println("dMatrix:");
    	eMatrix.println("eMatrix:");
    	fMatrix.println("fMatrix:");
    	gMatrix.println("gMatrix:");
    	hMatrix.println("hMatrix:");
    	iMatrix.println("iMatrix:");

    	Complex.printBoxText(3, boxSize, "NUMBERS DECLARATION SECTION");
    	Complex.setFormatOFF();
    	c1Exp.println("COMPLEX REAL EXACT c1Exp:");
    	c2Exp.println("COMPLEX c2Exp:");
    	System.out.println("DOUBLE EXACT d1Exp:"+d1Exp);
    	System.out.println("DOUBLE d2Exp:"+d2Exp);
    	System.out.println("INTEGER iExp:"+iExp);

    	Complex.setFormatON();

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE INTEGER");
    	zMatrix = cMatrix.power(iExp);
    	zMatrix.println("cMatrix.power(iExp)");
 
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A NEGATIVE INTEGER");
    	zMatrix = cMatrix.power(-iExp);
    	zMatrix.println("cMatrix.power(-iExp)");

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE REAL INTEGER");
    	zMatrix = cMatrix.power(d1Exp);
    	zMatrix.println("cMatrix.power(d1Exp)");
 
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A NEGATIVE REAL INTEGER");
    	zMatrix = cMatrix.power(-d1Exp);
    	zMatrix.println("cMatrix.power(-d1Exp)");

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE REAL");
    	zMatrix = cMatrix.power(d2Exp);
    	zMatrix.println("cMatrix.power(d2Exp)");
 
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A NEGATIVE REAL");
    	zMatrix = cMatrix.power(-d2Exp);
    	zMatrix.println("cMatrix.power(-d2Exp)");

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE COMPLEX REAL EXACT");
    	zMatrix = cMatrix.power(c1Exp);
    	zMatrix.println("cMatrix.power(c1Exp)");
 
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A NEGATIVE COMPLEX REAL EXACT");
    	zMatrix = cMatrix.power(c1Exp.opposite());
    	zMatrix.println("cMatrix.power(-c1Exp)");

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE COMPLEX");
    	zMatrix = cMatrix.power(c2Exp);
    	zMatrix.println("cMatrix.power(c2Exp)");
 
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A NEGATIVE COMPLEX");
    	zMatrix = cMatrix.power(c2Exp.opposite());
    	zMatrix.println("cMatrix.power(-c2Exp)");

    	
    	Complex.printBoxTitle(3, boxSize, "LOG OF A MATRIX OF REAL NUMBERS");
    	MatrixComplex.debugOFF();
    	zMatrix = cMatrix.log();
    	zMatrix.println("cMatrix.log()");
    	cMatrix.logMercator().println("cMatrix.logMercator()");
    	zMatrix.exp().println("CHECK exp(cMatrix.log())");
    	
    	Complex.printBoxTitle(3, boxSize, "LOG OF A MATRIX OF COMPLEX NUMBERS");
    	MatrixComplex.debugOFF();
    	zMatrix = dMatrix.logTaylor();
    	zMatrix.println("dMatrix.log()");
    	dMatrix.logMercator().println("dMatrix.logMercator()");
    	zMatrix.exp().println("CHECK exp(dMatrix.log())");

    	Complex.printBoxTitle(3, boxSize, "REAL MATRIX RAISED TO A REAL MATRIX");
    	MatrixComplex.debugOFF();
    	zMatrix = cMatrix.power(eMatrix);
    	zMatrix.println("cMatrix.power(eMatrix)");
    	zMatrix.power(eMatrix.inverse()).println("CHECK zMatrix.power(eMatrix.inverse())");

    	Complex.printBoxTitle(3, boxSize, "REAL MATRIX RAISED TO A COMPLEX MATRIX");
    	MatrixComplex.debugOFF();
    	zMatrix = eMatrix.power(dMatrix);
    	zMatrix.println("eMatrix.power(dMatrix)");
    	zMatrix.power(dMatrix.inverse()).println("CHECK zMatrix.power(dMatrix.inverse())");

    	Complex.printBoxTitle(3, boxSize, "COMPLEX MATRIX RAISED TO A REAL MATRIX");
    	MatrixComplex.debugOFF();
    	zMatrix = dMatrix.power(eMatrix);
    	zMatrix.println("dMatrix.power(eMatrix)");
    	zMatrix.power(eMatrix.inverse()).println("CHECK zMatrix.power(eMatrix.inverse())");

    	Complex.printBoxTitle(3, boxSize, "COMPLEX MATRIX RAISED TO A COMPLEX MATRIX (2x2)");
    	MatrixComplex.debugOFF();
    	zMatrix = hMatrix.power(iMatrix);
    	zMatrix.println("hMatrix.power(iMatrix)");
    	zMatrix.power(iMatrix.inverse()).println("CHECK zMatrix.power(iMatrix.inverse())");

    	Complex.printBoxTitle(3, boxSize, "COMPLEX MATRIX RAISED TO A COMPLEX MATRIX (3x3)");
    	MatrixComplex.debugOFF();
    	zMatrix = fMatrix.power(gMatrix);
    	zMatrix.println("fMatrix.power(gMatrix)");
    	zMatrix.power(gMatrix.inverse()).println("CHECK zMatrix.power(gMatrix.inverse())");
    }
}
