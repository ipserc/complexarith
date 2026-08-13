package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Grover;

/**
 * Audit of Grover.java (oracle()/diffusion()/run()/search()) -- third "algoritmo cuantico mas
 * grande" follow-up of the Rol Fisica/Mecanica Cuantica, after DeutschJozsa/BernsteinVazirani.
 */
public class ScratchGroverAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		// 1. oracle()/diffusion() are unitary, for several n and targets
		boolean unitaryOk = true;
		for (int n = 1; n <= 5; ++n) {
			MatrixComplex diffusionOp = Grover.diffusion(n);
			MatrixComplex identity = MatrixComplex.eye(1 << n);
			if (diffusionOp.adjoint().times(diffusionOp).minus(identity).norm() > 1e-9) { unitaryOk = false; }
			for (int target = 0; target < (1 << n); ++target) {
				MatrixComplex oracleOp = Grover.oracle(target, n);
				if (oracleOp.adjoint().times(oracleOp).minus(identity).norm() > 1e-9) { unitaryOk = false; }
			}
		}
		check("oracle()/diffusion() are unitary, n=1..5 (every target)", unitaryOk);

		// 2. initialState() is the uniform superposition: every amplitude has modulus 1/sqrt(N)
		boolean uniformOk = true;
		for (int n = 1; n <= 5; ++n) {
			MatrixComplex s = Grover.initialState(n);
			double expected = 1.0 / Math.sqrt(1 << n);
			for (int i = 0; i < (1 << n); ++i) {
				if (Math.abs(s.getItem(i, 0).mod() - expected) > 1e-9) { uniformOk = false; }
			}
		}
		check("initialState() is the uniform superposition, n=1..5", uniformOk);

		// 3. search() finds EVERY target with probability >=0.5, for n=2..6 -- the algorithm's
		//    headline result. (n capped at 6 -- MatrixComplex isn't optimized for dense arithmetic,
		//    and this loop already runs search() once per target; N=2 (n=1) is a genuine exception
		//    to ">=0.5", a rotation-angle degeneracy documented separately in check 7 below, so this
		//    loop starts at n=2 on purpose, where it doesn't apply.)
		boolean amplifiesOk = true;
		for (int n = 2; n <= 6; ++n) {
			int dim = 1 << n;
			for (int target = 0; target < dim; ++target) {
				if (Grover.search(target, n) < 0.5) { amplifiesOk = false; }
			}
		}
		check("search() finds EVERY target with probability >=0.5, n=2..6 (all targets)", amplifiesOk);

		// 3b. Quantitative advantage, spelled out for 1 concrete case: at n=6 (N=64), search() beats
		//    the classical baseline (1/64~=0.0156) by close to 2 orders of magnitude.
		double p64 = Grover.search(37, 6);
		double classicalBaseline64 = 1.0 / 64;
		check("search(37,6) beats the classical 1/64 baseline by >=50x", p64 > 50 * classicalBaseline64);

		// 4. Closed-form cross-check: measured probability after k iterations matches
		//    sin^2((2k+1)*asin(1/sqrt(N))) EXACTLY (to floating-point precision), for several
		//    n/target/iteration-count combinations -- not just "amplification happened", the actual
		//    textbook formula.
		boolean closedFormOk = true;
		for (int n = 2; n <= 6; ++n) {
			int dim = 1 << n;
			double theta = Math.asin(1.0 / Math.sqrt(dim));
			for (int k = 0; k <= Grover.optimalIterations(n) + 2; ++k) {
				double expected = Math.pow(Math.sin((2 * k + 1) * theta), 2);
				MatrixComplex state = Grover.run(0, n, k);
				double actual = Grover.probabilityOfTarget(state, 0);
				if (Math.abs(expected - actual) > 1e-9) { closedFormOk = false; }
			}
		}
		check("measured probability matches sin^2((2k+1)*theta) exactly, n=2..6, several k", closedFormOk);

		// 5. Over-rotation: the probability is NOT monotonically increasing with more iterations --
		//    somewhere within a full period (predicted by the closed form itself, not a hardcoded
		//    offset) it must dip back below its value at optimalIterations(), confirming the "keeps
		//    rotating past the target and comes back down" behaviour instead of simple convergence.
		int n = 5;
		int target = 17;
		int optimal = Grover.optimalIterations(n);
		double pAtOptimal = Grover.search(target, n);
		double theta = Math.asin(1.0 / Math.sqrt(1 << n));
		int periodInIterations = (int) Math.round(Math.PI / (2 * theta)); // (2k+1)*theta has period pi in k*2theta
		boolean overRotationOk = pAtOptimal > 0.9;
		boolean foundDip = false;
		for (int k = optimal + 1; k <= optimal + periodInIterations; ++k) {
			if (Grover.probabilityOfTarget(Grover.run(target, n, k), target) < pAtOptimal - 0.2) { foundDip = true; break; }
		}
		check("probability dips well below its optimal-iteration value somewhere within 1 period (over-rotation)",
				overRotationOk && foundDip);

		// 6. n=1 (N=2) is a genuine mathematical degeneracy, not a bug: theta=asin(1/sqrt(2))=45
		//    degrees, so (2k+1)*theta is an odd multiple of 45 degrees for EVERY k -- sin^2 of that
		//    is exactly 0.5 no matter how many iterations run. Grover provides no advantage at all
		//    for N=2 (found while writing this audit, not assumed beforehand -- the original attempt
		//    to assert "probability 1" here was wrong and caught by this very test failing).
		check("optimalIterations(1)==1", Grover.optimalIterations(1) == 1);
		boolean n1DegeneracyOk = true;
		for (int k = 0; k <= 5; ++k) {
			double p0 = Grover.probabilityOfTarget(Grover.run(0, 1, k), 0);
			double p1 = Grover.probabilityOfTarget(Grover.run(1, 1, k), 1);
			if (Math.abs(p0 - 0.5) > 1e-9 || Math.abs(p1 - 0.5) > 1e-9) { n1DegeneracyOk = false; }
		}
		check("N=2 (n=1) is stuck at exactly 0.5 for every iteration count -- a genuine rotation-angle degeneracy",
				n1DegeneracyOk);

		// 7. oracle()/diffusion()/optimalIterations()/initialState() reject n<1; oracle() rejects an
		//    out-of-range target
		boolean rejectOk = true;
		try { Grover.oracle(0, 0); rejectOk = false; } catch (IllegalArgumentException e) { /* expected */ }
		try { Grover.diffusion(0); rejectOk = false; } catch (IllegalArgumentException e) { /* expected */ }
		try { Grover.oracle(4, 2); rejectOk = false; } catch (IllegalArgumentException e) { /* expected */ }
		check("oracle()/diffusion() reject n<1 or an out-of-range target", rejectOk);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
