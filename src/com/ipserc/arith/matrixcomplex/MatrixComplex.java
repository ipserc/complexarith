package com.ipserc.arith.matrixcomplex;

import java.lang.Math;

import com.ipserc.arith.combinatoric.*;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.factorization.Schurfactor;
import com.ipserc.arith.polynom.*;
import com.ipserc.arith.syseq.Syseq;

/**
 * 
 * @author ipserc
 *
 */
public class MatrixComplex {
	public Complex[][] complexMatrix;
	
	final static String HEADINFO = "MatrixComplex --- INFO: ";
	private final static String VERSION = "1.44 (2026_0802_2338)";
	/* VERSION Release Note
	 *
	 * 1.40 (2026_0803_1900)
	 * EQUATION SYSTEMS section, sub-fase A+B (classification/resolution + Gauss/Cramer/submatrices,
	 * ~808 lines) extracted to new package-private MatrixComplexEquationSystems, same pattern as
	 * MatrixComplexFormat (Etapa 1)/MatrixComplexFunctions (Etapa 2). HEADINFO widened private ->
	 * package-private (used throughout the moved code, same as trace()/__log10__ in Etapa 2);
	 * intSplit(String,String) and copyCol(MatrixComplex,int) (both COLS & ROWS OPERATIONS, staying
	 * in the core) widened the same way for the same reason. The INCONSISTENT/INDETERMINATE/
	 * DETERMINATE constants stay declared here (public API), referenced fully qualified from the
	 * new class. isNullSolution(int,int) (private, confirmed zero callers anywhere in the project,
	 * pre-existing dead code per the saved restructuring plan) moved verbatim, unchanged. Etapa 3
	 * sub-fase A+B of the multi-session MatrixComplex.java restructuring roadmap (Duodecima sesion,
	 * ver ComplexArithRev.md) -- Syseq.java confirmed to only call public methods of this core
	 * (typeEqSys, solveGauss, isHomogeneous, completeEqSys, solve, coefMatrix, indMatrix,
	 * constMatrix), extraction transparent to it. Public API unchanged; verified byte-for-byte
	 * against HEAD (~35 method calls over 6 representative matrices: determinate/indeterminate/
	 * rectangular/inconsistent/homogeneous/complex, including exception-path cases) plus the full
	 * 73-file regression battery (equation-system-specific + standard), 0 exit-code mismatches
	 * (residual stdout differences confirmed pre-existing Math.random()-decoration/timing/unseeded-
	 * random-matrix noise, same as documented in Decima/Undecima sesion -- verified by diffing the
	 * unmodified reference binary against itself).
	 *
	 * 1.39 (2026_0803_0130)
	 * Fixed both bugs found incidentally in VERSION 1.38 (both explicitly requested by the user,
	 * not deferred): (1) static ccos(MatrixComplex) used Complex.sin() instead of Complex.cos()
	 * for every entry -- now matches ccos()'s (correct) instance body; verified diff==0 against
	 * the instance method. (2) MatrixComplexFunctions.logMercator() had no iteration cap (still
	 * used raw Complex.digits(), ~10^13) -- added LOG_MERCATOR_MAX_ITER=10000 (same value/rationale
	 * as logTaylor()'s LOG_TAYLOR_MAX_ITER, VERSION 1.36) plus an explicit "did not converge"
	 * exception, same pattern. Confirmed real hang before the fix (the nilpotent "0,1;0,0" case,
	 * same boundary as logTaylor()'s); now throws in ~40ms. Convergent-case regression (matches
	 * log()) and full test battery unaffected -- see ComplexArithRev.md for detail.
	 *
	 * 1.38 (2026_0803_0100)
	 * TAYLOR'S SERIES section (exp/exp_, sin/cos/tan families incl. Taylor/Euler/item-to-item
	 * variants, euler, sinh/cosh/tanh families, logTaylor/logMercator/logHat/logm/log,
	 * llog/log10/llog10/logbase family) extracted to new package-private MatrixComplexFunctions,
	 * same pattern as MatrixComplexFormat (Etapa 1). trace(String/MatrixComplex/Complex) widened
	 * private->package-private (used throughout the moved code); __log10__ widened the same way;
	 * doPlot(String,double[][],int) moved in (all 8 call sites were in this section) instead of
	 * just widened, using the already-public doPlot()/debug() getters for its flag checks. Item-
	 * to-item static twins (ssin(MatrixComplex) etc.) and the 3 power(...) statics were left
	 * UNTOUCHED in this class -- they don't depend on anything moved, and moving them too would
	 * have collided in signature with their own instance-derived counterpart in the new class.
	 * Etapa 2 of the multi-session MatrixComplex.java restructuring roadmap (Undecima sesion, ver
	 * ComplexArithRev.md). Public API unchanged; verified byte-for-byte against HEAD (65 method
	 * calls x 7 matrices) plus the full regression battery, 0 behavior changes (only expected
	 * extra stack frames on the tests that exercise thrown exceptions).
	 * Two PRE-EXISTING bugs found incidentally while moving code, NOT fixed here (analysis only,
	 * per usual practice): (1) logMercator() has no iteration cap -- same LOG_TAYLOR_MAX_ITER-style
	 * hang logTaylor() used to have (fixed VERSION 1.36) for a boundary-case matrix (e.g. the
	 * nilpotent "0,1;0,0"), confirmed still present, unrelated to this move; (2) the static
	 * ccos(MatrixComplex) item-to-item method computes Complex.sin() instead of Complex.cos() for
	 * every entry -- confirmed present verbatim in this class, unrelated to this move.
	 *
	 * 1.37 (2026_0802_2230)
	 * PRINTING section (print/println/toString/toMaxima/toWolfram/toMatlab/toOctave/
	 * preMatrixComplex/toMatrixComplex) extracted to new package-private MatrixComplexFormat,
	 * same pattern as PolynomFormat (Decima sesion) -- MatrixComplex.java's own methods keep their
	 * exact signatures, delegating in one line each. Etapa 1 of the multi-session MatrixComplex.java
	 * restructuring roadmap (Undecima sesion, ver ComplexArithRev.md). No visibility widening needed
	 * (isEmpty() was already public). Public API unchanged.
	 *
	 * 1.36 (2026_0802_1800)
	 * logTaylor(): new LOG_TAYLOR_MAX_ITER=10000 cap (was effectively Complex.digits()'s
	 * 10^precision, never meant to be reached) plus an explicit "did not converge" exception when
	 * the iteration cap is exhausted -- fixes the hang confirmed for a nilpotent matrix (see the
	 * constant's own Javadoc). VERSION bump missed in the commit that made this change; added here
	 * as a tiny follow-up, no further code change.
	 *
	 * 1.35 (2026_0802_1700)
	 * log(): the defective (non-diagonalizable) branch now tries logm() (Schur + inverse
	 * scaling-and-squaring, any eigenvalue orientation) first, falling back to logTaylor() (narrow
	 * convergence range, close to +||A||) only on explicit failure. Connects logm() into the
	 * dispatcher for the first time -- was deliberately left unconnected since the Novena sesion.
	 * Confirmed real case fixed: a defective 2x2 block (lambda=-50, P not orthogonal) that made
	 * log() throw outright now resolves cleanly via logm() (exp(log(A)) matches A to ~1.5e-9).
	 *
	 * 1.34 (2026_0802_1400)
	 * New public nullspaceBasis(): Gauss-Jordan elimination to reduced row echelon form, tracking
	 * pivot vs. free columns explicitly, returning one independent basis vector per free column
	 * (nullity() rows) -- unlike kernel()/kernel(Complex), which return a SINGLE vector (all free
	 * variables set to the same scalar), the right tool only when the nullspace is 1-dimensional.
	 * Fase A of generalizing Jordan.java beyond geometric multiplicity 1 (ver ComplexArithRev.md).
	 *
	 * 1.33 (2026_0802_0131)
	 * rank2() now solves A'*A's characteristic polynomial via Polynom.solveRobust() instead of
	 * solve() (Durand-Kerner only) -- fixes the "Fail prone due to lack precision" fragility this
	 * method's own Javadoc had warned about (comment now removed, no longer true): confirmed with
	 * a 1200-random-matrix battery that Durand-Kerner alone threw an arithmetic-overflow exception
	 * 74-100% of the time for 5x5+ matrices; solveRobust() (try Durand-Kerner, fall back to
	 * Aberth-Ehrlich only on overflow) gives 0 exceptions and identical rank to rank1()/brute-force
	 * ground truth in every case, with no behavior change on the cases that already worked.
	 *
	 * 1.32 (2026_0801_2318)
	 * New public logm(): natural logarithm of a defective (non-diagonalizable) matrix via Schur
	 * factorization + inverse scaling-and-squaring (MATLAB logm/Higham) -- fills the gap log()
	 * falls back to logTaylor() for, which only converges for a narrow dominant-eigenvalue
	 * orientation. Uses sqrtTriangular() (VERSION 1.31) plus logMercator()'s convergence-loop body
	 * reused without its own norm-reduction preprocessing. Verified against 14 cases: a closed-form
	 * oracle for single Jordan blocks (log(λI+N)=log(λ)I+Σ(-1)^(k+1)(N/λ)^k/k, finite since N is
	 * nilpotent) across real/negative-real/complex/small/large eigenvalues; regression against
	 * log() on diagonal/diagonalizable matrices; exp(logm(A))~=A self-consistency for multi-block
	 * synthetic defective matrices; a genuinely singular (nilpotent) matrix failing cleanly instead
	 * of hanging. NOT yet wired into log()'s dispatcher -- deliberate scope decision, see the
	 * Novena sesion plan.
	 * Newly characterized while verifying (not caused by this change, pre-existing): Schurfactor's
	 * internal Eigenspace.eigenvectors3() hits the same "imprecise Durand-Kerner eigenvalue keeps
	 * (A-eigenval*I) from being quite singular, homogeneous solve collapses to the trivial all-zero
	 * vector" failure mode already diagnosed in Jordan.java this session, for REPEATED eigenvalues
	 * at larger block sizes (confirmed with a sweep: e.g. n=4 fails for lambda=3/-3/-1/50/-50, only
	 * lambda=1 works; n=3 fails only for lambda=50). Distinct eigenvalues (diagonalizable case) are
	 * unaffected at any size tested. logm() detects this correctly via Schurfactor.factorized()
	 * and throws, doesn't produce garbage -- but it does mean logm()'s practical domain for
	 * genuinely defective matrices is narrower than hoped until Eigenspace's generic eigenvector
	 * solver gets the same kind of fix Jordan.java got this session. Documented, not fixed here --
	 * separate, larger-scope decision.
	 * New private sqrtTriangular(MatrixComplex): principal square root of an upper triangular
	 * matrix via the Parlett recurrence (Björck-Hammarling), the standard building block for
	 * scaling-and-squaring logm() -- see the pending Novena sesion plan. Diagonal via
	 * Complex.sqrt()'s principal branch; off-diagonal solved diagonal-by-diagonal from S*S=T.
	 * Verified against 7 hand-built/self-consistency cases (real/complex/repeated eigenvalues up
	 * to 5x5, including a genuinely negative real eigenvalue). KNOWN LIMITATION, documented not
	 * fixed: divides by S_ii+S_jj, which under THIS project's principal branch (Re>=0 always) can
	 * only vanish for a repeated ZERO eigenvalue within the same Jordan chain (a nontrivial
	 * nilpotent block genuinely has no triangular square root) -- verified the naive "opposite
	 * eigenvalues" case from the generic literature does NOT actually degenerate here
	 * (T=diag(4,-4) gives S_00+S_11=2+2i, nowhere near zero).
	 *
	 * 1.30 (2026_0801_1600)
	 * orthonormalize() normalized the wrong axis: this.orthogonalize().normalizeByCols() should
	 * have been normalizeByRows(). orthogonalize() (Gram-Schmidt via gramSchmidt()) leaves the
	 * orthogonal basis vectors in the ROWS of the result (confirmed: gramSchmidt() builds each
	 * vector as a column of an internal matrix, then returns it transposed), so normalizing by
	 * columns left the actual basis vectors untouched -- confirmed with A=[[4,1],[2,3]]: before
	 * the fix, U*.U-I had error ~0.6 (not unitary at all); after, ~2.2e-16 (machine precision).
	 * Only 2 callers in the whole project: Schurfactor.java (the Schur decomposition used to
	 * build a matrix U1 to complete an eigenvector to an orthonormal basis -- was silently
	 * producing a non-unitary U1, so every downstream Schur factorization was wrong) and
	 * TestGram06.java (never checked unitarity, which is why this went undetected). Verified
	 * with a 9-matrix battery covering diagonal, well-conditioned non-diagonal (real/complex),
	 * and the defective Jordan case that matters for the pending logm work (A=P*J*P^-1,
	 * J=[[50,1],[0,50]], P not orthogonal, not already triangular): Schurfactor now factorizes
	 * it correctly (U unitary to 3e-16, reconstruction exact to 2e-14).
	 *
	 * 1.29 (2026_0801_0932)
	 * solveGauss()'s INCONSISTENT branch now throws IllegalArgumentException instead of
	 * returning a NaN/Infinity marker (follow-up to the Hallazgo 3 SCOPE DECISION of 1.26 --
	 * Syseq.java was fixed first, see Syseq.VERSION 1.6, to stop depending on that marker).
	 * Verified no other real caller (MatrixComplex.kernel(), Jordan.java, geom/Plane.java,
	 * Polynom.java's Vandermonde interpolation) depends on it either -- all solve either a
	 * homogeneous system (always consistent) or one geometrically/algebraically guaranteed
	 * consistent whenever actually reached.
	 *
	 * 1.28 (2026_0801_0915)
	 * removeDuplicateRows() had an ungated System.out.println() logging every duplicate row it
	 * dropped, unconditionally, in normal (non-debug) use. Switched to trace() (gated behind
	 * the class's __DEBUG__ flag, false by default), same convention already used everywhere
	 * else in this file. With this commit, the 5 EQUATION SYSTEMS findings of the Octava
	 * sesion are closed (Hallazgo 3 documented, not code-changed; the other 4 fixed).
	 *
	 * 1.27 (2026_0801_0912)
	 * typeEqSys() misclassified a homogeneous system of full column rank as INDETERMINATE
	 * instead of DETERMINATE. Full column rank always means exactly one solution (the trivial
	 * x=0 for a homogeneous system), not a free parameter.
	 *
	 * 1.26 (2026_0801_0906)
	 * Javadoc only, no behavior change: documents why solveGauss()'s INCONSISTENT branch keeps
	 * returning a NaN/Infinity marker instead of throwing -- Syseq.solveq() genuinely depends
	 * on it (calls solve() before checking typeEqSys()). SCOPE DECISION, deferred at the
	 * user's explicit request; a real fix would also need to touch Syseq.java.
	 *
	 * 1.25 (2026_0801_0858)
	 * solveGauss()/solveCramer() throw IllegalArgumentException on a shape they can't handle
	 * instead of continuing in silence: solveGauss() for an over-determined system (more
	 * equations than unknowns -- the trace()-only guard was a no-op by default, __DEBUG__=false);
	 * solveCramer() for any non-square coefficient shape (println without return/throw before).
	 *
	 * 1.24 (2026_0801_0854)
	 * solveGauss()/nbrOfSolutions() completed a genuinely rectangular system (fewer equations
	 * than unknowns) internally via completeEqSys() instead of silently returning an empty
	 * solution matrix / 0. Only for the under-determined case; over-determined systems are
	 * left untouched (completeEqSys() would truncate, not pad, extra equations).
	 *
	 * 1.23 (2026_0801_0100)
	 * trigonTaylor(): trampa explicita de NaN/Infinity (hallazgo colateral del
	 * hallazgo 6, descubierto al investigar el cuelgue de sinTaylor() con Jordan
	 * [[80,1],[0,80]]): para autovalores grandes, powMatrix desborda el rango de
	 * double antes de que la serie converja o dispare cualquiera de los 2 detectores
	 * ya existentes (divergencia del hallazgo 5, cancelacion del hallazgo 6) -- y una
	 * vez aparece NaN, AMBOS quedan ciegos, porque toda comparacion numerica contra
	 * NaN es falsa en IEEE754 (deviation>1 falso, finalNorm<noiseFloor falso). Antes:
	 * sinTaylor() en ese Jordan tardaba 83s en agotar ~10^7 iteraciones y devolvia
	 * NaN+NaNi sin avisar. Ahora: excepcion inmediata en 13ms, en el momento exacto
	 * en que el NaN/Infinity aparece por primera vez (confirmado: powMatrix.norm()
	 * ya es NaN en k=163, antes de que fact desborde en k=171). Confirmado con
	 * barrido de autovalores 1..100: sin excepcion hasta ~20, cancelacion catastrofica
	 * 35-60, desbordamiento a partir de ~80, sin cuelgues en todo el rango. sin()/
	 * cos() publicos (via Euler/exp(), que si escala) dan el resultado exacto en el
	 * mismo Jordan sin tocar este codigo -- no estan expuestos, igual que el hallazgo 6.
	 *
	 * 1.22 (2026_0731_2400)
	 * Cancelacion numerica catastrofica detectada en trigonTaylor() (auditoria
	 * matematica, hallazgo 6): sinTaylor()/cosTaylor() lanzan IllegalArgumentException
	 * si las sumas parciales de la serie alternante superaron el resultado final por
	 * encima del suelo de ruido de double precision (maxPartialNorm*k*eps*10, con
	 * cushion de 10x), en vez de devolver basura numerica en silencio -- mismo
	 * espiritu que los hallazgos 3 y 5. Confirmado con Jordan [[50,1],[0,50]]:
	 * sinTaylor() pasaba de -31194.8 (exacto: -0.2624) a lanzar excepcion; el propio
	 * test preexistente TestTaylorSeries02/03/04.java confirma el bug en produccion,
	 * sin relacion con mi caso sintetico (aMatrix=[32.0] escalar: Sin Taylor=0.552733
	 * frente a Sin Euler=0.551427, exacto, con Sin^2+Cos^2=1.0036 en vez de 1.0). La
	 * API publica sin()/cos() no esta expuesta (usan sinEuler()/cosEuler() desde el
	 * hallazgo 4); sinh()/cosh() tampoco (sin cancelacion, terminos de un solo signo).
	 *
	 * 1.21 (2026_0731_2330)
	 * Detectores de divergencia en trigonTaylor()/trigonHyperbolycTaylor()/exp_()
	 * (auditoria matematica, hallazgo 5): mismo patron ya usado en logTaylor/
	 * logMercator/logHat (accumulator>500 tras k>100 -> IllegalArgumentException)
	 * en vez de iterar en silencio hasta maxIter (~10^8). exp_() ademas convierte
	 * el "if (errMatrix.isNaN()) break;" ya existente (silencioso) en excepcion.
	 * Verificado sin cambio de comportamiento en casos bien condicionados.
	 *
	 * 1.20 (2026_0731_2230)
	 * normalize2PI() eliminado (auditoria matematica, hallazgo 4): reducia la
	 * matriz por su norma euclidea global antes de sin()/cos()/tan()/euler(),
	 * invalido matematicamente -- la periodicidad de sin/cos es por autovalor,
	 * no por la norma conjunta de la matriz. Corrompia silenciosamente sin()/
	 * cos()/tan() para matrices no diagonalizables con norma grande. Quitada la
	 * llamada de trigonTaylor()/sinEuler()/cosEuler()/euler()/euler(MatrixComplex);
	 * sin()/cos() pasan a usar sinEuler()/cosEuler() (formula de Euler via exp(),
	 * ya correcto) como fallback en vez de sinTaylor()/cosTaylor(), igual que ya
	 * hacia tan()/tanEuler(). normalize2PI() eliminado del todo (sin llamadores).
	 *
	 * 1.19 (2026_0731_2130)
	 * logTaylor()/logMercator()/logHat() (auditoria matematica, hallazgo 3): el
	 * detector de divergencia ya existente (accumulator>500) devolvia
	 * this.divides(Complex.ZERO) -- Infinity/NaN en silencio -- al dispararse.
	 * Ahora lanza IllegalArgumentException. No arregla la convergencia en si
	 * (decision de alcance explicita: el fix real, scaling-and-squaring inverso,
	 * queda aplazado para otra sesion), solo deja de devolver basura sin avisar.
	 *
	 * 1.18 (2026_0731_2030)
	 * Pivoteo parcial proactivo en inverse()/triangleUp()/triangleLo() (auditoria
	 * matematica, paso 3, hallazgo 2): antes solo se pivotaba si el pivote actual
	 * era EXACTAMENTE cero; ahora siempre se pivota a la fila de mayor modulo de
	 * la columna, evitando amplificacion de error de redondeo con pivotes pequeños
	 * pero no nulos. Nuevo helper privado partialPivotUp(int) (espejo de
	 * partialPivot(int), para triangleLo()).
	 *
	 * 1.17 (2026_0731_1900)
	 * Los 25 System.exit(1) del fichero sustituidos por IllegalArgumentException
	 * (auditoria matematica, paso 3): plus/minus (dimensiones), power(MatrixComplex),
	 * exp/trigonTaylor/trigonHyperbolycTaylor/logTaylor/logMercator/logHat (no cuadrada),
	 * p_norm (orden<=0), trace/cotrace (no cuadrada), minor/cofactors (indice de pivote),
	 * determinantGauss/determinant3/determinantAdj (no cuadrada), subMatrix/subMatrixAug
	 * (rango fuera de la matriz), charactPoly/cofactor (no cuadrada).
	 *
	 * 1.16 (2025_0131_2358)
	 * 	private static double __log10__ = 2.30258509299405;
	 * 	public static MatrixComplex log(MatrixComplex matrix)
	 * 	public static MatrixComplex log10(MatrixComplex matrix)
	 * 	public String toString()
	 * 	public MatrixComplex sin()
	 * 	public MatrixComplex cos()
	 * 	public MatrixComplex tan()
	 * 	public MatrixComplex sinh()
	 * 	public MatrixComplex cosh()
	 * 	public MatrixComplex tanh()
	 * 	public MatrixComplex log()
	 * 	public MatrixComplex logTaylor()
	 * 	public MatrixComplex logMercator()
	 * 	public MatrixComplex exp()
	 * 	public MatrixComplex exp_()
	 * 	public MatrixComplex minusMat(Complex cNum)
	 * 	public MatrixComplex minusMat(double dNum)
	 * 	public MatrixComplex minusMat(int iNum)
	 * 	public MatrixComplex plusMat(Complex cNum)
	 * 	public MatrixComplex plusMat(int iNum)
	 * 	public MatrixComplex log()
	 * 	public MatrixComplex ppower(int iExp)
	 * 	public MatrixComplex ppower(double dExp)
	 * 	public MatrixComplex ppower(Complex cExp)
	 * 	public MatrixComplex ssin()
	 * 	public static MatrixComplex ssin(MatrixComplex matrix)
	 * 	public MatrixComplex ccos()
	 * 	public static MatrixComplex ccos(MatrixComplex matrix)
	 * 	public MatrixComplex ttan()
	 * 	public static MatrixComplex ttan(MatrixComplex matrix)
	 * 	public MatrixComplex ssinh()
	 * 	public static MatrixComplex ssinh(MatrixComplex matrix)
	 * 	public MatrixComplex ccosh()
	 * 	public static MatrixComplex ccosh(MatrixComplex matrix)
	 * 	public MatrixComplex ttanh()
	 * 	public static MatrixComplex ttanh(MatrixComplex matrix)
	 * 	public MatrixComplex llog()
	 * 	public static MatrixComplex llog(MatrixComplex matrix)
	 * 	public MatrixComplex llog10()
	 * 	public static MatrixComplex llog10(MatrixComplex matrix)
	 * 	public MatrixComplex llogbase(Complex base)
	 * 	public MatrixComplex llogbase(double base)
	 * 	public static MatrixComplex llogbase(MatrixComplex matrix, double base)
	 *	public MatrixComplex minor(int rowPivot, int colPivot) {
	 *	public MatrixComplex cofactors(int rowPivot, int colPivot) {
	 *	public MatrixComplex orthogonalize()
	 *	public MatrixComplex orthonormalize()
	 *  public MatrixComplex kroneckerprod(MatrixComplex matrix) {
	 * 
	 *
	 * 1.15 (2024_0408_1400)
	 *  private MatrixComplex trigonTaylor(int sign)
	 * 	private enum hyptrigon {SINH, COSH};
	 *  private MatrixComplex trigonHyperbolycTaylor(hyptrigon hypFunc) {
	 *  public MatrixComplex sinhTaylor() {
	 *  public MatrixComplex coshTaylor() {
	 *  public MatrixComplex completeRows() {
	 * 
	 * 1.14 (2024_0320_1945)
	 * 	public MatrixComplex log10() {
	 * 	public static MatrixComplex log10(MatrixComplex matrix) {
	 * 	public MatrixComplex logbase(Complex base) {
	 * 	public static MatrixComplex logbase(MatrixComplex matrix, Complex base) {
	 * 	public MatrixComplex logbase(Complex base) {
	 * 	public static MatrixComplex logbase(MatrixComplex matrix, Complex base) {
	 * 	public static MatrixComplex power(Complex cBase, MatrixComplex exponent) {
	 * 	public static MatrixComplex power(double base, MatrixComplex exponent) {
	 * 	private MatrixComplex trigonTaylor(int sign) {
	 *  public MatrixComplex sinTaylor() {
	 *  public MatrixComplex sinEuler() {
	 *  public MatrixComplex cosTaylor() {
	 *  public MatrixComplex cosEuler() {
	 * 	public MatrixComplex euler() {
	 * 	public static MatrixComplex euler(MatrixComplex matrix) {
	 * 	public MatrixComplex normalize2PI() {
	 *  REMOVED private MatrixComplex log1mx() {
	 *  REMOVED private MatrixComplex log1px() {
	 *  public MatrixComplex logTaylor() {
	 *  public MatrixComplex logMercator() {
	 *  public MatrixComplex power(MatrixComplex mcExpo) {
	 *  public static MatrixComplex power(MatrixComplex mcBase, MatrixComplex mcExpo) {
	 * 
	 *  
	 * 1.13 (2024_0120_2330)
	 *  public MatrixComplex exp() {
	 *  public MatrixComplex sinTaylor() {
	 *  public MatrixComplex sinEuler() {
	 *  public MatrixComplex sin() {
	 *  public MatrixComplex cosTaylor() {
	 *  public MatrixComplex cosEuler() {
	 *  public MatrixComplex cos() {
	 *  public MatrixComplex tanTaylor() {
	 *  public MatrixComplex tanEuler() {
	 *  public MatrixComplex tan() {
	 *  public MatrixComplex sinhTaylor() {
	 *  public MatrixComplex sinhEuler()
	 *  public MatrixComplex sinh() {
	 *  public MatrixComplex coshTaylor() {
	 *  public MatrixComplex coshEuler() {
	 *  public MatrixComplex cosh() {
	 *  public MatrixComplex tanhTaylor() {
	 *  public MatrixComplex tanhEuler() {
	 *  public MatrixComplex tanh() {
	 *  private MatrixComplex log1mx() {
	 *  private MatrixComplex log1px() {
	 *  public MatrixComplex logTaylor() {
	 *  public MatrixComplex logMercator() {
	 *  public MatrixComplex logHat() {
	 *  public MatrixComplex log() {
	 *  public boolean sameDimension(MatrixComplex matrix) {
	 *  public Complex totalize() {
	 *  public boolean isGT(MatrixComplex matrix) {
	 *  public boolean isGTE(MatrixComplex matrix) {
	 *  public boolean isLT(MatrixComplex matrix) {
	 *  public boolean isLTE(MatrixComplex matrix) {
	 *  public boolean isPostiveSemiDefinite() {
	 *  public boolean isNegtiveDefinite() {
	 *  public boolean isNegtiveSemiDefinite() {
	 *  public boolean hasZeroMainDiag() {
	 *  public boolean repPositiveMainDiag() {
	 *  public MatrixComplex plusMat(Complex cNum) {
	 *  public MatrixComplex plusMat(String strcNum) {
	 *  public MatrixComplex plusMat(double rep, double imp) {
	 *  public MatrixComplex minusMat(Complex cNum) {
	 *  public MatrixComplex minusMat(String strcNum) {
	 *  public MatrixComplex minusMat(double rep, double imp) {
	 *  private void doPlot(String Title, double[][] dataTable) {
	 * 
	 * 1.12 (2023_0507_1800)
	 * 	public MatrixComplex times(MatrixComplex cMatrix)
	 *  	Removed because makes to enter in an infinite loop call
	 *  		if (this.rows() == 1 && this.cols() == 1) return cMatrix.times(this.getItem(0, 0));
	 *  		if (cMatrix.rows() == 1 && cMatrix.cols() == 1) return this.times(cMatrix.getItem(0, 0));
	 * 
	 * 1.11 (2022_0319_2359)
	 * public MatrixComplex base()
	 * 
	 * 1.10 (2022_0123_0100)
	 *  kernel of a basis' generator of vectors
	 * 		kernel(Complex lambda)
	 * 		kernel()
	 * 		ker(Complex lambda)
	 * 		ker()
	 * 	public MatrixComplex normalize(): shortcut to normalizeByRows()
	 * 	public MatrixComplex normalizeByCols()
	 * 	public MatrixComplex normalizeByRows()
	 *  public void setCol(int colIdx, Complex cValue)
	 *  public void setRow(int rowIdx, Complex cValue)
	 *  public MatrixComplex rowReduce()
	 *  public MatrixComplex getCol(int colIdx)
	 *  public boolean isTriangleLo() Missing curly braces and remade the method
	 *  public MatrixComplex gramSchmidt() Reprogrammed
	 *  public MatrixComplex gramSchmidtFull() Reprogrammed
	 *  public MatrixComplex gramSchmidtMFull() Reprogrammed
	 *  public MatrixComplex gramSchmidtM() Reprogrammed
	 *  public int rank3() is now the rank method
	 *  public boolean isEmpty()
	 * 
	 * 1.9 (2022_0106_1400)
	 * solveGauss(Complex lambda, boolean Reduced) is now the method used to solve Equation Systems
	 * 		It uses the Gaussian Elimination and Back Substitution from a diagonalized matrix. 
	 * 		The matrix is "perfect" diagonalized to accommodate the rows in the position into the system where the not null main diagonal terms occupy the row corresponding with their column number
	 * 		It takes on account the use of the value lambda for the unknown, and so on, to find the base solution "solBase" for each equation
	 * 		The solBase keeps the fixed values for the unknowns with the null equations of the system
	 * 		With the solBase calculated, the rest of the solutions are calculated by the Gaussian Elimination and Back Substitution method
	 * 		solBase is constructed with the complex number parameter lambda
	 * MatrixComplex power(int power). Now power can be negative.
	 * 
	 * 1.8 (2021_1106_1400)
	 * augment(matrixComplex interms) is now using augment2(matrixComplex interms) which returns an augmented matrix with full columns. 
	 * augment1(matrixComplex interms) is DEPRECATED and is kept only for recovery.
	 * gramSchmidtGauss() added. It should be used only with square matrices.
	 * 
	 * 1.7 (2021_0929_2000)
	 * solveGauss is now using the solveGauss2 Method. solveReduction and solveSubstition dosen't work right and are deprecated.
	 */

