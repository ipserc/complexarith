package com.ipserc.arith.mechanics;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * The Hamiltonian counterpart of {@link LagrangianSystem}: given the same kind of Lagrangian
 * {@code L(q,qDot)}, performs the Legendre transform to {@code H(q,p)} (generalized coordinates
 * and MOMENTA instead of velocities) and integrates Hamilton's equations
 * {@code qDot_i = dH/dp_i, pDot_i = -dH/dq_i} -- fourth candidate of the "Rol Mecánica
 * Clásica/Lagrangiana" (24 agosto 2026).
 * <p>
 * SCOPE: valid for the standard mechanical Lagrangian {@code L(q,qDot) = T(q,qDot) - V(q)} with
 * {@code T} an exactly quadratic, homogeneous form in {@code qDot} ({@code T = (1/2)*qDot^T *
 * M(q) * qDot}, {@code M(q)} the mass matrix from {@link LagrangianSystem#massMatrix}) -- true for
 * every scleronomic mechanical system in this package ({@link HarmonicOscillator}, the pendulum
 * and coupled oscillators in {@code TestMechanics_LagrangianSystem01}, {@link DoublePendulum}),
 * but NOT for a Lagrangian with an explicit term linear in {@code qDot} (e.g. a velocity-dependent
 * potential, as for a charged particle in a magnetic field). Under that assumption the generalized
 * momenta are exactly {@code p = M(q)*qDot} (no additive term), so velocities are recovered from
 * momenta by {@code qDot = M(q)^-1 * p} -- a genuine matrix inversion, not a numerical root solve
 * of the full nonlinear Legendre transform a completely general {@code L} would need.
 */
public final class HamiltonianSystem {

	private final static String HEADINFO = "HamiltonianSystem --- INFO: ";
	private final static String VERSION = "1.0 (2026_0824_0930)";
	/* VERSION Release Note
	 *
	 * 1.0 (2026_0824_0930)
	 * Cuarta clase de com.ipserc.arith.mechanics -- transformada de Legendre y ecuaciones de
	 * Hamilton, sobre LagrangianSystem.VERSION 1.0 (reusa massMatrix() y dL_dqDot()).
	 */

	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION);
	}

	private static final double STEP = 1e-4;

	private final int dof;
	private final LagrangianSystem.Lagrangian lagrangian;
	private final LagrangianSystem lagrangianSystem;

	/**
	 * @param degreesOfFreedom The number of generalized coordinates. Must be at least 1.
	 * @param lagrangian The Lagrangian {@code L(q, qDot)} -- see class Javadoc for the required
	 * scope ({@code T} quadratic homogeneous in {@code qDot}, no term linear in {@code qDot}).
	 */
	public HamiltonianSystem(int degreesOfFreedom, LagrangianSystem.Lagrangian lagrangian) {
		this.dof = degreesOfFreedom;
		this.lagrangian = lagrangian;
		this.lagrangianSystem = new LagrangianSystem(degreesOfFreedom, lagrangian);
	}

	/**
	 * The generalized momenta {@code p_i = dL/dqDot_i} conjugate to {@code q}, at a given
	 * {@code (q,qDot)}.
	 * @param q The generalized coordinates.
	 * @param qDot The generalized velocities.
	 * @return The generalized momenta {@code p}, same length as {@code q}.
	 */
	public double[] momenta(double[] q, double[] qDot) {
		double[] p = new double[dof];
		for (int i = 0; i < dof; ++i) {
			p[i] = lagrangianSystem.dL_dqDot(q, qDot, i);
		}
		return p;
	}

	/**
	 * Recovers the generalized velocities from {@code (q,p)} via {@code qDot = M(q)^-1 * p} (see
	 * class Javadoc for why this is exact, not an approximation, under this class's scope).
	 * @param q The generalized coordinates.
	 * @param p The generalized momenta.
	 * @return The generalized velocities {@code qDot}.
	 */
	public double[] velocitiesFromMomenta(double[] q, double[] p) {
		MatrixComplex mass = lagrangianSystem.massMatrix(q, new double[dof]);
		MatrixComplex pVector = new MatrixComplex(dof, 1);
		for (int i = 0; i < dof; ++i) {
			pVector.setItem(i, 0, new Complex(p[i], 0));
		}
		MatrixComplex qDotVector = mass.inverse().times(pVector);
		double[] qDot = new double[dof];
		for (int i = 0; i < dof; ++i) {
			qDot[i] = qDotVector.getItem(i, 0).rep();
		}
		return qDot;
	}

	/**
	 * The Hamiltonian {@code H(q,p) = sum_i p_i*qDot_i - L(q,qDot)}, {@code qDot} recovered from
	 * {@code (q,p)} via {@link #velocitiesFromMomenta}.
	 * @param q The generalized coordinates.
	 * @param p The generalized momenta.
	 * @return The value of the Hamiltonian (the total mechanical energy, for this class's scope).
	 */
	public double hamiltonian(double[] q, double[] p) {
		double[] qDot = velocitiesFromMomenta(q, p);
		double sum = 0;
		for (int i = 0; i < dof; ++i) {
			sum += p[i]*qDot[i];
		}
		return sum - lagrangian.apply(q, qDot);
	}

	/** {@code dH/dq_i} at {@code (q,p)}, central difference -- {@code -pDot_i} by Hamilton's equations. */
	private double dH_dq(double[] q, double[] p, int i) {
		double[] qPlus = q.clone();  qPlus[i] += STEP;
		double[] qMinus = q.clone(); qMinus[i] -= STEP;
		return (hamiltonian(qPlus, p) - hamiltonian(qMinus, p)) / (2*STEP);
	}

	/**
	 * Integrates Hamilton's equations {@code qDot_i = dH/dp_i = velocitiesFromMomenta(q,p)_i},
	 * {@code pDot_i = -dH/dq_i}, from {@code t=0} to {@code t=tMax} with classical RK4, fixed step
	 * {@code dt}, on the state {@code (q,p)}.
	 * @param q0 The initial generalized coordinates.
	 * @param p0 The initial generalized momenta.
	 * @param tMax The final time (inclusive, up to rounding by {@code dt}).
	 * @param dt The integration step. Must be positive.
	 * @return One row {@code {t, q_0..q_(n-1), p_0..p_(n-1)}} per step.
	 * @throws IllegalArgumentException if {@code q0}/{@code p0} don't have length {@code
	 * degreesOfFreedom}, or if {@code dt} is not positive.
	 */
	public double[][] simulate(double[] q0, double[] p0, double tMax, double dt) {
		if (q0.length != dof || p0.length != dof) {
			throw new IllegalArgumentException(HEADINFO + "q0/p0 must have length degreesOfFreedom=" + dof);
		}
		if (dt <= 0) {
			throw new IllegalArgumentException(HEADINFO + "dt must be positive, got " + dt);
		}
		int steps = (int) Math.round(tMax/dt) + 1;
		double[][] trajectory = new double[steps][1 + 2*dof];
		double[] state = new double[2*dof];
		System.arraycopy(q0, 0, state, 0, dof);
		System.arraycopy(p0, 0, state, dof, dof);
		double t = 0;
		for (int s = 0; s < steps; ++s) {
			trajectory[s][0] = t;
			System.arraycopy(state, 0, trajectory[s], 1, 2*dof);
			state = rk4Step(state, dt);
			t += dt;
		}
		return trajectory;
	}

	private double[] derivative(double[] state) {
		double[] q = new double[dof], p = new double[dof];
		System.arraycopy(state, 0, q, 0, dof);
		System.arraycopy(state, dof, p, 0, dof);
		double[] qDot = velocitiesFromMomenta(q, p);
		double[] result = new double[2*dof];
		System.arraycopy(qDot, 0, result, 0, dof);
		for (int i = 0; i < dof; ++i) {
			result[dof+i] = -dH_dq(q, p, i);
		}
		return result;
	}

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
