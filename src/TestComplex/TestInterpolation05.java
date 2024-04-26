/*
 * java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes TestComplex.TestPolynom01
 */
package TestComplex;

import java.util.function.ToIntFunction;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;
import com.panayotis.gnuplot.JavaPlot;

public class TestInterpolation05 {

	public static void showResults(Polynom aPolynom) {
		MatrixComplex hMatrix;
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "POLYNOMIAL ROOTS TEST"));
		System.out.println(Complex.boxTextRandom(boxSize, "Polynomial Coeficients"));
		System.out.println(aPolynom.toCoefs());
		System.out.println(Complex.boxTextRandom(boxSize, "Polynomial Representations"));
		//aPolynom.toPolynom();
		System.out.println(aPolynom.toPolynom("POLYNOM:"));
		System.out.println(aPolynom.toMaxima_poly("MAXIMA :"));
		System.out.println(aPolynom.toOctave_poly("OCTAVE :"));
		System.out.println(aPolynom.toWolfram_poly("WOLFRAM:"));
		System.out.println(Complex.boxTextRandom(boxSize, "Polynomial Roots"));
		System.out.println(aPolynom.toMaxima_roots("MAXIMA :"));
		System.out.println(aPolynom.toOctave_roots("OCTAVE :"));
		System.out.println(aPolynom.toWolfram_roots("WOLFRAM:"));
    	hMatrix = aPolynom.solve();
    	//hMatrix.println("Hay "+(aPolynom.complexMatrix[0].length-1)+" Soluciones");
    	hMatrix.quicksort(0);
    	hMatrix.println("There are "+(aPolynom.complexMatrix[0].length-1)+" roots");
		System.out.println(Complex.boxTextRandom(boxSize, "Roots Test"));
    	for (int i = 0; i < hMatrix.complexMatrix.length; ++i) {
    		System.out.println("f(" + hMatrix.complexMatrix[i][0] + ")=" + aPolynom.eval(hMatrix.complexMatrix[i][0]));
    	}
	}
	
	public static void plotResults(Polynom myPolynom, int rows, boolean allSamples, String interpolName)
	{
		int boxSize = 65;
		int degree = rows-1;
		Complex.printBoxTextRandom(boxSize, "Polynom Degree:" + degree);
		myPolynom.println(interpolName + " Interpolation Polynom");
		System.out.println(myPolynom.toPolynom());

		int samples = (rows-2)*20+1;
		int noSamples = allSamples ? 0 : (int)(rows*Math.log10(samples)*2);
		double[][] dataRe = new double[(int)samples][2];
		for (int i = 0; i < samples; ++i) {
			dataRe[i][0] = i / 20.0;
			if (i >= noSamples && i <= samples - noSamples )
				dataRe[i][1] = myPolynom.eval(dataRe[i][0]).rep();
			else dataRe[i][1] = 0;
			//System.out.println(dataRe[i][0] + "," + dataRe[i][1]);
		}
		JavaPlot p = new JavaPlot();
		//p.setTitle(myPolynom.toGNUPlot_poly());
		p.setTitle(interpolName + " Polynomial Degree:" + degree);
		p.addPlot(dataRe);
		p.set("zeroaxis", "");
		p.set("style","data lines");
		// p.set("xrange", "[-6:6]");
		p.set("grid","");
		p.plot();
	}


	public static void main(String[] args) {
		Polynom myPolynom = new Polynom();
		MatrixComplex points;
		int boxSize = 65;

		Complex.zero_threshold_exact(0);
		Complex.zero_threshold_approx(1E-40);
		Complex.setFormatON();
		Complex.exact(true);
		Complex.setScientificON(9);
		//Complex.setFixedON(5);

		points = new MatrixComplex(6,2);
		points.setItem(0, 0, Complex.ZERO);
		points.setItem(0, 1, Complex.ZERO);
		points.setItem(1, 0, new Complex(1));
		points.setItem(1, 1, new Complex(2));
		points.setItem(2, 0, new Complex(50));
		points.setItem(2, 1, new Complex(229));
		points.setItem(3, 0, new Complex(100));
		points.setItem(3, 1, new Complex(541));
		points.setItem(4, 0, new Complex(150));
		points.setItem(4, 1, new Complex(863));
		points.setItem(5, 0, new Complex(200));
		points.setItem(5, 1, new Complex(1223));

		myPolynom = myPolynom.interpolationNewton(points);
		myPolynom.println("Newton Interpolation Polynom");
		MatrixComplex table = myPolynom.walkInterval(points.getItem(0, 0), points.getItem(5, 0));
		myPolynom.plotRe(table, "");
		
		for (int i = 1; i < 100; ++i) {
			System.out.println("Prime("+i+"):"+Math.ceil(myPolynom.eval(i).rep()));
		}
	}
}
