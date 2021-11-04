package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestGram02 {

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
		System.out.println(gMatrix.toOctave());
		System.out.println(gMatrix.toMatrixComplex());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix;
		
		Complex.setFormatON();
		Complex.setFixedON(3);
		
		aMatrix = new MatrixComplex("1.000i,1.000i,1.000i;-1.000i,-1.000i,-1.000i;1.000i,1.000i,-1.000i");
		showResults(aMatrix);

	}
}
