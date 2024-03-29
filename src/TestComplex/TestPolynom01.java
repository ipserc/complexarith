/*
 * java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes TestComplex.TestPolynom01
 */
package TestComplex;

import java.util.ArrayList;
import java.util.List;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;

public class TestPolynom01 {

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
		MatrixComplex points;
    	List<MatrixComplex> pointsList = new ArrayList<MatrixComplex>();

		Complex.setFormatON();
		Complex.setFixedON(3);
		
		//(-1.000)x^3+(27.000)x^2+(-243.000)x+(729.000)
    	myPolynom = new Polynom("-1,27,-243,729");
    	showResults(myPolynom);
    	
    	//-x²+2ix+1
    	myPolynom = new Polynom("-1,2i,1");
    	showResults(myPolynom);

    	// x²+2ix-1
    	myPolynom = new Polynom("1,2i,-1");
    	showResults(myPolynom);
 
    	// x²-2x+1
    	myPolynom = myPolynom.fromRoots("1,1");
    	showResults(myPolynom);

    	// X²+2x+3
    	myPolynom = new Polynom("1,2,3");
    	showResults(myPolynom);

    	// 3x²+2x+1
    	myPolynom = new Polynom("3,2,1");
    	showResults(myPolynom);

    	// 2x³+3x²+2x+1
    	myPolynom = new Polynom("-7-i,6+3i,5-5i,4-3i,-3i,2+7i,-1+3i");
    	for (int i = -10; i < 11; ++i)
    		System.out.println("f(" + i + ")=" + myPolynom.eval(i));
    	showResults(myPolynom);
    	//myPolynom.plot(-5, 5);

    	// x³+2x²+3x+2
    	myPolynom = new Polynom("1,2,3,2");
    	showResults(myPolynom);

    	// -4ix⁶+(3+2i)x⁵+x⁴+(2-i)x³+3x²+2x-i
    	myPolynom = new Polynom("-4i,3+2i,1,2-i,3,2,-i");
    	showResults(myPolynom);

       	// 3x⁷-5x⁶+2x⁵-x⁴-x³+2x-6
    	myPolynom = new Polynom("3,-5,0,2,-1,-1,2,-6");
    	showResults(myPolynom);

    	// 4x⁴-3x³+2x²-x
    	myPolynom = new Polynom("4,-3,2,-1,0");
    	showResults(myPolynom);
    	
    	myPolynom = new Polynom("-2.0i,1.0i,1.0i,-3.0i,-2.0i,2.0i,-2.0i,-2.0i,-1.0i,-2.0i,2.0i,1.0i,2.0i,-3.0i,0.0,0.0,0.0");
       	//myPolynom = new MatrixComplex(1,17); myPolynom.initPolynomRandomImagInt(3);
    	showResults(myPolynom);       	
       	System.out.println(myPolynom.toMatrixComplex());

    	myPolynom = new Polynom(16); myPolynom.initMatrixRandomRecInt(9);	//initRandomRecInt(9);
    	System.out.println(myPolynom.toMatrixComplex());
    	showResults(myPolynom);       	
    	
    	myPolynom = new Polynom("1,10,-30.49,-445.14,199.54,5727.08,-1890.73,-21585.78,6065.64,20638.8");
    	points = myPolynom.walkInterval(-7, 5);
    	pointsList.add(points);
    	myPolynom.plotRe(pointsList, "");
    	showResults(myPolynom);
    	
    	myPolynom = new Polynom("1,10,30.49,445.14,199.54,5727.08,1890.73,21585.78,6065.64,20638.8");
    	showResults(myPolynom);
    	
    	myPolynom = new Polynom("-3+2i,-8+5i,5+4i,2-5i,9+6i,-8-3i,-2-2i,-3+7i,-5+1i,7+5i,6+8i,-1+2i,-8-5i,3-5i,-7-2i,1");
    	points = myPolynom.walkInterval(-1, 1);
    	myPolynom.plotRe(pointsList, "");
    	System.out.println(myPolynom.toMatrixComplex());
    	showResults(myPolynom);       	

