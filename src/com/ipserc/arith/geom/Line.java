package com.ipserc.arith.geom;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.vectorcomplex.*;

public class Line {
	private VectorComplex direction;
	private Point point;
	
	private final static String HEADINFO = "Line --- INFO: ";
	private final static String VERSION = "1.1 (2026_0801_1530)";
	private final static double PARALLEL_TOLERANCE = 1e-9;
	/* VERSION Release Note
	 *
	 * 1.1 (2026_0801_1530)
	 * distance(Line)/intersection(Line): parallelism was detected by comparing an acos-derived
	 * angle (VectorComplex.angle(), Math.acos(dotprod/normA/normB)) to exactly 0. For genuinely
	 * parallel directions, rounding in the two independent norm computations can push the ratio
	 * fed to acos() 1 ULP above 1.0 (e.g. sqrt(13)*sqrt(52) -> 1.0000000000000002), making acos()
	 * return NaN -- and NaN != 0 is true in IEEE754, silently misrouting the call into the
	 * "not parallel" branch. Both methods now detect parallelism via the norm of the direction
	 * vectors' cross product, scaled by their magnitudes (PARALLEL_TOLERANCE), which never calls
	 * acos() and is immune to this failure mode.
	 * distance(Line) also had an incomplete "parallel" branch: it returned 0 unconditionally
	 * instead of the actual point-to-line distance (distance(Line) is only 0 when the lines
	 * coincide) -- now delegates to the existing, correct distance(Point).
	 * KNOWN LIMITATION, left unresolved by explicit user decision: the "not parallel" branch of
	 * distance(Line) computes the minimum distance via the scalar triple product (mixedprod),
	 * which is only mathematically valid in 3 dimensions. A dim==2 special case is added (two
	 * non-parallel lines in a plane always intersect, distance is trivially 0), but dimension>3
	 * still uses the 3D formula for lack of a general implementation (orthogonal projection onto
	 * the orthogonal complement of both directions) -- may throw a dimension-mismatch exception
	 * from crossprod()/dotprod(), or coincidentally work only where dimensions happen to align
	 * (e.g. dim==7). See project continuity doc (Claude/ComplexArithRev.md) for context.
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
	 * Instantiates an empty line
	 */
	public Line() {
		this.direction = new VectorComplex();
		this.point = new Point();
	}
	
	/**
	 * Instantiates a null line of dimension dim
	 * @param dim The dimension
	 */
	public Line(int dim) {
		this.direction = new VectorComplex(dim);
		this.point = new Point(dim);
	}

	/**
	 * Instantiates a line from a vector equation of director vector "direction" and point "point"
	 * @param direction Vector The director vector
	 * @param point Point The point
	 */
	public Line(VectorComplex direction, Point point) {
		this.direction = direction;
		this.point = point;
		if (direction.dim() != point.dim()) {
			this.direction = new VectorComplex(0);
			this.point = new Point(0);
			System.err.println(HEADINFO + "Direction vector and point must have the same dimension.");
			return;
		}		
	}

	/**
	 * Instantiates a line from a vector equation of director vector "direction" and point "point" in string representation
	 * @param sDirection The string representation of the director vector
	 * @param sPoint The string representation of the point
	 */
	public Line(String sDirection, String sPoint) {
		this.direction = new VectorComplex(sDirection);
		this.point = new Point(sPoint);
		if (direction.dim() != point.dim()) {
			this.direction = new VectorComplex(0);
			this.point = new Point(0);
			System.err.println(HEADINFO + "Direction vector and point must have the same dimension.");
			return;
		}		
	}	
	
	/**
	 * Instantiates a line from two points
	 * @param pointA Point The point A
	 * @param pointB Point The point B
	 */
	public Line(Point pointA, Point pointB) {
		if (pointA.dim() == pointB.dim()) {
			//this.direction.complexMatrix = pointB.minus(pointA).complexMatrix;
			this.direction = pointB.minus(pointA);
			this.point = pointA;
		}
		else {
			this.direction = new VectorComplex(0);
			this.point = new Point(0);
			System.err.println(HEADINFO + "Both points must have the same dimension.");
		}
	}
	
