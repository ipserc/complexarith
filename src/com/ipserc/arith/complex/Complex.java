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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.Normalizer;
import java.text.NumberFormat;

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
	private final static String VERSION = "1.1 (2021_0208_0000)";

	public final static double PI = Math.PI; 			// 3.1415926535897932384626433832795;
	public final static double TWO_PI = 2 * Math.PI;	// 2 * 3.1415926535897932384626433832795;
	public final static double DOS_PI = TWO_PI;			// 2 * 3.1415926535897932384626433832795;
	public final static double HALF_PI =  Math.PI / 2; 	// 3.1415926535897932384626433832795 / 2;
	public final static Complex i = new Complex(0,1);
	public final static Complex j = i; // For engineers
	public final static Complex ZERO = new Complex(0,0);
	public final static Complex ONE = new Complex(1,0);
	public final static Complex mONE = new Complex(-1,0);
	//public final static Complex _j_ = new Complex(0,1);
	public final static double LIM_INF = 2147483647; //2147483647
	
	private final static double PRECISION = 1E-13;
	private final static double ZERO_THRESHOLD = PRECISION*10;//9.999999999999E-13; //Zero threshold for formatting numbers
	private final static double ZERO_THRESHOLD_R = Math.sqrt(PRECISION);//9.999999999999E-6; //Reduced Zero threshold for formatting numbers 9.999999999999E-3
	private final static int SIGNIFICATIVE = (int)Math.abs(Math.log10(ZERO_THRESHOLD));
	private final static long DIGITS = (long)Math.pow(10, SIGNIFICATIVE); 

	/*
	 * ***********************************************
	 * MEMBER VARS
	 * ***********************************************
	 */
	private static boolean FORMAT_NBR = false; //Member Variable. Flag for formatting numbers
	private static boolean FIXED_NOTATION = false; //Member Variable. Flag for comma fixed notation
	private static boolean SCIENTIFIC_NOTATION = false; //Member Variable. Flag for scientific notation
	private static int MAX_DECIMALS = 3; //Member Variable
	/* BACK UP to allow restoring status */
	private static boolean FORMAT_NBR_BCK = false; //Member Variable. Flag for formatting numbers
	private static boolean FIXED_NOTATION_BCK = false; //Member Variable. Flag for comma fixed notation
	private static boolean SCIENTIFIC_NOTATION_BCK = false; //Member Variable. Flag for scientific notation
	private static int MAX_DECIMALS_BCK = 3; //Member Variable

	private double rep;	// the real part
	private double imp;	// the imaginary part
	private double mod;	// the modulus
	private double pha;	// the phase
	private double cre; // sgn*modulus sgn=any func. Used to compare Complex
	
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

	/*
	 * ***********************************************
	 * CONSTRUCTORS 
	 * ***********************************************
	 */

	/**
	 * Creates a new complex object initialized to 0+0i
	 */
	public Complex() {
		rep = 0;
		imp = 0;
		mod = 0;
		pha = 0;
		cre = 0;
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
		if ((Math.abs(this.imp) <= ZERO_THRESHOLD_R)) this.cre = this.rep;
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
		this.imp = this.mod * Math.sin(this.pha);
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
		double n1, n2;
		int sign1, sign2;

		sign1 = Math.random() > 0.5 ? 1 : -1;
		sign2 = Math.random() < 0.5 ? 1 : -1;

		n1 = Math.random() * base * sign1;
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
	 * Gets the precision used for calculations.
	 * @return The value of the constant PRECISION. 
	 */
	public static double getPrecision() { 
		return PRECISION; }

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
	 * Gets the abs or modulus of the Complex Object.
	 * @return The abs or modulus.
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
		MAX_DECIMALS = 3;
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
	 * Returns a Title inside of a single line box
	 * @param size The minimum size of the box
	 * @param title The text to put inside the box
	 * @return The String representation of the box title
	 */
	public static String boxTitle(int size, String title) {
		String theTitleTop;
		String theTitleText;
		String theTitleMid;
		String theTitleBot;
		
		int titleSize = title.length() < size ? size : title.length()+4;
		theTitleTop = repeat("_", titleSize);
		theTitleText = "|"+repeat(" ", (titleSize-title.length())/2-2)+" "+title;
		theTitleText += " "+repeat(" ", titleSize-theTitleText.length()-2)+"|";
		theTitleMid = "|"+repeat(" ", titleSize-2)+"|";
		theTitleBot = "|"+repeat("_", titleSize-2)+"|";
		return theTitleTop+System.lineSeparator()
			+theTitleMid+System.lineSeparator()
			+theTitleText+System.lineSeparator()
			//+theTitleMid+System.lineSeparator()
			+theTitleBot;	
	}
	
	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle2(int size, String text) {
		String theBoxTopBot;
		String theBoxText;
		String theBoxMid;
		int boxSize = text.length() < size ? size : text.length()+4;
		
		theBoxTopBot = repeat("=", boxSize);
		theBoxText = "|"+repeat(" ", (boxSize-text.length()-2)/2)+text;
		theBoxText += repeat(" ", boxSize-1-theBoxText.length())+"|";
		theBoxMid = "|"+repeat(" ", boxSize-2)+"|";

		return theBoxTopBot+System.lineSeparator()+
				theBoxMid+System.lineSeparator()+
				theBoxText+System.lineSeparator()+
				theBoxMid+System.lineSeparator()+
				theBoxTopBot;
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle3(int size, String text) {
		String theBoxTop;
		String theBoxBot;
		String theBoxText;
		String theBoxMid;
		int boxSize = text.length() < size ? size : text.length()+4;
		
		theBoxTop = " "+repeat("_", boxSize-2)+" ";
		theBoxMid = "/"+repeat(" ", boxSize-2)+"\\";
		theBoxText = "|"+repeat(" ", (boxSize-text.length()-2)/2)+text;
		theBoxText += repeat(" ", boxSize-1-theBoxText.length())+"|";
		theBoxBot = "\\"+repeat("_", boxSize-2)+"/";

		return theBoxTop+System.lineSeparator()+
				theBoxMid+System.lineSeparator()+
				theBoxText+System.lineSeparator()+
				theBoxBot;
	}

	/**
	 * Returns a text inside of a plus-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText(int size, String text) {
		String theBoxTopBot;
		String theBoxText;
		int boxSize = text.length() < size ? size : text.length()+4;
		
		theBoxTopBot = repeat("+", boxSize);
		theBoxText = "|"+repeat(" ", (boxSize-text.length()-2)/2)+text;
		theBoxText += repeat(" ", boxSize-1-theBoxText.length())+"|";
		
		return theBoxTopBot+System.lineSeparator()+
				theBoxText+System.lineSeparator()+
				theBoxTopBot;
	}

	/**
	 * Returns a text inside of a plus-minux-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText2(int size, String text) {
		String theBoxTopBot;
		String theBoxText;
		int boxSize = text.length() < size ? size : text.length()+4;
		
		theBoxTopBot = "+" + repeat("-", boxSize-2)+"+";
		theBoxText = "|"+repeat(" ", (boxSize-text.length()-2)/2)+text;
		theBoxText += repeat(" ", boxSize-1-theBoxText.length())+"|";
		
		return theBoxTopBot+System.lineSeparator()+
				theBoxText+System.lineSeparator()+
				theBoxTopBot;
	}

	/**
	 * Returns a text inside of a X-*-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText3(int size, String text) {
		String theBoxTopBot;
		String theBoxText;
		int boxSize = text.length() < size ? size : text.length()+4;
		
		theBoxTopBot = "O" + repeat("*", boxSize-2)+"O";
		theBoxText = "|"+repeat(" ", (boxSize-text.length()-2)/2)+text;
		theBoxText += repeat(" ", boxSize-1-theBoxText.length())+"|";
		
		return theBoxTopBot+System.lineSeparator()+
				theBoxText+System.lineSeparator()+
				theBoxTopBot;
	}

	/**
	 * Returns a text inside of a hash-equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText4(int size, String text) {
		String theBoxTopBot;
		String theBoxText;
		int boxSize = text.length() < size ? size : text.length()+4;
		
		theBoxTopBot = "#" + repeat("=", boxSize-2)+"#";
		theBoxText = "|"+repeat(" ", (boxSize-text.length()-2)/2)+text;
		theBoxText += repeat(" ", boxSize-1-theBoxText.length())+"|";
		
		return theBoxTopBot+System.lineSeparator()+
				theBoxText+System.lineSeparator()+
				theBoxTopBot;
	}

	/**
	 * Returns a text inside of a colon (:) box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText5(int size, String text) {
		String theBoxTopBot;
		String theBoxText;
		int boxSize = text.length() < size ? size : text.length()+6;
		
		theBoxTopBot = ":" + repeat(":", boxSize-2)+":";
		theBoxText = "::"+repeat(" ", (boxSize-text.length()-2)/2)+text;
		theBoxText += repeat(" ", boxSize-2-theBoxText.length())+"::";
		
		return theBoxTopBot+System.lineSeparator()+
				theBoxText+System.lineSeparator()+
				theBoxTopBot;
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
		numberFormat.setMinimumFractionDigits(MAX_DECIMALS);
		numberFormat.setMaximumFractionDigits(MAX_DECIMALS);
	}

	/**
	 * Private Method. Normalizes the phase between -pi and pi.
	 * @param phase to normalize.
	 * @return phase normalized.
	 */
	private static double normalizePhase(double phase) {
		//double phaNorm  = z.pha > Math.PI ? Math.PI - z.pha : z.pha;
		//phaNorm = z.pha < -Math.PI ? Math.PI + z.pha : z.pha;
		int sign = phase < 0.0 ? -1 : 1;
		phase *= sign;
		while (phase > Math.PI) phase -= DOS_PI;
		return phase * sign;
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
		return this.toStringRec();
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
		return this.toStringRec("i");
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
			if (Math.abs(fRep/fImp) < ZERO_THRESHOLD_R) fRep = 0.0;
			else if (Math.abs(fImp/fRep) < ZERO_THRESHOLD_R) fImp = 0.0;
		}
		sfRep = String.valueOf(fRep);
		if (SCIENTIFIC_NOTATION) sfRep = String.format("%."+MAX_DECIMALS+"E", fRep).replace(',', '.');
		else if (FIXED_NOTATION) sfRep = String.format("%."+MAX_DECIMALS+"f", fRep).replace(',', '.');
			//else sfRep = String.format("%."+MAX_DECIMALS+"f", fRep).replace(',', '.');

		sfImp = String.valueOf(fImp);
		if (SCIENTIFIC_NOTATION) sfImp = String.format("%."+MAX_DECIMALS+"E", fImp).replace(',', '.');
		else if (FIXED_NOTATION) sfImp = String.format("%."+MAX_DECIMALS+"f", fImp).replace(',', '.');
			//else sfImp = String.format("%."+MAX_DECIMALS+"f", fImp).replace(',', '.');

		if (fImp == 0.0 ) 
			return sfRep + "";
		if (fRep == 0.0)  
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
			if (Math.abs(fRep/fImp) < ZERO_THRESHOLD_R) fRep = 0.0;
			else if (Math.abs(fImp/fRep) < ZERO_THRESHOLD_R) fImp = 0.0;
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
		double fRep = rep;
		double fImp = imp;
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
		
		if (fMod == 0) fPha = 0;
		if (Math.abs(fPha) < ZERO_THRESHOLD) fPha = 0;
		
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

		if (Math.abs(fRep*ZERO_THRESHOLD_R) > Math.abs(fImp)) fImp = 0.0;
		if (Math.abs(fImp*ZERO_THRESHOLD_R) > Math.abs(fRep)) fRep = 0.0;
		if (fImp == 0) return Double.toString(fRep);
		return "{" + fRep + "," + fImp + "}";
	}

	/**
	 * Prints the complex number to the output console
	 */
	public void print() {
		System.out.print(this.toString());
	}

	/**
	 * Prints the complex number with a new line to the output console
	 */
	public void println() {
		System.out.println(this.toString());
	}

	/**
	 * Prints the complex number to the output console with a test before it.
	 * @param str The text to put before
	 */
	public void print(String str) {
		System.out.print(str);
		System.out.print(this.toString());
	}

	/**
	 * Prints the complex number with a new line to the output console with a test before it.
	 * @param str The text to put before
	 */
	public void println(String str) {
		System.out.print(str);
		System.out.println(this.toString());
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
	 * UNARY OPERATIONS
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
	 * Returns a new Complex object which value is the reciprocal of this (1/this).
	 * @return The new Complex Object with the reciprocal.
	 */
	public Complex reciprocal() {
		return new Complex('P', 1/this.mod, -this.pha);
	}

	/** 
	 * Shortcut to reciprocal.
	 * Returns a new Complex object which value is the reciprocal of this.
	 * @return The new Complex Object with the reciprocal.
	 */
	public Complex inverse() {
		return this.reciprocal();
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
		if (this.mod() <= ZERO_THRESHOLD) return true;
		else return false;
	}
	
	/**
	 * Checks if the Complex is reduced zero.
	 * @return true if this Complex value is reduced zero, false otherwise.
	 */
	public boolean isZeroRed() {
		if (this.mod() <= ZERO_THRESHOLD_R) return true;
		else return false;
	}
	
	/**
	 * Checks if the imaginary part is zero.
	 * @return true if imaginary part is zero, false otherwise.
	 */
	public boolean imPartNull() {
		if (Math.abs(imp/rep) <= ZERO_THRESHOLD) return true;
		else return false;
	}

	/**
	 * Checks if the imaginary part is reduced zero.
	 * @return true if imaginary part is reduced zero, false otherwise.
	 */
	public boolean imPartNullRed() {
		if (Math.abs(imp/rep) <= ZERO_THRESHOLD_R) return true;
		else return false;
	}

	/**
	 * Checks if the real part is zero.
	 * @return true if real part is zero, false otherwise.
	 */
	public boolean rePartNull() {
		if (Math.abs(rep/imp) <= ZERO_THRESHOLD) return true;
		else return false;
	}

	/**
	 * Checks if the real part is reduced zero.
	 * @return true if real part is reduced zero, false otherwise.
	 */
	public boolean rePartNullRed() {
		if (Math.abs(rep/imp) <= ZERO_THRESHOLD_R) return true;
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
	public boolean equalsred(Complex cNum) {
		return this.equalsred(cNum.rep, cNum.imp);
	}

	/**
	 * Compares the Complex Object with another given in Rectangular coordinates using the equal operator.
	 * @param n1 The real part.
	 * @param n2 The imaginary part.
	 * @return The result of the comparison.
	 */
	public boolean equals(double n1, double n2) {
		return ((Math.abs(this.rep - n1) <= ZERO_THRESHOLD) && (Math.abs(this.imp - n2) <= ZERO_THRESHOLD));
	}

	/**
	 * Compares with REDUCED THRESHOLD the Complex Object with another given in Rectangular coordinates using the equal operator.
	 * @param n1 The real part.
	 * @param n2 The imaginary part.
	 * @return The result of the comparison.
	 */
	public boolean equalsred(double n1, double n2) {
		return ((Math.abs(this.rep - n1) <= ZERO_THRESHOLD_R) && (Math.abs(this.imp - n2) <= ZERO_THRESHOLD_R));
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
	public static Complex sqrroot(Complex z) {
		return root(z, 2);
	}

	/**
	 * Calculates the "k-th" square root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @param k The "k-th" root.
	 * @return The "k-th" pot-root of the Complex Object 'this'.
	 */
	public static Complex sqrroot(Complex z, int k) {
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
	 * Returns a new Complex Object which value is the cosecant of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object cosecant of 'z'.
	 */
	public static Complex csc(Complex z) {
		return sin(z).power(-1);

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
	 * Returns a new Complex Object which value is the secant of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object secant of 'z'.
	 */
	public static Complex sec(Complex z) {
		return cos(z).power(-1);
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
	 * Returns a new Complex Object which value is the cotangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object cotangent of 'z'.
	 */
	public static Complex cot(Complex z) {
		return cos(z).divides(sin(z));	   
	}
	
	/**
	 * Returns a new Complex Object which value is the hyperbolic sine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic sine of 'z'.
	 */
	public static Complex sinh(Complex z) {
		//sinh(Z) = (cos(b) * ((exp(a) - exp(-a)) / 2) + Sin(b) * ((exp(a) + exp(-a)) / 2)i)
		//sinh(Z) = senh a * cos b + (cosh a * sen b)i
		double Rep = Math.sinh(z.rep) * Math.cos(z.imp);
		double Imp = Math.cosh(z.rep) * Math.sin(z.imp);
		return new Complex('C', Rep, Imp);
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
	 * Returns a new Complex Object which value is the hyperbolic cosine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic cosine of 'z'.
	 */
	public static Complex cosh(Complex z) {
		//cosh(Z) = (cos(b) * ((exp(a) + exp(-a)) / 2) + Sin(b) * ((exp(a) - exp(-a)) / 2)i)
		//cosh(Z) = cosh x * cos b + (sinh a * sen b)i
		double Rep = Math.cosh(z.rep) * Math.cos(z.imp);
		double Imp = Math.sinh(z.rep) * Math.sin(z.imp);
		return new Complex('C', Rep, Imp);
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
	 * Returns a new Complex Object which value is the hyperbolic tangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic tangent of 'z'.
	 */
	public static Complex tanh(Complex z) {
		return sinh(z).divides(cosh(z));
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
	 * Rounds a double number to decs decimals
	 * @param num The number to round
	 * @param decs The number of decimals
	 * @return The rounded number
	 */
	static public double round(double num, int decs) {
		String format = "%." + decs +"f";
		String strNum = String.format(format, num).replace(",", ".");
		double round = Double.parseDouble(strNum);
		return round;
	}

	/**
	 * Rounds a complex number to decs decimals
	 * @param num The complex number to round
	 * @param decs The number of decimals
	 * @return The rounded complex number
	 */
	static public Complex round(Complex num, int decs) {
		Complex rndComplex = new Complex();
		rndComplex.setComplexPol(round(num.mod, decs), num.pha);
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
		newPha = newMod < LIM_PRECISION ? point.pha + Complex.PI : point.pha;
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
