package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.QRfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Regression driver for the QRfactor.java audit fixes (Decimoctava sesion, continuacion, ver
 * Claude/ComplexArithRev.md, QRfactor.VERSION 1.3).
 */
public class ScratchQRfactorAudit01 {

	private static int pass = 0;
	private static int fail = 0;

	private static void check(String label, boolean ok, String detail) {
		System.out.println((ok ? "OK   " : "FAIL ") + label + (detail.isEmpty() ? "" : " (" + detail + ")"));
		if (ok) ++pass; else ++fail;
	}

	private static double maxAbsDiff(MatrixComplex a, MatrixComplex b) {
		double max = 0;
		for (int i = 0; i < a.rows(); ++i) {
			for (int j = 0; j < a.cols(); ++j) {
				max = Math.max(max, a.getItem(i, j).minus(b.getItem(i, j)).mod());
			}
		}
		return max;
	}

	public static void main(String[] args) {

		// --- Finding 1: constructor aliasing, fixed ---
		MatrixComplex original = new MatrixComplex("1,2;3,4");
		Complex before = original.getItem(0, 0).copy();
		QRfactor qr = new QRfactor(original);
		qr.setItem(0, 0, new Complex(999, 0));
		Complex after = original.getItem(0, 0);
		check("Finding 1: constructor no longer aliases the caller's matrix", after.equals(before.rep(), before.imp()), "");

		// --- Finding 2: single-row matrix no longer NPEs, and gives the trivial correct QR ---
		MatrixComplex singleRow = new MatrixComplex("1,2,3");
		QRfactor qr2 = new QRfactor(singleRow);
		try {
			qr2.qrHouseholder();
			MatrixComplex recon = qr2.Q().times(qr2.R());
			check("Finding 2: single-row matrix qrHouseholder() no longer throws, reconstructs A",
					qr2.factorized() && maxAbsDiff(recon, singleRow) < 1e-9, "diff=" + maxAbsDiff(recon, singleRow));
		} catch (Exception e) {
			check("Finding 2: single-row matrix qrHouseholder() no longer throws", false, "threw " + e);
		}

		// --- Finding 2b: 1x1 matrix, same class of edge case ---
		MatrixComplex oneByOne = new MatrixComplex("5");
		QRfactor qr3 = new QRfactor(oneByOne);
		try {
			qr3.qrHouseholder();
			MatrixComplex recon = qr3.Q().times(qr3.R());
			check("Finding 2b: 1x1 matrix qrHouseholder() no longer throws, reconstructs A",
					qr3.factorized() && maxAbsDiff(recon, oneByOne) < 1e-9, "diff=" + maxAbsDiff(recon, oneByOne));
		} catch (Exception e) {
			check("Finding 2b: 1x1 matrix qrHouseholder() no longer throws", false, "threw " + e);
		}

		// --- Regression guard: a normal multi-row matrix still factorizes correctly ---
		MatrixComplex normalMat = new MatrixComplex("1,2,3;4,5,6;7,8,10");
		QRfactor qr4 = new QRfactor(normalMat);
		qr4.qrHouseholder();
		MatrixComplex recon4 = qr4.Q().times(qr4.R());
		check("Regression: normal 3x3 matrix qrHouseholder() still reconstructs A",
				qr4.factorized() && maxAbsDiff(recon4, normalMat) < 1e-9, "diff=" + maxAbsDiff(recon4, normalMat));

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
		if (fail > 0) System.exit(1);
	}
}
