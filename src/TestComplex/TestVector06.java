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

public class TestVector06 {

	/**
	 * @param args
	 */
	


	public static void productsResults(VectorComplex[] v) {
		int boxSize = 65;
		int dim = v[0].dim();
		VectorComplex u, t;

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTORIAL PRODUCTS - DIMENSION:" + dim));
		System.out.println(Complex.boxText1(boxSize, "Jacobi Identity v[0]^v[1]^v[2]^...[n] + ... + v[n]^v[0]^v[1]^...[n-1] = 0"));

		t = new VectorComplex(dim);
		for(int i = 0; i < dim; ++i ) {
			u = v[i].clone();
			for(int j = i; j < i + dim; ++j) {
				u = u.vectorprod(v[j%dim]);
			}
			t = t.plus(u);
			t.println("t["+i+"]: ");
		}
		t.println("result: ");
	}
	
	public static void main(String[] args) {
       	int dim = 6;
       	VectorComplex vb1;
		VectorComplex vector1, vector2;
       	int boxSize = 70;
       	boolean normalize, baseize;
       	MatrixComplex baseSVect;
		boolean isBase = true;

    	Complex.setFixedON(4);
    	Complex.setScientificOFF();
    	Complex.setFormatON();
    	Complex.printFormatStatus();
		Complex.numpPadPLUS();
    	/*
       	for(int i = 0; i < dim; ++i) {
       		v[i] = new Vector(dim);
       		v[i].initMatrixRandomInt(12);
       		v[i].println("v["+i+"]:");
       	}
       	*/

    	baseize = true;
    	normalize = true;
    	
    	Complex.printBoxTitleRandom(boxSize, "Orthogonallity & Orthonormallity in Matrix and Vectorial Spaces");
    	Complex.printBoxTextRandom(boxSize, "Vectorial Spaces");
    	if (baseize) {
	    	//vb1 = new Vector("1 ,2, 0, -1, 3");
	    	vb1 = new VectorComplex("1, 2, 3");
	       	//vb1 = new Vector("-1+3i,0,5-2i,0");
	
	    	vb1.println("Let vb1: ");
	    	Complex.printBoxTextRandom(boxSize, "Base from vb1: baseSVect");
	    	baseSVect = vb1.orthogonal();
	    	/** /
	    	baseSVect = new MatrixComplex(""
	    			+ "-1.6012815000E-01+4.8038446000E-01i, 0.0000000000E+00, 8.0064077000E-01-3.2025631000E-01i, 0.0000000000E+00; "
	    			+ "0.0000000000E+00, 1.0000000000E+00i, 0.0000000000E+00, 0.0000000000E+00;"
	    			+ " 5.5700665000E-01-6.5828059000E-01i, 0.0000000000E+00, 5.0636968000E-01, 0.0000000000E+00;"
	    			+ " 0.0000000000E+00, 0.0000000000E+00, 0.0000000000E+00, 1.0000000000E+00i");

			/**/
    	}
    	else {
    		baseSVect = new MatrixComplex(3);
    		baseSVect.initMatrixRandomInt(4);
    	}
    	
    	if (normalize) baseSVect = baseSVect.normalize();
    	
    	System.out.println("MatrixComplex: "+baseSVect.toMatrixComplex());
    	System.out.println("Octave: "+baseSVect.toOctave());
    	baseSVect.println("baseSVect");
    	baseSVect.determinant().println("baseSVect.determinant(): ");

    	{
	    	Complex.printBoxTextRandom(boxSize, "Check if baseSVect Is Base ");
	    	System.out.println("Vector.isBase(baseSVect)...: " + VectorComplex.isBase(baseSVect));
	    	for (int i = 0; i < baseSVect.rows(); ++i) {
	        	for (int j = 0; j < baseSVect.rows(); ++j) {
	        		VectorComplex v1 = new VectorComplex(baseSVect.getRow(i)); 
	        		VectorComplex v2 = new VectorComplex(baseSVect.getRow(j));
	        		if (i == j) continue;
	        		if (!v1.dotprod(v2).equals(Complex.ZERO, Complex.getMaxDecimals())) {
	        			isBase = false;
	        			v1.dotprod(v2).println("baseSVect:["+i+"]·["+j+"] = ");
	        			break;
	        		}
	        	}
	        	if (!isBase) break;
	    	}
	    	System.out.println("baseSVect Is Base ORTOGONAL: " + isBase);
   	}
    	
    	/*
    	 * Notar que es necesario utilizar el concepto de Ortogonal y Ortonormal de VECTORES
    	 */
    	Complex.printBoxTextRandom(boxSize, "Check Orthogonallity of baseSVect");
    	System.out.println("baseSVect.isOrthogonal(): " + VectorComplex.isOrthogonal(baseSVect));

    	Complex.printBoxTextRandom(boxSize, "Check Orthnormallity of baseSVect");
    	System.out.println("baseSVect.isOrthonormal(): " + VectorComplex.isOrthonormal(baseSVect));

       	Complex.printBoxTextRandom(boxSize, "Notable checks");
    	baseSVect.adjoint().println("baseSVect.adjoint()");
    	baseSVect.inverse().println("baseSVect.inverse()");
    	MatrixComplex bbAdj = baseSVect.times(baseSVect.adjoint());
    	MatrixComplex bAdjb = baseSVect.adjoint().times(baseSVect);
    	bbAdj.println("baseSVect.times(baseSVect.adjoint())");
    	bAdjb.println("baseSVect.adjoint().times(baseSVect)");

       	Complex.printBoxTextRandom(boxSize, "Base Change");
       	//Vector vector1 = new Vector(baseSVect.cols());
       	//vector1.initMatrixRandomInt(9);
       	vector1 = new VectorComplex("1 , 2 , 3");
       	vector1.println("vector1:");
       	vector1.baseChg(baseSVect);
       	vector1.println("vector1 in baseSVect:");

		Complex.printBoxTextRandom(boxSize, "Outer Product");
       	vector1 = new VectorComplex("1 , 2 , 3");
		vector2 = new VectorComplex("4, 5");
		vector1.println("vector1:");
		vector2.println("vector2:");
		vector1.outerprod(vector2).println("vector1.outerprod(vector2)");

		Complex.printBoxTextRandom(boxSize, "inner Product");
		vector1 = new VectorComplex("1 , 2 , 3");
		vector2 = new VectorComplex("-0.169030850000,+0.845154250000,-0.507092550000");
		vector1.println("vector1:");
		vector2.println("vector2:");
		vector1.innerprod(vector2).println("vector1.innerprod(vector2):");

	}

}
