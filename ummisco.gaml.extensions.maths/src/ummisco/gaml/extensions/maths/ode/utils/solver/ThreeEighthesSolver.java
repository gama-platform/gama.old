/*******************************************************************************************************
 *
 * ThreeEighthesSolver.java, in ummisco.gaml.extensions.maths, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import org.apache.commons.math3.ode.nonstiff.ThreeEighthesIntegrator;

import msi.gama.util.IMap;
import msi.gama.util.IList;

/**
 * The Class ThreeEighthesSolver.
 */
public class ThreeEighthesSolver extends Solver {

	/**
	 * Instantiates a new three eighthes solver.
	 *
	 * @param step the step
	 * @param integrated_val the integrated val
	 */
	public ThreeEighthesSolver(final double step, final IMap<String, IList<Double>> integrated_val) {
		super(step, new ThreeEighthesIntegrator(step), integrated_val);
	}

}
