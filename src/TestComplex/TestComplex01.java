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

public class TestComplex01 {
	
    public static void main(String[] args) {
    	int boxSize = 65;
    	Complex a = new Complex("4-3i");
        //Complex b = new Complex("3.605551275463989|2.1587989303424644"); //"-2+3i");
        //Complex b = new Complex("3.605551275463989|5.3003915839322576384626433832795"); //"-2+3i");
        Complex b = new Complex("3.605551275463989|	8.4419842375220508769252867665590"); //"-2+3i");
        //Complex b = new Complex("3.605551275463989|11.5835768911118441153879301498385"); //"-2+3i");
        //Complex b = new Complex("3.605551275463989|14.7251695447016373538505735331180"); //"-2+3i");
        Complex c = new Complex("i");
        Complex d = new Complex();
        Complex e = new Complex();
        Complex f = new Complex();
        
        System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX WITH FORMAT OFF TEST"));
        Complex.setFormatOFF(true);
        Complex.setRepres(Complex.Representation.POLAR);

        Complex.facts();

        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        System.out.println("rep(a)		= " + a.rep());
        System.out.println("imp(a)		= " + a.imp());
        System.out.println("-a		= " + a.opposite().toStringRec() + " : " + a.opposite().toStringPol());        
        System.out.println("b + a		= " + b.plus(a));
        System.out.println("a - b		= " + a.minus(b));
        System.out.println("a * b		= " + a.times(b));
        System.out.println("a * 100		= " + a.times(100));
        System.out.println("a * 100i	= " + a.times(new Complex("100i")));
        System.out.println("b * a		= " + b.times(a));
        System.out.println("a / b		= " + a.divides(b));
        System.out.println("(a / b) * b	= " + a.divides(b).times(b));
        System.out.println("1/a		= " + a.reciprocal());

        System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX WITH FORMAT ON TEST"));
        Complex.setFormatON(true);
        
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        System.out.println("rep(a)		= " + a.rep());
        System.out.println("imp(a)		= " + a.imp());
        System.out.println("-a		= " + a.opposite().toStringRec() + " : " + a.opposite().toStringPol());        
        System.out.println("b + a		= " + b.plus(a));
        System.out.println("a - b		= " + a.minus(b));
        System.out.println("a * b		= " + a.times(b));
        System.out.println("a * 100		= " + a.times(100));
        System.out.println("a * 100i	= " + a.times(new Complex("100i")));
        System.out.println("b * a		= " + b.times(a));
        System.out.println("a / b		= " + a.divides(b));
        System.out.println("(a / b) * b	= " + a.divides(b).times(b));
        System.out.println("1/a		= " + a.reciprocal());

        System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX OPERATIONS TEST"));
        System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX WITH FORMAT & FIXED ON TEST"));
        Complex.setFixedON(3, true);
        
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        System.out.println("rep(a)		= " + a.rep());
        System.out.println("imp(a)		= " + a.imp());
        System.out.println("-a		= " + a.opposite().toStringRec() + " : " + a.opposite().toStringPol());        
        System.out.println("b + a		= " + b.plus(a));
        System.out.println("a - b		= " + a.minus(b));
        System.out.println("a * b		= " + a.times(b));
        System.out.println("a * 100		= " + a.times(100));
        System.out.println("a * 100i	= " + a.times(new Complex("100i")));
        System.out.println("b * a		= " + b.times(a));
        System.out.println("a / b		= " + a.divides(b));
        System.out.println("(a / b) * b	= " + a.divides(b).times(b));
        System.out.println("1/a		= " + a.reciprocal());

        
        System.out.println("conj(a)		= " + a.conjugate());
        System.out.println("a*conj(a)	= " + a.times(a.conjugate()));
        System.out.println("|a|		= " + a.mod());
        System.out.println("tan(a)		= " + Complex.tan(a));
        System.out.println("log(a)		= " + Complex.log(a));
        System.out.println("log10(a)	= " + Complex.log10(a));
        System.out.println("a * a		= " + a.times(a));
        System.out.println("a^2		= " + a.power(2));
        System.out.println("a^(1/2) [power]	= " + a.power(.5));
        System.out.println("a^(1/2)	[root]	= " + Complex.root(a, 2));

        
        System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX ROOT & LOG TEST"));
        c.setComplex("-1");
        System.out.println("c		= " + c);
        System.out.println("c^(1/2)		= " + Complex.root(c,2));
        System.out.println("log(c)		= " + Complex.log(c));
        System.out.println("------------------------------------------------------------");

        c.setComplex("-2");
        System.out.println("c		= " + c);
        System.out.println("c^(1/2)		= " + Complex.root(c,2));
        System.out.println("log(c)		= " + Complex.log(c));
        System.out.println("------------------------------------------------------------");

        c.setComplex("-3");
        System.out.println("c		= " + c);
        System.out.println("c^(1/2)		= " + Complex.root(c,2));
        System.out.println("log(c)		= " + Complex.log(c));
        System.out.println("------------------------------------------------------------");
        
        c.setComplex("i");
        System.out.println("c		= " + c);
        System.out.println("c^(1/2)		= " + Complex.root(c,2));
        System.out.println("log(c)		= " + Complex.log(c));
        System.out.println("------------------------------------------------------------");
        
        c.setComplex("-i");
        System.out.println("c		= " + c);
        System.out.println("c^(1/2)		= " + Complex.root(c,2));
        System.out.println("log(c)		= " + Complex.log(c));
        System.out.println("------------------------------------------------------------");

        c.setComplex("-3i");
        System.out.println("c		= " + c);
        System.out.println("c^(1/2)		= " + Complex.root(c,2));
        System.out.println("log(c)		= " + Complex.log(c));
        System.out.println("------------------------------------------------------------");

        c.setComplex("4");
        System.out.println("c		= " + c);
        System.out.println("c^(1/2)		= " + Complex.root(c,2));
        System.out.println("log(c)		= " + Complex.log(c));

        System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX ALL ROOTS TEST"));
        c.setComplex("4 - 3i");
        System.out.println("c		= " + c);
        int pot = 5;
        for (int k = 0; k < pot; ++k) {
        	a = Complex.root(c, pot, k);
        	System.out.println("c^(1/"+ pot + ")[" + k + "]	= " + a.toStringRec() + " : " + a.toStringPol());
        	d = a.power(pot);
        	System.out.println("a^(" + pot + ")		= " + d.toStringRec() + "  Equals c " + (d.equals(c) ? "True" : "False"));
        }

        System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX POWER TEST"));
        a.setComplex("2");
        b.setComplex("4");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        c = a.power(b);
        System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("i");
        b.setComplex("i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        c = a.power(b);
        System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("-i");
        b.setComplex("-i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        c = a.power(b);
        System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("i");
        b.setComplex("-i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        c = a.power(b);
        System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("-i");
        b.setComplex("i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        c = a.power(b);
        System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("i");
        b.setComplex("1-2i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        c = a.power(b);
        System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("i");
        b.setComplex("i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        System.out.println("1/b		= " + b.reciprocal());
        c = a.power(b.reciprocal());
        System.out.println("c=a^(1/b)	= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("i");
        b.setComplex("-i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        System.out.println("1/b		= " + b.reciprocal());
        c = a.power(b.reciprocal());
        System.out.println("c=a^(1/b)	= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("10i");
        b.setComplex("1-2i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        c = a.power(b);
        System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("2i");
        b.setComplex("1-4i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        c = a.power(b);
        System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("1+i");
        b.setComplex("3i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        c = a.power(b);
        System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("-1+i");
        b.setComplex("3+i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        c = a.power(b);
        System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
        
        System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX LOG TEST"));
        a.setComplex("4-3i");
        b.setComplex("10");
        c = Complex.log10(a);
        d = b.power(c);
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
        System.out.println("c=log10(a)	= " + c.toStringRec() + " : " + c.toStringPol());
        System.out.println("d=10^(log10(a))	= " + d.toStringRec() + " : " + d.toStringPol());
 
        System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX STORE/RESTORE FORMAT TEST"));
        System.out.println("Initial format");
        Complex.printFormatStatus();
        System.out.println("Store initial format");
        Complex.storeFormatStatus();
        System.out.println("New format");
        Complex.setScientificON(3, true);
        a.setComplex("10.606601717798 + 10.606601717798i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("10.606601717798|.65343");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("1.60446E-5|.65343");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("------------------------------------------------------------");
        System.out.println("Repeat with restored format");
        Complex.restoreFormatStatus();
        Complex.printFormatStatus();
        a.setComplex("10.606601717798 + 10.606601717798i");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("10.606601717798|.65343");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        System.out.println("------------------------------------------------------------");

        a.setComplex("1.60446E-5|.65343");
        System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
        
        
        System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX LOG IN BASES TEST"));
        a.setComplex("1-i");
        System.out.println("log(1-i) in base -1 = "+(Complex.logb(a,-1)).toString());
        System.out.println("log(1-i) in base -3 = "+(Complex.logb(a,-3)).toString());
        System.out.println("log(1-i) in base +3 = "+(Complex.logb(a,3)).toString());
        b.setComplex("i");
        System.out.println("log(1-i) in base +i = "+(Complex.logb(a,b)).toString());
        b.setComplex("-i");
        System.out.println("log(1-i) in base -i = "+(Complex.logb(a,b)).toString());
        System.out.println("------------------------------------------------------------");
        
	   a.setComplex("2");
	   System.out.println("log("+a.toString()+") in base 2 = "+(Complex.logb(a,2)).toString());
	   System.out.println("log("+a.toString()+") in base -2 = "+(Complex.logb(a,-2)).toString());
	   b.setComplex("i");
	   System.out.println("log("+a.toString()+") in base +i = "+(Complex.logb(a,b)).toString());
	   b.setComplex("-i");
	   System.out.println("log("+a.toString()+") in base -i = "+(Complex.logb(a,b)).toString());
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("-2");
	   System.out.println("log("+a.toString()+") in base 2 = "+(Complex.logb(a,2)).toString());
	   System.out.println("log("+a.toString()+") in base -2 = "+(Complex.logb(a,-2)).toString());
	   b.setComplex("i");
	   System.out.println("log("+a.toString()+") in base +i = "+(Complex.logb(a,b)).toString());
	   b.setComplex("-i");
	   System.out.println("log("+a.toString()+") in base -i = "+(Complex.logb(a,b)).toString());
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("-3+.5i");
	   System.out.println("log("+a.toString()+") in base -1 = "+(Complex.logb(a,-1)).toString());
	   System.out.println("log("+a.toString()+") in base -3 = "+(Complex.logb(a,-3)).toString());
	   System.out.println("log("+a.toString()+") in base +3 = "+(Complex.logb(a,3)).toString());
	   b.setComplex("i");
	   System.out.println("log("+a.toString()+") in base +i = "+(Complex.logb(a,b)).toString());
	   b.setComplex("-i");
	   System.out.println("log("+a.toString()+") in base -i = "+(Complex.logb(a,b)).toString());
	  
       System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX CALCULATIONS TEST"));
	   a.setComplex("-1-3i");
	   b.setComplex("3+2i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
	   c = a.power(b);
	   System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
	   d = Complex.exp(Complex.log(c).divides(b));
	   System.out.println("exp(log(c)/b)	= " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("------------------------------------------------------------");      
	
	   a.setComplex("-1-3i");
	   b.setComplex("13+21i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
	   c = a.power(b);
	   System.out.println("c=a^b		= " + c.toStringRec() + " : " + c.toStringPol());
	   d = Complex.exp(Complex.log(c).divides(b));
	   System.out.println("exp(log(c)/b)	= " + d.toStringRec() + " : " + d.toStringPol());
	   
       System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX TRIGONOMETRICS TEST"));
	   a.setComplex("1+3i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.sin(a);
	   System.out.println("c=sin(a)	= " + c.toStringRec() + " : " + c.toStringPol());
	   c = Complex.arcsin(c);
	   System.out.println("c=arcsin(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("1-3i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.sin(a);
	   System.out.println("c=sin(a)	= " + c.toStringRec() + " : " + c.toStringPol());
	   c = Complex.arcsin(c);
	   System.out.println("c=arcsin(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("-1-3i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.sin(a);
	   System.out.println("c=sin(a)	= " + c.toStringRec() + " : " + c.toStringPol());
	   c = Complex.arcsin(c);
	   System.out.println("c=arcsin(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("-1+3i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.sin(a);
	   System.out.println("c=sin(a)	= " + c.toStringRec() + " : " + c.toStringPol());
	   c = Complex.arcsin(c);
	   System.out.println("c=arcsin(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("1+3i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.cos(a);
	   System.out.println("c=cos(a)	= " + c.toStringRec() + " : " + c.toStringPol());
	   c = Complex.arccos(c);
	   System.out.println("c=arccos(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("1-3i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.cos(a);
	   System.out.println("c=cos(a)	= " + c.toStringRec() + " : " + c.toStringPol());
	   c = Complex.arccos(c);
	   System.out.println("c=arccos(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("-1+3i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.cos(a);
	   System.out.println("c=cos(a)	= " + c.toStringRec() + " : " + c.toStringPol());
	   c = Complex.arccos(c);
	   System.out.println("c=arccos(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("-1-3i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.cos(a);
	   System.out.println("c=cos(a)	= " + c.toStringRec() + " : " + c.toStringPol());
	   c = Complex.arccos(c);
	   System.out.println("c=arccos(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   
       System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX LOG TEST"));
	   a.setComplex("-2");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.log(a);
	   System.out.println("c=log(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	   
	   a.setComplex("2+2i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.log(a);
	   System.out.println("c=log(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	   
	   a.setComplex("-2+2i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.log(a);
	   System.out.println("c=log(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("2-2i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.log(a);
	   System.out.println("c=log(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	  
	       a.setComplex("-2-2i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   c = Complex.log(a);
	   System.out.println("c=log(c)	= " + c.toStringRec() + " : " + c.toStringPol());       
	   
       System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX DIVISION TEST"));
	   a.setComplex("13");
	   b.setComplex("7");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
	   c = a.divides(b);
	   System.out.println("c=a/b		= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	
	   a.setComplex("13i");
	   b.setComplex("7i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
	   c = a.divides(b);
	   System.out.println("c=a/b		= " + c.toStringRec() + " : " + c.toStringPol());       
	   System.out.println("------------------------------------------------------------");
	 
	       a.setComplex("27+13i");
	   b.setComplex("8-7i");
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
	   c = a.divides(b);
	   System.out.println("c=a/b		= " + c.toStringRec() + " : " + c.toStringPol());       
	   
       System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX CALCULATIONS 2 TEST"));
	   a.setComplexPol(9.0E-1, 0);
	   b.setComplexPol(9.0E-1, 3.141592653589793);
	   System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
	   System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
	   c = Complex.sin(a);
	   d = Complex.sin(b);
	   e = a.times(c);
	   f = b.times(d);
	   System.out.println("sin(a)		= " + c.toStringRec() + " : " + c.toStringPol());
	   System.out.println("sin(b)		= " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("a*sin(a)	= " + e.toStringRec() + " : " + e.toStringPol());
	   System.out.println("b*sin(b)	= " + f.toStringRec() + " : " + f.toStringPol());
	   System.out.println("------------------------------------------------------------");
	   
       System.out.println(Complex.boxTextRandom(boxSize, "COMPLEX POSITIVE TEST"));
	   a.setComplex("1+3i").println("a = ");
	   b.setComplex("1+5i").println("b = ");
	   c = a.plus(b);
	   System.out.println("a+b = " + c.toStringRec() + " : " + c.toStringPol());	   
	   d = a.times(b);
	   System.out.println("a*b = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("------------------------------------------------------------");
	   a.setComplex("3+i").println("a = ");
	   b.setComplex("5+i").println("b = ");
	   c = a.plus(b);
	   System.out.println("a+b = " + c.toStringRec() + " : " + c.toStringPol());	   
	   d = a.times(b);
	   System.out.println("a*b = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("------------------------------------------------------------");
	   a.setComplex("1+3i").println("a = ");
	   b.setComplex("5+i").println("b = ");
	   c = a.plus(b);
	   System.out.println("a+b = " + c.toStringRec() + " : " + c.toStringPol());	   
	   d = a.times(b);
	   System.out.println("a*b = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("------------------------------------------------------------");
	   a.setComplex("3+i").println("a = ");
	   b.setComplex("1+5i").println("b = ");
	   c = a.plus(b);
	   System.out.println("a+b = " + c.toStringRec() + " : " + c.toStringPol());	   
	   d = a.times(b);
	   System.out.println("a*b = " + d.toStringRec() + " : " + d.toStringPol());
	   System.out.println("------------------------------------------------------------");
	   
    }
}
