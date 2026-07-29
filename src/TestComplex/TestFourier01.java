package TestComplex;

import java.util.function.Function;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.signal.Fourier;
import com.ipserc.arith.signal.Sigfunc;
import com.ipserc.arith.signal.Fourier.e_domain;
import com.ipserc.arith.signal.Fourier.e_lineStyle;
import com.ipserc.arith.signal.Fourier.e_operator;

public class TestFourier01 {

	/**
	 * 
	 *  PRIMITIVE FUNCTIONS 
	 *  
	 * /
	// Delta Dirac
	private static Complex delta(Complex z, int t) {
		return (z.rep() == t ? Complex.ONE : Complex.ZERO);
	}
	
	//Delta Kronecker
	private static Complex delta(Complex z) {
		return delta(z, 0);
		//return (z.rep() == 0 ? Complex.ONE : Complex.ZERO);
	}
	
	// unit-step Heaviside function
	private static Complex U(Complex z, int t) {
		return (z.rep() > t ? Complex.ONE : Complex.ZERO);
	}
	
	private static Complex step(Complex z, double ton) {
		return (z.rep() >= ton) ? Complex.ONE : Complex.ZERO;
	}

	private static Complex step(Complex z, double ton, double toff) {
		return (Math.abs(z.rep()) >= ton && Math.abs(z.rep()) <= toff) ? Complex.ONE : Complex.ZERO;
	}

	private static Complex step(Complex z, int T, double ton, double toff) {
		double x = Math.abs(z.rep()%T);
		return (Math.abs(x) >= ton && Math.abs(x) <= toff) ? Complex.ONE : Complex.ZERO;
	}

	private static Complex ramp(Complex z, int T, int y0, int y1) {
		double zr = z.rep();
		double x0 = -T;
		double x1 = T;
		double x = zr%(T);
		double y = y1-(y1-y0)/(x1-x0)*(x1-x);
		y = zr < 0 ? 2*y+y1 : 2*y-y1;
		return 	new Complex(y, 0);
	}

	private static Complex ramp0(Complex z, int T, int y0, int y1) {
		Complex zr = z.plus(T/2);
		return 	ramp(zr, T, y0, y1);
	}

	private static Complex sin(Complex z, int frec) {
		return Complex.sin(z.times(Complex.DOS_PI*frec));
	}

	private static Complex cos(Complex z, int frec) {
		return Complex.cos(z.times(Complex.DOS_PI*frec));
	}
		
	private static Complex sinc(Complex z) {
		return z.equals(Complex.ZERO) ? Complex.ONE : Complex.sin(z).divides(z);
	}
	*/

	/**
	 * 
	 *  COMPOSITE FUNCTIONS 
	 *  
	 */

	private static Complex func00(Complex z) {
		// sin(1kz)cos(3kz)
		return (Complex.sin(z.times(Math.PI*1000)).times(Complex.cos(z.times(Math.PI*3000)).plus(Complex.sin(z.times(Math.PI*75)))));
	}

	private static Complex func01(Complex z) {
		// (2+sin²(2z))/(2+cos³(3z))
		return ((Complex.sin(z.times(Math.PI*2)).power(2)).plus(2)).divides((Complex.cos(z.times(Math.PI*3)).power(3)).plus(2));
	}

	private static Complex func02(Complex z) {
		// z³-3z-2
		return (z.power(3).minus(z.times(3)).minus(2));
	}

	private static Complex func03(Complex z) {
		// z
		return (z);
	}
	
	private static Complex func04(Complex z) {
		// 1/(z-5)
		return (z.plus(5)).inverse().plus(Complex.cos(z.times(100)));
	}
	
	private static Complex func05(Complex z) {
		// sin(1kz)+cos(3kz)
		//return (Complex.sin(z.times(500000000)).times(10).plus(Complex.cos(z.times(7000+(Math.random()-0.5)*10))));
		return Complex.cos(z.times(Math.PI*(7000+(Math.random()-0.5)*3)));
	}

	private static Complex func06(Complex z) {
		// z
		return (z.minus(1).divides(z.plus(12)));
	}
	
	private static Complex func07(Complex z) {
		// z
		return (z.power(0.5));
	}

	private static Complex func08(Complex z, int a) {
		// z
		if (z.cre() <= a-1 || z.cre() >= a+1 ) return (Complex.mONE);
		else return (Complex.ONE);
	}

	private static Complex func09(Complex z) {
		// z
		if (z.cre() <= 0) return (Complex.mONE);
		else return (Complex.ONE);
	}
	
	private static Complex func10(Complex z) {
		return z.power(.5);
	}

	private static Complex func11(Complex z) {
		// sin(z)(1-exp(-z²/5k))
		return (Complex.sin(z.times(Math.PI)).times(Complex.ONE.minus(Complex.exp(z.power(2).opposite().times(Math.PI).divides(8000)))));
	}

	private static Complex func12(Complex z) {
		return (Complex.sin(z.times(Math.PI))).times(Math.log(z.abs()+1));
	}

