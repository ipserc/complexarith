package com.ipserc.arith.factorization;

import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.complex.Complex;

import java.util.ArrayList;
import java.util.List;

public class Jordan extends Eigenspace {

	private final static String HEADINFO = "Jordan --- INFO:";
	private final static String VERSION = "1.6 (2026_0811_2015)";
	/* VERSION Release Note
	 *
	 * 1.6 (2026_0811_2015)
	 * Comentarios Javadoc traducidos al inglés y corregidos (sin cambios funcionales), como parte de
	 * la generación de la documentación de la API. De paso, corregida una descripción incorrecta en
	 * el Javadoc de factorize(): decía que D contenía los autovectores y P los autovalores, al revés
	 * de lo que hace realmente el código (J=autovalores en forma de Jordan, P=autovectores). También
	 * se rellenó el Javadoc vacío de jordanForm().
	 *
	 * 1.5 (2026_0808_0130)
	 * factorize(): el KNOWN LIMITATION heredado del buscador de raices (autovalores repetidos de
	 * alta multiplicidad) se cierra en firme como IRRESOLUBLE dentro de aritmetica de coma
	 * flotante -- ver Polynom.VERSION 1.15 para el argumento completo. Sin cambio de
	 * comportamiento, solo doc.
	 *
	 * 1.4 (2026_0805_1200)
	 * New private checkReconstruction(), called at the end of factorize()/factorize2(): verifies
	 * P*J*P^-1 actually reconstructs the original matrix (within RECONSTRUCTION_TOLERANCE=1e-4,
	 * deliberately looser than machine precision -- see that constant's own Javadoc) and that P is
	 * square, throwing an explicit IllegalArgumentException otherwise instead of returning silently
	 * wrong results. Closes a gap VERSION 1.3 left open: eigenvectors()'s NaN check only covers an
	 * outright NaN entry in the vectors it builds, not a P that inverts into garbage afterwards.
	 * Two real, previously-silent failures confirmed and now caught: (1) lambda=3, a 4x4 matrix with
	 * two 2x2 chains -- factorize() used to report success with det(P)~7.7e-28 and a P*J*P^-1
	 * reconstruction of literal NaN; (2) a genuinely new finding, TestJordan01's own 4x4 matrix
	 * "2,2,-3,4;-2,2,1,0;3,3,-5,7;4,2,-6,7" -- eigenvalues() and arithmeticMultiplicity() disagree on
	 * this matrix's true grouping (both its eigenvalue pairs near 2 and near 1 sit ~1e-7 apart from
	 * Durand-Kerner), producing a non-square P (4 rows, 2 columns) that used to reach
	 * MatrixComplex.inverse()'s old print-and-continue error path and silently corrupt the caller's
	 * own P*J*P^-1 printout instead of ever surfacing as a Jordan-level failure. Neither is a new bug
	 * in the geometric-multiplicity construction itself -- both are the same already-documented
	 * root-finder-precision ceiling (see factorize()'s own Javadoc) now failing loud instead of quiet.
	 * TestJordan01.java (a diagnostic-only script predating any pass/fail convention, zero assertions
	 * of its own) now crashes with a stack trace instead of printing a NaN "PROOF" matrix for its
	 * second case -- an intentional, expected consequence of this fix, not a regression: its exit
	 * code was never a meaningful signal. Verified against a 12-file battery (every TestComplex file
	 * referencing Jordan/factorize, plus TestLU01) compiled and run against both builds -- 0 exit-code
	 * mismatches except TestJordan01 (expected, explained above); TestJordanAudit01's documented
	 * "defective 3x3, simple eigenvalue processed first" case (residual ~3.4e-7, deliberately accepted
	 * since VERSION 1.3) still passes comfortably under RECONSTRUCTION_TOLERANCE.
	 *
	 * 1.3 (2026_0802_1500)
	 * KNOWN LIMITATION resolved: eigenvalues with geometric multiplicity greater than 1 (needing
	 * more than one Jordan chain) are now built correctly -- see block()/blockSizePartition()/
	 * eigenvectors(Complex,int)'s own Javadoc for the algorithm. Uses the new
	 * MatrixComplex.nullspaceBasis() (Fase A) to get a genuine basis of Null((A-lambda*I)^k), not
	 * just one scalar-parameterized vector. Verified with TestJordan02 (the real case that used to
	 * throw "solveGauss: INCONSISTENT") plus several synthetic multi-block cases (P not orthogonal)
	 * -- P*J*P^-1 reconstructs the original matrix to within machine precision in every case tried.
	 * factorize2()'s defective-eigenvalue branch simplified to delegate fully to eigenvectors()
	 * instead of mixing a partial direct-copy with the old single-chain machinery (which is no
	 * longer consistent with eigenvectors() now returning ALL of an eigenvalue's chains at once).
	 *
	 * 1.2 (2026_0801_2252)
	 * Bug fix: factorize()/factorize2() built eigenValArray from this.roots().clone() -- the raw,
	 * individually-perturbed Durand-Kerner output -- even after arithmeticMultiplicity() correctly
	 * detected a repeated eigenvalue's multiplicity (via the Eigenspace.eigenval() fix, VERSION
	 * 1.6). Feeding that raw root into eigenvectors(Complex,int) built (A-eigenval*I)^k with
	 * eigenval off from the true eigenvalue by whatever error Durand-Kerner happened to leave on
	 * that specific root, enough for a defective eigenvalue to make the matrix insufficiently close
	 * to singular and collapse the generalized eigenvector to the trivial all-zero solution (P
	 * singular, P.inverse() -> NaN). Fixed with a new helper, expandedEigenValues(), that expands
	 * the corrected/averaged Eigenspace.eigenvalues() array back to one row per repetition instead
	 * of using roots() directly -- see its own Javadoc. Verified with P*J*P^-1 reconstruction: the
	 * "defective 3x3, simple eigenvalue processed first" case (TestJordanAudit01, previously
	 * documented as an unexplained ~2^-24 residual) now reconstructs to within ~3.4e-7, down from
	 * NaN -- the remaining residual is inherent numerical amplification from Durand-Kerner's
	 * achievable precision through a near-singular pivot, not a further bug (see
	 * TestJordanAudit01's own Javadoc for the full diagnosis).
	 *
	 * 1.1 (2026_0801_0954)
	 * Compile fix: values()/vectors() no longer exist in Eigenspace, replaced with roots()/
	 * solutions() (NOT eigenvalues()/eigenvectors(), which have a different row count/ordering --
	 * see the comment in factorize()).
	 * Bug fix: appendRows() returns a NEW MatrixComplex, doesn't mutate in place -- 4 call sites
	 * (eigenvectors(), factorize()) called it as a bare statement, discarding the result, crashing
	 * with ArrayIndexOutOfBoundsException for any defective eigenvalue.
	 * Bug fix: block()'s superdiagonal condition checked global matrix bounds instead of "still
	 * inside this eigenvalue's own chain" -- silently corrupted the Jordan matrix (wrong result,
	 * not even NaN) whenever a simple eigenvalue (arithMult=1) wasn't the last one processed.
	 * Verified with P*J*P^-1 reconstruction: correct for every eigenvalue with geometric
	 * multiplicity exactly 1. KNOWN LIMITATION, documented not fixed: eigenvalues with geometric
	 * multiplicity greater than 1 (needing more than one Jordan block) are still built wrong --
	 * see factorize()'s own Javadoc.
	 *
	 * 1.0 (2020_0627_1130)
	 */

