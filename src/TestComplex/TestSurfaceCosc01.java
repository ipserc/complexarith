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
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;

import java.awt.image.SampleModel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestSurfaceCosc01 {

	public static void plot(String title, double[][][] grid) {
		MatrixComplexPlot.plotGrid3DSync(title, grid);
	}

    public static void main(String[] args) {
    	int XY = 0, Z = 1;
		Complex s = new Complex();
		Complex z = new Complex();
		int boxSize = 65;
		int sampleBase = 110;
		int totalPoints = (sampleBase*sampleBase);
		double xlow = -1, xhigh = -0.001;
		double ylow = -1, yhigh = -0.001;
		double incx = (xhigh-xlow)/sampleBase;
		double incy = (yhigh-ylow)/sampleBase;
		double x, y;
		int k;
		Complex coordPlot[][] = new Complex[totalPoints][2];
		
		System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX COSC TEST"));
		Complex.setFormatON();
		Complex.setScientificON(8);
		Complex.precision(1E-18);
		Complex.facts();
		Complex.printFormatStatus();
		
		Complex.printBoxText(1, boxSize, "Calculating...");		
		double offset = 0;
		x = xlow+offset;
		k = 0;
		try {
			do {
				y = ylow+offset;
				do {
					s.setComplexRec(x, y);
					z = Complex.cosc(s);
					coordPlot[k][XY] = s.copy();
					coordPlot[k][Z] = z.copy();
					++k;
					y += incy;
				} while (y < yhigh);
				x += incx;
			} while (x < xhigh);
		} catch (Exception e) {
			System.err.println("Index k:"+k+" out of bounds:"+totalPoints);
		}
		
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
