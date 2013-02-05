package ummisco.gaml.extensions.maths.utils;

import ummisco.gaml.extensions.maths.statements.SystemOfEquationsStatement;
import msi.gama.runtime.IScope;

public abstract class Solver {

	// Declare the integrator using facets values (name, parameters)
	// Declare a step handler also using facets values
	// Should the solver implement StepHandler or use a given StepHandler ?

	// Call the integrator, which should call computeDerivatives on the system of equations;
	public abstract void solve(IScope scope, SystemOfEquationsStatement eq, double time_initial, double time_final, double cycle_length);


}
