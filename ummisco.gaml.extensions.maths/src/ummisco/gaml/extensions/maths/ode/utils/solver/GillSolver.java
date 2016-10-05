/*********************************************************************************************
 *
 *
 * 'GillSolver.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import org.apache.commons.math3.ode.nonstiff.GillIntegrator;

import msi.gama.util.GamaMap;
import msi.gama.util.IList;

public class GillSolver extends Solver {

	public GillSolver(final double step, final GamaMap<String, IList<Double>> integrated_val) {
		super(step, new GillIntegrator(step), integrated_val);
	}

}
