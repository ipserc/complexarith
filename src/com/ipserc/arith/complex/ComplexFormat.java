package com.ipserc.arith.complex;

/**
 * String formatting/presentation for {@link Complex} ({@code toString}, {@code toStringRec},
 * {@code toStringRecI}, {@code toStringPol}, {@code toStringGNUPlot} and their {@code formatNbr}/
 * {@code chr} helpers). Package-private: each of {@code Complex}'s public instance methods keeps
 * its exact signature and stays defined ON {@code Complex} (a JVM override of
 * {@code Object.toString()} must live on the class itself, and the {@code toStringXxx} methods are
 * public API called as {@code someComplex.toStringRec()} by ~200 test files) -- their bodies just
 * delegate to the static methods here, which take the {@code Complex} instance as a parameter and
 * read it through its public getters/{@code rePartNull()}/{@code imPartNull()} (this class cannot
 * touch {@code Complex}'s private fields directly).
 * <p>
 * Extracted verbatim (Sexta sesion, paso 2, Fase 2.4) from {@code Complex.java}'s "PRESENTATION"
 * section. {@code normalizePhase()} (originally a dispatcher over three normalizePhase_0/1/2
 * variants; the two dead ones were removed in a later audit fix) stayed in {@code Complex.java}'s
 * core despite living under the same banner -- it is an arithmetic invariant-maintenance helper
 * also used by the core arithmetic (plus/minus/times/divides and their in-place variants), not
 * presentation. The {@code print*}/{@code println*} instance methods also stayed: they only call
 * {@code this.toString()}/{@code toStringRec()}/{@code toStringPol()}, so they work unchanged
 * regardless of where those methods' implementation lives.
 */
final class ComplexFormat {

	private ComplexFormat() {}

	static String toString(Complex z) {
		String strComplex = "";
		switch (ComplexState.representation()) {
		case RECTANGULAR:
			strComplex = toStringRec(z);
			break;
		case POLAR:
			strComplex = toStringPol(z);
			break;
		}
		return strComplex;
	}

	static String toStringRec(Complex z) {
		return toStringRec(z, "i");
	}

	static String toStringRecWolfram(Complex z) {
		String strWolfram = toStringRec(z, "I");
		return strWolfram.replace("E", "*10^");
	}

	private static String toStringRec(Complex z, String imu) {
		double fRep = formatNbr(z.rep());
		double fImp = formatNbr(z.imp());
		String sfRep = new String();
		String sfImp = new String();

		if (Double.isInfinite(z.mod())) {
			if (Math.tan(z.pha()) >= 0) return ("Infinity");
			else return ("-Infinity");
		}

		if (ComplexState.getFortmatStatus()) {
			// Purity check: delegates to the canonical rePartNull()/imPartNull() predicates
			// (ZERO_THRESHOLD*CORRECTION_FACTOR), the same ones setRecCoord() already uses when
			// building a Complex from polar coordinates, so every toString* formatter agrees on
			// when a component is negligible relative to the other.
			if (z.rePartNull()) fRep = 0.0;
			if (z.imPartNull()) fImp = 0.0;
		}
		sfRep = String.valueOf(fRep);
		if (ComplexState.getScientificStatus()) sfRep = String.format("%."+ComplexState.getMaxDecimals()+"E", fRep).replace(',', '.');
		else if (ComplexState.getFixedStatus()) sfRep = String.format("%."+ComplexState.getMaxDecimals()+"f", fRep).replace(',', '.');
			//else sfRep = String.format("%."+MAX_DECIMALS+"f", fRep).replace(',', '.');

		sfImp = String.valueOf(fImp);
		if (ComplexState.getScientificStatus()) sfImp = String.format("%."+ComplexState.getMaxDecimals()+"E", fImp).replace(',', '.');
		else if (ComplexState.getFixedStatus()) sfImp = String.format("%."+ComplexState.getMaxDecimals()+"f", fImp).replace(',', '.');
			//else sfImp = String.format("%."+MAX_DECIMALS+"f", fImp).replace(',', '.');

		String strComplex;

		if (fImp == 0.0 || Double.parseDouble(sfImp) == 0.0)
			strComplex =  sfRep + "";
		else if (fRep == 0.0 || Double.parseDouble(sfRep) == 0.0)
			strComplex = sfImp + imu;
		else if (fImp <  0.0)
			strComplex = sfRep + sfImp + imu;
		else strComplex = sfRep + "+" + sfImp + imu;

		// Padding the complex
		strComplex = strComplex.charAt(0)== '-' ? strComplex : Complex.numpad() + strComplex ;
		return strComplex;
	}

