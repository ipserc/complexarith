package com.ipserc.arith.geom;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vectorcomplex.*;

public class Point extends MatrixComplex {

	private final static String HEADINFO = "Point --- INFO: ";
	private final static String VERSION = "1.1 (2026_0807_1200)";
	/* VERSION Release Note
	 *
	 * 1.1 (2026_0807_1200)
	 * Point(int dim): fixed an off-by-one, super(1, dim+1) allocated one column too many, so a
	 * point of dimension dim ended up with dim()==dim+1 -- inconsistent with Point(String), which
	 * does not pad (a 3-coordinate string gives dim()==3). Confirmed to corrupt
	 * Plane(VectorComplex,VectorComplex,Point)'s internal point (an extra trailing zero
	 * coordinate), breaking generalEq() with a dimension-mismatch exception; Line.intersection(Line)
	 * had already worked around it locally by passing dim()-1. Now also guards dim<1, matching the
	 * existing VectorComplex(int) guard (a Point of dimension 0 doesn't mean anything).
	 *
	 * 1.0 (2021_0206_0100)
	 */


	/*
	 * ***********************************************
	 * 	VERSION 
	 * ***********************************************
	 */
	
	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION); 
	}

	/*
	 * ***********************************************
	 * 	CONSTRUCTORS 
	 * ***********************************************
	 */

	/**
	 * Instantiates an empty Point
	 */
	public Point() {
		super();
	}

	/**
	 * Intantiates a null Point of dimension dim
	 * @param dim The dimension of the space of the point
	 */
	public Point(int dim) {
		super(1, dim);
		if (dim < 1) {
			throw new IllegalArgumentException(HEADINFO + "Not valid dimension: " + dim);
		}
	}

	/**
	 * Intantiates a Point with ist coordinates as String
	 * @param coords The point coordinates as String comma separated
	 */
	public Point(String coords) {
		super(coords);
		int rowLen = this.complexMatrix.length;
		if (rowLen > 1) {
			System.err.println(HEADINFO + "Not valid point: point set to null point.");
			this.complexMatrix = null;
		}
	}

	
	/*
	 * ***********************************************
	 * 	METHODS
	 * ***********************************************
	 */

	/**
	 * Returns the distance between two points using the Euclidean norm
	 * @param point The other point as Point
	 * @return The distance
	 */
	public double distance(Point point) {
		return point.minus(this).norm();
	}
	
	/**
	 * Returns the distance between two points using the norm of order pnorm
	 * @param point The other point as Point
	 * @param pnorm The order of the norm
	 * @return
	 */
	public double distance(Point point, int pnorm) {
		return point.minus(this).p_norm(pnorm);
	}
	
	/*
	 * ***********************************************
	 * 	OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Adds two points by returning the vector that points the sum point
	 * @param point The other point
	 * @return The vector that points the sum point
	 */
	public VectorComplex plus(Point point) {
		VectorComplex vector = new VectorComplex(this.dim());
		for (int i = 0; i < this.dim(); ++i) {
			vector.complexMatrix[0][i] = this.complexMatrix[0][i].plus(point.complexMatrix[0][i]);
		}
		return vector;
	}

	/**
	 * Differentiates two points by returning the vector that points the difference point
	 * @param point The other point
	 * @return The vector that points the difference point
	 */
	public VectorComplex minus(Point point) {
		VectorComplex vector = new VectorComplex(this.dim());
		for (int i = 0; i < this.dim(); ++i) {
			vector.complexMatrix[0][i] = this.complexMatrix[0][i].minus(point.complexMatrix[0][i]);
		}
		return vector;
	}
}
