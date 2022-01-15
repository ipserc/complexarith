package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.*;

public class TestLU01 {

	public static void showResults(LUfactor luMatrix) {
    	Complex det = new Complex();
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "LOWER UPPER DIAGONALIZATION (LU) TEST"));
    	
		System.out.println(Complex.boxTextRandom(boxSize, "LU Results"));
		System.out.println("Diagonalization Method LU." + luMatrix.getMethodName());
    	System.out.println("Is Symmetryc    :" + luMatrix.isSymmetric());
    	System.out.println("Is AntiSymmetryc:" + luMatrix.isAntiSymmetric());
    	System.out.println("Is Hermitian    :" + luMatrix.isHermitian());
    	System.out.println("Is SkewHermitian:" + luMatrix.isSkewHermitian());
		luMatrix.println("luMatrix");
    	System.out.println("MAXIMA :"+luMatrix.toMaxima_lu_factor(true));
    	System.out.println("OCTAVE :"+luMatrix.toOctave_lu());
    	System.out.println("WOLFRAM:"+luMatrix.toWolfram_LUDecomposition());
    	if (!luMatrix.factorized()) {
    		System.out.println("No factorizable!!!!!!!!!!!");
    		return;
    	}
    	luMatrix.L().println("L");
    	luMatrix.U().println("U");
    	luMatrix.P().println("P");
    	
		System.out.println(Complex.boxTextRandom(boxSize, "LU Operations"));
    	((luMatrix.U().transpose().times(luMatrix.L().transpose())).times(luMatrix.P())).transpose().println("(UT · LT · P)T");;
    	luMatrix.P().transpose().times(luMatrix.L().times(luMatrix.U())).println("PT · L · U");
    	System.out.println("Determinant(luMatrix)=" + luMatrix.determinant());
		det = luMatrix.U().determinant().times(luMatrix.L().determinant());
    	System.out.println("Determinant(U*L)=" + det);
    	System.out.println("|Determinant(U*L)|=" + det.mod());        	
    	luMatrix.U().triangleLo().println("Triangle(U)");
    	luMatrix.U().inverse().println("Invert(U)");		
	}

	public static void main(String[] args) {
		int boxSize = 65;
		MatrixComplex aMatrix;
    	LUfactor luMatrix;
 
    	LUfactor.version();
    	
    	Complex.setFormatON();
    	Complex.setFixedON(3);


    	luMatrix = new LUfactor(  
    			" 1, 2, 3;" +
				" 6, 5, 4;" +
				" 9, 7, 8", 
				LUfactor.LUmethod.CROUT);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(  
    			" 1, 2, 3;" +
				" 6, 5, 4;" +
				" 9, 7, 8", 
				LUfactor.LUmethod.PIVOT);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(  
    			" 3,-1, 4,-1;" +
    			"-1,-1, 3, 1;" +
    			" 2, 3,-1,-1;" +
    			" 7, 1, 1, 2", 
    			LUfactor.LUmethod.DOOLITTLE);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(  
    			"   1,   2,   4,   7;" +
				"   2,  13,  23,  38;" +
				"   4,  23,  77, 122;" +
				"   7,  38, 122, 294", 
				LUfactor.LUmethod.CROUT);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(  
    			"   1,   2,   4,   7;" +
				"   2,  13,  23,  38;" +
				"   4,  23,  77, 122;" +
				"   7,  38, 122, 294",
				LUfactor.LUmethod.PIVOT);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(  
    			" 1, 3,-1;" +
    			" 3, 9, 2;" +
    			" 2, 1,-1", 
    			LUfactor.LUmethod.CROUT);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(  
    			" 1, 3,-1;" +
				" 3, 9, 2;" +
				" 2, 1,-1", 
				LUfactor.LUmethod.PIVOT);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(
    			" 3, 3,-1;" +
    			" 1, 9, 2;" +
    			" 2, 1,-1", 
    			LUfactor.LUmethod.DOOLITTLE);
    	showResults(luMatrix);
    	
    	luMatrix = new LUfactor(
    			" 3, 3,-1;" +
    			" 1, 9, 2;" +
    			" 2, 1,-1", 
    			LUfactor.LUmethod.PIVOT);
    	showResults(luMatrix);
  	
    	System.out.println(Complex.boxTitleRandom(boxSize, "FREE METHOD LOWER UPPER DIAGONALIZATION (LU)"));
    	luMatrix = new LUfactor(
    			" 3, 3,-1, 5;" +
    			" 1, 9, 2,-4;" +
    			" 2, 1,-1, 3;" +
    			" 2,-2, 0, 1");
    	showResults(luMatrix);

    	luMatrix = new LUfactor(
    			" 0, 2, 1, 2;" +
    			" 1, 0, 1, 3;" +
    			" 3, 1,-4, 2;" +
    			"-4, 0, 1, 1");
    	showResults(luMatrix);

    	luMatrix = new LUfactor(
    			" 0, 2, 1, 2;" +
    			" 1, 0, 1, 3;" +
    			" 3, 1,-4, 2;" +
    			"-4, 0, 1, 1");
    	showResults(luMatrix);

    	luMatrix = new LUfactor(" 0, -i, 0, -1-2i; i, -2-2i, -1-2i, 1+i; -2+i,-2, 0, -i; i, -2-2i, i, -2+i");
    	showResults(luMatrix);

    	luMatrix = new LUfactor("1,3,-1;3,7,2;2,1,-1");
    	showResults(luMatrix);

    	luMatrix = new LUfactor(5); luMatrix.initMatrixRandomInteger(9); luMatrix.factorice();
    	showResults(luMatrix);

    	luMatrix = new LUfactor(7); luMatrix.initMatrixRandomRec(9); luMatrix.factorice();
    	showResults(luMatrix);
    	
    	luMatrix = new LUfactor("1,2,3,;4,2,1;-3,0,1");
    	showResults(luMatrix);

    	luMatrix = new LUfactor("0.0,1.0 - 2.0i,1.0 + 1.0i,-2.0;-2.0 - 2.0i,1.0 - 2.0i,-1.0 - 1.0i,-2.0 - 1.0i;-i,1.0 - 1.0i,-1.0 - 2.0i,-2.0 - 1.0i;1.0,-1.0,-2.0 + 1.0i,-i");
    	showResults(luMatrix);


    	luMatrix = new LUfactor("1,3,-1; 3,9,-2; 2,1,-1");
    	showResults(luMatrix);

		System.out.println(Complex.boxTitleRandom(boxSize, "HERMITIAN MATRIX"));
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomInteger(7);
     	luMatrix = new LUfactor(aMatrix.hermitian(), LUfactor.LUmethod.CHOLESKY); 
    	showResults(luMatrix);
     	luMatrix = new LUfactor(aMatrix.hermitian()); 
    	showResults(luMatrix);

		System.out.println(Complex.boxTitleRandom(boxSize, "SKEW-HERMITIAN MATRIX"));
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomInteger(7); 
     	luMatrix = new LUfactor(aMatrix.skewHermitian(), LUfactor.LUmethod.CHOLESKY); 
    	showResults(luMatrix);
     	luMatrix = new LUfactor(aMatrix.skewHermitian()); 
    	showResults(luMatrix);
 
		System.out.println(Complex.boxTitleRandom(boxSize, "HERMITIAN COMPLEX MATRIX"));
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomRecInt(7);
     	luMatrix = new LUfactor(aMatrix.hermitian(), LUfactor.LUmethod.CHOLESKY); 
    	showResults(luMatrix);
     	luMatrix = new LUfactor(aMatrix.hermitian()); 
    	showResults(luMatrix);

		System.out.println(Complex.boxTitleRandom(boxSize, "SKEW-HERMITIAN COMPLEX MATRIX"));
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomRecInt(7); 
     	luMatrix = new LUfactor(aMatrix.skewHermitian(), LUfactor.LUmethod.CHOLESKY); 
    	showResults(luMatrix);
     	luMatrix = new LUfactor(aMatrix.skewHermitian()); 
    	showResults(luMatrix);
    	
    	luMatrix = new LUfactor(
    			  " 1,-1, 0;"
    			+ "-1, 5, 0;"
    			+ " 0, 0, 7", 
    			LUfactor.LUmethod.CHOLESKY);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(
  			  " 1,-3, 0;"
  			+ "-3, 4,-2;"
  			+ " 0,-2, 8", 
  			LUfactor.LUmethod.CHOLESKY);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(
    			  " 1,-3, 7,-1;"
    			+ "-3, 2,-2, 1;"
       			+ " 7,-2, 6, 5;" 
       			+ "-1, 1, 5, 5", 
       		    LUfactor.LUmethod.CHOLESKY);
      	showResults(luMatrix);

	}

}
