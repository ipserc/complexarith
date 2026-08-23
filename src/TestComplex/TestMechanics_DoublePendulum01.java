package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.mechanics.DoublePendulum;

/**
 * Verifica {@link DoublePendulum}: sus aceleraciones (calculadas por {@link
 * com.ipserc.arith.mechanics.LagrangianSystem}, nunca resueltas a mano en la propia clase) contra
 * la ecuación de movimiento del péndulo doble derivada a mano aquí mismo (ver comentario de {@link
 * #handDerivedAccelerations}), y la conservación de la energía mecánica total a lo largo de una
 * trayectoria simulada.
 */
public class TestMechanics_DoublePendulum01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	/**
	 * Ecuación de movimiento del péndulo doble, derivada a mano por sustitución directa en
	 * Euler-Lagrange (independiente de {@link com.ipserc.arith.mechanics.LagrangianSystem}, para
	 * poder verificarlo). Con {@code A=(m1+m2)*l1}, {@code B=m2*l2*cos(t1-t2)},
	 * {@code C=-m2*l2*t2Dot^2*sin(t1-t2)-(m1+m2)*g*sin(t1)}, {@code D=l1*cos(t1-t2)}, {@code E=l2},
	 * {@code F=l1*t1Dot^2*sin(t1-t2)-g*sin(t2)}, el sistema lineal {@code A*t1DD+B*t2DD=C},
	 * {@code D*t1DD+E*t2DD=F} se resuelve por Cramer.
	 */
	static double[] handDerivedAccelerations(double m1, double m2, double l1, double l2, double g,
			double theta1, double theta2, double theta1Dot, double theta2Dot) {
		double diff = theta1 - theta2;
		double A = (m1+m2)*l1;
		double B = m2*l2*Math.cos(diff);
		double C = -m2*l2*theta2Dot*theta2Dot*Math.sin(diff) - (m1+m2)*g*Math.sin(theta1);
		double D = l1*Math.cos(diff);
		double E = l2;
		double F = l1*theta1Dot*theta1Dot*Math.sin(diff) - g*Math.sin(theta2);
		double det = A*E - B*D;
		double theta1DotDot = (C*E - B*F) / det;
		double theta2DotDot = (A*F - C*D) / det;
		return new double[] {theta1DotDot, theta2DotDot};
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;
		Complex.printBoxText(boxShape, boxMargin, "DoublePendulum -- Euler-Lagrange generico vs derivacion a mano");

		double m1 = 1.2, m2 = 0.8, l1 = 1.0, l2 = 0.7, g = 9.8;
		DoublePendulum pendulum = new DoublePendulum(m1, m2, l1, l2, g);

		boolean matchesHandDerived = true;
		double maxDiff = 0;
		for (double t1 = -2.0; t1 <= 2.0; t1 += 0.5) {
			for (double t2 = -2.0; t2 <= 2.0; t2 += 0.5) {
				for (double v1 = -1.0; v1 <= 1.0; v1 += 1.0) {
					for (double v2 = -1.0; v2 <= 1.0; v2 += 1.0) {
						double[] generic = pendulum.accelerations(t1, t2, v1, v2);
						double[] hand = handDerivedAccelerations(m1, m2, l1, l2, g, t1, t2, v1, v2);
						double diff = Math.max(Math.abs(generic[0]-hand[0]), Math.abs(generic[1]-hand[1]));
						maxDiff = Math.max(maxDiff, diff);
						if (diff > 1e-2) matchesHandDerived = false;
					}
				}
			}
		}
		System.out.printf("max |accel_generico - accel_mano| = %.3e%n", maxDiff);
		check("LagrangianSystem reproduce la ecuacion de movimiento del pendulo doble derivada a mano", matchesHandDerived);

		// Constructor rechaza parametros no positivos
		boolean rejectsNonPositive = true;
		try { new DoublePendulum(-1, 1, 1, 1, 9.8); rejectsNonPositive = false; } catch (IllegalArgumentException e) { }
		try { new DoublePendulum(1, 1, 1, 1, 0); rejectsNonPositive = false; } catch (IllegalArgumentException e) { }
		check("constructor rechaza masas/longitudes/g no positivas", rejectsNonPositive);

		// Conservacion de energia en una trayectoria caotica (angulo inicial grande)
		double theta1_0 = 2.5, theta2_0 = -1.0, theta1Dot0 = 0.0, theta2Dot0 = 0.0;
		double[][] trajectory = pendulum.simulate(theta1_0, theta2_0, theta1Dot0, theta2Dot0, 10.0, 0.0005);
		double e0 = pendulum.energy(theta1_0, theta2_0, theta1Dot0, theta2Dot0);
		double maxEnergyDrift = 0;
		for (double[] row : trajectory) {
			double e = pendulum.energy(row[1], row[2], row[3], row[4]);
			maxEnergyDrift = Math.max(maxEnergyDrift, Math.abs(e - e0));
		}
		System.out.printf("Energia inicial E0=%.6f , max deriva de energia sobre %d pasos = %.3e%n", e0, trajectory.length, maxEnergyDrift);
		check("La energia se conserva a lo largo de la trayectoria caotica del pendulo doble", maxEnergyDrift < 1e-2);

		// Representacion grafica de la trayectoria (smoke test -- no debe lanzar excepcion)
		pendulum.plotTrajectoryAsync("Pendulo doble -- trayectoria de ambas masas", trajectory);
		System.out.println("plotTrajectoryAsync no lanzo excepcion");

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}
}
