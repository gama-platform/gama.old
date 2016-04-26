/*********************************************************************************************
 *
 *
 * 'MidpointSolver.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import java.util.List;

import org.apache.commons.math3.ode.nonstiff.MidpointIntegrator;

public class MidpointSolver extends Solver {

	public MidpointSolver(final double step, final int discretizing_step, final List<Double> integrated_time,
			final List<List<Double>> integrated_val) {
		super(step, new MidpointIntegrator(step), discretizing_step, integrated_time, integrated_val);
	}

}
