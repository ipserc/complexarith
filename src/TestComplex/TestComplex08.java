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

public class TestComplex08 {
	
    public static void main(String[] args) {
    	int boxSize = 65;
    	double da, db;
    	Complex a = new Complex();
        Complex b = new Complex();
        Complex c = new Complex();
        Complex d = new Complex();
        Complex e = new Complex();
        Complex f = new Complex();
        Complex g = new Complex();
        
        Complex.setFormatON();
        Complex.exact(false);
        Complex.setRepres(Complex.Representation.RECTANGULAR);

        Complex.facts();
	   
	   System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX EXPONENTIAL"));
	   
	   System.out.println(Complex.boxTextRandom(boxSize, "Real Exponential"));
	   da = 32;
	   Complex.exp(32).println("e**"+da+"=");
	   
	   System.out.println(Complex.boxTextRandom(boxSize, "Complex Exponential"));
	   da = 32;
	   Complex.exp(Complex.i.times(Complex.i.times(-32))).println("e**-ii"+da+"=");

	   System.out.println(Complex.boxTextRandom(boxSize, "Complex trigon equivalent Exponential"));
	   da = 32;
	   a = Complex.i.times(-da);
	   b = Complex.sin(a); //SIN
	   c = Complex.cos(a); //COS
	   c.plus(b.times(Complex.i)).println("cos(-i·a)+i·sin(-i·a)=");


   }
}
