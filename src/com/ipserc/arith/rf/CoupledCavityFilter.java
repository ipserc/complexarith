package com.ipserc.arith.rf;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * A bandpass filter built by CLASSICAL filter synthesis (handbook lowpass-prototype tables plus
 * an impedance-inverter/coupled-resonator equivalent circuit -- Matthaei-Young-Jones' "direct
 * coupled-cavity filter" -- Pozar's "Microwave Engineering" Sec. 8.6), not from Maxwell's
 * equations as {@link EvanescentModeFilter} is. This is the "option 1" contrast the user asked
 * for explicitly: it starts from the DESIRED response (Chebyshev, order {@code N}, ripple
 * {@code rippleDB}) and derives an equivalent circuit that produces it, rather than starting from
 * a physical geometry and computing whatever response comes out.
 * <p>
 * Physically, each series resonator below stands in for one resonant cavity, and each impedance
 * inverter stands in for the iris (coupling aperture) between two cavities -- the classical design
 * flow computes exactly the coupling {@code K_i} each iris must realize, leaving the actual iris
 * dimensioning (an EM full-wave problem) as a later, separate step not modeled here.
 * <p>
 * Design flow:
 * <ol>
 * <li>{@link #chebyshevPrototype} -- the lowpass-prototype element values {@code g_0..g_(N+1)},
 * closed-form (Pozar Table 8.4's generating formula), for a Chebyshev response of order {@code N}
 * and passband ripple {@code rippleDB}.</li>
 * <li>{@link #couplingInverters} -- the {@code N+1} impedance-inverter values {@code K_01..K_N,N+1}
 * from the prototype, the reference impedance {@code z0} and the fractional bandwidth
 * {@code fbw=BW/f0} (Pozar eq. 8.77).</li>
 * <li>{@link #abcd}/{@link #sParameters} -- the lossless {@code ABCD} cascade of alternating ideal
 * inverters ({@code [[0,jK],[j/K,0]]}) and series reactance resonators ({@code [[1,jX(f)],[0,1]]},
 * {@code X(f)=z0*(f/f0-f0/f)}, all resonators given the same reactance slope parameter {@code z0}
 * -- a standard, convenient normalization, Pozar eq. 8.72), same {@code S}-parameter formula as
 * {@link EvanescentModeFilter#sParameters} (constant {@code z0} reference here, instead of a
 * guide's own frequency-dependent wave impedance).</li>
 * </ol>
 * Verified in {@code TestRF_CoupledCavityFilter01}: losslessness, {@code N} equal-ripple
 * passband peaks (the Chebyshev signature), and the prototype values against a hand-checked
 * reference case.
 */
public final class CoupledCavityFilter {

	private final static String HEADINFO = "CoupledCavityFilter --- INFO: ";
	private final static String VERSION = "1.0 (2026_0825_1300)";
	/* VERSION Release Note
	 *
	 * 1.0 (2026_0825_1300)
	 * Cuarta clase de com.ipserc.arith.rf, y primera de "sintesis clasica" (no derivada de
	 * Maxwell): prototipo Chebyshev paso bajo + inversores de impedancia + resonadores serie, en
	 * contraste explicito con EvanescentModeFilter (fisica de ondas directa).
	 */

	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION);
	}

	/**
	 * The Chebyshev lowpass-prototype element values {@code g_0,g_1,...,g_N,g_(N+1)} (length
	 * {@code N+2}), {@code g_0=1} always (source reference).
	 * @param order The filter order {@code N}. Must be positive.
	 * @param rippleDB The passband ripple, dB. Must be positive.
	 * @return {@code {g_0, g_1, ..., g_N, g_(N+1)}}.
	 * @throws IllegalArgumentException if {@code order} is not positive or {@code rippleDB} is not
	 * positive.
	 */
	public static double[] chebyshevPrototype(int order, double rippleDB) {
		if (order < 1) {
			throw new IllegalArgumentException(HEADINFO + "order must be positive, got " + order);
		}
		if (rippleDB <= 0) {
			throw new IllegalArgumentException(HEADINFO + "rippleDB must be positive, got " + rippleDB);
		}
		double beta = Math.log(1.0/Math.tanh(rippleDB/17.37));
		double gamma = Math.sinh(beta/(2*order));

		double[] g = new double[order+2];
		g[0] = 1;
		double[] a = new double[order+1], b = new double[order+1];
		for (int i = 1; i <= order; ++i) {
			a[i] = Math.sin((2*i-1)*Math.PI/(2*order));
			b[i] = gamma*gamma + Math.sin(i*Math.PI/order)*Math.sin(i*Math.PI/order);
		}
		g[1] = 2*a[1]/gamma;
		for (int i = 2; i <= order; ++i) {
			g[i] = 4*a[i-1]*a[i] / (b[i-1]*g[i-1]);
		}
		g[order+1] = (order % 2 == 1) ? 1.0 : (1.0/Math.tanh(beta/4))*(1.0/Math.tanh(beta/4));
		return g;
	}

	private final int order;
	private final double f0, fbw, z0;
	private final double[] g;
	private final double[] k; // k[0..order], the N+1 impedance inverters K_01..K_N,(N+1)

	/**
	 * @param order The filter order {@code N} (number of coupled cavities). Must be positive.
	 * @param rippleDB The Chebyshev passband ripple, dB. Must be positive.
	 * @param f0 The center frequency, Hz. Must be positive.
	 * @param fbw The fractional bandwidth {@code BW/f0} (dimensionless, e.g. {@code 0.05} for 5%).
	 * Must be positive.
	 * @param z0 The reference impedance, Ohm (also the reactance slope parameter given to every
	 * resonator, see class Javadoc). Must be positive.
	 * @throws IllegalArgumentException if any argument is out of its stated range.
	 */
	public CoupledCavityFilter(int order, double rippleDB, double f0, double fbw, double z0) {
		if (f0 <= 0 || fbw <= 0 || z0 <= 0) {
			throw new IllegalArgumentException(HEADINFO + "f0, fbw and z0 must all be positive");
		}
		this.order = order;
		this.f0 = f0;
		this.fbw = fbw;
		this.z0 = z0;
		this.g = chebyshevPrototype(order, rippleDB); // validates order/rippleDB
		this.k = couplingInverters(g, fbw, z0);
	}

	/**
	 * The {@code N+1} impedance inverters {@code K_01,...,K_N,(N+1)} (Pozar eq. 8.77) from the
	 * lowpass-prototype {@code g} values, fractional bandwidth {@code fbw} and reference impedance
	 * {@code z0}.
	 */
	private static double[] couplingInverters(double[] g, double fbw, double z0) {
		int order = g.length - 2;
		double[] k = new double[order+1];
		k[0] = Math.sqrt(z0*z0*fbw/(g[0]*g[1]));
		for (int i = 1; i < order; ++i) {
			k[i] = z0*fbw/Math.sqrt(g[i]*g[i+1]);
		}
		k[order] = Math.sqrt(z0*z0*fbw/(g[order]*g[order+1]));
		return k;
	}

	/** The lowpass-prototype element values used, {@code {g_0,...,g_(N+1)}} (a defensive copy). */
	public double[] gValues() {
		return g.clone();
	}

	/** The impedance-inverter values used, {@code {K_01,...,K_N,(N+1)}} (a defensive copy). */
	public double[] couplingInverters() {
		return k.clone();
	}

	/** {@code [[0,j*K],[j/K,0]]}, the ABCD matrix of an ideal impedance inverter. */
	private static MatrixComplex inverterABCD(double kValue) {
		MatrixComplex abcd = new MatrixComplex(2, 2);
		abcd.setItem(0, 0, new Complex(0, 0));
		abcd.setItem(0, 1, Complex.i.times(kValue));
		abcd.setItem(1, 0, Complex.i.divides(new Complex(kValue, 0)));
		abcd.setItem(1, 1, new Complex(0, 0));
		return abcd;
	}

	/** {@code [[1,j*X(f)],[0,1]]}, the ABCD matrix of a series reactance {@code X(f)=z0*(f/f0-f0/f)}. */
	private MatrixComplex resonatorABCD(double frequency) {
		double reactance = z0*(frequency/f0 - f0/frequency);
		MatrixComplex abcd = new MatrixComplex(2, 2);
		abcd.setItem(0, 0, new Complex(1, 0));
		abcd.setItem(0, 1, Complex.i.times(reactance));
		abcd.setItem(1, 0, new Complex(0, 0));
		abcd.setItem(1, 1, new Complex(1, 0));
		return abcd;
	}

	/**
	 * The overall {@code ABCD} matrix of the inverter/resonator cascade,
	 * {@code K_01, res_1, K_12, res_2, ..., res_N, K_N,(N+1)}, at {@code frequency}.
	 * @param frequency The operating frequency, Hz. Must be positive.
	 * @return The {@code 2x2} cascaded ABCD matrix.
	 */
	public MatrixComplex abcd(double frequency) {
		if (frequency <= 0) {
			throw new IllegalArgumentException(HEADINFO + "frequency must be positive, got " + frequency);
		}
		MatrixComplex cascade = inverterABCD(k[0]);
		for (int i = 1; i <= order; ++i) {
			cascade = cascade.times(resonatorABCD(frequency)).times(inverterABCD(k[i]));
		}
		return cascade;
	}

	/**
	 * The scattering parameters {@code {S11, S21}} at {@code frequency}, referenced to {@code z0}
	 * on both ports.
	 * @param frequency The operating frequency, Hz. Must be positive.
	 * @return {@code {S11, S21}}.
	 */
	public Complex[] sParameters(double frequency) {
		MatrixComplex m = abcd(frequency);
		Complex A = m.getItem(0, 0), B = m.getItem(0, 1), C = m.getItem(1, 0), D = m.getItem(1, 1);
		Complex zRef = new Complex(z0, 0);
		Complex bOverZ0 = B.divides(zRef);
		Complex cTimesZ0 = C.times(zRef);
		Complex denom = A.plus(bOverZ0).plus(cTimesZ0).plus(D);
		Complex s11 = A.plus(bOverZ0).minus(cTimesZ0).minus(D).divides(denom);
		Complex s21 = new Complex(2, 0).divides(denom);
		return new Complex[] {s11, s21};
	}
}
