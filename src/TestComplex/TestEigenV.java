package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;

public class TestEigenV {

	public static void doEigenCalculations(MatrixComplex aMatrix) {
    	MatrixComplex eigenVal;
    	MatrixComplex eigenVectors;  
    	MatrixComplex eigenVect;

    	System.out.println("------------- CALCULO AUTOVALORES/AUTOVECTORES -----------------");
    	aMatrix.println("aMatrix");
    	System.out.println("Maxima:\n"+aMatrix.toMaxima());
    	System.out.println("Wolfram:\n"+aMatrix.toWolfram());
    	eigenVal = aMatrix.eigenvalues();
    	//eigenVal.quicksort(0, 0, eigenVal.complexMatrix.length-1);
    	eigenVal.quicksort(0);
    	eigenVectors = aMatrix.eigenvectors(eigenVal);
    	aMatrix.charactPoly().println("Characteristic polynom");
    	//aMatrix.charactPoly().plotReIm(-10, 10);
    	eigenVal.println("eigenVal");
    	eigenVectors.println("eigenVectors");

    	int colLen = aMatrix.complexMatrix[0].length;
    	eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < eigenVal.complexMatrix.length; ++eigv) {
	    	for(int col = 0; col < colLen; ++col) eigenVect.complexMatrix[0][col] = eigenVectors.complexMatrix[eigv][col];
	    	//eigenVect.divides(eigenVect.complexMatrix[0][0]).println("Norm eigenVect "+eigv);
	    	eigenVect.println("eigenVect");
	    	aMatrix.times(eigenVect.transpose()).transpose().println("aMatrix·eigenVect "+eigv);
	    	eigenVect.times(eigenVal.complexMatrix[eigv][0]).println("lamb["+eigv+"]·eigenVect "+eigv);
    	}

