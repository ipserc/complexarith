package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Jordan;

public class TestJordan {

	public static void showResults(Jordan jordan) {
		System.out.println("=================================================================");
		jordan.println("---------------- MATRIZ ORIGINAL");
		jordan.factorize();
		
		jordan.P().inverse().times(jordan.J().times(jordan.P())).println("PROOF");
		
	}
	
	public static void main(String[] args) {
		Jordan jordan;
		
		Complex.setFixedON(3);
		
		/*
		jordan = new Jordan("2,2,-3,4;-2,2,1,0;3,3,-5,7;4,2,-6,7");
		showResults(jordan);

		jordan = new Jordan("0,3,1;2,-1,-1;-2,-1,-1");
		showResults(jordan);

		jordan = new Jordan("0,2,2;2,0,-1;-1,-1,0");
		showResults(jordan);
		*/

		jordan = new Jordan(  " 1, 0, 0, 0;"
							+ "-1, 2, 1, 0;"
							+ "-1, 0, 2, 0;"
							+ "-1, 0, 1, 1");
		showResults(jordan);
	}
}
