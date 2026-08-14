package TestComplex;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;

import com.ipserc.arith.plot.GnuplotMultiPlot;
import com.ipserc.arith.plot.PlotStyle;
import com.ipserc.arith.plot.SimpleGnuplot;

/**
 * Audit of GnuplotMultiPlot -- the "multiplot en la capa de plotting" candidate, deferred several
 * sessions when this plotting layer was first designed (see the class's own Javadoc for why). No
 * window is opened: same reflection-redirect technique as ScratchBlochSphereAudit01/
 * ScratchPlotPowerAudit01 (SimpleGnuplot.cachedExe pointed at a decoy .bat that captures the script
 * it receives over stdin instead of actually launching gnuplot).
 * <p>
 * Check 1 is the load-bearing regression check for the SimpleGnuplot refactor this class needed
 * (buildScript() split into terminalHeader()/buildPanelBody(boolean)/runScript()): a single-panel
 * SimpleGnuplot used exactly as before must still produce the EXACT SAME script, verified against
 * the script hand-traced from the pre-refactor code, not just "looks about right".
 */
public class ScratchGnuplotMultiPlotAudit01 {

	static int ok = 0, fail = 0;
	static File captured;
	static File decoyBat;

	static void redirectGnuplotToFake() throws Exception {
		captured = File.createTempFile("multiplot_script", ".txt");
		captured.deleteOnExit();
		decoyBat = File.createTempFile("findstr_decoy", ".bat");
		decoyBat.deleteOnExit();
		try (PrintWriter w = new PrintWriter(decoyBat)) {
			w.println("@echo off");
			w.println("C:\\Windows\\System32\\findstr.exe \"^\" > \"" + captured.getAbsolutePath() + "\"");
		}
		Field f = SimpleGnuplot.class.getDeclaredField("cachedExe");
		f.setAccessible(true);
		f.set(null, decoyBat.getAbsolutePath());
	}

	static String readAndClearCapture() throws IOException {
		if (!captured.isFile()) return "";
		String content = new String(Files.readAllBytes(captured.toPath()));
		captured.delete();
		return content;
	}

