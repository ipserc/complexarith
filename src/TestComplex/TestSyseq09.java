/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq8
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.*;

public class TestSyseq09 {

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
		Syseq fMatrix = new Syseq();
		
		Syseq.version();

		Complex.setFormatON();
		Complex.setFixedON(3);
		
		fMatrix = new Syseq(  " 3, 1, 1 ,0; "
							+ " 3, 4, 2, 0; "
							+ "-5, 3,-1, 0");
		solve(fMatrix);
		
		fMatrix = new Syseq(("2.562,-1.000,-1.000,0.000;-3.000,1.562,-2.000,0.000;5.000,-3.000,6.562,0.000"));
		solve(fMatrix);
		
		fMatrix = new Syseq(("0.000,-2.000,0.000;0.000,2.000,0.000"));
		solve(fMatrix);

	}
}
