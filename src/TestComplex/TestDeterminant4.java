package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestDeterminant4 {

	private static void showResults(MatrixComplex fMatrix) {
		long startTime, duration;
		int dim = (int)Math.sqrt(fMatrix.dim());
		boolean setFormatOff = (dim < 8) ? false : true; 

		System.out.println("");
       	System.out.println("==================================================================================");
       	fMatrix.println("------------ Matrix Dimension: " + dim + "x" + dim + " ------------");
		if (setFormatOff) {
	       	Complex.setFormatOFF();
	    	Complex.setFixedOFF();
		}
       	startTime = System.currentTimeMillis();
		System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString());
      	duration = System.currentTimeMillis() - startTime;
    	System.out.println("El cálculo del determinante ha llevado " + duration + "ms");
    	if (fMatrix.dim() < 800) {
           	startTime = System.currentTimeMillis();
    	   	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString());
    	   	duration = System.currentTimeMillis() - startTime;
        	System.out.println("El cálculo del determinante ha llevado " + duration + "ms");
    	}
       	Complex.setFormatON();
    	Complex.setFixedON(3);
       	System.out.println("==================================================================================");
	}

	public static void main(String[] args) {
		MatrixComplex fMatrix;
		int limSup = 13;
  
       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	for (int i = 2; i < limSup; ++i) {
    		fMatrix = new MatrixComplex(i);
         	fMatrix.initMatrixRandomInteger(10);
        	showResults(fMatrix);    		
    	}

    	for (int i = 2; i < limSup; ++i) {
    		fMatrix = new MatrixComplex(i);
    		fMatrix.initMatrixRandomRec(10);;
        	showResults(fMatrix);    		
    	}
	}

}
