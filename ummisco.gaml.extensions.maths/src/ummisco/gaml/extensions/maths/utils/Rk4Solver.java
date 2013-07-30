package ummisco.gaml.extensions.maths.utils;

import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.expressions.IVarExpression;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.*;
import ummisco.gaml.extensions.maths.statements.SystemOfEquationsStatement;

public class Rk4Solver extends Solver {

	FirstOrderIntegrator integrator;
	double step;
	double cycle_length;
	double time_initial;
	double time_final;
	public StepHandler stepHandler;
	public GamaList integrated_time;
	public GamaList integrated_val;

	public Rk4Solver(final double S, final GamaList iT, final GamaList iV) {
		step = S;
		integrated_time = iT;
		integrated_val = iV;

		integrator = new ClassicalRungeKuttaIntegrator(step);
		stepHandler = new StepHandler() {

			@Override
			public void init(final double t0, final double[] y0, final double t) {
			}

			@Override
			public void handleStep(final StepInterpolator interpolator,
					final boolean isLast) {
				final double time = interpolator.getCurrentTime();
				final double[] y = interpolator.getInterpolatedState();

				integrated_time.add(time);

				for (int i = 0; i < integrated_val.size(); i++) {
					((GamaList) integrated_val.get(i)).add(y[i]);
				}

				// GuiUtils.informConsole("time="+time);
			}
		};
		integrator.addStepHandler(stepHandler);
	}

	public Rk4Solver(final double S) {
		// initialize the integrator, the step handler, etc.
		// This class has access to the facets of the statement
		// ie. getFacet(...)
		//
		// Just a trial
		step = S;
		integrator = new ClassicalRungeKuttaIntegrator(step);

	}

	@Override
	public void solve(final IScope scope, final SystemOfEquationsStatement eq,
			final double time_initial, final double time_final,
			final double cycle_length) throws GamaRuntimeException {
		// call the integrator.
		// We need to save the state (previous time the integrator has been
		// solved, etc.)
		// GuiUtils.informConsole("it work ");
		if (eq instanceof SystemOfEquationsStatement) {
			// add all equations externe to have one complete systemofequation
			//

			/*
			 * prepare initial value of variables 1. loop through variables
			 * expression 2. if its equaAgents != null, it mean variable of
			 * external equation, set current scope to this agent scope 3. get
			 * value 4. return to previous scope
			 */

			integrated_val.clear();

			final double[] y = new double[eq.variables.size()];
			// System.out.println(eq.variables + " " + eq.currentScope);
			for (int i = 0, n = eq.variables.size(); i < n; i++) {
				final IVarExpression v = eq.variables.get(i);
				boolean pushed = false;
				if (eq.equaAgents.size() > 0) {
					pushed = scope.push(eq.equaAgents.get(i));
				}
				try {
					y[i] = Double.parseDouble("" + v.value(scope));

					final GamaList obj = new GamaList();
					integrated_val.add(obj);
				} catch (final Exception ex1) {
				} finally {
					if (eq.equaAgents.size() > 0) {
						if (pushed) {
							scope.pop(eq.equaAgents.get(i));
						}
					}
				}

			}

			// GuiUtils.informConsole(""+y);
			// double[] y = new double[] { 0.0, 1.0 };
			try {
				// // GuiUtils.informConsole("t="+time_initial+" : "+y[0]+"\n");
				// eq.integrate_time=new GamaList();

				integrator.integrate(eq, (time_initial)
						* (step / cycle_length / step), y, time_final
						* (step / cycle_length / step), y);
				eq.assignValue(time_final * (step / cycle_length / step), y);

				// integrator.integrate(eq, time_initial * cycle_length, y,
				// time_final * cycle_length, y);
				// eq.assignValue(time_final * cycle_length, y);

				// scope.setAgentVarValue("myt", eq.integrate_time);

//				GuiUtils.informConsole("step= " + step + " cycle_length="
//						+ cycle_length + " t0=" + time_initial
//						* (step / cycle_length / step) + " tf=" + time_final
//						* (step / cycle_length / step) + " y0=" + y[0]);
			} catch (final Exception ex) {
				System.out.println(ex);
			}

			// remove external equations
		}

	}
}