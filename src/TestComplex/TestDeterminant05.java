package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.*;

public class TestDeterminant05 {

	private static void showResults(MatrixComplex fMatrix) {
		int dim = (int)Math.sqrt(fMatrix.dim());
		boolean setFormat = (dim > 8) ? true : false; 
	   	int boxSize = 65;

       	System.out.println("");
       	System.out.println(Complex.boxTitleRandom(boxSize, "DETERMINANT TEST"));
       	fMatrix.println(Complex.boxTextRandom(boxSize, "Matrix Dimension: " + dim + "x" + dim));
    	System.out.println("Wolfram: Det["+fMatrix.toWolfram()+"]");
		if (setFormat) {
			Complex.storeFormatStatus();
	    	Complex.setFixedOFF();
	       	Complex.setScientificON(8);
		}
       	Chronometer gaussChrono = new Chronometer();
		System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString());
		gaussChrono.stop();
    	System.out.println("El cálculo del determinante ha llevado " + gaussChrono.toString());
    	if (fMatrix.dim() < 800) {
           	Chronometer adjChrono = new Chronometer();
    	   	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString());
    	   	adjChrono.stop();
        	System.out.println("El cálculo del determinante ha llevado " + adjChrono.toString());
    	}
		if (setFormat) {
			Complex.restoreFormatStatus();
		}
 	}

	public static void main(String[] args) {
		MatrixComplex fMatrix;
		int limSup = 12;
  
       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	for (int i = 2; i <= limSup; ++i) {
    		fMatrix = new MatrixComplex(i);
         	fMatrix.initMatrixRandomInteger(10);
        	showResults(fMatrix);
    	}

    	for (int i = 2; i <= limSup; ++i) {
    		fMatrix = new MatrixComplex(i);
    		fMatrix.initMatrixRandomRec(10);
        	showResults(fMatrix);
    	}
	}

}
