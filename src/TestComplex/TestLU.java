package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.*;

public class TestLU {

	public static void showResults(LUfactor luMatrix) {
    	MatrixComplex lMatrix;
    	MatrixComplex uMatrix;
    	MatrixComplex pMatrix;
    	Complex det = new Complex();
    	
    	luMatrix.println("luMatrix");
    	System.out.println("A:"+luMatrix.toMaxima());
    	luMatrix.triangle().println("luMatrix Triangle");
    	lMatrix = luMatrix.L();
    	lMatrix.println("L");
    	uMatrix = luMatrix.U();
    	uMatrix.println("U");
    	pMatrix = luMatrix.P();
    	pMatrix.println("P");
    	if (!luMatrix.factorized()) System.out.println("No factorizable!!!!!!!!!!!");
    	((luMatrix.U().transpose().times(luMatrix.L().transpose())).times(pMatrix)).transpose().println("(UT · LT · P)T");;
    	luMatrix.P().transpose().times(luMatrix.L().times(luMatrix.U())).println("PT · L · U");
        	System.out.println("Determinant(A)=" + luMatrix.determinant());
    		det = uMatrix.determinant();
        	System.out.println("Determinant(U)=" + det);
        	System.out.println("|Determinant(U)|=" + det.mod());        	
        	uMatrix.inverse().println("Invert(U)");
    	System.out.println("-----------------------------------------------------\n");
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MatrixComplex aMatrix;
    	LUfactor luMatrix;
 
    	//luMatrix = new LUfactor("1,3,-1;3,9,2;2,1,-1");
    	//luMatrix = new LUfactor("3,3,-1;1,9,2;2,1,-1");
    	//luMatrix = new LUfactor("3,3,-1,5;1,9,2,-4;2,1,-1,3;2,-2,0,1");
    	//luMatrix = new LUfactor("0,2,1,2;1,0,1,3;3,1,-4,2;-4,0,1,1");
    	luMatrix = new LUfactor("0.0,1.0 - 2.0i,1.0 + 1.0i,-2.0;-2.0 - 2.0i,1.0 - 2.0i,-1.0 - 1.0i,-2.0 - 1.0i;-i,1.0 - 1.0i,-1.0 - 2.0i,-2.0 - 1.0i;1.0,-1.0,-2.0 + 1.0i,-i");
    	//luMatrix = new LUfactor("0.0,-i,0.0,-1.0 - 2.0i;i,-2.0 - 2.0i,-1.0 - 2.0i,1.0 + 1.0i;-2.0 + 1.0i,-2.0,0.0,-i;i,-2.0 - 2.0i,i,-2.0 + 1.0i");
    	//luMatrix = new LUfactor("1,3,-1;3,7,2;2,1,-1");
    	//luMatrix = new LUfactor(5); luMatrix.initMatrixRandomInteger(9);
       	//luMatrix = new LUfactor(5); luMatrix.initMatrixRandomRec(9);
    	//luMatrix = new LUfactor("1,2,3,;4,2,1;-3,0,1");

       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	luMatrix.println("luMatrix Original");
    	showResults(luMatrix);
/*
    	luMatrix = new LUfactor("1,3,-1;2,1,-1;3,9,2");
    	luMatrix.LUfactorize();
    	showResults(luMatrix);

    	//luMatrix = new LUfactor("1,3,-1;3,7,2;2,1,-1");
    	//luMatrix = new LUfactor("1,1,1;1,2,2;1,1,3");
    	luMatrix = new LUfactor(3); luMatrix.initMatrixRandomRec(9);
     	luMatrix.LUfactorize();
    	showResults(luMatrix);
*/

     	System.out.println("\n\n__________________________________ Matriz HERMITICA __________________________________"); 
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomRecInt(7);
     	luMatrix = new LUfactor(aMatrix.hermitian()); 
    	showResults(luMatrix);

     	System.out.println("\n\n__________________________________ Matriz SKEW-HERMITICA __________________________________"); 
     	aMatrix = new MatrixComplex(4); 
     	aMatrix.initMatrixRandomRecInt(7); 
     	luMatrix = new LUfactor(aMatrix.skewHermitian()); 
    	showResults(luMatrix);
	}

}
