/* /usr/lib/jvm/java-8-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 
 * -classpath /home/ipserc/workspace_neon/complexarith/bin:/home/ipserc/workspace_neon/complexarith/classes 
 * TestComplex.TestDiagonal
 */
package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;

public class TestDiagonal {

	private static void showResults(Diagfactor fMatrix) {
		System.out.println("__________________________________________________________________________________________");
    	System.out.println("______________________________ DIAGONALIZACION DE MATRICES _______________________________");
		fMatrix.diagonalize();
		MatrixComplex D = fMatrix.D();
		MatrixComplex P = fMatrix.P();
 		fMatrix.println("--- A");
		D.println("--- D Diagonal");
		P.println("--- P Matrix");
		P.determinant().println("Determinant(P)");
		(P.times(D).times(P.inverse())).println("A=P·D·P⁻¹");
	}

	private static MatrixComplex diagonal(MatrixComplex fMatrix) {
		int rowLen = fMatrix.complexMatrix.length; 
		int colLen= fMatrix.complexMatrix[0].length;
		MatrixComplex eigenVal = fMatrix.eigenvalues();
		MatrixComplex eigenVect = fMatrix.eigenvectors(eigenVal).transpose();
		eigenVal.println("eigenVal");		
		eigenVect.println("eigenVect");

		// Σ: diagonal eigenvalues square root matrix, augmented with zeroes
		//
		MatrixComplex diagonal = new MatrixComplex(rowLen, colLen);
		for (int i = 0; i < colLen; ++i)
			diagonal.complexMatrix[i][i] = eigenVal.complexMatrix[i][0];
		diagonal.println("--- Diagonal");

		return eigenVal;
	}

	public static void main(String[] args) {
		Diagfactor fMatrix;

       	Complex.setFormatON();
    	Complex.setFixedON(3);

		fMatrix= new Diagfactor(1);
    	
		fMatrix= new Diagfactor("1,i,1;1,-2+i,3-i;1,0,-i");
		showResults(fMatrix);

		fMatrix = new Diagfactor("3,1;1,3");
		showResults(fMatrix);

		fMatrix = new Diagfactor("3,1i,-1i;1,3i,2;-1,1,-3");
		showResults(fMatrix);

		/**/
		fMatrix = new Diagfactor(6); fMatrix.initMatrixRandomInteger(9);
		showResults(fMatrix);

		fMatrix = new Diagfactor(4); fMatrix.initMatrixRandomInteger(9);
		fMatrix.complexMatrix = fMatrix.hermitian().complexMatrix;
		showResults(fMatrix);

		//try { System.in.read(); } catch(java.io.IOException e) {}
		
		fMatrix = new Diagfactor(4); fMatrix.initMatrixRandomInteger(10);
		fMatrix.complexMatrix = fMatrix.skewHermitian().complexMatrix;
		showResults(fMatrix);
		/**/

		fMatrix = new Diagfactor("1,2,3;0,-1,2;0,1,1");
		showResults(fMatrix);

		fMatrix = new Diagfactor("1,2,3,-i;0,+2i,-1,2;3i,0,1,1;4,-2,-i,5");
		showResults(fMatrix);

		fMatrix = new Diagfactor("1,0,1;0,1i,-2;0,0,2");
		showResults(fMatrix);

	}

}
