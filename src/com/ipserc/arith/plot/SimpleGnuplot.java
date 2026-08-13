package com.ipserc.arith.plot;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal, dependency-free replacement for {@code com.panayotis.gnuplot.JavaPlot} -- covers exactly
 * the API surface this project actually uses (confirmed by a project-wide grep before writing this:
 * {@code setTitle}/{@code addPlot(double[][]/String)}/{@code set}/{@code setPersist}/
 * {@code getPostInit}/{@code newGraph3D}/{@code plot}), nothing more.
 * <p>
 * Motivation (August 8, 2026, at the user's request): {@code GNUPlotExec.plot()} (the base of the
 * whole Panayotis chain) unconditionally calls {@code Process.waitFor()} after launching gnuplot --
 * confirmed by inspecting its bytecode (no source available for that dependency in this project).
 * On Windows, with a persistent window, that blocks the calling thread until the user closes the
 * plot. This class gives full control over that (see {@link #plot(e_syncMode)}), and drops the
 * external dependency entirely -- no more copying {@code classes/com/panayotis/*.class} to the output
 * directory to compile against it (a documented environment workaround, see project memory
 * "classpath ';' corrupto en Bash tool"/"javac trunca lotes grandes").
 * <p>
 * The exact gnuplot script syntax below was reverse-engineered from Panayotis's own output (via its
 * {@code getCommands()}, which only builds the string, no process spawned) for every pattern
 * actually used in this project -- not guessed. Verified live (user-confirmed window rendering) with
 * a hand-rolled script equivalent to this class's output before writing any of the 2 real call
 * sites this class replaces ({@code MatrixComplexPlot}, {@code PolynomPlot}).
 */
public class SimpleGnuplot {

	private String title;
	private final Map<String, String> settings = new LinkedHashMap<>();
	private final List<String> postInit = new ArrayList<>();
	private final List<Object> plotTerms = new ArrayList<>(); // each element: Term (data block, labeled) or ExprTerm (native expression, labeled) or String (native expression, legacy)

	/** A labeled data block -- {@code data} is {@code double[][]} (flat) or {@code double[][][]} (grid); {@code label} is {@code null} for the default {@code "Series N"} title; {@code style} is {@code null} for gnuplot's own default look. */
	private static final class Term {
		final Object data;
		final String label;
		final PlotStyle style;

		Term(Object data, String label, PlotStyle style) {
			this.data = data;
			this.label = label;
			this.style = style;
		}
	}

	/** A native gnuplot expression term with an explicit style -- kept separate from the plain
	 * {@code String} case (added via {@link #addPlot(String)}) so that path stays byte-identical
	 * to before this class supported per-series styling. */
	private static final class ExprTerm {
		final String expression;
		final PlotStyle style;

		ExprTerm(String expression, PlotStyle style) {
			this.expression = expression;
			this.style = style;
		}
	}

	/** {@code null} means "not set by the caller" -- see {@link #launch(boolean)} for the default
	 * this resolves to. */
	private Boolean persist;
	private boolean is3D = false;
	private String outputFile;
	private String terminal;

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Sets a raw gnuplot {@code set <key> <value>} option. An empty {@code value} emits a
	 * flag-only {@code set <key>} (e.g. {@code set("grid", "")} -> {@code set grid}).
	 */
	public void set(String key, String value) {
		settings.put(key, value);
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}

	/**
	 * Routes the plot to a file instead of an interactive window: emits {@code set terminal
	 * pngcairo size 1024,768} + {@code set output '<path>'} ahead of every other setting, and
	 * forces the process to run without {@code -persist} (there is no window to keep open). For
	 * any other output format, use {@link #setTerminal(String)} plus a raw {@code set("output",
	 * "'<path>'")} instead.
	 * @param path The output file path (extension should match the terminal, e.g. {@code .png}).
	 */
	public void setOutputFile(String path) {
		this.outputFile = path;
	}

	/**
	 * Raw escape hatch for any gnuplot terminal not covered by {@link #setOutputFile(String)}
	 * (e.g. {@code "svg size 800,600"}, {@code "pdfcairo"}). Emitted verbatim as {@code set
	 * terminal <rawSpec>} ahead of every other setting. Ignored if {@link #setOutputFile(String)}
	 * was also called -- that convenience method takes precedence.
	 * @param rawSpec The raw terminal spec, without the leading {@code "set terminal "}.
	 */
	public void setTerminal(String rawSpec) {
		this.terminal = rawSpec;
	}

	/** Raw gnuplot commands appended verbatim after the settings, before the plot/splot command. */
	public List<String> getPostInit() {
		return postInit;
	}

	/** Switches subsequent {@link #addPlot} calls to 3D ({@code splot}) instead of 2D ({@code plot}). */
	public void newGraph3D() {
		this.is3D = true;
	}

	/** Adds a data series: each row is one point ({@code [x,y]} for 2D, {@code [x,y,z]} for 3D). */
	public void addPlot(double[][] data) {
		addPlot(data, null, null);
	}

	/** Same as {@link #addPlot(double[][])}, but with an explicit legend title instead of the default {@code "Series N"}. */
	public void addPlot(double[][] data, String label) {
		addPlot(data, label, null);
	}

	/** Same as {@link #addPlot(double[][], String)}, but with an explicit {@link PlotStyle}
	 * instead of gnuplot's own default look for the series (color/width/dashtype/pointtype). */
	public void addPlot(double[][] data, String label, PlotStyle style) {
		plotTerms.add(new Term(data, label, style));
	}

	/**
	 * Adds a GRIDDED 3D data series: {@code grid[row][col]} is one {@code [x,y,z]} point. Unlike
	 * {@link #addPlot(double[][])} (a flat, unstructured point list -- gnuplot draws it as a
	 * disconnected cloud, or connects EVERY point in sequence for {@code lines}), this emits a
	 * blank line after each {@code row} in the data block, gnuplot's own convention for marking
	 * the end of a "scan line" -- the format {@code splot} needs to connect points into an actual
	 * surface mesh (each row forms one isoline; blank lines keep {@code lines}/{@code pm3d} from
	 * connecting across rows). Only meaningful in 3D mode (see {@link #newGraph3D()}); every row
	 * must have the same number of columns for the resulting mesh to be well-formed.
	 * @param grid The grid of points, {@code grid[row][col] = {x, y, z}}.
	 */
	public void addPlotGrid(double[][][] grid) {
		addPlotGrid(grid, null, null);
	}

	/** Same as {@link #addPlotGrid(double[][][])}, but with an explicit legend title instead of the default {@code "Series N"}. */
	public void addPlotGrid(double[][][] grid, String label) {
		addPlotGrid(grid, label, null);
	}

	/** Same as {@link #addPlotGrid(double[][][], String)}, but with an explicit {@link PlotStyle}
	 * instead of gnuplot's own default look for the surface. */
	public void addPlotGrid(double[][][] grid, String label, PlotStyle style) {
		plotTerms.add(new Term(grid, label, style));
	}

	/** Adds a native gnuplot expression term (e.g. {@code "sin(x)"}, {@code "[-2:4] x**2+1"}) --
	 * gnuplot evaluates it directly, no sampling in Java. */
	public void addPlot(String expression) {
		plotTerms.add(expression);
	}

	/** Same as {@link #addPlot(String)}, but with an explicit {@link PlotStyle} for the curve. */
	public void addPlot(String expression, PlotStyle style) {
		plotTerms.add(new ExprTerm(expression, style));
	}

	/**
	 * Selects whether {@link #plot(e_syncMode)} blocks until gnuplot exits ({@code SYNC}, same
	 * contract {@code JavaPlot.plot()} had) or returns immediately after launching it
	 * ({@code ASYNC}, the plot window stays open independently, without blocking the caller).
	 */
	public enum e_syncMode {
		SYNC, ASYNC;
	}

	/** Builds the script and launches gnuplot, blocking or not per {@code mode}. The single entry
	 * point every {@code xxxSync}/{@code xxxAsync} pair in the higher plotting layers
	 * ({@code MatrixComplexPlot}, {@code PolynomPlot}, {@code Polynom}, {@code Fourier}/
	 * {@code Laplace}/{@code Z}) ultimately calls, so the sync-vs-async decision lives in one
	 * place. */
	public void plot(e_syncMode mode) {
		launch(mode == e_syncMode.SYNC);
	}

	private void launch(boolean wait) {
		String script = buildScript();
		String exe = resolveGnuplotExecutable();
		try {
			// outputFile != null => rendering to a file, no window to keep alive -- ignore
			// whatever persist may have been set to. Otherwise: honor an explicit setPersist(),
			// or fall back to the project's long-standing default (persistent window, so the
			// native zoom doesn't freeze -- see DEFAULT_TERMINAL below) when the caller never
			// called setPersist() at all.
			boolean effectivePersist = outputFile == null && (persist != null ? persist : true);
			ProcessBuilder pb = effectivePersist ? new ProcessBuilder(exe, "-persist") : new ProcessBuilder(exe);
			Process proc = pb.start();
			try (OutputStream out = proc.getOutputStream()) {
				out.write(script.getBytes(StandardCharsets.UTF_8));
				out.flush();
			}
			if (wait) proc.waitFor();
		} catch (IOException | InterruptedException e) {
			throw new SimpleGnuplotException("Error launching gnuplot (\"" + exe + "\"): " + e.getMessage(), e);
		}
	}

	// The literal terminal directive every caller of this class used to append by hand to
	// getPostInit() -- see the class Javadoc's "SOLUCION" note history. Centralized here (instead
	// of repeated ~20 times across MatrixComplexPlot/PolynomPlot) as the implicit default when
	// neither setOutputFile(String) nor setTerminal(String) was called -- byte-identical script
	// output for every caller that never touches those 2 new methods.
	private static final String DEFAULT_TERMINAL = "set terminal windows";

	private String buildScript() {
		StringBuilder sb = new StringBuilder();
		if (outputFile != null) {
			sb.append("set terminal pngcairo size 1024,768\n");
			sb.append("set output '").append(escape(outputFile)).append("'\n");
		} else if (terminal != null) {
			sb.append("set terminal ").append(terminal).append('\n');
		}
		for (Map.Entry<String, String> e : settings.entrySet()) {
			sb.append("set ").append(e.getKey());
			if (e.getValue() != null && !e.getValue().isEmpty()) sb.append(' ').append(e.getValue());
			sb.append('\n');
		}
		if (title != null) sb.append("set title '").append(escape(title)).append("'\n");
		for (String raw : postInit) sb.append(raw).append('\n');
		if (outputFile == null && terminal == null && !postInit.contains(DEFAULT_TERMINAL)) {
			sb.append(DEFAULT_TERMINAL).append('\n');
		}

		List<String> termClauses = new ArrayList<>();
		List<Object> dataBlocks = new ArrayList<>(); // each element: double[][] (flat) or double[][][] (grid)
		int seriesN = 0;
		for (Object term : plotTerms) {
			if (term instanceof String) {
				String expr = (String) term;
				termClauses.add(expr + " title '" + escape(expr) + "'");
			} else if (term instanceof ExprTerm) {
				ExprTerm et = (ExprTerm) term;
				termClauses.add(et.expression + " title '" + escape(et.expression) + "'" + styleClause(et.style));
			} else {
				++seriesN;
				Term t = (Term) term;
				String label = t.label != null ? t.label : "Series " + seriesN;
				termClauses.add("'-' title '" + escape(label) + "'" + styleClause(t.style));
				dataBlocks.add(t.data);
			}
		}
		sb.append(is3D ? "splot " : "plot ").append(String.join(", ", termClauses)).append('\n');
		for (Object block : dataBlocks) {
			if (block instanceof double[][][]) {
				// Gridded surface: a blank line after each row marks the end of a "scan line",
				// so splot connects points WITHIN a row but not across rows -- the format needed
				// for a real connected mesh instead of a disconnected point cloud.
				for (double[][] row : (double[][][]) block) {
					for (double[] point : row) {
						for (double v : point) sb.append(v).append(' ');
						sb.append('\n');
					}
					sb.append('\n');
				}
			} else {
				for (double[] row : (double[][]) block) {
					for (double v : row) sb.append(v).append(' ');
					sb.append('\n');
				}
			}
			sb.append("e\n");
		}
		sb.append("quit\n");
		return sb.toString();
	}

	private static String escape(String s) {
		return s.replace("'", "\\'");
	}

	/** Builds the {@code with ...} clause fragment for a per-series {@link PlotStyle}, or {@code ""}
	 * (no fragment at all) for {@code null}/{@link PlotStyle#DEFAULT} -- so the script stays
	 * byte-identical for every series that doesn't opt into custom styling. When any field is set
	 * but {@link PlotStyle#plotWith} isn't, gnuplot still needs a style keyword after {@code with}
	 * to attach linecolor/linewidth/dashtype/pointtype to -- {@code "lines"} is used, matching the
	 * project's own default ({@code set style data lines}) everywhere it's used today. */
	private static String styleClause(PlotStyle style) {
		if (style == null || style.isDefault()) return "";
		StringBuilder sb = new StringBuilder(" with ").append(style.plotWith != null ? style.plotWith : "lines");
		if (style.color != null) sb.append(" linecolor rgb '").append(escape(style.color)).append('\'');
		if (style.lineWidth != null) sb.append(" linewidth ").append(style.lineWidth);
		if (style.dashType != null) sb.append(" dashtype ").append(style.dashType);
		if (style.pointType != null) sb.append(" pointtype ").append(style.pointType);
		return sb.toString();
	}

	// Cached after first resolution -- same absolute-path-first strategy Panayotis's own
	// FileUtils.findPathExec() uses, because ProcessBuilder/CreateProcess on Windows does NOT do
	// PATHEXT resolution the way a shell does: "gnuplot" (no extension) is not found via
	// ProcessBuilder even when "gnuplot.exe" exists and is reachable from a shell -- confirmed live
	// this session (CreateProcess error=2 with the bare name, works with the resolved absolute path).
	private static volatile String cachedExe;

	private static synchronized String resolveGnuplotExecutable() {
		if (cachedExe != null) return cachedExe;
		String[] fixedCandidates = {
				"/usr/bin/gnuplot", "/usr/local/bin/gnuplot", "/bin/gnuplot",
				"/opt/local/bin/gnuplot", "/opt/bin/gnuplot", "/sw/bin/gnuplot",
				"C:\\Program Files\\gnuplot\\bin\\gnuplot.exe",
				"C:\\Program Files (x86)\\gnuplot\\bin\\gnuplot.exe",
				"C:\\Program Files\\gnuplot\\bin\\wgnuplot.exe",
				"C:\\Program Files (x86)\\gnuplot\\bin\\wgnuplot.exe",
		};
		for (String c : fixedCandidates) {
			if (new File(c).isFile()) return cachedExe = c;
		}
		String path = System.getenv("PATH");
		if (path != null) {
			for (String dir : path.split(File.pathSeparator)) {
				for (String name : new String[] { "gnuplot", "gnuplot.exe", "wgnuplot.exe" }) {
					File f = new File(dir, name);
					if (f.isFile()) return cachedExe = f.getAbsolutePath();
				}
			}
		}
		return cachedExe = "gnuplot"; // last resort: let the OS try to resolve it
	}

	/** Thrown when launching or communicating with the external {@code gnuplot} process fails. */
	public static class SimpleGnuplotException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public SimpleGnuplotException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
