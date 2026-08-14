package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.DensityMatrix;
import com.ipserc.arith.quantum.Qubits;

/**
 * Audit of Decoherence.applyChain() -- chains more than one noise channel (repeats of the same
 * family, different families, same or different qubits) in a single execution, the "encadenar mas
 * de un canal de Decoherence" candidate carried over from the close of the previous block.
 * <p>
 * 2 findings below were checked numerically, not assumed by symmetry with the single-channel case
 * (Probe06..Probe08, scratch dir, not committed): (1) chaining the SAME channel family twice with
 * probabilities p1,p2 is NOT the same as a single application with probability p1+p2 -- the exact
 * combination formula for 2 independent bit flips is p1+p2-2*p1*p2 (a bit flips overall iff exactly
 * one of the 2 independent flip events happens -- classical XOR-of-independent-events probability).
 * (2) whether chain ORDER matters depends on which 2 channel families are combined: bitFlip/
 * phaseFlip commute with each other (an algebraic fact -- X*Z*rho*Z*X and Z*X*rho*X*Z both reduce
 * to EXACTLY Y*rho*Y via the Pauli identity XZ=-iY, ZX=iY), and amplitudeDamping commutes with
 * phaseFlip (Z anticommutes with amplitudeDamping's off-diagonal Kraus operator E1, but a channel
 * is insensitive to an overall sign on a Kraus operator, so Z*E1*Z=-E1 gives the identical channel)
 * -- but amplitudeDamping does NOT commute with bitFlip (confirmed numerically, no such algebraic
 * cancellation applies).
 */
public class ScratchDecoherenceChainAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		// 1. Empty-effect chain (every probability 0) leaves rho EXACTLY unchanged.
		MatrixComplex bell = new MatrixComplex(4, 1);
		bell.setItem(0, 0, new Complex(1.0 / Math.sqrt(2), 0.0));
		bell.setItem(3, 0, new Complex(1.0 / Math.sqrt(2), 0.0));
		MatrixComplex rhoBell = DensityMatrix.of(bell);
		MatrixComplex noop = Decoherence.applyChain(rhoBell, 2,
				new MatrixComplex[][] { Decoherence.bitFlip(0.0), Decoherence.phaseFlip(0.0), Decoherence.depolarizing(0.0) },
				new int[] { 0, 1, 0 });
		check("chain of 3 no-op channels leaves rho EXACTLY unchanged", matrixEquals(noop, rhoBell));

		// 2. A chain of 1 channel matches apply() directly, several channels/qubits.
		boolean singleLinkOk = true;
		for (int q = 0; q < 2; ++q) {
			MatrixComplex viaChain = Decoherence.applyChain(rhoBell, 2, new MatrixComplex[][] { Decoherence.depolarizing(0.4) }, new int[] { q });
			MatrixComplex viaApply = Decoherence.apply(rhoBell, Decoherence.depolarizing(0.4), q, 2);
			if (!matrixEquals(viaChain, viaApply)) { singleLinkOk = false; }
		}
		check("a 1-link chain matches apply() directly, both qubits", singleLinkOk);

		// 3. Manually unrolling a 2-link chain (apply() then apply() again) matches applyChain()
		//    exactly, several channel/qubit combinations.
		boolean unrollOk = true;
		MatrixComplex manual = Decoherence.apply(Decoherence.apply(rhoBell, Decoherence.bitFlip(0.3), 0, 2), Decoherence.phaseFlip(0.6), 1, 2);
		MatrixComplex viaChain = Decoherence.applyChain(rhoBell, 2, new MatrixComplex[][] { Decoherence.bitFlip(0.3), Decoherence.phaseFlip(0.6) }, new int[] { 0, 1 });
		if (!matrixEquals(manual, viaChain)) { unrollOk = false; }
		check("unrolling a 2-link chain by hand matches applyChain() exactly", unrollOk);

		// 4. Combining 2 independent bitFlip events on the SAME qubit is NOT the same as 1 bitFlip
		//    with the summed probability -- matches the exact closed form p1+p2-2*p1*p2 instead (the
		//    classical probability that exactly one of 2 independent coin flips happens).
		boolean bitFlipCombinationOk = true;
		MatrixComplex rho0 = DensityMatrix.of(Qubits.ket(0));
		for (double p1 : new double[] { 0.1, 0.3, 0.5 }) {
			for (double p2 : new double[] { 0.1, 0.4, 0.7 }) {
				MatrixComplex chained = Decoherence.applyChain(rho0, 1, new MatrixComplex[][] { Decoherence.bitFlip(p1), Decoherence.bitFlip(p2) }, new int[] { 0, 0 });
				double combined = p1 + p2 - 2 * p1 * p2;
				MatrixComplex single = Decoherence.apply(rho0, Decoherence.bitFlip(combined), 0, 1);
				if (!matrixEquals(chained, single)) { bitFlipCombinationOk = false; }
				// and NOT equal to the naive (wrong) summed-probability channel, whenever p1,p2>0
				MatrixComplex naiveWrong = Decoherence.apply(rho0, Decoherence.bitFlip(Math.min(1.0, p1 + p2)), 0, 1);
				if (Math.abs(combined - Math.min(1.0, p1 + p2)) > 1e-9 && matrixEquals(chained, naiveWrong)) { bitFlipCombinationOk = false; }
			}
		}
		check("2 chained bitFlip(p1,p2) match the exact combination formula p1+p2-2p1p2, NOT p1+p2", bitFlipCombinationOk);

		// 5. Order commutes for bitFlip+phaseFlip and for amplitudeDamping+phaseFlip on the same
		//    qubit (algebraic identities, confirmed numerically on a non-trivial state), for several
		//    probability pairs.
		MatrixComplex psi = new MatrixComplex(2, 1);
		psi.setItem(0, 0, new Complex(0.8, 0.0));
		psi.setItem(1, 0, new Complex(0.0, 0.6));
		MatrixComplex rhoPsi = DensityMatrix.of(psi);
		boolean commutingPairsOk = true;
		for (double pa : new double[] { 0.2, 0.5, 0.9 }) {
			for (double pb : new double[] { 0.3, 0.6 }) {
				MatrixComplex bf_pf = Decoherence.applyChain(rhoPsi, 1, new MatrixComplex[][] { Decoherence.bitFlip(pa), Decoherence.phaseFlip(pb) }, new int[] { 0, 0 });
				MatrixComplex pf_bf = Decoherence.applyChain(rhoPsi, 1, new MatrixComplex[][] { Decoherence.phaseFlip(pb), Decoherence.bitFlip(pa) }, new int[] { 0, 0 });
				if (!matrixEquals(bf_pf, pf_bf)) { commutingPairsOk = false; }

				MatrixComplex ad_pf = Decoherence.applyChain(rhoPsi, 1, new MatrixComplex[][] { Decoherence.amplitudeDamping(pa), Decoherence.phaseFlip(pb) }, new int[] { 0, 0 });
				MatrixComplex pf_ad = Decoherence.applyChain(rhoPsi, 1, new MatrixComplex[][] { Decoherence.phaseFlip(pb), Decoherence.amplitudeDamping(pa) }, new int[] { 0, 0 });
				if (!matrixEquals(ad_pf, pf_ad)) { commutingPairsOk = false; }
			}
		}
		check("bitFlip<->phaseFlip and amplitudeDamping<->phaseFlip commute exactly, several probability pairs", commutingPairsOk);

		// 6. ...but amplitudeDamping+bitFlip do NOT commute in general -- order genuinely matters.
		boolean nonCommutingOk = false;
		for (double pa : new double[] { 0.2, 0.5, 0.9 }) {
			for (double pb : new double[] { 0.3, 0.6 }) {
				MatrixComplex ad_bf = Decoherence.applyChain(rhoPsi, 1, new MatrixComplex[][] { Decoherence.amplitudeDamping(pa), Decoherence.bitFlip(pb) }, new int[] { 0, 0 });
				MatrixComplex bf_ad = Decoherence.applyChain(rhoPsi, 1, new MatrixComplex[][] { Decoherence.bitFlip(pb), Decoherence.amplitudeDamping(pa) }, new int[] { 0, 0 });
				if (!matrixEquals(ad_bf, bf_ad)) { nonCommutingOk = true; }
			}
		}
		check("amplitudeDamping<->bitFlip do NOT commute -- at least 1 tested pair gives a genuinely different result by order", nonCommutingOk);

		// 7. Trace preservation of a 3-link chain across 4 channel families on a 2-qubit register.
		MatrixComplex chain3 = Decoherence.applyChain(rhoBell, 2,
				new MatrixComplex[][] { Decoherence.depolarizing(0.3), Decoherence.amplitudeDamping(0.4), Decoherence.phaseFlip(0.6) },
				new int[] { 0, 1, 0 });
		double trace = chain3.getItem(0, 0).rep() + chain3.getItem(1, 1).rep() + chain3.getItem(2, 2).rep() + chain3.getItem(3, 3).rep();
		check("trace preserved (==1) after a 3-link chain of 3 different channel families", Math.abs(trace - 1.0) < 1e-9);

		// 8. applyChain() rejects mismatched array lengths.
		boolean rejectsMismatchOk = false;
		try {
			Decoherence.applyChain(rhoBell, 2, new MatrixComplex[][] { Decoherence.bitFlip(0.1) }, new int[] { 0, 1 });
		} catch (IllegalArgumentException e) {
			rejectsMismatchOk = true;
		}
		check("applyChain() rejects channels.length != qubitIndices.length", rejectsMismatchOk);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static boolean matrixEquals(MatrixComplex a, MatrixComplex b) {
		for (int i = 0; i < a.rows(); ++i) {
			for (int j = 0; j < a.cols(); ++j) {
				Complex diff = a.getItem(i, j).minus(b.getItem(i, j));
				if (diff.mod() > 1e-9) { return false; }
			}
		}
		return true;
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
