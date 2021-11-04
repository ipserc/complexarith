package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vector.*;

public class TestNorm01 {

	public static void calcNorms(MatrixComplex arrayC) {
		int p;
		int boxSize = 65;

		System.out.println(Complex.boxTextRandom(boxSize, "Calculating Norms"));
       	arrayC.println("fMatrix = Original Matrix");
       	p = 1; System.out.println("Norm " + p +  " = " + arrayC.p_norm(p));
       	p = 2; System.out.println("Norm " + p +  " = " + arrayC.p_norm(p));
       	p = 3; System.out.println("Norm " + p +  " = " + arrayC.p_norm(p));
       	p = 4; System.out.println("Norm " + p +  " = " + arrayC.p_norm(p));
       	p = 66; System.out.println("Norm " + p +  " = " + arrayC.p_norm(p));
       	p = 100; System.out.println("Norm " + p +  " = " + arrayC.p_norm(p));
       	System.out.println("Infinity Norm = " + arrayC.inf_norm());
       	System.out.println("Euclidean Norm = " + arrayC.euc_norm());
       	System.out.println("Frobenius Norm = " + arrayC.f_norm());
 		
	}
	public static void main(String[] args) {
    	MatrixComplex fMatrix;
    	Vector fVector;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "MATRIX NORMS TEST"));
    	
       	fMatrix = new MatrixComplex("1,i,1,1-i;1,-2+i,3-i,2;1,0,-i,5");
       	calcNorms(fMatrix);
       	fMatrix = fMatrix.transpose();
       	calcNorms(fMatrix);
       	
       	fMatrix = new MatrixComplex(
       			  " 1, 0,   1, 1, 3;"
       			+ " 1,-2,   3, 2,-4;"
       			+ " 1,-0.7, 1, 5, 2;"
       			+ " 2, 3,   0, 4,-3");
       	calcNorms(fMatrix);
       	fMatrix = fMatrix.transpose();
       	calcNorms(fMatrix);
       	
       	fMatrix = new MatrixComplex("2,1,1,1;6,2,1,-1;-2,2,1,7");
       	calcNorms(fMatrix);
       	fMatrix = fMatrix.transpose();
       	calcNorms(fMatrix);
       	
       	fMatrix = new MatrixComplex("1+i,0,i,1;1,-2,3i,2-i;1,-1,1i,5;2i,3,0,4");
       	calcNorms(fMatrix);

       	fMatrix = new MatrixComplex(4,5);
       	fMatrix.initMatrixRandomRec();
       	calcNorms(fMatrix);
       	fMatrix = fMatrix.transpose();
       	calcNorms(fMatrix);
       	
       	fMatrix = new MatrixComplex(10,11);
       	fMatrix.initMatrixRandomRecInt(10);
       	calcNorms(fMatrix);
       	
       	fMatrix = new MatrixComplex("5,-4,2;-1,2,3;-2,1,0");
       	calcNorms(fMatrix);

       	fMatrix = new MatrixComplex("2,-8;3,1");
       	calcNorms(fMatrix);

       	fMatrix = new MatrixComplex("3,6,-1;3,1,0;2,4,-7");
       	calcNorms(fMatrix);

       	fMatrix = new MatrixComplex("1,7,3;4,-2,-2;-2,-1,1");
       	calcNorms(fMatrix);

    	fMatrix = new MatrixComplex(5);
    	fMatrix.initMatrixRandomRecInt(9);
       	calcNorms(fMatrix);

       	fMatrix = new MatrixComplex("3,-1,4;-5,0,2;1,-2,6");
       	calcNorms(fMatrix);
       	fMatrix = fMatrix.transpose();
       	calcNorms(fMatrix);

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR NORMS TEST"));

		fVector = new Vector("2,-4,1");
       	//fVector.println("fVector");
       	calcNorms(fVector);

       	fVector = new Vector("1,2,3,4");
       	//fVector.println("fVector");
       	calcNorms(fVector);

       	fVector = new Vector("1,i,1,1-i");
       	//fVector.println("fVector");
       	calcNorms(fVector);
       	
	}

}
