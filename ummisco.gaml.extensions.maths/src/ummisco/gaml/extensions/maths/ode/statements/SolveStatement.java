/*********************************************************************************************
 * 
 * 
 * 'SolveStatement.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.statements;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
import msi.gaml.expressions.VariableExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import ummisco.gaml.extensions.maths.ode.statements.SolveStatement.SolveValidator;
import ummisco.gaml.extensions.maths.ode.utils.solver.DormandPrince853Solver;
import ummisco.gaml.extensions.maths.ode.utils.solver.Rk4Solver;
import ummisco.gaml.extensions.maths.ode.utils.solver.Solver;

@facets(value = {
	@facet(name = IKeyword.EQUATION,
		type = IType.ID,
		optional = false,
		doc = @doc("the equation system identifier to be numerically solved")),
	@facet(name = IKeyword.METHOD,
		type = IType.ID /* CHANGE */,
		optional = true,
		values = { "rk4", "dp853" },
		doc = @doc(value = "integrate method (can be only \"rk4\" or \"dp853\") (default value: \"rk4\")")),
	@facet(name = "integrated_times",
		type = IType.LIST,
		optional = true,
		doc = @doc(value = "time interval inside integration process")),
	@facet(name = "integrated_values",
		type = IType.LIST,
		optional = true,
		doc = @doc(value = "list of variables's value inside integration process")),
	@facet(name = "discretizing_step",
		type = IType.INT,
		optional = true,
		doc = @doc(value = "number of discret beside 2 step of simulation (default value: 0)")),
	@facet(name = "time_initial", type = IType.FLOAT, optional = true, doc = @doc(value = "initial time")),
	@facet(name = "time_final",
		type = IType.FLOAT,
		optional = true,
		doc = @doc(value = "target time for the integration (can be set to a value smaller than t0 for backward integration)")),
	@facet(name = "cycle_length",
		type = IType.INT,
		optional = true,
		doc = @doc(value = "length of simulation cycle which will be synchronize with step of integrator (default value: 1)")),
	@facet(name = IKeyword.STEP,
		type = IType.FLOAT,
		optional = true,
		doc = @doc(value = "integration step, use with most integrator methods (default value: 1)")),
	@facet(name = "min_step",
		type = IType.FLOAT,
		optional = true,
		doc = @doc(value = "minimal step, (used with dp853 method only), (sign is irrelevant, regardless of integration direction, forward or backward), the last step can be smaller than this value")),
	@facet(name = "max_step",
		type = IType.FLOAT,
		optional = true,
		doc = @doc(value = "maximal step, (used with dp853 method only), (sign is irrelevant, regardless of integration direction, forward or backward), the last step can be smaller than this value")),
	@facet(name = "scalAbsoluteTolerance",
		type = IType.FLOAT,
		optional = true,
		doc = @doc(value = "allowed absolute error (used with dp853 method only)")),
	@facet(name = "scalRelativeTolerance",
		type = IType.FLOAT,
		optional = true,
		doc = @doc(value = "allowed relative error (used with dp853 method only)")) },
	omissible = IKeyword.EQUATION)
