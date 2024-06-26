/* /usr/lib/jvm/java-8-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 
 * -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes 
 * TestComplex.TestDiagonal
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.chronometer.Chronometer;

public class TestDiagonal04 {

	private static void showResults(MatrixComplex fMatrix) {
		int boxSize = 65;
		
       	System.out.println("");
       	System.out.println(Complex.boxTitleRandom(boxSize, "DIAGONALIZATION TEST"));
       	System.out.println(Complex.boxTextRandom(boxSize, "Matrix (" + fMatrix.rows() + "x" + fMatrix.cols() + ")"));
       	System.out.println("MCOMPX :"+fMatrix.toMatrixComplex());

       	Chronometer chrono = new Chronometer();
		Diagfactor dMatrix = new Diagfactor(fMatrix);
		chrono.stop();

       	System.out.println("MAXIMA :"+dMatrix.toMaxima_diagonalize());
       	System.out.println("OCTAVE :"+dMatrix.toOctave_diagonalize());
       	System.out.println("WOLFRAM:"+dMatrix.toWolfram_diagonalize());

		fMatrix.println("--- A Matrix");
		fMatrix.triangle().heap().println("--- Triangle");
		if (dMatrix.factorized()) {
	       	System.out.println(Complex.boxTextRandom(boxSize, "Diagonal Matrix: " + chrono.toString()));
	      	System.out.println(Complex.boxTextRandom(boxSize, "Diagonalizaton Results"));
			MatrixComplex D = dMatrix.D();
			MatrixComplex P = dMatrix.P();
	 		D.println("--- D Diagonal");
			P.println("--- P Matrix");
			P.determinant().println("Determinant(P)=");
	      	System.out.println(Complex.boxTextRandom(boxSize, "Diagonalizaton Check"));
	      	(P.times(D).times(P.inverse())).println("A=P·D·P⁻¹");
		}
	}
	
	public static void main(String[] args) {
		MatrixComplex fMatrix;

       	Complex.setFormatON();
    	Complex.setFixedON(3);
  	
    	for (int i = 2; i < 10; ++i) {
    		fMatrix = new MatrixComplex(i); fMatrix.initMatrixRandomInteger(5);
    		fMatrix = fMatrix.hermitian();
    		showResults(fMatrix);
    	}
	}

}
