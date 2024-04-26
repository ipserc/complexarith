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

public class TestComplex06 {
	
    public static void main(String[] args) {
    	int boxSize = 65;
    	Complex a = new Complex();
        Complex b = new Complex();
        Complex c = new Complex();
        Complex d = new Complex();
        Complex e = new Complex();
        Complex f = new Complex();
        Complex g = new Complex();
        
        Complex.setFormatON();
        Complex.exact(false);
        Complex.setRepres(Complex.Representation.POLAR);

        Complex.facts();

        System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX CALCULATIONS COS+iSIN power"));
	   
        Complex.setScientificON(8);
	   

        a.setComplexRec(11.11, -1.03);
        // b.setComplexRec(2.14, 21.4); //VAYA COINCIDENCIA!!!!!!!!!!!
        b.setComplexRec(-2.14, 4.21); 
        c.setComplexRec(10.17, 1.12);
        System.out.println("a                   = " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b                   = " + b.toStringRec() + " : " + b.toStringPol());
        System.out.println("c                   = " + c.toStringRec() + " : " + c.toStringPol());
        d = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).times((Complex.cos(b).plus(Complex.i.times(Complex.sin(b))))).times((Complex.cos(c).plus(Complex.i.times(Complex.sin(c)))));
        System.out.println("PROD(cos+i·sin)(1)  = " + d.toStringRec() + " : " + d.toStringPol());
        d =         Complex.cos(a).plus(Complex.i.times(Complex.sin(a)));
		d = d.times(Complex.cos(b).plus(Complex.i.times(Complex.sin(b))));
		d = d.times(Complex.cos(c).plus(Complex.i.times(Complex.sin(c))));
		System.out.println("PROD(cos+i·sin)(2)  = " + d.toStringRec() + " : " + d.toStringPol());
		d = Complex.cos(a).plus(Complex.i.times(Complex.sin(a)));
		e = Complex.cos(b).plus(Complex.i.times(Complex.sin(b)));
		f = Complex.cos(c).plus(Complex.i.times(Complex.sin(c)));
		g = d.times(e).times(f);
		System.out.println("PROD(cos+i·sin)(3)  = " + g.toStringRec() + " : " + g.toStringPol());
		System.out.println("c(a)+i*s(a)         = " + d.toStringRec() + " : " + d.toStringPol());
		System.out.println("c(b)+i*s(b)         = " + e.toStringRec() + " : " + e.toStringPol());
		System.out.println("c(c)+i*s(c)         = " + f.toStringRec() + " : " + f.toStringPol());
		
		e = (Complex.cos(a.plus(b).plus(c))).plus(Complex.i.times(Complex.sin(a.plus(b).plus(c))));
		System.out.println("cos(SUM)+i·sin(SUM) = " + e.toStringRec() + " : " + e.toStringPol());
		d = a.plus(b).plus(c);
		System.out.println("a + b + c           = " + d.toStringRec() + " : " + d.toStringPol());
		e = Complex.cos(d);
		System.out.println("cos(a+b+c)		   = " + e.toStringRec() + " : " + e.toStringPol());
		f = Complex.sin(d);
		System.out.println("sin(a+b+c)		   = " + f.toStringRec() + " : " + f.toStringPol());
		f = Complex.i.times(f);
		System.out.println("i*sin(a+b+c)		   = " + f.toStringRec() + " : " + f.toStringPol());
		e = e.plus(f);
		System.out.println("cos(SUM)+i·sin(SUM)(2) = " + e.toStringRec() + " : " + e.toStringPol());
		
		f = Complex.cos(a);
		System.out.println("cos(a) 			   = " + f.toStringRec() + " : " + f.toStringPol());
		f = Complex.sin(a);
		System.out.println("sin(a) 			   = " + f.toStringRec() + " : " + f.toStringPol());
		f = Complex.cos(b);
		System.out.println("cos(b) 			   = " + f.toStringRec() + " : " + f.toStringPol());
		f = Complex.sin(b);
		System.out.println("sin(b) 			   = " + f.toStringRec() + " : " + f.toStringPol());
		f = Complex.cos(c);
		System.out.println("cos(c) 			   = " + f.toStringRec() + " : " + f.toStringPol());
		f = Complex.sin(c);
		System.out.println("sin(c) 			   = " + f.toStringRec() + " : " + f.toStringPol());
		
		e = Complex.cos(b);
		System.out.println("cos(b) 			   = " + e.toStringRec() + " : " + e.toStringPol());
		f = Complex.sin(b);
		System.out.println("sin(b) 			   = " + f.toStringRec() + " : " + f.toStringPol());
		f = Complex.i.times(f);
		System.out.println("i*sin(b) 		   = " + f.toStringRec() + " : " + f.toStringPol());
		g = e.plus(f);
		System.out.println("cos(b)+i*sin(b)	   = " + g.toStringRec() + " : " + g.toStringPol());
		
		
		System.out.println("------------------------------------------------------------");
	   

    }
}
