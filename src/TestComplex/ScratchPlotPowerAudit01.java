package TestComplex;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Arrays;

import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.plot.CanvasOptions;
import com.ipserc.arith.plot.PlotStyle;
import com.ipserc.arith.plot.SimpleGnuplot;
import com.ipserc.arith.polynom.Polynom;

/**
 * Verifies the "plotting capa mas potente" plan (Trigesimoquinta sesion, plan
 * smooth-scribbling-toast.md): {@link SimpleGnuplot}'s new {@code PlotStyle}/{@code CanvasOptions}/
 * {@code setOutputFile}/{@code setTerminal}, the centralized default terminal (replacing the
 * per-call-site "SOLUCION" boilerplate), and their passthrough via {@code MatrixComplexPlot}/
 * {@code Polynom} -- WITHOUT launching a real gnuplot window, same reflection-redirect technique as
 * {@code ScratchNamedSeriesAudit01} (Trigesimosegunda sesion).
 */
public class ScratchPlotPowerAudit01 {
	static int ok = 0, total = 0;
	static final String SCRATCH = "C:\\cygwin64\\tmp\\claude\\C--Users-josel-workspace-eclipse-complexarith-github\\e65d032d-9e70-4546-84cf-618d11e3f7fa\\scratchpad\\";
	static final File CAPTURE = new File(SCRATCH + "captured_script.txt");

	static void check(String name, boolean cond) {
		total++;
		if (cond) { ok++; System.out.println("OK   - " + name); }
		else System.out.println("FAIL - " + name);
	}

	static void redirectGnuplotToFake() throws Exception {
		Field f = SimpleGnuplot.class.getDeclaredField("cachedExe");
		f.setAccessible(true);
		f.set(null, SCRATCH + "fakegnuplot.bat");
	}

	static String readAndClearCapture() throws IOException {
		if (!CAPTURE.isFile()) return "";
		String content = new String(Files.readAllBytes(CAPTURE.toPath()));
		CAPTURE.delete();
		return content;
	}

