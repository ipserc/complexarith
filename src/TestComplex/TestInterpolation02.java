/*
 * java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes TestComplex.TestPolynom01
 */
package TestComplex;

import java.util.function.Function;
import java.util.function.ToIntFunction;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;
import com.panayotis.gnuplot.JavaPlot;

public class TestInterpolation02 {

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
	
	public static void plotResults(Polynom myPolynom, int rows, boolean allSamples)
	{
		int boxSize = 65;
		int degree = rows-2;
		Complex.printBoxTextRandom(boxSize, "Polynom Degree:" + degree);
		myPolynom.println("Newton Interpolation Polynom");
		System.out.println(myPolynom.toPolynom());

		int samples = (rows-2)*20+1;
		int noSamples = allSamples ? 0 : (int)(rows*Math.log10(samples)*1.5);
		double[][] dataRe = new double[(int)samples][2];
		for (int i = 0; i < samples; ++i) {
			dataRe[i][0] = i / 20.0;
			if (i >= noSamples && i <= samples - noSamples )
				dataRe[i][1] = myPolynom.eval(dataRe[i][0]).rep();
			else dataRe[i][1] = 0;
			System.out.println(dataRe[i][0] + "," + dataRe[i][1]);
		}
		System.out.println("Slope:" + ((dataRe[samples-noSamples-1][1]-dataRe[noSamples+1][1])/(dataRe[samples-noSamples-1][1]-dataRe[noSamples+1][0])));
		System.out.println("Point:" + dataRe[2*noSamples+1][0] + ":" + dataRe[2*noSamples+1][1]);
		JavaPlot p = new JavaPlot();
		//p.setTitle(myPolynom.toGNUPlot_poly());
		p.setTitle("Polynomial Degree:"+degree);
		p.addPlot(dataRe);
		p.set("zeroaxis", "");
		p.set("style","data lines");
		// p.set("xrange", "[-6:6]");
		p.set("grid","");
		p.plot();
	}

	public static void calculate(Polynom myPolynom, int rows) {
		int boxSize = 65;
		int degree = rows-2;
		Complex.printBoxTextRandom(boxSize, "Polynom Degree:" + degree);
		myPolynom.println("Newton Interpolation Polynom");
		System.out.println(myPolynom.toPolynom());
		int samples = (rows-2)*20+1;
		double[][] dataRe = new double[(int)samples][2];
		double[][] piRe = new double[(int)samples][2];
		for (int i = 0; i < samples; ++i) {
			dataRe[i][0] = i / 20.0;
			dataRe[i][1] = myPolynom.eval(dataRe[i][0]).rep();
			piRe[i][0] = dataRe[i][0];
			piRe[i][1] = pi3(piRe[i][0]);
			//System.out.println(dataRe[i][0] + "," + dataRe[i][1]);
		}
		JavaPlot p = new JavaPlot();
		//p.setTitle(myPolynom.toGNUPlot_poly());
		p.setTitle("Polynomial Degree:"+degree);
		//p.addPlot(dataRe);
		p.addPlot(piRe);
		p.set("zeroaxis", "");
		p.set("style","data lines");
		// p.set("xrange", "[-6:6]");
		p.set("grid","");
		p.plot();


	}

	public static double pi1(double x) {
		return x / Math.log(x);
	}
	
	public static double pi2(double x) {
		int decPrec = 4;
		Function<Complex, Complex> func;
		func = z -> Complex.log(z).inverse();
		return Complex.integrate(1+10e-3, x, func, decPrec).rep();
	}
	
	public static double pi3(double x) {
		return pi2(x) - Math.log(2);
	}
	
	public static void main(String[] args) {
		Polynom myPolynom = new Polynom();
		MatrixComplex points;

		Complex.setFormatON();
		Complex.setScientificON(6);
		//Complex.setFixedON(5);

		/* Grado 7
		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19");
		myPolynom = myPolynom.interpolationNewton(points);
		System.out.println("Some check points");
		point.setComplexRec(4, 0);
		myPolynom.eval(point).println(point.toString()+":");
		point.setComplexRec(8, 0);
		myPolynom.eval(point).println(point.toString()+":");
		point.setComplexRec(12, 0);
		myPolynom.eval(point).println(point.toString()+":");
		plotResults(myPolynom, points.rows(), true);
		*/
		
		/* Grado 11
		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;10,29;11,31;12,37");
		myPolynom = myPolynom.interpolationNewton(points);
		System.out.println("Some check points");
		point.setComplexRec(4, 0);
		myPolynom.eval(point).println(point.toString()+":");
		point.setComplexRec(8, 0);
		myPolynom.eval(point).println(point.toString()+":");
		point.setComplexRec(12, 0);
		myPolynom.eval(point).println(point.toString()+":");		
		plotResults(myPolynom, points.rows(), false);
		*/
		
		/* Grado 13
		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;"
				+ "10,29;11,31;12,37;13,41;14,43");
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points.rows(), false);
		*/
		
		/* Grado 19 */
		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;"
				+ "10,29;11,31;12,37;13,41;14,43;15,47;16,53;17,59;18,61;19,67;"
				+ "20,71");
		myPolynom = myPolynom.interpolationNewton(points);
		//plotResults(myPolynom, points.rows(), false);
		calculate(myPolynom, points.rows());
		/**/
		
		/* Grado 20 * /
		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;"
				+ "10,29;11,31;12,37;13,41;14,43;15,47;16,53;17,59;18,61;19,67;"
				+ "20,71;21,73");
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points.rows(), false);
		/**/
		
		/* Grado 21
		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;"
				+ "10,29;11,31;12,37;13,41;14,43;15,47;16,53;17,59;18,61;19,67;"
				+ "20,71;21,73;22,79");
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points.rows(), false);
		*/
		
		/* Grado 23
		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;"
				+ "10,29;11,31;12,37;13,41;14,43;15,47;16,53;17,59;18,61;19,67;"
				+ "20,71;21,73;22,79;23,83;24,89");
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points.rows(), false);
		*/
	}
}
