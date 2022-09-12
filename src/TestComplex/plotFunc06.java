package TestComplex;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.panayotis.gnuplot.JavaPlot;

public class plotFunc06 {
	/**
	 * Plots the function expressed in GNUPlot format between loLimit and upLimit.
	 * @param GNUplotExpression The function to plot in GNUPlot format.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public static void plot(long samples, double dataRe[][], double dataIm[][]) {
		JavaPlot p = new JavaPlot();
		p.setTitle("PRUEBA");
		p.addPlot(dataRe);
		p.addPlot(dataIm);
		p.set("zeroaxis", "");
		p.set("style","data lines");
		// p.set("xrange", "[-6:6]");
		p.set("grid","");
		p.plot();
	}
	
	public static void plot(long samples,MatrixComplex data) {
		double dataRe[][]  = new double[(int)samples+1][2];
		double dataIm[][]  = new double[(int)samples+1][2];

		for (int t = 0; t <= samples; ++t) {
			dataRe[t][0] = data.complexMatrix[t][0].rep();
			dataIm[t][0] = data.complexMatrix[t][0].rep();
			dataRe[t][1] = data.complexMatrix[t][1].rep();
			dataIm[t][1] = data.complexMatrix[t][1].imp();
		}
		plot(samples, dataRe, dataIm);
	}

	public static Complex func1(double x) {
		Complex X = new Complex(x,0);
		return X.power(Complex.sin(X));
	}

	public static Complex func2(double x) {
		Complex X = new Complex(x,0);
		return X.power(Complex.cos(X));
	}

	public static Complex func3(double x) {
		return func1(x).minus(func2(x));
	}
	
	public static Complex func4(double x) {
		return func1(x).plus(func2(x));
	}

	public static Complex func5(double x) {
		Complex X = new Complex(x,0);
		return Complex.sin(X).power(X);
	}
	
	public static Complex func6(double x) {
		Complex X = new Complex(x,0);
		return Complex.cos(X).power(X);
	}

	public static void main(String[] args) {
		long samples = 30000;
		double start = 0;
		double end = 40;
		double step = 1.0*(end-start)/(samples+1);
		MatrixComplex data = new MatrixComplex((int)samples+1, 2);

		Complex.setFormatON();
		Complex.setFixedON(3);
		for (int t = 0; t <= samples; ++t) {
			data.complexMatrix[t][0] = new Complex(start+step*t);
			data.complexMatrix[t][1] = func6(start+step*t);
			System.out.println("X:" + data.complexMatrix[t][0].toString() + " Y:" + data.complexMatrix[t][1].toString());
		}
		plot(samples, data);
	}

}
