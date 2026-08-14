package com.ipserc.arith.quantum;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Unitary time evolution under a time-independent Hamiltonian, {@code U(t)=exp(-i*H*t)} (the
 * solution of the Schrödinger equation {@code i*d|psi>/dt = H|psi>} for constant {@code H}) --
 * built directly on {@link MatrixComplex#exp()}, already implemented and connected in this project
 * (see {@code Claude/ComplexArithRev.md}, "Décima sesión"/"NOVENA SESIÓN").
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (Trigesimoquinta/Trigesimosexta sesión) --
 * candidate 1 of the roadmap catalogued at the close of the Trigesimosexta sesión.
 */
public final class TimeEvolution {

	private final static String VERSION = "1.1 (2026_0814_1600)";
	/* VERSION Release Note
	 * 1.1 (2026_0814_1600)
	 * expectationValue() usa Qubits.bra() en vez de state.adjoint() (legibilidad, a peticion del
	 * usuario). Sin cambio de comportamiento.
	 */

	private TimeEvolution() {}

	/**
	 * The time-evolution operator {@code U(t)=exp(-i*H*t)} for a time-independent Hamiltonian
	 * {@code H} -- unitary because {@code H} is Hermitian ({@code (-i*H*t)} is skew-Hermitian, and
	 * the exponential of a skew-Hermitian matrix is always unitary).
	 * @param hamiltonian The Hermitian Hamiltonian operator, {@code dim x dim}.
	 * @param t The elapsed time (any real unit consistent with {@code hamiltonian}'s energy unit,
	 * {@code hbar=1} convention).
	 * @return The {@code dim x dim} unitary evolution operator {@code U(t)}.
	 * @throws IllegalArgumentException if {@code hamiltonian} isn't Hermitian -- a Hamiltonian must
	 * be Hermitian for the evolution it generates to be unitary (probability-preserving), failing
	 * loud here catches a misuse of this API the same way {@link BellTest#correlation} does for
	 * non-Hermitian measurement operators.
	 */
	public static MatrixComplex unitary(MatrixComplex hamiltonian, double t) {
		if (!hamiltonian.isHermitian()) {
			throw new IllegalArgumentException("TimeEvolution.unitary() expects a Hermitian Hamiltonian "
					+ "(a real physical observable) -- got a non-Hermitian matrix");
		}
		return hamiltonian.times(new Complex(0.0, -t)).exp();
	}

	/**
	 * The state {@code |psi(t)> = U(t)|psi(0)>} obtained by evolving {@code state} under {@code
	 * hamiltonian} for time {@code t}.
	 * @param state The initial state, as a {@code dim x 1} column vector.
	 * @param hamiltonian The Hermitian Hamiltonian operator, {@code dim x dim}.
	 * @param t The elapsed time.
	 * @return The evolved state {@code |psi(t)>}, still normalized ({@code U(t)} is unitary).
	 */
	public static MatrixComplex evolve(MatrixComplex state, MatrixComplex hamiltonian, double t) {
		return unitary(hamiltonian, t).times(state);
	}

	/**
	 * The expectation value {@code <state|op|state>} of a Hermitian observable {@code op} on {@code
	 * state} -- the generic form of the expectation value {@link BellTest#correlation} already
	 * computed for the specific case of a tensor-product observable on a 2-qubit state.
	 * @param state The state, as a {@code dim x 1} column vector.
	 * @param op The Hermitian observable, {@code dim x dim}.
	 * @return The real-valued expectation value.
	 * @throws IllegalStateException if the result comes out with a non-negligible imaginary part --
	 * would mean {@code op} isn't actually Hermitian (a physical observable's expectation value must
	 * be real).
	 */
	public static double expectationValue(MatrixComplex state, MatrixComplex op) {
		Complex expectation = Qubits.bra(state).times(op).times(state).getItem(0, 0);
		if (!expectation.isPureReal()) {
			throw new IllegalStateException("expectation value <state|op|state> came out complex "
					+ "(Im=" + expectation.imp() + ") -- expected a real expectation value; check that op is Hermitian");
		}
		return expectation.rep();
	}
}