    	eigenVectors.adjoint().times(eigenVectors).println("eVT·eV");
    	/*
    	System.out.print("press any key");
    	try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    	
    	Diagfactor diagonal = new Diagfactor(aMatrix);
    	diagonal.diagonalize();
    	diagonal.D().println("Matriz Diagianl");
    	diagonal.P().println("Matriz Valores Propios");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MatrixComplex aMatrix;

    	//aMatrix = new MatrixComplex(8); aMatrix.initMatrixRandomInteger(1);
    	//doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex(8); aMatrix.initMatrixRandomRecInt(1);
    	//doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("-1,2,1;3,4,-1;1,2,2");
    	//aMatrix = new MatrixComplex("-1,2,1;1,2,-1;1,2,2");
    	//aMatrix = new MatrixComplex("1,-1,0;-1,2,-1;0,-1,1");
    	//aMatrix = new MatrixComplex("-1,2,1;3,-2,1;5,-2,3;6,2,-7"); aMatrix = aMatrix.adjoint().times(aMatrix);
    	//aMatrix = new MatrixComplex("11,7,-7;7,11,1;-7,1,9");
    	//aMatrix = new MatrixComplex(4); aMatrix.initMatrixRandomRecInt(9);
    	//doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("1,-1,0;9,3,1"); aMatrix = aMatrix.adjoint().times(aMatrix);
    	//aMatrix = new MatrixComplex("-5,1,7,-15;-2,0,5,-7;1,-5,7,-3;4,-7,3,4");aMatrix = aMatrix.adjoint().times(aMatrix);
    	//aMatrix = new MatrixComplex("-5,1,7,-14;-2,0,5,-7;1,-5,7,-3;4,-7,3,4");
    	//aMatrix = new MatrixComplex(2); aMatrix.initMatrixRandomInteger(9);
    	//doEigenCalculations(aMatrix);
    	//aMatrix = new MatrixComplex("-11.0,-20.0,10.0,4.0,-15.0,-18.0,-7.0,-16.0,-10.0,2.0;8.0,-1.0,1.0,17.0,-4.0,16.0,-13.0,-11.0,0.0,-17.0;4.0,-7.0,-11.0,5.0,15.0,10.0,-19.0,19.0,17.0,3.0;15.0,11.0,-6.0,-5.0,17.0,19.0,11.0,-4.0,-8.0,7.0;-9.0,9.0,-14.0,-14.0,-18.0,14.0,19.0,-6.0,7.0,5.0;-6.0,-3.0,-17.0,9.0,19.0,10.0,-8.0,-4.0,-11.0,-1.0;-20.0,-18.0,-18.0,14.0,-2.0,-5.0,8.0,-8.0,0.0,-7.0;-1.0,7.0,-5.0,19.0,13.0,-6.0,9.0,0.0,0.0,5.0;-13.0,-15.0,5.0,3.0,16.0,16.0,15.0,-6.0,0.0,-3.0;9.0,-20.0,-13.0,-8.0,6.0,2.0,-18.0,-19.0,2.0,18.0");
    	//aMatrix = new MatrixComplex("-1.0,0.0,-1.0,0.0;-1.0,0.0,-1.0,-1.0;0.0,0.0,0.0,0.0;0.0,-1.0,-1.0,-1.0");
    	//aMatrix = new MatrixComplex("3,2-i,-3i;2+i,0,1-i;3i,1+i,0");
    	//aMatrix = new MatrixComplex("1,2,0;-2,1,2;1,3,1");
    	//aMatrix = new MatrixComplex("0.000,0.174,0.782,0.263,0.022,0.000,0.000,0.000,0.000,0.000;0.985,0,0,0,0,0,0,0,0,0;0,0.996,0,0,0,0,0,0,0,0;0,0,0.994,0,0,0,0,0,0,0;0,0,0,0.990,0,0,0,0,0,0;0,0,0,0,0.975,0,0,0,0,0;0,0,0,0,0,0.940,0,0,0,0;0,0,0,0,0,0,0.866,0,0,0;0,0,0,0,0,0,0,0.680,0,0;0,0,0,0,0,0,0,0,0.361,0");
    	//aMatrix = new MatrixComplex(5); aMatrix.initMatrixRandomRecInt(9);
    	//aMatrix = new MatrixComplex("4.0,-7.0 - 2.0i,8.0i,5.0 + 3.0i,-7.0 + 1.0i,-5.0 + 7.0i,6.0 - 2.0i,-6.0 - 9.0i,-4.0 - 4.0i,-8.0 - 4.0i;-1.0 - 2.0i,5.0 + 5.0i,-2.0 + 2.0i,-2.0 + 8.0i,5.0 + 5.0i,-7.0 + 8.0i,-3.0 - 8.0i,-9.0 + 6.0i,-9.0 + 5.0i,-6.0 + 4.0i;4.0 + 5.0i,-6.0 + 1.0i,8.0 + 3.0i,-7.0,4.0 - 8.0i,6.0 - 9.0i,-5.0 - 6.0i,-2.0 + 6.0i,3.0 - 7.0i,-9.0 - 1.0i;-3.0 + 2.0i,5.0i,7.0 - 6.0i,-1.0i,-2.0 + 7.0i,3.0 - 3.0i,6.0 - 8.0i,3.0i,6.0 + 1.0i,5.0 - 5.0i;-4.0 - 6.0i,-1.0,-3.0 + 8.0i,5.0 + 2.0i,8.0 - 8.0i,7.0 - 5.0i,-8.0 + 6.0i,6.0 - 7.0i,-7.0 - 9.0i,-4.0 - 9.0i;-2.0 - 2.0i,-4.0 + 5.0i,-4.0 - 7.0i,6.0 - 7.0i,-8.0 + 2.0i,-1.0 - 9.0i,3.0 + 5.0i,-6.0 - 7.0i,2.0 + 8.0i,2.0;-7.0 - 1.0i,2.0 + 7.0i,1.0 - 4.0i,4.0 - 8.0i,-9.0 + 6.0i,2.0 + 5.0i,-4.0,4.0i,3.0 + 8.0i,6.0;4.0 - 9.0i,-7.0i,-1.0 - 6.0i,-1.0 + 1.0i,-2.0 - 3.0i,-5.0 - 3.0i,3.0 - 8.0i,-7.0 + 7.0i,4.0 + 5.0i,-9.0 - 9.0i;1.0 - 8.0i,-3.0 + 4.0i,2.0 + 3.0i,-3.0 + 3.0i,-3.0 - 8.0i,6.0i,-1.0 + 1.0i,3.0 - 4.0i,7.0 - 8.0i,-9.0 + 6.0i;2.0 + 2.0i,-5.0,8.0 - 7.0i,4.0 + 1.0i,6.0 + 3.0i,4.0 - 7.0i,2.0i,-9.0,-9.0 - 2.0i,5.0 - 5.0i");
    	//aMatrix = new MatrixComplex("3.0,-3.0,-4.0,-2.0;-4.0,-1.0,-1.0,-3.0;0.0,0.0,-3.0,-1.0;-4.0,2.0,-3.0,-3.0");
    	//aMatrix = new MatrixComplex("-1.0,-1.0,0.0,-1.0;0.0,0.0,0.0,-1.0;-1.0,0.0,0.0,0.0;0.0,-1.0,0.0,0.0");
    	//aMatrix = new MatrixComplex("7,-1,-1;-1,5,1;-1,1,5");
    	//aMatrix = new MatrixComplex("43.000,49.000 + 30.000i;49.000 - 30.000i,115.000");
    	//aMatrix = new MatrixComplex("1,1;1,0;0,1"); aMatrix = aMatrix.adjoint().times(aMatrix);
    	//aMatrix = new MatrixComplex("2.000,1.000;1.000,2.000");
    	//aMatrix = new MatrixComplex("43.000,49.000 + 30.000i;49.000 - 30.000i,115.000");
       	aMatrix = new MatrixComplex(3); aMatrix.initMatrixRandomRec(5);
       	aMatrix = aMatrix.hermitian();
     	Complex.setFormatOFF();
    	doEigenCalculations(aMatrix);
    	Complex.setFixedON(3);
    	doEigenCalculations(aMatrix);
    	Complex.setScientificON(3);;
    	doEigenCalculations(aMatrix);
       	Complex.setFormatON();
    	doEigenCalculations(aMatrix);
    	Complex.setFixedON(3);
    	doEigenCalculations(aMatrix);
    	Complex.setScientificON(3);;
    	doEigenCalculations(aMatrix);
    	/*
    	System.out.println("Matriz HERMITICA");
    	aMatrix = new MatrixComplex(5); aMatrix.initMatrixRandomRecInt(9);
    	aMatrix = aMatrix.hermitian();
    	doEigenCalculations(aMatrix);
    	System.out.println("Matriz SKEW-HERMITICA");
    	aMatrix = new MatrixComplex(5); aMatrix.initMatrixRandomRecInt(9);
    	aMatrix = aMatrix.skewHermitian();
    	doEigenCalculations(aMatrix);
    	*/
    	    	
	}

}
