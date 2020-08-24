package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV7 {

	public static void doEigenCalculations(MatrixComplex aMatrix) {
    	MatrixComplex eigenVect;
    	final String Header = new String("TEV --- "); 
    	Complex seed = new Complex(1,0);
		Eigenspace eigenspace = new Eigenspace(seed, aMatrix);
		Complex eigenVal;

    	System.out.println("__________________________________________________________________________________________");
    	System.out.println("____________________________ CALCULO AUTOVALORES/AUTOVECTORES ____________________________");
    	aMatrix.println(Header + "aMatrix");
    	System.out.println(Header + "Maxima:\n"+aMatrix.toMaxima());
    	System.out.println(Header + "Wolfram:\n"+aMatrix.toWolfram());
    	aMatrix.determinant().println(Header + "Det[aMatrix]:");
    	eigenspace.getCharactPoly().println(Header + "Characteristic polynom");
    	//aMatrix.charactPoly().plotReIm(-10, 10);
    	eigenspace.values().println(Header + "eigenVal");
    	eigenspace.vectors().println(Header + "eigenVectors");

    	int colLen = aMatrix.complexMatrix[0].length;
    	eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < eigenspace.values().rows(); ++eigv) {
	    	for(int col = 0; col < colLen; ++col) 
	    		eigenVect.complexMatrix[0][col] = eigenspace.vectors().complexMatrix[eigv][col];
	    	//eigenVect.divides(eigenVect.complexMatrix[0][0]).println(Header + "Norm eigenVect "+eigv);
	    	eigenVect.println(Header + "**************** \n" + Header + "eigenVect:");
	    	eigenVal = eigenspace.values().getItem(eigv,0);
	    	eigenVal.println(Header + "eigenVal:");
	    	eigenVal = eigenspace.values().getItem(eigv,0);
	    	aMatrix.times(eigenVect.transpose()).transpose().println(Header + "aMatrix·eigenVect "+eigv+":");
	    	eigenVect.times(eigenVal).println(Header + "eigval["+eigv+"]·eigenVect:");
    	}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix;

     	Complex.setFormatON();
     	Complex.setFixedON(3);
     	aMatrix = new MatrixComplex(3); aMatrix.initMatrixRandomInteger(3) ;
     	aMatrix = aMatrix.hermitian().divides(2);
     	doEigenCalculations(aMatrix);
     	
	}
}
