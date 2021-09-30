package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.Syseq;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV10 {

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
    		eigenSpace.values().println("Eigenvalues Array");
    		/*
    		 * Get the 1st eigenvalue
    		 * keep it to compare with the next one, if is the same one jump to the next inside the loop
    		 */
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
       	System.out.println(Complex.boxTextRandom(boxSize, "WOLFRAM"));
    	eigenSpace.printCharactEq(outputFormat.WOLFRAM, true);
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
       	System.out.println(Complex.boxTextRandom(boxSize, "-- END EIGENVALUES & EIGENVECTORS TEST END --"));
       	System.out.println("");
     	{
     		// Forma de Jordan de los autovalores
     		/*
     		MatrixComplex jMatrix = new MatrixComplex(""
     				+ "1,1,0;"
     				+ "0,1,0;"
     				+ "0,0,2");
     		*/
     	   	for (int i = 0; i < eigenSpace.values().rows(); ++i) {
     	   		int arithMult = eigenSpace.arithmeticMultiplicity(eigenSpace.values().getItem(i,0));
     	   		int geomMult = eigenSpace.geometricMultiplicity(eigenSpace.values().getItem(i,0));
     	   		if ( arithMult > 1) {
     	   			
     	   		}
     		
     		MatrixComplex CI_matrix = (aMatrix.minus(aMatrix.eye())).power(2).augment();
     		Syseq sysCI_matrix = new Syseq(CI_matrix);
     		sysCI_matrix.checkSol(sysCI_matrix.solution());
     		MatrixComplex sol1 = new MatrixComplex(1,1);
     		sol1.complexMatrix[0] = sysCI_matrix.solution().complexMatrix[0].clone();
     		sol1.println("Sol1");
     		
     	   	}
     	}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix;

     	Complex.setFormatON();
     	Complex.setFixedON(3);
     	Eigenspace.version();

     	/*
     	aMatrix = new MatrixComplex(""
     			+ " 3,  1,  1;"
     			+ " 3,  4,  2;"
     			+ "-5,  3, -1"
     			);
     	*/
     	
     	aMatrix = new MatrixComplex(""
     			+ " 0,  2,  2;"
     			+ " 2,  0, -1;"
     			+ "-1, -1,  0"
     			);

     	doEigenCalculations(aMatrix);
     	
	}
}
