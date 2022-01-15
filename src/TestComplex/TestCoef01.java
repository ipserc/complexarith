package TestComplex;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;

public class TestCoef01 {

	public static void showResults(MatrixComplex aMatrix) {
		Polynom bMatrix;
		MatrixComplex cMatrix;
		Complex result = new Complex();

		System.out.println(Complex.boxTitleRandom(65, "ROOTS CHARACTERISTIC POLYNOMIAL TEST"));
		System.out.println(Complex.boxTextRandom(65, "aMatrix"));
		aMatrix.println();
		System.out.println(Complex.boxTextRandom(65, "Characteristic polynomial"));
		bMatrix = aMatrix.charactPoly();
		bMatrix.println();
		System.out.println(Complex.boxTextRandom(65, "Check roots"));
		cMatrix = bMatrix.solve();
		for (int i = 0; i < cMatrix.complexMatrix.length; ++i) {
			result = bMatrix.eval(cMatrix.complexMatrix[i][0]);
			System.out.println("CP(" + cMatrix.complexMatrix[i][0] + ")=" + result);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixComplex aMatrix;

		Complex.setFixedON(3);

		//characteristic polynomial[//math:{{-2.0 , -1.0 , 1.0 , 3.0},{-4.0 , -1.0 , 0.0 , 4.0},{-3.0 , -1.0 , 2.0 , 3.0},{-5.0 , -3.0 , 1.0 , 6.0}}//]
		aMatrix = new MatrixComplex("-2,-1,1,3;-4,-1,0,4;-3,-1,2,3;-5,-3,1,6");
		showResults(aMatrix);

		//characteristic polynomial[//math:{{1,-1,-1},{2,-3,-1},{2,2,-3}}//]
		aMatrix = new MatrixComplex("1,-1,-1;2,-3,-1;2,2,-3");
		showResults(aMatrix);

		aMatrix = new MatrixComplex("i,-1,-1;2,-3i,-1;2,2-i,-3");
		showResults(aMatrix);
		
		aMatrix = new MatrixComplex("2,-1,-1,-2; 2,-3,-1, 0; 2, 2, -3, 1; 2, 0, -3, 4");
		showResults(aMatrix);

    	aMatrix = new MatrixComplex(3);aMatrix.initMatrixRandomRecInt(9);
		showResults(aMatrix);

		aMatrix = new MatrixComplex(5);aMatrix.initMatrixRandomRec(9);
		showResults(aMatrix);

    	aMatrix = new MatrixComplex(5);aMatrix.initMatrixRandomInteger(4);
		showResults(aMatrix);
		
    	//aMatrix = new MatrixComplex(8);
    	//aMatrix.initMatrixRandomRecInt(9);
    	//System.out.println(aMatrix.toMatrixComplex());
    	aMatrix = new MatrixComplex("7.0 + 7.0i,-2.0 + 7.0i,1.0 - 3.0i,-6.0 + 3.0i,3.0 + 1.0i,-2.0 - 5.0i,2.0 + 6.0i,5.0i;-7.0,-5.0 + 3.0i,4.0 - 4.0i,-4.0 - 5.0i,-3.0,5.0 + 7.0i,-5.0 + 7.0i,-4.0 + 7.0i;2.0,6.0,5.0,3.0 - 4.0i,3.0 + 1.0i,8.0 + 5.0i,-5.0 + 4.0i,-2.0 - 7.0i;-3.0 + 5.0i,-1.0 - 7.0i,-4.0 + 5.0i,8.0 - 1.0i,2.0 - 4.0i,-4.0 + 1.0i,-9.0 + 8.0i,6.0 - 8.0i;-1.0 + 4.0i,-4.0 + 5.0i,-1.0 + 8.0i,6.0 + 7.0i,6.0 - 3.0i,-9.0 - 3.0i,-8.0 - 9.0i,1.0 + 2.0i;8.0i,-4.0 - 3.0i,2.0 + 2.0i,-5.0 + 3.0i,3.0 - 5.0i,-4.0 + 6.0i,5.0 - 4.0i,-8.0 - 2.0i;7.0 - 5.0i,6.0 + 2.0i,-7.0,-9.0 + 8.0i,-2.0 + 4.0i,-9.0 + 5.0i,-1.0 - 6.0i,-9.0 - 3.0i;-8.0 - 8.0i,8.0 + 7.0i,8.0 + 8.0i,-5.0 + 8.0i,8.0 + 8.0i,-7.0 - 3.0i,7.0,-8.0 - 6.0i");
		aMatrix = aMatrix.divides(10);
    	showResults(aMatrix);
	}

}
