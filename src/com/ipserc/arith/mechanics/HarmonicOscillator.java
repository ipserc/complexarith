package com.ipserc.arith.mechanics;

/**
 * The classical simple harmonic oscillator (mass {@code m} on a spring of constant {@code k}),
 * derived from its Lagrangian via the Euler-Lagrange equation -- first candidate of the new "Rol
 * Mecánica Clásica/Lagrangiana" (23 agosto 2026), a single concrete system worked out by hand
 * before building any general-purpose Euler-Lagrange machinery (a separate, later candidate).
 * <p>
 * {@code L(x, xDot) = T - V = (1/2)*m*xDot^2 - (1/2)*k*x^2}. The Euler-Lagrange equation
 * {@code d/dt(dL/dxDot) - dL/dx = 0} gives:
 * <pre>
 * dL/dxDot = m*xDot            d/dt(dL/dxDot) = m*xDotDot
 * dL/dx    = -k*x
 * m*xDotDot - (-k*x) = 0  =&gt;  m*xDotDot + k*x = 0  =&gt;  xDotDot = -(k/m)*x
 * </pre>
 * the standard equation of motion, integrated here numerically (classical RK4 on the state
 * {@code (x,xDot)}) and cross-checked against the exact closed-form solution
 * {@code x(t) = x0*cos(omega*t) + (v0/omega)*sin(omega*t)}, {@code omega = sqrt(k/m)}.
 */
public final class HarmonicOscillator {

