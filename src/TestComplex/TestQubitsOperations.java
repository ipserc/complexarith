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

		Complex.printBoxText(boxShape, boxMargin, "Operaciones con Qubits.identity2(): operador identidad");

		MatrixComplex identity = Qubits.identity2();
		
		/* **********************************************************************************************
		 * OPERACIONES CON KETS
		 ********************************************************************************************** */
		Complex.printLineText(boxShape, boxMargin, "operador identidad con Kets |0> y |1>");
		MatrixComplex ket0 = Qubits.ket0();
		ket0.println("|0>");
		MatrixComplex ket1 = Qubits.ket1();
		ket1.println("|1>");
		check("identity2()|0> == |0>", identity.times(ket0).equals(ket0, 9));
		check("identity2()|1> == |1>", identity.times(ket1).equals(ket1, 9));

		/*
		 *   Explicación de plus
		 *   Es el estado que produce Qubits.hadamard() al aplicarse sobre |0⟩ — una superposición equiprobable de medir 0 o 1 
		 *   (cada uno con probabilidad |1/√2|² = 0.5), a diferencia de |0⟩ o |1⟩ que son estados propios de la base computacional sin incertidumbre.
		 *   
		 *   En el test lo uso como tercer caso de identity2(): comprobar que la identidad también deja invariante un estado en superposición, 
		 *   no solo los estados base — un caso algo más exigente porque involucra coeficientes no triviales, no solo  0/1.
		 */
		Complex.printLineText(boxShape, boxMargin, "operadores Hadamard e identidad con Ket |+>");
		MatrixComplex ketPlus = ket0.plus(ket1).normalizeByCols();
		ketPlus.println("|+>");
		check("identity2()|+> == |+>", identity.times(ketPlus).equals(ketPlus, 9));

		System.out.println("Con Hadamard y |0> : Qubits.hadamard().times(ket0)");
		MatrixComplex hadKet0 = Qubits.hadamard().times(ket0);
		check("hadamard*|0> == |+>",hadKet0.equals(ketPlus, 9));
		
		/*
		 * Explicación de minus
		 * > ¿|-> no sería |+>.opposite()?                                                                                                                                                                                                               
			● No — |+⟩.opposite() da −|+⟩, no |−⟩. Son estados distintos:
			
			  - |+⟩ = (|0⟩+|1⟩)/√2 = [0.7071, 0.7071]
			  - |+⟩.opposite() = −|+⟩ = [−0.7071, −0.7071]
			  - |−⟩ = (|0⟩−|1⟩)/√2 = [0.7071, −0.7071]
			
			  opposite() niega ambas componentes por igual (es multiplicar por el escalar −1), mientras que pasar de |+⟩ a |−⟩ 
			  requiere negar solo la componente de |1⟩, no la de |0⟩ — eso es lo que hace Qubits.pauliZ() aplicado a |+⟩, no opposite().
			
			  De hecho, físicamente −|+⟩ y |+⟩ son el mismo estado cuántico: un signo global (fase global) no es observable 
			  — cualquier medida da las mismas probabilidades. Por eso el test que añadí comprueba plus.opposite().opposite() == plus
			  (involución algebraica), no plus.opposite() == minus (eso sería falso).
			
			  Tu código original, Qubits.ket0().plus(Qubits.ket1()).normalizeByCols().opposite(), sí calculaba −|+⟩ — que además de no ser |−⟩, 
			  casualmente reveló el bug de opposite() porque el vector es no cuadrado. Al final lo dejé como
			  Qubits.ket0().minus(Qubits.ket1()).normalizeByCols(), que sí es |−⟩ de verdad.
		 */
		Complex.printLineText(boxShape, boxMargin, "operadores Hadamard e identidad con ket |->");
		MatrixComplex ketMinus = ket0.minus(ket1).normalizeByCols();
		ketMinus.println("|->");
		check("identity2()|-> == |->", identity.times(ketMinus).equals(ketMinus, 9));

		System.out.println("Con Hadamard y |1> : Qubits.hadamard().times(ket1) ");
		MatrixComplex hadKet1 = Qubits.hadamard().times(ket1);
		check("hadamard*|1> == |->",hadKet1.equals(ketMinus, 9));

		check("ketPlus.opposite().opposite() == ketPlus", ketPlus.opposite().opposite().equals(ketPlus, 9));
		check("ketMinus.opposite().opposite() == ketMinus", ketMinus.opposite().opposite().equals(ketMinus, 9));

		check("identity2() * pauliX() == pauliX()", identity.times(Qubits.pauliX()).equals(Qubits.pauliX(), 9));
		check("pauliX() * identity2() == pauliX()", Qubits.pauliX().times(identity).equals(Qubits.pauliX(), 9));

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
	
		/* **********************************************************************************************
		 * OPERACIONES CON BRAS
		 ********************************************************************************************** */
		Complex.printLineText(boxShape, boxMargin, "operador identidad con Bras <0| y <1|");
		MatrixComplex bra0 = Qubits.bra(Qubits.ket0());
		bra0.println("<0| = ");
		MatrixComplex bra1 = Qubits.bra(Qubits.ket1());
		bra1.println("<1| = ");
		check("<0|identity2() == <0|", bra0.times(identity).equals(bra0, 9));
		check("<1|identity2() == <1|", bra1.times(identity).equals(bra1, 9));

		Complex.printLineText(boxShape, boxMargin, "operadores Hadamard e identidad con Bra <+|");
		MatrixComplex braPlus = bra0.plus(bra1).normalizeByRows();
		braPlus.println("<+| = ");
		check("<+|identity2() == <+|", braPlus.times(identity).equals(braPlus, 9));

		System.out.println("Con Hadamard y <0| : hadBra0 = bra0.times(Qubits.hadamard())");
		MatrixComplex hadBra0 = bra0.times(Qubits.hadamard());
		check("<0|*hadamard == <+|",hadBra0.equals(braPlus, 9));
		
		Complex.printLineText(boxShape, boxMargin, "operadores Hadamard e identidad con bra <-|");
		MatrixComplex braMinus = bra0.minus(bra1).normalizeByRows();
		braMinus.println("<-| = ");
		check("<-|identity2() == <-|", braMinus.times(identity).equals(braMinus, 9));

		System.out.println("Con Hadamard y <1| : bra1.times(Qubits.hadamard())");
		MatrixComplex hadBra1 = bra1.times(Qubits.hadamard());
		check("hadamard*|1> == |->",hadBra1.equals(braMinus, 9));

		check("braPlus.opposite().opposite() == braPlus", braPlus.opposite().opposite().equals(braPlus, 9));
		check("braMinus.opposite().opposite() == braMinus", braMinus.opposite().opposite().equals(braMinus, 9));

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		
		/* **********************************************************************************************
		 * OPERACIONES CON PRODUCTO ESCALAR CON KETS Y BTRAS
		 ********************************************************************************************** */
		Complex cnum = new Complex("2-i");
		
		MatrixComplex ket0Cnum = ket0.times(cnum);
		ket0Cnum.println("|0> * " + cnum.toString() + " = ");
		MatrixComplex bra0Cnum = bra0.times(cnum);
		bra0Cnum.println("<0| * " + cnum.toString() + " = ");
		
		Qubits.bra(ket0Cnum).println("bra(ket0Cnum) = ");
		Qubits.ket(bra0Cnum).println("ket(bra0Cnum) = ");
		
		
				
				
		if (fail > 0) { System.exit(1); }
	}

}
