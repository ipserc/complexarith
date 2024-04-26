package TestComplex;

import java.util.function.Function;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.signal.Fourier;
import com.ipserc.arith.signal.Sigfunc;
import com.ipserc.arith.signal.Fourier.e_domain;
import com.ipserc.arith.signal.Fourier.e_lineStyle;
import com.ipserc.arith.signal.Fourier.e_operator;

public class TestLimits02 {

	private static Complex func00(Complex z) {
		// (z³-1)/(z-1)
		return z.power(3).minus(1).divides(z.power(1).minus(1));
	}

	private static Complex func01(Complex z) {
		// (z³-27)/(z-3)
		return z.power(3).minus(27).divides(z.power(1).minus(3));
	}

	private static Complex func02(Complex z) {
		// (z/(z+1))^(z+1)
		return (z.divides(z.plus(1))).power(z.plus(1));
	}

	private static Complex func03(Complex z) {
		// sin(z-1)/ln(z)
		return Complex.sin(z.minus(1)).divides(Complex.log(z));
	}

	private static Complex func04(Complex z) {
		// (z²-1)/ln(z)
		return (z.power(2).minus(1)).divides(Complex.log(z));
	}

	private static Complex func05(Complex z) {
		// (z⁵-1)/ln(z²)
		return (z.power(5).minus(1)).divides(Complex.log(z.power(2)));
	}

	private static Complex func06(Complex z) {
		// (z⁵-1)/(2*ln(z))
		return (z.power(5).minus(1)).divides(Complex.log(z).times(2));
	}

	private static Complex func07(Complex z) {
		// ln(z)/(z-1)^(1/2)
		return (Complex.log(z).divides(Complex.sqrt(z.minus(1))));
	}

	private static Complex func08(Complex z) {
		// (z²-3)/(z⁴-3z²+1)
		return ((z.power(2).minus(3)).divides(z.power(4).minus(z.power(2).times(3)).plus(1)));
	}

	private static Complex func09(Complex z) {
		// ((z²-3)/(z²+z))^(2*z+1)
		return ((z.power(2).minus(3)).divides(z.power(2).plus(z))).power(z.times(2).plus(1));
	}
	
	private static Complex func10(Complex z, Complex t) {
		// (1-t/z)^z
		return ((Complex.ONE.minus(t.divides(z))).power(z));
	}

	private static Complex func10a(Complex z) {
		// ((z+1)/(z-3))^(-z²+2)
		/** /
		Complex numer = new Complex();
		Complex denom = new Complex();
		Complex power = new Complex();
		Complex result1, result2;

		numer = z.plus(1);
		denom = z.minus(3);
		power = z.power(2);
		power = power.opposite();
		power = power.plus(2);
		result1 = numer.divides(denom);
		result2 = result1.power(power);
		return result2;
		/**/
		return ((z.plus(1)).divides(z.minus(3))).power((z.power(2)).opposite().plus(2));
	}

	private static Complex func10b(Complex z) {
		// ((z-3)/(z+1))^(z²-2)
		return ((z.minus(3)).divides(z.plus(1))).power(z.power(2).minus(2));
	}

	private static Complex func11(Complex z) {
		// "z*sin(z)/(1-cos(z))"
		/*
		Complex numer, denom, result;
		numer = z.times(Complex.sin(z));
		denom = Complex.ONE.minus(Complex.cos(z));
		result = numer.divides(denom);
		return result;
		*/
		return z.times(Complex.sin(z)).divides(Complex.ONE.minus(Complex.cos(z)));
	}
	
	private static Complex funcIntGamma(Complex z, Complex t, int n) {
		// t^(z+n)/(z+n)
		Complex g = new Complex();
		for (int i = 0; i <=n; ++i) {
			Complex z_plus_n = z.plus(n);
			g = g.plus(t.power(z_plus_n)).divides(z_plus_n).times(Math.pow(-1, n));
		}
		return g;
	}

	public static void limit(String strFunc, Function <Complex, Complex> func, Complex point) {
		Complex limit = Complex.limit(func, point);
		System.out.print("limit(" + strFunc +", z, " + point.toStringPol() + ") = ");
		if (limit != null) System.out.println(limit.toStringPol());
		else System.out.println("The limit doesn't exist");
		System.out.println(Complex.repeat("-",80));
	}

	public static void limit(String strFunc, Function <Complex, Complex> func, double point) {
		limit(strFunc, func, new Complex(point, 0));
	}

	public static void limit_inf(String strFunc, Function <Complex, Complex> func) {
		Complex limit = Complex.limit_inf(func);
		System.out.print("limit(" + strFunc +", z, inf) = ");
		if (limit != null) limit.println();
		else System.out.println("The limit doesn't exist");
		System.out.println(Complex.repeat("-",80));
	}

	public static void limit_Minf(String strFunc, Function <Complex, Complex> func) {
		Complex limit = Complex.limit_Minf(func);
		System.out.print("limit(" + strFunc +", z,-inf) = ");
		if (limit != null) limit.println();
		else System.out.println("The limit doesn't exist");
		System.out.println(Complex.repeat("-",80));
	}

	public static void main(String[] args) {
		Complex.setFormatON(true);
		int boxSize = 65;
		
		Complex z = new Complex(-7);  
		int n = 80;

		System.out.println(Complex.boxTitleRandom(boxSize, "LIMIT TEST"));
		
		limit("t^(z+n)/(z+n)", t -> funcIntGamma(z,t,n), Complex.LIM_INF);
		limit_inf("t^(z+n)/(z+n)", t -> funcIntGamma(z,t,n));

		limit("t^(z+n)/(z+n)", t -> funcIntGamma(z,t,n), Complex.ZERO);
		/**/
	}
}
