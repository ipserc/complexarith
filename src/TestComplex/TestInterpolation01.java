/*
 * java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes TestComplex.TestPolynom01
 */
package TestComplex;

import java.util.function.ToIntFunction;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;
import com.panayotis.gnuplot.JavaPlot;

public class TestInterpolation01 {

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
		Complex point = new Complex();
		int boxSize = 65;

		Complex.setFormatON();
		Complex.setScientificON(6);
		//Complex.setFixedON(5);

		/*
		points = new MatrixComplex("1,6;2,9;3,2;4,5;6,3;8,-1");
		myPolynom = myPolynom.interpolationNewton(points);
		myPolynom.println("Newton Interpolation Polynom");
		plotResults(myPolynom, points.rows(), true);
		
		points = new MatrixComplex("1,.6;2,.9;3,.2;4,.5;6,.3;8,-.1");
		myPolynom = myPolynom.interpolationNewton(points);
		myPolynom.println("Newton Interpolation Polynom");
		plotResults(myPolynom, points.rows(), true);

		points = new MatrixComplex("1,2;2,0;3,2");
		myPolynom = myPolynom.interpolationNewton(points);
		myPolynom.println("Newton Interpolation Polynom");
		plotResults(myPolynom, points.rows(), true);

		points = new MatrixComplex("1,2+2i;2,-i;3,-2+2i;4,-3-4i");
		myPolynom = myPolynom.interpolationNewton(points);
		myPolynom.println("Newton Interpolation Polynom");
		System.out.println(myPolynom.toPolynom());
		plotResults(myPolynom, points.rows(), true);

		points = new MatrixComplex("1-i,2+2i;2+3i,-i;3-3i,-2+2i;4,-3-4i");
		myPolynom = myPolynom.interpolationNewton(points);
		myPolynom.println("Newton Interpolation Polynom");
		System.out.println(myPolynom.toPolynom());
		point.setComplexRec(1, -1);
		myPolynom.eval(point).println(point.toString()+":");
		point.setComplexRec(4, 0);
		myPolynom.eval(point).println(point.toString()+":");

		/* * /
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
		
		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;"
				+ "10,29;11,31;12,37;13,41;14,43");
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points.rows(), false);

		
		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;"
				+ "10,29;11,31;12,37;13,41;14,43;15,47;16,53;17,59;18,61;19,67;"
				+ "20,71");
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points.rows(), false);

		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;"
				+ "10,29;11,31;12,37;13,41;14,43;15,47;16,53;17,59;18,61;19,67;"
				+ "20,71;21,73");
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points.rows(), false);

		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;"
				+ "10,29;11,31;12,37;13,41;14,43;15,47;16,53;17,59;18,61;19,67;"
				+ "20,71;21,73;22,79");
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points.rows(), false);

		points = new MatrixComplex("0,1;1,2;2,3;3,5;4,7;5,11;6,13;7,17;8,19;9,23;"
				+ "10,29;11,31;12,37;13,41;14,43;15,47;16,53;17,59;18,61;19,67;"
				+ "20,71;21,73;22,79;23,83;24,89");
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points.rows(), false);
		/ * */

		//points = new MatrixComplex("-2, 1.25; -0.5, 0.6; 0.5, 1; 1, 2; 2, 0; 4, -2; 6, -5.25; 7, -1.75; 8.8, 0.75; 10, 2");
		//points = new MatrixComplex("2.3,6.38512;2.7,13.6218;2.9,18.676;3.2,28.2599;3.5,40.4082;3.7,49.9945"); // Temperatura en función de la presión.
		
		points = new MatrixComplex("1,6;2,9;3,2;4,5;6,3;8,-1");

		myPolynom = myPolynom.interpolationNewton(points);
		myPolynom.println("Newton Interpolation Polynom");
		myPolynom.plotExpression(points.getItem(0, 0).rep(), points.getItem(points.rows()-1, 0).rep());
		
		myPolynom = myPolynom.interpolationLagrange(points);
		myPolynom.println("Lagrange Interpolation Polynom");
		myPolynom.plotExpression(points.getItem(0, 0).rep(), points.getItem(points.rows()-1, 0).rep());

		myPolynom = myPolynom.interpolationVandermonde(points, false);
		myPolynom.println("Vandermonde Interpolation Polynom");
		myPolynom.plotExpression(points.getItem(0, 0).rep(), points.getItem(points.rows()-1, 0).rep());
	}
}
