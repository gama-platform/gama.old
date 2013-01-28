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
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;

public class Rk4Solver extends Solver {

	FirstOrderIntegrator integrator;

	public Rk4Solver() {
		// initialize the integrator, the step handler, etc.
		// This class has access to the facets of the statement
		// ie. getFacet(...)
		//

		// Just a trial
		integrator = new ClassicalRungeKuttaIntegrator(
				SimulationClock.getCycle()+1);
		StepHandler stepHandler = new StepHandler() {
			public void init(double t0, double[] y0, double t) {
			}

			@Override
			public void handleStep(StepInterpolator interpolator, boolean isLast) {
				double t = interpolator.getCurrentTime();
				double[] y = interpolator.getInterpolatedState();
				GuiUtils.informConsole("time="+t + " y=" + y[0] + " y'=" + y[1]);
			}
		};
		integrator.addStepHandler(stepHandler);

	}

	@Override
	public void solve(IScope scope, SingleEquationStatement eq) {
		// call the integrator.
		// We need to save the state (previous time the integrator has been
		// solved, etc.)

		// GuiUtils.informConsole("it work "+eq);
		if (eq instanceof SingleEquationStatement) {

			// ((SingleEquationStatement) eq).executeOn(scope);
			eq.c = new double[] { 1.0, 1.0 };
			eq.omega = 0.1;
			double[] y = new double[] { 0.0, 1.0 }; // initial state
			try {
				integrator.integrate(eq, 0, y, SimulationClock.getCycle(), y);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

	}

}