package com.ipserc.arith.rf;

import com.ipserc.arith.complex.Complex;

/**
 * The dominant TE11 mode of a circular waveguide (radius {@code a}, propagation along {@code z}),
 * derived from Maxwell's equations the same way as {@link RectangularWaveguide}'s TE10 -- the only
 * difference is the transverse geometry, which replaces the {@code sin(pi*x/a)} separation of
 * variables (Cartesian) with Bessel functions {@code J_n} (cylindrical), already available as
 * {@link Complex#besselJ}.
 * <p>
 * Generating the mode from its longitudinal field {@code Hz = H0*J_n(kc*rho)*cos(n*phi)*
 * exp(j(wt-beta*z))} (TE: {@code Ez=0}) and the standard transverse-field relations
 * {@code Et = (-j*w*mu/kc^2) * zhat x grad_t(Hz)}, {@code Ht = (-j*beta/kc^2) * grad_t(Hz)} gives,
 * for {@code n=1} (TE11, the dominant circular mode):
 * <pre>
 * Hz  =  H0*J1(kc*rho)*cos(phi)*cos(wt-beta*z)
 * Hrho=  H0*(beta/kc)*J1'(kc*rho)*cos(phi)*sin(wt-beta*z)
 * Hphi= -H0*(beta/(kc^2*rho))*J1(kc*rho)*sin(phi)*sin(wt-beta*z)
 * Erho=  H0*(w*mu/(kc^2*rho))*J1(kc*rho)*sin(phi)*sin(wt-beta*z)
 * Ephi=  H0*(w*mu/kc)*J1'(kc*rho)*cos(phi)*sin(wt-beta*z)
 * Ez  =  0
 * </pre>
 * with {@code kc=p'_11/a} ({@code p'_11}, the first positive root of {@code J1'}, located here by
 * bisection rather than hardcoded -- see {@link #FIRST_ROOT_J1_PRIME}), {@code k=w*sqrt(mu*
 * epsilon)}, {@code beta=sqrt(k^2-kc^2)}, real above cutoff (propagating), purely imaginary below
 * it (evanescent) -- exactly {@link RectangularWaveguide}'s story, just with a different {@code
 * kc}. {@link #instantEx}/{@link #instantEy}/{@link #instantHx}/{@link #instantHy}/{@link
 * #instantHz} give the real, instantaneous Cartesian fields (rotated from {@code
 * rho}/{@code phi}), verified against Maxwell's equations directly in {@code
 * TestRF_MaxwellTE11_01} using {@link com.ipserc.arith.vectorcalculus.VectorCalculus}.
 */
public final class CircularWaveguide implements WaveguideMode {

