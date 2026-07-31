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
import com.ipserc.arith.vectorcomplex.*;

public class TestVector01 {

	/**
	 * @param args
	 */
	
	public static void calcNorms(VectorComplex fVector) {
		int p;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "NORMS TEST"));
       	fVector.println("fVector");
       	p = 1; System.out.println("Norm   " + p + " = " + fVector.p_norm(p));
       	p = 2; System.out.println("Norm   " + p + " = " + fVector.p_norm(p));
       	p = 3; System.out.println("Norm   " + p + " = " + fVector.p_norm(p));
       	p = 4; System.out.println("Norm   " + p + " = " + fVector.p_norm(p));
       	p = 66; System.out.println("Norm  " + p + " = " + fVector.p_norm(p));
       	p = 100; System.out.println("Norm " + p + " = " + fVector.p_norm(p));
       	System.out.println("Infinity  Norm = " + fVector.inf_norm());
       	System.out.println("Euclidean Norm = " + fVector.euc_norm());
       	System.out.println("Frobenius Norm = " + fVector.f_norm());
 	}
	
	public static void vectorOperate(VectorComplex aVector, VectorComplex bVector) {
    	Complex result = new Complex();
    	VectorComplex cVector;
    	MatrixComplex matrix = new MatrixComplex();
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR TEST"));
    	
		System.out.println(Complex.boxTextRandom(boxSize, "Vector Operations"));
       	aVector.println("Vector a");
       	System.out.printf("Dimensión Vector a:%d\n", aVector.dim());
       	bVector.println("Vector b");
       	System.out.printf("Dimensión Vector b:%d\n", bVector.dim());
       	result = aVector.dotprod(bVector);
       	System.out.println("dotProd/scalar prod (a·b) = " + result);
       	matrix = aVector.outerprod(bVector);
       	matrix.println("outerprod (a /\\o b) = ");
       	matrix.determinant().println("Det.outerprod (a /\\o b) = ");
       	matrix = aVector.kroneckerprod(bVector);
       	matrix.println("kroneckerprod (a /\\k b) = ");
       	matrix.determinant().println("Det.kroneckerprod (a /\\k b) = ");
       	//System.out.println(" |a /\\ b|= " + cVector.determinant() + "\n");       	
       	cVector = aVector.crossprod(bVector);
       	cVector.println("Producto vectorial (axb) = ");
       	if (cVector.dim() == aVector.dim()) {
	       	cVector.crossprod(aVector).println("Producto vectorial (cxa) = ");
	       	cVector.dotprod(aVector).println("Producto escalar (c·a) = ");
       	}
     	System.out.printf("Dimensión Vector prod.vect:%d\n", cVector.dim());
       	cVector = aVector.plus(bVector);
       	cVector.println("cVector (a+b)");
       	cVector = aVector.minus(bVector);
       	cVector.println("cVector (a-b)");
	}

	public static void cambioBase(VectorComplex aVector, MatrixComplex base) {
		VectorComplex cVector = new VectorComplex();
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR CHANGING BASE TEST"));
    	
       	aVector.println("Vector A");
       	base.println("Nueva base B");
    	cVector = aVector.baseChg(base);
    	cVector.println("A en base B");
    	cVector = cVector.baseExchg(base);
    	cVector.println("A en base original");

	}
	
	public static void vectorCalcs(VectorComplex aVector, VectorComplex bVector) {
    	VectorComplex cVector;
    	VectorComplex dVector;    	
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR CALCULATIONS TEST"));
    	aVector.println("Vector a");
    	bVector.println("Vector b");
    	cVector = aVector.projection(bVector);
    	cVector.println("vector proyección a sobre b: ");
    	aVector.projectionScalar(bVector).println("proyección escalar a sobre b: ");
    	System.out.println("ángulo a b: " + aVector.angle(bVector));
    	System.out.println("ángulo a b: " + Complex.rad_DMS(aVector.angle(bVector)));
       	System.out.println("ángulops a b: " + aVector.angleps(bVector));
       	System.out.println("ángulops a b: " + Complex.rad_DMS(aVector.angleps(bVector)));
    	dVector = aVector.rejection(bVector);
    	dVector.println("vector rejection a sobre b: ");
       	bVector.dotprod(dVector).println("Producto escalar b·d: ");
    	System.out.println("ángulo b d: " + bVector.angle(dVector));
    	System.out.println("ángulo b d: " + Complex.rad_DMS(bVector.angle(dVector)));
       	System.out.println("distancia a b: " + aVector.distance(bVector));

	}
	
	public static void main(String[] args) {
    	MatrixComplex bMatrix;

    	VectorComplex aVector;
    	VectorComplex bVector;
    	VectorComplex cVector;
    	VectorComplex dVector;    	
    	VectorComplex eVector;    	
    	VectorComplex fVector;

    	Complex.setFixedON(3);
    	Complex.setFormatON();
    	
       	aVector = new VectorComplex();
       	bVector = new VectorComplex();
       	cVector = new VectorComplex();
       	dVector = new VectorComplex();
       	eVector = new VectorComplex();
       	fVector = new VectorComplex();

       	//CALCULO DE NORMAS
       	fVector = new VectorComplex("2,-4,1");
    	calcNorms(fVector);

       	fVector = new VectorComplex("1,2,3,4");
       	calcNorms(fVector);

       	fVector = new VectorComplex("1,i,1,1-i");
       	calcNorms(fVector);
       	
       	fVector = new VectorComplex("1-3i,-2+i,1+7i,1-i,-15-8i");
       	calcNorms(fVector);

       	// OPERACIONES CON VECTORES
       	aVector = new VectorComplex("1,2");
       	bVector = new VectorComplex("2,-1");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex("1,2,0");
       	bVector = new VectorComplex("2,-1,0");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex("1,2,3");
       	bVector = new VectorComplex("3,2,1");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex("2,-4,1");
       	bVector = new VectorComplex("-2,4,-1");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex("2,-4,1");
       	bVector = new VectorComplex("-3,-1,2");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new VectorComplex("2i,-4i,1");
       	bVector = new VectorComplex("-3,-i,2i");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new VectorComplex("3-2i,2-4i,1,-3i");
       	bVector = new VectorComplex("-3,-1,2+i,2+i");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new VectorComplex("3,2,1,-3");
       	bVector = new VectorComplex("-15,-10,-5,15");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new VectorComplex("3-2i,2-4i,i");
       	bVector = new VectorComplex("-3,-1,-1+2i");
       	vectorOperate(aVector, bVector);
    	
       	aVector = new VectorComplex("3+2i,2,1-i,-2,-3+5i,-1,-1-4i");
       	bVector = new VectorComplex("-3,-1+6i,-1,2-7i,1,-2+i,-3");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex("3,2,1,-2,-4");
       	bVector = new VectorComplex("-3,-1,-1,2,2");
       	vectorOperate(aVector, bVector);
       	vectorOperate(bVector, aVector);

       	aVector = new VectorComplex("3,2,1,-2,-4,1");
       	bVector = new VectorComplex("-3,-1,-1,2,2,-1");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex("3,2");
       	bVector = new VectorComplex("-3,-1");
       	vectorOperate(aVector, bVector);

       	// CAMBIO DE BASE
    	aVector = new VectorComplex("3.5,-5.2,-1.9");
    	bMatrix = new MatrixComplex("1,-2,0;0,1,-2;-1,0,-1");
    	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex("3,5");
    	bMatrix = new MatrixComplex("1,2;2,1");
    	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex("1,0,0");
    	bMatrix = new MatrixComplex("1, 1, 0; 1, 0, 1; 0, 1, 1");
    	cambioBase(aVector, bMatrix);
    	aVector = new VectorComplex("0,1,0");
    	cambioBase(aVector, bMatrix);
    	aVector = new VectorComplex("0,0,1");
    	cambioBase(aVector, bMatrix);
    	
    	bMatrix.initMatrixRandomInt(4);
    	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex("1,2,0,-1");
    	bMatrix = new MatrixComplex("1,1,0,0;-2,0,1,1;1,-1,0,1;1,0,1,0");
    	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex("1,2,0,-1");
    	bMatrix = new MatrixComplex("i,i,0,0;-2i,0,i,i;i,-i,0,i;i,0,i,0");
    	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex("2,i,2-i");
    	bMatrix = new MatrixComplex("i,i,0;0,i,0;0,0,i");
    	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex("1,2,-1");
    	bMatrix = new MatrixComplex("i,i,0;0,i,0;0,0,i");
    	cambioBase(aVector, bMatrix);
    	
    	aVector = new VectorComplex("2,1");
    	bVector = new VectorComplex("-3,4");
    	vectorCalcs(aVector, bVector);
    	
    	aVector = new VectorComplex("-3,5,-2");
    	bVector = new VectorComplex("-7,-1,3");
    	vectorCalcs(aVector, bVector);
    	
    	aVector = new VectorComplex("3,4i,-2i,7");
    	bVector = new VectorComplex("5i,6,1,-4i");
    	vectorCalcs(aVector, bVector);
    	
    	aVector = new VectorComplex("0,2,1,-1");
    	bVector = new VectorComplex("1,-1,0,0");
    	vectorCalcs(aVector, bVector);
    	
    	aVector = new VectorComplex("1,1,1");
    	bVector = new VectorComplex("2,2,0");
    	vectorCalcs(aVector, bVector);

    	aVector = new VectorComplex(" 6, 9, 14");
    	bMatrix = new MatrixComplex(" 1, 1, 1; 1, 1, 2; 1, 2, 3");
       	cambioBase(aVector, bMatrix);
       	
    	aVector = new VectorComplex(" 1, 1, 1");
    	bMatrix = new MatrixComplex(" .5, 0, 0; 0, 0.33333333333333333, 0; 0, 0, .25");
       	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex(" 1, 1, 1");
    	bMatrix = new MatrixComplex(" 0, 0.33333333333333333, 0;  .5, 0, 0; 0, 0, .25");
       	cambioBase(aVector, bMatrix);

       	aVector = new VectorComplex(" -2, 0, 1");
       	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex(" -3, -5, 4");
       	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex(" 6, 9, 14");
    	bMatrix = new MatrixComplex(" 1, i, 1; -i, 1, 2+i; 1, 2-i, 3i");
       	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex(" -2, 0, 1");
       	cambioBase(aVector, bMatrix);

    	aVector = new VectorComplex(" -3, -5, 4");
       	cambioBase(aVector, bMatrix);

       	aVector = new VectorComplex(" 3, 2");
       	bVector = new VectorComplex("-3,-1");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex(" 3, 2");
       	bVector = new VectorComplex("-3,-1");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new VectorComplex(" 3, 2, 0");
       	bVector = new VectorComplex(" 0, 0, 3");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex(" 3, 2,-1");
       	bVector = new VectorComplex("-3,-1, 2");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex(" 3, 2,-1");
       	bVector = new VectorComplex(" 3,-3, 3");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex(" 3, 2, 5,-2,-4, 1");
       	bVector = new VectorComplex("-3,-1,-1, 2, 2,-1");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex(" 3, 2, 5,-2,-4, 1, 0");
       	bVector = new VectorComplex("7, -12, 1, 7, -4, -4, 8");
       	vectorOperate(aVector, bVector);
      	
       	aVector = new VectorComplex(" 3, 2, 5,-2,-4, 1, 0");
       	bVector = new VectorComplex("-3,-1,-1, 2, 2,-1, 0");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex(" 3-2i, 2+0i, 5+i,-2-3i,-4i");
       	bVector = new VectorComplex("-3,-1,-1, 2, 2");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex(" -1+3i, -12-5i, 3+2i, -6+16i, -13i, -8-1i, 12-5i");
       	bVector = new VectorComplex("   -3,     -1,    -1,      2,    2,     0,     0");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex(" 3, 5,-2,-4, 1");
       	bVector = new VectorComplex("-3,-1, 2, 2,-1");
       	vectorOperate(aVector, bVector);
       
       	aVector = new VectorComplex("3,2,1");
       	bVector = new VectorComplex("-3,-2,-1");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new VectorComplex("3,2,1");
       	bVector = new VectorComplex("5,7,11");
       	vectorOperate(aVector, bVector);
       	
       	aVector = new VectorComplex("1,2,3,1");
       	bVector = new VectorComplex("0,3,2,1");
       	vectorOperate(aVector, bVector);

       	aVector = new VectorComplex("1,2,3,1,0,0,0");
       	bVector = new VectorComplex("0,3,2,1,0,0,0");
       	vectorOperate(aVector, bVector);

		aVector = new VectorComplex("3, 2, 1");
		bVector = new VectorComplex("1 , 1, 0");
       	vectorCalcs(aVector, bVector);

	}

}
