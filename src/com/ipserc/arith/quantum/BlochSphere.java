package com.ipserc.arith.quantum;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.plot.PlotStyle;
import com.ipserc.arith.plot.SimpleGnuplot;

/**
 * The Bloch-sphere representation of a single-qubit state: {@code rho = (I + x*X + y*Y + z*Z)/2}
 * -- a bijection between {@code 2x2} density matrices and points {@code (x,y,z)} of the unit ball
 * (the sphere's surface {@code x^2+y^2+z^2=1} is exactly the pure states, its interior the mixed
 * ones, its center {@code (0,0,0)} the maximally mixed state {@code I/2}), plus a 3D plot of a
 * trajectory of such points -- the standard way to SEE a qubit's state, and especially how {@link
 * Decoherence} shrinks it from the surface toward the center.
 * <p>
 * Continuation of the "Rol Física/Mecánica Cuántica" (see {@code Claude/ComplexArithRev.md},
 * Trigesimoctava sesión) -- the "visualización (esfera de Bloch)" candidate catalogued at the
 * close of the Trigesimosexta sesión, built on {@link Qubits} (Pauli operators), {@link
 * DensityMatrix} (pure-state-to-{@code rho} convenience), {@link Decoherence} (the natural source
 * of an interesting trajectory), and the plotting infrastructure from the Trigesimoquinta sesión
 * ({@code SimpleGnuplot}'s low-level {@code addPlot}/{@code addPlotGrid} mixing, needed here to
 * overlay a wireframe surface and a point trajectory in the same {@code splot} -- the higher {@code
 * MatrixComplexPlot} convenience layer only exposes one shape family per call).
 */
public final class BlochSphere {

	private final static String VERSION = "1.0 (2026_0813_2359)";

	private BlochSphere() {}

	/**
	 * The Bloch vector {@code (x,y,z) = (Tr(rho*X), Tr(rho*Y), Tr(rho*Z))} of a single-qubit
	 * density matrix.
	 * @param rho The {@code 2x2} density matrix (e.g. from {@link DensityMatrix#of(MatrixComplex)}
	 * or {@link Decoherence#apply(MatrixComplex, MatrixComplex[], int, int)}).
	 * @return {@code {x, y, z}}, each in {@code [-1,1]}, with {@code x^2+y^2+z^2<=1} ({@code ==1}
	 * for a pure state, {@code <1} for a mixed one).
	 * @throws IllegalArgumentException if {@code rho} isn't {@code 2x2}.
	 * @throws IllegalStateException if a component comes out with a non-negligible imaginary part
	 * -- would mean {@code rho} isn't actually Hermitian (same fail-loud pattern as {@link
	 * BellTest#correlation}/{@link TimeEvolution#expectationValue}).
	 */
	public static double[] vector(MatrixComplex rho) {
		if (rho.rows() != 2 || rho.cols() != 2) {
			throw new IllegalArgumentException("BlochSphere only represents single-qubit (2x2) density "
					+ "matrices, got " + rho.rows() + "x" + rho.cols());
		}
		double x = component(rho, Qubits.pauliX());
		double y = component(rho, Qubits.pauliY());
		double z = component(rho, Qubits.pauliZ());
		return new double[] { x, y, z };
	}

	private static double component(MatrixComplex rho, MatrixComplex pauli) {
		Complex trace = rho.times(pauli).trace();
		if (Math.abs(trace.imp()) > 1e-9) {
			throw new IllegalStateException("Tr(rho*pauli) came out complex (Im=" + trace.imp()
					+ ") -- expected a real value; check that rho is Hermitian");
		}
		return trace.rep();
	}

	/**
	 * Convenience form of {@link #vector(MatrixComplex)} for a pure ket instead of an already-built
	 * density matrix -- {@code vector(DensityMatrix.of(psi))}.
	 * @param psi A normalized single-qubit ket (e.g. {@link Qubits#ket0()}).
	 * @return The Bloch vector, always on the unit sphere's surface for a pure state.
	 */
	public static double[] vectorOfState(MatrixComplex psi) {
		return vector(DensityMatrix.of(psi));
	}

