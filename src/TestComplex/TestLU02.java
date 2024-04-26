package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.*;

public class TestLU02 {

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
    	System.out.println("MatComp:"+luMatrix.toMatrixComplex());
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


    	luMatrix = new LUfactor(""
    			+ " 0,-5,  3, 7;"
    			+ " 5, 0,-11,-8;"
    			+ "-3,11,  0, 1;"
    			+ "-7, 8, -1, 0",
    			LUfactor.LUmethod.CHOLESKY);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(""
    			+ " 0,-5,  3, 7;"
    			+ " 5, 0,-11,-8;"
    			+ "-3,11,  0, 1;"
    			+ "-7, 8, -1, 0",
    			LUfactor.LUmethod.CROUT);
    	showResults(luMatrix);

    	luMatrix = new LUfactor(""
    			+ " 0,-5,  3, 7;"
    			+ " 5, 0,-11,-8;"
    			+ "-3,11,  0, 1;"
    			+ "-7, 8, -1, 0",
    			LUfactor.LUmethod.DOOLITTLE);
    	showResults(luMatrix);
  
    	luMatrix = new LUfactor(""
    			+ " 0,-5,  3, 7;"
    			+ " 5, 0,-11,-8;"
    			+ "-3,11,  0, 1;"
    			+ "-7, 8, -1, 0",
    			LUfactor.LUmethod.PIVOT);
    	showResults(luMatrix);
	}

}
