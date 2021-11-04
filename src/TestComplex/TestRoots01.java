package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;
import com.ipserc.arith.complex.*;

public class TestRoots01 {

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
    	Polynom aPolynom;

    	Complex.setFixedON(3);
    	Complex.setFormatON();

    	//-x²+2ix+1
    	aPolynom = new Polynom("-1,2i,1");
    	showResults(aPolynom);

    	// X²+2ix-1
       	aPolynom = new Polynom("1,2i,-1");
    	showResults(aPolynom);
 
    	// X²+2x+3
       	aPolynom = new Polynom("1,2,3");
    	showResults(aPolynom);

    	// 3x²+2x+1
       	aPolynom = new Polynom("3,2,1");
    	showResults(aPolynom);

    	// -7x⁷+6x⁶+5x⁵+4x³-3x²+2x-1
       	aPolynom = new Polynom("-7,6,5,4,-3,2,-1");
       	System.out.println("f(" + 0 + ")=" + aPolynom.eval(0));
       	System.out.println("f(" + 1 + ")=" + aPolynom.eval(1));
       	System.out.println("f(" + 2 + ")=" + aPolynom.eval(2));
    	showResults(aPolynom);

    	// x³+2x²+3x+2
       	aPolynom = new Polynom("1,2,3,2");
    	showResults(aPolynom);

    	// -4ix⁶+(3+2i)x⁵+x⁴+(2-i)x³+3x²+2x-i
       	aPolynom = new Polynom("-4i,3+2i,1,2-i,3,2,-i");
    	showResults(aPolynom);

       	// 3x⁷-5x⁶+2x⁴-x³-x²+2x-6
       	aPolynom = new Polynom("3,-5,0,2,-1,-1,2,-6");
    	showResults(aPolynom);
/**/
    	// 4x⁴-3x³+2x²-x
       	aPolynom = new Polynom("4,-3,2,-1,0");
    	showResults(aPolynom);

    	
    	aPolynom = new Polynom("-2.0i,1.0i,1.0i,-3.0i,-2.0i,2.0i,-2.0i,-2.0i,-1.0i,-2.0i,2.0i,1.0i,2.0i,-3.0i,0.0,0.0,0.0");
       	//aPolynom = new MatrixComplex(1,17); aPolynom.initPolynomRandomImagInt(3);
       	System.out.println(aPolynom.toMatrixComplex());
    	showResults(aPolynom);       	
/**/
    	aPolynom = new Polynom(16); aPolynom.initMatrixRandomRecInt(9);
    	System.out.println(aPolynom.toMatrixComplex());
    	showResults(aPolynom);       	
    	
       	aPolynom = new Polynom("1,10,-30.49,-445.14,199.54,5727.08,-1890.73,-21585.78,6065.64,20638.8");
    	showResults(aPolynom);
    	
       	aPolynom = new Polynom("1,10,30.49,445.14,199.54,5727.08,1890.73,21585.78,6065.64,20638.8");
    	showResults(aPolynom);
    	
    	aPolynom = new Polynom("-3+2i,-8+5i,5+4i,2-5i,9+6i,-8-3i,-2-2i,-3+7i,-5+1i,7+5i,6+8i,-1+2i,-8-5i,3-5i,-7-2i,1");
    	System.out.println(aPolynom.toMatrixComplex());
    	showResults(aPolynom);       	

      	aPolynom = new Polynom("1,-7+8i, 4+4i,-8+9i,-7+7i,-3-6i, 1-8i, 5-7i,-9-8i, 8+7i, 3+8i, 6+8i, 6+8i,-3-3i,2-i,-6-6i");
    	showResults(aPolynom);

      	aPolynom = new Polynom("1, 7+8i, 4-4i,-8+9i,-7+7i,-3-6i, 1-8i, 5-7i,-9-8i, 8+7i, 3+8i, 6+8i, 6+8i,-3-3i,2-i,-6-6i");
    	showResults(aPolynom);

    	aPolynom = new Polynom(16); aPolynom.initMatrixRandomRecInt(1);
    	showResults(aPolynom); 
    	
    	aPolynom = new Polynom(8); aPolynom.initMatrixRandomRecInt(9);
    	showResults(aPolynom); 

    	aPolynom = aPolynom.hermite(9);
    	showResults(aPolynom); 

    	aPolynom = aPolynom.chebyshev(9, 4);
    	showResults(aPolynom); 
    	
    	aPolynom = aPolynom.chebyshev(16, 1);
    	showResults(aPolynom); 

/**/
	}

}
