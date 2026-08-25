package com.ipserc.arith.quantum;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Shor's factoring algorithm -- the culmination of the QFT&rarr;{@link QPE} line built up over
 * this "Rol Física/Mecánica Cuántica": both existed specifically to make THIS possible (Nielsen
 * &amp; Chuang, "Quantum Computation and Quantum Information", Sec. 5.3). Factoring composite
 * {@code N} reduces to a classical problem (finding the order {@code r} of {@code a modulo N},
 * i.e. the smallest {@code r>0} with {@code a^r == 1 (mod N)}) that is exponentially hard
 * classically but efficient on a quantum computer via {@link QPE} applied to the unitary
 * {@code U_a|y> = |a*y mod N>}: {@code U_a}'s eigenvalues are exactly {@code e^(2*pi*i*s/r)} for
 * {@code s=0..r-1}, so {@link QPE#countingProbabilities} started from the (non-eigenstate, but
 * EQUAL SUPERPOSITION of all {@code r} eigenstates) state {@code |1>} returns a distribution
 * peaked at multiples of {@code 2^t/r} -- a continued-fraction expansion of any such measurement
 * recovers {@code r} with high probability, and even where a single measurement is ambiguous
 * (e.g. it happens to land on a value whose reduced fraction implies a smaller, WRONG order),
 * classical verification ({@code a^r mod N == 1}) rejects it outright, exactly as done here.
 * <p>
 * Full flow ({@link #factor(int, int)}, or step by step):
 * <ol>
 * <li>Pick {@code a} coprime to {@code N} (if {@code gcd(a,N)>1}, that gcd IS a factor already --
 * classical luck, no quantum step needed).</li>
 * <li>{@link #modularMultiplicationUnitary} builds {@code U_a} as an explicit {@code 2^m x 2^m}
 * permutation matrix ({@code m} the number of work-register qubits, {@code 2^m>=N}), identity on
 * the unused basis states {@code N<=y<2^m} (a standard padding trick -- keeps {@code U_a}
 * unitary without changing the algorithm, since the computation never reaches those states
 * starting from {@code |1>}).</li>
 * <li>{@link #findOrder} runs {@link QPE#countingProbabilities(int, MatrixComplex, MatrixComplex)}
 * with this {@code U_a} and {@code eigenvector=|1>}, then recovers {@code r} from every nonzero
 * outcome by continued fractions ({@link #continuedFractionDenominator}), keeping the smallest
 * {@code r} that survives classical verification.</li>
 * <li>{@link #factor(int, int, int)} applies the standard post-processing: if {@code r} is even
 * and {@code a^(r/2) != -1 (mod N)}, {@code gcd(a^(r/2)-1, N)} and {@code gcd(a^(r/2)+1, N)} are
 * (with high probability) the 2 nontrivial factors.</li>
 * </ol>
 * Verified in {@code TestQuantum_Shor01} against the standard textbook example
 * ({@code N=15, a=7}, order {@code r=4}, exactly representable in {@code t=3} counting qubits --
 * chosen deliberately, like {@code QPE}'s own exact-phase test cases, so the counting-register
 * probabilities are exact and the test stays fully deterministic instead of relying on sampling).
 */
public final class Shor {

	private final static String VERSION = "1.0 (2026_0825_1900)";
	/* VERSION Release Note
	 *
	 * 1.0 (2026_0825_1900)
	 * Primera version -- culmina la linea QFT->QPE->Shor. modularMultiplicationUnitary()/
	 * findOrder()/factor(): orden por QPE (registro de trabajo de varios qubits, via la
	 * generalizacion de QPE.circuit()/Qubits.controlledBlockGate() de esta misma sesion) +
	 * fracciones continuas + verificacion clasica.
	 */

	private Shor() {}

	/**
	 * The greatest common divisor of {@code a} and {@code b} (Euclid's algorithm), non-negative.
	 */
	static int gcd(int a, int b) {
		a = Math.abs(a); b = Math.abs(b);
		while (b != 0) { int t = b; b = a % b; a = t; }
		return a;
	}

	/** {@code base^exp mod mod}, by repeated squaring. Requires {@code exp>=0}, {@code mod>=1}. */
	static int modPow(int base, int exp, int mod) {
		long result = 1, b = base % mod;
		if (b < 0) { b += mod; }
		while (exp > 0) {
			if ((exp & 1) == 1) { result = (result*b) % mod; }
			b = (b*b) % mod;
			exp >>= 1;
		}
		return (int) result;
	}

	/** The number of qubits {@code m} needed for a work register spanning {@code 0..N-1}, {@code 2^m>=N}. */
	static int workRegisterSize(int N) {
		int m = 1;
		while ((1 << m) < N) { ++m; }
		return m;
	}

	private static int[] toBits(int value, int m) {
		int[] bits = new int[m];
		for (int i = 0; i < m; ++i) { bits[i] = (value >> (m-1-i)) & 1; }
		return bits;
	}

	/**
	 * The modular-multiplication-by-{@code a} unitary {@code U_a|y> = |a*y mod N>} for
	 * {@code 0<=y<N}, identity ({@code U_a|y>=|y>}) for {@code N<=y<2^m} -- a {@code 2^m x 2^m}
	 * permutation matrix (hence unitary regardless of {@code m}'s padding), the operator {@link
	 * #findOrder} runs {@link QPE} against.
	 * @param a The multiplier, must satisfy {@code gcd(a,N)=1} (otherwise multiplication by
	 * {@code a} is not a bijection mod {@code N}, and {@code U_a} would not be a permutation).
	 * @param N The modulus, must be at least 2.
	 * @param m The work-register size in qubits, must satisfy {@code 2^m>=N}.
	 * @return The {@code 2^m x 2^m} permutation unitary.
	 * @throws IllegalArgumentException if {@code N<2}, {@code m<1}, {@code (1<<m)<N},
	 * {@code a<1}, {@code a>=N}, or {@code gcd(a,N)!=1}.
	 */
	public static MatrixComplex modularMultiplicationUnitary(int a, int N, int m) {
		if (N < 2) { throw new IllegalArgumentException("modularMultiplicationUnitary(): N must be at least 2, got " + N); }
		if (m < 1 || (1 << m) < N) { throw new IllegalArgumentException("modularMultiplicationUnitary(): m must satisfy 2^m>=N, got m=" + m + ", N=" + N); }
		if (a < 1 || a >= N) { throw new IllegalArgumentException("modularMultiplicationUnitary(): a must satisfy 1<=a<N, got a=" + a + ", N=" + N); }
		if (gcd(a, N) != 1) { throw new IllegalArgumentException("modularMultiplicationUnitary(): a=" + a + " and N=" + N + " must be coprime"); }

		int dim = 1 << m;
		MatrixComplex u = new MatrixComplex(dim, dim);
		for (int y = 0; y < dim; ++y) {
			int image = (y < N) ? (a*y) % N : y;
			u.setItem(image, y, new Complex(1, 0));
		}
		return u;
	}

	/**
	 * The convergent denominators of the continued-fraction expansion of {@code numerator/
	 * denominator}, returning the LARGEST one not exceeding {@code maxDenominator} -- the standard
	 * classical post-processing step that turns a {@link QPE} measurement {@code k} (so
	 * {@code numerator=k, denominator=2^t}) into a candidate order {@code r} (candidate denominator,
	 * bounded by {@code N}, since the true order is always {@code <N}).
	 */
	static int continuedFractionDenominator(int numerator, int denominator, int maxDenominator) {
		int n = numerator, d = denominator;
		int hPrev2 = 0, hPrev1 = 1;
		int kPrev2 = 1, kPrev1 = 0;
		int bestDenominator = 1;
		while (d != 0) {
			int a = n/d;
			int h = a*hPrev1 + hPrev2;
			int k = a*kPrev1 + kPrev2;
			if (k > maxDenominator) { break; }
			bestDenominator = k;
			hPrev2 = hPrev1; hPrev1 = h;
			kPrev2 = kPrev1; kPrev1 = k;
			int r = n % d;
			n = d; d = r;
		}
		return bestDenominator;
	}

	/**
	 * Finds the order of {@code a} modulo {@code N} (the smallest {@code r>0} with
	 * {@code a^r == 1 (mod N)}) via {@link QPE}: runs {@link
	 * QPE#countingProbabilities(int, MatrixComplex, MatrixComplex)} on {@link
	 * #modularMultiplicationUnitary} starting from {@code |1>}, then for every counting-register
	 * outcome with nonzero probability, recovers a candidate {@code r} by continued fractions and
	 * keeps the smallest one that survives classical verification ({@code a^r mod N == 1}).
	 * @param a The base, must satisfy {@code gcd(a,N)=1} and {@code 1<a<N}.
	 * @param N The modulus, must be at least 3 (composite, in practice, though not checked here).
	 * @param t The number of QPE counting qubits, must be at least 1 -- more qubits resolve the
	 * phase more finely, needed when {@code r} does not divide {@code 2^t} exactly.
	 * @return The order {@code r}, or {@code -1} if no candidate from this run survives
	 * verification (try a larger {@code t}).
	 * @throws IllegalArgumentException if {@code a}/{@code N}/{@code t} are out of range or not
	 * coprime.
	 */
	public static int findOrder(int a, int N, int t) {
		if (a <= 1 || a >= N) { throw new IllegalArgumentException("findOrder(): a must satisfy 1<a<N, got a=" + a + ", N=" + N); }
		if (gcd(a, N) != 1) { throw new IllegalArgumentException("findOrder(): a=" + a + " and N=" + N + " must be coprime"); }
		if (t < 1) { throw new IllegalArgumentException("findOrder(): t must be at least 1, got " + t); }

		int m = workRegisterSize(N);
		MatrixComplex u = modularMultiplicationUnitary(a, N, m);
		MatrixComplex one = Qubits.ket(toBits(1, m));
		double[] probabilities = QPE.countingProbabilities(t, u, one);

		int countingDim = 1 << t;
		int best = -1;
		for (int k = 1; k < countingDim; ++k) { // k=0 (phase 0) carries no information, skip
			if (probabilities[k] < 1e-9) { continue; }
			int r = continuedFractionDenominator(k, countingDim, N-1);
			if (r > 0 && modPow(a, r, N) == 1 && (best == -1 || r < best)) {
				best = r;
			}
		}
		return best;
	}

	/**
	 * Attempts to factor {@code N} using a specific base {@code a}: finds the order {@code r} of
	 * {@code a} modulo {@code N} via {@link #findOrder}, then applies the standard post-processing
	 * -- if {@code r} is even and {@code a^(r/2) != N-1 (mod N)} (i.e. not the degenerate
	 * {@code -1} case), {@code gcd(a^(r/2)-1,N)} and {@code gcd(a^(r/2)+1,N)} are the 2 factors.
	 * @param N The (odd, composite, non-prime-power) number to factor.
	 * @param a The base, must satisfy {@code gcd(a,N)=1} and {@code 1<a<N}.
	 * @param t The number of QPE counting qubits, see {@link #findOrder}.
	 * @return {@code {factor1, factor2}} (both {@code 1<factor<N}, {@code factor1*factor2==N}), or
	 * {@code null} if this {@code a} did not yield a usable order (odd {@code r}, degenerate
	 * {@code a^(r/2)==-1}, order-finding inconclusive, or a trivial gcd) -- try another {@code a}.
	 */
	public static int[] factor(int N, int a, int t) {
		int r = findOrder(a, N, t);
		if (r <= 0 || r % 2 != 0) { return null; }
		int halfPow = modPow(a, r/2, N);
		if (halfPow == N-1) { return null; }
		int f1 = gcd(halfPow-1, N);
		int f2 = gcd(halfPow+1, N);
		if (f1 > 1 && f1 < N) { return new int[] {f1, N/f1}; }
		if (f2 > 1 && f2 < N) { return new int[] {f2, N/f2}; }
		return null;
	}

	/**
	 * Convenience over {@link #factor(int, int, int)}: tries {@code a=2,3,4,...,N-1} in order,
	 * short-circuiting on a lucky classical {@code gcd(a,N)>1} (no quantum step needed for that
	 * {@code a} at all) and otherwise running the full quantum order-finding for each coprime
	 * {@code a} until one succeeds.
	 * @param N The (odd, composite, non-prime-power) number to factor, must be at least 4.
	 * @param t The number of QPE counting qubits for each attempt, see {@link #findOrder}.
	 * @return {@code {factor1, factor2}}, or {@code null} if no {@code a} in {@code [2,N-1]}
	 * yielded a usable order at this {@code t} (try a larger {@code t}).
	 * @throws IllegalArgumentException if {@code N<4} or {@code t<1}.
	 */
	public static int[] factor(int N, int t) {
		if (N < 4) { throw new IllegalArgumentException("factor(): N must be at least 4, got " + N); }
		if (t < 1) { throw new IllegalArgumentException("factor(): t must be at least 1, got " + t); }
		for (int a = 2; a < N; ++a) {
			int g = gcd(a, N);
			if (g > 1) { return new int[] {g, N/g}; }
			int[] result = factor(N, a, t);
			if (result != null) { return result; }
		}
		return null;
	}
}