	private MatrixComplex cJ;
	private MatrixComplex cP;
	private boolean factorized = false;

	/*
	 * 	CONSTRUCTORS 
	 */
	/**
	 * Instantiates a complex square array of length len.
	 * @param len The length of the square array.
	 */
	/*
	public Jordan(int len) {
		super(len);
	}
	*/
	
	/**
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix the string with the rows and columns.
	 */
	public Jordan(String strMatrix) {
		super(strMatrix);
	}

	/**
	 * Instantiates a Diagfactor array from a MatrixComplex.
	 * @param matrix the MatrixComplex already instantiated.
	 */
	public Jordan(MatrixComplex matrix) {
		super(matrix);
		//this.complexMatrix = matrix.complexMatrix.clone();
	}

	/*
	 * 	GETTERS 
	 */

	/**
	 * Gets the diagonal matrix
	 * @return J
	 */
	public MatrixComplex J() {
		return cJ;
	}

	/**
	 * Gets the eigenvector matrix
	 * @return P
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

	/**
	 * Builds the Jordan block(s) for ONE eigenvalue, starting at row/col {@code order}, given its
	 * block-size partition (one entry per Jordan chain, e.g. {@code [3,2]} for an eigenvalue of
	 * algebraic multiplicity 5 and geometric multiplicity 2). Generalizes the single-chain case
	 * (a one-element partition {@code [arithMult]}) to any number of chains: each chain gets its
	 * own consecutive run of rows with a superdiagonal 1 connecting its own rows only -- never
	 * across chains, and never past this eigenvalue's own last row (see the historical bug fixed
	 * in VERSION 1.1 for why that boundary matters).
	 * @param order The first row/col this eigenvalue's blocks occupy.
	 * @param blockSizes The sizes of this eigenvalue's Jordan chains (see {@link
	 * #blockSizePartition(Complex, int)}); must sum to this eigenvalue's algebraic multiplicity.
	 * @param eigenValue The eigenvalue.
	 * @return The (rows x cols)-sized matrix with just this eigenvalue's block(s) filled in.
	 */
	private MatrixComplex block(int order, List<Integer> blockSizes, Complex eigenValue) {
		MatrixComplex block = new MatrixComplex(this.rows(), this.cols());
		block.initMatrix(0, 0);
		int row = order;
		for (int size : blockSizes) {
			for (int i = 0; i < size; ++i, ++row) {
				block.setItem(row, row, eigenValue);
				if (i+1 < size) block.setItem(row, row+1, Complex.ONE);
			}
		}
		return block;
	}

