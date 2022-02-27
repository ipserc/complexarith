/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq8
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.*;

public class TestSyseq10 {

	private static void solve(Syseq fMatrix) {
		MatrixComplex solution;
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize , "LINEAR EQUATIONS SYSTEM TEST"));
		
		fMatrix.print(Complex.boxTextRandom(boxSize ,"Equation System"));
		
     	System.out.println(Complex.boxTextRandom(boxSize , "System Solve Commands"));
     	System.out.println("MComplex:" + fMatrix.toMatrixComplex());
		fMatrix.printSystemEqSolve(outputFormat.MAXIMA , true);
		fMatrix.printSystemEqSolve(outputFormat.OCTAVE , true);
		fMatrix.printSystemEqSolve(outputFormat.WOLFRAM , true);

		fMatrix.printSol(Complex.boxTextRandom(boxSize , "System Solutions"));
		switch (fMatrix.typeEqSys()) {
			case MatrixComplex.INCONSISTENT: break ;
			case MatrixComplex.DETERMINATE: {
				fMatrix.checkSol(fMatrix.solution());
				break ;
			} 
			default: { // MatrixComplex.INDETERMINATE
		     	System.out.println(Complex.boxTextRandom(boxSize , "Solutions check"));				
				for (int i = -5; i < 6; ++i) {
					double n = i/10.0;
					System.out.println(Complex.repeat("*" , 20) + " Sol nbr:" + i); 
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
		Complex.setFormatON();
		Complex.setFixedON(3);
		Syseq.version();
		
		//Syseq aMatrix = new Syseq(("-1 ,-4 ,-3 ,0;4 ,7 ,3 ,0;-3 ,-3 ,0 ,0"));
		//Syseq aMatrix  = new Syseq("4 ,4 ,3 ,0;-4 ,-4 ,-3 ,0;3 ,3 ,3 ,0");
		//Syseq aMatrix = new Syseq(("-4 ,-4 ,-3 ,0;4 ,4 ,3 ,0;-3 ,-3 ,-3 ,0"));
		//Syseq aMatrix = new Syseq("-0.300-3.074i ,-1i ,-1i ,0;1i ,-0.300-1.074i ,1 ,0;-1i ,-1 ,-0.300-1.074i ,0");
		//Syseq aMatrix = new Syseq("0.727-1.142i ,-1i ,-1i ,0;1i ,0.727+0.858i ,1 ,0;-1i ,-1 ,0.727+0.858i ,0");
		//Syseq aMatrix = new Syseq("-0.427+0.216i ,-1i ,-1i ,0;1i ,-0.427+2.216i ,1 ,0;-1i ,-1 ,-0.427+2.216i ,0");
		//Syseq aMatrix = new Syseq("-4 ,-4 ,-3 ,0;4 ,4 ,3 ,0;-3 ,-3 ,-3 ,0");
		Syseq aMatrix  = new Syseq(""
				+ " 1 ,-1 ,-1 , 1 ,0;"
				+ " 1 , 1 ,-1 ,-1 ,0;"
				+ "-1 ,-1 , 1 , 1 ,0;"
				+ " 1 ,-1 ,-1 ,-1 ,0");

		solve(aMatrix);
		
		aMatrix  = new Syseq(""
				+ " 1 ,-1 ,-1 , 1 ,0;"
				+ " 0 ,-2 ,-0 , 0 ,0;"
				+ "-0 ,-0 , 0 , 0 ,0;"
				+ " 0 ,-0 , 0 , 2 ,0");

		solve(aMatrix);
		
		aMatrix  = new Syseq(""
				+ " 1 ,-1 ,-1 , 1 ,0;"
				+ " 0 ,-2 ,-0 , 0 ,0;"
				+ "-0 ,-0 , 0 , 2 ,0;"
				+ " 0 ,-0 ,-0 ,-0 ,0");

		solve(aMatrix);

	}
}
