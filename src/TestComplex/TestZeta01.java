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

public class TestZeta01 {
	
	public static Complex zeta_pi(Complex s) {
		BufferedReader reader;
		Complex z = new Complex(1,0);
		Complex z2 = new Complex();
		Complex prime;
		Complex s_opp = s.opposite();

		try {
			reader = new BufferedReader(new FileReader("./data/primes_n.txt"));
			String line = reader.readLine();
			line = reader.readLine();

			while (line != null) {
				//System.out.println(line);
				// read next line
				
				prime = new Complex(line);
				z = z.times((Complex.ONE.minus(prime.power(s_opp))));
				//z.println("z=");
				if (z.inverse().equals(z2.inverse())) {
					break;
				}
				z2 = z.copy();
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return z.inverse();
		}
		return z.inverse();
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
		
		/*
		s.setComplex("1.14845024-5.90075626i");
		s.println("s=");
		z = zeta_pi(s);
        z.println("z("+s.toString()+")=");
		*/
		
		/** /
		s.setComplex("1.14845024-5.90075626i");
		//s.setComplex("2.3");
		//s.setComplex("0");
		s.println("s=");
		z = Complex.zeta_primes(s);
        z.println("z("+s.toString()+")=");
		z = Complex.zeta(s);
        z.println("z("+s.toString()+")=");
		/**/
		
       /* */
        for(int i = 1; i <21; ++i) {
			s.setComplexRandomRec(16);
			s.println("s=");
			z = Complex.zeta(s);
	        z.println("z("+s.toString()+")=");
		}
		/**/
		s.setComplexRec(0.85,0);
		s.println("s=");
		z = Complex.zeta(s);
        z.println("z ("+s.toString()+")=");
		z = Complex.zeta_havil(s);
        z.println("zh("+s.toString()+")=");
  
        
    }
}
