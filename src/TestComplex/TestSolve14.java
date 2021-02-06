package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestSolve14 {

	private static void checkSol(MatrixComplex fMatrix, MatrixComplex solution) {
		MatrixComplex indTerm = fMatrix.indMatrix().transpose();
		MatrixComplex uknMatix = fMatrix.unkMatrix().transpose();
		
		System.out.println(".".repeat(55));
		indTerm.println                 ("Independent Terms");
		solution.println                ("Solution         ");
		solution.times(uknMatix).println("Check            ");
		System.out.println(".".repeat(55));
	}
	
	private static MatrixComplex solve(MatrixComplex fMatrix) {
		MatrixComplex hMatrix = new MatrixComplex();
		int nbrSolutions = fMatrix.nbrOfSolutions();
		/************************************************************
		 * seed to calculate the solution for indeterminate systems *
		 ************************************************************/
		Complex seed = new Complex(1);
		fMatrix.println("Equation System");
		int typeEqSys = fMatrix.typeEqSys();
		fMatrix.printTypeEqSys(typeEqSys, seed);	
		if (typeEqSys == MatrixComplex.DETERMINATE)
			System.out.println("Se devuelve 1 solución única.");
		else if (typeEqSys == MatrixComplex.INDETERMINATE) System.out.println("Se devuelven "+nbrSolutions+" soluciones LI.") ;
			else {
				System.out.println("Sistema sin solución.") ;
				return hMatrix;
			}
		//System.out.println("	SOLVE GAUSS with λ " + seed.toString());
		hMatrix = fMatrix.solve(seed);
		hMatrix.println("Soluciones (hMatrix)");
		return hMatrix;
	}
	
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
		/************************************************************
		 * seed to calculate the solution for indeterminate systems *
		 ************************************************************/
		Complex seed = new Complex(1,0);
		int typeEqSys = fMatrix.typeEqSys();
		fMatrix.printTypeEqSys(typeEqSys, seed);	
		if (typeEqSys == MatrixComplex.DETERMINATE)
			System.out.println("Se devuelve 1 solución única.");
		else if (typeEqSys == MatrixComplex.INDETERMINATE) System.out.println("Se devuelven "+nbrSolutions+" soluciones LI.") ;
			else {
				System.out.println("Sistema sin solución.") ;
				return;
			}
		//System.out.println("	SOLVE GAUSS with λ " + seed.toString());
		hMatrix = fMatrix.solve(seed);
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
		MatrixComplex partsol;
		MatrixComplex homosol;
		MatrixComplex solution;		
		MatrixComplex fMatrix;
		MatrixComplex hMatrix;

		Complex.setFormatON();
		Complex.setFixedON(3);
		
		fMatrix = new MatrixComplex(  " 1,-2, 3 ,1; "
									+ "-2, 5,-1, 2; "
									+ " 4,-9, 7, 0");
		partsol = solve(fMatrix);
		hMatrix = fMatrix.homogeneous();
		homosol = solve(hMatrix);
		
		homosol.println("Soluciones sistema homogeneo");
		
		for (int i = -5; i < 6; ++i) {
			solution = partsol.plus(homosol.times(i));
			checkSol(fMatrix, solution);
		}

		fMatrix = new MatrixComplex(  " 3, 1,-1,-4,  6;"
									+ " 1,-2, 3, 1,  2;"
									+ "-2, 5,-1, 2, -3;"
									+ "10,-7, 5,-8, 19");
		partsol = solve(fMatrix);
		hMatrix = fMatrix.homogeneous();
		homosol = solve(hMatrix);
		
		for (int i = -5; i < 6; ++i) {
			solution = partsol.plus(homosol.times(i));
			checkSol(fMatrix, solution);
		}
	}
}
