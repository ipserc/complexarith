package com.ipserc.arith.signal;

import com.ipserc.arith.complex.Complex;

/**
 * Sigfunc: signal functions.
 * @author ipserc
 *
 */
public class Sigfunc {

	private final static String HEADINFO = "Sigfunc --- INFO: ";
	private final static String VERSION = "1.4 (2026_0819_1200)";
	/* VERSION Release Note
	 *
	 * 1.4 (2026_0819_1200)
	 * ramp()/ramp0() and saw()/saw0() had their names crossed with respect to the waveform they
	 * actually compute: the old ramp()/ramp0() produced a discontinuous linear ramp that resets
	 * every period (a sawtooth), while the old saw()/saw0() produced a continuous folded wave
	 * (a triangle). Verified no caller in the project depends on the specific shape (only
	 * boundedness/periodicity are checked, which hold for both waveforms), so fixed by swapping
	 * the method names to match their real behaviour: ramp()/ramp0() now denote the sawtooth,
	 * saw()/saw0() now denote the triangle. Javadoc corrected accordingly.
	 * Also: U(Complex,double) and step(Complex,double) had identical bodies; U() now delegates to
	 * step(), which is the canonical name for the family (it also has 3-arg/4-arg siblings).
	 *
	 * 1.3 (2026_0811_1900)
	 * Comentarios Javadoc traducidos al inglés y corregidos (sin cambios funcionales), como parte
	 * de la generación de la documentación de la API.
	 *
	 * 1.2 (2026_0807_2030)
	 * step(Complex,double,double) (the non-periodic "rectangular function", 3-arg): used
	 * Math.abs(z.rep()), making it symmetric around 0 -- apparently copied from its periodic
	 * sibling step(Complex,int,double,double), which explicitly documents "simmetric respect 0"
	 * (this one's own Javadoc never mentions symmetry). Confirmed: step(-4,3,6) returned 1 (ON)
	 * even though -4 is nowhere near the documented [3,6] interval, because |-4|=4 falls in it.
	 * Fixed by dropping Math.abs() -- now a plain one-sided pulse, matching its own Javadoc. No
	 * active caller in the project uses this exact 3-arg overload (the TestFourier01/TestSigfunc01
	 * calls that look similar all pass an extra int argument, resolving to the 4-arg periodic
	 * overload instead) -- zero regression risk.
	 *
	 * 1.1 (2021_0228_0045)
	 */


	/*
	 * ***********************************************
	 * 	VERSION
	 * ***********************************************
	 */

	/**
	 * Prints the class version.
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
	 * Dirac delta function.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param t the point at which the delta is nonzero, as an int
	 * @return Complex.ONE at t, Complex.ZERO elsewhere
	 */
	public static Complex delta(Complex z, int t) {
		return (z.rep() == t ? Complex.ONE : Complex.ZERO);
	}

	//Delta Kronecker
	/**
	 * Kronecker delta. It is the Dirac delta with t = 0.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @return Complex.ONE at 0, Complex.ZERO elsewhere
	 */
	public static Complex delta(Complex z) {
		return delta(z, 0);
		//return (z.rep() == 0 ? Complex.ONE : Complex.ZERO);
	}

	// unit-step Heaviside function
	/**
	 * Unit-step Heaviside function.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param ton the point from which the unit step is nonzero, as a double
	 * @return Complex.ONE from ton onward, Complex.ZERO before it
	 */
	public static Complex step(Complex z, double ton) {
		return (z.rep() >= ton) ? Complex.ONE : Complex.ZERO;
	}

	/**
	 * Unit-step Heaviside function (alias of {@link #step(Complex, double)}).
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param ton the point from which the unit step is nonzero
	 * @return Complex.ONE from ton onward, Complex.ZERO before it
	 */
	public static Complex U(Complex z, double ton) {
		return step(z, ton);
	}

	/**
	 * Rectangular function.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param ton the point from which the rectangular function is nonzero, as a double
	 * @param toff the point from which the rectangular function becomes zero again, as a double
	 * @return Complex.ONE inside [ton, toff], Complex.ZERO outside it
	 */
	public static Complex step(Complex z, double ton, double toff) {
		return (z.rep() >= ton && z.rep() <= toff) ? Complex.ONE : Complex.ZERO;
	}

