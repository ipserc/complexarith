/*
 * Schur factorization via QR iteration with Wilkinson shift and deflation.

    Applicable to: an n-by-n complex square matrix A.

    Factorization: A = Q*T*Q^H, where Q is n-by-n unitary and T is upper triangular (Schur form),
    with the eigenvalues of A on the diagonal of T.

    Computation method (Stages 1-3 of the QR-with-shifts roadmap, see
    Claude/ComplexArithRev.md and the mutable-rolling-stardust.md plan):
    1. Preliminary reduction to Hessenberg form (Hessenbergfactor, Stage 1).
    2. QR iteration WITHOUT shift over the active window [0..hi] (Stage 2): H_(k+1)=Qk^H*H_k*Qk.
    3. Wilkinson shift (Stage 3, this version): instead of factorizing H directly, (H-mu*I) is
       factorized, where mu is the eigenvalue of the active window's trailing 2x2 block closest to
       its bottom-right corner -- this accelerates convergence from linear to (generally) cubic,
       and resolves the equal-modulus eigenvalue case that Stage 2 left non-convergent (the shift
       is computed LOCALLY from the 2x2 block, independent of the GLOBAL modulus separation of the
       spectrum).
    4. Deflation: once the subdiagonal at (hi,hi-1) vanishes (within tolerance), that eigenvalue is
       already isolated in H[hi][hi] and the active window shrinks by one.

    Always works in pure complex arithmetic: each complex eigenvalue is deflated one at a time,
    with no need for the special 2x2-block handling a real Schur form would require -- which, as a
    side effect, also simplifies the shift computation itself (a direct complex square root via
    Complex.sqrt(), with no separate real/complex case).

    KNOWN LIMITATION inherited from any "single-shift" variant of the QR iteration, RESOLVED: every
    EXCEPTIONAL_SHIFT_INTERVAL (10) consecutive iterations without deflating, an ad-hoc EXCEPTIONAL
    shift (standard LAPACK formula, see exceptionalShift()) replaces the Wilkinson shift -- breaking
    the rare cycle without depending on resolving the 2x2 block that may be stuck in it.
    MAX_ITERS_PER_EIGENVALUE is kept as a final safety net for the even rarer case where not even
    the exceptional shift makes progress.
 */

package com.ipserc.arith.factorization;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * Schur factorization of a complex square matrix via QR iteration on its Hessenberg form, using a
 * Wilkinson shift with an exceptional-shift fallback and deflation.
 * @author ipserc
 *
 */
public class QRSchurfactor extends MatrixComplex {

	private final static String HEADINFO = "QRSchurfactor --- INFO: ";
	private final static String VERSION = "1.5 (2026_0811_2020)";

	/**
	 * Bound on the number of QR iterations allowed to deflate ONE eigenvalue before the iteration
	 * is declared non-convergent. With the Wilkinson shift (Stage 3), convergence is usually cubic
	 * -- a handful of iterations per eigenvalue, even for adversarial cases (equal modulus, repeated
	 * eigenvalues) -- so this bound is a generous safety net, not the expected case.
	 */
	private final static int MAX_ITERS_PER_EIGENVALUE = 300;

	/**
	 * Every this many consecutive QR iterations without deflating, an EXCEPTIONAL shift (see
	 * {@link #factorize()}) is used instead of the Wilkinson shift, to escape the rare cases where
	 * the Wilkinson shift itself enters a non-progressing cycle. Same classic threshold used by
	 * LAPACK ({@code dlahqr}/{@code zlahqr}).
	 */
	private final static int EXCEPTIONAL_SHIFT_INTERVAL = 10;

	private boolean factorized = false;

	private MatrixComplex cQ;
	private MatrixComplex cSchur;

	/* VERSION Release Note
	 *
	 * 1.5 (2026_0811_2020)
	 * Comentarios Javadoc traducidos al inglés y corregidos (sin cambios funcionales), como parte de
	 * la generación de la documentación de la API. También traducido a inglés el comentario de
	 * cabecera del fichero.
	 *
	 * 1.4 (2026_0809_1018)
	 * El chequeo de vector despreciable en la iteracion QR (stableHouseholderQ) usaba Complex.zero(),
	 * otro sitio dependiente del modo EXACT/APPROXIMATED global (ahora retirado del todo, ver
	 * Claude/ComplexArithRev.md, Vigesimosegunda sesion) -- sustituido por
	 * Complex.zero_treshold_exact() (fijo). No es el mismo sitio que causo el bug original de esa
	 * sesion (la deflacion en factorize(), linea ~188, ya quedo arreglada por herencia al fijar
	 * Complex.isZero()) -- este es un segundo umbral independiente en la misma clase.
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
	 * Instantiates and factorizes a complex square matrix expressed as a String.
	 * @param strMatrix The matrix in String format (rows separated by ";", columns by ",").
	 */
	public QRSchurfactor(String strMatrix) {
		super(strMatrix);
		factorize();
	}

