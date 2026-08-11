package com.ipserc.arith.signal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.plot.SimpleGnuplot;
import com.ipserc.arith.signal.Fourier.e_domain;
import com.ipserc.arith.signal.Fourier.e_lineStyle;
import com.ipserc.arith.signal.Fourier.e_operator;
import com.ipserc.chronometer.*;

public class Laplace extends MatrixComplex  {
	private Function<Complex, Complex> func;
	private Complex loLimit; 
	private Complex upLimit;
	private Complex period;
	private int N;
	private int sampleFreq;
	private MatrixComplex samples;
	private MatrixComplex series;
	private MatrixComplex transform;
	private Complex offset = new Complex();
	private Boolean isSampled = false;
	private Boolean isSerialized = false;
	private Boolean isTransformed = false;
	private String filterData;
	/** Real part of the Laplace variable s=sigma+j*omega used by DLT()/IDLT() (1/T units). */
	private double sigma = 0.0;

	/*
	 * ****************	CONSTRUCTORS ****************
	 */
	/**
	 * Instantiates an empty Laplace object, ready to load Laplace Series Coefficients, or Discrete Laplace Transform items as function samples or DLT coefficients.
	 */
	public Laplace() {
		loLimit = new Complex (); 
		upLimit  = new Complex ();
		period  = new Complex ();
	}
	
	/**
	 * Instantiates an empty Laplace object with the minimal components, ready to load Laplace Series Coefficients, or Discrete Laplace Transform items as function samples or DLT coefficients.
	 * @param nbrSamples The number of samples taken in the interval 
	 * @param DloLimit Lower limit of the points to use with the function.
	 * @param DupLimit Upper limit of the points to use with the function.
	 */
	public Laplace(int nbrSamples, double DloLimit, double DupLimit) {
		this.loLimit = new Complex(DloLimit,0);
		this.upLimit = new Complex(DupLimit,0);
		this.period = upLimit.minus(loLimit);
		this.sampleFreq = nbrSamples;
		this.N = nbrSamples;
	}

	/**
	 * Instantiates an empty Laplace object with the minimal components, ready to load Laplace Series Coefficients, or Discrete Laplace Transform items as function samples or DLT coefficients.
	 * @param nbrSamples The number of samples taken in the interval 
	 * @param CloLimit Lower limit of the points to use with the function, as a Complex number. Only the real part will be used.
	 * @param CupLimit Upper limit of the points to use with the function, as a Complex number. Only the real part will be used.
	 */
	public Laplace(int nbrSamples, Complex CloLimit, Complex CupLimit) {
		this.loLimit = CloLimit;
		this.upLimit = CupLimit;
		this.period = upLimit.minus(loLimit);
		this.sampleFreq = nbrSamples;
		this.N = nbrSamples;
	}

	/**
	 * Instantiates the Laplace object, using a function definition and its domain.
	 * @param func The function to work with. Must return data in the Complex domain.
	 * @param DloLimit Lower limit of the points to use with the function.
	 * @param DupLimit Upper limit of the points to use with the function.
	 */
	public Laplace(Function<Complex, Complex> func, double DloLimit, double DupLimit) {
		this.func = func;
		this.loLimit = new Complex(DloLimit,0);
		this.upLimit = new Complex(DupLimit,0);
		this.period = upLimit.minus(loLimit);
	}

	/**
	 * Instantiates the Laplace object, using a function definition and its domain.
	 * @param func The function to work with. Must return data in the Complex domain.
	 * @param loLimit Lower limit of the points to use with the function as a Complex number. Only the real part will be used.
	 * @param upLimit Upper limit of the points to use with the function as a Complex number. Only the real part will be used.
	 */
	public Laplace(Function<Complex, Complex> func, Complex loLimit ,Complex upLimit) {
		this.func = func;
		this.loLimit = loLimit;
		this.upLimit = upLimit;
		this.period = upLimit.minus(loLimit);
		//this.isSerialized = false;
		//this.isTransformed = false;
	}
	
	/**
	 * Creates an instance of the Laplace object, using the sampled values of the function.
	 * It requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled
	 * 	sampleFreq: The frequency used to sample the function
	 *  filterData: The data facts if the samples describe a filter
	 * After this, the N samples expressed as 0.0000+0.0000i
	 * @param pathSamples The path to the file with the sampled values.
	 */
	public Laplace(String pathSamples) {
		readSamples(pathSamples, "");
	}

	/**
	 * Creates an instance of the Laplace object, using the sampled values of the function.
	 * It requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled
	 * 	sampleFreq: The frequency used to sample the function
	 *  filterData: The data facts if the samples describe a filter
	 * After this, the N function samples expressed as 0.0000<separator>0.0000
	 * @param pathSamples The path to the file with the sampled values.
	 * @param separator The character used to separate the real part from the imaginary one.
	 */
	public Laplace(String pathSamples, String separator) {
		readSamples(pathSamples, separator);
	}
	
