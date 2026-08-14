package com.ipserc.arith.quantum;

import java.util.function.IntPredicate;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * The Bernstein-Vazirani algorithm ({@link BernsteinVazirani}) with a noise channel ({@link
 * Decoherence}) applied to one qubit of the {@code n+1}-qubit register right after state
 * preparation, before any circuit gate runs -- the question "does the single-query exact secret
 * recovery survive if the register has already decohered by the time the circuit executes?".
 * Reuses {@link NoisyDeutschJozsa#circuitDensityMatrix(IntPredicate, int, MatrixComplex[], int)}
 * VERBATIM (package-visible): Bernstein-Vazirani's oracle {@code f(x)=(a.x) mod 2} is just a
 * particular {@code f}, the noisy circuit shape (state prep, noise, {@code H^(x)(n+1)}, oracle,
 * {@code H^(x)n} on the input register) is byte-for-byte the same one {@link NoisyDeutschJozsa}
 * already builds -- only what's read off the final density matrix differs (a single diagonal pair
 * for Deutsch-Jozsa's all-zero test, one diagonal pair PER candidate secret here).
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoctava sesión) -- third cross-exercise combination, after {@link NoisyTeleportation}/
 * {@link NoisyDeutschJozsa}.
 */
public final class NoisyBernsteinVazirani {

	private final static String VERSION = "1.0 (2026_0814_1200)";

	private NoisyBernsteinVazirani() {}

	/**
	 * The measurement probability of a specific input-register outcome {@code x} (marginalizing
	 * over the ancilla) WITH noise applied to qubit {@code noisyQubit} -- the sum of the 2 relevant
	 * diagonal entries ({@code x}, either ancilla value) of {@link
	 * NoisyDeutschJozsa#circuitDensityMatrix(IntPredicate, int, MatrixComplex[], int)}, the noisy
	 * analog of the amplitude-squared read in {@link BernsteinVazirani#findSecret(IntPredicate, int)}.
	 * @param f The oracle function under test.
	 * @param n The number of input qubits, must be at least 1.
	 * @param kraus The noise channel's Kraus operators (e.g. from {@link Decoherence#bitFlip(double)}).
	 * @param noisyQubit The 0-based index (in {@code [0,n]}, {@code n} itself being the ancilla) of
	 * the qubit the channel acts on.
	 * @param x The candidate input-register outcome, in {@code [0,2^n)}.
	 * @return The probability, in {@code [0,1]}.
	 */
	public static double outcomeProbability(IntPredicate f, int n, MatrixComplex[] kraus, int noisyQubit, int x) {
		MatrixComplex rho = NoisyDeutschJozsa.circuitDensityMatrix(f, n, kraus, noisyQubit);
		return NoisyDeutschJozsa.diagonalProbability(rho, 2 * x) + NoisyDeutschJozsa.diagonalProbability(rho, 2 * x + 1);
	}

	/**
	 * The noise-degraded probability that the algorithm correctly recovers a KNOWN hidden {@code
	 * secret} -- {@link #outcomeProbability(IntPredicate, int, MatrixComplex[], int, int)} for
	 * {@code f=}{@link BernsteinVazirani#oracleFunction(int, int)}{@code (secret,n)} and {@code
	 * x=secret}. The main "how much does noise hurt recovery" metric this class exposes, e.g. for
	 * plotting fidelity against channel strength.
	 * @param secret The hidden {@code n}-bit string, as an integer in {@code [0,2^n)}.
	 * @param n The number of input qubits, must be at least 1.
	 * @param kraus The noise channel's Kraus operators.
	 * @param noisyQubit The qubit the channel acts on, in {@code [0,n]} ({@code n} itself being the
	 * ancilla).
	 * @return The probability, EXACTLY {@code 1.0} with a no-op channel (matching {@link
	 * BernsteinVazirani#findSecret(IntPredicate, int)} exactly).
	 */
	public static double successProbability(int secret, int n, MatrixComplex[] kraus, int noisyQubit) {
		return outcomeProbability(BernsteinVazirani.oracleFunction(secret, n), n, kraus, noisyQubit, secret);
	}

	/**
	 * The noisy analog of {@link BernsteinVazirani#findSecret(IntPredicate, int)}: the input-register
	 * outcome with the HIGHEST probability under noise, i.e. the algorithm's best guess when the
	 * circuit no longer collapses to a single deterministic outcome. Unlike the noiseless version,
	 * never throws for a well-formed linear {@code f} -- noise can legitimately push the winning
	 * probability below {@code 1}, that degradation is the point of this class, not an error; use
	 * {@link #outcomeProbability(IntPredicate, int, MatrixComplex[], int, int)} on the returned
	 * guess to see how confident the recovery actually was.
	 * @param f The oracle function under test, PROMISED to be {@code f(x)=(a.x) mod 2} for some
	 * hidden {@code a} (e.g. from {@link BernsteinVazirani#oracleFunction(int, int)}).
	 * @param n The number of input qubits, must be at least 1.
	 * @param kraus The noise channel's Kraus operators.
	 * @param noisyQubit The qubit the channel acts on, in {@code [0,n]} ({@code n} itself being the
	 * ancilla).
	 * @return The best-guess secret, as an integer in {@code [0,2^n)}.
	 */
	public static int findMostLikelySecret(IntPredicate f, int n, MatrixComplex[] kraus, int noisyQubit) {
		MatrixComplex rho = NoisyDeutschJozsa.circuitDensityMatrix(f, n, kraus, noisyQubit);
		int dim = 1 << n;
		int best = -1;
		double bestProbability = -1.0;
		for (int x = 0; x < dim; ++x) {
			double probability = NoisyDeutschJozsa.diagonalProbability(rho, 2 * x) + NoisyDeutschJozsa.diagonalProbability(rho, 2 * x + 1);
			if (probability > bestProbability) {
				best = x;
				bestProbability = probability;
			}
		}
		return best;
	}
}
