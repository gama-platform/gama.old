/*********************************************************************************************
 * 
 *
 * 'Solver.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import ummisco.gaml.extensions.maths.ode.statements.SystemOfEquationsStatement;
import msi.gama.runtime.IScope;

public abstract class Solver {

	// Declare the integrator using facets values (name, parameters)
	// Declare a step handler also using facets values
	// Should the solver implement StepHandler or use a given StepHandler ?

	// Call the integrator, which should call computeDerivatives on the system
	// of equations;
	public abstract void solve(IScope scope, SystemOfEquationsStatement eq,
			double time_initial, double time_final, double cycle_length);

}
