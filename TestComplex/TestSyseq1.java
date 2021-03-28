/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq1
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.*;

public class TestSyseq1 {

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
		
		fMatrix = new Syseq(  " 1,-2, 3 ,1; "
							+ "-2, 5,-1, 2; "
							+ " 4,-9, 7, 0");
		solve(fMatrix);

		fMatrix = new Syseq(  " 3, 1,-1,-4,  6;"
							+ " 1,-2, 3, 1,  2;"
							+ "-2, 5,-1, 2, -3;"
							+ "10,-7, 5,-8, 19");
		solve(fMatrix);
		
		fMatrix = new Syseq(  " 1, 2, 1, -1;"
				            + " 2, 4, 2, -2;"
				            + " 3, 6, 3, -3");
		solve(fMatrix);
		
		fMatrix = new Syseq(   " 1, -3,  4,  2,-1,  3;"
							+  " 3, -9, 12,  6,-3,  9;"
							+  "-1, -5,  2,  0,-3,  1;"
							+  " 4,-12, 16,  8,-4, 12;"
							+  "-5, 15,-20,-10, 5,-15;");
		solve(fMatrix);

		fMatrix = new Syseq(  " 1, -3,  1;"
							+ "-2,  3, -1");
		solve(fMatrix);

		fMatrix = new Syseq(  " 1, -3, 0;"
							+ " 2, -6, 0");
		solve(fMatrix);
		
		fMatrix = new Syseq(  " 1, -3,  1;"
							+ "-2,  6,  1");
		solve(fMatrix);
		
		fMatrix = new Syseq(  " 1, -3,  1;"
							+ "-2,  6, -2");
		solve(fMatrix);

		fMatrix = new Syseq(   " 1, -3,  4,  2,-1,  3;"
							+  " 3, -9, 12,  6,-3,  9;"
							+  "-1, -5, 2i,  0,-3,  1;"
							+  " 4,-12, 16,  8,-4, 12;"
							+  "-5, 15,-20,-10, 5,-15;");
		solve(fMatrix);

		fMatrix = new Syseq(5); fMatrix.initMatrixRandomRecInt(10);
		solve(fMatrix);
		
		fMatrix = new Syseq(3); fMatrix.initMatrixRandomInteger(1);
		solve(fMatrix);
		
		fMatrix = new Syseq(7); fMatrix.initMatrixRandomInteger(10);
		solve(fMatrix);
			
		fMatrix = new Syseq("2.000,-2.000,-4.000,0.000;-2.000,2.000,4.000,0.000;0.000,0.000,0.000-0.000i,0.000");
		solve(fMatrix);

     	fMatrix = new Syseq(""+
     			"  4.000 , -1.000 ,  1.000, 0 ;" +
     			"  0.000 ,  1.000 ,  3.000, 0 ;" +
     			"  0.000 ,  2.000 ,  2.000, 0 ");
		solve(fMatrix);
	}
}
