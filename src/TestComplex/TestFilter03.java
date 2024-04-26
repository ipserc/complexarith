package TestComplex;

import java.awt.image.SampleModel;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.*;
import com.ipserc.arith.signal.Fourier.*;

public class TestFilter03 {

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
		return Complex.cos(z.times(Math.PI*(2300+50*(Math.sin(z.rep()%13)))));
	}

	private static Complex func16(Complex z) {
		//cos(z(70Hz+5sin(z mod 7)))
		return Complex.cos(z.times(Math.PI*(70+5*(Math.sin(z.rep()%7)))));
	}

	private static Complex func17(Complex z) {
		//cos(z(70Hz+5sin(z mod 7)))
		return Complex.cos(z.times(Math.PI*70)).times(Complex.sin(z.times(Math.PI*120)));
	}

	private static Complex func18(Complex z) {
		//cos(z(70Hz+5sin(z mod 7)))
		//(50f-20)/159 f=64 --> 20
		return Complex.cos(z.times(Math.PI*20)).plus(Complex.sin(z.times(Math.PI*120)));
	}

	public static void main(String[] args) {
		double gain;
		double fInit;
		double bandwidth;
		double loLimit;
		double upLimit;
		int samplefreq;
		double slope;
		Fourier signal;
		Fourier filter = new Fourier();
		Fourier signalDFT , signalCONV, signalDIFF;
		int boxSize = 65;

      	System.out.println(Complex.boxTitleRandom(boxSize, "FOURIER TRANSF FILTER TEST"));		
		/*****************
		 * SIGNAL SECTION
		 *****************/
		System.out.println(Complex.boxTextRandom(boxSize, "Signal Section"));
		boolean createSignal = true;
		if (createSignal) {
			loLimit = -30; //Math.PI/40;
			upLimit = +30; //Math.PI/40;
			samplefreq = 30000;
			System.out.println("Creating signal...");
			//Fourier signal = new Fourier(z -> sin(z,2000), loLimit, upLimit);
			signal = new Fourier(z -> func16(z), loLimit, upLimit);
			signal.DFT(samplefreq);
		
			// SAVING DATA
			signal.saveSamples("/home/ipserc/saco/signal_samples.txt");
			signal.saveDFT("/home/ipserc/saco/signal_dft.txt");
		}
		else {
			signal = new Fourier("/home/ipserc/saco/signal_samples.txt");
			signal.readDFT("/home/ipserc/saco/signal_dft.txt");
			loLimit = signal.getLoLimit().rep();
			upLimit = signal.getUpLimit().rep();
			samplefreq = signal.getSampleFreq();			
		}
		
		// PLOTING RESULTS
		System.out.println("SIGNAL Plotting results...");		
		signal.plotSamples("Original SIGNAL", true, e_lineStyle.IMPULSES);
		signal.plotDFT("Original signal", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		/***********************
		 * FILTER SECTION
		 ***********************/
		System.out.println(Complex.boxTextRandom(boxSize, "Filter Section"));
		boolean createFilter = true;
		// FILTER DEFINITION
		if (createFilter) {
			System.out.println("\nCreating filter...");
			gain = 1;
			fInit = 0;
			bandwidth = 2500;
			slope = .11;
			filter = new Fourier(samplefreq, loLimit, upLimit);
			// SELECT A FILTER
			filter.bandPassFilter(gain, fInit, bandwidth, samplefreq);
			//filter.bandPassFilter(gain, fInit, bandwidth, slope, samplefreq);
			//filter.deltaFilter(gain, fInit, samplefreq);
			//filter.slopeFilter(gain, fInit, slope, samplefreq);
			//filter.lowPassFilter(gain, fInit, samplefreq);
			//filter.highPassFilter(gain, fInit, samplefreq);
			
			// SAVING DATA
			filter.saveSamples("/home/ipserc/saco/filter_samples.txt");
			filter.saveDFT("/home/ipserc/saco/filter_dft.txt");	

		}
		else {
			filter = new Fourier("/home/ipserc/saco/filter_samples.txt");
			filter.readDFT("/home/ipserc/saco/filter_dft.txt");
		}
		
		System.out.println("Filter " + filter.getFilterData());
		
		// PLOTING RESULTS
		System.out.println("FILTER Plotting results...");		
		filter.plotSamples("FILTER " + filter.getFilterData(), true, e_lineStyle.IMPULSES);
		filter.plotDFT("FILTER "+ filter.getFilterData(), e_domain.FREC, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		//filter.plotDFT("FILTER "+ filter.getFilterData(), e_domain.SAMP, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		
		/***********************
		 * DFT SECTION
		 ***********************/
		System.out.println(Complex.boxTextRandom(boxSize, "DFT Section"));
		boolean createDFT = true;
		// DFT FILTERING PROCESS
		if (createDFT) {
			System.out.println("\nProcessing signal with DFT filter...");
			signalDFT = filter.filter(signal);
			
			// SAVING DATA
			signalDFT.saveSamples("/home/ipserc/saco/dft_filt_signal_samples.txt");
			signalDFT.saveDFT("/home/ipserc/saco/dft_filt_signal_dft.txt");
		}
		else {
			signalDFT = new Fourier("/home/ipserc/saco/dft_filt_signal_samples.txt");
			signalDFT.readDFT("/home/ipserc/saco/dft_filt_signal_dft.txt");			
		}

		//PLOTING RESULTS
		System.out.println("DFT Plotting results...");		
		signalDFT.plotSamples("DFT Filtered signal " + filter.getFilterData(), true, e_lineStyle.IMPULSES);
		signalDFT.plotDFT("DFT Filtered signal " + filter.getFilterData(), e_domain.FREC, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		signalDFT.plotDFT("DFT Filtered signal " + filter.getFilterData(), e_domain.SAMP, e_operator.MAGNITUDE, true, e_lineStyle.LINES);		
		/***/
		
		/***********************
		 * CONVOLUTION SECTION
		 ***********************/
		System.out.println(Complex.boxTextRandom(boxSize, "Convolution Section"));
		boolean createCONV = true;
		// CONVOLUTION FILTERING PROCESS
		if (createCONV) {
			System.out.println("\nProcessing signal with CONVOLUTION filter...");
			signalCONV = filter.convolution(signal);
			//signalCONV = filter.convolution1(signal, 0);
			
			// SAVING DATA
			signalCONV.saveSamples("/home/ipserc/saco/convolution_samples.txt");
			signalCONV.saveDFT("/home/ipserc/saco/convolution_dft.txt");
		}
		else {
			signalCONV = new Fourier("/home/ipserc/saco/convolution_samples.txt");
			signalCONV.readDFT("/home/ipserc/saco/convolution_dft.txt");						
		}

		//PLOTING RESULTS
		System.out.println("CONVOLUTION Plotting results...");		
		signalCONV.plotSamples("CONVOLUTION Filtered signal " + filter.getFilterData(), true, e_lineStyle.IMPULSES);
		signalCONV.plotDFT("CONVOLUTION Filtered signal " + filter.getFilterData(), e_domain.FREC, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		signalCONV.plotDFT("CONVOLUTION Filtered signal " + filter.getFilterData(), e_domain.SAMP, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		/*
		signalDIFF = signal.minus(signalDFT);
		// PLOTING RESULTS
		System.out.println("ORIG-DFT Plotting results...");		
		signalDIFF.plotSamples("ORIG-DFT", true, e_lineStyle.IMPULSES);
		signalDIFF.plotDFT("ORIG-DFT", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		//signalDiff.saveSamples("/home/ipserc/saco/diff_samples.txt");

		signalDIFF.saveSamples("/home/ipserc/saco/orig_minus_dft_signal_samples.txt");
		signalDIFF.saveDFT("/home/ipserc/saco/orig_minus_dft_signal_dft.txt");

		signalDIFF = signal.minus(signalCONV);
		// PLOTING RESULTS
		System.out.println("ORIG-CONV Plotting results...");		
		signalDIFF.plotSamples("ORIG-CONV", true, e_lineStyle.IMPULSES);
		signalDIFF.plotDFT("ORIG-CONV", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		//signalDiff.saveSamples("/home/ipserc/saco/diff_samples.txt");

		signalDIFF.saveSamples("/home/ipserc/saco/orig_minus_conv_signal_samples.txt");
		signalDIFF.saveDFT("/home/ipserc/saco/orig_minus_conv_signal_dft.txt");

		signalDIFF = signalDFT.minus(signalCONV);
		// PLOTING RESULTS
		System.out.println("DFT-CONV Plotting results...");		
		signalDIFF.plotSamples("DFT-CONV ", true, e_lineStyle.IMPULSES);
		signalDIFF.plotDFT("DFT-CONV ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		//signalDiff.saveSamples("/home/ipserc/saco/diff_samples.txt");

		signalDIFF.saveSamples("/home/ipserc/saco/dft_minus_conv_signal_samples.txt");
		signalDIFF.saveDFT("/home/ipserc/saco/dft_minus_conv_signal_dft.txt");

		*/
	}
}
