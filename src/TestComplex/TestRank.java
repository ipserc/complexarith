package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;

public class TestRank {

	private static void showResults(MatrixComplex aMatrix) {
		aMatrix.println("aMatrix");
		System.out.println("Complexarith:\n"+aMatrix.toMatrixComplex());
		System.out.println("Wolfram:\n"+aMatrix.toWolfram());
		aMatrix.triangle().println("aMatrix Triang");
		aMatrix.triangleLo().println("aMatrix Triang lo");
		System.out.println("RankDown(aMatrix):" + aMatrix.rankDown());
		System.out.println("RankUp(aMatrix)  :" + aMatrix.rankUp());
		System.out.println("Nullity(aMatrix) :" + aMatrix.nullity());
		System.out.println("------------------------------------");		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix;
		
		Complex.setFormatON();
		Complex.setFixedON(2);

		/**/
		aMatrix = new MatrixComplex("-2,-1,1,3;-4,-2,2,6;-3,-1,2,3;-5,-3,1,6");
		//aMatrix = new MatrixComplex("-2,-1,1,3;-3,-1,2,3;-5,-3,1,6;-4,-2,-2,6");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex("-2,-1,1,3;-4,-2,-2,6;-3,-1,2,3;-5,-3,1,6");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex("2,-1,-1,-2; 2,-3,-1, 0; 2, 2, -3, 1; 2, 0, -3, 4");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(" 2, 2, 1; 0, 0, 1; 1, 0, 1");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(" 2,-1,-1,-2; 4,-2, 2,-4; 2, 2,-3, 1; -6, 6,-11, 9");
		showResults(aMatrix);	

		aMatrix = new MatrixComplex(" -1, 3, 0,1; 0, 0,-3, 4; 0, 1, 0,-2; 1, 0, 0, 0");
		showResults(aMatrix);		

		aMatrix = new MatrixComplex(4);
		aMatrix.initMatrixRandomRecInt(9);
		showResults(aMatrix);
		

		aMatrix = new MatrixComplex(" 0, 0, 1, 0; 0, 0, 0, 1; 1, 0, 0, 0; 0, 1, 0, 0");
		showResults(aMatrix);
		
		/**/
		aMatrix = new MatrixComplex("-2,-5,8,0,-17;1,3,-5,1,8;3,11,-19,7,1;1,7,-13,5,-3");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("-3,6,-1,1,-7;1,-2,2,3,-1;2,-4,5,8,-4");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex("1,3,5,7,9;2,4,6,8,10;3,7,11,15,19;-10,-8,-6,-4,-2");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("-1,1,-7;2,3,-1;5,8,-4");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("1,1,1,1;1,1,-1,-1;1,1,-1,-1;1,1,1,1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex(  " 1, 2, 3, 4;"
									+ " 2, 0,-1, 3;"
									+ " 5, 6, 8,15;"
									+ "13,18,25,42");		
		showResults(aMatrix);

		/**/

		aMatrix = new MatrixComplex(
				"-1.00, 1.00, 1.00,-1.00, 1.00, 1.00,-1.00, 1.00;"+
				"-1.00,-1.00, 1.00,-1.00, 1.00, 1.00, 1.00, 1.00;"+
				"-1.00, 1.00, 1.00,-1.00, 1.00, 1.00, 1.00, 1.00;"+
				"-1.00, 1.00, 1.00, 1.00, 1.00, 1.00,-1.00, 1.00;"+
				"-1.00, 1.00, 1.00,-1.00, 1.00,-1.00, 1.00, 1.00;"+
				"-1.00,-1.00, 1.00,-1.00, 1.00,-1.00, 1.00, 1.00;"+
				" 1.00, 1.00,-1.00,-1.00,-1.00, 1.00,-1.00, 1.00;"+
				" 1.00,-1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00");
		showResults(aMatrix);

		aMatrix = new MatrixComplex(8); aMatrix.initMatrixRandomInteger(1);
		showResults(aMatrix);

		aMatrix = new MatrixComplex(
				  "-1.00,-1.00, 1.00,-1.00, 1.00, 1.00, 1.00,-1.00;"
				+ " 1.00, 1.00, 1.00,-1.00, 1.00,-1.00,-1.00, 1.00;"
				+ "-1.00,-1.00, 1.00,-1.00,-1.00,-1.00,-1.00,-1.00;"
				+ " 1.00,-1.00,-1.00, 1.00, 1.00,-1.00,-1.00,-1.00;"
				+ " 1.00, 1.00, 1.00, 1.00,-1.00, 1.00, 1.00, 1.00;"
				+ " 1.00,-1.00,-1.00,-1.00,-1.00, 1.00, 1.00, 1.00;"
				+ "-1.00, 1.00,-1.00,-1.00,-1.00, 1.00, 1.00,-1.00;"
				+ " 1.00,-1.00, 1.00, 1.00,-1.00, 1.00, 1.00, 1.00");
		showResults(aMatrix);

		new MatrixComplex(
				    "-1.00, 1.00,-1.00, 1.00, 1.00,-1.00, 1.00,-1.00;"
				  + "-1.00,-1.00,-1.00,-1.00, 1.00,-1.00,-1.00,-1.00;"
				  + "-1.00, 1.00,-1.00,-1.00,-1.00,-1.00, 1.00, 1.00;"
				  + " 1.00, 1.00,-1.00,-1.00,-1.00, 1.00,-1.00, 1.00;"
				  + " 1.00, 1.00,-1.00, 1.00, 1.00, 1.00,-1.00,-1.00;"
				  + "-1.00,-1.00, 1.00,-1.00, 1.00, 1.00, 1.00, 1.00;"
				  + "-1.00, 1.00,-1.00, 1.00, 1.00,-1.00,-1.00, 1.00;"
				  + " 1.00,-1.00, 1.00,-1.00,-1.00, 1.00,-1.00, 1.00");
		showResults(aMatrix);

		aMatrix = new MatrixComplex(
				"  1,  2, -3, -1;" +
				" -3, -6,  9,  3;" +
				"  5, 10,-15, -5;" +
				"-10,-20, 30, 10" );
		showResults(aMatrix);


	}
}
