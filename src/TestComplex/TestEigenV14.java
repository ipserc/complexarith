/*
 * java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes TestComplex.TestEigenV14
 */

package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.Syseq;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV14 {

	public static void doEigenCalculations(MatrixComplex aMatrix, boolean exact) {
		Complex.exact(exact);
		Complex seed = new Complex(1,0);
		Complex.facts();
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
 
    	/* Solutions of the Characteristics Equations */
     	MatrixComplex solution = new MatrixComplex(1,aMatrix.cols());
    	System.out.println(Complex.boxTextRandom(boxSize, "Solutions of the Characteristics Equations"));
    	for (int eigv = 0; eigv < eigenSpace.eigenvalues().rows(); ++eigv) {
    		solution.complexMatrix[0] = eigenSpace.solutions().complexMatrix[eigv].clone();
     		eigenSpace.printCharactEq(eigv, outputFormat.WOLFRAM, false);
	    	solution.println("Solution:");
    	}

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
     	//Complex.setFixedON(3);
     	Eigenspace.version();


     	//aMatrix = new MatrixComplex(3);
     	//aMatrix.initMatrixRandomInteger(6);
     	//aMatrix = new MatrixComplex("-6.000,-2.000,4.000;-5.000,3.000,4.000;-5.000,6.000,-2.000");
     	//aMatrix = new MatrixComplex("4.000,-6.000;5.000,-6.000");
    	// INCorrect EIGEN     	
     	//aMatrix = new MatrixComplex("1.000,1.000,-4.000;4.000,-4.000,-1.000;-2.000,1.000,-2.000");
    	// INCorrect EIGEN
     	//aMatrix = new MatrixComplex("-1.000,5.000,-2.000;-3.000,3.000,4.000;-5.000,6.000,6.000");
     	// INCorrect EIGEN
     	//aMatrix = new MatrixComplex("2.000,-2.000,-1.000;-6.000,-6.000,-1.000;-5.000,-5.000,6.000");
     	// MIX Case INCorrect EIGEN
     	//aMatrix = new MatrixComplex("1.000,-2.000,-3.000;-2.000,5.000,5.000;-1.000,6.000,6.000");
     	//aMatrix = new MatrixComplex("2.0000,5.0000,5.0000;1.0000,-6.0000,1.0000;-3.0000,-6.0000,6.0000");

     	// Correct EIGEN
     	//aMatrix = new MatrixComplex("-2.000,3.000,5.000;-1.000,2.000,5.000;5.000,-5.000,3.000");
     	
     	// INCorrect EIGEN - enviar este ejempplo a Wolfram
     	aMatrix = new MatrixComplex("-2.000,6.000,-6.000;-2.000,-1.000,-3.000;-6.000,3.000,-2.000");

     	// INCorrect EIGEN - enviar este ejempplo a Wolfram
     	//aMatrix = new MatrixComplex("3.0000,-6.0000,-4.0000;-1.0000,-4.0000,4.0000;6.0000,-3.0000,-6.0000");

     	doEigenCalculations(aMatrix, true); //Exact
      	doEigenCalculations(aMatrix, false); //Approx
	}
}
