package com.ipserc.arith.mechanics;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Generic numerical Euler-Lagrange engine: given any Lagrangian {@code L(q, qDot)} over a fixed
 * number of generalized coordinates -- with no symbolic algebra or automatic differentiation
 * machinery available in this project (only {@link com.ipserc.arith.complex.Complex#derivative}
 * for a single variable) -- derives the generalized accelerations {@code qDotDot} purely by
 * NUMERICAL differentiation (central finite differences), then integrates them (classical RK4)
 * the same way {@link HarmonicOscillator} does for its single, hand-derived degree of freedom.
 * <p>
 * The Euler-Lagrange equations {@code d/dt(dL/dqDot_i) - dL/dq_i = 0}, {@code i=1..n}, expand via
 * the chain rule (since {@code dL/dqDot_i} generally depends on both {@code q} and {@code qDot})
 * into a LINEAR system in the unknown accelerations:
 * <pre>
 * sum_j (d^2L/dqDot_i.dqDot_j) * qDotDot_j = dL/dq_i - sum_j (d^2L/dqDot_i.dq_j) * qDot_j
 * </pre>
 * i.e. {@code M(q,qDot)*qDotDot = f(q,qDot)}, where {@code M} (the "mass matrix", the Hessian of
 * {@code L} in the velocities) and {@code f} are both computed numerically at each step, and the
 * system is solved via {@link MatrixComplex#inverse()} -- reusing this project's own linear
 * algebra instead of writing a separate real linear solver.
 * <p>
 * NUMERICAL DIFFERENTIATION vs. the alternatives (discussed with the user before building this):
 * automatic differentiation (dual numbers propagated through the Lagrangian's arithmetic) would
 * give machine-precision derivatives with no truncation error, but needs a new numeric type
 * threaded through every operation in the caller's {@code L} instead of a plain {@code double[],
 * double[] -> double} function; a full symbolic engine (an expression tree for {@code L},
 * differentiated term-by-term) would give exact, reusable closed-form equations of motion, at the
 * cost of building an expression representation this project doesn't have. Central finite
 * differences need neither, at the cost of the modest, well-understood error of {@link #STEP}.
 */
public final class LagrangianSystem {

	private final static String HEADINFO = "LagrangianSystem --- INFO: ";
	private final static String VERSION = "1.1 (2026_0824_1200)";
	/* VERSION Release Note
	 *
	 * 1.1 (2026_0824_1200)
	 * action(trajectory): la accion S=integral(L dt) a lo largo de CUALQUIER trayectoria (no solo
	 * las de simulate()), por la regla del trapecio -- a peticion del usuario, para verificar el
	 * principio de Hamilton (la trayectoria fisica es un punto ESTACIONARIO de S, no
	 * necesariamente su minimo) en TestMechanics_ActionPrinciple01.
	 *
	 * 1.0 (2026_0823_2300)
	 * Segunda clase de com.ipserc.arith.mechanics -- motor generico de Euler-Lagrange por
	 * diferenciacion numerica (candidato 2, tras HarmonicOscillator.VERSION 1.0).
	 */

	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION);
	}

	/** A Lagrangian {@code L(q, qDot)} over a fixed number of generalized coordinates. */
	@FunctionalInterface
	public interface Lagrangian {
		double apply(double[] q, double[] qDot);
	}

	/**
	 * Central-difference step, shared by the gradient and the Hessian. Chosen as a compromise: for
	 * a first derivative, central differences have O(h^2) truncation error and O(eps/h) rounding
	 * error (eps~2.2e-16), both minimized around h~1e-5..1e-4; for the second derivatives used by
	 * the mass matrix, the same h gives rounding error O(eps/h^2)~2e-8, still small next to the
	 * O(h^2)~1e-8 truncation term. Not a per-derivative-order optimal step, but adequate for the
	 * accuracy this class targets (see the verification tolerances in its test battery).
	 */
	private static final double STEP = 1e-4;

	private final int dof;
	private final Lagrangian lagrangian;

	/**
	 * @param degreesOfFreedom The number of generalized coordinates. Must be at least 1.
	 * @param lagrangian The Lagrangian {@code L(q, qDot)}, {@code q}/{@code qDot} of length
	 * {@code degreesOfFreedom}.
	 * @throws IllegalArgumentException if {@code degreesOfFreedom} is less than 1.
	 */
	public LagrangianSystem(int degreesOfFreedom, Lagrangian lagrangian) {
		if (degreesOfFreedom < 1) {
			throw new IllegalArgumentException(HEADINFO + "degreesOfFreedom must be >= 1, got " + degreesOfFreedom);
		}
		this.dof = degreesOfFreedom;
		this.lagrangian = lagrangian;
	}

	/**
	 * @return The number of generalized coordinates of this system.
	 */
	public int degreesOfFreedom() {
		return dof;
	}

	/**
	 * {@code dL/dq_i} at {@code (q,qDot)}, central difference.
	 * @param q The generalized coordinates.
	 * @param qDot The generalized velocities.
	 * @param i The coordinate index to differentiate with respect to.
	 * @return The partial derivative of the Lagrangian with respect to {@code q_i}.
	 */
	public double dL_dq(double[] q, double[] qDot, int i) {
		double[] qPlus = q.clone();  qPlus[i] += STEP;
		double[] qMinus = q.clone(); qMinus[i] -= STEP;
		return (lagrangian.apply(qPlus, qDot) - lagrangian.apply(qMinus, qDot)) / (2*STEP);
	}

	/**
	 * {@code dL/dqDot_i} at {@code (q,qDot)}, central difference -- the generalized momentum
	 * conjugate to {@code q_i}.
	 * @param q The generalized coordinates.
	 * @param qDot The generalized velocities.
	 * @param i The velocity index to differentiate with respect to.
	 * @return The partial derivative of the Lagrangian with respect to {@code qDot_i}.
	 */
	public double dL_dqDot(double[] q, double[] qDot, int i) {
		double[] vPlus = qDot.clone();  vPlus[i] += STEP;
		double[] vMinus = qDot.clone(); vMinus[i] -= STEP;
		return (lagrangian.apply(q, vPlus) - lagrangian.apply(q, vMinus)) / (2*STEP);
	}

	/** {@code d^2L/(dqDot_i.dqDot_j)} at {@code (q,qDot)} -- one entry of the mass matrix. */
	private double d2L_dqDotdqDot(double[] q, double[] qDot, int i, int j) {
		if (i == j) {
			double[] vPlus = qDot.clone();  vPlus[i] += STEP;
			double[] vMinus = qDot.clone(); vMinus[i] -= STEP;
			return (lagrangian.apply(q, vPlus) - 2*lagrangian.apply(q, qDot) + lagrangian.apply(q, vMinus)) / (STEP*STEP);
		}
		double[] pp = qDot.clone(); pp[i] += STEP; pp[j] += STEP;
		double[] pm = qDot.clone(); pm[i] += STEP; pm[j] -= STEP;
		double[] mp = qDot.clone(); mp[i] -= STEP; mp[j] += STEP;
		double[] mm = qDot.clone(); mm[i] -= STEP; mm[j] -= STEP;
		return (lagrangian.apply(q, pp) - lagrangian.apply(q, pm) - lagrangian.apply(q, mp) + lagrangian.apply(q, mm)) / (4*STEP*STEP);
	}

	/** {@code d^2L/(dqDot_i.dq_j)} at {@code (q,qDot)} -- the velocity-position coupling term. */
	private double d2L_dqDotdq(double[] q, double[] qDot, int i, int j) {
		double[] qPlusJ = q.clone();  qPlusJ[j] += STEP;
		double[] qMinusJ = q.clone(); qMinusJ[j] -= STEP;
		double[] vPlusI = qDot.clone();  vPlusI[i] += STEP;
		double[] vMinusI = qDot.clone(); vMinusI[i] -= STEP;
		double Lpp = lagrangian.apply(qPlusJ, vPlusI);
		double Lpm = lagrangian.apply(qPlusJ, vMinusI);
		double Lmp = lagrangian.apply(qMinusJ, vPlusI);
		double Lmm = lagrangian.apply(qMinusJ, vMinusI);
		return (Lpp - Lpm - Lmp + Lmm) / (4*STEP*STEP);
	}

	/**
	 * The "mass matrix" {@code M(q,qDot)}, the Hessian of {@code L} in the velocities --
	 * {@code M_ij = d^2L/(dqDot_i.dqDot_j)}. Exposed publicly (beyond {@link #accelerations}'s own
	 * use of it) for {@link HamiltonianSystem}'s Legendre transform, which needs this same matrix
	 * to recover velocities from generalized momenta.
	 * @param q The generalized coordinates.
	 * @param qDot The generalized velocities.
	 * @return The {@code dof x dof} mass matrix at {@code (q,qDot)}.
	 */
	public MatrixComplex massMatrix(double[] q, double[] qDot) {
		MatrixComplex mass = new MatrixComplex(dof, dof);
		for (int i = 0; i < dof; ++i) {
			for (int j = 0; j < dof; ++j) {
				mass.setItem(i, j, new Complex(d2L_dqDotdqDot(q, qDot, i, j), 0));
			}
		}
		return mass;
	}

	/**
	 * The generalized accelerations {@code qDotDot} solving {@code M(q,qDot)*qDotDot=f(q,qDot)}
	 * from the Euler-Lagrange equations (see class Javadoc for the derivation).
	 * @param q The generalized coordinates.
	 * @param qDot The generalized velocities.
	 * @return The generalized accelerations {@code qDotDot}, same length as {@code q}.
	 */
	public double[] accelerations(double[] q, double[] qDot) {
		MatrixComplex mass = massMatrix(q, qDot);
		double[] f = new double[dof];
		for (int i = 0; i < dof; ++i) {
			double coupling = 0;
			for (int j = 0; j < dof; ++j) {
				coupling += d2L_dqDotdq(q, qDot, i, j) * qDot[j];
			}
			f[i] = dL_dq(q, qDot, i) - coupling;
		}

		MatrixComplex fVector = new MatrixComplex(dof, 1);
		for (int i = 0; i < dof; ++i) {
			fVector.setItem(i, 0, new Complex(f[i], 0));
		}

		MatrixComplex accel = mass.inverse().times(fVector);
		double[] result = new double[dof];
		for (int i = 0; i < dof; ++i) {
			result[i] = accel.getItem(i, 0).rep();
		}
		return result;
	}

	/**
	 * Integrates the equations of motion from {@code t=0} to {@code t=tMax} with classical RK4,
	 * fixed step {@code dt}, on the state {@code (q,qDot)} -- generalization of {@link
	 * HarmonicOscillator#simulate}'s stepper to an arbitrary number of degrees of freedom.
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
			state = rk4Step(state, dt);
			t += dt;
		}
		return trajectory;
	}

	/**
	 * The action {@code S[q(t)] = integral of L(q(t),qDot(t)) dt} along ANY trajectory -- not
	 * necessarily one returned by {@link #simulate}, which is exactly the point: Hamilton's
	 * principle says the true physical trajectory (a solution of the Euler-Lagrange equations) is
	 * a STATIONARY point of {@code S} among all trajectories sharing the same endpoints, not
	 * necessarily its minimum. {@code TestMechanics_ActionPrinciple01} verifies this directly, by
	 * comparing {@code S} on the true trajectory against nearby trajectories perturbed away from
	 * it (same endpoints, different path in between).
	 * <p>
	 * Integrated by the trapezoidal rule over the {@code (t,q,qDot)} rows of {@code trajectory},
	 * consistent with the rest of this class staying purely numerical (no closed-form integral of
	 * {@code L} is assumed, or even possible in general for an arbitrary caller-supplied {@code
	 * L}).
	 * @param trajectory A trajectory as returned by {@link #simulate} (or built by hand from any
	 * other path): each row {@code {t, q_0..q_(n-1), qDot_0..qDot_(n-1)}}.
	 * @return The action along {@code trajectory}.
	 */
	public double action(double[][] trajectory) {
		double s = 0;
		double[] q = new double[dof], qDot = new double[dof];
		System.arraycopy(trajectory[0], 1, q, 0, dof);
		System.arraycopy(trajectory[0], 1 + dof, qDot, 0, dof);
		double previousL = lagrangian.apply(q, qDot);
		for (int i = 1; i < trajectory.length; ++i) {
			double dt = trajectory[i][0] - trajectory[i-1][0];
			System.arraycopy(trajectory[i], 1, q, 0, dof);
			System.arraycopy(trajectory[i], 1 + dof, qDot, 0, dof);
			double currentL = lagrangian.apply(q, qDot);
			s += 0.5*(previousL + currentL)*dt;
			previousL = currentL;
		}
		return s;
	}

	private double[] derivative(double[] state) {
		double[] q = new double[dof], qDot = new double[dof];
		System.arraycopy(state, 0, q, 0, dof);
		System.arraycopy(state, dof, qDot, 0, dof);
		double[] qDotDot = accelerations(q, qDot);
		double[] result = new double[2*dof];
		System.arraycopy(qDot, 0, result, 0, dof);
		System.arraycopy(qDotDot, 0, result, dof, dof);
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
