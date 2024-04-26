package TestComplex;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.panayotis.gnuplot.JavaPlot;

public class plotFunc05 {
	public static void plot(long samples, double dataRe[][], double dataIm[][]) {
		JavaPlot p = new JavaPlot();
		p.setTitle("PRUEBA");
		p.addPlot(dataRe);
		p.addPlot(dataIm);
		p.set("zeroaxis", "");
		p.set("style","data lines");
		//p.set("xrange", "[0:"+samples+"]");
		p.set("grid","");
		p.plot();
	}
	
	public static void plot(long samples,MatrixComplex data) {
		double dataRe[][]  = new double[(int)samples+1][1];
		double dataIm[][]  = new double[(int)samples+1][1];

		for (int t = 0; t <= samples; ++t) {
			dataRe[t][0] = data.complexMatrix[t][0].rep();
			dataIm[t][0] = data.complexMatrix[t][0].imp();
		}
		plot(samples, dataRe, dataIm);
	}

	public static Complex signalSin(double amplitude, double freq, double time) {
		Complex amp = new Complex(amplitude);
		return amp.times(Complex.exp(Complex.i.times(Complex.DOS_PI * freq * time)));
	}
	
	public static Complex signalSin(Complex amplitude, double freq, double time) {
		return amplitude.times(Complex.exp(Complex.i.times(Complex.DOS_PI * freq * time)));
	}

	public static Complex signalSin1(Complex amplitude, double freq, double time) {
		return amplitude.times(Complex.exp(Complex.DOS_PI * freq * time));
	}
	
	public static void main(String[] args) {
		double freq = 104.3;
		long period = 4;
		long samples = (long)freq*period;
		MatrixComplex data = new MatrixComplex((int)samples+1, 1);
		Complex amplitud = new Complex("1");
		
		for (int t = 0; t <= samples; ++t) {
			data.complexMatrix[t][0] = signalSin(amplitud, 1/freq, t);
			System.out.println("time:" + t + " Signal:" + data.complexMatrix[t][0].toString());
		}
		plot(samples, data);
	}

}