	/*
	 * **************** GETTERS / SETTERS ****************
	 */
	/**
	 * Gets the sample frequency used to get the function samples
	 * @return The sample frequency
	 */
	public int getSampleFreq() {
		return this.sampleFreq;
	}

	/**
	 * Gets the Nbr of samples used to sample the function
	 * @return The Nbr of samples
	 */
	public int getN() {
		return this.N;
	}

	/**
	 * Gets the value at index idx of the DLT transform (row 1 -- row 0 holds the associated
	 * sample index/abscissa, see DLT()).
	 * @param idx The coefficient index.
	 * @return The DLT coefficient at idx.
	 */
	public Complex getTransformItem(int idx) {
		return this.transform.getItem(1, idx);
	}

	/**
	 * Gets the value at index idx of the time-domain samples (row 1 -- row 0 holds the associated
	 * abscissa point).
	 * @param idx The sample index.
	 * @return The sample value at idx.
	 */
	public Complex getSampleItem(int idx) {
		return this.samples.getItem(1, idx);
	}

	/**
	 * Gets the lower limit of the abscissa axis as a complex number
	 * @return The lower limit of the abscissa axis as a complex number
	 */
	public Complex getLoLimit() {
		return this.loLimit;
	}
	
	/**
	 * Gets the upper limit of the abscissa axis as a complex number
	 * @return The upper limit of the abscissa axis as a complex number
	 */
	public Complex getUpLimit() {
		return this.upLimit;
	}
	
	/**Complex
	 * Gets the length of the period of the function calculated as upper limit minus lower limit
	 * @return The length of the period of the function
	 */
	public Complex getPeriod() {
		return this.period;
	}

	/**
	 * Gets sigma, the real part of the Laplace variable s=sigma+j*omega used by DLT()/IDLT().
	 * @return sigma
	 */
	public double getSigma() {
		return this.sigma;
	}

	/**
	 * Sets sigma, the real part of the Laplace variable s=sigma+j*omega used by DLT()/IDLT().
	 * @param sigma The decay rate to evaluate the DLT at.
	 */
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	/*
	 * **************** FUNCTION SAMPLER METHODS ****************
	 */
	/**
	 * Does the sampling of the function for the Laplace Series Analysis. The samples are stored in the Laplace Object
	 */
	public void doSrsSampling() {
		//this.N = sampleFreq;
		Complex incr = upLimit.minus(loLimit).divides(N);
		System.out.printf("Sample step: %e s\n", incr.rep());
		Complex point = loLimit.copy();
		samples = new MatrixComplex(2,N);
		for (int n = 0; n < N; ++n) { // time index
			samples.setItem(0, n, point);
			samples.setItem(1, n, func.apply(point));
			point = point.plus(incr);
		}
		isSampled = true;
	}

	/**
	 * Does the sampling of the function for the Discrete Laplace Transform. The samples are stored in the Laplace Object
	 */
	public void doTrfSampling() {
		doSrsSampling();
	}
	
	/*
	 * **************** SAVE AND READ METHODS TO STORE CALCULATIONS ****************
	 */
	/**
	 * Saves the 'data' in a given file in text format.
	 * @param filePath The path to the file in which the data are saved.
	 * @param data The array with the data. The data are stored in the columns.
	 * @return The status of the save operation.
	 */
	private Boolean saveTFile(String filePath, MatrixComplex data, String separator) {
		Complex.storeFormatStatus();
		Complex.resetFormatStatus();
		Boolean fsaved = false;
		System.out.println("writing data into " + filePath);
	    try {
	        FileWriter fWriter = new FileWriter(filePath);
	        fWriter.write(loLimit.toString()+System.lineSeparator()); 
	        fWriter.write(upLimit.toString()+System.lineSeparator());
	        fWriter.write(period.toString()+System.lineSeparator());
	        fWriter.write(N+System.lineSeparator());
	        fWriter.write(sampleFreq+System.lineSeparator());
	        fWriter.write(filterData+System.lineSeparator());
			//for (int i = 0; i < transform.cols(); ++i) {
			for (int i = 0; i < this.N; ++i) {
				if (separator != "") {
					fWriter.write(data.getItem(0,i).rep()+separator+data.getItem(0,i).imp()+System.lineSeparator());	
				}
				else fWriter.write(data.getItem(0,i).toString()+System.lineSeparator());
			}
	        fWriter.close();
	        System.out.println("Successfully wrote to the file.");
	        fsaved = true;
	      } catch (IOException e) {
	        System.out.println("Error writing file:" + e.getCause());
	        e.printStackTrace();
	      }
		Complex.restoreFormatStatus();
	    return fsaved;
	}
	
