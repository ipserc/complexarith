/*
 * Factorizacion de Schur via iteracion QR con desplazamiento de Wilkinson, con deflacion.

    Aplicable a: una matriz compleja A cuadrada n por n.

    Factorizacion: A = Q*T*Q^H donde Q es unitaria n por n y T es triangular superior (forma de
    Schur), con los autovalores de A en la diagonal de T.

    Metodo de calculo (Etapas 1-3 de la hoja de ruta QR-con-desplazamientos, ver
    Claude/ComplexArithRev.md y el plan mutable-rolling-stardust.md):
    1. Reduccion previa a forma de Hessenberg (Hessenbergfactor, Etapa 1).
    2. Iteracion QR SIN desplazamiento sobre la ventana activa [0..hi] (Etapa 2): H_(k+1)=Qk^H*H_k*Qk.
    3. Desplazamiento de Wilkinson (Etapa 3, esta version): en vez de factorizar H directamente, se
       factoriza (H-mu*I) donde mu es el autovalor del bloque 2x2 final de la ventana activa mas
       cercano a la esquina inferior derecha -- acelera la convergencia de lineal a (generalmente)
       cubica, y resuelve el caso de autovalores de modulo igual que la Etapa 2 dejaba sin converger
       (el desplazamiento se calcula localmente del bloque 2x2, no depende de la separacion GLOBAL
       de modulos del espectro).
    4. Deflacion: cuando la subdiagonal en (hi,hi-1) se anula (dentro de tolerancia), ese autovalor
       ya esta aislado en H[hi][hi] y la ventana activa se reduce en uno.

    Trabaja siempre en aritmetica compleja pura: cada autovalor complejo se deflaciona de uno en
    uno, sin el tratamiento especial de bloques 2x2 que necesitaria una forma de Schur real -- lo
    cual, de paso, simplifica tambien el propio calculo del desplazamiento (raiz cuadrada compleja
    directa via Complex.sqrt(), sin distinguir casos real/complejo).

    KNOWN LIMITATION heredada de cualquier variante de "desplazamiento simple" (single-shift) de la
    iteracion QR, RESUELTA: cada EXCEPTIONAL_SHIFT_INTERVAL (10) iteraciones consecutivas sin
    deflacionar, un desplazamiento EXCEPCIONAL ad-hoc (formula estandar de LAPACK, ver
    exceptionalShift()) sustituye al de Wilkinson -- rompe el raro ciclo sin depender de resolver
    el bloque 2x2 que puede estar atrapado en el. MAX_ITERS_PER_EIGENVALUE se conserva como red de
    seguridad final para el caso, aun mas raro, de que ni siquiera el desplazamiento excepcional
    progrese.
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
	private final static String VERSION = "1.3 (2026_0808_0030)";

	/**
	 * Cota de iteraciones QR admitidas para deflacionar UN autovalor antes de declarar que la
	 * iteracion no converge. Con el desplazamiento de Wilkinson (Etapa 3) la convergencia suele
	 * ser cubica -- unas pocas iteraciones por autovalor, incluso para casos adversarios (modulo
	 * igual, autovalores repetidos) -- asi que esta cota es una red de seguridad generosa, no el
	 * caso esperado.
	 */
	private final static int MAX_ITERS_PER_EIGENVALUE = 300;

	/**
	 * Cada este numero de iteraciones QR consecutivas sin deflacionar, se usa un desplazamiento
	 * EXCEPCIONAL (ver {@link #factorize()}) en vez del de Wilkinson, para escapar de los raros
	 * casos en que el propio desplazamiento de Wilkinson entra en un ciclo sin progreso. Mismo
	 * umbral clasico usado por LAPACK ({@code dlahqr}/{@code zlahqr}).
	 */
	private final static int EXCEPTIONAL_SHIFT_INTERVAL = 10;

	private boolean factorized = false;

	private MatrixComplex cQ;
	private MatrixComplex cSchur;

	/* VERSION Release Note
	 *
	 * 1.3 (2026_0808_0030)
	 * factorize(): resuelve el KNOWN LIMITATION heredado de single-shift QR (sin salvaguarda ante
	 * un desplazamiento de Wilkinson estancado). Cada EXCEPTIONAL_SHIFT_INTERVAL (10) iteraciones
	 * consecutivas sin deflacionar, un desplazamiento EXCEPCIONAL ad-hoc (nuevo helper privado
	 * exceptionalShift(), formula estandar de LAPACK dlahqr/zlahqr) sustituye al de Wilkinson --
	 * no depende de resolver el bloque 2x2 final, asi que no puede heredar el mismo ciclo que
	 * causo el estancamiento. Verificado sin regresion en TestQRSchur01 (9/9), TestEigenspaceQR
	 * Compare01 (4/4, incluye bateria de 300 matrices aleatorias) y TestEigenspaceFallback01
	 * (3/3); codigo del desplazamiento excepcional en si verificado forzandolo a disparar en
	 * cada iteracion (EXCEPTIONAL_SHIFT_INTERVAL=1 temporal): ejecuta sin corromper nada, solo
	 * converge mas despacio que Wilkinson (esperado, es un escape de emergencia, no el
	 * desplazamiento principal).
	 *
	 * 1.2 (2026_0807_2359)
	 * QRSchurfactor(MatrixComplex): mismo bug de aliasing por clone() superficial encontrado en
	 * Schurfactor/LUfactor/QRfactor esta sesion -- "matrix.complexMatrix.clone()" comparte las
	 * filas del array con la matriz original del llamador. Arreglado con
	 * matrix.copy().complexMatrix.
	 *
	 * 1.1 (2026_0802_1015)
	 * factorize(): anade el desplazamiento de Wilkinson (Etapa 3) al paso QR de cada iteracion,
	 * calculado por el nuevo helper privado wilkinsonShift(). MAX_ITERS_PER_EIGENVALUE 1000->300
	 * (la convergencia con desplazamiento es mucho mas rapida, la cota es solo red de seguridad).
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
		this.complexMatrix = matrix.copy().complexMatrix;
		factorize();
	}

	/*
	 * ***********************************************
	 * 	METHODS
	 * ***********************************************
	 */

	/**
	 * Calcula la factorizacion de Schur A = Q*T*Q^H via reduccion a Hessenberg (Hessenbergfactor)
	 * seguida de iteracion QR con desplazamiento de Wilkinson y deflacion.
	 * <p>
	 * En cada paso, en vez de factorizar la ventana activa H directamente (Etapa 2), se factoriza
	 * {@code H-mu*I} (mu = {@link #wilkinsonShift(MatrixComplex, int)}) y se aplica la MISMA
	 * actualizacion por semejanza {@code H := Qk^H*H*Qk} sobre la {@code H} SIN desplazar -- el
	 * desplazamiento cancela algebraicamente ({@code Qk} viene de {@code H-mu*I=Qk*Rk}, luego
	 * {@code Rk*Qk+mu*I = Qk^H*(H-mu*I)*Qk+mu*I = Qk^H*H*Qk}), asi que solo hace falta un termino
	 * {@code Qk^H*H*Qk} en el codigo, igual que en la Etapa 2.
	 * <p>
	 * <b>KNOWN LIMITATION heredada de cualquier variante de desplazamiento simple (single-shift),
	 * resuelta:</b> cada {@link #EXCEPTIONAL_SHIFT_INTERVAL} iteraciones consecutivas sin
	 * deflacionar, {@link #exceptionalShift(MatrixComplex, int)} (formula ad-hoc estandar de
	 * LAPACK) sustituye al desplazamiento de Wilkinson para el caso raro en que este ultimo entra
	 * en un ciclo sin progreso -- {@code MAX_ITERS_PER_EIGENVALUE} se conserva como red de
	 * seguridad final.
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
					+ "shifted QR iteration did not converge (subdiagonal at (" + hi + "," + (hi - 1)
					+ ") stayed non-zero after " + MAX_ITERS_PER_EIGENVALUE + " iterations).");

			// Desplazamiento de Wilkinson: factoriza (activa - mu*I), no la ventana activa directa.
			// Cada EXCEPTIONAL_SHIFT_INTERVAL iteraciones sin deflacionar, un desplazamiento
			// EXCEPCIONAL (formula ad-hoc estandar, LAPACK) sustituye al de Wilkinson para romper
			// el raro caso de estancamiento -- no depende de resolver el bloque 2x2, así que no
			// puede heredar el mismo ciclo que causo el estancamiento.
			Complex mu = (itersSinceDeflate % EXCEPTIONAL_SHIFT_INTERVAL == 0)
				? exceptionalShift(h, hi)
				: wilkinsonShift(h, hi);
			MatrixComplex active = h.subMatrix(0, 0, hi + 1);
			MatrixComplex shifted = active.minus(MatrixComplex.eye(hi + 1).times(mu));
			MatrixComplex qStep = stableHouseholderQ(shifted);

			// Embebe Qstep en una identidad n x n (identidad fuera de la ventana activa) y aplica
			// la transformacion por semejanza SIN desplazar al array COMPLETO -- esto actualiza
			// tambien el bloque de acoplamiento superior-derecho (filas 0..hi, columnas hi+1..n-1),
			// no solo la ventana activa, manteniendo A = Q*H*Q^H valido en todo momento.
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
	 * Desplazamiento de Wilkinson: el autovalor del bloque 2x2 final de la ventana activa
	 * ({@code H[hi-1][hi-1..hi], H[hi][hi-1..hi]}) mas cercano a la esquina inferior derecha
	 * {@code H[hi][hi]} -- eleccion estandar (Golub &amp; Van Loan) que acelera la convergencia y,
	 * al ser puramente LOCAL (no depende de la separacion de modulos del espectro completo, a
	 * diferencia de la iteracion sin desplazar de la Etapa 2), tambien resuelve el caso de
	 * autovalores de modulo igual. Generalizado a complejos de forma directa: las dos raices del
	 * bloque 2x2 se obtienen con la formula cuadratica de siempre, usando {@code Complex.sqrt()}
	 * (que ya respeta el corte de rama principal de este proyecto) en vez de necesitar un caso
	 * aparte para discriminante negativo como haria falta en aritmetica real.
	 * @param h La matriz completa (solo se leen las 4 entradas del bloque 2x2 final de la ventana activa).
	 * @param hi El indice superior de la ventana activa (el bloque 2x2 usa las filas/columnas hi-1,hi).
	 * @return El desplazamiento mu.
	 */
	private static Complex wilkinsonShift(MatrixComplex h, int hi) {
		Complex a = h.getItem(hi - 1, hi - 1);
		Complex b = h.getItem(hi - 1, hi);
		Complex c = h.getItem(hi, hi - 1);
		Complex d = h.getItem(hi, hi);

		Complex trace = a.plus(d);
		Complex det = a.times(d).minus(b.times(c));
		Complex discriminant = Complex.sqrt(trace.times(trace).minus(det.times(4.0)));

		Complex lambda1 = trace.plus(discriminant).divides(2.0);
		Complex lambda2 = trace.minus(discriminant).divides(2.0);

		return (lambda1.minus(d).mod() <= lambda2.minus(d).mod()) ? lambda1 : lambda2;
	}

	/**
	 * Desplazamiento EXCEPCIONAL: formula ad-hoc estandar (Francis/Wilkinson, la misma que usan
	 * las implementaciones de referencia {@code dlahqr}/{@code zlahqr} de LAPACK) para escapar de
	 * un raro estancamiento del desplazamiento de Wilkinson -- perturba el ultimo elemento
	 * diagonal con la suma de las dos magnitudes subdiagonales mas recientes, en vez de resolver
	 * el bloque 2x2 final (que es precisamente lo que puede estar atrapado en un ciclo). Al no
	 * depender de esa resolucion, no puede heredar el mismo ciclo que causo el estancamiento.
	 * @param h La matriz completa (solo se leen 2-3 entradas cerca de la esquina de la ventana activa).
	 * @param hi El indice superior de la ventana activa.
	 * @return El desplazamiento mu.
	 */
	private static Complex exceptionalShift(MatrixComplex h, int hi) {
		double s = h.getItem(hi, hi - 1).mod();
		if (hi >= 2) s += h.getItem(hi - 1, hi - 2).mod();
		return h.getItem(hi, hi).plus(new Complex(s, 0));
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
