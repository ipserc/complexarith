package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;

public class TestRank05 {

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
		aMatrix = new MatrixComplex("1.00,-1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,1.00,-1.00,1.00;-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,-1.00");
		aMatrix = new MatrixComplex("-66.0,-61.0,-42.0,2.0,-9.0,-40.0,-46.0,-39.0,101.0;360.0,187.0,161.0,126.0,-79.0,323.0,331.0,-5.0,-402.0;30.0,15.0,13.0,11.0,-7.0,27.0,28.0,-1.0,-33.0;6.0,3.0,2.0,7.0,-5.0,-1.0,3.0,-6.0,-2.0;-162.0,-81.0,-54.0,-54.0,27.0,-108.0,-162.0,-27.0,189.0;-180.0,-90.0,-78.0,-66.0,42.0,-162.0,-168.0,6.0,198.0;120.0,60.0,40.0,40.0,-20.0,80.0,120.0,20.0,-140.0;-30.0,-15.0,-10.0,-10.0,5.0,-20.0,-30.0,-5.0,35.0;0.0,0.0,0.0,15.0,-12.0,-15.0,-9.0,-21.0,27.0");
		aMatrix = new MatrixComplex("0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0;4.0,-5.0,1.0,3.0,5.0,-2.0,3.0,1.0,3.0;-32.0,40.0,-8.0,-24.0,-40.0,16.0,-24.0,-8.0,-24.0;68.0,-85.0,17.0,51.0,113.0,-58.0,11.0,25.0,83.0;12.0,-15.0,3.0,9.0,8.0,0.0,13.0,-4.0,7.0;24.0,-30.0,6.0,18.0,30.0,-12.0,18.0,6.0,18.0;-12.0,15.0,-3.0,-9.0,-8.0,0.0,-19.0,-1.0,-1.0;24.0,-30.0,6.0,18.0,30.0,-12.0,18.0,6.0,18.0;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0");
		rank1 = aMatrix.rank1();
		rank2 = aMatrix.rank2();
		System.out.println(Complex.boxTextRandom(boxSize, "TEST"));
		System.out.println("rank1 = " + rank1);
		System.out.println("rank2 = " + rank2);
		System.out.println("CMPLXAR: "+aMatrix.toMatrixComplex());
		System.out.println("MAXIMA : rank("+aMatrix.toMaxima()+")");
		System.out.println("OCTAVE : rank("+aMatrix.toOctave()+")");
		System.out.println("WOLFRAM: MatrixRank["+aMatrix.toWolfram()+"]");
		chrono.stop();
		System.out.println("Tiempo: " + chrono.toString());
		System.out.println(Complex.boxTextRandom(boxSize, "--- FINISHED TEST ---"));
	}
}
