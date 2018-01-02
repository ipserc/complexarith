package TestComplex;

import com.ipserc.arith.matrixcomplex.*;

public class TestInvertMatrix {
    public static void main(String[] args) {
    	MatrixComplex aMatrix;
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;
    	MatrixComplex fMatrix;
    	MatrixComplex gMatrix;
    	MatrixComplex hMatrix;
    	MatrixComplex iMatrix;

    	fMatrix = new MatrixComplex("1,-2,3,2,-1;2,0,1,4,-2;-3,-1,0,-1,2;-1,2,3,2,4;2,-1,2,3,5");
       	hMatrix = fMatrix.triangle();
       	fMatrix.println("fMatrix = Original Matrix");
       	hMatrix.println("hMatrix = fMatrix triangle");
    	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString() + "\n");
    	System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString() + "\n");
       	hMatrix = fMatrix.triangleLo();
       	hMatrix.println("hMatrix = fMatrix triangle Inf");
       	hMatrix = fMatrix.inverse();
       	hMatrix.println("Inverse Matrix");
       	fMatrix.times(hMatrix).println("Original * Inverse Matrix");
   	
    	fMatrix = new MatrixComplex("1,1i,2;0,3+2i,1;1,1,i");
       	hMatrix = fMatrix.triangle();
       	fMatrix.println("fMatrix = Original Matrix");
       	hMatrix.println("hMatrix = fMatrix triangle");
    	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString() + "\n");
    	System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString() + "\n");
       	hMatrix = fMatrix.triangleLo();
       	hMatrix.println("hMatrix = fMatrix triangle Inf");
       	hMatrix = fMatrix.inverse();
       	hMatrix.println("Inverse Matrix");
       	fMatrix.times(hMatrix).println("Original * Inverse Matrix");

       	fMatrix = new MatrixComplex("0,1,2+i;0,0,i;1,i,0");
       	hMatrix = fMatrix.triangle();
       	fMatrix.println("fMatrix = Original Matrix");
       	hMatrix.println("hMatrix = fMatrix triangle");
    	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString() + "\n");
    	System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString() + "\n");
       	hMatrix = fMatrix.triangleLo();
       	hMatrix.println("hMatrix = fMatrix triangle Inf");
       	hMatrix = fMatrix.inverse();
       	hMatrix.println("Inverse Matrix");
       	fMatrix.times(hMatrix).println("Original * Inverse Matrix");

       	fMatrix = new MatrixComplex("3,1,2;1,2,1;1,3,1");
       	hMatrix = fMatrix.triangle();
       	fMatrix.println("fMatrix = Original Matrix");
       	hMatrix.println("hMatrix = fMatrix triangle");
    	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString() + "\n");
    	System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString() + "\n");
       	hMatrix = fMatrix.triangleLo();
       	hMatrix.println("hMatrix = fMatrix triangle Inf");
       	hMatrix = fMatrix.inverse();
       	hMatrix.println("Inverse Matrix");
       	fMatrix.times(hMatrix).println("Original * Inverse Matrix");

       	fMatrix = new MatrixComplex("1,2,i,0;-i,2,1,0;0,2-i,3,5;2,1+3i,0,4");
       	hMatrix = fMatrix.triangle();
       	fMatrix.println("fMatrix = Original Matrix");
       	hMatrix.println("hMatrix = fMatrix triangle");
    	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString() + "\n");
    	System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString() + "\n");
       	hMatrix = fMatrix.triangleLo();
       	hMatrix.println("hMatrix = fMatrix triangle Inf");
       	hMatrix = fMatrix.inverse();
       	hMatrix.println("Inverse Matrix");
       	fMatrix.times(hMatrix).println("Original * Inverse Matrix");
    }
}
