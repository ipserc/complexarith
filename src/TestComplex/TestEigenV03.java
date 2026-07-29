package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV03 {

	public static void doEigenCalculations(MatrixComplex aMatrix) {
		Complex seed = new Complex(1,0);
		Eigenspace eigenSpace = new Eigenspace(seed, aMatrix);
     	int boxSize = 65;

       	System.out.println("");
       	System.out.println(Complex.boxTitleRandom(boxSize, "EIGENVALUES & EIGENVECTORS TEST"));
    	aMatrix.println("aMatrix");
       	System.out.println("MatrixComplex :"+aMatrix.toMatrixComplex());

       	/* EigenVectors Expressions in other languages */
    	System.out.println(Complex.boxTextRandom(boxSize, "EigenVectors Expressions in other languages"));
    	System.out.println("Maxima:"+eigenSpace.toMaxima_eigenvalues(true));
    	System.out.println("Maxima:"+eigenSpace.toMaxima_eigenvectors(true));
    	System.out.println("Maxima:"+eigenSpace.toMaxima_charpoly(true));
    	System.out.println("Octave:"+eigenSpace.toOctave_eigenvectors());
    	System.out.println("Wolfram:"+eigenSpace.toWolfram_eigenvectors());
    	System.out.println("Wolfram:"+eigenSpace.toWolfram_eigensystem());

    	System.out.println(Complex.boxTextRandom(boxSize, "Determinant, Triangle & Characteristic polynomial"));
    	aMatrix.determinant().println("Determinant:");
    	aMatrix.triangle().heap().println("triangle:");
    	eigenSpace.getCharactPoly().println("Characteristic polynom:");

    	/* EigenValues Calculated & Multiplicities */
    	System.out.println(Complex.boxTextRandom(boxSize, "EigenValues Calculated  & Multiplicities"));
    	{
	    	for (int i = 0; i < eigenSpace.eigenvalues().rows(); ++i) {
	    		Complex eVal = eigenSpace.eigenvalues().getItem(i,0);
				System.out.println("EigenValue: " + eVal.toString() + 
						" - arith mult:" + eigenSpace.arithmeticMultiplicity(eVal) + 
						" - geom mult:" + eigenSpace.geometricMultiplicity(eVal));    		
	    	}
    		
    	}
    	
    	/* Characteristics Equations */
    	System.out.println(Complex.boxTextRandom(boxSize, "Characteristics Equations"));
       	System.out.println(Complex.boxTextRandom(boxSize, "MATRIXCOMPLEX"));
    	eigenSpace.printCharactEq(outputFormat.MATRIXCOMPLEX, true);
       	System.out.println(Complex.boxTextRandom(boxSize, "MAXIMA"));
    	eigenSpace.printCharactEq(outputFormat.MAXIMA, true);
    	System.out.println(Complex.boxTextRandom(boxSize, "OCTAVE"));
    	eigenSpace.printCharactEq(outputFormat.OCTAVE, true);
    	System.out.println(Complex.boxTextRandom(boxSize, "WOLFRAM"));
    	eigenSpace.printCharactEq(outputFormat.WOLFRAM, true);
 
    	/* roots & solutions */
    	System.out.println(Complex.boxTextRandom(boxSize, "Roots & Solutions "));
    	for (int row = 0; row < eigenSpace.solutions().rows(); ++row){
    		System.out.println("root: " + eigenSpace.root(row).getItem(0,0).toString() + " - solution: " + eigenSpace.solution(row).getRow(0) + " - Is eigenvector: " + (eigenSpace.solution(row).isNull() ? "No" : "Yes"));
    	}
    	
    	/* EigenVectors Calculated */
    	System.out.println(Complex.boxTextRandom(boxSize, "EigenVectors Calculated"));
    	eigenSpace.eigenvectors().println("EigenVectors:");
    	if(!eigenSpace.eigenvectors().isEmpty() && eigenSpace.eigenvectors().isSquare()) System.out.println("EigenVectors - Determinant:"+eigenSpace.eigenvectors().determinant());
    	else System.out.println("EigenVectors - Determinant: dosen't exist.");
   	
    	/* Check Eigenvectors */
    	eigenSpace.checkEigenvectors();

    	/* Diagonalize Section if possible */
     	Diagfactor diagonal = new Diagfactor(aMatrix);
    	if (diagonal.factorized()) {
        	System.out.println(Complex.boxTextRandom(boxSize, "IS DIAGONALIZABLE"));
         	diagonal.D().println("Matriz Diagonal (D):");
    	    diagonal.P().println("Matriz Valores Propios (P):");
    	    diagonal.P().times(diagonal.D()).times(diagonal.P().inverse()).println("P·D·P⁻¹:");
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix = new MatrixComplex();

     	Complex.setFormatON();
     	Complex.setFixedON(3);
     	Eigenspace.version();

     	/* */
     	aMatrix = new MatrixComplex(""+
     			"  4.000 , -1.000 ,  1.000 ;" +
     			"  0.000 ,  1.000 ,  3.000 ;" +
     			"  0.000 ,  2.000 ,  2.000 ");
     	doEigenCalculations(aMatrix);
     	/* */

     	/* */
     	aMatrix = new MatrixComplex(""+
     			"  4.100 , -1.000 ,  1.100 ;" +
     			"  0.000 ,  1.000 ,  3.000 ;" +
     			"  0.100 ,  2.000 ,  2.000 ");
     	doEigenCalculations(aMatrix);
     	/* */
     	
     	/* */
     	aMatrix = new MatrixComplex(""+
     			"  5 , -2 , -3 ;" +
     			"  2 ,  0 , -2 ;" +
     			"  3 , -2 , -1 ");
     	doEigenCalculations(aMatrix);
     	/* */
     	
     	/* */
     	aMatrix = new MatrixComplex(""+
     			"  1 , -1 ,  2 ;" +
     			" -1 ,  1 , -2 ;" +
     			"  2 , -2 ,  4 ");
     	doEigenCalculations(aMatrix);
     	/* */
     	
     	/* */
     	aMatrix = new MatrixComplex(""+
     			"  4 , -1 ,  6 ;" +
     			"  2 ,  1 ,  6 ;" +
     			"  2 , -1 ,  8 ");
     	doEigenCalculations(aMatrix);
     	/* */
     	
     	/* */
     	aMatrix = new MatrixComplex(""+
     			"  1 ,  3 ,  3 ;" +
     			" -3 , -5 , -3 ;" +
     			"  3 ,  3 ,  1 ");
     	doEigenCalculations(aMatrix);
     	/* */
     	
     	/* */
     	aMatrix = new MatrixComplex(""+
     			"  2 ,  4 ,  3 ;" +
     			" -4 , -6 , -3 ;" +
     			"  3 ,  3 ,  1 ");
     	doEigenCalculations(aMatrix);
     	/* */
     	
     	/* */
     	aMatrix = new MatrixComplex(""+
     			"  2 ,  4 ,  3 ;" +
     			" -4 , -6 , -3 ;" +
     			" -3 ,  7 ,  1 ");
     	doEigenCalculations(aMatrix);
     	/* */
	}
}