	/**
	 * Saves the samples of the function analyzed as Re<separator>Im in a given file in text format.
	 * @param filePath The path to the file in which the data are saved.
	 * @param separator The character to separate the real part from the imaginary one.
	 * @return The status of the save operation.
	 */
	public Boolean saveSamples(String filePath, String separator) {
		if(!isSampled) {
			System.out.println("WARNING:Function not sampled yet. Sample it first.");
			return false;
		}
		MatrixComplex newSamples = new MatrixComplex(1,N);
		newSamples.copyRow(0, samples, 1);
		return this.saveTFile(filePath, newSamples, separator);
	}

	/**
	 * Saves the samples of the function analyzed as a+bi in a given file in text format.
	 * @param filePath The path to the file in which the data are saved.
	 * @return The status of the save operation.
	 */
	public Boolean saveSamples(String filePath) {
		return saveSamples(filePath, "");
	}

	/**
	 * Saves the coefficients of the DLT as Re<separator>Im in a given file in text format.
	 * @param filePath The path to the file in which the data are saved.
	 * @param separator the character to separate the real part from the imaginary one.
	 * @return The status of the save operation.
	 */
	public Boolean saveDLT(String filePath, String separator) {
		if(!isTransformed) {
			System.out.println("WARNING:Function not transformed yet. Transform it first.");
			return false;
		}
		return this.saveTFile(filePath, transform, separator);
	}
	
	/**
	 * Saves the coefficients of the DLT as a+bi in a given file in text format.
	 * @param filePath The path to the file in which the data are saved.
	 * @return The status of the save operation.
	 */
	public Boolean saveDLT(String filePath) {
		return saveDLT(filePath, "");
	}
	
	/**
	 * Reads the samples of the function as Re<separator>Im to be analyzed from a given file in text format.
	 * The file requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled.
	 * 	sampleFreq: The frequency used to sample the function.
	 * After this, the N function samples expressed as 0.0000<separator>0.0000
	 * if separator is null the sampled should be expressed as 0.0000+0.0000i
	 * @param filePath The path to the file in which the data are stored.
	 * @param separator The character to separate the real part from the imaginary one.
	 * @return The status of the read operation.
	 */
	public Boolean readSamples(String filePath, String separator) {
		Boolean fread = false;
		this.loLimit = new Complex();
		this.upLimit = new Complex();
		this.period = new Complex();
	    try {
	        File fileObj = new File(filePath);
	        BufferedReader br = new BufferedReader(new FileReader(fileObj));
	    	loLimit.setComplex(br.readLine()); 
	    	upLimit.setComplex(br.readLine());
	    	period.setComplex(br.readLine());
	    	N = Integer.parseInt(br.readLine());
	    	sampleFreq = Integer.parseInt(br.readLine());
	    	filterData = br.readLine();

	    	samples = new MatrixComplex(2, N);
	    	int n = 0;
	    	Complex point = loLimit.copy();
	    	Complex incr = upLimit.minus(loLimit).divides(N);
	    	Complex cVal = new Complex();
	    	String line;
	        while ((line = br.readLine()) != null) {
				if (separator == "") cVal.setComplex(line);
				else {
					String value[] = line.split(separator);
					cVal.setComplexRec(Double.parseDouble(value[0]), Double.parseDouble(value[1]));
				}
				samples.setItem(0, n, point);
				samples.setItem(1, n++, cVal);
				point = point.plus(incr);
	        }
	        br.close();
	        fread = true;
		    if (n != N) System.out.println("WARNING: The number of samples doesn't math the number of data in the file.");
	      } catch (IOException e) {
		        System.out.println("Error reading file:" + e.getCause());
		        e.printStackTrace();
	      }
	    isSampled = fread;
		return fread;
	}

	/**
	 * Reads the samples of the function as a+bi to be analyzed from a given file in text format.
	 * The file requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled.
	 * 	sampleFreq: The frequency used to sample the function.
	 * After this, the N function samples expressed as 0.0000+0.0000i
	 * @param filePath The path to the file in which the data are stored.
	 * @return The status of the read operation.
	 */
	public Boolean readSamples(String filePath) {
		return 	readSamples(filePath, "");
	}

