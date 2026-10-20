package com.ipserc.arith.quantum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Fixed-photon-number Fock space of {@code n} identical bosons (photons) distributed over {@code m}
 * modes, and the action of a linear-optical network (beam splitters, phase shifters...) on it --
 * the minimal "second quantization" layer needed for multi-photon interference such as the
 * Hong-Ou-Mandel effect, which the one-qubit-per-particle model of {@link Qubits} cannot express
 * (a tensor product of qubits describes DISTINGUISHABLE particles; see {@code
 * quantum_doc/17_limites_y_extensiones.md}, section 3).
 * <p>
 * A basis state is an occupation-number vector {@code |n_1,...,n_m>} with {@code n_1+...+n_m = n};
 * the space has dimension {@code C(n+m-1, n)}. Basis states are ordered lexicographically
 * DESCENDING on the occupation vector ({@code |2,0>, |1,1>, |0,2>} for 2 photons in 2 modes), and a
 * state is a plain {@link MatrixComplex} column vector over that basis -- same representation
 * choice as {@code Qubits}, so {@code times()}, {@code adjoint()}, {@code println()} all apply
 * directly.
 * <p>
 * A passive linear-optical network is fully described by its {@code m x m} single-photon unitary
 * {@code U} (creation operators transform as {@code a}<sup>&#8224;</sup>{@code _j -> sum_i U_ij
 * a}<sup>&#8224;</sup>{@code _i}, so for {@code n=1} the Fock-space operator IS {@code U}, the same
 * matrix {@code Qubits} applies to a 1-photon path qubit). Its action on {@code n} photons is
 * obtained exactly -- no truncation, no operator exponential -- from permanents of submatrices of
 * {@code U} (the standard boson-sampling formula, see {@link #multiPhotonUnitary(MatrixComplex,
 * int)}). For fermions the same construction would use determinants instead; not implemented.
 */
public final class Fock {

	private final static String VERSION = "1.0 (2026_1021_0017)";
	/* VERSION Release Note
	 * 1.0 (2026_1021_0017)
	 * Primera version: base de numeros de ocupacion (basis, dimension, indexOf, ket, basisLabel),
	 * permanente por la formula de Ryser, operador de n fotones Phi_n(U) a partir de la unitaria
	 * de 1 foton (formula de permanentes del boson sampling), y medida de Born sobre la base de
	 * Fock (measure, reutilizando el muestreo comun de Qubits). Construida para el efecto
	 * Hong-Ou-Mandel (TestQuantum_HongOuMandel01), candidato aplazado desde la Cuadragesimosexta
	 * sesion, a peticion del usuario.
	 */

	private Fock() {}

	/**
	 * The dimension of the Fock space of {@code nPhotons} identical bosons in {@code nModes} modes,
	 * {@code C(nPhotons+nModes-1, nPhotons)} (stars and bars).
	 * @param nPhotons Number of photons, at least 1.
	 * @param nModes Number of modes, at least 1.
	 * @return The number of occupation-number basis states.
	 * @throws IllegalArgumentException if {@code nPhotons < 1} or {@code nModes < 1}.
	 */
	public static int dimension(int nPhotons, int nModes) {
		checkSizes(nPhotons, nModes, "dimension");
		long result = 1;
		// C(n+m-1, n) as an incremental product: exact at every step, since each partial
		// product is itself a binomial coefficient C(m-1+k, k).
		for (int k = 1; k <= nPhotons; ++k) {
			result = result * (nModes - 1 + k) / k;
		}
		return (int) result;
	}

	/**
	 * All occupation-number basis states of {@code nPhotons} photons in {@code nModes} modes, in the
	 * canonical order used by every other method of this class (lexicographically descending:
	 * {@code [2,0], [1,1], [0,2]} for 2 photons in 2 modes).
	 * @param nPhotons Number of photons, at least 1.
	 * @param nModes Number of modes, at least 1.
	 * @return An array of {@link #dimension(int, int)} occupation vectors, each of length {@code
	 * nModes} and summing to {@code nPhotons}.
	 * @throws IllegalArgumentException if {@code nPhotons < 1} or {@code nModes < 1}.
	 */
	public static int[][] basis(int nPhotons, int nModes) {
		checkSizes(nPhotons, nModes, "basis");
		List<int[]> states = new ArrayList<>();
		fillBasis(new int[nModes], 0, nPhotons, states);
		return states.toArray(new int[0][]);
	}

	/** Recursive generator for {@link #basis(int, int)}: puts the most photons in the earliest mode first. */
	private static void fillBasis(int[] current, int mode, int remaining, List<int[]> states) {
		if (mode == current.length - 1) {
			current[mode] = remaining;
			states.add(current.clone());
			return;
		}
		for (int k = remaining; k >= 0; --k) {
			current[mode] = k;
			fillBasis(current, mode + 1, remaining - k, states);
		}
	}

