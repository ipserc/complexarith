package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for {@code MatrixComplex.nullspaceBasis()} (Fase A de la generalizacion de
 * {@code Jordan.java} a multiplicidad geometrica &gt;1, ver {@code Claude/ComplexArithRev.md}):
 * a diferencia de {@code kernel()}/{@code kernel(Complex)} (un unico vector, todas las variables
 * libres al mismo escalar -- la herramienta correcta solo cuando el nucleo es 1-dimensional),
 * devuelve una base genuina de {@code nullity()} vectores independientes.
 * <p>
 * Tres comprobaciones por caso:
 * <ol>
 * <li>El numero de vectores de la base coincide con {@code cols()-rank()} (oracle ya verificado
 * en sesiones anteriores contra fuerza bruta).</li>
 * <li>Cada vector satisface {@code M*v=0} (residuo a precision de maquina).</li>
 * <li>Los vectores de la base son linealmente independientes entre si (el rango de la propia
 * base coincide con su numero de filas).</li>
 * </ol>
 * Incluye el caso real {@code N=A+2I} de {@code TestJordan02} (nulidad 2 -- la matriz de esa
 * prueba tiene multiplicidad geometrica 2, el caso que hace fallar {@code Jordan.factorize()}
 * hoy, motivo de esta Fase A).
 */
public class TestNullspace01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void report(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + " -- " + detail);
		if (ok) ++pass; else ++fail;
	}

	private static void checkNullspace(String label, MatrixComplex m, int expectedNullity, double tolerance) {
		MatrixComplex basis = m.nullspaceBasis();
		int actualNullity = m.cols() - m.rank();

		boolean nullityMatchesRank = basis.rows() == actualNullity;
		boolean nullityMatchesExpected = expectedNullity < 0 || basis.rows() == expectedNullity;

		double maxResidual = 0.0;
		for (int i = 0; i < basis.rows(); ++i) {
			MatrixComplex v = basis.getRow(i).transpose();
			MatrixComplex mv = m.times(v);
			for (int r = 0; r < mv.rows(); ++r) maxResidual = Math.max(maxResidual, mv.getItem(r, 0).mod());
		}

		boolean independent = basis.rows() == 0 || basis.rank() == basis.rows();

		boolean ok = nullityMatchesRank && nullityMatchesExpected && maxResidual < tolerance && independent;
		report(label, ok, "nullity=" + basis.rows() + " (cols-rank=" + actualNullity + ") maxResidual="
			+ maxResidual + " independent=" + independent);
	}

	public static void main(String[] args) {
		Complex.setFixedOFF();

		// N=A+2I de TestJordan02 -- multiplicidad geometrica 2, el caso real que motiva esta fase.
		checkNullspace("TestJordan02 N=A+2I (nulidad 2, caso real)",
			new MatrixComplex("0,0,3,4,5;0,0,0,6,7;0,0,0,0,8;0,0,0,0,0;0,0,0,0,0"), 2, 1e-9);

		checkNullspace("zero 4x4 (nulidad completa 4)", new MatrixComplex(4, 4), 4, 1e-9);
		checkNullspace("full rank 3x3 (nulidad 0)", new MatrixComplex("1,2,3;0,1,4;5,6,0"), 0, 1e-9);
		checkNullspace("rank-1 3x3 (nulidad 2)", new MatrixComplex("1,2,3;2,4,6;3,6,9"), 2, 1e-9);
		checkNullspace("todas las filas proporcionales, 5x5 (nulidad 4)",
			new MatrixComplex("1,2,3,4,5;2,4,6,8,10;-1,-2,-3,-4,-5;3,6,9,12,15;0,0,0,0,0"), 4, 1e-9);
		checkNullspace("complejo, nulidad 2, 4x4",
			new MatrixComplex("1+1i,2,0,1-1i;0,0,0,0;2+2i,4,0,2-2i;1,1,1,1"), 2, 1e-9);

		// Bateria aleatoria: sin autovalor restado, tipicamente rango completo (nulidad 0) --
		// verifica que la primitiva no rompe/produce falsos positivos en el caso comun.
		for (int size = 4; size <= 7; ++size) {
			MatrixComplex a = new MatrixComplex(size, size);
			a.initMatrixRandomInt(9);
			checkNullspace(size + "x" + size + " aleatoria (nulidad no asumida de antemano)", a, -1, 1e-9);
		}

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}

}
