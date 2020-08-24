package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestDeterminant4 {

	private static String strDuration(long duration) {
		int ms, s, m, h, d;
		double dec;
		double time = duration * 1.0;

		time = (time / 1000.0);
		dec = time % 1;
		time = time - dec;
		ms = (int)(dec * 1000);

		time = (time / 60.0);
		dec = time % 1;
		time = time - dec;
		s = (int)(dec * 60);

		time = (time / 60.0);
		dec = time % 1;
		time = time - dec;
		m = (int)(dec * 60);

		time = (time / 24.0);
		dec = time % 1;
		time = time - dec;
		h = (int)(dec * 24);
		
		d = (int)time;
		
		return (String.format("%d d - %02d:%02d:%02d.%03d", d, h, m, s, ms));
	}
	
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
    	System.out.println("El cálculo del determinante ha llevado " + strDuration(duration));
    	if (fMatrix.dim() < 800) {
           	startTime = System.currentTimeMillis();
    	   	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString());
    	   	duration = System.currentTimeMillis() - startTime;
        	System.out.println("El cálculo del determinante ha llevado " + strDuration(duration));
    	}
       	Complex.setFormatON();
    	Complex.setFixedON(3);
       	System.out.println("==================================================================================");
	}

	public static void main(String[] args) {
		MatrixComplex fMatrix;
		int limSup = 17;
  
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
