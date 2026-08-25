package com.ipserc.arith.rf;

import com.ipserc.arith.complex.Complex;

/**
 * The two quantities a single propagating waveguide mode needs to act as a uniform transmission
 * line in a cascade -- its propagation constant and its (TE) wave impedance -- abstracted so that
 * {@link EvanescentModeFilter}'s barrier-cavity-barrier cascade works over ANY mode implementing
 * this (today {@link RectangularWaveguide}'s TE10 and {@link CircularWaveguide}'s TE11) without
 * duplicating the ABCD-matrix cascade logic per geometry: the physics that differs between
 * geometries (how {@code kc} comes out of the boundary condition) lives entirely in the
 * implementing class, not in the filter.
 */
public interface WaveguideMode {

	/**
	 * The (possibly complex) propagation constant at {@code frequency} -- real above cutoff
	 * (propagating), purely imaginary below it (evanescent).
	 * @param frequency The operating frequency, Hz. Must be positive.
	 * @return The propagation constant.
	 */
	Complex propagationConstant(double frequency);

	/**
	 * The (possibly complex) TE wave impedance at {@code frequency} -- real above cutoff, purely
	 * reactive below it.
	 * @param frequency The operating frequency, Hz. Must be positive.
	 * @return The wave impedance.
	 */
	Complex waveImpedanceTE(double frequency);
}