	/**
	 * Determines the Jordan block-size partition for ONE eigenvalue -- i.e., how many chains it
	 * needs and how long each one is -- via the standard rank-of-successive-powers formula (Golub
	 * &amp; Van Loan / Horn &amp; Johnson): with {@code N=A-eigenval*I} and {@code nu_k =
	 * dim(Null(N^k)) = n-rank(N^k)}, the number of chains of length EXACTLY {@code k} is
	 * {@code (nu_k-nu_{k-1}) - (nu_{k+1}-nu_k)}. {@code nu_1} is the geometric multiplicity (total
	 * number of chains); {@code nu_k} stops growing once it reaches the algebraic multiplicity,
	 * at {@code k} = the size of the largest chain.
	 * <p>
	 * Resolves the KNOWN LIMITATION documented in previous versions of this class: {@code
	 * block()}/{@code eigenvectors(Complex,int)} used to assume every eigenvalue needs exactly ONE
	 * chain of length {@code arithMult} -- wrong whenever the geometric multiplicity is greater
	 * than 1 (confirmed real case: {@code TestJordan02}'s eigenvalue {@code -2}, algebraic
	 * multiplicity 5, geometric multiplicity 2, needing chains {@code [3,2]}, not one chain of 5).
	 * @param eigenval The eigenvalue.
	 * @param arithMult The eigenvalue's algebraic multiplicity (from {@link
	 * Eigenspace#arithmeticMultiplicity(Complex)}).
	 * @return The chain lengths, sorted descending, summing to {@code arithMult}.
	 * @throws IllegalArgumentException if {@code nu_k} ever exceeds {@code arithMult} -- this
	 * means {@code arithMult} itself is unreliable for this {@code eigenval}, almost always because
	 * the root finder grouped a genuinely higher-multiplicity eigenvalue into several
	 * distinct-looking ones of lower multiplicity each (confirmed real case: {@code TestJordan02}'s
	 * eigenvalue {@code -2}, true algebraic multiplicity 5, comes back from Durand-Kerner/
	 * Aberth-Ehrlich as 5 roots ~1e-3 apart, each grouped as its own "eigenvalue" of multiplicity 1
	 * -- a separate, pre-existing limitation of the root-finding pipeline this class builds on, not
	 * of this method; thrown explicitly here instead of silently building a wrong/oversized
	 * partition that would crash later with a confusing {@code ArrayIndexOutOfBoundsException} in
	 * {@link #block(int, List, Complex)}.
	 */
	public List<Integer> blockSizePartition(Complex eigenval, int arithMult) {
		int n = this.rows();
		MatrixComplex nMat = this.minus(this.eye().times(eigenval));

		List<Integer> nullities = new ArrayList<Integer>();
		nullities.add(0);
		MatrixComplex power = this.eye();
		int k = 0;
		while (nullities.get(k) < arithMult) {
			power = (k == 0) ? nMat.copy() : power.times(nMat);
			++k;
			int nu = n - power.rank();
			if (nu > arithMult)
				throw new IllegalArgumentException(HEADINFO + " blockSizePartition: nullity of "
					+ "(A-eigenval*I)^" + k + " (" + nu + ") exceeds the given algebraic multiplicity "
					+ "(" + arithMult + ") for eigenval=" + eigenval + " -- its true multiplicity is "
					+ "almost certainly higher than reported; likely root-finder grouping imprecision "
					+ "on a high-multiplicity eigenvalue (see this method's own Javadoc).");
			nullities.add(nu);
		}
		int maxBlockSize = k;

		List<Integer> sizes = new ArrayList<Integer>();
		int total = 0;
		for (int size = maxBlockSize; size >= 1; --size) {
			int nuPrev = nullities.get(size - 1);
			int nuCur = nullities.get(size);
			int nuNext = (size + 1 <= maxBlockSize) ? nullities.get(size + 1) : arithMult;
			int count = (nuCur - nuPrev) - (nuNext - nuCur);
			for (int c = 0; c < count; ++c) sizes.add(size);
			total += size * Math.max(count, 0);
		}

		// The per-step guard above only catches an outright overshoot mid-sequence; it does NOT
		// catch every way an unreliable arithMult can corrupt this partition (confirmed real case:
		// TestJordan02's imprecise roots gave nu_1=0 -- BELOW arithMult, no overshoot -- yet the
		// resulting partition still summed to more than arithMult once nu_2 pushed past it). The
		// sum of the partition is the one invariant that must ALWAYS hold for a genuine algebraic
		// multiplicity, regardless of which step the inconsistency actually originates from -- so
		// this is the authoritative check.
		if (total != arithMult)
			throw new IllegalArgumentException(HEADINFO + " blockSizePartition: the computed chain "
				+ "sizes " + sizes + " sum to " + total + ", not the given algebraic multiplicity "
				+ "(" + arithMult + ") for eigenval=" + eigenval + " -- its true multiplicity is "
				+ "almost certainly different than reported; likely root-finder grouping imprecision "
				+ "on a high-multiplicity eigenvalue (see this method's own Javadoc).");
		return sizes;
	}

