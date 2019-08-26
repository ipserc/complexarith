package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.*;

public class TestLU {

	public static void showResults(LUfactor cMatrix) {
    	MatrixComplex lMatrix;
    	MatrixComplex uMatrix;
    	MatrixComplex pMatrix;
    	Complex det = new Complex();
    	
    	cMatrix.println("aMatrix");
    	System.out.println("A:"+cMatrix.toMaxima());
    	cMatrix.triangle().println("aMatrix Triangle");
    	lMatrix = cMatrix.L();
    	lMatrix.println("L");
    	uMatrix = cMatrix.U();
    	uMatrix.println("U");
    	pMatrix = cMatrix.P();
    	pMatrix.println("P");
    	if (!cMatrix.factorized()) System.out.println("No factorizable!!!!!!!!!!!");
    	((cMatrix.U().transpose().times(cMatrix.L().transpose())).times(pMatrix)).transpose().println("(UT · LT · P)T");;
    	cMatrix.P().transpose().times(cMatrix.L().times(cMatrix.U())).println("PT · L · U");
        	System.out.println("Determinant(A)=" + cMatrix.determinant());
    		det = uMatrix.determinant();
        	System.out.println("Determinant(U)=" + det);
        	System.out.println("|Determinant(U)|=" + det.mod());        	
        	uMatrix.inverse().println("Invert(U)");
    	System.out.println("-----------------------------------------------------\n");
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    	LUfactor aMatrix;
 
    	//aMatrix = new LUfactor("1,3,-1;3,9,2;2,1,-1");
    	//aMatrix = new LUfactor("3,3,-1;1,9,2;2,1,-1");
    	//aMatrix = new LUfactor("3,3,-1,5;1,9,2,-4;2,1,-1,3;2,-2,0,1");
    	//aMatrix = new LUfactor("0,2,1,2;1,0,1,3;3,1,-4,2;-4,0,1,1");
    	aMatrix = new LUfactor("0.0,1.0 - 2.0i,1.0 + 1.0i,-2.0;-2.0 - 2.0i,1.0 - 2.0i,-1.0 - 1.0i,-2.0 - 1.0i;-i,1.0 - 1.0i,-1.0 - 2.0i,-2.0 - 1.0i;1.0,-1.0,-2.0 + 1.0i,-i");
    	//aMatrix = new LUfactor("0.0,-i,0.0,-1.0 - 2.0i;i,-2.0 - 2.0i,-1.0 - 2.0i,1.0 + 1.0i;-2.0 + 1.0i,-2.0,0.0,-i;i,-2.0 - 2.0i,i,-2.0 + 1.0i");
    	//aMatrix = new LUfactor("1,3,-1;3,7,2;2,1,-1");
    	//aMatrix = new LUfactor(5); aMatrix.initMatrixRandomInteger(9);
       	//aMatrix = new LUfactor(5); aMatrix.initMatrixRandomRec(9);
    	//aMatrix = new LUfactor("1,2,3,;4,2,1;-3,0,1");

       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	aMatrix.println("aMatrix Original");
     	aMatrix.LUfactorize();
    	showResults(aMatrix);
/*
    	aMatrix = new LUfactor("1,3,-1;2,1,-1;3,9,2");
    	aMatrix.LUfactorize();
    	showResults(aMatrix);

    	//aMatrix = new LUfactor("1,3,-1;3,7,2;2,1,-1");
    	//aMatrix = new LUfactor("1,1,1;1,2,2;1,1,3");
    	aMatrix = new LUfactor(3); aMatrix.initMatrixRandomRec(9);
     	aMatrix.LUfactorize();
    	showResults(aMatrix);
*/

     	System.out.println("\n\n__________________________________ Matriz HERMITICA __________________________________"); 
     	aMatrix = new LUfactor(4); 
     	aMatrix.initMatrixRandomRecInt(7); 
     	aMatrix.complexMatrix = aMatrix.hermitian().complexMatrix; 
     	aMatrix.LUfactorize();
    	showResults(aMatrix);

     	System.out.println("\n\n__________________________________ Matriz SKEW-HERMITICA __________________________________"); 
     	aMatrix = new LUfactor(4); 
     	aMatrix.initMatrixRandomRecInt(7); 
     	aMatrix.complexMatrix = aMatrix.skewHermitian().complexMatrix; 
     	aMatrix.LUfactorize();
    	showResults(aMatrix);
	}

}
