package TestComplex;

import java.util.function.Function;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.signal.Fourier;
import com.ipserc.arith.signal.Laplace;
import com.ipserc.arith.signal.Laplace.e_domain;
import com.ipserc.arith.signal.Laplace.e_lineStyle;
import com.ipserc.arith.signal.Laplace.e_operator;

public class TestLaplace01 {
	
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
		double zr = z.rep()+T/2;
		double x0 = -T;
		double x1 = T;
		double x = zr%(T);
		double y = y1-(y1-y0)/(x1-x0)*(x1-x);
		y = zr < 0 ? 2*y+y1 : 2*y-y1;
		return 	new Complex(y, 0);
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


	/* ----------------------------------------------------------------------------------------- */
	
	private static Complex func00(Complex z) {
		// sin(1kz)cos(3kz)
		return (Complex.sin(z.times(Complex.DOS_PI*1000)).times(Complex.cos(z.times(Complex.DOS_PI*3000)).plus(Complex.sin(z.times(Complex.DOS_PI*75)))));
	}

	private static Complex func01(Complex z) {
		// (2+sin²(2z))/(2+cos³(3z))
		return ((Complex.sin(z.times(Complex.DOS_PI*2)).power(2)).plus(2)).divides((Complex.cos(z.times(Complex.DOS_PI*3)).power(3)).plus(2));
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
		return Complex.cos(z.times(Complex.DOS_PI*(7000+(Math.random()-0.5)*3)));
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
		return (Complex.sin(z.times(Complex.DOS_PI)).times(Complex.ONE.minus(Complex.exp(z.power(2).opposite().times(Complex.DOS_PI).divides(8000)))));
	}

	private static Complex func12(Complex z) {
		return (Complex.sin(z.times(Complex.DOS_PI))).times(Math.log(z.abs()+1));
	}

	private static Complex func13(Complex z, double T) {
		
		if (Math.sin(z.mod()*T) >= 0) return Complex.ONE;
		else return Complex.mONE;
	}
	
	private static Complex sin300(Complex z) {
		return Complex.sin(z.times(Complex.DOS_PI*300));
	}
	
	private static Complex sin3k(Complex z) {
		return Complex.sin(z.times(Complex.DOS_PI*3000));
	}
	
	private static Complex cos3k(Complex z) {
		return Complex.cos(z.times(Complex.DOS_PI*3000));
	}
	
	private static Complex sin30k(Complex z) {
		return Complex.sin(z.times(30000));
	}