    	myPolynom = new Polynom("1,-7+8i,4+4i,-8+9i,-7+7i,-3-6i,1-8i,5-7i,-9-8i,8+7i,3+8i,6+8i,6+8i,-3-3i,2-i,-6-6i");
    	showResults(myPolynom);

    	myPolynom = new Polynom("1,-7+8i,4+4i,-8+9i,-7+7i,-3-6i,1-8i,5-7i,-9-8i,8+7i,3+8i,6+8i,6+8i,-3-3i,2-i,-6-6i");
    	showResults(myPolynom);

    	myPolynom = myPolynom.fromRoots("-7,-6,-5,-4,-3,-2,-1,+1,+2,+3,+4,+5,+6,+7");
    	showResults(myPolynom);

    	myPolynom = myPolynom.fromRoots("3-2i,2,5,4+i");
    	showResults(myPolynom);

    	myPolynom = new Polynom(16); myPolynom.initMatrixRandomRecInt(1);	
    	showResults(myPolynom); 
    	
    	myPolynom = new Polynom(8); myPolynom.initMatrixRandomInteger(9);	//initRandomInteger(9);
    	showResults(myPolynom);

    	myPolynom = new Polynom("1,-2,3,-2");
    	showResults(myPolynom);
    	
    	myPolynom = new Polynom("-1,-3,4");
    	showResults(myPolynom);

    	System.out.println(Complex.boxTitle1(boxSize, "REMARKABLE POLYNOMIALS"));
    	myPolynom = new Polynom();

    	myPolynom = myPolynom.chebyshev1(5);
    	myPolynom.println("chebyshev 1 grado 5");
    	points = myPolynom.walkInterval(-1, 1);
    	pointsList.add(points);
    	myPolynom.plotRe(pointsList, "");
    	showResults(myPolynom);

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
    	
    	myPolynom = myPolynom.legendre(5);
    	myPolynom.println("legendre grado 5");

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

		System.out.println(Complex.boxTitleRandom(boxSize, "POLYNOMIAL OPERATIONS TEST"));
    	Polynom polynomA = new Polynom("2,0,-3,0,4,-5");
    	Polynom polynomB = new Polynom("2,5,-1");
    	System.out.println("polynomA grado:" + polynomA.degree());
    	polynomA.println("polynomA");
    	polynomB.println("polynomB");    	
    	Polynom resultPolynom = polynomA.plus(polynomB);
    	resultPolynom.println("Suma");
    	resultPolynom = polynomA.minus(polynomB);
    	resultPolynom.println("Resta");
    	Polynom product = polynomA.times(polynomB);
    	product.println("Producto");

		System.out.println(Complex.boxTitleRandom(boxSize, "POLYNOMIAL OPERATIONS TEST"));
    	Polynom dividend = new Polynom("2,0,-3,0,4,-5");
    	Polynom divisor = new Polynom("2,5,-1");
    	Polynom quotient = new Polynom(dividend.degree());
    	quotient = dividend.divides(divisor);
    	dividend.println("dividend");
    	divisor.println("divisor");
    	quotient.println("quotient");
    	quotient.getRemainder().println("remainder");
    	
		System.out.println(Complex.boxTitleRandom(boxSize, "POLYNOMIAL OPERATIONS TEST"));
    	dividend = new Polynom("-3,0,4,2,0,6,2,0,-3,0,4,-5,0");
    	divisor = new Polynom("2,5,-1,0");
    	quotient = new Polynom(dividend.degree());
    	quotient = dividend.divides(divisor);
    	dividend.println("dividend");
    	divisor.println("divisor");
    	quotient.println("quotient");
    	quotient.getRemainder().println("remainder");
    	resultPolynom = divisor.times(quotient).plus(quotient.getRemainder());
    	resultPolynom.print("El dividendo");

	}
}
