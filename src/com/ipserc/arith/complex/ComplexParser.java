package com.ipserc.arith.complex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex-based parsing of {@link Complex#setComplex(String)}'s "a+bi" (rectangular) / "a|b" (polar)
 * string format. Package-private: {@code Complex.setComplex(String)} keeps its exact public
 * signature and behavior (same field assignments, same exception messages), it just delegates the
 * pattern-matching/interpretation to {@link #parse(String)} here and then does the actual field
 * mutation + {@code setRecCoord()}/{@code setPolCoord()} call itself -- those two methods mutate
 * {@code this} directly and stay in {@code Complex.java}'s core, they do not move.
 * <p>
 * Extracted verbatim (Sexta sesion, paso 2, Fase 2.3) from {@code Complex.java}'s
 * "INITIALIZERS &amp; SETTERS" section -- only the parsing logic moved, not the whole section
 * (setCre/setPolCoord/setRecCoord/setComplex(char,double,double)/setComplexRec/setComplexPol/
 * setComplexRandom* all stay, they mutate {@code Complex} instance fields directly).
 */
final class ComplexParser {

	private ComplexParser() {}

	// Precompiled once instead of on every setComplex(String) call.
	private final static Pattern REC_PATTERN = Pattern.compile("[ \\t]*(([\\+|\\-])?[ \\t]*(\\d*\\.?\\d+(E[\\+|\\-|\\d]\\d*)?))?[ \\t]*(([\\+|\\-])?[ \\t]*(\\d*\\.?\\d+(E[\\+|\\-|\\d]\\d*)?)?)(i)?");
	private final static Pattern POL_PATTERN = Pattern.compile("[ \\t]*(\\d*\\.?\\d+(E[\\+|\\-|\\d]\\d*)?){1}[ \\t]*(\\|){1}[ \\t]*((\\+|\\-)?[\\d]*\\.?[\\d]+?){1}");

	/**
	 * The result of parsing a "a+bi" or "a|b" string: either (rep, imp) in rectangular form, or
	 * (mod, pha) in polar form -- {@link #polar} tells the caller which.
	 */
	static final class Parsed {
		final boolean polar;
		final double a;
		final double b;
		Parsed(boolean polar, double a, double b) {
			this.polar = polar;
			this.a = a;
			this.b = b;
		}
	}

	/**
	 * Parses a Complex's string representation.
	 * @param numC String representing a complex in Rectangular "a+bi" or Polar "a|b" coordinates. a,b are doubles.
	 * @return the parsed (rep,imp) or (mod,pha) pair, see {@link Parsed#polar}.
	 * @throws IllegalArgumentException if numC does not match either pattern.
	 */
	static Parsed parse(String numC) {
		double rep = 0.0, imp = 0.0, mod = 0.0, pha = 0.0;

		numC = numC.replace('e', 'E');
		if (numC.contains("|")) {
			Matcher polMatcher = POL_PATTERN.matcher(numC);
			if (polMatcher.matches()) {
				mod = Double.parseDouble(polMatcher.group(1).toString());
				pha = Double.parseDouble(polMatcher.group(4).toString());
				return new Parsed(true, mod, pha);
			} else {
				throw new IllegalArgumentException("Invalid number:" + numC);
			}
		}
		else {
			Matcher recMatcher = REC_PATTERN.matcher(numC);
			if (recMatcher.matches()) {
				/*
              System.out.println("numC " + numC);
             for (int i = 1; i <= recMatcher.groupCount(); i++) {
                 System.out.println("Group " + i + ":" + recMatcher.group(i) + ":");
             }
				 */

				int selector = 0;
				if (recMatcher.group(3) != null ) selector += 100;
				if (recMatcher.group(7) != null ) selector += 10;
				if (recMatcher.group(9) != null ) selector += 1;

				//System.out.println("selector " + selector);

				switch (selector) {
				case 1:
					//Just "i", "+i" or "-i"
					if (recMatcher.group(6) == null) imp = 1;
					else if (recMatcher.group(6).equals("-")) imp = -1.0;
					else imp = 1.0;
					break;
				case 100:
					// A real number
					rep = Double.parseDouble((((recMatcher.group(2) == null) ? "" : recMatcher.group(2)) + recMatcher.group(3)).toString());
					break;
				case 101:
					if (recMatcher.group(6) == null) { // A pure imag number
						imp = Double.parseDouble((((recMatcher.group(2) == null) ? "" : recMatcher.group(2)) + recMatcher.group(3)).toString());
					}
					else { // A complex number of the form a+i or a-i
						rep = Double.parseDouble((((recMatcher.group(2) == null) ? "" : recMatcher.group(2)) + recMatcher.group(3)).toString());
						//if (recMatcher.group(7) == null)
						imp = (recMatcher.group(6).equals("-")) ? -1.0 : 1.0;
						//else
						//imp = Double.parseDouble(((recMatcher.group(6)) + recMatcher.group(7)).toString());
					}
					break;
				case 111:
					rep = Double.parseDouble((((recMatcher.group(2) == null) ? "" : recMatcher.group(2)) + recMatcher.group(3)).toString());
					imp = Double.parseDouble((((recMatcher.group(6) == null) ? "" : recMatcher.group(6)) + recMatcher.group(7)).toString());
					break;
				default:
					throw new IllegalArgumentException("Invalid number:" + numC);
				}
				return new Parsed(false, rep, imp);
			}
			else {
				throw new IllegalArgumentException("Not matched number:" + numC);
			}
		}
	}
}
