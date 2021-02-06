package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV2 {

	public static void doEigenCalculations(MatrixComplex aMatrix) {
		Complex seed = new Complex(1,0);
		Eigenspace eigenSpace = new Eigenspace(seed, aMatrix);
     	MatrixComplex eigenVect;
    	final String Header = new String("TEV5 --- "); 

    	System.out.println("__________________________________________________________________________________________");
    	System.out.println("____________________________ CALCULO AUTOVALORES/AUTOVECTORES ____________________________");
    	aMatrix.println(Header + "aMatrix");
    	System.out.println(Header + "Maxima:\n"+aMatrix.toMaxima());
    	System.out.println(Header + "Maxima:\n"+eigenSpace.Maxima_eigenvalues(true));
    	System.out.println(Header + "Maxima:\n"+eigenSpace.Maxima_eigenvectors(true));
    	System.out.println(Header + "Maxima:\n"+eigenSpace.Maxima_charpoly(true));
    	System.out.println(Header + "Octave:\n"+eigenSpace.Octave_eigenvectors());
    	System.out.println(Header + "Octave:\n"+eigenSpace.Octave_eigenvectors());
    	System.out.println(Header + "Wolfram:\n"+aMatrix.toWolfram());
    	aMatrix.determinant().println(Header + "Det[aMatrix]:");
    	aMatrix.triangle().heap().println("---------------- triangle:");
    	
    	eigenSpace.getCharactPoly().println(Header + "Characteristic polynom");
    	eigenSpace.values().println(Header + "**************** eigenVal ****************");
    	{
	    	Complex eVal = eigenSpace.values().getItem(0,0);
			System.out.println("arith mult[" +  eVal + "]:" + eigenSpace.arithmeticMultiplicity(eVal));    		
	    	for (int i = 0; i < eigenSpace.values().rows(); ++i) {
	    		if (eVal.equalsred(eigenSpace.values().getItem(i,0))) continue;
	    		eVal = eigenSpace.values().getItem(i,0);
	    		System.out.println("arith mult[" +  eVal + "]:" + eigenSpace.arithmeticMultiplicity(eVal));    		
	    	}
    	}

    	//aMatrix.charactPoly().plotReIm(-10, 10);
    	eigenSpace.vectors().println(Header + "**************** eigenVectors ****************");
    	{
	    	Complex eVal = eigenSpace.values().getItem(0,0);
			System.out.println("geom mult(" +  eVal + "):" + eigenSpace.geometricMultiplicity(eVal));    		
	    	for (int i = 0; i < eigenSpace.values().rows(); ++i) {
	    		if (eVal.equalsred(eigenSpace.values().getItem(i,0))) continue;
	    		eVal = eigenSpace.values().getItem(i,0);
	    		System.out.println("geom mult(" +  eVal + "):" + eigenSpace.geometricMultiplicity(eVal));
	    	}
    	}

    	
//    	if (true) return;
    	
    	int colLen = aMatrix.cols(); //complexMatrix[0].length;
    	eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < eigenSpace.values().rows(); ++eigv) {
    		eigenVect.complexMatrix[0] = eigenSpace.vectors().complexMatrix[eigv].clone();
	    	//eigenVect.divides(eigenVect.complexMatrix[0][0]).println(Header + "Norm eigenVect "+eigv);
	    	eigenVect.println(Header + "**************** eigenVect ****************");
	    	aMatrix.times(eigenVect.transpose()).transpose().println(Header + "aMatrix·eigenVect  "+eigv);
	    	eigenVect.times(eigenSpace.values().complexMatrix[eigv][0]).println(Header + "eigval["+eigv+"]·eigenVect"+eigv);
    	}

    	eigenSpace.vectors().adjoint().times(eigenSpace.vectors()).println(Header + "eVT·eV");
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

/* * /
     	aMatrix = new MatrixComplex(""+
     			"  1.000 ,  2.000 ,  4.000 ;" +
     			"  2.000 ,  1.000 , -4.000 ;" +
     			"  0.000 ,  0.000 ,  3.000 ");
     	doEigenCalculations(aMatrix);
/* */
     	
/* */
     	aMatrix = new MatrixComplex(""+
     			"  1.000i ,  1.000i ,  1.000i ;" +
     			" -1.000i , -1.000i , -1.000i ;" +
     			"  1.000i ,  1.000i , -1.000i ");
/* */
     	aMatrix = new MatrixComplex(""+
     			"  1.0000 ,  1.0000 ,  1.00000 ;" +
     			" -1.0000 , -1.0000 , -1.00000 ;" +
     			"  1.0000 ,  1.0000 , -1.00000 ");
     	doEigenCalculations(aMatrix);

/* */
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
/* */
	}
}
