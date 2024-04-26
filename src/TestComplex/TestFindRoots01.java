/*
 * java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes TestComplex.TestFindRoots01
 */

package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.polynom.*;

public class TestFindRoots01 {
	
	public static Polynom showResults(Polynom polynom, Polynom divisor) {
		System.out.println("--------------------------------------------------");
		polynom.println("Original Polynomial");
		divisor.println("divisor");
		polynom = polynom.divides(divisor);
		polynom.println("Quotient");
		polynom.getRemainder().println("Remainder");
		System.out.println("--------------------------------------------------");
		return polynom;
	}

	public static void main(String[] args) {
       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	Polynom polynom = new Polynom("1.0,-7.0+8.0i,4.0+4.0i,-8.0+9.0i,-7.0+7.0i,-3.0-6.0i,1.0-8.0i,5.0-7.0i,-9.0-8.0i,8.0+7.0i,3.0+8.0i,6.0+8.0i,6.0+8.0i,-3.0-3.0i,2.0-1.0i,-6.0-6.0i");
		
		/* Raices
		  	[ 7.1035876-8.427250899999999i ] ESTA ES LA MALA
				[ 0.254456947+1.45909675i ]
				[ 0.41067637-1.17206437i ]
				[ -0.83343177+0.71201695i ]
				[ 0.92977454-0.46071633i ]
				[ -0.96586359-0.225533432i ]
				[ -0.98785806+0.061698594i ]
				[ -0.36217968-0.89543605i ]
				[ 0.44827866+0.85213038i ]
				[ 0.9191396000000001+0.170338861i ]
				[ -0.5068745-0.75449764i ]
				[ 0.157717675-0.86881274i ]
				[ -0.3728884+0.73421144i ]
				[ 0.0116202761+0.82158423i ]
				[ 0.79384432-0.0067657836i ]
		 */
		
		Polynom divisor = new Polynom("1, -0.254456947-1.45909675i");
		polynom = showResults(polynom, divisor);
		
		divisor = new Polynom("1, -0.41067637+1.17206437i"); 
		polynom = showResults(polynom, divisor);
		
		divisor = new Polynom("1, 0.83343177-0.71201695i"); 
		polynom = showResults(polynom, divisor);
		
		divisor = new Polynom("1, -0.92977454+0.46071633i"); 
		polynom = showResults(polynom, divisor);

		divisor = new Polynom("1, 0.96586359+0.225533432i"); 
		polynom = showResults(polynom, divisor);

		divisor = new Polynom("1, 0.98785806-0.061698594i"); 
		polynom = showResults(polynom, divisor);

		divisor = new Polynom("1, 0.36217968+0.89543605i"); 
		polynom = showResults(polynom, divisor);

		divisor = new Polynom("1, -0.44827866-0.85213038i"); 
		polynom = showResults(polynom, divisor);

		//divisor = new Polynom("1, -0.9191396000000001-0.170338861i"); 
		divisor = new Polynom("1, -0.9191396-0.170338861i"); 
		polynom = showResults(polynom, divisor);

		divisor = new Polynom("1, 0.5068745+0.75449764i"); 
		polynom = showResults(polynom, divisor);

		divisor = new Polynom("1, -0.157717675+0.86881274i"); 
		polynom = showResults(polynom, divisor);

		divisor = new Polynom("1, 0.3728884-0.73421144i"); 
		polynom = showResults(polynom, divisor);

		divisor = new Polynom("1, -0.0116202761-0.82158423i"); 
		polynom = showResults(polynom, divisor);

		divisor = new Polynom("1, -0.79384432+0.0067657836i"); 
		polynom = showResults(polynom, divisor);

       	Complex.setFormatOFF();
    	Complex.setFixedOFF();
		System.out.println("Exact result");
		divisor = polynom.copy();
		showResults(polynom, divisor);
		
		System.out.println("IN-Exact result");
		//This root should give  a bad reminder
		divisor = new Polynom("1, -7.1035876+8.427250899999999i"); 
		showResults(polynom, divisor);
}

}
