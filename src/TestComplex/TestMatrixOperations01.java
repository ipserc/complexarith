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

public class TestMatrixOperations01 {
	private static int boxSize = 65;

	
	public static void showDIVresults(MatrixComplex aMatrix, MatrixComplex bMatrix) {
    	MatrixComplex cMatrix;
    	
		System.out.println(Complex.boxTextRandom(boxSize, "Divides Right & Left"));
		aMatrix.println("aMatrix");
		bMatrix.println("bMatrix");

		System.out.println(Complex.boxText1(boxSize, "Test DIV 1"));
		bMatrix.dividesright(bMatrix).println("bMatrix.dividesright(bMatrix)");
		bMatrix.dividesleft(bMatrix).println("bMatrix.dividesleft(bMatrix)");

		System.out.println(Complex.boxText1(boxSize, "Test DIV 2"));
		cMatrix = aMatrix.dividesright(bMatrix);
		cMatrix.println("cMatrix = aMatrix.dividesright(bMatrix)");
		cMatrix.inverse().println("cMatrix.inverse()");
		cMatrix = bMatrix.dividesright(aMatrix);
		cMatrix.println("cMatrix = bMatrix.dividesright(aMatrix)");
		cMatrix.inverse().println("cMatrix.inverse()");

		System.out.println(Complex.boxText1(boxSize, "Test DIV 3"));
		cMatrix = aMatrix.dividesleft(bMatrix);
		cMatrix.println("cMatrix = aMatrix.dividesleft(bMatrix)");
		cMatrix.inverse().println("cMatrix.inverse()");		
		cMatrix = bMatrix.dividesleft(aMatrix);
		cMatrix.println("cMatrix = bMatrix.dividesleft(aMatrix)");
		cMatrix.inverse().println("cMatrix.inverse()");
	}
	
	public static void showMULresults(MatrixComplex aMatrix, MatrixComplex bMatrix) {
    	MatrixComplex cMatrix;

		System.out.println(Complex.boxTextRandom(boxSize, "Matrices products"));
		aMatrix.println("aMatrix");
		bMatrix.println("bMatrix");

		System.out.println(Complex.boxText1(boxSize, "Test MUL 1"));
		cMatrix = aMatrix.times(bMatrix);
		cMatrix.power(2).println("(aMatrix.times(bMatrix))²");
		aMatrix.times(bMatrix.times(aMatrix)).times(bMatrix).println("aMatrix.times(bMatrix.times(aMatrix)).times(bMatrix)");
		(aMatrix.power(2)).times(aMatrix.power(2)).println("(aMatrix)².times((bMatrix)²)");
		(bMatrix.power(2)).times(aMatrix.power(2)).println("(bMatrix)².times((aMatrix)²)");
	}
	
	public static void showPOWresults(MatrixComplex aMatrix, MatrixComplex bMatrix) {
    	MatrixComplex cMatrix, dMatrix;
		System.out.println(Complex.boxTextRandom(boxSize, "Powers of matrices"));
		aMatrix.println("aMatrix");
		bMatrix.println("bMatrix");

		System.out.println(Complex.boxText1(boxSize, "Test POW 1"));
		cMatrix = aMatrix.power(2);
		dMatrix = aMatrix.power(-2);
		
		cMatrix.println("cMatrix = aMatrix.power(2)");
		dMatrix.println("dMatrix = aMatrix.power(-2)");
		
		cMatrix.times(dMatrix).println("cMatrix.times(dMatrix)");

		System.out.println(Complex.boxText1(boxSize, "Test POW 2"));
		cMatrix = aMatrix.power(-1);
		cMatrix.println("aMatrix.power(-1)");
		aMatrix.inverse().println("aMatrix.inverse()");
	}

    public static void main(String[] args) {
    	MatrixComplex aMatrix, bMatrix;

		Complex.setFormatON();
		Complex.setFixedON(3);
		
		System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX MATRIX OPERATIONS TEST"));

		aMatrix = new MatrixComplex(""
				+ " 1, 2, 3;"
				+ " 3, 4, 5;"
				+ "-1, 0, 3");
		bMatrix = new MatrixComplex(3); bMatrix.initMatrixRandomInteger(5);	
		showDIVresults(aMatrix, bMatrix);
		
		aMatrix = new MatrixComplex(""
				+ " 1, 2, 3;"
				+ " 3, 4, 5;"
				+ " 1, 1, 1");		
		showDIVresults(aMatrix, bMatrix);

		aMatrix = new MatrixComplex(3); aMatrix.initMatrixRandomInteger(5);
		bMatrix = new MatrixComplex(3); bMatrix.initMatrixRandomInteger(5);
		showMULresults(aMatrix, bMatrix);
		
		aMatrix = new MatrixComplex(3); aMatrix.initMatrixRandomInteger(5);
		bMatrix = new MatrixComplex(3); bMatrix.initMatrixRandomInteger(5);
		showPOWresults(aMatrix, bMatrix);
		
		System.out.println(Complex.boxTextRandom(boxSize, "kernel"));
		aMatrix = new MatrixComplex(""
				+ " 1, 2, 3;"
				+ " 2, 4, 6;"
				+ " 3, 6, 9");		
		aMatrix.println("aMatrix");
		aMatrix.kernel().println("Kernel(aMatrix)");

		aMatrix = new MatrixComplex(""
				+ " 1,-3,-2;"
				+ "-5, 9,-1;"
				+ " 0, 0, 0");		
		aMatrix.println("aMatrix");
		aMatrix.kernel().println("Kernel(aMatrix)");

		aMatrix = new MatrixComplex(""
				+ " 3, 6, 6, 3, 9;"
				+ " 6,12,13, 0, 3;"
				+ " 0, 0, 0, 0, 0;"
				+ " 0, 0, 0, 0, 0;"
				+ " 0, 0, 0, 0, 0");
		aMatrix.println("aMatrix");
		aMatrix.kernel().println("Kernel(aMatrix)");
}
}
