package com.ipserc.arith.quantum;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Quantum teleportation ({@link Teleportation}) with a noise channel ({@link Decoherence})
 * applied to one qubit of the 3-qubit register BEFORE Alice's local circuit runs -- the standard
 * question "what happens to teleportation fidelity if the shared entangled resource (or the input
 * state itself) has already decohered by the time the protocol runs?", cast in the density-matrix
 * formalism ({@link DensityMatrix}) since a noise channel can turn a pure state genuinely mixed,
 * something a plain state vector (what {@link Teleportation} works with) can no longer represent.
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoctava sesión) -- combines 2 previously separate exercises of this package for the first
 * time: {@link Teleportation} (the protocol circuit) and {@link Decoherence} (the noise model),
 * bridged through {@link DensityMatrix#partialTrace(MatrixComplex, int, int...)} (already used to
 * extract Bob's qubit from the 3-qubit register in {@link Teleportation}'s pure-state math, now
 * needed on a genuinely mixed 3-qubit density matrix instead).
 * <p>
 * 3-qubit register throughout, same convention {@link Teleportation} uses: qubit 0 is {@code psi}
 * (Alice), qubit 1 is Alice's half of the Bell pair, qubit 2 is Bob's half.
 */
public final class NoisyTeleportation {

	private final static String VERSION = "1.0 (2026_0814_1000)";

	private NoisyTeleportation() {}

	/**
	 * The 3-qubit density matrix right before Alice's measurement, WITH noise: {@code psi}
	 * entangled with a Bell pair, {@code kraus} applied to qubit {@code noisyQubit} (before any
	 * circuit gate runs -- e.g. the shared entangled pair having already decohered by the time
	 * teleportation is attempted), then Alice's local circuit ({@code CNOT} qubit0-&gt;qubit1, then
	 * {@code Hadamard} on qubit0) applied by conjugation ({@code U*rho*U^dagger}) -- the
	 * density-matrix analog of {@link Teleportation#circuitState(MatrixComplex)}. Package-visible
	 * for the same reason that method is: an intermediate a caller of this class's public methods
	 * never needs directly.
	 * @param psi Alice's qubit to teleport, a normalized 2x1 column vector.
	 * @param kraus The noise channel's Kraus operators (e.g. from {@link Decoherence#bitFlip(double)}).
	 * @param noisyQubit The 0-based index ({@code 0}, {@code 1} or {@code 2}) of the qubit the
	 * channel acts on.
	 * @return The {@code 8x8} density matrix of the full 3-qubit register after noise + circuit,
	 * before measurement.
	 */
	static MatrixComplex circuitDensityMatrix(MatrixComplex psi, MatrixComplex[] kraus, int noisyQubit) {
		MatrixComplex rho0 = DensityMatrix.of(psi.kroneckerprod(Qubits.bellPhiPlus()));
		MatrixComplex rhoNoisy = Decoherence.apply(rho0, kraus, noisyQubit, 3);
		MatrixComplex cnot = Qubits.controlledGate(Qubits.pauliX(), 0, 1, 3);
		MatrixComplex hadamardOnQubit0 = Qubits.operatorOnQubit(Qubits.hadamard(), 0, 3);
		MatrixComplex u = hadamardOnQubit0.times(cnot);
		return u.times(rhoNoisy).times(u.adjoint());
	}

	/** The projector {@code |b0><b0|_0 (x) |b1><b1|_1 (x) I_2} onto Alice's classical outcome
	 * {@code (b0,b1)} on qubits 0/1, identity on qubit 2 -- the density-matrix analog of the
	 * amplitude indexing {@link Teleportation} uses directly on a pure state vector. */
	private static MatrixComplex outcomeProjector(int b0, int b1) {
		return Qubits.operatorOnQubit(singleQubitProjector(b0), 0, 3)
				.times(Qubits.operatorOnQubit(singleQubitProjector(b1), 1, 3));
	}

	private static MatrixComplex singleQubitProjector(int bit) {
		MatrixComplex ket = (bit == 0) ? Qubits.ket0() : Qubits.ket1();
		return ket.times(ket.adjoint());
	}

