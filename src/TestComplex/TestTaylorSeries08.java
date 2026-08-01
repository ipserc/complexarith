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
import com.ipserc.arith.factorization.*;

import java.util.List;
import java.util.ArrayList;

public class TestTaylorSeries08 {
	
	static int boxSize = 65;
	
	public static void check1(Complex cplx2Exp, MatrixComplex zMatrix) {
    	Complex.printBoxText(3, boxSize, "Check ONE");
    	cplx2Exp.inverse().println("cplx2Exp.inverse():");
    	System.out.println("|cplx2Exp.inverse()| = " + cplx2Exp.inverse().mod());
    	try {
    		zMatrix.power(cplx2Exp.inverse()).println("aMatrix = zMatrix.power(cplx2Exp.inverse())");
    	}
    	catch (Exception e)  {
    		System.out.println("******* ERROR:"+e.getCause());
    		return;
    	}

    	Diagfactor dmat = new Diagfactor(zMatrix);   	
    	MatrixComplex Dmat = dmat.D().copy();
    	for (int i = 0; i < Dmat.cols(); ++i) 
    		Dmat.setItem(i, i, Dmat.getItem(i, i).power(cplx2Exp.inverse()));
 
    	MatrixComplex Pmat = dmat.P().copy();  	
    	Pmat.times(Dmat).times(Pmat.inverse()).println("Power Diagonal");
	}

	public static void check2(Complex cplx2Exp, MatrixComplex zMatrix) {
    	Complex.printBoxText(3, boxSize, "Check TWO");
    	cplx2Exp = cplx2Exp.inverse();
    	cplx2Exp.println("cplx2Exp.inverse():");
    	cplx2Exp = cplx2Exp.times(10);
    	cplx2Exp.println("cplx2Exp.inverse().times(10):");
    	System.out.println("|cplx2Exp.inverse().times(10)| = " + cplx2Exp.mod());
    	zMatrix = zMatrix.power(cplx2Exp);
    	zMatrix.println("zMatrix = zMatrix.power(cplx2Exp.inverse().times(10))");
    	zMatrix = zMatrix.power(.1);    	
    	zMatrix.println("aMatrix = zMatrix.power(cplx2Exp.inverse().times(10)).power(.1)");
	}
	
	public static void check3(MatrixComplex zM, MatrixComplex eM) {
		Complex.printBoxText(3, boxSize, "Check THREE");
		try {
			MatrixComplex cM = zM.power(eM.inverse());
			cM.println("cM = zM.power(eM.inverse())");
	    	System.out.println("det(cM) = " + cM.determinant());
		}
    	catch (Exception e)  {
    		System.out.println("******* ERROR:"+e.getCause());
    		return;
    	}
	}
	
	
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
    	MatrixComplex jMatrix;
    	MatrixComplex kMatrix;
    	MatrixComplex lMatrix;
    	MatrixComplex zMatrix;
    	
    	MatrixComplex temp1Matrix, temp2Matrix;
    	
    	MatrixComplex potMat;
    	
		Chronometer chrono = new Chronometer();

		Complex.resetFormatStatus();
		Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotON();
    	Complex.digits(10000000);
    	Complex.setScientificON(4);
    	Complex.numpPadPLUS();
    	
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
    	cMatrix = new MatrixComplex(2); cMatrix.initMatrixRandomInt(3);
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
      	jMatrix = new MatrixComplex("3+i , -2+3i ; -4-i , 7-2i");
       	kMatrix = new MatrixComplex(2); kMatrix.initMatrixRandomRec(); // kMatrix = kMatrix.divides(10);
       	lMatrix = hMatrix.divides(10);

      	
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
    	gMatrix.println("gMatrix Complex 3x3 MatrcM = zM.exp(eM.opposite())ix:");
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


    	/* ************************************************************************************************* /
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A COMPLEX");
      	cplx2Exp = new Complex(".03-.04i");
    	cplx2Exp.println("COMPLEX cplx2Exp:");
    	System.out.println("|cplx2Exp| = " + cplx2Exp.mod());
    	zMatrix = cMatrix.power(cplx2Exp);
    	zMatrix.println("zMatrix = cMatrix.power(cplx2Exp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix.power(cplx2Exp.inverse()).println("cMatrix = zMatrix.power(cplx2Exp.inverse())");
    	/************************************************************************************************* */

    	/* ************************************************************************************************* /
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A COMPLEX");
      	cplx2Exp = new Complex(".3-.4i");
    	cplx2Exp.println("COMPLEX cplx2Exp:");
    	System.out.println("|cplx2Exp| = " + cplx2Exp.mod());
    	zMatrix = cMatrix.power(cplx2Exp);
    	zMatrix.println("cMatrix.power(cplx2Exp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	cplx2Exp.inverse().println("cplx2Exp.inverse():");
    	System.out.println("|cplx2Exp.inverse()| = " + cplx2Exp.inverse().mod());
    	zMatrix.power(cplx2Exp.inverse()).println("cMatrix = zMatrix.power(cplx2Exp.inverse())");
    	/************************************************************************************************* */

