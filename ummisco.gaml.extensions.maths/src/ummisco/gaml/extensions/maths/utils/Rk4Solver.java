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
import msi.gaml.expressions.IVarExpression;

public class Rk4Solver extends Solver {

	FirstOrderIntegrator integrator;

	public Rk4Solver(double step) {
		// initialize the integrator, the step handler, etc.
		// This class has access to the facets of the statement
		// ie. getFacet(...)
		//

		// Just a trial
		integrator = new ClassicalRungeKuttaIntegrator(step);
		StepHandler stepHandler = new StepHandler() {
			public void init(double t0, double[] y0, double t) {
			}

			@Override
			public void handleStep(StepInterpolator interpolator, boolean isLast) {
				double t = interpolator.getCurrentTime();
				double[] y = interpolator.getInterpolatedState();
//				GuiUtils.informConsole("time="+t + " S=" + y[0] + " I=" + y[1]);
			}
		};
		integrator.addStepHandler(stepHandler);

	}

	@Override
	public void solve(IScope scope, SystemOfEquationsStatement eq) {
		// call the integrator.
		// We need to save the state (previous time the integrator has been
		// solved, etc.)

//		 GuiUtils.informConsole("it work ");
		if (eq instanceof SystemOfEquationsStatement) {
			
//			 ((SystemOfEquationsStatement) eq).executeOn(scope);
//			eq.S = new double[] { 1.0, 1.0 };
//			eq.I = new double[] { 1.0, 1.0 };
//			eq.R = new double[] { 1.0, 1.0 };

			eq.currentScope=scope;
//			double[] y = new double[] { (Double) scope.getAgentVarValue("x"), (Double) scope.getAgentVarValue("y")}; // initial state
			double[] y = new double[eq.variables.size()];
			for(int i=0,n=eq.variables.size(); i<n; i++){
				y[i]=(Double) eq.variables.get(i).value(scope);
			}
//			GuiUtils.informConsole(""+y[0]+" "+y[1]);
//			double[] y = new double[] { 0.0, 1.0 };
			try {
				integrator.integrate(eq, SimulationClock.getTime()-0.1, y,  SimulationClock.getTime(), y);
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}

	}

}