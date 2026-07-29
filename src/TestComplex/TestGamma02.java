/******************************************************************************
 *  Compilation:  javac TestComplex.java
 *  Execution:    java TestComplex
 *
 *  Tests for arith.Complex.
 *	
 *  
 *  
 *  
 *  
 *  
 *  
 *
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *
 ******************************************************************************/
package TestComplex;

import com.ipserc.arith.complex.*;

public class TestGamma02 {
	public static void showResults(Complex a) {
		Complex b = new Complex();

		System.out.println("a			= " + a.toStringRec() + " : " + a.toStringPol());
		b = Complex.gamma(a);
		System.out.println("Gamma			= " + b.toStringRec() + " : " + b.toStringPol());
		b = Complex.gamma_fast(a);
		System.out.println("Gamma_fast(a)		= " + b.toStringRec() + " : " + b.toStringPol());	   
		b = Complex.gamma_zones(a);
		System.out.println("Gamma_zones(a)		= " + b.toStringRec() + " : " + b.toStringPol());	   
		b = Complex.gamma_integral(a);
		System.out.println("gamma_integral(a)	= " + b.toStringRec() + " : " + b.toStringPol());
		b = Complex.gamma_integral2(a);
		System.out.println("gamma_integral2(a)	= " + b.toStringRec() + " : " + b.toStringPol());
		b = Complex.gamma_weiertrass(a);
		System.out.println("Gamma_weiertrass(a)	= " + b.toStringRec() + " : " + b.toStringPol());
		System.out.println("------------------------------------------------------------");

	}
	
    public static void main(String[] args) {
		int boxSize = 65;
		Complex a = new Complex();
	
		Complex.setFormatON();;
		Complex.setFixedON(5);

		Complex.facts();
		Complex.printFormatStatus();

		a.setComplex("44.234");
		showResults(a);

		a.setComplex("4");
		showResults(a);

		a.setComplex("3.5");
		showResults(a);

		a.setComplex("3");
		showResults(a);

		a.setComplex("2.3");
		showResults(a);

		a.setComplex("2");
		showResults(a);

		a.setComplex("1");
		showResults(a);

		a.setComplex("12.1");
		showResults(a);
		
		a.setComplex("6.3");
		showResults(a);

		a.setComplex("2.5");
		showResults(a);

		a.setComplex("2");
		showResults(a);

		a.setComplex("1.324");
		showResults(a);

		a.setComplex(".97566");
		showResults(a);

		a.setComplex(".00356");
		showResults(a);

		a.setComplex("-2.5");
		showResults(a);
		
		a.setComplex("-2.6");
		showResults(a);

		a.setComplex("i");
		showResults(a);
			   
		a.setComplex("-2+4i");
		showResults(a);
		
		a.setComplex("0.7+0.3i");
		showResults(a);

		a.setComplex("0.1");
		showResults(a);

		a.setComplex("-0.1");
		showResults(a);

		a.setComplex("-0.1-0.1i");
		showResults(a);
		
		a.setComplex("0.1-0.1i");
		showResults(a);
		
		a.setComplex("10.1-0.1i");
		showResults(a);
		
		a.setComplex("1.1");
		showResults(a);

		a.setComplex("-0.1");
		showResults(a);
		
		a.setComplex("-7.1");
		showResults(a);

		a.setComplex("7.1-3i");
		showResults(a);

		a.setComplex("3.3-3.3i");
		showResults(a);

		a.setComplex("5.3-5.3i");
		showResults(a);

    }
}
