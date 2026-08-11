package com.ipserc.arith.complex;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.text.NumberFormat;

/**
 * Per-thread calculation configuration for {@link Complex} (PRECISION, zero thresholds, number
 * formatting flags, default representation) and the public static methods that read/write it
 * (precision/significative/digits, setFormatON/OFF family, setRepres/getRepres/restoreRepres,
 * store/restore pairs). Package-private: {@code Complex} keeps every current public method as a
 * one-line delegator with the exact same signature, so external callers (7 library classes --
 * {@code MatrixComplex}, {@code Eigenspace}, {@code Polynom}, {@code Laplace}, {@code Fourier},
 * {@code Z}, {@code Spline} -- and ~200 test files) need zero changes.
 * <p>
 * No longer has an EXACT/APPROXIMATED mode (removed August 9, 2026, 22nd session,
 * see Claude/ComplexArithRev.md): {@code ZERO_THRESHOLD_EXACT} is now the sole "is this zero"
 * threshold, used unconditionally everywhere (algorithms and display alike).
 * {@code ZERO_THRESHOLD_APPROX} survives only as an independent, fixed loose tolerance used
 * directly by a handful of convergence guards ({@code Syseqnum}, {@code sqrtTriangular()},
 * {@code Complex.setCre()}) that always wanted a coarser value, never a switchable "mode".
 * <p>
 * Extracted from {@code Complex.java}'s "CONSTANTS"/"MEMBER VARS" and "PRECISION" sections plus
 * the format/representation methods that lived under its "PRESENTATION" banner (Sexta sesion,
 * paso 2, Fase 2.2). {@code Complex.Representation} itself stays declared on {@code Complex} (it
 * is part of the public API as {@code Complex.Representation}, e.g.
 * {@code Complex.setRepres(Complex.Representation.POLAR)} in test files) -- only the
 * configuration STATE that references it moved here.
 * <p>
 * Backed by a {@link ThreadLocal} so each thread gets its own independent copy -- state is still
 * shared globally within a single thread (across all {@code Complex} instances created on it),
 * matching how {@code storePrecision()}/{@code restorePrecision()} and {@code setRepres()}/
 * {@code restoreRepres()} are actually used by callers: temporarily override the precision/
 * representation, run a nested computation that creates many {@code Complex} instances, then
 * restore.
 * <p>
 * REENTRANCY: {@code storePrecision()}/{@code restorePrecision()}, {@code setRepres()}/
 * {@code restoreRepres()} and {@code storeFormatStatus()}/{@code restoreFormatStatus()} each
 * push/pop a snapshot on a per-thread {@link Deque} ({@code precisionStack}/{@code represStack}/
 * {@code formatStack}) instead of overwriting a single backup slot, so a store/restore pair
 * nested inside another one on the same thread does not clobber the outer call's backup. A
 * restore with an empty stack is a no-op.
 */
final class ComplexState {

	private ComplexState() {}

	/* Factory defaults -- computed once from the literal initial values below, never reassigned.
	 * Kept as plain static fields (not part of State): they are immutable in practice, so they
	 * are safe to share across all threads without any synchronization. */
	static final double PRECISION_DEF = 1E-13;
	static final double ZERO_THRESHOLD_EXACT_DEF = PRECISION_DEF*10;
	static final double ZERO_THRESHOLD_APPROX_DEF = Math.sqrt(PRECISION_DEF);
	static final int SIGNIFICATIVE_DEF = (int)Math.abs(Math.log10(ZERO_THRESHOLD_EXACT_DEF)) > 8 ? 8 : (int)Math.abs(Math.log10(ZERO_THRESHOLD_EXACT_DEF));
	static final long DIGITS_DEF = (long)Math.pow(10, SIGNIFICATIVE_DEF);
	static final int MAX_DECIMALS_DEFAULT = 8; //Member Variable

	private static final class State {
		/* Precision Block */
		double PRECISION = 1E-13; //1E-16; //1E-13;
		double ZERO_THRESHOLD_EXACT = PRECISION*10;	//9.999999999999E-13; //Zero threshold for "is this zero" decisions (algorithms) and display
		double ZERO_THRESHOLD_APPROX = Math.sqrt(PRECISION);	//9.999999999999E-6; //Loose tolerance, used directly (not mode-switched) by a few convergence guards (Syseqnum, sqrtTriangular, setCre())
		int SIGNIFICATIVE = (int)Math.abs(Math.log10(ZERO_THRESHOLD_EXACT)) > 8 ? 8 : (int)Math.abs(Math.log10(ZERO_THRESHOLD_EXACT));
		long DIGITS = (long)Math.pow(10, SIGNIFICATIVE);

