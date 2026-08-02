package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Comparison test for {@code Eigenspace.eigenvaluesQR()} (Etapa 4 del plan QR-con-desplazamientos,
 * ver {@code Claude/ComplexArithRev.md} / plan mutable-rolling-stardust.md): mide, sin sustituir
 * el motor por defecto de {@code Eigenspace} (polinomio caracteristico + Durand-Kerner/
 * Aberth-Ehrlich), si la via alternativa (Hessenberg + QR con desplazamiento de Wilkinson,
 * {@code QRSchurfactor}) aporta algo real -- no solo en el motor de autovalores aislado (ya
 * verificado en {@code TestQRSchur01}), sino cuando se compara directamente contra el motor
 * existente sobre las mismas matrices, incluidos los casos adversarios (autovalor repetido en un
 * bloque de Jordan grande) que motivaron toda esta hoja de ruta desde la Novena sesion.
 * <p>
 * Dos partes:
 * <ol>
 * <li>Bateria aleatoria (semilla fija, reproducible): para matrices genericas (tipicamente
 * autovalores bien separados), ambos motores deben coincidir como conjunto dentro de tolerancia --
 * verifica que la via nueva no introduce discrepancias en el caso comun.</li>
 * <li>Casos adversarios conocidos: un unico bloque de Jordan de tamano n con autovalor lambda
 * REPETIDO n veces, conjugado por P no ortogonal -- exactamente el patron que la Novena sesion
 * documento como limite de precision (no de desbordamiento, ya resuelto por
 * {@code Polynom.solveRobust()}) para autovalores repetidos. Reporta, sin asumir el resultado de
 * antemano, el error maximo de cada motor frente al lambda verdadero conocido.</li>
 * </ol>
 */
public class TestEigenspaceQRCompare01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void report(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + " -- " + detail);
		if (ok) ++pass; else ++fail;
	}

	private static MatrixComplex expandByMultiplicity(MatrixComplex eigenvaluesWithMult) {
		int total = 0;
		for (int i = 0; i < eigenvaluesWithMult.rows(); ++i) total += (int) eigenvaluesWithMult.getItem(i, 1).rep();
		MatrixComplex expanded = new MatrixComplex(total, 1);
		int idx = 0;
		for (int i = 0; i < eigenvaluesWithMult.rows(); ++i) {
			Complex val = eigenvaluesWithMult.getItem(i, 0);
			int mult = (int) eigenvaluesWithMult.getItem(i, 1).rep();
			for (int k = 0; k < mult; ++k) expanded.setItem(idx++, 0, val);
		}
		return expanded;
	}

	private static boolean matchesAsSet(MatrixComplex a, MatrixComplex b, double tolerance) {
		if (a.rows() != b.rows()) return false;
		boolean[] used = new boolean[b.rows()];
		for (int i = 0; i < a.rows(); ++i) {
			Complex v = a.getItem(i, 0);
			int bestIdx = -1;
			double bestDist = Double.MAX_VALUE;
			for (int j = 0; j < b.rows(); ++j) {
				if (used[j]) continue;
				double d = v.minus(b.getItem(j, 0)).mod();
				if (d < bestDist) { bestDist = d; bestIdx = j; }
			}
			if (bestIdx == -1 || bestDist > tolerance) return false;
			used[bestIdx] = true;
		}
		return true;
	}

	private static double maxNearestDistance(MatrixComplex actual, Complex[] expected) {
		double max = 0.0;
		for (Complex e : expected) {
			double best = Double.MAX_VALUE;
			for (int j = 0; j < actual.rows(); ++j) best = Math.min(best, e.minus(actual.getItem(j, 0)).mod());
			max = Math.max(max, best);
		}
		return max;
	}

	private static MatrixComplex jordanBlock(int n, Complex lambda) {
		MatrixComplex j = new MatrixComplex(n, n);
		for (int i = 0; i < n; ++i) {
			j.setItem(i, i, lambda);
			if (i + 1 < n) j.setItem(i, i + 1, Complex.ONE);
		}
		return j;
	}

	private static void randomBattery(int count, int minSize, int maxSize, int base, double tolerance) {
		int agree = 0, disagree = 0, defaultThrew = 0, qrThrew = 0;
		Random rnd = new Random(42);
		for (int t = 0; t < count; ++t) {
			int n = minSize + rnd.nextInt(maxSize - minSize + 1);
			MatrixComplex m = new MatrixComplex(n, n);
			m.initMatrixRandomInt(base);

			MatrixComplex defaultExpanded, qrRaw;
			try {
				Eigenspace es = new Eigenspace(m);
				defaultExpanded = expandByMultiplicity(es.eigenvalues());
				qrRaw = es.eigenvaluesQR();
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().contains("QRSchurfactor")) ++qrThrew;
				else ++defaultThrew;
				continue;
			}

			if (matchesAsSet(defaultExpanded, qrRaw, tolerance)) ++agree; else ++disagree;
		}
		report("random battery (" + count + " matrices, n=" + minSize + ".." + maxSize + ", base=" + base + ")",
			disagree == 0 && defaultThrew == 0 && qrThrew == 0,
			"agree=" + agree + " disagree=" + disagree + " defaultEngineThrew=" + defaultThrew + " qrEngineThrew=" + qrThrew);
	}

	private static void adversarialJordanBlock(String label, int n, Complex lambda, MatrixComplex p, double maxAcceptableError) {
		MatrixComplex a = p.times(jordanBlock(n, lambda)).times(p.inverse());
		Complex[] trueEigenvalues = new Complex[n];
		for (int i = 0; i < n; ++i) trueEigenvalues[i] = lambda;

		Eigenspace es = new Eigenspace(a);
		double defaultErr, qrErr;
		String qrNote = "";
		defaultErr = maxNearestDistance(expandByMultiplicity(es.eigenvalues()), trueEigenvalues);
		try {
			qrErr = maxNearestDistance(es.eigenvaluesQR(), trueEigenvalues);
		} catch (IllegalArgumentException e) {
			qrErr = Double.NaN;
			qrNote = " (QR engine threw: " + e.getMessage() + ")";
		}

		boolean ok = defaultErr < maxAcceptableError && (Double.isNaN(qrErr) ? false : qrErr < maxAcceptableError);
		report(label, ok, "defaultEngineErr=" + defaultErr + " QRengineErr=" + qrErr + qrNote);
	}

	public static void main(String[] args) {
		Complex.setFixedOFF();

		randomBattery(300, 3, 5, 12, 1e-4);

		// Casos adversarios documentados en la Novena sesion: bloque de Jordan unico, autovalor
		// repetido n veces, P no ortogonal. n=3,P y n=4,P reutilizados de sesiones/etapas previas.
		MatrixComplex p3 = new MatrixComplex("1,2,0;0,1,1;1,0,1");
		MatrixComplex p4 = new MatrixComplex("1,2,0,1;0,1,3,0;1,0,1,2;0,1,0,1");

		adversarialJordanBlock("3x3 bloque de Jordan unico, lambda=50 (repetido x3)", 3, new Complex(50, 0), p3, 1e-2);
		adversarialJordanBlock("4x4 bloque de Jordan unico, lambda=3 (repetido x4)", 4, new Complex(3, 0), p4, 1e-2);
		adversarialJordanBlock("4x4 bloque de Jordan unico, lambda=-50 (repetido x4)", 4, new Complex(-50, 0), p4, 1e-1);

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}

}