	public static void main(String[] args) throws Exception {
		redirectGnuplotToFake();

		// 1. A single-panel SimpleGnuplot, used exactly as any existing caller (MatrixComplexPlot,
		//    PolynomPlot, BlochSphere...) already does, produces the EXACT SAME script the
		//    pre-refactor monolithic buildScript() did -- hand-traced expected string, not just a
		//    "contains" check.
		SimpleGnuplot single = new SimpleGnuplot();
		single.setTitle("Single");
		single.addPlot(new double[][] { { 0, 0 }, { 1, 1 } }, "line");
		single.plot(SimpleGnuplot.e_syncMode.SYNC);
		String singleScript = readAndClearCapture();
		String expectedSingle = "set title 'Single'\n"
				+ "set terminal windows\n"
				+ "plot '-' title 'line'\n"
				+ "0.0 0.0 \n1.0 1.0 \n"
				+ "e\n"
				+ "quit\n";
		check("single-panel SimpleGnuplot.plot() unchanged after the buildScript() refactor", singleScript.equals(expectedSingle));

		// 2. A 2-panel multiplot: exactly 1 terminal directive and 1 "set multiplot" for the WHOLE
		//    script (not 1 per panel -- the reason buildPanelBody(false) exists), 2 "plot" commands
		//    (1 per panel, each with its own title/data/style), exactly 1 "unset multiplot", ends
		//    in "quit".
		GnuplotMultiPlot mp = new GnuplotMultiPlot();
		mp.setTitle("Comparativa");
		mp.layout(1, 2);
		SimpleGnuplot panelA = mp.addPanel();
		panelA.setTitle("Panel A");
		panelA.addPlot(new double[][] { { 0, 0 }, { 1, 2 } }, "seriesA");
		SimpleGnuplot panelB = mp.addPanel();
		panelB.setTitle("Panel B");
		panelB.addPlot(new double[][] { { 0, 1 }, { 1, 0 } }, "seriesB", new PlotStyle("red", 2, null, null, "lines"));
		mp.plot(SimpleGnuplot.e_syncMode.SYNC);
		String twoPanelScript = readAndClearCapture();
		boolean twoPanelOk = countOccurrences(twoPanelScript, "set terminal windows") == 1
				&& countOccurrences(twoPanelScript, "set multiplot layout 1,2 title 'Comparativa'") == 1
				&& countOccurrences(twoPanelScript, "plot '-'") == 2
				&& countOccurrences(twoPanelScript, "set title 'Panel A'") == 1
				&& countOccurrences(twoPanelScript, "set title 'Panel B'") == 1
				&& twoPanelScript.contains("linecolor rgb 'red'")
				&& countOccurrences(twoPanelScript, "unset multiplot") == 1
				&& twoPanelScript.trim().endsWith("quit");
		check("2-panel multiplot: exactly 1 terminal/multiplot directive, 2 plot commands, each panel's own title/style", twoPanelOk);

		// 3. layout() never called -- defaults to 1 row, 1 column PER panel (a simple strip).
		GnuplotMultiPlot mp2 = new GnuplotMultiPlot();
		mp2.addPanel().addPlot(new double[][] { { 0, 0 } }, "a");
		mp2.addPanel().addPlot(new double[][] { { 0, 0 } }, "b");
		mp2.addPanel().addPlot(new double[][] { { 0, 0 } }, "c");
		mp2.plot(SimpleGnuplot.e_syncMode.SYNC);
		String autoLayoutScript = readAndClearCapture();
		check("layout() never called defaults to 1 row x panels.size() cols", autoLayoutScript.contains("set multiplot layout 1,3"));

		// 4. plot() with no panels added throws IllegalStateException, no script launched at all.
		boolean emptyThrowsOk = false;
		try {
			new GnuplotMultiPlot().plot(SimpleGnuplot.e_syncMode.SYNC);
		} catch (IllegalStateException e) {
			emptyThrowsOk = true;
		}
		check("plot() with 0 panels throws IllegalStateException", emptyThrowsOk);

		// 5. layout() rejects rows<1 or cols<1.
		boolean rejectsBadLayoutOk = false;
		try {
			new GnuplotMultiPlot().layout(0, 2);
		} catch (IllegalArgumentException e) {
			rejectsBadLayoutOk = true;
		}
		check("layout() rejects rows<1", rejectsBadLayoutOk);

		// 6. Mixed 2D/3D panels in the same multiplot: each panel keeps its own plot/splot command
		//    independently (a panel's newGraph3D() doesn't leak into any other panel).
		GnuplotMultiPlot mp3 = new GnuplotMultiPlot();
		mp3.layout(1, 2);
		SimpleGnuplot flat = mp3.addPanel();
		flat.addPlot(new double[][] { { 0, 0 }, { 1, 1 } }, "2d");
		SimpleGnuplot surface = mp3.addPanel();
		surface.newGraph3D();
		surface.addPlotGrid(new double[][][] { { { 0, 0, 0 }, { 1, 0, 1 } }, { { 0, 1, 1 }, { 1, 1, 2 } } }, "3d");
		mp3.plot(SimpleGnuplot.e_syncMode.SYNC);
		String mixedScript = readAndClearCapture();
		boolean mixedOk = mixedScript.contains("plot '-' title '2d'") && mixedScript.contains("splot '-' title '3d'");
		check("mixed 2D/3D panels in the same multiplot keep independent plot/splot commands", mixedOk);

		// 7. setOutputFile() at the multiplot level applies ONCE for the whole combined script (not
		//    per panel), and suppresses the "set terminal windows" fallback.
		GnuplotMultiPlot mp4 = new GnuplotMultiPlot();
		mp4.setOutputFile("out.png");
		mp4.addPanel().addPlot(new double[][] { { 0, 0 } }, "a");
		mp4.addPanel().addPlot(new double[][] { { 0, 0 } }, "b");
		mp4.plot(SimpleGnuplot.e_syncMode.SYNC);
		String outputFileScript = readAndClearCapture();
		boolean outputFileOk = countOccurrences(outputFileScript, "set terminal pngcairo") == 1
				&& !outputFileScript.contains("set terminal windows")
				&& outputFileScript.contains("set output 'out.png'");
		check("setOutputFile() at the multiplot level applies once, no per-panel terminal fallback", outputFileOk);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static int countOccurrences(String haystack, String needle) {
		int count = 0, idx = 0;
		while ((idx = haystack.indexOf(needle, idx)) != -1) { ++count; idx += needle.length(); }
		return count;
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
