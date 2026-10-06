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
public class TestQuantum_QubitMeasure01 {

	public static void probabilidades_resultados_ket(MatrixComplex state) {
		// 2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.
		Complex.printLineText(1, 90, "2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.", false, false);
		for (int row = 0; row < state.rows(); ++row)
			for (int col = 0; col < state.cols(); ++col)
				System.out.printf("P(%s) = %.2f%%\n", Qubits.basisLabel(state, row), Math.pow(state.getItem(row, col).abs(), 2)*100.0);
	}

	public static void realizar_medidas_ket(int shots, MatrixComplex state) {
		// 3. Simular las medidas.
		Complex.printLineText(1, 90, "3. Realizando de forma simulada las medidas.", true, false);
		int[] counts = Qubits.measure(state, shots, new Random());

		System.out.println("Resultados sobre " + shots + " medidas:");
		for (int estado = 0; estado < state.dim(); ++estado)
			System.out.printf("  %s -> %d (%.2f%%)\n", Qubits.basisLabel(state, estado), counts[estado], 100.0 * counts[estado] / shots);
		
		System.out.println("\n");
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

	public static void start_ket_circuit(MatrixComplex theKet, int shots) {
		int nQubits = Integer.numberOfTrailingZeros(theKet.rows());
		String theKetText = Qubits.ketLabel(theKet);
		Complex.printBoxText(3, 90, "Circuito: " + theKetText + " --H(x)"+nQubits+"--> superposicion, simulando la medicion en la base computacional multiples veces ("+shots+")");

		// 1. Preparar el circuito: n qubits en theKet, puerta Hadamard sobre cada uno para ponerlo en superposicion.
		Complex.printLineText(1, 90, "1. Preparar el circuito: " + nQubits + " qubit(s) en " + theKetText
				+ ", puerta Hadamard sobre cada qubit para ponerlo en superposicion.", false, false);
		theKet.println("Este es el qbit:");
		MatrixComplex state = hadamardTransform(nQubits).times(theKet);
		state.println("Estado tras Hadamard:");

		// 2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.
		probabilidades_resultados_ket(state);

		// 3. Simular las medidas.
		realizar_medidas_ket(shots, state);
	}

	/**
	 * Circuito con el estado GHZ (Greenberger-Horne-Zeilinger) de {@code n} qubits, {@link
	 * Qubits#ghz(int)} -- a diferencia de {@link #start_ket_circuit(String, MatrixComplex, String,
	 * int)}, NO parte de un ket de la base ni aplica Hadamard: {@code ghz(n)} ya es el estado
	 * entrelazado en si mismo, {@code (|00...0> + |11...1>)/sqrt(2)}. Maximamente entrelazado, no
	 * separable en un producto de estados por qubit -- a diferencia del caso {@code |110>} de
	 * {@code main()} (superposicion uniforme de los 2^n estados, pero SIN correlacion entre
	 * qubits), medir GHZ SOLO puede dar {@code |00...0>} o {@code |11...1>}, nunca ninguna
	 * combinacion intermedia.
	 * @param n Numero de qubits del estado GHZ, debe ser al menos 2.
	 * @param shots Numero de medidas simuladas.
	 */
	public static void start_ghz_circuit(MatrixComplex theKet, int shots) {
		int nQubits = Integer.numberOfTrailingZeros(theKet.rows());
		
		// 1. Preparar el circuito: estado GHZ de n qubits, ya entrelazado (sin Hadamard).
		MatrixComplex state = Qubits.ghz(nQubits);

		String stateText = "(" + Qubits.basisLabel(state, 0) + " + " + Qubits.basisLabel(state, state.dim() - 1) + ")/sqrt(2)";
		Complex.printBoxText(3, 90, "Circuito: estado GHZ de " + nQubits + " qubits " + stateText
				+ ", simulando la medicion en la base computacional multiples veces (" + shots + ")");

		Complex.printLineText(1, 90, "1. Preparar el circuito: estado GHZ de " + nQubits + " qubits, ya entrelazado (sin Hadamard).", false, false);
		state.println("Estado GHZ:");

		// 2. Probabilidades de cada resultado, |amplitud|^2 (regla de Born), solo a modo informativo.
		probabilidades_resultados_ket(state);

		// 3. Simular las medidas.
		realizar_medidas_ket(shots, state);
	}

	/**
	 * Verifica, disparo a disparo (no en agregado como el histograma de {@link
	 * #realizar_medidas_ket(int, MatrixComplex)}), que el estado GHZ esta perfectamente
	 * correlacionado: en CADA medida individual los {@code n} qubits salen siempre con el mismo
	 * bit (todos 0 o todos 1), nunca una combinacion mixta -- la prueba de que "cambiar" el
	 * resultado de un qubit fuerza el mismo cambio en los demas, sin haberlos tocado.
	 * @param theKet Un ket cualquiera de {@code n} qubits, solo para derivar {@code n} (igual que
	 * {@link #start_ghz_circuit(MatrixComplex, int)}, su valor concreto no se usa).
	 * @param shots Numero de medidas simuladas, una a una.
	 */
	public static void verificar_correlacion_ghz(MatrixComplex theKet, int shots) {
		int nQubits = Integer.numberOfTrailingZeros(theKet.rows());
		MatrixComplex state = Qubits.ghz(nQubits);
		Random random = new Random();

		Complex.printBoxText(3, 90, "5. Correlacion GHZ: verificando disparo a disparo que los " + nQubits
				+ " qubits salen siempre con el mismo bit (" + shots + " medidas individuales)");

		int ceros = 0, unos = 0, mixtos = 0;
		for (int shot = 0; shot < shots; ++shot) {
			int resultado = Qubits.measure(state, random);
			String bits = Qubits.basisLabel(resultado, nQubits).replace("|", "").replace(">", "");

			boolean todosIguales = true;
			for (int i = 1; i < bits.length(); ++i) {
				if (bits.charAt(i) != bits.charAt(0)) {
					todosIguales = false;
					break;
				}
			}

			if (!todosIguales) mixtos++;
			else if (bits.charAt(0) == '0') ceros++;
			else unos++;
		}

		System.out.printf("  Todos-0 (%s): %d (%.2f%%)\n", Qubits.basisLabel(0, nQubits), ceros, 100.0 * ceros / shots);
		System.out.printf("  Todos-1 (%s): %d (%.2f%%)\n", Qubits.basisLabel((1 << nQubits) - 1, nQubits), unos, 100.0 * unos / shots);
		System.out.printf("  Mixtos, nunca deberian aparecer: %d\n", mixtos);
		if (mixtos > 0) {
			throw new IllegalStateException("verificar_correlacion_ghz() encontro " + mixtos + " disparos mixtos -- la correlacion GHZ se ha roto");
		}

		System.out.println("\n");
	}

	/*
	 * main() recorre 3 casos, todos con shots=3477, cada uno llamando a start_ket_circuit:
	 * 1. theKet = Qubits.ket0() → |0>, 1 cúbit. Se le aplica H⊗1 (Hadamard normal) y queda en (|0>+|1>)/sqrt(2). Con 3477 disparos, el histograma sale ~50/50 entre |0> y |1>.
	 * 2. theKet = Qubits.ket1() → |1>, 1 cúbit. Mismo Hadamard, pero da (|0>-|1>)/sqrt(2) (signo relativo distinto, pero la medición en base computacional también sale ~50/50 — la fase relativa no se ve al medir en esta base).
	 * 3. theKet = Qubits.ket(1,1,0) → |110>, 3 cúbits. Se le aplica H⊗H⊗H sobre los 3 cúbits, dando una superposición uniforme de los 8 estados de la base (cada uno con distinto signo, pero |amplitud|² igual para todos = 1/8). El histograma
     de 3477 disparos sale repartido ~12,5% entre |000>...|111>.
	 * 4. start_ghz_circuit(3, shots) → GHZ de 3 qubits, (|000>+|111>)/sqrt(2), ya entrelazado (sin Hadamard). El histograma sale SOLO entre |000> y |111> (~50/50), nunca ninguna combinacion intermedia -- a diferencia del caso 3, que reparte entre los 8 estados.
	 * 5. verificar_correlacion_ghz(3, shots) → mismo estado GHZ, pero en vez del histograma agregado del caso 4, mide disparo a disparo y comprueba que los 3 bits del resultado son siempre iguales (nunca una mezcla tipo |010>) -- la prueba directa de que "leer" un qubit ya determina los otros dos sin haberlos tocado.
     *
     * En los casos 1-3, probabilidades_resultados_ket imprime las probabilidades exactas (regla de Born) y realizar_medidas_ket simula el muestreo real con Qubits.measure; start_ghz_circuit() reutiliza esas mismas 2 funciones.
	 */

	public static void main(String[] args) {
		MatrixComplex theKet;
		int shots = 3477;

		theKet = Qubits.ket0();
		start_ket_circuit(theKet, shots);

		theKet = Qubits.ket1();
		start_ket_circuit(theKet, shots);

		theKet = Qubits.ket(1, 1, 0);
		start_ket_circuit(theKet, shots);
		start_ghz_circuit(theKet, shots);
		verificar_correlacion_ghz(theKet, shots);
	}
}
