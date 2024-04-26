package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.factorization.*;
import com.ipserc.arith.factorization.SVDfactor.SVDmethod;
import com.ipserc.arith.complex.*;

public class TestSVD03 {
	
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
       	Complex.setFormatON();
    	Complex.setFixedON(3);
    	
    	/* * /
    	Complex.Exact = true;
    	svd = new SVDfactor(""
    			+ "-1+2i,3i,1,3+3i;"
    			+ "-9-2i,2+5i,1-7i,-5+7i");
      	showResults(svd);
		/* */

    	/* * /
      	svd = new SVDfactor(""
    			+ "-1+2i,1-3i,1,3+3i;"
    			+ "-9-2i,2+5i,1-7i,-5+7i");
    	showResults(svd);
		/* */
    	
    	/* */
    	svd = new SVDfactor(""
    			+ " 1 , 1 , 1; "
    			+ " 1 , 2 , 1; "
    			+ " 1 , 1 , 1");
    	showResults(svd);
		/* */

    	/* */
    	svd = new SVDfactor(""
    			+ " 1 , 0 , 1 , 0; "
    			+ " 0 , 1 , 0 , 1 ");
    	showResults(svd);
		/* */

    	/* */
    	svd = new SVDfactor(""
    			+ " 1 , 0 ;"
    			+ " 0 , 1; "
    			+ " 1 , 0 ;"
    			+ " 0 , 1 ");
    	showResults(svd);
		/* */

    	/* */
    	svd = new SVDfactor(""
    			+ " 1 , 0 ;"
    			+ " 1 , 0; "
    			+ " 0 , 1 ;"
    			+ " 0 , 1 ");
    	showResults(svd);
		/* */
    	
		/* */
    	svd = new SVDfactor(""
    			+ "4,-1,6;"
    			+ "2,1,6;"
    			+ "2,-1,8");
    	showResults(svd);
		/* */
		/* */
    	svd = new SVDfactor(""
    			+ " 4, 11, 14;"
    			+ " 8,  7, -2");
    	showResults(svd);
    	svd = new SVDfactor(""
    			+ " 4, 11, 14;"
    			+ " 8,  7, -2",
    			SVDmethod.IMAGINARY);
    	showResults(svd);
		/* */

    	/* */
    	svd = new SVDfactor(""
    			+ " 1 , -1 ;"
    			+ "-2 ,  2; "
    			+ " 2 , -2 ",
    			SVDmethod.IMAGINARY);
    	showResults(svd);
		/* */

	}
}
