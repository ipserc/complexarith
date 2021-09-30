package TestComplex;

import java.awt.image.SampleModel;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.signal.*;
import com.ipserc.arith.signal.Fourier.*;

public class TestFilter6 {

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
		//cos(z(70Hz+5sin(z mod 7)))
		//(50f-20)/159 f=64 --> 20
		return Complex.cos(z.times(Complex.PI*20)).plus(Complex.sin(z.times(Complex.PI*120)));
	}

	public static void main(String[] args) {
		double gain;
		double fInit;
		double bandwidth;
		double loLimit = -1;
		double upLimit = 1;
		int samplefreq = 9000;
		int frecSignal1 = 800;
		int frecSignal2 = 4000;
		double slope;
		Fourier signal1, signal2, signal3;
		Fourier filter = new Fourier();
		Fourier signalPROD;
		
		int boxSize = 65;

      	System.out.println(Complex.boxTitleRandom(boxSize, "FOURIER TRANSF FILTER TEST"));				
		/*****************
		 * SIGNAL SECTION
		 *****************/
		System.out.println(Complex.boxTextRandom(boxSize, "Signal Section"));
		boolean createSignal1 = true;
		if (createSignal1) {
			System.out.println("Creating signal...");
			//Fourier signal = new Fourier(z -> sin(z,2000), loLimit, upLimit);
			signal1 = new Fourier(z -> cos(z, frecSignal1), loLimit, upLimit);
			signal1.DFT(samplefreq);
		
			// SAVING DATA
			signal1.saveSamples("/home/ipserc/saco/signal_samples.txt");
			signal1.saveDFT("/home/ipserc/saco/signal_dft.txt");
		}
		else {
			signal1 = new Fourier("/home/ipserc/saco/signal_samples.txt");
			signal1.readDFT("/home/ipserc/saco/signal_dft.txt");
			loLimit = signal1.getLoLimit().rep();
			upLimit = signal1.getUpLimit().rep();
			samplefreq = signal1.getSampleFreq();			
		}
		System.out.println("SIGNAL 1 Plotting results...");		
		signal1.plotSamples("Original SIGNAL 1", true, e_lineStyle.IMPULSES);
		signal1.plotDFT("Original SIGNAL 1", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		boolean createSignal2 = true;
		if (createSignal2) {
			System.out.println("Creating signal...");
			//Fourier signal = new Fourier(z -> sin(z,2000), loLimit, upLimit);
			signal2 = new Fourier(z -> sin(z, frecSignal2), loLimit, upLimit);
			signal2.DFT(samplefreq);
		
			// SAVING DATA
			signal2.saveSamples("/home/ipserc/saco/signal_samples.txt");
			signal2.saveDFT("/home/ipserc/saco/signal_dft.txt");
		}
		else {
			signal2 = new Fourier("/home/ipserc/saco/signal_samples.txt");
			signal2.readDFT("/home/ipserc/saco/signal_dft.txt");
			loLimit = signal2.getLoLimit().rep();
			upLimit = signal2.getUpLimit().rep();
			samplefreq = signal2.getSampleFreq();			
		}

		// PLOTING RESULTS
		System.out.println("SIGNAL 2 Plotting results...");		
		signal2.plotSamples("Original SIGNAL 2", true, e_lineStyle.IMPULSES);
		signal2.plotDFT("Original SIGNAL 2", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		boolean createSignal3 = true;
		if (createSignal3) {
			System.out.println("Creating signal...");
			//Fourier signal = new Fourier(z -> sin(z,2000), loLimit, upLimit);
			signal3 = new Fourier(z -> cos(z, frecSignal1).times(sin(z, frecSignal2)), loLimit, upLimit);
			signal3.DFT(samplefreq);
		
			// SAVING DATA
			signal3.saveSamples("/home/ipserc/saco/signal_samples.txt");
			signal3.saveDFT("/home/ipserc/saco/signal_dft.txt");
		}
		else {
			signal3 = new Fourier("/home/ipserc/saco/signal_samples.txt");
			signal3.readDFT("/home/ipserc/saco/signal_dft.txt");
			loLimit = signal3.getLoLimit().rep();
			upLimit = signal3.getUpLimit().rep();
			samplefreq = signal3.getSampleFreq();			
		}

		// PLOTING RESULTS
		System.out.println("SIGNAL 3 Plotting results...");		
		signal3.plotSamples("Original SIGNAL 3", true, e_lineStyle.IMPULSES);
		signal3.plotDFT("Original SIGNAL 3", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

	}
}
