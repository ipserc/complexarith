/*
 * Descomposición en valores singulares

    Aplicable a: una matriz A m-por-n.

    Factorización: A = U Σ V T, donde Σ es una matriz diagonal mxn, y U y V son matrices ortogonales mxm y nxn 
    respectivamente, siendo V T la traspuesta de V. Los elementos de la diagonal de Σ son los valores singulares de A 
    y son mayores o iguales a cero.

    Notas: a la matriz V Σ + U T , donde Σ + es igual a la matriz Σ reemplazando los valores singulares por sus recíprocos, 
    se le llama pseudoinversa de A.


 */

package com.ipserc.arith.factorization;

import java.awt.image.Kernel;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.LUfactor.LUmethod;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.Polynom;

/**
 * 
 * @author ipserc
 *
 */
public class SVDfactor extends MatrixComplex {
	public static enum SVDmethod {NONE, FULL, REDUCED, IDENTITY, IDENTMIX, IMAGINARY};

	private MatrixComplex cS;
	private MatrixComplex cV;
	private MatrixComplex cU;

	private final static String HEADINFO = "SVDfactor --- INFO: ";
	private final static String VERSION = "1.1 (2022_0123_0100)";
	private boolean factorized = false;
	private boolean oriented = false;
	private SVDmethod method = SVDmethod.NONE;

	
	/* VERSION Release Note
	 * 
	 * 1.1 (2022_0123_0100)
	 * toWolfram_svd() --> svd[...]
	 * factorize() completely reprogrammed 
	 * toMaxima_dgesvd(boolean expand) degsvd for Maxima
	 * private void factorizeIDENTITY()
	 * private void factorizeIDENTMIX()
	 * private void factorizeFULL()
	 * private void factorizeREDUCED()
	 * public static enum SVDmethod {NONE, FULL, REDUCED, IDENTITY, IDENTMIX, IMAGINARY};
	 * public SVDfactor(String strMatrix, final SVDmethod method) {
	 * public SVDfactor(MatrixComplex matrix, final SVDmethod method) {
	 * public void factorize() {
	 * public void factorize(final SVDmethod method) {
	 * private void factorizeIMAGINARY()
	 * 
	 * 1.0 (2020_0824_1800)
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
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix the string with the rows and columns.
	 */
	public SVDfactor(String strMatrix) {
		super(strMatrix);
		factorize();
	}

	/**
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix the string with the rows and columns.
	 * @param method he method used to do the factorization
	 */
	public SVDfactor(String strMatrix, final SVDmethod method) {
		super(strMatrix);
		factorize(method);
	}

