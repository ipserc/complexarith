package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.syseq.*;

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
		fMatrix.print("Equation System");
		fMatrix.homogeneous().println("Homogeneous Equation System");
		fMatrix.printSol();
		if (fMatrix.typeEqSys() == MatrixComplex.INCOMPATIBLE) return ;
		for (int i = -5; i < 6; ++i) {
			double n = i/10.0;
			System.out.println("*".repeat(20) + " Sol nbr:" + i); 
			solution = fMatrix.solution(n);
			checkSol(fMatrix, solution);
		}		
		System.out.println(".".repeat(55));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Syseq fMatrix;

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

	}
}
