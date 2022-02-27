package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.factorization.*;

public class TestSolve09 {
	private static int boxSize = 65;

	public static void showResults(LUfactor cMatrix) {
    	MatrixComplex lMatrix;
    	MatrixComplex uMatrix;
    	MatrixComplex pMatrix;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "LU FACTORIZATION"));   	
		//cMatrix.LUfactorize();
    	cMatrix.println("Original Matrix");
    	lMatrix = cMatrix.L();
    	lMatrix.println("L");
    	uMatrix = cMatrix.U();
    	uMatrix.println("U");
    	pMatrix = cMatrix.P();
    	pMatrix.println("P");
    	cMatrix.P().transpose().times(cMatrix.L().times(cMatrix.U())).println("PT · L · U");

	}
	public static void showResults(SVDfactor A) {
		MatrixComplex S;
    	MatrixComplex V;
    	MatrixComplex U;

		S = A.getS();
    	V = A.getV();
    	U = A.getU();
		
		System.out.println(Complex.boxTitleRandom(boxSize, "SVD FACTORIZATION"));   	
    	A.println("A");
    	S.println("S");
    	U.println("U");
    	V.println("V");
    	(U.times(S)).times(V.adjoint()).println("U·S·V*");
	}
	
	private static void showResults(MatrixComplex fMatrix) {
		MatrixComplex dMatrix;
		MatrixComplex gMatrix;
		MatrixComplex hMatrix;
		
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
		MatrixComplex aMatrix;
		Complex.setFormatON();
		Complex.setFixedON(3);

     	aMatrix = new MatrixComplex(
     			"-1, 1,-1, 0;"+
     			"-1,-1, 1, 0;" +
     			" 1,-1, 1, 0");
		showResults(aMatrix);

		SVDfactor svdMatrix;
		svdMatrix = new SVDfactor(
     			"-1, 1,-1;"+
     			"-1,-1, 1;" +
     			" 1,-1, 1,");
		showResults(svdMatrix);
		
		svdMatrix = new SVDfactor(
     			" 2i, 1,-1;"+
     			"-1,0, 1;" +
     			" 1,-1, 2i,");
		showResults(svdMatrix);
		
		svdMatrix = new SVDfactor(
     			" 2i, 1;"+
     			"-1, 1;" +
     			" -1, 2i,");
		showResults(svdMatrix);
		
		svdMatrix = new SVDfactor(svdMatrix.transpose());
		showResults(svdMatrix);

	
		LUfactor luMatrix;
		luMatrix = new LUfactor(
     			"-1, 1,-1;"+
     			"-1,-1, 1;" +
     			" 1,-1, 1,");
		showResults(luMatrix);
		
		aMatrix = new MatrixComplex(3);
		MatrixComplex interms = new MatrixComplex(1,3); 
		aMatrix.initMatrixRandomInteger(1); 
		//interms.initMatrixRandomInteger(1); 
		aMatrix = aMatrix.augment(interms.transpose()); 
		showResults(aMatrix);
	}
}