	/**
	 * Instantiates a complex array from a MatrixComplex.
	 * @param matrix the MatrixComplex.
	 */
	public SVDfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
		factorize();
	}

	/**
	 * Instantiates a SVDfactor array from a MatrixComplex and factorizes it.
	 * @param matrix the MatrixComplex already instantiated.
	 * @param method The method used to do the factorization
	 */
	public SVDfactor(MatrixComplex matrix, final SVDmethod method) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
		factorize(method);
	}

	
	/*
	 * ***********************************************
	 * 	PRIVATE METHODS 
	 * ***********************************************
	 */

	/**
	 * Private method. If the matrix to decompose has less rows the columns this method returns the adjoint of the matrix to do the factorization over it.
	 * @return The adjoint matrix to decompose with the SVD factorization.
	 */
	private MatrixComplex orientMatrix() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex oMatrix = this.copy();
		if (rowLen <= colLen) {
			oMatrix = oMatrix.adjoint();
			oriented = true;
		}
		else oriented = false;
		return oMatrix;
	}

	/*
	 * ***********************************************
	 * 	METHODS 
	 * ***********************************************
	 */
	
	/**
	 * Does the SVD factorization using the default method SVD Full. If this method doesn't achieve the factorization use the surrogate method SVD Identity
	 */
	public void factorize() {
		factorized = false;
		this.method = SVDmethod.NONE;
		factorizeREDUCED();		if (factorized){ this.method = SVDmethod.REDUCED; return; }
		//factorizeFULL();		if (factorized){ this.method = SVDmethod.FULL; return; }
		factorizeIDENTITY();	if (factorized){ this.method = SVDmethod.IDENTITY; return; }
	}
	
	/**
	 * Does the SVD factorization using the given method as parameter.
	 * @param method The method used for the factorization
	 */
	public void factorize(final SVDmethod method) {
		factorized = false;
		this.method = SVDmethod.NONE;
		this.method = method;
		switch (method) {
			case FULL: factorizeFULL(); break;
			case REDUCED: factorizeREDUCED(); break;
			case IDENTITY: factorizeIDENTITY(); break;
			case IDENTMIX: factorizeIDENTMIX(); break;
			case IMAGINARY: factorizeIMAGINARY(); break;
			case NONE: break;
			default: break;
		}
	}
	
	/**
	 * Factorizes matrices mxn where m is greater or equal than n.
	 * If in the array m is lesser n, it uses the orientMatrix method to return the adjoint matrix and operate on it.
	 * At the end undoes the change to get the results for the original matrix.
	 * In linear algebra, the singular value decomposition (SVD) is a factorization of a real or complex matrix. 
	 * It is the generalization of the eigendecomposition of a positive semidefinite normal matrix (for example, a symmetric matrix with positive eigenvalues) to any m × n  
	 * matrix via an extension of the polar decomposition. 
	 * It has many useful applications in signal processing and statistics.
	 * The result of the factorization are the U, S and V arrays, where M = U·S·V*
	 * U is an m × m unitary matrix (if K = R {\displaystyle \mathbb {R} } \mathbb {R} , unitary matrices are orthogonal matrices),
	 * Σ is a diagonal m × n matrix with non-negative real numbers on the diagonal,
	 * V is an n × n unitary matrix over K, and
	 * V* is the conjugate transpose of V.
	 * [Source Wikipedia]
	 * IMPORTANT NOTE: Sigma Matrix (S) is calculated with approximation. A trick to manage roots of value zero is used. It is an approximation and it is not correct, cause in the case of having roots equals zero, 
	 * the algorithm substitutes zero for ZERO_THRESHOLD_R (10E-9), with this is possible calculate the U Matrix row for this root. Zero roots don't have any management in this model of SVD
	 * Algorithm source http://www.mate.unlp.edu.ar/practicas/70_18_0911201012951
	 * Algorithm source https://www.yanivplan.com/files/SVD-from-Lay.pdf
	 */
	/**
	 * factorizeIDENTITY()
	 * Σ (mxn): diagonal (A*·A) eigenvalues square root matrix, augmented with zeroes as required. If eigenval is zero it is changed to one.
	 * V (nxn): Identity matrix
	 * U (mxm): solutions to the equations systems A·V = U·Σ, for each A·V vector (interms) Ui = A·Vi / Σi
	 */
	private void factorizeIDENTITY() {
		final boolean DEBUG_ON = false; 
		final String METH_NAME = "factorize()";
		final String HEAD_METH = HEADINFO + " " + METH_NAME + ":";
		MatrixComplex cSS;
		MatrixComplex cII;
		MatrixComplex cPP;

		factorized = false;

		// Solution for rowLen > colLen
		// Otherwise works with the adjoint matrix
		MatrixComplex aMatrix = this.orientMatrix();

		int rowLen = aMatrix.rows();
		int colLen = aMatrix.cols();
		
		// Matrix Σ calculation section
		//
		// Σ: diagonal eigenvalues square root matrix. If eigenval is zero it is changed to one.  augmented with zeroes as required
		//
		cSS = new MatrixComplex(rowLen, colLen); 
		{
			MatrixComplex bMatrix = aMatrix.adjoint().times(aMatrix);
			Polynom bMatrixPol = bMatrix.charactPoly();
			MatrixComplex roots = bMatrixPol.solve();
			roots.quicksort(0);
	
			for (int i = 0; i < colLen; ++i) {
				Complex root = roots.getItem(i,0);
				// root.println(HEAD_METH + "root(" + i  +"): ");
				if (root.isZero())
					cSS.complexMatrix[i][i] = Complex.ONE; // Skip the indetermination to come setting this to ONE
				else
					cSS.complexMatrix[i][i] = root.power(0.5);
			}
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				aMatrix.println(HEAD_METH + "aMatrix 'oriented'");
				bMatrix.println(HEAD_METH + "bMatrix as aMatrix.adjoint().times(aMatrix)");
				roots.println(HEAD_METH + "roots");
			}
			/* ------------- END DEBUGGING BLOCK ------------- */
		}

		// Matrix V calculation section
		//
		// V: Identity matrix
		//
		cII = new MatrixComplex(colLen); cII.initMatrixDiag(1,0);

		// Matrix U calculation section
		//
		// U: solutions to the equations systems A·V = U·Σ, for each A·V vector (interms)
		// Ui = A·Vi / Σi
		//
		cPP = new MatrixComplex(rowLen); cPP.initMatrixDiag(1,0);
		for (int col = 0; col < colLen; ++col) {
			cPP.copyCol(col, (aMatrix.times(cII.getRow(col).transpose())).divides(cSS.getItem(col, col)), 0);
		}
		cPP.normalize();
		
		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			//eigenSpace.roots().println(HEAD_METH + "eigenSpace.roots()");
			//eigenSpace.eigenvalues().println(HEAD_METH + "eigenSpace.eigenvalues()");
			//eigenSpace.solutions().println(HEAD_METH + "eigenSpace.solutions()");
			//eigenSpace.eigenvectors().println(HEAD_METH + "eigenSpace.eigenvectors()");
			cSS.println(HEAD_METH + "cSS");
			System.out.println("cSS:" +cSS.toMatrixComplex());
			cII.println(HEAD_METH + "cII");
			System.out.println("cII:" +cII.toMatrixComplex());
			cPP.println(HEAD_METH + "cPP");
			System.out.println("cPP:" +cPP.toMatrixComplex());
			aMatrix.times(cII).println(HEAD_METH + "aMatrix.times(cII)");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */
		
		
		// Solution for oriented (rowLen <= colLen)
		// Otherwise undo the re-orientation 
		if (oriented){
			this.cU = cII.transpose();
			this.cV = cPP;
			this.cS = cSS.transpose();
		}
		else {
			this.cU = cPP;
			this.cV = cII.transpose();
			this.cS = cSS;
		}
		factorized = cU.times(cS).times(cV.adjoint()).equals(this);
	}

	/**
	 * factorizeIMAGINARY()
	 * Σ (mxn): diagonal (A*·A) eigenvalues square root matrix, augmented with zeroes as required. If eigenval is zero it is changed to one.
	 * V (nxn): Identity Imaginary matrix
	 * U (mxm): solutions to the equations systems A·V = U·Σ, for each A·V vector (interms) Ui = A·Vi / Σi
	 */
	private void factorizeIMAGINARY() {
		final boolean DEBUG_ON = false; 
		final String METH_NAME = "factorize()";
		final String HEAD_METH = HEADINFO + " " + METH_NAME + ":";
		MatrixComplex cSS;
		MatrixComplex cII;
		MatrixComplex cPP;

		factorized = false;

		// Solution for rowLen > colLen
		// Otherwise works with the adjoint matrix
		MatrixComplex aMatrix = this.orientMatrix();

		int rowLen = aMatrix.rows();
		int colLen = aMatrix.cols();
		
		// Matrix Σ calculation section
		//
		// Σ: diagonal eigenvalues square root matrix, augmented with zeroes
		//
		cSS = new MatrixComplex(rowLen, colLen); 
		{
			MatrixComplex bMatrix = aMatrix.adjoint().times(aMatrix);
			Polynom bMatrixPol = bMatrix.charactPoly();
			MatrixComplex roots = bMatrixPol.solve();
			roots.quicksort(0);
	
			for (int i = 0; i < colLen; ++i) {
				Complex root = roots.getItem(i,0);
				// root.println(HEAD_METH + "root(" + i  +"): ");
				if (root.isZero())
					cSS.complexMatrix[i][i] = Complex.i; // Skip the indetermination to come setting this to ONE
				else
					cSS.complexMatrix[i][i] = root.power(0.5);
			}
			/* -------------   DEBUGGING BLOCK   ------------- */
			if (DEBUG_ON) {
				aMatrix.println(HEAD_METH + "aMatrix 'oriented'");
				bMatrix.println(HEAD_METH + "bMatrix as aMatrix.adjoint().times(aMatrix)");
				roots.println(HEAD_METH + "roots");
			}
			/* ------------- END DEBUGGING BLOCK ------------- */
		}

		//Matrix V calculation section
		//
		// V: Identity matrix
		//
		cII = new MatrixComplex(colLen); cII.initMatrixDiag(0,1);

		// Matrix U calculation section
		//
		// U: solutions to the equations systems A·V = U·Σ, for each A·V vector (interms)
		// Ui = A·Vi / Σi
		//
		cPP = new MatrixComplex(rowLen); cPP.initMatrixDiag(1,0);
		for (int col = 0; col < colLen; ++col) {
			cPP.copyCol(col, (aMatrix.times(cII.getRow(col).transpose())).divides(cSS.getItem(col, col)), 0);
		}
		cPP.normalize();
		
		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			//eigenSpace.roots().println(HEAD_METH + "eigenSpace.roots()");
			//eigenSpace.eigenvalues().println(HEAD_METH + "eigenSpace.eigenvalues()");
			//eigenSpace.solutions().println(HEAD_METH + "eigenSpace.solutions()");
			//eigenSpace.eigenvectors().println(HEAD_METH + "eigenSpace.eigenvectors()");
			cSS.println(HEAD_METH + "cSS");
			System.out.println("cSS:" +cSS.toMatrixComplex());
			cII.println(HEAD_METH + "cII");
			System.out.println("cII:" +cII.toMatrixComplex());
			cPP.println(HEAD_METH + "cPP");
			System.out.println("cPP:" +cPP.toMatrixComplex());
			aMatrix.times(cII).println(HEAD_METH + "aMatrix.times(cII)");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */
		
		// Solution for oriented (rowLen <= colLen)
		// Otherwise undo the re-orientation 
		if (oriented){
			this.cU = cII.transpose();
			this.cV = cPP;
			this.cS = cSS.transpose();
		}
		else {
			this.cU = cPP;
			this.cV = cII.transpose();
			this.cS = cSS;
		}
		factorized = cU.times(cS).times(cV.adjoint()).equals(this);
	}
	
	/**
	 * factorizeIDENTMIX()
	 * Σ (mxn): diagonal (A*·A) eigenvalues square root matrix, augmented with zeroes as required. If eigenval is zero it is changed to one.
	 * V (nxn): (A*·A) eigenvectors matrix, augmented with some other linearly independent vectors to form a basis of F. If there is no eigenvectors enoug is completed with vectors from the unitary basis. Orthogonalized later if required. 
	 * U (mxm): solutions to the equations systems A·V = U·Σ, for each A·V vector (interms) Ui = A·Vi / Σi
	 */
	private void factorizeIDENTMIX() {
		final boolean DEBUG_ON = false; 
		final String METH_NAME = "factorize()";
		final String HEAD_METH = HEADINFO + " " + METH_NAME + ":";
		MatrixComplex cSS;
		MatrixComplex cVV;
		MatrixComplex cUU;

		factorized = false;

		// Solution for rowLen > colLen
		// Otherwise works with the adjoint matrix
		MatrixComplex aMatrix = this.orientMatrix();

		int rowLen = aMatrix.rows();
		int colLen = aMatrix.cols();

		MatrixComplex bMatrix = aMatrix.adjoint().times(aMatrix);
		Eigenspace eigenSpace = new Eigenspace(bMatrix);
		// We need the eigenvalues ordered (DOWN) from higher to lower
		if (eigenSpace.order() == Eigenspace.Order.UP) eigenSpace.orderSwap();

		// Matrix Σ calculation section
		//
		// Σ: diagonal eigenvalues square root matrix, augmented with zeroes. mxn
		//
		cSS = new MatrixComplex(rowLen, colLen);
		for (int i = 0; i < colLen; ++i) {
			Complex root = eigenSpace.roots().getItem(i,0);
			// root.println(HEAD_METH + "root(" + i  +"): ");
			if (root.isZero())
				cSS.complexMatrix[i][i] = Complex.ONE; // Skip the indetermination to come setting this to ONE
			else
				cSS.complexMatrix[i][i] = root.power(0.5);
		}

		//Matrix V calculation section
		//
		// V: eigenvectors matrix, augmented with some other linearly independent vectors to form a basis of F nxn
		//
		boolean gramSchmidt = false;
		cVV = new MatrixComplex(colLen); cVV.initMatrixDiag(1,0);
		for (int row = 0; row < eigenSpace.solutions().rows(); ++row) {
			if (!eigenSpace.solutions().isNullRow(row))
				cVV.copyRow(row, eigenSpace.solutions(), row);
			else gramSchmidt = true; // If there are null rows, we will rely on gramSchmidt to generate an orthogonal matrix 
		}
		if (gramSchmidt) cVV = cVV.gramSchmidtFull();
		cVV = cVV.normalize();

		// Matrix U calculation section
		//
		// U: solutions to the equations systems A·V = U·Σ, for each A·V vector (interms). mxm
		// Ui = A·Vi / Σi
		//
		cUU = new MatrixComplex(rowLen); cUU.initMatrixDiag(1,0);
		for (int col = 0; col < colLen; ++col) {
			cUU.copyCol(col, (aMatrix.times(cVV.getRow(col).transpose())).divides(cSS.getItem(col, col)), 0);
		}
		cUU.normalize();
		
		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			aMatrix.println(HEAD_METH + "aMatrix 'oriented'");
			bMatrix.println(HEAD_METH + "bMatrix as aMatrix.adjoint().times(aMatrix)");
			eigenSpace.roots().println(HEAD_METH + "eigenSpace.roots()");
			eigenSpace.eigenvalues().println(HEAD_METH + "eigenSpace.eigenvalues()");
			eigenSpace.solutions().println(HEAD_METH + "eigenSpace.solutions()");
			eigenSpace.eigenvectors().println(HEAD_METH + "eigenSpace.eigenvectors()");
			cSS.println(HEAD_METH + "cSS");
			System.out.println("cSS:" +cSS.toMatrixComplex());
			cVV.println(HEAD_METH + "cVV");
			System.out.println("cVV:" +cVV.toMatrixComplex());
			cUU.println(HEAD_METH + "cUU");
			System.out.println("cUU:" +cUU.toMatrixComplex());
			aMatrix.times(cVV).println(HEAD_METH + "aMatrix.times(cVV)");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */
		
		// Solution for oriented (rowLen <= colLen)
		// Otherwise undo the re-orientation 
		if (oriented) {
			this.cU = cVV.transpose();
			this.cV = cUU;
			this.cS = cSS.transpose();
		}
		else {
			this.cU = cUU;
			this.cV = cVV.transpose();
			this.cS = cSS;
		}
		factorized = cU.times(cS).times(cV.adjoint()).equals(this);
	}

	/**
	 * factorizeFULL()
	 * Σ (mxn): diagonal (A*·A) eigenvalues square root matrix, augmented with zeroes as required. Eigenvals zero are included.
	 * V (nxn): (A*·A) eigenvectors matrix, augmented with some other linearly independent vectors to form a basis of F. If there is no eigenvectors enoug is completed with vectors from the unitary basis. Orthogonalized later if required. 
	 * U (mxm): solutions to the equations systems A·V = U·Σ, for each A·V vector (interms) Ui = A·Vi / Σi
	 */
	private void factorizeFULL() {
		final boolean DEBUG_ON = false; 
		final String METH_NAME = "factorize()";
		final String HEAD_METH = HEADINFO + " " + METH_NAME + ":";
		MatrixComplex cSS;
		MatrixComplex cVV;
		MatrixComplex cUU;

		factorized = false;

		// Solution for rowLen > colLen
		// Otherwise works with the adjoint matrix
		MatrixComplex aMatrix = this.orientMatrix();

		int rowLen = aMatrix.rows();
		int colLen = aMatrix.cols();

		MatrixComplex bMatrix = aMatrix.adjoint().times(aMatrix);
		Eigenspace eigenSpace = new Eigenspace(bMatrix);
		// We need the eigenvalues ordered (DOWN) from higher to lower
		if (eigenSpace.order() == Eigenspace.Order.UP) eigenSpace.orderSwap();

		// Matrix Σ calculation section
		//
		// Σ: diagonal eigenvalues square root matrix, augmented with zeroes. mxn
		//
		cSS = new MatrixComplex(rowLen, colLen);
		for (int i = 0; i < colLen; ++i) {
			Complex root = eigenSpace.roots().getItem(i,0);
			// root.println(HEAD_METH + "root(" + i  +"): ");
			if (root.isZero()) break;
			else cSS.complexMatrix[i][i] = root.power(0.5);
		}

		//Matrix V calculation section
		//
		// V: eigenvectors matrix, augmented with some other linearly independent vectors to form a basis of F nxn
		//
		boolean gramSchmidt = false;
		cVV = new MatrixComplex(colLen); cVV.initMatrixDiag(1,0);
		for (int row = 0; row < eigenSpace.solutions().rows(); ++row) {
			if (!eigenSpace.solutions().isNullRow(row))
				cVV.copyRow(row, eigenSpace.solutions(), row);
			else gramSchmidt = true; // If there are null rows, we will rely on gramSchmidt to generate an orthogonal matrix 
		}
		if (gramSchmidt) cVV = cVV.gramSchmidtM();
		cVV = cVV.normalize();

		// Matrix U calculation section
		//
		// U: solutions to the equations systems A·V = U·Σ, for each A·V vector (interms). mxm
		// Ui = A·Vi / Σi
		//
		cUU = new MatrixComplex(rowLen); //cUU.initMatrixDiag(1,0);
		MatrixComplex cUU2;
		for (int col = 0; col < colLen; ++col) {
			if (cSS.getItem(col, col).isZero()) {
				cUU2 = cUU.transpose().ker();
				cUU2 = cUU2.gramSchmidtFull();
				cUU2 = cUU2.normalizeByRows();
				for (int col2 = col; col2 < rowLen; ++col2) {
					cUU.copyCol(col2, cUU2.getRow(col2-col).transpose(), 0);
				}
				break;
			}
			else cUU.copyCol(col, (aMatrix.times(cVV.getRow(col).transpose())).divides(cSS.getItem(col, col)), 0);
		}
		cUU.normalize();
		
		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			aMatrix.println(HEAD_METH + "aMatrix 'oriented'");
			bMatrix.println(HEAD_METH + "bMatrix as aMatrix.adjoint().times(aMatrix)");
			eigenSpace.roots().println(HEAD_METH + "eigenSpace.roots()");
			eigenSpace.eigenvalues().println(HEAD_METH + "eigenSpace.eigenvalues()");
			eigenSpace.solutions().println(HEAD_METH + "eigenSpace.solutions()");
			eigenSpace.eigenvectors().println(HEAD_METH + "eigenSpace.eigenvectors()");
			cSS.println(HEAD_METH + "cSS");
			System.out.println("cSS:" +cSS.toMatrixComplex());
			cVV.println(HEAD_METH + "cVV");
			System.out.println("cVV:" +cVV.toMatrixComplex());
			cUU.println(HEAD_METH + "cUU");
			System.out.println("cUU:" +cUU.toMatrixComplex());
			aMatrix.times(cVV).println(HEAD_METH + "aMatrix.times(cVV)");
		}
		/* ------------- END DEBUGGING BLOCK ------------- */
		
		// Solution for oriented (rowLen <= colLen)
		// Otherwise undo the re-orientation 
		if (oriented) {
			this.cU = cVV.transpose();
			this.cV = cUU;
			this.cS = cSS.transpose();
		}
		else {
			this.cU = cUU;
			this.cV = cVV.transpose();
			this.cS = cSS;
		}
		factorized = cU.times(cS).times(cV.adjoint()).equals(this);
	}
	
	/**
	 * factorizeREDUCED()
	 * Σ (kxk): k = min(m,n). Diagonal (A*·A) eigenvalues square root matrix, augmented with zeroes as required. Eigenvals zero are included.
	 * V (nxn): (A*·A) eigenvectors matrix, augmented with some other linearly independent vectors to form a basis of F. If there is no eigenvectors enoug is completed with vectors from the unitary basis. Orthogonalized later if required. 
	 * U (mxn): solutions to the equations systems A·V = U·Σ, for each A·V vector (interms) Ui = A·Vi / Σi
	 */
	private void factorizeREDUCED() {
	final boolean DEBUG_ON = false; 
	final String METH_NAME = "factorize()";
	final String HEAD_METH = HEADINFO + " " + METH_NAME + ":";
	MatrixComplex cSS;
	MatrixComplex cVV;
	MatrixComplex cUU;

	factorized = false;

	// Solution for rowLen > colLen
	// Otherwise works with the adjoint matrix
	MatrixComplex aMatrix = this.orientMatrix();

	int rowLen = aMatrix.rows();
	int colLen = aMatrix.cols();
	int kLen = Math.min(rowLen, colLen);

	MatrixComplex bMatrix = aMatrix.adjoint().times(aMatrix);
	Eigenspace eigenSpace = new Eigenspace(bMatrix);
	// We need the eigenvalues ordered (DOWN) from higher to lower
	if (eigenSpace.order() == Eigenspace.Order.UP) eigenSpace.orderSwap();

	// Matrix Σ calculation section
	//
	// Σ: diagonal eigenvalues square root matrix, augmented with zeroes. mxn
	//
	cSS = new MatrixComplex(kLen);
	for (int i = 0; i < colLen; ++i) {
		Complex root = eigenSpace.roots().getItem(i,0);
		// root.println(HEAD_METH + "root(" + i  +"): ");
		if (root.isZero()) break;
		else cSS.complexMatrix[i][i] = root.power(0.5);
	}

	//Matrix V calculation section
	//
	// V: eigenvectors matrix, augmented with some other linearly independent vectors to form a basis of F nxn
	//
	boolean gramSchmidt = false;
	cVV = new MatrixComplex(colLen); cVV.initMatrixDiag(1,0);
	for (int row = 0; row < eigenSpace.solutions().rows(); ++row) {
		if (!eigenSpace.solutions().isNullRow(row))
			cVV.copyRow(row, eigenSpace.solutions(), row);
		else gramSchmidt = true; // If there are null rows, we will rely on gramSchmidt to generate an orthogonal matrix 
	}
	if (gramSchmidt) cVV = cVV.gramSchmidtFull();
	cVV = cVV.normalize();

	// Matrix U calculation section
	//
	// U: solutions to the equations systems A·V = U·Σ, for each A·V vector (interms). mxm
	// Ui = A·Vi / Σi
	//
	cUU = new MatrixComplex(rowLen, colLen);
	MatrixComplex cUU2;
	for (int col = 0; col < colLen; ++col) {
		if (cSS.getItem(col, col).isZero()) {
			cUU2 = cUU.augment(rowLen - colLen).transpose().ker();
			cUU2 = cUU2.gramSchmidtFull();
			cUU2 = cUU2.normalizeByRows();
			for (int col2 = col; col2 < colLen; ++col2) {
				cUU.copyCol(col2, cUU2.getRow(col2-col).transpose(), 0);
			}
			break;
		}
		else cUU.copyCol(col, (aMatrix.times(cVV.getRow(col).transpose())).divides(cSS.getItem(col, col)), 0);
	}
	cUU.normalize();
	
	/* -------------   DEBUGGING BLOCK   ------------- */
	if (DEBUG_ON) {
		aMatrix.println(HEAD_METH + "aMatrix 'oriented'");
		bMatrix.println(HEAD_METH + "bMatrix as aMatrix.adjoint().times(aMatrix)");
		eigenSpace.roots().println(HEAD_METH + "eigenSpace.roots()");
		eigenSpace.eigenvalues().println(HEAD_METH + "eigenSpace.eigenvalues()");
		eigenSpace.solutions().println(HEAD_METH + "eigenSpace.solutions()");
		eigenSpace.eigenvectors().println(HEAD_METH + "eigenSpace.eigenvectors()");
		cSS.println(HEAD_METH + "cSS");
		System.out.println("cSS:" +cSS.toMatrixComplex());
		cVV.println(HEAD_METH + "cVV");
		System.out.println("cVV:" +cVV.toMatrixComplex());
		cUU.println(HEAD_METH + "cUU");
		System.out.println("cUU:" +cUU.toMatrixComplex());
		aMatrix.times(cVV).println(HEAD_METH + "aMatrix.times(cVV)");
	}
	/* ------------- END DEBUGGING BLOCK ------------- */
	
	// Solution for oriented (rowLen <= colLen)
	// Otherwise undo the re-orientation 
	if (oriented) {
		this.cU = cVV.transpose();
		this.cV = cUU;
		this.cS = cSS.transpose();
	}
	else {
		this.cU = cUU;
		this.cV = cVV.transpose();
		this.cS = cSS;
	}
	factorized = cU.times(cS).times(cV.adjoint()).equals(this);
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
	public SVDmethod getMethod() {
		return this.method;
	}
	
	/**
	 * Gets the class member variable with the V array.
	 * @return The V array of the SVD decomposition.
	 */
	public MatrixComplex getV() {
		return cV;
	}

	/**
	 * Gets the class member variable with the S (Sigma Σ) array.
	 * @return The S array of the SVD decomposition.
	 */
	public MatrixComplex getS() {
		return cS;
	}

	/**
	 * Gets the class member variable with the U array.
	 * @return The U array of the SVD decomposition.
	 */
	public MatrixComplex getU() {
		return cU;
	}

	/**
	 * Returns the value of factorized. favtoriced is true if the S V D matrix have been found
	 * @return Trued if factorized, false otherwise
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
			case FULL: return "FULL";
			case REDUCED: return "REDUCED";
			case IDENTITY: return "IDENTITY";
			case IDENTMIX: return "IDENTMIX";
			case IMAGINARY: return "IMAGINARY";
			default: return "NONE";
		}
	}
	
	/**
	 * Returns the expression for SVD Factorization for Maxima. expand is available. 
	 * @param expand True if you want to expand the expressions
	 * @return The SVD Factorization expression
	 */
	public String toMaxima_dgesvd(boolean expand) {
		String toMaxima;
		toMaxima = "[sigma, U, VT] : dgesvd (" +this.toMaxima()+", true, true)";
		toMaxima = expand ? "expand("+toMaxima+");" : ";";
		return toMaxima;
	}
	
	/**
	 * Returns the expression for generating Sigma from sigma returned from toMaxima_dgesvd.
	 * @return Sigma:genmatrix expression
	 */
	public String toMaxima_Sigma() {
		return "Sigma:genmatrix(lambda ([i, j], if i=j then sigma[i] else 0),length(U), length(VT));";
	}
	
	/**
	 * Returns the expression for SVD Factorization for GNU Octave. 
	 * @return The SVD Factorization expression
	 */
	public String toOctave_svd() {
		String toOctave;
		toOctave = "[U, S, V] = svd("+this.toOctave()+")";
		return toOctave;
	}

	/**
	 * Returns the expression for SVD Factorization for Matlab.
	 * @return The SVD Factorization expression
	 */
	public String toMatlab_svd() {
		String toMatlab;
		toMatlab = "[U, S, V] = svd("+this.toMatlab()+")";
		return toMatlab;
	}

	/**
	 * Returns the expression for SVD Factorization for Wolfram.
	 * @return The SVD Factorization expression
	 */
	public String toWolfram_svd() {
		String toWolfram;
		toWolfram = "{u, s, v} = SingularValueDecomposition["+this.toWolfram()+"]";
		return toWolfram;
	}
}


