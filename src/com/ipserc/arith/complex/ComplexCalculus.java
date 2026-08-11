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
	 * Returns the Riemann integral of a Complex function along the real line
	 * @param lolimit the lower limit of the integral
	 * @param uplimit the upper limit of the integral
	 * @param func the function to be integrated
	 * @param numDec the number of significant decimals
	 * @return The value of the integral
	 * @apiNote Delegates to {@link #integrate(Complex, Complex, Function, int)} with both limits
	 * placed on the real axis (imp=0): the segment {@code lolimit+t*(uplimit-lolimit)}, t in
	 * [0,1], stays on the real axis exactly as this overload always evaluated {@code func}, so
	 * this is behavior-preserving beyond the Simpson upgrade described there (including handling
	 * descending/negative limits correctly, which the parametrization does natively).
	 */
	static Complex integrate(double lolimit, double uplimit, Function <Complex, Complex> func, int numDec) {
		return integrate(new Complex(lolimit, 0), new Complex(uplimit, 0), func, numDec);
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

	// Safety net for integrate(Complex,Complex,...)'s step-doubling below: caps the subdivision
	// count so an integrand whose Simpson error never shrinks smoothly (a genuine singularity, a
	// discontinuity, or the pre-existing gamma_integral(z) domain gap for Re(z)<1 -- t^(z-1)
	// diverges at t=0, unrelated to and unaffected by this Simpson upgrade) returns its last
	// estimate instead of looping forever.
	private static final int SIMPSON_MAX_SUBDIVISIONS = 1 << 20;

	/**
	 * Returns the Riemann integral of a Complex function in the complex plane, from lolimit to
	 * uplimit along the straight segment joining them.
	 * @param lolimit the lower limit of the integral expressed as Complex
	 * @param uplimit the upper limit of the integral expressed as Complex
	 * @param func the function to be integrated
	 * @param numDec the number of significant decimals
	 * @return The value of the integral
	 * @apiNote SCOPE CHANGE (Sexta sesion, auditoria matematica, decision de alcance hablada con
	 * el usuario): replaces the fixed-step Riemann sum this used to dispatch to (via the two
	 * near-duplicate private methods {@code integrateRE}/{@code integrateIM}, chosen by the
	 * segment's angle, one projecting the step onto the real axis and the other onto the
	 * imaginary axis -- both removed) with composite Simpson's rule, O(h^4) error per step instead
	 * of Riemann's O(h). Parametrizing the segment by t in [0,1] (point(t)=lolimit+t*(uplimit-lolimit))
	 * makes the axis-projection dispatch unnecessary: it is mathematically equivalent to either
	 * projection for a straight segment, and simpler because there is now one code path instead of
	 * two near-duplicates to verify. It also naturally handles descending/negative limits, since
	 * the sign lives in the (uplimit-lolimit) factor rather than in a separately-signed step.
	 * <p>
	 * Subdivision count starts at 10 and doubles until two successive estimates agree within
	 * {@code 10^-(numDec+2)} -- Richardson-style: since Simpson's error is O(h^4), the gap between
	 * an estimate and the one at double the resolution is a reliable proxy for the remaining error
	 * (unlike checking whether the last term added to a slowly-converging sum changed the
	 * accumulator, the pitfall documented on {@code zeta_re}'s Fix 10 in ComplexArithRev.md -- here
	 * the two quantities compared are full independent estimates of the same integral at different
	 * fidelity, not a partial sum vs. its next term). This is NOT full adaptive quadrature (no
	 * recursive interval subdivision/local refinement): a deliberate, smaller-risk scope decided
	 * with the user over the alternative. {@link #SIMPSON_MAX_SUBDIVISIONS} is a safety net, not
	 * the normal exit path, for integrands whose error does not shrink smoothly (see its comment).
	 * <p>
	 * This changes the numeric result of every caller (more accurate, not bit-identical to
	 * before) and, for the same requested numDec, needs vastly fewer evaluations for smooth
	 * integrands -- verified against several closed-form integrals and against
	 * {@code gamma_integral}/{@code gamma_integral2} (which call this internally).
	 * <p>
	 * KNOWN LIMITATION, unchanged from before this fix: {@code func} is assumed to never mutate
	 * the {@code Complex} argument it is given and return that same mutated reference (e.g.
	 * {@code z -> z.plusEq(Complex.ONE)}) -- every real caller in this codebase constructs a fresh
	 * {@code Complex} via non-mutating ops ({@code times}, {@code plus}, ...), so this has no
	 * observed impact.
	 */
	static Complex integrate(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, int numDec) {
		double tolerance = Math.pow(10, -(numDec + 2));
		Complex delta = uplimit.minus(lolimit);

		int n = 10;
		Complex estimate = simpsonEstimate(lolimit, delta, func, n);
		while (n < SIMPSON_MAX_SUBDIVISIONS) {
			if (estimate.isNaN()) return estimate;
			int nextN = n * 2;
			Complex nextEstimate = simpsonEstimate(lolimit, delta, func, nextN);
			if (nextEstimate.minus(estimate).mod() < tolerance) return nextEstimate;
			n = nextN;
			estimate = nextEstimate;
		}
		return estimate;
	}

	/**
	 * Composite Simpson's rule for the integral from lolimit to lolimit+delta, with n subintervals
	 * (n must be even). Parametrizes the segment as point(t)=lolimit+t*delta, t in [0,1], and
	 * applies Simpson's rule to g(t)=func(point(t)) over [0,1], scaling back by delta at the end.
	 * @apiNote Evaluates each node via {@link #evalNearOrAt}, not {@code func.apply} directly, to
	 * absorb an integrable singularity that happens to land exactly on a grid node -- see that
	 * method's comment for why this is a real, observed case (not a hypothetical one).
	 */
	private static Complex simpsonEstimate(Complex lolimit, Complex delta, Function <Complex, Complex> func, int n) {
		double h = 1.0 / n;
		Complex sum = evalNearOrAt(func, lolimit, delta, 0.0, h, +1)
				.plus(evalNearOrAt(func, lolimit, delta, 1.0, h, -1));
		for (int i = 1; i < n; ++i) {
			double weight = (i % 2 == 0) ? 2.0 : 4.0;
			double t = i * h;
			// Accumulator mutated in place instead of reassigned to a new Complex each iteration.
			sum.plusEq(evalNearOrAt(func, lolimit, delta, t, h, +1).times(weight));
		}
		return sum.times(h / 3.0).times(delta);
	}

	/**
	 * Evaluates func at point(t)=lolimit+t*delta, falling back to a point nudged by a tiny
	 * fraction of the local step h when the direct evaluation is not finite (NaN or Infinite).
	 * @apiNote Simpson's evenly-spaced nodes always include some "nice" fractions of the interval
	 * (the exact midpoint for any even n, both endpoints always) -- an integrable singularity that
	 * happens to sit exactly there (e.g. {@code log(x)} over a symmetric [-1,1], singular exactly
	 * at the midpoint x=0; or {@code gamma_integral2}'s own integrand at its upper limit s=1, for
	 * z outside its documented [1,4) domain) poisons the WHOLE estimate with a single non-finite
	 * sample, no matter how much n is increased -- confirmed with {@code TestIntegral01}'s
	 * {@code log(-1,1)}/{@code log10(-1,1)}, which regressed from a finite approximation to
	 * {@code -Infinity} before this fallback was added (the old fixed-step Riemann sum never
	 * landed exactly on the singular point, by sheer accident of cumulative floating-point step
	 * drift, not by design -- nothing to preserve). Nudging by {@code direction} first (inward, at
	 * the endpoints, to stay inside [lolimit,uplimit]; either direction, for interior nodes) and
	 * using that neighboring finite value stands in for the singular node itself, which is
	 * negligible next to Simpson's own O(h^4) error for a well-behaved integrand, and turns an
	 * infinite/nonsensical result into a finite, converging one for an integrable singularity.
	 * Costs nothing extra for the overwhelmingly common case where the direct evaluation is
	 * already finite.
	 */
	private static Complex evalNearOrAt(Function <Complex, Complex> func, Complex lolimit, Complex delta, double t, double h, int direction) {
		Complex val = func.apply(lolimit.plus(delta.times(t)));
		if (!val.isNaN() && !val.isInfinite()) return val;
		double nudge = h * 1e-6 * direction;
		Complex nudged = func.apply(lolimit.plus(delta.times(t + nudge)));
		if (!nudged.isNaN() && !nudged.isInfinite()) return nudged;
		nudged = func.apply(lolimit.plus(delta.times(t - nudge)));
		if (!nudged.isNaN() && !nudged.isInfinite()) return nudged;
		return val;
	}

	/**
	 * Returns the value of the derivative at the point point
	 * @param point the point to calculate the derivative
	 * @param func the complex function to derive
	 * @param precision The precision of the result
	 * @return the complex value of the derivative at the point
	 * @apiNote Central difference (f(point+h)-f(point-h))/(2h) with a DIAGONAL complex step
	 * h=hComp+hComp*i, not a purely real or purely imaginary one. This is deliberate and valid
	 * for a holomorphic func: by the Cauchy-Riemann equations, a holomorphic function's complex
	 * derivative is direction-independent, so the limit is the same regardless of which way h
	 * points, as long as |h|->0. Near a branch cut, pole, or any point where func is not
	 * holomorphic, this is no longer guaranteed and the result can depend on which side of the
	 * cut the diagonal step happens to land on. hComp=10^-precision is scaled by max(1,|point|):
	 * an absolute step (no scaling) is indistinguishable from 0 in double once it falls below
	 * point's own ULP, silently collapsing point+h and point-h to the same value -- confirmed
	 * empirically to give 100% relative error (or an outright 0 result) for |point| >= 1e9 at
	 * ordinary precisions. Scaling by max(1,|point|) keeps h well above that floor for large
	 * points while leaving it unchanged for |point|<=1 (the max(1,...) floor). The only current
	 * caller ({@code TestIntegral01.java}, point=2+2i, |point|~2.83) has |point|>1, so its printed
	 * result now uses a slightly larger step than before -- expected, and verified to still match
	 * the analytic derivative (and to be more accurate, not less) at the requested precision.
	 */
	static Complex derivative(Complex point, Function <Complex, Complex> func, double precision) {
		double hComp = Math.pow(10, -precision) * Math.max(1.0, point.mod());
		Complex h = new Complex(hComp, hComp);
		return (func.apply(point.plus(h)).minus(func.apply(point.minus(h)))).divides(h.times(2));
	}

	/**
	 * Returns the value of the derivative at the point point
	 * @param point the point to calculate the derivative
	 * @param func the complex function to derive
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
	 * Determines whether the calculated limit value is an indeterminate form
	 * @param limit The value of the limit calculated
	 * @return True if it is an indeterminate form, False otherwise
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
	 * Calculates the limit of func at a point of type Complex
	 * @param func The function to evaluate for the limit
	 * @param point The Complex point at which the function is evaluated
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
	 * Calculates the limit of func at a point of type double
	 * @param func The function to evaluate for the limit
	 * @param point The real point at which the function is evaluated
	 * @return The Complex value of the limit
	 */
	static Complex limit(Function <Complex, Complex> func, double point) {
		Complex Cpoint = new Complex(point,0);
		return limit(func, Cpoint);
	}

	/**
	 * Calculates the limit of func at +Infinite or -Infinite, depending on the sign parameter
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
	 * Indicates whether the function is continuous at the given point.
	 * @param func The Complex function
	 * @param point The point at which continuity is evaluated. Complex.
	 * @return True if the function is continuous. False otherwise.
	 */
	static boolean isContinuous(Function <Complex, Complex> func, Complex point) {
		if (limit(func, point) != null) return true;
		return false;
	}

	/**
	 * Indicates whether the function is continuous at the given point.
	 * @param func The Complex function
	 * @param point The point at which continuity is evaluated. Double.
	 * @return True if the function is continuous. False otherwise.
	 */
	static boolean isContinuous(Function <Complex, Complex> func, double point) {
		if (limit(func, point) != null) return true;
		return false;
	}
}
