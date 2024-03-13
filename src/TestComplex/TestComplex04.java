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

public class TestComplex04 {
    public static void main(String[] args) {
		Complex z = new Complex();
		Complex exp = new Complex();
		Complex zexp ;
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "POWER OPERATIONS WITH Z"));
		Complex.setFormatON();
		//Complex.setFixedON(16);
		Complex.exact(true);
		Complex.facts();
		Complex.printFormatStatus();
		z.setComplexRec(5, 0);
		exp.setComplexRec(0, 3);
		zexp = z.power(exp);
		
		z.		println("z    = ");
		exp.	println("exp  = ");
		zexp.	println("zexp = ");
		
		double term = exp.imp()*Math.log(z.rep());
		Complex cos = Complex.cos(term);
		Complex sin = Complex.sin(term);
		cos.plus(sin.times(Complex.i)).println("Check1 = ");
		
		System.out.println("|zexp| = " + zexp.mod());
		
    }
}
