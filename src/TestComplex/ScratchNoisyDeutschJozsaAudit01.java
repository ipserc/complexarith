package TestComplex;

import java.util.function.IntPredicate;

import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.DeutschJozsa;
import com.ipserc.arith.quantum.NoisyDeutschJozsa;

/**
 * Audit of NoisyDeutschJozsa.java -- combines Decoherence with DeutschJozsa, second cross-
 * exercise combination candidate from the close of the Trigesimoseptima sesion, after
 * NoisyTeleportation.
 * <p>
 * The checks below encode several genuine, non-obvious facts about WHERE noise matters in this
 * circuit -- found by actually running the numbers, not assumed beforehand (an earlier draft of
 * this audit asserted "ancilla noise degrades a constant f" and "amplitudeDamping/phaseFlip on an
 * input qubit degrades the guarantee", both WRONG; both failed and were replaced by what the math
 * actually does, see checks 2/3/5 below).
 */
public class ScratchNoisyDeutschJozsaAudit01 {

	static int ok = 0, fail = 0;

	static IntPredicate constantZero() { return x -> false; }
	static IntPredicate constantOne() { return x -> true; }
	static IntPredicate parity() { return x -> (Integer.bitCount(x) % 2) == 1; }

	public static void main(String[] args) {
		// 1. No-op channel (p=0): probabilityAllZero() matches DeutschJozsa.probabilityAllZero()
		//    EXACTLY, for constant and balanced f, several n, every possible noisyQubit.
		boolean noOpOk = true;
		for (int n = 1; n <= 4; ++n) {
			for (IntPredicate f : new IntPredicate[] { constantZero(), constantOne(), parity() }) {
				double expected = DeutschJozsa.probabilityAllZero(f, n);
				for (int noisyQubit = 0; noisyQubit <= n; ++noisyQubit) {
					double p = NoisyDeutschJozsa.probabilityAllZero(f, n, Decoherence.bitFlip(0.0), noisyQubit);
					if (Math.abs(p - expected) > 1e-9) { noOpOk = false; }
				}
			}
		}
		check("no-op channel matches DeutschJozsa.probabilityAllZero() exactly, 3 f x n=1..4 x every qubit", noOpOk);

		// 2. Ancilla-noise ROBUSTNESS of a constant f: probabilityAllZero() stays EXACTLY 1.0 for a
		//    constant f no matter how strongly the ANCILLA is noised -- for every channel family,
		//    even amplitudeDamping(1.0) (which forces the ancilla to a completely different state,
		//    |1>->|0>). Makes sense once you see WHY: a constant f never entangles the ancilla with
		//    the input register in the first place (the oracle acts identically for every x), so
		//    H^n always exactly undoes the initial H^n on the input register regardless of what
		//    state the (never-entangled, later marginalized-away) ancilla was in.
		boolean ancillaRobustForConstantOk = true;
		int n3 = 3;
		for (IntPredicate f : new IntPredicate[] { constantZero(), constantOne() }) {
			for (double p : new double[] { 0.0, 0.25, 0.5, 0.75, 1.0 }) {
				if (Math.abs(NoisyDeutschJozsa.probabilityAllZero(f, n3, Decoherence.bitFlip(p), n3) - 1.0) > 1e-9) { ancillaRobustForConstantOk = false; }
				if (Math.abs(NoisyDeutschJozsa.probabilityAllZero(f, n3, Decoherence.depolarizing(p), n3) - 1.0) > 1e-9) { ancillaRobustForConstantOk = false; }
				if (Math.abs(NoisyDeutschJozsa.probabilityAllZero(f, n3, Decoherence.amplitudeDamping(p), n3) - 1.0) > 1e-9) { ancillaRobustForConstantOk = false; }
			}
		}
		check("constant-f probabilityAllZero()==1.0 EXACTLY under ANY ancilla noise, any strength, 3 channels", ancillaRobustForConstantOk);

		// 3. ...but ancilla noise DOES degrade a BALANCED f, and does so with a clean linear closed
		//    form for 2 of the channel families tested: depolarizing(p) on the ancilla gives
		//    EXACTLY p/2, amplitudeDamping(gamma) on the ancilla gives EXACTLY gamma.
		boolean balancedAncillaOk = true;
		for (double p : new double[] { 0.0, 0.25, 0.5, 0.75, 1.0 }) {
			double viaDepolarizing = NoisyDeutschJozsa.probabilityAllZero(parity(), n3, Decoherence.depolarizing(p), n3);
			if (Math.abs(viaDepolarizing - p / 2.0) > 1e-9) { balancedAncillaOk = false; }
			double viaAmpDamp = NoisyDeutschJozsa.probabilityAllZero(parity(), 2, Decoherence.amplitudeDamping(p), 2);
			if (Math.abs(viaAmpDamp - p) > 1e-9) { balancedAncillaOk = false; }
		}
		check("balanced-f probabilityAllZero() under ancilla noise matches exact closed forms (p/2, gamma)", balancedAncillaOk);

		// 4. Noise on an INPUT qubit degrades a CONSTANT f -- unlike the ancilla, input qubits are
		//    part of what gets measured. bitFlip(p) on a single input qubit gives the exact closed
		//    form probabilityAllZero()=1-p (that qubit alone controls whether the all-zero outcome
		//    is even possible).
		boolean inputDegradesConstantOk = true;
		for (double p : new double[] { 0.0, 0.25, 0.5, 0.75, 1.0 }) {
			double prob = NoisyDeutschJozsa.probabilityAllZero(constantOne(), n3, Decoherence.bitFlip(p), 0);
			if (Math.abs(prob - (1.0 - p)) > 1e-9) { inputDegradesConstantOk = false; }
		}
		check("bitFlip(p) on an input qubit gives constant-f probabilityAllZero()=1-p EXACTLY", inputDegradesConstantOk);

		// 5. amplitudeDamping/phaseFlip on an input qubit have ZERO effect, for any f -- input
		//    qubits start EXACTLY at |0>, the fixed point of both those channels (E0|0>=|0>,
		//    E1|0>=0 for amplitudeDamping; Z|0>=|0> for phaseFlip), so the channel simply cannot
		//    perturb them no matter how strong. A genuine invariant, not a missing degradation bug.
		boolean fixedPointOk = true;
		for (IntPredicate f : new IntPredicate[] { constantZero(), constantOne(), parity() }) {
			double baseline = DeutschJozsa.probabilityAllZero(f, n3);
			for (double p : new double[] { 0.3, 0.6, 1.0 }) {
				if (Math.abs(NoisyDeutschJozsa.probabilityAllZero(f, n3, Decoherence.amplitudeDamping(p), 0) - baseline) > 1e-9) { fixedPointOk = false; }
				if (Math.abs(NoisyDeutschJozsa.probabilityAllZero(f, n3, Decoherence.phaseFlip(p), 0) - baseline) > 1e-9) { fixedPointOk = false; }
			}
		}
		check("amplitudeDamping/phaseFlip on an input qubit (starts at |0>, their fixed point) have ZERO effect", fixedPointOk);

		// 6. probabilityAllZero() stays within [0,1] even under maximal noise, several f/n/qubit
		boolean boundedOk = true;
		for (int n = 1; n <= 3; ++n) {
			for (IntPredicate f : new IntPredicate[] { constantZero(), parity() }) {
				for (int noisyQubit = 0; noisyQubit <= n; ++noisyQubit) {
					double p = NoisyDeutschJozsa.probabilityAllZero(f, n, Decoherence.depolarizing(1.0), noisyQubit);
					if (p < -1e-9 || p > 1.0 + 1e-9) { boundedOk = false; }
				}
			}
		}
		check("probabilityAllZero() stays in [0,1] under maximal noise, 2 f x n=1..3 x every qubit", boundedOk);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