	/**
	 * The inverse of {@link #vector(MatrixComplex)}: reconstructs the {@code 2x2} density matrix
	 * {@code rho=(I+x*X+y*Y+z*Z)/2} for a given Bloch vector.
	 * @param x X component.
	 * @param y Y component.
	 * @param z Z component.
	 * @return The {@code 2x2} density matrix.
	 * @throws IllegalArgumentException if {@code x^2+y^2+z^2>1} -- outside the unit ball, not a
	 * physical single-qubit state (would give {@code rho} a negative eigenvalue).
	 */
	public static MatrixComplex fromVector(double x, double y, double z) {
		double normSq = x * x + y * y + z * z;
		if (normSq > 1.0 + 1e-9) {
			throw new IllegalArgumentException("Bloch vector (" + x + "," + y + "," + z + ") has norm "
					+ Math.sqrt(normSq) + " > 1 -- not a physical single-qubit state");
		}
		return Qubits.identity2()
				.plus(Qubits.pauliX().times(x))
				.plus(Qubits.pauliY().times(y))
				.plus(Qubits.pauliZ().times(z))
				.times(0.5);
	}

	/**
	 * Plots a trajectory of Bloch vectors together with a wireframe unit sphere for reference --
	 * e.g. the successive states of a qubit under repeated {@link Decoherence#apply(MatrixComplex,
	 * MatrixComplex[], int, int)} calls (a curve spiraling from the surface toward the center) or
	 * under {@link TimeEvolution#evolve(MatrixComplex, MatrixComplex, double)} (a curve staying
	 * exactly on the surface, unitary evolution never mixes a pure state). Equal-scaled axes ({@code
	 * set view equal xyz}) so the sphere actually looks round.
	 * @param title The plot window title.
	 * @param blochVectors The trajectory, one {@code {x,y,z}} row per point, in order (e.g. built by
	 * repeatedly calling {@link #vector(MatrixComplex)} on a sequence of density matrices).
	 * @param mode {@link SimpleGnuplot.e_syncMode#SYNC} to block until the window is closed, {@link
	 * SimpleGnuplot.e_syncMode#ASYNC} to return immediately.
	 */
	public static void plotTrajectory(String title, double[][] blochVectors, SimpleGnuplot.e_syncMode mode) {
		SimpleGnuplot p = new SimpleGnuplot();
		p.setTitle(title);
		p.newGraph3D();
		p.set("view", "equal xyz");
		p.set("xrange", "[-1.2:1.2]");
		p.set("yrange", "[-1.2:1.2]");
		p.set("zrange", "[-1.2:1.2]");
		p.set("xlabel", "'X'");
		p.set("ylabel", "'Y'");
		p.set("zlabel", "'Z'");
		p.addPlotGrid(sphereWireframe(24, 24), "Bloch sphere", new PlotStyle("grey", null, null, null, "lines"));
		p.addPlot(blochVectors, "trajectory", new PlotStyle("red", 2, null, 7, "linespoints"));
		p.plot(mode);
	}

	/**
	 * A {@code (thetaSteps+1) x (phiSteps+1)} wireframe grid of the unit sphere, spherical
	 * coordinates {@code x=sin(theta)cos(phi), y=sin(theta)sin(phi), z=cos(theta)}, {@code
	 * theta} in {@code [0,pi]}, {@code phi} in {@code [0,2*pi]} -- the reference surface {@link
	 * #plotTrajectory(String, double[][], SimpleGnuplot.e_syncMode)} draws every Bloch vector
	 * against.
	 */
	private static double[][][] sphereWireframe(int thetaSteps, int phiSteps) {
		double[][][] grid = new double[thetaSteps + 1][phiSteps + 1][3];
		for (int i = 0; i <= thetaSteps; ++i) {
			double theta = Math.PI * i / thetaSteps;
			for (int j = 0; j <= phiSteps; ++j) {
				double phi = 2.0 * Math.PI * j / phiSteps;
				grid[i][j][0] = Math.sin(theta) * Math.cos(phi);
				grid[i][j][1] = Math.sin(theta) * Math.sin(phi);
				grid[i][j][2] = Math.cos(theta);
			}
		}
		return grid;
	}
}
