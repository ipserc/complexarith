package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.vectorcomplex.VectorComplex;

/**
 * Diagnostic driver: Schurfactor.factorize() swallows the real exception (broad try/catch,
 * generic message) -- this replicates its private Schur() recursion step by step, letting any
 * exception propagate with its real stack trace, to find out WHY a given matrix fails.
 */
public class ScratchSchurfactorDiag01 {
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

		System.out.println("aMat:");
		nearIdentity.println("aMat");

		Eigenspace eig = new Eigenspace(nearIdentity);
		System.out.println("eigenvalues:");
		eig.eigenvalues().println("eigenvalues");
		System.out.println("eigenvector(0):");
		eig.eigenvector(0).println("eigenvector(0)");
		System.out.println("eigenvector(0).norm()=" + eig.eigenvector(0).norm());

		VectorComplex eigenVector = new VectorComplex(eig.eigenvector(0));
		MatrixComplex base = eigenVector.base();
		System.out.println("base:");
		base.println("base");

		MatrixComplex baseOrtn = base.orthonormalize();
		System.out.println("orthonormalize():");
		baseOrtn.println("orthonormalize()");

		MatrixComplex baseW1ortn = baseOrtn.transpose();
		System.out.println("baseW1ortn (transpose):");
		baseW1ortn.println("baseW1ortn");
		System.out.println("baseW1ortn.determinant()=" + baseW1ortn.determinant());

		MatrixComplex inv = baseW1ortn.inverse();
		System.out.println("baseW1ortn.inverse() OK:");
		inv.println("inverse");

		MatrixComplex schur = inv.times(nearIdentity).times(baseW1ortn);
		System.out.println("schur (1st iteration):");
		schur.println("schur");
	}
}
