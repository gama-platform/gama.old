/*********************************************************************************************
 *
 *
 * 'Rk4Solver.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import msi.gama.util.GamaMap;
import msi.gama.util.IList;

public class Rk4Solver extends Solver {

	public Rk4Solver(final double step, final GamaMap<String, IList<Double>> integrated_val) {
		super(step, new ClassicalRungeKuttaIntegrator(step), integrated_val);
	}

}
