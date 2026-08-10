package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Auditoria matematica dedicada de Eigenspace.java (Vigesimosexta sesion, bloque 3 de la hoja de
 * ruta "Matematicas Aplicadas"). Verifica con matrices de autovalor conocido -- no solo lectura.
 */
public class ScratchEigenspaceAudit01 {

	static int pass = 0, fail = 0;

	static void checkTrue(String label, boolean cond) {
		if (cond) ++pass; else ++fail;
		System.out.printf("%-70s %s%n", label, cond ? "OK" : "***FAIL***");
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(6);

		System.out.println("=== geometricMultiplicityFromEVectors: matriz diag(2,2,3), autovalor 2 con multiplicidad 2 ===");
		// this.rows()=3 (dimension de la matriz), eigenvalues.rows()=2 (autovalores DISTINTOS: 2 y 3)
		// -- indices distintos, el bucle usa "i < this.rows()" para indexar AMBOS arrays.
		MatrixComplex diagM = new MatrixComplex(3);
		diagM.setItem(0, 0, new Complex(2, 0));
		diagM.setItem(1, 1, new Complex(2, 0));
		diagM.setItem(2, 2, new Complex(3, 0));
		Eigenspace eig = new Eigenspace(diagM);
		System.out.println("this.rows()=" + eig.rows() + "  eigenvalues().rows()=" + eig.eigenvalues().rows());
		try {
			int gm = eig.geometricMultiplicityFromEVectors(new Complex(2, 0));
			System.out.println("geometricMultiplicityFromEVectors(2) = " + gm + " (esperado: 2, autovalor 2 en una matriz diagonal es geometricamente 2)");
			checkTrue("geometricMultiplicityFromEVectors(2) == 2", gm == 2);
		} catch (Exception e) {
			System.out.println("geometricMultiplicityFromEVectors(2) -> EXCEPCION: " + e);
			++fail;
		}
		// Referencia independiente: el metodo YA arreglado y activo, geometricMultiplicity()
		int gmReference = eig.geometricMultiplicity(new Complex(2, 0));
		System.out.println("geometricMultiplicity(2) [referencia, ya activo/arreglado] = " + gmReference);

		System.out.println("\n=== arithmeticMultiplicity__ (metodo __ viejo): mismo patron de indexado ===");
		try {
			int am = eig.arithmeticMultiplicity__(new Complex(2, 0), 6);
			System.out.println("arithmeticMultiplicity__(2,6) = " + am + " (esperado: 2)");
			checkTrue("arithmeticMultiplicity__(2,6) == 2", am == 2);
		} catch (Exception e) {
			System.out.println("arithmeticMultiplicity__(2,6) -> EXCEPCION: " + e);
			++fail;
		}
		int amReference = eig.arithmeticMultiplicity(new Complex(2, 0));
		System.out.println("arithmeticMultiplicity(2) [referencia, activo] = " + amReference);
		checkTrue("arithmeticMultiplicity(2) == 2 (referencia)", amReference == 2);

		System.out.println("\n=== orderSwap(): eigenvalues() debe seguir coherente con roots()/solutions() tras el swap ===");
		MatrixComplex simpleM = new MatrixComplex(3);
		simpleM.setItem(0, 0, new Complex(1, 0));
		simpleM.setItem(1, 1, new Complex(5, 0));
		simpleM.setItem(2, 2, new Complex(9, 0));
		Eigenspace eig2 = new Eigenspace(simpleM);
		System.out.println("ANTES  orden=" + eig2.getOrder() + "  eigenvalues=" + printCol(eig2.eigenvalues()) + "  roots=" + printCol(eig2.roots()));
		eig2.orderSwap();
		System.out.println("DESPUES orden=" + eig2.getOrder() + "  eigenvalues=" + printCol(eig2.eigenvalues()) + "  roots=" + printCol(eig2.roots()));
		// Tras el swap, el primer elemento de "roots" y el primer elemento de "eigenvalues" deberian
		// seguir siendo consistentes con el orden declarado (mismo criterio: el primero de roots
		// deberia coincidir en valor con el primero de eigenvalues, ya que para esta matriz todos
		// los autovalores son simples/distintos).
		Complex firstRoot = eig2.roots().getItem(0, 0);
		Complex firstEigenvalue = eig2.eigenvalues().getItem(0, 0);
		checkTrue("tras orderSwap(), eigenvalues()[0] == roots()[0] (misma matriz, autovalores distintos)",
				firstRoot.minus(firstEigenvalue).mod() < 1e-6);

		System.out.println("\n===================================");
		System.out.println("TOTAL: " + pass + " OK, " + fail + " FAIL");
	}

	static String printCol(MatrixComplex m) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < m.rows(); ++i) {
			if (i > 0) sb.append(", ");
			sb.append(m.getItem(i, 0).toString());
		}
		return sb.append("]").toString();
	}
}
