/******************************************************************************
 *  Compilation:  javac TestVector.java
 *  Execution:    /usr/lib/jvm/java-8-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/workspace_oxigen/complexarith/bin:/home/ipserc/workspace_oxigen/complexarith/classes TestComplex.TestVector
 *
 *  Tests for arith.Complex.
 *	
 *  
 *  
 *  
 *  
 *  
 *  
 *
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *
 ******************************************************************************/

package TestComplex;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vector.*;

public class TestVector {

	/**
	 * @param args
	 */
	
	public static void calcNorms(Vector fVector) {
		int p;
       	System.out.println("------------- CALCULO DE NORMAS -----------------");
       	fVector.println("fVector");
       	p = 1; System.out.println("Norm " + p +  " = " + fVector.p_norm(p));
       	p = 2; System.out.println("Norm " + p +  " = " + fVector.p_norm(p));
       	p = 3; System.out.println("Norm " + p +  " = " + fVector.p_norm(p));
       	p = 4; System.out.println("Norm " + p +  " = " + fVector.p_norm(p));
       	p = 66; System.out.println("Norm " + p +  " = " + fVector.p_norm(p));
       	p = 100; System.out.println("Norm " + p +  " = " + fVector.p_norm(p));
       	System.out.println("Infinity Norm = " + fVector.inf_norm());
       	System.out.println("Euclidean Norm = " + fVector.euc_norm());
       	System.out.println("Frobenius Norm = " + fVector.f_norm());
 	}
	
	public static void vectorOperate(Vector aVector, Vector bVector) {
    	Complex result = new Complex();
    	Vector cVector;
    	MatrixComplex matrix = new MatrixComplex();
    	
       	System.out.println("------------- OPERACIONES CON VECTORES -----------------");
       	aVector.println("Vector a");
       	System.out.printf("Dimensión Vector a:%d\n", aVector.dim());
       	bVector.println("Vector b");
       	System.out.printf("Dimensión Vector b:%d\n", bVector.dim());
       	result = aVector.dotprod(bVector);
       	System.out.println("dotProd/scalar prod (a·b) = " + result);
       	matrix = aVector.outerprod(bVector);
       	matrix.println("outerprod (a /\\ b) = ");
       	matrix.determinant().println("Det.outerprod (a /\\ b) = ");
       	//System.out.println(" |a /\\ b|= " + cVector.determinant() + "\n");       	
       	cVector = aVector.crossprod(bVector);
       	cVector.println("Producto vectorial (axb) = ");
       	System.out.printf("Dimensión Vector prod.vect:%d\n", cVector.dim());
       	cVector = aVector.plus(bVector);
       	cVector.println("cVector (a+b)");
       	cVector = aVector.minus(bVector);
       	cVector.println("cVector (a-b)");
	}

	public static void cambioBase(Vector aVector, MatrixComplex base) {
		Vector cVector = new Vector();
       	System.out.println("------------- CAMBIO BASE -----------------");
       	aVector.println("Vector A");
       	base.println("Nueva base B");
    	cVector = aVector.baseChg(base);
    	cVector.println("A en base B");
    	cVector = cVector.baseExchg(base);
    	cVector.println("A en base original");

	}
	
	public static void vectorCalcs(Vector aVector, Vector bVector) {
    	Vector cVector;
    	Vector dVector;    	
    	
    	System.out.println("-------------- VECTORES --------------");
    	aVector.println("Vector a");
    	bVector.println("Vector b");
    	cVector = aVector.projection(bVector);
    	cVector.println("vector proyección a sobre b: ");
    	aVector.projectionScalar(bVector).println("proyección escalar a sobre b: ");
    	System.out.println("ángulo a b: " + aVector.angle(bVector));
       	System.out.println("ángulops a b: " + aVector.angleps(bVector));
    	dVector = aVector.rejection(bVector);
    	dVector.println("vector rejection a sobre b: ");
       	bVector.dotprod(dVector).println("Producto escalar b·d: ");
    	System.out.println("ángulo b d: " + bVector.angle(dVector));
       	System.out.println("distancia a b: " + aVector.distance(bVector));

	}
	
