package TestComplex;

import com.ipserc.arith.quantum.Qubits;

public class ScratchKetTextAudit01 {
	public static void main(String[] args) {
		System.out.println(Qubits.ketText(Qubits.ket0()) + " (esperado |0>)");
		System.out.println(Qubits.ketText(Qubits.ket1()) + " (esperado |1>)");
		System.out.println(Qubits.ketText(Qubits.ket(1, 0, 1)) + " (esperado |101>)");
		try {
			Qubits.ketText(Qubits.hadamard().times(Qubits.ket0()));
			System.out.println("FAIL: no lanzo con superposicion");
		} catch (IllegalArgumentException e) {
			System.out.println("OK: superposicion lanza -- " + e.getMessage());
		}
	}
}
