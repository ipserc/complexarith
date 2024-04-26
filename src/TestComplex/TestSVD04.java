package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.factorization.*;
import com.ipserc.arith.factorization.SVDfactor.SVDmethod;
import com.ipserc.arith.complex.*;
import java.util.Random;

public class TestSVD04 {
	private static Random randomNbr = new Random(System.currentTimeMillis());
	
	public static void showResults(SVDfactor A) {
    	MatrixComplex S;
    	MatrixComplex V;
    	MatrixComplex U;
    	MatrixComplex USV;
		int boxSize = 65;
		boolean UVproperties = true;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "SVD FACTORIZATION TEST"));
		System.out.println(Complex.boxTextRandom(boxSize, "EXACT:" + Complex.exact()));
		System.out.println(Complex.boxTextRandom(boxSize, "Method:" + A.getMethodName()));
		if (!A.factorized())
			System.out.println(Complex.boxTextRandom(boxSize, "F A I L ! ! ! ! ! ! !"));
    	S = A.getS();
    	V = A.getV();
    	U = A.getU();
		System.out.println(Complex.boxTitleRandom(boxSize, "Solutions Section"));
    	A.println("A");
		System.out.println("A:" +A.toMatrixComplex());
		System.out.println("A:" +A.toWolfram());
    	S.println("S");
		System.out.println("S:" +S.toMatrixComplex());
		System.out.println("S:" +S.toWolfram());
		U.println("U");
		System.out.println("U:" +U.toMatrixComplex());
		System.out.println("U:" +U.toWolfram());
    	V.println("V");
		System.out.println("V:" +V.toMatrixComplex());
		System.out.println("V:" +V.toWolfram());
		System.out.println(Complex.boxTitleRandom(boxSize, "Check Section"));
		USV = (U.times(S)).times(V.adjoint());
		USV.println("U·S·V*");
		System.out.println("Is equal: " + ((USV.equals(A)) ? "Yes" : "No")); 
    	
    	//(V.times(S.transpose())).times(U.adjoint()).println("V·S*·U*");

		if (UVproperties) {
			System.out.println(Complex.boxTitleRandom(boxSize, "U Properties Section"));
	       	if (U.isSquare()) U.inverse().println("U.inverse()");
	    	U.adjoint().println("U.adjoint()");
	       	if (U.isSquare()) {
		       	U.inverse().times(U.adjoint()).println("Unitary Matrix: U.inverse()·U.adjoint()");
		    	Complex Udet = U.determinant();
		    	System.out.println("Det(U) = " + Udet);
		    	System.out.println("|Det(U)| = " + Udet.abs());
	       	}
	    	
			System.out.println(Complex.boxTitleRandom(boxSize, "V Properties Section"));
	       	if (V.isSquare()) V.inverse().println("V.inverse()");
	       	V.adjoint().println("V.adjoint()");
	       	if (V.isSquare()) {
		       	V.adjoint().times(V.inverse()).println("Unitary Matrix: V.adjoint()·V.inverse())");
	       		Complex Vdet = V.determinant();
	        	System.out.println("Det(V) = " + Vdet);
	        	System.out.println("|Det(V)| = " + Vdet.abs());    	
	       	}
		}
		
		System.out.println(Complex.boxTitleRandom(boxSize, "Definitions Section"));
	   	System.out.println("MAXIMA :"+A.toMaxima_dgesvd(true));
	   	System.out.println("MAXIMA :"+A.toMaxima_Sigma());
    	System.out.println("OCTAVE :"+A.toOctave_svd());
    	System.out.println("WOLFRAM:"+A.toWolfram_svd());
		System.out.println(Complex.boxTextRandom(boxSize, "--- END SVD FACTORIZATION TEST ---"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	SVDfactor svd;
    	MatrixComplex matrixSVD;
       	Complex.setFormatON();
    	Complex.setFixedON(3);
    	
    	matrixSVD = new MatrixComplex(""
    			+ "-3.000,-3.000,-4.000,1.000;"
    			+ "-4.000,2.000,-5.000,5.000;"
    			+ "3.000,-3.000,-3.000,-1.000;"
    			+ "-4.000,-4.000,-3.000,-5.000");
		svd = new SVDfactor(matrixSVD);
	   	showResults(svd);
		
		matrixSVD = new MatrixComplex(""
	    		+ " 0, 0,  -4;"
	    		+ " 0, 2i,  0;"
	    		+ " 1, 0,  0");
		svd = new SVDfactor(matrixSVD);
	   	showResults(svd);
		
		matrixSVD = new MatrixComplex(""
	    		+ " 0, 0,  9;"
	    		+ " 0, 3,  0;"
	    		+ " 1, 0,  0");
		svd = new SVDfactor(matrixSVD);
	   	showResults(svd);

	   	/* */
    	for (int i = 1; i < 11; ++i) {
    		int rows = randomNbr.nextInt(7)+1;
    		int cols = randomNbr.nextInt(7)+1;
    		matrixSVD = new MatrixComplex(rows,cols);
    		matrixSVD.initMatrixRandomInteger(5);
    		
    		svd = new SVDfactor(matrixSVD);
    	   	showResults(svd);
    	}
    	/* */

    	for (int i = 1; i < 11; ++i) {
    		int rows = randomNbr.nextInt(7)+1;
    		matrixSVD = new MatrixComplex(rows);
    		matrixSVD.initMatrixRandomInteger(5);
    		
    		svd = new SVDfactor(matrixSVD);
    	   	showResults(svd);
    	}
}
}
