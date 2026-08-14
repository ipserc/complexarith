package TestComplex;

import com.ipserc.arith.plot.GnuplotMultiPlot;
import com.ipserc.arith.plot.PlotStyle;
import com.ipserc.arith.plot.SimpleGnuplot;

/**
 * Ejemplo comentado de GnuplotMultiPlot -- cómo tilear varios gráficos independientes en 1 sola
 * ventana, en vez de abrir 1 ventana por gráfico o superponer varias curvas en los mismos ejes.
 */
public class plotFuncMultiPlot01 {

	public static void main(String[] args) {

		// PASO 1: crear el contenedor del multiplot. Este objeto NO es un grafico en si mismo,
		// es el "marco" que va a agrupar varios paneles y lanzarlos como 1 sola ventana.
		GnuplotMultiPlot multiplot = new GnuplotMultiPlot();

		// PASO 2 (opcional): titulo general de la ventana completa, distinto del titulo de
		// cada panel individual (eso se pone panel a panel mas abajo).
		multiplot.setTitle("Ejemplo de multiplot: 4 funciones distintas");

		// PASO 3 (opcional): la disposicion en rejilla, filas x columnas. Si no se llama a
		// layout(), por defecto sale 1 sola fila con tantas columnas como paneles se hayan
		// anadido (una tira horizontal). Aqui pedimos una rejilla 2x2.
		multiplot.layout(2, 2);

		// Preparamos los datos de las 4 curvas de ejemplo. Cada dato es un array de puntos
		// [x, y] -- el mismo formato double[][] que ya usa SimpleGnuplot.addPlot() a solas.
		double[][] senoData = muestrear(x -> Math.sin(x));
		double[][] cosenoData = muestrear(x -> Math.cos(x));
		double[][] parabolaData = muestrear(x -> x * x);
		double[][] rectaData = muestrear(x -> 2 * x);

		// PASO 4: anadir un panel con addPanel(). Cada llamada crea y devuelve un
		// SimpleGnuplot NORMAL -- exactamente la misma clase que se usa para un grafico suelto,
		// con la MISMA API de siempre (setTitle/addPlot/set/newGraph3D...). La diferencia es que
		// este panel NUNCA llama a su propio .plot() -- quien lo lanza es el multiplot al final.
		SimpleGnuplot panelSeno = multiplot.addPanel();
		panelSeno.setTitle("sin(x)");
		// PlotStyle(color, grosorDeLinea, dashType, pointType, "with ...") -- aqui solo usamos
		// color y grosor, el resto a null para que gnuplot use su estilo por defecto en lo demas.
		panelSeno.addPlot(senoData, "sin(x)", new PlotStyle("blue", 2, null, null, "lines"));

		// Segundo panel: mismo patron, otro color.
		SimpleGnuplot panelCoseno = multiplot.addPanel();
		panelCoseno.setTitle("cos(x)");
		panelCoseno.addPlot(cosenoData, "cos(x)", new PlotStyle("red", 2, null, null, "lines"));

		// Tercer panel.
		SimpleGnuplot panelParabola = multiplot.addPanel();
		panelParabola.setTitle("x^2");
		panelParabola.addPlot(parabolaData, "x^2", new PlotStyle("dark-green", 2, null, null, "lines"));

		// Cuarto panel -- el orden de addPanel() es el orden en que se colocan en la rejilla,
		// de izquierda a derecha y de arriba a abajo (fila 1: seno,coseno -- fila 2: parabola,recta).
		SimpleGnuplot panelRecta = multiplot.addPanel();
		panelRecta.setTitle("2x");
		panelRecta.addPlot(rectaData, "2x", new PlotStyle("orange", 2, null, null, "lines"));

		// PASO 5: lanzar el multiplot. Esto construye 1 UNICO script de gnuplot con los 4
		// paneles dentro de un bloque "set multiplot ... unset multiplot", y arranca 1 SOLO
		// proceso gnuplot -- no 4 ventanas sueltas.
		//
		// SimpleGnuplot.e_syncMode.ASYNC (en vez de SYNC) hace que esta llamada vuelva de
		// inmediato sin bloquear el hilo Java, dejando la ventana abierta de forma independiente
		// -- util aqui para que el proceso Java pueda terminar sin esperar a que cierres la
		// ventana a mano.
		multiplot.plot(SimpleGnuplot.e_syncMode.ASYNC);

		System.out.println("Multiplot lanzado -- deberia haberse abierto 1 ventana con 4 paneles (2x2).");
	}

	/** Muestrea una funcion matematica en 50 puntos entre 0 y 10 -- helper solo para este
	 * ejemplo, nada especifico de GnuplotMultiPlot. */
	private static double[][] muestrear(java.util.function.DoubleUnaryOperator f) {
		int n = 50;
		double[][] data = new double[n][2];
		for (int i = 0; i < n; ++i) {
			double x = i * 10.0 / (n - 1);
			data[i][0] = x;
			data[i][1] = f.applyAsDouble(x);
		}
		return data;
	}
}
