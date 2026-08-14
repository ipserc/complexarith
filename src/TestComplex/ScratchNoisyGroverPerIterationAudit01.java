package TestComplex;

import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.Grover;
import com.ipserc.arith.quantum.NoisyGrover;

/**
 * Audit of NoisyGrover.searchPerIteration()/circuitDensityMatrixPerIteration() -- the "ruido por
 * iteracion" model deliberately left out of the previous Grover+noise block in favor of the
 * single-dose model, now added as an explicit alternative (both stay available, neither replaces
 * the other).
 * <p>
 * The central finding here (probed numerically in Probe/ScratchGroverPerIter*.java, /tmp, not
 * committed, before writing any assertion): the per-iteration model is NOT just "the single-dose
 * degradation, but worse" -- it is QUALITATIVELY different for bitFlip specifically. Under the
 * single-dose model bitFlip is an EXACT invariant of search() (see ScratchNoisyGroverAudit01,
 * check 2: the uniform superposition |+>^n is X's fixed point). Under the per-iteration model
 * that invariance is GONE: after the first oracle+diffusion round, a single qubit's marginal state
 * is no longer |+>, so a bit-flip channel re-applied every round DOES measurably degrade the
 * search -- but, surprisingly, still UNIFORMLY across every target (same value for every target
 * tested, exactly like depolarizing/phaseFlip already were under the single-dose model), not
 * target-dependent the way amplitudeDamping is.
 */
public class ScratchNoisyGroverPerIterationAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		int n = 4;
		int dim = 1 << n;

		// 1. No-op channel (p=0) matches Grover.search() EXACTLY, every target/qubit -- same bridge
		//    check every Noisy* class in this package starts with.
		boolean noOpOk = true;
		for (int target = 0; target < dim; ++target) {
			double expected = Grover.search(target, n);
			for (int noisyQubit = 0; noisyQubit < n; ++noisyQubit) {
				double p = NoisyGrover.searchPerIteration(target, n, Decoherence.bitFlip(0.0), noisyQubit);
				if (Math.abs(p - expected) > 1e-9) { noOpOk = false; }
			}
		}
		check("no-op channel matches Grover.search() exactly, every target x qubit", noOpOk);

		// 2. bitFlip per-iteration is NO LONGER invariant (unlike the single-dose model) -- it DOES
		//    degrade search(), strictly below the noiseless baseline, for every target tested.
		boolean bitFlipDegradesOk = true;
		for (int target : new int[] { 0, 5, 10, 15 }) {
			double baseline = Grover.search(target, n);
			for (double p : new double[] { 0.1, 0.3, 0.6 }) {
				double perIter = NoisyGrover.searchPerIteration(target, n, Decoherence.bitFlip(p), 0);
				if (perIter >= baseline - 1e-9) { bitFlipDegradesOk = false; }
			}
		}
		check("bitFlip per-iteration DOES degrade search() (unlike the single-dose invariant), every target", bitFlipDegradesOk);

		// 3. ...but that degradation is still UNIFORM across every target -- the same value for a
		//    fixed p/qubit regardless of which target is being searched, exactly like
		//    depolarizing/phaseFlip already were under the single-dose model.
		boolean bitFlipUniformOk = true;
		Double bitFlipReference = null;
		for (int target = 0; target < dim; ++target) {
			double perIter = NoisyGrover.searchPerIteration(target, n, Decoherence.bitFlip(0.3), 0);
			if (bitFlipReference == null) { bitFlipReference = perIter; }
			else if (Math.abs(perIter - bitFlipReference) > 1e-9) { bitFlipUniformOk = false; }
		}
		check("bitFlip per-iteration degradation is UNIFORM across every target, for a fixed p/qubit", bitFlipUniformOk);

		// 4. depolarizing/phaseFlip per-iteration stay uniform across target/qubit too (same
		//    property they already had under the single-dose model, verified again here since the
		//    per-iteration circuit is genuinely different code).
		boolean depPhaseUniformOk = true;
		Double depReference = null, phaseReference = null;
		for (int target = 0; target < dim; ++target) {
			for (int noisyQubit = 0; noisyQubit < n; ++noisyQubit) {
				double dep = NoisyGrover.searchPerIteration(target, n, Decoherence.depolarizing(0.1), noisyQubit);
				double phase = NoisyGrover.searchPerIteration(target, n, Decoherence.phaseFlip(0.1), noisyQubit);
				if (depReference == null) { depReference = dep; } else if (Math.abs(dep - depReference) > 1e-9) { depPhaseUniformOk = false; }
				if (phaseReference == null) { phaseReference = phase; } else if (Math.abs(phase - phaseReference) > 1e-9) { depPhaseUniformOk = false; }
			}
		}
		check("depolarizing/phaseFlip per-iteration stay uniform across target x qubit", depPhaseUniformOk);

		// 5. amplitudeDamping per-iteration STILL depends only on target's bit at noisyQubit (2
		//    exact values) -- the same qualitative shape the single-dose model has, still true once
		//    the noise is re-applied every round instead of once.
		boolean ampDampBitDependentOk = true;
		Double bit0Reference = null, bit1Reference = null;
		for (int target = 0; target < dim; ++target) {
			for (int noisyQubit = 0; noisyQubit < n; ++noisyQubit) {
				int bitAtQubit = (target >> (n - 1 - noisyQubit)) & 1;
				double amp = NoisyGrover.searchPerIteration(target, n, Decoherence.amplitudeDamping(0.3), noisyQubit);
				if (bitAtQubit == 0) {
					if (bit0Reference == null) { bit0Reference = amp; } else if (Math.abs(amp - bit0Reference) > 1e-9) { ampDampBitDependentOk = false; }
				} else {
					if (bit1Reference == null) { bit1Reference = amp; } else if (Math.abs(amp - bit1Reference) > 1e-9) { ampDampBitDependentOk = false; }
				}
			}
		}
		if (bit0Reference == null || bit1Reference == null || Math.abs(bit0Reference - bit1Reference) < 1e-6) { ampDampBitDependentOk = false; }
		check("amplitudeDamping per-iteration still gives exactly 2 values, keyed only by target's bit at noisyQubit", ampDampBitDependentOk);

		// 6. Per-iteration degradation is STRICTLY worse than the single-dose model, same channel/
		//    probability/target/qubit -- the noise had more chances to act (once per round instead
		//    of once total), consistent with "more exposure to a noisy environment over time".
		boolean worseThanSingleDoseOk = true;
		for (int target : new int[] { 0, 5, 15 }) {
			for (double p : new double[] { 0.1, 0.2, 0.3 }) {
				double perIter = NoisyGrover.searchPerIteration(target, n, Decoherence.depolarizing(p), 0);
				double singleDose = NoisyGrover.search(target, n, Decoherence.depolarizing(p), 0);
				if (perIter >= singleDose - 1e-9) { worseThanSingleDoseOk = false; }
			}
		}
		check("per-iteration degradation is strictly worse than single-dose, same channel/p/target/qubit", worseThanSingleDoseOk);

		// 7. search() stays in [0,1] under maximal noise, several channels/targets/qubits.
		boolean boundedOk = true;
		for (int target : new int[] { 0, 3, 7, 15 }) {
			for (int noisyQubit = 0; noisyQubit < n; ++noisyQubit) {
				for (com.ipserc.arith.matrixcomplex.MatrixComplex[] kraus : new com.ipserc.arith.matrixcomplex.MatrixComplex[][] {
						Decoherence.depolarizing(1.0), Decoherence.amplitudeDamping(1.0), Decoherence.phaseFlip(1.0), Decoherence.bitFlip(1.0) }) {
					double p = NoisyGrover.searchPerIteration(target, n, kraus, noisyQubit);
					if (p < -1e-9 || p > 1.0 + 1e-9) { boundedOk = false; }
				}
			}
		}
		check("searchPerIteration() stays in [0,1] under maximal noise, 4 channels x every target x every qubit", boundedOk);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
