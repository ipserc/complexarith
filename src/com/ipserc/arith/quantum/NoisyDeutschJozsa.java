package com.ipserc.arith.quantum;

import java.util.function.IntPredicate;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * The Deutsch-Jozsa algorithm ({@link DeutschJozsa}) with a noise channel ({@link Decoherence})
 * applied to one qubit of the {@code n+1}-qubit register right after state preparation, before
 * any circuit gate runs -- the question "does the algorithm's deterministic all-or-nothing
 * guarantee ({@code probabilityAllZero()} EXACTLY {@code 0} or {@code 1}) survive if the register
 * has already decohered by the time the circuit executes?" Cast in the density-matrix formalism
 * ({@link DensityMatrix}) for the same reason {@link NoisyTeleportation} is: a noise channel can
 * turn a pure state genuinely mixed, something the plain state vector {@link DeutschJozsa} works
 * with can no longer represent.
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoctava sesión) -- second cross-exercise combination, after {@link NoisyTeleportation};
 * reuses {@link DeutschJozsa#hadamardChain(int)}/{@link DeutschJozsa#oracle(IntPredicate, int)}
 * (both package-visible) verbatim instead of duplicating the circuit's gate construction.
 */
public final class NoisyDeutschJozsa {

	private final static String VERSION = "1.0 (2026_0814_1100)";

	private NoisyDeutschJozsa() {}

	/**
	 * The {@code n+1}-qubit density matrix right before measurement, WITH noise: prepare {@code
	 * |0>^n|1>}, apply {@code kraus} to qubit {@code noisyQubit}, then the full Deutsch-Jozsa
	 * circuit ({@code H^(x)(n+1)}, {@link DeutschJozsa#oracle(IntPredicate, int)}, {@code H^(x)n}
	 * on the input register only) by conjugation ({@code U*rho*U^dagger}) -- the density-matrix
	 * analog of {@link DeutschJozsa#runCircuit(IntPredicate, int)}. Package-visible for the same
	 * reason that method is: an intermediate a caller of {@link #probabilityAllZero(IntPredicate,
	 * int, MatrixComplex[], int)} never needs directly.
	 * @param f The Boolean function under test.
	 * @param n The number of input qubits, must be at least 1.
	 * @param kraus The noise channel's Kraus operators (e.g. from {@link Decoherence#bitFlip(double)}).
	 * @param noisyQubit The 0-based index (in {@code [0,n]}, {@code n} itself being the ancilla) of
	 * the qubit the channel acts on.
	 * @return The {@code 2^(n+1) x 2^(n+1)} density matrix after noise + circuit, before measurement.
	 */
	static MatrixComplex circuitDensityMatrix(IntPredicate f, int n, MatrixComplex[] kraus, int noisyQubit) {
		int[] initBits = new int[n + 1];
		initBits[n] = 1; // ancilla starts at |1>, everything else at |0>
		MatrixComplex rho0 = DensityMatrix.of(Qubits.ket(initBits));
		MatrixComplex rhoNoisy = Decoherence.apply(rho0, kraus, noisyQubit, n + 1);

		MatrixComplex hAll = DeutschJozsa.hadamardChain(n + 1);
		MatrixComplex oracleOp = DeutschJozsa.oracle(f, n);
		MatrixComplex hInputOnly = DeutschJozsa.hadamardChain(n).kroneckerprod(Qubits.identity2());
		MatrixComplex u = hInputOnly.times(oracleOp).times(hAll);

		return u.times(rhoNoisy).times(u.adjoint());
	}

	/**
	 * The measurement probability of the all-zero outcome on the first {@code n} qubits WITH noise
	 * applied to qubit {@code noisyQubit} -- the noisy analog of {@link
	 * DeutschJozsa#probabilityAllZero(IntPredicate, int)}: the sum of the 2 relevant diagonal
	 * entries ({@code x=0}, either ancilla value) of {@link #circuitDensityMatrix(IntPredicate,
	 * int, MatrixComplex[], int)} -- a density matrix's diagonal entries ARE the computational-basis
	 * measurement probabilities directly, no projector/trace step needed (unlike {@link
	 * NoisyTeleportation#probabilityOfOutcome}, which measures 2 qubits jointly and so does need one).
	 * @param f The Boolean function under test.
	 * @param n The number of input qubits, must be at least 1.
	 * @param kraus The noise channel's Kraus operators.
	 * @param noisyQubit The qubit the channel acts on, in {@code [0,n]} ({@code n} itself being the
	 * ancilla).
	 * @return The probability, in {@code [0,1]} -- EXACTLY {@code 1}/{@code 0} for a constant/
	 * balanced {@code f} with a no-op channel (matching {@link DeutschJozsa} exactly). WHERE noise
	 * matters is asymmetric and non-obvious, verified in {@code ScratchNoisyDeutschJozsaAudit01}
	 * rather than assumed: noise on the ANCILLA ({@code noisyQubit=n}) never moves this probability
	 * at all for a CONSTANT {@code f} (stays EXACTLY {@code 1} under any channel, any strength -- a
	 * constant {@code f} never entangles the ancilla with the input register, so the final {@code
	 * H^(x)n} always exactly undoes the initial one regardless of the ancilla's state), but DOES
	 * degrade a BALANCED {@code f} (pulls the probability up from {@code 0}, with a clean linear
	 * closed form for some channels -- e.g. exactly {@code p/2} for {@link
	 * Decoherence#depolarizing(double)}, exactly {@code gamma} for {@link
	 * Decoherence#amplitudeDamping(double)}). Noise on an INPUT qubit is the opposite: it degrades a
	 * CONSTANT {@code f} (e.g. {@link Decoherence#bitFlip(double)} gives the exact closed form
	 * {@code 1-p}), since that qubit is part of what gets measured -- but {@link
	 * Decoherence#amplitudeDamping(double)}/{@link Decoherence#phaseFlip(double)} on an input qubit
	 * have ZERO effect for ANY {@code f}, because input qubits start EXACTLY at {@code |0>}, the
	 * fixed point of both those channels.
	 * @throws IllegalStateException if a diagonal entry comes out with a non-negligible imaginary
	 * part -- would mean {@code rho} isn't Hermitian.
	 */
	public static double probabilityAllZero(IntPredicate f, int n, MatrixComplex[] kraus, int noisyQubit) {
		MatrixComplex rho = circuitDensityMatrix(f, n, kraus, noisyQubit);
		return diagonalProbability(rho, 0) + diagonalProbability(rho, 1);
	}

	private static double diagonalProbability(MatrixComplex rho, int index) {
		Complex entry = rho.getItem(index, index);
		if (Math.abs(entry.imp()) > 1e-9) {
			throw new IllegalStateException("rho[" + index + "][" + index + "] came out complex (Im="
					+ entry.imp() + ") -- expected a real probability; check that rho is Hermitian");
		}
		return entry.rep();
	}
}
