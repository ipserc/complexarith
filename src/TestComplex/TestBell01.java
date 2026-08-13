package TestComplex;

import java.util.Random;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.BellTest;
import com.ipserc.arith.quantum.Qubits;

/**
 * Primer ejercicio del "Rol Física/Mecánica Cuántica" (Trigesimoquinta sesión, ver
 * Claude/ComplexArithRev.md): entrelazamiento cuántico + test de Bell (desigualdad CHSH), sobre el
 * estado de Bell {@code |Phi+>} de {@link Qubits#bellPhiPlus()}. Verifica el cálculo exacto contra
 * el límite de Tsirelson (2*sqrt(2)) conocido, y que la simulación Monte Carlo converge a ese
 * mismo valor exacto según crecen los "trials".
 */
public class TestBell01 {
	static int ok = 0, total = 0;

	static void check(String name, boolean cond) {
		total++;
		if (cond) { ok++; System.out.println("OK   - " + name); }
		else System.out.println("FAIL - " + name);
	}

	public static void main(String[] args) {
		MatrixComplex state = Qubits.bellPhiPlus();
		check("bellPhiPlus() esta normalizado (norma euclidea = 1)", Math.abs(state.norm() - 1.0) < 1e-12);

		// spinOperator(theta)^2 == I para varios angulos (sanity check analitico A(theta)^2=I)
		MatrixComplex identity = Qubits.identity2();
		for (double theta : new double[] { 0, Math.PI / 8, Math.PI / 4, Math.PI / 3, 1.234 }) {
			MatrixComplex a = Qubits.spinOperator(theta);
			MatrixComplex aSquared = a.times(a);
			check("spinOperator(" + theta + ")^2 == I", aSquared.minus(identity).norm() < 1e-9);
		}

		// CHSH exacto con los angulos que maximizan la violacion PARA ESTA parametrizacion
		// (Qubits.spinOperator(theta) ya lleva el angulo completo, no la mitad -- la correlacion
		// resultante es E(a,b)=cos(a-b), maximizada con angulos espaciados pi/4, NO pi/8 como en
		// el convenio de "angulo de polarizador" de los libros de texto, donde la correlacion es
		// cos(2*(a-b)) en su lugar -- ver Javadoc de BellTest.chsh).
		double a = 0;
		double aPrime = Math.PI / 2;
		double b = Math.PI / 4;
		double bPrime = 3 * Math.PI / 4;
		double sExact = BellTest.chsh(state, a, aPrime, b, bPrime);
		double tsirelson = 2 * Math.sqrt(2);
		System.out.println("CHSH exacto S = " + sExact + " (Tsirelson = " + tsirelson + ")");
		check("CHSH exacto cerca del limite de Tsirelson (2*sqrt(2))", Math.abs(sExact - tsirelson) < 1e-9);
		check("CHSH exacto viola la cota clasica de la teoria local-realista (S > 2)", sExact > 2.0);

		// Monte Carlo: converge al valor exacto segun crecen los trials
		Random rng = new Random(42);
		int[] trialCounts = { 2_000, 200_000, 2_000_000 };
		for (int trials : trialCounts) {
			double sSim = BellTest.simulateChsh(state, a, aPrime, b, bPrime, trials, rng);
			double error = Math.abs(sSim - sExact);
			System.out.println("Monte Carlo (trials=" + trials + "): S=" + sSim + ", error=" + error);
			check("Monte Carlo (trials=" + trials + ") tambien viola la cota clasica (S > 2)", sSim > 2.0);
		}

		// El error decrece con mas trials (ley de los grandes numeros) -- comparacion laxa entre
		// el primer y el ultimo punto, no exige monotonia estricta paso a paso (hay ruido).
		double sSimFirst = BellTest.simulateChsh(state, a, aPrime, b, bPrime, trialCounts[0], new Random(1));
		double sSimLast = BellTest.simulateChsh(state, a, aPrime, b, bPrime, trialCounts[trialCounts.length - 1], new Random(2));
		check("Monte Carlo con mas trials se acerca mas al valor exacto",
				Math.abs(sSimLast - sExact) < Math.abs(sSimFirst - sExact));

		System.out.println();
		System.out.println(ok + "/" + total + " OK");
	}
}
