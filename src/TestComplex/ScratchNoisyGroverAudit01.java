package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.Grover;
import com.ipserc.arith.quantum.NoisyGrover;

/**
 * Audit of NoisyGrover.java -- combines Decoherence with Grover, fourth cross-exercise
 * combination candidate, after NoisyTeleportation/NoisyDeutschJozsa/NoisyBernsteinVazirani. Noise
 * is injected ONCE at state prep (before any oracle/diffusion round), a deliberate choice
 * (confirmed with the user) over "noise every iteration" -- see NoisyGrover's class doc.
 * <p>
 * The checks below were derived by actually probing the numbers first (Probe03..Probe05, scratch
 * dir, not committed) -- 2 findings were genuinely surprising and NOT assumed by analogy with
 * NoisyDeutschJozsa/NoisyBernsteinVazirani: (1) bitFlip has EXACTLY ZERO effect on search(),
 * regardless of target/qubit/probability -- because Grover's initial state is the UNIFORM
 * superposition |+>^n, and |+> is the exact fixed-point eigenstate of X (X|+>=|+>), so a bit-flip
 * channel leaves rho_q=|+><+| completely unchanged no matter how strong; (2) amplitudeDamping is
 * the ONLY channel among the 4 whose effect depends on WHICH basis state -- specifically on
 * target's bit at noisyQubit (0 or 1), because amplitudeDamping is the only channel here that
 * isn't symmetric under |0><->|1>.
 */
