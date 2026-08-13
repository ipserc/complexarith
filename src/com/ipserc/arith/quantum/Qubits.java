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

	private final static String VERSION = "1.0 (2026_0813_1606)";

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
}
