package com.ipserc.arith.matrixcomplex;

import com.ipserc.arith.plot.SimpleGnuplot;

/**
 * Shared GNUPlot integration for {@link MatrixComplex} and the classes derived from it that plot
 * {@code MatrixComplex}-shaped sample data (row 0: x values, row 1: y values) -- {@code Fourier},
 * {@code Laplace} and {@code Z} (package {@code com.ipserc.arith.signal}) each used to carry their
 * own byte-for-byte identical copy of {@link #plot(String, int, MatrixComplex, boolean, e_lineStyle)}
 * and {@link #setLineStyle(e_lineStyle)}; consolidated here (8 agosto 2026, a peticion del usuario)
 * to remove that duplication. Also picks up {@code MatrixComplexFunctions.doPlot()} (the deviation
 * plot used by the Taylor/Mercator convergence loops), the other plotting code that lived in the
 * {@code matrixcomplex} package.
 * <p>
 * Unlike the package-private extraction pattern used elsewhere in this project ({@code
 * ComplexFormat}, {@code PolynomPlot}...), this class is {@code public}: {@code Fourier}/{@code
 * Laplace}/{@code Z} live in a different package ({@code com.ipserc.arith.signal}) and need to call
 * {@link #plot(String, int, MatrixComplex, boolean, e_lineStyle)} from there. Their OWN {@code
 * plot(...)} methods keep their exact original signatures (including their own, separate {@code
 * e_lineStyle} enum -- deliberately NOT unified with this class's, to avoid changing their public
 * API) and delegate to this class's implementation, converting the enum value at the boundary.
 * <p>
 * Scope note (8 agosto 2026): only the genuinely duplicated, generic pieces were moved here -- each
 * class's domain-specific plotting methods ({@code plotFunction}/{@code plotSamples}/{@code
 * plotSeries}/{@code plotCompare}/{@code plotDFT*}/{@code plotDLT*}/{@code plotZT*}) read private
 * instance state ({@code samples}, {@code transform}, {@code N}...) and stay in their own classes --
 * moving them here as well was considered and deferred to a future pass, at the user's request.
 * <p>
 * Continuation (8 agosto 2026, same day): {@code Laplace}/{@code Z} turned out to already delegate
 * their domain-specific methods into their own {@code plot(String, int, MatrixComplex, boolean,
 * e_lineStyle)} (itself delegating here) -- only {@code Fourier} still carried raw, hand-duplicated
 * {@code JavaPlot} boilerplate in {@code plotSamples}/{@code plotSeries}/{@code plotCompare}/{@code
 * plotDFTsamp}/{@code plotDFTfrec}, because those plot pre-computed {@code double[][]} series (not
 * a single {@code MatrixComplex}) and had no matching helper to delegate to. {@link
 * #plotSeries(String, e_lineStyle, double[][]...)} (and its labeled/logscale overload) close that
 * gap: they take already-computed series, so the domain-specific computation (which needs
 * {@code Fourier}'s private state and its {@code eval()}) stays in {@code Fourier}, only the
 * repeated {@code JavaPlot} construction/config tail moved here.
 * <p>
 * Migration (8 agosto 2026, continuacion, a peticion del usuario): {@code com.panayotis.gnuplot.
 * JavaPlot} replaced by {@link com.ipserc.arith.plot.SimpleGnuplot}, a dependency-free homegrown
 * replacement (same gnuplot engine underneath, only the Java wrapper changed) -- motivated by
 * {@code GNUPlotExec.plot()} unconditionally blocking on {@code Process.waitFor()} (confirmed by
 * bytecode inspection, no source available for that dependency), which {@code SimpleGnuplot} gives
 * full control over via {@code plot()} (sync, same contract as before) vs {@code plotAsync()} (new).
 * Method call shapes ({@code setTitle}/{@code addPlot}/{@code set}/{@code setPersist}/
 * {@code getPostInit}/{@code plot}) kept identical on purpose, so this was a drop-in swap.
 */
public class MatrixComplexPlot {

