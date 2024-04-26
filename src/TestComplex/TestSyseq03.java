/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq1
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.*;

public class TestSyseq03 {

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
		Syseq fMatrix;
		
     	Complex.setFormatON();
     	Complex.setFixedON(3);

     	Syseq.version();

		fMatrix = new Syseq("2,-3,0,-9;0,4,0,16;1,0,1,2"); // Sist Compatible determinado
		solve(fMatrix);
		
		fMatrix = new Syseq("2,-3,0,0;0,4,0,0;1,0,1,0"); // Sist homogéneo
		solve(fMatrix);

		fMatrix = new Syseq("2,-3,0,1;-6,9,0,-3;1,0,1,1"); // Sist Comp Indeterminado
		solve(fMatrix);

		/*
		fMatrix = new Syseq("2,-3,1;0,4,-1;1,0,1"); // No es un sistema de ecuaciones
		solve(fMatrix);
		 */

		fMatrix = new Syseq("1.0,-1.0,-1.0,-1.0,-1.0;1.0,-1.0,1.0,-1.0,1.0;-1.0,-1.0,-1.0,-1.0,-1.0;-1.0,-1.0,1.0,-1.0,1.0");
		solve(fMatrix);

		fMatrix = new Syseq(	
				" 2.0,-1.0,-1.0,-1.0,-1.0;"
			+	" 1.0,-2.0, 1.0,-1.0, 2.0;"
			+	"-1.0,-1.0,-2.0,-1.0,-1.0;"
			+	"-1.0,-1.0, 1.0,-2.0, 2.0");
		solve(fMatrix);

		fMatrix = new Syseq(	
				" 2.0i,-1.0,-1.0i,-1.0,-1.0i;"
			+	" 1.0,-2.0i, 1.0,-1.0i, 2.0;"
			+	"-1.0i,-1.0,-2.0i,-1.0,-1.0i;"
			+	"-1.0,-1.0i, 1.0,-2.0i, 2.0");
		solve(fMatrix);
		
		System.out.println(Complex.repeat("-",65));
		MatrixComplex matrix = new MatrixComplex(7,8);
		matrix.initMatrixRandomRec(5);
		System.out.println(matrix.toMatrixComplex());
		matrix.Ftransf(3, 0); 
			matrix.Ftransf(3, 1, -2);
		matrix.Ftransf(4, 0.0);
			matrix.Ftransf(4, 2, +3.0);
			matrix.Ftransf(4, 1, -1.0);
		matrix.Ftransf(5, 0.0);
			matrix.Ftransf(5, 2, -1.0);
			matrix.Ftransf(5, 4, +1.0);
		System.out.println(matrix.toMatrixComplex());
		fMatrix = new Syseq(matrix);
		solve(fMatrix);
		
	}
}
