package com.ipserc.arith.complex;

import java.util.function.Function;

/**
 * Numerical integration/derivation and limit calculation for {@link Complex} ({@code integrate*},
 * {@code derivative}, {@code limit}/{@code limit_inf}/{@code limit_Minf}, {@code isContinuous}).
 * Package-private: {@code Complex} keeps every public method as a one-line delegator with the
 * exact same signature, so external callers need zero changes.
 * <p>
 * Extracted verbatim (Sexta sesion, paso 2, Fase 2.6 -- last phase of the architectural split)
 * from {@code Complex.java}'s "INTEGRATION &amp; DERIVATION" and "LIMITS" sections. Every direct
 * field access on a {@code Complex} parameter/local (e.g. {@code point.mod}, {@code vector.imp})
 * was rewritten to the corresponding public getter ({@code point.mod()}, {@code vector.imp()}),
 * since those fields are private to {@code Complex} and this class cannot touch them directly --
 * same pattern as {@link ComplexFunctions} (Fase 2.5). One exception: {@code limit_inf} used to
 * write directly to a result's {@code pha} field (leaving rep/imp/mod untouched, deliberately not
 * going through {@code setComplexPol}, which would also recompute those); since there was no
 * existing public setter for that, {@code Complex} gained a small package-private
 * {@code setPhaRaw(double)} instance method for this.
 * <p>
 * {@code round(Complex, int)} (used by {@code limit}) and the {@code LIM_NUMDECS}/
 * {@code LIM_PRECISION}/{@code LIM_INF_MAX_ITER} constants stayed declared on {@code Complex.java}
 * -- {@code round} because it is also used by the {@code equals} family in the core, and the
 * constants because {@code Complex.showPrecision()} (core) prints {@code LIM_NUMDECS}/
 * {@code LIM_PRECISION} alongside {@code LIM_INF} (a genuine core constant, never part of this
 * section). Both are referenced here as {@code Complex.round(...)}/{@code Complex.LIM_NUMDECS}/etc.
 */
final class ComplexCalculus {

	private ComplexCalculus() {}

	/**
	 * Returns the Riemann integral of a Complex function along of the real line
	 * @param lolimit the lower limit of the integral
	 * @param uplimit the upper limit of the integral
	 * @param func the function to be integrated
	 * @param numDec the number of significant decimals
	 * @return The value of the integral
	 * @apiNote Fixed-step Riemann sum: iterates roughly 10^(numDec+1) times with no adaptive
	 * convergence check, so the cost of this method (and of {@code gamma_integral(Complex)}/
	 * {@code gamma_integral2(Complex)}, which call it internally with numDec=5/6) scales
	 * directly with the requested decimals. Bumping numDec by 1 multiplies the cost by ~10.
	 */
	static Complex integrate(double lolimit, double uplimit, Function <Complex, Complex> func, int numDec) {
		int iter  = 1;
		double precision = Math.pow(10, -Math.abs(++numDec));
		double step = (uplimit - lolimit) * precision;

		Complex integral = new Complex();
		Complex prevPoint = new Complex(lolimit, 0);
		Complex point = new Complex(lolimit + step, 0);
		Complex val = new Complex();

		val = func.apply(prevPoint);
		// .copy() is precautionary here, not fixing an active bug: prevPoint/point are always
		// rebound to freshly-allocated objects each iteration (never re-derived from a Complex
		// this method still reads by reference later), unlike integrateRE/integrateIM's lolimit,
		// which the loop keeps re-reading -- see the identical-shaped fix there for the real bug.
		integral = val.copy();
		//System.out.printf("ulimit:%f point:%f val:%s \n", ulimit, prevPoint.mod, val.toString());
		// Loop condition compares point.rep() (position along the real line, signed) against
		// uplimit in the direction step already points to -- NOT point.mod() (magnitude), which
		// used to make this loop terminate after a single iteration (silently returning a wrong
		// result) whenever uplimit was negative, or whenever uplimit < lolimit (descending
		// integration), since a magnitude can never be less than a negative uplimit. Verified
		// against the closed form of integrate(x)dx=(b^2-a^2)/2 for lolimit/uplimit both negative
		// and for reversed (descending) limits.
		while (step > 0 ? point.rep() < uplimit : point.rep() > uplimit) {
			val = func.apply(point);
			// Accumulator mutated in place instead of reassigned to a new Complex each iteration.
			integral.plusEq(val);
			//System.out.printf("ulimit:%f point:%f val:%s \n", ulimit, point.mod, val.toString());
			prevPoint = point;
			point = prevPoint.plus(step);
			++iter;
		}
		return integral.times((uplimit-lolimit)/iter);
	}

