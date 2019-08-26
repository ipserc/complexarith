package TestComplex;

import java.util.function.Function;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;
import com.panayotis.gnuplot.JavaPlot;

public class plotFunc4 {
	/**
	 * Plots the function expressed in GNUPlot format between loLimit and upLimit.
	 * @param GNUplotExpression The function to plot in GNUPlot format.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */
	public static void plot(String title, long samples, double dataRe[][], double dataIm[][]) {
		JavaPlot p = new JavaPlot();
		p.setTitle("Integral "+title);
		p.addPlot(dataRe);
		p.addPlot(dataIm);
		p.set("zeroaxis", "");
		p.set("style","data lines");
		//p.set("xrange", "[0:"+samples+"]");
		p.set("grid","");
		p.plot();
	}
	
	public static void plot(String title, long samples, MatrixComplex data) {
		double dataRe[][]  = new double[(int)samples+1][1];
		double dataIm[][]  = new double[(int)samples+1][1];

		for (int t = 0; t <= samples; ++t) {
			dataRe[t][0] = data.complexMatrix[t][0].rep();
			dataIm[t][0] = data.complexMatrix[t][0].imp();
		}
		plot(title, samples, dataRe, dataIm);
	}

	public static void main(String[] args) {
		int decPrec = 3;
		Complex.setScientificON(decPrec);
		//String dashes = "-".repeat(80);
		Complex lolimit = new Complex();
		Complex uplimit = new Complex();
		Complex point = new Complex();
		Complex point1 = new Complex();
		Complex point2 = new Complex();
		Complex midpoint = new Complex();
		Complex integral = new Complex();
		double stepRe;
		double stepIm;
		Function<Complex, Complex> func;
		func = z -> z.times(2);
		
		int samples = 100;
		MatrixComplex data = new MatrixComplex(samples+1, 1);
		lolimit.setComplex("-5+3i");
		uplimit.setComplex("5+3i");
		stepRe = uplimit.minus(lolimit).rep()/samples;
		stepIm = uplimit.minus(lolimit).imp()/samples;
		
		point1 = lolimit;
		point2.setComplexRec(point1.rep() + stepRe, point1.imp() + stepIm);
		Complex cte = new Complex(26,-35);
		integral = point1.times(2).plus(cte);
		for (int t = 0; t <= samples; ++t) {
			integral = integral.plus(Complex.integrate(point1, point2, func, decPrec));
			data.complexMatrix[t][0] = integral.copy();
			//data.complexMatrix[t][0] = Complex.integrate(point1, point2, func, decPrec);
			System.out.println("point1:" + point1.toString() + "  point2:" + point2.toString());
			System.out.println("step:" + t + " Data:" + data.complexMatrix[t][0].toString());
			midpoint.setComplexRec(point1.rep()+stepRe/2, point1.imp()+stepIm/2);
			System.out.println("CHECK midpoint:" + midpoint.toString() + " Valor:" + midpoint.power(2).toString());
			point1 = point2.copy();
			point2.setComplexRec(point1.rep() + stepRe, point1.imp() + stepIm);
		}
		plot("func = z -> z.times(2) (3e-6+3e-6i, 10+2i)", samples, data);

		point1 = lolimit;
		point = point1.copy();
		for (int t = 0; t <= samples; ++t) {			
			data.complexMatrix[t][0] = point.power(2);
			System.out.println("point:" + point.toString());			
			System.out.println("step:" + t + " Data:" + data.complexMatrix[t][0].toString());
			point.setComplexRec(point1.rep() + stepRe/2, point1.imp() + stepIm/2);
			point1.setComplexRec(point1.rep() + stepRe, point1.imp() + stepIm);
			
		}
		plot("z.power(2) (3e-6+3e-6i, 10+2i)", samples, data);
	}

}
