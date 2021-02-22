package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestSolve05 {

	private static void showResults(MatrixComplex fMatrix) {
		MatrixComplex dMatrix;
		MatrixComplex gMatrix;
		MatrixComplex hMatrix;
		int boxSize = 65;
		
		System.out.println(Complex.boxTitle(boxSize, "MATRIX SOLVE EQUATION"));   	
		fMatrix.println("fMatrix = Original Matrix");
		System.out.println(Complex.boxText(boxSize, "Some Matrix Operations"));   	
		gMatrix = fMatrix.triangleUp();
		gMatrix.println("Triangle");
		System.out.println("rank(gMatrix) = " + gMatrix.rank());		
		dMatrix = fMatrix.triangleLo();
		dMatrix.println("Triangle Lo");

		System.out.println(Complex.boxText(boxSize, "Equations Operations"));   	
		fMatrix.unkMatrix().println("Unknowns Matrix");
		int rank1 = fMatrix.unkMatrix().rank();
			System.out.println("rank(Unknowns Matrix) = " + rank1);
			fMatrix.unkMatrix().determinant().println("unkMatrix().det=");
		int nbrSolutions = fMatrix.nbrOfSolutions();
		int typeEqSys = fMatrix.typeEqSys();
		fMatrix.printTypeEqSys();	
		if (typeEqSys == MatrixComplex.DETERMINATE)
			System.out.println("Se devuelve 1 solución única.");
		else if (typeEqSys == MatrixComplex.INDETERMINATE) System.out.println("Se devuelven "+nbrSolutions+" soluciones LI.") ;
			else {
				System.out.println("Sistema sin solución.") ;
				return;
			}
		//System.out.println("	SOLVE GAUSS with λ " + seed.toString());
		System.out.println(Complex.boxText(boxSize, "System Equations Solutions"));   	
		hMatrix = fMatrix.solve();
		hMatrix.println("Soluciones (hMatrix)");
		for (int i = 0 ; i < hMatrix.rows(); ++i) {
			MatrixComplex solMatrix = new MatrixComplex(1,hMatrix.cols());
			for (int col = 0; col < hMatrix.cols(); ++col)
				solMatrix.setItem(0,col,hMatrix.getItem(i,col));
			solMatrix.println("Soluciones (solMatrix)");
			fMatrix.coefMatrix().times(solMatrix.transpose()).println("Proof check fMatrix.coefMatrix().times(hMatrix)");
		}
		if (fMatrix.typeEqSys() == MatrixComplex.DETERMINATE) {
			System.out.println("	SOLVE CRAMER");		
			hMatrix = fMatrix.solveCramer();
			hMatrix.println("Soluciones Cramer (hMatrix)");
			fMatrix.coefMatrix().times(hMatrix.transpose()).println("Proof check fMatrix.coefMatrix().times(hMatrix)");			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix, bMatrix;

		Complex.setFormatON();
		Complex.setFixedON(3);
		

		aMatrix = new MatrixComplex("2,-3,0,-9;0,4,0,16;1,0,1,2"); // Sist Compatible determinado
		aMatrix = new MatrixComplex("2,-3,0,0;0,4,0,0;1,0,1,0"); // Sist homogéneo
		aMatrix = new MatrixComplex("2,-3,0,1;-6,9,0,-3;1,0,1,1"); // Sist Comp Indeterminado
		//aMatrix = new MatrixComplex("2,-3,1;0,4,-1;1,0,1"); // No es un sistema de ecuaciones
		aMatrix = new MatrixComplex("1.0,-1.0,-1.0,-1.0,-1.0;1.0,-1.0,1.0,-1.0,1.0;-1.0,-1.0,-1.0,-1.0,-1.0;-1.0,-1.0,1.0,-1.0,1.0");
		aMatrix = new MatrixComplex(	
				" 2.0,-1.0,-1.0,-1.0,-1.0;"
			+	" 1.0,-2.0, 1.0,-1.0, 2.0;"
			+	"-1.0,-1.0,-2.0,-1.0,-1.0;"
			+	"-1.0,-1.0, 1.0,-2.0, 2.0");
		
		showResults(aMatrix);

		aMatrix = new MatrixComplex(	
					" 2.0,-1.0,-1.0,-1.0;"
				+	" 1.0,-2.0, 1.0,-1.0;"
				+	"-1.0,-1.0,-2.0,-1.0;"
				+	"-1.0,-1.0, 1.0,-2.0");
		bMatrix = new MatrixComplex("-1.0; 2.0; -1.0; 2.0");
		aMatrix.dividesleft(bMatrix).println("Solucion");

		aMatrix = new MatrixComplex("1.0,-1.0,-1.0,-1.0,-1.0;1.0,-1.0,1.0,-1.0,1.0;-1.0,-1.0,-1.0,-1.0,-1.0;-1.0,-1.0,1.0,-1.0,1.0");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex("-1,-1,-1,-2;-2,-2,-2,-2;0,2,0,2");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("-1,-1,-1,-2;0,2,0,2;-2,-2,-2,-2");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("2,-3,0,0;-6,9,0,0;1,0,1,0"); // Sist Comp Indeterminado homogéneo
		showResults(aMatrix);

	}
}
