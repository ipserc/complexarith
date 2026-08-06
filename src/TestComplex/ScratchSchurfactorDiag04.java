package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.factorization.QRSchurfactor;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Diagnostic: compares raw QRSchurfactor eigenvalues computed two ways for the SAME numeric
 * matrix, back to back in the same process -- (1) direct "new Eigenspace(nearIdentity)" (which
 * failed in ScratchSchurfactorDiag01/02.java) vs (2) via "new Diagfactor(nearIdentity)" (which
 * SUCCEEDED when called from power(2) in ScratchSchurfactorDiag03.java). If they differ, the
 * clone chain or object identity matters; if they match, the failure is path-independent and
 * something else (e.g. Complex.digits()/other static state) differs between the two drivers.
 */
public class ScratchSchurfactorDiag04 {
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

		System.out.println("--- Path 1: direct Eigenspace(nearIdentity) ---");
		System.out.println("cond()=" + nearIdentity.cond() + " bestNumDecs()=" + nearIdentity.bestNumDecs());
		MatrixComplex rawRoots1 = new QRSchurfactor(nearIdentity).getEigenvalues();
		rawRoots1.println("rawRoots (path 1, direct)");
		Eigenspace eig1 = new Eigenspace(nearIdentity);
		eig1.eigenvalues().println("eigenvalues (path 1, direct, after grouping)");

		System.out.println("--- Path 2: via Diagfactor(nearIdentity) (mirrors power(2)) ---");
		Diagfactor dmat = new Diagfactor(nearIdentity);
		System.out.println("dmat.isDiagonalizable()=" + dmat.isDiagonalizable());
		System.out.println("dmat.D():");
		dmat.D().println("D");

		System.out.println("--- Path 3: same as path 1, run AGAIN right after path 2 ---");
		MatrixComplex rawRoots3 = new QRSchurfactor(nearIdentity).getEigenvalues();
		rawRoots3.println("rawRoots (path 3, direct, 2nd time)");
	}
}