	private final static String HEADINFO = "CircularWaveguide --- INFO: ";
	private final static String VERSION = "1.1 (2026_0825_1200)";
	/* VERSION Release Note
	 *
	 * 1.1 (2026_0825_1200)
	 * implements WaveguideMode -- permite reutilizar EvanescentModeFilter sobre guia circular sin
	 * duplicar la cascada ABCD; sin cambios de comportamiento.
	 *
	 * 1.0 (2026_0825_1100)
	 * Tercera clase de com.ipserc.arith.rf: modo TE11 de una guia de onda circular, derivado de
	 * Maxwell igual que RectangularWaveguide.VERSION 1.0 pero con separacion de variables
	 * cilindrica (Bessel) en vez de cartesiana.
	 */

	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION);
	}

	/**
	 * The first positive root of {@code J1'(x) = J0(x) - J1(x)/x} -- the TE11 cutoff condition
	 * ({@code d/d(rho) J1(kc*rho) = 0} at {@code rho=a}) -- located once by bisection over
	 * {@link Complex#besselJ}, not hardcoded, so the cutoff genuinely traces back to Maxwell's
	 * boundary condition rather than a tabulated constant. Standard reference value
	 * {@code p'_11 ~ 1.8411837813406593} (verified against this computed value in {@code
	 * TestRF_MaxwellTE11_01}).
	 */
	private static final double FIRST_ROOT_J1_PRIME = computeFirstRootJ1Prime();

	private static double besselJ0(double x) {
		return Complex.besselJ(new Complex(0, 0), new Complex(x, 0)).rep();
	}

	private static double besselJ1(double x) {
		return Complex.besselJ(new Complex(1, 0), new Complex(x, 0)).rep();
	}

	/** {@code J1'(x) = J0(x) - J1(x)/x} (three-term Bessel recurrence, n=1), {@code J1'(0)=1/2}. */
	private static double besselJ1Prime(double x) {
		if (x == 0) { return 0.5; }
		return besselJ0(x) - besselJ1(x)/x;
	}

	/** Bisection for the first positive root of {@link #besselJ1Prime}, bracketed in {@code (1,3)}. */
	private static double computeFirstRootJ1Prime() {
		double lo = 1.0, hi = 3.0;
		double signLo = Math.signum(besselJ1Prime(lo));
		for (int i = 0; i < 200; ++i) {
			double mid = 0.5*(lo+hi);
			if (Math.signum(besselJ1Prime(mid)) == signLo) { lo = mid; } else { hi = mid; }
		}
		return 0.5*(lo+hi);
	}

	public final double a, mu, epsilon;

	/**
	 * A vacuum/air-filled circular waveguide.
	 * @param a The radius (m). Must be positive.
	 */
	public CircularWaveguide(double a) {
		this(a, RectangularWaveguide.MU0, RectangularWaveguide.EPS0);
	}

	/**
	 * @param a The radius (m). Must be positive.
	 * @param mu The permeability of the fill material (H/m). Must be positive.
	 * @param epsilon The permittivity of the fill material (F/m). Must be positive.
	 * @throws IllegalArgumentException if any argument is not positive.
	 */
	public CircularWaveguide(double a, double mu, double epsilon) {
		if (a <= 0 || mu <= 0 || epsilon <= 0) {
			throw new IllegalArgumentException(HEADINFO + "a, mu and epsilon must all be positive");
		}
		this.a = a;
		this.mu = mu;
		this.epsilon = epsilon;
	}

	/** {@code kc = p'_11/a}, the TE11 cutoff wavenumber. */
	public double cutoffWavenumberTE11() {
		return FIRST_ROOT_J1_PRIME/a;
	}

	/**
	 * The TE11 cutoff frequency {@code fc = v*kc/(2*pi)}, {@code v=1/sqrt(mu*epsilon)} the fill
	 * material's wave speed. Below {@code fc}, TE11 does not propagate (evanescent).
	 * @return The TE11 cutoff frequency, Hz.
	 */
	public double cutoffFrequencyTE11() {
		double v = 1.0/Math.sqrt(mu*epsilon);
		return v*cutoffWavenumberTE11()/(2*Math.PI);
	}

	/**
	 * The TE11 propagation constant {@code beta = sqrt(k^2-kc^2)}, {@code k=w*sqrt(mu*epsilon)} --
	 * real above {@link #cutoffFrequencyTE11()} (propagating), purely imaginary below it
	 * (evanescent).
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
		double kc = cutoffWavenumberTE11();
		return Complex.sqrt(new Complex(k*k - kc*kc, 0));
	}

	/**
	 * The TE11 wave impedance {@code Z_TE = w*mu/beta} -- real above cutoff, purely reactive below
	 * it -- same formula as {@link RectangularWaveguide#waveImpedanceTE}, {@code kc} aside.
	 * @param frequency The operating frequency, Hz. Must be positive.
	 * @return The (possibly complex) TE11 wave impedance at {@code frequency}.
	 */
	@Override
	public Complex waveImpedanceTE(double frequency) {
		double omega = 2*Math.PI*frequency;
		return new Complex(omega*mu, 0).divides(propagationConstant(frequency));
	}

	/**
	 * The (real) propagation constant, requiring {@code frequency} to be above cutoff -- the
	 * instantaneous field formulas only make sense for a genuinely propagating mode.
	 */
	private double propagatingBeta(double frequency) {
		Complex beta = propagationConstant(frequency);
		if (Math.abs(beta.imp()) > 1e-9*Math.max(1.0, Math.abs(beta.rep()))) {
			throw new IllegalArgumentException(HEADINFO + "frequency=" + frequency
					+ " Hz is below cutoff (" + cutoffFrequencyTE11() + " Hz) -- TE11 does not propagate, "
					+ "the instantaneous field formulas don't apply");
		}
		return beta.rep();
	}

	/**
	 * The instantaneous TE11 field vector {@code {Ex,Ey,Hx,Hy,Hz}} (amplitude {@code H0=1}) at a
	 * Cartesian point, {@code Ez=0} always (TE mode) -- computed once in cylindrical coordinates
	 * ({@code rho,phi}, see class Javadoc) and rotated to Cartesian, so every public {@code
	 * instantXxx} accessor is a thin wrapper over this single computation instead of repeating it.
	 * @param x,y Position across the guide's cross-section (m), not both zero (the fields are
	 * singular exactly on the axis, {@code rho=0}).
	 * @param z Position along the guide (m).
	 * @param t Time (s).
	 * @param frequency The operating frequency, Hz. Must be above cutoff.
	 * @throws IllegalArgumentException if {@code x==y==0} (on-axis) or {@code frequency} is at or
	 * below cutoff.
	 */
	private double[] instantFieldVector(double x, double y, double z, double t, double frequency) {
		double rho = Math.hypot(x, y);
		if (rho == 0) {
			throw new IllegalArgumentException(HEADINFO + "the TE11 field is singular on the axis (rho=0)");
		}
		double phi = Math.atan2(y, x);
		double beta = propagatingBeta(frequency);
		double kc = cutoffWavenumberTE11();
		double omega = 2*Math.PI*frequency;
		double theta = omega*t - beta*z;
		double j1 = besselJ1(kc*rho);
		double j1p = besselJ1Prime(kc*rho);
		double cosPhi = Math.cos(phi), sinPhi = Math.sin(phi);
		double cosTheta = Math.cos(theta), sinTheta = Math.sin(theta);

		double hz = j1*cosPhi*cosTheta;
		double hRho = (beta/kc)*j1p*cosPhi*sinTheta;
		double hPhi = -(beta/(kc*kc*rho))*j1*sinPhi*sinTheta;
		double eRho = (omega*mu/(kc*kc*rho))*j1*sinPhi*sinTheta;
		double ePhi = (omega*mu/kc)*j1p*cosPhi*sinTheta;

		double ex = eRho*cosPhi - ePhi*sinPhi;
		double ey = eRho*sinPhi + ePhi*cosPhi;
		double hx = hRho*cosPhi - hPhi*sinPhi;
		double hy = hRho*sinPhi + hPhi*cosPhi;
		return new double[] {ex, ey, hx, hy, hz};
	}

	/** The instantaneous (real) {@code Ex(x,y,z,t)} field of the TE11 mode -- see {@link #instantFieldVector}. */
	public double instantEx(double x, double y, double z, double t, double frequency) {
		return instantFieldVector(x, y, z, t, frequency)[0];
	}

	/** The instantaneous (real) {@code Ey(x,y,z,t)} field of the TE11 mode -- see {@link #instantFieldVector}. */
	public double instantEy(double x, double y, double z, double t, double frequency) {
		return instantFieldVector(x, y, z, t, frequency)[1];
	}

	/** The instantaneous (real) {@code Hx(x,y,z,t)} field of the TE11 mode -- see {@link #instantFieldVector}. */
	public double instantHx(double x, double y, double z, double t, double frequency) {
		return instantFieldVector(x, y, z, t, frequency)[2];
	}

	/** The instantaneous (real) {@code Hy(x,y,z,t)} field of the TE11 mode -- see {@link #instantFieldVector}. */
	public double instantHy(double x, double y, double z, double t, double frequency) {
		return instantFieldVector(x, y, z, t, frequency)[3];
	}

	/** The instantaneous (real) {@code Hz(x,y,z,t)} field of the TE11 mode -- see {@link #instantFieldVector}. */
	public double instantHz(double x, double y, double z, double t, double frequency) {
		return instantFieldVector(x, y, z, t, frequency)[4];
	}
}
