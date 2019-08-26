/******************************************************************************
 *  Compilation:  javac TestComplex.java
 *  Execution:    java TestComplex
 *
 *  Tests for arith.Complex.
 *	
 *  
 *  
 *  
 *  
 *  
 *  
 *
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *
 ******************************************************************************/
package TestComplex;

import com.ipserc.arith.complex.Complex;
//import arith.Complex;
import com.ipserc.arith.matrixcomplex.*;
import java.util.List;
import java.util.ArrayList;

public class TestMatrixComplex {
    public static void main(String[] args) {
    	MatrixComplex aMatrix;
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;
    	MatrixComplex fMatrix;
    	MatrixComplex gMatrix;
    	MatrixComplex hMatrix;
    	MatrixComplex iMatrix;
    	MatrixComplex sMatrix;

    	Complex.setFixedON(3);
    	/*
    	fMatrix = new MatrixComplex("12,-51,4,-13;6,167,-68,23;-4,24,-41,-2;-1,1,0,45;2,0,3,7");
    	fMatrix = new MatrixComplex("0,1,-1;1,1,0;-1,0,1");
    	fMatrix = new MatrixComplex("2,3,0;-1,3,-2;5,-2,1");
    	fMatrix = new MatrixComplex("3,-1,1;0,2,0;1,-1,3");
    	fMatrix = new MatrixComplex("i,1,-2;-1,2,1;0,1,-1");
    	aMatrix = new MatrixComplex("12,-51,4,3;6,167,-68,1;-4,24,-41,-3");
    	 */
    	
    	aMatrix = new MatrixComplex(""+
    			" 2, 1, 3, 4;"+
    			" 4, 2, 1, 5;"+
    			" 1, 0, 2, 3");
    	aMatrix.println("00-aMatrix A");
    	fMatrix = aMatrix.Ftransf(1,2);
    	fMatrix.println("01-Ftransf(1,2)·A");

    	fMatrix = aMatrix.Ftransf(1,"3");
    	fMatrix.println("02-Ftransf(1,'3')·A");

    	fMatrix = aMatrix.Ftransf(1, 0,"-2");
    	fMatrix.println("03-Ftransf(1, 0,'-2')·A");

    	aMatrix = new MatrixComplex(""+
    			" 2, 1, 1, 1;"+
    			" 4, 1, 0,-2;"+
    			"-2, 2, 1, 7");
    	dMatrix = aMatrix.triangle();
    	dMatrix.println("04-dMatrix");
    	
    	aMatrix = new MatrixComplex(""+
    			" 2, 1, 1, 1;"+
    			" 4, 1, 0,-2;"+
    			"-2, 2, 1, 7");
    	aMatrix.println("05-aMatrix A");
    	sMatrix = aMatrix.solve();
    	sMatrix.println("06-sMatrix");
    	
    	aMatrix = new MatrixComplex(""+
    			" 1, 1, 1, 1;"+
    			" 2i,2, 1, 2;"+
    			" 1, 1i,0, 2");
    	aMatrix.println("07-aMatrix A");
    	sMatrix = aMatrix.solve();
    	sMatrix.println("08-sMatrix");
    	bMatrix = new MatrixComplex(""+
    			" 1, 1, 1;"+
    			" 2i,2, 1;"+
    			" 1, 1i,0");
    	bMatrix.times(aMatrix).println();
    	
    	aMatrix = new MatrixComplex(11, 12);
    	aMatrix.initMatrixRandomPolInt(10);
    	aMatrix.println("09-aMatrix A");
    	sMatrix = aMatrix.solve();
    	sMatrix.println("10-sMatrix");
    	
    	aMatrix = new MatrixComplex(""+
    			" 1, 2, 1, 0, 0;"+
    			" 0,-1, 1, 1, 0;"+
    			" 1, 0,-1, 0, 1;"+
    			" 1, 1, 2, 1, 0");
    	bMatrix = aMatrix.triangle();
    	bMatrix.println("Triangle");
    	bMatrix.transpose().println("Triangle T");
    	
    	aMatrix = new MatrixComplex(""+
    			" 1, 1, 0, 0, 1;"+
    			"-2, 0, 1, 1, 2;"+
    			" 1,-1, 0, 1, 0;"+
    			" 0, 0, 1, 0,-1");
    	aMatrix.println("aMatrix A");
    	sMatrix = aMatrix.solve();
    	sMatrix.println("sMatrix");

    	aMatrix = new MatrixComplex(""+
    			"1, 1, 0, 0;"+
    			"-2,0, 1, 1;"+
    			"1,-1, 0, 1;"+
    			"0, 0, 1, 0");
    	bMatrix = new MatrixComplex("1;2;0;-1");
    	aMatrix.println("aMatrix");
    	bMatrix.println("bMatrix");
    	aMatrix.triangle().println("aMatrix Triangle");
    	aMatrix.inverse().println();
    	aMatrix.inverse().times(bMatrix).println();

    	aMatrix = new MatrixComplex(""+
    			" 1, 1, 0, 1;"+
    			"-2, 1, 1, 1;"+ 
    			" 1,-1, 1, 1;"+ 
    			" 3, 0, 1, 0");
    	bMatrix = new MatrixComplex("1;2;0;-1");
    	aMatrix.println("aMatrix");
    	bMatrix.println("bMatrix");
    	aMatrix.triangle().println("aMatrix Triangle");
    	aMatrix.inverse().println();
    	aMatrix.inverse().times(bMatrix).println();

    	//aMatrix = new MatrixComplex("00, 01, 02, 03; 10, 11, 12, 13; 20, 21, 22, 23; 40, 41, 32, 33");
    	//aMatrix = new MatrixComplex("10, 11, 12, 13; 20, 21, 22, 23; 30, 31, 32, 33; 40, 41, 42, 43");
    	aMatrix = new MatrixComplex(""+
    			"-1, 2, 3;"+
    			" 1, 5, 6;"+
    			" 0, 4, 3");
    	//aMatrix = new MatrixComplex("-1, 2, 3, 4; 1, 5, 6, -3; 0, 4, 3, 2; -2, 4, -1, -3");
    	//aMatrix = new MatrixComplex(3); aMatrix.initMatrixRandomPol(3);
    	System.out.println(aMatrix.toWolfram());
    	aMatrix.println("matriz original");
    	int order = 2;
    	int incRows[] = new int[order];
    	incRows[0] = 1; incRows[1] = 3;
    	bMatrix = aMatrix.cofactors(incRows);
    	bMatrix.println("cofactors:");
    	bMatrix = aMatrix.cofactors("2,0,1");
    	bMatrix.println("cofactors(2,0,1)");
    	bMatrix = aMatrix.adjugate("3,0");
    	bMatrix.println("adjugate(\"3,0\"):");
    	bMatrix = aMatrix.adjugate();
    	bMatrix.println("adjugate:");
    	bMatrix = aMatrix.cofactor();
    	bMatrix.println("Cofactor:");
    	
    	aMatrix = new MatrixComplex(3); aMatrix.initMatrixRandomRecInt(9);
    	bMatrix = new MatrixComplex(3); bMatrix.initMatrixRandomRecInt(9);
    	aMatrix.println("aMatrix");
    	bMatrix.println("bMatrix");
    	hMatrix = aMatrix.hermitian();
    	hMatrix.println("hMatrix hermitian");
    	hMatrix.adjoint().println("hMatrixŦ");
    	fMatrix = aMatrix.skewHermitian();
    	fMatrix.println("fMatrix skew hermitian");
    	fMatrix.adjoint().opposite().println("-fMatrixŦ");
    	cMatrix = hMatrix.commutator(fMatrix);
    	cMatrix.println("commutator");
    	cMatrix = hMatrix.anticommutator(fMatrix);
    	cMatrix.println("anticommutator");
    	
    	aMatrix = new MatrixComplex("2,-3,4");
    	bMatrix = new MatrixComplex("2,5,-1");
    	cMatrix = aMatrix.transpose().times(bMatrix);
    	aMatrix.println("aMatrix");
    	bMatrix.println("bMatrix");    	
    	cMatrix.println("aMatrix.times(bMatrix)");

    }
}
