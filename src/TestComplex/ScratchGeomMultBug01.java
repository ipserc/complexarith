package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Reproduce y diagnostica el reporte del usuario: para la matriz
 * [i,-i,i;i,-i,-i;i,-i,i], Eigenspace imprime "EigenValue: 0.000 - arith mult:1 - geom mult:2",
 * lo cual viola geom_mult <= arith_mult (teorema fundamental de algebra lineal). Investiga si
 * rankNearSingular()/geometricMultiplicity() estan calculando mal el rango de A-0*I=A.
 */
public class ScratchGeomMultBug01 {
	public static void main(String[] args) {
		Complex.setFormatOFF();

		MatrixComplex A = new MatrixComplex("i,-i,i;i,-i,-i;i,-i,i");
		System.out.println("A:");
		A.println("");

		System.out.println("A.rank() [algoritmo general, referencia]: " + A.rank());
		System.out.println("A.rankNearSingular() [usado por geometricMultiplicity()]: " + A.rankNearSingular());
		System.out.println("A.rows() - A.rank() = nulidad esperada: " + (A.rows() - A.rank()));
		System.out.println("A.rows() - A.rankNearSingular() = geom mult calculada: " + (A.rows() - A.rankNearSingular()));

		System.out.println("\ntriangleUp(A):");
		A.triangleUp().println("");

		Eigenspace E = new Eigenspace(A);
		E.eigenval();
		System.out.println("\ngeometricMultiplicity(0) reportado por Eigenspace: " + E.geometricMultiplicity(new Complex(0,0)));

		System.out.println("\n--- Comprobacion independiente: dimension real del nucleo de A ---");
		// A*x=0 resuelto a mano: col0=[i,i,i], col1=[-i,-i,-i]=-1*col0 -> col0,col1 dependientes.
		// col2=[i,-i,i] (=col0 en filas 0,2 pero -i en fila1) -> comprobar si col2 es combinacion de col0.
		// Verificamos con vectores candidatos del nucleo.
		MatrixComplex x1 = new MatrixComplex("1;1;0"); // col0+col1=0 => x=(1,1,0) deberia anular A
		MatrixComplex Ax1 = A.times(x1);
		System.out.println("A*(1,1,0)^T (deberia ser 0 si (1,1,0) esta en el nucleo):");
		Ax1.println("");

		System.out.println("\nTOTAL: geom_mult debe ser <= arith_mult siempre (teorema fundamental). Si esta implementacion da geom_mult=2 y arith_mult=1, es una violacion matematica.");
	}
}
