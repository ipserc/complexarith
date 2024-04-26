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

public class TestComplex03 {
    public static void main(String[] args) {
		Complex s = new Complex();
		Complex z = new Complex();
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX RIEMANN ZETA TEST"));
		Complex.setFormatON();
		Complex.setScientificON(8);
		Complex.exact(true);
		Complex.facts();
		Complex.printFormatStatus();
		/*
		for(int i = 2; i < 17; ++i) {
			s.setComplexRec(i,0);
			z = Complex.zeta(s);
	        z.println("z("+s.toString()+")=");
		}

		for(int i = 0; i > -17; --i) {
			s.setComplexRec(i,0);
			z = Complex.zeta(s);
	        z.println("z("+s.toString()+")=");
		}
		*/
		
		/*
		s.setComplex("1.36745462-12.57032897i");
		s.println("s=");
		z = Complex.zeta(s);
        z.println("z("+s.toString()+")=");
		*/
		
		s.setComplex("1.14845024-5.90075626i");
		s.println("s=");
		z = Complex.zeta(s);
        z.println("z("+s.toString()+")=");

        /*
        for(int i = 1; i <21; ++i) {
			s.setComplexRandomRec(16);
			s.println("s=");
			z = Complex.zeta(s);
	        z.println("z("+s.toString()+")=");
		}
		*/
    }
}
