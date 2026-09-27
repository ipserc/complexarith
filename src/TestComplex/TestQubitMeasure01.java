package TestComplex;

import java.util.Random;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Qubits;

/**
 * Circuito de 1 cubit: |0> --H--> (|0>+|1>)/sqrt(2), simulando la medicion en la base
 * computacional múltiples veces (shots) con {@link Qubits#measure(MatrixComplex, int, Random)}.
 */
public class TestQubitMeasure01 {
	
	public static void probabilidades_resultados(MatrixComplex state) {
		// Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.
		
		for (int row = 0; row < state.rows(); ++row)
			for (int col = 0; col < state.cols(); ++col)
				System.out.printf("P(%d) = %.2f\n", row, Math.pow(state.getItem(row, col).abs(), 2));
		// double p0 = Math.pow(state.getItem(0, 0).abs(), 2);
		// double p1 = Math.pow(state.getItem(1, 0).abs(), 2);
		// System.out.printf("P(0) = %.2f , P(1) = %.2f\n", p0, p1);
	}
	
	public static void realizar_medidas(int shots, MatrixComplex state) {
		int[] counts = Qubits.measure(state, shots, new Random());

		System.out.println("\nResultados sobre " + shots + " medidas:");
		for (int estado = 0; estado < state.dim(); ++estado)
			System.out.printf("  |%d> -> %d (%.2f%%)\n",estado, counts[estado], 100.0 * counts[estado] / shots);
	}

	public static void main(String[] args) {
		// 1. Preparar el circuito: 1 cubit en |0>, puerta Hadamard para ponerlo en superposicion.
		MatrixComplex ket0 = Qubits.ket0();
		MatrixComplex state = Qubits.hadamard().times(ket0);
		state.println("Estado tras Hadamard: (|0>+|1>)/sqrt(2)");

		// 2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.
		probabilidades_resultados(state);
		
		// 3. Simular las medidas.
		int shots = 523;
		realizar_medidas(shots, state);
	}
}
