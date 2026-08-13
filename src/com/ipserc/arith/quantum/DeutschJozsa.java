package com.ipserc.arith.quantum;

import java.util.function.IntPredicate;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * The Deutsch-Jozsa algorithm: given a black-box Boolean function {@code f:{0,1}^n->{0,1}}
 * promised to be either CONSTANT (same output for every input) or BALANCED (output {@code 0} for
 * exactly half the inputs, {@code 1} for the other half), decides which with a SINGLE oracle
 * query -- exponentially fewer than the {@code 2^(n-1)+1} classical queries a deterministic
 * algorithm needs in the worst case. The first quantum algorithm to show a provable speedup over
 * any classical one, if only for an artificial promise problem.
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoctava sesión) -- an extension of the roadmap catalogued at the close of the
 * Trigesimosexta sesión (that roadmap's 7 candidates are all closed; this is the first of the
 * "algoritmos cuánticos más grandes" follow-ups noted there), built on {@link Qubits#hadamard()}/
 * {@link Qubits#ket(int...)} (added for {@link Teleportation}) and the same {@code
 * MatrixComplex#kroneckerprod(MatrixComplex)} chaining every class in this package already uses.
 * <p>
 * Unlike {@link BellTest}/{@link Teleportation} (which sample a probabilistic outcome), this
 * algorithm is fully DETERMINISTIC: the final measurement probability of the all-zero outcome on
 * the first {@code n} qubits is EXACTLY {@code 1} for a constant {@code f} and EXACTLY {@code 0}
 * for a balanced one -- no Monte Carlo simulation needed, {@link #isConstant(IntPredicate, int)}
 * just computes that probability directly.
 */
public final class DeutschJozsa {

	private final static String VERSION = "1.0 (2026_0813_2359)";

	private DeutschJozsa() {}

	/**
	 * The oracle unitary {@code U_f: |x>|y> -> |x>|y XOR f(x)>} for an {@code n}-qubit input
	 * register {@code x} and a 1-qubit ancilla {@code y}, {@code x} MSB-first over the first {@code
	 * n} qubits (same convention as {@link Qubits#ket(int...)}), {@code y} the last qubit -- a
	 * {@code 2^(n+1) x 2^(n+1)} permutation matrix (unitary by construction: XOR-ing {@code f(x)}
	 * twice is its own inverse), built directly from {@code f}'s truth table without simulating any
	 * circuit for {@code f} itself (a black-box oracle, exactly the algorithm's premise).
	 * @param f The Boolean function under test, as an {@link IntPredicate} over {@code x} in {@code
	 * [0,2^n)} ({@code true}={@code 1}, {@code false}={@code 0}).
	 * @param n The number of input qubits, must be at least 1.
	 * @return The {@code 2^(n+1) x 2^(n+1)} oracle unitary.
	 * @throws IllegalArgumentException if {@code n<1}.
	 */
	public static MatrixComplex oracle(IntPredicate f, int n) {
		if (n < 1) {
			throw new IllegalArgumentException("oracle() needs at least 1 input qubit, got n=" + n);
		}
		int fullDim = 1 << (n + 1);
		MatrixComplex u = new MatrixComplex(fullDim, fullDim);
		for (int idx = 0; idx < fullDim; ++idx) {
			int x = idx >> 1;
			int y = idx & 1;
			int fx = f.test(x) ? 1 : 0;
			int newIdx = (x << 1) | (y ^ fx);
			u.setItem(newIdx, idx, 1.0);
		}
		return u;
	}

	/** {@code H (x) H (x) ... (x) H}, {@code count} copies chained left to right -- the "apply
	 * Hadamard to every qubit of this block" step the algorithm's circuit needs twice (once on
	 * every qubit including the ancilla, once on just the input register). */
	private static MatrixComplex hadamardChain(int count) {
		MatrixComplex result = Qubits.hadamard();
		for (int i = 1; i < count; ++i) {
			result = result.kroneckerprod(Qubits.hadamard());
		}
		return result;
	}

	/**
	 * The full Deutsch-Jozsa circuit's measurement probability of the all-zero outcome on the first
	 * {@code n} qubits (marginalizing over the ancilla): prepare {@code |0>^n|1>}, apply {@code
	 * H^(x)(n+1)}, apply {@link #oracle(IntPredicate, int)}, apply {@code H^(x)n} to the input
	 * register only (identity on the ancilla), then sum the squared amplitudes of the 2 basis states
	 * with {@code x=0} (ancilla {@code 0} or {@code 1}). Exposed as its own method (not just inlined
	 * into {@link #isConstant(IntPredicate, int)}) so a caller/test can see the raw probability
	 * instead of only the boolean verdict.
	 * @param f The Boolean function under test.
	 * @param n The number of input qubits, must be at least 1.
	 * @return The probability, EXACTLY {@code 1.0} if {@code f} is constant, EXACTLY {@code 0.0} if
	 * {@code f} is balanced (up to floating-point rounding) -- the algorithm's whole point is that
	 * there is no third possibility for a genuinely constant-or-balanced {@code f}.
	 */
	public static double probabilityAllZero(IntPredicate f, int n) {
		int[] initBits = new int[n + 1];
		initBits[n] = 1; // ancilla starts at |1>, everything else at |0>
		MatrixComplex state = Qubits.ket(initBits);

		state = hadamardChain(n + 1).times(state);
		state = oracle(f, n).times(state);
		MatrixComplex hadamardOnInputOnly = hadamardChain(n).kroneckerprod(Qubits.identity2());
		state = hadamardOnInputOnly.times(state);

		double amp0 = state.getItem(0, 0).mod();
		double amp1 = state.getItem(1, 0).mod();
		return amp0 * amp0 + amp1 * amp1;
	}

	/**
	 * Decides, with a SINGLE call to {@link #oracle(IntPredicate, int)}, whether {@code f} is
	 * constant or balanced -- the algorithm's actual verdict, via {@link
	 * #probabilityAllZero(IntPredicate, int)}.
	 * @param f The Boolean function under test, PROMISED to be either constant or balanced (the
	 * algorithm's premise -- for any other {@code f} the result is undefined, see the thrown
	 * exception below).
	 * @param n The number of input qubits, must be at least 1.
	 * @return {@code true} if {@code f} is constant, {@code false} if balanced.
	 * @throws IllegalStateException if {@link #probabilityAllZero(IntPredicate, int)} comes out
	 * neither {@code ~0} nor {@code ~1} -- means {@code f} violates the constant-or-balanced
	 * promise this algorithm relies on (a misuse of this API, not a bug in the algorithm itself).
	 */
	public static boolean isConstant(IntPredicate f, int n) {
		double p = probabilityAllZero(f, n);
		if (Math.abs(p - 1.0) < 1e-9) { return true; }
		if (Math.abs(p) < 1e-9) { return false; }
		throw new IllegalStateException("probabilityAllZero()=" + p + " is neither ~0 nor ~1 -- f "
				+ "violates the constant-or-balanced promise Deutsch-Jozsa relies on");
	}
}
