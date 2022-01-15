package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;

public class TestRankCheck01 {
	private static boolean Reduced = true;

	private static void showResults(MatrixComplex aMatrix, int i) {
		Chronometer chrono = new Chronometer();
		String chrono0, chrono1;
		int rank0, rank1;
		int boxSize = 65;
		
		/**/
		chrono.start();
		rank0 = aMatrix.rank0(Reduced);
		chrono.stop();
		chrono0 = chrono.toString();
		/**/
		chrono.start();
		rank1 = aMatrix.rank1(Reduced);
		chrono.stop();
		chrono1 = chrono.toString();
		if (rank0 != rank1) {
			System.out.println(Complex.boxTextRandom(boxSize, "TEST #"+i));
			aMatrix.println("Matrix");
			System.out.println("Rank0(aMatrix)  :" + rank0);
			System.out.println("Tiempo: " + chrono0);
			System.out.println("Rank1(aMatrix) :" + rank1);
			System.out.println("Tiempo: " + chrono1);
			System.out.println("OCTAVE : rank("+aMatrix.toOctave()+")");
			System.out.println("Nullity(aMatrix) :" + aMatrix.nullity(Reduced));
		}

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix;
		int filas = 4;
		int columnas = 5;
		int seed = 1;
		aMatrix = new MatrixComplex(filas,columnas);
		int boxSize = 65;
		int numTests = 50000;
		Chronometer chrono = new Chronometer();
		
		chrono.start();
		Complex.setFormatON();
		Complex.setFixedON(3);

		System.out.println(Complex.boxTitleRandom(boxSize, "RANK TEST: " + numTests));
		System.out.println("Testing with arrays of " +filas+"x"+columnas+" - seed:"+seed);
		for(int i = 1; i <= numTests; ++i) {
			aMatrix.initMatrixRandomInteger(seed);
			//aMatrix.abs();
			//aMatrix.println("Matrix");
			showResults(aMatrix, i );
		}
		chrono.stop();
		System.out.println(Complex.boxTextRandom(boxSize, "END TEST - Time:" + chrono.toString()));
	}
}
