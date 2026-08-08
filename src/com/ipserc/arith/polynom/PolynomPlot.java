package com.ipserc.arith.polynom;

import java.util.ArrayList;
import java.util.List;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.plot.SimpleGnuplot;
import com.ipserc.arith.plot.SimpleGnuplot.e_syncMode;

/**
 * Package-private GNUPlot integration for {@link Polynom} -- isolates the {@link SimpleGnuplot}
 * dependency to this one class, instead of spreading it through the resolution/arithmetic core.
 * <p>
 * Extracted from {@code Polynom.java} (Décima sesión, ver {@code Claude/ComplexArithRev.md}),
 * Fase 2 of the same restructuring that moved presentation to {@link PolynomFormat} in Fase 1 --
 * same pattern as {@code Complex.java}'s {@code ComplexBoxArt}/{@code ComplexFormat} (Sexta
 * sesión, Paso 2): methods take the {@link Polynom} instance as an explicit parameter (where they
 * actually need it -- some of the original methods never touched {@code this} at all, and stay
 * plain static utilities here too) and read it only through already-public members. {@code
 * Polynom.java}'s own public methods keep their exact signatures, delegating to these in one line
 * each -- the public API is unchanged.
 * <p>
 * Migration (8 agosto 2026, a peticion del usuario): {@code com.panayotis.gnuplot.JavaPlot}
 * (external dependency, not present in every environment, previously documented here as a
 * compilation blocker when missing from the classpath) replaced by {@link SimpleGnuplot}, a
 * dependency-free homegrown replacement -- same gnuplot engine underneath, only the Java wrapper
 * changed. See {@link SimpleGnuplot}'s own Javadoc for the full motivation (blocking
 * {@code Process.waitFor()} in the old dependency, confirmed by bytecode inspection). Method call
 * shapes ({@code setTitle}/{@code addPlot}/{@code set}/{@code setPersist}/{@code getPostInit}/
 * {@code plot}) kept identical on purpose, so this was a drop-in swap -- verified live before
 * applying (user-confirmed correct rendering, both the data-block and native-expression
 * {@code addPlot} paths this class actually uses).
 * <p>
 * Continuation (8 agosto 2026, a peticion del usuario): every method here now takes an explicit
 * {@link e_syncMode} parameter (threaded straight to {@code SimpleGnuplot.plot(mode)}) instead of
 * always blocking -- this class is package-private, so the {@code xxxSync}/{@code xxxAsync} naming
 * pair the user asked for lives one layer up, on {@code Polynom.java}'s public methods (the actual
 * call sites), which pick the {@code e_syncMode} literal and pass it down to a single generic
 * method here. No back-compat aliases: the old always-blocking method names are gone.
 */
class PolynomPlot {