	/**
	 * Returns the Riemann integral of a Complex function in the complex plane
	 * @param slolimit the lower limit of the integral expressed as "a+bi"
	 * @param suplimit the upper limit of the integral expressed as "a+bi"
	 * @param func the function to be integrated
	 * @param numDec the number of significant decimals
	 * @return The value of the integral
	 */
	static Complex integrate(String slolimit, String suplimit, Function <Complex, Complex> func, int numDec) {
		Complex lolimit = new Complex(slolimit);
		Complex uplimit = new Complex(suplimit);
		return integrate(lolimit, uplimit, func, numDec);
	}

	/**
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the real axe
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param numDec the number of significant decimals
	 * @return The value of the integral
	 * @apiNote Dispatches to {@link #integrateRE} or {@link #integrateIM} (fixed-step Riemann
	 * sums, ~10^(numDec+2) iterations, no adaptive convergence check), so cost scales directly
	 * with numDec here too.
	 * <p>
	 * KNOWN BUG, not fixed here (out of scope for the aliasing fix in {@link #integrateRE}/
	 * {@link #integrateIM}): {@code func} is assumed to never mutate the {@code Complex} argument
	 * it is given and return that same mutated reference (e.g. {@code z -> z.plusEq(Complex.ONE)}).
	 * {@code integrateRE}/{@code integrateIM} reuse a single {@code nextPoint} instance across all
	 * iterations (mutated in place via {@code setComplexRec} for performance) and read it back to
	 * compute the next position, so a self-mutating {@code func} corrupts that position tracking.
	 * Every real caller in this codebase constructs a fresh {@code Complex} via non-mutating ops
	 * ({@code times}, {@code plus}, ...), so this has no observed impact; documented as a caveat
	 * for the {@code func} contract rather than fixed, since fixing it would mean copying on every
	 * iteration instead of just once per call (a real cost/correctness trade-off to weigh, not a
	 * one-line fix like the aliasing bug below).
	 */
	static Complex integrate(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, int numDec) {
		Complex vector = uplimit.minus(lolimit);
		double vectSlope = vector.imp()/vector.rep();
		double vectAngle = Math.atan(vectSlope);
		double precision = Math.pow(10, -Math.abs(numDec+2));

		vectAngle = vectAngle > Math.PI ? Math.PI - vectAngle : vectAngle;
		vectAngle = vectAngle < -Math.PI ? Math.PI + vectAngle : vectAngle;

		if (((vectAngle >= Math.PI/4) && (vectAngle < 3*Math.PI/4 )) ||
				((vectAngle >= -3*Math.PI/4) && (vectAngle < -Math.PI/4 ))) {
			return integrateIM(lolimit, uplimit, func, precision);
		}
		else return integrateRE(lolimit, uplimit, func, precision);
	}

