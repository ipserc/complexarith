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

public class TestTaylorSeries06 {
	
	static int boxSize = 65;

	private static void doPower(MatrixComplex aMatrix, Complex cExp) {
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;
    	Diagfactor BMatrix, DMatrix;

    	Complex.printBoxTextRandom(boxSize, "power Matrix");
    	aMatrix.println("aMatrix:");
    	
       	DMatrix = new Diagfactor(aMatrix);
       	if (DMatrix.isDiagonalizable()) {
       		bMatrix = DMatrix.D().power(cExp);
       		bMatrix = DMatrix.P().times(bMatrix.times(DMatrix.P().inverse()));
       		bMatrix.println("[1] Diagonal aMatrix^"+cExp.toString());
       		//DMatrix.D().println("DMatrix.D()");
       		//DMatrix.P().println("DMatrix.P()");
       	}
       	{
        	bMatrix = aMatrix.power(cExp); 
        	bMatrix.println("[1] Series power aMatrix^"+cExp.toString());
       	}
       	
    	bMatrix.println("bMatrix:");
       	BMatrix = new Diagfactor(bMatrix);
       	Complex cInvExp = cExp.inverse();
       	if (BMatrix.isDiagonalizable()) {
       		cMatrix = BMatrix.D().power(cInvExp);
       		cMatrix = BMatrix.P().times(cMatrix.times(BMatrix.P().inverse()));
       		cMatrix.println("[2] Diagonal bMatrix^"+cInvExp.toString());
       		
       	}
       	{
        	cMatrix = bMatrix.power(cInvExp); 
        	cMatrix.println("[2] Series power bMatrix^"+cInvExp.toString());
       	}

	}
	
    public static void main(String[] args) {
    	Complex cExp;
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
    	MatrixComplex.doPlotOFF();
    	Complex.digits(10000000);
    	Complex.setScientificON(4);
    	
    	Complex.showPrecision();
    	Complex.printFormatStatus();

    	Complex.printBoxTitleRandom(boxSize, "COMPLEX MATRIX FUNCTION'S TEST");
    	System.out.println();

    	/**/
    	aMatrix = new MatrixComplex("32.0");
    	cExp = new Complex(3,0);
       	doPower(aMatrix, cExp); 

    	aMatrix = new MatrixComplex("32.0i");
    	cExp = new Complex(3,0);
       	doPower(aMatrix, cExp); 
       	Complex base = new Complex(0,32);
       	base.power(cExp).power(cExp.inverse()).println("base.power(cExp).power(cExp.inverse())");

    	aMatrix = new MatrixComplex("32.0i");
    	cExp = new Complex(0,-1);
       	doPower(aMatrix, cExp);
       	
    	aMatrix = new MatrixComplex("1.0, 0.0; 0.0, 1.0");
    	cExp = new Complex(0,-1);
       	doPower(aMatrix, cExp); 

      	aMatrix = new MatrixComplex("32.0, 0.0; 0.0, 32.0");
    	cExp = new Complex(0,-1);
       	doPower(aMatrix, cExp); 

      	aMatrix = new MatrixComplex("12.0, -27.0; 15.0, 77.0");
    	cExp = new Complex(5,0);
       	doPower(aMatrix, cExp); 

      	aMatrix = new MatrixComplex("11.2,1.3-10.1i;1.3+10.1i,12.4");
    	cExp = new Complex(5,0);
       	doPower(aMatrix, cExp); 

       	/*
       	aMatrix = new MatrixComplex("12.0,5.0,7.0;-17.0,7.0,-1.0;-8.0,-14.0,-4.0");
    	cExp = new Complex(0,-1);
       	doPower(aMatrix, cExp);
       	*/
       	
       	aMatrix = new MatrixComplex("36.0,-18.0;-18.0,24.0");
    	cExp = new Complex(4, 0);
       	doPower(aMatrix, cExp);

       	/** /
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
       	/** /
    	
    	aMatrix = new MatrixComplex("3,-2;5,1");
        doPower(aMatrix);
       	/**/
    }
}
