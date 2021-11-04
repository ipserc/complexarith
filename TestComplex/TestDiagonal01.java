/* /usr/lib/jvm/java-8-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 
 * -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes 
 * TestComplex.TestDiagonal
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.chronometer.Chronometer;

public class TestDiagonal01 {

	private static void showResults(MatrixComplex fMatrix) {
		int boxSize = 65;
		
       	System.out.println("");
       	System.out.println(Complex.boxTitleRandom(boxSize, "DIAGONALIZATION TEST"));
       	fMatrix.println("--- A");
		fMatrix.triangle().heap().println("--- Triangle");
       	System.out.println(Complex.boxTextRandom(boxSize, "Matrix"));
       	System.out.println("MCOMPX :"+fMatrix.toMatrixComplex());
       	System.out.println("MAXIMA :"+fMatrix.toMaxima());
       	System.out.println("OCTAVE :"+fMatrix.toOctave());
       	System.out.println("WOLFRAM:"+fMatrix.toWolfram());

       	Chronometer chrono = new Chronometer();
		Diagfactor dMatrix = new Diagfactor(fMatrix);
		chrono.stop();

       	System.out.println(Complex.boxTextRandom(boxSize, "Diagonalize Expresions"));
       	System.out.println("MAXIMA :"+dMatrix.toMaxima_diagonalize());
       	System.out.println("OCTAVE :"+dMatrix.toOctave_diagonalize());
       	System.out.println("WOLFRAM:"+dMatrix.toWolfram_diagonalize());

		if (dMatrix.factorized()) {
	       	System.out.println(Complex.boxTextRandom(boxSize, "Diagonal Matrix"));
	    	System.out.println("The diagonalization has taken: " + chrono.toString());
			MatrixComplex D = dMatrix.D();
			MatrixComplex P = dMatrix.P();
	 		D.println("--- D Diagonal");
			P.println("--- P Matrix");
			P.determinant().println("Determinant(P)=");
			(P.times(D).times(P.inverse())).println("A=P·D·P⁻¹");
		}
	}

	/*
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
	*/

	public static void main(String[] args) {
		MatrixComplex fMatrix;

       	Complex.setFormatON();
    	Complex.setFixedON(3);
  	
    	/**/

		fMatrix= new MatrixComplex("1,i,1;1,-2+i,3-i;1,0,-i");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("3,1;1,3");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("3,1i,-1i;1,3i,2;-1,1,-3");
		showResults(fMatrix);

		/**/

		fMatrix = new MatrixComplex(6); fMatrix.initMatrixRandomInteger(9);
		showResults(fMatrix);

		fMatrix = new MatrixComplex(4); fMatrix.initMatrixRandomInteger(9);
		fMatrix.complexMatrix = fMatrix.hermitian().complexMatrix;
		showResults(fMatrix);

		//try { System.in.read(); } catch(java.io.IOException e) {}
		
		fMatrix = new MatrixComplex(4); fMatrix.initMatrixRandomInteger(10);
		fMatrix.complexMatrix = fMatrix.skewHermitian().complexMatrix;
		showResults(fMatrix);

		/**/

		fMatrix = new MatrixComplex("1,2,3;0,-1,2;0,1,1");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1,2,3,-i;0,+2i,-1,2;3i,0,1,1;4,-2,-i,5");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1,0,1;0,1i,-2;0,0,2");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1,2,3;2,4,6;3,6,9");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1,2,3;0,0,2;0,1,1");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("2,2,-3,4;-2,2,1,0;3,3,-5,7;4,2,-6,7");
		showResults(fMatrix);

		/**/
		
		fMatrix = new MatrixComplex(10); fMatrix.initMatrixRandomInteger(5);
		showResults(fMatrix);		
	}

}