		/* Stack of backups to allow restoring status, one push per storePrecision(), reentrant */
		final Deque<PrecisionSnapshot> precisionStack = new ArrayDeque<>();

		/* Formating block */
		boolean FORMAT_NBR = false; //Member Variable. Flag for formatting numbers
		boolean FIXED_NOTATION = false; //Member Variable. Flag for comma fixed notation
		boolean SCIENTIFIC_NOTATION = false; //Member Variable. Flag for scientific notation
		int MAX_DECIMALS = MAX_DECIMALS_DEFAULT; //Member Variable
		/* Stack of backups to allow restoring status, one push per storeFormatStatus(), reentrant */
		final Deque<FormatSnapshot> formatStack = new ArrayDeque<>();

		/* REPRESENTATION BLOCK */
		Complex.Representation REPRESENTATION = Complex.Representation.RECTANGULAR;
		/* Stack of backups to allow restoring status, one push per setRepres(), reentrant */
		final Deque<Complex.Representation> represStack = new ArrayDeque<>();
	}

	/** Immutable snapshot of the precision block, pushed/popped by {@link #storePrecision()}/{@link #restorePrecision()}. */
	private static final class PrecisionSnapshot {
		final double precision, zeroThresholdExact, zeroThresholdApprox;
		final int significative;
		final long digits;
		PrecisionSnapshot(double precision, double zeroThresholdExact, double zeroThresholdApprox, int significative, long digits) {
			this.precision = precision;
			this.zeroThresholdExact = zeroThresholdExact;
			this.zeroThresholdApprox = zeroThresholdApprox;
			this.significative = significative;
			this.digits = digits;
		}
	}

	/** Immutable snapshot of the format block, pushed/popped by {@link #storeFormatStatus()}/{@link #restoreFormatStatus()}. */
	private static final class FormatSnapshot {
		final boolean formatNbr, fixedNotation, scientificNotation;
		final int maxDecimals;
		FormatSnapshot(boolean formatNbr, boolean fixedNotation, boolean scientificNotation, int maxDecimals) {
			this.formatNbr = formatNbr;
			this.fixedNotation = fixedNotation;
			this.scientificNotation = scientificNotation;
			this.maxDecimals = maxDecimals;
		}
	}

	private static final ThreadLocal<State> STATE = ThreadLocal.withInitial(State::new);
	static State state() { return STATE.get(); }

	static double precision() {
	    return state().PRECISION;
	}

	static void precision_(double value) {
	    state().PRECISION = value;
	}

	static void precision(double value) {
		storePrecision();
	    state().PRECISION = value;
	    state().ZERO_THRESHOLD_EXACT = state().PRECISION*10;
	    state().ZERO_THRESHOLD_APPROX = Math.sqrt(state().PRECISION);
	    state().SIGNIFICATIVE = (int)Math.abs(Math.log10(state().ZERO_THRESHOLD_EXACT));
	    state().DIGITS = (long)Math.pow(10, state().SIGNIFICATIVE);
	}

	static double zero_treshold_exact() {
	    return state().ZERO_THRESHOLD_EXACT;
	}

	static void zero_threshold_exact(double value) {
	    state().ZERO_THRESHOLD_EXACT = value;
	}

	static void zero_threshold_exact_prec(double value) {
		storePrecision();
		zero_threshold_exact(value);
	    state().SIGNIFICATIVE = (int)Math.abs(Math.log10(state().ZERO_THRESHOLD_EXACT));
	    state().DIGITS = (long)Math.pow(10, state().SIGNIFICATIVE);
	}

	static double zero_threshold_approx() {
	    return state().ZERO_THRESHOLD_APPROX;
	}

	static void zero_threshold_approx(double value) {
	    state().ZERO_THRESHOLD_APPROX = value;
	}

	static int significative() {
	    return state().SIGNIFICATIVE;
	}

	static void significative_(int value) {
	    state().SIGNIFICATIVE = value;
	}

	static void significative(int value) {
		storePrecision();
	    state().SIGNIFICATIVE = value;
	    state().DIGITS = (long)Math.pow(10, state().SIGNIFICATIVE);
	}

	static long digits() {
	    return state().DIGITS;
	}

	static void digits(long value) {
	    state().DIGITS = value;
	}

	/**
	 * Stores the Precision parameters to recover them later. Reentrant: each call pushes a new
	 * backup, so nested store/restore pairs on the same thread do not clobber each other.
	 */
	static void storePrecision() {
	    state().precisionStack.push(new PrecisionSnapshot(state().PRECISION, state().ZERO_THRESHOLD_EXACT, state().ZERO_THRESHOLD_APPROX, state().SIGNIFICATIVE, state().DIGITS));
	}

