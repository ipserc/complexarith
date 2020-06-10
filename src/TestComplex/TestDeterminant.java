package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class TestDeterminant {
	public static void showResults(MatrixComplex fMatrix) {
    	MatrixComplex hMatrix;
    	MatrixComplex iMatrix;

       	System.out.println("");
       	System.out.println("========================================================================================");
       	fMatrix.println("------------ Original Matrix ------------");
       	fMatrix.triangle().println("------------ fMatrix triangle ------------");
       	showResultDeterminant(fMatrix);
    	iMatrix = fMatrix.inverse();
       	iMatrix.println("------------ fMatrix inverse ------------");
       	showResultDeterminant(iMatrix); 
       	fMatrix.times(iMatrix).println("Original * Inverse Matrix");
       	System.out.println("========================================================================================");
	}
	
	private static void showResultDeterminant(MatrixComplex fMatrix) {
		long startTime, duration;
    	Complex.setFormatOFF();
    	Complex.setFixedOFF();
       	startTime = System.currentTimeMillis();
		System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString());
      	duration = System.currentTimeMillis() - startTime;
    	System.out.println("El cálculo del determinante ha llevado " + duration + "ms");
    	if (fMatrix.dim() < 800) {
           	startTime = System.currentTimeMillis();
    	   	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString());
    	   	duration = System.currentTimeMillis() - startTime;
        	System.out.println("El cálculo del determinante ha llevado " + duration + "ms");
    	}
       	Complex.setFormatON();
    	Complex.setFixedON(3);
	}
	
    public static void main(String[] args) {
    	MatrixComplex fMatrix;
    	MatrixComplex hMatrix;

    	Complex.setFixedON(3);

    	fMatrix = new MatrixComplex("2+i,3i,-4;5,-1-i,3-i;2i,-1,4-7i");
    	showResults(fMatrix);


       	fMatrix = new MatrixComplex("1,3+2i,3,i;3i,2,i,0;2,1-i,3,4;2-i,3,4,1-5i");
    	showResults(fMatrix);

       	fMatrix = new MatrixComplex("1,2,3,4i;1,5,3,7;2,4,8,6;9,7,7,6");
    	showResults(fMatrix);

       	fMatrix = new MatrixComplex("1,2,i,0;-i,2,1,0;0,2-i,3,5;2,1+3i,0,4");
    	showResults(fMatrix);

       	fMatrix = new MatrixComplex("1,2;i,1");
    	showResults(fMatrix);
       	
       	fMatrix = new MatrixComplex("1,2,-2;-3,4,1;3,0,2");
    	showResults(fMatrix);

       	hMatrix = fMatrix.cofactors(0, 0);
    	showResults(fMatrix);
       	
       	fMatrix = new MatrixComplex("3,5,1;2,-1,0;-1,3,1");
    	showResults(fMatrix);

       	fMatrix = new MatrixComplex("1,1,2+i;0,1,i;1,0,0");
    	showResults(fMatrix);

       	//MatrixComplex fMatrix = new MatrixComplex("0,1,2+i;0,0,i;1,i,0");
    	//MatrixComplex fMatrix = new MatrixComplex("0,1,2+i;1,0,i;1,0,0");
    	//MatrixComplex fMatrix = new MatrixComplex("0,1,2+i;-i,0,i;1,i,0");

       	fMatrix = new MatrixComplex("0,2-i,1,-i;0,0,1+i,-3;-i,0,i,1;1-i,2-3i,4,0");
    	showResults(fMatrix);

       	fMatrix = new MatrixComplex("i,2,1;0,0,1;1,0,i");
    	hMatrix = new MatrixComplex("0,1,2+i;1,0,i;1,0,0");
       	(fMatrix.divides(hMatrix)).println("fMatrix / hMatrix");
       	
    	fMatrix = new MatrixComplex("2,2,1;0,0,1;1,0,1");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("2,2,1;-3,3,7;1,0,1");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("2,1;1,3");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("2,1,1,2;1,3,1,3;2,1,3,4;0,3,1,2");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("1,1,2;0,5,1;1,1,1");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("1,1i,2;0,3+2i,1;1,1,i");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("3,2,3,4;4,4,3,2;1,4,4,3;2,3,1,1");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("1,2,3;4,5,6;1,7,7");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("1,-2,3,2,-1;2,0,1,4,-2;-3,-1,0,-1,2;-1,2,3,2,4;2,-1,2,3,5");
    	showResults(fMatrix);
    	
    	fMatrix = new MatrixComplex("5,2,-1;1,-3,2;2,3,-1");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("5,2,-1;0,17,-11;2,3,-1");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("5,2,-1;0,17,-11;0,-11,3");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex("5,2,-1;1,-3,2;2,3,-1");
    	showResults(fMatrix);

       	hMatrix = fMatrix.copy();
      	hMatrix.swapRows(0,1);
      	System.out.println("Swap 0-1 Matrix");
    	showResults(hMatrix);

      	hMatrix = fMatrix.copy();
      	hMatrix.swapRows(0,2);
      	System.out.println("Swap 0-2 Matrix");
    	showResults(hMatrix);

      	//hMatrix = fMatrix.copy();
      	hMatrix.swapRows(1,2);
      	hMatrix.println("Swap 0-2 1-2 Matrix");
    	showResults(fMatrix);

    	showResults(fMatrix);
       	hMatrix = fMatrix.triangle();
    	showResults(hMatrix);

    	fMatrix = new MatrixComplex("3,2,3,4;4,4,3,2;1,4,4,3;2,3,1,1");
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex(" -5.0 , 3.0 , -1.0 ; -1.0 , -9.0 , -8.0 ; -7.0 , -5.0 , -5.0 ");
    	showResults(fMatrix);

       	fMatrix = new MatrixComplex(10,10);
       	fMatrix.initMatrixRandomRec(10);
    	showResults(fMatrix);

       	fMatrix = new MatrixComplex(10,10);
       	fMatrix.initMatrixRandomInteger(10);
    	showResults(fMatrix);

    	fMatrix = new MatrixComplex(11,11);
       	fMatrix.initMatrixRandomInteger(10);
    	showResults(fMatrix);
    }

}