	/**
	 * The position of an occupation vector in the canonical basis order of {@link #basis(int,
	 * int)}, for {@code nPhotons} = sum of {@code occupations} and {@code nModes} = its length.
	 * @param occupations The occupation number of each mode, all {@code >= 0}, summing to at least 1.
	 * @return The 0-based basis index.
	 * @throws IllegalArgumentException if any occupation is negative or the total is 0.
	 */
	public static int indexOf(int... occupations) {
		int nPhotons = totalPhotons(occupations, "indexOf");
		int[][] basis = basis(nPhotons, occupations.length);
		for (int i = 0; i < basis.length; ++i) {
			if (java.util.Arrays.equals(basis[i], occupations)) {
				return i;
			}
		}
		throw new IllegalStateException("indexOf(): occupation vector not found in its own basis (bug)");
	}

	/**
	 * The Fock basis state {@code |n_1,...,n_m>}, as a column vector over {@link #basis(int, int)}
	 * with a single amplitude 1.
	 * @param occupations The occupation number of each mode (length = number of modes), all {@code
	 * >= 0}, summing to at least 1.
	 * @return The basis ket, a {@code dimension x 1} column vector.
	 * @throws IllegalArgumentException if any occupation is negative or the total is 0.
	 */
	public static MatrixComplex ket(int... occupations) {
		int nPhotons = totalPhotons(occupations, "ket");
		MatrixComplex ket = new MatrixComplex(dimension(nPhotons, occupations.length), 1);
		ket.setItem(indexOf(occupations), 0, 1.0);
		return ket;
	}

	/**
	 * Dirac label of an occupation vector, e.g. {@code "|1,1>"}.
	 * @param occupations The occupation number of each mode.
	 * @return The label.
	 */
	public static String basisLabel(int... occupations) {
		StringBuilder sb = new StringBuilder("|");
		for (int i = 0; i < occupations.length; ++i) {
			if (i > 0) sb.append(',');
			sb.append(occupations[i]);
		}
		return sb.append('>').toString();
	}

	/**
	 * The permanent of a square matrix, {@code Per(A) = sum over permutations s of prod_i
	 * A[i][s(i)]} -- the determinant without the alternating signs, i.e. the quantity that encodes
	 * bosonic exchange symmetry (the determinant encodes the fermionic one). Computed with Ryser's
	 * formula, {@code Per(A) = (-1)^n sum over non-empty column subsets S of (-1)^|S| prod_i sum_{j
	 * in S} A[i][j]}, in {@code O(2^n n^2)} instead of the {@code O(n! n)} of the definition. No
	 * polynomial algorithm is known (computing permanents is #P-hard, the root of the classical
	 * hardness of boson sampling), so this is meant for small {@code n} only -- the number of
	 * photons, not the number of modes.
	 * @param a A square matrix.
	 * @return {@code Per(a)}; {@code 1} for a {@code 0x0} matrix (empty product).
	 * @throws IllegalArgumentException if {@code a} is not square.
	 */
	public static Complex permanent(MatrixComplex a) {
		int n = a.rows();
		if (a.cols() != n) {
			throw new IllegalArgumentException("permanent() needs a square matrix, got " + n + "x" + a.cols());
		}
		if (n == 0) {
			return new Complex(1.0, 0.0);
		}
		Complex total = new Complex(0.0, 0.0);
		for (int subset = 1; subset < (1 << n); ++subset) {
			Complex product = new Complex(1.0, 0.0);
			for (int i = 0; i < n; ++i) {
				Complex rowSum = new Complex(0.0, 0.0);
				for (int j = 0; j < n; ++j) {
					if ((subset & (1 << j)) != 0) {
						rowSum = rowSum.plus(a.getItem(i, j));
					}
				}
				product = product.times(rowSum);
			}
			total = (Integer.bitCount(subset) % 2 == 0) ? total.plus(product) : total.minus(product);
		}
		return (n % 2 == 0) ? total : total.times(-1.0);
	}

