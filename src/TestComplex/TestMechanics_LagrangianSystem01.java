package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.mechanics.HarmonicOscillator;
import com.ipserc.arith.mechanics.LagrangianSystem;

/**
 * Verifica {@link LagrangianSystem}, el motor genérico de Euler-Lagrange por diferenciación
 * numérica, en tres sistemas de dificultad creciente:
 * <ol>
 * <li>Oscilador armónico simple (1 GDL, lineal) -- contra {@link HarmonicOscillator}, ya
 * verificado con la ecuación derivada a mano.</li>
 * <li>Dos osciladores acoplados (2 GDL, lineal) -- ejercita la matriz de masas 2x2 y la
 * resolución del sistema lineal, contra la ecuación de movimiento derivada a mano.</li>
 * <li>Péndulo simple (1 GDL, NO lineal) -- ejercita un Lagrangiano no lineal, contra la ecuación
 * de movimiento conocida {@code thetaDotDot=-(g/l)*sin(theta)}, y conservación de energía en una
 * trayectoria sin solución cerrada simple (oscilación de amplitud grande).</li>
 * </ol>
 */
public class TestMechanics_LagrangianSystem01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;

		/*************************************************************
		 * 1) Oscilador armonico simple (1 GDL, lineal)
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "LagrangianSystem 1/3 -- Oscilador armonico (1 GDL)");

		double mass = 2.0, springConstant = 8.0;
		HarmonicOscillator handDerived = new HarmonicOscillator(mass, springConstant);
		LagrangianSystem genericHO = new LagrangianSystem(1, (q, qDot) -> handDerived.lagrangian(q[0], qDot[0]));

		boolean matchesHandDerived = true;
		double maxAccelDiff = 0;
		for (double x = -3.0; x <= 3.0; x += 0.5) {
			for (double v = -2.0; v <= 2.0; v += 1.0) {
				double[] qDotDot = genericHO.accelerations(new double[]{x}, new double[]{v});
				double expected = handDerived.acceleration(x);
				double diff = Math.abs(qDotDot[0] - expected);
				maxAccelDiff = Math.max(maxAccelDiff, diff);
				if (diff > 1e-4) matchesHandDerived = false;
			}
		}
		System.out.printf("max |accel_generico - accel_mano| = %.3e%n", maxAccelDiff);
		check("LagrangianSystem reproduce la aceleracion del oscilador armonico derivada a mano", matchesHandDerived);

		double[][] genericTrajectory = genericHO.simulate(new double[]{1.5}, new double[]{-0.5}, 5.0, 0.001);
		double e0HO = handDerived.energy(1.5, -0.5);
		double maxEnergyDriftHO = 0;
		for (double[] row : genericTrajectory) {
			double e = handDerived.energy(row[1], row[2]);
			maxEnergyDriftHO = Math.max(maxEnergyDriftHO, Math.abs(e - e0HO));
		}
		System.out.printf("max deriva de energia (motor generico) = %.3e%n", maxEnergyDriftHO);
		check("La energia se conserva integrando con el motor generico", maxEnergyDriftHO < 1e-3);

		/*************************************************************
		 * 2) Dos osciladores acoplados (2 GDL, lineal)
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "LagrangianSystem 2/3 -- Osciladores acoplados (2 GDL)");

		double m1 = 1.0, m2 = 1.5, k1 = 6.0, k2 = 4.0;
		// L = (1/2)m1*x1Dot^2 + (1/2)m2*x2Dot^2 - (1/2)k1*x1^2 - (1/2)k2*(x2-x1)^2
		LagrangianSystem coupled = new LagrangianSystem(2, (q, qDot) -> {
			double x1 = q[0], x2 = q[1], v1 = qDot[0], v2 = qDot[1];
			double T = 0.5*m1*v1*v1 + 0.5*m2*v2*v2;
			double V = 0.5*k1*x1*x1 + 0.5*k2*(x2-x1)*(x2-x1);
			return T - V;
		});

		// Ecuaciones de movimiento derivadas a mano:
		// m1*x1DotDot = -k1*x1 + k2*(x2-x1)
		// m2*x2DotDot = -k2*(x2-x1)
		boolean coupledOk = true;
		double maxCoupledDiff = 0;
		for (double x1 = -2.0; x1 <= 2.0; x1 += 1.0) {
			for (double x2 = -2.0; x2 <= 2.0; x2 += 1.0) {
				double[] qDotDot = coupled.accelerations(new double[]{x1, x2}, new double[]{0.3, -0.4});
				double expected1 = (-k1*x1 + k2*(x2-x1)) / m1;
				double expected2 = (-k2*(x2-x1)) / m2;
				double diff = Math.max(Math.abs(qDotDot[0]-expected1), Math.abs(qDotDot[1]-expected2));
				maxCoupledDiff = Math.max(maxCoupledDiff, diff);
				if (diff > 1e-3) coupledOk = false;
			}
		}
		System.out.printf("max |accel_generico - accel_mano| (2 GDL) = %.3e%n", maxCoupledDiff);
		check("LagrangianSystem reproduce las ecuaciones de movimiento acopladas derivadas a mano", coupledOk);

		double[] q0Coupled = {1.0, -0.5};
		double[] v0Coupled = {0.2, 0.3};
		double[][] coupledTrajectory = coupled.simulate(q0Coupled, v0Coupled, 5.0, 0.001);
		double e0Coupled = 0.5*m1*v0Coupled[0]*v0Coupled[0] + 0.5*m2*v0Coupled[1]*v0Coupled[1]
				+ 0.5*k1*q0Coupled[0]*q0Coupled[0] + 0.5*k2*Math.pow(q0Coupled[1]-q0Coupled[0], 2);
		double maxEnergyDriftCoupled = 0;
		for (double[] row : coupledTrajectory) {
			double x1 = row[1], x2 = row[2], v1 = row[3], v2 = row[4];
			double e = 0.5*m1*v1*v1 + 0.5*m2*v2*v2 + 0.5*k1*x1*x1 + 0.5*k2*Math.pow(x2-x1, 2);
			maxEnergyDriftCoupled = Math.max(maxEnergyDriftCoupled, Math.abs(e - e0Coupled));
		}
		System.out.printf("max deriva de energia (2 GDL) = %.3e%n", maxEnergyDriftCoupled);
		check("La energia total se conserva en el sistema de 2 GDL acoplado", maxEnergyDriftCoupled < 1e-3);

		/*************************************************************
		 * 3) Pendulo simple (1 GDL, NO lineal)
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "LagrangianSystem 3/3 -- Pendulo simple (1 GDL, no lineal)");

		double g = 9.8, l = 1.0, mPend = 1.0;
		// L = (1/2)*m*l^2*thetaDot^2 - m*g*l*(1-cos(theta))
		LagrangianSystem pendulum = new LagrangianSystem(1, (q, qDot) -> {
			double theta = q[0], thetaDot = qDot[0];
			double T = 0.5*mPend*l*l*thetaDot*thetaDot;
			double V = mPend*g*l*(1 - Math.cos(theta));
			return T - V;
		});

		// Ecuacion de movimiento conocida: thetaDotDot = -(g/l)*sin(theta)
		boolean pendulumOk = true;
		double maxPendulumDiff = 0;
		for (double theta = -2.5; theta <= 2.5; theta += 0.25) {
			double[] qDotDot = pendulum.accelerations(new double[]{theta}, new double[]{0.7});
			double expected = -(g/l)*Math.sin(theta);
			double diff = Math.abs(qDotDot[0] - expected);
			maxPendulumDiff = Math.max(maxPendulumDiff, diff);
			if (diff > 1e-3) pendulumOk = false;
		}
		System.out.printf("max |accel_generico - accel_mano| (pendulo) = %.3e%n", maxPendulumDiff);
		check("LagrangianSystem reproduce thetaDotDot=-(g/l)*sin(theta) del pendulo simple", pendulumOk);

		double theta0 = 2.0; // amplitud grande, sin solucion cerrada simple
		double thetaDot0 = 0.0;
		double[][] pendulumTrajectory = pendulum.simulate(new double[]{theta0}, new double[]{thetaDot0}, 5.0, 0.0005);
		double e0Pendulum = 0.5*mPend*l*l*thetaDot0*thetaDot0 + mPend*g*l*(1 - Math.cos(theta0));
		double maxEnergyDriftPendulum = 0;
		for (double[] row : pendulumTrajectory) {
			double theta = row[1], thetaDot = row[2];
			double e = 0.5*mPend*l*l*thetaDot*thetaDot + mPend*g*l*(1 - Math.cos(theta));
			maxEnergyDriftPendulum = Math.max(maxEnergyDriftPendulum, Math.abs(e - e0Pendulum));
		}
		System.out.printf("max deriva de energia (pendulo, amplitud grande) = %.3e%n", maxEnergyDriftPendulum);
		check("La energia se conserva en el pendulo de amplitud grande (sin solucion cerrada)", maxEnergyDriftPendulum < 1e-2);

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}
}
