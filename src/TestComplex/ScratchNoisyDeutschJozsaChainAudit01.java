package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.NoisyDeutschJozsa;
import java.util.function.IntPredicate;

/**
 * Audit of NoisyDeutschJozsa.circuitDensityMatrixChain()/probabilityAllZeroChain() -- the first
 * real consumer of Decoherence.applyChain() inside a Noisy* class (the "encadenar canales dentro
 * de un Noisy* concreto" candidate carried over from the block that added applyChain() itself).
 * <p>
 * Central finding (probed numerically in ProbeDJChain.java, /tmp, not committed, before writing
 * any assertion): the ancilla-invariance NoisyDeutschJozsa already had for a CONSTANT f (single
 * channel on the ancilla never moves probabilityAllZero() away from EXACTLY 1.0, see
 * ScratchNoisyDeutschJozsaAudit01 check 2) generalizes cleanly to an entire CHAIN of several
 * different channels all applied to the ancilla -- still EXACTLY 1.0, no matter how many channels
 * or how strong. Makes sense from the same root cause as the single-channel case: a constant f
 * never entangles the ancilla with the input register regardless of what state the ancilla is in
 * (pure, mixed, or however it got there), so chaining more noise onto an already-noisy ancilla
 * changes nothing about the argument.
 */
public class ScratchNoisyDeutschJozsaChainAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		int n = 3;
		IntPredicate constante = x -> false;
		IntPredicate paridad = x -> (Integer.bitCount(x) % 2) == 1;

		// 1. A 1-element chain matches circuitDensityMatrix()/probabilityAllZero() (the single-
		//    channel case) EXACTLY -- several channels/probabilities/qubits.
		boolean bridgeOk = true;
		for (int noisyQubit = 0; noisyQubit <= n; ++noisyQubit) {
			for (double p : new double[] { 0.0, 0.3, 0.7, 1.0 }) {
				double single = NoisyDeutschJozsa.probabilityAllZero(paridad, n, Decoherence.depolarizing(p), noisyQubit);
				double chain1 = NoisyDeutschJozsa.probabilityAllZeroChain(paridad, n,
						new MatrixComplex[][] { Decoherence.depolarizing(p) }, new int[] { noisyQubit });
				if (Math.abs(single - chain1) > 1e-9) { bridgeOk = false; }
			}
		}
		check("a 1-element chain matches the single-channel probabilityAllZero() exactly, every qubit/p", bridgeOk);

		// 2. A CHAIN of several DIFFERENT channels, all on the ancilla, leaves a CONSTANT f's
		//    probabilityAllZero() at EXACTLY 1.0 -- the single-channel ancilla invariant generalizes
		//    to an entire chain, not just 1 channel.
		boolean chainAncillaInvariantOk = true;
		for (double p1 : new double[] { 0.2, 0.6 }) {
			for (double p2 : new double[] { 0.3, 0.8 }) {
				double result = NoisyDeutschJozsa.probabilityAllZeroChain(constante, n,
						new MatrixComplex[][] { Decoherence.depolarizing(p1), Decoherence.amplitudeDamping(p2), Decoherence.bitFlip(0.5) },
						new int[] { n, n, n });
				if (Math.abs(result - 1.0) > 1e-9) { chainAncillaInvariantOk = false; }
			}
		}
		check("constant-f probabilityAllZeroChain()==1.0 EXACTLY for a chain of 3 different channels on the ancilla", chainAncillaInvariantOk);

		// 3. Chaining EXTRA noise on the ancilla, after noise on an input qubit, changes NOTHING for
		//    a constant f -- the input-qubit noise alone already determines the result, exactly as
		//    if the ancilla chain link weren't there at all.
		boolean extraAncillaNoOpOk = true;
		for (double pInput : new double[] { 0.1, 0.4 }) {
			double withAncillaChain = NoisyDeutschJozsa.probabilityAllZeroChain(constante, n,
					new MatrixComplex[][] { Decoherence.bitFlip(pInput), Decoherence.depolarizing(0.9) },
					new int[] { 0, n });
			double inputOnly = NoisyDeutschJozsa.probabilityAllZero(constante, n, Decoherence.bitFlip(pInput), 0);
			if (Math.abs(withAncillaChain - inputOnly) > 1e-9) { extraAncillaNoOpOk = false; }
		}
		check("chaining extra ancilla noise after input-qubit noise changes nothing for a constant f", extraAncillaNoOpOk);

		// 4. probabilityAllZeroChain() stays in [0,1] under a long, strong, mixed-family chain.
		boolean boundedOk = true;
		for (double p : new double[] { 0.0, 0.5, 1.0 }) {
			double result = NoisyDeutschJozsa.probabilityAllZeroChain(paridad, n,
					new MatrixComplex[][] { Decoherence.bitFlip(p), Decoherence.phaseFlip(p), Decoherence.amplitudeDamping(p), Decoherence.depolarizing(p) },
					new int[] { 0, 1, 2, n });
			if (result < -1e-9 || result > 1.0 + 1e-9) { boundedOk = false; }
		}
		check("probabilityAllZeroChain() stays in [0,1] under a 4-link mixed-family chain", boundedOk);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
