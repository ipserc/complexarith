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
import com.ipserc.arith.factorization.*;

import java.util.List;
import java.util.ArrayList;

public class TestTaylorSeries05 {
	
	static int boxSize = 65;

	private static void doPower(MatrixComplex aMatrix) {
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;
    	Diagfactor DMatrix;

    	Complex.printBoxTitleRandom(boxSize, "power Matrix functions");
    	
    	/** /
    	Complex.printBoxTextRandom(boxSize, "Matrix Square Root");
    	aMatrix.println("aMatrix");
    	bMatrix = aMatrix.power(0.5); bMatrix.println("sqrt(aMatrix)");
    	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) bMatrix.power(2).println("bMatrix²");
    	
    	Complex.printBoxTextRandom(boxSize, "Matrix power -3");
    	aMatrix.println("aMatrix");
    	bMatrix = aMatrix.power(-1.0/3); bMatrix.println("aMatrix^(⁻1/3)");
    	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) bMatrix.power(-3).println("bMatrix^(-3)");

    	Complex.printBoxTextRandom(boxSize, "Matrix power 4");
    	aMatrix.println("aMatrix");
    	bMatrix = aMatrix.power(1.0/4); bMatrix.println("aMatrix^(1/4)");
    	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) bMatrix.power(4).println("bMatrix^(4)");

    	Complex.printBoxTextRandom(boxSize, "Matrix power 3-2i");
    	aMatrix.println("aMatrix");
    	bMatrix = aMatrix.power(new Complex(3,-2).inverse()); bMatrix.println("aMatrix^(1/(3-2i))");
    	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) bMatrix.power((new Complex(3,-2))).println("bMatrix^(3-2i)");
 
       	/**/

    	Complex.printBoxTextRandom(boxSize, "(2) Matrix power 3+0i");
    	aMatrix.println("aMatrix");
    	bMatrix = aMatrix.power(new Complex(3,0)); bMatrix.println("aMatrix^(3+0i)");
       	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) {
       		Complex cExp = new Complex(3,0);
       		cExp = cExp.inverse();
       		bMatrix.power(cExp).println("bMatrix^(1/(3+0i))");
       	}

       	DMatrix = new Diagfactor(aMatrix);
       	if (DMatrix.isDiagonalizable()) {
       		dMatrix = DMatrix.D().power(new Complex(3,0));
       		dMatrix = DMatrix.P().times(dMatrix.times(DMatrix.P().inverse()));
       		dMatrix.println("aMatrix^(3+0i) D");
       		//DMatrix.D().println("DMatrix.D()");
       		//DMatrix.P().println("DMatrix.P()");
       	}
       	
       	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) { // bMatrix.power((new Complex(3,0)).inverse()).println("bMatrix^(1/(3+0i))");
           	DMatrix = new Diagfactor(bMatrix);
           	if (DMatrix.isDiagonalizable()) {
           		dMatrix = DMatrix.D().power((new Complex(3,0)).inverse());
           		dMatrix = DMatrix.P().times(dMatrix.times(DMatrix.P().inverse()));
           		dMatrix.println("bMatrix^(1/(3+0i)) D");
           		//DMatrix.D().println("DMatrix.D()");
           		//DMatrix.P().println("DMatrix.P()");
           	}
       	}

       	Complex.printBoxTextRandom(boxSize, "(3) Matrix power 3-2i");
    	aMatrix.println("aMatrix");
    	bMatrix = aMatrix.power(new Complex(3,-2)); bMatrix.println("aMatrix^(3-2i)");
    	//bMatrix = aMatrix.powerCOPIA(new Complex(3,-2)); bMatrix.println("aMatrix^(3-2i) COPIA");
       	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) { 
       		bMatrix.power((new Complex(3,0)).inverse()).println("bMatrix^(1/(3-2i))");
       		//bMatrix.powerCOPIA((new Complex(3,0)).inverse()).println("bMatrix^(1/(3+0i)) COPIA");
           	DMatrix = new Diagfactor(bMatrix);
           	if (DMatrix.isDiagonalizable()) {
           		dMatrix = DMatrix.D().power((new Complex(3,-2)).inverse());
           		dMatrix = DMatrix.P().times(dMatrix.times(DMatrix.P().inverse()));
           		dMatrix.println("bMatrix^(1/(3-2i)) D");
           		DMatrix.D().println("DMatrix.D()");
           		DMatrix.P().println("DMatrix.P()");
           		DMatrix.P().times(DMatrix.D().times(DMatrix.P().inverse())).println("P·DP⁻¹");
           	}
       	}
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
    	Complex.exact(false);
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotOFF();
    	Complex.digits(10000000);
    	
    	Complex.showPrecision();
    	Complex.printFormatStatus();

    	Complex.printBoxTitleRandom(boxSize, "COMPLEX MATRIX TAYLOR'S TRIGONOMETRICS TEST");
    	System.out.println();

    	/**/
    	aMatrix = new MatrixComplex("32.0");
       	doPower(aMatrix); 

    	aMatrix = new MatrixComplex("1.0, 0.0; 0.0, 1.0");
       	doPower(aMatrix); 

    	aMatrix = new MatrixComplex("1.0, 0.0; 0.0, 1.0").times(Complex.DOSPI);
       	doPower(aMatrix); 

    	aMatrix = new MatrixComplex("1.0, 1.0; 1.0, 1.0").times(Complex.DOSPI);
       	doPower(aMatrix); 

       	aMatrix = new MatrixComplex("32.0, 0.0; 0.0, 32.0");
       	doPower(aMatrix); 

      	aMatrix = new MatrixComplex("32.0, 12.0; 1.0, 32.0");
       	doPower(aMatrix); 

       	aMatrix = new MatrixComplex("32.0,25.0,30.0;13.0,20.0,-21.0;-2.0,-1.0,31.0");
       	doPower(aMatrix); 

    	aMatrix = new MatrixComplex("132.0,25.0,30.0;13.0,120.0,-21.0;-2.0,-1.0,131.0");
       	doPower(aMatrix);
       	/**/
    	
    	aMatrix = new MatrixComplex("3,-2;5,1");
        doPower(aMatrix);
       	
    }
}
