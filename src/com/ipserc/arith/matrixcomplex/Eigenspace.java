/**
 * 
 */
package com.ipserc.arith.matrixcomplex;



/**
 * @author ipserc
 *
 */

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.arith.syseq.Syseq;
import com.ipserc.arith.vectorcomplex.*;

public class Eigenspace extends MatrixComplex {
	
	private final static String HEADINFO = "Eigenspace --- INFO: ";
	private final static String VERSION = "1.7 (2026_0802_0131)";
	/* VERSION Release Note
	 *
	 * 1.7 (2026_0802_0131)
	 * eigenval() now solves the characteristic polynomial via Polynom.solveRobust() instead of
	 * solve() (Durand-Kerner only) -- same fix as MatrixComplex.rank2() (previous commit), same
	 * root cause (Durand-Kerner's arithmetic-overflow exception on higher-degree/widely-spread-
	 * coefficient polynomials). Confirmed with a live repro: TestDiag.java/TestDiag01.java were
	 * crashing outright (uncaught IllegalArgumentException from solveWeierstrass(), not a check
	 * failure) via Diagfactor -> Eigenspace's own constructor -- both now complete cleanly.
	 * Verified with the full Schurfactor/Jordan-block sweep already used this session to
	 * characterize Eigenspace's fragility: byte-for-byte identical results to plain Durand-Kerner
	 * in every case that already worked -- solveRobust() only ever falls back to Aberth-Ehrlich on
	 * cases that were already throwing, never changes an already-successful result. Does NOT fix
	 * the separate, already-documented precision ceiling for repeated eigenvalues in larger Jordan
	 * blocks (see Jordan.java/TestJordanAudit01, MatrixComplex.logm()) -- that is inherent to
	 * Newton-type root-finding near a multiple root, not an overflow.
	 *
	 * 1.6 (2026_0801_2252)
	 * Bug fix: eigenval() grouped consecutive roots into "the same eigenvalue" using
	 * Complex.equals()'s fixed ~1e-11 tolerance, tighter than Durand-Kerner's actual error on a
	 * genuinely repeated root (measured ~5.3e-9 for a double eigenvalue) -- silently splitting one
	 * defective eigenvalue of multiplicity 2 into two "eigenvalues" of multiplicity 1 each,
	 * corrupting Jordan.factorize() downstream (see Jordan.VERSION 1.2). Fixed to group with the
	 * same bestNumDecs()-based rounded comparison arithmeticMultiplicity() already promises, and to
	 * store the GROUP AVERAGE of the grouped roots as the representative eigenvalue instead of an
	 * arbitrary raw member (measurably closer to the true value).
	 *
	 * 1.5 (2025_0319_2345)
	 * 	public MatrixComplex eigenvector(int i) {
	 *  public void check() {
	 *  public void checkEigenvectors() {
	 * 
	 * 1.4 (2022_0123_0100)
	 * toMaxima_eigenvalues(boolean expand)
	 * toMaxima_eigenvectors(boolean expand)
	 * toMaxima_charpoly(boolean expand)
	 * toOctave_eigenvectors()
	 * toWolfram_eigenvectors() --> Eigenvectors[...]
	 * eigenvectors3(): Checks if (dMatrix.typeEqSys() == INCONSISTENT) and no eigenvector is calculated
	 * toWolfram_eigenvalues() --> Eigenvalues[...]
	 * toWolfram_eigensystem() --> Eigensystem[...]
	 * eigensolve() returns the eigenvectors normalized, if it is possible
	 * eigenvectors2() DEPRECATED - REMOVED
	 * geometricMultiplicity(Complex eigenVal) Based on rank
	 * geometricMultiplicityFromEVectors(Complex eigenVal) geometric multiplicity based on linearity independence of Eigenvectors
	 * root(int rootId) Returns the root rootId index
	 * 
	 *
	 * 1.3 (2021_0929_2100)
	 * eigensolve uses eigenvect3 which has changed MatrixComplex cMatrix, dMatrix; to Syseq dMatrix; for solving the system equations based on Syseq class.
	 * eigenvect2 is deprecated.
	 * 
	 */

	 /**
	  * Controls whether the eigenvectors are normalized or not
	  * Only the eigenvectors, the solutions rest as they were calculated
	  */
	 private static boolean normalizeVectors = false;

	 /**
	  * Sets the order in which the roots and solutions of the Characteristic polynomila are stored
	  */
	 private static Order order = Order.DOWN;

	/**
	 * Enumeration that gives the value of the order in which the eigenvalues, and therefore, the eigenvectors are returned
	 * DOWN: The eigenvalues are sorted from higher to lower
	 * UP: The eigenvalues are sorted from lower to higher
	 */
	public enum Order {DOWN, UP};
	
