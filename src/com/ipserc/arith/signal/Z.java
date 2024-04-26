package com.ipserc.arith.signal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.panayotis.gnuplot.JavaPlot;

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
	 * Creates an instance of the Z object, using the sampled values ​​of the function.
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
	 * Creates an instance of the Z object, using the sampled values ​​of the function.
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
	 * Saves the coefficients of the DZT as Re<separator>Im in a given file in text format.
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
	 * Reads the DZT coefficients of the transformed function as Re<separator>Im from a given file in text format.
	 * The file requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled.
	 * 	sampleFreq: The frequency used to sample the function.
	 * After this, the N DZT coefficients expressed as 0.0000<separator>0.0000
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
	 */
	/**
	 * Calculates the DZT.
	 * @param sampleFreq The frequency used to sample the function.
	 */
	public void DZT(int sampleFreq) {
		this.sampleFreq = sampleFreq;
		this.N = sampleFreq;
		// Complex ONEidospiN = new Complex(-1/N, -Complex.DOS_PI/N); // -1/N-2*pi*i/N
		
		System.out.println("Samples:" + this.N);
		System.out.printf("sample frequency: %.3e Hz\n",(double)sampleFreq);
		
		if (!isSampled) doTrfSampling();
		
		transform = new MatrixComplex(2,N);

		/*DURATION*/ long time = System.currentTimeMillis();
		
		for (int k = 0; k < N; ++k) { // freq index
			Complex Ak = new Complex();
			for (int n = 0; n < N; ++n) { // time index
				Ak = Ak.plus(this.samples.getItem(1,n).times(z.power(-k)));
			}
			transform.setItem(0, k, this.samples.getItem(0,k));
			transform.setItem(1, k, Ak);
			//transform.setItem(1, k, Ak.divides(N));
		}
		
		/*DURATION*/ time = System.currentTimeMillis() - time;
		/*DURATION*/ duration FTDuration = new duration(time, "ms");
		/*DURATION*/ System.out.println("Computing Time DLT:" + FTDuration.toString());
		
		isTransformed = true;
	}

	/**
	 * Calculates the samples of the function using the Inverse DLT.
	 */
	public void IDLT() {
		if(!isTransformed) {
			System.out.println("WARNING:DLT coeficients not calculated/loaded. Do the DLT or Load them first.");
			return;
		}
		
		System.out.println("Computing the Inverse DLT...");
		double dospiN = Complex.DOS_PI/N; // +2*pi/N
		Complex expkn = new Complex();
		samples = new MatrixComplex(2,N);
		Complex point = loLimit.copy();
    	Complex incr = upLimit.minus(loLimit).divides(N);
		Complex Tk = new Complex();

    	/*DURATION*/ long time = System.currentTimeMillis();

		for (int k = 0; k < N; ++k) { // time index
			Tk.setComplexRec(0,0);
			for (int n = 0; n < N; ++n) { // freq index
				expkn = Complex.exp(dospiN*k*n);
				Tk = Tk.plus(expkn.times(this.transform.getItem(0, n)));
			}
			samples.setItem(0, k, point);
			samples.setItem(1, k, Tk.divides(N));
			point = point.plus(incr);
		}
		/*DURATION*/ time = System.currentTimeMillis() - time;
		/*DURATION*/ duration IDLTDuration = new duration(time, "ms");
		/*DURATION*/ System.out.println("Computing Time IDLT:" + IDLTDuration.toString());
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
	 * Enumerative for plotting the DLT in different operators 
	 * COMPLEX. Plots the values of the coefficients in Rectangular representation.
	 * MAGNITUDE. Plots the values of the coefficients in Polar representation.
	 * SQUARE. Plots the square values of the coefficients in Rectangular representation.
	 */
	public static enum e_operator {
		COMPLEX, MAGNITUDE, SQUARE;
	}
	
	/**
	 * Enumerative for the two kinds of x axis units
	 * SAMP. x axis represents the index of the coefficients
	 * FREC. x axix represents the frequency associated to the coefficient
	 */
	public static  enum e_domain {
		SAMP, FREC;
	}
	
	/**
	 * Does the operations required for the different views
	 * @param cNum The Complex number to operate with.
	 * @param operator The operator as defined in e_operator
	 * @param logscale True if the cNum should be non-negative to be plotted in logarithmic scale 
	 * @return  the number operated
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
	 * Returns the "style" set required as "data " LINES or IMPULSES for gnuplot
	 * @param lineStyle
	 * @return The "style" set
	 */
	private String setLineStyle(e_lineStyle lineStyle) {
		String strLineStyle = "data ";
		
		switch (lineStyle) {
		case LINES: return strLineStyle+"lines";
		case IMPULSES: return strLineStyle+"impulses";
		}
		return strLineStyle+"lines";
	}
	
	/**
	 * Plots a graphic with the points given in 'data'. Row 0 is for the x axis values, Row 1 is for the y axis values. The values to plot are in the columns.
	 * @param title The title of the graphic.
	 * @param data The points to be plotted.
	 * @param showIm If true plots the imaginary part in the graphic.
	 */
	private void plot(String title, int nbrSamples, MatrixComplex data, boolean showIm, e_lineStyle lineStyle) {
		//Split the data into Re and Im parts
		double dataRe[][]  = new double[nbrSamples][2];
		double dataIm[][]  = new double[nbrSamples][2];

		for (int t = 0; t < nbrSamples ; ++t) {
			dataRe[t][0] = data.complexMatrix[0][t].rep();
			dataIm[t][0] = data.complexMatrix[0][t].rep();
			dataRe[t][1] = data.complexMatrix[1][t].rep();
			dataIm[t][1] = data.complexMatrix[1][t].imp();
		}
		
		//Plot the data
		JavaPlot p = new JavaPlot();
		p.setTitle(title);
		p.addPlot(dataRe);
		if (showIm) p.addPlot(dataIm);
		p.set("zeroaxis", "");
		//p.set("style","data lines");
		p.set("style", setLineStyle(lineStyle));
		p.set("grid","");
		p.plot();
	}

	public void plotFunction(String title, int nbrSamples, boolean showIm, e_lineStyle lineStyle) {
		if(!isSampled || this.N != nbrSamples) {
			this.N = nbrSamples;
			this.sampleFreq = nbrSamples;
			doSrsSampling();
		}

		plot(title, nbrSamples, samples, showIm, lineStyle);
	}
	
	/**
	 * Plots the samples of the function used for the Laplace analysis
	 * @param title The title of the graphic.
	 * @param nbrSamples The number of the samples to draw the plot.
	 * @param showIm True for plotting the imaginary part.
	 */
	
	/* ***************************************************************************************************

	public void plotSamples(String title, int nbrSamples, boolean showIm, e_lineStyle lineStyle) {
		if(!isSampled || this.N != nbrSamples) {
			this.N = nbrSamples;
			this.sampleFreq = nbrSamples;
			doSrsSampling();
		}
		
		//Split the data into Re and Im parts
		double dataRe[][]  = new double[nbrSamples][2];
		double dataIm[][]  = new double[nbrSamples][2];

		for (int t = 0; t < nbrSamples ; ++t) {
			dataRe[t][0] = samples.complexMatrix[0][t].rep();
			dataIm[t][0] = samples.complexMatrix[0][t].rep();
			dataRe[t][1] = samples.complexMatrix[1][t].rep();
			dataIm[t][1] = samples.complexMatrix[1][t].imp();
		}
		
		//Plot the data
		JavaPlot p = new JavaPlot();
		p.setTitle(title);
		p.addPlot(dataRe);
		if (showIm) p.addPlot(dataIm);
		p.set("zeroaxis", "");
		//p.set("style","data lines");
		p.set("style", setLineStyle(lineStyle));
		p.set("grid","");
		p.plot();
	}
	 *************************************************************************************************** */

	/**
	 * Plots the samples of the function used for the Fourier analysis
	 * @param title The title of the graphic.
	 * @param showIm True for plotting the imaginary part.
	 */
	public void plotSamples(String title, boolean showIm, e_lineStyle lineStyle) {
		plot(title, N, samples, showIm, lineStyle);
	}
	
	/**
	 * Does the plot of the DLT graphic in the domain of Coefficients
	 * @param Title The title of the polt
	 * @param domain The domain in which plot the DLT
	 * @param operator The operator used
	 * @param logscale True in y axis should be set in logarithmic scale
	 */
	public void plotDLT(String Title, boolean showIm, e_lineStyle lineStyle) {
		if(!isTransformed) {
			System.out.println("WARNING:DFT coeficients not calculated/loaded. Do the DFT or Load them first.");
			return;
		}
		plot(Title, N, transform, showIm, lineStyle);
	}
	
}
