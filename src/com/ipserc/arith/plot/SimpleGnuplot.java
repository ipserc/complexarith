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
 * Motivation (8 agosto 2026, a peticion del usuario): {@code GNUPlotExec.plot()} (the base of the
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
	private final List<Object> plotTerms = new ArrayList<>(); // each element: double[][] (data block) or String (gnuplot expression)
	private boolean persist = false;
	private boolean is3D = false;

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
		plotTerms.add(data);
	}

	/** Adds a native gnuplot expression term (e.g. {@code "sin(x)"}, {@code "[-2:4] x**2+1"}) --
	 * gnuplot evaluates it directly, no sampling in Java. */
	public void addPlot(String expression) {
		plotTerms.add(expression);
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
			ProcessBuilder pb = persist ? new ProcessBuilder(exe, "-persist") : new ProcessBuilder(exe);
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

	private String buildScript() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> e : settings.entrySet()) {
			sb.append("set ").append(e.getKey());
			if (e.getValue() != null && !e.getValue().isEmpty()) sb.append(' ').append(e.getValue());
			sb.append('\n');
		}
		if (title != null) sb.append("set title '").append(escape(title)).append("'\n");
		for (String raw : postInit) sb.append(raw).append('\n');

		List<String> termClauses = new ArrayList<>();
		List<double[][]> dataBlocks = new ArrayList<>();
		int seriesN = 0;
		for (Object term : plotTerms) {
			if (term instanceof String) {
				String expr = (String) term;
				termClauses.add(expr + " title '" + escape(expr) + "'");
			} else {
				++seriesN;
				termClauses.add("'-' title 'Series " + seriesN + "'");
				dataBlocks.add((double[][]) term);
			}
		}
		sb.append(is3D ? "splot " : "plot ").append(String.join(", ", termClauses)).append('\n');
		for (double[][] data : dataBlocks) {
			for (double[] row : data) {
				for (double v : row) sb.append(v).append(' ');
				sb.append('\n');
			}
			sb.append("e\n");
		}
		sb.append("quit\n");
		return sb.toString();
	}

	private static String escape(String s) {
		return s.replace("'", "\\'");
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

	public static class SimpleGnuplotException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public SimpleGnuplotException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
