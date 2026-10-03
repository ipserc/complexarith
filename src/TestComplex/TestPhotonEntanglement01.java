package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.BellTest;
import com.ipserc.arith.quantum.Qubits;

/**
 * Lectura "fotonica" del par de Bell ya existente en {@link Qubits#bellPhiPlus()} -- NO hay
 * ninguna clase nueva para fotones en {@code com.ipserc.arith.quantum}, porque no hace falta: un
 * foton no tiene masa, asi que su grado de libertad de polarizacion solo tiene 2 helicidades
 * fisicamente realizables (+1/-1), igual que un espin-1/2 -- cae en el mismo espacio de Hilbert
 * 2D que ya modela {@code Qubits.ket0()}/{@code ket1()} (ver {@code
 * quantum_doc/17_limites_y_extensiones.md}, seccion 1). Este test reetiqueta {@code |0>}/{@code
 * |1>} como polarizacion Horizontal/Vertical y recorre, paso a paso, los 3 experimentos clasicos
 * del par EPR fotonico: correlacion perfecta en la misma base, correlacion angular a polarizadores
 * desalineados, y la violacion de la desigualdad de Bell (CHSH) tipo experimento de Aspect.
 */
public class TestPhotonEntanglement01 {

	private static String polarizacionLabel(int bit) {
		return bit == 0 ? "H" : "V";
	}

	private static String parFotonesLabel(int index) {
		return polarizacionLabel(index >> 1) + polarizacionLabel(index & 1);
	}

	public static void main(String[] args) {
		int shots = 3477;
		Random random = new Random();

		// 1. Preparar el par de fotones entrelazados.
		Complex.printBoxText(3, 90, "Par de fotones entrelazados: |Phi+> = (|HH> + |VV>)/sqrt(2)");
		Complex.printLineText(1, 90, "1. Preparar el par: Qubits.bellPhiPlus() con |0>=H (horizontal), |1>=V (vertical).", false, false);
		MatrixComplex parFotones = Qubits.bellPhiPlus();
		parFotones.println("Estado del par:");
		for (int index = 0; index < parFotones.rows(); ++index) {
			double probabilidad = Math.pow(parFotones.getItem(index, 0).abs(), 2) * 100.0;
			System.out.printf("P(%s) = %.2f%%\n", parFotonesLabel(index), probabilidad);
		}

		// 2. Correlacion perfecta en la base H/V, disparo a disparo -- mismo patron que
		// TestQubitMeasure01.verificar_correlacion_ghz(), aqui con solo 2 fotones: medir el fotonA
		// en H/V ya determina el resultado del fotonB, sin haberlo tocado.
		Complex.printLineText(1, 90, "2. Correlacion perfecta en la base H/V: midiendo ambos fotones " + shots + " veces, disparo a disparo.", true, false);
		int hh = 0, vv = 0, mixtos = 0;
		for (int shot = 0; shot < shots; ++shot) {
			int resultado = Qubits.measure(parFotones, random);
			boolean fotonAesH = (resultado >> 1) == 0;
			boolean fotonBesH = (resultado & 1) == 0;
			if (fotonAesH != fotonBesH) mixtos++;
			else if (fotonAesH) hh++;
			else vv++;
		}
		System.out.printf("  HH: %d (%.2f%%)\n", hh, 100.0 * hh / shots);
		System.out.printf("  VV: %d (%.2f%%)\n", vv, 100.0 * vv / shots);
		System.out.printf("  Mixtos (HV o VH), nunca deberian aparecer: %d\n", mixtos);
		if (mixtos > 0) {
			throw new IllegalStateException("se midieron " + mixtos + " pares HV/VH -- el entrelazamiento se ha roto");
		}

		// 3. Correlacion angular: polarizadores desalineados un angulo relativo (a-b), en vez de
		// medir siempre en la misma base H/V. BellTest.correlation(state,opA,opB) da el valor
		// esperado EXACTO <Phi+|A(a) (x) B(b)|Phi+>, que para este par vale cos(a-b) (formula
		// cerrada documentada en BellTest.chsh -- theta en spinOperator() es el angulo COMPLETO del
		// operador, no el "angulo de polarizador" de libro de texto, que llevaria cos(2*(a-b))).
		Complex.printLineText(1, 90, "3. Correlacion angular E(a,b) a varios desalineamientos de polarizador (formula exacta: E(a,b)=cos(a-b)).", true, false);
		double[] desalineamientos = { 0, Math.PI / 8, Math.PI / 4, Math.PI / 2, Math.PI };
		for (double delta : desalineamientos) {
			double a = 0, b = delta;
			double eExacta = BellTest.correlation(parFotones, Qubits.spinOperator(a), Qubits.spinOperator(b));
			double eFormula = Math.cos(a - b);
			System.out.printf("  delta=%.4f rad -> E(a,b)=%.6f (cos(a-b)=%.6f)\n", delta, eExacta, eFormula);
		}

		// 4. Desigualdad de Bell (CHSH): con los angulos que maximizan la violacion para esta
		// parametrizacion (espaciados pi/4, ver TestBell01), el experimento de Aspect con fotones
		// entrelazados supera el limite clasico S<=2 y se acerca al limite de Tsirelson
		// 2*sqrt(2) -- la prueba de que ninguna teoria local-realista de "variables ocultas" puede
		// explicar esta correlacion.
		Complex.printLineText(1, 90, "4. Desigualdad de Bell (CHSH): violacion del limite clasico S<=2.", true, false);
		double a = 0, aPrime = Math.PI / 2, b = Math.PI / 4, bPrime = 3 * Math.PI / 4;
		double s = BellTest.chsh(parFotones, a, aPrime, b, bPrime);
		double tsirelson = 2 * Math.sqrt(2);
		System.out.printf("  S = %.6f (limite clasico = 2, limite de Tsirelson = %.6f)\n", s, tsirelson);
		System.out.println("  " + (s > 2.0 ? "Viola el limite clasico: entrelazamiento genuino, no correlacion clasica." : "No viola el limite clasico (no deberia pasar con bellPhiPlus())."));

		System.out.println("\n");
	}
}
