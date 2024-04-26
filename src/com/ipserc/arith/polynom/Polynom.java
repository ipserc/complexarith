/**
 * /usr/lib/jvm/java-11-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin TestComplex.plotPolynom
 */
package com.ipserc.arith.polynom;
/**
 * @author ipserc
 *
 */
import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.syseq.Syseq;
import com.ipserc.geom.*;
import java.util.ArrayList;
import java.util.List;
import com.panayotis.gnuplot.JavaPlot;

public class Polynom extends MatrixComplex {
	private Complex[][] polyNorm;
	private Polynom remainder;

	public static int sampleBase = 300;		// The number of samples used to plot te grapic
	public static int maxRootIter = 5000;

	private final static String HEADINFO = "Polynom --- INFO: ";
	private final static String VERSION = "1.5 (2024_0114_0200)";
	/* VERSION Release Note
	 * 1.5 (2024_0114_0200)
	 * public Complex eval(Complex value) 
	 * public Complex eval(double value)
	 * public Complex evalHorner(Complex value)
	 * public Complex evalHorner(double value)
	 * public Complex evalFact(Complex value) 
	 * public Complex evalFact(double value) 
	 * 
	 * 1.4 (2023_0528_1900)
	 * public Polynom power(int pot): Calculates the polynomial to the nth power
	 * public Polynom hermiteI(int degree) : Added just for fun hermite(n) = 2n*hermite(n-1)i - 2(n-1)*hermite(n-2)
	 * public Polynom legendreI(int degree) : Added just for fun (n+1)*legendre(n+1) = (2n+1)·x*legendre(n)i - n*legendre(n-1)
	 * public void plot(List<double[][]> pointsList, String title)
	 * public void plotRe(List<MatrixComplex> pointsList, String title)
	 * public void plotIm(List<MatrixComplex> pointsList, String title)
	 * public MatrixComplex coefMatrix()
	 * private MatrixComplex walkIntervalRE(Complex lolimit, Complex uplimit, int samples) 
	 * private MatrixComplex walkIntervalIM(Complex lolimit, Complex uplimit, int samples) 
	 * public MatrixComplex walkInterval(Complex lolimit, Complex uplimit, int samples) 
	 * public MatrixComplex walkInterval(double lolimit, double uplimit, int samples) 
	 * private Complex slope(int i, int j, MatrixComplex points) 
	 * private Complex f(int order, int i, MatrixComplex points) 
	 * public Polynom interpolationNewton(MatrixComplex points) 
	 * public String toWolfram_poly() : replace("E", "*10^")
	 * public static double sampleBase = 300;
	 * public static int maxRootIter = 5000;
	 * public Polynom L(int k, MatrixComplex points)
	 * public Polynom interpolationLagrange(MatrixComplex points)
	 * public void plotExpression(String GNUplotExpression,double loLimit, double upLimit) {
	 * public void plotExpression(double loLimit, double upLimit) {
	 * public void plotExpressionRe(double loLimit, double upLimit) {
	 * public void plotExpressionIm(double loLimit, double upLimit) {
	 * public void plotExpressionReIm(double loLimit, double upLimit) {
	 * public void plotExpressionRepIm(double loLimit, double upLimit) {
	 * public void plotExpressionAbs(double loLimit, double upLimit) {
	 * public void plotExpressionPhase(double loLimit, double upLimit) {
	 * public void plot(List<double[][]> pointsList, String title) {
	 * public void plotRe(List<MatrixComplex> pointsList, String title) {
	 * public void plotIm(List<MatrixComplex> pointsList, String title) {
	 * public MatrixComplex walkInterval(Complex lolimit, Complex uplimit) {
	 * public Polynom normalize() {
	 * public Polynom fromRoots(MatrixComplex rootMatrix) {
	 * public void plotRe(MatrixComplex points, String title) {
	 * public void plotIm(MatrixComplex points, String title) {
	 * public void plotMod(MatrixComplex points, String title) {
	 * public void plotPha(MatrixComplex points, String title) {
	 * 
	 * 1.3 (2022_0123_0100)
	 * toWolfram_roots() --> roots[...]
	 * public MatrixComplex solveWeierstrass(double theprecision) Various adjustments to the precision and rounding rules for more precision on roots. 
	 * 
	 * 1.2 (2021_0929_2000)
	 * solveWeierstrass uses Complex.round(root,numOfDecs) to return the value of each root found. This gives a more precise value of the root. The numOfDecs is calculated by the method based on the
	 * PRECISION defined in Complex using the following rule:
	 * 		double maxPrec = Math.sqrt(precision);
	 *		int numOfDecs = (int) Math.abs(Math.log10(maxPrec));
	 *
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
	 * Instantiates a null polynomial.
	 */
	public Polynom() {
		super();
	}

	/**
	 * Instantiates a "degree" degree polynomial with its coefficients to zero.
	 * Instantiates a MatrixComplex object in a row with "degree + 1" columns representing a "degree" degree polynomial.
	 * The polynomial is stored with the coefficients according to the numbering of the indexes of the columns.
	 * Poly [0], is the independent term.
	 * Poly [1], is the term in x.
	 * Poly [2], is the term in x ^ 2.
	 * ...
	 * Poly [n], is the term in x ^ n.
	 * @param degree The degree of the polynomial.	 
	 */
	public Polynom(int degree) {
		super(1, degree+1);
	}

	/**
	 * Constructs a new polynomial in a natural way as a string of coefficients separated by commas.
	 * This means that the coefficient of highest degree is in the column of zero id.
	 * The rest of the coefficients are entered from that column.
	 * The constructor inverts the order of the coefficients to place them according to the index of the column so that they coincide with the degree of term to which they are associated.
	 * @param cadena The coefficients of the polynomial in natural order as a string separated by commas.
	 */
	public Polynom(String cadena) {
		super(cadena);
		this.reverse(); // The array is inverted to adecuate it with the columns indexes
	}

	/**
	 * Allows the entry of a new polynomial in a natural way as a string of coefficients separated by commas.
	 * This means that the coefficient of highest degree is in the column of zero id.
	 * The rest of the coefficients are entered from that column.
	 * The constructor inverts the order of the coefficients to place them according to the index of the column so that they coincide with the degree of term to which they are associated.
	 * @param cadena The coefficients of the polynomial in natural order as a string separated by commas.
	 * @return Pointer to the matrix with the polynomial coefficients in the order of the columns.
	 */
	public MatrixComplex newPolynom(String cadena) {
		Polynom polynom = new Polynom(cadena); 
		polynom.reverse(); // The array is reversed to adecuate it with the columns indexes
		return polynom;   	
	}

	/**
	 * Private method. Toggle column elements to sort them according to the column index.
	 * It is necessary to do so that the polynomial is managed with the complex matrices methods.
	 * The order of the coefficients in the matrix will match with the degree of the coefficients.
	 * Poly [0], is the independent term.
	 * Poly [1], is the term in x.
	 * Poly [2], is the term in x ^ 2.
	 * ...
	 * Poly [n], is the term in x ^ n.
	 */
	private void reverse() {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex swap = new Complex();
		int limit = (colLen / (int)2);

		//limit = if ((x & 1) == 0 ) { even... } else { odd... }
		for (int row = 0; row < rowLen; ++row)
			for (int col = 0; col < limit; ++col) {
				swap = this.complexMatrix[row][col];
				this.complexMatrix[row][col] = this.complexMatrix[row][colLen-1-col];
				this.complexMatrix[row][colLen-1-col] = swap;
			}		
	}

	/*
	 * ***********************************************
	 * SETTERS & GETTERS
	 * ***********************************************
	 */

	/**
	 * Returns the remainder resultant of the polynomial division associated to the quotient.
	 * @return The polynomial remainder.
	 */
	public Polynom getRemainder() {
		return remainder;
	}

	/**
	 * Sets the class's member variable "remainder" to a specific polynomial.
	 * @param theRemainder The polynomial remainder to set.
	 */
	public void setRemainder(Polynom theRemainder) {
		remainder = theRemainder.copy();
	}

	/**
	 * Sets the class's member variable "sampleBase" to a specific value.
	 * sampleBase is the number of samples per unity taking when plotting
	 * @param value the number of samples per unity
	 */
	public void setSampleBase(int value) {
		sampleBase = value;
	}

