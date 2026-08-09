package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.complex.Complex;

/**
 * Investigacion (9 agosto 2026): por que Eigenspace.geometricMultiplicity() da 0 (en vez de 1)
 * para los autovalores 2, 1 y -1 de TestEigenV21's aMatrix cuando Complex.exact(false)
 * (modo APPROXIMATED), mientras que exact(true) (EXACT) y el propio calculo del autovector
 * (eigenvectors3()/solution()) funcionan bien en ambos modos. Hipotesis: QRSchurfactor.factorize()
 * deflaciona (h.getItem(hi,hi-1).isZero(), linea ~188) usando el ZERO_THRESHOLD dependiente de modo
 * -- en APPROX (~3.16e-7*10) para antes que en EXACT (~1e-12*10), dejando un autovalor calculado
 * con mas error residual, que luego rankNearSingular() (umbral FIJO, SINGULARITY_REL_TOL=1e-9,
 * independiente de modo) ya no considera "casi singular".
 */
public class ScratchApproxGeomMultDiag01 {
	public static void main(String[] args) {
		MatrixComplex aMatrix = new MatrixComplex(""
				+ "+2.0,-3.0,-2.0,+3.0,-1.0;"
				+ "+1.0,+2.0,+1.0,-1.0,+1.0;"
				+ "-1.0,+1.0,+1.0,+1.0,-1.0;"
				+ "+1.0,-3.0,-1.0,+3.0,+1.0;"
				+ "-2.0,+2.0,+1.0,-2.0,+1.0");

		for (boolean exact : new boolean[]{true, false}) {
			Complex.exact(exact);
			System.out.println("=== Complex.exact(" + exact + ") ===");
			Eigenspace eig = new Eigenspace(Complex.ONE, aMatrix);
			for (int i = 0; i < eig.eigenvalues().rows(); ++i) {
				Complex lambda = eig.eigenvalues().getItem(i, 0);
				System.out.println(String.format("lambda[%d] = %.17g %+.17gi", i, lambda.rep(), lambda.imp()));
				MatrixComplex diff = aMatrix.minus(MatrixComplex.eye(aMatrix.rows()).times(lambda));
				MatrixComplex tri = diff.triangleUp();
				double maxPivot = 0, minPivot = Double.MAX_VALUE;
				for (int k = 0; k < tri.rows(); ++k) {
					double p = tri.getItem(k, k).mod();
					if (p > maxPivot) maxPivot = p;
					if (p < minPivot) minPivot = p;
				}
				System.out.println("  pivots min=" + minPivot + " max=" + maxPivot + " ratio=" + (minPivot / maxPivot)
						+ "  (SINGULARITY_REL_TOL=1e-9)  rankNearSingular=" + diff.rankNearSingular()
						+ "  geomMult=" + eig.geometricMultiplicity(lambda));
			}
			System.out.println();
		}
	}
}
