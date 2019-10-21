package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV2 {

	public static void doEigenCalculations(MatrixComplex aMatrix) {
    	MatrixComplex eigenVal;
    	MatrixComplex eigenVectors;  
    	MatrixComplex eigenVect;
    	final String Header = new String("TEV2 --- "); 

    	System.out.println("__________________________________________________________________________________________");
    	System.out.println("____________________________ CALCULO AUTOVALORES/AUTOVECTORES ____________________________");
    	aMatrix.println(Header + "aMatrix");
    	System.out.println(Header + "Maxima:\n"+aMatrix.toMaxima());
    	System.out.println(Header + "Wolfram:\n"+aMatrix.toWolfram());
    	aMatrix.determinant().println(Header + "Det[aMatrix]:");
    	aMatrix.triangle().heap().println("---------------- triangle:");
    	
    	eigenVal = aMatrix.eigenvalues();
    	eigenVal.quicksort(0);
    	eigenVal.println(Header + "**************** eigenVal ****************");
    	eigenVectors = aMatrix.eigenvectors(eigenVal);
    	aMatrix.charactPoly().println(Header + "Characteristic polynom");
    	//aMatrix.charactPoly().plotReIm(-10, 10);
    	eigenVectors.println(Header + "**************** eigenVectors ****************");
    	
//    	if (true) return;
    	
    	int colLen = aMatrix.cols(); //complexMatrix[0].length;
    	eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < eigenVal.complexMatrix.length; ++eigv) {
	    	for(int col = 0; col < colLen; ++col) 
	    		eigenVect.complexMatrix[0][col] = eigenVectors.complexMatrix[eigv][col];
	    	//eigenVect.divides(eigenVect.complexMatrix[0][0]).println(Header + "Norm eigenVect "+eigv);
	    	eigenVect.println(Header + "**************** eigenVect ****************");
	    	aMatrix.times(eigenVect.transpose()).transpose().println(Header + "aMatrix·eigenVect "+eigv);
	    	eigenVect.times(eigenVal.complexMatrix[eigv][0]).println(Header + "eigval["+eigv+"]·eigenVect "+eigv);
    	}

    	eigenVectors.adjoint().times(eigenVectors).println(Header + "eVT·eV");
    	/*
    	System.out.print("press any key");
    	try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    	
    	Diagfactor diagonal = new Diagfactor(aMatrix);
    	diagonal.diagonalize();
    	diagonal.D().println(Header + "Matriz Diagonal");
    	diagonal.P().println(Header + "Matriz Valores Propios");
    	diagonal.P().times(diagonal.D()).times(diagonal.P().inverse()).println(Header + "P·D·P⁻¹");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix = new MatrixComplex();

     	Complex.setFormatON();
     	Complex.setFixedON(3);
/*
     	aMatrix = new MatrixComplex(""+
     			" -1.000 ,  1.000 , -1.000 ;"+
     			" -1.000 , -1.000 ,  1.000 ;" +
     			"  1.000 , -1.000 ,  1.000 ");
     	doEigenCalculations(aMatrix);

     	aMatrix = new MatrixComplex(""+
     			" -1.000 ,  1.000 , -1.000 ;"+
     			" -1.000 , -1.000 ,  1.000 ;" +
     			"  1.000 , -1.000 , -1.000 ");
     	doEigenCalculations(aMatrix);

     	aMatrix = new MatrixComplex();
     	aMatrix.initMatrixRandomImagInt();
     	doEigenCalculations(aMatrix);
*/
     	aMatrix = new MatrixComplex(""+
     			"  1.000 ,  1.000 ,  1.000 ;"+
     			" -1.000 , -1.000 , -1.000 ;" +
     			"  1.000 ,  1.000 , -1.000 ");
     	aMatrix = new MatrixComplex(""+
     			"  1.000i ,  1.000i ,  1.000i ;"+
     			" -1.000i , -1.000i , -1.000i ;" +
     			"  1.000i ,  1.000i , -1.000i ");
     	aMatrix = new MatrixComplex(""+
     			"  1.000 ,  2.000 ,  4.000 ;"+
     			"  2.000 ,  1.000 , -4.000 ;" +
     			"  0.000 ,  0.000 ,  3.000 ");
     	aMatrix = new MatrixComplex(""+
     			"  1.000 ,  1.001 ,  1.000 ;"+
     			" -1.000 , -1.000 , -1.000 ;" +
     			"  1.000 ,  1.000 , -1.000 ");
  	doEigenCalculations(aMatrix);
	}
}
