package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.BellTest;
import com.ipserc.arith.quantum.Qubits;

/**
 * Lectura "fotonica" del par de Bell ya existente en {@link Qubits#bellPsiMinus()} -- NO hay
 * ninguna clase nueva para fotones en {@code com.ipserc.arith.quantum}, porque no hace falta: un
 * foton no tiene masa, asi que su grado de libertad de polarizacion solo tiene 2 helicidades
 * fisicamente realizables (+1/-1), igual que un espin-1/2 -- cae en el mismo espacio de Hilbert
 * 2D que ya modela {@code Qubits.ket0()}/{@code ket1()} (ver {@code
 * quantum_doc/17_limites_y_extensiones.md}, seccion 1). Se usa el singlete {@code Psi-} (no {@code
 * Phi+}) porque es el estado ANTICORRELACIONADO en polarizacion -- fotonA Horizontal implica
 * fotonB Vertical y viceversa -- el caso tipicamente citado en la bibliografia de pares de fotones
 * entrelazados (conversion parametrica descendente Tipo II, protocolo de distribucion de clave
 * cuantica E91 de Ekert). {@code Phi+} ({@code |HH>+|VV>}, CORRELACIONADO) es igual de valido
 * fisicamente -- lo produce, por ejemplo, Tipo I -- y es localmente equivalente a {@code Psi-} (una
 * puerta Pauli-X sobre un solo foton pasa de uno a otro, igual que una lamina de media onda):
 * mismo entrelazamiento, distinta convencion de montaje. Este test recorre, paso a paso, los 3
 * experimentos clasicos del par EPR fotonico: correlacion perfecta en la misma base, correlacion
 * angular a polarizadores desalineados, y la violacion de la desigualdad de Bell (CHSH) tipo
 * experimento de Aspect.
 */
public class TestQuantum_PhotonEntanglement01 {

	private static String polarizacionLabel(int bit) {
		return bit == 0 ? "H" : "V";
	}

	private static String parFotonesLabel(int index) {
		return polarizacionLabel(index >> 1) + polarizacionLabel(index & 1);
	}

	public static void main(String[] args) {
		int shots = 100001;
		Random random = new Random();

		// 1. Preparar el par de fotones entrelazados (singlete, anticorrelacionado).
		Complex.printBoxText(3, 90, "Par de fotones entrelazados: |Psi-> = (|HV> - |VH>)/sqrt(2)");
		Complex.printLineText(1, 90, "1. Preparar el par: Qubits.bellPsiMinus() con |0>=H (horizontal), |1>=V (vertical).", false, false);
		MatrixComplex parFotones = Qubits.bellPsiMinus();
		parFotones.println("Estado del par:");
		for (int index = 0; index < parFotones.rows(); ++index) {
			double probabilidad = Math.pow(parFotones.getItem(index, 0).abs(), 2) * 100.0;
			System.out.printf("P(%s) = %.2f%%\n", parFotonesLabel(index), probabilidad);
		}

		// 2. Correlacion perfecta (ANTIcorrelacion) en la base H/V, disparo a disparo -- mismo
		// patron que TestQubitMeasure01.verificar_correlacion_ghz(), aqui con solo 2 fotones:
		// medir el fotonA en H/V ya determina el resultado del fotonB (el OPUESTO), sin haberlo
		// tocado.
		Complex.printLineText(1, 90, "2. Anticorrelacion perfecta en la base H/V: midiendo ambos fotones " + shots + " veces, disparo a disparo.", true, false);
		int hv = 0, vh = 0, iguales = 0;
		for (int shot = 0; shot < shots; ++shot) {
			int resultado = Qubits.measure(parFotones, random);
			// resultado siempre será < que Num Estados ==> qué puede ser 0, 1, 2, 3. 
			
			// [0 y 2 resultado >> 1 da 1]
			// [1 y 3 resultado >> 1 da 0]
			boolean fotonAesH = (resultado >> 1) == 0;
			// [0 y 2 resultado & 1 da 0]
			// [1 y 3 resultado & 1 da 1]
			boolean fotonBesH = (resultado & 1) == 0;
			if (fotonAesH == fotonBesH) iguales++;
			else if (fotonAesH) hv++;
			else vh++;
		}
		System.out.printf("  HV: %d (%.2f%%)\n", hv, 100.0 * hv / shots);
		System.out.printf("  VH: %d (%.2f%%)\n", vh, 100.0 * vh / shots);
		System.out.printf("  Iguales (HH o VV), nunca deberian aparecer: %d\n", iguales);
		if (iguales > 0) {
			throw new IllegalStateException("se midieron " + iguales + " pares HH/VV -- el entrelazamiento se ha roto");
		}

		// 3. Correlacion angular: polarizadores desalineados un angulo relativo (a-b), en vez de
		// medir siempre en la misma base H/V. BellTest.correlation(state,opA,opB) da el valor
		// esperado EXACTO <Psi-|A(a) (x) B(b)|Psi->, que para este par vale -cos(a-b) -- misma
		// dependencia en (a-b) que bellPhiPlus(), pero con el signo cambiado (ver Javadoc de
		// Qubits.bellPsiMinus()); theta en spinOperator() es el angulo COMPLETO del operador, no
		// el "angulo de polarizador" de libro de texto, que llevaria cos(2*(a-b)).
		Complex.printLineText(1, 90, "3. Correlacion angular E(a,b) a varios desalineamientos de polarizador (formula exacta: E(a,b)=-cos(a-b)).", true, false);
		Complex.printLineText(1, 90, "a = Math.PI / 4, desalineamientos = { 0, Math.PI / 8, Math.PI / 4, Math.PI / 2, Math.PI };", false, false);
		double[] desalineamientos = { 0, Math.PI / 8, Math.PI / 4, Math.PI / 2, Math.PI };
		for (double delta : desalineamientos) {
			double a = Math.PI / 4, b = delta;
			double eExacta = BellTest.correlation(parFotones, Qubits.spinOperator(a), Qubits.spinOperator(b));
			double eFormula = -Math.cos(a - b);
			System.out.printf("  delta=%.4f rad -> E(a,b)=%.6f (-cos(a-b)=%.6f)\n", delta, eExacta, eFormula);
		}

		// 4. Desigualdad de Bell (CHSH): mismos angulos que TestBell01 (espaciados pi/4). Como
		// E(a,b)=-cos(a-b) lleva el signo cambiado respecto a bellPhiPlus(), S sale con el signo
		// cambiado tambien (-2*sqrt(2) en vez de +2*sqrt(2)) -- un artefacto de la convencion de
		// signo, no una fisica distinta; lo que importa es |S|, que sigue violando el limite
		// clasico |S|<=2 y alcanza el mismo limite de Tsirelson.
		Complex.printLineText(1, 90, "4. Desigualdad de Bell (CHSH): violacion del limite clasico |S|<=2.", true, false);
		double a = 0, aPrime = Math.PI / 2, b = Math.PI / 4, bPrime = 3 * Math.PI / 4;
		double s = BellTest.chsh(parFotones, a, aPrime, b, bPrime);
		double tsirelson = 2 * Math.sqrt(2);
		System.out.printf("  S = %.6f, |S| = %.6f (limite clasico = 2, limite de Tsirelson = %.6f)\n", s, Math.abs(s), tsirelson);
		System.out.println("  " + (Math.abs(s) > 2.0 ? "Viola el limite clasico: entrelazamiento genuino, no correlacion clasica." : "No viola el limite clasico (no deberia pasar con bellPsiMinus())."));

		System.out.println("\n");
	}
}
