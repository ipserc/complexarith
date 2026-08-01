/**
 * 
 */
package com.ipserc.arith.geom;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vectorcomplex.*;

/**
 * @author ipserc
 *
 */
public class Plane {
	private VectorComplex normal;
	private Point point;

	private final static String HEADINFO = "Plane --- INFO: ";
	private final static String VERSION = "1.2 (2026_0801_1530)";
	/* VERSION Release Note
	 *
	 * 1.2 (2026_0801_1530)
	 * intersection(Plane): the parallelism guard was inverted -- Math.cos(angle)!=0.0 detects
	 * perpendicular normals, not parallel ones. Fixed to Math.abs(Math.cos(angle))!=1.0, the same
	 * pattern already used correctly in distance(Line)/distance(Plane). Before the fix, two
	 * parallel-and-distinct planes reached solveGauss() with a genuinely inconsistent system,
	 * which used to return a silent NaN marker and now throws IllegalArgumentException (session
	 * of 2026-07-31, MatrixComplex audit) -- exposed a real, unrelated preexisting bug instead of
	 * masking it. Also removes an ungated debug println left in the same method.
	 * The "planes are parallel" branch had the same class of dead-sentinel bug already fixed in
	 * distance(Point) above: it built a placeholder new Line(0) to signal "no intersection",
	 * which now throws (Not valid dimension: 0) since the VectorComplex(int) audit fix of the
	 * previous session -- confusing, unrelated to the real cause. Now throws
	 * IllegalArgumentException directly with a clear message, same pattern as distance(Point).
	 *
	 * 1.1 (2026_0801_1023)
	 * distance(Point): the dimension-mismatch guard used to reset internal state and call
	 * System.exit(0) -- already dead code in practice (an incidental side effect of the
	 * VectorComplex(int) audit fix earlier this session: new VectorComplex(0) now throws before
	 * System.exit(0) is ever reached), but with a misleading message unrelated to the actual
	 * plane/point mismatch. Now throws IllegalArgumentException directly with a clear message.
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
		this.normal = new VectorComplex();
		this.point = new Point();
	}

	/**
	 * Instantiates an empty plane in a space of dimension dim
	 * @param dim The dimension of the space
	 */
	public Plane(int dim) {
		this.normal = new VectorComplex(dim);
		this.point = new Point(dim);
	}

	/**
	 * Instantiates plane by giving its normal vector and a point of the plane
	 * @param normal The normal vector of the plane as Vector
	 * @param point A point of the plane as Point
	 */
	public Plane(VectorComplex normal, Point point) {
		this.normal = normal;
		this.point = point;
		if (this.normal.dim() != point.dim()) {
			this.normal = new VectorComplex(0);
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
		this.normal = new VectorComplex(sNormal);
		this.point = new Point(sPoint);		
		if (normal.dim() != point.dim()) {
			this.normal = new VectorComplex(0);
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
	public Plane(VectorComplex v1, VectorComplex v2, Point point) {
		if ((v1.dim() != v2.dim()) || (v1.dim() != point.dim()) || (v2.dim() != point.dim())) {
			this.normal = new VectorComplex(0);
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
		VectorComplex v1 = new VectorComplex(sV1);
		VectorComplex v2 = new VectorComplex(sV2);
		Point point = new Point(sPoint);
		if ((v1.dim() != v2.dim()) || (v1.dim() != point.dim()) || (v2.dim() != point.dim())) {
			this.normal = new VectorComplex(0);
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
	public VectorComplex normal() {
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
	public VectorComplex projection(VectorComplex vector) {
		VectorComplex projNormal = vector.projection(this.normal);
		return projNormal.minus(vector);
	}
	
	/**
	 * Returns the projection of a vector over the plane
	 * @param sVector The vector to project as String
	 * @return The projection vector
	 */
	public VectorComplex projectionVector(String sVector) {
		VectorComplex vector = new VectorComplex(sVector);
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
			throw new IllegalArgumentException(HEADINFO + "distance: Plane and point must have the same dimension: " + this.normal.dim() + " != " + point.dim() + ".");
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
	public double angle(VectorComplex vector) {
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
	private VectorComplex toVector(Point point) {
		VectorComplex vector = new VectorComplex();
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
		if (Math.abs(Math.cos(angle)) != 1.0) {
			VectorComplex direction = new VectorComplex();
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
			MatrixComplex solutions = aMatrix.solve();
			
			Point point = new Point();
			point.complexMatrix = solutions.transpose().complexMatrix.clone();
			Line line = new Line(direction, point);			
			return line;
		}
		else {
			throw new IllegalArgumentException(HEADINFO + "intersection: Planes are parallel, no intersection line exists.");
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
		VectorComplex pointVect = new VectorComplex(this.point.dim());
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
		// **** if (this.normal.dotprod(line.direction()).equalsred(0,0)) return new Point(0);
		if (this.normal.dotprod(line.direction()).equals(0,0)) return new Point(0);
		VectorComplex vectorP1 = new VectorComplex();
		VectorComplex vectorP2 = new VectorComplex();
		vectorP1.complexMatrix = this.point.complexMatrix;
		vectorP2.complexMatrix = line.point().complexMatrix;

		Complex t = this.normal.dotprod(vectorP1).minus(this.normal.dotprod(vectorP2)).divides(this.normal.dotprod(line.direction()));
		return line.point(t);
	}
}