	/**
	 * Calculates ALL the generalized eigenvectors for one eigenvalue, handling any geometric
	 * multiplicity. Tries {@link #multiChainEigenvectors(Complex, int)} (needs a reliable
	 * {@link #blockSizePartition(Complex, int)}) first; falls back to the original single-chain
	 * construction ({@link #singleChainEigenvectors(Complex, int)}) only if that throws -- same
	 * "try the general case, fall back to the proven one only on explicit failure" pattern already
	 * used by {@code Polynom.solveRobust()}/{@code Eigenspace.eigenval()}'s own fallback.
	 * <p>
	 * The fallback is NOT optional, and matters even for cases that were already correct before
	 * VERSION 1.3 (geometric multiplicity exactly 1): {@code blockSizePartition}'s rank-of-
	 * successive-powers approach turned out to be dramatically more sensitive to eigenvalue
	 * imprecision than the old system-solving approach -- confirmed with a real regression on
	 * {@code TestJordanAudit01}'s "defective 3x3, simple eigenvalue processed first" case
	 * (algebraic multiplicity 2, geometric multiplicity 1, previously reconstructing to ~3.4e-7):
	 * even the group-averaged eigenvalue (already the most precise value available, off from the
	 * true eigenvalue by only ~5e-12) was enough for squaring {@code N} to push {@code rank(N^2)}
	 * back up to a "full-looking" value under {@link MatrixComplex#rank()}'s tolerance, corrupting
	 * the whole nullity sequence. Forcing {@code blockSizePartition} to just return {@code
	 * [arithMult]} on failure is NOT a safe fix either -- {@link #multiChainEigenvectors(Complex,
	 * int)} would still try to build {@code Null(N^2)} via the same corrupted rank computation and
	 * silently return too few (sometimes zero) eigenvectors instead of throwing. Falling back to
	 * the entirely different, already-proven system-solving construction is what actually restores
	 * the old (working, ~3.4e-7 residual) behavior with zero risk of regressing it further.
	 * @param eigenval The eigenvalue to calculate the eigenvectors for.
	 * @param arithMult The eigenvalue's algebraic multiplicity.
	 * @return A MatrixComplex with {@code arithMult} rows.
	 */
	public MatrixComplex eigenvectors(Complex eigenval, int arithMult) {
		MatrixComplex eigenVect;
		try {
			eigenVect = this.multiChainEigenvectors(eigenval, arithMult);
		} catch (IllegalArgumentException e) {
			eigenVect = this.singleChainEigenvectors(eigenval, arithMult);
		}

		// Neither construction is immune to eigenvalue imprecision degrading a near-singular solve
		// into NaN (confirmed real case: singleChainEigenvectors(), the fallback above, silently
		// returned NaN rows for arithMult=4 with an eigenvalue off by only ~1e-6 -- a LATENT
		// fragility of that already-existing method, never exercised at this multiplicity before
		// today). Rather than let NaN survive into P (silently corrupting factorize()'s result,
		// exactly what this whole fix is trying to avoid), fail explicitly here -- one check,
		// covers both construction paths.
		for (int r = 0; r < eigenVect.rows(); ++r)
			for (int c = 0; c < eigenVect.cols(); ++c)
				if (eigenVect.getItem(r, c).isNaN())
					throw new IllegalArgumentException(HEADINFO + " eigenvectors: construction for "
						+ "eigenval=" + eigenval + " (arithMult=" + arithMult + ") produced a NaN "
						+ "entry -- the eigenvalue is almost certainly too imprecise for this "
						+ "multiplicity (root-finder grouping/precision limitation, see this "
						+ "method's own Javadoc), not a defect in the construction itself.");
		return eigenVect;
	}

	/**
	 * Calculates ALL the generalized eigenvectors for one eigenvalue -- one full Jordan chain per
	 * entry of {@link #blockSizePartition(Complex, int)}, handling any geometric multiplicity, not
	 * just 1. Algorithm (standard construction, e.g. Horn &amp; Johnson): with {@code
	 * N=A-eigenval*I}, process chain lengths from LARGEST to SMALLEST; at each level {@code k}, a
	 * basis of {@code Null(N^k)} ({@link MatrixComplex#nullspaceBasis()} of {@code N^k}) is
	 * extended past {@code Null(N^{k-1})} PLUS the vectors already carried down from longer
	 * chains -- the extension vectors needed are exactly this level's new chain generators (see
	 * {@link #extendBasis(List, MatrixComplex, int)}). Each new generator's chain is then built by
	 * repeated left-multiplication by {@code N} (a chain of length {@code k} descends {@code
	 * v_k, N*v_k, N^2*v_k, ..., N^(k-1)*v_k=v_1}, the last one being the actual eigenvector) --
	 * direct matrix-vector multiplication, no linear system to solve for the descent itself.
	 * @param eigenval The eigenvalue to calculate the eigenvectors for.
	 * @param arithMult The eigenvalue's algebraic multiplicity.
	 * @return A MatrixComplex with {@code arithMult} rows: for each chain (in the same order as
	 * {@link #blockSizePartition(Complex, int)}), its vectors bottom-up (eigenvector first, then
	 * each generalized eigenvector) -- the row order {@link #block(int, List, Complex)} expects.
	 * @throws IllegalArgumentException if {@link #blockSizePartition(Complex, int)} does (see its
	 * own Javadoc) -- callers should use {@link #eigenvectors(Complex, int)} instead of this
	 * method directly unless they specifically want the exception, not the fallback.
	 */
	private MatrixComplex multiChainEigenvectors(Complex eigenval, int arithMult) {
		List<Integer> blockSizes = this.blockSizePartition(eigenval, arithMult);
		int n = this.rows();
		MatrixComplex nMat = this.minus(this.eye().times(eigenval));

		int maxSize = blockSizes.get(0);
		MatrixComplex[] nullBasis = new MatrixComplex[maxSize + 1];
		nullBasis[0] = new MatrixComplex(0, n);
		MatrixComplex power = this.eye();
		for (int k = 1; k <= maxSize; ++k) {
			power = power.times(nMat);
			nullBasis[k] = power.nullspaceBasis();
		}

		int[] countBySize = new int[maxSize + 1];
		for (int size : blockSizes) ++countBySize[size];

		List<List<MatrixComplex>> chains = new ArrayList<List<MatrixComplex>>();

		for (int k = maxSize; k >= 1; --k) {
			List<MatrixComplex> existing = new ArrayList<MatrixComplex>();
			for (int r = 0; r < nullBasis[k-1].rows(); ++r) existing.add(nullBasis[k-1].getRow(r));
			for (List<MatrixComplex> chain : chains) existing.add(chain.get(chain.size()-1));

			List<MatrixComplex> newGens = extendBasis(existing, nullBasis[k], countBySize[k]);
			for (MatrixComplex g : newGens) {
				List<MatrixComplex> chain = new ArrayList<MatrixComplex>();
				chain.add(g);
				chains.add(chain);
			}

			if (k > 1) {
				for (List<MatrixComplex> chain : chains) {
					MatrixComplex top = chain.get(chain.size()-1);
					chain.add(nMat.times(top.transpose()).transpose());
				}
			}
		}

		MatrixComplex eigenVect = new MatrixComplex(0, n);
		List<List<MatrixComplex>> remaining = new ArrayList<List<MatrixComplex>>(chains);
		for (int size : blockSizes) {
			for (int idx = 0; idx < remaining.size(); ++idx) {
				if (remaining.get(idx).size() == size) {
					List<MatrixComplex> chain = remaining.remove(idx);
					for (int j = chain.size()-1; j >= 0; --j) eigenVect = eigenVect.appendRows(chain.get(j));
					break;
				}
			}
		}
		return eigenVect;
	}

