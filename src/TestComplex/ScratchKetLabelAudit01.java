package TestComplex;

import com.ipserc.arith.quantum.Qubits;

public class ScratchKetLabelAudit01 {
	public static void main(String[] args) {
		System.out.println(Qubits.ketLabel(Qubits.ket0()) + " (esperado |0>)");
		System.out.println(Qubits.ketLabel(Qubits.ket1()) + " (esperado |1>)");
		System.out.println(Qubits.ketLabel(Qubits.ket(1, 0, 1)) + " (esperado |101>)");
		try {
			Qubits.ketLabel(Qubits.hadamard().times(Qubits.ket0()));
			System.out.println("FAIL: no lanzo con superposicion");
		} catch (IllegalArgumentException e) {
			System.out.println("OK: superposicion lanza -- " + e.getMessage());
		}
	}
}
