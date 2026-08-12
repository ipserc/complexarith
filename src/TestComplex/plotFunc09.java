package TestComplex;

import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.e_lineStyle;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class plotFunc09 {
	/**
	 * Plots the function expressed in GNUPlot format between loLimit and upLimit.
	 * @param GNUplotExpression The function to plot in GNUPlot format.
	 * @param loLimit Lower limit of the plot.
	 * @param upLimit Upper limit of the plot.
	 */

	public static void plot(long samples, double points[][]) {
		MatrixComplexPlot.plotSeriesSync("PRUEBA", e_lineStyle.LINES, points);
	}
	

	public static void main(String[] args) {
		int X = 0;
		int Y = 1;
		int samples = -1; // The last line of the file is empty
		double[][] points;
			
		try {
			// NOTA: fichero generado externamente por un script Python; no lo genera este test,
			// hay que copiarlo a <user.home>/ipserc/saco/primes/ antes de poder ejecutarlo.
			File myObj = new File(TestScratchPaths.path("primes", "puntos_funcion_primos.txt"));
			Scanner myReader = new Scanner(myObj);
			// Get the number of lines --> the size of the array
			while (myReader.hasNextLine()) {
				++samples;
				// System.out.println(samples);
				myReader.nextLine();
			}
			myReader.close();
			myReader = new Scanner(myObj);
			points = new double[samples][2];
			int i = 0;
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				if (data.isEmpty()) break;
				// System.out.println(data);
				String[] tupla = data.split("\t");
				points[i][X] = Double.parseDouble(tupla[X]);
				points[i++][Y] = Double.parseDouble(tupla[Y]);
			}
			myReader.close();
			plot(samples, points);
		} catch (FileNotFoundException e) {
			System.out.println("A File Not Found Exception occurred.");
			e.printStackTrace();
		}
	}

}