	private MatrixComplex roots; // The roots of the characteristics polynomial
	private MatrixComplex eigenvalues; // First Column:The eigenvalues, the roots with no repetitions, 2nd Column: The artihmetic multiplicity
	private MatrixComplex solutions; // The solution vectors of the eigen equations
	private MatrixComplex eigenvectors; // The eigenvectors taken from vectors by removing the null and the linear combination ones
	private Polynom charactPoly;
	private Complex seed;
	
	

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
	 * Instantiates an eigenspace class form a complex array previously defined.
	 * @param cmatrix The MatrixComplex that generates the Eigenspace
	 */
	public Eigenspace(MatrixComplex cmatrix) {
		super();
		this.complexMatrix = cmatrix.complexMatrix.clone();
		this.seed = Complex.ONE;
		calculate();
	}

	/**
	 * Instantiates an eigenspace class form a complex array previously defined.
	 * @param seed Is the value of the constant used to find the eigenvectors 
	 * @param cmatrix The MatrixComplex that generates the Eigenspace
	 */
	public Eigenspace(Complex seed, MatrixComplex cmatrix) {
		super();
		this.complexMatrix = cmatrix.complexMatrix.clone();
		this.seed = seed;
		calculate();
	}
	
	/**
	 * Instantiates an eigenspace class from matrix represented as a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix The matrix that generates the Eigenspace
	 */
	public Eigenspace(String strMatrix) {
		super(strMatrix);
		seed = Complex.ONE;
		calculate();		
	}
	
	/**
	 * Instantiates an eigenspace class from matrix represented as a string, rows are separated with ";", cols are separated with ",".
	 * @param seed Is the value of the constant used to find the eigenvectors 
	 * @param strMatrix The matrix that generates the Eigenspace
	 */
	public Eigenspace(Complex seed, String strMatrix) {
		super(strMatrix);
		this.seed = seed;
		calculate();
	}

	/*
	 * ***********************************************
	 * SETTERS
	 * ***********************************************
	 */
	
	 /**
	  * Sets the order in which the roots of the characteristic polynomial are found. DOWN means in decreasing order.
	  * Eigenvalues ​​are sorted only ONCE. Subsequent changes to the order affect only the roots.
	  */
	public static void setOrderDOWN() {
		order = Order.DOWN;
	}

	/**
	  * Sets the order in which the roots of the characteristic polynomial are found. UP in increasing order.
	  * Eigenvalues ​​are sorted only ONCE. Subsequent changes to the order affect only the roots.
	 */
	public static void setOrderUP() {
		order = Order.UP;
	}

	/**
	 * Sets whether the eigenvectors are normalized or not. It only affect to the eigenvectors, the roots remain as they were calculated.
	 * @param val True normalize eigenvetors, false keep as they were calculated.
	 */
	public static void setNormalize(boolean val) {
		normalizeVectors = val;
	}

	/*
	 * ***********************************************
	 * GETTERS
	 * ***********************************************
	 */

	 /**
	  * Retrieves the value of normalizeVectors which controls whether the eigenvectors are normalized or not. 
	  * @return The value of normalizeVectors.
	  */
	 public static boolean getNormalice() {
		return normalizeVectors;
	 }

	/**
	 * Private Method. Calculates the Eigenspace components. Characteristic Polynomial, Eigenvalues and Eigenvetors
	 */
	private void calculate() {
		charactPoly = this.charactPoly();
		eigenval();
		eigensolve();
	}
	
	/**
	 * Returns the solutions of the eigen system equations as a MatrixComplex with the vectors in the array rows
	 * @return The eigen system equations solutions
	 */
	public MatrixComplex solutions() {
		return solutions;
	}

	/**
	 * Returns ith the solutions of the eigen system equations as a MatrixComplex vector of 1 row 
	 * @param idx the index of the solution
	 * @return The ith solution
	 */
	public MatrixComplex solution(int idx) {
		MatrixComplex row; 
		if (idx >= solutions.rows()) return (MatrixComplex)null;
		row = new MatrixComplex(1, solutions.cols());
		row.complexMatrix[0] = solutions.complexMatrix[idx];
		return row;
	}

	/**
	 * Returns the eigenvectors as a MatrixComplex with the vectors in the array rows
	 * @return The eigenvectors
	 */
	public MatrixComplex eigenvectors() {
		return eigenvectors;
	}
	
	/**
	 * Returns the i-th eigenvector as a MatrixComplex one row
	 * @param idx The i-th eigenvector to get
	 * @return The i-th eigenvector
	 */
	public MatrixComplex eigenvector(int idx) {
		return eigenvectors.getRow(idx).clone();
	}
	
	/**
	 * Returns the eigenvectors as a MatrixComplex with the vectors in the array rows
	 * @return The eigenvectors
	 */
 	public MatrixComplex base() {
		return solutions;
	}
	