	public static void main(String[] args) {
    	MatrixComplex bMatrix;

    	Vector aVector;
    	Vector bVector;
    	Vector cVector;
    	Vector dVector;    	
    	Vector eVector;    	
    	Vector fVector;

    	Complex.setFixedON(3);
    	Complex.setFormatON();
    	
       	aVector = new Vector();
       	bVector = new Vector();
       	cVector = new Vector();
       	dVector = new Vector();
       	eVector = new Vector();
       	fVector = new Vector();

       	//CALCULO DE NORMAS
       	fVector = new Vector("2,-4,1");
    	calcNorms(fVector);

       	fVector = new Vector("1,2,3,4");
       	calcNorms(fVector);

       	fVector = new Vector("1,i,1,1-i");
       	calcNorms(fVector);
       	
       	fVector = new Vector("1-3i,-2+i,1+7i,1-i,-15-8i");
       	calcNorms(fVector);

       	// OPERACIONES CON VECTORES
       	aVector = new Vector("1,2");
       	bVector = new Vector("2,-1");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector("1,2,0");
       	bVector = new Vector("2,-1,0");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector("1,2,3");
       	bVector = new Vector("3,2,1");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector("2,-4,1");
       	bVector = new Vector("-2,4,-1");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector("2,-4,1");
       	bVector = new Vector("-3,-1,2");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new Vector("2i,-4i,1");
       	bVector = new Vector("-3,-i,2i");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new Vector("3-2i,2-4i,1,-3i");
       	bVector = new Vector("-3,-1,2+i,2+i");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new Vector("3,2,1,-3");
       	bVector = new Vector("-3,-2,-1,3");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new Vector("3-2i,2-4i,i");
       	bVector = new Vector("-3,-1,-1+2i");
       	vectorOperate(aVector, bVector);
    	
       	aVector = new Vector("3+2i,2,1-i,-2,-3+5i,-1,-1-4i");
       	bVector = new Vector("-3,-1+6i,-1,2-7i,1,-2+i,-3");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector("3,2,1,-2,-4");
       	bVector = new Vector("-3,-1,-1,2,2");
       	vectorOperate(aVector, bVector);
       	vectorOperate(bVector, aVector);

       	aVector = new Vector("3,2,1,-2,-4,1");
       	bVector = new Vector("-3,-1,-1,2,2,-1");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector("3,2");
       	bVector = new Vector("-3,-1");
       	vectorOperate(aVector, bVector);

       	// CAMBIO DE BASE
    	aVector = new Vector("3.5,-5.2,-1.9");
    	bMatrix = new MatrixComplex("1,-2,0;0,1,-2;-1,0,-1");
    	cambioBase(aVector, bMatrix);

    	aVector = new Vector("3,5");
    	bMatrix.base("1,2;2,1");
    	cambioBase(aVector, bMatrix);

    	aVector = new Vector("1,0,0");
    	bMatrix.base("1, 1, 0; 1, 0, 1; 0, 1, 1");
    	cambioBase(aVector, bMatrix);
    	aVector = new Vector("0,1,0");
    	cambioBase(aVector, bMatrix);
    	aVector = new Vector("0,0,1");
    	cambioBase(aVector, bMatrix);
    	
    	bMatrix.initMatrixRandomInteger(4);
    	cambioBase(aVector, bMatrix);

    	aVector = new Vector("1,2,0,-1");
    	bMatrix.base("1,1,0,0;-2,0,1,1;1,-1,0,1;1,0,1,0");
    	cambioBase(aVector, bMatrix);

    	aVector = new Vector("1,2,0,-1");
    	//bMatrix = new MatrixComplex("i,i,0,0;-2i,0,i,i;i,-i,0,i;i,0,i,0");
    	bMatrix.base("i,1,0,0;1-2i,0,1,i;1+i,-i,0,1+i;i,0,1-i,0");
    	cambioBase(aVector, bMatrix);

    	aVector = new Vector("2,i,2-i");
    	bMatrix.base("i,0,0;i,i,0;0,0,i");
    	//bMatrix = new MatrixComplex("i,i,0;0,i,0;0,0,i");
    	cambioBase(aVector, bMatrix);

    	aVector = new Vector("1,2");
    	bMatrix.base("2,3;1,-1");
    	//bMatrix = new MatrixComplex("i,i,0;0,i,0;0,0,i");
    	cambioBase(aVector, bMatrix);
    	
    	aVector = new Vector("2,1");
    	bVector = new Vector("-3,4");
    	vectorCalcs(aVector, bVector);
    	
    	aVector = new Vector("-3,5,-2");
    	bVector = new Vector("-7,-1,3");
    	vectorCalcs(aVector, bVector);
    	
    	aVector = new Vector("3,4i,-2i,7");
    	bVector = new Vector("5i,6,1,-4i");
    	vectorCalcs(aVector, bVector);
    	
    	aVector = new Vector("0,2,1,-1");
    	bVector = new Vector("1,-1,0,0");
    	vectorCalcs(aVector, bVector);
    	
    	aVector = new Vector("1,1,1");
    	bVector = new Vector("2,2,0");
    	vectorCalcs(aVector, bVector);

    	aVector = new Vector(" 6, 9, 14");
    	bMatrix.base(" 1, 1, 1; 1, 1, 2; 1, 2, 3");
       	cambioBase(aVector, bMatrix);
       	
       	aVector = new Vector(" 3, 2");
       	bVector = new Vector("-3,-1");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector(" 3, 2");
       	bVector = new Vector("-3,-1");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new Vector(" 3, 2, 0");
       	bVector = new Vector(" 0, 0, 3");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector(" 3, 2,-1");
       	bVector = new Vector("-3,-1, 2");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector(" 3, 2,-1");
       	bVector = new Vector(" 3,-3, 3");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector(" 3, 2, 5,-2,-4, 1");
       	bVector = new Vector("-3,-1,-1, 2, 2,-1");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector(" 3, 2, 5,-2,-4, 1, 0");
       	bVector = new Vector("7, -12, 1, 7, -4, -4, 8");
       	vectorOperate(aVector, bVector);
      	
       	aVector = new Vector(" 3, 2, 5,-2,-4, 1, 0");
       	bVector = new Vector("-3,-1,-1, 2, 2,-1, 0");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector(" 3-2i, 2+0i, 5+i,-2-3i,-4i");
       	bVector = new Vector("-3,-1,-1, 2, 2");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector(" -1+3i, -12-5i, 3+2i, -6+16i, -13i, -8-1i, 12-5i");
       	bVector = new Vector("   -3,     -1,    -1,      2,    2,     0,     0");
       	vectorOperate(aVector, bVector);

       	aVector = new Vector(" 3, 5,-2,-4, 1");
       	bVector = new Vector("-3,-1, 2, 2,-1");
       	vectorOperate(aVector, bVector);
       	
}

}
