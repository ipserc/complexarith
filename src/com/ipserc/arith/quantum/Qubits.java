package com.ipserc.arith.quantum;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Factory of qubit kets, Pauli operators and canonical entangled states, as plain {@link
 * MatrixComplex} column vectors/matrices -- same static-utility-class pattern already used
 * elsewhere in this project (e.g. {@code ComplexFunctions}, {@code Sigfunc}).
 * <p>
 * First exercise of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoquinta sesión): a qubit state is nothing more than a complex column vector, a
 * measurement operator nothing more than a Hermitian complex matrix -- exactly what {@link
 * MatrixComplex} already represents. No new linear algebra was needed: {@link
 * MatrixComplex#kroneckerprod(MatrixComplex)} (tensor product of Hilbert spaces), {@link
 * MatrixComplex#adjoint()} (bra from ket) and {@link MatrixComplex#normalizeByCols()} (Euclidean
 * norm of a column vector -- NOT {@code normalize()}/{@code normalizeByRows()}, which would
 * normalize each single-element row independently, wrong for a state vector) were already public
 * and stable.
 */
public final class Qubits {

	private final static String VERSION = "1.3 (2026_0814_1600)";
	/* VERSION Release Note
	 * 1.3 (2026_0814_1600)
	 * bra() -- envoltorio de 1 linea sobre adjoint() (a peticion del usuario, para dar nombre de
	 * Dirac explicito al bra en vez de dejarlo implicito como ".adjoint()" en cada punto de uso).
	 * Sin cambio de comportamiento: sigue siendo el mismo MatrixComplex.adjoint() de siempre.
	 */

	private Qubits() {}

	/** The computational basis state {@code |0>}, as a {@code MatrixComplex} 2x1 column vector. */
	public static MatrixComplex ket0() {
		MatrixComplex ket = new MatrixComplex(2, 1);
		ket.setItem(0, 0, 1.0);
		ket.setItem(1, 0, 0.0);
		return ket;
	}

	/** The computational basis state {@code |1>}, as a {@code MatrixComplex} 2x1 column vector. */
	public static MatrixComplex ket1() {
		MatrixComplex ket = new MatrixComplex(2, 1);
		ket.setItem(0, 0, 0.0);
		ket.setItem(1, 0, 1.0);
		return ket;
	}

	/**
	 * The bra {@code <psi|} corresponding to a ket {@code |psi>} -- {@code ket.adjoint()} (conjugate
	 * transpose: a ket's bra is not a distinct kind of object in this project's representation, just
	 * the same {@link MatrixComplex} viewed as a row instead of a column, entries complex-conjugated).
	 * A thin, deliberately trivial wrapper (no behavior beyond {@link MatrixComplex#adjoint()}) --
	 * exists purely so call sites that mean "the bra of this ket" can say so directly ({@code
	 * Qubits.bra(psi)}) instead of the generic, unlabeled {@code psi.adjoint()}, which reads
	 * identically whether the operand is a ket (a bra is the physically meaningful reading) or an
	 * operator (where {@code .adjoint()} means its Hermitian conjugate/dagger, a different concept --
	 * {@code U.adjoint()} is NOT "the bra of U"). Use this only on kets; keep using {@code
	 * op.adjoint()} directly for operators.
	 * @param ket A ket, as a {@code MatrixComplex} column vector.
	 * @return The corresponding bra, as a {@code MatrixComplex} row vector.
	 */
	public static MatrixComplex bra(MatrixComplex ket) {
		return ket.adjoint();
	}

	/** The 2x2 identity operator. */
	public static MatrixComplex identity2() {
		MatrixComplex m = new MatrixComplex(2, 2);
		m.setItem(0, 0, 1.0); m.setItem(0, 1, 0.0);
		m.setItem(1, 0, 0.0); m.setItem(1, 1, 1.0);
		return m;
	}

	/** The Pauli-X (bit-flip) operator, {@code [[0,1],[1,0]]}. */
	public static MatrixComplex pauliX() {
		MatrixComplex m = new MatrixComplex(2, 2);
		m.setItem(0, 0, 0.0); m.setItem(0, 1, 1.0);
		m.setItem(1, 0, 1.0); m.setItem(1, 1, 0.0);
		return m;
	}

	/** The Pauli-Y operator, {@code [[0,-i],[i,0]]}. */
	public static MatrixComplex pauliY() {
		MatrixComplex m = new MatrixComplex(2, 2);
		m.setItem(0, 0, Complex.ZERO);
		m.setItem(0, 1, new Complex(0.0, -1.0));
		m.setItem(1, 0, new Complex(0.0, 1.0));
		m.setItem(1, 1, Complex.ZERO);
		return m;
	}

	/** The Pauli-Z (phase-flip) operator, {@code [[1,0],[0,-1]]}. */
	public static MatrixComplex pauliZ() {
		MatrixComplex m = new MatrixComplex(2, 2);
		m.setItem(0, 0, 1.0); m.setItem(0, 1, 0.0);
		m.setItem(1, 0, 0.0); m.setItem(1, 1, -1.0);
		return m;
	}

