package com.ipserc.arith.quantum;

import java.util.Random;

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

	private final static String VERSION = "1.10 (2026_0927_1400)";
	/* VERSION Release Note
	 * 1.10 (2026_0927_1400)
	 * qubitLabel(state) -- despacha a ketLabel()/braLabel() segun la forma de state (columna vs
	 * fila), para puntos de uso que manejan kets y bras indistintamente sin tener que saber cual
	 * es cual de antemano. Lanza ante el caso degenerado 1x1 (ambiguo) o cualquier forma que no sea
	 * columna/fila. A peticion del usuario.
	 * 1.9 (2026_0927_1300)
	 * braLabel(bra) -- espejo de ketLabel() para bras: localiza la columna de amplitud 1 y la
	 * formatea como "<...|" en vez de "|...>". zeroPaddedBinary(index,n,caller) extraido de
	 * basisLabel(index,n) como helper privado compartido (mensajes de error con el nombre del
	 * metodo llamador), sin cambio de comportamiento en basisLabel(). A peticion del usuario.
	 * 1.8 (2026_0927_1200)
	 * basisLabel(index,n) -- string de Dirac de un estado de la base computacional ("|00>",
	 * "|01>"...), inverso de ket(int... bits): representacion binaria de index, MSB primero,
	 * rellenada con ceros a la izquierda hasta n digitos. basisLabel(state,index) -- mismo calculo
	 * tomando n de state.rows() (delega en la version de arriba), comodo en los puntos de uso de
	 * measure()/measure(shots). ketLabel(ket) -- version a nivel de vector (no de indice): localiza
	 * la fila de amplitud 1 y delega en basisLabel(); llevada a la factoria desde
	 * TestQubitMeasure01 a peticion del usuario. Todo a peticion del usuario.
	 * 1.7 (2026_0926_1200)
	 * measure(state,random) -- primera utilidad de medicion ESTOCASTICA del proyecto (todo lo
	 * anterior en este fichero es exacto: estados y operadores, sin muestreo). Colapsa un ket de
	 * n qubits a un unico estado base de la base computacional, con probabilidad |amplitud|^2
	 * (regla de Born), muestreado contra un Random externo (reproducible en tests). measure(state,
	 * shots,random) -- histograma de "shots" medidas independientes, delegando en la version de 1
	 * disparo (sin duplicar la logica de muestreo). A peticion del usuario, llevado desde un test
	 * suelto (TestQubitMeasure01) a la factoria.
	 * 1.6 (2026_0831_1200)
	 * ket(MatrixComplex bra) -- inverso de bra(MatrixComplex ket), a peticion del usuario. Mismo
	 * envoltorio de 1 linea sobre adjoint() (involutivo), sin cambio de comportamiento en nada mas.
	 * 1.5 (2026_0825_1700)
	 * controlledBlockGate(op,control,blockStart,blockSize,nQubits) -- generaliza controlledGate a
	 * un operador sobre un BLOQUE contiguo de qubits (no solo 1), necesario para Shor (registro de
	 * trabajo de m qubits controlado por 1 qubit de conteo). controlledGate() ahora delega en ella
	 * (blockSize=1) en vez de duplicar la construccion -- sin cambio de comportamiento.
	 * 1.4 (2026_0815_1700)
	 * phaseGate(k) -- puerta de fase R_k = diag(1, e^(2*pi*i/2^k)), primera pieza de la QFT
	 * (Etapa 1 del plan guiado por el usuario). Falla alto ante k<0.
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

	/**
	 * The ket {@code |psi>} corresponding to a bra {@code <psi|} -- the inverse of {@link
	 * #bra(MatrixComplex)}. Since {@code adjoint()} is involutive ({@code (A}<sup>&#8224;</sup>
	 * {@code )}<sup>&#8224;</sup>{@code  = A} exactly, entry by entry), this is the same one-line
	 * wrapper as {@code bra()}, just named for the opposite direction of use: call sites that hold a
	 * bra and want its ket back can say so directly ({@code Qubits.ket(phi)}) instead of the
	 * unlabeled {@code phi.adjoint()}. Use this only on bras (row vectors); for operators, {@code
	 * op.adjoint()} means the Hermitian conjugate/dagger, a different concept.
	 * @param bra A bra, as a {@code MatrixComplex} row vector.
	 * @return The corresponding ket, as a {@code MatrixComplex} column vector.
	 */
	public static MatrixComplex ket(MatrixComplex bra) {
		return bra.adjoint();
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
	 * The phase gate {@code R_k = diag(1, e^(2*pi*i / 2^k))} -- the controlled version of this
	 * gate ({@link #controlledGate(MatrixComplex, int, int, int)}) is the building block of the
	 * Quantum Fourier Transform's controlled-phase rotations.
	 * @param k The phase gate's index ({@code k=1 -> R_1=Z}, {@code k=2 -> R_2=S}, etc.). Must be
	 * {@code >= 0}.
	 * @return The 2x2 phase gate.
	 * @throws IllegalArgumentException if {@code k < 0}.
	 */
	public static MatrixComplex phaseGate(int k) {
		if (k < 0) {
			throw new IllegalArgumentException("phaseGate index k must be >= 0, got k=" + k);
		}
		double angle = 2.0 * Math.PI / Math.pow(2, k);
		MatrixComplex m = new MatrixComplex(2, 2);
		m.setItem(0, 0, 1.0); m.setItem(0, 1, 0.0);
		m.setItem(1, 0, 0.0); m.setItem(1, 1, new Complex(Math.cos(angle), Math.sin(angle)));
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
	 * The Dirac ket label of computational-basis state {@code index} within an {@code n}-qubit
	 * register -- the inverse of {@link #ket(int...)}: {@code index}'s binary representation, MSB
	 * (qubit 1) first, zero-padded to {@code n} digits, e.g. {@code n=1}: {@code "|0>"}/{@code
	 * "|1>"}; {@code n=2}: {@code "|00>"}, {@code "|01>"}, {@code "|10>"}, {@code "|11>"}.
	 * @param index The 0-based basis state index, {@code 0 <= index < 2^n}.
	 * @param n The number of qubits, must be at least 1.
	 * @return The bracketed label, e.g. {@code "|01>"}.
	 * @throws IllegalArgumentException if {@code n<1}, or {@code index} is out of range.
	 */
	public static String basisLabel(int index, int n) {
		return "|" + zeroPaddedBinary(index, n, "basisLabel") + ">";
	}

	private static String zeroPaddedBinary(int index, int n, String caller) {
		if (n < 1) {
			throw new IllegalArgumentException(caller + "() needs at least 1 qubit, got n=" + n);
		}
		int nStates = 1 << n;
		if (index < 0 || index >= nStates) {
			throw new IllegalArgumentException(caller + "() index=" + index
					+ " out of range for n=" + n + " qubits (0.." + (nStates - 1) + ")");
		}
		StringBuilder bits = new StringBuilder(Integer.toBinaryString(index));
		while (bits.length() < n) {
			bits.insert(0, '0');
		}
		return bits.toString();
	}

	/**
	 * Same as {@link #basisLabel(int, int)}, taking the number of qubits from an existing ket
	 * instead of naming it explicitly -- convenient at a {@link #measure(MatrixComplex, int,
	 * Random)} call site, where {@code state} is already at hand.
	 * @param state An {@code n}-qubit ket, as a {@code 2^n x 1} column vector (only its row count
	 * is used).
	 * @param index The 0-based basis state index, {@code 0 <= index < state.rows()}.
	 * @return The bracketed label, e.g. {@code "|01>"}.
	 * @throws IllegalArgumentException if {@code state.rows()} is not a power of 2, or {@code
	 * index} is out of range.
	 */
	public static String basisLabel(MatrixComplex state, int index) {
		int nStates = state.rows();
		if ((nStates & (nStates - 1)) != 0) {
			throw new IllegalArgumentException("basisLabel() needs a power-of-2 number of basis states, got " + nStates);
		}
		return basisLabel(index, Integer.numberOfTrailingZeros(nStates));
	}

	/**
	 * The Dirac ket label of a computational-basis ket -- the inverse of {@link #ket(int...)} at
	 * the vector level (rather than the index level, like {@link #basisLabel(MatrixComplex, int)}):
	 * finds the single row with amplitude {@code 1} and delegates to it.
	 * @param ket A computational-basis ket, i.e. amplitude {@code 1} in exactly one row and {@code
	 * 0} elsewhere (e.g. {@link #ket0()}, {@link #ket1()}, or any {@link #ket(int...)}) -- NOT a
	 * superposition.
	 * @return The bracketed label, e.g. {@code "|101>"}.
	 * @throws IllegalArgumentException if no row of {@code ket} has amplitude {@code 1} (i.e.
	 * {@code ket} is a superposition, not a basis ket), or (from {@link #basisLabel(MatrixComplex,
	 * int)}) {@code ket.rows()} is not a power of 2.
	 */
	public static String ketLabel(MatrixComplex ket) {
		int index = -1;
		for (int row = 0; row < ket.rows(); ++row) {
			if (Math.abs(ket.getItem(row, 0).abs() - 1.0) < 1e-9) {
				index = row;
				break;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException("ketLabel() needs a computational basis ket (amplitude 1 in exactly one row), got a superposition");
		}
		return basisLabel(ket, index);
	}

	/**
	 * The Dirac bra label of a computational-basis bra -- the {@link #bra(MatrixComplex)} mirror
	 * of {@link #ketLabel(MatrixComplex)}: finds the single column with amplitude {@code 1} and
	 * formats it as {@code "<...|"} instead of {@code "|...>"}.
	 * @param bra A computational-basis bra, i.e. a {@code 1 x 2^n} row vector with amplitude
	 * {@code 1} in exactly one column and {@code 0} elsewhere (e.g. {@code Qubits.bra(ket0())}) --
	 * NOT a superposition.
	 * @return The bracketed label, e.g. {@code "<101|"}.
	 * @throws IllegalArgumentException if {@code bra} is not a row vector, its column count is not
	 * a power of 2, or no column has amplitude {@code 1} (i.e. {@code bra} is a superposition, not
	 * a basis bra).
	 */
	public static String braLabel(MatrixComplex bra) {
		if (bra.rows() != 1) {
			throw new IllegalArgumentException("braLabel() needs a row vector (a bra), got a "
					+ bra.rows() + "x" + bra.cols() + " matrix");
		}
		int nStates = bra.cols();
		if ((nStates & (nStates - 1)) != 0) {
			throw new IllegalArgumentException("braLabel() needs a power-of-2 number of basis states, got " + nStates);
		}
		int index = -1;
		for (int col = 0; col < nStates; ++col) {
			if (Math.abs(bra.getItem(0, col).abs() - 1.0) < 1e-9) {
				index = col;
				break;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException("braLabel() needs a computational basis bra (amplitude 1 in exactly one column), got a superposition");
		}
		return "<" + zeroPaddedBinary(index, Integer.numberOfTrailingZeros(nStates), "braLabel") + "|";
	}

	/**
	 * The Dirac label of a computational-basis ket OR bra, dispatching on its shape -- a column
	 * vector ({@code cols()==1}) goes to {@link #ketLabel(MatrixComplex)}, a row vector ({@code
	 * rows()==1}) goes to {@link #braLabel(MatrixComplex)}. Convenient at a call site handling
	 * both kets and bras generically, without the caller having to know which one it has.
	 * @param state A computational-basis ket or bra (not a superposition), as described by {@link
	 * #ketLabel(MatrixComplex)}/{@link #braLabel(MatrixComplex)}.
	 * @return The bracketed label, e.g. {@code "|101>"} for a ket or {@code "<101|"} for a bra.
	 * @throws IllegalArgumentException if {@code state} is neither a column nor a row vector (e.g.
	 * a general matrix, or the degenerate {@code 1x1} case, ambiguous between the two), or (from
	 * the delegated call) it is a superposition.
	 */
	public static String qubitLabel(MatrixComplex state) {
		if (state.cols() == 1 && state.rows() != 1) {
			return ketLabel(state);
		}
		if (state.rows() == 1 && state.cols() != 1) {
			return braLabel(state);
		}
		throw new IllegalArgumentException("qubitLabel() needs a ket (column vector) or bra (row vector), got a "
				+ state.rows() + "x" + state.cols() + " matrix");
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
		if (controlIndex == targetIndex) {
			throw new IllegalArgumentException("controlIndex and targetIndex must differ, both were " + controlIndex);
		}
		return controlledBlockGate(op, controlIndex, targetIndex, 1, nQubits);
	}

	/**
	 * A controlled gate lifted to act on a CONTIGUOUS block of {@code blockSize} qubits (starting
	 * at {@code blockStart}) of an {@code nQubits}-qubit system, controlled by a single qubit
	 * {@code controlIndex} OUTSIDE that block -- the multi-qubit generalization of {@link
	 * #controlledGate(MatrixComplex, int, int, int)} (which is exactly the {@code blockSize=1}
	 * case, and now delegates here). Needed for e.g. Shor's order-finding: a single counting qubit
	 * controls a unitary over an entire {@code m}-qubit work register, not just 1 target qubit.
	 * <p>
	 * {@code |0><0|_control (x) I_block (x) rest + |1><1|_control (x) op_block (x) rest}, same
	 * "project control, apply on |1>" construction as {@link #controlledGate}, built by a single
	 * Kronecker chain that steps by {@code blockSize} positions when it reaches {@code blockStart}
	 * (treating the whole block as the single {@code op}/{@link #identity2()}-of-matching-dimension
	 * factor at that position) and by {@code 1} position everywhere else, including at {@code
	 * controlIndex}.
	 * @param op The {@code 2^blockSize x 2^blockSize} operator applied to the block when the
	 * control is {@code |1>}.
	 * @param controlIndex The 0-based index of the control qubit, must lie outside the block.
	 * @param blockStart The 0-based index of the first qubit of the target block.
	 * @param blockSize The number of qubits in the target block, must be at least 1.
	 * @param nQubits The total number of qubits in the register.
	 * @throws IllegalArgumentException if {@code blockSize<1}, any index/range falls outside
	 * {@code [0,nQubits)}, or {@code controlIndex} falls inside {@code [blockStart,blockStart+blockSize)}.
	 */
	public static MatrixComplex controlledBlockGate(MatrixComplex op, int controlIndex, int blockStart, int blockSize, int nQubits) {
		if (blockSize < 1) {
			throw new IllegalArgumentException("controlledBlockGate() needs blockSize>=1, got " + blockSize);
		}
		if (controlIndex < 0 || controlIndex >= nQubits || blockStart < 0 || blockStart + blockSize > nQubits) {
			throw new IllegalArgumentException("controlIndex=" + controlIndex + "/block=[" + blockStart + ","
					+ (blockStart+blockSize) + ") out of range for nQubits=" + nQubits);
		}
		if (controlIndex >= blockStart && controlIndex < blockStart + blockSize) {
			throw new IllegalArgumentException("controlIndex=" + controlIndex + " must lie outside the block ["
					+ blockStart + "," + (blockStart+blockSize) + ")");
		}
		MatrixComplex proj0 = ket0().times(bra(ket0()));
		MatrixComplex proj1 = ket1().times(bra(ket1()));
		MatrixComplex identityBlock = MatrixComplex.eye(1 << blockSize);
		return chainWithControlAndBlock(proj0, controlIndex, identityBlock, blockStart, blockSize, nQubits)
				.plus(chainWithControlAndBlock(proj1, controlIndex, op, blockStart, blockSize, nQubits));
	}

	/**
	 * A single Kronecker chain over qubit positions {@code 0..nQubits-1}: {@code controlOp} at
	 * position {@code controlIndex} (1 qubit wide), {@code blockOp} at position {@code blockStart}
	 * ({@code blockSize} qubits wide, so the chain skips ahead by {@code blockSize} once it places
	 * it), {@link #identity2()} everywhere else -- the shared machinery behind {@link
	 * #controlledBlockGate}.
	 */
	private static MatrixComplex chainWithControlAndBlock(MatrixComplex controlOp, int controlIndex, MatrixComplex blockOp, int blockStart, int blockSize, int nQubits) {
		MatrixComplex result = null;
		int i = 0;
		while (i < nQubits) {
			MatrixComplex factor;
			int step;
			if (i == controlIndex) { factor = controlOp; step = 1; }
			else if (i == blockStart) { factor = blockOp; step = blockSize; }
			else { factor = identity2(); step = 1; }
			result = (result == null) ? factor : result.kroneckerprod(factor);
			i += step;
		}
		return result;
	}

	/**
	 * Simulates a single projective measurement of an {@code n}-qubit ket in the computational
	 * basis: collapses {@code state} to one of its {@code 2^n} basis states, chosen with
	 * probability {@code |amplitude|^2} (the Born rule), by sampling {@code random.nextDouble()}
	 * against the cumulative distribution of those probabilities. The only stochastic operation in
	 * this factory -- every other method here (states, gates, operators) is exact; this is where a
	 * ket actually becomes a classical outcome.
	 * @param state An {@code n}-qubit ket, as a {@code 2^n x 1} column vector (need not be
	 * separately re-normalized here: the cumulative probabilities are expected to sum to 1, as for
	 * any valid quantum state).
	 * @param random The source of randomness -- pass a seeded {@link Random} for reproducible tests.
	 * @return The 0-based index of the measured basis state, {@code 0 <= result < state.rows()}
	 * (e.g. for 1 qubit, {@code 0} means {@code |0>} was measured, {@code 1} means {@code |1>}).
	 * @throws IllegalArgumentException if {@code state} is not a column vector, or its row count is
	 * not a power of 2.
	 */
	public static int measure(MatrixComplex state, Random random) {
		if (state.cols() != 1) {
			throw new IllegalArgumentException("measure() needs a column vector (a ket), got a "
					+ state.rows() + "x" + state.cols() + " matrix");
		}
		int nStates = state.rows();
		if ((nStates & (nStates - 1)) != 0) {
			throw new IllegalArgumentException("measure() needs a power-of-2 number of basis states, got " + nStates);
		}
		double r = random.nextDouble();
		double cumulative = 0.0;
		for (int i = 0; i < nStates; ++i) {
			cumulative += Math.pow(state.getItem(i, 0).abs(), 2);
			if (r < cumulative) {
				return i;
			}
		}
		return nStates - 1;
	}

	/**
	 * Simulates {@code shots} independent measurements of {@code state} (each one collapsing a
	 * FRESH copy of the state -- no cumulative back-action between shots, exactly what running the
	 * same circuit {@code shots} times on real hardware means), returning a histogram of how many
	 * times each basis state was measured. A thin loop over {@link #measure(MatrixComplex, Random)}
	 * -- no separate sampling logic.
	 * @param state An {@code n}-qubit ket, as a {@code 2^n x 1} column vector.
	 * @param shots The number of independent measurements to simulate, must be at least 1.
	 * @param random The source of randomness -- pass a seeded {@link Random} for reproducible tests.
	 * @return An array of length {@code state.rows()}, {@code result[i]} = number of shots that
	 * measured basis state {@code i}, summing to {@code shots}.
	 * @throws IllegalArgumentException if {@code shots < 1}, or (from the underlying single-shot
	 * call) {@code state} is not a valid ket.
	 */
	public static int[] measure(MatrixComplex state, int shots, Random random) {
		if (shots < 1) {
			throw new IllegalArgumentException("measure() needs shots>=1, got " + shots);
		}
		int[] counts = new int[state.rows()];
		for (int i = 0; i < shots; ++i) {
			counts[measure(state, random)]++;
		}
		return counts;
	}
}
