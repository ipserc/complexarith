package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV01 {

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
    	MatrixComplex aMatrix;

     	Complex.setFormatON();
     	Complex.setFixedON(3);
     	Eigenspace.version();
     	/**/
    	aMatrix = new MatrixComplex("2,0;4,3"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex(8); aMatrix.initMatrixRandomRecInt(1); doEigenCalculations(aMatrix);
    	aMatrix = new MatrixComplex("-1,2,1;3,4,-1;1,2,2"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("-1,2,1;1,2,-1;1,2,2"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("1,-1,0;-1,2,-1;0,-1,1"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("-1,2,1;3,-2,1;5,-2,3;6,2,-7"); aMatrix = aMatrix.adjoint().times(aMatrix); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("11,7,-7;7,11,1;-7,1,9"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex(4); aMatrix.initMatrixRandomRecInt(9); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("1,-1,0;9,3,1"); aMatrix = aMatrix.adjoint().times(aMatrix); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("-5,1,7,-15;-2,0,5,-7;1,-5,7,-3;4,-7,3,4");aMatrix = aMatrix.adjoint().times(aMatrix); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("-5,1,7,-14;-2,0,5,-7;1,-5,7,-3;4,-7,3,4"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex(2); aMatrix.initMatrixRandomInteger(9); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("-1.0,0.0,-1.0,0.0;-1.0,0.0,-1.0,-1.0;0.0,0.0,0.0,0.0;0.0,-1.0,-1.0,-1.0"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("3,2-i,-3i;2+i,0,1-i;3i,1+i,0"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("1,2,0;-2,1,2;1,3,1"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("0.000,0.174,0.782,0.263,0.022,0.000,0.000,0.000,0.000,0.000;0.985,0,0,0,0,0,0,0,0,0;0,0.996,0,0,0,0,0,0,0,0;0,0,0.994,0,0,0,0,0,0,0;0,0,0,0.990,0,0,0,0,0,0;0,0,0,0,0.975,0,0,0,0,0;0,0,0,0,0,0.940,0,0,0,0;0,0,0,0,0,0,0.866,0,0,0;0,0,0,0,0,0,0,0.680,0,0;0,0,0,0,0,0,0,0,0.361,0"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex(5); aMatrix.initMatrixRandomRecInt(9); doEigenCalculations(aMatrix);
 /** /
    	aMatrix = new MatrixComplex(""
    			+ " 4.0,-7.0 - 2.0i, 8.0i,5.0 + 3.0i,-7.0 + 1.0i,-5.0 + 7.0i,6.0 - 2.0i,-6.0 - 9.0i,-4.0 - 4.0i,-8.0 - 4.0i;"
    			+ "-1.0 - 2.0i,5.0 + 5.0i,-2.0 + 2.0i,-2.0 + 8.0i,5.0 + 5.0i,-7.0 + 8.0i,-3.0 - 8.0i,-9.0 + 6.0i,-9.0 + 5.0i,-6.0 + 4.0i;"
    			+ " 4.0 + 5.0i,-6.0 + 1.0i,8.0 + 3.0i,-7.0,4.0 - 8.0i,6.0 - 9.0i,-5.0 - 6.0i,-2.0 + 6.0i,3.0 - 7.0i,-9.0 - 1.0i;"
    			+ "-3.0 + 2.0i,5.0i,7.0 - 6.0i,-1.0i,-2.0 + 7.0i,3.0 - 3.0i,6.0 - 8.0i,3.0i,6.0 + 1.0i,5.0 - 5.0i;"
    			+ "-4.0 - 6.0i,-1.0,-3.0 + 8.0i,5.0 + 2.0i,8.0 - 8.0i,7.0 - 5.0i,-8.0 + 6.0i,6.0 - 7.0i,-7.0 - 9.0i,-4.0 - 9.0i;"
    			+ "-2.0 - 2.0i,-4.0 + 5.0i,-4.0 - 7.0i,6.0 - 7.0i,-8.0 + 2.0i,-1.0 - 9.0i,3.0 + 5.0i,-6.0 - 7.0i,2.0 + 8.0i,2.0;"
    			+ "-7.0 - 1.0i,2.0 + 7.0i,1.0 - 4.0i,4.0 - 8.0i,-9.0 + 6.0i,2.0 + 5.0i,-4.0,4.0i,3.0 + 8.0i,6.0;"
    			+ " 4.0 - 9.0i,-7.0i,-1.0 - 6.0i,-1.0 + 1.0i,-2.0 - 3.0i,-5.0 - 3.0i,3.0 - 8.0i,-7.0 + 7.0i,4.0 + 5.0i,-9.0 - 9.0i;"
    			+ " 1.0 - 8.0i,-3.0 + 4.0i,2.0 + 3.0i,-3.0 + 3.0i,-3.0 - 8.0i,6.0i,-1.0 + 1.0i,3.0 - 4.0i,7.0 - 8.0i,-9.0 + 6.0i;"
    			+ " 2.0 + 2.0i,-5.0,8.0 - 7.0i,4.0 + 1.0i,6.0 + 3.0i,4.0 - 7.0i,2.0i,-9.0,-9.0 - 2.0i,5.0 - 5.0i"); 
    	doEigenCalculations(aMatrix);
/**/
    	//aMatrix = new MatrixComplex("3.0,-3.0,-4.0,-2.0;-4.0,-1.0,-1.0,-3.0;0.0,0.0,-3.0,-1.0;-4.0,2.0,-3.0,-3.0"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("-1.0,-1.0,0.0,-1.0;0.0,0.0,0.0,-1.0;-1.0,0.0,0.0,0.0;0.0,-1.0,0.0,0.0"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("7,-1,-1;-1,5,1;-1,1,5"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("1,1;1,0;0,1"); aMatrix = aMatrix.adjoint().times(aMatrix); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("2.000,1.000;1.000,2.000"); doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("43.000,49.000 + 30.000i;49.000 - 30.000i,115.000"); doEigenCalculations(aMatrix);
       	aMatrix = new MatrixComplex(3); aMatrix.initMatrixRandomRec(5); aMatrix = aMatrix.hermitian(); doEigenCalculations(aMatrix);
       	aMatrix = new MatrixComplex(2); aMatrix.initMatrixRandomRec(5); aMatrix = aMatrix.skewHermitian(); doEigenCalculations(aMatrix);
    	Complex.setFixedON(3); doEigenCalculations(aMatrix);
    	Complex.setScientificON(3); doEigenCalculations(aMatrix);
       	Complex.setFormatON(); doEigenCalculations(aMatrix);
    	Complex.setFixedON(3); doEigenCalculations(aMatrix);
    	Complex.setScientificON(3);	doEigenCalculations(aMatrix);
    	aMatrix = new MatrixComplex("3,2,-1;2,3,1;0,0,5"); doEigenCalculations(aMatrix);
    	aMatrix = new MatrixComplex("3,2,-1;2,3,1;1,-2,5"); doEigenCalculations(aMatrix);
/** /
    	Complex.setFixedON(3);
    	aMatrix = new MatrixComplex(""+
    			"-11.0i,-20.0 , 10.0i,  4.0 ,-15.0i,-18.0 , -7.0i,-16.0 ,-10.0i,  2.0 ;"+
    			"  8.0 , -1.0i,  1.0 , 17.0i, -4.0 , 16.0i,-13.0 ,-11.0i,  0.0 ,-17.0i;"+
    			"  4.0i, -7.0 ,-11.0i,  5.0 , 15.0i, 10.0 ,-19.0i, 19.0 , 17.0i,  3.0 ;"+
    			" 15.0 , 11.0i, -6.0 , -5.0i, 17.0 , 19.0i, 11.0 , -4.0i, -8.0 ,  7.0i;"+
    			" -9.0i,  9.0 ,-14.0i,-14.0 ,-18.0i, 14.0 , 19.0i, -6.0 ,  7.0i,  5.0 ;"+
    			" -6.0 , -3.0i,-17.0 ,  9.0i, 19.0 , 10.0i, -8.0 , -4.0i,-11.0 , -1.0i;"+
    			"-20.0i,-18.0 ,-18.0i, 14.0 , -2.0i, -5.0 ,  8.0i, -8.0 ,  0.0i, -7.0 ;"+
    			" -1.0 ,  7.0i, -5.0 , 19.0i, 13.0 , -6.0i,  9.0 ,  0.0i,  0.0 ,  5.0i;"+
    			"-13.0i,-15.0 ,  5.0i,  3.0 , 16.0i, 16.0 , 15.0i, -6.0 ,  0.0i, -3.0 ;"+
    			"  9.0 ,-20.0i,-13.0 , -8.0i,  6.0 ,  2.0i,-18.0 ,-19.0i,  2.0 , 18.0i"); 
    	doEigenCalculations(aMatrix);
/**/
    	aMatrix = new MatrixComplex(""
    			+ " 3,-3, 0, 0, 0;"
    			+ " 0,-4, 4, 0, 0;"
    			+ " 0, 0, 1,-1, 0;"
    			+ " 0, 0, 0, 3,-3;"
    			+ "-1, 0, 0, 0, 1");
    	doEigenCalculations(aMatrix);
		
    	aMatrix = new MatrixComplex(""+
    			" 1, 2, 1;"+
    			" 2, 4, 2;"+
    			" 3 ,6, 3"); 
    	doEigenCalculations(aMatrix);
     	
    	aMatrix = new MatrixComplex(""+
    			" 1, 2, 1;"+
    			" 2, 4, 2;"+
    			" 3, 6, -3"); 
    	doEigenCalculations(aMatrix);
     	
    	aMatrix = new MatrixComplex(""+
    			" 1, 2, 1;"+
    			" 2, 4, 3;"+
    			" 3, 6, 4"); 
    	doEigenCalculations(aMatrix);
    	/**/
     	
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

     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomInteger(4); 
     	doEigenCalculations(aMatrix);

     	System.out.println("\n\n__________________________________ Matriz HERMITICA __________________________________"); 
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomRecInt(9); 
     	aMatrix = aMatrix.hermitian(); 
     	doEigenCalculations(aMatrix);
    	
     	System.out.println("\n\n__________________________________ Matriz SKEW-HERMITICA __________________________________"); 
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomRecInt(9); 
     	aMatrix = aMatrix.skewHermitian(); 
     	doEigenCalculations(aMatrix);

    	aMatrix = new MatrixComplex("" + 
    			"43.000          , 49.000 + 30.000i;"+ 
    			"49.000 - 30.000i,115.000          "); 
    	doEigenCalculations(aMatrix);

     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomRec(4); 
     	doEigenCalculations(aMatrix);
     	
     	aMatrix = new MatrixComplex(""+
     			" 3, 2,-1, 5, 2;"+
     			"-2,-1, 1, 0,-3;" +
     			" 2,-1, 3, 3,-3;" +
     			"-1,-1, 1, 2, 5;" +
     			" 4, 0,-3, 1, 1");
     	doEigenCalculations(aMatrix);

     	System.out.println("\n\n__________________________________ Matriz HERMITICA __________________________________"); 
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomRecInt(7); 
     	aMatrix = aMatrix.hermitian(); 
     	doEigenCalculations(aMatrix);

     	aMatrix = new MatrixComplex(""+
     			" 3, 2,-1, 5, 2;"+
     			" 2,-1, 1, 0,-3;" +
     			"-1, 1, 3, 3,-3;" +
     			" 5, 0, 3, 2, 5;" +
     			" 2,-3,-3, 5, 1");
     	doEigenCalculations(aMatrix);
     	
		aMatrix= new MatrixComplex(
       			" 6.0 , 0.0 ,-1.0 ,-4.0 ,-2.0;"+
       			" 7.0 , 0.0 ,-6.0 , 0.0 ,-2.0;"+
       			"-4.0 ,-1.0 ,-1.0 , 1.0 ,-7.0;"+
       			"-1.0 , 8.0 , 8.0 , 6.0 ,-4.0;"+
       			" 1.0 ,-4.0 , 6.0 , 7.0 , 2.0");
		aMatrix= new MatrixComplex(
       			"-4.0 , 3.0 ,13.0 ,14.0 ,-9.0;"+
       			" 7.0 , 0.0 ,-6.0 , 0.0 ,-2.0;"+
       			"-4.0 ,-1.0 ,-1.0 , 1.0 ,-7.0;"+
       			"-1.0 , 8.0 , 8.0 , 6.0 ,-4.0;"+
       			" 1.0 ,-4.0 , 6.0 , 7.0 , 2.0");    	
     	doEigenCalculations(aMatrix);
	    /**/
		aMatrix = new MatrixComplex(
				  " 2.925-0.533i, 0.729+2.732i, 7.443+6.499i,-4.267+3.362i;"
				+ " 0.208-2.559i,-2.815+3.671i,-7.676+0.295i,-1.295-2.829i;"
				+ "-0.525+2.311i, 2.814+5.875i,-7.154-1.795i,-4.754+2.267i;"
				+ "-3.146+4.064i,-8.656+6.937i, 7.892-3.179i,-8.197+8.063i");
		doEigenCalculations(aMatrix);

		aMatrix = new MatrixComplex(
				  " 143.981        ,-49.251-71.500i,-21.671-42.518i,101.438+42.882i;"
				+ " -49.251+71.500i, 96.680        , 61.750+28.227i,-34.934+ 6.514i;"
				+ " -21.671+42.518i, 61.750-28.227i,130.192        , 33.935-92.671i;"
				+ " 101.438-42.882i,-34.934-6.514i,  33.935+92.671i,354.054         ");
		doEigenCalculations(aMatrix);

     	aMatrix = new MatrixComplex(""+
     			" 3, 2;"+
     			" 0, 1");
     	doEigenCalculations(aMatrix);
}
}