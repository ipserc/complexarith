package com.ipserc.arith.plot;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiles several independent {@link SimpleGnuplot} panels into ONE window/file via gnuplot's own
 * {@code set multiplot layout R,C} -- e.g. a function's Re/Im/Mod/Pha as 4 side-by-side panels
 * instead of 4 separate windows or several curves overlaid in the same axes. Each panel is a
 * plain {@link SimpleGnuplot} instance, reusing its full existing API ({@code addPlot}, {@code
 * addPlotGrid}, {@code set}, {@code newGraph3D}, per-series {@link PlotStyle}) unchanged -- a
 * panel simply never calls {@code plot(...)} on itself; this class collects several of them and
 * launches exactly 1 combined script for the whole group instead.
 * <p>
 * Deliberately kept as a NEW, additive class rather than a mode switch inside {@link
 * SimpleGnuplot} itself: multiplot was explicitly deferred when this plotting layer was first
 * designed because doing it required separating "build the script" from "launch the process" --
 * that split is now available as {@link SimpleGnuplot#buildPanelBody(boolean)}/{@link
 * SimpleGnuplot#terminalHeader(String, String)}/{@link SimpleGnuplot#runScript(String, boolean,
 * boolean)} (package-visible), reused here verbatim instead of duplicated. No existing caller of
 * {@link SimpleGnuplot} ({@code MatrixComplexPlot}, {@code PolynomPlot}, {@code BlochSphere},
 * {@code Fourier}/{@code Laplace}/{@code Z}, {@code Polynom}) is touched by this class's addition.
 */
public final class GnuplotMultiPlot {

	private final List<SimpleGnuplot> panels = new ArrayList<>();
	private Integer rows;
	private Integer cols;
	private String title;
	private Boolean persist;
	private String outputFile;
	private String terminal;

	/** Creates a new panel, adds it to this multiplot (in the order {@link #plot(SimpleGnuplot.e_syncMode)}
	 * will lay them out), and returns it so the caller can configure it exactly like a standalone
	 * {@link SimpleGnuplot} -- {@code addPlot}/{@code addPlotGrid}/{@code set}/{@code newGraph3D}/
	 * {@code setTitle} all work normally; the panel's own {@code plot(...)} is simply never called. */
	public SimpleGnuplot addPanel() {
		SimpleGnuplot panel = new SimpleGnuplot();
		panels.add(panel);
		return panel;
	}

	/**
	 * The grid shape, {@code set multiplot layout rows,cols}. Optional -- if never called, {@link
	 * #plot(SimpleGnuplot.e_syncMode)} defaults to 1 row with 1 column per panel (a simple
	 * side-by-side strip), computed from however many panels {@link #addPanel()} produced by then.
	 * @param rows Number of rows, must be at least 1.
	 * @param cols Number of columns, must be at least 1.
	 */
	public void layout(int rows, int cols) {
		if (rows < 1 || cols < 1) {
			throw new IllegalArgumentException("layout() needs rows>=1 and cols>=1, got rows=" + rows + " cols=" + cols);
		}
		this.rows = rows;
		this.cols = cols;
	}

	/** The overall multiplot title, {@code set multiplot ... title '...'} -- separate from any
	 * individual panel's own {@code setTitle(...)}. */
	public void setTitle(String title) {
		this.title = title;
	}

	/** See {@link SimpleGnuplot#setPersist(boolean)} -- applies to the combined multiplot window,
	 * not to any individual panel (panels don't launch their own process). */
	public void setPersist(boolean persist) {
		this.persist = persist;
	}

	/** See {@link SimpleGnuplot#setOutputFile(String)} -- applies once, to the whole multiplot. */
	public void setOutputFile(String path) {
		this.outputFile = path;
	}

	/** See {@link SimpleGnuplot#setTerminal(String)} -- applies once, to the whole multiplot. */
	public void setTerminal(String rawSpec) {
		this.terminal = rawSpec;
	}

	/**
	 * Builds ONE combined script (terminal header, {@code set multiplot layout ...}, every panel's
	 * body in the order {@link #addPanel()} was called, {@code unset multiplot}, {@code quit}) and
	 * launches gnuplot exactly once, via {@link SimpleGnuplot#runScript(String, boolean, boolean)}
	 * (the same process-spawning path {@link SimpleGnuplot#plot(SimpleGnuplot.e_syncMode)} uses).
	 * @param mode {@code SYNC} to block until the window is closed, {@code ASYNC} to return
	 * immediately.
	 * @throws IllegalStateException if {@link #addPanel()} was never called.
	 */
	public void plot(SimpleGnuplot.e_syncMode mode) {
		if (panels.isEmpty()) {
			throw new IllegalStateException("GnuplotMultiPlot.plot() called with no panels -- call addPanel() at least once");
		}
		String script = buildScript();
		boolean effectivePersist = outputFile == null && (persist != null ? persist : true);
		SimpleGnuplot.runScript(script, mode == SimpleGnuplot.e_syncMode.SYNC, effectivePersist);
	}

	private String buildScript() {
		StringBuilder sb = new StringBuilder();
		sb.append(SimpleGnuplot.terminalHeader(outputFile, terminal));
		if (outputFile == null && terminal == null) {
			sb.append(SimpleGnuplot.DEFAULT_TERMINAL).append('\n');
		}

		int effectiveRows = rows != null ? rows : 1;
		int effectiveCols = cols != null ? cols : panels.size();
		sb.append("set multiplot layout ").append(effectiveRows).append(',').append(effectiveCols);
		if (title != null) {
			sb.append(" title '").append(SimpleGnuplot.escape(title)).append('\'');
		}
		sb.append('\n');

		for (SimpleGnuplot panel : panels) {
			sb.append(panel.buildPanelBody(false));
		}
		sb.append("unset multiplot\n");
		sb.append("quit\n");
		return sb.toString();
	}
}
