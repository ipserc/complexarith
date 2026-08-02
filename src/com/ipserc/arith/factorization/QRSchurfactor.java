/*
 * Factorizacion de Schur via iteracion QR sin desplazamiento, con deflacion.

    Aplicable a: una matriz compleja A cuadrada n por n.

    Factorizacion: A = Q*T*Q^H donde Q es unitaria n por n y T es triangular superior (forma de
    Schur), con los autovalores de A en la diagonal de T.

    Metodo de calculo (Etapa 2 de la hoja de ruta QR-con-desplazamientos, ver
    Claude/ComplexArithRev.md y el plan mutable-rolling-stardust.md):
    1. Reduccion previa a forma de Hessenberg (Hessenbergfactor, Etapa 1).
    2. Iteracion QR clasica SIN desplazamiento sobre la ventana activa [0..hi]: H_(k+1) = Qk^H*H_k*Qk
       donde Qk viene de la factorizacion QR (Householder, reutilizando QRfactor) del bloque activo.
    3. Deflacion: cuando la subdiagonal en (hi,hi-1) se anula (dentro de tolerancia), ese autovalor
       ya esta aislado en H[hi][hi] y la ventana activa se reduce en uno.

    Esta etapa NO tiene desplazamientos todavia (eso es la Etapa 3) -- converge mas despacio, a
    una tasa gobernada por el cociente de modulos de autovalores consecutivos, y estanca (no
    converge) para autovalores de modulo exactamente igual (limitacion conocida de la iteracion QR
    sin desplazar, no un bug de esta implementacion -- documentada explicitamente, no oculta, mas
    abajo). Trabaja siempre en aritmetica compleja pura: cada autovalor complejo se deflaciona de
    uno en uno, sin el tratamiento especial de bloques 2x2 que necesitaria una forma de Schur real.
 */

package com.ipserc.arith.factorization;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 *
 * @author ipserc
 *
 */
public class QRSchurfactor extends MatrixComplex {

	private final static String HEADINFO = "QRSchurfactor --- INFO: ";
	private final static String VERSION = "1.0 (2026_0802_0824)";

	/**
	 * Cota de iteraciones QR sin desplazamiento admitidas para deflacionar UN autovalor antes de
	 * declarar que la iteracion no converge. Sin desplazamientos, la convergencia es lineal
	 * (tasa ~|lambda_(i+1)/lambda_i|); 1000 es generoso para autovalores con modulos razonablemente
	 * separados. Autovalores de modulo exactamente igual (p.ej. +-i) NUNCA convergen bajo este
	 * esquema sin desplazar, sea cual sea la cota -- ver KNOWN LIMITATION en factorize().
	 */
	private final static int MAX_ITERS_PER_EIGENVALUE = 1000;

	private boolean factorized = false;

	private MatrixComplex cQ;
	private MatrixComplex cSchur;

