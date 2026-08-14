package TestComplex;

import java.util.function.IntPredicate;

import com.ipserc.arith.quantum.BernsteinVazirani;
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.NoisyBernsteinVazirani;

/**
 * Audit of NoisyBernsteinVazirani.java -- combines Decoherence with BernsteinVazirani, third
 * cross-exercise combination candidate, after NoisyTeleportation/NoisyDeutschJozsa.
 * <p>
 * The checks below were derived by actually probing the numbers first (Probe01/Probe02, scratch
 * dir, not committed), not assumed from the NoisyDeutschJozsa findings by symmetry -- some DO
 * carry over (ancilla noise is invisible to secret=0, which IS a constant f; input-qubit
 * amplitudeDamping/phaseFlip are always invisible, the |0> fixed-point property), but the
 * quantitative closed forms for a nonzero secret (checks 3/4) and the "where does the lost
 * probability go" question (check 3) are new findings specific to this class, confirmed
 * numerically before being written down as assertions here.
 */
public class ScratchNoisyBernsteinVaziraniAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		// 1. No-op channel (p=0): successProbability()==1.0 and findMostLikelySecret() matches
		//    BernsteinVazirani.findSecret() EXACTLY, several secrets x n.
		boolean noOpOk = true;
		for (int n = 1; n <= 4; ++n) {
			for (int secret = 0; secret < (1 << n); ++secret) {
				IntPredicate f = BernsteinVazirani.oracleFunction(secret, n);
				int expected = BernsteinVazirani.findSecret(f, n);
				for (int noisyQubit = 0; noisyQubit <= n; ++noisyQubit) {
					double success = NoisyBernsteinVazirani.successProbability(secret, n, Decoherence.bitFlip(0.0), noisyQubit);
					int guess = NoisyBernsteinVazirani.findMostLikelySecret(f, n, Decoherence.bitFlip(0.0), noisyQubit);
					if (Math.abs(success - 1.0) > 1e-9 || guess != expected || expected != secret) { noOpOk = false; }
				}
			}
		}
		check("no-op channel: successProbability()==1.0 and findMostLikelySecret()==secret exactly, n=1..4, every qubit", noOpOk);

		// 2. Ancilla-noise ROBUSTNESS for secret=0 -- oracleFunction(0,n) IS a constant f (f(x)=0 for
		//    every x), so it inherits DeutschJozsa's finding: ancilla noise never moves the result,
		//    any channel, any strength.
		boolean ancillaRobustForZeroSecretOk = true;
		int n3 = 3;
		for (double p : new double[] { 0.0, 0.25, 0.5, 0.75, 1.0 }) {
			if (Math.abs(NoisyBernsteinVazirani.successProbability(0, n3, Decoherence.depolarizing(p), n3) - 1.0) > 1e-9) { ancillaRobustForZeroSecretOk = false; }
			if (Math.abs(NoisyBernsteinVazirani.successProbability(0, n3, Decoherence.amplitudeDamping(p), n3) - 1.0) > 1e-9) { ancillaRobustForZeroSecretOk = false; }
		}
		check("secret=0 (a constant f): successProbability()==1.0 EXACTLY under ANY ancilla noise, any strength", ancillaRobustForZeroSecretOk);

		// 3. ...but ancilla noise DOES degrade a NONZERO secret, with exact closed forms depolarizing
		//    (p): 1-p/2, amplitudeDamping(gamma): 1-gamma -- and the ENTIRE lost probability mass
		//    lands on the single spurious outcome x=0, not spread over other x (verified directly by
		//    reading outcomeProbability(...,x=0), not inferred from successProbability alone).
		boolean nonzeroAncillaOk = true;
		for (int secret : new int[] { 1, 5, 7 }) {
			IntPredicate f = BernsteinVazirani.oracleFunction(secret, n3);
			for (double p : new double[] { 0.0, 0.25, 0.5, 0.75, 1.0 }) {
				double successDep = NoisyBernsteinVazirani.successProbability(secret, n3, Decoherence.depolarizing(p), n3);
				double zeroDep = NoisyBernsteinVazirani.outcomeProbability(f, n3, Decoherence.depolarizing(p), n3, 0);
				if (Math.abs(successDep - (1.0 - p / 2.0)) > 1e-9) { nonzeroAncillaOk = false; }
				if (Math.abs(zeroDep - p / 2.0) > 1e-9) { nonzeroAncillaOk = false; }

				double successAmp = NoisyBernsteinVazirani.successProbability(secret, n3, Decoherence.amplitudeDamping(p), n3);
				double zeroAmp = NoisyBernsteinVazirani.outcomeProbability(f, n3, Decoherence.amplitudeDamping(p), n3, 0);
				if (Math.abs(successAmp - (1.0 - p)) > 1e-9) { nonzeroAncillaOk = false; }
				if (Math.abs(zeroAmp - p) > 1e-9) { nonzeroAncillaOk = false; }
			}
		}
		check("nonzero-secret ancilla noise matches exact closed forms (1-p/2, 1-gamma), lost mass lands EXACTLY on x=0", nonzeroAncillaOk);

		// 4. Noise on an INPUT qubit degrades EVERY secret alike -- bitFlip(p) on any single input
		//    qubit gives successProbability()=1-p EXACTLY, regardless of the secret's bit at that
		//    position (0 or 1) and regardless of which of the n input qubits: bitFlip doesn't depend
		//    on the current value, so flipping any one input bit always costs exactly p of the
		//    outcome distribution to a wrong (bit-flipped) answer.
		boolean inputBitFlipOk = true;
		for (int secret : new int[] { 0, 1, 3, 5, 7 }) {
			for (int noisyQubit = 0; noisyQubit < n3; ++noisyQubit) {
				for (double p : new double[] { 0.0, 0.3, 0.6, 1.0 }) {
					double success = NoisyBernsteinVazirani.successProbability(secret, n3, Decoherence.bitFlip(p), noisyQubit);
					if (Math.abs(success - (1.0 - p)) > 1e-9) { inputBitFlipOk = false; }
				}
			}
		}
		check("bitFlip(p) on any single input qubit gives successProbability()=1-p EXACTLY, every secret", inputBitFlipOk);

		// 5. amplitudeDamping/phaseFlip on an input qubit have ZERO effect for ANY secret -- input
		//    qubits start EXACTLY at |0>, the fixed point of both channels (same invariant found for
		//    DeutschJozsa, here it holds independent of f entirely since it's purely a fact about the
		//    state BEFORE the oracle runs).
		boolean fixedPointOk = true;
		for (int secret : new int[] { 0, 1, 3, 5, 7 }) {
			for (int noisyQubit = 0; noisyQubit < n3; ++noisyQubit) {
				for (double p : new double[] { 0.3, 0.6, 1.0 }) {
					if (Math.abs(NoisyBernsteinVazirani.successProbability(secret, n3, Decoherence.amplitudeDamping(p), noisyQubit) - 1.0) > 1e-9) { fixedPointOk = false; }
					if (Math.abs(NoisyBernsteinVazirani.successProbability(secret, n3, Decoherence.phaseFlip(p), noisyQubit) - 1.0) > 1e-9) { fixedPointOk = false; }
				}
			}
		}
		check("amplitudeDamping/phaseFlip on an input qubit (fixed point |0>) have ZERO effect, every secret", fixedPointOk);

		// 6. findMostLikelySecret() still recovers the correct secret under substantial (but not
		//    total) ancilla noise, since the correct secret keeps the largest probability as long as
		//    the closed forms above stay above the spurious x=0 share (1-p/2 > p/2 for depolarizing
		//    whenever p<1, 1-gamma > gamma for amplitudeDamping whenever gamma<0.5) -- NOT tested at
		//    the exact boundary p=1/gamma=0.5 where the 2 shares tie and the guess becomes ambiguous
		//    (a genuine "signal destroyed" case, not a bug in findMostLikelySecret()).
		boolean robustGuessOk = true;
		for (int secret : new int[] { 1, 5, 7 }) {
			IntPredicate f = BernsteinVazirani.oracleFunction(secret, n3);
			if (NoisyBernsteinVazirani.findMostLikelySecret(f, n3, Decoherence.depolarizing(0.9), n3) != secret) { robustGuessOk = false; }
			if (NoisyBernsteinVazirani.findMostLikelySecret(f, n3, Decoherence.amplitudeDamping(0.4), n3) != secret) { robustGuessOk = false; }
		}
		check("findMostLikelySecret() still recovers the secret under substantial (sub-critical) ancilla noise", robustGuessOk);

		// 7. Probability conservation: outcomeProbability() over all x sums to 1, several channels/
		//    secrets/qubits -- a basic sanity check that circuitDensityMatrix()/diagonalProbability()
		//    (both reused from NoisyDeutschJozsa) still describe a valid probability distribution.
		boolean conservedOk = true;
		for (int secret : new int[] { 0, 1, 5, 7 }) {
			IntPredicate f = BernsteinVazirani.oracleFunction(secret, n3);
			for (double p : new double[] { 0.0, 0.5, 1.0 }) {
				double sum = 0.0;
				for (int x = 0; x < (1 << n3); ++x) {
					sum += NoisyBernsteinVazirani.outcomeProbability(f, n3, Decoherence.depolarizing(p), 1, x);
				}
				if (Math.abs(sum - 1.0) > 1e-9) { conservedOk = false; }
			}
		}
		check("outcomeProbability() sums to 1 over all x, several secrets/probabilities", conservedOk);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
