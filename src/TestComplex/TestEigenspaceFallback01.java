package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression test for the {@code QRSchurfactor} fallback in {@code Eigenspace.eigenval()} (ver
 * {@code Eigenspace.VERSION} 1.9 / {@code Claude/ComplexArithRev.md}): {@code
 * charactPoly.solveRobust()} (Durand-Kerner con respaldo en Aberth-Ehrlich) puede exhausar ambos
 * root-finders para polinomios caracteristicos de grado alto con coeficientes muy dispares --
 * este fallback rescata ese caso via {@code QRSchurfactor}, que nunca forma el polinomio
 * caracteristico explicitamente.
 * <p>
 * Las tres matrices de abajo son casos REALES encontrados con una busqueda acotada (no
 * sinteticos ni construidos a mano): matrices 10x10/12x12 enteras aleatorias con entradas hasta
 * +-1000 cuyo {@code eigenval()} lanzaba {@code IllegalArgumentException} ("Arithmetic Overflow")
 * antes de este fix. Verificacion: (1) ya no lanza excepcion; (2) la traza de la matriz coincide
 * con la suma de autovalores (con multiplicidad) devueltos -- comprobacion independiente de que
 * el resultado del fallback es genuino, no basura, sin depender de un oraculo de autovalores
 * exacto (no disponible para una matriz aleatoria densa de este tamano).
 */
