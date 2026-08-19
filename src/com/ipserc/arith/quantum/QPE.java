package com.ipserc.arith.quantum;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Quantum Phase Estimation (QPE): given a 1-qubit unitary {@code U} with a known eigenvector
 * {@code |psi>} and eigenvalue {@code e^(2*pi*i*theta)}, estimates {@code theta} into a {@code t}
 * qubit "counting" register -- if {@code theta} is exactly representable with {@code t} bits, the
 * counting register collapses to {@code |2^t * theta>} with probability exactly {@code 1}.
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md}),
 * built directly on top of the just-closed QFT (candidate flagged when the QFT<->Fourier bridge
 * was closed): {@link #circuit(int, MatrixComplex)} reuses {@link Qubits#hadamard()}/{@link
 * Qubits#controlledGate(MatrixComplex, int, int, int)} (already used by {@link QFT#circuit(int)})
 * plus {@link QFT#circuit(int)}{@code .adjoint()} for the inverse-QFT stage, over a register of
 * {@code n=t+1} qubits: qubits {@code 0..t-1} are the counting register (MSB-first, same
 * convention as {@link Qubits#ket(int...)}), qubit {@code t} (the LAST one) holds the eigenvector.
 * <p>
 * Circuit, in application order (left-to-right below == first-applied-to-first, i.e. each stage's
 * matrix is left-multiplied onto the running state/matrix, exactly as {@link QFT#circuit(int)}
 * builds its own stages):
 * <ol>
 * <li>{@code hadamard()} on each of the {@code t} counting qubits (identity on qubit {@code t}).</li>
 * <li>For {@code j=0..t-1}: {@code controlledGate(U^(2^(t-1-j)), control=j, target=t, n)} -- qubit
 * {@code j} is the MSB-first {@code j}-th counting qubit, and it must carry the LARGEST remaining
 * power of {@code U} (exponent {@code 2^(t-1-j)}, not the naive {@code 2^j}) so that, after the
 * inverse QFT, the counting register reads out the estimated integer MSB-first, matching {@link
 * QFT#circuit(int)}'s own convention for {@code |k>} directly (empirically verified below against
 * the two known exact test cases; the naive {@code 2^j} pairing was tried first and gave the WRONG
 * index -- see {@code TestQuantum_QPE01}).</li>
 * <li>{@code QFT.circuit(t).adjoint()} (inverse QFT) on the counting register only, extended to
 * the full {@code n}-qubit space as {@code QFT.circuit(t).adjoint() (x) I_2} -- the counting
 * register occupies the FIRST {@code t} qubits (matching {@link Qubits#ket(int...)}'s and {@link
 * DeutschJozsa#oracle}'s convention of "main register first, ancilla/eigenvector qubit last"), so
 * the identity for the untouched eigenvector qubit goes on the RIGHT of the Kronecker product.</li>
 * </ol>
 */
public final class QPE {

	private final static String VERSION = "1.0 (2026_0819_1200)";
	/* VERSION Release Note
	 * 1.0 (2026_0819_1200)
	 * Primera version -- circuit()/countingProbabilities() (Etapa candidata "QPE" del roadmap tras
	 * cerrar el puente QFT<->Fourier). Verificado empiricamente contra los 2 casos de fase exacta
	 * (k0=t=2, k0=t=3, U=Qubits.phaseGate(k0), autovector Qubits.ket1()) y contra unitariedad.
	 */

	private QPE() {}

	/**
	 * Builds the full {@code t+1}-qubit QPE circuit unitary for a given 1-qubit unitary {@code U}:
	 * Hadamards on the {@code t} counting qubits, controlled-{@code U^(2^j)} from each counting
	 * qubit {@code j} onto the last (eigenvector) qubit, then the inverse QFT on the counting
	 * register alone. See the class doc for the exact stage order/orientation, empirically
	 * verified (not just assumed) against known exact-phase cases.
	 * @param t The number of counting qubits, must be at least 1.
	 * @param u The 2x2 unitary whose eigenphase is being estimated.
	 * @return The {@code 2^(t+1) x 2^(t+1)} QPE circuit unitary, over a register of {@code t+1}
	 * qubits (counting register first, eigenvector qubit last).
	 * @throws IllegalArgumentException if {@code t<1}.
	 */
	public static MatrixComplex circuit(int t, MatrixComplex u) {
		if (t < 1) {
			throw new IllegalArgumentException("circuit() needs at least 1 counting qubit, got t=" + t);
		}
		int n = t + 1;
		int targetIndex = t;
		int dim = 1 << n;
		MatrixComplex result = MatrixComplex.eye(dim);

		// Stage 1: Hadamard on each counting qubit (identity on the eigenvector qubit, left alone).
		for (int j = 0; j < t; ++j) {
			result = Qubits.operatorOnQubit(Qubits.hadamard(), j, n).times(result);
		}

		// Stage 2: controlled-U^(2^(t-1-j)) from counting qubit j onto the eigenvector qubit -- qubit
		// j=0 is the MSB-first FIRST counting qubit, which must carry the LARGEST power (2^(t-1)),
		// matching the standard textbook wiring; empirically confirmed below (the naive 2^j pairing
		// gave the wrong index -- see TestQuantum_QPE01).
		for (int j = 0; j < t; ++j) {
			int power = 1 << (t - 1 - j);
			MatrixComplex uPow = u.power(power);
			MatrixComplex cu = Qubits.controlledGate(uPow, j, targetIndex, n);
			result = cu.times(result);
		}

		// Stage 3: inverse QFT on the counting register (first t qubits), identity on the last qubit.
		MatrixComplex inverseQftExtended = QFT.circuit(t).adjoint().kroneckerprod(MatrixComplex.eye(2));
		result = inverseQftExtended.times(result);

		return result;
	}

	/**
	 * Applies {@link #circuit(int, MatrixComplex)} to the initial state {@code |0>^t (x) eigenvector}
	 * and returns the probability distribution over the {@code 2^t} computational-basis outcomes of
	 * the counting register alone -- obtained by summing {@code |amplitude|^2} over the 2 possible
	 * states of the (untouched, but still present in the state vector) eigenvector qubit for each
	 * counting-register index. Since {@code eigenvector} is assumed to actually be an eigenvector of
	 * {@code u} (not checked here), that qubit stays disentangled from the counting register
	 * throughout, so exactly one of the 2 amplitudes summed per index is nonzero in practice -- the
	 * sum is just the simplest way to read off "the counting register's marginal probability"
	 * without hand-picking which of the 2 branches is the live one.
	 * @param t The number of counting qubits, must be at least 1.
	 * @param u The 2x2 unitary whose eigenphase is being estimated.
	 * @param eigenvector A 2x1 (approximate) eigenvector of {@code u} (e.g. {@link Qubits#ket1()}).
	 * @return A {@code double[2^t]} array, index {@code k} holding the probability of the counting
	 * register collapsing to {@code |k>} (MSB-first, same convention as {@link Qubits#ket(int...)}).
	 * @throws IllegalArgumentException if {@code t<1}.
	 */
	public static double[] countingProbabilities(int t, MatrixComplex u, MatrixComplex eigenvector) {
		if (t < 1) {
			throw new IllegalArgumentException("countingProbabilities() needs at least 1 counting qubit, got t=" + t);
		}
		int n = t + 1;
		int[] zeros = new int[t];
		MatrixComplex countingZeros = Qubits.ket(zeros);
		MatrixComplex initialState = countingZeros.kroneckerprod(eigenvector);

		MatrixComplex finalState = circuit(t, u).times(initialState);

		int countingDim = 1 << t;
		double[] probabilities = new double[countingDim];
		for (int fullIndex = 0; fullIndex < (1 << n); ++fullIndex) {
			int countingIndex = fullIndex >> 1;	// eigenvector qubit is the last (least significant) one
			Complex amplitude = finalState.getItem(fullIndex, 0);
			probabilities[countingIndex] += amplitude.mod() * amplitude.mod();
		}
		return probabilities;
	}

}