	/**
	 * Enumerative to set the style for gnuplot.
	 * LINES: connects adjacent points with straight line segments.
	 * IMPULSES: displays a vertical line from y=0 to the y value of each point.
	 */
	public static enum e_lineStyle {
		LINES, IMPULSES;
	}

	/**
	 * Returns the "style" set required as "data " LINES or IMPULSES for gnuplot.
	 * @param lineStyle The line style.
	 * @return The "style" set.
	 */
	public static String setLineStyle(e_lineStyle lineStyle) {
		String strLineStyle = "data ";
		switch (lineStyle) {
			case LINES: return strLineStyle + "lines";
			case IMPULSES: return strLineStyle + "impulses";
		}
		return strLineStyle + "lines";
	}

	/**
	 * Plots a graphic with the points given in 'data'. Row 0 is for the x axis values, Row 1 is
	 * for the y axis values. The values to plot are in the columns.
	 * @param title The title of the graphic.
	 * @param nbrSamples The number of samples (columns of {@code data}) to plot.
	 * @param data The points to be plotted.
	 * @param showIm If true plots the imaginary part in the graphic.
	 * @param lineStyle The line style.
	 */
	public static void plotSync(String title, int nbrSamples, MatrixComplex data, boolean showIm, e_lineStyle lineStyle) {
		plot(title, nbrSamples, data, showIm, lineStyle, SimpleGnuplot.e_syncMode.SYNC);
	}

	/** Same as {@link #plotSync(String, int, MatrixComplex, boolean, e_lineStyle)} but returns
	 * immediately -- the plot window stays open independently, without blocking the caller. */
	public static void plotAsync(String title, int nbrSamples, MatrixComplex data, boolean showIm, e_lineStyle lineStyle) {
		plot(title, nbrSamples, data, showIm, lineStyle, SimpleGnuplot.e_syncMode.ASYNC);
	}

	/** Generic entry point every {@code plotSync}/{@code plotAsync} pair calls -- also public so
	 * {@code Fourier}/{@code Laplace}/{@code Z} (a different package) can thread their OWN
	 * {@code mode} straight through instead of re-implementing the sync/async branch themselves. */
	public static void plot(String title, int nbrSamples, MatrixComplex data, boolean showIm, e_lineStyle lineStyle, SimpleGnuplot.e_syncMode mode) {
		// Split the data into Re and Im parts
		double dataRe[][] = new double[nbrSamples][2];
		double dataIm[][] = new double[nbrSamples][2];

		for (int t = 0; t < nbrSamples; ++t) {
			dataRe[t][0] = data.complexMatrix[0][t].rep();
			dataIm[t][0] = data.complexMatrix[0][t].rep();
			dataRe[t][1] = data.complexMatrix[1][t].rep();
			dataIm[t][1] = data.complexMatrix[1][t].imp();
		}

		// Plot the data
		SimpleGnuplot p = new SimpleGnuplot();
		p.setTitle(title);
		p.addPlot(dataRe);
		if (showIm) p.addPlot(dataIm);
		p.set("zeroaxis", "");
		p.set("style", setLineStyle(lineStyle));
		p.set("grid", "");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		p.plot(mode);
	}

	/**
	 * Plots one or more already-computed {@code [x,y]} series on the same canvas, no axis labels
	 * and no logarithmic scale. Shorthand for the labeled overload below.
	 * @param title The plot title.
	 * @param lineStyle The line style.
	 * @param series One or more {@code double[nbrPoints][2]} series to plot together.
	 */
	public static void plotSeriesSync(String title, e_lineStyle lineStyle, double[][]... series) {
		plotSeriesSync(title, null, null, false, lineStyle, series);
	}

	/** Same as {@link #plotSeriesSync(String, e_lineStyle, double[][]...)} but returns immediately
	 * -- the plot window stays open independently, without blocking the caller. */
	public static void plotSeriesAsync(String title, e_lineStyle lineStyle, double[][]... series) {
		plotSeriesAsync(title, null, null, false, lineStyle, series);
	}

