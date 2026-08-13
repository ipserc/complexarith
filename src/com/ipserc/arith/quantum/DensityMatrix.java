package com.ipserc.arith.quantum;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Density-matrix formalism for multi-qubit states: {@code rho=|psi><psi|} for a pure state,
 * partial trace to obtain the reduced density matrix of a subsystem, and the von Neumann entropy
 * {@code S(rho)=-Tr(rho log2 rho)} used to quantify entanglement of a subsystem with the rest.
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoquinta/Trigesimosexta sesión) -- the second catalogued gap from the first exercise
 * ({@link BellTest}), after the n-qubit generalization in {@link Qubits}.
 */
public final class DensityMatrix {

	private final static String VERSION = "1.0 (2026_0813_1900)";

	private DensityMatrix() {}

	/**
	 * The density matrix {@code rho=|state><state|} of a pure state -- {@code
	 * state.times(state.adjoint())}, a rank-1 projector.
	 * @param state A normalized {@code 2^n x 1} column-vector state (e.g. {@link Qubits#ket(int...)},
	 * {@link Qubits#bellPhiPlus()}, {@link Qubits#ghz(int)}).
	 * @return The {@code 2^n x 2^n} density matrix.
	 */
	public static MatrixComplex of(MatrixComplex state) {
		return state.times(state.adjoint());
	}

	/**
	 * The reduced density matrix obtained by tracing out one or more qubits of an {@code
	 * nQubits}-qubit system -- {@code rho_keep = Tr_traceOutQubits(rho)}. For each pair of
	 * surviving basis states {@code (i,j)} of the kept qubits, sums {@code rho[(i,k),(j,k)]} over
	 * every basis state {@code k} of the traced-out qubits (the diagonal-in-the-traced-subsystem
	 * sum that defines a partial trace), using the same bit ordering as {@link Qubits#ket(int...)}
	 * (qubit 0 is the MSB of the {@code 2^n}-dimensional index).
	 * @param rho The {@code 2^nQubits x 2^nQubits} density matrix of the full system.
	 * @param nQubits The number of qubits {@code rho} represents.
	 * @param traceOutQubits The 0-based indices of the qubits to trace out, each in {@code
	 * [0,nQubits)}, no duplicates, at least 1 and at most {@code nQubits}.
	 * @return The {@code 2^m x 2^m} reduced density matrix, {@code m=nQubits-traceOutQubits.length}
	 * (a {@code 1x1} matrix -- the full trace -- if every qubit is traced out).
	 * @throws IllegalArgumentException if {@code rho} isn't {@code 2^nQubits x 2^nQubits}, or
	 * {@code traceOutQubits} is empty, out of range, or has duplicates.
	 */
	public static MatrixComplex partialTrace(MatrixComplex rho, int nQubits, int... traceOutQubits) {
		int fullDim = 1 << nQubits;
		if (nQubits < 1 || rho.rows() != fullDim || rho.cols() != fullDim) {
			throw new IllegalArgumentException("partialTrace() expects a " + fullDim + "x" + fullDim
					+ " density matrix for nQubits=" + nQubits + ", got " + rho.rows() + "x" + rho.cols());
		}
		if (traceOutQubits.length == 0 || traceOutQubits.length > nQubits) {
			throw new IllegalArgumentException("traceOutQubits must have between 1 and " + nQubits
					+ " entries, got " + traceOutQubits.length);
		}
		boolean[] isTracedOut = new boolean[nQubits];
		for (int q : traceOutQubits) {
			if (q < 0 || q >= nQubits) {
				throw new IllegalArgumentException("traceOutQubits index " + q + " out of range for nQubits=" + nQubits);
			}
			if (isTracedOut[q]) {
				throw new IllegalArgumentException("traceOutQubits has a duplicate index: " + q);
			}
			isTracedOut[q] = true;
		}

		int[] keepQubits = new int[nQubits - traceOutQubits.length];
		int k = 0;
		for (int q = 0; q < nQubits; ++q) {
			if (!isTracedOut[q]) { keepQubits[k++] = q; }
		}
		int m = keepQubits.length;
		int t = traceOutQubits.length;
		int reducedDim = 1 << m;

		MatrixComplex result = new MatrixComplex(reducedDim, reducedDim);
		for (int rowKeep = 0; rowKeep < reducedDim; ++rowKeep) {
			int[] rowKeepBits = bitsOf(rowKeep, m);
			for (int colKeep = 0; colKeep < reducedDim; ++colKeep) {
				int[] colKeepBits = bitsOf(colKeep, m);
				Complex accumulator = new Complex(0.0, 0.0);
				for (int trace = 0; trace < (1 << t); ++trace) {
					int[] traceBits = bitsOf(trace, t);
					int[] fullRowBits = new int[nQubits];
					int[] fullColBits = new int[nQubits];
					for (int i = 0; i < m; ++i) {
						fullRowBits[keepQubits[i]] = rowKeepBits[i];
						fullColBits[keepQubits[i]] = colKeepBits[i];
					}
					for (int i = 0; i < t; ++i) {
						fullRowBits[traceOutQubits[i]] = traceBits[i];
						fullColBits[traceOutQubits[i]] = traceBits[i];
					}
					accumulator = accumulator.plus(rho.getItem(indexOf(fullRowBits), indexOf(fullColBits)));
				}
				result.setItem(rowKeep, colKeep, accumulator);
			}
		}
		return result;
	}

	private static int[] bitsOf(int value, int width) {
		int[] bits = new int[width];
		for (int j = 0; j < width; ++j) {
			bits[j] = (value >> (width - 1 - j)) & 1;
		}
		return bits;
	}

	private static int indexOf(int[] bits) {
		int idx = 0;
		for (int bit : bits) {
			idx = (idx << 1) | bit;
		}
		return idx;
	}

	/**
	 * The von Neumann entropy {@code S(rho)=-Tr(rho log2 rho)=-sum(lambda_i*log2(lambda_i))} over
	 * the eigenvalues {@code lambda_i} of {@code rho} (each counted with its algebraic multiplicity,
	 * via {@link Eigenspace#roots()}) -- the standard measure of how mixed {@code rho} is: {@code 0}
	 * for a pure state (rank-1 projector, one eigenvalue {@code =1}, the rest {@code =0}), up to
	 * {@code log2(dim)} for the maximally mixed state. Applied to a reduced density matrix from
	 * {@link #partialTrace(MatrixComplex, int, int...)}, a nonzero value is exactly the standard
	 * entanglement-entropy witness: a pure global state whose reduced subsystem is mixed is
	 * entangled with the rest.
	 * @param rho A Hermitian density matrix (trace {@code 1}, eigenvalues real and non-negative,
	 * e.g. from {@link #of(MatrixComplex)} or {@link #partialTrace(MatrixComplex, int, int...)}).
	 * @return The entropy in bits, always {@code >=0}.
	 */
	public static double vonNeumannEntropy(MatrixComplex rho) {
		MatrixComplex eigVals = new Eigenspace(rho).roots();
		double entropy = 0.0;
		for (int i = 0; i < eigVals.rows(); ++i) {
			double p = eigVals.getItem(i, 0).rep();
			if (p > 1e-10) {
				entropy -= p * (Math.log(p) / Math.log(2.0));
			}
		}
		return entropy;
	}
}
