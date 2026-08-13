package TestComplex;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.plot.SimpleGnuplot;
import com.ipserc.arith.quantum.BlochSphere;
import com.ipserc.arith.quantum.Decoherence;
import com.ipserc.arith.quantum.DensityMatrix;
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.quantum.TimeEvolution;

/**
 * Audit of BlochSphere.java (vector()/fromVector()/plotTrajectory()) -- candidate "visualizacion
 * (esfera de Bloch)" of the Rol Fisica/Mecanica Cuantica roadmap catalogued at the close of the
 * Trigesimosexta sesion. Uses the established reflection technique (redirect
 * SimpleGnuplot.cachedExe to a decoy .bat that captures the script) to verify the plot's generated
 * script by text, without opening any window.
 */
public class ScratchBlochSphereAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) throws Exception {
		// 1. Textbook Bloch vectors for the computational/X/Y eigenbases
		double[] v0 = BlochSphere.vectorOfState(Qubits.ket0());
		double[] v1 = BlochSphere.vectorOfState(Qubits.ket1());
		MatrixComplex plusX = Qubits.ket0().plus(Qubits.ket1()).normalizeByCols();
		MatrixComplex plusY = Qubits.ket0().plus(Qubits.ket1().times(new Complex(0.0, 1.0))).normalizeByCols();
		double[] vPlusX = BlochSphere.vectorOfState(plusX);
        double[] vPlusY = BlochSphere.vectorOfState(plusY);
		check("|0> -> (0,0,1)", closeTo(v0, 0, 0, 1));
		check("|1> -> (0,0,-1)", closeTo(v1, 0, 0, -1));
		check("|+> -> (1,0,0)", closeTo(vPlusX, 1, 0, 0));
		check("|+i> -> (0,1,0)", closeTo(vPlusY, 0, 1, 0));

		// 2. Every pure-state Bloch vector is exactly on the unit sphere's surface
		boolean onSurfaceOk = true;
		Random rng = new Random(20260813L);
		for (int i = 0; i < 20; ++i) {
			double theta = rng.nextDouble() * Math.PI;
			double phiRel = rng.nextDouble() * 2 * Math.PI;
			MatrixComplex psi = new MatrixComplex(2, 1);
			psi.setItem(0, 0, Math.cos(theta / 2));
			psi.setItem(1, 0, new Complex(Math.sin(theta / 2) * Math.cos(phiRel), Math.sin(theta / 2) * Math.sin(phiRel)));
			double[] v = BlochSphere.vectorOfState(psi);
			double norm = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
			if (Math.abs(norm - 1.0) > 1e-9) { onSurfaceOk = false; }
		}
		check("every pure-state Bloch vector has norm exactly 1 (20 random states)", onSurfaceOk);

		// 3. fromVector(0,0,0) is exactly I/2 (maximally mixed, sphere's center)
		MatrixComplex maxMixed = BlochSphere.fromVector(0, 0, 0);
		MatrixComplex halfIdentity = Qubits.identity2().times(0.5);
		check("fromVector(0,0,0)==I/2", maxMixed.minus(halfIdentity).norm() < 1e-12);

		// 4. Round trip vector(fromVector(x,y,z))==(x,y,z), several valid vectors
		double[][] testVectors = { {0, 0, 1}, {0, 0, -1}, {1, 0, 0}, {0, 1, 0}, {0.3, -0.4, 0.5}, {0, 0, 0} };
		boolean roundTripOk = true;
		for (double[] tv : testVectors) {
			MatrixComplex rho = BlochSphere.fromVector(tv[0], tv[1], tv[2]);
			double[] back = BlochSphere.vector(rho);
			if (!closeTo(back, tv[0], tv[1], tv[2])) { roundTripOk = false; }
		}
		check("vector(fromVector(x,y,z)) round-trips exactly, 6 vectors", roundTripOk);

		// 5. fromVector() rejects a vector outside the unit ball
		try {
			BlochSphere.fromVector(1.0, 1.0, 1.0);
			check("fromVector() rejects norm>1", false);
		} catch (IllegalArgumentException e) {
			check("fromVector() rejects norm>1", true);
		}

