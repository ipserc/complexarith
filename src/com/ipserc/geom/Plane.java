/**
 * 
 */
package com.ipserc.geom;

import com.ipserc.arith.vector.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.*;

/**
 * @author ipserc
 *
 */
public class Plane {
	private Vector normal;
	private Point point;

	private final static String HEADINFO = "Plane --- INFO: ";
	private final static String VERSION = "1.0 (2021_0206_0100)";

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

	public Plane() {
		this.normal = new Vector();
		this.point = new Point();
	}

	public Plane(int dim) {
		this.normal = new Vector(dim);
		this.point = new Point(dim);
	}

	public Plane(Vector normal, Point point) {
		this.normal = normal;
		this.point = point;
		if (this.normal.dim() != point.dim()) {
			this.normal = new Vector(0);
			this.point = new Point(0);
			System.err.println(HEADINFO + "Normal vector and point must have the same dimension.");
			return;
		}		
	}
	
	public Plane(String sNormal, String sPoint) {
		this.normal = new Vector(sNormal);
		this.point = new Point(sPoint);		
		if (normal.dim() != point.dim()) {
			this.normal = new Vector(0);
			this.point = new Point(0);
			System.err.println(HEADINFO + "Normal vector and point must have the same dimension.");
			return;
		}		
	}
	
	public Plane(Vector v1, Vector v2, Point point) {
		if ((v1.dim() != v2.dim()) || (v1.dim() != point.dim()) || (v2.dim() != point.dim())) {
			this.normal = new Vector(0);
			this.point = new Point(0);
			System.err.println(HEADINFO + "Both vectors and point must have the same dimension.");
			return;
		}		
		this.normal = v1.crossprod(v2);
		this.point = new Point(this.normal.dim());
		this.point.initMatrix(0, 0);
		for (int i = 0; i < point.dim(); ++i) {
			this.point.complexMatrix[0][i] = point.complexMatrix[0][i].copy();
		}
	}

	public Plane(String sV1, String sV2, String sPoint) {
		Vector v1 = new Vector(sV1);
		Vector v2 = new Vector(sV2);
		Point point = new Point(sPoint);
		if ((v1.dim() != v2.dim()) || (v1.dim() != point.dim()) || (v2.dim() != point.dim())) {
			this.normal = new Vector(0);
			this.point = new Point(0);
			System.err.println(HEADINFO + "Both vectors and point must have the same dimension.");
			return;
		}		
		this.normal = v1.crossprod(v2);
		this.point = new Point(this.normal.dim());
		this.point.initMatrix(0, 0);
		for (int i = 0; i < point.dim(); ++i) {
			this.point.complexMatrix[0][i] = point.complexMatrix[0][i].copy();
		}
	}
		
	public Vector normal() {
		return this.normal;
	}

	public Point point() {
		return this.point;
	}

	/**
	 * Prints the plane from the components of its vectorial equation
	 * @param caption
	 */
	public void print(String caption) {
		System.out.println(caption);
		this.normal.println("  normal:");
		this.point.print("  point:");
	}

	/**
	 * Prints the plane from the components of its vectorial equation
	 * @param caption
	 */
	public void println(String caption) {
		this.print(caption);
		System.out.println();
	}
	
	public Vector projection(Vector vector) {
		Vector projNormal = vector.projection(this.normal);
		return projNormal.minus(vector);
	}
	
	public Vector projectionVector(String sVector) {
		Vector vector = new Vector(sVector);
		return this.projection(vector);
	}
	
	public Line projection(Line line) {
		Line projLine = new Line();
		projLine.direction(this.projection(line.direction()));
		projLine.point(this.point);
		return projLine;
	}

	public Line projectionLine(String sDirection, String sPoint) {
		Line line = new Line(sDirection, sPoint);
		return this.projection(line);
	}

	/**
	 * Given a point it calculates the projection of point as the intersection point of the line which passes for point and has the normal vector o the plan as director vector. 
	 * @param point The given point
	 * @return the in
	 */
	public Point projection(Point point) {
		Point projection = new Point(point.dim());
		Complex t = (this.normal.dotprod(this.point.minus(point))).divides(this.normal.dotprod(this.normal));
		projection.complexMatrix = point.plus(this.normal.prod(t)).complexMatrix;
		return projection;
	}
	
