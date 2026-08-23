package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.mechanics.HarmonicOscillator;
import com.ipserc.arith.mechanics.LagrangianSystem;
import com.ipserc.arith.mechanics.NewtonianSystem;

/**
 * Verifica {@link NewtonianSystem} (F=m*a, tercer pilar de la mecánica clásica junto a Lagrange y
 * Hamilton): coincidencia con {@link LagrangianSystem} para una fuerza conservativa (el oscilador
 * armónico, ya verificado en las 3 formulaciones anteriores -- ver {@code
 * TestMechanics_HamiltonianSystem01}), y un caso genuinamente disipativo (caída libre con
 * rozamiento lineal) que {@link LagrangianSystem} en este paquete no puede expresar.
 */
public class TestMechanics_NewtonianSystem01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;

		/*************************************************************
		 * 1) Oscilador armonico: F(x)=-k*x, cruzado contra LagrangianSystem
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "NewtonianSystem 1/2 -- Oscilador armonico (fuerza conservativa)");

		double mass = 2.0, springConstant = 8.0;
		HarmonicOscillator hand = new HarmonicOscillator(mass, springConstant);
		NewtonianSystem newtonOsc = new NewtonianSystem(new double[]{mass}, (q, qDot, t) -> new double[]{-springConstant*q[0]});
		LagrangianSystem lagOsc = new LagrangianSystem(1, (q, qDot) -> hand.lagrangian(q[0], qDot[0]));

		double x0 = 1.5, v0 = -0.5;
		double[][] newtonTrajectory = newtonOsc.simulate(new double[]{x0}, new double[]{v0}, 5.0, 0.001);
		double[][] lagTrajectory = lagOsc.simulate(new double[]{x0}, new double[]{v0}, 5.0, 0.001);

		boolean trajectoriesMatch = true;
		double maxTrajDiff = 0;
		for (int i = 0; i < newtonTrajectory.length; ++i) {
			double diff = Math.abs(newtonTrajectory[i][1] - lagTrajectory[i][1]);
			maxTrajDiff = Math.max(maxTrajDiff, diff);
			if (diff > 1e-6) trajectoriesMatch = false;
		}
		System.out.printf("max |x_Newton - x_Lagrange| = %.3e%n", maxTrajDiff);
		check("La trayectoria de Newton (F=m*a) coincide con la de Lagrange para el mismo oscilador", trajectoriesMatch);

		double e0 = hand.energy(x0, v0);
		double maxEnergyDrift = 0;
		for (double[] row : newtonTrajectory) {
			maxEnergyDrift = Math.max(maxEnergyDrift, Math.abs(hand.energy(row[1], row[2]) - e0));
		}
		System.out.printf("max deriva de energia (Newton, fuerza conservativa) = %.3e%n", maxEnergyDrift);
		check("La energia se conserva con una fuerza conservativa", maxEnergyDrift < 1e-6);

		boolean rejectsNonPositiveMass = true;
		try { new NewtonianSystem(new double[]{-1}, (q, qDot, t) -> new double[]{0}); rejectsNonPositiveMass = false; } catch (IllegalArgumentException e) { }
		check("constructor rechaza masas no positivas", rejectsNonPositiveMass);

		/*************************************************************
		 * 2) Caida libre con rozamiento lineal: F=-m*g-b*v (disipativa, fuera del alcance de
		 *    LagrangianSystem en este paquete -- L=T-V no admite terminos como este).
		 *    Solucion analitica: v(t) = -m*g/b + (v0+m*g/b)*exp(-b*t/m)
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "NewtonianSystem 2/2 -- Caida libre con rozamiento (fuerza disipativa)");

		double mDrop = 1.0, g = 9.8, b = 0.5;
		NewtonianSystem drop = new NewtonianSystem(new double[]{mDrop}, (q, qDot, t) -> new double[]{-mDrop*g - b*qDot[0]});

		double y0 = 100.0, vy0 = 0.0;
		double[][] dropTrajectory = drop.simulate(new double[]{y0}, new double[]{vy0}, 8.0, 0.001);

		boolean velocityMatchesAnalytic = true;
		double maxVelDiff = 0;
		double terminalVelocity = -mDrop*g/b;
		for (double[] row : dropTrajectory) {
			double t = row[0], v = row[2];
			double vExact = terminalVelocity + (vy0 - terminalVelocity)*Math.exp(-b*t/mDrop);
			double diff = Math.abs(v - vExact);
			maxVelDiff = Math.max(maxVelDiff, diff);
			if (diff > 1e-4) velocityMatchesAnalytic = false;
		}
		System.out.printf("max |v_RK4 - v_exacta| = %.3e (velocidad terminal = %.4f)%n", maxVelDiff, terminalVelocity);
		check("La velocidad de caida con rozamiento coincide con la solucion analitica exacta", velocityMatchesAnalytic);

		// La energia mecanica (cinetica+potencial) NO se conserva -- decrece monotonamente por
		// disipacion (a diferencia de todos los sistemas conservativos verificados hasta ahora).
		boolean energyMonotonicallyDecreasing = true;
		double previousEnergy = 0.5*mDrop*vy0*vy0 + mDrop*g*y0;
		for (double[] row : dropTrajectory) {
			double y = row[1], v = row[2];
			double e = 0.5*mDrop*v*v + mDrop*g*y;
			if (e > previousEnergy + 1e-9) energyMonotonicallyDecreasing = false;
			previousEnergy = e;
		}
		double finalEnergy = previousEnergy;
		double initialEnergy = 0.5*mDrop*vy0*vy0 + mDrop*g*y0;
		System.out.printf("Energia inicial=%.4f , energia final=%.4f (debe ser menor -- disipacion)%n", initialEnergy, finalEnergy);
		check("La energia mecanica decrece monotonamente por el rozamiento (sistema NO conservativo)",
				energyMonotonicallyDecreasing && finalEnergy < initialEnergy);

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}
}
