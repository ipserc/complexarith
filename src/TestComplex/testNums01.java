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

public class testNums01 {

	public testNums01() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Complex a;
        a = new Complex("-8.111329172694093E-4-6.533903855322524E-4i");
        //a = new Complex("-8.111329172694093E-4-8.533903855322524E-4i");
        //a = new Complex("-8.111329172694093-8.533903855322524E-4i");
        //a = new Complex("-8.111329172694093E-4-8.533903855322524i");

        System.out.println("a:"+a.toString());
        System.out.println("a:"+a.toStringPol());

        Complex.setFormatON(true);
        System.out.println("a:"+a.toString());
        System.out.println("a:"+a.toStringPol());
        
        Complex.setFormatOFF(true);
        Complex.setFixedON(6);
        System.out.println("a:"+a.toString());
        System.out.println("a:"+a.toStringPol());

        Complex.setFormatON(false);
        Complex.setFixedON(8, true);
        System.out.println("a:"+a.toString());
        System.out.println("a:"+a.toStringPol());
	}
}
