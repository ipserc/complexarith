package com.ipserc.arith.plot;

/**
 * Per-series look override for {@link SimpleGnuplot#addPlot(double[][], String, PlotStyle)} and
 * its {@code addPlotGrid}/expression siblings -- color, line width, dash type, point type, and the
 * gnuplot {@code with <style>} keyword itself. Every field is nullable: {@code null} means "leave
 * that aspect at gnuplot's own default" (the {@code set style data ...} global, or gnuplot's own
 * color cycling). Plain public final fields, same pattern as {@code MatrixComplexPlot.NamedSeries}/
 * {@code NamedGrid} -- no builder needed for 5 fields.
 */
public final class PlotStyle {

	/** No override at all -- equivalent to passing {@code null} as the style. */
	public static final PlotStyle DEFAULT = new PlotStyle(null, null, null, null, null);

	/** A gnuplot color spec (e.g. {@code "red"}, {@code "#1f77b4"}), or {@code null} for gnuplot's own color cycling. */
	public final String color;
	/** {@code linewidth}, or {@code null} to omit. */
	public final Integer lineWidth;
	/** {@code dashtype}, or {@code null} to omit. */
	public final Integer dashType;
	/** {@code pointtype}, or {@code null} to omit. */
	public final Integer pointType;
	/** The gnuplot {@code with <style>} keyword (e.g. {@code "linespoints"}, {@code "steps"}), or
	 * {@code null} to fall back to {@code "lines"} when any other field here is set (gnuplot
	 * requires a style keyword to attach linecolor/linewidth/dashtype/pointtype to). */
	public final String plotWith;

	public PlotStyle(String color, Integer lineWidth, Integer dashType, Integer pointType, String plotWith) {
		this.color = color;
		this.lineWidth = lineWidth;
		this.dashType = dashType;
		this.pointType = pointType;
		this.plotWith = plotWith;
	}

	/** {@code true} if every field is {@code null} -- no {@code with ...} clause should be emitted. */
	boolean isDefault() {
		return color == null && lineWidth == null && dashType == null && pointType == null && plotWith == null;
	}
}
