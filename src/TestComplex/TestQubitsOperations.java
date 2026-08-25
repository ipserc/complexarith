package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Qubits;

/**
 * Bateria de pruebas sobre operadores de {@link Qubits} aplicados a kets/operadores. Primer test:
 * el operador identidad {@code Qubits.identity2()} debe dejar cualquier estado invariante, tanto
 * aplicado a un ket como compuesto con otro operador.
 */
public class TestQubitsOperations {

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

		Complex.printBoxText(boxShape, boxMargin, "Qubits.identity2(): operador identidad");

		MatrixComplex identity = Qubits.identity2();

		check("identity2()|0> == |0>", identity.times(Qubits.ket0()).equals(Qubits.ket0(), 9));
		check("identity2()|1> == |1>", identity.times(Qubits.ket1()).equals(Qubits.ket1(), 9));

		MatrixComplex plus = Qubits.ket0().plus(Qubits.ket1()).normalizeByCols();
		check("identity2()|+> == |+>", identity.times(plus).equals(plus, 9));

		check("identity2() * pauliX() == pauliX()", identity.times(Qubits.pauliX()).equals(Qubits.pauliX(), 9));
		check("pauliX() * identity2() == pauliX()", Qubits.pauliX().times(identity).equals(Qubits.pauliX(), 9));

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}

}
