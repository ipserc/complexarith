package com.ipserc.arith.factorization;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Reduction to upper Hessenberg form by unitary similarity.
 * <p>
 * Applicable to: an n-by-n complex square matrix A.
 * <p>
 * Factorization: A = Q*H*Q^H, where Q is n-by-n unitary and H is upper Hessenberg (every entry
 * below the subdiagonal, H[i][j] with i&gt;j+1, is zero).
 * <p>
 * Computation method: n-2 complex Householder reflectors, each one annihilating the subcolumn
 * below the subdiagonal in one column, applied by similarity (H := Qk*H*Qk, not just from the
 * left as in QR factorization) to preserve the eigenvalues of the original matrix at every step.
 * <p>
 * This is Stage 1 of the QR-with-shifts roadmap (see plan mutable-rolling-stardust.md): the
 * standard LAPACK/MATLAB method to obtain eigenvalues without ever forming the characteristic
 * polynomial explicitly (forming it is itself numerically treacherous -- a Wilkinson result).
 * This stage only reduces to Hessenberg form; it does not compute eigenvalues yet (that is
 * Stage 2, QR iteration with deflation).
 * @author ipserc
 */
public class Hessenbergfactor extends MatrixComplex {

	private final static String HEADINFO = "Hessenbergfactor --- INFO: ";
	private final static String VERSION = "1.4 (2026_0811_2205)";

	private boolean factorized = false;

	private MatrixComplex cQ;
	private MatrixComplex cH;

	/* VERSION Release Note
	 *
	 * 1.4 (2026_0811_2205)
	 * Reportado por el usuario tras revisar el HTML generado: la clase no tenia Javadoc real (la
	 * cabecera con la explicacion matematica era un comentario de bloque normal en espanol, no
	 * Javadoc, y estaba antes del package/import sin pegar a la declaracion de la clase), asi que
	 * su pagina en doc/ salia sin descripcion -- mismo patron ya visto y arreglado en
	 * Eigenspace.java. Traducida al ingles y convertida en el Javadoc real de la clase, sin
	 * cambios funcionales.
	 *
	 * 1.3 (2026_0809_1018)
	 * El chequeo de vector despreciable en la reduccion de Householder usaba Complex.zero(), otro
	 * sitio dependiente del modo EXACT/APPROXIMATED global (ahora retirado del todo, ver
	 * Claude/ComplexArithRev.md, Vigesimosegunda sesion) -- sustituido por
	 * Complex.zero_treshold_exact() (fijo).
	 * 1.2 (2026_0807_2359)
	 * Hessenbergfactor(MatrixComplex): mismo bug de aliasing por clone() superficial encontrado en
	 * Schurfactor/LUfactor/QRfactor esta sesion -- "matrix.complexMatrix.clone()" comparte las
	 * filas del array con la matriz original del llamador. Arreglado con
	 * matrix.copy().complexMatrix.
	 *
	 * 1.1 (2026_0802_0932)
	 * factorize(): reflector de Householder usa la convencion de signo numericamente estable
	 * (alpha=-sign(x0)*||x||) para evitar cancelacion catastrofica bajo reaplicacion iterativa
	 * (descubierto al construir QRSchurfactor, Etapa 2 del plan QR-con-desplazamientos).
	 *
	 * 1.0 (2026_0802_0824)
	 * public Hessenbergfactor(String strMatrix)
	 * public Hessenbergfactor(MatrixComplex matrix)
	 * public void factorize()
	 * public MatrixComplex getQ()
	 * public MatrixComplex getH()
	 * public boolean factorized()
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
	 * Instantiates and factorizes a square complex matrix expressed as a String.
	 * @param strMatrix The matrix in String format (rows separated by ";", columns by ",").
	 */
	public Hessenbergfactor(String strMatrix) {
		super(strMatrix);
		factorize();
	}

