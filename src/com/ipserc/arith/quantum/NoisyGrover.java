package com.ipserc.arith.quantum;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Grover's search algorithm ({@link Grover}) combined with a noise channel ({@link Decoherence}),
 * in 2 deliberately distinct models: {@link #circuitDensityMatrix(int, int, int, MatrixComplex[],
 * int)}/{@link #search(int, int, MatrixComplex[], int)} apply the channel ONCE, right after the
 * initial uniform superposition {@code |s>} is prepared, before any oracle/diffusion round runs --
 * the same "noise at state prep, before the circuit" placement {@link NoisyDeutschJozsa}/{@link
 * NoisyBernsteinVazirani} use, asking whether a SINGLE dose of decoherence gets diluted or
 * amplified by the {@code sqrt(N)} rounds of amplitude amplification that follow; {@link
 * #circuitDensityMatrixPerIteration(int, int, int, MatrixComplex[], int)}/{@link
 * #searchPerIteration(int, int, MatrixComplex[], int)} instead re-apply the channel after EVERY
 * round -- more physically realistic (a real device's qubits keep interacting with their
 * environment for as long as the computation runs), and, as it turns out, NOT simply "the same
 * degradation but worse": see the per-iteration methods' doc for a channel whose effect is
 * qualitatively different (not just quantitatively) between the 2 models. Cast in the
 * density-matrix formalism ({@link DensityMatrix}) for the same reason every other {@code Noisy*}
 * class in this package is: a channel can turn the pure initial state genuinely mixed, something
 * {@link Grover}'s plain state vector can no longer represent.
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoctava sesión) -- fourth cross-exercise combination, after {@link NoisyTeleportation}/
 * {@link NoisyDeutschJozsa}/{@link NoisyBernsteinVazirani}; reuses {@link
 * NoisyDeutschJozsa#diagonalProbability(MatrixComplex, int)} (package-visible) instead of
 * duplicating the Hermiticity-checked diagonal read.
 */
public final class NoisyGrover {

	private final static String VERSION = "1.1 (2026_0814_1700)";
	/* VERSION Release Note
	 * 1.1 (2026_0814_1700)
	 * circuitDensityMatrixPerIteration()/searchPerIteration() -- el modelo de ruido "por iteracion"
	 * dejado fuera deliberadamente en la 1.0 a favor de la dosis unica, ahora anadido COMO
	 * ALTERNATIVA (no sustituye a circuitDensityMatrix()/search()): el canal se reaplica tras CADA
	 * ronda de oraculo+difusion en vez de una unica vez al preparar el estado.
	 */

	private NoisyGrover() {}

	/**
	 * The {@code n}-qubit density matrix after {@code iterations} noisy Grover rounds: prepare
	 * {@link Grover#initialState(int)}, apply {@code kraus} to qubit {@code noisyQubit}, then
	 * {@code iterations} rounds of {@code diffusion(n)*oracle(target,n)} by conjugation ({@code
	 * U*rho*U^dagger}) -- the density-matrix analog of {@link Grover#run(int, int, int)}.
	 * @param target The marked item's index, in {@code [0,2^n)}.
	 * @param n The number of qubits, must be at least 1.
	 * @param iterations The number of Grover iterations to run.
	 * @param kraus The noise channel's Kraus operators (e.g. from {@link Decoherence#bitFlip(double)}).
	 * @param noisyQubit The 0-based index (in {@code [0,n)}) of the qubit the channel acts on.
	 * @return The {@code 2^n x 2^n} density matrix after noise + {@code iterations} rounds.
	 */
	public static MatrixComplex circuitDensityMatrix(int target, int n, int iterations, MatrixComplex[] kraus, int noisyQubit) {
		MatrixComplex rho0 = DensityMatrix.of(Grover.initialState(n));
		MatrixComplex rho = Decoherence.apply(rho0, kraus, noisyQubit, n);

		MatrixComplex u = Grover.diffusion(n).times(Grover.oracle(target, n));
		MatrixComplex uDagger = u.adjoint();
		for (int i = 0; i < iterations; ++i) {
			rho = u.times(rho).times(uDagger);
		}
		return rho;
	}

	/**
	 * The measurement probability of {@code target} in {@code rho} -- the noisy analog of {@link
	 * Grover#probabilityOfTarget(MatrixComplex, int)}: the {@code target}-th diagonal entry
	 * directly (a density matrix's diagonal entries ARE the computational-basis measurement
	 * probabilities, no projector/trace step needed, same shortcut {@link
	 * NoisyDeutschJozsa#probabilityAllZero(java.util.function.IntPredicate, int, MatrixComplex[],
	 * int)} uses).
	 * @param rho A {@code 2^n x 2^n} density matrix (e.g. from {@link #circuitDensityMatrix(int,
	 * int, int, MatrixComplex[], int)}).
	 * @param target The basis-state index to measure the probability of.
	 * @return The probability, in {@code [0,1]}.
	 * @throws IllegalStateException if the diagonal entry comes out with a non-negligible imaginary
	 * part -- would mean {@code rho} isn't Hermitian.
	 */
	public static double probabilityOfTarget(MatrixComplex rho, int target) {
		return NoisyDeutschJozsa.diagonalProbability(rho, target);
	}

	/**
	 * Runs {@link Grover#optimalIterations(int)} noisy rounds of Grover search for {@code target}
	 * among {@code N=2^n} items (single noise dose at state prep, see the class doc) and returns
	 * the resulting probability of measuring it -- the noisy analog of {@link Grover#search(int,
	 * int)}.
	 * @param target The marked item's index.
	 * @param n The number of qubits, must be at least 1.
	 * @param kraus The noise channel's Kraus operators.
	 * @param noisyQubit The qubit the channel acts on, in {@code [0,n)}.
	 * @return The probability of measuring {@code target} after the optimal number of iterations.
	 */
	public static double search(int target, int n, MatrixComplex[] kraus, int noisyQubit) {
		MatrixComplex rho = circuitDensityMatrix(target, n, Grover.optimalIterations(n), kraus, noisyQubit);
		return probabilityOfTarget(rho, target);
	}

	/**
	 * The {@code n}-qubit density matrix after {@code iterations} noisy Grover rounds, with {@code
	 * kraus} re-applied to qubit {@code noisyQubit} AFTER EVERY ROUND instead of once before the
	 * first -- the "noise every iteration" model deliberately left out of {@link
	 * #circuitDensityMatrix(int, int, int, MatrixComplex[], int)} (see this class's doc), added here
	 * as a separate, explicit alternative rather than a replacement: a real device's qubits keep
	 * interacting with their environment for as long as the computation runs, not just once at the
	 * start, so this is the more physically realistic (and more expensive -- the channel runs
	 * {@code iterations} times instead of once) model of the 2.
	 * <p>
	 * Genuinely different from the single-dose model, not just "the same but worse": {@link
	 * Decoherence#bitFlip(double)} is an EXACT invariant of {@link #search(int, int,
	 * MatrixComplex[], int)} (the initial {@code |+>^n} state is X's fixed point, see the class doc
	 * of {@link NoisyGrover}) but NOT of {@link #searchPerIteration(int, int, MatrixComplex[], int)}
	 * -- after the first oracle+diffusion round the marginal state of any single qubit is generally
	 * no longer {@code |+>}, so it stops being a fixed point of {@code X}, and a bit-flip channel
	 * re-applied each round does measurably degrade the search (confirmed numerically, not assumed
	 * from the single-dose result).
	 * @param target The marked item's index, in {@code [0,2^n)}.
	 * @param n The number of qubits, must be at least 1.
	 * @param iterations The number of Grover iterations to run.
	 * @param kraus The noise channel's Kraus operators.
	 * @param noisyQubit The 0-based index (in {@code [0,n)}) of the qubit the channel acts on.
	 * @return The {@code 2^n x 2^n} density matrix after {@code iterations} rounds, each followed by
	 * the noise channel.
	 */
	public static MatrixComplex circuitDensityMatrixPerIteration(int target, int n, int iterations, MatrixComplex[] kraus, int noisyQubit) {
		MatrixComplex rho = DensityMatrix.of(Grover.initialState(n));
		MatrixComplex u = Grover.diffusion(n).times(Grover.oracle(target, n));
		MatrixComplex uDagger = u.adjoint();
		for (int i = 0; i < iterations; ++i) {
			rho = u.times(rho).times(uDagger);
			rho = Decoherence.apply(rho, kraus, noisyQubit, n);
		}
		return rho;
	}

	/**
	 * Runs {@link Grover#optimalIterations(int)} noisy rounds of Grover search for {@code target}
	 * among {@code N=2^n} items (noise re-applied after EVERY round, see {@link
	 * #circuitDensityMatrixPerIteration(int, int, int, MatrixComplex[], int)}) and returns the
	 * resulting probability of measuring it -- the "per iteration" analog of {@link #search(int,
	 * int, MatrixComplex[], int)}.
	 * @param target The marked item's index.
	 * @param n The number of qubits, must be at least 1.
	 * @param kraus The noise channel's Kraus operators.
	 * @param noisyQubit The qubit the channel acts on, in {@code [0,n)}.
	 * @return The probability of measuring {@code target} after the optimal number of iterations.
	 */
	public static double searchPerIteration(int target, int n, MatrixComplex[] kraus, int noisyQubit) {
		MatrixComplex rho = circuitDensityMatrixPerIteration(target, n, Grover.optimalIterations(n), kraus, noisyQubit);
		return probabilityOfTarget(rho, target);
	}
}
