/******************************************************************************
 *  Compilation:  javac TestComplex.java
 *  Execution:    java TestComplex
 *
 *  Tests for arith.Complex.
 *	
 *  
 *  
 *  
 *  
 *  
 *  
 *
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *
 ******************************************************************************/
package TestComplex;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;



public class TestSurfacePolyn01 {

	public static void plot(String title, double[][][] grid) {
		MatrixComplexPlot.plotGrid3DSync(title, grid);
	}

	public static Complex func(Complex z) {
		// return z.power(4).plus(z.power(3).times(3)).minus(z.power(2).times(7)).minus(z.times(5)).plus(-3);
		return Complex.log(z);
		// return z.divides(z.minus(new Complex(1,1)));
	}
	
    public static void main(String[] args) {
    	int XY = 0, Z = 1;
		Complex s = new Complex();
		Complex z = new Complex();
		int boxSize = 65;
		int sampleBase = 110;
		int totalPoints = (sampleBase*sampleBase);
		double xlow = -4.5, xhigh = 2.5;
		double ylow = -4.5, yhigh = 2.5;
		double incx = (xhigh-xlow)/sampleBase;
		double incy = (yhigh-ylow)/sampleBase;
		double x, y;
		int k;
		Complex coordPlot[][] = new Complex[totalPoints][2];
		
		System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX SINC TEST"));
		Complex.setFormatON();
		Complex.setScientificON(8);
		Complex.precision(1E-18);
		Complex.facts();
		Complex.printFormatStatus();
		
		Complex.printBoxText(1, boxSize, "Calculating...");		
		double offset = 0; //Complex.precision();
		x = xlow+offset;
		k = 0;
		do {
			y = ylow+offset;
			do {
				s.setComplexRec(x, y);
				z = func(s);
				coordPlot[k][XY] = s.copy();
				coordPlot[k][Z] = z.copy();
				++k;
				y += incy;
			} while (y < yhigh && k < totalPoints);
			x += incx;
		} while (x < xhigh && k < totalPoints);
		
		Complex.printBoxText(2, boxSize, "Distributing...");
		// Reshaped from the flat, k-indexed coordPlot into a proper [row][col] grid instead of a
		// flat point list: the calculation loop above sweeps ALL of y (sampleBase iterations) for
		// each x before moving to the next x, so row = i/sampleBase (x index), col = i%sampleBase
		// (y index) recovers the exact grid the data was already computed in. A real connected
		// surface mesh (MatrixComplexPlot.plotGrid3DSync()) needs that structure -- the flat list
		// this used to build only supported a disconnected point cloud, never a real surface.
	    double[][][] pointsRe = new double[sampleBase][sampleBase][3];
	    double[][][] pointsIm = new double[sampleBase][sampleBase][3];
	    for (int i = 0; i < k; ++i) {
	    	int row = i / sampleBase, col = i % sampleBase;
    		pointsRe[row][col][0] = coordPlot[i][XY].rep();
    		pointsRe[row][col][1] = coordPlot[i][XY].imp();
    		pointsRe[row][col][2] = coordPlot[i][Z].rep();
    		pointsIm[row][col][0] = coordPlot[i][XY].rep();
    		pointsIm[row][col][1] = coordPlot[i][XY].imp();
    		pointsIm[row][col][2] = coordPlot[i][Z].imp();
    	}

		Complex.printBoxText(3, boxSize, "Plotting...");
		plot("Re(Z)", pointsRe);
		plot("Im(Z)", pointsIm);
    }
}
