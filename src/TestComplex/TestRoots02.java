package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;

import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import com.ipserc.arith.complex.*;

public class TestRoots02 {

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

		aPolynom.plotExpression(-2, 4);
		System.out.println(Complex.boxTextRandom(boxSize, "Roots - DETERMINISTIC"));
    	hMatrix = aPolynom.solve();
    	//hMatrix.println("Hay "+(aPolynom.complexMatrix[0].length-1)+" Soluciones");
    	hMatrix.quicksort(0);
    	hMatrix.println("There are "+(aPolynom.complexMatrix[0].length-1)+" roots");
		System.out.println(Complex.boxTextRandom(boxSize, "Roots Test - DETERMINISTIC"));
    	for (int i = 0; i < hMatrix.complexMatrix.length; ++i) {
    		System.out.println("f(" + hMatrix.complexMatrix[i][0] + ")=" + aPolynom.eval(hMatrix.complexMatrix[i][0]));
    	}

		System.out.println(Complex.boxTextRandom(boxSize, "Roots - STATISTIC"));
    	MatrixComplex sMatrix = aPolynom.solve(Polynom.e_rootCalcMode.STATISTIC);
    	sMatrix.quicksort(0);
    	sMatrix.println("There are "+(aPolynom.complexMatrix[0].length-1)+" roots");
		System.out.println(Complex.boxTextRandom(boxSize, "Roots Test - STATISTIC"));
    	for (int i = 0; i < sMatrix.complexMatrix.length; ++i) {
    		System.out.println("f(" + sMatrix.complexMatrix[i][0] + ")=" + aPolynom.eval(sMatrix.complexMatrix[i][0]));
    	}
	}

	public static void main(String[] args) {
    	Polynom aPolynom = new Polynom("1");
    	Polynom rootPoly;
     	
		Complex.setFormatON();
		Complex.setFixedOFF();
		Complex.setScientificON(9);
		Complex.facts();
		Complex.printFormatStatus();

    	//(-1.000)x^3+(5.000)x^2+(-3.000)x+(-9.000)
		
    	rootPoly = new Polynom("1, -1.12131415");
		rootPoly = rootPoly.power(4);
		aPolynom = aPolynom.times(rootPoly);

		rootPoly = new Polynom("1, -3");
		rootPoly = rootPoly.power(1);
		aPolynom = aPolynom.times(rootPoly);
		
		rootPoly = new Polynom("1, 1");
		rootPoly = rootPoly.power(3);
		aPolynom = aPolynom.times(rootPoly);


    	showResults(aPolynom);
    	
    	System.out.println("f(1.12131415)=" + aPolynom.eval(1.12131415));
    	System.out.println("f(1.1213141)=" + aPolynom.eval(1.1213141));
    	System.out.println("f(1.12)=" + aPolynom.eval(1.12));
    	System.out.println("f(1.1)=" + aPolynom.eval(1.1));

	}

}
