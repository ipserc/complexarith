package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.factorization.*;
import com.ipserc.arith.complex.*;

public class TestSVD {
	
	public static void showResults(SVDfactor A) {
    	MatrixComplex S;
    	MatrixComplex V;
    	MatrixComplex U;
    	S = A.getS();
    	V = A.getV();
    	U = A.getU();
    	System.out.println("\n-------------- Solutions Section --------------");
    	A.println("A");
    	S.println("S");
    	U.println("U");
    	V.println("V");
    	System.out.println("\n---------------- Check Section ----------------");
    	(U.times(S)).times(V.adjoint()).println("U·S·V*");
    	//(V.times(S.transpose())).times(U.adjoint()).println("V·S*·U*");
    	System.out.println("\n------------ U Properties Section -------------");
       	U.inverse().println("U.inverse()");
    	U.adjoint().println("U.adjoint()");
       	U.times(U.adjoint()).println("U·U.adjoint()");
    	Complex Udet = U.determinant();
    	System.out.println("Det(U) = " + Udet);
    	System.out.println("|Det(U)| = " + Udet.abs());
    	System.out.println("\n------------- V Properties Section -------------");
       	V.inverse().println("V.inverse()");
       	V.adjoint().println("V.adjoint()");
       	V.times(V.adjoint()).println("V·V.adjoint()");
    	Complex Vdet = V.determinant();
    	System.out.println("Det(V) = " + Vdet);
    	System.out.println("|Det(V)| = " + Vdet.abs());    	
    	System.out.println("\n-------------- Definitions Section -------------");
    	System.out.println("                MatrixComplex Section");
    	System.out.println("A="+A.toMatrixComplex());    	
    	System.out.println("S="+S.toMatrixComplex());
    	System.out.println("U="+U.toMatrixComplex());
    	System.out.println("V="+V.toMatrixComplex());
    	System.out.println("                   Maxima Section");
    	System.out.println("A="+A.toMaxima());
    	System.out.println("S="+S.toMaxima());
    	System.out.println("U="+U.toMaxima());
    	System.out.println("V="+V.toMaxima());
    	System.out.println("           Wolfram Mathematica Section");
    	System.out.println("A="+A.toWolfram());
    	System.out.println("S="+S.toWolfram());
    	System.out.println("U="+U.toWolfram());
    	System.out.println("V="+V.toWolfram());
    	System.out.println("                Mathlab Section");
    	System.out.println("A="+A.toMathlab());
    	System.out.println("S="+S.toMathlab());
    	System.out.println("U="+U.toMathlab());
    	System.out.println("V="+V.toMathlab());
    	System.out.println("\n------------------------------------------------");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex A;
    	SVDfactor svd;
       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	// ------ m = n ------
    	//A = new SVDfactor(2); A.initMatrixRandomInteger(3);
       	//A = new SVDfactor(11); A.initMatrixRandomRecInt(9);
    	//A = new SVDfactor(" 6.0 , 0.0 , -1.0 , -4.0 , -2.0 ; 7.0 , 0.0 , -6.0 , 0.0 , -2.0 ; -4.0 , -1.0 , -1.0 , 1.0 , -7.0 ; -1.0 , 8.0 , 8.0 , 6.0 , -4.0 ; 1.0 , -4.0 , 6.0 , 7.0 , 2.0 ");    	
    	//A = new SVDfactor(5); A.initMatrixRandomRecInt(9);
    	//A = new SVDfactor("2+2i,3-i,4+3i;5,6+i,7-2i;-i,7,3-3i");
    	//A = new SVDfactor("-2,2,1;3,-2,1;5,-2,3");
    	//A = new SVDfactor("-1-i,2,1;3,4+3i,17;8,9,17i");
    	//A = new SVDfactor("1,-2;-3,2");
    	//A = new SVDfactor("-1,2,1;3,-2,1;5,-2,-3");
    	//A = new SVDfactor("-1+2i,3i,1;3+3i,-9-2i,-i;2+5i,1-7i,-5+7i");

    	// ------ m > n ------
    	//A = new SVDfactor(7,3); A.initMatrixRandomInteger(9);
       	//A = new SVDfactor(10,5); A.initMatrixRandomRecInt(9);
    	//A = new SVDfactor("-1,2,1;3,4,7;5,6,11;8,9,17"); /*****/
    	//A = new SVDfactor("-1,2,1;3,-2,1;5,-2,3;6,2,-7");
    	//A = new SVDfactor("-1,2,2;3,-2,1;5,-2,3;6,2,-7");
    	//A = new SVDfactor("0,0;0,9;3,0"); 
    	//A.complexMatrix =  A.transpose().times(A).complexMatrix.clone();
    	//A = new SVDfactor("1,3;3,9;2,1");
    	//A = new SVDfactor("-1,3;3,-9;2,1;-5,7");
    	//A = new SVDfactor("1,1;1,0;0,1");
    	//A = new SVDfactor("-1,2;3,-2;5,-2");

    	// ------ m < n ------    	
    	//A = new SVDfactor(3,7); A.initMatrixRandomInteger(9);
    	//A = new SVDfactor(5,10); A.initMatrixRandomRecInt(9);
    	//A = new SVDfactor(" -5.0 , 3.0 , -3.0 , -3.0 , 4.0 ; -1.0 , -6.0 , 0.0 , 0.0 , -7.0 ; -6.0 , -6.0 , -8.0 , -8.0 , 5.0 ");
    	//A = new SVDfactor(" -4.0 , 3.0 , -6.0 , 5.0 , 0.0 ; 0.0 , 5.0 , -1.0 , 6.0 , 6.0 ; -5.0 , -4.0 , -2.0 , 3.0 , 5.0 ");
    	//A = new SVDfactor("2+2i,3-i,4+3i;5,6+i,7-2i");
    	//A = new SVDfactor("3,2,2;2,3,-2");
       	//A = new SVDfactor("2+2i,3-i,4+3i;5,6+i,7-2i");
    	//A = new SVDfactor("1,1,0;1,0,1");
    	//A = new SVDfactor("1,1,0,-1;1,0,1,2");
    	//A = new SVDfactor("3,2i,2;2i,3,-2");
    	//A = new SVDfactor("-1,2,1;3,-2,1");
/**/ 
    	svd = new SVDfactor(
  			  " 2.925-0.533i, 0.729+2.732i, 7.443+6.499i,-4.267+3.362i;"
  			+ " 0.208-2.559i,-2.815+3.671i,-7.676+0.295i,-1.295-2.829i;"
  			+ "-0.525+2.311i, 2.814+5.875i,-7.154-1.795i,-4.754+2.267i;"
  			+ "-3.146+4.064i,-8.656+6.937i, 7.892-3.179i,-8.197+8.063i");
    	showResults(svd);

    	/**/

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
    	
    	svd = new SVDfactor("-1+2i,3i,1;3+3i,-9-2i,-i;2+5i,1-7i,-5+7i");
    	showResults(svd);
    	
    	svd = new SVDfactor("-1+2i,3i;3+3i,-9-2i;2+5i,1-7i");
    	showResults(svd);

    	svd = new SVDfactor("-1+2i,3i,1,3+3i;-9-2i,2+5i,1-7i,-5+7i");
    	showResults(svd);
    	
		svd = new SVDfactor(
				" 1, 0, 0;"+
				" 0, 1, 0;"+
				" 1,-2, 2");
    	showResults(svd);

    	A = new MatrixComplex(11,11); A.initMatrixRandomRec(1);
    	svd = new SVDfactor(A);
    	showResults(svd);

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
    	
/* 	
    	A = new MatrixComplex(17,17); Amatrix.initMatrixRandomRec(1);
    	A = new SVDfactor(A);
    	//Amatrix.println("Amatrix");
    	A.SVDfactorize();
    	showResults(A);
    	A.charactPoly().println();

    	Amatrix = new MatrixComplex(17,27); Amatrix.initMatrixRandomRec(1);
    	A = new SVDfactor(Amatrix);
    	A.setFixFloatON(3);
    	A.SVDfactorize();
    	showResults(A);
*/
	}
}
