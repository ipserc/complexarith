package TestComplex;

import java.util.function.Function;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.panayotis.gnuplot.JavaPlot;

public class plotFunc07 {
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
	
	public static Complex func41(double x) {
		return func1(x).times(func2(x));
	}

	public static Complex func42(double x) {
		return Complex.sqrt(func1(x).times(func2(x)).power(2));
	}

	public static Complex func43(double x) {
		return new Complex((func1(x).times(func2(x))).rep(),0);
	}

	public static Complex func5(double x) {
		Complex X = new Complex(x,0);
		return Complex.cos(X).power(x);
	}

	public static Complex func6(double x) {
		Complex X = new Complex(x,0);
		return Complex.sin(X).power(x);
	}

	public static Complex func7(double x) {
		return func5(x).minus(func6(x));
	}

	public static Complex func8(double x) {
		return func5(x).plus(func6(x));
	}

	public static Complex func9(double x) {
		Complex X = new Complex(x,0);
		return Complex.log(Complex.sin(X).power(x));
	}
	
	public static Complex func10(double x) {
		Complex X = new Complex(x,0);
		return ((X.power(2)).minus(9)).divides(X.minus(3));
	}
                          
	public static void main(String[] args) {
		long samples = 600000;
		double start = -30;
		double end = 30;
		
		Complex.setFormatON();
		Complex.setFixedON(3);
		/* * /
		doPlot(x -> func1(x), start, end, samples, "f1(X):X.power(Complex.sin(X))");
		doPlot(x -> func2(x), start, end, samples, "f2(X):X.power(Complex.cos(X))");
		doPlot(x -> func3(x), start, end, samples, "f1(X)-f2(X)");
		doPlot(x -> func4(x), start, end, samples, "f1(X)+f2(X)");
		/* */
		
		/* * /
		doPlot(x -> func41(x), start, end, samples, "f1(X)*f2(X)");
		doPlot(x -> func42(x), start, end, samples, "Complex.sqrt(func1(x).times(func2(x)).power(2))");
		doPlot(x -> func43(x), start, end, samples, "Re(f1(X)*f2(X))");
		/* */

		/* * /
		doPlot(x -> func5(x), -0, 80, samples, "Complex.sin(X).power(x)");
		doPlot(x -> func6(x), -0, 80, samples, "Complex.cos(X).power(x)");
		doPlot(x -> func7(x), -0, 80, samples, "func5(x).minus(func6(x)");
		doPlot(x -> func8(x), -0, 80, samples, "func5(x).plus(func6(x)");
		/* */
		//doPlot(x -> func9(x), start, end, samples, "Complex.log(Complex.sin(X).power(x));");
		
		doPlot(x -> func10(x), start, end, samples, "Complex.log(X)");

	}

}
