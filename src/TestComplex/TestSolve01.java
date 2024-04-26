package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestSolve01 {

	private static void showResults(MatrixComplex fMatrix, Complex seed) {
		MatrixComplex dMatrix;
		MatrixComplex gMatrix;
		MatrixComplex hMatrix;
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "MATRIX SOLVE EQUATION"));   	
		fMatrix.println("fMatrix = Original Matrix");
		System.out.println(Complex.boxTextRandom(boxSize, "Some Matrix Operations"));   	
		gMatrix = fMatrix.triangleUp();
		gMatrix.println("Triangle");
		System.out.println("rank(gMatrix) = " + gMatrix.rank());		
		dMatrix = fMatrix.triangleLo();
		dMatrix.println("Triangle Lo");

		System.out.println(Complex.boxTextRandom(boxSize, "Equations Operations"));   	
		fMatrix.unkMatrix().println("Unknowns Matrix");
		int rank1 = fMatrix.unkMatrix().rank();
			System.out.println("rank(Unknowns Matrix) = " + rank1);
			fMatrix.unkMatrix().determinant().println("unkMatrix().det=");
		int nbrSolutions = fMatrix.nbrOfSolutions();
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
		System.out.println(Complex.boxTextRandom(boxSize, "System Equations Solutions"));   	
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
		MatrixComplex fMatrix;
		/************************************************************
		 * seed to calculate the solution for indeterminate systems *
		 ************************************************************/
		Complex seed; 
		
		Complex.setFormatON();
		Complex.setFixedON(3);
		
		seed = new Complex(1.334567,-2.72345);
		fMatrix = new MatrixComplex(""
				+ "2, 18, 0;"
				+ "1,  9, 0");
		showResults(fMatrix, seed);
		
		fMatrix = new MatrixComplex(""
				+ "1,  9, 0;"
				+ "2, 18, 0");
		showResults(fMatrix, seed);


		fMatrix = new MatrixComplex(""
				+ " 2,-18, 0;"
				+ " 1, -9, 0");
		showResults(fMatrix, seed);

		seed = new Complex(1.334567, 0);
		fMatrix = new MatrixComplex(""
				+ "2, 18, 0;"
				+ "1,  9, 0");
		showResults(fMatrix, seed);
		
		fMatrix = new MatrixComplex(""
				+ "2i, 18i, 0;"
				+ "1i,  9i, 0");
		showResults(fMatrix, seed);


		seed = new Complex(0,-2.72345);
		fMatrix = new MatrixComplex(""
				+ "2, 18, 0;"
				+ "1,  9, 0");
		showResults(fMatrix, seed);

		fMatrix = new MatrixComplex(""
				+ "2i, 18i, 0;"
				+ "1i,  9i, 0");
		showResults(fMatrix, seed);
		
		fMatrix = new MatrixComplex(""
				+ "2, 18,  -8, 0;"
				+ "3, 27, -12, 0;"
				+ "1,  9,  -4, 0");
		showResults(fMatrix, seed); 
		
		seed = new Complex(0,-2.72345);
		fMatrix = new MatrixComplex(""
				+ "2, 18, 6;"
				+ "1,  9, 3");
		showResults(fMatrix, seed);

		seed = new Complex(0,-2);
		fMatrix = new MatrixComplex(""
				+ "2, 18, 6;"
				+ "1,  9, 3");
		showResults(fMatrix, seed);

		seed = new Complex(0,0);
		fMatrix = new MatrixComplex(""
				+ "2, 18, 6;"
				+ "1,  9, 3");
		showResults(fMatrix, seed);
		
		seed = new Complex(1,0);
		fMatrix = new MatrixComplex(""
				+ "2, 18, 6;"
				+ "1,  9, 3");
		showResults(fMatrix, seed);

		seed = new Complex(0,0);
		fMatrix = new MatrixComplex(""
				+ "2, 18, 6;"
				+ "1,  9, 3i");
		showResults(fMatrix, seed);

	} 
}
