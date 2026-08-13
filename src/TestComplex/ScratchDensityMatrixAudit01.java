package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.DensityMatrix;
import com.ipserc.arith.quantum.Qubits;

/**
 * Verification for {@code DensityMatrix} (Rol Física/Mecánica Cuántica, matriz densidad + traza
 * parcial + entropía de von Neumann): {@code of()}, {@code partialTrace()}, {@code
 * vonNeumannEntropy()}.
 */
public class ScratchDensityMatrixAudit01 {

	static int ok = 0;
	static int total = 0;

	public static void main(String[] args) {
		testDensityOfBasisStateIsProjector();
		testDensityTraceIsOne();
		testPureStateEntropyIsZero();
		testMaximallyMixedEntropyIsOne();
		testProductStatePartialTraceIsPure();
		testBellPartialTraceIsMaximallyMixed();
		testGhz3PartialTraceOneQubitMaximallyMixed();
		testGhz3PartialTraceTwoQubitsMaximallyMixed();
		testPartialTraceOfProduct000ByQubit();
		testPartialTraceRejectsBadDim();
		testPartialTraceRejectsDuplicateIndex();
		testPartialTraceRejectsOutOfRangeIndex();
		testPartialTraceRejectsEmpty();
		testPartialTraceAllQubitsGivesFullTrace();

		System.out.println(ok + "/" + total + " OK");
		if (ok != total) { System.exit(1); }
	}

	static void check(String name, boolean cond) {
		total++;
		if (cond) { ok++; }
		else { System.out.println("FAIL: " + name); }
	}

	static boolean near(double a, double b) { return Math.abs(a - b) < 1e-9; }

