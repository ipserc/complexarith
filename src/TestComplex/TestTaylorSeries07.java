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
    	int intExp;
    	double dbl1Exp, dbl2Exp;
    	Complex cplx1Exp, cplx2Exp;
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
    	
    	MatrixComplex temp1Matrix, temp2Matrix;
    	
		Chronometer chrono = new Chronometer();

		Complex.resetFormatStatus();
		Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
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
      	fMatrix = new MatrixComplex("+7.2726E+01+9.8278E+01i,+7.5078E+00+5.4783E+01i,+1.6334E+01+6.4540E+01i;+8.8763E+01-7.5169E+01i,+9.8896E+01-3.3809E+01i,+5.2836E+01-2.1529E+01i;-9.3018E+01+1.1183E+00i,-1.7963E+01-2.6198E+01i,-1.0862E+01-3.8458E+01i").divides(100);
      	fMatrix = new MatrixComplex("+6.6000E+01,+1.5000E+01,-2.0000E+01;-1.4000E+01,+9.6000E+01,+6.0000E+01;-8.0000E+00,+2.5000E+01,+5.4000E+01").divides(100);

      	gMatrix = new MatrixComplex("4.0+6.0i,6.0-4.0i,-3.0-7.0i;-5.0-6.0i,5.0-2.0i,1.0+8.0i;-1.0-1.0i,-3.0-4.0i,1.0+3.0i").divides(10);
      	gMatrix = new MatrixComplex("+5.0677E+01+9.5330E+01i,+1.0658E+01+8.9966E+01i,-1.4917E+01+1.5147E+01i;+3.4245E-01+3.3125E+01i,+6.6810E+01-4.1174E+01i,+5.8589E+01+3.2591E+01i;+5.6825E+01+2.4992E+01i,+8.8140E+01-2.0386E+01i,+6.1106E+01+9.3839E+01i").divides(100);
      	gMatrix = new MatrixComplex("+7.0294E+01-8.9947E+01i,+4.8836E+01+1.9820E+01i,+4.3029E+01+6.3582E+01i;-4.1176E+01-2.8271E+00i,+9.6981E+01+7.2586E+01i,+2.1621E+01+3.8152E+01i;-7.2680E+01-9.1144E+01i,-2.6745E+01+4.0653E+01i,+5.0771E+01-1.8990E+01i").divides(1000);
      	gMatrix = new MatrixComplex("+5.0000E+01,+2.9000E+01,+4.3000E+01;+6.6000E+01,+3.1000E+01,+8.1000E+01;-4.3000E+01,-6.5000E+01,-3.0000E+00").divides(100);
      	
      	hMatrix = new MatrixComplex("2.0-2.0i,-1.0-3.0i;-1.0-3.0i,2.0-2.0i").divides(10);
      	iMatrix = new MatrixComplex("2.0+2.0i,-3.0+3.0i;3.0+2.0i,1.0+1.0i").divides(10);

    	/*
    	 * NUMBERS DECLARATION SECTION
    	 */
      	cplx1Exp = new Complex("2");
      	cplx2Exp = new Complex(".2-.1i");
    	dbl1Exp = 2;
    	dbl2Exp = 2.0000000001;    	
    	intExp = 2;

       	Complex.printBoxText(3, boxSize, "MATRICES DECLARATION SECTION");
    	Complex.setFormatOFF();
    	aMatrix.println("aMatrix Real Number:");
    	bMatrix.println("bMatrix Complex number:");
    	cMatrix.println("cMatrix Real 2x2 Matrix:");
    	dMatrix.println("dMatrix Complex 2x2 Matrix:");
    	eMatrix.println("eMatrix Real 2x2 Matrix:");
    	fMatrix.println("fMatrix Complex 3x3 Matrix:");
    	gMatrix.println("gMatrix Complex 3x3 Matrix:");
    	hMatrix.println("hMatrix Complex 2x2 Matrix:");
    	iMatrix.println("iMatrix Complex 2x2 Matrix:");

    	Complex.printBoxText(3, boxSize, "NUMBERS DECLARATION SECTION");
    	Complex.setFormatOFF();
    	cplx1Exp.println("COMPLEX REAL EXACT cplx1Exp:");
    	cplx2Exp.println("COMPLEX cplx2Exp:");
    	System.out.println("DOUBLE EXACT dbl1Exp:"+dbl1Exp);
    	System.out.println("DOUBLE dbl2Exp:"+dbl2Exp);
    	System.out.println("INTEGER intExp:"+intExp);

    	Complex.setFormatON();

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE INTEGER");
    	zMatrix = cMatrix.power(intExp);
    	zMatrix.println("zMatrix = cMatrix.power(intExp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix.power(1.0/intExp).println("cMatrix = zMatrix.power(1.0/intExp)");
 
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A NEGATIVE INTEGER");
    	zMatrix = cMatrix.power(-intExp);
    	zMatrix.println("cMatrix.power(-intExp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix.power(-1.0/intExp).println("cMatrix = zMatrix.power(-1.0/intExp)");
    	
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE REAL INTEGER");
    	zMatrix = cMatrix.power(dbl1Exp);
    	zMatrix.println("zMatrix = cMatrix.power(dbl1Exp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix.power(1.0/dbl1Exp).println("cMatrix = zMatrix.power(1.0/dbl1Exp)");
 
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A NEGATIVE REAL INTEGER");
    	zMatrix = cMatrix.power(-dbl1Exp);
    	zMatrix.println("cMatrix.power(-dbl1Exp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix.power(-1.0/dbl1Exp).println("cMatrix = zMatrix.power(-1.0/dbl1Exp)");

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE REAL INTEGER TIMES MATRIX POWERED TO A NEGATIVE REAL INTEGER");
    	temp1Matrix = cMatrix.power(dbl1Exp);
    	temp2Matrix = cMatrix.power(-dbl1Exp);
    	temp1Matrix.println("cMatrix.power(dbl1Exp)");
       	temp2Matrix.println("cMatrix.power(-dbl1Exp)");
    	zMatrix = temp1Matrix.times(temp2Matrix);
    	zMatrix.println("cMatrix.power(dbl1Exp) * cMatrix.power(-dbl1Exp)");

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE REAL");
    	zMatrix = cMatrix.power(dbl2Exp);
    	zMatrix.println("cMatrix.power(dbl2Exp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix.power(1.0/dbl2Exp).println("cMatrix = zMatrix.power(1.0/dbl2Exp)");
 
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A NEGATIVE REAL");
    	zMatrix = cMatrix.power(-dbl2Exp);
    	zMatrix.println("cMatrix.power(-dbl2Exp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix.power(-1.0/dbl2Exp).println("cMatrix = zMatrix.power(-1.0/dbl2Exp)");

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE REAL TIMES MATRIX POWERED TO A NEGATIVE REAL");
    	temp1Matrix = cMatrix.power(dbl2Exp);
    	temp2Matrix = cMatrix.power(-dbl2Exp);
    	temp1Matrix.println("cMatrix.power(dbl2Exp)");
       	temp2Matrix.println("cMatrix.power(-dbl2Exp)");
    	zMatrix = temp1Matrix.times(temp2Matrix);
    	zMatrix.println("cMatrix.power(dbl2Exp) * cMatrix.power(-dbl2Exp)");

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE COMPLEX REAL EXACT");
    	zMatrix = cMatrix.power(cplx1Exp);
    	zMatrix.println("cMatrix.power(cplx1Exp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix.power(cplx1Exp.inverse()).println("cMatrix = zMatrix.power(cplx1Exp.inverse())");
 
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A NEGATIVE COMPLEX REAL EXACT");
    	zMatrix = cMatrix.power(cplx1Exp.opposite());
    	zMatrix.println("cMatrix.power(-cplx1Exp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix.power(cplx1Exp.opposite().inverse()).println("cMatrix = zMatrix.power(cplx1Exp.opposite().inverse())");

    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A POSITIVE COMPLEX");
    	zMatrix = cMatrix.power(cplx2Exp);
    	zMatrix.println("cMatrix.power(cplx2Exp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix.power(cplx2Exp.inverse()).println("cMatrix = zMatrix.power(cplx2Exp.inverse())");
 
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A NEGATIVE COMPLEX");
    	zMatrix = cMatrix.power(cplx2Exp.opposite());
    	zMatrix.println("cMatrix.power(-cplx2Exp)");

    	
    	Complex.printBoxTitle(3, boxSize, "LOG OF A MATRIX OF REAL NUMBERS");
    	MatrixComplex.debugOFF();
    	//zMatrix = cMatrix.log();
    	zMatrix = MatrixComplex.log(cMatrix);
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
    	
    	Complex.printBoxTitle(3, boxSize, "COMPLEX MATRIX RAISED TO A COMPLEX MATRIX (3x3) TIMES ITS INVERSE");
    	MatrixComplex.debugOFF();
    	zMatrix = fMatrix.power(gMatrix).times(fMatrix.power(gMatrix.opposite()));
    	zMatrix.println("fMatrix.power(gMatrix).times(fMatrix.power(gMatrix.opposite()))");

    }
}