		/* ************************************************************************************************* /
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A COMPLEX MATRIX");
    	cMatrix.println("cMatrix");
    	System.out.println("det(cMatrix) = " + cMatrix.determinant());
    	hMatrix.println("Exponent hMatrix");
    	System.out.println("det(hMatrix) = " + hMatrix.determinant());
    	zMatrix = cMatrix.power(hMatrix);
    	zMatrix.println("cMatrix.power(hMatrix)");

    	check3(zMatrix, hMatrix);
    	/************************************************************************************************* */

    	/* ************************************************************************************************* /
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A COMPLEX");
      	cplx2Exp = new Complex("3-4i");
    	cplx2Exp.println("COMPLEX cplx2Exp:");
    	System.out.println("|cplx2Exp| = " + cplx2Exp.mod());
    	zMatrix = cMatrix.power(cplx2Exp);
    	zMatrix.println("cMatrix.power(cplx2Exp)");

    	Complex.printBoxText(3, boxSize, "Check");
    	cplx2Exp.inverse().println("cplx2Exp.inverse():");
    	System.out.println("|cplx2Exp.inverse()| = " + cplx2Exp.inverse().mod());
    	zMatrix.power(cplx2Exp.inverse()).println("cMatrix = zMatrix.power(cplx2Exp.inverse())");

    	Complex.printBoxText(3, boxSize, "Check 2");
    	cplx2Exp = cplx2Exp.inverse();COMPLEX
    	cplx2Exp.println("cplx2Exp.inverse():");
    	cplx2Exp = cplx2Exp.times(10);
    	cplx2Exp.println("cplx2Exp.inverse().times(10):");
    	System.out.println("|cplx2Exp.inverse().times(10)| = " + cplx2Exp.mod());
    	zMatrix = zMatrix.power(cplx2Exp);
    	zMatrix.println("zMatrix = zMatrix.power(cplx2Exp.inverse().times(10))");
    	zMatrix = zMatrix.power(.1);    	
    	zMatrix.println("cMatrix = zMatrix.power(cplx2Exp.inverse().times(10)).power(.1)");
    	/************************************************************************************************* */
    	
    	/* ************************************************************************************************* /
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A COMPLEX");
      	cplx2Exp = new Complex("30-40i").inverse();
    	cplx2Exp.println("COMPLEX cplx2Exp:");
    	System.out.println("|cplx2Exp| = " + cplx2Exp.mod());
    	cMatrix = new MatrixComplex(  "-8.1918E+01-1.0171E+02i, 1.1118E+02+1.3404E+02i; "
    								+ " 2.2237E+02+2.6808E+02i,-3.0429E+02-3.6979E+02i" );
    	zMatrix = cMatrix.power(cplx2Exp);
    	zMatrix.println("cMatrix.power(cplx2Exp)");
    	Complex.printBoxText(3, boxSize, "Check");
    	zMatrix = zMatrix.power(cplx2Exp.inverse());
    	zMatrix.println("cMatrix = zMatrix.power(cplx2Exp.inverse())");
    	/************************************************************************************************* */

    	/* ************************************************************************************************* /
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A COMPLEX");
    	aMatrix.println("aMatrix");
      	cplx2Exp = new Complex("3");
    	cplx2Exp.println("Exponent cplx2Exp:");
    	System.out.println("|cplx2Exp| = " + cplx2Exp.mod());
    	zMatrix = aMatrix.power(cplx2Exp);
    	zMatrix.println("aMatrix.power(cplx2Exp)");

    	check1(cplx2Exp, zMatrix);

    	check2(cplx2Exp, zMatrix);
    	/************************************************************************************************* */

    	/* ************************************************************************************************* /
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A COMPLEX");
    	bMatrix.println("bMatrix");
      	cplx2Exp = new Complex("3");
    	cplx2Exp.println("Exponent cplx2Exp:");
    	System.out.println("|cplx2Exp| = " + cplx2Exp.mod());
    	zMatrix = bMatrix.power(cplx2Exp);
    	zMatrix.println("bMatrix.power(cplx2Exp)");

    	check1(cplx2Exp, zMatrix);

    	check2(cplx2Exp, zMatrix);
    	/************************************************************************************************* * 

    	/* ************************************************************************************************* /
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO AN INTEGER");
    	cMatrix.println("cMatrix");
    	intExp = 3;
    	System.out.println("Exponent intExp:" + intExp);
    	System.out.println("|intExp| = " + Math.abs(intExp));
    	zMatrix = cMatrix.power(intExp);
    	zMatrix.println("cMatrix.power(intExp)");
    	cMatrix.times(cMatrix).times(cMatrix).println("cMatrix^intExp");

    	check1(cplx2Exp, zMatrix);

    	check2(cplx2Exp, zMatrix);
    	/************************************************************************************************* */
    	
