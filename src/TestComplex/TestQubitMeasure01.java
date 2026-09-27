package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Qubits;

/**
 * Circuito de 1 cubit: |0> --H--> (|0>+|1>)/sqrt(2), simulando la medicion en la base
 * computacional múltiples veces (shots) con {@link Qubits#measure(MatrixComplex, int, Random)}.
 */
public class TestQubitMeasure01 {
	
	public static void probabilidades_resultados_ket(MatrixComplex state) {
		// 2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.
		Complex.printLineText(1, 90, "2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.", false, false);
		for (int row = 0; row < state.rows(); ++row)
			for (int col = 0; col < state.cols(); ++col)
				System.out.printf("P(%d) = %.2f\n", row, Math.pow(state.getItem(row, col).abs(), 2));
	}
	
	public static void realizar_medidas_ket(int shots, MatrixComplex state) {
		// 3. Simular las medidas.
		Complex.printLineText(1, 90, "3. Realizando de forma simulada las medidas.", true, false);
		int[] counts = Qubits.measure(state, shots, new Random());

		System.out.println("Resultados sobre " + shots + " medidas:");
		for (int estado = 0; estado < state.dim(); ++estado)
			System.out.printf("  |%d> -> %d (%.2f%%)\n",estado, counts[estado], 100.0 * counts[estado] / shots);
	}
	
	public static void start_ket_circuit(String title, MatrixComplex theKet, String theKetText, int shots) {
		Complex.printBoxText(3, 90, title);
		// 1. Preparar el circuito: 1 cubit en |0>, puerta Hadamard para ponerlo en superposicion.
		Complex.printLineText(1, 90, "1. Preparar el circuito: 1 cubit en " + theKetText + ", puerta Hadamard para ponerlo en superposicion.", true, false);
		theKet.println("Este es el qbit:");
		MatrixComplex state = Qubits.hadamard().times(theKet);
		state.println("Estado tras Hadamard: (|0>+|1>)/sqrt(2)");

		// 2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.
		probabilidades_resultados_ket(state);
		
		// 3. Simular las medidas.
		realizar_medidas_ket(shots, state);
	
	}
	
	public static void main(String[] args) {
		MatrixComplex theKet;
		String theKetText;
		
		theKet = Qubits.ket0();
		theKetText = Qubits.ketLabel(theKet);
		start_ket_circuit("Circuito de 1 qubit: "+theKetText+" --H--> (|0>+|1>)/sqrt(2), simulando la medicion en la base computacional múltiples veces (shots)", theKet, theKetText, 357);

		theKet = Qubits.ket1();
		theKetText = Qubits.ketLabel(theKet);
		start_ket_circuit("Circuito de 1 qubit: "+theKetText+" --H--> (|0>+|1>)/sqrt(2), simulando la medicion en la base computacional múltiples veces (shots)", theKet, theKetText, 357);
		
	}
}
