package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Qubits;

/**
 * Verification for the n-qubit basics added to {@code Qubits} (Rol Física/Mecánica Cuántica,
 * generalización a n qubits): {@code ket(int...)}, {@code ghz(int)}, {@code operatorOnQubit}.
 */
public class ScratchQubitsNAudit01 {

	static int ok = 0;
	static int total = 0;

	public static void main(String[] args) {
		testKetMatchesKron2();
		testKetMatchesKron3();
		testKetRejectsBadBit();
		testKetRejectsEmpty();
		testGhz2EqualsBell();
		testGhzNormalized(3);
		testGhzNormalized(5);
		testGhzRejectsTooFew();
		testGhzOnlyAllZeroAllOne(4);
		testOperatorOnQubitDimension(4);
		testOperatorOnQubitActsLocally();
		testOperatorOnQubitIdentityElsewhere();
		testOperatorOnQubitRejectsBadIndex();

		System.out.println(ok + "/" + total + " OK");
		if (ok != total) { System.exit(1); }
	}

	static void check(String name, boolean cond) {
		total++;
		if (cond) { ok++; }
		else { System.out.println("FAIL: " + name); }
	}

	static void testKetMatchesKron2() {
		MatrixComplex expected = Qubits.ket0().kroneckerprod(Qubits.ket1());
		MatrixComplex actual = Qubits.ket(0, 1);
		check("ket(0,1) matches manual kron", expected.minus(actual).norm() < 1e-12);
	}

	static void testKetMatchesKron3() {
		MatrixComplex expected = Qubits.ket1().kroneckerprod(Qubits.ket0()).kroneckerprod(Qubits.ket1());
		MatrixComplex actual = Qubits.ket(1, 0, 1);
		check("ket(1,0,1) matches manual kron", expected.minus(actual).norm() < 1e-12);
	}

	static void testKetRejectsBadBit() {
		boolean threw = false;
		try { Qubits.ket(0, 2); }
		catch (IllegalArgumentException e) { threw = true; }
		check("ket() rejects bit!=0/1", threw);
	}

	static void testKetRejectsEmpty() {
		boolean threw = false;
		try { Qubits.ket(); }
		catch (IllegalArgumentException e) { threw = true; }
		check("ket() rejects empty", threw);
	}

	static void testGhz2EqualsBell() {
		MatrixComplex ghz2 = Qubits.ghz(2);
		MatrixComplex bell = Qubits.bellPhiPlus();
		check("ghz(2) == bellPhiPlus()", ghz2.minus(bell).norm() < 1e-12);
	}

	static void testGhzNormalized(int n) {
		MatrixComplex state = Qubits.ghz(n);
		Complex normSq = state.adjoint().times(state).getItem(0, 0);
		check("ghz(" + n + ") normalized", Math.abs(normSq.rep() - 1.0) < 1e-12 && Math.abs(normSq.imp()) < 1e-12);
	}

	static void testGhzRejectsTooFew() {
		boolean threw = false;
		try { Qubits.ghz(1); }
		catch (IllegalArgumentException e) { threw = true; }
		check("ghz() rejects n<2", threw);
	}

	static void testGhzOnlyAllZeroAllOne(int n) {
		MatrixComplex state = Qubits.ghz(n);
		double invSqrt2 = 1.0 / Math.sqrt(2.0);
		boolean allOk = true;
		for (int row = 0; row < state.rows(); ++row) {
			Complex v = state.getItem(row, 0);
			boolean isEndpoint = (row == 0) || (row == state.rows() - 1);
			double expectedMod = isEndpoint ? invSqrt2 : 0.0;
			if (Math.abs(v.mod() - expectedMod) > 1e-12) { allOk = false; }
		}
		check("ghz(" + n + ") nonzero only at |00..0>/|11..1>", allOk);
	}

	static void testOperatorOnQubitDimension(int n) {
		MatrixComplex lifted = Qubits.operatorOnQubit(Qubits.pauliX(), 1, n);
		check("operatorOnQubit dimension 2^n", lifted.rows() == (1 << n) && lifted.cols() == (1 << n));
	}

	static void testOperatorOnQubitActsLocally() {
		// X on qubit 0 of 2 flips |00>-><10>, |01>-><11>
		MatrixComplex xOn0 = Qubits.operatorOnQubit(Qubits.pauliX(), 0, 2);
		MatrixComplex result = xOn0.times(Qubits.ket(0, 0));
		MatrixComplex expected = Qubits.ket(1, 0);
		check("X on qubit 0 of 2: |00>-><10>", result.minus(expected).norm() < 1e-12);

		MatrixComplex xOn1 = Qubits.operatorOnQubit(Qubits.pauliX(), 1, 2);
		MatrixComplex result2 = xOn1.times(Qubits.ket(0, 0));
		MatrixComplex expected2 = Qubits.ket(0, 1);
		check("X on qubit 1 of 2: |00>-><01>", result2.minus(expected2).norm() < 1e-12);
	}

	static void testOperatorOnQubitIdentityElsewhere() {
		// Z on qubit 1 of 3 leaves qubits 0/2 alone: <110| Z_1 |110> = -1 (qubit1=1 -> eigenvalue -1)
		MatrixComplex zOn1 = Qubits.operatorOnQubit(Qubits.pauliZ(), 1, 3);
		MatrixComplex ket110 = Qubits.ket(1, 1, 0);
		Complex eigen = ket110.adjoint().times(zOn1).times(ket110).getItem(0, 0);
		check("Z on qubit 1 of |110> gives eigenvalue -1", Math.abs(eigen.rep() - (-1.0)) < 1e-12);

		MatrixComplex ket100 = Qubits.ket(1, 0, 0);
		Complex eigen2 = ket100.adjoint().times(zOn1).times(ket100).getItem(0, 0);
		check("Z on qubit 1 of |100> gives eigenvalue +1", Math.abs(eigen2.rep() - 1.0) < 1e-12);
	}

	static void testOperatorOnQubitRejectsBadIndex() {
		boolean threw1 = false;
		try { Qubits.operatorOnQubit(Qubits.pauliX(), 3, 3); }
		catch (IllegalArgumentException e) { threw1 = true; }
		check("operatorOnQubit rejects index==nQubits", threw1);

		boolean threw2 = false;
		try { Qubits.operatorOnQubit(Qubits.pauliX(), 0, 0); }
		catch (IllegalArgumentException e) { threw2 = true; }
		check("operatorOnQubit rejects nQubits<1", threw2);
	}
}
