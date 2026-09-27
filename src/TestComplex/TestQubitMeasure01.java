package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Qubits;

/**
 * Circuito de n qubits: |b1...bn> --H(x)n--> superposicion uniforme de los 2^n estados de la base
 * computacional, simulando la medicion en la base computacional multiples veces (shots) con
 * {@link Qubits#measure(MatrixComplex, int, Random)}.
 */
public class TestQubitMeasure01 {

	public static void probabilidades_resultados_ket(MatrixComplex state) {
		// 2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.
		Complex.printLineText(1, 90, "2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.", false, false);
		for (int row = 0; row < state.rows(); ++row)
			for (int col = 0; col < state.cols(); ++col)
				System.out.printf("P(%s) = %.2f\n", Qubits.basisLabel(state, row), Math.pow(state.getItem(row, col).abs(), 2));
	}

	public static void realizar_medidas_ket(int shots, MatrixComplex state) {
		// 3. Simular las medidas.
		Complex.printLineText(1, 90, "3. Realizando de forma simulada las medidas.", true, false);
		int[] counts = Qubits.measure(state, shots, new Random());

		System.out.println("Resultados sobre " + shots + " medidas:");
		for (int estado = 0; estado < state.dim(); ++estado)
			System.out.printf("  %s -> %d (%.2f%%)\n", Qubits.basisLabel(state, estado), counts[estado], 100.0 * counts[estado] / shots);
	}

	/**
	 * La puerta Hadamard sobre CADA uno de los {@code n} qubits del registro, {@code H(x)H(x)...(x)H}
	 * ({@code n} veces) -- misma construccion que ya usan {@code DeutschJozsa}/{@code QFT}/{@code QPE}
	 * para poner un registro entero en superposicion uniforme, no extraida a la factoria porque ya
	 * vive duplicada tal cual en esos 3 sitios (este test es el 4).
	 */
	private static MatrixComplex hadamardTransform(int n) {
		MatrixComplex result = Qubits.hadamard();
		for (int i = 1; i < n; ++i) {
			result = result.kroneckerprod(Qubits.hadamard());
		}
		return result;
	}

	public static void start_ket_circuit(String title, MatrixComplex theKet, String theKetText, int shots) {
		Complex.printBoxText(3, 90, title);
		int nQubits = Integer.numberOfTrailingZeros(theKet.rows());

		// 1. Preparar el circuito: n qubits en theKet, puerta Hadamard sobre cada uno para ponerlo en superposicion.
		Complex.printLineText(1, 90, "1. Preparar el circuito: " + nQubits + " qubit(s) en " + theKetText
				+ ", puerta Hadamard sobre cada qubit para ponerlo en superposicion.", true, false);
		theKet.println("Este es el qbit:");
		MatrixComplex state = hadamardTransform(nQubits).times(theKet);
		state.println("Estado tras Hadamard:");

		// 2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.
		probabilidades_resultados_ket(state);

		// 3. Simular las medidas.
		realizar_medidas_ket(shots, state);
	}

	public static void main(String[] args) {
		MatrixComplex theKet;
		String theKetText;
		int shots = 3477;
		int nQubits; 

		theKet = Qubits.ket0();
		theKetText = Qubits.ketLabel(theKet);
		nQubits = Integer.numberOfTrailingZeros(theKet.rows());
		start_ket_circuit("Circuito: " + theKetText + " --H(x)"+nQubits+"--> superposicion, simulando la medicion en la base computacional multiples veces ("+shots+")", theKet, theKetText, shots);

		theKet = Qubits.ket1();
		theKetText = Qubits.ketLabel(theKet);
		nQubits = Integer.numberOfTrailingZeros(theKet.rows());
		start_ket_circuit("Circuito: " + theKetText + " --H(x)"+nQubits+"--> superposicion, simulando la medicion en la base computacional multiples veces ("+shots+")", theKet, theKetText, shots);

		theKet = Qubits.ket(1, 1, 0);
		theKetText = Qubits.ketLabel(theKet);
		nQubits = Integer.numberOfTrailingZeros(theKet.rows());
		start_ket_circuit("Circuito: " + theKetText + " --H(x)"+nQubits+"--> superposicion, simulando la medicion en la base computacional multiples veces ("+shots+")", theKet, theKetText, shots);
	}
}
