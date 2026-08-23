package com.ipserc.arith.mechanics;

import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.NamedSeries;
import com.ipserc.arith.plot.SimpleGnuplot;

/**
 * The classical double pendulum (two point masses {@code m1}/{@code m2} at the ends of massless
 * rods of length {@code l1}/{@code l2}, angles {@code theta1}/{@code theta2} from the vertical) --
 * third candidate of the "Rol Mecánica Clásica/Lagrangiana" (23 agosto 2026), chosen specifically
 * because, unlike {@link HarmonicOscillator}'s trivial constant mass matrix or the coupled
 * oscillators in {@code TestMechanics_LagrangianSystem01} (diagonal mass matrix, no velocity
 * coupling), its mass matrix genuinely depends on {@code (theta1-theta2)} and its equations of
 * motion have real centrifugal/Coriolis-like terms (the {@code thetaDot^2} terms below) -- the
 * general case {@link LagrangianSystem} was built for.
 * <p>
 * {@code L = T - V}:
 * <pre>
 * T = (1/2)(m1+m2)*l1^2*theta1Dot^2 + (1/2)*m2*l2^2*theta2Dot^2
 *     + m2*l1*l2*theta1Dot*theta2Dot*cos(theta1-theta2)
 * V = -(m1+m2)*g*l1*cos(theta1) - m2*g*l2*cos(theta2)
 * </pre>
 * The equations of motion are NOT hand-solved here for {@code accelerations()}/{@code simulate()}
 * (that is exactly the point of using {@link LagrangianSystem}); they are, however, hand-derived
 * once for cross-checking, in {@code TestMechanics_DoublePendulum01}.
 */
public final class DoublePendulum {

