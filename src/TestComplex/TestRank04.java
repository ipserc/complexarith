package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;

public class TestRank04 {

	private static void showResults(MatrixComplex aMatrix) {
		Chronometer chrono = new Chronometer();
		int rank0, rank1, rank2;
		
		System.out.println("TRUE Rank(aMatrix)  :" + aMatrix.rank());		
		chrono.start();
		rank0 = aMatrix.rank0();
		System.out.println("Rank0(aMatrix) :" + rank0);
		chrono.stop();
		System.out.println("Tiempo: " + chrono.toString());
		chrono.start();
		rank1 = aMatrix.rank1();
		System.out.println("Rank1(aMatrix) :" + rank1);
		chrono.stop();
		System.out.println("Tiempo: " + chrono.toString());
		chrono.start();
		rank2 = aMatrix.rank2();
		System.out.println("Rank2(aMatrix) :" + rank2);
		chrono.stop();
		System.out.println("Tiempo: " + chrono.toString());
		System.out.println("Nullity(aMatrix) :" + aMatrix.nullity());
		if (rank2 != rank1) aMatrix.println("Matrix");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Chronometer chrono = new Chronometer();
		MatrixComplex aMatrix;
		int filas = 5;
		int columnas = 6;
		aMatrix = new MatrixComplex(filas,columnas);
		int boxSize = 65;
		int rank1, rank0, rank2;
		
		Complex.setFormatON();
		Complex.setFixedON(2);

		System.out.println(Complex.boxTitleRandom(boxSize, "RANK TEST"));
		System.out.println("Testing with arrays of " +filas+"x"+columnas);
		chrono.start();
		for(int i = 1; i < 10000; ++i) {
			aMatrix.initMatrixRandomInteger(1);			
			rank1 = aMatrix.rank1();
			rank2 = aMatrix.rank2();
			if (rank1 != rank2) {				
				System.out.println(Complex.boxTextRandom(boxSize, "TEST #"+i));
				System.out.println("rank1 = " + rank1);
				System.out.println("rank2 = " + rank2);
				System.out.println("CMPLXAR: "+aMatrix.toMatrixComplex());
				System.out.println("MAXIMA : rank("+aMatrix.toMaxima()+")");
				System.out.println("OCTAVE : rank("+aMatrix.toOctave()+")");
				System.out.println("WOLFRAM: MatrixRank["+aMatrix.toWolfram()+"]");
			}
		}
		chrono.stop();
		System.out.println("Tiempo: " + chrono.toString());
		System.out.println(Complex.boxTextRandom(boxSize, "--- FINISHED TEST ---"));
	}
}
