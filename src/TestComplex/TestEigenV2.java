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
    	aMatrix.charactPoly().println(Header + "Characteristic polynom");
    	eigenVal.println(Header + "**************** eigenVal ****************");
    	{
	    	Complex eVal = eigenVal.getItem(0,0);
			System.out.println("arith mult[" +  eVal + "]:" + eigenVal.arithmeticMultiplicity(eVal));    		
	    	for (int i = 0; i < eigenVal.rows(); ++i) {
	    		if (eVal.equalsred(eigenVal.getItem(i,0))) continue;
	    		eVal = eigenVal.getItem(i,0);
	    		System.out.println("arith mult[" +  eVal + "]:" + eigenVal.arithmeticMultiplicity(eVal));    		
	    	}
    	}

    	eigenVectors = aMatrix.eigenvectors(eigenVal);
    	//aMatrix.charactPoly().plotReIm(-10, 10);
    	eigenVectors.println(Header + "**************** eigenVectors ****************");
    	{
	    	Complex eVal = eigenVal.getItem(0,0);
			System.out.println("geom mult(" +  eVal + "):" + eigenVal.geometricMultiplicity(eVal, eigenVectors));    		
	    	for (int i = 0; i < eigenVal.rows(); ++i) {
	    		if (eVal.equalsred(eigenVal.getItem(i,0))) continue;
	    		eVal = eigenVal.getItem(i,0);
	    		System.out.println("geom mult(" +  eVal + "):" + eigenVal.geometricMultiplicity(eVal, eigenVectors));
	    	}
    	}

    	
//    	if (true) return;
    	
    	int colLen = aMatrix.cols(); //complexMatrix[0].length;
    	eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < eigenVal.rows(); ++eigv) {
    		eigenVect.complexMatrix[0] = eigenVectors.complexMatrix[eigv].clone();
	    	//eigenVect.divides(eigenVect.complexMatrix[0][0]).println(Header + "Norm eigenVect "+eigv);
	    	eigenVect.println(Header + "**************** eigenVect ****************");
	    	aMatrix.times(eigenVect.transpose()).transpose().println(Header + "aMatrix·eigenVect_"+eigv);
	    	eigenVect.times(eigenVal.complexMatrix[eigv][0]).println(Header + "eigval["+eigv+"]·eigenVect_"+eigv);
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
     			"  1.000 ,  2.000 ,  4.000 ;" +
     			"  2.000 ,  1.000 , -4.000 ;" +
     			"  0.000 ,  0.000 ,  3.000 ");
     	doEigenCalculations(aMatrix);
     	aMatrix = new MatrixComplex(""+
     			"  1.000i ,  1.000i ,  1.000i ;" +
     			" -1.000i , -1.000i , -1.000i ;" +
     			"  1.000i ,  1.000i , -1.000i ");
     	aMatrix = new MatrixComplex(""+
     			"  1.0000 ,  1.0000 ,  1.00000 ;" +
     			" -1.0000 , -1.0000 , -1.00000 ;" +
     			"  1.0000 ,  1.0000 , -1.00000 ");
     	doEigenCalculations(aMatrix);
     	aMatrix = new MatrixComplex(""+
     			"  1.000 ,  1.000 ,  1.000 ;" +
     			" -1.000 , -1.000 , -1.000 ;" +
     			"  1.000 ,  1.000 , -1.000 ");
     	doEigenCalculations(aMatrix);
     	aMatrix = new MatrixComplex(""+
     			" -1.000 ,  3.000 ,  6.000 , -2.000 ,  3.000;" +
     			"  2.000 , -1.000 , -1.000 ,  2.000 , -1.000;" +
     			" -5.000 , -1.000 , -2.000 , -5.000 , -1.000;" + 
     			"  3.000 ,  0.000 , -1.000 ,  4.000 , -2.000;" +
     			" -1.000 , -1.000 , -2.000 , -5.000 , -2.000");
     	doEigenCalculations(aMatrix);
     	aMatrix = new MatrixComplex(""+
     			"  4.000 , -1.000 ,  1.000 ;" +
     			"  0.000 ,  1.000 ,  3.000 ;" +
     			"  0.000 ,  2.000 ,  2.000 ");
     	doEigenCalculations(aMatrix);

	}
}