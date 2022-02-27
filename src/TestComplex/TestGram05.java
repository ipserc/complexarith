package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestGram05 {

	public static void showResults(MatrixComplex aMatrix) {
		MatrixComplex gMatrix;
		MatrixComplex gnMatrix;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "GRAM-SCHMIDT TEST"));
		System.out.println("OCTAVE : [Q,R] = qr(" + aMatrix.toOctave() +")");
		System.out.println("WOLFRAM: Orthogonalize[" + aMatrix.toWolfram() +"]");
		
		System.out.println(Complex.boxTitleRandom(boxSize, "GRAM-SCHMIDT TEST"));
		aMatrix.println("aMatrix");
		gMatrix = aMatrix.gramSchmidt();
		System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt"));
		gMatrix.println("Gram-Schmidt Matrix");
		gMatrix.times(gMatrix.adjoint()).println("Gram Matrix:G x G*");
		gnMatrix = gMatrix.normalize();
		gnMatrix.println("Gram-Schmidt Matrix Normalized (GN)");
		gnMatrix.times(gnMatrix.adjoint()).println("GN x GN*");
		
		System.out.println(Complex.boxTitleRandom(boxSize, "GRAM-SCHMIDT FULL TEST"));
		aMatrix.println("aMatrix");
		gMatrix = aMatrix.gramSchmidtFull();
		System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt Full"));
		gMatrix.println("Gram-Schmidt Matrix");
		gMatrix.times(gMatrix.adjoint()).println("Gram Matrix:G x G*");
		gnMatrix = gMatrix.normalize();
		gnMatrix.println("Gram-Schmidt Matrix Normalized (GN)");
		gnMatrix.times(gnMatrix.adjoint()).println("GN x GN*");

		System.out.println(Complex.boxTitleRandom(boxSize, "GRAM-SCHMIDT MODIFIED TEST"));
		aMatrix.println("aMatrix");
		gMatrix = aMatrix.gramSchmidtM();
		System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt Modified"));
		gMatrix.println("Gram-Schmidt Matrix");
		gMatrix.times(gMatrix.adjoint()).println("Gram Matrix:G x G*");
		gnMatrix = gMatrix.normalize();
		gnMatrix.println("Gram-Schmidt Matrix Normalized (GN)");
		gnMatrix.times(gnMatrix.adjoint()).println("GN x GN*");

		System.out.println(Complex.boxTitleRandom(boxSize, "GRAM-SCHMIDT MODIFIED FULL TEST"));
		aMatrix.println("aMatrix");
		gMatrix = aMatrix.gramSchmidtMFull();
		System.out.println(Complex.boxTextRandom(boxSize, "Gram-Schmidt Modified Full"));
		gMatrix.println("Gram-Schmidt Matrix");
		gMatrix.times(gMatrix.adjoint()).println("Gram Matrix:G x G*");
		gnMatrix = gMatrix.normalize();
		gnMatrix.println("Gram-Schmidt Matrix Normalized (GN)");
		gnMatrix.times(gnMatrix.adjoint()).println("GN x GN*");

		if (aMatrix.isSquare()) {
			Complex det = gnMatrix.determinant();
			det.println("Det(GN)=");
			System.out.println("|Det(GN)|=" + det.mod());
			System.out.println("Is GN Unitary?:" + (gnMatrix.isUnitary() ? "Yes" : "No"));
			//gnMatrix.inverse().println("GN⁻¹");
			//gnMatrix.adjoint().println("GNAd");
			//gnMatrix.adjoint().times(gnMatrix).println("GNAd x GN");
		}
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
			gnMatrix = gMatrix.normalize();
			gnMatrix.println("G Normalized");
			{
				Complex det = gnMatrix.determinant();
				det.println("Det(GN)=");
				System.out.println("|Det(GN)|=" + det.mod());
				System.out.println("Is GN Unitary?:" + (gnMatrix.isUnitary() ? "Yes" : "No"));
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
		int boxSize = 65;
	
		Complex.setFormatON();
		Complex.setFixedON(3);
		
		/**/
		aMatrix = new MatrixComplex(3,4); aMatrix.initMatrixRandomRecInt(6);
		System.out.println(Complex.boxTitleRandom(boxSize, "++++++ MATRIX 3x4 ++++++"));
		showResults(aMatrix);		
		System.out.println(Complex.boxTitleRandom(boxSize, "++++++ MATRIX 3x4 T ++++++"));
		showResults(aMatrix.transpose());		
		/**/
		
		/**/
		aMatrix = new MatrixComplex(4,4); aMatrix.initMatrixRandomRecInt(6);
		System.out.println(Complex.boxTitleRandom(boxSize, "++++++ MATRIX 4x4 ++++++"));
		showResults(aMatrix);		
		System.out.println(Complex.boxTitleRandom(boxSize, "++++++ MATRIX 4x4 T ++++++"));
		showResults(aMatrix.transpose());		
		/**/
		
		/**/
		aMatrix = new MatrixComplex(4,3); aMatrix.initMatrixRandomRecInt(6);
		System.out.println(Complex.boxTitleRandom(boxSize, "++++++ MATRIX 4x3 ++++++"));
		showResults(aMatrix);		
		System.out.println(Complex.boxTitleRandom(boxSize, "++++++ MATRIX 4x3 T ++++++"));
		showResults(aMatrix.transpose());		
		/**/

	}
}