	/**
	 * Instantiates a line from two points in string representation
	 * @param sPointA The point A in string format
	 * @param sPointB The point B in string format
	 * @return The Line object
	 */
	public Line line(String sPointA, String sPointB) {
		Point pointA = new Point(sPointA); 
		Point pointB = new Point(sPointB); 
		Line line = new Line(0);
		if (pointA.dim() == pointB.dim()) {
			//Line line = new Line(pointA.dim());
			this.direction.complexMatrix = pointB.minus(pointA).complexMatrix;
			this.point = pointA;
		}
		else {
			System.err.println(HEADINFO + "Both points must have the same dimension.");
		}
		return line;
	}
	
	/*
	 * ***********************************************
	 * GETTERS
	 * ***********************************************
	 */
	
	/**
	 * Gets the director vector of the line
	 * @return
	 */
	public VectorComplex direction() {
		return this.direction;
	}
	
	/**
	 * Gets the point of the line
	 * @return
	 */
	public Point point() {
		return this.point;
	}
	
	/*
	 * ***********************************************
	 * SETTERS
	 * ***********************************************
	 */
	
	/**
	 * Sets the director vector of the line
	 */
	public void direction(VectorComplex vector) {
		this.direction = vector;
	}
	
	/**
	 * Sets the point of the line
	 */
	public void point(Point point) {
		this.point = point;
	}

	/*
	 * ***********************************************
	 * PRINTING
	 * ***********************************************
	 */

	/**
	 * Prints the line from the components of its vectorial equation
	 * @param caption
	 */
	public void print(String caption) {
		System.out.println(caption);
		this.direction.println(	"  direction:");
		this.point.print(		"  point    :");
	}
	
	/**
	 * Prints the line from the components of its vectorial equation whit a caption
	 * @param caption The caption
	 */
	public void println(String caption) {
		this.print(caption);
		System.out.println();
	}

	/*
	 * ***********************************************
	 * CALCULATIONS
	 * ***********************************************
	 */
	
	/**
	 * Calculates a point of the line from its vectorial equation given a parameter lambda
	 * @param lambda The parameter
	 * @return the point of the line
	 */
	public Point point(Complex lambda) {
		Point linePoint = new Point(this.point.dim());
		linePoint.complexMatrix = (this.point.plus(this.direction.prod(lambda))).complexMatrix.clone();
		return linePoint;
	}

	/**
	 * Calculates a point of the line from its vectorial equation given a parameter lambda
	 * @param lambda The parameter
	 * @return the point of the line
	 */
	public Point point(double lambda) {
		Complex clambda = new Complex(lambda);
		return point(clambda);
	}

	/**
	 * Calculate the distance of a line to a given point
	 * PaPp = PaPq + PqPp --> PaPp x V = PaPq x V + PqPp x V, as PaPq and V are parallel --> PaPq x V = 0 -->
	 * PaPp x V = PqPp X V, as PqPp and V are perpendicular --> |PpPq x V| = |PpPq|*|V| -->
	 * d(r,Pp) = d(Pq,Pp) = |PpPq| = |PaPp x V|/|V|
	 * @param point The given point
	 * @return The distance
	 */
	public double distance(Point point) {
		VectorComplex PaPp = this.point.minus(point);
		return PaPp.crossprod(this.direction).norm()/this.direction.norm();
	}

	/**
	 * Calculate the distance of the line to a given point by the distance from the normal point of the line to the given point
	 * @param point The given point
	 * @return The distance
	 */
	public double distance2(Point point) {
		return this.normalPoint(point).distance(point);
	}

