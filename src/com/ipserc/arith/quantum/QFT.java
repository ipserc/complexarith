package com.ipserc.arith.quantum;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * The Quantum Fourier Transform (QFT): the {@code n}-qubit unitary {@code QFT|j> =
 * (1/sqrt(2^n)) * sum_k e^(2*pi*i*j*k / 2^n) |k>}, the quantum analogue of the classical DFT
 * (see {@code com.ipserc.arith.signal.Fourier}) over the computational basis.
 * <p>
 * Guided development (see {@code Claude/ComplexArithRev.md}, Trigesimoctava/Trigesimonovena
 * sesión) -- the user built {@link #circuit(int)} himself, staged from the trivial {@code n=1}
 * case (exactly {@link Qubits#hadamard()}) up through the 2-qubit case by hand (Hadamard + a
 * single controlled phase + a final SWAP) before generalizing to any {@code n}. {@link
 * #matrix(int)} is the literal formula, used only as the independent oracle {@link #circuit(int)}
 * is verified against -- entry-by-entry, {@code O(2^(2n))}, no gates involved, NOT how a real
 * quantum computer would compute it.
 */
public final class QFT {

	private final static String VERSION = "1.0 (2026_0816_1200)";
	/* VERSION Release Note
	 * 1.0 (2026_0816_1200)
	 * Primera version -- circuit()/matrix() extraidos del TestComplex/TestQuantum_QFT01.java del
	 * usuario (Etapas 1-4 del desarrollo guiado) a una clase permanente del paquete, mismo patron
	 * que DeutschJozsa/Grover/BernsteinVazirani. Sin cambio de comportamiento respecto al fichero
	 * de aprendizaje.
	 */

	private QFT() {}

	/**
	 * The QFT built from elementary gates ({@link Qubits#hadamard()}, controlled {@link
	 * Qubits#phaseGate(int)}, a final SWAP block), exactly as a real quantum computer would
	 * implement it: for each qubit {@code j} (0-based, MSB-first, same convention as {@link
	 * Qubits#ket(int...)}), a {@code hadamard()} followed by a controlled phase {@code
	 * R_(k-j+1)} for every later qubit {@code k>j} (control={@code k}, target={@code j}), then a
	 * SWAP block reversing the qubit order (SWAP of qubit {@code i} with qubit {@code n-1-i}, for
	 * {@code i<n/2} -- the middle qubit of an odd {@code n} is left untouched, it maps to
	 * itself). Each SWAP is built from 3 chained {@code controlledGate(pauliX(),...)}, the
	 * standard CNOT-CNOT-CNOT identity -- no dedicated SWAP gate exists in {@link Qubits}.
	 * @param n The number of qubits, must be at least 1.
	 * @return The {@code 2^n x 2^n} QFT unitary.
	 * @throws IllegalArgumentException if {@code n<1}.
	 */
	public static MatrixComplex circuit(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("circuit() needs at least 1 qubit, got n=" + n);
		}
		int dim = 1 << n;
		MatrixComplex result = MatrixComplex.eye(dim);

		for (int j = 0; j < n; j++) {
			result = Qubits.operatorOnQubit(Qubits.hadamard(), j, n).times(result);
			for (int k = j + 1; k < n; k++) {
				MatrixComplex cphase = Qubits.controlledGate(Qubits.phaseGate(k - j + 1), k, j, n);
				result = cphase.times(result);
			}
		}

		for (int i = 0; i < n / 2; i++) {
			int a = i, b = n - 1 - i;
			MatrixComplex swap = Qubits.controlledGate(Qubits.pauliX(), a, b, n)
					.times(Qubits.controlledGate(Qubits.pauliX(), b, a, n))
					.times(Qubits.controlledGate(Qubits.pauliX(), a, b, n));
			result = swap.times(result);
		}

		return result;
	}

	/**
	 * The QFT's literal defining formula, {@code M[k][j] = (1/sqrt(2^n)) * e^(2*pi*i*j*k / 2^n)}
	 * -- built entry-by-entry ({@code O(2^(2n))}), with no gates/circuit involved. Used as the
	 * independent oracle {@link #circuit(int)} is verified against, NOT as a practical way to
	 * compute the QFT (a real quantum computer applies {@code O(n^2)} elementary gates, never
	 * this {@code 2^n x 2^n} table directly).
	 * @param n The number of qubits, must be at least 1.
	 * @return The {@code 2^n x 2^n} QFT matrix.
	 * @throws IllegalArgumentException if {@code n<1}.
	 */
	public static MatrixComplex matrix(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("matrix() needs at least 1 qubit, got n=" + n);
		}
		int dim = 1 << n;
		double s = 1.0 / Math.sqrt(dim);
		MatrixComplex m = new MatrixComplex(dim, dim);

		for (int j = 0; j < dim; j++) {
			for (int k = 0; k < dim; k++) {
				double angle = 2.0 * Math.PI * j * k / dim;
				m.setItem(k, j, new Complex(s * Math.cos(angle), s * Math.sin(angle)));
			}
		}
		return m;
	}

}
