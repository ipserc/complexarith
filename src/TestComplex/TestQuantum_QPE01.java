package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.QPE;
import com.ipserc.arith.quantum.Qubits;

/**
 * Quantum Phase Estimation (QPE) de juguete: {@code U = Qubits.phaseGate(k0)}, autovector {@code
 * Qubits.ket1()} (autovalor {@code e^(2*pi*i/2^k0)}, fase {@code theta = 1/2^k0}), con {@code
 * t=k0} qubits de conteo -- asi theta es EXACTAMENTE representable en t bits, y el registro de
 * conteo debe colapsar a {@code |2^t*theta> = |1>} con probabilidad exactamente 1 (sin ambiguedad
 * de redondeo), igual que se hizo con la fase exacta al verificar {@code QFT.circuit(n)|0...0>}.
 */
public class TestQuantum_QPE01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedON(4);
		int boxMargin = 65;
		int boxShape = 3;

		Complex.printBoxText(boxShape, boxMargin, "QPE de juguete: U=phaseGate(k0), autovector |1>, t=k0");

		int[] k0Values = { 2, 3 };
		for (int k0 : k0Values) {
			int t = k0;
			int expectedIndex = 1;	// 2^t * theta = 2^t * (1/2^k0) = 1, ya que t==k0

			Complex.printBoxText(boxShape, boxMargin, "k0=t=" + k0 + " (theta=1/" + (1 << k0) + ")");

			MatrixComplex u = Qubits.phaseGate(k0);
			double[] probs = QPE.countingProbabilities(t, u, Qubits.ket1());

			System.out.print("Probabilidades registro de conteo: [");
			for (int idx = 0; idx < probs.length; ++idx) {
				System.out.print((idx > 0 ? ", " : "") + String.format("%.6f", probs[idx]));
			}
			System.out.println("]");

			double sum = 0.0;
			for (double p : probs) { sum += p; }
			check("k0=t=" + k0 + ": las probabilidades del registro de conteo suman 1", Math.abs(sum - 1.0) < 1e-9);

			check("k0=t=" + k0 + ": probabilidad ~1 en el indice esperado j=" + expectedIndex,
					Math.abs(probs[expectedIndex] - 1.0) < 1e-9);

			boolean restZero = true;
			for (int idx = 0; idx < probs.length; ++idx) {
				if (idx != expectedIndex && probs[idx] > 1e-9) { restZero = false; }
			}
			check("k0=t=" + k0 + ": el resto de indices tiene probabilidad ~0", restZero);

			MatrixComplex circuit = QPE.circuit(t, u);
			MatrixComplex shouldBeIdentity = circuit.times(circuit.adjoint());
			check("k0=t=" + k0 + ": QPE.circuit(t,U) es unitaria (M*M^dagger == I)",
					shouldBeIdentity.equals(MatrixComplex.eye(1 << (t + 1)), 9));
		}

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}

}
