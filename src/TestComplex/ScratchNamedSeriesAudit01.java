package TestComplex;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.plot.SimpleGnuplot;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.arith.signal.Fourier;

/**
 * Verifies the "named series" plan (Trigesimosegunda sesion, plan quirky-wondering-yao.md): every
 * layer that plots multiple curves (SimpleGnuplot -> MatrixComplexPlot -> Fourier -> Polynom) puts
 * the requested label in the gnuplot {@code title '...'} clause instead of the default
 * {@code "Series N"}, WITHOUT launching a real gnuplot window -- {@link SimpleGnuplot}'s cached
 * executable path is redirected (via reflection on its private {@code cachedExe} field) to a tiny
 * {@code findstr "^"} batch script that just copies stdin (the generated script) to a fixed file
 * and exits immediately, so {@code SYNC} mode calls return right away and the exact text gnuplot
 * would have received can be inspected afterwards.
 */
public class ScratchNamedSeriesAudit01 {
	static int ok = 0, total = 0;
	static final File CAPTURE = new File("C:\\cygwin64\\tmp\\claude\\C--Users-josel-workspace-eclipse-complexarith-github\\3f9ebec3-0e3e-499d-95d8-0655dfd19761\\scratchpad\\captured_script.txt");

	static void check(String name, boolean cond) {
		total++;
		if (cond) { ok++; System.out.println("OK   - " + name); }
		else System.out.println("FAIL - " + name);
	}

	static void redirectGnuplotToFake() throws Exception {
		Field f = SimpleGnuplot.class.getDeclaredField("cachedExe");
		f.setAccessible(true);
		f.set(null, "C:\\cygwin64\\tmp\\claude\\C--Users-josel-workspace-eclipse-complexarith-github\\3f9ebec3-0e3e-499d-95d8-0655dfd19761\\scratchpad\\fakegnuplot.bat");
	}

	static String readAndClearCapture() throws IOException {
		if (!CAPTURE.isFile()) return "";
		String content = new String(Files.readAllBytes(CAPTURE.toPath()));
		CAPTURE.delete();
		return content;
	}

