package TestComplex;

import java.util.function.Function;

import com.ipserc.arith.complex.*;

public class TestIntegral01 {
	
	private static Complex zSinCos(Complex z) {
		// z*sinz(z)*cos(z)
		return z.times(Complex.sin(z).times(Complex.cos(z)));
	}

	public static void main(String[] args) {
		Complex integral = new Complex();
		Complex derivate = new Complex();
		Complex check;
		int decPrec = 3;
		//String dashes = "-".repeat(80);
		Complex lolimit = new Complex();
		Complex uplimit = new Complex();
		Function<Complex, Complex> func;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "RIEMANN INTEGRAL TEST"));
		func = z -> Complex.sin(z);
		{
			System.out.println(Complex.boxTextRandom(boxSize, "Integral Seno (0, PI) setFixedOFF"));
			Complex.setFixedOFF();
			integral = Complex.integrate(0, Math.PI, func, decPrec);
			integral.println("Integral Seno (0, PI):");
		}
		{
			System.out.println(Complex.boxTextRandom(boxSize, "Integral Seno (PI, 2PI) setFixedON"));
			Complex.setFixedON(decPrec);
			integral.println("Integral Seno (PI, 2PI):");
		}
		
		System.out.println(Complex.boxTextRandom(boxSize, "Integral Seno (PI, 2PI) setFixedOFF"));
		Complex.setFixedOFF();	
		integral = Complex.integrate(Math.PI, Complex.DOS_PI, func, decPrec);
		integral.println("Integral Seno (PI, 2PI):");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral Seno (PI, 2PI) setFixedON"));
		Complex.setFixedON(decPrec);
		integral.println("Integral Seno (PI, 2PI):");

		func = z -> Complex.cos(z);
		System.out.println(Complex.boxTextRandom(boxSize, "Integral Coseno (0, PI/2) setFixedOFF"));
		Complex.setFixedOFF();
		integral = Complex.integrate(0, Complex.HALF_PI, func, decPrec);
		integral.println("Integral Coseno (0, PI/2):");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral Coseno (0, PI/2) setFixedON"));
		Complex.setFixedON(decPrec);
		integral.println("Integral Coseno (0, PI/2):");		

		System.out.println(Complex.boxTextRandom(boxSize, "Integral Coseno (PI, 2PI) setFixedOFF"));
		Complex.setFixedOFF();
		integral = Complex.integrate(Math.PI, Complex.DOS_PI, func, decPrec);
		integral.println("Integral Coseno (PI, 2PI):");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral Coseno (PI, 2PI) setFixedON"));
		Complex.setFixedON(decPrec);
		integral.println("Integral COSENO (PI, 2PI):");		

		func = z -> Complex.log(z);
		System.out.println(Complex.boxTextRandom(boxSize, "Integral ln (-1, 1) setFixedOFF"));
		Complex.setFixedOFF();
		integral = Complex.integrate(-1, +1, func, decPrec);
		integral.println("Integral ln (-1, 1):");
		Complex.setFixedON(decPrec);
		System.out.println(Complex.boxTextRandom(boxSize, "Integral ln (-1, 1) setFixedON"));
		integral.println("Integral ln (-1, 1):");

		func = z -> Complex.log10(z);
		System.out.println(Complex.boxTextRandom(boxSize, "Integral log10 (-1, 1) setFixedOFF"));
		Complex.setFixedOFF();
		integral = Complex.integrate(-1, +1, func, decPrec);
		integral.println("Integral log (-1, 1):");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral log10 (-1, 1) setFixedON"));
		Complex.setFixedON(decPrec);
		integral.println("Integral log (-1, 1):");

		func = z -> zSinCos(z);
		System.out.println(Complex.boxTextRandom(boxSize, "Integral zSinCos_i (-1, 1) setFixedOFF"));
		Complex.setFixedOFF();
		integral = Complex.integrate(-1, +1, func, decPrec);
		integral.println("Integral zSinCos_i (-1, 1):");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral zSinCos_i (-1, 1) setFixedON"));
		Complex.setFixedON(decPrec);
		integral.println("Integral zSinCos_i (-1, 1):");

		Complex.setFixedON(decPrec);
		//Complex.setFormatON();
		
		//Function<Complex, Complex> func = z -> Complex.sin(z);
		func = z -> Complex.sin(z);

		lolimit.setComplex("3+2i");
		uplimit.setComplex("-3+4i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.cos(lolimit).minus(Complex.cos(uplimit)).toString());


		lolimit.setComplex("-3-4i");
		uplimit.setComplex("3-2i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.cos(lolimit).minus(Complex.cos(uplimit)).toString());


		lolimit.setComplex("1+2i");
		uplimit.setComplex("7+5i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.cos(lolimit).minus(Complex.cos(uplimit)).toString());


		lolimit.setComplex("7+5i");
		uplimit.setComplex("1+2i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.cos(lolimit).minus(Complex.cos(uplimit)).toString());

		
		func = z -> Complex.inverse(z);
		lolimit.setComplex("3+2i");
		uplimit.setComplex("-3+4i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.log(uplimit).minus(Complex.log(lolimit)).toString());


		lolimit.setComplex("3-2i");
		uplimit.setComplex("-3+4i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.log(uplimit).minus(Complex.log(lolimit)).toString());


		func = z -> z;
		lolimit.setComplex("3+2i");
		uplimit.setComplex("4+2i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + (uplimit.power(2).minus(lolimit.power(2))).divides(2).toString());


		func = z -> z;
		lolimit.setComplex("3+2i");
		uplimit.setComplex("3+4i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + (uplimit.power(2).minus(lolimit.power(2))).divides(2).toString());


		func = z -> z;
		lolimit.setComplex("1+2i");
		uplimit.setComplex("2+9i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + (uplimit.power(2).minus(lolimit.power(2))).divides(2).toString());


		func = z -> Complex.inverse(z);
		lolimit.setComplex("1-2i");
		uplimit.setComplex("2-9i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.log(uplimit).minus(Complex.log(lolimit)).toString());

	
		func = z -> zSinCos(z);
		lolimit.setComplex("1-2i");
		uplimit.setComplex("1.2-2.9i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral z*sinz(z)*cos(z) (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z*sinz(z)*cos(z) (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());


		func = z -> Complex.log(z.times(Complex.cos(z)));
		lolimit.setComplex("1-2i");
		uplimit.setComplex("1.2-2.9i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral log(z*cos(z)) (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral log(z*cos(z)) (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());

	
		/**************************
		 * ESTE FALLA el CHECK!!!!!
		 *             Integral 1/z (2.000+2.000i, -6.000-3.000i) = 0.864+2.820i
		 *       CHECK Integral 1/z (2.000+2.000i, -6.000-3.000i) = 0.864-3.463i
		 */
		Complex.setFixedON(decPrec);
		Complex.setFixedOFF();	
		func = z -> Complex.inverse(z);
		uplimit.setComplex("2+2i");
		lolimit.setComplex("-6-3i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		check = Complex.log(uplimit).minus(Complex.log(lolimit));
		System.out.println("      ----- FAIL -----");
		System.out.println("      Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral 1/z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + check.toString());
		System.out.println("Cte Integración = " + (check.minus(integral)));
		System.out.println("log(" + lolimit.toString() + ") = " + Complex.log(lolimit).toString());
		System.out.println("log(" + uplimit.toString() + ") = " + Complex.log(uplimit).toString());


		func = z -> Complex.sin(z);
		System.out.println(Complex.boxTextRandom(boxSize, "Integral sin(z) (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral sin (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + Complex.cos(lolimit).minus(Complex.cos(uplimit)).toString());


		func = z -> zSinCos(z);
		uplimit.setComplex("2e-3+2e-4i");
		lolimit.setComplex("-6e-2-3e-1i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral z*sinz(z)*cos(z) (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z*sinz(z)*cos(z) (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());


		func = z -> z;
		uplimit.setComplex("9.000E-01+3.000E-01i");
		lolimit.setComplex("1.050E+00+3.500E-01i");
		System.out.println(Complex.boxTextRandom(boxSize, "Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")"));
		integral = Complex.integrate(lolimit, uplimit, func, decPrec);
		System.out.println("      Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + integral.toString());
		System.out.println("CHECK Integral z (" + lolimit.toString() + ", " + uplimit.toString() + ")=" + (uplimit).power(2).divides(2).minus((lolimit).power(2).divides(2)).toString());


		System.out.println(Complex.boxTitleRandom(boxSize, "DERIVATION TEST"));
		
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

	}
}
