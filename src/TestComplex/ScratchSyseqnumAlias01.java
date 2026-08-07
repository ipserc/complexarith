package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.syseq.Syseq;
import com.ipserc.arith.syseq.Syseqnum;

public class ScratchSyseqnumAlias01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		try {
			MatrixComplex m = new MatrixComplex("1,2; 3,4");
			Syseqnum s = new Syseqnum(m);
			s.setItem(0, 0, new Complex(99, 0));
			System.out.println("Syseqnum(MatrixComplex) aliasing test - original m should stay 1,2;3,4 :");
			m.println("m after mutating s");

			MatrixComplex m2 = new MatrixComplex("1,2; 3,4");
			Syseqnum s2 = new Syseqnum(m2);
			Syseqnum s2clone = s2.clone();
			s2clone.setItem(0, 0, new Complex(77, 0));
			System.out.println("Syseqnum.clone() aliasing test - original s2 should stay 1,2;3,4 :");
			s2.println("s2 after mutating s2clone");

			MatrixComplex m3 = new MatrixComplex("1,2; 3,4");
			Syseq q = new Syseq(m3);
			q.setItem(0, 0, new Complex(55, 0));
			System.out.println("Syseq(MatrixComplex) aliasing test - original m3 should stay 1,2;3,4 :");
			m3.println("m3 after mutating q");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
