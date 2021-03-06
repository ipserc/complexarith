package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;

public class TestDeterminant {
	public static void showResults(MatrixComplex fMatrix) {
    	MatrixComplex hMatrix;
    	MatrixComplex iMatrix;
    	int boxSize = 65;

       	System.out.println("");
       	System.out.println(Complex.boxTitle2(boxSize, "DETERMINANT TEST"));
       	fMatrix.println(Complex.boxText(boxSize, "Original Matrix"));
       	fMatrix.triangle().println(Complex.boxText(boxSize, "fMatrix triangle"));
       	showResultDeterminant(fMatrix);
    	iMatrix = fMatrix.inverse();
       	iMatrix.println(Complex.boxText3(boxSize, "fMatrix inverse"));
       	showResultDeterminant(iMatrix); 
       	fMatrix.times(iMatrix).println(Complex.boxText2(boxSize, "Original * Inverse Matrix"));
	}
	
	private static void showResultDeterminant(MatrixComplex fMatrix) {
		int dim = (int)Math.sqrt(fMatrix.dim());
		boolean setFormatOff = (dim < 8) ? false : true; 

	   	if (setFormatOff) {
			Complex.storeFormatStatus();
	       	Complex.setFormatOFF();
	    	Complex.setFixedOFF();
		}
       	Chronometer gaussChrono = new Chronometer();
		System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString());
		gaussChrono.stop();
    	System.out.println("El cálculo del determinante ha llevado " + gaussChrono.toString());
    	if (fMatrix.dim() < 800) {
           	Chronometer adjChrono = new Chronometer();
    	   	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString());
    	   	adjChrono.stop();
        	System.out.println("El cálculo del determinante ha llevado " + adjChrono.toString());
    	}
		if (setFormatOff) {
			Complex.restoreFormatStatus();
		}
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
