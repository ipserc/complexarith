package com.ipserc.geom;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vector.*;

public class Point extends MatrixComplex {

	private final static String HEADINFO = "Point --- INFO:";

	public Point() {
		super();
	}

	public Point(int degree) {
		super(1, degree+1);
	}

	public Point(String cadena) {
		super(cadena);
		int rowLen = this.complexMatrix.length;
		if (rowLen > 1) {
			System.err.println(HEADINFO + "Not valid point: point set to null point.");
			this.complexMatrix = null;
		}
	}

	public double distance(Point point) {
		return point.minus(this).norm();
	}
	
	public double distance(Point point, int pnorm) {
		return point.minus(this).p_norm(pnorm);
	}
	
	public Vector plus(Point point) {
		Vector vector = new Vector(this.dim());
		for (int i = 0; i < this.dim(); ++i) {
			vector.complexMatrix[0][i] = this.complexMatrix[0][i].plus(point.complexMatrix[0][i]);
		}
		return vector;
	}

	public Vector minus(Point point) {
		Vector vector = new Vector(this.dim());
		for (int i = 0; i < this.dim(); ++i) {
			vector.complexMatrix[0][i] = this.complexMatrix[0][i].minus(point.complexMatrix[0][i]);
		}
		return vector;
	}
}
