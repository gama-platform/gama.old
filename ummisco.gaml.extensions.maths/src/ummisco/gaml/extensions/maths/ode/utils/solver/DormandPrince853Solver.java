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

import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gaml.expressions.IExpression;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import ummisco.gaml.extensions.maths.ode.statements.SystemOfEquationsStatement;

public class DormandPrince853Solver extends Solver {

	FirstOrderIntegrator integrator;
	double step;
	double cycle_length;
	double time_initial;
	double time_final;
	double minStep;
	double maxStep;
	double scalAbsoluteTolerance;
	double scalRelativeTolerance;
	public StepHandler stepHandler;
	public GamaList integrated_time;
	public GamaList integrated_val;

	public DormandPrince853Solver(final double minStep, final double maxStep, final double scalAbsoluteTolerance,
		final double scalRelativeTolerance, final GamaList iT, final GamaList iV) {

		integrated_time = iT;
		integrated_val = iV;
		this.minStep = minStep;
		this.maxStep = maxStep;
		this.step = (minStep + maxStep) / 2;
		this.scalAbsoluteTolerance = scalAbsoluteTolerance;
		this.scalRelativeTolerance = scalRelativeTolerance;
		integrator = new DormandPrince853Integrator(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
		stepHandler = new StepHandler() {

			@Override
			public void init(final double t0, final double[] y0, final double t) {}

			@Override
			public void handleStep(final StepInterpolator interpolator, final boolean isLast) {
				final double time = interpolator.getCurrentTime();
				final double[] y = interpolator.getInterpolatedState();
				integrated_time.add(time);

				for ( int i = 0; i < integrated_val.size(); i++ ) {
					((GamaList) integrated_val.get(i)).add(y[i]);
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
		this.minStep = minStep;
		this.maxStep = maxStep;
		this.step = (minStep + maxStep) / 2;
		this.scalAbsoluteTolerance = scalAbsoluteTolerance;
		this.scalRelativeTolerance = scalRelativeTolerance;
		integrator = new DormandPrince853Integrator(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
	}

	@Override
	public void solve(final IScope scope, final SystemOfEquationsStatement eq, final double time_initial,
		final double time_final, final double cycle_length) {

		if ( eq instanceof SystemOfEquationsStatement ) {
			// add all equations externe to have one complete systemofequation
			//

			/*
			 * prepare initial value of variables 1. loop through variables
			 * expression 2. if its equaAgents != null, it mean variable of
			 * external equation, set current scope to this agent scope 3. get
			 * value 4. return to previous scope
			 */

			integrated_val.clear();

			final double[] y = new double[eq.variables_diff.size()];

			for (int i = 0, n = eq.variables_diff.size(); i < n; i++) {
				final IExpression v = eq.variables_diff.get(i);
				boolean pushed = false;
				if (eq.equaAgents.size() > 0) {
					pushed = scope.push(eq.equaAgents.get(i));
				}
				try {
					y[i] = Double.parseDouble("" + v.value(scope));

					final GamaList obj = new GamaList();
					integrated_val.add(obj);
				} catch (final Exception ex1) {
					GuiUtils.debug(ex1.getMessage());
				} finally {
					if (eq.equaAgents.size() > 0) {
						if (pushed) {
							scope.pop(eq.equaAgents.get(i));
						}
					}
				}

			}
			if (y.length > 0)
			try {

				integrator.integrate(eq, (time_initial)
						* (step / cycle_length / step), y, time_final
						* (step / cycle_length / step), y);
			} catch (final Exception ex) {
					System.out.println(ex);
			}
			eq.assignValue(time_final * (step / cycle_length / step), y);

		}

	}
}