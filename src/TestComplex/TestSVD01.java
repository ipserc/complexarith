package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.factorization.*;
import com.ipserc.arith.complex.*;

public class TestSVD01 {
	
	public static void showResults(SVDfactor A) {
    	MatrixComplex S;
    	MatrixComplex V;
    	MatrixComplex U;
    	MatrixComplex USV;
		int boxSize = 65;
		boolean UVproperties = true;
		
		if (!A.factorized()) return;
		
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
    	U.println("U");
		System.out.println("U:" +U.toMatrixComplex());
    	V.println("V");
		System.out.println("V:" +V.toMatrixComplex());
		System.out.println(Complex.boxTitleRandom(boxSize, "Check Section"));
		USV = (U.times(S)).times(V.adjoint());
		USV.println("U·S·V*");
		System.out.println("Is equal: " + ((USV.equals(A)) ? "Yes" : "No")); 
    	
    	//(V.times(S.transpose())).times(U.adjoint()).println("V·S*·U*");

		if (UVproperties) {
			System.out.println(Complex.boxTitleRandom(boxSize, "U Properties Section"));
	       	if (U.isSquare()) U.inverse().println("U.inverse()");
	    	U.adjoint().println("U.adjoint()");
	       	U.inverse().times(U.adjoint()).println("Unitary Matrix: U.inverse()·U.adjoint()");
	       	if (U.isSquare()) {
		    	Complex Udet = U.determinant();
		    	System.out.println("Det(U) = " + Udet);
		    	System.out.println("|Det(U)| = " + Udet.abs());
	       	}
	    	
			System.out.println(Complex.boxTitleRandom(boxSize, "V Properties Section"));
	       	if (V.isSquare()) V.inverse().println("V.inverse()");
	       	V.adjoint().println("V.adjoint()");
	       	V.adjoint().times(V.inverse()).println("Unitary Matrix: V.adjoint()·V.inverse())");
	       	if (V.isSquare()) {
	       		Complex Vdet = V.determinant();
	        	System.out.println("Det(V) = " + Vdet);
	        	System.out.println("|Det(V)| = " + Vdet.abs());    	
	       	}
		}
		
		System.out.println(Complex.boxTitleRandom(boxSize, "Definitions Section"));
	   	System.out.println("MCMPLX :"+A.toMatrixComplex());
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
    	MatrixComplex A;
    	SVDfactor svd;
       	Complex.setFormatON();
    	Complex.setFixedON(3);
    	//Complex.Exact = false;

    	// ------ m = n ------
    	//A = new MatrixComplex(3); A.initMatrixRandomInteger(3); svd = new SVDfactor(A); 
       	//A = new MatrixComplex(11); A.initMatrixRandomRecInt(9); svd = new SVDfactor(A.divides(10)); 
    	svd = new SVDfactor(" 6.0 , 0.0 , -1.0 , -4.0 , -2.0 ; 7.0 , 0.0 , -6.0 , 0.0 , -2.0 ; -4.0 , -1.0 , -1.0 , 1.0 , -7.0 ; -1.0 , 8.0 , 8.0 , 6.0 , -4.0 ; 1.0 , -4.0 , 6.0 , 7.0 , 2.0 ");    	
    	//svd = new SVDfactor(5); A.initMatrixRandomRecInt(9);
    	//svd = new SVDfactor("2+2i,3-i,4+3i;5,6+i,7-2i;-i,7,3-3i");
    	//svd = new SVDfactor("-2,2,1;3,-2,1;5,-2,3");
    	//svd = new SVDfactor("-1-i,2,1;3,4+3i,17;8,9,17i");
    	//svd = new SVDfactor("1,-2;-3,2");
    	//svd = new SVDfactor("-1,2,1;3,-2,1;5,-2,-3");
    	//svd = new SVDfactor("-1+2i,3i,1;3+3i,-9-2i,-i;2+5i,1-7i,-5+7i");

    	// ------ m > n ------
    	//svd = new SVDfactor(7,3); A.initMatrixRandomInteger(9);
       	//svd = new SVDfactor(10,5); A.initMatrixRandomRecInt(9);
    	//svd = new SVDfactor("-1,2,1;3,4,7;5,6,11;8,9,17"); /*****/
    	//svd = new SVDfactor("-1,2,1;3,-2,1;5,-2,3;6,2,-7");
    	//svd = new SVDfactor("-1,2,2;3,-2,1;5,-2,3;6,2,-7");
    	//svd = new SVDfactor("0,0;0,9;3,0"); 
    	//A.complexMatrix =  A.transpose().times(A).complexMatrix.clone();
    	//svd = new SVDfactor("1,3;3,9;2,1");
    	//svd = new SVDfactor("-1,3;3,-9;2,1;-5,7");
    	//svd = new SVDfactor("1,1;1,0;0,1");
    	//svd = new SVDfactor("-1,2;3,-2;5,-2");

    	// ------ m < n ------    	
    	//svd = new SVDfactor(3,7); A.initMatrixRandomInteger(9);
    	//svd = new SVDfactor(5,10); A.initMatrixRandomRecInt(9);
    	//svd = new SVDfactor(" -5.0 , 3.0 , -3.0 , -3.0 , 4.0 ; -1.0 , -6.0 , 0.0 , 0.0 , -7.0 ; -6.0 , -6.0 , -8.0 , -8.0 , 5.0 ");
    	//svd = new SVDfactor(" -4.0 , 3.0 , -6.0 , 5.0 , 0.0 ; 0.0 , 5.0 , -1.0 , 6.0 , 6.0 ; -5.0 , -4.0 , -2.0 , 3.0 , 5.0 ");
    	//svd = new SVDfactor("2+2i,3-i,4+3i;5,6+i,7-2i");
    	//svd = new SVDfactor("3,2,2;2,3,-2");
       	//svd = new SVDfactor("2+2i,3-i,4+3i;5,6+i,7-2i");
    	//svd = new SVDfactor("1,1,0;1,0,1");
    	//svd = new SVDfactor("1,1,0,-1;1,0,1,2");
    	//svd = new SVDfactor("3,2i,2;2i,3,-2");
    	//svd = new SVDfactor("-1,2,1;3,-2,1");

       	showResults(svd);
        
    	/**/ 
    	svd = new SVDfactor(
  			  " 2.925-0.533i, 0.729+2.732i, 7.443+6.499i,-4.267+3.362i;"
  			+ " 0.208-2.559i,-2.815+3.671i,-7.676+0.295i,-1.295-2.829i;"
  			+ "-0.525+2.311i, 2.814+5.875i,-7.154-1.795i,-4.754+2.267i;"
  			+ "-3.146+4.064i,-8.656+6.937i, 7.892-3.179i,-8.197+8.063i");
    	showResults(svd);

    	A = new MatrixComplex(4); 
    	A.initMatrixRandomRec(9);
    	svd = new SVDfactor(A);
    	showResults(svd);

       	svd = new SVDfactor(
       			" 6.0 , 0.0 ,-1.0 ,-4.0 ,-2.0;"+
       			" 7.0 , 0.0 ,-6.0 , 0.0 ,-2.0;"+
       			"-4.0 ,-1.0 ,-1.0 , 1.0 ,-7.0;"+
       			"-1.0 , 8.0 , 8.0 , 6.0 ,-4.0;"+
       			" 1.0 ,-4.0 , 6.0 , 7.0 , 2.0");    	
    	showResults(svd);

    	svd = new SVDfactor(
    			"-1, 2, 1, 4,-3;"+
    			" 3,-2, 1,-2,-4;"+
    			" 5,-2,-3,-1, 6");
    	showResults(svd);
    	
    	svd = new SVDfactor("-1,2;3,-2;5,-2");
    	showResults(svd);
    	
    	svd = new SVDfactor("-1,2,1;3,-2,1");
    	showResults(svd);
    	
    	//Complex.Exact = true;
    	svd = new SVDfactor("-1+2i,3i,1;3+3i,-9-2i,-i;2+5i,1-7i,-5+7i");
    	showResults(svd);
    	
    	//Complex.Exact = true;
    	svd = new SVDfactor("-1+2i,3i;3+3i,-9-2i;2+5i,1-7i");
    	showResults(svd);

    	svd = new SVDfactor("-1+2i,3i,1,3+3i;-9-2i,2+5i,1-7i,-5+7i");
    	showResults(svd);
    	
		svd = new SVDfactor(
				" 1, 0, 0;"+
				" 0, 1, 0;"+
				" 1,-2, 2");
    	showResults(svd);

    	A = new MatrixComplex(7,7); A.initMatrixRandomRec(1);
    	svd = new SVDfactor(A);
    	showResults(svd);

    	//Complex.Exact = false;
		svd = new SVDfactor(
				" 1, 1, 1;"+
				" 1, 2, 1;"+
				" 1, 1, 1");
    	showResults(svd);
    	
		svd = new SVDfactor(
				" 1, 1, 0 ,1;"+
				" 0, 0, 0, 1;"+
				" 1, 1, 0, 0");
    	showResults(svd);

		svd = new SVDfactor(
				" 1, 2, -3,-1, 2, 3;"+
				"-3,-5,  9, 3,-1,-8;"+
				" 5, 9,-15,-5, 3,14;"+
				" 2, 4, -6,-2, 3, 6" );
    	showResults(svd);
    	//Complex.Exact = true;
    	
		svd = new SVDfactor(
				"1, -3, 5, 2;"+
				"2, -5, 9, 4;"+
				"-3, 9, -15, -6;"+
				"-1, 3, -5, -2;"+
				"2, -1, 3, 3;"+
				"3, -8, 14, 6");
    	showResults(svd);
    	//Complex.Exact = false;
		svd = new SVDfactor(
				"1, -3, 5, 2;"+
				"2, -5, 9, 4;"+
				"-3, 9, -15, -6;"+
				"-1, 3, -5, -2;"+
				"2, -1, 3, 3;"+
				"3, -8, 14, 6");
    	showResults(svd);

    	//Complex.Exact = true;
		svd = new SVDfactor(
				"-2,-5,8,0,-17;"
				+ "1,3,-5,1,8;"
				+ "3,11,-19,7,1;"
				+ "1,7,-13,5,-3");
    	showResults(svd);

    	/**/

	}
}
