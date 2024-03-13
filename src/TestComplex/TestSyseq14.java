/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq8
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.*;

public class TestSyseq14 {
	private static boolean Reduced = true;
	private static int boxSize = 65;


	private static void solve(Syseq fMatrix) {
		MatrixComplex solution;
		
		System.out.println(Complex.boxTitleRandom(boxSize , "LINEAR EQUATIONS SYSTEM TEST"));
    	Complex.facts();
		
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
				for (int i = -10; i < 11; ++i) {
					double n = i/10.0;
					System.out.println(Complex.repeat("*" , 20) + " Sol nbr:" + i); 
					solution = fMatrix.solution(n);
					fMatrix.checkSol(solution);
				}		
			}
		}
	}
	
	/**
	 * Taken from https://www.geeksforgeeks.org/java-math-random-method-examples/
	 * @param max
	 * @return
	 */
    public static int random(int max)
    {
        // define the range
        int min = 0;
        int range = max - min + 1;
        return (int)(Math.random() * range) + min;
    }

    public static Syseq generateSyseq(int nbrUnknowns) {
    	Syseq aMatrix;
		int nbrNullRows = nbrUnknowns-2;
		
		aMatrix = new Syseq(nbrUnknowns);
		aMatrix.initMatrixRandomPolInt(7);
		
		//aMatrix.setItem(0, aMatrix.cols()-1, Complex.ZERO);
		for (int row = 1; row < aMatrix.rows(); ++row) {
			//aMatrix.setItem(row, aMatrix.cols()-1, Complex.ZERO);
			for (int col = 0; col < row; ++col) {
				aMatrix.setItem(row, col, Complex.ZERO);
			}
			//aMatrix.setItem(row, aMatrix.rows()-1, Complex.ZERO);
		}
		
		for (int nullRow = 0; nullRow < nbrNullRows; ++nullRow) {
			int row = random(nbrNullRows)+1;
			for (int col = 0; col < aMatrix.cols(); ++col) {
				aMatrix.setItem(row, col, Complex.ZERO);
			}
		}
		aMatrix.heap();
		
		for (int row = 1; row < aMatrix.rows(); ++row) {
			int aRow = random(nbrNullRows)+1;
			int coef = random(5)+1; coef = random(1) == 1 ? coef : -coef;
			aMatrix.Ftransf(aRow, 0, coef);
		}
		
		for (int row = 0; row < aMatrix.rows(); ++row) {
			int aRow = random(aMatrix.rows()-1);
			int coef = random(5)+1; coef = random(1) == 1 ? coef : -coef;
			aMatrix.Ftransf(aRow, row, coef);
		}
		return aMatrix;
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Syseq aMatrix;
		Complex.setFormatON();
		Complex.setFixedON(4);
		Syseq.version();

		System.out.println(Complex.boxTitleRandom(boxSize , "PROOF OF CONCEPT FOR COMPLEX LINEAR EQUATION SYSTEMS INDETERMINATE WITH MORE THAN ONE SOLUTION"));

		aMatrix = generateSyseq(5);
		
		/* * /
		aMatrix = new Syseq(""
				+ "54.555-12.638i,-54.142-14.306i,4.848+83.860i,-5.949-27.361i,143.671-124.751i,-52.218+104.458i;"
				+ "0,0,11.985+0.592i,22.926-19.349i,-14.331+10.892i,-3.634+4.774i;"
				+ "0,0,1.998+0.099i,3.821-3.225i,-2.388+1.815i,-0.606+0.796i;"
				+ "0,0,0,0,0,0;"
				+ "17.536-4.062i,-17.403-4.598i,1.558+26.955i,-1.912-8.795i,46.271-39.904i,-16.848+33.608i");
		/* */
		
		//aMatrix = new Syseq("-3.5000-4.2131i,60,40,00;10,3.5000-4.2131i,-40,00;-60,30,5.5000-4.2131i,00");
		//aMatrix = new Syseq("-3.5000+4.2131i,60,40,00;10,3.5000+4.2131i,-40,00;-60,30,5.5000+4.2131i,00");
		//aMatrix = new Syseq("-90,60,40,00;10,-20,-40,00;-60,30,00,00");

		//aMatrix = new Syseq("0,1,-1,0;0,3,-3,0;0,-2,2,0");
		
		/* * /
     	aMatrix = new Syseq(""+
     			"  4 , -1 ,  6, 0 ;" +
     			"  2 ,  1 ,  6, 0 ;" +
     			"  2 , -1 ,  8, 0 ");
     	/* */
     	
		/* * /
     	aMatrix = new Syseq(""+
     			"  4 , -1 ,  1, 0 ;" +
     			"  0 ,  1 ,  3, 0 ;" +
     			"  0 ,  2 ,  2, 0 ");
     	/* */
     	
     	/* * /
     	aMatrix = new Syseq(""
     			+ "-2,1,-6,0;"
     			+ "-2,1,-6,0;"
     			+ "-2,1,-6,0");
     	/* */

		/* * /
		// [x0=%r6,x1=%r6,x2=%r6] 
     	aMatrix = new Syseq("5,1,-6,0;-2,8,-6,0;-2,1,1,0");
     	/* */

		/* * /
		// [x0=0.5*%r8-3.0*%r7,x1=%r8,x2=%r7]
     	aMatrix = new Syseq("-2,1,-6,0;-2,1,-6,0;-2,1,-6,0");
     	/* */

		/* * /
		aMatrix = new Syseq(""
				+ "1,3,0,0,0,0;"
				+ "0,8,-4,0,0,0;"
				+ "0,0,3,1,0,0;"
				+ "0,0,0,1,3,0;"
				+ "1,0,0,0,3,0");
		/* */
		
		/* * /
		aMatrix = new Syseq("-1+1i,3,0,0,0,0;0,6+1i,-4,0,0,0;0,0,1+1i,1,0,0;0,0,0,-1+1i,3,0;1,0,0,0,1+1i,0");
		/* */
		
		/* * /
		//Particular  Solution [ 1.0000 , 1.0000 , 1.0000 , 1.0000 , 1.0000 ]
		aMatrix = new Syseq("-3,3,0,0,0,0;0,4,-4,0,0,0;0,0,-1,1,0,0;0,0,0,-3,3,0;1,0,0,0,-1,0");
		/* */
		
		/* * /
		aMatrix = new Syseq("-1-1i,3,0,0,0,0;0,6-1i,-4,0,0,0;0,0,1-1i,1,0,0;0,0,0,-1-1i,3,0;1,0,0,0,1-1i,0");
		/* */
		
		/* * /
		aMatrix = new Syseq("-7,3,0,0,0,0;0,0,-4,0,0,0;0,0,-5,1,0,0;0,0,0,-7,3,0;1,0,0,0,-5,0");
		/* */

		//aMatrix = new Syseq("-1.8477E+00-4.4868E+00i,3.0000E+00,4.0000E+00,-1.0000E+00,0.0000E+00;4.0000E+00,-6.8477E+00-4.4868E+00i,5.0000E+00,-5.0000E+00,0.0000E+00;-3.0000E+00,3.0000E+00,-1.8477E+00-4.4868E+00i,1.0000E+00,0.0000E+00;4.0000E+00,4.0000E+00,3.0000E+00,1.5232E-01-4.4868E+00i,0.0000E+00");
		//aMatrix = new Syseq("7.0907E+00,3.0000E+00,4.0000E+00,-1.0000E+00,0.0000E+00;4.0000E+00,2.0907E+00,5.0000E+00,-5.0000E+00,0.0000E+00;-3.0000E+00,3.0000E+00,7.0907E+00,1.0000E+00,0.0000E+00;4.0000E+00,4.0000E+00,3.0000E+00,9.0907E+00,0.0000E+00");
		//aMatrix = new Syseq("-3.9533E-01,3.0000E+00,4.0000E+00,-1.0000E+00,0.0000E+00;4.0000E+00,-5.3953E+00,5.0000E+00,-5.0000E+00,0.0000E+00;-3.0000E+00,3.0000E+00,-3.9533E-01,1.0000E+00,0.0000E+00;4.0000E+00,4.0000E+00,3.0000E+00,1.6047E+00,0.0000E+00");
		aMatrix = new Syseq(""
				+ " 5.00000 , 1.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 5.00000 ;"
				+ " 1.00000 , 3.00000 , 0.50000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 9.60000 ;"
				+ " 0.00000 , 0.50000 , 3.00000 , 1.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , -24.00000 ;"
				+ " 0.00000 , 0.00000 , 1.00000 , 6.00000 , 2.00000 , 0.00000 , 0.00000 , 0.00000 , 6.00000 ;"
				+ " 0.00000 , 0.00000 , 0.00000 , 2.00000 , 8.00000 , 2.00000 , 0.00000 , 0.00000 , -3.75000 ;"
				+ " 0.00000 , 0.00000 , 0.00000 , 0.00000 , 2.00000 , 6.00000 , 1.00000 , 0.00000 , 30.75000 ;"
				+ " 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 1.00000 , 5.60000 , 1.80000 , 12.66667 ;"
				+ " 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 1.80000 , 6.00000 , -2.08333 ;");
		solve(aMatrix);
	}
}
