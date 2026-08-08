package com.ipserc.arith.matrixcomplex;

import com.panayotis.gnuplot.JavaPlot;

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
	public static void plot(String title, int nbrSamples, MatrixComplex data, boolean showIm, e_lineStyle lineStyle) {
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
		JavaPlot p = new JavaPlot();
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
		p.plot();
	}

	/**
	 * Plots one or more already-computed {@code [x,y]} series on the same canvas, no axis labels
	 * and no logarithmic scale. Shorthand for the labeled overload below.
	 * @param title The plot title.
	 * @param lineStyle The line style.
	 * @param series One or more {@code double[nbrPoints][2]} series to plot together.
	 */
	public static void plotSeries(String title, e_lineStyle lineStyle, double[][]... series) {
		plotSeries(title, null, null, false, lineStyle, series);
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
	public static void plotSeries(String title, String x2label, String xlabel, boolean logscale, e_lineStyle lineStyle, double[][]... series) {
		JavaPlot p = new JavaPlot();
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
		p.plot();
	}

	/**
	 * Plots a table (moved from {@code MatrixComplexFunctions}, all 8 call sites are there). Used
	 * to bring more info at debug for the Taylor/Mercator series convergence loops. No-op unless
	 * {@link MatrixComplex#doPlot()} is on.
	 * @param title The plot title.
	 * @param dataTable The table to plot.
	 * @param dataLen The number of valid rows in {@code dataTable}.
	 */
	static void doPlot(String title, double[][] dataTable, int dataLen) {
		if (!MatrixComplex.doPlot()) return;
		JavaPlot p = new JavaPlot();
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
		p.plot();
	}
}