	/*
	public double distance2(Point point) {
		Complex t = new Complex(0);
		Complex num = new Complex(0);
		Complex den = new Complex(0);
		
		for (int i = 0; i < this.point.dim(); ++i) {
			Complex C = this.point.complexMatrix[0][i].minus(point.complexMatrix[0][i]);
			C = C.times(this.direction.complexMatrix[0][i]);
			num = num.plus(C);
			den = den.plus(this.direction.complexMatrix[0][i].power(2));
		}
		t = num.divides(den).opposite();
		Point Pr = this.point(t);
		return Pr.distance(point);
	}
	*/
	
	/**
	 * Returns the point of the line through which the perpendicular line that joins the given point passes
	 * @param point The given point
	 * @return The point of the line
	 */
	public Point normalPoint(Point point) {
		Complex t = new Complex(0);
		Complex num = new Complex(0);
		Complex den = new Complex(0);
		
		for (int i = 0; i < this.point.dim(); ++i) {
			Complex C = this.point.complexMatrix[0][i].minus(point.complexMatrix[0][i]);
			C = C.times(this.direction.complexMatrix[0][i]);
			num = num.plus(C);
			den = den.plus(this.direction.complexMatrix[0][i].power(2));
		}
		t = num.divides(den).opposite();
		return this.point(t);
	}
	
	/**
	 * Returns the perpendicular to the line joining the given point
	 * @param point The given point
	 * @return The perpendicular line
	 */
	public Line perpendicular(Point point) {
		Line normalLine = new Line();
		normalLine.point = this.normalPoint(point);
		normalLine.direction = point.minus(normalLine.point);
		return normalLine;
	}
	
	/**
	 * Calculates the angle between two lines
	 * @param line The given line
	 * @return The angle in radians
	 */
	public double angle(Line line) {
		double sin = (this.direction.crossprod(line.direction)).norm()/this.direction.norm()/line.direction.norm();
		double cos = (this.direction.dotprod(line.direction)).abs()/this.direction.norm()/line.direction.norm();
		if (cos == 0) return Math.PI/2;
		//else return Math.acos(cos);
		else return Math.atan(sin/cos);
	}
	
	/**
	 * Calculates the minimum distance between two lines
	 * @param line The given line
	 * @return The minimum distance 
	 */
	public double distance(Line line) {
		double crossNorm = this.direction.crossprod(line.direction).norm();
		boolean parallel = crossNorm <= PARALLEL_TOLERANCE * this.direction.norm() * line.direction.norm();
		if (parallel) {
			return this.distance(line.point());
		}
		if (this.direction.dim() == 2 && line.direction.dim() == 2) {
			return 0.0;
		}
		double num = this.point.minus(line.point).mixedprod(this.direction, line.direction).abs();
		return num/crossNorm;
	}

	/**
	 * Calculates the intersection between two lines
	 * @param line The given line
	 * @return The point of intersection
	 */
	public Point intersection(Line line) {
		Point intersection = new Point(this.direction.dim()-1);
		double crossNorm = this.direction.crossprod(line.direction).norm();
		boolean parallel = crossNorm <= PARALLEL_TOLERANCE * this.direction.norm() * line.direction.norm();
		if (!parallel) {
			Complex t1 = (this.point.complexMatrix[0][1].minus(line.point.complexMatrix[0][1]).times(line.direction.complexMatrix[0][0]));
			t1 = t1.minus(this.point.complexMatrix[0][0].minus(line.point.complexMatrix[0][0]).times(line.direction.complexMatrix[0][1]));
			t1 = t1.divides((this.direction.complexMatrix[0][0].times(line.direction.complexMatrix[0][1])).
					  minus((this.direction.complexMatrix[0][1].times(line.direction.complexMatrix[0][0]))));
			for (int i = 0; i < this.point.dim(); ++i) {
				intersection.complexMatrix[0][i] = this.point.complexMatrix[0][i].plus(this.direction.complexMatrix[0][i].times(t1));
			}			
		}
		return intersection;
	}
}
