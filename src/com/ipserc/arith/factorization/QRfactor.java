/*
 * Factorización QR o triangularización ortogonal

    Aplicable a: una matriz A m por n.

    Factorización: A = Q R donde Q es una matriz ortogonal m por m, y R es una matriz triangular superior m por n.

    Métodos de cálculo: La factorización QR puede calcularse mediante el proceso de ortogonalización de Gram-Schmidt 
    aplicado a las columnas de A, mediante el uso de transformaciones de Householder y mediante transformaciones de Givens.

    Notas: La factorización QR puede utilizarse para "resolver" el sistema de ecuaciones lineales Ax = b cuando el 
    número de ecuaciones es distinto al de incógnitas.

 */

package com.ipserc.arith.factorization;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.complex.*;

/**
 * 
 * @author ipserc
 *
 */
public class QRfactor extends MatrixComplex {
	private MatrixComplex cR;
	private MatrixComplex cQ;
	private boolean factorized = false;

	private final static String HEADINFO = "QRfactor --- INFO: ";
	private final static String VERSION = "1.4 (2026_0810_2330)";
	/* VERSION Release Note
	 *
	 * 1.4 (2026_0810_2330)
	 * Auditoria matematica dedicada (Vigesimosexta sesion, bloque 4 de la hoja de ruta
	 * "Matematicas Aplicadas", ver Claude/ComplexArithRev.md): qrGramSchmidt()/qrGramSchmidtFull()/
	 * qrGramSchmidtM()/qrGramSchmidtMFull() fallaban con Q de forma incorrecta para cualquier
	 * matriz genuinamente rectangular (confirmado: Q de 2x2 en vez de 3x2 para una entrada 3x2,
	 * Q*R indefinido/Infinity) -- causa raiz en MatrixComplexOrtho.gramSchmidt() y hermanos (ver
	 * MatrixComplex.VERSION 1.70), mas un desajuste propio de esta clase: esos 4 metodos
	 * ortogonalizan las FILAS de su argumento (convencion ya establecida en el resto del proyecto),
	 * pero QR necesita las COLUMNAS de la matriz ortogonalizadas -- invisible para matrices
	 * cuadradas (Q*R=A se cumple trivialmente para cualquier Q unitaria de la forma correcta, dado
	 * que R se define como Q^H*A), pero necesario para el caso rectangular. Arreglado transponiendo
	 * antes y despues de llamar a gramSchmidt()/etc., sin tocar el contrato de esos 4 metodos
	 * compartidos. qrHouseholder() ya funcionaba correctamente para matrices rectangulares (no
	 * tocado). Verificado con ScratchFactorizationAudit01.java (src/TestComplex/, conservado): los
	 * 5 metodos dan Q semi-unitaria/unitaria y A==Q*R exactos (~1e-14) para una matriz 3x2 real,
	 * ademas de una bateria de 65 ficheros consumidores sin regresiones (ver MatrixComplex.VERSION
	 * 1.70 para el detalle completo de la verificacion).
	 *
	 * 1.3 (2026_0807_2330)
	 * Auditoria de com.ipserc.arith.factorization.QRfactor (ver Claude/ComplexArithRev.md), 3
	 * hallazgos reales confirmados en ejecucion, todos arreglados:
	 * (1) QRfactor(MatrixComplex): mismo bug de aliasing por clone() superficial ya arreglado en
	 * Schurfactor/LUfactor esta sesion. Arreglado con matrix.copy().complexMatrix.
	 * (2) qrHouseholder() lanzaba NullPointerException con una matriz de 1 fila: el bucle
	 * "k<colLen && k<rowLen-1" nunca se ejecuta cuando rowLen==1 (rowLen-1==0), dejando q[0] sin
	 * asignar antes de "cQ=q[0]". Una matriz de 1 fila tiene una QR trivial y valida (Q=identidad,
	 * no hay nada por debajo de la diagonal que eliminar) -- arreglado con cQ=I (la identidad ya
	 * construida) cuando k==0 tras el bucle.
	 * (3) cleanCR() era codigo muerto -- cero llamadores en todo el proyecto, ni siquiera dentro de
	 * la propia clase. Eliminado (tenia ademas un bug propio, solo limpiaba la ultima columna en
	 * vez de todas las columnas de las filas por debajo de la diagonal -- irrelevante al ser
	 * inalcanzable).
	 * Investigado y descartado: la cancelacion catastrofica de qrHouseholder() (misma convencion de
	 * signo ya diagnosticada y arreglada en QRSchurfactor/Hessenbergfactor, pero solo bajo
	 * reaplicacion iterativa cerca de la convergencia). Probado con una columna deliberadamente
	 * casi alineada con su propio eje (~1e-10 de desviacion): reconstruccion y ortogonalidad ambas
	 * a precision de maquina (~1e-15) -- confirma que no es un problema real para el uso de una
	 * sola pasada que esta clase hace de si misma.
	 *
	 * 1.2 (2026_0802_0824)
	 * signHH(Complex) widened from private to package-private static so Hessenbergfactor can reuse it.
	 *
	 * 1.1 (2022_0209_2130)
	 * public void qrGramSchmidt()
	 * public void qrGramSchmidtFull()
	 * public void qrGramSchmidtM()
	 * public void qrGramSchmidtMFull() 
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
	 * Instantiates a complex square array of length len.
	 * @param len the length of the square array.
	 */
	public QRfactor(int len) {
		super(len);
	}