	/**
	 * The operator {@code Phi_n(U)} by which a linear-optical network with single-photon unitary
	 * {@code U} acts on the Fock space of {@code nPhotons} identical photons:
	 * <p>
	 * {@code <s|Phi_n(U)|t> = Per(U[s,t]) / sqrt(prod_i s_i! * prod_j t_j!)}
	 * <p>
	 * where {@code s}, {@code t} are occupation vectors and {@code U[s,t]} is the {@code n x n}
	 * matrix built from {@code U} by taking row {@code i} {@code s_i} times and column {@code j}
	 * {@code t_j} times. For {@code nPhotons=1} this is {@code U} itself. {@code Phi_n} is a group
	 * homomorphism ({@code Phi_n(UV) = Phi_n(U) Phi_n(V)}) and maps unitaries to unitaries -- the
	 * result is re-checked for unitarity before returning (fail-loud), as a guard against a wrong
	 * basis ordering or normalization.
	 * @param u The single-photon unitary, {@code m x m}.
	 * @param nPhotons Number of photons, at least 1.
	 * @return {@code Phi_n(U)}, a {@code dimension(n,m) x dimension(n,m)} unitary over {@link
	 * #basis(int, int)}.
	 * @throws IllegalArgumentException if {@code u} is not square or not unitary, or {@code
	 * nPhotons < 1}.
	 * @throws IllegalStateException if the constructed operator is not unitary (internal bug).
	 */
	public static MatrixComplex multiPhotonUnitary(MatrixComplex u, int nPhotons) {
		int nModes = u.rows();
		if (u.cols() != nModes) {
			throw new IllegalArgumentException("multiPhotonUnitary() needs a square single-photon matrix, got "
					+ nModes + "x" + u.cols());
		}
		if (!u.isUnitary()) {
			throw new IllegalArgumentException("multiPhotonUnitary() needs a unitary single-photon matrix");
		}
		int[][] basis = basis(nPhotons, nModes);
		int dim = basis.length;
		int[][] modeLists = new int[dim][];
		double[] sqrtFactorials = new double[dim];
		for (int k = 0; k < dim; ++k) {
			modeLists[k] = repeatedModes(basis[k], nPhotons);
			sqrtFactorials[k] = Math.sqrt(productOfFactorials(basis[k]));
		}
		MatrixComplex phi = new MatrixComplex(dim, dim);
		MatrixComplex sub = new MatrixComplex(nPhotons, nPhotons);
		for (int s = 0; s < dim; ++s) {
			for (int t = 0; t < dim; ++t) {
				for (int i = 0; i < nPhotons; ++i) {
					for (int j = 0; j < nPhotons; ++j) {
						sub.setItem(i, j, u.getItem(modeLists[s][i], modeLists[t][j]));
					}
				}
				phi.setItem(s, t, permanent(sub).divides(sqrtFactorials[s] * sqrtFactorials[t]));
			}
		}
		if (!phi.isUnitary()) {
			throw new IllegalStateException("multiPhotonUnitary(): Phi_n(U) is not unitary (bug)");
		}
		return phi;
	}

	/**
	 * Simulates a single photon-number-resolving measurement of every mode: collapses {@code state}
	 * to one Fock basis state with probability {@code |amplitude|^2} (Born rule). Same sampling as
	 * {@link Qubits#measure(MatrixComplex, Random)}, without its power-of-2 requirement (a Fock
	 * space has dimension {@code C(n+m-1, n)}, rarely a power of 2).
	 * @param state A ket over {@link #basis(int, int)}, as a column vector.
	 * @param random The source of randomness -- pass a seeded {@link Random} for reproducible tests.
	 * @return The 0-based index of the measured basis state (look it up in {@code basis(n, m)}).
	 * @throws IllegalArgumentException if {@code state} is not a column vector.
	 */
	public static int measure(MatrixComplex state, Random random) {
		if (state.cols() != 1) {
			throw new IllegalArgumentException("measure() needs a column vector (a ket), got a "
					+ state.rows() + "x" + state.cols() + " matrix");
		}
		return Qubits.bornSample(state, random);
	}

	/**
	 * Histogram of {@code shots} independent measurements of {@code state} (each on a fresh copy),
	 * a loop over {@link #measure(MatrixComplex, Random)}.
	 * @param state A ket over {@link #basis(int, int)}, as a column vector.
	 * @param shots Number of measurements, at least 1.
	 * @param random The source of randomness.
	 * @return {@code result[i]} = number of shots that measured basis state {@code i}.
	 * @throws IllegalArgumentException if {@code shots < 1} or {@code state} is not a column vector.
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

	/** Mode index of each photon of an occupation vector, e.g. {@code [1,0,2] -> [0,2,2]}. */
	private static int[] repeatedModes(int[] occupations, int nPhotons) {
		int[] modes = new int[nPhotons];
		int p = 0;
		for (int mode = 0; mode < occupations.length; ++mode) {
			for (int k = 0; k < occupations[mode]; ++k) {
				modes[p++] = mode;
			}
		}
		return modes;
	}

	private static double productOfFactorials(int[] occupations) {
		double result = 1.0;
		for (int n : occupations) {
			for (int k = 2; k <= n; ++k) {
				result *= k;
			}
		}
		return result;
	}

	private static int totalPhotons(int[] occupations, String caller) {
		int total = 0;
		for (int n : occupations) {
			if (n < 0) {
				throw new IllegalArgumentException(caller + "() needs non-negative occupations, got " + n);
			}
			total += n;
		}
		if (occupations.length < 1 || total < 1) {
			throw new IllegalArgumentException(caller + "() needs at least 1 mode and 1 photon");
		}
		return total;
	}

	private static void checkSizes(int nPhotons, int nModes, String caller) {
		if (nPhotons < 1 || nModes < 1) {
			throw new IllegalArgumentException(caller + "() needs nPhotons>=1 and nModes>=1, got "
					+ nPhotons + ", " + nModes);
		}
	}
}
