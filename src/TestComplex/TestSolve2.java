package TestComplex;

import com.ipserc.arith.matrixcomplex.*;

public class TestSolve2 {

	private static void showResults(MatrixComplex fMatrix) {
		MatrixComplex dMatrix;
		MatrixComplex gMatrix;
		MatrixComplex hMatrix;

		fMatrix.println("fMatrix = Original Matrix");
		gMatrix = fMatrix.triangleUp();
		gMatrix.println("Triangle");
		dMatrix = fMatrix.triangleLo();
		dMatrix.println("Triangle Lo");
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


		//fMatrix = new MatrixComplex("1,5,-2,12");
		//fMatrix = new MatrixComplex("2,-3,0,-9;0,4,0,16");
		fMatrix = new MatrixComplex("2,-3,0,-9;0,4,0,16;1,5,-2,12");
		showResults(fMatrix);

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("Soluciones a los ejercicios de  http://www.vitutor.com/algebra/sistemas%20I/sg_e.html");
		System.out.println("----------------------------- EJERCICIO 1 -------------------------------------------");
		fMatrix = new MatrixComplex("2,1,1;-1,2,7;3,1,0");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 2 -------------------------------------------");
		fMatrix = new MatrixComplex("2,-1,3,1;3,2,-1,5");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 3 -------------------------------------------");
		fMatrix = new MatrixComplex("1,1,1,1;2,3,-4,9;1,-1,1,-1");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 4 -------------------------------------------");
		fMatrix = new MatrixComplex("3,2,1,1;5,3,4,2;1,1,-1,1");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 5 -------------------------------------------");
		fMatrix = new MatrixComplex("1,-9,5,33;1,3,-1,-9;1,-1,1,5");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 6 -------------------------------------------");
		fMatrix = new MatrixComplex("1,-1,1,1,4;2,1,-3,1,4;1,-2,2,-1,3;1,-3,3,-3,2");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 7 -------------------------------------------");
		fMatrix = new MatrixComplex("1,-2,-2,1,4;1,1,1,-1,5;1,-1,-1,1,6;6,-3,-3,2,32");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 8 -------------------------------------------");
		fMatrix = new MatrixComplex("2,-5,4,1,-1,-3;1,-2,1,-1,1,5;1,-4,6,2,1,10");
		showResults(fMatrix);

		System.out.println("----------------------------- EJERCICIO 9 -------------------------------------------");
		fMatrix = new MatrixComplex("1,1,-1,1;3,2,1,1;5,3,4,2;-2,-1,5,6");
		showResults(fMatrix);

	}
}
