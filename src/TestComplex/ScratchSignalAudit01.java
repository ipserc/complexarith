package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.signal.Fourier;
import com.ipserc.arith.signal.Laplace;
import com.ipserc.arith.signal.Sigfunc;
import com.ipserc.arith.signal.Z;

/**
 * Auditoria matematica dedicada de signal.* (Vigesimosexta sesion, bloque 5 de la hoja de ruta
 * "Matematicas Aplicadas"): Fourier, Laplace, Z, Sigfunc. Verifica identidades conocidas
 * (round-trip transformada/inversa, casos de referencia cruzados entre las 3 transformadas) -- no
 * solo lectura de codigo.
 */
public class ScratchSignalAudit01 {

	static int pass = 0, fail = 0;

	static void checkClose(String label, Complex actual, Complex expected, double tol) {
		double err = actual.minus(expected).mod();
		boolean ok = err < tol;
		if (ok) ++pass; else ++fail;
		System.out.printf("%-70s %-4s err=%.3e%n", label, ok ? "OK" : "***FAIL***", err);
	}

	static void checkTrue(String label, boolean cond) {
		if (cond) ++pass; else ++fail;
		System.out.printf("%-70s %s%n", label, cond ? "OK" : "***FAIL***");
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();
		Complex.setFixedON(8);
		double tol = 1e-6;
		int N = 8;

		System.out.println("=== Sigfunc: ramp()/saw() -- periodicidad y rango de amplitud ===");
		double T = 4, a = 2;
		boolean rampBounded = true, rampPeriodic = true, sawBounded = true, sawPeriodic = true;
		for (double t = -10; t <= 10; t += 0.37) {
			double r0 = Sigfunc.ramp(new Complex(t, 0), (int) T, a).rep();
			double r1 = Sigfunc.ramp(new Complex(t + 2 * T, 0), (int) T, a).rep();
			if (Math.abs(r0) > a + 1e-9) rampBounded = false;
			if (Math.abs(r0 - r1) > 1e-9) rampPeriodic = false;
			double s0 = Sigfunc.saw(new Complex(t, 0), (int) T, a).rep();
			double s1 = Sigfunc.saw(new Complex(t + 2 * T, 0), (int) T, a).rep();
			if (Math.abs(s0) > a + 1e-9) sawBounded = false;
			if (Math.abs(s0 - s1) > 1e-9) sawPeriodic = false;
		}
		checkTrue("ramp() acotada dentro de [-a,a]", rampBounded);
		checkTrue("ramp() periodica de periodo 2T", rampPeriodic);
		checkTrue("saw() acotada dentro de [-a,a]", sawBounded);
		checkTrue("saw() periodica de periodo T", sawPeriodic);

		System.out.println("\n=== Fourier: DFT/IDFT round-trip, senal compleja ===");
		Complex[] refSignal = new Complex[N];
		for (int n = 0; n < N; ++n) refSignal[n] = new Complex(Math.sin(n) + 1, Math.cos(2 * n));

		Fourier fourier = new Fourier(z -> refSignal[(int) Math.round(z.rep())], 0, N);
		fourier.DFT(N);
		fourier.IDFT();
		for (int n = 0; n < N; ++n) {
			checkClose("IDFT(DFT(x))[" + n + "] == x[" + n + "]", fourier.getSampleItem(n), refSignal[n], tol);
		}

		System.out.println("\n=== Fourier: DFT de una señal constante == delta en k=0 (DC), 0 en el resto ===");
		Fourier fourierConst = new Fourier(z -> new Complex(3, 0), 0, N);
		fourierConst.DFT(N);
		checkClose("DFT(const=3)[0] == 3*N (DC)", fourierConst.getTransformItem(0), new Complex(3.0 * N, 0), tol);
		boolean restZero = true;
		for (int k = 1; k < N; ++k) if (fourierConst.getTransformItem(k).mod() > tol) restZero = false;
		checkTrue("DFT(const=3)[k!=0] == 0", restZero);

		System.out.println("\n=== Laplace: DLT(sigma=0) == Fourier.DFT() para la MISMA senal ===");
		Laplace laplace = new Laplace(z -> refSignal[(int) Math.round(z.rep())], 0, N);
		laplace.DLT(N, 0.0);
		for (int k = 0; k < N; ++k) {
			checkClose("DLT(sigma=0)[" + k + "] == DFT[" + k + "]", laplace.getTransformItem(k), fourier.getTransformItem(k), tol);
		}

		System.out.println("\n=== Laplace: DLT/IDLT round-trip (sigma!=0) ===");
		Laplace laplaceSigma = new Laplace(z -> refSignal[(int) Math.round(z.rep())], 0, N);
		laplaceSigma.DLT(N, 0.3);
		laplaceSigma.IDLT();
		for (int n = 0; n < N; ++n) {
			checkClose("IDLT(DLT(x,sigma=0.3))[" + n + "] == x[" + n + "]", laplaceSigma.getSampleItem(n), refSignal[n], tol);
		}

		System.out.println("\n=== Z: DZT(radius=1) == Fourier.DFT() para la MISMA senal ===");
		Z z = new Z(zz -> refSignal[(int) Math.round(zz.rep())], 0, N);
		z.DZT(N, 1.0);
		for (int k = 0; k < N; ++k) {
			checkClose("DZT(radius=1)[" + k + "] == DFT[" + k + "]", z.getTransformItem(k), fourier.getTransformItem(k), tol);
		}

		System.out.println("\n=== Z: DZT/IDZT round-trip (radius!=1) ===");
		Z zRadius = new Z(zz -> refSignal[(int) Math.round(zz.rep())], 0, N);
		zRadius.DZT(N, 1.2);
		zRadius.IDZT();
		for (int n = 0; n < N; ++n) {
			checkClose("IDZT(DZT(x,radius=1.2))[" + n + "] == x[" + n + "]", zRadius.getSampleItem(n), refSignal[n], tol);
		}

		System.out.println("\n===================================");
		System.out.println("TOTAL: " + pass + " OK, " + fail + " FAIL");
	}
}
