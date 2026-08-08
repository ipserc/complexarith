package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Confirms the 3 rank1-vs-rank2 disagreement cases documented in MatrixComplexRank.rank1()'s
 * Javadoc (TEST #2853/#3648/#7425) still produce rank1()==4 after the relative-tolerance fix to
 * rank11()/rank12()'s null-row test (8 agosto 2026, ver Claude/ComplexArithRev.md) -- these are
 * exact +-1 matrices (scale 1), so the old fixed ~1e-11 absolute epsilon and the new 1e-9 relative
 * one should behave identically here (no near-singular floating residue at that scale).
 */
public class ScratchRankDocumentedCases01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		MatrixComplex m2853 = new MatrixComplex("1.00,-1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,1.00,-1.00,1.00;-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,-1.00");
		MatrixComplex m3648 = new MatrixComplex("1.00,1.00,-1.00,1.00,1.00,1.00;1.00,1.00,-1.00,1.00,1.00,1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;-1.00,-1.00,1.00,-1.00,-1.00,-1.00;-1.00,-1.00,1.00,-1.00,-1.00,1.00");
		MatrixComplex m7425 = new MatrixComplex("-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,1.00,-1.00,-1.00;1.00,-1.00,1.00,1.00,-1.00,-1.00");

		System.out.println("TEST #2853: rank1=" + m2853.rank1() + " (expected 4), rank()=" + m2853.rank());
		System.out.println("TEST #3648: rank1=" + m3648.rank1() + " (expected 4), rank()=" + m3648.rank());
		System.out.println("TEST #7425: rank1=" + m7425.rank1() + " (expected 4), rank()=" + m7425.rank());
	}
}
