package ummisco.gaml.extensions.maths.utils;

import java.util.List;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import ummisco.gaml.extensions.maths.statements.SingleEquationStatement;
import ummisco.gaml.extensions.maths.statements.SystemOfEquationsStatement;
import ummisco.gaml.extensions.maths.utils.Solver;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;

public class Rk4Solver extends Solver {
	FirstOrderIntegrator integrator;
	IExpression cycleLength;
	IExpression time0;
	IExpression timef;
	double step;
	double cycle_length;
	double time_initial;
	double time_final;

	public Rk4Solver(double S, IExpression cl, IExpression t0, IExpression tf) {
		// initialize the integrator, the step handler, etc.
		// This class has access to the facets of the statement
		// ie. getFacet(...)
		//

		// Just a trial
		step = S;
		cycleLength = cl;
		time0 = t0;
		timef = tf;
		integrator = new ClassicalRungeKuttaIntegrator(step);
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
	public void solve(IScope scope, SystemOfEquationsStatement eq) {
		// call the integrator.
		// We need to save the state (previous time the integrator has been
		// solved, etc.)

		// GuiUtils.informConsole("it work ");
		if (eq instanceof SystemOfEquationsStatement) {

			// ((SystemOfEquationsStatement) eq).executeOn(scope);
			// eq.S = new double[] { 1.0, 1.0 };
			// eq.I = new double[] { 1.0, 1.0 };
			// eq.R = new double[] { 1.0, 1.0 };

			eq.currentScope = scope;
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
				cycle_length = 1;
				if (cycleLength != null) {
					cycle_length = Double.parseDouble(""
							+ cycleLength.value(scope));
				}
				time_initial = SimulationClock.getCycle() - 1;
				if (time0 != null) {
					time_initial = Double.parseDouble("" + time0.value(scope));
				}
				time_final = SimulationClock.getCycle();
				if (timef != null) {
					time_final = Double.parseDouble("" + timef.value(scope));
				}
				integrator.integrate(eq, time_initial * cycle_length, y,
						time_final * cycle_length, y);
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}

	}

}