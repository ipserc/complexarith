package TestComplex;

import java.util.function.IntPredicate;

import com.ipserc.arith.quantum.BernsteinVazirani;
import com.ipserc.arith.quantum.DeutschJozsa;

/**
 * Audit of BernsteinVazirani.java (oracleFunction()/findSecret()) -- second "algoritmo cuantico
 * mas grande" follow-up of the Rol Fisica/Mecanica Cuantica, after DeutschJozsa.
 */
public class ScratchBernsteinVaziraniAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		// 1. findSecret() recovers EVERY possible secret exactly, for n=1..6 (exhaustive, not a sample)
		boolean exhaustiveOk = true;
		for (int n = 1; n <= 6; ++n) {
			for (int secret = 0; secret < (1 << n); ++secret) {
				IntPredicate f = BernsteinVazirani.oracleFunction(secret, n);
				int recovered = BernsteinVazirani.findSecret(f, n);
				if (recovered != secret) { exhaustiveOk = false; }
			}
		}
		check("findSecret() recovers EVERY secret exactly, n=1..6 (exhaustive)", exhaustiveOk);

		// 2. oracleFunction(0,n) is the all-zero function (f(x)=0 for every x) -- the secret-string
		//    special case that also happens to be Deutsch-Jozsa's constantZero(): both algorithms
		//    should agree it's "constant"/"secret=0" on the exact same oracle function object.
		IntPredicate zeroSecret = BernsteinVazirani.oracleFunction(0, 3);
		boolean crossCheckOk = DeutschJozsa.isConstant(zeroSecret, 3) && BernsteinVazirani.findSecret(zeroSecret, 3) == 0;
		check("secret=0 oracle is DeutschJozsa-constant AND BernsteinVazirani finds 0 (same f)", crossCheckOk);

		// 3. oracleFunction(secret=2^n-1, n) (all-ones secret) is the parity function -- another
		//    cross-check against DeutschJozsa (parity is balanced) on the exact same oracle.
		for (int n = 2; n <= 4; ++n) {
			int allOnes = (1 << n) - 1;
			IntPredicate f = BernsteinVazirani.oracleFunction(allOnes, n);
			boolean isBalanced = !DeutschJozsa.isConstant(f, n);
			boolean findsAllOnes = BernsteinVazirani.findSecret(f, n) == allOnes;
			if (!isBalanced || !findsAllOnes) { crossCheckOk = false; }
		}
		check("all-ones secret oracle is DeutschJozsa-balanced AND BernsteinVazirani finds it, n=2..4", crossCheckOk);

		// 4. findSecret() fails loud for an f that is NOT of the linear (a.x) mod 2 form. A constant
		//    function turns out NOT to be a counterexample here (verified, not assumed): it only
		//    contributes a global phase, so it collapses to the SAME |0> outcome secret=0 would --
		//    the circuit genuinely can't tell "constant" from "secret=0" apart. A genuinely non-linear
		//    f (here, a single-input delta: f(x)=1 only for x=0) spreads probability across MULTIPLE
		//    input-register outcomes instead, which findSecret() correctly rejects.
		try {
			BernsteinVazirani.findSecret(x -> x == 0, 3);
			check("findSecret() rejects a non-linear f (delta at x=0)", false);
		} catch (IllegalStateException e) {
			check("findSecret() rejects a non-linear f (delta at x=0)", true);
		}
		check("constant functions collapse to the SAME outcome as secret=0 (not a counterexample)",
				BernsteinVazirani.findSecret(x -> true, 3) == 0);

		// 5. oracleFunction() rejects n<1 and an out-of-range secret
		try {
			BernsteinVazirani.oracleFunction(0, 0);
			check("oracleFunction() rejects n<1", false);
		} catch (IllegalArgumentException e) {
			check("oracleFunction() rejects n<1", true);
		}
		try {
			BernsteinVazirani.oracleFunction(8, 3); // 8 is out of [0,8) for n=3
			check("oracleFunction() rejects secret out of range", false);
		} catch (IllegalArgumentException e) {
			check("oracleFunction() rejects secret out of range", true);
		}

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
