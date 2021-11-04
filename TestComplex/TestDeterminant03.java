package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;

public class TestDeterminant03 {
	
	private static void showResults(MatrixComplex fMatrix) {
		int dim = (int)Math.sqrt(fMatrix.dim());
		boolean setFormatOff = (dim < 8) ? false : true; 
	   	int boxSize = 65;

       	System.out.println("");
       	System.out.println(Complex.boxTitleRandom(boxSize, "DETERMINANT TEST"));
       	fMatrix.println(Complex.boxTextRandom(boxSize, "Matrix Dimension: " + dim + "x" + dim));
		if (setFormatOff) {
			Complex.storeFormatStatus();
	       	Complex.setFormatOFF();
	    	Complex.setFixedOFF();
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
		if (setFormatOff) {
			Complex.restoreFormatStatus();
		}
 	}

	public static void main(String[] args) {
		MatrixComplex fMatrix;
		
       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	fMatrix = new MatrixComplex(2);
		fMatrix.initMatrixRandomInteger(10);
    	showResults(fMatrix);

		fMatrix = new MatrixComplex(5);
       	fMatrix.initMatrixRandomInteger(10);
    	showResults(fMatrix);
    	
		fMatrix = new MatrixComplex(7);
       	fMatrix.initMatrixRandomInteger(10);
    	showResults(fMatrix);

		fMatrix = new MatrixComplex(10);
       	fMatrix.initMatrixRandomInteger(10);
    	showResults(fMatrix);

		fMatrix = new MatrixComplex(11);
       	fMatrix.initMatrixRandomInteger(10);
    	showResults(fMatrix);

		fMatrix = new MatrixComplex(12);
       	fMatrix.initMatrixRandomInteger(10);
    	showResults(fMatrix);
    	
    	/* ------------------------------------------- */
    	
		fMatrix = new MatrixComplex(2);
       	fMatrix.initMatrixRandomRec(10);
    	showResults(fMatrix);

		fMatrix = new MatrixComplex(5);
       	fMatrix.initMatrixRandomRec(10);
    	showResults(fMatrix);
    	
		fMatrix = new MatrixComplex(7);
       	fMatrix.initMatrixRandomRec(10);
    	showResults(fMatrix);

		fMatrix = new MatrixComplex(10);
       	fMatrix.initMatrixRandomRec(10);
    	showResults(fMatrix);

		fMatrix = new MatrixComplex(11);
       	fMatrix.initMatrixRandomRec(10);
    	showResults(fMatrix);
	}

}
