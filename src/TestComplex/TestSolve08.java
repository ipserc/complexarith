package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestSolve08 {

	private static void showResults(MatrixComplex fMatrix) {
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
		fMatrix.printTypeEqSys();	
		if (typeEqSys == MatrixComplex.DETERMINATE)
			System.out.println("Se devuelve 1 solución única.");
		else if (typeEqSys == MatrixComplex.INDETERMINATE) System.out.println("Se devuelven "+nbrSolutions+" soluciones LI.") ;
			else {
				System.out.println("Sistema sin solución.") ;
				return;
			}
		//System.out.println("	SOLVE GAUSS with λ " + seed.toString());
		System.out.println(Complex.boxTextRandom(boxSize, "System Equations Solutions"));   	
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
		MatrixComplex aMatrix;

		Complex.setFormatON();
		Complex.setFixedON(3);
		
		aMatrix = new MatrixComplex(
				  " 2.925-0.533i, 0.729+2.732i, 7.443+6.499i,-4.267+3.362i,0;"
				+ " 0.208-2.559i,-2.815+3.671i,-7.676+0.295i,-1.295-2.829i,0;"
				+ "-0.525+2.311i, 2.814+5.875i,-7.154-1.795i,-4.754+2.267i,0;"
				+ "-3.146+4.064i,-8.656+6.937i, 7.892-3.179i,-8.197+8.063i,0");
		aMatrix = new MatrixComplex(
				  " 143.981        ,-49.251-71.500i,-21.671-42.518i,101.438+42.882i,0;"
				+ " -49.251+71.500i, 96.680        , 61.750+28.227i,-34.934+ 6.514i,0;"
				+ " -21.671+42.518i, 61.750-28.227i,130.192        , 33.935-92.671i,0;"
				+ " 101.438-42.882i,-34.934-6.514i,  33.935+92.671i,354.054        ,0");
		showResults(aMatrix);

		/* ***/
		aMatrix = new MatrixComplex("" + 
				" 1, 2, 1,-3, 0;"+
				" 1,-2, 1,-1, 0;"+
				" 3, 5, 3,-4, 0;"+ 
				" 1,-3, 1,-2, 0");
		showResults(aMatrix);

		aMatrix = new MatrixComplex(""+ 
				" 1, 2, 1,-3,-2, 0;"+
				" 1,-2, 1,-1, 2, 0;"+
				" 3, 6, 3,-9,-6, 0;"+ 
				" 1,-2, 1,-1, 2, 0;"+
				" 1,-2, 0,-1, 2, 0;");
		showResults(aMatrix);
			
		aMatrix = new MatrixComplex(""+
				" 1, 2, 1, 0;"+
				" 2, 4, 2, 0;"+
				"-3, 6,-3, 0");
		showResults(aMatrix);		

		aMatrix = new MatrixComplex("" +
				" 2,-3, 0,-9;"+
				" 0, 4, 0,16;"+
				" 1, 0, 1, 2");
		showResults(aMatrix);

		/* ***/

		aMatrix = new MatrixComplex("" + 
				" 1, 2, 1,-3, 0;"+
				" 2,-2,-1, 4, 0;"+
				" 5, 2,-3,-2, 0;"+ 
				" 3,-1, 1,-3, 0");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("" + 
				" 1, 2, 1,-3, 0;"+
				" 4, 0,-4, 1, 0;"+
				" 5, 2,-3,-2, 0;"+ 
				" 3,-1, 1,-3, 0");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(
       			" 6.0 , 0.0 ,-1.0 ,-4.0 ,-2.0, 0;"+
       			" 7.0 , 0.0 ,-6.0 , 0.0 ,-2.0, 0;"+
       			"-4.0 ,-1.0 ,-1.0 , 1.0 ,-7.0, 0;"+
       			"-1.0 , 8.0 , 8.0 , 6.0 ,-4.0, 0;"+
       			" 1.0 ,-4.0 , 6.0 , 7.0 , 2.0, 0");    	
		showResults(aMatrix);

		aMatrix = new MatrixComplex(
				"-3,-7, 5, 0;"+
				" 2, 5, 1, 0;"+
				" 1, 2,-6, 0");		
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(
				"-2, 1, 3, 4;"+
				" 1,-1, 0, 3;"+
				"-1, 0, 3, 7");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex(
				"  2,  9,  5,  2,  1,  4,  8,  9;" + 
				"  7,  5,  9,  7,  8,  9,  2,  3;" + 
				"-14,  3, -4,-12, -8, -5, -8,  5;" + 
				"-21, 10, -5,-15, -8, -5, -7, 11;" + 
				"  9,  2,  6,  5,  1,  4,  7,  3;" + 
				" 30,  4, 19, 24, 17, 18, 17,  1;" + 
				"  7,  4,  3,  9,  8,  5,  9,  1" ); 
		showResults(aMatrix);

		/* ***/
	}
}
