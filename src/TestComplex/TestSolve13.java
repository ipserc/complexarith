package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestSolve13 {

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
		/************************************************************
		 * seed to calculate the solution for indeterminate systems *
		 ************************************************************/
		Complex seed = new Complex(1.334567,-2.72345);
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
		MatrixComplex bMatrix;
		MatrixComplex fMatrix;
		MatrixComplex hMatrix;

		Complex.setFormatON();
		Complex.setFixedON(3);
		
		fMatrix = new MatrixComplex("1,i,1,1-i;1,-2+i,3-i,2;1,0,-i,5");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1,0,1,1,3;1,-2,3,2,-4;1,-0.7,1,5,2;2,3,0,4,-3");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("2,1,1,1;6,2,1,-1;-2,2,1,7");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1+i,0,i,1;1,-2,3i,2-i;1,-1,1i,5;2i,3,0,4");
		fMatrix.println("fMatrix = Original Matrix");
		hMatrix = fMatrix.conjugate();
		hMatrix.println("hMatrix conjugated");       	
		hMatrix = hMatrix.transpose();
		hMatrix = fMatrix.times(hMatrix);
		hMatrix.println("Original * Conj Transp Matrix");

		fMatrix = new MatrixComplex(4,5);
		fMatrix.initMatrixRandomPol();
		showResults(fMatrix);

		aMatrix = new MatrixComplex("1,2;3,1");
		bMatrix = aMatrix.times(aMatrix);
		aMatrix.println("aMatrix = Original Matrix");       	
		bMatrix.println("bMatrix = aMatrix^2");       	

		aMatrix = new MatrixComplex("2,2;3,1");
		bMatrix = aMatrix.times(aMatrix);
		aMatrix.println("aMatrix = Original Matrix");       	
		bMatrix.println("bMatrix = aMatrix^2");       	

		aMatrix = new MatrixComplex("3,2;3,1");
		bMatrix = aMatrix.times(aMatrix);
		aMatrix.println("aMatrix = Original Matrix");       	
		bMatrix.println("bMatrix = aMatrix^2");       	

		aMatrix = new MatrixComplex("4,2;3,1");
		bMatrix = aMatrix.times(aMatrix);
		aMatrix.println("aMatrix = Original Matrix");       	
		bMatrix.println("bMatrix = aMatrix^2");       	

		fMatrix = new MatrixComplex(10,11);
		fMatrix.initMatrixRandomRecInt(10);
		showResults(fMatrix);

		fMatrix = new MatrixComplex( 
				  " 6.0+1.0i, 1.0- 3.0i, 8.0+9.0i,  0.0-8.0i,-8.0+ 7.0i, 1.0+ 9.0i, 1.0+6.0i, 8.0+6.0i,  9.0-2.0i,  8.0+ 2.0i;"
				+ "-3.0+9.0i, 5.0+ 4.0i,-1.0-7.0i,-10.0+7.0i,-9.0- 1.0i,-2.0+ 3.0i, 1.0-6.0i, 3.0+6.0i,-10.0-8.0i, -9.0- 7.0i;"
				+ "-6.0+0.0i, 1.0- 2.0i, 0.0-3.0i,  6.0-4.0i, 0.0- 5.0i, 9.0+ 2.0i, 6.0-3.0i, 3.0-2.0i,  0.0-3.0i,-10.0+ 4.0i;"
				+ "-8.0+0.0i, 3.0-10.0i, 8.0-8.0i, -6.0+4.0i, 1.0+ 9.0i, 1.0+ 1.0i, 3.0+6.0i, 7.0-7.0i,  4.0-7.0i, -3.0+ 0.0i;"
				+ "-7.0+7.0i, 2.0-10.0i,-2.0-9.0i, -5.0+9.0i,-7.0-10.0i,-9.0- 6.0i,-4.0-4.0i, 3.0-2.0i, -6.0+7.0i, -9.0+ 4.0i;"
				+ "-2.0+9.0i,-9.0- 7.0i, 0.0-9.0i,  3.0+1.0i, 2.0- 3.0i, 3.0-10.0i,-2.0+0.0i, 7.0-8.0i,  5.0-2.0i, -3.0-10.0i;"
				+ "-1.0-7.0i,-4.0- 5.0i, 4.0+1.0i, -9.0+7.0i, 9.0+ 7.0i,-8.0- 1.0i, 3.0-7.0i,-3.0+7.0i,  3.0+0.0i,  3.0+ 3.0i;"
				+ " 8.0+4.0i,-5.0- 3.0i,-5.0+ 5.0i,-8.0-8.0i, 9.0- 7.0i,-7.0+ 7.0i,-8.0+6.0i,-1.0-8.0i, -6.0-6.0i,-10.0- 8.0i;"
				+ "0,0,0,0,0,0,0,0,0,0");
		showResults(fMatrix);

		fMatrix = new MatrixComplex(2,3);
		fMatrix.initMatrixRandomRecInt(10);
		showResults(fMatrix);

		fMatrix = new MatrixComplex("5,2,-1,2;1,-3,2,-5;2,3,-1,1");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1,1,-1;2,-3,-1"); //2,2,-3");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("3,-0.2,-0.5,8;0.1,7,0.4,-19.5;0.4,-0.1,10,72.4");
		showResults(fMatrix);

		fMatrix = new MatrixComplex(8,9);
		fMatrix.initMatrixRandomRecInt(9);
		showResults(fMatrix);

		fMatrix = new MatrixComplex(""
				+ "2, 18, 0;"
				+ "1,  9, 0");
		showResults(fMatrix);

		fMatrix = new MatrixComplex(""
				+ " 2,-18, 0;"
				+ " 1, -9, 0");
		showResults(fMatrix);

		fMatrix = new MatrixComplex(""
				+ "2i, 18i, 0;"
				+ "1i,  9i, 0");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("71.0 , -6.0 , -25.0, 0; -6.0 , 16.0 , -20.0, 0 ; -25.0 , -20.0 , 60, 0");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("71.0 , -6.0 , -25.0, 0; -6.0 , 16.0 , -20.0, 0; 0, 0, 0, 0");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1,2,4;2,4,7");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1,1,-1,3;2,-1,-1,6;3,0,-2,0");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("0.143,0.357,2.01,-5.17;-1.31,0.911,1.99,-5.46;11.2,-4.30,-0.605,4.42");
		showResults(fMatrix);

		fMatrix = new MatrixComplex(4,5); fMatrix.initMatrixRandomRecInt(9);
		showResults(fMatrix);

		fMatrix = new MatrixComplex(5,6); fMatrix.initMatrixRandomRecInt(9);
		for (int i = 0; i < fMatrix.complexMatrix[0].length; ++i) {
			fMatrix.complexMatrix[3][i].setComplexRec(0,0);
			fMatrix.complexMatrix[2][i].setComplexRec(0,0);
		}
		showResults(fMatrix);

		fMatrix = new MatrixComplex("4,-3,-3,-6;4,-2,3,-17;4,-4,-9,4");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("4,-5,-6,1;1,-1,-3,0;2,-1,-12,-1");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("2,-3,0,-9;1,5,-2,12;6,-1,-1,5");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("2,-3,0,-9;1,5,-2,12;0,4,0,16");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("2,-3,0,-9;0,4,0,16;1,5,-2,12");
		showResults(fMatrix);

		// fMatrix = new MatrixComplex("2,-3,0,-9;0,4,0,16;0,0,0,0");
		// showResults(fMatrix);

		fMatrix = new MatrixComplex("4,-3,-3,-6i;4i,-2,3,0;3,-4,-9,4i");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1,2i,3;2,1,-1i");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("10,7,8,7,32; 7,5,6,5,23; 8,6,10,9,33; 7,5,9,10,31");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("10,7,8,7,32.1; 7,5,6,5,22.9; 8,6,10,9,32.9; 7,5,9,10,31.1");
		showResults(fMatrix);

		Complex.setFixedOFF();
		fMatrix = new MatrixComplex("0.01,1,1;1,1,2");
		showResults(fMatrix);
		
		fMatrix = new MatrixComplex(15,16); fMatrix.initMatrixRandomInteger(9);
		showResults(fMatrix);

	}
}
