package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.factorization.Hessenbergfactor;
import com.ipserc.arith.factorization.QRSchurfactor;
import com.ipserc.arith.factorization.SVDfactor;
import com.ipserc.arith.factorization.SVDfactor.SVDmethod;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression driver for the constructor-aliasing sweep across com.ipserc.arith.factorization
 * (Decimoctava sesion, continuacion, ver Claude/ComplexArithRev.md). Same bug class already found
 * and fixed in Schurfactor/LUfactor/QRfactor this session: "matrix.complexMatrix.clone()" is a
 * shallow Java array clone (shares row arrays with the caller's matrix), not the project's
 * established deep-copy idiom (matrix.copy()). Confirmed here in 4 more classes
 * (Diagfactor, Hessenbergfactor, QRSchurfactor, SVDfactor x2 constructors), all fixed the same way.
 */
public class ScratchFactorizationAliasSweep01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, MatrixComplex original, Complex before) {
		Complex after = original.getItem(0, 0);
		boolean ok = after.equals(before.rep(), before.imp());
		System.out.println((ok ? "OK   " : "FAIL ") + label + ": original[0][0] before=" + before + " after=" + after);
		if (ok) ++pass; else ++fail;
	}

	public static void main(String[] args) {
		MatrixComplex m;
		Complex before;

		m = new MatrixComplex("1,2;3,4");
		before = m.getItem(0, 0).copy();
		new Diagfactor(m).setItem(0, 0, new Complex(999, 0));
		check("Diagfactor(MatrixComplex)", m, before);

		m = new MatrixComplex("1,2;3,4");
		before = m.getItem(0, 0).copy();
		new Hessenbergfactor(m).setItem(0, 0, new Complex(999, 0));
		check("Hessenbergfactor(MatrixComplex)", m, before);

		m = new MatrixComplex("1,2;3,4");
		before = m.getItem(0, 0).copy();
		new QRSchurfactor(m).setItem(0, 0, new Complex(999, 0));
		check("QRSchurfactor(MatrixComplex)", m, before);

		m = new MatrixComplex("1,2;3,4");
		before = m.getItem(0, 0).copy();
		new SVDfactor(m).setItem(0, 0, new Complex(999, 0));
		check("SVDfactor(MatrixComplex)", m, before);

		m = new MatrixComplex("1,2;3,4");
		before = m.getItem(0, 0).copy();
		new SVDfactor(m, SVDmethod.SVD).setItem(0, 0, new Complex(999, 0));
		check("SVDfactor(MatrixComplex,SVDmethod)", m, before);

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
