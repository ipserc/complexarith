package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.*;

public class TestQRfactor01 {

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
    	MatrixComplex aMatrix;
    	QRfactor fMatrix;
		int boxSize = 65;
 
       	Complex.setFormatON();
    	Complex.setFixedON(3);

		System.out.println(Complex.boxTitleRandom(boxSize, " - - - QR Householder - - -"));
		
		aMatrix = new MatrixComplex(""
				+ "12,-51,4;"
				+ "6,167,-68;"
				+ "-4,24,-41;"
				+ "-3,2,1");
    	fMatrix = new QRfactor(aMatrix);
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(aMatrix.transpose());
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);

    	fMatrix = new QRfactor(""
    			+ "12,-51,  4;"
    			+ " 6,167,-68;"
    			+ "-4, 24,-41");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(""
    			+ "12,-51,  4;"
    			+ " 6,167,-68");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(""
    			+ "12, -51,  4,-13;"
    			+ " 6,-167,-68, 23;"
    			+ "-4,  24,-41, -2;"
    			+ "-1,   1,  0, 45");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(""
    			+ "12, -51,  4,-13;"
    			+ " 6,-167,-68, 23;"
    			+ "-4,  24,-41, -2;"
    			+ "-1,   1,  0, 45;"
    			+ " 2,   0,  3,  7");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(""
    			+ " 0, 1,-1;"
    			+ " 1, 1, 0;"
    			+ "-1, 0, 1");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(""
    			+ " 2, 3, 0;"
    			+ "-1, 3,-2;"
    			+ " 5,-2, 1");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(""
    			+ "3,-1, 1;"
    			+ "0, 2, 0;"
    			+ "1,-1, 3");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(""
    			+ "  2, 3i,  0;"
    			+ "-1i, 3,  -2;"
    			+ "  5, -2, 1i");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(""
    			+ "    2, -i, -1, -2;"
    			+ "-1+3i, -3, -1,  0;"
    			+ "    i,  2, -3,  1;"
    			+ "  -2i,  0, -3,  4");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(""
    			+ "    2, -1-i,  -i,  -2;"
    			+ "-1+3i,   -3,  -1,   0;"
    			+ " 1-5i,    2, -3i, 1+i;"
    			+ "    i,    2,  -3,   1;"
    			+ "  -2i,    0,  -3,   4");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(5);fMatrix.initMatrixRandomRecInt(9);
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(""
    			+ " 1,    0, 1, 1,  3;"
    			+ " 1,   -2, 3, 2, -4;"
    			+ " 1, -0.7, 1, 5,  2;"
    			+ " 2,    3, 0, 4, -3");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(4);fMatrix.initMatrixRandomRecInt(9);
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	//fMatrix = new QRfactor(4);fMatrix.initMatrixRandomInteger(9);
		fMatrix = new QRfactor("1,2,3,0;1,2,0,0;1,0,0,1");
		fMatrix.qrGramSchmidt();
    	showResults(fMatrix);
    	
		fMatrix = new QRfactor("1,1,1;2,2,0;3,0,0;0,0,1");
		fMatrix.qrGramSchmidtFull();
    	showResults(fMatrix);
    	
		fMatrix = new QRfactor(4,6);fMatrix.initMatrixRandomInteger(9);
		fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(6,4);fMatrix.initMatrixRandomInteger(9);
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
		
    	fMatrix = new QRfactor(6,6);fMatrix.initMatrixRandomInteger(9);
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
		
    	fMatrix = new QRfactor(3,3);fMatrix.initMatrixRandomRecInt(9);
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
		
    	fMatrix = new QRfactor(""
    			+ "  1-i,   1,  i;"
    			+"     1, -5i,  1;"
    			+ " 3+2i,   1,  1");
    	fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
		System.out.println(Complex.boxTitleRandom(boxSize, " - - - QR Householder - - -"));   	
		fMatrix = new QRfactor(3,3);fMatrix.initMatrixRandomRec(9);
		fMatrix.qrHouseholder();
    	showResults(fMatrix);

    	fMatrix.qrGramSchmidt();
		System.out.println(Complex.boxTitleRandom(boxSize, "- - - QR Gram-Schmidt - - -"));
    	showResults(fMatrix);

    	fMatrix.qrGramSchmidtMFull();
		System.out.println(Complex.boxTitleRandom(boxSize, "- - - QR Gram-Schmidt M Full - - -"));   	
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(	" 4.5212e+00, 1.1226e+00, 3.5451e+00, 7.3891e+00;"
    						+ 	"-2.9500e+00, 4.6084e-01,-4.7104e+00,-6.2082e+00;"
    						+ 	" 0,          8.6139e-02, 4.5200e+00, 1.1976e+00;"
    						+ 	" 0,          0,         -1.6270e+00, 4.9795e-01");
		fMatrix.qrHouseholder();
    	showResults(fMatrix);
    	
    	fMatrix = new QRfactor(35);
    	fMatrix.initMatrixRandomInteger(9);
    	/*CHRONO*/ Chronometer QRChrono = new Chronometer();
		fMatrix.qrHouseholder();
		/*CHRONO*/ QRChrono.stop();
    	showResults(fMatrix);
		/*CHRONO*/ System.out.println("Computing Time QR:" + QRChrono.toString());
   	
	}
}
