package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class ScratchLogmIsolationCheck02 {
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

		print("logTaylor", nearIdentity.logTaylor());
		print("logMercator", nearIdentity.logMercator());
		print("logHat", nearIdentity.logHat());
		print("logm", nearIdentity.logm());
	}

	static void print(String label, MatrixComplex m) {
		System.out.println("=== " + label + " ===");
		for (int r = 0; r < m.rows(); r++) {
			StringBuilder sb = new StringBuilder();
			for (int c = 0; c < m.cols(); c++) {
				Complex v = m.getItem(r, c);
				sb.append(String.format("%.15e%+.15ei ", v.rep(), v.imp()));
			}
			System.out.println(sb.toString());
		}
	}
}
