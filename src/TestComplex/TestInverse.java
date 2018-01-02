package TestComplex;

import com.ipserc.arith.matrixcomplex.*;

public class TestInverse {
	public static void showResults(MatrixComplex aMatrix) {
		MatrixComplex inv;
		aMatrix.println("aMatrix");
		//invOld = aMatrix.invertOLD();
		//invOld.println("aMatrix inverse OLD");
		//aMatrix.times(invOld).println("A · invOld");
		inv = aMatrix.inverse();
		inv.println("aMatrix inverse");
		aMatrix.times(inv).println("A · inv");
		
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		if (aMatrix.complexMatrix.length < 10)
			System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
		System.out.println("---------------------------------------");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MatrixComplex aMatrix;

		aMatrix = new MatrixComplex("2,2,1;0,0,1;1,0,1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("2,2,1;1,-2,1;1,1,-1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("2,2,1;-4,-4,-2;1,1,-1");
		showResults(aMatrix);

		aMatrix = new MatrixComplex(40); aMatrix.initMatrixRandomInteger(9);
		showResults(aMatrix);

		aMatrix = new MatrixComplex(6); aMatrix.initMatrixRandomRec(17);
		showResults(aMatrix);
	}

}
