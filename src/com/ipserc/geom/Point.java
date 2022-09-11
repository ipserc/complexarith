package com.ipserc.geom;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vector.*;

public class Point extends MatrixComplex {
	private final static String HEADINFO = "Point --- INFO:";
	private final static String VERSION = "1.0 (2021_0228_0045)";
	/* VERSION Release Note
	 * 
	 * 1.0 (2021_0228_0045)
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
	 * CONSTRUCTORS 
	 * ***********************************************
	 */

	/**
	 * 
	 */
	public Point() {
		super();
	}

	/**
	 * 
	 * @param degree
	 */
	public Point(int degree) {
		super(1, degree+1);
	}

	/**
	 * 
	 * @param cadena
	 */
	public Point(String cadena) {
		super(cadena);
		int rowLen = this.complexMatrix.length;
		if (rowLen > 1) {
			System.err.println(HEADINFO + "Not valid point: point set to null point.");
			this.complexMatrix = null;
		}
	}

	/**
	 * 
	 * @param point
	 * @return
	 */
	public double distance(Point point) {
		return point.minus(this).norm();
	}
	
	/**
	 * 
	 * @param point
	 * @param pnorm
	 * @return
	 */
	public double distance(Point point, int pnorm) {
		return point.minus(this).p_norm(pnorm);
	}
	
	/**
	 * 
	 * @param point
	 * @return
	 */
	public Vector plus(Point point) {
		Vector vector = new Vector(this.dim());
		for (int i = 0; i < this.dim(); ++i) {
			vector.complexMatrix[0][i] = this.complexMatrix[0][i].plus(point.complexMatrix[0][i]);
		}
		return vector;
	}

	/**
	 * 
	 * @param point
	 * @return
	 */
	public Vector minus(Point point) {
		Vector vector = new Vector(this.dim());
		for (int i = 0; i < this.dim(); ++i) {
			vector.complexMatrix[0][i] = this.complexMatrix[0][i].minus(point.complexMatrix[0][i]);
		}
		return vector;
	}
}
