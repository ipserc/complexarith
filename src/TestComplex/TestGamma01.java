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

public class TestGamma01 {
	public static void showresults(Complex a) {
		Complex b = new Complex();
		b = Complex.gamma(a);
		System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
		System.out.println("Gamma(a)	= " + b.toStringRec() + " : " + b.toStringPol());
		b = Complex.gamma_nemes(a);
		System.out.println("GammaNemes(a)	= " + b.toStringRec() + " : " + b.toStringPol());	   
		System.out.println("------------------------------------------------------------");		
	}
	
    public static void main(String[] args) {
		int boxSize = 65;
		Complex a = new Complex();
	
		Complex.setFormatON();;
		Complex.setScientificON(5);

		Complex.facts();
		Complex.printFormatStatus();

		a.setComplex("1.1i");
		showresults(a);
		
		a.setComplex("12.1");
		showresults(a);
		
		a.setComplex("-2.5");
		showresults(a);
		
		a.setComplex("-2.6");
		showresults(a);
		
		a.setComplex("i");
		showresults(a);
			   
		a.setComplex("-2+4i");
		showresults(a);
		
		a.setComplex("-1+2i");
		showresults(a);
		
		a.setComplex("0.7+0.3i");
		showresults(a);

		a.setComplex("0.1");
		showresults(a);

		a.setComplex("-0.1");
		showresults(a);

		a.setComplex("-0.1-0.1i");
		showresults(a);
		
    }
}
