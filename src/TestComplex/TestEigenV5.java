package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV5 {

	public static void doEigenCalculations(MatrixComplex aMatrix) {
    	MatrixComplex eigenVal;
    	MatrixComplex eigenVectors;  
    	MatrixComplex eigenVect;
    	final String Header = new String("TEV --- "); 

    	System.out.println("__________________________________________________________________________________________");
    	System.out.println("____________________________ CALCULO AUTOVALORES/AUTOVECTORES ____________________________");
    	aMatrix.println(Header + "aMatrix");
    	System.out.println(Header + "Maxima:\n"+aMatrix.toMaxima());
    	System.out.println(Header + "Wolfram:\n"+aMatrix.toWolfram());
    	aMatrix.determinant().println(Header + "Det[aMatrix]:");
    	eigenVal = aMatrix.eigenvalues();
    	eigenVal.quicksort(0);
    	eigenVectors = aMatrix.eigenvectors(eigenVal);
    	aMatrix.charactPoly().println(Header + "Characteristic polynom");
    	//aMatrix.charactPoly().plotReIm(-10, 10);
    	eigenVal.println(Header + "eigenVal");
    	eigenVectors.println(Header + "eigenVectors");

    	int colLen = aMatrix.complexMatrix[0].length;
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
    	if (diagonal.factorized()) {
	    	diagonal.D().println(Header + "Matriz Diagonal (D)");
	    	diagonal.P().println(Header + "Matriz Valores Propios (P)");
	    	diagonal.P().times(diagonal.D()).times(diagonal.P().inverse()).println(Header + "P·D·P⁻¹");
    	}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix;

     	Complex.setFormatON();
     	Complex.setFixedON(3);
     	aMatrix = new MatrixComplex(
     			  " 3, 2,-1;"
     			+ " 2 ,3, 1;"
     			+ " 0, 0, 5"); 
     	doEigenCalculations(aMatrix);
     	
     	/**/
		MatrixComplex D = new MatrixComplex(
				  " 1, 0, 0;"
				+ " 0, 5, 0;"
				+ " 0, 0, 5");
		
		MatrixComplex P = new MatrixComplex(
				  "-1,-0.5, 1;"
				+ " 1, 0, 1;"
				+ " 0, 1, 0");
		
		P.times(D).times(P.inverse()).println("Matriz");
	/**/
	}
}
