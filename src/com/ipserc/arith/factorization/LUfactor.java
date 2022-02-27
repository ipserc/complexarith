/*
 * Factorización LU

    Aplicable a: una matriz cuadrada A

    Factorización: A = L U, donde L es una matriz triangular inferior y U es una matriz triangular superior

    Notas: La factorización LU expresa el método de Gauss en forma matricial. En efecto, PA = LU donde P es una matriz 
    de permutación. Los elementos de la diagonal principal de L son todos iguales a 1. Una condición suficiente de que 
    exista la factorización es que la matriz A sea invertible.

    Resolución del sistema de ecuaciones lineales Ax = b: primero se resuelve el sistema de ecuaciones Ly = b y después 
    Ux = y.

    Existencia: Una condición necesaria y suficiente es que todos los menores principales de A sean distintos de cero.1

    Métodos de cálculo: método de Crout que obtiene una matriz U cuyos elementos de la diagonal son todos 1. 
    El método de Doolittle es una modificación del mismo.

 */


package com.ipserc.arith.factorization;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

/**
 * 
 * @author ipserc
 *
 */
public class LUfactor extends MatrixComplex {
	public static enum LUmethod {NONE, PIVOT, CROUT, DOOLITTLE, CHOLESKY};

	private MatrixComplex cL;
	private MatrixComplex cU;
	private MatrixComplex cP;
	private boolean factorized = false;
	private LUmethod method = LUmethod.NONE;

	private final static String HEADINFO = "LUfactor --- INFO: ";
	private final static String VERSION = "1.4 (2022_0209_2130)";
	/* VERSION Release Note
	 * 
	 * 1.4 (2022_0209_2130)
	 * private MatrixComplex CHOLESKYcoef() sqrrot changed to sqrt
	 * 
	 * 1.3 (2021_0319_1200)
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
	 * Instantiates a complex square array of length len.
	 * @param len The length of the square array.
	 */
	public LUfactor(int len) {
		super(len);
	}

	/**
	 * Instantiates a complex array of dimensions rowLen x colLen.
	 * @param rowLen Number of rows.
	 * @param colLen Number of columns.
	 */
	public LUfactor(int rowLen, int colLen) {
		super(rowLen, colLen);
	}

	/**
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with "," and factorizes it.
	 * @param strMatrix the string with the rows and columns.
	 */
	public LUfactor(String strMatrix) {
		super(strMatrix);
		factorize();
	}

	/**
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with "," and factorizes it.
	 * @param strMatrix the string with the rows and columns.
	 * @param method The method used to do the factorization
	 */
	public LUfactor(String strMatrix, final LUmethod method) {
		super(strMatrix);
		factorize(method);
	}

