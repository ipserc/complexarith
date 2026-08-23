package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.*;
import com.ipserc.arith.signal.Fourier.*;

/**
 * Variante de {@link TestFilter02} para probar los métodos nuevos de análisis espectral de
 * {@link Fourier}: {@link Fourier#getSpectralComponents()}, {@link
 * Fourier#getSpectralComponents(double)} y {@link Fourier#plotSpectralComponentsSync(String,
 * SpectralComponent[], e_lineStyle)}/{@link Fourier#plotSpectralComponentsAsync(String,
 * SpectralComponent[], e_lineStyle)}. Misma señal y mismo primer filtro que {@link TestFilter02},
 * para poder comparar directamente el espectro completo (plotDFTAsync, ya existente) contra las
 * componentes extraídas (getSpectralComponents), antes y después de filtrar.
 */
public class TestFilter02a {

	private static Complex func15(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Math.PI*(800+50*(Math.sin(z.rep()%13)))));
	}

	private static void printComponents(String label, SpectralComponent[] components, int maxPrinted) {
		System.out.println(label + " (" + components.length + " componentes en total, mostrando las " + Math.min(maxPrinted, components.length) + " de mayor amplitud):");
		for (int i = 0; i < components.length && i < maxPrinted; ++i) {
			System.out.println("  " + components[i]);
		}
		if (components.length > maxPrinted) {
			System.out.println("  ... (" + (components.length - maxPrinted) + " más)");
		}
	}

	public TestFilter02a() {
	}

	public static void main(String[] args) {
		double gain;
		double fInit;
		double bandwidth;
		String filterparams;
		Fourier filter;
		Fourier signalFiltered;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "FOURIER SPECTRAL COMPONENTS TEST"));

		/*****************
		 * SIGNAL SECTION
		 *****************/
		System.out.println(Complex.boxTextRandom(boxSize, "Signal Section"));
		double loLimit = -80;
		double upLimit = 80;
		int samplefreq = 16384;
		System.out.println("Creating signal...");
		Fourier signal = new Fourier(z -> func15(z), loLimit, upLimit);
		signal.DFT(samplefreq);

		// PLOTING RESULTS -- espectro completo (ya existente) para comparar visualmente
		System.out.println("SIGNAL Plotting results...");
		signal.plotSamplesAsync("Original SIGNAL", true, e_lineStyle.LINES);
		signal.plotDFTAsync("Original signal", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		// NUEVO: componentes espectrales significativas (umbral relativo al pico esperado del filtro)
		double threshold = 100.0;
		SpectralComponent[] allComponents = signal.getSpectralComponents();
		SpectralComponent[] peaks = signal.getSpectralComponents(threshold);
		System.out.println("\nTotal de bins del DFT: " + allComponents.length);
		printComponents("Componentes significativas de la señal original (umbral " + threshold + ")", peaks, 10);
		signal.plotSpectralComponentsAsync("Componentes espectrales - Señal original", peaks, e_lineStyle.IMPULSES);

		/***********************
		 * FILTER SECTION
		 ***********************/
		System.out.println(Complex.boxTextRandom(boxSize, "Filter Section"));
		System.out.println("Creating filter...");
		gain = 5;
		fInit = 0;
		bandwidth = 2000;
		filterparams = "G:" + gain + " Fc:" + (fInit+bandwidth/2) + " Hz Bw:" + bandwidth + " Hz";
		filter = new Fourier();
		filter.bandPassFilter(gain, fInit, bandwidth, samplefreq);

		System.out.println("Processing signal with filter...");
		signalFiltered = filter.filter(signal);

		// PLOTING RESULTS -- espectro completo (ya existente)
		System.out.println("Plotting results...");
		signalFiltered.plotDFTSync("Filtered signal " + filterparams, e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		// NUEVO: componentes espectrales significativas de la señal filtrada -- deberían quedar
		// solo dentro de la banda de paso [fInit, fInit+bandwidth] (y su espejo negativo)
		SpectralComponent[] filteredPeaks = signalFiltered.getSpectralComponents(threshold);
		printComponents("Componentes significativas tras el filtro " + filterparams, filteredPeaks, 10);
		signalFiltered.plotSpectralComponentsSync("Componentes espectrales - Señal filtrada " + filterparams, filteredPeaks, e_lineStyle.IMPULSES);

		boolean allInBand = true;
		for (SpectralComponent c : filteredPeaks) {
			double absFreq = Math.abs(c.frequency);
			if (absFreq < fInit || absFreq > fInit + bandwidth) allInBand = false;
		}
		System.out.println("\nTodas las componentes filtradas caen dentro de la banda de paso: " + allInBand);
	}
}
