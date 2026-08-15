package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.QFT;
import com.ipserc.arith.quantum.Qubits;

/**
 * Audit of QFT.java (circuit()/matrix()) -- Etapa 5 of the QFT guided development (see
 * Claude/ComplexArithRev.md, Trigesimoctava/Trigesimonovena sesion). circuit() was built by the
 * user by hand, staged from n=1 (trivial, ==hadamard()) up through n=2 (Hadamard + 1 controlled
 * phase + SWAP) before generalizing to any n in TestComplex/TestQuantum_QFT01.java; matrix() is
 * the literal defining formula, used here as the independent oracle circuit() is checked against.
 */
public class ScratchQFTAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		// 1. circuit(n) is unitary (U*U^dagger == U^dagger*U == I), n=1..6 -- confirms the gate
		//    composition (Hadamard + controlled phase + SWAP) is genuinely a valid quantum operation,
		//    not just "happens to match matrix(n)".
		boolean unitaryOk = true;
		for (int n = 1; n <= 6; ++n) {
			MatrixComplex u = QFT.circuit(n);
			MatrixComplex identity = MatrixComplex.eye(1 << n);
			if (u.times(u.adjoint()).minus(identity).norm() > 1e-9) { unitaryOk = false; }
			if (u.adjoint().times(u).minus(identity).norm() > 1e-9) { unitaryOk = false; }
		}
		check("circuit(n) is unitary, n=1..6", unitaryOk);

		// 2. circuit(n) == matrix(n), n=1..6 -- the gate-based construction reproduces the literal
		//    formula exactly (within numerical tolerance), the central cross-check of the whole
		//    guided development.
		boolean crossCheckOk = true;
		for (int n = 1; n <= 6; ++n) {
			if (!QFT.circuit(n).equals(QFT.matrix(n), 10)) { crossCheckOk = false; }
		}
		check("circuit(n) == matrix(n), n=1..6", crossCheckOk);

		// 3. circuit(n) applied to |00...0> gives the exact equal superposition, real amplitude
		//    1/sqrt(2^n) on every basis state (phase EXACTLY 0, not just close) -- the textbook
		//    special case QFT|0>=(1/sqrt(N))*sum_k|k>, checked to full double precision.
		boolean zeroStateOk = true;
		for (int n = 1; n <= 6; ++n) {
			int dim = 1 << n;
			double expected = 1.0 / Math.sqrt(dim);
			MatrixComplex out = QFT.circuit(n).times(Qubits.ket(new int[n]));
			for (int k = 0; k < dim; ++k) {
				var amp = out.getItem(k, 0);
				if (Math.abs(amp.rep() - expected) > 1e-12) { zeroStateOk = false; }
				if (Math.abs(amp.imp()) > 1e-12) { zeroStateOk = false; }
			}
		}
		check("circuit(n)|0...0> is the exact equal superposition, n=1..6", zeroStateOk);

		// 4. circuit(n) applied to a single basis ket |j> matches column j of matrix(n) exactly --
		//    confirms circuit() also works correctly as a STATE transformation, not only as an
		//    abstract matrix compared to another matrix (checks 1-3 never apply it to a ket).
		boolean basisStateOk = true;
		for (int n = 2; n <= 4; ++n) {
			int dim = 1 << n;
			MatrixComplex m = QFT.matrix(n);
			MatrixComplex u = QFT.circuit(n);
			for (int j = 0; j < dim; ++j) {
				int[] bits = new int[n];
				for (int i = 0; i < n; ++i) { bits[i] = (j >> (n - 1 - i)) & 1; }
				MatrixComplex out = u.times(Qubits.ket(bits));
				for (int k = 0; k < dim; ++k) {
					if (out.getItem(k, 0).minus(m.getItem(k, j)).mod() > 1e-9) { basisStateOk = false; }
				}
			}
		}
		check("circuit(n)|j> matches column j of matrix(n), n=2..4, all j", basisStateOk);

		// 5. circuit()/matrix() reject n<1
		try {
			QFT.circuit(0);
			check("circuit() rejects n<1", false);
		} catch (IllegalArgumentException e) {
			check("circuit() rejects n<1", true);
		}
		try {
			QFT.matrix(-1);
			check("matrix() rejects n<1", false);
		} catch (IllegalArgumentException e) {
			check("matrix() rejects n<1", true);
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
