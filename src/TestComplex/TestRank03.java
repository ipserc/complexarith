package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;

public class TestRank03 {
	private static boolean Reduced = true;

	private static void showResults(MatrixComplex aMatrix) {
		Chronometer chrono = new Chronometer();
		int rank0, rank1;
		
		/**/
		chrono.start();
		rank0 = aMatrix.rank0(Reduced);
		System.out.println("Rank0(aMatrix)  :" + rank0);
		chrono.stop();
		System.out.println("Tiempo: " + chrono.toString());
		/**/
		System.out.println("OCTAVE : rank("+aMatrix.toOctave()+")");
		chrono.start();
		rank1 = aMatrix.rank1(Reduced);
		System.out.println("Rank1(aMatrix) :" + rank1);
		chrono.stop();
		System.out.println("Tiempo: " + chrono.toString());
		System.out.println("Nullity(aMatrix) :" + aMatrix.nullity(Reduced));
		if (rank0 != rank1) aMatrix.println("Matrix");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix;
		int filas = 4;
		int columnas = 5;
		aMatrix = new MatrixComplex(filas,columnas);
		int boxSize = 65;
		
		Complex.setFormatON();
		Complex.setFixedON(2);

		System.out.println(Complex.boxTitleRandom(boxSize, "RANK TEST"));
		System.out.println("Testing with arrays of " +filas+"x"+columnas);
		for(int i = 1; i < 100; ++i) {
			System.out.println(Complex.boxTextRandom(boxSize, "TEST #"+i));   	
			aMatrix.initMatrixRandomInteger(1);
			//aMatrix.abs();
			aMatrix.println("Matrix");
			showResults(aMatrix);
		}
	}
}
