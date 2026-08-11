package com.ipserc.arith.polynom;
/**
 * 
 * @author ipserc
 *
 */

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.syseq.Syseq;

/**
 * Structure that stores one spline segment (piece) within a {@link Spline}: its defining
 * interval and its interpolation polynomial. The {@code degree} field is redundant (it could
 * be derived from the polynomial) but is kept for convenient access.
 */
class SplineComponent extends Polynom {
	//int degree;
	public MatrixComplex interval = new MatrixComplex(2,2);
	public Polynom polynom;
	
	public SplineComponent(int degree) {
		//this.degree = degree;
		//this.polynom = new Polynom(degree);
	}

	/*
	public SplineComponent(int degree, MatrixComplex interval) {
		//this.degree = degree;
		this.interval = interval;
		this.polynom = new Polynom(degree);
	}
	*/
}

/**
 * 
 *
 */
public class Spline extends SplineComponent {
	private final static String HEADINFO = "Spline --- INFO: ";
	private final static String VERSION = "1.2 (2026_0811_2130)";
	/* VERSION Release Note
	 *
	 * 1.2 (2026_0811_2130)
	 * Corregido error de generacion de Javadoc preexistente: walkInterval() (sin argumentos)
	 * documentaba un @param "samples" inexistente y un @return vacio. Sin cambios funcionales.
	 *
	 * 1.1 (2026_0807_1500)
	 * Audited interpolate3Natural() -- the natural cubic spline formula/construction itself is
	 * correct (verified against an independent Thomas-algorithm reference, ~1e-14 for 4/5/6-point
	 * and irregular-spacing datasets). Two real bugs found and fixed, both in the shared
	 * MatrixComplex/Syseq infrastructure this method drives into, exposed by minimal point counts:
	 * (1) a Spline with exactly 2 points (0 interior unknowns) crashed with NegativeArraySizeException
	 * building the degenerate Syseq(0) -- guarded with "if (this.intervals > 1)" around the system
	 * build/solve, skipping straight to the (trivially correct, all M=0) polynomial construction,
	 * since MatrixComplex.cols() can't recover a 0-row matrix's intended column count.
	 * (2) a Spline with exactly 3 points (1 interior unknown) silently computed a wrong M_1 --
	 * root cause was MatrixComplex.dividesleft(), see its VERSION 1.56 for detail; nothing to fix
	 * here once that was corrected.
	 * De propina, the loop building M's super-diagonal had a guard far looser than intended
	 * ("j+1 <= this.intervals" instead of "j+1 < M.cols()-1") -- confirmed harmless in practice (the
	 * unconditional RHS write on the next line always overwrote the errant value when the guard was
	 * too loose), but tightened to the mathematically correct bound instead of relying on write-order
	 * to paper over it.
	 * Verified (ScratchSplineAudit01.java, conserved): 6/6 datasets now pass, including the
	 * previously-failing 3-point (matches reference to ~1e-14, was off by 0.54) and 2-point
	 * (degenerates to a straight line as expected, was a crash) cases -- 33/33 total checks OK.
	 *
	 * 1.0 (2023_1108_1330)
	 * Initial release
	 */
	
	private int degree;
	private SplineComponent[] component;
	private int intervals;
	private MatrixComplex points;

	final boolean DEBUG_ON = true;

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
	

	public Spline() {
		super(0);
		this.degree = 0;		
	}
	
	/**
	 * 
	 * @param degree
	 * @param points
	 */
	public Spline(int degree, MatrixComplex points) {
		super(degree);
		this.degree = degree;
		this.points = points.copy();
		// this.points.quicksortup(0);
		fillIntervals(points);
	}

	/**
	 * 
	 * @param degree
	 * @param strpoints
	 */
	public Spline(int degree, String strpoints) {
		super(degree);
		this.degree = degree;
		this.points = new MatrixComplex(strpoints);
		// this.points.quicksortup(0);
		fillIntervals(this.points);
	}	

