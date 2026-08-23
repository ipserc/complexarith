package com.ipserc.arith.vectorcalculus;

/**
 * Gradient, divergence and curl of scalar/vector fields over {@code R^n} (curl restricted to
 * {@code R^3}, its only classically-defined dimension), by NUMERICAL differentiation (central
 * finite differences) -- same technique, and same reasoning for it, as {@link
 * com.ipserc.arith.mechanics.LagrangianSystem}: no symbolic algebra or automatic differentiation
 * available in this project, so a field stays an ordinary {@code double[] -> double} (or
 * {@code double[] -> double[]}) function instead of needing a new numeric type threaded through
 * every arithmetic operation.
 * <p>
 * A scalar field {@code f(x)} and a vector field {@code F(x)} are plain functional interfaces
 * ({@link ScalarField}/{@link VectorField}) over a point {@code x} of arbitrary dimension {@code
 * n} (gradient/divergence) or exactly 3 ({@code curl}).
 */
public final class VectorCalculus {

	private final static String HEADINFO = "VectorCalculus --- INFO: ";
	private final static String VERSION = "1.0 (2026_0824_1000)";
	/* VERSION Release Note
	 *
	 * 1.0 (2026_0824_1000)
	 * Primera clase de com.ipserc.arith.vectorcalculus (paquete nuevo): gradient()/divergence()/
	 * curl() por diferencias finitas centradas, mismo enfoque que LagrangianSystem.VERSION 1.0.
	 */

	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION);
	}

	private VectorCalculus() {}

	/** A scalar field {@code f(x)} over {@code R^n}. */
	@FunctionalInterface
	public interface ScalarField {
		double apply(double[] x);
	}

	/** A vector field {@code F(x)} over {@code R^n}, returning a vector of the same dimension. */
	@FunctionalInterface
	public interface VectorField {
		double[] apply(double[] x);
	}

	/**
	 * Central-difference step for the first partial derivatives computed here -- same order of
	 * magnitude as, and for the same reason as, {@code LagrangianSystem.STEP}: a compromise between
	 * {@code O(h^2)} truncation error and {@code O(eps/h)} rounding error.
	 */
	private static final double STEP = 1e-5;

	/** {@code df/dx_i} at {@code point}, central difference. */
	private static double partialDerivative(ScalarField f, double[] point, int i) {
		double[] plus = point.clone();  plus[i] += STEP;
		double[] minus = point.clone(); minus[i] -= STEP;
		return (f.apply(plus) - f.apply(minus)) / (2*STEP);
	}

	/** {@code dF_component/dx_wrtIndex} at {@code point}, central difference. */
	private static double partialComponent(VectorField F, double[] point, int component, int wrtIndex) {
		double[] plus = point.clone();  plus[wrtIndex] += STEP;
		double[] minus = point.clone(); minus[wrtIndex] -= STEP;
		return (F.apply(plus)[component] - F.apply(minus)[component]) / (2*STEP);
	}

	/**
	 * The gradient {@code grad(f) = (df/dx_0, ..., df/dx_(n-1))} of a scalar field at {@code point}.
	 * @param f The scalar field.
	 * @param point The point at which to evaluate the gradient, any dimension {@code n}.
	 * @return The gradient vector, same length as {@code point}.
	 */
	public static double[] gradient(ScalarField f, double[] point) {
		double[] grad = new double[point.length];
		for (int i = 0; i < point.length; ++i) {
			grad[i] = partialDerivative(f, point, i);
		}
		return grad;
	}

	/**
	 * The divergence {@code div(F) = sum_i dF_i/dx_i} of a vector field at {@code point}.
	 * @param F The vector field. Must return a vector of the same dimension as {@code point}.
	 * @param point The point at which to evaluate the divergence, any dimension {@code n}.
	 * @return The (scalar) divergence at {@code point}.
	 */
	public static double divergence(VectorField F, double[] point) {
		double div = 0;
		for (int i = 0; i < point.length; ++i) {
			div += partialComponent(F, point, i, i);
		}
		return div;
	}

	/**
	 * The curl {@code curl(F) = (dF_z/dy-dF_y/dz, dF_x/dz-dF_z/dx, dF_y/dx-dF_x/dy)} of a vector
	 * field at {@code point} -- only classically defined in {@code R^3}.
	 * @param F The vector field, {@code R^3 -> R^3}.
	 * @param point The point at which to evaluate the curl, must have length 3.
	 * @return The curl vector, length 3.
	 * @throws IllegalArgumentException if {@code point} does not have length 3.
	 */
	public static double[] curl(VectorField F, double[] point) {
		if (point.length != 3) {
			throw new IllegalArgumentException(HEADINFO + "curl() is only defined in R^3, got a point of dimension " + point.length);
		}
		double dFz_dy = partialComponent(F, point, 2, 1);
		double dFy_dz = partialComponent(F, point, 1, 2);
		double dFx_dz = partialComponent(F, point, 0, 2);
		double dFz_dx = partialComponent(F, point, 2, 0);
		double dFy_dx = partialComponent(F, point, 1, 0);
		double dFx_dy = partialComponent(F, point, 0, 1);
		return new double[] {
				dFz_dy - dFy_dz,
				dFx_dz - dFz_dx,
				dFy_dx - dFx_dy
		};
	}
}