	/**
	 * Instantiates a LUfactor array from a MatrixComplex and factorizes it.
	 * @param matrix the MatrixComplex already instantiated.
	 */
	public LUfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
		factorize();
	}

	/**
	 * Instantiates a LUfactor array from a MatrixComplex and factorizes it.
	 * @param matrix the MatrixComplex already instantiated.
	 * @param method The method used to do the factorization
	 */
	public LUfactor(MatrixComplex matrix, final LUmethod method) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
		factorize(method);
	}

	/*
	 * ***********************************************
	 * 	FACTORIZATION
	 * ***********************************************
	 */

	/**
	 * Factorizes the array using the LU decomposition.
	 * In numerical analysis and linear algebra, LU decomposition (where 'LU' stands for 'lower upper', and also called LU factorization) 
	 * factors a matrix as the product of a lower triangular matrix and an upper triangular matrix. 
	 * The product sometimes includes a permutation matrix as well. The LU decomposition can be viewed as the matrix form of Gaussian elimination. 
	 * Computers usually solve square systems of linear equations using the LU decomposition, and it is also a key step when inverting a matrix, 
	 * or computing the determinant of a matrix. 
	 * The LU decomposition was introduced by mathematician Tadeusz Banachiewicz in 1938.[Source Wikipedia]
	 */
	public void factorize(final LUmethod method) {
		factorized = false;
		this.method = LUmethod.NONE;
		if (this.rows() != this.cols()) {
			System.out.println("rows:" + this.rows() + " cols:" + this.cols());
			factorized = false;
			return;
		}
		this.method = method;
		switch (method) {
			case PIVOT: PIVOTfactorize(); break;
			case CROUT: CROUTfactorize(); break;
			case DOOLITTLE: DOOLITTLEfactorize(); break;
			case CHOLESKY: CHOLESKIfactorize(); break;
			case NONE: break;
			default: break;
		}
	}

	public void factorize() {
		factorized = false;
		this.method = LUmethod.NONE;
		if (this.rows() != this.cols()) {
			System.out.println("rows:" + this.rows() + " cols:" + this.cols());
			factorized = false;
			return;
		}
		CROUTfactorize();		if (factorized){ this.method = LUmethod.CROUT; return; }
		//DOOLITTLE is the same as CROUT but returning L and U both transposed
		//DOOLITTLEfactorize();	if (factorized){ this.method = LUmethod.DOOLITTLE; return; }
		//CHOLESKIfactorize();	if (factorized){ this.method = LUmethod.CHOLESKY; return; }
		PIVOTfactorize();		if (factorized){ this.method = LUmethod.PIVOT; return; }
	}

	/**
	 * Factorizes the array using the LU decomposition.
	 * In numerical analysis and linear algebra, LU decomposition (where 'LU' stands for 'lower upper', and also called LU factorization) 
	 * factors a matrix as the product of a lower triangular matrix and an upper triangular matrix. 
	 * The product sometimes includes a permutation matrix as well. The LU decomposition can be viewed as the matrix form of Gaussian elimination. 
	 * Computers usually solve square systems of linear equations using the LU decomposition, and it is also a key step when inverting a matrix, 
	 * or computing the determinant of a matrix. 
	 * The LU decomposition was introduced by mathematician Tadeusz Banachiewicz in 1938.[Source Wikipedia]
	 */
	private void PIVOTfactorize() {
		int rowLen = this.rows();
		int colLen = this.cols();

		cP = new MatrixComplex(rowLen, colLen); cP.initMatrixDiag(1,0);
		cL = new MatrixComplex(rowLen, colLen); cL.initMatrixDiag(1,0);
		//cU = new MatrixComplex(rowLen, colLen); 
		cU = this.copy();

		for (int k = 0; k < rowLen-1; ++k) {
			if (cU.complexMatrix[k][k].equals(0,0)) {
				int rowSwap = cU.partialPivot(k);
				//int rowSwap = cU.locateSwapRowUp(k);
				if (rowSwap == -1) 
					return;
				if (rowSwap != k) {
					cU.swapRows(k, rowSwap);
					cP.swapRows(k, rowSwap);				
					//cL.swapRows(k, rowSwap);
					cL.swapRowsL(k, rowSwap);
				}
			}
			for (int row = k+1; row < rowLen; ++row) {
				cL.complexMatrix[row][k] = cU.complexMatrix[row][k].divides(cU.complexMatrix[k][k]);
				cU.complexMatrix[row][k].setComplexRec(0,0);
				for (int col = k+1; col < colLen; ++col) {
					cU.complexMatrix[row][col] = cU.complexMatrix[row][col].minus(cU.complexMatrix[k][col].times(cL.complexMatrix[row][k]));
				}
			}
		}
		if (cL.isNaN() || cL.isInfinite() || cU.isNaN() || cU.isInfinite()) factorized = false;
		else factorized = true;
	}

	/**
	 * Calculates the CROUT L row matrix for a given column 
	 * @param theCol The column
	 */
	private void CROUT_L(int theCol) {
		//Boolean dep = false;
		//if(dep) System.out.println(Complex.repeat("-L", 25));
		for (int row = theCol; row < this.rows(); ++row) {
			Complex SLU = new Complex(0,0);
			for (int idx = 0; idx < theCol; ++idx) {
				//if(dep) System.out.print("L"+row+idx+"*U"+idx+theCol+"+");
				SLU = SLU.plus(cL.getItem(row, idx).times(cU.getItem(idx, theCol)));
			}
			//if(dep) System.out.println("\na"+row+theCol);
			SLU = this.getItem(row, theCol).minus(SLU);
			cL.setItem(row, theCol, SLU);
		}
	}
	
	/**
	 * Calculates the CROUT U column matrix for a given row 
	 * @param theRow The row
	 * @return true if the diagonalization can be done, else false
	 */
	private boolean CROUT_U(int theRow) {
		//Boolean dep = false;
		//if(dep) System.out.println(Complex.repeat("+U", 25));
		for (int col = theRow+1; col < this.cols(); ++col) {
			Complex SLU = new Complex(0,0);
			for (int idx = 0; idx < theRow; ++idx) {
				//if(dep) System.out.print("L"+theRow+idx+"*U"+idx+col+"+");
				SLU = SLU.plus(cL.getItem(theRow, idx).times(cU.getItem(idx, col)));				
			}
			//if(dep) System.out.println("\na"+theRow+col+" L"+theRow+theRow);
			SLU = (this.getItem(theRow, col).minus(SLU)).divides(cL.getItem(theRow, theRow));
			if (SLU.isInfinite() || SLU.isNaN()) return false;
			cU.setItem(theRow, col, SLU);
		}
		return true;
	}
	
	/**
	 * In linear algebra, the Crout matrix decomposition is an LU decomposition which decomposes a matrix into a lower triangular matrix (L), 
	 * an upper triangular matrix (U) and, although not always needed, a permutation matrix (P). It was developed by Prescott Durand Crout. [1]
	 * The Crout matrix decomposition algorithm differs slightly from the Doolittle method. Doolittle's method returns a unit lower triangular matrix 
	 * and an upper triangular matrix, while the Crout method returns a lower triangular matrix and a unit upper triangular matrix.
	 * So, if a matrix decomposition of a matrix A is such that: A = LDU being L a unit lower triangular matrix, D a diagonal matrix 
	 * and U a unit upper triangular matrix, then Doolittle's method produces A = L(DU) and Crout's method produces A = (LD)U.
	 * source wikipedia https://en.wikipedia.org/wiki/Crout_matrix_decomposition
	 */
	private void CROUTfactorize() {
		int rowLen = this.rows();
		int colLen = this.cols();

		// **** if (this.determinant().equalsred(Complex.ZERO)) return;
		if (this.determinant().equals(Complex.ZERO)) return;
		
		cP = new MatrixComplex(rowLen, colLen); cP.initMatrixDiag(1,0);
		cL = new MatrixComplex(rowLen, colLen);
		cU = new MatrixComplex(rowLen, colLen); cU.initMatrixDiag(1,0);
		
		// -----------
		// L Matrix
		// -----------
		// col 0
		{
			int col = 0;
			for (int row = 0; row < rowLen; ++row ) {
				cL.setItem(row, col, this.getItem(row, col));
			}
		}

		// -----------
		// U Matrix
		// -----------
		// row 0
		{
			int row = 0;
			for (int col = 1; col < rowLen; ++col ) {
				cU.setItem(row, col, this.getItem(row, col).divides(cL.getItem(row, row)));
			}
		}
		
		for(int idx = 1; idx < rowLen; ++idx) {
			CROUT_L(idx);
			if (!CROUT_U(idx)) return;
		}
		factorized = true;
	}

	/**
	 * Calculates the DOOLITTLE U column matrix for a given row
	 * @param theRow The given row
	 */
	private void DOOLITTLE_U(int theRow) {
		//Boolean dep = false;
		//if(dep) System.out.println(Complex.repeat("-U", 25));
		for (int col = theRow; col < this.cols(); ++col) {
			Complex SLU = new Complex(0,0);
			for (int idx = 0; idx < theRow; ++idx) {
				//if(dep) System.out.print("L"+theRow+idx+"*U"+idx+col+"+");
				SLU = SLU.plus(cU.getItem(theRow, idx).times(cL.getItem(idx, col)));
			}
			//if(dep) System.out.println("\na"+theRow+col);
			SLU = this.getItem(theRow, col).minus(SLU);
			cU.setItem(theRow, col, SLU);
		}
	}

	/**
	 * Calculates the DOOLITTLE U row matrix for a given column
	 * @param theCol
	 * @return true if the diagonalization can be done, else false
	 */
	private boolean DOOLITTLE_L(int theCol) {
		//Boolean dep = false;
		//if(dep) System.out.println(Complex.repeat("+L", 25));
		for (int row = theCol; row < this.rows(); ++row) {
			Complex SLU = new Complex(0,0);
			for (int idx = 0; idx < theCol; ++idx) {
				//if(dep) System.out.print("L"+row+idx+"*U"+idx+theCol+"+");
				SLU = SLU.plus(cU.getItem(row, idx).times(cL.getItem(idx, theCol)));				
			}
			//if(dep) System.out.println("\na"+row+theCol+" U"+theCol+theCol);
			SLU = (this.getItem(row, theCol).minus(SLU)).divides(cU.getItem(theCol, theCol));
			if (SLU.isInfinite() || SLU.isNaN()) return false;
			cL.setItem(row, theCol, SLU);
		}
		return true;
	}

	/**
	 * In linear algebra, the Crout matrix decomposition is an LU decomposition which decomposes a matrix into a lower triangular matrix (L), 
	 * an upper triangular matrix (U) and, although not always needed, a permutation matrix (P). It was developed by Prescott Durand Crout. [1]
	 * The Crout matrix decomposition algorithm differs slightly from the Doolittle method. Doolittle's method returns a unit lower triangular matrix 
	 * and an upper triangular matrix, while the Crout method returns a lower triangular matrix and a unit upper triangular matrix.
	 * So, if a matrix decomposition of a matrix A is such that: A = LDU being L a unit lower triangular matrix, D a diagonal matrix 
	 * and U a unit upper triangular matrix, then Doolittle's method produces A = L(DU) and Crout's method produces A = (LD)U.
	 * source wikipedia https://en.wikipedia.org/wiki/Crout_matrix_decomposition
	 */
	private void DOOLITTLEfactorize_() {
		int rowLen = this.rows();
		int colLen = this.cols();

		// **** if (this.determinant().equalsred(Complex.ZERO)) return;
		if (this.determinant().equals(Complex.ZERO)) return;
		
		cP = new MatrixComplex(rowLen, colLen); cP.initMatrixDiag(1,0);
		cL = new MatrixComplex(rowLen, colLen); cL.initMatrixDiag(1,0);
		cU = new MatrixComplex(rowLen, colLen);
		
		// -----------
		// U Matrix
		// -----------
		// row 0
		{
			int row = 0;
			for (int col = 0; col < rowLen; ++col ) {
				cU.setItem(row, col, this.getItem(row, col));
			}
		}
		
		// -----------
		// L Matrix
		// -----------
		// col 0
		{
			int col = 0;
			for (int row = 1; row < rowLen; ++row ) {
				cL.setItem(row, col, this.getItem(row, col).divides(cU.getItem(col, col)));
			}
		}

		for(int idx = 1; idx < rowLen; ++idx) {
			DOOLITTLE_U(idx);
			if (!DOOLITTLE_L(idx)) return;
		}
		factorized = true;
	}

	/**
	 * The Doolittle factorization is as doing a Crout's one but returning the U and L arrays transposed
	 */
	private void DOOLITTLEfactorize() {
		LUfactor newLU = new LUfactor(this.transpose(), LUmethod.CROUT);
		this.factorized = newLU.factorized;
		if (factorized) {
			this.cU = newLU.cL.transpose();
			this.cL = newLU.cU.transpose();
			this.cP = newLU.P();
		}
	}
	
	/**
	 * Calculates the Cholesky array values for the decomposition
	 * @return The L array
	 */
	private MatrixComplex CHOLESKYcoef() {
		MatrixComplex coefMatrix = new MatrixComplex(this.rows());
		Complex coef = new Complex();

		//Step 1
		coefMatrix.setItem(0,0,Complex.sqrt(this.getItem(0,0)));
		
		//Step 2
		for (int row = 1; row < this.rows(); ++row) {
			coefMatrix.setItem(row,0,this.getItem(row,0).divides(coefMatrix.getItem(0,0)));
		}
		
		//Step 3
		for (int col = 1; col < this.cols()-1; ++col) {
		
			//Step 4
			coef = Complex.ZERO;
			for (int k = 0; k < col; ++k) {
				coef = coef.plus(coefMatrix.getItem(col,k).power(2));
			}
			coefMatrix.setItem(col,col,Complex.sqrt(this.getItem(col,col).minus(coef)));
			
			//Step 5
			for (int row = col+1; row < this.rows(); ++row) {
				coef = Complex.ZERO;
				for (int k = 0; k < col; ++k) {
					coef = coef.plus(coefMatrix.getItem(row,k).times(coefMatrix.getItem(col,k)));						
				}
				coefMatrix.setItem(row,col,(this.getItem(row,col).minus(coef)).divides(coefMatrix.getItem(col,col)));
			}
		}
		
		//Step 6
		int n = this.rows()-1;
		coef = Complex.ZERO;
		for (int k = 0; k < n-1; ++k) {
			coef = coef.plus(coefMatrix.getItem(n,k).power(2));						
		}
		coefMatrix.setItem(n,n,Complex.sqrt(this.getItem(n,n).minus(coef)));
		
		return coefMatrix;
	}

	
	/**
	 * In linear algebra, the Cholesky decomposition or Cholesky factorization (pronounced /ʃəˈlɛski/ shə-LES-kee) is a decomposition of a Hermitian, 
	 * positive-definite matrix into the product of a lower triangular matrix and its conjugate transpose, which is useful for efficient numerical 
	 * solutions, e.g., Monte Carlo simulations. It was discovered by André-Louis Cholesky for real matrices. When it is applicable, the Cholesky decomposition 
	 * is roughly twice as efficient as the LU decomposition for solving systems of linear equations.
	 */
	private void CHOLESKIfactorize() {
		int rowLen = this.rows();
		int colLen = this.cols();
		
		// **** if (this.determinant().equalsred(Complex.ZERO)) return;
		if (this.determinant().equals(Complex.ZERO)) return;
		
		cP = new MatrixComplex(rowLen, colLen); cP.initMatrixDiag(1,0);
		cL = new MatrixComplex(rowLen, colLen);
		cU = new MatrixComplex(rowLen, colLen);

		cL = CHOLESKYcoef();
		cU = cL.transpose();
		if (cL.isNaN() || cL.isInfinite() || cU.isNaN() || cU.isInfinite()) factorized = false;
		else factorized = true;
	}

	/*
	 * ***********************************************
	 * 	GETTERS 
	 * ***********************************************
	 */

	/**
	 * Returns the id of the factorization method used to decompose the array
	 * @return The id of the factorization method
	 */
	public LUmethod getMethod() {
		return this.method;
	}
	
	/**
	 * Gets the class member variable with the Lower array.
	 * @return The Lower array of the LU decomposition.
	 */
	public MatrixComplex L() {
		return cL;
	}

	/**
	 * Gets the class member variable with the Upper array.
	 * @return The Upper array of the LU decomposition.
	 */
	public MatrixComplex U() {
		return cU;
	}

	/**
	 * Gets the class member variable with the Permutation array.
	 * @return The Permutation array of the LU decomposition.
	 */
	public MatrixComplex P() {
		return cP;
	}

	/**
	 * Gets the class member variable with the status of the factorization.
	 * @return The factorization status.
	 */
	public boolean factorized() {
		return factorized;
	}
	
	/*
	 * ***********************************************
	 * 	PRINTING
	 * ***********************************************
	 */

	/**
	 * Returns the name of the factorization method used to decompose the array
	 * @return The name of the factorization method
	 */
	public String getMethodName() {
		switch (this.method) {
			case PIVOT: return "PIVOT";
			case CROUT: return "CROUT";
			case DOOLITTLE: return "DOOLITTLE";
			case CHOLESKY: return "CHOLESKY";
			default: return "NONE";
		}
	}

	/**
	 * Returns the expression for LU Factorization for Maxima. expand is available. 
	 * @param expand True if you want to expand the expressions
	 * @return The LU Factorization expression
	 * 
	 * generalring - el anillo de las expresiones de Maxima
	 * floatfield - el campo de los números decimales en coma flotante de doble precisión
	 * complexfield - el campo de los números complejos decimales en coma flotante de doble precisión
	 * crering - el anillo de las expresiones canónicas racionales (Canonical Rational Expression o CRE) de Maxima
	 * rationalfield - el campo de los números racionales
	 * runningerror - controla los errores de redondeo de las operaciones en coma flotante
	 * noncommutingring - el anillo de las expresiones de Maxima en las que el producto es el operador no conmutativo "." 
	 */
	public String toMaxima_lu_factor(boolean expand) {
		String toMaxima;
		toMaxima = "get_lu_factors(lu_factor(" +this.toMaxima()+", complexfield))";
		toMaxima = expand ? "expand("+toMaxima+");" : ";";
		toMaxima = "[P,L,U]:" + toMaxima;
		return toMaxima;
	}
	
	/**
	 * Returns the expression for LU Factorization for GNU Octave. expand is available. 
	 * @return The LU Factorization expression
	 */
	public String toOctave_lu() {
		String toOctave;
		toOctave = "[l, u, p] = lu("+this.toOctave()+")";
		return toOctave;
	}

	/**
	 * Returns the expression for LU Factorization for Matlab. expand is available. 
	 * @return The LU Factorization expression
	 */
	public String toMatlab_lu() {
		String toMatlab;
		toMatlab = "[l, u, p] = lu("+this.toMatlab()+")";
		return toMatlab;
	}

	/**
	 * Returns the expression for LU Factorization for Wolfram. expand is available. 
	 * @return The LU Factorization expression
	 */
	public String toWolfram_LUDecomposition() {
		String toWolfram;
		toWolfram = "LUDecomposition["+this.toWolfram()+"]";
		return toWolfram;
	}
}
