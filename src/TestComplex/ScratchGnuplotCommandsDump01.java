package TestComplex;

import com.panayotis.gnuplot.JavaPlot;

/**
 * Reverse-engineers the EXACT gnuplot script text panayotis/JavaPlot generates for the API surface
 * actually used across this project (confirmed narrow by grep: setTitle, addPlot(double[][]),
 * newGraph3D, set(key,value), setPersist, getPostInit().add, plot()) -- via getCommands(), which
 * only builds the string (GNUPlotExec.getCommands(), no process spawn, no waitFor(), safe to call
 * without opening any window). This is the scoping step for a possible homegrown replacement of
 * com.panayotis.gnuplot: need to know the exact syntax to replicate, not guess it.
 */
public class ScratchGnuplotCommandsDump01 {
	public static void main(String[] args) throws Exception {
		System.out.println("=== 2D, single series, MatrixComplexPlot.plot()-style ===");
		JavaPlot p1 = new JavaPlot();
		p1.setTitle("PRUEBA");
		p1.addPlot(new double[][] { { 0, 1.5 }, { 1, 2.5 }, { 2, 0.5 } });
		p1.set("zeroaxis", "");
		p1.set("style", "data lines");
		p1.set("grid", "");
		p1.setPersist(true);
		p1.getPostInit().add("set terminal windows");
		System.out.println(p1.getCommands());

		System.out.println();
		System.out.println("=== 2D, TWO series (Re/Im), plotSeries()-style ===");
		JavaPlot p2 = new JavaPlot();
		p2.setTitle("dos series");
		p2.set("x2label", "x2");
		p2.addPlot(new double[][] { { 0, 1 }, { 1, 2 } });
		p2.addPlot(new double[][] { { 0, 0.1 }, { 1, 0.2 } });
		p2.set("zeroaxis", "");
		p2.set("xlabel", "x");
		p2.set("style", "data impulses");
		p2.set("logscale", "y");
		p2.set("grid", "");
		System.out.println(p2.getCommands());

		System.out.println();
		System.out.println("=== 3D surface, newGraph3D()-style (TestSurfaceCosc01 pattern) ===");
		JavaPlot p3 = new JavaPlot();
		p3.newGraph3D();
		p3.setTitle("superficie");
		p3.addPlot(new double[][] { { 0, 0, 1 }, { 0, 1, 2 }, { 1, 0, 3 }, { 1, 1, 4 } });
		p3.set("style", "data boxplot");
		p3.set("grid", "");
		System.out.println(p3.getCommands());
	}
}
