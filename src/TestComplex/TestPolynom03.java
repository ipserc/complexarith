/*
 * java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes TestComplex.TestPolynom01
 */
package TestComplex;

import java.util.ArrayList;
import java.util.List;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;

public class TestPolynom03 {

	public static void main(String[] args) {
		Polynom myPolynom;		
		int boxSize = 65;
		List<MatrixComplex> pointsList;
		int limCinf = 1;
		int limCsup = 7;
		int degree;

		Complex.setFormatON();
		Complex.setScientificON(5);
		//Complex.setFixedON(5);

    	System.out.println(Complex.boxTitle1(boxSize, "CHEBYSHEV POLYNOMIALS"));
    	myPolynom = new Polynom();

    	System.out.println(Complex.boxTextRandom(boxSize, "Chebyshev Order 1 Polynomials"));
    	pointsList = new ArrayList<MatrixComplex>();
    	for (int i = limCinf; i <= limCsup; ++i) {
    		myPolynom = myPolynom.chebyshev1(i);
    		myPolynom.println("chebyshev 1 grado " + i);
    		MatrixComplex points = myPolynom.walkInterval(-1, 1);
    		pointsList.add(points);
    	}
    	
    	myPolynom.plotRe(pointsList, "Chebyshev 1 Figures");
       	pointsList = new ArrayList<MatrixComplex>();   	
    	System.out.println(Complex.boxTextRandom(boxSize, "Chebyshev Order 2 Polynomials"));
    	pointsList = new ArrayList<MatrixComplex>();
    	for (int i = limCinf; i <= limCsup; ++i) {
    		myPolynom = myPolynom.chebyshev2(i);
    		myPolynom.println("chebyshev 2 grado " + i);
    		MatrixComplex points = myPolynom.walkInterval(-1, 1);
    		pointsList.add(points);
    	}
    	myPolynom.plotRe(pointsList, "Chebyshev 2 Figures");

    	System.out.println(Complex.boxTextRandom(boxSize, "Chebyshev Order 3 Polynomials"));
       	pointsList = new ArrayList<MatrixComplex>();
    	for (int i = limCinf; i <= limCsup; ++i) {
    		myPolynom = myPolynom.chebyshev(i,3);
    		myPolynom.println("chebyshev 3 grado " + i);
    		MatrixComplex points = myPolynom.walkInterval(-1, 1);
    		pointsList.add(points);
    	}
    	myPolynom.plotRe(pointsList, "Chebyshev 3 Figures");
    	
    	degree = 7;
    	System.out.println(Complex.boxTextRandom(boxSize, "Chebyshev Order 1 degree "+degree+" values comparision"));
    	myPolynom = myPolynom.chebyshev1(degree);
    	myPolynom.println("Chebyshev Order 1 degree "+degree+" Polynomial:");
    	Complex cx = new Complex("-3+5i");
    	Complex inc = new Complex("0.2+.5i");
    	for (int i = 0 ; i < 10; ++i ) {
	    	myPolynom.eval(cx).println("myPolynom.eval   (   "+cx.toString()+"): ");
	    	Complex.chebyshev(degree, cx).println(	"Complex.chebyshev("+degree+", "+cx.toString()+"): ");
	    	cx = cx.plus(inc);
    	}
    	
    	System.out.println(Complex.boxTextRandom(boxSize, "Normalized Chebyshev Order 1 Polynomials"));
    	pointsList = new ArrayList<MatrixComplex>();
    	for (int i = limCinf; i <= limCsup; ++i) {
    		myPolynom = myPolynom.chebyshev1(i).normalize();
    		myPolynom.println("chebyshev 1 grado " + i);
    		MatrixComplex points = myPolynom.walkInterval(-1, 1);
    		pointsList.add(points);
    	}
    	myPolynom.plotRe(pointsList, "Monic Chebyshev 1 Figures");

	}
}
