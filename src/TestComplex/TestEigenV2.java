package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV2 {

	public static void doEigenCalculations(MatrixComplex aMatrix) {
		Complex seed = new Complex(1,0);
		Eigenspace eigenSpace = new Eigenspace(seed, aMatrix);
     	MatrixComplex eigenVect;

    	System.out.println("__________________________________________________________________________________________");
    	System.out.println("____________________________ CALCULO AUTOVALORES/AUTOVECTORES ____________________________");
    	aMatrix.println("aMatrix");

    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	System.out.println("|                   EigenVectors Expressions                    |");
    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	System.out.println("Maxima:"+eigenSpace.Maxima_eigenvalues(true));
    	System.out.println("Maxima:"+eigenSpace.Maxima_eigenvectors(true));
    	System.out.println("Maxima:"+eigenSpace.Maxima_charpoly(true));
    	System.out.println("Octave:"+eigenSpace.Octave_eigenvectors());
    	System.out.println("Wolfram:"+eigenSpace.Wolfrak_eigenvectors());

    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	System.out.println("|        Determinant, Triangle & Characteristic polynomial      |");
    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	aMatrix.determinant().println("Determinant]:");
    	aMatrix.triangle().heap().println("triangle:");
    	eigenSpace.getCharactPoly().println("Characteristic polynom:");

    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	System.out.println("|                    EigenValues Calculated                     |");
    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	{
	    	Complex eVal = eigenSpace.values().getItem(0,0);
			System.out.println("EigenValue: " + eVal.toString() + " - arith mult:" + eigenSpace.arithmeticMultiplicity(eVal) + " - geom mult:" + eigenSpace.geometricMultiplicity(eVal));    		
	    	for (int i = 0; i < eigenSpace.values().rows(); ++i) {
	    		if (eVal.equalsred(eigenSpace.values().getItem(i,0))) continue;
	    		eVal = eigenSpace.values().getItem(i,0);
				System.out.println("EigenValue: " + eVal.toString() + " - arith mult:" + eigenSpace.arithmeticMultiplicity(eVal) + " - geom mult:" + eigenSpace.geometricMultiplicity(eVal));    		
	    	}
    	}

    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	System.out.println("|                   Chacteristics Equations                     |");
    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	eigenSpace.printCharactEq(outputFormat.WOLFRAM, true);

     	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	System.out.println("|                   EigenVectors Calculated                     |");
    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	eigenSpace.vectors().println();
    	{
	    	Complex eVal = eigenSpace.values().getItem(0,0);
	    	for (int i = 0; i < eigenSpace.values().rows(); ++i) {
	    		if (eVal.equalsred(eigenSpace.values().getItem(i,0))) continue;
	    		eVal = eigenSpace.values().getItem(i,0);
	    	}
    	}

    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	System.out.println("|                      Check eigenvectors                       |");
    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

    	int colLen = aMatrix.cols(); //complexMatrix[0].length;
    	eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < eigenSpace.values().rows(); ++eigv) {
    		eigenVect.complexMatrix[0] = eigenSpace.vectors().complexMatrix[eigv].clone();
    		Complex eigenVal = eigenSpace.values().complexMatrix[eigv][0];
	    	//eigenVect.divides(eigenVect.complexMatrix[0][0]).println("Norm eigenVect "+eigv);
	    	eigenVect.println ("**************** eigenVect:");
	    	System.out.println("                 eigenVal : "+ eigenVal.toString());
	    	aMatrix.times(eigenVect.transpose()).transpose().println("aMatrix·eigenVect  "+eigv);
	    	eigenVect.times(eigenVal).println("eigval["+eigv+"]·eigenVect"+eigv);
    	}

    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	System.out.println("|                   Some other calculations                     |");
    	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	eigenSpace.vectors().adjoint().times(eigenSpace.vectors()).println("eVT·eV:");
    	Diagfactor diagonal = new Diagfactor(aMatrix);
    	diagonal.diagonalize();
    	if (diagonal.factorized()) {
	    	diagonal.D().println("Matriz Diagonal (D):");
	    	diagonal.P().println("Matriz Valores Propios (P):");
	    	diagonal.P().times(diagonal.D()).times(diagonal.P().inverse()).println("P·D·P⁻¹:");
    	}
    	/*
    	System.out.print("press any key");
    	try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
    	
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix = new MatrixComplex();

     	Complex.setFormatON();
     	Complex.setFixedON(3);
     	Eigenspace.version();
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
