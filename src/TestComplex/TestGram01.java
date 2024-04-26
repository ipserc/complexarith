package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestGram01 {

	public static void showResults(MatrixComplex aMatrix) {
		MatrixComplex gMatrix;
		MatrixComplex gnMatrix;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "GRAM-SCHMIDT TEST"));
		aMatrix.println("aMatrix");
		System.out.println("Orthogonalize[" + aMatrix.toWolfram()+ "]");
		gMatrix = aMatrix.gramSchmidt();
		System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt"));
		gMatrix.println("Gram-Schmidt Matrix");
		gMatrix.times(gMatrix.adjoint()).println("Gram Matrix:G x G*");
		gnMatrix = gMatrix.normalize();
		gnMatrix.println("Gram-Schmidt Matrix Normalized (GN)");
		gnMatrix.times(gnMatrix.adjoint()).println("GN x GN*");

		/*********************
		 * DEPRECATED
		System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt Full"));
		gMatrix = aMatrix.gramSchmidtFull();
		gMatrix.println("Gram-Schmidt Full Matrix");
		gMatrix.times(gMatrix.adjoint()).println("Gram Matrix:GF x GF*");
		gnMatrix = gMatrix.normalize();
		gnMatrix.println("Gram-Schmidt Full Matrix Normalized (GFN)");
		gnMatrix.times(gnMatrix.adjoint()).println("GFN x GFN*");
		***********************/

		if (aMatrix.isSquare()) {
			System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt Gauss"));
			gMatrix = aMatrix.gramSchmidtGauss();
			gMatrix.println("Gram-Schmidt Gauss Matrix");
			gMatrix.times(gMatrix.adjoint()).println("Gram Gauss Matrix:GGF x GGF*");
			gnMatrix = gMatrix.normalize();
			gnMatrix.println("Gram-Schmidt Gauss Full Matrix Normalized (GFN)");
			gnMatrix.times(gnMatrix.adjoint()).println("GGFN x GGFN*");

		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix;
		
		Complex.setFormatON();
		Complex.setFixedON(3);

		aMatrix = new MatrixComplex(""
				+ " 4,-2,-1, 2;"
				+ "-6, 3, 4,-8;"
				+ " 5,-5,-3,-4");
		showResults(aMatrix);		
		showResults(aMatrix.transpose());		

		aMatrix = new MatrixComplex(""
				+ "1,0,2;"
				+ "0,1,1");
		showResults(aMatrix);		
		showResults(aMatrix.transpose());		
		
		aMatrix = new MatrixComplex(""
				+ "2,1;"
				+ "1,4");
		showResults(aMatrix);

		aMatrix = new MatrixComplex(""
				+ "1,2;"
				+ "1,2;"
				+ "0,3");
		showResults(aMatrix);
		showResults(aMatrix.transpose());

		aMatrix = new MatrixComplex(""
				+ "1,1,1;"
				+ "2,2,0;"
				+ "3,0,0;"
				+ "0,0,1");
		showResults(aMatrix);
		showResults(aMatrix.transpose());

		aMatrix = new MatrixComplex(""
				+ "1,-1,1;"
				+ "1,0,1;"
				+ "1,1,2");
		showResults(aMatrix);
		aMatrix = aMatrix.transpose();
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(6,4);aMatrix.initMatrixRandomInteger(9);
		showResults(aMatrix);
		aMatrix = aMatrix.transpose();
		showResults(aMatrix);
	}
}