	static boolean isIdentityLike(MatrixComplex rho, int dim, double diagVal) {
		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				Complex v = rho.getItem(i, j);
				double expectedRe = (i == j) ? diagVal : 0.0;
				if (!near(v.rep(), expectedRe) || !near(v.imp(), 0.0)) { return false; }
			}
		}
		return true;
	}

	static void testDensityOfBasisStateIsProjector() {
		MatrixComplex rho = DensityMatrix.of(Qubits.ket0());
		// |0><0| = [[1,0],[0,0]]
		check("of(|0>) is projector [[1,0],[0,0]]",
				near(rho.getItem(0, 0).rep(), 1.0) && near(rho.getItem(1, 1).rep(), 0.0)
						&& near(rho.getItem(0, 1).rep(), 0.0) && near(rho.getItem(1, 0).rep(), 0.0));
	}

	static void testDensityTraceIsOne() {
		MatrixComplex rho = DensityMatrix.of(Qubits.bellPhiPlus());
		Complex trace = rho.getItem(0, 0).plus(rho.getItem(1, 1)).plus(rho.getItem(2, 2)).plus(rho.getItem(3, 3));
		check("Tr(of(bellPhiPlus)) == 1", near(trace.rep(), 1.0) && near(trace.imp(), 0.0));
	}

	static void testPureStateEntropyIsZero() {
		double s = DensityMatrix.vonNeumannEntropy(DensityMatrix.of(Qubits.bellPhiPlus()));
		check("S(pure Bell state) == 0", near(s, 0.0));
	}

	static void testMaximallyMixedEntropyIsOne() {
		MatrixComplex maximallyMixed = Qubits.identity2().times(0.5);
		double s = DensityMatrix.vonNeumannEntropy(maximallyMixed);
		check("S(I/2) == 1 bit", near(s, 1.0));
	}

	static void testProductStatePartialTraceIsPure() {
		// |01> is a product state -- tracing out qubit 1 must leave qubit 0 in a PURE state |0><0|
		MatrixComplex rho = DensityMatrix.of(Qubits.ket(0, 1));
		MatrixComplex reduced = DensityMatrix.partialTrace(rho, 2, 1);
		check("partialTrace(|01>, trace qubit1) == |0><0|", isIdentityLike(reduced, 1, 1.0)
				&& near(DensityMatrix.vonNeumannEntropy(reduced), 0.0));
	}

	static void testBellPartialTraceIsMaximallyMixed() {
		// Tracing out either qubit of a Bell pair leaves the other maximally mixed: I/2
		MatrixComplex rho = DensityMatrix.of(Qubits.bellPhiPlus());
		MatrixComplex reducedA = DensityMatrix.partialTrace(rho, 2, 1);
		MatrixComplex reducedB = DensityMatrix.partialTrace(rho, 2, 0);
		check("partialTrace(bellPhiPlus, trace qubit1) == I/2", isIdentityLike(reducedA, 2, 0.5));
		check("partialTrace(bellPhiPlus, trace qubit0) == I/2", isIdentityLike(reducedB, 2, 0.5));
		check("S(reduced Bell qubit) == 1 bit (entanglement witness)",
				near(DensityMatrix.vonNeumannEntropy(reducedA), 1.0));
	}

	static void testGhz3PartialTraceOneQubitMaximallyMixed() {
		// GHZ(3): tracing out 1 qubit leaves a MIXED (not maximally mixed) 2-qubit reduced state
		MatrixComplex rho = DensityMatrix.of(Qubits.ghz(3));
		MatrixComplex reduced = DensityMatrix.partialTrace(rho, 3, 2);
		// diag(0.5,0,0,0.5) -- classical mixture of |00> and |11>, off-diagonal coherence destroyed
		boolean diagOk = near(reduced.getItem(0, 0).rep(), 0.5) && near(reduced.getItem(3, 3).rep(), 0.5)
				&& near(reduced.getItem(1, 1).rep(), 0.0) && near(reduced.getItem(2, 2).rep(), 0.0);
		check("partialTrace(ghz(3), trace qubit2) == diag(0.5,0,0,0.5)", diagOk);
		check("S(ghz(3) reduced to 2 qubits) == 1 bit", near(DensityMatrix.vonNeumannEntropy(reduced), 1.0));
	}

	static void testGhz3PartialTraceTwoQubitsMaximallyMixed() {
		MatrixComplex rho = DensityMatrix.of(Qubits.ghz(3));
		MatrixComplex reduced = DensityMatrix.partialTrace(rho, 3, 1, 2);
		check("partialTrace(ghz(3), trace qubits1,2) == I/2", isIdentityLike(reduced, 2, 0.5));
		check("S(ghz(3) reduced to 1 qubit) == 1 bit", near(DensityMatrix.vonNeumannEntropy(reduced), 1.0));
	}

	static void testPartialTraceOfProduct000ByQubit() {
		MatrixComplex rho = DensityMatrix.of(Qubits.ket(0, 0, 0));
		MatrixComplex reduced = DensityMatrix.partialTrace(rho, 3, 0, 2);
		check("partialTrace(|000>, trace qubits0,2) == |0><0|", isIdentityLike(reduced, 1, 1.0));
	}

	static void testPartialTraceRejectsBadDim() {
		boolean threw = false;
		try { DensityMatrix.partialTrace(Qubits.identity2(), 2, 0); }
		catch (IllegalArgumentException e) { threw = true; }
		check("partialTrace rejects mismatched dim", threw);
	}

	static void testPartialTraceRejectsDuplicateIndex() {
		MatrixComplex rho = DensityMatrix.of(Qubits.ket(0, 0, 0));
		boolean threw = false;
		try { DensityMatrix.partialTrace(rho, 3, 0, 0); }
		catch (IllegalArgumentException e) { threw = true; }
		check("partialTrace rejects duplicate index", threw);
	}

	static void testPartialTraceRejectsOutOfRangeIndex() {
		MatrixComplex rho = DensityMatrix.of(Qubits.ket(0, 0, 0));
		boolean threw = false;
		try { DensityMatrix.partialTrace(rho, 3, 3); }
		catch (IllegalArgumentException e) { threw = true; }
		check("partialTrace rejects out-of-range index", threw);
	}

	static void testPartialTraceRejectsEmpty() {
		MatrixComplex rho = DensityMatrix.of(Qubits.ket(0, 0, 0));
		boolean threw = false;
		try { DensityMatrix.partialTrace(rho, 3); }
		catch (IllegalArgumentException e) { threw = true; }
		check("partialTrace rejects empty traceOutQubits", threw);
	}

	static void testPartialTraceAllQubitsGivesFullTrace() {
		MatrixComplex rho = DensityMatrix.of(Qubits.bellPhiPlus());
		MatrixComplex fullTrace = DensityMatrix.partialTrace(rho, 2, 0, 1);
		check("partialTrace(all qubits) == [[1]] (full trace)",
				fullTrace.rows() == 1 && fullTrace.cols() == 1 && near(fullTrace.getItem(0, 0).rep(), 1.0));
	}
}
