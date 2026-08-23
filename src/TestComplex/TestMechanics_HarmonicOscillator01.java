package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.mechanics.HarmonicOscillator;

/**
 * Verifica {@link HarmonicOscillator}: la ecuación de movimiento derivada de Euler-Lagrange
 * (comparando la aceleración contra {@code -omega^2*x}), la trayectoria integrada numéricamente
 * (RK4) contra la solución analítica exacta, y la conservación de la energía mecánica total a lo
 * largo de la trayectoria.
 */
public class TestMechanics_HarmonicOscillator01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;
		Complex.printBoxText(boxShape, boxMargin, "HarmonicOscillator -- Euler-Lagrange");

		double mass = 2.0;
		double springConstant = 8.0; // omega = sqrt(k/m) = 2
		HarmonicOscillator osc = new HarmonicOscillator(mass, springConstant);

		double expectedOmega = Math.sqrt(springConstant/mass);
		check("angularFrequency() == sqrt(k/m)", Math.abs(osc.angularFrequency() - expectedOmega) < 1e-12);

		// La ecuación de movimiento (Euler-Lagrange): xDotDot = -(k/m)*x = -omega^2*x
		boolean eomOk = true;
		for (double x = -5.0; x <= 5.0; x += 0.5) {
			double expected = -expectedOmega*expectedOmega*x;
			if (Math.abs(osc.acceleration(x) - expected) > 1e-12) eomOk = false;
		}
		check("acceleration(x) == -omega^2*x para x en [-5,5]", eomOk);

		// El constructor rechaza masa/constante no positivas
		boolean rejectsNonPositive = true;
		try { new HarmonicOscillator(-1, 1); rejectsNonPositive = false; } catch (IllegalArgumentException e) { }
		try { new HarmonicOscillator(1, 0); rejectsNonPositive = false; } catch (IllegalArgumentException e) { }
		check("constructor rechaza mass/springConstant no positivos", rejectsNonPositive);

		// RK4 simulate() vs solucion analitica exacta, varios periodos
		double x0 = 1.5, v0 = -0.5;
		double period = 2*Math.PI/expectedOmega;
		double tMax = 5*period;
		double dt = 0.001;
		double[][] trajectory = osc.simulate(x0, v0, tMax, dt);

		double maxPosError = 0, maxVelError = 0;
		for (double[] row : trajectory) {
			double t = row[0], x = row[1], v = row[2];
			double xExact = osc.analyticPosition(x0, v0, t);
			double vExact = osc.analyticVelocity(x0, v0, t);
			maxPosError = Math.max(maxPosError, Math.abs(x - xExact));
			maxVelError = Math.max(maxVelError, Math.abs(v - vExact));
		}
		System.out.printf("max |x_RK4 - x_exact| = %.3e , max |v_RK4 - v_exact| = %.3e (sobre %d pasos, %d periodos)%n",
				maxPosError, maxVelError, trajectory.length, 5);
		check("RK4 coincide con la solucion analitica (tolerancia 1e-6)", maxPosError < 1e-6 && maxVelError < 1e-6);

		// Conservacion de la energia total a lo largo de la trayectoria RK4
		double e0 = osc.energy(x0, v0);
		double maxEnergyDrift = 0;
		for (double[] row : trajectory) {
			double e = osc.energy(row[1], row[2]);
			maxEnergyDrift = Math.max(maxEnergyDrift, Math.abs(e - e0));
		}
		System.out.printf("Energia inicial E0=%.6f , max deriva de energia = %.3e%n", e0, maxEnergyDrift);
		check("La energia se conserva a lo largo de la trayectoria (tolerancia 1e-6)", maxEnergyDrift < 1e-6);

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}
}
