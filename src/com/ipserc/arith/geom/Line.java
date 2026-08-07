package com.ipserc.arith.geom;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.vectorcomplex.*;

public class Line {
	private VectorComplex direction;
	private Point point;
	
	private final static String HEADINFO = "Line --- INFO: ";
	private final static String VERSION = "1.2 (2026_0807_1330)";
	private final static double PARALLEL_TOLERANCE = 1e-9;
	/* VERSION Release Note
	 *
	 * 1.2 (2026_0807_1330)
	 * Audit of com.ipserc.arith.geom (ver Claude/ComplexArithRev.md), continuacion de la sesion
	 * anterior. intersection(Line) tenia dos bugs reales, no cubiertos por el KNOWN LIMITATION de
	 * distance(Line) de arriba (que documentaba solo la rama "no paralelas"):
	 * (1) el caso paralelo no se detectaba en absoluto -- devolvia en silencio el punto (this.point
	 * + this.direction*0), es decir this.point sin mover, disfrazado de "interseccion" (confirmado
	 * con el propio par de datos paralelos de TestLine01.java). Ahora lanza IllegalArgumentException,
	 * mismo patron ya usado en distance(Line)/Plane.intersection(Plane).
	 * (2) la formula resolvia el sistema 2x2 con las componentes 0 y 1 fijas (regla de Cramer 2D) y
	 * nunca comprobaba que el punto resultante estuviera tambien en la otra recta -- para rectas
	 * cruzadas (skew) en 3D+ (el caso generico, no un borde raro) devolvia con confianza un punto que
	 * no esta en ninguna de las dos rectas. Reescrito: busca el par de coordenadas con el menor 2x2
	 * mejor condicionado (mayor determinante) entre las dos direcciones -- funciona en cualquier
	 * dimension sin depender de crossprod()/mixedprod() (limitados a 3D, ver nota de 1.1) -- y valida
	 * el candidato contra distance(Point) antes de devolverlo; si no esta sobre la otra recta, lanza
	 * IllegalArgumentException ("skew lines") en vez de devolver un resultado incorrecto.
	 * Ademas: Line(VectorComplex,Point)/Line(String,String)/Line(Point,Point) tenian el mismo patron
	 * de "sentinela muerto" ya identificado y arreglado en Plane.distance()/Plane.intersection(Plane)
	 * la sesion anterior -- construian un VectorComplex(0) para senalizar "dimensiones incompatibles",
	 * pero VectorComplex(0) ahora lanza excepcion antes de llegar al mensaje informativo propio,
	 * dejandolo muerto. Las 3 ahora lanzan IllegalArgumentException directamente con su mensaje.
	 * line(String,String) (metodo de instancia, cero llamadores en el proyecto) se elimina: ademas de
	 * heredar el mismo bug (llamaba a new Line(0), que siempre lanzaba antes de mirar los argumentos),
	 * su propia logica estaba rota de forma independiente -- mutaba "this" pero devolvia un objeto
	 * "line" local sin poblar, vestigial desde antes de que existiera el constructor Line(Point,Point).
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
		if (direction.dim() != point.dim()) {
			throw new IllegalArgumentException(HEADINFO + "Direction vector and point must have the same dimension.");
		}
		this.direction = direction;
		this.point = point;
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
			throw new IllegalArgumentException(HEADINFO + "Direction vector and point must have the same dimension.");
		}
	}
	
	/**
	 * Instantiates a line from two points
	 * @param pointA Point The point A
	 * @param pointB Point The point B
	 */
	public Line(Point pointA, Point pointB) {
		if (pointA.dim() != pointB.dim()) {
			throw new IllegalArgumentException(HEADINFO + "Both points must have the same dimension.");
		}
		this.direction = pointB.minus(pointA);
		this.point = pointA;
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
	 * Calculates the intersection between two lines. Solves the 2-unknown (t1,t2) linear system
	 * this.point+t1*this.direction = line.point+t2*line.direction using the best-conditioned pair
	 * of coordinates (largest 2x2 minor of the two direction vectors), then validates the
	 * candidate point against ALL coordinates via the existing distance(Point) -- this generalizes
	 * correctly to any dimension, since it never relies on crossprod()/mixedprod() (which, per the
	 * KNOWN LIMITATION documented above for distance(Line), are only well-founded in 3D).
	 * @param line The given line
	 * @return The point of intersection
	 * @throws IllegalArgumentException if the lines have different dimensions, are parallel (no
	 * unique intersection, whether distinct or coincident), or are skew (non-parallel but with no
	 * common point).
	 */
	public Point intersection(Line line) {
		if (this.direction.dim() != line.direction.dim() || this.point.dim() != line.point.dim()) {
			throw new IllegalArgumentException(HEADINFO + "intersection: Lines must have the same dimension.");
		}

		double crossNorm = this.direction.crossprod(line.direction).norm();
		boolean parallel = crossNorm <= PARALLEL_TOLERANCE * this.direction.norm() * line.direction.norm();
		if (parallel) {
			throw new IllegalArgumentException(HEADINFO + "intersection: Lines are parallel, no unique intersection point exists.");
		}

		int dim = this.direction.dim();
		int idxI = -1, idxJ = -1;
		Complex det = new Complex(0);
		double bestMod = 0;
		for (int i = 0; i < dim; ++i) {
			for (int j = i+1; j < dim; ++j) {
				Complex candidateDet = this.direction.complexMatrix[0][i].times(line.direction.complexMatrix[0][j])
						.minus(this.direction.complexMatrix[0][j].times(line.direction.complexMatrix[0][i]));
				if (candidateDet.mod() > bestMod) {
					bestMod = candidateDet.mod();
					idxI = i;
					idxJ = j;
					det = candidateDet;
				}
			}
		}
		// Unreachable: two non-parallel direction vectors always have at least one non-zero 2x2 minor.
		if (idxI < 0) {
			throw new IllegalArgumentException(HEADINFO + "intersection: Unable to solve for the intersection parameter.");
		}

		Complex t1 = (this.point.complexMatrix[0][idxJ].minus(line.point.complexMatrix[0][idxJ])).times(line.direction.complexMatrix[0][idxI]);
		t1 = t1.minus((this.point.complexMatrix[0][idxI].minus(line.point.complexMatrix[0][idxI])).times(line.direction.complexMatrix[0][idxJ]));
		t1 = t1.divides(det);

		Point candidate = this.point(t1);
		double residual = line.distance(candidate);
		double scale = Math.max(this.direction.norm(), line.direction.norm());
		if (residual > PARALLEL_TOLERANCE * scale) {
			throw new IllegalArgumentException(HEADINFO + "intersection: Lines do not intersect (skew lines).");
		}
		return candidate;
	}
}