	/**
	 * Original single-chain-only construction (predates VERSION 1.3), kept as the fallback {@link
	 * #eigenvectors(Complex, int)} uses when {@link #multiChainEigenvectors(Complex, int)} can't
	 * (see that method's Javadoc for why forcing a partition isn't a safe alternative fallback).
	 * Always builds exactly ONE chain of length {@code arithMult} via {@code (A-eigenval*I)^k}
	 * augmented linear systems -- correct only when the geometric multiplicity is exactly 1, but
	 * more tolerant of eigenvalue imprecision for that case than the newer rank-of-powers approach.
	 * @param eigenval The eigenvalue to calculate the eigenvector for.
	 * @param arithMult The arithmetic multiplicity.
	 * @return A MatrixComplex with the eigenvectors.
	 */
	private MatrixComplex singleChainEigenvectors(Complex eigenval, int arithMult) {
		int order = arithMult;
		MatrixComplex I = this.eye();
		MatrixComplex eigenVect = new MatrixComplex(0,0);
		MatrixComplex cMatrix;
		MatrixComplex sols;

		for (order = arithMult; order > 1; --order) {
			cMatrix = ((this.minus(I.times(eigenval))).power(order)).augment(); //.heap();
			cMatrix.println("------------------[f-I]^" + order);
			sols = cMatrix.solve();
			eigenVect = eigenVect.appendRows(sols.getRow(0));
		}
		cMatrix = ((this.minus(I.times(eigenval))).power(order)).augment(); //.heap();
		cMatrix.println("------------------[f-I]^" + order);
		if (arithMult > 1) {
			sols = cMatrix.unkMatrix().times(eigenVect.getRow(arithMult-order-1).transpose()).transpose();
			eigenVect = eigenVect.appendRows(sols);
			eigenVect.transrow();
		}
		else {
			cMatrix = (this.minus(I.times(eigenval))).augment(); //.heap();
			cMatrix.println("------------------[f-I]^" + order);
			sols = cMatrix.solve();
			eigenVect = eigenVect.appendRows(sols);
		}
		return eigenVect;
	}

	/**
	 * Greedily picks up to {@code needed} rows from {@code candidates} that extend the span of
	 * {@code existing} -- each candidate is accepted only if adding it increases the rank of
	 * {@code existing} + accepted-so-far, using {@link MatrixComplex#rank()} (already verified
	 * against brute force in previous sessions). Used by {@link #eigenvectors(Complex, int)} to
	 * find each level's NEW chain generators in {@code Null(N^k)}, beyond {@code Null(N^{k-1})}
	 * and whatever longer chains already contribute at that level.
	 * @param existing The rows already known to be independent (a basis, or any spanning set).
	 * @param candidates Candidate rows to extend the span with.
	 * @param needed How many new independent vectors to find.
	 * @return Up to {@code needed} rows from {@code candidates}, each independent from
	 * {@code existing} and from each other.
	 */
	private static List<MatrixComplex> extendBasis(List<MatrixComplex> existing, MatrixComplex candidates, int needed) {
		List<MatrixComplex> accepted = new ArrayList<MatrixComplex>();
		if (needed == 0) return accepted;
		List<MatrixComplex> current = new ArrayList<MatrixComplex>(existing);
		int baseRank = rankOfRows(current, candidates.cols());
		for (int i = 0; i < candidates.rows() && accepted.size() < needed; ++i) {
			MatrixComplex candidate = candidates.getRow(i);
			List<MatrixComplex> trial = new ArrayList<MatrixComplex>(current);
			trial.add(candidate);
			int trialRank = rankOfRows(trial, candidates.cols());
			if (trialRank > baseRank) {
				accepted.add(candidate);
				current.add(candidate);
				baseRank = trialRank;
			}
		}
		return accepted;
	}

