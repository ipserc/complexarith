package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.factorization.*;

public class TestSolve09 {

	public static void showResults(LUfactor cMatrix) {
    	MatrixComplex lMatrix;
    	MatrixComplex uMatrix;
    	MatrixComplex pMatrix;

		System.out.println("_____________________________________________________________________________________");		
		System.out.println("_____________________________ LU FACTORIZATION ______________________________________");		

		cMatrix.LUfactorize();
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
		A.SVDfactorize();
    	S = A.getS();
    	V = A.getV();
    	U = A.getU();
		System.out.println("_____________________________________________________________________________________");		
		System.out.println("____________________________ SVD FACTORIZATION ______________________________________");		
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

		System.out.println("_____________________________________________________________________________________");		
		System.out.println("__________________________ SOLVE EQUATION SYSTEMS ___________________________________");		
		fMatrix.println("fMatrix = Original Matrix");
		gMatrix = fMatrix.triangleUp();
		gMatrix.println("Triangle");
		gMatrix = fMatrix.triangleUp().heap();
		gMatrix.println("Triangle heap");
		System.out.println("rank(gMatrix) = " + gMatrix.rank());		
		dMatrix = fMatrix.triangleLo();
		dMatrix.println("Triangle Lo");
		dMatrix = fMatrix.triangleLo().hollow();
		dMatrix.println("Triangle Lo hollow");

		fMatrix.unkMatrix().println("Unknowns Matrix");
		int rank1 = fMatrix.unkMatrix().rank();
			System.out.println("rank(Unknowns Matrix) = " + rank1);
			fMatrix.unkMatrix().determinant().println("unkMatrix().det=");
		int nbrSolutions = fMatrix.nbrOfSolutions();
		fMatrix.printTypeEqSys();
		fMatrix.typeEqSys();
		if (fMatrix.typeEqSys() == MatrixComplex.COMPATIBLE_DET)
			System.out.println("Se devuelve 1 solución única.");
		else if (fMatrix.typeEqSys() == MatrixComplex.COMPATIBLE_INDET) System.out.println("Se devuelven "+nbrSolutions+" soluciones LI.") ;
			else System.out.println("Sistema sin solución.");
		hMatrix = fMatrix.solve();
		hMatrix.println("Soluciones (hMatrix)");
		System.out.println("Num Sols - hMatrix.rows():" + hMatrix.rows());
		for (int i = 0 ; i < hMatrix.rows(); ++i) {
			MatrixComplex solMatrix = new MatrixComplex(1,hMatrix.cols());
			for (int col = 0; col < hMatrix.cols(); ++col) {
				System.out.println("hMatrix[" + i + "][" + col + "]:" + hMatrix.getItem(i,col));
				solMatrix.setItem(0,col,hMatrix.getItem(i,col));
			}
			solMatrix.println("Soluciones (solMatrix)");
			fMatrix.coefMatrix().times(solMatrix.transpose()).println("Proof check fMatrix.coefMatrix().times(hMatrix)");
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