	/**
	 * 
	 * @param points
	 */
	private void fillIntervals(MatrixComplex points) {
		this.intervals = points.rows()-1;
		this.component = new SplineComponent[this.intervals];
		for (int i = 0; i < this.intervals; ++i) {
			this.component[i] = new SplineComponent(degree);
			this.component[i].interval.setItem(0, 0, points.getItem(i, 0));
			this.component[i].interval.setItem(0, 1, points.getItem(i, 1));
			this.component[i].interval.setItem(1, 0, points.getItem(i+1, 0));
			this.component[i].interval.setItem(1, 1, points.getItem(i+1, 1));
		}		
		
	}
	
	/*
	 * ***********************************************
	 * SETTERS & GETTERS
	 * ***********************************************
	 */

	/**
	 * 
	 * @param i
	 * @return
	 */
	public MatrixComplex getInterval(int i) {
		return this.component[i].interval;
	}
	
	/**
	 * 
	 * @return
	 */
	public int intervals() {
		return this.intervals;
	}
	
	/**
	 * 
	 */
	public int degree() {
		return 	this.degree;
	}
	
	/**
	 * 
	 * @param showinternals
	 */
	public void interpolate(boolean showinternals) {
		switch (degree) {
		case 1: interpolate1(); break;
		case 3: interpolate3(1, showinternals); break;
		default:
			System.out.println("Not implemented yet.");
		}
	}
	
	/**
	 * 
	 */
	public void interpolate1() {
		for (int i = 0; i < this.intervals; ++i) {
			this.component[i].polynom = this.interpolationNewton(this.component[i].interval).copy();
		}
	}

	/**
	 * 
	 * @param type 0 Natural, 1 Priodico, 2 Completo
	 */
	public void interpolate3(int type, boolean showinternals) {
		switch (type) {
		case 1: interpolate3Natural(showinternals); break;
		case 2: interpolate3Periodic(showinternals); break;
		case 3: interpolate3Complete(showinternals); break;
		}
	}

	
	private Complex t(int i) {
		return this.points.getItem(i, 0);
	}
	
	private Complex y(int i) {
		return this.points.getItem(i, 1);
	}

	private Complex h(int i) {
		return this.t(i+1).minus(this.t(i));
	}
	
	private Complex u(int i) {
		return (h(i).plus(h(i-1))).times(2);
	}
	
	private Complex alpha(int i) {
		Complex c1, c2;
		c1 = (this.y(i+1).minus(this.y(i))).divides(h(i));
		c2 = (this.y(i).minus(this.y(i-1))).divides(h(i-1));
		return (c1.minus(c2)).times(6);
	}

	private Complex Mnatural(Syseq M, int i) {
		if (i == 0 || i > this.intervals-1) return Complex.ZERO;
		return M.partSol(i-1);
	}


