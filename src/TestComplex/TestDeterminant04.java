package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.*;

public class TestDeterminant04 {

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
		int limSup = 17;
  
       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	fMatrix = new MatrixComplex(""
    			+ "1.000E+00 , 0.000E+00;"
    			+ "-7.071E-01 , 7.071E-01" );

        showResults(fMatrix);    		
	}

}
