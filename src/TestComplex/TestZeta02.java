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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestZeta02 {
	
	/**
	 * https://mathworld.wolfram.com/RiemannZetaFunction.html --> Analytic continuation can also be performed using Hankel functions
	 * @param s
	 * @return
	 */
	public static Complex zeta_hk(Complex s) {
		Complex one_s = new Complex(1).minus(s);
		Complex z3 = new Complex(0,0);
		Complex z2 = new Complex(0.5,0);
		Complex cte = Complex.ONE.minus(new Complex(2,0).power(one_s));
		int n = 1;
		int k;
		Complex s_opp = s.opposite(); 
		
		while( true ) {
			Complex z1 = new Complex(0,0);
			for (k = 0; k <= n; ++k) {
				z1 = z1.plus(new Complex(k+1,0).power(s_opp).times(nCr(n, k)).times(Math.pow(-1, k)));
			}
			z2 = z2.plus(z1.times(1.0 / Math.pow(2, n+1)));
			z2.println("z2 = ");
			if (z3.equals(z2)) break;
			z3 = z2.copy();
			++n;
		}
		
		return cte.times(z2);
	}
	
	public static long nCr(int n, int r) {
		return fact(n)/fact(r)/fact(n-r);
	}
	
	public static long fact(int n) {
		long fact = 1;
		for(int i = 1; i < n; ++n) {
			fact *= i;
		}
		return fact;
	}
	
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
	        z.println("zeta("+s.toString()+")	=	");
		}

		for(int i = 0; i > -17; --i) {
			s.setComplexRec(i,0);
			z = Complex.zeta(s);
	        z.println("zeta("+s.toString()+")	=	");
		}
		*/
		
		/*
		s.setComplex("1.36745462-12.57032897i");
		s.println("s=");
		z = Complex.zeta(s);
        z.println("zeta("+s.toString()+")	=	");
		*/
		
		/*
		s.setComplex("1.14845024-5.90075626i");
		s.println("s=");
		z = zeta_hk(s);
        z.println("zeta("+s.toString()+")	=	");
		*/

		s.setComplex("2.60044300E+00+1.43814595E+01i");
		//s.println("s=");
		z = Complex.zeta(s);
        z.println("zeta("+s.toString()+")	=	");

        s.setComplex("2.60044300E+02+1.43814595E+01i");
		//s.println("s=");
		z = Complex.zeta(s);
        z.println("zeta("+s.toString()+")	=	");

		s.setComplex("2.60044300E-02+1.43814595E+01i");
		//s.println("s=");
		z = Complex.zeta(s);
        z.println("zeta("+s.toString()+")	=	");

        s.setComplex("-2.60044300E-02-1.43814595E+01i");
		//s.println("s=");
		z = Complex.zeta(s);
        z.println("zeta("+s.toString()+")	=	");
        
        //z = Complex.zeta_analytic_continuation(s);
        //z.println("zeta("+s.toString()+")	=	");

		s.setComplex("-1.7");
		//s.println("s=");
		z = Complex.zeta(s);
        z.println("zeta("+s.toString()+")	=	");

        for(int i = 1; i <21; ++i) {
        	s.setComplexRandomRec(16);
        	/* * /
        	do s.setComplexRandomRec(16);
        	while (s.rep() >= 0 && s.rep() <= 1);
        	/* */
        	// s.setComplexRandomRec(16);
			//s.println("s=");
			z = Complex.zeta(s);
	        z.println("zeta("+s.toString()+")	=	");
		}
    }
}
