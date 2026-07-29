package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.*;

public class TestDiag02 {

	public static void showResults(Diagfactor diagMatrix) {
    	Complex det = new Complex();
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "DIAGONALIZATION FACTORIZATION TEST"));
    	
		System.out.println(Complex.boxTextRandom(boxSize, "Diagfactor Expressions"));
		diagMatrix.println("diagMatrix");
    	System.out.println("MAXIMA :"+diagMatrix.toMaxima_diagonalize());
    	System.out.println("OCTAVE :"+diagMatrix.toOctave_diagonalize());
    	System.out.println("WOLFRAM:"+diagMatrix.toWolfram_diagonalize());
		System.out.println(Complex.boxTextRandom(boxSize, "Diagfactor Results"));
    	if (!diagMatrix.factorized()) {
    		System.out.println("No factorizable!!!!!!!!!!!");
    		return;
    	}
    	diagMatrix.D().println("D(iagonal) Matrix");
    	diagMatrix.P().transpose().println("P(roper Values) transposed eigenvectors Matrix");
    	
		System.out.println(Complex.boxTextRandom(boxSize, "Diagfactor Operations"));
    	diagMatrix.P().times(diagMatrix.D().times(diagMatrix.P().inverse())).println("P · D · P⁻¹");
    	System.out.println("Determinant(A)=" + diagMatrix.determinant());
		det = diagMatrix.D().determinant();
    	System.out.println("Determinant(D)=" + det);
    	System.out.println("|Determinant(D)|=" + det.mod());        	
    	diagMatrix.triangle().println("diagMatrix Triangle");
    	diagMatrix.D().inverse().println("Invert(D)");		
	}

	public static void main(String[] args) {
		MatrixComplex aMatrix;
    	Diagfactor diagMatrix;
  
    	Diagfactor.version();
    	
    	Complex.setFormatON();
    	Complex.setFixedON(3);
    	Complex.exact(true); // WATCH THIS

    	diagMatrix = new Diagfactor("-2.000,3.000,5.000;-1.000,2.000,5.000;5.000,-5.000,3.000");
    	//diagMatrix = new Diagfactor("1,3,-1; 3,9,2; 2,1,-1");
    	showResults(diagMatrix);

	}

}
