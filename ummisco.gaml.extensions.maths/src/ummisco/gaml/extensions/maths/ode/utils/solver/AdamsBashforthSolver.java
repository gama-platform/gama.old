/*******************************************************************************************************
 *
 * AdamsBashforthSolver.java, in ummisco.gaml.extensions.maths, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import org.apache.commons.math3.ode.nonstiff.AdamsBashforthIntegrator;

import msi.gama.util.IList;
import msi.gama.util.IMap;

/**
 * The Class AdamsBashforthSolver.
 */
public class AdamsBashforthSolver extends Solver {

	/**
	 * Instantiates a new adams bashforth solver.
	 *
	 * @param nSteps the n steps
	 * @param minStep the min step
	 * @param maxStep the max step
	 * @param scalAbsoluteTolerance the scal absolute tolerance
	 * @param scalRelativeTolerance the scal relative tolerance
	 * @param integrated_val the integrated val
	 */
	public AdamsBashforthSolver(final int nSteps, final double minStep, final double maxStep,
			final double scalAbsoluteTolerance, final double scalRelativeTolerance,
			final IMap<String, IList<Double>> integrated_val) {
		super((minStep + maxStep) / 2,
				new AdamsBashforthIntegrator(nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance),
				integrated_val);
	}

}