	/**
	 * Recovers the Precision parameters stored before. A no-op if called without a matching
	 * prior {@link #storePrecision()} on the same thread.
	 */
	static void restorePrecision() {
	    PrecisionSnapshot snapshot = state().precisionStack.poll();
	    if (snapshot == null) return;
	    state().PRECISION = snapshot.precision;
	    state().ZERO_THRESHOLD_EXACT = snapshot.zeroThresholdExact;
	    state().ZERO_THRESHOLD_APPROX = snapshot.zeroThresholdApprox;
	    state().SIGNIFICATIVE = snapshot.significative;
	    state().DIGITS = snapshot.digits;
	}

	/**
	 * Restores all the precision values to the Factory defined ones
	 */
	static void restorePrecisionFactorySettings() {
	    state().PRECISION = PRECISION_DEF;
	    state().ZERO_THRESHOLD_EXACT = ZERO_THRESHOLD_EXACT_DEF;
	    state().ZERO_THRESHOLD_APPROX = ZERO_THRESHOLD_APPROX_DEF;
	    state().SIGNIFICATIVE = SIGNIFICATIVE_DEF;
	    state().DIGITS = DIGITS_DEF;
	}

	/**
	 * Activates the numbers' formatting. This value is GLOBAL. Prints a message in the Console.
	 * @param printStat
	 */
	static void setFormatON(boolean printStat) {
		state().FORMAT_NBR = true;
		if (printStat) printFormatStatus();
	}

	/**
	 * Activates the numbers' formatting. This value is GLOBAL.
	 */
	static void setFormatON() {
		setFormatON(false);
	}

	/**
	 * Activates the Fixed notation. This value is GLOBAL. Prints a message in the Console.
	 * @param decimals Number of decimals to show
	 * @param printStat
	 */
	static void setFixedON(int decimals, boolean printStat) {
		state().FIXED_NOTATION = true;
		state().SCIENTIFIC_NOTATION = false;
		state().MAX_DECIMALS = decimals;
		if (printStat) printFormatStatus();
	}

	/**
	 * Activates the Fixed notation. This value is GLOBAL.
	 * @param decimals Number of decimals to show
	 */
	static void setFixedON(int decimals) {
		setFixedON(decimals, false);
	}

	/**
	 * Activates the Scientific notation. This value is GLOBAL. Prints a message in the Console.
	 * @param decimals Number of decimals to show
	 * @param printStat
	 */
	static void setScientificON(int decimals, boolean printStat) {
		state().FIXED_NOTATION = false;
		state().SCIENTIFIC_NOTATION = true;
		state().MAX_DECIMALS = decimals;
		if (printStat) printFormatStatus();
	}

	/**
	 * Activates the Scientific notation. This value is GLOBAL.
	 * @param decimals Number of decimals to show
	 */
	static void setScientificON(int decimals) {
		setScientificON(decimals, false);
	}

	/**
	 * Deactivates the numbers' formatting presentation. This value is GLOBAL. Prints a message in the Console.
	 * @param printStat
	 */
	static void setFormatOFF(boolean printStat) {
		state().FORMAT_NBR = false;
		if (printStat) printFormatStatus();
	}

	/**
	 * Deactivates the numbers' formatting presentation. This value is GLOBAL.
	 */
	static void setFormatOFF() {
		setFormatOFF(false);
	}

	/**
	 * Gets the Format Number Status
	 * @return True or False
	 */
	static Boolean getFortmatStatus() {
		return state().FORMAT_NBR;
	}

	/**
	 * Deactivates the Fixed notation. This value is GLOBAL. Prints a message in the Console.
	 * @param printStat
	 */
	static void setFixedOFF(boolean printStat) {
		state().FIXED_NOTATION = false;
		if (printStat) printFormatStatus();
	}

	/**
	 * Deactivates the Fixed notation. This value is GLOBAL. Prints a message in the Console.
	 */
	static void setFixedOFF() {
		setFixedOFF(false);
	}

	/**
	 * Gets the Fixed notation status.
	 * @return True if fixed notation is enabled.
	 */
	static Boolean getFixedStatus() {
		return state().FIXED_NOTATION;
	}

	/**
	 * Deactivates the Scientific notation. This value is GLOBAL. Prints a message in the Console.
	 * @param printStat
	 */
	static void setScientificOFF(boolean printStat) {
		state().SCIENTIFIC_NOTATION = false;
		if (printStat) printFormatStatus();
	}