	/**
	 * https://riull.ull.es/xmlui/bitstream/915/6225/1/Funciones+Spline.pdf
	 */
	public void interpolate3Natural(boolean showinternals) {
		Syseq M = new Syseq(this.intervals-1);

		/* -------------   showinternals BLOCK   ------------- */
		if (showinternals)
			System.out.println("******** Intervals: " + this.intervals);
		/* ------------- END showinternals BLOCK ------------- */

		// With fewer than 2 intervals there are no interior unknowns to solve (natural
		// boundary conditions already fix M_0=M_n=0 -- see Mnatural()): building/solving M
		// would hit a degenerate 0-unknown Syseq (NegativeArraySizeException, MatrixComplex.cols()
		// can't recover the intended column count from a 0-row matrix). Skip straight to the
		// (trivial, straight-line) polynomial construction below.
		if (this.intervals > 1) {
			// Builds the equation systems to get the solution M (pag 19 (2.9))
			for (int i = 1, j = 0; i < this.intervals; ++i, ++j) {
				if (j-1 >= 0) M.setItem(j, j-1, h(i-1));
				if(j >= 0) M.setItem(j, j, u(i));
				if(j+1 < M.cols()-1) M.setItem(j, j+1, h(i));
				M.setItem(j, M.cols()-1, alpha(i));
			}

			/* -------------   showinternals BLOCK   ------------- */
			if (showinternals)
				M.print("******** Sistema M");
			/* ------------- END showinternals BLOCK ------------- */

			M.solveq();

			/* -------------   showinternals BLOCK   ------------- */
			if (showinternals) {
				M.printSol("******** Soluciones Sistema M");
				M.checkSol(M.partSol());
			}
			/* ------------- END showinternals BLOCK ------------- */
		}

		for (int i = 0; i < this.intervals; i++) {
			Polynom P1 = new Polynom(1);
			Polynom P2 = new Polynom(1);
			Polynom P3 = new Polynom(1);
			Polynom P4 = new Polynom(1);
			Complex hi = h(i);
			Complex Mi = Mnatural(M,i);
			Complex Mj = Mnatural(M,i+1);				// j means i+1
			Complex ti = t(i);
			Complex tj = t(i+1);						// j means i+1
			Complex yi = this.points.getItem(i, 1);
			Complex yj = this.points.getItem(i+1, 1);	// j means i+1

			P1.setItem(0, 0, tj);												//	tj
			P1.setItem(0, 1, -1.0);												//	-x
			P1 = P1.power(3);													//	(tj-x)³
			P1 = P1.times(Mi).divides(hi.times(6.0));							//	Mi/6/hi·(tj-x)³
			
			P2.setItem(0, 0, ti.opposite());									//	-ti
			P2.setItem(0, 1, 1.0);												//	x
			P2 = P2.power(3);													//	(x-ti)³
			P2 = P2.times(Mj).divides(h(i).times(6.0));							//	Mj/6/hi·(x-ti)³
			
			P3.setItem(0, 0, ti.opposite());									//	-ti
			P3.setItem(0, 1, 1.0);												//	x
			P3 = P3.times((yj.divides(hi)).minus(Mj.times(hi).divides(6.0)));	//	(x-ti)·(yj/hi-(Mj·hi/6))

			P4.setItem(0, 0, tj);												//	tj
			P4.setItem(0, 1, -1.0);												//	-x
			P4 = P4.times((yi.divides(hi)).minus(Mi.times(hi).divides(6.0)));	//	(tj-x)·(yi/hi-(Mi·hi/6)
			
			this.component[i].polynom = (P1.plus(P2).plus(P3).plus(P4)).copy();
		}
	}
	
	public void interpolate3Periodic(boolean showinternals) {
		// TODO
		// To be done
	}
	
	public void interpolate3Complete(boolean showinternals) {
		// TODO
		// To be done
	}
	
	/**
	 * 
	 * @param point
	 * @return
	 */
	
	public int getComponentFromPoint_DEBUG(Complex point) {
		Boolean check1, check2;
		double op1, op2;
		point.println("\npoint: ");
		for (int i = 0; i < this.intervals; ++i) {
			System.out.println("Interval: " + i);
			
			this.component[i].interval.getItem(0,0).println("P0: ");
			op1 = point.mod()-this.component[i].interval.getItem(0,0).mod();
			System.out.println("op1: " + op1);
			check1 = op1 >= 0.0;
			System.out.println("Check >= 0 : " + check1);

			this.component[i].interval.getItem(1,0).println("P1: ");
			op2 = point.mod()-this.component[i].interval.getItem(1,0).mod();
			System.out.println("op2 :" + op2);
			check2 = op2 <= 0.0; 
			System.out.println("Check <= 0 : " + check2);
			if (check1 && check2) 
				return i;
		}
		return -1;
	}

	/**
	 * 
	 * @param point
	 * @return
	 */
	public int getComponentFromPoint1(Complex point) {
		for (int i = 0; i < this.intervals; ++i) {
			if ((point.mod()-this.component[i].interval.getItem(0,0).mod()) >= 0.0 && 
					(point.mod()-this.component[i].interval.getItem(1,0).mod()) <= 0.0) 
				return i;
		}
		point.println("Out of bounds: ");
		System.out.println("cre:" + point.cre());
		return -1;
	}
	
