package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestInvertMatrix {
	public static void showResults(MatrixComplex aMatrix) {
    	MatrixComplex bMatrix;

       	System.out.println("--------------------------------------------------------------");
    	bMatrix = aMatrix.triangle();
       	aMatrix.println("a = Original Matrix");
       	bMatrix.println("b = fMatrix triangle");
    	System.out.println("detAdj(a) = " + aMatrix.determinantAdj().toString());
    	System.out.println("detGauss(a) = " + aMatrix.determinantGauss().toString());
       	bMatrix = aMatrix.triangleLo();
       	bMatrix.println("b = a triangle Inf");
       	System.out.println("detGauss(b) = " + bMatrix.determinantGauss().toString());
       	bMatrix = aMatrix.inverse();
       	bMatrix.println("Inverse Matrix");
       	aMatrix.times(bMatrix).println("Original * Inverse Matrix");
       	bMatrix = aMatrix.transpose().cofactor().divides(aMatrix.determinant());
       	bMatrix.println("Inverse Matrix adj(a)/det(a)");
       	bMatrix = aMatrix.adjugate();
       	bMatrix.println("adjugate() a");
       	aMatrix.times(bMatrix).println("a*b");
       	bMatrix = aMatrix.adjoint();
       	bMatrix.println("adjoint() a");
       	aMatrix.plus(bMatrix).println("a + b");
       	aMatrix.times(bMatrix).println("a * b");
       	aMatrix.minus(bMatrix).println("a - b");
       	aMatrix.divides(bMatrix).println("a / b");
       	aMatrix.hermitian().println("Hermitian");
       	aMatrix.skewHermitian().println("skewHermitian");    	
	}

    public static void main(String[] args) {
    	MatrixComplex aMatrix;

    	Complex.setFixedON(3);
    	Complex.setFormatON();
    	
    	aMatrix = new MatrixComplex("1,-2,3,2,-1;2,0,1,4,-2;-3,-1,0,-1,2;-1,2,3,2,4;2,-1,2,3,5");
    	showResults(aMatrix);
    	
    	aMatrix = new MatrixComplex("1,1i,2;0,3+2i,1;1,1,i");
    	showResults(aMatrix);

       	aMatrix = new MatrixComplex("0,1,2+i;0,0,i;1,i,0");
    	showResults(aMatrix);

       	aMatrix = new MatrixComplex("3,1,2;1,2,1;1,3,1");
    	showResults(aMatrix);

       	aMatrix = new MatrixComplex("1,2,i,0;-i,2,1,0;0,2-i,3,5;2,1+3i,0,4");
    	showResults(aMatrix);

       	aMatrix = new MatrixComplex("1+i,2-i,-1-i,2+i;-3-i,2+2i,1-3i,-3+3i;4-2i,-2-i,-3+2i,5-3i;-2+i,1+3i,-4+i,4-i");
    	showResults(aMatrix);
    }
}
