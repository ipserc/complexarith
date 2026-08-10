package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Confirma con rank() de referencia (independiente de rankNearSingular()) el cambio observado en
 * TestEigenV12 tras el arreglo de rankByRelativePivot(): para el bloque de Jordan clasico
 * [7,-3,1; 0,7,-3; 0,0,7] (autovalor 7, arith mult 3), geom mult debe ser 1 (un unico bloque de
 * Jordan de tamano 3), no 3 como reportaba el codigo previo al arreglo.
 */
public class ScratchGeomMultBug03 {
	public static void main(String[] args) {
		Complex.setFormatOFF();

		MatrixComplex AmL = new MatrixComplex("0,-3,1;0,0,-3;0,0,0"); // A - 7*I
		System.out.println("A - 7*I:");
		AmL.println("");
		System.out.println("rank() [referencia]: " + AmL.rank());
		System.out.println("rankNearSingular(): " + AmL.rankNearSingular());
		System.out.println("geom mult = 3 - rank = " + (3 - AmL.rank()));
	}
}
