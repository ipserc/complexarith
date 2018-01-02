package TestComplex;

import com.ipserc.arith.matrixcomplex.*;

public class TestSolve {

	private static void showResults(MatrixComplex fMatrix) {
		MatrixComplex dMatrix;
		MatrixComplex gMatrix;
		MatrixComplex hMatrix;

		fMatrix.println("fMatrix = Original Matrix");
		gMatrix = fMatrix.triangleUp();
		gMatrix.println("Triangle Up");
		fMatrix.triangleLo().println("Triangle Lo");
		System.out.println("rank = " + gMatrix.rank());
		System.out.println("rowLen = " + gMatrix.complexMatrix.length);
		hMatrix = fMatrix.solve();
		hMatrix.println("Soluciones (hMatrix)");
		fMatrix.coefMatrix().times(hMatrix).println("Proof check fMatrix.coefMatrix().times(hMatrix)");
		System.out.println("------------------------------------------------------------------------");		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MatrixComplex aMatrix;
		MatrixComplex bMatrix;
		MatrixComplex cMatrix;
		MatrixComplex dMatrix;
		MatrixComplex eMatrix;
		MatrixComplex fMatrix;
		MatrixComplex gMatrix;
		MatrixComplex hMatrix;
		MatrixComplex iMatrix;

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

		fMatrix = new MatrixComplex( "6.0 + 1.0i,1.0 - 3.0i,8.0 + 9.0i,-8.0i,-8.0 + 7.0i,1.0 + 9.0i,1.0 + 6.0i,8.0 + 6.0i,9.0 - 2.0i,8.0 + 2.0i; "
				+ "-3.0 + 9.0i,5.0 + 4.0i,-1.0 - 7.0i,-10.0 + 7.0i,-9.0 - 1.0i,-2.0 + 3.0i,1.0 - 6.0i,3.0 + 6.0i,-10.0 - 8.0i,-9.0 - 7.0i; "
				+ "-6.0,1.0 - 2.0i,-3.0i,6.0 - 4.0i,-5.0i,9.0 + 2.0i,6.0 - 3.0i,3.0 - 2.0i,-3.0i,-10.0 + 4.0i; "
				+ "-8.0,3.0 - 10.0i,8.0 - 8.0i,-6.0 + 4.0i,1.0 + 9.0i,1.0 + 1.0i,3.0 + 6.0i,7.0 - 7.0i,4.0 - 7.0i,-3.0; "
				+ "-7.0 + 7.0i,2.0 - 10.0i,-2.0 - 9.0i,-5.0 + 9.0i,-7.0 - 10.0i,-9.0 - 6.0i,-4.0 - 4.0i,3.0 - 2.0i,-6.0 + 7.0i,-9.0 + 4.0i; "
				+ "-2.0 + 9.0i,-9.0 - 7.0i,-9.0i,3.0 + 1.0i,2.0 - 3.0i,3.0 - 10.0i,-2.0,7.0 - 8.0i,5.0 - 2.0i,-3.0 - 10.0i; "
				+ "-1.0 - 7.0i,-4.0 - 5.0i,4.0 + 1.0i,-9.0 + 7.0i,9.0 + 7.0i,-8.0 - 1.0i,3.0 - 7.0i,-3.0 + 7.0i,3.0,3.0 + 3.0i; "
				+ "8.0 + 4.0i,-5.0 - 3.0i,-5.0 + 5.0i,-8.0 - 8.0i,9.0 - 7.0i,-7.0 + 7.0i,-8.0 + 6.0i,-1.0 - 8.0i,-6.0 - 6.0i,-10.0 - 8.0i");
		showResults(fMatrix);

		fMatrix = new MatrixComplex(2,3);
		fMatrix.initMatrixRandomRecInt(10);
		showResults(fMatrix);

		fMatrix = new MatrixComplex("5,2,-1,2;1,-3,2,-5;2,3,-1,1");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("1,1,-1,;2,-3,-1;2,2,-3");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("3,-0.2,-0.5,8;0.1,7,0.4,-19.5;0.4,-0.1,10,72.4");
		showResults(fMatrix);

		fMatrix = new MatrixComplex(8,9);
		fMatrix.initMatrixRandomRecInt(9);
		showResults(fMatrix);

		fMatrix = new MatrixComplex("2,18,0;1,9,0");
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

		fMatrix = new MatrixComplex("2,-3,0,-9;0,4,0,16;0,0,0,0");
		showResults(fMatrix);

		fMatrix = new MatrixComplex("4,-3,-3,-6i;4i,-2,3,0;3,-4,-9,4i");
		showResults(fMatrix);
	}
}
