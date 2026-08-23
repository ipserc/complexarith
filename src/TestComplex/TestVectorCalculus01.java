package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.vectorcalculus.VectorCalculus;
import com.ipserc.arith.vectorcalculus.VectorCalculus.ScalarField;
import com.ipserc.arith.vectorcalculus.VectorCalculus.VectorField;

/**
 * Verifica {@link VectorCalculus}: contra fórmulas cerradas conocidas para casos concretos, y
 * contra 2 identidades del cálculo vectorial que valen para CUALQUIER campo suficientemente
 * suave -- {@code curl(grad(f))=0} y {@code div(curl(F))=0} -- una verificación mucho más fuerte
 * que un puñado de casos concretos, porque no depende de haber elegido "bien" el ejemplo.
 */
public class TestVectorCalculus01 {

	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}

	static boolean closeTo(double[] a, double[] b, double tol) {
		for (int i = 0; i < a.length; ++i) {
			if (Math.abs(a[i]-b[i]) > tol) return false;
		}
		return true;
	}

	public static void main(String[] args) {
		int boxMargin = 65;
		int boxShape = 3;

		/*************************************************************
		 * 1) Gradiente: f(x,y,z) = x^2*y + y*z^3
		 * grad(f) = (2xy, x^2+z^3, 3yz^2)
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "VectorCalculus 1/4 -- Gradiente");

		ScalarField f = p -> p[0]*p[0]*p[1] + p[1]*p[2]*p[2]*p[2];
		boolean gradientOk = true;
		double maxGradDiff = 0;
		for (double x = -2.0; x <= 2.0; x += 1.0) {
			for (double y = -2.0; y <= 2.0; y += 1.0) {
				for (double z = -2.0; z <= 2.0; z += 1.0) {
					double[] grad = VectorCalculus.gradient(f, new double[]{x, y, z});
					double[] expected = {2*x*y, x*x + z*z*z, 3*y*z*z};
					double diff = Math.max(Math.abs(grad[0]-expected[0]), Math.max(Math.abs(grad[1]-expected[1]), Math.abs(grad[2]-expected[2])));
					maxGradDiff = Math.max(maxGradDiff, diff);
					if (diff > 1e-4) gradientOk = false;
				}
			}
		}
		System.out.printf("max |grad_calculado - grad_esperado| = %.3e%n", maxGradDiff);
		check("gradient() de x^2*y+y*z^3 coincide con (2xy, x^2+z^3, 3yz^2)", gradientOk);

		/*************************************************************
		 * 2) Divergencia: F(x,y,z) = (x*y, y*z, z*x) -> div(F) = y+z+x
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "VectorCalculus 2/4 -- Divergencia");

		VectorField Fdiv = p -> new double[]{p[0]*p[1], p[1]*p[2], p[2]*p[0]};
		boolean divergenceOk = true;
		double maxDivDiff = 0;
		for (double x = -2.0; x <= 2.0; x += 1.0) {
			for (double y = -2.0; y <= 2.0; y += 1.0) {
				for (double z = -2.0; z <= 2.0; z += 1.0) {
					double div = VectorCalculus.divergence(Fdiv, new double[]{x, y, z});
					double expected = x + y + z;
					double diff = Math.abs(div - expected);
					maxDivDiff = Math.max(maxDivDiff, diff);
					if (diff > 1e-4) divergenceOk = false;
				}
			}
		}
		System.out.printf("max |div_calculada - div_esperada| = %.3e%n", maxDivDiff);
		check("divergence() de (xy,yz,zx) coincide con x+y+z", divergenceOk);

		/*************************************************************
		 * 3) Rotacional: F(x,y,z) = (-y, x, 0) (campo de rotacion pura) -> curl(F) = (0,0,2)
		 *    F(x,y,z) = (x, y, z) (campo radial) -> curl(F) = (0,0,0) (irrotacional)
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "VectorCalculus 3/4 -- Rotacional");

		VectorField rotationField = p -> new double[]{-p[1], p[0], 0};
		VectorField radialField = p -> new double[]{p[0], p[1], p[2]};
		boolean curlOk = true;
		double maxCurlDiff = 0;
		for (double x = -2.0; x <= 2.0; x += 1.0) {
			for (double y = -2.0; y <= 2.0; y += 1.0) {
				for (double z = -2.0; z <= 2.0; z += 1.0) {
					double[] point = {x, y, z};
					double[] curlRotation = VectorCalculus.curl(rotationField, point);
					double[] curlRadial = VectorCalculus.curl(radialField, point);
					double diff = Math.max(
							Math.abs(curlRotation[0]) + Math.abs(curlRotation[1]) + Math.abs(curlRotation[2] - 2),
							Math.abs(curlRadial[0]) + Math.abs(curlRadial[1]) + Math.abs(curlRadial[2]));
					maxCurlDiff = Math.max(maxCurlDiff, diff);
					if (diff > 1e-4) curlOk = false;
				}
			}
		}
		System.out.printf("max desviacion (rotacion->(0,0,2), radial->(0,0,0)) = %.3e%n", maxCurlDiff);
		check("curl() del campo de rotacion pura da (0,0,2) y del campo radial da (0,0,0)", curlOk);

		boolean rejectsNon3D = true;
		try { VectorCalculus.curl(p -> p, new double[]{1, 2}); rejectsNon3D = false; } catch (IllegalArgumentException e) { }
		check("curl() rechaza puntos que no son de dimension 3", rejectsNon3D);

		/*************************************************************
		 * 4) Identidades del calculo vectorial (validas para CUALQUIER campo suave):
		 *    curl(grad(f)) = 0 ; div(curl(F)) = 0
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "VectorCalculus 4/4 -- Identidades curl(grad)=0, div(curl)=0");

		ScalarField g = p -> Math.sin(p[0]*p[1]) + p[2]*p[2]*Math.cos(p[0]) + p[1]*p[1]*p[1];
		VectorField gradG = p -> VectorCalculus.gradient(g, p);

		boolean curlOfGradIsZero = true;
		double maxCurlGrad = 0;
		for (double x = -1.5; x <= 1.5; x += 0.75) {
			for (double y = -1.5; y <= 1.5; y += 0.75) {
				for (double z = -1.5; z <= 1.5; z += 0.75) {
					double[] curlGrad = VectorCalculus.curl(gradG, new double[]{x, y, z});
					double mod = Math.abs(curlGrad[0]) + Math.abs(curlGrad[1]) + Math.abs(curlGrad[2]);
					maxCurlGrad = Math.max(maxCurlGrad, mod);
					if (mod > 1e-2) curlOfGradIsZero = false;
				}
			}
		}
		System.out.printf("max |curl(grad(g))| = %.3e%n", maxCurlGrad);
		check("curl(grad(g))=0 para un campo escalar arbitrario g", curlOfGradIsZero);

		VectorField H = p -> new double[]{
				p[1]*p[2] + Math.sin(p[0]),
				p[0]*p[2]*p[2] - p[1],
				Math.cos(p[0]*p[1]) + p[2]
		};
		VectorField curlH = p -> VectorCalculus.curl(H, p);

		boolean divOfCurlIsZero = true;
		double maxDivCurl = 0;
		for (double x = -1.5; x <= 1.5; x += 0.75) {
			for (double y = -1.5; y <= 1.5; y += 0.75) {
				for (double z = -1.5; z <= 1.5; z += 0.75) {
					double div = VectorCalculus.divergence(curlH, new double[]{x, y, z});
					maxDivCurl = Math.max(maxDivCurl, Math.abs(div));
					if (Math.abs(div) > 1e-2) divOfCurlIsZero = false;
				}
			}
		}
		System.out.printf("max |div(curl(H))| = %.3e%n", maxDivCurl);
		check("div(curl(H))=0 para un campo vectorial arbitrario H", divOfCurlIsZero);

		/*************************************************************
		 * 5) Laplaciano ("operador Newtoniano"): f(x,y,z)=x^2+y^2+z^2 -> laplacian=6
		 *    y el potencial newtoniano f(x,y,z)=1/r (r=|x|) -> laplacian=0 para r!=0 (armonico)
		 *************************************************************/
		Complex.printBoxText(boxShape, boxMargin, "VectorCalculus 5/5 -- Laplaciano (operador Newtoniano)");

		ScalarField quadratic = p -> p[0]*p[0] + p[1]*p[1] + p[2]*p[2];
		boolean laplacianQuadraticOk = true;
		double maxLapQuadDiff = 0;
		for (double x = -2.0; x <= 2.0; x += 1.0) {
			for (double y = -2.0; y <= 2.0; y += 1.0) {
				for (double z = -2.0; z <= 2.0; z += 1.0) {
					double lap = VectorCalculus.laplacian(quadratic, new double[]{x, y, z});
					double diff = Math.abs(lap - 6.0);
					maxLapQuadDiff = Math.max(maxLapQuadDiff, diff);
					if (diff > 1e-3) laplacianQuadraticOk = false;
				}
			}
		}
		System.out.printf("max |laplacian(x^2+y^2+z^2) - 6| = %.3e%n", maxLapQuadDiff);
		check("laplacian() de x^2+y^2+z^2 coincide con 6", laplacianQuadraticOk);

		ScalarField newtonianPotential = p -> 1.0 / Math.sqrt(p[0]*p[0] + p[1]*p[1] + p[2]*p[2]);
		boolean newtonianPotentialHarmonic = true;
		double maxLapPotential = 0;
		for (double x = -3.0; x <= 3.0; x += 0.6) {
			for (double y = -3.0; y <= 3.0; y += 0.6) {
				for (double z = -3.0; z <= 3.0; z += 0.6) {
					double r = Math.sqrt(x*x + y*y + z*z);
					if (r < 0.5) continue; // fuera del entorno de la fuente (singular en r=0)
					double lap = VectorCalculus.laplacian(newtonianPotential, new double[]{x, y, z});
					maxLapPotential = Math.max(maxLapPotential, Math.abs(lap));
					if (Math.abs(lap) > 1e-2) newtonianPotentialHarmonic = false;
				}
			}
		}
		System.out.printf("max |laplacian(1/r)| para r>=0.5 (deberia ser 0, armonico) = %.3e%n", maxLapPotential);
		check("el potencial newtoniano 1/r es armonico (laplacian=0) fuera del origen", newtonianPotentialHarmonic);

		Complex.printBoxText(boxShape, boxMargin, ok + " tests passed out of " + (ok + fail) + " taken. " + fail + " tests failed.");
		if (fail > 0) { System.exit(1); }
	}
}
