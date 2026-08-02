package com.ipserc.arith.polynom;

import com.ipserc.arith.complex.Complex;

/**
 * Package-private presentation/export logic for {@link Polynom} -- string representations
 * (default {@code toString()}, coefficient listing, constructor-statement round-trip) and export
 * to the syntax of external CAS/plotting tools (Maxima, Wolfram, GNU Octave, GNUPlot).
 * <p>
 * Extracted from {@code Polynom.java} (Décima sesión, ver {@code Claude/ComplexArithRev.md}) to
 * separate presentation from the resolution/arithmetic core, same pattern already used for
 * {@code Complex.java}'s {@code ComplexFormat} (Sexta sesión, Paso 2): every method here is
 * {@code static}, takes the {@link Polynom} instance as an explicit parameter, and reads it only
 * through already-public members ({@code complexMatrix}, {@code rows()}, {@code cols()},
 * {@code copy()}, {@code toOctave()}) plus {@link Polynom#reverse()} (widened from {@code private}
 * to package-private for this, same body, same single existing call site unaffected).
 * {@code Polynom.java}'s own public methods keep their exact signatures, delegating to these in
 * one line each -- the public API is unchanged.
 */
class PolynomFormat {

	/**
	 * Builds the string representation of the polynomial in the form
	 * {@code A[n]x^n + A[n-1]x^(n-1) + ... + A[2]x^2 + A[1]x + A[0]}.
	 * @param p The polynomial.
	 * @return The string representation of the polynomial.
	 */
	static String toString(Polynom p) {
		int rowLen = p.rows();
		int colLen = p.cols();
		Complex cCoef;
		String polynom = "";

		for (int row = 0; row < rowLen; ++row) {
			for (int col = colLen-1; col >= 0; --col) {
				cCoef = p.complexMatrix[row][col];
				if (cCoef.equals(0,0))
					continue;
				if (col < colLen-1) polynom += "+";
				if (col != 0)
					if (col != 1) polynom += "("+cCoef.toStringRec()+")x^"+col;
					else polynom += "("+cCoef.toStringRec()+")x";
				else polynom += "("+cCoef.toStringRec()+")";
			}
		}
		return polynom;
	}

	/**
	 * Constructs the polynomial as string as the one used by Maxima (Computer Algebra System).
	 * @param p The polynomial.
	 * @return The Maxima's polynomial representation as a string.
	 */
	static String toMaximaPoly(Polynom p) {
		String polynom = toString(p);
		polynom = polynom.replace("i", "*%i");
		polynom = polynom.replace("x", "*x");
		return polynom;
	}

