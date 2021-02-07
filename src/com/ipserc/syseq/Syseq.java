package com.ipserc.syseq;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.complex.*;

public class Syseq extends MatrixComplex{
	private MatrixComplex partsol;	// Solution for the system
	private MatrixComplex homosol;	// Solution for the homogeneous system
	private Boolean solved = false;

	private final static String HEADINFO = "Syseq --- INFO: ";
	private final static String VERSION = "1.1 (2021_0207_2100)";

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
		partsol = this.solve(lambda);
		if (this.typeEqSys() == INDETERMINATE) {
			homosol = this.homogeneous().solve(lambda);
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
			case DETERMINATE: sol = partsol; break;
			case INDETERMINATE: sol =  partsol.plus(homosol.times(n)); break;
			case INCONSISTENT: sol = partsol; break;
		}
		return sol;
	}
	
	/**
	 * Returns the corresponding solution as [particular sol]+[homogeneous sol]
	 * @return The solution calculated
	 */
	public MatrixComplex solution() {
		return this.solution(1);
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
	 * Prints the particular and homogeneous solutions if exists
	 */
	public void printSol() {
		if (!solved) this.solveq();
		if (typeEqSys() == INCONSISTENT) {
			System.out.println("There are no solutions for an INCONSISTENT Equation System");
		}
		else {
			partsol.println("Particular  Solution");
			if (homosol != null) homosol.println("Homogeneous Solution");
		}
	}

	/**
	 * 
	 */
	public String MatrixComplex_sysEq() {
		String toMMatrixComplex;
		toMMatrixComplex = this.preMatrixComplex();
		toMMatrixComplex = "Syseq aMatrix = new Syseq("+");";
		return toMMatrixComplex;
	}
	
	/**
	 * Returns the linsolve command for Maxima with the equations and the unknowns blocks. It can use expand if enabled.
	 * @param expand if True adds expand to the linsolve
	 * @return The linsolve command for Maxima
	 */
	public String Maxima_linsolve(boolean expand) {
		int row, col;
		//Set up equations block
		String toWolfram = "([";
		for (row = 0; row < rows(); ++ row) {
			for (col = 0; col < cols()-1; ++col) {
				toWolfram = toWolfram + "+("+getItem(row, col)+")*x"+col;
			}
			toWolfram = toWolfram + "="+getItem(row, col);
			toWolfram = toWolfram + (row < rows()-1 ? "," : "],[") ;
		}
		// replace "i" with "*%i" (imaginary term for Maxima)
		toWolfram = toWolfram.replace("i", "*%i");
		
		// Set up unknowns block
		for (col = 0; col < cols() - 2; ++col) {
			toWolfram = toWolfram + "x"+col+",";
		}
		toWolfram = toWolfram + "x"+col+"])";
		
		// Header for linsolve with expand
		toWolfram = "linsolve" + toWolfram;
		if (expand) toWolfram = "expand("+ toWolfram + ")";

		return toWolfram;
	}
	
	/**
	 * Returns the linsolve command for GNU Octave with the equations and the unknowns blocks. It can use disp if enabled.
	 * @param disp if True adds disp to the linsolve
	 * @return The linsolve command for GNU Octave
	 */
	public String Octave_linsolve(boolean disp) {
		String toOctave = "Sol = linsolve(";
		MatrixComplex A, B;
		A = this.coefMatrix();
		B = this.constMatrix();
		toOctave += (A.toOctave()+","+B.toOctave()+")");
		if (disp) toOctave = "disp(" + toOctave + ")";
		return toOctave;
	}
	
	/**
	 * Returns the linsolve command for Mathlab with the equations and the unknowns blocks. It can use disp if enabled.
	 * @param disp if True adds disp to the linsolve
	 * @return The linsolve command for Mathlab
	 */
	public String Mathlab_linsolve(boolean disp) {
		return Octave_linsolve(disp);
	}
	
	/**
	 * Returns the solve command for Wolfram with the equations and the unknowns blocks.
	 * @return The solve command for Wolfram
	 */
	public String Wolfram_solve() {
		int row, col;
		//Set up equations block
		String toWolfram = "solve(";
		for (row = 0; row < rows(); ++ row) {
			for (col = 0; col < cols()-1; ++col) {
				toWolfram = toWolfram + "+("+getItem(row, col)+")*x"+col;
			}
			toWolfram = toWolfram + "="+getItem(row, col);
			toWolfram = toWolfram + (row < rows()-1 ? "," : ",") ;
		}
		
		// Set up unknowns block
		for (col = 0; col < cols() - 2; ++col) {
			toWolfram = toWolfram + "x"+col+",";
		}
		toWolfram = toWolfram + "x"+col+")";

		return toWolfram;
	}

	/**
	 * 
	 * @param format
	 * @param display
	 */
	public void printSystemEqSolve(outputFormat format, boolean display) {
		switch (format) {
			case MATRIXCOMPLEX: System.out.println("MATRIXCOMPLEX:"+MatrixComplex_sysEq()); break;
			case MAXIMA: System.out.println("MAXIMA:"+Maxima_linsolve(display)); break;
			case OCTAVE: System.out.println("OCTAVE:"+Octave_linsolve(display)); break;
			case MATHLAB: System.out.println("MATHLAB:"+Mathlab_linsolve(display)); break;
			case WOLFRAM: System.out.println("WOLFRAM:"+Wolfram_solve()); break;				
		}
		
	}
}
