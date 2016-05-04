/*********************************************************************************************
 *
 *
 * 'EulerSolver.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import java.util.List;

import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

import msi.gama.util.GamaMap;
import msi.gama.util.IList;

public class EulerSolver extends Solver {

	public EulerSolver(final double step, final GamaMap<String, IList<Double>> integrated_val) {
		super(step, new EulerIntegrator(step), integrated_val);
	}

}