	static String toStringRecI(Complex z) {
		double fRep = z.rep();
		double fImp = z.imp();

		if (ComplexState.getFortmatStatus()) {
			// Purity check: delegates to the canonical rePartNull()/imPartNull() predicates
			// (ZERO_THRESHOLD*CORRECTION_FACTOR), the same ones setRecCoord() already uses when
			// building a Complex from polar coordinates, so every toString* formatter agrees on
			// when a component is negligible relative to the other.
			if (z.rePartNull()) fRep = 0.0;
			if (z.imPartNull()) fImp = 0.0;
		}
		if (fImp == 0.0 )
			return fRep + "";
		if (fRep == 0.0)
			if (Math.abs(fImp) != 1.0)
				return fImp + "i";
			else
				return (fImp == -1.0) ? "-i" : "i";
		if (fImp <  0.0)
			return fRep + "-" + ((fImp == -1.0) ? "" : (-fImp)) + "i";
		return fRep + "+" + ((fImp == 1.0) ? "" : fImp) + "i";
	}

	static String toStringPol(Complex z) {
		double fMod = z.mod();
		double fPha = z.pha();
		String sfMod = new String();
		String sfPha = new String();

		if (ComplexState.getFortmatStatus()) {
			// Purity check: snaps the phase to the nearest axis-aligned value (0, +HALF_PI, PI,
			// -HALF_PI) using the same rePartNull()/imPartNull() predicates as toStringRec and
			// setRecCoord(), instead of only detecting "phase near zero" (pure positive real) as
			// before -- this also recognizes pure negative real (phase near PI) and pure
			// imaginary (phase near +-HALF_PI) numbers, which the previous check missed entirely.
			if (fMod < ComplexState.zero_treshold_exact()) {
				fMod = 0.0;
				fPha = 0.0;
			}
			else if (z.imPartNull()) fPha = (z.rep() >= 0.0) ? 0.0 : Math.PI;
			else if (z.rePartNull()) fPha = (z.imp() >= 0.0) ? Complex.HALF_PI : -Complex.HALF_PI;
		}

		sfMod = String.valueOf(fMod);
		if (ComplexState.getScientificStatus()) sfMod = String.format("%."+ComplexState.getMaxDecimals()+"E", fMod).replace(',', '.');
		else if (ComplexState.getFixedStatus()) sfMod = String.format("%."+ComplexState.getMaxDecimals()+"f", fMod).replace(',', '.');
			//else sfMod = String.format("%."+MAX_DIGITS+"f", fMod).replace(',', '.');

		sfPha = String.valueOf(fPha);
		if (ComplexState.getScientificStatus() || ComplexState.getFixedStatus()) sfPha = String.format("%."+ComplexState.getMaxDecimals()+"f", fPha).replace(',', '.');

		return sfMod + "|" + sfPha;
	}

	static String toStringGNUPlot(Complex z) {
		double fRep = formatNbr(z.rep());
		double fImp = formatNbr(z.imp());

		if (ComplexState.getFortmatStatus()) {
			// Purity check: delegates to the canonical rePartNull()/imPartNull() predicates
			// (ZERO_THRESHOLD*CORRECTION_FACTOR), same as toStringRec/toStringPol/setRecCoord.
			// Now gated by FORMAT_NBR like the other three formatters, instead of purging
			// unconditionally regardless of the flag.
			if (z.rePartNull()) fRep = 0.0;
			if (z.imPartNull()) fImp = 0.0;
		}
		if (fImp == 0) return Double.toString(fRep);
		return "{" + fRep + "," + fImp + "}";
	}

	/**
	 * Returns the character string for the character code
	 * @param charCode The character code
	 * @return The character string
	 */
	private static String chr(int charCode) {
		return Character.toString((char) charCode);
	}

	/**
	 * Formats the number according to a ZERO_THRESHOLD and a number of significant decimals.
	 * Tries to return clean-looking integers while keeping the maximum number of decimals for real values.
	 * @param number The number to be formatted.
	 * @return The formatted number.
	 */
	private static double formatNbr(double number) {
		if (!ComplexState.getFortmatStatus()) return number;
		if (Math.abs(number) < ComplexState.zero_treshold_exact()) return 0.0;
		double newNumber = Math.rint(number * ComplexState.digits()) / ComplexState.digits();
		return newNumber;
	}
}