	/**
	 * Returns the root rootId index from the roots of the characteristic polynomial
	 * @return The characteristic polynomial root rootId
	 */
	public MatrixComplex root(int rootId) {
		return roots.getRow(rootId);
	}
	
	/**
	 * Returns ALL the roots as a MatrixComplex with the roots of the characteristic polynomial in the rows of one column array
	 * @return The characteristic polynomial roots
	 */
	public MatrixComplex roots() {
		return roots;
	}

	/**
	 * Returns the eigenvalues as a MatrixComplex with the eigenvalues in the rows of one column array
	 * @return The eigenvalues
	 */
	public MatrixComplex eigenvalues() {
		return eigenvalues;
	}

	/**
	 * Gets the eigenvalue index "eigValId" from the eigenvalues array
	 * @param eigValId
	 * @return
	 */
	public Complex getEigenValue(int eigValId) {
		int evvCol = 0;
		return eigenvalues().getRow(eigValId).complexMatrix[0][evvCol];
	}
	
	/**
	 * Gets the arithmetic mutltiplicity of eigenvalue index "eigValId" from the eigenvalues array
	 * @param eigValId
	 * @return
	 */
	public int getEigenValueArithMult(int eigValId) {
		int evamCol = 1;
		return (int)eigenvalues().getRow(eigValId).complexMatrix[0][evamCol].cre();
	}
	
	
	/**
	 * Returns the characteristic polynomial as a polynomial
	 * @return The characteristic polynomial
	 */
	public Polynom getCharactPoly() {
		return charactPoly;
	}
	
	/**
	 * Gets the order in which the eigenvalues are sorted. The eigenvectors follow this order
	 * @return the order in which the eigenvalues are stored
	 */
	public static Order order() {
		return order;
	}
	
	public String getOrder() {
		return order.toString();
	}
	
	
	/*
	 * ***********************************************
	 * OPERATORS
	 * ***********************************************
	 */

	/**
	 * Returns the arithmetic multiplicity of an specific eigenvalue
	 * @param eigenVal The value to evaluate the arithmetic multiplicity
	 * @return The arithmetic multiplicity
	 */
	public int arithmeticMultiplicity(Complex eigenVal) {
		return arithmeticMultiplicity(eigenVal, this.bestNumDecs());
	}
	
	/**
	 * Returns the arithmetic multiplicity of an specific eigenvalue using an specific number of decimals of precision
	 * @param eigenVal The value to evaluate the arithmetic multiplicity
	 * @param digits The number of decimals of precision
	 * @return The arithmetic multiplicity
	 */
	public int arithmeticMultiplicity(Complex eigenVal, int digits) {
		int row;
		for (row = 0; row < eigenvalues.rows(); ++row) {
			if (Complex.round(eigenVal, digits).equals(Complex.round(eigenvalues.getItem(row, 0), digits))) break;
		}
		return (int)eigenvalues.getItem(row, 1).cre();
	}
	
	public int arithmeticMultiplicity__(Complex eigenVal, int digits) {
		final boolean DEBUG_ON = false;
		final String METH_NAME = "arithmeticMultiplicity";
		int arithMult = 0;
		for (int i = 0; i < this.rows(); ++i) {
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				Complex.storeFormatStatus();
				Complex.setFixedOFF(); ;
				eigenvalues.getItem(i,0).println(HEADINFO + METH_NAME + ":eigenvalues.getItem("+i+",0):");
				eigenVal.println(HEADINFO + METH_NAME + ":eigenVal:");
				eigenvalues.getItem(i,0).minus(eigenVal).println(HEADINFO + METH_NAME + ":value - eignval:");
				System.out.println("digits:" + digits);
				Complex.round(eigenvalues.getItem(i,0), digits).println(HEADINFO + METH_NAME + ":round(eigenvalues.getItem("+i+",0), "+ digits +"):");
				eigenVal.println(HEADINFO + METH_NAME + ":Complex.round(eigenVal, "+ digits + "):");
				Complex.restoreFormatStatus();
			}
			/* ------------- END DEBUGGING BLOCK ------------- */
				if (Complex.round(eigenvalues.getItem(i,0), digits).equals(Complex.round(eigenVal, digits))) ++arithMult;
		}