	/**
	 * Plots one or more already-computed {@code [x,y]} series on the same canvas. Consolidates the
	 * {@code JavaPlot} construction/configuration tail that used to be hand-duplicated across
	 * {@code Fourier.plotSamples}/{@code plotSeries}/{@code plotCompare}/{@code plotDFTsamp}/{@code
	 * plotDFTfrec} -- the series themselves (which need each caller's own private state) are still
	 * computed by the caller and passed in already built.
	 * @param title The plot title.
	 * @param x2label Secondary x-axis label ({@code gnuplot}'s {@code x2label}), or {@code null} to omit.
	 * @param xlabel Primary x-axis label, or {@code null} to omit.
	 * @param logscale If true sets the y axis to logarithmic scale.
	 * @param lineStyle The line style.
	 * @param series One or more {@code double[nbrPoints][2]} series to plot together.
	 */
	public static void plotSeriesSync(String title, String x2label, String xlabel, boolean logscale, e_lineStyle lineStyle, double[][]... series) {
		plotSeries(title, x2label, xlabel, logscale, lineStyle, SimpleGnuplot.e_syncMode.SYNC, series);
	}

	/** Same as {@link #plotSeriesSync(String, String, String, boolean, e_lineStyle, double[][]...)}
	 * but returns immediately -- the plot window stays open independently, without blocking the
	 * caller. */
	public static void plotSeriesAsync(String title, String x2label, String xlabel, boolean logscale, e_lineStyle lineStyle, double[][]... series) {
		plotSeries(title, x2label, xlabel, logscale, lineStyle, SimpleGnuplot.e_syncMode.ASYNC, series);
	}

	/** Generic entry point every {@code plotSeriesSync}/{@code plotSeriesAsync} pair calls -- also
	 * public so {@code Fourier}/{@code Laplace}/{@code Z} can thread their OWN {@code mode} straight
	 * through. */
	public static void plotSeries(String title, String x2label, String xlabel, boolean logscale, e_lineStyle lineStyle, SimpleGnuplot.e_syncMode mode, double[][]... series) {
		SimpleGnuplot p = new SimpleGnuplot();
		p.setTitle(title);
		if (x2label != null) p.set("x2label", x2label);
		for (double[][] s : series) p.addPlot(s);
		p.set("zeroaxis", "");
		if (xlabel != null) p.set("xlabel", xlabel);
		p.set("style", setLineStyle(lineStyle));
		if (logscale) p.set("logscale", "y");
		p.set("grid", "");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		p.plot(mode);
	}

	/**
	 * Enumerative to set the gnuplot 3D data style ({@code splot}). Covers the 3 styles actually
	 * used across the project's 3D scripts (the other {@code data} styles gnuplot supports --
	 * {@code polygons}/{@code rgbalpha}/{@code isosurface}/{@code pm3d} -- appear only as
	 * commented-out alternatives in every one of those scripts, never live).
	 * LINES: connects adjacent points with straight line segments.
	 * BOXPLOT: draws each point as a small box (the most common choice in this project's scripts).
	 * SURFACE: draws a continuous surface across the points (needs a regular grid to look right).
	 */
	public static enum e_lineStyle3D {
		LINES, BOXPLOT, SURFACE;
	}

	/**
	 * Returns the "style" set required for the 3D data styles above.
	 * @param lineStyle The 3D line style.
	 * @return The "style" set.
	 */
	public static String setLineStyle3D(e_lineStyle3D lineStyle) {
		String strLineStyle = "data ";
		switch (lineStyle) {
			case LINES: return strLineStyle + "lines";
			case BOXPLOT: return strLineStyle + "boxplot";
			case SURFACE: return strLineStyle + "surface";
		}
		return strLineStyle + "boxplot";
	}

	/**
	 * Plots one or more already-computed {@code [x,y,z]} series as a 3D {@code splot}, no
	 * logarithmic Z scale. Shorthand for the logscale overload below.
	 * @param title The plot title.
	 * @param lineStyle The 3D line style.
	 * @param series One or more {@code double[nbrPoints][3]} series to plot together.
	 */
	public static void plotSeries3DSync(String title, e_lineStyle3D lineStyle, double[][]... series) {
		plotSeries3DSync(title, false, lineStyle, series);
	}

