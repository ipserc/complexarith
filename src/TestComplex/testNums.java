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

public class testNums {

	public testNums() {
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

        Complex.setFormatON();
        System.out.println("a:"+a.toString());
        System.out.println("a:"+a.toStringPol());
        
        Complex.setFormatOFF();
        Complex.setFixedON(6);
        System.out.println("a:"+a.toString());
        System.out.println("a:"+a.toStringPol());

        Complex.setFormatON();
        Complex.setFixedON(6);
        System.out.println("a:"+a.toString());
        System.out.println("a:"+a.toStringPol());
	}
}
