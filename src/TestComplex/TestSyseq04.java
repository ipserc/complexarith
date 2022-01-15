/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq1
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.*;

public class TestSyseq04 {
	private static boolean Reduced = true;

	private static void solve(Syseq fMatrix) {
		MatrixComplex solution;
		int boxSize = 65;
		
		fMatrix.print(Complex.boxTextRandom(boxSize,"Equation System"));
		
     	System.out.println(Complex.boxTextRandom(boxSize, "System Solve Commands"));
     	fMatrix.printSystemEqSolve(outputFormat.MAXIMA, true);
		fMatrix.printSystemEqSolve(outputFormat.OCTAVE, true);
		fMatrix.printSystemEqSolve(outputFormat.WOLFRAM, true);

		fMatrix.printSol(Complex.boxTextRandom(boxSize, "System Solutions"));
		switch (fMatrix.typeEqSys(Reduced)) {
			case MatrixComplex.INCONSISTENT: break ;
			case MatrixComplex.DETERMINATE: {
		     	System.out.println(Complex.boxTextRandom(boxSize, "Solutions check"));				
		     	fMatrix.checkSol(fMatrix.solution());
				break ;
			} 
			default: { // MatrixComplex.INDETERMINATE
		     	System.out.println(Complex.boxTextRandom(boxSize, "Solutions check"));				
				for (int i = -5; i < 6; ++i) {
					double n = i/10.0;
					System.out.println(Complex.repeat("*", 20) + " Sol nbr:" + i); 
					solution = fMatrix.solution(n);
					fMatrix.checkSol(solution);
				}		
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Syseq aMatrix;
		int boxSize = 65;
		int filas = 5;

     	Complex.setFormatON();
     	Complex.setFixedON(3);

     	Syseq.version();

		System.out.println(Complex.boxTitleRandom(boxSize, "SYSEQ TEST"));
		System.out.println("Testing with Sys. Eq. of " +filas);
		for(int i = 1; i < 10; ++i) {
			System.out.println(Complex.boxTitleRandom(boxSize, "LINEAR EQUATIONS SYSTEM TEST"));
			System.out.println(Complex.boxTextRandom(boxSize, "TEST #"+i));   	
	     	aMatrix = new Syseq(filas);
			aMatrix.initMatrixRandomInteger(1);
			aMatrix.println("Matrix");
	    	System.out.println("MATRIX COMPLEX:"+aMatrix.toMatrixComplex());
			solve(aMatrix);
		}
	}
}
