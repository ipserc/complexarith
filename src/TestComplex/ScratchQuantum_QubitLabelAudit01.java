package TestComplex;

import com.ipserc.arith.quantum.Qubits;

public class ScratchQuantum_QubitLabelAudit01 {

	public static void main(String[] args) {
		// ket -> mismo resultado que ketLabel()
		check(Qubits.qubitLabel(Qubits.ket0()), "|0>");
		check(Qubits.qubitLabel(Qubits.ket(1, 0, 1)), "|101>");

		// bra -> mismo resultado que braLabel()
		check(Qubits.qubitLabel(Qubits.bra(Qubits.ket1())), "<1|");
		check(Qubits.qubitLabel(Qubits.bra(Qubits.ket(1, 0, 1))), "<101|");

		// validaciones
		try {
			Qubits.qubitLabel(Qubits.hadamard()); // 2x2, ni columna ni fila
			System.out.println("FAIL: no lanzo con matriz 2x2");
		} catch (IllegalArgumentException e) {
			System.out.println("OK: matriz 2x2 lanza -- " + e.getMessage());
		}
		try {
			Qubits.qubitLabel(buildOneByOne()); // 1x1, ambiguo
			System.out.println("FAIL: no lanzo con matriz 1x1");
		} catch (IllegalArgumentException e) {
			System.out.println("OK: matriz 1x1 (ambigua) lanza -- " + e.getMessage());
		}
		try {
			Qubits.qubitLabel(Qubits.hadamard().times(Qubits.ket0()));
			System.out.println("FAIL: no lanzo con superposicion");
		} catch (IllegalArgumentException e) {
			System.out.println("OK: superposicion lanza -- " + e.getMessage());
		}
	}

	private static com.ipserc.arith.matrixcomplex.MatrixComplex buildOneByOne() {
		com.ipserc.arith.matrixcomplex.MatrixComplex m = new com.ipserc.arith.matrixcomplex.MatrixComplex(1, 1);
		m.setItem(0, 0, 1.0);
		return m;
	}

	private static void check(String actual, String expected) {
		System.out.println((actual.equals(expected) ? "OK: " : "FAIL: ") + actual + " (esperado " + expected + ")");
	}
}
