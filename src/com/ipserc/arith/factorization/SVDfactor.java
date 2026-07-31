/*
 * Descomposición en valores singulares
 * 
 * /home/ipserc/Documentos/eBooks/Matematicas/SVD/42221-03.pdf
 * /home/ipserc/Documentos/eBooks/Matematicas/SVD/simetricas_y_ortogonales.pdf  (Pag. 34)	
 * /home/ipserc/Documentos/eBooks/Matematicas/SVD/TrefethenBau_Lec4_SVD.pdf
 * /home/ipserc/Documentos/eBooks/Matematicas/SVD/

    Aplicable a: una matriz A m-por-n.

    Factorización: A = U Σ VT, donde Σ es una matriz diagonal mxn, y U y V son matrices ortogonales mxm y nxn 
    respectivamente, siendo VT la traspuesta de V. Los elementos de la diagonal de Σ son los valores singulares de A 
    y son mayores o iguales a cero.

    Notas: a la matriz V Σ + U T , donde Σ + es igual a la matriz Σ reemplazando los valores singulares por sus recíprocos, 
    se le llama pseudoinversa de A.


 */

package com.ipserc.arith.factorization;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vectorcomplex.*;

/**
 * 
 * @author ipserc
 *
 */
public class SVDfactor extends MatrixComplex {
	public static enum SVDmethod {NONE, SVD, REDUCED};
	
	private static Boolean __DEBUG__ = false; 

	private MatrixComplex cS;
	private MatrixComplex cV;
	private MatrixComplex cU;

	private final static String HEADINFO = "SVDfactor --- INFO: ";
	private final static String VERSION = "1.2 (2025_0417_1630)";
	private boolean factorized = false;
	private boolean oriented = false;
	private SVDmethod method = SVDmethod.NONE;

	
	/* VERSION Release Note
	 * 1.2 (2025_0417_1630)
	 * private void factorizeSVD() {
	 * public void factorize() {
	 * REMOVED private void factorizeFULL()
	 * REMOVED private void factorizeIDENTITY() 
	 * REMOVED private void factorizeIDENTMIX() 
	 * REMOVED private void factorizeFULL()
	 * public static enum SVDmethod {SVD, REDUCED};
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
		this.method = method;
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
		this.method = method;
		factorize(method);
	}

	/*
	 * ***********************************************
	 * 	PRIVATE METHODS 
	 * ***********************************************
	 */

