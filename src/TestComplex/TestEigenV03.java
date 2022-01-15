package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV03 {

	public static void doEigenCalculations(MatrixComplex aMatrix) {
		Complex seed = new Complex(1,0);
		Eigenspace eigenSpace = new Eigenspace(seed, aMatrix);
     	MatrixComplex eigenVect;
     	int boxSize = 65;

       	System.out.println("");
       	System.out.println(Complex.boxTitleRandom(boxSize, "EIGENVALUES & EIGENVECTORS TEST"));
    	aMatrix.println("aMatrix");

    	System.out.println(Complex.boxTextRandom(boxSize, "EigenVectors Expressions"));
    	System.out.println("Maxima:"+eigenSpace.Maxima_eigenvalues(true));
    	System.out.println("Maxima:"+eigenSpace.Maxima_eigenvectors(true));
    	System.out.println("Maxima:"+eigenSpace.Maxima_charpoly(true));
    	System.out.println("Octave:"+eigenSpace.Octave_eigenvectors());
    	System.out.println("Wolfram:"+eigenSpace.Wolfram_eigenvectors());

    	System.out.println(Complex.boxTextRandom(boxSize, "Determinant, Triangle & Characteristic polynomial"));
    	aMatrix.determinant().println("Determinant:");
    	aMatrix.triangle().heap().println("triangle:");
    	eigenSpace.getCharactPoly().println("Characteristic polynom:");

    	System.out.println(Complex.boxTextRandom(boxSize, "EigenValues Calculated"));
    	{
	    	Complex eVal = eigenSpace.values().getItem(0,0);
			System.out.println("EigenValue: " + eVal.toString() + 
					" - arith mult:" + eigenSpace.arithmeticMultiplicity(eVal) + 
					" - geom mult:" + eigenSpace.geometricMultiplicity(eVal));    		
	    	for (int i = 0; i < eigenSpace.values().rows(); ++i) {
	    		if (eVal.equalsred(eigenSpace.values().getItem(i,0))) continue;
	    		eVal = eigenSpace.values().getItem(i,0);
				System.out.println("EigenValue: " + eVal.toString() + 
						" - arith mult:" + eigenSpace.arithmeticMultiplicity(eVal) + 
						" - geom mult:" + eigenSpace.geometricMultiplicity(eVal));    		
	    	}
    	}

    	System.out.println(Complex.boxTextRandom(boxSize, "Characteristics Equations"));
    	System.out.println(Complex.boxTextRandom(boxSize, "OCTAVE"));
    	eigenSpace.printCharactEq(outputFormat.OCTAVE, true);
       	System.out.println(Complex.boxTextRandom(boxSize, "MAXIMA"));
    	eigenSpace.printCharactEq(outputFormat.MAXIMA, true);
       	System.out.println(Complex.boxTextRandom(boxSize, "MATRIXCOMPLEX"));
    	eigenSpace.printCharactEq(outputFormat.MATRIXCOMPLEX, true);
 
    	eigenSpace.vectors().println(Complex.boxTextRandom(boxSize, "EigenVectors Calculated"));
    	System.out.println("EigenVectors - Determinant:"+eigenSpace.vectors().determinant());
   	
    	System.out.println(Complex.boxTextRandom(boxSize, "Check eigenvectors"));
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

    	System.out.println(Complex.boxTextRandom(boxSize, "Some other calculations"));
    	eigenSpace.vectors().adjoint().times(eigenSpace.vectors()).println("eVT·eV:");
    	Diagfactor diagonal = new Diagfactor(aMatrix);
    	if (eigenSpace.isDiagonaizable()) {
        	System.out.println(Complex.boxTextRandom(boxSize, "IS DIAGONALIZABLE"));
        	diagonal.diagonalize();
        	if (diagonal.factorized()) {
    	    	diagonal.D().println("Matriz Diagonal (D):");
    	    	diagonal.P().println("Matriz Valores Propios (P):");
    	    	diagonal.P().times(diagonal.D()).times(diagonal.P().inverse()).println("P·D·P⁻¹:");
        	}
    	}
    	else {
        	System.out.println(Complex.boxTextRandom(boxSize, "IS NOT!!!!!!!!!!!!!! DIAGONALIZABLE"));    		
    	}
    	/*
    	System.out.print("press any key");
    	try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
       	System.out.println(Complex.boxTextRandom(boxSize, "-- END EIGENVALUES & EIGENVECTORS TEST END --"));
    	System.out.println();
    	System.out.println();
    	System.out.println();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix = new MatrixComplex();

     	Complex.setFormatON();
     	Complex.setFixedON(3);
     	Eigenspace.version();

     	aMatrix = new MatrixComplex(""+
     			"  4.000 , -1.000 ,  1.000 ;" +
     			"  0.000 ,  1.000 ,  3.000 ;" +
     			"  0.000 ,  2.000 ,  2.000 ");
     	doEigenCalculations(aMatrix);

     	aMatrix = new MatrixComplex(""+
     			"  4.100 , -1.000 ,  1.100 ;" +
     			"  0.000 ,  1.000 ,  3.000 ;" +
     			"  0.100 ,  2.000 ,  2.000 ");
     	doEigenCalculations(aMatrix);

     	aMatrix = new MatrixComplex(""+
     			"  5 , -2 , -3 ;" +
     			"  2 ,  0 , -2 ;" +
     			"  3 , -2 , -1 ");
     	doEigenCalculations(aMatrix);
     	
     	aMatrix = new MatrixComplex(""+
     			"  1 , -1 ,  2 ;" +
     			" -1 ,  1 , -2 ;" +
     			"  2 , -2 ,  4 ");
     	doEigenCalculations(aMatrix);
     	
     	aMatrix = new MatrixComplex(""+
     			"  4 , -1 ,  6 ;" +
     			"  2 ,  1 ,  6 ;" +
     			"  2 , -1 ,  8 ");
     	doEigenCalculations(aMatrix);

     	aMatrix = new MatrixComplex(""+
     			"  1 ,  3 ,  3 ;" +
     			" -3 , -5 , -3 ;" +
     			"  3 ,  3 ,  1 ");
     	doEigenCalculations(aMatrix);

     	aMatrix = new MatrixComplex(""+
     			"  2 ,  4 ,  3 ;" +
     			" -4 , -6 , -3 ;" +
     			"  3 ,  3 ,  1 ");
     	doEigenCalculations(aMatrix);

     	aMatrix = new MatrixComplex(""+
     			"  2 ,  4 ,  3 ;" +
     			" -4 , -6 , -3 ;" +
     			" -3 ,  7 ,  1 ");
     	doEigenCalculations(aMatrix);
	}
}