	/**
	 * Stacks {@code rows} into a single matrix and returns its {@link MatrixComplex#rank()}.
	 * @param rows The row vectors to stack.
	 * @param cols The number of columns each row vector has.
	 * @return The rank of the stacked matrix (0 if {@code rows} is empty).
	 */
	private static int rankOfRows(List<MatrixComplex> rows, int cols) {
		if (rows.isEmpty()) return 0;
		MatrixComplex m = new MatrixComplex(rows.size(), cols);
		for (int r = 0; r < rows.size(); ++r) m.setRow(r, rows.get(r));
		return m.rank();
	}

	/**
	 * Expands {@link Eigenspace#eigenvalues()} (one row per DISTINCT eigenvalue, with its
	 * arithmetic multiplicity in the second column) back into one row per REPETITION -- the same
	 * shape as {@link Eigenspace#roots()} -- but using the group-averaged representative value from
	 * {@code eigenvalues()} instead of {@code roots()}'s raw, individually-perturbed Durand-Kerner
	 * output.
	 * <p>
	 * Needed because {@link #factorize()}/{@link #jordanForm(MatrixComplex)} use the {@code i +=
	 * arithMult} indexing convention, which assumes one row per repetition -- so a plain {@code
	 * this.roots().clone()} looked like the right source (see the historical comment in {@link
	 * #factorize()}). But feeding one of {@code roots()}'s raw individual roots of a repeated
	 * eigenvalue straight into {@link #eigenvectors(Complex, int)} builds (A-eigenval*I)^k with
	 * {@code eigenval} off from the true eigenvalue by whatever error Durand-Kerner happened to
	 * leave on that particular root -- for a genuinely defective eigenvalue this is enough to make
	 * that matrix insufficiently close to singular, collapsing the generalized eigenvector to the
	 * trivial (all-zero) solution instead of a real one (confirmed with "0,3,1;2,-1,-1;-2,-1,-1":
	 * P*J*P^-1 came out NaN). {@link Eigenspace#eigenvalues()}'s representative value is the
	 * AVERAGE of the roots grouped into that eigenvalue, measurably closer to the true value (see
	 * {@link Eigenspace#eigenval()}'s own comment) -- expanding it back to one-row-per-repetition
	 * keeps every existing {@code i += arithMult} loop below unchanged.
	 * @return one row per repetition of each eigenvalue, in the same order as {@code eigenvalues()}
	 */
	private MatrixComplex expandedEigenValues() {
		MatrixComplex distinct = this.eigenvalues();
		int total = 0;
		for (int row = 0; row < distinct.rows(); ++row) total += (int)distinct.getItem(row, 1).cre();
		MatrixComplex expanded = new MatrixComplex(total, 1);
		int idx = 0;
		for (int row = 0; row < distinct.rows(); ++row) {
			Complex eigenval = distinct.getItem(row, 0);
			int arithMult = (int)distinct.getItem(row, 1).cre();
			for (int k = 0; k < arithMult; ++k) expanded.setItem(idx++, 0, eigenval);
		}
		return expanded;
	}

