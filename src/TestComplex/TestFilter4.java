 package TestComplex;

import java.awt.image.SampleModel;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.*;
import com.ipserc.arith.signal.Fourier.*;

public class TestFilter4 {

	private static Complex sin(Complex z, int frec) {
		return Complex.sin(z.times(Complex.PI*frec));
	}

	private static Complex cos(Complex z, int frec) {
		return Complex.cos(z.times(Complex.PI*frec));
	}
	

	private static Complex func14(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Complex.PI*(7000+50*(Math.sin(Math.ceil(z.rep()%13))))));
	}

	private static Complex func15(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Complex.PI*(2300+50*(Math.sin(z.rep()%13)))));
	}

	private static Complex func16(Complex z) {
		//cos(z(70Hz+5sin(z mod 7)))
		return Complex.cos(z.times(Complex.PI*(70+5*(Math.sin(z.rep()%7)))));
	}

	private static Complex func17(Complex z) {
		//cos(z(70Hz+5sin(z mod 7)))
		return Complex.cos(z.times(Complex.PI*70)).times(Complex.sin(z.times(Complex.PI*120)));
	}

	private static Complex func18(Complex z) {
		return Complex.cos(z.times(Complex.PI*20)).plus(Complex.sin(z.times(Complex.PI*120)));
	}

	public static void main(String[] args) {
		Fourier signalDFT1, signalDFT2, signalDFT;
		Fourier signalConv1, signalConv2, signalConv;
		Fourier signalDiff;
		Fourier signalOrig;
		int boxSize = 65;
		
      	System.out.println(Complex.boxTitleRandom(boxSize, "FOURIER TRANSF FILTER TEST"));		
		/*****************
		 * SIGNAL SECTION
		 *****************/
		System.out.println(Complex.boxTextRandom(boxSize, "Signal Section"));
		signalOrig = new Fourier("/home/ipserc/saco/fourier_20201023_2013/signal_samples.txt");
		signalOrig.readDFT("/home/ipserc/saco/fourier_20201023_2013/signal_dft.txt");
		// PLOTING RESULTS
		System.out.println("Original Signal Plotting results...");		
		signalOrig.plotSamples("Original Signal (Time) ", true, e_lineStyle.IMPULSES);
		signalOrig.plotDFT("Original Signal Spectrum ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);	
		
		signalDFT1 = new Fourier("/home/ipserc/saco/fourier_20201023_2012/dft_filt_signal_samples.txt");
		signalDFT1.readDFT("/home/ipserc/saco/fourier_20201023_2012/dft_filt_signal_dft.txt");
		// PLOTING RESULTS
		System.out.println("DFT1 Plotting results...");		
		signalDFT1.plotSamples("DFT1 (Time) ", true, e_lineStyle.IMPULSES);
		signalDFT1.plotDFT("DFT1 Spectrum ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);	
		
		signalDFT2 = new Fourier("/home/ipserc/saco/fourier_20201023_2013/dft_filt_signal_samples.txt");
		signalDFT2.readDFT("/home/ipserc/saco/fourier_20201023_2013/dft_filt_signal_dft.txt");
		// PLOTING RESULTS
		System.out.println("DFT2 Plotting results...");		
		signalDFT2.plotSamples("DFT2 (Time) ", true, e_lineStyle.IMPULSES);
		signalDFT2.plotDFT("DFT2 Spectrum ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);	

		signalDFT = signalDFT1.plus(signalDFT2);
		// PLOTING RESULTS
		System.out.println("DFT1+DFT2 Plotting results...");		
		signalDFT.plotSamples("DFT1+DFT2 ", true, e_lineStyle.IMPULSES);
		signalDFT.plotDFT("DFT1+DFT2 ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		signalConv1 = new Fourier("/home/ipserc/saco/fourier_20201023_2012/convolution_samples.txt");
		signalConv1.readDFT("/home/ipserc/saco/fourier_20201023_2012/convolution_dft.txt");
		// PLOTING RESULTS
		System.out.println("CONV1 Plotting results...");		
		signalConv1.plotSamples("CONV1 ", true, e_lineStyle.IMPULSES);
		signalConv1.plotDFT("CONV1 ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		
		signalConv2 = new Fourier("/home/ipserc/saco/fourier_20201023_2013/convolution_samples.txt");
		signalConv2.readDFT("/home/ipserc/saco/fourier_20201023_2013/convolution_dft.txt");
		// PLOTING RESULTS
		System.out.println("CONV2 Plotting results...");		
		signalConv2.plotSamples("CONV2 ", true, e_lineStyle.IMPULSES);
		signalConv2.plotDFT("CONV2 ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		signalConv = signalConv1.plus(signalConv2);
		// PLOTING RESULTS
		System.out.println("CONV1+CONV2 Plotting results...");		
		signalConv.plotSamples("CONV1+CONV2 ", true, e_lineStyle.IMPULSES);
		signalConv.plotDFT("CONV1+CONV2 ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		
		signalDiff = signalDFT.minus(signalConv);
		// PLOTING RESULTS
		System.out.println("DFT-CONV Plotting results...");		
		signalDiff.plotSamples("DFT-CONV ", true, e_lineStyle.IMPULSES);
		signalDiff.plotDFT("DFT-CONV ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		//signalDiff.saveSamples("/home/ipserc/saco/diff_samples.txt");

		signalDiff = signalOrig.minus(signalDFT);
		// PLOTING RESULTS
		System.out.println("ORIGINAL-DFT Plotting results...");		
		signalDiff.plotSamples("ORIGINAL-DFT ", true, e_lineStyle.IMPULSES);
		signalDiff.plotDFT("ORIGINAL-DFT ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		
		signalDiff = signalOrig.minus(signalConv);
		// PLOTING RESULTS
		System.out.println("ORIGINAL-CONV Plotting results...");		
		signalDiff.plotSamples("ORIGINAL-CONV ", true, e_lineStyle.IMPULSES);
		signalDiff.plotDFT("ORIGINAL-CONV ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
	}
}
