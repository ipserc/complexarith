package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vectorcomplex.VectorComplex;
import com.ipserc.arith.factorization.*;
import com.ipserc.arith.factorization.SVDfactor.SVDmethod;
import com.ipserc.arith.complex.*;
import java.util.Random;

public class TestSVD05 {
	private static Random randomNbr = new Random(System.currentTimeMillis());
	
	public static void showResults(SVDfactor A) {
    	MatrixComplex S;
    	MatrixComplex V;
    	MatrixComplex U;
    	MatrixComplex USV;
		int boxSize = 65;
		boolean UVproperties = true;
		
		Complex.printBoxTitleRandom(boxSize, "SVD FACTORIZATION TEST");
		Complex.printBoxTextRandom(boxSize, "EXACT:" + Complex.exact());
		Complex.printBoxTextRandom(boxSize, "Method:" + A.getMethodName());
		if (!A.factorized())
			Complex.printBoxTextRandom(boxSize, "F A I L ! ! ! ! ! ! !");
    	S = A.getS();
    	V = A.getV();
    	U = A.getU();
		Complex.printBoxTitleRandom(boxSize, "Solutions Section");
		Complex.printBoxTextRandom(boxSize, "--- Matrix A ---");
    	A.println("A");
		System.out.println("MATRIXCOMPLEX.A:" +A.toMatrixComplex());
		System.out.println("WOLFRAM.......A:" +A.toWolfram());
		System.out.println("OCTAVE........A:" +A.toOctave());
		Complex.printBoxTextRandom(boxSize, "--- Matrix S ---");
		S.println("S");
		System.out.println("MATRIXCOMPLEX.S:" +S.toMatrixComplex());
		System.out.println("WOLFRAM.......S:" +S.toWolfram());
		System.out.println("OCTAVE........S:" +S.toOctave());
		Complex.printBoxTextRandom(boxSize, "--- Matrix U ---");
		U.println("U");
		System.out.println("MATRIXCOMPLEX.U:" +U.toMatrixComplex());
		System.out.println("WOLFRAM.......U:" +U.toWolfram());
		System.out.println("OCTAVE........U:" +U.toOctave());
		Complex.printBoxTextRandom(boxSize, "--- Matrix V ---");
    	V.println("V");
		System.out.println("MATRIXCOMPLEX.V:" +V.toMatrixComplex());
		System.out.println("WOLFRAM.......V:" +V.toWolfram());
		System.out.println("OCTAVE........V:" +V.toOctave());
		System.out.println(Complex.boxTitleRandom(boxSize, "Check Section"));
		USV = (U.times(S)).times(V.adjoint());
		USV.println("U·S·V*");
		System.out.println("Is equal: " + ((USV.equals(A)) ? "Yes" : "No")); 
    	
		if (UVproperties) {
			System.out.println(Complex.boxTitleRandom(boxSize, "U Properties Section"));
	       	if (U.isSquare()) U.inverse().println("U.inverse()");
	    	U.adjoint().println("U.adjoint()");
			Complex.printBoxTextRandom(boxSize, "U is Unitary: " + U.isUnitary());
			Complex.printBoxTextRandom(boxSize, "U is Base orthogonal: " + VectorComplex.isOrthogonal(U));

			Complex.printBoxTextRandom(boxSize, "U transpose is Unitary: " + U.transpose().isUnitary());
			Complex.printBoxTextRandom(boxSize, "U transpose is Base orthogonal: " + VectorComplex.isOrthogonal(U.transpose()));


			U.times(U.adjoint()).println("Unitary Matrix: U·U.adjoint()");
	       	if (U.isSquare()) {
		       	U.inverse().times(U.adjoint()).println("Unitary Matrix: U.inverse()·U.adjoint()");
		    	Complex Udet = U.determinant();
		    	System.out.println("Det(U) = " + Udet);
		    	System.out.println("|Det(U)| = " + Udet.abs());
	       	}
	    	
			System.out.println(Complex.boxTitleRandom(boxSize, "V Properties Section"));
			if (V.isSquare()) V.inverse().println("V.inverse()");
	    	V.adjoint().println("V.adjoint()");
	       	Complex.printBoxTextRandom(boxSize, "V is Unitary: " + V.isUnitary());
			Complex.printBoxTextRandom(boxSize, "V is Base orthogonal: " + VectorComplex.isOrthogonal(V));

			Complex.printBoxTextRandom(boxSize, "V transpose is Unitary: " + V.transpose().isUnitary());
			Complex.printBoxTextRandom(boxSize, "V transpose is Base orthogonal: " + VectorComplex.isOrthogonal(V.transpose()));

			V.times(V.adjoint()).println("Unitary Matrix: V·V.adjoint())");
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
    	MatrixComplex aMat;

		// Complex.resetFormatStatus();
		// Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotON();
    	// Complex.digits(10000000);
    	//Complex.setScientificON(4);
    	Complex.setFixedON(4);
    	Complex.numpPadPLUS();
		SVDfactor.debugOFF();
    	
    	// aMat = new MatrixComplex("+5,-9,-2;-2,+5,+2;+6,+2,+4");
		// aMat = new MatrixComplex("+5,-9,-2;+6,+2,+4");
	    // aMat = new MatrixComplex(3,5); aMat.initMatrixRandomInt(99);
	    // aMat = aMat.hermitian();
		/* */
	    // aMat = new MatrixComplex("1,1;1,0;0,1");
		// aMat = new MatrixComplex("i,1;1-i,0;0,1");
		// aMat = aMat.transpose();
		/* */
		// aMat = new MatrixComplex("2,1,-2;4,-4,2");

		// Este ejemplo da la matriz U is Unitary: false
		// aMat = new MatrixComplex("+3,+2,-1;+2,+3,+1;+0,+0,+5");

		// Este ejemplo da la matriz V is Unitary: false
		// Los vectores propios asociados a valores propios distintos de una matriz hermitiana generan subespacios propios ortogonales.
		// root: +84.0000 - solution: [ +0.4082 , +0.8165 , +0.4082 ] - Is eigenvector: Yes
		// root: +0.0000 - solution: [ -0.8944 , +0.4472 , +0.0000 ] - Is eigenvector: Yes
		// root: +0.0000 - solution: [ -0.7071 , +0.0000 , +0.7071 ] - Is eigenvector: Yes
		// 
		// aMat = new MatrixComplex(" 1, 2, 1; 2, 4, 2; 3, 6, 3");

		// aMat = new MatrixComplex("+5,-80,-33;-22,-47,+34;-91,+9,+6;-66,+38,+66;+59,-75,-86");
		// aMat = new MatrixComplex("-25,-26,+60;+96,-8,+1;+40,+61,-82;+18,-4,-77;+51,+77,+2");

		/* * /
		aMat = new MatrixComplex("" +
									"2, 3;" +
									"7, 6;" +
									"2, 0;" +
									"1, 9" );
		aMat = aMat.transpose();
		/* */

		/* * /
		aMat = new MatrixComplex("" +
									"1, 1;" +
									"2, 1" );
		//aMat = aMat.transpose();
		/* */

		/* * /
		aMat = new MatrixComplex("" +
									"1, 0;" +
									"0, 1;" +
									"-1i, 0;" +
									"0, -1i" );
		//aMat = aMat.transpose();
		/* */

		/* * /
		aMat = new MatrixComplex("" +
									"4, 11, 14;" +
									"8, 7, -2");
		/* */

		/* * /
		aMat = new MatrixComplex("" +
									"4, 11, 14;" +
									"8, 7, -2;" +
									"0, 0, 12");
		//aMat = aMat.transpose();
		/* */

		/* * /
	    aMat = new MatrixComplex(""
	    		+ "+2.0,-3.0,-2.0,+3.0,-1.0;"
	    		+ "+1.0,+2.0,+1.0,-1.0,+1.0;"
	    		+ "-1.0,+1.0,+1.0,+1.0,-1.0;"
	    		+ "+1.0,-3.0,-1.0,+3.0,+1.0;"
	    		+ "-2.0,+2.0,+1.0,-2.0,+1.0");
		/* */

		/* * /
	    aMat = new MatrixComplex(""
		+ "-18,13,-4,  4;"
		+ "  2,19,-4, 12;"
		+ "-14,11,-12, 8;"
		+ " -2,21,  4, 8");
		/* */

		/*  */
		aMat = new MatrixComplex(2, 5);
		aMat.initMatrixRandomRec(5);;
		/* */

		/* *********************************** */
		/* CALCULATIONS HERE */
		/* *********************************** */
		svd = new SVDfactor(aMat); 
		showResults(svd);

		boolean checkMethods = false;
		if (checkMethods) {
			// SVDmethod {NONE, SVD, FULL, REDUCED, IDENTITY, IDENTMIX, IMAGINARY};
			svd = new SVDfactor(aMat, SVDfactor.SVDmethod.SVD);
			showResults(svd);
			
			svd = new SVDfactor(aMat, SVDfactor.SVDmethod.REDUCED);
			showResults(svd);
		}

	}
}