	/**
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the real axe
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param precision The precision of the result
	 * @return The value of the integral
	 */
	private static Complex integrateRE(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, double precision) {
		Complex vector = uplimit.minus(lolimit);
		Complex nextPoint = new Complex();
		Complex integral = new Complex();

		//Recorrer la recta con distancia Euclidea
		// stepRe used to be computed as vector.mod*Math.cos(Math.atan(vector.imp/vector.rep))*precision*Math.signum(vector.rep).
		// cos(atan(x)) == 1/sqrt(1+x^2), so that projection algebraically reduces to
		// Math.abs(vector.rep), and Math.abs(vector.rep)*Math.signum(vector.rep) == vector.rep,
		// i.e. the atan/cos round-trip was just computing vector.rep*precision the long way.
		double vectSlope = vector.imp()/vector.rep();
		double stepRe = vector.rep() * precision;
		double nextRep, nextImp;

		int iter = 0;
		nextPoint = lolimit.copy();

		/** /
		System.out.println("vectSlope:" + vectSlope);
		System.out.println("stepRe   :" + stepRe);
		System.out.println("iter:" + iter + "   nextPoint:" + lolimit.toString());
		/**/

		Complex val = new Complex();
		val = func.apply(lolimit);
		// .copy() breaks aliasing: if func returns the same reference it was given (e.g. the
		// identity z->z, or any in-place-style func written using Complex's own plusEq/timesEq/etc.
		// idiom, which return 'this'), 'integral' would otherwise BE 'lolimit' itself -- and the loop
		// below keeps re-reading lolimit.imp()/lolimit.rep() as the fixed lower limit on every
		// iteration while integral.plusEq(val) mutates that same object underneath it, silently
		// corrupting the limit mid-integration. Confirmed with integrate(0,3,z->z,...): without the
		// copy this returned -22497.75 instead of the correct 4.5.
		integral = val.copy();

		while (++iter <= 1/precision) {
			//System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
			nextRep = nextPoint.rep() + stepRe;
			nextImp = lolimit.imp() + vectSlope * (nextRep - lolimit.rep());
			nextPoint.setComplexRec(nextRep, nextImp);
			val = func.apply(nextPoint);
			// Accumulator mutated in place instead of reassigned to a new Complex each iteration.
			integral.plusEq(val);
		}
		// System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
		return integral.times(uplimit.minus(lolimit)).divides(iter);
	}

	/**
	 * Private method. Calculates the Riemann integral of a Complex function in the complex plane by projecting the vector that joins the limits over the imaginary axe
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param precision The precision of the result
	 * @return The value of the integral
	 */
	private static Complex integrateIM(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, double precision) {
		Complex vector = uplimit.minus(lolimit);
		Complex nextPoint = new Complex();
		Complex integral = new Complex();

		//Recorrer la recta con distancia Euclidea
		// stepIm used to be computed as vector.mod*Math.cos(Math.atan(vector.rep/vector.imp))*precision*Math.signum(vector.imp);
		// see integrateRE's comment for the algebraic reduction -- this simplifies the same way
		// to vector.imp*precision.
		double vectSlope = vector.rep()/vector.imp();
		double stepIm = vector.imp() * precision;
		double nextRep, nextImp;

		int iter = 0;
		nextPoint = lolimit.copy();

		/** /
		System.out.println("vectSlope:" + vectSlope);
		System.out.println("stepIm   :" + stepIm);
		System.out.println("iter:" + iter + "   nextPoint:" + lolimit.toString());
		/**/

		Complex val = new Complex();
		val = func.apply(lolimit);
		// .copy() breaks aliasing -- see the identical comment in integrateRE just above this
		// method for the full explanation (the same lolimit-corruption bug affects this method too).
		integral = val.copy();

		while (++iter <= 1/precision) {
			//System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
			nextImp = nextPoint.imp() + stepIm;
			nextRep = lolimit.rep() + vectSlope * (nextImp - lolimit.imp());
			nextPoint.setComplexRec(nextRep, nextImp);
			val = func.apply(nextPoint);
			// Accumulator mutated in place instead of reassigned to a new Complex each iteration.
			integral.plusEq(val);
		}
		// System.out.println("iter:" + iter + "   nextPoint:" + nextPoint.toString());
		return integral.times(uplimit.minus(lolimit)).divides(iter);
	}

