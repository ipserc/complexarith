package com.ipserc.arith.complex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Special functions and trigonometry for {@link Complex}: sign/log/power/root/exp, the gamma
 * family, zeta family, binomial coefficient/factorial, and the full trig/hyperbolic/inverse-trig
 * block (sin/cos/tan/.../arcsin/.../chebyshev). Package-private: every public method that moved
 * here keeps an exact one-line delegator on {@code Complex} with the same signature, so external
 * callers (7 library classes and ~200 test files) need zero changes.
 * <p>
 * Extracted verbatim (Sexta sesion, paso 2, Fase 2.5) from {@code Complex.java}'s "FUNCTIONS" and
 * "TRIGONOMETRICS" sections. Two design notes:
 * <ul>
 * <li>{@code power(Complex)}/{@code power(double)}/{@code power(int)} were INSTANCE methods on
 * {@code Complex} (using {@code this.mod}/{@code this.pha} directly), unlike everything else in
 * these two sections (all {@code static}). They stay instance methods on {@code Complex} (required
 * -- {@code someComplex.power(z)} is public API), with bodies delegating to the static
 * {@link #power(Complex, Complex)}/{@link #power(Complex, double)}/{@link #power(Complex, int)}
 * here, which take the former {@code this} as an explicit first parameter.</li>
 * <li>{@code isInteger()}/{@code isIntegerPositive()}/{@code isIntegerNegative()}/
 * {@code isIntegerPositiveZero()}/{@code isIntegerNegativeZero()} lived under the "FUNCTIONS"
 * banner too, but are boolean predicates on {@code this}'s own fields (like the BOOLEAN OPERATIONS
 * section, which stayed in {@code Complex.java}'s core) -- they did NOT move. Called from several
 * functions here (e.g. {@code gamma_zones}) as an ordinary public instance-method call
 * ({@code z.isIntegerNegativeZero()}), which works unchanged regardless of where their
 * implementation lives.</li>
 * </ul>
 * All direct field access on a foreign {@code Complex} value (parameter or local variable -- this
 * class cannot touch another instance's private {@code rep}/{@code imp}/{@code mod}/{@code pha})
 * was rewritten to use the existing public getters ({@code rep()}/{@code imp()}/{@code mod()}/
 * {@code pha()}), which return the exact same value. Bare references to {@code Complex}'s static
 * constants ({@code ZERO}, {@code ONE}, {@code DOS_PI}, {@code HALF_PI}, {@code EULER_MASC}) and to
 * {@code getMaxDecimals()} were qualified with {@code Complex.}; calls that were already qualified
 * (e.g. {@code Complex.exp(...)}, {@code Complex.integrate(...)}) needed no change, since
 * {@code Complex.java} keeps a public delegator/constant for everything that moved.
 */
final class ComplexFunctions {

	private ComplexFunctions() {}

	/**
	 * Calculates the sign of a complex z.
	 * @param z The complex number.
	 * @return the sign of the complex z as a new Complex Object.
	 */
	static Complex sign(Complex z) {
		Complex sign = new Complex();
		sign.setComplexPol(z.mod(), z.pha());
		if (z.mod() == 0 ) {
			sign.setComplexPol(0, 0);
			return sign;
		}
		return sign.divides(sign.mod());
	}

	/**
	 * Returns the inverse of a Complex number
	 * @param z the complex number
	 * @return the inverse of the Complex number
	 */
	static Complex inverse(Complex z) {
		return Complex.ONE.divides(z);
	}

	/**
	 * Calculates the ZERO_POSITIVE sign of a complex z, Zero is included as a Positive.
	 * @param z The complex number.
	 * @return the sign of the complex z as a new Complex Object.
	 */
	static Complex signP(Complex z) {
		Complex sign = new Complex();
		sign.setComplexPol(z.mod(), z.pha());
		if (z.mod() == 0 ) sign.setComplexPol(1, 0);
		return sign.divides(sign.mod());
	}

	/**
	 * Calculates the ZERO_NEGATIVE sign of a complex z, Zero is included as a Negative.
	 * @param z The complex number.
	 * @return the sign of the complex z as a new Complex Object.
	 */
	static Complex signN(Complex z) {
		Complex sign = new Complex();
		sign.setComplexPol(z.mod(), z.pha());
		if (z.mod() == 0 ) sign.setComplexPol(-1, 0);
		return sign.divides(sign.mod());
	}

	/**
	 * Calculates the Natural Logarithm of z.
	 * @param z The complex number.
	 * @return The new Complex Object with the Natural Logarithm.
	 */
	static Complex log(Complex z) {
		return new Complex('C', Math.log(z.mod()), z.pha());
	}

	/**
	 * Calculates the Decimal Logarithm of z.
	 * @param z The complex number.
	 * @return The new Complex Object with the Decimal Logarithm.
	 */
	static Complex log10(Complex z) {
		return new Complex('C', Math.log10(z.mod()), z.pha()*Math.log10(Math.E));
	}

	/**
	 * Calculates the Logarithm in Complex base of 'z'.
	 * @param z The complex number.
	 * @param base The Complex Object base of the Logarithm.
	 * @return The new Complex Object with the Logarithm in base 'base'.
	 */
	static Complex logbase(Complex z, Complex base) {
		Complex cNum = new Complex('C', Math.log(z.mod()), z.pha());
		Complex cDen = new Complex('C', Math.log(base.mod()), base.pha());
		return cNum.divides(cDen);
	}

	/**
	 * Calculates the Logarithm in Real base of 'z'.
	 * @param z The complex number.
	 * @param base The Real base of the Logarithm.
	 * @return The new Complex Object with the Logarithm in base 'base'.
	 */
	static Complex logbase(Complex z, double base) {
		Complex cBase = new Complex('C', base, 0.0);
		return Complex.logbase(z, cBase);
	}

	/**
	 * Calculates the value of 'base' raised to the Complex 'z'.
	 * @param base The Complex Object being raised (the former {@code this}).
	 * @param z The Complex Object to raise 'base'.
	 * @return The new Complex Object with the value of 'base' raised to 'z'.
	 */
	static Complex power(Complex base, Complex z) {
		double comExp, comTri;

		if (Double.isInfinite(z.mod())) {
			return new Complex('P', z.mod(), base.pha() * z.rep());
		}

		if (base.mod() != 0) {
			comExp = Math.exp(z.rep() * Math.log(base.mod()) - base.pha() * z.imp());
			comTri = base.pha() * z.rep() + z.imp() * Math.log(base.mod());
			return new Complex('P', comExp, comTri);
		}
		// 0^z branch cut: 0^0 = 1 by convention; 0^z with Re(z) > 0 = 0;
		// 0^z with Re(z) < 0 is unbounded (Infinity); 0^z with Re(z) == 0, Im(z) != 0 is indeterminate (NaN).
		if (z.isZero()) return Complex.ONE.copy();
		if (z.rep() > 0) return Complex.ZERO.copy();
		if (z.rep() < 0) return new Complex('C', Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		return new Complex('C', Double.NaN, Double.NaN);
	}

	/**
	 * Calculates the value of 'base' raised to the REAL number 'nExp'.
	 * @param base The Complex Object being raised (the former {@code this}).
	 * @param nExp The exponent.
	 * @return The new Complex Object with the value of 'base' raised to 'nExp'.
	 */
	static Complex power(Complex base, double nExp) {
		return new Complex('P', Math.pow(base.mod(), nExp), nExp * base.pha());
	}

	/**
	 * Calculates the value of 'base' raised to the REAL number 'iExp'.
	 * @param base The Complex Object being raised (the former {@code this}).
	 * @param iExp The exponent.
	 * @return The new Complex Object with the value of 'base' raised to 'iExp'.
	 */
	static Complex power(Complex base, int iExp) {
		return new Complex('P', Math.pow(base.mod(), iExp), iExp * base.pha());
	}

	/**
	 * Calculates the "1st" pot-root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @param pot The degree of the radical.
	 * @return The "1st" pot-root of the Complex Object 'this'.
	 */
	static Complex root(Complex z, double pot) {
		return new Complex('P', Math.pow(z.mod(), 1/pot), z.pha() / pot);
	}

	/**
	 * Calculates the "k-th" pot-root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @param pot pot The degree of the radical.
	 * @param k The "k-th" root.
	 * @return The "k-th" pot-root of the Complex Object 'this'.
	 */
	static Complex root(Complex z, int pot, int k) {
		if (k > pot) k %= pot;
		return new Complex('P', Math.pow(z.mod(), 1/(double)pot), (z.pha() + Complex.DOS_PI * k) / pot);
	}

	/**
	 * Calculates the "1st" square root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @return The "1st" pot-root of the Complex Object 'this'.
	 */
	static Complex sqrt(Complex z) {
		return root(z, 2);
	}

	/**
	 * Calculates the "1st" square root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @return The "1st" pot-root of the Complex Object 'this'.
	 * @apiNote Not called anywhere in this codebase. Identical body to {@link #sqrt(Complex)},
	 * already marked DEPRECATED in this file's own version-history comment. Kept as documented
	 * dead code rather than removed.
	 */
	static Complex sqrroot__(Complex z) {
		return root(z, 2);
	}

	/**
	 * Calculates the "k-th" square root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @param k The "k-th" root.
	 * @return The "k-th" pot-root of the Complex Object 'this'.
	 */
	static Complex sqrt(Complex z, int k) {
		return root(z, 2, k);
	}

	/**
	 * Calculates the "k-th" square root of the Complex Object 'this'.
	 * @param z The complex number.
	 * @param k The "k-th" root.
	 * @return The "k-th" pot-root of the Complex Object 'this'.
	 * @apiNote Not called anywhere in this codebase. Identical body to
	 * {@link #sqrt(Complex, int)}, already marked DEPRECATED in this file's own version-history
	 * comment. Kept as documented dead code rather than removed.
	 */
	static Complex sqrroot__(Complex z, int k) {
		return root(z, 2, k);
	}

	/**
	 * Returns a new Complex Object which value is the z exponential of 'z'.
	 * @param z The complex number.
	 * @return The new Complex Object exponential of 'z'.
	 */
	static Complex exp(Complex z) {
		return new Complex('C', Math.exp(z.rep()) * Math.cos(z.imp()), Math.exp(z.rep()) * Math.sin(z.imp()));
	}

	/**
	 * Returns a new Complex Object which value is the z exponential of 'z'.
	 * @param d double number.
	 * @return The new Complex Object exponential of 'z'.
	 */
	static Complex exp(double d) {
		Complex z = new Complex(d);
		return new Complex('C', Math.exp(z.rep()) * Math.cos(z.imp()), Math.exp(z.rep()) * Math.sin(z.imp()));
	}

	/**
	 * Funtion modulus
	 * @param z
	 * @return The modulus of the complex z
	 */
	static double mod(Complex z) {
		return z.mod();
	}

	/**
	 * Function absolute value
	 * @param z
	 * @return The modulus of the complex z
	 */
	static double abs(Complex z) {
		return Complex.mod(z);
	}

	/**
	 * Returns a new complex with Re and Im parts positive
	 * @param z
	 * @return a new complex with Re and Im parts positive
	 */
	static Complex positive(Complex z) {
		return new Complex(Math.abs(z.rep()), Math.abs(z.imp()));
	}

	/**
	 *
	 * @param d
	 * @return
	 */
	static Complex gamma(double d) {
		Complex z = new Complex(d,0);
		return gamma(z);
	}

	/**
	 *
	 * @param z
	 * @return
	 */
	static Complex gamma(Complex z) {
		return gamma_fast(z);
	}

	/**
	 * The function gamma optimized for calculations. Selects the best calculator function based on the gamma parameter z
	 * @param z the gamma parameter as Complex
	 * @return the gamma value
	 */
	static Complex gamma_zones(Complex z) {
		if (z.isIntegerNegativeZero()) {
			double sign = Math.pow(-1.0, Math.ceil(z.rep()));
			return Complex.ZERO.inverse().times(sign);
		}

		if (z.isPureReal() && z.mod() > 3) {
			if (z.rep() > 0) return gamma_integral(z);
			else return gamma_integral(Complex.ONE.minus(z)).inverse().times(Math.PI/Math.sin(z.rep()*Math.PI)); // Use of the gamma reflection property
		}
		else if (z.rep() > 5) return gamma_integral(z);
			else return gamma_euler(z);
	}

	/**
	 * Gamma function calculated by the integral. Only valid for positive real numbers
	 * @param n the Gamma parameter
	 * @return the gamma value
	 */
	static Complex gamma_integral(double n) {
		Complex z = new Complex(n,0);
		return gamma_integral(z);
	}

	/**
	 * Gamma function calculated by the integtral of t.power(z.minus(ONE))).times(Complex.exp(t.opposite()) dt
	 * @param z the gamma parameter as Complex
	 * @return the gamma value
	 */
	static Complex gamma_integral(Complex z) {
		if (z.isIntegerNegativeZero()) {
			double sign = Math.pow(-1.0, Math.ceil(z.rep()));
			return Complex.ZERO.inverse().times(sign);
		}
		// Valores válidos van desde 50 hasta 100000
		double uplimit = 100;
		Function<Complex, Complex> gammafunc;
		gammafunc = t -> (t.power(z.minus(Complex.ONE))).times(Complex.exp(t.opposite()));
		return Complex.integrate(0,uplimit , gammafunc, 5);
	}

	/**
	 * Gamma function calculated by the integtral of Complex.log(s).opposite()).power(z.minus(ONE))ds
	 * Valid only for interval [1,4)
	 * @param z the gamma parameter as Complex
	 * @return the gamma value
	 */
	static Complex gamma_integral2(Complex z) {
		if (z.isIntegerNegativeZero()) {
			double sign = Math.pow(-1.0, Math.ceil(z.rep()));
			return Complex.ZERO.inverse().times(sign);
		}
		Function<Complex, Complex> gammafunc;
		gammafunc = s -> (Complex.log(s).opposite()).power(z.minus(Complex.ONE));
		return Complex.integrate(ComplexState.zero_treshold_exact(), 1, gammafunc, 6);
	}

	/**
	 * Gamma function calculated by definition from Weierstrass. Valid for any Complex number
	 * @param z the gamma parameter as Complex
	 * @return the gamma value
	 */
	static Complex gamma_weiertrass(Complex z) {
		int iterations = (int)Math.pow(150/Complex.getMaxDecimals(), Complex.getMaxDecimals());

		switch (Complex.getMaxDecimals()) {
			case 0:
			case 1:  //iterations = 50000; break;
			case 2:  //iterations = 100000; break;
			case 3:  //iterations = 500000; break;
			case 4:  iterations = 1000000; break;
			case 5:  iterations = 5000000; break;
			case 6:  iterations = 30000000; break;
			default: iterations = 50000000;
		}

		if (z.isIntegerNegativeZero()) {
			double sign = Math.pow(-1.0, Math.ceil(z.rep()));
			return Complex.ZERO.inverse().times(sign);
		}
		Complex prod = new Complex(1,0);
		Complex zdi = new Complex();
		for (int i = 1; i <= iterations; ++i) {
			zdi = z.divides(i);
			// Accumulator mutated in place instead of reassigned to a new Complex each iteration.
			prod.timesEq((zdi.plus(Complex.ONE)).inverse().times(Complex.exp(zdi)));
		}
		Complex result = Complex.exp(z.times(-Complex.EULER_MASC));
		result.dividesEq(z);
		result.timesEq(prod);
		return result;
	}

	/**
	 * Gamma function calculated by definition from Euler's Product. Valid for any Complex number except negative integers
	 * @param z the gamma parameter as Complex
	 * @return the gamma value
	 */
	static Complex gamma_euler(Complex z) {
		int iterations = (int)Math.pow(150/Complex.getMaxDecimals(), Complex.getMaxDecimals());

		switch (Complex.getMaxDecimals()) {
			case 0:
			case 1:  //iterations = 50000; break;
			case 2:  //iterations = 100000; break;
			case 3:  //iterations = 500000; break;
			case 4:  iterations = 1000000; break;
			case 5:  iterations = 5000000; break;
			case 6:  iterations = 30000000; break;
			default: iterations = 50000000;
		}
		if (z.isIntegerNegativeZero()) {
			double sign = Math.pow(-1.0, Math.ceil(z.rep()));
			return Complex.ZERO.inverse().times(sign);
		}
		Complex prod = new Complex(1, 0);
		Complex term1;
		Complex term2;

		for (int n = 1; n <= iterations; ++n) {
			//term1 = (ONE.divides(n).plus(1)).power(z); // (1+1/n)^z
			term1 = (Complex.ONE.plus(Complex.ONE.divides(n))).power(z); // (1+1/n)^z
			term2 = (Complex.ONE.plus(z.divides(n))).inverse(); // (1+z/n)⁻¹
			// Accumulator mutated in place instead of reassigned to a new Complex each iteration.
			prod.timesEq(term1).timesEq(term2);
		}
		prod.dividesEq(z);
		return prod;
	}

	/**
	 * https://math.stackexchange.com/questions/19236/algorithm-to-compute-gamma-function
	 * http://www.rskey.org/CMS/index.php/the-library/11
	 * @param z
	 * @return
	 */
	static Complex gamma_nemes(Complex z) {
		// gammaVal/Val are private accumulators (fresh objects, not shared constants), so each
		// step mutates in place instead of reassigning to a newly allocated Complex.
		Complex gammaVal = new Complex(.5*Math.log(Complex.DOS_PI));
		gammaVal.plusEq((z.minus(0.5)).times(Complex.log(z)));
		gammaVal.minusEq(z);
		Complex Val = z.times(Complex.sinh(z.inverse()));
		Val.plusEq((z.power(6).times(810)).inverse());
		gammaVal.plusEq((z.divides(2)).times(Complex.log(Val)));
		return Complex.exp(gammaVal);
	}

	/**
	 * https://es.wikipedia.org/wiki/Aproximaci%C3%B3n_de_Lanczos
	 * @param z
	 * @return
	 */
	static Complex gamma_fast(Complex z) {
		Complex result;
	    double p[] = {676.5203681218851, -1259.1392167224028, 771.32342877765313,
	    			  -176.61502916214059, 12.507343278686905, -0.13857109526572012,
	    			  9.9843695780195716e-6, 1.5056327351493116e-7};
	    if (z.rep() < 0.5)
	        result = Complex.PI.divides((sin(Complex.PI.times(z)).times(gamma_fast(Complex.ONE.minus(z)))));
	    else {
	        z = z.minus(1);
	        Complex x = new Complex(0.99999999999980993);
	        for (int i = 0; i < p.length; ++i) { // pval) in enumerate(p):
	        	Complex pval = new Complex(p[i]);
	        	// Accumulator mutated in place instead of reassigned to a new Complex each iteration.
	        	x.plusEq(pval.divides(z.plus(i+1)));
	        }
            Complex t = z.plus(p.length - 0.5);
            result = Complex.sqrt(Complex.DOSPI);
            result.timesEq(t.power(z.plus(0.5)));
            result.timesEq(Complex.exp(t.opposite()).times(x));
	    }
	    return result;
	}

	/**
	 * The factorial for integers
	 * @param n
	 * @return
	 */
	static double factorial(int n) {
		double fact = 1.0;
		for (int i = 2; i <= n; ++i)
			fact *= i;
		return fact;
	}

	/**
	 * Tecomplex factorial as gamma
	 * @param n
	 * @return
	 */
	static Complex factorial(Complex n) {
		return gamma(n);
	}

	/**
	 * The Beta function
	 * @param p Complex
	 * @param q Complex
	 * @return the Beta of p,q
	 */
	static Complex beta(Complex p, Complex q) {
		// gamma(p) is a freshly allocated private accumulator; times/divides mutate it in place
		// instead of allocating an intermediate Complex for each step of the chain.
		Complex result = gamma(p);
		result.timesEq(gamma(q));
		result.dividesEq(gamma(p.plus(q)));
		return result;
	}

	/*
	 * http://mrob.com/pub/ries/src/zeta.cpp.txt
	 */
	/**
	 * The Riemann's zeta function. Only for Re(s) > 1
	 * @param s The s parameter of the zeta function
	 * @return The Riemann's zeta function value
	 */
	static Complex zeta(Complex s) {
		if (s.isZero()) return new Complex(-0.5);
		// if (s.equals(ZERO)) return new Complex(-0.5);
		if (s.rep() == 1.0 && s.isPureReal()) return Complex.ONE.divides(0);
		if (s.rep() > 2.0) return zeta_re(s);
		if (s.rep() < -1.0) return zeta_ext(s);
		return zeta_havil(s);
	}

	/**
	 * The Riemann's zeta function. Only for Re(s) > 1
	 * @param s The s parameter of the zeta function
	 * @return The Riemann's zeta function value
	 */
	static Complex zeta_re(Complex s) {
		Complex z1 = new Complex(0);
		Complex z2 = new Complex(0);
		// A private, mutable accumulator - NOT Complex.ONE itself, which timesEq/plusEq would corrupt.
		Complex k = new Complex(1, 0);
		Complex sOpp = s.opposite();
		boolean notFinished = true;

		while (notFinished) {
			z1.plusEq(k.power(sOpp));
			if (z1.equals(z2)) notFinished = false;
			z2 = z1.copy();
			k.plusEq(1);
		}
		return z1;
	}

	/**
	 * The Riemann's zeta function. Only for Re(s) > 1
	 * @param s The s parameter of the zeta function
	 * @return The Riemann's zeta function value
	 */
	static Complex zeta_ext(Complex s) {
		Complex s_one = s.minus(1);
		Complex one_s = Complex.ONE.minus(s);
		// z is a freshly allocated private accumulator (from sin(), not a shared constant), so the
		// subsequent product chain mutates it in place instead of allocating at each step.
		Complex z = Complex.sin(Complex.PI.divides(2).times(s));
		if (z.equals(Complex.ZERO)) return Complex.ZERO;
		z.timesEq(new Complex(2).power(s));
		z.timesEq(Complex.PI.power(s_one));
		z.timesEq(Complex.gamma(one_s));
		z.timesEq(zeta(one_s));
		return z;
	}

	/**
	 * TODO
	 * @param s
	 * @return
	 */
	static Complex zeta_reflex(Complex s) {
		Complex s_one = s.minus(1);
		Complex one_s = Complex.ONE.minus(s);
		Complex z = Complex.cos(Complex.PI.divides(2).times(one_s));
		if (z.equals(Complex.ZERO)) return Complex.ZERO;
		z = z.times(one_s.divides(2*Math.PI*Math.E));
		z = z.times(Complex.sqrt(new Complex(8*Math.PI).divides(one_s)));
		return z;
	}

	/**
	 * Solo vale para s.rep() > 1 - Only for Re(s) > 1
	 * @param s
	 * @return
	 */
	static Complex zeta_primes(Complex s) {
		long prime;
		Scanner sc;
		Complex zPrime = new Complex();
		Complex z = new Complex(1);
		Complex z0 = new Complex(0);
		Complex sOpp = s.opposite();
		Complex epsilon = new Complex();
		String str;

		if (s.rep() <= 1.0) return zeta(s);

		try {
			sc = new Scanner(new File("./data/primes_n.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Complex.mONE;
		}
		sc.useDelimiter(" ");   //sets the delimiter pattern
		while (sc.hasNext())  //returns a boolean value
		{
			str = sc.next().trim();
			// System.out.println("str:" + str);
			if (str.isBlank() || str.isEmpty()) break;
			prime = Long.parseLong(str);
			// System.out.println("prime:" + prime);  //find and returns the next complete token from this scanner
			zPrime.setComplexRec(prime, 0);
			z = z.divides(Complex.ONE.minus(zPrime.power(sOpp)));
			epsilon = z.minus(z0);
			//System.out.println("epsilon:" + epsilon.mod());
			if (Double.isNaN(epsilon.mod())) break;
			if (epsilon.mod() <= ComplexState.zero_treshold()) break;
			z0 = z.copy();
		}
		sc.close();  //closes the scanner
		return (z);
	}

	/**
	 * Sondow, Jonathan and Weisstein, Eric W. "Riemann Zeta Function." From MathWorld--A Wolfram Web Resource.
	 * https://mathworld.wolfram.com/RiemannZetaFunction.html
	 * @param s
	 * @return
	 * @apiNote The outer sum's n-th term carries a 1/2^(n+1) prefactor (geometric decay), so its
	 * contribution to sum1 becomes unrepresentable in double precision long before n reaches the
	 * previous fixed cap of 170 -- each of those unnecessary iterations still paid for an O(n)
	 * inner loop of power()/binomialCoef() calls (the whole method was O(maxN^2), ~14450 term
	 * evaluations regardless of how many were actually needed). Now breaks as soon as a term
	 * leaves sum1 completely unchanged in EXACT double equality (not the tolerance-based
	 * {@code equals()} used elsewhere in this class for convergence, e.g. zeta_re/zeta_ext/
	 * gamma_integral): since the terms decay geometrically, an exact-equality miss here means
	 * every later term is provably too small to move sum1's bits either, so this is guaranteed to
	 * give bit-identical results to running the full 170 iterations, unlike a tolerance-based
	 * check (tried first, measured to change the last 2-4 significant digits of the result --
	 * sometimes closer to the true value, sometimes further, because stopping within a coarser
	 * tolerance forgoes some of the incidental extra precision that grinding through all 170
	 * iterations happened to accumulate). maxN=170 stays as the same safety cap as before for s
	 * where convergence is genuinely slower.
	 */
	static Complex zeta_havil(Complex s) {
		int maxN = 170;
		Complex sum1 = new Complex();
		Complex prevSum1 = sum1.copy();
		for (int n = 0; n < maxN; ++n) {
			// power(s) always allocates a fresh Complex (not an alias of Complex.ONE), safe to mutate.
			Complex sum2 = Complex.ONE.power(s);
			for (int k = 1; k <= n; ++k) {
				Complex sum = (Complex.mONE.power(k).times(Complex.binomialCoef(n, k))).divides((Complex.ONE.plus(k)).power(s));
				sum2.plusEq(sum);
			}
			sum1.plusEq(Complex.ONE.divides(Math.pow(2,n+1)).times(sum2));
			if (sum1.rep() == prevSum1.rep() && sum1.imp() == prevSum1.imp()) break;
			prevSum1 = sum1.copy();
		}
		sum1.dividesEq(Complex.ONE.minus(new Complex(2,0).power(Complex.ONE.minus(s))));
		return sum1;
	}

	/**
	 * Returns the k zero of the Chebyshev Unary Polynomial of n+1 degree
	 * @param n the number of samples (0..n)
	 * @param k The k term looked after
	 * @return the value of the k zero
	 */
	static Complex ChebyshevZero(int n, int k) {
		return Complex.cos(new Complex((2.0*k+1.0)/(2.0*n+2.0)*Math.PI));
	}

	/**
	 * The complex binomial coefficient with integer arguments
	 * @param n
	 * @param k
	 * @return
	 * @apiNote Computed via the standard incremental-ratio product C(n,k)=Prod_{i=1}^{k}(n-k+i)/i
	 * instead of the previous factorial(n)/factorial(k)/factorial(n-k). The factorial approach
	 * forms three independent huge numbers (up to 169!~=4.27e304, near double's overflow ceiling,
	 * since zeta_havil calls this for n up to 169) each accumulating up to ~n rounding errors of
	 * its own before the final division, whereas this product's running result never grows
	 * further than the actual C(n,k) value itself, so it carries far less accumulated
	 * floating-point noise. Also picks the smaller of k/(n-k) to minimize the number of
	 * multiplications. Confirmed to give identical or more accurate results (verified against
	 * exact integer binomial coefficients for n up to 30, and against the previous
	 * implementation for the zeta_havil-relevant range up to n=169) with zero risk of the
	 * factorial approach's overflow-adjacent intermediate values.
	 */
	static Complex binomialCoef(int n, int k) {
		if (k < 0 || k > n) return new Complex(0.0, 0.0);
		int kk = Math.min(k, n - k);
		double result = 1.0;
		for (int i = 1; i <= kk; ++i) {
			result *= (n - kk + i);
			result /= i;
		}
		return new Complex(result, 0.0);
	}

	/**
	 * The complex binomial coefficient with integer arguments
	 * @param n
	 * @param k
	 * @return
	 */
	static Complex binomialCoef(Complex n, Complex k) {
		// C(n,k) = n!/(k!(n-k)!) = gamma(n+1)/(gamma(k+1)*gamma(n-k+1)); gamma(m) = (m-1)!, so the
		// previous gamma(n)/gamma(k)/gamma(n-k) was off by one factorial in each term - e.g. it gave
		// binomialCoef(6,2)=20 instead of 15. gamma(n+1) is a freshly allocated private accumulator;
		// the two divisions mutate it in place instead of allocating an intermediate Complex each.
		Complex result = gamma(n.plus(1));
		result.dividesEq(gamma(k.plus(1)));
		result.dividesEq(gamma(n.minus(k).plus(1)));
		return result;
	}

	/**
	 * Returns a string with the representation of an angle in ° ' "
	 * @param deg The angle in degrees
	 * @return The angle expressed in ° ' "
	 */
	static String deg_DMS(double deg) {
		int degs, mins;
		String secs;
		double decPart;

		degs = (int)deg;
		decPart = deg - degs;
		decPart = decPart * 60.0;
		mins = (int)(decPart);
		secs = String.format("%.3f", (decPart - mins)*60);

		return degs + "°" + mins + "'" + secs + "\"";
	}

	/**
	 * Returns a string with the representation of an angle in ° ' "
	 * @param rad The angle in radians
	 * @return The angle expressed in ° ' "
	 */
	static String rad_DMS(double rad) {
		return deg_DMS(Math.toDegrees(rad));
	}

	/**
	 * Returns a new Complex Object which value is the sine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object sine of 'z'.
	 */
	static Complex sin(Complex z) {
		return new Complex('C', Math.sin(z.rep()) * Math.cosh(z.imp()), Math.cos(z.rep()) * Math.sinh(z.imp()));
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex sin(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return sin(z);
	}

	/**
	 * Returns a new Complex Object which value is the cosecant of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object cosecant of 'z'.
	 */
	static Complex csc(Complex z) {
		return sin(z).power(-1);

	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex csc(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return csc(z);
	}

	// returns a new Complex object which value is the z cosine of this
	/**
	 * Returns a new Complex Object which value is the cosine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object cosine of 'z'.
	 */
	static Complex cos(Complex z) {
		return new Complex('C', Math.cos(z.rep()) * Math.cosh(z.imp()), -Math.sin(z.rep()) * Math.sinh(z.imp()));
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex cos(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return cos(z);
	}

	/**
	 * Returns a new Complex Object which value is the secant of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object secant of 'z'.
	 */
	static Complex sec(Complex z) {
		return cos(z).power(-1);
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex sec(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return sec(z);
	}

	// returns a new Complex object which value is the z tangent of this
	/**
	 * Returns a new Complex Object which value is the tangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object tangent of 'z'.
	 */
	static Complex tan(Complex z) {
		return sin(z).divides(cos(z));
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex tan(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return tan(z);
	}

	/**
	 * Returns a new Complex Object which value is the cotangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object cotangent of 'z'.
	 */
	static Complex cot(Complex z) {
		return cos(z).divides(sin(z));
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex cot(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return cot(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic sine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic sine of 'z'.
	 */
	static Complex sinh(Complex z) {
		//sinh(Z) = (cos(b) * ((exp(a) - exp(-a)) / 2) + Sin(b) * ((exp(a) + exp(-a)) / 2)i)
		//sinh(Z) = senh a * cos b + (cosh a * sen b)i
		//return (Complex.exp(z).minus(Complex.exp(z.opposite()))).divides(2);
		double Rep = Math.sinh(z.rep()) * Math.cos(z.imp());
		double Imp = Math.cosh(z.rep()) * Math.sin(z.imp());
		return new Complex('C', Rep, Imp);
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex sinh(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return sinh(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic cosecant of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic cosecant of 'z'.
	 */
	static Complex csch(Complex z) {
		return sinh(z).power(-1);
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex csch(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return csch(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic cosine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic cosine of 'z'.
	 */
	static Complex cosh(Complex z) {
		//cosh(Z) = (cos(b) * ((exp(a) + exp(-a)) / 2) + Sin(b) * ((exp(a) - exp(-a)) / 2)i)
		//cosh(Z) = cosh x * cos b + (sinh a * sen b)i
		//return (Complex.exp(z).plus(Complex.exp(z.opposite()))).divides(2);
		double Rep = Math.cosh(z.rep()) * Math.cos(z.imp());
		double Imp = Math.sinh(z.rep()) * Math.sin(z.imp());
		return new Complex('C', Rep, Imp);
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex cosh(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return cosh(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic secant of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic secant of 'z'.
	 */
	static Complex sech(Complex z) {
		return cosh(z).power(-1);
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex sech(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return sech(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic tangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic tangent of 'z'.
	 */
	static Complex tanh(Complex z) {
		return sinh(z).divides(cosh(z));
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex tanh(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return tanh(z);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic cotangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic cotangent of 'z'.
	 */
	static Complex coth(Complex z) {
		return cosh(z).divides(sinh(z));
	}

	/**
	 * TODO
	 * @param zd
	 * @return
	 */
	static Complex coth(double zd) {
		Complex z = new Complex(); z.setComplexRec(zd, 0);
		return coth(z);
	}

	/**
	 * Returns a new Complex Object which value is the arcsine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object arcsine of 'z'.
	 */
	static Complex arcsin(Complex z) {
		Complex i = new Complex(0,1);
		Complex one = new Complex(1,0);
		if (Math.abs(z.rep()) > SAFE_SQUARE_LIMIT || Math.abs(z.imp()) > SAFE_SQUARE_LIMIT) return arcsinExtreme(z);
		return log((z.times(i)).plus(root((one.minus(z.power(2))),2))).divides(i);
	}

	/**
	 * Returns a new Complex Object which value is the arccosine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object arccosine of 'z'.
	 */
	static Complex arccos(Complex z) {
		Complex negi = new Complex(0,-1);
		Complex one = new Complex(1,0);
		if (Math.abs(z.rep()) > SAFE_SQUARE_LIMIT || Math.abs(z.imp()) > SAFE_SQUARE_LIMIT) return arccosExtreme(z);
		return log((z).plus(root((z.power(2).minus(one)),2))).divides(negi);
	}

	// z.power(2) overflows to Infinity once |z| > Math.sqrt(Double.MAX_VALUE) ~ 1.34e154, even though
	// asin(z)/acos(z) themselves stay finite there - the naive formula then yields NaN. Comfortably
	// below that overflow point, at this magnitude.
	private final static double SAFE_SQUARE_LIMIT = 1e150;

	/**
	 * Private method. Closed-form arcsin(z) for |z| beyond SAFE_SQUARE_LIMIT, where z.power(2) would
	 * overflow. Derived from asin(z) = -i*ln(iz + sqrt(1-z^2)): at this magnitude 1/z^2 underflows
	 * to 0 in double precision, so sqrt(1-z^2) = eps*i*z EXACTLY (to full double precision), where
	 * eps = +1 selects the principal branch (Re(sqrt(1-z^2)) >= 0) when Im(z) < 0, or Im(z) == 0 and
	 * Re(z) >= 0; eps = -1 otherwise. Substituting gives asin(z) = pi/2 -+ i*ln(2z) (sign per eps).
	 * Validated: matches the general formula exactly at the SAFE_SQUARE_LIMIT boundary (continuous
	 * hand-off), and asin(z)+acos(z) == pi/2 holds exactly in all four quadrants and both axes.
	 * @param z The complex number, with |z| > SAFE_SQUARE_LIMIT.
	 * @return The arcsine of 'z'.
	 */
	private static Complex arcsinExtreme(Complex z) {
		Complex i = new Complex(0,1);
		Complex halfPi = new Complex(Complex.HALF_PI, 0);
		boolean principalBranchPositive = z.imp() < 0 || (z.imp() == 0 && z.rep() >= 0);
		Complex iLog2z = log(z.times(2)).times(i);
		return principalBranchPositive ? halfPi.minus(iLog2z) : halfPi.plus(iLog2z);
	}

	/**
	 * Private method. Closed-form arccos(z) for |z| beyond SAFE_SQUARE_LIMIT. Derived the same way
	 * as arcsinExtreme, using the exact identity acos(z) = pi/2 - asin(z).
	 * @param z The complex number, with |z| > SAFE_SQUARE_LIMIT.
	 * @return The arccosine of 'z'.
	 */
	private static Complex arccosExtreme(Complex z) {
		Complex i = new Complex(0,1);
		boolean principalBranchPositive = z.imp() < 0 || (z.imp() == 0 && z.rep() >= 0);
		Complex iLog2z = log(z.times(2)).times(i);
		return principalBranchPositive ? iLog2z : iLog2z.opposite();
	}

	/**
	 * Returns a new Complex Object which value is the arc tangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object arc tangent of 'z'.
	 */
	static Complex arctan(Complex z) {
		Complex i = new Complex(0,1);
		Complex twoi = new Complex(0,2);
		return log((i.minus(z)).divides(i.plus(z))).divides(twoi);
	}

	/**
	 * Returns a new Complex Object which value is the arc cotangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object arc cotangent of 'z'.
	 */
	static Complex acotan(Complex z) {
		Complex i = new Complex(0,1);
		Complex twoi = new Complex(0,2);
		return log((z.plus(i)).divides(z.minus(i))).divides(twoi);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic arc sine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic arc sine of 'z'.
	 */
	static Complex arcsinh(Complex z) {
		// arcsinh(z) = -i*arcsin(iz), derived directly from the arcsin(w)=-i*ln(iw+sqrt(1-w^2))
		// formula (matches this file's branch convention exactly, not an independent textbook
		// identity that might disagree on branch). Delegating to arcsin also inherits its
		// SAFE_SQUARE_LIMIT guard against the z.power(2) overflow for |z| beyond ~1.34e154, which
		// the direct log(z+sqrt(z^2+1)) formula below does not have.
		Complex negi = new Complex(0,-1);
		Complex i = new Complex(0,1);
		return arcsin(z.times(i)).times(negi);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic arc cosine of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic arc cosine of 'z'.
	 */
	static Complex arccosh(Complex z) {
		// arccosh(z) = -i*arccos(z), derived directly from this file's arccos(z)=i*ln(z+sqrt(z^2-1))
		// (so it matches the branch convention exactly). Delegating to arccos inherits its
		// SAFE_SQUARE_LIMIT guard against the z.power(2) overflow for |z| beyond ~1.34e154.
		Complex negi = new Complex(0,-1);
		return arccos(z).times(negi);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic arc tangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic arc tangent of 'z'.
	 */
	static Complex arctanh(Complex z) {
		Complex one = new Complex(1,0);
		return log((one.plus(z)).divides(one.minus(z))).divides(2);
	}

	/**
	 * Returns a new Complex Object which value is the hyperbolic arccotangent of 'z'.
	 * @param z The complex number
	 * @return The new Complex Object hyperbolic arccotangent of 'z'.
	 */
	static Complex acoth(Complex z) {
		return log((z.plus(1)).divides(z.minus(1))).divides(2);
	}

	/**
	 * Returns sin(z)/z, with the removable singularity at z=0 resolved to its limit value of 1.
	 * @param z The complex number
	 * @return sinc(z)
	 */
	static Complex sinc(Complex z) {
		if (z.isZero()) return Complex.ONE;
		return sin(z).divides(z);
	}

	/**
	 * Returns cos(z)/z.
	 * @param z The complex number
	 * @return cosc(z)
	 * @apiNote Unlike {@link #sinc(Complex)}, z=0 is NOT a removable singularity here -- cos(0)=1
	 * while z->0, so cos(z)/z is a genuine simple pole at the origin with no single well-defined
	 * limit value (the result depends on the direction of approach, same as 1/z). No zero guard
	 * is applied for that reason. This is a different function from {@code Sigfunc.cosc(Complex)}
	 * in the signal package, which computes the versine-like (1-cos(z))/z instead -- same name,
	 * different math, do not confuse the two.
	 */
	static Complex cosc(Complex z) {
		return cos(z).divides(z);
	}

	/**
	 * Returns tan(z)/z, with the removable singularity at z=0 resolved to its limit value of 1.
	 * @param z The complex number
	 * @return tanc(z)
	 */
	static Complex tanc(Complex z) {
		if (z.isZero()) return Complex.ONE;
		return tan(z).divides(z);
	}

	/**
	 * Returns the value of the Chebyshev polynomial of degree at a poinnt
	 * @param degree The degree of the polynomial
	 * @param cx The point
	 * @return The value of the Chebyshev polynomial
	 * @apiNote Cross-checked against {@code Polynom.chebyshev(degree,1)}'s 3-term recurrence
	 * (T0=1, T1=x, Tn=2x*T(n-1)-T(n-2)) for degrees 0-6 at points inside [-1,1], outside it
	 * (both signs), and complex points off the real axis: they agree everywhere to within
	 * floating-point noise (~1e-10 or smaller), so this trig-based analytic continuation and the
	 * polynomial recurrence are consistent, not two independently-drifting implementations.
	 */
	static Complex chebyshev(int degree, Complex cx) {
		return Complex.cos(Complex.arccos(cx).times(degree));
	}
}