	/** Same as {@link #plotSeries3DSync(String, e_lineStyle3D, double[][]...)} but returns
	 * immediately -- the plot window stays open independently, without blocking the caller. */
	public static void plotSeries3DAsync(String title, e_lineStyle3D lineStyle, double[][]... series) {
		plotSeries3DAsync(title, false, lineStyle, series);
	}

	/**
	 * Plots one or more already-computed {@code [x,y,z]} series as a 3D {@code splot}. Consolidates
	 * the {@code SimpleGnuplot} construction/configuration tail that used to be hand-duplicated
	 * across the 8 3D scripts in {@code TestComplex/} ({@code TestSurfaceCosc01/02}, {@code
	 * TestSurfaceLog01}, {@code TestSurfacePolyn01}, {@code TestSurfaceSin01}, {@code
	 * TestSurfaceSinc01}, {@code TestZeta05}) -- the series themselves (which need each script's own
	 * function evaluation) are still computed by the caller and passed in already built, same
	 * division of responsibility as {@link #plotSeries(String, String, String, boolean, e_lineStyle,
	 * SimpleGnuplot.e_syncMode, double[][]...)} for 2D.
	 * @param title The plot title.
	 * @param logscaleZ If true sets the Z axis to logarithmic scale (only {@code TestSurfaceLog01}
	 * used this among the 8 scripts inventoried).
	 * @param lineStyle The 3D line style.
	 * @param series One or more {@code double[nbrPoints][3]} series to plot together.
	 */
	public static void plotSeries3DSync(String title, boolean logscaleZ, e_lineStyle3D lineStyle, double[][]... series) {
		plotSeries3D(title, logscaleZ, lineStyle, SimpleGnuplot.e_syncMode.SYNC, series);
	}

	/** Same as {@link #plotSeries3DSync(String, boolean, e_lineStyle3D, double[][]...)} but returns
	 * immediately -- the plot window stays open independently, without blocking the caller. */
	public static void plotSeries3DAsync(String title, boolean logscaleZ, e_lineStyle3D lineStyle, double[][]... series) {
		plotSeries3D(title, logscaleZ, lineStyle, SimpleGnuplot.e_syncMode.ASYNC, series);
	}

	/** Generic entry point every {@code plotSeries3DSync}/{@code plotSeries3DAsync} pair calls. */
	public static void plotSeries3D(String title, boolean logscaleZ, e_lineStyle3D lineStyle, SimpleGnuplot.e_syncMode mode, double[][]... series) {
		SimpleGnuplot p = new SimpleGnuplot();
		p.newGraph3D();
		p.setTitle(title);
		for (double[][] s : series) p.addPlot(s);
		p.set("zeroaxis", "");
		p.set("style", setLineStyle3D(lineStyle));
		if (logscaleZ) p.set("logscale", "z");
		p.set("grid", "");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		p.plot(mode);
	}

