package TestComplex;

import java.util.function.Function;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.QFT;
import com.ipserc.arith.signal.Fourier;
import com.ipserc.arith.signal.Fourier.e_domain;
import com.ipserc.arith.signal.Fourier.e_lineStyle;
import com.ipserc.arith.signal.Fourier.e_operator;

/**
 * Puente entre la QFT ({@link QFT#circuit(int)}) y la DFT clásica ({@link Fourier#DFT(int)}):
 * ambas son, salvo escala y signo del exponente, la misma transformación. {@code QFT.matrix(n)}
 * usa {@code M[k][j] = (1/sqrt(N)) * e^(+2*pi*i*j*k/N)} (signo positivo), que es el mismo signo
 * que {@link Fourier#IDFT()} (no el de {@link Fourier#DFT(int)}, que usa signo negativo y sin
 * normalizar). Por eso, para comparar contra {@code Fourier.DFT()} hay que aplicar la QFT
 * INVERSA -- la adjunta de {@code circuit(n)}, ya que es unitaria (verificado en
 * {@code ScratchQFTAudit01}) -- no {@code circuit(n)} directamente.
 */
public class TestQuantum_QFTBridge01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	public static void main(String[] args) {
		Complex.setFormatON();
		Complex.setFixedON(4);
		int boxMargin = 65;
		int boxShape = 3;

		Complex.printBoxText(boxShape, boxMargin, "Puente QFT <-> DFT clasica, n=1..6");

		// n=1..6: no basta con un único caso "de suerte" -- cada n reusa la misma fórmula de señal,
		// genuinamente compleja (no solo real), para que ambos signos del exponente se ejerciten de
		// verdad y no coincidan por casualidad de simetría conjugada.
		for (int n = 1; n <= 6; ++n) {
			int N = 1 << n;	// muestras discretas

			Complex.printBoxText(boxShape, boxMargin, "n=" + n + " qubits, N=" + N + " muestras");

			// Señal periódica discreta compleja, 2 armónicos: x[t] = e^(i*2*pi*t/N) + 0.5*cos(2*pi*t/N)
			Function<Complex, Complex> signal = t -> Complex.exp(Complex.i.times(2.0 * Math.PI * t.rep() / N))
					.plus(new Complex(0.5 * Math.cos(2.0 * Math.PI * t.rep() / N), 0.0));

			// DFT clásica de referencia. loLimit=0, upLimit=N con N muestras da paso 1: doTrfSampling()
			// samplea exactamente en t=0,1,...,N-1, igual que el índice de la QFT.
			Fourier fourier = new Fourier(signal, 0.0, (double) N);
			fourier.DFT(N);
			fourier.plotDFTSync("DFT clasica de referencia", e_domain.FREC, e_operator.MAGNITUDE, false, e_lineStyle.LINES);

			// Mismas N muestras, como vector de álgebra lineal |x> = sum_n x[n]|n> -- sin normalizar:
			// esto NO es un estado cuántico físico, es la matriz QFT aplicada como transformación lineal.
			MatrixComplex xVec = new MatrixComplex(N, 1);
			for (int t = 0; t < N; ++t) {
				xVec.setItem(t, 0, signal.apply(new Complex(t, 0)));
			}

			MatrixComplex u = QFT.circuit(n);

			/*
			 * (U^dagger * x)[k] = (1/sqrt(N)) * sum_j e^(-2*pi*i*j*k/N) * x[j] = (1/sqrt(N)) * DFT(x)[k]
			 * -- la QFT inversa lleva el mismo signo negativo que Fourier.DFT(), solo falta deshacer el
			 * 1/sqrt(N).
			 */
			MatrixComplex yFromInverseQFT = u.adjoint().times(xVec).times(Math.sqrt(N));

			boolean matchesDFT = true;
			for (int k = 0; k < N; ++k) {
				Complex got = yFromInverseQFT.getItem(k, 0);
				Complex expected = fourier.getTransformItem(k);
				if (got.minus(expected).mod() > 1e-9) { matchesDFT = false; }
			}
			check("n=" + n + ": QFT.circuit(n).adjoint()*x, escalado por sqrt(N), == Fourier.DFT(x)", matchesDFT);

			/*
			 * Viaje de vuelta: aplicar la QFT hacia ADELANTE (u, sin adjunta) al vector de coeficientes
			 * espectrales de Fourier.DFT() reconstruye las muestras de tiempo originales, escaladas por
			 * sqrt(N) -- confirma que "QFT hacia adelante" es, salvo escala, la IDFT clásica, no la DFT.
			 */
			MatrixComplex XVec = new MatrixComplex(N, 1);
			for (int k = 0; k < N; ++k) {
				XVec.setItem(k, 0, fourier.getTransformItem(k));
			}

			MatrixComplex xBack = u.times(XVec).divides(Math.sqrt(N));

			boolean roundTripOk = true;
			for (int t = 0; t < N; ++t) {
				if (xBack.getItem(t, 0).minus(xVec.getItem(t, 0)).mod() > 1e-9) { roundTripOk = false; }
			}
			check("n=" + n + ": QFT.circuit(n)*DFT(x), escalado por 1/sqrt(N), recupera las muestras x originales", roundTripOk);
		}

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}

}