	/**
	 * The Born-rule probability of Alice measuring classical outcome {@code (m1,m2)}, WITH noise
	 * applied to qubit {@code noisyQubit} -- {@code Tr(rho*P_(m1,m2))}, {@code rho} from {@link
	 * #circuitDensityMatrix(MatrixComplex, MatrixComplex[], int)}. Unlike the noiseless {@link
	 * Teleportation#probabilityOfOutcome} (implicit, always exactly {@code 0.25}), noise CAN skew
	 * these away from uniform -- verified NOT to in {@code ScratchNoisyTeleportationAudit01} for
	 * every channel family this package provides (they all commute with the Bell-pair symmetry that
	 * makes Alice's outcome probabilities uniform in the noiseless case), but the API doesn't assume
	 * that in general.
	 * @param psi Alice's qubit to teleport.
	 * @param kraus The noise channel's Kraus operators.
	 * @param noisyQubit The qubit the channel acts on.
	 * @param m1 Alice's first measured bit, {@code 0} or {@code 1}.
	 * @param m2 Alice's second measured bit, {@code 0} or {@code 1}.
	 * @return The probability of this outcome, in {@code [0,1]}.
	 */
	public static double probabilityOfOutcome(MatrixComplex psi, MatrixComplex[] kraus, int noisyQubit, int m1, int m2) {
		MatrixComplex rho = circuitDensityMatrix(psi, kraus, noisyQubit);
		Complex trace = rho.times(outcomeProjector(m1, m2)).trace();
		if (Math.abs(trace.imp()) > 1e-9) {
			throw new IllegalStateException("Tr(rho*outcomeProjector) came out complex (Im=" + trace.imp()
					+ ") -- expected a real probability; check that rho is Hermitian");
		}
		return trace.rep();
	}

	/**
	 * Bob's corrected qubit, as a {@code 2x2} density matrix, for the classical outcome
	 * {@code (m1,m2)} Alice measured -- the noisy analog of {@link
	 * Teleportation#correctedStateForOutcome(MatrixComplex, int, int)}: collapse {@link
	 * #circuitDensityMatrix(MatrixComplex, MatrixComplex[], int)} onto {@code (m1,m2)}, trace out
	 * qubits 0/1 (via {@link DensityMatrix#partialTrace(MatrixComplex, int, int...)}) to get Bob's
	 * reduced state, normalize, then apply the SAME correction {@link Teleportation} derived
	 * analytically ({@code Z^m1} after {@code X^m2}) -- by conjugation ({@code op*rho*op}, since
	 * every correction Pauli is Hermitian and its own inverse) instead of left-multiplication (the
	 * density-matrix form of applying a unitary to a state).
	 * @param psi Alice's qubit to teleport.
	 * @param kraus The noise channel's Kraus operators.
	 * @param noisyQubit The qubit the channel acts on.
	 * @param m1 Alice's first measured bit, {@code 0} or {@code 1}.
	 * @param m2 Alice's second measured bit, {@code 0} or {@code 1}.
	 * @return Bob's corrected {@code 2x2} density matrix, trace {@code 1} -- EXACTLY {@code
	 * |psi><psi|} when {@code kraus} is a no-op channel (e.g. {@link Decoherence#bitFlip(double)
	 * Decoherence.bitFlip(0.0)}), strictly mixed otherwise.
	 * @throws IllegalArgumentException if {@code probabilityOfOutcome(...)} for this {@code
	 * (m1,m2)} is {@code ~0} -- normalizing by a near-zero probability is meaningless (use {@link
	 * #averageFidelity(MatrixComplex, MatrixComplex[], int)} instead, which never divides by the
	 * per-branch probability).
	 */
	public static MatrixComplex correctedDensityMatrixForOutcome(MatrixComplex psi, MatrixComplex[] kraus, int noisyQubit, int m1, int m2) {
		MatrixComplex rho = circuitDensityMatrix(psi, kraus, noisyQubit);
		MatrixComplex projector = outcomeProjector(m1, m2);
		MatrixComplex collapsed = projector.times(rho).times(projector);
		MatrixComplex reducedUnnormalized = DensityMatrix.partialTrace(collapsed, 3, 0, 1);
		double probability = reducedUnnormalized.trace().rep();
		if (probability < 1e-12) {
			throw new IllegalArgumentException("probabilityOfOutcome(m1=" + m1 + ",m2=" + m2 + ")~0 -- "
					+ "cannot normalize; use averageFidelity() instead if this branch legitimately never happens");
		}
		return applyCorrection(reducedUnnormalized.times(1.0 / probability), m1, m2);
	}