	/**
	 * Reads the DLT coefficients of the transformed function as Re<separator>Im from a given file in text format.
	 * The file requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled.
	 * 	sampleFreq: The frequency used to sample the function.
	 * After this, the N DLT coefficients expressed as 0.0000<separator>0.0000
	 * if separator is null the sampled should be expressed as 0.0000+0.0000i
	 * @param filePath The path to the file in which the data are stored.
	 * @param separator The character to separate the real part from the imaginary one.
	 * @return The status of the read operation.
	 */
	public Boolean readDLT(String filePath, String separator) {
		Boolean fread = false;
		this.loLimit = new Complex();
		this.upLimit = new Complex();
		this.period = new Complex();
	    try {
	        File fileObj = new File(filePath);
	        BufferedReader br = new BufferedReader(new FileReader(fileObj));
	    	loLimit.setComplex(br.readLine()); 
	    	upLimit.setComplex(br.readLine());
	    	period.setComplex(br.readLine());
	    	N = Integer.parseInt(br.readLine());
	    	sampleFreq = Integer.parseInt(br.readLine());
	    	filterData = br.readLine();

	    	transform = new MatrixComplex(1, N);
	    	int n = 0;
	    	Complex point = loLimit.copy();
	    	Complex incr = upLimit.minus(loLimit).divides(N);
	    	Complex cVal = new Complex();
	    	String line;
	        while ((line = br.readLine()) != null) {
				if (separator == "") cVal.setComplex(line);
				else {
					String value[] = line.split(separator);
					cVal.setComplexRec(Double.parseDouble(value[0]), Double.parseDouble(value[1]));
				}
				transform.setItem(0, n++, cVal);
				point = point.plus(incr);
	        }
	        br.close();
	        fread = true;
		    if (n != N) System.out.println("WARNING: The number of samples doesn't math the number of data in the file.");
	      } catch (IOException e) {
		        System.out.println("Error reading file:" + e.getCause());
		        e.printStackTrace();
	      }
	    isTransformed = fread;
		return fread;
	}

	/**
	 * Reads the DLT coefficients of the transformed function as a+bi from a given file in text format.
	 * The file requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled.
	 * 	sampleFreq: The frequency used to sample the function.
	 * After this, the N function samples expressed as 0.0000+0.0000i
	 * @param filePath The path to the file in which the data are stored.
	 * @return The status of the read operation.
	 */
	public Boolean readDLT(String filePath) {
		return readDLT(filePath, "");
	}

	/*
	 * **************** INTEGRATION METHODS FOR FOURIER SERIES ****************
	 */
	/**
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the real axis
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param expz the function EXP((-2*pi*n*i/T)*z)
	 * @param numDec the number of significant decimals 
	 * @return The COMPLEX of the integral
	 */
 	private static Complex integrate(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, Function <Complex, Complex> expz, int numDec) {
		Complex vector = uplimit.minus(lolimit);
		double vectSlope = vector.imp()/vector.rep();
		double vectAngle = Math.atan(vectSlope);
		double precision = Math.pow(10, -Math.abs(numDec+2));
		
		vectAngle = vectAngle > Math.PI ? Math.PI - vectAngle : vectAngle;
		vectAngle = vectAngle < -Math.PI ? Math.PI + vectAngle : vectAngle;
		
		if (((vectAngle >= Math.PI/4) && (vectAngle < 3*Math.PI/4 )) ||
				((vectAngle >= -3*Math.PI/4) && (vectAngle < -Math.PI/4 ))) {
			return integrateIM(lolimit, uplimit, func, expz, precision);
		}
		else return integrateRE(lolimit, uplimit, func, expz, precision);
	}
	
	/**
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the real axis
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param expz the function EXP((-2*pi*n*i/T)*z)
	 * @param numDec the number of significant decimals 
	 * @return The COMPLEX of the integral
	 */
	private static Complex integrateRE(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, Function <Complex, Complex> expz, double precision) {
		Complex vector = uplimit.minus(lolimit);
		Complex nextPoint = new Complex();
		Complex integral = new Complex();

		//Recorrer la recta con distancia Euclidea
		double vectSlope = vector.imp()/vector.rep();
		double vectAngle = Math.atan(vectSlope);
		double projRe = vector.mod() * Math.cos(vectAngle);
		double stepRe = projRe * precision * Math.signum(vector.rep());
		double nextRep, nextImp;
		
		int iter = 0;
		nextPoint = lolimit.copy();
		
		/* DBUG SECTION * /
		System.out.println("vectSlope:" + vectSlope);
		System.out.println("vectAngle: PI*" + vectAngle*Math.PI);
		System.out.println("projRe   :" + projRe);
		System.out.println("stepRe   :" + stepRe);
		System.out.println("iter:" + iter + "   nextPoint:" + lolimit.toString());
		/* DBUG SECTION */

		Complex val = new Complex();
		val = func.apply(lolimit).times(expz.apply(lolimit));
		integral = val;

		while (++iter <= 1/precision) {
			//System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
			nextRep = nextPoint.rep() + stepRe;
			nextImp = lolimit.imp() + vectSlope * (nextRep - lolimit.rep());
			nextPoint.setComplexRec(nextRep, nextImp);
			val = func.apply(nextPoint).times(expz.apply(nextPoint));
			integral = integral.plus(val);
		}		
		// System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
		return integral.times(uplimit.minus(lolimit)).divides(iter);
	}

