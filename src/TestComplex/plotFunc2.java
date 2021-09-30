package TestComplex;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.panayotis.gnuplot.JavaPlot;

public class plotFunc2 {
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
			dataRe[t][1] = data.complexMatrix[t][1].rep();
			dataIm[t][0] = data.complexMatrix[t][0].rep();
			dataIm[t][1] = data.complexMatrix[t][1].imp();
		}
		plot(samples, dataRe, dataIm);
	}

	public static Complex func1(double x) {
		Polynom numerPol = new Polynom(); 
		Polynom denomPol = new Polynom();
		
		numerPol = numerPol.fromRoots("-3, -1, 4, 6, 7");
		denomPol = denomPol.fromRoots("-4, -2, 1, 3");
		return numerPol.eval(x).divides(denomPol.eval(x));
	}

	public static void main(String[] args) {
		long samples = 30000;
		int start = -7;
		int end = 7;
		double step = 1.0*(end-start)/(samples+1);
		MatrixComplex data = new MatrixComplex((int)samples+1, 2);

		Polynom numerPol = new Polynom(); 
		Polynom denomPol = new Polynom();		
		numerPol = numerPol.fromRoots("-3, -1, 4, 6");
		denomPol = denomPol.fromRoots("-4, -1, 1, 3,5");

		Complex.setFormatON();
		Complex.setFixedON(3);
		for (int t = 0; t <= samples; ++t) {
			data.complexMatrix[t][0] = new Complex(start+step*t);
			data.complexMatrix[t][1] = numerPol.eval(start+step*t).divides(denomPol.eval(start+step*t));
			System.out.println("X:" + data.complexMatrix[t][0].toString() + " Y:" + data.complexMatrix[t][1].toString());
		}
		plot(samples, data);
	}

}
