package com.ipserc.arith.quantum;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Single-qubit noise channels (bit-flip, phase-flip, bit-phase-flip, depolarizing, amplitude
 * damping) expressed as Kraus operators, and {@link #apply(MatrixComplex, MatrixComplex[], int,
 * int)} to run any of them on one qubit of an {@code nQubits}-qubit density matrix -- {@code
 * rho' = sum_k E_k*rho*E_k^dagger}, the standard open-quantum-system way to model decoherence
 * (information leaking into an unmodeled environment) without simulating that environment
 * explicitly.
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoctava sesión) -- the "ruido/decoherencia" candidate catalogued at the close of the
 * Trigesimosexta sesión, built on {@link DensityMatrix} (the mixed-state formalism decoherence
 * needs -- a pure state stays pure under unitary evolution, only a channel like this one can make
 * it genuinely mixed) and {@link Qubits#operatorOnQubit(MatrixComplex, int, int)} (to lift each
 * single-qubit Kraus operator into the full n-qubit register, the same technique {@link
 * TimeEvolution}/{@link BellTest} already use for unitaries/observables).
 */
public final class Decoherence {

	private final static String VERSION = "1.0 (2026_0813_2359)";

	private Decoherence() {}

	/**
	 * Applies a single-qubit quantum channel (given as its Kraus operators) to qubit {@code
	 * qubitIndex} of an {@code nQubits}-qubit density matrix -- {@code rho' = sum_k
	 * E_k*rho*E_k^dagger}, each {@code E_k} lifted to the full register via {@link
	 * Qubits#operatorOnQubit(MatrixComplex, int, int)} (identity on every other qubit, so the
	 * channel acts locally on {@code qubitIndex} alone). Trace-preserving whenever {@code kraus}
	 * satisfies the completeness relation {@code sum_k E_k^dagger*E_k = I} -- true by construction
	 * for every channel factory in this class (verified in {@code ScratchDecoherenceAudit01}), but
	 * not re-checked here on every call for a caller-supplied {@code kraus} array (would be
	 * redundant work on a hot path; the completeness relation is a one-time property of the
	 * channel, not of each state it's applied to).
	 * @param rho The {@code 2^nQubits x 2^nQubits} density matrix (e.g. from {@link
	 * DensityMatrix#of(MatrixComplex)}).
	 * @param kraus The channel's Kraus operators, each a 2x2 single-qubit matrix (e.g. from {@link
	 * #bitFlip(double)}).
	 * @param qubitIndex The 0-based index of the qubit the channel acts on.
	 * @param nQubits The total number of qubits {@code rho} represents.
	 * @return The {@code 2^nQubits x 2^nQubits} density matrix after the channel.
	 */
	public static MatrixComplex apply(MatrixComplex rho, MatrixComplex[] kraus, int qubitIndex, int nQubits) {
		MatrixComplex result = new MatrixComplex(rho.rows(), rho.cols());
		for (MatrixComplex e : kraus) {
			MatrixComplex lifted = Qubits.operatorOnQubit(e, qubitIndex, nQubits);
			result = result.plus(lifted.times(rho).times(lifted.adjoint()));
		}
		return result;
	}

	/**
	 * The bit-flip channel: with probability {@code p} applies {@link Qubits#pauliX()} (flips
	 * {@code |0>}/{@code |1>}), otherwise leaves the qubit untouched -- Kraus operators {@code
	 * {sqrt(1-p)*I, sqrt(p)*X}}.
	 * @param p The flip probability, in {@code [0,1]}.
	 * @return The 2 Kraus operators of the channel.
	 * @throws IllegalArgumentException if {@code p} is outside {@code [0,1]}.
	 */
	public static MatrixComplex[] bitFlip(double p) {
		return twoOperatorChannel(p, Qubits.pauliX());
	}

	/**
	 * The phase-flip channel: with probability {@code p} applies {@link Qubits#pauliZ()} (flips the
	 * relative phase between {@code |0>}/{@code |1>}, leaving populations untouched), otherwise
	 * leaves the qubit untouched -- Kraus operators {@code {sqrt(1-p)*I, sqrt(p)*Z}}. The purest
	 * form of decoherence: destroys coherence (off-diagonal density-matrix terms in the {@code
	 * Z} basis) without any energy exchange.
	 * @param p The flip probability, in {@code [0,1]}.
	 * @return The 2 Kraus operators of the channel.
	 * @throws IllegalArgumentException if {@code p} is outside {@code [0,1]}.
	 */
	public static MatrixComplex[] phaseFlip(double p) {
		return twoOperatorChannel(p, Qubits.pauliZ());
	}

