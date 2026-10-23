package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Fock;
import com.ipserc.arith.quantum.Qubits;

/**
 * Hong-Ou-Mandel con FERMIONES (electrones): el mismo semiespejo 50/50 de {@code
 * TestQuantum_HongOuMandel01}, pero con 2 fermiones identicos -- y salen SIEMPRE separados, uno por
 * cada puerto (antiagrupamiento), justo lo contrario que los fotones.
 * <p>
 * Por que: las mismas 2 maneras de acabar en coincidencia que en el caso bosonico -- NINGUNO cruza
 * ({@code H[a<-a]*H[b<-b] = -1/2}) y LOS DOS cruzan ({@code H[b<-a]*H[a<-b] = +1/2}) -- pero "los
 * dos cruzan" es "ninguno cruza" con las 2 particulas intercambiadas, y para fermiones el
 * intercambio lleva un signo {@code -}: se combinan con el DETERMINANTE en vez del permanente,
 * {@code det(H) = -1/2 - 1/2 = -1}, {@code |det(H)|^2 = 1}. El principio de exclusion de Pauli es
 * el caso particular "los 2 en la misma salida": {@code |2,0>} y {@code |0,2>} ni siquiera existen
 * en el espacio de Fock fermionico. La clase {@link Fock} lo construye con {@code
 * multiFermionUnitary(H, 2)} = {@code Lambda_2(H)} (determinantes de submatrices de {@code H}).
 * <p>
 * Matiz importante: el antiagrupamiento total exige que los 2 fermiones sean identicos en TODO,
 * tambien en el espin. El signo {@code -} afecta a la funcion de onda completa (camino x espin): en
 * el singlete de espin (antisimetrico) la parte de camino queda SIMETRICA y los 2 electrones se
 * AGRUPAN como bosones. Se ve en los puntos 3 y 4 con 4 modos (camino a/b x espin arriba/abajo).
 */
public class TestQuantum_HongOuMandelFermiones01 {

	/** Indices de modo de los puntos 3 y 4: camino (a/b) x espin (arriba/abajo), modo = 2*camino + espin. */
	private static final int A_ARRIBA = 0, A_ABAJO = 1, B_ARRIBA = 2, B_ABAJO = 3;

	public static void main(String[] args) {
		int shots = 3477;
		Random random = new Random();

		Complex.printBoxText(3, 90, "Hong-Ou-Mandel con fermiones: 2 electrones identicos en el mismo semiespejo 50/50");

		// 1. Fermiones sin espin (o con el mismo espin): 2 modos, a y b.
		Complex.printLineText(1, 90, "1. Entrada |1,1> (un fermion por cada puerto) y semiespejo Lambda_2(H).", false, false);
		int[][] base = Fock.fermionBasis(2, 2);
		System.out.println("  Base de Fock fermionica de 2 fermiones en 2 modos: solo " + Fock.basisLabel(base[0])
				+ " (|2,0> y |0,2> los prohibe Pauli)");
		MatrixComplex semiespejo2 = Fock.multiFermionUnitary(Qubits.hadamard(), 2);
		semiespejo2.println("Semiespejo sobre 2 fermiones, Lambda_2(H) = det(H):");
		MatrixComplex salida = semiespejo2.times(Fock.fermionKet(1, 1));
		System.out.printf("  Estado de salida: %s %s  (amplitud de coincidencia = det(H) = -1/2 - 1/2 = -1)\n",
				salida.getItem(0, 0).toString(), Fock.basisLabel(base[0]));
		System.out.printf("  Comparacion: Per(H) = %s (bosones), det(H) = %s (fermiones)\n",
				Fock.permanent(Qubits.hadamard()).toString(), Fock.determinant(Qubits.hadamard()).toString());

		// 2. Medida.
		Complex.printLineText(1, 90, "2. Simulacion de medidas (" + shots + " disparos) con fermiones identicos.", true, false);
		int[] cuentas = Fock.measure(salida, shots, random);
		System.out.printf("  %s : %4d (%6.2f%%)\n", Fock.basisLabel(base[0]), cuentas[0], 100.0 * cuentas[0] / shots);
		if (cuentas[0] != shots) {
			throw new IllegalStateException("HOM fermionico: no todas las medidas son coincidencias");
		}
		System.out.println("  -> 100% coincidencias: los 2 fermiones salen SIEMPRE separados (antiagrupamiento).");
		System.out.println("  Resumen de P(coincidencia): bosones 0, distinguibles 1/2, fermiones 1.");

		// 3. Con espin: 4 modos (camino x espin), el semiespejo actua solo sobre el camino, U = H (x) I_2.
		// Los estados de entrada tienen un electron por el puerto a y otro por el b; lo que cambia es
		// el espin. Recordatorio del signo: fermionKet(...) = a+_i1 a+_i2 |0> con i1 < i2.
		Complex.printLineText(1, 90, "3. Con espin (4 modos: camino x espin): el resultado depende del estado de espin.", true, false);
		int[][] base4 = Fock.fermionBasis(2, 4);
		MatrixComplex semiespejoCamino = Fock.multiFermionUnitary(Qubits.hadamard().kroneckerprod(Qubits.identity2()), 2);
		double s = 1.0 / Math.sqrt(2.0);
		MatrixComplex mismoEspin = Fock.fermionKet(ocupacion(A_ARRIBA, B_ARRIBA));
		MatrixComplex singlete = Fock.fermionKet(ocupacion(A_ARRIBA, B_ABAJO))
				.minus(Fock.fermionKet(ocupacion(A_ABAJO, B_ARRIBA))).times(s);
		MatrixComplex triplete0 = Fock.fermionKet(ocupacion(A_ARRIBA, B_ABAJO))
				.plus(Fock.fermionKet(ocupacion(A_ABAJO, B_ARRIBA))).times(s);
		casoEspin("Mismo espin |a arriba, b arriba>", mismoEspin, semiespejoCamino, base4, 1.0, shots, random);
		casoEspin("Triplete m=0 (|a arr,b abj> + |a abj,b arr>)/sqrt2", triplete0, semiespejoCamino, base4, 1.0, shots, random);
		casoEspin("Singlete (|a arr,b abj> - |a abj,b arr>)/sqrt2", singlete, semiespejoCamino, base4, 0.0, shots, random);
		System.out.println("  -> en el singlete el espin es antisimetrico, el camino queda simetrico: se AGRUPAN como bosones.");

		// 4. El "pico" fermionico, espejo del dip bosonico: el electron 1 entra por a con espin
		// arriba; el 2 por b con espin cos(alfa)|arriba> + sin(alfa)|abajo>. cos(alfa) es el
		// solapamiento de espin (alfa=0: identicos; alfa=pi/2: espines ortogonales, distinguibles).
		// La probabilidad de coincidencia NO se introduce a mano: sale del calculo en Fock y se
		// compara con la formula (1 + cos^2(alfa))/2.
		Complex.printLineText(1, 90, "4. Pico fermionico: coincidencias frente al solapamiento de espin cos(alfa).", true, false);
		System.out.println("  alfa/(pi/2)  solapamiento  P(coinc) Fock  formula (1+cos^2)/2  medido (" + shots + " disparos)");
		for (int paso = 0; paso <= 8; ++paso) {
			double alfa = paso * Math.PI / 16.0;
			MatrixComplex entrada4 = Fock.fermionKet(ocupacion(A_ARRIBA, B_ARRIBA)).times(Math.cos(alfa))
					.plus(Fock.fermionKet(ocupacion(A_ARRIBA, B_ABAJO)).times(Math.sin(alfa)));
			MatrixComplex salida4 = semiespejoCamino.times(entrada4);
			double pCoincidencia = probabilidadCoincidencia(salida4, base4);
			double formula = (1.0 + Math.pow(Math.cos(alfa), 2)) / 2.0;
			if (Math.abs(pCoincidencia - formula) > 1e-12) {
				throw new IllegalStateException("Pico fermionico: P(coincidencia)=" + pCoincidencia + " frente a formula=" + formula);
			}
			int coincidencias4 = coincidenciasMedidas(salida4, base4, shots, random);
			System.out.printf("     %5.3f        %6.4f         %6.4f            %6.4f            %4d (%6.2f%%)\n",
					paso / 8.0, Math.cos(alfa), pCoincidencia, formula, coincidencias4, 100.0 * coincidencias4 / shots);
		}
		System.out.println("  -> solapamiento 1 (identicos): 100% coincidencias; solapamiento 0 (distinguibles): 50%.");
		System.out.println("     Con bosones el mismo barrido da (1 - cos^2)/2: de 0% a 50% (el dip de HOM).");
	}

