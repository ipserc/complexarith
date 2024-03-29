/**
 *  Complex es una clase que permite definir y operar con números complejos.
 *  <p>
 *  Incorpora funciones aritméticas como la suma, la resta, el producto y la división por complejos.
 *  Se ha definido el producto 
 *  Compilation:  javac Complex.java
 *  Execution:    java Complex
 *
 *  Data type for complex numbers.
 *	
 *  Based on http://introcs.cs.princeton.edu/java/97data/Complex.java.html
 *  from http://introcs.cs.princeton.edu/java/32class/
 *  
 *  
 *  
 *  
 *
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *
 */
package com.ipserc.arith.complex;

import java.lang.Double;
import java.lang.Math;
import java.math.*;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Random;
import java.io.*;
import java.util.Scanner;

/**
 * Complex class to work with complex numbers
 * @author ipserc
 *
 */
public class Complex {
	/*
	 * ***********************************************
	 * CONSTANTS
	 * ***********************************************
	 */
	private final static String HEADINFO = "Complex --- INFO: ";
	private final static String VERSION = "1.9 (2023_0514_2000)";
	/* VERSION Release Note
	 * 1.9 (2023_0514_2000)
	 * public static void printBoxTitle(int boxId, int size, String title) {
	 * public static void printBoxText(int boxId, int size, String title) {
	 * public static Complex ChebyshevZero(int n, int k)
	 * private void setRecCoord() { . . . if (this.imPartNull()) this.imp = 0.0;
	 * public static Complex zeta(Complex s) {
	 * public static Complex zeta_re(Complex s) {
	 * public static Complex zeta_ext(Complex s) {
	 * public static Complex zeta_reflex(Complex s) {
	 * public static Complex zeta_primes(Complex s) {
	 * public static Complex zeta_riemann_siegel(Complex s) {
	 * public static Complex zeta_analytic_continuation(Complex s) {
	 * public static Complex zeta_havil(Complex s) {
	 * public static Complex binomialCoef(int n, int k) {
	 * public static Complex binomialCoef(Complex n, Complex k) {
	 * public static double factorial(int n) {
	 * public static Complex factorial(Complex n) {
	 * 
	 * 1.8 (2022_0928_0000)
	 * public boolean isInteger()
	 * public boolean isIntegerPositive()
	 * public boolean isIntegerNegative()
	 * public boolean isIntegerPositiveZero()
	 * public boolean isIntegerNegativeZero()
	 * public static Complex gamma(Complex z)
	 * public static Complex gamma_integral(double n)
	 * public static Complex gamma_integral(Complex z)
	 * public static Complex gamma_integral2(Complex z)
	 * public static Complex gamma_weiertrass(Complex z)
	 * public static Complex gamma_euler(Complex z)
	 * public static Complex gamma_nemes(Complex z)
	 * public static Complex beta(Complex p, Complex q)
	 * REPRESENTATION enumeration to select the default representation of the complex number between rectangular or polar coordinates
	 * public static void setRepres(Representation Repres)
	 * public static String getRepres()
	 * public static void restoreRepres()
	 * public String toString()
	 * public void printRec()
	 * public void printPol()
	 * public void printlnRec()
	 * public void printlnPol()
	 * 
	 * 1.7 (2022_0911_1130)
	 * New management of EXACT/APPROXIMATED settings. Now ZERO_THRESHOLD holds the current value of the threshold for EXACT/APPROXIMATED
	 * All the methods regarding ZERO_THRESHOLD_XXXX have been modified to include this optimization
	 * public static boolean exact() {
	 * public static void exact(boolean value) {
	 * public static double zero_treshold_exact() {
	 * public static void zero_threshold_exact(double value) {
	 * public static double zero_threshold_approx() {
	 * public static void zero_threshold_approx(double value) {
	 * and all the methods which are using the above ones
	 * 
	 * 1.6 (2022_0202_2100)
	 * To use approximated equality. true use EXACT equality. false use APPROX equality.
	 * 
	 * public static boolean Exact = true; Eliminate Reduced local class member. Complex.Exact rules all the clasess
	 * 
	 * Now SIGNIFICATIVE is 8 decimals maximum. Machine significatives is 15, 7 decimals are for carry the rounding error
	 * 
	 * Correction factor for equality comparisons. I hate these kinds of things that seem to work but have no way to justify or prove
	 * public final static int CORRECTION_FACTOR = 100; 
	 * 
	 * All the flavors of equaslred disappear. Now equals decide the use of ZERO_THRESHOLD  or ZERO_THRESHOLD_R in function of the value of Exact
	 * public boolean isZero()
	 * public boolean imPartNull() 
	 * public boolean rePartNull()
	 * public boolean equals(Complex cNum)
	 * public boolean equals(double n1, double n2) 
	 * public boolean equals(Complex cNum, int numDecs) 
	 * public Boolean isPureReal()
	 * public Boolean isPureImaginary()
	 * All the xxxxxxxxRed are DEPRECATED and will be removed in the next release
	 * public boolean isZeroRed__() 
	 * public boolean imPartNullRed__()
	 * public boolean rePartNullRed__()
	 * public boolean equalsred__(Complex cNum)
	 * public boolean equalsred__(double n1, double n2) 
	 * public boolean equalsred__(double n1, double n2, int numDecs)
	 * public boolean equalsred__(Complex cNum, int numDecs)
	 * 
	 * public static Complex sqrt(Complex z)		New Square Root Method
	 * public static Complex sqrt(Complex z, int k) New Square Root Method
	 * DERECATED
	 * public static Complex sqrroot__(Complex z)
	 * public static Complex sqrroot__(Complex z, int k)
	 * 
	 * public static void showPrecision() Renamed to camel style
	 * public static void restorePrecisionFactorySettings() Included from now on
	 * 
	 * public String toStringRecWolfram() replace("E", "*10^"); TODO with polar representation
	 * 
	 * 1.5 (2021_0929_2100)
	 * added trunc method to truncate a double value.
	 * added trunc method to truncate a Complex value.
	 * round method uses BigDecimal.setScale(decs, RoundingMode.HALF_UP); for rounding the double
	 * round(Complex complex, int decs) uses the round method with the modulus of the complex leaving the phase unaltered
	 * 
	 */

	//public final static double PI = Math.PI; 			// 3.1415926535897932384626433832795;
	public final static double TWO_PI = 2 * Math.PI;	// 2 * 3.1415926535897932384626433832795;
	public final static double DOS_PI = TWO_PI;			// 2 * 3.1415926535897932384626433832795;
	public final static double HALF_PI =  Math.PI / 2; 	// 3.1415926535897932384626433832795 / 2;
	public final static double EULER_MASC = 0.5772156649015328606065120900824024310421; // Constant of Euler-Mascheroni
	public final static double LIM_INF = 2147483647; //2147483647

	public final static Complex i = new Complex(0,1);
	public final static Complex j = i; // For Electric Engineering
	public final static Complex ZERO = new Complex(0,0);
	public final static Complex ONE = new Complex(1,0);
	public final static Complex mONE = new Complex(-1,0);
	public final static Complex PI = new Complex(Math.PI,0);
	public final static Complex DOSPI = new Complex(DOS_PI,0);
	public final static Complex TWOPI = DOSPI;	
	public final static Complex HALFPI = new Complex(HALF_PI,0);
	
	// FIXED - Correction factor for equality comparisons. I hate these kind of things that seem to work but have no way to justify or prove
	public final static int CORRECTION_FACTOR = 10; 
	// FIXED - The same feeling as Einstein before the cosmological constant 
	// FIXED: PRECISION = 1E-13
	
	public static enum Representation {RECTANGULAR, POLAR};
	/*
	 * ***********************************************
	 * MEMBER VARS
	 * ***********************************************
	 */

	/**
	 * To use approximated equality. true use EXACT equality. false use APPROX equality.
	 */
	private static boolean EXACT = true;

	/* Precision Block */
	private static double PRECISION = 1E-13; //1E-16; //1E-13;
	private static double ZERO_THRESHOLD_EXACT = PRECISION*10;	//9.999999999999E-13; //Zero threshold for formatting numbers
	private static double ZERO_THRESHOLD_APPROX = Math.sqrt(PRECISION);	//9.999999999999E-6; //Reduced Zero threshold for formatting numbers 9.999999999999E-3
	private static double ZERO_THRESHOLD = ZERO_THRESHOLD_EXACT;	//Current in use Zero threshold for formatting numbers
	private static int SIGNIFICATIVE = (int)Math.abs(Math.log10(ZERO_THRESHOLD)) > 8 ? 8 : (int)Math.abs(Math.log10(ZERO_THRESHOLD));
	private static long DIGITS = (long)Math.pow(10, SIGNIFICATIVE);

	/* BACK UP to allow restoring status */
	private static double PRECISION_DEF = PRECISION;
	private static double ZERO_THRESHOLD_EXACT_DEF = ZERO_THRESHOLD_EXACT;
	private static double ZERO_THRESHOLD_APPROX_DEF = ZERO_THRESHOLD_APPROX;
	private static double ZERO_THRESHOLD_DEF = ZERO_THRESHOLD;
	private static int SIGNIFICATIVE_DEF = SIGNIFICATIVE;
	private static long DIGITS_DEF = DIGITS; 
	
	/* BACK UP to allow restoring status */
	private static double PRECISION_BCK = PRECISION;
	private static double ZERO_THRESHOLD_EXACT_BCK = ZERO_THRESHOLD_EXACT;
	private static double ZERO_THRESHOLD_APPROX_BCK = ZERO_THRESHOLD_APPROX;
	private static double ZERO_THRESHOLD_BCK = ZERO_THRESHOLD;
	private static int SIGNIFICATIVE_BCK = SIGNIFICATIVE;
	private static long DIGITS_BCK = DIGITS; 
	
	/* Formating block */
	private static boolean FORMAT_NBR = false; //Member Variable. Flag for formatting numbers
	private static boolean FIXED_NOTATION = false; //Member Variable. Flag for comma fixed notation
	private static boolean SCIENTIFIC_NOTATION = false; //Member Variable. Flag for scientific notation
	private static int MAX_DECIMALS_DEFAULT = 8; //Member Variable
	private static int MAX_DECIMALS = MAX_DECIMALS_DEFAULT; //Member Variable
	/* BACK UP to allow restoring status */
	private static boolean FORMAT_NBR_BCK = FORMAT_NBR; //Member Variable. Flag for formatting numbers
	private static boolean FIXED_NOTATION_BCK = FIXED_NOTATION; //Member Variable. Flag for comma fixed notation
	private static boolean SCIENTIFIC_NOTATION_BCK = SCIENTIFIC_NOTATION; //Member Variable. Flag for scientific notation
	private static int MAX_DECIMALS_BCK = MAX_DECIMALS; //Member Variable

	/* REPRESENTATION BLOCK */
	private static Representation REPRESENTATION = Representation.RECTANGULAR; 
	private static Representation REPRESENTATION_BCK = REPRESENTATION; 
	
	private double rep;	// the real part
	private double imp;	// the imaginary part
	private double mod;	// the modulus
	private double pha;	// the phase
	private double cre; // sgn*modulus sgn=any func. Used to compare Complex
	
	private static Random randomNbr = new Random(System.currentTimeMillis());

	/*
	 * ***********************************************
	 * VERSION
	 * ***********************************************
	 */
	
	/*
	 * Prints the version of the class
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION); 
	}

	public static void facts() {
		System.out.println(HEADINFO + "VERSION..............:" + VERSION); 
		showPrecision();
		System.out.println(HEADINFO + "REPRESENTATION.......:" + getRepres()); 
	}

	/*
	 * ***********************************************
	 * CONSTRUCTORS 
	 * ***********************************************
	 */

	/**
	 * Creates a new complex object initialized to 0+0i
	 */
	public Complex() {
		rep = 0.0;
		imp = 0.0;
		mod = 0.0;
		pha = 0.0;
		cre = 0.0;
	}

	/**
	 * Creates a new complex object from its string representation.
	 * @param numC String representing a complex in Rectangular "a+bi" or Polar "a|b" coordinates. a,b are doubles.
	 */
	public Complex(String numC) {
		this.setComplex(numC);
	}

	/**
	 * Creates a new complex object with the given real and imaginary or modulus and phase parts.
	 * @param coord 'C' or 'c' for Rectangular, 'P' or 'p' for Polar.
	 * @param n1 coordinate #1
	 * @param n2 coordinate #2
	 */
	public Complex(char coord, double n1, double n2) {
		this.setComplex(coord, n1, n2);
	}

	/**
	 * Creates a new complex object with the given real and imaginary in Rectangular coordinates parts by default.
	 * @param n1 Real Part.
	 * @param n2 Imaginary Part.
	 */
	public Complex(double n1, double n2) {
		this.setComplex('C', n1, n2);
	}

	/**
	 * Creates a new complex object with the given real part in Rectangular coordinates by default.
	 * @param n1 Real Part.
	 */
	public Complex(double n1) {
		this.setComplex('C', n1, 0);
	}

	/*
	 * ***********************************************
	 * INITIALIZERS & SETTERS
	 * ***********************************************
	 */

