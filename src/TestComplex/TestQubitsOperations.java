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

	static boolean check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
		return condition;
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
		Complex.printBoxText(boxShape, boxMargin, "OPERACIONES CON KETS");
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
		Complex.printLineText(boxShape, boxMargin, "operadores Hadamard e identidad con Ket |+>", true, false);
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
		Complex.printLineText(boxShape, boxMargin, "operadores Hadamard e identidad con ket |->", true, false);
		MatrixComplex ketMinus = ket0.minus(ket1).normalizeByCols();
		ketMinus.println("|->");
		check("identity2()|-> == |->", identity.times(ketMinus).equals(ketMinus, 9));

		Complex.printLineText(boxShape, boxMargin, "Con Hadamard y |1> : Qubits.hadamard().times(ket1) ", true, false);
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
		Complex.printBoxText(boxShape, boxMargin, "OPERACIONES CON BRAS");
		Complex.printLineText(boxShape, boxMargin, "operador identidad con Bras <0| y <1|");
		MatrixComplex bra0 = Qubits.bra(Qubits.ket0());
		bra0.println("<0| = ");
		MatrixComplex bra1 = Qubits.bra(Qubits.ket1());
		bra1.println("<1| = ");
		check("<0|identity2() == <0|", bra0.times(identity).equals(bra0, 9));
		check("<1|identity2() == <1|", bra1.times(identity).equals(bra1, 9));

		Complex.printLineText(boxShape, boxMargin, "operadores Hadamard e identidad con Bra <+|", true, false);
		MatrixComplex braPlus = bra0.plus(bra1).normalizeByRows();
		braPlus.println("<+| = ");
		check("<+|identity2() == <+|", braPlus.times(identity).equals(braPlus, 9));

		Complex.printLineText(boxShape, boxMargin, "Con Hadamard y <0| : hadBra0 = bra0.times(Qubits.hadamard())", true, false);
		MatrixComplex hadBra0 = bra0.times(Qubits.hadamard());
		check("<0|*hadamard == <+|",hadBra0.equals(braPlus, 9));
		
		Complex.printLineText(boxShape, boxMargin, "operadores Hadamard e identidad con bra <-|", true, false);
		MatrixComplex braMinus = bra0.minus(bra1).normalizeByRows();
		braMinus.println("<-| = ");
		check("<-|identity2() == <-|", braMinus.times(identity).equals(braMinus, 9));

		Complex.printLineText(boxShape, boxMargin, "Con Hadamard y <1| : bra1.times(Qubits.hadamard())", true, false);
		MatrixComplex hadBra1 = bra1.times(Qubits.hadamard());
		check("hadamard*|1> == |->",hadBra1.equals(braMinus, 9));

		check("braPlus.opposite().opposite() == braPlus", braPlus.opposite().opposite().equals(braPlus, 9));
		check("braMinus.opposite().opposite() == braMinus", braMinus.opposite().opposite().equals(braMinus, 9));

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		
		/* **********************************************************************************************
		 * OPERACIONES CON PRODUCTO ESCALAR CON KETS Y BRAS
		 ********************************************************************************************** */
		Complex.printBoxText(boxShape, boxMargin, "OPERACIONES CON PRODUCTO ESCALAR CON KETS Y BRAS");
		
		Complex.printLineText(boxShape, boxMargin, "Kets de partida |0> y <0|");
		ket0.println("ket0 = ");
		bra0.println("bra0 = ");
		Qubits.bra(ket0).println("Qbits.bra(ket0) = ");

		check("ket0 == ket(bra0)", ket0.equals(Qubits.ket(bra0)));
		
		Complex.printLineText(boxShape, boxMargin, "Producto escalar de Kets |0> y <0| con nº Complejo", true, false);

		Complex cnum = new Complex("2-i");
		
		MatrixComplex ket0Cnum = ket0.times(cnum);
		ket0Cnum.println("ket0Cnum: |0> * " + cnum.toString() + " = ");
		MatrixComplex bra0Cnum = bra0.times(cnum.conjugate());
		bra0Cnum.println("bra0Cnum: <0| * " + cnum.conjugate().toString() + " = ");
		
		Qubits.bra(ket0Cnum).println("bra(ket0Cnum) = ");
		Qubits.ket(bra0Cnum).println("ket(bra0Cnum) = ");

		check("ket0Cnum == ket(bra0Cnum)", ket0Cnum.equals(Qubits.ket(bra0Cnum)));
		check("ket0Cnum == ket(bra0Cnum).conjugate()", ket0Cnum.equals(Qubits.ket(bra0Cnum).conjugate()));

		bra0.transpose().times(cnum.conjugate()).println("bra0.transpose().times(cnum.conjugate())");
		bra0Cnum.transpose().println("bra0Cnum.transpose()");
		check("ket0 * cnum <--> bra0 * cnum", ket0Cnum.equals(bra0Cnum.transpose()));
		Qubits.ket(bra0.times(cnum.conjugate())).println("Qubits.ket(bra0.times(cnum.conjugate()))");
		
		Complex.printLineText(boxShape, boxMargin, "Esto lo aclara todo");
		check("ket0 * cnum == ket(bra0 * cnum.conjugate)", ket0Cnum.equals(Qubits.ket(bra0.times(cnum.conjugate()))));
		check("ket0 * cnum == ket(bra0) * cnum.conjugate", ket0Cnum.equals(Qubits.ket(bra0).times(cnum.conjugate())));
		check("ket0 * cnum == ket(bra0) * cnum", ket0Cnum.equals(Qubits.ket(bra0).times(cnum)));
		Complex.printLineText(boxShape, boxMargin, "FIN - Esto lo aclara todo - FIN");
		
		//
		// recalculo ket0cnum y bra0cnum adecuadamente para utilizarlos en adelante
		// kte0Cnum = ket0 * cnum
		// bra0cnum = bra(ket0cnum) = bra(ket0 * cnum) = bra(ket0) * bra(cnum) = bra0 * cnum.congugate
		//
		ket0Cnum = ket0.times(cnum);
		bra0Cnum = Qubits.bra(ket0Cnum);
		if (check("bra0Cnum == bra(ket0) * cnum.conjujate", bra0Cnum.equals(Qubits.bra(ket0).times(cnum.conjugate())))) {
			Complex.printLineText(boxShape, boxMargin, "Ya sé manejar el producto de números complejos dentro de operaciones kets y bras. ", false, true);
		} else {
			Complex.printLineText(boxShape+1, boxMargin, "NO sé manejar el producto de números complejos dentro de operaciones kets y bras. NO", false, true);

		}

		Complex.printLineText(boxShape, boxMargin, "Producto escalar de Kets |02> y <02| con otro nº Complejo", true, false);
		ket0Cnum.println("ket0Cnum =");
		bra0Cnum.println("bra0Cnum =");
		Complex cnum2 = new Complex ("1 + i");
		cnum2.println("cnum2 =");
		MatrixComplex ket0Cnum2 = ket0Cnum.times(cnum2);
		ket0Cnum2.println("ket0Cnum2: |02> * cnum2 = ");
		MatrixComplex bra0Cnum2 = bra0Cnum.times(cnum2.conjugate());
		bra0Cnum2.println("bra0Cnum2: <02| * cnum2.conjugate() = ");
		
		check("ket0Cnum2 <--> bra0Cnum2", ket0Cnum2.equals(Qubits.ket(bra0Cnum2)));
	
		Qubits.ket(bra0Cnum.times(cnum2.conjugate())).println("Qubits.ket(bra0Cnum.times(cnum2.conjugate()))");
		check("ket0Cnum * cnum2 == ket(bra0Cnum * cnum2,conjugate())", ket0Cnum2.equals(Qubits.ket(bra0Cnum.times(cnum2.conjugate()))));

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
				
		if (fail > 0) { System.exit(1); }
	}

}
