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
import java.util.ArrayList;
import java.util.List;
import com.panayotis.gnuplot.JavaPlot;

public class Polynom extends MatrixComplex {
	private Complex[][] polyNorm;
	private Polynom remainder;

	private static double sampleBase = 300;

	private final static String HEADINFO = "Polynom --- INFO: ";
	private final static String VERSION = "1.2 (2021_0929_2000)";
	/* VERSION Release Note
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
		polynom.reverse(); // The array is inverted to adecuate it with the columns indexes
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
	public void setSampleBase(double value) {
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
		//polynom = polynom.replace("i", "I");

		return polynom;
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
		return "roots(" + toWolfram_poly() + ")";	
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
	 * Calculates the result of evaluating a polynomial for a specific complex value.
	 * @param value The value to use in the polynomial.
	 * @return The complex number resultant of evaluating the polynomial for "value".
	 */
	public Complex eval(Complex value) {
		int rowLen = this.rows();
		int colLen = this.cols();
		if (rowLen != 1) {
			System.err.println("Not valid matrix: The matrix doesn't represent polynomial.");
			System.exit(1);
		}

		Complex cRes = new Complex();
		int i;
		for (i = 1; i < colLen; ++i)
			cRes = cRes.plus(this.complexMatrix[0][i].times(value.power(i)));
		cRes = cRes.plus(this.complexMatrix[0][0]);
		return cRes;
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

		Complex cRes = new Complex();
		for (int i = 1; i < colLen; ++i)
			cRes = cRes.plus(this.polyNorm[0][i].times(value.power(i)));
		cRes = cRes.plus(this.polyNorm[0][0]);
		return cRes;
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
	 * Finds the roots to a Nth degree equation with a determined precision using the Weierstrass method.
	 * Durand-Kerner Method. Discovered by Karl Weierstrass in 1891 and rediscovered independently by Durand in 1960 and Kerner in 1966.
	 * @param precision The precision used to identify a zero.
	 * @return The column array with the solutions found.
	 */
	public MatrixComplex solveWeierstrass(double precision) {
		final boolean DEBUG_ON = false; 
		int rowLen = this.rows();
		int colLen = this.cols();
		Complex cDenom = new Complex(1,0);
		int iter = 0;

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

				finished &= (cCoef.complexMatrix[1][i].minus(cCoef.complexMatrix[0][i])).mod() < precision;
				cCoef.complexMatrix[1][i] = cCoef.complexMatrix[0][i];
			}

			//Sometimes ending condition doesn't work
			if (iter++ > 5000) finished = true;
		} while (!finished);			

		//int numOfDecs = (int) Math.abs(Math.log10(precision)) / 2 + 1;
		//numOfDecs = numOfDecs-1 > 0 ? --numOfDecs : numOfDecs;
		double maxPrec = Math.sqrt(precision);
		int numOfDecs = (int) Math.abs(Math.log10(maxPrec));

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
			cSol.complexMatrix[i][0] = Complex.round(cCoef.complexMatrix[0][i],numOfDecs); //DOESN'T WORK. round IS NOT OK
			//cSol.complexMatrix[i][0] = cCoef.complexMatrix[0][i];
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
	public void plot(String GNUplotExpression,double loLimit, double upLimit) {
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
	 * Plots the polynomial between loLimit and upLimit.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plot(double loLimit, double upLimit) {
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
	 * Plots the Real part of the polynomial between loLimit and upLimit.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotRe(double loLimit, double upLimit) {
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
	 * Plots the Imaginary part of the polynomial between loLimit and upLimit.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotIm(double loLimit, double upLimit) {
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
	 * Plots the Real Imaginary parts of the polynomial between loLimit and upLimit in the same graphic.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotReIm(double loLimit, double upLimit) {
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
	 * Plots the nosenses Real Imaginary parts of the polynomial between loLimit and upLimit in the same graphic.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotRepIm(double loLimit, double upLimit) {
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
	 * Plots the modulus (Abs) of the polynomial between loLimit and upLimit in the same graphic.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotAbs(double loLimit, double upLimit) {
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
	 * Plots the phase (atan(Re/Im)) of the polynomial between loLimit and upLimit in the same graphic.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public void plotPhase(double loLimit, double upLimit) {
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
	 * Calculates the Hermite polynomial of degree "degree" using the formula hermite(n) = 2n*hermite(n-1) - 2(n-1)*hermte(n-2)
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
	 * Calculates the probabilists' Hermite polynomial of degree "degree" using the formula hermiteP(n) = 2·x*hermiteP(n-1) - 2(n-1)*hermteP(n-2)
	 * Hermite(0) = 1
	 * Hermite(1) = x
	 * @param degree The degree of the Hermite polynomial.
	 * @return The probabilists' Hermite polynomial of degree "degree".
	 */
	public Polynom hermiteP(int degree) {
		final Polynom hermite0 = new Polynom("1");
		final Polynom hermite1 = new Polynom("1,0");

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
		final Polynom legendre1 = new Polynom("1,0");

		if (degree == 0) return legendre0;
		if (degree == 1) return legendre1;
		Polynom legendre = new Polynom(degree);
		int n = degree - 1;
		legendre = legendre(n).times(legendre1.times(2*n+1)).minus(legendre(n-1).times(n));
		return legendre.divides(degree);
	}

	/**
	 * Calculates the generalized Laguerre polynomial of degree "degree" and alpha "alpha" using the formula (n+1)*laguerre(n+1, alpha) = (2n+1+alpha)·x*laguerre(n,alpha) - (n+alpha)*laguerre(n-1,alpha)
	 * Laguerre(0) = 1
	 * Laguerre(1) = -x+1+alpha
	 * @param degree The degree of the Laguerre polynomial.
	 * @param alpha The value of alpha.
	 * @return The Laguerre polynomial of degree "degree" and alpha "alpha".
	 */
	public Polynom laguerre(int degree, double alpha) {
		final Polynom laguerre0 = new Polynom("1");
		/*
		 * laguerre1 = -x + 1 + alpha
		 */
		final Polynom laguerre1 = new Polynom("-1,0");
		laguerre1.complexMatrix[0][0].setComplexPol(1+alpha, 0);

		if (degree == 0) return laguerre0;
		if (degree == 1) return laguerre1;
		Polynom laguerre = new Polynom(degree);
		int k = degree - 1;
		/*
		 * term = (-x+2k+1+alpha)
		 */
		Polynom term = new Polynom("-1, 0");
		term.complexMatrix[0][0].setComplexPol(2*k+1+alpha,0);
		laguerre = term.times(laguerre(k)).minus(laguerre(k-1).times(k+alpha));
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
		final Polynom chebyshev1 = new Polynom("1,0").times(kind);
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

}
