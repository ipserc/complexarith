package TestComplex;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Live verification (user-authorized) of a homegrown gnuplot launcher, with ZERO dependency on
 * com.panayotis.gnuplot: builds the exact script text reverse-engineered via getCommands() in
 * ScratchGnuplotCommandsDump01.java, feeds it to gnuplot through stdin (no temp file, simpler than
 * Panayotis's own approach) via ProcessBuilder, and launches it WITHOUT waitFor() -- proving the
 * async behavior works as a natural side effect of not depending on GNUPlotExec.plot()'s blocking
 * design.
 * <p>
 * Prints a heartbeat AFTER launching the plot, before the program exits, to prove Java control
 * returns immediately while the gnuplot window stays open (persist).
 */
public class ScratchHomegrownGnuplotLive01 {

	// Panayotis's own FileUtils.findPathExec() resolves this exact path first on Windows
	// (hardcoded fallback list, since ProcessBuilder does not do PATHEXT resolution like a shell
	// does -- "gnuplot" without ".exe" is not found even though the file exists and is on no PATH
	// Git Bash sees either; confirmed by ls).
	static final String GNUPLOT_EXE = "C:\\Program Files\\gnuplot\\bin\\gnuplot.exe";

	static void launchGnuplot(String script, boolean persist, boolean wait) throws IOException, InterruptedException {
		ProcessBuilder pb = persist
				? new ProcessBuilder(GNUPLOT_EXE, "-persist")
				: new ProcessBuilder(GNUPLOT_EXE);
		pb.redirectErrorStream(true);
		Process proc = pb.start();
		try (OutputStream out = proc.getOutputStream()) {
			out.write(script.getBytes(StandardCharsets.UTF_8));
			out.flush();
		}
		if (wait) proc.waitFor();
	}

	public static void main(String[] args) throws Exception {
		String script =
				"set grid\n" +
				"set style data lines\n" +
				"set title 'Homegrown launcher (sin Panayotis)'\n" +
				"set zeroaxis\n" +
				"set terminal windows\n" +
				"plot '-' title 'Datafile 1'\n" +
				"0.0 0.0\n" +
				"1.0 1.5\n" +
				"2.0 0.7\n" +
				"3.0 2.3\n" +
				"4.0 1.1\n" +
				"e\n" +
				"quit\n";

		System.out.println("Lanzando gnuplot (persist, SIN esperar -- async) ...");
		long t0 = System.currentTimeMillis();
		launchGnuplot(script, true, false);
		long t1 = System.currentTimeMillis();
		System.out.println("launchGnuplot() volvio en " + (t1 - t0) + " ms (deberia ser casi instantaneo si es async de verdad)");
		System.out.println("El programa Java sigue ejecutandose -- la ventana deberia quedarse abierta.");
	}
}
