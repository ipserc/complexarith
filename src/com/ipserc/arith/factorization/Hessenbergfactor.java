/*
 * Reduccion a forma de Hessenberg superior por semejanza unitaria.

    Aplicable a: una matriz compleja A cuadrada n por n.

    Factorizacion: A = Q*H*Q^H donde Q es unitaria n por n y H es Hessenberg superior
    (todas las entradas por debajo de la subdiagonal, H[i][j] con i>j+1, son nulas).

    Metodo de calculo: n-2 reflectores de Householder complejos, cada uno anulando la
    subcolumna por debajo de la subdiagonal en una columna, aplicados por semejanza
    (H := Qk*H*Qk, no solo por la izquierda como en la factorizacion QR) para preservar
    los autovalores de la matriz original en cada paso.

    Es la Etapa 1 de la hoja de ruta QR-con-desplazamientos (ver plan mutable-rolling-stardust.md):
    el metodo estandar de LAPACK/MATLAB para obtener autovalores sin formar nunca el
    polinomio caracteristico explicitamente (formarlo ya es, en si mismo, numericamente
    traicionero -- resultado de Wilkinson). Esta etapa solo reduce a Hessenberg; no calcula
    autovalores todavia (eso es la Etapa 2, iteracion QR con deflacion).
 */

package com.ipserc.arith.factorization;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 *
 * @author ipserc
 *
 */
public class Hessenbergfactor extends MatrixComplex {

	private final static String HEADINFO = "Hessenbergfactor --- INFO: ";
	private final static String VERSION = "1.0 (2026_0802_0824)";

	private boolean factorized = false;

	private MatrixComplex cQ;
	private MatrixComplex cH;

	/* VERSION Release Note
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
	 * Instancia y factoriza una matriz compleja cuadrada expresada como String.
	 * @param strMatrix La matriz en formato String (filas separadas por ";", columnas por ",").
	 */
	public Hessenbergfactor(String strMatrix) {
		super(strMatrix);
		factorize();
	}

	/**
	 * Instancia y factoriza una matriz compleja cuadrada ya construida como MatrixComplex.
	 * @param matrix La matriz a factorizar.
	 */
	public Hessenbergfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
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

			// v = sign(x[0])*||x||*e1 - x ; u = v/||v|| ; P = I - 2*u*u^H  (mismo patron que QRfactor.qrHouseholder())
			Complex xNorm = QRfactor.signHH(x.getItem(0, 0)).times(x.norm());
			MatrixComplex e1 = new MatrixComplex(p, 1);
			e1.setItem(0, 0, Complex.ONE);
			MatrixComplex v = (e1.times(xNorm)).minus(x);
			double vNorm = v.norm();

			// Subcolumna ya nula por debajo de la subdiagonal: nada que reflejar en este paso.
			if (vNorm < Complex.zero()) continue;

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