	private static Complex func14(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Complex.DOS_PI*(7000+50*(Math.sin(Math.ceil(z.rep()%13))))));
	}

	private static Complex func15(Complex z) {
		//cos(z(7KHz+50sin(z mod 13)))
		return Complex.cos(z.times(Complex.DOS_PI*(7000+50*(Math.sin(z.rep()%13)))));
	}

	private static Complex func16(Complex z) {
		//cos(z(70Hz+5sin(z mod 7)))
		return Complex.cos(z.times(Complex.DOS_PI*(70+5*(Math.sin(z.rep()%7)))));
	}

	private static Complex func17(Complex z, double slope, int step) {
		Complex val = new Complex(Math.abs(z.rep()) % step, 0);
		return val.times(slope);
	}
	public static Complex z2Sinz(Complex z) {
		return z.power(2).times(Complex.sin(z));
	}
	
	public static Complex func18(Complex z, int frec) {
		return (Complex.sin(z.times(Complex.DOS_PI*frec)).plus(2)).divides(Complex.cos(z.times(Complex.DOS_PI*frec)).plus(2));
	}
	
	public static Complex func19(Complex z, int frec) {
		return func18(z,frec).minus(1.3375);
	}
	
	public static Complex func20(Complex z) {
		return Complex.sin(z).times(z.power(2).plus(2));
	}
	
	public static void TestSDLT() {
		Laplace tLaplace = new Laplace("/home/ipserc/saco/samples.txt");
		tLaplace.DLT(tLaplace.getSampleFreq());
		
		tLaplace.plotFunction("Function", tLaplace.getSampleFreq(), true, e_lineStyle.LINES);
		/** /
		tLaplace.plotDLT("DLT COMPLEX", e_domain.SAMP ,e_operator.COMPLEX, false, e_lineStyle.LINES);
		tLaplace.plotDLT("DLT MAGNITUDE", e_domain.SAMP, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		tLaplace.plotDLT("DLT SQUARE", e_domain.SAMP, e_operator.SQUARE, false, e_lineStyle.LINES);
		/**/

	}

	public static void TestIDLT() {
		Laplace tLaplace = new Laplace();
		tLaplace.readDLT("/home/ipserc/saco/dft.txt", "");
		tLaplace.IDLT();
		
		tLaplace.plotFunction("Function", tLaplace.getSampleFreq(), true, e_lineStyle.LINES);
		/** /
		tLaplace.plotDLT("DLT COMPLEX", e_domain.SAMP ,e_operator.COMPLEX, false, e_lineStyle.LINES);
		tLaplace.plotDLT("DLT MAGNITUDE", e_domain.SAMP, e_operator.MAGNITUDE, false, e_lineStyle.LINES);
		tLaplace.plotDLT("DLT SQUARE", e_domain.SAMP, e_operator.SQUARE, false, e_lineStyle.LINES);
		/**/

	}
	
	public static void TestContLaplaceTrf(Function<Complex, Complex> func, int sampleFreq, double DloLimit, double DupLimit, int decPrc) {
		Complex loLimit = new Complex(DloLimit,0); 
		Complex upLimit = new Complex(DupLimit,0);
		Laplace sLaplace = new Laplace(func, loLimit, upLimit);
		sLaplace.CLT(sampleFreq, decPrc);
		//sLaplace.printCoefs();
		/* .println("ival(" + ival.toString() + ") = ") */
		sLaplace.plotFunction("Function", sampleFreq, true, e_lineStyle.LINES);
		sLaplace.plotDLT("Transform", true, e_lineStyle.LINES);
	}

	public static void TestDiscLaplaceTrf(Function<Complex, Complex> func, int sampleFreq, double DloLimit, double DupLimit, int decPrc) {
		Complex loLimit = new Complex(DloLimit,0); 
		Complex upLimit = new Complex(DupLimit,0);
		Laplace sLaplace = new Laplace(func, loLimit, upLimit);
		sLaplace.DLT(sampleFreq);
		//sLaplace.printCoefs();
		/* .println("ival(" + ival.toString() + ") = ") */
		sLaplace.plotFunction("Function", sampleFreq, true, e_lineStyle.LINES);
		sLaplace.plotDLT("Transform", true, e_lineStyle.LINES);
	}

	public static void main(String[] args) {
		double loLimit, upLimit;
		int sampleFreq = 500;
		Complex.setFormatON();
		Complex.setScientificON(5);
		
		//TestContLaplaceTrf(z -> ramp(z, 5, -3, 7), sampleFreq, 0, 25, 3);
		//TestDiscLaplaceTrf(z -> ramp(z, 5, -3, 7), sampleFreq, 0, 25, 3);
		//TestContLaplaceTrf(z -> step(z, 8, 2, 5), sampleFreq, 0, 25, 3);
		//TestDiscLaplaceTrf(z -> step(z, 8, 2, 5), sampleFreq, 0, 25, 3);
		TestContLaplaceTrf(z -> sin(z,5), sampleFreq, 0, 5, 3);
		TestDiscLaplaceTrf(z -> sin(z,5), sampleFreq, 0, 5, 3);
		//TestContLaplaceTrf(z -> U(z,3).times(sin(z,5)), sampleFreq, 0, 5, 3);
		//TestDiscLaplaceTrf(z -> U(z,3).times(sin(z,5)), sampleFreq, 0, 10);
		//TestDiscLaplaceTrf(z -> U(z,3).times(cos(z,5)), sampleFreq, 0, 10);
		//TestContLaplaceTrf(z -> (sin(z,5).divides((z.plus(3))).power(new Complex(0,3))), sampleFreq, 0.0000000000001, 5, 3);
		//TestContLaplaceTrf(z -> (sin(z,5).divides(z.plus(3))), sampleFreq, 0, 10, 3);
		//TestContLaplaceTrf(z -> (sin(z,5).plus(2)), sampleFreq, 1, 10, 3);
		//TestContLaplaceTrf(z -> U(z,1).times(Complex.log(z)), sampleFreq, 0.1, 10, 3);
		//TestContLaplaceTrf(z -> ((z.plus(3)).divides(sin(z,5).plus(2))), sampleFreq, 0, 10, 3);
		//TestContLaplaceTrf(z -> U(z,3).times(sin(z,5)), sampleFreq, 0, 10, 3);
		//TestContLaplaceTrf(z -> U(z,3).times(cos(z,5)), sampleFreq, 0, 10, 3);
		//TestContLaplaceTrf(z -> z2Sinz(z), sampleFreq, 0, 10, 3);
		//TestDiscLaplaceTrf(z -> z2Sinz(z), sampleFreq, 0, 10);
		//TestContLaplaceTrf(z -> func02(z), sampleFreq, 0, 10, 3);
		//TestDiscLaplaceTrf(z -> func02(z), sampleFreq, 0, 10);
		//TestContLaplaceTrf(z -> step(z, 5, 7), sampleFreq, 0, 10, 3);
		//TestDiscLaplaceTrf(z -> step(z, 5, 7), sampleFreq, 0, 10);
		//TestContLaplaceTrf(z -> func17(z, .25, 7), sampleFreq, 0, 7, 3);
		//TestDiscLaplaceTrf(z -> func17(z, .255, 7), sampleFreq, 0, 7);
		//TestContLaplaceTrf(z -> func16(z), sampleFreq, 0, 30, 3);
		//TestDiscLaplaceTrf(z -> func16(z), sampleFreq, 0, 30);
		//TestContLaplaceTrf(z -> func19(z,10), sampleFreq, 0, 10, 3);
		//TestContLaplaceTrf(z -> func20(z), sampleFreq, 0, 60, 3);
		//TestContLaplaceTrf(z -> z2Sinz(z), sampleFreq, 0, 60, 3);
		
		//TestDiscLaplaceTrf(z -> sinc(z), 66, -30, 30);		
		//TestDiscLaplaceTrf(z -> func00(z), 8000, -.4084, .4084);
		//TestDiscLaplaceTrf(z -> func02(z), sampleFreq, 10);		
		//TestDiscLaplaceTrf(z -> func03(z), 20, 0, 300);		
		//TestDiscLaplaceTrf(z -> sin3k(z), 6000, -Complex.DOS_PI*500/6000, Complex.DOS_PI*500/6000);
		//TestDiscLaplaceTrf(z -> func14(z), 6000, -Complex.DOS_PI, Complex.DOS_PI);
		//TestDiscLaplaceTrf(z -> func14(z), 8000, -5, 5);
		//TestDiscLaplaceTrf(z -> func15(z), 8000, -5, 5);
		//TestDiscLaplaceTrf(z -> func15(z), 16000, -5, 5);
		//TestDiscLaplaceTrf(z -> func16(z), 10000, -25, 25);

		/** /
		sampleFreq = 10000;
		loLimit = -25;
		upLimit = 25;
		TestDiscLaplaceTrf(z -> func16(z), sampleFreq, loLimit, upLimit);
		/**/
		
		//TestDiscLaplaceTrf(z -> func00(z), 6500, -.4084, .4084);
		//TestSDLT();
		//TestIDLT();

	}
}
