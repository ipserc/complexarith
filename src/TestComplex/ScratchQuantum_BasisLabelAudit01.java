package TestComplex;

import com.ipserc.arith.quantum.Qubits;

public class ScratchQuantum_BasisLabelAudit01 {

	public static void main(String[] args) {
		// n=1
		check(Qubits.basisLabel(0, 1), "|0>");
		check(Qubits.basisLabel(1, 1), "|1>");

		// n=2 -- el caso pedido: |00>, |01>, |10>, |11>
		check(Qubits.basisLabel(0, 2), "|00>");
		check(Qubits.basisLabel(1, 2), "|01>");
		check(Qubits.basisLabel(2, 2), "|10>");
		check(Qubits.basisLabel(3, 2), "|11>");

		// n=3
		check(Qubits.basisLabel(5, 3), "|101>");

		// overload basado en el ket real
		check(Qubits.basisLabel(Qubits.ket(1, 0, 1), 5), "|101>");

		// validaciones
		try {
			Qubits.basisLabel(4, 2);
			System.out.println("FAIL: no lanzo con index fuera de rango");
		} catch (IllegalArgumentException e) {
			System.out.println("OK: index fuera de rango lanza -- " + e.getMessage());
		}
		try {
			Qubits.basisLabel(0, 0);
			System.out.println("FAIL: no lanzo con n<1");
		} catch (IllegalArgumentException e) {
			System.out.println("OK: n<1 lanza -- " + e.getMessage());
		}
	}

	private static void check(String actual, String expected) {
		System.out.println((actual.equals(expected) ? "OK: " : "FAIL: ") + actual + " (esperado " + expected + ")");
	}
}
