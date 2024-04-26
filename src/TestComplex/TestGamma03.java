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


public class TestGamma03 {
	private static void showResults(Complex a) {
		Complex b = new Complex();
		System.out.println("a			= " + a.toStringRec() + " : " + a.toStringPol());
		b = Complex.gamma(a);
		System.out.println("Gamma			= " + b.toStringRec() + " : " + b.toStringPol());
		b = Complex.gamma_integral(a);
		System.out.println("gamma_integral(a)	= " + b.toStringRec() + " : " + b.toStringPol());
		b = Complex.gamma_integral2(a);
		System.out.println("Gamma_integral2(a)	= " + b.toStringRec() + " : " + b.toStringPol());	   
		b = Complex.gamma_euler(a);
		System.out.println("Gamma_euler(a)		= " + b.toStringRec() + " : " + b.toStringPol());	   
		System.out.println("------------------------------------------------------------");
		
	}

    public static void main(String[] args) {
		int boxSize = 65;
		Complex a = new Complex();
		Complex b = new Complex();
	
		Complex.setFormatON();;
		Complex.setFixedON(5);

		Complex.facts();
		Complex.printFormatStatus();

		a.setComplex("0.1");
		showResults(a);		

		a.setComplex("1.0");
		showResults(a);
		
		a.setComplex("2.1");
		showResults(a);		

		a.setComplex("-0.1");
		showResults(a);		

		a.setComplex("-1.0");
		showResults(a);
		
		a.setComplex("-2.1");
		showResults(a);		

		a.setComplex("-2.1+0.3i");
		showResults(a);	
		
    }
}