	/**
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the imaginary axis
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param expz the function EXP((-2*pi*n*i/T)*z)
	 * @param numDec the number of significant decimals 
	 * @return The COMPLEX of the integral
	 */
	private static Complex integrateIM(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, Function <Complex, Complex> expz, double precision) {
		Complex vector = uplimit.minus(lolimit);
		Complex nextPoint = new Complex();
		Complex integral = new Complex();

		//Recorrer la recta con distancia Euclidea
		double vectSlope = vector.rep()/vector.imp();
		double vectAngle = Math.atan(vectSlope);
		double projIm = vector.mod() * Math.cos(vectAngle);
		double stepIm = projIm * precision * Math.signum(vector.imp());
		double nextRep, nextImp;
		
		int iter = 0;
		nextPoint = lolimit.copy();
		
		/* DBUG SECTION * /
		System.out.println("vectSlope:" + vectSlope);
		System.out.println("vectAngle: PI*" + vectAngle*Math.PI);
		System.out.println("projIm   :" + projIm);
		System.out.println("stepIm   :" + stepIm);
		System.out.println("iter:" + iter + "   nextPoint:" + lolimit.toString());
		/* DEBUG SECTION */
		
		Complex val = new Complex();
		val = func.apply(lolimit).times(expz.apply(lolimit));
		integral = val;

		while (++iter <= 1/precision) {
			//System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
			nextImp = nextPoint.imp() + stepIm;
			nextRep = lolimit.rep() + vectSlope * (nextImp - lolimit.imp());
			nextPoint.setComplexRec(nextRep, nextImp);
			val = func.apply(nextPoint).times(expz.apply(nextPoint));
			integral = integral.plus(val);
		}
		// System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
		return integral.times(uplimit.minus(lolimit)).divides(iter);
	}

	/*
	 * **************** MATHEMATICAL METHODS ****************
	 */
	/**
	 * Indicates if the function is continuous at the given point.
	 * @param p The point at which the continuity is analyzed. Only the real part is evaluated.
	 * @return True if the function is continuous, false otherwise.
	 */
	private Boolean isContinue(Complex p) {
		Complex fp, fp1;
		fp = func.apply(p);
		fp1 = func.apply(p.plus(Complex.precision()));
		//fp.println("fp :");
		//fp1.println("fp1:");
		if (fp1.equals(fp)) return true;
		return false;
	}
		
	/*
	 * **************** CONTINUOUS LAPLACE TRANSFORM ****************
	 */
	/**
	 * Calculates the CLT (Continuous Laplace Transform) at N points s_n = sigma + j*2*pi*n/T,
	 * T = upLimit-loLimit (the window duration) -- the continuous-integral analog of DLT(), same
	 * s_n grid, evaluated via direct numerical integration instead of a discrete sum:
	 * X(s_n) = INTEGRAL_loLimit^upLimit f(z)*e^(-s_n*z) dz.
	 * sigma (the field set by setSigma()/DLT()) is the real part of s; sigma=0 makes s_n purely
	 * imaginary, matching the kernel serialize() uses for the Fourier series coefficients over the
	 * same domain (up to serialize()'s own 2/T normalization).
	 * @param nbrSamples The number of s_n points to evaluate (and, if not already sampled, the
	 * number of time-domain samples taken for the abscissa bookkeeping in transform's row 0).
	 * @param decPrec Precision passed to the underlying Riemann-sum integration.
	 */
	public void CLT(int nbrSamples, int decPrec) {
		Complex coef;

		if(!isSampled) {
			this.N = nbrSamples;
			this.sampleFreq = nbrSamples;
			doSrsSampling();
		}

		this.transform = new MatrixComplex(2, N);

		/*CHRONO*/ Chronometer chrono = new Chronometer();
		/*CHRONO*/ chrono.start();

		for (int n = 0; n < N ; ++n) {
			Complex sn = new Complex(sigma, 0).plus(Complex.i.times(Complex.DOS_PI * n).divides(period)); // s_n = sigma + j*2*pi*n/T
			Complex cn = sn.opposite(); // -s_n
			Function<Complex, Complex> expz = z -> Complex.exp(z.times(cn)); // e^(-s_n*z)
			coef = integrate(loLimit, upLimit, func, expz, decPrec);
			transform.setItem(0, n, samples.getItem(0, n));
			transform.setItem(1, n, coef);
		}

		/*CHRONO*/ chrono.stop();
		/*CHRONO*/ System.out.println("Computing Time CLT:" + chrono.toString());

		isTransformed = true;
		//setOffset();
	}
	

	/*
	 * Prints in the console the Fourier Series coefficients calculated as a vector.
	 */
	/* *********************************************************************************************************************************************************** * /

	public void printCoefs() {
		if(!isSerialized) {
			System.out.println("WARNING (printCoefs):Function not serialized yet. Serialize it first.");
			return;
		}
		//series.transpose().println("Fourier Series Coefs ");
		System.out.println("Fourier Series Coefs ");
		series.println();
	}
	/ * *********************************************************************************************************************************************************** */

