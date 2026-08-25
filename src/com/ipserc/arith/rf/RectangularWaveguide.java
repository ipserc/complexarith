package com.ipserc.arith.rf;

import com.ipserc.arith.complex.Complex;

/**
 * The dominant TE10 mode of a rectangular waveguide (broad wall {@code a}, narrow wall {@code b},
 * propagation along {@code z}), derived from Maxwell's equations -- first candidate of a new
 * "Rol RF/Microondas" (24 agosto 2026), at the user's request to model a waveguide filter from
 * Maxwell's equations directly instead of from filter-synthesis tables.
 * <p>
 * In a source-free, lossless region, Maxwell's curl equations plus the Helmholtz wave equation
 * they imply, separated in {@code (x,y)} with perfectly-conducting-wall boundary conditions,
 * gives the standard TE10 phasor fields (amplitude {@code H0=1}, propagating as
 * {@code exp(j(wt-beta*z))}):
 * <pre>
 * Ey = -j*(w*mu*a/pi)*sin(kc*x)*exp(-j*beta*z)
 * Hx =  j*(beta*a/pi)*sin(kc*x)*exp(-j*beta*z)
 * Hz =  cos(kc*x)*exp(-j*beta*z)
 * Ex = Ez = Hy = 0
 * </pre>
 * with {@code kc=pi/a} (the TE10 cutoff wavenumber), {@code k=w*sqrt(mu*epsilon)},
 * {@code beta=sqrt(k^2-kc^2)} (real above the cutoff frequency -- propagating; purely imaginary
 * below it -- evanescent, the physical origin of every waveguide's built-in high-pass behavior).
 * Taking the real part of these phasors, {@link #instantEy}/{@link #instantHx}/{@link
 * #instantHz} give the physical, instantaneous (real-valued) fields at a fixed time {@code t},
 * verified against Maxwell's equations directly (not assumed) in
 * {@code TestRF_MaxwellTE10_01} using {@link com.ipserc.arith.vectorcalculus.VectorCalculus}.
 */
public final class RectangularWaveguide implements WaveguideMode {