	/**
	 * Returns the value of the derivative at the point point
	 * @param point the point to calculate the derivative
	 * @param func the complex function to derived
	 * @param precision The precision of the result
	 * @return the complex value of the derivative at the point
	 * @apiNote Central difference (f(point+h)-f(point-h))/(2h) with a DIAGONAL complex step
	 * h=hComp+hComp*i, not a purely real or purely imaginary one. This is deliberate and valid
	 * for a holomorphic func: by the Cauchy-Riemann equations, a holomorphic function's complex
	 * derivative is direction-independent, so the limit is the same regardless of which way h
	 * points, as long as |h|->0. Near a branch cut, pole, or any point where func is not
	 * holomorphic, this is no longer guaranteed and the result can depend on which side of the
	 * cut the diagonal step happens to land on. Also, hComp=10^-precision underflows to exactly
	 * 0.0 for precision above ~308 (double range), which would divide by zero; not a concern for
	 * the only current caller ({@code TestIntegral01.java}, precision=3).
	 */
	static Complex derivative(Complex point, Function <Complex, Complex> func, double precision) {
		double hComp = Math.pow(10, -precision);
		Complex h = new Complex(hComp, hComp);
		return (func.apply(point.plus(h)).minus(func.apply(point.minus(h)))).divides(h.times(2));
	}

	/**
	 * Returns the value of the derivative at the point point
	 * @param point the point to calculate the derivative
	 * @param func the complex function to derived
	 * @param precision The precision of the result
	 * @return the complex value of the derivative at the point
	 */
	static Complex derivative(double point, Function <Complex, Complex> func, double precision) {
		Complex CPoint = new Complex(point, 0);
		return derivative(CPoint, func, precision);
	}

	/**
	 * Gets the next point in a series for evaluating a function
	 * @param point The point to evaluate the function
	 * @param mult The multiplier for the surrounding point
	 * @param sign The sign of the surrounding point
	 * @return The new point calculated
	 */
	private static Complex nextPoint(Complex point, double mult, int sign) {
		double newMod, newPha;
		Complex nextPoint = new Complex();
		double precision = Complex.LIM_PRECISION; //Complex.PRECISION * 1e5; //1e5;
		newMod = point.mod() + precision * mult * sign;
		newPha = newMod < Complex.LIM_PRECISION ? point.pha() + Math.PI : point.pha();
		newMod = Math.abs(newMod);
		nextPoint.setComplexPol(newMod, newPha);
		return nextPoint;
	}

	/**
	 * Determines if the value of the limit obtained is an indetermination or not
	 * @param limit The value of the limit calculated
	 * @return True is is an indetermination, False if not
	 */
	private static boolean isIndetermination(Complex limit) {
		//if (Double.isNaN(limit.mod)) return true;
		if (limit.isNaN()) return true;
		//if (Double.isNaN(limit.rep) || Double.isNaN(limit.imp)) return true;
		return false;
	}

	/**
	 * Compares the values of two limits, usually the limit on the right and the limit on the left, and indicates whether they are the same or not
	 * @param limr The value of the limit on the right
	 * @param liml The value of the limit on the left
	 * @return If both values of the limit are equals or not
	 * @apiNote Used to delegate to a hand-rolled check: round the ratio of moduli to
	 * LIM_NUMDECS-2 decimals and compare it to 1 via exact {@code ==}, then confirm the phases
	 * "match" via {@code sin(phaR)^2+cos(phaL)^2==1 && cos(phaR)^2+sin(phaL)^2==1}. Measured: for
	 * a sweep of angles -pi..pi in steps of 1e-3, sin(t)^2+cos(t)^2 fails to be EXACTLY 1.0 in
	 * double arithmetic ~22% of the time (off by 1-2 ULP), so that phase check spuriously
	 * rejected genuinely-equal phases about 1 time in 5. It was also logically looser than
	 * intended: sin(a)^2==sin(b)^2 and cos(a)^2==cos(b)^2 both reduce to cos(2a)==cos(2b), i.e.
	 * a==+-b+k*PI, which would also accept phases exactly PI apart (opposite directions) as
	 * "equal" for a nonzero modulus. Replaced with {@code equals(Complex)}, the same
	 * rep/imp-tolerance equality already used as the convergence test elsewhere in this class
	 * (e.g. zeta_havil): simpler, and free of both issues since it compares rep/imp directly
	 * instead of re-deriving phase equality through squared trig identities.
	 */
	private static boolean limequ(Complex limr, Complex liml) {
		return limr.equals(liml);
	}

