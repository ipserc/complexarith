package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Spline;

public class ScratchSplineDebug04 {
	public static void main(String[] args) {
		Complex.setFormatOFF();
		try {
			MatrixComplex pTable = new MatrixComplex("0,1; 2,5");
			Spline spline = new Spline(3, pTable);
			spline.interpolate(false);
			System.out.println(spline.eval(new Complex(1.0)));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
