
package com.ipserc.arith.quantum;

import java.util.Random;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * A CHSH (Clauser-Horne-Shimony-Holt) Bell test on a 2-qubit entangled state -- the standard
 * experiment demonstrating that quantum correlations violate the classical bound {@code |S|<=2}
 * that any local-hidden-variable theory must respect, reaching up to {@code 2*sqrt(2)} (Tsirelson's
 * bound) for a maximally entangled state and the optimal measurement angles.
 * <p>
 * Two ways to run the same experiment, both built on the same {@link Qubits} vocabulary:
 * <ul>
 * <li>{@link #chsh(MatrixComplex, double, double, double, double)} -- the EXACT quantum-mechanical
 * prediction, computed directly by matrix algebra ({@code E(a,b)=<state|A(a) tensor B(b)|state>}).
 * No randomness involved; this is the reference value every real (or simulated) experiment is
 * expected to converge to.</li>
 * <li>{@link #simulateChsh(MatrixComplex, double, double, double, double, int, Random)} -- a Monte
 * Carlo simulation of individual projective measurements (Born-rule sampling, +-1 outcomes), the
 * same way a real lab experiment would estimate the correlations from many runs. Converges to the
 * exact value above as the number of trials grows.</li>
 * </ul>
 * First exercise of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoquinta sesión) -- see that document for the review methodology this exercise is the
 * first step of, and the catalogued gaps (density matrix/partial trace, n-qubit generalization)
 * deliberately left out of this first pass.
 */
public final class BellTest {

	private final static String VERSION = "1.1 (2026_0813_2030)";

	private BellTest() {}

	/**
	 * The exact quantum-mechanical correlation {@code E(A,B) = <state|(opA tensor opB)|state>}
	 * for a 2-qubit {@code state} and two single-qubit Hermitian measurement operators.
	 * @param state The 2-qubit state, as a 4x1 column vector (e.g. {@link Qubits#bellPhiPlus()}).
	 * @param opA The measurement operator for the first qubit (e.g. {@link Qubits#spinOperator(double)}).
	 * @param opB The measurement operator for the second qubit.
	 * @return The real-valued correlation, in {@code [-1,1]}.
	 * @throws IllegalStateException if the expectation value comes out with a non-negligible
	 * imaginary part -- would mean {@code opA}/{@code opB} aren't actually Hermitian (a physical
	 * observable's expectation value must be real; failing loud here catches a misuse of this API
	 * the same way {@code Jordan.checkReconstruction()} fails loud elsewhere in this project).
	 */
	public static double correlation(MatrixComplex state, MatrixComplex opA, MatrixComplex opB) {
		return TimeEvolution.expectationValue(state, opA.kroneckerprod(opB));
	}

	/**
	 * The exact CHSH parameter {@code S = E(a,b) - E(a,b') + E(a',b) + E(a',b')}, for the
	 * {@link Qubits#spinOperator(double)} family of measurement operators at 4 angles. For {@code
	 * state=Qubits.bellPhiPlus()} this correlation works out to {@code E(a,b)=cos(a-b)} (expand
	 * {@code A(a) tensor B(b)} in the Pauli basis and use {@code <Phi+|Z tensor Z|Phi+> =
	 * <Phi+|X tensor X|Phi+> = 1}, {@code <Phi+|Z tensor X|Phi+> = <Phi+|X tensor Z|Phi+> = 0}) --
	 * maximized at angles spaced by {@code pi/4} (NOT {@code pi/8}, the spacing used in the
	 * textbook "polarizer angle" convention where the correlation is {@code cos(2*(a-b))} instead
	 * -- {@code theta} here is already the full operator angle, the {@code /2} is baked into
	 * {@link #spinEigenket(double, int)} instead). {@code a=0, a'=pi/2, b=pi/4, b'=3*pi/4} reaches
	 * {@code 2*sqrt(2)~=2.8284271247461903} (Tsirelson's bound) -- strictly above the classical
	 * bound of {@code 2}, the actual "violation of Bell's theorem".
	 * @param state The 2-qubit state.
	 * @param a First measurement angle for qubit A, in radians.
	 * @param aPrime Second measurement angle for qubit A, in radians.
	 * @param b First measurement angle for qubit B, in radians.
	 * @param bPrime Second measurement angle for qubit B, in radians.
	 * @return The CHSH parameter S.
	 */
	public static double chsh(MatrixComplex state, double a, double aPrime, double b, double bPrime) {
		double eab = correlation(state, Qubits.spinOperator(a), Qubits.spinOperator(b));
		double eabPrime = correlation(state, Qubits.spinOperator(a), Qubits.spinOperator(bPrime));
		double eaPrimeB = correlation(state, Qubits.spinOperator(aPrime), Qubits.spinOperator(b));
		double eaPrimeBPrime = correlation(state, Qubits.spinOperator(aPrime), Qubits.spinOperator(bPrime));
		return eab - eabPrime + eaPrimeB + eaPrimeBPrime;
	}

	/**
	 * The {@code +1}/{@code -1} eigenket of {@link Qubits#spinOperator(double)} at angle {@code
	 * theta} -- closed form for this specific 2x2 operator (not a generic diagonalization, no
	 * {@code Eigenspace}/{@code Jordan} needed): solving {@code (A(theta)-sign*I)v=0} with the
	 * half-angle identities {@code cos(theta)-1=-2sin^2(theta/2)}, {@code
	 * sin(theta)=2sin(theta/2)cos(theta/2)} gives {@code v=(cos(theta/2),sin(theta/2))} for {@code
	 * sign=+1} and its orthogonal {@code v=(-sin(theta/2),cos(theta/2))} for {@code sign=-1} --
	 * the textbook spin-1/2 measurement eigenstates.
	 */
	private static MatrixComplex spinEigenket(double theta, int sign) {
		double half = theta / 2.0;
		MatrixComplex ket = new MatrixComplex(2, 1);
		if (sign > 0) {
			ket.setItem(0, 0, Math.cos(half));
			ket.setItem(1, 0, Math.sin(half));
		} else {
			ket.setItem(0, 0, -Math.sin(half));
			ket.setItem(1, 0, Math.cos(half));
		}
		return ket;
	}

	/**
	 * Monte Carlo estimate of {@link #correlation(MatrixComplex, MatrixComplex, MatrixComplex)}
	 * for the {@link Qubits#spinOperator(double)} family: samples {@code trials} independent joint
	 * projective measurements (Born-rule probabilities {@code |<e_a^s1 tensor e_b^s2|state>|^2}
	 * over the 4 combinations of {@code +-1} outcomes, via {@link #spinEigenket(double, int)}),
	 * and returns the average of the {@code s1*s2} products -- the same statistic a real Bell-test
	 * experiment estimates from many runs.
	 * @param state The 2-qubit state.
	 * @param thetaA Measurement angle for qubit A, in radians.
	 * @param thetaB Measurement angle for qubit B, in radians.
	 * @param trials Number of simulated measurement runs.
	 * @param rng The random source (no fixed seed required by this method -- pass a seeded {@link
	 * Random} for reproducible runs).
	 * @return The estimated correlation, converging to {@link #correlation} as {@code trials} grows.
	 */
	public static double simulateCorrelation(MatrixComplex state, double thetaA, double thetaB, int trials, Random rng) {
		MatrixComplex[] jointKets = {
				spinEigenket(thetaA, +1).kroneckerprod(spinEigenket(thetaB, +1)),
				spinEigenket(thetaA, +1).kroneckerprod(spinEigenket(thetaB, -1)),
				spinEigenket(thetaA, -1).kroneckerprod(spinEigenket(thetaB, +1)),
				spinEigenket(thetaA, -1).kroneckerprod(spinEigenket(thetaB, -1)),
		};
		int[] product = { 1, -1, -1, 1 }; // (+,+) (+,-) (-,+) (-,-)
		double[] probability = new double[4];
		for (int i = 0; i < 4; ++i) {
			double amplitudeMod = jointKets[i].adjoint().times(state).getItem(0, 0).mod();
			probability[i] = amplitudeMod * amplitudeMod;
		}

		double accumulator = 0.0;
		for (int t = 0; t < trials; ++t) {
			double r = rng.nextDouble();
			double cumulative = 0.0;
			int outcome = 3; // last bucket by default, guards against floating-point rounding at the tail
			for (int i = 0; i < 4; ++i) {
				cumulative += probability[i];
				if (r < cumulative) { outcome = i; break; }
			}
			accumulator += product[outcome];
		}
		return accumulator / trials;
	}

	/**
	 * Monte Carlo estimate of {@link #chsh(MatrixComplex, double, double, double, double)}: runs 4
	 * independent {@link #simulateCorrelation} experiments (one per angle pair), same as a real
	 * Bell-test setup that measures each of the 4 settings in its own run.
	 * @param trials Number of simulated measurement runs PER angle pair (4 pairs total).
	 * @param rng The random source, shared across the 4 experiments.
	 * @return The estimated CHSH parameter S, converging to {@link #chsh} as {@code trials} grows.
	 */
	public static double simulateChsh(MatrixComplex state, double a, double aPrime, double b, double bPrime, int trials, Random rng) {
		double eab = simulateCorrelation(state, a, b, trials, rng);
		double eabPrime = simulateCorrelation(state, a, bPrime, trials, rng);
		double eaPrimeB = simulateCorrelation(state, aPrime, b, trials, rng);
		double eaPrimeBPrime = simulateCorrelation(state, aPrime, bPrime, trials, rng);
		return eab - eabPrime + eaPrimeB + eaPrimeBPrime;
	}
}
