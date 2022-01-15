package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestGram03 {

	public static void showResults(MatrixComplex aMatrix) {
		MatrixComplex gMatrix;
		MatrixComplex gnMatrix;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "GRAM-SCHMIDT DIAGONALIZATION TEST"));
		aMatrix.println("aMatrix");
		gMatrix = aMatrix.gramSchmidt();
		System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt"));
		gMatrix.println("Gram-Schmidt Matrix (G)");
		// aMatrix.adjoint().times(gMatrix).println("AxG");
		// gMatrix.adjoint().times(gMatrix).println("GxG");
		gnMatrix = gMatrix.normalize();
		gnMatrix.println("Gram-Schmidt Normalized (GN)");
		{
			Complex det = gnMatrix.determinant();
			det.println("Det(GN)=");
			System.out.println("|Det(GN)|=" + det.mod());
			System.out.println("Is GN Unitary?:" + (gnMatrix.isUnitary(false) ? "Yes" : "No"));
			//gnMatrix.inverse().println("GN⁻¹");
			//gnMatrix.adjoint().println("GNAd");
			//gnMatrix.adjoint().times(gnMatrix).println("GNAd x GN");
		}
		gMatrix = aMatrix.gramSchmidtFull();
		System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt Full"));
		gMatrix.println("Gram-Schmidt Full Matrix");
		System.out.println(gMatrix.toOctave());
		System.out.println(gMatrix.toMatrixComplex());

		if (aMatrix.isSquare()) {
			System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt Gauss"));
			gMatrix = aMatrix.gramSchmidtGauss();
			gMatrix.println("Gram-Schmidt Gauss Matrix");
			gnMatrix = gMatrix.normalize();
			gnMatrix.println("G Normalized");
			{
				Complex det = gnMatrix.determinant();
				det.println("Det(GN)=");
				System.out.println("|Det(GN)|=" + det.mod());
				System.out.println("Is GN Unitary?:" + (gnMatrix.isUnitary(false) ? "Yes" : "No"));
				//gnMatrix.inverse().println("GN⁻¹");
				//gnMatrix.adjoint().println("GNAd");
				//gnMatrix.adjoint().times(gnMatrix).println("GNAd x GN");
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix;
		
		Complex.setFormatON();
		Complex.setFixedON(3);
		
		aMatrix = new MatrixComplex(5); aMatrix.initMatrixRandomInteger(7);
		showResults(aMatrix);

		aMatrix = new MatrixComplex(5); aMatrix.initMatrixRandomPol(7);
		showResults(aMatrix);
	}
}