		return arithMult;
	}
	
	/**
	 * Returns the geometric multiplicity of an specific eigenvalue using the eigenvectors matrix
	 * The geometric multiplicity of an eigenvalue is the number of linearly independent eigenvectors associated with it. 
	 * That is, it is the dimension of the nullspace of A
	 * The method used here is get the eigenvectors associated to the eigenvalue and calculate the rank of the eigenvector basis
	 * @param eigenVal The value to evaluate the geometric multiplicity
	 * @return The geometric multiplicity
	 */
	public int geometricMultiplicityFromEVectors(Complex eigenVal) {
		final boolean DEBUG_ON = false;
		final String METH_NAME = "geometricMultiplicity";
		MatrixComplex eigenV = new MatrixComplex(solutions.rows(), solutions.cols());

		for (int i = 0; i < this.rows(); ++i) {
				if (eigenvalues.getItem(i,0).equals(eigenVal)) 
					eigenV.complexMatrix[i] = solutions.complexMatrix[i];
		}
		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			//Complex.storeFormatStatus();
			//Complex.setFixedOFF(); ;
			eigenVal.println(HEADINFO + METH_NAME + ":eigenVal:");
			eigenV.println(HEADINFO + METH_NAME + ":eigenV:");
			Complex.restoreFormatStatus();
		}
		/* ------------- END DEBUGGING BLOCK ------------- */
		return eigenV.rank();
	}

	/**
	 * This way of calculating the geometric multiplicity is previous toa calculate the eigenvectors
	 * It should be the preferred method, but right now the calculations are done with the eigenvectors
	 * already calculated, and this way of doing the things should be changed to make all the rest of
	 * things have sense.
	 * It relays on the accuracy of the rank method
	 * @param eigenVal
	 * @return
	 */
	public int geometricMultiplicity(Complex eigenVal) {
		return this.rows() - (this.minus(eye(this.rows()).times(eigenVal))).rank();
	}
	
	/**
	 * DEPRECATED - WILL BE REMOVED
	 * Determines whether a matrix is diagonalizable or not
	 * @return True if the matrix is diagonalizable, otherwise False
	 */
	public boolean isDiagonaizable() {
		boolean DEBUG_ON = false;
		if (solutions.determinant().equals(0,0)) {
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				System.out.println("vectors.determinant().equalsred(0,0)");
			}
			/* ------------- END DEBUGGING BLOCK ------------- */
			return false;
		}
		for (int id = 0; id < eigenvalues.rows(); ++id) {
			if (geometricMultiplicity(eigenvalues.getItem(id,0)) != arithmeticMultiplicity(eigenvalues.getItem(id,0))) return false;
		}
		return true;
	}
	
	/**
	 * Calculates the eigenvalue, characteristic value, or characteristic root associated with eigenvector v by solving the Characteristic Polynomial.
	 * An eigenvalue is a scalar associated with a given linear transformation of a vector space and having the property that there is some nonzero vector which when multiplied by the scalar is equal to the vector obtained by letting the transformation operate on the vector; especially :  a root of the characteristic equation of a matrix
	 * The eigenvalues as a column array
	 */
	public void eigenval() {
		Complex prevRoot, actRoot;
		// Was charactPoly.solve() (Durand-Kerner only, Polynom.solveWeierstrass()). Switched to
		// solveRobust() -- tries Durand-Kerner first, falls back to Aberth-Ehrlich only if that
		// throws an arithmetic-overflow exception -- verified this session with the full
		// Schurfactor/Jordan-block sweep already used to characterize Eigenspace's fragility:
		// byte-for-byte identical results to plain Durand-Kerner in every case that already
		// worked (no silent precision change on the already-working path), while eliminating the
		// crash risk on the higher-degree/widely-spread-coefficient characteristic polynomials
		// that Durand-Kerner alone can hit (same root cause already fixed for MatrixComplex.rank2()
		// this session). Does NOT by itself fix the separate, already-documented precision
		// limitation for repeated eigenvalues in larger Jordan blocks (see Jordan.java/
		// TestJordanAudit01 and MatrixComplex.logm()'s Javadoc) -- that is a precision ceiling
		// inherent to Newton-type root-finding near a multiple root, not an overflow, and neither
		// Durand-Kerner nor Aberth-Ehrlich escapes it.
		roots = charactPoly.solveRobust();
		// eigenvalues.quicksortup(0); // DO NOT USE - SVD factorization doesn't allow this
		// order = Order.UP;
		// By default the order in which the eigenvalues are sorted is from Higher to Lower
		switch (order) {
			case UP: roots.quicksortup(0); break;
			case DOWN: roots.quicksortdown(0); break;
		}

		// Grouping consecutive roots into "the same eigenvalue" must use the same tolerance as
		// arithmeticMultiplicity(Complex) (rounded to bestNumDecs() significant decimals), not
		// Complex.equals()'s fixed ~1e-11 tolerance. Durand-Kerner's actual numerical error on a
		// genuinely repeated root is condition-number-dependent and can be much coarser than that
		// fixed tolerance (confirmed: a double root at -2 converged to -1.9999999973348/
		// -2.0000000026547, 5.3e-9 apart, for a matrix whose bestNumDecs()==3) -- comparing with
		// Complex.equals() silently split one defective eigenvalue of multiplicity 2 into two
		// distinct "eigenvalues" of multiplicity 1 each, which downstream (Jordan.factorize()) built
		// a purely diagonal Jordan form instead of the correct 2x2 block, reconstructing the wrong
		// matrix by ~5.96e-8. Using the same rounded comparison here keeps eigenval()'s own grouping
		// consistent with what arithmeticMultiplicity() already promises callers.
		int digits = this.bestNumDecs();

		// calculates the number of different roots
		int rootCount = 1;
		prevRoot = roots.getItem(0, 0);
		for (int i = 1; i < roots.rows(); ++i) {
			actRoot = roots.getItem(i, 0);
			if (Complex.round(prevRoot, digits).equals(Complex.round(actRoot, digits))) continue;
			++rootCount;
			prevRoot = actRoot;
		}
		// With the number of different roots creates the eignevalues array
		// Col 0 for the eigenvalue, Col 1 for the arithmetic multiplicity
		// The representative eigenvalue stored per group is the AVERAGE of its grouped raw roots,
		// not just the first one encountered: Durand-Kerner's error on the two (or more) roots of a
		// genuinely repeated eigenvalue is not one-sided, so averaging cancels part of it (in the
		// -2 case documented above, the average lands ~5e-12 from the true value vs. ~2.6e-9 for
		// either raw root alone -- about 500x closer). Picking an arbitrary raw member of the group
		// was fine when grouping only fired for near-exact matches (the old ~1e-11 tolerance), but
		// is not precise enough once grouping uses the coarser bestNumDecs()-based tolerance above:
		// feeding an imprecise scalar into (A-eigenval*I) for a generalized-eigenvector chain
		// (Jordan.java) can make that matrix insufficiently close to singular, producing a
		// degenerate (all-zero) generalized eigenvector instead of a real one.
		eigenvalues = new MatrixComplex(rootCount, 2);
		int eigvalIdx = 0;
		int arithMult = 1;
		double sumRe = roots.getItem(0, 0).rep();
		double sumIm = roots.getItem(0, 0).imp();
		prevRoot = roots.getItem(0, 0);
		for (int i = 1; i < roots.rows(); ++i) {
			actRoot = roots.getItem(i, 0);
			if (Complex.round(prevRoot, digits).equals(Complex.round(actRoot, digits))) {
				sumRe += actRoot.rep();
				sumIm += actRoot.imp();
				++arithMult;
			}
			else {
				eigenvalues.setItem(eigvalIdx, 0, new Complex(sumRe/arithMult, sumIm/arithMult));
				eigenvalues.setItem(eigvalIdx, 1, new Complex(arithMult, 0));
				++eigvalIdx;
				sumRe = actRoot.rep();
				sumIm = actRoot.imp();
				arithMult = 1;
				prevRoot = actRoot;
			}
		}
		eigenvalues.setItem(eigvalIdx, 0, new Complex(sumRe/arithMult, sumIm/arithMult));
		eigenvalues.setItem(eigvalIdx, 1, new Complex(arithMult, 0));
	}

	/**
	 * Swaps the order of the roots and the solutions. Use 
	 */
	public void orderSwap() {
		MatrixComplex rootsTmp = roots.clone();
		MatrixComplex solutionsTmp = solutions.clone();
		// swap eigenvalues
		for (int row = 0; row < eigenvalues.rows(); ++row) {
			roots.complexMatrix[eigenvalues.rows() - row - 1] = rootsTmp.complexMatrix[row];
		}
		// swap vectors
		for (int row = 0; row < eigenvalues.rows(); ++row) {
			solutions.complexMatrix[eigenvalues.rows() - row - 1] = solutionsTmp.complexMatrix[row];
		}
		order = order == Order.DOWN? Order.UP : Order.DOWN;
	}
	
	/**
	 * Calculates the eigenvector or characteristic vector of a linear transformation associated to an specific eigenvalues.
	 * The eigenvector is a non-zero vector whose direction does not change when that linear transformation is applied to it
	 * The eigenvector as a column array
	 */
	public void eigensolve() {
		eigenvectors3();
	}

	/**
	 * Private method. Implementation of eigensolve
	 */
	private void eigenvectors3() {
		final boolean DEBUG_ON = false; 
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex I = MatrixComplex.eye(rowLen);
		MatrixComplex eigensolve;
		MatrixComplex cMatrix; 
		Syseq dMatrix;
		Complex eigenval;
		solutions = new MatrixComplex(rowLen, colLen);

		//for (int rowEig = 0, eigVallIdx = 0; rowEig < rowLen; ++eigVallIdx) {
		for (int rowEig = 0, eigVallIdx = 0; eigVallIdx < eigenvalues.rows(); ++eigVallIdx) {
			eigenval = eigenvalues.getItem(eigVallIdx, 0);
			//cMatrix = (I.times(eigenval)).minus(this); //- ORIGINAL
			cMatrix = (this.minus(I.times(eigenval)));
			dMatrix = new Syseq(cMatrix.augment().heap());
			
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				eigenval.println("\n" + HEADINFO + " * * * * * eigenval:");
				dMatrix.println(HEADINFO + "Eq. System for EigenVectors");
				dMatrix.triangle().println(HEADINFO + "Eq. System TRIANGLE for EigenVectors");
		     	System.out.println("MComplex:" + dMatrix.toMatrixComplex());
		     	System.out.println("MComplex:new Syseq(" + dMatrix.preMatrixComplex() +");");
				dMatrix.printSystemEqSolve(outputFormat.MAXIMA, true);
				dMatrix.printSystemEqSolve(outputFormat.OCTAVE, true);
				dMatrix.printSystemEqSolve(outputFormat.WOLFRAM, true);
			}
			/* ------------- END DEBUGGING BLOCK ------------- */

			if (dMatrix.typeEqSys() == INCONSISTENT) {
				rowEig += this.arithmeticMultiplicity(eigenval);
				continue;
			}
			eigensolve = dMatrix.solution(1);
			
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				System.out.println("Ec.Caract.["+rowEig+"]" + dMatrix.toMatrixComplex());
				eigensolve.println(HEADINFO + "eigensolve:");
			}
			/* ------------- END DEBUGGING BLOCK ------------- */
			
			//for (int sol = 0; sol < eigensolve.rows()-rowEig; ++sol) {
			for (int sol = 0; sol < eigensolve.rows(); ++sol) {
				/* -------------   DEBUGGING BLOCK   ------------- */
				if (DEBUG_ON) {
					System.out.println("rowEig.................................:" + rowEig);
					System.out.println("sol....................................:" + sol);
				}
				/* ------------- END DEBUGGING BLOCK ------------- */
				
				if (rowEig+sol >= solutions.rows()) break;
								
				solutions.complexMatrix[rowEig+sol] = eigensolve.complexMatrix[sol].clone();
				
				/* -------------   DEBUGGING BLOCK   ------------- */
				if (DEBUG_ON) {
					System.out.println("solutions.complexMatrix["+(rowEig+sol)+"]:" + solutions.getRow(rowEig+sol).getRow(0));
					System.out.println("eigensolve.complexMatrix["+sol+"].......:" + eigensolve.complexMatrix[sol][0]);
				}
				/* ------------- END DEBUGGING BLOCK ------------- */

			}
			//rowEig += eigensolve.rows();
			rowEig += this.arithmeticMultiplicity(eigenval);
		}
		/* ******************************************** */
		// Normalize the eigenvectors if possible
		if (normalizeVectors)
			solutions = solutions.normalizeByRows();
		/* ******************************************** */

		eigenvectors = setEigenvectors();
		
		// Call to the Garbage Collector
		Runtime.getRuntime().gc();
	}
	
	private MatrixComplex setEigenvectors() {
		int eigenvRows = solutions.rank();
		eigenvectors = new MatrixComplex(eigenvRows, this.cols());

		// Recorremos TODAS las soluciones
		for (int row = 0, i = 0; row < solutions.rows(); ++row) {
			// Pero solo son autovectores las soluciones NO NULAS
			if (solutions.isNullRow(row)) continue;
			else {
				eigenvectors.setRow(i++, solutions.getRow(row));
			}
		}
		return eigenvectors;
	}
	
	/*
	 * ***********************************************
	 * PRINTING
	 * ***********************************************
	 */
	
	/**
	 * Returns the eigenvalues command as a String for the matrix to use in Maxima
	 * @param expand If true adds epxpand() to the expression
	 * @return The eigenvalues command for Maxima 
	 */
	public String toMaxima_eigenvalues(boolean expand) {
		String toMaxima = "eigval: eigenvalues("+this.toMaxima()+")";
		return expand ? "expand("+toMaxima+")" : toMaxima; 		
	}
	
	/**
	 * Returns the eigenvectors command as a String for the matrix to use in Maxima
	 * @param expand If true adds epxpand() to the expression
	 * @return The eigenvectors command for Maxima 
	 */
	public String toMaxima_eigenvectors(boolean expand) {
		String toMaxima = "eigvect: eigenvectors("+this.toMaxima()+")";
		return expand ? "expand("+toMaxima+")" : toMaxima; 		
	}
	
	/**
	 * Returns the charpoly command as a String for the matrix to use in Maxima
	 * @param expand If true adds epxpand() to the expression
	 * @return The charpoly command for Maxima 
	 */
	public String toMaxima_charpoly(boolean expand) {
		String toMaxima = "charpoly("+this.toMaxima()+",t)";
		return expand ? "expand("+toMaxima+")" : toMaxima;
	}
	
	/**
	 * Returns the eig command as a String for the matrix to use in Octave
	 * @return The eig command for Octave
	 */
	public String toOctave_eigenvectors() {
		return "[eVects,eVals] = eig("+this.toMatlab()+",\"vector\")";
	}

	/**
	 * Returns the eigenvectors command as a String for the matrix to use in Wolfram
	 * @return The eigenvectors command for Wolfram
	 */
	public String toWolfram_eigenvectors() {
		return "Eigenvectors["+this.toWolfram()+"]";
	}

	/**
	 * Returns the eigenvalues command as a String for the matrix to use in Wolfram
	 * @return The eigenvalues command for Wolfram
	 */
	public String toWolfram_eigenvalues() {
		return "Eigenvalues["+this.toWolfram()+"]";
	}

	/**
	 * Returns the eigensystem command as a String for the matrix to use in Wolfram
	 * @return The eigensystem command for Wolfram
	 */
	public String toWolfram_eigensystem() {
		return "Eigensystem["+this.toWolfram()+"]";
	}

	/**
	 * Prints the characteristics equations for each eigenvalue in a specific dialect
	 * @param format The dialect used as MATRIXCOMPLEX, MAXIMA, OCTAVE, MATLAB or WOLFRAM
	 * @param display Adds the expand() to Maxima or the disp() to Octave or Matlab
	 */
	public void printCharactEq(MatrixComplex.outputFormat format, boolean display) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Syseq system = new Syseq();

		MatrixComplex I = new MatrixComplex(rowLen, colLen); I.initMatrixDiag(1,0);
		MatrixComplex cMatrix, dMatrix;
		Complex eigenval;
		
		for (int rowEig = 0; rowEig < eigenvalues.rows(); ++rowEig) {
			eigenval = eigenvalues.getItem(rowEig, 0);
			cMatrix = (I.times(eigenval)).minus(this);
			dMatrix = cMatrix.augment().heap();
			system.complexMatrix = dMatrix.complexMatrix;
			System.out.print("Charact.Eq.["+rowEig+"]: " ); system.printSystemEqSolve(format, display);
		}
	}
	
	/**
	 * Prints the characteristics equations associated to an specific eigenvalue in a specific dialect
	 * @param idx The idx of the eigenvalue s array
	 * @param format
	 * @param display
	 */
	public void printCharactEq(int idx, MatrixComplex.outputFormat format, boolean display) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Syseq system = new Syseq();

		if (idx > eigenvalues.rows()-1) {
			System.out.print("No characteristics equations for the idx:" + idx);
			return;
		}
		MatrixComplex I = new MatrixComplex(rowLen, colLen); I.initMatrixDiag(1,0);
		MatrixComplex cMatrix, dMatrix;
		Complex eigenval;
		
		eigenval = eigenvalues.getItem(idx, 0);
		cMatrix = (I.times(eigenval)).minus(this);
		System.out.println("NullSpace[" + cMatrix.toWolfram()+"]");
		dMatrix = cMatrix.augment().heap();
		system.complexMatrix = dMatrix.complexMatrix;
		System.out.print("Charact.Eq.["+eigenval+"]: " ); system.printSystemEqSolve(format, display);
	}
	

	/**
	 * Prints the characteristics equations for each eigenvalue in MATRIXCOMPLEX dialect
	 */
	public void printCharactEq() {
		printCharactEq(outputFormat.MATRIXCOMPLEX, false);
	}

	/*
	 * ***********************************************
	 * CHECKERS
	 * ***********************************************
	 */
	
	/**
	 * Checks the eigenvectors calculated to prove that (AX-λI) = 0
	 */
	public void checkEigenvectors() {
		int boxSize = 65;
    	Complex.printBoxTextRandom(boxSize, "Check eigenvectors");
    	int colLen = cols(); 
    	MatrixComplex eigenVect = new MatrixComplex(1,colLen);
    	
    	for (int eigv = 0; eigv < roots().rows(); ++eigv) {
    		eigenVect.complexMatrix[0] = solutions().complexMatrix[eigv].clone();
    		if (eigenVect.isNull()) continue;

			/* ***************************************** */
			if (normalizeVectors)
				eigenVect = eigenVect.normalizeByRows();
			/* ***************************************** */
			/* */

			Complex eigenVal = roots().complexMatrix[eigv][0];
	    	eigenVect.println ("**************** eigenVect:");
	    	System.out.println("                 eigenVal : "+ eigenVal.toString());
	    	this.times(eigenVect.transpose()).transpose().println("aMatrix·eigenVect  "+eigv);
	    	eigenVect.times(eigenVal).println("eigval["+eigv+"]·eigenVect"+eigv);
    	}
	}
	
	public void check() {
		MatrixComplex aMatrix = new MatrixComplex();
		aMatrix.complexMatrix = this.complexMatrix.clone();

     	int boxSize = 65;
		int boxId = 5;

    	System.out.println();
       	Complex.printBoxTitle(boxId, boxSize, "EIGENVALUES & EIGENVECTORS CHECK");

		Complex.showPrecision();
		Eigenspace.version();

		Complex.printBoxText(boxId, boxSize, "Matrix definition section");
		aMatrix.println("aMatrix");
       	System.out.println("MatrixComplex :"+aMatrix.toMatrixComplex());
       	/* EigenVectors Expressions in other languages */
    	Complex.printBoxText(boxId, boxSize, "EigenVectors Expressions in other languages");
    	System.out.println("Maxima:"+this.toMaxima_eigenvalues(true));
    	System.out.println("Maxima:"+this.toMaxima_eigenvectors(true));
    	System.out.println("Maxima:"+this.toMaxima_charpoly(true));
    	System.out.println("Octave:"+this.toOctave_eigenvectors());
    	System.out.println("Wolfram:"+this.toWolfram_eigenvectors());
    	System.out.println("Wolfram:"+this.toWolfram_eigensystem());

    	Complex.printBoxText(boxId, boxSize, "Determinant, Triangle & Characteristic polynomial");
    	aMatrix.determinant().println("Determinant:");
    	aMatrix.triangle().heap().println("triangle:");
    	this.getCharactPoly().println("Characteristic polynom:");

    	/* EigenValues Calculated & Multiplicities */
    	Complex.printBoxText(boxId, boxSize, "EigenValues Calculated  & Multiplicities");
    	{
	    	for (int i = 0; i < this.eigenvalues().rows(); ++i) {
	    		Complex eVal = this.eigenvalues().getItem(i,0);
				System.out.println("EigenValue: " + eVal.toString() + 
						" - arith mult:" + this.arithmeticMultiplicity(eVal) + 
						" - geom mult:" + this.geometricMultiplicity(eVal));    		
	    	}
    	}

    	/* Characteristics Equations */
    	Complex.printBoxText(boxId, boxSize, "Characteristics Equations");
       	Complex.printBoxText(boxId, boxSize, "MATRIXCOMPLEX");
    	this.printCharactEq(outputFormat.MATRIXCOMPLEX, true);
       	Complex.printBoxText(boxId, boxSize, "MAXIMA");
    	this.printCharactEq(outputFormat.MAXIMA, true);
    	Complex.printBoxText(boxId, boxSize, "OCTAVE");
    	this.printCharactEq(outputFormat.OCTAVE, true);
    	Complex.printBoxText(boxId, boxSize, "WOLFRAM");
    	this.printCharactEq(outputFormat.WOLFRAM, true);
    	
    	/* roots & solutions */
    	Complex.printBoxText(boxId, boxSize, "Roots & Solutions ");
    	for (int row = 0; row < this.solutions().rows(); ++row){
    		System.out.println("root: " + this.root(row).getItem(0,0).toString() + " - solution: " + this.solution(row).getRow(0) + " - Is eigenvector: " + (this.solution(row).isNull() ? "No" : "Yes"));
    	}
    	
    	/* EigenVectors Calculated */
    	Complex.printBoxText(boxId, boxSize, "EigenVectors Calculated" + (Eigenspace.getNormalice() ? " Normalized" : ""));
    	this.eigenvectors().println("EigenVectors:");
    	if(!this.eigenvectors().isEmpty() && this.eigenvectors().isSquare()) System.out.println("EigenVectors - Determinant:"+this.eigenvectors().determinant());
    	else System.out.println("EigenVectors - Determinant: dosen't exist.");
   	
    	/* Check Eigenvectors */
    	this.checkEigenvectors();

		Complex.printBoxText(boxId, boxSize, "EigenVectors Base, Orthogonality & Orthonormality");
		System.out.println("Eigenvectors are a Base:" +VectorComplex.checkBase(this.solutions));
		System.out.println("Eigenvectors are a Base orthogonal:" +VectorComplex.isOrthogonal(this.solutions));
		System.out.println("Eigenvectors are a Base orthonormal:" +VectorComplex.isOrthonormal(this.solutions));
		
    	/* Diagonalize Section if possible */
     	Diagfactor diagonal = new Diagfactor(aMatrix);
    	if (diagonal.factorized()) {
        	Complex.printBoxText(boxId, boxSize, "IS DIAGONALIZABLE");
         	diagonal.D().println("Matriz Diagonal (D):");
    	    diagonal.P().transpose().println("Matriz Valores Propios Traspuesta (Pŧ):");
    	    diagonal.P().times(diagonal.D()).times(diagonal.P().inverse()).println("P·D·P⁻¹:");
    	}
    	else {
        	Complex.printBoxText(boxId, boxSize, "IS NOT!!!!!!!!!!!!!! DIAGONALIZABLE");
    	}
    	
     	/*
    	System.out.print("press any key");
    	try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
       	Complex.printBoxText(boxId, boxSize, "-- END EIGENVALUES & EIGENVECTORS TEST END --");
    	System.out.println();
	}

}