	/**
	 * Deactivates the Scientific notation. This value is GLOBAL. Prints a message in the Console.
	 */
	static void setScientificOFF() {
		setScientificOFF(false);
	}

	/**
	 * Gets the Status of the SCIENTIFIC_NOTATION
	 * @return The value of SCIENTIFIC_NOTATION
	 */
	static Boolean getScientificStatus() {
		return state().SCIENTIFIC_NOTATION;
	}

	/**
	 * Gets the number of decimals
	 * @return MAX_DECIMALS
	 */
	static int getMaxDecimals() {
		return state().MAX_DECIMALS;
	}

	/**
	 * Gets the number of decimals
	 * @return SIGNIFICATIVE
	 */
	static int getSignificative() {
		return state().SIGNIFICATIVE;
	}

	/**
	 * Shows the numbers formatting presentation status. Prints a message in the Console.
	 */
	static void printFormatStatus() {
		System.out.println( "------------------------------------------------");
		System.out.println( "Formatting numbers is " + (state().FORMAT_NBR ? "ENABLED" : "DISABLED"));
		System.out.println( "Fixed notation is " + (state().FIXED_NOTATION ? "ENABLED decimals:" + getMaxDecimals() : "DISABLED"));
		System.out.println( "Scientific notation is " + (state().SCIENTIFIC_NOTATION ? "ENABLED decimals:" + getMaxDecimals() : "DISABLED"));
		System.out.println( "------------------------------------------------");
	}

	/**
	 * Stores the Format Status to be restored by restoreFormatStatus. Reentrant: each call pushes
	 * a new backup, so nested store/restore pairs on the same thread do not clobber each other.
	 */
	static void storeFormatStatus() {
		state().formatStack.push(new FormatSnapshot(state().FORMAT_NBR, state().FIXED_NOTATION, state().SCIENTIFIC_NOTATION, state().MAX_DECIMALS));
	}

	/**
	 * Restore the Format Status to its previous condition if it was stored before. A no-op if
	 * called without a matching prior {@link #storeFormatStatus()} on the same thread.
	 */
	static void restoreFormatStatus() {
		FormatSnapshot snapshot = state().formatStack.poll();
		if (snapshot == null) return;
		state().FORMAT_NBR = snapshot.formatNbr;
		state().FIXED_NOTATION = snapshot.fixedNotation;
		state().SCIENTIFIC_NOTATION = snapshot.scientificNotation;
		state().MAX_DECIMALS = snapshot.maxDecimals;
	}

	/**
	 * Reset the format status to the predefined condition
	 */
	static void resetFormatStatus() {
		state().FORMAT_NBR = false;
		state().FIXED_NOTATION = false;
		state().SCIENTIFIC_NOTATION = false;
		state().MAX_DECIMALS = MAX_DECIMALS_DEFAULT;
	}

	/**
	 * Configures the number format for the Locale("en", "UK") and sets the maximum and minimum number of digits.
	 */
	static void setDigits() {
		Locale locale = new Locale("en", "UK");
		NumberFormat numberFormat = NumberFormat.getInstance(locale);
		numberFormat.setMinimumFractionDigits(20); //MAX_DECIMALS);
		numberFormat.setMaximumFractionDigits(20); //MAX_DECIMALS);
	}

	/**
	 * Sets the output format of the complex representation. Reentrant: each call pushes the
	 * previous representation onto a backup stack, so nested setRepres()/restoreRepres() pairs
	 * on the same thread do not clobber each other.
	 * @param Repres
	 */
	static void setRepres(Complex.Representation Repres) {
		state().represStack.push(state().REPRESENTATION);
		state().REPRESENTATION = Repres;
	}

	/**
	 * Gets the current representation enum value directly (unlike {@link #getRepres()}, which
	 * returns its String name) -- needed by the {@code toString*} switch statements that still
	 * live in {@code Complex.java} pending Fase 2.4.
	 */
	static Complex.Representation representation() {
		return state().REPRESENTATION;
	}

	/**
	 * Gets the output format of the complex representation
	 */
	static String getRepres() {
		String Repres = "";
		switch (state().REPRESENTATION) {
		case RECTANGULAR: Repres = "RECTANGULAR"; break;
		case POLAR: Repres =  "POLAR"; break;
		}
		return Repres;
	}

	/**
	 * Restores the last used output format of the complex representation. A no-op if called
	 * without a matching prior {@link #setRepres(Complex.Representation)} on the same thread.
	 */
	static void restoreRepres() {
		Complex.Representation previous = state().represStack.poll();
		if (previous == null) return;
		state().REPRESENTATION = previous;
	}
}