	/* 
	 * ***********************************************
	 * MATH & PROGRAM CONSTANTS 
	 * ***********************************************
	 */
	static double __log10__ = 2.30258509299405;

	/* 
	 * ***********************************************
	 * INTERNAL FLAGS 
	 * ***********************************************
	 */
	private int mSign = 1; //Tracks the correct sign in the determinants calculated through triangularization (Chio's rule)

	/*
	 * ***********************************************
	 * 	VERSION 
	 * ***********************************************
	 */
	
	/**
	 * Prints Class Version
	 */
	public static void version() {
		trace("VERSION:" + VERSION); 
	}
	
	/**
	 * Enumeration that selects the possible output format for some expressions 
	 * MATRIXCOMPLEX, 
	 * MAXIMA, 
	 * OCTAVE, 
	 * MATLAB, 
	 * WOLFRAM
	 */
	public enum outputFormat {MATRIXCOMPLEX, MAXIMA, OCTAVE, MATLAB, WOLFRAM};

	/*
	 * __DEBUG__
	 */
	
	private static boolean __DEBUG__ = false;
	
	public static void debugON() {
		__DEBUG__ = true;
	}

	public static void debugOFF() {
		__DEBUG__ = false;
	}

	public static boolean debug() {
		return __DEBUG__;
	}

	private static boolean __DOPLOT__ = true;

	public static void doPlotON() {
		__DOPLOT__ = true;
	}

	public static void doPlotOFF() {
		__DOPLOT__ = false;
	}

	public static boolean doPlot() {
		return __DOPLOT__;
	}

	static void trace(String cadena) {
		if (__DEBUG__) System.out.println("--- TRACE --- " + HEADINFO + cadena);
	}

	static void trace(MatrixComplex mat, String cadena) {
		if (__DEBUG__) mat.println("--- TRACE --- " + HEADINFO + cadena);
	}

	static void trace(Complex complex, String cadena) {
		if (__DEBUG__) complex.println("--- TRACE --- " + HEADINFO + cadena);
	}
	
	/*
	 * ***********************************************
	 * 	CONSTRUCTORS 
	 * ***********************************************
	 */

	/**
	 * Returns the empty complex square matrix object of length 0.
	 */
 	public MatrixComplex() {
		this.complexMatrix = new Complex[0][0];
	}

	/**
	 * Returns the complex square matrix object of length len.
	 * @param len length of the square matrix.
	 */
	public MatrixComplex(int len) {
		this.complexMatrix = new Complex[len][len];
		for (int row = 0; row < len; ++row)
			for (int col = 0; col < len; ++col)
				this.complexMatrix[row][col] = new Complex();
	}

	/**
	 * Instantiates the complex matrix object of dimensions rowLen x colLen.
	 * @param rowLen Number of rows.
	 * @param colLen Number of columns.
	 */
	public MatrixComplex(int rowLen, int colLen) {
		this.complexMatrix = new Complex[rowLen][colLen];
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				this.complexMatrix[row][col] = new Complex();
	}

	/**
	 * Instantiates the complex matrix object from a string representation of the matrix where rows are separated with ";", columns are separated with ",".
	 * @param cadena the string with the rows and columns.
	 */
	public MatrixComplex(String cadena) {
		this.setMatrix(cadena);
	}

	/**
	 * Method for creating a complex matrix object from a string representation of the matrix where rows are separated with ";", columns are separated with ",".
	 * @param cadena the string representation of the matrix.
	 */
	public void setMatrix(String cadena) {
		String[] sRow = cadena.split(";");
		int rowLen = sRow.length;
		String[] sCol = sRow[0].split(",");
		int colLen = sCol.length;
		complexMatrix = new Complex[rowLen][colLen];
		for (int row = 0; row < rowLen; ++row) {
			sCol = sRow[row].split(",");
			colLen = sCol.length;
			for (int col = 0; col < colLen; ++col)
				complexMatrix[row][col] = new Complex(sCol[col].trim());
		}
	}

	/*
	 * ***********************************************
	 * INITIALIZERS
	 * ***********************************************
	 */
	