	private final static String HEADINFO = "HarmonicOscillator --- INFO: ";
	private final static String VERSION = "1.0 (2026_0823_2200)";
	/* VERSION Release Note
	 *
	 * 1.0 (2026_0823_2200)
	 * Primera clase de com.ipserc.arith.mechanics (Rol Mecánica Clásica/Lagrangiana, nuevo paquete).
	 */

	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION);
	}

	/** The mass {@code m} (must be positive). */
	public final double mass;
	/** The spring constant {@code k} (must be positive). */
	public final double springConstant;

	/**
	 * @param mass The mass {@code m} of the oscillator. Must be positive.
	 * @param springConstant The spring constant {@code k}. Must be positive.
	 * @throws IllegalArgumentException if either argument is not positive.
	 */
	public HarmonicOscillator(double mass, double springConstant) {
		if (mass <= 0) {
			throw new IllegalArgumentException(HEADINFO + "mass must be positive, got " + mass);
		}
		if (springConstant <= 0) {
			throw new IllegalArgumentException(HEADINFO + "springConstant must be positive, got " + springConstant);
		}
		this.mass = mass;
		this.springConstant = springConstant;
	}

	/**
	 * The Lagrangian {@code L = T - V = (1/2)*m*xDot^2 - (1/2)*k*x^2}.
	 * @param x The position.
	 * @param xDot The velocity.
	 * @return The value of the Lagrangian at {@code (x, xDot)}.
	 */
	public double lagrangian(double x, double xDot) {
		return 0.5*mass*xDot*xDot - 0.5*springConstant*x*x;
	}

	/**
	 * The total mechanical energy {@code E = T + V = (1/2)*m*xDot^2 + (1/2)*k*x^2} -- conserved
	 * along any trajectory that solves the equation of motion, since the Lagrangian has no
	 * explicit time dependence.
	 * @param x The position.
	 * @param xDot The velocity.
	 * @return The total mechanical energy at {@code (x, xDot)}.
	 */
	public double energy(double x, double xDot) {
		return 0.5*mass*xDot*xDot + 0.5*springConstant*x*x;
	}

	/**
	 * The angular frequency {@code omega = sqrt(k/m)} of the resulting motion.
	 * @return {@code omega}.
	 */
	public double angularFrequency() {
		return Math.sqrt(springConstant/mass);
	}

	/**
	 * The acceleration {@code xDotDot = -(k/m)*x} given by the Euler-Lagrange equation of motion
	 * (see class Javadoc for the derivation).
	 * @param x The position.
	 * @return The acceleration at position {@code x} (independent of velocity, as expected for
	 * this Lagrangian).
	 */
	public double acceleration(double x) {
		return -springConstant/mass * x;
	}

	/**
	 * The exact closed-form position at time {@code t}, {@code x(t) = x0*cos(omega*t) +
	 * (v0/omega)*sin(omega*t)}, for the initial conditions {@code x(0)=x0}, {@code xDot(0)=v0}.
	 * @param x0 The initial position.
	 * @param v0 The initial velocity.
	 * @param t The elapsed time.
	 * @return The exact position at time {@code t}.
	 */
	public double analyticPosition(double x0, double v0, double t) {
		double omega = angularFrequency();
		return x0*Math.cos(omega*t) + (v0/omega)*Math.sin(omega*t);
	}

	/**
	 * The exact closed-form velocity at time {@code t} -- the time derivative of {@link
	 * #analyticPosition(double, double, double)}.
	 * @param x0 The initial position.
	 * @param v0 The initial velocity.
	 * @param t The elapsed time.
	 * @return The exact velocity at time {@code t}.
	 */
	public double analyticVelocity(double x0, double v0, double t) {
		double omega = angularFrequency();
		return -x0*omega*Math.sin(omega*t) + v0*Math.cos(omega*t);
	}

	/**
	 * Integrates the equation of motion from {@code t=0} to {@code t=tMax} with classical RK4
	 * (4th-order Runge-Kutta) on the state {@code (x, xDot)}, fixed step {@code dt}.
	 * @param x0 The initial position.
	 * @param v0 The initial velocity.
	 * @param tMax The final time (inclusive, up to rounding by {@code dt}).
	 * @param dt The integration step. Must be positive.
	 * @return One row {@code {t, x, xDot}} per step, from {@code t=0} to {@code t<=tMax}.
	 * @throws IllegalArgumentException if {@code dt} is not positive.
	 */
	public double[][] simulate(double x0, double v0, double tMax, double dt) {
		if (dt <= 0) {
			throw new IllegalArgumentException(HEADINFO + "dt must be positive, got " + dt);
		}
		int steps = (int) Math.round(tMax/dt) + 1;
		double[][] trajectory = new double[steps][3];
		double t = 0, x = x0, v = v0;
		for (int i = 0; i < steps; ++i) {
			trajectory[i][0] = t;
			trajectory[i][1] = x;
			trajectory[i][2] = v;
			double[] next = rk4Step(new double[] {x, v}, dt);
			x = next[0];
			v = next[1];
			t += dt;
		}
		return trajectory;
	}

	/**
	 * The state derivative {@code (xDot, xDotDot)} for the RK4 stepper -- {@code xDotDot} comes
	 * from {@link #acceleration(double)}, the Euler-Lagrange equation of motion.
	 */
	private double[] derivative(double[] state) {
		double x = state[0], v = state[1];
		return new double[] {v, acceleration(x)};
	}

	/** One classical RK4 step of size {@code dt} on {@code state=(x,xDot)}. */
	private double[] rk4Step(double[] state, double dt) {
		double[] k1 = derivative(state);
		double[] k2 = derivative(addScaled(state, k1, dt/2));
		double[] k3 = derivative(addScaled(state, k2, dt/2));
		double[] k4 = derivative(addScaled(state, k3, dt));
		double[] next = new double[state.length];
		for (int i = 0; i < state.length; ++i) {
			next[i] = state[i] + (dt/6.0)*(k1[i] + 2*k2[i] + 2*k3[i] + k4[i]);
		}
		return next;
	}

	private static double[] addScaled(double[] base, double[] delta, double scale) {
		double[] result = new double[base.length];
		for (int i = 0; i < base.length; ++i) {
			result[i] = base[i] + delta[i]*scale;
		}
		return result;
	}
}
