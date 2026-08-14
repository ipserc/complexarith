 package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.*;
import com.ipserc.arith.signal.Fourier.*;

public class TestFilter04 {

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
		return Complex.cos(z.times(Math.PI*20)).plus(Complex.sin(z.times(Math.PI*120)));
	}

	public static void main(String[] args) {
		// Los ficheros de datos originales de este test (signal_samples.txt/signal_dft.txt,
		// dft_filt_signal_*.txt, convolution_*.txt, generados a mano en 2020 bajo
		// <user.home>/ipserc/saco/fourier_20201023_2012 y .../2013) no sobreviven -- están en
		// otra máquina, nunca se versionaron. En vez de eso, se genera todo aquí mismo en Java a
		// partir de func14()..func18() (definidas más arriba, hasta ahora sin ningún llamador) y
		// de la API de Fourier: constructor con Function<Complex,Complex>, DFT(), filter(),
		// convolution() -- los propios nombres de los ficheros perdidos ("dft_filt_signal",
		// "convolution") encajan exactamente con lo que filter()/convolution() calculan, así que
		// el diseño de abajo reconstruye la comparación que el test original hacía entre las 2
		// formas de aplicar un filtro (por multiplicación en frecuencia vía filter(), o por suma
		// en el dominio del tiempo vía convolution()) para 2 filtros distintos (paso bajo / paso
		// alto) sobre la misma señal original -- NO son la misma operación en esta implementación
		// concreta (verificado numéricamente, ver el comentario junto a "DFT-CONV" más abajo).
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

		// N=1024 (potencia de 2 -> camino FFT), periodo de 1s -> cada bin de la DFT vale
		// exactamente 1 Hz, así que fIni en lowPassFilter()/highPassFilter() se puede leer
		// directamente en Hz. func16() es la más baja en frecuencia (70Hz+5·sin(t mod 7)) de las
		// 5 disponibles -- la más cómoda de visualizar con un muestreo de 1024 puntos.
		int samplefreq = 1024;
		Complex lo = new Complex(0.0);
		Complex up = new Complex(1.0);

		signalOrig = new Fourier(TestFilter04::func16, lo, up);
		signalOrig.DFT(samplefreq);
		// PLOTING RESULTS
		System.out.println("Original Signal Plotting results...");
		signalOrig.plotSamplesSync("Original Signal (Time) ", true, e_lineStyle.IMPULSES);
		signalOrig.plotDFTSync("Original Signal Spectrum ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		// 2 filtros distintos (paso bajo / paso alto), cada uno su propio objeto Fourier:
		// lowPassFilter()/highPassFilter() rellenan a la vez transform (la máscara en frecuencia,
		// para filter()) y samples (la respuesta al impulso en tiempo, para convolution() -- vía
		// el IDFT() interno que ya hace slopeFilter()).
		Fourier filter1 = new Fourier(samplefreq, lo, up);
		filter1.lowPassFilter(1.0, 100, samplefreq);
		Fourier filter2 = new Fourier(samplefreq, lo, up);
		filter2.highPassFilter(1.0, 50, samplefreq);

		signalDFT1 = filter1.filter(signalOrig);
		// PLOTING RESULTS
		System.out.println("DFT1 Plotting results...");
		signalDFT1.plotSamplesSync("DFT1 (Time) ", true, e_lineStyle.IMPULSES);
		signalDFT1.plotDFTSync("DFT1 Spectrum ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		signalDFT2 = filter2.filter(signalOrig);
		// PLOTING RESULTS
		System.out.println("DFT2 Plotting results...");
		signalDFT2.plotSamplesSync("DFT2 (Time) ", true, e_lineStyle.IMPULSES);
		signalDFT2.plotDFTSync("DFT2 Spectrum ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		signalDFT = signalDFT1.plus(signalDFT2);
		// PLOTING RESULTS
		System.out.println("DFT1+DFT2 Plotting results...");
		signalDFT.plotSamplesSync("DFT1+DFT2 ", true, e_lineStyle.IMPULSES);
		signalDFT.plotDFTSync("DFT1+DFT2 ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		signalConv1 = filter1.convolution(signalOrig);
		// PLOTING RESULTS
		System.out.println("CONV1 Plotting results...");
		signalConv1.plotSamplesSync("CONV1 ", true, e_lineStyle.IMPULSES);
		signalConv1.plotDFTSync("CONV1 ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		signalConv2 = filter2.convolution(signalOrig);
		// PLOTING RESULTS
		System.out.println("CONV2 Plotting results...");
		signalConv2.plotSamplesSync("CONV2 ", true, e_lineStyle.IMPULSES);
		signalConv2.plotDFTSync("CONV2 ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		signalConv = signalConv1.plus(signalConv2);
		// PLOTING RESULTS
		System.out.println("CONV1+CONV2 Plotting results...");
		signalConv.plotSamplesSync("CONV1+CONV2 ", true, e_lineStyle.IMPULSES);
		signalConv.plotDFTSync("CONV1+CONV2 ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		// NO sale cerca de 0 (verificado numericamente, max|diff|~0.37 sobre 1024 muestras con
		// estos filtros) -- Fourier.filter() multiplica DFTs completas, que por el teorema de
		// convolucion de la DFT corresponde a convolucion CIRCULAR (periodica, con wraparound);
		// Fourier.convolution() (arriba) calcula una suma CAUSAL truncada en la ventana [0,N)
		// (corta en cuanto t-n<0, sin wraparound) -- convolucion LINEAL, no circular. Para un
		// filtro ideal paso bajo/alto (respuesta al impulso tipo sinc, ni corta ni causal) ambas
		// difieren de verdad, no solo por redondeo -- el clasico aviso de DSP de que "filtrar
		// multiplicando en frecuencia" y "convolucionar en tiempo" NO son intercambiables sin mas
		// salvo que la respuesta al impulso quepa causal y corta dentro de la ventana (o se rellene
		// con ceros para evitar el wraparound, tecnica que ninguno de los 2 metodos aplica aqui).
		signalDiff = signalDFT.minus(signalConv);
		// PLOTING RESULTS
		System.out.println("DFT-CONV Plotting results...");
		signalDiff.plotSamplesSync("DFT-CONV ", true, e_lineStyle.IMPULSES);
		signalDiff.plotDFTSync("DFT-CONV ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		signalDiff = signalOrig.minus(signalDFT);
		// PLOTING RESULTS
		System.out.println("ORIGINAL-DFT Plotting results...");
		signalDiff.plotSamplesSync("ORIGINAL-DFT ", true, e_lineStyle.IMPULSES);
		signalDiff.plotDFTSync("ORIGINAL-DFT ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);

		signalDiff = signalOrig.minus(signalConv);
		// PLOTING RESULTS
		System.out.println("ORIGINAL-CONV Plotting results...");
		signalDiff.plotSamplesSync("ORIGINAL-CONV ", true, e_lineStyle.IMPULSES);
		signalDiff.plotDFTSync("ORIGINAL-CONV ", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
	}
}
