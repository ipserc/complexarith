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
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.Graph;

import java.awt.image.SampleModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestZeta04 {
	
	public static void plot(long samples, double points[][][]) {
		JavaPlot p = new JavaPlot();
		p.newGraph3D();
		p.setTitle("PRUEBA");
		//p.addGraph(points);
		p.set("zeroaxis", "");
		// p.set("style", "data lines");
		// p.set("style", "data polygons");
		p.set("style", "data boxplot");
		// p.set("style", "data surface");
		// p.set("style", "data rgbalpha");
		//p.set("style", "data isosurface");
		//p.set("xrange", "[0:"+samples+"]");
		p.set("grid","");
		p.plot();
	}

	public static File newFile(String filename) {
		File fileHdlr = (File)null;
		Path filePath = FileSystems.getDefault().getPath(filename);
		if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS )) {
			try {
				Files.delete(filePath);
			}
			catch (IOException e) {
		        System.out.println("Deletion "+ filePath.getFileName() + " error occurred.");
		        e.printStackTrace();
		        System.exit(e.hashCode());
		        //return (File)null;
		      } 
		}
	    try {
	        fileHdlr = new File(filename);
	        if (fileHdlr.createNewFile()) {
	          System.out.println("File created: " + fileHdlr.getName());
	        } else {
	          System.out.println("File already exists.");
	        }
	      } catch (IOException e) {
	        System.out.println("Creation " + filename + " error occurred.");
	        e.printStackTrace();
	        System.exit(e.hashCode());
	        //return (File)null;
	      }
	    return fileHdlr;
	}
	
	public static FileWriter newFileWriter(String filename) {
		FileWriter fileWriteHdlr = (FileWriter)null;
	    try {
	    	fileWriteHdlr = new FileWriter(filename);
	        System.out.println("Successfully created write file: " + filename);
	      } catch (IOException e) {
	        System.out.println("Error occurred creating write file: " + filename);
	        e.printStackTrace();
	        System.exit(e.hashCode());
	      }
	    return fileWriteHdlr;
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
		Complex.exact(false);
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

	    FileWriter fileWriteHdlr = newFileWriter("/home/ipserc/eclipse-workspace/complexarith/data/Zeta.dat");
	    
	    int row = 0, col;
	    String dataLine;
	    
	    try {
		    // row 0
		    dataLine = "";
		    // 1st item
		    dataLine += (sampleBase+1) + " ";
		    for (col = 0; col < sampleBase; ++col) {
		    	// set de x values
		    	dataLine += pointsRe[row][col][1] + " ";
		    }
		    
		    System.out.println(dataLine);
		    fileWriteHdlr.write(dataLine+"\n");
		    
		    // 1 <= row <= samplebase
		    for (row = 1; row <= sampleBase; ++row) {
		    	col = 0;
			    dataLine = "";
			    dataLine += pointsRe[row-1][col][0] + " ";
			    for (col = 1; col <= sampleBase; ++col) {
				    dataLine += pointsRe[row-1][col-1][2] + " ";		    	
			    }
			    System.out.println(dataLine);
			    fileWriteHdlr.write(dataLine+"\n");
		    }
		    fileWriteHdlr.flush();
		    fileWriteHdlr.close();
	    }
	    catch (IOException e) {
	        System.out.println("An error occurred writing in file: " + fileWriteHdlr.toString());
	        e.printStackTrace();
	        return;
	    }
    }
}
