/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq1
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.syseq.*;

public class TestSyseq2 {

	private static void checkSol(MatrixComplex fMatrix, MatrixComplex solution) {
		MatrixComplex indTerm = fMatrix.indMatrix().transpose();
		MatrixComplex uknMatix = fMatrix.unkMatrix().transpose();
		
		indTerm.println                 ("Independent Terms");
		solution.println                ("Solution         ");
		solution.times(uknMatix).println("Check            ");
	}

	private static void solve(Syseq fMatrix) {
		MatrixComplex solution;	
		System.out.println(".".repeat(55));
		fMatrix.print("Equation System");
		//System.out.println("Maxima:" + fMatrix.toMaxima());
		fMatrix.printSystemEqSolve(outputFormat.WOLFRAM, true); 
		fMatrix.printSystemEqSolve(outputFormat.OCTAVE, true); 
		fMatrix.printSystemEqSolve(outputFormat.MAXIMA, true); 
		//fMatrix.homogeneous().println("Homogeneous Equation System");
		fMatrix.printSol();

		switch (fMatrix.typeEqSys()) {
			case MatrixComplex.INCONSISTENT: break ;
			case MatrixComplex.DETERMINATE: {
				checkSol(fMatrix, fMatrix.solution());
				break ;
			} 
			default: { // MatrixComplex.INDETERMINATE
				for (int i = -5; i < -6; ++i) {
					double n = i/10.0;
					System.out.println("*".repeat(20) + " Sol nbr:" + i); 
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
		
     	Complex.setFormatON();
     	//Complex.setFixedON(3);

     	fMatrix.version();

     	/**/
     	fMatrix = new Syseq(""+
     			"  1.000 ,  1.000 ,  1.000 , 0;" +
     			" -1.000 , -1.000 , -1.000 , 0;" +
     			"  1.000 ,  1.000 , -1.000 , 0");
		solve(fMatrix);
		/**/
     	
		/**/
		fMatrix = new Syseq(""+
				"	-1.000,	-1.000,	-1.000,	0.000;"
				+ "	 1.000,	 1.000,	 1.000,	0.000;"
				+ "	-1.000,	-1.000,	 1.000,	0.000");
		solve(fMatrix);
		/**/
		
		/**/
		fMatrix = new Syseq(""+
				"	-2.000,	-1.000,	-1.000,	0.000;"
				+ "	 1.000,	 0.000,	 1.000,	0.000;"
				+ "	-1.000,	-1.000,	 0.000,	0.000");
		solve(fMatrix);
		/**/
	}
}
