package com.ipserc.arith.rf;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

/**
 * A waveguide bandpass filter built directly from the TE10 physics of {@link
 * RectangularWaveguide} (Maxwell's equations), not from filter-synthesis tables: a short
 * resonant section of the normal (propagating) guide {@code passGuide}, sandwiched between two
 * identical barrier sections of a narrower guide {@code barrierGuide} operated BELOW its own
 * cutoff (evanescent -- see {@link RectangularWaveguide#propagationConstant}). Each barrier
 * reflects most of the incident power back (like a partially-silvered mirror), so the structure
 * behaves like a Fabry-Perot resonator: transmission is high only near the frequencies where the
 * cavity length matches a resonance condition, low elsewhere -- a genuine bandpass response
 * emerging purely from wave physics, not from an equivalent-circuit approximation.
 * <p>
 * Each uniform section (barrier or cavity) is a lossless transmission-line-equivalent 2-port,
 * {@code ABCD = [[cosh(g*L), Z0*sinh(g*L)], [sinh(g*L)/Z0, cosh(g*L)]]}, {@code g=j*beta(f)},
 * {@code Z0} the section's own TE10 wave impedance -- unimodular ({@code A*D-B*C=1}) by the
 * identity {@code cosh^2-sinh^2=1} regardless of the sign convention chosen for the complex square
 * root in {@code beta} below cutoff, so the cascade is reciprocal and, since every section here is
 * lossless, the overall 2-port is lossless too ({@code |S11|^2+|S21|^2=1}, verified in
 * {@code TestRF_EvanescentFilter01} as the primary sanity check).
 */
public final class EvanescentModeFilter {

	private final static String HEADINFO = "EvanescentModeFilter --- INFO: ";
	private final static String VERSION = "1.0 (2026_0824_1500)";
	/* VERSION Release Note
	 *
	 * 1.0 (2026_0824_1500)
	 * Segunda clase de com.ipserc.arith.rf: filtro paso banda evanescente (barrera-cavidad-
	 * barrera) sobre RectangularWaveguide.VERSION 1.0, matrices ABCD en cascada.
	 */

	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION);
	}

	private final RectangularWaveguide passGuide;
	private final RectangularWaveguide barrierGuide;
	private final double barrierLength;
	private final double cavityLength;

	/**
	 * @param passGuide The normal (wider) guide, used for the resonant cavity and as the
	 * input/output reference.
	 * @param barrierGuide The narrower guide used for the two barrier sections -- must have a
	 * higher TE10 cutoff frequency than {@code passGuide} for it to be evanescent in the filter's
	 * passband.
	 * @param barrierLength The length of each of the two barrier sections. Must be positive.
	 * @param cavityLength The length of the resonant cavity section (of {@code passGuide}).
	 * Must be positive.
	 * @throws IllegalArgumentException if {@code barrierLength}/{@code cavityLength} are not
	 * positive.
	 */
	public EvanescentModeFilter(RectangularWaveguide passGuide, RectangularWaveguide barrierGuide, double barrierLength, double cavityLength) {
		if (barrierLength <= 0 || cavityLength <= 0) {
			throw new IllegalArgumentException(HEADINFO + "barrierLength and cavityLength must be positive");
		}
		this.passGuide = passGuide;
		this.barrierGuide = barrierGuide;
		this.barrierLength = barrierLength;
		this.cavityLength = cavityLength;
	}

	/** The ABCD matrix of a single uniform section (see class Javadoc). */
	private static MatrixComplex sectionABCD(RectangularWaveguide guide, double length, double frequency) {
		Complex beta = guide.propagationConstant(frequency);
		Complex z0 = guide.waveImpedanceTE(frequency);
		Complex gammaL = Complex.i.times(beta).times(length);
		Complex coshValue = Complex.cosh(gammaL);
		Complex sinhValue = Complex.sinh(gammaL);
		MatrixComplex abcd = new MatrixComplex(2, 2);
		abcd.setItem(0, 0, coshValue);
		abcd.setItem(0, 1, z0.times(sinhValue));
		abcd.setItem(1, 0, sinhValue.divides(z0));
		abcd.setItem(1, 1, coshValue);
		return abcd;
	}

	/**
	 * The overall {@code ABCD} matrix of the barrier-cavity-barrier cascade at {@code frequency}.
	 * @param frequency The operating frequency, Hz. Must be positive.
	 * @return The {@code 2x2} cascaded ABCD matrix.
	 */
	public MatrixComplex abcd(double frequency) {
		MatrixComplex barrier = sectionABCD(barrierGuide, barrierLength, frequency);
		MatrixComplex cavity = sectionABCD(passGuide, cavityLength, frequency);
		return barrier.times(cavity).times(barrier);
	}

	/**
	 * The scattering parameters {@code {S11, S21}} at {@code frequency}, referenced to
	 * {@code passGuide}'s own TE10 wave impedance at that frequency (the natural reference: the
	 * filter is meant to be inserted into a {@code passGuide}-type feedline on both sides).
	 * @param frequency The operating frequency, Hz. Must be positive.
	 * @return {@code {S11, S21}}.
	 */
	public Complex[] sParameters(double frequency) {
		MatrixComplex m = abcd(frequency);
		Complex A = m.getItem(0, 0), B = m.getItem(0, 1), C = m.getItem(1, 0), D = m.getItem(1, 1);
		Complex z0 = passGuide.waveImpedanceTE(frequency);
		Complex bOverZ0 = B.divides(z0);
		Complex cTimesZ0 = C.times(z0);
		Complex denom = A.plus(bOverZ0).plus(cTimesZ0).plus(D);
		Complex s11 = A.plus(bOverZ0).minus(cTimesZ0).minus(D).divides(denom);
		Complex s21 = new Complex(2, 0).divides(denom);
		return new Complex[] {s11, s21};
	}
}
