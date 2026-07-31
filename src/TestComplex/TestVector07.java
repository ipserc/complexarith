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

import java.util.ArrayList;
import java.util.Arrays;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vectorcomplex.*;

public class TestVector07 {

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

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR PROJECTIONS TEST"));
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


		aVector = new VectorComplex("3, 2, 1");
		bVector = new VectorComplex("1 , 3, 0");
		vectorCalcs(aVector, bVector);

		aVector = new VectorComplex("3, 2, 1");
		bVector = new VectorComplex("2 , 6, 0");
       	vectorCalcs(aVector, bVector);

		aVector = new VectorComplex("1, 2, 3");
		bVector = new VectorComplex("1, 1, 0");
		// ArrayList<Double> angleList = Arrays.asList(0,Math.PI/6,Math.PI/20);
		double[] angleList = {0.0, Math.PI/6.0, Math.PI/20.0};
		cVector =new VectorComplex(bVector.dim());
		for (int i = 0; i < bVector.dim(); ++i) {
			vectorCalcs(aVector, bVector);
			cVector = cVector.plus(aVector.projection(bVector).rotation2D(angleList[i], bVector));
			bVector = bVector.shiftr();
		}
		//cVector = cVector.div(cVector.dim());
		cVector.println("cVector:");

	}

}
