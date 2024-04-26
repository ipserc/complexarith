package TestComplex;

import com.ipserc.arith.complex.*;

public class TestOneChecks {

	public static void main(String[] args) {
		int boxSize = 65;
		
        Complex.setFormatON();
        Complex.setRepres(Complex.Representation.RECTANGULAR);
        Complex.facts();

        /* ******************************************************
         * Square Root Section
         ****************************************************** */
		System.out.println(Complex.boxTextRandom(boxSize, "Square Root Section"));

		Complex.sqrt(Complex.ONE,0).println("squaresquareRoot(1)[0]	= ");
		Complex.sqrt(Complex.ONE,1).println("squaresquareRoot(1)[1]	= ");
		Complex.sqrt(Complex.mONE,0).println("squaresquareRoot(-1)[0]	= ");
		Complex.sqrt(Complex.mONE,1).println("squaresquareRoot(-1)[1]	= ");
		
        /* ******************************************************
         * (+-1)/(-+1) Section
         ******************************************************* */
		System.out.println(Complex.boxTextRandom(boxSize, "(+-1)/(-+1) Section"));
		
		Complex minusOne01 = Complex.ONE.divides(Complex.mONE);
		minusOne01.println("minusOne01 = 1/-1		= ");
		minusOne01.printlnPol("minusOne01 = 1/-1		= ");

		Complex minusOne10 = Complex.mONE.divides(Complex.ONE);
		minusOne10.println("minusOne10 = -1/1		= ");
		minusOne10.printlnPol("minusOne10 = -1/1		= ");

		Complex.sqrt(minusOne01,0).println("squaresquareRoot(1/-1)[0]	= ");
		Complex.sqrt(minusOne01,1).println("squaresquareRoot(1/-1)[1]	= ");
		Complex.sqrt(minusOne10,0).println("squaresquareRoot(-1/1)[0]	= ");
		Complex.sqrt(minusOne10,1).println("squaresquareRoot(-1/1)[1]	= ");
		if (minusOne01.equals(minusOne10))
			System.out.println("minusOne01 EQUALS minusOne10");
		else 
			System.out.println("minusOne01 DIFFERENT minusOne10");
		
		/* ******************************************************
         * Check Section
         ******************************************************* */
		System.out.println(Complex.boxTextRandom(boxSize, "Check Section"));
		
		Complex.sqrt(Complex.ONE.divides(Complex.mONE)).println("squareRoot(1/-1)		= ");
		Complex.sqrt(Complex.ONE).divides(Complex.sqrt(Complex.mONE)).println("squareRoot(1)/squareRoot(-1)	= ");

		Complex.sqrt(Complex.mONE.divides(Complex.ONE), 1).println("squareRoot(-1/1)[1]		= ");
		Complex.sqrt(Complex.mONE, 1).divides(Complex.sqrt(Complex.ONE)).println("squareRoot(-1)[1]/squareRoot(1)	= ");

		Complex.sqrt(Complex.mONE.divides(Complex.ONE)).println("squareRoot(-1/1)		= ");
		Complex.sqrt(Complex.mONE).divides(Complex.sqrt(Complex.ONE)).println("squareRoot(-1)/squareRoot(1)	= ");

		Complex.sqrt(Complex.mONE.divides(Complex.ONE), 0).println("squareRoot(-1/1)[0]		= ");
		Complex.sqrt(Complex.mONE, 0).divides(Complex.sqrt(Complex.ONE)).println("squareRoot(-1)[0]/squareRoot(1)	= ");
		
		System.out.println(Complex.boxTextRandom(boxSize, "Roots Section"));

		for (int root = 0; root < 2; ++root) {
			Complex.sqrt(Complex.ONE.divides(Complex.mONE),root).println("squareRoot(1/-1)["+root+"]			= ");
			Complex.sqrt(Complex.ONE,root).divides(Complex.sqrt(Complex.mONE,root)).println("squareRoot(1)["+root+"]/squareRoot(-1)["+root+"]	= ");
		}
		
		for (int root = 0; root < 2; ++root) {
			Complex.sqrt(Complex.mONE.divides(Complex.ONE),root).println("squareRoot(-1/1)["+root+"]			= ");
			Complex.sqrt(Complex.mONE,root).divides(Complex.sqrt(Complex.ONE,root)).println("squareRoot(-1)["+root+"]/squareRoot(1)["+root+"]	= ");
		}

	}

}
