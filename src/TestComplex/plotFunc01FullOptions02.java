package TestComplex;

import java.util.function.Function;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.NamedSeries;
import com.ipserc.arith.matrixcomplex.MatrixComplexPlot.e_lineStyle;
import com.ipserc.arith.plot.CanvasOptions;
import com.ipserc.arith.plot.PlotStyle;
import com.ipserc.arith.plot.SimpleGnuplot;

/**
 * Continuacion de {@link plotFunc01FullOptions}: la misma demo completa de la capa de plotting,
 * pero generalizando {@code plotFunc01.signalSin} para que la funcion Compleja aplicada sea un
 * PARAMETRO en vez de estar fijada a {@code Complex.exp}.
 * <p>
 * {@code com.ipserc.arith.complex.ComplexFunctions} (donde viven {@code sin}/{@code cos}/
 * {@code exp}/{@code sinh}/... de verdad) es <i>package-private</i> -- no se puede referenciar
 * desde {@code TestComplex}. Pero, segun su propio Javadoc de clase, "every public method that
 * moved here keeps an exact one-line delegator on Complex with the same signature" -- por eso
 * {@code Complex.sin(Complex)}, {@code Complex.cos(Complex)}, {@code Complex.exp(Complex)}, etc.
 * son la via publica para usarlas, y encajan exactamente en {@code Function<Complex,Complex>} via
 * referencia a metodo ({@code Complex::sin}, {@code Complex::exp}...).
 * <p>
 * {@code plotFunc01.signalSin(Complex,double,double)} no es mas que {@code amplitud *
 * exp(i*2*pi*freq*t)} con {@code exp} fijo -- generalizada aqui a {@code amplitud *
 * f(i*2*pi*freq*t)}, {@code calcFunc(Complex::exp)} reproduce exactamente esa misma senal.
 */
public class plotFunc01FullOptions02 {

	private static String stYrange = "[-6.5:6.5]";
	public record CalcResultFunc(String nombreFuncion, Complex amplitud, double[][] dataRe, double[][] dataIm) {}

	/**
	 * Calcula la senal {@code amplitud * complexFunc(2*pi*freq*t)} para {@code t=0..samples},
	 * con {@code complexFunc} como parametro -- cualquier funcion {@code Complex -> Complex} de
	 * {@code ComplexFunctions.java} vale, via su delegador publico en {@code Complex}.
	 * @param nombreFuncion Nombre de la funcion (solo para etiquetar la grafica), p.ej. {@code "tan"}.
	 * @param complexFunc La funcion a aplicar al angulo complejo, p.ej. {@code Complex::tan}/{@code Complex::sin}/{@code Complex::cos}.
	 */
	private static CalcResultFunc calcFunc(String nombreFuncion, Function<Complex, Complex> complexFunc) {
		double freq = 60;
		long samples = (long) (freq * 4); // 2 ciclos completos, suficientes puntos para ver la curva. OJO: samples en t^-1

		MatrixComplex data = new MatrixComplex((int) samples + 1, 1);
		Complex amplitud = new Complex("2");
		double twoPI = 2 * Math.PI;
		for (int t = 0; t <= samples; ++t) {
			Complex angulo = new Complex( twoPI / freq * t); //Complex.i.times(towPI * freq * t);
			data.complexMatrix[t][0] = amplitud.times(complexFunc.apply(angulo));
		}

		/*
		 * Split the payload in real an imag parts
		 */
		double[][] dataRe = new double[(int) samples + 1][2];
		double[][] dataIm = new double[(int) samples + 1][2];
		for (int t = 0; t <= samples; ++t) {
			dataRe[t][0] = t;
			dataIm[t][0] = t;
			dataRe[t][1] = data.complexMatrix[t][0].rep();
			dataIm[t][1] = data.complexMatrix[t][0].imp();
		}

		return new CalcResultFunc(nombreFuncion, amplitud, dataRe, dataIm);
	}

