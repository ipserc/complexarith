/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq1
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.*;

public class TestSyseq7 {

	private static void checkSol(MatrixComplex fMatrix, MatrixComplex solution) {
		MatrixComplex indTerm = fMatrix.indMatrix().transpose();
		MatrixComplex uknMatix = fMatrix.unkMatrix().transpose();
		
		indTerm.println                 ("Independent Terms");
		solution.println                ("Solution         ");
		solution.times(uknMatix).println("Check            ");
	}

	private static void solve(Syseq fMatrix) {
		MatrixComplex solution;
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "LINEAR EQUATIONS SYSTEM TEST"));
		
		fMatrix.print(Complex.boxTextRandom(boxSize,"Equation System"));
		
     	System.out.println(Complex.boxTextRandom(boxSize, "System Solve Commands"));
     	System.out.println("MComplex:" + fMatrix.toMatrixComplex());
		fMatrix.printSystemEqSolve(outputFormat.MAXIMA, true);
		fMatrix.printSystemEqSolve(outputFormat.OCTAVE, true);
		fMatrix.printSystemEqSolve(outputFormat.WOLFRAM, true);

		fMatrix.printSol(Complex.boxTextRandom(boxSize, "System Solutions"));
		switch (fMatrix.typeEqSys()) {
			case MatrixComplex.INCONSISTENT: break ;
			case MatrixComplex.DETERMINATE: {
				checkSol(fMatrix, fMatrix.solution());
				break ;
			} 
			default: { // MatrixComplex.INDETERMINATE
		     	System.out.println(Complex.boxTextRandom(boxSize, "Solutions check"));				
				for (int i = -5; i < 6; ++i) {
					double n = i/10.0;
					System.out.println(Complex.repeat("*", 20) + " Sol nbr:" + i); 
					solution = fMatrix.solution(n);
					checkSol(fMatrix, solution);
				}		
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Syseq fMatrix = new Syseq();
		
		Syseq.version();

		Complex.setFormatON();
		Complex.setFixedON(3);
		
		fMatrix = new Syseq(""+
				  " 1.00,-1.00,-1.00, 1.00, 1.00;"
				+ " 1.00, 1.00,-1.00,-1.00,-1.00;"
				+ "-1.00,-1.00, 1.00, 1.00, 1.00;"
				+ " 1.00,-1.00,-1.00,-1.00,-1.00");
		solve(fMatrix);
	}
}
