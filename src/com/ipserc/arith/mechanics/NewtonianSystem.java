package com.ipserc.arith.mechanics;

/**
 * The Newtonian counterpart of {@link LagrangianSystem}/{@link HamiltonianSystem}: Newton's
 * second law, {@code F=m*a}, as the third and last of the three equivalent formulations of
 * classical mechanics built in this package. Given an explicit force {@code F(q,qDot,t)} (per
 * generalized coordinate, with its own mass), integrates {@code qDotDot_i = F_i/m_i} directly --
 * no Euler-Lagrange equation, no Legendre transform, no numerical differentiation of anything at
 * all, since the force is already given in closed form by the caller.
 * <p>
 * Unlike {@link LagrangianSystem} (built from a conservative-style {@code L=T-V}, no dissipation
 * function in this package), {@code F} here may depend explicitly on {@code qDot} and {@code t}
 * with no restriction -- so this class is the natural home for genuinely non-conservative forces
 * (friction, drag) that don't fit the {@code L=T-V} framework the rest of this package assumes.
 * {@code TestMechanics_NewtonianSystem01} verifies both: agreement with {@link LagrangianSystem}
 * for a conservative force (the harmonic oscillator, all three formulations already cross-checked
 * against each other), and a genuinely dissipative case (free fall with linear drag) that
 * {@link LagrangianSystem} in this package cannot express.
 */
public final class NewtonianSystem {

	private final static String HEADINFO = "NewtonianSystem --- INFO: ";
	private final static String VERSION = "1.0 (2026_0824_1300)";
	/* VERSION Release Note
	 *
	 * 1.0 (2026_0824_1300)
	 * Quinta clase de com.ipserc.arith.mechanics -- F=m*a, tercer pilar (Newton) junto a
	 * LagrangianSystem/HamiltonianSystem (Lagrange/Hamilton).
	 */

	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION);
	}

	/** A force {@code F(q, qDot, t)}, one component per generalized coordinate. */
	@FunctionalInterface
	public interface Force {
		double[] apply(double[] q, double[] qDot, double t);
	}

	private final double[] masses;
	private final Force force;
	private final int dof;

	/**
	 * @param masses The mass associated with each generalized coordinate, one per degree of
	 * freedom. All must be positive.
	 * @param force The force {@code F(q,qDot,t)}, returning one component per degree of freedom.
	 * @throws IllegalArgumentException if any mass is not positive.
	 */
	public NewtonianSystem(double[] masses, Force force) {
		for (double m : masses) {
			if (m <= 0) {
				throw new IllegalArgumentException(HEADINFO + "all masses must be positive, got " + m);
			}
		}
		this.masses = masses.clone();
		this.dof = masses.length;
		this.force = force;
	}

	/**
	 * @return The number of degrees of freedom (masses) of this system.
	 */
	public int degreesOfFreedom() {
		return dof;
	}

	/**
	 * The accelerations {@code qDotDot_i = F_i(q,qDot,t)/m_i} -- Newton's second law, directly.
	 * @param q The generalized coordinates.
	 * @param qDot The generalized velocities.
	 * @param t The current time.
	 * @return The accelerations, same length as {@code q}.
	 */
	public double[] accelerations(double[] q, double[] qDot, double t) {
		double[] F = force.apply(q, qDot, t);
		double[] a = new double[dof];
		for (int i = 0; i < dof; ++i) {
			a[i] = F[i]/masses[i];
		}
		return a;
	}

	/**
	 * Integrates {@code F=m*a} from {@code t=0} to {@code t=tMax} with classical RK4, fixed step
	 * {@code dt}, on the state {@code (q,qDot)} -- same stepper shape as {@link
	 * LagrangianSystem#simulate}, generalized to a time-dependent right-hand side since {@code
	 * force} may depend explicitly on {@code t}.
	 * @param q0 The initial generalized coordinates.
	 * @param qDot0 The initial generalized velocities.
	 * @param tMax The final time (inclusive, up to rounding by {@code dt}).
	 * @param dt The integration step. Must be positive.
	 * @return One row {@code {t, q_0..q_(n-1), qDot_0..qDot_(n-1)}} per step.
	 * @throws IllegalArgumentException if {@code q0}/{@code qDot0} don't have length {@link
	 * #degreesOfFreedom()}, or if {@code dt} is not positive.
	 */
	public double[][] simulate(double[] q0, double[] qDot0, double tMax, double dt) {
		if (q0.length != dof || qDot0.length != dof) {
			throw new IllegalArgumentException(HEADINFO + "q0/qDot0 must have length degreesOfFreedom=" + dof);
		}
		if (dt <= 0) {
			throw new IllegalArgumentException(HEADINFO + "dt must be positive, got " + dt);
		}
		int steps = (int) Math.round(tMax/dt) + 1;
		double[][] trajectory = new double[steps][1 + 2*dof];
		double[] state = new double[2*dof];
		System.arraycopy(q0, 0, state, 0, dof);
		System.arraycopy(qDot0, 0, state, dof, dof);
		double t = 0;
		for (int s = 0; s < steps; ++s) {
			trajectory[s][0] = t;
			System.arraycopy(state, 0, trajectory[s], 1, 2*dof);
			state = rk4Step(state, t, dt);
			t += dt;
		}
		return trajectory;
	}

	private double[] derivative(double[] state, double t) {
		double[] q = new double[dof], qDot = new double[dof];
		System.arraycopy(state, 0, q, 0, dof);
		System.arraycopy(state, dof, qDot, 0, dof);
		double[] qDotDot = accelerations(q, qDot, t);
		double[] result = new double[2*dof];
		System.arraycopy(qDot, 0, result, 0, dof);
		System.arraycopy(qDotDot, 0, result, dof, dof);
		return result;
	}

	private double[] rk4Step(double[] state, double t, double dt) {
		double[] k1 = derivative(state, t);
		double[] k2 = derivative(addScaled(state, k1, dt/2), t + dt/2);
		double[] k3 = derivative(addScaled(state, k2, dt/2), t + dt/2);
		double[] k4 = derivative(addScaled(state, k3, dt), t + dt);
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
