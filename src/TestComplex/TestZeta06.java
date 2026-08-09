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

public class TestZeta06 {
	
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
		Complex s = new Complex(0.49, 14.135);
		Complex sAnt = new Complex();
		Complex z = new Complex();
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX RIEMANN ZETA TEST"));
		Complex.setFormatON();
		Complex.setFixedON(8);
		Complex.facts();
		Complex.printFormatStatus();
		double incRe = 1E-6;
		Complex incIm = new Complex(0, 1E-4);
		double zModAnt = 100;
        while ( s.imp() <= 100) {
        	while (s.rep() <= 0.51) {
        		// System.out.println(mod + "|"+ Complex.rad_DMS(phase));
        		// System.out.println(s.mod() + "|" + Complex.rad_DMS(s.phase()));
        		// s.println();
        		//if (Math.abs(s.rep()) > 1) break;
        		z = Complex.zeta(s);
        		if (z.isZero()) {
            		s.println("Zero found for s = ");        			
        	        z.println("z("+s.toString()+")=");        			
        		}
        		else if (z.mod() > zModAnt) { 
        			sAnt.println("s = ");
        	        //z.println("z("+s.toString()+")=");
        	        System.out.println("z.mod=" + zModAnt);
        		}
        		else {
        			zModAnt = z.mod();
        			sAnt = s.copy();
        		}
        		s = s.plus(incRe);
        	}
        	s = s.plus(incIm);
        	s.setComplexRec(0, s.imp());
		}
    }
}