	private final static String HEADINFO = "DoublePendulum --- INFO: ";
	private final static String VERSION = "1.0 (2026_0824_0900)";
	/* VERSION Release Note
	 *
	 * 1.0 (2026_0824_0900)
	 * Tercera clase de com.ipserc.arith.mechanics -- primer sistema con matriz de masas
	 * dependiente de q y terminos centrifugos/Coriolis genuinos, construido sobre
	 * LagrangianSystem.VERSION 1.0 (que gano massMatrix() publico en esta misma sesion, para uso
	 * futuro de HamiltonianSystem).
	 */

	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION);
	}

	public final double m1, m2, l1, l2, g;
	private final LagrangianSystem system;

	/**
	 * @param m1 Mass of the first bob. Must be positive.
	 * @param m2 Mass of the second bob. Must be positive.
	 * @param l1 Length of the first rod. Must be positive.
	 * @param l2 Length of the second rod. Must be positive.
	 * @param g The gravitational acceleration. Must be positive.
	 * @throws IllegalArgumentException if any argument is not positive.
	 */
	public DoublePendulum(double m1, double m2, double l1, double l2, double g) {
		if (m1 <= 0 || m2 <= 0 || l1 <= 0 || l2 <= 0 || g <= 0) {
			throw new IllegalArgumentException(HEADINFO + "m1, m2, l1, l2 and g must all be positive");
		}
		this.m1 = m1;
		this.m2 = m2;
		this.l1 = l1;
		this.l2 = l2;
		this.g = g;
		this.system = new LagrangianSystem(2, (q, qDot) -> lagrangian(q[0], q[1], qDot[0], qDot[1]));
	}

	/**
	 * The Lagrangian {@code L = T - V} (see class Javadoc).
	 * @param theta1 Angle of the first rod from the vertical.
	 * @param theta2 Angle of the second rod from the vertical.
	 * @param theta1Dot Angular velocity of the first rod.
	 * @param theta2Dot Angular velocity of the second rod.
	 * @return The value of the Lagrangian.
	 */
	public double lagrangian(double theta1, double theta2, double theta1Dot, double theta2Dot) {
		double cosDiff = Math.cos(theta1 - theta2);
		double T = 0.5*(m1+m2)*l1*l1*theta1Dot*theta1Dot
				+ 0.5*m2*l2*l2*theta2Dot*theta2Dot
				+ m2*l1*l2*theta1Dot*theta2Dot*cosDiff;
		double V = -(m1+m2)*g*l1*Math.cos(theta1) - m2*g*l2*Math.cos(theta2);
		return T - V;
	}

	/**
	 * The total mechanical energy {@code E = T + V}, conserved along any trajectory.
	 * @param theta1 Angle of the first rod from the vertical.
	 * @param theta2 Angle of the second rod from the vertical.
	 * @param theta1Dot Angular velocity of the first rod.
	 * @param theta2Dot Angular velocity of the second rod.
	 * @return The total mechanical energy.
	 */
	public double energy(double theta1, double theta2, double theta1Dot, double theta2Dot) {
		double cosDiff = Math.cos(theta1 - theta2);
		double T = 0.5*(m1+m2)*l1*l1*theta1Dot*theta1Dot
				+ 0.5*m2*l2*l2*theta2Dot*theta2Dot
				+ m2*l1*l2*theta1Dot*theta2Dot*cosDiff;
		double V = -(m1+m2)*g*l1*Math.cos(theta1) - m2*g*l2*Math.cos(theta2);
		return T + V;
	}

	/**
	 * The angular accelerations {@code (theta1DotDot, theta2DotDot)}, computed by {@link
	 * LagrangianSystem#accelerations} (numerical Euler-Lagrange) -- never hand-solved in this
	 * class, see class Javadoc.
	 * @param theta1 Angle of the first rod from the vertical.
	 * @param theta2 Angle of the second rod from the vertical.
	 * @param theta1Dot Angular velocity of the first rod.
	 * @param theta2Dot Angular velocity of the second rod.
	 * @return {@code {theta1DotDot, theta2DotDot}}.
	 */
	public double[] accelerations(double theta1, double theta2, double theta1Dot, double theta2Dot) {
		return system.accelerations(new double[] {theta1, theta2}, new double[] {theta1Dot, theta2Dot});
	}

	/**
	 * Integrates the equations of motion with {@link LagrangianSystem#simulate}.
	 * @param theta1_0 Initial angle of the first rod.
	 * @param theta2_0 Initial angle of the second rod.
	 * @param theta1Dot0 Initial angular velocity of the first rod.
	 * @param theta2Dot0 Initial angular velocity of the second rod.
	 * @param tMax The final time.
	 * @param dt The integration step.
	 * @return One row {@code {t, theta1, theta2, theta1Dot, theta2Dot}} per step.
	 */
	public double[][] simulate(double theta1_0, double theta2_0, double theta1Dot0, double theta2Dot0, double tMax, double dt) {
		return system.simulate(new double[] {theta1_0, theta2_0}, new double[] {theta1Dot0, theta2Dot0}, tMax, dt);
	}

	/**
	 * The Cartesian position {@code (x,y)} of the first bob, pivot at the origin, {@code y}
	 * pointing up.
	 * @param theta1 Angle of the first rod from the vertical.
	 * @return {@code {x1, y1}}.
	 */
	public double[] bob1Position(double theta1) {
		return new double[] {l1*Math.sin(theta1), -l1*Math.cos(theta1)};
	}

	/**
	 * The Cartesian position {@code (x,y)} of the second bob, relative to the same origin as
	 * {@link #bob1Position(double)}.
	 * @param theta1 Angle of the first rod from the vertical.
	 * @param theta2 Angle of the second rod from the vertical.
	 * @return {@code {x2, y2}}.
	 */
	public double[] bob2Position(double theta1, double theta2) {
		double[] p1 = bob1Position(theta1);
		return new double[] {p1[0] + l2*Math.sin(theta2), p1[1] - l2*Math.cos(theta2)};
	}

	/**
	 * Plots the Cartesian path traced by both bobs over a trajectory from {@link
	 * #simulate(double, double, double, double, double, double)}.
	 * @param title The title of the plot.
	 * @param trajectory A trajectory as returned by {@link #simulate}.
	 */
	public void plotTrajectorySync(String title, double[][] trajectory) {
		plotTrajectory(title, trajectory, SimpleGnuplot.e_syncMode.SYNC);
	}

	public void plotTrajectoryAsync(String title, double[][] trajectory) {
		plotTrajectory(title, trajectory, SimpleGnuplot.e_syncMode.ASYNC);
	}

	private void plotTrajectory(String title, double[][] trajectory, SimpleGnuplot.e_syncMode mode) {
		double[][] path1 = new double[trajectory.length][2];
		double[][] path2 = new double[trajectory.length][2];
		for (int i = 0; i < trajectory.length; ++i) {
			double theta1 = trajectory[i][1], theta2 = trajectory[i][2];
			double[] p1 = bob1Position(theta1);
			double[] p2 = bob2Position(theta1, theta2);
			path1[i][0] = p1[0]; path1[i][1] = p1[1];
			path2[i][0] = p2[0]; path2[i][1] = p2[1];
		}
		MatrixComplexPlot.plotSeries(title, "\"x\"", "\"y\"", false, MatrixComplexPlot.e_lineStyle.LINES, mode,
				new NamedSeries("Bob 1", path1), new NamedSeries("Bob 2", path2));
	}
}