	private final static String HEADINFO = "RectangularWaveguide --- INFO: ";
	private final static String VERSION = "1.1 (2026_0825_1200)";
	/* VERSION Release Note
	 *
	 * 1.1 (2026_0825_1200)
	 * implements WaveguideMode -- generaliza EvanescentModeFilter para que trabaje tambien con
	 * CircularWaveguide sin duplicar la cascada ABCD; sin cambios de comportamiento.
	 *
	 * 1.0 (2026_0824_1400)
	 * Primera clase de com.ipserc.arith.rf (paquete nuevo, Rol RF/Microondas): modo TE10 de una
	 * guia de onda rectangular, derivado de Maxwell.
	 */

	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION);
	}

	/** Permeability of free space, H/m. */
	public static final double MU0 = 4*Math.PI*1e-7;
	/** Permittivity of free space, F/m. */
	public static final double EPS0 = 8.8541878128e-12;

	public final double a, b, mu, epsilon;

	/**
	 * A vacuum/air-filled waveguide.
	 * @param a The broad wall dimension (m). Must be positive.
	 * @param b The narrow wall dimension (m). Must be positive.
	 */
	public RectangularWaveguide(double a, double b) {
		this(a, b, MU0, EPS0);
	}

	/**
	 * @param a The broad wall dimension (m). Must be positive.
	 * @param b The narrow wall dimension (m). Must be positive.
	 * @param mu The permeability of the fill material (H/m). Must be positive.
	 * @param epsilon The permittivity of the fill material (F/m). Must be positive.
	 * @throws IllegalArgumentException if any argument is not positive.
	 */
	public RectangularWaveguide(double a, double b, double mu, double epsilon) {
		if (a <= 0 || b <= 0 || mu <= 0 || epsilon <= 0) {
			throw new IllegalArgumentException(HEADINFO + "a, b, mu and epsilon must all be positive");
		}
		this.a = a;
		this.b = b;
		this.mu = mu;
		this.epsilon = epsilon;
	}

	/** {@code kc = pi/a}, the TE10 cutoff wavenumber. */
	public double cutoffWavenumberTE10() {
		return Math.PI/a;
	}

	/**
	 * The TE10 cutoff frequency {@code fc = v/(2a)}, {@code v=1/sqrt(mu*epsilon)} the fill
	 * material's wave speed. Below {@code fc}, TE10 does not propagate (evanescent) -- the
	 * physical origin of the waveguide's built-in high-pass behavior.
	 * @return The TE10 cutoff frequency, Hz.
	 */
	public double cutoffFrequencyTE10() {
		double v = 1.0/Math.sqrt(mu*epsilon);
		return v*cutoffWavenumberTE10()/(2*Math.PI);
	}

	/**
	 * The TE10 propagation constant {@code beta = sqrt(k^2-kc^2)}, {@code k=w*sqrt(mu*epsilon)} --
	 * real above {@link #cutoffFrequencyTE10()} (propagating), purely imaginary below it
	 * (evanescent: the field decays instead of propagating).
	 * @param frequency The operating frequency, Hz. Must be positive.
	 * @return The (possibly complex) propagation constant at {@code frequency}.
	 * @throws IllegalArgumentException if {@code frequency} is not positive.
	 */
	@Override
	public Complex propagationConstant(double frequency) {
		if (frequency <= 0) {
			throw new IllegalArgumentException(HEADINFO + "frequency must be positive, got " + frequency);
		}
		double omega = 2*Math.PI*frequency;
		double k = omega*Math.sqrt(mu*epsilon);
		double kc = cutoffWavenumberTE10();
		return Complex.sqrt(new Complex(k*k - kc*kc, 0));
	}

	/**
	 * The TE10 wave impedance {@code Z_TE = w*mu/beta} -- real above cutoff (the mode carries real
	 * power), purely reactive below it (evanescent modes store energy but transmit no net power).
	 * @param frequency The operating frequency, Hz. Must be positive.
	 * @return The (possibly complex) TE10 wave impedance at {@code frequency}.
	 */
	@Override
	public Complex waveImpedanceTE(double frequency) {
		double omega = 2*Math.PI*frequency;
		return new Complex(omega*mu, 0).divides(propagationConstant(frequency));
	}

	/**
	 * The instantaneous (real, physical) {@code Ey(x,z,t)} field of the TE10 mode, amplitude
	 * {@code H0=1}, valid for {@code frequency} above {@link #cutoffFrequencyTE10()} (propagating,
	 * real {@code beta}) -- the real part of the standard phasor field, see class Javadoc.
	 * @param x Position across the broad wall, {@code 0<=x<=a}.
	 * @param z Position along the guide.
	 * @param t Time.
	 * @param frequency The operating frequency, Hz. Must be above cutoff.
	 * @return The instantaneous {@code Ey} field.
	 */
	public double instantEy(double x, double z, double t, double frequency) {
		double omega = 2*Math.PI*frequency;
		double beta = propagatingBeta(frequency);
		double kc = cutoffWavenumberTE10();
		return (omega*mu*a/Math.PI) * Math.sin(kc*x) * Math.sin(omega*t - beta*z);
	}

	/** The instantaneous (real) {@code Hx(x,z,t)} field of the TE10 mode -- see {@link #instantEy}. */
	public double instantHx(double x, double z, double t, double frequency) {
		double beta = propagatingBeta(frequency);
		double kc = cutoffWavenumberTE10();
		double omega = 2*Math.PI*frequency;
		return -(beta*a/Math.PI) * Math.sin(kc*x) * Math.sin(omega*t - beta*z);
	}

	/** The instantaneous (real) {@code Hz(x,z,t)} field of the TE10 mode -- see {@link #instantEy}. */
	public double instantHz(double x, double z, double t, double frequency) {
		double beta = propagatingBeta(frequency);
		double kc = cutoffWavenumberTE10();
		double omega = 2*Math.PI*frequency;
		return Math.cos(kc*x) * Math.cos(omega*t - beta*z);
	}

	/**
	 * The (real) propagation constant, requiring {@code frequency} to be above cutoff -- the
	 * instantaneous field formulas ({@link #instantEy}/{@link #instantHx}/{@link #instantHz})
	 * only make sense for a genuinely propagating (oscillating in {@code z}) mode.
	 */
	private double propagatingBeta(double frequency) {
		Complex beta = propagationConstant(frequency);
		if (Math.abs(beta.imp()) > 1e-9*Math.max(1.0, Math.abs(beta.rep()))) {
			throw new IllegalArgumentException(HEADINFO + "frequency=" + frequency
					+ " Hz is below cutoff (" + cutoffFrequencyTE10() + " Hz) -- TE10 does not propagate, "
					+ "the instantaneous field formulas don't apply");
		}
		return beta.rep();
	}
}
