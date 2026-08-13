package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.DensityMatrix;
import com.ipserc.arith.quantum.Qubits;

/**
 * Audit of Decoherence.java (Kraus-operator noise channels + apply()) -- candidate
 * "ruido/decoherencia" of the Rol Fisica/Mecanica Cuantica roadmap catalogued at the close of the
 * Trigesimosexta sesion.
 */
public class ScratchDecoherenceAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		MatrixComplex I2 = Qubits.identity2();
		MatrixComplex ket0 = Qubits.ket0();
		MatrixComplex ket1 = Qubits.ket1();
		MatrixComplex ketPlus = ket0.plus(ket1).normalizeByCols();
		MatrixComplex[] pureStates = { ket0, ket1, ketPlus,
				ket0.times(new Complex(0.6, 0.0)).plus(ket1.times(new Complex(0.0, 0.8))).normalizeByCols() };

		// 1. Kraus completeness: sum_k E_k^dagger E_k == I, for every channel factory, several p
		boolean completenessOk = true;
		double[] probs = { 0.0, 0.1, 0.37, 0.5, 0.9, 1.0 };
		for (double p : probs) {
			completenessOk &= isComplete(Decoherence.bitFlip(p));
			completenessOk &= isComplete(Decoherence.phaseFlip(p));
			completenessOk &= isComplete(Decoherence.bitPhaseFlip(p));
			completenessOk &= isComplete(Decoherence.depolarizing(p));
			completenessOk &= isComplete(Decoherence.amplitudeDamping(p));
		}
		check("Kraus completeness sum(E_k^dagger E_k)=I for all 5 channels x 6 probabilities", completenessOk);

		// 2. p=0 (or gamma=0) is the identity channel for every family, on several states
		boolean identityOk = true;
		for (MatrixComplex psi : pureStates) {
			MatrixComplex rho = DensityMatrix.of(psi);
			identityOk &= Decoherence.apply(rho, Decoherence.bitFlip(0.0), 0, 1).minus(rho).norm() < 1e-12;
			identityOk &= Decoherence.apply(rho, Decoherence.phaseFlip(0.0), 0, 1).minus(rho).norm() < 1e-12;
			identityOk &= Decoherence.apply(rho, Decoherence.bitPhaseFlip(0.0), 0, 1).minus(rho).norm() < 1e-12;
			identityOk &= Decoherence.apply(rho, Decoherence.depolarizing(0.0), 0, 1).minus(rho).norm() < 1e-12;
			identityOk &= Decoherence.apply(rho, Decoherence.amplitudeDamping(0.0), 0, 1).minus(rho).norm() < 1e-12;
		}
		check("p=0/gamma=0 is the identity channel for all 5 families, 4 different states", identityOk);

		// 3. bitFlip(1.0) == X rho X exactly (full deterministic flip)
		boolean fullFlipOk = true;
		for (MatrixComplex psi : pureStates) {
			MatrixComplex rho = DensityMatrix.of(psi);
			MatrixComplex viaChannel = Decoherence.apply(rho, Decoherence.bitFlip(1.0), 0, 1);
			MatrixComplex expected = Qubits.pauliX().times(rho).times(Qubits.pauliX());
			if (viaChannel.minus(expected).norm() > 1e-9) { fullFlipOk = false; }
		}
		check("bitFlip(1.0) applies X*rho*X exactly", fullFlipOk);

		// 4. depolarizing(1.0) maps ANY single-qubit state to exactly I/2, for several states
		boolean depolarizeOk = true;
		MatrixComplex halfIdentity = I2.times(0.5);
		for (MatrixComplex psi : pureStates) {
			MatrixComplex rho = DensityMatrix.of(psi);
			MatrixComplex depolarized = Decoherence.apply(rho, Decoherence.depolarizing(1.0), 0, 1);
			if (depolarized.minus(halfIdentity).norm() > 1e-9) { depolarizeOk = false; }
		}
		check("depolarizing(1.0) maps every state to exactly I/2", depolarizeOk);

		// 5. amplitudeDamping: |0> is a fixed point at any gamma; |1> fully decays to |0> at gamma=1
		MatrixComplex rho0 = DensityMatrix.of(ket0);
		MatrixComplex rho1 = DensityMatrix.of(ket1);
		boolean ampDampOk = true;
		for (double gamma : probs) {
			MatrixComplex damped0 = Decoherence.apply(rho0, Decoherence.amplitudeDamping(gamma), 0, 1);
			if (damped0.minus(rho0).norm() > 1e-9) { ampDampOk = false; }
		}
		MatrixComplex damped1Full = Decoherence.apply(rho1, Decoherence.amplitudeDamping(1.0), 0, 1);
		if (damped1Full.minus(rho0).norm() > 1e-9) { ampDampOk = false; }
		check("amplitudeDamping: |0> fixed point at any gamma, |1> decays to |0> at gamma=1", ampDampOk);

		// 6. Trace preservation: Tr(rho')=1 for every channel, several p, several states
		boolean traceOk = true;
		for (MatrixComplex psi : pureStates) {
			MatrixComplex rho = DensityMatrix.of(psi);
			for (double p : probs) {
				traceOk &= Math.abs(Decoherence.apply(rho, Decoherence.bitFlip(p), 0, 1).trace().rep() - 1.0) < 1e-9;
				traceOk &= Math.abs(Decoherence.apply(rho, Decoherence.phaseFlip(p), 0, 1).trace().rep() - 1.0) < 1e-9;
				traceOk &= Math.abs(Decoherence.apply(rho, Decoherence.depolarizing(p), 0, 1).trace().rep() - 1.0) < 1e-9;
				traceOk &= Math.abs(Decoherence.apply(rho, Decoherence.amplitudeDamping(p), 0, 1).trace().rep() - 1.0) < 1e-9;
			}
		}
		check("Tr(rho')=1 (trace preservation) for all channels x 6 p x 4 states", traceOk);

		// 7. Decoherence of one qubit of a Bell pair: von Neumann entropy of the FULL 2-qubit state
		//    goes from 0 (pure) at p=0 to strictly positive (mixed) as noise increases -- the direct
		//    signature of decoherence (a pure state becoming mixed).
		MatrixComplex bellRho = DensityMatrix.of(Qubits.bellPhiPlus());
		double entropyNoNoise = DensityMatrix.vonNeumannEntropy(bellRho);
		double entropyLowNoise = DensityMatrix.vonNeumannEntropy(Decoherence.apply(bellRho, Decoherence.phaseFlip(0.1), 0, 2));
		double entropyHighNoise = DensityMatrix.vonNeumannEntropy(Decoherence.apply(bellRho, Decoherence.phaseFlip(0.5), 0, 2));
		check("full-system entropy: 0 at p=0, increases with phase-flip noise on 1 qubit of a Bell pair",
				entropyNoNoise < 1e-9 && entropyLowNoise > entropyNoNoise && entropyHighNoise > entropyLowNoise);

		// 8. No-signalling: the REDUCED density matrix of the untouched qubit B (tracing out A) is
		//    EXACTLY unchanged by noise applied to A alone, at every noise strength -- a fundamental
		//    property of trace-preserving quantum channels (Tr_A(E_A(rho)) = Tr_A(rho) always), not
		//    just an approximate/qualitative check.
		MatrixComplex reducedBBefore = DensityMatrix.partialTrace(bellRho, 2, 0);
		boolean noSignallingOk = true;
		for (double p : probs) {
			MatrixComplex afterBitFlipOnA = Decoherence.apply(bellRho, Decoherence.bitFlip(p), 0, 2);
			MatrixComplex afterPhaseFlipOnA = Decoherence.apply(bellRho, Decoherence.phaseFlip(p), 0, 2);
			MatrixComplex afterDepolarizeOnA = Decoherence.apply(bellRho, Decoherence.depolarizing(p), 0, 2);
			MatrixComplex reducedAfterBitFlip = DensityMatrix.partialTrace(afterBitFlipOnA, 2, 0);
			MatrixComplex reducedAfterPhaseFlip = DensityMatrix.partialTrace(afterPhaseFlipOnA, 2, 0);
			MatrixComplex reducedAfterDepolarize = DensityMatrix.partialTrace(afterDepolarizeOnA, 2, 0);
			if (reducedAfterBitFlip.minus(reducedBBefore).norm() > 1e-9) { noSignallingOk = false; }
			if (reducedAfterPhaseFlip.minus(reducedBBefore).norm() > 1e-9) { noSignallingOk = false; }
			if (reducedAfterDepolarize.minus(reducedBBefore).norm() > 1e-9) { noSignallingOk = false; }
		}
		check("no-signalling: reduced state of the untouched qubit B is EXACTLY unchanged by noise on A, all p",
				noSignallingOk);

		// 9. apply() rejects an out-of-range probability at the channel-factory level
		try {
			Decoherence.bitFlip(1.5);
			check("bitFlip() rejects p outside [0,1]", false);
		} catch (IllegalArgumentException e) {
			check("bitFlip() rejects p outside [0,1]", true);
		}
		try {
			Decoherence.amplitudeDamping(-0.1);
			check("amplitudeDamping() rejects gamma outside [0,1]", false);
		} catch (IllegalArgumentException e) {
			check("amplitudeDamping() rejects gamma outside [0,1]", true);
		}

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static boolean isComplete(MatrixComplex[] kraus) {
		MatrixComplex sum = new MatrixComplex(2, 2);
		for (MatrixComplex e : kraus) {
			sum = sum.plus(e.adjoint().times(e));
		}
		MatrixComplex identity = Qubits.identity2();
		return sum.minus(identity).norm() < 1e-9;
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
