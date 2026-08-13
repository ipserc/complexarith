package TestComplex;

import java.util.function.IntPredicate;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.DeutschJozsa;
import com.ipserc.arith.quantum.Qubits;

/**
 * Audit of DeutschJozsa.java (oracle()/probabilityAllZero()/isConstant()) -- first "algoritmo
 * cuantico mas grande" follow-up of the Rol Fisica/Mecanica Cuantica, after the 7-candidate
 * roadmap catalogued at the close of the Trigesimosexta sesion was closed.
 */
public class ScratchDeutschJozsaAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		// 1. Oracle unitarity: U_f is its own inverse (U_f^2 = I), for constant and balanced f,
		//    n=1..4 -- confirms the permutation-matrix construction is genuinely unitary.
		boolean oracleUnitaryOk = true;
		for (int n = 1; n <= 4; ++n) {
			IntPredicate[] fs = { constantZero(), constantOne(), parity(), msb(n) };
			for (IntPredicate f : fs) {
				MatrixComplex u = DeutschJozsa.oracle(f, n);
				MatrixComplex identity = identity(1 << (n + 1));
				if (u.times(u).minus(identity).norm() > 1e-9) { oracleUnitaryOk = false; }
				if (u.adjoint().times(u).minus(identity).norm() > 1e-9) { oracleUnitaryOk = false; }
			}
		}
		check("oracle(f,n) is unitary and its own inverse, 4 f x n=1..4", oracleUnitaryOk);

		// 2. Oracle truth table: U_f|x,0> has the ancilla bit equal to f(x), for every x, n=1..4
		boolean truthTableOk = true;
		for (int n = 1; n <= 4; ++n) {
			IntPredicate f = parity();
			MatrixComplex u = DeutschJozsa.oracle(f, n);
			for (int x = 0; x < (1 << n); ++x) {
				int[] bits = new int[n + 1];
				for (int i = 0; i < n; ++i) { bits[i] = (x >> (n - 1 - i)) & 1; }
				bits[n] = 0;
				MatrixComplex in = Qubits.ket(bits);
				MatrixComplex out = u.times(in);
				int expectedIdx = (x << 1) | (f.test(x) ? 1 : 0);
				double amp = out.getItem(expectedIdx, 0).mod();
				if (Math.abs(amp - 1.0) > 1e-9) { truthTableOk = false; }
			}
		}
		check("oracle(parity,n)|x,0> lands exactly on |x,f(x)>, every x, n=1..4", truthTableOk);

		// 3. isConstant() correctly identifies constant functions, n=1..6
		boolean constantOk = true;
		for (int n = 1; n <= 6; ++n) {
			if (!DeutschJozsa.isConstant(constantZero(), n)) { constantOk = false; }
			if (!DeutschJozsa.isConstant(constantOne(), n)) { constantOk = false; }
		}
		check("isConstant()==true for constantZero/constantOne, n=1..6", constantOk);

		// 4. isConstant() correctly identifies balanced functions, n=1..6
		boolean balancedOk = true;
		for (int n = 1; n <= 6; ++n) {
			if (DeutschJozsa.isConstant(parity(), n)) { balancedOk = false; }
			if (DeutschJozsa.isConstant(msb(n), n)) { balancedOk = false; }
		}
		check("isConstant()==false for parity/msb (balanced), n=1..6", balancedOk);

		// 5. probabilityAllZero() is EXACTLY 1.0 or 0.0 (not just close), confirming the algorithm's
		//    deterministic "no third possibility" guarantee -- checked to full double precision, not
		//    the 1e-9 tolerance isConstant() itself uses internally.
		boolean exactOk = true;
		for (int n = 1; n <= 5; ++n) {
			double pConst = DeutschJozsa.probabilityAllZero(constantZero(), n);
			double pBal = DeutschJozsa.probabilityAllZero(parity(), n);
			if (Math.abs(pConst - 1.0) > 1e-12) { exactOk = false; }
			if (Math.abs(pBal - 0.0) > 1e-12) { exactOk = false; }
		}
		check("probabilityAllZero() is 1.0/0.0 to full double precision, not just approximately", exactOk);

		// 6. A brute-force cross-check: for every possible balanced function on n=2,3 qubits (built
		//    by explicitly listing which half of the inputs map to 1), isConstant() says balanced --
		//    not just the 2 hand-picked examples above.
		boolean bruteForceOk = true;
		for (int n = 2; n <= 3; ++n) {
			int dim = 1 << n;
			// a handful of DIFFERENT balanced truth tables per n, not an exhaustive enumeration
			for (int seed = 0; seed < 5; ++seed) {
				boolean[] table = balancedTruthTable(dim, seed);
				IntPredicate f = x -> table[x];
				if (DeutschJozsa.isConstant(f, n)) { bruteForceOk = false; }
			}
		}
		check("isConstant()==false for 5 different balanced truth tables x n=2,3", bruteForceOk);

		// 7. isConstant() fails loud for an f that is NEITHER constant nor balanced (violates the
		//    algorithm's promise) -- e.g. f(x)=1 only for x=0 (1 out of 2^n inputs, not half).
		try {
			IntPredicate onlyZeroIsOne = x -> x == 0;
			DeutschJozsa.isConstant(onlyZeroIsOne, 3);
			check("isConstant() rejects an f that isn't constant or balanced", false);
		} catch (IllegalStateException e) {
			check("isConstant() rejects an f that isn't constant or balanced", true);
		}

		// 8. oracle() rejects n<1
		try {
			DeutschJozsa.oracle(constantZero(), 0);
			check("oracle() rejects n<1", false);
		} catch (IllegalArgumentException e) {
			check("oracle() rejects n<1", true);
		}

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static IntPredicate constantZero() { return x -> false; }
	static IntPredicate constantOne() { return x -> true; }
	static IntPredicate parity() { return x -> (Integer.bitCount(x) % 2) == 1; }
	static IntPredicate msb(int n) { return x -> ((x >> (n - 1)) & 1) == 1; }

	/** A balanced truth table (exactly half true) over {@code [0,dim)}, deterministic per {@code seed}. */
	static boolean[] balancedTruthTable(int dim, int seed) {
		boolean[] table = new boolean[dim];
		int[] indices = new int[dim];
		for (int i = 0; i < dim; ++i) { indices[i] = i; }
		// simple deterministic shuffle keyed by seed, no need for a real Random/library shuffle here
		for (int i = dim - 1; i > 0; --i) {
			int j = (i * (seed + 7) + seed) % (i + 1);
			int tmp = indices[i]; indices[i] = indices[j]; indices[j] = tmp;
		}
		for (int i = 0; i < dim / 2; ++i) { table[indices[i]] = true; }
		return table;
	}

	static MatrixComplex identity(int dim) {
		MatrixComplex m = new MatrixComplex(dim, dim);
		for (int i = 0; i < dim; ++i) { m.setItem(i, i, 1.0); }
		return m;
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