	/**
	 * Instantiates and factorizes a square complex matrix already built as a MatrixComplex.
	 * @param matrix The matrix to factorize.
	 */
	public Hessenbergfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.copy().complexMatrix;
		factorize();
	}

	/*
	 * ***********************************************
	 * 	METHODS
	 * ***********************************************
	 */

	/**
	 * Reduce la matriz a forma de Hessenberg superior mediante n-2 reflectores de Householder
	 * complejos aplicados por semejanza unitaria (Qk es hermitica y unitaria, Qk^H = Qk):
	 * <pre>
	 *   H_0 = A
	 *   H_(k+1) = Qk * H_k * Qk        (columna k queda con ceros por debajo de la subdiagonal)
	 *   Q = Q0 * Q1 * ... * Q(n-3)
	 * </pre>
	 * de modo que al final {@code A = Q*H*Q^H}. Si un paso ya tiene la subcolumna nula por debajo
	 * de la subdiagonal (columna ya en forma), ese reflector se omite (equivale a la identidad).
	 * Deja el flag {@code factorized} en {@code true} si termina sin excepcion.
	 * @throws IllegalArgumentException si la matriz no es cuadrada.
	 */
	public void factorize() {
		factorized = false;
		int n = this.rows();
		if (n != this.cols())
			throw new IllegalArgumentException(HEADINFO + "the matrix must be square.");

		cH = this.copy();
		cQ = MatrixComplex.eye(n);

		for (int k = 0; k <= n - 3; ++k) {
			int p = n - k - 1;

			MatrixComplex x = new MatrixComplex(p, 1);
			for (int i = 0; i < p; ++i) x.setItem(i, 0, cH.getItem(k + 1 + i, k));

			// v = -sign(x[0])*||x||*e1 - x ; u = v/||v|| ; P = I - 2*u*u^H
			// El signo NEGATIVO (a diferencia de QRfactor.qrHouseholder(), que usa +sign(x[0])*||x||)
			// evita cancelacion catastrofica en v[0] cuando x ya esta casi alineada con e1 (x[0]~=||x||):
			// con + , v[0]=||x||-x[0] resta dos cantidades casi iguales; con -, v[0]=-(||x||+x[0]) suma
			// magnitudes del mismo signo. Sin esta correccion, QRSchurfactor (que reaplica este mismo
			// reflector de forma iterativa, acercandose cada vez mas a una columna ya alineada) se
			// estanca en un residuo de ~1e-8 en vez de converger a precision de maquina.
			Complex alpha = QRfactor.signHH(x.getItem(0, 0)).times(x.norm()).opposite();
			MatrixComplex e1 = new MatrixComplex(p, 1);
			e1.setItem(0, 0, Complex.ONE);
			MatrixComplex v = (e1.times(alpha)).minus(x);
			double vNorm = v.norm();

			// Subcolumna ya nula por debajo de la subdiagonal: nada que reflejar en este paso.
			if (vNorm < Complex.zero_treshold_exact()) continue;

			MatrixComplex u = v.divides(vNorm);
			MatrixComplex reflector = MatrixComplex.eye(p).minus(u.times(u.adjoint()).times(2));

			MatrixComplex qK = MatrixComplex.eye(n);
			for (int i = 0; i < p; ++i)
				for (int j = 0; j < p; ++j)
					qK.setItem(k + 1 + i, k + 1 + j, reflector.getItem(i, j));

			cH = qK.times(cH).times(qK);
			cQ = cQ.times(qK);
		}

		factorized = true;
	}

	/*
	 * ***********************************************
	 * 	GETTERS
	 * ***********************************************
	 */

	/**
	 * Devuelve la matriz unitaria Q tal que A = Q*H*Q^H.
	 * @return La matriz Q de la factorizacion.
	 */
	public MatrixComplex getQ() {
		if (!factorized) System.out.println(HEADINFO + "the matrix hasn't been factorized.");
		return cQ;
	}

	/**
	 * Devuelve la matriz H, Hessenberg superior, tal que A = Q*H*Q^H.
	 * @return La matriz H de la factorizacion.
	 */
	public MatrixComplex getH() {
		if (!factorized) System.out.println(HEADINFO + "the matrix hasn't been factorized.");
		return cH;
	}

	/**
	 * Devuelve el estado de la factorizacion.
	 * @return true si la factorizacion se completo sin excepcion.
	 */
	public boolean factorized() {
		return factorized;
	}

}
