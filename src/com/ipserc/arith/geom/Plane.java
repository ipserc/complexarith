/**
 * 
 */
package com.ipserc.arith.geom;

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
	private boolean Reduced = true;

	private final static String HEADINFO = "Plane --- INFO: ";
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
	 * Instantiates an empty plane
	 */
	public Plane() {
		this.normal = new Vector();
		this.point = new Point();
	}

	/**
	 * Instantiates an empty plane in a space of dimension dim
	 * @param dim The dimension of the space
	 */
	public Plane(int dim) {
		this.normal = new Vector(dim);
		this.point = new Point(dim);
	}

	/**
	 * Instantiates plane by giving its normal vector and a point of the plane
	 * @param normal The normal vector of the plane as Vector
	 * @param point A point of the plane as Point
	 */
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
	
	/**
	 * Instantiates plane by giving its normal vector and a point of the plane
	 * @param sNormal The normal vector of the plane as String vector
	 * @param sPoint A point of the plane as a String point
	 */
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
	
	/**
	 * Instantiates plane by giving two coplanar vectors and a point of the plane
	 * @param v1 The first coplanar vector as Vector
	 * @param v2 The second coplanar vector as Vector
	 * @param point A point of the plane as Point
	 */
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

	/**
	 * Instantiates plane by giving two coplanar vectors and a point of the plane in string format
	 * @param sV1 The first coplanar vector as String
	 * @param sV2 The second coplanar vector as String
	 * @param sPoint A point of the plane as String
	 */
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
	
	/*
	 * ***********************************************
	 * 	GETTERS 
	 * ***********************************************
	 */
	/**
	 * returns the normal vector of the plane
	 * @return The normal vector as Vector
	 */
	public Vector normal() {
		return this.normal;
	}

	/**
	 * returns the point of the plane given in its definition
	 * @return The given point as Point
	 */
	public Point point() {
		return this.point;
	}

	/*
	 * ***********************************************
	 * 	PRINTING 
	 * ***********************************************
	 */

	/**
	 * Prints the plane from the components of its vectorial equation
	 * @param caption The caption text before the plane components
	 */
	public void print(String caption) {
		System.out.println(caption);
		this.normal.println("  normal:");
		this.point.print("  point:");
	}

	/**
	 * Prints the plane from the components of its vectorial equation
	 * @param caption The caption text before the plane components
	 */
	public void println(String caption) {
		this.print(caption);
		System.out.println();
	}
	
	/*
	 * ***********************************************
	 * 	METHODS 
	 * ***********************************************
	 */
	
	/**
	 * Returns the projection of a vector over the plane
	 * @param vector The vector to project as Vector
	 * @return The projection vector
	 */
	public Vector projection(Vector vector) {
		Vector projNormal = vector.projection(this.normal);
		return projNormal.minus(vector);
	}
	
	/**
	 * Returns the projection of a vector over the plane
	 * @param sVector The vector to project as String
	 * @return The projection vector
	 */
	public Vector projectionVector(String sVector) {
		Vector vector = new Vector(sVector);
		return this.projection(vector);
	}
	
	/**
	 * Returns the projection of a line over the plane
	 * @param line The line to project as Line
	 * @return The projection line
	 */
	public Line projection(Line line) {
		Line projLine = new Line();
		projLine.direction(this.projection(line.direction()));
		projLine.point(this.point);
		return projLine;
	}

	/**
	 * Returns the projection of a line over the plane
	 * @param sDirection The line direction vector to project as String
	 * @param sPoint A point of the line as String
	 * @return The projection line
	 */
	public Line projectionLine(String sDirection, String sPoint) {
		Line line = new Line(sDirection, sPoint);
		return this.projection(line);
	}

	/**
	 * Given a point it calculates the projection of the point as the intersection point of the line which passes by the point and has the normal vector of the plane as director vector. 
	 * @param point The given point as Point
	 * @return the projection point
	 */
	public Point projection(Point point) {
		Point projection = new Point(point.dim());
		Complex t = (this.normal.dotprod(this.point.minus(point))).divides(this.normal.dotprod(this.normal));
		projection.complexMatrix = point.plus(this.normal.prod(t)).complexMatrix;
		return projection;
	}
	
	/**
	 * Given a point it calculates the projection of the point as the intersection point of the line which passes by the point and has the normal vector of the plane as director vector. 
	 * @param sPoint The given point as String
	 * @return the projection point
	 */
	public Point projectionPoint(String sPoint) {
		Point point = new Point(sPoint);
		return projection(point);
	}
	
	/**
	 * Returns the distance between the plane and a point
	 * @param point The given point as Point
	 * @return The distance
	 */
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

	/**
	 * Returns the distance between the plane and a point
	 * @param sPoint The given point as String
	 * @return The distance
	 */
	public double distancePoint(String sPoint) {
		Point point = new Point(sPoint);
		return this.distance(point);
	}
	
	/**
	 * Returns the angle between the plane and another plane
	 * @param plane The given point as Plane
	 * @return The angle in radians
	 */
	public double angle(Plane plane) {
		return this.normal.angle(plane.normal());
	}

	/**
	 * Returns the angle between the plane and a line
	 * @param line The given line as Line
	 * @return The angle in radians
	 */
	public double angle(Line line) {
		return Math.PI/2 - this.normal.angle(line.direction());
	}
	
	/**
	 * Returns the angle between the plane and a vector
	 * @param vector The given vector as Vector
	 * @return The angle in radians
	 */
	public double angle(Vector vector) {
		return Math.PI/2 - this.normal.angle(vector);
	}

	/**
	 * Returns the distance between the plane and a line
	 * @param line The given line as Line
	 * @return The distance
	 */
	public double distance(Line line) {
		double angle = this.angle(line);
		if (Math.abs(Math.cos(angle)) == 1.0) {
			return this.distance(line.point()); 
		}
		else return 0.0;
	}

	/**
	 * Returns the distance between the plane and a line given as direction vector and a point
	 * @param sDirection The direction vector of the line as String
	 * @param sPoint A point of the line as String
	 * @return
	 */
	public double distanceLine(String sDirection, String sPoint) {
		Line line = new Line(sDirection, sPoint);
		return this.distance(line);
	}

	/**
	 * Returns the distance between the plane and another plane
	 * @param plane The other plines as Plane
	 * @return The distance
	 */
	public double distance(Plane plane) {
		double angle = this.angle(plane);
		if (Math.abs((Math.cos(angle))) == 1.0) {
			return this.distance(plane.point()); 
		}
		else return 0.0;
	}

	/**
	 * Returns the distance between the plane and another plane defined by its normal vector and a point
	 * @param sNormal The plane normal vector as String
	 * @param sPoint A plane's point as String
	 * @return the distance
	 */
	public double distancePlane(String sNormal, String sPoint) {
		Plane plane = new Plane(sNormal, sPoint);
		return this.distance(plane);
	}

	/**
	 * Private Method. Returns the vector pointed from the origin to the point
	 * @param point The point as Point
	 * @return The vector
	 */
	private Vector toVector(Point point) {
		Vector vector = new Vector();
		vector.complexMatrix = point.complexMatrix.clone();
		return vector;
	}
	
	/**
	 * Returns the intersection between the two planes as a Line
	 * @param plane The other plane
	 * @return The intersection line
	 */
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
			MatrixComplex solutions = aMatrix.solve(Reduced);
			
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
	
	/**
	 * Returns the general equation of the plane
	 * @return The general equation of the plane
	 */
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
	
	/**
	 * Returns the intersection between the plane and a line
	 * @param line The given line as Line
	 * @return The intersection point as Point
	 */
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