	/**
	 * The Hadamard gate, {@code (1/sqrt2)*[[1,1],[1,-1]]} -- maps {@code |0>}/{@code |1>} to the
	 * equal-superposition states {@code |+>}/{@code |->}, the standard gate used to prepare a
	 * qubit for measurement in the X basis (e.g. the Bell-basis measurement of quantum
	 * teleportation, see {@code Teleportation}).
	 */
	public static MatrixComplex hadamard() {
		double s = 1.0 / Math.sqrt(2.0);
		MatrixComplex m = new MatrixComplex(2, 2);
		m.setItem(0, 0, s); m.setItem(0, 1, s);
		m.setItem(1, 0, s); m.setItem(1, 1, -s);
		return m;
	}

	/**
	 * The Bell state {@code |Phi+> = (|00> + |11>) / sqrt(2)} -- the maximally entangled 2-qubit
	 * state used by the canonical CHSH experiment. Built directly from {@link #ket0()}/{@link
	 * #ket1()} via {@link MatrixComplex#kroneckerprod(MatrixComplex)}, then normalized as a single
	 * 4-component column vector ({@link MatrixComplex#normalizeByCols()}, not {@code normalize()}).
	 */
	public static MatrixComplex bellPhiPlus() {
		MatrixComplex zeroZero = ket0().kroneckerprod(ket0());
		MatrixComplex oneOne = ket1().kroneckerprod(ket1());
		return zeroZero.plus(oneOne).normalizeByCols();
	}

	/**
	 * The spin/polarization measurement operator along angle {@code theta} in the X-Z plane,
	 * {@code A(theta) = cos(theta)*Z + sin(theta)*X} -- the standard operator used in Bell-test
	 * experiments (measuring in a basis rotated by {@code theta} instead of the fixed Z basis).
	 * Hermitian by construction (real linear combination of Hermitian Pauli matrices) with
	 * eigenvalues {@code +-1} (verified analytically: {@code A(theta)^2 = I}, since {@code Z^2 =
	 * X^2 = I} and {@code ZX+XZ = 0}).
	 * @param theta The measurement angle, in radians.
	 * @return The 2x2 Hermitian measurement operator.
	 */
	public static MatrixComplex spinOperator(double theta) {
		return pauliZ().times(Math.cos(theta)).plus(pauliX().times(Math.sin(theta)));
	}

	/**
	 * The computational-basis state {@code |b1 b2 ... bn>} of {@code n=bits.length} qubits, as a
	 * {@code 2^n x 1} column vector -- built by encoding each {@code bits[i]} as {@link #ket0()}/
	 * {@link #ket1()} and chaining {@link MatrixComplex#kroneckerprod(MatrixComplex)} left to
	 * right, the same construction {@link #bellPhiPlus()} already used by hand for the fixed 2-qubit
	 * case.
	 * @param bits The classical bit string, one entry per qubit, each {@code 0} or {@code 1}, MSB
	 * (qubit 1) first. Must have at least 1 entry.
	 * @return The {@code 2^bits.length x 1} basis ket.
	 * @throws IllegalArgumentException if {@code bits} is empty or contains a value other than
	 * {@code 0}/{@code 1}.
	 */
	public static MatrixComplex ket(int... bits) {
		if (bits.length == 0) {
			throw new IllegalArgumentException("ket() needs at least 1 bit");
		}
		MatrixComplex state = singleBitKet(bits[0]);
		for (int i = 1; i < bits.length; ++i) {
			state = state.kroneckerprod(singleBitKet(bits[i]));
		}
		return state;
	}

	private static MatrixComplex singleBitKet(int bit) {
		if (bit == 0) { return ket0(); }
		if (bit == 1) { return ket1(); }
		throw new IllegalArgumentException("ket() bits must be 0 or 1, got " + bit);
	}

	/**
	 * The {@code n}-qubit GHZ (Greenberger-Horne-Zeilinger) state {@code (|00...0> + |11...1>) /
	 * sqrt(2)} -- the natural generalization of {@link #bellPhiPlus()} (its {@code n=2} case) to
	 * more than 2 qubits: still maximally entangled (not separable into any tensor product of
	 * per-qubit states), with perfectly correlated all-0/all-1 outcomes under a computational-basis
	 * measurement.
	 * @param n Number of qubits, must be at least 2 (an entangled state needs at least 2 subsystems).
	 * @return The {@code 2^n x 1} normalized GHZ state.
	 * @throws IllegalArgumentException if {@code n<2}.
	 */
	public static MatrixComplex ghz(int n) {
		if (n < 2) {
			throw new IllegalArgumentException("ghz() needs at least 2 qubits, got " + n);
		}
		int[] allZeros = new int[n];
		int[] allOnes = new int[n];
		java.util.Arrays.fill(allOnes, 1);
		return ket(allZeros).plus(ket(allOnes)).normalizeByCols();
	}

