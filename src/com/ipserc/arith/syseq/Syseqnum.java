package com.ipserc.arith.syseq;

import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.complex.*;

public class Syseqnum extends MatrixComplex {
	private boolean solved = false;
	private MatrixComplex indTerm;
	private MatrixComplex unkMatrix;
	private int numIters = -1;
	
	private final static String HEADINFO = "Syseqnum --- INFO: ";
	private final static String VERSION = "1.4 (2026_0807_1700)";
	/* VERSION Release Note
	 * 	Syseqnum: System Equation Solver by Numerical Methods
	 *
	 * 1.4 (2026_0807_1700)
	 * genminres(): era un esqueleto sin terminar -- calculaba vectores de Arnoldi y una matriz de
	 * Hessenberg pero nunca actualizaba xi, asi que devolvia siempre initGuess sin tocar (confirmado
	 * en vivo con ScratchGenminresProbe01.java antes de tocar codigo). Reescrito como GMRES(m) con
	 * reinicio completo: base de Krylov guardada en V[], h dimensionada (kry+1)xkry (antes n x n),
	 * producto interno hermitico (V[i].adjoint(), antes v[i]^T sin conjugar -- incorrecto para
	 * matrices genuinamente complejas), resolucion del minimo cuadrado sobre la Hessenberg via
	 * rotaciones de Givens complejas + sustitucion hacia atras, actualizacion xi+=V*y y chequeo de
	 * convergencia por reinicio. Tambien: el paso interno de Arnoldi aplicaba el patron de residuo
	 * (b-A*v) en vez de aplicar el operador (A*v) -- corregido. Se quita el guard isSymmetric()
	 * (copiado de congrad(), con el aviso al reves: GMRES no requiere matriz simetrica, para eso
	 * existe) -- ahora triangulariza siempre, igual que jacobi()/gaussSeidel()/sucor()/symsucor().
	 * DEBUG_ON pasa a false y los println sueltos quedan condicionados, como en el resto de la clase.
	 *
	 * 1.3 (2026_0807_1500)
	 * Syseqnum(MatrixComplex matrix) y clone(): complexMatrix.clone() era un clon superficial
	 * de Java (fila compartida con la matriz original) en vez de una copia profunda -- mismo
	 * patron de aliasing ya arreglado repetidas veces en factorization/ esta sesion. Ahora
	 * ambos usan copy().complexMatrix. Confirmado con ScratchSyseqnumAlias01.java
	 * (src/TestComplex/): mutar el Syseqnum o su clon ya no corrompe la matriz original.
	 *
	 * 1.2 (2023_0527_1900)
	 * 
	 * 	public MatrixComplex congrad(Complex initGuess, int iterations)
	 * 		if (checkConvergence(xi.transpose(), xi1.transpose(), Complex.zero_treshold_r())) {
	 * 		--> Replaced with Complex.zero_threshold_approx())) {
	 * 
	 * 1.1 (2021_1112_2300)
	 * 
	 * BOOK
	 * 		Templates for the Solution of Linear Systems:Building Blocks for Iterative Methods 1
	 * AUTHORS
	 * 		Richard Barrett 2 ,Michael Berry 3 , Tony F. Chan 4 ,
	 * 		James Demmel 5 , June M. Donato 6 , Jack Dongarra 3 ; 6 ,
	 * 		Victor Eijkhout 4 , Roldan Pozo 7 Charles Romine 6 ,
	 * 		and Henk Van der Vorst 8
	 * URL  
	 * 		http://www.netlib.org/templates/index.html
	 * 		http://www.netlib.org/templates/templates.pdf
	 * 	Other Sources:
	 * 		https://en.1lib.limited/book/1232295/c3c1bc
	 * 		https://www.academia.edu/35818330/Templates_for_the_Solution_of_Linear_Systems_Building_Blocks_for_Iterative_Methods
	 * 
	 * PERSONAL PAGES
	 * 		Jack Dongarra
	 * 			https://www.eecs.utk.edu/people/jack-dongarra/
	 * 			http://www.netlib.org/utk/people/JackDongarra/
	 * 			E-mail: dongarra@utk.edu
	 * 
	 * An implementation of the methods described on this book in Java for fun
	 * 
	 * STATIONARY ITERATIVE METHODS
	 * - Jacobi: jacobi
	 * - Gauss-Seidel: gaussSeidel
	 * - Successive Over-Relaxation: sucor
	 * - Symmetric Successive Over-Relaxation: symsucor
	 * 
	 * NONSTATIONARY ITERATIVE METHODS
	 * - Conjugate Gradient Method: congrad
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
	public Syseqnum() {
		super();
	}

	/**
	 * Instantiates the system of equations of dimensions rowLen x rowLen + 1.
	 * @param rowLen Number of rows.
	 */	
	public Syseqnum(int rowLen) {
		 super(rowLen, rowLen +1);
	}
	
