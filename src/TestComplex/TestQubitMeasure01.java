package TestComplex;

import java.util.Random;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Qubits;

/**
 * Circuito de 1 cubit: |0> --H--> (|0>+|1>)/sqrt(2), simulando la medicion en la base
 * computacional 1000 veces con {@link Qubits#measure(MatrixComplex, int, Random)}.
 */
public class TestQubitMeasure01 {

	public static void main(String[] args) {
		// 1. Preparar el circuito: 1 cubit en |0>, puerta Hadamard para ponerlo en superposicion.
		MatrixComplex ket0 = Qubits.ket0();
		MatrixComplex state = Qubits.hadamard().times(ket0);
		state.println("Estado tras Hadamard: (|0>+|1>)/sqrt(2)");

		// 2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.
		double p0 = Math.pow(state.getItem(0, 0).abs(), 2);
		double p1 = Math.pow(state.getItem(1, 0).abs(), 2);
		System.out.println("P(0) = " + p0 + " , P(1) = " + p1);

		// 3. Simular 1000 medidas.
		int shots = 1000;
		int[] counts = Qubits.measure(state, shots, new Random());

		System.out.println("Resultados sobre " + shots + " medidas:");
		System.out.println("  |0> -> " + counts[0] + " (" + (100.0 * counts[0] / shots) + "%)");
		System.out.println("  |1> -> " + counts[1] + " (" + (100.0 * counts[1] / shots) + "%)");
	}
}