		// 6. Bridge with Decoherence: depolarizing(p) shrinks the Bloch vector by exactly (1-p),
		//    for several p and several starting states -- the textbook "depolarizing channel
		//    contracts the Bloch ball toward the center" picture, confirmed numerically not assumed.
		boolean depolarizeShrinkOk = true;
		MatrixComplex[] startStates = { Qubits.ket0(), Qubits.ket1(), plusX, plusY };
		double[] probs = { 0.0, 0.25, 0.5, 0.75, 1.0 };
		for (MatrixComplex psi : startStates) {
			double[] original = BlochSphere.vectorOfState(psi);
			for (double p : probs) {
				MatrixComplex rho = DensityMatrix.of(psi);
				MatrixComplex depolarized = Decoherence.apply(rho, Decoherence.depolarizing(p), 0, 1);
				double[] shrunk = BlochSphere.vector(depolarized);
				if (!closeTo(shrunk, original[0] * (1 - p), original[1] * (1 - p), original[2] * (1 - p))) {
					depolarizeShrinkOk = false;
				}
			}
		}
		check("depolarizing(p) shrinks the Bloch vector by exactly (1-p), 4 states x 5 p", depolarizeShrinkOk);

		// 7. Bridge with Decoherence: amplitudeDamping(1.0) applied once to |1> (z=-1) lands exactly
		//    at |0> (z=+1); at gamma=0 stays exactly at z=-1; every intermediate gamma stays INSIDE
		//    the unit ball (norm<=1), never outside.
		double[] afterFullDamp = BlochSphere.vector(Decoherence.apply(DensityMatrix.of(Qubits.ket1()), Decoherence.amplitudeDamping(1.0), 0, 1));
		double[] afterNoDamp = BlochSphere.vector(Decoherence.apply(DensityMatrix.of(Qubits.ket1()), Decoherence.amplitudeDamping(0.0), 0, 1));
		boolean ampDampBlochOk = closeTo(afterFullDamp, 0, 0, 1) && closeTo(afterNoDamp, 0, 0, -1);
		for (double gamma = 0.0; gamma <= 1.0; gamma += 0.1) {
			double[] v = BlochSphere.vector(Decoherence.apply(DensityMatrix.of(Qubits.ket1()), Decoherence.amplitudeDamping(gamma), 0, 1));
			double norm = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
			if (norm > 1.0 + 1e-9) { ampDampBlochOk = false; }
		}
		check("amplitudeDamping trajectory of |1>: exact endpoints, stays inside the unit ball throughout",
				ampDampBlochOk);

		// 8. Bridge with TimeEvolution: unitary evolution NEVER mixes a pure state -- the Bloch
		//    vector stays exactly on the surface (norm==1) at every t, unlike Decoherence.
		boolean unitaryStaysOnSurfaceOk = true;
		MatrixComplex hamiltonian = Qubits.pauliX();
		for (double t = 0.0; t <= 2 * Math.PI; t += 0.3) {
			MatrixComplex evolved = TimeEvolution.evolve(plusY, hamiltonian, t);
			double[] v = BlochSphere.vector(DensityMatrix.of(evolved));
			double norm = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
			if (Math.abs(norm - 1.0) > 1e-9) { unitaryStaysOnSurfaceOk = false; }
		}
		check("unitary time evolution keeps the Bloch vector exactly on the sphere's surface", unitaryStaysOnSurfaceOk);

		// 9. plotTrajectory() script verification (no window opened): redirect cachedExe to a decoy
		//    .bat that captures stdin, confirm the sphere wireframe + trajectory + equal-axes setting
		//    all appear in the generated script.
		File captured = File.createTempFile("blochsphere_script", ".txt");
		captured.deleteOnExit();
		File decoyBat = File.createTempFile("findstr_decoy", ".bat");
		decoyBat.deleteOnExit();
		try (PrintWriter w = new PrintWriter(decoyBat)) {
			w.println("@echo off");
			w.println("C:\\Windows\\System32\\findstr.exe \"^\" > \"" + captured.getAbsolutePath() + "\"");
		}
		Field cachedExeField = SimpleGnuplot.class.getDeclaredField("cachedExe");
		cachedExeField.setAccessible(true);
		cachedExeField.set(null, decoyBat.getAbsolutePath());

		double[][] trajectory = { {0, 0, 1}, {0.3, 0, 0.9}, {0.5, 0, 0.7}, {0.6, 0, 0.5} };
		BlochSphere.plotTrajectory("Bloch sphere audit", trajectory, SimpleGnuplot.e_syncMode.SYNC);
		String script = new String(Files.readAllBytes(captured.toPath()));
		boolean scriptOk = script.contains("splot")
				&& script.contains("Bloch sphere")
				&& script.contains("trajectory")
				&& script.contains("view equal xyz")
				&& script.contains("linespoints");
		check("plotTrajectory() script has splot, sphere+trajectory labels, equal-axes view, linespoints style", scriptOk);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static boolean closeTo(double[] v, double x, double y, double z) {
		return Math.abs(v[0] - x) < 1e-9 && Math.abs(v[1] - y) < 1e-9 && Math.abs(v[2] - z) < 1e-9;
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
