package com.ipserc.arith.signal;

import com.ipserc.arith.complex.Complex;

/**
 * Sigfunc: Signal functions
 * @author ipserc
 *
 */
public class Sigfunc {

	private final static String HEADINFO = "Sigfunc --- INFO: ";
	private final static String VERSION = "1.1 (2021_0228_0045)";
	/* VERSION Release Note
	 * 
	 * 1.1 (2021_0228_0045)
	 */


	/*
	 * ***********************************************
	 * 	VERSION 
	 * ***********************************************
	 */
	
	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION); 
	}

	/*
	 * ***********************************************
	 * 	DISCONTINOUS FUNCTIONS 
	 * ***********************************************
	 */
	
	// Delta Dirac
	/**
	 * Dirac Delta
	 * @param z Point to evaluate the function as Complex
	 * @param t Point in which Delta is not null as int
	 * @return Complex Zero or One
	 */
	public static Complex delta(Complex z, int t) {
		return (z.rep() == t ? Complex.ONE : Complex.ZERO);
	}
	
	//Delta Kronecker
	/**
	 * Kronecker Delta. Is the Dirac Delta with t = 0
	 * @param z Point to evaluate the function as Complex
	 * @return Complex Zero or One
	 */
	public static Complex delta(Complex z) {
		return delta(z, 0);
		//return (z.rep() == 0 ? Complex.ONE : Complex.ZERO);
	}
	
	// unit-step Heaviside function
	/**
	 * Unit-step Heaviside function
	 * @param z Point to evaluate the function as Complex
	 * @param ton Point from which Unit Step is not null
	 * @return Complex Zero or One
	 */
	public static Complex U(Complex z, double ton) {
		return (z.rep() >= ton ? Complex.ONE : Complex.ZERO);
	}
	
	/**
	 * Unit-step Heaviside function
	 * @param z Point to evaluate the function as Complex
	 * @param ton Point from which Unit Step is not null as double
	 * @return Complex Zero or One
	 */
	public static Complex step(Complex z, double ton) {
		return (z.rep() >= ton) ? Complex.ONE : Complex.ZERO;
	}

	/**
	 * Rectangular function
	 * @param z Point to evaluate the function as Complex
	 * @param ton Point from which rectangular function is not null as double
	 * @param toff Point from which rectangular function becomes null as double
	 * @return Complex Zero or One
	 */
	public static Complex step(Complex z, double ton, double toff) {
		return (Math.abs(z.rep()) >= ton && Math.abs(z.rep()) <= toff) ? Complex.ONE : Complex.ZERO;
	}

	/**
	 * Rectangular function of period T simmetric respect 0
	 * @param z Point to evaluate the function as Complex
	 * @param T The period
	 * @param ton Point from which rectangular function is not null as double
	 * @param toff toff Point from which rectangular function becomes null as double
	 * @return Complex Zero or One
	 */
	public static Complex step(Complex z, int T, double ton, double toff) {
		double x = Math.abs(z.rep()%T);
		return (Math.abs(x) >= ton && Math.abs(x) <= toff) ? Complex.ONE : Complex.ZERO;
	}

	/**
	 * Ramp function
	 * @param z Point to evaluate the function as Complex
	 * @param T The period
	 * @param y1 Te semi-amplitud of the signal
	 * @return Complex abscissa value
	 */
	public static Complex ramp(Complex z, int T, double y1) {
		y1 = Math.abs(y1);
		double y0 = -y1; 
		double zr = z.rep();
		double x0 = -T;
		double x1 = T;
		double x = zr%(T);
		double y = y1-(y1-y0)/(x1-x0)*(x1-x);
		y = zr < 0 ? 2*y+y1 : 2*y-y1;
		return new Complex(y, 0);
	}

	/**
	 * Ramp function centered at zero
	 * @param z Point to evaluate the function as Complex
	 * @param T The period
	 * @param y1 Te semi-amplitud of the signal
	 * @return Complex abscissa value
	 */
	public static Complex ramp0(Complex z, int T,  double y1) {
		return ramp(z.plus(T/2.0), T, y1);
	}

	/**
	 * Saw function
	 * @param z Point to evaluate the function as Complex
	 * @param T The period
	 * @param a The semi-amplitud of the signal
	 * @return Complex abscissa value
	 */
	public static Complex saw(Complex z, int T, double a) {
		double x = Math.abs(z.rep()%T);
		double y;
		if (x >= 0 && x < T/2) y = -4*a/T*x+a;
		else y = 4*a/T*x-3*a;
		return new Complex(y, 0);
	}
	
	/**
	 * Saw function centered at zero
	 * @param z Point to evaluate the function as Complex
	 * @param T The period
	 * @param a The semi-amplitud of the signal
	 * @return Complex abscissa value
	 */
	public static Complex saw0(Complex z, int T, double a) {
		return saw(z.plus(-T/4.0), T, a);
	}

	/*
	 * ***********************************************
	 * 	TRIGONOMETRIC FUNCTIONS 
	 * ***********************************************
	 */

	/**
	 * SIN function
	 * @param z Point to evaluate the function as Complex
	 * @param frec The frequency
	 * @return Complex abscissa value
	 */
	public static Complex sin(Complex z, double frec) {
		return Complex.sin(z.times(Complex.DOS_PI*frec));
	}

	/**
	 * COS function
	 * @param z Point to evaluate the function as Complex
	 * @param frec The frequency
	 * @return Complex abscissa value
	 */
	public static Complex cos(Complex z, double frec) {
		return Complex.cos(z.times(Complex.DOS_PI*frec));
	}
		
	/**
	 * SINC function
	 * @param z Point to evaluate the function as Complex
	 * @return Complex abscissa value
	 */
	public static Complex sinc(Complex z) {
		return z.equals(Complex.ZERO) ? Complex.ONE : Complex.sin(z).divides(z);
	}
	
	/**
	 * COSINC function
	 * @param z Point to evaluate the function as Complex
	 * @return Complex abscissa value
	 */
	public static Complex cosc(Complex z) {
		return z.equals(Complex.ZERO) ? Complex.ZERO : (Complex.ONE.minus(Complex.cos(z))).divides(z);
	}

	/**
	 * COSINC function (another alias)
	 * @param z Point to evaluate the function as Complex
	 * @return Complex abscissa value
	 */
	public static Complex cosinc(Complex z) {
		return cosc(z);
	}

}
