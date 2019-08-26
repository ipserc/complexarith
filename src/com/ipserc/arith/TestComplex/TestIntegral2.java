package TestComplex;

import java.util.function.Function;

import com.ipserc.arith.complex.*;

public class TestIntegral2 {

	private static void printResult(Complex lolimit, Complex uplimit, Complex integral) {
		String dashes = "------------------------------------------------------------------------------------";
		System.out.println("      Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + ((uplimit).power(2).minus((lolimit).power(2)).divides(2)).toString());
		System.out.println(dashes);	
	}
	
	public static void main(String[] args) {
		Complex integral = new Complex();
		Complex derivate = new Complex();
		int decPrec = 3;
		//String dashes = "-".repeat(80);
		Complex lolimit = new Complex();
		Complex uplimit = new Complex();
		Function<Complex, Complex> func;

		Complex.setScientificON(decPrec);
		func = z -> z;
		lolimit.setComplex("9.000E-01+3.000E-01i");
		uplimit.setComplex("1.050E+00+3.500E-01i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec); printResult(lolimit, uplimit, integral);

		lolimit.setComplex("4.950E+00+1.650E+00i");
		uplimit.setComplex("5.100E+00+1.700E+00i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec); printResult(lolimit, uplimit, integral);
	}
}
