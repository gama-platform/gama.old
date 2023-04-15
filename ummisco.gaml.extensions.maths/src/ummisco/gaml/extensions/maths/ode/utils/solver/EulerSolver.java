/*******************************************************************************************************
 *
 * EulerSolver.java, in ummisco.gaml.extensions.maths, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

import msi.gama.util.IList;
import msi.gama.util.IMap;

/**
 * The Class EulerSolver.
 */
public class EulerSolver extends Solver {

	/**
	 * Instantiates a new euler solver.
	 *
	 * @param step the step
	 * @param integrated_val the integrated val
	 */
	public EulerSolver(final double step, final IMap<String, IList<Double>> integrated_val) {
		super(step, new EulerIntegrator(step), integrated_val);
	}

}
