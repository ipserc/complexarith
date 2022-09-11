/*
 * 
 * clear;runJava.sh eclipse-workspace/complexarith/bin/TestComplex/TestEigenV18.class
 * 
 * */
 

package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.factorization.SVDfactor;

public class TestEigenV18 {

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
    	System.out.println(Complex.boxTextRandom(boxSize, "EigenVectors Calculated Normalized"));
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
	}

	/**
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix = new MatrixComplex();
     	int boxSize = 65;

    	Complex.exact(true);
    	Complex.setFormatON();
     	//Complex.setFixedON(4);
     	//Complex.setScientificON(4);
     	Complex.showPrecision();
     	Eigenspace.version();


	    aMatrix = new MatrixComplex("-1.0,-1.0,1.0;1.0,-3.0,-1.0;-1.0,3.0,-1.0");
	    doEigenCalculations(aMatrix);

	    aMatrix = new MatrixComplex("-1.0,2.0,-1.0;-1.0,2.0,-1.0;-1.0,1.0,-3.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("-3.0,-2.0,3.0;-3.0,-2.0,1.0;4.0,-4.0,2.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("8.0,-6.0,6.0;-7.0,-3.0,7.0;-9.0,2.0,2.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("-1.0,-2.0,2.0;4.0,5.0,-4.0;2.0,1.0,-1.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("2.0,3.0,4.0;4.0,4.0,2.0;-4.0,-4.0,-2.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("-1.0,8.0,8.0;14.0,0.0,1.0;-1.0,4.0,3.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("0.0,10.0,0.0;2.0,1.0,-2.0;10.0,1.0,14.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("-3.0,6.0,9.0,-5.0;5.0,9.0,-1.0,6.0;7.0,2.0,-3.0,5.0;7.0,-8.0,-1.0,1.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("3.0,2.0,-1.0,3.0;-3.0,1.0,2.0,1.0;-1.0,3.0,-2.0,2.0;1.0,-2.0,-1.0,-3.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("3.0,-2.0,3.0,3.0;2.0,3.0,3.0,2.0;-2.0,-3.0,3.0,-2.0;-2.0,2.0,-3.0,-2.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("-1.0,4.0,-4.0,2.0;-3.0,-3.0,-3.0,-3.0;1.0,-2.0,1.0,-2.0;-1.0,-2.0,2.0,-2.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("-5.0,-2.0,5.0,-5.0;3.0,3.0,-1.0,-2.0;2.0,-5.0,4.0,-5.0;3.0,-5.0,-1.0,2.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("4.0,5.0,2.0,1.0;-4.0,-3.0,-2.0,5.0;3.0,1.0,-1.0,-3.0;2.0,1.0,-3.0,2.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("5.0,3.0,-4.0,2.0;4.0,4.0,2.0,-4.0;1.0,3.0,-2.0,-5.0;-1.0,-1.0,1.0,4.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("3.0,-3.0,2.0,-5.0;-2.0,2.0,-3.0,-3.0;4.0,-4.0,-2.0,-2.0;4.0,-4.0,3.0,-1.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("-5.0,1.0,-5.0,-4.0;-1.0,-5.0,3.0,2.0;5.0,3.0,-2.0,2.0;-3.0,-2.0,-1.0,-5.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("-2.0,2.0,-1.0,1.0,-3.0;2.0,-2.0,-1.0,1.0,-3.0;3.0,3.0,1.0,-2.0,-2.0;2.0,3.0,2.0,-3.0,-1.0;-2.0,-1.0,3.0,-3.0,3.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("2.0,-3.0,-2.0,3.0,-1.0;1.0,2.0,1.0,-1.0,1.0;-1.0,1.0,1.0,1.0,-1.0;1.0,-3.0,-1.0,3.0,1.0;-2.0,2.0,1.0,-2.0,1.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("2.0,-1.0,-2.0,-3.0,-3.0;1.0,-3.0,-3.0,-1.0,-3.0;3.0,3.0,3.0,-3.0,3.0;1.0,1.0,-2.0,-2.0,-1.0;2.0,-3.0,1.0,-2.0,-3.0");
	    doEigenCalculations(aMatrix);

	    aMatrix = new MatrixComplex("10.0,5.0,4.0;8.0,3.0,6.0;8.0,5.0,4.0");
	    doEigenCalculations(aMatrix);
	    
	    aMatrix = new MatrixComplex("9.0,8.0,5.0,9.0;9.0,4.0,4.0,6.0;6.0,9.0,9.0,9.0;6.0,5.0,8.0,6.0");
	    doEigenCalculations(aMatrix);

	    aMatrix = new MatrixComplex("-2.0,2.0,1.0,2.0;2.0,3.0,-3.0,-3.0;2.0,-1.0,1.0,-1.0;2.0,2.0,1.0,-2.0");
	    doEigenCalculations(aMatrix);
	    
		aMatrix = new MatrixComplex("2.0,-6.0,10.0;3.0,5.0,11.0;9.0,-9.0,11.0");
		doEigenCalculations(aMatrix);

		aMatrix = new MatrixComplex("1.0,-2.0,-3.0,-2.0,3.0;-3.0,3.0,2.0,3.0,-2.0;2.0,1.0,-3.0,-1.0,1.0;1.0,3.0,-1.0,-3.0,2.0;-2.0,3.0,1.0,-3.0,3.0");
		doEigenCalculations(aMatrix);
		
		aMatrix = new MatrixComplex("-2.0,1.0,-1.0,-2.0,-1.0;1.0,1.0,-3.0,2.0,1.0;-1.0,-1.0,-2.0,-2.0,-1.0;3.0,2.0,-1.0,-1.0,2.0;-2.0,-2.0,1.0,2.0,-2.0");
		doEigenCalculations(aMatrix);

		aMatrix = new MatrixComplex("2.0,-2.0,3.0,1.0,-1.0;-1.0,3.0,3.0,2.0,3.0;2.0,-1.0,-2.0,-1.0,-2.0;1.0,2.0,1.0,3.0,-1.0;-3.0,-1.0,1.0,-2.0,3.0");
		doEigenCalculations(aMatrix);

		aMatrix = new MatrixComplex("-1.0,-2.0,-3.0,2.0,-1.0;2.0,-3.0,1.0,-3.0,-3.0;1.0,-3.0,2.0,-1.0,-2.0;1.0,-3.0,2.0,-2.0,-1.0;1.0,-3.0,-3.0,-1.0,3.0");
		doEigenCalculations(aMatrix);

		aMatrix = new MatrixComplex("-2.0,-1.0,-2.0,-1.0,2.0;3.0,-2.0,-2.0,-2.0,2.0;-1.0,2.0,3.0,2.0,1.0;1.0,-1.0,-2.0,1.0,2.0;2.0,1.0,2.0,1.0,2.0");
		doEigenCalculations(aMatrix);

		aMatrix = new MatrixComplex("-3.0,1.0,-3.0,-1.0,3.0;3.0,3.0,3.0,2.0,-1.0;3.0,2.0,-1.0,3.0,-1.0;1.0,2.0,3.0,-2.0,-3.0;1.0,1.0,-3.0,2.0,3.0");
		doEigenCalculations(aMatrix);

		aMatrix = new MatrixComplex("-3.0,3.0,-1.0,1.0,-3.0;-1.0,-2.0,-1.0,-3.0,-2.0;3.0,-2.0,-2.0,1.0,-3.0;2.0,1.0,-3.0,2.0,2.0;-2.0,1.0,-2.0,-2.0,3.0");		
		doEigenCalculations(aMatrix);

		aMatrix = new MatrixComplex("-1.0,2.0,3.0,-1.0,-3.0;1.0,-1.0,-3.0,-2.0,3.0;-3.0,-2.0,2.0,-3.0,-2.0;-2.0,-2.0,-1.0,-2.0,1.0;-2.0,-2.0,2.0,-2.0,-2.0");
		doEigenCalculations(aMatrix);

		aMatrix = new MatrixComplex("-4.0,1.0,-4.0;-1.0,5.0,-3.0;4.0,-3.0,-5.0");
		doEigenCalculations(aMatrix);
		
		new MatrixComplex("-4.0,-7.0,3.0;2.0,6.0,-7.0;-1.0,7.0,-7.0");
		new MatrixComplex("2.0,14.0,5.0;12.0,-3.0,7.0;-8.0,-11.0,-12.0");
		new MatrixComplex("-12.0,4.0,4.0;13.0,5.0,-12.0;-2.0,-9.0,-13.0");
		new MatrixComplex("1.0,1.0,1.0,-2.0;3.0,4.0,-3.0,-2.0;-2.0,-3.0,4.0,2.0;1.0,2.0,2.0,-1.0");
		
		
		
		/* ***********************************************
		 *                 APROXIMATED
		 *********************************************** */
    	Complex.exact(false);
       	System.out.println("");
       	System.out.println(Complex.boxTitleRandom(boxSize, "APROXIMATED"));
    	Complex.showPrecision();

		aMatrix = new MatrixComplex("-4.0,1.0,-4.0;-1.0,5.0,-3.0;4.0,-3.0,-5.0");
		doEigenCalculations(aMatrix);

		aMatrix = new MatrixComplex("2.0,2.0,3.0,3.0,-1.0;3.0,-3.0,1.0,1.0,-1.0;1.0,-1.0,-2.0,-2.0,-3.0;1.0,-3.0,-3.0,2.0,-3.0;2.0,-1.0,-2.0,2.0,-1.0");
       	doEigenCalculations(aMatrix);

       	aMatrix = new MatrixComplex("1.0,1.0,-2.0,2.0,-2.0,-2.0,3.0,-3.0;-3.0,1.0,3.0,3.0,3.0,-1.0,-2.0,-2.0;2.0,-3.0,-2.0,-3.0,-1.0,1.0,3.0,-3.0;-1.0,-1.0,-2.0,-3.0,-3.0,-3.0,1.0,-2.0;-1.0,2.0,-2.0,-3.0,-2.0,-1.0,3.0,1.0;3.0,-3.0,-2.0,2.0,-2.0,1.0,1.0,2.0;1.0,1.0,1.0,2.0,-3.0,-2.0,-2.0,2.0;-3.0,-1.0,1.0,3.0,3.0,-1.0,-3.0,-1.0");
       	doEigenCalculations(aMatrix);
	   
       	aMatrix = new MatrixComplex("6.0,9.0,6.0,5.0,6.0,4.0,4.0,8.0;9.0,9.0,4.0,5.0,6.0,9.0,5.0,6.0;6.0,5.0,4.0,8.0,6.0,9.0,9.0,9.0;9.0,4.0,9.0,6.0,9.0,6.0,6.0,6.0;10.0,9.0,10.0,6.0,9.0,4.0,9.0,5.0;5.0,6.0,10.0,10.0,9.0,8.0,6.0,10.0;5.0,10.0,9.0,9.0,10.0,8.0,5.0,10.0;9.0,6.0,4.0,9.0,8.0,6.0,9.0,8.0");
		doEigenCalculations(aMatrix);
		
		aMatrix = new MatrixComplex("4.0,4.0,5.0,8.0,10.0,5.0,8.0,10.0,6.0,6.0,5.0;9.0,8.0,8.0,6.0,8.0,8.0,5.0,9.0,6.0,8.0,4.0;5.0,5.0,8.0,6.0,6.0,9.0,6.0,10.0,9.0,8.0,10.0;9.0,10.0,10.0,10.0,4.0,6.0,6.0,10.0,6.0,5.0,10.0;4.0,9.0,4.0,4.0,8.0,10.0,8.0,6.0,10.0,6.0,5.0;10.0,8.0,6.0,9.0,5.0,9.0,8.0,8.0,5.0,8.0,6.0;9.0,4.0,5.0,8.0,9.0,6.0,10.0,9.0,5.0,6.0,10.0;5.0,9.0,10.0,5.0,10.0,9.0,10.0,10.0,6.0,6.0,10.0;9.0,4.0,4.0,8.0,6.0,5.0,10.0,8.0,4.0,6.0,6.0;5.0,6.0,5.0,8.0,4.0,8.0,6.0,6.0,9.0,6.0,8.0;8.0,10.0,5.0,5.0,4.0,9.0,10.0,8.0,6.0,10.0,10.0");
		doEigenCalculations(aMatrix);
		
		aMatrix = new MatrixComplex("-2.0,10.0,5.0,2.0,-10.0,5.0,8.0,-5.0,9.0;-5.0,-10.0,-7.0,7.0,2.0,-11.0,2.0,-3.0,10.0;9.0,-8.0,-7.0,10.0,7.0,-3.0,1.0,7.0,-6.0;-9.0,-2.0,4.0,11.0,9.0,-3.0,10.0,11.0,9.0;8.0,3.0,10.0,7.0,6.0,5.0,-1.0,2.0,-6.0;-1.0,-10.0,9.0,1.0,-10.0,-2.0,-4.0,-11.0,-1.0;-10.0,-2.0,-8.0,-9.0,8.0,-4.0,9.0,3.0,1.0;9.0,8.0,6.0,-4.0,-11.0,11.0,-6.0,1.0,6.0;5.0,-9.0,-5.0,-8.0,8.0,2.0,-4.0,-10.0,-4.0");
		doEigenCalculations(aMatrix);
	 
		aMatrix = new MatrixComplex("-2.0,2.0,3.0,8.0,7.0,-6.0,-9.0,8.0,10.0;-9.0,-7.0,-3.0,11.0,-2.0,-9.0,-4.0,-6.0,9.0;-8.0,-9.0,-10.0,8.0,-7.0,1.0,-5.0,3.0,-3.0;6.0,-4.0,-3.0,7.0,-11.0,5.0,3.0,4.0,-6.0;-5.0,-7.0,5.0,7.0,11.0,2.0,3.0,1.0,4.0;-8.0,-7.0,-1.0,3.0,10.0,-6.0,6.0,-4.0,1.0;-1.0,3.0,-11.0,8.0,3.0,5.0,10.0,5.0,-6.0;-10.0,3.0,4.0,9.0,-8.0,-11.0,-4.0,-1.0,-10.0;-2.0,11.0,-8.0,-1.0,8.0,11.0,6.0,-11.0,4.0");
		doEigenCalculations(aMatrix);

	}
}