@symbol(name = { IKeyword.SOLVE }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@validator(SolveValidator.class)
@doc(value = "Solves all equations which matched the given name, with all systems of agents that should solved simultaneously.",
	usages = { @usage(value = "", examples = { @example(value = "solve SIR method: \"rk4\" step:0.001;",
		isExecutable = false) }) })
public class SolveStatement extends AbstractStatement {

	public static class SolveValidator implements IDescriptionValidator {

		@Override
		public void validate(final IDescription desc) {
			IExpression method = desc.getFacets().getExpr(IKeyword.METHOD);
			if (method != null) {
				String methodName = method.literalValue();
				if (methodName != null) {
					if ("dp853".equals(methodName)) {
						if (!desc.getFacets().containsKey("min_step") || !desc.getFacets().containsKey("min_step") || !desc.getFacets().containsKey("max_step")
								|| !desc.getFacets().containsKey("scalAbsoluteTolerance")|| !desc.getFacets().containsKey("scalRelativeTolerance"))
						desc.error("For method dp853, the facets min_step, max_step, scalAbsoluteTolerance and scalRelativeTolerance have to be defined", IGamlIssue.GENERAL);
					}else if (!"rk4".equals(methodName)) {
						desc.error("The method facet must have for value either \"rk4\" or \"dp853\"", IGamlIssue.GENERAL);
					}
				} else {
					desc.error("The method facet must have for value either \"rk4\" or \"dp853\"", IGamlIssue.GENERAL);
				}		
			}
		}
	}

	Solver solver;
	final String equationName, solverName;
	SystemOfEquationsStatement theEquations;
	double timeInit = 0, timeFinal = 1;
	int discret = 0;
	double cycle_length = 1;
	final IExpression stepExp, cycleExp, discretExp, minStepExp, maxStepExp, absTolerExp, relTolerExp, timeInitExp,
		timeFinalExp;


	public SolveStatement(final IDescription desc) {
		super(desc);
		equationName = getFacet(IKeyword.EQUATION).literalValue();
		IExpression sn = getFacet(IKeyword.METHOD);
		solverName = sn == null ? "rk4" : sn.literalValue();
		sn = getFacet(IKeyword.STEP);
		stepExp = sn == null ? new ConstantExpression(0.2d) : sn;
		sn = getFacet("cycle_length");
		cycleExp = sn == null ? new ConstantExpression(1d) : sn;
		sn = getFacet("discretizing_step");
		discretExp = sn == null ? new ConstantExpression(0) : sn;
		minStepExp = getFacet("min_step");
		maxStepExp = getFacet("max_step");
		absTolerExp = getFacet("scalAbsoluteTolerance");
		relTolerExp = getFacet("scalRelativeTolerance");
		timeInitExp = getFacet("time_initial");
		timeFinalExp = getFacet("time_final");
	}

	private SystemOfEquationsStatement getEquations(final IScope scope) {
		if ( theEquations == null ) {
			theEquations = scope.getAgentScope().getSpecies().getStatement(SystemOfEquationsStatement.class, equationName);
		}
		return theEquations;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {

		discret = Cast.asInt(scope, discretExp.value(scope));
		cycle_length = Cast.asFloat(scope, cycleExp.value(scope));
		double step = Cast.asFloat(scope, stepExp.value(scope));
		// step = 1.0 / cycle_length != step?step:1.0 / cycle_length;
		// step = step*cycle_length>1?1/(step*cycle_length):step*cycle_length;
		// step = cycle_length > 1?(step/cycle_length):1;
		step = cycle_length > 1.0 ? step / cycle_length : step;
		if ( getEquations(scope) == null ) { return null; }
		theEquations.currentScope = scope;

		if ( solverName.equals("rk4") ) {
			solver = new Rk4Solver(step, discret>0?discret:0, theEquations.integrated_times, theEquations.integrated_values);
		} else if ( solverName.equals("dp853")) {
			if (minStepExp != null && maxStepExp != null && absTolerExp != null &&
				relTolerExp != null ) {
				double minStep = Cast.asFloat(scope, minStepExp.value(scope));
				double maxStep = Cast.asFloat(scope, maxStepExp.value(scope));
				minStep = cycle_length > 1.0 ? minStep / cycle_length : minStep;
				maxStep = cycle_length > 1.0 ? maxStep / cycle_length : maxStep;
	
				double scalAbsoluteTolerance = Cast.asFloat(scope, absTolerExp.value(scope));
				double scalRelativeTolerance = Cast.asFloat(scope, relTolerExp.value(scope));
	
				solver =
					new DormandPrince853Solver(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance, discret>0?discret:0,
							theEquations.integrated_times, theEquations.integrated_values);
			} else {
				throw GamaRuntimeException.error("For method dp853, the facets min_step, max_step, scalAbsoluteTolerance and scalRelativeTolerance have to be defined", scope);
			}
		}
		timeInit = timeInitExp == null ? scope.getClock().getCycle() : Cast.asFloat(scope, timeInitExp.value(scope));
		timeFinal =
			timeFinalExp == null ? scope.getClock().getCycle() + 1 : Cast.asFloat(scope, timeFinalExp.value(scope));

		timeInit = cycle_length > 1.0 ? timeInit / cycle_length : timeInit;
		timeFinal = cycle_length > 1.0 ? timeFinal / cycle_length : timeFinal;

		theEquations.addExtern(equationName);
		solver.solve(scope, theEquations, timeInit, timeFinal, cycle_length);
		theEquations.removeExtern(scope, equationName);

		if ( getFacet("integrated_times") != null ) {
			((VariableExpression) getFacet("integrated_times")).setVal(scope, theEquations.integrated_times, false);
		}

		if ( getFacet("integrated_values") != null ) {
			IExpression fv = getFacet("integrated_values").resolveAgainst(scope);
			IExpression[] exp = ((ListExpression) fv).getElements();
			for ( int i = 0; i < exp.length; i++ ) {
				((VariableExpression) exp[i]).setVal(scope, theEquations.integrated_values.get(i), false);
			}
		}

		return null;
	}

}
