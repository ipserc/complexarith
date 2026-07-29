package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.*;

public class TestQRfactor03 {

	public static void showResults(QRfactor cMatrix) {
    	MatrixComplex rMatrix;
    	MatrixComplex qMatrix;
    	Complex det = new Complex();
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "QR FACTORIZATION TEST"));   	
    	cMatrix.println("fMatrix");
    	System.out.println("MAXIMA :"+cMatrix.toMaxima_qr());
    	System.out.println("OCTAVE :"+cMatrix.toOctave_qr());
    	System.out.println("WOLFRAM:"+cMatrix.toWolfram_QRdecomposition());
    	if (cMatrix.factorized()) {
    		System.out.println(Complex.boxTextRandom(boxSize, "QR Factors"));   	
	    	rMatrix = cMatrix.R();
	    	rMatrix.println("R:rMatrix");
	    	qMatrix = cMatrix.Q();
	    	qMatrix.println("Q:qMatrix");
	    	cMatrix.Q().times(cMatrix.R()).println("Q·R");
	    	if (qMatrix.complexMatrix.length == qMatrix.complexMatrix[0].length) {
	    		System.out.println(Complex.boxTextRandom(boxSize, "QR Operations"));   	
	    		det = qMatrix.determinant();
	        	System.out.println("Determinant(Q)=" + det);
	        	System.out.println("|Determinant(Q)|=" + det.mod());        	
	        	qMatrix.inverse().println("Invert(Q)");
	    	}
    	} else System.out.println("Unable to factorize the matrix");
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

    	QRfactor fMatrix;
		int boxSize = 65;
 
       	Complex.setFormatON();
    	Complex.setFixedON(3);

		System.out.println(Complex.boxTitleRandom(boxSize, " - - - QR Householder - - -"));   	
    	fMatrix = new QRfactor(""
    			+ "  i ,  i ,  i,  1 ;"
    			+ " -i , -i , -1,  1;"
    			+ "  i ,  1 , -i,  1");
    	
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix.qrGramSchmidt();
    	showResults(fMatrix);
    	
    	fMatrix.qrGramSchmidtFull();
    	showResults(fMatrix);
    	
	}
}
