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


public class TestBeta02 {
	private static void showResults(Complex p, Complex q) {
		Complex b = new Complex();
		System.out.println("p			= " + p.toStringRec() + " : " + p.toStringPol());
		System.out.println("q			= " + q.toStringRec() + " : " + q.toStringPol());
		b = Complex.beta(p,q);
		System.out.println("Beta			= " + b.toStringRec() + " : " + b.toStringPol());
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

		p.setComplex("3+2i");
		q.setComplex("4+2i");
		showResults(p, q);		

		p.setComplex("3-2i");
		q.setComplex("4+2i");
		showResults(p, q);		

		p.setComplex("3-2i");
		q.setComplex("-4+2i");
		showResults(p, q);		

		p.setComplex("3-2i");
		q.setComplex("-4-2i");
		showResults(p, q);		

		p.setComplex("-3-2i");
		q.setComplex("-4+2i");
		showResults(p, q);		

		p.setComplex("-3-2i");
		q.setComplex("4+2i");
		showResults(p, q);		

		p.setComplex("-3-2i");
		q.setComplex("-4+2i");
		showResults(p, q);		

		p.setComplex("-3-2i");
		q.setComplex("-4-2i");
		showResults(p, q);		

		p.setComplex("-3-2i");
		q.setComplex("-4+2i");
		showResults(p, q);		

		p.setComplex("3-2i");
		q.setComplex("-4+2i");
		showResults(p, q);		

		p.setComplex("-3+2i");
		q.setComplex("4-2i");
		showResults(p, q);		

    }
}
