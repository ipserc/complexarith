package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Verifies MatrixComplex.timesEqRaw(MatrixComplex) (Camino A de rendimiento, Vector API,
 * Vigesimotercera sesion, ver Claude/ComplexArithRev.md): confirma que da resultado BIT A BIT
 * IDENTICO a times(MatrixComplex) en varios casos, incluida la auto-multiplicacion (cMatrix==this).
 */
public class ScratchTimesEqRawVerify01 {

	static boolean bitIdentical(MatrixComplex a, MatrixComplex b) {
		if (a.rows() != b.rows() || a.cols() != b.cols()) return false;
		for (int r = 0; r < a.rows(); ++r)
			for (int c = 0; c < a.cols(); ++c) {
				Complex ca = a.getItem(r, c);
				Complex cb = b.getItem(r, c);
				if (Double.doubleToLongBits(ca.rep()) != Double.doubleToLongBits(cb.rep())) return false;
				if (Double.doubleToLongBits(ca.imp()) != Double.doubleToLongBits(cb.imp())) return false;
				if (Double.doubleToLongBits(ca.mod()) != Double.doubleToLongBits(cb.mod())) return false;
				if (Double.doubleToLongBits(ca.pha()) != Double.doubleToLongBits(cb.pha())) return false;
			}
		return true;
	}

	static void runCase(String label, MatrixComplex a, MatrixComplex b) {
		MatrixComplex expected = a.times(b);
		MatrixComplex actual = a.copy().timesEqRaw(b);
		boolean ok = bitIdentical(expected, actual);
		System.out.println(label + " -> " + (ok ? "OK (bit-identico)" : "*** MISMATCH ***"));
		if (!ok) {
			expected.println("  expected (times):");
			actual.println("  actual (timesEqRaw):");
		}
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();

		// Caso 1: real, cuadrada, valores enteros pequenos.
		MatrixComplex a1 = new MatrixComplex("1,2,3;4,5,6;7,8,10");
		MatrixComplex b1 = new MatrixComplex("9,8,7;6,5,4;3,2,1");
		runCase("Caso 1 (real 3x3)", a1, b1);

		// Caso 2: complejo, cuadrada.
		MatrixComplex a2 = new MatrixComplex("1+2i,3-1i;0.5,-2+4i");
		MatrixComplex b2 = new MatrixComplex("2-3i,1;4,-1+1i");
		runCase("Caso 2 (complejo 2x2)", a2, b2);

		// Caso 3: no cuadrada (2x3 * 3x2).
		MatrixComplex a3 = new MatrixComplex("1,2,3;4,5,6");
		MatrixComplex b3 = new MatrixComplex("1,0;0,1;1,1");
		runCase("Caso 3 (no cuadrada 2x3 * 3x2)", a3, b3);

		// Caso 4: auto-multiplicacion (cMatrix == this), el caso de riesgo de aliasing.
		MatrixComplex a4 = new MatrixComplex("1,2;3,4");
		MatrixComplex expected4 = a4.times(a4);
		MatrixComplex actual4 = a4.copy().timesEqRaw(a4.copy());
		// tambien probar el aliasing genuino: mismo objeto como this y argumento.
		MatrixComplex a4b = a4.copy();
		MatrixComplex actual4b = a4b.timesEqRaw(a4b);
		boolean ok4 = bitIdentical(expected4, actual4) && bitIdentical(expected4, actual4b);
		System.out.println("Caso 4 (auto-multiplicacion, aliasing genuino) -> " + (ok4 ? "OK (bit-identico)" : "*** MISMATCH ***"));

		// Caso 5: matriz mayor, 8x8, valores fraccionarios deterministas (misma formula que
		// ScratchTaylorMercatorBench01.java de la Decimoctava sesion, sin aleatoriedad).
		int n = 8;
		MatrixComplex a5 = new MatrixComplex(n, n);
		MatrixComplex b5 = new MatrixComplex(n, n);
		for (int r = 0; r < n; ++r)
			for (int c = 0; c < n; ++c) {
				a5.setItem(r, c, new Complex(Math.sin(r + c * 0.37), Math.cos(r * 0.21 - c)));
				b5.setItem(r, c, new Complex(Math.cos(r - c * 0.13), Math.sin(r * 0.5 + c)));
			}
		runCase("Caso 5 (8x8 complejo, valores deterministas)", a5, b5);

		// Caso 6: dimension incompatible -- confirmar PARIDAD exacta con times(), no solo que
		// ambos "avisen": times() en este caso concreto (2x2 * 1x3) avisa por stderr y LUEGO
		// lanza ArrayIndexOutOfBoundsException al recorrer una fila de cMatrix que no existe --
		// confirmado con ScratchTimesDirectCheck01.java. timesEqRaw() debe fallar exactamente
		// igual (misma estructura de bucle), no silenciar ni comportarse distinto.
		MatrixComplex a6 = new MatrixComplex("1,2;3,4");
		MatrixComplex b6 = new MatrixComplex("1,2,3");
		System.out.println("Caso 6 (dimensiones incompatibles, se espera aviso por stderr + ArrayIndexOutOfBoundsException, igual que times()):");
		try {
			a6.copy().timesEqRaw(b6);
			System.out.println("  *** no lanzo excepcion -- diverge de times(), revisar ***");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("  OK: misma excepcion que times() (" + e + ")");
		} catch (Exception e) {
			System.out.println("  *** excepcion de tipo distinto a times(): " + e);
		}
	}
}
