package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Investigacion dedicada del bug real preexistente encontrado en el Bloque E de "nuevos
 * instrumentos matematicos" (ver Claude/ComplexArithRev.md): Complex.isPureReal()/isPureImaginary()
 * excluian el propio cero de ambas categorias (una guarda extra sobre rePartNull()/imPartNull()
 * respectivamente), rompiendo isInteger() e isIntegerNegativeZero() en z=0 exacto -- con efecto en
 * cadena en gamma_zones/gamma_integral/gamma_weiertrass/gamma_euler (signo del polo equivocado) y
 * en MatrixComplex.isHermitian()/isAntiHermitian() (cualquier matriz con un cero exacto en la
 * diagonal, mal clasificada). Arreglado quitando la guarda extra en ambos metodos.
 */
public class ScratchIsPureRealZeroAudit01 {

	static int ok = 0;
	static int fail = 0;

	static void check(String label, boolean pass) {
		if (pass) ok++; else fail++;
		System.out.printf("%-70s %s%n", label, pass ? "OK" : "***FAIL***");
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(8);

		System.out.println("=== (1) El propio cero es, correctamente, tanto puro real como puro imaginario ===");
		Complex zero = new Complex(0, 0);
		check("Complex(0,0).isPureReal()==true", zero.isPureReal());
		check("Complex(0,0).isPureImaginary()==true", zero.isPureImaginary());
		check("Complex(0,0).isInteger()==true", zero.isInteger());
		check("Complex(0,0).isIntegerPositiveZero()==true", zero.isIntegerPositiveZero());
		check("Complex(0,0).isIntegerNegativeZero()==true", zero.isIntegerNegativeZero());

		System.out.println("\n=== (2) No hay regresion para valores no nulos ===");
		Complex real5 = new Complex(5, 0);
		Complex imag5 = new Complex(0, 5);
		Complex mixed = new Complex(1, 1);
		check("Complex(5,0).isPureReal()==true", real5.isPureReal());
		check("Complex(5,0).isPureImaginary()==false", !real5.isPureImaginary());
		check("Complex(0,5).isPureImaginary()==true", imag5.isPureImaginary());
		check("Complex(0,5).isPureReal()==false", !imag5.isPureReal());
		check("Complex(1,1).isPureReal()==false", !mixed.isPureReal());
		check("Complex(1,1).isPureImaginary()==false", !mixed.isPureImaginary());

		System.out.println("\n=== (3) gamma_zones/gamma_integral/gamma_weiertrass/gamma_euler en z=0: polo con signo correcto (+Infinity) ===");
		check("gamma_zones(0)==+Infinity", Complex.gamma_zones(zero).rep() == Double.POSITIVE_INFINITY);
		check("gamma_integral(0)==+Infinity", Complex.gamma_integral(zero).rep() == Double.POSITIVE_INFINITY);
		check("gamma_weiertrass(0)==+Infinity", Complex.gamma_weiertrass(zero).rep() == Double.POSITIVE_INFINITY);
		check("gamma_euler(0)==+Infinity", Complex.gamma_euler(zero).rep() == Double.POSITIVE_INFINITY);

		System.out.println("\n=== (4) isHermitian/isAntiHermitian con un cero exacto en la diagonal ===");
		MatrixComplex h = new MatrixComplex(2);
		h.setItem(0, 0, new Complex(0, 0));
		h.setItem(0, 1, new Complex(1, 2));
		h.setItem(1, 0, new Complex(1, -2));
		h.setItem(1, 1, new Complex(3, 0));
		check("Matriz Hermitiana con diagonal[0]=0 -> isHermitian()==true", h.isHermitian());

		MatrixComplex ah = new MatrixComplex(2);
		ah.setItem(0, 0, new Complex(0, 0));
		ah.setItem(0, 1, new Complex(1, 2));
		ah.setItem(1, 0, new Complex(-1, 2));
		ah.setItem(1, 1, new Complex(0, 3));
		check("Matriz anti-Hermitiana con diagonal[0]=0 -> isAntiHermitian()==true", ah.isAntiHermitian());

		System.out.println("\n=== (5) Caso real de TestLU02.java, matriz real antisimetrica (diagonal toda cero) ===");
		MatrixComplex m = new MatrixComplex(""
				+ " 0,-5,  3, 7;"
				+ " 5, 0,-11,-8;"
				+ "-3,11,  0, 1;"
				+ "-7, 8, -1, 0");
		check("Matriz real antisimetrica -> isSkewHermitian()==true (antes del fix daba false)", m.isSkewHermitian());
		check("La misma matriz -> isHermitian()==false (sigue siendo correcto)", !m.isHermitian());

		System.out.println("\n" + ok + " OK, " + fail + " FAIL");
	}
}
