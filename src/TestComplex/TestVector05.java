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

public class TestVector05 {

	/**
	 * @param args
	 */
	


	public static void productsResults(Vector[] v) {
		int boxSize = 65;
		int dim = v[0].dim();
		Vector u, t;

		System.out.println(Complex.boxTitleRandom(boxSize, "VECTORIAL PRODUCTS - DIMENSION:" + dim));
		System.out.println(Complex.boxText1(boxSize, "Jacobi Identity v[0]^v[1]^v[2]^...[n] + ... + v[n]^v[0]^v[1]^...[n-1] = 0"));

		t = new Vector(dim);
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
    	Vector v[] = new Vector[dim];
		int boxSize = 65;

    	Complex.setFixedON(3);
    	Complex.setFormatON();
    	/*
       	for(int i = 0; i < dim; ++i) {
       		v[i] = new Vector(dim);
       		v[i].initMatrixRandomInteger(12);
       		v[i].println("v["+i+"]:");
       	}
       	*/
    	
    	Vector vb1 = new Vector("1 ,2, -1");
    	vb1.println("vb1");
    	MatrixComplex B1 = new MatrixComplex("1,1,0;0,1,2;1,0,1");
    	B1.println("B1");
    	MatrixComplex B2 = new MatrixComplex("1,0,0;2,1,0;1,1,1");
    	B2.println("B2");
    	Vector.matBaseChg(B1, B2).println("Matriz cambio base B1 -> B2");
    	Vector vb2 = vb1.baseChg(B1, B2);
    	vb2.println("vb2");
    	Vector vb3 = vb2.baseExchg(B1, B2);
    	vb3.println("vb3");
    	Vector vb4 = vb2.baseChg(B2, B1);
    	vb4.println("vb4");
     	
	}
       	
}