	/**
	 * 
	 * @param point
	 * @return
	 */
	public int getComponentFromPoint(Complex point) {
		for (int i = 0; i < this.intervals; ++i) {
			if ((point.cre()-this.component[i].interval.getItem(0,0).cre()) >= 0.0 && 
					(point.cre()-this.component[i].interval.getItem(1,0).cre()) <= 0.0) 
				return i;
		}
		point.println("Out of bounds: ");
		System.out.println("cre:" + point.cre());
		return -1;
	}
	
	/**
	 * 
	 * @param point
	 * @return
	 */
	public Complex eval(Complex point) {
		int compNbr = getComponentFromPoint(point);
		if (compNbr == -1) return new Complex();
		return this.component[compNbr].polynom.eval(point);
	}
	
	/**
	 * 
	 * @param lolimit
	 * @param uplimit
	 * @return
	 */
	private MatrixComplex walkIntervalRE(Complex lolimit, Complex uplimit) {
		Complex.storeFormatStatus();
		Complex.setScientificON(20);
		Complex vector = uplimit.minus(lolimit);
		Complex nextPoint = new Complex();
		MatrixComplex points = new MatrixComplex(sampleBase,2);

		//Recorrer la recta con distancia Euclidea
		double vectSlope = vector.imp()/vector.rep();
		double vectAngle = Math.atan(vectSlope);
		double projRe = vector.mod() * Math.cos(vectAngle);
		double stepRe = projRe / (sampleBase) * Math.signum(vector.rep());
		double nextRep, nextImp;
		
		int iter = 0;
		nextPoint = lolimit.copy();
		
		/** /
		System.out.println("vectSlope:" + vectSlope);
		System.out.println("vectAngle: PI*" + vectAngle*Math.PI);
		System.out.println("projRe   :" + projRe);
		System.out.println("stepRe   :" + stepRe);
		System.out.println("iter:" + iter + "   nextPoint:" + lolimit.toString());
		/**/

		points.setItem(0, 0, lolimit);
		points.setItem(0, 1, this.eval(lolimit));

		while (++iter < sampleBase) {
			//System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
			nextRep = nextPoint.rep() + stepRe;
			nextImp = lolimit.imp() + vectSlope * (nextRep - lolimit.rep());
			nextPoint.setComplexRec(nextRep, nextImp);
			points.setItem(iter, 0, nextPoint);
			points.setItem(iter, 1, this.eval(nextPoint));
		}		
		// System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
		Complex.restoreFormatStatus();		
		return points;
	}
	
	/**
	 * 
	 * @param lolimit
	 * @param uplimit
	 * @return
	 */
	private MatrixComplex walkIntervalIM(Complex lolimit, Complex uplimit) {
		Complex.storeFormatStatus();
		Complex.setScientificON(20);
		Complex vector = uplimit.minus(lolimit);
		Complex nextPoint = new Complex();
		MatrixComplex points = new MatrixComplex(sampleBase,2);

		//Recorrer la recta con distancia Euclidea
		Double vectSlope = vector.rep()/vector.imp();
		double vectAngle = Math.atan(vectSlope);
		double projIm = vector.mod() * Math.cos(vectAngle);
		double stepIm = projIm / (sampleBase) * Math.signum(vector.imp());
		double nextRep, nextImp;
		
		int iter = 0;
		nextPoint = lolimit.copy();
		
		/** /
		System.out.println("vectSlope:" + vectSlope);
		System.out.println("vectAngle: PI*" + vectAngle*Math.PI);
		System.out.println("projIm   :" + projIm);
		System.out.println("stepIm   :" + stepIm);
		System.out.println("iter:" + iter + "   nextPoint:" + lolimit.toString());
		/**/
		
		points.setItem(0, 0, lolimit);
		points.setItem(0, 1, this.eval(lolimit));

		while (++iter < sampleBase) {
			//System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
			nextImp = nextPoint.imp() + stepIm;
			nextRep = lolimit.rep() + vectSlope * (nextImp - lolimit.imp());
			nextPoint.setComplexRec(nextRep, nextImp);
			points.setItem(iter, 0, nextPoint);
			points.setItem(iter, 1, this.eval(nextPoint));
		}
		// System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
		Complex.restoreFormatStatus();
		return points;
	}
	