	/**
	 * Method for initializing a complex matrix with all its items set to the value csNum.
	 * @param csNum String expression of the number in rectangular "A+Bi" or polar "A|B" coordinates.
	 */
	public void initMatrix(String csNum) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cNum = new Complex(csNum);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				this.complexMatrix[row][col].setComplexRec(cNum.rep(), cNum.imp());
	}

	/**
	 * Method for initializing a complex matrix with all its items set to the value cNum.
	 * @param cNum Complex number.
	 */
	public void initMatrix(Complex cNum) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				this.complexMatrix[row][col].setComplexRec(cNum.rep(), cNum.imp());
	}

	/**
	 * Method for initializing a complex matrix with all its items set to the value from the two parts of the complex both Cartesian or Polar representation.
	 * @param coordType Type of coordinate "C" Cartesian, "P" Polar.
	 * @param n1 Value of the first coordinate.
	 * @param n2 Value of the second coordinate.
	 */
	private void initMatrix(char coordType, double n1, double n2) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				switch (coordType) {
					case 'C':
					case 'c':this.complexMatrix[row][col].setComplexRec(n1, n2); 
					break;
					case 'P':
					case 'p':this.complexMatrix[row][col].setComplexPol(n1, n2); 
					break;
				}
	}

	/**
	 * Shortcut of initMatrixRec.
	 * Initializes an array with a complex value in Cartesian coordinates specified as real part and imaginary part.
	 * @param n1 Real Part.
	 * @param n2 Imaginary Part.
	 */    
	public void initMatrix(double n1, double n2) {
		this.initMatrixRec(n1, n2);
	}

	/**
	 * Initializes an array with a complex value in Cartesian coordinates specified as real part and imaginary part.
	 * @param n1 Real Part.
	 * @param n2 Imaginary Part.
	 */
	public void initMatrixRec(double n1, double n2) {
		this.initMatrix('C', n1, n2);
	}

	/**
	 * Initializes an array with a complex value in polar coordinates specified as module and phase.
	 * @param n1 Module.
	 * @param n2 Phase.
	 */
	public void initMatrixPol(double n1, double n2) {
		this.initMatrix('P', n1, n2);
	}

	/**
	 * Initializes the main diagonal of an array with the complex number indicated in text.
	 * @param csNum Complex number in text format.
	 */
	public void initMatrixDiag(String csNum) {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cNum = new Complex(csNum);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (row == col) this.complexMatrix[row][col].setComplexRec(cNum.rep(), cNum.imp());
				else this.complexMatrix[row][col].setComplexRec(0, 0);
	}

	/**
	 * Private method to initialize the main diagonal of a matrix with a complex number expressed in Cartesian or polar coordinates.
	 * @param coordType Coordinate type 'C' or 'P'.
	 * @param n1 coordinate 1.
	 * @param n2 coordinate 2.
	 */
	private void initMatrixDiag(char coordType, double n1, double n2) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (row == col) {
					switch (coordType) {
						case 'C':
						case 'c':this.complexMatrix[row][col].setComplexRec(n1, n2); 
						break;
						case 'P':
						case 'p':this.complexMatrix[row][col].setComplexPol(n1, n2); 
						break;
					}
				}
				else this.complexMatrix[row][col].setComplexRec(0, 0);
	}

	/**
	 * Shortcut to initMatrixDiagRec
	 * Initializes the main diagonal of a matrix with the complex number in Cartesian coordinates indicated as real part and imaginary part.
	 * @param n1 Real part.
	 * @param n2 Imaginary Part.
	 */
	public void initMatrixDiag(double n1, double n2) {
		this.initMatrixDiagRec(n1, n2);
	}

	/**
	 * Shortcut to initMatrixDiagRec
	 * Initializes the main diagonal of a matrix with the complex number cNum.
	 * @param cNum the complex number
	 */
	public void initMatrixDiag(Complex cNum) {
		this.initMatrixDiagRec(cNum.rep(), cNum.imp());
	}

	/**
	 * Initializes the main diagonal of a matrix with the complex number in Cartesian coordinates indicated as real part and imaginary part.
	 * @param n1 Real part.
	 * @param n2 Imaginary Part.
	 */
	public void initMatrixDiagRec(double n1, double n2) {
		this.initMatrixDiag('C', n1, n2);
	}

	/**
	 * Initializes the main diagonal of an array with the complex number in polar coordinates indicated as module and phase.
	 * @param n1 Module.
	 * @param n2 Phase.
	 */
	public void initMatrixDiagPol(double n1, double n2) {
		this.initMatrixDiag('P', n1, n2);
	}

	/**
	 * Private method to initialize the main diagonal of an array with a base random number and a type of coordinate.
	 * @param coordType Type of coordinate 'A' Real, 'B' Integer, 'C' Cartesian Complex, 'D', Pure imaginary, 'E' Imaginary pure integer, 'P' Polar Complex, 'I' Complex Cartesian Integers, 'J' Polar Integers.
	 * @param base Base to generate the random number.
	 */
	private void initMatrixRandom(char coordType, int base) {
		int rowLen = this.rows();
		int colLen = this.cols();

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				switch (coordType) {
					case 'A':
					case 'a':this.complexMatrix[row][col].setComplexRandomReal(base); 
					break;
					case 'B':
					case 'b':this.complexMatrix[row][col].setComplexRandomInt(base); 
					break;
					case 'C':
					case 'c':this.complexMatrix[row][col].setComplexRandomRec(base); 
					break;
					case 'D':
					case 'd':this.complexMatrix[row][col].setComplexRandomImag(base); 
					break;
					case 'E':
					case 'e':this.complexMatrix[row][col].setComplexRandomImagInt(base); 
					break;
					case 'P':
					case 'p':this.complexMatrix[row][col].setComplexRandomPol(base); 
					break;
					case 'I':
					case 'i':this.complexMatrix[row][col].setComplexRandomRecInt(base); 
					break;
					case 'J':
					case 'j':this.complexMatrix[row][col].setComplexRandomPolInt(base); 
					break;
				}
	}

	/**
	 * Initializes an array with a complex number of module 1 in Cartesian coordinates.
	 */
	public void initMatrixRandomRec() {
		this.initMatrixRandom('C', 1);
	}

	/**
	 * Initializes an array with a complex number of module 1 in polar coordinates.
	 */
	public void initMatrixRandomPol() {
		this.initMatrixRandom('P', 1);
	}

	/**
	 * Initializes an array with a real number between -1 and 1 in Cartesian coordinates.
	 */
	public void initMatrixRandomReal() {
		this.initMatrixRandom('A', 1);
	}

	/**
	 * Initializes an array with an integer between -1 and 1 in Cartesian coordinates.
	 */
	public void initMatrixRandomInt() {
		this.initMatrixRandom('B', 1);
	}

	/**
	 * Initializes an array with a pure imaginary number between -i and i in Cartesian coordinates.
	 */
	public void initMatrixRandomImag() {
		this.initMatrixRandom('D', 1);
	}

	/**
	 * Initializes an array with an integer pure imaginary number between -i and i in Cartesian coordinates.
	 */
	public void initMatrixRandomImagInt() {
		this.initMatrixRandom('E', 1);
	}

	/**
	 * Initializes an array with a complex module number between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomRec(int base) {
		this.initMatrixRandom('C', base);
	}

	/**
	 * Initializes an array with a complex module number between 0 and base in polar coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomPol(int base) {
		this.initMatrixRandom('P', base);
	}

	/**
	 * Initializes an array with a module integer between 0 and base in Cartesian coordinates
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomRecInt(int base) {
		this.initMatrixRandom('I', base);
	}

	/**
	 * Initializes an array with a complex integer number between 0 and base in polar coordinates
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomPolInt(int base) {
		this.initMatrixRandom('J', base);
	}

	/**
	 * Initializes an array with a real module number between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomReal(int base) {
		this.initMatrixRandom('A', base);
	}

	/**
	 * Initializes an array with a complex number of real module between 0 and base in polar coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomInt(int base) {
		this.initMatrixRandom('B', base);
	}

	/**
	 * Initializes an array with an imaginary module number between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomImag(int base) {
		this.initMatrixRandom('D', base);
	}

	/**
	 * Initializes an array with an imaginary module integer between 0 and base in Cartesian coordinates.
	 * @param base Base to generate the number.
	 */
	public void initMatrixRandomImagInt(int base) {
		this.initMatrixRandom('E', base);
	}

	/**
	 * Initializes an array sequentially with an integer from 0 with steps of 1 in 1 in polar coordinates.
	 */
	public void initMatrixSeqInt() {
		this.initMatrixSeqInt(0, 1);
	}

	/**
	 * Initializes an array sequentially with a real number from first with steps of 1 in 1 in polar coordinates.
	 * @param first Starting number.
	 */
	public void initMatrixSeqInt(double first) {
		this.initMatrixSeqInt(first, 1);
	}

	/**
	 * Initializes an array sequentially with a real number from first with increment "step" in polar coordinates.
	 * @param first Starting number.
	 * @param step Increment.
	 */
	public void initMatrixSeqInt(double first, double step) {
		int rowLen = this.rows();
		int colLen = this.cols();
		double val = first;
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col) {
				val += step;
				this.complexMatrix[row][col].setComplexPol(val,0);
			}
	}

	/*
	 * ***********************************************
	 * ALGEBRAIC BASIS
	 * REMOVED
	 * No tiene sentido manejar bases en MatrixComplex
	 * Se reprograman en la clase Vector
	 * REMOVED
	 * ***********************************************
	 */

	/**
	 * Creates a base of a vector space.
	 * Allows the input of a set vectors written by rows, the base. 
	 * @param str the base vectors.
	 * /
	public void base(String str) {
		MatrixComplex setOfVectors = new MatrixComplex(str); 
		this.complexMatrix = setOfVectors.complexMatrix.clone(); 
	}

	/**
	 * Calculates an orthonormal base associated to the first row component of the matrix
	 * @return the orthonormal base
	 * /	
	public MatrixComplex base() {
		/*
		MatrixComplex vectorBase = new MatrixComplex(this.rows(), this.cols());
		vectorBase.initMatrixDiag(1, 0);
		* /
		MatrixComplex vectorBase = MatrixComplex.eye(this.rows());
		MatrixComplex normal = this.getRow(0).normalize();
		//Check for unitary vector and gets its position
		Complex sum = new Complex();
		int pos = -1;
		for (int i = 0; i < this.cols(); ++i) {
			if (normal.getItem(0, i).equals(Complex.ZERO)) continue;
			pos = i;
			sum = sum.plus(normal.getItem(0, i));
		}
		// if unitary vector is found, put the base row in its position
		// else use position 0
		if (!sum.equals(Complex.ONE)) pos = 0;
		vectorBase.setRow(pos, normal);
		vectorBase = vectorBase.gramSchmidt();
		return vectorBase;		
	}

	/**
	 * Creates a new base of a vector space.
	 * Allows the input of a set vectors written by rows, the base. 
	 * @param str the base vectors.
	 * @return The new matrix with the base.
	 * /
	public MatrixComplex newBase(String str) {
		MatrixComplex setOfVectors = new MatrixComplex(str);
		return setOfVectors;
	}

	/*
	 * ***********************************************
	 * INTERNAL FUNCTIONS
	 * ***********************************************
	 */

	/**
	 * Returns the number of rows of the array
	 * @return The number of rows
	 */
	public int rows() {
		int rows = 0;
		try {
			rows = this.complexMatrix.length;
		}
		catch (Exception e){
			rows = 0;
		}
		return rows;
	}
	
	/**
	 * Returns the number of columns of the array
	 * @return The number of columns
	 */
	public int cols() {
		int cols = 0;
		
		try {
			cols = this.complexMatrix[0].length;
		}
		catch (Exception e) {
			cols = 0;
		}
		return cols;
	}
	
	/**
	 * Gets the item(row, col) of the array
	 * @param row
	 * @param col
	 * @return
	 */
	public Complex getItem(int row, int col) {
		return this.complexMatrix[row][col].copy();
	}
	
	/**
	 * Sets the item(row, col) of the array to a specific double number
	 * @param row
	 * @param col
	 * @param numD
	 */
	public void setItem(int row, int col, double numD) {
		this.complexMatrix[row][col].setComplexRec(numD, 0);
	}
	
	/**
	 * Sets the item(row, col) of the array to a specific complex number
	 * @param row
	 * @param col
	 * @param numC
	 */
	public void setItem(int row, int col, Complex numC) {
		this.complexMatrix[row][col] = numC.copy();
		// this.complexMatrix[row][col].setComplexRec(numC.rep(), numC.imp());
	}
	
	/**
	 * Sets the item(row, col) of the array to a specific complex number in string format
	 * @param row
	 * @param col
	 * @param snumC
	 */
	public void setItem(int row, int col, String snumC) {
		this.complexMatrix[row][col].setComplex(snumC);
	}
	
	/**
	 * Private method to return the components of a string separated by a delimiter. The components have to be integers.
	 * @param str String to chop.
	 * @param delimiter Delimiter of tokens.
	 * @return Array of integers with the tokens.
	 */
	int[] intSplit(String str, String delimiter) {
		String[] sItem = str.split(delimiter);
		int[] token = new int[sItem.length];
		for (int i = 0; i < sItem.length; ++i) {
			token[i] = Integer.parseInt(sItem[i]);
		}
		return token;
	}

	/**
	 * Returns the best number of significant decimals based on the condition number
	 * @return the best number of significant decimals
	 */
	public int bestNumDecs() {
		int numDecs = (int)(this.cond()/2);
		return numDecs > Complex.getSignificative() ? Complex.getSignificative() : numDecs;
	}
	
	/**
	 * Completes the matrix filling it up with zero's rows to make it a square matrix
	 * @return The new square matrix filled up with new zero's rows
	 */
	public MatrixComplex completeRows() {
		MatrixComplex matrixCompleted = new MatrixComplex(this.cols());
		
		for (int row = 0; row < this.rows(); ++row) {
			matrixCompleted.setRow(row, this.getRow(row));
		}
		return matrixCompleted;
	}
		
	/*
	 * ***********************************************
	 * COPY & REPLICATION
	 * ***********************************************
	 */
	
	/**
	 * Copies the values of the array into another object.
	 * @return The new array with the copy.
	 */
	public MatrixComplex copy() {
		int rowLen = this.rows();
		int colLen = this.cols();
		MatrixComplex cMatrix = new MatrixComplex(rowLen, colLen);
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].copy();
		return cMatrix;
	}

	/**
	 * Clones the values of the array into another object.
	 * @return The new array with the clone.
	 */
	public MatrixComplex clone() {
		MatrixComplex cMatrix = this.copy();
		cMatrix.mSign = this.mSign;
		return cMatrix;
	}

	/*
	 * ***********************************************
	 * PRINTING
	 * ***********************************************
	 */

	/**
	 * Prints the array of values without carriage return.
	 */
	public void print() {
		MatrixComplexFormat.print(this);
	}

	/**
	 * Prints the array of values with a carriage return.
	 */
	public void println() {
		MatrixComplexFormat.println(this);
	}

	/**
	 * Prints a title and then in a new line the array of values without carriage return.
	 * @param caption The title above the matrix.
	 */
	public void print(String caption) {
		MatrixComplexFormat.print(this, caption);
	}

	/**
	 * Prints a title and then in a new line the array of values with a carriage return.
	 * @param caption The title above the matrix.
	 */
	public void println(String caption) {
		MatrixComplexFormat.println(this, caption);
	}

	/**
	 * Private method that presents the matrix enclosed in brackets.
	 * Each line corresponds to a row of the array.
	 * The columns are separated by commas.
	 */
	public String toString() {
		return MatrixComplexFormat.toString(this);
	}

	/**
	 * Displays a title and selected column
	 * @param col The selected column
	 * @param caption The title
	 */
	public void println(int col, String caption) {
		MatrixComplexFormat.println(this, col, caption);
	}

	/**
	 * Returns a string with the array expression in the format used by Maxima (Computer Algebra System)
	 * @return The string with the array in Maxima format.
	 */
	public String toMaxima() {
		return MatrixComplexFormat.toMaxima(this);
	}

	/**
	 * Returns a string with the array expression in the format used by Wolfram Mathematica.
	 * @return The string with the array in Wolfram Mathematica format.
	 */
	public String toWolfram() {
		return MatrixComplexFormat.toWolfram(this);
	}

	/**
	 * Returns a string with the array expression in the format used by Matlab.
	 * @return The string with the array in Matlab format.
	 */
	public String toMatlab() {
		return MatrixComplexFormat.toMatlab(this);
	}

	/**
	 * Returns a string with the array expression in the format used by GNU Octave.
	 * @return The string with the array in GNU Octave format.
	 */
	public String toOctave() {
		return MatrixComplexFormat.toOctave(this);
	}

	/**
	 * Prepares the matrix string for toMatrixComplex
	 * @return the matrix string
	 */
	public String preMatrixComplex() {
		return MatrixComplexFormat.preMatrixComplex(this);
	}

	/**
	 * Returns a string with the array expression in the format used by Matrix Complex.
	 * @return The string with the array in MatrixComplex format.
	 */
	public String toMatrixComplex() {
		return MatrixComplexFormat.toMatrixComplex(this);
	}

	/*
	 * ***********************************************
	 * COLS & ROWS OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Looks for from the row "col" to the last row of the array the element that complies that the indicated column is different from zero to use it as an exchange row in the diagonalization of arrays.
	 * @param col Column to check.
	 * @return The index of the row found or -1 if it does not find it.
	 */
	public int locateSwapRowUp(int col) {
		int row;

		for (row = col; row < this.rows(); ++row)
			if (!this.complexMatrix[row][col].equals(0,0)) 
				break;
		return (row == this.rows()) ? -1 : row;
	}

	/**
	 * Looks for from the first row to the row "col-1" of the array that complies that the indicated column is different from zero to use it as an exchange row in the diagonalization of arrays.
	 * @param col Column to check.
	 * @return The index of the row found or -1 if it does not find it.
	 */
	public int locateSwapRowDown(int col) {
		int row;

		for (row = 0; row < col; ++row)
			if (!this.complexMatrix[row][col].equals(0,0)) 
				break;
		return row;
	}

	/**
	 * Returns the row from "rowIni" whose column from "rowIni" to the last one has the maximum value in module.
	 * @param rowIni Row and column from which the maximum search starts.
	 * @return The index of the row with the value in maximum nonzero module or -1 if the value was not found.
	 */
	public int partialPivot(int rowIni) {
		int rowLen = this.rows();
		int colP = rowIni;
		int rowMax = rowIni;
		Complex cMax = new Complex(this.complexMatrix[rowIni][rowIni].rep(), this.complexMatrix[rowIni][rowIni].imp());
		for (int row = rowIni; row < rowLen; ++row) {
			if (this.complexMatrix[row][colP].mod() > cMax.mod())  {
				cMax = this.complexMatrix[row][colP];
				rowMax = row;
			}
		}
		if (cMax.equals(0,0))
			return -1;
		return rowMax;
	}

	/**
	 * Returns the row from 0 to "rowEnd" whose column "rowEnd" has the maximum value in module. Mirror of partialPivot(int),
	 * searching upwards from "rowEnd" to row 0 instead of downwards, for triangularizations that eliminate towards row 0 (e.g. triangleLo()).
	 * @param rowEnd Row and column from which the maximum search ends (search goes from row 0 to rowEnd, inclusive).
	 * @return The index of the row with the value in maximum nonzero module or -1 if the value was not found.
	 */
	int partialPivotUp(int rowEnd) {
		int colP = rowEnd;
		int rowMax = rowEnd;
		Complex cMax = new Complex(this.complexMatrix[rowEnd][rowEnd].rep(), this.complexMatrix[rowEnd][rowEnd].imp());
		for (int row = rowEnd; row >= 0; --row) {
			if (this.complexMatrix[row][colP].mod() > cMax.mod())  {
				cMax = this.complexMatrix[row][colP];
				rowMax = row;
			}
		}
		if (cMax.equals(0,0))
			return -1;
		return rowMax;
	}

	/**
	 * Swaps the rows "row1" and "row2" in the array and updates the sign variable to correctly evaluate the determinant.
	 * @param row1 Row to swap.
	 * @param row2 Row to swap.
	 */
	public void swapRows(int row1, int row2) {
		int colLen = this.cols();
		MatrixComplex pivot = new MatrixComplex(1, colLen);

		if (row1 == row2) return;
		pivot.complexMatrix[0] = this.complexMatrix[row1];
		this.complexMatrix[row1] = this.complexMatrix[row2];
		this.complexMatrix[row2] = pivot.complexMatrix[0];
		this.mSign = -this.mSign;
		//	System.out.println("swapRows this.mSign="+this.mSign);
	}

	/**
	 * Swaps the rows "row1" and "row2" in the array and updates the sign variable to correctly evaluate the determinant.
	 * Performs the swap by copying the values of the columns. Is the Long way to do it.
	 * @param row1 Row to swap.
	 * @param row2 Row to swap.
	 */
	public void swapRowsL(int row1, int row2) {
		Complex pivot ;

		if (row1 == row2) return;
		for (int col = 0; col < row1; ++col) {
			pivot = this.complexMatrix[row1][col];
			this.complexMatrix[row1][col] = this.complexMatrix[row2][col];
			this.complexMatrix[row2][col] = pivot;
		}
		this.mSign = -this.mSign;
	}
	
	/**
	 * Copies the value of the last column of the matrix "origMatrix" into the column indicated by "copyCol".
	 * @param origMatrix Array with values to be copied.
	 * @param copyCol Index of the column to place the copied values.
	 */
	void copyCol(MatrixComplex origMatrix, int copyCol) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				this.complexMatrix[row][col] = col == copyCol ? origMatrix.complexMatrix[row][colLen].copy() : origMatrix.complexMatrix[row][col].copy();
			}
		}	
	}

	/**
	 * Copies the value of the "copyCol" column of "origMatrix" in the "destCol" column of this.
	 * @param destCol target copy column.
	 * @param origMatrix Matrix in which values are copied.
	 * @param copyCol index to the column to be copied.
	 */
	public void copyCol(int destCol, MatrixComplex origMatrix, int copyCol) {
		int rowLen = this.rows();
		int row;

		for (row = 0; row < rowLen; ++row) {
			this.complexMatrix[row][destCol] = origMatrix.complexMatrix[row][copyCol].copy();
		}
	}

	/**
	 * Copies the value of the "copyRow" row of "origMatrix" in the "destRow" row of this.
	 * @param destRow target copy row.
	 * @param origMatrix Matrix in which values are copied.
	 * @param copyRow index to the row to be copied.
	 */
	public void copyRow(int destRow, MatrixComplex origMatrix, int copyRow) {
		int colLen = this.cols();
		int col;

		for (col = 0; col < colLen; ++col) {
			this.complexMatrix[destRow][col] = origMatrix.complexMatrix[copyRow][col].copy();
		}
	}

	/**
	 * Compares two matrices and return the result of the comparingif (__DEBUG__) 
	 * @param cMatrix The matrix to compare with
	 * @return The result of the comparing
	 */
	public boolean equals(MatrixComplex cMatrix) {
		int rowLen = this.rows();
		int colLen = this.cols();
		
		if (rowLen != cMatrix.rows()) return false;
		if (colLen != cMatrix.cols()) return false;
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (!this.complexMatrix[row][col].equals(cMatrix.complexMatrix[row][col])) return false;
		return true;
	}
	
	/**
	 * 
	 * @param cMatrix					matrix.println("row "+row+" col "+col);
	 * @param numdecs
	 * @return
	 */
	public boolean equals(MatrixComplex cMatrix, int numdecs) {
		int rowLen = this.rows();
		int colLen = this.cols();
		
		if (rowLen != cMatrix.rows()) return false;
		if (colLen != cMatrix.cols()) return false;
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				if (!this.complexMatrix[row][col].equals(cMatrix.complexMatrix[row][col], numdecs)) return false;
		return true;
	}

	/**
	 * Appends new NULL rows to the array (Only if the number of columns are the same in both arrays)
	 * @param newRows The new rows to append
	 * @return 
	 */
	public MatrixComplex appendRows(MatrixComplex newRows) {
		int rows = this.rows() + newRows.rows();
		int row;
		
		if ( this.cols() != 0 && this.cols() != newRows.cols()) {
			return this;
		}
		MatrixComplex newArray = new MatrixComplex(rows, this.cols());
		
		for (row = 0; row < this.rows(); ++row)
			newArray.setRow(row, this.getRow(row));
		for (int idx = 0; row < rows; ++row, ++idx)
			newArray.setRow(row, newRows.getRow(idx));
		
		return newArray;
	}
	
	/**
	 * Reorders the array by putting the last row in the first position and so on
	 */
	public void transrow() {
		MatrixComplex transrow = new MatrixComplex(this.rows(), this.cols());
		
		for (int row1 = 0, row2 = this.rows()-1; row1 < this.rows(); ++row1, --row2)
			transrow.setRow(row1, this.getRow(row2));
		
		this.complexMatrix = transrow.complexMatrix.clone();
	}
	
	/*
	 * ***********************************************
	 * ARITHMETIC OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Calculates the sum of two matrices.
	 * @param cMatrix the array to add.
	 * @return The matrix result from the sum.
	 */
	public MatrixComplex plus(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();		
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (rowLenA1 != rowLenA2 || colLenA1 != colLenA2) {
			throw new IllegalArgumentException("Not valid sum: The rows/cols of matrix1 has to be equal to the rows/cols of matrix2.");
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLenA1, colLenA2);

		for (int row = 0; row < rowLenA1; ++row) {
			for (int col = 0; col < colLenA1; ++col) {
				resultMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].plus(cMatrix.complexMatrix[row][col]);
			}
		}
		return resultMatrix;
	}

	/**
	 * THIS SUM MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE plusMat FOR A DIFFERENT APPROXIMATION OF THE ADITION BETWEEN A MATRIX AND A SCALAR.
	 * @param val
	 */
	public MatrixComplex plus(double val) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(val));
		return newThis;
	}

	/**
	 * THIS SUM MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE plusMat FOR A DIFFERENT APPROXIMATION OF THE ADITION BETWEEN A MATRIX AND A SCALAR.
	 * @param cVal
	 */
	public MatrixComplex plus(Complex cVal) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(cVal));
		return newThis;
	}

	/**
	 * This method returns the sum for this plus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(Complex cNum) {
		if (!this.isSquare()) {
			System.err.println("Not valid sum: This sum is only for square matrices.");
		}
		
		MatrixComplex cNumMat = new MatrixComplex(this.rows(), this.cols());
		cNumMat.initMatrixDiag(cNum);
		
		return this.plus(cNumMat);
	}
	
	/**
	 * This method returns the sum for this plus dNum*I, where I is the identity matrix.
	 * @param dNum the real number to construct the diagonal matrix dNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(double dNum) {
		Complex cNum = new Complex(dNum, 0);
		return plusMat(cNum);
	}
	
	/**
	 * This method returns the sum for this plus iNum*I, where I is the identity matrix.
	 * @param iNum the integer number to construct the diagonal matrix iNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(int iNum) {
		Complex cNum = new Complex(iNum, 0);
		return plusMat(cNum);
	}
	
	/**
	 * This method returns the sum for this plus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number in String format to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(String strcNum) {
		Complex cNum = new Complex(strcNum);
		return plusMat(cNum);
	}

	/**
	 * This method returns the sum for this plus cNum*I, where I is the identity matrix.
	 * @param rep the real part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @param imp the imaginary part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices sum.
	 */
	public MatrixComplex plusMat(double rep, double imp) {
		Complex cNum = new Complex(rep, imp);
		return plusMat(cNum);
	}
	
	/**
	 * Calculates the difference of two matrices.
	 * @param cMatrix the subtracting matrix.
	 * @return the matrix result of the difference.
	 */
	public MatrixComplex minus(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();		
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (rowLenA1 != rowLenA2 || colLenA1 != colLenA2) {
			throw new IllegalArgumentException("Not valid substraction: The rows/cols of matrix1 has to be equal to the rows/cols of matrix2.");
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLenA1, colLenA2);

		for (int row = 0; row < rowLenA1; ++row) {
			for (int col = 0; col < colLenA1; ++col) {
				resultMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].minus(cMatrix.complexMatrix[row][col]);
			}
		}
		return resultMatrix;
	}

	/**
	 * THIS SUBTRACTION MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE MINUSMat FOR A DIFFERENT APPROXIMATION OF THE SUBTRACTION BETWEEN A MATRIX AND A SCALAR.
	 * @param val
	 */
	public MatrixComplex minus(double val) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(val));
		return newThis;
	}

	/**
	 * THIS SUBTRACTION MAKES NO SENSE AT ALL. I INCLUDE IT FOR COMPATIBILITY WITH OTHER CALCULATION PROGRAMS.
	 * PLEASE SEE MINUSMat FOR A DIFFERENT APPROXIMATION OF THE SUBTRACTION BETWEEN A MATRIX AND A SCALAR.
	 * @param cVal
	 */
	public MatrixComplex minus(Complex cVal) {
		MatrixComplex newThis = new MatrixComplex(this.rows(), this.cols());
		for (int row = 0; row < this.rows(); ++row)
			for(int col = 0; col < this.cols(); ++col)
				newThis.setItem(row, col, this.getItem(row, col).plus(cVal));
		return newThis;
	}

	/**
	 * This method returns the difference for this minus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(Complex cNum) {
		if (!this.isSquare()) {
			System.err.println("Not valid sum: This sum is only for square matrices.");
		}
		
		MatrixComplex cNumMat = new MatrixComplex(this.rows(), this.cols());
		cNumMat.initMatrixDiag(cNum);
		
		return this.minus(cNumMat);
	}

	/**
	 * This method returns the difference for this minus dNum*I, where I is the identity matrix.
	 * @param dNum the real number to construct the diagonal matrix dNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(double dNum) {
		Complex cNum = new Complex(dNum, 0);
		return minusMat(cNum);
	}
	
	/**
	 * This method returns the difference for this minus iNum*I, where I is the identity matrix.
	 * @param iNum the integer number to construct the diagonal matrix iNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(int iNum) {
		Complex cNum = new Complex(iNum, 0);
		return minusMat(cNum);		
	}
	
	/**
	 * This method returns the difference for this minus cNum*I, where I is the identity matrix.
	 * @param cNum the complex number in String format to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(String strcNum) {
		Complex cNum = new Complex(strcNum);
		return minusMat(cNum);
	}

	/**
	 * This method returns the difference for this minus cNum*I, where I is the identity matrix.
	 * @param rep the real part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @param imp the imaginary part of the complex number cNum to construct the diagonal matrix cNum*I.
	 * @return The matrix resulting from the matrices difference.
	 */
	public MatrixComplex minusMat(double rep, double imp) {
		Complex cNum = new Complex(rep, imp);
		return minusMat(cNum);
	}

	/**
	 * Calculates the matrix product by a real scalar.
	 * @param num Real number.
	 * @return The result matrix of the product by the scalar.
	 */
	public MatrixComplex times(double num) {
		Complex cNum = new Complex(num, 0);
		return this.times(cNum);
	}

	/**
	 * Calculates the product of the matrix by a scalar complex
	 * @param cNum Complex number
	 * @return The result matrix of the product by the scalar
	 */
	public MatrixComplex times(Complex cNum) {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex resultMatrix = new MatrixComplex(rowLen, colLen);

		for (int rowf = 0; rowf < rowLen; ++rowf)
			for (int colf = 0; colf < colLen; ++colf)
				resultMatrix.complexMatrix[rowf][colf] = this.complexMatrix[rowf][colf].times(cNum);
		return resultMatrix;    	
	}

	/**
	 * Calculates the product of two matrices.
	 * @param cMatrix The multiplier matrix.
	 * @return The matrix resulting from the matrices product.
	 */
	public MatrixComplex times(MatrixComplex cMatrix) {
		int rowLenA1 = this.rows();
		int colLenA1 = this.cols();
		int rowLenA2 = cMatrix.rows();
		int colLenA2 = cMatrix.cols();

		if (colLenA1 != rowLenA2) {
			System.err.println("Not valid product: The cols of matrix1 has to be equal to the rows of matrix2.");
			//System.exit(1);
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLenA1, colLenA2);

		for (int rowf = 0; rowf < rowLenA1; ++rowf)
			for (int colf = 0; colf < colLenA2; ++colf)
				for (int iter = 0; iter < colLenA1; ++iter)
					resultMatrix.complexMatrix[rowf][colf] = resultMatrix.complexMatrix[rowf][colf].plus(this.complexMatrix[rowf][iter].times(cMatrix.complexMatrix[iter][colf]));
		return resultMatrix;
	}

	/**
	 * Calculates the matrix division by a real scalar.
	 * @param num Real number.
	 * @return The matrix resulting from the division by the scalar.
	 */
	public MatrixComplex divides(double num) {
		Complex cNum = new Complex(num, 0);
		return this.divides(cNum);
	}

	/**
	 * Calculates the division of the matrix by a complex scalar.
	 * @param cNum The complex number.
	 * @return The result matrix by the scalar division.
	 */
	public MatrixComplex divides(Complex cNum) {
		return this.times(cNum.reciprocal());
	}

	/**
	 * Calculates the division between two matrices as the product of the dividend by the inverse of the divisor matrix.
	 * @param cMatrix the divisor matrix.
	 * @return The matrix resulting from the division.
	 */
	public MatrixComplex divides(MatrixComplex cMatrix) {
		//return this.times(cMatrix.inverse());
		return this.dividesright(cMatrix);
	}

	/**
	 * Calculates the left division of two arrays as this⁻¹*cMatrix
	 * @param cMatrix The matrix to divide
	 * @return The left division
	 */
	public MatrixComplex dividesleft(MatrixComplex cMatrix) {
		if (this.rows() == 1 && this.cols() == 1) return cMatrix.inverse().times(this.getItem(0, 0));
		if (cMatrix.rows() == 1 && cMatrix.cols() == 1) return this.divides(cMatrix.getItem(0, 0));
		return this.inverse().times(cMatrix);
	}

	/**
	 * Calculates the right division of two arrays as this*cMatrix⁻¹
	 * @param cMatrix The matrix used as divisor
	 * @return The right division
	 */
	public MatrixComplex dividesright(MatrixComplex cMatrix) {
		if (this.rows() == 1 && this.cols() == 1) return cMatrix.inverse().times(this.getItem(0, 0));
		if (cMatrix.rows() == 1 && cMatrix.cols() == 1) return this.divides(cMatrix.getItem(0, 0));
		return this.times(cMatrix.inverse());
	}

	/**
	 * Calculates the power of a Matrix raised to an integer, power can be positive or negative
	 * @param power The power at which the matrix is raised. Only integers are allowed
	 * @return The matrix raised to power
	 */
	public MatrixComplex power(int iExp) {
		// Take advantage from diagonal matrices
		if (this.isDiagonal()) {
    		trace("Power() of diagonal matrix");
    		MatrixComplex powerMat = this.copy();
			for (int i = 0; i < this.rows(); ++i)
				powerMat.setItem(i, i, this.getItem(i, i).power(iExp));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(this);
    	if (dmat.isDiagonalizable()) {
			trace("Power() using diagonalization P·D·P⁻¹");
        	trace(dmat.P(), "Matrix P");
        	trace(dmat.D(), "Matrix D");
    		
        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i) 
        		Dmat.setItem(i, i, Dmat.getItem(i, i).power(iExp));
    			trace(Dmat, "Dmat");
            	trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Power() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}
    	
    	// Finally use the Taylor Expansion
		trace("Power() using the Taylor expansion");
    	return power_(iExp);
	}

	/**
	 * Calculates the power to iExp item to item of this matrix
	 * @param iExp The integer exponent 
	 * @return The power to iExp item to item of this matrix
	 */
	public MatrixComplex ppower(int iExp) {
		MatrixComplex powerMat = new MatrixComplex(this.rows(), this.cols());
		
		for (int row = 0; row < powerMat.rows(); ++row)
			for (int col = 0; col < powerMat.cols(); ++col)
				powerMat.setItem(row, col, this.getItem(row, col).power(iExp));
		return powerMat;
	}
	
	/**
	 * Calculates the power of a Matrix raised to an integer, power can be positive or negative
	 * @param power The power at which the matrix is â€‹â€‹raised. Only integers are allowed
	 * @return The matrix raised to power
	 */
	public MatrixComplex power_(int power) {
		boolean inverse = false;
		
		MatrixComplex powerMatrix = new MatrixComplex(this.rows(), this.cols());
		powerMatrix.initMatrixDiag(1, 0);
		if (power == 0) return powerMatrix;

		if (power < 0) {
			inverse = true;
			power = -power;
		}
		
		double cNorma = this.euc_norm();
		MatrixComplex thisNorma = this.divides(cNorma);
		
		for (int i = 1; i <= power; ++i)
			powerMatrix = powerMatrix.times(thisNorma);
		powerMatrix = powerMatrix.times(Math.pow(cNorma,power));

		if (inverse)
			return powerMatrix.inverse();
		else
			return powerMatrix;
	}
	
	/**
	 * Calculates the power of a Matrix raised to a real number
	 * @param dExpo
	 * @return
	 */
	public MatrixComplex power(double dExpo) {
		// Take advantage from diagonal matrices
		if (this.isDiagonal()) {
    		trace("Power() of diagonal matrix");
			MatrixComplex powerMat = this.copy();
			for (int i = 0; i < this.rows(); ++i)
				powerMat.setItem(i, i, this.getItem(i, i).power(dExpo));
			return powerMat;
		}
		
		/* ************************************************************************		
		if(Math.ceil(dExpo) == Math.floor(dExpo)) {
			int iExpo = (int)Math.floor(dExpo);
			return this.power(iExpo);
		}
		************************************************************************ */
		
		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(this);
    	if (dmat.isDiagonalizable()) {
			trace("Power() using diagonalization P·D·P⁻¹");
        	trace(dmat.P(), "Matrix P");
        	trace(dmat.D(), "Matrix D");
    		
        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i) 
        		Dmat.setItem(i, i, Dmat.getItem(i, i).power(dExpo));
			trace(Dmat, "Dmat");
        	trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Power() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}
    	
		trace("Power() using MatrixComplex.exp((this.log()).times(dExpo))");
		return MatrixComplex.exp((this.log()).times(dExpo));
	}

	/**
	 * Calculates the power to dExp item to item of this matrix
	 * @param dExp The real exponent 
	 * @return The power to dExp item to item of this matrix
	 */
	public MatrixComplex ppower(double dExp) {
		MatrixComplex powerMat = new MatrixComplex(this.rows(), this.cols());
		
		for (int row = 0; row < powerMat.rows(); ++row)
			for (int col = 0; col < powerMat.cols(); ++col)
				powerMat.setItem(row, col, this.getItem(row, col).power(dExp));
		return powerMat;
	}
		
	/**
	 * Calculates the power to cExp item to item of this matrix
	 * @param cExp The complex exponent 
	 * @return The power to cExp item to item of this matrix
	 */
	public MatrixComplex ppower(Complex cExp) {
		MatrixComplex powerMat = new MatrixComplex(this.rows(), this.cols());
		
		for (int row = 0; row < powerMat.rows(); ++row)
			for (int col = 0; col < powerMat.cols(); ++col)
				powerMat.setItem(row, col, this.getItem(row, col).power(cExp));
		return powerMat;
	}

	/**
	 * Calculates the power of a Matrix raised to a complex number
	 * @param cExpo
	 * @return
	 */
	public MatrixComplex power(Complex cExpo) {		
		// Take advantage from diagonal matrices
		if (this.isDiagonal()) {
    		trace("Power() of diagonal matrix");
    		MatrixComplex powerMat = this.copy();
			for (int i = 0; i < this.rows(); ++i)
				powerMat.setItem(i, i, this.getItem(i, i).power(cExpo));
			return powerMat;
		}

		// Try using diagonalization
		Diagfactor dmat = new Diagfactor(this);
    	if (dmat.isDiagonalizable()) {
			trace("Power() using diagonalization P·D·P⁻¹");
        	trace(dmat.P(), "Matrix P");
        	trace(dmat.D(), "Matrix D");
    		
        	MatrixComplex Dmat = dmat.D().copy();
        	for (int i = 0; i < Dmat.cols(); ++i) 
        		Dmat.setItem(i, i, Dmat.getItem(i, i).power(cExpo));
			trace(Dmat, "Dmat");
        	trace(dmat.P().times(Dmat).times(dmat.P().inverse()), "Power() Diagonal");
        	return dmat.P().times(Dmat).times(dmat.P().inverse());
    	}

    	// Finally use the taylor Expansion
		trace("Power() using the Taylor expansion");
    	return power_(cExpo);
	}

	/**
	 * Calculates the power of a Matrix raised to a complex number
	 * @param cExpo
	 * @return
	 */
	public MatrixComplex power_(Complex cExpo) {	
		if (cExpo.isPureReal()) {
			double dExpo = cExpo.rep();
			return this.power(dExpo);
		}
		
		return MatrixComplex.exp((this.log()).times(cExpo));
	}
	
	/**
	 * Complex Matrix raised to a Complex Matrix 
	 * @param mcExpo
	 * @return
	 */
	public MatrixComplex power(MatrixComplex mcExpo) {
		if (this.dim() != mcExpo.dim() || !this.isSquare() || !mcExpo.isSquare()) {
			throw new IllegalArgumentException("Not valid matrices: Both matrices has to be square and of the same dimension.");
		}

		return MatrixComplex.exp((this.log()).times(mcExpo));
	}
	
	/*
	 * ***********************************************
	 * TAYLOR'S SERIES - TAYLOR'S EXPANSIONS 
	 * ***********************************************
	 */

	/**
	 * Calculates the exponential of the matrix (e^this)
	 * This calculation is achieved using the Taylor's series of the exponential extended for complex matrices
	 * @return The value of e^this
	 */
	public MatrixComplex exp() {
		return MatrixComplexFunctions.exp(this);
	}
	
	/**
	 * Calculates the exponential of the matrix (e^this)
	 * This calculation is achieved using the Taylor's series of the exponential extended for complex matrices
	 * @return The value of e^this
	 */
	public MatrixComplex exp_() {
		return MatrixComplexFunctions.exp_(this);
	}

	/**
	 * Calculates the exp of the matrix exp(matrix)
	 * @param matrix
	 * @return The exp of matrix
	 */
	public static MatrixComplex exp(MatrixComplex matrix) {
		return matrix.exp();
	}

	/**
	 * The "SIN" or "COS" depending on the sign. One method to rule them all
	 * @param sign 1 for "SIN"; -1 for "COS"
	 * @return The "SIN" or "COS" depending on the sign	 
	 */
	/**
	 * Calculates the sin of the matrix
	 * This calculation is achieved using the Taylor's series of the sin extended for complex matrices
	 * @return The value of sinTaylor()
	 */
	public MatrixComplex sinTaylor() {
		return MatrixComplexFunctions.sinTaylor(this);
	}

	/**
	 * Calculates the sin of the matrix sinEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of sinEuler()
	 */
	public MatrixComplex sinEuler() {
		return MatrixComplexFunctions.sinEuler(this);
	}

	/**
	 * Calculates the sin of the matrix sin()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of sin()
	 */
	public MatrixComplex sin() {
		return MatrixComplexFunctions.sin(this);
	}

	/**
	 * Calculates the sin of the matrix sin(matrix)
	 * @param matrix
	 * @return The sin of matrix
	 */
	public static MatrixComplex sin(MatrixComplex matrix) {
		return matrix.sin();
	}
	
	/**
	 * Calculates the sin item to item of this matrix  
	 * @return The sin item to item of this matrix
	 */
	public MatrixComplex ssin() {
		return MatrixComplexFunctions.ssin(this);
	}
			
	/**
	 * Calculates the sin item to item of the matrix ssin(matrix)
	 * @param matrix
	 * @return The sin item to item of matrix
	 */
	public static MatrixComplex ssin(MatrixComplex matrix) {
		MatrixComplex sinMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < sinMat.rows(); ++row)
			for (int col = 0; col < sinMat.cols(); ++col)
				sinMat.setItem(row, col, Complex.sin(matrix.getItem(row, col)));
		return sinMat;
	}
	
	/**
	 * Calculates the cos of the matrix cosTaylor()
	 * This calculation is achieved using the Taylor's series of the cos extended for complex matrices
	 * @return The value of cosTaylor()
	 */
	public MatrixComplex cosTaylor() {
		return MatrixComplexFunctions.cosTaylor(this);
	}

	/**
	 * Calculates the sin of the matrix cosEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of cosEuler()
	 */
	public MatrixComplex cosEuler() {
		return MatrixComplexFunctions.cosEuler(this);
	}

	/**
	 * Calculates the sin of the matrix cos()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of cos()
	 */
	public MatrixComplex cos() {
		return MatrixComplexFunctions.cos(this);
	}

	/**
	 * Calculates the cos of the matrix cos(matrix)
	 * @param matrix
	 * @return The cos of matrix
	 */
	public static MatrixComplex cos(MatrixComplex matrix) {
		return matrix.cos();
	}

	
	/**
	 * Calculates the cos item to item of this matrix  
	 * @return The cos item to item of this matrix
	 */
	public MatrixComplex ccos() {
		return MatrixComplexFunctions.ccos(this);
	}
			
	/**
	 * Calculates the cos item to item of the matrix ccos(matrix)
	 * @param matrix
	 * @return The cos item to item of matrix
	 */
	public static MatrixComplex ccos(MatrixComplex matrix) {
		MatrixComplex cosMat = new MatrixComplex(matrix.rows(), matrix.cols());

		for (int row = 0; row < cosMat.rows(); ++row)
			for (int col = 0; col < cosMat.cols(); ++col)
				cosMat.setItem(row, col, Complex.cos(matrix.getItem(row, col)));
		return cosMat;
	}

	
	/**
	 * Calculates the tan of the matrix tanTaylor()
	 * The tangent is calculated as sinTaylor()/cosTaylor()
	 * @return The value of tanTaylor()
	 */
	public MatrixComplex tanTaylor() {
		return MatrixComplexFunctions.tanTaylor(this);
	}

	/**
	 * Calculates the tan of the matrix tanEuler()
	 * The tangent is calculated as sinEuler()/cosEuler()
	 * @return The value of tanEuler()
	 */
	public MatrixComplex tanEuler() {
		return MatrixComplexFunctions.tanEuler(this);
	}

	/**
	 * Calculates the sin of the matrix tan()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of tan()
	 */
	public MatrixComplex tan() {
		return MatrixComplexFunctions.tan(this);
	}

	/**
	 * Calculates the tan of the matrix tan(matrix)
	 * @param matrix
	 * @return The tan of matrix
	 */
	public static MatrixComplex tan(MatrixComplex matrix) {
		return matrix.tan();
	}
	
	/**
	 * Calculates the tan item to item of this matrix  
	 * @return The tan item to item of this matrix
	 */
	public MatrixComplex ttan() {
		return MatrixComplexFunctions.ttan(this);
	}
			
	/**
	 * Calculates the tan item to item of the matrix ttan(matrix)
	 * @param matrix
	 * @return The tan item to item of matrix
	 */
	public static MatrixComplex ttan(MatrixComplex matrix) {
		MatrixComplex tanMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < tanMat.rows(); ++row)
			for (int col = 0; col < tanMat.cols(); ++col)
				tanMat.setItem(row, col, Complex.tan(matrix.getItem(row, col)));
		return tanMat;
	}	
	
	/**
	 * Euler's formula e^[+/-]x=cos(x)[+/-]i·sin(x)
	 * @return Euler's formula e^x
	 */
	public MatrixComplex euler() {
		return MatrixComplexFunctions.euler(this);
	}

	/**
	 * Euler's formula e^[+/-]x=cos(x)[+/-]i·sin(x)
	 * @param matrix
	 * @return Euler's formula e^x
	 */
	public static MatrixComplex euler(MatrixComplex matrix) {
		// normalize2PI() removed, see sinEuler().
		return exp(matrix.times(Complex.i));
	}

	/**
	 * Calculates the hyperbolic sin of the matrix sinhTaylor(this)
	 * This calculation is achieved using the Taylor's series of the hyperbolic sin extended for complex matrices
	 * @return The value of sinh()
	 */
	public MatrixComplex sinhTaylor() {
		return MatrixComplexFunctions.sinhTaylor(this);
	}

	/**
	 * Calculates the hyperbolic sin of the matrix sinhEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of sinhEuler()
	 */
	public MatrixComplex sinhEuler() {
		return MatrixComplexFunctions.sinhEuler(this);
	}

	/**
	 * Calculates the sin of the matrix sinh()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of sinh()
	 */
	public MatrixComplex sinh() {
		return MatrixComplexFunctions.sinh(this);
	}

	/**
	 * Calculates the sinh of the matrix sinh(matrix)
	 * @param matrix
	 * @return The sinh of matrix
	 */
	public static MatrixComplex sinh(MatrixComplex matrix) {
		return matrix.sinh();
	}
	
	/**
	 * Calculates the sinh item to item of this matrix  
	 * @return The sinh item to item of this matrix
	 */
	public MatrixComplex ssinh() {
		return MatrixComplexFunctions.ssinh(this);
	}
			
	/**
	 * Calculates the sinh item to item of the matrix ssinh(matrix)
	 * @param matrix
	 * @return The sinh item to item of matrix
	 */
	public static MatrixComplex ssinh(MatrixComplex matrix) {
		MatrixComplex sinhMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < sinhMat.rows(); ++row)
			for (int col = 0; col < sinhMat.cols(); ++col)
				sinhMat.setItem(row, col, Complex.sinh(matrix.getItem(row, col)));
		return sinhMat;
	}
	
	/**
	 * Calculates the hyperbolic cos of the matrix coshTaylor()
	 * This calculation is achieved using the Taylor's series of the hyp cos extended for complex matrices
	 * @return The value of coshTaylor()
	 */
	public MatrixComplex coshTaylor() {
		return MatrixComplexFunctions.coshTaylor(this);
	}

	/**
	 * Calculates the hyperbolic sin of the matrix coshEuler()
	 * This calculation is achieved using the Euler's formula extended complex matrices
	 * @return The value of coshEuler()
	 */
	public MatrixComplex coshEuler() {
		return MatrixComplexFunctions.coshEuler(this);
	}

	/**
	 * Calculates the sin of the matrix cosh()
	 * This is a shortcut to the preferred method for doing the calculation
	 * @return The value of cosh()
	 */
	public MatrixComplex cosh() {
		return MatrixComplexFunctions.cosh(this);
	}

	/**
	 * Calculates the cosh of the matrix cosh(matrix)
	 * @param matrix
	 * @return The cosh of matrix
	 */
	public static MatrixComplex cosh(MatrixComplex matrix) {
		return matrix.cosh();
	}

	/**
	 * Calculates the cosh item to item of this matrix  
	 * @return The cosh item to item of this matrix
	 */
	public MatrixComplex ccosh() {
		return MatrixComplexFunctions.ccosh(this);
	}
			
	/**
	 * Calculates the cosh item to item of the matrix ccosh(matrix)
	 * @param matrix
	 * @return The cosh item to item of matrix
	 */
	public static MatrixComplex ccosh(MatrixComplex matrix) {
		MatrixComplex coshMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < coshMat.rows(); ++row)
			for (int col = 0; col < coshMat.cols(); ++col)
				coshMat.setItem(row, col, Complex.cosh(matrix.getItem(row, col)));
		return coshMat;
	}
	
	/**
	 * Calculates the hyperbolic tan of the matrix tanhTaylor()
	 * This calculation uses the Taylor's series of the sin and cos extended for complex matrices
	 * The hyperbolic tangent is calculated as sinhTaylor()/coshTaylor()
	 * @return The value of tanhTaylor()
	 */
	public MatrixComplex tanhTaylor() {
		return MatrixComplexFunctions.tanhTaylor(this);
	}

	/**
	 * Calculates the hyperbolic tan of the matrix tanhEuler()
	 * This calculation uses the Euler's formulas of the sin and cos extended for complex matrices
	 * The hyperbolic tangent is calculated as sinhEuler()/coshEuler()
	 * @return The value of tanhEuler()
	 */
	public MatrixComplex tanhEuler() {
		return MatrixComplexFunctions.tanhEuler(this);
	}

	/**
	 * Calculates the tan of the matrix tanh()
	 * The tangent is calculated as tanh preferred method
	 * @return The value of tanh()
	 */
	public MatrixComplex tanh() {
		return MatrixComplexFunctions.tanh(this);
	}

	/**
	 * Calculates the tanh of the matrix tanh(matrix)
	 * @param matrix
	 * @return The tanh of matrix
	 */
	public static MatrixComplex tanh(MatrixComplex matrix) {
		return matrix.tanh();
	}

	/**
	 * Calculates the tanh item to item of this matrix  
	 * @return The tanh item to item of this matrix
	 */
	public MatrixComplex ttanh() {
		return MatrixComplexFunctions.ttanh(this);
	}
			
	/**
	 * Calculates the tanh item to item of the matrix ttanh(matrix)
	 * @param matrix
	 * @return The tanh item to item of matrix
	 */
	public static MatrixComplex ttanh(MatrixComplex matrix) {
		MatrixComplex tanhMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < tanhMat.rows(); ++row)
			for (int col = 0; col < tanhMat.cols(); ++col)
				tanhMat.setItem(row, col, Complex.tanh(matrix.getItem(row, col)));
		return tanhMat;
	}
	
	/**
	 * Calculates the logarithm of a Matrix using Taylor's Extension summation log(1 - x)
	 * @return The logarithm of a Matrix using Taylor's Extension
	 */
	public MatrixComplex logTaylor() {
		return MatrixComplexFunctions.logTaylor(this);
	}

	/**
	 * Calculates the logarithm of a Matrix using Mercator's Extension summation log(1 + x)
	 * @return The logarithm of a Matrix using Mercator's Extension
	 */
	public MatrixComplex logMercator() {
		return MatrixComplexFunctions.logMercator(this);
	}

	/**
	 * Calculates the logarithm of a Matrix using Hyperbolic Arc Tangent's Extension summation.
	 * Not recommended to use
	 * @return The logarithm of a Matrix using Hyperbolic Arc Tangent's Extension
	 */
	public MatrixComplex logHat() {
		return MatrixComplexFunctions.logHat(this);
	}
	
	/**
	 * Calculates the principal natural logarithm of a (possibly defective, non-diagonalizable)
	 * matrix via Schur factorization plus inverse scaling-and-squaring (MATLAB {@code logm},
	 * Higham).
	 * @return The principal natural logarithm of this matrix.
	 */
	public MatrixComplex logm() {
		return MatrixComplexFunctions.logm(this);
	}

	/**
	 * Shortcut to the preferred natural logarithm expansion.
	 * @return the natural logarithm of the matrix
	 */
	public MatrixComplex log() {
		return MatrixComplexFunctions.log(this);
	}

	/**
	 * Static method to get the natural log of the matrix log(matrix)
	 * @param matrix
	 * @return The log of matrix
	 */
	public static MatrixComplex log(MatrixComplex matrix) {
		return matrix.log();
	}
	
	/**
	 * Calculates the log item to item of this matrix  
	 * @return The log item to item of this matrix
	 */
	public MatrixComplex llog() {
		return MatrixComplexFunctions.llog(this);
	}
			
	/**
	 * Calculates the log item to item of the matrix llog(matrix)
	 * @param matrix
	 * @return The log item to item of matrix
	 */
	public static MatrixComplex llog(MatrixComplex matrix) {
		MatrixComplex logMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < logMat.rows(); ++row)
			for (int col = 0; col < logMat.cols(); ++col)
				logMat.setItem(row, col, Complex.log(matrix.getItem(row, col)));
		return logMat;
	}
	
	/**
	 * Calculates the natural log in base 10 of the matrix log10(matrix)
	 * @param matrix
	 * @return
	 */
	public MatrixComplex log10() {
		return MatrixComplexFunctions.log10(this);
	}

	/**
	 * Static method to get the natural log in base 10 of the matrix log10(matrix)
	 * @param matrix
	 * @return
	 */
	public static MatrixComplex log10(MatrixComplex matrix) {
		return log(matrix).divides(__log10__);
	}

	/**
	 * Calculates the log10 item to item of this matrix  
	 * @return The log  item to item of this matrix
	 */
	public MatrixComplex llog10() {
		return MatrixComplexFunctions.llog10(this);
	}
			
	/**
	 * Calculates the log10 item to item of the matrix llog10(matrix)
	 * @param matrix
	 * @return The log10 item to item of matrix
	 */
	public static MatrixComplex llog10(MatrixComplex matrix) {
		MatrixComplex logMat = new MatrixComplex(matrix.rows(), matrix.cols());
		
		for (int row = 0; row < logMat.rows(); ++row)
			for (int col = 0; col < logMat.cols(); ++col)
				logMat.setItem(row, col, Complex.log(matrix.getItem(row, col)).divides(__log10__));
		return logMat;
	}
	
	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public MatrixComplex logbase(Complex base) {
		return MatrixComplexFunctions.logbase(this, base);
	}

	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public MatrixComplex llogbase(Complex base) {
		return MatrixComplexFunctions.llogbase(this, base);
	}
	
	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public static MatrixComplex logbase(MatrixComplex matrix, Complex base) {
		return log(matrix).divides(Complex.log(base));
	}

	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public static MatrixComplex llogbase(MatrixComplex matrix, Complex base) {
		return matrix.llogbase(base);
	}
	
	/**
	 * Calculates the natural log in Real base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public MatrixComplex logbase(double base) {
		return MatrixComplexFunctions.logbase(this, base);
	}

	/**
	 * Calculates the natural log in Real base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public MatrixComplex llogbase(double base) {
		return MatrixComplexFunctions.llogbase(this, base);
	}
	
	/**
	 * Calculates the natural log in Real base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public static MatrixComplex logbase(MatrixComplex matrix, double base) {
		return log(matrix).divides(Math.log(base));
	}

	/**
	 * Calculates the natural log in Complex base "base" of the matrix logbase(matrix)
	 * @param matrix
	 * @param base
	 * @return
	 */
	public static MatrixComplex llogbase(MatrixComplex matrix, double base) {
		return matrix.llogbase(base);
	}

	/**
	 * Calculates the log in complex matrix base "baseMat" of the matrix mat
	 * @param mat
	 * @param baseMat
	 * @return
	 */
	public MatrixComplex logbase(MatrixComplex baseMat) {
		return MatrixComplexFunctions.logbase(this, baseMat);
	}

	/**
	 * Calculates the log in complex matrix base "baseMat" of the matrix mat
	 * @param mat
	 * @param baseMat
	 * @return
	 */
	public static MatrixComplex logbase(MatrixComplex mat, MatrixComplex baseMat) {
		return mat.log().divides(baseMat.log());
	}

	
	
	
	/**
	 * Calculates the power of a complex number raised a complex matrix
	 * @param cBase
	 * @param matrix
	 * @return
	 */
	public static MatrixComplex power(Complex cBase, MatrixComplex exponent) {
		return exp(exponent.times(Complex.log(cBase)));
	}
		
	/**
	 * Calculates the power of a real number raised a complex matrix
	 * @param base
	 * @param matrix
	 * @return
	 */
	public static MatrixComplex power(double base, MatrixComplex exponent) {
		Complex cBase = new Complex(base);
		return power(cBase, exponent);
	}

	/**
	 * Complex Matrix raised to a Complex Matrix 
	 * @param mcBase
	 * @param mcExpo
	 * @return
	 */
	public static MatrixComplex power(MatrixComplex mcBase, MatrixComplex mcExpo) {
		return mcBase.power(mcExpo);
	}
	
	/*
	 * ***********************************************
	 * ORDER RELATIONSHIPS IN MATRICES
	 * ***********************************************
	 */
	
	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean sameDimension(MatrixComplex matrix) {
		if (this.rows() != matrix.rows() || this.cols() != matrix.cols()) return false;
		return true;
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isGT(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}
		//return this.totalize().cre() > matrix.totalize().cre();
		return this.norm() > matrix.norm();
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isGTE(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}	
		//return this.totalize().cre() >= matrix.totalize().cre();
		return this.norm() >= matrix.norm();

	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isLT(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}
		// return this.totalize().cre() < matrix.totalize().cre();
		return this.norm() < matrix.norm();
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean isLTE(MatrixComplex matrix) {
		if (!this.sameDimension(matrix)) {
			System.out.println("Both matrices must have the same dimension.");
			return false;
		}
		// return this.totalize().cre() <= matrix.totalize().cre();
		return this.norm() <= matrix.norm();
	}

	/*
	 * ***********************************************
	 * NORMS
	 * ***********************************************
	 */
	
	/**
	 * Private method that returns the maximum value of a set of real values.
	 * @param valMatrix The set of real values.
	 * @return The maximum real value.
	 */
 	private double max(double[] valMatrix) {
		double temp;
		temp = valMatrix[0];

		for (int i = 1; i < valMatrix.length;  ++i)
			temp = Math.max(temp, valMatrix[i]);
		return temp;
	}
	
 	/**
	 * P Norm or Hölder Norm. Calculates Hölder's norm of order "p".
	 * @param p The order of the norm.
	 * @return The value of the norm.
	 */
	public double p_norm(int p) {
		int rowLen = this.rows();
		int colLen = this.cols();

		if ( p <= 0 ) {
			throw new IllegalArgumentException("Not valid order. The order of the norm must be greater than zero.");
		}

		double[] norm = new double[rowLen];
		for (int row = 0; row  < rowLen; ++row) {
			norm[row] = 0.0;
			for (int col = 0; col < colLen; ++col)
				norm[row] += Math.pow(this.complexMatrix[row][col].mod(), p);
		}	
		return Math.pow(max(norm), 1.0/p);
	}

	/**
	 * Shortcut to euc_norm method.
	 * Euclidean Norm. Calculates the Euclidean norm.
	 * In linear algebra, functional analysis, and related areas of mathematics, a norm is a function that assigns a strictly positive length or size to each vector in a vector spaceâ€”save for the zero vector, which is assigned a length of zero.
	 * @return The value of the norm.
	 */
	public double norm() {
		return this.euc_norm();
	}

	/**
	 * Calculates the norm of infinite order.
	 * @return The value of the norm.
	 */
	public double inf_norm() {
		int rowLen = this.rows();
		int colLen = this.cols();

		double[] norm = new double[rowLen+1];
		for (int row = 0; row < rowLen; ++row) {
			norm[row] = 0.0;
			for (int col = 0; col  < colLen; ++col)
				norm[row] += this.complexMatrix[row][col].mod();
		}	
		return max(norm);
	}

	/**
	 * Euclidean Norm. Calculates the Euclidean norm or norm of order 2.
	 * @return The value of the norm.
	 */
	public double euc_norm() {
		int rowLen = this.rows();
		int colLen = this.cols();

		double norm = 0;
		for (int row = 0; row < rowLen; ++row) {
			for (int col = 0; col  < colLen; ++col)
				norm += Math.pow(this.complexMatrix[row][col].mod(), 2.0);
		}	
		return Math.pow(norm, 0.5);
	}

	/**
	 * Frobenius norm. Calculates the Euclidean norm or norm of order 2.
	 * @return The actual value of the norm.
	 */
	public double f_norm() {
		return this.euc_norm();    	
	}

	/*
	 * ***********************************************
	 * UNARY OPERATORS
	 * ***********************************************
	 */
	
	/**
	 * Checks if a matrix is empty, A matrix is empty if rows = cols = 0
	 * @return True is matrix is empty
	 */
	public boolean isEmpty() {
		if (this.rows() == 0 && this.cols() == 0) return true;
		return false;
	}
	
	/**
	 * Makes the matrix to become positive semidefinite
	 */
	public void abs() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				this.setItem(row, col, this.getItem(row, col).abs());
	}	

	/**
	 * Checks whether the matrix is singular or not (determinant = 0)
	 * @return True if the matrix is singular, false otherwise
	 */
	public boolean isSingular() {
		return determinant().equals(Complex.ZERO);
	}

	/**
	 * Normal matrices: A*A.adjoint() = A.adjoint()*A
	 * @return True if the matrix is normal, false otherwise
	 */
	public boolean isNormal() {
		if (!isSquare()) return false;
		return this.times(this.adjoint()).equals(this.adjoint().times(this));
	}

	/**
	 * Normal matrices: A square and A*A.adjoint() = A.adjoint()*A = I
	 * @return True if the matrix is normal, false otherwise
	 */
	public boolean isUnitary() {
		if (!isSquare()) return false;
		return this.adjoint().times(this).equals(eye(this.rows()));
	}

	/**
	 * Checks whether a Matrix is diagonal or not
	 * @return True if the matrix is diagonal, false otherwise
	 */
	public boolean isDiagonal() {
		if (!isSquare()) return false;
		for (int row = 0; row < rows(); ++row)
			for (int col = 0; col < cols(); ++col) {
				if (row != col) {
					if (!getItem(row, col).equals(Complex.ZERO)) return false;
				}
			}
		return true;
	}

	/**
	 * Checks whether a Matrix is orthogonal or not
	 * @return True if the matrix is orthogonal, false otherwise
	 */
	public boolean isOrthogonal() {
		if (!isSquare()) return false;
		if (this.determinant().isZero()) return false;
		//return isDiagonal() && this.adjoint().times(this).equals(eye(this.rows()));
		//return this.times(this.adjoint()).determinant().abs() - 1 <= Complex.zero_threshold_approx();
		/*
		 * I've observed that this happens if the basis is orthogonal.
		 * this.times(this.adjoint()).isDiagonal();
		 * This will resolve the duality between orthogonal matrices and orthogonal bases of a vector space, allowing a single orthogonality method to be defined for matrices.
		 * this.adjoint().times(this), on the other hand, does not satisfy this property.
		 */
		//return this.times(this.adjoint()).isDiagonal();
		//Una matriz ortogonal es una matriz cuadrada cuya matriz inversa coincide con su matriz traspuesta conjugada.
		return this.adjoint().equals(this.inverse());
		// return this.isUnitary();
	}

	/**
	 * Checks whether a Matrix is orthonormal or not. Othonormal and Orthogonal are the same concept in Matrices. BAD!!!!
	 * @return True if the matrix is orthonormal, false otherwise
	 */
	public boolean isOrthonormal() {
		return isOrthogonal();
		// if (!isSquare()) return false;
		// if (this.determinant().equals(Complex.ZERO)) return false;
		////return this.inverse().equals(this.adjoint());
		//return this.times(this.adjoint()).equals(eye(this.rows()));
	}
	
	/**
	 * Upper Hessenberg matrices: a(i,j) = 0 for any pair i, j such that i > j + 1.
	 * @return True if the matrix is upper Hessenberg, false otherwise
	 */
	public boolean isHessenbergUpper() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col <= row; ++col)
				if (!getItem(row,col).equals(Complex.ZERO)) return false;
		return true;
	}

	/**
	 * Upper Hessenberg matrices: a(i,j) = 0 for any pair i, j such that i <= j.
	 * @return True if the matrix is lower Hessenberg, false otherwise
	 */
	public boolean isHessenbergLower() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = row; col < this.cols(); ++col)
				if (!getItem(row,col).equals(Complex.ZERO)) return false;					
		return true;
	}
	
	/**
	 * Checks if at least one of the values of the array is infinite
	 * @return True if one infinite value is found
	 */
 	public boolean isInfinite() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				if (this.getItem(row, col).isInfinite()) return true;
		return false;
	}
	
	/**
	 * Checks if at least one of the values of the array is NaN
	 * @return True if one NaN value is found
	 */
	public boolean isNaN() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				if (this.getItem(row, col).isNaN()) return true;
		return false;
	}

	/**
	 * Checks if the matrix is null compared with Complex.ZERO.
	 * @return true if the matrix is null, otherwise false.
	 */
	public boolean isNullC() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				if (!this.getItem(row, col).equals(Complex.ZERO)) return false;
		return true;
	}

	/**
	 * Checks if the matrix is null compared with Complex.ZERO.
	 * @return true if the matrix is null, otherwise false.
	 */
	public boolean isNull() {
		for (int row = 0; row < this.rows(); ++row)
			for (int col = 0; col < this.cols(); ++col)
				if (!this.getItem(row, col).equals(Complex.ZERO)) return false;					
		return true;
	}

	/**
	 * The dimension of the matrix as a product of the number of rows by the number of columns.
	 * @return The matrix dimension.
	 */
	public int dim() {
		int rowLen = this.rows();
		int colLen = this.cols();
		return rowLen * colLen;
	}

	/**
	 * Returns the condition number of the array using the p norm, where p is the order of the norm.  
	 * @return The condition number
	 */
	public double cond_p(int p) {
		return this.p_norm(p) * this.inverse().p_norm(p);
	}
	
	/**
	 * Returns the condition number of the array using the euclidean norm 
	 * @return The condition number
	 */
	public double cond_f() {
		return this.f_norm() * this.inverse().f_norm();
	}
	
	/**
	 * Returns the condition number of the array using the infinite norm 
	 * @return The condition number
	 */
	public double cond_inf() {
		return this.inf_norm() * this.inverse().inf_norm();
	}

	/**
	 * Returns the condition number of the array using the infinite norm.
	 * Short cut to cond_imf() 
	 * @return The condition number
	 */
	public double cond() {
		return cond_inf();
	}
	
	/**
	 * Trace of an n-by-n square matrix A - the sum of the elements on the main diagonal. 
	 * @return The value of the trace.
	 */
	public Complex trace() {
		if (!this.isSquare()) {
			throw new IllegalArgumentException("Not valid trace: The matrix has to be square.");
		}
		int rowLen = this.rows();
		Complex trace = new Complex();

		for (int i = 0; i < rowLen; ++i)
			trace = trace.plus(this.complexMatrix[i][i]);
		return trace;
	}

	/**
	 * Cotrace of an n-by-n square matrix A - the sum of the elements on the secondary diagonal. 
	 * @return The value of the trace.
	 */
	public Complex cotrace() {
		if (!this.isSquare()) {
			throw new IllegalArgumentException("Not valid cotrace: The matrix has to be square.");
		}
		int rowLen = this.rows();
		Complex cotrace = new Complex();
		
		int col = rowLen - 1;
		for (int i = 0; i < rowLen; ++i)
			cotrace = cotrace.plus(this.complexMatrix[i][col--]);
		return cotrace;
	}
	
	/**
	 * Calculates the opposite of the matrix.
	 * @return The matrix opposite.
	 */
	public MatrixComplex opposite() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex cMatrix = new MatrixComplex(colLen, rowLen);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].opposite();  		
		return cMatrix;
	}

	/**
	 * Transpose of the matrix by reflecting it over its main diagonal.
	 * @return The matrix transposed.
	 */
	public MatrixComplex transpose() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex cMatrix = new MatrixComplex(colLen, rowLen);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[col][row] = this.complexMatrix[row][col];
		return cMatrix;
	}

	/**
	 * Calculates the conjugate of the matrix.
	 * Matrix complex conjugate is a new matrix with equal real part and imaginary part equal in magnitude but opposite in sign.
	 * @return The matrix conjugated.
	 */
	public MatrixComplex conjugate() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex cMatrix = new MatrixComplex(rowLen, colLen);

		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[row][col] = this.complexMatrix[row][col].conjugate();		
		return cMatrix;
	}

	/**
	 * Calculates the adjoint of the matrix.
	 * The adjoint is the transposed conjugated matrix.
	 * @return The new matrix adjoint.
	 */
	public MatrixComplex adjoint() {
		int rowLen = this.rows();
		int colLen = this.cols();

		MatrixComplex cMatrix = new MatrixComplex(colLen, rowLen);

		//cMatrix = this.transpose().conjugate();
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < colLen; ++col)
				cMatrix.complexMatrix[col][row] = this.complexMatrix[row][col].conjugate();
		return cMatrix;
	}

	/**
	 * Minor for row "rowPivot" and column "colPivot".
	 * The minor the Matrix resultant of removing the row "rowPivot" and column "colPivot".
	 * @param rowPivot The index of the row to eliminate.
	 * @param colPivot The index of the column to eliminate.
	 * @return The minors' matrix.
	 */
	public MatrixComplex minor(int rowPivot, int colPivot) {
		int rowLen = this.rows();
		int colLen = this.cols();

		if (rowPivot < 0 || rowPivot > rowLen) {
			throw new IllegalArgumentException("Not valid minor: The row to pivot is incorrect.");
		}

		if (colPivot < 0 || colPivot > colLen) {
			throw new IllegalArgumentException("Not valid minor: The col to pivot is incorrect.");
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLen-1, colLen-1);

		for (int row = 0, rowf = 0; row < rowLen; ++row) {
			if (row == rowPivot) 
				continue;
			for (int col = 0, colf = 0; col < colLen; ++col) {
				if (col == colPivot) 
					continue;
				resultMatrix.complexMatrix[rowf][colf++] = this.complexMatrix[row][col];
			}
			++rowf;
		}
		return resultMatrix;
	}

	/**
	 * Matrix of Cofactors order 1 for row "rowPivot" and column "colPivot".
	 * The co-factor of an element of the matrix is equal to the product of the minor of the element and -1 to the power of the positional value of the element.
	 * @param rowPivot The index of the row minor.
	 * @param colPivot The index of the column minor.
	 * @return The cofactors' matrix.
	 */
	public MatrixComplex cofactors(int rowPivot, int colPivot) {
		int rowLen = this.rows();
		int colLen = this.cols();

		if (rowPivot < 0 || rowPivot > rowLen) {
			throw new IllegalArgumentException("Not valid cofactor: The row to pivot is incorrect.");
		}

		if (colPivot < 0 || colPivot > colLen) {
			throw new IllegalArgumentException("Not valid cofactor: The col to pivot is incorrect.");
		}

		MatrixComplex resultMatrix = new MatrixComplex(rowLen-1, colLen-1);

		for (int row = 0, rowf = 0; row < rowLen; ++row) {
			if (row == rowPivot) 
				continue;
			for (int col = 0, colf = 0; col < colLen; ++col) {
				if (col == colPivot) 
					continue;
				resultMatrix.complexMatrix[rowf][colf++] = this.complexMatrix[row][col].times(Math.pow(-1, row+col));
			}
			++rowf;
		}
		return resultMatrix;
	}

	/**
	 * Calculates the adjugate of an square matrix.
	 * Adjugate or classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate() {
		return this.cofactor().transpose();
	}

	/**
	 * Calculates the adjugate of an square matrix.
	 * Adjunct or adjugate, or classical adjoint, of a square matrix is the transpose of its cofactor matrix.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct() {
		return this.adjugate();
	}

	/**
	 * Calculates the adjugate for a row and a column.
	 * Adjugate or classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param rowPivot The index of the row to remove.
	 * @param colPivot The index of the column to remove.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate(int rowPivot, int colPivot) {
		return this.cofactors(rowPivot, colPivot).transpose();
	}

	/**
	 * Calculates the adjunct for a row and a column.
	 * Adjunct or adjugate, or classical adjoint, of a square matrix is the transpose of its cofactor matrix.
	 * @param rowPivot The index of the row to remove.
	 * @param colPivot The index of the column to remove.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct(int rowPivot, int colPivot) {
		return this.adjugate(rowPivot, colPivot);
	}

	/**
	 * Calculates the adjugate matrix of the rows passed in the parameter "includedRows".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRows A list with the indexes of the the rows included in the cofactors array.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate(int[] includedRows) {
		return this.cofactors(includedRows).transpose();
	}

	/**
	 * Calculates the adjunct matrix of the rows passed in the parameter "includedRows".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRows A list with the indexes of the the rows included in the cofactors array.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct(int[] includedRows) {
		return this.adjunct(includedRows);
	}

	/**
	 * Calculates the adjugate matrix of the rows passed in the parameter "includedRowsList".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRowsList A list with the rows included in the cofactors array as a comma separated string.
	 * @return The adjugate matrix.
	 */
	public MatrixComplex adjugate(String includedRowsList) {
		return this.cofactors(includedRowsList).transpose();
	}

	/**
	 * Calculates the adjunct matrix of the rows passed in the parameter "includedRowsList".
	 * The adjugate, classical adjoint, or adjunct of a square matrix is the transpose of its cofactor matrix.
	 * @param includedRowsList A list with the rows included in the cofactors array as a comma separated string.
	 * @return The adjunct matrix.
	 */
	public MatrixComplex adjunct(String includedRowsList) {
		return this.adjugate(includedRowsList);
	}

	/**
	 * The inverse of the matrix calculated by Gauss-Jordan elimination method
	 * Gauss-Jordan elimination method can be used for finding the inverse of a matrix, if it exists. 
	 * If A is a n by n square matrix, then row reduction can be used to compute its inverse matrix, if it exists. 
	 * First, the n by n identity matrix is augmented to the right of A, forming a n by 2n block matrix [A | I]. 
	 * Now through application of elementary row operations, finds the reduced echelon form of this n by 2n matrix. 
	 * The matrix A is invertible if and only if the left block can be reduced to the identity matrix I; in this case 
	 * the right block of the final matrix is A⁻¹. If the algorithm is unable to reduce the left block to I, 
	 * then A is not invertible.
	 * @return The inverse matrix.
	 */
	public MatrixComplex inverse() {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cCoef = new Complex();
		int row, col;

		if (rowLen != colLen) {
			System.err.println(HEADINFO + "inverse: Not valid matrix: The matrix has to be square.");
			return this.divides(0);
		}

		if (this.determinant().equals(0,0) ) {
			//System.err.println(HEADINFO + "inverse: Not valid matrix: The matrix determinat is ZERO.");
			return this.divides(0);
		}

		MatrixComplex auxMatrix = this.copy();
		MatrixComplex unitMatrix = new MatrixComplex(rowLen); unitMatrix.initMatrixDiag(1,0);

		for (int k = 0; k < rowLen-1; ++k) {
			// Proactive partial pivoting: always swap to the row with the maximum modulus in this
			// column, not only when the current pivot is exactly zero -- a pivot that is merely
			// small (but nonzero within Complex.equals() tolerance) still amplifies rounding error.
			int rowSwap = auxMatrix.partialPivot(k);
			if (rowSwap == -1)
				return auxMatrix.divides(0);
			if (rowSwap != k) {
				auxMatrix.swapRows(k, rowSwap);
				unitMatrix.swapRows(k, rowSwap);
			}
			for (row = k+1; row < rowLen; ++row) {
				cCoef = auxMatrix.complexMatrix[row][k].divides(auxMatrix.complexMatrix[k][k]);
				for (col = 0; col < colLen; ++col) {
					unitMatrix.complexMatrix[row][col] = unitMatrix.complexMatrix[row][col].minus(unitMatrix.complexMatrix[k][col].times(cCoef)); 
					auxMatrix.complexMatrix[row][col] = auxMatrix.complexMatrix[row][col].minus(auxMatrix.complexMatrix[k][col].times(cCoef));
				}
			}
		}

		for (int k = rowLen-1; k >= 0 ; --k) {
			if (auxMatrix.complexMatrix[k][k].equals(0,0)) {
				int rowSwap = auxMatrix.partialPivot(k);
				//int rowSwap = auxMatrix.locateSwapRowDown(k);
				if (rowSwap == -1) 
					return auxMatrix.divides(0);
				if (rowSwap != k) {
					auxMatrix.swapRows(k, rowSwap);
					unitMatrix.swapRows(k, rowSwap);
				}
			}
			for (row = k-1; row >= 0; --row) {
				cCoef = auxMatrix.complexMatrix[row][k].divides(auxMatrix.complexMatrix[k][k]);
				for (col = 0; col < colLen; ++col) {
					unitMatrix.complexMatrix[row][col] = unitMatrix.complexMatrix[row][col].minus(unitMatrix.complexMatrix[k][col].times(cCoef)); 
					auxMatrix.complexMatrix[row][col] = auxMatrix.complexMatrix[row][col].minus(auxMatrix.complexMatrix[k][col].times(cCoef));
				}
			}
		}

		for (row = 0; row < rowLen; ++row) {
			cCoef = auxMatrix.complexMatrix[row][row].reciprocal();
			for (col = 0; col < colLen; ++col) {
				auxMatrix.complexMatrix[row][col] = auxMatrix.complexMatrix[row][col].times(cCoef);
				unitMatrix.complexMatrix[row][col] = unitMatrix.complexMatrix[row][col].times(cCoef); 
			}
		}
		return unitMatrix;
	}

	/**
	 * Returns the upper triangularization of the matrix.
	 * @return The  upper triangular matrix.
	 */
	public MatrixComplex triangle(){
		return this.triangleUp();
	}

	/**
	 * Generates a diagonal matrix using triangularization Low and then Up
	 * @return The diagonal matrix
	 */  
	public MatrixComplex diagonalLo() {
		return (this.triangleLo()).triangleUp();
	}

	/**
	 * Generates a diagonal matrix using triangularization Up and then Lo
	 * @return The diagonal matrix
	 */  
	public MatrixComplex diagonalUp() {
		return (this.triangleUp()).triangleLo();
	}

	/**
	 * Shortcut to determinantGauss.
	 * Calculate the matrix determinant by the default rule (Gauss)
	 * @return The value of the determinant.
	 */
	public Complex determinant() {
		return this.determinantGauss();
	}

	/**
	 * Calculates the matrix determinant by the Gauss' method.
	 * @return The value of the determinant.
	 */
	public Complex determinantGauss() {
		int rowLen = this.rows();

		if (rowLen != this.cols()) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		Complex cResult = new Complex(1, 0);
		MatrixComplex auxMatrix = this.triangle();
		for (int iter = 0; iter < rowLen; ++iter) {
			cResult = cResult.times(auxMatrix.complexMatrix[iter][iter]);
		}
		return cResult.times(auxMatrix.mSign);
	}

	/**
	 * Private method that calculates the matrix 3x3 determinant by the Sarrus' rule.
	 * @return The value of the determinant.
	 */
	private Complex determinant3() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int k = 0;
		Complex cGroup = new Complex(1,0);
		Complex determinant = new Complex();

		if (rowLen != colLen) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		for (int row = 0; row < rowLen; ++row) {
			k = 0;
			for (int col = 0; col < colLen; ++col) {
				cGroup = cGroup.times(this.complexMatrix[(row+k++)%rowLen][col]);
			}
			determinant = determinant.plus(cGroup);
			cGroup.setComplexPol(1, 0);

		}
		for (int row = rowLen-1; row >= 0; --row) {
			k = rowLen-1;
			for (int col = 0; col < colLen; ++col) {
				cGroup = cGroup.times(this.complexMatrix[(row+k--)%rowLen][col]);
			}
			determinant = determinant.minus(cGroup);
			cGroup.setComplexRec(1, 0);
		}
		return determinant;
	}

	/**
	 * Calculates the matrix determinant through matrix of adjoints (cofactors)
	 * DO NOT USE FOR MATRIX OVER 5x5.
	 * @return The value of the determinant.
	 */
	public Complex determinantAdj() {
		Complex cSum = new Complex(); 
		int rowLen = this.rows();
		if (rowLen == 1) 
			return this.complexMatrix[0][0];	

		int colLen = this.cols();

		if (rowLen != colLen) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		if (rowLen == 2)
			return (this.complexMatrix[0][0].times(this.complexMatrix[1][1])).
					minus (this.complexMatrix[0][1].times(this.complexMatrix[1][0]));

		if (rowLen == 3) {  //bottom case of recursion. size 1 complexMatrix determinant is itself.
			return this.determinant3();
		}

		for (int i = 0; i < rowLen; ++i){ //finds determinant using row-by-row expansion
			MatrixComplex smaller = new MatrixComplex(rowLen - 1, colLen - 1); //creates smaller complexMatrix- values not in same row, column
			for (int a = 1; a < rowLen; ++a) {
				for (int b = 0; b < colLen; ++b) {
					if (b < i) { 
						smaller.complexMatrix[a-1][b] = this.complexMatrix[a][b];
					}
					else if (b > i) { smaller.complexMatrix[a-1][b-1] = this.complexMatrix[a][b];
					}
				}
			}
			if ((i&1) == 0) cSum = cSum.plus (this.complexMatrix[0][i].times(smaller.determinantAdj()));
			else cSum = cSum.minus(this.complexMatrix[0][i].times(smaller.determinantAdj()));
		}
		return cSum ; //returns determinant value. once stack is finished, returns final determinant.
	}

	/**
	 * Checks if the matrix is symmetric or not
	 * @return True if the matrix is symmetric
	 */
	public boolean isSymmetric() {
		if (this.rows() != this.cols()) return false;
		for (int row = 0; row < this.rows(); ++row)
			for (int col = row; col < this.cols(); ++col)
				if(!getItem(row, col).equals(getItem(col, row))) return false;
		return true;
	}
	
	/**
	 * Checks if the matrix is antisymmetric or not
	 * @return True if the matrix is antisymmetric
	 */
	public boolean isAntiSymmetric() {
		if (this.rows() != this.cols()) return false;
		for (int row = 0; row < this.rows(); ++row)
			for (int col = row; col < this.cols(); ++col)
				if(row == col) {
					if (!getItem(row, col).equals(Complex.ZERO)) return false;
				}
				else if(!getItem(row, col).equals(getItem(col, row).opposite().conjugate())) return false;
		return true;
	}

	/**
	 * Checks if the matrix is skew-symmetric or not
	 * @return True if the matrix is skew-symmetric
	 */
	public boolean isSkewSymmetric() {
		return isAntiSymmetric();
	}

	/**
	 * Checks if the matrix is hermitian or not
	 * @return True if the matrix is hermitian
	 */
	public boolean isHermitian() {
		if (this.rows() != this.cols()) return false;
		for (int row = 0; row < this.rows(); ++row)
			for (int col = row; col < this.cols(); ++col)
				if(row == col) {
					if (!getItem(row, col).isPureReal()) return false;
				}
				else if(!getItem(row, col).equals(getItem(col, row).conjugate())) return false;
		return true;
	}
	
	/**
	 * Checks if the matrix is antihermitian or not
	 * @return True if the matrix is antihermitian
	 */
	public boolean isAntiHermitian() {
		if (this.rows() != this.cols()) return false;
		for (int row = 0; row < this.rows(); ++row)
			for (int col = row; col < this.cols(); ++col)
				if(row == col) {
					if (!getItem(row, col).isPureImaginary()) return false;
				}
				else if(!getItem(row, col).equals(getItem(col, row).opposite().conjugate())) return false;
		return true;
	}

	/**
	 * Checks if the matrix is skew-hermitian or not
	 * @return True if the matrix is skew-hermitian
	 */
	public boolean isSkewHermitian() {
		return isAntiHermitian();
	}
	
	/**
	 * Method for creating an Square Identity array of "dim" size
	 * @param dim The size of Identity array
	 * @return The Identity array
	 */
	public static MatrixComplex eye(int dim) {
		MatrixComplex eye = new MatrixComplex(dim);
		eye.initMatrixDiag(1,0);
		return eye;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPostiveDefinite() {
		if (this.isHermitian()) {
			Eigenspace eigenSpace = new Eigenspace(this);
			MatrixComplex eigenvals = eigenSpace.eigenvalues();
			for (int row = 0; row < eigenvals.rows(); ++row) {
				if (Math.abs(eigenvals.getItem(row, 0).imp()) < Complex.zero() &&
						eigenvals.getItem(row, 0).rep() <= Complex.zero() )
					return false;
			}
			return true;
		}
		else return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPostiveSemiDefinite() {
		if (this.isHermitian()) {
			Eigenspace eigenSpace = new Eigenspace(this);
			MatrixComplex eigenvals = eigenSpace.eigenvalues();
			for (int row = 0; row < eigenvals.rows(); ++row) {
				if (Math.abs(eigenvals.getItem(row, 0).imp()) < Complex.zero() &&
						eigenvals.getItem(row, 0).rep() < Complex.zero() )
					return false;
			}
			return true;
		}
		else return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNegtiveDefinite() {
		if (this.isHermitian()) {
			Eigenspace eigenSpace = new Eigenspace(this);
			MatrixComplex eigenvals = eigenSpace.eigenvalues();
			for (int row = 0; row < eigenvals.rows(); ++row) {
				if (Math.abs(eigenvals.getItem(row, 0).imp()) > Complex.zero() && 
						eigenvals.getItem(row, 0).rep() >= -Complex.zero() )
					return false;
			}
			return true;
		}
		else return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNegtiveSemiDefinite() {
		if (this.isHermitian()) {
			Eigenspace eigenSpace = new Eigenspace(this);
			MatrixComplex eigenvals = eigenSpace.eigenvalues();
			for (int row = 0; row < eigenvals.rows(); ++row) {
				if (Math.abs(eigenvals.getItem(row, 0).imp()) > Complex.zero() && 
						eigenvals.getItem(row, 0).rep() > -Complex.zero() )
					return false;
			}
			return true;
		}
		else return false;
	}

	/**
	 * Checks if there is a zero on the main diagonal.
	 * @return True if a zero was found, false otherwise.
	 */
	public boolean hasZeroMainDiag() {
		for (int i=0; i < this.rows(); ++i)
			if (this.getItem(i, i).isZero()) return true;
		return false;
	}
	
	/**
	 * Checks if there is one item on the main diagonal for which its REAL PART is zero or negative .
	 * @return False if a non positive was found, false otherwise.
	 */
	public boolean repPositiveMainDiag() {
		for (int i=0; i < this.rows(); ++i)
			if (this.getItem(i, i).rep() < 0) return false;
		return true;
	}
	
	/**
	 * Method for creating an Square Identity array of "this" matrix size
	 * @param dim The size of Identity array
	 * @return The Identity array
	 */
	public MatrixComplex eye() {
		MatrixComplex eye = new MatrixComplex(this.rows());
		eye.initMatrixDiag(1,0);
		return eye;
	}

	/**
	 * Copies the 1xN values of a one row array and put one by one in a diagonal NxN matrix
	 * @param values The values of a one row array
	 * @return The diagonal NxN matrix
	 */
	public static MatrixComplex diagonal(MatrixComplex values) {
		MatrixComplex newValArray = values.clone();
		if (newValArray.rows() == 1) newValArray = newValArray.transpose();
		
		MatrixComplex sqDiagonal = new MatrixComplex(newValArray.rows());
		for (int row = 0; row < newValArray.rows(); ++row) {
			sqDiagonal.setItem(row, row, newValArray.getItem(row, 0));
		}
		return sqDiagonal;
	}
	
	/*
	 * ***********************************************
	 * EQUATION SYSTEMS
	 * ***********************************************
	 */
	
	/**
	 * Returns a new matrix with the coefficients of the object matrix.
	 * The new matrix is the original one with the independent terms removed.
	 * @return The new matrix with the coefficients copied without independent terms.
	 */
 	public MatrixComplex coefMatrix() {
		return MatrixComplexEquationSystems.coefMatrix(this);
	}

 	/**
	 * Returns a new matrix with the independent terms of the object matrix.
	 * The new matrix is the independent terms column matrix.
	 * @return The new matrix with the independent terms.
	 */
	public MatrixComplex indMatrix() {
		return MatrixComplexEquationSystems.indMatrix(this);
	}

	/**
	 * Returns a new matrix with the independent terms of the object matrix.
	 * The new matrix is the independent terms column matrix.
	 * @return The new matrix with the independent terms.
	 */
	public MatrixComplex constMatrix() {
		return MatrixComplexEquationSystems.constMatrix(this);
	}
	
	/**
	 * Defines the constants that identify the type of equation system being solved.
	 */
	public static final int INCONSISTENT = -1;
	public static final int INDETERMINATE = 0;
	public static final int DETERMINATE = 1;

	/**
	 * Identifies if the system of equations is isHomogeneous.
	 * @return Returns true if the system is isHomogeneous, false otherwise.
	 */
	public boolean isHomogeneous() {
		return MatrixComplexEquationSystems.isHomogeneous(this);
	}

	/**
	 * Returns the homogeneous equation system of this
	 * @return the homogeneous equation system of this
	 */
	public MatrixComplex homogeneous() {
		return MatrixComplexEquationSystems.homogeneous(this);
	}
	
	/**
	 * Identifies the type of systems of equations returning the constant according to the definition.
	 * @return INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 */
	public int typeEqSys() {
		return MatrixComplexEquationSystems.typeEqSys(this);
	}

	/**
	 * Returns a string indicating the system type of equations according to type "type".
	 * @param type INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 * @param lambda Parameter value to calculate solutions for indeterminate systems.
	 */
	public String strTypeEqSys(int type, Complex lambda) {
		return MatrixComplexEquationSystems.strTypeEqSys(type, lambda);
	}

	/**
	 * Prints a message indicating the system type of equations according to type "type".
	 * @param type INCONSISTENT = -1, INDETERMINATE = 0 or DETERMINATE = 1.
	 * @param lambda Parameter value to calculate solutions for indeterminate systems.
	 */
	public void printTypeEqSys(int type, Complex lambda) {
		MatrixComplexEquationSystems.printTypeEqSys(type, lambda);
	}

	/**
	 * Prints a message indicating the system type of equations according to type "type". lambda is 1 by default
	 */
	public void printTypeEqSys() {
		MatrixComplexEquationSystems.printTypeEqSys(this);
	}

	/**
	 * If the number of equations is less than of unknowns, the missing equations are introduced with the coefficients and the independent term set to zero.
	 * @return the system of equations completed with equations that are missing to zero.
	 */
	public MatrixComplex completeEqSys() {
		return MatrixComplexEquationSystems.completeEqSys(this);
	}

	/**
	 * Finds the solutions of an equation systems by the default rule (Gauss reduction)
	 * @return The solutions of the equation systems
	 */
	public MatrixComplex solve() {
		return MatrixComplexEquationSystems.solve(this);
	}

	/**
	 * Shortcut to solveGauss
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist.
	 */
	public MatrixComplex solve(Complex lambda) {
		return MatrixComplexEquationSystems.solve(this, lambda);
	}

	/**
	 * Equation evaluator
	 * Evaluates an equation replacing its unknowns with values
	 * @param row
	 * @param col
	 * @param point
	 * @return
	 */
	public Complex eqEval(int row, int col,MatrixComplex point) {
		return MatrixComplexEquationSystems.eqEval(this, row, col, point);
	}
	
	/**
	 * Removes the null rows of a Matrix
	 * @return A new Matrix without the null rows
	 */
	public MatrixComplex removeNullRows() {
		return MatrixComplexEquationSystems.removeNullRows(this);
	}

	/**
	 * Removes the duplicated rows of a Matrix
	 * @return A new Matrix without the duplicated rows
	 */
	public MatrixComplex removeDuplicateRows() {
		return MatrixComplexEquationSystems.removeDuplicateRows(this);
	}
	
	/**
	 * Returns the minimum number of LI solutions that the equation system has
	 * @return The minimum number of LI solutions
	 */
	public int nbrOfSolutions() {
		return MatrixComplexEquationSystems.nbrOfSolutions(this);
	}

	/**
	 * Calculates the minimum number of LI solutions of an equation system and writes it on the console
	 * @return The minimum number of LI solutions
	 */
	public int nbrOfSolutionsText() {
		return MatrixComplexEquationSystems.nbrOfSolutionsText(this);
	}

	/**
	 * Gets the row (rowIdx) of a Matrix
	 * @param rowIdx The row index to retrieve. 1st row is 0, and so on.
	 * @return The row selected
	 */
	public MatrixComplex getRow(int rowIdx) {
		return MatrixComplexEquationSystems.getRow(this, rowIdx);
	}

	/**
	 * Gets the col (colIdx) of a Matrix
	 * @param colIdx The col index to retrieve. 1st col is 0, and so on.
	 * @return The col selected
	 */
	public MatrixComplex getCol(int colIdx) {
		return MatrixComplexEquationSystems.getCol(this, colIdx);
	}
	/**
	 * Sets the row (rowIdx) of "this" with the values of rowMatrix
	 * @param rowIdx The row index to retrieve. 1st row is 0, and so on.
	 */
	public void setRow(int rowIdx, MatrixComplex rowMatrix) {
		MatrixComplexEquationSystems.setRow(this, rowIdx, rowMatrix);
	}

	/**
	 * Sets the column colIdx of the matrix to certain complex value
	 * @param colIdx The Id of the col to set
	 * @param cValue The value to set
	 */
	public void setCol(int colIdx, Complex cValue) {
		MatrixComplexEquationSystems.setCol(this, colIdx, cValue);
	}

	/**
	 * Sets colum colMatrix into this at colIdx column
	 * @param colIdx
	 * @param colMatrix
	 */
	public void setCol(int colIdx, MatrixComplex colMatrix) {
		MatrixComplexEquationSystems.setCol(this, colIdx, colMatrix);
	}

	/**
	 * Sets the row rowIdx of the matrix to certain complex value
	 * @param rowIdx The Id of the col to set
	 * @param cValue The value to set
	 */
	public void setRow(int rowIdx, Complex cValue) {
		MatrixComplexEquationSystems.setRow(this, rowIdx, cValue);
	}

	/**
	 * finds the solutions to a equation systems by the default rule (Gauss reduction)
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The column matrix with the solutions if they exist, otherwise null.
	 */	
	public MatrixComplex solveGauss(Complex lambda) {
		return MatrixComplexEquationSystems.solveGauss(this, lambda);
	}

	/**
	 * finds the solutions to a equation systems using the Cramer's rule
	 * @return The column matrix with the solutions if they exist.
	 */
	public MatrixComplex solveCramer() {
		return MatrixComplexEquationSystems.solveCramer(this);
	}

	/**
	 * rowReduce performs the Gauss-Jordan elimination, adding multiples of rows together so as to produce zero elements when possible. The final matrix is in reduced row echelon form.
	 * @return The final matrix in its reduced row echelon form.
	 */
	public MatrixComplex rowReduce() {
		return MatrixComplexEquationSystems.rowReduce(this);
	}

	/**
	 * Returns the sub-matrix from row, col of order "order" (number of rows/columns taken)
	 * @param row The index of the initial row.
	 * @param col The index of the initial column.
	 * @param order Number of rows/columns taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrixAug(int row, int col, int order) {
		return MatrixComplexEquationSystems.subMatrixAug(this, row, col, order);
	}

	/**
	 * Returns the sub-matrix from row, col of order "order" (number of rows/columns taken)
	 * @param row The index of the initial row.
	 * @param col The index of the initial column.
	 * @param order Number of rows/columns taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrix(int row, int col, int order) {
		return MatrixComplexEquationSystems.subMatrix(this, row, col, order);
	}

	/**
	 * Returns the sub-matrix formed with rows an columns indicated in the parameters "rows" and "columns".
	 * @param rows A list of integers of the rows to be taken.
	 * @param cols A list of integers of the columns to be taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrix(int[] rows, int[] cols) {
		return MatrixComplexEquationSystems.subMatrix(this, rows, cols);
	}

	/**
	 * Returns the sub-matrix formed with rows an columns indicated in the parameters "rows" and "columns" as a list of comma separated values.
	 * @param Srows A string with a list of integers separated by commas of the rows to be taken.
	 * @param Scols A string with a list of integers separated by commas of the columns to be taken.
	 * @return The new sub-matrix.
	 */
	public MatrixComplex subMatrix(String Srows, String Scols) {
		return MatrixComplexEquationSystems.subMatrix(this, Srows, Scols);
	}

	/**
	 * Returns TRUE if rowIdx has only zeros
	 * @param rowIdx The row to check
	 * @return True if is null
	 */
	public boolean isNullRow(int rowIdx) {
		return MatrixComplexEquationSystems.isNullRow(this, rowIdx);
	}

	/**
	 * Returns TRUE if colIdx has only zeros
	 * @param colIdx The col to check
	 * @return True if is null
	 */
	public boolean isNullCol(int colIdx) {
		return MatrixComplexEquationSystems.isNullCol(this, colIdx);
	}

	/**
	 * Removes every row and col which are null
	 * @return The reduced matrix without null rows and cols
	 */
	public MatrixComplex reduce() {
		return MatrixComplexEquationSystems.reduce(this);
	}
	
	/**
	 * Calculates the rank of an array.
	 * @return The rank of the matrix.
	*/
	public int rank() {
		return MatrixComplexRank.rank1(this);
	}

	/**
	 * Calculates the rank of an array. It is not reliable for ill-conditioned matrix due to lack of precision
	 * Kept for testing proposes
	 * @return The rank of the matrix.
	 */
	public int rank0() {
		return MatrixComplexRank.rank0(this);
	}

	/**
	 * Major Independent Lineal submatrix. Traverse the different minors of the matrix untils the first not dependent linear minor
	 * @return The major independet lineal minor
	 */
	public MatrixComplex majorIL() {
		return MatrixComplexRank.majorIL(this);
	}

	/**
	 * Calculates the rank of an array.
	 *  TEST FAILED FIXED
	 * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	 * :                          TEST #2853                           :
	 * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	 * rank1 = 4
	 * rank2 = 3
	 * CMPLXAR: new MatrixComplex("1.00,-1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,1.00,-1.00,1.00;-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,-1.00");
	 * MAXIMA : rank(matrix([1.00,-1.00,1.00,1.00,-1.00,1.00],[1.00,-1.00,1.00,1.00,-1.00,1.00],[-1.00,1.00,1.00,1.00,-1.00,1.00],[1.00,-1.00,-1.00,-1.00,1.00,-1.00],[-1.00,1.00,-1.00,1.00,-1.00,-1.00]))
	 * OCTAVE : rank([1.00,-1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,1.00,-1.00,1.00;-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,-1.00])
	 * WOLFRAM: MatrixRank[{{1.00,-1.00,1.00,1.00,-1.00,1.00},{1.00,-1.00,1.00,1.00,-1.00,1.00},{-1.00,1.00,1.00,1.00,-1.00,1.00},{1.00,-1.00,-1.00,-1.00,1.00,-1.00},{-1.00,1.00,-1.00,1.00,-1.00,-1.00}}]
	 * *****************************************************************
	 * |                          TEST #3648                           |
	 * *****************************************************************
	 * rank1 = 4
	 * rank2 = 3
	 * CMPLXAR: new MatrixComplex("1.00,1.00,-1.00,1.00,1.00,1.00;1.00,1.00,-1.00,1.00,1.00,1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;-1.00,-1.00,1.00,-1.00,-1.00,-1.00;-1.00,-1.00,1.00,-1.00,-1.00,1.00");
	 * MAXIMA : rank(matrix([1.00,1.00,-1.00,1.00,1.00,1.00],[1.00,1.00,-1.00,1.00,1.00,1.00],[-1.00,1.00,-1.00,1.00,-1.00,1.00],[-1.00,-1.00,1.00,-1.00,-1.00,-1.00],[-1.00,-1.00,1.00,-1.00,-1.00,1.00]))
	 * OCTAVE : rank([1.00,1.00,-1.00,1.00,1.00,1.00;1.00,1.00,-1.00,1.00,1.00,1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;-1.00,-1.00,1.00,-1.00,-1.00,-1.00;-1.00,-1.00,1.00,-1.00,-1.00,1.00])
	 * WOLFRAM: MatrixRank[{{1.00,1.00,-1.00,1.00,1.00,1.00},{1.00,1.00,-1.00,1.00,1.00,1.00},{-1.00,1.00,-1.00,1.00,-1.00,1.00},{-1.00,-1.00,1.00,-1.00,-1.00,-1.00},{-1.00,-1.00,1.00,-1.00,-1.00,1.00}}]
	 * *****************************************************************
	 * |                          TEST #7425                           |
	 * *****************************************************************
	 * rank1 = 4
	 * rank2 = 3
	 * CMPLXAR: new MatrixComplex("-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,1.00,-1.00,-1.00;1.00,-1.00,1.00,1.00,-1.00,-1.00");
	 * MAXIMA : rank(matrix([-1.00,1.00,1.00,1.00,-1.00,1.00],[1.00,-1.00,1.00,-1.00,1.00,-1.00],[-1.00,1.00,-1.00,1.00,-1.00,1.00],[1.00,-1.00,-1.00,1.00,-1.00,-1.00],[1.00,-1.00,1.00,1.00,-1.00,-1.00]))
	 * OCTAVE : rank([-1.00,1.00,1.00,1.00,-1.00,1.00;1.00,-1.00,1.00,-1.00,1.00,-1.00;-1.00,1.00,-1.00,1.00,-1.00,1.00;1.00,-1.00,-1.00,1.00,-1.00,-1.00;1.00,-1.00,1.00,1.00,-1.00,-1.00])
	 * WOLFRAM: MatrixRank[{{-1.00,1.00,1.00,1.00,-1.00,1.00},{1.00,-1.00,1.00,-1.00,1.00,-1.00},{-1.00,1.00,-1.00,1.00,-1.00,1.00},{1.00,-1.00,-1.00,1.00,-1.00,-1.00},{1.00,-1.00,1.00,1.00,-1.00,-1.00}}]
	 * @return The rank of the matrix.
	 */
	public int rank1() {
		return MatrixComplexRank.rank1(this);
	}

	/**
	 * The rank of A is equal the number of non-zero singular values of the characteristic polynomial of A.adjoint()*A
	 * This is method used for other numerical programs
	 * Kept for testing proposes
	 * @return The rank of the matrix.
	 */
	public int rank2() {
		return MatrixComplexRank.rank2(this);
	}

	/**
	 * Calculates the nullity of a Vectorial Space.
	 * @return The nullity of the Vectorial Space.
	 */
	public int nullity() {
		return MatrixComplexRank.nullity(this);
	}

	/**
	 * Checks if the matrix is upper triangular.
	 * @return true if the matrix is upper triangular, false otherwise.
	 */
	public boolean isTriangleUp() {
		return MatrixComplexRank.isTriangleUp(this);
	}

	/**
	 * Sorts the rows of an array so that those rows whose elements are all zeros occupy the highest positions in the array
	 * @return The array with the null rows at the top
	 */
	public MatrixComplex hollow() {
		return MatrixComplexRank.hollow(this);
	}

	/**
	 * Sorts the rows of an array so that those rows whose elements are all zeros occupy the lowest positions in the array
	 * @return The array with the null rows at the end
	 */
	public MatrixComplex heap() {
		return MatrixComplexRank.heap(this);
	}

	/**
	 * Calculates the upper triangularization of the matrix.
	 * @return The upper triangularized matrix.
	 */
	public MatrixComplex triangleUp() {
		return MatrixComplexRank.triangleUp(this);
	}

	/**
	 * It Upper Triangularize  the matrix by rearranging its rows so that they occupy the place corresponding to their non-zero element on the diagonal
	 * @return The perfect upper triangularized array
	 */
	public MatrixComplex triangleUpPerfect() {
		return MatrixComplexRank.triangleUpPerfect(this);
	}

	/**
	 * Checks if the matrix is lower triangular.
	 * @return true if the matrix is lower triangular, false otherwise.
	 */
	public boolean isTriangleLo() {
		return MatrixComplexRank.isTriangleLo(this);
	}

	/**
	 * Calculates the lower triangularization of the matrix.
	 * @return The lower triangularized matrix.
	 */
	public MatrixComplex triangleLo(){
		return MatrixComplexRank.triangleLo(this);
	}

	/**
	 * Indicates if the array is square or nor
	 * @return true for square matrix, false otherwise
	 */
	public boolean isSquare() {
		return MatrixComplexRank.isSquare(this);
	}

	/**
	 * Gram-Schmidt orthogonalization process via Gaussian elimination.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtGauss() {
		return MatrixComplexOrtho.gramSchmidtGauss(this);
	}

	/**
	 * Gram-Schmidt orthogonalization process.
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * It uses the MatrixComplex dotprod column oriented
	 * Use the Column calc for the dotprod. Otherwise dotprod needs work with transposed
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidt() {
		return MatrixComplexOrtho.gramSchmidt(this);
	}

	/**
	 * Gram-Schmidt Full orthogonalization process. Full means that the not included vectors of the base are randomly generated with integers between 0 and 9.
	 * The calculated orthogonal matrix is extended to the full dimension of the matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtFull() {
		return MatrixComplexOrtho.gramSchmidtFull(this);
	}

	/**
	 * Gram-Schmidt Full Modified orthogonalization process. Modified Algorithm (http://www.ehu.eus/izaballa/Ana_Matr/Apuntes/lec6.pdf)
	 * The calculated orthogonal matrix is extended to the full dimension of the matrix.
	 * @return The matrix with the orthogonal base that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtMFull() {
		return MatrixComplexOrtho.gramSchmidtMFull(this);
	}

	/**
	 * Gram-Schmidt Modified orthogonalization process. Modified Algorithm (http://www.ehu.eus/izaballa/Ana_Matr/Apuntes/lec6.pdf)
	 * The calculated orthogonal matrix is reduced to the smaller dimension of the matrix.
	 * @return The matrix with the orthogonal basis that generates the same vector subspace.
	 */
	public MatrixComplex gramSchmidtM() {
		return MatrixComplexOrtho.gramSchmidtM(this);
	}

	/**
	 * Shortcut to the preferred orthogonalization method
	 * @return The orthogonal Matrix
	 */
	public MatrixComplex orthogonalize() {
		return MatrixComplexOrtho.orthogonalize(this);
	}

	/**
	 * Shortcut to normalize method.
	 * @return The normalized matrix.
	 */
	public MatrixComplex normalize() {
		return MatrixComplexOrtho.normalize(this);
	}

	/**
	 * Shortcut to the preferred orthonormalization method
	 * @return The orthonormal Matrix
	 */
	public MatrixComplex orthonormalize() {
		return MatrixComplexOrtho.orthonormalize(this);
	}

	/**
	 * Normalizes the matrix by columns using the Euclidean norm.
	 * @return The normalized matrix.
	 */
	public MatrixComplex normalizeByCols() {
		return MatrixComplexOrtho.normalizeByCols(this);
	}

	/**
	 * Normalizes the matrix by rows using the Euclidean norm.
	 * @return The normalized matrix.
	 */
	public MatrixComplex normalizeByRows() {
		return MatrixComplexOrtho.normalizeByRows(this);
	}

	/**
	 * We define here A ⊗ B, the Kronecker product of two square matrices A = (ai,j) and B of dimension nA and nB, 
	 * respectively: A ⊗ B is the square matrix of dimension nA nB obtained from A by replacing every entry ai,j by ai,j B.
	 * https://www.sciencedirect.com/topics/mathematics/kronecker-product
	 * @param matrix
	 * @return
	 */
	public MatrixComplex kroneckerprod(MatrixComplex matrix) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowLenC = matrix.rows();
		int colLenC = matrix.cols();

		MatrixComplex kronmat = new MatrixComplex(rowLen*rowLenC, colLen*colLenC);
		int row = 0, col = 0;
		for (int f1 = 0; f1 < rowLen; ++f1) {
			for (int f2 = 0; f2 < rowLenC; ++f2) {
				for (int c1 = 0; c1 < colLen; ++c1) {
					for (int c2 = 0; c2 < colLenC; ++c2) {
						// System.out.printf("(f1:%d,c1:%d)*(f2:%d,c2:%d) - ", f1,c1, f2,c2); 
						kronmat.setItem(row, col, this.getItem(f1, c1).times(matrix.getItem(f2, c2)));
						++col;
					}
				}
				// System.out.println();
				++row;
				col = 0;
			}
		}
		return kronmat;
	}
	
	/**
	 * Calulates the Kernel of a base
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The kernel vector components
	 */
	public MatrixComplex kernel(Complex lambda) {
		MatrixComplex kernel = new MatrixComplex();
		if (this.rows() != this.cols()) {
			System.err.println("The matrix has to be square.");
			return kernel;
		}
		Syseq newSysEq = new Syseq(this.augment());
		return newSysEq.solve(lambda);
	}
	
	/**
	 * Calulates the Kernel of a base using Complex.ONE as seed
	 * @return The kernel vector components
	 */
	public MatrixComplex kernel() {
		return this.kernel(Complex.ONE);
	}
	
	/**
	 * Shortcut to kernel() method
	 * @param lambda Value of lambda parameter used to calculate solutions in indeterminate systems.
	 * @return The kernel vector components
	 */
	public MatrixComplex ker(Complex lambda) {
		return kernel(lambda);
	}
	
	/**
	 * Shortcut to kernel() method
	 * @return The kernel vector components
	 */
	public MatrixComplex ker() {
		return kernel();
	}

	/**
	 * Computes a genuine BASIS of the nullspace (kernel) of this matrix via Gauss-Jordan
	 * elimination to reduced row echelon form, tracking pivot vs. free columns explicitly.
	 * <p>
	 * Unlike {@link #kernel()}/{@link #kernel(Complex)} (which return a SINGLE vector, all free
	 * variables set to the same scalar {@code lambda} -- the right tool when the nullspace is
	 * known to be 1-dimensional, the common case throughout this project), this returns one
	 * independent vector per free column: {@code nullity()} rows, each with exactly one free
	 * variable set to {@code 1} and the rest to {@code 0}, back-substituted through the reduced
	 * pivot rows. Needed to generalize {@code Jordan.java} beyond geometric multiplicity 1, where
	 * a single scalar-parameterized vector can no longer span the eigenspace.
	 * @return A matrix with one basis vector per row ({@code cols()-rank()} rows, {@code cols()}
	 * columns each); empty (0 rows) if the nullspace is trivial.
	 */
	public MatrixComplex nullspaceBasis() {
		int n = this.cols();
		int rowLen = this.rows();
		MatrixComplex r = this.copy();

		int[] pivotColOfRow = new int[rowLen];
		boolean[] isPivotCol = new boolean[n];
		int pivotRow = 0;

		for (int col = 0; col < n && pivotRow < rowLen; ++col) {
			int best = pivotRow;
			double bestMod = r.getItem(pivotRow, col).mod();
			for (int row = pivotRow + 1; row < rowLen; ++row) {
				double mod = r.getItem(row, col).mod();
				if (mod > bestMod) { bestMod = mod; best = row; }
			}
			if (r.getItem(best, col).isZero()) continue; // no usable pivot in this column: free column

			if (best != pivotRow) r.swapRows(pivotRow, best);
			r.Ftransf(pivotRow, r.getItem(pivotRow, col).inverse()); // normalize pivot to 1
			for (int row = 0; row < rowLen; ++row) {
				if (row == pivotRow) continue;
				Complex factor = r.getItem(row, col);
				if (!factor.isZero()) r.Ftransf(row, pivotRow, factor.opposite());
			}
			pivotColOfRow[pivotRow] = col;
			isPivotCol[col] = true;
			++pivotRow;
		}

		int nullity = 0;
		for (int col = 0; col < n; ++col) if (!isPivotCol[col]) ++nullity;

		MatrixComplex basis = new MatrixComplex(nullity, n);
		int basisRow = 0;
		for (int freeCol = 0; freeCol < n; ++freeCol) {
			if (isPivotCol[freeCol]) continue;
			basis.setItem(basisRow, freeCol, Complex.ONE);
			for (int prow = 0; prow < pivotRow; ++prow)
				basis.setItem(basisRow, pivotColOfRow[prow], r.getItem(prow, freeCol).opposite());
			++basisRow;
		}
		return basis;
	}

	/*
	 * ***********************************************
	 * CHARACTERISTIC POLYNOMIAL
	 * ***********************************************
	 */
	
	/**
	 * Returns the characteristic polynomial of the matrix.
	 * The characteristic polynomial of a square matrix is a polynomial which is invariant under matrix similarity and has the eigenvalues as roots. It has the determinant and the trace of the matrix as coefficients. The characteristic polynomial of an endomorphism of vector spaces of finite dimension is the characteristic polynomial of the matrix of the endomorphism over any base; it does not depend on the choice of a basis
	 * @return The characteristic polynomial
	 */
	public Polynom charactPoly() {
		int rowLen = this.rows();
		int colLen = this.cols();
		Polynom charactPoly = new Polynom(colLen);

		if (rowLen != colLen) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}

		for (int order = 0; order <= colLen; ++order) {
			switch (order) {
				case 0: 
					charactPoly.complexMatrix[0][colLen-order].setComplexPol(1, 0); 
					break;
				case 1: 
					charactPoly.complexMatrix[0][colLen-order] = this.trace().opposite(); 
					break;
				default: 
					charactPoly.complexMatrix[0][colLen-order] = this.coefCP(order).times(Math.pow(-1, order)); 
					break;
			}
		}
		//this.print(charactPolyMatrix.complexMatrix);
		return (colLen % 2) == 0 ? charactPoly : charactPoly.opposite();
	}

	/**
	 * Returns a new augmented matrix.
	 * Copy the original matrix and add a column to zeros.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		MatrixComplex extendedMatrix = new MatrixComplex(rowLen, colLen+1);
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				extendedMatrix.complexMatrix[row][col] = this.complexMatrix[row][col];
			}
			extendedMatrix.complexMatrix[row][col].setComplexPol(0,0);
		}
		return extendedMatrix;
	}

	public MatrixComplex augment(int numCols) {
		MatrixComplex extendedMatrix = this;
		for (int iter = 0; iter < numCols; ++iter) {
			extendedMatrix = extendedMatrix.augment();
		}
		return extendedMatrix;
	}

	/**
	 * Shortcut to the default augment method
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment(MatrixComplex interms) {
		return augment2(interms);
	}
	
	/**
	 * DEPRECATED Returns a new augmented array with the FIRST column of terms.
	 * DEPRECATED Copies the original matrix and add the FIRST column "interms".
	 * DEPRECATED Left only for fail recovery of augment1()
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment1(MatrixComplex interms) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		MatrixComplex extendedMatrix = new MatrixComplex(rowLen, colLen+1);
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				extendedMatrix.complexMatrix[row][col] = this.complexMatrix[row][col];
			}
			extendedMatrix.complexMatrix[row][col] = interms.complexMatrix[row][0];
		}
		return extendedMatrix;
	}

	/**
	 * Returns a new augmented array with ALL the columns of terms.
	 * Copies the original matrix and add the ALL the columns of "interms".
	 * @param interms The column to be added.
	 * @return The augmented matrix.
	 */
	public MatrixComplex augment2(MatrixComplex interms) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		MatrixComplex extendedMatrix = new MatrixComplex(rowLen, colLen+interms.cols());
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen; ++col) {
				extendedMatrix.complexMatrix[row][col] = this.complexMatrix[row][col];
			}
			for (int incol = 0; incol < interms.cols(); ++incol) {
				extendedMatrix.complexMatrix[row][col+incol] = interms.complexMatrix[row][incol];
			}
		}
		return extendedMatrix;
	}

	/**
	 * Returns a new array with the terms of the unknowns.
	 * Copy the original matrix and remove the column "interms".
	 * @return The augmented matrix.
	 */
	public MatrixComplex unkMatrix() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;

		MatrixComplex unkMatrix = new MatrixComplex(rowLen, colLen-1);
		for (row = 0; row < rowLen; ++row) {
			for (col = 0; col < colLen-1; ++col) {
				unkMatrix.complexMatrix[row][col] = this.complexMatrix[row][col];
			}
		}
		return unkMatrix;
	}
	
	/**
	 * Private method. Identifies if a row is used to construct the cofactor's matrix (included for convenience)
	 * @param listRows List with the rows to check.
	 * @param item The item to look for.
	 * @return true if found, otherwise false.
	 */
	private boolean foundItem(int[] listRows, int item) {
		for (int i = 0; i < listRows.length; ++i) {
			if (listRows[i] == item) 
				return true;
		}
		return false;
	}

	/**
	 * Private method. Identifies if a row is used to construct the cofactor's matrix.
	 * @param listRows List with the rows to check.
	 * @param item The item to look for.
	 * @return true if NOT found, otherwise false.
	 */    
	private boolean notFoundItem(int[] listRows, int item) {
		for (int i = 0; i < listRows.length; ++i) {
			if (listRows[i] == item) 
				return false;
		}
		return true;
	}

	/**
	 * Calculates the new cofactor matrix.
	 * @return The new cofactor matrix.
	 */
	public MatrixComplex cofactor() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;
		MatrixComplex cofactor = new MatrixComplex(rowLen);

		if (rowLen != colLen) {
			throw new IllegalArgumentException("Not valid matrix: The matrix has to be square.");
		}
		
		for (row = 0; row < rowLen; ++row)
			for (col = 0; col < colLen; ++col) {
				cofactor.complexMatrix[row][col] = this.cofactors(row, col).determinant().times(((col+row)&1)==0?1:-1);
			}
		return cofactor;
	}
	
	/**
	 * Returns a new matrix of cofactors.
	 * @param includedRows The list with the indexes of the rows included in the new matrix.
	 * @return The new matrix of cofactors.
	 */
	public MatrixComplex cofactors(int[] includedRows) {
		int rowLen = this.rows();
		int order = includedRows.length;

		MatrixComplex cofactor = new MatrixComplex(order, order);
		for (int row = 0, rrow = 0; row < rowLen; ++row) {
			if (notFoundItem(includedRows, row)) 
				continue;
			for (int col = 0, ccol = 0; col < rowLen; ++col) {
				if (notFoundItem(includedRows, col)) 
					continue;
				cofactor.complexMatrix[rrow][ccol++] = this.complexMatrix[row][col];
			}
			++rrow;
		}
		return cofactor;
	}

	/**
	 * Private method. Bubblesort method for sort a list of integers. 
	 * Used in cofactors(String includedRowsList) for sorting the row list.
	 * @param in The list of integers to sort.
	 * @return The sorted list.
	 */
	private static int[] bubbleSort(int[] in) {
		int n = in.length;
		int c, d, swap;
		int matrix[] = new int[in.length];
		for (c = 0; c < n; ++c) matrix[c] = in[c];

		for (c = 0; c < n-1; ++c) {
			for (d = 0; d < n-c-1; ++d) {
				if (matrix[d] > matrix[d+1]) { /* For descending order use < */
					swap 		= matrix[d];
					matrix[d]   = matrix[d+1];
					matrix[d+1] = swap;
				}
			}
		}
		return matrix;
	}

	/**
	 * Returns the cofactors' matrix for a included row list given in string format.
	 * @param includedRowsList The indexes' list of the rows included in the new matrix as a comma separated string.
	 * @return The new matrix of cofactors.
	 */
	public MatrixComplex cofactors(String includedRowsList) {
		String[] includedRowsTextItems = includedRowsList.split(",");
		int incRows[] = new int[includedRowsTextItems.length];    	
		for (int i = 0; i < incRows.length; ++i) incRows[i] = Integer.parseInt(includedRowsTextItems[i]);
		incRows = bubbleSort(incRows);
		return this.cofactors(incRows);
	}

	/**
	 * Calculates the determinant of the cofactors' array generated with the included rows (minor)
	 * The determinant of some smaller square matrix, cut down from this by removing one or more of its rows.
	 * @param includedRows Array with the indexes of the included rows to generate the cofactors' matrix.
	 * @return The result of the determinant.
	 */
	public Complex minor(int[] includedRows) {
		return cofactors(includedRows).determinant();
	}

	/**
	 * Private method. Returns the exact list of rows included to generate the cofactors' matrix.
	 * Used in coefCP(int order)
	 * @param order The order of the cofactors' matrix.
	 * @param v The array of the rows included.
	 * @return Aa array with the indexes of the included rows
	 */
	private int[] includedRows(int order, int[] v) {
		int includedRows[] = new int[order];		
		int i;

		for (i = 0; i < order; ++i) includedRows[i] = v[i];
		return includedRows;
	}

	/**
	 * Returns the coefficient of order "order" of the characteristic polynomial from an array
	 * @param order The order of the coefficient
	 * @return The coefficient of the polynomial
	 */
	public Complex coefCP(int order) {
		int grade = this.rows();
		Complex coefCP = new Complex();
		int[] includedRows;
		int i, j;
		int v[] = new int[grade];

		for (i = 0; i < grade; ++i) v[i]=i;
		includedRows = includedRows(order, v);
		coefCP = coefCP.plus(minor(includedRows));
		while (true) {
			i = order-1;
			while (v[i] == grade-order+i && --i >= 0);
			if (i < 0) break;
			v[i] += 1;
			for (j = i+1; j < order; ++j) v[j] = v[i]+j-i;
			includedRows = includedRows(order, v);
			coefCP = coefCP.plus(minor(includedRows));
		}
		return coefCP;
	}

	/**
	 * Constant to define the decreasing sort
	 */
	private final static int DECREASING = 0;

	/**
	 * Constant to define the increasing sort
	 */
	private final static int INCREASING = 1;

	/**
	 * Compare method parameterized by sort
	 * @param cVal1 Complex Value1. The method uses cVal1.cre()
	 * @param cVal2 Complex Value2. The method uses cVal1.cre()
	 * @param sort the type of rule to sort. From Max to min, decreasing sort = DECREASING = 0. From min to Max, increasing sort = INCREASING = 1
	 * @return the comparison result
	 */
	private static boolean compare(Complex cVal1, Complex cVal2, int sort) {
		//int signif = Complex.significative()-1;
		switch (sort) {
			//case DECREASING: return Complex.round(cVal1.cre(), signif) >= Complex.round(cVal2.cre(), signif);
			//case INCREASING: return Complex.round(cVal1.cre(), signif) <= Complex.round(cVal2.cre(), signif);
			case DECREASING: return cVal1.cre() >= cVal2.cre();
			case INCREASING: return cVal1.cre() <= cVal2.cre();
		}
		return cVal1.cre() >= cVal2.cre();
	}

	/**
	 * Sorts using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param col Index of the column to order.
	 * @param izq Pivot index on the left.
	 * @param der Pivot index on the right.
	 * @param order the type of order to sort. From Max to min, decreasing order = DECREASING = 0. From min to Max, increasing order = INCREASING = 1
	 */
	private void quicksort(int col, int izq, int der, int order) {
		int colLen = this.cols();
		int i = izq; 									// i realiza la búsqueda de izquierda a derecha
		int j = der; 									// j realiza la búsqueda de derecha a izquierda

		MatrixComplex aux = new MatrixComplex(1, colLen);
		MatrixComplex pivote = new MatrixComplex(1, colLen);
		pivote.complexMatrix[0] = this.complexMatrix[izq].clone();	// tomamos primer elemento como pivote

		while (i < j) {            																		// mientras no se crucen las búsquedas
			while (i < j &&  compare(this.complexMatrix[i][col], pivote.complexMatrix[0][col], order)) ++i;	// busca elemento mayor que pivote
			while (!compare(this.complexMatrix[j][col], pivote.complexMatrix[0][col], order)) --j;			// busca elemento menor que pivote
			if (i < j) {                      									// si no se han cruzado                      
				aux.complexMatrix[0] = this.complexMatrix[i].clone();			// los intercambia
				this.complexMatrix[i] = this.complexMatrix[j].clone();
				this.complexMatrix[j] = aux.complexMatrix[0].clone();
			}
		}
		this.complexMatrix[izq] = this.complexMatrix[j].clone(); 	// se coloca el pivote en su lugar de forma que tendremos
		this.complexMatrix[j] = pivote.complexMatrix[0].clone(); 	// los menores a su izquierda y los mayores a su derecha
		if (izq < j-1)
			this.quicksort(col, izq, j-1, order); // ordenamos submatrix izquierdo
		if (j+1 < der)
			this.quicksort(col, j+1, der, order); // ordenamos submatrix derecho
	}

	/**
	 * Sorts from maximum to minimum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param col Index of the column to order.
	 */   
	public void quicksort(int col) {
		this.quicksort(col,0,this.rows()-1, DECREASING);
	}

	/**
	 * Sorts from maximum to minimum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param col Index of the column to order.
	 */   
	public void quicksortdown(int col) {
		this.quicksort(col,0,this.rows()-1, DECREASING);
	}

	/**
	 * Sorts from minimum to maximum using the quicksort method the rows of an array by the modulus of the item in the column "col".
	 * @param col Index of the column to order.
	 */   
	public void quicksortup(int col) {
		this.quicksort(col,0,this.rows()-1, INCREASING);
	}

	/**
	 * Calculates the Hermitian matrix (or self-adjoint matrix).
	 * The Hermitian matrix (or self-adjoint matrix) is a complex square matrix that is equal to its own conjugate transposeâ€”that is, the element in the i-th row and j-th column is equal to the complex conjugate of the element in the j-th row and i-th column, for all indices i and j.
	 * Hermitian matrices can be understood as the complex extension of real symmetric matrices.
	 * @return The Hermitian matrix.
	 */
	public MatrixComplex hermitian() {
		return (this.plus(this.adjoint()));
	}

	/**
	 * Calculates the skew-Hermitian or antihermitian.
	 * The skew-Hermitian or antihermitian if its conjugate transpose is equal to the original matrix, with all the entries being of opposite sign.[1] That is, the matrix A is skew-Hermitian if it satisfies the relation.
	 * @return The skew-Hermitian matrix.
	 */
	public MatrixComplex skewHermitian() {
		return (this.minus(this.adjoint()));
	}

	/**
	 * Calculates the commutator between two arrays.
	 * The commutator gives an indication of the extent to which a certain binary operation fails to be commutative.
	 * @param B the second array.
	 * @return The commutator array.
	 */
	public MatrixComplex commutator(MatrixComplex B) {
		return (this.times(B)).minus(B.times(this));
	}

	/**
	 * Calculates the anticommutator between two arrays.
	 * @param B the second array.
	 * @return The anticommutator array.
	 */
	public MatrixComplex anticommutator(MatrixComplex B) {
		return (this.times(B)).plus(B.times(this));
	}

	/*
	 * ***********************************************
	 * LINE EQUATION
	 * ***********************************************
	 */

	/**
	 * Calculates the General Form equation A0*x0 + A1*x1 +...+ An-1*xn-1 = An from a point and a normal vector (direction vector for 2 dimensions)
	 * @param point The point of the line or the surface
	 * @param vector The direction vector of the line or the normal vector of the surface
	 * @return The general form equation coefficients A0*x0 + A1*x1 +...+ An-1*xn-1 = An
	 */
	public MatrixComplex pointVector(MatrixComplex point, MatrixComplex vector) {
		int colLen = point.cols();
		int col;
		MatrixComplex lineEq = new MatrixComplex(1, colLen+1);
		for (col = 0; col < colLen; ++col)
			lineEq.complexMatrix[0][col] = vector.complexMatrix[0][col];
		lineEq.complexMatrix[0][col] = point.times(vector.transpose()).complexMatrix[0][0];
		return lineEq;
	}

	/**
	 * Calculates the General Form equation A0*x0 + A1*x1 +...+ An-1*xn-1 = An from a point and a normal vector (direction vector for 2 dimensions)
	 * @param point The point of the line or the surface as a string separated by commas
	 * @param vector The direction vector of the line or the normal vector of the surface as a string separated by commas
	 * @return The general form equation coefficients A0*x0 + A1*x1 +...+ An-1*xn-1 = An
	 */
	public MatrixComplex pointVector(String point, String vector) {
		MatrixComplex aPoint = new MatrixComplex();
		MatrixComplex aVector = new MatrixComplex();		

		aPoint = new MatrixComplex(point);
		aVector = new MatrixComplex(vector);
		return this.pointVector(aPoint, aVector);
	}

	/**
	 * Calculates the distance between a line or a surface expressed in general form and a point
	 * @param point expressed as a matrix complex
	 * @return the distance
	 */
	public double distance(MatrixComplex point) {
		Complex d1 = new Complex();
		int col;

		for (col = 0; col < point.cols(); ++col)
			d1 = d1.plus(this.complexMatrix[0][col].times(point.complexMatrix[0][col]));
		d1 = d1.minus(this.complexMatrix[0][col]);
		return d1.mod()/this.coefMatrix().norm();
	}

	/**
	 * Calculates the distance between a line or a surface expressed in general form and a point
	 * @param spoint The point expressed as a string separated by commas
	 * @return the distance
	 */
	public double distance(String spoint) {
		MatrixComplex point = new MatrixComplex(spoint);
		return this.distance(point);
	}

	/*
	 * ***********************************************
	 * ELEMENTARY ROW TRANSFORMATIONS
	 * ***********************************************
	 */

	/**
	 * Transformation FSwapff(i,j) it swaps rows i and j of this matrix A ∈ C m × n and returns the result into a new Matrix.
	 * @param rowi Index of row i.
	 * @param rowj Index of row j.
	 * @return The transformed matrix.
	 */
	public MatrixComplex FSwapff(int rowi, int rowj) {
		int rowLen = this.rows();
		MatrixComplex Ftrans = new MatrixComplex(rowLen);
		MatrixComplex pivot = new MatrixComplex(1, rowLen);

		Ftrans.initMatrixDiag(1, 0);
		pivot.complexMatrix[0] = Ftrans.complexMatrix[rowi];
		Ftrans.complexMatrix[rowi] = Ftrans.complexMatrix[rowj];
		Ftrans.complexMatrix[rowj] = pivot.complexMatrix[0];
		Ftrans = Ftrans.times(this);
		return Ftrans;
	}

	/**
	 * Transformation FSwapf(i,j) it swaps rows i and j of this matrix A ∈ C m × n.
	 * @param rowi Index of row i.
	 * @param rowj Index of row j.
	 */
	public void FSwapf(int rowi, int rowj) {
		int rowLen = this.rows();
		MatrixComplex pivot = new MatrixComplex(1, rowLen);

		pivot.complexMatrix[0] = this.complexMatrix[rowi];
		this.complexMatrix[rowi] = this.complexMatrix[rowj];
		this.complexMatrix[rowj] = pivot.complexMatrix[0];
		this.mSign *= -1;
	}

	/**
	 * Transformation Ftransff(i,α) multiplies row i of this matrix A ∈ C m × n by a number α and returns the result into a new Matrix.
	 * @param row Index of row i.
	 * @param cNum The complex number α.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int row, Complex cNum) {
		int rowLen = this.rows();
		MatrixComplex Ftrans = new MatrixComplex(rowLen);

		Ftrans.initMatrixDiag(1, 0);
		Ftrans.setItem(row, row, cNum);
		Ftrans = Ftrans.times(this);
		return Ftrans;
	}

	/**
	 * Transformation Ftransff(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α in text format and returns the result into a new Matrix.
	 * @param row The index of row i.
	 * @param sNum The complex number α in text format.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int row, String sNum) {
		Complex cNum = new Complex(sNum);
		return this.Ftransff(row, cNum);
	}

	/**
	 * Transformation Ftransff(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α and returns the result into a new Matrix.
	 * @param row The index of row i.
	 * @param dNum The number α.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int row, double dNum) {
		return Ftransff(row, new Complex(dNum,0));
	}

	/**
	 * Transformation Ftransf(i,α) multiplies row i of this matrix A ∈ C m × n by a number α.
	 * @param row Index of row i.
	 * @param cNum The complex number α.
	 */
	public void Ftransf(int row, Complex cNum) {
		int colLen = this.cols();
		
		for(int col = 0; col < colLen; ++col)
			this.setItem(row, col, this.getItem(row, col).times(cNum));
	}

	/**
	 * Transformation Ftransf(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α in text format.
	 * @param row The index of row i.
	 * @param sNum The complex number α in text format.
	 */
	public void Ftransf(int row, String sNum) {
		Complex cNum = new Complex(sNum);
		this.Ftransf(row, cNum);
	}

	/**
	 * Transformation Ftransf(i,"α") Multiplies the row i of a matrix A ∈ C m × n by a number α in text format.
	 * @param row The index of row i.
	 * @param dNum The number α.
	 */
	public void Ftransf(int row, double dNum) {
		Ftransf(row, new Complex(dNum,0));
	}

	/**
	 * Transformation Ftransff(i,j,α) Adds to row i of a matrix A ∈ C m × n its row j multiplied by the complex α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param cNum The complex number α.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int rowi, int rowj, Complex cNum) {
		int rowLen = this.rows();
		MatrixComplex Ftrans = new MatrixComplex(rowLen);

		Ftrans.initMatrixDiag(1, 0);
		Ftrans.setItem(rowi,rowj,cNum);
		Ftrans = Ftrans.times(this);
		return Ftrans;
	}

	/**
	 * Transformation Ftransff(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α != 0 in string format.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param sNum The complex number α in text format.
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int rowi, int rowj, String sNum) {
		Complex cNum = new Complex(sNum);    	
		return this.Ftransff(rowi, rowj, cNum);
	}    

	/**
	 * Transformation Ftransff(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param dNum The number α. 
	 * @return The transformed matrix.
	 */
	public MatrixComplex Ftransff(int rowi, int rowj, double dNum) {
		return this.Ftransff(rowi, rowj, new Complex(dNum, 0));
	}

	/**
	 * Transformation Ftransf(i,j,α) Adds to row i of a matrix A ∈ C m × n its row j multiplied by the complex α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param cNum The complex number α.
	 */
	public void Ftransf(int rowi, int rowj, Complex cNum) {
		int colLen = this.cols();
		
		for (int col = 0; col < colLen; ++col)
			this.setItem(rowi, col, this.getItem(rowi, col).plus(this.getItem(rowj, col).times(cNum)));
	}

	/**
	 * Transformation Ftransf(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α in string format.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param sNum The complex number α in text format.
	 */
	public void Ftransf(int rowi, int rowj, String sNum) {
		Complex cNum = new Complex(sNum);    	
		this.Ftransf(rowi, rowj, cNum);
	}    

	/**
	 * Transformation F(i,j,"α") Adds to row i of a matrix A ∈ C m × n its row j multiplied by α.
	 * @param rowi The index of row i.
	 * @param rowj The index of row j.
	 * @param dNum The number α
	 */
	public void Ftransf(int rowi, int rowj, double dNum) {
		this.Ftransf(rowi, rowj, new Complex(dNum, 0));
	}
	
	/*
	 * ***********************************************
	 * GENERAL PORPOUSE METHODS
	 * ***********************************************
	 */



}
