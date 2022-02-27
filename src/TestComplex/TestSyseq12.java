/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.TestSyseq8
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.*;

public class TestSyseq12 {
	private static int boxSize = 65;


	private static void solve(Syseq fMatrix) {
		MatrixComplex solution;
		
		System.out.println(Complex.boxTitleRandom(boxSize , "LINEAR EQUATIONS SYSTEM TEST"));
		
		fMatrix.print(Complex.boxTextRandom(boxSize ,"Equation System"));
		
     	System.out.println(Complex.boxTextRandom(boxSize , "System Solve Commands"));
     	System.out.println("MComplex:" + fMatrix.toMatrixComplex());
		fMatrix.printSystemEqSolve(outputFormat.MAXIMA , true);
		fMatrix.printSystemEqSolve(outputFormat.OCTAVE , true);
		fMatrix.printSystemEqSolve(outputFormat.WOLFRAM , true);

		//fMatrix.solveq(new Complex(0,1)); // Calculate the solutions in Complex field
		fMatrix.printSol(Complex.boxTextRandom(boxSize , "System Solutions"));
		switch (fMatrix.typeEqSys()) {
			case MatrixComplex.INCONSISTENT: break ;
			case MatrixComplex.DETERMINATE: {
				fMatrix.checkSol(fMatrix.solution());
				break ;
			} 
			default: { // MatrixComplex.INDETERMINATE
		     	System.out.println(Complex.boxTextRandom(boxSize , "Solutions check"));				
				for (int i = -3; i <= 3; ++i) {
					double n = i/10.0;
					System.out.println(Complex.repeat("*" , 20) + " Sol nbr:" + i); 
					solution = fMatrix.solution(n);
					fMatrix.checkSol(solution);
				}		
			}
		}
	}
	
	/**
	 * Taken form https://www.geeksforgeeks.org/java-math-random-method-examples/
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
		aMatrix.initMatrixRandomInteger(5);
		
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
		Complex.setFixedON(3);
		Syseq.version();

		System.out.println(Complex.boxTitleRandom(boxSize , "PROOF OF CONCEPT FOR LINEAR EQUATION SYSTEMS INDETERMINATE WITH MORE THAN ONE SOLUTION"));

		aMatrix = generateSyseq(6);
		/* * /
		aMatrix = new Syseq(""
				+ "-3.000,-1.000,-2.000,9.000,7.000,-4.000,0.000;"
				+ "204.000,68.000,-204.000,-272.000,-140.000,272.000,0.000;"
				+ "0.000,0.000,-1.000,1.000,1.000,0.000,0.000;"
				+ "-3.000,-1.000,3.000,4.000,2.000,-4.000,0.000;"
				+ "-90.000,-30.000,90.000,120.000,62.000,"
				+ "-120.000,0.000;0.000,0.000,0.000,0.000,0.000,0.000,0.000");
		/* */
		/* * /
		aMatrix = new Syseq(""
				+ "3.000,4.000,5.000,3.000,1.000,1.000,0.000;"
				+ "-12.000,-16.000,-20.000,-12.000,-4.000,-4.000,0.000;"
				+ "18.000,24.000,21.000,12.000,18.000,6.000,0.000;"
				+ "-15.000,-20.000,-25.000,-11.000,-13.000,-5.000,0.000;"
				+ "3.000,4.000,5.000,3.000,1.000,1.000,0.000;"
				+ "-18.000,-24.000,-30.000,-18.000,-6.000,-6.000,0.000");
		/* */
		/* * /
		aMatrix = new Syseq(""
				+ "-6.000,10.000,6.000,4.000,-4.000,-10.000,0.000;"
				+ "-99.000,165.000,99.000,72.000,-76.000,-165.000,0.000;"
				+ "-18.000,30.000,18.000,12.000,-12.000,-30.000,0.000;"
				+ "0.000,0.000,0.000,3.000,-5.000,0.000,0.000;"
				+ "-27.000,45.000,27.000,18.000,-18.000,-45.000,0.000;"
				+ "0.000,0.000,0.000,0.000,0.000,0.000,0.000");
		/* */
		
		/* * /
    	aMatrix = new Syseq(""
    			+ "-5.000,-5.000,1.000,-3.000,-5.000,-4.000,0.000;"
    			+ "30.000,30.000,-6.000,18.000,30.000,24.000,0.000;"
    			+ "-50.000,-50.000,14.000,-42.000,-68.000,-40.000,0.000;"
    			+ "-20.000,-20.000,4.000,-12.000,-20.000,-16.000,0.000;"
    			+ "-50.000,-50.000,10.000,-30.000,-52.000,-40.000,0.000;"
    			+ "20.000,20.000,-4.000,12.000,20.000,16.000,0.000");
    	/* */

		/* * /
    	aMatrix = new Syseq(""
    			+ "-5.000,4.000,-3.000,-2.000,1.000,2.000,0.000;"
    			+ "0.000,0.000,0.000,0.000,0.000,0.000,0.000;"
    			+ "-40.000,32.000,-24.000,-16.000,8.000,16.000,0.000;"
    			+ "-55.000,44.000,-33.000,-22.000,11.000,22.000,0.000;"
    			+ "-40.000,32.000,-24.000,-16.000,7.000,16.000,0.000;"
    			+ "-20.000,16.000,-12.000,-8.000,4.000,8.000,0.000");
		/* */

		solve(aMatrix);
	}
}
