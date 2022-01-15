package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Jordan;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class solveV4 extends MatrixComplex {
	private final static String HEADINFO = "solveV4 --- INFO:";

	public static void main(String[] args) {
		MatrixComplex equationSystem;
		MatrixComplex solutions;
		boolean Reduced = true;
		
		equationSystem = new MatrixComplex(   " 2,-1, 2, 3;"
											+ " 1, 0,-1,-1;"
											+ " 3,-1, 1, 2");

		equationSystem = new MatrixComplex(	  " 2,-1, 2, 3;"
											+ " 1, 0,-1,-1;"
											+ " 1, 0,-1,-1");
		
		equationSystem = new MatrixComplex(	  " 1, 0,-1,-1;"
											+ " 1, 0,-1,-1;"
											+ " 1, 0,-1,-1");

		equationSystem = new MatrixComplex(	  " 1, 2,-3, 1, 4;"
											+ " 0,-1, 2, 2,-1;"
											+ " 1, 1,-1, 3, 3;"
											+ " 1, 1,-1, 3, 3");

		equationSystem.triangle().println("***** TRIANGULO");
		equationSystem.solve(Complex.ONE, Reduced).println("***** SOLUTIONS");
	}

}
