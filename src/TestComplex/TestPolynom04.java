/*
 * java -Dfile.encoding=UTF-8 -classpath /home/ipserc/eclipse-workspace/complexarith/bin:/home/ipserc/eclipse-workspace/complexarith/classes TestComplex.TestPolynom01
 */
package TestComplex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToLongFunction;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;

public class TestPolynom04 {
	
	public static long intPart(double number) {
		String strIntPart;
		strIntPart = String.valueOf(number).split("\\.")[0];
		return Long.parseLong(strIntPart); 
	}
	
	public static boolean isPrime(double number) {
		double maxDiv = Math.ceil(Math.sqrt(number));
		double quot;

		quot = number / 2.0;
		if (quot - intPart(quot) == 0.0) return false;

		for (int i = 3; i <= maxDiv; i=i+2) {
			quot = number / i;
			//System.out.println(number + "/"+ i + " = " + quot);
			//System.out.println("intPart(" + quot + ") = " + intPart(quot));			
			if (quot - intPart(quot) == 0.0) return false;
		}
		return true;
	}
	
	public static double calcPrime(double limInf, double limSup) {
		int maxIter = 17;
		
		for (int i = 0; i < maxIter; ++i) {
			if (isPrime(limInf+i)) return limInf+i;
			if (isPrime(limSup-i)) return limSup-i;
		}
		return 0.0;
	}
	
	public static void guessPrime(Polynom primeApprox, int ordinal) {
		Complex primecalc;
		double primeceil, primefloor;
		double calcPrime;

		primecalc = primeApprox.eval(ordinal+1);
    	primecalc.println("Prime Numbre("+ordinal+")=");
    	primeceil = Math.ceil(primecalc.rep());
    	primefloor = Math.floor(primecalc.rep());
    	
    	System.out.println("Prime Ceil("+ordinal+")="+primeceil );
    	System.out.println("Prime Floor("+ordinal+")="+primefloor );
    	
    	calcPrime = calcPrime(primefloor, primeceil);
    	System.out.println("Calc Prime("+ordinal+")="+calcPrime);
	}

	public static void main(String[] args) {
		Polynom primeApprox;		
		int boxSize = 65;
		List<MatrixComplex> pointsList;
		int limCinf = 1;
		int limCsup = 7;
		int ordinal = 100;
		int degree;

		Complex.setFormatOFF();
		Complex.setScientificON(12, true);
		//Complex.setFixedON(5);
		System.out.printf("zero_treshold:%g\n", Complex.zero_treshold());
		System.out.printf("zero_treshold_exact:%g\n", Complex.zero_treshold_exact());
		Complex.zero_threshold_exact_prec(1E-120);
		System.out.printf("zero_treshold:%g\n", Complex.zero_treshold());
		System.out.printf("zero_treshold_exact:%g\n", Complex.zero_treshold_exact());
		/*
		Complex.restorePrecisionFactorySettings();
		System.out.printf("zero_treshold:%g\n", Complex.zero_treshold());
		System.out.printf("zero_treshold_exact:%g\n", Complex.zero_treshold_exact());
		*/
		Complex.exact(true);
		
    	System.out.println(Complex.boxTitle1(boxSize, "APPROXIMATION INTERPOLATION TO FIND PRIME NUMBERS BASED IN ITS ORDINAL"));
    	/*
    	primeApprox = new Polynom(""
    			+ "0.000000000000E+00,0.000000000000E+00,0.000000000000E+00,0.000000000000E+00,0.000000000000E+00,0.000000000000E+00,0.000000000000E+00,"
    			+ "0.000000000000E+00,0.000000000000E+00,0.000000000000E+00,0.000000000000E+00,0.000000000000E+00,0.000000000000E+00,-"
    			+ "3.010000000000E-06,1.360800000000E-04,-3.953330000000E-03,6.502902000000E-02,-4.103768300000E-01,2.820199170000E+00,1.000000000000E+00");
    			*/
    	/*
    	primeApprox = new Polynom("3.135403480346E-40-3.839761836241E-56i,-1.473786144888E-36+1.804867484965E-52i,3.183819973792E-33-3.564348882420E-49i,-4.190343901615E-30+5.131691246439E-46i,3.755173836915E-27-3.913448619426E-43i,-2.425793420273E-24+2.970740147530E-40i,1.166522729055E-21-1.146987654674E-37i,-4.252251353960E-19+5.207506009797E-35i,1.185386943046E-16-1.111613619191E-32i,-2.531365204131E-14+3.100028294712E-30i,4.122060599193E-12-3.720703300928E-28i,-5.062663929901E-10+6.200313491255E-26i,4.604281745168E-08-4.032631608791E-24i,-3.014404710181E-06+3.696376190481E-22i,1.360821584608E-04-1.161466232530E-20i,-3.953325629289E-03+4.910702119015E-19i,6.502901564340E-02-5.232571605492E-18i,-4.103768319015E-01+7.315878969343E-17i,2.820199170940E+00-2.458528006649E-17i,1.000000000000E+00");
		Prime Numbre(100)=5.28471E+02
		Prime Numbre(100)=5.28471E+02
		*/
    	
    	primeApprox = new Polynom(""
    			+ "3.135403480346E-40,"
    			+ "-1.473786144888E-36,"
    			+ "3.183819973792E-33,"
    			+ "-4.190343901615E-30,"
    			+ "3.755173836915E-27,"
    			+ "-2.425793420273E-24,"
    			+ "1.166522729055E-21,"
    			+ "-4.252251353960E-19,"
    			+ "1.185386943046E-16,"
    			+ "-2.531365204131E-14,"
    			+ "4.122060599193E-12,"
    			+ "-5.062663929901E-10,"
    			+ "4.604281745168E-08,"
    			+ "-3.014404710181E-06,"
    			+ "1.360821584608E-04,"
    			+ "-3.953325629289E-03,"
    			+ "6.502901564340E-02,"
    			+ "-4.103768319015E-01,"
    			+ "2.820199170940E+00,"
    			+ "1.000000000000E+00");

    	System.out.println(Complex.boxTextRandom(boxSize, ""));
    	primeApprox.println();
    	guessPrime(primeApprox, 500);
    	guessPrime(primeApprox, 510);
    	guessPrime(primeApprox, 520);
    	
    	/*
    	pointsList = new ArrayList<MatrixComplex>();
    	for (int i = limCinf; i <= limCsup; ++i) {
    		
    	}
    	*/

	}
}
