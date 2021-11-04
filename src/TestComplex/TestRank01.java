package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;

public class TestRank01 {

	private static void showResults(MatrixComplex aMatrix) {
		int boxSize = 65;
		Chronometer chrono = new Chronometer();
		
		System.out.println(Complex.boxTitleRandom(boxSize, "RANK TEST"));   	
		aMatrix.println("aMatrix");
		System.out.println("CMPLXAR: "+aMatrix.toMatrixComplex());
		System.out.println("MAXIMA : rank("+aMatrix.toMaxima()+")");
		System.out.println("OCTAVE : rank("+aMatrix.toOctave()+")");
		System.out.println("WOLFRAM: rank("+aMatrix.toWolfram()+")");
		aMatrix.triangle().println("aMatrix Triang");
		aMatrix.triangleLo().println("aMatrix Triang lo");
		chrono.start();
		System.out.println("Rank0(aMatrix)  :" + aMatrix.rank0());
		chrono.stop();
		System.out.println("Tiempo: " + chrono.toString());
		chrono.start();
		System.out.println("Rank1(aMatrix) :" + aMatrix.rank1());
		chrono.stop();
		System.out.println("Tiempo: " + chrono.toString());
		System.out.println("Nullity(aMatrix) :" + aMatrix.nullity());
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
		
		aMatrix = new MatrixComplex(
				"-2,-5,8,0,-17;"
				+ "1,3,-5,1,8;"
				+ "3,11,-19,7,1;"
				+ "1,7,-13,5,-3");
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
				" -3, -5,  9,  3;" +
				"  5,  9,-15, -5;" +
				"-10,-14, 14,  2" );
		showResults(aMatrix);

		aMatrix = new MatrixComplex(
				"  1,  2, -3, -1;" +
				"  5,  9,-15, -5;" +
				"-10,-14, 14,  2" );
		showResults(aMatrix);

		aMatrix = new MatrixComplex(
				"  2, -3, -1;" +
				" -5,  9,  3;" +
				"  9,-15, -5;" +
				"-14, 14,  2" );
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(
				" 1, 2, -3,-1, 2, 3;"+
				"-3,-5,  9, 3,-1,-8;"+
				" 5, 9,-15,-5, 3,14;"+
				" 2, 4, -6,-2, 3, 6" );
		showResults(aMatrix);
		/**/
		
		aMatrix = new MatrixComplex(
				"1, -3, 5, 2;"+
				"2, -5, 9, 4;"+
				"-3, 9, -15, -6;"+
				"-1, 3, -5, -2;"+
				"2, -1, 3, 3;"+
				"3, -8, 14, 6");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(
					"1.000,-1.000,1.000,1.000,-1.000;"
				+ 	"1.000,-1.000,-1.000,1.000,-1.000;"
				+ 	"1.000,-1.000,-1.000,1.000,-1.000;"
				+ 	"-1.000,-1.000,1.000,1.000,-1.000;"
				+ 	"-1.000,-1.000,1.000,-1.000,-1.000");
		showResults(aMatrix);

		aMatrix = new MatrixComplex(
				"1.000,-1.000,1.000,1.000,-1.000;"
			+ 	"1.000,-1.000,-1.000,1.000,-1.000;"
			+ 	"1.000,-1.000,-1.000,1.000,1.000;"
			+ 	"-1.000,-1.000,1.000,1.000,1.000;"
			+ 	"-1.000,-1.000,1.000,-1.000,1.000");
		showResults(aMatrix);
	}
}
