package TestComplex;

import java.awt.image.SampleModel;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.*;
import com.ipserc.arith.signal.Fourier.*;
import java.io.*;

public class TestFilter01 {
	private static boolean __NORMALIZE__ = false;

	private static Complex sin(Complex z, int frec) {
		return Complex.sin(z.times(Math.PI*frec));
	}

	private static Complex cos(Complex z, int frec) {
		return Complex.cos(z.times(Math.PI*frec));
	}

	private static Complex func14(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Math.PI*(7000+50*(Math.sin(Math.ceil(z.rep()%13))))));
	}

	private static Complex func15(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Math.PI*(7000+50*(Math.sin(z.rep()%13)))));
	}

	private static Complex func16(Complex z) {
		//cos(z(70Hz+5sin(z mod 7)))
		return Complex.cos(z.times(Math.PI*(70+5*(Math.sin(z.rep()%7)))));
	}

	public static void filter(double gain, double fInit, double bandwidth, int samplefreq, Fourier signal) {
		int boxSize = 65;
		String filterparams;
		Fourier filter = new Fourier();
		Fourier signalFiltered;

		// FILTER DEFINITION
		System.out.println(Complex.boxTextRandom(boxSize, "Filter Section"));
		System.out.println("Creating filter...");
		filterparams = "G:" + gain + " Fc:" + (fInit+bandwidth/2) + " Hz Bw:" + bandwidth + " Hz";
		System.out.println("Filter Params:" + filterparams);
		filter = new Fourier();
		filter.bandPassFilter(gain, fInit, bandwidth, samplefreq);

		// FILTERING PROCESS
		System.out.println("Processing signal with filter...");
		signalFiltered = filter.filter(signal);
		
		//PLOTING RESULTS
		System.out.println("Plotting results...");		
		filter.plotDFT("Filter " + filterparams, e_domain.FREC, e_operator.MAGNITUDE, false, e_lineStyle.IMPULSES);
		signalFiltered.plotSamples("Filtered signal " + filterparams, true, e_lineStyle.LINES);
		signalFiltered.plotDFT("Filtered signal " + filterparams, e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		signalFiltered.saveRAWSamples("/home/ipserc/Saco/Fourier/signal_func"+samplefreq+"_"+fInit+"_"+bandwidth+".raw", __NORMALIZE__);

		System.out.println("\n" + Complex.repeat("-", 20) + "\n");

	}

	public static void main(String[] args) {
		double gain;
		double fInit;
		double bandwidth;
		int boxSize = 65;
		Fourier signal;
		int samplefreq = 16000; // 44100; //

       	System.out.println(Complex.boxTitleRandom(boxSize, "FOURIER TRANSF FILTER TEST"));
		/*****************
		 * SIGNAL SECTION
		 *****************/
       	// String signalFileNamePath = "/home/ipserc/Saco/Sonido/muestra_"+samplefreq+".raw"; // "/home/ipserc/saco/Sonido/muestra01.raw"; // "/home/ipserc/saco/Sonido/signal_func8000.raw";
       	String signalFileNamePath = "/home/ipserc/Saco/Sonido/muestra01.raw";
       	File samplesFile = new File(signalFileNamePath);
       	if (samplesFile.exists()) {
       		signal = new Fourier(signalFileNamePath);
       		signal.DFT(signal.getSampleFreq());
       	}
       	else {
			double loLimit = -25;
			double upLimit = 25;
			System.out.println(Complex.boxTextRandom(boxSize, "Signal Section"));
			System.out.println("Creating signal...");
			signal = new Fourier(z -> func16(z), loLimit, upLimit);
			signal.DFT(samplefreq);
			signal.saveRAWSamples(signalFileNamePath, __NORMALIZE__);
			//signal.saveSamples("/home/ipserc/saco/signal_func44100.txt", "");
       	}
		signal.plotSamples("Original Signal", signal.getN(), true, e_lineStyle.LINES);

		/***********************
		 * FILTER SECTION
		 ***********************/
		
		/* * /
		gain = 5; fInit = 300; bandwidth = 30;
		filter(gain, fInit, bandwidth, samplefreq, signal);
		
		gain = 5; fInit = 300; bandwidth = 60; 
		filter(gain, fInit, bandwidth, samplefreq, signal);
		
		gain = 5; fInit = 600; bandwidth = 60; 
		filter(gain, fInit, bandwidth, samplefreq, signal);

		gain = 5; fInit = 1600; bandwidth = 600; 
		filter(gain, fInit, bandwidth, samplefreq, signal);

		gain = 5; fInit = 2000; bandwidth = 1500; 
		filter(gain, fInit, bandwidth, samplefreq, signal);

		gain = 5; fInit = 3000; bandwidth = 500; 
		filter(gain, fInit, bandwidth, samplefreq, signal);
		
		gain = 5; fInit = 3000; bandwidth = 1500; 
		filter(gain, fInit, bandwidth, samplefreq, signal);

		gain = 5; fInit = 800; bandwidth = 2500; 
		filter(gain, fInit, bandwidth, samplefreq, signal);

		gain = 5; fInit = 3800; bandwidth = 1; 
		filter(gain, fInit, bandwidth, samplefreq, signal);
		
		gain = 5; fInit = 100; bandwidth = 1; 
		filter(gain, fInit, bandwidth, samplefreq, signal);

		gain = 5; fInit = 1000; bandwidth = 1; 
		filter(gain, fInit, bandwidth, samplefreq, signal);
		
		gain = 5; fInit = 2000; bandwidth = 1; 
		filter(gain, fInit, bandwidth, samplefreq, signal);

		gain = 5; fInit = 3997; bandwidth = 1; 
		filter(gain, fInit, bandwidth, samplefreq, signal);

		gain = 5; fInit = 3998; bandwidth = 1; 
		filter(gain, fInit, bandwidth, samplefreq, signal);

		gain = 5; fInit = 3999; bandwidth = 1; 
		filter(gain, fInit, bandwidth, samplefreq, signal);
		
		gain = 5; fInit = 3996; bandwidth = 1; 
		filter(gain, fInit, bandwidth, samplefreq, signal);

		/* */
		
		gain = 5; fInit = 200; bandwidth = 100; 
		gain = 5; fInit = 500; bandwidth = 100; 
		gain = 5; fInit = 800; bandwidth = 100; 		
		gain = 5; fInit = 1100; bandwidth = 100; 		
		gain = 5; fInit = 1400; bandwidth = 100; 
		gain = 5; fInit = 1700; bandwidth = 100; 
		gain = 5; fInit = 5000; bandwidth = 100; 
		gain = 5; fInit = 7000; bandwidth = 100; 
		gain = 5; fInit = 150; bandwidth = 100;
		gain = 5; fInit = 150; bandwidth = 10;
		gain = 5; fInit = 4000; bandwidth = 4000; 
		gain = 5; fInit = 0; bandwidth = 16000; 		
		gain = 5; fInit = 0; bandwidth = 3000; 		
		gain = 5; fInit = 0; bandwidth = 800; 		
		gain = 5; fInit = 0; bandwidth = 1100;
		gain = 5; fInit = 0; bandwidth = 1300;
		gain = 1; fInit = 1100; bandwidth = 600;
		gain = 1; fInit = 0; bandwidth = 4000;
		gain = 1; fInit = 4000; bandwidth = 4000;
		filter(gain, fInit, bandwidth, samplefreq, signal);


	}
}
