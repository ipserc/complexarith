/* /usr/lib/jvm/java-8-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 
 * -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes 
 * TestComplex.TestDiagonal
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.chronometer.Chronometer;

public class TestDiagonal02 {

	private static void showResults(MatrixComplex fMatrix) {
		int boxSize = 65;
		
       	System.out.println("");
       	System.out.println(Complex.boxTitleRandom(boxSize, "DIAGONALIZATION TEST"));
       	System.out.println(Complex.boxTextRandom(boxSize, "Matrix"));
       	System.out.println("MCOMPX :"+fMatrix.toMatrixComplex());
       	System.out.println("MAXIMA :"+fMatrix.toMaxima());
       	System.out.println("OCTAVE :"+fMatrix.toOctave());
       	System.out.println("WOLFRAM:"+fMatrix.toWolfram());

       	Chronometer chrono = new Chronometer();
		Diagfactor dMatrix = new Diagfactor(fMatrix);
		chrono.stop();

		System.out.println("time to diagonalize:" + chrono.toString());
		fMatrix.println("--- A");
		fMatrix.triangle().heap().println("--- Triangle");
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

	public static void main(String[] args) {
		MatrixComplex fMatrix;

       	Complex.setFormatON();
    	Complex.setFixedON(3);
  	
    	for (int i = 2; i < 10; ++i) {
    		fMatrix = new MatrixComplex(i); fMatrix.initMatrixRandomInteger(5);
    		showResults(fMatrix);
    	}
	}

}
