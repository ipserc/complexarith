package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ScratchMatrixSquarePrint01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		MatrixComplex m = new MatrixComplex("1,-22,333.456;4444,-5,6;7,8,-9999.5");
		System.out.println(m.toString());
		System.out.println();
		MatrixComplex m2 = new MatrixComplex("1+1i,2;3,4-100i");
		System.out.println(m2.toString());
	}
}
