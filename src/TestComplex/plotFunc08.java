package TestComplex;

import java.util.function.Function;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.panayotis.gnuplot.JavaPlot;

public class plotFunc08 {
	/**
	 * Plots the function expressed in GNUPlot format between loLimit and upLimit.
	 * @param GNUplotExpression The function to plot in GNUPlot format.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public static void plot(long samples, double dataRe[][], double dataIm[][], String Title) {
		JavaPlot p = new JavaPlot();
		p.setTitle(Title);
		p.addPlot(dataRe);
		p.addPlot(dataIm);
		p.set("zeroaxis", "");
		p.set("style","data lines");
		// p.set("xrange", "[-6:6]");
		p.set("grid","");
		p.plot();
	}
	
	public static void doPlot(Function <Double,Complex> func, double start, double end, long samples, String Title) {
		double dataRe[][]  = new double[(int)samples+1][2];
		double dataIm[][]  = new double[(int)samples+1][2];
		double step = 1.0*(end-start)/(samples+1);
		MatrixComplex data = new MatrixComplex((int)samples+1, 2);
		
		for (int t = 0; t <= samples; ++t) {
			data.complexMatrix[t][0] = new Complex(start+step*t);
			data.complexMatrix[t][1] = func.apply(start+step*t);
			//System.out.println("X:" + data.complexMatrix[t][0].toString() + " Y:" + data.complexMatrix[t][1].toString());
		}
		
		for (int t = 0; t <= samples; ++t) {
			dataRe[t][0] = data.complexMatrix[t][0].rep();
			dataIm[t][0] = data.complexMatrix[t][0].rep();
			dataRe[t][1] = data.complexMatrix[t][1].rep();
			dataIm[t][1] = data.complexMatrix[t][1].imp();
		}
		plot(samples, dataRe, dataIm, Title);
	}

	public static Complex func1(double x) {
		Complex X = new Complex(x,0);
		Polynom polynom = new Polynom("-7.87500E+00,0.00000E+00,4.75000E+00,0.00000E+00,5.25000E-01,0.00000E+00");
		return polynom.eval(X);
	}

	public static Complex func2(double x) {
		Complex X = new Complex(0,x);
		Polynom polynom = new Polynom("-7.87500E+00,0.00000E+00,4.75000E+00,0.00000E+00,5.25000E-01,0.00000E+00");
		return polynom.eval(X);
	}

	public static Complex func3(double x) {
		Complex f3 = new Complex((func1(x).rep()*func2(x).imp()), 0);
		return f3;
	}
                          
	public static void main(String[] args) {
		long samples = 600000;
		double start = -1;
		double end = 1;
		
		Complex.setFormatON();
		Complex.setFixedON(3);
		doPlot(x -> func1(x), start, end, samples, "f1(X):(-7.87500E+00)X^5+(4.75000E+00)X^3+(5.25000E-01)X");
		doPlot(x -> func2(x), start, end, samples, "f2(X):(-7.87500E+00)X^5+(4.75000E+00)X^3+(5.25000E-01)X");
		doPlot(x -> func3(x), start, end, samples, "f3(X):(-7.87500E+00)X^5+(4.75000E+00)X^3+(5.25000E-01)X");

	}

}
