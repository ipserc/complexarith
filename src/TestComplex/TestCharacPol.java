package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestCharacPol {
	
	private static void ShowResults(MatrixComplex A) {
		System.out.println("En Wolfram:" + A.toWolfram());
		A.charactPoly().println("Polinomio Caraterístico");
		System.out.println("------------------------------------------------");
	}

	public static void main(String[] args) {
		MatrixComplex A;
		
		Complex.setFormatON();
		
		A = new MatrixComplex("1,2,3;0,0,2;0,1,1");
		ShowResults(A);
		
		A = new MatrixComplex("1,-2,3;3,1,2;-1,1,1");
		ShowResults(A);

		A = new MatrixComplex("1,2,3,4;-1,0,2,-3;3,1,1,-2;5,-3,0,4");
		ShowResults(A);

		A = new MatrixComplex("1,2,3,4,0;-1,0,2,-1,-3;3,1,1,2,-2;5,-3,0,4,-3;-1,0,0,3,2");
		ShowResults(A);
}
}
