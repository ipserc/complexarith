package com.ipserc.arith.syseq;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.complex.*;

public class Syseq extends MatrixComplex {
	private MatrixComplex partsol;	// Solution for the system
	private MatrixComplex homosol;	// Solution for the homogeneous system
	private Boolean solved = false;

	private final static String HEADINFO = "Syseq --- INFO: ";
	private final static String VERSION = "1.5 (2023_0528_1700)";

	/* VERSION Release Note
	 * 
	 * 1.5 (2023_0528_1700)
	 * public Complex partSol(int n)
	 * public MatrixComplex partSol() 
	 * 
	 * 1.4 (2022_0123_0100)
	 * toWolfram_solve() --> Solve[...]
	 * toMaxima_linsolve(boolean expand) --> Added sol to store the solution: toMaxima = "sol : linsolve" + toMaxima;
	 * 
	 * 1.3 (2021_0224_1312)
	 * 
	 * 
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
	 * Instantiates an empty system of equations
	 */
	public Syseq() {
		super();
	}

	/**
	 * Instantiates the system of equations of dimensions rowLen x rowLen + 1.
	 * @param rowLen Number of rows.
	 */	
	public Syseq(int rowLen) {
		 super(rowLen, rowLen +1);
	}
	
	/**
	 * Instantiates the system of equations given by the string "cadena" as a complex matrix representation
	 * @param cadena the matrix with the equation system values
	 */
	public Syseq(String cadena) {
		super(cadena);
	}
	
	/**
	 * Instantiates the system of equations given matrix as a complex matrix representation
	 * @param matrix The matrix with the equation system values
	 */
	public Syseq(MatrixComplex matrix) {
		this.complexMatrix = matrix.complexMatrix.clone();
	}

