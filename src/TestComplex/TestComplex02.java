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

public class TestComplex02 {
    public static void main(String[] args) {
        Complex a = new Complex();
        Complex b = new Complex();
        Complex c, d , e, f;

        Complex.setFormatON();
        Complex.setFixedON(3);
       
       a.setComplexPol(9.0E-1, 0);
       b.setComplexPol(9.0E-1, 3.141592653589793);
       System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
       System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
       c = Complex.sin(a);
       d = Complex.sin(b);
       e = a.times(c);
       f = b.times(d);
       System.out.println("sin(a)   = " + c.toStringRec() + " : " + c.toStringPol());
       System.out.println("sin(b)   = " + d.toStringRec() + " : " + d.toStringPol());
       System.out.println("a*sin(a) = " + e.toStringRec() + " : " + e.toStringPol());
       System.out.println("b*sin(b) = " + f.toStringRec() + " : " + f.toStringPol());

       System.out.println("------------------------------------------------------------");
       
       a.setComplex("1.999999999+0i");
       a = Complex.round(a, 3);
       a.println("a:");

       a.setComplex("1+1.999999999i");
       a = Complex.round(a, 3);
       a.println("a:");
       
       a.setComplex("1.899999999+0i");
       a = Complex.round(a, 3);
       a.println("a:");

       a.setComplex("1+1.899999999i");
       a = Complex.round(a, 3);
       a.println("a:");

       a.setComplex("1.85555+0i");
       a = Complex.round(a, 3);
       a.println("a:");

       a.setComplex("1+1.85555i");
       a = Complex.round(a, 3);
       a.println("a:");

       a.setComplex("1.8555+1i");
       a = Complex.round(a, 3);
       a.println("a:");

       a.setComplex("0+1.8555i");
       a = Complex.round(a, 3);
       a.println("a:");

       a.setComplex("1.8554+0i");
       a = Complex.round(a, 3);
       a.println("a:");

       a.setComplex("0+1.8554i");
       a = Complex.round(a, 3);
       a.println("a:");

       a.setComplex("1.855+0i");
       a = Complex.round(a, 3);
       a.println("a:");

       a.setComplex("0+1.855i");
       a = Complex.round(a, 3);
       a.println("a:");
    }
}
