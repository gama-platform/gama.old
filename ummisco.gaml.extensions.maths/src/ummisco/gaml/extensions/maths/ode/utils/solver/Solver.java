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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import ummisco.gaml.extensions.maths.ode.statements.SystemOfEquationsStatement;

public abstract class Solver {

	final FirstOrderIntegrator integrator;
	int count;
	final double step;

	Solver(final double step, final FirstOrderIntegrator integrator,
			final GamaMap<String, IList<Double>> integratedValues) {
		this.step = step;
		this.integrator = integrator;
		if (integratedValues != null)
			integrator.addStepHandler(new StepHandler() {

				@Override
				public void init(final double t0, final double[] y0, final double t) {
				}

				@Override
				public void handleStep(final StepInterpolator interpolator, final boolean isLast) {
					final double time = interpolator.getCurrentTime();
					final double[] y = interpolator.getInterpolatedState();
					count++;
					storeValues(time, y, integratedValues);
				}
			});
	}

	// Call the integrator, which should call computeDerivatives on the system
	// of equations;
	public void solve(final IScope scope, final SystemOfEquationsStatement eq, final double initialTime,
			final double finalTime, final GamaMap<String, IList<Double>> integrationValues) {

		eq.executeInScope(scope, new Runnable() {

			@Override
			public void run() {
				final IList<IAgent> equationAgents = eq.getEquationAgents(scope);
				/*
				 * prepare initial value of variables 1. loop through variables
				 * expression 2. if its equaAgents != null, it mean variable of
				 * external equation, set current scope to this agent scope 3.
				 * get value 4. return to previous scope
				 */

				final double[] y = new double[eq.variables_diff.size()];
				final List<IExpression> equationValues = new ArrayList(eq.variables_diff.values());
				for (int i = 0, n = equationValues.size(); i < n; i++) {
					if (integrationValues.size() < n) {
						integrationValues.put(equationValues.get(i).getName(), GamaListFactory.create(Double.class));
					}
					final IAgent a = equationAgents.get(i);
					if (!a.dead()) {
						final boolean pushed = scope.push(a);
						try {
							y[i] = Cast.asFloat(scope, equationValues.get(i).value(scope));
						} catch (final Exception ex1) {
							scope.getGui().debug(ex1.getMessage());
						} finally {
							if (pushed) {
								scope.pop(a);
							}
						}
					}

				}
				integrationValues.put(eq.variable_time.getName(), GamaListFactory.create(Double.class));

				if (scope.getClock().getCycle() == 0) {
					storeValues(initialTime, y, integrationValues);
				}
				if (y.length > 0) {
					try {
						integrator.integrate(eq, initialTime, y, finalTime, y);
					} catch (final Exception ex) {
						System.out.println(ex);
					}
				}
				eq.assignValue(scope, finalTime * step, y);
				storeValues(finalTime, y, integrationValues);
			}
		});

	}

	private void storeValues(final double time, final double[] y,
			final GamaMap<String, IList<Double>> integrationValues) {
		// if (integrationTimes != null)
		// integrationTimes.add(time);
		if (integrationValues != null)
			for (int i = 0; i < y.length; i++) {
				integrationValues.getValues().get(i).add(y[i]);
			}
		integrationValues.getValues().get(y.length).add(time);

	}
}
