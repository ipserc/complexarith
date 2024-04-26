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

public class TestZeta03 {
	
	
    public static void main(String[] args) {
		Complex s = new Complex();
		Complex z = new Complex();
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX RIEMANN ZETA TEST"));
		Complex.setFormatON();
		Complex.setScientificON(8);
		Complex.exact(false);
		Complex.precision(1E-18);
		Complex.facts();
		Complex.printFormatStatus();
		

		s.setComplexRec(-6.85,0);
		s.println("s=");
		z = Complex.zeta(s);
        z.println("z ("+s.toString()+")=");
		z = Complex.zeta_havil(s);
        z.println("zh("+s.toString()+")=");
  
		s.setComplexRec(-3.85,1.34);
		s.println("s=");
		z = Complex.zeta(s);
        z.println("z ("+s.toString()+")=");
		z = Complex.zeta_havil(s);
        z.println("zh("+s.toString()+")=");
 
        
    }
}
