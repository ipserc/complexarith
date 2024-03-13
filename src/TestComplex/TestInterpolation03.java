/*
 * java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes TestComplex.TestPolynom01
 */
package TestComplex;

import java.util.function.ToIntFunction;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;
import com.panayotis.gnuplot.JavaPlot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestInterpolation03 {
	static double[][] puntos = new double[1000][2];
	static int sampler = 20;

	
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
	
	public static void mergePoints(double[][] dataRe) {
		int p, d;
		boolean salir = false;
		for (p = 0; p < puntos.length; ++p) if (puntos[p][0] == dataRe[0][0]) break;

		for (d = 0; d < dataRe.length; ++d) {
			if (dataRe[d][1] == 0.0 && salir ) break;
			//puntos[p][0] = dataRe[d][0];
			if (puntos[p][1] != 0.0 && dataRe[d][1] != 0.0) {
				salir = true;
				puntos[p][1] = (puntos[p][1]+dataRe[d][1])/2;
			}
			else if (puntos[p][1] == 0.0 && dataRe[d][1] != 0.0) {
				puntos[p][1] = dataRe[d][1];
			}
			++p;
		}
		d=d;
	}

	private static void plotGraph(double[][] dataRe, String Title) {
		JavaPlot p = new JavaPlot();
		p.setTitle(Title);
		p.addPlot(dataRe);
		p.set("zeroaxis", "");
		p.set("style","data lines");
		p.set("grid","");
		p.plot();
	}
	
	public static void plotResults(Polynom myPolynom, MatrixComplex points, boolean allSamples)
	{
		int boxSize = 65;
		int degree = points.rows()-1;
		Complex.printBoxTextRandom(boxSize, "Polynom Degree:" + degree);
		myPolynom.println("Newton Interpolation Polynom");
		System.out.println(myPolynom.toPolynom());
		Complex.printBoxTextRandom(boxSize, "Check interpolation points");
		for (int i = 0; i < points.rows(); ++i) {
			myPolynom.eval(points.getItem(i, 0)).println(points.getItem(i, 0).toString()+":");
		}
		
		Complex.printBoxTextRandom(boxSize, "Plot Polynomial");
		int samples = (points.rows()-1)*sampler+1;
		int noSamples = allSamples ? 0 : (int)(points.rows()*Math.log10(samples));
		double[][] dataRe = new double[(int)samples][2];
		double p0 = points.getItem(0, 0).rep();
		for (int i = 0; i < samples; ++i) {
			dataRe[i][0] = i / (double)sampler + p0;
			if (i > noSamples && i < samples - noSamples )
				dataRe[i][1] = myPolynom.eval(dataRe[i][0]).rep();
			else dataRe[i][1] = 0;
			System.out.println(dataRe[i][0] + "," + dataRe[i][1]);
		}
		mergePoints(dataRe);
		plotGraph(dataRe, "Polynomial Degree:"+degree);		
	}

	private static String getSet(int start, int points) {
		int pairLen = 8;
		String pares = 	"  0,  1;  1,  2;  2,  3;  3,  5;  4,  7;  5, 11;  6, 13;  7, 17;  8, 19;  9, 23;"
					+ 	" 10, 29; 11, 31; 12, 37; 13, 41; 14, 43; 15, 47; 16, 53; 17, 59; 18, 61; 19, 67;"
					+ 	" 20, 71; 21, 73; 22, 79; 23, 83; 24, 89; 25, 97; 26,101; 27,103; 28,107; 29,109;"
					+ 	" 30,113; 31,127; 32,131; 33,137; 34,139; 35,149; 36,151; 37,157; 38,163; 39,167;"
					+ 	" 40,173; 41,179; 42,181; 43,191; 44,193; 45,197; 46,199; 47,211; 48,223; 49,227 ";
		
		int ini, end;
		int backupPoints = 2;
		
		ini = start > backupPoints ? (start-backupPoints)*pairLen : start*pairLen;
		end = (start+points+backupPoints)*pairLen;
		if (end > pares.length()) end = pares.length();

		return pares.substring(ini, end-1);
	}

	private static void initPuntos() {
		for (int i = 0; i < puntos.length; ++i) {
			puntos[i][0] = i/(double)sampler;
			puntos[i][1] = 0.0;
		}
	}
	
	
	public static void main(String[] args) {
		Polynom myPolynom = new Polynom();
		MatrixComplex points;
		int boxSize = 65;
		String range;
		Complex.setFormatON();
		Complex.setScientificON(3);
		//Complex.setFixedON(5);

		initPuntos();
		Complex.printBoxTitleRandom(boxSize, " * 1 * ");
		//points = new MatrixComplex(" 0, 1; 1, 2; 2, 3; 3, 5; 4, 7; 5,11; 6,13; 7,17; 8,19; 9,23;10,29;11,31;12,37;13,41;14,43");
		range = getSet(0, 5);
		System.out.println(range);
		points = new MatrixComplex(range);
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points, false);

		Complex.printBoxTitleRandom(boxSize, " * 2 * ");
		//points = new MatrixComplex("10,29;11,31;12,37;13,41;14,43;15,47;16,53;17,59;18,61;19,67;20,71;21,73");
		range = getSet(5, 3);
		System.out.println(range);
		points = new MatrixComplex(range);
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points, false);

		Complex.printBoxTitleRandom(boxSize, " * 3 * ");
		//points = new MatrixComplex("17,59;18,61;19,67;20,71;21,73;22,79;23,83;24,89;25,97;26,101");
		range = getSet(8, 3);
		System.out.println(range);
		points = new MatrixComplex(range);
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points, false);

		Complex.printBoxTitleRandom(boxSize, " * 4 * ");
		//points = new MatrixComplex("23,83;24,89;25,97;26,101;27,103;28,107;29,109;30,113;31,127;32,131;33,137");
		range = getSet(11, 3);
		System.out.println(range);
		points = new MatrixComplex(range);
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points, false);

		Complex.printBoxTitleRandom(boxSize, " * 5 * ");
		//points = new MatrixComplex("29,109;30,113;31,127;32,131;33,137;34,139;35,149;36,151;37,157;38,163");
		range = getSet(14, 3);
		System.out.println(range);
		points = new MatrixComplex(range);
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points, false);

		Complex.printBoxTitleRandom(boxSize, " * 6 * ");
		//points = new MatrixComplex("33,137;34,139;35,149;36,151;37,157;38,163;39,167;40,173;41,179;42,181");
		range = getSet(17, 3);
		System.out.println(range);
		points = new MatrixComplex(range);
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points, false);

		Complex.printBoxTitleRandom(boxSize, " * 7 * ");
		range = getSet(20, 3);
		System.out.println(range);
		points = new MatrixComplex(range);
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points, false);

		Complex.printBoxTitleRandom(boxSize, " * 8 * ");
		range = getSet(23, 3);
		System.out.println(range);
		points = new MatrixComplex(range);
		myPolynom = myPolynom.interpolationNewton(points);
		plotResults(myPolynom, points, false);
		
		plotGraph(puntos, "Gráfica total");
	}
}
