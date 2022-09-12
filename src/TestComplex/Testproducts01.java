package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class Testproducts01 {

	public Testproducts01() {

	}

	public static void main(String[] args) {
		MatrixComplex aMat = new MatrixComplex(1,4);
		MatrixComplex bMat = new MatrixComplex(1,3);
		MatrixComplex resultMat;
		
		aMat.initMatrixRandomInteger(4);
		bMat.initMatrixRandomInteger(4);
		resultMat = aMat.tensorprod(bMat);
		resultMat.println("tensorprod");
		resultMat = aMat.kroneckerprod(bMat);
		resultMat.println("kroneckerprod");
		
		

	}

}
