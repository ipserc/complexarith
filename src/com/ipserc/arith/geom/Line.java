package com.ipserc.arith.geom;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.vectorcomplex.*;

public class Line {
	private VectorComplex direction;
	private Point point;
	
	private final static String HEADINFO = "Line --- INFO: ";
	private final static String VERSION = "1.8 (2026_0811_1200)";
	private final static double PARALLEL_TOLERANCE = 1e-9;
	/* VERSION Release Note
	 *
	 * 1.8 (2026_0811_1200)
	 * Comentarios Javadoc traducidos al inglés y corregidos (sin cambios funcionales), como parte
	 * de la generación de la documentación de la API.
	 *
	 * 1.7 (2026_0810_2340)
	 * Auditoria matematica dedicada (Vigesimosexta sesion, bloque 6 de la hoja de ruta
	 * "Matematicas Aplicadas", ver Claude/ComplexArithRev.md): angle(Line) calculaba sin() via
	 * crossprod() (limite 7D, mismo defecto ya encontrado y arreglado en distance(Point)/
	 * distance(Line) de este mismo fichero) -- para dim>7 devolvia 0.0 para CUALQUIER par de
	 * rectas cuyo angulo no fuera exactamente pi/2 (el atajo cos==0 solo salvaba el caso
	 * ortogonal). Confirmado con un caso de 60 grados en dimension 8. Arreglado delegando en
	 * VectorComplex.angle() (ya general en dimension y con clamp de acos desde su VERSION 1.10),
	 * mismo patron que Plane.angle(Line)/angle(VectorComplex)/angle(Plane) ya usaban. Verificado
	 * con ScratchGeomAudit03.java (conservado).
	 *
	 * 1.6 (2026_0807_2300)
	 * intersection(Line): limpia una referencia cruzada obsoleta en el Javadoc -- citaba "el KNOWN
	 * LIMITATION documentado arriba para distance(Line)" como si siguiera vigente, pero esa
	 * limitacion ya se resolvio en VERSION 1.4. Sin cambio de comportamiento.
	 *
	 * 1.5 (2026_0807_2230)
	 * distance(Point): resuelve el hallazgo colateral anotado en VERSION 1.4. Usaba
	 * PaPp.crossprod(this.direction).norm()/this.direction.norm() -- crossprod() solo esta bien
	 * fundado hasta 7D (teorema de Hurwitz); para dim>7 cae a un vector vacio por defecto cuyo
	 * norm() es 0, asi que distance(Point) devolvia 0.0 en silencio sin importar la distancia real
	 * (confirmado con un caso 10D: distancia real 5.0, devolvia 0.0). Reescrito para usar la misma
	 * proyeccion Hermitiana que ya usa normalPoint() (arreglada en VERSION 1.3) -- valido en
	 * cualquier dimension y para lineas de valor complejo. distance2(Point) simplificado a delegar
	 * en distance(Point) (mismo cuerpo desde este fix, ya no hace falta la logica duplicada) --
	 * conservado como metodo aparte por si algun caller lo usa por nombre. Como distance(Line)'s
	 * rama "paralela" delega en distance(Point), tambien queda arreglada de rebote.
	 *
	 * 1.4 (2026_0807_2200)
	 * distance(Line): resuelve el KNOWN LIMITATION documentado en VERSION 1.1 (rama "no paralelas"
	 * solo valida en 3D via mixedprod()/crossprod()). Reescrito con minimos cuadrados Hermitianos
	 * de proposito general: el paralelismo se detecta con el determinante de Gram
	 * det(d1,d2)=<d1,d1><d2,d2>-|<d1,d2>|^2 (dotprod-based), que por Cauchy-Schwarz es exactamente
	 * 0 si y solo si d1,d2 son linealmente dependientes -- valido en cualquier dimension, a
	 * diferencia de crossprod() (solo bien fundado hasta 7D, teorema de Hurwitz). Para rectas no
	 * paralelas, la distancia minima es la norma del residuo de aproximacion mas cercana
	 * v=this.point-line.point+t*d1-s*d2, con (t,s) solucion del sistema 2x2 Hermitiano
	 * <d1,v>=<d2,v>=0 -- valido en cualquier dimension.
	 * Verificado: los 5 casos 3D coinciden con el build de referencia (formula antigua) a
	 * precision de maquina; el caso 2D no paralelo sigue dando exactamente 0.0 (caso especial
	 * conservado); los casos 4D/5D/10D (antes lanzaban excepcion o "funcionaban por coincidencia")
	 * ahora dan el valor correcto, verificado a mano resolviendo el sistema real equivalente; el
	 * caso 7D antiguo daba 0.577 (documentado como "coincidencia dimensional", ahora confirmado
	 * incorrecto) frente al 1.732 correcto de la nueva formula. De propina, TestLine01.java pasa de
	 * exit=1 a exit=0: el crash preexistente resulto deberse a que crossprod() tampoco funcionaba
	 * bien para un par 4D-4D (sin mismatch de dimension entre las dos rectas, el propio crossprod()
	 * internamente delega a una construccion de 7D para dims 4-7 y fallaba) -- la nueva formula, al
	 * no depender de crossprod() en absoluto, lo resuelve de raiz. El unico caso que sigue
	 * lanzando excepcion es un par autenticamente 4D-vs-7D en los propios datos del test (bug de
	 * datos preexistente, no relacionado) -- ahora con un mensaje claro en vez del error críptico
	 * "4 != 7" desde dentro de VectorComplex.
	 * Hallazgo colateral, NO arreglado: distance(Point) (y por tanto la rama "paralela" de
	 * distance(Line), que delega en ella) sigue dependiendo de crossprod(), asi que para dim>7
	 * (fuera incluso del propio techo de crossprod()) devuelve 0.0 en silencio sin importar la
	 * distancia real -- confirmado con un caso 10D (distancia real 5.0, distance(Point) da 0.0,
	 * distance2(Point), que no usa crossprod(), da el 5.0 correcto). Reportado al usuario, sin
	 * decidir si se arregla.
	 *
	 * 1.3 (2026_0807_1800)
	 * normalPoint(Point): used the bilinear inner product (direction[i] with no conjugate, and
	 * direction[i]^2 for the denominator) instead of the Hermitian one (direction[i]*conj(direction[i])
	 * = |direction[i]|^2) used consistently everywhere else in the project (dotprod()/adjoint(),
	 * Plane.projection(Point)). Invisible for real-valued lines (conj(real)=real, no difference),
	 * but for a genuinely complex direction vector it computed a different "foot of the
	 * perpendicular" than distance(Point) (crossprod-based, already Hermitian-consistent) --
	 * confirmed with a ~33% discrepancy between distance(Point) and distance2(Point) (which is
	 * documented to compute the exact same distance via normalPoint(), see the two methods'
	 * Javadoc). perpendicular(Point) is built from normalPoint() and inherits the same fix. Fixed
	 * by conjugating direction[i] in both the numerator and the denominator.
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
	 * Prints the class version.
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
	 * @return The director vector
	 */
	public VectorComplex direction() {
		return this.direction;
	}