	/**
	 * A single-qubit operator lifted to act on qubit {@code qubitIndex} of an {@code nQubits}-qubit
	 * system, leaving every other qubit untouched -- {@code I (x) ... (x) op (x) ... (x) I}, {@code
	 * op} at position {@code qubitIndex} (0-based, MSB first, matching {@link #ket(int...)}'s bit
	 * order) and {@link #identity2()} everywhere else, chained left to right with {@link
	 * MatrixComplex#kroneckerprod(MatrixComplex)}. Needed because {@link MatrixComplex#times(MatrixComplex)}
	 * on a {@code 2^n x 2^n} state only accepts an operator of matching dimension -- this is how a
	 * local single-qubit gate/measurement is embedded into the full Hilbert space of an n-qubit
	 * register.
	 * @param op The 2x2 single-qubit operator (e.g. {@link #pauliZ()}, {@link #spinOperator(double)}).
	 * @param qubitIndex The 0-based index of the qubit {@code op} acts on, {@code 0<=qubitIndex<nQubits}.
	 * @param nQubits The total number of qubits in the register, must be at least 1.
	 * @return The {@code 2^nQubits x 2^nQubits} lifted operator.
	 * @throws IllegalArgumentException if {@code nQubits<1} or {@code qubitIndex} is out of range.
	 */
	public static MatrixComplex operatorOnQubit(MatrixComplex op, int qubitIndex, int nQubits) {
		if (nQubits < 1) {
			throw new IllegalArgumentException("operatorOnQubit() needs at least 1 qubit, got " + nQubits);
		}
		if (qubitIndex < 0 || qubitIndex >= nQubits) {
			throw new IllegalArgumentException("qubitIndex=" + qubitIndex + " out of range for nQubits=" + nQubits);
		}
		MatrixComplex result = (qubitIndex == 0) ? op : identity2();
		for (int i = 1; i < nQubits; ++i) {
			result = result.kroneckerprod((i == qubitIndex) ? op : identity2());
		}
		return result;
	}

	/**
	 * A controlled two-qubit gate lifted to act on qubits {@code controlIndex}/{@code targetIndex}
	 * of an {@code nQubits}-qubit system, leaving every other qubit untouched -- {@code
	 * |0><0|_control (x) I_target (x) rest + |1><1|_control (x) op_target (x) rest}, the standard
	 * "apply {@code op} to the target iff the control is {@code |1>}" construction (e.g. {@code
	 * CNOT = controlledGate(pauliX(),control,target,nQubits)}). {@code controlIndex}/{@code
	 * targetIndex} need not be adjacent -- each of the 2 terms is built the same way {@link
	 * #operatorOnQubit(MatrixComplex, int, int)} builds a single-site operator, just with 2
	 * non-identity sites instead of 1.
	 * @param op The 2x2 single-qubit operator applied to the target when the control is {@code |1>}
	 * (e.g. {@link #pauliX()} for {@code CNOT}).
	 * @param controlIndex The 0-based index of the control qubit.
	 * @param targetIndex The 0-based index of the target qubit, must differ from {@code controlIndex}.
	 * @param nQubits The total number of qubits in the register, must be at least 2.
	 * @return The {@code 2^nQubits x 2^nQubits} lifted controlled operator.
	 * @throws IllegalArgumentException if {@code nQubits<2}, either index is out of range, or
	 * {@code controlIndex==targetIndex}.
	 */
	public static MatrixComplex controlledGate(MatrixComplex op, int controlIndex, int targetIndex, int nQubits) {
		if (nQubits < 2) {
			throw new IllegalArgumentException("controlledGate() needs at least 2 qubits, got " + nQubits);
		}
		if (controlIndex < 0 || controlIndex >= nQubits || targetIndex < 0 || targetIndex >= nQubits) {
			throw new IllegalArgumentException("controlIndex=" + controlIndex + "/targetIndex=" + targetIndex
					+ " out of range for nQubits=" + nQubits);
		}
		if (controlIndex == targetIndex) {
			throw new IllegalArgumentException("controlIndex and targetIndex must differ, both were " + controlIndex);
		}
		MatrixComplex proj0 = ket0().times(bra(ket0()));
		MatrixComplex proj1 = ket1().times(bra(ket1()));
		return twoSiteOperator(proj0, identity2(), controlIndex, targetIndex, nQubits)
				.plus(twoSiteOperator(proj1, op, controlIndex, targetIndex, nQubits));
	}

	/**
	 * Builds {@code opA (x) opB (x) I (x) ... (x) I}, chained left to right, with {@code opA} at
	 * position {@code indexA}, {@code opB} at position {@code indexB} and {@link #identity2()}
	 * everywhere else -- the 2-site generalization of the single-site chain in {@link
	 * #operatorOnQubit(MatrixComplex, int, int)}, used by {@link #controlledGate(MatrixComplex,
	 * int, int, int)}.
	 */
	private static MatrixComplex twoSiteOperator(MatrixComplex opA, MatrixComplex opB, int indexA, int indexB, int nQubits) {
		MatrixComplex site0 = (indexA == 0) ? opA : (indexB == 0) ? opB : identity2();
		MatrixComplex result = site0;
		for (int i = 1; i < nQubits; ++i) {
			MatrixComplex site = (i == indexA) ? opA : (i == indexB) ? opB : identity2();
			result = result.kroneckerprod(site);
		}
		return result;
	}
}
