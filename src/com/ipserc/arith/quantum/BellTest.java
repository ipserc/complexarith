
package com.ipserc.arith.quantum;

import java.util.Random;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * A CHSH (Clauser-Horne-Shimony-Holt) Bell test on an entangled state -- the standard experiment
 * demonstrating that quantum correlations violate the classical bound {@code |S|<=2} that any
 * local-hidden-variable theory must respect, reaching up to {@code 2*sqrt(2)} (Tsirelson's bound)
 * for a maximally entangled 2-qubit state and the optimal measurement angles.
 * <p>
 * Two ways to run the same experiment, both built on the same {@link Qubits} vocabulary:
 * <ul>
 * <li>{@link #chsh(MatrixComplex, int, int, int, double, double, double, double)} -- the EXACT
 * quantum-mechanical prediction, computed directly by matrix algebra ({@code
 * E(a,b)=<state|A(a) tensor B(b)|state>}, {@code A}/{@code B} lifted onto their own qubit of an
 * n-qubit register). No randomness involved; this is the reference value every real (or
 * simulated) experiment is expected to converge to.</li>
 * <li>{@link #simulateChsh(MatrixComplex, int, int, int, double, double, double, double, int,
 * Random)} -- a Monte Carlo simulation of individual projective measurements (Born-rule sampling,
 * +-1 outcomes, marginalizing over every qubit not being measured), the same way a real lab
 * experiment would estimate the correlations from many runs. Converges to the exact value above
 * as the number of trials grows.</li>
 * </ul>
 * Every method has a 2-qubit convenience overload (measuring qubits 0/1 of a 2-qubit register)
 * that delegates to the n-qubit general form -- the original API surface of this class, kept
 * unchanged.
 * <p>
 * First exercise of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoquinta sesión); generalized to measurements on 2 arbitrary qubits of an n-qubit
 * register (Trigesimoctava sesión) -- one of the gaps catalogued at the close of the
 * Trigesimosexta sesión, using {@link Qubits#operatorOnQubit(MatrixComplex, int, int)} (already
 * built for {@link Qubits#ghz(int)}/n-qubit measurements) instead of a fixed 2-qubit {@code
 * kroneckerprod}.
 */
public final class BellTest {

	private final static String VERSION = "1.3 (2026_0814_1600)";
	/* VERSION Release Note
	 * 1.3 (2026_0814_1600)
	 * spinEigenProjector() usa Qubits.bra() en vez de ket.adjoint() (legibilidad, a peticion del
	 * usuario). Sin cambio de comportamiento.
	 */

	private BellTest() {}

	/**
	 * The exact quantum-mechanical correlation {@code E(A,B) = <state|(opA_qubitA tensor
	 * opB_qubitB)|state>} for a {@code nQubits}-qubit {@code state} and two single-qubit Hermitian
	 * measurement operators acting on 2 arbitrary (not necessarily adjacent) qubits of it -- every
	 * other qubit is left untouched (implicit identity), so this is exactly the same expectation
	 * value a partial-trace-then-{@code Tr(rho*(opA tensor opB))} calculation over the reduced
	 * 2-qubit density matrix of {@code qubitA}/{@code qubitB} would give (verified in {@code
	 * ScratchBellTestNQubitAudit01} against {@link DensityMatrix}).
	 * @param state The {@code 2^nQubits x 1} state.
	 * @param opA The measurement operator for {@code qubitA} (e.g. {@link
	 * Qubits#spinOperator(double)}).
	 * @param qubitA The 0-based index of the qubit {@code opA} measures.
	 * @param opB The measurement operator for {@code qubitB}.
	 * @param qubitB The 0-based index of the qubit {@code opB} measures, must differ from {@code
	 * qubitA}.
	 * @param nQubits The total number of qubits in {@code state}.
	 * @return The real-valued correlation, in {@code [-1,1]}.
	 * @throws IllegalArgumentException if {@code qubitA==qubitB} or either index is out of range
	 * (via {@link Qubits#operatorOnQubit(MatrixComplex, int, int)}).
	 * @throws IllegalStateException if the expectation value comes out with a non-negligible
	 * imaginary part -- would mean {@code opA}/{@code opB} aren't actually Hermitian (a physical
	 * observable's expectation value must be real; failing loud here catches a misuse of this API
	 * the same way {@code Jordan.checkReconstruction()} fails loud elsewhere in this project).
	 */
	public static double correlation(MatrixComplex state, MatrixComplex opA, int qubitA, MatrixComplex opB, int qubitB, int nQubits) {
		if (qubitA == qubitB) {
			throw new IllegalArgumentException("qubitA and qubitB must differ, both were " + qubitA);
		}
		MatrixComplex observable = Qubits.operatorOnQubit(opA, qubitA, nQubits).times(Qubits.operatorOnQubit(opB, qubitB, nQubits));
		return TimeEvolution.expectationValue(state, observable);
	}

	/**
	 * 2-qubit convenience form of {@link #correlation(MatrixComplex, MatrixComplex, int,
	 * MatrixComplex, int, int)}: {@code qubitA=0}, {@code qubitB=1}, {@code nQubits=2}.
	 * @param state The 2-qubit state, as a 4x1 column vector (e.g. {@link Qubits#bellPhiPlus()}).
	 * @param opA The measurement operator for the first qubit.
	 * @param opB The measurement operator for the second qubit.
	 * @return The real-valued correlation, in {@code [-1,1]}.
	 */
	public static double correlation(MatrixComplex state, MatrixComplex opA, MatrixComplex opB) {
		return correlation(state, opA, 0, opB, 1, 2);
	}

	/**
	 * The exact CHSH parameter {@code S = E(a,b) - E(a,b') + E(a',b) + E(a',b')}, for the
	 * {@link Qubits#spinOperator(double)} family of measurement operators at 4 angles, measuring
	 * {@code qubitA}/{@code qubitB} of an {@code nQubits}-qubit register. For {@code
	 * state=Qubits.bellPhiPlus()} (the {@code nQubits=2} case) this correlation works out to
	 * {@code E(a,b)=cos(a-b)} (expand {@code A(a) tensor B(b)} in the Pauli basis and use
	 * {@code <Phi+|Z tensor Z|Phi+> = <Phi+|X tensor X|Phi+> = 1}, {@code <Phi+|Z tensor X|Phi+> =
	 * <Phi+|X tensor Z|Phi+> = 0}) -- maximized at angles spaced by {@code pi/4} (NOT {@code
	 * pi/8}, the spacing used in the textbook "polarizer angle" convention where the correlation is
	 * {@code cos(2*(a-b))} instead -- {@code theta} here is already the full operator angle, the
	 * {@code /2} is baked into {@link #spinEigenket(double, int)} instead). {@code a=0, a'=pi/2,
	 * b=pi/4, b'=3*pi/4} reaches {@code 2*sqrt(2)~=2.8284271247461903} (Tsirelson's bound) --
	 * strictly above the classical bound of {@code 2}, the actual "violation of Bell's theorem".
	 * For {@code nQubits>2} (e.g. 2 qubits of a {@link Qubits#ghz(int)} state, tracing out the
	 * rest), {@code E(a,b)} need not follow the same {@code cos(a-b)} closed form -- it depends on
	 * the reduced 2-qubit density matrix of {@code qubitA}/{@code qubitB}, which for {@code
	 * ghz(n>2)} is a MIXED state, not the pure Bell pair {@code bellPhiPlus()} is.
	 * @param state The {@code nQubits}-qubit state.
	 * @param qubitA The 0-based index of the first measured qubit.
	 * @param qubitB The 0-based index of the second measured qubit, must differ from {@code qubitA}.
	 * @param nQubits The total number of qubits in {@code state}.
	 * @param a First measurement angle for {@code qubitA}, in radians.
	 * @param aPrime Second measurement angle for {@code qubitA}, in radians.
	 * @param b First measurement angle for {@code qubitB}, in radians.
	 * @param bPrime Second measurement angle for {@code qubitB}, in radians.
	 * @return The CHSH parameter S.
	 */
	public static double chsh(MatrixComplex state, int qubitA, int qubitB, int nQubits, double a, double aPrime, double b, double bPrime) {
		double eab = correlation(state, Qubits.spinOperator(a), qubitA, Qubits.spinOperator(b), qubitB, nQubits);
		double eabPrime = correlation(state, Qubits.spinOperator(a), qubitA, Qubits.spinOperator(bPrime), qubitB, nQubits);
		double eaPrimeB = correlation(state, Qubits.spinOperator(aPrime), qubitA, Qubits.spinOperator(b), qubitB, nQubits);
		double eaPrimeBPrime = correlation(state, Qubits.spinOperator(aPrime), qubitA, Qubits.spinOperator(bPrime), qubitB, nQubits);
		return eab - eabPrime + eaPrimeB + eaPrimeBPrime;
	}

	/**
	 * 2-qubit convenience form of {@link #chsh(MatrixComplex, int, int, int, double, double,
	 * double, double)}: {@code qubitA=0}, {@code qubitB=1}, {@code nQubits=2}.
	 * @param state The 2-qubit state.
	 * @param a First measurement angle for qubit A, in radians.
	 * @param aPrime Second measurement angle for qubit A, in radians.
	 * @param b First measurement angle for qubit B, in radians.
	 * @param bPrime Second measurement angle for qubit B, in radians.
	 * @return The CHSH parameter S.
	 */
	public static double chsh(MatrixComplex state, double a, double aPrime, double b, double bPrime) {
		return chsh(state, 0, 1, 2, a, aPrime, b, bPrime);
	}

	/**
	 * The {@code +1}/{@code -1} eigenket of {@link Qubits#spinOperator(double)} at angle {@code
	 * theta} -- closed form for this specific 2x2 operator (not a generic diagonalization, no
	 * {@code Eigenspace}/{@code Jordan} needed): solving {@code (A(theta)-sign*I)v=0} with the
	 * half-angle identities {@code cos(theta)-1=-2sin^2(theta/2)}, {@code
	 * sin(theta)=2sin(theta/2)cos(theta/2)} gives {@code v=(cos(theta/2),sin(theta/2))} for {@code
	 * sign=+1} and its orthogonal {@code v=(-sin(theta/2),cos(theta/2))} for {@code sign=-1} --
	 * the textbook spin-1/2 measurement eigenstates.
	 */
	private static MatrixComplex spinEigenket(double theta, int sign) {
		double half = theta / 2.0;
		MatrixComplex ket = new MatrixComplex(2, 1);
		if (sign > 0) {
			ket.setItem(0, 0, Math.cos(half));
			ket.setItem(1, 0, Math.sin(half));
		} else {
			ket.setItem(0, 0, -Math.sin(half));
			ket.setItem(1, 0, Math.cos(half));
		}
		return ket;
	}

	/** The projector {@code |e><e|} onto the {@code sign} eigenket of {@link #spinEigenket(double, int)}. */
	private static MatrixComplex spinEigenProjector(double theta, int sign) {
		MatrixComplex ket = spinEigenket(theta, sign);
		return ket.times(Qubits.bra(ket));
	}

	/**
	 * Monte Carlo estimate of {@link #correlation(MatrixComplex, MatrixComplex, int,
	 * MatrixComplex, int, int)} for the {@link Qubits#spinOperator(double)} family, measuring
	 * {@code qubitA}/{@code qubitB} of an {@code nQubits}-qubit register: samples {@code trials}
	 * independent joint projective measurements (Born-rule probabilities {@code
	 * <state|(proj_A^s1 tensor proj_B^s2)|state>} over the 4 combinations of {@code +-1} outcomes,
	 * {@code proj} the rank-1 projector onto {@link #spinEigenket(double, int)}, marginalizing over
	 * every qubit other than {@code qubitA}/{@code qubitB} -- the projector leaves every other
	 * qubit as an implicit identity, exactly as {@link #correlation} does for the operator itself),
	 * and returns the average of the {@code s1*s2} products -- the same statistic a real Bell-test
	 * experiment estimates from many runs.
	 * @param state The {@code nQubits}-qubit state.
	 * @param thetaA Measurement angle for {@code qubitA}, in radians.
	 * @param qubitA The 0-based index of the first measured qubit.
	 * @param thetaB Measurement angle for {@code qubitB}, in radians.
	 * @param qubitB The 0-based index of the second measured qubit, must differ from {@code qubitA}.
	 * @param nQubits The total number of qubits in {@code state}.
	 * @param trials Number of simulated measurement runs.
	 * @param rng The random source (no fixed seed required by this method -- pass a seeded {@link
	 * Random} for reproducible runs).
	 * @return The estimated correlation, converging to {@link #correlation} as {@code trials} grows.
	 */
	public static double simulateCorrelation(MatrixComplex state, double thetaA, int qubitA, double thetaB, int qubitB, int nQubits, int trials, Random rng) {
		if (qubitA == qubitB) {
			throw new IllegalArgumentException("qubitA and qubitB must differ, both were " + qubitA);
		}
		int[] sign = { +1, -1 };
		int[] product = { 1, -1, -1, 1 }; // (+,+) (+,-) (-,+) (-,-)
		double[] probability = new double[4];
		int idx = 0;
		for (int sA : sign) {
			for (int sB : sign) {
				MatrixComplex proj = Qubits.operatorOnQubit(spinEigenProjector(thetaA, sA), qubitA, nQubits)
						.times(Qubits.operatorOnQubit(spinEigenProjector(thetaB, sB), qubitB, nQubits));
				probability[idx] = TimeEvolution.expectationValue(state, proj);
				++idx;
			}
		}

		double accumulator = 0.0;
		for (int t = 0; t < trials; ++t) {
			double r = rng.nextDouble();
			double cumulative = 0.0;
			int outcome = 3; // last bucket by default, guards against floating-point rounding at the tail
			for (int i = 0; i < 4; ++i) {
				cumulative += probability[i];
				if (r < cumulative) { outcome = i; break; }
			}
			accumulator += product[outcome];
		}
		return accumulator / trials;
	}

	/**
	 * 2-qubit convenience form of {@link #simulateCorrelation(MatrixComplex, double, int, double,
	 * int, int, int, Random)}: {@code qubitA=0}, {@code qubitB=1}, {@code nQubits=2}.
	 * @param state The 2-qubit state.
	 * @param thetaA Measurement angle for qubit A, in radians.
	 * @param thetaB Measurement angle for qubit B, in radians.
	 * @param trials Number of simulated measurement runs.
	 * @param rng The random source.
	 * @return The estimated correlation.
	 */
	public static double simulateCorrelation(MatrixComplex state, double thetaA, double thetaB, int trials, Random rng) {
		return simulateCorrelation(state, thetaA, 0, thetaB, 1, 2, trials, rng);
	}

	/**
	 * Monte Carlo estimate of {@link #chsh(MatrixComplex, int, int, int, double, double, double,
	 * double)}: runs 4 independent {@link #simulateCorrelation} experiments (one per angle pair),
	 * same as a real Bell-test setup that measures each of the 4 settings in its own run.
	 * @param state The {@code nQubits}-qubit state.
	 * @param qubitA The 0-based index of the first measured qubit.
	 * @param qubitB The 0-based index of the second measured qubit, must differ from {@code qubitA}.
	 * @param nQubits The total number of qubits in {@code state}.
	 * @param trials Number of simulated measurement runs PER angle pair (4 pairs total).
	 * @param rng The random source, shared across the 4 experiments.
	 * @return The estimated CHSH parameter S, converging to {@link #chsh} as {@code trials} grows.
	 */
	public static double simulateChsh(MatrixComplex state, int qubitA, int qubitB, int nQubits, double a, double aPrime, double b, double bPrime, int trials, Random rng) {
		double eab = simulateCorrelation(state, a, qubitA, b, qubitB, nQubits, trials, rng);
		double eabPrime = simulateCorrelation(state, a, qubitA, bPrime, qubitB, nQubits, trials, rng);
		double eaPrimeB = simulateCorrelation(state, aPrime, qubitA, b, qubitB, nQubits, trials, rng);
		double eaPrimeBPrime = simulateCorrelation(state, aPrime, qubitA, bPrime, qubitB, nQubits, trials, rng);
		return eab - eabPrime + eaPrimeB + eaPrimeBPrime;
	}

	/**
	 * 2-qubit convenience form of {@link #simulateChsh(MatrixComplex, int, int, int, double,
	 * double, double, double, int, Random)}: {@code qubitA=0}, {@code qubitB=1}, {@code nQubits=2}.
	 * @param trials Number of simulated measurement runs PER angle pair (4 pairs total).
	 * @param rng The random source, shared across the 4 experiments.
	 * @return The estimated CHSH parameter S, converging to {@link #chsh} as {@code trials} grows.
	 */
	public static double simulateChsh(MatrixComplex state, double a, double aPrime, double b, double bPrime, int trials, Random rng) {
		return simulateChsh(state, 0, 1, 2, a, aPrime, b, bPrime, trials, rng);
	}
}
