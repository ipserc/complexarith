package TestComplex;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.matrixcomplex.*;

public class TestOctaveEig {

	public static void doEigenCalculations(MatrixComplex aMatrix) {
		Complex seed = new Complex(1,0);
		Eigenspace eigenSpace = new Eigenspace(seed, aMatrix);
     	MatrixComplex eigenVect;
 
    	System.out.println("__________________________________________________________________________________________");
    	System.out.println("____________________________ Eigenspace vectors & values CHECK ___________________________");
    	aMatrix.println("aMatrix");
    	eigenSpace.vectors().println("Eigenspace eigenVectors");
    	eigenSpace.values().transpose().println("Eigenspace eigenValues");

    	int colLen = aMatrix.cols(); //complexMatrix[0].length;
    	eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < eigenSpace.values().rows(); ++eigv) {
    		eigenVect.complexMatrix[0] = eigenSpace.vectors().complexMatrix[eigv].clone();
	    	//eigenVect.divides(eigenVect.complexMatrix[0][0]).println("Norm eigenVect "+eigv);
    		eigenVect.println									("**************** eigenVect: ");
    		eigenSpace.values().complexMatrix[eigv][0].println	("                 eigenVal : ");
	    	aMatrix.times(eigenVect.transpose()).transpose().println("aMatrix·eigenVect  "+eigv);
	    	eigenVect.times(eigenSpace.values().complexMatrix[eigv][0]).println("eigval["+eigv+"]·eigenVect"+eigv);
    	}

	}
	
	public static void showOctave_eig_Results(MatrixComplex aMatrix) {
		MatrixComplex eigenVectors = new MatrixComplex(""
				+ "   8.9443e-01,  -3.5500e-01,   3.0062e-01;"
				+ "  -4.4721e-01,  -6.7253e-01,   3.1737e-02;"
				+ "   1.3010e-17,  -6.4937e-01,   9.5322e-01");
		eigenVectors = eigenVectors.transpose();
		MatrixComplex eigenVal = new MatrixComplex("-2.000; 4.618; 2.382");

		System.out.println("__________________________________________________________________________________________");
    	System.out.println("___________________________________ GNU OCTAVE eig CHECK _________________________________");
		System.out.println("Octave command:>> [V,lambda] = eig([-1.000,2.000,1.000;3.000,4.000,-1.000;1.000,2.000,2.000])");

		aMatrix.println("Matrix");
		eigenVectors.println("Octave eigenVectors");
		eigenVal.transpose().println("Octave eigenValues");
    	int colLen = eigenVectors.cols(); //complexMatrix[0].length;
    	MatrixComplex eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < eigenVal.rows(); ++eigv) {
    		eigenVect.complexMatrix[0] = eigenVectors.complexMatrix[eigv].clone();
	    	//eigenVect.divides(eigenVect.complexMatrix[0][0]).println("Norm eigenVect "+eigv);
    		eigenVect.println						("**************** eigenVect: ");
    		eigenVal.complexMatrix[eigv][0].println	("                 eigenVal : ");
    		aMatrix.times(eigenVect.transpose()).transpose().println("aMatrix·eigenVect  "+eigv);
	    	eigenVect.times(eigenVal.complexMatrix[eigv][0]).println("eigval["+eigv+"]·eigenVect"+eigv);
    	}		
	}

	public static void showOctave_eigs_Results(MatrixComplex aMatrix) {
		MatrixComplex eigenVectors = new MatrixComplex(""
				+ "  -3.5500e-01,   3.0062e-01,   8.9443e-01;"
				+ "  -6.7253e-01,   3.1737e-02,  -4.4721e-01;"
				+ "  -6.4937e-01,   9.5322e-01,   1.3010e-17");
		eigenVectors = eigenVectors.transpose();
		MatrixComplex eigenVal = new MatrixComplex(" 4.618; 2.382; -2.000");

		System.out.println("__________________________________________________________________________________________");
    	System.out.println("__________________________________ GNU OCTAVE eigs CHECK _________________________________");
		System.out.println("Octave command:>> [V,lambda] = eigs([-1.000,2.000,1.000;3.000,4.000,-1.000;1.000,2.000,2.000])");

    	aMatrix.println("Matrix");
		eigenVectors.println("Octave eigenVectors");
		eigenVal.transpose().println("Octave eigenValues");
    	int colLen = eigenVectors.cols(); //complexMatrix[0].length;
    	MatrixComplex eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < eigenVal.rows(); ++eigv) {
    		eigenVect.complexMatrix[0] = eigenVectors.complexMatrix[eigv].clone();
	    	//eigenVect.divides(eigenVect.complexMatrix[0][0]).println("Norm eigenVect "+eigv);
    		eigenVect.println						("**************** eigenVect: ");
    		eigenVal.complexMatrix[eigv][0].println	("                 eigenVal : ");
    		aMatrix.times(eigenVect.transpose()).transpose().println("aMatrix·eigenVect  "+eigv);
	    	eigenVect.times(eigenVal.complexMatrix[eigv][0]).println("eigval["+eigv+"]·eigenVect"+eigv);
    	}		
	}

	public static MatrixComplex eigenVectorsNorm(MatrixComplex eigenVectors) {
		MatrixComplex eigenVectorsNorm = eigenVectors.clone();
		
		for(int row = 0; row < eigenVectorsNorm.rows(); ++row) {
			Complex lastTerm = eigenVectors.getItem(row, eigenVectorsNorm.cols()-1);
			for(int col = 0; col < eigenVectorsNorm.cols(); ++col)
			eigenVectorsNorm.setItem(row, col, eigenVectors.getItem(row, col).divides(lastTerm));
		}
		return eigenVectorsNorm;
	}
	
	public static void main(String[] args) {
		MatrixComplex aMatrix = new MatrixComplex("-1,2,1;3,4,-1;1,2,2");
		Complex.setFormatON();
		Complex.setFixedON(3);
    	
		showOctave_eig_Results(aMatrix);
		showOctave_eigs_Results(aMatrix);
    	doEigenCalculations(aMatrix);
    }
}
