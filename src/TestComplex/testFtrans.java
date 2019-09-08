package TestComplex;

import com.ipserc.arith.matrixcomplex.*;


public class testFtrans {


	
	public static void main(String[] args) {
		MatrixComplex matrixA;
		MatrixComplex matrixB;
		
		System.out.println("_________________________________________________________");
		System.out.println("______________________Ftrans producto____________________");
		
		matrixA = new MatrixComplex(5);
		matrixA.initMatrixRandomInteger(1);

		System.out.println("--------------_Ftansff_---------------");

		matrixA.println("matrixA");
		matrixB = matrixA.Ftransff(1, "4");
		matrixB.println("matrixB * 4");
		
		System.out.println("--------------_Ftansf_----------------");
		
		matrixA.println("matrixA");
		matrixA.Ftransf(1, "4");
		matrixA.println("matrixA * 4");

		System.out.println("_________________________________________________________");
		System.out.println("________________________Ftrans swap______________________");

		matrixA = new MatrixComplex(5);
		matrixA.initMatrixRandomInteger(4);

		System.out.println("--------------_Ftansff_---------------");

		matrixA.println("matrixA");
		matrixB = matrixA.Ftransff(0, 2);
		matrixB.println("matrixB");
		if (matrixB.rows() == matrixB.cols())  matrixB.determinant().println("matrixB.determinant():");
		
		System.out.println("--------------_Ftansf_----------------");

		matrixA.println("matrixA");
		matrixA.Ftransf(0, 2);
		matrixA.println("matrixA");
		if (matrixA.rows() == matrixA.cols()) matrixA.determinant().println("matrixA.determinant():");
		
		System.out.println("_________________________________________________________");
		System.out.println("_________________Ftrans add row time cNum________________");

		matrixA = new MatrixComplex(5);
		matrixA.initMatrixRandomInteger(4);

		System.out.println("--------------_Ftansff_---------------");

		matrixA.println("matrixA");
		matrixB = matrixA.Ftransff(0, 2, "3");
		matrixB.println("matrixB");
		if (matrixB.rows() == matrixB.cols())  matrixB.determinant().println("matrixB.determinant():");
		
		System.out.println("--------------_Ftansf_----------------");

		matrixA.println("matrixA");
		matrixA.Ftransf(0, 2, "3");
		matrixA.println("matrixA");
		if (matrixA.rows() == matrixA.cols()) matrixA.determinant().println("matrixA.determinant():");

	}

}