	private static MatrixComplex applyCorrection(MatrixComplex rho, int m1, int m2) {
		MatrixComplex corrected = rho;
		if (m2 == 1) { corrected = Qubits.pauliX().times(corrected).times(Qubits.pauliX()); }
		if (m1 == 1) { corrected = Qubits.pauliZ().times(corrected).times(Qubits.pauliZ()); }
		return corrected;
	}

	/**
	 * The fidelity {@code <psi|rho|psi>} of Bob's corrected qubit (for classical outcome {@code
	 * (m1,m2)}) to the original {@code psi} -- {@code 1} for a perfect teleportation of this
	 * branch, less than {@code 1} whenever noise degraded it.
	 * @param psi Alice's qubit to teleport.
	 * @param kraus The noise channel's Kraus operators.
	 * @param noisyQubit The qubit the channel acts on.
	 * @param m1 Alice's first measured bit, {@code 0} or {@code 1}.
	 * @param m2 Alice's second measured bit, {@code 0} or {@code 1}.
	 * @return The fidelity, in {@code [0,1]}.
	 */
	public static double fidelityForOutcome(MatrixComplex psi, MatrixComplex[] kraus, int noisyQubit, int m1, int m2) {
		MatrixComplex rho = correctedDensityMatrixForOutcome(psi, kraus, noisyQubit, m1, m2);
		return fidelity(psi, rho);
	}

	private static double fidelity(MatrixComplex psi, MatrixComplex rho) {
		Complex f = psi.adjoint().times(rho).times(psi).getItem(0, 0);
		if (Math.abs(f.imp()) > 1e-9) {
			throw new IllegalStateException("fidelity <psi|rho|psi> came out complex (Im=" + f.imp()
					+ ") -- expected a real value; check that rho is Hermitian");
		}
		return f.rep();
	}

	/**
	 * The overall teleportation fidelity for {@code psi}, averaged over Alice's 4 possible
	 * measurement outcomes weighted by their probability -- {@code sum_(m1,m2)
	 * probabilityOfOutcome(m1,m2) * fidelityForOutcome(m1,m2)}, computed WITHOUT ever normalizing
	 * (and so without any risk of dividing by a near-zero branch probability, unlike {@link
	 * #correctedDensityMatrixForOutcome}: the per-branch normalization and the probability weight
	 * exactly cancel algebraically, so this sums the UNNORMALIZED collapsed-and-corrected
	 * contributions directly).
	 * @param psi Alice's qubit to teleport.
	 * @param kraus The noise channel's Kraus operators.
	 * @param noisyQubit The qubit the channel acts on.
	 * @return The average fidelity, in {@code [0,1]} -- EXACTLY {@code 1} for a no-op channel
	 * (matching {@link Teleportation}'s noiseless exact-equality result), strictly less than {@code
	 * 1} for genuine noise.
	 */
	public static double averageFidelity(MatrixComplex psi, MatrixComplex[] kraus, int noisyQubit) {
		MatrixComplex rho = circuitDensityMatrix(psi, kraus, noisyQubit);
		double total = 0.0;
		for (int m1 = 0; m1 <= 1; ++m1) {
			for (int m2 = 0; m2 <= 1; ++m2) {
				MatrixComplex collapsed = outcomeProjector(m1, m2).times(rho).times(outcomeProjector(m1, m2));
				MatrixComplex reducedUnnormalized = DensityMatrix.partialTrace(collapsed, 3, 0, 1);
				MatrixComplex corrected = applyCorrection(reducedUnnormalized, m1, m2);
				total += fidelity(psi, corrected);
			}
		}
		return total;
	}
}
