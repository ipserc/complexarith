package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestGram01 {

	public static void showResults(MatrixComplex aMatrix) {
		MatrixComplex gMatrix;
		MatrixComplex gnMatrix;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "GRAM-SCHMIDT DIAGONALIZATION TEST"));
		aMatrix.println("aMatrix");
		gMatrix = aMatrix.gramSchmidt();
		System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt"));
		gMatrix.println("Gram-Schmidt Matrix");
		aMatrix.adjoint().times(gMatrix).println("AxG");
		gMatrix.adjoint().times(gMatrix).println("GxG");
		gnMatrix = gMatrix.normalize();
		gnMatrix.println("G Normalized");
		gnMatrix.adjoint().times(gnMatrix).println("GN*xGN");
		gMatrix = aMatrix.gramSchmidtFull();
		System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt Full"));
		gMatrix.println("Gram-Schmidt Full Matrix");

		if (aMatrix.isSquare()) {
			System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt Gauss"));
			gMatrix = aMatrix.gramSchmidtGauss();
			gMatrix.println("Gram-Schmidt Gauss Matrix");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix;
		
		Complex.setFormatON();
		Complex.setFixedON(3);
		
		aMatrix = new MatrixComplex("2,1;1,4");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("1,2;1,2;0,3");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("1,1,1;2,2,0;3,0,0;0,0,1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("1,-1,1;1,0,1;1,1,2");
		showResults(aMatrix);

		aMatrix = aMatrix.transpose();
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(6,4);aMatrix.initMatrixRandomInteger(9);
		showResults(aMatrix);
		aMatrix = aMatrix.transpose();
		showResults(aMatrix);
	}
}
