/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq6
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.*;

public class TestSyseq6 {

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
	
	private static Syseq selectSyseq(int selector) {
		Syseq fMatrix = new Syseq();

		switch (selector) {
		case 1:
			fMatrix = new Syseq(
					" 1,-2, 3 ,1; "
				  + "-2, 5,-1, 2; "
				  + " 4,-9, 7, 0");
			break;
		case 2:
			fMatrix = new Syseq(  
					  " 1, 2, 1, -1;"
		            + " 2, 4, 2, -2;"
		            + " 3, 6, 3, -3");
			break;
		case 3:
			fMatrix = new Syseq(   
					   " 1, -3,  4,  2,-1,  3;"
					+  " 3, -9, 12,  6,-3,  9;"
					+  "-1, -5,  2,  0,-3,  1;"
					+  " 4,-12, 16,  8,-4, 12;"
					+  "-5, 15,-20,-10, 5,-15;");
			break;
		case 4:
			fMatrix = new Syseq(""+
					  "-1,-1, 1,-1, 1, 1;"
					+ "-1,-1, 1,-1,-1,-1;"
					+ "-1,-1, 1,-1, 1, 1;"
					+ "-1, 1,-1, 1, 1, 1;"
					+ " 1,-1,-1,-1, 1, 1");
			break;
		case 5:
			fMatrix = new Syseq(""+
					  "-1,-1, 1,-1, 1;"
					+ "-1,-1, 1,-1, 1;"
					+ "-1,-1, 1,-1, 1;"
					+ "-1, 1,-1, 1, 1");
			break;
		case 6:
			fMatrix = new Syseq(""+
					  "-1,-1, 1,-1;"
					+ "-1,-1, 1,-1;"
					+ "-1, 1,-1, 1");
			break;
		case 7:
			fMatrix = new Syseq(""+
					  "-1,-1, 1;"
					+ "-1,-1, 1");
			break;
		case 8:
			fMatrix = new Syseq(""+
					  "-1,-1, 1,-1, 1, 0;"
					+ "-1,-1, 1,-1,-1,-0;"
					+ "-1,-1, 1,-1, 1, 0;"
					+ "-1, 1,-1, 1, 1, 0;"
					+ " 1,-1,-1,-1, 1, 0");
			break;
		case 9:
			fMatrix = new Syseq(""+
					  " 1,-1,-1, 1, 1;"
					+ " 1, 1,-1,-1,-1;"
					+ "-1,-1, 1, 1, 1;"
					+ " 1,-1,-1,-1,-1");
			break;
		case 10:
			fMatrix = new Syseq(""+
					  " 1, 2, 3, 4, 1;"
					+ " 1, 2, 3, 4, 1;"
					+ " 1, 2, 3, 4, 1;"
					+ " 1, 2, 3, 4, 1");
			break;
		case 11:
			fMatrix = new Syseq(""+
	     			"  4 , -1 ,  1 , 0;" +
	     			"  0 ,  1 ,  3 , 0 ;" +
	     			"  0 ,  2 ,  2 , 0");
			break;
		case 12:
			fMatrix = new Syseq(""+
					  "0, 1,-1 ,0;"
					+ "0, 1,-3, 0;"
					+ "0,-2, 2, 0");
			break;
		case 13:
			fMatrix = new Syseq(""+
					  " 3, 1,-1;"
					+ "-6,-2, 2");
			break;
		case 14:
			fMatrix = new Syseq(""+
					  " 1.0, 1.0,-1.0, 0.0;"
					+ " 0.0, 2.0,-3.0, 0.0;"
					+ " 0.0,-2.0, 3.0, 0.0");
			break;
		case 15:
			fMatrix = new Syseq(""+
					  " 0.0, 1.0,-1.0, 0.0;"
					+ " 0.0, 1.0,-3.0, 0.0;"
					+ " 0.0,-2.0, 2.0, 0.0");
			break;
		case 16:
			fMatrix = new Syseq(""+
					  "-4.0, 1.0,-1.0, 0.0;"
					+ " 0.0,-3.0,-3.0, 0.0;"
					+ " 0.0,-2.0,-2.0, 0.0");
			break;
		case 17:
			fMatrix = new Syseq(
					  "-1.000,-1.000,-1.000, 0.000;"
					+ " 1.000, 1.000, 1.000, 0.000;"
					+ "-1.000,-1.000, 1.000, 0.000");
			break;
		}
		return fMatrix;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Syseq fMatrix;
		
		Syseq.version();

		Complex.setFormatON();
		Complex.setFixedON(3);
		
		fMatrix = selectSyseq(6);
		solve(fMatrix);

	}
}
