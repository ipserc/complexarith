package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Follow-up to ScratchRankDocumentedCases01.java: cross-checks rank1()/rank() against rank0()
 * (brute-force via minors, unaffected by this session's fix) for the 3 documented cases, to find
 * out whether rank1()==3 after the fix is a REGRESSION or was already the objectively correct
 * answer (with the Javadoc's "rank1=4" being the OLD, pre-existing bug it was documenting).
 */
public class ScratchRankDocumentedCases02 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		MatrixComplex m2853 = new MatrixComplex("1.00,-1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,1.00,-1.00,1.00;-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,-1.00");
		MatrixComplex m3648 = new MatrixComplex("1.00,1.00,-1.00,1.00,1.00,1.00;1.00,1.00,-1.00,1.00,1.00,1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;-1.00,-1.00,1.00,-1.00,-1.00,-1.00;-1.00,-1.00,1.00,-1.00,-1.00,1.00");
		MatrixComplex m7425 = new MatrixComplex("-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,1.00,-1.00,-1.00;1.00,-1.00,1.00,1.00,-1.00,-1.00");

		System.out.println("TEST #2853: rank0=" + m2853.rank0() + " rank1=" + m2853.rank1() + " rank2=" + m2853.rank2());
		System.out.println("TEST #3648: rank0=" + m3648.rank0() + " rank1=" + m3648.rank1() + " rank2=" + m3648.rank2());
		System.out.println("TEST #7425: rank0=" + m7425.rank0() + " rank1=" + m7425.rank1() + " rank2=" + m7425.rank2());
	}
}
