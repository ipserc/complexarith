package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestSolve02 {

	private static void showResults(MatrixComplex fMatrix) {
		MatrixComplex dMatrix;
		MatrixComplex gMatrix;
		MatrixComplex hMatrix;

		System.out.println("_____________________________________________________________________________________");		
		System.out.println("__________________________ SOLVE EQUATION SYSTEMS ___________________________________");		
		fMatrix.println("fMatrix = Original Matrix");
		gMatrix = fMatrix.triangleUp();
		gMatrix.println("Triangle");
		System.out.println("rank(gMatrix) = " + gMatrix.rank());		
		dMatrix = fMatrix.triangleLo();
		dMatrix.println("Triangle Lo");

		fMatrix.unkMatrix().println("Unknowns Matrix");
		int rank1 = fMatrix.unkMatrix().rank();
			System.out.println("rank(Unknowns Matrix) = " + rank1);
			fMatrix.unkMatrix().determinant().println("unkMatrix().det=");
		int nbrSolutions = fMatrix.nbrOfSolutions();
		fMatrix.printTypeEqSys();
		fMatrix.typeEqSys();
		if (fMatrix.typeEqSys() == MatrixComplex.DETERMINATE)
			System.out.println("Se devuelve 1 solución única.");
		else if (fMatrix.typeEqSys() == MatrixComplex.INDETERMINATE) System.out.println("Se devuelven "+nbrSolutions+" soluciones LI.") ;
			else System.out.println("Sistema sin solución.") ;
		System.out.println("	SOLVE GAUSS");
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
		// TODO Auto-generated method stub
		MatrixComplex fMatrix;

		Complex.setFormatON();
		//fMatrix = new MatrixComplex("1,5,-2,12");
		//fMatrix = new MatrixComplex("2,-3,0,-9;0,4,0,16");
		fMatrix = new MatrixComplex("2,-3,0,-9;0,4,0,16;1,5,-2,12");
		showResults(fMatrix);

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("Soluciones a los ejercicios de  http://www.vitutor.com/algebra/sistemas%20I/sg_e.html");
		System.out.println("----------------------------- EJERCICIO 1 -------------------------------------------");
		fMatrix = new MatrixComplex("2,1,1,0; -1,2,7,0; 3,1,0,0");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 2 -------------------------------------------");
		fMatrix = new MatrixComplex("2,-1,3,1; 3,2,-1,5; 0,0,0,0");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 3 -------------------------------------------");
		fMatrix = new MatrixComplex("1,1,1,1; 2,3,-4,9; 1,-1,1,-1");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 4 -------------------------------------------");
		fMatrix = new MatrixComplex("3,2,1,1; 5,3,4,2; 1,1,-1,1");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 5 -------------------------------------------");
		fMatrix = new MatrixComplex("1,-9,5,33; 1,3,-1,-9; 1,-1,1,5");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 6 -------------------------------------------");
		fMatrix = new MatrixComplex("1,-1,1,1,4; 2,1,-3,1,4; 1,-2,2,-1,3; 1,-3,3,-3,2");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 7 -------------------------------------------");
		fMatrix = new MatrixComplex("1,-2,-2,1,4; 1,1,1,-1,5; 1,-1,-1,1,6; 6,-3,-3,2,32");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 8 -------------------------------------------");
		fMatrix = new MatrixComplex("2,-5,4,1,-1,-3; 1,-2,1,-1,1,5; 1,-4,6,2,1,10; 0,0,0,0,0,0; 0,0,0,0,0,0");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 9 -------------------------------------------");
		fMatrix = new MatrixComplex("1,1,-1,1,0; 3,2,1,1,0; 5,3,4,2,0; -2,-1,5,6,0");
		showResults(fMatrix);

	}
}