	public Point projectionPoint(String sPoint) {
		Point point = new Point(sPoint);
		return projection(point);
	}
	
	public double distance(Point point) {
		if (this.normal.dim() != point.dim()) {
			this.normal = new Vector(0);
			this.point = new Point(0);
			System.err.println(HEADINFO + "Plane and point must have the same dimension.");
			System.exit(0);
		}		
		Point intersection = this.projection(point);
		return (point.minus(intersection)).norm();
	}

	public double distancePoint(String sPoint) {
		Point point = new Point(sPoint);
		return this.distance(point);
	}
	
	public double angle(Plane plane) {
		return this.normal.angle(plane.normal());
	}

	public double angle(Line line) {
		return Math.PI/2 - this.normal.angle(line.direction());
	}
	
	public double angle(Vector vector) {
		return Math.PI/2 - this.normal.angle(vector);
	}

	public double distance(Line line) {
		double angle = this.angle(line);
		if (Math.abs(Math.cos(angle)) == 1.0) {
			return this.distance(line.point()); 
		}
		else return 0.0;
	}

	public double distanceLine(String sDirection, String sPoint) {
		Line line = new Line(sDirection, sPoint);
		return this.distance(line);
	}

	public double distance(Plane plane) {
		double angle = this.angle(plane);
		if (Math.abs((Math.cos(angle))) == 1.0) {
			return this.distance(plane.point()); 
		}
		else return 0.0;
	}

	public double distancePlane(String sNormal, String sPoint) {
		Plane plane = new Plane(sNormal, sPoint);
		return this.distance(plane);
	}

	private Vector toVector(Point point) {
		Vector vector = new Vector();
		vector.complexMatrix = point.complexMatrix.clone();
		return vector;
	}
	
	public Line intersection(Plane plane) {
		double angle = this.angle(plane);
		if (Math.cos(angle) != 0.0) {
			Vector direction = new Vector();
			direction = this.normal().crossprod(plane.normal());
			MatrixComplex aMatrix = new MatrixComplex(this.normal.dim(),this.normal.dim()+1);
			int i;
			for (i = 0; i < this.normal.dim(); ++i) {
				aMatrix.complexMatrix[0][i] = this.normal.complexMatrix[0][i];
			}
			aMatrix.complexMatrix[0][i] = this.normal.dotprod(this.toVector(point));
			for (i = 0; i < this.normal.dim(); ++i) {
				aMatrix.complexMatrix[1][i] = plane.normal.complexMatrix[0][i];
			}
			aMatrix.complexMatrix[1][i] = plane.normal.dotprod(plane.toVector(plane.point));
				aMatrix.println("Sistema ecuaciones");
			MatrixComplex solutions = aMatrix.solve();
			
			Point point = new Point();
			point.complexMatrix = solutions.transpose().complexMatrix.clone();
			Line line = new Line(direction, point);			
			return line;
		}
		else {
			Line line = new Line(0);
			return line;
		}
	}
	
	public MatrixComplex generalEq() {
		MatrixComplex generalEq = new MatrixComplex (1, this.normal.dim()+1);
		for (int i = 0; i < this.normal.dim(); ++i) {
			generalEq.complexMatrix[0][i] = this.normal.complexMatrix[0][i].copy();
		}
		Vector pointVect = new Vector(this.point.dim());
		pointVect.complexMatrix = this.point.complexMatrix.clone();
		generalEq.complexMatrix[0][this.normal.dim()] = this.normal.dotprod(pointVect).opposite();
		return generalEq;
	}
	
	public Point intersection(Line line) {
		if (this.normal.dotprod(line.direction()).equalsred(0,0)) return new Point(0);
		Vector vectorP1 = new Vector();
		Vector vectorP2 = new Vector();
		vectorP1.complexMatrix = this.point.complexMatrix;
		vectorP2.complexMatrix = line.point().complexMatrix;

		Complex t = this.normal.dotprod(vectorP1).minus(this.normal.dotprod(vectorP2)).divides(this.normal.dotprod(line.direction()));
		return line.point(t);
	}
}
