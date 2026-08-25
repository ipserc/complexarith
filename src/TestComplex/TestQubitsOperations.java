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

		Qubits.ket0().println("|0>");
		Qubits.ket1().println("|1>");
		check("identity2()|0> == |0>", identity.times(Qubits.ket0()).equals(Qubits.ket0(), 9));
		check("identity2()|1> == |1>", identity.times(Qubits.ket1()).equals(Qubits.ket1(), 9));

		/*
		 *   Explicación de plus
		 *   Es el estado que produce Qubits.hadamard() al aplicarse sobre |0⟩ — una superposición equiprobable de medir 0 o 1 
		 *   (cada uno con probabilidad |1/√2|² = 0.5), a diferencia de |0⟩ o |1⟩ que son estados propios de la base computacional sin incertidumbre.
		 *   
		 *   En el test lo uso como tercer caso de identity2(): comprobar que la identidad también deja invariante un estado en superposición, 
		 *   no solo los estados base — un caso algo más exigente porque involucra coeficientes no triviales, no solo  0/1.
		 */
		MatrixComplex plus = Qubits.ket0().plus(Qubits.ket1()).normalizeByCols();
		plus.println("|+>");
		check("identity2()|+> == |+>", identity.times(plus).equals(plus, 9));

		MatrixComplex minus = Qubits.ket0().minus(Qubits.ket1()).normalizeByCols();
		minus.println("|->");
		check("identity2()|-> == |->", identity.times(minus).equals(minus, 9));

		check("plus.opposite().opposite() == plus", plus.opposite().opposite().equals(plus, 9));

		check("identity2() * pauliX() == pauliX()", identity.times(Qubits.pauliX()).equals(Qubits.pauliX(), 9));
		check("pauliX() * identity2() == pauliX()", Qubits.pauliX().times(identity).equals(Qubits.pauliX(), 9));

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}

}
