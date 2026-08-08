package TestComplex;

import com.ipserc.arith.plot.SimpleGnuplot;

/**
 * Live verification (user-authorized) of the real com.ipserc.arith.plot.SimpleGnuplot class, end to
 * end, before migrating MatrixComplexPlot.java/PolynomPlot.java to use it. Two cases, matching the
 * two real usage patterns found in those 2 files: (1) MatrixComplexPlot-style, two double[][]
 * series with title/zeroaxis/style/grid/persist/postInit; (2) PolynomPlot-style, a native gnuplot
 * expression term via addPlot(String) with an inline range, plus set(xrange)/set(key,"noautotitle").
 */
public class ScratchSimpleGnuplotLive01 {
	public static void main(String[] args) {
		System.out.println("=== Caso 2 primero: estilo PolynomPlot (expresion nativa, async) ===");
		SimpleGnuplot p2 = new SimpleGnuplot();
		p2.addPlot("[-5:5] x**3 - 3*x");
		p2.setTitle("SimpleGnuplot - caso 2 (PolynomPlot-style, x^3-3x)");
		p2.set("zeroaxis", "");
		p2.set("key", "noautotitle");
		p2.set("samples", "300");
		p2.setPersist(true);
		p2.getPostInit().add("set terminal windows");
		long t0 = System.currentTimeMillis();
		p2.plotAsync();
		long t1 = System.currentTimeMillis();
		System.out.println("plotAsync() volvio en " + (t1 - t0) + " ms -- deberia ser casi instantaneo");

		System.out.println();
		System.out.println("=== Caso 1 despues: estilo MatrixComplexPlot (dos series double[][], SYNC -- bloquea aqui) ===");
		SimpleGnuplot p1 = new SimpleGnuplot();
		p1.setTitle("SimpleGnuplot - caso 1 (MatrixComplexPlot-style)");
		p1.addPlot(new double[][] { { 0, 0 }, { 1, 1 }, { 2, 4 }, { 3, 9 } });
		p1.addPlot(new double[][] { { 0, 0 }, { 1, -1 }, { 2, -2 }, { 3, -1 } });
		p1.set("zeroaxis", "");
		p1.set("style", "data lines");
		p1.set("grid", "");
		p1.setPersist(true);
		p1.getPostInit().add("set terminal windows");
		p1.plot();
		System.out.println("caso 1 (sync) devuelto -- deberia haber esperado a que cerraras esta ventana");
	}
}
