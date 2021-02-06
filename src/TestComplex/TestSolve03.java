package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestSolve03 {

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
		MatrixComplex aMatrix, interms;

		Complex.setFormatON();
		Complex.setFixedON(3);
/*
		aMatrix = new MatrixComplex("1,3,4,-5;0,0,0,0;0,0,0,0"); showResults(aMatrix);
		aMatrix = new MatrixComplex("2,18,0;1,9,0"); showResults(aMatrix);
		aMatrix = new MatrixComplex(""
    			+ "3,0,0,0,0,0;"
    			+ "0,-4,0,0,0,0;"
    			+ "0,0,1,0,0,0;"
    			+ "0,0,0,3,0,0;"
    			+ "0,0,0,0,1,0");
		showResults(aMatrix);
		aMatrix = new MatrixComplex("1,3,4,-5;-4,0,3,5;-8,0,6,10");	showResults(aMatrix);
		aMatrix = new MatrixComplex("1,3,4,-5;0,0,0,0;0,0,0,0"); showResults(aMatrix);
		aMatrix = new MatrixComplex("1,2,3,4,-5;0,0,0,0,0;0,0,0,0,0;0,0,0,0,0"); showResults(aMatrix);
		aMatrix = new MatrixComplex("1,2,3,4,-5;0,0,2,-3,1;0,0,0,0,0;0,0,0,0,0"); showResults(aMatrix);
		aMatrix = new MatrixComplex("1,2,3,4,-5;0,0,2,-3,1;0,3,0,0,6;0,0,0,0,0"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("-2,2,-1,0;2,-2,1,0;0,0,0,0"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("2,2,-1,0;2,2,1,0;0,0,4,0"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("1,3,-1,2,-5;2,6,-2,4,-10;0,0,0,0,0;0,0,0,0,0"); showResults(aMatrix);
    	aMatrix = new MatrixComplex("1,3,-1,-2,5;2,6,-2,4,-10;0,0,0,0,0;0,0,0,0,0"); showResults(aMatrix);
		aMatrix = new MatrixComplex("-1,2,-1,0;2,-2,1,0;1,-2,1 ,0");showResults(aMatrix);
*/
/* * /		
		aMatrix = new MatrixComplex(4);interms = new MatrixComplex(1,4); 
		aMatrix.initMatrixRandomInteger(1); 
		interms.initMatrixRandomInteger(1); 
		aMatrix = aMatrix.augment(interms.transpose()); 
		showResults(aMatrix);
/ * */
		//aMatrix = new MatrixComplex("1.0,1.0,1.0,-1.0,-1.0;1.0,1.0,1.0,1.0,-1.0;-1.0,-1.0,-1.0,1.0,1.0;1.0,1.0,-1.0,-1.0,-1.0"); showResults(aMatrix);
		//aMatrix = new MatrixComplex("1.0,1.0,1.0,-1.0,-1.0;0.0,0.0,0.0,2.0,0.0;0.0,0.0,0.0,0.0,0.0;0.0,0.0,-2.0,0.0,0.0"); showResults(aMatrix);
		//aMatrix = new MatrixComplex("1.0,1.0,1.0,-1.0,-1.0;0.0,0.0,0.0,2.0,0.0;0.0,0.0,-2.0,0.0,0.0;0.0,0.0,0.0,0.0,0.0"); showResults(aMatrix);
		aMatrix = new MatrixComplex(
				  "1.0,-1.0,-1.0,-1.0,-1.0;"
				+ "1.0,-1.0,1.0,-1.0,1.0;"
				+ "-1.0,-1.0,-1.0,-1.0,-1.0;"
				+ "-1.0,-1.0,1.0,-1.0,1.0"); showResults(aMatrix);
		//aMatrix = new MatrixComplex("1,3,4,-5;-4,0,3,5;-8,0,6,10");	showResults(aMatrix);
/* * /

		aMatrix = new MatrixComplex(""
				+ "1,0,0,0,0;"
				+ "3,2,0,0,0;"
				+ "3,2,0,0,0;"
				+ "0,0,0,0,0"); showResults(aMatrix);
/* */
				aMatrix = new MatrixComplex("1,3,4,-5;-4,0,3,5;-8,0,6,10"); showResults(aMatrix);
				aMatrix = new MatrixComplex("1,4,4,-5;-4,0,5,7;-8,0,6,10"); showResults(aMatrix);

	}
}
