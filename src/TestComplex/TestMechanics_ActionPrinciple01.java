package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.mechanics.HarmonicOscillator;
import com.ipserc.arith.mechanics.LagrangianSystem;

/**
 * Verifica el principio de Hamilton (acción estacionaria) usando {@link
 * LagrangianSystem#action(double[][])}: la trayectoria física real (solución de Euler-Lagrange,
 * de {@link LagrangianSystem#simulate}) es un punto ESTACIONARIO de la acción {@code S} entre
 * todas las trayectorias con los mismos extremos -- no necesariamente su mínimo, solo que la
 * primera variación se anula. Se perturba la trayectoria real con {@code eta(t)=sin(pi*t/T)}
 * (que se anula en {@code t=0} y {@code t=T}, igual que la trayectoria real en esos extremos) y
 * se mide {@code S(eps)} para varios {@code eps}.
 */
public class TestMechanics_ActionPrinciple01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;
		Complex.printBoxText(boxShape, boxMargin, "Principio de Hamilton -- accion estacionaria");

		double mass = 2.0, springConstant = 8.0;
		HarmonicOscillator hand = new HarmonicOscillator(mass, springConstant);
		LagrangianSystem osc = new LagrangianSystem(1, (q, qDot) -> hand.lagrangian(q[0], qDot[0]));

		double x0 = 1.0, v0 = 0.5;
		double T = 1.0, dt = 0.0005;
		double[][] trueTrajectory = osc.simulate(new double[]{x0}, new double[]{v0}, T, dt);
		double S0 = osc.action(trueTrajectory);
		System.out.printf("S[trayectoria real] = %.8f (T=%.2f, %d pasos)%n", S0, T, trueTrajectory.length);

		double[][] perturbedPlus1 = perturb(trueTrajectory, T, 0.01);
		double[][] perturbedMinus1 = perturb(trueTrajectory, T, -0.01);
		double[][] perturbedPlus2 = perturb(trueTrajectory, T, 0.02);
		double[][] perturbedMinus2 = perturb(trueTrajectory, T, -0.02);

		double S_plus1 = osc.action(perturbedPlus1);
		double S_minus1 = osc.action(perturbedMinus1);
		double S_plus2 = osc.action(perturbedPlus2);
		double S_minus2 = osc.action(perturbedMinus2);

		double firstVariation = (S_plus1 - S_minus1) / (2*0.01);
		System.out.printf("Primera variacion (dS/deps en eps=0, diferencia central) = %.3e%n", firstVariation);
		check("La primera variacion de S se anula en la trayectoria fisica (principio de Hamilton)", Math.abs(firstVariation) < 1e-4);

		double diff1 = 0.5*(S_plus1 + S_minus1) - S0; // termino de 2o orden en eps=0.01
		double diff2 = 0.5*(S_plus2 + S_minus2) - S0; // termino de 2o orden en eps=0.02
		double ratio = diff2 / diff1;
		System.out.printf("(S(0.01)+S(-0.01))/2-S0 = %.3e ; (S(0.02)+S(-0.02))/2-S0 = %.3e ; ratio = %.4f (esperado ~4.0)%n", diff1, diff2, ratio);
		check("El termino de 2o orden escala como eps^2 (ratio~4 al doblar eps) -- S tiene curvatura genuina en el estacionario", Math.abs(ratio - 4.0) < 0.1);

		// Trayectoria NO fisica con los mismos extremos (interpolacion lineal) -- debe dar una
		// accion distinta de S0, para confirmar que S0 no es un valor trivial/degenerado.
		double xT = trueTrajectory[trueTrajectory.length-1][1];
		double[][] straightLine = new double[trueTrajectory.length][3];
		for (int i = 0; i < trueTrajectory.length; ++i) {
			double t = trueTrajectory[i][0];
			straightLine[i][0] = t;
			straightLine[i][1] = x0 + (xT - x0)*(t/T);
			straightLine[i][2] = (xT - x0)/T;
		}
		double S_straight = osc.action(straightLine);
		System.out.printf("S[interpolacion lineal, mismos extremos] = %.8f%n", S_straight);
		check("La accion de una trayectoria NO fisica con los mismos extremos difiere de S0", Math.abs(S_straight - S0) > 1e-3);

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}

	/** trueTrajectory perturbada por eps*sin(pi*t/T) (se anula en t=0 y t=T, mismos extremos). */
	static double[][] perturb(double[][] trueTrajectory, double T, double eps) {
		double[][] result = new double[trueTrajectory.length][3];
		for (int i = 0; i < trueTrajectory.length; ++i) {
			double t = trueTrajectory[i][0];
			double eta = Math.sin(Math.PI*t/T);
			double etaDot = (Math.PI/T)*Math.cos(Math.PI*t/T);
			result[i][0] = t;
			result[i][1] = trueTrajectory[i][1] + eps*eta;
			result[i][2] = trueTrajectory[i][2] + eps*etaDot;
		}
		return result;
	}
}
