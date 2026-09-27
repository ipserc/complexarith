package TestComplex;

import com.ipserc.arith.quantum.Qubits;

public class ScratchBraLabelAudit01 {

	public static void main(String[] args) {
		// bra(ket0()), bra(ket1())
		check(Qubits.braLabel(Qubits.bra(Qubits.ket0())), "<0|");
		check(Qubits.braLabel(Qubits.bra(Qubits.ket1())), "<1|");

		// n=2/3, misma correspondencia que ketLabel
		check(Qubits.braLabel(Qubits.bra(Qubits.ket(1, 0))), "<10|");
		check(Qubits.braLabel(Qubits.bra(Qubits.ket(1, 0, 1))), "<101|");

		// validaciones
		try {
			Qubits.braLabel(Qubits.ket0()); // columna, no fila
			System.out.println("FAIL: no lanzo con columna en vez de fila");
		} catch (IllegalArgumentException e) {
			System.out.println("OK: columna en vez de fila lanza -- " + e.getMessage());
		}
		try {
			Qubits.braLabel(Qubits.bra(Qubits.hadamard().times(Qubits.ket0())));
			System.out.println("FAIL: no lanzo con superposicion");
		} catch (IllegalArgumentException e) {
			System.out.println("OK: superposicion lanza -- " + e.getMessage());
		}
	}

	private static void check(String actual, String expected) {
		System.out.println((actual.equals(expected) ? "OK: " : "FAIL: ") + actual + " (esperado " + expected + ")");
	}
}