	/**
	 * Prints in the console the Fourier Series coefficients calculated.
	 */
	/* *********************************************************************************************************************************************************** * /
	public void printSCoefs() {
		if(!isSerialized) {
			System.out.println("WARNING (printCoefs):Function not serialized yet. Serialize it first.");
			return;
		}
		//series.transpose().println("Fourier Series Coefs ");
		System.out.println("Fourier Series Coefs ");
		//System.out.println("C0 = " + series.getItem(0,0).toString());
		int idxDig = (N+"").length();
		String strFormat = "C%0"+idxDig+"d = %s\n";
		for (int i = 0; i < series.cols(); ++i) {
			System.out.printf(strFormat, i, series.getItem(0,i).toString());
		}
	}
	/ * *********************************************************************************************************************************************************** */

	/**
	 * Returns the value of the Fourier Series at a given point.
	 * @param p The point in which the Series is evaluated. Only the real part has sense.
	 * @return The value returned from the Fourier Series.
	 * @apiNote DEAD CODE, parked not fixed (Vigesimosexta sesion, auditoria matematica): {@code
	 * series}/{@code isSerialized} (the state this method reads) are declared in this class but
	 * NEVER assigned anywhere -- unlike {@code Fourier.java}, this class has no {@code serialize()}
	 * method to populate them, so {@code isSerialized} stays {@code false} forever and this method
	 * always hits the early-return guard, returning exactly {@code Complex(0)} for ANY input {@code
	 * p} regardless of the actual signal -- it never evaluates any series, contradicting its own
	 * Javadoc. Confirmed zero callers anywhere in this codebase. Same root cause already parked (via
	 * the {@code "* /"} convention) on this method's two immediate neighbors, {@code printCoefs()}/
	 * {@code printSCoefs()}, just above -- this is the third and last method depending on that
	 * never-populated state, parked here the same way for consistency rather than inventing a new
	 * {@code serialize()} implementation that was never asked for. The class's real, working,
	 * already-audited API is {@link #CLT(int, int)}/{@link #DLT(int, double)}/{@link #IDLT()}, none
	 * of which touch {@code series}.
	 */
	/* *********************************************************************************************************************************************************** * /
	public Complex calc(Complex p) {
		Complex val = new Complex(0);

		if(!isSerialized) {
			System.out.println("WARNING (Calc):Function not serialized yet. Serialize it first.");
			return val ;
		}

		val = val.plus(series.getItem(0,0).times(2));
		for (int i = 1; i < series.cols(); ++i) {
			val = val.plus(series.getItem(0,i).times(Complex.exp(Complex.i.times(p).times(Complex.DOS_PI * i).divides(period))));
		}
		return val.minus(offset);
	}
	/ * *********************************************************************************************************************************************************** */

	/*
	 * **************** DISCRETE LAPLACE TRANSFORM METHODS ****************
	 *
	 * Arreglado (Decimonovena sesion, ver Claude/ComplexArithRev.md): la nota original de este
	 * bloque decia "NO SE HA CONSEGUIDO OBTENER UNA APROXIMACION ACEPTABLE AL CALCULO DE LA
	 * TRANSFORMADA DISCRETA DE LAPLACE" -- dos causas raiz distintas encontradas:
	 * (1) DLT() aplicaba un peso exponencial e^{-n} fijo (".minus(n)" en el kernel), es decir
	 * sigma=1/T sin parametrizar ni justificar -- ahora sigma es un parametro explicito (por
	 * defecto 0, caso verificable: sigma=0 colapsa exactamente a la DFT de las muestras, ver
	 * Fourier.DFT()).
	 * (2) IDLT() calculaba el kernel de la inversa con Complex.exp(double) -- esa sobrecarga
	 * devuelve la EXPONENCIAL REAL e^x (ver ComplexFunctions.exp(double): construye
	 * new Complex(d) con parte imaginaria CERO antes de exponenciar), no e^{i*angulo}. Pasarle un
	 * angulo real (double) no da un punto en la circunferencia unidad, da un real que crece sin
	 * limite -- comparar con Fourier.IDFT(), que si usa Complex.exp(Complex) sobre
	 * Complex.i.times(angulo). La formula era estructuralmente incapaz de invertir nada.
	 */
	/**
	 * Calculates the DLT (Discrete Laplace Transform) at N points s_k = sigma + j*omega_k,
	 * omega_k = 2*pi*k/(N*T), T el paso de muestreo -- equivalente a evaluar
	 * X(s_k) = SUM_n x[n]*e^(-s_k*n*T) = DFT{x[n]*e^(-sigma*n*T)}[k].
	 * sigma=0 reduce exactamente a la DFT de las muestras (caso de referencia verificable contra
	 * Fourier.DFT()).
	 * @param sampleFreq The frequency used to sample the function.
	 * @param sigma The real part of the Laplace variable s (decay rate), in 1/T units.
	 */
	public void DLT(int sampleFreq, double sigma) {
		this.sampleFreq = sampleFreq;
		this.N = sampleFreq;
		this.sigma = sigma;
		Complex idospiN = Complex.i.times(-Complex.DOS_PI/N); // -2*pi*i/N

		System.out.println("Samples:" + this.N);
		System.out.printf("sample frequency: %.3e Hz\n",(double)sampleFreq);
		System.out.printf("sigma: %.3e\n", sigma);

		if (!isSampled) doTrfSampling();

		double T = upLimit.minus(loLimit).divides(N).rep();

		transform = new MatrixComplex(2,N);

		/*CHRONO*/ Chronometer chrono = new Chronometer();
		/*CHRONO*/ chrono.start();

		for (int k = 0; k < N; ++k) { // freq index
			Complex Ak = new Complex();
			for (int n = 0; n < N; ++n) { // time index
				// e^(-s_k*n*T) = e^(-sigma*n*T) * e^(-j*2*pi*k*n/N)
				Complex expkn = Complex.exp(idospiN.times(k*n)).times(Math.exp(-sigma*n*T));
				Ak = Ak.plus(this.samples.getItem(1,n).times(expkn));
			}
			transform.setItem(0, k, this.samples.getItem(0,k));
			transform.setItem(1, k, Ak);
		}

		/*CHRONO*/ chrono.stop();
		/*CHRONO*/ System.out.println("Computing Time DLT:" + chrono.toString());

		isTransformed = true;
	}

