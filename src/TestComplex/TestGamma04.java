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

public class TestGamma04 {
	public static void showResults(Complex a) {
		Complex b = new Complex();
		System.out.println("a			= " + a.toStringRec() + " : " + a.toStringPol());
		b = Complex.gamma(a);
		System.out.println("Gamma			= " + b.toStringRec() + " : " + b.toStringPol());
		b = Complex.gamma_weiertrass(a);
		System.out.println("GammaWeiertrass		= " + b.toStringRec() + " : " + b.toStringPol());
		System.out.println("------------------------------------------------------------");		
	}
	
    public static void main(String[] args) {
		int boxSize = 65;
		Complex a = new Complex();

		Complex.setFormatOFF();;
		Complex.setScientificON(5);

		Complex.facts();
		Complex.printFormatStatus();

		a.setComplex("4+1e-8i");
		showResults(a);
		
		a.setComplex("4.8");
		showResults(a);
		
		a.setComplex("0.8");
		showResults(a);

		a.setComplex("-0.8");
		showResults(a);

		a.setComplex("-4.8");
		showResults(a);
		
		a.setComplex("5.3+3.5i");
		showResults(a);
		

		a.setComplex("-5.3+3.5i");
		showResults(a);
		
		a.setComplex("-5.3-3.5i");
		showResults(a);
		
		a.setComplex("5.3-3.5i");
		showResults(a);
		
		a.setComplex("-3");
		showResults(a);

		a.setComplex("-4");
		showResults(a);

		a.setComplex("-3+.000001i");
		showResults(a);

		a.setComplex("-4+.000001i");
		showResults(a);
		

    }
}
