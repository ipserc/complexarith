package com.ipserc.arith.signal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.plot.SimpleGnuplot;
import com.ipserc.chronometer.*;

public class Z extends MatrixComplex {
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
	/** Radius r of the sampling ring z_k=r*e^(j*2*pi*k/N) used by DZT()/IDZT(). r=1 reduces exactly to the DFT. */
	private double radius = 1.0;

	/*
	 * ****************	CONSTRUCTORS ****************
	 */
	/**
	 * Instantiates an empty Z object, ready to load Z Series Coefficients, or Discrete Z Transform items as function samples or DZT coefficients.
	 */
	public Z() {
		loLimit = new Complex ();
		upLimit  = new Complex ();
		period  = new Complex ();
	}

	/**
	 * Instantiates an empty Z object with the minimal components, ready to load Z Series Coefficients, or Discrete Z Transform items as function samples or DZT coefficients.
	 * @param nbrSamples The number of samples taken in the interval
	 * @param DloLimit Lower limit of the points to use with the function.
	 * @param DupLimit Upper limit of the points to use with the function.
	 */
	public Z(int nbrSamples, double DloLimit, double DupLimit) {
		this.loLimit = new Complex(DloLimit,0);
		this.upLimit = new Complex(DupLimit,0);
		this.period = upLimit.minus(loLimit);
		this.sampleFreq = nbrSamples;
		this.N = nbrSamples;
	}

	/**
	 * Instantiates an empty Z object with the minimal components, ready to load Z Series Coefficients, or Discrete Z Transform items as function samples or DZT coefficients.
	 * @param nbrSamples The number of samples taken in the interval
	 * @param CloLimit Lower limit of the points to use with the function, as a Complex number. Only the real part will be used.
	 * @param CupLimit Upper limit of the points to use with the function, as a Complex number. Only the real part will be used.
	 */
	public Z(int nbrSamples, Complex CloLimit, Complex CupLimit) {
		this.loLimit = CloLimit;
		this.upLimit = CupLimit;
		this.period = upLimit.minus(loLimit);
		this.sampleFreq = nbrSamples;
		this.N = nbrSamples;
	}

	/**
	 * Instantiates the Z object, using a function definition and its domain.
	 * @param func The function to work with. Must return data in the Complex domain.
	 * @param DloLimit Lower limit of the points to use with the function.
	 * @param DupLimit Upper limit of the points to use with the function.
	 */
	public Z(Function<Complex, Complex> func, double DloLimit, double DupLimit) {
		this.func = func;
		this.loLimit = new Complex(DloLimit,0);
		this.upLimit = new Complex(DupLimit,0);
		this.period = upLimit.minus(loLimit);
	}

	/**
	 * Instantiates the Z object, using a function definition and its domain.
	 * @param func The function to work with. Must return data in the Complex domain.
	 * @param loLimit Lower limit of the points to use with the function as a Complex number. Only the real part will be used.
	 * @param upLimit Upper limit of the points to use with the function as a Complex number. Only the real part will be used.
	 */
	public Z(Function<Complex, Complex> func, Complex loLimit ,Complex upLimit) {
		this.func = func;
		this.loLimit = loLimit;
		this.upLimit = upLimit;
		this.period = upLimit.minus(loLimit);
		//this.isSerialized = false;
		//this.isTransformed = false;
	}