	public static void main(String[] args) throws Exception {
		redirectGnuplotToFake();

		// --- Layer 1: SimpleGnuplot engine itself ---
		SimpleGnuplot p1 = new SimpleGnuplot();
		p1.setTitle("engine test");
		p1.addPlot(new double[][] { { 0, 0 }, { 1, 1 } }, "Labeled2D");
		p1.addPlot(new double[][] { { 0, 0 }, { 1, 1 } }); // no label -> default
		p1.plot(SimpleGnuplot.e_syncMode.SYNC);
		String s1 = readAndClearCapture();
		check("SimpleGnuplot.addPlot(data,label) -> title 'Labeled2D'", s1.contains("title 'Labeled2D'"));
		check("SimpleGnuplot.addPlot(data) (no label) -> default 'Series 2'", s1.contains("title 'Series 2'"));

		SimpleGnuplot p2 = new SimpleGnuplot();
		p2.newGraph3D();
		p2.setTitle("grid test");
		p2.addPlotGrid(new double[][][] { { { 0, 0, 0 }, { 1, 1, 1 } } }, "LabeledGrid");
		p2.plot(SimpleGnuplot.e_syncMode.SYNC);
		String s2 = readAndClearCapture();
		check("SimpleGnuplot.addPlotGrid(grid,label) -> title 'LabeledGrid'", s2.contains("title 'LabeledGrid'"));

		// --- Layer 2: MatrixComplexPlot ---
		double[][] seriesA = { { 0, 0 }, { 1, 1 } };
		double[][] seriesB = { { 0, 0 }, { 1, -1 } };
		MatrixComplexPlot.plotSeriesSync("mcx named 2D", MatrixComplexPlot.e_lineStyle.LINES,
				new MatrixComplexPlot.NamedSeries("Alpha", seriesA), new MatrixComplexPlot.NamedSeries("Beta", seriesB));
		String s3 = readAndClearCapture();
		check("MatrixComplexPlot.plotSeriesSync(NamedSeries...) -> title 'Alpha'", s3.contains("title 'Alpha'"));
		check("MatrixComplexPlot.plotSeriesSync(NamedSeries...) -> title 'Beta'", s3.contains("title 'Beta'"));

		MatrixComplexPlot.plotSeriesSync("mcx unnamed 2D", MatrixComplexPlot.e_lineStyle.LINES, seriesA, seriesB);
		String s3b = readAndClearCapture();
		check("MatrixComplexPlot.plotSeriesSync(double[][]...) old overload still defaults to 'Series N'",
				s3b.contains("title 'Series 1'") && s3b.contains("title 'Series 2'"));

		double[][] seriesA3D = { { 0, 0, 0 }, { 1, 1, 1 } };
		MatrixComplexPlot.plotSeries3DSync("mcx named 3D", MatrixComplexPlot.e_lineStyle3D.LINES,
				new MatrixComplexPlot.NamedSeries("Gamma", seriesA3D));
		String s4 = readAndClearCapture();
		check("MatrixComplexPlot.plotSeries3DSync(NamedSeries...) -> title 'Gamma'", s4.contains("title 'Gamma'"));

		double[][][] grid3D = { { { 0, 0, 0 }, { 1, 1, 1 } } };
		MatrixComplexPlot.plotGrid3DSync("mcx named grid", new MatrixComplexPlot.NamedGrid("Delta", grid3D));
		String s5 = readAndClearCapture();
		check("MatrixComplexPlot.plotGrid3DSync(NamedGrid...) -> title 'Delta'", s5.contains("title 'Delta'"));

		// plot(...) (fixed Re/Im pair) default labels
		MatrixComplex data = new MatrixComplex(2, 3);
		for (int t = 0; t < 3; ++t) {
			data.setItem(0, t, new Complex(t, 0));
			data.setItem(1, t, new Complex(t, -t));
		}
		MatrixComplexPlot.plotSync("mcx ReIm defaults", 3, data, true, MatrixComplexPlot.e_lineStyle.LINES);
		String s6 = readAndClearCapture();
		check("MatrixComplexPlot.plot(...) fixed pair -> title 'Real'", s6.contains("title 'Real'"));
		check("MatrixComplexPlot.plot(...) fixed pair -> title 'Imaginary'", s6.contains("title 'Imaginary'"));

		// --- Layer 3: Fourier ---
		Fourier fourier = new Fourier(z -> z, new Complex(-1, 0), new Complex(1, 0)); // f(t) = t
		fourier.serialize(3, 6);
		fourier.plotSeriesSync("fourier series", 8, true, Fourier.e_lineStyle.LINES);
		String s7 = readAndClearCapture();
		check("Fourier.plotSeriesSync(...) -> title 'Real'", s7.contains("title 'Real'"));
		check("Fourier.plotSeriesSync(...) -> title 'Imaginary'", s7.contains("title 'Imaginary'"));

		fourier.plotCompareSync(8, Fourier.e_lineStyle.LINES);
		String s8 = readAndClearCapture();
		check("Fourier.plotCompareSync(...) -> title 'Fourier Series'", s8.contains("title 'Fourier Series'"));
		check("Fourier.plotCompareSync(...) -> title 'Function'", s8.contains("title 'Function'"));

		// --- Layer 5: Polynom / PolynomPlot (List<...> + labels) ---
		Polynom poly = new Polynom(1);
		double[][] pointsX = { { 0, 0 }, { 1, 1 } };
		double[][] pointsY = { { 0, 0 }, { 1, 2 } };
		poly.plotSync(Arrays.asList(pointsX, pointsY), Arrays.asList("Curve X", "Curve Y"), "polynom named list");
		String s9 = readAndClearCapture();
		check("Polynom.plotSync(List,List<String>,title) -> title 'Curve X'", s9.contains("title 'Curve X'"));
		check("Polynom.plotSync(List,List<String>,title) -> title 'Curve Y'", s9.contains("title 'Curve Y'"));

		poly.plotSync(Arrays.asList(pointsX, pointsY), "polynom unnamed list");
		String s9b = readAndClearCapture();
		check("Polynom.plotSync(List,title) old overload still defaults to 'Series N'",
				s9b.contains("title 'Series 1'") && s9b.contains("title 'Series 2'"));

		List<MatrixComplex> mcList = Arrays.asList(data, data);
		poly.plotReSync(mcList, Arrays.asList("MC Re A", "MC Re B"), "polynom named MC list");
		String s10 = readAndClearCapture();
		check("Polynom.plotReSync(List<MatrixComplex>,List<String>,title) -> title 'MC Re A'", s10.contains("title 'MC Re A'"));
		check("Polynom.plotReSync(List<MatrixComplex>,List<String>,title) -> title 'MC Re B'", s10.contains("title 'MC Re B'"));

		System.out.println();
		System.out.println(ok + "/" + total + " OK");
	}
}