	static void plotExpression(Polynom p, String gnuPlotExpression, double loLimit, double upLimit, e_syncMode mode) {
		double samples = (upLimit - loLimit) * Polynom.sampleBase;
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.addPlot(gnuPlotExpression);
		plt.setTitle(p.toString());
		plt.addPlot("[" + loLimit + ":" + upLimit + "] " + p.toGNUPlot_poly());
		plt.set("zeroaxis", "");
		plt.set("samples", Double.toString(samples));
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

	static void plotExpression(Polynom p, double loLimit, double upLimit, e_syncMode mode) {
		double samples = (upLimit - loLimit) * Polynom.sampleBase;
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.setTitle(p.toString());
		plt.set("zeroaxis", "");
		plt.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		plt.set("samples", Double.toString(samples));
		plt.addPlot(p.toGNUPlot_poly());
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

	static void plotExpressionRe(Polynom p, double loLimit, double upLimit, e_syncMode mode) {
		double samples = (upLimit - loLimit) * Polynom.sampleBase;
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.setTitle("Re(" + p.toString() + ")");
		plt.set("zeroaxis", "");
		plt.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		plt.set("key", "noautotitle");
		plt.set("samples", Double.toString(samples));
		plt.addPlot("real("+ p.toGNUPlot_poly() + ")");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

	static void plotExpressionIm(Polynom p, double loLimit, double upLimit, e_syncMode mode) {
		double samples = (upLimit - loLimit) * Polynom.sampleBase;
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.setTitle("Im(" + p.toString() + ")");
		plt.set("zeroaxis", "");
		plt.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		plt.set("key", "noautotitle");
		plt.set("samples", Double.toString(samples));
		plt.addPlot("imag("+ p.toGNUPlot_poly() + ")");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

	static void plotExpressionReIm(Polynom p, double loLimit, double upLimit, e_syncMode mode) {
		double samples = (upLimit - loLimit) * Polynom.sampleBase;
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.setTitle("Re() Im() " + p.toString());
		plt.set("zeroaxis", "");
		plt.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		plt.set("key", "noautotitle");
		plt.set("samples", Double.toString(samples));
		plt.addPlot("real(" + p.toGNUPlot_poly() + "), imag("+ p.toGNUPlot_poly() + ")");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

	static void plotExpressionRepIm(Polynom p, double loLimit, double upLimit, e_syncMode mode) {
		double samples = (upLimit - loLimit) * Polynom.sampleBase;
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.setTitle("Re() Im() " + p.toString());
		plt.set("zeroaxis", "");
		plt.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		plt.set("key", "noautotitle");
		plt.set("samples", Double.toString(samples));
		plt.addPlot("real(" + p.toGNUPlot_poly() + ") / imag("+ p.toGNUPlot_poly() + ")");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

	static void plotExpressionAbs(Polynom p, double loLimit, double upLimit, e_syncMode mode) {
		double samples = (upLimit - loLimit) * Polynom.sampleBase;
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.setTitle("Abs("+p.toString()+")");
		plt.set("zeroaxis", "");
		plt.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		plt.set("key", "noautotitle");
		plt.set("samples", Double.toString(samples));
		plt.addPlot("abs(" + p.toGNUPlot_poly() + ")");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

	static void plotExpressionPhase(Polynom p, double loLimit, double upLimit, e_syncMode mode) {
		double samples = (upLimit - loLimit) * Polynom.sampleBase;
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.setTitle("Phase("+p.toString()+")");
		plt.set("grid", "");
		plt.set("zeroaxis", "");
		plt.set("xrange", "[" + loLimit + ":" + upLimit + "]");
		plt.set("key", "noautotitle");
		plt.set("samples", Double.toString(samples));
		plt.addPlot("atan(real(" + p.toGNUPlot_poly() + ")/imag("+ p.toGNUPlot_poly() + "))");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

	/**
	 * Plots different graphics in the same canvas from their list of points. Does not depend on
	 * any {@link Polynom} instance state -- a plain static utility, same as in the original class.
	 * @param pointsList The list with the points defined as List&lt;double[][]&gt;.
	 * @param title the title of the graphic.
	 */
	static void plot(List<double[][]> pointsList, String title, e_syncMode mode) {
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.setTitle(title);
		for(int i = 0; i < pointsList.size(); ++i) {
			plt.addPlot(pointsList.get(i));
		}
		plt.set("zeroaxis", "");
		plt.set("style","data lines");
		plt.set("mxtics","10");
		plt.set("mytics","10");
		plt.set("grid","xtics mxtics ytics mytics");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

	static void plot(double[][] points, String title, e_syncMode mode) {
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.setTitle(title);
		plt.addPlot(points);
		plt.set("zeroaxis", "");
		plt.set("style","data lines");
		plt.set("mxtics","10");
		plt.set("mytics","10");
		plt.set("grid","xtics mxtics ytics mytics");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

	static void plotRe(MatrixComplex points, String title, e_syncMode mode) {
		int samples = points.rows();
		double[][] pointsRe = new double[samples][2];
		for (int i = 0; i < samples; ++i) {
			pointsRe[i][0] = points.getItem(i, 0).rep();
			pointsRe[i][1] = points.getItem(i, 1).rep();
		}
		plot(pointsRe, title, mode);
	}

	/**
	 * Plots the real component of different graphics in the same canvas from its definition of
	 * its points as complexes.
	 * @param pointsList The list with the points defined as &lt;MatrixComplex&gt;.
	 * @param title the title of the graphic.
	 */
	static void plotRe(List<MatrixComplex> pointsList, String title, e_syncMode mode) {
		List<double[][]> pointsListGraph = new ArrayList<double[][]>();
		int samples = pointsList.get(0).rows();
		for(int l = 0; l < pointsList.size(); ++l) {
			double[][] pointsRe = new double[samples][2];
			for (int i = 0; i < samples; ++i) {
				pointsRe[i][0] = pointsList.get(l).getItem(i, 0).rep();
				pointsRe[i][1] = pointsList.get(l).getItem(i, 1).rep();
			}
			pointsListGraph.add(pointsRe);
		}
		plot(pointsListGraph, title, mode);
	}

	static void plotIm(MatrixComplex points, String title, e_syncMode mode) {
		int samples = points.rows();
		double[][] pointsIm = new double[samples][2];
		for (int i = 0; i < samples; ++i) {
			pointsIm[i][0] = points.getItem(i, 0).imp();
			pointsIm[i][1] = points.getItem(i, 1).imp();
		}
		plot(pointsIm, title, mode);
	}

	/**
	 * Plots the imaginary component of different graphics in the same canvas from its definition
	 * of its points as complexes.
	 * @param pointsList The list with the points defined as &lt;MatrixComplex&gt;.
	 * @param title the title of the graphic.
	 */
	static void plotIm(List<MatrixComplex> pointsList, String title, e_syncMode mode) {
		List<double[][]> pointsListGraph = new ArrayList<double[][]>();
		int samples = pointsList.get(0).rows();
		for(int l = 0; l < pointsList.size(); ++l) {
			double[][] pointsIm = new double[samples][2];
			for (int i = 0; i < samples; ++i) {
				pointsIm[i][0] = pointsList.get(l).getItem(i, 0).imp();
				pointsIm[i][1] = pointsList.get(l).getItem(i, 1).imp();
			}
			pointsListGraph.add(pointsIm);
		}
		plot(pointsListGraph, title, mode);
	}

	static void plotMod(MatrixComplex points, String title, e_syncMode mode) {
		int samples = points.rows();
		double[][] pointsMod = new double[samples][2];
		for (int i = 0; i < samples; ++i) {
			pointsMod[i][0] = points.getItem(i, 0).mod();
			pointsMod[i][1] = points.getItem(i, 1).mod();
		}
		plot(pointsMod, title, mode);
	}

	static void plotMod(List<MatrixComplex> pointsList, String title, e_syncMode mode) {
		List<double[][]> pointsListGraph = new ArrayList<double[][]>();
		int samples = pointsList.get(0).rows();
		for(int l = 0; l < pointsList.size(); ++l) {
			double[][] pointsIm = new double[samples][2];
			for (int i = 0; i < samples; ++i) {
				pointsIm[i][0] = pointsList.get(l).getItem(i, 0).mod();
				pointsIm[i][1] = pointsList.get(l).getItem(i, 1).mod();
			}
			pointsListGraph.add(pointsIm);
		}
		plot(pointsListGraph, title, mode);
	}

	static void plotPha(MatrixComplex points, String title, e_syncMode mode) {
		int samples = points.rows();
		double[][] pointsPha = new double[samples][2];
		for (int i = 0; i < samples; ++i) {
			pointsPha[i][0] = points.getItem(i, 0).pha();
			pointsPha[i][1] = points.getItem(i, 1).pha();
		}
		plot(pointsPha, title, mode);
	}

	static void plotPha(List<MatrixComplex> pointsList, String title, e_syncMode mode) {
		List<double[][]> pointsListGraph = new ArrayList<double[][]>();
		int samples = pointsList.get(0).rows();
		for(int l = 0; l < pointsList.size(); ++l) {
			double[][] pointsIm = new double[samples][2];
			for (int i = 0; i < samples; ++i) {
				pointsIm[i][0] = pointsList.get(l).getItem(i, 0).mod();
				pointsIm[i][1] = pointsList.get(l).getItem(i, 1).pha();
			}
			pointsListGraph.add(pointsIm);
		}
		plot(pointsListGraph, title, mode);
	}

	/**
	 * Traverses an interval taking {@code Polynom.sampleBase} samples and evaluating the
	 * polynomial at these points. You should set {@code sampleBase} before calling this method.
	 * @param p The polynomial.
	 * @param lolimit Lower limit.
	 * @param uplimit Upper limit.
	 * @return The sampled (point, value) pairs.
	 */
	static MatrixComplex walkInterval(Polynom p, Complex lolimit, Complex uplimit) {
		Complex.storeFormatStatus();
		Complex.setScientificON(20);
		MatrixComplex tupla = new MatrixComplex(Polynom.sampleBase, 2);
		Complex dir = uplimit.minus(lolimit);
		Complex inc = dir.divides(Polynom.sampleBase);
		Complex point;
		for(int i=0; i < Polynom.sampleBase; ++i) {
			point = lolimit.plus(inc.times(i));
			tupla.setItem(i, 0, point);
			tupla.setItem(i, 1, p.eval(point));
		}
		Complex.restoreFormatStatus();
		return tupla;
	}

	static void plotRe(Polynom p, double[][] points, e_syncMode mode) {
		double lolimit = points[0][0];
		double uplimit = points[points.length-1][0];
		SimpleGnuplot plt = new SimpleGnuplot();
		plt.setTitle("Phase("+p.toString()+")");
		plt.set("grid", "");
		plt.set("zeroaxis", "");
		plt.set("xrange", "[" + lolimit + ":" + uplimit + "]");
		plt.set("key", "noautotitle");
		plt.set("samples", Double.toString(Polynom.sampleBase));
		plt.addPlot(points);
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		plt.setPersist(true);
		plt.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		plt.plot(mode);
	}

}
