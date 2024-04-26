/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq8
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.*;

public class TestSyseqnum01 {

	private static void printResults(String method, Syseqnum gMatrix, MatrixComplex solution) {
     	System.out.println(method + " Method " + (gMatrix.isSolved() ? "has found a solution" : "FAILS"));
     	System.out.println("Iterations:"+ gMatrix.getIterations());
     	solution.println("Numeric Solution:");
     	gMatrix.checkSol(solution);		
	}

	private static void solve(Syseq fMatrix) {
		int boxSize = 65;
		MatrixComplex solution;
		Syseqnum gMatrix = new Syseqnum(fMatrix);
		final int MAX_ITERATIONS = 500;
		Complex seed = new Complex(-10,0);
		
		System.out.println(Complex.boxTitleRandom(boxSize, "LINEAR EQUATIONS SYSTEM TEST"));
		
		//((MatrixComplex)fMatrix).println("fMatrix");
		fMatrix.print(Complex.boxTextRandom(boxSize,"Equation System " + (fMatrix.isSymmetric() ? "Symmetric" : "No symmetric")));
		
     	System.out.println(Complex.boxTextRandom(boxSize, "System Solve Commands"));
     	System.out.println("MComplex:" + fMatrix.toMatrixComplex());
		fMatrix.printSystemEqSolve(outputFormat.MAXIMA, true);
		fMatrix.printSystemEqSolve(outputFormat.OCTAVE, true);
		fMatrix.printSystemEqSolve(outputFormat.WOLFRAM, true);

		fMatrix.printSol(Complex.boxTextRandom(boxSize, "Exact System Solution"));
		switch (fMatrix.typeEqSys()) {
			case MatrixComplex.INCONSISTENT: break ;
			case MatrixComplex.DETERMINATE: {
				/**/
				fMatrix.checkSol(fMatrix.solution());
				
		     	System.out.println(Complex.boxTextRandom(boxSize, "Jacobi Method System Solution"));
		     	solution = gMatrix.jacobi(seed, MAX_ITERATIONS);
		     	printResults("Jacobi", gMatrix, solution);
		     	
		     	System.out.println(Complex.boxTextRandom(boxSize, "Gauss-Seidel Method System Solution"));
		     	solution = gMatrix.gaussSeidel(seed, MAX_ITERATIONS);
		     	printResults("Gauss-Seidel", gMatrix, solution);

		     	System.out.println(Complex.boxTextRandom(boxSize, "Successive Overrelaxation Method System Solution"));
		     	solution = gMatrix.sucor(seed, 1.01, MAX_ITERATIONS);
		     	printResults("Successive Overrelaxation", gMatrix, solution);
		     	
		     	System.out.println(Complex.boxTextRandom(boxSize, "Symmetric Successive Overrelaxation Method System Solution"));
		     	solution = gMatrix.symsucor(seed, 1.36, MAX_ITERATIONS);
		     	printResults("Symmetric Successive Overrelaxation", gMatrix, solution);

		     	System.out.println(Complex.boxTextRandom(boxSize, "Conjugate Gradient Method System Solution"));
		     	solution = gMatrix.congrad(seed, MAX_ITERATIONS);
		     	printResults("Conjugate Gradient", gMatrix, solution);
/* */
		     	System.out.println(Complex.boxTextRandom(boxSize, "Generalized Minimal Residual System Solution"));
		     	solution = gMatrix.genminres(seed, 1, MAX_ITERATIONS);
		     	printResults("Generalized Minimal Residual", gMatrix, solution);
/* */
		     	break;
			} 
			default: { // MatrixComplex.INDETERMINATE
		     	System.out.println(Complex.boxTextRandom(boxSize, "Solutions check"));				
				for (int i = -5; i < 6; ++i) {
					double n = i/10.0;
					System.out.println(Complex.repeat("*", 20) + " Sol nbr:" + i); 
					solution = fMatrix.solution(n);
					fMatrix.checkSol(solution);
				}
				
		     	System.out.println(Complex.boxTextRandom(boxSize, "Jacobi Method System Solution"));
		     	solution = gMatrix.jacobi(seed, MAX_ITERATIONS);
		     	printResults("Jacobi", gMatrix, solution);
		     	
		     	System.out.println(Complex.boxTextRandom(boxSize, "Gauss-Seidel Method System Solution"));
		     	solution = gMatrix.gaussSeidel(seed, MAX_ITERATIONS);
		     	printResults("Gauss-Seidel", gMatrix, solution);

		     	System.out.println(Complex.boxTextRandom(boxSize, "Successive Overrelaxation Method System Solution"));
		     	solution = gMatrix.sucor(seed, 1.01, MAX_ITERATIONS);
		     	printResults("Successive Overrelaxation", gMatrix, solution);
		     	
		     	System.out.println(Complex.boxTextRandom(boxSize, "Symmetric Successive Overrelaxation Method System Solution"));
		     	solution = gMatrix.symsucor(seed, 1.36, MAX_ITERATIONS);
		     	printResults("Symmetric Successive Overrelaxation", gMatrix, solution);

		     	System.out.println(Complex.boxTextRandom(boxSize, "Conjugate Gradient Method System Solution"));
		     	solution = gMatrix.congrad(seed, MAX_ITERATIONS);
		     	printResults("Conjugate Gradient", gMatrix, solution);
/*
		     	System.out.println(Complex.boxTextRandom(boxSize, "Generalized Minimal Residual System Solution"));
		     	solution = gMatrix.genminres(seed, MAX_ITERATIONS);
		     	printResults("Generalized Minimal Residual", gMatrix, solution);
*/
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedON(8);
		Syseq.version();
		Syseqnum.version();
		Complex.facts();
		
		//Syseq aMatrix = new Syseq(("-1.000,-4.000,-3.000,0.000;4.000,7.000,3.000,0.000;-3.000,-3.000,0.000,0.000"));
		//Syseq aMatrix = new Syseq("4.000,4.000,3.000,0.000;-4.000,-4.000,-3.000,0.000;3.000,3.000,3.000,0.000");
		//Syseq aMatrix = new Syseq(("-4.000,-4.000,-3.000,0.000;4.000,4.000,3.000,0.000;-3.000,-3.000,-3.000,0.000"));
		//Syseq aMatrix = new Syseq("-0.300-3.074i,-1.000i,-1.000i,0.000;1.000i,-0.300-1.074i,1.000,0.000;-1.000i,-1.000,-0.300-1.074i,0.000");
		//Syseq aMatrix = new Syseq("0.727-1.142i,-1.000i,-1.000i,0.000;1.000i,0.727+0.858i,1.000,0.000;-1.000i,-1.000,0.727+0.858i,0.000");
		//Syseq aMatrix = new Syseq("-0.427+0.216i,-1.000i,-1.000i,0.000;1.000i,-0.427+2.216i,1.000,0.000;-1.000i,-1.000,-0.427+2.216i,0.000");
		//Syseq aMatrix = new Syseq("-4.000,-4.000,-3.000,0.000;4.000,4.000,3.000,0.000;-3.000,-3.000,-3.000,0.000");

		Syseq aMatrix = new Syseq(2);
		aMatrix.initMatrixRandomInteger(5);
		/* MAKE IT SYMMETRIC */
		//aMatrix.complexMatrix = aMatrix.unkMatrix().hermitian().augment(aMatrix.indMatrix()).complexMatrix;
		//aMatrix.complexMatrix = aMatrix.triangle().complexMatrix;
		//aMatrix = new Syseq("5.000,-4.000,-5.000,-5.000;1.000,-3.000,-2.000,-4.000;-2.000,-2.000,5.000,3.000");
		//aMatrix = new Syseq("-3.000,-3.000,-1.000,-1.000;-5.000,-4.000,-3.000,5.000;4.000,1.000,4.000,1.000");
		//aMatrix = new Syseq("1.000,3.000,1.000,-3.000;3.000,-3.000,-3.000,3.000;1.000,4.000,2.000,3.000");
		//aMatrix = new Syseq("-2.000,4.000,-1.000,1.000;-1.000,2.000,-1.000,-3.000;-1.000,-5.000,-3.000,-1.000");

		//aMatrix = new Syseq("-5,3,2,-4;4,-5,-4,4;5,-1,5,-1");
		/* * /
		aMatrix = new Syseq(""
				+ " 10,  -1,  2,  0,  6;"
				+ " -1,  11, -1,  3, 25;"
				+ "  2,  -1, 10, -1,-11;"
				+ "  0,   3, -1,  8, 15");
		/**/
		/* * /
		aMatrix = new Syseq(""
				+ " 10,  -1,  2,  0,  6;"
				+ " -1,  11, -1,  3, 25;"
				+ "  2,  -1, 10, -6,-11;"
				+ " -1,   4, -7,  8, 15");
		/**/
		/*
		 * aMatrix = new Syseq("4,-5,-4,4;-5,3,2,-4;5,-1,5,-1");
		 * aMatrix = new Syseq("4,-5,-4,4;5,-1,5,-1;-5,3,2,-4");
		 * aMatrix = new Syseq("5,-1,5,-1;4,-5,-4,4;-5,3,2,-4");
		 * aMatrix = new Syseq("-5,3,2,-4;5,-1,5,-1;4,-5,-4,4");
		 */
		/* * /
		aMatrix = new Syseq(""
				+ "  5,  -1,  2,  1,  2;"
				+ " -1,   6, -3,  2,  1;"
				+ "  2,  -3,  7, -4, -5;"
				+ "  1,   2, -4,  8,  3");
		/**/

		/* * /
		aMatrix = new Syseq(""
				+ "  2,  -3, -5;"
				+ " -3,  10,  3");
		/**/

		/* * /
		aMatrix = new Syseq("-1,4,-5;5,-4,2");
		/**/

		/* * /
		aMatrix = new Syseq("-1.000,3.000,-1.000;1.000,-2.000,-3.000");
		/**/
		
		/* * /
		aMatrix = new Syseq("-5.000,1.000,2.000;-4.000,-4.000,-2.000");
		/**/
		
		
		/* * /
		aMatrix = new Syseq("-2,-2,-1;1,5,-5");
		/**/
		
		solve(aMatrix);
	}
}
