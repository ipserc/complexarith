package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.*;

public class TestQRfactor {

	public static void showResults(QRfactor cMatrix) {
    	MatrixComplex rMatrix;
    	MatrixComplex qMatrix;
    	Complex det = new Complex();
    	
    	cMatrix.println("fMatrix");
    	if (cMatrix.factorized()) {
	    	rMatrix = cMatrix.R();
	    	rMatrix.println("R:rMatrix");
	    	qMatrix = cMatrix.Q();
	    	qMatrix.println("Q:qMatrix");
	    	cMatrix.Q().times(cMatrix.R()).println("Q·R");
	    	if (qMatrix.complexMatrix.length == qMatrix.complexMatrix[0].length) {
	    		det = qMatrix.determinant();
	        	System.out.println("Determinant(Q)=" + det);
	        	System.out.println("|Determinant(Q)|=" + det.mod());        	
	        	qMatrix.inverse().println("Invert(Q)");
	    	}
    	} else System.out.println("Unable to factorize the matrix");
    	System.out.println("-----------------------------------------------------\n");
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix;
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;
    	QRfactor fMatrix;
    	MatrixComplex gMatrix;
    	MatrixComplex hMatrix;
    	MatrixComplex iMatrix;
		Complex result = new Complex();
 
       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	//fMatrix = new QRfactor("12,-51,4;6,167,-68;-4,24,-41;-3,2,1");
    	//fMatrix = new QRfactor("12,-51,4;6,167,-68;-4,24,-41");
    	//fMatrix = new QRfactor("12,-51,4;6,167,-68");
    	//fMatrix = new QRfactor("12,-51,4,-13;6,-167,-68,23;-4,24,-41,-2;-1,1,0,45");
    	//fMatrix = new QRfactor("12,-51,4,-13;6,-167,-68,23;-4,24,-41,-2;-1,1,0,45;2,0,3,7");
    	//fMatrix = new QRfactor("0,1,-1;1,1,0;-1,0,1");
    	//fMatrix = new QRfactor("2,3,0;-1,3,-2;5,-2,1");
    	//fMatrix = new QRfactor("3,-1,1;0,2,0;1,-1,3");
    	//fMatrix = new QRfactor("2,3i,0;-1i,3,-2;5,-2,1i");
    	//fMatrix = new QRfactor("2,-i,-1,-2;-1+3i,-3,-1,0;i,2,-3,1;-2i,0,-3,4");
    	//fMatrix = new QRfactor("2,-1-i,-i,-2;-1+3i,-3,-1,0;1-5i,2,-3i,1+i;i,2,-3,1;-2i,0,-3,4");
    	//fMatrix = new QRfactor(5);fMatrix.initMatrixRandomRecInt(9);
    	//fMatrix = new QRfactor("1,0,1,1,3;1,-2,3,2,-4;1,-0.7,1,5,2;2,3,0,4,-3");
    	//fMatrix = new QRfactor(4);fMatrix.initMatrixRandomRecInt(9);
    	//fMatrix = new QRfactor(4);fMatrix.initMatrixRandomInteger(9);
		//fMatrix = new QRfactor("1,2,3,0;1,2,0,0;1,0,0,1");
		//fMatrix = new QRfactor("1,1,1;2,2,0;3,0,0;0,0,1");
    	//fMatrix = new QRfactor(4,6);fMatrix.initMatrixRandomInteger(9);
    	//fMatrix = new QRfactor(6,4);fMatrix.initMatrixRandomInteger(9);
		//fMatrix = new QRfactor(6,6);fMatrix.initMatrixRandomInteger(9);
		//fMatrix = new QRfactor(3,3);fMatrix.initMatrixRandomRecInt(9);
		//fMatrix = new QRfactor("1-i,1,i;1,-5i,1;3+2i,1,1");
		fMatrix = new QRfactor(3,3);fMatrix.initMatrixRandomRec(9);
		fMatrix.qrHouseholder();
    	System.out.println("Householder (QR)");
    	showResults(fMatrix);
    	fMatrix.qrGramSchmidt();
    	System.out.println("QR Gram-Schmidt");
    	showResults(fMatrix);
    	fMatrix.qrGramSchmidtMFull();
    	System.out.println("QR Gram-Schmidt M Full");
    	showResults(fMatrix);
	}
}