	/**
	 * Creates an instance of the Z object, using the sampled values of the function.
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
	public Z(String pathSamples) {
		readSamples(pathSamples, "");
	}

	/**
	 * Creates an instance of the Z object, using the sampled values of the function.
	 * It requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled
	 * 	sampleFreq: The frequency used to sample the function
	 *  filterData: The data facts if the samples describe a filter
	 * After this, the N function samples expressed as {@code 0.0000<separator>0.0000}
	 * @param pathSamples The path to the file with the sampled values.
	 * @param separator The character used to separate the real part from the imaginary one.
	 */
	public Z(String pathSamples, String separator) {
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
	 * Gets the value at index idx of the DZT transform (row 1 -- row 0 holds the associated
	 * sample index/abscissa, see DZT()).
	 * @param idx The coefficient index.
	 * @return The DZT coefficient at idx.
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

	/**
	 * Gets the length of the period of the function calculated as upper limit minus lower limit
	 * @return The length of the period of the function
	 */
	public Complex getPeriod() {
		return this.period;
	}

	/**
	 * Gets the radius r of the sampling ring z_k=r*e^(j*2*pi*k/N) used by DZT()/IDZT().
	 * @return radius
	 */
	public double getRadius() {
		return this.radius;
	}

	/**
	 * Sets the radius r of the sampling ring z_k=r*e^(j*2*pi*k/N) used by DZT()/IDZT(). r=1
	 * samples exactly on the unit circle (equivalent to the DFT of the samples).
	 * @param radius The radius to evaluate the DZT at. Must be strictly positive.
	 */
	public void setRadius(double radius) {
		if (radius <= 0) throw new IllegalArgumentException("radius must be > 0, got " + radius);
		this.radius = radius;
	}

	/*
	 * **************** FUNCTION SAMPLER METHODS ****************
	 */
	/**
	 * Does the sampling of the function for the Z Series Analysis. The samples are stored in the Z Object
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
	 * Does the sampling of the function for the Discrete Z Transform. The samples are stored in the Z Object
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
	 * Saves the samples of the function analyzed as {@code Re<separator>Im} in a given file in text format.
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
	 * Saves the coefficients of the DZT as {@code Re<separator>Im} in a given file in text format.
	 * @param filePath The path to the file in which the data are saved.
	 * @param separator the character to separate the real part from the imaginary one.
	 * @return The status of the save operation.
	 */
	public Boolean saveDZT(String filePath, String separator) {
		if(!isTransformed) {
			System.out.println("WARNING:Function not transformed yet. Transform it first.");
			return false;
		}
		return this.saveTFile(filePath, transform, separator);
	}

	/**
	 * Saves the coefficients of the DZT as a+bi in a given file in text format.
	 * @param filePath The path to the file in which the data are saved.
	 * @return The status of the save operation.
	 */
	public Boolean saveDZT(String filePath) {
		return saveDZT(filePath, "");
	}

	/**
	 * Reads the samples of the function as {@code Re<separator>Im} to be analyzed from a given file in text format.
	 * The file requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled.
	 * 	sampleFreq: The frequency used to sample the function.
	 * After this, the N function samples expressed as {@code 0.0000<separator>0.0000}
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
	 * Reads the DZT coefficients of the transformed function as {@code Re<separator>Im} from a given file in text format.
	 * The file requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled.
	 * 	sampleFreq: The frequency used to sample the function.
	 * After this, the N DZT coefficients expressed as {@code 0.0000<separator>0.0000}
	 * if separator is null the sampled should be expressed as 0.0000+0.0000i
	 * @param filePath The path to the file in which the data are stored.
	 * @param separator The character to separate the real part from the imaginary one.
	 * @return The status of the read operation.
	 */
	public Boolean readDZT(String filePath, String separator) {
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
	 * Reads the DZT coefficients of the transformed function as a+bi from a given file in text format.
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
	public Boolean readDZT(String filePath) {
		return readDZT(filePath, "");
	}

	/*
	 * **************** DISCRETE Z TRANSFORM METHODS ****************
	 *
	 * Reescrito (Decimoctava sesion, ver Claude/ComplexArithRev.md): la version original no
	 * compilaba ("z"/"duration" indefinidos), era una copia a medio adaptar de Laplace.DLT() antes
	 * de que este se arreglase, y la formula del nucleo no dependia de "n" (solo de "k"), asi que
	 * estructuralmente no calculaba una transformada Z de nada. Reescrito desde cero con
	 * z_k = r*e^(j*2*pi*k/N), r (radius) parametrizado explicitamente -- r=1 reduce exactamente a
	 * la DFT de las muestras (caso de referencia verificable contra Fourier.DFT()), igual que
	 * sigma=0 en Laplace.DLT().
	 */
	/**
	 * Calculates the DZT (Discrete Z Transform) at N points z_k = radius*e^(j*2*pi*k/N),
	 * equivalent to evaluating X(z_k) = SUM_n x[n]*z_k^(-n). radius=1 samples exactly on the unit
	 * circle, reducing exactly to the DFT of the samples (a verifiable reference case against
	 * Fourier.DFT()).
	 * @param sampleFreq The frequency used to sample the function.
	 * @param radius The radius r of the sampling ring. Must be strictly positive.
	 */
	public void DZT(int sampleFreq, double radius) {
		setRadius(radius);
		this.sampleFreq = sampleFreq;
		this.N = sampleFreq;
		Complex idospiN = Complex.i.times(-Complex.DOS_PI/N); // -2*pi*i/N

		System.out.println("Samples:" + this.N);
		System.out.printf("sample frequency: %.3e Hz\n",(double)sampleFreq);
		System.out.printf("radius: %.3e\n", radius);

		if (!isSampled) doTrfSampling();

		transform = new MatrixComplex(2,N);

		/*CHRONO*/ Chronometer chrono = new Chronometer();
		/*CHRONO*/ chrono.start();

		for (int k = 0; k < N; ++k) { // freq index
			Complex Ak = new Complex();
			for (int n = 0; n < N; ++n) { // time index
				// z_k^(-n) = radius^(-n) * e^(-j*2*pi*k*n/N)
				double rInvN = Math.pow(radius, -n);
				if (Double.isNaN(rInvN) || Double.isInfinite(rInvN)) {
					throw new ArithmeticException("DZT: radius^(-n) overflowed for radius=" + radius + ", n=" + n);
				}
				Complex expkn = Complex.exp(idospiN.times(k*n)).times(rInvN);
				Ak = Ak.plus(this.samples.getItem(1,n).times(expkn));
			}
			transform.setItem(0, k, this.samples.getItem(0,k));
			transform.setItem(1, k, Ak);
		}

		/*CHRONO*/ chrono.stop();
		/*CHRONO*/ System.out.println("Computing Time DZT:" + chrono.toString());

		isTransformed = true;
	}

	/**
	 * Calculates the DZT with radius=1 (samples exactly on the unit circle -- equivalent to the
	 * DFT of the samples).
	 * @param sampleFreq The frequency used to sample the function.
	 */
	public void DZT(int sampleFreq) {
		DZT(sampleFreq, 1.0);
	}

	/**
	 * Calculates the DZT using the signal definitions.
	 */
	public void DZT() {
		DZT(this.sampleFreq, this.radius);
	}

	/**
	 * Calculates the samples of the function using the Inverse DZT:
	 * x[n] = (1/N) * SUM_k X(z_k) * z_k^n, the exact inverse of DZT(sampleFreq, radius).
	 */
	public void IDZT() {
		if(!isTransformed) {
			System.out.println("WARNING:DZT coeficients not calculated/loaded. Do the DZT or Load them first.");
			return;
		}

		System.out.println("Computing the Inverse DZT...");
		Complex idospiN = Complex.i.times(Complex.DOS_PI/N); // +2*pi*i/N
		samples = new MatrixComplex(2,N);
		Complex point = loLimit.copy();
    	Complex incr = upLimit.minus(loLimit).divides(N);
		Complex Tk = new Complex();

		/*CHRONO*/ Chronometer chrono = new Chronometer();
		/*CHRONO*/ chrono.start();

		for (int n = 0; n < N; ++n) { // time index
			Tk.setComplexRec(0,0);
			for (int k = 0; k < N; ++k) { // freq index
				// z_k^n = radius^n * e^(j*2*pi*k*n/N)
				double rN = Math.pow(radius, n);
				if (Double.isNaN(rN) || Double.isInfinite(rN)) {
					throw new ArithmeticException("IDZT: radius^n overflowed for radius=" + radius + ", n=" + n);
				}
				Complex expkn = Complex.exp(idospiN.times(k*n)).times(rN);
				Tk = Tk.plus(expkn.times(this.transform.getItem(1, k)));
			}
			samples.setItem(0, n, point);
			samples.setItem(1, n, Tk.divides(N));
			point = point.plus(incr);
		}

		/*CHRONO*/ chrono.stop();
		/*CHRONO*/ System.out.println("Computing Time IDZT:" + chrono.toString());
		isSampled = true;
	}

	/**
	 * Evaluates the Z series X(z) = SUM_n x[n]*z^(-n) at any point z of the Z plane (not
	 * restricted to the DZT sampling ring), using the time samples x[n] as the Laurent series
	 * coefficients. Computed via Horner's method on w=1/z. Useful for pole/zero and stability
	 * analysis away from the discrete grid.
	 * @param z The point of the Z plane in which the series is evaluated. Must be non-zero.
	 * @return The value of the Z series at z.
	 */
	public Complex calc(Complex z) {
		if(!isSampled) {
			System.out.println("WARNING (calc):Function not sampled yet. Sample it first.");
			return new Complex();
		}
		if (z.isZero()) throw new IllegalArgumentException("calc: z must be non-zero (z^-1 is undefined at z=0)");

		Complex w = z.power(-1); // 1/z
		Complex result = samples.getItem(1, N-1).copy();
		for (int n = N-2; n >= 0; --n) {
			result = result.times(w).plus(samples.getItem(1, n));
		}
		return result;
	}

	/**
	 * Prints in the console the DZT coefficients.
	 */
	public void printTCoefs() {
		if(!isTransformed) {
			System.out.println("WARNING:Function not transformed yet. Transform it first.");
			return;
		}
		//series.transpose().println("Z Series Coefs ");
		System.out.println("Z Transform Coefs ");
		int idxDig = (N+"").length();
		String strFormat = "A%0"+idxDig+"d = %s\n";
		for (int i = 0; i < transform.cols(); ++i) {
			System.out.printf(strFormat, i, transform.getItem(0, i).toString());
		}
	}

	/**
	 * Enum for plotting the DZT with different operators.
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
	 * **************** DZT PLOTTING METHODS ****************
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
	 * MatrixComplexPlot.e_lineStyle, SimpleGnuplot.e_syncMode)} (8 agosto 2026) -- this method used to carry its own,
	 * byte-for-byte identical copy of that logic (also duplicated in {@code Fourier}/{@code
	 * Laplace}). This class's own {@code e_lineStyle} keeps its exact public signature; only the
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
	 * Plots the samples of the function used for the Z analysis
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
	 * Plots the DZT graphic in the domain of the coefficients.
	 * @param Title The title of the plot.
	 * @param showIm True to plot the imaginary part.
	 * @param lineStyle The line style used for the plot.
	 * @apiNote The Javadoc previously listed {@code domain}/{@code operator}/{@code logscale} as
	 * parameters, but the method's actual signature is {@code (String, boolean, e_lineStyle)} --
	 * corrected to match, no behavior change.
	 */
	public void plotDZTSync(String Title, boolean showIm, e_lineStyle lineStyle) {
		plotDZT(Title, showIm, lineStyle, SimpleGnuplot.e_syncMode.SYNC);
	}

	public void plotDZTAsync(String Title, boolean showIm, e_lineStyle lineStyle) {
		plotDZT(Title, showIm, lineStyle, SimpleGnuplot.e_syncMode.ASYNC);
	}

	private void plotDZT(String Title, boolean showIm, e_lineStyle lineStyle, SimpleGnuplot.e_syncMode mode) {
		if(!isTransformed) {
			System.out.println("WARNING:DZT coeficients not calculated/loaded. Do the DZT or Load them first.");
			return;
		}
		plot(Title, N, transform, showIm, lineStyle, mode);
	}

}
