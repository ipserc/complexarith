package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Fock;
import com.ipserc.arith.quantum.Qubits;

/**
 * Efecto Hong-Ou-Mandel (HOM, 1987): 2 fotones IDENTICOS entran a la vez, uno por cada puerto, en
 * el mismo semiespejo 50/50 -- y salen SIEMPRE juntos por el mismo puerto. Nunca se detecta una
 * coincidencia (un foton en cada salida), cuando con 2 particulas clasicas (o 2 fotones
 * distinguibles) las coincidencias son el 50% de los casos.
 * <p>
 * Continuacion directa de {@code TestQuantum_BeamSplitter01} (Mach-Zehnder de 1 foton): el
 * semiespejo sigue siendo {@link Qubits#hadamard()}, pero con 2 fotones ya no basta "un qubit por
 * foton" -- un producto tensorial de qubits describe particulas DISTINGUIBLES. Para fotones
 * identicos hace falta el espacio de Fock (numeros de ocupacion {@code |n_a,n_b>}), que aporta la
 * clase {@link Fock}: el semiespejo actua sobre 2 fotones con {@code Phi_2(H)}, construido con
 * permanentes de submatrices de {@code H}.
 * <p>
 * Por que salen juntos: hay 2 maneras de acabar con "un foton en cada salida" -- NINGUNO cruza
 * (a sale por a, b sale por b: {@code H[a<-a]*H[b<-b] = (1/sqrt2)*(-1/sqrt2) = -1/2}, el {@code -1}
 * de {@code H} esta en {@code b->b}) y LOS DOS cruzan (a sale por b, b sale por a: {@code
 * H[b<-a]*H[a<-b] = +1/2}). Con fotones distinguibles se suman sus PROBABILIDADES ({@code 1/4 +
 * 1/4 = 1/2}, el 50% clasico); con fotones identicos no hay forma, ni en principio, de saber cual
 * de las 2 maneras ocurrio, asi que se suman sus AMPLITUDES y se cancelan: {@code Per(H) = -1/2 +
 * 1/2 = 0}. Si los fotones se pueden distinguir parcialmente (por ejemplo, llegan con un retardo),
 * las 2 maneras dejan de interferir del todo y la cancelacion desaparece gradualmente: el "dip" de
 * HOM.
 */
public class TestQuantum_HongOuMandel01 {

	/** Indices de modo del punto 4: camino (a/b) x instante de llegada (temprano/tardio). */
	private static final int A_TEMPRANO = 0, A_TARDIO = 1, B_TEMPRANO = 2, B_TARDIO = 3;

