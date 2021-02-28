package TestComplex;

import java.util.function.Function;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.signal.Fourier;
import com.ipserc.arith.signal.Sigfunc;
import com.ipserc.arith.signal.Fourier.e_lineStyle;

public class TestSigfunc {

	public static void Test_Sigfunc(String strFunction, Function<Complex, Complex> func, double DloLimit, double DupLimit,int order, int decPrc) {
		Complex loLimit = new Complex(DloLimit,0); 
		Complex upLimit = new Complex(DupLimit,0);
		int sampleFreq = 10000;
		// Used only for plotting purposes
		Fourier sFourier = new Fourier(func, loLimit, upLimit);
		sFourier.doSrsSampling();
		sFourier.plotSamples(strFunction, sampleFreq, true, e_lineStyle.LINES);
	}

	public TestSigfunc() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		Sigfunc.version();

		/*
		Test_Sigfunc("step(z, 8, 3, 6)", z -> Sigfunc.step(z, 8, 3, 6), -40, 40, 100, 3);
		Test_Sigfunc("step(z, 5, 0, 3)", z -> Sigfunc.step(z, 5, 0, 3), -40, 40, 100, 3);
		Test_Sigfunc("step(z, 5, 1, 3)", z -> Sigfunc.step(z, 5, 1, 3), -40, 40, 100, 3);
		*/
		
		Test_Sigfunc("ramp(z, 18, 8)", z -> Sigfunc.ramp(z, 18, 8), -40, 40, 100, 3);
		Test_Sigfunc("ramp0(z, 18, 8)" ,z -> Sigfunc.ramp0(z, 18, 8), -40, 40, 100, 3);
		
		Test_Sigfunc("saw(z, 18, 8)", z -> Sigfunc.saw(z, 18, 8), -40, 40, 100, 3);
		Test_Sigfunc("saw0(z, 18, 8)", z -> Sigfunc.saw0(z, 18, 8), -40, 40, 100, 3);
		
	}

}
