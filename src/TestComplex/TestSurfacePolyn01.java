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
import com.panayotis.gnuplot.JavaPlot;



public class TestSurfacePolyn01 {
	
	public static void plot(String title, long samples, double points[][]) {
		JavaPlot p = new JavaPlot();
		p.newGraph3D();
		p.setTitle(title);
		p.addPlot(points);
		p.set("zeroaxis", "");
		// p.set("logscale", "z");
		// p.set("style", "data lines");
		// p.set("style", "data polygons");
		p.set("style", "data boxplot");
		// p.set("style", "data surface");
		// p.set("style", "data rgbalpha");
		// p.set("style", "data isosurface");
		/*
			p.set("style", "data pm3d"); 
			//p.set("style", "boxes");
			//set pm3d depthorder base
			p.set("pm3d","depthorder base");
			//set pm3d lighting
			p.set("pm3d","lighting");
			//set boxdepth 0.4
			p.set("boxdepth","0.4");
			//p.set("xrange", "[0:"+samples+"]");
		 * 
		 */
		p.set("grid","");
		p.plot();
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
		Complex.exact(false);
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
	    double[][] pointsRe = new double[k][3];
	    double[][] pointsIm = new double[k][3];
	    for (int i = 0; i < k; ++i) {
    		pointsRe[i][0] = coordPlot[i][XY].rep();
    		pointsRe[i][1] = coordPlot[i][XY].imp();
    		pointsRe[i][2] = coordPlot[i][Z].rep();
    		pointsIm[i][0] = coordPlot[i][XY].rep();
    		pointsIm[i][1] = coordPlot[i][XY].imp();
    		pointsIm[i][2] = coordPlot[i][Z].imp();
    	}
	    
		Complex.printBoxText(3, boxSize, "Plotting...");
		plot("Re(Z)", sampleBase, pointsRe);
		plot("Im(Z)", sampleBase, pointsIm);
    }
}