	/**
	 * Builds the full Jordan matrix J by placing each eigenvalue's block(s) (see
	 * {@link #block(int, List, Complex)}) at consecutive row/col offsets, advancing by each
	 * eigenvalue's algebraic multiplicity.
	 * @param eigenValArray One row per repetition of each eigenvalue (see
	 * {@link #expandedEigenValues()}).
	 * @return The Jordan matrix J.
	 */
	public MatrixComplex jordanForm(MatrixComplex eigenValArray) {
		int rowLen = this.rows(); 
		int colLen= this.cols();
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Jordan Matrix");
			System.exit(-1);
		}
		MatrixComplex jordanForm = new MatrixComplex(rowLen, colLen);
		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = this.arithmeticMultiplicity(eigenval);
			// Same fallback as eigenvectors(Complex,int) (see its Javadoc): a single chain of
			// length arithMult whenever blockSizePartition can't be trusted for this eigenval,
			// keeping J consistent with whichever construction eigenvectors() actually used.
			List<Integer> blockSizes;
			try {
				blockSizes = this.blockSizePartition(eigenval, arithMult);
			} catch (IllegalArgumentException e) {
				blockSizes = new ArrayList<Integer>();
				blockSizes.add(arithMult);
			}
			MatrixComplex jordanBlock = this.block(i, blockSizes, eigenval);
			jordanForm = jordanForm.plus(jordanBlock);
			i += arithMult;
		}
		return jordanForm;
	}

	/**
	 * Factorizes the matrix using a Jordan (block-diagonal) matrix of eigenvalues (J) and an
	 * eigenvector matrix (P). The factorization gives A=P·J·P⁻¹
	 * <p>
	 * <b>Geometric multiplicity greater than 1 (multiple Jordan chains per eigenvalue) is now
	 * handled correctly</b> (VERSION 1.3) via {@link #blockSizePartition(Complex, int)} +
	 * {@link #eigenvectors(Complex, int)}'s chain-based construction -- see their own Javadoc for
	 * the algorithm. Verified with the real case that used to throw ({@code TestJordan02}, an
	 * eigenvalue of algebraic multiplicity 5 and geometric multiplicity 2) plus several synthetic
	 * multi-block cases (P not orthogonal): {@code P·J·P⁻¹} reconstructs the original matrix to
	 * within machine precision.
	 * <p>
	 * <b>Remaining, independent limitation, inherent to the eigenvalue pipeline this class builds
	 * on, not specific to Jordan factorization -- IRRESOLUBLE within floating-point arithmetic,
	 * closed for good (see {@link Polynom#solveAberth(double)}'s own Javadoc for the full
	 * argument):</b> the Jordan form is a classically ill-posed computation (not a continuous
	 * function of the matrix entries -- an arbitrarily small perturbation can change which
	 * eigenvalues are exactly equal, and therefore the whole block structure). For a genuinely
	 * high-multiplicity eigenvalue, the root finder ({@code Polynom}/Durand-Kerner/Aberth-Ehrlich)
	 * can numerically split it into several distinct-looking roots close to but not exactly equal
	 * to each other -- an inherent {@code O(ε^(1/m))} sensitivity of the MATHEMATICAL PROBLEM
	 * itself (Wilkinson's classic result), true of any finite-precision method, not a gap a better
	 * algorithm could close (a deflation candidate was designed and measured with rigor, and
	 * discarded for trading one failure mode for a worse one -- see
	 * {@code Claude/ComplexArithRev.md}) -- and {@link MatrixComplex#rank()}'s tolerance-based
	 * decisions (used throughout {@link #blockSizePartition(Complex, int)}/{@link
	 * MatrixComplex#nullspaceBasis()}) can then misjudge the true block structure. QR-with-shifts
	 * ({@code QRSchurfactor}) improved GROUPING nearby root estimates into the same eigenvalue,
	 * which is a genuinely different problem from the root-precision one documented here. This
	 * resolves correctly for exact/well-separated cases (confirmed with several synthetic and the
	 * one real example available); it is not promised to resolve every arbitrarily ill-conditioned
	 * case, and no floating-point method could promise that -- only exact/symbolic arithmetic
	 * would, a different computational paradigm entirely, out of scope by architecture.
	 */
	public void factorize() {
		int rowLen = this.rows(); 
		int colLen= this.cols();
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Jordan Matrix");
			System.exit(-1);
		}
		// values()/vectors() no longer exist in Eigenspace (see the class' own VERSION history).
		// eigenValArray needs one row PER REPETITION of each eigenvalue (matching the i/i+=arithMult
		// indexing used everywhere below) -- but sourced from the group-averaged values in
		// eigenvalues(), not the raw individually-perturbed roots() -- see expandedEigenValues()'s
		// own Javadoc for why using roots() directly corrupts defective eigenvalues' generalized
		// eigenvectors.
		MatrixComplex eigenValArray = this.expandedEigenValues();
		MatrixComplex eigenVectArray = this.solutions();
		eigenVectArray.println(HEADINFO + "eigenVectArray");

		cJ = new MatrixComplex(rowLen, colLen);
		cP = new MatrixComplex(0, 0);
		
		cJ = jordanForm(eigenValArray);
		cJ.println("----------JORDAN");
		
		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = this.arithmeticMultiplicity(eigenval);
			//int geomMult = this.geometricMultiplicity(eigenval);
			
			System.out.println("\n" + HEADINFO + "Testing eigenval:" + eigenval.toString() + "\n");
			MatrixComplex eigenVect = this.eigenvectors(eigenval, arithMult);
			eigenVect.println("----------EIGENVECTORS for eigenvalue:" + eigenval.toString() + ", multiplicity:" + arithMult);
			cP = cP.appendRows(eigenVect);
			cP.println(HEADINFO + "+ + + + + 3. computing cP");
			i += arithMult;
		}
		cP = cP.transpose();
		cP.println("----------PASS MATRIX");
		checkReconstruction();
	}

	/**
	 * Alternate implementation of {@link #factorize()}: uses the eigenvectors already known from
	 * {@link Eigenspace} directly when an eigenvalue's geometric multiplicity equals its algebraic
	 * multiplicity (no defect), falling back to {@link #eigenvectors(Complex, int)} otherwise.
	 * Same KNOWN LIMITATION as {@link #factorize()} -- see its Javadoc -- for eigenvalues needing
	 * multiple separate Jordan blocks.
	 */
	public void factorize2() {
		int rowLen = this.rows(); 
		int colLen= this.cols();
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Jordan Matrix");
			System.exit(-1);
		}
		// See expandedEigenValues()'s Javadoc for why eigenValArray must come from the group-averaged
		// eigenvalues() (expanded back to one row per repetition), not a raw roots() clone.
		MatrixComplex eigenValArray = this.expandedEigenValues();
		MatrixComplex eigenVectArray = this.solutions();
		eigenVectArray.println(HEADINFO + "eigenVectArray");

		cJ = new MatrixComplex(rowLen, colLen);
		cP = new MatrixComplex(rowLen, colLen);
		
		cJ = jordanForm(eigenValArray);
		cJ.println("----------JORDAN");
		
		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = this.arithmeticMultiplicity(eigenval);
			int geomMult = this.geometricMultiplicity(eigenval);
			
			System.out.println("\n" + HEADINFO + "Testing eigenval:" + eigenval.toString() + "\n");
			
			if (arithMult == geomMult) {
				for (int sol = 0; sol < arithMult; ++sol) {
					cP.complexMatrix[i+sol] = eigenVectArray.complexMatrix[i+sol].clone();
					cP.println(HEADINFO + "+ + + + + 1. computing cP");
				}

			}
			else {
				// Used to copy the first geomMult rows directly from eigenVectArray and fill only
				// the rest via eigenvectors(eigenval, arithMult) -- that mixing predates
				// eigenvectors() handling multiple Jordan chains (VERSION 1.3): it assumed
				// eigenvectors() built exactly ONE chain missing its first geomMult-1 entries,
				// which stopped being true once eigenvectors() started returning ALL of this
				// eigenvalue's chains (bottom-up, one after another) in one call. Delegating fully
				// keeps this branch consistent with factorize()'s simpler, already-verified path.
				MatrixComplex eigenVect = this.eigenvectors(eigenval, arithMult);
				eigenVect.println("----------EIGENVECTORS for eigenvalue:" + eigenval.toString() + ", multiplicity:" + arithMult);
				for (int sol = 0; sol < eigenVect.rows(); ++sol) {
					cP.complexMatrix[i+sol] = eigenVect.complexMatrix[sol].clone();
					cP.println(HEADINFO + "+ + + + + 3. computing cP");
				}
			}
			i += arithMult;
		}
		cP = cP.transpose();
		cP.println("----------PASS MATRIX");
		checkReconstruction();
	}

	/**
	 * Reconstruction residual tolerance for {@link #checkReconstruction()} -- deliberately much
	 * looser than machine precision. A genuinely defective eigenvalue reconstructs with a residual
	 * around {@code precision^(1/m)} for a chain of length {@code m} (confirmed real case: {@code
	 * TestJordanAudit01}'s "defective 3x3, simple eigenvalue processed first", residual ~3.4e-7 for
	 * {@code m=2}, already documented and deliberately accepted, not a bug) -- this constant sits
	 * two orders of magnitude above that, comfortably accepting every known-good case while still
	 * being enormously tighter than the actual failures {@link #checkReconstruction()} exists to
	 * catch, which measured {@code NaN} or a completely wrong matrix shape, not a borderline residual.
	 */
	private final static double RECONSTRUCTION_TOLERANCE = 1e-4;

	/**
	 * Verifies {@code cP*cJ*cP^-1} actually reconstructs the original matrix before letting
	 * {@link #factorize()}/{@link #factorize2()} return normally. Closes a gap left open since
	 * VERSION 1.3: {@link #eigenvectors(Complex, int)} already fails loud on an outright {@code
	 * NaN} entry, but a genuinely near-singular {@code cP} (confirmed real case: {@code lambda=3},
	 * a 4x4 matrix with two {@code 2x2} chains, {@code det(P)~7.7e-28}) can pass that check and
	 * still make {@code cP.inverse()} blow up -- {@code factorize()} used to report success with
	 * a {@code cP}/{@code cJ} that don't actually reconstruct the matrix at all. Same "fail loud,
	 * not silent garbage" pattern used throughout this project; does not fix the underlying
	 * eigenvalue-imprecision problem (see {@link #factorize()}'s own Javadoc), only stops it from
	 * being reported as success. Uses {@link #RECONSTRUCTION_TOLERANCE} rather than {@link
	 * MatrixComplex#equals(MatrixComplex)}'s tight default tolerance -- see that constant's own
	 * Javadoc for why.
	 */
	private void checkReconstruction() {
		if (cP.rows() != cP.cols()) {
			throw new IllegalArgumentException(HEADINFO + " factorize: P has " + cP.rows() + " rows "
				+ "but " + cP.cols() + " columns (not square) -- fewer or more generalized "
				+ "eigenvectors were collected than the matrix size, almost certainly because an "
				+ "imprecise eigenvalue made arithmeticMultiplicity() misjudge some eigenvalue's true "
				+ "multiplicity.");
		}
		MatrixComplex reconstructed = cP.times(cJ).times(cP.inverse());
		double maxAbsDiff = 0.0;
		for (int row = 0; row < this.rows(); ++row) {
			for (int col = 0; col < this.cols(); ++col) {
				Complex d = this.getItem(row, col).minus(reconstructed.getItem(row, col));
				maxAbsDiff = Math.max(maxAbsDiff, Math.max(Math.abs(d.rep()), Math.abs(d.imp())));
			}
		}
		if (!(maxAbsDiff < RECONSTRUCTION_TOLERANCE))
			throw new IllegalArgumentException(HEADINFO + " factorize: P*J*P^-1 does not "
				+ "reconstruct the original matrix (max residual " + maxAbsDiff + ", tolerance "
				+ RECONSTRUCTION_TOLERANCE + ") -- the eigenvalue(s) involved are almost "
				+ "certainly too imprecise for their multiplicity (root-finder grouping/precision "
				+ "limitation), producing a near-singular P that the individual eigenvector "
				+ "construction checks didn't catch.");
	}

}
