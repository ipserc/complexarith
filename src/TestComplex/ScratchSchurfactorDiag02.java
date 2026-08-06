package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.QRSchurfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Diagnostic driver: traces the eigenvalue root-finding + grouping pipeline
 * (Eigenspace.eigenval()) step by step for the matrix that breaks Schurfactor, to find whether
 * the raw roots are already collapsed (a QRSchurfactor precision issue) or whether they are
 * correct-and-distinct but the DISTANCE-based grouping tolerance merges them incorrectly
 * (an Eigenspace.groupingTolerance()/bestNumDecs() issue).
 */
public class ScratchSchurfactorDiag02 {
	public static void main(String[] args) {
		Complex.digits(30L);

		MatrixComplex nearIdentity = new MatrixComplex(3, 3);
		nearIdentity.setItem(0, 0, new Complex(1.1, 0.02));
		nearIdentity.setItem(0, 1, new Complex(0.05, -0.01));
		nearIdentity.setItem(0, 2, new Complex(-0.02, 0.01));
		nearIdentity.setItem(1, 0, new Complex(0.01, 0.0));
		nearIdentity.setItem(1, 1, new Complex(0.95, 0.03));
		nearIdentity.setItem(1, 2, new Complex(0.02, -0.02));
		nearIdentity.setItem(2, 0, new Complex(-0.01, 0.02));
		nearIdentity.setItem(2, 1, new Complex(0.03, 0.0));
		nearIdentity.setItem(2, 2, new Complex(1.05, -0.01));

		System.out.println("bestNumDecs()=" + nearIdentity.bestNumDecs());

		MatrixComplex rawRoots = new QRSchurfactor(nearIdentity).getEigenvalues();
		System.out.println("raw QRSchurfactor roots (before grouping):");
		rawRoots.println("rawRoots");

		int digits = nearIdentity.bestNumDecs();
		double tol = 0.5 * Math.pow(10, -digits);
		System.out.println("grouping tolerance (0.5 * 10^-" + digits + ") = " + tol);

		for (int i = 1; i < rawRoots.rows(); ++i) {
			Complex a = rawRoots.getItem(i - 1, 0);
			Complex b = rawRoots.getItem(i, 0);
			double dist = a.minus(b).mod();
			System.out.println("|root[" + (i - 1) + "]-root[" + i + "]| = " + dist + (dist <= tol ? "  <= tol -> GROUPED" : "  > tol -> separate"));
		}
	}
}
