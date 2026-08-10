package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Investigacion dedicada del hallazgo colateral de la Vigesimoctava sesion (ver
 * Claude/ComplexArithRev.md): MatrixComplexFunctions.exp() tenia una guarda temprana que devolvia
 * la matriz CERO sin tocar para {@code m.isNull()} -- correcto para NaN/Infinito, incorrecto para
 * el cero (exp(0)=IDENTIDAD). Alcanzable en la practica via MatrixComplex.power(double 0.0) para
 * cualquier matriz no diagonalizable (M^0 caia en exp(M.log()*0)=exp(0)). Arreglado quitando
 * isNull() de la guarda.
 */
public class ScratchMatrixExpZeroAudit01 {

	static int ok = 0;
	static int fail = 0;

	static void check(String label, boolean pass) {
		if (pass) ok++; else fail++;
		System.out.printf("%-70s %s%n", label, pass ? "OK" : "***FAIL***");
	}

	static boolean isIdentity(MatrixComplex m, double tol) {
		for (int row = 0; row < m.rows(); ++row)
			for (int col = 0; col < m.cols(); ++col) {
				Complex expected = (row == col) ? Complex.ONE : Complex.ZERO;
				if (m.getItem(row, col).minus(expected).mod() > tol) return false;
			}
		return true;
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(10);

		System.out.println("=== (1) exp(matriz cero explicita) == identidad ===");
		MatrixComplex zero3 = new MatrixComplex(3, 3);
		check("exp(zero 3x3) == I", isIdentity(MatrixComplex.exp(zero3), 1e-12));
		MatrixComplex zero1 = new MatrixComplex(1, 1);
		check("exp(zero 1x1) == I", isIdentity(MatrixComplex.exp(zero1), 1e-12));

		System.out.println("\n=== (2) M^0 == identidad, para M no diagonalizable (el caso reportado) ===");
		MatrixComplex m = new MatrixComplex(3);
		m.initMatrixDiag(2, 0);
		m.setItem(0, 1, new Complex(0.3, 0));
		check("m no es diagonalizable (confirma que se ejercita la ruta exp(log*0))", !new com.ipserc.arith.factorization.Diagfactor(m).isDiagonalizable());
		check("m.power(0.0) == I (antes daba la matriz cero)", isIdentity(m.power(0.0), 1e-9));
		check("m.power_(new Complex(0,0)) == I (antes daba la matriz cero)", isIdentity(m.power_(new Complex(0, 0)), 1e-9));

		System.out.println("\n=== (3) M^0 == identidad tambien para M singular (rango deficiente) ===");
		MatrixComplex ms = new MatrixComplex(2);
		ms.setItem(0, 0, new Complex(1, 0));
		ms.setItem(0, 1, new Complex(2, 0));
		ms.setItem(1, 0, new Complex(2, 0));
		ms.setItem(1, 1, new Complex(4, 0));
		check("ms.power_(0) == I", isIdentity(ms.power_(new Complex(0, 0)), 1e-9));

		System.out.println("\n=== (4) Sin regresion: exp() de una matriz NO nula sigue igual ===");
		MatrixComplex diag2 = new MatrixComplex(2);
		diag2.initMatrixDiag(1, 0);
		MatrixComplex expDiag = MatrixComplex.exp(diag2);
		Complex expected = Complex.exp(new Complex(1, 0));
		check("exp(diag(1,1)) diagonal == exp(1) (Complex.exp de referencia)", expDiag.getItem(0, 0).minus(expected).mod() < 1e-9 && expDiag.getItem(1, 1).minus(expected).mod() < 1e-9);

		System.out.println("\n=== (5) Sin regresion: exp() de NaN/Infinito sigue propagandose sin tocar ===");
		MatrixComplex nanM = new MatrixComplex(2);
		nanM.setItem(0, 0, new Complex(Double.NaN, 0));
		check("exp(matriz con NaN) sigue teniendo NaN (guarda NaN intacta)", MatrixComplex.exp(nanM).isNaN());

		System.out.println("\n" + ok + " OK, " + fail + " FAIL");
	}
}