	/**
	 * Sets the complex object from its string representation.
	 * @param numC String representing a complex in Rectangular "a+bi" or Polar "a|b" coordinates. a,b are doubles.
	 * @return The Complex Object.
	 */
	public Complex setComplex(String numC) {
		final String recRegExp = "[ \\t]*(([\\+|\\-])?[ \\t]*(\\d*\\.?\\d+(E[\\+|\\-|\\d]\\d*)?))?[ \\t]*(([\\+|\\-])?[ \\t]*(\\d*\\.?\\d+(E[\\+|\\-|\\d]\\d*)?)?)(i)?";
		final String polRegExp = "[ \\t]*(\\d*\\.?\\d+(E[\\+|\\-|\\d]\\d*)?){1}[ \\t]*(\\|){1}[ \\t]*((\\+|\\-)?[\\d]*\\.?[\\d]+?){1}";

		rep = 0.0;
		imp = 0.0;
		mod = 0.0;
		pha = 0.0;
		cre = 0.0;

		numC = numC.replace('e', 'E');
		if (numC.contains("|")) {
			Pattern polPattern = Pattern.compile(polRegExp);
			Matcher polMatcher = polPattern.matcher(numC);
			if (polMatcher.matches()) {
				mod = Double.parseDouble(polMatcher.group(1).toString());
				pha = Double.parseDouble(polMatcher.group(4).toString());
				this.normalizePhase();
				this.setRecCoord();
			} else {
				System.err.println("Invalid number:" + numC);
				System.exit(1);
			}
		}
		else {
			Pattern recPattern = Pattern.compile(recRegExp);
			Matcher recMatcher = recPattern.matcher(numC);
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
					System.err.println("Invalid number:" + numC);
					System.exit(1);
				}
				this.setPolCoord();
			}
			else {
				System.err.println("Not matched number:" + numC);
				System.exit(1);
			}
		}
		return this;
	}

	/**
	 * Private Method. Sets the value of the complex for making Compare REal .
	 * Used to update the Complex Object.
	 */
	private void setCre() {
		//double sgn = this.rep == 0.0 ? Math.signum(this.imp): Math.signum(Math.cos(pha));
		//double sgn = (Math.abs(this.rep) <= ZERO_THRESHOLD_R) ? Math.signum(this.imp): Math.signum(Math.cos(pha));
		if ((Math.abs(this.imp) <= ZERO_THRESHOLD_APPROX)) this.cre = this.rep;
		else {
			//double sgn = Math.signum(this.rep);
			double sgn = Math.signum(this.rep*this.imp);
			this.cre = sgn * this.mod;
		}
	}

	/**
	 * Private Method. Sets the Polar representation of a Complex object from its Rectangular values.
	 * Used to update the Complex Object.
	 */
	private void setPolCoord() {
		this.mod = Math.hypot(this.rep, this.imp); //Math.sqrt(this.rep*this.rep + this.imp*this.imp );
		this.pha = Math.atan2(this.imp, this.rep);
		this.normalizePhase();
		this.setCre();
	}

	/**
	 * Private Method. Sets the Rectangular representation of a Complex object from its Polar values.
	 * Used to update the Complex Object.
	 */
	private void setRecCoord() {
		this.rep = this.mod * Math.cos(this.pha);
		if (this.rePartNull()) this.rep = 0.0;
		this.imp = this.mod * Math.sin(this.pha);
		if (this.imPartNull()) this.imp = 0.0;
		this.setCre();
	}

	/**
	 * Private Method. Sets the Rectangular/Polar representation of a Complex object from its current values.
	 * Used to update the Complex Object .
	 * @param coordType 'C' or 'c' for Rectangular, 'P' or 'p' for Polar.
	 * @param n1 coordinate #1
	 * @param n2 coordinate #2
	 */
	private void setComplex(char coordType, double n1, double n2) {
		switch (coordType) {
		case 'C': // For Cartesian Coordinates
		case 'c':
			this.rep = n1;
			this.imp = n2;
			this.setPolCoord();
			break;
		case 'P': // For Polar Coordinates
		case 'p':
			this.mod = Math.abs(n1);
			this.pha = n2;
			this.normalizePhase();
			this.setRecCoord();
			break;
		default:
			System.err.println("Invalid type of coordinates:" + coordType);
			System.exit(1);   	
		}
	}

	/**
	 * Sets the Real and Imaginary parts of a Complex Object.
	 * @param cRep The Real Part.
	 * @param cImp The Imaginary Part.
	 */
	public void setComplexRec(double cRep, double cImp) {
		this.setComplex('C', cRep, cImp);
	}

	/**
	 * Sets the modulus and Phase of a Complex Object.
	 * @param cMod The Modulus.
	 * @param cPha The Phase in radians.
	 */
	public void setComplexPol(double cMod, double cPha) {
		this.setComplex('P', cMod, cPha);
	}

	/**
	 * Initializes the Complex Object with two random doubles from -base .. base.
	 * @param coordType  'C' or 'c' for Rectangular, 'P' or 'p' for Polar.
	 * @param base Base to generate the random complex.
	 */
	private void setComplexRandom(char coordType, int base) {
		Double n1, n2;
		int sign1, sign2;

		sign1 = Math.random() > 0.5 ? 1 : -1;
		sign2 = Math.random() < 0.5 ? 1 : -1;

		n1 = Math.random() * base * sign1;
		n2 = Math.random() * ((coordType == 'P' || coordType == 'p') ? Math.PI : 1) * base * sign2;
		this.setComplex(coordType, n1, n2);
	}

	private void setComplexRandomInt(char coordType, int base) {
		Double n1, n2;
		int sign1, sign2;

		sign1 = Math.random() > 0.5 ? 1 : -1;
		sign2 = Math.random() < 0.5 ? 1 : -1;

		n1 = Math.random() < 0.5 ? 0.0 : 1 * base * sign1;
		n2 = Math.random() * ((coordType == 'P' || coordType == 'p') ? Math.PI : 1) * base * sign2;
		this.setComplex(coordType, n1, n2);
	}
	
	/**
	 * Initializes the Complex Object in Rectangular with two random doubles from -base .. base.
	 * @param base Base to generate the random complex.
	 */
	public void setComplexRandomRec(int base) {
		this.setComplexRandom('C', base);
	}

	/**
	 * private Method. Returns the closest integer value as a double.
	 * @param value The value.
	 * @return The closest integer value as a double.
	 */
	private double integrize(double value) {
		int sign = value > 0 ? -1 : 1;
		double uval = sign * value;

		return Math.floor(uval) * sign;
	}

	/**
	 * Initializes the Complex Object in Rectangular with two random integers from -base .. base.
	 * @param base Base to generate the random integer.
	 */
	public void setComplexRandomRecInt(int base) {
		this.setComplexRandom('C', base);
		this.setComplex('C',this.integrize(this.rep), this.integrize(this.imp));
	}

	/**
	 * Initializes the Complex Object in Polar with two random doubles from -base .. base.
	 * @param base Base to generate the random complex.
	 */
	public void setComplexRandomPol(int base) {
		this.setComplexRandom('P', base);
	}

	/**
	 * Initializes the Complex Object in Polar with two random integers from -base .. base.
	 * @param base Base to generate the random integer.
	 */
	public void setComplexRandomPolInt(int base) {
		this.setComplexRandom('P', base);
		this.setComplex('P',this.integrize(this.mod), this.pha);
	}

	/**
	 * Initializes the Complex Object in Rectangular with a REAL random double from -base .. base.
	 * Imaginary part is set to 0.
	 * @param base Base to generate the random real.
	 */
	public void setComplexRandomReal(int base) {
		this.setComplexRandom('C', base);
		this.setComplex('C',this.rep, 0);
	}

	/**
	 * Initializes the Complex Object in Rectangular with an INTEGER random from -base .. base.
	 * Imaginary part is set to 0.
	 * @param base Base to generate the random integer.
	 */
	public void setComplexRandomInt(int base) {
		this.setComplexRandom('C', base);
		this.setComplex('C',this.integrize(this.rep), 0);
	}

	/**
	 * Initializes the Complex Object in Rectangular as an IMAGINARY PURE NUMBER with a REAL random double from -base .. base.
	 * Real part is set to 0.
	 * @param base Base to generate the random pure imaginary.
	 */
	public void setComplexRandomImag(int base) {
		this.setComplexRandom('C', base);
		this.setComplex('C', 0, this.imp);
	}

	/**
	 * Initializes the Complex Object in Rectangular as an IMAGINARY PURE NUMBER with an INTEGER random from -base .. base.
	 * Real part is set to 0.
	 * @param base Base to generate the random pure imaginary integer.
	 */
	public void setComplexRandomImagInt(int base) {
		this.setComplexRandom('C', base);
		this.setComplex('C', 0,this.integrize(this.imp));
	}

	/*
	 * ***********************************************
	 * GETTERS
	 * ***********************************************
	 */

	/**
	 * Gets the Complex REal value for comparisons.
	 * @return The Complex REal value.
	 */
	public double cre() { 
		return cre; }

	/**
	 * Gets the real part of the Complex Object.
	 * @return The real part.
	 */
	public double rep() { 
		return rep; }

	/**
	 * Gets the imaginary part of the Complex Object.
	 * @return The imaginary part.
	 */
	public double imp() { 
		return imp; }

	// returns abs/modulus/magnitude
	/**
	 * Gets the modulus of the Complex Object.
	 * @return The modulus.
	 */
	public double mod() { 
		return mod; }

	// returns angle/phase/argument
	/**
	 * Gets the phase of the Complex Object.
	 * @return The phase.
	 */
	public double pha() { 
		return pha; }

	/**
	 * Gets the phase of the Complex Object.
	 * @return The phase.
	 */
	public double phase() { 
		return pha; }

	/**
	 * Gets the absolute value or modulus of the Complex Object.
	 * @return The absolute value or modulus.
	 */
	public double abs() { 
		return mod; }

	/*
	 * ***********************************************
	 * PRESENTATION
	 * ***********************************************
	 */

	/**
	 * Activates the numbers' formatting. This value is GLOBAL. Prints a message in the Console.
	 * @param printStat
	 */
	public static void setFormatON(boolean printStat) { 
		FORMAT_NBR = true;
		if (printStat) printFormatStatus();	
	}

	/**
	 * Activates the numbers' formatting. This value is GLOBAL.
	 */
	public static void setFormatON() { 
		setFormatON(false); 
	}

	/**
	 * Activates the Fixed notation. This value is GLOBAL. Prints a message in the Console.
	 * @param decimals Number of decimals to show
	 * @param printStat
	 */
	public static void setFixedON(int decimals, boolean printStat) { 
		FIXED_NOTATION = true;
		SCIENTIFIC_NOTATION = false;
		MAX_DECIMALS = decimals;
		if (printStat) printFormatStatus();	
	}

	/**
	 * Activates the Fixed notation. This value is GLOBAL.
	 * @param decimals Number of decimals to show
	 */
	public static void setFixedON(int decimals) { 
		setFixedON(decimals, false);
	}

	/**
	 * Activates the Scientific notation. This value is GLOBAL. Prints a message in the Console.
	 * @param decimals Number of decimals to show
	 * @param printStat
	 */
	public static void setScientificON(int decimals, boolean printStat) { 
		FIXED_NOTATION = false;
		SCIENTIFIC_NOTATION = true;
		MAX_DECIMALS = decimals;
		if (printStat) printFormatStatus();	
	}

	/**
	 * Activates the Scientific notation. This value is GLOBAL.
	 * @param decimals Number of decimals to show
	 */
	public static void setScientificON(int decimals) { 
		setScientificON(decimals, false);
	}

	/**
	 * Deactivates the numbers' formatting presentation. This value is GLOBAL. Prints a message in the Console.
	 * @param printStat
	 */
	public static void setFormatOFF(boolean printStat) { 
		FORMAT_NBR = false; 
		if (printStat) printFormatStatus();	
	}

	/**
	 * Deactivates the numbers' formatting presentation. This value is GLOBAL.
	 */
	public static void setFormatOFF() { 
		setFormatOFF(false);
	}

	/**
	 * Gets the Format Number Status
	 * @return True or False
	 */
	public static Boolean getFortmatStatus() {
		return FORMAT_NBR;
	}

	/**
	 * Deactivates the Fixed notation. This value is GLOBAL. Prints a message in the Console.
	 * @param printStat
	 */
	public static void setFixedOFF(boolean printStat) {
		FIXED_NOTATION = false;
		if (printStat) printFormatStatus();	
	}

	/**
	 * Deactivates the Fixed notation. This value is GLOBAL. Prints a message in the Console.
	 */
	public static void setFixedOFF() {
		setFixedOFF(false);	
	}
	
	/**
	 * getFixedStatus
	 * @return
	 */
	public static Boolean getFixedStatus() {
		return FIXED_NOTATION;
	}

	/**
	 * Deactivates the Scientific notation. This value is GLOBAL. Prints a message in the Console.
	 * @param printStat
	 */
	public static void setScientificOFF(boolean printStat) {
		SCIENTIFIC_NOTATION = false;
		if (printStat) printFormatStatus();	
	}
	
	/**
	 * Deactivates the Scientific notation. This value is GLOBAL. Prints a message in the Console.
	 */
	public static void setScientificOFF() {
		setScientificOFF(false);
	}
	
	/**
	 * Gets the Status of the SCIENTIFIC_NOTATION
	 * @return The value of SCIENTIFIC_NOTATION
	 */
	public static Boolean getScientificStatus() {
		return SCIENTIFIC_NOTATION;
	}
	
	/**
	 * Gets the number of decimals 
	 * @return MAX_DECIMALS
	 */
	public static int getMaxDecimals() {
		return MAX_DECIMALS;
	}

	/**
	 * Gets the number of decimals 
	 * @return SIGNIFICATIVE
	 */
	public static int getSignificative() {
		return SIGNIFICATIVE;
	}

	/**
	 * Shows the numbers formatting presentation status. Prints a message in the Console.
	 */
	public static void printFormatStatus() {
		System.out.println( "------------------------------------------------");
		System.out.println( "Formatting numbers is " + (FORMAT_NBR ? "ENABLED" : "DISABLED"));
		System.out.println( "Fixed notation is " + (FIXED_NOTATION ? "ENABLED decimals:" + getMaxDecimals() : "DISABLED"));
		System.out.println( "Scientific notation is " + (SCIENTIFIC_NOTATION ? "ENABLED decimals:" + getMaxDecimals() : "DISABLED"));	
		System.out.println( "------------------------------------------------");
	}

	/**
	 * Stores the Format Status to be restored by restoreFormatStatus
	 */
	public static void storeFormatStatus() {
		FORMAT_NBR_BCK = FORMAT_NBR;
		FIXED_NOTATION_BCK = FIXED_NOTATION;
		SCIENTIFIC_NOTATION_BCK = SCIENTIFIC_NOTATION;
		MAX_DECIMALS_BCK = MAX_DECIMALS;
	}
	
	/**
	 * Restore the Format Status to its previous condition if it was stored before
	 */
	public static void restoreFormatStatus() {
		FORMAT_NBR = FORMAT_NBR_BCK;
		FIXED_NOTATION = FIXED_NOTATION_BCK;
		SCIENTIFIC_NOTATION = SCIENTIFIC_NOTATION_BCK;
		MAX_DECIMALS = MAX_DECIMALS_BCK;
	}

	/**
	 * Reset the format status to the predefined condition
	 */
	public static void resetFormatStatus() {
		FORMAT_NBR = false;
		FIXED_NOTATION = false;
		SCIENTIFIC_NOTATION = false;
		MAX_DECIMALS = MAX_DECIMALS_DEFAULT;
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
	 * Formats the number according with a ZERO_THRESHOLD and SIGNIFICATIVE decimals. 
	 * Tries to return pretty integers keeping the maximum of decimals for reals.
	 * @param number The number to be formatted.
	 * @return The formatted number.
	 */
	private static double formatNbr(double number) {
		if (!FORMAT_NBR) return number;
		if (Math.abs(number) < ZERO_THRESHOLD) return 0.0;
		double newNumber = Math.rint(number * DIGITS) / DIGITS;
		return newNumber;
	}

	/**
	 * Sets the number format from the Locale("en", "UK"). Sets the number of Max an Min digits.
	 */
	public static void setDigits() {
		Locale locale = new Locale("en", "UK");
		NumberFormat numberFormat = NumberFormat.getInstance(locale);
		numberFormat.setMinimumFractionDigits(20); //MAX_DECIMALS);
		numberFormat.setMaximumFractionDigits(20); //MAX_DECIMALS);
	}
	
	/**
	 * Sets the output format of the complex representation
	 * @param Repres
	 */
	public static void setRepres(Representation Repres) {
		REPRESENTATION_BCK = REPRESENTATION;
		REPRESENTATION = Repres;
	}

	/**
	 * Gets the output format of the complex representation
	 */
	public static String getRepres() {
		String Repres = "";
		switch (REPRESENTATION) {
		case RECTANGULAR: Repres = "RECTANGULAR"; break;
		case POLAR: Repres =  "POLAR"; break;
		}
		return Repres;
	}

	/**
	 * Restores the last used output format of the complex representation
	 */
	public static void restoreRepres() {
		Representation oldRepres;
		oldRepres = REPRESENTATION;
		REPRESENTATION = REPRESENTATION_BCK;		
		REPRESENTATION_BCK = oldRepres;
	}

	/**
	 * Private Method. Uses the normalizedPhase_X method selected
	 * @param phase to normalize.
	 * @return phase normalized.
	 */
	private static double normalizePhase(double phase) {
		return normalizePhase_1(phase);
	}

	/**
	 * Private Method. Normalizes the phase between [-pi, pi]
	 * @param phase to normalize.
	 * @return phase normalized.
	 */
	private static double normalizePhase_0(double phase) {
		int sign = phase < 0.0 ? -1 : 1;
		phase *= sign;
		while (phase > Math.PI) phase -= DOS_PI;
		return phase * sign;
	}

	/**
	 * Private Method. Normalizes the phase between (-pi, pi]
	 * @param phase to normalize.
	 * @return phase normalized.
	 */
	private static double normalizePhase_1(double phase) {
		int sign = phase < 0.0 ? -1 : 1;
		phase *= sign;
		while (phase > Math.PI) phase -= DOS_PI;
		if (phase == Math.PI) return Math.PI; 
		return phase * sign;
	}

	/**
	 * Private Method. Normalizes the phase between [0, 2·pi)
	 * @param phase to normalize.
	 * @return phase normalized.
	 */
	private static double normalizePhase_2(double phase) {
		while (phase >= DOS_PI) phase -= DOS_PI;
		while (phase < 0.0) phase += DOS_PI;
		return phase;
	}

	/**
	 * Private Method. Normalizes the Complex Object phase between -pi and pi.
	 */
	private void normalizePhase() {
		this.pha = Complex.normalizePhase(this.pha);
	}

	/**
	 * Returns the Rectangular string representation of the Complex Object.
	 * @return The string representation in Rectangular coordinates.
	 */
	public String toString() {
		String strComplex = "";
		switch (REPRESENTATION) {
		case RECTANGULAR:
			strComplex = this.toStringRec();
			break;
		case POLAR:
			strComplex =  this.toStringPol();
			break;
		}
		return strComplex;
	}

	/**
	 * Builds the string representation of a complex number using scientific notation with MAX_DECIMALS. 
	 * @return The string representation of a complex number using scientific notation.
	 */
	public String toStringRec() {
		return this.toStringRec("i");
	}

	/**
	 * Builds the string representation of a complex number using scientific notation with MAX_DECIMALS. 
	 * @return The string representation of a complex number using scientific notation.
	 */
	public String toStringRecWolfram() {
		String strWolfram = this.toStringRec("I");
		return strWolfram.replace("E", "*10^");
	}
	
	/**
	 * Builds the string representation of a complex number using scientific notation with MAX_DECIMALS. 
	 * @return The string representation of a complex number using scientific notation.
	 */
	private String toStringRec(String imu) {
		double fRep = formatNbr(rep);
		double fImp = formatNbr(imp);
		String sfRep = new String();
		String sfImp = new String();
		
		if (Double.isInfinite(mod)) {
			if (Math.tan(this.pha) >= 0) return ("Infinity");
			else return ("-Infinity");
		}

		if (FORMAT_NBR) {
			// Uses the current value of ZERO_THRESHOLD_APPROX (ZERO_THRESHOLD_R was the before setting)
			if (fImp != 0.0)
				if (Math.abs(fRep/fImp) < ZERO_THRESHOLD_APPROX) fRep = 0.0;
			if (fRep != 0.0)
				if (Math.abs(fImp/fRep) < ZERO_THRESHOLD_APPROX) fImp = 0.0;
		}
		sfRep = String.valueOf(fRep);
		if (SCIENTIFIC_NOTATION) sfRep = String.format("%."+MAX_DECIMALS+"E", fRep).replace(',', '.');
		else if (FIXED_NOTATION) sfRep = String.format("%."+MAX_DECIMALS+"f", fRep).replace(',', '.');
			//else sfRep = String.format("%."+MAX_DECIMALS+"f", fRep).replace(',', '.');

		sfImp = String.valueOf(fImp);
		if (SCIENTIFIC_NOTATION) sfImp = String.format("%."+MAX_DECIMALS+"E", fImp).replace(',', '.');
		else if (FIXED_NOTATION) sfImp = String.format("%."+MAX_DECIMALS+"f", fImp).replace(',', '.');
			//else sfImp = String.format("%."+MAX_DECIMALS+"f", fImp).replace(',', '.');

		if (fImp == 0.0 || Double.parseDouble(sfImp) == 0.0)
			return sfRep + "";
		if (fRep == 0.0 || Double.parseDouble(sfRep) == 0.0)  
			return sfImp + imu;
		if (fImp <  0.0) 
			return sfRep + sfImp + imu;
		return sfRep + "+" + sfImp + imu;
	}

	/**
	 * Returns the Rectangular string representation of the Complex Object. '1.0i' is represented as 'i.
	 * @return The string representation of a complex number in rectangular coordinates.
	 */
	public String toStringRecI() {
		double fRep = rep;
		double fImp = imp;

		if (FORMAT_NBR) {
			// Uses the current value of ZERO_THRESHOLD_APPROX (ZERO_THRESHOLD_R was the before setting)
			if (fImp != 0.0)
				if (Math.abs(fRep/fImp) < ZERO_THRESHOLD_APPROX) fRep = 0.0;
			if (fRep != 0.0)
				if (Math.abs(fImp/fRep) < ZERO_THRESHOLD_APPROX) fImp = 0.0;
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

	/**
	 * Builds the string representation of a complex number using scientific notation with MAX_DECIMALS.
	 * Corrects the VERY VERY BAD fact of having two ways to represent decimals and thousands in English/Latin way using commas for points or viceversa
	 * I would like to promote a worldwide amendment to adopt the English way for number representation and by the way the use of YYYY/MM/DD for dates as default 
	 * @return The string representation of a complex number using scientific notation.
	 */
	public String toStringPol() {
		double fMod = mod;
		double fPha = pha;
		String sfMod = new String();
		String sfPha = new String();

		/*
		if (FORMAT_NBR) {
			if (Math.abs(rep/imp) < ZERO_THRESHOLD_R) {
				fMod = Math.abs(imp);
				fPha = Complex.HALF_PI*Math.signum(imp);
			}
			else if (Math.abs(fImp/fRep) < ZERO_THRESHOLD_R) {
				fMod = Math.abs(rep);
				fPha = 0;
			}			
		}
		*/
		
		if (FORMAT_NBR) {
			if (fMod < ZERO_THRESHOLD) fMod = 0.0;
			if (Math.abs(fPha) < ZERO_THRESHOLD) fPha = 0.0;
			if (fMod == 0.0) fPha = 0.0;
		}
		
		sfMod = String.valueOf(fMod);
		if (SCIENTIFIC_NOTATION) sfMod = String.format("%."+MAX_DECIMALS+"E", fMod).replace(',', '.');
		else if (FIXED_NOTATION) sfMod = String.format("%."+MAX_DECIMALS+"f", fMod).replace(',', '.');
			//else sfMod = String.format("%."+MAX_DIGITS+"f", fMod).replace(',', '.');

		sfPha = String.valueOf(fPha);
		if (SCIENTIFIC_NOTATION || FIXED_NOTATION)sfPha = String.format("%."+MAX_DECIMALS+"f", fPha).replace(',', '.');

		return sfMod + "|" + sfPha;
	}

	/**
	 * Express a complex number in GNUPlot format {<real>,<imag>}, where <real> and <imag> must be numerical constants. 
	 * @return The string representation of a complex number in GNUPlot format 
	 */
	public String toStringGNUPlot() {
		double fRep = formatNbr(rep);
		double fImp = formatNbr(imp);

		// Uses the current value of ZERO_THRESHOLD_APPROX (ZERO_THRESHOLD_R was the before setting)
		if (Math.abs(fRep*ZERO_THRESHOLD_APPROX) > Math.abs(fImp)) fImp = 0.0;
		if (Math.abs(fImp*ZERO_THRESHOLD_APPROX) > Math.abs(fRep)) fRep = 0.0;
		if (fImp == 0) return Double.toString(fRep);
		return "{" + fRep + "," + fImp + "}";
	}

	/**
	 * Prints the complex number to the output console
	 */
	public void print() {
		System.out.print(this.toString());
	}

	public void printRec() {
		System.out.print(this.toStringRec());
	}

	public void printPol() {
		System.out.print(this.toStringPol());
	}

	/**
	 * Prints the complex number with a new line to the output console
	 */
	public void println() {
		System.out.println(this.toString());
	}

	public void printlnRec() {
		System.out.println(this.toStringRec());
	}
	public void printlnPol() {
		System.out.println(this.toStringPol());
	}
	
	/**
	 * Prints the complex number to the output console with a test before it.
	 * @param str The text to put before
	 */
	public void print(String str) {
		System.out.print(str);
		System.out.print(this.toString());
	}

	public void printRec(String str) {
		System.out.print(str);
		System.out.print(this.toStringRec());
	}

	public void printPol(String str) {
		System.out.print(str);
		System.out.print(this.toStringPol());
	}
	/**
	 * Prints the complex number with a new line to the output console with a test before it.
	 * @param str The text to put before
	 */
	public void println(String str) {
		System.out.print(str);
		System.out.println(this.toString());
	}

	public void printlnRec(String str) {
		System.out.print(str);
		System.out.println(this.toStringRec());
	}
	public void printlnPol(String str) {
		System.out.print(str);
		System.out.println(this.toStringPol());
	}

	/*
	 * ***********************************************
	 * PRECISION
	 * ***********************************************
	 */
	
	/**
	 * Shows the Precision parameters used
	 */
	public static void showPrecision() {
		System.out.println(HEADINFO + "MODE.................:" + exact_str()); 
		System.out.println(HEADINFO + "PRECISION............:" + PRECISION); 
		System.out.println(HEADINFO + "ZERO_THRESHOLD.......:" + ZERO_THRESHOLD); 
		System.out.println(HEADINFO + "ZERO_THRESHOLD_EXACT.:" + ZERO_THRESHOLD_EXACT); 
		System.out.println(HEADINFO + "ZERO_THRESHOLD_APPROX:" + ZERO_THRESHOLD_APPROX); 
		System.out.println(HEADINFO + "SIGNIFICATIVE........:" + SIGNIFICATIVE);
		System.out.println(HEADINFO + "DIGITS...............:" + DIGITS);		
		System.out.println(HEADINFO + "LIM_INF..............:" + LIM_INF); 
		System.out.println(HEADINFO + "LIM_NUMDECS..........:" + LIM_NUMDECS); 
		System.out.println(HEADINFO + "LIM_PRECISION........:" + LIM_PRECISION); 
	}

	/**
	 * Gets the EXACT used for calculations.
	 * @return The value of the pseudo-constant EXACT. 
	 */
	public static boolean exact() {
	    return EXACT; 
	}

	/**
	 * Gets the STRING value of EXACT used for calculations.
	 * @return The value of the pseudo-constant EXACT. 
	 */
	public static String exact_str() {
	    return EXACT ? "EXACT" : "APPROXIMATED"; 
	}

	/**
	 * Sets the EXACT used for calculations
	 */
	public static void exact(boolean value) {
	    EXACT = value;
	    ZERO_THRESHOLD = EXACT ? ZERO_THRESHOLD_EXACT : ZERO_THRESHOLD_APPROX;
	}
	
	/**
	 * Gets the PRECISION used for calculations.
	 * @return The value of the constant PRECISION. 
	 */
	public static double precision() {
	    return PRECISION; 
	}

	/**
	 * Sets the PRECISION used for calculations WIHTOUT updating the rest of Precision parameters
	 */
	public static void precision_(double value) {
	    PRECISION = value;
	}

	/**
	 * Sets the PRECISION used for calculations UPDATING the rest of Precision parameters
	 */
	public static void precision(double value) {
		storePrecision();
	    PRECISION = value;
	    ZERO_THRESHOLD_EXACT = PRECISION*10;
	    ZERO_THRESHOLD_APPROX = Math.sqrt(PRECISION);
	    ZERO_THRESHOLD = exact() ? ZERO_THRESHOLD_EXACT : ZERO_THRESHOLD_APPROX;
	    SIGNIFICATIVE = (int)Math.abs(Math.log10(ZERO_THRESHOLD));
	    DIGITS = (long)Math.pow(10, SIGNIFICATIVE);
	}

	/**
	 * Gets the ZERO_THRESHOLD used for calculations.
	 * @return The value of the constant ZERO_THRESHOLD. 
	 */
	public static double zero_treshold() {
	    return ZERO_THRESHOLD; 
	}

	/**
	 * Gets the ZERO_THRESHOLD_EXACT used for calculations.
	 * @return The value of the constant ZERO_THRESHOLD_EXACT. 
	 */
	public static double zero_treshold_exact() {
	    return ZERO_THRESHOLD_EXACT; 
	}

	/**
	 * Sets the ZERO_THRESHOLD_EXACT used for calculations WIHTOUT updating the rest of Precision parameters
	 */
	public static void zero_threshold_exact(double value) {
	    ZERO_THRESHOLD_EXACT = value;
	    // ---- ZERO_THRESHOLD_APPROX = Math.sqrt(ZERO_THRESHOLD_EXACT);
	    ZERO_THRESHOLD = exact() ? ZERO_THRESHOLD_EXACT : ZERO_THRESHOLD_APPROX;
	}

	/**
	 * Sets the ZERO_THRESHOLD_EXACT used for calculations UPDATING the rest of Precision parameters
	 */
	public static void zero_threshold_exact_prec(double value) {
		storePrecision();
		zero_threshold_exact(value);
	    SIGNIFICATIVE = (int)Math.abs(Math.log10(ZERO_THRESHOLD));
	    DIGITS = (long)Math.pow(10, SIGNIFICATIVE);
	}

	/**
	 * Gets the ZERO_THRESHOLD_APPROX used for calculations.
	 * @return The value of the constant ZERO_THRESHOLD_APPROX. 
	 */
	public static double zero_threshold_approx() {
	    return ZERO_THRESHOLD_APPROX; 
	}

	/**
	 * Sets the ZERO_THRESHOLD_APPROX used for calculations
	 */
	public static void zero_threshold_approx(double value) {
	    ZERO_THRESHOLD_APPROX = value;
	    // ---- ZERO_THRESHOLD_EXACT = Math.power(ZERO_THRESHOLD_APPROX, 2);
	    ZERO_THRESHOLD = exact() ? ZERO_THRESHOLD_EXACT : ZERO_THRESHOLD_APPROX;
	}

	/**
	 * Gets the value for zero used in the calculations.
	 * @return Hhe value for zero used in the calculations.
	 */
	public static double zero() {
		return exact() ? ZERO_THRESHOLD_EXACT : ZERO_THRESHOLD_APPROX;
	}
	
	/**
	 * Gets the SIGNIFICATIVE used for calculations.
	 * @return The value of the constant SIGNIFICATIVE. 
	 */
	public static int significative() {
	    return SIGNIFICATIVE; 
	}

	/**
	 * Sets the SIGNIFICATIVE used for calculations WIHTOUT updating the rest of Precision parameters
	 */
	public static void significative_(int value) {
	    SIGNIFICATIVE = value;
	}

	/**
	 * Sets the SIGNIFICATIVE used for calculations UPDATING the rest of Precision parameters
	 */
	public static void significative(int value) {
		storePrecision();
	    SIGNIFICATIVE = value;
	    DIGITS = (long)Math.pow(10, SIGNIFICATIVE);
	}

	/**
	 * Gets the DIGITS used for calculations.
	 * @return The value of the constant DIGITS. 
	 */
	public static long digits() {
	    return DIGITS; 
	}

	/**
	 * Sets the DIGITS used for calculations
	 */
	public static void digits(long value) {
	    DIGITS = value;
	}

	/**
	 * Stores the Precision parameters for recover them later
	 */
	public static void storePrecision() {
	    PRECISION_BCK = PRECISION;
	    ZERO_THRESHOLD_BCK = ZERO_THRESHOLD;
	    ZERO_THRESHOLD_EXACT_BCK = ZERO_THRESHOLD_EXACT;
	    ZERO_THRESHOLD_APPROX_BCK = ZERO_THRESHOLD_APPROX;
	    SIGNIFICATIVE_BCK = SIGNIFICATIVE;
	    DIGITS_BCK = DIGITS;
	}

	/**
	 * Recovers the Precision parameters stored before
	 */
	public static void restorePrecision() {
	    PRECISION = PRECISION_BCK;
	    ZERO_THRESHOLD_EXACT = ZERO_THRESHOLD_EXACT_BCK;
	    ZERO_THRESHOLD_APPROX = ZERO_THRESHOLD_APPROX_BCK;
	    ZERO_THRESHOLD = exact() ? ZERO_THRESHOLD_EXACT : ZERO_THRESHOLD_APPROX;
	    //ZERO_THRESHOLD = ZERO_THRESHOLD_BCK;
	    SIGNIFICATIVE = SIGNIFICATIVE_BCK;
	    DIGITS = DIGITS_BCK;
	}	

	/**
	 * Restores all the precision values to the Factory defined ones
	 */
	public static void restorePrecisionFactorySettings() {
	    PRECISION = PRECISION_DEF;
	    ZERO_THRESHOLD_EXACT = ZERO_THRESHOLD_EXACT_DEF;
	    ZERO_THRESHOLD_APPROX = ZERO_THRESHOLD_APPROX_DEF;
	    ZERO_THRESHOLD = exact() ? ZERO_THRESHOLD_EXACT : ZERO_THRESHOLD_APPROX;
	    //ZERO_THRESHOLD = ZERO_THRESHOLD_DEF;
	    SIGNIFICATIVE = SIGNIFICATIVE_DEF;
	    DIGITS = DIGITS_DEF;
	}	

	/*
	 * ***********************************************
	 * BOXES & TITLES
	 * ***********************************************
	 */
	
	/**
	 * Substitute of String.repeat(int n). This method is not available for Java 1.8 on Windows
	 * @param str The String to repeat
	 * @param n The number of times to repeat the string
	 * @return The final String
	 */
	public static String repeat(String str, int n) {
		String result ="" ;
		for(int i = 0; i < n; ++i) result += str;
		return result;
	}
	
	/**
	 * Prints a random BoxTitle from the ones defined
	 * @param size
	 * @param title
	 */
	public static void printBoxTitleRandom(int size, String title) {
		System.out.println(boxTitleRandom(size, title));
	}
	
	/**
	 * Generates a random BoxTitle from the ones defined
	 * @param size
	 * @param title
	 * @return
	 */
	public static String boxTitleRandom(int size, String title) {
		switch (randomNbr.nextInt(7)+1) {		 
			case 1: return boxTitle1(size, title);
			case 2: return boxTitle2(size, title);
			case 3: return boxTitle3(size, title);
			case 4: return boxTitle4(size, title);
			case 5: return boxTitle5(size, title);
			case 6: return boxTitle6(size, title);
			case 7: return boxTitle7(size, title);
		}
		return boxTitle1(size, title);
	}

	/**
	 * Generates a BoxTitle from it components
	 * @param size
	 * @param title
	 * @param csi
	 * @param top
	 * @param csd
	 * @param msi
	 * @param msd
	 * @param mdi
	 * @param mdd
	 * @param cii
	 * @param bot
	 * @param cid
	 * @param nmid
	 * @return
	 */
	public static String  makeBoxTitle(int size, String title, String csi, String top, String csd, String msi, String msd, String mdi, String mdd, String cii, String bot, String cid, Boolean nmid) {
		String boxTitle = "";
		String theTitleTop;
		String theTitleText;
		String theTitleMid;
		String theTitleBot;
		
		int titleSize = title.length() < size ? size : title.length()+4;
		theTitleTop = csi+repeat(top, titleSize-2)+csd;
		theTitleMid = msi+repeat(" ", titleSize-2)+msd;
		theTitleText = mdi+repeat(" ", ((titleSize-title.length())/2)-2)+" "+title;
		theTitleText += " "+repeat(" ", titleSize-theTitleText.length()-2)+mdd;
		theTitleBot = cii+repeat(bot, titleSize-2)+cid;
		
		boxTitle = theTitleTop+System.lineSeparator()
			+theTitleMid+System.lineSeparator()
			+theTitleText+System.lineSeparator();
		if (nmid) boxTitle += theTitleMid+System.lineSeparator();
		boxTitle += theTitleBot;
		return boxTitle;
	}
		
	/**
	 * Returns a Title inside of a single line box
	 * @param size The minimum size of the box
	 * @param title The text to put inside the box
	 * @return The String representation of the box title
	 */
	public static String boxTitle1(int size, String title) {
		return makeBoxTitle(size, title,
							"_", "_", "_",
							"|", "|",
							"|", "|",
							"|", "_", "|", false);
	}
	
	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle2(int size, String title) {
		return makeBoxTitle(size, title,
							" ", "_", " ",
							"/", "\\",
							"|", "|",
							"\\", "_", "/" , false);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle3(int size, String title) {
		return makeBoxTitle(size, title,
							"_", "_", "_",
							"\\", "/",
							"<", ">",
							"/", "_", "\\", false);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle4(int size, String title) {
		return makeBoxTitle(size, title,
							"+", "-", "+",
							"|", "|",
							"|", "|",
							"+", "-", "+", true);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle5(int size, String title) {
		return makeBoxTitle(size, title,
							"#", "=", "#",
							"I", "I",
							"I", "I",
							"#", "=", "#", true);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle6(int size, String title) {
		return makeBoxTitle(size, title,
							"_", "_", "_",
							"\\",     "/",
							"[",      "]",
							"/", "_", "\\", false);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle7(int size, String title) {
		return makeBoxTitle(size, title,
							"_", "_", "_",
							"\\",     "/",
							"(",      ")",
							"/", "_", "\\", false);
	}

	/**
	 * Prints a Title Box in the standar output 
	 * @param boxId The box Id
	 * @param size The box size
	 * @param title The box title
	 */
	public static void printBoxTitle(int boxId, int size, String title) {
		switch (boxId) {
		case 1: System.out.println(boxTitle1(size, title)); break;
		case 2: System.out.println(boxTitle2(size, title)); break;
		case 3: System.out.println(boxTitle3(size, title)); break;
		case 4: System.out.println(boxTitle4(size, title)); break;
		case 5: System.out.println(boxTitle5(size, title)); break;
		case 6: System.out.println(boxTitle6(size, title)); break;
		case 7: System.out.println(boxTitle7(size, title)); break;
		}
	}
	
	/**
	 * Prints a random BoxText from the ones defined
	 * @param size
	 * @param title
	 */
	public static void printBoxTextRandom(int size, String title) {
		System.out.println(boxTextRandom(size, title));
	}

	/**
	 * Generates a random BoxText from the ones defined
	 * @param size
	 * @param title
	 * @return
	 */
	public static String boxTextRandom(int size, String title) {
		switch (randomNbr.nextInt(7)+1) {		 
			case 1: return boxText1(size, title);
			case 2: return boxText2(size, title);
			case 3: return boxText3(size, title);
			case 4: return boxText4(size, title);
			case 5: return boxText5(size, title);
			case 6: return boxText6(size, title);
			case 7: return boxText7(size, title);
		}
		return boxTitle1(size, title);
	}

	/**
	 * Returns a text inside of a plus-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String makeBoxText(int size, String text, String csi, String top, String csd, String mdi, String mdd) {
		String theBoxTopBot;
		String theBoxText;
		int boxSize = text.length() < size ? size : text.length()+4;
		
		theBoxTopBot = csi+repeat(top, boxSize-2)+csd;
		theBoxText = mdi+repeat(" ", (boxSize-text.length()-2)/2)+text;
		theBoxText += repeat(" ", boxSize-1-theBoxText.length())+mdd;
		
		return theBoxTopBot+System.lineSeparator()+
				theBoxText+System.lineSeparator()+
				theBoxTopBot;
	}

	/**
	 * Returns a text inside of a plus-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText1(int size, String text) {
		return makeBoxText(size, text, 
							"+", "+", "+", 
							"|",      "|");
	}

	/**
	 * Returns a text inside of a plus-minux-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText2(int size, String text) {
		return makeBoxText(size, text, 
				"+", "-", "+", 
				"|",      "|");
	}

	/**
	 * Returns a text inside of a X-*-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText3(int size, String text) {
		return makeBoxText(size, text, 
				"*", "*", "*", 
				"|",      "|");
	}

	/**
	 * Returns a text inside of a hash-equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText4(int size, String text) {
		return makeBoxText(size, text, 
				"#", "=", "#", 
				"|",      "|");
	}

	/**
	 * Returns a text inside of a colon (:) box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText5(int size, String text) {
		return makeBoxText(size, text, 
				":", ":", ":", 
				":",      ":");
	}
	
	/**
	 * Returns a text inside of a colon (:) box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText6(int size, String text) {
		return makeBoxText(size, text, 
				" ", "-", " ", 
				"(",      ")");
	}

	/**
	 * Returns a text inside of a colon (:) box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText7(int size, String text) {
		return makeBoxText(size, text, 
				"·", "-", "·", 
				"[",      "]");
	}

	/**
	 * Pints a box text in the standard output
	 * @param boxId The id ob the text box
	 * @param size The size of the text box
	 * @param text The text
	 */
	public static void printBoxText(int boxId, int size, String text) {
		switch (boxId) {
		case 1: System.out.println(boxText1(size, text)); break;
		case 2: System.out.println(boxText2(size, text)); break;
		case 3: System.out.println(boxText3(size, text)); break;
		case 4: System.out.println(boxText4(size, text)); break;
		case 5: System.out.println(boxText5(size, text)); break;
		case 6: System.out.println(boxText6(size, text)); break;
		case 7: System.out.println(boxText7(size, text)); break;
		}
	}

	/*
	 * ***********************************************
	 * COPY & REPLICATION
	 * ***********************************************
	 */

	/**
	 * Copies the Complex Object values (imp, rep, mod, pha, cre) to a new one.
	 * @return The new Complex Object with the copied values.
	 */
	public Complex copy() {
		Complex cComplex = new Complex();
		cComplex.imp = this.imp();
		cComplex.rep = this.rep();
		cComplex.mod = this.mod();
		cComplex.pha = this.pha();
		cComplex.cre = this.cre();
		return cComplex;
	}

	/*
	 * ***********************************************
	 * UNARY OPERATIONS	import java.util.function.Function;

	 * ***********************************************
	 */

	/**
	 * Returns a new Complex Object with the opposite of this.
	 * @return The new Complex Object with the opposite.
	 */
	public Complex opposite() {
		return new Complex('C', -rep, -imp);
	}

	/**
	 * Returns a new Complex object which value is the conjugate of this.
	 * @return The new Complex Object with the conjugate.
	 */
	public Complex conjugate() {
		return new Complex('C', rep, -imp);
	}

	/** 
	 * Returns a new Complex object which value is the inverse of this (1/this).
	 * @return The new Complex Object with the inverse.
	 */
	public Complex inverse() {
		return new Complex('P', 1/this.mod, -this.pha);
	}

	/** 
	 * Shortcut to inverse.
	 * Returns a new Complex object which value is the inverse of this.
	 * @return The new Complex Object with the reciprocal.
	 */
	public Complex reciprocal() {
		return this.inverse();
	}

	/*
	 * ***********************************************
	 * BOOLEAN OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Cheks if the Complex is infinitely large in magnitude.
	 * @return true if this Complex value is infinitely large in magnitude, false otherwise
	 */
	public boolean isInfinite() {
		Double mod = this.mod();
		return mod.isInfinite();
	}
	
	/**
	 * Checks if the Complex is a Not-a-Number (NaN).
	 * @return true if this Complex value is a Not-a-Number (NaN), false otherwise.
	 */
	public boolean isNaN() {
		Double mod = this.mod();
		return mod.isNaN();
	}
	
	/**
	 * Checks if the Complex is zero.
	 * @return true if this Complex value is zero, false otherwise.
	 */
	public boolean isZero() {
		//if (this.mod() <= ZERO_THRESHOLD) return true;
		if (Math.abs(this.rep()) <= ZERO_THRESHOLD*CORRECTION_FACTOR && Math.abs(this.imp()) <= ZERO_THRESHOLD*CORRECTION_FACTOR) return true;
		else return false;
	}

	/**
	 * Checks if the Complex is reduced zero.
	 * @return true if this Complex value is reduced zero, false otherwise.
	 */
	public boolean isZeroRed__() {
		//if (this.mod() <= ZERO_THRESHOLD_R) return true;
		if (Math.abs(this.rep()) <= ZERO_THRESHOLD_APPROX && Math.abs(this.imp()) <= ZERO_THRESHOLD_APPROX) return true;
		else return false;
	}
	
	/**
	 * Checks if the imaginary part is zero.
	 * @return true if imaginary part is zero, false otherwise.
	 */
	public boolean imPartNull() {
		if (Math.abs(imp/rep) <= ZERO_THRESHOLD*CORRECTION_FACTOR) return true;
		else return false;
	}

	/**
	 * Checks if the imaginary part is reduced zero.
	 * @return true if imaginary part is reduced zero, false otherwise.
	 */
	public boolean imPartNullRed__() {
		if (Math.abs(imp/rep) <= ZERO_THRESHOLD_APPROX) return true;
		else return false;
	}

	/**
	 * Checks if the real part is zero.
	 * @return true if real part is zero, false otherwise.
	 */
	public boolean rePartNull() {
		if (Math.abs(rep/imp) <= ZERO_THRESHOLD*CORRECTION_FACTOR) return true;
		else return false;
	}

	/**
	 * Checks if the real part is reduced zero.
	 * @return true if real part is reduced zero, false otherwise.
	 */
	public boolean rePartNullRed__() {
		if (Math.abs(rep/imp) <= ZERO_THRESHOLD_APPROX) return true;
		else return false;
	}

	/**
	 * Compares the Complex Object with another using the equal operator.
	 * @param cNum Complex to compare.
	 * @return The result of the comparison.
	 */
	public boolean equals(Complex cNum) {
		return this.equals(cNum.rep, cNum.imp);
	}

	/**
	 * Compares the Complex Object with another using the equal operator.
	 * @param cNum Complex to compare.
	 * @return The result of the comparison.
	 */
	public boolean equalsred__(Complex cNum) {
		return this.equalsred__(cNum.rep, cNum.imp);
		//return this.equals(cNum.rep, cNum.imp);
	}

	/**
	 * Compares the Complex Object with another given in Rectangular coordinates using the equal operator.
	 * @param n1 The real part.
	 * @param n2 The imaginary part.
	 * @return The result of the comparison.
	 */
	public boolean equals(double n1, double n2) {
		return ((Math.abs(this.rep - n1) <= ZERO_THRESHOLD*CORRECTION_FACTOR) && (Math.abs(this.imp - n2) <= ZERO_THRESHOLD*CORRECTION_FACTOR));
	}

	/**
	 * Compares with REDUCED THRESHOLD the Complex Object with another given in Rectangular coordinates using the equal operator.
	 * @param n1 The real part.
	 * @param n2 The imaginary part.
	 * @return The result of the comparison.
	 */
	public boolean equalsred__(double n1, double n2) {
		//System.out.println("equalsred REAL:" + Math.abs(this.rep - n1) + " - " + (Math.abs(this.rep - n1) <= ZERO_THRESHOLD_R));
		//System.out.println("equalsred IMAG:" + Math.abs(this.imp - n2) + " - " + (Math.abs(this.imp - n2) <= ZERO_THRESHOLD_R));
		return ((Math.abs(this.rep - n1) <= ZERO_THRESHOLD_APPROX) && (Math.abs(this.imp - n2) <= ZERO_THRESHOLD_APPROX));
	}

	/**
	 * Compares with REDUCED THRESHOLD the Complex Object with another given in Rectangular coordinates using the equal operator with an specific number of precision decimals.
	 * @param n1 The real part.
	 * @param n2 The imaginary part.
	 * @param numDecs The number of precision decimals.
	 * @return The result of the comparison.
	 */
	public boolean equals(double n1, double n2, int numDecs) {
		//System.out.println("equalsred REAL:" + Math.abs(this.rep - n1) + " - " + (Math.abs(this.rep - n1) <= ZERO_THRESHOLD_R));
		//System.out.println("equalsred IMAG:" + Math.abs(this.imp - n2) + " - " + (Math.abs(this.imp - n2) <= ZERO_THRESHOLD_R));
		return ((Math.abs(this.rep - n1) <= ZERO_THRESHOLD*CORRECTION_FACTOR) && (Math.abs(this.imp - n2) <= ZERO_THRESHOLD*CORRECTION_FACTOR));
	}

	/**
	 * Compares with REDUCED THRESHOLD the Complex Object with another given in Rectangular coordinates using the equal operator with an specific number of precision decimals.
	 * @param n1 The real part.
	 * @param n2 The imaginary part.
	 * @param numDecs The number of precision decimals.
	 * @return The result of the comparison.
	 */
	public boolean equalsred__(double n1, double n2, int numDecs) {
		//System.out.println("equalsred REAL:" + Math.abs(this.rep - n1) + " - " + (Math.abs(this.rep - n1) <= ZERO_THRESHOLD_R));
		//System.out.println("equalsred IMAG:" + Math.abs(this.imp - n2) + " - " + (Math.abs(this.imp - n2) <= ZERO_THRESHOLD_R));
		return ((Math.abs(this.rep - n1) <= ZERO_THRESHOLD_APPROX) && (Math.abs(this.imp - n2) <= ZERO_THRESHOLD_APPROX));
	}

	/**
	 * Compares with REDUCED THRESHOLD the Complex Object with another given in Rectangular coordinates using the equal operator with an specific number of precision decimals.
	 * @param cNum The complex number.
	 * @param numDecs The number of precision decimals.
	 * @return The result of the comparison.
	 */
	public boolean equals(Complex cNum, int numDecs) {
		Complex _this_ = Complex.round(this, numDecs);
		Complex _cNum_ = Complex.round(cNum, numDecs);
		//System.out.println("this.rep  :" +  this.rep  + "  this.imp :" +  this .imp);
		//System.out.println("_this_.rep:" + _this_.rep + " _this_.imp:" + _this_.imp);
		//System.out.println(" cNum.rep :" +  cNum .rep + "  cNum.imp :" +  cNum .imp);
		//System.out.println("_cNum_.rep:" + _cNum_.rep + " _cNum_.imp:" + _cNum_.imp);
		return _this_.equals(_cNum_.rep, _cNum_.imp, numDecs);
	}

	/**
	 * Compares with REDUCED THRESHOLD the Complex Object with another given in Rectangular coordinates using the equal operator with an specific number of precision decimals.
	 * @param cNum The complex number.
	 * @param numDecs The number of precision decimals.
	 * @return The result of the comparison.
	 */
	public boolean equalsred__(Complex cNum, int numDecs) {
		Complex _this_ = Complex.round(this, numDecs);
		Complex _cNum_ = Complex.round(cNum, numDecs);
		//System.out.println("this.rep  :" +  this.rep  + "  this.imp :" +  this .imp);
		//System.out.println("_this_.rep:" + _this_.rep + " _this_.imp:" + _this_.imp);
		//System.out.println(" cNum.rep :" +  cNum .rep + "  cNum.imp :" +  cNum .imp);
		//System.out.println("_cNum_.rep:" + _cNum_.rep + " _cNum_.imp:" + _cNum_.imp);
		//return _this_.equalsred(_cNum_.rep, _cNum_.imp, numDecs);
		return _this_.equals(_cNum_.rep, _cNum_.imp, numDecs);
	}

	/**
	 * Checks if a number is pure real
	 * @return True if the number is pure real
	 */
	public Boolean isPureReal() {
		if (rePartNull()) return false;
		if (!imPartNull()) return false;
		return true;
	}

	/**
	 * Checks if a number is pure imaginary
	 * @return True if the number is pure imaginary
	 */
	public Boolean isPureImaginary() {
		if (imPartNull()) return false;
		if (!rePartNull()) return false;
		return true;
	}

	/*
	 * ***********************************************
	 * ARITHMETIC OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Returns a new Complex Object with the addition of 'this' and 'that' (this + that).
	 * @param that The Complex Object to add to 'this'.
	 * @return The new Complex Object with the result of the addition.
	 */
	public Complex plus(Complex that) {
		return new Complex('C', this.rep + that.rep, this.imp + that.imp);
	}

	/**
	 * Returns a new Complex Object with the addition of 'this' and the REAL number 'that' (this + that).
	 * @param that The REAL number to add to 'this'.
	 * @return The new Complex Object with the result of the addition.
	 */
	public Complex plus(double that) {
		return new Complex('C', this.rep + that, this.imp);
	}

	/**
	 * Returns a new Complex Object with the subtraction of 'this' minus 'that' (this - that).
	 * @param that The Complex Object to subtract to 'this'.
	 * @return The new Complex Object with the result of the subtraction.
	 */
	public Complex minus(Complex that) {
		return new Complex('C', this.rep - that.rep, this.imp - that.imp);
	}

	/**
	 * Returns a new Complex Object with the subtraction of 'this' minus the REAL number 'that' (this - that).
	 * @param that The REAL number to subtract to 'this'.
	 * @return The new Complex Object with the result of the subtraction.
	 */
	public Complex minus(double that) {
		return new Complex('C', this.rep - that, this.imp);
	}

	/**
	 * Returns a new Complex Object with the product of 'this' and 'that' (this * that).
	 * @param that The Complex Object to multiply to 'this'.
	 * @return The new Complex Object with the result of the product.
	 */
	public Complex times(Complex that) {
		return new Complex('P', this.mod * that.mod, this.pha + that.pha);
	}

	/**
	 * Returns a new Complex Object with the product of 'this' and the REAL number 'alpha' (this * alpha).
	 * @param alpha The REAL number to multiply to 'this'.
	 * @return The new Complex Object with the result of the product.
	 */
	public Complex times(double alpha) {
		double aMod = Math.abs(alpha);
		double aPha = alpha >= 0.0 ? 0 : Math.PI;
		return new Complex('P', aMod * mod, pha + aPha);
	}

	/**
	 * Returns a new Complex Object with the DOT product of 'this' and 'cNum' (this · cNum).
	 * The DOT product  is defined as the conjugated product (a* times b).
	 * @param cNum The Complex Object to multiply to 'this'.
	 * @return The new Complex Object with the result of the dot product.
	 */
	public Complex dotprod(Complex cNum) {
		return this.conjugate().times(cNum);
	}

	// returns a / b
	/**
	 * Returns a new Complex Object with the division of 'this' between 'that' (this / that).
	 * @param that The Complex Object to divide to 'this'.
	 * @return The new Complex Object with the result of the division.
	 */
	public Complex divides(Complex that) {
		return new Complex('P', this.mod / that.mod, this.pha - that.pha);
	}

	/**
	 * Returns a new Complex Object with the division of 'this' and the REAL number 'alpha' (this * alpha).
	 * @param alpha The REAL number to divide to 'this'.
	 * @return The new Complex Object with the result of the division.
	 */
	public Complex divides(double alpha) {
		double aMod = Math.abs(alpha);
		double aPha = alpha >= 0.0 ? 0 : Math.PI;
		return new Complex('P', this.mod / aMod, this.pha - aPha);
	}
	
	/*
	 * ***********************************************
	 * FUNCTIONS
	 * ***********************************************
	 */

	/**
	 * Calculates the sign of a complex z.
	 * @param z The complex number.
	 * @return the sign of the complex z as a new Complex Object.
	 */
	public static Complex sign(Complex z) {
		Complex sign = new Complex();
		sign.setComplexPol(z.mod(), z.pha());
		if (z.mod == 0 ) {
			sign.setComplexPol(0, 0);
			return sign;
		}
		return sign.divides(sign.mod);	   
	}
	
	/**
	 * Returns the inverse of a Complex number
	 * @param z the complex number
	 * @return the inverse of the Complex number
	 */
	public static Complex inverse(Complex z) {
		return Complex.ONE.divides(z);
	}

	/**
	 * Calculates the ZERO_POSITIVE sign of a complex z, Zero is included as a Positive.
	 * @param z The complex number.
	 * @return the sign of the complex z as a new Complex Object.
	 */
	public static Complex signP(Complex z) {
		Complex sign = new Complex();
		sign.setComplexPol(z.mod(), z.pha());
		if (z.mod == 0 ) sign.setComplexPol(1, 0);
		return sign.divides(sign.mod);
	}

	/**
	 * Calculates the ZERO_NEGATIVE sign of a complex z, Zero is included as a Negative.
	 * @param z The complex number.
	 * @return the sign of the complex z as a new Complex Object.
	 */
	public static Complex signN(Complex z) {
		Complex sign = new Complex();
		sign.setComplexPol(z.mod(), z.pha());
		if (z.mod == 0 ) sign.setComplexPol(-1, 0);
		return sign.divides(sign.mod);
	}

	/**
	 * Calculates the Natural Logarithm of z.
	 * @param z The complex number.
	 * @return The new Complex Object with the Natural Logarithm.
	 */
	public static Complex log(Complex z) {
		return new Complex('C', Math.log(z.mod), z.pha);
	}

	/**
	 * Calculates the Decimal Logarithm of z.
	 * @param z The complex number.
	 * @return The new Complex Object with the Decimal Logarithm.
	 */
	public static Complex log10(Complex z) {
		return new Complex('C', Math.log10(z.mod), z.pha*Math.log10(Math.E));
	}

	/**
	 * Calculates the Logarithm in Complex base of 'z'.
	 * @param z The complex number.
	 * @param base The Complex Object base of the Logarithm.
	 * @return The new Complex Object with the Logarithm in base 'base'.
	 */
	public static Complex logb(Complex z, Complex base) {
		Complex cNum = new Complex('C', Math.log(z.mod), z.pha);
		Complex cDen = new Complex('C', Math.log(base.mod()), base.pha());
		return cNum.divides(cDen); 
	}

	/**
	 * Calculates the Logarithm in Real base of 'z'.
	 * @param z The complex number.
	 * @param base The Real base of the Logarithm.
	 * @return The new Complex Object with the Logarithm in base 'base'.
	 */
	public static Complex logb(Complex z, double base) {
		Complex cBase = new Complex('C', base, 0.0);
		return Complex.logb(z, cBase); 
	}

	/**
	 * Calculates the value of 'this' raised to the Complex 'z'.
	 * @param z The Complex Object to raise 'this'.
	 * @return The new COmplex Object with the value of 'this' raised to 'z'.
	 */
	public Complex power(Complex z) {
		double comExp, comTri;
		
		if (Double.isInfinite(z.mod)) {
			return new Complex('P', z.mod, this.pha * z.rep);
		}
		
		if (this.mod != 0) {
			comExp = Math.exp(z.rep * Math.log(this.mod) - this.pha * z.imp);
			comTri = this.pha * z.rep + z.imp * Math.log(this.mod);
		}
		else {
			comExp = 0;
			comTri = 0;
		}
		return new Complex('P', comExp, comTri);
		//double nRe = comExp * Math.cos(comTri);
		//double nIm = comExp * Math.sin(comTri);
		//return new Complex('C', nRe, nIm);
	}
	
	/**
	 * Calculates the value of 'this' raised to the REAL number 'nExp'.
	 * @param nExp The Complex Object to raise 'this'.
	 * @return The new COmplex Object with the value of 'this' raised to 'nExp'.
	 */
	public Complex power(double nExp) {
		return new Complex('P', Math.pow(this.mod, nExp), nExp * this.pha);  
	}

	/**
	 * Calculates the "1st" pot-root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @param pot The degree of the radical.
	 * @return The "1st" pot-root of the Complex Object 'this'.
	 */
	public static Complex root(Complex z, double pot) {
		return new Complex('P', Math.pow(z.mod, 1/pot), z.pha / pot);  
	}

	/**
	 * Calculates the "k-th" pot-root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @param pot pot The degree of the radical.
	 * @param k The "k-th" root.
	 * @return The "k-th" pot-root of the Complex Object 'this'.
	 */
	public static Complex root(Complex z, int pot, int k) {
		if (k > pot) k %= pot;
		return new Complex('P', Math.pow(z.mod, 1/(double)pot), (z.pha + DOS_PI * k) / pot);  
	}

	/**
	 * Calculates the "1st" square root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @return The "1st" pot-root of the Complex Object 'this'.
	 */
	public static Complex sqrt(Complex z) {
		return root(z, 2);
	}
	
	/**
	 * Calculates the "1st" square root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @return The "1st" pot-root of the Complex Object 'this'.
	 */
	public static Complex sqrroot__(Complex z) {
		return root(z, 2);
	}

	/**
	 * Calculates the "k-th" square root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @param k The "k-th" root.
	 * @return The "k-th" pot-root of the Complex Object 'this'.
	 */
	public static Complex sqrt(Complex z, int k) {
		return root(z, 2, k);
	}

	/**
	 * Calculates the "k-th" square root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @param k The "k-th" root.
	 * @return The "k-th" pot-root of the Complex Object 'this'.
	 */
	public static Complex sqrroot__(Complex z, int k) {
		return root(z, 2, k);
	}
		
	/**
	 * Returns a new Complex Object which value is the z exponential of 'z'.
	 * @param z The complex number.
	 * @return The new Complex Object exponential of 'z'.
	 */
	public static Complex exp(Complex z) {
		return new Complex('C', Math.exp(z.rep) * Math.cos(z.imp), Math.exp(z.rep) * Math.sin(z.imp));
	}

	/**
	 * Returns a new Complex Object which value is the z exponential of 'z'.
	 * @param d double number.
	 * @return The new Complex Object exponential of 'z'.
	 */
	public static Complex exp(double d) {
		Complex z = new Complex(d);
		return new Complex('C', Math.exp(z.rep) * Math.cos(z.imp), Math.exp(z.rep) * Math.sin(z.imp));
	}
	
	/**
	 * Funtion modulus 
	 * @param z
	 * @return The modulus of the complex z
	 */
	public static double mod(Complex z) {
		return z.mod();
	}

	/**
	 * Function absolute value
	 * @param z
	 * @return The modulus of the complex z
	 */
	public static double abs(Complex z) {
		return Complex.mod(z);
	}
	
	/**
	 * Returns a new complex with Re and Im parts positive
	 * @param z
	 * @return a new complex with Re and Im parts positive
	 */
	public static Complex positive(Complex z) {
		return new Complex(Math.abs(z.rep), Math.abs(z.imp));
	}
	
	/**
	 * Checks whether the complex number is an integer/zero or not
	 * @return True if is integer, otherwise false
	 */
	public boolean isInteger() {
		return (isPureReal() && (Math.ceil(rep) == Math.floor(rep)));
	}
	
	/**
	 * Checks whether the complex number is an integer greater than zero or not
	 * @return True if is integer positive, otherwise false
	 */
	public boolean isIntegerPositive() {
		return (rep > 0 && isInteger());
	}
	
	/**
	 * Checks whether the complex number is an integer less than zero or not
	 * @return True if is integer negative, otherwise false
	 */
	public boolean isIntegerNegative() {
		return (rep < 0 && isInteger());	
	}

	/**
	 * Checks whether the complex number is an integer great or equal to zero or not
	 * @return True if is integer positive/zero, otherwise false
	 */
	public boolean isIntegerPositiveZero() {
		return (rep >= 0 && isInteger());
	}
	
	/**
	 * Checks whether the complex number is an integer less or equal to zero or not
	 * @return True if is integer negative/zero, otherwise false
	 */
	public boolean isIntegerNegativeZero() {
		return (rep <= 0 && isInteger());	
	}
	
	/**
	 * 
	 * @param d
	 * @return
	 */
	public static Complex gamma(double d) {
		Complex z = new Complex(d,0);
		return gamma(z);
	}

	/**
	 * 
	 * @param z
	 * @return
	 */
	public static Complex gamma(Complex z) {
		return gamma_fast(z);
	}

	/**
	 * The function gamma optimized for calculations. Selects the best calculator function based on the gamma parameter z
	 * @param z the gamma parameter as Complex
	 * @return the gamma value
	 */
	public static Complex gamma_zones(Complex z) {
		if (z.isIntegerNegativeZero()) {
			double sign = Math.pow(-1.0, Math.ceil(z.rep));
			return ZERO.inverse().times(sign);
		}

		if (z.isPureReal() && z.mod() > 3) {
			if (z.rep() > 0) return gamma_integral(z);
			else return gamma_integral(ONE.minus(z)).inverse().times(Math.PI/Math.sin(z.rep()*Math.PI)); // Use of the gamma reflection property
		}
		else if (z.rep > 5) return gamma_integral(z);
			else return gamma_euler(z);
	}
	
	/**
	 * Gamma function calculated by the integral. Only valid for positive real numbers
	 * @param n the Gamma parameter
	 * @return the gamma value
	 */
	public static Complex gamma_integral(double n) {
		Complex z = new Complex(n,0);
		return gamma_integral(z);
	}

	/**
	 * Gamma function calculated by the integtral of t.power(z.minus(ONE))).times(Complex.exp(t.opposite()) dt
	 * @param z the gamma parameter as Complex
	 * @return the gamma value
	 */
	public static Complex gamma_integral(Complex z) {
		if (z.isIntegerNegativeZero()) {
			double sign = Math.pow(-1.0, Math.ceil(z.rep));
			return ZERO.inverse().times(sign);
		}
		// Valores válidos van desde 50 hasta 100000
		double uplimit = 100; 
		Function<Complex, Complex> gammafunc;
		gammafunc = t -> (t.power(z.minus(ONE))).times(Complex.exp(t.opposite()));
		return Complex.integrate(0,uplimit , gammafunc, 5);
	}
	
	/**
	 * Gamma function calculated by the integtral of Complex.log(s).opposite()).power(z.minus(ONE))ds
	 * Valid only for interval [1,4)
	 * @param z the gamma parameter as Complex
	 * @return the gamma value
	 */
	public static Complex gamma_integral2(Complex z) {
		if (z.isIntegerNegativeZero()) {
			double sign = Math.pow(-1.0, Math.ceil(z.rep));
			return ZERO.inverse().times(sign);
		}
		Function<Complex, Complex> gammafunc;
		gammafunc = s -> (Complex.log(s).opposite()).power(z.minus(ONE));
		return Complex.integrate(ZERO_THRESHOLD_EXACT, 1, gammafunc, 6);
	}
	
	/**
	 * Gamma function calculated by definition from Weierstrass. Valid for any Complex number
	 * @param z the gamma parameter as Complex
	 * @return the gamma value
	 */
	public static Complex gamma_weiertrass(Complex z) {
		int iterations = (int)Math.pow(150/Complex.getMaxDecimals(), getMaxDecimals());
		
		switch (Complex.getMaxDecimals()) {
			case 0:  
			case 1:  //iterations = 50000; break;
			case 2:  //iterations = 100000; break;
			case 3:  //iterations = 500000; break;
			case 4:  iterations = 1000000; break;
			case 5:  iterations = 5000000; break;
			case 6:  iterations = 30000000; break;
			default: iterations = 50000000;
		}
		
		if (z.isIntegerNegativeZero()) {
			double sign = Math.pow(-1.0, Math.ceil(z.rep));
			return ZERO.inverse().times(sign);
		}
		Complex prod = new Complex(1,0);
		Complex zdi = new Complex();
		for (int i = 1; i <= iterations; ++i) {
			zdi = z.divides(i);
			prod = prod.times((zdi.plus(ONE)).inverse().times(Complex.exp(zdi)));
		}
		return Complex.exp(z.times(-EULER_MASC)).divides(z).times(prod);
	}

	/**
	 * Gamma function calculated by definition from Euler's Product. Valid for any Complex number except negative integers
	 * @param z the gamma parameter as Complex
	 * @return the gamma value
	 */
	public static Complex gamma_euler(Complex z) {
		int iterations = (int)Math.pow(150/Complex.getMaxDecimals(), getMaxDecimals());

		switch (Complex.getMaxDecimals()) {
			case 0:  
			case 1:  //iterations = 50000; break;
			case 2:  //iterations = 100000; break;
			case 3:  //iterations = 500000; break;
			case 4:  iterations = 1000000; break;
			case 5:  iterations = 5000000; break;
			case 6:  iterations = 30000000; break;
			default: iterations = 50000000;
		}
		if (z.isIntegerNegativeZero()) {
			double sign = Math.pow(-1.0, Math.ceil(z.rep));
			return ZERO.inverse().times(sign);
		}
		Complex prod = new Complex(1, 0);
		Complex term1;
		Complex term2;

		for (int n = 1; n <= iterations; ++n) {
			//term1 = (ONE.divides(n).plus(1)).power(z); // (1+1/n)^z
			term1 = (ONE.plus(ONE.divides(n))).power(z); // (1+1/n)^z
			term2 = (ONE.plus(z.divides(n))).inverse(); // (1+z/n)⁻¹
			prod = prod.times(term1.times(term2));
		}
		return prod.divides(z);
	}
	
	/**
	 * https://math.stackexchange.com/questions/19236/algorithm-to-compute-gamma-function
	 * http://www.rskey.org/CMS/index.php/the-library/11
	 * @param z
	 * @return
	 */
	public static Complex gamma_nemes(Complex z) {
		Complex Val;
		Complex gammaVal = new Complex(.5*Math.log(DOS_PI));
		gammaVal = gammaVal.plus((z.minus(0.5)).times(Complex.log(z)));
		gammaVal = gammaVal.minus(z);
		Val = (z.times(Complex.sinh(z.inverse())));
		Val = Val.plus((z.power(6).times(810)).inverse());
		gammaVal = gammaVal.plus((z.divides(2)).times(Complex.log(Val)));
		return Complex.exp(gammaVal);
	}
	
	/**
	 * https://es.wikipedia.org/wiki/Aproximaci%C3%B3n_de_Lanczos
	 * @param z
	 * @return
	 */
	public static Complex gamma_fast(Complex z) {
		Complex result = new Complex();
	    double p[] = {676.5203681218851, -1259.1392167224028, 771.32342877765313,
	    			  -176.61502916214059, 12.507343278686905, -0.13857109526572012,
	    			  9.9843695780195716e-6, 1.5056327351493116e-7};
	    if (z.rep < 0.5)
	        result = Complex.PI.divides((sin(Complex.PI.times(z)).times(gamma_fast(Complex.ONE.minus(z)))));
	    else {
	        z = z.minus(1);
	        Complex x = new Complex(0.99999999999980993);
	        for (int i = 0; i < p.length; ++i) { // pval) in enumerate(p):
	        	Complex pval = new Complex(p[i]);
	        	x = x.plus(pval.divides(z.plus(i+1)));
	        }
            Complex t = z.plus(p.length - 0.5);
            result = Complex.sqrt(Complex.DOSPI).times(t.power(z.plus(0.5))).times(Complex.exp(t.opposite()).times(x));
	    }
	    return result;
	}
	
	/**
	 * The factorial for integers
	 * @param n
	 * @return
	 */
	public static double factorial(int n) {
		double fact = 1.0;
		for (int i = 2; i <= n; ++i)
			fact *= i;
		return fact;
	}
	
	/**
	 * Tecomplex factorial as gamma
	 * @param n
	 * @return
	 */
	public static Complex factorial(Complex n) {
		return gamma(n);
	}

	/**
	 * The Beta function
	 * @param p Complex
	 * @param q Complex
	 * @return the Beta of p,q
	 */
	public static Complex beta(Complex p, Complex q) {
		return gamma(p).times(gamma(q)).divides(gamma(p.plus(q)));
	}
	
	/*
	 * http://mrob.com/pub/ries/src/zeta.cpp.txt
	 */
	/**
	 * The Riemann's zeta function. Only for Re(s) > 1
	 * @param s The s parameter of the zeta function
	 * @return The Riemann's zeta function value
	 */
	public static Complex zeta(Complex s) {
		/* * /
		Complex.storeFormatStatus();
		Complex.storePrecision();
		boolean _exact_ = Complex.exact();

		Complex.setFixedON(8);
		Complex.exact(true);
		/* */

		if (s.isZero()) return new Complex(-0.5);
		// if (s.equals(ZERO)) return new Complex(-0.5);
		if (s.rep == 1.0 && s.isPureReal()) return Complex.ONE.divides(0);
		if (s.rep > 2.0) return zeta_re(s);
		if (s.rep < -1.0) return zeta_ext(s);
		return zeta_havil(s);
	}
		
	/**
	 * The Riemann's zeta function. Only for Re(s) > 1
	 * @param s The s parameter of the zeta function
	 * @return The Riemann's zeta function value
	 */
	public static Complex zeta_re(Complex s) {
		Complex z1 = new Complex(0);
		Complex z2 = new Complex(0);
		Complex k = Complex.ONE;
		boolean notFinished = true;
		
		while (notFinished) {
			z1 = z1.plus(k.power(s.opposite()));
			if (z1.equals(z2)) notFinished = false;
			z2 = z1.copy();
			k = k.plus(1);
		}
		return z1;
	}

	/**
	 * The Riemann's zeta function. Only for Re(s) > 1
	 * @param s The s parameter of the zeta function
	 * @return The Riemann's zeta function value
	 */
	public static Complex zeta_ext(Complex s) {
		Complex s_one = s.minus(1);
		Complex one_s = Complex.ONE.minus(s);
		Complex z = Complex.sin(Complex.PI.divides(2).times(s));
		if (z.equals(Complex.ZERO)) return Complex.ZERO;
		z = z.times(new Complex(2).power(s));
		z = z.times(Complex.PI.power(s_one));
		z = z.times(Complex.gamma(one_s));
		z = z.times(zeta(one_s));
		return z;
	}

	/**
	 * TODO
	 * @param s
	 * @return
	 */
	public static Complex zeta_reflex(Complex s) {
		Complex s_one = s.minus(1);
		Complex one_s = Complex.ONE.minus(s);
		Complex z = Complex.cos(Complex.PI.divides(2).times(one_s));
		if (z.equals(Complex.ZERO)) return Complex.ZERO;
		z = z.times(one_s.divides(2*Math.PI*Math.E));
		z = z.times(Complex.sqrt(new Complex(8*Math.PI).divides(one_s)));
		return z;
	}
	
	/**
	 * Solo vale para s.rep() > 1 - Only for Re(s) > 1
	 * @param s
	 * @return
	 */
	public static Complex zeta_primes(Complex s) {
		long prime;
		Scanner sc;
		Complex zPrime = new Complex();
		Complex z = new Complex(1);
		Complex z0 = new Complex(0);
		Complex sOpp = s.opposite();
		Complex epsilon = new Complex();
		String str;
		
		if (s.rep() <= 1.0) return zeta(s);
		
		try {
			sc = new Scanner(new File("./data/primes_n.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Complex.mONE;
		}  
		sc.useDelimiter(" ");   //sets the delimiter pattern 
		while (sc.hasNext())  //returns a boolean value  
		{  
			str = sc.next().trim();
			// System.out.println("str:" + str);
			if (str.isBlank() || str.isEmpty()) break;
			prime = Long.parseLong(str);
			// System.out.println("prime:" + prime);  //find and returns the next complete token from this scanner
			zPrime.setComplexRec(prime, 0);
			z = z.divides(Complex.ONE.minus(zPrime.power(sOpp)));
			epsilon = z.minus(z0);
			//System.out.println("epsilon:" + epsilon.mod()); 
			if (Double.isNaN(epsilon.mod())) break;
			if (epsilon.mod() <= Complex.ZERO_THRESHOLD) break;
			z0 = z.copy();		
		}   
		sc.close();  //closes the scanner  	
		return (z);
	}
	
	/**
	 * The Riemann-Siegel formula for 0 < Re(s) < 1
	 * @param s
	 * @return
	 */
	public static Complex zeta_riemann_siegel(Complex s) {
		double m = Math.sqrt(Math.abs(s.imp()/2/Math.PI));
		Complex dos = new Complex(2);
		Complex one_s = Complex.ONE.minus(s);
		Complex X = dos.power(s).times(Complex.PI.power(s.minus(1))).times(Complex.sin(Complex.PI.times(s).divides(2))).times(Complex.gamma(one_s));
		Complex S1 = new Complex(0);
		Complex S2 = new Complex(0);
		Complex N = new Complex(0);
		for(int n = 1; n <= m; ++n) {
			N.setComplexRec(n, 0);
			S1 = S1.plus((N.power(s)).inverse());
			S2 = S2.plus((N.power(one_s)).inverse());
		}
		return S1.plus(X.times(S2));
	}
	
	//https://www.robertelder.ca/calculatevalue
	//https://mathworld.wolfram.com/RiemannZetaFunction.html
	/**
	 * The Riemann's zeta function. Only for Re(s) > 0
	 * @param s
	 * @return
	 */
	public static Complex zeta_analytic_continuation(Complex s) {
		Complex cDOS = new Complex(2);
		Complex term = Complex.ONE.minus(cDOS.power(Complex.ONE.minus(s)));
		Complex z1 = new Complex(0);
		Complex z2 = new Complex(0);
		Complex k = Complex.ONE;
		boolean notFinished = true;
		
		while (notFinished) {
			z1 = z1.plus((Complex.mONE.power(k.minus(1))).divides(k.power(s)));
			//z1.println("z1 = ");
			if (z1.equals(z2)) notFinished = false;
			z2 = z1.copy();
			k = k.plus(1);
			k.println("k=");
		}
		return z1.divides(term);
	}

	/**
	 * Sondow, Jonathan and Weisstein, Eric W. "Riemann Zeta Function." From MathWorld--A Wolfram Web Resource. 
	 * https://mathworld.wolfram.com/RiemannZetaFunction.html
	 * @param s
	 * @return
	 */
	public static Complex zeta_havil(Complex s) {
		int maxN = 170;
		Complex sum2 = new Complex();
		Complex sum = new Complex();
		Complex sum1 = new Complex();
		for (int n = 0; n < maxN; ++n) {
			sum2 = Complex.ONE.power(s);
			for (int k = 1; k <= n; ++k) {
				sum = (Complex.mONE.power(k).times(Complex.binomialCoef(n, k))).divides((Complex.ONE.plus(k)).power(s));
				sum2 = sum2.plus(sum);
			}
			sum1 = sum1.plus(Complex.ONE.divides(Math.pow(2,n+1)).times(sum2));
		}
		return sum1.divides(Complex.ONE.minus(new Complex(2,0).power(Complex.ONE.minus(s))));
	}
	
	/**
	 * Returns the k zero of the Chebyshev Unary Polynomial of n+1 degree
	 * @param n the number of samples (0..n)
	 * @param k The k term looked after
	 * @return the value of the k zero 
	 */
	public static Complex ChebyshevZero(int n, int k) {
		return Complex.cos(new Complex((2.0*k+1.0)/(2.0*n+2.0)*Math.PI));
	}
	
	/**
	 * The complex binomial coefficient with integer arguments
	 * @param n
	 * @param k
	 * @return
	 */
	public static Complex binomialCoef(int n, int k) {
		if (k >= 0 && k <= n)
			return new Complex(factorial(n)/factorial(k)/factorial(n-k), 0.0);
		else return new Complex(0.0,0.0);
	}
	
	/**
	 * The complex binomial coefficient with integer arguments
	 * @param n
	 * @param k
	 * @return
	 */
	public static Complex binomialCoef(Complex n, Complex k) {
		return gamma(n).divides(gamma(k)).divides(gamma(n.minus(k)));
	}
	
	/*
	 * ***********************************************
	 * TRIGONOMETRICS
	 * ***********************************************
	 */

	/**
	 * Returns a string with the representation of an angle in ° ' "
	 * @param deg The angle in degrees
	 * @return The angle expressed in ° ' "
	 */
	public static String deg_DMS(double deg) {
		int degs, mins;
		String secs;
		double decPart;
		
		degs = (int)deg;
		decPart = deg - degs;
		decPart = decPart * 60.0;
		mins = (int)(decPart);
		secs = String.format("%.3f", (decPart - mins)*60);
		
		return degs + "°" + mins + "'" + secs + "\"";
	}
	
	/**
	 * Returns a string with the representation of an angle in ° ' "
	 * @param rad The angle in radians
	 * @return The angle expressed in ° ' "
	 */
	public static String rad_DMS(double rad) {
		return deg_DMS(Math.toDegrees(rad));
	}
	
	/**
	 * Returns a new Complex Object which value is the sine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object sine of 'z'.
	 */
	public static Complex sin(Complex z) {
		return new Complex('C', Math.sin(z.rep) * Math.cosh(z.imp), Math.cos(z.rep) * Math.sinh(z.imp));
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex sin(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return sin(z);
	}

	/**
	 * Returns a new Complex Object which value is the cosecant of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object cosecant of 'z'.
	 */
	public static Complex csc(Complex z) {
		return sin(z).power(-1);

	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex csc(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return csc(z);
	}

	// returns a new Complex object which value is the z cosine of this
	/**
	 * Returns a new Complex Object which value is the cosine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object cosine of 'z'.
	 */
	public static Complex cos(Complex z) {
		return new Complex('C', Math.cos(z.rep) * Math.cosh(z.imp), -Math.sin(z.rep) * Math.sinh(z.imp));
	}
	
	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex cos(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return cos(z);
	}

	/**
	 * Returns a new Complex Object which value is the secant of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object secant of 'z'.
	 */
	public static Complex sec(Complex z) {
		return cos(z).power(-1);
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex sec(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return sec(z);
	}

	// returns a new Complex object which value is the z tangent of this
	/**
	 * Returns a new Complex Object which value is the tangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object tangent of 'z'.
	 */
	public static Complex tan(Complex z) {
		return sin(z).divides(cos(z));
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex tan(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return tan(z);
	}

	/**
	 * Returns a new Complex Object which value is the cotangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object cotangent of 'z'.
	 */
	public static Complex cot(Complex z) {
		return cos(z).divides(sin(z));	   
	}
	
	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex cot(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return cot(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic sine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic sine of 'z'.
	 */
	public static Complex sinh(Complex z) {
		//sinh(Z) = (cos(b) * ((exp(a) - exp(-a)) / 2) + Sin(b) * ((exp(a) + exp(-a)) / 2)i)
		//sinh(Z) = senh a * cos b + (cosh a * sen b)i
		//return (Complex.exp(z).minus(Complex.exp(z.opposite()))).divides(2);
		double Rep = Math.sinh(z.rep) * Math.cos(z.imp);
		double Imp = Math.cosh(z.rep) * Math.sin(z.imp);
		return new Complex('C', Rep, Imp);
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex sinh(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return sinh(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic cosecant of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic cosecant of 'z'.
	 */
	public static Complex csch(Complex z) {
		return sinh(z).power(-1);
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex csch(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return csch(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic cosine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic cosine of 'z'.
	 */
	public static Complex cosh(Complex z) {
		//cosh(Z) = (cos(b) * ((exp(a) + exp(-a)) / 2) + Sin(b) * ((exp(a) - exp(-a)) / 2)i)
		//cosh(Z) = cosh x * cos b + (sinh a * sen b)i
		//return (Complex.exp(z).plus(Complex.exp(z.opposite()))).divides(2);
		double Rep = Math.cosh(z.rep) * Math.cos(z.imp);
		double Imp = Math.sinh(z.rep) * Math.sin(z.imp);
		return new Complex('C', Rep, Imp);
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex cosh(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return cosh(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic secant of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic secant of 'z'.
	 */
	public static Complex sech(Complex z) {
		return cosh(z).power(-1);
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex sech(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return sech(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic tangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic tangent of 'z'.
	 */
	public static Complex tanh(Complex z) {
		return sinh(z).divides(cosh(z));
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex tanh(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return tanh(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic cotangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic cotangent of 'z'.
	 */
	public static Complex coth(Complex z) {
		return cosh(z).divides(sinh(z));
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	public static Complex coth(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return coth(z);
	}

	/**
	 * Returns a new Complex Object which value is the arcsine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object arcsine of 'z'.
	 */
	public static Complex arcsin(Complex z) {
		Complex i = new Complex(0,1);
		Complex one = new Complex(1,0);
		return log((z.times(i)).plus(root((one.minus(z.power(2))),2))).divides(i);
	}

	/**
	 * Returns a new Complex Object which value is the arccosine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object arccosine of 'z'.
	 */
	public static Complex arccos(Complex z) {
		Complex negi = new Complex(0,-1);
		Complex one = new Complex(1,0);
		return log((z).plus(root((z.power(2).minus(one)),2))).divides(negi);
	}   

	/**
	 * Returns a new Complex Object which value is the arc tangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object arc tangent of 'z'.
	 */
	public static Complex arctan(Complex z) {
		Complex i = new Complex(0,1);
		Complex twoi = new Complex(0,2);
		return log((i.minus(z)).divides(i.plus(z))).divides(twoi);
	}

	/**
	 * Returns a new Complex Object which value is the arc cotangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object arc cotangent of 'z'.
	 */
	public static Complex acotan(Complex z) {
		Complex i = new Complex(0,1);
		Complex twoi = new Complex(0,2);
		return log((z.plus(i)).divides(z.minus(i))).divides(twoi);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic arc sine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic arc sine of 'z'.
	 */
	public static Complex arcsinh(Complex z) {
		return log(z.plus(root((z.power(2).plus(1)),2)));
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic arc cosine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic arc cosine of 'z'.
	 */
	public static Complex arccosh(Complex z) {
		return log(z.plus(root((z.power(2).minus(1)),2)));
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic arc tangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic arc tangent of 'z'.
	 */
	public static Complex arctanh(Complex z) {
		Complex one = new Complex(1,0);
		return log((one.plus(z)).divides(one.minus(z))).divides(2);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic arccotangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic arccotangent of 'z'.
	 */
	public static Complex acoth(Complex z) {
		return log((z.plus(1)).divides(z.minus(1))).divides(2);
	}
	
	public static Complex sinc(Complex z) {
		if (z.isZero()) return Complex.ONE;
		return sin(z).divides(z);
	}
	
	public static Complex cosc(Complex z) {
		return cos(z).divides(z);
	}

	public static Complex tanc(Complex z) {
		return sinc(z).divides(cosc(z));
	}

	/**
	 * Returns the value of the Chebyshev polynomial of degree at a poinnt
	 * @param degree The degree of the polynomial
	 * @param cx The point 
	 * @return The value of the Chebyshev polynomial
	 */
	public static Complex chebyshev(int degree, Complex cx) {
		return Complex.cos(Complex.arccos(cx).times(degree));
	}

	/*
	 * ***********************************************
	 * INTEGRATION & DERIVATION
	 * ***********************************************
	 */
	
	/**
	 * Returns the Riemann integral of a Complex function along of the real line
	 * @param lolimit the lower limit of the integral
	 * @param uplimit the upper limit of the integral
	 * @param func the function to be integrated
	 * @param numDec the number of significant decimals 
	 * @return The value of the integral
	 */
	public static Complex integrate(double lolimit, double uplimit, Function <Complex, Complex> func, int numDec) {
		int iter  = 1;
		double precision = Math.pow(10, -Math.abs(++numDec));
		double step = (uplimit - lolimit) * precision;
		
		Complex integral = new Complex();
		Complex prevPoint = new Complex(lolimit, 0);
		Complex point = new Complex(lolimit + step, 0);
		Complex prevVal = new Complex();
		Complex val = new Complex();
		
		val = func.apply(prevPoint);
		integral = val;
		//System.out.printf("ulimit:%f point:%f val:%s \n", ulimit, prevPoint.mod, val.toString());
		while (uplimit > point.mod) {
			prevVal = func.apply(prevPoint);
			val = func.apply(point);
			integral = integral.plus(val);
			//System.out.printf("ulimit:%f point:%f val:%s \n", ulimit, point.mod, val.toString());
			prevPoint = point;
			point = prevPoint.plus(step);
			++iter;
		}
		return integral.times((uplimit-lolimit)/iter);
	}

	/**
	 * Returns the Riemann integral of a Complex function in the complex plane 
	 * @param slolimit the lower limit of the integral expressed as "a+bi"
	 * @param suplimit the upper limit of the integral expressed as "a+bi"
	 * @param func the function to be integrated
	 * @param numDec the number of significant decimals 
	 * @return The value of the integral
	 */
	public static Complex integrate(String slolimit, String suplimit, Function <Complex, Complex> func, int numDec) {
		Complex lolimit = new Complex(slolimit);
		Complex uplimit = new Complex(suplimit);
		return integrate(lolimit, uplimit, func, numDec);
	}

	/**
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the real axe
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param numDec the number of significant decimals 
	 * @return The value of the integral
	 */
	public static Complex integrate(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, int numDec) {
		Complex vector = uplimit.minus(lolimit);
		double vectSlope = vector.imp/vector.rep;
		double vectAngle = Math.atan(vectSlope);
		double precision = Math.pow(10, -Math.abs(numDec+2));
		
		vectAngle = vectAngle > Math.PI ? Math.PI - vectAngle : vectAngle;
		vectAngle = vectAngle < -Math.PI ? Math.PI + vectAngle : vectAngle;
		
		if (((vectAngle >= Math.PI/4) && (vectAngle < 3*Math.PI/4 )) ||
				((vectAngle >= -3*Math.PI/4) && (vectAngle < -Math.PI/4 ))) {
			return integrateIM(lolimit, uplimit, func, precision);
		}
		else return integrateRE(lolimit, uplimit, func, precision);
	}
	
	/**
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the real axe
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param precision The precision of the result 
	 * @return The value of the integral
	 */
	private static Complex integrateRE(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, double precision) {
		Complex vector = uplimit.minus(lolimit);
		Complex nextPoint = new Complex();
		Complex integral = new Complex();

		//Recorrer la recta con distancia Euclidea
		double vectSlope = vector.imp/vector.rep;
		double vectAngle = Math.atan(vectSlope);
		double projRe = vector.mod * Math.cos(vectAngle);
		double stepRe = projRe * precision * Math.signum(vector.rep);
		double nextRep, nextImp;
		
		int iter = 0;
		nextPoint = lolimit.copy();
		
		/** /
		System.out.println("vectSlope:" + vectSlope);
		System.out.println("vectAngle: PI*" + vectAngle*Math.PI);
		System.out.println("projRe   :" + projRe);
		System.out.println("stepRe   :" + stepRe);
		System.out.println("iter:" + iter + "   nextPoint:" + lolimit.toString());
		/**/

		Complex val = new Complex();
		val = func.apply(lolimit);
		integral = val;

		while (++iter <= 1/precision) {
			//System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
			nextRep = nextPoint.rep + stepRe;
			nextImp = lolimit.imp + vectSlope * (nextRep - lolimit.rep);
			nextPoint.setComplexRec(nextRep, nextImp);
			val = func.apply(nextPoint);
			integral = integral.plus(val);
		}		
		// System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
		return integral.times(uplimit.minus(lolimit)).divides(iter);
	}

	/**
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the imaginary axe
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param precision The precision of the result 
	 * @return The value of the integral
	 */
	private static Complex integrateIM(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, double precision) {
		Complex vector = uplimit.minus(lolimit);
		Complex nextPoint = new Complex();
		Complex integral = new Complex();

		//Recorrer la recta con distancia Euclidea
		double vectSlope = vector.rep/vector.imp;
		double vectAngle = Math.atan(vectSlope);
		double projIm = vector.mod * Math.cos(vectAngle);
		double stepIm = projIm * precision * Math.signum(vector.imp);
		double nextRep, nextImp;
		
		int iter = 0;
		nextPoint = lolimit.copy();
		
		/** /
		System.out.println("vectSlope:" + vectSlope);
		System.out.println("vectAngle: PI*" + vectAngle*Math.PI);
		System.out.println("projIm   :" + projIm);
		System.out.println("stepIm   :" + stepIm);
		System.out.println("iter:" + iter + "   nextPoint:" + lolimit.toString());
		/**/
		
		Complex val = new Complex();
		val = func.apply(lolimit);
		integral = val;

		while (++iter <= 1/precision) {
			//System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
			nextImp = nextPoint.imp + stepIm;
			nextRep = lolimit.rep + vectSlope * (nextImp - lolimit.imp);
			nextPoint.setComplexRec(nextRep, nextImp);
			val = func.apply(nextPoint);
			integral = integral.plus(val);
		}
		// System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
		return integral.times(uplimit.minus(lolimit)).divides(iter);
	}

	/**
	 * Returns the value of the derivative at the point point
	 * @param point the point to calculate the derivative
	 * @param func the complex function to derived
	 * @param precision The precision of the result 
	 * @return the complex value of the derivative at the point
	 */
	static public Complex derivative(Complex point, Function <Complex, Complex> func, double precision) {
		double hComp = Math.pow(10, -precision);
		Complex h = new Complex(hComp, hComp);
		return (func.apply(point.plus(h)).minus(func.apply(point.minus(h)))).divides(h.times(2));
	}

	/**
	 * Returns the value of the derivative at the point point
	 * @param point the point to calculate the derivative
	 * @param func the complex function to derived
	 * @param precision The precision of the result 
	 * @return the complex value of the derivative at the point
	 */
	static public Complex derivative(double point, Function <Complex, Complex> func, double precision) {
		Complex CPoint = new Complex(point, 0);
		return derivative(CPoint, func, precision);
	}

	/*
	 * ***********************************************
	 * ROUND & INT-DEC PARTS OF A NUMBER
	 * ***********************************************
	 */
	
	/**
	 * Gets the decimal part of a double number
	 * @param num The number
	 * @return The decimal part
	 */
	static public double getDecPart(double num) {
		BigDecimal bigDecimal = new BigDecimal(String.valueOf(num));
		int intValue = bigDecimal.intValue();
		return num -  intValue;
	}

	/**
	 * Gets the integer part of a double number
	 * @param num The number
	 * @return The integre part
	 */
	static public double getIntPart(double num) {
		BigDecimal bigDecimal = new BigDecimal(String.valueOf(num));
		int intValue = bigDecimal.intValue();
		return intValue;
	}

	/**
	 * Truncates a double number to decs decimals
	 * @param complex The number to round
	 * @param d The number of decimals
	 * @return The rounded number
	 */
	static public double trunc(double num, int d) {
		String format = "%." + d +"f";
		String strNum = String.format(format, num).replace(",", ".");
		double roundNbr = Double.parseDouble(strNum);
		return roundNbr;
	}

	/**
	 * Truncates a complex from its Rectangular components
	 * @param num Complex to truncate
	 * @param d Nbr of decimals to keep
	 * @return the new truncated complex
	 */
	static public Complex trunc(Complex num, int d) {
		Complex truncated = new Complex();
		truncated.setComplexRec(Complex.trunc(num.rep, d), Complex.trunc(num.imp, d));
		return truncated;
	}
	
	/**
	 * Rounds a double number to decs decimals
	 * @param complex The number to round
	 * @param d The number of decimals
	 * @return The rounded number
	 */
	public static double round(double value, int decs) {
	    if (decs < 0) throw new IllegalArgumentException();

	    Double DBval = value;
	    if (DBval.isNaN()) return value;
		if (DBval.isInfinite()) return value;

	   //BigDecimal bd = new BigDecimal(Double.toString(value));
	    BigDecimal bd = new BigDecimal(DBval);
	    bd = bd.setScale(decs, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * Rounds a complex number to decs decimals using default method
	 * @param complex The number to round
	 * @param decs The number of decimals
	 * @return The rounded number
	 */
	static public Complex round(Complex complex, int decs) {
		if (complex.isNaN()) return complex;
		if (complex.isInfinite()) return complex;
		return roundRec(complex, decs);
	}

	/**
	 * Rounds a complex number to decs decimals using Rectangular Coordinates
	 * @param complex The number to round
	 * @param decs The number of decimals
	 * @return The rounded number
	 */
	static public Complex roundRec(Complex complex, int decs) {
		double rep, imp;
		rep = round(complex.rep, decs);
		imp = round(complex.imp, decs);
		return new Complex(rep, imp);
	}

	/**
	 * Rounds a complex number to decs decimals using Polar Coordinates
	 * @param num The complex number to round
	 * @param decs The number of decimals
	 * @return The rounded complex number
	 */
	static public Complex roundPol(Complex complex, int decs) {
		Complex rndComplex = new Complex();
		rndComplex.setComplexPol(round(complex.mod, decs), round(complex.pha, decs));
		return rndComplex;
	}

	/*
	 * ***********************************************
	 * LIMITS
	 * ***********************************************
	 */

	static int LIM_NUMDECS = 10; // Number of significative decimals for limits calculations
	static double LIM_PRECISION = Math.pow(10, -LIM_NUMDECS);

	/**
	 * Gets the next point in a series for evaluating a function
	 * @param point The point to evaluate the function
	 * @param mult The multiplier for the surrounding point
	 * @param sign The sign of the surrounding point
	 * @return The new point calculated
	 */
	static private Complex nextPoint(Complex point, double mult, int sign) {
		double newMod, newPha;
		Complex nextPoint = new Complex();
		double precision = Complex.LIM_PRECISION; //Complex.PRECISION * 1e5; //1e5; 
		newMod = point.mod + precision * mult * sign;
		newPha = newMod < LIM_PRECISION ? point.pha + Math.PI : point.pha;
		newMod = Math.abs(newMod);
		nextPoint.setComplexPol(newMod, newPha);
		return nextPoint;
	}
	
	/**
	 * Determines if the value of the limit obtained is an indetermination or not
	 * @param limit The value of the limit calculated
	 * @return True is is an indetermination, False if not
	 */
	static private boolean isIndetermination(Complex limit) {
		//if (Double.isNaN(limit.mod)) return true;
		if (limit.isNaN()) return true;
		//if (Double.isNaN(limit.rep) || Double.isNaN(limit.imp)) return true;
		return false;
	}
	
	/**
	 * Compares the values of two limits, usually the limit on the right and the limit on the left, and indicates whether they are the same or not
	 * @param limr The value of the limit on the right
	 * @param liml The value of the limit on the left
	 * @return If both values of the limit are equals or not
	 */
	static private boolean limequ(Complex limr, Complex liml) {
		if (limr.mod == 0 && liml.mod == 0) return true;
		double cocient = limr.mod < liml.mod ? limr.mod/liml.mod : liml.mod/limr.mod;
		if (round(cocient, LIM_NUMDECS-2) == 1) {
			double sin2r = Math.pow(Math.sin(limr.pha),2);
			double cos2r = Math.pow(Math.cos(limr.pha),2);
			double sin2l = Math.pow(Math.sin(liml.pha),2);
			double cos2l = Math.pow(Math.cos(liml.pha),2);
			if (sin2r + cos2l == 1 && cos2r + sin2l == 1) return true;
			return false;
		}
		else return false;
	}
	
	/**
	 * Calculates the limit of func at point of type double
	 * @param func The function to evaluate for the limit
	 * @param point The Complex point in which the function is evaluated
	 * @return The Complex value of the limit
	 */
	static public Complex limit(Function <Complex, Complex> func, Complex point) {
		Complex lastLimit = null;
		Complex limit = func.apply(point);
		if (!Complex.isIndetermination(limit)) {
			System.out.println("NO indetermination");
			return limit;
		}
		//	System.out.println("INDETERMINATION!!!");

		limit = null;
		Complex pointr = new Complex(); 
		Complex pointl = new Complex();
		double mult = 1;
		Complex limr = new Complex();
		Complex liml = new Complex();

		do {
			pointr = nextPoint(point, mult, 1);
			pointl = nextPoint(point, mult, -1);
			limr = round(func.apply(pointr), LIM_NUMDECS/3);
			liml = round(func.apply(pointl), LIM_NUMDECS/3);
			
			//	System.out.println("pointr = " + pointr.toStringPol());
			//	System.out.println("pointl = " + pointl.toStringPol());
			//	System.out.println("limr   = " + limr.toStringPol());
			//	System.out.println("liml   = " + liml.toStringPol());
			
			//if (Double.isInfinite(limr.mod) && Double.isInfinite(liml.mod)) return limr;
			if (limr.isInfinite() && liml.isInfinite()) return limr;
			if (limr.isNaN() && liml.isNaN()) return limr;
			if (limequ(limr,liml)) {
				if (lastLimit != null) {
					if (lastLimit.mod == limr.mod) return limr;
				}
				lastLimit = limr;
			}
			mult *=10;
		} while(mult * LIM_PRECISION < 1);
		
		return limit;
	}

	/**
	 * Calculates the limit of func at point of type Complex
	 * @param func The function to evaluate for the limit
	 * @param point The Complex point in which the function is evaluated
	 * @return The Complex value of the limit
	 */
	static public Complex limit(Function <Complex, Complex> func, double point) {
		Complex Cpoint = new Complex(point,0);
		return limit(func, Cpoint);
	}
	
	/**
	 * Calculates the limit of func at +Infinite or -Inifinite regarding param sign
	 * @param func The function to evaluate for the limit
	 * @param sign The sign of the Infinite
	 * @return The Complex value of the limit
	 */
	static private Complex limit_inf(Function <Complex, Complex> func, int sign) {
		Complex result;
		Complex result2;
		Complex point;
		// 1st - Determine if the function is convergent
		/*************************************************************/
		result = func.apply(new Complex(sign*Double.MAX_VALUE, 0));
		if (!Complex.isIndetermination(result)) {
			if (result.equals(Complex.ZERO)) {
				System.out.println("NO indetermination");
				return result;
			}
		}
		//	System.out.println("INDETERMINATION!!!");
		/*************************************************************/

		// 2nd Try to find the convergence value
		point = new Complex(sign*Complex.LIM_INF, 0);
		result = func.apply(point);
		if (result.isInfinite()) {
			result.pha = func.apply(new Complex(sign*Complex.LIM_INF/1e8, 0)).pha;
			//	System.out.println(" + + + Infinito detectado pha = " + result.pha);
			return result;
		}
		do {
			result2 = result.copy();
			point.setComplexPol(point.mod*2, point.pha);			
			result = func.apply(point);
			//	System.out.println("result2 = " + result2.toStringPol());
			//	System.out.println("result  = " + result.toStringPol());
			// If it grows the cut it
			if ((result.mod-result2.mod) > 0 ) {
				// System.out.println("result.mod-result2.mod)*sign  = " + (result.mod-result2.mod)*sign);
				result = result2;
				break;
			}
			if ((result.mod == 0)) {
				result.setComplexPol(0, 0);
				break;
			}		
		} while (result2.mod/result.mod != 1);
		return result;
	}
	
	/**
	 * Calculates the limit of func at +Infinite
	 * @param func The function to evaluate for the limit
	 * @return The Complex value of the limit
	 */
	static public Complex limit_inf(Function <Complex, Complex> func) {
		return limit_inf(func, 1);
	}
	
	/**
	 * Calculates the limit of func at -Infinite
	 * @param func The function to evaluate for the limit
	 * @return The Complex value of the limit
	 */
	static public Complex limit_Minf(Function <Complex, Complex> func) {
		return limit_inf(func, -1);
	}

	/**
	 * Indicates if the function is continuous in the given point.
	 * @param func The Complex function
	 * @param point The point in which the continuity is evaluated. Complex.
	 * @return True if the function in continuous. False in other case.
	 */
	static public boolean isContinuous(Function <Complex, Complex> func, Complex point) {
		if (limit(func, point) != null) return true;
		return false;
	}
	
	/**
	 * Indicates if the function is continuous in the given point.
	 * @param func The Complex function
	 * @param point The point in which the continuity is evaluated. Double.
	 * @return True if the function in continuous. False in other case.
	 */
	static public boolean isContinuous(Function <Complex, Complex> func, double point) {
		if (limit(func, point) != null) return true;
		return false;
	}
	
	/*
	 * RESTOS
	 */
/**	
	public static Complex integrateCurv(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, int numDec) {
		Complex vector = uplimit.minus(lolimit);
		double precision = Math.pow(10, -Math.abs(++numDec));
		
		Complex integral = new Complex();

		//Recorrer la distancia
		double phiOrigin = lolimit.pha <= uplimit.pha ? lolimit.pha : uplimit.pha;
		double phiEnd = lolimit.pha > uplimit.pha ? lolimit.pha : uplimit.pha;
		double phiStep = (phiEnd - phiOrigin) * precision;
		int iter = 0;
		
		System.out.println("phiOrigin:" + phiOrigin);
		System.out.println("phiEnd   :" + phiEnd);
		System.out.println("phiStep  :" + phiStep);
		
		
		while (phiOrigin+(iter+1)*phiStep < phiEnd) {
			System.out.println("iter:" + iter + "   phiEnd:" + phiEnd + "   phi:" + (phiOrigin+iter*phiStep));
			
			++iter;
		}
		
		return integral;
	}
	**/
}
