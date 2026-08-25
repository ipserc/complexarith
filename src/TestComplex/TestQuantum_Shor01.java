package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.QPE;
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.quantum.Shor;

/**
 * Verifica {@link Shor}, la culminación de la línea QFT&rarr;{@link QPE}, contra el ejemplo
 * estándar de manual (Nielsen &amp; Chuang): {@code N=15}, {@code a=7}, orden {@code r=4} --
 * elegido, igual que los casos de fase exacta de {@code TestQuantum_QPE01}, porque {@code r=4}
 * divide exactamente {@code 2^t} con {@code t=3} counting qubits, así que la distribución de
 * probabilidades del registro de conteo es EXACTA (no una muestra aleatoria), manteniendo el test
 * completamente determinista.
 */
public class TestQuantum_Shor01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;
		Complex.printBoxText(boxShape, boxMargin, "Shor -- N=15, a=7 (Nielsen & Chuang)");

		int N = 15, a = 7, t = 3, m = 4;

		MatrixComplex u = Shor.modularMultiplicationUnitary(a, N, m);
		System.out.printf("dim(U) = %d%n", u.rows());
		MatrixComplex product = u.times(u.adjoint());
		double maxUnitarityError = 0;
		for (int i = 0; i < u.rows(); ++i) {
			for (int j = 0; j < u.rows(); ++j) {
				double expected = (i == j) ? 1 : 0;
				maxUnitarityError = Math.max(maxUnitarityError, Math.abs(product.getItem(i, j).mod() - expected));
			}
		}
		System.out.printf("max |U*U^dagger - I| = %.3e%n", maxUnitarityError);
		check("modularMultiplicationUnitary(7,15,4) es unitaria", maxUnitarityError < 1e-9);

		// U debe ser una biyeccion sobre 0..N-1: 7*y mod 15 para y=0..14 no debe repetir imagen.
		boolean[] seen = new boolean[N];
		boolean bijective = true;
		for (int y = 0; y < N; ++y) {
			int image = (7*y) % 15;
			if (seen[image]) { bijective = false; }
			seen[image] = true;
		}
		check("U_a es biyectiva sobre 0..N-1 (multiplicacion modular por a coprimo con N)", bijective);

		int[] oneBits = new int[m];
		oneBits[m-1] = 1;
		MatrixComplex one = Qubits.ket(oneBits);
		double[] probabilities = QPE.countingProbabilities(t, u, one);
		System.out.print("probabilidades registro de conteo (t=3):");
		for (double p : probabilities) { System.out.printf(" %.4f", p); }
		System.out.println();

		double sumProb = 0;
		for (double p : probabilities) { sumProb += p; }
		check("las probabilidades del registro de conteo suman 1", Math.abs(sumProb-1.0) < 1e-9);

		// r=4 divide exactamente 2^t=8 -- solo k=0,2,4,6 (multiplos de 2^t/r=2) tienen probabilidad
		// no nula, cada uno exactamente 1/4 (superposicion equiprobable de los r=4 autoestados).
		boolean exactPeaks = true;
		for (int k = 0; k < 8; ++k) {
			double expected = (k % 2 == 0) ? 0.25 : 0.0;
			if (Math.abs(probabilities[k]-expected) > 1e-9) { exactPeaks = false; }
		}
		check("distribucion EXACTA: 1/4 en k=0,2,4,6, 0 en los impares", exactPeaks);

		int order = Shor.findOrder(a, N, t);
		System.out.printf("findOrder(a=%d,N=%d,t=%d) = %d%n", a, N, t, order);
		check("findOrder recupera el orden exacto r=4", order == 4);

		// a=2 tambien tiene orden 4 modulo 15 (2,4,8,16mod15=1) -- segundo caso independiente.
		int order2 = Shor.findOrder(2, N, t);
		check("findOrder(2,15,3) tambien recupera r=4 (segundo caso independiente)", order2 == 4);

		int[] factors = Shor.factor(N, a, t);
		System.out.printf("factor(15,7,3) = %s%n", factors == null ? "null" : (factors[0] + "," + factors[1]));
		boolean correctFactors = factors != null && factors[0]*factors[1] == N
				&& ((factors[0] == 3 && factors[1] == 5) || (factors[0] == 5 && factors[1] == 3));
		check("factor(15,7,3) da exactamente {3,5}", correctFactors);

		int[] factorsAuto = Shor.factor(N, t);
		boolean autoValid = factorsAuto != null && factorsAuto[0] > 1 && factorsAuto[1] > 1 && factorsAuto[0]*factorsAuto[1] == N;
		System.out.printf("factor(15,3) [busqueda automatica de a] = %s%n", factorsAuto == null ? "null" : (factorsAuto[0] + "," + factorsAuto[1]));
		check("factor(15,3) (busqueda automatica de a) da un par de factores validos de 15", autoValid);

		boolean rejects;
		rejects = true; try { Shor.modularMultiplicationUnitary(3, 15, 4); rejects = false; } catch (IllegalArgumentException e) { }
		check("modularMultiplicationUnitary rechaza a no coprimo con N (gcd(3,15)=3)", rejects);
		rejects = true; try { Shor.modularMultiplicationUnitary(7, 15, 3); rejects = false; } catch (IllegalArgumentException e) { }
		check("modularMultiplicationUnitary rechaza m insuficiente (2^3=8<15)", rejects);
		rejects = true; try { Shor.findOrder(3, 15, 3); rejects = false; } catch (IllegalArgumentException e) { }
		check("findOrder rechaza a no coprimo con N", rejects);
		rejects = true; try { Shor.factor(3, 3); rejects = false; } catch (IllegalArgumentException e) { }
		check("factor(N,t) rechaza N<4", rejects);

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}
}