	/** Demo completa (PlotStyle + CanvasOptions + Sync/Async + exportar a fichero) para UNA sola
	 * senal -- igual que {@link plotFunc01FullOptions}, pero ahora {@code resultado} puede venir
	 * de cualquier funcion de {@code ComplexFunctions.java}, no solo {@code exp}. */
	private static void doPlot(CalcResultFunc resultado) {
		Complex amplitud = resultado.amplitud();
		double[][] dataRe = resultado.dataRe();
		double[][] dataIm = resultado.dataIm();
		String titulo = "plotFunc01 -- f=" + resultado.nombreFuncion();

		// --- 1) PlotStyle: cada curva con su propio color/grosor/tipo de linea ---
		PlotStyle estiloRe = new PlotStyle("red", 2, null, null, "linespoints"); // roja, gruesa, con puntos
		PlotStyle estiloIm = new PlotStyle("blue", 2, 2, null, "lines");         // azul, gruesa, discontinua (dashtype 2)

		NamedSeries curvaRe = new NamedSeries("Real", dataRe, estiloRe);
		NamedSeries curvaIm = new NamedSeries("Imaginaria", dataIm, estiloIm);

		// --- 2) CanvasOptions: rango del eje Y, leyenda arriba a la derecha, anotacion de texto ---
		CanvasOptions opciones = new CanvasOptions()
				.withSetting("yrange", stYrange)
				.withSetting("key", "top right box")
				.withPostInit("set label 'amplitud = " + amplitud.rep() + "' at graph 0.02,0.95");

		// --- 3a) Sync: bloquea el hilo hasta que CIERRAS la ventana a mano ---
		System.out.println("[Sync] antes de plotSeries (" + resultado.nombreFuncion() + ")...");
		MatrixComplexPlot.plotSeries(titulo + " (Sync)", null, null, false,
				e_lineStyle.LINES, SimpleGnuplot.e_syncMode.SYNC, opciones, curvaRe, curvaIm);
		System.out.println("[Sync] ...despues de plotSeries (solo se ve esto tras cerrar la ventana)");

		// --- 3b) Async: NO bloquea -- el siguiente println sale de inmediato ---
		System.out.println("[Async] antes de plotSeries (" + resultado.nombreFuncion() + ")...");
		MatrixComplexPlot.plotSeries(titulo + " (Async)", null, null, false,
				e_lineStyle.LINES, SimpleGnuplot.e_syncMode.ASYNC, opciones, curvaRe, curvaIm);
		System.out.println("[Async] ...esto sale ENSEGUIDA, sin esperar a que cierres la ventana anterior");

		// --- 4) Exportar a fichero PNG, sin abrir ninguna ventana ---
		String rutaExport = System.getProperty("java.io.tmpdir") + "plotFunc01_" + resultado.nombreFuncion() + "_export.png";
		CanvasOptions exportar = new CanvasOptions()
				.withSetting("yrange", stYrange)
				.withOutputFile(rutaExport);
		MatrixComplexPlot.plotSeries(titulo + " (export)", null, null, false,
				e_lineStyle.LINES, SimpleGnuplot.e_syncMode.ASYNC, exportar, curvaRe, curvaIm);
		System.out.println("Grafica exportada a " + rutaExport);
	}

	/** Compara la parte REAL de varias funciones de {@code ComplexFunctions.java} sobre la MISMA
	 * senal base, todas en un unico canvas -- cada {@code NamedSeries} con su propio color, para
	 * que se distingan a simple vista. */
	private static void doPlotComparacion(CalcResultFunc... resultados) {
		NamedSeries[] curvas = new NamedSeries[resultados.length];
		String[] colores = { "red", "blue", "dark-green", "orange", "purple" };
		for (int i = 0; i < resultados.length; ++i) {
			String color = colores[i % colores.length];
			curvas[i] = new NamedSeries("Re(" + resultados[i].nombreFuncion() + ")", resultados[i].dataRe(),
					new PlotStyle(color, 2, null, null, "lines"));
		}

		CanvasOptions opciones = new CanvasOptions()
				.withSetting("yrange", stYrange)
				.withSetting("key", "top right box");

		MatrixComplexPlot.plotSeries("plotFunc01 -- comparacion de funciones de ComplexFunctions.java", null, null, false,
				e_lineStyle.LINES, SimpleGnuplot.e_syncMode.ASYNC, opciones, curvas);
	}

	public static void main(String[] args) {
		// calcFunc(Complex::exp) reproduce EXACTAMENTE plotFunc01.signalSin -- misma demo completa
		// (PlotStyle/CanvasOptions/Sync/Async/exportar) que en plotFunc01FullOptions.java.
		CalcResultFunc resultadoTan = calcFunc("tan", Complex::tan);
		doPlot(resultadoTan);

		// El resto de funciones de ComplexFunctions.java, pasadas como el mismo parametro --
		// ninguna necesita su propio metodo signalXxx en plotFunc01.java.
		CalcResultFunc resultadoSin = calcFunc("sin", Complex::sin);
		CalcResultFunc resultadoCos = calcFunc("cos", Complex::cos);
		CalcResultFunc resultadoLn = calcFunc("ln", Complex::log);

		// Comparacion de las 4 en un solo canvas, cada una con su color -- demuestra NamedSeries +
		// PlotStyle con datos que vienen realmente de calcFunc(ComplexFunctions...).
		doPlotComparacion(resultadoSin, resultadoCos,resultadoTan, resultadoLn);
	}
}