	/** Aplica el semiespejo a un estado de espin, imprime la salida y comprueba P(coincidencia). */
	private static void casoEspin(String titulo, MatrixComplex entrada, MatrixComplex semiespejo, int[][] base4,
			double esperado, int shots, Random random) {
		MatrixComplex salida = semiespejo.times(entrada);
		double p = probabilidadCoincidencia(salida, base4);
		if (Math.abs(p - esperado) > 1e-12) {
			throw new IllegalStateException(titulo + ": P(coincidencia)=" + p + " frente a " + esperado);
		}
		System.out.println("  " + titulo + ":");
		for (int k = 0; k < base4.length; ++k) {
			if (salida.getItem(k, 0).abs() > 1e-12) {
				System.out.printf("      %s  %s  %s\n", Fock.basisLabel(base4[k]), salida.getItem(k, 0).toString(),
						esCoincidencia(base4[k]) ? "(coincidencia)" : "(misma salida)");
			}
		}
		int c = coincidenciasMedidas(salida, base4, shots, random);
		System.out.printf("      P(coincidencia) = %.4f; medido %4d de %d (%6.2f%%)\n", p, c, shots, 100.0 * c / shots);
	}

	private static double probabilidadCoincidencia(MatrixComplex estado, int[][] base4) {
		double p = 0.0;
		for (int k = 0; k < base4.length; ++k) {
			if (esCoincidencia(base4[k])) {
				p += Math.pow(estado.getItem(k, 0).abs(), 2);
			}
		}
		return p;
	}

	private static int coincidenciasMedidas(MatrixComplex estado, int[][] base4, int shots, Random random) {
		int[] cuentas = Fock.measure(estado, shots, random);
		int c = 0;
		for (int k = 0; k < base4.length; ++k) {
			if (esCoincidencia(base4[k])) c += cuentas[k];
		}
		return c;
	}

	/** Vector de ocupacion de 4 modos con un fermion en cada uno de los 2 modos dados (distintos). */
	private static int[] ocupacion(int modo1, int modo2) {
		int[] occ = new int[4];
		occ[modo1] = 1;
		occ[modo2] = 1;
		return occ;
	}

	/** Coincidencia: un fermion en la salida a (cualquier espin) y otro en la salida b. */
	private static boolean esCoincidencia(int[] occ) {
		return occ[A_ARRIBA] + occ[A_ABAJO] == 1 && occ[B_ARRIBA] + occ[B_ABAJO] == 1;
	}
}
