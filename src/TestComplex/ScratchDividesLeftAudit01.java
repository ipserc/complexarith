package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ScratchDividesLeftAudit01 {

	static int pass = 0, fail = 0;

	static void check(String label, boolean ok, String detail) {
		if (ok) { pass++; System.out.println("OK   " + label); }
		else { fail++; System.out.println("FAIL " + label + " -- " + detail); }
	}

	static boolean closeTo(MatrixComplex a, MatrixComplex b, double tol) {
		if (a.rows() != b.rows() || a.cols() != b.cols()) return false;
		for (int r = 0; r < a.rows(); ++r)
			for (int c = 0; c < a.cols(); ++c)
				if (a.getItem(r, c).minus(b.getItem(r, c)).mod() > tol) return false;
		return true;
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();

		// 1) 1x1 dividesleft: a\b == b/a
		MatrixComplex a1 = new MatrixComplex("6");
		MatrixComplex b1 = new MatrixComplex("-15");
		MatrixComplex got = a1.dividesleft(b1);
		check("1x1 dividesleft: 6\\(-15) == -2.5", Math.abs(got.getItem(0,0).rep() - (-2.5)) < 1e-12,
			"got=" + got.getItem(0,0));

		// 2) 1x1 dividesleft with a complex cMatrix (bigger than 1x1) -- scalar broadcast
		MatrixComplex aScalar = new MatrixComplex("2");
		MatrixComplex cMat = new MatrixComplex("2,4;6,8");
		MatrixComplex expected = cMat.divides(new Complex(2)); // cMatrix/2, since a^-1=1/2
		MatrixComplex gotBroadcast = aScalar.dividesleft(cMat);
		check("1x1 dividesleft broadcast: 2\\[[2,4],[6,8]] == matrix/2",
			closeTo(gotBroadcast, expected, 1e-12), "got=" + gotBroadcast + " expected=" + expected);

		// 3) NxN dividesleft with a 1x1 cMatrix -- this^-1 * scalar
		MatrixComplex thisMat = new MatrixComplex("2,0;0,4"); // inverse = [[0.5,0],[0,0.25]]
		MatrixComplex bScalar = new MatrixComplex("10");
		MatrixComplex expected2 = thisMat.inverse().times(new Complex(10));
		MatrixComplex got2 = thisMat.dividesleft(bScalar);
		check("NxN dividesleft with 1x1 cMatrix: this^-1 * b", closeTo(got2, expected2, 1e-12),
			"got=" + got2 + " expected=" + expected2);

		// 4) general NxN dividesleft still matches this.inverse().times(cMatrix) (untouched path)
		MatrixComplex A = new MatrixComplex("2,1;1,3");
		MatrixComplex B = new MatrixComplex("5;10");
		MatrixComplex expected3 = A.inverse().times(B);
		MatrixComplex got3 = A.dividesleft(B);
		check("NxN dividesleft general case unchanged", closeTo(got3, expected3, 1e-12),
			"got=" + got3 + " expected=" + expected3);
		// and matches the hand-known solution of 2x+y=5, x+3y=10 -> x=1,y=3
		check("NxN dividesleft general case == hand solution [1;3]",
			Math.abs(got3.getItem(0,0).rep()-1) < 1e-12 && Math.abs(got3.getItem(1,0).rep()-3) < 1e-12,
			"got=" + got3);

		// 5) dividesright untouched -- sanity check against its own definition this*cMatrix^-1
		MatrixComplex right = A.dividesright(new MatrixComplex("1,0;0,1"));
		check("dividesright unchanged (A / I == A)", closeTo(right, A, 1e-12), "got=" + right);

		// 6) consistency: this.dividesleft(cMatrix) == this.inverse().times(cMatrix) for a random-ish 3x3
		MatrixComplex A3 = new MatrixComplex("4,2,1;1,5,2;0,1,3");
		MatrixComplex B3 = new MatrixComplex("1;2;3");
		check("3x3 dividesleft == inverse().times()", closeTo(A3.dividesleft(B3), A3.inverse().times(B3), 1e-9),
			"got=" + A3.dividesleft(B3));

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
	}
}