	/* VERSION Release Note
	 *
	 * 1.0 (2026_0802_0824)
	 * public QRSchurfactor(String strMatrix)
	 * public QRSchurfactor(MatrixComplex matrix)
	 * public void factorize()
	 * public MatrixComplex getQ()
	 * public MatrixComplex getSchur()
	 * public MatrixComplex getEigenvalues()
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
	public QRSchurfactor(String strMatrix) {
		super(strMatrix);
		factorize();
	}

	/**
	 * Instancia y factoriza una matriz compleja cuadrada ya construida como MatrixComplex.
	 * @param matrix La matriz a factorizar.
	 */
	public QRSchurfactor(MatrixComplex matrix) {
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
	 * Calcula la factorizacion de Schur A = Q*T*Q^H via reduccion a Hessenberg (Hessenbergfactor)
	 * seguida de iteracion QR sin desplazamiento con deflacion.
	 * <p>
	 * <b>KNOWN LIMITATION (Etapa 2, sin desplazamientos todavia):</b> la ventana activa [0..hi] se
	 * itera completa en cada paso (no se detecta deflacion interior, solo en el borde inferior
	 * hi/hi-1) y no hay desplazamientos de Wilkinson (Etapa 3) -- para autovalores de modulo
	 * EXACTAMENTE igual (p.ej. el par +i/-i de una matriz de rotacion) la subdiagonal (hi,hi-1)
	 * nunca converge a cero bajo este esquema, por muchas iteraciones que se den; se lanza
	 * {@code IllegalArgumentException} tras {@code MAX_ITERS_PER_EIGENVALUE} iteraciones en vez de
	 * colgarse o devolver basura. No es un defecto de esta implementacion, es una propiedad
	 * matematica de la iteracion QR sin desplazar -- se resuelve anadiendo desplazamientos.
	 * @throws IllegalArgumentException si la matriz no es cuadrada, o si la iteracion no converge
	 * dentro de la cota de iteraciones (ver arriba).
	 */
	public void factorize() {
		factorized = false;
		int n = this.rows();
		if (n != this.cols())
			throw new IllegalArgumentException(HEADINFO + "the matrix must be square.");

		Hessenbergfactor hessenberg = new Hessenbergfactor(this);
		MatrixComplex qHessenberg = hessenberg.getQ();
		MatrixComplex h = hessenberg.getH();
		MatrixComplex qIter = MatrixComplex.eye(n);

		int hi = n - 1;
		int itersSinceDeflate = 0;

		while (hi > 0) {
			if (h.getItem(hi, hi - 1).isZero()) {
				hi--;
				itersSinceDeflate = 0;
				continue;
			}

			if (++itersSinceDeflate > MAX_ITERS_PER_EIGENVALUE)
				throw new IllegalArgumentException(HEADINFO
					+ "unshifted QR iteration did not converge (subdiagonal at (" + hi + "," + (hi - 1)
					+ ") stayed non-zero after " + MAX_ITERS_PER_EIGENVALUE + " iterations -- likely "
					+ "eigenvalues of equal modulus; needs Wilkinson shifts, Etapa 3 of the roadmap, "
					+ "not implemented yet).");

			// Paso QR (Householder estable) sobre la ventana activa [0..hi] x [0..hi].
			MatrixComplex active = h.subMatrix(0, 0, hi + 1);
			MatrixComplex qStep = stableHouseholderQ(active);

			// Embebe Qstep en una identidad n x n (identidad fuera de la ventana activa) y aplica
			// la transformacion por semejanza al array COMPLETO -- esto actualiza tambien el
			// bloque de acoplamiento superior-derecho (filas 0..hi, columnas hi+1..n-1), no solo
			// la ventana activa, manteniendo A = Q*H*Q^H valido en todo momento.
			MatrixComplex qK = MatrixComplex.eye(n);
			for (int i = 0; i <= hi; ++i)
				for (int j = 0; j <= hi; ++j)
					qK.setItem(i, j, qStep.getItem(i, j));

			h = qK.adjoint().times(h).times(qK);
			qIter = qIter.times(qK);
		}

		cSchur = h;
		cQ = qHessenberg.times(qIter);
		factorized = true;
	}

	/**
	 * QR de Householder con la convencion de signo NUMERICAMENTE ESTABLE
	 * (alpha = -sign(x0)*||x||, en vez de +sign(x0)*||x|| como {@code QRfactor.qrHouseholder()}).
	 * <p>
	 * No se reutiliza {@code QRfactor.qrHouseholder()} aqui a proposito: su convencion de signo
	 * (heredada tambien por {@code Hessenbergfactor} hasta que este mismo hallazgo se corrigio ahi)
	 * sufre cancelacion catastrofica en {@code v[0]} cuando la columna a reflejar ya esta casi
	 * alineada con {@code e1} (x[0]~=||x||) -- justo lo que ocurre en TODOS los pasos de esta
	 * iteracion salvo el primero, a medida que la ventana activa converge. Confirmado
	 * empiricamente: con la convencion de {@code QRfactor}, la subdiagonal se estanca en un
	 * residuo de {@code ~1e-8} en vez de seguir bajando hacia la precision de maquina. Con
	 * {@code alpha} de signo opuesto, {@code v[0] = -(sign(x0)*||x||+x0)} suma magnitudes del
	 * mismo signo en vez de restar dos cantidades casi iguales, y la convergencia llega a
	 * {@code ~1e-15}. Devuelve solo {@code Q} (unitaria); {@code R} no hace falta aqui, se
	 * recupera implicitamente via la transformacion por semejanza en {@code factorize()}.
	 * @param active La ventana activa (matriz cuadrada) a factorizar.
	 * @return La matriz Q, unitaria, tal que {@code Q^H*active} es triangular superior.
	 */
	private static MatrixComplex stableHouseholderQ(MatrixComplex active) {
		int m = active.rows();
		MatrixComplex z = active.copy();
		MatrixComplex q = MatrixComplex.eye(m);

		for (int k = 0; k < m - 1; ++k) {
			int p = m - k;
			MatrixComplex x = new MatrixComplex(p, 1);
			for (int i = 0; i < p; ++i) x.setItem(i, 0, z.getItem(k + i, k));

			Complex alpha = QRfactor.signHH(x.getItem(0, 0)).times(x.norm()).opposite();
			MatrixComplex e1 = new MatrixComplex(p, 1);
			e1.setItem(0, 0, Complex.ONE);
			MatrixComplex v = (e1.times(alpha)).minus(x);
			double vNorm = v.norm();

			if (vNorm < Complex.zero()) continue;

			MatrixComplex u = v.divides(vNorm);
			MatrixComplex reflector = MatrixComplex.eye(p).minus(u.times(u.adjoint()).times(2));

			MatrixComplex qK = MatrixComplex.eye(m);
			for (int i = 0; i < p; ++i)
				for (int j = 0; j < p; ++j)
					qK.setItem(k + i, k + j, reflector.getItem(i, j));

			z = qK.times(z);
			q = q.times(qK);
		}
		return q;
	}

	/*
	 * ***********************************************
	 * 	GETTERS
	 * ***********************************************
	 */

	/**
	 * Devuelve la matriz unitaria Q tal que A = Q*T*Q^H.
	 * @return La matriz Q de la factorizacion.
	 */
	public MatrixComplex getQ() {
		if (!factorized) System.out.println(HEADINFO + "the matrix hasn't been factorized.");
		return cQ;
	}

	/**
	 * Devuelve la matriz T (forma de Schur, triangular superior) tal que A = Q*T*Q^H.
	 * @return La matriz de Schur de la factorizacion.
	 */
	public MatrixComplex getSchur() {
		if (!factorized) System.out.println(HEADINFO + "the matrix hasn't been factorized.");
		return cSchur;
	}

	/**
	 * Devuelve los autovalores de la matriz original, tomados de la diagonal de la forma de Schur.
	 * @return Vector columna (n x 1) con los autovalores.
	 */
	public MatrixComplex getEigenvalues() {
		if (!factorized) System.out.println(HEADINFO + "the matrix hasn't been factorized.");
		int n = cSchur.rows();
		MatrixComplex eigenvalues = new MatrixComplex(n, 1);
		for (int i = 0; i < n; ++i) eigenvalues.setItem(i, 0, cSchur.getItem(i, i));
		return eigenvalues;
	}

	/**
	 * Devuelve el estado de la factorizacion.
	 * @return true si la factorizacion se completo sin excepcion.
	 */
	public boolean factorized() {
		return factorized;
	}

}
