package TestComplex;

import java.awt.image.SampleModel;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.*;
import com.ipserc.arith.signal.Fourier.*;

public class TestFilter02 {

	private static Complex sin(Complex z, int frec) {
		return Complex.sin(z.times(Math.PI*frec));
	}

	private static Complex cos(Complex z, int frec) {
		return Complex.cos(z.times(Math.PI*frec));
	}
	

	private static Complex func14(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Math.PI*(600+50*(Math.sin(Math.ceil(z.rep()%13))))));
	}

	private static Complex func15(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Math.PI*(800+50*(Math.sin(z.rep()%13)))));
	}

	private static Complex func16(Complex z) {
		//cos(z(70Hz+5sin(z mod 7)))
		return Complex.cos(z.times(Math.PI*(70+5*(Math.sin(z.rep()%7)))));
	}

	public TestFilter02() {		
	}

	public static void main(String[] args) {
		double gain;
		double fInit;
		double bandwidth;
		String filterparams;
		Fourier filter = new Fourier();
		Fourier signalFiltered;
		int boxSize = 65;

      	System.out.println(Complex.boxTitleRandom(boxSize, "FOURIER TRANSF FILTER TEST"));
		/*****************
		 * SIGNAL SECTION
		 *****************/
		System.out.println(Complex.boxTextRandom(boxSize, "Signal Section"));
		double loLimit = -80;
		double upLimit = 80;
		int samplefreq = 12000;
		System.out.println("Creating signal...");
		Fourier signal = new Fourier(z -> func15(z), loLimit, upLimit);
		signal.DFT(samplefreq);

		// PLOTING RESULTS
		System.out.println("SIGNAL Plotting results...");		
		signal.plotSamples("Original SIGNAL", true, e_lineStyle.LINES);
		signal.plotDFT("Original signal", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		/***********************
		 * FILTER SECTION
		 ***********************/
		System.out.println(Complex.boxTextRandom(boxSize, "Filter Section"));
		// FILTER DEFINITION
		System.out.println("Creating filter...");
		gain = 5;
		fInit = 0;
		bandwidth = 2000;
		filterparams = "G:" + gain + " Fc:" + (fInit+bandwidth/2) + " Hz Bw:" + bandwidth + " Hz";
		filter = new Fourier();
		filter.bandPassFilter(gain, fInit, bandwidth, samplefreq);

		// FILTERING PROCESS
		System.out.println("Processing signal with filter...");
		signalFiltered = filter.filter(signal);
		
		//PLOTING RESULTS
		System.out.println("Plotting results...");		
		filter.plotSamples("FILTER " + filterparams, true, e_lineStyle.LINES);
		filter.plotDFT("FILTER " + filterparams, e_domain.SAMP, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		filter.plotDFT("FILTER " + filterparams, e_domain.FREC, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		signalFiltered.plotSamples("Filtered signal " + filterparams, true, e_lineStyle.LINES);
		signalFiltered.plotDFT("Filtered signal "+ filterparams, e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		
		System.out.println("\n" + Complex.repeat("-", 20) + "\n");

		/***********************
		 * FILTER SECTION
		 ***********************/
		System.out.println(Complex.boxTextRandom(boxSize, "Filter Section"));
		// FILTER DEFINITION
		System.out.println("Creating filter...");
		gain = 5;
		fInit = 2000;
		bandwidth = 2000; 
		filterparams = "G:" + gain + " Fc:" + (fInit+bandwidth/2) + " Hz Bw:" + bandwidth + " Hz";
		filter = new Fourier();
		filter.bandPassFilter(gain, fInit, bandwidth, samplefreq);

		// FILTERING PROCESS
		System.out.println("Processing signal with filter...");
		signalFiltered = filter.filter(signal);
		
		//PLOTING RESULTS
		System.out.println("Plotting results...");		
		filter.plotDFT("Filter 2 " + filterparams, e_domain.FREC, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		signalFiltered.plotSamples("Filtered signal " + filterparams, true, e_lineStyle.LINES);
		signalFiltered.plotDFT("Filtered signal "+ filterparams, e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		
		System.out.println("\n" + Complex.repeat("-", 20) + "\n");
		
		/***********************
		 * FILTER SECTION
		 ***********************/
		System.out.println(Complex.boxTextRandom(boxSize, "Filter Section"));
		// FILTER DEFINITION
		System.out.println("Creating filter...");
		gain = 5;
		fInit = 4000;
		bandwidth = 2000; 
		filterparams = "G:" + gain + " Fc:" + (fInit+bandwidth/2) + " Hz Bw:" + bandwidth + " Hz";
		filter = new Fourier();
		filter.bandPassFilter(gain, fInit, bandwidth, samplefreq);

		// FILTERING PROCESS
		System.out.println("Processing signal with filter...");
		signalFiltered = filter.filter(signal);
		
		//PLOTING RESULTS
		System.out.println("Plotting results...");		
		filter.plotDFT("Filter 2 " + filterparams, e_domain.FREC, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		signalFiltered.plotSamples("Filtered signal " + filterparams, true, e_lineStyle.LINES);
		signalFiltered.plotDFT("Filtered signal "+ filterparams, e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
	}
}
