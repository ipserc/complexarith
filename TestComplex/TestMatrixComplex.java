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
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "MATRIX COMPLEX TEST"));

    	Complex.setFixedON(3);
    	/*
    	fMatrix = new MatrixComplex("12,-51,4,-13;6,167,-68,23;-4,24,-41,-2;-1,1,0,45;2,0,3,7");
    	fMatrix = new MatrixComplex("0,1,-1;1,1,0;-1,0,1");
    	fMatrix = new MatrixComplex("2,3,0;-1,3,-2;5,-2,1");
    	fMatrix = new MatrixComplex("3,-1,1;0,2,0;1,-1,3");
    	fMatrix = new MatrixComplex("i,1,-2;-1,2,1;0,1,-1");
    	aMatrix = new MatrixComplex("12,-51,4,3;6,167,-68,1;-4,24,-41,-3");
    	 */
    	
		System.out.println(Complex.boxTextRandom(boxSize, "F Transformations"));
    	aMatrix = new MatrixComplex(""+
    			" 2-3i, 1, 3, 4;"+
    			" 4, 2, 1+3i, 5;"+
    			" 1, 0, 2, 3+4i");
    	aMatrix.println("00-aMatrix A");
    	aMatrix.Ftransf(1,2);
    	aMatrix.println("01-Ftransf(1,2)·A");

    	aMatrix.println("00-aMatrix A");
    	aMatrix.Ftransf(1,"3");
    	aMatrix.println("02-Ftransff(1,\"3\")·A");

    	aMatrix.println("00-aMatrix A");
    	aMatrix.Ftransf(1, 0,"3-2i");
    	aMatrix.println("03-Ftransff(1, 0,\"3-2i\")·A");

		System.out.println(Complex.boxTextRandom(boxSize, "Triangle"));
    	aMatrix = new MatrixComplex(""+
    			" 2, 1, 1, 1;"+
    			" 4, 1, 0,-2;"+
    			"-2, 2, 1, 7");
    	dMatrix = aMatrix.triangle();
    	dMatrix.println("04-dMatrix");
    	
    	aMatrix = new MatrixComplex(""+
    			" 1, 2, 1, 0, 0;"+
    			" 0,-1, 1, 1, 0;"+
    			" 1, 0,-1, 0, 1;"+
    			" 1, 1, 2, 1, 0");
    	bMatrix = aMatrix.triangle();
    	bMatrix.println("Triangle");
    	bMatrix.transpose().println("Triangle T");

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
    	aMatrix.diagonal().println("aMatrix Diagonal");
    	aMatrix.inverse().println("aMatrix Inverse");
    	aMatrix.inverse().times(bMatrix).println("aMatrix Inverse * bMatrix");

		System.out.println(Complex.boxTextRandom(boxSize, "Solve Matrix Eq System"));
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
    			" 1, 1, 0, 0, 1;"+
    			"-2, 0, 1, 1, 2;"+
    			" 1,-1, 0, 1, 0;"+
    			" 0, 0, 1, 0,-1");
    	aMatrix.println("aMatrix A");
    	sMatrix = aMatrix.solve();
    	sMatrix.println("sMatrix");

		System.out.println(Complex.boxTextRandom(boxSize, "Matrix Cofactors"));
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
    	
		System.out.println(Complex.boxTextRandom(boxSize, "Matrix Hermitian Skewhermitian Conmutator & Anticonmutator"));
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
    	
		System.out.println(Complex.boxTextRandom(boxSize, "Matrix 1x3 product"));
    	aMatrix = new MatrixComplex("2,-3,4");
    	bMatrix = new MatrixComplex("2,5,-1");
    	cMatrix = aMatrix.transpose().times(bMatrix);
    	aMatrix.println("aMatrix");
    	bMatrix.println("bMatrix");    	
    	cMatrix.println("aMatrixT.times(bMatrix)");

    }
}
