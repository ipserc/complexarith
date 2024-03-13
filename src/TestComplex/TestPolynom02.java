/*
 * java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes TestComplex.TestPolynom01
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;

public class TestPolynom02 {

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

	public static void main(String[] args) {
		Polynom myPolynom;		
		int boxSize = 65;

		Complex.setFormatON();
		Complex.setScientificON(5);
		//Complex.setFixedON(5);

    	System.out.println(Complex.boxTitle1(boxSize, "REMARKABLE POLYNOMIALS"));
    	myPolynom = new Polynom();

    	myPolynom = myPolynom.chebyshev1(5);
    	myPolynom.println("chebyshev 1 grado 5");

    	myPolynom = myPolynom.chebyshev2(5);
    	myPolynom.println("chebyshev 2 grado 5");

    	myPolynom = myPolynom.chebyshev(5, 3);
    	myPolynom.println("chebyshev 3 grado 5");

    	myPolynom = myPolynom.chebyshev(5, 4);
    	myPolynom.println("chebyshev 4 grado 5");

    	myPolynom = myPolynom.chebyshev(5, 5);
    	myPolynom.println("chebyshev 5 grado 5");
    	
    	myPolynom = myPolynom.chebyshev(5, 6);
    	myPolynom.println("chebyshev 6 grado 5");
    	
    	myPolynom = myPolynom.hermite(5);
    	myPolynom.println("hermite grado 5");

    	myPolynom = myPolynom.hermiteI(5);
    	myPolynom.println("hermiteI grado 5");

    	myPolynom = myPolynom.legendre(5);
    	myPolynom.println("legendre grado 5");

    	myPolynom = myPolynom.legendreI(5);
    	myPolynom.println("legendreI grado 5");

    	myPolynom = myPolynom.laguerre(5, 0);
    	myPolynom.println("laguerre 0 grado 5");

    	myPolynom = myPolynom.laguerre(5, 1);
    	myPolynom.println("laguerre 1 grado 5");

    	myPolynom = myPolynom.laguerre(5, 2);
    	myPolynom.println("laguerre 2 grado 5");

    	myPolynom = myPolynom.laguerre(5, 3);
    	myPolynom.println("laguerre 3 grado 5");

    	myPolynom = myPolynom.laguerre(5, 4);
    	myPolynom.println("laguerre 4 grado 5");

    	myPolynom = myPolynom.laguerre(5, 5);
    	myPolynom.println("laguerre 5 grado 5");

    	myPolynom = myPolynom.chebyshev1(5).divides(myPolynom.legendre(5));
    	myPolynom.println("chebyshev1(5) / legendre(5)");

    	myPolynom = myPolynom.chebyshev2(5).divides(myPolynom.legendre(5));
    	myPolynom.println("chebyshev2(5) / legendre(5)");

    	myPolynom = myPolynom.hermiteI(5);
    	myPolynom.println("hermiteI grado 5");
    	showResults(myPolynom);

    	myPolynom = myPolynom.hermite(5);
    	myPolynom.println("hermite grado 5");
    	showResults(myPolynom);
    	
    	myPolynom = myPolynom.legendreI(5);
    	myPolynom.println("legendreI grado 5");
    	showResults(myPolynom);

    	myPolynom = myPolynom.legendre(5);
    	myPolynom.println("legendre grado 5");
    	showResults(myPolynom);

    	myPolynom = myPolynom.laguerre(5, 5);
    	myPolynom.println("laguerre 5 grado 5");
    	showResults(myPolynom);

    	Complex.printBoxTitleRandom(boxSize, "MORE INTERESTING POLYNOMIALS");
    	myPolynom = myPolynom.legendreI(2);
    	myPolynom.println("legendereI 2");
    	myPolynom = myPolynom.legendreI(3);
    	myPolynom.println("legendereI 3");

    	myPolynom = myPolynom.hermiteI(2);
    	myPolynom.println("hermiteI 2");
    	myPolynom = myPolynom.hermiteI(3);
    	myPolynom.println("hermiteI 3");

    	myPolynom = myPolynom.fromRoots("-5,-3,3,5");
    	myPolynom.println("fromRoots(\"-5,-3,3,5\")");

    	myPolynom = myPolynom.laguerre(3);
    	myPolynom.println("laguerre grado 3");
    	
	}
}