	/**
	 * The bit-phase-flip channel: with probability {@code p} applies {@link Qubits#pauliY()},
	 * otherwise leaves the qubit untouched -- Kraus operators {@code {sqrt(1-p)*I, sqrt(p)*Y}}.
	 * @param p The flip probability, in {@code [0,1]}.
	 * @return The 2 Kraus operators of the channel.
	 * @throws IllegalArgumentException if {@code p} is outside {@code [0,1]}.
	 */
	public static MatrixComplex[] bitPhaseFlip(double p) {
		return twoOperatorChannel(p, Qubits.pauliY());
	}

	private static MatrixComplex[] twoOperatorChannel(double p, MatrixComplex flipOperator) {
		if (p < 0.0 || p > 1.0) {
			throw new IllegalArgumentException("probability p must be in [0,1], got " + p);
		}
		return new MatrixComplex[] { Qubits.identity2().times(Math.sqrt(1.0 - p)), flipOperator.times(Math.sqrt(p)) };
	}

	/**
	 * The depolarizing channel: {@code rho -> (1-p)*rho + (p/4)*(X*rho*X + Y*rho*Y + Z*rho*Z)} --
	 * with probability {@code p} the qubit is replaced by the maximally mixed state {@code I/2}
	 * (using the Pauli identity {@code X*rho*X+Y*rho*Y+Z*rho*Z = 2*Tr(rho)*I - rho} for a 2x2
	 * {@code rho}, this expression equals {@code (1-p)*rho + p*I/2} whenever {@code Tr(rho)=1}),
	 * otherwise left untouched. Kraus operators {@code {sqrt(1-3p/4)*I, sqrt(p/4)*X, sqrt(p/4)*Y,
	 * sqrt(p/4)*Z}} (Nielsen &amp; Chuang's standard single-qubit form).
	 * @param p The depolarizing probability, in {@code [0,1]} (at {@code p=1} the qubit becomes
	 * exactly {@code I/2}, the maximally mixed state -- verified in {@code
	 * ScratchDecoherenceAudit01} for several input states, not just algebraically).
	 * @return The 4 Kraus operators of the channel.
	 * @throws IllegalArgumentException if {@code p} is outside {@code [0,1]}.
	 */
	public static MatrixComplex[] depolarizing(double p) {
		if (p < 0.0 || p > 1.0) {
			throw new IllegalArgumentException("probability p must be in [0,1], got " + p);
		}
		return new MatrixComplex[] {
				Qubits.identity2().times(Math.sqrt(1.0 - 0.75 * p)),
				Qubits.pauliX().times(Math.sqrt(p / 4.0)),
				Qubits.pauliY().times(Math.sqrt(p / 4.0)),
				Qubits.pauliZ().times(Math.sqrt(p / 4.0)),
		};
	}

	/**
	 * The amplitude-damping channel: models spontaneous energy loss (relaxation from the excited
	 * state {@code |1>} to the ground state {@code |0>}, e.g. spontaneous photon emission) with
	 * decay probability {@code gamma}. Kraus operators {@code E0=[[1,0],[0,sqrt(1-gamma)]]}
	 * (survives without decaying), {@code E1=[[0,sqrt(gamma)],[0,0]]} (decays {@code |1>->|0>}) --
	 * unlike {@link #bitFlip}/{@link #phaseFlip}/{@link #depolarizing}, NOT symmetric under
	 * {@code |0><->|1>}: {@code |0>} is a fixed point ({@code E0|0>=|0>}, {@code E1|0>=0}), only
	 * {@code |1>} decays.
	 * @param gamma The decay probability, in {@code [0,1]} (at {@code gamma=1}, {@code |1><1|}
	 * decays to exactly {@code |0><0|} -- verified in {@code ScratchDecoherenceAudit01}).
	 * @return The 2 Kraus operators of the channel.
	 * @throws IllegalArgumentException if {@code gamma} is outside {@code [0,1]}.
	 */
	public static MatrixComplex[] amplitudeDamping(double gamma) {
		if (gamma < 0.0 || gamma > 1.0) {
			throw new IllegalArgumentException("gamma must be in [0,1], got " + gamma);
		}
		MatrixComplex e0 = new MatrixComplex(2, 2);
		e0.setItem(0, 0, 1.0); e0.setItem(0, 1, 0.0);
		e0.setItem(1, 0, 0.0); e0.setItem(1, 1, Math.sqrt(1.0 - gamma));

		MatrixComplex e1 = new MatrixComplex(2, 2);
		e1.setItem(0, 0, 0.0); e1.setItem(0, 1, Math.sqrt(gamma));
		e1.setItem(1, 0, 0.0); e1.setItem(1, 1, 0.0);

		return new MatrixComplex[] { e0, e1 };
	}
}
