package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.mechanics.HamiltonianSystem;
import com.ipserc.arith.mechanics.LagrangianSystem;

/**
 * Verifica {@link HamiltonianSystem} (transformada de Legendre + ecuaciones de Hamilton) contra:
 * el Hamiltoniano de libro de texto del oscilador armónico y del péndulo simple, y una
 * comparación directa de trayectorias contra {@link LagrangianSystem} para el mismo sistema
 * físico partiendo de las mismas condiciones iniciales.
 */
public class TestMechanics_HamiltonianSystem01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;

		/*************************************************************
		 * 1) Oscilador armonico: H(x,p) = p^2/(2m) + (1/2)k*x^2
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "HamiltonianSystem 1/2 -- Oscilador armonico");

		double mass = 2.0, springConstant = 8.0;
		HamiltonianSystem hamOsc = new HamiltonianSystem(1, (q, qDot) -> 0.5*mass*qDot[0]*qDot[0] - 0.5*springConstant*q[0]*q[0]);

		boolean matchesTextbookHO = true;
		double maxDiffHO = 0;
		for (double x = -3.0; x <= 3.0; x += 0.5) {
			for (double p = -4.0; p <= 4.0; p += 1.0) {
				double H = hamOsc.hamiltonian(new double[]{x}, new double[]{p});
				double expected = (p*p)/(2*mass) + 0.5*springConstant*x*x;
				maxDiffHO = Math.max(maxDiffHO, Math.abs(H - expected));
				if (Math.abs(H - expected) > 1e-3) matchesTextbookHO = false;
			}
		}
		System.out.printf("max |H_calculado - H_libro| (oscilador) = %.3e%n", maxDiffHO);
		check("H(x,p) coincide con p^2/(2m)+(1/2)k*x^2 del libro de texto", matchesTextbookHO);

		// Comparacion de trayectorias: LagrangianSystem (x,xDot) vs HamiltonianSystem (x,p=m*xDot)
		LagrangianSystem lagOsc = new LagrangianSystem(1, (q, qDot) -> 0.5*mass*qDot[0]*qDot[0] - 0.5*springConstant*q[0]*q[0]);
		double x0 = 1.5, v0 = -0.5;
		double[][] lagTrajectory = lagOsc.simulate(new double[]{x0}, new double[]{v0}, 5.0, 0.001);
		double[][] hamTrajectory = hamOsc.simulate(new double[]{x0}, new double[]{mass*v0}, 5.0, 0.001);

		boolean trajectoriesMatch = true;
		double maxTrajDiff = 0;
		for (int i = 0; i < lagTrajectory.length; ++i) {
			double xLag = lagTrajectory[i][1];
			double xHam = hamTrajectory[i][1];
			double diff = Math.abs(xLag - xHam);
			maxTrajDiff = Math.max(maxTrajDiff, diff);
			if (diff > 1e-3) trajectoriesMatch = false;
		}
		System.out.printf("max |x_Lagrange - x_Hamilton| a lo largo de la trayectoria = %.3e%n", maxTrajDiff);
		check("La trayectoria de Hamilton coincide con la de Lagrange para el mismo oscilador", trajectoriesMatch);

		double h0 = hamOsc.hamiltonian(new double[]{x0}, new double[]{mass*v0});
		double maxEnergyDrift = 0;
		for (double[] row : hamTrajectory) {
			double h = hamOsc.hamiltonian(new double[]{row[1]}, new double[]{row[2]});
			maxEnergyDrift = Math.max(maxEnergyDrift, Math.abs(h - h0));
		}
		System.out.printf("max deriva de H a lo largo de la trayectoria de Hamilton = %.3e%n", maxEnergyDrift);
		check("H se conserva integrando las ecuaciones de Hamilton", maxEnergyDrift < 1e-3);

		/*************************************************************
		 * 2) Pendulo simple: H(theta,p) = p^2/(2*m*l^2) + m*g*l*(1-cos(theta))
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "HamiltonianSystem 2/2 -- Pendulo simple");

		double g = 9.8, l = 1.0, mPend = 1.0;
		HamiltonianSystem hamPend = new HamiltonianSystem(1, (q, qDot) -> {
			double theta = q[0], thetaDot = qDot[0];
			return 0.5*mPend*l*l*thetaDot*thetaDot - mPend*g*l*(1 - Math.cos(theta));
		});

		boolean matchesTextbookPendulum = true;
		double maxDiffPendulum = 0;
		for (double theta = -2.0; theta <= 2.0; theta += 0.4) {
			for (double p = -3.0; p <= 3.0; p += 1.0) {
				double H = hamPend.hamiltonian(new double[]{theta}, new double[]{p});
				double expected = (p*p)/(2*mPend*l*l) + mPend*g*l*(1 - Math.cos(theta));
				maxDiffPendulum = Math.max(maxDiffPendulum, Math.abs(H - expected));
				if (Math.abs(H - expected) > 1e-3) matchesTextbookPendulum = false;
			}
		}
		System.out.printf("max |H_calculado - H_libro| (pendulo) = %.3e%n", maxDiffPendulum);
		check("H(theta,p) coincide con p^2/(2ml^2)+mgl(1-cos(theta)) del libro de texto", matchesTextbookPendulum);

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}
}
