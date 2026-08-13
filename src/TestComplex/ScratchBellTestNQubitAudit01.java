package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.BellTest;
import com.ipserc.arith.quantum.DensityMatrix;
import com.ipserc.arith.quantum.Qubits;

/**
 * Audit of the n-qubit generalization of BellTest.correlation()/chsh()/simulateCorrelation()/
 * simulateChsh() -- candidate "generalizar BellTest a n qubits" of the Rol Fisica/Mecanica
 * Cuantica roadmap.
 */
public class ScratchBellTestNQubitAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		MatrixComplex Z = Qubits.pauliZ();
		MatrixComplex X = Qubits.pauliX();

		// 1. The 2-qubit convenience overloads are UNCHANGED behaviourally: bellPhiPlus() correlations
		//    and Tsirelson's bound still come out exactly as before the refactor.
		MatrixComplex bell = Qubits.bellPhiPlus();
		check("correlation(bell,Z,Z)==1 (2-qubit convenience overload)",
				Math.abs(BellTest.correlation(bell, Z, Z) - 1.0) < 1e-9);
		double s = BellTest.chsh(bell, 0.0, Math.PI / 2, Math.PI / 4, 3 * Math.PI / 4);
		check("chsh() reaches Tsirelson's bound 2*sqrt(2) (2-qubit convenience overload)",
				Math.abs(s - 2 * Math.sqrt(2.0)) < 1e-9);

		// 2. The general n-qubit form, called with qubitA=0,qubitB=1,nQubits=2, matches the
		//    convenience overload EXACTLY (same underlying math, just explicit indices).
		double sGeneral = BellTest.chsh(bell, 0, 1, 2, 0.0, Math.PI / 2, Math.PI / 4, 3 * Math.PI / 4);
		check("chsh(state,0,1,2,...) == chsh(state,...) exactly", Math.abs(sGeneral - s) < 1e-12);
		double corrGeneral = BellTest.correlation(bell, Z, 0, Z, 1, 2);
		check("correlation(state,Z,0,Z,1,2) == correlation(state,Z,Z) exactly",
				Math.abs(corrGeneral - BellTest.correlation(bell, Z, Z)) < 1e-12);

		// 3. GHZ(n): Z-Z correlation between ANY 2 qubits is exactly 1 (all qubits perfectly
		//    correlated in the computational basis, |00..0>+|11..1>), for n=3,4,5 and every pair.
		boolean ghzZZOk = true;
		for (int n = 3; n <= 5; ++n) {
			MatrixComplex ghz = Qubits.ghz(n);
			for (int qa = 0; qa < n; ++qa) {
				for (int qb = 0; qb < n; ++qb) {
					if (qa == qb) { continue; }
					double c = BellTest.correlation(ghz, Z, qa, Z, qb, n);
					if (Math.abs(c - 1.0) > 1e-9) { ghzZZOk = false; }
				}
			}
		}
		check("GHZ(n) Z-Z correlation == 1 for every pair of qubits, n=3,4,5", ghzZZOk);

		// 4. Two INDEPENDENT Bell pairs packed into a 4-qubit product state: correlation between a
		//    qubit of pair 1 and a qubit of pair 2 must be exactly 0 (no entanglement between them) --
		//    confirms marginalization over "other" qubits doesn't manufacture spurious correlation.
		MatrixComplex twoBellPairs = Qubits.bellPhiPlus().kroneckerprod(Qubits.bellPhiPlus()); // qubits 0,1 | 2,3
		boolean crossPairOk = true;
		int[][] crossPairs = { {0, 2}, {0, 3}, {1, 2}, {1, 3} };
		for (int[] pair : crossPairs) {
			double c = BellTest.correlation(twoBellPairs, Z, pair[0], Z, pair[1], 4);
			if (Math.abs(c - 0.0) > 1e-9) { crossPairOk = false; }
		}
		check("correlation==0 across 2 independent Bell pairs packed into 4 qubits", crossPairOk);
		// ...but WITHIN each pair, the correlation is still exactly 1, same 4-qubit state.
		boolean withinPairOk = Math.abs(BellTest.correlation(twoBellPairs, Z, 0, Z, 1, 4) - 1.0) < 1e-9
				&& Math.abs(BellTest.correlation(twoBellPairs, Z, 2, Z, 3, 4) - 1.0) < 1e-9;
		check("correlation==1 within each of the 2 independent Bell pairs, same 4-qubit state", withinPairOk);

		// 5. Cross-check against DensityMatrix: correlation(state,opA,qubitA,opB,qubitB,nQubits) must
		//    equal Tr(rho_reduced * (opA tensor opB)), rho_reduced from DensityMatrix.partialTrace()
		//    tracing out every OTHER qubit -- an independent computational path through a different
		//    part of this same package, bridging the 2 "Rol Fisica/Mecanica Cuantica" exercises.
		boolean crossCheckOk = true;
		for (int n = 3; n <= 4; ++n) {
			MatrixComplex ghz = Qubits.ghz(n);
			MatrixComplex rhoFull = DensityMatrix.of(ghz);
			for (int qa = 0; qa < n; ++qa) {
				for (int qb = qa + 1; qb < n; ++qb) {
					int[] traceOut = otherQubits(n, qa, qb);
					MatrixComplex rhoReduced = DensityMatrix.partialTrace(rhoFull, n, traceOut);
					// rhoReduced's basis order is (qa,qb) as kept qubits, MSB-first -- opA tensor opB
					// in that same order matches Qubits.ket(qa,qb)'s convention.
					MatrixComplex observable = X.kroneckerprod(X); // arbitrary Hermitian 2-qubit observable
					Complex expectedTrace = rhoReduced.times(observable).trace();
					double viaBellTest = BellTest.correlation(ghz, X, qa, X, qb, n);
					if (Math.abs(expectedTrace.rep() - viaBellTest) > 1e-9 || Math.abs(expectedTrace.imp()) > 1e-9) {
						crossCheckOk = false;
					}
				}
			}
		}
		check("correlation() matches Tr(rho_reduced*(opA(x)opB)) via DensityMatrix.partialTrace()", crossCheckOk);

		// 6. Rejects qubitA==qubitB
		try {
			BellTest.correlation(bell, Z, 0, Z, 0, 2);
			check("correlation() rejects qubitA==qubitB", false);
		} catch (IllegalArgumentException e) {
			check("correlation() rejects qubitA==qubitB", true);
		}

		// 7. Monte Carlo, general form: converges to the exact correlation for a GHZ(3) pair, and the
		//    2-qubit convenience overload still matches its own general form under the hood.
		Random rng = new Random(20260813L);
		MatrixComplex ghz3 = Qubits.ghz(3);
		double exactZZ01 = BellTest.correlation(ghz3, Z, 0, Z, 1, 3);
		double mcZZ01 = BellTest.simulateCorrelation(ghz3, 0.0, 0, 0.0, 1, 3, 200000, rng);
		check("simulateCorrelation() general form converges to the exact GHZ(3) Z-Z correlation",
				Math.abs(mcZZ01 - exactZZ01) < 0.01);

		double exactSConv = BellTest.chsh(bell, 0.0, Math.PI / 2, Math.PI / 4, 3 * Math.PI / 4);
		double mcSConv = BellTest.simulateChsh(bell, 0.0, Math.PI / 2, Math.PI / 4, 3 * Math.PI / 4, 100000, rng);
		check("simulateChsh() 2-qubit convenience overload converges to Tsirelson's bound",
				Math.abs(mcSConv - exactSConv) < 0.02);

		double mcSGeneral = BellTest.simulateChsh(bell, 0, 1, 2, 0.0, Math.PI / 2, Math.PI / 4, 3 * Math.PI / 4, 100000, rng);
		check("simulateChsh() general form (qubitA=0,qubitB=1,nQubits=2) also converges",
				Math.abs(mcSGeneral - exactSConv) < 0.02);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static int[] otherQubits(int n, int qa, int qb) {
		int[] result = new int[n - 2];
		int idx = 0;
		for (int q = 0; q < n; ++q) {
			if (q != qa && q != qb) { result[idx++] = q; }
		}
		return result;
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
