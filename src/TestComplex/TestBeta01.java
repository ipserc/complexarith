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


public class TestBeta01 {
	private static void showResults(Complex p, Complex q) {
		Complex b = new Complex();
		System.out.println("p			= " + p.toStringRec() + " : " + p.toStringPol());
		System.out.println("q			= " + q.toStringRec() + " : " + q.toStringPol());
		b = Complex.beta(p,q);
		System.out.println("ß(p,q)			= " + b.toStringRec() + " : " + b.toStringPol());
		System.out.println("------------------------------------------------------------");
		
	}

    public static void main(String[] args) {
		int boxSize = 65;
		Complex p = new Complex();
		Complex q = new Complex();
	
		Complex.setFormatON();;
		Complex.setScientificON(5);

		Complex.facts();
		Complex.printFormatStatus();

		p.setComplex("0.1");
		q.setComplex("-0.2");
		showResults(p, q);		

		p.setComplex("1.1");
		q.setComplex("-1.2");
		showResults(p, q);		

		p.setComplex("0.1");
		q.setComplex("-0.2i");
		showResults(p, q);		

		p.setComplex("6");
		q.setComplex("4");
		showResults(p, q);		

		p.setComplex("-6.1");
		q.setComplex("4.11");
		showResults(p, q);		

		p.setComplex("-6.5");
		q.setComplex("4.51");
		showResults(p, q);		

		p.setComplex("-6.5");
		q.setComplex("4.5");
		showResults(p, q);		

		p.setComplex("3.234+.876i");
		q.setComplex("1.561+.802i");
		showResults(p, q);		

		p.setComplex("3.234-6.876i");
		q.setComplex("1.561+4.802i");
		showResults(p, q);		

		p.setComplex("-4.00000000000001");
		q.setComplex("-3.00000000000001");
		showResults(p, q);		
}
}