	/**
	 * Instantiates the system of equations given by the string "cadena" as a complex matrix representation
	 * @param cadena the matrix with the equation system values
	 */
	public Syseqnum(String cadena) {
		super(cadena);
	}
	
	/**
	 * Instantiates the system of equations given matrix as a complex matrix representation
	 * @param matrix The matrix with the equation system values
	 */
	public Syseqnum(MatrixComplex matrix) {
		this.complexMatrix = matrix.copy().complexMatrix;
	}

	/*
	 * ***********************************************
	 * MEMBER VARS ACCESS: GETTERS
	 * ***********************************************
	 */
	/**
	 * Flag to indicate that on solution of the equation system has been found
	 * @return True if a solution has been found, False otherwise
	 */
	public boolean isSolved() {
		return solved;
	}

	/**
	 * Gets the number of iterations taken to find the solution
	 * @return The number of iterations
	 */
	public int getIterations() {
		return numIters;
	}

	/*
	 * ***********************************************
	 * CLASS METHODS
	 * ***********************************************
	 */
	public Syseqnum clone() {
		Syseqnum newSyseqnum = new Syseqnum();
		newSyseqnum.solved = solved;		
		newSyseqnum.complexMatrix = this.copy().complexMatrix;
		return newSyseqnum;
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
	 * OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Checks the convergence of the solution by comparing the variation between the previous solution and the current one
	 * If there is no variation in more decimals than the indicates by numDecs the process has found one solution
	 * @param solution1 The 1st solution calculated
	 * @param solution2 The 2nd solution calculated
	 * @param numDecs The number of decimals to observe 
	 * @return True if the variation for the number of decimals is null, false otherwise
	 */
	public boolean checkConvergence(MatrixComplex solution1, MatrixComplex solution2 , int numDecs) {
		return solution1.equals(solution2, numDecs);
	}

	/**
	 * Checks the convergence of the solution by comparing the norm of the difference of the previous solution and the current one with the threshold
	 * If the norm is less than the precision the process has found one solution
	 * @param solution1 The 1st solution calculated
	 * @param solution2 The 2nd solution calculated
	 * @param precision The precision threshold 
	 * @return True if the variation for the number of decimals is null, false otherwise
	 */
	public boolean checkConvergence(MatrixComplex solution1, MatrixComplex solution2 , double precision) {
		return solution1.minus(solution2).inf_norm() < precision ? true : false;
	}

	/**
	 * Checks the convergence of the solution by probing it with the equation system
	 * If the solution gives the independent terms with numDecs of precision a solution has been found
	 * This method is computational costly, do not use 
	 * @param solution The solution to check
	 * @param numDecs The number of decimals to observe 
	 * @return True if the variation of the independent terms calculated for the number of decimals is null, false otherwise 
	 */
	public boolean checkConvergence(MatrixComplex solution, int numDecs) {
		final boolean DEBUG_ON = false; 

		MatrixComplex nullMatrix = new MatrixComplex(this.rows(),1);
		MatrixComplex unitMatrix = new MatrixComplex(solution.rows(),1);
		
		unitMatrix.initMatrix(1, 0);
		nullMatrix.initMatrix(0, 0);
		
		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			MatrixComplex checkResult = solution.times(unkMatrix.transpose()).minus(unitMatrix.times(indTerm.transpose()));
			checkResult.println(HEADINFO + "checkResult:");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		if (solution.times(unkMatrix.transpose()).minus(unitMatrix.times(indTerm.transpose())).transpose().equals(nullMatrix, numDecs)) return true;
		else return false;
	}

	/**
	 * Checks the solution putting that solution in the equation system and operating 
	 * If the check operation returns a row with all zeros, the solution is correct
	 * @param solution The solution to check with the Equation System
	 */
	public void checkSol(MatrixComplex solution) {
		MatrixComplex unitMatrix = new MatrixComplex(solution.rows(),1);
		unitMatrix.initMatrix(1, 0);
		
		solution.println                                                 ("Solution                                      ");
		solution.times(unkMatrix.transpose()).minus(unitMatrix.times(indTerm.transpose())).println("Check: solution.times(unkMatrix).minus(indTerm)");
	}

	/*
	 * * STATIONARY ITERATIVE METHODS
	 * 	- Jacobi: jacobi
	 * 	- Gauss-Seidel: gaussSeidel
	 * 	- Successive Over-Relaxation: sucor
	 */

	/**
	 * 
	 * @param solutionI
	 * @param iterations
	 * @return
	 */
	public MatrixComplex jacobi(MatrixComplex solutionI, int iterations) {
		final boolean DEBUG_ON = false;
		int k;
		solved = false;

		MatrixComplex syseqTriang = this.copy();
		syseqTriang = syseqTriang.triangleUp();	// Required to find a solution almost always, I don't know why. It should be studied later.
		indTerm = syseqTriang.indMatrix();
		unkMatrix = syseqTriang.unkMatrix();

		MatrixComplex solutionK = new MatrixComplex(1, unkMatrix.rows()); // Solution k

		solutionK.initMatrix(0, 0);
		//solutionI.initMatrix(0, 0);
		//solutionK.initMatrix(initGuess.rep(), initGuess.imp());

		for(k = 0; k < iterations; ++k) { // FOREVER
			for (int i = 0; i < unkMatrix.rows(); ++i) {
				solutionK.setItem(0, i, Complex.ZERO);
				for (int j = 0; j < unkMatrix.rows(); ++j) {
					if (j == i) continue;
					solutionK.setItem(0, i, solutionK.getItem(0, i).plus(unkMatrix.getItem(i, j).times(solutionI.getItem(0, j)))); 
				}
				solutionK.setItem(0, i, (indTerm.getItem(i, 0).minus(solutionK.getItem(0, i))).divides(unkMatrix.getItem(i, i)));
			}
			if (checkConvergence(solutionK, solutionI, Complex.precision())) {
			//if (checkConvergence(solutionK, solutionI, Complex.getMaxDecimals())) {
			// COSTLY ALTERNATIVE if (checkConvergence(solutionK, Complex.getMaxDecimals())) {
				/* -------------   DEBUGGING BLOCK   ------------- */
				if (DEBUG_ON) {
					System.out.println(HEADINFO + "Jacobi Iteration:" + k);
				}
				/* ------------- END DEBUGGING BLOCK ------------- */
				numIters = k+1;
				solved = true;
				return solutionK;
			}
			solutionI = solutionK.copy();
		}
		numIters = k;
		return solutionK;				
	}

	/**
	 * Jacobi
	 * @param initGuess
	 * @param iterations
	 * @return
	 */
	public MatrixComplex jacobi(Complex initGuess, int iterations) {
		MatrixComplex solutionI = new MatrixComplex(1, this.rows()); // Solution k - 1

		solutionI.initMatrix(initGuess.rep(), initGuess.imp());
		return jacobi(solutionI, iterations);
	}
	
	/**
	 * Gauss-Seidel
	 * @param initGuess
	 * @param iterations
	 * @return
	 */
	public MatrixComplex gaussSeidel(Complex initGuess, int iterations) {
		final boolean DEBUG_ON = false;
		int k;
		solved = false;

		MatrixComplex syseqTriang = this.copy();
		syseqTriang = syseqTriang.triangleUp();	// Required to find a solution almost always, I don't know why. It should be studied later.
		indTerm = syseqTriang.indMatrix();
		unkMatrix = syseqTriang.unkMatrix();
		
		MatrixComplex solutionI = new MatrixComplex(1, unkMatrix.rows()); // Solution k - 1
		MatrixComplex solutionK = new MatrixComplex(1, unkMatrix.rows()); // Solution k
		Complex sigma = new Complex();

		solutionI.initMatrix(initGuess.rep(), initGuess.imp());
		//solutionK.initMatrix(0, 0);
		//solutionI.initMatrix(0, 0);
		solutionK.initMatrix(initGuess.rep(), initGuess.imp());

		for(k = 0; k < iterations; ++k) {
			for (int i = 0; i < unkMatrix.rows(); ++i) {
				sigma.setComplexPol(0, 0);
				for (int j = 0; j < i-1; ++j) {
					sigma = sigma.plus(unkMatrix.getItem(i, j).times(solutionK.getItem(0, j)));
				}
				for (int j = i+1; j < unkMatrix.rows(); ++j) {
					sigma = sigma.plus(unkMatrix.getItem(i, j).times(solutionI.getItem(0, j))); 
				}
				solutionK.setItem(0, i, (indTerm.getItem(i, 0).minus(sigma)).divides(unkMatrix.getItem(i, i)));
			}
			if (checkConvergence(solutionK, solutionI, Complex.precision())) {
			// if (checkConvergence(solutionK, solutionI, Complex.getMaxDecimals())) {
			// COSTLY ALTERNATIVE if (checkConvergence(solutionK, Complex.getMaxDecimals())) {
				/* -------------   DEBUGGING BLOCK   ------------- */
				if (DEBUG_ON) {
					System.out.println(HEADINFO + "gaussSeidel Iteration:" + (k+1));
					solutionK.println(HEADINFO + "gaussSeidel Solution (k):");
				}
				/* ------------- END DEBUGGING BLOCK ------------- */
				if (!checkConvergence(solutionK, Complex.getMaxDecimals())) {
					if (k >= iterations) {
						numIters = k;
						return solutionK;
					}
					else continue;
				}
				numIters = k+1;
				solved = true;
				return solutionK;
			}
		solutionI = solutionK.copy();
		}
		numIters = k;
		return solutionK;
	}

	/**
	 * Successive Over-Relaxation
	 * @param initGuess
	 * @param omega
	 * @param iterations
	 * @return
	 */
	public MatrixComplex sucor(Complex initGuess, double omega, int iterations) {
		final boolean DEBUG_ON = false;
		int k;
		solved = false;

		MatrixComplex syseqTriang = this.copy();
		syseqTriang = syseqTriang.triangleUp();	// Required to find a solution almost always, I don't know why. It should be studied later.
		indTerm = syseqTriang.indMatrix();
		unkMatrix = syseqTriang.unkMatrix();
		
		MatrixComplex solutionI = new MatrixComplex(1, unkMatrix.rows()); // Solution k - 1
		MatrixComplex solutionK = new MatrixComplex(1, unkMatrix.rows()); // Solution k
		Complex sigma = new Complex(); // Partial Solution sigma
		Complex comega = new Complex(omega, 0);

		solutionI.initMatrix(initGuess.rep(), initGuess.imp());
		//solutionK.initMatrix(0, 0);
		//solutionI.initMatrix(0, 0);
		solutionK.initMatrix(initGuess.rep(), initGuess.imp());

		for(k = 0; k < iterations; ++k) {
			for (int i = 0; i < unkMatrix.rows(); ++i) {
				sigma.setComplexPol(0, 0);
				for (int j = 0; j < i-1; ++j) {
					sigma = sigma.plus(unkMatrix.getItem(i, j).times(solutionK.getItem(0, j)));
				}
				for (int j = i+1; j < unkMatrix.rows(); ++j) {
					sigma = sigma.plus(unkMatrix.getItem(i, j).times(solutionI.getItem(0, j))); 
				}
				sigma = (indTerm.getItem(i, 0).minus(sigma)).divides(unkMatrix.getItem(i, i));
				solutionK.setItem(0, i, solutionI.getItem(0, i).plus(comega.times(sigma.minus(solutionI.getItem(0, i)))));
			}
			if (checkConvergence(solutionK, solutionI, Complex.precision())) {
			// if (checkConvergence(solutionK, solutionI, Complex.getMaxDecimals())) {
			// COSTLY ALTERNATIVE if (checkConvergence(solutionK, Complex.getMaxDecimals())) {
				/* -------------   DEBUGGING BLOCK   ------------- */
				if (DEBUG_ON) {
					System.out.println(HEADINFO + "sucor Iteration:" + (k+1));
				}
				/* ------------- END DEBUGGING BLOCK ------------- */
				if (!checkConvergence(solutionK, Complex.getMaxDecimals())) {
					if (k >= iterations) {
						numIters = k;
						return solutionK;
					}
					else continue;
				}
				numIters = k+1;
				solved = true;
				return solutionK;
			}
		solutionI = solutionK.copy();
		}
		numIters = k;
		return solutionK;
	}

	/**
	 * Symmetric Successive Over-Relaxation: symsucor
	 * @param initGuess
	 * @param omega
	 * @param iterations
	 * @return
	 */
	public MatrixComplex symsucor(Complex initGuess, double omega, int iterations) {
		final boolean DEBUG_ON = false;
		int k = -1;
		solved = false;

		/* IT IS NOT TRUE
		 * syseqTriang.triangleUp() fix it
		 *
		if (!this.unkMatrix().isSimmetryc()) {
			System.out.println(HEADINFO + "WARNING: Symmetric Successive Over-Relaxation (symsucor) is intended for SYMMETRIC matrices. The matrix you're using is not symmetric.");
		}
		/* IT IS NOT TRUE */

		MatrixComplex syseqTriang = this.copy();
		syseqTriang = syseqTriang.triangleUp();	// Required to find a solution almost always, I don't know why. It should be studied later.
		indTerm = syseqTriang.indMatrix();
		unkMatrix = syseqTriang.unkMatrix();
		
		MatrixComplex solutionI = new MatrixComplex(1, unkMatrix.rows()); // Solution k - 1
		MatrixComplex solution2 = new MatrixComplex(1, unkMatrix.rows()); // Solution k - 1/2
		MatrixComplex solutionK = new MatrixComplex(1, unkMatrix.rows()); // Solution k
		Complex sigma = new Complex(); 
		Complex comega = new Complex(omega, 0);

		solutionI.initMatrix(initGuess.rep(), initGuess.imp());
		solution2.initMatrix(initGuess.rep(), initGuess.imp());
		//solutionK.initMatrix(0, 0);
		//solutionI.initMatrix(0, 0);
		solutionK.initMatrix(initGuess.rep(), initGuess.imp());

		for(k = 0; k < iterations; ++k) {
			for (int i = 0; i < unkMatrix.rows(); ++i) {
				sigma.setComplexPol(0, 0);
				for (int j = 0; j < i; ++j) {
					sigma = sigma.plus(unkMatrix.getItem(i, j).times(solution2.getItem(0, j)));
				}
				for (int j = i+1; j < unkMatrix.rows(); ++j) {
					sigma = sigma.plus(unkMatrix.getItem(i, j).times(solutionI.getItem(0, j))); 
				}
				sigma = (indTerm.getItem(i, 0).minus(sigma)).divides(unkMatrix.getItem(i, i));
				solution2.setItem(0, i, solutionI.getItem(0, i).plus(comega.times(sigma.minus(solutionI.getItem(0, i)))));
			}
			
			for (int i = unkMatrix.rows()-1; i > -1 ; --i) {
				sigma.setComplexPol(0, 0);
				for (int j = 0; j < i; ++j) {
					sigma = sigma.plus(unkMatrix.getItem(i, j).times(solution2.getItem(0, j)));
				}
				for (int j = i+1; j < unkMatrix.rows(); ++j) {
					sigma = sigma.plus(unkMatrix.getItem(i, j).times(solutionK.getItem(0, j))); 
				}
				/**** BOOK CORRECTION ****/
				sigma = (indTerm.getItem(i, 0).minus(sigma)).divides(unkMatrix.getItem(i, i));
				/**** BOOK CORRECTION ****/
				solutionK.setItem(0, i, solution2.getItem(0, i).plus(comega.times(sigma.minus(solution2.getItem(0, i)))));
			}
			
			if (checkConvergence(solutionK, solutionI, Complex.precision())) {
			// if (checkConvergence(solutionK, solutionI, Complex.getMaxDecimals())) {
			// COSTLY ALTERNATIVE if (checkConvergence(solutionK, Complex.getMaxDecimals())) {
				//* -------------   DEBUGGING BLOCK   ------------- *
				if (DEBUG_ON) {
					System.out.println(HEADINFO + "symsucor Iteration:" + (k+1));
				}
				//* ------------- END DEBUGGING BLOCK ------------- *
				if (!checkConvergence(solutionK, Complex.getMaxDecimals())) {
					if (k >= iterations) {
						numIters = k;
						return solutionK;
					}
					else continue;
				}
				numIters = k+1;
				solved = true;
				return solutionK;
			}
			solutionI = solution2.copy();
			solution2 = solutionK.copy();
		}
		numIters = k;
		return solutionK;
	}
	
	/*
	 * * NONSTATIONARY ITERATIVE METHODS
	 * 	- Conjugate Gradient Method: congrad
	 * 	- Generalized Minimal Residual: genminres
	 * 	- 
	 */

	/**
	 * Conjugate Gradient Method: congrad
	 * @param initGuess
	 * @param iterations
	 * @return
	 */
	public MatrixComplex congrad(Complex initGuess, int iterations) {
		final boolean DEBUG_ON = false;
		int k = -1;
		int offset = 0; // Extra iterations
		solved = false;

		if (!this.isSymmetric()) {
			System.out.println(HEADINFO + "WARNING: Conjugate Gradient Method (congrad) is intended for SYMMETRIC matrices. The matrix you're using is not symmetric.");
			System.out.println(HEADINFO + "WARNING: Trying with the upper triangle version of the original matrix");
			MatrixComplex syseqTriang = this.copy();
			syseqTriang = syseqTriang.triangleUp();	// Required to find a solution almost always, I don't know why. It should be studied later.
			indTerm = syseqTriang.indMatrix();
			unkMatrix = syseqTriang.unkMatrix();

		} else {
			indTerm = this.indMatrix();
			unkMatrix = this.unkMatrix();
		}
		
		MatrixComplex xi = new MatrixComplex(unkMatrix.rows(), 1); // x[i]
		MatrixComplex xi1 = new MatrixComplex(unkMatrix.rows(), 1); // x[i-1]
		MatrixComplex ri; // = new MatrixComplex(1, unkMatrix.rows()); // r[i]
		MatrixComplex ri1; // = new MatrixComplex(1, unkMatrix.rows()); // r[i-1]
		MatrixComplex zi1; // = new MatrixComplex(1, unkMatrix.rows()); // z[i-1]
		MatrixComplex pi  = new MatrixComplex(unkMatrix.rows(), 1); // p[i]
		MatrixComplex qi; // = new MatrixComplex(1, unkMatrix.rows()); // q[i]
		xi.initMatrix(initGuess.rep(), initGuess.imp());
		xi1.initMatrix(initGuess.rep(), initGuess.imp());		
		ri1 = indTerm.minus(unkMatrix.times(xi));

		MatrixComplex M = new MatrixComplex(unkMatrix.rows()).eye(); // M starts as I, the identity matrix

		MatrixComplex ro1; // = new MatrixComplex(1); // ro i-1
		MatrixComplex ro2 = new MatrixComplex(1); // ro i-2
		MatrixComplex alpha; // = new MatrixComplex(1); 
		
		for (k = 0; k < iterations; ++k) {
			Syseqnum Mri1 = new Syseqnum(M.augment(ri1));			//ri1.println(HEADINFO + "ri1");
			zi1 = (Mri1.jacobi(initGuess, iterations)).transpose();	//zi1.println(HEADINFO + "zi1");
			offset += Mri1.getIterations();
			ro1 = ri1.transpose().times(zi1);						//ro1.println(HEADINFO + "ro1");
			/**** BOOK CORRECTION to add in pseudocode ****/
			if (ro1.isNullC()) {
				//* -------------   DEBUGGING BLOCK   ------------- *
				if (DEBUG_ON) {
					System.out.println(HEADINFO + "Terminating reason is: ro1 is Null");
					System.out.println(HEADINFO + "congrad Iteration:" + (k+1));
				}
				//* ------------- END DEBUGGING BLOCK ------------- *				
				numIters = k+1+offset;
				solved = true;
				return xi.transpose();				
			}
			/**** BOOK CORRECTION ****/

			if (k == 0) {
				pi = zi1.copy();
			}
			else {
				/**** BOOK CORRECTION in pseudocode ****/
				// p(i)=z(i-1)+beta(i-1)*p(i-1)
				//pi = zi1.plus((ro1.divides(ro2)).times(pi));
				/**** BOOK CORRECTION Errata March 7, 2006 ****/
				// Correction p(i)=r(i-1)+beta(i-1)*p(i-1)
				pi = ri1.plus((ro1.divides(ro2)).times(pi));
			}
															//pi.println(HEADINFO + "pi");
			qi = unkMatrix.times(pi);						//qi.println(HEADINFO + "qi");
			alpha = ro1.divides(pi.transpose().times(qi));	//alpha.println(HEADINFO + "alpha");
			xi = xi1.plus(alpha.times(pi)); 				//xi.println(HEADINFO + "xi");	xi1.println(HEADINFO + "xi1");
			ri = ri1.minus(alpha.times(qi));				//ri.println(HEADINFO + "ri");
			/**** BOOK CORRECTION to add in pseudocode ****/
			if (ri.isNullC()) {
				//* -------------   DEBUGGING BLOCK   ------------- *
				if (DEBUG_ON) {
					System.out.println(HEADINFO + "Terminating reason is: ri is Null");
					System.out.println(HEADINFO + "congrad Iteration:" + (k+1));
				}
				//* ------------- END DEBUGGING BLOCK ------------- *
				numIters = k+1+offset;
				solved = true;
				return xi.transpose();				
			}
			/**** BOOK CORRECTION ****/

			// Here we need to use the reduced threshold comparison for identifying the zero
			if (checkConvergence(xi.transpose(), xi1.transpose(), Complex.zero_threshold_approx())) {
				//* -------------   DEBUGGING BLOCK   ------------- *
				if (DEBUG_ON) {
					System.out.println(HEADINFO + "congrad Iteration:" + (k+1));
				}
				//* ------------- END DEBUGGING BLOCK ------------- *
				if (!checkConvergence(xi.transpose(), Complex.getMaxDecimals())) {
					if (k >= iterations) {
						numIters = k+offset;
						return xi.transpose();
					}
					else continue;
				}
				numIters = k+1+offset;
				solved = true;
				return xi.transpose();
			}
			xi1 = xi.copy();
			ri1 = ri.copy();
			ro2 = ro1.copy();
		}
		numIters = k+offset;
		return xi.transpose();
	}
	
	/**
	 * Generalized Minimal Residual: genminres
	 * Restarted GMRES(m): builds an orthonormal Krylov basis of dimension up to "m" via the
	 * Arnoldi process (modified Gram-Schmidt, Hermitian inner product), solves the resulting
	 * (usedDim+1) x usedDim upper-Hessenberg least-squares problem with complex Givens rotations,
	 * and updates the solution. If a cycle doesn't converge, restarts the Arnoldi process from the
	 * updated solution, up to "iterations" cycles.
	 * @param initGuess The initial guess, broadcast to every component of the solution vector
	 * @param m The Krylov subspace dimension used on each restart cycle (clamped internally to the system size)
	 * @param iterations The maximum number of restart cycles
	 * @return The solution found, or the best approximation reached if "iterations" is exhausted first
	 */
	public MatrixComplex genminres(Complex initGuess, int m, int iterations) {
		final boolean DEBUG_ON = false;
		int k;
		int offset = 0; // Extra iterations spent inside the (identity) preconditioner solves
		solved = false;

		MatrixComplex syseqTriang = this.copy();
		syseqTriang = syseqTriang.triangleUp();	// Required to find a solution almost always, I don't know why. It should be studied later.
		indTerm = syseqTriang.indMatrix();
		unkMatrix = syseqTriang.unkMatrix();

		int n = unkMatrix.rows();
		int kry = Math.max(1, Math.min(m, n)); // Krylov subspace dimension can't exceed the system size

		MatrixComplex xi = new MatrixComplex(n, 1); // x[i]
		xi.initMatrix(initGuess.rep(), initGuess.imp());

		MatrixComplex M = new MatrixComplex(n).eye(); // M starts as I, the identity matrix
		MatrixComplex nullResidual = new MatrixComplex(n, 1);
		nullResidual.initMatrix(0, 0);

		for (k = 0; k < iterations; ++k) {
			Syseqnum Mr = new Syseqnum(M.augment(indTerm.minus(unkMatrix.times(xi))));
			MatrixComplex r = (Mr.jacobi(initGuess, iterations)).transpose();
			offset += Mr.getIterations();
			double beta = r.euc_norm();

			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				System.out.println(HEADINFO + "genminres cycle:" + k + " beta:" + beta);
			}
			/* ------------- END DEBUGGING BLOCK ------------- */

			if (beta < Complex.zero_threshold_approx()) {
				numIters = k + offset;
				solved = true;
				return xi.transpose();
			}

			MatrixComplex[] V = new MatrixComplex[kry + 1]; // Orthonormal Krylov basis, one column per entry
			MatrixComplex h = new MatrixComplex(kry + 1, kry); // Upper-Hessenberg matrix built by Arnoldi
			V[0] = r.divides(beta);

			int usedDim = kry; // Shrinks on a lucky breakdown (exact solution reached inside the Krylov space)
			for (int j = 0; j < kry; ++j) {
				Syseqnum Mw = new Syseqnum(M.augment(unkMatrix.times(V[j])));
				MatrixComplex w = (Mw.jacobi(V[j].transpose(), iterations)).transpose();
				offset += Mw.getIterations();

				for (int i = 0; i <= j; ++i) {
					Complex hij = V[i].adjoint().times(w).getItem(0, 0); // Hermitian inner product <V[i],w>
					h.setItem(i, j, hij);
					w = w.minus(V[i].times(hij));
				}
				double hNext = w.euc_norm();
				h.setItem(j + 1, j, new Complex(hNext, 0));

				if (hNext < Complex.zero_threshold_approx()) {
					usedDim = j + 1;
					break;
				}
				V[j + 1] = w.divides(hNext);
			}

			// Solve min||beta*e1 - H*y|| (H is (usedDim+1) x usedDim upper-Hessenberg) via successive
			// complex Givens rotations that reduce H to upper triangular, applied to the RHS too.
			Complex[][] R = new Complex[usedDim + 1][usedDim];
			for (int row = 0; row <= usedDim; ++row)
				for (int col = 0; col < usedDim; ++col)
					R[row][col] = h.getItem(row, col);

			Complex[] g = new Complex[usedDim + 1];
			g[0] = new Complex(beta, 0);
			for (int i = 1; i <= usedDim; ++i) g[i] = Complex.ZERO;

			for (int i = 0; i < usedDim; ++i) {
				Complex a = R[i][i];
				Complex b = R[i + 1][i];
				double rot = Math.hypot(a.mod(), b.mod());
				Complex c, s;
				if (rot < Complex.zero_threshold_approx()) {
					c = Complex.ONE;
					s = Complex.ZERO;
				} else {
					c = a.conjugate().divides(new Complex(rot, 0));
					s = b.conjugate().divides(new Complex(rot, 0));
				}
				for (int col = i; col < usedDim; ++col) {
					Complex top = R[i][col];
					Complex bot = R[i + 1][col];
					R[i][col] = c.times(top).plus(s.times(bot));
					R[i + 1][col] = c.conjugate().times(bot).plus(s.conjugate().times(Complex.mONE).times(top));
				}
				Complex gTop = g[i];
				Complex gBot = g[i + 1];
				g[i] = c.times(gTop).plus(s.times(gBot));
				g[i + 1] = c.conjugate().times(gBot).plus(s.conjugate().times(Complex.mONE).times(gTop));
			}

			// Back-substitution over the (now upper triangular) R
			Complex[] y = new Complex[usedDim];
			for (int i = usedDim - 1; i >= 0; --i) {
				Complex sum = g[i];
				for (int col = i + 1; col < usedDim; ++col) {
					sum = sum.minus(R[i][col].times(y[col]));
				}
				y[i] = sum.divides(R[i][i]);
			}

			MatrixComplex update = new MatrixComplex(n, 1);
			update.initMatrix(0, 0);
			for (int j = 0; j < usedDim; ++j) {
				update = update.plus(V[j].times(y[j]));
			}
			xi = xi.plus(update);

			MatrixComplex residual = indTerm.minus(unkMatrix.times(xi));
			if (checkConvergence(residual, nullResidual, Complex.precision())) {
				numIters = k + 1 + offset;
				solved = true;
				return xi.transpose();
			}
		}

		numIters = k + offset;
		return xi.transpose();
	}

}
		