	/**
	 * Constructs the polynomial as string as the one used by Maxima (Computer Algebra System)
	 * appending a text before.
	 * @param p The polynomial.
	 * @param text The text at the beginning of the string.
	 * @return The Maxima's polynomial representation as a string.
	 */
	static String toMaximaPoly(Polynom p, String text) {
		return text + toMaximaPoly(p);
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used
	 * by Maxima (Computer Algebra System).
	 * @param p The polynomial.
	 * @return The Maxima's allroots command for a polynomial.
	 */
	static String toMaximaRoots(Polynom p) {
		return "allroots(" + toMaximaPoly(p) + ")";
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used
	 * by Maxima (Computer Algebra System) appending a text before.
	 * @param p The polynomial.
	 * @param text The text at the beginning of the string.
	 * @return The Maxima's allroots command for a polynomial.
	 */
	static String toMaximaRoots(Polynom p, String text) {
		return text + toMaximaRoots(p);
	}

	/**
	 * Constructs the polynomial as string as the used by GNUPlot.
	 * @param p The polynomial.
	 * @return The GNUPlot's polynomial representation as a string.
	 */
	static String toGNUPlotPoly(Polynom p) {
		int rowLen = p.rows();
		int colLen = p.cols();
		Complex cCoef;
		String polynom = "";

		for (int row = 0; row < rowLen; ++row) {
			for (int col = colLen-1; col >= 0; --col) {
				cCoef = p.complexMatrix[row][col];
				if (cCoef.equals(0,0))
					continue;
				if (col < colLen-1) polynom += "+";
				if (col != 0)
					if (col != 1) polynom += cCoef.toStringGNUPlot()+"*x**"+col;
					else polynom += cCoef.toStringGNUPlot()+"*x";
				else polynom += cCoef.toStringGNUPlot();
			}
		}
		return polynom;
	}

	/**
	 * Constructs the polynomial as string as the one used by Wolfram.
	 * @param p The polynomial.
	 * @return The Wolfram Mathematica's polynomial representation as a string.
	 */
	static String toWolframPoly(Polynom p) {
		return toString(p).replace("E", "*10^");
	}

	/**
	 * Constructs the polynomial as string as the one used by Wolfram appending a text before.
	 * @param p The polynomial.
	 * @param text The text at the beginning of the string.
	 * @return The Wolfram Mathematica's polynomial representation as a string.
	 */
	static String toWolframPoly(Polynom p, String text) {
		return text + toWolframPoly(p);
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used
	 * by Wolfram.
	 * @param p The polynomial.
	 * @return The Wolfram's roots command for a polynomial.
	 */
	static String toWolframRoots(Polynom p) {
		return "roots[" + toWolframPoly(p) + "]";
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used
	 * by Wolfram appending a text before.
	 * @param p The polynomial.
	 * @param text The text at the beginning of the string.
	 * @return The Wolfram's roots command for a polynomial.
	 */
	static String toWolframRoots(Polynom p, String text) {
		return text + toWolframRoots(p);
	}

	/**
	 * Constructs the polynomial as string as the used by GNU Octave.
	 * @param p The polynomial.
	 * @return The Octave polynomial representation as a string.
	 */
	static String toOctavePoly(Polynom p) {
		Polynom newPol = p.copy();
		newPol.reverse();
		return newPol.toOctave();
	}

	/**
	 * Constructs the polynomial as string as the used by GNU Octave appending a text before.
	 * @param p The polynomial.
	 * @param text The text at the beginning of the string.
	 * @return The Octave polynomial representation as a string.
	 */
	static String toOctavePoly(Polynom p, String text) {
		return text + toOctavePoly(p);
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used
	 * by Octave.
	 * @param p The polynomial.
	 * @return The Octave's roots command for a polynomial.
	 */
	static String toOctaveRoots(Polynom p) {
		return "roots(" + toOctavePoly(p) + ")";
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used
	 * by Octave appending a text before.
	 * @param p The polynomial.
	 * @param text The text at the beginning of the string.
	 * @return The Octave's roots command for a polynomial.
	 */
	static String toOctaveRoots(Polynom p, String text) {
		return text + toOctaveRoots(p);
	}

	/**
	 * Displays the coefficients of the polynomial as a vector.
	 * @param p The polynomial.
	 * @return The coefficient listing.
	 */
	static String toCoefs(Polynom p) {
		int rowLen = p.rows();
		int colLen = p.cols();
		String polyCoefs = "";

		for (int row = 0; row < rowLen; ++row) {
			polyCoefs += ("(");
			for (int col = colLen-1; col >= 0; --col) {
				polyCoefs += p.complexMatrix[row][col].toString();
				polyCoefs += (col == 0 ? ")" : ",");
			}
		}
		return polyCoefs;
	}

	/**
	 * Builds the string that represents a polynomial constructor.
	 * @param p The polynomial.
	 * @return The string with the constructor statement of the polynomial.
	 */
	static String toPolynom(Polynom p) {
		int rowLen = p.rows();
		int colLen = p.cols();
		int row, col;
		String polynom = "";

		polynom = "new Polynom(\"";
		for (row = 0; row < rowLen - 1; ++row) {
			for (col = colLen-1; col >= 0; --col) {
				polynom += p.complexMatrix[row][col].toString();
				polynom += col == 0 ? ";" : ",";
			}
		}
		for (col = colLen-1; col >= 0; --col) {
			polynom += p.complexMatrix[row][col].toString();
			polynom += col == 0 ? "\");" : ",";
		}
		return polynom;
	}

	/**
	 * Builds the string that represents a polynomial constructor with a text in front of it.
	 * @param p The polynomial.
	 * @param text The text at the beginning of the string.
	 * @return The string with constructor instruction.
	 */
	static String toPolynom(Polynom p, String text) {
		return text + toPolynom(p);
	}

}