	/**
	 * Instantiates and factorizes a complex square matrix already built as a MatrixComplex.
	 * @param matrix The matrix to factorize.
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
	 * Computes the Schur factorization A = Q*T*Q^H via reduction to Hessenberg form
	 * (Hessenbergfactor) followed by QR iteration with Wilkinson shift and deflation.
	 * <p>
	 * At each step, instead of factorizing the active window H directly (Stage 2), {@code H-mu*I}
	 * is factorized (mu = {@link #wilkinsonShift(MatrixComplex, int)}), and the SAME similarity
	 * update {@code H := Qk^H*H*Qk} is applied to the UNshifted {@code H} -- the shift cancels
	 * algebraically ({@code Qk} comes from {@code H-mu*I=Qk*Rk}, so
	 * {@code Rk*Qk+mu*I = Qk^H*(H-mu*I)*Qk+mu*I = Qk^H*H*Qk}), so only one {@code Qk^H*H*Qk} term
	 * is needed in the code, same as in Stage 2.
	 * <p>
	 * <b>KNOWN LIMITATION inherited from any single-shift variant, resolved:</b> every
	 * {@link #EXCEPTIONAL_SHIFT_INTERVAL} consecutive iterations without deflating,
	 * {@link #exceptionalShift(MatrixComplex, int)} (standard ad-hoc LAPACK formula) replaces the
	 * Wilkinson shift for the rare case where the latter enters a non-progressing cycle --
	 * {@code MAX_ITERS_PER_EIGENVALUE} is kept as a final safety net.
	 * @throws IllegalArgumentException if the matrix is not square, or if the iteration does not
	 * converge within the iteration bound (see above).
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
	 * Wilkinson shift: the eigenvalue of the active window's trailing 2x2 block
	 * ({@code H[hi-1][hi-1..hi], H[hi][hi-1..hi]}) closest to the bottom-right corner
	 * {@code H[hi][hi]} -- the standard choice (Golub &amp; Van Loan) that accelerates convergence
	 * and, being purely LOCAL (independent of the modulus separation of the full spectrum, unlike
	 * the unshifted iteration of Stage 2), also resolves the equal-modulus eigenvalue case.
	 * Generalized to complex numbers directly: the two roots of the 2x2 block are obtained with the
	 * usual quadratic formula, using {@code Complex.sqrt()} (which already respects this project's
	 * principal branch cut) instead of needing a separate case for a negative discriminant as real
	 * arithmetic would require.
	 * @param h The full matrix (only the 4 entries of the active window's trailing 2x2 block are read).
	 * @param hi The upper index of the active window (the 2x2 block uses rows/columns hi-1,hi).
	 * @return The shift mu.
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
	 * EXCEPTIONAL shift: standard ad-hoc formula (Francis/Wilkinson, the same one used by the
	 * reference LAPACK implementations {@code dlahqr}/{@code zlahqr}) to escape a rare stall of the
	 * Wilkinson shift -- perturbs the last diagonal element with the sum of the two most recent
	 * subdiagonal magnitudes, instead of resolving the trailing 2x2 block (which is precisely what
	 * may be stuck in a cycle). Since it does not depend on that resolution, it cannot inherit the
	 * same cycle that caused the stall.
	 * @param h The full matrix (only 2-3 entries near the active window's corner are read).
	 * @param hi The upper index of the active window.
	 * @return The shift mu.
	 */
	private static Complex exceptionalShift(MatrixComplex h, int hi) {
		double s = h.getItem(hi, hi - 1).mod();
		if (hi >= 2) s += h.getItem(hi - 1, hi - 2).mod();
		return h.getItem(hi, hi).plus(new Complex(s, 0));
	}

	/**
	 * Householder QR with the NUMERICALLY STABLE sign convention
	 * (alpha = -sign(x0)*||x||, instead of +sign(x0)*||x|| as in {@code QRfactor.qrHouseholder()}).
	 * <p>
	 * {@code QRfactor.qrHouseholder()} is deliberately NOT reused here: its sign convention (also
	 * inherited by {@code Hessenbergfactor} until this same finding was fixed there) suffers
	 * catastrophic cancellation in {@code v[0]} when the column to reflect is already nearly
	 * aligned with {@code e1} (x[0]~=||x||) -- exactly what happens at EVERY step of this iteration
	 * except the first, as the active window converges. Confirmed empirically: with
	 * {@code QRfactor}'s convention, the subdiagonal stalls at a residual of {@code ~1e-8} instead
	 * of continuing to drop toward machine precision. With the opposite-sign {@code alpha},
	 * {@code v[0] = -(sign(x0)*||x||+x0)} adds magnitudes of the same sign instead of subtracting
	 * two nearly-equal quantities, and convergence reaches {@code ~1e-15}. Returns only {@code Q}
	 * (unitary); {@code R} is not needed here, it is recovered implicitly via the similarity
	 * transformation in {@code factorize()}.
	 * @param active The active window (square matrix) to factorize.
	 * @return The unitary matrix Q such that {@code Q^H*active} is upper triangular.
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

			if (vNorm < Complex.zero_treshold_exact()) continue;

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
	 * Returns the unitary matrix Q such that A = Q*T*Q^H.
	 * @return The Q matrix of the factorization.
	 */
	public MatrixComplex getQ() {
		if (!factorized) System.out.println(HEADINFO + "the matrix hasn't been factorized.");
		return cQ;
	}

	/**
	 * Returns the matrix T (Schur form, upper triangular) such that A = Q*T*Q^H.
	 * @return The Schur matrix of the factorization.
	 */
	public MatrixComplex getSchur() {
		if (!factorized) System.out.println(HEADINFO + "the matrix hasn't been factorized.");
		return cSchur;
	}

	/**
	 * Returns the eigenvalues of the original matrix, taken from the diagonal of the Schur form.
	 * @return A column vector (n x 1) with the eigenvalues.
	 */
	public MatrixComplex getEigenvalues() {
		if (!factorized) System.out.println(HEADINFO + "the matrix hasn't been factorized.");
		int n = cSchur.rows();
		MatrixComplex eigenvalues = new MatrixComplex(n, 1);
		for (int i = 0; i < n; ++i) eigenvalues.setItem(i, 0, cSchur.getItem(i, i));
		return eigenvalues;
	}

	/**
	 * Returns the factorization status.
	 * @return true if the factorization completed without an exception.
	 */
	public boolean factorized() {
		return factorized;
	}

}
