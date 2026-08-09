package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Benchmarks MatrixComplex.times(MatrixComplex)/timesEq(MatrixComplex) (current) against the new
 * timesEqRaw(MatrixComplex) (Camino A de rendimiento, Vector API, Vigesimotercera sesion, ver
 * Claude/ComplexArithRev.md) across several matrix sizes, isolating the O(n^3) product itself --
 * unlike ScratchTaylorMercatorBench01.java (Decimoctava sesion), which benchmarked whole Taylor
 * series functions at a single fixed N=8 and found no measurable difference for the earlier
 * allocation-focused change. Here the goal is to find whether/where the trigonometric-call
 * reduction (O(rows*cols*inner) -> O(rows*cols) calls to Complex.setPolCoord()) becomes visible.
 */
public class ScratchTimesEqRawBench01 {
	static final int WARMUP = 5;
	static final int REPS = 30;

	public static void main(String[] args) {
		int[] sizes = {8, 20, 50, 100, 200};
		for (int n : sizes) {
			MatrixComplex a = buildFixed(n, 0.37);
			MatrixComplex b = buildFixed(n, -0.21);

			double msTimesEq = benchTimesEq(a, b);
			double msTimesEqRaw = benchTimesEqRaw(a, b);
			double speedup = msTimesEq / msTimesEqRaw;
			System.out.println("N=" + n + "  timesEq=" + String.format("%.4f", msTimesEq) + "ms"
					+ "  timesEqRaw=" + String.format("%.4f", msTimesEqRaw) + "ms"
					+ "  speedup=" + String.format("%.2fx", speedup));
		}

		System.out.println("\n=== Simulacion de bucle Taylor (20 iteraciones encadenadas, mismo patron powMatrix.timesEq(x) ===");
		for (int n : new int[] {8, 50, 100}) {
			MatrixComplex x = buildFixed(n, 0.05); // escala pequena, como thisNorma en exp_()
			double msChainEq = benchChainTimesEq(x, n);
			double msChainRaw = benchChainTimesEqRaw(x, n);
			System.out.println("N=" + n + "  cadena timesEq=" + String.format("%.4f", msChainEq) + "ms"
					+ "  cadena timesEqRaw=" + String.format("%.4f", msChainRaw) + "ms"
					+ "  speedup=" + String.format("%.2fx", msChainEq / msChainRaw));
		}
	}

	static MatrixComplex buildFixed(int n, double scale) {
		MatrixComplex m = new MatrixComplex(n, n);
		for (int r = 0; r < n; r++)
			for (int c = 0; c < n; c++)
				m.setItem(r, c, new Complex(scale * Math.sin(1.3 * r + 0.7 * c + 1), scale * Math.cos(0.9 * r - 1.1 * c + 0.5)));
		return m;
	}

	static double benchTimesEq(MatrixComplex a, MatrixComplex b) {
		for (int i = 0; i < WARMUP; i++) a.copy().timesEq(b);
		long total = 0;
		for (int i = 0; i < REPS; i++) {
			long t0 = System.nanoTime();
			a.copy().timesEq(b);
			total += System.nanoTime() - t0;
		}
		return total / (double) REPS / 1_000_000.0;
	}

	static double benchTimesEqRaw(MatrixComplex a, MatrixComplex b) {
		for (int i = 0; i < WARMUP; i++) a.copy().timesEqRaw(b);
		long total = 0;
		for (int i = 0; i < REPS; i++) {
			long t0 = System.nanoTime();
			a.copy().timesEqRaw(b);
			total += System.nanoTime() - t0;
		}
		return total / (double) REPS / 1_000_000.0;
	}

	static double benchChainTimesEq(MatrixComplex x, int n) {
		for (int i = 0; i < WARMUP; i++) chainEq(x, n);
		long total = 0;
		for (int i = 0; i < REPS; i++) {
			long t0 = System.nanoTime();
			chainEq(x, n);
			total += System.nanoTime() - t0;
		}
		return total / (double) REPS / 1_000_000.0;
	}

	static double benchChainTimesEqRaw(MatrixComplex x, int n) {
		for (int i = 0; i < WARMUP; i++) chainRaw(x, n);
		long total = 0;
		for (int i = 0; i < REPS; i++) {
			long t0 = System.nanoTime();
			chainRaw(x, n);
			total += System.nanoTime() - t0;
		}
		return total / (double) REPS / 1_000_000.0;
	}

	static void chainEq(MatrixComplex x, int n) {
		MatrixComplex powMatrix = MatrixComplex.eye(n);
		for (int k = 0; k < 20; ++k) powMatrix.timesEq(x);
	}

	static void chainRaw(MatrixComplex x, int n) {
		MatrixComplex powMatrix = MatrixComplex.eye(n);
		for (int k = 0; k < 20; ++k) powMatrix.timesEqRaw(x);
	}
}
