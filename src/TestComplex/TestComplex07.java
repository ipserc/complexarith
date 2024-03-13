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

public class TestComplex07 {
	
    public static void main(String[] args) {
    	int boxSize = 65;
    	int ia, ib;
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

	   
	   System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX BINOMIAL COEFFICIENTS"));
	   
	   Complex.setScientificON(8);

	   ia = 12;
	   ib = 7;
	   Complex.binomialCoef(ia,ib).println("binomialCoef("+ia+","+ib+"):");

	   a.setComplexRec(ia,0);
	   b.setComplexRec(ib,0);
	   Complex.binomialCoef(ia,ib).println("binomialCoef("+a+","+b+"):");
	   
	   a.setComplexRec(ia,-ib);
	   b.setComplexRec(ib,ia);
	   Complex.binomialCoef(ia,ib).println("binomialCoef("+a+","+b+"):");

	   ia = 5;
	   ib = 4;
	   a.setComplexRec(ia,-ib);
	   b.setComplexRec(ib,ia);
	   Complex.binomialCoef(a,b).println("binomialCoef("+a+","+b+"):");

	   a.setComplexRec(12.4,0);
	   b.setComplexRec(3.2,0);
	   Complex.binomialCoef(a,b).println("binomialCoef("+a+","+b+"):");

	   a.setComplexRec(12.4,0);
	   b.setComplexRec(3.5,0);
	   Complex.binomialCoef(a,b).println("binomialCoef("+a+","+b+"):");

	   a.setComplexRec(12.4,0);
	   b.setComplexRec(3.8,0);
	   Complex.binomialCoef(a,b).println("binomialCoef("+a+","+b+"):");

	   a.setComplexRec(12.4,2);
	   b.setComplexRec(3.8,3);
	   Complex.binomialCoef(a,b).println("binomialCoef("+a+","+b+"):");

	   a.setComplexRec(12.4,2);
	   b.setComplexRec(3.8,-3);
	   Complex.binomialCoef(a,b).println("binomialCoef("+a+","+b+"):");
    }
}
