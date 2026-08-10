package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Diagnostica la REGRESION introducida por el primer intento de arreglo de rankByRelativePivot()
 * (escanear la fila entera en vez de solo la diagonal): para A-1*I de TestEigenV05
 * ([3,2,-1;2,3,1;0,0,5], autovalor simple lambda=1), geom mult paso de 1 (correcto) a 0
 * (matematicamente imposible: todo autovalor genuino tiene geom mult>=1).
 */
public class ScratchGeomMultBug02 {
	public static void main(String[] args) {
		Complex.setFormatOFF();

		MatrixComplex AmI = new MatrixComplex("2,2,-1;2,2,1;0,0,4");
		System.out.println("A - 1*I:");
		AmI.println("");

		MatrixComplex tri = AmI.triangleUp();
		System.out.println("triangleUp(A-1*I):");
		tri.println("");

		System.out.println("rank() [referencia]: " + AmI.rank());
		System.out.println("rankNearSingular(): " + AmI.rankNearSingular());

		System.out.println("\nPor fila, modulo maximo de toda la fila tras triangleUp():");
		for (int i = 0; i < tri.rows(); ++i) {
			double rm = 0;
			for (int j = 0; j < tri.cols(); ++j) {
				double mod = tri.getItem(i, j).mod();
				if (mod > rm) rm = mod;
				System.out.printf("  tri[%d][%d] = %s (mod=%.6e)%n", i, j, tri.getItem(i,j).toString(), mod);
			}
			System.out.printf(" -> rowMax[%d] = %.6e%n", i, rm);
		}
	}
}
