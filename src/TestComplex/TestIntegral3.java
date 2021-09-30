package TestComplex;

import java.util.function.Function;
import com.ipserc.arith.complex.*;

public class TestIntegral3 {
	
	private static Complex zSinCos(Complex z) {
		// z*sinz(z)*cos(z)
		return z.times(Complex.sin(z).times(Complex.cos(z)));
	}

	private static Complex func01(Complex z) {
		// sin²(2z)/cos³(3z)
		return (Complex.sin(z.times(2)).power(2)).divides(Complex.cos(z.times(3)).power(3));
	}

	private static Complex func02(Complex z) {
		// sin³(3z)/cos²(2z)
		return (Complex.sin(z.times(3)).power(3)).divides(Complex.cos(z.times(2)).power(2));
	}

	public static void main(String[] args) {
		Complex integral = new Complex();
		int decPrec = 5;
		Complex lolimit = new Complex();
		Complex uplimit = new Complex();
		Function<Complex, Complex> func;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "RIEMANN INTEGRAL TEST"));
		Complex.setScientificON(decPrec);
		
		func = z -> func01(z);
		lolimit.setComplexRec(-Complex.PI/8, 0);
		uplimit.setComplexRec(+Complex.PI/8, 0);
		System.out.println(Complex.boxTextRandom(boxSize, "Integral sin²(2z)/cos³(3z) (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin²(2z)/cos³(3z) (" + lolimit.toString() + ", " + uplimit.toString() + ") = " + integral.toString());

		func = z -> func02(z);
		lolimit.setComplexRec(-Complex.PI/8, 0);
		uplimit.setComplexRec(+Complex.PI/8, 0);
		System.out.println(Complex.boxTextRandom(boxSize, "Integral sin²(2z)/cos³(3z) (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin³(3z)/cos²(2z) (" + lolimit.toString() + ", " + uplimit.toString() + ") = " + integral.toString());
	}
}
