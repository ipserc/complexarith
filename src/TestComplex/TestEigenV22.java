/*
 * 
 * clear;runJava.sh eclipse-workspace/complexarith_github/bin/TestComplex/TestEigenV18.class
 * 
 * 
 */
 

package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.factorization.SVDfactor;

public class TestEigenV22 {

	public static boolean doEigenCalculations(MatrixComplex aMatrix) {
		//Complex seed = new Complex(1,0);
		Eigenspace eigenSpace = new Eigenspace(Complex.ONE, aMatrix);

		if (eigenSpace.eigenvectors().dim() != aMatrix.dim()) {
			System.out.println("NO WAY");
			return false;

		}

		Complex.showPrecision();
		Eigenspace.version();

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
    	System.out.println(Complex.boxTextRandom(boxSize, "EigenVectors Calculated" + (Eigenspace.getNormalice() ? " Normalized" : "")));
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
    	    diagonal.P().transpose().println("Matriz Valores Propios Traspuesta (Pŧ):");
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

		return diagonal.factorized();
	}

	/**
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix = new MatrixComplex();
     	int boxSize = 65;

		Complex.numpPadPLUS();
    	Complex.setFormatON();
     	Complex.setFixedON(4);
     	//Complex.setScientificON(4);
 		Eigenspace.setNormalize(true);
		Eigenspace.setOrderDOWN();

		boolean doApprox = false;
		boolean doExact = true;
		boolean found = true;
		int iter = 1;

		/* * /
	    aMatrix = new MatrixComplex(""
	    		+ "+2.0,-3.0,-2.0,+3.0,-1.0;"
	    		+ "+1.0,+2.0,+1.0,-1.0,+1.0;"
	    		+ "-1.0,+1.0,+1.0,+1.0,-1.0;"
	    		+ "+1.0,-3.0,-1.0,+3.0,+1.0;"
	    		+ "-2.0,+2.0,+1.0,-2.0,+1.0");
		/* */
		aMatrix = new MatrixComplex(3); aMatrix.initMatrixRandomInt(99);
		//aMatrix = new MatrixComplex("+46.0000,+91.0000;+89.0000,-59.0000");
		//aMatrix = new MatrixComplex(" 3.000E+00, 2.000E+00,-1.000E+00; 2.000E+00, 3.000E+00, 1.000E+00; 0.000E+00, 0.000E+00, 5.000E+00");
		//aMatrix = new MatrixComplex("11,0,5;0,11,3;2,5,11");
		//aMatrix = new MatrixComplex("-69.0000,-73.0000,+90.0000,+43.0000,+41.0000;-16.0000,+99.0000,+8.0000,+88.0000,-70.0000;-8.0000,+47.0000,+48.0000,-85.0000,-27.0000;+23.0000,-67.0000,-6.0000,+5.0000,+26.0000;+95.0000,-63.0000,+56.0000,-74.0000,-64.0000");
		//aMatrix = aMatrix.normalize();

		do {
			System.out.println("Iter:" + iter++);
			if (doApprox) {
				/* ***********************************************
				*                 APROXIMATED
				*********************************************** */
				System.out.println(Complex.boxTitleRandom(boxSize, "APROXIMATED"));
				Complex.exact(false);
				found = doEigenCalculations(aMatrix);

			}

			if (doExact) {
				System.out.println("");
				/* ***********************************************
				*                    EXACT
				*********************************************** */
				System.out.println(Complex.boxTitleRandom(boxSize, "EXACT"));
				Complex.exact(true);
				found = doEigenCalculations(aMatrix);
			}

			
		} while (!found);
	}
}
