package TestComplex;

import java.util.function.Function;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.e_lineStyle;

public class TestFuncSerie01 {

	private static int SAMPLES = 1000;

	private static Complex func00(Complex z, int n) {
		// n·z·(1-z)^(1/n)
		return 	z.times(n).times((Complex.ONE.minus(z)).power(n));
	}

	private static Complex func01(Complex z, int n) {
		// z^(2·n)/(1+x^(2·n))
		return 	z.power(2*n).divides(Complex.ONE.plus(z.power(2*n)));
	}

	private static Complex func02(Complex z, int n) {
		// z·n²·exp(-z·n)
		return 	z.times(n*n).times(Complex.exp(z.times(-n)));
		//return 	z.times(n*n).times(Complex.exp(z.opposite().times(n)));
		//return 	z.times(n*n).divides(Complex.exp(z.times(n)));
	}

	public static double[][] toSeries(long samples, int idPlot, MatrixComplex data) {
		double dataRe[][]  = new double[(int)samples+1][2];

		for (int t = 0; t <= samples; ++t) {
			dataRe[t][0] = data.complexMatrix[0][t].rep();
			dataRe[t][1] = data.complexMatrix[idPlot][t].rep();
		}
		return dataRe;
	}

	public static void plotSerie(int n0, int nn, Double x0, Double xn) {
		MatrixComplex data = new MatrixComplex((nn-n0)+2, SAMPLES+1);
		double inc = (xn-x0)/SAMPLES;
		double x = x0;

		for (int step = 0; step <= SAMPLES; ++step) {
			data.setItem(0, step, x);
			x += inc;
		}
		
		for (int n = n0; n <= nn; ++n) {
			int idxFunc = (n-n0)+1;
			for (int step = 0; step <= SAMPLES; ++step) {
				//Set the function here
				data.setItem(idxFunc, step, func02(data.getItem(0, step), n));
				//data.setItem(idxFunc, step, func(data.getItem(0, step), n));
			}
		}
		
		data.println();

		String title = "";
		double[][][] series = new double[(nn-n0)+1][][];
		for (int n = n0; n <= nn; ++n) {
			int idxFunc = (n-n0)+1;
			series[n-n0] = toSeries(SAMPLES, idxFunc, data);
		}

		MatrixComplexPlot.plotSeriesSync(title, e_lineStyle.LINES, series);

		System.out.println("END");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int n0 = 1;
		int nn = 30;
		Double x0 = 0.0;
		Double xn = 2.5;
		
		Complex.setFormatON();
		Complex.setScientificON(3);
		plotSerie(n0, nn, x0, xn);
	}

}
