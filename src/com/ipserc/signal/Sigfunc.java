package com.ipserc.signal;

import com.ipserc.arith.complex.Complex;

public class Sigfunc {

	// Delta Dirac
	public static Complex delta(Complex z, int t) {
		return (z.rep() == t ? Complex.ONE : Complex.ZERO);
	}
	
	//Delta Kronecker
	public static Complex delta(Complex z) {
		return delta(z, 0);
		//return (z.rep() == 0 ? Complex.ONE : Complex.ZERO);
	}
	
	// unit-step Heaviside function
	public static Complex U(Complex z, int t) {
		return (z.rep() > t ? Complex.ONE : Complex.ZERO);
	}
	
	public static Complex step(Complex z, double ton) {
		return (z.rep() >= ton) ? Complex.ONE : Complex.ZERO;
	}

	public static Complex step(Complex z, double ton, double toff) {
		return (Math.abs(z.rep()) >= ton && Math.abs(z.rep()) <= toff) ? Complex.ONE : Complex.ZERO;
	}

	public static Complex step(Complex z, int T, double ton, double toff) {
		double x = Math.abs(z.rep()%T);
		return (Math.abs(x) >= ton && Math.abs(x) <= toff) ? Complex.ONE : Complex.ZERO;
	}

	public static Complex ramp(Complex z, int T, double y0, double y1) {
		double zr = z.rep();
		double x0 = -T;
		double x1 = T;
		double x = zr%(T);
		double y = y1-(y1-y0)/(x1-x0)*(x1-x);
		y = zr < 0 ? 2*y+y1 : 2*y-y1;
		return new Complex(y, 0);
	}

	public static Complex ramp0(Complex z, int T, double y0, double y1) {
		return ramp(z.plus(T/2.0), T, y0, y1);
	}

	public static Complex sin(Complex z, double frec) {
		return Complex.sin(z.times(Complex.DOS_PI*frec));
	}

	public static Complex cos(Complex z, double frec) {
		return Complex.cos(z.times(Complex.DOS_PI*frec));
	}
		
	public static Complex sinc(Complex z) {
		return z.equals(Complex.ZERO) ? Complex.ONE : Complex.sin(z).divides(z);
	}
	
	public static Complex cosc(Complex z) {
		return z.equals(Complex.ZERO) ? Complex.ZERO : (Complex.ONE.minus(Complex.cos(z))).divides(z);
	}

	public static Complex cosinc(Complex z) {
		return cosc(z);
	}

	public static Complex saw(Complex z, int T, double a) {
		double x = Math.abs(z.rep()%T);
		double y;
		if (x >= 0 && x < T/2) y = -4*a/T*x+a;
		else y = 4*a/T*x-3*a;
		return new Complex(y, 0);
	}
	
	public static Complex saw0(Complex z, int T, double a) {
		return saw(z.plus(-T/4.0), T, a);
	}
}