	/**
	 * Calculates the limit of func at point of type double
	 * @param func The function to evaluate for the limit
	 * @param point The Complex point in which the function is evaluated
	 * @return The Complex value of the limit
	 */
	static Complex limit(Function <Complex, Complex> func, Complex point) {
		Complex lastLimit = null;
		Complex limit = func.apply(point);
		if (!isIndetermination(limit)) {
			System.out.println("NO indetermination");
			return limit;
		}
		//	System.out.println("INDETERMINATION!!!");

		limit = null;
		Complex pointr = new Complex();
		Complex pointl = new Complex();
		double mult = 1;
		Complex limr = new Complex();
		Complex liml = new Complex();

		do {
			pointr = nextPoint(point, mult, 1);
			pointl = nextPoint(point, mult, -1);
			limr = Complex.round(func.apply(pointr), Complex.LIM_NUMDECS/3);
			liml = Complex.round(func.apply(pointl), Complex.LIM_NUMDECS/3);

			//	System.out.println("pointr = " + pointr.toStringPol());
			//	System.out.println("pointl = " + pointl.toStringPol());
			//	System.out.println("limr   = " + limr.toStringPol());
			//	System.out.println("liml   = " + liml.toStringPol());

			//if (Double.isInfinite(limr.mod) && Double.isInfinite(liml.mod)) return limr;
			if (limr.isInfinite() && liml.isInfinite()) return limr;
			if (limr.isNaN() && liml.isNaN()) return limr;
			if (limequ(limr,liml)) {
				if (lastLimit != null) {
					if (lastLimit.mod() == limr.mod()) return limr;
				}
				lastLimit = limr;
			}
			mult *=10;
		} while(mult * Complex.LIM_PRECISION < 1);

		return limit;
	}

	/**
	 * Calculates the limit of func at point of type Complex
	 * @param func The function to evaluate for the limit
	 * @param point The Complex point in which the function is evaluated
	 * @return The Complex value of the limit
	 */
	static Complex limit(Function <Complex, Complex> func, double point) {
		Complex Cpoint = new Complex(point,0);
		return limit(func, Cpoint);
	}

