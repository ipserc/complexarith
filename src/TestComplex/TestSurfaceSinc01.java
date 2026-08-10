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

public class TestSurfaceSinc01 {

	public static void plot(String title, double[][][] grid) {
		MatrixComplexPlot.plotGrid3DAsync(title, grid);
	}

    public static void main(String[] args) {
    	int XY = 0, Z = 1;
		Complex s = new Complex();
		Complex z = new Complex();
		int boxSize = 65;
		int sampleBase = 161;
		int totalPoints = (sampleBase+1)*(sampleBase+1);
		double xlow = -6, xhigh = 6;
		double ylow = -6, yhigh = 6;
		double incx = (xhigh-xlow)/sampleBase;
		double incy = (yhigh-ylow)/sampleBase;
		double x, y;
		int k;
		Complex coordPlot[][] = new Complex[totalPoints][2];
		
		System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX SINC TEST"));
		Complex.setFormatON();
		Complex.setScientificON(8);
		Complex.setFixedON(4);
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
				// System.out.printf("x: %8.4f , y: %8.4f - %d%n", x, y, k);
				s.setComplexRec(x, y);
				s.println("punto s:");
				z = Complex.sinc(s);
				coordPlot[k][XY] = s.copy();
				coordPlot[k][Z] = z.copy();
				++k;
				y += incy;
			} while (y < yhigh); // k < totalPoints);
			x += incx;
		} while (x < xhigh && k < totalPoints);
		
		Complex.printBoxText(2, boxSize, "Distributing...");
		// Reshaped from the flat, k-indexed coordPlot into a proper [row][col] grid instead of a
		// flat point list: the calculation loop above sweeps ALL of y (sampleBase iterations) for
		// each x before moving to the next x, so row = i/sampleBase (x index), col = i%sampleBase
		// (y index) recovers the exact grid the data was already computed in. A real connected
		// surface mesh (MatrixComplexPlot.plotGrid3DSync()) needs that structure -- the flat list
		// this used to build only supported a disconnected point cloud, never a real surface.
	    double[][][] pointsRe = new double[sampleBase+2][sampleBase+2][3];
	    double[][][] pointsIm = new double[sampleBase+2][sampleBase+2][3];
	    for (int i = 0; i < k - 1; ++i) {
	    	int row = i / sampleBase, col = i % sampleBase;
			//System.out.printf("row: %4d , col: %4d - %d:%d%n", row, col, i, k);
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