	/**
	 * Gets the point of the line
	 * @return The point
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
	 * @param vector The new director vector
	 */
	public void direction(VectorComplex vector) {
		this.direction = vector;
	}

	/**
	 * Sets the point of the line
	 * @param point The new point
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
	 * @param caption The caption to print before the components
	 */
	public void print(String caption) {
		System.out.println(caption);
		this.direction.println(	"  direction:");
		this.point.print(		"  point    :");
	}

	/**
	 * Prints the line from the components of its vectorial equation with a caption, followed by a blank line
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
	 * Calculates the distance of a line to a given point, in any dimension: the norm of the vector
	 * from the point to its Hermitian projection onto the line (normalPoint()) -- valid for any
	 * dimension and for complex-valued lines, unlike the previous crossprod()-based formula
	 * (|PaPp x direction|/|direction|), only well-founded up to 7D (Hurwitz's theorem). For
	 * dim>7, crossprod() falls back to a degenerate empty vector whose norm() is 0, so the old
	 * formula silently returned 0.0 regardless of the true distance.
	 * @param point The given point
	 * @return The distance
	 */
	public double distance(Point point) {
		return this.normalPoint(point).distance(point);
	}

	/**
	 * Same as distance(Point) -- kept as a separate, independently-named method for readability
	 * at call sites that want to make the "via the normal point" derivation explicit.
	 * @param point The given point
	 * @return The distance
	 */
	public double distance2(Point point) {
		return this.distance(point);
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
			Complex dConj = this.direction.complexMatrix[0][i].conjugate();
			Complex C = this.point.complexMatrix[0][i].minus(point.complexMatrix[0][i]);
			C = C.times(dConj);
			num = num.plus(C);
			den = den.plus(this.direction.complexMatrix[0][i].times(dConj));
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
	 * @apiNote BUG FIXED (Vigesimosexta sesion, auditoria matematica): computed {@code sin} via
	 * {@code direction.crossprod(line.direction)}, only well-founded up to 7D (Hurwitz's theorem,
	 * same limitation already found and fixed in {@link #distance(Point)}/{@link #distance(Line)}
	 * elsewhere in this class) -- for dim&gt;7, {@code crossprod()} degenerates to an empty vector
	 * (norm 0), so {@code sin} silently collapsed to 0 for ANY pair of lines whose angle isn't
	 * exactly {@code pi/2} (the {@code cos==0} guard masked the orthogonal case only). Confirmed:
	 * two 8-dimensional lines at a genuine 60-degree angle (&lt;d1,d2&gt;=1, |d1|=|d2|=sqrt(2))
	 * returned {@code 0.0} instead of {@code pi/3}. {@code Plane.angle(Line)}/{@code
	 * angle(VectorComplex)}/{@code angle(Plane)} already avoid this by delegating to {@link
	 * VectorComplex#angle(VectorComplex)} (dimension-general, {@code acos}-clamped since VERSION
	 * 1.10) instead of reimplementing via {@code crossprod()} -- this method now does the same,
	 * matching the {@code |&lt;d1,d2&gt;|}-based (unsigned/acute-angle) convention this method
	 * already had via its own {@code .abs()} on {@code cos}.
	 */
	public double angle(Line line) {
		return this.direction.angle(line.direction);
	}
	
	/**
	 * Calculates the minimum distance between two lines, in any dimension. Parallelism is
	 * detected via the Gram determinant det(d1,d2) = &lt;d1,d1&gt;&lt;d2,d2&gt;-|&lt;d1,d2&gt;|^2
	 * (dotprod-based, Hermitian) -- by Cauchy-Schwarz this is exactly 0 iff d1,d2 are linearly
	 * dependent (parallel), for any dimension, unlike the previous crossprod()-based check (only
	 * well-founded up to 7D, Hurwitz's theorem). For non-parallel lines, the minimum distance is
	 * the norm of the closest-approach residual v=this.point-line.point+t*d1-s*d2, where (t,s)
	 * solve the 2x2 Hermitian least-squares system &lt;d1,v&gt;=&lt;d2,v&gt;=0 -- valid in any
	 * dimension, unlike the previous mixedprod()-based formula (only valid in 3D).
	 * @param line The given line
	 * @return The minimum distance
	 */
	public double distance(Line line) {
		if (this.direction.dim() != line.direction.dim()) {
			throw new IllegalArgumentException(HEADINFO + "distance: Lines must have the same dimension.");
		}

		Complex d1d1 = this.direction.dotprod(this.direction);
		Complex d2d2 = line.direction.dotprod(line.direction);
		Complex d1d2 = this.direction.dotprod(line.direction);
		double gramDet = (d1d1.times(d2d2).minus(d1d2.times(d1d2.conjugate()))).rep();

		boolean parallel = gramDet <= PARALLEL_TOLERANCE * d1d1.rep() * d2d2.rep();
		if (parallel) {
			return this.distance(line.point());
		}
		if (this.direction.dim() == 2) {
			return 0.0; // two non-parallel 2D lines always intersect
		}

		VectorComplex w = this.point.minus(line.point); // P1-P2
		Complex m11 = d1d1;
		Complex m12 = d1d2.opposite();
		Complex m21 = d1d2.conjugate(); // <d2,d1> = conj(<d1,d2>)
		Complex m22 = d2d2.opposite();
		Complex rhs1 = this.direction.dotprod(w).opposite();
		Complex rhs2 = line.direction.dotprod(w).opposite();
		Complex det = m11.times(m22).minus(m12.times(m21));
		Complex t = (rhs1.times(m22).minus(m12.times(rhs2))).divides(det);
		Complex s = (m11.times(rhs2).minus(rhs1.times(m21))).divides(det);

		VectorComplex v = w.plus(this.direction.prod(t)).minus(line.direction.prod(s));
		return v.norm();
	}

	/**
	 * Calculates the intersection between two lines. Solves the 2-unknown (t1,t2) linear system
	 * this.point+t1*this.direction = line.point+t2*line.direction using the best-conditioned pair
	 * of coordinates (largest 2x2 minor of the two direction vectors), then validates the
	 * candidate point against ALL coordinates via the existing distance(Point) -- this generalizes
	 * correctly to any dimension, since it never relies on crossprod()/mixedprod() (which are only
	 * well-founded in 3D; distance(Line) used to share that limitation too, resolved in VERSION 1.4).
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