	public static void main(String[] args) throws Exception {
		redirectGnuplotToFake();

		// --- Default path: unchanged from before this session (no PlotStyle/CanvasOptions/output) ---
		double[][] seriesA = { { 0, 0 }, { 1, 1 } };
		MatrixComplexPlot.plotSeriesSync("default path", MatrixComplexPlot.e_lineStyle.LINES, seriesA);
		String sDefault = readAndClearCapture();
		check("Default path still emits 'set terminal windows'", sDefault.contains("set terminal windows"));
		check("Default path emits NO 'set output'", !sDefault.contains("set output"));
		check("Default path term clause has no 'with' fragment (no style requested)",
				!sDefault.matches("(?s).*'-' title 'Series 1' with.*"));

		// --- SimpleGnuplot.setOutputFile: pngcairo + output, no interactive terminal, no -persist ---
		SimpleGnuplot pOut = new SimpleGnuplot();
		pOut.setTitle("export test");
		pOut.addPlot(seriesA, "Alpha");
		pOut.setOutputFile(SCRATCH + "plotexport.png");
		pOut.plot(SimpleGnuplot.e_syncMode.SYNC);
		String sOut = readAndClearCapture();
		check("setOutputFile -> 'set terminal pngcairo size 1024,768'", sOut.contains("set terminal pngcairo size 1024,768"));
		check("setOutputFile -> 'set output' with the given path", sOut.contains("set output '") && sOut.contains("plotexport.png'"));
		check("setOutputFile -> NO 'set terminal windows'", !sOut.contains("set terminal windows"));

		// --- SimpleGnuplot.setTerminal: raw escape hatch ---
		SimpleGnuplot pTerm = new SimpleGnuplot();
		pTerm.setTitle("terminal override test");
		pTerm.addPlot(seriesA);
		pTerm.setTerminal("svg size 800,600");
		pTerm.plot(SimpleGnuplot.e_syncMode.SYNC);
		String sTerm = readAndClearCapture();
		check("setTerminal -> 'set terminal svg size 800,600'", sTerm.contains("set terminal svg size 800,600"));
		check("setTerminal -> NO 'set terminal windows'", !sTerm.contains("set terminal windows"));

		// --- PlotStyle: per-series color/width/dashtype/pointtype ---
		PlotStyle style = new PlotStyle("red", 3, 2, 7, "linespoints");
		SimpleGnuplot pStyle = new SimpleGnuplot();
		pStyle.setTitle("style test");
		pStyle.addPlot(seriesA, "Styled", style);
		pStyle.addPlot(seriesA, "Plain");
		pStyle.plot(SimpleGnuplot.e_syncMode.SYNC);
		String sStyle = readAndClearCapture();
		check("PlotStyle -> 'with linespoints linecolor rgb ''red'' linewidth 3 dashtype 2 pointtype 7'",
				sStyle.contains("title 'Styled' with linespoints linecolor rgb 'red' linewidth 3 dashtype 2 pointtype 7"));
		check("PlotStyle -> unstyled series in the same script has no 'with' fragment",
				sStyle.contains("title 'Plain'") && !sStyle.contains("title 'Plain' with"));

		// --- MatrixComplexPlot: CanvasOptions passthrough (yrange/key position/label) + NamedSeries style ---
		MatrixComplexPlot.NamedSeries styledSeries = new MatrixComplexPlot.NamedSeries("Beta", seriesA, new PlotStyle("blue", null, null, null, null));
		CanvasOptions mcOptions = new CanvasOptions()
				.withSetting("yrange", "[-2:2]")
				.withSetting("key", "top left")
				.withPostInit("set label 'origin' at 0,0");
		MatrixComplexPlot.plotSeries("mcx canvas options", null, null, false, MatrixComplexPlot.e_lineStyle.LINES,
				SimpleGnuplot.e_syncMode.SYNC, mcOptions, styledSeries);
		String sMc = readAndClearCapture();
		check("MatrixComplexPlot CanvasOptions -> 'set yrange [-2:2]'", sMc.contains("set yrange [-2:2]"));
		check("MatrixComplexPlot CanvasOptions -> 'set key top left'", sMc.contains("set key top left"));
		check("MatrixComplexPlot CanvasOptions -> raw postInit label", sMc.contains("set label 'origin' at 0,0"));
		check("MatrixComplexPlot NamedSeries style -> 'linecolor rgb ''blue'''", sMc.contains("linecolor rgb 'blue'"));
		check("MatrixComplexPlot CanvasOptions path still gets the default terminal", sMc.contains("set terminal windows"));

		// --- Polynom: CanvasOptions passthrough reaches through PolynomPlot ---
		Polynom poly = new Polynom(1);
		double[][] pointsX = { { 0, 0 }, { 1, 1 } };
		CanvasOptions polyOptions = new CanvasOptions().withSetting("yrange", "[-5:5]");
		poly.plotSync(pointsX, "polynom canvas options", polyOptions);
		String sPoly = readAndClearCapture();
		check("Polynom.plotSync(...,CanvasOptions) -> 'set yrange [-5:5]'", sPoly.contains("set yrange [-5:5]"));
		check("Polynom.plotSync(...,CanvasOptions) path still gets the default terminal", sPoly.contains("set terminal windows"));

		Polynom polyExport = new Polynom(1);
		CanvasOptions exportOptions = new CanvasOptions().withOutputFile(SCRATCH + "polyexport.png");
		polyExport.plotExpressionSync(0, 1, exportOptions);
		String sPolyExport = readAndClearCapture();
		check("Polynom.plotExpressionSync(...,CanvasOptions.withOutputFile) -> pngcairo", sPolyExport.contains("set terminal pngcairo"));
		check("Polynom.plotExpressionSync(...,CanvasOptions.withOutputFile) -> NO windows terminal", !sPolyExport.contains("set terminal windows"));

		System.out.println();
		System.out.println(ok + "/" + total + " OK");
	}
}
