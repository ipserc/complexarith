package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;


public class testFtrans01 {


	
	public static void main(String[] args) {
		MatrixComplex matrixA;
		MatrixComplex matrixB;		
		int boxSize = 65;

		Complex.setFormatON();
		
       	System.out.println("");
       	System.out.println(Complex.boxTitleRandom(boxSize, "FTRANS TEST"));
		matrixA = new MatrixComplex(5);
		matrixA.initMatrixRandomInteger(1);

		System.out.println(Complex.boxTextRandom(boxSize, "Ftansff"));
		matrixA.println("matrixA");
		matrixB = matrixA.Ftransff(1, "4");
		matrixB.println("Ftansff:matrixA Row 1 * 4");
		
		System.out.println(Complex.boxTextRandom(boxSize, "Ftansf"));
		matrixA.println("matrixA");
		matrixA.Ftransf(1, "4");
		matrixA.println("Ftansf:matrixA Row 1 * 4");

       	System.out.println(Complex.boxTitleRandom(boxSize, "FTRANS swap row1 row2"));

		matrixA = new MatrixComplex(5);
		matrixA.initMatrixRandomInteger(4);

		System.out.println(Complex.boxTextRandom(boxSize, "Ftansff"));
		matrixA.println("matrixA");
		matrixB = matrixA.Ftransff(0, 2);
		matrixB.println("Ftansff:matrixA swap row 0 row 2");
		if (matrixB.rows() == matrixB.cols())  matrixB.determinant().println("matrixB.determinant():");
		
		System.out.println(Complex.boxTextRandom(boxSize, "Ftansf"));
		matrixA.println("matrixA");
		matrixA.Ftransf(0, 2);
		matrixA.println("Ftansf:matrixA swap row 0 row 2");
		if (matrixA.rows() == matrixA.cols()) matrixA.determinant().println("matrixA.determinant():");
		
       	System.out.println(Complex.boxTitleRandom(boxSize, "FTRANS add row time cNum"));
		matrixA = new MatrixComplex(5);
		matrixA.initMatrixRandomInteger(4);

		System.out.println(Complex.boxTextRandom(boxSize, "Ftansff"));
		matrixA.println("matrixA");
		matrixB = matrixA.Ftransff(0, 2, "3");
		matrixB.println("Ftansff:matrixa adds row2*3 to row0");
		if (matrixB.rows() == matrixB.cols())  matrixB.determinant().println("matrixB.determinant():");
		
		System.out.println(Complex.boxTextRandom(boxSize, "Ftansf"));
		matrixA.println("matrixA");
		matrixA.Ftransf(0, 2, "3");
		matrixA.println("Ftansf:matrixA adds row2*3 to row0");
		if (matrixA.rows() == matrixA.cols()) matrixA.determinant().println("matrixA.determinant():");

	}

}