	/*
	 * ***********************************************
	 * OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Solves the equation system as a linear combination of a particular solution and the solution of the homogeneous system
	 * @param lambda The complex value to calculate the solutions
	 */
	public void solveq(Complex lambda) {
		final boolean DEBUG_ON = false; 
		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			System.out.println(HEADINFO + Complex.repeat("# ", 40));
			System.out.println(HEADINFO + "PARTICULAR SOLUTION ");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		partsol = this.solve(lambda);

		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			partsol.println(HEADINFO + " partsol:");
			System.out.println(HEADINFO + " --- END OF --- PARTICULAR SOLUTION ");
			System.out.println(HEADINFO + Complex.repeat("# ", 40));
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		if (this.typeEqSys() == INDETERMINATE) {
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				System.out.println(HEADINFO + Complex.repeat("# ", 40));
				System.out.println(HEADINFO + "HOMOGENOUS SOLUTION ");
			}
			/* ------------- END DEBUGGING BLOCK ------------- */

			if (this.isHomogeneous()) homosol = partsol;
			else homosol = this.unkMatrix().kernel(lambda);
			
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				homosol.println(HEADINFO + " homosol:");
				System.out.println(HEADINFO + " --- END OF --- HOMOGENEOUS SOLUTION ");
				System.out.println(HEADINFO + Complex.repeat("# ", 40));
			}
			/* ------------- END DEBUGGING BLOCK ------------- */
		}
		solved = true;
	}

	/**
	 * Solves the equation system as a linear combination of a particular solution and the solution of the homogeneous system with lamda = 1
	 */
	public void solveq() {
		this.solveq(Complex.ONE);
	}

	/**
	 * Returns the corresponding solution as [particular sol]+n*[homogeneous sol]
	 * @param n The value of the n 
	 * @return The solution calculated
	 */
	public MatrixComplex solution(double n) {
		MatrixComplex sol = new MatrixComplex();
		
		if (!solved) this.solveq();
		switch (this.typeEqSys()) {
			case DETERMINATE:	sol = partsol; break;
			case INDETERMINATE:	if (this.isHomogeneous()) sol = partsol.times(n); 
								else sol = partsol.plus(homosol.times(n));
								break;
			case INCONSISTENT:	sol = partsol; break;
		}
		return sol;
	}
	
	/**
	 * Returns the nth component of the particular solution if there is any
	 * @param n the nth component
	 * @return Returns the nth component of the particular solution
	 */
	public Complex partSol(int n) {
		if (partsol.isEmpty()) return new Complex();
		return this.partsol.getItem(0, n);
	}
	
	/**
	 * Returns the particular solution 
	 * @return the particular solution
	 */
	public MatrixComplex partSol() {
		return partsol;
	}
	
	/**
	 * Returns the corresponding solution as [particular sol]+[homogeneous sol]
	 * @return The solution calculated
	 */
	public MatrixComplex solution() {
		return this.solution(1);
	}
	
	/**
	 * Checks the solution putting that solution in the equation system and operating 
	 * If the check operation returns a row with all zeros, the solution is correct
	 */
	public void checkSol(MatrixComplex solution) {
		MatrixComplex indTerm = this.indMatrix().transpose();
		MatrixComplex uknMatix = this.unkMatrix().transpose();
		MatrixComplex unitMatrix = new MatrixComplex(solution.rows(),1);
		unitMatrix.initMatrix(1, 0);
		
		solution.println                                                 ("Solution                                      ");
		solution.times(uknMatix).minus(unitMatrix.times(indTerm)).println("Check: solution.times(uknMatix).minus(indTerm)");
	}

	/*
	 * ***********************************************
	 * FUNCTIONS
	 * ***********************************************
	 */

	/**
	 * Checks if the unknown coefficient matrix is symmetric or not
	 * @return True if the matrix is symmetric
	 */
	public boolean isSymmetric() {
		return this.unkMatrix().isSymmetric();
	}

	/**
	 * Checks if the unknown coefficient matrix is antisymmetric or not
	 * @return True if the matrix is antisymmetric
	 */
	public boolean isAntiSymmetric() {
		return this.unkMatrix().isAntiSymmetric();
	}

	/**
	 * Checks if the unknown coefficient matrix is skew-symmetric or not
	 * @return True if the matrix is skew-symmetric
	 */
	public boolean isSkewSymmetric() {
		return this.unkMatrix().isSkewSymmetric();
	}
	
	/**
	 * Checks if the unknown coefficient matrix is hermitian or not
	 * @return True if the matrix is hermitian
	 */
	public boolean isHermitian() {
		return this.unkMatrix().isHermitian();
	}
	
	/**
	 * Checks if the unknown coefficient matrix is antihermitian or not
	 * @return True if the matrix is antihermitian
	 */
	public boolean isAntiHermitian() {
		return this.unkMatrix().isAntiHermitian();		
	}
	
	/**
	 * Checks if the unknown coefficient matrix is skewhermitian (antihermitian) or not
	 * @return True if the matrix is skewhermitian
	 */
	public boolean isSkewHermitian() {
		return isAntiHermitian();
	}
	
	/*
	 * ***********************************************
	 * PRINTING
	 * ***********************************************
	 */

	/**
	 * Prints the equation system as N(0) x0 + N(1) x1 + ... N(n) xn = N(n+1)
	 * @param Title The title
	 */
	public void print(String Title) {
		System.out.println(Title);
		for (int row = 0; row < rows(); ++ row) {
			int col = 0;
			for (; col < cols() - 1; ++col) {
				System.out.print("+("+getItem(row, col)+")x"+col);
			}
			System.out.println(" = "+getItem(row, col));
		}
		printTypeEqSys();
	}

	/**
	 * Prints the equation system as N(0) x0 + N(1) x1 + ... N(n) xn = N(n+1)
	 */
	public void print() {
		this.print("");
	}
	
	/**
	 * Prints the particular and homogeneous solutions if they exist
	 * If the solution hasn't be calculated it calculates the solution before printing
	 */
	public void printSol(String title) {
		System.out.println(title);
		if (!solved) this.solveq();
		if (typeEqSys() == INCONSISTENT) {
			System.out.println(HEADINFO + ":There are no solutions for an INCONSISTENT Equation System");
		}
		else {
			partsol.println("Particular  Solution");
			if (homosol != null && !isHomogeneous()) homosol.println("Homogeneous Solution");
		}
	}

	/**
	 * Returns the "new Syseq" instruction for the matrix
	 */
	public String MatrixComplex_sysEq() {
		String toMMatrixComplex;
		toMMatrixComplex = this.preMatrixComplex();
		toMMatrixComplex = "Syseq aMatrix = new Syseq("+ toMMatrixComplex +");";
		return toMMatrixComplex;
	}
	
	/**
	 * Returns the linsolve command for Maxima with the equations and the unknowns blocks. It can use expand if enabled.
	 * @param expand if True adds expand to the linsolve
	 * @return The linsolve command for Maxima
	 */
	public String toMaxima_linsolve(boolean expand) {
		int row, col;
		//Set up equations block
		String toMaxima = "([";
		for (row = 0; row < rows(); ++ row) {
			for (col = 0; col < cols()-1; ++col) {
				toMaxima = toMaxima + "+("+getItem(row, col)+")*x"+col;
			}
			toMaxima = toMaxima + "="+getItem(row, col);
			toMaxima = toMaxima + (row < rows()-1 ? "," : "],[") ;
		}
		// replace "i" with "*%i" (imaginary term for Maxima)
		toMaxima = toMaxima.replace("i", "*%i");
		
		// Set up unknowns block
		for (col = 0; col < cols() - 2; ++col) {
			toMaxima = toMaxima + "x"+col+",";
		}
		toMaxima = toMaxima + "x"+col+"])";
		
		// Header for linsolve with expand
		toMaxima = "sol : linsolve" + toMaxima;
		if (expand) toMaxima = "expand("+ toMaxima + ")";

		return toMaxima;
	}
	
	/**
	 * Returns the linsolve command for GNU Octave with the equations and the unknowns blocks. It can use disp if enabled.
	 * @param disp if True adds disp to the linsolve
	 * @return The linsolve command for GNU Octave
	 */
	public String toOctave_linsolve(boolean disp) {
		String toOctave = "Sol = linsolve(";
		MatrixComplex A, B;
		A = this.coefMatrix();
		B = this.constMatrix();
		toOctave += (A.toOctave()+","+B.toOctave()+")");
		if (disp) toOctave = "disp(" + toOctave + ")";
		return toOctave;
	}
	
	/**
	 * Returns the linsolve command for Matlab with the equations and the unknowns blocks. It can use disp if enabled.
	 * @param disp if True adds disp to the linsolve
	 * @return The linsolve command for Matlab
	 */
	public String toMatlab_linsolve(boolean disp) {
		return toOctave_linsolve(disp);
	}
	
	/**
	 * Returns the solve command for Wolfram with the equations and the unknowns blocks.
	 * @return The solve command for Wolfram
	 */
	public String toWolfram_solve() {
		int row, col;
		//Set up equations block
		String toWolfram = "Solve[";
		for (row = 0; row < rows(); ++ row) {
			for (col = 0; col < cols()-1; ++col) {
				toWolfram = toWolfram + "+("+getItem(row, col)+")*x"+col;
			}
			toWolfram = toWolfram + "="+getItem(row, col);
			toWolfram = toWolfram + (row < rows()-1 ? "," : ",") ;
		}
		
		// Set up unknowns block
		toWolfram = toWolfram + "{";
		for (col = 0; col < cols() - 2; ++col) {
			toWolfram = toWolfram + "x"+col+",";
		}
		toWolfram = toWolfram + "x"+col+"}]";

		toWolfram = toWolfram.replace("i", "I");
		return toWolfram;
	}

	/**
	 * Prints the system equations solve command for MATRIXCOMPLEX, MAXIMA, OCTAVE, MATLAB, WOLFRAM
	 * @param format Onf the allowed fprmats (MATRIXCOMPLEX, MAXIMA, OCTAVE, MATLAB, WOLFRAM)
	 * @param display If true adds "disp" or "expand" to the solve instruction if needed
	 */
	public void printSystemEqSolve(outputFormat format, boolean display) {
		switch (format) {
			case MATRIXCOMPLEX: System.out.println("MATRIXCOMPLEX:"+MatrixComplex_sysEq()); break;
			case MAXIMA: System.out.println("MAXIMA:"+toMaxima_linsolve(display)); break;
			case OCTAVE: System.out.println("OCTAVE:"+toOctave_linsolve(display)); break;
			case MATLAB: System.out.println("MATLAB:"+toMatlab_linsolve(display)); break;
			case WOLFRAM: System.out.println("WOLFRAM:"+toWolfram_solve()); break;				
		}
		
	}
}