	/**
	 * Calculates the DLT with sigma=0 (evaluates purely on the j*omega axis -- equivalent to the
	 * DFT of the samples).
	 * @param sampleFreq The frequency used to sample the function.
	 */
	public void DLT(int sampleFreq) {
		DLT(sampleFreq, 0.0);
	}

	/**
	 * Calculates the DLT using the signal definitions.
	 */
	public void DLT() {
		DLT(this.sampleFreq, this.sigma);
	}

	/**
	 * Calculates the samples of the function using the Inverse DLT:
	 * x[n] = (1/N) * SUM_k X(s_k) * e^(s_k*n*T), the exact inverse of DLT(sampleFreq, sigma).
	 */
	public void IDLT() {
		if(!isTransformed) {
			System.out.println("WARNING:DLT coeficients not calculated/loaded. Do the DLT or Load them first.");
			return;
		}

		System.out.println("Computing the Inverse DLT...");
		Complex idospiN = Complex.i.times(Complex.DOS_PI/N); // +2*pi*i/N
		samples = new MatrixComplex(2,N);
		Complex point = loLimit.copy();
    	Complex incr = upLimit.minus(loLimit).divides(N);
		double T = incr.rep();
		Complex Tk = new Complex();

		/*CHRONO*/ Chronometer chrono = new Chronometer();
		/*CHRONO*/ chrono.start();

		for (int n = 0; n < N; ++n) { // time index
			Tk.setComplexRec(0,0);
			for (int k = 0; k < N; ++k) { // freq index
				// e^(s_k*n*T) = e^(sigma*n*T) * e^(j*2*pi*k*n/N)
				Complex expkn = Complex.exp(idospiN.times(k*n)).times(Math.exp(sigma*n*T));
				Tk = Tk.plus(expkn.times(this.transform.getItem(1, k)));
			}
			samples.setItem(0, n, point);
			samples.setItem(1, n, Tk.divides(N));
			point = point.plus(incr);
		}

		/*CHRONO*/ chrono.stop();
		/*CHRONO*/ System.out.println("Computing Time IDLT:" + chrono.toString());
		isSampled = true;
	}

	/**
	 * Prints in the console the DLT coefficients.
	 */
	public void printTCoefs() {
		if(!isTransformed) {
			System.out.println("WARNING:Function not transformed yet. Transform it first.");
			return;
		}
		//series.transpose().println("Laplace Series Coefs ");
		System.out.println("Laplace Transform Coefs ");
		int idxDig = (N+"").length();
		String strFormat = "A%0"+idxDig+"d = %s\n";
		for (int i = 0; i < transform.cols(); ++i) {
			System.out.printf(strFormat, i, transform.getItem(0, i).toString());
		}
	}
	
	/**
	 * Enum for plotting the DLT with different operators.
	 * COMPLEX. Plots the values of the coefficients in rectangular representation.
	 * MAGNITUDE. Plots the values of the coefficients in polar representation.
	 * SQUARE. Plots the squared values of the coefficients in rectangular representation.
	 */
	public static enum e_operator {
		COMPLEX, MAGNITUDE, SQUARE;
	}

	/**
	 * Enum for the two kinds of x-axis units.
	 * SAMP. The x axis represents the index of the coefficients.
	 * FREC. The x axis represents the frequency associated with the coefficient.
	 */
	public static  enum e_domain {
		SAMP, FREC;
	}
	
