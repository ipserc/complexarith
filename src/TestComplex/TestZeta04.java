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

public class TestZeta04 {

	public static void plot(String title, double[][][] grid) {
		MatrixComplexPlot.plotGrid3DSync(title, grid);
	}

    public static void main(String[] args) {
    	int XY = 0, Z = 1;
		Complex s = new Complex();
		Complex z = new Complex();
		int boxSize = 65;
		int sampleBase = 25;
		double xlow = 2.5, xhigh = 5;
		double ylow = -25, yhigh = 25;
		double incx = (xhigh-xlow)/sampleBase;
		double incy = (yhigh-ylow)/sampleBase;
		double x, y;
		int i, j;
		//MatrixComplex coord = new MatrixComplex(1,2);
		Complex coordPlot[][][] = new Complex[sampleBase][sampleBase][2];
		//List<MatrixComplex> pointsList = new ArrayList<MatrixComplex>();
		//List<double[][][]> pointsListRe = new ArrayList<double[][][]>();
		//List<double[][][]> pointsListIm = new ArrayList<double[][][]>();
		
		System.out.println(Complex.boxTitleRandom(boxSize, "COMPLEX RIEMANN ZETA TEST"));
		Complex.setFormatON();
		Complex.setScientificON(8);
		Complex.precision(1E-18);
		Complex.facts();
		Complex.printFormatStatus();
		
		double offset = 1e-6;
		x = xlow+offset;
		i = 0;
		do {
			y = ylow+offset;
			j = 0;
			do {
				s.setComplexRec(x, y);
				z = Complex.zeta(s);
//				coord.setCol(XY, new Complex(x,y));
//				coord.setCol(Z, z);
				coordPlot[i][j][XY] = s.copy();
				coordPlot[i][j][Z] = z.copy();			
				y += incy;
				++j;
			} while (y <= yhigh);
			x += incx;
			++i;
		} while (x <= xhigh);
		
	    double[][][] pointsRe = new double[sampleBase][sampleBase][3];
	    double[][][] pointsIm = new double[sampleBase][sampleBase][3];
	    for (j = 0; j < sampleBase; ++j)
	    	for (i = 0; i < sampleBase; ++i) {
	    		pointsRe[i][j][0] = coordPlot[i][j][XY].rep();
	    		pointsRe[i][j][1] = coordPlot[i][j][XY].imp();
	    		pointsRe[i][j][2] = coordPlot[i][j][Z].rep();
	    		pointsIm[i][j][0] = coordPlot[i][j][XY].rep();
	    		pointsIm[i][j][1] = coordPlot[i][j][XY].imp();
	    		pointsIm[i][j][2] = coordPlot[i][j][Z].imp();
	    	}

	    plot("Re(Z)", pointsRe);
	    plot("Im(Z)", pointsIm);
    }
}
