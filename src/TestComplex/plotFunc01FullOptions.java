package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.NamedSeries;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.e_lineStyle;
import com.ipserc.arith.plot.CanvasOptions;
import com.ipserc.arith.plot.PlotStyle;
import com.ipserc.arith.plot.SimpleGnuplot;

/**
 * Caso completo de uso de la capa de plotting (sesion "hacer la clase plot mas potente", 13 agosto
 * 2026), sobre la misma senal senoidal compleja de {@link plotFunc01} (reutiliza su {@code
 * signalSin(Complex,double,double)}, sin tocar ese fichero -- tiene ediciones locales tuyas sin
 * commitear). Muestra las 5 capacidades nuevas en un solo sitio:
 * <ol>
 *   <li>{@link PlotStyle} por serie (color/grosor/dashtype/pointtype), via {@link NamedSeries}.</li>
 *   <li>{@link CanvasOptions} (rango del eje Y, posicion de la leyenda, anotacion de texto).</li>
 *   <li>La diferencia real entre {@code Sync} y {@code Async} (los {@code println} alrededor de
 *       cada llamada lo hacen visible: con {@code Sync} el segundo bloque de {@code println} no
 *       sale hasta que cierras la ventana a mano; con {@code Async} sale de inmediato).</li>
 *   <li>Exportar a fichero PNG en vez de ventana interactiva.</li>
 * </ol>
 * <b>Nota:</b> a diferencia de {@code plotFunc01} (7577 Hz, ~15000 muestras), aqui se usa una
 * frecuencia mucho menor para que la grafica sea legible a simple vista -- la logica de la senal
 * (misma llamada a {@code signalSin}) es exactamente la misma.
 */
public class plotFunc01FullOptions {

	public static void main(String[] args) {
		double freq = 60;
		long samples = (long) (freq * 4); // 2 ciclos completos, suficientes puntos para ver la curva

		MatrixComplex data = new MatrixComplex((int) samples + 1, 1);
		Complex amplitud = new Complex("2");
		for (int t = 0; t <= samples; ++t) {
			data.complexMatrix[t][0] = plotFunc01.signalSin(amplitud, 1 / freq, t);
		}

		double[][] dataRe = new double[(int) samples + 1][2];
		double[][] dataIm = new double[(int) samples + 1][2];
		for (int t = 0; t <= samples; ++t) {
			dataRe[t][0] = t;
			dataIm[t][0] = t;
			dataRe[t][1] = data.complexMatrix[t][0].rep();
			dataIm[t][1] = data.complexMatrix[t][0].imp();
		}

		// --- 1) PlotStyle: cada curva con su propio color/grosor/tipo de linea ---
		PlotStyle estiloRe = new PlotStyle("red", 2, null, null, "linespoints"); // roja, gruesa, con puntos
		PlotStyle estiloIm = new PlotStyle("blue", 2, 2, null, "lines");         // azul, gruesa, discontinua (dashtype 2)

		NamedSeries curvaRe = new NamedSeries("Real", dataRe, estiloRe);
		NamedSeries curvaIm = new NamedSeries("Imaginaria", dataIm, estiloIm);

		// --- 2) CanvasOptions: rango del eje Y, leyenda arriba a la derecha, anotacion de texto ---
		CanvasOptions opciones = new CanvasOptions()
				.withSetting("yrange", "[-2.5:2.5]")
				.withSetting("key", "top right box")
				.withPostInit("set label 'amplitud = " + amplitud.rep() + "' at graph 0.02,0.95");

		// --- 3a) Sync: bloquea el hilo hasta que CIERRAS la ventana a mano ---
		System.out.println("[Sync] antes de plotSeries...");
		MatrixComplexPlot.plotSeries("plotFunc01 -- PlotStyle + CanvasOptions (Sync)", null, null, false,
				e_lineStyle.LINES, SimpleGnuplot.e_syncMode.SYNC, opciones, curvaRe, curvaIm);
		System.out.println("[Sync] ...despues de plotSeries (solo se ve esto tras cerrar la ventana)");

		// --- 3b) Async: NO bloquea -- el siguiente println sale de inmediato ---
		System.out.println("[Async] antes de plotSeries...");
		MatrixComplexPlot.plotSeries("plotFunc01 -- misma grafica, Async", null, null, false,
				e_lineStyle.LINES, SimpleGnuplot.e_syncMode.ASYNC, opciones, curvaRe, curvaIm);
		System.out.println("[Async] ...esto sale ENSEGUIDA, sin esperar a que cierres la ventana anterior");

		// --- 4) Exportar a fichero PNG, sin abrir ninguna ventana ---
		String rutaExport = System.getProperty("java.io.tmpdir") + "plotFunc01_export.png";
		CanvasOptions exportar = new CanvasOptions()
				.withSetting("yrange", "[-2.5:2.5]")
				.withOutputFile(rutaExport);
		MatrixComplexPlot.plotSeries("plotFunc01 -- exportado a PNG", null, null, false,
				e_lineStyle.LINES, SimpleGnuplot.e_syncMode.SYNC, exportar, curvaRe, curvaIm);
		System.out.println("Grafica exportada a " + rutaExport);
	}
}
