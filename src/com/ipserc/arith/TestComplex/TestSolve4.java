package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestSolve4 {

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
		
		/****/
		aMatrix = new MatrixComplex("2,-3,0,-9;0,4,0,16;1,0,1,2");
		//aMatrix = new MatrixComplex("2,-3,-9;0,4,16");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex("1,2,1,3;2,4,2,6;-3,6,-3,9");
		showResults(aMatrix);		

		aMatrix = new MatrixComplex("1,2,1,3;2,4,2,6;3,6,3,9");
		showResults(aMatrix);
		 /****/

		aMatrix = new MatrixComplex("1,2,1,3;2,4,2,6;3,6,3,9");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("1,2,1,0;3,6,-1,0;1,2,4,0");
		showResults(aMatrix);		

		aMatrix = new MatrixComplex("1,2,1,0;2,4,2,0;3,6,3,0");
		showResults(aMatrix);

	}
}
