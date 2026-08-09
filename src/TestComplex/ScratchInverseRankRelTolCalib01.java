package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Calibration script for a scale-relative singularity test to replace inverse()'s current
 * "determinant(m).equals(0,0)" absolute-epsilon precheck. Measures the ratio
 * smallestPivot/largestPivot (both magnitudes, from the SAME single-pass triangleUp() elimination
 * inverse()/determinant() already use) across 3 families of matrices at several magnitude scales:
 * (1) well-conditioned invertible -- establishes the "normal, definitely NOT singular" ratio floor.
 * (2) exactly rank-deficient by construction (row2 = 2*row1) -- establishes the "genuinely
 * singular" ratio ceiling.
 * (3) the REAL failing case from this session (Eigenspace char.eq. matrix for an imprecise
 * eigenvalue) -- where does it fall relative to (1) and (2)?
 * Same "measure, don't guess" methodology already used for ROOT_GROUPING_TOL_FACTOR (Polynom/
 * Eigenspace, Vigesima sesion, Claude/ComplexArithRev.md).
 */
public class ScratchInverseRankRelTolCalib01 {

	static double smallestOverLargestPivotRatio(MatrixComplex m) {
		MatrixComplex tri = m.triangleUp();
		double minPivot = Double.MAX_VALUE;
		double maxPivot = 0;
		for (int i = 0; i < tri.rows(); ++i) {
			double mod = tri.getItem(i, i).mod();
			if (mod < minPivot) minPivot = mod;
			if (mod > maxPivot) maxPivot = mod;
		}
		return minPivot / maxPivot;
	}

	static void report(String label, MatrixComplex m) {
		double ratio = smallestOverLargestPivotRatio(m);
		Complex det = m.determinant();
		System.out.println(label + "  ratio(min/max pivot)=" + ratio + "  |det|=" + det.mod());
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();

		System.out.println("=== (1) WELL-CONDITIONED INVERTIBLE, several scales ===");
		double[] scales = {1, 8, 30, 200};
		for (double scale : scales) {
			MatrixComplex m = new MatrixComplex(5);
			m.initMatrixRandomRec((int) scale);
			report("scale=" + scale, m);
		}

		System.out.println("\n=== (2) EXACTLY RANK-DEFICIENT (row2 = 2*row1), several scales ===");
		for (double scale : scales) {
			MatrixComplex m = new MatrixComplex(5);
			m.initMatrixRandomRec((int) scale);
			MatrixComplex row0 = m.getRow(0);
			m.setRow(1, row0.times(2.0));
			report("scale=" + scale, m);
		}

		System.out.println("\n=== (3) REAL FAILING CASE (this session's eigen char.eq. matrix) ===");
		MatrixComplex aMatrix = new MatrixComplex(
				"+5,+5,+3,+4,+4,+2,+1;" +
				"+3,-3,+4,-5,+2,+3,+2;" +
				"+3,+4,+2,+3,-2,+1,-4;" +
				"-4,+2,-5,-2,+2,-5,-2;" +
				"+1,+5,-5,-1,+5,-3,-3;" +
				"+1,+4,-4,+3,+4,-2,-5;" +
				"-4,+1,+1,+1,+2,+2,+2");
		Eigenspace eigenSpace = new Eigenspace(new Complex(1, 0), aMatrix);
		for (int i = 0; i < eigenSpace.eigenvalues().rows(); ++i) {
			Complex eVal = eigenSpace.eigenvalues().getItem(i, 0);
			MatrixComplex cMatrix = aMatrix.minus(MatrixComplex.eye(aMatrix.rows()).times(eVal));
			int geom = eigenSpace.geometricMultiplicity(eVal);
			report("eigval=" + eVal + " geomMult(OLD)=" + geom, cMatrix);
		}

		System.out.println("\n=== (4) NEAR-SINGULAR BY CONSTRUCTION (genuine A, slightly perturbed lambda), several scales ===");
		// Build A = P * D * P^-1 with a KNOWN eigenvalue, then perturb that eigenvalue by a tiny
		// relative amount before forming (A - lambda*I) -- mimics what an imprecise eigenvalue
		// solver produces, but with a KNOWN, controlled perturbation instead of whatever
		// QRSchurfactor happens to produce.
		for (double scale : scales) {
			MatrixComplex P = new MatrixComplex(5);
			P.initMatrixRandomRec((int) scale);
			// Make sure P is invertible-ish: not checked here, if this particular draw is singular
			// the determinant print below will make it obvious.
			MatrixComplex D = new MatrixComplex(5);
			D.initMatrixDiag(1, 0);
			D.setItem(0, 0, new Complex(scale * 3.0, 0));
			D.setItem(1, 1, new Complex(scale * 1.5, 0));
			D.setItem(2, 2, new Complex(scale * 0.7, 0));
			D.setItem(3, 3, new Complex(scale * 2.2, 0));
			D.setItem(4, 4, new Complex(scale * 0.9, 0));
			MatrixComplex A = P.times(D).times(P.inverse());
			double trueLambda = scale * 3.0;
			double perturbedLambda = trueLambda * (1 + 1e-12); // ~1e-12 RELATIVE error, like a real eigensolver residual
			MatrixComplex cMatrix = A.minus(MatrixComplex.eye(5).times(new Complex(perturbedLambda, 0)));
			report("scale=" + scale + " trueLambda=" + trueLambda + " perturbedLambda=" + perturbedLambda, cMatrix);
		}

		System.out.println("\n=== (5) GENUINELY INVERTIBLE BUT ILL-CONDITIONED (Hilbert matrix), several orders ===");
		// Hilbert matrix H(i,j) = 1/(i+j+1) -- famous for being extremely ill-conditioned yet
		// mathematically nonsingular for any finite order. Worst case for a relative-ratio test:
		// if a legitimate (if poorly conditioned) invertible matrix like this falls BELOW the
		// chosen threshold, the fix would wrongly refuse to invert it.
		for (int order : new int[] {4, 6, 8}) {
			MatrixComplex h = new MatrixComplex(order, order);
			for (int i = 0; i < order; ++i)
				for (int j = 0; j < order; ++j)
					h.setItem(i, j, new Complex(1.0 / (i + j + 1), 0));
			report("Hilbert order=" + order, h);
		}
	}
}
