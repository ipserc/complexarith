package TestComplex;

import com.ipserc.arith.complex.*;

public class TestFormatNumber {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Complex a = new Complex("4-3i");
		//Complex b = new Complex("3.605551275463989|2.1587989303424644"); //"-2+3i");
		//Complex b = new Complex("3.605551275463989|5.3003915839322576384626433832795"); //"-2+3i");
		Complex b = new Complex("3.605551275463989|	8.4419842375220508769252867665590"); //"-2+3i");
		//Complex b = new Complex("3.605551275463989|11.5835768911118441153879301498385"); //"-2+3i");
		//Complex b = new Complex("3.605551275463989|14.7251695447016373538505735331180"); //"-2+3i");
		Complex c = new Complex("i");
		Complex d = new Complex();
		
		Complex.setFormatOFF();
		
		System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
		System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
		System.out.println("rep(a)		= " + a.rep());
		System.out.println("imp(a)		= " + a.imp());
		System.out.println("-a		= " + a.opposite().toStringRec() + " : " + a.opposite().toStringPol());        
		System.out.println("b + a		= " + b.plus(a));
		System.out.println("a - b		= " + a.minus(b));
		System.out.println("a * b		= " + a.times(b));
		System.out.println("b * a		= " + b.times(a));
		System.out.println("a / b		= " + a.divides(b));
		System.out.println("(a / b) * b	= " + a.divides(b).times(b));
		System.out.println("1/a		= " + a.reciprocal());
		System.out.println("------------------------------------------------------------");
		
		Complex.setFormatON();
		System.out.println("SIGNIFICATIVE:"+Complex.getSignificative());
		//Complex.setFixFloatON(3);
		
		System.out.println("a		= " + a.toStringRec() + " : " + a.toStringPol());
		System.out.println("b		= " + b.toStringRec() + " : " + b.toStringPol());
		System.out.println("rep(a)		= " + a.rep());
		System.out.println("imp(a)		= " + a.imp());
		System.out.println("-a		= " + a.opposite().toStringRec() + " : " + a.opposite().toStringPol());        
		System.out.println("b + a		= " + b.plus(a));
		System.out.println("a - b		= " + a.minus(b));
		System.out.println("a * b		= " + a.times(b));
		System.out.println("b * a		= " + b.times(a));
		System.out.println("a / b		= " + a.divides(b));
		System.out.println("(a / b) * b	= " + a.divides(b).times(b));
		System.out.println("1/a		= " + a.reciprocal());
		System.out.println("------------------------------------------------------------");

	}

}
