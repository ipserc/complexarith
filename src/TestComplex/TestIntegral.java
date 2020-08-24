package TestComplex;

import java.util.function.Function;

import com.ipserc.arith.complex.*;

public class TestIntegral {
	
	private static Complex zSinCos(Complex z) {
		// z*sinz(z)*cos(z)
		return z.times(Complex.sin(z).times(Complex.cos(z)));
	}

	public static void main(String[] args) {
		Complex integral = new Complex();
		Complex derivate = new Complex();
		Complex check;
		int decPrec = 3;
		String dashes = "------------------------------------------------------------------------------------";
		//String dashes = "-".repeat(80);
		Complex lolimit = new Complex();
		Complex uplimit = new Complex();
		Function<Complex, Complex> func;
		

/*****************************************/
		func = z -> Complex.sin(z);
		Complex.setFixedOFF();
		integral = Complex.integrate(0, Math.PI, func, decPrec);
		integral.println("Integral Seno (0, PI):");
		Complex.setFixedON(decPrec);
		integral.println("Integral Seno (PI, 2PI):");
		
		Complex.setFixedOFF();	
		integral = Complex.integrate(Math.PI, Complex.DOS_PI, func, decPrec);
		integral.println("Integral Seno (PI, 2PI):");
		Complex.setFixedON(decPrec);
		integral.println("Integral Seno (PI, 2PI):");

		func = z -> Complex.cos(z);
		Complex.setFixedOFF();
		integral = Complex.integrate(0, Complex.HALF_PI, func, decPrec);
		integral.println("Integral COSENO (0, PI/2):");
		Complex.setFixedON(decPrec);
		integral.println("Integral COSENO (0, PI/2):");		
		System.out.println(dashes);

		Complex.setFixedOFF();
		integral = Complex.integrate(Math.PI, Complex.DOS_PI, func, decPrec);
		integral.println("Integral COSENO (PI, 2PI):");
		Complex.setFixedON(decPrec);
		integral.println("Integral COSENO (PI, 2PI):");		

		func = z -> Complex.log(z);
		Complex.setFixedOFF();
		integral = Complex.integrate(-1, +1, func, decPrec);
		integral.println("Integral ln (-1, 1):");
		Complex.setFixedON(decPrec);
		integral.println("Integral ln (-1, 1):");

		func = z -> Complex.log10(z);
		Complex.setFixedOFF();
		integral = Complex.integrate(-1, +1, func, decPrec);
		integral.println("Integral log (-1, 1):");
		Complex.setFixedON(decPrec);
		integral.println("Integral log (-1, 1):");

		func = z -> zSinCos(z);
		Complex.setFixedOFF();
		integral = Complex.integrate(-1, +1, func, decPrec);
		integral.println("Integral zSinCos_i (-1, 1):");
		Complex.setFixedON(decPrec);
		integral.println("Integral zSinCos_i (-1, 1):");

		Complex.setFixedON(decPrec);
		//Complex.setFormatON();
		
		//Function<Complex, Complex> func = z -> Complex.sin(z);
		func = z -> Complex.sin(z);

		lolimit.setComplex("3+2i");
		uplimit.setComplex("-3+4i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.cos(lolimit).minus(Complex.cos(uplimit)).toString());
		System.out.println(dashes);

		lolimit.setComplex("-3-4i");
		uplimit.setComplex("3-2i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.cos(lolimit).minus(Complex.cos(uplimit)).toString());
		System.out.println(dashes);

		lolimit.setComplex("1+2i");
		uplimit.setComplex("7+5i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.cos(lolimit).minus(Complex.cos(uplimit)).toString());
		System.out.println(dashes);

		lolimit.setComplex("7+5i");
		uplimit.setComplex("1+2i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.cos(lolimit).minus(Complex.cos(uplimit)).toString());
		System.out.println(dashes);
		
		func = z -> Complex.inverse(z);
		lolimit.setComplex("3+2i");
		uplimit.setComplex("-3+4i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.log(uplimit).minus(Complex.log(lolimit)).toString());
		System.out.println(dashes);

		lolimit.setComplex("3-2i");
		uplimit.setComplex("-3+4i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.log(uplimit).minus(Complex.log(lolimit)).toString());
		System.out.println(dashes);

		func = z -> z;
		lolimit.setComplex("3+2i");
		uplimit.setComplex("4+2i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + (uplimit.power(2).minus(lolimit.power(2))).divides(2).toString());
		System.out.println(dashes);

		func = z -> z;
		lolimit.setComplex("3+2i");
		uplimit.setComplex("3+4i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + (uplimit.power(2).minus(lolimit.power(2))).divides(2).toString());
		System.out.println(dashes);

		func = z -> z;
		lolimit.setComplex("1+2i");
		uplimit.setComplex("2+9i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + (uplimit.power(2).minus(lolimit.power(2))).divides(2).toString());
		System.out.println(dashes);

		func = z -> Complex.inverse(z);
		lolimit.setComplex("1-2i");
		uplimit.setComplex("2-9i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.log(uplimit).minus(Complex.log(lolimit)).toString());
		System.out.println(dashes);
	
		func = z -> zSinCos(z);
		lolimit.setComplex("1-2i");
		uplimit.setComplex("1.2-2.9i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z*sinz(z)*cos(z) (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println(dashes);

		func = z -> Complex.log(z.times(Complex.cos(z)));
		lolimit.setComplex("1-2i");
		uplimit.setComplex("1.2-2.9i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral log(z*cos(z)) (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println(dashes);
/*****************************************/

	
		/**************************
		 * ESTE FALLA el CHECK!!!!!
		 *             Integral 1/z (2.000+2.000i, -6.000-3.000i = 0.864+2.820i
		 *       CHECK Integral 1/z (2.000+2.000i, -6.000-3.000i = 0.864-3.463i
		 */
		Complex.setFixedON(decPrec);
		Complex.setFixedOFF();	
		func = z -> Complex.inverse(z);
		uplimit.setComplex("2+2i");
		lolimit.setComplex("-6-3i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		check = Complex.log(uplimit).minus(Complex.log(lolimit));
		System.out.println("      ----- FAIL -----");
		System.out.println("      Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + check.toString());
		System.out.println("Cte Integración = " + (check.minus(integral)));
		System.out.println("log(" + lolimit.toString() + ") = " + Complex.log(lolimit).toString());
		System.out.println("log(" + uplimit.toString() + ") = " + Complex.log(uplimit).toString());
		System.out.println(dashes);

		func = z -> Complex.sin(z);
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.cos(lolimit).minus(Complex.cos(uplimit)).toString());
		System.out.println(dashes);

		func = z -> zSinCos(z);
		uplimit.setComplex("2e-3+2e-4i");
		lolimit.setComplex("-6e-2-3e-1i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z*sinz(z)*cos(z) (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println(dashes);

		func = z -> z;
		uplimit.setComplex("9.000E-01+3.000E-01i");
		lolimit.setComplex("1.050E+00+3.500E-01i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + (uplimit).power(2).divides(2).minus((lolimit).power(2).divides(2)).toString());
		System.out.println(dashes);

		Complex point = new Complex();
		Complex devVal = new Complex();

		func = z -> Complex.log(z);
		point.setComplex("2+2i");
		derivate = Complex.derivative(point, func, decPrec);
		System.out.println("      derivate log(z) (" + point.toString() + ")=" + derivate.toString());
		System.out.println("CHECK derivate log(z) (" + point.toString() + ")=" + Complex.inverse(point));
		
		func = z -> zSinCos(z);
		point.setComplex("2+2i");
		derivate = Complex.derivative(point, func, decPrec);
		System.out.println("      derivate zSinCos(z) (" + point.toString() + ")=" + derivate.toString());
		devVal = point.times(Complex.cos(point).power(2)).minus(point.times(Complex.sin(point).power(2))).plus(Complex.cos(point).times(Complex.sin(point)));
		System.out.println("CHECK derivate zSinCos(z) (" + point.toString() + ")=" + devVal.toString());

		Complex.setScientificON(decPrec);
		func = z -> z;
		uplimit.setComplex("9.000E-01+3.000E-01i");
		lolimit.setComplex("1.050E+00+3.500E-01i");
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + (uplimit).power(2).divides(2).minus((lolimit).power(2).divides(2)).toString());
		System.out.println(dashes);
	}
}
