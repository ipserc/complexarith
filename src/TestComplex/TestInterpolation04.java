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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestInterpolation04 {
	
	private static Complex sinc(Complex z) {
		if (z.isZero()) return Complex.ONE;
		else return Complex.sin(z).divides(z);
	}

	private static Complex sinz(Complex z) {
		return Complex.sin(z).times(z);
	}

	private static Complex zSinCos(Complex z) {
		// z*sinz(z)*cos(z)
		return z.times(Complex.sin(z).times(Complex.cos(z)));
	}

	private static Complex expSin(Complex z) {
		// z*sinz(z)*cos(z)
		return  Complex.exp(Complex.sin(z));
	}

	public static void main(String[] args) {
		int boxSize = 65;
		
		Complex.setFormatON();
		Complex.exact(false); // Use this to make all the operations with the default accuracy of the processor
		Complex.setScientificON(8);
		//Complex.setFixedON(5);
		
		Polynom.sampleBase = 1000;
		int numSamples = Polynom.sampleBase;
		int degree = 5;
		int numPoints = 23; 
		Complex lolimit = new Complex("-7");
		Complex uplimit = new Complex("7");
		MatrixComplex points = new MatrixComplex(numPoints,2);
		
		/*
		 * FUNCTION
		 */
		Function<Complex, Complex> func;
		func = z -> zSinCos(z);
		
		List<MatrixComplex> samplesList = new ArrayList<MatrixComplex>();
		MatrixComplex samples;
		
		/*
		 * FUNTION & SAMPLES
		 */
		Complex vector = uplimit.minus(lolimit);
		Complex inc = vector.divides(numSamples);
		samples = new MatrixComplex(numSamples, 2);
		for (int i = 0; i < numSamples; ++i) {
			Complex newX = lolimit.plus(inc.times(i));
			//newX.println("newX("+i+"):");
			samples.setItem(i, 0, newX);
			samples.setItem(i, 1, func.apply(newX));
		}
		samplesList.add(samples);
		
		/*
		 * INTERPOLATION POINTS
		 */
		boolean chebyNodes = true;
		if (chebyNodes) {
			Complex v1 = (lolimit.minus(uplimit)).divides(2);
			Complex v2 = (lolimit.plus(uplimit)).divides(2);
			Complex xhat = new Complex();
			double factor = Complex.PI/(2*(numPoints));
			points.setItem(0, 0, lolimit);
			points.setItem(0, 1, func.apply(lolimit));
			for(int i = 1; i < numPoints-1; ++i) {
				xhat = Complex.cos(new Complex((2*i+1)*factor));
				Complex newX =  xhat.times(v1).plus(v2);
				//newX.println("newX("+i+"):");
				points.setItem(i, 0, newX);
				points.setItem(i, 1, func.apply(newX));
			}
			points.setItem(numPoints-1, 0, uplimit);
			points.setItem(numPoints-1, 1, func.apply(uplimit));
		}
		else {
			inc = vector.divides(numPoints-1);
			for(int i = 0; i < numPoints; ++i) {
				Complex newX = lolimit.plus(inc.times(i));
				//newX.println("newX("+i+"):");
				points.setItem(i, 0, newX);
				points.setItem(i, 1, func.apply(newX));
			}
		}
		Complex.printBoxTextRandom(boxSize, "INTERPOLATION " + numPoints + " NODES - " + (chebyNodes ? "CHEBYSHEV" : "EQUIDISTANT"));
		points.println();
		
		/*
		 * NEWTON INTERPOLATION POLYNOMIAL
		 */
		Complex.printBoxTextRandom(boxSize, "NEWTON INTERPOLATION POLYNOMIAL");
		Polynom polyNewton = new Polynom(degree);
		polyNewton = polyNewton.interpolationNewton(points);
		polyNewton.println();
		samples = polyNewton.walkInterval(lolimit, uplimit);
		samplesList.add(samples);

		/*
		 * CUBIC SPLINE INTERPOLATION
		 */
		Complex.printBoxTextRandom(boxSize, "CUBIC SPLINE INTERPOLATION");
		Spline polySpline = new Spline(3, points);
		polySpline.interpolate(false);
		polySpline.print();
		samples = polySpline.walkInterval(lolimit, uplimit);
		samplesList.add(samples);

		polyNewton.plotRe(samplesList, "INTERPOLATION " + numPoints + " NODES - " + (chebyNodes ? "CHEBYSHEV" : "EQUIDISTANT"));
		
	}
}
