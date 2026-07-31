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

public class TestVector02 {

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
	
	public static void vectorProperties(VectorComplex u, VectorComplex v) {
		int boxSize = 65;
		Complex uu = u.dotprod(u);
		Complex vv = v.dotprod(v);
		Complex uv = u.dotprod(v);
		VectorComplex uXv = u.crossprod(v);

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR PROPERTIES"));
    	u.println("Vector u");
    	v.println("Vector v");

    	System.out.println(Complex.boxText1(boxSize, "Projections"));
    	u.projection(v).println("vector proyección u sobre v: ");
    	u.projectionScalar(v).println("proyección escalar u sobre v: ");

    	System.out.println(Complex.boxText1(boxSize, "Propiedad (1) (u·v)/sqrt((u·u)(v·v)) = cos(ø)"));
    	System.out.println("ø = " + Complex.arccos(uv.divides(Complex.sqrt(uu.times(vv)))));
    	System.out.println("ángulo u v = " + (u.angle(v)-Math.PI));
    	
    	System.out.println(Complex.boxText1(boxSize, "Propiedad (2) ||u×v||/sqrt((u·u)(v·v)) = sin(ø)"));
    	System.out.println("ø = " + Math.asin(uXv.euc_norm()/(Complex.sqrt(uu.times(vv)).mod())));
    	System.out.println("ángulo u v = " + u.angle(v));
    	
    	System.out.println(Complex.boxText1(boxSize, "Propiedad (3) u·(u × v) = v·(u × v) = 0 "));
       	u.dotprod(uXv).println("u·(u × v) = ");
       	v.dotprod(uXv).println("v·(u × v) = ");
       	
    	System.out.println(Complex.boxText1(boxSize, "Propiedad (4) (u × v)·(u × v) = (u·u)(v·v)-(u·v)²"));
    	System.out.println("(u × v) · (u × v) = " + uXv.dotprod(uXv));
    	System.out.println("(u·u)(v·v)-(u·v)² = " + uu.times(vv).minus(uv.power(2)));
       	
		System.out.println(Complex.boxText1(boxSize, "Propiedad (5) ((au1+bu2)×(cv1+dv2)) = ac(u1×v1)+ad(u1×v2)+bc(u2×v1)+bd(u2×v2))"));
    	VectorComplex u2 = new VectorComplex(u.dim()); u2.initMatrixRandomInt(3);
    	VectorComplex v2 = new VectorComplex(v.dim()); v2.initMatrixRandomInt(3);
    	u.print("u1:"); u2.println(" - u2:");
    	v.print("v1:"); v2.println(" - v2:");
    	double a = 2, b = 3, c = -1, d = 4;
    	(u.prod(a)).plus(u2.prod(b)).crossprod((v.prod(c)).plus(v2.prod(d))).println("(au1+bu2)×(cv1+dv2)) = ");
    	(u.crossprod(v)).prod(a*c).plus(
    			(u.crossprod(v2)).prod(a*d)).plus(
    					(u2.crossprod(v)).prod(b*c).plus(
    							(u2.crossprod(v2)).prod(b*d))).println("ac(u1×v1)+ad(u1×v2)+bc(u2×v1)+bd(u2×v2))");
 
    	
		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR LEMA1 PROPERTIES"));
    	u.println("Vector u");
    	v.println("Vector v");
		VectorComplex vXu = v.crossprod(u);
    	VectorComplex w = new VectorComplex(u.dim()); w.initMatrixRandomInt(3);
    	w.println("Vector w");
		VectorComplex wXv = w.crossprod(v);
		Complex wv = w.dotprod(v);
		Complex wu = w.dotprod(u);
		System.out.println(Complex.boxText1(boxSize, "Propiedad (1) w·(u × v) = −u·(w × v)"));
       	w.dotprod(uXv).println("w·(u × v) = ");
       	u.dotprod(wXv).println("u·(w × v) = ");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (2) u × v = −v × u which implies u × u = 0"));
		uXv.println("u × v =");
		vXu.println("v × u =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (3) v × (v × u) = (v·u)v − (v·v)u"));
		v.crossprod(vXu).println("v × (v × u) =");
		v.prod(uv).minus(u.prod(vv)).println("(v·u)v − (v·v)u =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (3a) u × (u × v) = u(u·v) − (u·u)v"));
		u.crossprod(uXv).println("u × (u × v) =");
		u.prod(uv).minus(v.prod(uu)).println("u(u·v) − (u·u)v =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (4) w × (v × u) = −((w × v) × u) + (u·v)w + (w·v)u − 2(w·u)v"));
       	w.crossprod(uXv).println("w × (u × v) = ");
       	wXv.crossprod(u).plus(w.prod(uv)).plus(u.prod(wv)).minus(v.prod(wu.times(2))).println("((w × v) × u) + (u·v)w + (w·v)u − 2(w·u)v = ");
       	
       	System.out.println(Complex.boxText1(boxSize, "Vecctores Ortonormales"));
       	u.println("Vector u:");
       	VectorComplex uNorm = u.normalize();
		MatrixComplex uOrth = u.orthogonal();
       	VectorComplex uOrth1 = new VectorComplex(uOrth.getRow(0));
       	VectorComplex uOrth2 = new VectorComplex(uOrth.getRow(1));
       	VectorComplex uOrth3 = new VectorComplex(uOrth.getRow(2));
       	uNorm.println("Vector u norm:");
       	uOrth1.println("Vector u orth1:");
       	System.out.println("uNorm·uOrth1:" + uNorm.dotprod(uOrth1));
       	uNorm.crossprod(uOrth1).println("uNorm × uOrth1 = uOrth11 = ");
      	uOrth2.println("Vector u orth2:");
       	System.out.println("uNorm·uOrth2:" + uNorm.dotprod(uOrth2));
       	System.out.println("uOrth1·uOrth2:" + uOrth1.dotprod(uOrth2));
       	uOrth3.println("Vector u orth3:");
       	System.out.println("uNorm·uOrth3:" + uNorm.dotprod(uOrth3));
       	System.out.println("uOrth1·uOrth3:" + uOrth1.dotprod(uOrth3));
       	System.out.println("uOrth2·uOrth3:" + uOrth2.dotprod(uOrth3));       	
       	VectorComplex uo1Xuo2 = uOrth1.crossprod(uOrth2);
       	uo1Xuo2.println("uOrth1 × uOrth2");
       	VectorComplex uo2Xuo3 = uOrth2.crossprod(uOrth3);
       	uo2Xuo3.println("uOrth2 × uOrth3");
       	VectorComplex uo1Xuo3 = uOrth1.crossprod(uOrth3);
       	uo1Xuo3.println("uOrth1 × uOrth3");
       	
       	u.crossprod(uo1Xuo3).println("u × (uOrth1 × uOrth3) =");
	}
	
	public static void main(String[] args) {
    	VectorComplex aVector;
    	VectorComplex bVector;

    	Complex.setFixedON(3);
    	Complex.setFormatON();
    	
       	aVector = new VectorComplex();
       	bVector = new VectorComplex();
       	
       	aVector = new VectorComplex(3);
       	bVector = new VectorComplex(3);

       	aVector.initMatrixRandomInt(3); aVector.abs();
       	bVector.initMatrixRandomInt(3); bVector.abs();
       	aVector.println("a: ");
       	System.out.println("Euclidean Norm a = " + aVector.euc_norm());
       	bVector.println("b: ");
       	System.out.println("Euclidean Norm b = " + bVector.euc_norm());
       	aVector.crossprod(bVector).println("a × b: ");
       	System.out.println("Euclidean Norm (a × b)² = " + Math.pow((aVector.crossprod(bVector)).euc_norm(), 2));
       	bVector.crossprod(aVector).println("b × a: ");
       	aVector.dotprod(aVector.crossprod(bVector)).println("a·(a × b): ");
       	bVector.dotprod(aVector.crossprod(bVector)).println("b·(a × b): ");
       	aVector.crossprod(bVector).dotprod(aVector.crossprod(bVector)).println("(a × b)·(a × b): ");
       	System.out.println("(a·b)²: " + aVector.dotprod(bVector).power(2));
       	System.out.println("a·a * b·b :" + aVector.dotprod(aVector).times(bVector.dotprod(bVector)));

       	System.out.println("a·b: " + aVector.dotprod(bVector));
       	System.out.println("(a·b) * (a·b) : " + aVector.dotprod(bVector).times(aVector.dotprod(bVector)));
       	System.out.println("(a·b)² = |a·b| * |a·b| : " + aVector.dotprod(bVector).power(2));
      	aVector.crossprod(bVector).dotprod(aVector.crossprod(bVector)).println("(a × b)·(a × b): ");
      	System.out.println("|a × b|² (= (a × b)·(a × b)): " + Math.pow(aVector.crossprod(bVector).euc_norm(),2));
      	System.out.println("|a × b| : " + (aVector.crossprod(bVector).euc_norm()));
      	
      	vectorProperties(aVector, bVector);

      	aVector = new VectorComplex("0,.34567,0");
      	vectorProperties(aVector, bVector);
	}

}
