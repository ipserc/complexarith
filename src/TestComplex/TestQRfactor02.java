package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.*;

public class TestQRfactor02 {

	public static void showResults(QRfactor cMatrix, String method) {
    	MatrixComplex rMatrix;
    	MatrixComplex qMatrix;
    	Complex det = new Complex();
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "QR FACTORIZATION TEST METHOD:" + method));   	
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
	  	//fMatrix = new QRfactor("-1,4,-3,-2;4,-1,-3,2;-3,3,1,4;-2,2,4,1");
	  	fMatrix = new QRfactor(""
	  			+ "  12, -51,   4;"
	  			+ "   6, 167, -68;"
	  			+ "  -4,  24, -41");
	  	    	
    	fMatrix.qrHouseholder();
    	showResults(fMatrix, "qrHouseholder");
    	
    	fMatrix.qrGramSchmidt();
    	showResults(fMatrix, "qrGramSchmidt");
    	
    	fMatrix.qrGramSchmidtM();;
    	showResults(fMatrix, "qrGramSchmidtM");
    	
	}
}