	/**
	 * Private method. If the matrix to decompose has less rows the columns this method returns the adjoint of the matrix to do the factorization over it.
	 * NOTA: Usamos la convencion (rowLen < colLen) para caraterizar el metodo como orinted lo que da una matriz V unitaria
	 * Sin embargo, funcion tambien con (rowLen <= colLen) aunque la matriz unitaria es la U, con lo que U·S·V^* no deberia funcionar PERO SÍ LO HACE!!!! 
	 * @return The adjoint matrix to decompose with the SVD factorization.
	 */
	private MatrixComplex orientMatrix() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex oMatrix = this.copy();
		if (rowLen < colLen) {
			oMatrix = oMatrix.adjoint();
			oriented = true;
		}
		else oriented = false;
		return oMatrix;
	}

	private MatrixComplex orientMatrixLE() {
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

	private MatrixComplex orientMatrixGT() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex oMatrix = this.copy();
		if (rowLen > colLen) {
			oMatrix = oMatrix.adjoint();
			oriented = true;
		}
		else oriented = false;
		return oMatrix;
	}

	private MatrixComplex orientMatrixGE() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex oMatrix = this.copy();
		if (rowLen >= colLen) {
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
		this.factorized = false;
		this.method = SVDmethod.NONE;

		factorizeSVD();			if (this.factorized){ this.method = SVDmethod.SVD; return; }
		factorizeREDUCED();		if (factorized){ this.method = SVDmethod.REDUCED; return; }
	}
	
	/**
	 * Does the SVD factorization using the given method as parameter.
	 * @param method The method used for the factorization
	 */
	public void factorize(final SVDmethod method) {
		factorized = false;
		this.method = method;
		switch (method) {
			case SVD		: factorizeSVD(); break;
			case REDUCED	: factorizeREDUCED(); break;
			case NONE		: break;
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
	 * Algorithm source https://www.mate.unlp.edu.ar/practicas/70_18_0911201012951.pdf
	 * Algorithm source https://www.yanivplan.com/files/SVD-from-Lay.pdf
	 */

	/**
	 * factorizeSVD()
	 * /home/ipserc/Documentos/eBooks/Matematicas/SVD/simetricas_y_ortogonales.pdf  (Pag. 34)
	 * Σ (mxn): Diagonal (A'·A) eigenvalues square root matrix, augmented with zeroes as required. Eigenvals zero are included.
	 * U (mxm): (A'·A) eigenvectors matrix. 
	 * V (nxn): Matrix V is assembled by doing the matricial product of the original oriented matrix with the column-normalized matrix U.
	 */

	/**
	 * factorizeREDUCED()
	 * /home/ipserc/Documentos/eBooks/Matematicas/SVD/TrefethenBau_Lec4_SVD.pdf
	 * Σ (kxk): k = min(m,n). Diagonal (A'·A) eigenvalues square root matrix, augmented with zeroes as required. Eigenvals zero are included.
	 * U (nxn): (A'·A) eigenvectors matrix, augmented with some other linearly independent vectors to form a basis of F. If there is no eigenvectors enoug is completed with vectors from the unitary basis. Orthogonalized later if required. 
	 * V (mxn): solutions to the equations systems A·V = U·Σ, for each A·V vector (interms) Ui = A·Vi / Σi
	 */
	private void factorizeREDUCED() {
		final String METH_NAME = "factorizeREDUCED()";
		final String HEAD_METH = HEADINFO + " " + METH_NAME + ":";
		MatrixComplex cSS;
		MatrixComplex cVV;
		MatrixComplex cUU;

		factorized = false;

		// Solution for rowLen >= colLen
		// Otherwise works with the adjoint matrix
		MatrixComplex aMatrix = this.orientMatrix();

		int rowLen = aMatrix.rows();
		int colLen = aMatrix.cols();
		int kLen = Math.min(rowLen, colLen); // Shold be colLen

		MatrixComplex bMatrix = aMatrix.adjoint().times(aMatrix);

		if (__DEBUG__) {
			System.out.println(HEAD_METH + "OCTAVE bMatrix:" + bMatrix.toOctave());
		}

		// We need the eigenvalues ordered (DOWN) from higher to lower
		Eigenspace.setOrderDOWN();
		Eigenspace eigenSpace = new Eigenspace(bMatrix);
		
		if (__DEBUG__) {
			eigenSpace.check();
		}

		// Matrix Σ calculation section
		//
		// Σ{nxn}: diagonal eigenvalues square root matrix, NOT augmented with zeroes.
		//
		cSS = new MatrixComplex(kLen);
		for (int i = 0; i < colLen; ++i) {
			Complex root = eigenSpace.roots().getItem(i,0);
			if (root.isZero()) break;
			else cSS.complexMatrix[i][i] = root.power(0.5);
		}

		//Matrix V calculation section
		//
		// V{nxn}: eigenvectors matrix, augmented with some other linearly independent vectors to form a basis of F 
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
		// U{mxn}: solutions to the equations systems A·V = U·Σ, for each A·V vector (interms). mxm
		// Ui = A·Vi / Σi
		//
		cUU = new MatrixComplex(rowLen, colLen);
		MatrixComplex cUU2;
		for (int col = 0; col < colLen; ++col) {
			if (cSS.getItem(col, col).isZero()) {
				// Beware with this code
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
		if (__DEBUG__) {
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
	 * factorizeSVD()
	 * /home/ipserc/Documentos/eBooks/Matematicas/SVD/simetricas_y_ortogonales.pdf  (Pag. 34)
	 * Σ (mxn): Diagonal (A'·A) eigenvalues square root matrix, augmented with zeroes as required. Eigenvals zero are included.
	 * U (mxm): (A'·A) eigenvectors matrix. 
	 * V (nxn): Matrix V is assembled by doing the matricial product of the original oriented matrix with the column-normalized matrix U.
	 * El unico caso en que no da matrices unitarias es cuando no tiene ningun autovector asociado a los autovalores
	 * por ejemplo: new MatrixComplex("+5,-9,-2;-2,+5,+2;+6,+2,+4");
	 */
	private void factorizeSVD() {
		final String METH_NAME = "factorizeSVD()";
		final String HEAD_METH = HEADINFO + " " + METH_NAME + ":";
		MatrixComplex cSS;
		MatrixComplex cVV;
		MatrixComplex cUU;

		this.factorized = false;

		// Solution for Matrices rowLen >= colLen (mxn, p = min(m, n))
		// Otherwise it works with the adjoint matrix
		// MatrixComplex aMatrix = this.orientMatrix();
		MatrixComplex aMatrix = this.orientMatrix();

		// For short
		int rowLen = aMatrix.rows();
		int colLen = aMatrix.cols();

		// 1. Dada la matriz A ∈ C{m×n} formamos la matriz A'xA (A': matriz adjunta)
		// 1. Given the matrix A ∈ C{m×n} we form the matrix A'xA (A': adjoint matrix)
		MatrixComplex AAT = aMatrix.adjoint().times(aMatrix);

		if (__DEBUG__) {
			System.out.println(HEAD_METH + "OCTAVE AAT:"+AAT.toOctave());
		}

		// 2. Resolvemos el problema de autovalores para A'xAxu = λ·u. Esto siempre se puede hacer porque A'xA es Hermitiana.
		//    Necesitamos los valores propios ordenados (HACIA ABAJO) de mayor a menor. Eigenspace.setOrderDOWN();
		//    Esto es para identificar correctamente los autovalores cero a la hora de construir las soluciones.
		//    roots() es el conjunto de raíces ordenadas; eigenvalues() es para presentaciones.
		//    solutions() es el conjunto de soluciones ordenadas; eigenvectors() es para presentaciones.
		//    solutions() Se calcula normalizado Eigenspace.setNormalize(true);
		// 2. We solve the eigenvalue problem for A'xAxu = λ u. This can always be done because A'xA is Hermitian.
		//    We need the eigenvalues ​​ordered (DOWN) from largest to smallest. Eigenspace.setOrderDOWN();
		//    This is to correctly identify zero eigenvalues ​​when constructing solutions.
		//    roots() is the set of ordered roots; eigenvalues() is for presentation.
		//    solutions() is the set of ordered solutions; eigenvectors() is for presentation.
		//    solutions() is computed normalized. Eigenspace.setNormalize(true);
		Eigenspace.setOrderDOWN();
		Eigenspace.setNormalize(true);
		Eigenspace AATeigen = new Eigenspace(AAT);

		if (__DEBUG__) {
			AATeigen.check();
		}
		
		// Seccion de calculo de la Matriz Σ {mxn}
		// 3. Calculanos la matriz Σ {mxn} como una matriz diagonal con los valores de las raices cuadradas de los autovalores λ[i]
		//    Como AxA' es semidefinida positiva, los λ[j]:(j = 1, ..., m) que hallamos son no negativos y podemos definir σ[j] = squareroot(λ[j]).
		//    Teniendo los σ[j] ordenados de manera decreciente (por conveniencia, no es necesadrio), montamos la matriz Σ {mxn} colo cando
		//    cada σ[j] en la posicion Σ[j][j] de la matriz Σ. Esto se hace hasta agotar los σ que concidira con el número de columnas de la matriz
		//    y que coincidira con el número de σ[j] distintos de cero.
		//    La matriz Σ {mxn} se termina añadiendo filas de vectores 0 hasta completar a m. 
		// Matrix Σ{mxn} calculation section
		// 3. Let's calculate the matrix Σ{mxn} as a diagonal matrix with the square root values ​​of the eigenvalues ​​λ[i]
		//    Since AxA' is positive semidefinite, the λ[j]:(j = 1, ..., m) we find are non-negative, and we can define σ[j] = squareroot(λ[j]).
		//    Having the σ[j] sorted in decreasing order (for convenience, this is not necessary), we assemble the matrix Σ{mxn} by placing
		//    each σ[j] in position Σ[j][j] in the matrix Σ. This is done until the σ that matches the number of columns in the matrix are exhausted
		//    and that matches the number of non-zero σ[j].
		//    The matrix Σ {mxn} is completed by adding rows of 0 vectors until m is complete.
		cSS = new MatrixComplex(rowLen, colLen);
		for (int i = 0; i < colLen; ++i) {
			Complex root = AATeigen.roots().getItem(i,0);
			cSS.complexMatrix[i][i] = root.power(0.5);
		}
		
		// Seccion de calculo de la Matriz U {nxn}
		// 4. Teniendo los σ[j] ordenados de manera decreciente, armamos la matriz U ∈ C{n×n} con los vectores u[j] que son
		//    los autovectores A'xA correspondientes a los autovalores usado para Σ NORMALIZADOS.
		//    Recordar que AATeigen.solutions() ya etá normalizado
		//    La operación se realiza recuperando las soluciones de A'xA que son vectores fila y colocandolos en la matriz.
		//    Si los autovectores son nulos se recurre a tomar otro juego de vectores con la condición de que sean ortonormales.
		//    Como la matriz es cuadrada se transpone para ponerlo como columnas.
		// Matrix calculation section U {nxn}
		// 4. Having the σ[j] sorted in decreasing order, we build the matrix U ∈ C{n×n} with the vectors u[j] which are
		//    the eigenvectors A'xA corresponding to the eigenvalues ​​used for NORMALIZED Σ.
		//    Remember that AATeigen.solutions() is already normalized
		//    The operation is performed by retrieving the solutions of A'xA which are row vectors and placing them in the matrix.
		//    If the eigenvectors are zero, another set of vectors is taken with the condition that they are orthonormal.
		//    Since the matrix is ​​square, it is transposed to put it as columns.
		/* 
		 * MatrixComplex entiende las columnas como los componentes axiales del espacio vectorial o de coordenadas
		 * Por lo tanto las operaciones se realizan por filas
		 */
		boolean gramSchmidt = false;
		cUU = new MatrixComplex(colLen);
		cUU.initMatrixDiag(1,0);
		int rowU; 
		for (rowU = 0; rowU < AATeigen.solutions().rows(); ++rowU) {
			if (!AATeigen.solutions().isNullRow(rowU))
				cUU.copyRow(rowU, AATeigen.solutions(), rowU);
			else {
				gramSchmidt = true;
				break;
			}
		}
		if (gramSchmidt) {
			// Se construye el resto de la matriz cUU añadiendo vectores ortonormales a la cUU calculada en el paso anterior
			// zk = vk − SUM[j=1,k-1](<vk , zj>/<zj , zj>*zj) con (k = 1, 2, . . . , n)
			// Los zj están en la cVV calculada anteriormente
			// zj se normaliza antes de introducirlo en cUU como un vector fila
			MatrixComplex newRow = new MatrixComplex(1, colLen);
			//for (int col = colLen; col < rowLen; ++col) {
			for (int row = rowU; row < colLen; ++row) {
				newRow.initMatrixRandomReal();
				VectorComplex vk = new VectorComplex(newRow);
				VectorComplex sum = new VectorComplex(colLen);
				// Los zj están en la cUU calculada anteriormente
				// Hacemos el SUM[j=1,k-1](<vk , zj>/<zj , zj>*zj) con (k = 1, 2, . . . , col)
				for (int k = 0; k < row; ++k) {
					VectorComplex zj = new VectorComplex(cUU.getRow(k));
					Complex c1 = vk.dotprod(zj); 			// Esto es <vk , zj>
					double d1 = Math.pow(zj.mod(),2.0); 	// Esto es <zj , zj>
					sum = sum.plus(zj.prod(c1).div(d1));
				}
				VectorComplex zk = vk.minus(sum);
				if (Eigenspace.getNormalice()) 
					zk = zk.normalize();
				cUU.setRow(row, zk);
			}
		}

		if (__DEBUG__) {
			cUU.println(HEAD_METH + "cUU");
			System.out.println(HEAD_METH + "cUU is BASE orthogonal:" + VectorComplex.isOrthogonal(cUU));
			System.out.println(HEAD_METH + "cUU is Unitary:" + cUU.isUnitary());
		}

		cUU = cUU.transpose();

		if (__DEBUG__) {
			cUU.println(HEAD_METH + "cUU transpose");
			System.out.println(HEAD_METH + "cUU is BASE orthogonal:" + VectorComplex.isOrthogonal(cUU));
			System.out.println(HEAD_METH + "cUU is Unitary:" + cUU.isUnitary());
		}
		/* */

		// Seccion de calculo de la Matriz V {nxn}
		// 5. La matriz V se monta realizando el producto de la matriz original orientada por la matriz U que fue calculada en el paso anterior.
		//    El resultado se normaliza por columnas.
		//    Como el número de filas puede superar al de columnas, puede ser nececesadio ampliar la matriz V con vectores ortogonales
		//    a los que ya se tienen.
		// Matrix V {nxn} calculation section
		// 5. The matrix V is assembled by multiplying the original oriented matrix by the matrix U calculated in the previous step.
		//    The result is normalized by columns.
		//    Since the number of rows may exceed the number of columns, it may be necessary to extend matrix V with orthogonal vectors
		//    to those already available.

		// cVV armado con los autovectores previamente normalizados
		/* */
		cVV = new MatrixComplex(rowLen, rowLen);
		int colV;
		for (colV = 0; colV < colLen; ++colV) {
			if (cSS.getItem(colV, colV).isZero()) break;
			cVV.copyCol(colV, (aMatrix.times(cUU.getCol(colV))).divides(cSS.getItem(colV, colV)), 0);
		}
		/* */

		/* */
		// OJO TO-DO SE OPERA POR COLUMNAS. cUU ES UNA MATRIZ DE VECTORES COLUMNA
		// Se construye el resto de la matriz cVV añadiendo vectores ortonormales a la cVV calculada en el paso anterior
		// zk = vk − SUM[j=1,k-1](<vk , zj>/<zj , zj>*zj) con (k = 1, 2, . . . , n)
		// Los zj están en la cVV calculada anteriormente
		// zj se normaliza antes de introducirlo en cVV como un vector columna
		MatrixComplex newCol = new MatrixComplex(1, rowLen);
		for (int col = colV; col < rowLen; ++col) {
			newCol.initMatrixRandomReal();
			VectorComplex vk = new VectorComplex(newCol);
			VectorComplex sum = new VectorComplex(rowLen);
			// Los zj están en la cVV calculada anteriormente
			// Hacemos el SUM[j=1,k-1](<vk , zj>/<zj , zj>*zj) con (k = 1, 2, . . . , col)
			for (int k = 0; k < col; ++k) {
				VectorComplex zj = new VectorComplex(cVV.getCol(k).transpose());
				Complex c1 = vk.dotprod(zj);			// Esto es <vk , zj>
				double d1 = Math.pow(zj.mod(),2.0); 	// Esto es <zj , zj>
				sum = sum.plus(zj.prod(c1).div(d1));
			}
			VectorComplex zk = vk.minus(sum);
			cVV.setCol(col, zk.normalize().transpose());
		}
		/* */

		if (__DEBUG__) {
			cVV.println(HEAD_METH + "cVV");
			System.out.println(HEAD_METH + "cVV is BASE orthogonal:" + VectorComplex.isOrthogonal(cVV));
			System.out.println(HEAD_METH + "cVV is Unitary:" + cVV.isUnitary());
			System.out.println(HEAD_METH + "cVV transpose is BASE orthogonal:" + VectorComplex.isOrthogonal(cVV.transpose()));
			System.out.println(HEAD_METH + "cVV transpose is Unitary:" + cVV.transpose().isUnitary());
			cVV.adjoint().println(HEAD_METH + "cVV.adjoint()");
			cVV.inverse().println(HEAD_METH + "cVV.inverse()");
		}
		/* */

		
		// Reorentación de las soluciones
		// 6. La solución que se obtiene es para la matriz A{mxn}, en la que el numero de filas m es mayor o igual al numero de filas (n). m >= n
		//    En caso contrario, la solución hallada es para la matriz transpuesta A.traspuesta {nxm} de n filas y m columnas
		//    Si se ha realizado esta trasposición hay que deshacerla. La mariz S es la S calculada traspuesta.
		//    En caso contrario las matrices U y V están cruzadas y hay que intercambiarlas.
		// Reorientation of the solutions
		// 6. The solution obtained is for the matrix A{mxn}, in which the number of rows m is greater than or equal to the number of rows (n). m >= n
		//    Otherwise, the solution found is for the transposed matrix A.transpose {nxm} of n rows and m columns.
		//    If this transposition has been performed, it must be undone. The matrix S is the calculated S transposed.
		//    Otherwise the matrices U and V are crossed and must be exchanged.
		//
		// Solution for oriented (rowLen <=colLen)
		// Otherwise undo the re-orientation 
		if (oriented) {
			this.cU = cUU;
			this.cV = cVV;
			this.cS = cSS.transpose();
		}
		else {
			this.cU = cVV;
			this.cV = cUU;
			this.cS = cSS;
		}
		
		if (__DEBUG__) {
			cS.println(HEAD_METH + "cS");
			cV.println(HEAD_METH + "cV");
			cU.println(HEAD_METH + "cU");
		
			this.println(HEAD_METH + "THIS");
			cU.times(cS).times(cV.adjoint()).println(HEAD_METH + "USV*");
			System.out.println(HEAD_METH + "cU.toMatrixComplex:"+cU.toMatrixComplex());
			System.out.println(HEAD_METH + "cU.isOrthogonal:"+cU.isOrthogonal());
		}

		this.factorized = cU.times(cS).times(cV.adjoint()).equals(this);
	}
	
	/*
	 * ***********************************************
	 * 	SETTERS
	 * ***********************************************
	 */

	public static void debugON() {
		__DEBUG__ = true;
	}

	public static void debugOFF() {
		__DEBUG__ = false;
	}

	/*
	 * ***********************************************
	 * 	GETTERS
	 * ***********************************************
	 */

	 public static boolean debug() {
		return __DEBUG__;
	}

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
	 * Returns the value of factorized. factoriced is true if the S V D matrix have been found
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
			case REDUCED: 	return "REDUCED";
			case SVD:		return "SVD";
			default: 		return "NONE";
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