    	/* ************************************************************************************************* /
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A COMPLEX");
    	cMatrix.println("cMatrix");
      	cplx2Exp = new Complex("3");
    	cplx2Exp.println("Exponent cplx2Exp:");
    	System.out.println("|cplx2Exp| = " + cplx2Exp.mod());
    	zMatrix = cMatrix.power(cplx2Exp);
    	zMatrix.println("cMatrix.power(cplx2Exp)");

    	check1(cplx2Exp, zMatrix);

    	check2(cplx2Exp, zMatrix);
    	/************************************************************************************************* */

       	/* *************************************************************************************************/
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO AN INTEGER");
    	cMatrix.println("cMatrix");
    	intExp = 3;
    	System.out.println("Exponent intExp:" + intExp);
    	System.out.println("|intExp| = " + Math.abs(intExp));
    	zMatrix = cMatrix.power(intExp);
    	zMatrix.println("zMatrix = cMatrix.power(intExp)");
    	potMat = cMatrix.copy();
    	for (int i = 1; i < intExp; ++i)
    		potMat = potMat.times(cMatrix);
    	potMat.println("kMatrix^intExp");

    	check1(new Complex(intExp,0), zMatrix);
    	/************************************************************************************************* */

    	/* *************************************************************************************************/
    	Complex.printBoxTitle(3, boxSize, "COMPLEX MATRIX POWERED TO AN INTEGER");
    	kMatrix.println("kMatrix");
    	intExp = 4;
    	System.out.println("Exponent intExp:" + intExp);    	
    	System.out.println("|intExp| = " + Math.abs(intExp));
    	zMatrix = kMatrix.power(intExp);
    	zMatrix.println("zMatrix = kMatrix.power(intExp)");
    	potMat = kMatrix.copy();
    	for (int i = 1; i < intExp; ++i)
    		potMat = potMat.times(kMatrix);
    	potMat.println("kMatrix^intExp");

    	check1(new Complex(intExp,0), zMatrix);
    	/************************************************************************************************* */

    	/* *************************************************************************************************/
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A DOUBLE");
    	cMatrix.println("cMatrix");
    	dbl1Exp = 1.0/3.0;
    	System.out.println("Exponent dbl1Exp:" + dbl1Exp);
    	System.out.println("|dbl1Exp| = " + Math.abs(dbl1Exp));
    	zMatrix = cMatrix.power(dbl1Exp);
    	zMatrix.println("zMatrix = cMatrix.power(dbl1Exp)");

    	check1(new Complex(dbl1Exp,0), zMatrix);
    	/************************************************************************************************* */

     	/* *************************************************************************************************/
    	Complex.printBoxTitle(3, boxSize, "COMPLEX MATRIX POWERED TO A DOUBLE");
    	jMatrix.println("jMatrix");
    	dbl1Exp = 1.0/3.0;
    	System.out.println("Exponent dbl1Exp:" + dbl1Exp);
    	System.out.println("|dbl1Exp| = " + Math.abs(dbl1Exp));
    	zMatrix = jMatrix.power(dbl1Exp);
    	zMatrix.println("zMatrix = jMatrix.power(dbl1Exp)");
    	jMatrix.times(jMatrix).times(jMatrix).inverse().println("jMatrix^dbl1Exp");

    	check1(new Complex(dbl1Exp,0), zMatrix);
    	/************************************************************************************************* */
    	
    	/* *************************************************************************************************/
    	Complex.printBoxTitle(3, boxSize, "COMPLEX MATRIX POWERED TO A MATRIX");
    	jMatrix.println("jMatrix");
    	System.out.println("det(jMatrix) = " + jMatrix.determinant());
    	eMatrix.println("Exponent eMatrix");
    	System.out.println("|det(eMatrix)| = " + eMatrix.determinant().abs());
    	zMatrix = jMatrix.power(eMatrix);
    	zMatrix.println("zMatrix = jMatrix.power(eMatrix)");

    	check3(zMatrix, eMatrix);
    	/************************************************************************************************* */

    	/* *************************************************************************************************/
    	Complex.printBoxTitle(3, boxSize, "MATRIX POWERED TO A COMPLEX MATRIX");
    	cMatrix.println("cMatrix");
    	System.out.println("det(cMatrix) = " + cMatrix.determinant());
    	hMatrix.println("Exponent hMatrix");
    	System.out.println("det(hMatrix) = " + hMatrix.determinant());
    	zMatrix = cMatrix.power(hMatrix);
    	zMatrix.println("zMatrix = cMatrix.power(hMatrix)");

    	check3(zMatrix, hMatrix);
    	/************************************************************************************************* */

    	/* *************************************************************************************************/
    	Complex.printBoxTitle(3, boxSize, "COMPLEX MATRIX POWERED TO A COMPLEX MATRIX");
    	iMatrix.println("iMatrix");
    	System.out.println("det(iMatrix) = " + iMatrix.determinant());
    	hMatrix.println("Exponent hMatrix");
    	System.out.println("det(hMatrix) = " + hMatrix.determinant());
    	zMatrix = iMatrix.power(hMatrix);
    	zMatrix.println("zMatrix = iMatrix.power(hMatrix)");

    	check3(zMatrix, hMatrix);
    	/************************************************************************************************* */
	}
}
