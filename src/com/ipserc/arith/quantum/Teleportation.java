package com.ipserc.arith.quantum;

import java.util.Random;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * The standard quantum-teleportation protocol: Alice holds an unknown qubit {@code psi} and one
 * half of a Bell pair, Bob holds the other half; after a local 2-qubit gate + measurement on
 * Alice's side and 2 classical bits sent to Bob, Bob's qubit becomes exactly {@code psi} once he
 * applies the correction the 2 bits dictate -- no qubit physically travels, only classical bits.
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoséptima sesión) -- the "toy quantum algorithm" candidate catalogued at the close of the
 * Trigesimosexta sesión, built on {@link Qubits} (kets/gates), the same {@link
 * Qubits#bellPhiPlus()} used by {@link BellTest}, and {@link Qubits#controlledGate(MatrixComplex,
 * int, int, int)}/{@link Qubits#hadamard()} added in this session for the circuit itself.
 * <p>
 * 3-qubit register throughout, MSB-first (same convention as {@link Qubits#ket(int...)}): qubit 0
 * is {@code psi} (Alice), qubit 1 is Alice's half of the Bell pair, qubit 2 is Bob's half.
 */
public final class Teleportation {

	private final static String VERSION = "1.0 (2026_0813_2200)";

	private Teleportation() {}

	/**
	 * The 3-qubit state right before Alice's measurement: {@code psi} entangled with a Bell pair,
	 * then Alice's local circuit ({@code CNOT} qubit0-&gt;qubit1, then {@code Hadamard} on qubit0)
	 * applied -- the standard "Bell-basis measurement expressed as a computational-basis
	 * measurement after a disentangling circuit" trick. Package-visible (not exposed as public
	 * API, since it's an intermediate a caller of {@link #simulate}/{@link #correctedStateForOutcome}
	 * never needs directly) so a future protocol variant in this same package could reuse it.
	 * @param psi Alice's qubit to teleport, a normalized 2x1 column vector.
	 * @return The 8x1 state of the full 3-qubit register after the circuit, before measurement.
	 */
	static MatrixComplex circuitState(MatrixComplex psi) {
		MatrixComplex full = psi.kroneckerprod(Qubits.bellPhiPlus());
		MatrixComplex afterCnot = Qubits.controlledGate(Qubits.pauliX(), 0, 1, 3).times(full);
		return Qubits.operatorOnQubit(Qubits.hadamard(), 0, 3).times(afterCnot);
	}

	/**
	 * The Born-rule probability of Alice measuring the classical outcome {@code (m1,m2)} on qubits
	 * 0/1 -- the sum of squared moduli of the 2 (qubit-2) amplitudes of {@link #circuitState} whose
	 * index matches {@code (m1,m2)}. A hallmark of teleportation, verified in {@code
	 * ScratchTeleportationAudit01}: this comes out exactly {@code 0.25} for all 4 outcomes,
	 * regardless of {@code psi} -- Alice's measurement carries no information about {@code psi}
	 * itself, only the (uniformly random) classical bits Bob needs for his correction.
	 * @param psi Alice's qubit to teleport.
	 * @param m1 Alice's first measured bit (qubit 0's outcome), {@code 0} or {@code 1}.
	 * @param m2 Alice's second measured bit (qubit 1's outcome), {@code 0} or {@code 1}.
	 * @return The probability of this outcome, in {@code [0,1]}.
	 */
	public static double probabilityOfOutcome(MatrixComplex psi, int m1, int m2) {
		MatrixComplex state = circuitState(psi);
		int base = branchBaseIndex(m1, m2);
		double c0mod = state.getItem(base, 0).mod();
		double c1mod = state.getItem(base + 1, 0).mod();
		return c0mod * c0mod + c1mod * c1mod;
	}

	/**
	 * Bob's qubit, corrected, for the classical outcome {@code (m1,m2)} Alice measured -- the exact
	 * (no randomness) prediction every {@link #simulate} run is expected to match. Derived
	 * analytically (expanding {@code psi (x) Phi+} through {@code CNOT} then {@code Hadamard} in
	 * the Pauli basis): the raw (uncorrected) post-measurement state of qubit 2 for outcome
	 * {@code (m1,m2)} is {@code X^m2 Z^m1 psi} up to a positive real scale, so the correction that
	 * undoes it is {@code Z^m1} applied after {@code X^m2} -- {@code (0,0)} needs no correction,
	 * {@code (0,1)} needs {@code X}, {@code (1,0)} needs {@code Z}, {@code (1,1)} needs {@code X}
	 * then {@code Z}.
	 * @param psi Alice's qubit to teleport.
	 * @param m1 Alice's first measured bit, {@code 0} or {@code 1}.
	 * @param m2 Alice's second measured bit, {@code 0} or {@code 1}.
	 * @return Bob's corrected qubit, normalized -- EXACTLY equal to {@code psi} (not just up to a
	 * global phase: the amplitude ratio extracted per branch from {@link #circuitState} is always
	 * {@code psi}'s own ratio scaled by a positive real factor, so normalizing preserves the exact
	 * phase), verified in {@code ScratchTeleportationAudit01}.
	 */
	public static MatrixComplex correctedStateForOutcome(MatrixComplex psi, int m1, int m2) {
		MatrixComplex state = circuitState(psi);
		int base = branchBaseIndex(m1, m2);
		MatrixComplex raw = new MatrixComplex(2, 1);
		raw.setItem(0, 0, state.getItem(base, 0));
		raw.setItem(1, 0, state.getItem(base + 1, 0));
		MatrixComplex corrected = raw.normalizeByCols();
		if (m2 == 1) { corrected = Qubits.pauliX().times(corrected); }
		if (m1 == 1) { corrected = Qubits.pauliZ().times(corrected); }
		return corrected;
	}

	private static int branchBaseIndex(int m1, int m2) {
		if (m1 != 0 && m1 != 1) { throw new IllegalArgumentException("m1 must be 0 or 1, got " + m1); }
		if (m2 != 0 && m2 != 1) { throw new IllegalArgumentException("m2 must be 0 or 1, got " + m2); }
		return (m1 * 2 + m2) * 2;
	}

	/**
	 * A full simulated run of the protocol: samples Alice's classical outcome via {@link
	 * #probabilityOfOutcome} (Born-rule sampling, the same technique {@link
	 * BellTest#simulateCorrelation} already uses), then returns Bob's corrected qubit for that
	 * outcome -- the same way a real lab run would sample one of the 4 branches and Bob would apply
	 * whichever correction the 2 classical bits he received call for.
	 * @param psi Alice's qubit to teleport.
	 * @param rng The random source (no fixed seed required -- pass a seeded {@link Random} for
	 * reproducible runs).
	 * @return Bob's corrected qubit -- equal to {@code psi} regardless of which outcome was sampled.
	 */
	public static MatrixComplex simulate(MatrixComplex psi, Random rng) {
		double r = rng.nextDouble();
		double cumulative = 0.0;
		int m1 = 1, m2 = 1; // last bucket by default, guards against floating-point rounding at the tail
		outer:
		for (int a = 0; a < 2; ++a) {
			for (int b = 0; b < 2; ++b) {
				cumulative += probabilityOfOutcome(psi, a, b);
				if (r < cumulative) { m1 = a; m2 = b; break outer; }
			}
		}
		return correctedStateForOutcome(psi, m1, m2);
	}
}