	/**
	 * Calculates the limit of func at +Infinite or -Inifinite regarding param sign
	 * @param func The function to evaluate for the limit
	 * @param sign The sign of the Infinite
	 * @return The Complex value of the limit
	 * @apiNote {@code LIM_INF=2147483647} (Integer.MAX_VALUE as a double) is used as the initial
	 * "far out" probe point below -- a fairly modest magnitude to stand in for infinity in a
	 * double-based library that can represent values up to ~1.8e308, but it only serves as a
	 * starting point: the loop below doubles point.mod from there until the result stabilizes or
	 * genuinely diverges. Its exit condition, {@code result2.mod/result.mod != 1} (exact double
	 * comparison, no tolerance), by itself never terminates for a function whose ratio hovers
	 * near-but-not-exactly 1.0 forever; this used to be a genuine hang, not just "many
	 * iterations": once {@code point.mod} doubles past {@code Double.MAX_VALUE} it overflows to
	 * {@code Infinity}, further doubling leaves it at {@code Infinity} forever, and if
	 * {@code func} maps an infinite-modulus point to a {@code NaN} result (common with
	 * {@code Infinity*0} inside trig identities), every comparison against that {@code NaN}
	 * (the growth check, the zero check, and the loop condition itself) silently evaluates to
	 * "keep looping" -- an infinite loop the JVM cannot recover from. Fixed by (1) breaking out
	 * with the last finite result as soon as {@code result} is {@code NaN}, and (2) a hard
	 * iteration cap ({@code Complex.LIM_INF_MAX_ITER}) as a backstop, mirroring how
	 * {@link #limit(Function, Complex)}'s outer loop is bounded by {@code mult*LIM_PRECISION<1}.
	 * Neither changes the result for any function that already converged within the previous
	 * (uncapped) behavior: {@code LIM_INF_MAX_ITER=2000} is far more doublings than
	 * {@code point.mod} can perform before overflowing (~992, starting from {@code LIM_INF}), so
	 * it only ever triggers in the pathological non-finite-result case the NaN check already
	 * catches, or for a function that never converges, never grows, and never NaNs/hits zero --
	 * a case the previous "no cap" behavior handled by hanging forever.
	 */
	private static Complex limit_inf(Function <Complex, Complex> func, int sign) {
		Complex result;
		Complex result2;
		Complex point;
		// 1st - Determine if the function is convergent
		/*************************************************************/
		result = func.apply(new Complex(sign*Double.MAX_VALUE, 0));
		if (!isIndetermination(result)) {
			if (result.equals(Complex.ZERO)) {
				System.out.println("NO indetermination");
				return result;
			}
		}
		//	System.out.println("INDETERMINATION!!!");
		/*************************************************************/

		// 2nd Try to find the convergence value
		point = new Complex(sign*Complex.LIM_INF, 0);
		result = func.apply(point);
		if (result.isInfinite()) {
			result.setPhaRaw(func.apply(new Complex(sign*Complex.LIM_INF/1e8, 0)).pha());
			//	System.out.println(" + + + Infinito detectado pha = " + result.pha());
			return result;
		}
		int iterations = 0;
		do {
			result2 = result.copy();
			point.setComplexPol(point.mod()*2, point.pha());
			result = func.apply(point);
			//	System.out.println("result2 = " + result2.toStringPol());
			//	System.out.println("result  = " + result.toStringPol());
			// func mapped an infinite-modulus point to NaN: every comparison below against NaN
			// would silently mean "keep looping", so this must be checked before them.
			if (result.isNaN()) {
				result = result2;
				break;
			}
			// If it grows the cut it
			if ((result.mod()-result2.mod()) > 0 ) {
				// System.out.println("result.mod-result2.mod)*sign  = " + (result.mod-result2.mod)*sign);
				result = result2;
				break;
			}
			if ((result.mod() == 0)) {
				result.setComplexPol(0, 0);
				break;
			}
		} while (result2.mod()/result.mod() != 1 && ++iterations < Complex.LIM_INF_MAX_ITER);
		return result;
	}

	/**
	 * Calculates the limit of func at +Infinite
	 * @param func The function to evaluate for the limit
	 * @return The Complex value of the limit
	 */
	static Complex limit_inf(Function <Complex, Complex> func) {
		return limit_inf(func, 1);
	}

	/**
	 * Calculates the limit of func at -Infinite
	 * @param func The function to evaluate for the limit
	 * @return The Complex value of the limit
	 */
	static Complex limit_Minf(Function <Complex, Complex> func) {
		return limit_inf(func, -1);
	}

	/**
	 * Indicates if the function is continuous in the given point.
	 * @param func The Complex function
	 * @param point The point in which the continuity is evaluated. Complex.
	 * @return True if the function in continuous. False in other case.
	 */
	static boolean isContinuous(Function <Complex, Complex> func, Complex point) {
		if (limit(func, point) != null) return true;
		return false;
	}

	/**
	 * Indicates if the function is continuous in the given point.
	 * @param func The Complex function
	 * @param point The point in which the continuity is evaluated. Double.
	 * @return True if the function in continuous. False in other case.
	 */
	static boolean isContinuous(Function <Complex, Complex> func, double point) {
		if (limit(func, point) != null) return true;
		return false;
	}
}
