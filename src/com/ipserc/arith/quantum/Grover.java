package com.ipserc.arith.quantum;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Grover's search algorithm (small-scale simulation): finds a single marked item {@code target}
 * among {@code N=2^n} unsorted items with {@code O(sqrt(N))} oracle queries, instead of the
 * {@code O(N)} a classical search needs in the worst case -- the canonical quadratic (not
 * exponential, unlike {@link DeutschJozsa}/{@link BernsteinVazirani}'s promise-problem speedup)
 * quantum advantage.
 * <p>
 * Two operators, repeated {@link #optimalIterations(int)} times: {@link #oracle(int, int)} (phase-
 * flips {@code |target>}, leaves every other basis state untouched) and {@link #diffusion(int)}
 * ("inversion about the mean", {@code 2|s><s|-I} for the uniform superposition {@code |s>},
 * implemented via the standard {@code H^(x)n * (2|0..0><0..0|-I) * H^(x)n} identity) -- together,
 * "amplitude amplification": each iteration rotates the state vector a fixed angle {@code
 * 2*theta} (with {@code theta=asin(1/sqrt(N))}) inside the 2-dimensional plane spanned by
 * {@code |target>} and the uniform superposition over everything else, growing the target's
 * measurement probability from {@code 1/N} toward {@code ~1} -- then SHRINKING it again past the
 * optimal iteration count ("over-rotation"), unlike {@link DeutschJozsa}/{@link
 * BernsteinVazirani}'s one-shot circuits.
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoctava sesión) -- third "algoritmo cuántico más grande" follow-up, after {@link
 * DeutschJozsa}/{@link BernsteinVazirani}; reuses {@link DeutschJozsa#hadamardChain(int)}
 * (package-visible) for the {@code H^(x)n} chains both the initial uniform state and the
 * diffusion operator need.
 */
public final class Grover {

	private final static String VERSION = "1.0 (2026_0813_2359)";

	private Grover() {}

	/**
	 * The phase-flip oracle {@code |x> -> -|x>} for {@code x=target}, {@code |x> -> |x>} for every
	 * other {@code x} -- a {@code 2^n x 2^n} diagonal unitary (its own inverse: 2 phase flips cancel).
	 * @param target The marked item's index, in {@code [0,2^n)}.
	 * @param n The number of qubits, must be at least 1.
	 * @return The {@code 2^n x 2^n} oracle unitary.
	 * @throws IllegalArgumentException if {@code n<1} or {@code target} is out of range.
	 */
	public static MatrixComplex oracle(int target, int n) {
		if (n < 1) {
			throw new IllegalArgumentException("oracle() needs at least 1 qubit, got n=" + n);
		}
		int dim = 1 << n;
		if (target < 0 || target >= dim) {
			throw new IllegalArgumentException("target=" + target + " out of range for n=" + n + " qubits");
		}
		MatrixComplex u = new MatrixComplex(dim, dim);
		for (int i = 0; i < dim; ++i) {
			u.setItem(i, i, (i == target) ? -1.0 : 1.0);
		}
		return u;
	}

	/**
	 * The diffusion operator ("inversion about the mean") {@code D = 2|s><s| - I}, {@code |s>} the
	 * uniform superposition over all {@code 2^n} basis states -- built via the standard identity
	 * {@code D = H^(x)n * (2|0..0><0..0| - I) * H^(x)n} (cheaper than forming {@code |s><s|}
	 * directly: {@code |0..0><0..0|} is a single {@code 1} entry, the rest of the work is 2
	 * {@code H^(x)n} multiplications this class already needs for the initial state).
	 * @param n The number of qubits, must be at least 1.
	 * @return The {@code 2^n x 2^n} diffusion unitary.
	 * @throws IllegalArgumentException if {@code n<1}.
	 */
	public static MatrixComplex diffusion(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("diffusion() needs at least 1 qubit, got n=" + n);
		}
		int dim = 1 << n;
		MatrixComplex hAll = DeutschJozsa.hadamardChain(n);
		MatrixComplex zeroProjector = new MatrixComplex(dim, dim);
		zeroProjector.setItem(0, 0, 1.0);
		MatrixComplex twoProjMinusI = zeroProjector.times(2.0).minus(MatrixComplex.eye(dim));
		return hAll.times(twoProjMinusI).times(hAll);
	}

	/**
	 * The uniform superposition {@code |s> = H^(x)n|0..0>} -- Grover's starting state, before any
	 * iteration.
	 * @param n The number of qubits, must be at least 1.
	 * @return The {@code 2^n x 1} uniform superposition state.
	 */
	public static MatrixComplex initialState(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("initialState() needs at least 1 qubit, got n=" + n);
		}
		int[] zeros = new int[n];
		return DeutschJozsa.hadamardChain(n).times(Qubits.ket(zeros));
	}

	/**
	 * The optimal number of Grover iterations for a single marked item among {@code N=2^n}, {@code
	 * floor(pi/4 * sqrt(N))} -- past this point the target's measurement probability starts
	 * shrinking again ("over-rotation"), verified numerically (not just by this formula) in {@code
	 * ScratchGroverAudit01}.
	 * @param n The number of qubits, must be at least 1.
	 * @return The optimal iteration count, at least {@code 1} for any {@code n>=1}.
	 */
	public static int optimalIterations(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("optimalIterations() needs at least 1 qubit, got n=" + n);
		}
		int dim = 1 << n;
		return Math.max(1, (int) Math.floor(Math.PI / 4.0 * Math.sqrt(dim)));
	}

	/**
	 * Runs {@code iterations} rounds of {@code diffusion(n) * oracle(target,n)} starting from {@link
	 * #initialState(int)}.
	 * @param target The marked item's index.
	 * @param n The number of qubits, must be at least 1.
	 * @param iterations The number of Grover iterations to run (see {@link #optimalIterations(int)}
	 * for the count that maximizes the target's measurement probability).
	 * @return The {@code 2^n x 1} state after {@code iterations} rounds.
	 */
	public static MatrixComplex run(int target, int n, int iterations) {
		MatrixComplex oracleOp = oracle(target, n);
		MatrixComplex diffusionOp = diffusion(n);
		MatrixComplex state = initialState(n);
		for (int i = 0; i < iterations; ++i) {
			state = diffusionOp.times(oracleOp.times(state));
		}
		return state;
	}

	/**
	 * The measurement probability of {@code target} in {@code state} -- {@code
	 * |<target|state>|^2}.
	 * @param state A {@code 2^n x 1} state (e.g. from {@link #run(int, int, int)}).
	 * @param target The basis-state index to measure the probability of.
	 * @return The probability, in {@code [0,1]}.
	 */
	public static double probabilityOfTarget(MatrixComplex state, int target) {
		double amp = state.getItem(target, 0).mod();
		return amp * amp;
	}

	/**
	 * Runs {@link #optimalIterations(int)} rounds of Grover search for {@code target} among {@code
	 * N=2^n} items and returns the resulting probability of measuring it -- the algorithm's
	 * headline result: {@code ~1} instead of the classical {@code 1/N}, using only {@code
	 * O(sqrt(N))} oracle queries.
	 * @param target The marked item's index.
	 * @param n The number of qubits, must be at least 1.
	 * @return The probability of measuring {@code target} after the optimal number of iterations.
	 */
	public static double search(int target, int n) {
		MatrixComplex state = run(target, n, optimalIterations(n));
		return probabilityOfTarget(state, target);
	}
}
