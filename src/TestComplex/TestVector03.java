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

public class TestVector03 {

	/**
	 * @param args
	 */
	
	public static void calcNorms(Vector fVector) {
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
	
	public static void vectorOperate(Vector u, Vector v) {
    	Complex result = new Complex();
    	Vector cVector;
    	MatrixComplex matrix = new MatrixComplex();
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR TEST"));
    	
		System.out.println(Complex.boxTextRandom(boxSize, "Vector Operations"));
       	u.println("Vector a");
       	System.out.printf("Dimensión Vector a:%d\n", u.dim());
       	v.println("Vector b");
       	System.out.printf("Dimensión Vector b:%d\n", v.dim());
       	result = u.dotprod(v);
       	System.out.println("dotProd/scalar prod (a·b) = " + result);
       	matrix = u.outerprod(v);
       	matrix.println("outerprod (a /\\ b) = ");
       	matrix.determinant().println("Det.outerprod (a /\\ b) = ");
       	//System.out.println(" |a /\\ b|= " + cVector.determinant() + "\n");       	
       	cVector = u.crossprod(v);
       	cVector.println("Producto vectorial (axb) = ");
       	System.out.printf("Dimensión Vector prod.vect:%d\n", cVector.dim());
       	cVector = u.plus(v);
       	cVector.println("cVector (a+b)");
       	cVector = u.minus(v);
       	cVector.println("cVector (a-b)");
	}

	public static void cambioBase(Vector u, MatrixComplex base) {
		Vector cVector = new Vector();
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR CHANGING BASE TEST"));
    	
       	u.println("Vector A");
       	base.println("Nueva base B");
    	cVector = u.baseChg(base);
    	cVector.println("A en base B");
    	cVector = cVector.baseExchg(base);
    	cVector.println("A en base original");

	}
	
	public static void crossProdProperties(Vector u, Vector v, Vector u2, Vector v2, Vector w) {
		int boxSize = 65;
		Complex uu = u.dotprod(u);
		Complex vv = v.dotprod(v);
		Complex uv = u.dotprod(v);
		Vector uXv = u.crossprod(v);
		Vector vXw = v.crossprod(w);


		System.out.println(Complex.boxTitleRandom(boxSize, "CROSS PROD PROPERTIES"));
    	u.println("Vector u");
    	v.println("Vector v");

    	System.out.println(Complex.boxText1(boxSize, "Projections"));
    	u.projection(v).println("vector proyección u sobre v: ");
    	u.projectionScalar(v).println("proyección escalar u sobre v: ");

    	System.out.println(Complex.boxText1(boxSize, "Propiedad (1c) (u·v)/sqrt((u·u)(v·v)) = cos(ø)"));
    	System.out.println("ø = " + Complex.arccos(uv.divides(Complex.sqrt(uu.times(vv)))));
    	System.out.println("ángulo u v = " + (u.angle(v)-Math.PI));
    	
    	System.out.println(Complex.boxText1(boxSize, "Propiedad (2c) ||u×v||/sqrt((u·u)(v·v)) = sin(ø)"));
    	System.out.println("ø = " + Math.asin(uXv.euc_norm()/(Complex.sqrt(uu.times(vv)).mod())));
    	System.out.println("ángulo u v = " + u.angle(v));
    	
    	System.out.println(Complex.boxText1(boxSize, "Propiedad (3c) u·(u×v) = v·(u×v) = 0 "));
       	u.dotprod(uXv).println("u·(u×v) = ");
       	v.dotprod(uXv).println("v·(u×v) = ");
       	
    	System.out.println(Complex.boxText1(boxSize, "Propiedad (4c) (u×v)·(u×v) = (u·u)(v·v)-(u·v)²"));
    	System.out.println("(u×v)·(u×v) = " + uXv.dotprod(uXv));
    	System.out.println("(u·u)(v·v)-(u·v)² = " + uu.times(vv).minus(uv.power(2)));
       	
		System.out.println(Complex.boxText1(boxSize, "Propiedad (5c) ((au1+bu2)×(cv1+dv2)) = ac(u1×v1)+ad(u1×v2)+bc(u2×v1)+bd(u2×v2))"));
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
		Vector vXu = v.crossprod(u);
    	w.println("Vector w");
		Vector wXv = w.crossprod(v);
		Complex wv = w.dotprod(v);
		Complex wu = w.dotprod(u);
		System.out.println(Complex.boxText1(boxSize, "Jacobi Identity u×(v×w) + v×(w×u) + w×(u×v) = 0"));
		Vector uXvXw = u.crossprod((v.crossprod(w)));
		Vector vXwXu = v.crossprod((w.crossprod(u)));
		Vector wXuXv = w.crossprod((u.crossprod(v)));
		uXvXw.plus(vXwXu).plus(wXuXv).println("u×(v×w) + v×(w×u) + w×(u×v) =" );
		System.out.println(Complex.boxText1(boxSize, "Propiedad (1c) w·(u×v) = −u·(w×v)"));
       	w.dotprod(uXv).println("w·(u×v) = ");
       	u.dotprod(wXv).println("u·(w×v) = ");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (2c) u×v = −v×u which implies u×u = 0"));
		uXv.println("u×v =");
		vXu.println("v×u =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (3c) u×(v×w) = v(u·w) − w(u·v)"));
		u.crossprod(vXw).println("u×(v×w) =");
		v.prod(u.dotprod(w)).minus(w.prod(u.dotprod(v))).println("v(w·u) − w(u·v) =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (3ac) v×(v×u) = (v·u)v − (v·v)u"));
		v.crossprod(vXu).println("v×(v×u) =");
		v.prod(uv).minus(u.prod(vv)).println("(v·u)v − (v·v)u =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (3bc) u×(u×v) = u(u·v) − (u·u)v"));
		u.crossprod(uXv).println("u×(u×v) =");
		u.prod(uv).minus(v.prod(uu)).println("u(u·v) − (u·u)v =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (4c) w×(v×u) = −((w×v)×u) + (u·v)w + (w·v)u − 2(w·u)v"));
       	w.crossprod(uXv).println("w×(u×v) = ");
       	wXv.crossprod(u).plus(w.prod(uv)).plus(u.prod(wv)).minus(v.prod(wu.times(2))).println("((w×v)×u) + (u·v)w + (w·v)u − 2(w·u)v = ");
       	
       	System.out.println(Complex.boxText1(boxSize, "Vectores Ortonormales"));
       	u.println("Vector u:");
       	Vector uNorm = u.normalize();
       	Vector uOrth1 = uNorm.orthogonal(0);
       	Vector uOrth2 = uNorm.orthogonal(1);
       	Vector uOrth3 = uNorm.orthogonal(2);
       	uNorm.println("Vector u norm:");
       	uOrth1.println("Vector u orth1:");
       	System.out.println("uNorm·uOrth1:" + uNorm.dotprod(uOrth1));
       	uNorm.crossprod(uOrth1).println("uNorm×uOrth1 = uOrth11 = ");
      	uOrth2.println("Vector u orth2:");
       	System.out.println("uNorm·uOrth2:" + uNorm.dotprod(uOrth2));
       	System.out.println("uOrth1·uOrth2:" + uOrth1.dotprod(uOrth2));
       	uOrth3.println("Vector u orth3:");
       	System.out.println("uNorm·uOrth3:" + uNorm.dotprod(uOrth3));
       	System.out.println("uOrth1·uOrth3:" + uOrth1.dotprod(uOrth3));
       	System.out.println("uOrth2·uOrth3:" + uOrth2.dotprod(uOrth3));       	
       	Vector uo1Xuo2 = uOrth1.crossprod(uOrth2);
       	uo1Xuo2.println("uOrth1×uOrth2");
       	Vector uo2Xuo3 = uOrth2.crossprod(uOrth3);
       	uo2Xuo3.println("uOrth2×uOrth3");
       	Vector uo1Xuo3 = uOrth1.crossprod(uOrth3);
       	uo1Xuo3.println("uOrth1×uOrth3");
       	
       	u.crossprod(uo1Xuo3).println("u×(uOrth1×uOrth3) =");

       	System.out.println(Complex.boxText1(boxSize, "Base"));
       	Vector base1[] = u.baseV();
       	for(int i = 0; i < u.dim(); ++i)
       		base1[i].println("base1"+i+":");
      	for(int i = 0; i < u.dim(); ++i)
      		for(int j = i+1; j < u.dim(); ++j)
      			System.out.println(i+"·"+j+": " + base1[i].dotprod(base1[j]));
	}
	
	public static void vectorProdProperties(Vector u, Vector v, Vector u2, Vector v2, Vector w) {
		int boxSize = 65;
		Complex uu = u.dotprod(u);
		Complex vv = v.dotprod(v);
		Complex uv = u.dotprod(v);
		Vector uXv = u.vectorprod(v);
		Vector vXw = v.vectorprod(w);

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR PROD PROPERTIES"));
    	u.println("Vector u");
    	v.println("Vector v");

    	System.out.println(Complex.boxText1(boxSize, "Projections"));
    	u.projection(v).println("vector proyección u sobre v: ");
    	u.projectionScalar(v).println("proyección escalar u sobre v: ");

    	System.out.println(Complex.boxText1(boxSize, "Propiedad (1v) (u·v)/sqrt((u·u)(v·v)) = cos(ø)"));
    	System.out.println("ø = " + Complex.arccos(uv.divides(Complex.sqrt(uu.times(vv)))));
    	System.out.println("ángulo u v = " + (u.angle(v)-Math.PI));
    	
    	System.out.println(Complex.boxText1(boxSize, "Propiedad (2v) ||u^v||/sqrt((u·u)(v·v)) = sin(ø)"));
    	System.out.println("ø = " + Math.asin(uXv.euc_norm()/(Complex.sqrt(uu.times(vv)).mod())));
    	System.out.println("ángulo u v = " + u.angle(v));
    	
    	System.out.println(Complex.boxText1(boxSize, "Propiedad (3v) u·(u^v) = v·(u^v) = 0 "));
       	u.dotprod(uXv).println("u·(u^v) = ");
       	v.dotprod(uXv).println("v·(u^v) = ");
       	
    	System.out.println(Complex.boxText1(boxSize, "Propiedad (4v) (u^v)·(u^v) = (u·u)(v·v)-(u·v)²"));
    	System.out.println("(u^v)·(u^v) = " + uXv.dotprod(uXv));
    	System.out.println("(u·u)(v·v)-(u·v)² = " + uu.times(vv).minus(uv.power(2)));
       	
		System.out.println(Complex.boxText1(boxSize, "Propiedad (5v) ((au1+bu2)^(cv1+dv2)) = ac(u1^v1)+ad(u1^v2)+bc(u2^v1)+bd(u2^v2))"));
    	u.print("u1:"); u2.println(" - u2:");
    	v.print("v1:"); v2.println(" - v2:");
    	double a = 2, b = 3, c = -1, d = 4;
    	(u.prod(a)).plus(u2.prod(b)).vectorprod((v.prod(c)).plus(v2.prod(d))).println("(au1+bu2)^(cv1+dv2)) = ");
    	(u.vectorprod(v)).prod(a*c).plus(
    			(u.vectorprod(v2)).prod(a*d)).plus(
    					(u2.vectorprod(v)).prod(b*c).plus(
    							(u2.vectorprod(v2)).prod(b*d))).println("ac(u1^v1)+ad(u1^v2)+bc(u2^v1)+bd(u2^v2))");
 
    	
		System.out.println(Complex.boxTitleRandom(boxSize, "VECTOR LEMA1 PROPERTIES"));
    	u.println("Vector u");
    	v.println("Vector v");
		Vector vXu = v.vectorprod(u);
    	w.println("Vector w");
		Vector wXv = w.vectorprod(v);
		Complex wv = w.dotprod(v);
		Complex wu = w.dotprod(u);
		System.out.println(Complex.boxText1(boxSize, "Jacobi Identity u^(v^w) + v^(w^u) + w^(u^v) = 0"));
		Vector uXvXw = u.vectorprod((v.vectorprod(w)));
		Vector vXwXu = v.vectorprod((w.vectorprod(u)));
		Vector wXuXv = w.vectorprod((u.vectorprod(v)));
		uXvXw.plus(vXwXu).plus(wXuXv).println("u^(v^w) + v^(w^u) + w^(u^v) =" );
		System.out.println(Complex.boxText1(boxSize, "Propiedad (1v) w·(u^v) = −u·(w^v)"));
       	w.dotprod(uXv).println("w·(u^v) = ");
       	u.dotprod(wXv).println("u·(w^v) = ");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (2v) u^v = −v^u which implies u^u = 0"));
		uXv.println("u^v =");
		vXu.println("v^u =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (3c) u^(v^w) = v(u·w) − w(u·v)"));
		u.vectorprod(vXw).println("u^(v^w) =");
		v.prod(u.dotprod(w)).minus(w.prod(u.dotprod(v))).println("v(u·w) − w(u·v) =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (3av) v^(v^u) = (v·u)v − (v·v)u"));
		v.vectorprod(vXu).println("v^(v^u) =");
		v.prod(uv).minus(u.prod(vv)).println("(v·u)v − (v·v)u =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (3bv) u^(u^v) = u(u·v) − (u·u)v"));
		u.vectorprod(uXv).println("u^(u^v) =");
		u.prod(uv).minus(v.prod(uu)).println("u(u·v) − (u·u)v =");
		System.out.println(Complex.boxText1(boxSize, "Propiedad (4v) w^(v^u) = −((w^v)^u) + (u·v)w + (w·v)u − 2(w·u)v"));
       	w.vectorprod(uXv).println("w^(u^v) = ");
       	wXv.vectorprod(u).plus(w.prod(uv)).plus(u.prod(wv)).minus(v.prod(wu.times(2))).println("((w^v)^u) + (u·v)w + (w·v)u − 2(w·u)v = ");
       	
       	System.out.println(Complex.boxText1(boxSize, "Vectores Ortonormales"));
       	u.println("Vector u:");
       	Vector uNorm = u.normalize();
       	Vector uOrth1 = uNorm.orthogonal(0);
       	Vector uOrth2 = uNorm.orthogonal(1);
       	Vector uOrth3 = uNorm.orthogonal(2);
       	uNorm.println("Vector u norm:");
       	uOrth1.println("Vector u orth1:");
       	System.out.println("uNorm·uOrth1:" + uNorm.dotprod(uOrth1));
       	uNorm.vectorprod(uOrth1).println("uNorm^uOrth1 = uOrth11 = ");
      	uOrth2.println("Vector u orth2:");
       	System.out.println("uNorm·uOrth2:" + uNorm.dotprod(uOrth2));
       	System.out.println("uOrth1·uOrth2:" + uOrth1.dotprod(uOrth2));
       	uOrth3.println("Vector u orth3:");
       	System.out.println("uNorm·uOrth3:" + uNorm.dotprod(uOrth3));
       	System.out.println("uOrth1·uOrth3:" + uOrth1.dotprod(uOrth3));
       	System.out.println("uOrth2·uOrth3:" + uOrth2.dotprod(uOrth3));       	
       	Vector uo1Xuo2 = uOrth1.vectorprod(uOrth2);
       	uo1Xuo2.println("uOrth1^uOrth2");
       	Vector uo2Xuo3 = uOrth2.vectorprod(uOrth3);
       	uo2Xuo3.println("uOrth2^uOrth3");
       	Vector uo1Xuo3 = uOrth1.vectorprod(uOrth3);
       	uo1Xuo3.println("uOrth1^uOrth3");
       	
       	u.vectorprod(uo1Xuo3).println("u^(uOrth1^uOrth3) =");

       	System.out.println(Complex.boxText1(boxSize, "Base"));
       	Vector base1[] = u.baseV();
       	for(int i = 0; i < u.dim(); ++i)
       		base1[i].println("base1"+i+":");
      	for(int i = 0; i < u.dim(); ++i)
      		for(int j = i+1; j < u.dim(); ++j)
      			System.out.println(i+"·"+j+": " + base1[i].dotprod(base1[j]));
	}

	public static void productsResults(Vector u, Vector v, Vector u2, Vector v2, Vector w) {
		Vector cVector, dVector;
		Vector eVector, fVector;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "CROSS PRODUCTS - DIMENSION:" + u.dim()));
		if (u.dim() == v.dim())
		u.println("Vector A:");
		v.println("Vector B:");
		cVector = u.crossprod(v);
		cVector.println("Vector C (A×B):");
		dVector = v.crossprod(u);
		dVector.println("Vector D (B×A):");
		if (u.dim() == cVector.dim()) {
			cVector.crossprod(u).println("(C×A):");
			cVector.vectorprod(u).println("(C^u):");
			cVector.dotprod(u).println("(C·u):");
			crossProdProperties(u, v, u2, v2, w);
		}
		else {
			System.out.println(Complex.boxText1(boxSize, "* * * * * CROSSPROD NOT DEFINED * * * * *"));
		}

		
		System.out.println(Complex.boxTitleRandom(boxSize, "VECTORIAL PRODUCTS - DIMENSION:" + u.dim()));
		eVector = u.vectorprod(v);
		eVector.println("Vector E (A^B):");
		fVector = v.vectorprod(u);
		fVector.println("Vector F (B^A):");
		eVector.vectorprod(u).println("(E^A):");
		eVector.dotprod(u).println("(E·A):");	
		vectorProdProperties(u, v, u2, v2, w);
	}
	
	public static void main(String[] args) {
    	Vector u ,v, u2, v2, w;
    	int dim;
		int boxSize = 65;

    	Complex.setFixedON(3);
    	Complex.setFormatON();
    	
       	dim = 3;
       	u = new Vector(dim);
       	v = new Vector(dim);
       	u2 = new Vector(dim);
       	v2 = new Vector(dim);
       	w = new Vector(dim);
       	u.initMatrixRandomInteger(3);
       	v.initMatrixRandomInteger(3);
       	u2.initMatrixRandomInteger(3);
    	v2.initMatrixRandomInteger(3);
    	w.initMatrixRandomInteger(3);
       	productsResults(u, v, u2, v2, w);
       	
       	dim = 5;
       	u = new Vector(dim);
       	v = new Vector(dim);
       	u2 = new Vector(dim);
       	v2 = new Vector(dim);
       	w = new Vector(dim);
       	u.initMatrixRandomInteger(3);
       	v.initMatrixRandomInteger(3);
       	u2.initMatrixRandomInteger(3);
    	v2.initMatrixRandomInteger(3);
    	w.initMatrixRandomInteger(3);
      	productsResults(u, v, u2, v2, w);

       	dim = 7;
       	u = new Vector(dim);
       	v = new Vector(dim);
       	u2 = new Vector(dim);
       	v2 = new Vector(dim);
       	w = new Vector(dim);
       	u.initMatrixRandomInteger(3);
       	v.initMatrixRandomInteger(3);
       	u2.initMatrixRandomInteger(3);
    	v2.initMatrixRandomInteger(3);
    	w.initMatrixRandomInteger(3);
      	productsResults(u, v, u2, v2, w);

	}
       	
}
