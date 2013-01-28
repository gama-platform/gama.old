package ummisco.gaml.ext.maths.utils;

import java.util.List;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import ummisco.gaml.ext.maths.statements.SingleEquationStatement;
import ummisco.gaml.ext.maths.statements.SystemOfEquationsStatement;
import ummisco.gaml.ext.maths.utils.Solver;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;

public class Rk4Solver extends Solver {

	StatementDescription equations;
	FirstOrderIntegrator integrator;
	
	public Rk4Solver(StatementDescription eq) {
		// initialize the integrator, the step handler, etc.
		// This class has access to the facets of the statement
		// ie. getFacet(...)
		//

		// Just a trial
		equations=eq;
		integrator = new ClassicalRungeKuttaIntegrator(1.0);
		StepHandler stepHandler = new StepHandler() {
			public void init(double t0, double[] y0, double t) {
			}

			@Override
			public void handleStep(StepInterpolator interpolator, boolean isLast) {
				double t = interpolator.getCurrentTime();
				double[] y = interpolator.getInterpolatedState();
				GuiUtils.informConsole(t + " " + y[0] + " " + y[1]);
			}
		};
		integrator.addStepHandler(stepHandler);

	}

	@Override
	public void solve(final IScope scope) {
		// call the integrator.
		// We need to save the state (previous time the integrator has been
		// solved, etc.)

		List<IDescription> singleEquations= equations.getChildren();
		
		for ( IDescription s : singleEquations ) {

			GuiUtils.informConsole("it work"+s);
			if ( s instanceof SingleEquationStatement) {

				((SingleEquationStatement) s).executeOn(scope);
			}
		}
//		FirstOrderDifferentialEquations ode = new CircleODE(new double[] { 1.0, 1.0 }, 0.1);
//		double[] y = new double[] { 0.0, 1.0 }; // initial state
//		dp853.integrate(ode, 0.0, y, 16.0, y);
		
	}

}