	private static Complex func13(Complex z, double T) {
		
		if (Math.sin(z.mod()*T) >= 0) return Complex.ONE;
		else return Complex.mONE;
	}
	
	private static Complex sin300(Complex z) {
		return Complex.sin(z.times(Math.PI*300));
	}
	
	private static Complex sin3k(Complex z) {
		return Complex.sin(z.times(Math.PI*3000));
	}
	
	private static Complex cos3k(Complex z) {
		return Complex.cos(z.times(Math.PI*3000));
	}
	
	private static Complex sin30k(Complex z) {
		return Complex.sin(z.times(30000));
	}

	private static Complex func14(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Math.PI*(7000+50*(Math.sin(Math.ceil(z.rep()%13))))));
	}

	private static Complex func15(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Math.PI*(7000+50*(Math.sin(z.rep()%13)))));
	}

	private static Complex func16(Complex z) {
		//cos(z(70Hz+5sin(z mod 7)))
		return Complex.cos(z.times(Math.PI*(70+5*(Math.sin(z.rep()%7)))));
	}

	private static Complex func17(Complex z, double slope, int step) {
		Complex val = new Complex(Math.abs(z.rep()) % step, 0);
		return val.times(slope);
	}
	
	
	public static void TestFourierSeries(Function<Complex, Complex> func, double DloLimit, double DupLimit,int order, int decPrc) {
		Complex loLimit = new Complex(DloLimit,0); 
		Complex upLimit = new Complex(DupLimit,0);
		int sampleFreq = 10000;
		Fourier sFourier = new Fourier(func, loLimit, upLimit);
		sFourier.serialize(order, decPrc);
		sFourier.printCoefs();
		/* .println("ival(" + ival.toString() + ") = ") */
		sFourier.plotSamples("Function", sampleFreq, true, e_lineStyle.LINES);
		sFourier.plotSeries("Fourier Series", sampleFreq, true, e_lineStyle.LINES);
		sFourier.plotCompare(sampleFreq, e_lineStyle.LINES);
	}

	public static void TestDFTL(Function<Complex, Complex> func, int sampleFreq, double loLimit, double upLimit) {
		MatrixComplex dataf = new MatrixComplex((int)sampleFreq+1, 2);
		Complex ival = new Complex();
		Complex increment = new Complex();
		Fourier tFourier = new Fourier(func, loLimit, upLimit);
		tFourier.DFTL(sampleFreq);
		increment.setComplexRec((upLimit - loLimit)/sampleFreq, 0);

		ival.setComplexRec(loLimit, 0);
		for (int i = 0; i < sampleFreq; ++i) {
			dataf.setItem(i, 0, ival);
			dataf.setItem(i, 1, func.apply(ival));
			ival = ival.plus(increment);
		}

		tFourier.plot("Function", sampleFreq, dataf, true, e_lineStyle.LINES);

		tFourier.plotDFT("DFT COMPLEX", e_domain.SAMP ,e_operator.COMPLEX, false, e_lineStyle.LINES);
		tFourier.plotDFT("DFT MAGNITUDE", e_domain.SAMP, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		tFourier.plotDFT("DFT SQUARE", e_domain.SAMP, e_operator.SQUARE, false, e_lineStyle.LINES);

		tFourier.plotDFT("DFT COMPLEX", e_domain.FREC, e_operator.COMPLEX, true, e_lineStyle.LINES);
		tFourier.plotDFT("DFT MAGNITUDE", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		tFourier.plotDFT("DFT SQUARE", e_domain.FREC, e_operator.SQUARE, true, e_lineStyle.LINES);

		tFourier.saveSamples("/home/ipserc/saco/samples.txt", "");
		tFourier.saveDFT("/home/ipserc/saco/dft.txt", "");
		tFourier.IDFT();
		tFourier.plot("IFT - Function", sampleFreq, dataf, true, e_lineStyle.LINES);
	}

	public static void TestDFT(Function<Complex, Complex> func, int sampleFreq, double loLimit, double upLimit) {
		Fourier tFourier = new Fourier(func, loLimit, upLimit);
		tFourier.DFT(sampleFreq);

		//tFourier.printTCoefs();
		tFourier.plotFunction("Function", sampleFreq, true, e_lineStyle.LINES);
		/**/
		tFourier.plotDFT("DFT COMPLEX", e_domain.SAMP ,e_operator.COMPLEX, false, e_lineStyle.LINES);
		tFourier.plotDFT("DFT MAGNITUDE", e_domain.SAMP, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		tFourier.plotDFT("DFT SQUARE", e_domain.SAMP, e_operator.SQUARE, false, e_lineStyle.LINES);
		/**/

		/** /
		tFourier.plotDFT("DFT COMPLEX", e_domain.SAMP ,e_operator.COMPLEX, true, e_lineStyle.LINES);
		tFourier.plotDFT("DFT MAGNITUDE", e_domain.SAMP, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		tFourier.plotDFT("DFT SQUARE", e_domain.SAMP, e_operator.SQUARE, true, e_lineStyle.LINES);
		/**/
		
		/**/
		tFourier.plotDFT("DFT COMPLEX", e_domain.FREC, e_operator.COMPLEX, false, e_lineStyle.LINES);
		tFourier.plotDFT("DFT MAGNITUDE", e_domain.FREC, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		tFourier.plotDFT("DFT SQUARE", e_domain.FREC, e_operator.SQUARE, false, e_lineStyle.LINES);
		/**/

		/** /
		tFourier.plotDFT("DFT COMPLEX", e_domain.FREC, e_operator.COMPLEX, true, e_lineStyle.LINES);
		tFourier.plotDFT("DFT MAGNITUDE", e_domain.FREC, e_operator.MAGNITUDE, true, e_lineStyle.LINES);
		tFourier.plotDFT("DFT SQUARE", e_domain.FREC, e_operator.SQUARE, true, e_lineStyle.LINES);
		/**/

		tFourier.saveSamples("/home/ipserc/saco/samples.txt");
		tFourier.saveDFT("/home/ipserc/saco/dft.txt");

		/** /
		tFourier.IDFT();
		tFourier.plotFunction("IFT - Function", sampleFreq, true, e_lineStyle.LINES);

		/**/
	}

	public static void TestSDFT() {
		Fourier tFourier = new Fourier("/home/ipserc/saco/samples.txt");
		tFourier.DFT(tFourier.getSampleFreq());
		
		tFourier.plotFunction("Function", tFourier.getSampleFreq(), true, e_lineStyle.LINES);
		/**/
		tFourier.plotDFT("DFT COMPLEX", e_domain.SAMP ,e_operator.COMPLEX, false, e_lineStyle.LINES);
		tFourier.plotDFT("DFT MAGNITUDE", e_domain.SAMP, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		tFourier.plotDFT("DFT SQUARE", e_domain.SAMP, e_operator.SQUARE, false, e_lineStyle.LINES);
		/**/

	}

	public static void TestIDFT() {
		Fourier tFourier = new Fourier();
		tFourier.readDFT("/home/ipserc/saco/dft.txt", "");
		tFourier.IDFT();
		
		tFourier.plotFunction("Function", tFourier.getSampleFreq(), true, e_lineStyle.LINES);
		/**/
		tFourier.plotDFT("DFT COMPLEX", e_domain.SAMP ,e_operator.COMPLEX, false, e_lineStyle.LINES);
		tFourier.plotDFT("DFT MAGNITUDE", e_domain.SAMP, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		tFourier.plotDFT("DFT SQUARE", e_domain.SAMP, e_operator.SQUARE, false, e_lineStyle.LINES);
		/**/

	}
	
	public static void main(String[] args) {
		double loLimit, upLimit;
		int sampleFreq;
		Complex.setFormatON();
		Complex.setScientificON(5);
		
		/*
		TestFourierSeries(z -> func08(z, 2), -4.9, 4.9, 100, 3);
		TestFourierSeries(z -> func13(z, 2), -4.9, 4.9, 100, 3);
		//TestFourierSeries(z -> func17(z, .15, 7), -27, 27, 100, 3);
		//TestFourierSeries(z -> Sigfunc.sinc(z), -30, 30, 100, 3);
		TestFourierSeries(z -> Sigfunc.cosinc(z), -30, 30, 100, 3);
		// TestFourierSeries(z -> Sigfunc.step(z, 2, -1, 1), -5, 5, 100, 3);
		TestFourierSeries(z -> Sigfunc.step(z, 8, 3, 6), -20, 20, 100, 3);
		TestFourierSeries(z -> Sigfunc.ramp(z, 2, 1), -5, 5, 100, 3);
		//TestFourierSeries(z -> Sigfunc.ramp0(z, 2, -1, 1), -5, 5, 100, 3);
		//TestFourierSeries(z -> Sigfunc.saw(z, 2, 1), -5, 5, 100, 3);
		TestFourierSeries(z -> Sigfunc.saw0(z, 2, 1), -15, 15, 300, 3);
		*/

		//TestDFT(z -> sinc(z), 66, -30, 30);		
		//TestDFT(z -> func00(z), 8000, -.4084, .4084);
		TestDFT(z -> sin3k(z), 6000, -Complex.DOS_PI*500/6000, Complex.DOS_PI*500/6000);
		//TestDFT(z -> func14(z), 6000, -Complex.DOS_PI, Complex.DOS_PI);
		//TestDFT(z -> func14(z), 8000, -5, 5);
		//TestDFT(z -> func15(z), 8000, -5, 5);
		//TestDFT(z -> func15(z), 16000, -5, 5);
		//TestDFT(z -> func16(z), 10000, -25, 25);

		/** /
		sampleFreq = 10000;
		loLimit = -25;
		upLimit = 25;
		TestDFT(z -> func16(z), sampleFreq, loLimit, upLimit);
		/**/
		
		//TestDFT(z -> func00(z), 6500, -.4084, .4084);
		//TestSDFT();
		//TestIDFT();

	}
}