	public static void main(String[] args) {
		int shots = 3477;
		Random random = new Random();

		Complex.printBoxText(3, 90, "Efecto Hong-Ou-Mandel: 2 fotones identicos en el mismo semiespejo 50/50");

		// 1. Estado de entrada |1,1>: un foton por el puerto a y otro por el puerto b.
		Complex.printLineText(1, 90, "1. Entrada |1,1> (un foton por cada puerto) y semiespejo Phi_2(H).", false, false);
		int[][] base = Fock.basis(2, 2);
		MatrixComplex entrada = Fock.ket(1, 1);
		imprimirEstado("Estado de entrada:", entrada, base);
		MatrixComplex semiespejo2 = Fock.multiPhotonUnitary(Qubits.hadamard(), 2);
		semiespejo2.println("Semiespejo sobre 2 fotones, Phi_2(H), en la base |2,0>,|1,1>,|0,2>:");
		MatrixComplex salida = semiespejo2.times(entrada);
		imprimirEstado("Estado de salida, (|2,0> - |0,2>)/sqrt(2):", salida, base);
		System.out.printf("  Amplitud de coincidencia <1,1|salida> = Per(H) = %s\n", salida.getItem(1, 0).toString());

		// 2. Medida con detectores que cuentan fotones en cada salida.
		Complex.printLineText(1, 90, "2. Simulacion de medidas (" + shots + " disparos) con fotones identicos.", true, false);
		int[] cuentas = Fock.measure(salida, shots, random);
		for (int k = 0; k < base.length; ++k) {
			System.out.printf("  %s : %4d (%6.2f%%)\n", Fock.basisLabel(base[k]), cuentas[k], 100.0 * cuentas[k] / shots);
		}
		if (cuentas[Fock.indexOf(1, 1)] != 0) {
			throw new IllegalStateException("HOM: se ha medido una coincidencia |1,1> con fotones identicos");
		}
		System.out.println("  -> 0 coincidencias: los 2 fotones salen SIEMPRE juntos (agrupamiento bosonico).");

		// 3. Comparacion: 2 fotones distinguibles, modelo de 1 qubit de camino por foton (producto
		// tensorial plano, sin simetrizar). Cada foton atraviesa su propio Hadamard por separado.
		Complex.printLineText(1, 90, "3. Comparacion: 2 fotones DISTINGUIBLES (un qubit de camino por foton).", true, false);
		MatrixComplex distinguibles = Qubits.hadamard().kroneckerprod(Qubits.hadamard()).times(Qubits.ket(0, 1));
		int[] cuentasD = Qubits.measure(distinguibles, shots, random);
		int coincidenciasD = cuentasD[1] + cuentasD[2]; // |01> y |10>: un foton en cada salida
		System.out.printf("  Coincidencias (un foton en cada salida): %4d (%6.2f%%), teorico 50%%\n",
				coincidenciasD, 100.0 * coincidenciasD / shots);
		System.out.printf("  Mismo puerto (|00> o |11>):              %4d (%6.2f%%), teorico 50%%\n",
				cuentasD[0] + cuentasD[3], 100.0 * (cuentasD[0] + cuentasD[3]) / shots);

		// 4. El "dip" de HOM: distinguibilidad parcial. Cada foton lleva ademas un modo temporal
		// (temprano/tardio): 4 modos en total. El foton 1 entra por a, temprano; el foton 2 entra
		// por b en cos(alfa)|temprano> + sin(alfa)|tardio> -- cos(alfa) es el solapamiento de los 2
		// paquetes de onda (alfa=0: identicos; alfa=pi/2: un retardo los hace distinguibles). El
		// semiespejo actua solo sobre el camino: U = H (x) I_2. La probabilidad de coincidencia NO se
		// introduce a mano: sale del calculo en Fock y se compara con la formula (1 - cos^2(alfa))/2.
		Complex.printLineText(1, 90, "4. Dip de HOM: coincidencias frente al solapamiento cos(alfa) de los fotones.", true, false);
		int[][] base4 = Fock.basis(2, 4);
		MatrixComplex semiespejoCamino = Fock.multiPhotonUnitary(Qubits.hadamard().kroneckerprod(Qubits.identity2()), 2);
		System.out.println("  alfa/(pi/2)  solapamiento  P(coinc) Fock  formula (1-cos^2)/2  medido (" + shots + " disparos)");
		for (int paso = 0; paso <= 8; ++paso) {
			double alfa = paso * Math.PI / 16.0;
			MatrixComplex entrada4 = Fock.ket(ocupacion(A_TEMPRANO, B_TEMPRANO)).times(Math.cos(alfa))
					.plus(Fock.ket(ocupacion(A_TEMPRANO, B_TARDIO)).times(Math.sin(alfa)));
			MatrixComplex salida4 = semiespejoCamino.times(entrada4);

			double pCoincidencia = 0.0;
			for (int k = 0; k < base4.length; ++k) {
				if (esCoincidencia(base4[k])) {
					pCoincidencia += Math.pow(salida4.getItem(k, 0).abs(), 2);
				}
			}
			double formula = (1.0 - Math.pow(Math.cos(alfa), 2)) / 2.0;
			if (Math.abs(pCoincidencia - formula) > 1e-12) {
				throw new IllegalStateException("Dip de HOM: P(coincidencia)=" + pCoincidencia + " frente a formula=" + formula);
			}
			int[] cuentas4 = Fock.measure(salida4, shots, random);
			int coincidencias4 = 0;
			for (int k = 0; k < base4.length; ++k) {
				if (esCoincidencia(base4[k])) coincidencias4 += cuentas4[k];
			}
			System.out.printf("     %5.3f        %6.4f         %6.4f            %6.4f            %4d (%6.2f%%)\n",
					paso / 8.0, Math.cos(alfa), pCoincidencia, formula, coincidencias4, 100.0 * coincidencias4 / shots);
		}
		System.out.println("  -> solapamiento 1 (fotones identicos): 0 coincidencias; solapamiento 0 (distinguibles): 50%.");
	}

	/** Vector de ocupacion de 4 modos con un foton en cada uno de los 2 modos dados. */
	private static int[] ocupacion(int modo1, int modo2) {
		int[] occ = new int[4];
		occ[modo1]++;
		occ[modo2]++;
		return occ;
	}

	/** Coincidencia: un foton en la salida a (cualquier instante) y otro en la salida b. */
	private static boolean esCoincidencia(int[] occ) {
		return occ[A_TEMPRANO] + occ[A_TARDIO] == 1 && occ[B_TEMPRANO] + occ[B_TARDIO] == 1;
	}

	private static void imprimirEstado(String titulo, MatrixComplex estado, int[][] base) {
		System.out.println(titulo);
		for (int k = 0; k < base.length; ++k) {
			System.out.printf("  %s : %s\n", Fock.basisLabel(base[k]), estado.getItem(k, 0).toString());
		}
	}
}