	/**
	 * Gets the current value of the class's member variable "sampleBase".
	 * @return the value of "sampleBase".
	 */
	public double getSampleBase() {
		return sampleBase;
	}
	
	/*
	 * ***********************************************
	 * PRINTING
	 * ***********************************************
	 */

	/**
	 * Displays the polynomial as a string of the form A[n]x^n + A[n-1]x^(n-1) + ... + A[2]x^2 + A[1]x + A[0].
	 */
	public void print() {
		System.out.print(this.toString()); //+ "=0\n");
	}

	/**
	 * Displays the polynomial as a string of the form A[n]x^n + A[n-1]x^(n-1) + ... + A[2]x^2 + A[1]x + A[0] with a carriage return.
	 */
	public void println() {
		System.out.println(this.toString()); //+ "=0\n");
	}

	/**
	 * Constructs the polynomial as string as the one used by Maxima (Computer Algebra System)
	 * @return The Maxima's polynomial representation as a string.
	 */
	public String toMaxima_poly() {
		String polynom = new String(); 

		polynom = this.toString();
		polynom = polynom.replace("i", "*%i");
		polynom = polynom.replace("x", "*x");
		return polynom;
	}
	/**
	 * Constructs the polynomial as string as the one used by Maxima (Computer Algebra System) appending a text before
	 * @param text The text at the beginning of the string
	 * @return The Maxima's polynomial representation as a string.
	 */
	public String toMaxima_poly(String text) {
		return text + this.toMaxima_poly();
	}
	
	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used by Maxima (Computer Algebra System)
	 * @return The Maxima's allroots command for a polynomial.
	 */
	public String toMaxima_roots() {
		return "allroots(" + this.toMaxima_poly() + ")";
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used by Maxima (Computer Algebra System) appending a text before
	 * @param text The text at the beginning of the string
	 * @return The Maxima's allroots command for a polynomial.
	 */
	public String toMaxima_roots(String text) {
		return text + this.toMaxima_roots();
	}

	/**
	 * Constructs the polynomial as string as the used by GNUPlot.
	 * @return The GNUPlot's polynomial representation as a string.
	 */
	public String toGNUPlot_poly() {
		int rowLen = this.rows();   
		int colLen = this.cols();
		Complex cCoef = new Complex();
		String polynom = new String(); 

		for (int row = 0; row < rowLen; ++row) {
			for (int col = colLen-1; col >= 0; --col) {
				cCoef = this.complexMatrix[row][col];				
				//if (cCoef.mod() == 0.0) continue;
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
	 * Constructs the polynomial as string as the one used by Wolfram
	 * @return The Wolfram Mathematica's polynomial representation as a string.
	 */
	public String toWolfram_poly() {
		String polynom = new String(); 

		polynom = this.toString();
		return polynom.replace("E", "*10^");
	}

	/**
	 * Constructs the polynomial as string as the one used by Wolfram appending a text before
	 * @param text The text at the beginning of the string
	 * @return The Wolfram Mathematica's polynomial representation as a string.
	 */
	public String toWolfram_poly(String text) {
		return text + toWolfram_poly();	
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used by Wolfram
	 * @return The Wolfram's roots command for a polynomial.
	 */
	public String toWolfram_roots() {
		return "roots[" + toWolfram_poly() + "]";	
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used by Wolfram appending a text before
	 * @param text The text at the beginning of the string
	 * @return The Wolfram's roots command for a polynomial.
	 */
	public String toWolfram_roots(String text) {
		return text + toWolfram_roots();	
	}

	/**
	 * Constructs the polynomial as string as the used by GNU Octave.
	 * @return The Octave polynomial representation as a string.
	 */
	public String toOctave_poly() {
		String polynom = new String();
		Polynom newPol = new Polynom();

		newPol = this.copy();
		newPol.reverse();
		polynom = newPol.toOctave();

		return polynom;
	}

	/**
	 * Constructs the polynomial as string as the used by GNU Octave appending a text before
	 * @param text The text at the beginning of the string
	 * @return The Octave polynomial representation as a string.
	 */
	public String toOctave_poly(String text) {
		return text + this.toOctave_poly();	
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used by Octave
	 * @return The Octave's roots command for a polynomial.
	 */
	public String toOctave_roots() {
		return "roots(" + this.toOctave_poly() +")";	
	}

	/**
	 * Constructs the expression for finding the roots of a polynomial as string as the one used by Octave  appending a text before
	 * @param text The text at the beginning of the string
	 * @return The Octave's roots command for a polynomial.
	 */
	public String toOctave_roots(String text) {
		return text + this.toOctave_roots();	
	}

	/**
	 * Displays the coefficients of the polynomial as a vector.
	 */
	public String toCoefs() {
		int rowLen = this.rows();
		int colLen = this.cols();
		String polyCoefs = new String(); 

		for (int row = 0; row < rowLen; ++row) {
			polyCoefs += ("(");
			for (int col = colLen-1; col >= 0; --col) {
				polyCoefs += this.complexMatrix[row][col].toString();
				polyCoefs += (col == 0 ? ")" : ",");
			}
		}
		return polyCoefs;
	}

	/**
	 * Builds the string that represents a polynomial constructor.
	 * @return The string with the constructor statement of the polynomial.
	 */
	public String toPolynom() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int row, col;
		String polynom = new String(); 

		polynom = "new Polynom(\"";
		for (row = 0; row < rowLen - 1; ++row) {
			for (col = colLen-1; col >= 0; --col) {
				polynom += this.complexMatrix[row][col].toString();
				polynom += col == 0 ? ";" : ",";
			}
		}	
		for (col = colLen-1; col >= 0; --col) {
			polynom += this.complexMatrix[row][col].toString();
			polynom += col == 0 ? "\");" : ",";
		}
		return polynom;
	}

	/**
	 * Builds the string that represents a polynomial constructor with a text in front of it.
	 * @param text The text at the beginning of the string
	 * @return The string with constructor instruction
	 */
	public String toPolynom(String text) {
		return text + this.toPolynom();	
	}
	
	/**
	 * Builds the string representation of the polynomial in the form A[n]x^n + A[n-1]x^(n-1) + ... + A[2]x^2 + A[1]x + A[0].
	 * @return The string representation of the polynomial.
	 */
	public String toString() {
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cCoef = new Complex();
		String polynom = new String(); 

		for (int row = 0; row < rowLen; ++row) {
			for (int col = colLen-1; col >= 0; --col) {
				cCoef = this.complexMatrix[row][col];				
				//if (cCoef.mod() == 0.0) continue;
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

	/*
	 * ***********************************************
	 * COPY & REPLICATION
	 * ***********************************************
	 */

	/**
	 * Copies the coefficients of the polynomial in a new one.
	 * @return The new polynomial with the coefficients copied.
	 */
	public Polynom copy() {
		Polynom newPoly = new Polynom(this.degree());
		for (int col = 0; col <= this.degree(); ++col)
			newPoly.complexMatrix[0][col] = this.complexMatrix[0][col].copy();
		return newPoly;
	}

	/*
	 * ***********************************************
	 * METHODS
	 * ***********************************************
	 */
	
	/**
	 * Calculates the result of evaluating a polynomial for a specific complex value. Shortcut to the preferred method.
	 * @param value The value to use in the polynomial.
	 * @return The complex number resultant of evaluating the polynomial for "value".
	 */	
	public Complex eval(Complex value) {
		return evalHorner(value);
	}
	
	/**
	 * Calculates the result of evaluating a polynomial for a specific real value.
	 * @param value The value to use in the polynomial.
	 * @return The complex number resultant of evaluating the polynomial for "value".
	 */
	public Complex eval(double value) {
		Complex cNum = new Complex(value,0);
		return eval(cNum);
	}

	/**
	 * Calculates the result of evaluating a polynomial for a specific complex value using the Horner method. 
	 * @param value The value to use in the polynomial.
	 * @return The complex number resultant of evaluating the polynomial for "value".
	 */
	public Complex evalHorner(Complex value) {
		int colLen = this.cols();
		int rowLen = this.rows();
		if (rowLen != 1) {
			System.err.println("Not valid matrix: The matrix doesn't represent polynomial.");
			System.exit(1);
		}

		Complex interValue = this.getItem(0,colLen-1).copy();
		for (int i = colLen-2; i > -1; --i) {
			interValue = this.getItem(0,i).plus((interValue).times(value));
		}
		return interValue;
	}

	public Complex evalHorner(double value) {
		Complex cNum = new Complex(value,0);
		return evalHorner(cNum);
	}

	/**
	 * Calculates the result of evaluating a polynomial for a specific complex value through the power on the value. 
	 * @param value The value to use in the polynomial.
	 * @return The complex number resultant of evaluating the polynomial for "value".
	 */
	public Complex evalFact(Complex value) {
		int rowLen = this.rows();
		int colLen = this.cols();
		if (rowLen != 1) {
			System.err.println("Not valid matrix: The matrix doesn't represent polynomial.");
			System.exit(1);
		}

		Complex cRes = this.complexMatrix[0][0].copy();
		Complex xpower = new Complex(1);
		int i;
		for (i = 1; i < colLen; ++i) {
			xpower = xpower.times(value);
			cRes = cRes.plus(this.complexMatrix[0][i].times(xpower));
		}
		return cRes;
	}

	public Complex evalFact(double value) {
		Complex cNum = new Complex(value,0);
		return evalFact(cNum);
	}

	/**
	 * Private method. Calculates the result of evaluating a NORMALIZED polynomial for a specific complex value.
	 * @param value The value to use in the polynomial.
	 * @return The complex number resultant of evaluating the polynomial for "value".
	 */
	private Complex evalNorm(Complex value) {
		int rowLen = this.rows();
		int colLen = this.cols();
		if (rowLen != 1) {
			System.err.println("Not valid matrix: The matrix doesn't represent polynomial.");
			System.exit(1);
		}

		Complex interValue = this.polyNorm[0][colLen-1].copy();
		for (int i = colLen-2; i > -1; --i) {
			interValue = this.polyNorm[0][i].plus((interValue).times(value));
		}

		return interValue;
	}

	public Complex evalNorm(double value) {
		Complex cNum = new Complex(value,0);
		return evalFact(cNum);
	}

	/**
	 * Normalize a polynomial by dividing the terms by the coefficient of the variable with higher exponent.
	 * The normalized polynomial is stored in the member polyNorm.
	 */
	private void normalizePol() {
		int rowLen = this.rows();
		int colLen = this.cols();
		int degree = colLen - 1; 
		this.polyNorm = new Complex[rowLen][colLen];

		for (int i = 0; i < rowLen; ++i)
			for (int j = 0; j < colLen; ++j)
				this.polyNorm[i][j] = this.complexMatrix[i][j].divides(this.complexMatrix[i][degree]);
	}
	
	/**
	 * 
	 */
	public Polynom normalize() {
		normalizePol();
		Polynom normPol = new Polynom(this.degree());
		normPol.complexMatrix = polyNorm.clone();
		return normPol;
	}

	/**
	 * Finds the roots to a Nth degree equation with a determined precision using the Weierstrass method.
	 * Durand-Kerner Method. Discovered by Karl Weierstrass in 1891 and rediscovered independently by Durand in 1960 and Kerner in 1966.
	 * @param precision The precision used to identify a zero.
	 * @return The column array with the solutions found.
	 */
	public MatrixComplex solveWeierstrass(double theprecision) {
		final boolean DEBUG_ON = false; 
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cDenom = new Complex(1,0);
		int iter = 0;
		double precision = theprecision;

		this.normalizePol();

		if (rowLen != 1 || colLen < 2) {
			System.err.println("Not valid matrix: The matrix doesn't represent a Nth degree equation.");
			System.exit(1);
		}

		if ( colLen == 2) {
			MatrixComplex cSol = new MatrixComplex(--colLen, 1);
			cSol.complexMatrix[0][0] = this.complexMatrix[0][0].opposite().divides(this.complexMatrix[0][1]); 
			return cSol;
		}
		if (colLen == 3) 
			return this.solve2d();

		MatrixComplex cSol = new MatrixComplex(--colLen, 1);
		MatrixComplex cCoef = new MatrixComplex(2, colLen);
		// boolean[] cCoefCalc = new boolean[colLen];

		cCoef.initMatrixRandomRec();

		for (int i = 0; i < colLen; ++i) {
			cCoef.complexMatrix[1][i] = cCoef.complexMatrix[0][i].times(Math.random());
		}

		Double cre;
		boolean finished = true;
		do {
			finished = true;
			for (int i = 0; i < colLen; ++i) {
				cDenom.setComplexPol(1, 0);
				for (int j = 0; j < colLen; ++j) 
					if (j != i) cDenom = cDenom.times(cCoef.complexMatrix[1][i].minus(cCoef.complexMatrix[1][j]));
				cCoef.complexMatrix[0][i] = cCoef.complexMatrix[1][i].minus((evalNorm(cCoef.complexMatrix[1][i]).divides(cDenom)));

				cre = cCoef.complexMatrix[0][i].cre();
				if (cre.isNaN()) {
					System.err.println("Arithmetic Overflow");
					System.exit(10);
				}
				//finished &= (cCoef.complexMatrix[1][i].minus(cCoef.complexMatrix[0][i])).mod() < precision;
				finished &= (cCoef.complexMatrix[1][i].minus(cCoef.complexMatrix[0][i])).equals(Complex.ZERO);
				cCoef.complexMatrix[1][i] = cCoef.complexMatrix[0][i];
			}

			//Sometimes ending condition doesn't work
			if (iter++ > maxRootIter) finished = true;
		} while (!finished);			

		//int numOfDecs = (int) Math.abs(Math.log10(precision)) / 2 + 1;
		//numOfDecs = numOfDecs-1 > 0 ? --numOfDecs : numOfDecs;
		double maxPrec = Math.sqrt(precision*10);
		// If the number of iterations to obtain the roots has been overpaswed, the precision is reduced drastically
		int numOfDecs = iter > maxRootIter ? 4 : (int) Math.abs(Math.log10(maxPrec));
		numOfDecs = numOfDecs-1 > 0 ? --numOfDecs : numOfDecs;

		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			System.out.println(HEADINFO + "precision:" + precision);
			System.out.println(HEADINFO + "maxPrec   :" + maxPrec);
			System.out.println(HEADINFO + "numOfDecs :" + numOfDecs);
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		for (int i = 0; i < colLen; ++i) {
			/*
			if (cCoef.complexMatrix[0][i].isZeroRed()) cSol.complexMatrix[i][0] = Complex.ZERO;
			else cSol.complexMatrix[i][0] = Complex.round(cCoef.complexMatrix[0][i],numOfDecs); //cCoef.complexMatrix[0][i]; //
			*/
			if (Math.abs(cCoef.complexMatrix[0][i].rep()) < maxPrec) cCoef.complexMatrix[0][i].setComplexRec(0, cCoef.complexMatrix[0][i].imp());
			if (Math.abs(cCoef.complexMatrix[0][i].imp()) < maxPrec) cCoef.complexMatrix[0][i].setComplexRec(cCoef.complexMatrix[0][i].rep(), 0);
			if (Complex.exact()) {
				cSol.complexMatrix[i][0] = Complex.round(cCoef.complexMatrix[0][i],numOfDecs); 
			}
			else {
				cSol.complexMatrix[i][0] = cCoef.complexMatrix[0][i]; 
			}
		}

		/* -------------   DEBUGGING BLOCK   ------------- */
		if (DEBUG_ON) {
			System.out.println(HEADINFO + "solveWeierstrass: " + "iterations for roots:" + iter);
		}
		/* ------------- END DEBUGGING BLOCK ------------- */

		return cSol;
	}    
	
	/**
	 * Finds the roots to a Nth degree equation with the precision specified in the library using the Weierstrass method.
	 * Durand-Kerner Method. discovered by Karl Weierstrass in 1891 and rediscovered independently by Durand in 1960 and Kerner in 1966.
	 * @return The column array with the solutions found.
	 */
	public MatrixComplex solveWeierstrass() {
		return solveWeierstrass(Complex.precision());
	}

	/**
	 * Shortcut for solveWeierstrass(double precision).
	 * @param precision The precision used to identify a zero.
	 * @return The column array with the solutions found.
	 */
	public MatrixComplex solve(double precision) {
		return solveWeierstrass(precision);
	}

	/**
	 * Shortcat for solveWeierstrass().
	 * @return The column array with the solutions found.
	 */
	public MatrixComplex solve() {
		return solveWeierstrass(Complex.precision());
	}

	/**
	 * Finds the roots of a 2dn degree equation using the quadratic formula.
	 * @return The column array with the solutions found.
	 */
	public MatrixComplex solve2d() {
		int rowLen = this.rows();
		int colLen = this.cols();

		if (rowLen != 1 ||  colLen != 3) {
			System.err.println("Not valid matrix: The matrix doesn't represent a 2nd degree equation.");
			System.exit(1);
		}

		MatrixComplex cSol = new MatrixComplex(colLen-1, 1);
		Complex cDisc = new Complex();
		Complex c = new Complex(this.complexMatrix[0][0].rep(), this.complexMatrix[0][0].imp());
		Complex b = new Complex(this.complexMatrix[0][1].rep(), this.complexMatrix[0][1].imp());
		Complex a = new Complex(this.complexMatrix[0][2].rep(), this.complexMatrix[0][2].imp());

		cDisc = Complex.root(b.power(2).minus(a.times(c.times(4))),2);
		b = b.opposite();
		cSol.complexMatrix[0][0] = (b.plus(cDisc)).divides(a.times(2));
		cSol.complexMatrix[1][0] = (b.minus(cDisc)).divides(a.times(2));

		return cSol;
	}

	/**
	 * Gets the maximum root based on its real part.
	 * @param roots The matrix with the roots returned by the solve method.
	 * @return The max root.
	 */
	public double getMaxRoot(MatrixComplex roots) {
		double maxRoot = roots.complexMatrix[0][0].rep();
		for (int i = 1; i < roots.complexMatrix.length; ++i)
			if (maxRoot < roots.complexMatrix[i][0].rep()) maxRoot = roots.complexMatrix[i][0].rep();
		return maxRoot;
	}

	/**
	 * Gets the minimum root based on its real part.
	 * @param roots The matrix with the roots returned by the solve method.
	 * @return The min root.
	 */
	public double getMinRoot(MatrixComplex roots) {
		double minRoot = roots.complexMatrix[0][0].rep();
		for (int i = 1; i < roots.complexMatrix.length; ++i)
			if (minRoot > roots.complexMatrix[i][0].rep()) minRoot = roots.complexMatrix[i][0].rep();
		return minRoot;
	}

	/**
	 * Calculates the opposite of the polynomial.
	 * @return The opposite of the polynomial.
	 */
	public Polynom opposite() {
		Polynom result = new Polynom(this.degree());
		for(int col = 0; col <= this.degree(); ++col)
			result.complexMatrix[0][col] = this.complexMatrix[0][col].opposite();
		return result;
	}

	/*
	 * ***********************************************
	 * PRIVATE FUNCS
	 * ***********************************************
	 */

	/**
	 * Expands the polynomial with the terms given in polynom.
	 * @param polynom The polynomial used to expand this.
	 * @param degree the maximum degree to expand.
	 * @return The expanded polynomial.
	 */
	private Polynom expand(Polynom polynom, int degree) {
		if (polynom.degree() >= degree) 
			return polynom;
		Polynom result = new Polynom(degree);
		for(int col = 0; col <= degree; ++col)
			result.complexMatrix[0][col] = polynom.complexMatrix[0][col];
		return result;
	}

	/**
	 * Returns a new matrix with the coefficients of the polynomial ordered from x^0 to x^n.
	 * The new matrix is the original one with the coeficients of the polynomial.
	 * @return The new matrix with the coeficients of the polynomial.
	 */
 	public MatrixComplex coefMatrix() {
		int colLen = this.cols();
		MatrixComplex coefMatrix = new MatrixComplex(1, colLen);
		for (int col = colLen-1; col > -1; --col)
				coefMatrix.complexMatrix[0][colLen-1-col] = this.complexMatrix[0][col].copy();
		return coefMatrix;
	}

	/*
	 * ***********************************************
	 * OPERATORS
	 * ***********************************************
	 */

	/**
	 * Calculates the degree of the polynomial.
	 * @return The degree of the polynomial.
	 */
	public int degree() {
		return this.cols() - 1;
	}

	/**
	 * Calculates the sum of two polynomials.
	 * @param polynom The polynomial to add.
	 * @return The sum of both polynomials.
	 */
	public Polynom plus(Polynom polynom) {
		int resDegree = (this.degree() > polynom.degree())? this.degree():polynom.degree() ;
		Polynom result = new Polynom(resDegree);
		Complex aNum = new Complex();
		Complex bNum = new Complex();
		Complex zero = new Complex();

		for(int col = 0; col <= result.degree(); ++col) {
			//System.out.println("row:" + 0 + " col:" + col);
			aNum = (col <= this.degree())?this.complexMatrix[0][col]: zero;
			bNum = (col <= polynom.degree())?polynom.complexMatrix[0][col]: zero;
			result.complexMatrix[0][col] = aNum.plus(bNum);
		}
		return result;
	}

	/**
	 * Calculates the differentiate of two polynomials.
	 * @param polynom The polynomial to differentiate.
	 * @return The difference of both polynomials.
	 */
	public Polynom minus(Polynom polynom) {
		int resDegree = (this.degree() > polynom.degree())? this.degree():polynom.degree() ;
		Polynom result = new Polynom(resDegree);
		result = (Polynom)this.plus(polynom.opposite());
		return result;
	}

	/**
	 * Calculates the product of two polynomials.
	 * @param polynom The multiplier polynomial.
	 * @return The product of both polynomials.
	 */
	public Polynom times(Polynom polynom) {
		int resDegree = this.degree() + polynom.degree();
		Polynom result = new Polynom(resDegree);
		MatrixComplex aMatrix = new MatrixComplex(result.degree());

		aMatrix = this.transpose().times(polynom);

		for (int row = 0; row <= this.degree(); ++row)
			for(int col = 0; col <= polynom.degree(); ++col)
				result.complexMatrix[0][row+col] = result.complexMatrix[0][row+col].plus(aMatrix.complexMatrix[row][col]);
		return result;
	}

	/**
	 * Calculates the product of a polynomial by a complex number.
	 * @param cNum The multiplier complex number.
	 * @return The polynomial by the complex number.
	 */
	public Polynom times(Complex cNum) {
		Polynom cNumPoly = new Polynom(0);
		cNumPoly.complexMatrix[0][0].setComplexPol(cNum.mod(), cNum.pha());
		return this.times(cNumPoly);
	}

	/**
	 * Calculates the product of a polynomial by a double.
	 * @param num The multiplier double.
	 * @return The polynomial by the double.
	 */
	public Polynom times(double num) {
		Polynom cNumPoly = new Polynom(0);
		cNumPoly.complexMatrix[0][0].setComplexPol(num,0);
		return this.times(cNumPoly);
	}

	/**
	 * Divides two polynomials giving the polynomial quotient as the return value.
	 * The quotient has the polynomial remainder result of the division as member variable. Use the getRemainder() method to get the remainder.
	 * Use the Method getRemainder to get the polynomial remainder.
	 * @param divisor The divisor polynomial.
	 * @return quotient The quotient of the division.
	 */
	public Polynom divides(Polynom divisor) {
		if (this.degree() < divisor.degree()) {
			Polynom quotient = new Polynom(0);
			return quotient;
		}
		Polynom dividend = this.copy(); //	dividend.print("dividend");
		Polynom quotient = new Polynom(dividend.degree() - divisor.degree());
		Complex coef = new Complex();
		for(int col1 = dividend.degree(); col1 >= divisor.degree(); --col1) {
			if (dividend.complexMatrix[0][col1].equals(0,0)) {
				quotient.complexMatrix[0][col1-divisor.degree()] = dividend.complexMatrix[0][col1];
				// Kept for testing purposes
				System.out.println("continue");
				//
				continue;
			}
			coef = dividend.complexMatrix[0][col1].divides(divisor.complexMatrix[0][divisor.degree()]);
			quotient.complexMatrix[0][col1-divisor.degree()] = coef;
			for(int col = col1, col2 = divisor.degree(); col >= col1 - divisor.degree(); --col, --col2) {
				dividend.complexMatrix[0][col] = dividend.complexMatrix[0][col].minus(divisor.complexMatrix[0][col2].times(coef));
			}
		}
		quotient.setRemainder(dividend);
		return quotient;
	}

	/**
	 * Calculates the division of a polynomial by a complex number.
	 * @param cNum The divisor complex number.
	 * @return The polynomial divided by the complex number.
	 */
	public Polynom divides(Complex cNum) {
		return this.times(cNum.inverse());
	}

	/**
	 * Calculates the division of a polynomial by a double.
	 * @param num The divisor double.
	 * @return The polynomial divided by the double.
	 */
	public Polynom divides(double num) {
		return this.times(1/num);
	}

	/**
	 * Calculates the polynomial to the nth power
	 * @param pot The exponent
	 * @return The polynomial raised to the nth power
	 */
	public Polynom power(int pot) {
		if (pot <= 0) return new Polynom();
		Polynom pol = this.copy();
		for(int i = 1; i < pot; ++i)
			pol = this.times(pol);
		return pol;
	}
	/*
	 * ***********************************************
	 * PLOTTING
	 * ***********************************************
	 */

	/**
	 * Plots the function expressed in GNUPlot format between loLimit and upLimit.
	 * @param GNUplotExpression The function to plot in GNUPlot format.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotExpression(String GNUplotExpression,double loLimit, double upLimit) {
		double samples = (upLimit - loLimit) * sampleBase;
		JavaPlot p = new JavaPlot();
		p.addPlot(GNUplotExpression);
		p.setTitle(this.toString());
		p.addPlot("[" + loLimit + ":" + upLimit + "] " + this.toGNUPlot_poly());
		p.set("zeroaxis", "");
		p.set("samples", Double.toString(samples));
		p.plot();
	}

	/**
	 * Plots the polynomial as a expression between loLimit and upLimit.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotExpression(double loLimit, double upLimit) {
		double samples = (upLimit - loLimit) * sampleBase;
		JavaPlot p = new JavaPlot();
		p.setTitle(this.toString());
		p.set("zeroaxis", "");
		p.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		p.set("samples", Double.toString(samples));
		p.addPlot(this.toGNUPlot_poly());
		p.plot();
	}

	/**
	 * Plots the Real part of the polynomial as a expression between loLimit and upLimit.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotExpressionRe(double loLimit, double upLimit) {
		double samples = (upLimit - loLimit) * sampleBase;
		JavaPlot p = new JavaPlot();
		p.setTitle("Re(" + this.toString() + ")");
		p.set("zeroaxis", "");
		p.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		p.set("key", "noautotitle");
		p.set("samples", Double.toString(samples));
		p.addPlot("real("+ this.toGNUPlot_poly() + ")");
		p.plot();
	}

	/**
	 * Plots the Imaginary part of the polynomial as a expression between loLimit and upLimit.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotExpressionIm(double loLimit, double upLimit) {
		double samples = (upLimit - loLimit) * sampleBase;
		JavaPlot p = new JavaPlot();
		p.setTitle("Im(" + this.toString() + ")");
		p.set("zeroaxis", "");
		p.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		p.set("key", "noautotitle");
		p.set("samples", Double.toString(samples));
		p.addPlot("imag("+ this.toGNUPlot_poly() + ")");
		p.plot();
	}

	/**
	 * Plots the Real Imaginary parts of the polynomial as a expression between loLimit and upLimit in the same graphic.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotExpressionReIm(double loLimit, double upLimit) {
		double samples = (upLimit - loLimit) * sampleBase;
		JavaPlot p = new JavaPlot();
		p.setTitle("Re() Im() " + this.toString());
		p.set("zeroaxis", "");
		p.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		p.set("key", "noautotitle");
		p.set("samples", Double.toString(samples));
		p.addPlot("real(" + this.toGNUPlot_poly() + "), imag("+ this.toGNUPlot_poly() + ")");
		p.plot();
	}

	/**
	 * Plots the nosenses Real Imaginary parts of the polynomial as a expression between loLimit and upLimit in the same graphic.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotExpressionRepIm(double loLimit, double upLimit) {
		double samples = (upLimit - loLimit) * sampleBase;
		JavaPlot p = new JavaPlot();
		p.setTitle("Re() Im() " + this.toString());
		p.set("zeroaxis", "");
		p.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		p.set("key", "noautotitle");
		p.set("samples", Double.toString(samples));
		p.addPlot("real(" + this.toGNUPlot_poly() + ") / imag("+ this.toGNUPlot_poly() + ")");
		p.plot();
	}

	/**
	 * Plots the modulus (Abs) of the polynomial as a expression between loLimit and upLimit in the same graphic.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotExpressionAbs(double loLimit, double upLimit) {
		double samples = (upLimit - loLimit) * sampleBase;
		JavaPlot p = new JavaPlot();
		p.setTitle("Abs("+this.toString()+")");
		p.set("zeroaxis", "");
		p.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		p.set("key", "noautotitle");
		p.set("samples", Double.toString(samples));
		p.addPlot("abs(" + this.toGNUPlot_poly() + ")");
		p.plot();
	}

	/**
	 * Plots the phase (atan(Re/Im)) of the polynomial as a expression between loLimit and upLimit in the same graphic.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotExpressionPhase(double loLimit, double upLimit) {
		double samples = (upLimit - loLimit) * sampleBase;
		JavaPlot p = new JavaPlot();
		p.setTitle("Phase("+this.toString()+")");
		p.set("grid", "");
		p.set("zeroaxis", "");
		p.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		p.set("key", "noautotitle");
		p.set("samples", Double.toString(samples));
		p.addPlot("atan(real(" + this.toGNUPlot_poly() + ")/imag("+ this.toGNUPlot_poly() + "))");
		p.plot();
	}

	/**
	 * Plots different graphics in the same canvas from their list of ponts
	 * @param pointsList The list with the points defined as List<double[][]> 
	 * @param title the title of the graphic
	 */
	public void plot(List<double[][]> pointsList, String title) {
		JavaPlot p = new JavaPlot();
		p.setTitle(title);
		for(int i = 0; i < pointsList.size(); ++i) {
			p.addPlot(pointsList.get(i));
		}
		p.set("zeroaxis", "");
		p.set("style","data lines");
		p.set("mxtics","10");
		p.set("mytics","10");
		p.set("grid","xtics mxtics ytics mytics");
		p.plot();
	}

	public void plot(double[][] points, String title) {
		JavaPlot p = new JavaPlot();
		p.setTitle(title);
		p.addPlot(points);
		p.set("zeroaxis", "");
		p.set("style","data lines");
		p.set("mxtics","10");
		p.set("mytics","10");
		p.set("grid","xtics mxtics ytics mytics");
		p.plot();
	}

	public void plotRe(MatrixComplex points, String title) {
		int samples = points.rows();
		double[][] pointsRe = new double[samples][2];		
		for (int i = 0; i < samples; ++i) {
			pointsRe[i][0] = points.getItem(i, 0).rep(); 
			pointsRe[i][1] = points.getItem(i, 1).rep(); 
		}
		plot(pointsRe, title);
	}
	
	/**
	 * Plots the real component of different graphics in the same canvas from its definition of its points as complexes
	 * @param pointsList The list with the points defined as <MatrixComplex>
	 * @param title the title of the graphic
	 */
	public void plotRe(List<MatrixComplex> pointsList, String title) {
		List<double[][]> pointsListGraph = new ArrayList<double[][]>();
		int samples = pointsList.get(0).rows();
		for(int l = 0; l < pointsList.size(); ++l) {
			double[][] pointsRe = new double[samples][2];
			for (int i = 0; i < samples; ++i) {
				pointsRe[i][0] = pointsList.get(l).getItem(i, 0).rep(); 
				pointsRe[i][1] = pointsList.get(l).getItem(i, 1).rep(); 
			}
			pointsListGraph.add(pointsRe);
		}
		plot(pointsListGraph, title);
	}	

	
	public void plotIm(MatrixComplex points, String title) {
		int samples = points.rows();
		double[][] pointsIm = new double[samples][2];		
		for (int i = 0; i < samples; ++i) {
			pointsIm[i][0] = points.getItem(i, 0).imp(); 
			pointsIm[i][1] = points.getItem(i, 1).imp(); 
		}
		plot(pointsIm, title);
	}

	/**
	 * Plots the imaginary component of different graphics in the same canvas from its definition of its points as complexes
	 * @param pointsList The list with the points defined as <MatrixComplex>
	 * @param title the title of the graphic
	 */
	public void plotIm(List<MatrixComplex> pointsList, String title) {
		List<double[][]> pointsListGraph = new ArrayList<double[][]>();
		int samples = pointsList.get(0).rows();
		for(int l = 0; l < pointsList.size(); ++l) {
			double[][] pointsIm = new double[samples][2];
			for (int i = 0; i < samples; ++i) {
				pointsIm[i][0] = pointsList.get(l).getItem(i, 0).imp(); 
				pointsIm[i][1] = pointsList.get(l).getItem(i, 1).imp();
			}
			pointsListGraph.add(pointsIm);
		}
		plot(pointsListGraph, title);
	}	

	public void plotMod(MatrixComplex points, String title) {
		int samples = points.rows();
		double[][] pointsMod = new double[samples][2];		
		for (int i = 0; i < samples; ++i) {
			pointsMod[i][0] = points.getItem(i, 0).mod(); 
			pointsMod[i][1] = points.getItem(i, 1).mod(); 
		}
		plot(pointsMod, title);
	}

	/**
	 * 
	 * @param pointsList
	 * @param title
	 */
	public void plotMod(List<MatrixComplex> pointsList, String title) {
		List<double[][]> pointsListGraph = new ArrayList<double[][]>();
		int samples = pointsList.get(0).rows();
		for(int l = 0; l < pointsList.size(); ++l) {
			double[][] pointsIm = new double[samples][2];
			for (int i = 0; i < samples; ++i) {
				pointsIm[i][0] = pointsList.get(l).getItem(i, 0).mod(); 
				pointsIm[i][1] = pointsList.get(l).getItem(i, 1).mod();
			}
			pointsListGraph.add(pointsIm);
		}
		plot(pointsListGraph, title);
	}	

	public void plotPha(MatrixComplex points, String title) {
		int samples = points.rows();
		double[][] pointsPha = new double[samples][2];		
		for (int i = 0; i < samples; ++i) {
			pointsPha[i][0] = points.getItem(i, 0).pha(); 
			pointsPha[i][1] = points.getItem(i, 1).pha(); 
		}
		plot(pointsPha, title);
	}
	
	/**
	 * 
	 * @param pointsList
	 * @param title
	 */
	public void plotPha(List<MatrixComplex> pointsList, String title) {
		List<double[][]> pointsListGraph = new ArrayList<double[][]>();
		int samples = pointsList.get(0).rows();
		for(int l = 0; l < pointsList.size(); ++l) {
			double[][] pointsIm = new double[samples][2];
			for (int i = 0; i < samples; ++i) {
				pointsIm[i][0] = pointsList.get(l).getItem(i, 0).mod(); 
				pointsIm[i][1] = pointsList.get(l).getItem(i, 1).pha();
			}
			pointsListGraph.add(pointsIm);
		}
		plot(pointsListGraph, title);
	}

	/**
	 * Traverses an interval taken sampleBase samples and evaluating the polynomial in these points
	 * You should set sampleBase before calling this method
	 * @param lolimit
	 * @param uplimit
	 * @return
	 */
	public MatrixComplex walkInterval(Complex lolimit, Complex uplimit) {
		Complex.storeFormatStatus();
		Complex.setScientificON(20);		
		MatrixComplex tupla = new MatrixComplex(sampleBase, 2);
		Complex dir = uplimit.minus(lolimit);
		Complex inc = dir.divides(sampleBase);
		Complex point;
		for(int i=0; i < sampleBase; ++i) {
			point = lolimit.plus(inc.times(i));
			tupla.setItem(i, 0, point);
			tupla.setItem(i, 1, this.eval(point));			
		}
		Complex.restoreFormatStatus();
		return tupla;
	}
	
	/**
	 * Traverses an interval taken sampleBase samples and evaluating the polynomial in these points
	 * @param dlolimit
	 * @param duplimit
	 * @return
	 */
	public MatrixComplex walkInterval(double dlolimit, double duplimit) {
		Complex lolimit = new Complex(dlolimit);
		Complex uplimit = new Complex(duplimit);
		return walkInterval(lolimit, uplimit);
	}
	
	public void plotRe(double[][] points) {
		double lolimit = points[0][0];
		double uplimit = points[points.length][0];
		JavaPlot p = new JavaPlot();
		p.setTitle("Phase("+this.toString()+")");
		p.set("grid", "");
		p.set("zeroaxis", "");
		p.set("xrange", "[" + lolimit + ":" + uplimit + "]");
		p.set("key", "noautotitle");
		p.set("samples", Double.toString(sampleBase));
		p.addPlot(points);
		p.plot();
	}
	

	/*
	 * ***********************************************
	 * POLYNIMIAL CALCULATIONS
	 * ***********************************************
	 */

	/**
	 * Returns a polynomial as result of the product of polynomials given in a List.
	 * @param listTerm List of polynomial terms.
	 * @return the polynomial.
	 */
	public Polynom fromTerms(List<Polynom> listTerm) {
		Polynom polynom = new Polynom("1");
		for (Polynom term : listTerm) {
			polynom = polynom.times(term);
		}
		return polynom;
	}

	/**
	 * Returns a polynomial from a list of its roots.
	 * @param strRoots list of the roots as a comma separated string.
	 * @return the polynomial.
	 */
	public Polynom fromRoots(String strRoots) {
		List<Polynom> listTerm = new ArrayList<Polynom>();
		String[] roots = strRoots.split(",");
		Complex root = new Complex();

		for (int i = 0; i < roots.length; ++i) {
			root = root.setComplex(roots[i]).opposite();
			listTerm.add(new Polynom("1,"+root.toString()));
		}
		return this.fromTerms(listTerm);
	}

	/**
	 * Returns a polynomial from MatrixComplex of its roots.
	 * @param rootMatrix MatrixComplex with the roots.
	 * @return the polynomial.
	 */
	public Polynom fromRoots(MatrixComplex rootMatrix) {
		Polynom polynom = new Polynom("1");
		for (int i = 0; i < rootMatrix.rows(); ++i) {
			Polynom polyRoot = new Polynom(1);
			polyRoot.setItem(0, 0, rootMatrix.getItem(i, 0).opposite());
			polyRoot.setItem(0, 1, 1);
			polynom = polynom.times(polyRoot);
		}
		return polynom;
	}

	/**
	 * Returns a polynomial from a list of its roots.
	 * @param listRoots Complex list of the roots.
	 * @return the polynomial.
	 */
	public Polynom fromRoots(List<Complex> listRoots) {
		List<Polynom> listTerm = new ArrayList<Polynom>();
		for (Complex root : listRoots) {
			listTerm.add(new Polynom("1,"+root.toString()));
		}
		return this.fromTerms(listTerm);
	}

	/**
	 * Calculates the Hermite polynomial of degree "degree" using the formula hermite(n) = 2n*hermite(n-1) - 2(n-1)*hermite(n-2)
	 * Hermite(0) = 1
	 * Hermite(1) = 2x
	 * @param degree The degree of the Hermite polynomial.
	 * @return The Hermite polynomial of degree "degree".
	 */
	public Polynom hermite(int degree) {
		final Polynom hermite0 = new Polynom("1");
		final Polynom hermite1 = new Polynom("2,0");

		if (degree == 0) return hermite0;
		if (degree == 1) return hermite1;
		Polynom hermite = new Polynom(degree);
		hermite = hermite1.times(hermite(degree-1)).minus(hermite(degree-2).times(2.0*(degree-1)));
		return hermite;
	}

	/**
	 * Calculates the Hermite I polynomial of degree "degree" using the formula hermite(n) = 2n*hermite(n-1)i - 2(n-1)*hermite(n-2)
	 * Hermite(0) = 1
	 * Hermite(1) = 2xi
	 * @param degree The degree of the Hermite polynomial.
	 * @return The Hermite polynomial of degree "degree".
	 */
	public Polynom hermiteI(int degree) {
		final Polynom hermite0 = new Polynom("1");
		final Polynom hermite1 = new Polynom("2i,0");

		if (degree == 0) return hermite0;
		if (degree == 1) return hermite1;
		Polynom hermite = new Polynom(degree);
		hermite = hermite1.times(hermite(degree-1).times(Complex.i)).minus(hermite(degree-2).times(2.0*(degree-1)));
		return hermite;
	}

	/**
	 * Calculates the probabilists' Hermite polynomial of degree "degree" using the formula hermiteP(n) = 2·x*hermiteP(n-1) - 2(n-1)*hermteP(n-2)
	 * Hermite(0) = 1
	 * Hermite(1) = x
	 * @param degree The degree of the Hermite polynomial.
	 * @return The probabilists' Hermite polynomial of degree "degree".
	 */
	public Polynom hermiteP(int degree) {
		final Polynom hermite0 = new Polynom("1");
		final Polynom hermite1 = new Polynom("1, 0");

		if (degree == 0) return hermite0;
		if (degree == 1) return hermite1;
		Polynom hermite = new Polynom(degree);
		hermite = hermite1.times(hermiteP(degree-1)).minus(hermiteP(degree-2).times(2.0*(degree-1)));
		return hermite;
	}

	/**
	 * Calculates the Legendre polynomial of degree "degree" using the formula (n+1)*legendre(n+1) = (2n+1)·x*legendre(n) - n*legendre(n-1)
	 * Legendre(0) = 1
	 * Legendre(1) = x
	 * @param degree The degree of the Legendre polynomial.
	 * @return The Legendre polynomial of degree "degree".
	 */
	public Polynom legendre(int degree) {
		final Polynom legendre0 = new Polynom("1");
		final Polynom legendre1 = new Polynom("1, 0");

		if (degree == 0) return legendre0;
		if (degree == 1) return legendre1;
		Polynom legendre = new Polynom(degree);
		int n = degree - 1;
		legendre = legendre(n).times(legendre1.times(2*n+1)).minus(legendre(n-1).times(n));
		return legendre.divides(degree);
	}

	/**
	 * Calculates the Legendre I polynomial of degree "degree" using the formula (n+1)*legendre(n+1) = (2n+1)·x*legendre(n)i - n*legendre(n-1)
	 * Legendre(0) = 1
	 * Legendre(1) = xi
	 * @param degree The degree of the Legendre polynomial.
	 * @return The Legendre polynomial of degree "degree".
	 */
	public Polynom legendreI(int degree) {
		final Polynom legendre0 = new Polynom("1");
		final Polynom legendre1 = new Polynom("i, 0");

		if (degree == 0) return legendre0;
		if (degree == 1) return legendre1;
		Polynom legendre = new Polynom(degree);
		int n = degree - 1;
		legendre = legendre(n).times(legendre1.times(2*n+1).times(Complex.i)).minus(legendre(n-1).times(n));
		return legendre.divides(degree);
	}

	/**
	 * Calculates the generalized Laguerre polynomial of degree "degree" and alpha "alpha" using the formula (n+1)*laguerre(n+1, alpha) = (2n+1+alpha)·x*laguerre(n,alpha) - (n+alpha)*laguerre(n-1,alpha)
	 * Laguerre(0) = 1
	 * Laguerre(1, alpha) = -x+1+alpha
	 * @param degree The degree of the Laguerre polynomial.
	 * @param alpha The value of alpha.
	 * @return The Laguerre polynomial of degree "degree" and alpha "alpha".
	 */
	public Polynom laguerre(int degree, double alpha) {
		final Polynom laguerre0 = new Polynom("1");
		/*
		 * laguerre1 = -x + 1 + alpha
		 */
		final Polynom laguerre1 = new Polynom("-1, 0");
		laguerre1.complexMatrix[0][0].setComplexPol(1+alpha, 0);

		if (degree == 0) return laguerre0;
		if (degree == 1) return laguerre1;
		Polynom laguerre = new Polynom(degree);
		int k = degree - 1;
		/*
		 * term = (-x+2k+1+alpha)
		 */
		Polynom term = new Polynom("-1,0");
		term.complexMatrix[0][0].setComplexPol(2*k+1+alpha,0);
		laguerre = term.times(laguerre(k,alpha)).minus(laguerre(k-1,alpha).times(k+alpha));
		return laguerre.divides((double)degree);
	}

	/**
	 * Calculates the Laguerre polynomial of degree "degree" using the formula (n+1)*laguerre(n+1) = (2n+1)·x*laguerre(n) - n*laguerre(n-1)
	 * Laguerre(0) = 1
	 * Laguerre(1) = -x+1
	 * Is the particular case of generalized Laguerre polynomial for alpha=0, laguerre(n,0)
	 * @param degree The degree of the Laguerre polynomial.
	 * @return The Laguerre polynomial of degree "degree".
	 */
	public Polynom laguerre(int degree) {
		return laguerre(degree, 0);
	}

	/**
	 * Calculates the Chebyshev polynomial of degree "degree" and kind "kind" using the formula chebyshev(n+1, kind) = 2·x*chebyshev(n,kind) - n*chebyshev(n-1,kind)
	 * Chebyshev(0) = 1
	 * Chebyshev(1) = kind*x
	 * @param degree The degree of the Chebyshev polynomial.
	 * @param kind The kind of alpha.
	 * @return The Chebyshev polynomial of degree "degree" and kind "kind".
	 */
	public Polynom chebyshev(int degree, int kind) {
		final Polynom chebyshev0 = new Polynom("1");
		final Polynom chebyshev1 = new Polynom("1, 0").times(kind);
		final Polynom term = new Polynom("2,0");

		if (degree == 0) return chebyshev0;
		if (degree == 1) return chebyshev1;
		Polynom chebyshev = new Polynom(degree);
		int n = degree - 1;
		chebyshev = term.times(chebyshev(n,kind)).minus(chebyshev(n-1,kind));
		return chebyshev;
	}

	/**
	 * Calculates the Chebyshev polynomial of degree "degree" and kind 1.
	 * Chebyshev(0) = 1
	 * Chebyshev(1) = x
	 * Chebyshev(n) = cos(n*arccos(x))
	 * @param degree The degree of the Chebyshev polynomial.
	 * @return The Chebyshev polynomial of degree "degree" and kind 1.
	 */
	public Polynom chebyshev1(int degree) {
		return this.chebyshev(degree, 1);
	}

	/**
	 * Calculates the Chebyshev polynomial of degree "degree" and kind 2.
	 * Chebyshev(0) = 1
	 * Chebyshev(1) = 2x
	 * @param degree The degree of the Chebyshev polynomial.
	 * @return The Chebyshev polynomial of degree "degree" and kind 2.
	 */
	public Polynom chebyshev2(int degree) {
		return this.chebyshev(degree, 2);
	}

	/*
	 * ***********************************************
	 * INTERPOLATION
	 * ***********************************************
	 */

	/**
	 * 
	 * @param lolimit
	 * @param uplimit
	 * @param samples
	 * @return
	 */
	private MatrixComplex walkIntervalRE(Complex lolimit, Complex uplimit, int samples) {
		Complex.storeFormatStatus();
		Complex.setScientificON(20);
		Complex vector = uplimit.minus(lolimit);
		Complex nextPoint = new Complex();
		MatrixComplex points = new MatrixComplex(samples,2);

		//Recorrer la recta con distancia Euclidea
		double vectSlope = vector.imp()/vector.rep();
		double vectAngle = Math.atan(vectSlope);
		double projRe = vector.mod() * Math.cos(vectAngle);
		double stepRe = projRe / (samples-1) * Math.signum(vector.rep());
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

		points.setItem(0, 0, lolimit);
		points.setItem(0, 1, this.eval(lolimit));

		while (++iter < samples) {
			//System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
			nextRep = nextPoint.rep() + stepRe;
			nextImp = lolimit.imp() + vectSlope * (nextRep - lolimit.rep());
			nextPoint.setComplexRec(nextRep, nextImp);
			points.setItem(iter, 0, nextPoint);
			points.setItem(iter, 1, this.eval(nextPoint));
		}		
		// System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
		Complex.restoreFormatStatus();
		return points;
	}
	
	/**
	 * 
	 * @param lolimit
	 * @param uplimit
	 * @param samples
	 * @return
	 */
	private MatrixComplex walkIntervalIM(Complex lolimit, Complex uplimit, int samples) {
		Complex.storeFormatStatus();
		Complex.setScientificON(20);
		Complex vector = uplimit.minus(lolimit);
		Complex nextPoint = new Complex();
		MatrixComplex points = new MatrixComplex(samples,2);

		//Recorrer la recta con distancia Euclidea
		Double vectSlope = vector.rep()/vector.imp();
		double vectAngle = Math.atan(vectSlope);
		double projIm = vector.mod() * Math.cos(vectAngle);
		double stepIm = projIm / (samples-1) * Math.signum(vector.imp());
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
		
		points.setItem(0, 0, lolimit);
		points.setItem(0, 1, this.eval(lolimit));

		while (++iter < samples) {
			//System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
			nextImp = nextPoint.imp() + stepIm;
			nextRep = lolimit.rep() + vectSlope * (nextImp - lolimit.imp());
			nextPoint.setComplexRec(nextRep, nextImp);
			points.setItem(iter, 0, nextPoint);
			points.setItem(iter, 1, this.eval(nextPoint));
		}
		// System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
		return points;
	}

	/**
	 * 
	 * @param lolimit
	 * @param uplimit
	 * @param samples
	 * @return
	 */
	public MatrixComplex walkInterval(Complex lolimit, Complex uplimit, int samples) {
		Complex vector = uplimit.minus(lolimit);
		double vectSlope = vector.imp()/vector.rep();
		double vectAngle = Math.atan(vectSlope);
		
		vectAngle = vectAngle > Math.PI ? Math.PI - vectAngle : vectAngle;
		vectAngle = vectAngle < -Math.PI ? Math.PI + vectAngle : vectAngle;
		
		if (((vectAngle >= Math.PI/4) && (vectAngle < 3*Math.PI/4 )) ||
				((vectAngle >= -3*Math.PI/4) && (vectAngle < -Math.PI/4 ))) {
			return walkIntervalIM(lolimit, uplimit, samples);
		}
		else return walkIntervalRE(lolimit, uplimit, samples);
	}
	
	/**
	 * 
	 * @param lolimit
	 * @param uplimit
	 * @param samples
	 * @return
	 */
	public MatrixComplex walkInterval(double lolimit, double uplimit, int samples) {
		return walkInterval(new Complex(lolimit), new Complex(uplimit), samples);
	}

	/*
	 * NEWTON
	 */
	/**
	 * Calculates f1(xi,xj) as (f0(xi)-f0(xj))/(xi-xj) (see https://en.wikipedia.org/wiki/Newton_polynomial)
	 * @param i The i idx of the abscissa x
	 * @param j The j idx of the abscissa x
	 * @param points The table of ordered pairs x,y with x ascendent
	 * @return The slope between the to points
	 */
	private Complex slope(int i, int j, MatrixComplex points) {
		return (points.getItem(j, 1).minus(points.getItem(i, 1))).divides(points.getItem(j, 0).minus(points.getItem(i,0)));
	}
	
	/**
	 * Calculates the slope for orders greater than 1 (see https://en.wikipedia.org/wiki/Newton_polynomial)
	 * @param order The order of the slope
	 * @param i	The maximum index of the abscissas, i.e. f2(3) -> f2(x1,x2,x3). f2(2) -> f2(x0,x1,x2). Be aware that the number of abscissas is always order+1
	 * @param points The table of ordered pairs x,y with x ascendent
	 * @return The slope between the to points
	 */
	private Complex f(int order, int i, MatrixComplex points) {
		if (order == 1) return slope(i, i-1, points);	
		return (f(order-1,i,points).minus(f(order-1,i-1,points))).divides(points.getItem(i, 0).minus(points.getItem(i-order, 0)));
	}

	/**
	 * Returns the Newton Interpolation Polynomial for the points given
	 * @param points The table of ordered pairs x,y.
	 * @return The Newton Interpolation Polynomial
	 */
	public Polynom interpolationNewtonORIGINAL(MatrixComplex points) {
		Polynom newtonPolynom = new Polynom(0);
		newtonPolynom.setItem(0, 0, points.getItem(0, 1));
		for (int order = 1; order < points.rows(); ++order ) {
			Polynom term = new Polynom("1");
			for (int i = 0; i < order; ++i) {
				term = term.times(new Polynom(1).fromRoots(points.getItem(i, 0).toString()));
			}
			newtonPolynom = newtonPolynom.plus(term.times(f(order, order, points)));
		}
		return newtonPolynom;
	}

	/**
	 * Returns the Newton Interpolation Polynomial for the points given
	 * @param points The table of ordered pairs x,y.
	 * @return The Newton Interpolation Polynomial
	 */
	public Polynom interpolationNewton(MatrixComplex points) {
		Polynom newtonPolynom = new Polynom(0);
		newtonPolynom.setItem(0, 0, points.getItem(0, 1));
		for (int order = 1; order < points.rows(); ++order ) {
			Polynom term = new Polynom("1");
			for (int i = 0; i < order; ++i) {
				Polynom polyRoot = new Polynom(1);
				polyRoot.setItem(0, 0, points.getItem(i, 0).opposite());
				polyRoot.setItem(0, 1, 1);
				term = term.times(polyRoot);
			}
			newtonPolynom = newtonPolynom.plus(term.times(f(order, order, points)));
		}
		return newtonPolynom;
	}

	/**
	 * Lagrange's polynomial of coefficient k
	 * @param k The k coefficient
	 * @param points The x,y tuplas
	 * @return The k Lagrange's polynomial  
 	 */
	public Polynom L(int k, MatrixComplex points) {
		Polynom Lpolynom = new Polynom("1");
		for (int j = 0; j < points.rows(); ++j) {
			if (j == k) continue;
			Polynom termPolynom = new Polynom(1);
			termPolynom.setItem(0, 0, points.getItem(j, 0).opposite());
			termPolynom.setItem(0, 1, 1);
			Lpolynom = Lpolynom.times(termPolynom.divides(points.getItem(k, 0).minus(points.getItem(j, 0))));
		}
		return Lpolynom ;
	}
	
	/**
	 * https://ocw.unican.es/pluginfile.php/1789/course/section/1349/Capitulo2.pdf
	 * @param points
	 * @return
	 */
	public Polynom interpolationLagrange(MatrixComplex points) {
		Polynom lagrangePolynom = new Polynom(0);
		for (int i = 0; i < points.rows(); ++i ) {
			lagrangePolynom = lagrangePolynom.plus(L(i, points).times(points.getItem(i, 1)));
		}
		return lagrangePolynom;
	}

	/**
	 * 
	 * @param points
	 * @param showme
	 * @return
	 */
	public Polynom interpolationVandermonde(MatrixComplex points, boolean showme) {
		Polynom vandermondePolynom = new Polynom(points.rows());
		Syseq vandermondeSyseq = new Syseq(points.rows());
		MatrixComplex solution = new MatrixComplex(1, points.rows());
		int row, col;
		
		for (row = 0; row < points.rows(); ++row) {
			for (col = 0; col < points.rows(); ++col) {
				vandermondeSyseq.setItem(row, col, points.getItem(row, 0).power(col));
			}
			vandermondeSyseq.setItem(row, col, points.getItem(row, 1));
		}
		solution = vandermondeSyseq.solve();
		if (showme) {
			vandermondeSyseq.print("Vandermonde Equations System");
			solution.println("Vandermonde Eq. Sys. Solution");
		}
		vandermondePolynom.setRow(0, solution.getRow(0));
		return vandermondePolynom;
	}
	
}
