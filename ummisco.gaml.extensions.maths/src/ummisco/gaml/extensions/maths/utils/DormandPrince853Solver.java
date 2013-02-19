package ummisco.gaml.extensions.maths.utils;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import ummisco.gaml.extensions.maths.statements.SystemOfEquationsStatement;
import ummisco.gaml.extensions.maths.utils.Solver;
import msi.gama.runtime.IScope;

public class DormandPrince853Solver extends Solver {
	FirstOrderIntegrator integrator;
	double step;
	double cycle_length;
	double time_initial;
	double time_final;

	public DormandPrince853Solver(double S) {
		// initialize the integrator, the step handler, etc.
		// This class has access to the facets of the statement
		// ie. getFacet(...)
		//

		// Just a trial
		step = S;
		integrator = new DormandPrince853Integrator(1, 1, 1, 1);
		StepHandler stepHandler = new StepHandler() {
			public void init(double t0, double[] y0, double t) {
			}

			@Override
			public void handleStep(StepInterpolator interpolator, boolean isLast) {
				double t = interpolator.getCurrentTime();
				double[] y = interpolator.getInterpolatedState();
				// GuiUtils.informConsole("time="+t + " S=" + y[0] + " I=" +
				// y[1]);
			}
		};
		integrator.addStepHandler(stepHandler);

	}

	@Override
	public void solve(IScope scope, SystemOfEquationsStatement eq, double time_initial, double time_final, double cycle_length) {
		// call the integrator.
		// We need to save the state (previous time the integrator has been
		// solved, etc.)

		// GuiUtils.informConsole("it work ");
		if (eq instanceof SystemOfEquationsStatement) {

			// ((SystemOfEquationsStatement) eq).executeOn(scope);
			// eq.S = new double[] { 1.0, 1.0 };
			// eq.I = new double[] { 1.0, 1.0 };
			// eq.R = new double[] { 1.0, 1.0 };

			// double[] y = new double[] { (Double) scope.getAgentVarValue("x"),
			// (Double) scope.getAgentVarValue("y")}; // initial state
			double[] y = new double[eq.variables.size()];
			for (int i = 0, n = eq.variables.size(); i < n; i++) {
				y[i] = (Double) eq.variables.get(i).value(scope);
			}
			// GuiUtils.informConsole(""+y[0]+" "+y[1]);
			// double[] y = new double[] { 0.0, 1.0 };
			try {
				// integrator.integrate(eq, SimulationClock.getCycle()-1, y,
				// SimulationClock.getCycle(), y);
				
				integrator.integrate(eq, time_initial * cycle_length, y,
						time_final * cycle_length, y);
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}

	}

}