	/**
	 * Instantiates a complex array of dimensions rowLen x colLen
	 * @param rowLen The number of rows.
	 * @param colLen The number of columns.
	 */
	public QRfactor(int rowLen, int colLen) {
		super(rowLen, colLen);
	}

	/**
	 * Instantiates a complex array from a string, rows are separated with ";", columns are separated with ","
	 * @param strMatrix The string with the rows and cols
	 */
	public QRfactor(String strMatrix) {
		super(strMatrix);
	}

	/**
	 * Instantiates a QRfactor array from a MatrixComplex
	 * @param matrix the MatrixComplex already instantiated.
	 */
	public QRfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.copy().complexMatrix;
	}

	/*
	 * ***********************************************
	 * 	PRIVATE METHODS
	 * ***********************************************
	 */

	/**
	 * Package-private Method (widened from private so {@code Hessenbergfactor} can reuse it without
	 * duplicating the same Householder sign convention). Aproximation of the sign function for complex
	 * numbers to use in the Housholder decomposition.
	 * @param complexMatrix Complex number to evalute the sign.
	 * @return The sign evaluated.
	 */
	static Complex signHH(Complex complexMatrix) {
		Complex signHH = new Complex();
		signHH = Complex.signP(complexMatrix);
		signHH.setComplexPol(1, signHH.pha());
		return signHH;
	}

	/**
	 * Private Method. Changes the sign of the last row if the number of iteration is odd.
	 * This method is used to force the sign of the determinant of Q to be positive.
	 * @param k The number of iterations.
	 */
	private void QcheckSign(int k) {
		int rowLen = cQ.complexMatrix.length;
		int colLen = cQ.complexMatrix[0].length;
		//if ( k%2 != 0 ) {
		if ((k & 1) != 0) {	
			for (int i = 0; i < colLen; ++i)
				cQ.complexMatrix[rowLen-1][i] = cQ.complexMatrix[rowLen-1][i].opposite(); 
		}
	}

	/*
	 * ***********************************************
	 * 	METHODS
	 * ***********************************************
	 */

	/**
	 * QR decomposition using the Housholder transformation. Factorices the array using the QR decomposition.
	 * QR decomposition (also called a QR factorization) of a matrix is a decomposition of a matrix A into a product A = QR of an orthogonal matrix Q and an upper triangular matrix R. 
	 * QR decomposition is often used to solve the linear least squares problem, and is the basis for a particular eigenvalue algorithm, the QR algorithm.
	 * QR algorithm is an eigenvalue algorithm: that is, a procedure to calculate the eigenvalues and eigenvectors of a matrix. 
	 * The QR transformation was developed in the late 1950s by John G. F. Francis and by Vera N. Kublanovskaya, working independently.
	 * The basic idea is to perform a QR decomposition, writing the matrix as a product of an orthogonal matrix and an upper triangular matrix, multiply the factors in the reverse order, and iterate.
	 * [Source Wikipedia]
	 */
	public void qrHouseholder() {
		int rowLen = this.complexMatrix.length;
		int colLen = this.complexMatrix[0].length;
		MatrixComplex[] q = new MatrixComplex[rowLen];
		MatrixComplex z = this.copy();
		int k;

		factorized = false;

		MatrixComplex I = new MatrixComplex(rowLen, rowLen);
		I.initMatrixDiag(1, 0);

		for (k = 0; k < colLen && k < rowLen - 1; ++k) {
			MatrixComplex e = new MatrixComplex(rowLen,1);
			MatrixComplex x = new MatrixComplex(rowLen,1);
			MatrixComplex z1 = new MatrixComplex(rowLen, colLen);
			for (int i = 0; i < k; ++i) z1.complexMatrix[i][i].setComplexPol(1, 0);
			for (int i = k; i < rowLen; ++i)
				for (int j = k; j < colLen; ++j)
					z1.complexMatrix[i][j] = z.complexMatrix[i][j];
			z = z1;
			for (int i = 0; i < z.complexMatrix.length; ++i) x.complexMatrix[i][0] = z.complexMatrix[i][k];
			//sign(x1)||x||exp(iθ)
			Complex xNorm = (signHH(x.complexMatrix[k][0])).times(x.norm());
			for (int i = 0; i < rowLen; ++i) e.complexMatrix[i][0].setComplexPol((i == k)?1:0, 0);
			//v = sign(x[k])||x||exp(iθ)·e[k] - x
			e = (e.times(xNorm)).minus(x);
			double eNorm = e.norm();
			//u = v/||v||
			e = e.divides(eNorm);
			q[k] = new MatrixComplex(rowLen, rowLen);
			// q[k] = I-2·u·u* (conjugate transpose)
			q[k] = I.minus(e.times(e.adjoint()).times(2));
			z = q[k].times(z);
		}
		// k stays 0 (no reflector ever built, q[0] left null) when rowLen==1: there is nothing
		// below the diagonal to eliminate for a single-row matrix, so Q is trivially the identity.
		cQ = (k == 0) ? I : q[0];
		for (int i = 1; i < k; ++i) cQ = q[i].times(cQ) ;
		this.QcheckSign(k); //Makes of Q an unitary "special" matrix --> det(Q) = 1
		cR = cQ.times(this);
		cQ = cQ.adjoint();

		factorized = true;
	}

	/**
	 * QR decomposition using the Gram - Schmidt factorization. Factorices the array using the QR decomposition.
	 * @apiNote BUG FIXED (Vigesimosexta sesion, auditoria matematica): {@code
	 * MatrixComplexOrtho.gramSchmidt()} (and its 3 siblings) orthogonalize their argument's ROWS
	 * (the vector-per-row convention used throughout this codebase, e.g. {@code
	 * Eigenspace.solutions()}/{@code VectorComplex.base()} -- see that method's own apiNote), but a
	 * QR factorization needs {@code this}'s COLUMNS orthogonalized into {@code Q}. For a SQUARE
	 * matrix this distinction was invisible ({@code cR:=Q^H*this} makes {@code Q*R=this} hold for
	 * ANY unitary {@code Q} of the right shape, and rows/columns have the same count) -- but for a
	 * genuinely rectangular matrix, calling {@code this.gramSchmidt()} directly orthogonalized the
	 * wrong-shaped set of vectors entirely, producing a {@code Q} that couldn't even multiply
	 * against {@code this} (confirmed: 2x2 {@code Q} for a 3x2 input, {@code cR} calculation
	 * throwing/producing {@code Infinity}). Fixed by transposing before AND after: {@code
	 * this.transpose()} feeds {@code gramSchmidt()} what it needs to treat as "rows" (this
	 * matrix's actual columns), and the trailing {@code .transpose()} flips the result back into a
	 * proper {@code this.rows() x k} column-oriented {@code Q}. {@code normalize()}
	 * ({@code normalizeByRows()}) is applied BEFORE that final transpose, while each of this
	 * matrix's columns is still oriented as a row -- normalizing rows there is exactly normalizing
	 * what will become {@code Q}'s columns.
	 */
	public void qrGramSchmidt() {
		factorized = false;
		cQ = this.transpose().gramSchmidt().normalize().transpose();
		cR = cQ.adjoint().times(this);
		factorized = true;
	}

	/**
	 * QR decomposition using the Gram - Schmidt Full factorization. Factorices the array using the QR decomposition.
	 * @apiNote BUG FIXED (Vigesimosexta sesion, auditoria matematica): see {@link
	 * #qrGramSchmidt()}'s apiNote -- identical fix (transpose before and after).
	 */
	public void qrGramSchmidtFull() {
		factorized = false;
		cQ = this.transpose().gramSchmidtFull().normalize().transpose();
		cR = cQ.adjoint().times(this);
		factorized = true;
	}

	/**
	 * QR decomposition using the Gram - Schmidt Modified factorization. Factorices the array using the QR decomposition.
	 * @apiNote BUG FIXED (Vigesimosexta sesion, auditoria matematica): see {@link
	 * #qrGramSchmidt()}'s apiNote -- identical fix (transpose before and after).
	 */
	public void qrGramSchmidtM() {
		factorized = false;
		cQ = this.transpose().gramSchmidtM().normalize().transpose();
		cR = cQ.adjoint().times(this);
		factorized = true;
	}

	/**
	 * QR decomposition using the Gram - Schmidt Modified Full factorization. Factorices the array using the QR decomposition.
	 * @apiNote BUG FIXED (Vigesimosexta sesion, auditoria matematica): see {@link
	 * #qrGramSchmidt()}'s apiNote -- identical fix (transpose before and after).
	 */
	public void qrGramSchmidtMFull() {
		factorized = false;
		cQ = this.transpose().gramSchmidtMFull().normalize().transpose();
		cR = cQ.adjoint().times(this);
		factorized = true;
	}
	
	/*
	 * ***********************************************
	 * 	GETTERS
	 * ***********************************************
	 */

	/**
	 * Gets the class member variable with the Q array.
	 * @return The Q array result of the QR decomposition.
	 */
	public MatrixComplex Q() {
		return cQ;
	}

	/**
	 * Gets the class member variable with the R array.
	 * @return The R array result of the QR decomposition.
	 */
	public MatrixComplex R() {
		return cR;
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
	 * Returns the expression for QR Factorization for Maxima. 
	 * @return The QR Factorization expression
	 */
	public String toMaxima_qr() {
		String toMaxima;
		System.out.println("MAXIMA :load (lapack)$");
		toMaxima = "[Q, R]:dgeqrf("+this.toMaxima()+")";
		return toMaxima;
	}

	/**
	 * Returns the expression for QR Factorization for GNU Octave. 
	 * @return The QR Factorization expression
	 */
	public String toOctave_qr() {
		String toOctave;
		toOctave = "[Q, R]=qr("+this.toOctave()+")";
		return toOctave;
	}

	/**
	 * Returns the expression for QR Factorization for Matlab. 
	 * @return The QR Factorization expression
	 */
	public String toMatlab_qr() {
		String toMatlab;
		toMatlab = "[Q, R]=qr(("+this.toMatlab()+")";
		return toMatlab;
	}

	/**
	 * Returns the expression for QR Factorization for Wolfram. 
	 * @return The QR Factorization expression
	 */
	public String toWolfram_QRdecomposition() {
		String toWolfram;
		toWolfram = "QRDecomposition["+this.toWolfram()+"]";
		return toWolfram;
	}

}
