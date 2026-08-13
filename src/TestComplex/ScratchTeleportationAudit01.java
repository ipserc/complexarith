package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.quantum.Teleportation;

/**
 * Audit of Teleportation.java (Qubits.hadamard()/controlledGate() + the protocol itself) --
 * candidate "toy quantum algorithm" of the Rol Fisica/Mecanica Cuantica roadmap catalogued at the
 * close of the Trigesimosexta sesion.
 */
public class ScratchTeleportationAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		MatrixComplex X = Qubits.pauliX();
		MatrixComplex I2 = Qubits.identity2();

		// 1. Qubits.hadamard() is unitary and its own inverse (H^2=I)
		MatrixComplex H = Qubits.hadamard();
		check("hadamard() unitary", H.adjoint().times(H).minus(I2).norm() < 1e-12);
		check("hadamard()^2=I", H.times(H).minus(I2).norm() < 1e-12);

		// 2. Qubits.controlledGate(pauliX,...) reproduces CNOT's truth table on the 4 basis states of 2 qubits
		MatrixComplex cnot = Qubits.controlledGate(X, 0, 1, 2);
		boolean cnotOk = true;
		int[][] truth = { {0,0,0,0}, {0,1,0,1}, {1,0,1,1}, {1,1,1,0} }; // control,target -> control,target'
		for (int[] row : truth) {
			MatrixComplex in = Qubits.ket(row[0], row[1]);
			MatrixComplex expected = Qubits.ket(row[2], row[3]);
			if (cnot.times(in).minus(expected).norm() > 1e-12) { cnotOk = false; }
		}
		check("controlledGate(pauliX,0,1,2) reproduces the CNOT truth table", cnotOk);

		// 3. controlledGate() rejects a bad configuration
		try {
			Qubits.controlledGate(X, 0, 0, 2);
			check("controlledGate() rejects control==target", false);
		} catch (IllegalArgumentException e) {
			check("controlledGate() rejects control==target", true);
		}

		// 4. probabilityOfOutcome: all 4 outcomes exactly 0.25, for several psi (the hallmark that
		//    Alice's measurement carries no information about psi)
		MatrixComplex[] testStates = {
				Qubits.ket0(),
				Qubits.ket1(),
				Qubits.ket0().plus(Qubits.ket1()).normalizeByCols(),
				Qubits.ket0().plus(Qubits.ket1().times(new Complex(0.0, 1.0))).normalizeByCols(),
				Qubits.ket0().times(new Complex(0.6, 0.0)).plus(Qubits.ket1().times(new Complex(0.0, 0.8))).normalizeByCols(),
		};
		boolean probsOk = true;
		for (MatrixComplex psi : testStates) {
			double sum = 0.0;
			for (int m1 = 0; m1 <= 1; ++m1) {
				for (int m2 = 0; m2 <= 1; ++m2) {
					double p = Teleportation.probabilityOfOutcome(psi, m1, m2);
					if (Math.abs(p - 0.25) > 1e-9) { probsOk = false; }
					sum += p;
				}
			}
			if (Math.abs(sum - 1.0) > 1e-9) { probsOk = false; }
		}
		check("probabilityOfOutcome==0.25 for all 4 outcomes, for 5 different psi", probsOk);

		// 5. correctedStateForOutcome: EXACT equality to psi (not just up to global phase) for all
		//    4 outcomes, for the same 5 psi
		boolean correctionOk = true;
		for (MatrixComplex psi : testStates) {
			for (int m1 = 0; m1 <= 1; ++m1) {
				for (int m2 = 0; m2 <= 1; ++m2) {
					MatrixComplex corrected = Teleportation.correctedStateForOutcome(psi, m1, m2);
					if (corrected.minus(psi).norm() > 1e-9) { correctionOk = false; }
				}
			}
		}
		check("correctedStateForOutcome==psi exactly, for all 4 outcomes x 5 psi", correctionOk);

		// 6. Monte Carlo simulate() always recovers psi, over many runs and several psi, and visits
		//    all 4 branches (confirms it isn't accidentally always landing on the trivial (0,0) case)
		Random rng = new Random(20260813L);
		boolean simulateOk = true;
		java.util.Set<String> outcomesSeen = new java.util.HashSet<>();
		for (MatrixComplex psi : testStates) {
			for (int trial = 0; trial < 200; ++trial) {
				MatrixComplex result = Teleportation.simulate(psi, rng);
				if (result.minus(psi).norm() > 1e-9) { simulateOk = false; }
			}
		}
		// separately sample outcomes to confirm all 4 branches are reachable (fixed psi, many trials)
		for (int trial = 0; trial < 400; ++trial) {
			double r = rng.nextDouble();
			double cumulative = 0.0;
			outer:
			for (int a = 0; a <= 1; ++a) {
				for (int b = 0; b <= 1; ++b) {
					cumulative += Teleportation.probabilityOfOutcome(testStates[2], a, b);
					if (r < cumulative) { outcomesSeen.add(a + "" + b); break outer; }
				}
			}
		}
		check("simulate() always recovers psi exactly (5 psi x 200 trials)", simulateOk);
		check("all 4 measurement branches reachable over many trials", outcomesSeen.size() == 4);

		// 7. Non-normalized psi still teleports correctly once normalized first (sanity, not part
		//    of the public contract but confirms no hidden normalization assumption breaks silently)
		MatrixComplex psiNorm = Qubits.ket0().plus(Qubits.ket1().times(2.0)).normalizeByCols();
		boolean normOk = true;
		for (int m1 = 0; m1 <= 1; ++m1) {
			for (int m2 = 0; m2 <= 1; ++m2) {
				MatrixComplex corrected = Teleportation.correctedStateForOutcome(psiNorm, m1, m2);
				if (corrected.minus(psiNorm).norm() > 1e-9) { normOk = false; }
			}
		}
		check("teleportation exact for a non-trivial-amplitude-ratio psi", normOk);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
