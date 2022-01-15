package com.ipserc.arith.geom;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vector.*;

public class Point extends MatrixComplex {

	private final static String HEADINFO = "Point --- INFO: ";
	private final static String VERSION = "1.0 (2021_0206_0100)";
	/* VERSION Release Note
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
		super(1, dim+1);
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
	public Vector plus(Point point) {
		Vector vector = new Vector(this.dim());
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
	public Vector minus(Point point) {
		Vector vector = new Vector(this.dim());
		for (int i = 0; i < this.dim(); ++i) {
			vector.complexMatrix[0][i] = this.complexMatrix[0][i].minus(point.complexMatrix[0][i]);
		}
		return vector;
	}
}