public class TestEigenspaceFallback01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void report(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + " -- " + detail);
		if (ok) ++pass; else ++fail;
	}

	private static Complex trace(MatrixComplex a) {
		Complex sum = new Complex(0, 0);
		for (int i = 0; i < a.rows(); ++i) sum = sum.plus(a.getItem(i, i));
		return sum;
	}

	private static Complex sumWithMultiplicity(MatrixComplex eigenvaluesWithMult) {
		Complex sum = new Complex(0, 0);
		for (int i = 0; i < eigenvaluesWithMult.rows(); ++i) {
			Complex value = eigenvaluesWithMult.getItem(i, 0);
			int mult = (int) eigenvaluesWithMult.getItem(i, 1).rep();
			sum = sum.plus(value.times((double) mult));
		}
		return sum;
	}

	private static void checkRescued(String label, MatrixComplex a, double tolerance) {
		try {
			Eigenspace es = new Eigenspace(a);
			Complex traceA = trace(a);
			Complex traceEigen = sumWithMultiplicity(es.eigenvalues());
			double diff = traceA.minus(traceEigen).mod();
			report(label, diff < tolerance, "trace(A)=" + traceA + " sum(eigenvalues)=" + traceEigen + " diff=" + diff);
		} catch (Exception e) {
			report(label, false, "threw (fallback did not rescue it): " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		Complex.setFixedOFF();

		checkRescued("10x10 real integer matrix (base ~1000), threw before the fallback",
			new MatrixComplex("-253.0, 624.0, 362.0,-643.0, 627.0, 981.0, 811.0,-973.0, 297.0, 670.0;"
				+ "-53.0, 465.0, 674.0,-834.0,-616.0,-396.0, 960.0, 777.0, 304.0, 998.0;"
				+ " 636.0, 353.0,-278.0, 12.0,-258.0,-804.0, 698.0, 926.0,-75.0, 527.0;"
				+ "-441.0,-408.0,-963.0, 295.0,-373.0, 428.0,-173.0,-410.0,-700.0, 574.0;"
				+ "-395.0,-303.0,-874.0, 58.0,-645.0, 69.0,-577.0, 913.0, 460.0, 455.0;"
				+ "-259.0, 881.0,-911.0,-776.0, 753.0,-496.0, 940.0, 282.0,-448.0, 854.0;"
				+ "-502.0, 459.0, 538.0, 576.0,-35.0, 781.0,-827.0,-464.0, 733.0,-394.0;"
				+ " 606.0, 343.0,-150.0,-244.0,-99.0,-605.0,-611.0,-154.0, 969.0,-933.0;"
				+ "-940.0, 326.0, 392.0, 721.0, 7.0,-805.0,-203.0,-689.0, 593.0, 365.0;"
				+ " 602.0,-182.0,-66.0,-351.0,-929.0,-668.0, 377.0, 955.0,-5.0,-921.0"), 1e-3);

		checkRescued("second 10x10 real integer matrix (base ~1000), threw before the fallback",
			new MatrixComplex(" 868.0, 446.0, 89.0, 243.0,-367.0,-205.0, 468.0,-522.0, 762.0, 265.0;"
				+ "-933.0, 787.0,-317.0, 756.0,-949.0, 58.0, 671.0,-19.0,-910.0,-686.0;"
				+ "-956.0, 138.0,-853.0, 39.0, 240.0,-916.0,-747.0, 885.0, 786.0, 785.0;"
				+ "-844.0, 965.0,-621.0, 166.0,-602.0, 257.0,-931.0,-763.0,-664.0, 431.0;"
				+ "-764.0, 301.0,-418.0,-994.0,-553.0, 210.0, 610.0, 632.0,-958.0, 742.0;"
				+ "-529.0,-236.0,-518.0, 38.0,-260.0, 124.0,-623.0, 628.0,-504.0,-955.0;"
				+ "-306.0, 879.0, 456.0, 831.0,-344.0,-880.0,-259.0,-92.0, 181.0,-275.0;"
				+ "-523.0,-304.0,-167.0,-526.0, 404.0,-937.0,-607.0,-132.0, 755.0,-232.0;"
				+ " 137.0, 273.0, 845.0,-765.0,-907.0,-412.0, 138.0, 197.0,-86.0, 325.0;"
				+ " 643.0, 720.0,-183.0,-80.0,-328.0, 687.0, 677.0, 456.0, 106.0,-946.0"), 1e-3);

		checkRescued("12x12 real integer matrix (base ~200), threw before the fallback",
			new MatrixComplex(" 11.0,-2.0, 123.0,-105.0,-192.0,-176.0,-77.0,-47.0, 90.0,-124.0, 24.0, 149.0;"
				+ "-75.0, 18.0,-191.0, 191.0, 194.0,-93.0, 118.0,-147.0,-17.0,-143.0, 168.0, 67.0;"
				+ " 42.0,-11.0, 54.0,-191.0, 166.0,-166.0, 73.0, 15.0, 115.0,-61.0, 165.0, 179.0;"
				+ " 140.0,-194.0,-71.0, 105.0,-81.0,-141.0, 116.0, 70.0, 82.0, 89.0, 101.0,-11.0;"
				+ "-83.0, 155.0, 2.0,-183.0, 167.0,-185.0,-197.0,-138.0,-72.0, 154.0,-88.0,-153.0;"
				+ "-167.0, 47.0, 56.0, 197.0,-189.0,-125.0,-9.0,-129.0, 49.0,-91.0, 62.0, 38.0;"
				+ " 195.0,-28.0,-13.0, 25.0,-42.0,-44.0, 152.0,-119.0,-161.0, 86.0, 59.0,-28.0;"
				+ " 138.0, 61.0, 140.0, 37.0,-193.0, 127.0, 159.0, 140.0, 57.0,-87.0, 88.0,-106.0;"
				+ " 23.0, 167.0,-177.0,-191.0,-48.0, 75.0,-124.0, 180.0,-33.0,-58.0,-72.0, 79.0;"
				+ "-151.0, 44.0,-111.0, 132.0, 148.0, 170.0,-195.0,-83.0,-105.0,-1.0,-174.0, 180.0;"
				+ " 161.0, 134.0, 35.0, 7.0, 104.0, 97.0, 64.0,-65.0, 120.0, 28.0, 12.0,-183.0;"
				+ " 7.0, 48.0,-181.0, 75.0,-158.0,-21.0, 124.0, 127.0, 195.0,-198.0,-47.0, 126.0"), 1e-2);

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}

}
