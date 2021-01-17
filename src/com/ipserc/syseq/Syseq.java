package com.ipserc.syseq;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.complex.*;

public class Syseq extends MatrixComplex{
	private MatrixComplex partsol;	// Solution for the system
	private MatrixComplex homosol;	// Solution for the homogeneous system
	private Boolean solved = false;

	/**
	 * Instantiates an empty system of equations
	 */
	public Syseq() {
		super();
	}

	/**
	 * Instantiates the system of equations given by the string "cadena" as a complex matrix representation
	 * @param cadena the matrix with the equation system values
	 */
	public Syseq(String cadena) {
		super(cadena);
	}
	
	/**
	 * Solves the equation system as a linear combination of a particular solution and the solution of the homogeneous system
	 * @param lambda The complex value to calculate the solutions
	 */
	public void solveq(Complex lambda) {
		partsol = this.solve(lambda);
		if (this.typeEqSys() == COMPATIBLE_INDET) {
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
		case COMPATIBLE_DET: sol = partsol; break;
		case COMPATIBLE_INDET: sol =  partsol.plus(homosol.times(n)); break;
		case INCOMPATIBLE: sol = partsol; break;
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
		if (typeEqSys() == INCOMPATIBLE) {
			System.out.println("There are no solutions for an INCOMPATIBLE Equation System");
		}
		else {
			partsol.println("Particular  Solution");
			if (homosol != null) homosol.println("Homogeneous Solution");
		}
	}
}
