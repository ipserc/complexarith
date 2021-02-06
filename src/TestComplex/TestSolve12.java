package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestSolve12 {

	private static void showResults(MatrixComplex fMatrix) {
		MatrixComplex dMatrix;
		MatrixComplex gMatrix;
		MatrixComplex hMatrix;

		System.out.println("_____________________________________________________________________________________");		
		System.out.println("__________________________ SOLVE EQUATION SYSTEMS ___________________________________");
		System.out.println(fMatrix.toWolfram());
		fMatrix.println("fMatrix = Original Matrix");
		System.out.println("---------------- rank:" +  fMatrix.rank());
		// -- fMatrix.printTypeEqSys();
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

		if (fMatrix.nbrOfSolutionsText() == 0) return;
		
		hMatrix = fMatrix.solve();
		System.out.println("--------------------------------------");
		hMatrix.println("Soluciones (hMatrix)");
		System.out.println("--------------------------------------");
		for (int i = 0 ; i < hMatrix.rows(); ++i) {
			MatrixComplex solMatrix = new MatrixComplex(1,hMatrix.cols());
			for (int col = 0; col < hMatrix.cols(); ++col)
				solMatrix.setItem(0,col,hMatrix.getItem(i,col));
			//solMatrix.println("Soluciones (solMatrix)");
			fMatrix.coefMatrix().times(solMatrix.transpose()).println("Proof check fMatrix.coefMatrix().times(solMatrix.transpose())");
		}
		if (fMatrix.typeEqSys() == MatrixComplex.DETERMINATE) {
			System.out.println("	SOLVE CRAMER");		
			hMatrix = fMatrix.solveCramer();
			System.out.println("++++++++++++++++++++++++++++++++++++++");
			hMatrix.println("Soluciones Cramer (hMatrix)");
			System.out.println("++++++++++++++++++++++++++++++++++++++");
			fMatrix.coefMatrix().times(hMatrix.transpose()).println("Proof check fMatrix.coefMatrix().times(hMatrix.transpose())");			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix;

		Complex.setFormatON();
		Complex.setFixedON(3);

		/* * /
		aMatrix = new MatrixComplex(
				  " 1, 0, 0, 1, 1;"
				+ " 3,-2, 0, 0, 2;"
				+ " 3,-2, 1, 0, 4;"
				+ " 0, 0, 1, 0, 2");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(
				  " 1, 2,-1,-2, 1;"
				+ " 3,-2, 0, 0, 2;"
				+ " 3,-2, 1, 0, 4;"
				+ " 0, 0, 1, 0, 2");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(
				  "-1,-1,-1,-2;"
				+ "-2,-2,-2,-2;"
				+ " 0, 2, 0, 2");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("" + 
				" 1, 2, 1,-3, 0;"+
				" 4, 0,-4, 1, 0;"+
				" 5, 2,-3,-2, 0;"+ 
				" 3,-1, 1,-3, 0");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("" + 
				" 1, 2, 1,-3, 0;"+
				" 4, 0,-4, 1, 0;"+
				" 5, 2,-3,-2, 0;"+ 
				" 5, 2,-3,-2, 0");
		showResults(aMatrix);
		/* * /

		aMatrix = new MatrixComplex(
				  " 2,-2, 1, 0;"
				+ " 2,-2, 1, 0;"
				+ " 0, 0, 4, 0");
		showResults(aMatrix);		
		/* * /
		
		aMatrix = new MatrixComplex(
				  " 3, 2,-1, 0;"
				+ " 2, 3, 1, 0;"
				+ " 0, 0, 5, 0");
		showResults(aMatrix);		
		/**/
     	aMatrix = new MatrixComplex(""+
     			"  4, -1,  1, 0;" +
     			"  0, -3,  3, 0;" +
     			"  0,  2, -2, 0");
		showResults(aMatrix);		

		aMatrix = new MatrixComplex(""+
     			"  4, -1,  1, -1;" +
     			"  0, -3,  3, -3;" +
     			"  0,  2, -2,  2");
		showResults(aMatrix);		

		MatrixComplex I = new MatrixComplex(aMatrix.rows(), aMatrix.cols()); I.initMatrixDiag(1,0);
     	aMatrix = aMatrix.minus(I.times(4));
		showResults(aMatrix);		

     	aMatrix = new MatrixComplex(""+
     			"  0, -1,  1, -1;" +
     			"  0, -3,  3,  3;" +
     			"  0,  2, -2,  2");
		showResults(aMatrix);		

		Complex.setFixedOFF();
     	aMatrix = new MatrixComplex(""+
     			"  0.000001, 1,  1;" +
     			"  1.000000, 1,  2");
		showResults(aMatrix);		
		
     	aMatrix = new MatrixComplex(""+
     			"  1.000000, 1,  2;" +
     			"  0.000001, 1,  1;");
		showResults(aMatrix);		

     	aMatrix = new MatrixComplex(""+
     			"  1.000000, 0.0000001,  2;" +
     			"  0.000001, 1.0000000,  1;");
		showResults(aMatrix);		

     	aMatrix = new MatrixComplex(""+
     			"  0.000001, 1.0000000,  1;"+
     			"  1.000000, 0.0000001,  2;");
		showResults(aMatrix);		
	}
}
