/*********************************************************************************************
 *
 *
 * 'AdamsBashforthSolver.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import org.apache.commons.math3.ode.nonstiff.AdamsBashforthIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;

import msi.gama.util.GamaMap;
import msi.gama.util.IList;

public class AdamsBashforthSolver extends Solver {

	public StepHandler stepHandler;

	public AdamsBashforthSolver(final int nSteps, final double minStep, final double maxStep,
			final double scalAbsoluteTolerance, final double scalRelativeTolerance,
			final GamaMap<String, IList<Double>> integrated_val) {
		super((minStep + maxStep) / 2,
				new AdamsBashforthIntegrator(nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance),
				integrated_val);
	}

}