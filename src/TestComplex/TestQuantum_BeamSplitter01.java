package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Qubits;

/**
 * Interferometro de Mach-Zehnder con 1 foton, modelado con las primitivas YA existentes de {@code
 * com.ipserc.arith.quantum} -- ninguna clase/metodo nuevo en {@code Qubits.java}, porque no hace
 * falta ninguno. El grado de libertad modelado aqui NO es la polarizacion (como en {@code
 * TestPhotonEntanglement01}), es el CAMINO del foton: {@code |0>}=brazo transmitido, {@code
 * |1>}=brazo reflejado, 1 solo qubit.
 * <p>
 * Un semiespejo 50/50 simetrico es, matematicamente, el mismo tipo de objeto que {@link
 * Qubits#hadamard()} -- una matriz unitaria 2x2 real que reparte {@code |0>}/{@code |1>} en partes
 * iguales -- asi que se reusa directamente, sin una fabrica "beamSplitter()" separada. El desfase
 * de uno de los 2 brazos del interferometro (un espejo movido unas pocas longitudes de onda, o una
 * lamina de fase) es, igual de directo, {@link Qubits#phaseGate(int)} (el mismo gate que ya usa la
 * QFT), aplicado entre los 2 semiespejos.
 * <p>
 * El circuito completo -- semiespejo, desfase, semiespejo -- es EXACTAMENTE {@code H}-fase-{@code
 * H} sobre 1 qubit, el mismo patron que ya aparece (sobre un registro de varios qubits) dentro de
 * {@code QPE}/{@code QFT}. Lo interesante fisicamente: con 2 "monedas" 50/50 independientes, la
 * probabilidad final de cada puerto de salida seria 50/50 SIEMPRE, cualquiera que sea el desfase
 * intermedio (es solo una permutacion de una variable aleatoria uniforme). Con amplitudes
 * complejas en vez de probabilidades clasicas, el resultado es justo lo contrario: el puerto de
 * salida queda DETERMINADO por el desfase relativo entre los 2 brazos (interferencia), pese a que
 * cada semiespejo por separado sigue siendo 50/50 -- la firma de la naturaleza ondulatoria/cuantica
 * de un solo foton, no una mezcla estadistica clasica.
 */
public class TestQuantum_BeamSplitter01 {

	/**
	 * Formula cerrada de las probabilidades de salida del Mach-Zehnder para un desfase {@code phi}
	 * entre los 2 brazos: {@code P(transmitido)=cos^2(phi/2)}, {@code P(reflejado)=sin^2(phi/2)}
	 * (deducida a mano aplicando {@code H}-fase({@code phi})-{@code H} sobre {@code |0>}, y
	 * verificada abajo contra el calculo real con {@code MatrixComplex}).
	 */
	private static void probabilidadesEsperadas(double phi) {
		double pTransmitido = Math.pow(Math.cos(phi / 2.0), 2);
		double pReflejado = Math.pow(Math.sin(phi / 2.0), 2);
		System.out.printf("  Formula cos^2(phi/2)/sin^2(phi/2): P(transmitido)=%.4f%%, P(reflejado)=%.4f%%\n",
				pTransmitido * 100.0, pReflejado * 100.0);
	}

	public static void main(String[] args) {
		int shots = 3477;
		Random random = new Random();

		Complex.printBoxText(3, 90, "Interferometro de Mach-Zehnder de 1 foton: semiespejo--desfase--semiespejo");

		// 1. Preparar el circuito: foton entra por el puerto |0> (camino transmitido).
		Complex.printLineText(1, 90, "1. Preparar el circuito: foton entra en el puerto |0> (camino transmitido).", false, false);
		MatrixComplex foton = Qubits.ket0();
		foton.println("Estado inicial del foton:");

		// 2. Primer semiespejo: Hadamard reparte el foton 50/50 entre los 2 brazos.
		Complex.printLineText(1, 90, "2. Primer semiespejo (Hadamard): reparte el foton 50/50 entre los 2 brazos.", true, false);
		MatrixComplex trasPrimerSemiespejo = Qubits.hadamard().times(foton);
		trasPrimerSemiespejo.println("Estado tras el primer semiespejo:");

		// 3. Desfase relativo entre los 2 brazos (phaseGate(k), angulo=2*pi/2^k) y segundo
		// semiespejo, para varios valores de k -- demuestra que el puerto de salida depende del
		// desfase, no es un 50/50 fijo como seria con monedas clasicas independientes.
		Complex.printLineText(1, 90, "3. Desfase phaseGate(k) en un brazo + segundo semiespejo, para varios k.", true, false);
		for (int k = 0; k <= 4; ++k) {
			double phi = 2.0 * Math.PI / Math.pow(2, k);
			MatrixComplex trasDesfase = Qubits.phaseGate(k).times(trasPrimerSemiespejo);
			MatrixComplex salida = Qubits.hadamard().times(trasDesfase);

			double pTransmitidoReal = Math.pow(salida.getItem(0, 0).abs(), 2) * 100.0;
			double pReflejadoReal = Math.pow(salida.getItem(1, 0).abs(), 2) * 100.0;
			System.out.printf("k=%d, phi=%.4f rad: P(transmitido)=%.4f%%, P(reflejado)=%.4f%%\n", k, phi, pTransmitidoReal, pReflejadoReal);
			probabilidadesEsperadas(phi);
		}

		// 4. Simular las medidas en los 2 casos extremos: k=0 (phi=2*pi==0, interferencia
		// constructiva total en el puerto transmitido pese a los 2 semiespejos 50/50) y k=1
		// (phi=pi, interferencia destructiva total en el puerto transmitido -- el foton sale
		// SIEMPRE por el puerto reflejado).
		Complex.printLineText(1, 90, "4. Simulacion de medidas (" + shots + " disparos) en los 2 casos extremos: k=0 y k=1.", true, false);
		for (int k : new int[] { 0, 1 }) {
			MatrixComplex salida = Qubits.hadamard().times(Qubits.phaseGate(k).times(trasPrimerSemiespejo));
			int[] counts = Qubits.measure(salida, shots, random);
			System.out.printf("  k=%d -> transmitido: %d (%.2f%%), reflejado: %d (%.2f%%)\n",
					k, counts[0], 100.0 * counts[0] / shots, counts[1], 100.0 * counts[1] / shots);
		}

		System.out.println("\n");
	}
}
