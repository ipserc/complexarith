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

public class TestVector05 {

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
    	VectorComplex v[] = new VectorComplex[dim];
		int boxSize = 65;

		// Complex.resetFormatStatus();
		// Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotON();
    	// Complex.digits(10000000);
    	//Complex.setScientificON(4);
    	Complex.setFixedON(4);
    	Complex.numpPadPLUS();

    	/*
       	for(int i = 0; i < dim; ++i) {
       		v[i] = new Vector(dim);
       		v[i].initMatrixRandomInt(12);
       		v[i].println("v["+i+"]:");
       	}
       	*/
    	
    	VectorComplex vb1 = new VectorComplex("1 ,2, 0, -1, 3");
    	vb1.println("vb1");
    	MatrixComplex baseOrtog = vb1.orthogonal();//.times(330);
    	System.out.println("MatrixComplex: "+baseOrtog.toMatrixComplex());
    	System.out.println("Octave: "+baseOrtog.toOctave());
    	baseOrtog.println("baseOrtog");
    	VectorComplex vb1otn1 = new VectorComplex(baseOrtog.getRow(0));
    	vb1otn1.println("vb1otn1:");
    	System.out.println("vb1otn1.norm():" + vb1otn1.norm());
    	VectorComplex vb1otn2 = new VectorComplex(baseOrtog.getRow(1));;
    	vb1otn2.println("vb1otn2:");
    	vb1otn1.dotprod(vb1otn2).println("vb1otn1.dotprod(vb1otn2):");
    	/*
    	 * Notar que es necesario utilizar el concepto de Ortogonal y Ortonormal de VECTORES
    	 */
    	Complex.printBoxTextRandom(boxSize, "Check Orthogonallity of baseSVect");
    	System.out.println("baseSVect.isOrthogonal(): " + VectorComplex.isOrthogonal(baseOrtog));
    	baseOrtog.times(baseOrtog.adjoint()).println("baseOrtog.times(baseOrtog.adjoint())");
    	baseOrtog.adjoint().times(baseOrtog).println("baseOrtog.adjoint().times(baseOrtog)");
    	baseOrtog.adjoint().println("baseOrtog.adjoint()");
    	baseOrtog.inverse().println("baseOrtog.inverse()");
    	
    	MatrixComplex B1 = new MatrixComplex("1,1,0;0,1,2;1,0,1");
    	B1.println("B1");
    	MatrixComplex B2 = new MatrixComplex("1,0,0;2,1,0;1,1,1");
    	B2.println("B2");
    	VectorComplex.matBaseChg(B1, B2).println("Matriz cambio base B1 -> B2");
    	vb1 = new VectorComplex(B1.cols());
    	vb1.initMatrixRandomInt(20);
    	vb1.println("vb1 in Base B1:");
    	VectorComplex vb2 = vb1.baseChg(B1, B2);
    	vb2.println("vb2: vb1 B1 -> B2: ");
    	VectorComplex vb3 = vb2.baseExchg(B1, B2);
    	vb3.println("vb3: vb2 B2 -> B1: ");
    	VectorComplex vb4 = vb2.baseChg(B2, B1);
    	vb4.println("vb4: vb2 B2 -> B1. ");
     	
    	Complex.printBoxTitleRandom(boxSize, "CHECK base() Method");
    	MatrixComplex base;
    	{
	    	VectorComplex coord1 = new VectorComplex("1,1,0");
	    	coord1.println("coord1:");
	    	base = coord1.base();
	    	base.println("Base:");
    	}
    	{
    		MatrixComplex coord1 = new MatrixComplex("0,1,-3;0,2,0");
	    	coord1.println("coord1:");
	    	base = VectorComplex.base(coord1);
	    	base.println("Base:");
    	}
    	{
	    	MatrixComplex coord1 = new MatrixComplex("3,0,1");
	    	coord1.println("coord1:");
	    	base = VectorComplex.base(coord1);
	    	base.println("Base");
    	}
    	{
    		MatrixComplex coord1 = new MatrixComplex("1,0,0;1,0,0");
	    	coord1.println("coord1:");
	    	base = VectorComplex.base(coord1);
	    	base.println("Base:");
    	}

	}
       	
}