	/**
	 * Rectangular function of period T, symmetric about 0.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param T the period
	 * @param ton the point from which the rectangular function is nonzero, as a double
	 * @param toff the point from which the rectangular function becomes zero again, as a double
	 * @return Complex.ONE inside the pulse, Complex.ZERO outside it
	 */
	public static Complex step(Complex z, int T, double ton, double toff) {
		double x = Math.abs(z.rep()%T);
		return (Math.abs(x) >= ton && Math.abs(x) <= toff) ? Complex.ONE : Complex.ZERO;
	}

	/**
	 * Sawtooth wave function: linear ramp that rises across the period and resets
	 * discontinuously (a jump of 2*y1) at every multiple of T.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param T the period
	 * @param y1 the semi-amplitude of the signal
	 * @return the ordinate value of the wave at z, as a Complex
	 */
	public static Complex saw(Complex z, int T, double y1) {
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
	 * Sawtooth wave function centered at zero.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param T the period
	 * @param y1 the semi-amplitude of the signal
	 * @return the ordinate value of the wave at z, as a Complex
	 */
	public static Complex saw0(Complex z, int T,  double y1) {
		return saw(z.plus(T/2.0), T, y1);
	}

	/**
	 * Triangle (ramp) wave function: continuous, peaking at +a at x=0 and bottoming
	 * out at -a at x=T/2, folded symmetrically around each period boundary.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param T the period
	 * @param a the semi-amplitude of the signal
	 * @return the ordinate value of the wave at z, as a Complex
	 */
	public static Complex ramp(Complex z, int T, double a) {
		double x = Math.abs(z.rep()%T);
		double y;
		if (x >= 0 && x < T/2) y = -4*a/T*x+a;
		else y = 4*a/T*x-3*a;
		return new Complex(y, 0);
	}

	/**
	 * Triangle (ramp) wave function centered at zero.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param T the period
	 * @param a the semi-amplitude of the signal
	 * @return the ordinate value of the wave at z, as a Complex
	 */
	public static Complex ramp0(Complex z, int T, double a) {
		return ramp(z.plus(-T/4.0), T, a);
	}

	/*
	 * ***********************************************
	 * 	TRIGONOMETRIC FUNCTIONS
	 * ***********************************************
	 */

	/**
	 * Sine function with a given frequency.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param frec the frequency
	 * @return the ordinate value of the wave at z, as a Complex
	 */
	public static Complex sin(Complex z, double frec) {
		return Complex.sin(z.times(Complex.DOS_PI*frec));
	}

	/**
	 * Cosine function with a given frequency.
	 * @param z the point at which to evaluate the function, as a Complex
	 * @param frec the frequency
	 * @return the ordinate value of the wave at z, as a Complex
	 */
	public static Complex cos(Complex z, double frec) {
		return Complex.cos(z.times(Complex.DOS_PI*frec));
	}

	/**
	 * Sinc function (sin(z)/z, with sinc(0) = 1).
	 * @param z the point at which to evaluate the function, as a Complex
	 * @return the value of the function at z, as a Complex
	 */
	public static Complex sinc(Complex z) {
		return z.equals(Complex.ZERO) ? Complex.ONE : Complex.sin(z).divides(z);
	}

	/**
	 * Cosinc function ((1-cos(z))/z, with cosc(0) = 0).
	 * @param z the point at which to evaluate the function, as a Complex
	 * @return the value of the function at z, as a Complex
	 */
	public static Complex cosc(Complex z) {
		return z.equals(Complex.ZERO) ? Complex.ZERO : (Complex.ONE.minus(Complex.cos(z))).divides(z);
	}

	/**
	 * Cosinc function (alias of {@link #cosc(Complex)}).
	 * @param z the point at which to evaluate the function, as a Complex
	 * @return the value of the function at z, as a Complex
	 */
	public static Complex cosinc(Complex z) {
		return cosc(z);
	}

}
