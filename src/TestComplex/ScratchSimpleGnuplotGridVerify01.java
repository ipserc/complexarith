package TestComplex;

import com.ipserc.arith.plot.SimpleGnuplot;

import java.lang.reflect.Method;

/**
 * Verifica SimpleGnuplot.addPlotGrid()/buildScript() (soporte de grid real para superficies 3D,
 * ver Claude/ComplexArithRev.md, 9 agosto 2026) SIN lanzar gnuplot -- inspecciona el texto exacto
 * del script via reflexion sobre buildScript() (privado).
 */
public class ScratchSimpleGnuplotGridVerify01 {
	public static void main(String[] args) throws Exception {
		Method m = SimpleGnuplot.class.getDeclaredMethod("buildScript");
		m.setAccessible(true);

		// Grid pequeno 2 filas x 3 columnas, valores reconocibles.
		double[][][] grid = {
			{ {0, 0, 1}, {0, 1, 2}, {0, 2, 3} },
			{ {1, 0, 4}, {1, 1, 5}, {1, 2, 6} }
		};

		SimpleGnuplot p = new SimpleGnuplot();
		p.newGraph3D();
		p.setTitle("test");
		p.addPlotGrid(grid);
		p.set("style", "data lines");
		p.set("hidden3d", "");
		p.set("grid", "");
		p.setPersist(true);
		p.getPostInit().add("set terminal windows");

		String script = (String) m.invoke(p);
		System.out.println("=== SCRIPT GENERADO ===");
		System.out.println(script);
		System.out.println("=== FIN SCRIPT ===");

		// Comprobaciones automaticas.
		String[] lines = script.split("\n", -1);
		int blankLinesInData = 0;
		boolean sawSplot = false, sawHidden3d = false, sawE = false;
		for (String line : lines) {
			if (line.startsWith("splot ")) sawSplot = true;
			if (line.equals("set hidden3d")) sawHidden3d = true;
			if (line.equals("e")) sawE = true;
		}
		// Cuenta las lineas en blanco DENTRO del bloque de datos (entre "splot ..." y "e").
		boolean inData = false;
		for (String line : lines) {
			if (line.startsWith("splot ")) { inData = true; continue; }
			if (line.equals("e")) { inData = false; continue; }
			if (inData && line.isEmpty()) ++blankLinesInData;
		}

		System.out.println("sawSplot=" + sawSplot + " sawHidden3d=" + sawHidden3d + " sawE=" + sawE);
		System.out.println("blankLinesInData=" + blankLinesInData + " (esperado: 2, una tras cada una de las 2 filas)");

		boolean ok = sawSplot && sawHidden3d && sawE && blankLinesInData == 2;
		System.out.println(ok ? "OK: formato de grid correcto" : "*** FALLO: revisar formato ***");

		// Verifica tambien que el orden de los valores dentro de cada fila es el esperado (row-major).
		int firstDataLine = -1;
		for (int i = 0; i < lines.length; ++i) if (lines[i].startsWith("splot ")) { firstDataLine = i + 1; break; }
		System.out.println("Primera linea de datos: \"" + lines[firstDataLine] + "\" (esperado: \"0.0 0.0 1.0 \")");
	}
}
