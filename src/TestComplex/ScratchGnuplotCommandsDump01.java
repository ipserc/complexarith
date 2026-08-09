package TestComplex;

/**
 * APARCADO (9 agosto 2026): dependia de com.panayotis.gnuplot.JavaPlot, retirado del proyecto
 * (classes/com/panayotis y doc/com/panayotis eliminados) tras el reemplazo completo por
 * com.ipserc.arith.plot.SimpleGnuplot (ver Claude/ComplexArithRev.md, Vigesima sesion). Ya cumplio
 * su proposito -- la sintaxis exacta que volcaba fue la base para SimpleGnuplot -- y no se borra
 * (regla del proyecto: los .java de test/verificacion se conservan), pero no compila sin la
 * dependencia, asi que el cuerpo queda comentado con el truco habitual del "* /" mal cerrado.
 * /
import com.panayotis.gnuplot.JavaPlot;

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
*/
