package com.ipserc.arith.signal;

import java.util.function.Function;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.panayotis.gnuplot.JavaPlot;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.File;  // Import the File class
import com.ipserc.chronometer.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.nio.file.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Fourier extends MatrixComplex {
	private Function<Complex, Complex> func;
	private Complex loLimit; 
	private Complex upLimit;
	private Complex period;
	private int N; 					// Nbr of samples
	private int sampleFreq; 		// Sampling Frequency
	private MatrixComplex samples;
	private MatrixComplex series;
	private MatrixComplex transform;
	private Complex offset = new Complex();
	private Boolean isSampled = false;
	private Boolean isSerialized = false;
	private Boolean isTransformed = false;
	private String filterData;
	
	private final static String HEADINFO = "Fourier --- INFO: ";
	private final static String VERSION = "1.8 (2026_0808_0300)";
	/* VERSION Release Note
	 *
	 * 1.8 (2026_0808_0300)
	 * plot(String,int,MatrixComplex,boolean,e_lineStyle): la implementacion (25 lineas, copiada
	 * byte a byte tambien en Laplace/Z) delega ahora en MatrixComplexPlot.plot() -- ver
	 * MatrixComplex.VERSION 1.59. Firma publica y enum e_lineStyle propios intactos.
	 *
	 * 1.7 (2026_0807_2330)
	 * DFT(int): el fallback O(n^2) (N no potencia de 2) calculaba solo la primera mitad de las
	 * frecuencias y rellenaba el resto via X[N-k]=conj(X[k]) -- simetria valida solo para señal de
	 * entrada real, incorrecta en silencio para una señal genuinamente compleja. Arreglado
	 * calculando las N frecuencias de forma directa, sin el atajo de simetria. Verificado con
	 * ScratchFourierComplexDFT01.java (señal compleja e^{it}, N=6) contra una DFT de fuerza bruta
	 * independiente: coincide a precision de maquina. Sin regresion para señal real
	 * (ScratchFourierRealDFT01.java, N=7, tambien a precision de maquina).
	 *
	 * 1.6 (2026_0807_1600)
	 * All 6 plot()-calling methods: a peticion del usuario, antes de cada "p.plot()" se anade
	 * "p.setPersist(true)" + "p.getPostInit().add(\"set terminal windows\")" -- evita que la ventana
	 * de gnuplot se quede congelada al hacer zoom. Ver Polynom.VERSION 1.14 para el detalle completo
	 * y el resto de ficheros con el mismo parche.
	 *
	 * 1.5 (2026_0807_2100)
	 * convolution(): tenia un ".times(2)" incondicional sobre cada muestra de salida, con un
	 * comentario del propio autor original admitiendo no saber por que estaba ahi ("times(2) don't
	 * know why"). Confirmado como bug real, no una convencion de normalizacion legitima: convolucionar
	 * cualquier señal con el filtro identidad (delta de Kronecker, h[0]=1, resto 0) debe devolver la
	 * señal EXACTAMENTE igual (la suma de convolucion colapsa a un unico termino, x[t]*h[0]=x[t], sin
	 * margen de ambiguedad matematica) -- el codigo devolvia el doble exacto en todos los puntos.
	 * Arreglado quitando el ".times(2)".
	 * Verificado (ScratchFourierConvolutionAudit01.java, conservado): con el filtro identidad, la
	 * señal de salida coincide exactamente con la de entrada (ratio 1.0 en todos los puntos, antes
	 * 2.0).
	 *
	 * 1.4 (2026_0807_2000)
	 * bandPassFilter(gain,fIni,bandwidth,slope,samplefreq) (5 argumentos, "con pendientes"): mismo
	 * patron de off-by-one que slopeFilter() (ver 1.3 abajo), implementado de forma distinta --
	 * "transform.setItem(0, N-i-1, fVal)" dentro del propio bucle de i=0..N2-1. Ese "N-i-1" hacia
	 * que el ultimo tramo (i=N2-1) cayera exactamente en el bin de Nyquist (N2), pero como efecto
	 * colateral desplazaba un bin TODO lo demas -- p.ej. el valor de DC (i=0) acababa espejado en el
	 * bin N-1 (frecuencia -1, no "DC negativo", que no existe). Confirmado en ejecucion: 15% de
	 * contaminacion imaginaria en la señal reconstruida (max|Im|=0.047 vs max|Re|=0.3125), mas alta
	 * que la de slopeFilter() antes de su fix. Arreglado con el mismo criterio: DC (i=0) no se
	 * espeja (no tiene bin de frecuencia negativa valido); para i=1..N2-1 el espejo va a N-i (no
	 * N-i-1); el bin de Nyquist se rellena aparte, fuera del bucle, con el ultimo valor calculado
	 * (transform[N2-1]) -- mismo convenio de frontera que slopeFilter().
	 *
	 * 1.3 (2026_0807_1900)
	 * slopeFilter() (y por herencia lowPassFilter()/highPassFilter(), que solo la delegan): el
	 * bucle que reflejaba la mitad de frecuencias negativas tenia un off-by-one --
	 * "for (i=N2,j=N2;i<N;++i) transform[i]=transform[--j]" acababa asignando
	 * transform[N2+k]=transform[N2-1-k]. Para una senal real, la simetria conjugada correcta es
	 * transform[N2+k]=transform[N2-k] (bin N2+k = frecuencia -(N2-k), que para un filtro real debe
	 * igualar el valor en +(N2-k) = bin N2-k) -- el codigo usaba N2-1-k, un bin de menos.
	 * Confirmado en ejecucion: la señal reconstruida via IDFT() tenia una parte imaginaria del
	 * ~5% de la real (deberia ser ~0 para un espectro genuinamente simetrico). El bin de Nyquist
	 * (N2) no tiene pareja especular real -- se deja igual que antes (hereda el ultimo valor de la
	 * rampa, transform[N2-1]), solo se corrige el resto del espejo (k=1..N2-1).
	 *
	 * 1.2 (2026_0807_1500)
	 * Anadida una FFT real (radix-2 Cooley-Tukey, iterativa con permutacion bit-reversal), a
	 * peticion del usuario -- DFTL()/DFT() eran O(n^2) (DFT() con una simetria parcial para senales
	 * reales, pero sigue siendo O(n^2) en el bucle interior; DFTL() sin ninguna optimizacion,
	 * conservada como referencia honesta para verificar). DFT(int)/IDFT() ahora usan la FFT/IFFT
	 * automaticamente cuando N es potencia de dos exacta (O(n log n)); para cualquier otro N caen
	 * al mismo camino O(n^2) de siempre, sin cambios. La FFT no necesita la simetria de senal real
	 * (calcula X[k]=SUM x[n]*e^(-2*pi*i*k*n/N) para cualquier entrada compleja), asi que es
	 * ademas mas correcta que el camino O(n^2) de DFT() para senales genuinamente complejas --
	 * pero solo cuando N es potencia de dos; para N que no lo es, DFT() sigue con la misma
	 * asuncion de senal real de siempre (preexistente, fuera de alcance de este cambio).
	 * Verificado (ver Claude/ComplexArithRev.md): DFT(N) con N potencia de dos coincide con
	 * DFTL(N) (referencia O(n^2) sin simetria) a precision de maquina, para señales reales y
	 * complejas, N=4..1024; IDFT() tras DFT() reconstruye la señal original igual de bien.
	 *
	 * 1.1 (2022_1103_2002)
	 * private Boolean saveRAWFile(String filePath, MatrixComplex dataOrig)
	 * public Boolean saveRAWSamples(String filePath)
	 * public MatrixComplex normalize(MatrixComplex values) 
	 * 
	 * 1.0 (2020_0824_1800)
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
	 * CONSTRUCTORS 
	 * ***********************************************
	 */
	
	/**
	 * Instantiates an empty Fourier object, ready to load Fourier Series Coefficients, or Discrete Fourier Transform items as function samples or DFT coefficients.
	 */
	public Fourier() {
		loLimit = new Complex (); 
		upLimit = new Complex ();
		period  = new Complex ();
	}
	
	/**
	 * Instantiates an empty Fourier object with the minimal components, ready to load Fourier Series Coefficients, or Discrete Fourier Transform items as function samples or DFT coefficients.
	 * @param nbrSamples The number of samples taken in the interval 
	 * @param DloLimit Lower limit of the points to use with the function.
	 * @param DupLimit Upper limit of the points to use with the function.
	 */
	public Fourier(int nbrSamples, double DloLimit, double DupLimit) {
		this.loLimit = new Complex(DloLimit,0);
		this.upLimit = new Complex(DupLimit,0);
		this.period = upLimit.minus(loLimit);
		this.sampleFreq = nbrSamples;
		this.N = nbrSamples;
	}

	/**
	 * Instantiates an empty Fourier object with the minimal components, ready to load Fourier Series Coefficients, or Discrete Fourier Transform items as function samples or DFT coefficients.
	 * @param nbrSamples The number of samples taken in the interval 
	 * @param CloLimit Lower limit of the points to use with the function, as a Complex number. Only the real part will be used.
	 * @param CupLimit Upper limit of the points to use with the function, as a Complex number. Only the real part will be used.
	 */
	public Fourier(int nbrSamples, Complex CloLimit, Complex CupLimit) {
		this.loLimit = CloLimit;
		this.upLimit = CupLimit;
		this.period = upLimit.minus(loLimit);
		this.sampleFreq = nbrSamples;
		this.N = nbrSamples;
	}

	/**
	 * Instantiates the Fourier object, using a function definition and its domain.
	 * @param func The function to work with. Must return data in the Complex domain.
	 * @param DloLimit Lower limit of the points to use with the function.
	 * @param DupLimit Upper limit of the points to use with the function.
	 */
	public Fourier(Function<Complex, Complex> func, double DloLimit, double DupLimit) {
		this.func = func;
		this.loLimit = new Complex(DloLimit,0);
		this.upLimit = new Complex(DupLimit,0);
		this.period = upLimit.minus(loLimit);
	}

	/**
	 * Instantiates the Fourier object, using a function definition and its domain.
	 * @param func The function to work with. Must return data in the Complex domain.
	 * @param loLimit Lower limit of the points to use with the function as a Complex number. Only the real part will be used.
	 * @param upLimit Upper limit of the points to use with the function as a Complex number. Only the real part will be used.
	 */
	public Fourier(Function<Complex, Complex> func, Complex loLimit, Complex upLimit) {
		this.func = func;
		this.loLimit = loLimit;
		this.upLimit = upLimit;
		this.period = upLimit.minus(loLimit);
		//this.isSerialized = false;
		//this.isTransformed = false;
	}
	
	/**
	 * Creates an instance of the Fourier object, using the sampled values ​​of the function.
	 * It requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled
	 * 	sampleFreq: The frequency used to sample the function
	 *  filterData: The data facts if the samples describe a filter
	 * After this, the N samples expressed as 0.0000+0.0000i
	 * @param pathSamples The path to the file with the sampled values.
	 * @param sampleFreq The sampling frequency ONLY for raw files
	 */
	public Fourier(String pathSamples) {
		boolean readResult = false; 
		File file = new File(pathSamples);
		String fileName = file.getName();
		int lastDotPos = fileName.lastIndexOf('.'); 
		String fileExt = lastDotPos > 0 ? fileName.substring(lastDotPos + 1) : "";
		switch (fileExt.toLowerCase()) {
			case "txt": readResult = readSamplesTXT(pathSamples); break;
			case "raw": readResult = readSamplesRAW(pathSamples); break;
		}
		if (readResult) isSampled = true;
	}

	/**
	 * Creates an instance of the Fourier object, using the sampled values ​​of the function.
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
	public Fourier(String pathSamples, String separator) {
		readSamples(pathSamples, separator);
	}
	
	/*
	 * ***********************************************
	 * GETTERS / SETTERS 
	 * ***********************************************
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
	 * Gets the value at index idx of the DFT transform.
	 * @param idx The coefficient index.
	 * @return The DFT coefficient at idx.
	 */
	public Complex getTransformItem(int idx) {
		return this.transform.getItem(0, idx);
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
	 * Gets the data facts of a filter.
	 * @return The data facts of a filter.
	 */
	public String getFilterData() {
		return filterData;
	}
	
	/*
	 * ***********************************************
	 * INTEGRATION METHODS FOR FOURIER SERIES 
	 * ***********************************************
	 */
	
	/**
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the real axe
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param expnz the function EXP((-2*pi*n*i/T)*z)
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
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the real axe
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param expnz the function EXP((-2*pi*n*i/T)*z)
	 * @param precision The precision of the result 
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
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the imaginary axe
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param expnz the function EXP((-2*pi*n*i/T)*z)
	 * @param precision The precision of the result 
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
	 * ***********************************************
	 * MATHEMATICAL METHODS 
	 * ***********************************************
	 */
	
	/**
	 * Indicates if the function is continuous in the given point.
	 * @param p The point in which the continuity is analyzed. Only the real part is evaluated.
	 * @return True if the function in continuous. False in other case.
	 */
	private Boolean isContinuous(Complex p) {
		Complex fp, fp1;
		fp = func.apply(p);
		fp1 = func.apply(p.plus(Complex.precision()*2));
		//	fp.println("-----> fp :");
		//	fp1.println("-----> fp1:");
		if (fp1.equals(fp)) return true;
		return false;
	}
	
	/*
	 * ***********************************************
	 * FOURIER SERIES METHODS 
	 * ***********************************************
	 */
	
	/**
	 * Calculates the Fourier Series up to the coefficient number given by 'order' and for an specific precision
	 * @param order Maximum order of the coefficient calculated.
	 * @param decPrec Precision of the coefficients
	 */
	public void serialize(int order, int decPrec) {
		this.series = new MatrixComplex(1, order+1);
		Complex coef ;

		/*CHRONO*/ Chronometer FTChrono = new Chronometer();

		for (int n = 0; n <= order ; ++n) {
			Complex expn = Complex.i.times(-Complex.DOS_PI * n).divides(period);	//-2*pi*n*i/T
			Function<Complex, Complex> expz = z -> Complex.exp(expn.times(z)); 		//EXP((-2*pi*n*i/T)*z)
			coef = integrate(period.opposite().divides(2), period.divides(2), func, expz, decPrec).times(2).divides(period); //.conjugate();
			series.setItem(0, n, coef);
		}
		
		/*CHRONO*/ FTChrono.stop();
		/*CHRONO*/ System.out.println("Computing Time FS:" + FTChrono.toString());
		
		isSerialized = true;
		setOffset();
	}

	/**
	 * For Fourier Series, calculates the offset required to match with the function. Is the constant evaluated for the integration operation.
	 */
	private void setOffset() {
		Complex midp = (upLimit.plus(loLimit)).divides(2);
		if (!isContinuous(midp)) midp = (upLimit.plus(loLimit.divides(10))).divides(2);
		//	midp.println("-----> midp = ");
		Complex off1 = this.calc(midp);
		//	off1.println("-----> sFourier.calc = ");
		Complex off2 = func.apply(midp);
		//	off2.println("-----> sFourier.func = ");
		System.out.println("offset = " + (off1.minus(off2)).toString());
		offset = off1.minus(off2);
	}
	
	/**
	 * Prints in the console the Fourier Series coefficients calculated as a vector.
	 */
	public void printCoefs() {
		if(!isSerialized) {
			System.out.println("WARNING (printCoefs):Function not serialized yet. Serialize it first.");
			return;
		}
		//series.transpose().println("Fourier Series Coefs ");
		System.out.println("Fourier Series Coefs ");
		series.println();
	}
	
	/**
	 * Prints in the console the Fourier Series coefficients calculated.
	 */
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
	
	/**
	 * Returns the value of the Fourier Series at a given point.
	 * @param p The point in which the Series is evaluated. Only the real part has sense.
	 * @return The value returned from the Fourier Series.
	 */
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
	
	/*
	 * ***********************************************
	 * FOURIER SERIES PLOTTING METHODS 
	 * ***********************************************
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
	 * <p>
	 * Delegates to {@link MatrixComplexPlot#plot(String, int, MatrixComplex, boolean,
	 * MatrixComplexPlot.e_lineStyle)} (8 agosto 2026) -- this method used to carry its own,
	 * byte-for-byte identical copy of that logic (also duplicated in {@code Laplace}/{@code Z}).
	 * This class's own {@code e_lineStyle} keeps its exact public signature; only the
	 * implementation moved, converted to {@code MatrixComplexPlot.e_lineStyle} at the boundary.
	 */
	public void plot(String title, int nbrSamples, MatrixComplex data, boolean showIm, e_lineStyle lineStyle) {
		MatrixComplexPlot.plot(title, nbrSamples, data, showIm,
			lineStyle == e_lineStyle.IMPULSES ? MatrixComplexPlot.e_lineStyle.IMPULSES : MatrixComplexPlot.e_lineStyle.LINES);
	}

	/**
	 * Plots the function used for the Fourier analysis
	 * @param title The title of the graphic.
	 * @param nbrSamples The number of the samples to draw the plot.
	 * @param showIm True for plotting the imaginary part.
	 */
	public void plotFunction(String title, int nbrSamples, boolean showIm, e_lineStyle lineStyle) {
		plotSamples(title, nbrSamples, showIm, lineStyle);
	}
	
	/**
	 * Plots the samples of the function used for the Fourier analysis
	 * @param title The title of the graphic.
	 * @param nbrSamples The number of the samples to draw the plot.
	 * @param showIm True for plotting the imaginary part.
	 */
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
		
		System.out.println("dataRe[nbrSamples-1][0]:"+dataRe[nbrSamples-1][0]);
				
		//Plot the data
		JavaPlot p = new JavaPlot();
		p.setTitle(title);
		p.addPlot(dataRe);
		if (showIm) p.addPlot(dataIm);
		p.set("zeroaxis", "");
		//p.set("style","data lines");
		p.set("style", setLineStyle(lineStyle));
		p.set("grid","");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		p.plot();
	}

	/**
	 * Plots the approximation to the function from the Fourier Series
	 * @param title The title of the graphic.
	 * @param nbrSamples The number of the samples to draw the plot.
	 * @param showIm True for plotting the imaginary part.
	 */
	public void plotSeries(String title, int nbrSamples, boolean showIm, e_lineStyle lineStyle) {
		if(!isSampled || this.N != nbrSamples) {
			this.N = nbrSamples;
			this.sampleFreq = nbrSamples;
			doSrsSampling();
		}
		
		//Split the data into Re and Im parts
		double dataRe[][]  = new double[nbrSamples][2];
		double dataIm[][]  = new double[nbrSamples][2];
		Complex cVal = new Complex();

		for (int t = 0; t < nbrSamples ; ++t) {
			cVal = calc(samples.complexMatrix[0][t]);
			dataRe[t][0] = samples.complexMatrix[0][t].rep();
			dataIm[t][0] = samples.complexMatrix[0][t].rep();
			dataRe[t][1] = cVal.rep();
			dataIm[t][1] = cVal.imp();
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
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		p.plot();
	}

	/**
	 * Plots a comparison graphic with the sampled points of the function and the values calculated from the Fourier Series.
	 * @param nbrSamples The number of the samples to draw the plot.
	 */
	public void plotCompare(int nbrSamples, e_lineStyle lineStyle) {
		if(!isSerialized) {
			System.out.println("WARNING (plotCompare):Function not serialized yet. Serialize it first.");
			return;
		}

		if(!isSampled || this.N != nbrSamples) {
			this.N = nbrSamples;
			this.sampleFreq = nbrSamples;
			doSrsSampling();
		}

		//Split the data into Re and Im parts
		double dataFF[][]  = new double[nbrSamples][2];
		double datafu[][]  = new double[nbrSamples][2];

		for (int t = 0; t < nbrSamples; ++t) {
			dataFF[t][0] = samples.complexMatrix[0][t].rep();
			datafu[t][0] = samples.complexMatrix[0][t].rep();
			dataFF[t][1] = calc(samples.complexMatrix[0][t]).rep();
			datafu[t][1] = samples.complexMatrix[1][t].rep();
		}
		
		//Plot the data
		JavaPlot p = new JavaPlot();
		p.setTitle("Fourier Series vs Function");
		p.addPlot(dataFF);
		p.addPlot(datafu);
		p.set("zeroaxis", "");
		//p.set("style","data lines");
		p.set("style", setLineStyle(lineStyle));
		p.set("grid","");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		p.plot();
	}

	/*
	 * ***********************************************
	 * FUNCTION SAMPLER METHODS 
	 * ***********************************************
	 */

	/**
	 * Does the sampling of the function for the Fourier Series Analysis. The samples are stored in the Fourier Object
	 */
	public void doSrsSampling() {
		//this.N = sampleFreq;
		Complex incr = upLimit.minus(loLimit).divides(N);
		// System.out.printf("Sample step: %e s\n", incr.rep());
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
	 * Does the sampling of the function for the Discrete Fourier Transform. The samples are stored in the Fourier Object
	 */
	public void doTrfSampling() {
		doSrsSampling();
	}
	
	/*
	 * ***********************************************
	 * SAVE AND READ METHODS TO STORE CALCULATIONS 
	 * ***********************************************
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
	 * Saves the signal samples as a raw file with Codification:64 bit, Byte order:big-endian, Channels:1 (mono)for the sample frecuency given
	 * This file can be loaded by a sound editor progrma as Audacity
	 * @param filePathfile
	 * @param data
	 * @return
	 */
	private Boolean saveRAWFile(String filePath, MatrixComplex dataOrig, boolean normalize) {	
		Complex.storeFormatStatus();
		Complex.resetFormatStatus();
		Boolean fsaved = false;
		System.out.println("writing RAW data into " + filePath);
		File file;

	    try {
	        file = new File(filePath);
	    } catch (Exception e) {
	        System.out.println("Error creating file:" + e.getCause());
	        e.printStackTrace();
	        return false;
	    }
	    
	    MatrixComplex data = normalize ? this.normalize(dataOrig) : dataOrig.copy();
	    
        try (FileOutputStream fos = new FileOutputStream(file); DataOutputStream dos = new DataOutputStream(fos);)
        {
    		for (int i = 0; i < this.N; ++i) {
    			dos.writeDouble(data.getItem(0,i).rep());
    			dos.flush();
    		}
	        dos.close();
	        System.out.println("Successfully wrote to the file.");
	        fsaved = true;
        }
        catch (IOException e) {
        	System.out.println("Error writing file:" + e.getCause());
	        e.printStackTrace();
        }
		Complex.restoreFormatStatus();
	    return fsaved;
	}
	
	private MatrixComplex readRAWFile(String filePath, boolean normalize) {	
		Complex.storeFormatStatus();
		Complex.resetFormatStatus();
		System.out.println("reading RAW data from " + filePath);
		byte[] allBytes;
		MatrixComplex  dataMatrixComplex = new MatrixComplex();

		try {
			allBytes = Files.readAllBytes(Paths.get(filePath));
		} 
		catch (IOException ex) {
			ex.printStackTrace();
			return dataMatrixComplex;
		}

		ByteBuffer allBytesBuffer = ByteBuffer.wrap(allBytes);
		N = allBytes.length / Double.BYTES;
		sampleFreq = N;
		loLimit = new Complex(0, 0);
		upLimit = new Complex((N+1.0)/sampleFreq, 0);
		Complex incr = upLimit.minus(loLimit).divides(N);
		Complex point = new Complex(0,0);
		this.sampleFreq = N;
		dataMatrixComplex = new MatrixComplex(2, N);
		for(int n = 0; n < N; ++n) {
			dataMatrixComplex.setItem(0, n, point);
			dataMatrixComplex.setItem(1, n, allBytesBuffer.getDouble());
			//dataMatrixComplex.setItem(1, n, allBytesBuffer.order(ByteOrder.LITTLE_ENDIAN ).getDouble(n * Double.BYTES));
			point = point.plus(incr);
		}
		
		if (normalize) dataMatrixComplex = this.normalize(dataMatrixComplex);
	    
	    return dataMatrixComplex;
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
	 * 
	 * @param filePath
	 * @return
	 */
	public Boolean saveRAWSamples(String filePath, boolean normalize) {
		if(!isSampled) {
			System.out.println("WARNING:Function not sampled yet. Sample it first.");
			return false;
		}
		MatrixComplex newSamples = new MatrixComplex(1,N);
		newSamples.copyRow(0, samples, 1);
		return saveRAWFile(filePath, newSamples, normalize);
	}

	/**
	 * Saves the coefficients of the DFT as Re<separator>Im in a given file in text format.
	 * @param filePath The path to the file in which the data are saved.
	 * @param separator the character to separate the real part from the imaginary one.
	 * @return The status of the save operation.
	 */
	public Boolean saveDFT(String filePath, String separator) {
		if(!isTransformed) {
			System.out.println("WARNING:Function not transformed yet. Transform it first.");
			return false;
		}
		return this.saveTFile(filePath, transform, separator);
	}
	
	/**
	 * Saves the coefficients of the DFT as a+bi in a given file in text format.
	 * @param filePath The path to the file in which the data are saved.
	 * @return The status of the save operation.
	 */
	public Boolean saveDFT(String filePath) {
		return saveDFT(filePath, "");
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
		    if (n != N) System.out.println("WARNING: The number of samples doesn't match the number of data in the file.");
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
	public Boolean readSamplesTXT(String filePath) {
		return 	readSamples(filePath, "");
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public Boolean readSamplesRAW(String filePath) {
		samples = readRAWFile(filePath, false);
		if (samples.rows() == 0) return false;
		return true;
	}
	
	/**
	 * Reads the DFT coefficients of the transformed function as Re<separator>Im from a given file in text format.
	 * The file requires the following information before to start with the values sampled:
	 * 	loLimit: The lower limit of the points to use with the function.
	 * 	upLimit: The upper limit of the points to use with the function.
	 * 	period: The period of the function, usually the distance between upper and lower limit.
	 * 	N: The number of values sampled.
	 * 	sampleFreq: The frequency used to sample the function.
	 * After this, the N DFT coefficients expressed as 0.0000<separator>0.0000
	 * if separator is null the sampled should be expressed as 0.0000+0.0000i
	 * @param filePath The path to the file in which the data are stored.
	 * @param separator The character to separate the real part from the imaginary one.
	 * @return The status of the read operation.
	 */
	public Boolean readDFT(String filePath, String separator) {
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
	 * Reads the DFT coefficients of the transformed function as a+bi from a given file in text format.
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
	public Boolean readDFT(String filePath) {
		return readDFT(filePath, "");
	}

	/*
	 * ***********************************************
	 * DISCRETE FOURIER TRANSFORM METHODS 
	 * ***********************************************
	 */

	/**
	 * Calculates the DFT (Long) no making use of the symmetry properties.
	 * It takes at least the double of time than the shorter DFT.
	 * Kept for testing purposes.
	 * @param sampleFreq The frequency used to sample the function.
	 */
	public void DFTL(int sampleFreq) {
		this.N = sampleFreq;
		this.sampleFreq = sampleFreq;

		this.sampleFreq = sampleFreq;
		this.N = sampleFreq;
		Complex idospiN = Complex.i.times(-Complex.DOS_PI/N); // -2*pi*i/N
		Complex expkn = new Complex();
		
		System.out.println("Samples:" + this.N);
		System.out.printf("sample frequency: %.3e Hz\n",(double)sampleFreq);
		
		if (!isSampled) doTrfSampling();
		
		transform = new MatrixComplex(1,N);

		/*CHRONO*/ Chronometer FTChrono = new Chronometer();
		
		for (int k = 0; k < N; ++k) { // freq index
			Complex Ak = new Complex();
			for (int n = 0; n < N; ++n) { // time index
				expkn = Complex.exp(idospiN.times(k*n));
				Ak = Ak.plus(expkn.times(this.samples.getItem(1,n)));
			}
			transform.setItem(0, k, Ak);
		}
		
		/*CHRONO*/ FTChrono.stop();
		/*CHRONO*/ System.out.println("Computing Time DFTL:" + FTChrono.toString());
		
		isTransformed = true;
	}

	/**
	 * Returns true if n is a strictly positive power of two.
	 * @param n The number to test.
	 * @return true if n>0 and n is a power of two.
	 */
	private static boolean isPowerOfTwo(int n) {
		return n > 0 && (n & (n - 1)) == 0;
	}

	/**
	 * Radix-2 Cooley-Tukey FFT, iterative with a bit-reversal permutation. x.length MUST be a
	 * power of two (checked by every caller via isPowerOfTwo() before calling this). Computes
	 * X[k] = SUM_n x[n]*e^(-2*pi*i*k*n/N) exactly, for arbitrary complex input -- unlike DFT()'s
	 * O(n^2) path below, it never assumes a real-valued signal.
	 * @param x The input samples, length a power of two.
	 * @return The N DFT coefficients, in natural (not bit-reversed) order.
	 */
	private static Complex[] fft(Complex[] x) {
		int n = x.length;
		Complex[] a = new Complex[n];
		for (int i = 0; i < n; ++i) a[i] = x[i].copy();

		int bits = Integer.numberOfTrailingZeros(n);
		for (int i = 0; i < n; ++i) {
			int j = Integer.reverse(i) >>> (32 - bits);
			if (j > i) {
				Complex tmp = a[i];
				a[i] = a[j];
				a[j] = tmp;
			}
		}

		for (int len = 2; len <= n; len <<= 1) {
			Complex wlen = Complex.exp(Complex.i.times(-Complex.DOS_PI / len));
			for (int i = 0; i < n; i += len) {
				Complex w = new Complex(1, 0);
				for (int j = 0; j < len / 2; ++j) {
					Complex u = a[i + j];
					Complex v = a[i + j + len / 2].times(w);
					a[i + j] = u.plus(v);
					a[i + j + len / 2] = u.minus(v);
					w = w.times(wlen);
				}
			}
		}
		return a;
	}

	/**
	 * Inverse of fft(), via the standard conjugate trick (ifft(X) = conj(fft(conj(X)))/N) --
	 * reuses the same butterfly network instead of a second one with the opposite twiddle sign.
	 * @param X The N DFT coefficients, in natural order. X.length MUST be a power of two.
	 * @return The N reconstructed time-domain samples.
	 */
	private static Complex[] ifft(Complex[] X) {
		int n = X.length;
		Complex[] conjX = new Complex[n];
		for (int i = 0; i < n; ++i) conjX[i] = X[i].conjugate();
		Complex[] y = fft(conjX);
		Complex[] result = new Complex[n];
		for (int i = 0; i < n; ++i) result[i] = y[i].conjugate().divides(n);
		return result;
	}

	/**
	 * Calculates the DFT. When N is a power of two, uses a radix-2 FFT (O(n log n), exact for any
	 * complex-valued signal, no symmetry assumption). Otherwise falls back to the direct O(n^2)
	 * definition below, computed for every frequency index -- no symmetry shortcut, so it stays
	 * correct for a genuinely complex-valued input signal, not just real-valued ones.
	 * <p>
	 * <b>KNOWN LIMITATION, resolved:</b> this path used to compute only the first half of the
	 * frequencies directly and fill in the rest via {@code X[N-k]=conj(X[k])}, a symmetry that only
	 * holds for a real-valued input signal -- wrong in silence for a complex-valued one whenever N
	 * isn't a power of two. Confirmed and fixed by dropping the shortcut; the ~2x speedup it gave
	 * for real signals isn't worth a silently wrong result for complex ones on this already-slow
	 * fallback path.
	 * @param sampleFreq The frequency used to sample the function.
	 */
	public void DFT(int sampleFreq) {
		this.N = sampleFreq;
		this.sampleFreq = sampleFreq;

		System.out.println("Samples:" + this.N);
		System.out.printf("sample frequency: %.3e Hz\n",(double)sampleFreq);

		if (!isSampled) doTrfSampling();

		transform = new MatrixComplex(1,N);

		/*CHRONO*/ Chronometer FTChrono = new Chronometer();

		if (isPowerOfTwo(N)) {
			Complex[] x = new Complex[N];
			for (int n = 0; n < N; ++n) x[n] = this.samples.getItem(1, n);
			Complex[] X = fft(x);
			for (int k = 0; k < N; ++k) transform.setItem(0, k, X[k]);
		} else {
			Complex idospiN = Complex.i.times(-Complex.DOS_PI/N); // -2*pi*i/N
			Complex expkn = new Complex();
			for (int k = 0; k < N; ++k) { // freq index
				Complex Ak = new Complex();
				for (int n = 0; n < N; ++n) { // time index
					expkn = Complex.exp(idospiN.times(k*n));
					Ak = Ak.plus(expkn.times(this.samples.getItem(1,n)));
				}
				transform.setItem(0, k, Ak);
			}
		}

		/*CHRONO*/ FTChrono.stop();
		/*CHRONO*/ System.out.println("Computing Time DFT:" + FTChrono.toString());

		isTransformed = true;
	}

	/**
	 * Calculates the DFT using the signal definitions.
	 */
	public void DFT() {
		DFT(this.sampleFreq);
	}

	/**
	 * Calculates the samples of the function using the Inverse DFT. When N is a power of two,
	 * uses the inverse radix-2 FFT (O(n log n)); otherwise falls back to the existing O(n^2) path.
	 * Both paths are exact for arbitrary complex-valued transform coefficients (no symmetry
	 * assumption on this side, unlike DFT()'s O(n^2) path).
	 */
	public void IDFT() {
		if(!isTransformed) {
			System.out.println("WARNING:DFT coeficients not calculated/loaded. Do the DFT or Load them first.");
			return;
		}

		System.out.println("Computing the Inverse DFT...");
		samples = new MatrixComplex(2,N);
		Complex point = loLimit.copy();
    	Complex incr = upLimit.minus(loLimit).divides(N);

		/*CHRONO*/ Chronometer IDFTChrono = new Chronometer();

		if (isPowerOfTwo(N)) {
			Complex[] X = new Complex[N];
			for (int k = 0; k < N; ++k) X[k] = this.transform.getItem(0, k);
			Complex[] x = ifft(X);
			for (int n = 0; n < N; ++n) {
				samples.setItem(0, n, point);
				samples.setItem(1, n, x[n]);
				point = point.plus(incr);
			}
		} else {
			Complex idospiN = Complex.i.times(Complex.DOS_PI/N); // +2*pi*i/N
			Complex expkn = new Complex();
			Complex Tk = new Complex();
			for (int k = 0; k < N; ++k) { // time index
				Tk.setComplexRec(0,0);
				for (int n = 0; n < N; ++n) { // freq index
					expkn = Complex.exp(idospiN.times(k*n));
					Tk = Tk.plus(expkn.times(this.transform.getItem(0, n)));
				}
				samples.setItem(0, k, point);
				samples.setItem(1, k, Tk.divides(N));
				point = point.plus(incr);
			}
		}

		/*CHRONO*/ IDFTChrono.stop();
		/*CHRONO*/ System.out.println("Computing Time IDFT:" + IDFTChrono.toString());
		isSampled = true;
	}

	/**
	 * Prints in the console the DFT coefficients.
	 */
	public void printTCoefs() {
		if(!isTransformed) {
			System.out.println("WARNING:Function not transformed yet. Transform it first.");
			return;
		}
		//series.transpose().println("Fourier Series Coefs ");
		System.out.println("Fourier Transform Coefs ");
		int idxDig = (N+"").length();
		String strFormat = "A%0"+idxDig+"d = %s\n";
		for (int i = 0; i < transform.cols(); ++i) {
			System.out.printf(strFormat, i, transform.getItem(0, i).toString());
		}
	}
	
	/**
	 * Enumerative for plotting the DFT in different operators 
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
	public static enum e_domain {
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
	
	
	/**
	 * 
	 */
	//@Override 
	public MatrixComplex normalize(MatrixComplex values) {
		double maxVal = 0.0;
		double absVal;
		
				
		for (int k = 0; k < N; ++k) { // time index
			absVal = Math.abs(values.getItem(0, k).rep());
			if (absVal > maxVal)  maxVal = absVal; 
		}
		
		for (int k = 0; k < N; ++k) { // time index
			values.setItem(0, k, values.getItem(0, k).divides(maxVal));
		}
		
		return values;
	}
	
	/*
	 * ***********************************************
	 * DFT PLOTTING METHODS 
	 * ***********************************************
	 */

	/**
	 * Plots the samples of the function used for the Fourier analysis
	 * @param title The title of the graphic.
	 * @param showIm True for plotting the imaginary part.
	 */
	public void plotSamples(String title, boolean showIm, e_lineStyle lineStyle) {
		plotSamples(title, this.N, showIm, lineStyle);
	}
	
	/**
	 * Plots the DFT graphic in the domain of Coefficients or Frequency
	 * @param Title The titel of the polt
	 * @param domain The domain in which plot the DFT
	 * @param operator The operator used
	 * @param logscale True in y axis should be set in logarithmic scale
	 */
	public void plotDFT(String Title, e_domain domain, e_operator operator, boolean logscale, e_lineStyle lineStyle) {
		if(!isTransformed) {
			System.out.println("WARNING:DFT coeficients not calculated/loaded. Do the DFT or Load them first.");
			return;
		}
		switch (domain) {
		case SAMP: plotDFTsamp(Title, domain, operator, logscale, lineStyle); break;
		case FREC: plotDFTfrec(Title, domain , operator, logscale, lineStyle); break;
		}
	}
	
	/**
	 * Does the plot the DFT graphic in the domain of Coefficients
	 * @param Title The title of the polt
	 * @param domain The domain in which plot the DFT
	 * @param operator The operator used
	 * @param logscale True in y axis should be set in logarithmic scale
	 */
	private void plotDFTsamp(String Title, e_domain domain, e_operator operator, boolean logscale, e_lineStyle lineStyle) {
		//Split the data into Re/Mod and Im/Pha parts
		double dataRe[][]  = new double[transform.cols()][1];
		double dataIm[][]  = new double[transform.cols()][1];
		Complex cNum= new Complex();
		for (int t = 0; t < transform.cols(); ++t) {
			cNum = eval(transform.getItem(0, t), operator, logscale);
			dataRe[t][0] = operator == e_operator.COMPLEX ? cNum.rep() : cNum.mod();
			dataIm[t][0] = operator == e_operator.COMPLEX ? cNum.imp() : cNum.pha();
		}
		
		//Plot the data
		JavaPlot p = new JavaPlot();
		p.setTitle(Title);
		String x2label = (domain == e_domain.SAMP ? "\"Samples' Index\"" : "\"Spectrum in Hz\"");
		p.set("x2label", x2label);
		p.addPlot(dataRe);
		p.addPlot(dataIm);
		p.set("zeroaxis", "");
		//p.set("style","data lines");
		p.set("style", setLineStyle(lineStyle));
		p.set("xlabel", domain == e_domain.SAMP ? "\"SAMPLES\"" : "\"Hz\"");
		if (logscale) p.set("logscale", "y");
		p.set("grid","");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		p.plot();
	}
		
	/**
	 * Does the plot the DFT graphic in the domain of Frequency
	 * @param Title The title of the polt
	 * @param domain The domain in which plot the DFT
	 * @param operator The operator used
	 * @param logscale True in y axis should be set in logarithmic scale
	 */
	private void plotDFTfrec(String Title, e_domain domain, e_operator operator, boolean logscale, e_lineStyle lineStyle) {
		//Split the data into Re/Mod and Im/Pha parts
		double dataRe[][]  = new double[transform.cols()][2];
		double dataIm[][]  = new double[transform.cols()][2];
		double incr = sampleFreq/N;
		double point;
		
		point = -sampleFreq/2;
		for (int t = 0; t < transform.cols(); ++t) {
			dataRe[t][0] = point;
			dataIm[t][0] = point;
			point += incr;			
		}
		
		Complex Ak = new Complex();
		int N2 = N/2;
		for (int k = 0; k < N2; ++k) { // freq index
			Ak = eval(transform.getItem(0, k), operator, logscale);
			dataRe[k+N2][1] = operator == e_operator.COMPLEX ? Ak.rep() : Ak.mod();
			dataIm[k+N2][1] = operator == e_operator.COMPLEX ? Ak.imp() : Ak.pha();
		}
		int k0 = N%2 == 0 ? 0: 1;
		for (int k = k0; k < N2 + k0; ++k) {
			Ak = eval(transform.getItem(0, N2+k), operator, logscale);
			dataRe[k][1] = operator == e_operator.COMPLEX ? Ak.rep() : Ak.mod();
			dataIm[k][1] = operator == e_operator.COMPLEX ? Ak.imp() : Ak.pha();
		}

		//Plot the data
		JavaPlot p = new JavaPlot();
		p.setTitle(Title);
		String x2label = (domain == e_domain.SAMP ? "\"Samples' Index\"" : "\"Spectrum in Hz\"");
		p.set("x2label", x2label);
		p.addPlot(dataRe);
		p.addPlot(dataIm);
		p.set("zeroaxis", "");
		p.set("xlabel", domain == e_domain.SAMP ? "\"SAMPLES\"" : "\"Hz\"");
		//p.set("style","data lines");
		p.set("style", setLineStyle(lineStyle));
		if (logscale) p.set("logscale", "y");
		p.set("grid","");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		p.plot();
	}

	/*
	 * ***********************************************
	 * SIGNAL FILTERING 
	 * ***********************************************
	 */

	/**
	 * Creates a Band Pass filter 
	 * @param gain The gain of the filter (0..N]
	 * @param fIni The lower frequency that passes the filter
	 * @param bandwidth The bandwidth of the filter
	 * @param samplefreq The number of samples (sample frequency) of the samples of the signal 
	 */
	public void bandPassFilter(double gain, double fIni, double bandwidth, int samplefreq) {		
		this.sampleFreq = samplefreq;
		this.N = samplefreq;
		this.period = this.upLimit.minus(this.loLimit);

		transform = new MatrixComplex(1,N);
		Complex fVal = new Complex();
		double fEnd = fIni + bandwidth;
		double incr = sampleFreq/N;
		//double incr = period.rep()/N;
		double N2 = N/2;
		double N2p;
		double point = -sampleFreq/2;
		for(int i = 0; i < N; ++i) {
			N2p = N2-Math.abs(point);
			fVal.setComplexRec((N2p < fIni || N2p > fEnd) ? 0.0 : gain, 0.0);
			transform.setItem(0, i, fVal);
			point += incr;
		}
		isTransformed = true;
		this.IDFT();
		filterData = "Band Pass - G:" + gain + " Fcentral:" + (fIni+bandwidth/2) + " Hz Bw:" + bandwidth + " Hz" + " Nbr.Samp.:" + samplefreq;
	}

	/**
	 * 
	 * Creates a Band Pass filter with slopes.
	 * @param gain The gain of the filter (0..N]
	 * @param fIni The lower frequency that passes the filter
	 * @param bandwidth The bandwidth of the filter
	 * @param slope The steepness or severity of a filter’s attenuation in percentage. The slope MUST be positive.
	 * @param samplefreq The number of samples (sample frequency) of the samples of the signal 
	 */
	public void bandPassFilter(double gain, double fIni, double bandwidth, double slope, int samplefreq) {
		if (slope <= 0 || slope > 1) {
			System.out.println("Slope must be in the range (0,1]. Slope has been set to .5 (50%)");
			slope = .5;
		}
		this.sampleFreq = samplefreq;
		this.N = samplefreq;
		this.period = this.upLimit.minus(this.loLimit);

		transform = new MatrixComplex(1,N);
		Complex fVal = new Complex();
		double fEnd = fIni + bandwidth;
		double incr = sampleFreq/N;
		double factor = 0;
		double N2 = N/2;
		double BW2 = bandwidth/2;
		double point = -sampleFreq/2;
		double N2p;
		double gainIncr = gain * slope;
		int i0 = 0, j = 0;
		for(int i = 0; i < N2; ++i) {
			N2p = N2-Math.abs(point);
			if (N2p <= fIni || N2p > fEnd) {
				fVal.setComplexRec(0.0, 0.0);
				factor = 0;
				i0 = i;
			}
			else {
				if (N2p >= fIni && N2p < fIni+BW2) {
					factor += gainIncr;
					factor = factor > gain ? gain : factor;
					fVal.setComplexRec(factor, 0.0);
					++j;
				}
				else {
					fVal = transform.getItem(0, i0+j--);
				}
			}
			transform.setItem(0, i, fVal);
			// DC (i=0) is self-paired -- no negative-frequency bin to mirror into. For i>0, bin N-i
			// (frequency -i) must equal bin i (frequency +i) for a real-valued filter.
			if (i > 0) {
				transform.setItem(0, N-i, fVal);
			}
			point += incr;
		}
		// Nyquist bin (N2) is self-paired (its own conjugate) -- no mirror source exists for it,
		// so it carries the last computed value, same boundary convention as slopeFilter().
		transform.setItem(0, (int) N2, transform.getItem(0, (int) N2 - 1));
		isTransformed = true;
		this.IDFT();
		filterData = "Band Pass Slope: " + (slope*100) + "% - G:"+ gain + " Fcentral:" + (fIni+bandwidth/2) + " Hz Bw:" + bandwidth + " Hz" + " Nbr.Samp.:" + samplefreq;
	}

	/**
	 * Creates a delta filter
	 * @param gain The gain of the filter (0..N]
	 * @param fDelta The frequency of the delta.
	 * @param samplefreq The number of samples (sample frequency) of the samples of the signal 
	 */
	public void deltaFilter(double gain, double fDelta, int samplefreq) {		
		this.sampleFreq = samplefreq;
		this.N = samplefreq;
		this.period = this.upLimit.minus(this.loLimit);

		transform = new MatrixComplex(1,N);
		Complex fVal = new Complex();
		double incr = sampleFreq/N;
		double N2 = N/2;
		double N2p;
		double point = -sampleFreq/2;
		for(int i = 0; i < N; ++i) {
			N2p = N2-Math.abs(point);
			fVal.setComplexRec((N2p == fDelta) ? gain: 0.0 , 0.0);
			transform.setItem(0, i, fVal);
			point += incr;
		}
		isTransformed = true;
		this.IDFT();
		filterData = "G:" + gain + " Fc:" + fDelta + " Hz Bw:1 Hz" + " Nbr.Samp.:" + samplefreq + " Hz";
	}

	/**
	 * Creates a slope filter, if the slope is positive it should be a high pass slope filter, otherwise it will be a low pass one.
	 * @param gain The gain of the filter (0..N]
	 * @param fIni The lower frequency that passes the filter
	 * @param slope The steepness or severity of a filter’s attenuation in percentage. The slope could be negative.
	 * @param samplefreq The number of samples (sample frequency) of the samples of the signal 
	 */
	public void slopeFilter(double gain, double fIni, double slope, int samplefreq) {
		if (slope == 0 || slope > 1) {
			System.out.println("Slope must be in the range [-1,0)(0,1]. Slope has been set to .5 (50%)");
			slope = .5;
		}
		if (slope < -1) {
			System.out.println("Slope must be in the range [-1,0)(0,1]. Slope has been set to -.5 (-50%)");
			slope = -.5;
		}

		this.sampleFreq = samplefreq;
		this.N = samplefreq;
		this.period = this.upLimit.minus(this.loLimit);

		transform = new MatrixComplex(1,N);
		Complex fVal = new Complex();
		int N2 = N/2;
		double g = gain*(slope);
		double gainp;
		if (slope < 0) gainp = gain; else gainp = 0;
		for (int i = 0; i < N2; ++i) {
			if (i > fIni) gainp += g;
			if (gainp > gain) gainp = gain;
			if (gainp < 0) gainp = 0;
			fVal.setComplexRec(gainp, 0.0);
			transform.setItem(0, i, fVal);
		}
		// Nyquist bin (N2) is self-paired (its own conjugate) -- no mirror source exists for it,
		// so it carries the last computed ramp value, same as before the fix below.
		transform.setItem(0, N2, transform.getItem(0, N2 - 1));
		for (int k = 1; k < N2; ++k) {
			transform.setItem(0, N2 + k, transform.getItem(0, N2 - k));
		}
		isTransformed = true;
		this.IDFT();
		filterData = "Slope: " + (slope*100) + "% - G:" + gain + " Fcentral:" + fIni+N/4 + " Hz Bw:" + 2*(fIni+N/4) + " Hz" + " Nbr.Samp.:" + samplefreq;
	}

	/**
	 * Creates a low pass filter.
	 * @param gain The gain of the filter (0..N]
	 * @param fIni The higher frequency that passes the filter
	 * @param samplefreq The number of samples (sample frequency) of the samples of the signal 
	 */
	public void lowPassFilter(double gain, double fIni, int samplefreq) {
		slopeFilter(gain, fIni, -1, samplefreq);
		filterData = "Low Pass - G:" + gain + " Fcut:" + 0 + " Hz Bw:" + fIni + " Hz" + " Nbr.Samp.:" + samplefreq;

	}
	
	/**
	 * Creates a high pass filter.
	 * @param gain The gain of the filter (0..N]
	 * @param fIni The lower frequency that passes the filter
	 * @param samplefreq The number of samples (sample frequency) of the samples of the signal 
	 */
	public void highPassFilter(double gain, double fIni, int samplefreq) {
		slopeFilter(gain, fIni, 1, samplefreq);
		filterData = "High Pass - G:" + gain + " Fcut:" + fIni + " Hz Bw:" + (sampleFreq/2-fIni) + " Hz" + " Nbr.Samp.:" + samplefreq;
	}

	/**
	 * Filters the signal
	 * @param signal The signal to filter
	 * @return the filtered signal
	 */
	public Fourier filter(Fourier signal) {
		Fourier signalF = new Fourier(signal.getSampleFreq(), signal.loLimit, signal.upLimit);
		signalF.transform = new MatrixComplex(1, signalF.N);
		
		/*CHRONO*/ Chronometer IDFTChrono = new Chronometer();

    	for (int i = 0; i < signalF.N; ++i) {
			signalF.transform.setItem(0, i, signal.transform.getItem(0, i).times(this.transform.getItem(0, i)));
		}

		/*CHRONO*/ IDFTChrono.stop();
		/*CHRONO*/ System.out.println("Computing Time Filter:" + IDFTChrono.toString());

		signalF.isTransformed = true;
		signalF.IDFT();	
		return signalF;
	}
	
	/*
	 * ***********************************************
	 * SIGNAL OPERATIONS 
	 * ***********************************************
	 */

	/**
	 * Calculates the addition of two signals
	 * @param signal
	 * @return
	 */
	public Fourier plus(Fourier signal) {
		Fourier sumSignal = new Fourier(this.N, this.loLimit, this.upLimit);
		sumSignal.samples = new MatrixComplex(2, this.N);
		sumSignal.transform = new MatrixComplex(1, this.N);
		for(int i = 0; i < this.N; ++i) {
			sumSignal.samples.setItem(0, i, this.samples.getItem(0, i));
			sumSignal.samples.setItem(1, i, this.samples.getItem(1, i).plus(signal.samples.getItem(1, i)));
			sumSignal.transform.setItem(0, i, this.transform.getItem(0, i).plus(signal.transform.getItem(0, i)));			
		}
		sumSignal.isSampled = true;
		sumSignal.isTransformed = true;
		return sumSignal;
	}
	
	/**
	 * Calculates the difference of two signals
	 * @param signal
	 * @return
	 */
	public Fourier minus(Fourier signal) {
		Fourier minusSignal = new Fourier(this.N, this.loLimit, this.upLimit);
		minusSignal.samples = new MatrixComplex(2, minusSignal.N);
		minusSignal.transform = new MatrixComplex(1, minusSignal.N);
		for(int i = 0; i < this.N; ++i) {
			minusSignal.samples.setItem(0, i, this.samples.getItem(0, i));
			minusSignal.samples.setItem(1, i, this.samples.getItem(1, i).minus(signal.samples.getItem(1, i)));
			minusSignal.transform.setItem(0, i, this.transform.getItem(0, i).minus(signal.transform.getItem(0, i)));			
		}
		minusSignal.isSampled = true;
		minusSignal.isTransformed = true;
		return minusSignal;
	}

	/**
	 * Calculates the convolution of two signals (or one signal through a filter) in the time domain.
	 * @param signal The signal to convolute.
	 * @return the convolutioned signal resultant 
	 */
	public Fourier convolution(Fourier signal) {
		Complex yVal = new Complex();
		Fourier signalF = new Fourier(signal.N, signal.loLimit, signal.upLimit);
		signalF.samples = new MatrixComplex(2, signalF.N);
		
		/*CHRONO*/ Chronometer IDFTChrono = new Chronometer();
    	
    	Complex point = signal.loLimit.copy();
		Complex incr = signal.upLimit.minus(signal.loLimit).divides(signal.N);
    	for(int t = 0; t < N; ++t) {
			signalF.samples.setItem(0, t, point);
			yVal.setComplexRec(0, 0);
			for(int n = 0; n < signal.N; ++n) {
				int t_n = t-n;
				if (t_n < 0) break;
				//y(t) = y() + signal(n)*filter(t-n) 
				yVal = yVal.plus(signal.samples.getItem(1, n).times(this.samples.getItem(1, t_n)));
			}
			signalF.samples.setItem(1, t, yVal);
			point = point.plus(incr);
		}

    	/*CHRONO*/ IDFTChrono.stop();
		/*CHRONO*/ System.out.println("Computing Time Filter:" + IDFTChrono.toString());

		signalF.isSampled = true;
		signalF.DFT(signalF.N);
		return signalF;
	}	
}

