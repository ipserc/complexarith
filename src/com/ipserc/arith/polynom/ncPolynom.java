/**
 * 
 */
package com.ipserc.arith.polynom;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * @author ipserc
 *
 */
public class ncPolynom extends MatrixComplex {
	private Complex[][] polyNorm;
	private Polynom remainder;
	private int dOffset = 0; // Degree offset
	
	private final static String HEADINFO = "Polynom --- INFO:";
	private static double sampleBase = 300;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Instantiates a null polynomial.
	 */
	public ncPolynom() {
		super();
	}
	
	/**
	 * Instantiates a "degree" degree polynomial with its coefficients to zero.
	 * Instantiates a MatrixComplex object in a row with "degree + 1" columns representing a "degree" degree polynomial.
	 * The polynomial is stored with the coefficients according to the numbering of the indexes of the columns.
	 * Poly [0], is the term in x ^ (dOffs+0).
	 * Poly [1], is the term in x ^ (dOffs+1).
	 * Poly [2], is the term in x ^ (dOffs+2).
	 * ...
	 * Poly [n], is the term in x ^ n.
	 * @param degree The degree of the polynomial.	 
	 */
	public ncPolynom(int dOffs, int degree) {
		super(1, degree+1-dOffs);
		this.dOffset = dOffs;
	}

}
