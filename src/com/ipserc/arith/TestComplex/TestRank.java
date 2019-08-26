package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;

public class TestRank {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MatrixComplex aMatrix;
		MatrixComplex bMatrix;
		Polynom cMatrix;
		MatrixComplex dMatrix;

		aMatrix = new MatrixComplex("-2,-1,1,3;-4,-2,2,6;-3,-1,2,3;-5,-3,1,6");
		//aMatrix = new MatrixComplex("-2,-1,1,3;-3,-1,2,3;-5,-3,1,6;-4,-2,-2,6");
		aMatrix.println("aMatrix");
		aMatrix.triangle().println("aMatrix Triang");
		aMatrix.triangleLo().println("aMatrix Triang lo");
		System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("------------------------------------");

		aMatrix = new MatrixComplex("-2,-1,1,3;-4,-2,-2,6;-3,-1,2,3;-5,-3,1,6");
		aMatrix.println("aMatrix");
		aMatrix.triangle().println("aMatrix Triang");
		aMatrix.triangleLo().println("aMatrix TriangLo");
		aMatrix.triangleLo().println("aMatrix Triang lo");
		System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("------------------------------------");

		aMatrix = new MatrixComplex("2,-1,-1,-2; 2,-3,-1, 0; 2, 2, -3, 1; 2, 0, -3, 4");
		aMatrix.println("aMatrix");
		aMatrix.triangle().println("aMatrix Triang");
		aMatrix.triangleLo().println("aMatrix Triang lo");
		System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("------------------------------------");

		aMatrix = new MatrixComplex(" 2, 2, 1; 0, 0, 1; 1, 0, 1");
		aMatrix.println("aMatrix");
		aMatrix.triangle().println("aMatrix Triang");
		aMatrix.triangleLo().println("aMatrix TriangLo");
		aMatrix.triangleLo().println("aMatrix Triang lo");
		System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("------------------------------------");

		aMatrix = new MatrixComplex(" 2,-1,-1,-2; 4,-2, 2,-4; 2, 2,-3, 1; -6, 6,-11, 9");
		aMatrix.println("aMatrix");
		aMatrix.triangle().println("aMatrix Triang");
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("------------------------------------");

		aMatrix = new MatrixComplex(" -1, 3, 0,1; 0, 0,-3, 4; 0, 1, 0,-2; 1, 0, 0, 0");
		aMatrix.println("aMatrix");
		aMatrix.triangle().println("aMatrix Triang");
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		System.out.println("------------------------------------");

		aMatrix = new MatrixComplex(4);
		aMatrix.initMatrixRandomRecInt(9);
		aMatrix.println("aMatrix");
		bMatrix = aMatrix.triangle();
		bMatrix.println("aMatrix Triang");
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		cMatrix = bMatrix.charactPoly();
		cMatrix.println("aMatrix Triang Char Pol");
		dMatrix = cMatrix.solve();
		dMatrix.println("Solutions");
		cMatrix =aMatrix.charactPoly();
		cMatrix.println("aMatrix Char Pol");
		dMatrix = cMatrix.solve();
		dMatrix.println("Solutions");		
		System.out.println("------------------------------------");

		aMatrix = new MatrixComplex(" 0, 0, 1, 0; 0, 0, 0, 1; 1, 0, 0, 0; 0, 1, 0, 0");
		aMatrix.println("aMatrix");
		aMatrix.println("aMatrix");
		bMatrix = aMatrix.triangle();
		bMatrix.println("aMatrix Triang");
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("aMatrix DetAdj:" + aMatrix.determinantAdj());
		System.out.println("aMatrix DetChi:" + aMatrix.determinant());
		cMatrix = bMatrix.charactPoly();
		cMatrix.println("aMatrix Triang Char Pol");
		dMatrix = cMatrix.solve();
		dMatrix.println("Solutions");
		cMatrix =aMatrix.charactPoly();
		cMatrix.println("aMatrix Char Pol");
		dMatrix = cMatrix.solve();
		dMatrix.println("Solutions");		
		System.out.println("------------------------------------");

		aMatrix = new MatrixComplex("-2,-5,8,0,-17;1,3,-5,1,8;3,11,-19,7,1;1,7,-13,5,-3");
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("Nullity(aMatrix):" + aMatrix.nullity());

		aMatrix = new MatrixComplex("-3,6,-1,1,-7;1,-2,2,3,-1;2,-4,5,8,-4");
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("Nullity(aMatrix):" + aMatrix.nullity());
		
		aMatrix = new MatrixComplex("1,3,5,7,9;2,4,6,8,10;3,7,11,15,19;-10,-8,-6,-4,-2");
		System.out.println("Rank(aMatrix):" + aMatrix.rank());
		System.out.println("Nullity(aMatrix):" + aMatrix.nullity());
	}
}
