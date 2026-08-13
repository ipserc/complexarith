package com.ipserc.arith.quantum;

import java.util.function.IntPredicate;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * The Bernstein-Vazirani algorithm: given a black-box function {@code f(x) = (a.x) mod 2} (the
 * bitwise dot product of the input {@code x} with a HIDDEN {@code n}-bit secret string {@code a}),
 * recovers the entire secret {@code a} with a SINGLE oracle query -- {@code n} classical queries
 * are needed in the worst case (probe each bit of {@code a} separately with {@code f(2^i)}).
 * Structurally the same circuit as {@link DeutschJozsa} ({@code H^(x)(n+1)}, oracle, {@code
 * H^(x)n} on the input register, reusing {@link DeutschJozsa#runCircuit(IntPredicate, int)}
 * verbatim), but where Deutsch-Jozsa only reads 1 bit of information out of the final state (is
 * the all-zero amplitude {@code ~1} or {@code ~0}?), Bernstein-Vazirani reads the WHOLE input
 * register: the final state collapses deterministically to {@code |a>}, not just to "not
 * {@code |0>}".
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoctava sesión) -- second "algoritmo cuántico más grande" follow-up, after {@link
 * DeutschJozsa}.
 */
public final class BernsteinVazirani {

	private final static String VERSION = "1.0 (2026_0813_2359)";

	private BernsteinVazirani() {}

	/**
	 * The oracle function {@code f(x) = (secret.x) mod 2} (bitwise AND then parity) for a hidden
	 * {@code n}-bit {@code secret} -- the specific linear Boolean function this algorithm is built
	 * to find, ready to pass to {@link DeutschJozsa#oracle(IntPredicate, int)} or {@link
	 * #findSecret(IntPredicate, int)}.
	 * @param secret The hidden {@code n}-bit string, as an integer in {@code [0,2^n)}.
	 * @param n The number of bits of {@code secret}, must be at least 1.
	 * @return The oracle function {@code f}.
	 * @throws IllegalArgumentException if {@code n<1} or {@code secret} is out of {@code [0,2^n)}.
	 */
	public static IntPredicate oracleFunction(int secret, int n) {
		if (n < 1) {
			throw new IllegalArgumentException("oracleFunction() needs at least 1 bit, got n=" + n);
		}
		if (secret < 0 || secret >= (1 << n)) {
			throw new IllegalArgumentException("secret=" + secret + " out of range for n=" + n + " bits");
		}
		return x -> (Integer.bitCount(x & secret) % 2) == 1;
	}

	/**
	 * Recovers the hidden {@code n}-bit secret with a SINGLE oracle query: runs {@link
	 * DeutschJozsa#runCircuit(IntPredicate, int)} (the same {@code H^(x)(n+1)}-oracle-{@code
	 * H^(x)n} circuit Deutsch-Jozsa uses), then reads off which of the {@code 2^n} input-register
	 * basis states has probability {@code ~1} (marginalizing over the ancilla) -- for {@code
	 * f(x)=(secret.x) mod 2}, that basis state is EXACTLY {@code |secret>}.
	 * @param f The oracle function, PROMISED to be {@code f(x)=(a.x) mod 2} for some hidden
	 * {@code a} (e.g. from {@link #oracleFunction(int, int)}) -- the algorithm's premise, for any
	 * other {@code f} the result is undefined (see the thrown exception below).
	 * @param n The number of input qubits, must be at least 1.
	 * @return The recovered secret {@code a}, as an integer in {@code [0,2^n)}.
	 * @throws IllegalStateException if the final state doesn't collapse to a single deterministic
	 * outcome (probability {@code ~1} on exactly one input-register value) -- means {@code f}
	 * isn't actually of the linear {@code (a.x) mod 2} form this algorithm relies on.
	 */
	public static int findSecret(IntPredicate f, int n) {
		MatrixComplex state = DeutschJozsa.runCircuit(f, n);
		int dim = 1 << n;
		int found = -1;
		double foundProbability = 0.0;
		for (int x = 0; x < dim; ++x) {
			double amp0 = state.getItem(x * 2, 0).mod();
			double amp1 = state.getItem(x * 2 + 1, 0).mod();
			double probability = amp0 * amp0 + amp1 * amp1;
			if (probability > 0.5) {
				found = x;
				foundProbability = probability;
			}
		}
		if (found < 0 || Math.abs(foundProbability - 1.0) > 1e-9) {
			throw new IllegalStateException("Bernstein-Vazirani circuit did not collapse to a single "
					+ "deterministic outcome (best probability=" + foundProbability + ") -- f is not "
					+ "of the form f(x)=(a.x) mod 2 this algorithm relies on");
		}
		return found;
	}
}