	/**
	 * Plots one or more already-computed {@code [x,y,z]} GRIDS as a real, CONNECTED 3D surface
	 * mesh -- unlike {@link #plotSeries3D(String, boolean, e_lineStyle3D, SimpleGnuplot.e_syncMode,
	 * double[][]...)}, which treats every point as part of one disconnected cloud, this uses
	 * {@link SimpleGnuplot#addPlotGrid(double[][][])} so gnuplot can tell where one "scan line"
	 * ends and the next begins.
	 * <p>
	 * Deliberately does NOT take an {@link e_lineStyle3D}: {@code LINES} is the only one of the 3
	 * that makes sense for gridded data ({@code BOXPLOT} would draw a disconnected box per point,
	 * ignoring the grid; a hypothetical {@code SURFACE} style was the original, unverified guess
	 * this method replaces -- see {@code Claude/ComplexArithRev.md}, 9 agosto 2026). {@code set
	 * hidden3d} is enabled so the mesh reads as an actual solid-looking surface instead of a
	 * see-through wireframe.
	 * @param title The plot title.
	 * @param logscaleZ If true sets the Z axis to logarithmic scale.
	 * @param mode {@code SYNC} to block until gnuplot exits, {@code ASYNC} to return immediately.
	 * @param grids One or more {@code double[rows][cols][3]} grids to plot together (every row of
	 * a given grid must have the same number of columns).
	 */
	public static void plotGrid3D(String title, boolean logscaleZ, SimpleGnuplot.e_syncMode mode, double[][][]... grids) {
		SimpleGnuplot p = new SimpleGnuplot();
		p.newGraph3D();
		p.setTitle(title);
		for (double[][][] g : grids) p.addPlotGrid(g);
		p.set("zeroaxis", "");
		p.set("style", setLineStyle3D(e_lineStyle3D.LINES));
		p.set("hidden3d", "");
		if (logscaleZ) p.set("logscale", "z");
		p.set("grid", "");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		p.plot(mode);
	}

	/**
	 * Plots one or more already-computed {@code [x,y,z]} grids as a real surface, no logarithmic
	 * Z scale. Shorthand for the logscale overload below.
	 */
	public static void plotGrid3DSync(String title, double[][][]... grids) {
		plotGrid3D(title, false, SimpleGnuplot.e_syncMode.SYNC, grids);
	}

	/** Same as {@link #plotGrid3DSync(String, double[][][]...)} but returns immediately -- the
	 * plot window stays open independently, without blocking the caller. */
	public static void plotGrid3DAsync(String title, double[][][]... grids) {
		plotGrid3D(title, false, SimpleGnuplot.e_syncMode.ASYNC, grids);
	}

	/** Same as {@link #plotGrid3DSync(String, double[][][]...)} but with an explicit logarithmic
	 * Z axis flag. */
	public static void plotGrid3DSync(String title, boolean logscaleZ, double[][][]... grids) {
		plotGrid3D(title, logscaleZ, SimpleGnuplot.e_syncMode.SYNC, grids);
	}

	/** Same as {@link #plotGrid3DSync(String, boolean, double[][][]...)} but returns immediately
	 * -- the plot window stays open independently, without blocking the caller. */
	public static void plotGrid3DAsync(String title, boolean logscaleZ, double[][][]... grids) {
		plotGrid3D(title, logscaleZ, SimpleGnuplot.e_syncMode.ASYNC, grids);
	}

	/**
	 * Plots a table (moved from {@code MatrixComplexFunctions}, all 8 call sites are there). Used
	 * to bring more info at debug for the Taylor/Mercator series convergence loops. No-op unless
	 * {@link MatrixComplex#doPlot()} is on.
	 * @param title The plot title.
	 * @param dataTable The table to plot.
	 * @param dataLen The number of valid rows in {@code dataTable}.
	 */
	static void doPlotSync(String title, double[][] dataTable, int dataLen) {
		doPlot(title, dataTable, dataLen, SimpleGnuplot.e_syncMode.SYNC);
	}

	static void doPlotAsync(String title, double[][] dataTable, int dataLen) {
		doPlot(title, dataTable, dataLen, SimpleGnuplot.e_syncMode.ASYNC);
	}

	private static void doPlot(String title, double[][] dataTable, int dataLen, SimpleGnuplot.e_syncMode mode) {
		if (!MatrixComplex.doPlot()) return;
		SimpleGnuplot p = new SimpleGnuplot();
		p.setTitle(title);
		double[][] fullDataTable = new double[dataLen][2];
		for (int i = 0; i < dataLen; ++i) fullDataTable[i] = dataTable[i];
		p.addPlot(fullDataTable);
		p.set("zeroaxis", "");
		p.set("style", "data lines");
		p.set("grid", "");
		// --- SOLUCION PARA QUE NO SE CONGELE EL ZOOM (METODOS NATIVOS) ---
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");
		// -------------------------------------------------------------
		p.plot(mode);
	}
}
