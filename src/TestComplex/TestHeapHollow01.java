package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestHeapHollow01 {
	private static boolean Reduced = true;

	private static void showResults(MatrixComplex aMatrix) {
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "RANK TEST"));   	
		aMatrix.println("aMatrix");
		System.out.println("CMPLXAR: "+aMatrix.toMatrixComplex());
		System.out.println("MAXIMA : rank("+aMatrix.toMaxima()+")");
		System.out.println("OCTAVE : rank("+aMatrix.toOctave()+")");
		System.out.println("WOLFRAM: rank("+aMatrix.toWolfram()+")");
		aMatrix.triangle().println("aMatrix Triang");
		aMatrix.triangleLo().println("aMatrix Triang lo");
		System.out.println("Rank(aMatrix)  :" + aMatrix.rank(Reduced));
		System.out.println("Nullity(aMatrix) :" + aMatrix.nullity(Reduced));
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix, loT, loTH, upT, upTH;
		int boxSize = 65;
		
		Complex.setFormatON();
		Complex.setFixedON(2);
		
		System.out.println(Complex.boxTitleRandom(boxSize, "HEAP & HOLLOW TEST"));
		aMatrix = new MatrixComplex(
				" 1, 2, -3,-1, 2, 3;"+
				"-3,-5,  9, 3,-1,-8;"+
				" 5, 9,-15,-5, 3,14;"+
				" 2, 4, -6,-2, 3, 6" );
		showResults(aMatrix);		
		upT = aMatrix.triangle();
		loT = aMatrix.triangleLo();
		
		System.out.println(Complex.boxTextRandom(boxSize, "aMatrix Triang UP Hollow"));
		upT.println("aMatrix Triang UP");
		upTH = upT.hollow();
		upTH.println("aMatrix Triang UP Hollow");

		System.out.println(Complex.boxTextRandom(boxSize, "aMatrix Triang LO Heap"));
		loT.println("aMatrix Triang LO");
		loTH = loT.heap();
		loTH.println("aMatrix Triang LO Heap");

		System.out.println(Complex.boxTitleRandom(boxSize, "HEAP & HOLLOW TEST"));
		aMatrix = new MatrixComplex(
				"1, -3, 5, 2;"+
				"2, -5, 9, 4;"+
				"-3, 9, -15, -6;"+
				"-1, 3, -5, -2;"+
				"2, -1, 3, 3;"+
				"3, -8, 14, 6");
		showResults(aMatrix);
		upT = aMatrix.triangle();
		loT = aMatrix.triangleLo();
		
		System.out.println(Complex.boxTextRandom(boxSize, "aMatrix Triang UP Hollow"));
		upT.println("aMatrix Triang UP");
		upTH = upT.hollow();
		upTH.println("aMatrix Triang UP Hollow");

		System.out.println(Complex.boxTextRandom(boxSize, "aMatrix Triang LO Heap"));
		loT.println("aMatrix Triang LO");
		loTH = loT.heap();
		loTH.println("aMatrix Triang LO Heap");
	}
}
