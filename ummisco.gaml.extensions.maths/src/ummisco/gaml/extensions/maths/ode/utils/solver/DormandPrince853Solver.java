/*********************************************************************************************
 *
 *
 * 'DormandPrince853Solver.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import java.util.*;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.Types;
import ummisco.gaml.extensions.maths.ode.statements.SystemOfEquationsStatement;

public class DormandPrince853Solver extends Solver {

	FirstOrderIntegrator integrator;
	double step;
	// double cycle_length;
	// double time_initial;
	// double time_final;
	// double minStep;
	// double maxStep;
	// double scalAbsoluteTolerance;
	// double scalRelativeTolerance;
	public StepHandler stepHandler;
	public List<Double> integrated_time;
	public List<List<Double>> integrated_val;
	private int count = 0;

	public DormandPrince853Solver(final double minStep, final double maxStep, final double scalAbsoluteTolerance,
		final double scalRelativeTolerance, final int discretizing_step, final List<Double> iT,
		final List<List<Double>> integrate_val) {

		integrated_time = iT;
		integrated_val = integrate_val;
		// this.minStep = minStep;
		// this.maxStep = maxStep;
		this.step = (minStep + maxStep) / 2;
		// this.scalAbsoluteTolerance = scalAbsoluteTolerance;
		// this.scalRelativeTolerance = scalRelativeTolerance;
		integrator = new DormandPrince853Integrator(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
		stepHandler = new StepHandler() {

			@Override
			public void init(final double t0, final double[] y0, final double t) {}

			@Override
			public void handleStep(final StepInterpolator interpolator, final boolean isLast) {
				final double time = interpolator.getCurrentTime();
				final double[] y = interpolator.getInterpolatedState();
				count++;
				// System.out.println(count+" "+1/step/discretizing_step+" "+count / (1/step/discretizing_step) % 1);
				if ( count / (1 / step / discretizing_step) % 1 <= 0.0001 && count < (int) (1 / step) ) {
					integrated_time.add(time);
					for ( int i = 0; i < y.length; i++ ) {
						integrated_val.get(i).add(y[i]);
					}
				}
			}
		};
		integrator.addStepHandler(stepHandler);
	}

	public DormandPrince853Solver(final double minStep, final double maxStep, final double scalAbsoluteTolerance,
		final double scalRelativeTolerance) {
		// initialize the integrator, the step handler, etc.
		// This class has access to the facets of the statement
		// ie. getFacet(...)
		//

		// Just a trial
		// this.minStep = minStep;
		// this.maxStep = maxStep;
		this.step = (minStep + maxStep) / 2;
		// this.scalAbsoluteTolerance = scalAbsoluteTolerance;
		// this.scalRelativeTolerance = scalRelativeTolerance;
		integrator = new DormandPrince853Integrator(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
	}

	@Override
	public void solve(final IScope scope, final SystemOfEquationsStatement eq, final double time_initial,
		final double time_final, final double cycle_length) {

		// if ( eq instanceof SystemOfEquationsStatement ) {
		// add all equations externe to have one complete systemofequation
		//

		/*
		 * prepare initial value of variables 1. loop through variables
		 * expression 2. if its equaAgents != null, it mean variable of
		 * external equation, set current scope to this agent scope 3. get
		 * value 4. return to previous scope
		 */

		// integrated_val.clear();

		final double[] y = new double[eq.variables_diff.size()];
		List<IExpression> equationValues = new ArrayList(eq.variables_diff.values());
		for ( int i = 0, n = equationValues.size(); i < n; i++ ) {
			final IExpression v = equationValues.get(i);
			boolean pushed = false;
			if ( eq.equaAgents.size() > 0 ) {
				pushed = scope.push(eq.equaAgents.get(i));
			}
			try {
				y[i] = Double.parseDouble("" + v.value(scope));

				final IList obj = GamaListFactory.create(Types.NO_TYPE);
				integrated_val.add(obj);
			} catch (final Exception ex1) {
				GuiUtils.debug(ex1.getMessage());
			} finally {
				if ( eq.equaAgents.size() > 0 ) {
					if ( pushed ) {
						scope.pop(eq.equaAgents.get(i));
					}
				}
			}

		}
		if ( scope.getClock().getCycle() == 0 ) {
			integrated_time.add(time_initial);
			for ( int i = 0; i < y.length; i++ ) {
				integrated_val.get(i).add(y[i]);
			}
		}
		if ( y.length > 0 ) {
			try {

				integrator.integrate(eq, time_initial * 1, y, time_final * 1, y);
			} catch (final Exception ex) {
				System.out.println(ex);
			}
		}
		eq.assignValue(time_final * step, y);
		integrated_time.add(time_final);
		for ( int i = 0; i < y.length; i++ ) {
			integrated_val.get(i).add(y[i]);
		}
		// System.out.println(integrated_time);
		// }

	}
}