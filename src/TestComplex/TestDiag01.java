package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.*;

public class TestDiag01 {

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
    	Complex.exact(false); // WHATCH THIS

        Complex.facts();
        Complex.printFormatStatus();
        
    	diagMatrix = new Diagfactor("1,3,-1; 3,9,2; 2,1,-1");
    	showResults(diagMatrix);

    	diagMatrix = new Diagfactor("3,3,-1; 1,9,2; 2,1,-1");
    	showResults(diagMatrix);

    	diagMatrix = new Diagfactor("3,3,-1,5; 1,9,2,-4; 2,1,-1,3; 2,-2,0,1");
    	showResults(diagMatrix);

    	diagMatrix = new Diagfactor("0,2,1,2; 1,0,1,3; 3,1,-4,2; -4,0,1,1");
    	showResults(diagMatrix);

    	diagMatrix = new Diagfactor(" 0, -i, 0, -1-2i; i, -2-2i, -1-2i, 1+i; -2+i,-2, 0, -i; i, -2-2i, i, -2+i");
    	showResults(diagMatrix);

    	diagMatrix = new Diagfactor("1,3,-1;3,7,2;2,1,-1");
    	showResults(diagMatrix);

    	diagMatrix = new Diagfactor(5); diagMatrix.initMatrixRandomInteger(9); diagMatrix.diagonalize();
    	showResults(diagMatrix);

    	diagMatrix = new Diagfactor(7); diagMatrix.initMatrixRandomRec(9); diagMatrix.diagonalize();
    	showResults(diagMatrix);
    	
    	diagMatrix = new Diagfactor("1,2,3,;4,2,1;-3,0,1");
    	showResults(diagMatrix);

    	diagMatrix = new Diagfactor("0.0,1.0 - 2.0i,1.0 + 1.0i,-2.0;-2.0 - 2.0i,1.0 - 2.0i,-1.0 - 1.0i,-2.0 - 1.0i;-i,1.0 - 1.0i,-1.0 - 2.0i,-2.0 - 1.0i;1.0,-1.0,-2.0 + 1.0i,-i");
    	showResults(diagMatrix);

		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "HERMITIAN MATRIX"));
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomRecInt(7);
     	diagMatrix = new Diagfactor(aMatrix.hermitian()); 
    	showResults(diagMatrix);

		System.out.println(Complex.boxTitleRandom(boxSize, "SKEW-HERMITIAN MATRIX"));
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomRecInt(7); 
     	diagMatrix = new Diagfactor(aMatrix.skewHermitian()); 
    	showResults(diagMatrix);
	}

}
