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

public class TestComplex05 {
	
    public static void main(String[] args) {
    	int boxSize = 65;
    	Complex a = new Complex();
        Complex b = new Complex();
        Complex c = new Complex();
        Complex d = new Complex();
        Complex e = new Complex();
        Complex f = new Complex();
        
        Complex.setFormatOFF(true);
        Complex.setRepres(Complex.Representation.POLAR);

        Complex.facts();

	   
	   System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX CALCULATIONS COS+iSIN power"));
	   
	   Complex.setScientificON(5);
	   
	   a.setComplexRec(1.1, -3.3);
	   b = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).power(3);
	   c = Complex.cos(a.times(3)).plus(Complex.i.times(Complex.sin(a.times(3))));
	   System.out.println("a               = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("(cos+i·sin)^n   = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("cos(n)+i·sin(n) = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("------------------------------------------------------------");

	   a.setComplexRec(11, -33);
	   b = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).power(3);
	   c = Complex.cos(a.times(3)).plus(Complex.i.times(Complex.sin(a.times(3))));
	   System.out.println("a               = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("(cos+i·sin)^n   = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("cos(n)+i·sin(n) = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("------------------------------------------------------------");

	   a.setComplexRec(66, -99);
	   b = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).power(3);
	   c = Complex.cos(a.times(3)).plus(Complex.i.times(Complex.sin(a.times(3))));
	   System.out.println("a               = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("(cos+i·sin)^n   = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("cos(n)+i·sin(n) = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("------------------------------------------------------------");

	   a.setComplexRec(1.1, 0);
	   b.setComplexRec(-2.4,0);
	   c.setComplexRec(0.7, 0);
	   d = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).times((Complex.cos(b).plus(Complex.i.times(Complex.sin(b))))).times((Complex.cos(c).plus(Complex.i.times(Complex.sin(c)))));
	   e = Complex.cos(a.plus(b).plus(c)).plus(Complex.i.times(Complex.sin(a.plus(b).plus(c))));
	   System.out.println("a                   = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b                   = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("c                   = " + c.toStringRec() + " : " + c.toStringPol());
	   System.out.println("PROD(cos+i·sin)     = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("cos(SUM)+i·sin(SUM) = " + e.toStringRec() + " : " + e.toStringPol());
	   System.out.println("------------------------------------------------------------");

	   a.setComplexRec(1.1, -0.3);
	   b.setComplexRec(2.4, 2.1);
	   c.setComplexRec(0.7, 1.2);
	   d = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).times((Complex.cos(b).plus(Complex.i.times(Complex.sin(b))))).times((Complex.cos(c).plus(Complex.i.times(Complex.sin(c)))));
	   e = (Complex.cos(a.plus(b).plus(c))).plus(Complex.i.times(Complex.sin(a.plus(b).plus(c))));
	   System.out.println("a                   = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b                   = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("c                   = " + c.toStringRec() + " : " + c.toStringPol());
	   System.out.println("PROD(cos+i·sin)     = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("cos(SUM)+i·sin(SUM) = " + e.toStringRec() + " : " + e.toStringPol());
	   System.out.println("------------------------------------------------------------");
 
	   a.setComplexRec(1.111, -.103);
	   b.setComplexRec(.214, 2.111);
	   c.setComplexRec(1.017, .112);
	   d = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).times((Complex.cos(b).plus(Complex.i.times(Complex.sin(b))))).times((Complex.cos(c).plus(Complex.i.times(Complex.sin(c)))));
	   e = (Complex.cos(a.plus(b).plus(c))).plus(Complex.i.times(Complex.sin(a.plus(b).plus(c))));
	   System.out.println("a                   = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b                   = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("c                   = " + c.toStringRec() + " : " + c.toStringPol());
	   System.out.println("PROD(cos+i·sin)     = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("cos(SUM)+i·sin(SUM) = " + e.toStringRec() + " : " + e.toStringPol());
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
	   System.out.println("------------------------------------------------------------");

	   a.setComplexRec(11.11, -1.03);
	   b.setComplexRec(2.14, 21.11);
	   c.setComplexRec(10.17, 1.12);
	   d = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).times((Complex.cos(b).plus(Complex.i.times(Complex.sin(b))))).times((Complex.cos(c).plus(Complex.i.times(Complex.sin(c)))));
	   e = (Complex.cos(a.plus(b).plus(c))).plus(Complex.i.times(Complex.sin(a.plus(b).plus(c))));
	   System.out.println("a                   = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b                   = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("c                   = " + c.toStringRec() + " : " + c.toStringPol());
	   System.out.println("PROD(cos+i·sin)     = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("cos(SUM)+i·sin(SUM) = " + e.toStringRec() + " : " + e.toStringPol());
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
	   System.out.println("------------------------------------------------------------");
	   
	   a.setComplexRec(111.1, -10.3);
	   b.setComplexRec(21.4, 211.1);
	   c.setComplexRec(101.7, 11.2);
	   d = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).times((Complex.cos(b).plus(Complex.i.times(Complex.sin(b))))).times((Complex.cos(c).plus(Complex.i.times(Complex.sin(c)))));
	   e = (Complex.cos(a.plus(b).plus(c))).plus(Complex.i.times(Complex.sin(a.plus(b).plus(c))));
	   System.out.println("a                   = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b                   = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("c                   = " + c.toStringRec() + " : " + c.toStringPol());
	   System.out.println("PROD(cos+i·sin)     = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("cos(SUM)+i·sin(SUM) = " + e.toStringRec() + " : " + e.toStringPol());
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
	   System.out.println("------------------------------------------------------------");

	   a.setComplexRec(111.1, -10.3);
	   d = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a))));
	   System.out.println("a         = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("cos+i·sin = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("------------------------------------------------------------");

	   a.setComplexRec(111.1, -10.3);
	   d = Complex.cos(a);
	   e = Complex.sin(a);
	   f = d.plus(e.times(Complex.i));
	   System.out.println("a   = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("cos = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("sin = " + e.toStringRec() + " : " + e.toStringPol());
	   System.out.println("cos+i·sin = " + f.toStringRec() + " : " + f.toStringPol());	   
	   System.out.println("------------------------------------------------------------");
	   
	   b.setComplexRec(21.4, 211.1);
	   d = Complex.cos(b);
	   e = Complex.sin(b);
	   f = d.plus(e.times(Complex.i));
	   System.out.println("b   = " + b.toStringRec() + " : " + b.toStringPol());
	   System.out.println("cos = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("sin = " + e.toStringRec() + " : " + e.toStringPol());
	   System.out.println("cos+i·sin = " + f.toStringRec() + " : " + f.toStringPol());	   
	   System.out.println("------------------------------------------------------------");

	   c.setComplexRec(101.7, 11.2);
	   d = Complex.cos(c);
	   e = Complex.sin(c);
	   f = d.plus(e.times(Complex.i));
	   System.out.println("c   = " + c.toStringRec() + " : " + c.toStringPol());
	   System.out.println("cos = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("sin = " + e.toStringRec() + " : " + e.toStringPol());
	   System.out.println("cos+i·sin = " + f.toStringRec() + " : " + f.toStringPol());	   
	   System.out.println("------------------------------------------------------------");
	   
	   a.setComplexRec(111.1, -10.3);
	   b.setComplexRec(21.4, 211.1);
	   c.setComplexRec(101.7, 11.2);
	   d = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).times((Complex.cos(b).plus(Complex.i.times(Complex.sin(b)))));
	   e = (Complex.cos(a.plus(b))).plus(Complex.i.times(Complex.sin(a.plus(b))));
	   System.out.println("a                   = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b                   = " + b.toStringRec() + " : " + b.toStringPol());
	   //System.out.println("c                   = " + c.toStringRec() + " : " + c.toStringPol());
	   System.out.println("PROD(cos+i·sin)     = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("cos(SUM)+i·sin(SUM) = " + e.toStringRec() + " : " + e.toStringPol());
	   System.out.println("------------------------------------------------------------");

	   a.setComplexRec(11.11, -1.03);
	   b.setComplexRec(2.14, 21.11);
	   c.setComplexRec(10.17, 1.12);
	   d = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).times((Complex.cos(b).plus(Complex.i.times(Complex.sin(b)))));
	   e = (Complex.cos(a.plus(b))).plus(Complex.i.times(Complex.sin(a.plus(b))));
	   System.out.println("a                   = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b                   = " + b.toStringRec() + " : " + b.toStringPol());
	   //System.out.println("c                   = " + c.toStringRec() + " : " + c.toStringPol());
	   System.out.println("PROD(cos+i·sin)     = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("cos(SUM)+i·sin(SUM) = " + e.toStringRec() + " : " + e.toStringPol());
	   System.out.println("------------------------------------------------------------");

	   a.setComplexRec(1.111, -.103);
	   b.setComplexRec(.214, 2.111);
	   c.setComplexRec(1.017, .112);
	   d = (Complex.cos(a).plus(Complex.i.times(Complex.sin(a)))).times((Complex.cos(b).plus(Complex.i.times(Complex.sin(b)))));
	   e = (Complex.cos(a.plus(b))).plus(Complex.i.times(Complex.sin(a.plus(b))));
	   System.out.println("a                   = " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b                   = " + b.toStringRec() + " : " + b.toStringPol());
	   //System.out.println("c                   = " + c.toStringRec() + " : " + c.toStringPol());
	   System.out.println("PROD(cos+i·sin)     = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("cos(SUM)+i·sin(SUM) = " + e.toStringRec() + " : " + e.toStringPol());
	   System.out.println("------------------------------------------------------------");

    }
}
