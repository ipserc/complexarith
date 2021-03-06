/*
set xrange [-30:30]
set yrange [-30:30]
set isosample 17
splot -17/3-x+2/3*y, -5+x+y, -5-x+1/2*y
splot -17/3-x+2/3*y, -5-x+1/2*y
 */

package TestComplex;

import java.util.List;
import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.*;

import java.util.ArrayList;

public class TestLineW {

	public static MatrixComplex intersect(List<MatrixComplex> listLines) {
		MatrixComplex lineSystem = new MatrixComplex(listLines.size());
		MatrixComplex solutions = new MatrixComplex();
		Complex cNum = new Complex();
		
		int i = 0;
		for (MatrixComplex line : listLines) {
			lineSystem.complexMatrix[i++] = line.complexMatrix[0].clone();
		}
		
		lineSystem = lineSystem.completepEqSys();
		if (lineSystem.typeEqSys() == 0) {
			for (i = 0; i < lineSystem.complexMatrix[0].length; ++i) {
				cNum.setComplexRec(i, 0);
				solutions = lineSystem.solveGauss(cNum);
				solutions.println("Intersection");
				//lineSystem.coefMatrix().times(solutions).println("Proof check");
			}
		}
		else {
			solutions = lineSystem.solve();
			solutions.println("Intersection");
		}
		return solutions;
	}
	
	public static String toGNUPlot(MatrixComplex hyper) {
		int rowLen = hyper.complexMatrix.length;   
		int colLen = hyper.complexMatrix[0].length;
		Complex cCoef = new Complex();
		String polynom = new String(); 

		for (int row = 0; row < rowLen; ++row) {
			for (int col = 0; col < colLen; ++col) {
				cCoef = hyper.complexMatrix[row][col];				
				if (cCoef.equals(0,0)) continue;
				if (col != 0 && col != colLen-1) polynom += "+";				
				if (col != colLen-1)
					polynom += "("+cCoef.toStringGNUPlot()+")*x"+col;
				else polynom += "="+cCoef.toStringGNUPlot();
			}
		}
		return polynom;
	}
	
	public static void main(String[] args) {
		MatrixComplex aLine = new MatrixComplex();		
		List<MatrixComplex> listLines = new ArrayList<MatrixComplex>();
		String strPoint;
		int boxSize = 65;

		System.out.println(Complex.boxTitle(boxSize, "LINE TEST"));
				
		Complex.setFormatON();

		System.out.println(Complex.boxText(boxSize, "Line Intersection Test"));
		aLine = aLine.pointVector("-1,4,-2,7", "3,-5,-2,3");
		listLines.add(aLine);
		aLine.println(toGNUPlot(aLine));
		strPoint = "1,0,0,0";
		System.out.println("Distance to ("+ strPoint +"):" + aLine.distance(strPoint));

		aLine = aLine.pointVector("-1,4,-2,7", "-1,-1,4,1");
		listLines.add(aLine);
		aLine.println(toGNUPlot(aLine));
		/* */
		aLine = aLine.pointVector("-1,4,-2,7", "-2,3,-2,-2");
		listLines.add(aLine);
		aLine.println(toGNUPlot(aLine));
		/* */
		aLine = aLine.pointVector("-1,4,-2,7", "2,1,-2,3");
		listLines.add(aLine);
		aLine.println(toGNUPlot(aLine));
		/* */
		intersect(listLines);
		
		System.out.println(Complex.boxText(boxSize, "Line Distance Test"));
		aLine = new MatrixComplex("2,1,-1,-1");
		aLine.println("aLine:");
		strPoint = "3,1,-2";
		System.out.println("Distance to ("+ strPoint +"):" + aLine.distance(strPoint));
		
		System.out.println(Complex.boxText(boxSize, "Line Distance Test"));
		aLine = new MatrixComplex("0,2,0,3");
		aLine.println("aLine:");
		strPoint = "3,1,-2";
		System.out.println("Distance to ("+ strPoint +"):" + aLine.distance(strPoint));
	}
}
