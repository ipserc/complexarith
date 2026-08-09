package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Verifica que cablear timesEqRaw() en los 7 metodos Taylor/Mercator (Vigesimotercera sesion,
 * Vector API/Camino A, ver Claude/ComplexArithRev.md) no cambia el resultado -- comparado a mano
 * contra la salida de un build de referencia desde HEAD (antes de este cambio).
 */
public class ScratchTimesEqRawWiringVerify01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();

		MatrixComplex general = new MatrixComplex(3, 3);
		MatrixComplex nearIdentity = new MatrixComplex(3, 3);
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 3; c++) {
				double re = 0.22 * Math.sin(1.3 * r + 0.7 * c + 1);
				double im = 0.22 * Math.cos(0.9 * r - 1.1 * c + 0.5);
				general.setItem(r, c, new Complex(re, im));
				double re2 = 0.03 * Math.sin(1.3 * r + 0.7 * c + 1);
				double im2 = 0.03 * Math.cos(0.9 * r - 1.1 * c + 0.5);
				if (r == c) re2 += 1.0;
				nearIdentity.setItem(r, c, new Complex(re2, im2));
			}
		MatrixComplex logmMat = new MatrixComplex("2,0,0;0,3,4;0,4,9");

		general.exp_().println("exp_:");
		general.sinTaylor().println("sinTaylor:");
		general.cosTaylor().println("cosTaylor:");
		general.sinhTaylor().println("sinhTaylor:");
		general.coshTaylor().println("coshTaylor:");
		nearIdentity.logTaylor().println("logTaylor:");
		nearIdentity.logMercator().println("logMercator:");
		nearIdentity.logHat().println("logHat:");
		logmMat.logm().println("logm:");
	}
}