	/**
	 * 
	 * @param lolimit
	 * @param uplimit
	 * @return
	 */
	public MatrixComplex walkInterval(Complex lolimit, Complex uplimit) {
		Complex vector = uplimit.minus(lolimit);
		double vectSlope = vector.imp()/vector.rep();
		double vectAngle = Math.atan(vectSlope);
		
		vectAngle = vectAngle > Math.PI ? Math.PI - vectAngle : vectAngle;
		vectAngle = vectAngle < -Math.PI ? Math.PI + vectAngle : vectAngle;
		
		if (((vectAngle >= Math.PI/4) && (vectAngle < 3*Math.PI/4 )) ||
				((vectAngle >= -3*Math.PI/4) && (vectAngle < -Math.PI/4 ))) {
			return walkIntervalIM(lolimit, uplimit);
		}
		else return walkIntervalRE(lolimit, uplimit);
	}

	/**
	 * Walks the whole spline, from the first component's lower limit to the last component's upper
	 * limit.
	 * @return The matrix of sampled points along the whole spline.
	 */
	public MatrixComplex walkInterval() {
		Complex lolimit = this.component[0].interval.getItem(0,0).copy();
		Complex uplimit = this.component[intervals-1].interval.getItem(1,0).copy();
		return walkInterval(lolimit, uplimit);
	}
	
	/**
	 * 
	 */
	public void print() {
		System.out.println(Complex.repeat("-", 20));
		System.out.println("Spline definition");
		System.out.println("Degree: " + this.degree);
 		for(int i = 0; i < this.intervals; ++i) {
 			//System.out.println(Complex.repeat(" · ", 7));
			this.component[i].polynom.print("Spline " + i);
			this.component[i].interval.getItem(0, 0).print("  ----  Interval " + i + ":[");
			this.component[i].interval.getItem(1, 0).print(", ");
			System.out.println("]");
		}
		System.out.println(Complex.repeat("-", 20));
	}
	
	/**
	 * Returs a new set of sampling ponts, 19 samples, using the Chebyshev nodes to get the values of the function
	 * The Chebyshev nodes are projected in the dominium of the Spline's interpolation function to get the expected values 
	 * Once the new set of samples is calculated it can be used to calculate a more friendly interpolation polynomial 
	 * @param pTable The original table with teh samples
	 * @return The new pTable with the samples distributed as Chebyshev nodes within the interval
	 */
	public MatrixComplex chebyshevNodes(MatrixComplex pTable) {
		Complex.storeFormatStatus();
		Complex.setScientificON(30);
		int k;
		int nodes = pTable.rows();
		nodes = nodes > 19 ? 19 : nodes; // degree 19 is the naximum for numeric stability
		MatrixComplex newpTable = new MatrixComplex(nodes,2);
		int newpTableRows = newpTable.rows();
		Complex lolimit = pTable.getItem(0,0).copy();
		Complex uplimit = pTable.getItem(pTable.rows()-1,0).copy();
		Complex v1 = (lolimit.minus(uplimit)).divides(2);
		Complex v2 = (lolimit.plus(uplimit)).divides(2);
		Complex xhat = new Complex();
		double factor = Math.PI/(2*(newpTableRows));
		newpTable.setRow(0,pTable.getRow(0));
		for (k = 1; k < newpTableRows-1; ++k) {
			xhat = Complex.cos(new Complex((2*k+1)*factor));
			newpTable.setItem(k, 0, xhat.times(v1).plus(v2));
			newpTable.setItem(k, 1, this.eval(newpTable.getItem(k, 0)));
		}
		newpTable.setRow(k,pTable.getRow(pTable.rows()-1));
		Complex.restoreFormatStatus();
		return newpTable;
	}

}
