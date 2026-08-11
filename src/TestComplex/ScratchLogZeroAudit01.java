package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Verifica que logTaylor()/logMercator()/logHat()/logm() fallan alto con IllegalArgumentException
 * para la matriz cero (log(0) es una singularidad genuina, ver Claude/ComplexArithRev.md),
 * en vez de devolver la matriz cero en silencio, y que isNaN()/isInfinite() se siguen propagando.
 */
public class ScratchLogZeroAudit01 {
	static int ok = 0, total = 0;

	static void check(String name, boolean cond) {
		total++;
		if (cond) { ok++; System.out.println("OK   - " + name); }
		else System.out.println("FAIL - " + name);
	}

	static boolean throwsIAE(Runnable r) {
		try { r.run(); return false; }
		catch (IllegalArgumentException e) { return true; }
		catch (Exception e) { return false; }
	}

	public static void main(String[] args) {
		MatrixComplex zero = new MatrixComplex(3);

		check("logTaylor(0) throws IllegalArgumentException", throwsIAE(() -> zero.logTaylor()));
		check("logMercator(0) throws IllegalArgumentException", throwsIAE(() -> zero.logMercator()));
		check("logHat(0) throws IllegalArgumentException", throwsIAE(() -> zero.logHat()));
		check("logm(0) throws IllegalArgumentException", throwsIAE(() -> zero.logm()));

		MatrixComplex nanMat = new MatrixComplex(2);
		nanMat.setItem(0, 0, new Complex(Double.NaN, 0));
		check("logTaylor(NaN) still propagates NaN (no exception)", !throwsIAE(() -> {
			MatrixComplex r = nanMat.logTaylor();
			if (!r.isNaN()) throw new RuntimeException("expected NaN passthrough");
		}));

		MatrixComplex infMat = new MatrixComplex(2);
		infMat.setItem(0, 0, new Complex(Double.POSITIVE_INFINITY, 0));
		check("logMercator(Infinite) still propagates Infinite (no exception)", !throwsIAE(() -> {
			MatrixComplex r = infMat.logMercator();
			if (!r.isInfinite()) throw new RuntimeException("expected Infinite passthrough");
		}));

		System.out.println(ok + "/" + total + " OK");
	}
}
