package ummisco.gaml.extensions.maths.utils;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import ummisco.gaml.extensions.maths.statements.SingleEquationStatement;
import ummisco.gaml.extensions.maths.statements.SystemOfEquationsStatement;
import ummisco.gaml.extensions.maths.utils.Solver;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.expressions.IVarExpression;

public class Rk4Solver extends Solver {
	FirstOrderIntegrator integrator;
	double step;
	double cycle_length;
	double time_initial;
	double time_final;
	public StepHandler stepHandler;
	public GamaList integrated_time;
	public GamaList integrated_val;
	
	public Rk4Solver(double S, GamaList integratedTime, GamaList integratedVal){
		step = S;
		integrated_time=integratedTime;
		integrated_val=integratedVal;
		integrator = new ClassicalRungeKuttaIntegrator(step);
		stepHandler = new StepHandler() {
			public void init(double t0, double[] y0, double t) {
			}

			@Override
			public void handleStep(StepInterpolator interpolator, boolean isLast) {
				double time = interpolator.getCurrentTime();
				double[] y = interpolator.getInterpolatedState();
				integrated_time.add(time);

				for(int i=0; i<integrated_val.size(); i++){
					((GamaList)integrated_val.get(i)).add(y[i]);
				}
//				 GuiUtils.informConsole("time="+time);
			}
		};
		integrator.addStepHandler(stepHandler);
	}
	public Rk4Solver(double S) {
		// initialize the integrator, the step handler, etc.
		// This class has access to the facets of the statement
		// ie. getFacet(...)
		//
		// Just a trial
		step = S;
		integrator = new ClassicalRungeKuttaIntegrator(step);

	}


	@Override
	public void solve(IScope scope, SystemOfEquationsStatement eq,
			double time_initial, double time_final, int cycle_length)
			throws GamaRuntimeException {
		// call the integrator.
		// We need to save the state (previous time the integrator has been
		// solved, etc.)
		// GuiUtils.informConsole("it work ");
		if (eq instanceof SystemOfEquationsStatement) {
			// add all equations externe to have one complete systemofequation
			//
			
			
			
			/*
			 *  prepare initial value of variables
			 *  1. loop through variables expression
			 *  2. if its equaAgents != null, it mean variable of external equation, set current scope to this agent scope
			 *  3. get value
			 *  4. return to previous scope 
			 */
			
			integrated_val.clear();
			
			double[] y = new double[eq.variables.size()];
			// System.out.println(eq.variables + " " + eq.currentScope);
			for (int i = 0, n = eq.variables.size(); i < n; i++) {
				IVarExpression v = eq.variables.get(i);
				if (eq.equaAgents.size() > 0)
					scope.push(eq.equaAgents.get(i));
				try {
					y[i]=Double.parseDouble(""+v.value(scope));
					
					GamaList obj=new GamaList();
					integrated_val.add(obj);
				} catch (Exception ex1) {
				} finally {
					if (eq.equaAgents.size() > 0){
						scope.pop(eq.equaAgents.get(i));						
						}
				}
				
			}
			
			
			
			
			// GuiUtils.informConsole(""+y);
			// double[] y = new double[] { 0.0, 1.0 };
			try {
////				GuiUtils.informConsole("t="+time_initial+" : "+y[0]+"\n");
//				eq.integrate_time=new GamaList();
				integrator.integrate(eq, time_initial * cycle_length, y, time_final * cycle_length, y);
				eq.assignValue(time_final * cycle_length, y);
//				scope.setAgentVarValue("myt", eq.integrate_time);
				
//				GuiUtils.informConsole("t"+time_final+"= "+y[0]+"\n");
			} catch (Exception ex) {
				System.out.println(ex);
			}
			
			// remove external equations 
		}

	}
}