	/**
	 * Performs the operation required for the different views.
	 * @param cNum The Complex number to operate on.
	 * @param operator The operator as defined in e_operator.
	 * @param logscale True if cNum should be non-negative to be plotted on a logarithmic scale.
	 * @return The resulting number.
	 */
	private Complex eval(Complex cNum, e_operator operator, boolean logscale) {
		switch (operator) {
		case COMPLEX: return logscale ? Complex.positive(cNum) : cNum;
		case MAGNITUDE: return logscale ? Complex.positive(cNum) : cNum; 
		case SQUARE: return logscale ? Complex.positive(cNum.times(cNum)) : cNum.times(cNum);
		}
		return cNum;
	}

	/*
	 * **************** DLT PLOTTING METHODS ****************
	 */
	/**
	 * Enumerative to set the style for gnuplot
	 * LINES The lines style connects adjacent points with straight line segments.
	 * IMPULSES The impulses style displays a vertical line from y=0 to the y value of each point.
	 */
	public static enum e_lineStyle {
		LINES, IMPULSES;
	}

	/**
	 * Plots a graphic with the points given in 'data'. Row 0 is for the x axis values, Row 1 is for the y axis values. The values to plot are in the columns.
	 * @param title The title of the graphic.
	 * @param data The points to be plotted.
	 * @param showIm If true plots the imaginary part in the graphic.
	 * <p>
	 * Delegates to {@link MatrixComplexPlot#plot(String, int, MatrixComplex, boolean,
	 * MatrixComplexPlot.e_lineStyle)} (8 agosto 2026) -- this method used to carry its own,
	 * byte-for-byte identical copy of that logic (also duplicated in {@code Fourier}/{@code Z}).
	 * This class's own {@code e_lineStyle} keeps its exact public signature; only the
	 * implementation moved, converted to {@code MatrixComplexPlot.e_lineStyle} at the boundary.
	 */
	private void plot(String title, int nbrSamples, MatrixComplex data, boolean showIm, e_lineStyle lineStyle, SimpleGnuplot.e_syncMode mode) {
		MatrixComplexPlot.plot(title, nbrSamples, data, showIm,
			lineStyle == e_lineStyle.IMPULSES ? MatrixComplexPlot.e_lineStyle.IMPULSES : MatrixComplexPlot.e_lineStyle.LINES, mode);
	}

	public void plotFunctionSync(String title, int nbrSamples, boolean showIm, e_lineStyle lineStyle) {
		plotFunction(title, nbrSamples, showIm, lineStyle, SimpleGnuplot.e_syncMode.SYNC);
	}

	public void plotFunctionAsync(String title, int nbrSamples, boolean showIm, e_lineStyle lineStyle) {
		plotFunction(title, nbrSamples, showIm, lineStyle, SimpleGnuplot.e_syncMode.ASYNC);
	}

	private void plotFunction(String title, int nbrSamples, boolean showIm, e_lineStyle lineStyle, SimpleGnuplot.e_syncMode mode) {
		if(!isSampled || this.N != nbrSamples) {
			this.N = nbrSamples;
			this.sampleFreq = nbrSamples;
			doSrsSampling();
		}

		plot(title, nbrSamples, samples, showIm, lineStyle, mode);
	}

	/**
	 * Plots the samples of the function used for the Fourier analysis
	 * @param title The title of the graphic.
	 * @param showIm True for plotting the imaginary part.
	 */
	public void plotSamplesSync(String title, boolean showIm, e_lineStyle lineStyle) {
		plot(title, N, samples, showIm, lineStyle, SimpleGnuplot.e_syncMode.SYNC);
	}

	public void plotSamplesAsync(String title, boolean showIm, e_lineStyle lineStyle) {
		plot(title, N, samples, showIm, lineStyle, SimpleGnuplot.e_syncMode.ASYNC);
	}

	/**
	 * Plots the DLT graphic in the domain of the coefficients.
	 * @param Title The title of the plot.
	 * @param showIm True to plot the imaginary part.
	 * @param lineStyle The line style used for the plot.
	 * @apiNote The Javadoc previously listed {@code domain}/{@code operator}/{@code logscale} as
	 * parameters, but the method's actual signature is {@code (String, boolean, e_lineStyle)} --
	 * corrected to match, no behavior change.
	 */
	public void plotDLTSync(String Title, boolean showIm, e_lineStyle lineStyle) {
		plotDLT(Title, showIm, lineStyle, SimpleGnuplot.e_syncMode.SYNC);
	}

	public void plotDLTAsync(String Title, boolean showIm, e_lineStyle lineStyle) {
		plotDLT(Title, showIm, lineStyle, SimpleGnuplot.e_syncMode.ASYNC);
	}

	private void plotDLT(String Title, boolean showIm, e_lineStyle lineStyle, SimpleGnuplot.e_syncMode mode) {
		if(!isTransformed) {
			System.out.println("WARNING:DFT coeficients not calculated/loaded. Do the DFT or Load them first.");
			return;
		}
		plot(Title, N, transform, showIm, lineStyle, mode);
	}


}