public class ScratchNoisyGroverAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		// 1. No-op channel (p=0) matches Grover.search() EXACTLY, several n/targets/qubits.
		boolean noOpOk = true;
		for (int n = 2; n <= 4; ++n) {
			int dim = 1 << n;
			for (int target = 0; target < dim; ++target) {
				double expected = Grover.search(target, n);
				for (int noisyQubit = 0; noisyQubit < n; ++noisyQubit) {
					double p = NoisyGrover.search(target, n, Decoherence.bitFlip(0.0), noisyQubit);
					if (Math.abs(p - expected) > 1e-9) { noOpOk = false; }
				}
			}
		}
		check("no-op channel matches Grover.search() exactly, n=2..4 x every target x every qubit", noOpOk);

		// 2. bitFlip has EXACTLY ZERO effect on search(), ANY probability, ANY target/qubit -- the
		//    uniform superposition |+>^n is the exact fixed point of a bit-flip channel on every
		//    qubit (X|+>=|+>), so this holds regardless of what's marked.
		boolean bitFlipInvariantOk = true;
		int n4 = 4;
		for (int target = 0; target < (1 << n4); ++target) {
			double baseline = Grover.search(target, n4);
			for (int noisyQubit = 0; noisyQubit < n4; ++noisyQubit) {
				for (double p : new double[] { 0.2, 0.5, 0.9, 1.0 }) {
					double bit = NoisyGrover.search(target, n4, Decoherence.bitFlip(p), noisyQubit);
					if (Math.abs(bit - baseline) > 1e-9) { bitFlipInvariantOk = false; }
				}
			}
		}
		check("bitFlip has EXACTLY ZERO effect on search(), every target x qubit x probability (fixed point of |+>)", bitFlipInvariantOk);

		// 3. depolarizing(p)/phaseFlip(p) degrade search() UNIFORMLY -- the resulting probability is
		//    the SAME regardless of which target or which qubit is noised (both channels ARE
		//    symmetric under |0><->|1>, and the uniform superposition treats every qubit/basis state
		//    alike), and decreases monotonically as p grows.
		boolean uniformDegradationOk = true;
		for (double p : new double[] { 0.1, 0.25, 0.5, 0.75, 1.0 }) {
			Double depReference = null, phaseReference = null;
			for (int target = 0; target < (1 << n4); ++target) {
				for (int noisyQubit = 0; noisyQubit < n4; ++noisyQubit) {
					double dep = NoisyGrover.search(target, n4, Decoherence.depolarizing(p), noisyQubit);
					double phase = NoisyGrover.search(target, n4, Decoherence.phaseFlip(p), noisyQubit);
					if (depReference == null) { depReference = dep; } else if (Math.abs(dep - depReference) > 1e-9) { uniformDegradationOk = false; }
					if (phaseReference == null) { phaseReference = phase; } else if (Math.abs(phase - phaseReference) > 1e-9) { uniformDegradationOk = false; }
				}
			}
		}
		double prevDep = 1.0, prevPhase = 1.0;
		for (double p = 0.0; p <= 1.0 + 1e-9; p += 0.1) {
			double dep = NoisyGrover.search(5, n4, Decoherence.depolarizing(p), 0);
			double phase = NoisyGrover.search(5, n4, Decoherence.phaseFlip(p), 0);
			if (dep > prevDep + 1e-9 || phase > prevPhase + 1e-9) { uniformDegradationOk = false; }
			prevDep = dep; prevPhase = phase;
		}
		check("depolarizing/phaseFlip degrade search() uniformly (independent of target/qubit) and monotonically in p", uniformDegradationOk);

		// 4. amplitudeDamping's effect depends ONLY on target's bit at noisyQubit (0 or 1) -- the
		//    ONE channel among the 4 not symmetric under |0><->|1>. Same probability for every
		//    target/qubit pair sharing the same bit value there, a DIFFERENT probability for the
		//    opposite bit value, for every (target,qubit) pair tested (exhaustive over n=4).
		boolean ampDampBitDependentOk = true;
		Double bit0Reference = null, bit1Reference = null;
		double gamma = 1.0;
		for (int target = 0; target < (1 << n4); ++target) {
			for (int noisyQubit = 0; noisyQubit < n4; ++noisyQubit) {
				int bitAtQubit = (target >> (n4 - 1 - noisyQubit)) & 1;
				double amp = NoisyGrover.search(target, n4, Decoherence.amplitudeDamping(gamma), noisyQubit);
				if (bitAtQubit == 0) {
					if (bit0Reference == null) { bit0Reference = amp; } else if (Math.abs(amp - bit0Reference) > 1e-9) { ampDampBitDependentOk = false; }
				} else {
					if (bit1Reference == null) { bit1Reference = amp; } else if (Math.abs(amp - bit1Reference) > 1e-9) { ampDampBitDependentOk = false; }
				}
			}
		}
		if (bit0Reference == null || bit1Reference == null || Math.abs(bit0Reference - bit1Reference) < 1e-6) { ampDampBitDependentOk = false; }
		check("amplitudeDamping(1.0) gives 1 of exactly 2 values, keyed ONLY by target's bit at noisyQubit", ampDampBitDependentOk);

		// 5. Probability conservation: the full diagonal of circuitDensityMatrix() sums to 1, several
		//    channels/targets/qubits -- sanity check that the density-matrix machinery reused from
		//    Decoherence/DensityMatrix still describes a valid distribution after several conjugated
		//    Grover rounds.
		boolean conservedOk = true;
		for (int target : new int[] { 0, 5, 10, 15 }) {
			for (double p : new double[] { 0.0, 0.5, 1.0 }) {
				MatrixComplex rho = NoisyGrover.circuitDensityMatrix(target, n4, Grover.optimalIterations(n4), Decoherence.depolarizing(p), 1);
				double sum = 0.0;
				for (int x = 0; x < (1 << n4); ++x) {
					sum += NoisyGrover.probabilityOfTarget(rho, x);
				}
				if (Math.abs(sum - 1.0) > 1e-9) { conservedOk = false; }
			}
		}
		check("circuitDensityMatrix() diagonal sums to 1, several targets/probabilities", conservedOk);

		// 6. search() stays in [0,1] under maximal noise, several channels/targets/qubits.
		boolean boundedOk = true;
		for (int target : new int[] { 0, 3, 7, 15 }) {
			for (int noisyQubit = 0; noisyQubit < n4; ++noisyQubit) {
				for (MatrixComplex[] kraus : new MatrixComplex[][] { Decoherence.depolarizing(1.0), Decoherence.amplitudeDamping(1.0), Decoherence.phaseFlip(1.0) }) {
					double p = NoisyGrover.search(target, n4, kraus, noisyQubit);
					if (p < -1e-9 || p > 1.0 + 1e-9) { boundedOk = false; }
				}
			}
		}
		check("search() stays in [0,1] under maximal noise, 3 channels x every target x every qubit", boundedOk);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
