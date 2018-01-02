package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestDeterminant3 {
	
	private static void showResults(MatrixComplex fMatrix) {
		long startTime, duration;
		int dim = (int)Math.sqrt(fMatrix.dim());
		boolean formatOff = (dim < 8) ? false : true; 

		System.out.println("");
       	System.out.println("==================================================================================");
       	fMatrix.println("------------ Matrix Dimension: " + dim + "x" + dim + " ------------");
		if (formatOff) {
	       	Complex.setFormatOFF();
	    	Complex.setFixedOFF();
		}
       	startTime = System.currentTimeMillis();
		System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString());
      	duration = System.currentTimeMillis() - startTime;
    	System.out.println("El c�lculo del determinante ha llevado " + duration + "ms");
    	if (fMatrix.dim() < 800) {
           	startTime = System.currentTimeMillis();
    	   	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString());
    	   	duration = System.currentTimeMillis() - startTime;
        	System.out.println("El c�lculo del determinante ha llevado " + duration + "ms");
    	}
       	Complex.setFormatON();
    	Complex.setFixedON(3);
       	System.out.println("==================================================================================");
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
