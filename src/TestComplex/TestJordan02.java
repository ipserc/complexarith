package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Jordan;

public class TestJordan02 {

	public static void showResults(Jordan jordan) {
		System.out.println("=================================================================");
		jordan.println("---------------- MATRIZ ORIGINAL");
		jordan.factorize();
		
		jordan.P().times(jordan.J().times(jordan.P().inverse())).println("\nPROOF\n");
		jordan.println("\n");
		
	}
	
	public static void main(String[] args) {
		Jordan jordan;
		
		Complex.setFixedON(3);
		
		/*
		jordan = new Jordan(""
				+ " 0, 2, 2;"
				+ " 2, 0,-1;"
				+ "-1,-1, 0");
		showResults(jordan);
		*/

		jordan = new Jordan(""
				+ "-2, 0, 3, 4, 5;"
				+ " 0,-2, 0, 6, 7;"
				+ " 0, 0,-2, 0, 8;"
				+ " 0, 0, 0,-2, 0;"
				+ " 0, 0, 0, 0,-2");
		showResults(jordan);

	}
}
